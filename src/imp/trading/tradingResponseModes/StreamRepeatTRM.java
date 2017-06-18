/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
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

package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Rest;
import imp.midi.MidiRecorder;
import imp.midi.StreamingMidiRecorder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Currently this is not used, as it was not fully developed.
 * StreamRepeatTRM defines a response mode for Active Trading which generates the response using an Autoencoder Neural Network while the user is playing.
 * @author Nicholas Weintraut
 */
public class StreamRepeatTRM extends TradingResponseMode {
    
    private LinkedBlockingQueue<Note> recorderNoteSequence;
    private int featureSize;
    private int slotResolution;
    private int numFeatures;
    private int numFeaturesPerPart;
    private AtomicBoolean isFinished;
    private Thread autoencoderRun;
    
    
    public StreamRepeatTRM(String message){
        super(message);
        featureSize = 24;
        slotResolution = 10;
        numFeatures = 8;
        numFeaturesPerPart = 2;
        isFinished = new AtomicBoolean();
        isFinished.set(true);
        
    }
    
    public void onStartTrading(){
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
        assert recorder instanceof StreamingMidiRecorder : "The MidiRecorder initialized with Notate is not a StreamingMidiRecorder! StreamingMidiRecorder is required for StreamRepeatTRM";
        this.recorderNoteSequence = (LinkedBlockingQueue<Note>) ((StreamingMidiRecorder) recorder).getMelodyQueue();
        if(!recorderNoteSequence.isEmpty())
            System.out.println("recordingSequence isnt empty at start of generate!");
        int numParts = 1;
        
        //responseParts consists of future melody parts which will be completed by generating from the autoencoder on a separate thread
        CompletableFuture<MelodyPart>[] responseParts = new CompletableFuture[numParts];
        for(int i = 0; i < responseParts.length; i++) {
            responseParts[i] = new CompletableFuture<>(); //on get() CompletableFutures will wait until their .complete() is called
        }
        
        //this thread will read the StreamingMidiRecorder's note sequence, run it through the Autoencoder network, and complete the response parts using the generated melody
        Thread autoencoderRun;
        autoencoderRun = new Thread() {
            public void run() {
                isFinished.set(false);
                System.out.println("thread has started running");
                int numGeneratedSlots = 0;
                MelodyPart generatedMelody = new MelodyPart();
                int numSlotsToGen = 1920;
                while(numGeneratedSlots < numSlotsToGen) {
                    Note currentNote = null;
                    try {
                        System.out.println("Attempting take");
                        currentNote = recorderNoteSequence.take();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StreamRepeatTRM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Current note rhythm value: " + currentNote.getRhythmValue());
                    if(currentNote.getRhythmValue() > numSlotsToGen - numGeneratedSlots) {
                        if(currentNote.isRest()) {
                            currentNote = new Rest(numSlotsToGen - numGeneratedSlots);
                        } else {
                            currentNote = new Note(currentNote.getPitch(), numSlotsToGen - numGeneratedSlots);
                        }
                    }
                    System.out.println("Current note rhythm value: " + currentNote.getRhythmValue());
                    numGeneratedSlots += currentNote.getRhythmValue();
                    System.out.println("Current numGeneratedSlots value: " + numGeneratedSlots);
                    generatedMelody.addNote(currentNote);
                }
                
                responseParts[0].complete(generatedMelody);
                System.out.println("thread has finished running");
                recorderNoteSequence.clear();
                System.out.println("cleared!");
                isFinished.set(true);
            }
        };
        autoencoderRun.start();
        return responseParts;
    }

    @Override
    public void signalFinish() {
        MidiRecorder recorder = this.responseInfo.getNotate().getMidiRecorder();
        assert recorder instanceof StreamingMidiRecorder : "The MidiRecorder initialized with Notate is not a StreamingMidiRecorder! StreamingMidiRecorder is required for StreamRepeatTRM";
        ((StreamingMidiRecorder) recorder).flushStream();
        if(isFinished.get() == false)  {
            System.out.println("adding rest to end");
            recorderNoteSequence.add(new Rest(1920));
        }
    }

    @Override
    public void endGeneration() {
        autoencoderRun.interrupt();
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
