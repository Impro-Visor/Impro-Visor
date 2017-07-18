/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics;

import imp.data.Note;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class Syncopation extends Metric{
    
    public Syncopation(double weight){
        super(weight, "syncopation", true);
    }
    
    private double getSyncopation(IndexedMelodyPart indexMel, int exactStartBeat){
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
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getSyncopation(exactMelody, getStartBeat(exactMelody));
        return this.value;
    }
}
