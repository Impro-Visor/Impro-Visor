/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.com;

import imp.ImproVisor;
import imp.data.MelodyPart;
import imp.data.Note;

/**
 *
 * @author muddCS15
 */
public class MergeSameNotesCommand implements Command {

    //undoable
    private static final boolean undoable = true;
    
    /**
     * the first slot to merge
     */
    int startIndex;

    /**
     * the last slot to merge
     */
    int stopIndex;
    
    /**
    * the part in which to resolve pitches
    */
    MelodyPart part;
    
    /**
    * the part before changes are made (for undo)
    */
    MelodyPart originalPart;

    /**
    * the new part, in case redo is called for
    */
    MelodyPart saveForRedo;
    
    public MergeSameNotesCommand(MelodyPart part){
        this(part, 0, part.size()-1);
    }
    
    public MergeSameNotesCommand(MelodyPart part, int startIndex, int stopIndex){
        this.part = part;
        if(part.getNote(startIndex) == null){
            startIndex = part.getNextIndex(startIndex);
        }
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
    }
    
    public void execute() {
        originalPart = part.extract(startIndex, stopIndex);
        //part = mergeSameNotes(part.copy(), startIndex, stopIndex);
        part.pasteOver(mergeSameNotes(part.copy(), startIndex, stopIndex), 0);
    }

    public void undo() {
        saveForRedo = part.extract(startIndex, stopIndex);
        part.pasteOver(originalPart, startIndex);
        playIt();
    }

    public void redo() {
        part.pasteOver(saveForRedo, startIndex);
        playIt();
    }

    public boolean isUndoable() {
        return undoable;
    }
    
      private void playIt()
    {
    if( ImproVisor.getPlay() )
      {
      ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS);

      //new PlayPartCommand(part.extract(startIndex, stopIndex)).execute();
      }
    
    }
    
    /**
     * mergeSameNotes
     * Merges consecutive notes that have the same pitch.
     * This should probably be a command so that more people can use it.
     * @param unmerged a MelodyPart whose consecutive same notes are to be merged.
     * @return a MelodyPart whose consecutive same notes have been merged.
     */
    private MelodyPart mergeSameNotes(MelodyPart unmerged, int startIndex, int stopIndex){
        //System.out.println("Unmerged: "+unmerged);
        //unmerged.printNotesAtSlots();
        
        MelodyPart merged = unmerged.copy();
        
//        int start = startIndex;
        Note toAdd = unmerged.getNote(startIndex);
//        while(toAdd == null && start < stopIndex){
//            start = unmerged.getNextIndex(start);
//            toAdd = unmerged.getNote(start);
//        }
        
        if(toAdd == null){
            return unmerged;
        }
        
        int toAddIndex = startIndex;
        
        int duration = toAdd.getRhythmValue();

        for(int i = startIndex; i + duration <= stopIndex; i = unmerged.getNextIndex(i)){
            
            Note curr = unmerged.getNote(i);
            if(curr != null){
                duration = curr.getRhythmValue();
                Note next = unmerged.getNote(i + duration);
                
                if(curr.getPitch() == next.getPitch()){
                    toAdd.setRhythmValue(toAdd.getRhythmValue() + next.getRhythmValue());
                    merged.setNote(i + duration, null);
                }else{
                    merged.getNote(toAddIndex).setRhythmValue(toAdd.getRhythmValue());
                    //merged.setNote(toAddIndex, toAdd.copy());
                    toAdd = next;
                    toAddIndex = i + duration;
                }
                
            }
            
        }
        
        //add the last note
        merged.setNote(toAddIndex, toAdd.copy());
        
        //System.out.println("Merged: "+merged);
        //merged.printNotesAtSlots();
        
        return merged;
    }
    
}
