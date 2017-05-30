/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lickgen.transformations;
import imp.data.*;
import imp.lickgen.NoteConverter;
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
    
    public String getRelativePitch()
    {
        if(note.isRest()){
            return "rest";
        }
        else if(chord.isNOCHORD()){
            return "none";
        }
        else{
            return (String)NoteConverter.noteToRelativePitch(note, chord).second();
        }
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
