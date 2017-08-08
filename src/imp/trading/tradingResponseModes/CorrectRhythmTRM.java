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
import imp.generalCluster.metrics.NoteCount;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author Cai Glencross & Lukas Gnirke
 */
public class CorrectRhythmTRM extends RhythmHelperTRM{
    ArrayList<Polylist> ruleStringsToEmulate;
    DataPoint[] dataPointsToEmulate;
    
    private int score;
    private static final int FURTHEST_RADIUS = 6;
    
    public static final double A = 5;
    public static final double B = 4;
    public static final double C = 3;
    public static final double D = 2;
    public static final double F = 1;
    
    //private DataPoint goalDataPoint;
    private int numTargetDataPoints;
    protected int numSkipped;
    
    public CorrectRhythmTRM(String message) {
        super(message);
        score = 0;
        //System.out.println("creating a correctRhythmTRM......");
    }

    
    @Override
    public MelodyPart generateResponse(){
        
        //System.out.println("\n\n\nin generateResponse for correct rhythm");
        return getTradingResponse();
//        responseInfo.correctRhythm();
//        return responseInfo.getResponse();
    }
    
    public String toString(){
        return "Rhythm Helper";
    }
    
  
    
    private void gradeUser(DataPoint userDataPoint){
        DataPoint goalDataPoint = getGoalDataPoint();
        double error = userDataPoint.calcEuclideanDistance(goalDataPoint);
        //System.out.println("distance from user to goal: "+error);
        
        double scoreForTrade = error - FURTHEST_RADIUS;
        if(scoreForTrade<0){
            score += Math.abs(scoreForTrade);
        }
        //System.out.println("score for trade: "+ scoreForTrade);
        //System.out.println("score is now: "+score);
        
        //if user has not played any notes, pretend like that trade didn't happen
        
        for(int i = 0; i < userDataPoint.getMetrics().length; i++){
            if (userDataPoint.getMetrics()[i] instanceof NoteCount){
                if(userDataPoint.getMetrics()[i].getValue() <= 0.0){
                    //System.out.println("score before realizing user didn't play anything: "+score);
                    numSkipped++;
                    score -= Math.abs(scoreForTrade);
                    //System.out.println("score after realizing user didn't play anything: "+score);
                }
            }
        }
        
        //tradeCounter++;
    }
    
    private DataPoint getGoalDataPoint(){
        int goalIndex = (tradeCounter - 1) % numTargetDataPoints;
        DataPoint goalDataPoint = dataPointsToEmulate[goalIndex];
        return goalDataPoint;
    }
    
    private DataPoint getDataPointToPaste(){
        int pasteIndex  = 0;
        if (numTargetDataPoints == 0){
            //System.out.println("0 target datapoints found!!");
        }else{
            //System.out.println(numTargetDataPoints+" target datapoints found!!");
            pasteIndex = tradeCounter % numTargetDataPoints;
        }
        for(int i = 0; i < dataPointsToEmulate.length; i++){
            //System.out.println("dataPointsToEmulate["+i+"]: "+dataPointsToEmulate[i]);
        }
        DataPoint pasteDataPoint = dataPointsToEmulate[pasteIndex];
        tradeCounter++;
        return pasteDataPoint;
    }
    
    
    private Polylist getTargetRhythm(DataPoint targetDataPoint){
        Polylist ruleString = Polylist.PolylistFromString(targetDataPoint.getRuleString());

        return ruleString;
    }
    
    public MelodyPart getFirstRhythm(){
        DataPoint goalDataPoint = getDataPointToPaste();
        Polylist rhythmPolylist = getTargetRhythm(goalDataPoint);
        String rhythmString = getRhythmStringFromRuleStringPolylist(rhythmPolylist);
        MelodyPart rhythmTemplate = new MelodyPart(rhythmString);
        
        return rhythmTemplate;
    }

//    @Override
//    public void setResponseInfo(TradingResponseInfo responseInfo)
//    {
//        System.out.println("calling setResponse from correctRhythmTRM........");
//        super.setResponseInfo(responseInfo);
//        for(int i = 0; i < clusterArray.size(); i++){
//            clusterArray.get(i).createDataPointsFromRuleStrings(maxMetricValues, minMetricValues);
//        }
//    }
    
    public double getScore(){
        return score;
    }

    public void setRuleStringsToEmulate(ArrayList<Polylist> selectedRuleStrings) {
        //System.out.println("selectedRuleStrings: "+selectedRuleStrings.toString());
        ruleStringsToEmulate = selectedRuleStrings;
        dataPointsToEmulate = getDataPointsToEmulate(ruleStringsToEmulate);
    }

    private DataPoint[] getDataPointsToEmulate(ArrayList<Polylist> ruleStringsToEmulate) {
        numTargetDataPoints = ruleStringsToEmulate.size();
        DataPoint[] dataPoints = new DataPoint[ruleStringsToEmulate.size()];
        for(int i = 0; i < ruleStringsToEmulate.size(); i++){
            dataPoints[i] = ruleStringToNormalizedDataPoint(ruleStringsToEmulate.get(i));
        }
        return dataPoints;
    }

    @Override
    protected Polylist getRhythmPolylist(RhythmCluster closestCluster, DataPoint userDataPoint) {
        response = responseInfo.getResponseNoMerge();
        nextSection = responseInfo.getNextSection();
        DataPoint pasteDataPoint = getDataPointToPaste();
        gradeUser(userDataPoint);
        Polylist rhythmPolylist = getTargetRhythm(pasteDataPoint);
        
        return rhythmPolylist;
    }
    
    @Override
    public int getNumTrades(){
        //System.out.println("calling correct getNum trades: "+ tradeCounter + "-" + numSkipped);
        return tradeCounter - numSkipped;
    }
    
    @Override
    protected MelodyPart createResponseFromRhythmTemplate(MelodyPart RhythmTemplate) {
        return RhythmTemplate;
}

    @Override
    protected MelodyPart getRhythmTemplate(String rhythmString, RhythmCluster closestCluster) {
        return new MelodyPart(rhythmString);
    }

       
}
