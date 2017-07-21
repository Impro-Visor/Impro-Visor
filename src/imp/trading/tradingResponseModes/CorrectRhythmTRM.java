/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.RhythmCluster;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.trading.TradingResponseInfo;
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
    
    
    public CorrectRhythmTRM(String message) {
        super(message);
        score = 0;
        System.out.println("creating a correctRhythmTRM......");
    }

    
    @Override
    public MelodyPart generateResponse(){
        
        System.out.println("\n\n\nin generateResponse for correct rhythm");
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
        System.out.println("distance from user to goal: "+error);
        
        double scoreForTrade = error - FURTHEST_RADIUS;
        if(scoreForTrade<0){
            score += Math.abs(scoreForTrade);
        }
        System.out.println("score for trade: "+ scoreForTrade);
        System.out.println("score is now: "+score);
        //tradeCounter++;
    }
    
    private DataPoint getGoalDataPoint(){
        int goalIndex = (tradeCounter - 1) % numTargetDataPoints;
        DataPoint goalDataPoint = dataPointsToEmulate[goalIndex];
        return goalDataPoint;
    }
    
    private DataPoint getDataPointToPaste(){
        int pasteIndex = tradeCounter % numTargetDataPoints;
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
////        for(int i = 0; i < clusterArray.size(); i++){
////            clusterArray.get(i).createDataPointsFromRuleStrings(maxMetricValues, minMetricValues);
////        }
//    }
    
    public double getScore(){
        return score;
    }

    public void setRuleStringsToEmulate(ArrayList<Polylist> selectedRuleStrings) {
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
    protected MelodyPart createResponseFromRhythmTemplate(MelodyPart RhythmTemplate) {
        return RhythmTemplate;
}

    @Override
    protected MelodyPart getRhythmTemplate(String rhythmString, RhythmCluster closestCluster) {
        return new MelodyPart(rhythmString);
    }

       
}
