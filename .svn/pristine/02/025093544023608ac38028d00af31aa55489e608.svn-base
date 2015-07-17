/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations;
import imp.data.*;
import polya.Polylist;

/**
 *
 * @author muddCS15
 */
public class NoteChordPair {
    
    private static final int NO_VAR = -1;
    
    private Note note;
    private Chord chord;
    private int slot;
    private int var;
    
    public NoteChordPair(Note note, Chord chord, int slot, int var){
        this.note = note;
        this.chord = chord;
        this.slot = slot;
        this.var = var;
    }
    
    public NoteChordPair(Note note, Chord chord, int slot){
        this(note, chord, slot, NO_VAR);
    }
    
    public Polylist toNCP(){
        Polylist list = Polylist.PolylistFromString("");
        list = list.addToEnd(note).addToEnd(chord).addToEnd(slot);
        
        if(var != NO_VAR){
            list = list.addToEnd("n"+var);
        }
        
        return list;
        
    }
    
    public String toString(){
        return "NCP: Note: "+note.getPitchClassName()+"; Chord: "+chord.getName()+"; Slot: "+slot+"; Var: "+var+".";
    }
    
}
