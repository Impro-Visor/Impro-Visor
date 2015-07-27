/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.Constants;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Mikayla Konst 2015
 */
public class IntervalLearner {
    
    private final MelodyPart melody;
    
    //can go up to an octave in either direction, or stay on the same note
    private static final int range = Constants.OCTAVE;
    public static final int intervals = 2*range+1;
    private static final boolean debug = false;
    
    public IntervalLearner(MelodyPart melody){
        this.melody = melody;
    }
    
    public double[][] probabilities(){
        int [] [] counts = counts();
        if(debug){
            System.out.println("Counts:");
            printArray(counts);
        }
        
        double [] [] probabilities = probabilities(counts);
        if(debug){
            System.out.println("Probabilities:");
            printArray(probabilities);
        }
        
        return probabilities;
    }
    
    public double[][][] probabilities2(){
        return probabilities(counts2());
    }
    
    public static double[][][] probabilities(int [] [] [] counts){
        
        //initialize probabilities to zero
        double [][][] probabilities = new double[intervals][intervals][intervals];
        for(double[][] x : probabilities){
            for(double [] y : x){
                for(int z = 0; z<y.length; z++){
                    y[z] = 0;
                }
            }
        }
        
        //initialize count totals to zero
        int [][] totals = new int [intervals][intervals];
        for(int [] x : totals){
            for(int y = 0; y < x.length; y++){
                x[y] = 0;
            }
        }
        
        //fill up totals grid
        for(int x = 0; x < counts.length; x++){
            for(int y = 0; y < counts[x].length; y++){
                for(int z = 0; z < counts[y].length; z++){
                    totals[x][y] += counts[x][y][z];
                }
            }
        }
        
        //fill probability table
        
        for(int x = 0; x < probabilities.length; x++){
            for(int y = 0; y < probabilities[x].length; y++){
                if(totals[x][y] != 0){
                    for(int z = 0; z < counts[x][y].length; z++){
                        probabilities[x][y][z] = (double)counts[x][y][z] / (double)totals[x][y];
                    }
                }else{
                    //no data: make all destination intervals equally likely
                    for(int z = 0; z < counts[x][y].length; z++){
                        probabilities[x][y][z] = 1.0/(double)probabilities[x][y].length;
                    }
                }
            }
        }

        //return probability table
        return probabilities;
    }
    
    public static double[][] probabilities(int [] [] counts){
        
        //initialize probabilities to zero
        double[][] probabilities = new double[intervals][intervals];
        for (double[] row : probabilities) {
            for (int cell = 0; cell < row.length; cell++) {
                row[cell] = 0;
            }
        }
        
        //initialize totals to zero
        int[] totals = new int[counts.length];
        for(int i = 0; i<totals.length; i++){
            totals[i] = 0;
        }
        
        //get row totals
        for(int row = 0; row < counts.length; row++){
            for(int cell = 0; cell<counts[row].length; cell++){
                totals[row] += counts[row][cell];
            }
        }
        
        //fill probability table
        for(int row = 0; row < counts.length; row++){
            if(totals[row]!=0){
                //assign probabilities based on counts
                for(int cell = 0; cell<probabilities[row].length; cell++){
                    probabilities[row][cell] = (double)counts[row][cell]/(double)totals[row];
                }
            }
            else{
                //no data: make all destination intervals equally likely
                for(int cell = 0; cell<probabilities[row].length; cell++){
                    probabilities[row][cell] = 1.0/(double)probabilities[row].length;
                }
            }
        }
        
        //return probability table
        return probabilities;
    }
    
    //2nd order Markov Chain
    public int[][][] counts2(){
        
        //initialize counts to 0
        int[][][] counts = new int[intervals][intervals][intervals];
        for(int [][] x : counts){
            for(int [] y : x){
                for(int z = 0; z < y.length; z++){
                    y[z] = 0;
                }
            }
        }
        
        //make list of just notes (no rests)
        ArrayList<Note> notes = new ArrayList<Note>();
        for(Note n : melody.getNoteList()){
            if(!n.isRest()){
                notes.add(n);
            }
        }
        
        //stop at fourth note from end
        for(int i = 0; i<notes.size()-3; i++){
            int first = notes.get(i).getPitch();
            int second = notes.get(i+1).getPitch();
            int third = notes.get(i+2).getPitch();
            int fourth = notes.get(i+3).getPitch();
            
            int x = second-first;
            int y = third-second;
            int z = fourth-third;
            
            if(inRange(x) && inRange(y) && inRange(z)){
                counts[intervalToIndex(x)][intervalToIndex(y)][intervalToIndex(z)]++;
            }
        }
        return counts;
    }
    
    public int[][] counts(){
        
        //initialize counts to zero
        int[][] counts = new int[intervals][intervals];
        for (int [] row : counts) {
            for (int j = 0; j < row.length; j++) {
                row[j] = 0;
            }
        }
        
        //make list of just notes (no rests)
        ArrayList<Note> notes = new ArrayList<Note>();
        for(Note n : melody.getNoteList()){
            if(!n.isRest()){
                notes.add(n);
            }
        }
        
        //stop at third note from end
        for(int i = 0; i<notes.size()-2; i++){
            int first = notes.get(i).getPitch();
            int second = notes.get(i+1).getPitch();
            int third = notes.get(i+2).getPitch();
            
            int src = second-first;
            int dest = third-second;
            
            if(inRange(src) && inRange(dest)){
                counts[intervalToIndex(src)][intervalToIndex(dest)]++;
            }
        }
        return counts;
    }
    
    private boolean inRange(int interval){
        return Math.abs(interval) <= range;
    }
    
    public static int intervalToIndex(int interval){
        return interval + range;
    }
    
    public static String arrayToString(double [][] array){
        String result = "";
        for(double [] row : array){
            result += rowToString(row);
        }
        return result;
    }
    
    private static String rowToString(double [] row){
        String result = "";
        DecimalFormat df = new DecimalFormat("#.##");
        for(double d : row){
            result += df.format(d)+"\t";
        }
        return result+"\n";
    }
    
    private static void printArray(double [][] array){
        for(double [] row : array){
            printRow(row);
        }
    }
    
    private static void printRow(double [] row){
        for(double cell : row){
            System.out.print(cell+"\t");
        }
        System.out.println();
    }
    
    private static void printArray(int [][] array){
        for(int [] row : array){
            printRow(row);
        }
    }
    
    private static void printRow(int [] row){
        for(int cell : row){
            System.out.print(cell+"\t");
        }
        System.out.println();
    }
    
    /**
     * First Order - best pitch
     * @param prevInterval
     * @param prevPitch
     * @param learner
     * @return 
     */
    public int bestPitch(int prevInterval, int prevPitch){
        double [][] probs = probabilities();
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        int sourceIndex = intervalToIndex(prevInterval);
        for(int destIndex = 0; destIndex < probs[sourceIndex].length; destIndex ++){
            double prob = probs[sourceIndex][destIndex];
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
    
    public int bestPitch(int prevInterval1, int prevInterval2, int prevPitch){
        double [][][] probs = probabilities2();
        
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        int x = intervalToIndex(prevInterval1);
        int y = intervalToIndex(prevInterval2);
        for(int z = 0; z < probs[x][y].length; z ++){
            double prob = probs[x][y][z];
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
    
    private static int indexToInterval(int index){
        return index - Constants.OCTAVE;
    }
    
}
