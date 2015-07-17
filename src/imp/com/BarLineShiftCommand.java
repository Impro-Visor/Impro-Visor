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
 * A Command that bar line shifts a part (adds a shifted version to the end of it)
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         CopyCommand
 * @see         CutCommand
 * @author      Amelia Sheppard
 */
public class BarLineShiftCommand implements Command, Constants {

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
     * the direction in which the bar line shift will occur
     */
    protected String direction;
    
    /**
     * the number of beats (distance) that the part will be shifted
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
     * Creates a new Command that can bar line shift the orignal melody part
     * @param source            the Part to paste
     * @param direction         the direction ("up" or "down") to transpose the second half
     * @param distance          the number of half steps to transpose the melody
     */
    public BarLineShiftCommand(MelodyPart source, String direction, int distance) {
        this.source = source;
        this.dest = source.copy();
        this.original = source.copy();
        this.direction = direction;
        this.distance = distance;
    }

    /**
     * Creates a new Command that can bar line shift the orignal melody part
     * @param source            the Part that has bar line shift applied to it
     * @param direction         the direction ("up" or "down") to transpose the second half
     * @param distance          the number of half steps to transpose the melody
     * @param undoable          the boolean to see if the action is undoable
     */
    public BarLineShiftCommand(MelodyPart source, boolean undoable, String direction, int distance) 
    {
        this(source, direction, distance);
        this.undoable = undoable;
        Trace.log(2, "creating BarLineShiftCommand");
    }

    /**
     * applies the transformation to the MelodyPart source
     */
    public void execute() {
        Trace.log(2, "executing BarLineShiftCommand");
      
        
        int start = original.getSize();

        int shiftForwardBy = distance * 120;

        if (direction.equals("forwards"))
        {//shift forward
            MelodyPart addRest = new MelodyPart(shiftForwardBy+start);
            addRest.pasteSlots(dest, shiftForwardBy);

            source.setSize(start + addRest.getSize());
            source.pasteSlots(addRest, start);


        }
        else if (direction.equals("backwards"))
        {//shift backward
            int shiftBackTo = start- shiftForwardBy;
            source.setSize(start+dest.getSize());
            source.pasteSlots(dest, shiftBackTo);
        }
    }

    /**
     * Undoes the bar line shift
     */
    public void undo() {
        if( source != null && dest != null )
          {
	  dest = original;
          }
    }

    /**
     * Redoes the bar line shift
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
