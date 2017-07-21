/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2016 Robert Keller and Harvey Mudd College XML export code
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
package imp.trading;

import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.RhythmCluster;
import imp.gui.Notate;
import imp.gui.QuantizationDialog;
import imp.lickgen.LickgenFrame;
import imp.lickgen.transformations.Transform;
import imp.trading.tradingResponseModes.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zach Kondak
 * @author Nicholas Weintraut Redesign
 */
public class TradingResponseController {

    TradingResponseInfo responseInfo;
    TradingResponseMode responseMode;
    Future<MelodyPart>[] responseSections;
    int sectionIndex;
    //the time that the last finish signal was processed
    private long finishTime;
    //the amount of time (in nanoseconds) which we would like to wait for the melody to be complete after the finish signal is sent
    private long desiredFinishDuration;
    boolean generationCancelled;
    private Notate notate;
    
    

    public TradingResponseController(Notate notate, int[] metre, int tradeLength, TradingResponseMode responseMode) throws ExceptionTradeModeNotFound {
        responseInfo = new TradingResponseInfo(notate, metre, tradeLength);
        //response mode is now initialized on creation of controller so that we aren't creating a new one AND doing all of our operations at the response method call
        this.responseMode = responseMode;
        responseMode.setResponseInfo(responseInfo);
        finishTime = System.nanoTime();
        generationCancelled = false;
        //desired finish duration is 1 second = 1000000000 nanoseconds
        desiredFinishDuration = 20000000;
        this.notate = notate;
    }
    
    public void onStartTrading() {
        responseMode.onStartTrading();
    }
    
    public int getNumRemainingParts() {
        return responseSections.length - sectionIndex;
    }
    
    /**
     * This method should be called at the beginning of the human
     * @param responseChords
     * @param nextSection 
     */
    public void startTradingGeneration(ChordPart responseChords, int nextSection)
    {
        //update response info with the chords to generate on as well as the next section's first slot
        responseInfo.updateResponse(null, null, responseChords, nextSection);
        responseMode.endGeneration(); //in case the responseMode was still running a generation thread
        responseSections = responseMode.generateResponseStructure(); //create placeholder future melody part sections and start generating their values
    }
    
    /**
     * This method is used to signal to old, retrofitted response modes (BlockResponseMode subclasses) that they should generate now with the given input
     * @param inputMelody
     * @param inputChords
     * @param responseChords
     * @param nextSection 
     */
    public void finishResponse(MelodyPart inputMelody, ChordPart inputChords, ChordPart responseChords, int nextSection)
    {
        responseInfo.updateResponse(inputMelody, inputChords, responseChords, nextSection);
        responseMode.signalFinish();
        sectionIndex = 0;
        finishTime = System.nanoTime();
    }
    
    public boolean hasNext(){
        //System.out.println("sectionIndex: " + sectionIndex + ", responseSections.length: " + responseSections.length);
        if(responseSections == null){//if Improvisor goes first, responseSections is null so tracking breaks, this seems to fix that
            return sectionIndex < 0;
        }
        return sectionIndex < responseSections.length;
    }
    
    public boolean hasNextReady()
    {
        return responseSections[sectionIndex].isDone();
    }
    
    public MelodyPart retrieveNext()
    {
        if(!generationCancelled) {
            try {
                long timeSinceFinish = System.nanoTime() - finishTime;
                long maxWaitTime = desiredFinishDuration - timeSinceFinish;
                //wait one second for the next melody part to be generated, then output
                return responseSections[sectionIndex++].get(maxWaitTime, TimeUnit.NANOSECONDS);

            } catch (TimeoutException ex) {
                MelodyPart defaultMelody = responseMode.getDefaultMelodyPart();
                //responseMode.endGeneration();
                return defaultMelody;
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            } 
        } else
        {
            sectionIndex++;
            return responseMode.getDefaultMelodyPart();
        }
        return null;
    }

    public void updateResponse(MelodyPart inputMelody, ChordPart inputChords, ChordPart responseChords, int nextSection) {
        responseInfo.updateResponse(inputMelody, inputChords, responseChords, nextSection);
    }
    
    public void setMusician(Transform musician)
    {
        responseInfo.setMusician(musician);
    }
    
    public void updateResponse(MelodyPart inputMelody, ChordPart inputChords, ChordPart responseChords, int nextSection, Transform musician) {
        responseInfo.updateResponse(inputMelody, inputChords, responseChords, nextSection);
        responseInfo.setMusician(musician);
        System.out.println("response is updated");
    }

    public MelodyPart extractFromGrammarSolo(int startSlot, int slotLength) {
        return responseInfo.extractFromGrammarSolo(startSlot, slotLength);
    }
    
    public MelodyPart response(){
            return ((BlockResponseMode) responseMode).generateResponse();
    }

    public TradingResponseInfo getTradingResponseInfo() {
        return responseInfo;
    }

}
