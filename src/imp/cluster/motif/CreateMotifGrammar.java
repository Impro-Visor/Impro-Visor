/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
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

package imp.cluster.motif;

import imp.cluster.Cluster;
import static imp.cluster.CreateGrammar.getClusterReps;
import imp.cluster.DataPoint;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import polya.Polylist;
/**
 *
 * @author Joseph Yaconelli
 */
public class CreateMotifGrammar {
 
    
    // turn on for print statements, and wrap all testing in if(TESTING){code}
    private static final boolean TESTING = false;
    
    // switch to make start symbol START_M or P (START_M only for testing)
    private static final boolean IS_ISOLATED = false;
    
    // isolated mode currently buggy and requires manual changes in grammar file after creation
    private static final String START_ISOLATED = "START_M";
    
    private static final String START_INTEGRATED = "P";
    private static final String NON_MOTIF_CLUSTER = "M_X";
    private static final String STD_GRAMMAR_START = "Q";
    
    // switch to make use of motif much more likely than normal grammar
    //(only changes anything if IS_ISOLATED = false)
    private static final boolean IS_HIGHLY_LIKELY = true;


    public static double MIN_PROB = 5.0; //include phrase transitions of greater than this probability
    public static int REPS_PER_CLUSTER = 5;
    private static DecimalFormat df = new DecimalFormat("0.00"); // will use when calculating probabilities
    private static final double DEFAULT_PROB = 1.00;
    
    
    /**
     * Creates grammar to represent structure of motifs and motivic development.
     * <br><b>NOTE:</b>A larger motifness value increases the number of melodies condensed into the same {@link MotifCluster}.
     * Recommended values (0.1, 0.5), but any value [0, infinity) works.
     * @param repsPerCluster number of representatives to use from each cluster
     * @param clusters learned clusters of melodies
     * @param dataPoints melody pieces
     * @param windowSize Size of window to capture motif grammar
     * @param windowSlide Distance between the start of each window
     * @param motifness Unnormalized Euclidean Distance cut-off to be considered part of a {@link MotifCluster}
     * @return Array of grammar rules in Polylist form
     * @see Polylist
     */
    public static Polylist[] create(
                              int repsPerCluster,
                              Cluster[] clusters,
                              Vector<DataPoint> dataPoints,
                              int windowSize,
                              int windowSlide,
                              double motifness)
    {
        
        // convert Vector to ArrayList (Vectors are depreciated)
        ArrayList<DataPoint> dataList = new ArrayList<>(dataPoints);
        
        //resets motif class for new run
        Motif.reset();
        
        // turns out getClusterReps doesn't do anything...
        DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
        
        
        // sort all data points in each cluster by distance to Centroid
        ArrayList<DataPoint>[] sortedClusters;
        sortedClusters = new ArrayList[clusters.length];
        
        EuclideanDistanceComparer edc = new EuclideanDistanceComparer();
        
        for(int i = 0; i < clusters.length; i++){
            sortedClusters[i] = new ArrayList<>(clusters[i].getDataPoints());
            sortedClusters[i].sort(edc);
        }
        

        if(TESTING){
            // testing
            System.out.println("********** TESTING ***********");
            ArrayList<DataPoint> sortedPoints = new ArrayList<>();

            for (ArrayList<DataPoint> sortedCluster : sortedClusters) {
                sortedPoints.addAll(sortedCluster);
            }

            for(DataPoint d : sortedPoints){
                System.out.println(d.getClusterName()
                        + "\t--\t" + d.getMelody().getIndex()
                        + "\t--\t" + d.getCurrentEuDt());
            }
            System.out.println("********** END TESTING ***********");
        }
        
        // lists to keep track of Motifs and MotifClusters
        ArrayList<Motif> motifMelody = new ArrayList<>(dataList.size());
        ArrayList<MotifCluster> motifClusters = new ArrayList<>(sortedClusters.length);
        
        //create MotifClusters for each Cluster
        for(ArrayList<DataPoint> adp : sortedClusters){
            // create Motif and MotifCluster for data point closest to Centroid
            Motif tempMotif = new Motif(adp.get(0).getMelody(), adp.get(0).getAbstractMelody(), adp.get(0).getClusterName());
            MotifCluster tempCluster = new MotifCluster(tempMotif);
                       
            motifClusters.add(tempCluster);
            motifMelody.add(tempMotif);
        }
        
        //for testing
        for(int i = 0; i < motifClusters.size(); i++){
            motifClusters.get(i).addMotif(motifMelody.get(i));
        }
        
        // used as temp vars while creating Motifs
        DataPoint d;
        MotifCluster c;
        ArrayList<DataPoint> adp;
        
        // turn each datapoint into a motif representation
        for(int i = 0; i < sortedClusters.length; i++){

            adp = sortedClusters[i];
            c = motifClusters.get(i);
            
            // add the rest of the data points to motifMelody (skipping the one per cluster already added)
            for(int j = 1; j < adp.size(); j++){
                d = adp.get(j);
                Motif temp = new Motif(d.getMelody(), d.getAbstractMelody(), d.getClusterName());
                
                //add Motif to cluster if Euclidean distance within Motifness requirement
                if(d.getCurrentEuDt() <= motifness)
                    c.addMotif(temp);
                else{
                    temp.setCluster(NON_MOTIF_CLUSTER);
                }
                // add motif to melody list
                motifMelody.add(temp);

            }
        }
        
        
        if(TESTING){
            // testing
            System.out.println("********** TESTING ***********");
            for(Motif m : motifMelody){
                System.out.println(m);
            }
            System.out.println("********** END TESTING ***********");


            // testing
            System.out.println("********** TESTING MOTIF CLUSTERS ***********");
            for(MotifCluster mc : motifClusters){
                System.out.println(mc);
            }
            System.out.println("********** END TESTING MOTIF CLUSTERS ***********");
        }
        // rename all motif clusters M_A, M_B,...
        renameMotifs(motifMelody);
        
        // update the names of MotifClusters to match cluster name of member motifs
        motifClusters.forEach((mc) -> {
            mc.updateName();
        });
        
        // compares Motifs based on time each instance appears in song
        MotifTemporalComparer mtc = new MotifTemporalComparer(); 
        
        // put Motifs back in order of melody
        motifMelody.sort(mtc);

        // TODO: change window size and slide to come from user input
        Polylist[] grammar = defineGrammarRules(motifMelody.toArray(new Motif[motifMelody.size()]),
                                                                        windowSize,
                                                                        windowSlide);
        

        
        // get start states
        ArrayList<Integer> startStates = new ArrayList<>();
        
        for(int i = 0; i < grammar.length; i++){
            startStates.add(i);
        }
        
        int[] states = new int[startStates.size()];
        
        for(int i = 0; i < states.length; i++){
            states[i] = startStates.get(i);
        }
        
        // add start states
        grammar = addStartStates(grammar, states);
        
        ArrayList<Polylist> finalGrammar = new ArrayList<>(Arrays.asList(grammar));
        
        // add terminal rules for motifs
        for(MotifCluster mc : motifClusters){
            finalGrammar.add(mc.getMotif().grammarRule());
        }
        
        // add terminal rules for non-motifs
        /*
         * when uncommented, this adds a grammar rule for each non motif
         * Commented out in favor of sending non motifs to the normal grammar's
         * START as to utilize Markov chaining, etc already implemented.
         * If you choose to use this instead, please comment out the other method
         * as they will conflict.
        for(Motif m : motifMelody){
            if(m.getCluster().equalsIgnoreCase(NON_MOTIF_CLUSTER))
                finalGrammar.add(m.grammarRule());
        }
        */
        
        for(int i = 0; i < sortedClusters.length; i++){
            finalGrammar.add(Polylist.list("rule",
                    Polylist.PolylistFromString(NON_MOTIF_CLUSTER),
                    Polylist.PolylistFromString(STD_GRAMMAR_START.concat(String.valueOf(i))),
                    MIN_PROB));
        }
        
        if(TESTING){
            // testing
            System.out.println("********** FINAL MOTIF CLUSTERS ***********");
            for(MotifCluster mc : motifClusters){
                System.out.println(mc);
            }
            System.out.println("********** END FINAL MOTIF CLUSTERS ***********");

            /****************** for testing *****************/
            System.out.println("************* FINAL GRAMMAR *************");

            finalGrammar.forEach((rule) -> {
                System.out.println(rule);
            });

            System.out.println("*********** END FINAL GRAMMAR ***********");

            /**************** end for testing ***************/
        }
        
        // convert to array and return
        return finalGrammar.toArray(new Polylist[finalGrammar.size()]);
        
    }
    
    /**
     * Renames all motif clusters to the form "M_*" with * being letters A,B,C...
     * @param motifs ArrayList of motifs to change the cluster names of
     */
    
    private static void renameMotifs(ArrayList<Motif> motifs){
        int clusts = Motif.getClusters().size();
        int nonMotifs = Motif.getClusters().indexOf(NON_MOTIF_CLUSTER);
        char name = 'A';
        
        String[] newClusters = new String[clusts];
        
        for(int i = 0; i < clusts; i++){
            newClusters[i] = "M_" + String.valueOf(name++);
        }

        // keep the non motifs labeled with the NON_MOTIF_CLUSTER label
        if(nonMotifs >= 0){
            newClusters[nonMotifs] = NON_MOTIF_CLUSTER;
        }
        
        Motif.setClusters(newClusters);
    }
    
    
    /**
     * Creates grammar rules for motivic order
     * @return An array of grammar rules in the form of Polylists
     */
    private static Polylist[] defineGrammarRules(Motif[] motifMelody, int windowSize, int windowSlide){
        
        ArrayList<Polylist> rules = new ArrayList<>();
        
        Polylist temp_rule, temp_rhs;
        
        for(int window = 0; window < (motifMelody.length - windowSize); window += windowSlide){
            
            temp_rhs = Polylist.list();
            
            for(int i = 0; i < windowSize; i++){
                temp_rhs = temp_rhs.addToEnd(motifMelody[window+i].getCluster());
            }
            
            
            temp_rule = Polylist.list("rule", Polylist.list("PATTERN_" + window), temp_rhs, DEFAULT_PROB);
                        
            rules.add(temp_rule);
        }

        return rules.toArray(new Polylist[rules.size()]);
    }
    
    /**
     * Add entry states to grammar
     * @param motifRules array of grammar rules
     * @param start array of entry states to add
     * @return A new array or grammar rules with the entry states added
     * @see Polylist
     */
    private static Polylist[] addStartStates(Polylist[] motifRules, int[] start){
        ArrayList<Polylist> temp = new ArrayList<>(Arrays.asList(motifRules));
        
        Polylist p, r;
        
        // add all start rules
        for(int i : start){
            
            r = (Polylist) motifRules[i].second();
            
            String startSymbol;
            double startProbability;
            
            // choose which start symbol and probability to use
            startSymbol = IS_ISOLATED ? START_ISOLATED : START_INTEGRATED;
            startProbability = IS_HIGHLY_LIKELY ? DEFAULT_PROB*100 : DEFAULT_PROB;
            
            // add generative start rule
            p = Polylist.list("rule", Polylist.list(startSymbol), Polylist.list(r.first(), startSymbol.concat(String.valueOf(i))), startProbability*10);
            temp.add(p);
            
            // add base case start rule
            p = Polylist.list("rule", Polylist.list(startSymbol), Polylist.list(r.first()), startProbability);
            temp.add(p);
        }

        
        return temp.toArray(new Polylist[temp.size()]);

    }
    
    /**
     * Add entry state to grammar
     * @param motifRules array or grammar rules
     * @param start entry state index
     * @return A new array or grammar rules with the entry states added
     * @see Polylist
     */
    private static Polylist[] addStartState(Polylist[] motifRules, int start){
        int[] startToArray = new int[1];
        startToArray[0] = start;
        
        return addStartStates(motifRules, startToArray);
        
    }
    /**
     * Add default entry state to grammar.
     * The default entry state is considered to be the first element of the rule array
     * @param motifRules
     * @return A new array or grammar rules with the entry states added
     * @see Polylist
     */
    private static Polylist[] addStartState(Polylist[] motifRules){
        return addStartState(motifRules, 0);
    }

    /**
     * writes grammar to file.
     * @param rules array of grammar rules in the form of Polylists
     * @param outFile file to output to
     * @see Polylist
     */
    public static void writeMotifGrammar(Polylist[] rules, String outFile) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile, true));
            for (Polylist rule : rules) {
                out.write(rule + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("IO EXCEPTION!" + e.toString());
        }
    }

}
