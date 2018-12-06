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
import polya.*;

/**
 * An undoable Command that places a sequence of Chords at a certain 
 * position in a ChordPart.  

 * Works by creating a new Part, then pasting that, exploiting the
 * logic in paste.

 * @see         Command
 * @see         CommandManager
 * @see         Chord
 * @see         ChordPart
 * @author      Stephen Jones, Robert Keller
 */

// For the moment, we are going to do both chords and melody
// together.  Then we'll separate them.


public class SetChordsCommand extends PasteCommand
    {
    /**
     * the ChordPart in which to place the Chord
     */
    private ChordPart chordProg;

    /**
     * the list of Chords to place in the ChordPart
     */
    private Polylist chords;

    /**
     * the MelodyPart in which to place the notes
     */
    private MelodyPart melody;

    /**
     * the list of Notes to place in the MelodyPart
     */
    private Polylist notes;


    /**
     * Creates a new Command that can set the Chord in a ChordPart at the
     * specified indices.
     * @param sI        the index of the Chord to place
     * @param chordsAndMelody incoming Polylist of chords and melody mingled
     * @param chordPart the destination part for chords
     * @param melody    the destination part for melody
     */

    public SetChordsCommand(int sI, Polylist chordsAndMelody, ChordPart chordPart, MelodyPart melody) {

        super(null, chordPart, sI, true);	// undoable

        dest = melody;
        chordDest = chordPart;
        startSlot = sI;

        Polylist separatedChordsAndMelody 
            = Leadsheet.extractChordsAndMelody(chordsAndMelody);

        chords = (Polylist)separatedChordsAndMelody.first();

        notes = (Polylist)separatedChordsAndMelody.second();
        
        int[] metre = chordPart == null ? DEFAULT_METRE : chordPart.getMetre();
        int beatValue = (WHOLE/metre[1]);
        int measureLength = beatValue * metre[0];

        if( chordPart != null )
          {

	  // Note: The first bar may be only partial, depending on the
	  // current slot.
            


	  int thisMeasure = startSlot/measureLength;

	  int nextMeasure = 1 + thisMeasure;

	  int slotsAvailable = nextMeasure * measureLength - startSlot;

          chordSource = new ChordPart();

          Leadsheet.populatePartWithChords((ChordPart)chordSource, chords, slotsAvailable, measureLength);
          }

        if( melody != null )
          {
          source = new MelodyPart();

          Leadsheet.addToMelodyPart(notes.reverse(), (MelodyPart)source, 0, measureLength, Key.Ckey);
          }
        
    }
    
    /**
     * To avoid a bug, undoing and redoing chord pastes is temporarily disabled
     */
    @Override
    public void undo()
    {
      System.err.println("For now, undo is disabled for chord pasting. Delete the chord using X.");
     }
    
    @Override
    public void redo()
    {
    }
    
    @Override
    public boolean isUndoable() {
        return false;
    }
}
