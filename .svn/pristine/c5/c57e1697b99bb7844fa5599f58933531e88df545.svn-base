/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.Constants;
import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public class ArpeggioTrend implements Trend{

    //check all three stop conditions
    public boolean stopCondition(Note n1, Note n2, Chord c) {
        return stopCondition(n1, n2) || stopCondition(n1, c) || stopCondition(n2, c);
    }

    //doesn't matter what the interval is
    public boolean stopCondition(Note n1, Note n2) {
        return false;
    }

    //returns whether a note is a chord tone of a chord (false if chord is no chord or note is rest)
    public static boolean chordTone(Note n, Chord c){
        return !c.isNOCHORD() && !n.isRest() && c.getTypeIndex(n) == Constants.CHORD_TONE;
    }
    
    //trend continues as long as the notes are chord tones
    public boolean stopCondition(Note n, Chord c) {
        return !chordTone(n, c);
    }

    //TODO
    public ArrayList<Note> importantNotes(ArrayList<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
