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

import java.util.Queue;
import java.util.Stack;

/**
 * Executes Commands and manages executed Commands for undoing and redoing 
 * purposes.
 * @see         Command
 * @see         SetNoteCommand
 * @see         PlayScoreCommand
 * @author      Stephen Jones
 */
public class CommandManager {

    /**
     * keeps track of executed, undoable Commands so they can be undone
     */
    private Stack<Command> undoStack;

    /**
     * keeps track of undone Commands so they can be redone
     */
    private Stack<Command> redoStack;

    /**
     * keeps track of whether the system has changed since the last save
     */
    private boolean changed = false;
    private boolean changedByCommand = false;
    private int lastSavedUndoSize = 0;
    private int lastSavedRedoSize = 0;
    
    /**
     * Creates a new CommandManager with empty Stacks.
     */
    public CommandManager() {
        undoStack = new Stack<Command>();
        redoStack = new Stack<Command>();
    }
    
    /**
     * called when the program has saved the leadsheet
     */
    public void changedSinceLastSave(boolean changed) {
        this.changed = changed;
        if(!changed) {  // if reset to false, also reset changedByCommand to false
            changedByCommand = false;
        }
        lastSavedUndoSize = undoStack.size();
        lastSavedRedoSize = redoStack.size();
    }
    
    /**
     * returns whether the stack has changed since the last save
     */
    public boolean changedSinceLastSave() {
        
        /**
         * special case:
         * not changed by user action, but was changed by execution of a command
         * then check to see if undoStack is empty and if the last saved undo
         * size is also empty; if so, then the user made some changes with commands
         * and then undid those commands
         */
        if(!changed && changedByCommand && undoStack.size() == 0 && lastSavedUndoSize == 0) 
            return false;
        
        return changed;
    }
    
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Executes the specified Command and adds it to the undoStack if it
     * is undoable.
     * @param cmd       Command to execute
     */
    public void execute(Command cmd) {
        changed = true;
        cmd.execute();
        if(cmd.isUndoable()) {
            redoStack.clear();
            undoStack.push(cmd);
        }
    }

    /**
     * Executes each command in the specified queue
     * @param cmdQueue  Queue<Command> to execute
     */
    public void executeQueue(Queue<Command> cmdQueue) {
        while(!cmdQueue.isEmpty())
            execute(cmdQueue.remove());
    }
    
    /**
     * Undoes the last Command executed.
     */
    public void undo() {
        if(!undoStack.empty()) {
            //Trace.log(0, "undo in command manager");
            changedByCommand = true;
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    /**
     * Redoes the last Command undone.
     */
    public void redo() {
        if(!redoStack.empty()) {
            //Trace.log(2, "redo in command manager");
            changedByCommand = true;
            Command cmd = redoStack.pop();
            cmd.redo();
            undoStack.push(cmd);
        }
    }
    
    /**
     * Returns size of undo stack (used for determining if leadsheet has changes)
     */
    public int undoStackSize() {
        return undoStack.size();
    }

    /**
     * Returns true if there is a Command in the undoStack.
     * @return boolean          true if there is a Command in the undoStack
     */
    public boolean canUndo() {
        return !undoStack.empty();
    }

    /**
     * Returns true if there is a Command in the redoStack.
     * @return boolean          true if there is a Command in the redoStack
     */
    public boolean canRedo() {
        return !redoStack.empty();
    }
}
