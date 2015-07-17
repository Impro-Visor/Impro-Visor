/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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

package imp.lickgen;

import java.util.ArrayList;
import java.util.Arrays;
import polya.Polylist;

/**
 *
 * @author David Halpern 2012
 */
public class Generator {

    private static int[] WEIGHTS = {0, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5, -1, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5};
    private static int WHOLE_WEIGHT = 0;
    private static int HALF_WEIGHT = 1;
    private static int QUARTER_WEIGHT = 2;
    private static int EIGHTH_WEIGHT = 3;
    private static int SIXTEENTH_WEIGHT = 4;
    private static int THIRTY_SECOND_WEIGHT = 5;
    private static int NUM_SLOTS = WEIGHTS.length;
    private static int MOD = 8;
    private static int NUM_QUARTERS = 4;
    private static int NUM_SLOTS2 = 120;
    private static double HIGHTEST_NOTE_PROBABILITY = .95;
    private static double APPROACH_PROBABILITY = .5;
    private static int THIRTY_SECOND = 1;
    private static String THIRTY_SECOND_NOTE = "32";
    private static String THIRTY_SECOND_REST = "R32";
    private static int SIXTEENTH = 2;
    private static String SIXTEENTH_NOTE = "16";
    private static String SIXTEENTH_NOTE_APPROACH = "A16";
    private static String SIXTEENTH_REST = "R16";
    private static int EIGHTH = 4;
    private static String EIGHTH_NOTE = "8";
    private static String EIGHTH_NOTE_APPROACH = "A8";
    private static String EIGHTH_REST = "R8";
    private static int QUARTER = 8;
    private static String QUARTER_NOTE = "4";
    private static String QUARTER_REST = "R4";
    private static int HALF = 16;
    private static String HALF_NOTE = "2";
    private static String HALF_REST = "R2";
    private static int WHOLE = 32;
    private static String WHOLE_NOTE = "1";
    private static String WHOLE_REST = "R1";

    public Generator() {
    }

    /**
     * Generates a string for use in grammars from a given rhythm
     *
     * @param rhythm
     * @return
     */
    public static String[] generateString(int[] rhythm, String noteType) 
    {
        noteType = noteType.toUpperCase();
        boolean approach = false;
        if(noteType.equals("C"))
        {
            approach = true;
        }
        String rhythms = "";
        int prev = -1;
        for (int i = 0; i < rhythm.length; i++) {
            if (rhythm[i] == 1) {
                if (prev != -1) {
                    int diff = i - prev;
                    if((WEIGHTS[prev % NUM_SLOTS] * -1) >= EIGHTH_WEIGHT)
                    {
                        rhythms += getString(diff, false, true, noteType, approach);
                    }
                    else
                    {
                        rhythms += getString(diff, false, false, noteType, approach);
                    }
                    prev = i;
                } 
                else if (i > 0) 
                {
                    rhythms += getString(i, true, false, noteType, approach);
                }
                prev = i;
            }
        }
        if (prev == -1) 
        {
            rhythms += getString(rhythm.length, true, false, noteType, approach);
        } 
        else if (prev != rhythm.length) 
        {
            int diff = rhythm.length - prev;
            if((WEIGHTS[prev % NUM_SLOTS] * -1) >= EIGHTH_WEIGHT)
            {
                rhythms += getString(diff, false, true, noteType, approach);
            }
            else
            {
                rhythms += getString(diff, false, false, noteType, approach);
            }
        }
        String[] rhythmsArray = rhythms.split("\\s+");
        return rhythmsArray;
    }
    
    /**
     * Converts a string of rhythms in the form used in grammars to a representation of the same rhythm in 1's 
     * and 0's
     * @param rhythmString
     * @return 
     */
    public static int[] getArray(Polylist rhythmString)
    {
        ArrayList<Integer> r = getArray(rhythmString, new ArrayList<Integer>());
        int[] rhythms = new int[r.size()];
        for(int i = 0; i < rhythms.length; i ++)
        {
            rhythms[i] = r.get(i);
        }
        return rhythms;
    }
    
    /**
     * Private helper method for above getArray(rhythmString). Converts grammar notation to array of 1 with 
     * a number of 0's after. For example, X8 becomes [1 0 0 0]
     * @param rhythmString
     * @param r
     * @return 
     */
    private static ArrayList<Integer> getArray(Polylist rhythmString, ArrayList<Integer> r)
    {
        ArrayList<Integer> rhythms = r;
        String rString = (String) rhythmString.first();
        rString = rString.substring(1);
        if(rString.equals(WHOLE_NOTE))
        {
            rhythms.add(1);
            for(int i = 1; i < WHOLE; i ++)
            {
                rhythms.add(0);
            }
        }
        else if(rString.equals(HALF_NOTE))
        {
            rhythms.add(1);
            for(int i = 1; i < HALF; i ++)
            {
                rhythms.add(0);
            }
        }
        else if(rString.equals(QUARTER_NOTE))
        {
            rhythms.add(1);
            for(int i = 1; i < QUARTER; i ++)
            {
                rhythms.add(0);
            }
        }
        else if(rString.equals(EIGHTH_NOTE))
        {
            rhythms.add(1);
            for(int i = 1; i < EIGHTH; i ++)
            {
                rhythms.add(0);
            }
        }
        else
        {
            rhythms.add(1);
            for(int i = 1; i < SIXTEENTH; i ++)
            {
                rhythms.add(0);
            }
        }
        if(rhythmString.rest().nonEmpty())
        {
            return getArray(rhythmString.rest(), rhythms);
        }
        else
        {
            return rhythms;
        }
    }

    /**
     * Generates a string for a note length given the inter-onset interval and
     * whether or not it is a rest
     *
     * @param diff
     * @param rest
     * @return
     */
    private static String getString(int diff, boolean rest, boolean offbeat, String noteType, boolean approach) 
    {
        if (!rest) {
            if (diff >= WHOLE) 
            {
                return noteType + WHOLE_NOTE + " " + getString(diff - WHOLE, true, false, noteType, approach) + " ";
            } 
            else if (diff >= HALF) 
            {
                return noteType + HALF_NOTE + " " + getString(diff - HALF, true, false, noteType, approach) + " ";
            } 
            else if (diff >= QUARTER) 
            {
                return noteType + QUARTER_NOTE + " " + getString(diff - QUARTER, true, false, noteType, approach) + " ";
            } 
            else if (diff >= EIGHTH) 
            {
                if(offbeat && approach)
                {
                    if(Math.random() > APPROACH_PROBABILITY)
                    {
                        return EIGHTH_NOTE_APPROACH + " " + getString(diff - EIGHTH, true, false, noteType, approach) + " ";
                    }
                    else
                    {
                        return noteType + EIGHTH_NOTE + " " + getString(diff - EIGHTH, true, false, noteType, approach) + " ";
                    }
                }
                else
                {
                    return noteType + EIGHTH_NOTE + " " + getString(diff - EIGHTH, true, false, noteType, approach) + " ";
                }
            } 
            else if (diff >= SIXTEENTH) 
            {
                if(offbeat && approach)
                {
                    if(Math.random() > APPROACH_PROBABILITY)
                    {
                        return SIXTEENTH_NOTE_APPROACH + " " + getString(diff - SIXTEENTH, true, false, noteType, approach) + " ";
                    }
                    else
                    {
                        return noteType + SIXTEENTH_NOTE + " " + getString(diff - SIXTEENTH, true, false, noteType, approach) + " ";
                    }
                }
                else
                {
                    return noteType + SIXTEENTH_NOTE + " " + getString(diff - SIXTEENTH, true, false, noteType, approach) + " ";
                }
            } 
            else if (diff >= THIRTY_SECOND) 
            {
                return THIRTY_SECOND_NOTE + " " + getString(diff - THIRTY_SECOND, true, false, noteType, approach) + " ";
            } else 
            {
                return "";
            }
        } else {
            if (diff >= WHOLE) 
            {
                return WHOLE_REST + " " + getString(diff - WHOLE, true, false, noteType, approach);
            } 
            else if (diff >= HALF) 
            {
                return HALF_REST + " " + getString(diff - HALF, true, false, noteType, approach);
            } 
            else if (diff >= QUARTER) 
            {
                return QUARTER_REST + " " + getString(diff - QUARTER, true, false, noteType, approach);
            } 
            else if (diff >= EIGHTH) 
            {
                return EIGHTH_REST + " " + getString(diff - EIGHTH, true, false, noteType, approach);
            } 
            else if (diff >= SIXTEENTH) 
            {
                return SIXTEENTH_REST + " " + getString(diff - SIXTEENTH, true, false, noteType, approach);
            } 
            else if (diff >= THIRTY_SECOND) {
                return THIRTY_SECOND_REST + " " + getString(diff - THIRTY_SECOND, true, false, noteType, approach);
            } 
            else {
                return "";
            }
        }
    }

    /**
     * Generates a rhythm that has a length of measures and a syncopation value
     * of mySynco starting with rArray
     *
     * @param measures
     * @param mySynco
     * @return
     */
    public static int[] generateSyncopation(int measures, int mySynco, int[] rArray) 
    {
        int[] rhythm = rArray;
        int synco = Tension.getSyncopation(rhythm, measures);
        //If desired syncopation is lower than the current syncopation value,...
        while (synco > mySynco && synco > 2)
        {
            //...pick a random slot in the rhythm array,...
            int i = (int) (Math.random() * NUM_SLOTS * measures);
            if (i < NUM_SLOTS * measures - 1) 
            {
                int index = i;
                int desiredWeight = (WEIGHTS[index % NUM_SLOTS] * -1) - 1;
                //...find the next slot with the desired weight...
                if (WHOLE_WEIGHT <= desiredWeight && desiredWeight < EIGHTH_WEIGHT) {
                    while ((WEIGHTS[i % NUM_SLOTS] * -1) != desiredWeight) {
                        i++;
                    }
                    int prevI = 0;
                    int prevIndex = rhythm[index];
                    if (i < rhythm.length) 
                    {
                        prevI = rhythm[i];
                        //...and put a note in that slot...
                        rhythm[i] = 1;
                        //...and remove the original note...
                        rhythm[index] = 0;
                        if(index >= 2 && i <= rhythm.length - 3 && (rhythm[index - SIXTEENTH] == 1 || rhythm[index + SIXTEENTH] == 1))
                        {
                            rhythm[index] = 1;
                        }
                    }
                    int synco2 = Tension.getSyncopation(rhythm, measures);
                    //Undo move if it actually increases syncpation
                    if(synco2 > synco)
                    {
                        rhythm[index] = prevIndex;
                        rhythm[i] = prevI;
                    }
                    synco = Tension.getSyncopation(rhythm, measures);
                }
            }
        }
        //Same as above but reversed in order to increase syncopation
        while (synco < mySynco) {
            int i = (int) (Math.random() * NUM_SLOTS * measures);
            if (i < NUM_SLOTS * measures - 1) 
            {
                int index = i;
                int desiredWeight = (WEIGHTS[index % NUM_SLOTS] * -1) - 1;
                if (WHOLE_WEIGHT <= desiredWeight && desiredWeight < EIGHTH_WEIGHT) {
                    while ((WEIGHTS[i % NUM_SLOTS] * -1) != desiredWeight) {
                        i++;
                    }
                    int prevI = 0;
                    int prevIndex = rhythm[index];
                    if (i < rhythm.length) 
                    {
                        prevI = rhythm[i];
                        rhythm[index] = 1;
                        rhythm[i] = 0;
                        if(i >= 2 && i <= rhythm.length - 3 && (rhythm[i - SIXTEENTH] == 1 || rhythm[i + SIXTEENTH] == 1))
                        {
                            rhythm[i] = 1;
                        }
                    }
                    int synco2 = Tension.getSyncopation(rhythm, measures);
                    if(synco2 < synco)
                    {
                        rhythm[index] = prevIndex;
                        rhythm[i] = prevI;
                    }
                    synco = Tension.getSyncopation(rhythm, measures);
                }
            }
        }
        return rhythm;
    }
    
    /**
     * Generates a rhythm that has a length of measures and a syncopation value
     * of mySynco starting with a randomly generated rhythm
     *
     * @param measures
     * @param mySynco
     * @return
     */
    public static int[] generateSyncopation(int measures, int mySynco) {
        int[] rhythm = generateRhythm(measures);
        return generateSyncopation(measures, mySynco, rhythm);
    }

    /**
     * Generates a random rhythm of measures length
     *
     * @param measures
     * @return
     */
    public static int[] generateRhythm(int measures) 
    {
        int[] rhythm = new int[measures * NUM_SLOTS];
        int prevIndex = 0;
        for (int i = 0; i < rhythm.length; i++) {
            int invMetHier = (WEIGHTS[i % NUM_SLOTS] * -1) + 1;
            double weight = 0;
            if (invMetHier <= SIXTEENTH_WEIGHT + 1) 
            {
                weight = (double) 1 / invMetHier;
            }
            if(invMetHier == SIXTEENTH_WEIGHT + 1 && prevIndex != i - SIXTEENTH)
            {
                    weight = 0;
            }
            if(invMetHier < SIXTEENTH_WEIGHT + 1 && prevIndex == i - SIXTEENTH)
            {
                    weight = 2;
            }
            double random = Math.random();
            if (random <= (weight * HIGHTEST_NOTE_PROBABILITY)) 
            {
                rhythm[i] = 1;
                prevIndex = i;
            } else {
                rhythm[i] = 0;
            }
        }
        return rhythm;
    }
    
//    public static int[] rArray = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//    
//    public static int[] generateBoringRhythm(int measures)
//    {
//        int totalLength = rArray.length * measures;
//        int[] rhythmArray = Arrays.copyOf(rArray, totalLength);
//        int offset = rArray.length;
//        for(int i = measures; i > 1; i --)
//        {
//            System.arraycopy(rArray, 0, rhythmArray, offset, rArray.length);
//            offset += rArray.length;
//        }
//        return rhythmArray;
//    }
}
