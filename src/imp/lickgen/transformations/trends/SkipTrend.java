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
public class SkipTrend extends Trend{
    
    //intervals
    private static final int MINOR_THIRD = 3;
    private static final int MAJOR_THIRD = 4;

    //moving a skip in either direction continues the trend
    public boolean stopCondition(Note n1, Note n2) {
        int absDist = absDist(n1, n2);
        return ! (absDist == MINOR_THIRD || absDist == MAJOR_THIRD);
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //equal weights
    public double[] weights() {
        double [] weights = {1, 1, 1};
        return weights;
    }

    //2 for now - will change
    public int numberOfSections() {
        return 2;
    }

    public String getName() {
        return "SKIP";
    }

}
