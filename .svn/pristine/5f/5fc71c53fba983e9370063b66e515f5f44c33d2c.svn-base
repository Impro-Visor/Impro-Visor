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
 * An undoable Command that changes a Note's pitch and accidental values.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         Accidental
 * @author      Stephen Jones
 */
public class SetPitchCommand implements Command, Constants {

    /**
     * the Note to change
     */
    private Note note;

    /**
     * the Note's old pitch
     */
    private int oldPitch;

    /**
     * the Note's new pitch
     */
    private int newPitch;

    /**
     * the Note's old accidental
     */
    private Accidental oldAccidental;

    /**
     * the Note's new accidental
     */
    private Accidental newAccidental;

    /**
     * true because this Command can be undone
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can change a Note's pitch and accidental
     * values.
     * @param note              the Note to modify
     * @param newPitch          the new pitch to set
     * @param newAccidental     the Accidental to set
     */
    public SetPitchCommand(Note note, int newPitch, Accidental newAccidental) {
        this.note = note;
        this.newPitch = newPitch;
        this.newAccidental = newAccidental;
    }

    /**
     * Changes the Note's pitch and accidental values
     */
    public void execute() {
        Trace.log(2, "executing SetPitchCommand");
        oldPitch = note.getPitch();
        oldAccidental = note.getAccidental();
        note.setPitch(newPitch);
        note.setAccidental(newAccidental);
    }

    /**
     * Undoes the change.
     */
    public void undo() {
        note.setPitch(oldPitch);
        note.setAccidental(oldAccidental);
    }

    /**
     * Redoes the change.
     */
    public void redo() {
        note.setPitch(newPitch);
        note.setAccidental(newAccidental);
    }

    public boolean isUndoable() {
        return undoable;
    }
}
