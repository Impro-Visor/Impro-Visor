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
import imp.gui.Notate;
import imp.gui.Stave;
import imp.util.Trace;

/**
 * A Command that deletes all units from a section of a Part.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         Unit
 * @author      Stephen Jones
 */
public class InsertPartCommand implements Command, Constants {

    /**
     * the receiving Part (Part we insert into)
     */
    private MelodyPart part;
    
    /**
     * the Part to insert
     */
    private Part insertedPart;

    /**
     * the slot to insert into
     */
    private int slotIndex;
    
    private int selectionStart;
    private int selectionEnd;
    
    /**
     * the owner of the part, used to add/delete measures from all parts when a part length needs to be changed
     */
    private Notate notate;

    /**
     * true since this can be undone
     */
    private boolean undoable = true;
    
    private DragSetCommand dragSetCommand;
    private PasteCommand pasteCommand;

    /**
     * Creates a new Command that can delete a section of a Part.
     * @param part         the Part to insert into
     * @param slotIndex    the slot to insert at
     * @param insertedPart the Part to insert
     */
    public InsertPartCommand(Notate notate, MelodyPart part, int slotIndex, Part insertedPart) {
        this.notate = notate;
        this.part = part;
        this.slotIndex = slotIndex;
        this.insertedPart = insertedPart;
    }

    /**
     * Deletes the section of the Part, saving the deleted units for
     * undoing.
     */
    public void execute() {
        
        Trace.log(2, "executing InsertPartCommand");

        Stave currentStave = notate.getCurrentStave();
        
        selectionStart = currentStave.getSelectionStart();
        selectionEnd = currentStave.getSelectionEnd();
        
        int availableRoom = part.getFreeSlotsFromEnd();
        int insertionSize = insertedPart.getSize();
        
        if(insertionSize > availableRoom) {
            int newMeasures = (int) Math.ceil((insertionSize - availableRoom)/(double)part.getMeasureLength());
            notate.addMeasures(newMeasures);
            availableRoom += newMeasures * part.getMeasureLength();
        }

        // dragSetCommand doesn't work as we would like unless the slotIndex corresponds to a note
        part.splitUnit(slotIndex);
        dragSetCommand = new DragSetCommand(part, slotIndex, part.getSize() - availableRoom, slotIndex + insertionSize);
        dragSetCommand.execute();

        pasteCommand = new PasteCommand(insertedPart, part, slotIndex, false);
        pasteCommand.execute();
        
        currentStave = notate.getCurrentStave();
        
        currentStave.setSelectionStart(insertionSize + selectionStart);
        currentStave.setSelectionEnd(insertionSize + selectionEnd);
        
        notate.repaint();
    }

    /**
     * Undoes the insertion.
     */
    public void undo() {
        Trace.log(2, "undo InsertPartCommand");
        dragSetCommand.undo();
        // TODO: undo the measure extension too
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
