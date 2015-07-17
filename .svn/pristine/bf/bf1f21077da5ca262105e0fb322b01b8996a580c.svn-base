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

import imp.data.*;
import imp.util.Trace;

/**
 * Crates a new Command that can toggle the enharmonic of a Note.
 * @see         CommandManager
 * @see         Command
 * @see         MelodyPart
 * @see         Note
 * @author      Stephen Jones
 */
public class ToggleEnharmonicCommand implements Command {

    /**
     * the part that contains the Notes to toggle
     */
    private Part part;

    /**
     * the first slot to toggle
     */
    private int startSlot;

    /**
     * the last slot to toggle
     */
    private int endSlot;

    /**
     * undoable is variable depending on the Notes' accidentals
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can toggle the enharmonic of Notes.
     * @param party     the MelodyPart containing the Notes to toggle
     * @param startSlot the first slot to toggle
     * @param endSlot   the last slot to toggle
     */
    public ToggleEnharmonicCommand(Part part, 
            int startSlot, int endSlot) {
        this.part = part;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    /**
     * Tries to toggle the enharmonic of the Note.  If the Note is 
     * natural or is a Rest, then undoable is set to false.
     */
    public void execute() {
        Trace.log(2, "executing ToggleEnharmonicCommand");
        undoable = false;
        for(int i = startSlot; i <= endSlot; i++)
            if(part.getUnit(i) != null && 
                    part.getUnit(i).toggleEnharmonic())
                undoable = true;
    }

    /**
     * Toggles again.
     */
    public void undo() {
        for(int i = startSlot; i <= endSlot; i++)
            if(part.getUnit(i) != null)
                part.getUnit(i).toggleEnharmonic();
    }

    /**
     * Toggles again.
     */
    public void redo() {
        for(int i = startSlot; i <= endSlot; i++)
            if(part.getUnit(i) != null)
                part.getUnit(i).toggleEnharmonic();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
