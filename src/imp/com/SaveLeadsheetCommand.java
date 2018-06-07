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

import imp.RecentFiles;
import imp.data.Leadsheet;
import imp.data.Score;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * A Command that saves a Score into a File.
 */
public class SaveLeadsheetCommand implements Command {
    
    /**
     * reference to Command Manager
     */
    private CommandManager cm;

    /**
     * the File to save to
     */
    private File file;

    /**
     * the Score to save
     */
    private Score score;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * stores error if exception during save
     */
    Exception error = null;
    
    private boolean saveRoadMap;
    
    /**
     * Creates a new Command that can save a Score to a File.
     * @param file      the File to save to
     * @param score     the Score to save
     */
    public SaveLeadsheetCommand(File file, Score score, CommandManager cm, boolean saveRoadMap) {
        this.cm = cm;
        this.file = file;
        this.score = score;
        this.saveRoadMap = saveRoadMap;
    }

    public SaveLeadsheetCommand(File file, Score score, boolean saveRoadMap) {
        this(file, score, null, saveRoadMap);
    }
    
    /**
     * Saves the Score to the File.
     */
    public void execute() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Leadsheet.saveLeadSheet(out, score, saveRoadMap);
            out.close();
            RecentFiles recent = new RecentFiles(file.getAbsolutePath());
            recent.writeNewFile();
//            ProgramStatus.setStatus("Lead sheet saved.");
            if(cm != null)
                cm.changedSinceLastSave(false);
        } catch(Exception e) {
            error = e;
            
            e.printStackTrace();
        }
    }
    
    public Exception getError() {
        return error;
    }

    /**
     * Undo unsupported for SaveLeadsheetCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for SaveLeadsheet.");
    }

    /**
     * Redo unsupported for SaveLeadsheetCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for SaveLeadsheet.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
