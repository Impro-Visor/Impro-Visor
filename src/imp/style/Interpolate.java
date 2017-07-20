/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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
package imp.style;

import imp.data.Chord;
import imp.data.ChordPart;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.style.stylePatterns.Interpolant;
import java.util.ArrayList;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 *
 * @author Isys Johnson
 */
public class Interpolate
  {


    public static Polylist ArraytoPoly(ArrayList array)
    {
        PolylistBuffer buffer = new PolylistBuffer();
        for( Object item : array )
          {
            buffer.append(item);
          }
        return buffer.toPolylist();
    }

    //returns a paired list of a chord and a boolean polylist that will tell where
    //the interpolants will lie depending on the block that the chord falls in
    public static Polylist willInterpolate(ChordPart chordPart, int index, int minVal, float prob)
    {
        
        if( chordPart.getChord(index) == null )
          {
            return Polylist.nil;
          }
        
        Chord currentChord = chordPart.getChord(index);

        
        //finds the number of "slots" available for interpolations depending on the
        //specified minimum duration within the style file
        //for example if minimum slots is 120, then a chord with a duration of 480 would be able
        //to put in 3 interpolations max
        //but if the minimum slots value was 60, then there would be a max of 7 new interpolations that are
        //able to be added
        int steps;
        Polylist list = Polylist.nil;

        if( currentChord.getRhythmValue() <=  minVal)
          {
            steps = 0;
          }
        else
          {
            steps = currentChord.getRhythmValue() / minVal - 1;
          }
        
        //at each step, using probability and the block, we will determine whether or not a interpolation should be available at
        //a that step
        int willInterpolate = (int) (Math.random() * 100 + 1);
        if((willInterpolate > (prob*100) || steps == 0) && (chordPart.getNextChord(index) != null))
        {
            list = list.cons(false);
            Polylist result = Polylist.list(currentChord, list);
            index += currentChord.getRhythmValue();
            return willInterpolate(chordPart, index, minVal, prob).cons(result);
        }
        
        list = list.cons(true);
        
              
          
        
        //zips together the current chord and it's boolean list and places them into a Polylist
        //for example, if the first chord is Fm7 with a duration of 480, minVal of 120, and
        //and no interpolations are to be added
        //then that chord would be in the resulting list as ((Fm7 (false false false)) ... rest of results list)
        Polylist result = Polylist.list(currentChord, list);
        index += currentChord.getRhythmValue();
        return willInterpolate(chordPart, index, minVal, prob).cons(result);
    } // willInterpolate
  }
