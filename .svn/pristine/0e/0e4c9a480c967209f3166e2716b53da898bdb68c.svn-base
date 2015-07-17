/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public class ChromaticTrend implements Trend{
    
    //intervals
    private static final int HALF_STEP = 1;
    
    //directional distance between two notes
    public static int dist(Note n1, Note n2){
        return n2.getPitch() - n1.getPitch();
    }
    
    //absolute distance between two notes
    public static int absDist(Note n1, Note n2){
        return Math.abs(dist(n1, n2));
    }

    //same pitch or half step away in either direction continues the trend
    public boolean stopCondition(Note n1, Note n2) {
        return absDist(n1, n2) > HALF_STEP;
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //TODO
    public ArrayList<Note> importantNotes(ArrayList<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //check all three stop conditions
    public boolean stopCondition(Note n1, Note n2, Chord c) {
        return stopCondition(n1, n2) || stopCondition(n1, c) || stopCondition(n2, c);
    }
    
}
