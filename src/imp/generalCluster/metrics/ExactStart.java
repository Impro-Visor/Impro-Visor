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
public class ExactStart extends Metric{
    
    
    public ExactStart(double weight){
        super(weight,"exactStart", true);
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
    
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = (double) getStartBeat(exactMelody);
        return value;
    }
    
}
