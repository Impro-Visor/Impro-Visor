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

package imp.data.advice;

import imp.data.Key;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.Part;

/**
 * Describes and contains a piece of Advice that is only one Note
 * for insertion, has mechanisms to turn Advice into practical form that can
 * be inserted into Score.
 * @see         Advice
 * @see         Advisor
 * @author      Stephen Jones
 */
public class AdviceForNote extends Advice {

    /**
     * the note to insert
     */
    private String advice;
    private int[] metre = new int[2];
    
    /**
     * Creates a new piece of Advice.
     * @param name      a String containing the display name for the Advice
     */
    public AdviceForNote(NoteSymbol noteSymbol, int[] metre) {
        super((noteSymbol.getPitchString()));	// strip duration and octave from symbol
        this.advice = noteSymbol.getPitchClass().toString();
        this.metre[0] = metre[0];
        this.metre[1] = metre[1];
    }

    /**
     * Converts the Advice into a Part and returns that.
     * @return Part     the Advice in Part form, ready to be inserted
     */
    public Part getPart() {
        int beatValue = ((BEAT*4)/metre[1]);
        Part newPart = new Part();
        newPart.setMetre(metre[0], metre[1]);
        Note note = Key.makeNote(advice, C4, beatValue/2);

        note.setRhythmValue(beatValue/2);

        newPart.addUnit(note);
        
// rk: Playing is now done in PasteCommand

//          {
//          Command playNote = new PlayNoteCommand(note);
//          playNote.execute();
//          }
        
        return newPart;
    }
}
