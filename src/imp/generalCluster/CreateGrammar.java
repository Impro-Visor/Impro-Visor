/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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
package imp.generalCluster;

import static imp.Constants.BEAT;
import imp.ImproVisor;
import imp.cluster.motif.CreateMotifGrammar;
import imp.data.ChordPart;
import imp.data.ContourData;
import imp.data.Duration;
import imp.data.MelodyRhythmCount;
import imp.data.Note;
import imp.data.Unit;
import imp.generalCluster.metrics.AverageMaxSlope;
import imp.generalCluster.metrics.Consonance;
import imp.generalCluster.metrics.ExactStart;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.MetricListFactories.MetricListFactory;
import imp.generalCluster.metrics.NoteCount;
import imp.generalCluster.metrics.NumSegments;
import imp.generalCluster.metrics.RestDuration;
import imp.generalCluster.metrics.StartBeat;
import imp.gui.Notate;
import imp.trading.TradingResponseInfo;
import imp.util.ErrorLog;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import polya.Formatting;
import polya.Polylist;

/**
 *
 * @author Jon Gillick, Kevin Tang
 * Includes additions by Mark Heimann to support processing of rules with expanded information
 * and additions by Joseph Yaconelli to incorporate motif grammar learning
 */

public class CreateGrammar implements imp.Constants {

    // switch to use motif grammar or not.
    private static boolean MOTIF_GRAMMAR_ON = true;
    // properties to set for motif grammar
    private static int MOTIF_WINDOW_SIZE = 4;
    private static double MOTIFNESS      = 0.7;
    
    
    public static final int SEG_LENGTH = 3;  //length of the word SEG
    private static final int XNOTATIONDELIMITER_LENGTH = 11;
    private static final int BRICKTYPEDELIMITER_LENGTH = 12; //"(Brick-type ".length
    public static double MIN_PROB = 5.0; //include phrase transitions of greater than this probability
    public static int REPS_PER_CLUSTER = 5;
    private static DataPoint averagePoint; //keeps track of average point in cluster
    private static DecimalFormat df = new DecimalFormat("0.00");
    private static int numMetrics = 1;
    private static boolean createClusterFile;
    
    private static int clusterWindowSize;
    private static Double[] maxMetricValues;
    private static Double[] minMetricValues;

    
    public static MetricListFactory metricListFactory;



    /* Takes a grammar file containing productions extracted from leadsheets
     * calls clustering algorithm on the productions and writes the results
     * into the grammar
     */
    public static void create(ChordPart chordProg, 
                              StringWriter inWriter,
                              String outFile,
                              int repsPerCluster, 
                              boolean Markov, 
                              int markovLength, 
                              boolean useRelative, 
                              boolean useAbstract,
                              Notate notate,
                              MetricListFactory mlf) throws IOException 
      {
          
      
        metricListFactory = mlf;
        notate.setLickGenStatus("Writing grammar rules: " + outFile);
        //make initial calls to read from the file
        Polylist[] rules = getRulesFromWriter(inWriter);
//        for (int i=0; i<rules.length; i++){
//            System.out.println(rules[i]);
//        }
        String[] ruleStrings = getRuleStringsFromWriter(inWriter);
        //initialize vectors
        Vector<DataPoint> dataPoints = new Vector<DataPoint>();

       
        numMetrics = metricListFactory.getNumMetrics();
        
        maxMetricValues = new Double[numMetrics];
        minMetricValues = new Double[numMetrics];
        Arrays.fill(maxMetricValues, Double.MIN_VALUE);
        Arrays.fill(minMetricValues, Double.MAX_VALUE);
       
        //put data into vectors
        for (int i = 0; i < rules.length; i++) {
            DataPoint temp = processRule(rules[i], ruleStrings[i], Integer.toString(i), metricListFactory);
            int segLength = temp.getSegLength();
            if(createClusterFile && segLength != clusterWindowSize){
//                System.out.println("mySegLength: " + segLength + ", targetSegLength: " + clusterWindowSize + ", normalizingRatio: " +  clusterWindowSize / segLength);
                temp.scaleMetrics( (clusterWindowSize) / segLength);
             }
             updateGlobalMetricMaxMin(temp);
            if (temp.getRestPercent() < 1.0){
                dataPoints.add(temp);
            }
        }
        
        notate.setLickGenStatus("Wrote " + rules.length + " grammar rules.");
        
        double[] averages = calcAverage(dataPoints);
        //System.out.println("datapoints after calc Average: "+dataPoints.toString());
        //averageVector(dataPoints, averages);

        normalizeDatapoints(dataPoints, maxMetricValues, minMetricValues);

        if (repsPerCluster>dataPoints.size()){
            repsPerCluster = 1;
        }
        Cluster[] clusters = getClusters(dataPoints, dataPoints.size() / repsPerCluster, mlf);
        /**@Todo
         * check if you want to create cluster file and pass in desired name
         */
        if(createClusterFile){
            String fileName = getClusterOutputFile(outFile);//also makes file&directory if it does not yet exist
            fileName = ImproVisor.getRhythmClusterDirectory().toString() + "/" + fileName;
            selectiveClusterToFile(clusters, fileName, (new ArrayList<String>()), clusterWindowSize,
                    maxMetricValues, minMetricValues);
        }
            
        
        
        
        // use clusters to create grammar for global motif structure
        /**
         * TODO: change "magic numbers" to user input
         */
        
        if(MOTIF_GRAMMAR_ON){
        
            int magicWindowSizeSlide = MOTIF_WINDOW_SIZE;
            double magicMotifness    = MOTIFNESS;

            
            //System.out.println("Motifness:" + magicMotifness + "\tWindow Size: " + magicWindowSizeSlide);
            
            
            Polylist[] motifGrammar = CreateMotifGrammar.create(repsPerCluster,
                    clusters, dataPoints, magicWindowSizeSlide, magicWindowSizeSlide, magicMotifness);
        
            CreateMotifGrammar.writeMotifGrammar(motifGrammar, outFile);
        }
        
        
        //create grammar with Markov chains and create soloist file
        /**@Todo rhythms used to break this but now it doesn't, find out why*/
        if (Markov) {

            //get the cluster orders and set up the ngrams and the reverse ngrams
            Vector<Vector<DataPoint>> orders = getClusterOrder(clusters, dataPoints);

            Vector<NGram> ngrams = getChains(orders, clusters, 2); //use 2 instead of markovlength for outlines
            Vector<NGram> reverseNGrams = getReverse(ngrams);

            ngrams = mergeDuplicateChains(ngrams);
            reverseNGrams = mergeDuplicateChains(reverseNGrams);

            setNGramProbabilities(ngrams);
            setNGramProbabilities(reverseNGrams);
            

            Vector<NGramWithTransitions> transitions = getTransitions(ngrams, clusters);
            Vector<NGramWithTransitions> reverseTransitions = getTransitions(reverseNGrams, clusters);

            
            //get the sets of similar clusters
            Vector<ClusterSet> clusterSets = getClusterSets(clusters);
            //get the outlines
            Vector<Vector<ClusterSet>> outlines = getOutlines(orders, clusters, clusterSets);

            //store memory in file as serialized objectsFile grammarOut = new File(outFile);
            String soloistFileName = outFile.replace(".grammar", ".soloist");

            File soloistFile = new File(soloistFileName);

           // notate.setLickGenStatus("Creating .soloist File with " + outlines.size() + " outlines: " + soloistFileName);

            createSoloistFile(dataPoints,
                    clusters,
                    clusterSets,
                    transitions,
                    reverseTransitions,
                    outlines,
                    soloistFile);
            

            //write grammar
            ngrams = getChains(orders, clusters, markovLength);
            DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
            Vector<float[]> chains = getChainProbabilitiesForGrammar(ngrams);


            writeGrammarWithChains(ngrams,
                    chains,
                    reps,
                    clusters,
                    outFile,
                    chordProg,
                    useRelative,
                    useAbstract);

          //  notate.setLickGenStatus("Done creating .soloist File with " + outlines.size() + " outlines: " + soloistFileName);

        } //otherwise just add productions to the original grammar
        else {
            DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
            writeClusterReps(reps, clusters, outFile);
        }

    }
    
   

    
    public static void setCreateClusterFileFlag(boolean b){
        createClusterFile = b;
    }
    
    public static void setClusterWindowSize(int size){
        clusterWindowSize = size;
    }
    
    public static String getClusterOutputFile(String outFile){
        String[] outFileParts = outFile.split("/");
        String clusterOutFile = "";
        
        String fileName = outFileParts[outFileParts.length - 1];
       
        String[] fileNameParts = fileName.split("\\.");
        
        fileName = fileNameParts[0];//fileName with .grammar removed
        fileName = fileName + ".cluster";
       
        return fileName;
    }
    
    /**Writes clusters to file,each cluster being a polylist of a centroid and a rhythmlist
     * 
     * @param clusters - array of clusters
     * @param outFile - file to write to
     * @param windowSize - size of window used to "learn" the cluster, used for normalization
     * @throws IOException if there's a problem with outFile
     */
    public static void clusterToFile(Cluster[] clusters, String outFile, int windowSize) throws IOException{
        Writer writer;
        
        
        
        //System.out.println("outfile: " + outFile);
        
        //System.out.println("fileName: " + outFile);
        Polylist windowSizeMetaDataPL = Polylist.list("windowSize", windowSize);

        try {  
            writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(windowSizeMetaDataPL.toString() + "\n");
            for(int i = 0; i < clusters.length; i++){
            //String clusterString = "(cluster "+ clusters[i].getName() + "\n";

                Cluster cluster = clusters[i];                
                Centroid centroid = cluster.getCentroid();
 
                Polylist clusterPL = Polylist.list("cluster", Polylist.list("name", "cluster"+i), getCentroidPolylist(centroid), cluster.getClusterMembersPolylist());
             
                writer.write(clusterPL.toString());
            }
            
            writer.close();
            
            //System.out.println("\n\n\n**********writer is closed****************\n\n\n");
            
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            System.out.println("Unable to write to file " + outFile);
            Logger.getLogger(CreateGrammar.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
       
        
    }
    
    /**Write the clusters to the file, excluding rhythms specified in the excludeList
     * 
     * @param clusters - array of clusters to write
     * @param outFile - file to write them to
     * @param excludeList - list of rhythms to exclude, represented as polylists in "rhythm" format
     * @param windowSize - size of the windows used to learn this cluster file,
     * written as metadata used for normalization 
     * @throws IOException - if theres a problem writing to outFile
     */
    public static void selectiveClusterToFile(
                                            Cluster[] clusters, 
                                            String outFile, 
                                            ArrayList<String> excludeList, 
                                            int windowSize, 
                                            Double[] maxMetricVals, 
                                            Double[] minMetricVals) throws IOException{
//        System.out.println("in clelective cluster to file");
        Writer writer;
        
        Polylist windowSizeMetaDataPL = Polylist.list("windowSize", windowSize);
        Polylist maxMetricValuesPL = Polylist.list("maxMetricValues", Polylist.PolylistFromArray(maxMetricVals));
        Polylist minMetricValuesPL = Polylist.list("minMetricValues", Polylist.PolylistFromArray(minMetricVals));
//        //Polylist averagesMetaDataPL = Polylist.list("averages", Polylist.PolylistFromArray(averages));
        //Polylist totalNumDataPointsPL = Polylist.list("totalNumDataPoints", totalNumDataPoints);
//        System.out.println("maxMetricValues: " + maxMetricValuesPL.toString());
//        System.out.println("minMetricValues: " + minMetricValuesPL.toString());
        
        
        try {  
            writer = new BufferedWriter(new FileWriter(outFile));        
            writer.write(windowSizeMetaDataPL.toString()+ "\n");
            writer.write(maxMetricValuesPL.toString()+ "\n");
            writer.write(minMetricValuesPL.toString()+ "\n");

            for(int i = 0; i < clusters.length; i++){
            //String clusterString = "(cluster "+ clusters[i].getName() + "\n";

                Cluster cluster = clusters[i];                
                Centroid centroid = cluster.getCentroid();
 
                Polylist clusterPL = Polylist.list("cluster", Polylist.list("name", "cluster"+i), getCentroidPolylist(centroid), 
                        cluster.selectivelyGetClusterMembersRuleStringsPolylist(excludeList));
             
                writer.write(Formatting.prettyFormat(clusterPL));
            }
            
            writer.close();
            
            //System.out.println("\n\n\n**********writer is closed****************\n\n\n");
            
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            System.out.println("Unable to write to file " + outFile);
            Logger.getLogger(CreateGrammar.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
       
        
    }
    
    /**Make a polylist out of the centroid. So we can write it to a file.
     * 
     * @param c - centroid to write
     * @return Polylist representation of that centroid
     */
    public static Polylist getCentroidPolylist(Centroid c){
        Metric[] metricList = c.getMetrics();
        Polylist centroidPL = Polylist.list("centroid");
        for(int i = 0; i < metricList.length; i++){
            Metric m = metricList[i];
            Polylist iMetric = Polylist.list(m.getName(), m.getValue());
            centroidPL = centroidPL.addToEnd(iMetric);
        }

        return centroidPL;
    }
    
    
    
    

    public static Vector<NGram> getChains(Vector<Vector<DataPoint>> orders, 
                                          Cluster[] clusters, 
                                          int chainLength) {
        Vector<NGram> ngrams = new Vector<NGram>();
        //last marks whether the ngram we are creating ends a chorus
        boolean last = false;
        
        int minimumSetSize = Integer.MAX_VALUE;
        for (int i = 0; i < orders.size(); i++) {
            Vector<DataPoint> currentSet = orders.get(i);
           if (currentSet.size()<minimumSetSize){
               minimumSetSize = currentSet.size();
           }
        }
        
        System.out.println("minimum set size: "+ minimumSetSize);
        if (minimumSetSize<chainLength){
            System.out.println("changing chainlength to: "+ minimumSetSize);
            chainLength = minimumSetSize;
        }
        
        
        for (int i = 0; i < orders.size(); i++) {
            Vector<DataPoint> currentSet = orders.get(i);
//            System.out.println("currentSet: "+ currentSet.toString());
//            System.out.println("currentSet's size: "+ (currentSet.size()));
//            System.out.println("chainLength: "+ ((chainLength)));
//            System.out.println("upperbound for for loop: "+ (currentSet.size() - (chainLength - 1)));
            /**@TODO: check if this is an adequate and robust fix*/
//            int upperbound = currentSet.size() - (chainLength - 1);
//            int thisChainLength;
//            if (upperbound <= 0){
//                thisChainLength = currentSet.size();
//            }else{
//                thisChainLength = chainLength;
//            }

            
            
            for (int j = 0; j < currentSet.size() - (chainLength - 1); j++) {
                int[] chain = new int[chainLength];
                for (int k = 0; k < chainLength; k++) {
                    chain[k] = currentSet.get(j + k).getCluster().getNumber();
                }
                if (j == currentSet.size() - (chainLength)) {
                    last = true;
                }
                ngrams.add(new NGram(chain, last));
                last = false;
            }
        }

        return ngrams;
    }

    /* Takes a vector of NGrams, removes duplicates and increments the
     * counters in the remaining copy
     * Returns the vector */
    public static Vector<NGram> mergeDuplicateChains(Vector<NGram> ngrams) {
        for (int i = 0; i < ngrams.size(); i++) {
            NGram current = ngrams.get(i);
            for (int j = i + 1; j < ngrams.size(); j++) {
                NGram other = ngrams.get(j);
                if (current.equals(other)) {
                    current.addAppearance();
                    ngrams.removeElementAt(j);
                    j--;
                }
            }
        }

        return ngrams;
    }

    //sets the probability fields within the ngrams by checking which ngrams
    //are equal until the final state
    public static void setNGramProbabilities(Vector<NGram> ngrams) {
        for (int i = 0; i < ngrams.size(); i++) {
            NGram current = ngrams.get(i);
            int appearancesUpToLast = 0;
            for (int j = 0; j < ngrams.size(); j++) {
                if (current.equalsUpToLast(ngrams.get(j))) {
                    appearancesUpToLast += ngrams.get(j).getNumAppearances();
                }
            }
            current.setAppearancesUpToLast(appearancesUpToLast);
            current.setProbability();
        }
    }

    public static void setMotifGrammarUse(boolean status){
        MOTIF_GRAMMAR_ON = status;
        //System.out.println("Motif Grammar use set to: " + MOTIF_GRAMMAR_ON);
    }
    
    public static void setMotifness(float m){
        MOTIFNESS = m;
        //System.out.printf("Motifness set to: %1.2f\n", MOTIFNESS);

    }
    
    public static void setMotifWindowSizeAndSlide(int sizeAndSlide){
        MOTIF_WINDOW_SIZE = sizeAndSlide;
        //System.out.println("Window size and slide set to: " +  MOTIF_WINDOW_SIZE);
    }
    
    public static Vector<float[]> getChainProbabilitiesForGrammar(Vector<NGram> ngrams) {
        for (int i = 0; i < ngrams.size(); i++) {
            NGram current = ngrams.get(i);
            int appearancesUpToLast = 0;
            for (int j = 0; j < ngrams.size(); j++) {
                if (current.equalsUpToLast(ngrams.get(j))) {
                    appearancesUpToLast += ngrams.get(j).getNumAppearances();
                }
            }
            current.setAppearancesUpToLast(appearancesUpToLast);
            current.setProbability();
        }

        //return float arrays to pass to writeGrammarWithChains
        //last 3 elements of arrays are numAppearances, probability, and boolean for ender
        //if last element is -1, it is an ender, otherwise it should be 0
        //TODO: change writeGrammarWithChains to take n-gram objects
        //and eliminate this method

        Vector<float[]> chains = new Vector<float[]>();
        for (int i = 0; i < ngrams.size(); i++) {
            NGram current = ngrams.get(i);
            int[] order = current.getChain();
            float[] chain = new float[order.length + 3];
            //copy chain over
            for (int j = 0; j < order.length; j++) {
                chain[j] = (float) order[j];
            }
            //set numappearances and prob
            chain[chain.length - 3] = (float) current.getNumAppearances();
            chain[chain.length - 2] = current.getProbability() * 100;
            if (current.isEnder()) {
                chain[chain.length - 1] = -1;
            } else {
                chain[chain.length - 1] = 0;
            }
            chains.add(chain);
        }
        /**@TODO figure out why this doesn't work*/
        Collections.sort((List) chains, new ChainComparer());

        return chains;
    }
    private static void printIntArray(int[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println("    `"+i+": " + a[i]);
        }
    }
    private static void printFloatArray(float[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println("    `"+i+": " + a[i]);
        }
    }

    public static void writeGrammarWithChains(Vector<NGram> ngrams, 
                                              Vector<float[]> chains, 
                                              DataPoint[] reps,
                                              Cluster[] clusters, 
                                              String outFile, 
                                              ChordPart chordProg,
                                              boolean useRelative,
                                              boolean useAbstract) {

        Vector<Integer> segLengths = new Vector<Integer>();
        for (int i = 0; i < reps.length; i++) {
            Integer length = new Integer(reps[i].getSegLength());
            if (!segLengths.contains(length)) {
                segLengths.add(length);
            }
        }

        int chainLength = chains.get(0).length - 3;

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile, true));

            //get total data points
            float totalPoints = 0;
            for (int i = 0; i < clusters.length; i++) {
                totalPoints += clusters[i].getNumDataPoints();
            }

            //put in start symbols for Markov chains
            for (int i = 0; i < segLengths.size(); i++) {
                String top = "";
                int counter = 0;
                for (int j = 1; j <= 64; j = (int) Math.pow(2, counter)) {
                    int dur = segLengths.get(i) * 120 * j;
                    top = top.concat("\n(rule (P Y) ((START " + j + ") (P (- Y "
                            + dur + "))) " + Math.pow(10, counter) + ")");
                    counter++;
                }

                out.write(top);
            }

            //write start symbols
            for (int j = 0; j < clusters.length; j++) {
                out.write("\n(rule (START Z) ((Cluster"
                        + j
                        + " Z)) "
                        + df.format(clusters[j].getNumDataPoints() / totalPoints)
                        + ")");
            }
            out.write("\n");

            //write base cases       
            Vector<String> addedBases = new Vector<String>();
            for (int k = 1; k < chainLength; k++) {        //k loops through chains of length up to chainLength
                for (int j = 0; j < chains.size(); j++) {       //j loops through all chains
                    float[] chain = chains.get(j);

                    String rule = "(base (Cluster";
                    for (int q = 0; q < k; q++) {
                        rule = rule.concat((new Integer((int) chain[q]).toString()));
                        if (q != k - 1) {
                            rule = rule.concat("to");
                        }
                    }
                    rule = rule.concat(" 0) ()" + " 1)\n");
                    if (!addedBases.contains(rule)) {
                        out.write(rule);
                        addedBases.add(rule);
                    }
                }
            }


            //write rules
            Vector<String> addedRules = new Vector<String>();
            for (int k = 1; k < chainLength; k++) {        //k loops through chains of length up to chainLength
                for (int j = 0; j < chains.size(); j++) {       //j loops through all chains
                    float[] chain = chains.get(j).clone();
                    NGram ngram = ngrams.get(j);

                    String rule = "(rule (Cluster";
                    for (int q = 0; q < k; q++) {
                        rule = rule.concat((new Integer((int) chain[q]).toString()));
                        if (q != k - 1) {
                            rule = rule.concat("to");
                        }
                    }
                    rule = rule.concat(" Z) " + "(Q" + new Integer((int) chain[k - 1]).toString());
                    rule = rule.concat(" (Cluster");
                    /* here we handle a special case; for example, if you are using a trigram but are only on the
                     second measure (you can only use the previous states that you have) */
                    if (k < chainLength - 1) {

                        int numOccurrences = 0;
                        int numPreviousState = 0;
                        for (int p = 0; p < chains.size(); p++) {
                            float[] tempChain = chains.get(p);
                            //check for chains matching the current one up to the first k+1 places
                            boolean match = true;
                            boolean previousStateMatch = true;
                            for (int q = 0; q < k + 1; q++) {
                                if (chain[q] != tempChain[q]) {
                                    match = false;
                                    if (q < k) {
                                        previousStateMatch = false;
                                    }
                                }
                            }
                            if (match == true) {
                                numOccurrences += tempChain[chainLength];
                            }
                            if (previousStateMatch == true) {
                                numPreviousState += tempChain[chainLength];
                            }
                        }

                        chain[chainLength + 1] = (float) 100.0 * numOccurrences / numPreviousState;

                        for (int q = 0; q < k + 1; q++) {
                            rule = rule.concat((new Integer((int) chain[q]).toString()));
                            if (q != k) {
                                rule = rule.concat("to");
                            }
                        }
                    } //here we handle the case when there are enough previous states to use the full chainlength
                    else {

                        if (chain[chainLength + 2] < 0) {
                            rule = rule.concat(Integer.toString(ngram.getLast()));
                        } else {
                            for (int q = 1; q <= k; q++) {
                                rule = rule.concat((new Integer((int) chain[q]).toString()));
                                if (chainLength > 2 && q != k) {
                                    rule = rule.concat("to");
                                }
                            }
                        }
                    }

                    rule = rule.concat(" (- Z 1))) ");
                    //rule = rule.concat(new Float(chain[chainLength + 1]).toString());
                    rule = rule.concat(df.format(chain[chainLength + 1] / 100));
                    rule = rule.concat(")\n");
                    if (!addedRules.contains(rule)) {
                        out.write(rule);
                        addedRules.add(rule);
                    }
                }
            }

            //write expansions to cluster representatives
            for (int i = 0; i < reps.length; i++) {
                String name = reps[i].getClusterName();
                float numAppearances = reps[i].getNumber();
                int clusterNumber = Integer.parseInt(name.substring(7));  //chop off the word cluster
                String rule = null;
                if (useRelative) {
                    writeRule(reps[i].getRelativePitchMelody(), clusterNumber, numAppearances, out);
                } 
                if (useAbstract) {
                    writeRule(reps[i].getObjData(), clusterNumber, numAppearances, out);
                }
                if (!(useRelative || useAbstract)) {
                    ErrorLog.log(ErrorLog.COMMENT, "No note option specified."
                                    + "Please try again using relative pitches and/or abstract melodies for windows");
                    return;
                }
            }
            out.close();

        } catch (IOException e) {
            System.out.println("IO EXCEPTION!" + e.toString());
        }
    }
    
    public static void writeRule(String rule, int clusterNumber, float numAppearances, BufferedWriter out) {
        try {
            out.write("(rule (Q"
                    + clusterNumber
                    + ")("
                    + rule
                    + ") "
                    + df.format(numAppearances / REPS_PER_CLUSTER)
                    + ")\n");          
        } catch (Exception e) {
            System.out.println("IO exception: " + e.toString());
        }
    }

    /**
     * normalize percentages of all elements in a vector
     */
    public static void normalizePercentages(Vector<float[]> transitions, Cluster[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            normalizePercentage(transitions, i);
        }
    }

    /**
     * Set percentages of one set of elements in an array to add to 100
     */
    public static void normalizePercentage(Vector<float[]> transitions, int index) {
        int indexOfPercentage = transitions.get(index).length - 1;
        float sumOfPercentages = 0;
        int num = 0;
        for (int i = 0; i < transitions.size(); i++) {
            if (transitions.get(i)[0] == index) {
                sumOfPercentages += transitions.get(i)[indexOfPercentage];
            }
        }
        for (int i = 0; i < transitions.size(); i++) {
            if (transitions.get(i)[0] == index) {
                float[] newPair = transitions.get(i);
                newPair[indexOfPercentage] = newPair[indexOfPercentage] * 100 / sumOfPercentages;
                transitions.set(i, newPair);
            }
        }
    }

    /*  Takes an array of clusters, and a vector of the phrase data in the clusters
     returns an integer array of which cluster each phrase is in in order
     */
    public static Vector<Vector<DataPoint>> getClusterOrder(Cluster[] clusters, Vector<DataPoint> phraseData) {
        Vector<Vector<DataPoint>> orders = new Vector<Vector<DataPoint>>();
        Vector<DataPoint> order = new Vector<DataPoint>();

        //set names for datapoints to be in order starting from 0
        for (int i = 0; i < phraseData.size(); i++) {
            DataPoint p = phraseData.get(i);
            p.setObjectName(Integer.toString(i));
        }

        for (int i = 0; i < phraseData.size(); i++) {
            if (phraseData.get(i).isStarter()) {
                if (!order.isEmpty()) {
                    orders.add(order);
                    order = new Vector<DataPoint>();
                }
            }
            order.add(phraseData.get(i));
        }
        //add last set
        if (!order.isEmpty()) {
            orders.add(order);
        }

        return orders;
    }

    public static Vector<Double> getSimilaritiesToHead(Vector<DataPoint> dataPoints,
            Vector<DataPoint> headData) {
        Vector<Double> values = new Vector<Double>();
        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint d = dataPoints.get(i);
            for (int j = 0; j < headData.size(); j++) {
                DataPoint h = headData.get(j);
                IndexedMelodyPart measure = d.getMelody();
                IndexedMelodyPart headMeasure = h.getMelody();
                if (measure.getIndex() == headMeasure.getIndex()) {
                    d.computeSimilarityToHead(h);
                    double sim = d.getSimilarityToHead();
                    values.add(sim);
                    break;
                }
            }
        }
        return values;
    }

    public static DataPoint processRule(Polylist rule, String ruleString, String i, MetricListFactory mlf) {
        //data variables
        boolean starter = false;
        boolean head = false;
        boolean startTied = false;
        boolean endTied = false;

        double noteCount = 0;
        double restDuration = 0;
        double averageMaxSlope = 0;
        double startBeat = -1;
        double numSegments = 0;
        double consonance;
        int segLength = Integer.parseInt(rule.first().toString().substring(SEG_LENGTH));
        //System.out.println("segLength is: "+ segLength);
        //System.out.println("rule string is: "+ruleString);
        
        
        String ruleStringCopy = ruleString;
        Polylist ruleCopy = rule;
        
        int chorusNumber = 0;
        Vector<String> chords = new Vector<String>();
        
        
        ContourData contourData = getContourData(rule);
        
       // System.out.println("num contour changes: " + contourData.getNumContourChanges());
        //System.out.println("contour array: " + contourData.getContourArray().toString());
        //we process the rule string, starting at the end and working backward
        
        //extract chord data
        int stopIndex = ruleString.length();
        if (ruleString.contains("CHORDS")) {
            stopIndex = ruleString.indexOf("CHORDS");
            String[] chordStrings = ruleString.substring(stopIndex).split(" ");
            ruleString = ruleString.substring(0, stopIndex);
            for (int j = 1; j < chordStrings.length; j++) {
                chords.add(chordStrings[j]);
            }
        }

        ruleString = removeTrailingSpaces(ruleString);

        //extract exact melody data
        if (ruleString.contains("Head")) {
            stopIndex = ruleString.indexOf("Head");
            head = true;
        } else if (ruleString.contains("Chorus")) {
            stopIndex = ruleString.indexOf("Chorus");
            String rest = ruleString.substring(stopIndex);
            int firstSpaceIndex = rest.indexOf(" ");
            chorusNumber = Integer.parseInt(ruleString.substring(stopIndex + 6, stopIndex + firstSpaceIndex));
        }

        String exactMelodyString = ruleString.substring(stopIndex, ruleString.length());
        String[] melody = exactMelodyString.split(" ");


        //create new melody part with the original melody and the start index
        IndexedMelodyPart exactMelody = new IndexedMelodyPart(melody);
        //make sure there are no merged rests that last too long
        int slots = segLength * BEAT;
        if (exactMelody.getSize() > slots) {
            exactMelody.setSize(slots);
        }

        //remove the exact melody from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);
        
        //extract brick type data
        //string "Brick-type" denotes where the brick type information is
        stopIndex = ruleString.indexOf("(Brick-type ");
        String brickType = ruleString.substring(stopIndex + BRICKTYPEDELIMITER_LENGTH, ruleString.length() - 1); //-1 to chop off closing parenthesis
        
        //remove the brick type data from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);

        //extract X notation melody data
        //string "Xnotation" denotes start of the X notation.  
        stopIndex = ruleString.indexOf("(Xnotation"); //find "Xnotation" delimiter
        String relativePitchMelodyString = ruleString.substring(stopIndex + XNOTATIONDELIMITER_LENGTH, ruleString.length() - 1);

        //remove the X notation from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);

        //determine if a measure is tied at start or end
        if (rule.last().equals("STARTTIED")) {
            startTied = true;
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.indexOf("STARTTIED") - 1).concat(" )");
        }

        //determine if a measure is tied at start or end
        if (rule.last().equals("ENDTIED")) {
            endTied = true;
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.indexOf("ENDTIED") - 1).concat(" )");
        }

        //determine if a rule is a 'song starter'
        if (rule.last().equals("STARTER")) {
            starter = true;
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.length() - 9).concat(" )");
        }

        //remove extra junk from the rule string such as "(rule (Seg 4)"
        ruleString = ruleString.substring(ruleString.indexOf("Seg") + 7, ruleString.length() - 3);
        //if the Seg number is 2 digits, this will leave an extra parenthesis at the beginning
        //so we must chop that off too if it exists
        if (ruleString.startsWith("((")) {
            ruleString = ruleString.substring(1, ruleString.length());
        }
        consonance = getConsonance(ruleString, exactMelody);

        rule = rule.rest();

        while (rule.nonEmpty()) {
            if (rule.first() instanceof Polylist) {
                Polylist inner = (Polylist) rule.first();
                //get maximum slope
                //inner is the slope polylist
                int maxslope;
                if (Math.signum(Integer.parseInt(inner.second().toString())) == -1
                        || Math.signum(Integer.parseInt(inner.third().toString())) == -1) {
                    maxslope = Integer.parseInt(inner.second().toString());
                } else {
                    maxslope = Integer.parseInt(inner.third().toString());
                }
                averageMaxSlope += Math.abs(maxslope);
                //get rid of slopes
                inner = inner.rest().rest().rest();
                //get startbeat if needed
                if (startBeat == -1) {
                    String terminal = inner.first().toString();
                    //check if first beat is rest
                    if (terminal.charAt(0) == 'R') {
                        //check if rest releases on a startbeat
                        if (Duration.getDuration(terminal.substring(1)) % 120 == 0) {
                            startBeat = 1;
                        } else {
                            startBeat = 0;
                        }
                    } else {
                        startBeat = 1;
                    }
                }
                //increment number of segments
                numSegments++;
                //loop through terminals of segments
                while (inner.nonEmpty()) {
                    String terminal = inner.first().toString();
                    if (terminal.charAt(0) != 'R') { 
                        noteCount++;
                    } else {
                        restDuration += Duration.getDuration(terminal.substring(1));
                    }
                    inner = inner.rest();
                }
            }
            rule = rule.rest();
        }

        int exactStartBeat = getStartBeat(exactMelody);
        
        ArrayList<Metric> metricList = new ArrayList<Metric>();
        
        
        double syncopation = getSyncopation(exactMelody, exactStartBeat);
        double restPercent = (double) restDuration / (segLength*BEAT);

        MelodyRhythmCount melodyRhythmCount = getMelodyRhythmCount(exactMelody, exactStartBeat);
        
        //d.addRhythmData(melodyRhythmCount, contourData, syncopation);
        
        
        Metric[] metrics = mlf.getNewMetricList();
        
        
        
//        System.out.println("\n\n\n\nWHHHHAAT?!?!?!?!?!About to call compute on all metrics");
        for(int index = 0; index < metrics.length; index++){
//            System.out.println("metrics["+index+"] before calling compute: " + metrics[index]);
            metrics[index].compute(ruleStringCopy, exactMelody, ruleCopy);
        }
        
        
        
        
        
//        metricList.add(exactStartMetric);
//       metricList.add(consonanceMetric);
//        metricList.add(noteCountMetric);
//        metricList.add(restDurationMetric);
//        metricList.add(avgMaxSlopeDividedByNumSegmentsMetric);
//        metricList.add(startBeatMetric);
//        metricList.add(numSegmentsMetric);
        //metricList.add(syncopationMetric);
        //metricList.add(diversityIndexMetric);
        //metricList.add(contourDataMetric);
        //metricList.add(longestNoteLengthMetric);
        //metricList.add(longestRestLengthMetric);
        
        
        //numMetrics = metricList.size();
        
        
        
        //System.out.println("rest percentage = "+ restPercent);
        //System.out.println("ruleStringCopy is: "+ ruleStringCopy);
        
        
        
        
        
        
        
        DataPoint d = new DataPoint(metrics, 
                                    "DataPoint " + i,
                                    ruleString, 
                                    segLength,
                                    starter, 
                                    exactMelody, 
                                    relativePitchMelodyString, 
                                    brickType, 
                                    head, 
                                    chorusNumber, 
                                    chords, 
                                    startTied, 
                                    endTied,
                                    restPercent,
                                    ruleStringCopy);
        
        
        
        /*
        DataPoint d = new DataPoint(exactStartBeat, 
                                    consonance, 
                                    noteCount, 
                                    restDuration, 
                                    averageMaxSlope / (numSegments),
                                    startBeat, 
                                    numSegments, 
                                    i, 
                                    ruleString, 
                                    segLength,
                                    starter, 
                                    exactMelody, 
                                    relativePitchMelodyString, 
                                    brickType, 
                                    head, 
                                    chorusNumber, 
                                    chords, 
                                    startTied, 
                                    endTied);
        */
        

        

        
        
        
        
        
        

        //System.out.println("d is: " + d.toString());
        
        return d;
    }
    
    private static void updateGlobalMetricMaxMin(DataPoint d){
       Metric[] metricList = d.getMetrics();
        for(int i = 0; i < metricList.length; i++){
            if(metricList[i].getValue() > maxMetricValues[i]){          
                    maxMetricValues[i] = metricList[i].getValue();
                }
                
            if(metricList[i].getValue() < minMetricValues[i]){          
                minMetricValues[i] = metricList[i].getValue();
            }
            
        }
        
    }
    
    private static double getSyncopation(IndexedMelodyPart indexMel, int exactStartBeat){
        int numOffBeatNotes = 0;

        int numNotes = 0;
        int currentSlot = exactStartBeat;
        //int lastSlot = indexMel.getLastNoteIndex();
        while(indexMel.getCurrentNote(currentSlot) != null){
            if(indexMel.getCurrentNote(currentSlot).getPitch() == Note.REST){//don't want to include rests in total note count
                currentSlot = indexMel.getNextIndex(currentSlot);
                continue;
            }
            //for the non-rest note
            if(currentSlot % 120 != 0){
                numOffBeatNotes++;
            }
            currentSlot = indexMel.getNextIndex(currentSlot);
            numNotes++;
        } 
        
        if(numNotes == 0){//if we don't have any notes --> avoid dividing by 0
            return 0;
        }
  
        return (double) numOffBeatNotes / numNotes;
    }
    /*
    private float getDiversityIndex(IndexedMelodyPart indexMel, int exactStartBeat){
        int currentSlot = exactStartBeat;
        int lastSlot = indexMel.getLastNoteIndex();
        
        while(currentSlot <= lastSlot){
            if(indexMel.getCurrentNote(currentSlot).getPitch() == Note.REST){//don't want to include rests in total note count
                currentSlot += indexMel.getCurrentNote(currentSlot).getRhythmValue();
                continue;
            }
            //for the non-rest note
            if(currentSlot){
                numOffBeatNotes++;
            }
            currentSlot += indexMel.getCurrentNote(currentSlot).getRhythmValue();
            numNotes++;
        } 
    }
    */
    
    private static MelodyRhythmCount getMelodyRhythmCount(IndexedMelodyPart indexMel, int exactStartBeat){
        int currentSlot = exactStartBeat;
        //int lastSlot = indexMel.getLastNoteIndex();
        int[] durationFrequencies = new int[819];
        int prime = 821;
        double mostFreqDuration = 0;
        int numNotes = 0;
        double longestRhythm = Integer.MIN_VALUE;
        double longestRest = Integer.MIN_VALUE;
        
        
        while(indexMel.getCurrentNote(currentSlot) != null){
            Note note = indexMel.getCurrentNote(currentSlot);
            double rhythm = note.getRhythmValue();
            if(note.getPitch() == Note.REST){//skip rests
                if(rhythm > longestRest){
                    longestRest = rhythm;
                }
                currentSlot = indexMel.getNextIndex(currentSlot);
                continue;
            }
            
            
            if(rhythm > longestRhythm){
                longestRhythm = rhythm;
            }
            
            currentSlot = indexMel.getNextIndex(currentSlot);
            numNotes++;
            
            int index = (int) rhythm % prime;
            
            durationFrequencies[index] += 1;
            if(durationFrequencies[index] > mostFreqDuration){
                mostFreqDuration = rhythm;
            }
            

        } 
        
        if(numNotes == 0){//if we don't have any notes, return default empty MelodyRhythmCount object
            return new MelodyRhythmCount(durationFrequencies, mostFreqDuration, 0, longestRhythm, longestRest);
        }
        
        double diversityIndex = ( (double) durationFrequencies[(int) mostFreqDuration % prime] ) / numNotes;
        
     
        return new MelodyRhythmCount(durationFrequencies, mostFreqDuration, diversityIndex, longestRhythm, longestRest);
    }
    
    private static Polylist getSlopePolylist(Polylist rule){
        //get rid of segment part
        rule = rule.rest();
        
        Polylist slopePolylist = new Polylist();
        while(rule.nonEmpty()){
            if(rule.first() instanceof Polylist){
                if(((Polylist)rule.first()).first().equals("slope")){
                    slopePolylist = slopePolylist.addToEnd((Polylist) rule.first());
                }
            }
            rule = rule.rest();
        }
        return slopePolylist;
    }
    
    private static ContourData getContourData(Polylist rule){
        Polylist slopePolylist = getSlopePolylist(rule);
        ArrayList<Short> contours = new ArrayList<Short>();
        short prev = -1;
        int numChanges = -1;
        
        while(slopePolylist.nonEmpty()){
            if( ((Polylist) slopePolylist.first()).length() > 4){//if the slope has more than one note
                long minSlope = ((long) ((Polylist) slopePolylist.first()).second());
                if (minSlope<0&&prev!=ContourData.DOWN){
                    contours.add(ContourData.DOWN);
                    prev = ContourData.DOWN;
                    numChanges++;
                }else if (minSlope>=0&&prev!=ContourData.UP){
                    contours.add(ContourData.UP);
                    prev = ContourData.UP;
                    numChanges++;
                }
            }else if(prev!=ContourData.FLAT){
                contours.add(ContourData.FLAT);
                prev = ContourData.FLAT;
                numChanges++;
            }
            slopePolylist=slopePolylist.rest();
        }
        
        return new ContourData(contours, numChanges);
    }
    
    private static boolean hashMapTest(){
        int[] hashMap = new int[819];
        int[] possibilities = {1, 3, 7, 15, 30, 60, 120, 240, 480};
        ArrayList<Integer> noteLengths = new ArrayList<Integer>();
        for(int i = 0; i < possibilities.length; i++){
            noteLengths.add(possibilities[i]);
        }
        
        for(int i = 0; i < possibilities.length; i++){
            for(int j = 0; j < possibilities.length; j++){
                noteLengths.add(possibilities[i]+possibilities[j]);
            }
        }
        
        for(int k = 0; k < possibilities.length; k++){
            for(int i = 0; i < possibilities.length; i++){
                for(int j = 0; j < possibilities.length; j++){
                    noteLengths.add(possibilities[i]+possibilities[j]+possibilities[k]);
                }
            }
        }
        
        ArrayList<Integer> uniqueNoteLengths = new ArrayList<Integer>();
        for (int i: noteLengths){
            if (!uniqueNoteLengths.contains(i)){
                uniqueNoteLengths.add(i);
            }
        }
        
        for(int i=0;i<hashMap.length;i++){
            hashMap[i] = 0;
        }
        
        for (int i: uniqueNoteLengths){
            hashMap[i % 821] += 1;
        }
        
        int nonperfectCount=0;
        for(int i=0;i<hashMap.length;i++){
            if(hashMap[i] > 1){
                nonperfectCount++;
            }
        }
        
        //System.out.println("nonPerfectCount : " + nonperfectCount);
        
        for(int i=0;i<hashMap.length;i++){
            if(hashMap[i] > 1){
                return false;
            }
        } 
        return true;
        
        
        
        
    }

    private static double getConsonance(String ruleString, IndexedMelodyPart p) {
        int consonance = 0;
        
        Vector<Character> noteTypes = new Vector<Character>();

        for (int i = 0; i < ruleString.length(); i++) {
            char c = ruleString.charAt(i);
            if (c == 'C' || c == 'L' || c == 'A' || c == 'X' || c == 'R' || c == 'G') {
                noteTypes.add(c);
            }
        }

        //this should only happen for all rests, where rests were not properly merged
        ArrayList<Unit> units = p.getUnitList();
        if (units.size() != noteTypes.size()) {
            return 0;
        }
        
        for (int i = 0; i < noteTypes.size(); i++) {
            int noteLength = units.get(i).getRhythmValue();
            switch (noteTypes.get(i)) {
                case 'G':
                    consonance += 1 * noteLength;
                    break;
                case 'C':
                    consonance += .8 * noteLength;
                    break;
                case 'A':
                    consonance += .6 * noteLength;
                    break;
                case 'L':
                    consonance += .4 * noteLength;
                    break;
                case 'X':
                    consonance += .1 * noteLength;
                    break;
                default:
                    break;
            }
        }
        
       // System.out.println("consonance is: " + consonance);

        return consonance;
    }

    //returns the slot on which the first note is struck
    private static int getStartBeat(IndexedMelodyPart p) {
        int slots = 0;
        int tracker = 0;
        Note n = p.getNote(tracker);
        
        if (n == null) {
            System.out.println(p);
            return 0;
        }
        while (n != null && n.isRest()) {
            tracker = p.getNextIndex(tracker);
            slots += n.getRhythmValue();
            n = p.getNote(tracker);
        }
        return slots;
    }

    public static Polylist readRule(Polylist rule) {
        if( rule.isEmpty() )
        {
            return rule; //shouldn't happen, but has
        }
        Polylist result = Polylist.nil;
        rule = ((Polylist) rule.first()).rest();
        while (rule.nonEmpty()) {
            if (rule.first() instanceof Polylist) {
                Polylist inner = (Polylist) rule.first();
                while (inner.nonEmpty()) {
                    result = result.cons(inner.first());
                    inner = inner.rest();
                }
            } else {
                result = result.cons(rule.first());
            }

            rule = rule.rest();
        }
        return result.reverse();
    }

    public static String removeTrailingSpaces(String in) {
        while (in.endsWith(" ")) {
            in = in.substring(0, in.length() - 2);
        }
        return in;
    }

    // Loads the grammar rules from the file in a polylist
    public static Polylist[] getRulesFromWriter(StringWriter inWriter) {
        Polylist[] grammarRules = null;

        //only add a rule to this list of rules if it's not a duplicate of any previous rules
        ArrayList<Polylist> rulesList = new ArrayList<Polylist>();
        String input = inWriter.toString();
        String[] rules = input.split("\n");
        for (int i = 0; i < rules.length; i++) {
            String rule = rules[i];
            int stopIndex = rule.length();
            if (rule.contains("Head")) {
                stopIndex = rule.indexOf("Head");
            } else if (rule.contains("Chorus")) {
                stopIndex = rule.indexOf("Chorus");
            }

            Polylist newRule = Polylist.PolylistFromString(rules[i]);
            //System.out.println("newRule is: " + newRule.toString());

            boolean isUnique = true;
            if (rulesList.size() > 0) {
                for (Polylist p : rulesList) {
                    if (newRule.equals(p)) {
                        isUnique = false;
                        break;
                    }
                }
            }
            if (isUnique) {
                rulesList.add(newRule);
            }
        }

        Polylist[] rulesArray = new Polylist[rulesList.size()];
        for (int i = 0; i < rulesList.size(); ++i) {
            rulesArray[i] = rulesList.get(i);
            rulesArray[i] = readRule(rulesArray[i]);
        }
        
        return rulesArray;
    }
    // Loads the grammar rules from in a polylist

    public static String[] getRuleStringsFromWriter(StringWriter inWriter) {
        
        ArrayList<String> stringsList = new ArrayList<String>();
        String input = inWriter.toString();
        String[] inputStrings = input.split("\n");
        for (int i = 0; i < inputStrings.length; ++i) {
            boolean isUnique = true;
            if (stringsList.size() > 0) {
                for (String s : stringsList) {
                    if (inputStrings[i].equals(s)) {
                        isUnique = false;
                        break;
                    }
                }
            }
            if (isUnique) {
                stringsList.add(inputStrings[i]);
            }
        }
        String[] ruleStrings = new String[stringsList.size()];
        for (int j = 0; j < stringsList.size(); ++j) {
            ruleStrings[j] = stringsList.get(j);
        }
        return ruleStrings;
    }

    //get averages
    public static double[] calcAverage(Vector<DataPoint> seg) {
//        System.out.println("Seg size is: " + seg.size());
        double[] averages = new double[numMetrics];
        for (int i = 0; i < seg.size(); i++) {
            DataPoint temp = seg.get(i);
            for (int j=0; j<numMetrics; j++){
                averages[j] += temp.getMetrics()[j].getValue();
            }
        }
        for (int i = 0; i<numMetrics;i++){
            averages[i] /= seg.size();
        }
        return averages;
    }
    //normalize vector

    public static void averageVector(Vector<DataPoint> seg, double[] averages) {
        //System.out.println("*****TESTING AVERAGE VECTOR******");
        for (int i = 0; i < seg.size(); i++) {
            DataPoint temp = seg.get(i);
            for(int j = 0; j<numMetrics;j++){
                double oldValue = temp.getMetrics()[j].getValue();
                //System.out.println("oldValue = "+ oldValue);
                temp.setMetricAtI(j, ((double) oldValue/averages[j]));
                //System.out.println("newValue = "+ ((double) oldValue/averages[j]));
            }
        }
    }

    
    public static void normalizeDatapoints(Vector<DataPoint> datapoints, Double[] maxMetricVals, Double[] minMetricVals) {
        for (int i = 0; i < datapoints.size(); i++) {
            DataPoint d = datapoints.get(i);
            normalizeDataPoint(d, maxMetricVals, minMetricVals);
        }
    }
    
    public static DataPoint normalizeDataPoint(DataPoint d, Double[] maxMetricVals, Double[] minMetricVals){
        int metricListLength = d.getMetrics().length;
        for(int j = 0; j < metricListLength; j++){
                double oldValue = d.getMetrics()[j].getValue();
                d.setMetricAtI( j, normalizeValue(oldValue, maxMetricVals[j], minMetricVals[j]) );
        }
        return d;
    }
    
    private static double normalizeValue(double value, double max, double min){
        return ( (value - min) / (max - min) );
    }
    
    

    public static Cluster[] getClusters(Vector<DataPoint> data, int numClusters, MetricListFactory mlf) {
        JCA jca;
        
        //numclusters is greater than the number of datapoints, use the same number of clusters
        //as there are datapoints
        if (data.size() < numClusters) {
            jca = new JCA(data.size(), data.size(), data, mlf);
        } else {
            jca = new JCA(numClusters, data.size(), data, mlf);
        }
        jca.startAnalysis();
        Cluster[] clusters = jca.getClusterOutput();

        //get average cluster size
        int totalPoints = 0;
        for (int i = 0; i < clusters.length; i++) {
            totalPoints += clusters[i].getNumDataPoints();
        }
        
        int averageClusterSize = totalPoints / clusters.length;
        
        //get number of clusters that are big enough
        //currently we are keeping all clusters
        int numGoodClusters = 0;
        for (int i = 0; i < clusters.length; i++) {
            if (true || clusters[i].getNumDataPoints() >= averageClusterSize / 5) {
                numGoodClusters++;
            }
        }

        //copy good clusters into a new array and return that array

        int counter = 0;
        Cluster[] goodClusters = new Cluster[numGoodClusters];
        for (int i = 0; i < clusters.length; i++) {
            if (true || clusters[i].getNumDataPoints() >= averageClusterSize / 5) {
                goodClusters[counter] = clusters[i];
                goodClusters[counter].setName("Cluster" + counter);
                counter++;
            }
        }

        
        return goodClusters;
    }

    /* Writes all the necessary data for a soloist that we have here in memory
     * Writes in serialized form as objects so that the same objects can be
     * loaded back in memory. Called from notate
     * Writes 6 objects in order:
     * datapoints, clusters, clusterSets, transitions, reverseTransitions, outlines
     */
    public static void createSoloistFile(Vector<DataPoint> dataPoints, 
                                         Cluster[] clusters,
                                         Vector<ClusterSet> clusterSets, 
                                         Vector<NGramWithTransitions> transitions,
                                         Vector<NGramWithTransitions> reverseTransitions, 
                                         Vector<Vector<ClusterSet>> outlines, 
                                         File outFile) 
      {
        FileOutputStream fileOut;
        ObjectOutputStream objOut;
        try {
            fileOut = new FileOutputStream(outFile);
            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(dataPoints);
            objOut.writeObject(clusters);
            objOut.writeObject(clusterSets);
            objOut.writeObject(transitions);
            objOut.writeObject(reverseTransitions);
            objOut.writeObject(outlines);
            objOut.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //NOTE: was private
    public static Vector<ClusterSet> getClusterSets(Cluster[] clusters) {
        Vector<ClusterSet> sets = new Vector<ClusterSet>();
        for (int i = 0; i < clusters.length; i++) {
            ClusterSet set = new ClusterSet(clusters, clusters[i]);
            sets.add(set);
        }
        return sets;
    }

    public static void printClusterSets(Vector<ClusterSet> clusterSets) {
        for (int i = 0; i < clusterSets.size(); i++) {
            ClusterSet set = clusterSets.get(i);
            Vector<Cluster> relatives = set.getSimilarClusters();
            String s = "";
            s = s.concat(Integer.toString(set.getOriginal().getNumber()));
            for (int j = 0; j < relatives.size(); j++) {
                s = s.concat(", " + Integer.toString(relatives.get(j).getNumber()));
            }
        }
    }

    //Takes the numbers of the clusters and returns vectors of ClusterSets
    //NOTE: was private
    public static Vector<Vector<ClusterSet>> getOutlines(Vector<Vector<DataPoint>> orders,
            Cluster[] clusters,
            Vector<ClusterSet> clusterSets) {
        
        Vector<Vector<ClusterSet>> outlines = new Vector<Vector<ClusterSet>>();

        for (int i = 0; i < orders.size(); i++) {
            Vector<DataPoint> order = orders.get(i);
            Vector<ClusterSet> outline = new Vector<ClusterSet>();
            for (int j = 0; j < order.size(); j++) {
                Cluster original = order.get(j).getCluster();
                ClusterSet next = null;
                for (int k = 0; k < clusterSets.size(); k++) {
                    if (original.equals(clusterSets.get(k).getOriginal())) {
                        next = clusterSets.get(k);
                    }
                }
                outline.add(next);
            }
            if (outline.size() > 2) {
                outlines.add(outline);
            }
        }

        return outlines;
    }

    //takes an array of bigrams and returns an array of bigrams with
    //the order of each bigram reversed
    private static Vector<NGram> getReverse(Vector<NGram> ngrams) {

        Vector<NGram> reverseNGrams = new Vector<NGram>();

        for (int i = 0; i < ngrams.size(); i++) {
            NGram ngram = ngrams.get(i);
            int[] reverseChain = {ngram.getLast(), ngram.getFirst()};
            NGram reverse = new NGram(reverseChain);
            reverseNGrams.add(reverse);
        }
        return reverseNGrams;
    }

    private static Vector<NGramWithTransitions> getTransitions(Vector<NGram> ngrams,
            Cluster[] clusters) {

        Vector<NGramWithTransitions> transitions = new Vector<NGramWithTransitions>();
        for (int i = 0; i < clusters.length; i++) {
            NGramWithTransitions t = new NGramWithTransitions(i, ngrams);
            transitions.add(t);
        }
        return transitions;
    }

    private static void printTransitionProbs(Vector<NGramWithTransitions> transitions) {
        for (int j = 0; j < transitions.size(); j++) {
            NGramWithTransitions t = transitions.get(j);
            Vector<Integer> states = t.getStates();
            Vector<Float> probs = t.getProbabilities();
            //System.out.println("State " + t.getState());
            for (int i = 0; i < states.size(); i++) {
                System.out.print(states.get(i) + ": " + probs.get(i) + ", ");
            }
            //System.out.println("\nSum of probs: " + t.getSumOfProbs());
        }
    }

    private static void printTransitionProbs(Vector<NGram> ngrams, Vector<NGram> reverseNGrams) {
        //get the reverse transition probabilities
        for (int i = 0; i < reverseNGrams.size(); i++) {
            System.out.println(reverseNGrams.get(i));
        }
        System.out.println();
        System.out.println("STOP");
        System.out.println();
        for (int i = 0; i < ngrams.size(); i++) {
            System.out.println(ngrams.get(i));
        }
    }

    /* Takes an array of clusters and a vector of datapoints
     * Looks through the datapoints and finds which cluster each is
     * in and sets the clusterName fields of the datapoints
     */
    private static Vector<DataPoint> setClusters(Vector<DataPoint> dataPoints, Cluster[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            Cluster c = clusters[i];
            for (int j = 0; j < c.getNumDataPoints(); j++) {
                DataPoint p = c.getDataPoint(j);
                for (int k = 0; k < dataPoints.size(); k++) {
                    if (p.equals(dataPoints.get(k))) {
                        p.setClusterName(Integer.toString(i));
                        dataPoints.set(j, p);
                    }
                }
            }
        }
        return dataPoints;
    }

    private static void writeClusterReps(DataPoint[] reps, Cluster[] clusters, String outFile) {
        int totalPoints = 0;
        for (int i = 0; i < clusters.length; i++) {
            totalPoints += clusters[i].getNumDataPoints();
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile, true));
            for (int i = 0; i < reps.length; i++) {
                String rule = reps[i].getObjData();
                int numAppearances = reps[i].getNumber();
                rule = rule.substring(0, rule.length() - 1);

                //get cluster number from string clusterName
                int clusterNumber = Integer.parseInt(reps[i].getClusterName().substring(7));
                int clusterSize = clusters[clusterNumber].getNumDataPoints();
                float prob = (float) clusterSize * numAppearances / (float) totalPoints;

                rule = rule.concat(df.format(prob) + ")");
                out.write(rule + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("IO EXCEPTION!" + e.toString());
        }
    }

    /* Takes an array of clusters
     * gets a variable number of representatives from each cluster by taking the
     * elements closest to the average of all elements in the cluster
     */
    public static DataPoint[] getClusterReps(Cluster[] clusters, int repsPerCluster) {
        //get average cluster size
        int totalPoints = 0;
        for (int i = 0; i < clusters.length; i++) {
            totalPoints += clusters[i].getNumDataPoints();
        }

        //System.out.println("totalPoints in getClusterReps: "+totalPoints);

        Metric[] averages;
        Vector<DataPoint> representatives = new Vector<DataPoint>();

        for (int i = 0; i < clusters.length; i++) {
            averages = metricListFactory.getNewMetricList();

            Cluster tempCluster = clusters[i];

            for (int j = 0; j < clusters[i].getNumDataPoints(); j++) {
                DataPoint tempPoint = tempCluster.getDataPoint(j);
                for (int k = 0; k<averages.length;k++){
                    averages[k].incrementValue(tempPoint.getMetrics()[k].getValue());
            }
            }
            for (int k = 0; k<averages.length;k++){
                    averages[k].divideValue(clusters.length);
                }

            //set averagePoint class variable
            averagePoint = new DataPoint(averages, "averages");

            //sort the points by distance from averagePoint
            Vector<DataPoint> points = tempCluster.getDataPoints();
            
            // This causes an exception sometime. Do we really need it?
            //java.lang.IllegalArgumentException: Comparison method violates its general contract!
            try
              {
              Collections.sort((List) points, new DataPointDistanceComparer());
              }
            catch( Exception e )
              {
                // Do nothing for now
              }

            //remove duplicates
            for (int j = 0; j < points.size() - 1; j++) {
                if (points.get(j).equals(points.get(j + 1))) {
                    DataPoint temp = points.get(j);
                    temp.addDuplicate();
                    points.set(j, temp);
                    points.remove(j + 1);
                    j--;

                }
            }

            //This approach caps the number of cluster reps, no longer used
            int realRepsPerCluster = tempCluster.getNumDataPoints();

            //put the closest REPS_PER_CLUSTER into array
            for (int k = 0; k < realRepsPerCluster; k++) {
                DataPoint dp = points.get(k);
                dp.setClusterName(tempCluster.getName());
                representatives.add(dp);
            }
        }
        DataPoint[] reps = new DataPoint[representatives.size()];
        for (int i = 0; i < reps.length; i++) {
            reps[i] = representatives.get(i);
        }
        return reps;
    }

    public static DataPoint getAveragePoint() {
        return averagePoint;
    }
}
