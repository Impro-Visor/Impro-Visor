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
 * An undoable Command that can be used to drag a set of Units from one
 * slot to another slot.
 * @see         Command
 * @see         CutCommand
 * @see         PasteCommand
 * @see         CommandManager
 * @see         Part
 * @author      Stephen Jones
 */
public class DragSetCommand implements Command {

    /**
     * the "clipboard" that contains the section to drag
     */
    private Part dragClip;

    /**
     * the Part that contains the section to drag and the target
     */
    private Part part;

    /**
     * the slot to start the cut
     */
    private int startSlot;

    /**
     * the slot to stop the cut
     */
    private int endSlot;

    /**
     * the slot to start the paste
     */
    private int pasteSlot;

    /**
     * true because this Command is undoable
     */
    private boolean undoable = true;

    /**
     * the CutCommand used in dragging
     */
    private Command cutCommand;

    /**
     * the PasteCommand used in dragging
     */
    private Command pasteCommand;

    /**
     * Creates a new Command that can copy and paste a set of Units in
     * one step (used in dragging.)
     * @param part      the Part to cut and paste in
     * @param startSlot the slot to start the cut
     * @param endSlot   the slot to end the cut
     * @param pasteSlot the slot to paste at
     */
    public DragSetCommand(Part part, int startSlot, int endSlot, int pasteSlot) 
    {
        Trace.log(2, "DragSetCommand: " + startSlot + " to " + endSlot + " paste to " + pasteSlot);
        this.part = part;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.pasteSlot = pasteSlot;
        dragClip = new Part();
    }

    /**
     * Executes the drag.
     */
    public void execute() {
        Trace.log(2, "executing DragSetCommand");
        cutCommand = new CutCommand(part, dragClip, startSlot, endSlot);
        cutCommand.execute();
        pasteCommand = new PasteCommand(dragClip, part, pasteSlot, false);
        pasteCommand.execute();
    }

    /**
     * Undoes the drag.
     */
    public void undo() {
        pasteCommand.undo();
        cutCommand.undo();
    }

    /**
     * Redoes the drag.
     */
    public void redo() {
        cutCommand.redo();
        pasteCommand.redo();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
