/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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
 * A Command that places a Rest at a certain position in a MelodyPart.
 * @see         Command
 * @see         CommandManager
 * @see         Rest
 * @see         Note
 * @see         MelodyPart
 * @author      Stephen Jones
 */
public class SetRestCommand implements Command, Constants {

    /**
     * the MelodyPart in which to place the Rest
     */
    private MelodyPart part;

    /**
     * the slot index in which to place the Rest
     */
    private int slotIndex;

    /**
     * the Note that was once at that slot index
     */
    private Note oldNote;

    /**
     * for undoing the rest insert
     */
    private DeleteUnitsCommand deleteUnits;
    
    /**
     * true since this Command can be undone.
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can place a Rest in a MelodyPart.
     * @param slotIndex         the slot to put the Rest in
     * @param part              the MelodyPart to put the Rest in
     */
    public SetRestCommand(int slotIndex, MelodyPart part) {
        this.slotIndex = slotIndex;
        this.part = part;
    }

    /**
     * Puts the Rest in the MelodyPart and preserves the Note that used to
     * be at that spot for undoing.
     */
    public void execute() {
        Trace.log(2, "executing SetRestCommand");
        int prevIndex = part.getPrevIndex(slotIndex);
        int nextIndex = part.getNextIndex(slotIndex);
        if(nextIndex == part.size())
            nextIndex = slotIndex;
        if(prevIndex == -1)
            prevIndex = slotIndex;
        deleteUnits = new DeleteUnitsCommand(part, prevIndex, nextIndex);
        deleteUnits.execute();

        if(deleteUnits.isUndoable())
            deleteUnits.undo();

        // oldNote = part.getNote(slotIndex);
        // part.setRest(slotIndex);

        if(part.getPrevNote(slotIndex) != null
                && part.getPrevNote(slotIndex).isRest()
                && part.getNextNote(slotIndex) != null
                && part.getNextNote(slotIndex).isRest()) {
            part.delUnit(slotIndex);
            part.delUnit(part.getNextIndex(slotIndex));
        }

        else if(part.getPrevNote(slotIndex) != null
                && part.getPrevNote(slotIndex).isRest()) {
            part.delUnit(slotIndex);
            undoable = false;
        }

        else if(part.getNextNote(slotIndex) != null
                && part.getNextNote(slotIndex).isRest()) {
            part.setRest(slotIndex);
            part.delUnit(part.getNextIndex(slotIndex));
        }

        else
            part.setRest(slotIndex);
    }

    /**
     * Undoes the Rest insertion.
     */
    public void undo() {
        // part.setNote(slotIndex, oldNote);
        if(deleteUnits.isUndoable())
            deleteUnits.undo();
    }

    /**
     * Redoes the Rest insertion.
     */
    public void redo() {
        // part.setRest(slotIndex);
        execute();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
