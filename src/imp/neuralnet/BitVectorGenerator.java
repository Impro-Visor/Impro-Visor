/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

package imp.neuralnet;

import imp.Constants;
import imp.data.Note;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created in July 2013  
 * @author Hayden Blauzvern
 */
public class BitVectorGenerator implements Constants {
    
    public static String WHOLE_REST = "1 0 0 0 0 0 0 1 1 1 1 1 1 1 0 0 0 0 0 ";
    
    /**
     * Saves note data as an 18-bit vector.
     * Bit 1      - Rest/Not rest
     * Bit 2      - Sonorous/Dissonant
     * Bit 3      - Chord/Color or Approach/Foreign Tone
     * Bit 4-7    - Distance from previous note, capped at 15
     * Bit 8-13   - Thermometer encoding for beat placement
     *            --- Beat 1        = 6 ('1 1 1 1 1 1')
     *            --- Beat 3        = 5 ('1 1 1 1 1 0')
     *            --- Beat 2/4      = 4 ('1 1 1 1 0 0')
     *            --- Upbeat of 2/4 = 3 ('1 1 1 0 0 0')
     *            --- Upbeat of 1/3 = 2 ('1 1 0 0 0 0')
     *            --- Offbeat 16th/triplets = ('1 0 0 0 0 0')
     * Beat 14-19 - Represents type of note in the following way:
     *              WHOLE-HALF-QUARTER-EIGHTH-SIXTEENTH-TRIPLET
     * @param out
     * @param notePrev Used for note distance
     * @param noteCurr Current note
     * @param classification Classification (color) of note
     * @param beatPos Current position in the lick
     * @param error Keeps track of any potential note parsing errors
     * @return beatPos, to keep track of the current slot
     * @throws IOException 
     */
    public static int printNoteData(StringBuilder out, Note notePrev, Note noteCurr, 
                      int classification, int beatPos, AtomicBoolean error) {
        
        char[] bits = {'0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' '};
        int currPos = 0;

        // The first three bits are for note classification   
        if (noteCurr.isRest())
        {
            bits[currPos] = '1';
            currPos += 6;
        }
        
        else
        {
            currPos += 2;

            if (classification == CHORD_TONE)
            {
                bits[currPos] = '1';
                currPos += 2;
                bits[currPos] = '1';
                currPos += 2;
            }
            else if (classification == COLOR_TONE)
            {
                bits[currPos] = '1';
                currPos += 2;
                bits[currPos] = '0';
                currPos += 2;
            }
            else if (classification == APPROACH_TONE)
            {
                bits[currPos] = '0';
                currPos += 2;
                bits[currPos] = '1';
                currPos += 2;
            }
            else if (classification == FOREIGN_TONE)
            {
                bits[currPos] = '0';
                currPos += 2;
                bits[currPos] = '0';
                currPos += 2;
            }
        }
        
        // The next 4 bits dictate the distance of the note from a preceding note
        int distance = 0;
        if(notePrev != null)
             if(!noteCurr.isRest() && !notePrev.isRest())
        {
            distance = Math.abs(noteCurr.getPitch() - notePrev.getPitch());
        }
        // In case the note distance is too large
        if (distance > 15)
        {
            distance = 15;
        }

        char[] binaryInts = {'0', '0', '0', '0'};
        char[] intToBinary = Integer.toBinaryString(distance).toCharArray();
        
        int difference = binaryInts.length - intToBinary.length;
        System.arraycopy(intToBinary, 0, binaryInts, difference, intToBinary.length);
        for (char b : binaryInts)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Determine a 5-bit thermometer encoding for the placement of the note on the beat
        // FIX: For quarter triplets, what am I encoding them as?
        String encoding;
        if (beatPos % WHOLE == 0) //Beat 1
        {
            encoding = "111111";
        }
        else if (beatPos % WHOLE == HALF) //Beat 3
        {
            encoding = "111110";
        }
        else if (beatPos % WHOLE == QUARTER 
                 || beatPos % WHOLE == QUARTER + HALF) //Beat 2 or 4
        {
            encoding = "111100";
        }
        else if (beatPos % WHOLE == EIGHTH + QUARTER 
                 || beatPos % WHOLE == EIGHTH + QUARTER + HALF) //Upbeat 2 or 4
        {
            encoding = "111000";
        }
        else if (beatPos % WHOLE == EIGHTH 
                 || beatPos % WHOLE == EIGHTH + HALF) //Upbeat 1 or 3
        {
            encoding = "110000";
        }
        else
        {
            encoding = "100000";
        }
        char[] encodingBits = encoding.toCharArray();
        for (char b : encodingBits)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Determine 6-bit beat durations and encode them as such:
        // WHOLE HALF QUARTER EIGHTH SIXTEENTH TRIPLET
        // If the note is not one of these, it leaves the duration encoding
        // as zeros.
        char[] durationEncoding = {'0', '0',  '0', '0', '0', '0'};
        
        StringBuilder buffer = new StringBuilder();
        int duration = noteCurr.getRhythmValue();
        int value = Note.getDurationString(buffer, duration);

        if (value != 0)
        {
            System.out.println("Extra residual unaccounted for.");
        }
        
        String noteValues;
        if(buffer.toString().matches("\\+.*"))
        {
            noteValues = buffer.toString().substring(1); //Trims leading "+"
        }
        else
        {
            noteValues = buffer.toString();
        }
        String[] values = noteValues.split("\\+");
        
        for (String note : values)
        {
            if (note.equals("1"))
                durationEncoding[0] = '1'; //WHOLE note
            else if (note.equals("2"))
                durationEncoding[1] = '1'; //HALF note
            else if (note.equals("4")) 
                durationEncoding[2] = '1'; //QUARTER note
            else if (note.equals("8")) 
                durationEncoding[3] = '1'; //EIGHTH note
            else if (note.equals("16")) 
                durationEncoding[4] = '1'; //SIXTEENTH note
            else if (note.equals("2/3"))
            {
                durationEncoding[1] = '1'; //HALF_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("4/3"))
            {
                durationEncoding[2] = '1'; //QUARTER_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("8/3"))
            {
                durationEncoding[3] = '1'; //EIGHTH_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("16/3"))
            {
                durationEncoding[4] = '1'; //SIXTEENTH_TRIPLET
                durationEncoding[5] = '1';
            }
                
            else
            {
                // error.set(true);
            }
        }
        for (char b : durationEncoding)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Used for debugging
        /*
        for (char b : bits)
            System.out.print(b);
        System.out.println();
        */
        
        // Write the data
        out.append(bits);
        
        // Move the beat position up by the current duration.
        beatPos += duration;
        return beatPos;
    }
}
