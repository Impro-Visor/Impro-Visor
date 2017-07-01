/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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
import imp.gui.SourceEditorDialog;
import imp.roadmap.RoadMapFrame;
import imp.roadmap.brickdictionary.BrickDictionaryEditorDialog;
import imp.util.*;
import java.io.*;

/**
 * A Command that sends the editor contents to the leadsheet.
 * @see         Command
 * @see         CommandManager
 * @see         Score
 * @see         File
 * @author      Robert Keller, following OpenLeadsheetCommand
 */
public class EditorToDictionaryCommand implements Command {

    /**
     * the Score to read the File into
     */
    private String dictionaryFilename;

    /**
     * editor for the file source
     */
    BrickDictionaryEditorDialog sourceEditor;

    RoadMapFrame roadMapFrame;
            
    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can read a File into a Score.
     * @param file      the File to read
     * @param score     the Score to read the File into
     */
    public EditorToDictionaryCommand(RoadMapFrame roadMapFrame, BrickDictionaryEditorDialog sourceEditor) {
        dictionaryFilename = roadMapFrame.getDictionaryFilename();
        this.sourceEditor = sourceEditor;
        this.roadMapFrame = roadMapFrame;
    }

    /**
     * Reads the editor into the Dictionary
     */
    public void execute() {
        String contents = sourceEditor.getText();
        try
        {
            FileWriter out = new FileWriter(new File(dictionaryFilename));
            out.write(contents);
            out.close();
            roadMapFrame.newDictionary(dictionaryFilename);
        }
        catch (IOException e)
        {
            ErrorLog.log(ErrorLog.WARNING, "Error writing to Dictionary file! " + dictionaryFilename);
        }
    }

    /**
     * Undo unsupported.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for UseEditorContents.");
    }

    /**
     * Redo unsupported.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for UseEditorContents.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
