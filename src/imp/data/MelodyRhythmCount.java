/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import java.io.Serializable;

/**
 *
 * @author cssummer17
 */
public class MelodyRhythmCount implements Serializable {
    private int[] durationFrequencies;
    private double mostFreqDuration;
    private double diversityIndex;
    private double longestNoteLength;
    private double longestRestLength;
    
   
    
   
    
    public MelodyRhythmCount(int[] durationFreq, double mostFrequentDuration, double diversityIndex, double longestRhythm, double longestRestLength){
        this.durationFrequencies = durationFreq;
        this.mostFreqDuration = mostFrequentDuration;
        this.diversityIndex = diversityIndex;
        this.longestNoteLength = longestRhythm;
        this.longestRestLength = longestRestLength;
    }
    
    public int[] getDurationFrequencies(){
        return durationFrequencies;
    }
    
    public double getMostFrequentDuration(){
        return mostFreqDuration;
    }
   
    public double getDiversityIndex(){
        return diversityIndex;
    }
    
    public double getLongestNoteLength(){
        return longestNoteLength;
    }
    
    public double getLongestRestLength(){
        return longestRestLength;
    }
    
    
}
