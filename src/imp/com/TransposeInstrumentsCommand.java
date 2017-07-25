/**
 * This Java Class is part of the Impro-Visor Application.
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
import imp.data.Score;
import imp.data.Transposition;
import imp.gui.Notate;
import imp.util.Trace;

/**
 * An undoable Command that transposes the instruments of a leadsheet
 * Notate.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Robert Keller
 */
public class TransposeInstrumentsCommand implements Command, Constants {
    
    /**
     * The notation window
     */
    
    Notate notate;
    
    /**
     * The new set of transpositions
     */
    
    Transposition newTransposition;
    
    /**
     * The old set of transpositions (for possible undo)
     */
    
    Transposition oldTransposition;
    
    /**
     * Whether to always use settings
     */
    
    boolean alwaysUse;
    
    
    /**
     * true since this Command can be undone
     */
    boolean undoable = true;
    
    /**
     * Creates a new Command that transposes melody, chords, and key signature
     * of a leadsheet and score.
     * @param newClef
     */
    public TransposeInstrumentsCommand(Notate notate,
                                       Transposition newTransposition,
                                       boolean alwaysUse) 
    {
        this.notate = notate;
        this.newTransposition = newTransposition;
        oldTransposition = notate.getTransposition();
        this.alwaysUse = alwaysUse;
    }
    
    /**
     * Execute the melodyTransposition.
     */
    @Override
    public void execute() {
        Trace.log(2, "executing TransposeInstrumentsCommand");
        notate.transposeInstruments(newTransposition, alwaysUse);
     }
    
    /**
     * Undoes the transposition.
     */
    @Override
    public void undo() {
        Trace.log(2, "undoing TransposeInstrumentsCommand");
        notate.transposeInstruments(oldTransposition, false);
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

