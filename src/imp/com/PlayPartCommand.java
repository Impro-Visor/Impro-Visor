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
import imp.util.Trace;

public class PlayPartCommand implements Command {
    private MelodyPart part;
    private boolean undoable = false;

    public PlayPartCommand(MelodyPart part) {
        this.part = part;
    }

    public void execute() {
        Trace.log(2, "executing PlayPartCommand");
        Score score = new Score();
        score.addPart(part.copy());

//??        Command scoreCommand = new PlayScoreCommand(score, 0, true);
//        scoreCommand.execute();
    }

    public void undo() {

    }

    public void redo() {

    }

    public boolean isUndoable() {
        return undoable;
    }
}
