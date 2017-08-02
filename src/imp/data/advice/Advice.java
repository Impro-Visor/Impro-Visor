/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
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
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package imp.data.advice;

import imp.*;
import imp.com.*;
import imp.data.MelodyPart;
import imp.data.NoteSymbol;
import imp.data.Part;
import imp.gui.Notate;
import polya.*;

/**
 * Describes and contains a piece of Advice, has mechanisms to turn Advice
 * into practical form that can be inserted into Score.
 *
 * @see NoteAdvice
 * @see Advisor
 * @see Polylist
 * @author Stephen Jones
 */
public class Advice implements Constants
  {

    /**
     * the name of the Advice that will be displayed in an Advice tree
     */
    protected String name;

    protected String chordRoot;

    /**
     * Creates a new piece of Advice.
     *
     * @param name a String containing the display name for the Advice
     */
    public Advice(String name)
    {
        this.name = name;
    }

    /**
     * Prints out the name of this piece of Advice
     *
     * @return String the name of the Advice
     */
    @Override
    public String toString()
    {
        // The blank is so that this entry will NOT be selected by letter in the advice window.
        return " " + name;
    }

    /**
     * Gets the name of the piece of Advice
     *
     * @return String the name of the Advice
     */
    public String getName()
    {
        return name;
    }

    /**
     * Inserts the Advice into the Part at a specified index
     *
     * @param part   the Part to insert into
     * @param index  the index at which to insert
     * @param cm
     * @param notate
     */
    public void insertInPart(Part part,
                             int index,
                             CommandManager cm,
                             Notate notate)
    {
        Polylist rhythm = notate.getAdviceRhythms();
        //System.out.println("insertInPart " + rhythm);
        String adviceTargetAsString = notate.getAdviceTargetCenter();
        int adviceTargetMidiValue = NoteSymbol.makeNoteSymbol(
                adviceTargetAsString).getMIDI();
        cm.execute(new SafePasteCommand(getPart(adviceTargetMidiValue, rhythm),
                                        part, index,
                                        !notate.getAlwaysPasteOver(), true,
                                        notate));
    }

    /**
     * Converts the Advice into a Part and returns that.
     *
     * @return Part the Advice in Part form, ready to be inserted
     */
    public Part getPart(int adviceTargetMidiValue)
    {
        throw new UnsupportedOperationException("No default Advice to Part "
                + "conversion.  Use a specific type of Advice.");
    }

    /**
     * Converts the Advice into a Part and returns that.
     *
     * @return Part the Advice in Part form, ready to be inserted
     */
    public Part getPart(int adviceTargetMidiValue, Polylist rhythm)
    {
        throw new UnsupportedOperationException("No default Advice to Part "
                + "conversion.  Use a specific type of Advice.");
    }

    /**
     * Converts the Advice into a Part and returns that
     *
     * @return Part the Advice in Part form, ready to be inserted
     */
    public MelodyPart getPart()
    {
        throw new UnsupportedOperationException("No default Advice to Part "
                + "conversion.  Use a specific type of Advice.");
    }
    
    /**
     * Return a string representing just the durations, for use in 
     * rhythm advice field.
     * @return 
     */

    public String toDurations()
    {
        return getPart().toDurations();
    }
  }
