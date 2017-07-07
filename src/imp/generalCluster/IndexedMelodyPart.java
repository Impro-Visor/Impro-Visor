/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.generalCluster;

import imp.data.MelodyPart;
import imp.data.Note;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Jon Gillick
 */
public class IndexedMelodyPart extends MelodyPart implements Serializable{
    
    //The index where this part originally appeared in a song
    private int index;
    
    public IndexedMelodyPart() {
        super();
    }
    
    public IndexedMelodyPart(int start) {
        super();
        index = start;
    }

    /*create a melody part from an array of strings, which has the index in the
     * first position, followed by a pitch and then a duration in subsequent
     * positions
     */
    public IndexedMelodyPart(String[] melody) {
        super();
        index = Integer.parseInt(melody[1]);
        
        
        
        for(int i = 2; i < melody.length; i += 2) {
            int pitch = Integer.parseInt(melody[i]);
            int duration = Integer.parseInt(melody[i+1]);
            Note n;
            if(pitch == -1)
                n = Note.makeRest(duration);
            else
                n = new Note(Integer.parseInt(melody[i]), Integer.parseInt(melody[i+1]));
            this.addNote(n);
        }
        
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int start) {
        index = start;
    }

    public int getPitchSounding(int ind) {
        //if there is a note struck at the index, return its pitch
        Note curr = this.getNote(ind);
        if(curr != null) 
            return curr.getPitch();
        
        //otherwise look for the last pitch
        int prevIndex = 0;
        try{
         prevIndex = this.getPrevIndex(ind);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        Note prevNote = this.getNote(prevIndex);
        if(prevNote.isRest()) return -1; 
        //if previous note is still sounding, return its pitch
        if(prevNote.getRhythmValue() > (ind - prevIndex))
            return prevNote.getPitch();
        //default case
        return -1;
    }
    
    
    public String toString(){
        String rtn = "";
        LinkedList<Note> notes = this.getNotes();
        int i = this.getFirstIndex();
        
        while(this.getCurrentNote(i) != null){
            rtn += "Note: " + this.getCurrentNote(i).toString();
            i = this.getNextIndex(i);
        }
        
        return rtn;
    }
    
}
