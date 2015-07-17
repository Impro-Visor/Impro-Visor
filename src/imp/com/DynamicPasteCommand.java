/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

import imp.data.Part;
import imp.gui.Stave;

/**
 * A Command that pastes a Part over a section of a destination Part.
 * This is intended for "dynamic" use, i.e. while improvising.
 * It is adapted from SafePasteCommand.
 * If it will overwrite existing notes, then it asks the user what to do.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         PasteCommand
 * @author      Stephen Jones
 */
public class DynamicPasteCommand implements Command {

    /**
     * the Part to paste from
     */
    private Part source;

    /**
     * the Part to paste onto
     */
    private Part dest;

    /**
     * the slot to paste at
     */
    private int startSlot;

    /**
     * the PasteCommand used for the paste itself
     */
    private PasteCommand pasteCommand;

    
    private Stave stave;

    /**
     * if the paste will overwrite existing notes, pop up a dialog if this is
     * set to true, otherwise go by the default overwrite boolean
     */
    private boolean dialog = false;

    /**
     * this command is undoable by default, but may be set to false
     * if the paste is cancelled
     */
    private boolean undoable = true;

    private boolean play = true;
  
    /**
     * Creates a new SafePasteCommand.
     * @param source    the Part to paste from
     * @param dest      the Part to paste onto
     * @param startSlot the slot to paste at
     * @param dialog    a boolean true if a dialog should be popped up
     * @param overwrite a boolean true if the command should default overwrite
     * @param frame     the JFrame to pop the dialog out of
     */
    public DynamicPasteCommand(Part source, 
                               Part dest, 
                               int startSlot, 
                               Stave stave) {
        this.source = source;
        this.dest = dest;
        this.startSlot = startSlot;
        this.stave = stave;
    }

    /**
     * Executes the safe paste.
     */
    public void execute() {

            overwrite();
    }

    /**
     * Pastes the source onto the dest.
     */
    private void overwrite() {
        undoable = true;
        pasteCommand = new PasteCommand(source, dest, startSlot, play);
        
//        // selects the notes & rests just inserted
//        if ( stave != null) {
//            stave.setSelection(startSlot, startSlot + source.size() - 1);

        pasteCommand.execute();
//        }
    }


    /**
     * Undoes the paste.
     */
    public void undo() {
        pasteCommand.undo();
    }

    /**
     * Redoes the paste.
     */
    public void redo() {
        pasteCommand.redo();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
