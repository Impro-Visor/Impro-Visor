/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Mikayla Konst 2015
 * An IntervalLearner learns transition probabilities between intervals.
 * It can learn both 1st and 2nd order Markov Chains.
 */
public final class IntervalLearner {
    
    //1st Order Markov Chains
    
    private final int [][] deg1Counts;        //counts
    private final double [][] deg1Probs;      //probabilities
    private final int [] deg1Totals;          //row totals (for calculating probabilities)
    
    //2nd Order Markov Chains
    
    private final int [][][] deg2Counts;      //counts
    private final double [][][] deg2Probs;    //probabilities
    private final int [][] deg2Totals;        //row totals (for calculating probabilities)
    
    //Constants
    
    private static final int range = Constants.OCTAVE;  //limit intervals learned from to +/- an octave
    public static final int intervals = 2*range+1;      //# of unique directional intervals (-12, -11, ... , 0 , ... , 11, 12)
    private static final int ONE = 1;                   //many times we need to just add 1 to a running total
    
    /**
     * IntervalLearner
     * Constructor
     * Initializes counts, row totals, and probabilities to zero.
     */
    public IntervalLearner(){
        deg1Counts = new int[intervals][intervals];
        deg2Counts = new int[intervals][intervals][intervals];
        deg1Probs = new double[intervals][intervals];
        deg2Probs = new double[intervals][intervals][intervals];
        deg1Totals = new int[intervals];
        deg2Totals = new int[intervals][intervals];
        clearAll();
    }

    //Public Methods:
    
    //Accessors:
    
    /**
     * getDeg1Counts
     * @return 1st order counts
     */
    public int [][] getDeg1Counts(){
        return deg1Counts;
    }
    
    /**
     * getDeg2Counts
     * @return 2nd order counts
     */
    public int [][][] getDeg2Counts(){
        return deg2Counts;
    }
    
    /**
     * getCountsCrossSection
     * Returns a 2D array that is a cross section of the complete 3D transition matrix.
     * @param interval - source interval #1
     * @return Cross section corresponding to that source interval
     */
    public int [][] getCountsCrossSection(int interval){
        return deg2Counts[intervalToIndex(interval)];
    }
    
    /**
     * getDeg1Probs
     * @return 1st order probabilities
     */
    public double [][] getDeg1Probs(){
        return deg1Probs;
    }
    
    /**
     * getDeg2Probs
     * @return 2nd order probabilities
     */
    public double [][][] getDeg2Probs(){
        return deg2Probs;
    }
    
    /**
     * getProbsCrossSection
     * Returns a 2D array that is a cross section of the complete 3D transition matrix.
     * @param interval - source interval #1
     * @return Cross section corresponding to that source interval
     */    
    public double [][] getProbsCrossSection(int interval){
        return deg2Probs[intervalToIndex(interval)];
    }
    
    //Learning Operations:
    
    /**
     * clearAll
     * Clears all counts, row totals, and probabilities by zeroing them out
     * Use this before learning if you want to start from a clean slate
     * (Learning is cumulative by default)
     */
    public void clearAll(){
        zeroCounts();
        updateProbs();
    }
    
    /**
     * learnFrom
     * Learn transition probabilities from a melody
     * NOTE: learning is cumulative by default.
     * If you do NOT want to add the counts learned to the running total,
     * please call clearAll first to start from a clean slate.
     * @param melody - melody to be learned from
     */
    public void learnFrom(MelodyPart melody){
        
        if(melody == null){
            return;
        }
        
        //make list of just notes (no rests)
        ArrayList<Note> notes = new ArrayList<Note>();
        for(Note n : melody.getNoteList()){
            if(!n.isRest()){
                notes.add(n);
            }
        }
        
        //add to 1st order counts
        for(int i = 0; i<notes.size()-2; i++){//stop at third note from end
            int first = notes.get(i).getPitch();
            int second = notes.get(i+1).getPitch();
            int third = notes.get(i+2).getPitch();
            
            int src = second-first;
            int dest = third-second;
            
            if(inRange(src) && inRange(dest)){
                addCount(intervalToIndex(src), intervalToIndex(dest));
            }
        }
        
        //update 1st order probs
        updateDeg1Probs();
        
        //add to 2nd order counts
        for(int i = 0; i<notes.size()-3; i++){//stop at fourth note from end
            int first = notes.get(i).getPitch();
            int second = notes.get(i+1).getPitch();
            int third = notes.get(i+2).getPitch();
            int fourth = notes.get(i+3).getPitch();
            
            int x = second-first;
            int y = third-second;
            int z = fourth-third;
            
            if(inRange(x) && inRange(y) && inRange(z)){
                addCount(intervalToIndex(x), intervalToIndex(y), intervalToIndex(z));
            }
        }
        
        //update 2nd order probs
        updateDeg2Probs();
    }
    
    /**
     * learnFrom
     * Learn transition probabilities from multiple melodies
     * NOTE: learning is cumulative by default.
     * If you do NOT want to add the counts learned to the running total,
     * please call clearAll first to start from a clean slate.
     * @param melodies - melodies to be learned from
     */
    public void learnFrom(ArrayList<MelodyPart> melodies){
        for(MelodyPart m : melodies){
            learnFrom(m);
        }
    }
    
    /**
     * learnFrom
     * Learns transition probabilities from a file.
     * NOTE: learning is cumulative by default.
     * If you do NOT want to add the counts learned to the running total,
     * please call clearAll first to start from a clean slate.
     * @param f - file to be learned from
     * @throws FileNotFoundException 
     */
    public void learnFrom(File f) throws FileNotFoundException{
        Scanner scan = new Scanner(f);
        //add to 1st order counts
        for(int row = 0; row < deg1Counts.length; row++){
            for(int column = 0; column < deg1Counts[row].length; column++){
                addCounts(scan.nextInt(), row, column);
            }
        }
        //add to 2nd order counts
        for(int x = 0; x < deg2Counts.length; x++){
            for(int y = 0; y < deg2Counts[x].length; y++){
                for(int z = 0; z < deg2Counts[x][y].length; z++){
                    addCounts(scan.nextInt(), x, y, z);
                }
            }
        }
        scan.close();
        updateProbs();
    }
    
    //Private Methods:
    
    //Zeroing Counts:
    
    /**
     * zeroCounts
     * Makes all counts zero
     * (Also zeros out row totals)
     */
    private void zeroCounts(){
        zeroDeg1Counts();
        zeroDeg2Counts();
    }
    
    /**
     * zeroDeg1Counts
     * zeros out 1st order counts and row totals
     */
    private void zeroDeg1Counts(){
        for(int row = 0; row < deg1Counts.length; row++){
            for(int column = 0; column < deg1Counts[row].length; column++){
                deg1Counts[row][column] = 0;
            }
            deg1Totals[row] = 0;
        }
    }
    
    /**
     * zeroDeg2Counts
     * zeros out 2nd order counts and row totals
     */
    private void zeroDeg2Counts(){
        for(int x = 0; x < deg2Counts.length; x++){
            for(int y = 0; y < deg2Counts[x].length; y++){
                for(int z = 0; z < deg2Counts[x][y].length; z++){
                    deg2Counts[x][y][z] = 0;
                }
                deg2Totals[x][y] = 0;
            }
        }
    }
    
    //Adding to Counts:
    
    /**
     * addCount
     * Add 1 to the count located at row, column in deg1Counts
     * (also updates row totals)
     * @param row - row of count to be added to
     * @param column - column of count to be added to
     */
    private void addCount(int row, int column){
        addCounts(ONE, row, column);
    }
    
    /**
     * Add 1 to the count located at x, y, z in deg2Counts
     * (also updates row totals)
     * @param x - 1st coordinate of count to be added to
     * @param y - 2nd coordinate of count to be added to
     * @param z - 3rd coordinate of count to be added to
     */
    private void addCount(int x, int y, int z){
        addCounts(ONE, x, y, z);
    }
    
    /**
     * addCounts
     * Add a number to the current count stored at row, column in deg1Counts
     * (also update the row total)
     * @param number - number to add
     * @param row - row where count is located
     * @param column - column where count is located
     */
    private void addCounts(int number, int row, int column) {
        deg1Counts[row][column] += number;
        deg1Totals[row] += number;
    }
    
    /**
     * addCounts
     * Add a number to the current count stored at row, column in deg2Counts
     * @param number - number to add
     * @param x - x coordinate of count
     * @param y - y coordinate of count
     * @param z - z coordinate of count
     */
    private void addCounts(int number, int x, int y, int z){
        deg2Counts[x][y][z] += number;
        deg2Totals[x][y] += number;
    }
    
    //Updating Probabilities:
    
    /**
     * updateProbs
     * update probability matrices based on row totals
     */
    private void updateProbs(){
        updateDeg1Probs();
        updateDeg2Probs();
    }

    /**
     * updateDeg1Probs
     * Update the 2st order probabilities based on counts and row totals.
     */
    private void updateDeg1Probs(){

        //fill probability table
        for(int row = 0; row < deg1Counts.length; row++){
            int denominator = deg1Totals[row];
            if(denominator!=0){
                //assign probabilities based on counts
                for(int cell = 0; cell < deg1Counts[row].length; cell++){
                    deg1Probs[row][cell] = (double)deg1Counts[row][cell]/(double)denominator;
                }
            }
            else{
                //no data: make all destination intervals equally likely
                for(int cell = 0; cell < deg1Counts[row].length; cell++){
                    deg1Probs[row][cell] = 1.0/(double)deg1Counts[row].length;
                }
            }
        }
    }
    
    /**
     * updateDeg2Probs
     * Update the 2nd order probabilities based on counts and row totals.
     */
    private void updateDeg2Probs(){
        for(int x = 0; x < deg2Probs.length; x++){
            for(int y = 0; y < deg2Probs[x].length; y++){
                int denominator = deg2Totals[x][y];
                if(denominator != 0){
                    for(int z = 0; z < deg2Counts[x][y].length; z++){
                        deg2Probs[x][y][z] = (double)deg2Counts[x][y][z] / (double)denominator;
                    }
                }else{
                    //no data: make all destination intervals equally likely
                    for(int z = 0; z < deg2Counts[x][y].length; z++){
                        deg2Probs[x][y][z] = 1.0/(double)intervals;
                    }
                }
            }
        }
    }

    //Utilities:
    
    /**
     * inRange
     * Tells whether an interval is within the range of intervals we can learn from.
     * @param interval - interval to be tested
     * @return true if the interval is within the range, false otherwise
     */
    private boolean inRange(int interval){
        return Math.abs(interval) <= range;
    }
    
    /**
     * indexToInterval
     * Converts an array index to an interval by subtracting range
     * @param index
     * @return 
     */
    private static int indexToInterval(int index){
        return index - range;
    }
    
    /**
     * intervalToIndex
     * Converts a directional interval to an array index by adding range
     * @param interval - interval to be converted
     * @return corresponding array index
     */
    private static int intervalToIndex(int interval){
        return interval + range;
    }
    
    //Best Pitch methods (For use in Trading):
    
    /**
     * bestPitch
     * Finds a likely pitch to go to next given a list of notes
     * If the note list is less than 2 elements, return 0.
     * If the note list is two elements long, return a pitch given by the 1st order Markov Chain.
     * If the note list is at least three elements long, return a pitch given by the 2nd order Markov Chain.
     * @param notes - list of notes
     * @return likely pitch to go to next
     */
    public int bestPitch(LinkedList<Integer> notes){
        if (notes.size() < 2){
            return 0;
        } else if (notes.size() > 2){
            int note3 = notes.removeLast();
            int note2 = notes.removeLast();
            int note1 = notes.removeLast();
            return bestPitch((note3 - note2), (note2 - note1), note3);
        } else {
            int note2 = notes.removeLast();
            int note1 = notes.removeLast();
            return bestPitch((note2 - note1), note2);
        }
    }

    /**
     * best pitch
     * Returns the best pitch to go to next based on a 1st order Markov Chain
     * @param prevInterval - interval we're coming from
     * @param prevPitch - pitch we're coming from
     * @return best pitch to travel to next
     */
    public int bestPitch(int prevInterval, int prevPitch){
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        int sourceIndex = intervalToIndex(prevInterval);
        for(int destIndex = 0; destIndex < deg1Probs[sourceIndex].length; destIndex ++){
            double prob = deg1Probs[sourceIndex][destIndex];
            int pitchToAdd = prevPitch + indexToInterval(destIndex);
            if(prob != 0){
                pitches.add(pitchToAdd);
                pitchProbs.add(prob);
            }
        }

        Random r = new Random();
        double decision = r.nextDouble();
        double totalProb = 0;
        
        int bestPitch = pitches.get(0);
        
        for(int i = 0; i < pitchProbs.size(); i++){
            totalProb += pitchProbs.get(i);
            if(totalProb > decision){
                bestPitch = pitches.get(i);
                break;
            }
        }
        
        return bestPitch;
    }
    
    /**
     * best Pitch
     * Returns the best pitch to go to next based on a 2nd order Markov Chain
     * @param prevInterval1 - source interval #1
     * @param prevInterval2 - source interval #2
     * @param prevPitch - pitch we're coming from
     * @return best pitch to travel to next
     */
    public int bestPitch(int prevInterval1, int prevInterval2, int prevPitch){
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        int x = intervalToIndex(prevInterval1);
        int y = intervalToIndex(prevInterval2);
        for(int z = 0; z < deg2Probs[x][y].length; z ++){
            double prob = deg2Probs[x][y][z];
            int pitchToAdd = prevPitch + indexToInterval(z);
            if(prob != 0){
                pitches.add(pitchToAdd);
                pitchProbs.add(prob);
            }
        }
        
        Random r = new Random();
        double decision = r.nextDouble();
        double totalProb = 0;
        
        int bestPitch = pitches.get(0);
        
        for(int i = 0; i < pitchProbs.size(); i++){
            totalProb += pitchProbs.get(i);
            if(totalProb > decision){
                bestPitch = pitches.get(i);
                break;
            }
        }
        
        return bestPitch;
    }
    
}
