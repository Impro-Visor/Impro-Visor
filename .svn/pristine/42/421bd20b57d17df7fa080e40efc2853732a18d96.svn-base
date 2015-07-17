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
import imp.ImproVisor;

public class PlayNoteCommand implements Command {
    private Note note;
    private boolean undoable = false;

    public PlayNoteCommand(Note note) {
        this.note = note;
    }

    public void execute() {
        Score score = new Score();
        score.addPart();
        score.getPart(0).addNote(note);

        ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.NODRUMS); // Don't loop one note

    }

    public void undo() {

    }

    public void redo() {

    }

    public boolean isUndoable() {
        return undoable;
    }
}
