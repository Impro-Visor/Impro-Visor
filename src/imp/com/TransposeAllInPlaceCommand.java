/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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
import imp.data.Score;
import imp.gui.Notate;
import imp.util.Trace;

/**
 * An undoable Command that transposes the Chords and the Melody of a
 * Score a specified number of semi-tones. It calls transposeAllInPlace in
 * Notate.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Robert Keller
 */
public class TransposeAllInPlaceCommand implements Command, Constants {
    
    /**
     * transposition amount for melody
     */

    int melodyTransposition;
    
    /**
     * transposition amount for chords
     */

    int chordTransposition;
    
    /**
     * the new key signature
     */
    int newKeySig;
    
    /**
     * the old key signature, in case of an undo
     */
    int oldKeySig;
    
    /**
     * the Notation window
     */
    
    Notate notate;
    
    /**
     * true since this Command can be undone
     */
    boolean undoable = true;
    
    /**
     * Creates a new Command that transposes melody, chords, and key signature
     * of a leadsheet and score.
     * @param notate
     * @param melodyTransposition
     * @param chordTransposition
     * @param newKeySig
     */
    public TransposeAllInPlaceCommand(Notate notate, 
                                      int melodyTransposition, 
                                      int chordTransposition,
                                      int newKeySig) 
    {
        this.notate = notate;
        this.melodyTransposition = melodyTransposition;
        this.chordTransposition = chordTransposition;
        this.newKeySig = newKeySig;
        Score score = notate.getScore();
        oldKeySig = score.getKeySignature();
    }
    
    /**
     * Execute the melodyTransposition.
     */
    @Override
    public void execute() {
        Trace.log(2, "executing TransposeAllInPlaceCommand");
        notate.transposeAllInPlace(melodyTransposition, chordTransposition, newKeySig);
    }
    
    /**
     * Undoes the transposition.
     */
    @Override
    public void undo() {
        Trace.log(2, "undoing TransposeAllInPlaceCommand");
        notate.transposeAllInPlace(-melodyTransposition, -chordTransposition, oldKeySig);
    }
    
    /**
     * Redoes the shifts.
     */
    @Override
    public void redo() {
        execute();
    }
    
    @Override
    public boolean isUndoable() {
        return undoable;
    }
}

