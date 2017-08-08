/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.generalCluster.metrics;

import imp.data.MelodyRhythmCount;
import imp.data.Note;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;

/**
 *
 * @author Cai Glencross
 */
public class MostFreqDuration extends Metric{
    
    public MostFreqDuration(float weight){
        super(weight, "mostFrequentDuration", true);
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
        float longestRhythm = Integer.MIN_VALUE;
        float longestRest = Integer.MIN_VALUE;
        
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
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getMelodyRhythmCount(exactMelody, getStartBeat(exactMelody)).getMostFrequentDuration();
        return this.value;
    }  
}
