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

import imp.*;
import imp.data.*;
import imp.util.Trace;

/**
 * A Command that pastes a Part over a section of a destination Part.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CopyCommand
 * @see         CutCommand
 * @author      Stephen Jones
 */
public class PasteCommand implements Command, Constants {

    /**
     * the Part to paste over
     */
    protected Part source;

    /**
     * the Part to paste onto
     */
    protected Part dest;

    /**
     * the section of the destination overwritten when the paste occurs
     */
    protected Part oldSection;

    /**
     * the chord Part to paste over
     */
    protected Part chordSource;

    /**
     * the chord Part to paste onto
     */
    protected Part chordDest;

    /**
     * the section of the chord destination overwritten when the paste occurs
     */
    protected Part oldChordSection;

    /**
     * Command used to delete the units pasted over
     */
    protected DeleteUnitsCommand deleteUnits;

    /**
     * the slot to start pasting over in the destination
     */
    protected int startSlot;

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
    protected boolean undoable = false;

    /**
     * whether to play when pasting
     */
    protected boolean play = false;

    /**
     * Creates a new Command that can paste a source Part over a section
     * of a destination Part.
     * @param source            the Part to paste
     * @param dest              the Part to paste over
     * @param startSlot         the slot to start pasting over in the dest
     */
    public PasteCommand(Part source, Part dest, int startSlot, boolean play) {
        this.source = source;
        this.dest = dest;
        this.startSlot = startSlot;
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
    public PasteCommand(Part source, Part dest, int startSlot, 
                                     boolean undoable, boolean play) {
        this(source, dest, startSlot, play);
        this.undoable = undoable;
        Trace.log(2, "creating Paste Command");
    }

    /**
     * Pastes the source onto the section of the destination, preserving the
     * section that is pasted over, and making a copy of the source for
     * undoing and redoing purposes.
     */
    public void execute() {
      if( source != null && dest != null && source.size() > 0 )
        {
        Trace.log(1, "pasting notes starting at " + startSlot 
                   + ", source size = " + source.size() + ", dest size = " + dest.size());
        source = source.copy();
        oldSection = dest.extractSlots(startSlot, 
                                       startSlot + source.size() - 1);

        // if the source won't fit, then we should resize the source
        if(oldSection.size() < source.size())
            source.setSize(oldSection.size());

        dest.pasteOver(source, startSlot);
       
        // if we're inserting a Note, then we want to make sure it stops
        // auto-expanding after two measures (and remember that we did that)
        int lastIndex = dest.getPrevIndex(startSlot + source.size());
        if( lastIndex >= 0  ) {
	  Unit lastUnit = dest.getUnit(lastIndex);
	  if(lastUnit instanceof Note) {
	      Note note = (Note)lastUnit;
	      if(note.nonRest()) {
		  // stopIndex = (lastIndex/(metre*BEAT)+2)*metre*BEAT;
		  Unit lastSourceUnit = source.getPrevUnit(source.size());
                  if( lastSourceUnit != null )
                    {
		    stopIndex = lastIndex + lastSourceUnit.getRhythmValue();
		    if(stopIndex < dest.size() &&
		       dest.getNextIndex(lastIndex) > stopIndex) 
                      {
		      dest.setUnit(stopIndex, new Rest());
		      restInserted = true;
                      }
		  }  
	      }
          }        
        }
      }
      
    if( chordSource != null && chordDest != null )
      {
        Trace.log(1, "pasting chords starting at " + startSlot 
                   + " source size = " + chordSource.size() 
                   + ", dest size = " + chordDest.size());

      oldChordSection = chordDest.extractSlots(startSlot, 
                                      startSlot + chordSource.size() - 1);

      chordDest.pasteOver(chordSource, startSlot);
      }
    }

    /**
     * Undoes the pasting.
     */
    public void undo() {
// For the time being, undo of chord pasting is disabled.
// The reason is that undoing a recent paste causes a crash
// wherein 120 slots appear and the application effectively freezes.

//        if( source != null && dest != null )
//          {
//	  if(restInserted)
//	      dest.delUnit(stopIndex);
//
//	  dest.pasteSlots(oldSection, startSlot);
//          }
//
//        if( chordSource != null && chordDest != null )
//          {
//          chordDest.pasteSlots(oldChordSection, startSlot);
//          }
    }

    /**
     * Redoes the pasting.
     */
    public void redo() {
        if( source != null && dest != null )
          {
	  dest.pasteOver(source, startSlot);

	  if(restInserted)
	      dest.setUnit(stopIndex, new Rest());
          }

        if( chordSource != null && chordDest != null )
          {
          chordDest.pasteOver(chordSource, startSlot);
          }
    }

    public boolean isUndoable() {
        return undoable;
    }
}
