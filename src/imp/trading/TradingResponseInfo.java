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

import imp.Constants;
import static imp.Constants.BEAT;
import imp.ImproVisor;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.com.CommandManager;
import imp.com.InvertCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ReverseCommand;
import imp.data.AbstractMelodyExtractor;
import imp.themeWeaver.BeatFinder;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.guidetone.GuideLineGenerator;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.RhythmCluster;
import imp.generalCluster.Centroid;
import imp.generalCluster.Metric;
import imp.gui.Notate;
import imp.lickgen.Grammar;
import imp.lickgen.LickGen;
import imp.lickgen.LickgenFrame;
import imp.lickgen.NoteConverter;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.TransformLearning;
import imp.midi.StreamingMidiRecorder;
import imp.util.NonExistentParameterException;
import imp.util.Preferences;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import polya.Polylist;

/**
 * Holds info regarding an active trading response; also holds
 * methods widely used by response models.
 * 
 * @author Mikayla Konst, Zach Kondak
 */
public class TradingResponseInfo {
    
    private MelodyPart response;
    private MelodyPart grammarSolo;
    private MelodyPart originalMelody;
    private static final int start = 0;
    private int[] metre;
    private int stop;
    private int nextSection;
    private int tradeLength;
    private ChordPart soloChords;
    private ChordPart responseChords;
    private Notate notate;
    private LickGen lickGen;
    private Transform musician;
    private final BeatFinder beatFinder;
    private final TransformLearning flattener;
    private static final boolean ONLY_CHORD_TONES = true;
    private static final boolean ALL_TONES = false;
    //HAS NOTHING TO DO WITH PHRASE CLASS
    private PhraseTable phrases;
    private static ArrayList<ArrayList<String>> testingArray = new ArrayList<ArrayList<String>>();
    private static int tradeCounter; //counter for number of times traded, for checking numClusters matched in user personalization
    private ArrayList<RhythmCluster> clusterArray;
    int numMetrics = -1;
    private float[] maxCentroidMetricValues;
    private float[] minCentroidMetricValues;
    private String clusterFileName;
    private ActiveTradingDialog activeTradingDialog;
    int tradeLengthInSlots;
    private int windowSize;

  
    
    public TradingResponseInfo(Notate notate, int [] metre, int tradeLength) {
        this.notate = notate;
        this.lickGen = notate.getLickGen();
        this.beatFinder = new BeatFinder(metre);
        this.flattener = new TransformLearning();
        this.metre = metre;
        this.tradeLength = tradeLength;
        
        this.activeTradingDialog = notate.getActiveTradingDialog();
        tradeLengthInSlots = activeTradingDialog.getUpdatedTradeLength() * BEAT * metre[0];
        //grammarSolo = notate.genSolo(0, notate.getScoreLength());
        
        //if(!activeTradingDialog.getTradeModeName().equals("Rhythm Helper")){
            System.out.println("entering the if statement and doing unnecessary grammar stuff");
            grammarSolo = generateFromGrammar(notate.getScoreLength(), notate.getChordProg());
        //}
        
        phrases = new PhraseTable(notate);
        tradeCounter = 0;
        
        System.out.println("notate score length is: "+ notate.getScoreLength());
        System.out.println("notate score is: "+ notate.getScore());
        clusterArray=new ArrayList<RhythmCluster>();
        clusterFileName = retrieveClusterFileName();
        try{
            
            //clusterArray = loadClustersFromFile(imp.generalCluster.CreateGrammar.getClusterOutputFile(clusterFileName));
            parseClusterFile(clusterFileName);
            setCentroidMetricMaxMinValues(clusterArray);//set the arrays keeping track of max and min values for all metrics over all centroids
           

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
    
    
    public String getClusterFileName(){
        return clusterFileName;
    }
    
    public Notate getNotate()
    {
        return notate;
    }
    
    public ChordPart getResponseChords()
    {
        return responseChords;
    }
    public void updateResponse(MelodyPart originalMelody,ChordPart soloChords, ChordPart responseChords, int nextSection){
        
        if(originalMelody != null) {
            this.response = originalMelody;
            this.originalMelody = originalMelody.copy();
            this.stop = response.size()-1;
        }
        this.soloChords = soloChords;
        this.responseChords = responseChords;
        this.nextSection = nextSection;
    }
    
    /**Trading response method for correcting a user's rhythm
     * takes the solo played by the user, finds the cluster it would fit best with
     * and picks a random melody from that cluster, it then takes the note pitches
     * from the user's melody and maps them onto the rhythm from the cluster melody
     * and returns that as the corrected rhythm
     * 
     * 
     * @return response, the user's melody with a corrected rhythm
     */
    public MelodyPart correctRhythm(){
        System.out.println("In correct rhythm we are like about to change some notes");
        tradeCounter++;

        
        System.out.println("trade length in slots is: " + tradeLengthInSlots);
        
        
        if(testingArray.size()==0){
        for (int i = 0; i<clusterArray.size(); i++){
            testingArray.add(new ArrayList<String>());
        }
        }
        DataPoint d = getDataPointForUser();
        RhythmCluster bestFit = findNearestCluster(clusterArray, d);
        d.getRhythmCluster().addUserDataPoint(d);

        int index = clusterArray.indexOf(bestFit);
        //System.out.println("relativePitch of dataPoint unchanged: " + getDataPointForUser().getRelativePitchMelody());
        //testingArray.get(index).add(getDataPointForUser().getRelativePitchPolylist().toString());
        //testingArray.get(index).add(getDataPointForUser().toString());
       // System.out.println("The best fit cluster: " + bestFit.toString());
        Polylist rhythmPolylist = bestFit.getRandomRhythm();
        String rhythm = getRhythmStringFromPolylist(rhythmPolylist);
        MelodyPart rhythmTemplate = new MelodyPart(rhythm);
        if(rhythmTemplate.getEndTime() < tradeLengthInSlots){
           rhythmTemplate = extendRhythmTemplate(rhythmTemplate, bestFit);
        }

        if(rhythmTemplate.getEndTime() > tradeLengthInSlots){
            rhythmTemplate = truncateRhythmTemplate(rhythmTemplate);
        }
        fitToRhythm(rhythmTemplate);
        System.out.println("testing array when tradecounter = "+tradeCounter);
        //printTestingArray(clusterArray);
        
        
        if(tradeCounter % 5==0){
            System.out.println("entered adjust cluster loop");
            RhythmCluster biggestCluster = biggestCluster(clusterArray);
            System.out.println("biggest Cluster is: "+biggestCluster);
            //if the biggest cluster is more than twice as big as it would be if all data was evenly distributed
            System.out.println("size of biggest cluster "+biggestCluster.getNumMatches());
            System.out.println("double the size of hypothetical evenly distributed cluster: "+2*((float) tradeCounter/(float) clusterArray.size()));
            if(biggestCluster.getNumMatches() > 2*((float) tradeCounter/(float) clusterArray.size())){
                System.out.println("entered unbalanced cluster if statement");
                adjustClusterCentroids(biggestCluster);
                System.out.println("clusters after adjustments: "+clusterArray);
            }
        }
        
        
        return response;
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
    private float getProblemMetricShiftAmount(int problemMetricIndex, Centroid metaCentroid, ArrayList<Metric> avgUserDataPointMetrics){
        float d = (float) 0.6;//set by us
        float newValue;
        
        Metric metaCentroidProblemMetric = metaCentroid.getMetricAtI(problemMetricIndex);
        System.out.println("metacentroid: " + metaCentroid);
        Metric avgUserDataPointProblemMetric = avgUserDataPointMetrics.get(problemMetricIndex);
        
        
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
    private void adjustClusterCentroids(RhythmCluster biggestCluster){
        Centroid metaCentroid = getCentroidofAllClusters(clusterArray);
        ArrayList<Metric> avgUserDataPointMetrics = biggestCluster.getAvgUserDataPointMetrics();
        System.out.println("avgDataPointMetrics: " + avgUserDataPointMetrics);
        int problemMetricIndex = getProblemMetricIndex(biggestCluster);
        float shiftAmount = getProblemMetricShiftAmount(problemMetricIndex, metaCentroid, avgUserDataPointMetrics);
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
    private void printArray(float[] a){
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
    private float[] getNormalizedDifferenceVector(RhythmCluster biggestCluster){
        float[] avgDatapointCentroidDistance = biggestCluster.getAvgCentroidDatapointDistance();
        float[] avgDatapointCentroidDistanceNormalized = biggestCluster.getAvgCentroidDatapointDistance();
        for(int i = 0; i < numMetrics; i++){
            avgDatapointCentroidDistanceNormalized[i] = avgDatapointCentroidDistance[i]//non-normalized average metric distance value between datapoints that matched to cluster c and the centroid of cluster c 
                                                        / 
                                                        (maxCentroidMetricValues[i] - minCentroidMetricValues[i]);//range of metric values over centroids
        }
        
        return avgDatapointCentroidDistanceNormalized;
     
    }
    
     public int getProblemMetricIndex(RhythmCluster biggestCluster){

        float[] avgDatapointCentroidDistanceNormalized = getNormalizedDifferenceVector(biggestCluster);
        System.out.println("\navgDatapointCentroidDistanceNormalized: ");
        printArray(avgDatapointCentroidDistanceNormalized);
       
        float minDistance = Float.MAX_VALUE;
        int problemMetricIndex = -1;
        
        //System.out.println("printing normalized avg datapoint centroid distance values: ");
        //printArray(avgNormalizedDatapointCentroidDistance);

        
        for(int i = 0; i < numMetrics; i++){
            if(avgDatapointCentroidDistanceNormalized[i] < minDistance){
                minDistance = avgDatapointCentroidDistanceNormalized[i];
                problemMetricIndex = i;
            }
        }
        
        System.out.println("Problem metric index: " + problemMetricIndex + ", metric is: " + biggestCluster.getCentroid().getMetrics().get(problemMetricIndex).getName());
                
        return problemMetricIndex;
    }
    
    private Centroid getCentroidofAllClusters(ArrayList<RhythmCluster> clusterArray){
        ArrayList<Metric> avgCentroidMetrics = new ArrayList<Metric>();
        //loop through all metrics
        for(int i = 0; i<clusterArray.get(0).getCentroid().getMetrics().size();i++){
            avgCentroidMetrics.add(new Metric(0,0,clusterArray.get(0).getCentroid().getMetricAtI(i).getName(),clusterArray.get(0).getCentroid().getMetricAtI(i).isLengthIndependent()));
            //loop through all centroids
            for(int j = 0; j<clusterArray.size();j++){
                float currentValue = avgCentroidMetrics.get(i).getValue();
                avgCentroidMetrics.get(i).setValue(currentValue + clusterArray.get(j).getCentroid().getMetricAtI(i).getValue());
            }
            float sumOfVals = avgCentroidMetrics.get(i).getValue();
            avgCentroidMetrics.get(i).setValue(sumOfVals / clusterArray.size());
        }
        
        System.out.println("avgCentroidMetrics is: "+avgCentroidMetrics);
        
        return new Centroid(avgCentroidMetrics);
    }
    
    private RhythmCluster biggestCluster(ArrayList<RhythmCluster> clusterArray){
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
    
    private MelodyPart truncateRhythmTemplate(MelodyPart rhythmTempSoFar){
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
    
    private MelodyPart extendRhythmTemplate(MelodyPart rhythmTempSoFar, RhythmCluster bestFit){
        MelodyPart finalRhythmTemp = rhythmTempSoFar;
        while(finalRhythmTemp.getEndTime() < response.getEndTime()){
            MelodyPart newRhythmTemp = new MelodyPart(getRhythmStringFromPolylist(bestFit.getRandomRhythm()));
            finalRhythmTemp = concatMelodyParts(finalRhythmTemp, newRhythmTemp);
        }
        return finalRhythmTemp;
        
    }
    
    private MelodyPart concatMelodyParts(MelodyPart mel1, MelodyPart mel2){
        int mel2Iterator = mel2.getFirstIndex();
        
        while(mel2.getNote(mel2Iterator) != null){
            mel1.addNote(mel2.getNote(mel2Iterator));
            mel2Iterator = mel2.getNextIndex(mel2Iterator);
        }
        
        return mel1;
    }
            
    
    private void printTestingArray(ArrayList<RhythmCluster> clusterArray){
        
        for (int i = 0; i<testingArray.size(); i++){
            System.out.println("cluster"+i+" centroid: "+clusterArray.get(i).getCentroid().toString());
            //System.out.println("cluster"+i+" rhythmList: ");
//            for (Polylist p:clusterArray.get(i).getRhythmList() ){
//                System.out.println("    "+p.toString());
//            }
            System.out.println("Number of rhythms matched to cluster"+i+": "+testingArray.get(i).size());
            System.out.println("Rhythms matched to cluster"+i+":");
            
            for(String s: testingArray.get(i)){
                System.out.println("    user rhythm: "+s);
            }
        }
        
        System.out.println("\n\n");
        
    }
    
    
   
    
    
    private String getAbstractMelody(){
        LickGen lg = new LickGen(ImproVisor.getGrammarFile().getAbsolutePath(),notate, null);
        LickgenFrame lgf = new LickgenFrame(notate, lg, new CommandManager());

        String abstractMel = lgf.addMeasureToAbstractMelody(nextSection - 480*4, 16, false, false);
        
        return abstractMel;
    }
    
    private String getAbstractMelWithAllRests(int responseLength){
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
    
    private DataPoint getDataPointForUser(){
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
        
        DataPoint d = CreateGrammar.processRule(rule, ruleString, i);
        if(d.getSegLength() != windowSize){
            d.scaleMetrics(( (float) windowSize )/ d.getSegLength());
        }
        
        return d;
    }
    
    private void checkIfMelPartOK(){
        //int index = response.;
    }
    
    private String mergeRests(String exactMelody){
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
        
        ArrayList<Metric> usersMetrics = d.getMetrics();
        
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
    
    private void parseClusterFile(String path) throws IOException{
        
        FileReader clusterFileReader = new FileReader(path);
        BufferedReader clusterTextReader = new BufferedReader(clusterFileReader);
        
        windowSize = loadWindowSizeFromFile(clusterTextReader);
        clusterArray = loadClustersFromFile(clusterTextReader);
        
        clusterTextReader.close();
    }
    
    private int loadWindowSizeFromFile(BufferedReader clusterTextReader)throws IOException{
        String windowSizePLString = clusterTextReader.readLine();
        Polylist windowSizePL = Polylist.PolylistFromString(windowSizePLString);
        if(windowSizePL instanceof Polylist){windowSizePL = (Polylist) windowSizePL.first();}//for some reason this first string polylist gets read in as a double polylist
        
        if (( (String) windowSizePL.first() ).equals("windowSize")){
            return ((Long) windowSizePL.second()).intValue();
        }else{
            System.out.println("malformed cluster file!!!!");
            return -1;
        }
        
    }
    private ArrayList<RhythmCluster> loadClustersFromFile(BufferedReader clusterTextReader) throws IOException{
        
        String clusterString = clusterTextReader.readLine();
        Polylist clustersPolylist = Polylist.PolylistFromString(clusterString);
        ArrayList<RhythmCluster> rhythmClusters = new ArrayList<RhythmCluster>(); 
        int iterator = 0;
        while (!clustersPolylist.isEmpty()){
            Polylist clusterIpolylist = (Polylist) clustersPolylist.first();
            RhythmCluster rc = new RhythmCluster(clusterIpolylist, iterator);
            if(numMetrics == -1){numMetrics = rc.getCentroid().getMetrics().size();}//set global numMetrics if it hasn't been set yet
            //System.out.println("rhythm list size for cluster: " + rc.getRhythmList().size());
            rhythmClusters.add(rc);
            clustersPolylist = clustersPolylist.rest();   
            iterator++;
        }
        

       // clusterFileReader.close();
        //System.out.println("in load clusters from file, buffer reader closed");
        
        
        return rhythmClusters;
    }
    
    private float[] fillInitialArrayValues(float[] a, float value){
        for (int i = 0; i < a.length; i++){
            a[i] = value;
        }
        
        return a;
    }
    
    
    private void setCentroidMetricMaxMinValues(ArrayList<RhythmCluster> rhythmClusters){     
        maxCentroidMetricValues = new float[numMetrics];
        minCentroidMetricValues = new float[numMetrics];
        maxCentroidMetricValues = fillInitialArrayValues(maxCentroidMetricValues, Float.MIN_VALUE);
        minCentroidMetricValues = fillInitialArrayValues(minCentroidMetricValues, Float.MAX_VALUE);
        
        for(int i = 0; i< rhythmClusters.size(); i++){
            ArrayList<Metric> centroidMetricList = rhythmClusters.get(i).getCentroid().getMetrics();
            for(int j = 0; j < numMetrics; j++){
                if(centroidMetricList.get(j).getValue() > maxCentroidMetricValues[j]){          
                    maxCentroidMetricValues[j] = centroidMetricList.get(j).getValue();
                }
                
                if(centroidMetricList.get(j).getValue() < minCentroidMetricValues[j]){          
                    minCentroidMetricValues[j] = centroidMetricList.get(j).getValue();
                }
            }   
        }
    }
    
    private float[] getClusterMetricMinValues(ArrayList<RhythmCluster> rhythmClusters){
        float[] maxClusterMetricValues = new float[numMetrics];
        
        for (int i = 0; i < numMetrics; i++){
            maxClusterMetricValues[i] = Float.MIN_VALUE;
        }
        for(int i = 0; i< rhythmClusters.size(); i++){
            ArrayList<Metric> centroidMetricList = rhythmClusters.get(i).getCentroid().getMetrics();
            for(int j = 0; j < numMetrics; j++){
                if(centroidMetricList.get(j).getValue() > maxClusterMetricValues[j]){          
                    maxClusterMetricValues[j] = centroidMetricList.get(j).getValue();
                }
            }   
        }
        
        return maxClusterMetricValues;
    }
    
    public void fitToFourOnTheFloor(){
        String fourOnTheFloor = "c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4 c4";
        MelodyPart rhythmTemplate = new MelodyPart(fourOnTheFloor);
        int thePitch;
        int responseIterator = response.getFirstIndex();
        int rhythmTemplateIterator = rhythmTemplate.getFirstIndex();
        int nextNoteIndex;
        boolean responseHasMoreNotes = true;
        
        while(rhythmTemplate.getNote(rhythmTemplateIterator) != null){//loop through all notes in the rhythm template
            if (response.getNote(responseIterator) == null){
                responseHasMoreNotes = false;
            }
            if(responseHasMoreNotes){
                Note n = response.getCurrentNote(responseIterator);
                if (n.isRest()){
                    responseIterator = response.getNextIndex(responseIterator);//increment responseIterator
                    if (response.getNote(responseIterator) == null){
                        responseHasMoreNotes = false;
                    }  
                }
                
            }
            
            if (responseHasMoreNotes){
                Note n = response.getCurrentNote(responseIterator);
                if(!rhythmTemplate.getCurrentNote(rhythmTemplateIterator).isRest()){
                    rhythmTemplate.getCurrentNote(rhythmTemplateIterator).setPitch(n.getPitch());
                    responseIterator = response.getNextIndex(responseIterator);
                }
                
            }else{
                rhythmTemplate.getCurrentNote(rhythmTemplateIterator).setPitch(-1);
            }
 
            rhythmTemplateIterator = rhythmTemplate.getNextIndex(rhythmTemplateIterator);//get the next note in the rhythm template
        }
        
        //System.out.println("rhythmTemplate is now "+rhythmTemplate);
        response = rhythmTemplate;
    }
    
    public String getRhythmStringFromPolylist(Polylist p){
        String rhythmString = "";
        while(!p.isEmpty()){
            Polylist notePolylist = (Polylist) p.first();
            if(notePolylist.first().equals("X")){
                rhythmString += "c";
            }else{
                rhythmString += notePolylist.first();
            }
            rhythmString+=notePolylist.second() + " ";
            p = p.rest();
            
        }
        
        return rhythmString;
    }
    
    public void fitToRhythm(MelodyPart rhythmTemplate){
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
    }
    public void setMusician(Transform musician)
    {
        this.musician = musician;
    }
    
    public void showGrammarSolo(){
        notate.addChorus(grammarSolo);
    }
    
    //Flatten a solo to the default resolution
    //currently flatten to every beat
    public void flattenSolo(){
        flattenSolo(Constants.QUARTER);
    }

    //Flatten solo to a specified resolution
    //Resolutions specified by strings must be converted
    //Examples:
    //beatFinder.EVERY_BEAT
    //beatFinder.MEASURE_LENGTH
    //beatFinder.STRONG_BEATS
    public void flattenSolo(String resolution){
        flattenSolo(beatFinder.getResolution(resolution));
    }

    //Flatten solo to specified resolution
    //(flattens based on response chords)
    //Examples:
    //Constants.WHOLE
    //Constants.HALF
    public void flattenSolo(int resolution){
        response = flattener.flattenByResolution(response, responseChords, resolution, start, stop, false);
    }

    //STEP 2 - modify the flattened solo (inversion/retrograde/retrograde inversion/no change)
    
    //Modify the solo in a simple way
    //i.e. invert, reverse, transpose
    public void modifySolo(){
        int options = 4;
        Random r = new Random();
        int selection = r.nextInt(options);
        switch(selection){
            case 0:
                //inversion
                invertSolo();
                break;
            case 1:
                //retrograde
                reverseSolo();
                break;
            case 2:
                //retrograde inversion
                invertSolo();
                reverseSolo();
                break;
            case 3:
                //original
                break;
        }
    }
    
    public void genSolo(){
        response = generateFromGrammar(tradeLength, responseChords);
        //response = notate.genSolo(nextSection, nextSection + tradeLength);
        //System.out.println("MELODY " + mp);
    }
    
    /**
     * Generates solo based on a {@link Polylist} implementation of a Grammar
     * @param grammar Grammar to generate solo from
     */
    public void genSolo(Polylist grammar){
        response = generateFromGrammar(tradeLength, responseChords, notate, grammar);
    }
    
    /**
     * Generates Motif solo based on a {@link Polylist} implementation of a Grammar
     * @param grammar Grammar to generate solo from
     */
    public void genMotifSolo(Grammar grammar){
        response = generateFromMotifGrammar(tradeLength, responseChords, notate, grammar);
//        System.out.println("Correct genSolo called!");

    }
    
    
    
    public static MelodyPart generateFromGrammar(int length, ChordPart chords, Notate notate, Polylist grammar){
        Grammar gram = new Grammar(grammar);
        MelodyPart generated = notate.getLickgenFrame().fillMelody(BEAT, gram.run(0, length, notate, false, false, -1), chords, 0);
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(generated, 0, generated.size()-1, chords, false, false, true, true, true, true);
        cmd.execute();
        return generated;
    }
    
    /**
     * Generates Motif solo based on a {@link Polylist} implementation of a Grammar
     * @param length length of solo to fill
     * @param chords chords over which to solo
     * @param notate {@link Notate}
     * @param grammar Grammar to base solo off of
     * @return A {@link MelodyPart} containing the solo
     */
    public static MelodyPart generateFromMotifGrammar(int length, ChordPart chords, Notate notate, Grammar grammar){
        MelodyPart generated = notate.getLickgenFrame().fillMelody(BEAT, grammar.run(0, length, notate, false, false, -1), chords, 0);
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(generated, 0, generated.size()-1, chords, false, false, true, true, true, true);
        cmd.execute();
//        System.out.println("Correct generateFromGrammar called!");
        return generated;
    }
    
    
    private MelodyPart generateFromGrammar(int length, ChordPart chords){
        return generateFromGrammar(length, chords, notate);
    }
    
    public  static MelodyPart generateFromGrammar(int length, ChordPart chords, Notate notate){
        System.out.println("Grammar File Name: "+notate.getGrammarFileName());
        Grammar gram = new Grammar(notate.getGrammarFileName());
        
        //TODO: TEST STUFF REMOVE!!! 
        //String testGramFileName = "/Users/cssummer17/impro-visor-version-9.1-files/grammars/LukasSucks.grammar";         
        //gram = new Grammar(testGramFileName);        
   
        System.out.println("Grammar itself is: "+ gram.toString());
        MelodyPart generated = notate.getLickgenFrame().fillMelody(BEAT, gram.run(0, length, notate, false, false, -1), chords, 0);
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(generated, 0, generated.size()-1, chords, false, false, true, true, true, true);
        cmd.execute();
        return generated;
    }
    
    public MelodyPart extractFromGrammarSolo(int startSlot, int slotLength){
        MelodyPart mp = 
         grammarSolo.extract(startSlot, startSlot + slotLength - 1, true, true);
        return mp;
    }

    public void abstractify() {
        String abstractMelody = AbstractMelodyExtractor.getAbstractMelody(
                0,
                responseChords.getSize() / BEAT,
                false,
                false,
                response,
                soloChords
        );
        response = notate.getLickgenFrame().fillAndReturnMelodyFromText(abstractMelody, responseChords);
    }
    
    /**
     * Returns the abstract melody for the MelodyPart.
     * @param mp MelodyPart
     * @return The abstract melody version of the MelodyPart
     */
    public String abstractify(MelodyPart mp){
        String abstractMelody = AbstractMelodyExtractor.getAbstractMelody(
                start,
                mp.getBars(),
                false,
                true,
                mp,
                getResponseChords().extract(mp.getFirstIndex(), mp.getEndTime())
        );
        
        
        
       
        return abstractMelody;
    
    
    }
 


    public void lookupAndPlay() {
        MelodyPart[] parts = chopResponse();
        MelodyPart finalMelody = new MelodyPart();
        for (MelodyPart mp: parts ) {
            phrases.addPhrase(mp);
            MelodyPart nextPhrase = phrases.getNextPhrase(mp);
            if (nextPhrase == null) {
                nextPhrase = mp;
            }
            ArrayList<Note> notes = nextPhrase.getNoteList();
            for(Note n : notes){
                finalMelody.addNote(n);
            }
        }
        response = finalMelody;
        rectifySolo();
        mergeRepeatedPitches();
    }
    
    public MelodyPart userRhythm(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        return mash(choppedGrammar, choppedResponse);
        
        //System.out.println("User chopped: " + Arrays.toString(choppedResponse));
        //System.out.println("Computer chopped: " + Arrays.toString(choppedGrammar));
    }
    
    public MelodyPart userMelody(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        return mash(choppedResponse, choppedGrammar);
        
        //System.out.println("User chopped: " + Arrays.toString(choppedResponse));
        //System.out.println("Computer chopped: " + Arrays.toString(choppedGrammar));
    }
    
    public MelodyPart swapMelodyRhythm(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        LinkedList<MelodyPart[]> swappedAndChopped = randomSwap(choppedResponse, choppedGrammar);
        
        MelodyPart[] melody = swappedAndChopped.getFirst();
        MelodyPart[] rhythm = swappedAndChopped.getLast();
                
        return mash(melody, rhythm);
    }
    
    public LinkedList<MelodyPart[]> randomSwap(MelodyPart[] part1, MelodyPart[] part2) {
        Random generator = new Random();
        for (int i = 0; i < part1.length; i++) {
            int isSwap = generator.nextInt(2);
            if (isSwap == 1) {
                MelodyPart savePart1 = part1[i].copy();
                MelodyPart savePart2 = part2[i].copy();
                part1[i] = savePart2;
                part2[i] = savePart1;
            }
        }
        LinkedList<MelodyPart[]> swapped = new LinkedList();
        swapped.set(0, part1);
        swapped.set(1, part2);
        return swapped;
    }
    
    public MelodyPart mash(MelodyPart[] melody, MelodyPart[] rhythm){
        Vector<Integer>[] responseMelodies = extractMelodies(melody);
        MelodyPart mp = new MelodyPart();
        for(int i = 0; i < rhythm.length; i++){
            if (!responseMelodies[i].isEmpty()){
                Vector<Integer> thisMelody = responseMelodies[i];
                LinkedList<Note> thisRhythm = rhythm[i].getNotes();
                int nonRestIndex = 0;
                for(int j = 0; j < thisRhythm.size(); j++){
                    Note n = thisRhythm.get(j);
                    if(n.isRest());
                    else{
                        int melodyiIndex = nonRestIndex % thisMelody.size();
                        n.setPitch(thisMelody.get(melodyiIndex));
                        nonRestIndex++;
                    }
                    mp.addNote(n);
                }
            }
        }
        return mp;
    }
    
    public MelodyPart[] chopResponse(){
        return chopResponse(response, 0);
    }
    
    /**
     * Chops the melody into parts of {@code windowSize} measures starting at {@code start}
     * @param mp MelodyPart to break up
     * @param start Where to start the chopping
     * @par/am windowSize Size of pieces (desired number of measures per chunk)
     * @return an array of MelodyParts
     */
    public MelodyPart[] chopResponse(MelodyPart mp, int start, int windowSize){
        int beat = (Constants.WHOLE / metre[1]);
        int measure = (metre[0] * beat);
        int numMeasures = (response.getSize() / measure);
        MelodyPart[] mpa = new MelodyPart[numMeasures];
        for(int i = 0; i < numMeasures; i += windowSize){
            int measureStartSlot = (start + (i * measure));
            int measureEndSlot = measureStartSlot + (measure*windowSize) - 1;
            mpa[i] = mp.extract(measureStartSlot, measureEndSlot, true, true);
        }
//        System.out.println("chopped:");
//        for( MelodyPart p: mpa )
//          {
//            System.out.println("    " + p);
//          }
        return mpa;
    }

    public MelodyPart[] chopResponse(MelodyPart mp, int start){
        return chopResponse(mp, start, 1);
    }
    
    public Vector<Integer>[] extractMelodies(MelodyPart[] mpa) {
        int beat = (Constants.WHOLE / metre[1]);
        int measure = (metre[0] * beat);
        int numMeasures = (response.getSize() / measure);
        Vector<Integer>[] melodies;
        melodies = new Vector[numMeasures];
        for (int i = 0; i < numMeasures; i++) {
            Vector<Integer> newMelody = new Vector();
            ArrayList<Note> notes = mpa[i].getNoteList();
            for (Note n : notes) {
                if (!n.isRest()) {
                    newMelody.add(n.getPitch());
                }
            }
            melodies[i] = newMelody;
        }
        return melodies;
    }

    public void rhythmicGuideLine(boolean generatedContour){
        int direction = direction();
        int [] limits = limits(direction);
        //System.out.println(limits[0]+"\t"+limits[1]);

        ChordPart rhythmicChords = rhythmicChords();
        String startDegree;
        if(response.getFirstNote()!=null){
            Note lastNote = response.getLastNote();
            Chord firstChord = rhythmicChords.getChord(0);
            NoteChordPair ncp = new NoteChordPair(lastNote, firstChord);
            startDegree = ncp.getRelativePitch();
        }else{
            startDegree = "3";
        }
        
        //                                                  chords direction / deg1 deg2 altern / low high / maxdur mix allowColor / alwaysDisallowSame
        //one line starting on three no preferred direction allow color tones. Use range limits of user solo.
        
        GuideLineGenerator generator;
        if(!generatedContour){
            generator = new GuideLineGenerator(
                                            rhythmicChords, direction, 
                                            startDegree, "", true, 
                                            limits[0], limits[1], 
                                            GuideLineGenerator.NOPREFERENCE, false, true,
                                            true, "");
        
        }else{
            int intervals = rhythmicChords.getChords().size()-1;
            //System.out.println("number of intervals: "+(intervals));
            int log2OfIntervals = logBase2(intervals);
            //round up to nearest power of 2
            if(Math.pow(2, log2OfIntervals)<intervals){
                log2OfIntervals++;
            }
            //System.out.println("log base 2 of intervals: "+log2OfIntervals);
            ContourGenerator gen = new ContourGenerator(log2OfIntervals);
            generator = new GuideLineGenerator(
                                            rhythmicChords, direction, 
                                            startDegree, "", true, 
                                            Constants.A0, Constants.C8, 
                                            GuideLineGenerator.NOPREFERENCE, false, true,
                                            true, gen.contour());
        }
        
        MelodyPart noRestsGTL = generator.makeGuideLine();
        
        //add rests back in
        MelodyPart finalResult = new MelodyPart();
        
        ArrayList<Note> yesRests = response.getNoteList();
        ArrayList<Note> noRests = noRestsGTL.getNoteList();
        
        for(int y = 0, n = 0; n<noRests.size()||y<yesRests.size(); y++){
            boolean yListDone = y>=yesRests.size();
            //boolean nListDone = y>=noRests.size();
            if(yListDone || !yesRests.get(y).isRest()){
                finalResult.addNote(noRests.get(n).copy());
                n++;
            }else{
                finalResult.addNote(yesRests.get(y).copy());
            }
        }
        
        response = finalResult;
    }
    
    public static int logBase2(int n){
        return (int)(Math.log(n)/Math.log(2));
    }
    
    /**
     * Returns the opposite direction of the user solo
     * @return 
     */
    public int direction(){
        if(response.getFirstNote()==null){
            return GuideLineGenerator.NOPREFERENCE;
        }
        int first = response.getFirstNote().getPitch();
        int last = response.getLastNote().getPitch();
        int diff = last-first;
        if(diff>0){
            return GuideLineGenerator.DESCENDING;
        }else if(diff<0){
            return GuideLineGenerator.ASCENDING;
        }else{
            return GuideLineGenerator.NOPREFERENCE;
        }
    }
    
    public ChordPart rhythmicChords(){
        ChordPart rhythmic = new ChordPart();
        int duration;
        for(int i = 0; i<response.getSize(); i += duration){
            Note n = response.getNote(i);
            duration = n.getRhythmValue();
            Chord copy = responseChords.getCurrentChord(i).copy();
            copy.setRhythmValue(duration);
            //DON'T ADD THE CHORD IF THE NOTE IT GOES WITH IS A REST
            //THIS PREVENTS THE GTL FROM JUMPING AROUND A LOT
            //ADD RESTS BACK AT THE END
            if(!n.isRest()){
                rhythmic.addChord(copy);
            }
        }
        return rhythmic;
    }
    
    public int [] limits(int direction){
        int [] limits = new int[2];
        ArrayList <Note> notes = response.getNoteList();
        if(notes.isEmpty()){
            //default range limits
            limits[0] = Constants.C4;
            limits[1] = Constants.C5;
        }else{
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for(Note n : notes){
                int pitch = n.getPitch();
                if(!n.isRest()){
                    if(pitch<min){
                        min = pitch;
                    }
                    if(pitch>max){
                        max = pitch;
                    }
                }

            }
            //guide tone lines need a range of at least an octave
            //extend range in a certain direction based on direction of response
            int range = max-min;
            if(range<Constants.OCTAVE){
                switch(direction){
                    case GuideLineGenerator.ASCENDING:
                        max = min+Constants.OCTAVE;
                        break;
                    case GuideLineGenerator.DESCENDING:
                        min = max-Constants.OCTAVE;
                        break;
                    case GuideLineGenerator.NOPREFERENCE:
                        int extendBy = Constants.OCTAVE-range;
                        max += extendBy/2;
                        min += extendBy/2;
                        if(extendBy % 2 != 0){
                            max += 1;
                        }
                        break;
                }
                
            }
            limits[0] = min;
            limits[1] = max;
        }
        
        return limits;
    }

    //invert the solo
    public void invertSolo(){
        InvertCommand cmd = new InvertCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //reverse the solo
    public void reverseSolo(){
        ReverseCommand cmd = new ReverseCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //STEP 3 - transform/embellish the solo (in the style of a particular musician)
    
    //transform solo using specified transform
    //(in gui, select this from a drop down menu)
    public void transformSolo(Transform musician){
        response = musician.applySubstitutionsToMelodyPart(response, responseChords, true);
    }
    
    //STEP 4 - rectify the solo to chord/color tones
    
    //rectify solo to response chords
    //allows chord, color, and approach tones
    //allows repeat pitches
    public void rectifySolo(boolean onlyChordTones){
        //System.out.println("\nbefore: " + response);
        RectifyPitchesCommand cmd;
        boolean chord, color, approach;
        chord = true;
        if(onlyChordTones){
            color = false;
            approach = false;
        }else{
            color = true;
            approach = true;
        }
        cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseChords, false, false, chord, color, approach, true);
        cmd.execute();
        //System.out.println("after: " + response);
    }
    
    public void rectifySolo(){
        rectifySolo(false);
    }

    public void mergeRepeatedPitches()
    {
        response = response.removeRepeatedNotes();
    }
    //STEP 5 - retreive the response
    
    //retreive response
    // 5/23/2016 Bob Keller added removeRepeatedNotes(), which is intended
    // to make the result sound more realistic. In the future, this
    // can be modified, to remove notes selectively according to
    // certain restrictions.
    public MelodyPart getResponse(){
        mergeRepeatedPitches();
        return response;
    }
   
    //ALL THE STEPS TOGETHER
    public MelodyPart musicianResponse(){
        //STEP 1
        flattenSolo();
        //STEP 2
        //modifySolo();
        //rectifySolo(ONLY_CHORD_TONES);
        //STEP 3
        transformSolo(musician);
        //STEP 4
        rectifySolo(ALL_TONES);
        //STEP 5
        return getResponse();
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
}
