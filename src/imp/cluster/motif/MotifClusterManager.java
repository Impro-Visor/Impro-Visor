/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
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

package imp.cluster.motif;

import imp.generalCluster.DataPoint;
import imp.generalCluster.Metric;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import polya.Polylist;

/**
 *
 * @author Joseph Yaconelli
 */
public class MotifClusterManager {
    
    private static double TOLERANCE = 0.9;
    private static final HashMap<MotifClusterKey, MotifCluster> CLUSTERS = new HashMap<>();
    //private static final ArrayList<Motif> nonMotifs = new ArrayList<>();
    private static int _currentMotifName = 0;
    //private static Polylist grammar = Polylist.nil;
    
    
    
    // turn on for print statements, and wrap all testing in if(TESTING){code}
    public static final boolean TESTING = false;
    
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
    private static final DecimalFormat df = new DecimalFormat("0.00"); // will use when calculating probabilities
    private static final double DEFAULT_PROB = 1.00;
    
    
    /**
     * Adds Motif to a MotifCluster if a good one exists, otherwise creates new MotifCluster.
     * @param dp DataPoint to add
     * @return MotifCluster that received the Motif
     */
    public static MotifCluster addMotif(DataPoint dp){
        
        Motif m = new Motif(dp.getMelody(),
                dp.getAbstractMelody(),
                "M_".concat(String.valueOf(_currentMotifName++)),
                dp.getCurrentEuDt());
        
        // makes sure not to accidentally name a Motif the reserved "non-motif" name M_X
        if(m.getCluster().equalsIgnoreCase("M_X")){
            m.setCluster("M_".concat(String.valueOf(_currentMotifName++)));
        }
        
        MotifClusterKey newClusterKey;
        
        Metric[] metric_objs;
        metric_objs = dp.getMetrics().toArray(new Metric[dp.getMetrics().size()]);
        
        double [] metrics = new double[dp.getMetrics().size()];
        
        for(int i = 0; i < metrics.length; i++){
            metrics[i] = metric_objs[i].getValue();
        }
        
        newClusterKey = new MotifClusterKey(metrics, TOLERANCE);
        
        
        
        if(CLUSTERS.containsKey(newClusterKey)){
            if(CLUSTERS.get(newClusterKey) == null){
                System.out.println("It's null......");
            }
            CLUSTERS.get(newClusterKey).addMotif(m);
            
            if(TESTING){
                System.out.println("Added to existing cluster: " + CLUSTERS.get(newClusterKey).getClusterName());
                System.out.println("Size of " + CLUSTERS.get(newClusterKey).getClusterName() + " = "  + CLUSTERS.get(newClusterKey).getTotalMotifsCount());
            }
        
        } else {
            if(TESTING)
                System.out.println("New cluster created");
            
            MotifCluster newCluster = new MotifCluster(m);
            CLUSTERS.put(newClusterKey, newCluster);
        }
        
        return CLUSTERS.get(newClusterKey);
    }
    
    /**
     * Adds a MotifCluster to the manager with a given key
     * @param key key under which the Motif Cluster will be accessible
     * @param cluster Motif Cluster to add to the manager
     */
    public static void addMotifCluster(MotifClusterKey key, MotifCluster cluster){
        CLUSTERS.put(key, cluster);
    }
    
    /**
     * Returns all Motif Clusters managed by the MotifClusterManager
     * @return all Motif Clusters
     */
    public static Collection<MotifCluster> getMotifClusters(){
        
        return CLUSTERS.values();
        
    }
    
    
    /**
     * Gets all grammar rules (and also updates MotifClusterManager.grammar)
     * @return grammar rules
     */
    public static Polylist getGrammar(){
        Polylist rules;
        
        Collection<MotifCluster> clusts = CLUSTERS.values();
        
        rules = Polylist.PolylistFromString("(parameter (auto-fill true)) (parameter (avoid-repeats true)) (parameter (chord-tone-decay 0.0)) (parameter (chord-tone-weight 0.7)) (parameter (color-tone-weight 0.2)) (parameter (expectancy-constant 0.7)) (parameter (expectancy-multiplier 0.0)) (parameter (leap-prob 0.01)) (parameter (max-duration 8)) (parameter (max-interval 6)) (parameter (max-pitch 82)) (parameter (min-duration 8)) (parameter (min-interval 0)) (parameter (min-pitch 58)) (parameter (rectify true)) (parameter (rest-prob 0.1)) (parameter (scale-root C)) (parameter (scale-tone-weight 0.1)) (parameter (scale-type Use_First_Scale)) (parameter (syncopation-constant 0.7)) (parameter (syncopation-multiplier 0.0)) (parameter (syncopation-type C)) (parameter (use-grammar true)) (parameter (use-syncopation false)) (startsymbol P)");
                
        Polylist p, r;
        String startSymbol;
        double startProbability;

        // choose which start symbol and probability to use
        startSymbol = IS_ISOLATED ? START_ISOLATED : START_INTEGRATED;
        startProbability = IS_HIGHLY_LIKELY ? DEFAULT_PROB*100 : DEFAULT_PROB;
        
        for(MotifCluster mc : clusts){
            rules = rules.addToEnd(mc.getMotif().grammarRule());
            
            r = (Polylist) mc.getMotif().grammarRule().second();

            // add generative start rule
            p = Polylist.list("rule", Polylist.list(startSymbol), Polylist.list(r.first(), startSymbol), startProbability*10);
            rules = rules.addToEnd(p);
        }
        
        if(TESTING){
            System.out.println("***************************GET GRAMMAR************************");
            System.out.println(rules.toString().replaceAll("rule", "\nrule"));
            System.out.println("***************************END GRAMMAR************************");
        }
        //MotifClusterManager.grammar = rules;
        return rules;
    }
    
    
    /**
     * Sets equivalence tolerance for MotifCluster equivalence
     * @param t tolerance
     */
    public static void setTolerance(double t){
        TOLERANCE = t;
    }
    
    
    /**
     * Resets all MotifClusters, then removes all Clusters.
     * @see MotifCluster.reset()
     */
    public static void reset(){
        CLUSTERS.entrySet().forEach((m) -> {
            m.getValue().reset();
        });
        
        CLUSTERS.clear();
    }
}
