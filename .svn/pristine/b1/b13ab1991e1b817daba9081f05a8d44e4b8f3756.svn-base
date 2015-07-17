/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;
import imp.data.*;
import imp.lickgen.transformations.TrendDetector;
import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public abstract class Trend {
    
    //stop condition based on interval and note's role in chord
    public boolean stopCondition(Note n1, Note n2, Chord c){
        
        //if no chord or one of the notes absent or rest, stop the trend
        if(c.isNOCHORD() || n1 == null || n1.isRest() || n2 == null || n2.isRest()){
            return true;
        }
        
        //previous note was not part of trend. n2 could start a new trend.
        if(n1 == TrendDetector.NOT_IN_TREND){
            return stopCondition(n2, c);
        }
        
        //return whether a stop condition is satisfied
        return stopCondition(n1, n2) || stopCondition(n2, c);
    }
    
    //stop condition based on interval
    public abstract boolean stopCondition(Note n1, Note n2);
    
    //stop condition based on note's role in the chord
    public abstract boolean stopCondition(Note n, Chord c);
    
    //method to extract important / outline notes from the identified trend
    public abstract ArrayList<Note> importantNotes(ArrayList<Note> notes);
    
}
