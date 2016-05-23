/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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

import imp.themeWeaver.ThemeWeaver;
import java.io.*;

/**
 * A Command that saves a Score into a File.
 */
public class SaveThemesCommand implements Command {

    /**
     * the File to save to
     */
    private File file;

    /**
     * the Advisor containing the rules
     */
    private ThemeWeaver themeWeaver;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can saves Advice to a file.
     * @param file      the File to save to
     */
    public SaveThemesCommand(File file, ThemeWeaver themeWeaver) {
        this.file = file;
        this.themeWeaver = themeWeaver;
    }

    /**
     * Saves the Score to the File.
     */
    public void execute() {
        try {
            themeWeaver.saveRules(file);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Undo unsupported for SaveAdviceCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for SaveThemes.");
    }

    /**
     * Redo unsupported for SaveAdviceCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for SaveThemes.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
