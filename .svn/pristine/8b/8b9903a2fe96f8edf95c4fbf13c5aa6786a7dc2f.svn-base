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
 * A Command that splits a unit in a Part into two units at the specified slotIndex if possible
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         Unit
 * @author      Martin Hunt
 */
public class SplitUnitCommand implements Command, Constants {

    /**
     * the part are we working with
     */
    private Part part;
    
    /**
     * the slot to split at
     */
    private int splitAtSlotIndex;
    
    /**
     * the unit to split
     */
    private Unit origSplitUnit;
    
    /**
     * the original unit's rhythmValue
     */
    private int origRhythmValue;
    
    /**
     * the new unit created from the split
     */
    private Unit newSplitUnit;

    /**
     * true since this can be undone
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can delete a section of a Part.
     * @param part         the Part to insert into
     * @param slotIndex    the slot to insert at
     * @param insertedPart the Part to insert
     */
    public SplitUnitCommand(Part part, int splitAtSlotIndex) {
        this.part = part;
        this.splitAtSlotIndex = splitAtSlotIndex;
    }

    /**
     * Deletes the section of the Part, saving the deleted units for
     * undoing.
     */
    public void execute() {
        Trace.log(2, "executing SplitUnitCommand");
        
        origSplitUnit = part.getUnit(splitAtSlotIndex);
        
        // check to see if we need to split a note:
        if(origSplitUnit != null) {
            undoable = false;   // nothing to split
            return;
        }
        
        int prevIndex = part.getPrevIndex(splitAtSlotIndex);
        origSplitUnit = part.getUnit(prevIndex);
        origRhythmValue = origSplitUnit.getRhythmValue();

        // if previous unit extends into this slot, we need to split it
        if(prevIndex + origRhythmValue >= splitAtSlotIndex) {
            System.out.println(part);

            Unit splitUnit = origSplitUnit.copy();
            splitUnit.setRhythmValue(origRhythmValue + prevIndex - splitAtSlotIndex);
            part.setUnit(splitAtSlotIndex, splitUnit);  // takes care of shortening the original note too!
        } else {
            // nothing to split, but if we actually did get here, things would be broken since the slot to split at is null
            Trace.log(0, "Error: SplitUnitCommand found inconsistencies with the Part");
        }
    }

    /**
     * Undoes the deleting.
     */
    public void undo() {
        Trace.log(2, "undo SplitUnitCommand");
        
        // remove the new unit, the delUnit function should handle the rhythmValues for us
        part.delUnit(splitAtSlotIndex);
    }

    /**
     * Redoes the deleting.
     */
    public void redo() {
        // part.delUnits(startSlot, endSlot);
        execute();
    }

    public boolean isUndoable() {
        return undoable;
    }
}

