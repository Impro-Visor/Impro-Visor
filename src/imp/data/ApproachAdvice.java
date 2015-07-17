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

package imp.data;

import polya.*;

/**
 * Describes and contains a piece of Advice that is an approach tone
 * and a target tone, has mechanisms to turn Advice into practical
 * form that can be inserted into Score.
 * @see         Advice
 * @see         Advisor
 * @see         Polylist
 * @author      Stephen Jones
 */
public class ApproachAdvice extends Advice {

    /**
     * Durations of each tone
     */
    private static final int APPROACH_DURATION = BEAT/2;
    private static final int TARGET_DURATION = BEAT;
    
    /**
     * the approach tone
     */
    private NoteSymbol approach;

    /**
     * the target tone
     */
    private NoteSymbol target;

    /**
     * Creates a new piece of Advice
     * @param name      a String containing the display name for the Advice
     * @param approach  a NoteSymbol containing the approach tone
     * @param target    a NoteSymbol containing the target tone
     */
    public ApproachAdvice(String name, NoteSymbol approach, NoteSymbol target) {
        super(name);
        this.approach = approach;
        this.target = target;
    }

    /**
     * Converts the Advice into a Part and returns that
     * @return Part     the Advice in Part form, ready to be inserted
     */
    public Part getPart() {
        Part newPart = new Part();
        Note appNote = approach.toNote(); //Key.makeNote(approach, C4, APPROACH_DURATION);
        Note tarNote = target.toNote(); // Key.makeNote(target, C4, TARGET_DURATION);
        int diff = appNote.getPitch() - tarNote.getPitch();
        if( diff < -3 )
          appNote.setPitch(appNote.getPitch()+OCTAVE);
        else if( diff > 3 )
          appNote.setPitch(appNote.getPitch()-OCTAVE);
        appNote.setRhythmValue(APPROACH_DURATION);
        tarNote.setRhythmValue(TARGET_DURATION);
        newPart.addUnit(appNote);
        newPart.addUnit(tarNote);
        return newPart;
    }
}
