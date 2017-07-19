/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics;

import imp.data.MelodyRhythmCount;
import imp.data.Note;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class LongestNoteLength extends Metric{
    
    public LongestNoteLength(double weight){
        super(weight, "longestNoteLength", true);
    }
    
    private int getStartBeat(IndexedMelodyPart p) {
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
    
     private MelodyRhythmCount getMelodyRhythmCount(IndexedMelodyPart indexMel, int exactStartBeat){
        int currentSlot = exactStartBeat;
        //int lastSlot = indexMel.getLastNoteIndex();
        int[] durationFrequencies = new int[819];
        int prime = 821;
        float mostFreqDuration = 0;
        int numNotes = 0;
        float longestRhythm = 0;
        float longestRest = 0;
        
        
        while(indexMel.getCurrentNote(currentSlot) != null){
            Note note = indexMel.getCurrentNote(currentSlot);
            float rhythm = note.getRhythmValue();
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
        
        float diversityIndex = ( (float) durationFrequencies[(int) mostFreqDuration % prime] ) / numNotes;
        
     
        return new MelodyRhythmCount(durationFrequencies, mostFreqDuration, diversityIndex, longestRhythm, longestRest);
    }
    
     public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getMelodyRhythmCount(exactMelody, getStartBeat(exactMelody)).getLongestNoteLength();
        return this.value;
    }
    
}
