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
 * A Command that copies a section of a Part and creates a destination
 * Part made up of the copied section.  Everything in the destination
 * Part is wiped out when the copy takes place.  Note that this
 * only copies Units.  No Part properties are carried over, and although
 * the destination Part will have properties like key signature, those
 * properties will not be transferred in a paste, either.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CutCommand
 * @see         PasteCommand
 * @author      Stephen Jones
 */
public class CopyCommand implements Command {

    /**
     * the Part to copy from
     */
    private Part source;

    /**
     * the Part to copy to
     */
    private Part dest;

    /**
     * the first slot to copy from in the source
     */
    private int startSlot;

    /**
     * the last slot to copy from in the source
     */
    private int endSlot;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can copy a section of the Part into
     * the destination.
     * @param source            the Part to copy from
     * @param dest              the Part to copy to
     * @param startSlot         the first slot in the Part to copy from
     * @param endSlot           the last slot in the Part to copy from
     */
    public CopyCommand(Part source, Part dest, int startSlot, int endSlot) {
        this.source = source;
        this.dest = dest;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    /**
     * Empties the destination and copies the section of the source into
     * the destination.
     */
    public void execute() {
        Trace.log(2, "executing CopyCommand");
        Part newPart = source.extract(startSlot, endSlot);
        Part.PartIterator i = newPart.iterator();
        dest.empty();
        while(i.hasNext())
            dest.addUnit(i.next());
    }

    /**
     * Undo unsupported.
     */
    public void undo() {
        throw new
            UnsupportedOperationException("Undo unsupported for Copy.");
    }

    /**
     * Redo unsupported.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for Copy.");
    }

    public boolean isUndoable() {
        return undoable;
    }
}
