/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import java.util.ArrayList;
import imp.data.*;
/**
 *
 * @author muddCS15
 */
public class SkipTrend extends Trend{
    
    //intervals
    private static final int MINOR_THIRD = 3;
    private static final int MAJOR_THIRD = 4;
    
    //directional distance between two notes
    public static int dist(Note n1, Note n2){
        return n2.getPitch() - n1.getPitch();
    }
    
    //absolute distance between two notes
    public static int absDist(Note n1, Note n2){
        return Math.abs(dist(n1, n2));
    }

    //moving a skip in either direction continues the trend
    public boolean stopCondition(Note n1, Note n2) {
        int absDist = absDist(n1, n2);
        return ! (absDist == MINOR_THIRD || absDist == MAJOR_THIRD);
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //TODO
    public ArrayList<Note> importantNotes(ArrayList<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
