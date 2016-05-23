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

import imp.gui.Notate;
import imp.themeWeaver.ThemeWeaver;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFrame;
import polya.Polylist;
import polya.Tokenizer;

/**
 * A Command that opens an advice rulebase.
 * @see         Command
 * @see         CommandManager
 * @see         Advisor
 * @see         File
 * @author      Stephen Jones
 */
public class LoadThemesCommand implements Command, Runnable {

    /** 
     * the File to open
     */
    private File file;

    /**
     * the Advisor to read the File into
     */
    private ThemeWeaver themeWeaver;

    /**
     * true if the rulebase is appended to
     */
    private boolean append = false;
    
    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;
    
    private Notate parent;
    
    /**
     * Creates a new Command that can read a File into an Advisor.
     * @param file      the File to read
     * @param adv     the Advisor to read the File into
     */
    public LoadThemesCommand(File file, ThemeWeaver themeWeaver, JFrame notate) {
        this.file = file;
        this.themeWeaver = themeWeaver;
	parent = (Notate)notate;
    }
    /**
     * Reads the File into the ThemeWeaver.
     */
    public void run() {
        themeWeaver.loadFromFile(file);
        FileInputStream adviceStream = null;
 

    }

    

    public void execute() {
	run();
    }

    /**
     * Undo unsupported for LoadAdviceCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for LoadAdvice.");
    }

    /**
     * Redo unsupported for LoadAdviceCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for LoadAdvice.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
