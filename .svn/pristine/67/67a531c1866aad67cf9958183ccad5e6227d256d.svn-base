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
public interface Trend {
    
    //stop condition based on interval and note's role in chord
    public boolean stopCondition(Note n1, Note n2, Chord c);
    
    //stop condition based on interval
    public boolean stopCondition(Note n1, Note n2);
    
    //stop condition based on note's role in the chord
    public boolean stopCondition(Note n, Chord c);
    
    //method to extract important / outline notes from the identified trend
    public ArrayList<Note> importantNotes(ArrayList<Note> notes);
    
}
