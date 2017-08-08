/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
