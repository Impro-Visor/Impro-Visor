/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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

import imp.*;
import imp.data.*;
import imp.util.Trace;
import imp.util.ErrorLog;

/**
 * An undoable Command that can shift a contiguous set of pitches up or down
 * a specified amount.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Stephen Jones, Steven Gomez
 */
public class ShiftPitchesCommand implements Command, Constants {
    
    /**
     * the first slot to shift
     */
    int startIndex;
    
    /**
     * the last slot to shift
     */
    int stopIndex;
    
    /**
     * the amount to shift the pitch
     */
    int shift;
    
    /**
     * the minimum allowed pitch
     */
    int minPitch;
    
    /**
     * the maximum allowed pitch
     */
    int maxPitch;
    
    /**
     * the key signature of the part containing the note
     */
    int keySig;
    
    /**
     * the part in which to shift pitches
     */
    MelodyPart part;
    
    /**
     * true since this Command can be undone
     */
    boolean undoable = true;
    
    /**
     * Creates a new Command that can shift pitches of a set of Notes.
     */
    public ShiftPitchesCommand(int shift, MelodyPart part, int startIndex,
            int stopIndex, int minPitch, int maxPitch,
            int keySig) {
        this.startIndex = startIndex;
        this.part = part;
        this.stopIndex = stopIndex;
        this.shift = shift;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
        this.keySig = keySig;
    }
    
    /**
     * Executes the shifts.
     */
    public void execute() {
        Trace.log(2, "executing ShiftPitchesCommand");
        
        doShift(shift);
        
//        if( ImproVisor.getPlay() ) {
//        ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.NODRUMS);
//            How it used to be: No chords were played.
//            new PlayPartCommand(((MelodyPart)part).extract(startIndex, stopIndex)).execute();
//        }
    }
    
    /**
     * Moves all selected notes one semitone in the given direction.  Undos
     * this change is the shift pushed a note out of bounds.
     */
    private void doShift(int shift) {
        boolean outOfBounds = false;
        try {          
            for(int i = startIndex; i <= stopIndex; i++) {
                Note note = part.getNote(i);
                if(note != null && note.nonRest()) {
                    note.shiftPitch(shift, keySig);

                    if(note.getPitch() < minPitch || note.getPitch() > maxPitch) 
                        outOfBounds = true;
                    
                }
            }
            
            /**
             * If that shift pushed any note/s out of bounds, shift it all back
             * the other direction.  We only have to do this once, since no note
             * can be out of bounds by more than one semitone (since 
             * the invariant is that all notes are in bounds, and we shift 
             * by one semitone).*/   
            if (outOfBounds) {
                for (int i = startIndex; i <= stopIndex; i++) {
                    Note note = part.getNote(i);
                    if(note != null && note.nonRest())
                        note.shiftPitch(-1*shift, keySig);
                }
            }
            
        } catch (Exception ex) {
            ErrorLog.log(ErrorLog.WARNING, "*** Warning: shift pitches failed.");
        }
    }
    
    /**
     * Undoes the shifts.
     */
    public void undo() {
        shift *= -1;
        execute();
    }
    
    /**
     * Redoes the shifts.
     */
    public void redo() {
        undo();
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
