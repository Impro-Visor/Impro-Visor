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
 * An undoable Command that places a Chord at a certain position in a ChordPart.
 * @see         Command
 * @see         CommandManager
 * @see         Chord
 * @see         ChordPart
 * @author      Stephen Jones
 */
public class SetChordCommand implements Command, Constants {

    /**
     * the ChordPart in which to place the Chord
     */
    private ChordPart chordProg;

    /**
     * the Chord to place in the ChordPart
     */
    private Chord chord;

    /**
     * the index at which to place the Chord
     */
    private int slotIndex;
    
    /**
     * true since this Command can be undone
     */
    private boolean undoable = true;

    /**
     * the Chord that used to be at the specified position
     */
    private Chord oldChord;
    
    /**
     * Creates a new Command that can set the Chord in a ChordPart at the
     * specified indices.
     * @param sI        the index of the Chord to place
     * @param crd       the Chord to place
     * @param prt       the ChordPart in which to place the Chord
     */
    public SetChordCommand(int sI, Chord crd, ChordPart prt) {
        chordProg = prt;
        chord = crd;
        slotIndex = sI;
    }

    /**
     * Places the Chord at the specified position in the ChordPart.
     */
    public void execute() {
        Trace.log(2, "executing SetChordCommand");
        oldChord = chordProg.getChord(slotIndex);
        chordProg.setChord(slotIndex, chord);
    }

    /**
     * Replaces the previously placed Chord with whatever used to be there.
     */
    public void undo() {
        chordProg.setChord(slotIndex, oldChord);
    }

    /**
     * Places the chord at the specified position in the chordProg.
     */
    public void redo() {
        chordProg.setChord(slotIndex, chord);
    }

    public boolean isUndoable() {
        return undoable;
    }
}
