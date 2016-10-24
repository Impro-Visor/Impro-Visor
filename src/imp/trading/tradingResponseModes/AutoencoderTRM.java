/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Rest;
import imp.midi.MidiRecorder;
import imp.midi.StreamingMidiRecorder;
import imp.trading.TradingResponseInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import imp.lstm.architecture.DataStep;
import imp.lstm.architecture.InvalidParametersException;
import imp.lstm.architecture.NetworkConnectomeLoader;
import imp.lstm.architecture.poex.ProductCompressingAutoencoder;
import imp.lstm.io.leadsheet.DataPartIO;
import imp.lstm.io.leadsheet.LeadSheetDataSequence;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AutoencoderTRM defines a response mode for Active Trading which generates the response using an Autoencoder Neural Network while the user is playing.
 * @author Nicholas Weintraut
 */
public class AutoencoderTRM extends TradingResponseMode {
    
    private BlockingQueue<Note> recorderNoteSequence;
    private ProductCompressingAutoencoder autoencoder;
    private int featureSize;
    private int slotResolution;
    private int numFeatures;
    private int numFeaturesPerPart;
    private AtomicBoolean isFinished;
    private Thread autoencoderRun;
    private String autoencoderParamsPath = "connectomes/fixed_feature_noise_corrected_poex.ctome";
    
    
    public AutoencoderTRM(String message){
        super(message);
        featureSize = 24;
        slotResolution = 10;
        numFeatures = 8;
        numFeaturesPerPart = 2;
        autoencoder = new ProductCompressingAutoencoder(24, 48, 84 + 1, false);
        try {
            (new NetworkConnectomeLoader()).load(autoencoderParamsPath, autoencoder);
        } catch (InvalidParametersException ex) {
            for(String name : ex.getInvalidParameters())
                System.out.println(name);
            Logger.getLogger(AutoencoderTRM.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex){
            
            Logger.getLogger(AutoencoderTRM.class.getName()).log(Level.SEVERE, null, ex);
        }
        isFinished = new AtomicBoolean();
        isFinished.set(true);
        
    }
    
    public void onStartTrading(){
    }
    
    public LinkedBlockingQueue<Note> getSampleMelody(){
        LinkedBlockingQueue<Note> melody = new LinkedBlockingQueue<>();
        //creates a 4-bar sample melody queue that consists of the notes from RBM-Provisor training data #150
        melody.add(new Note(77, 120));
        melody.add(new Note(74, 60));
        melody.add(new Note(75, 60));
        melody.add(new Note(77, 60));
        melody.add(new Note(82, 60));
        melody.add(new Note(81, 60));
        melody.add(new Note(79, 60));
        melody.add(new Note(77, 60));
        melody.add(new Note(75, 60));
        melody.add(new Note(72, 60));
        melody.add(new Note(67, 60));
        melody.add(new Note(74, 120));
        melody.add(new Note(77, 60));
        melody.add(new Note(71, 180));
        melody.add(new Note(71, 180));
        melody.add(new Rest(840));
        return melody;
    }
    
    //this will be called before a feature is decoded into music
    public void featureOperations(){
        
    }

    /**
     * Will need to create a thread to read from BlockingQueue here and create a
     * feature vector
     */
    @Override
    public Future<MelodyPart>[] generateResponseStructure() {
       MidiRecorder recorder = this.responseInfo.getNotate().getMidiRecorder();
        assert recorder instanceof StreamingMidiRecorder : "The MidiRecorder initialized with Notate is not a StreamingMidiRecorder! StreamingMidiRecorder is required for AutoencoderTRM";
        this.recorderNoteSequence = ((StreamingMidiRecorder) recorder).getMelodyQueue();
        if(!recorderNoteSequence.isEmpty())
            System.out.println("recordingSequence isnt empty at start of generate!");
        //recorderNoteSequence = getSampleMelody();
        int numParts = numFeatures / numFeaturesPerPart;
        int partSize = featureSize * numFeaturesPerPart;
        
        //responseParts consists of future melody parts which will be completed by generating from the autoencoder on a separate thread
        CompletableFuture<MelodyPart>[] responseParts = new CompletableFuture[numParts];
        for(int i = 0; i < responseParts.length; i++) {
            responseParts[i] = new CompletableFuture<>(); //on get() CompletableFutures will wait until their .complete() is called
        }
        if(autoencoderRun != null)
            autoencoderRun.interrupt();
        //this thread will read the StreamingMidiRecorder's note sequence, run it through the Autoencoder network, and complete the response parts using the generated melody
        autoencoderRun = new Thread() {
            public void run() {
                isFinished.set(false);
                LeadSheetDataSequence encoderInputSequence = DataPartIO.readJustChords(responseInfo.getResponseChords(), 0);
                LeadSheetDataSequence decoderInputSequence = encoderInputSequence.copy();
                Queue<AVector> autoencoderOutputSequence = new LinkedList<>();
                int currPart = 0; //the current melodyPart we are filling
                int featureInPart = 0; //keeps track of the feature we are generating for the current melody part (bounded by numFeaturesPerPart)
                int featureTimeStep = 0; //the current time step of the feature we are generating
                //until we have completed all parts
                int mostRecentPitch = -1;
                while (currPart < responseParts.length) {
                    Note currentNote = null;
                    try {
                            currentNote = recorderNoteSequence.take(); //wait until there is another note
                        //System.out.println(currentNote.getPitchClassName() + " for " + (currentNote.getRhythmValue() / slotResolution) + " timeSteps.");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AutoencoderTRM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (currentNote != null) /* if we have another note to process */ { 
                        int noteTimeSteps = currentNote.getRhythmValue() / slotResolution; //size of note in timeSteps
                        //System.out.println(noteTimeSteps);
                        int currentNoteStep = 0; //timeStep counter
                        do {
                            if(currentNoteStep == 0)
                                encoderInputSequence.pushStep(null, null, Vector.create(new double[]{(double) currentNote.getPitch()}));
                            else
                                encoderInputSequence.pushStep(null, null, Vector.create(new double[]{-2.0}));
                            synchronized(autoencoder) {
                                DataStep step = encoderInputSequence.retrieve();
                                //System.out.println(step.get("melody"));
                                //System.out.println(step.get("chord"));
                                //System.out.println(step.get("beat"));
                                //System.out.println(currentNoteStep);
                                autoencoder.encodeStep(step);
                            }
                            featureTimeStep++;
                            currentNoteStep++;
                            
                            //System.out.println(featureTimeStep);
                            if (featureTimeStep >= featureSize) /* If we have enough data to decode a feature */ {
                                featureTimeStep -= featureSize; //reset out counter
                                synchronized (autoencoder) {
                                    featureOperations(); //perform any operations on the feature vector or autoencoder before we decode
                                    for (int i = 0; i < featureSize; i++) /* Push the decoded steps to the output sequence */ {
                                        autoencoderOutputSequence.offer(autoencoder.decodeStep(decoderInputSequence.retrieve()));
                                        //System.out.println("Decoding timestep " + i + " of feature " + featureInPart + " of Part " + currPart);
                                    }
                                }
                                featureInPart++; //we've encoded/decoded another feature!
                                if (featureInPart >= numFeaturesPerPart) /*if we have enough output to fill a part*/ {
                                    MelodyPart generatedPart = new MelodyPart();
                                    int currNoteLength = slotResolution;
                                    int currPitch = (int) autoencoderOutputSequence.poll().get(0);
                                    
                                    if(currPitch == -2)
                                        currPitch = mostRecentPitch;
                                    for (int i = 1; i < numFeaturesPerPart * featureSize; i++) {
                                        //System.out.println(i);
                                        //System.out.println("Reading timestep " + i + " of Part " + currPart);
                                        int pitch = (int) autoencoderOutputSequence.poll().get(0);
                                        if (pitch == -2 || (pitch == -1 && currPitch == -1)) {
                                            currNoteLength += slotResolution;
                                        } else {
                                            if (currPitch == -1) {
                                                generatedPart.addRest(new Rest(currNoteLength));
                                            } else {
                                                generatedPart.addNote(new Note(currPitch, currNoteLength));
                                            }
                                            currNoteLength = slotResolution;
                                            currPitch = pitch;
                                            mostRecentPitch = pitch;
                                        }
                                    }
                                    if (currPitch == -1) {
                                        generatedPart.addRest(new Rest(currNoteLength));
                                    } else {
                                        generatedPart.addNote(new Note(currPitch, currNoteLength));
                                    }
                                    //System.out.println(generatedPart);
                                    responseParts[currPart].complete(generatedPart);
                                    //System.out.println("completed part " + currPart);
                                    currPart++;
                                    featureInPart = 0;
                                    if(currPart == numParts)
                                        break;
                                }
                            }
                        } while (currentNoteStep < noteTimeSteps);
                    }
                    
                }
                try {
                   
                    (new NetworkConnectomeLoader()).refresh(autoencoderParamsPath, autoencoder, "initialstate");
                } catch (InvalidParametersException ex) {
                    Logger.getLogger(AutoencoderTRM.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AutoencoderTRM.class.getName()).log(Level.SEVERE, null, ex);
                }
                recorderNoteSequence.clear();
                isFinished.set(true);
            }
        };
        autoencoderRun.start();
        return responseParts;
    }
    
    
    

    @Override
    public void signalFinish() {
        MidiRecorder recorder = this.responseInfo.getNotate().getMidiRecorder();
        assert recorder instanceof StreamingMidiRecorder : "The MidiRecorder initialized with Notate is not a StreamingMidiRecorder! StreamingMidiRecorder is required for AutoencoderTRM";
        ((StreamingMidiRecorder) recorder).flushStream();
        if(isFinished.get() == false)  {
            recorderNoteSequence.add(new Rest(numFeatures * featureSize * slotResolution));
        }
    }

    @Override
    public void endGeneration() {
        if(autoencoderRun != null){
            autoencoderRun.interrupt();
        }
    }

    @Override
    public MelodyPart getDefaultMelodyPart() {
        MelodyPart defaultMelody = new MelodyPart();
        int partSize = featureSize * numFeaturesPerPart;
        int partSlotSize = partSize * slotResolution;
        defaultMelody.addRest(new Rest(partSlotSize));
        return defaultMelody;
    }
}
