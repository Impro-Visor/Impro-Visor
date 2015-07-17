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

/**
 * A Command that inverts a Part over a section of a destination Part.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CopyCommand
 * @see         CutCommand
 * @author      Stephen Jones
 */
public class InvertCommand implements Command, Constants {

    /**
     * the Part to paste over
     */
    protected MelodyPart source;

    /**
     * the Part to paste onto
     */
    protected MelodyPart dest;

    /**
     * the section of the destination overwritten when the paste occurs
     */
    protected Part oldSection;

    /**
     * Command used to delete the units pasted over
     */
    protected DeleteUnitsCommand deleteUnits;

    /**
     * the slot to start pasting over in the destination
     */
    protected int startSlot;

    /**
     * the slot to end pasting over in the destination
     */
    protected int stopSlot;

    /**
     * true if a Rest was inserted to stop auto-expansion
     */
    protected boolean restInserted = false;

    /**
     * the slot where a rest is inserted
     */
    protected int stopIndex;
    
    /**
     * true since the pasting can be undone
     */
    protected boolean undoable = true;

    /**
     * whether to play when pasting
     */
    protected boolean play = false;

    /**
     * Creates a new Command that can invert a source Part over a section
     * of a destination Part.
     * It is assumed that rests have been trimmed from the front and back of the selection
     * @param source            the Part to paste
     * @param dest              the Part to paste over
     * @param startSlot         the slot to start pasting over in the dest
     * @param stopSlot          the slot to end pasting over in the dest
     */
    public InvertCommand(MelodyPart source, int startSlot, int stopSlot, boolean play) {
        this.source = source;
        this.dest = source;
        this.startSlot = startSlot;
        this.stopSlot = stopSlot;
        this.play = play;
    }

    /**
     * Creates a new Command that can paste a source Part over a section
     * of a destination Part.
     * @param source            the Part to paste
     * @param dest              the Part to paste over
     * @param startSlot         the slot to start pasting over in the dest
     * @param undoable          the boolean to see if the action is undoable
     */
    public InvertCommand(MelodyPart source, int startSlot, int stopSlot,
                                     boolean undoable, boolean play) {
        this(source, startSlot, stopSlot, play);
        this.undoable = undoable;
        Trace.log(2, "creating Invert Command");
    }

    /**
     * Pastes the source onto the section of the destination, preserving the
     * section that is pasted over, and making a copy of the source for
     * undoing and redoing purposes.
     */
    public void execute() {
      Trace.log(2, "executing InvertCommand");
      if( source != null && dest != null && source.size() > 0 )
        {
        Trace.log(3, "reversing slots from " + startSlot + " to " + stopSlot);

        source = source.extractInverse(startSlot, stopSlot);

        oldSection = dest.extractSlots(startSlot, stopSlot);

        dest.pasteOver(source, startSlot);
        
	if( play && source instanceof MelodyPart && ImproVisor.getPlay() )
	  {
          ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS);
	  // the old way: new PlayPartCommand(((MelodyPart)source)).execute();
	  }

        // if we're inserting a Note, then we want to make sure it stops
        // auto-expanding after two measures (and remember that we did that)

        int lastIndex = stopSlot;
        if( lastIndex >= 0  ) {
	  Unit lastUnit = dest.getUnit(lastIndex);
	  if(lastUnit instanceof Note) {
	      Note note = (Note)lastUnit;
	      if(note.nonRest()) {
		  // stopIndex = (lastIndex/(metre*BEAT)+2)*metre*BEAT;
		  Unit lastSourceUnit = source.getPrevUnit(source.size());
		  stopIndex = lastIndex + lastSourceUnit.getRhythmValue();
		  if(stopIndex < dest.size() &&
		     dest.getNextIndex(lastIndex) > stopIndex) {
		      dest.setUnit(stopIndex, new Rest());
		      restInserted = true;
		  }  
	      }
          }        
        }
      }
    }

    /**
     * Undoes the reversal.
     */
    public void undo() {
        if( source != null && dest != null )
          {
	  if(restInserted)
	      dest.delUnit(stopIndex);

	  dest.pasteSlots(oldSection, startSlot);
          }
    }

    /**
     * Redoes the reversal
     */
    public void redo() {
        if( source != null && dest != null )
          {
	  dest.pasteOver(source, startSlot);

	  if(restInserted)
	      dest.setUnit(stopIndex, new Rest());
          }

    }

    public boolean isUndoable() {
        return undoable;
    }
}
