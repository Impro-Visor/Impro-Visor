/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College
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
import imp.gui.Notate;
import java.util.*;

/**
 * A Command that can delete more than one Part. Everything in the destination
 * Part is wiped out when the delete takes place. Code should be able to be 
 * easily modified into cutAll using cut commands (what it was originally coded
 * as), but the functionality and logistics of pasting such cutAll commands.
 * As of now, it is tailored only to take care of melodies, but adding another
 * part parameter would allow for other features to be removed as well.
 * would be complicated
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         DeleteUnitsCommand
 * @author      Nathan Kim
 */
public class DeleteAllCommand implements Command {
    /**
     * the arrayList that stores delete commands to they can be undone
     */
    private ArrayList<DeleteUnitsCommand> deleteCommand = new ArrayList<DeleteUnitsCommand>();
    /**
     * number of parts to be deleted (iterations)
     */
    private int reps;
    /**
     * notate containing parts to be deleted (location)
     */
    private Notate not;

    /**
     * true since deleting can be undone
     */
    private boolean undoable = true;

    /**
     * Creates a new Command that can delete a desired number of parts.
     * @param repeats           number of parts to be deleted (iterations)
     * @param not               notate containing parts to be deleted
     */
    public DeleteAllCommand(int repeats, Notate not) {
        this.reps = repeats;
        this.not = not;
    }

    /**
     * Creates a new Command that can delete a desired number of parts.
     * @param repeats           number of parts to be deleted (iterations)
     * @param not               notate containing parts to be deleted
     * @param undoable          a boolean saying if this can be undone
     */
    public DeleteAllCommand(int repeats, Notate not,
                                                        boolean undoable) {
        this(repeats, not);
        this.undoable = undoable;
    }

    /**
     * Empties the all parts. The delete command is preserved for undoing
     * and redoing.
     */
    public void execute() {
        Trace.log(2, "executing DeleteAllCommand");

        for (int x=0; x<reps; x++)
        {

            //stave.setSelection(0, stave.getMelodyPart().size() - 1);
            not.getStaveAtTab(x).setSelection(0, not.getStaveAtTab(x).getMelodyPart().size()-1);

            //not.redoAdvice();

            not.staveRequestFocus();
    
            deleteCommand.add(new DeleteUnitsCommand(not.getStaveAtTab(x).getMelodyPart(),
                                  not.getStaveAtTab(x).getSelectionStart(),
                                  not.getStaveAtTab(x).getSelectionEnd()));
            deleteCommand.get(x).execute();
            
            if(!deleteCommand.get(x).isUndoable()){
                undoable = false;
            }
        }

    }

    /**
     * Undoes the entire deleting.
     */
    public void undo() {
        for (int x=reps-1; x>=0; x--){
            deleteCommand.get(x).undo();
        }
    }

    /**
     * Redoes the entire deleting.
     */
    public void redo() {
        for (int x=reps-1; x>=0; x--){
            deleteCommand.get(x).redo();
        }
    }

    public boolean isUndoable() {
        return undoable;
    }
}
