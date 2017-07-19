/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.RhythmCluster;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class SuggestRhythmTRM extends RhythmHelperTRM{
    private int tradeCounter;
    
    
    public SuggestRhythmTRM(String message) {
        super(message);
        tradeCounter = 0;
        System.out.println("creating a suggestRhythmTRM......");
    }

    
    @Override
    public MelodyPart generateResponse(){
        tradeCounter++;
        
        System.out.println("\n\n\nin generateResponse for suggest rhythm");
        
        MelodyPart response = getTradingResponse();
        
        if(tradeCounter % 5==0){
            adjustClusters(tradeCounter);
        }
        return response;
    }
    
    public String toString(){
        return "Rhythm Helper";
    }

    @Override
    protected Polylist getRhythmFromCluster(RhythmCluster closestCluster) {
        return closestCluster.getRandomRhythm();
    }
}
