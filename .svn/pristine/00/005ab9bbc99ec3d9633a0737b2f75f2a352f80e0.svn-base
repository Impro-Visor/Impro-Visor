/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author muddCS15
 */
public class DiatonicTrend implements Trend{

    //check all three stop conditions
    public boolean stopCondition(Note n1, Note n2, Chord c) {
        return stopCondition(n1, n2) || stopCondition(n1, c) || stopCondition(n2, c);
    }

    //doesn't matter what the interval is
    public boolean stopCondition(Note n1, Note n2) {
        return false;
    }

    //returns whether a note belongs to a chord's primary scale
    public boolean diatonic(Note n, Chord c){
        
        if(n.isRest() || c.isNOCHORD()){
            return false;
        }
        
        Polylist firstScale = c.getFirstScale();
        if(firstScale == null){
            return false;
        }
        
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        return ns.enhMember(firstScale);
    }
    
    //trend continues so long as notes belong to chord's first scale
    public boolean stopCondition(Note n, Chord c) {
        return !diatonic(n, c);
    }

    //TODO
    public ArrayList<Note> importantNotes(ArrayList<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
