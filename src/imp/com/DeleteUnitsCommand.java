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
 * A Command that deletes all units from a section of a Part.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         Unit
 * @author      Stephen Jones
 */
public class DeleteUnitsCommand implements Command, Constants {

    /**
     * the Part to delete from
     */
    private Part part;

    /**
     * the section that was deleted
     */
    private Part deletedUnits;

    /**
     * the first slot that had a unit
     */
    private int deletedStartSlot;
    
    /**
     * the first slot to delete from
     */
    private int startSlot;

    /**
     * the last slot to delete from
     */
    private int endSlot;

    /**
     * true since this can be undone
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can delete a section of a Part.
     * @param part      the Part to delete from
     * @param startSlot the first slot to delete from
     * @param endSlot   the last slot to delete from
     */
    public DeleteUnitsCommand(Part part, int startSlot, int endSlot) {
        this.part = part;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    /**
     * Deletes the section of the Part, saving the deleted units for
     * undoing.
     */
    public void execute() {
        Trace.log(2, "executing DeleteUnitsCommand");
        deletedStartSlot = startSlot;
        Unit firstUnit = part.getUnit(deletedStartSlot);
        
        while(firstUnit == null) {
            deletedStartSlot++;
            if(deletedStartSlot == part.size())
                break;
            firstUnit = part.getUnit(deletedStartSlot);
        }
        
        if(firstUnit == null || deletedStartSlot > endSlot) {
            undoable = false;
            return;
        }
        
        deletedUnits = part.extract(startSlot, endSlot);
        part.delUnits(startSlot, endSlot);

        if(part instanceof MelodyPart) {
            MelodyPart melody = (MelodyPart)part;

            if(melody.getPrevNote(startSlot) != null &&
               melody.getPrevNote(startSlot).isRest() &&
               melody.getNextNote(endSlot) != null &&
               melody.getNextNote(endSlot).isRest()) {
                Command setRest = new SetRestCommand(startSlot, melody);
                setRest.execute();
            }
            else {
                int prevIndex = melody.getPrevIndex(startSlot);
                int[] metre = melody.getMetre();
                int beatValue = ((WHOLE)/metre[1]);
                int measureLength = metre[0] * beatValue;
                int barSlot = startSlot - startSlot%(measureLength) - 1;
                
                if(prevIndex > -1) {
                    if(prevIndex < barSlot) {
                        Command setNote = new SetNoteCommand(barSlot, 
                                melody.getNote(prevIndex).copy(), melody);
                        setNote.execute();
                        melody.delUnit(barSlot);
                    }
                    else {
                        Command setNote = new SetNoteCommand(prevIndex, 
                                melody.getNote(prevIndex).copy(), melody);
                        setNote.execute();
                    }
                }
            }
        }
    }

    /**
     * Undoes the deleting.
     */
    public void undo() {
        Trace.log(2, "undo DeleteUnitsCommand");
        // part.pasteOver(deletedUnits, deletedStartSlot);
        Command paste = new PasteCommand(deletedUnits, part, deletedStartSlot, false);
        paste.execute();
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
