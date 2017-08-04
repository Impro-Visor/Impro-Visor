/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.RhythmCluster;
import imp.generalCluster.DataPoint;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class SuggestRhythmTRM extends RhythmHelperTRM{
    //private int tradeCounter;
    
    
    public SuggestRhythmTRM(String message) {
        super(message);
        //System.out.println("creating a suggestRhythmTRM......");
    }

    
    @Override
    public MelodyPart generateResponse(){
        tradeCounter++;
        //System.out.println("\n\n\nin generateResponse for suggest rhythm");
        
        MelodyPart response = getTradingResponse();
        
        if(tradeCounter % 5==0){
            adjustClusters(tradeCounter);
        }
        return response;
    }
    
    public String toString(){
        return "Rhythm Helper";
    }

//    @Override
    protected Polylist getRhythmFromCluster(RhythmCluster closestCluster, DataPoint d) {
        return closestCluster.getRandomRhythm();
    }

    @Override
    protected Polylist getRhythmPolylist(RhythmCluster closestCluster, DataPoint d) {
        return getRhythmFromCluster(closestCluster, d);
}

    @Override
    protected MelodyPart createResponseFromRhythmTemplate(MelodyPart rhythmTemplate) {
        MelodyPart improvisorResponse = fitToRhythm(rhythmTemplate);
        return improvisorResponse;

    }

    @Override
    protected MelodyPart getRhythmTemplate(String rhythmString, RhythmCluster bestFit) {
        MelodyPart rhythmTemplate = new MelodyPart(rhythmString);
        if(rhythmTemplate.getEndTime() < tradeLengthInSlots){
           rhythmTemplate = extendRhythmTemplate(rhythmTemplate, bestFit);
        }

        if(rhythmTemplate.getEndTime() > tradeLengthInSlots){
            rhythmTemplate = truncateRhythmTemplate(rhythmTemplate);
        }
        
        return rhythmTemplate;
    }
}
