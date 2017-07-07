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
    private float mostFreqDuration;
    private float diversityIndex;
    private float longestNoteLength;
    private float longestRestLength;
    
   
    
   
    
    public MelodyRhythmCount(int[] durationFreq, float mostFrequentDuration, float diversityIndex, float longestRhythm, float longestRestLength){
        this.durationFrequencies = durationFreq;
        this.mostFreqDuration = mostFrequentDuration;
        this.diversityIndex = diversityIndex;
        this.longestNoteLength = longestRhythm;
        this.longestRestLength = longestRestLength;
    }
    
    public int[] getDurationFrequencies(){
        return durationFrequencies;
    }
    
    public float getMostFrequentDuration(){
        return mostFreqDuration;
    }
   
    public float getDiversityIndex(){
        return diversityIndex;
    }
    
    public float getLongestNoteLength(){
        return longestNoteLength;
    }
    
    public float getLongestRestLength(){
        return longestRestLength;
    }
    
    
}
