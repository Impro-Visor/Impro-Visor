/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import static imp.Constants.BEAT;
import imp.ImproVisor;
import imp.com.CommandManager;
import imp.com.RectifyPitchesCommand;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.RhythmCluster;
import imp.generalCluster.Centroid;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.MetricListFactories.RhythmMetricListFactory;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import imp.lickgen.LickgenFrame;
import imp.lickgen.NoteConverter;
import imp.trading.ActiveTradingDialog;
import imp.trading.TradingResponseInfo;
import imp.util.NonExistentParameterException;
import imp.util.Preferences;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author Lukas Gnirke & Cai Glencross
 */
public abstract class RhythmHelperTRM extends BlockResponseMode{
    protected int[] metre;
    protected int nextSection;
    protected Notate notate;
    protected MelodyPart response;
    protected int numMetrics;
    protected Double[] maxMetricValues;
    protected Double[] minMetricValues;
    protected String clusterFileName;
    protected ActiveTradingDialog activeTradingDialog;
    protected int tradeLengthInSlots;
    protected int windowSize;
    protected double[] savedDatapointAverages;
    protected int totalNumSavedDatapoints;
    protected ArrayList<RhythmCluster> clusterArray;
    protected int tradeCounter;

    
    
    public RhythmHelperTRM(String message) {
        super(message);        
        tradeCounter = 0;
    }
    
    @Override
    public void setResponseInfo(TradingResponseInfo responseInfo)
    {
        this.responseInfo = responseInfo;
        setGlobals();
        loadAppropriateClusterFile();
    }
    
    private void setGlobals(){
        this.metre = this.responseInfo.getMetre();
        this.notate = this.responseInfo.getNotate();
        this.activeTradingDialog = this.notate.getActiveTradingDialog();
        tradeLengthInSlots = this.activeTradingDialog.getUpdatedTradeLength() * BEAT * metre[0];
        this.clusterArray = new ArrayList<RhythmCluster>();
        this.clusterFileName = retrieveClusterFileName();
        this.numMetrics = (new RhythmMetricListFactory()).getNumMetrics();   
    }
    
    
    private void loadAppropriateClusterFile(){  
        try{      
            parseClusterFile(this.clusterFileName);
        }catch(IOException e){
            System.out.println("File load failed!!");
        }
    }
    
    
    public String retrieveClusterFileName(){
        String fileName = getClusterFileNameFromPreferences();
        fileName = ImproVisor.getRhythmClusterDirectory() + "/" + fileName;
        return fileName;
    }
    
    public String getClusterFileNameFromPreferences(){
        String rtn = "";
        try{
            rtn = Preferences.getPreferenceQuietly(Preferences.CLUSTER_FILENAME);
        }catch(NonExistentParameterException e){
            System.out.println("No cluster file name found in preferences!");
        }
        
        
        return rtn;
    }
    
    protected abstract Polylist getRhythmPolylist(RhythmCluster closestCluster, DataPoint d);
    protected abstract MelodyPart getRhythmTemplate(String rhythmString, RhythmCluster closestCluster);
    protected abstract MelodyPart createResponseFromRhythmTemplate(MelodyPart RhythmTemplate);
    
    /**Trading response method for correcting a user's rhythm
     * takes the solo played by the user, finds the cluster it would fit best with
     * and picks a random melody from that cluster, it then takes the note pitches
     * from the user's melody and maps them onto the rhythm from the cluster melody
     * and returns that as the corrected rhythm
     * 
     * 
     * @return response, the user's melody with a corrected rhythm
     */
    public MelodyPart getTradingResponse(){
       response = responseInfo.getResponseNoMerge();
       nextSection = responseInfo.getNextSection();
//        System.out.println("In correct rhythm we are like about to change some notes");
//        tradeCounter++;

        
//        System.out.println("trade length in slots is: " + tradeLengthInSlots);
        
        DataPoint d = getDataPointForUser();
        d = CreateGrammar.normalizeDataPoint(d, maxMetricValues, minMetricValues);
        //System.out.println("normalized datapoint: " + d);
        RhythmCluster bestFit = findNearestCluster(clusterArray, d);
        bestFit.addUserDataPoint(d);

        Polylist rhythmPolylist = getRhythmPolylist(bestFit, d);
        String rhythmString = getRhythmStringFromRuleStringPolylist(rhythmPolylist);
        MelodyPart rhythmTemplate = getRhythmTemplate(rhythmString, bestFit);
        response = createResponseFromRhythmTemplate(rhythmTemplate);
        System.out.println("response before rectify"+response);
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseInfo.getResponseChords(), false, false, true, true, true, false);
        cmd.execute();
        System.out.println("response after rectify"+response);
        return response;

        //getRhythmPolylist
        //createResponse
        
        //System.out.println("relativePitch of dataPoint unchanged: " + getDataPointForUser().getRelativePitchMelody());
        //testingArray.get(index).add(getDataPointForUser().getRelativePitchPolylist().toString());
        //testingArray.get(index).add(getDataPointForUser().toString());
       // System.out.println("The best fit cluster: " + bestFit.toString());
        //Polylist rhythmPolylist = bestFit.getRandomRhythm();
//        Polylist rhythmPolylist = getRhythmFromCluster(bestFit, d);
//        String rhythm = getRhythmStringFromRuleStringPolylist(rhythmPolylist);
//        MelodyPart rhythmTemplate = new MelodyPart(rhythm);
//        if(rhythmTemplate.getEndTime() < tradeLengthInSlots){
//           rhythmTemplate = extendRhythmTemplate(rhythmTemplate, bestFit);
//        }
//
//        if(rhythmTemplate.getEndTime() > tradeLengthInSlots){
//            rhythmTemplate = truncateRhythmTemplate(rhythmTemplate);
//        }
//        fitToRhythm(rhythmTemplate);
////        System.out.println("testing array when tradecounter = "+tradeCounter);
//        //printTestingArray(clusterArray);
//        
//        
////        if(tradeCounter % 5==0){
////            adjustClusters();
////        }
//        System.out.println("response chords"+responseInfo.getResponseChords());
//        System.out.println("response before rectify"+response);
//        RectifyPitchesCommand cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseInfo.getResponseChords(), false, false, true, true, true, true);
//        cmd.execute();
//        System.out.println("response after rectify"+response);
//        return response;
    }
    
    
    //abstract protected Polylist getRhythmFromCluster(RhythmCluster closestCluster, DataPoint d);
    
    
    protected void adjustClusters(int tradeCount){
        System.out.println("\n**************************\nentered adjust cluster loop");
            System.out.println("clusters: ");
            for(int i = 0; i < clusterArray.size(); i++){
                System.out.println("cluster" + i + ": " + clusterArray.get(i).toString());
            }
            RhythmCluster biggestCluster = biggestCluster(clusterArray);
            System.out.println("biggest Cluster is: "+biggestCluster);
            //if the biggest cluster is more than twice as big as it would be if all data was evenly distributed
            System.out.println("size of biggest cluster "+biggestCluster.getNumMatches());
            System.out.println("double the size of hypothetical evenly distributed cluster: "+2*((float) tradeCount/(float) clusterArray.size()));
            if(biggestCluster.getNumMatches() > 2*((float) tradeCount/(float) clusterArray.size())){
                System.out.println("entered unbalanced cluster if statement");
                adjustClusterCentroids(biggestCluster);
                System.out.println("clusters after adjustments: "+clusterArray);
            }
    }
    
    
//    public static void averageVector(Vector<DataPoint> seg, double[] averages) {
//        for (int i = 0; i < seg.size(); i++) { 
//            DataPoint temp = seg.get(i);
//            for(int j = 0; j<numMetrics;j++){
//                double oldValue = temp.getMetrics().get(j).getValue();
//                temp.setMetricAtI(j, ((double) oldValue/averages[j]));
//            }
//        }
//    }
    
    protected DataPoint normalizeDataPoint(DataPoint d){
        for(int i = 0; i < numMetrics; i++){
            double oldValue = d.getMetrics()[i].getValue();
            d.setMetricAtI(i, ((double) oldValue / savedDatapointAverages[i]));
        }
        
        return d;
    }
            
    /**Determines how much we should shift the centroids to account for the similarity
     * of the user's solos
     * 
     * @param problemMetricIndex - the index of the metric most similar across a cluster
     * that is disproportionately large (problemCluster)
     * @param metaCentroid - the avg of all the centroids
     * @param avgUserDataPointMetrics the average of all user data points played in this exchange
     * @return distance metaCentoid should be shifted to account for the problem metric
     */
    protected double getProblemMetricShiftAmount(int problemMetricIndex, Centroid metaCentroid, Metric[] avgUserDataPointMetrics){
        float d = (float) 0.6;//set by us
        double newValue;
        
        Metric metaCentroidProblemMetric = metaCentroid.getMetricAtI(problemMetricIndex);
        System.out.println("metacentroid: " + metaCentroid);
        Metric avgUserDataPointProblemMetric = avgUserDataPointMetrics[problemMetricIndex];
        
        
        //M’ = M’ + (d * (M - M’) ) where M' is value of problem Metric for metaCentroid, M is value of problem metric for avgUserDataPoint
       
        newValue = metaCentroidProblemMetric.getValue() + (d * ( avgUserDataPointProblemMetric.getValue() - metaCentroidProblemMetric.getValue()));
        
        
        //shift amount is difference between old and new value
        return newValue - metaCentroidProblemMetric.getValue();
        
    }
    
    /**Moves the centroids of the clusters to account for similarities in the user's
     * solos
     * 
     * @param biggestCluster - the cluster that the most user rhythms are being mapped to
     */
    protected void adjustClusterCentroids(RhythmCluster biggestCluster){
        Centroid metaCentroid = getCentroidofAllClusters(clusterArray);
        Metric[] avgUserDataPointMetrics = biggestCluster.getAvgUserDataPointMetrics();
        System.out.println("avgDataPointMetrics: " + avgUserDataPointMetrics);
        int problemMetricIndex = getProblemMetricIndex(biggestCluster);
        System.out.println("problem metric index is: " + problemMetricIndex + ", metric is: " + avgUserDataPointMetrics[problemMetricIndex].getName());
        double shiftAmount = getProblemMetricShiftAmount(problemMetricIndex, metaCentroid, avgUserDataPointMetrics);
        System.out.println("\nshift value is: " + shiftAmount);
        
        
        for(int i = 0; i < clusterArray.size(); i++){
            System.out.println("\ncentroid " + i + " was: " + clusterArray.get(i).getCentroid());
            clusterArray.get(i).getCentroid().getMetricAtI(problemMetricIndex).adjustValue(shiftAmount);
            clusterArray.get(i).resetData();
            //testingArray.get(i).clear();
            System.out.println("centroid " + i + " after shift: " + clusterArray.get(i).getCentroid());
        }
    }
    

    /**Helpful function for printing an array 
     * 
     * @param a - the array to be printed
     */
    protected static void printArray(double[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println("    `" + a[i]);
        }
    }
    
    /**Compute the difference vector between the user's average
     *
     * 
     * @param biggestCluster
     * @return 
     */
//    private double[] getNormalizedDifferenceVector(RhythmCluster biggestCluster){
//        double[] avgDatapointCentroidDistance = biggestCluster.getAvgCentroidDatapointDistance();
//        double[] avgDatapointCentroidDistanceNormalized = biggestCluster.getAvgCentroidDatapointDistance();
//        for(int i = 0; i < numMetrics; i++){
//            avgDatapointCentroidDistanceNormalized[i] = avgDatapointCentroidDistance[i]//non-normalized average metric distance value between datapoints that matched to cluster c and the centroid of cluster c 
//                                                        / 
//                                                        (maxCentroidMetricValues[i] - minCentroidMetricValues[i]);//range of metric values over centroids
//        }
//        
//        return avgDatapointCentroidDistanceNormalized;
//     
//    }
    
     protected int getProblemMetricIndex(RhythmCluster biggestCluster){
         double minDistance = Double.MAX_VALUE;
         int problemMetricIndex = -1;
         Metric[] averageMatchedUserMetrics = biggestCluster.getAvgUserDataPointMetrics();
         
         System.out.println("averageMatchedUserMetrics: " + averageMatchedUserMetrics.toString());
         
         Metric[] centroidMetrics = biggestCluster.getCentroid().getMetrics();
         
         System.out.println("centroidMetrics: " + centroidMetrics.toString());
         
         for(int i = 0; i < numMetrics; i++){
             double diff = Math.abs( averageMatchedUserMetrics[i].getValue() - centroidMetrics[i].getValue() );
             if(diff < minDistance){
                minDistance = diff;
                problemMetricIndex = i;
            }
         }
                  

//        double[] avgDatapointCentroidDistanceNormalized = getNormalizedDifferenceVector(biggestCluster);
//        System.out.println("\navgDatapointCentroidDistanceNormalized: ");
//        printArray(avgDatapointCentroidDistanceNormalized);
//       
//        double minDistance = Double.MAX_VALUE;
//        int problemMetricIndex = -1;
//        
//        //System.out.println("printing normalized avg datapoint centroid distance values: ");
//        //printArray(avgNormalizedDatapointCentroidDistance);
//
//        
//        for(int i = 0; i < numMetrics; i++){
//            if(avgDatapointCentroidDistanceNormalized[i] < minDistance){
//                minDistance = avgDatapointCentroidDistanceNormalized[i];
//                problemMetricIndex = i;
//            }
//        }
//        
//        System.out.println("Problem metric index: " + problemMetricIndex + ", metric is: " + biggestCluster.getCentroid().getMetrics().get(problemMetricIndex).getName());
                
        return problemMetricIndex;
    }
    
    protected Centroid getCentroidofAllClusters(ArrayList<RhythmCluster> clusterArray){
        
        Metric[] metrics = (new RhythmMetricListFactory()).getNewMetricList();
        
         for(int i = 0; i<metrics.length; i++){
            for(int j = 0; j < clusterArray.size(); j++){
                double currentValue;
                if(metrics[i].getValue() == null){
                     currentValue = 0;
                }else{
                    currentValue = metrics[i].getValue();
                }

                metrics[i].setValue(currentValue + clusterArray.get(j).getCentroid().getMetricAtI(i).getValue());
            }
            
            double sumOfVals = metrics[i].getValue();
            metrics[i].setValue(sumOfVals / clusterArray.size());
        }
        
      
        
        return new Centroid(metrics);
    }
    
    protected RhythmCluster biggestCluster(ArrayList<RhythmCluster> clusterArray){
        float maxSize = -1;
        RhythmCluster biggestCluster = null;
        for(int i = 0; i < clusterArray.size(); i++){
            RhythmCluster cluster = clusterArray.get(i);
            if(cluster.getNumMatches()>maxSize){
                maxSize = cluster.getNumMatches();
                biggestCluster = cluster;
            }
        }
        return biggestCluster;
    }
    
    protected MelodyPart truncateRhythmTemplate(MelodyPart rhythmTempSoFar){
        MelodyPart finalRhythmTemp = new MelodyPart();
        System.out.println("\noverly long rhythmTemp matched: " + rhythmTempSoFar.toString());
        System.out.println("length of this template: " + rhythmTempSoFar.getEndTime());

        int tempSoFarIterator = rhythmTempSoFar.getFirstIndex();
        while(tempSoFarIterator < tradeLengthInSlots && rhythmTempSoFar.getNote(tempSoFarIterator) != null){
            System.out.println("tempSoFarIterator: " + tempSoFarIterator);
            finalRhythmTemp.addNote(rhythmTempSoFar.getNote(tempSoFarIterator));
            tempSoFarIterator = rhythmTempSoFar.getNextIndex(tempSoFarIterator);
                    
        }
        
        return finalRhythmTemp;
    }
    
    protected MelodyPart extendRhythmTemplate(MelodyPart rhythmTempSoFar, RhythmCluster bestFit){
        MelodyPart finalRhythmTemp = rhythmTempSoFar;
        while(finalRhythmTemp.getEndTime() < response.getEndTime()){
            MelodyPart newRhythmTemp = new MelodyPart(getRhythmStringFromRuleStringPolylist(bestFit.getRandomRhythm()));
            finalRhythmTemp = concatMelodyParts(finalRhythmTemp, newRhythmTemp);
        }
        return finalRhythmTemp;
        
    }
    
    protected MelodyPart concatMelodyParts(MelodyPart mel1, MelodyPart mel2){
        int mel2Iterator = mel2.getFirstIndex();
        
        while(mel2.getNote(mel2Iterator) != null){
            mel1.addNote(mel2.getNote(mel2Iterator));
            mel2Iterator = mel2.getNextIndex(mel2Iterator);
        }
        
        return mel1;
    }
            
    
//    private void printTestingArray(ArrayList<RhythmCluster> clusterArray){
//        
//        for (int i = 0; i<testingArray.size(); i++){
//            System.out.println("cluster"+i+" centroid: "+clusterArray.get(i).getCentroid().toString());
//            //System.out.println("cluster"+i+" rhythmList: ");
////            for (Polylist p:clusterArray.get(i).getRhythmList() ){
////                System.out.println("    "+p.toString());
////            }
//            System.out.println("Number of rhythms matched to cluster"+i+": "+testingArray.get(i).size());
//            System.out.println("Rhythms matched to cluster"+i+":");
//            
//            for(String s: testingArray.get(i)){
//                System.out.println("    user rhythm: "+s);
//            }
//        }
//        
//        System.out.println("\n\n");
//        
//    }
    
    
   
    
    
    protected String getAbstractMelody(){
        LickGen lg = new LickGen(ImproVisor.getGrammarFile().getAbsolutePath(),notate, null);
        LickgenFrame lgf = new LickgenFrame(notate, lg, new CommandManager());

        String abstractMel = lgf.addMeasureToAbstractMelody(nextSection - 480*4, 16, false, false);
        
        return abstractMel;
    }
    
    protected String getAbstractMelWithAllRests(int responseLength){
        String rtn = "((slope 0 0 R";
        int count = BEAT * 4;
       
       while(count < responseLength){
           rtn += 1 + "+";
           count += BEAT * 4;
       }
       if(count != responseLength){
           int remaining = responseLength - (count - (BEAT * 4));
           rtn += remaining / BEAT;
       }
       if(rtn.charAt(rtn.length() - 1) == '+'){
           rtn = rtn.substring(0, rtn.length() - 1);
       }
       rtn += "))";
       
        return rtn;
    }
    
    protected DataPoint getDataPointForUser(){
        int responseLength = response.getEndTime();
        int ruleSegLength = responseLength / BEAT;
        
        LickGen lg = new LickGen(ImproVisor.getGrammarFile().getAbsolutePath(),notate, null);
        LickgenFrame lgf = new LickgenFrame(notate, lg, new CommandManager());
        
        
        int startOfUserMelody = nextSection - responseLength;
        if (nextSection == 0){
            startOfUserMelody = notate.getScoreLength() - responseLength;
        }
        
        System.out.println("first index of response: "+response.getFirstIndex()+", start of user melody: "+startOfUserMelody);
        String abstractMel = lgf.addMeasureToAbstractMelody(startOfUserMelody, ruleSegLength, false, false);
        String exactMelody = lgf.getExactMelody(ruleSegLength, abstractMel, startOfUserMelody);
        exactMelody = mergeRests(exactMelody);

        
        if(abstractMel == null){//if melody played by user is all rests, abstractMel will be null. Create an abstractMel with all rests
            abstractMel = getAbstractMelWithAllRests(responseLength);
        }
        
        abstractMel = abstractMel.substring(1, abstractMel.length() - 1);
                
        String relativePitch = NoteConverter.melPartToRelativePitch(response, notate.getChordProg());
        //System.out.println("abstractMelody to Polylist: " + Polylist.PolylistFromString(abstractMel));
        
        /**@TODO figure out a way to not hard code chords, since these chords aren't used for anything it doesn't matter right now though*/
        /**@TODO use Polylist rule in ruleString for cleanliness & efficiency*/
        Polylist rule = Polylist.list("Seg"+ruleSegLength).append(Polylist.PolylistFromString(abstractMel));
        String ruleString = Polylist.list("rule", Polylist.list("Seg"+ruleSegLength), Polylist.PolylistFromString(abstractMel)) + "(Xnotation " + relativePitch + ") (Brick-type null) Head "+exactMelody+" CHORDS Fm7 G7b9";
        String i = "0";
        
        DataPoint d = CreateGrammar.processRule(rule, ruleString, i, (new RhythmMetricListFactory()));
        
        if(d.getSegLength() != windowSize){
            d.scaleMetrics(( (float) windowSize )/ d.getSegLength());
        }
        
        return d;
    }
    
    protected void checkIfMelPartOK(){
        //int index = response.;
    }
    
    protected String mergeRests(String exactMelody){
        System.out.println("exactMelody before merging rests: " + exactMelody);
        
        String rtn = "";
        String[] melParts = exactMelody.split("\\s+");
        rtn += melParts[0]+ " ";
        boolean lastNoteWasRest = false;
        int lastNoteDuration = 0;
        for (int i = 1; i<melParts.length; i+=2){
            String pitch = melParts[i];
            String duration = melParts[i+1];
            if(pitch.equals("-1")&&lastNoteWasRest){
               int durationInt = lastNoteDuration + Integer.parseInt(duration);
               rtn = rtn.trim();
               int substringIndex = rtn.lastIndexOf(" ");
               rtn = rtn.substring(0, substringIndex+1);
               rtn += durationInt + " ";
               continue;
            }
            rtn += pitch + " " + duration + " ";
            lastNoteDuration = Integer.parseInt(duration);
            if(pitch.equals("-1")){
                lastNoteWasRest = true;
            }else{
                lastNoteWasRest = false;
            }           
            
        }
        
       //System.out.println("exactMelody after merging rests: " + rtn);
        return rtn;
    }
    
    public RhythmCluster findNearestCluster(ArrayList<RhythmCluster> clusterArray, DataPoint d){
        
     
        //System.out.println("DataPoint " + d.toString());
        
        Metric[] usersMetrics = d.getMetrics();
        
        for (RhythmCluster r: clusterArray){
            Centroid c = r.getCentroid();
            //System.out.println("\nCentroid: "+c.toString());
            
            if(d.getRhythmCluster() == null){
                d.setRhythmCluster(r);
            }
            //System.out.println("Current closest centroid: "+ d.getRhythmCluster().getCentroid().toString());
            //System.out.println("Current Euclidean distance to centroid: "+ d.getEuclideanDistanceToCentroid(d.getRhythmCluster().getCentroid()));
            //System.out.println("Euclidean distance to new centroid: "+d.getEuclideanDistanceToCentroid(c));
            if(d.getEuclideanDistanceToCentroid(c) < 
                                    d.getEuclideanDistanceToCentroid(d.getRhythmCluster().getCentroid())){
                //System.out.println("updating closest to: "+r.getCentroid().toString());
                d.setRhythmCluster(r);
            }
            
        }
        //System.out.println("closest RhythmCluster's centroid is: " + d.getRhythmCluster().getCentroid().toString());

        return d.getRhythmCluster();
    }
    
    protected void parseClusterFile(String path) throws IOException{
        
        FileReader clusterFileReader = new FileReader(path);
        BufferedReader clusterTextReader = new BufferedReader(clusterFileReader);
        
        
        windowSize = loadWindowSizeFromFile(clusterTextReader);
        
        maxMetricValues = loadMaxMetricValsFromFile(clusterTextReader);
        
        minMetricValues = loadMinMetricValsFromFile(clusterTextReader);
          
        clusterArray = loadClustersFromFile(clusterTextReader);
        
        clusterTextReader.close();
    }
    
    protected Double[] polyListArrayToDoubleArray(Polylist polylistArray){
        int numElements = polylistArray.length();
        Double[] rtn = new Double[ numElements ];
        int iterator = 0;
        while(!polylistArray.isEmpty() && iterator < numElements){
            rtn[iterator] = (double) polylistArray.first();
            polylistArray = polylistArray.rest();
            iterator++;
        }
        return rtn;
    }
    
    protected int loadTotalNumSavedDataPoints(BufferedReader clusterTextReader) throws IOException {
        String totalNumDataPointsPLString = clusterTextReader.readLine();
        Polylist totalNumDataPointsPL = Polylist.PolylistFromString(totalNumDataPointsPLString);
 
        if(totalNumDataPointsPL.first() instanceof Polylist){totalNumDataPointsPL = (Polylist) totalNumDataPointsPL.first();}//for some reason this first string polylist gets read in as a double polylist
        
        if (( (String) totalNumDataPointsPL.first() ).equals("totalNumDataPoints")){
            return ((Long) totalNumDataPointsPL.second()).intValue();
      
        }else{
            System.out.println("malformed cluster file, could not find tag 'totalNumDataPoints'!!!!");
            return -1;
        }
    }
    
     protected Double[] loadMaxMetricValsFromFile(BufferedReader clusterTextReader) throws IOException {
        String metricMaxValsPLString = clusterTextReader.readLine();
        Polylist metricMaxValsPL = Polylist.PolylistFromString(metricMaxValsPLString);
        System.out.println("maxMetricValues: " + metricMaxValsPL.toString());
        if(metricMaxValsPL.first() instanceof Polylist){metricMaxValsPL = (Polylist) metricMaxValsPL.first();}//for some reason this first string polylist gets read in as a double polylist
        
        if (( (String) metricMaxValsPL.first() ).equals("maxMetricValues")){
            Polylist maxVals =  (Polylist) metricMaxValsPL.second();
            return polyListArrayToDoubleArray(maxVals);
        }else{
            System.out.println("malformed cluster file, could not find tag 'maxMetricValues'!!!!");
            return new Double[0];
        }
    }
    
    protected Double[] loadMinMetricValsFromFile(BufferedReader clusterTextReader) throws IOException {
        String metricMinValsPLString = clusterTextReader.readLine();
        Polylist metricMinValsPL = Polylist.PolylistFromString(metricMinValsPLString);
        System.out.println("minMetricValues: " + metricMinValsPL.toString());
        if(metricMinValsPL.first() instanceof Polylist){metricMinValsPL = (Polylist) metricMinValsPL.first();}//for some reason this first string polylist gets read in as a double polylist
        
        if (( (String) metricMinValsPL.first() ).equals("minMetricValues")){
            Polylist maxVals =  (Polylist) metricMinValsPL.second();
            return polyListArrayToDoubleArray(maxVals);
        }else{
            System.out.println("malformed cluster file, could not find tag 'minMetricValues'!!!!");
            return new Double[0];
        }
    }
    
    protected int loadWindowSizeFromFile(BufferedReader clusterTextReader)throws IOException{
        String windowSizePLString = clusterTextReader.readLine();
        System.out.println("windowSizePLString: " + windowSizePLString);
        Polylist windowSizePL = Polylist.PolylistFromString(windowSizePLString);
        if(windowSizePL.first() instanceof Polylist){windowSizePL = (Polylist) windowSizePL.first();}//for some reason this first string polylist gets read in as a double polylist
        
        if (( (String) windowSizePL.first() ).equals("windowSize")){
            return ((Long) windowSizePL.second()).intValue();
        }else{
            System.out.println("malformed cluster file, could not find tag 'windowSize'!!!!");
            return -1;
        }
        
    }
    protected ArrayList<RhythmCluster> loadClustersFromFile(BufferedReader clusterTextReader) throws IOException{
        
        String clusterString = "";
        String line;
        while((line = clusterTextReader.readLine()) != null){
            line = line.trim();
            clusterString+=line;
        }
        
        
        System.out.println("cluster string is: "+clusterString);
        Polylist clustersPolylist = Polylist.PolylistFromString(clusterString);
        ArrayList<RhythmCluster> rhythmClusters = new ArrayList<RhythmCluster>(); 
        int iterator = 0;
        while (!clustersPolylist.isEmpty()){
            Polylist clusterIpolylist = (Polylist) clustersPolylist.first();
            System.out.println("cluster polylist is: "+ clusterIpolylist);
            RhythmCluster rc = new RhythmCluster(clusterIpolylist, iterator);
            if(numMetrics == -1){numMetrics = rc.getCentroid().getMetrics().length;}//set global numMetrics if it hasn't been set yet
            //System.out.println("rhythm list size for cluster: " + rc.getRhythmList().size());
            rhythmClusters.add(rc);
            clustersPolylist = clustersPolylist.rest();   
            iterator++;
        }
        

       // clusterFileReader.close();
        //System.out.println("in load clusters from file, buffer reader closed");
        
        
        return rhythmClusters;
    }
    
    protected double[] fillInitialArrayValues(double[] a, double value){
        for (int i = 0; i < a.length; i++){
            a[i] = value;
        }
        
        return a;
    }
    
    public Double[] getMaxMetricVals(){
        return maxMetricValues;
    }
    
    public Double[] getMinMetricVals(){
        return minMetricValues;
    }
    
    public int getTotalNumSavedDataPoints(){
        return totalNumSavedDatapoints;
    }
    
         
    protected String getRhythmStringFromRuleStringPolylist(Polylist p){
        String rhythmString = "";
        //get rid of "ruleString" tag
        Polylist relativePitchPolylist = (Polylist) p.second();
        //System.out.println("\nrelativePitchPolylist: " + relativePitchPolylist);
        //get rid of "rule" tag
        relativePitchPolylist = relativePitchPolylist.rest();
        System.out.println("relativePitchPolylist after getting rid of tag: " + relativePitchPolylist);
        
        while(!relativePitchPolylist.isEmpty()){
            Polylist notePolylist;
            
            if(relativePitchPolylist.first() instanceof String){
                String toPL = ((String) relativePitchPolylist.first()).charAt(0) + " -1 " + ((String) relativePitchPolylist.first()).substring(1);
                notePolylist = Polylist.PolylistFromString(toPL);     
            }else if (relativePitchPolylist.first() instanceof Polylist){
                notePolylist = (Polylist) relativePitchPolylist.first();
            }else{
                System.out.println("relativePitchPolylist.first() is: " + relativePitchPolylist.first() + " and it is neither String nor Polylist"
                        + " This is a problem.");
                notePolylist = null;
            }
            
            //System.out.println("notePolylist is: " + notePolylist);
            
            if(notePolylist.first().equals("X")){
                rhythmString += "c";
            }else{
                rhythmString += notePolylist.first();
            }
            rhythmString+=notePolylist.third() + " ";
            relativePitchPolylist = relativePitchPolylist.rest();
            
        }
        //System.out.println("rhythmString: "+rhythmString);
        return rhythmString;
    }
    
    protected MelodyPart fitToRhythm(MelodyPart rhythmTemplate){
        int thePitch;
        int responseIterator = response.getFirstIndex();
        int rhythmTemplateIterator = rhythmTemplate.getFirstIndex();
        int nextNoteIndex;
        boolean responseHasMoreNotes = true;

        //System.out.println("response: "+response);
        
        while(rhythmTemplate.getNote(rhythmTemplateIterator) != null){//loop through all notes in the rhythm template
            if (response.getNote(responseIterator) == null){
                responseHasMoreNotes = false;
            }
            if(responseHasMoreNotes){
                Note n = response.getCurrentNote(responseIterator);
//                System.out.println("Note to fit is: " + n);
                if (n.isRest()){
                    responseIterator = response.getNextIndex(responseIterator);//increment responseIterator
                    if (response.getNote(responseIterator) == null){
                        responseHasMoreNotes = false;
                    }  
                }
                
            }
            
            if (responseHasMoreNotes){
                Note n = response.getCurrentNote(responseIterator);
//                System.out.println("Note to fit is: " + n);
                if(!rhythmTemplate.getCurrentNote(rhythmTemplateIterator).isRest()){
                    rhythmTemplate.getCurrentNote(rhythmTemplateIterator).setPitch(n.getPitch());
                    rhythmTemplate.getCurrentNote(rhythmTemplateIterator).setAccidental(n.getAccidental());
                    responseIterator = response.getNextIndex(responseIterator);
                }
                
            }else{
//                System.out.println("in the else statement!");
//                System.out.println("responseIterator: "+responseIterator);
//                System.out.println("response size"+ response.getSize());
                responseIterator = response.getFirstIndex();
                Note n = response.getCurrentNote(responseIterator);
                rhythmTemplate.getCurrentNote(rhythmTemplateIterator).setPitch(n.getPitch());
                responseIterator = response.getNextIndex(responseIterator);
                responseHasMoreNotes = true;
            }
 
            rhythmTemplateIterator = rhythmTemplate.getNextIndex(rhythmTemplateIterator);//get the next note in the rhythm template
        }
        //System.out.println("rhythmTemplate is now "+rhythmTemplate);
        response = rhythmTemplate;
        return response;
    }
    
    public ArrayList<RhythmCluster> getRhythmClusters() {
        return this.clusterArray;
    }
    
    public int getWindowSizeOfCluster(){
        return windowSize;
    }

    public int[] getMetre() {
        return this.metre;
    }
    
    public Notate getNotate(){
        return this.notate;
    }

    public String getClusterFileName() {
        return this.clusterFileName;
    }
    
    public int getNumTrades(){
        return tradeCounter;
}
    
     public DataPoint ruleStringToNormalizedDataPoint(Polylist ruleStringPL){
        String ruleString = ruleStringPL.toStringSansParens();
        //System.out.println("\nruleString: " + ruleString);
        Polylist rulePL = (Polylist) ruleStringPL.first();
        Polylist abstractMel = (Polylist) rulePL.third();
        //System.out.println("abstractMel: " + abstractMel.toString());
        Polylist SegLen = (Polylist) rulePL.second();
        Polylist rule = Polylist.list(SegLen.toStringSansParens()).append(abstractMel);
        //System.out.println("rule: " + rule.toString());
        DataPoint d = CreateGrammar.processRule(rule, ruleString, "0", (new RhythmMetricListFactory()) );
        //System.out.println("maxVals[0]: " + maxVals[0]);

        d.normalize(maxMetricValues, minMetricValues);
        
        return d;
    }
    
}
