/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import java.util.ArrayList;
import imp.data.*;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.Scorer;
import imp.lickgen.transformations.TrendSegment;
/**
 *
 * @author muddCS15
 */
public class AscendingTrend extends Trend{

    private static final int WHOLE_STEP = 2;
    
    //staying on the same note is okay (not strictly ascending)
    public static boolean ascending(Note n1, Note n2){
        return dist(n1, n2) >= 0;
    }

    //trend continues so long as direction is ascending
    //and motion is stepwise
    public boolean stopCondition(Note n1, Note n2) {
        return !ascending(n1, n2) && absDist(n1, n2) > WHOLE_STEP;
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //two sections for ascending
    public int numberOfSections() {
        return 1;
    }

    //equal weights
    public double[] weights() {
        double [] weights = {1, 5, 1};
        return weights;
    }

    public String getName() {
        return "ASCENDING";
    }
    
}
