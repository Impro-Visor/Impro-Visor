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
    
    public NoteChordPair copy(){
        return new NoteChordPair(note.copy(), chord.copy(), slot, var);
    }
    
    public NoteChordPair(Note note, Chord chord, int slot){
        this(note, chord, slot, NO_VAR);
    }
    
    public NoteChordPair(Note note, Chord chord){
        this(note, chord, 0, NO_VAR);
    }
    
    public Polylist toNCP(){
        Polylist list = Polylist.PolylistFromString("");
        list = list.addToEnd(note).addToEnd(chord).addToEnd(slot);
        
        if(var != NO_VAR){
            list = list.addToEnd("n"+var);
        }
        
        return list;
        
    }
    
    public Note getNote(){
        return note;
    }
    
    public Chord getChord(){
        return chord;
    }
    
    public int getSlot(){
        return slot;
    }
    
    public void setSlot(int slot){
        this.slot = slot;
    }
    
    public int getVar(){
        return var;
    }
    
    public void setVar(int var){
        this.var = var;
    }
    
    public int getDuration(){
        return note.getRhythmValue();
    }
    
    public void setDuration(int duration){
        note.setRhythmValue(duration);
    }
    
//    @Override
//    public String toString(){
//        return "NCP: Note: "+note.getPitchClassName()+"; Chord: "+chord.getName()+"; Slot: "+slot+"; Var: "+var+".";
//    }
    
    @Override
    public String toString(){
        return "NCP:\t"+note+"\t"+chord+"\tSLOT: "+slot+"\tVAR: "+var;
    }
    
    public String varName(){
        return "n"+var;
    }
    
}
