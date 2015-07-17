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
 * A Command that sideslips a part (adds a transposed version to the end of it)
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CopyCommand
 * @see         CutCommand
 * @author      Amelia Sheppard
 */
public class SideSlipCommand implements Command, Constants {

    /**
     * the melody passed in (the one that is motified)
     */
    protected MelodyPart source;

    /**
     * the adjusted melody that's tacked onto dest
     */
    protected MelodyPart dest;

    /**
     * the original melody part
     */
    protected MelodyPart original;
    
    
    /**
     * true since the pasting can be undone
     */
    protected boolean undoable = true;

    /**
     * the direction in which the sideslip will occur
     */
    protected String direction;
    
    /**
     * the number of steps (distance) that the part will be transposed
     */
    protected int distance;
    
    
    /**
     * the key signature 
     */
    protected int keySig;
    
    /**
     * the command manager
     */
    protected CommandManager cm;
   
    /**
     * Creates a new Command that can bar line shift a melody part.
     * @param source            the Part to paste
     * @param startSlot         the slot to start pasting over in the dest
     * @param stopSlot          the slot to end pasting over in the dest
     * @param direction         the direction ("up" or "down") to transpose the second half
     * @param distance          the number of half steps to transpose the melody
     * @param keySig            the key signature that the solo is being played in
     * @param cm                the command manager used
     */
    public SideSlipCommand(MelodyPart source,String direction, int distance, int keySig, CommandManager cm) {
        this.source = source;
        this.dest = source.copy();
        this.original = source.copy();
        this.direction = direction;
        this.distance = distance;
        this.keySig = keySig;
        this.cm = cm;
    }

    /**
     * Creates a new Command that can paste a source Part over a section
     * of a destination Part.
     * @param source            the Part to paste
     * @param dest              the Part to paste over
     * @param startSlot         the slot to start pasting over in the dest
     * @param undoable          the boolean to see if the action is undoable
     */
    public SideSlipCommand(MelodyPart source, boolean undoable, String direction, 
                                     int distance, int keySig, CommandManager cm) {
        this(source, direction, distance, keySig, cm);
        this.undoable = undoable;
        Trace.log(2, "creating SideSlipCommand");
    }

    /**
     * applies the transformation to the source MelodyPart
     */
    public void execute() {
        Trace.log(2, "executing SideSlipCommand");
      
        int start = original.getSize();
       
        if (direction.equals("up"))
        {//slide up
            cm.execute(new ShiftPitchesCommand(distance, dest, 
                            0, start, 0, 128, keySig));
        }
        else if (direction.equals("down"))
        {//slide down
            cm.execute(new ShiftPitchesCommand(-1*distance, dest, 
                            0, start, 0, 128, keySig));
        }

        //add two of the same melodies with one transposed
        source.setSize(dest.getSize()*2);
        source.pasteOver(dest, start);
    }

    /**
     * Undoes the side slip.
     */
    public void undo() {
        if( source != null && dest != null )
          {
	  dest = original;
          }
    }

    /**
     * Redoes the side slip.
     */
    public void redo() {
        if( source != null && dest != null )
          {
	  dest.pasteOver(source, source.getSize());

          }

    }

    public boolean isUndoable() {
        return undoable;
    }
}
