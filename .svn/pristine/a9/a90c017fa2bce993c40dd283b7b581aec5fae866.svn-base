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

import imp.*;
import imp.data.*;
import imp.util.Trace;

/**
 * An undoable Command that can shift a contiguous set of chords up or down
 * a specified amount.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         ChordPart
 * @author      Stephen Jones
 */
public class ShiftChordsCommand implements Command, Constants {

    /**
     * the first slot to shift
     */
    int startIndex;

    /**
     * the last slot to shift
     */
    int stopIndex;

    /**
     * the amount to shift the pitch
     */
    int shift;

    /**
     * the key signature of the part containing the note
     */
    Key key;

    /**
     * the part in which to shift chords
     */
    ChordPart part;

    /**
     * true since this Command can be undone
     */
    boolean undoable = true;
    
    /**
     * Creates a new Command that can shift chords of a set of Notes.
     */
    public ShiftChordsCommand(int shift, ChordPart part, int startIndex, 
                               int stopIndex, 
                               Key key) {
        this.startIndex = startIndex;
        this.part = part;
        this.stopIndex = stopIndex;
        this.shift = shift;
        this.key = key;
    }

    /**
     * Executes the shifts.
     */
    public void execute() {
        Trace.log(2, "executing ShiftChordsCommand");
        for(int i = startIndex; i <= stopIndex; i++) {
            Chord chord = part.getChord(i);
            if(chord != null && !chord.getName().equals(NOCHORD))
                chord.transpose(shift);
//                chord.setName(Key.transposeChord(chord.getName(), shift, key));
                }
    }

    /**
     * Undoes the shifts.
     */
    public void undo() {
        shift = -shift;
        execute();
    }

    /**
     * Redoes the shifts.
     */
    public void redo() {
        undo();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
