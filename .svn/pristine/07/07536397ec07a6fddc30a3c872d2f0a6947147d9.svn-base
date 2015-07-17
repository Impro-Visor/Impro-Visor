/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import java.util.ArrayList;
import imp.data.*;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.TrendSegment;
/**
 *
 * @author muddCS15
 */
public class ChromaticTrend extends Trend{
    
    //intervals
    private static final int HALF_STEP = 1;

    //same pitch or half step away in either direction continues the trend
    public boolean stopCondition(Note n1, Note n2) {
        return absDist(n1, n2) > HALF_STEP;
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //priority most important, then strong beat, then duration
    public double[] weights() {
        double [] weights = {1, .5, .25};
        return weights;
    }

    //2 for now - will change
    public int numberOfSections() {
        return 1;
    }

    public String getName() {
        return "CHROMATIC";
    }

    
}
