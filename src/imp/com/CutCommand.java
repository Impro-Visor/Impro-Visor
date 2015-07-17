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
 * A Command that cuts a section of a Part and creates a destination Part
 * made up of the cut section.  Everything in the destination Part is
 * wiped out when the cut takes place.  Note that this only carries
 * over Units.  No Part properties are carried over, and although
 * the destination Part will have properties like key signature, those
 * properties will not be transferred in a paste, either.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CopyCommand
 * @see         PasteCommand
 * @author      Stephen Jones
 */
public class CutCommand implements Command {

    /**
     * the Part to cut from
     */
    private Part source;

    /**
     * the undoable command used to delete the units
     */
    private DeleteUnitsCommand deleteUnits;
    
    /**
     * the Part to copy into
     */
    private Part dest;

    /**
     * the first slot to cut from
     */
    private int startSlot;

    /**
     * the last slot to cut from
     */
    private int endSlot;

    /**
     * true since cutting can be undone
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can cut a section of a Part and put that
     * into a destination.
     * @param source            the Part to cut from
     * @param dest              the Part to copy into
     * @param startSlot         the first slot to cut from
     * @param endSlot           the last slot to cut from
     */
    public CutCommand(Part source, Part dest, int startSlot, int endSlot) {
        this.source = source;
        this.dest = dest;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    /**
     * Creates a new Command that can cut a section of a Part and put that
     * into a destination.
     * @param source            the Part to cut from
     * @param dest              the Part to copy into
     * @param startSlot         the first slot to cut from
     * @param endSlot           the last slot to cut from
     * @param undoable          a boolean saying if this can be undone
     */
    public CutCommand(Part source, Part dest, int startSlot, int endSlot,
                                                        boolean undoable) {
        this(source, dest, startSlot, endSlot);
        this.undoable = undoable;
    }

    /**
     * Empties the destination, cuts the section from the Source, and copies
     * that into the destination.  The cut Part is preserved for undoing
     * and redoing.
     */
    public void execute() {
        Trace.log(2, "executing CutCommand");
        /*
        Part newPart = source.extract(startSlot, endSlot);
        source.delUnits(startSlot, endSlot);

        Part.PartIterator i = newPart.iterator();
        dest.empty();
        while(i.hasNext())
            dest.addUnit(i.next());
        */
        
        Command copyCommand = new CopyCommand(source, dest, startSlot, endSlot);
        copyCommand.execute();

        deleteUnits = new DeleteUnitsCommand(source, startSlot, endSlot);
        deleteUnits.execute();
        if(!deleteUnits.isUndoable()){
            undoable = false;
        }
    }

    /**
     * Undoes the cutting.
     */
    public void undo() {
        deleteUnits.undo();
    }

    /**
     * Redoes the cutting.
     */
    public void redo() {
        deleteUnits.redo();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
