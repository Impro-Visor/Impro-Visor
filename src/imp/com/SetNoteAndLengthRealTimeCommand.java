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

package imp.com;

import imp.Constants;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.gui.Notate;
import imp.util.Trace;

/**
 * An undoable Command that places a Note at a certain position in a MelodyPart.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Stephen Jones
 */
public class SetNoteAndLengthRealTimeCommand implements Command, Constants {

    /**
     * the MelodyPart in which to place the Note
     */
    private MelodyPart melodyPart;

    /**
     * the Note to place in the MelodyPart
     */
    private Note note;

    /**
     * the index at which to place the Note
     */
    private int slotIndex;

    /**
     * true if the setNote call inserted a Rest to stop auto-expansion
     */
    private boolean restInserted = false;


    /**
     * the index to insert a rest if a rest is inserted
     */
    private int stopIndex;
    
    /**
     * true since this Command can be undone
     */
    private boolean undoable = false;

    /**
     * the Note that used to be at the specified position
     */
    private Note oldNote;
    
    private Notate notate;
    
    /**
     * Creates a new Command that can set the Note in a MelodyPart at the
     * specified indices.
     * @param sI        the index of the Note to place
     * @param nte       the Note to place
     * @param prt       the MelodyPart in which to place the Note
     * @param play      play the note
     * @param notate    parent frame, used to elongate score if needed
     */
    public SetNoteAndLengthRealTimeCommand(int sI, Note nte, MelodyPart prt, Notate notate) {
        melodyPart = prt;
        note = nte;
        slotIndex = sI;
        this.notate = notate;
    }

    /**
     * Places the Note at the specified position in the MelodyPart.
     */
    public void execute() {
        Trace.log(2, "executing SetNoteAndLengthCommand, slotIndex = " + slotIndex);

        int[] metre = melodyPart.getMetre();
        int beatValue = (WHOLE/metre[1]);
        int measureLength = metre[0] * beatValue;
        
        if(note != null && note.nonRest()) 
          {
            stopIndex = (slotIndex/measureLength + 2) * measureLength;

          }
        oldNote = melodyPart.getNote(slotIndex);
        melodyPart.setNoteAndLength(slotIndex, note, notate);
    }

    /**
     * Replaces the previously placed Note with whatever used to be there.
     */
    public void undo() {

    }

    /**
     * Places the note at the specified position in the melodyPart.
     */
    public void redo() {

    }

    public boolean isUndoable() {
        return false;
    }
}
