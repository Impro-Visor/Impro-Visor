/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;

import lstm.architecture.InvalidParametersException;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.util.ErrorLog;
import imp.util.PartialBackgroundGenerator;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lstm.architecture.NetworkMeatPacker;
import lstm.architecture.poex.ForcePlayPostprocessor;
import lstm.architecture.poex.GenerativeProductModel;
import lstm.architecture.poex.ProbabilityPostprocessor;
import lstm.architecture.poex.RectifyPostprocessor;
import lstm.encoding.EncodingParameters;
import lstm.io.leadsheet.Constants;
import lstm.io.leadsheet.DataPartIO;
import lstm.io.leadsheet.LeadSheetDataSequence;
import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public class LSTMGen implements PartialBackgroundGenerator{
    GenerativeProductModel model;
    String params_path;
    boolean loaded;
    
    boolean isTrading;
    ChordPart savedChords;
    int savedOffset;
    boolean savedGenerateStart;
    int savedTradeQuantum;
    int tradePos;
    int generationStep;
    LeadSheetDataSequence chordSequence;
    LeadSheetDataSequence outputSequence;
    MelodyPart accumMelody;
    boolean done;
    
    ExecutorService executor;
    
    RectifyPostprocessor rectifier;
    ForcePlayPostprocessor forcePlay;
    int consecutiveRests;
    int timestep;
    int maxConsecutiveRests = Constants.WHOLE;
    boolean shouldResetAfterTooLong = false;
    boolean allowColorTones = true;
    int resetQuantum = Constants.WHOLE;
    
    public LSTMGen() {
        int outputSize = EncodingParameters.noteEncoder.getNoteLength();
        int beatSize = 9;
        int featureVectorSize = 100;
        int lowerBound = 48;
        int upperBound = 84+1;
        model = new GenerativeProductModel(outputSize, beatSize, featureVectorSize, lowerBound, upperBound);
        
        rectifier = new RectifyPostprocessor(lowerBound);
        forcePlay = new ForcePlayPostprocessor();
        
        params_path = null;
        loaded = false;
        
        executor = new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(1));
    }
    
    public void setLoadPath(String path) {
        params_path = path;
        loaded = false;
    }
    
    public void load() throws InvalidParametersException, IOException {
        if(params_path == null){
            throw new RuntimeException("Load called without providing parameters!");
        }
        NetworkMeatPacker packer = new NetworkMeatPacker();
        packer.pack(params_path, model);
        model.reset();
        loaded = true;
    }
    
    public void loadFromPath(String path) throws InvalidParametersException, IOException {
        setLoadPath(path);
        load();
    }
    
    public void reload() throws InvalidParametersException, IOException{
        if(!loaded) {
            load();
        } else {
            NetworkMeatPacker packer = new NetworkMeatPacker();
            packer.refresh(params_path, model, "initialstate");
            model.reset();
        }
        consecutiveRests = 0;
    }
    
    /**
     * Set probability adjustment levels
     * @param riskLevel How much risk to take. Range -inf to inf, 0 is default
     * @param biasLevel How to bias the experts. Range -1 (interval only) to 1
     * (chord only), 0 is default
     */
    public void setProbabilityAdjust(double riskLevel, double biasLevel) {
        double epsilon = 1.0e-8;
        double intervalScale = Math.exp(-riskLevel) * (1-biasLevel) + epsilon;
        double chordScale = Math.exp(-riskLevel) * (1+biasLevel) + epsilon;
        double[] modifiers = model.getModifierExponents();
        modifiers[0] = intervalScale;
        modifiers[1] = chordScale;
    }
    
    public void setPostprocess(
            boolean rectify,
            boolean colorTonesOK,
            boolean resetAfterRests,
            boolean forcePlayAfterRests,
            int maxNumRests)
    {
        assert !(resetAfterRests && forcePlayAfterRests);
        shouldResetAfterTooLong = resetAfterRests;
        allowColorTones = colorTonesOK;
        maxConsecutiveRests = maxNumRests;
        if(rectify){
            if(forcePlayAfterRests){
                model.setProbabilityPostprocessors(new ProbabilityPostprocessor[]{
                    rectifier,
                    forcePlay,
                });
            } else {
                model.setProbabilityPostprocessors(new ProbabilityPostprocessor[]{
                    rectifier,
                });
            }
        } else {
            if(forcePlayAfterRests){
                model.setProbabilityPostprocessors(new ProbabilityPostprocessor[]{
                    forcePlay,
                });
            } else {
                model.setProbabilityPostprocessors(new ProbabilityPostprocessor[]{});
            }
        }
    }
    
    /**
     * Run generation
     * @param maxIter Max iters to generate, or negative to generate until end of sequence.
     */
    private void runGenerate(int maxIter){
        for(int i=maxIter; i!=0 && chordSequence.hasNext(); i--){
            if(consecutiveRests >= maxConsecutiveRests){
                if(shouldResetAfterTooLong) {
                    if(timestep % resetQuantum == 0){
                        try {
                            reload();
                        } catch (InvalidParametersException ex) {
                            Logger.getLogger(LSTMGen.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(LSTMGen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    forcePlay.forcePlayNext();
                }
            }
            AVector curOutput = model.step(chordSequence.retrieve());
            if(curOutput.get(0) == -1 || (curOutput.get(0) == -2 && consecutiveRests > 0))
                consecutiveRests += Constants.RESOLUTION_SCALAR;
            else
                consecutiveRests = 0;
            timestep += Constants.RESOLUTION_SCALAR;
            outputSequence.pushStep(null, null, curOutput);
        }
    }
    
    public void startGenerate(ChordPart chords, int offset, int step){
        isTrading = false;
        try {
            reload();
        } catch (InvalidParametersException ex) {
            Logger.getLogger(LSTMGen.class.getName()).log(Level.WARNING, null, ex);
            ErrorLog.log(ErrorLog.WARNING, "Could not load LSTM parameters!", true);
            return;
        } catch (IOException ex) {
            Logger.getLogger(LSTMGen.class.getName()).log(Level.SEVERE, null, ex);
            ErrorLog.log(ErrorLog.WARNING, "Could not load LSTM parameters!", true);
            return;
        }
        savedChords = chords;
        chordSequence = DataPartIO.readChords(chords, offset);
        outputSequence = chordSequence.copy();
        outputSequence.clearMelody();
        if(step < 0){
            generationStep = -1;
        } else {
            generationStep = step/Constants.RESOLUTION_SCALAR;
        }
        
        timestep = offset;
        forcePlay.reset();
        rectifier.start(chords, allowColorTones);
        
        done = false;
    }
    
    public void startGenerateTrading(ChordPart chords, int offset, boolean generateStart, int tradeQuantum) {
        isTrading = true;
        savedChords = chords;
        savedOffset = offset;
        savedGenerateStart = generateStart;
        savedTradeQuantum = tradeQuantum;
        tradePos = 0;
        accumMelody = new MelodyPart();
        
        done = false;
    }
    
    public MelodyPart generate(ChordPart chords) {
        return generate(chords, 0);
    }
    public MelodyPart generate(ChordPart chords, int offset) {
        startGenerate(chords, offset, -1);
        Future<MelodyPart> fpart = lazyGenerateMore();
        while(true){
            try {
                return fpart.get();
            } catch (InterruptedException ex) {
                //try again
            } catch (ExecutionException ex) {
                Logger.getLogger(LSTMGen.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
    
    public MelodyPart generateTrading(ChordPart chords, boolean generateStart, int tradeQuantum) {
        return generateTrading(chords, 0, generateStart, tradeQuantum);
    }
    public MelodyPart generateTrading(ChordPart chords, int offset, boolean generateStart, int tradeQuantum) {
        startGenerateTrading(chords, offset, generateStart, tradeQuantum);
        MelodyPart part = null;
        while(canLazyGenerateMore()){
            Future<MelodyPart> fpart = lazyGenerateMore();
            while(true){
                try {
                    part = fpart.get();
                    break;
                } catch (InterruptedException ex) {
                    //try again
                } catch (ExecutionException ex) {
                    Logger.getLogger(LSTMGen.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        }
        return part;
    }

    @Override
    public boolean canLazyGenerateMore() {
        return !done;
    }

    @Override
    public Future<MelodyPart> lazyGenerateMore() {
        assert canLazyGenerateMore();
        return executor.submit(new Callable<MelodyPart>() {
            @Override
            public MelodyPart call() throws Exception {
                MelodyPart result;
                if (isTrading) {
                    try {
                        reload();
                    } catch (InvalidParametersException ex) {
                        Logger.getLogger(LSTMGen.class.getName()).log(Level.WARNING, null, ex);
                        ErrorLog.log(ErrorLog.WARNING, "Could not load LSTM parameters!", true);
                        return null;
                    }
                    int chordStartPos = tradePos + (savedGenerateStart ? 0 : savedTradeQuantum);
                    if (!savedGenerateStart) {
                        accumMelody.addNote(Note.makeRest(savedTradeQuantum));
                    }
                    ChordPart extracted = savedChords.extract(chordStartPos, chordStartPos + savedTradeQuantum - 1);
                    System.out.println("Extracted " + extracted.getSize());
                    chordSequence = DataPartIO.readChords(extracted, savedOffset + tradePos);
                    outputSequence = chordSequence.copy();
                    outputSequence.clearMelody();
                    
                    timestep = savedOffset + tradePos;
                    forcePlay.reset();
                    rectifier.start(extracted, allowColorTones);
                    
                    runGenerate(-1);
                    DataPartIO.addToMelodyPart(outputSequence, accumMelody);
                    if (savedGenerateStart) {
                        accumMelody.addNote(Note.makeRest(savedTradeQuantum));
                    }
                    tradePos += 2*savedTradeQuantum;
                    done = (tradePos >= savedChords.size());
                    result = accumMelody;
                } else {
                    runGenerate(generationStep);
                    result = DataPartIO.getMelodyPart(outputSequence.copy());
                    if(savedChords.size() == result.size()) {
                        done = true;
                    }
                }
                if(done){
                    savedChords = null;
                    chordSequence = null;
                    outputSequence = null;
                    accumMelody = null;
                }
                return result;
            }
        });
    }
}
