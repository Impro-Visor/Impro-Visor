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
import imp.gui.Notate;
import imp.util.BasicEditor;
import java.io.*;

/**
 * A Command that sends the editor contents to the leadsheet.
 * @see         Command
 * @see         CommandManager
 * @see         Score
 * @see         File
 * @author      Robert Keller, following OpenLeadsheetCommand
 */
public class LeadsheetToEditorCommand implements Command {

    Notate notate;

    /**
     * editor for the file source
     */
    BasicEditor sourceEditor;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    private boolean saveRoadMap;
    
    /**
     * Creates a new Command that can read a File into a Score.
     * @param file      the File to read
     * @param score     the Score to read the File into
     */
    public LeadsheetToEditorCommand(Notate notate, BasicEditor sourceEditor) {
        this.notate = notate;
        this.saveRoadMap = notate.getSaveRoadMap();
        this.sourceEditor = sourceEditor;
        sourceEditor.setTitle(notate.getScore().getTitle());
    }

    /**
     * Reads the File into the Score.
     */
    public void execute() {
        StringWriter writer = new StringWriter();
        BufferedWriter out = new BufferedWriter(writer);
        try {
            Leadsheet.saveLeadSheet(out, notate.getScore(), notate.getSaveRoadMap());
            out.close();
        } catch( Exception e ) {}
        sourceEditor.setText(writer.toString());
     }

    /**
     * Undo unsupported.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for GetEditorContents.");
    }

    /**
     * Redo unsupported.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for GetEditorContents.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
