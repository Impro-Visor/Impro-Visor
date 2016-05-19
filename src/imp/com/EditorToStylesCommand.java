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

import imp.data.advice.Advisor;
import imp.data.*;
import imp.gui.Notate;
import imp.util.BasicEditor;
import polya.*;

public class EditorToStylesCommand implements Command {

    /**
     * editor for the file source
     */
    BasicEditor sourceEditor;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    public EditorToStylesCommand(BasicEditor sourceEditor) {
        this.sourceEditor = sourceEditor;
    }

    public void execute() {
        // Get the contents of the edit window.
        String contents = sourceEditor.getText();
        
        // Parse into individual items ("rules").
        Polylist rules = Notate.parseListFromString(contents);
        
        if(rules.isEmpty())
            return;
        
        // Add the rules back as style specifications.
        while( rules.nonEmpty() ) {
            if(rules.first() instanceof Polylist)
                Advisor.addUserRule((Polylist)rules.first());
            rules = rules.rest();
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
