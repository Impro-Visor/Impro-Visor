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
import imp.util.ErrorLog;
import java.io.*;
import polya.Tokenizer;

/**
 * A Command that opens a file into a Score.
 * @see         Command
 * @see         CommandManager
 * @see         Score
 * @see         File
 * @author      Stephen Jones
 */
public class OpenLeadsheetCommand implements Command {

    /** 
     * the File to open
     */
    private File file;

    /**
     * the Score to read the File into
     */
    private Score score;

    /**
     * editor for the file source
     */
    javax.swing.JTextArea sourceEditor;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can read a File into a Score.
     * @param file      the File to read
     * @param score     the Score to read the File into
     */
    public OpenLeadsheetCommand(File file, Score score) {
        this.file = file;
        this.score = score;
    }

    /**
     * Reads the File into the Score.
     */
    public void execute() {
        FileInputStream leadStream;
        
        try {
            leadStream = new FileInputStream(file);
            RecentFiles recFile = new RecentFiles(file.getAbsolutePath());
            recFile.writeNewFile();
        }
        catch(Exception e) {
            ErrorLog.log(ErrorLog.SEVERE, "File does not exist: " + file);
            return;
            // e.printStackTrace();
        }

        Leadsheet.readLeadSheet(new Tokenizer(leadStream), score);
    }

public static String fileToString(File file)
  {
  InputStream leadStream;
  try {
      leadStream = new FileInputStream(file);
      }
  catch( Exception e )
    {
    return null;
    }
  InputStreamReader reader = new InputStreamReader(leadStream);

  StringBuilder buffer = new StringBuilder();

  try
    {
    int c;
    while( (c = reader.read()) != -1 )
      {
      buffer.append((char)c);
      }
    }
  catch( IOException e )
    {
    }
  return buffer.toString();
  }


    /**
     * Undo unsupported for OpenLeadsheet.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for OpenLeadsheet.");
    }

    /**
     * Redo unsupported for OpenLeadsheet.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for OpenLeadsheet.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
