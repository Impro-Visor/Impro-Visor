/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lstm.main;

import imp.lstm.architecture.InvalidParametersException;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.util.ErrorLog;
import imp.util.PartialBackgroundGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import imp.lstm.architecture.NetworkConnectomeLoader;
import imp.lstm.architecture.poex.ForcePlayPostprocessor;
import imp.lstm.architecture.poex.GenerativeProductModel;
import imp.lstm.architecture.poex.MergeRepeatedPostprocessor;
import imp.lstm.architecture.poex.ProbabilityPostprocessor;
import imp.lstm.architecture.poex.RectifyPostprocessor;
import imp.lstm.encoding.EncodingParameters;
import imp.lstm.io.leadsheet.Constants;
import imp.lstm.io.leadsheet.DataPartIO;
import imp.lstm.io.leadsheet.LeadSheetDataSequence;
import mikera.vectorz.AVector;

/**
 * LSTMGen manages generation of licks using a generative product-of-experts
 * neural network.
 * @author Daniel Johnson
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
    MergeRepeatedPostprocessor repeatMerger;
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
        model = new GenerativeProductModel(lowerBound, upperBound);
        
        rectifier = new RectifyPostprocessor(lowerBound);
        forcePlay = new ForcePlayPostprocessor();
        repeatMerger = new MergeRepeatedPostprocessor(lowerBound);
        
        params_path = null;
        loaded = false;
        
        executor = new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(1));
    }
    
    /**
     * Set the path to the network connectome file
     * @param path 
     */
    public void setLoadPath(String path) {
        params_path = path;
        loaded = false;
    }
    
    /**
     * Load parameters from the current connectome file
     * @param path 
     */
    public void load() throws InvalidParametersException, IOException {
        if(params_path == null){
            throw new RuntimeException("Load called without providing parameters!");
        }
        NetworkConnectomeLoader packer = new NetworkConnectomeLoader();
        packer.load(params_path, model);
        model.reset();
        loaded = true;
    }
    
    /**
     * Helper to set the path to the network connectome file and also load it
     */
    public void loadFromPath(String path) throws InvalidParametersException, IOException {
        setLoadPath(path);
        load();
    }
    
    
    /**
     * Reload state from an already loaded connectome file. Since only the state
     * of the network can change, this is all we need to update.
     */
    public void reload() throws InvalidParametersException, IOException{
        if(!loaded) {
            load();
        } else {
            NetworkConnectomeLoader packer = new NetworkConnectomeLoader();
            packer.refresh(params_path, model, "initialstate");
            model.reset();
        }
        consecutiveRests = 0;
    }
    
    /**
     * Set probability adjustment levels. These affect the behavior of the
     * network.
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
    
    /**
     * Set probability postprocessing modes. These specify how the output of the
     * network is modified before sampling.
     * @param rectify Should the network output be rectified?
     * @param colorTonesOK Are color tones ok?
     * @param mergeRepeated Should repeated pitches be merged into one note?
     * @param resetAfterRests After a long rest, should we reset the network
     * state?
     * @param forcePlayAfterRests After a long rest, should we force the network
     * to play?
     * @param maxNumRests How many timesteps is a "long rest"?
     */
    public void setPostprocess(
            boolean rectify,
            boolean colorTonesOK,
            boolean mergeRepeated,
            boolean resetAfterRests,
            boolean forcePlayAfterRests,
            int maxNumRests)
    {
        assert !(resetAfterRests && forcePlayAfterRests);
        shouldResetAfterTooLong = resetAfterRests;
        allowColorTones = colorTonesOK;
        maxConsecutiveRests = maxNumRests;
        
        ArrayList<ProbabilityPostprocessor> postprocessors = new ArrayList<ProbabilityPostprocessor>(3);
        if(rectify)
            postprocessors.add(rectifier);
        if(mergeRepeated)
            postprocessors.add(repeatMerger);
        if(forcePlayAfterRests)
            postprocessors.add(forcePlay);
        ProbabilityPostprocessor[] ppsArr = new ProbabilityPostprocessor[postprocessors.size()];
        model.setProbabilityPostprocessors(postprocessors.toArray(ppsArr));
    }
    
    /**
     * Helper to run generation process.
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
            repeatMerger.noteWasPlayed((int)curOutput.get(0));
            if(curOutput.get(0) == -1 || (curOutput.get(0) == -2 && consecutiveRests > 0))
                consecutiveRests += Constants.RESOLUTION_SCALAR;
            else
                consecutiveRests = 0;
            timestep += Constants.RESOLUTION_SCALAR;
            outputSequence.pushStep(null, null, curOutput);
        }
    }
    
    /**
     * Start generation of a leadsheet.
     * @param chords The chords to work with
     * @param offset The offset for the beat (i.e. the timestep the chords start
     * on)
     * @param step How long should the generator generate at a time?
     */
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
        repeatMerger.reset();
        rectifier.start(chords, allowColorTones);
        
        done = false;
    }
    
    /**
     * Start generating a leadsheet in trading mode, with gaps for user input.
     * @param chords The chords to work with
     * @param offset The offset for the beat (i.e. the timestep the chords start
     * on)
     * @param generateStart Whether the network should generate first
     * @param tradeQuantum Length of trading "turn" in timesteps
     */
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
    
    /**
     * Synchronously generate a melody over chords
     * @param chords
     * @return 
     */
    public MelodyPart generate(ChordPart chords) {
        return generate(chords, 0);
    }
    /**
     * Synchronously generate a melody over chords
     * @param chords
     * @param offset
     * @return 
     */
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
    
    /**
     * Synchronously generate a trading response
     * @param chords
     * @param generateStart
     * @param tradeQuantum
     * @return 
     */
    public MelodyPart generateTrading(ChordPart chords, boolean generateStart, int tradeQuantum) {
        return generateTrading(chords, 0, generateStart, tradeQuantum);
    }
    /**
     * Synchronously generate a trading response
     * @param chords
     * @param generateStart
     * @param tradeQuantum
     * @return 
     */
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
                    //System.out.println("Extracted " + extracted.getSize());
                    chordSequence = DataPartIO.readChords(extracted, savedOffset + tradePos);
                    outputSequence = chordSequence.copy();
                    outputSequence.clearMelody();
                    
                    timestep = savedOffset + tradePos;
                    forcePlay.reset();
                    repeatMerger.reset();
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
