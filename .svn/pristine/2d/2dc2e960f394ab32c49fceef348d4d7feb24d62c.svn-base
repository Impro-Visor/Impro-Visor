/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.lickgen;

import imp.Constants;
import imp.data.*;
import java.util.ArrayList;

/**
 *
 * @author David Halpern 2012
 */
public class Expectancy 
{
    
    public Expectancy()
    {       
    }
    
    /**
     * Setting some useful constants.
     */  
    private static final int P_OCTAVE = 12;     // 12 notes per octave
    private static final int A = 21;            // MIDI value of 1st key on keyboard
    private static final int C_EIGHTH = 108;    // MIDI value of last key on keyboard
    private static final int MIDDLE_C = 60;
    private static final int LOW_BASS = 24;
    private static final int ROOT_STABILITY = 6;   // Stability of a root note
    private static final int CHORD_STABILITY = 5;  // Stability of a chord tone
    //private static final int SCALE_STABILITY = 4;  // Stability of a scale tone
    private static final int COLOR_STABILITY = 4;  // Stability of a color tone
    private static final int OUTSIDE_STABILITY = 1;// Stability of an outside note
    private static final int NOCHORD_STABILITY = 1;// Stability of an outside note
    private static final double PROXIMITY_0 = 24;     // Proximity rating for a distance of 0
    private static final double PROXIMITY_1 = 36;     // Proximity rating for a distance of 1
    private static final double PROXIMITY_2 = 32;     // Proximity rating for a distance of 2
    private static final double PROXIMITY_3 = 25;     // Proximity rating for a distance of 3
    private static final double PROXIMITY_4 = 20;     // Proximity rating for a distance of 4
    private static final double PROXIMITY_5 = 16;     // Proximity rating for a distance of 5
    private static final double PROXIMITY_6 = 12;     // Proximity rating for a distance of 6
    private static final double PROXIMITY_7 = 9;      // Proximity rating for a distance of 7
    private static final double PROXIMITY_8 = 6;      // Proximity rating for a distance of 8
    private static final double PROXIMITY_9 = 4;      // Proximity rating for a distance of 9
    private static final double PROXIMITY_10 = 2;     // Proximity rating for a distance of 10
    private static final double PROXIMITY_11 = 1;     // Proximity rating for a distance of 11
    private static final double PROXIMITY_12 = 0.25;  // Proximity rating for a distance of 12
    private static final double PROXIMITY_13 = 0.02;  // Proximity rating for a distance of 13
    private static final double PROXIMITY_14 = 0.01;  // Proximity rating for a distance of 14
    private static final int DIRECTION_0 = 6;         // Direction rating for a distance of 0
    private static final int DIRECTION_1 = 20;        // Direction rating for a distance of 1
    private static final int DIRECTION_2 = 12;        // Direction rating for a distance of 2
    private static final int DIRECTION_3 = 6;         // Direction rating for a distance of 3
    private static final int DIRECTION_4 = 0;         // Direction rating for a distance of 4
    private static final int DIRECTION_5 = 6;         // Direction rating for a distance of 5
    private static final int DIRECTION_6 = 12;        // Direction rating for a distance of 6
    private static final int DIRECTION_7 = 25;        // Direction rating for a distance of 7
    private static final int DIRECTION_8 = 36;        // Direction rating for a distance of 8
    private static final int DIRECTION_9 = 52;        // Direction rating for a distance of 9
    private static final int DIRECTION_10 = 75;       // Direction rating for a distance of 75
    private static final double REPETITION = .67;     // Mobility rating for a repeated note   
    private static int noNote = -1; // DON'T CHANGE THIS VALUE. This is the same as the value
                             // that MelodyPart.getPitch() returns if the previous note
                             // was a rest, which is important.
    private static int suggestionNumMin = 0;
    private static int suggestionNumMax = 88;
    private static int suggestionNumInit = P_OCTAVE;
    private static int suggestionNum = suggestionNumInit;
    private static int QUARTER_NOTE = 120;
    private static int BASE_WEIGHT = 15;
    private static int QUARTER_WEIGHT = 5;
    
//    public static double getAverageExpectancy(int[] pitches)
//    {
//        
//    }
    
    public static double getExpectancy(int pitch, int prevPitch, int prevPrevPitch, Chord currentChord)
    {
        int stability = stability(pitch, currentChord);
        double proximity = proximity(pitch, prevPitch);
        int direction = direction(pitch, prevPitch, prevPrevPitch);
        double mobility = mobility(pitch, prevPitch);
        double expectancy = (stability * proximity * mobility) + direction;
        return expectancy;
    }
    
    public static double getExpectancyPerNote(MelodyPart currMelody, int lengthOfTrade, ChordPart chords, int currentSlot)
    {
        int quarter = 0;
        int[] quarterLevel = new int[4];
        double highestExpectancy = 0;
        int highestIndex = 0;
        int qLIndex = 0;
        int firstIndex = 0;
        while(!currMelody.getNote(firstIndex).nonRest() && firstIndex < lengthOfTrade)
        {
            firstIndex = currMelody.getNextIndex(firstIndex);
        }
        int secondIndex = currMelody.getNextIndex(firstIndex);
        while(!currMelody.getNote(secondIndex).nonRest() && secondIndex < lengthOfTrade)
        {
            secondIndex = currMelody.getNextIndex(secondIndex);
        }
        //System.out.println(currMelody.getNote(firstIndex));
        if(!currMelody.getNote(firstIndex).nonRest() || !currMelody.getNote(secondIndex).nonRest())
        {
            return -1;
        }
        Part.PartIterator pi = currMelody.iterator(secondIndex);
        int numPitches = 0;
        double totalExpectancy = 0;
        int firstQuarter = 0;
        int secondQuarter = 0;
        //Calculates expectancy of each following note
        while(pi.hasNext())
        {
            int nextIndex = pi.nextIndex() + currentSlot - lengthOfTrade;
            Chord c = chords.getCurrentChord(nextIndex);
            int first = currMelody.getNote(firstIndex).getPitch();
            int second = currMelody.getNote(secondIndex).getPitch();
            firstQuarter = first;
            secondQuarter = second;
            int curr = currMelody.getNote(pi.nextIndex()).getPitch();
            double mExpectancy = getExpectancy(curr, second, first, c);
            //Getting higher order expectancies
            if(quarter > QUARTER_NOTE)
            {
                firstQuarter = quarterLevel[highestIndex];
                secondQuarter = firstQuarter;
                quarter = quarter % QUARTER_NOTE;
                quarterLevel = new int[4];
                highestExpectancy = 0;
            }
            double quarterExpect = getExpectancy(curr, firstQuarter, secondQuarter, c);
            double weightedExpectancy = ((BASE_WEIGHT * mExpectancy) + (QUARTER_WEIGHT * quarterExpect)) / (BASE_WEIGHT + QUARTER_WEIGHT);
            quarterLevel[qLIndex] = curr;
            if(mExpectancy > highestExpectancy)
            {
                highestIndex = qLIndex;
                highestExpectancy = mExpectancy;
            }
            quarter += currMelody.getNote(pi.nextIndex()).getRhythmValue();
            totalExpectancy += weightedExpectancy;
            numPitches ++;
            firstIndex = secondIndex;
            secondIndex = pi.nextIndex();
            pi.next();
            while(!currMelody.getNote(secondIndex).nonRest() && pi.hasNext())
            {
                secondIndex = pi.nextIndex();
                pi.next();
            }
        }
        return (totalExpectancy/numPitches);
    }
    
    /**
     * Calculates the stability of a pitch within its chord context according to the Margulis algorithm. 
     * Returned values represent relative stability of different types of pitches.
     * @param pitch - MIDI value of current pitch
     * @param currentChord - current chord
     * @return the stability rating of the pitch
     */
    public static int stability (int pitch, Chord currentChord)
    {
        if(currentChord.getName().equals(Constants.NOCHORD))
            return NOCHORD_STABILITY;
        else if(isRoot(pitch, currentChord))
            return ROOT_STABILITY;
        else if(isChordTone(pitch, currentChord))
            return CHORD_STABILITY;
//        else if(isScaleTone(pitch, currentChord))
//            return SCALE_STABILITY;
        else if(isColorTone(pitch, currentChord))
            return COLOR_STABILITY;
        else
            return OUTSIDE_STABILITY;
    }
    
    /**
     * Calculates the proximity of a pitch to the previous pitch according to the Margulis algorithm. 
     * Returned values represent perceived closeness, something resembling an inverse logarithmic scale.
     * @param pitch - MIDI value of current pitch
     * @param prevPitch - MIDI value of previous pitch
     * @return the proximity rating of the pitch
     */
    public static double proximity (int pitch, int prevPitch)
    {
        int distance = Math.abs(pitch - prevPitch);
        switch (distance)
        {
            case 0: return PROXIMITY_0;
            case 1: return PROXIMITY_1;
            case 2: return PROXIMITY_2;
            case 3: return PROXIMITY_3;
            case 4: return PROXIMITY_4;
            case 5: return PROXIMITY_5;
            case 6: return PROXIMITY_6;
            case 7: return PROXIMITY_7;
            case 8: return PROXIMITY_8;
            case 9: return PROXIMITY_9;
            case 10: return PROXIMITY_10;
            case 11: return PROXIMITY_11;
            case 12: return PROXIMITY_12;
            case 13: return PROXIMITY_13;
            default: return PROXIMITY_14;
        }
    }
    
    /**
     * Calculates the direction value of a pitch given the interval that came before it according to the  
     * Margulis algorithm. If previous interval is less a major third, it is expected to continue in the  
     * same direction. If it is greater, it is expected to continue in the opposite direction. Returns a 
     * value of 0 if it does not go along with expectations. Otherwise, returns a value associated with 
     * relative likelihood of interval.
     * @param pitch - MIDI value of the current pitch
     * @param prevPitch - MIDI value of the previous pitch
     * @param prevPrevPitch - MIDI value of the pitch before the previous pitch
     * @return - the direction rating of the pitch
     */
    public static int direction (int pitch, int prevPitch, int prevPrevPitch)
    {
        int intervalSize = Math.abs(prevPitch - prevPrevPitch);
        int direction = prevPitch - prevPrevPitch;
        if(intervalSize <= 4)
        {
            if(direction < 0)
            {
                return 0;
            }
            else
            {
                switch(intervalSize)
                {
                    case 0: return DIRECTION_0;
                    case 1: return DIRECTION_1;
                    case 2: return DIRECTION_2;
                    case 3: return DIRECTION_3;
                    default: return DIRECTION_4;
                }
            }
        }
        else
        {
            if(direction > 0)
            {
                return 0;
            }
            else
            {
                switch(intervalSize)
                {
                    case 5: return DIRECTION_5;
                    case 6: return DIRECTION_6;
                    case 7: return DIRECTION_7;
                    case 8: return DIRECTION_8;
                    case 9: return DIRECTION_9;
                    default: return DIRECTION_10;
                }
            }
        }
    }
    
    public static double mobility(int pitch, int prevPitch)
    {
        if(pitch == prevPitch)
            return REPETITION;
        else
            return 1;
    }
    
    /**
     * Returns true if the note specified by midiValue is the root of the current chord, false otherwise
     * @param midiValue - midiValue of pitch
     * @param currentChord - current chord
     * @return 
     */
    public static boolean isRoot(int midiValue, Chord currentChord)
    {
        ChordForm curChordForm = currentChord.getChordForm();
        String root = currentChord.getRoot();
        int bass = findBass(root);
        return midiValue % 12 == bass % 12;
    }
    
    /**
     * Returns true if the note specified by midiValue is the in the current chord, false otherwise
     * @param midiValue - midiValue of pitch
     * @param currentChord - current chord
     * @return 
     */
    private static boolean isChordTone(int midiValue, Chord currentChord)
    {
        ChordForm curChordForm = currentChord.getChordForm();
        String root = currentChord.getRoot();

        ArrayList<Integer> chordMIDIs = // the midi values for the notes in the chord
                chordToSuggestions(curChordForm.getSpellMIDIarray(root), midiValue);

        return (chordMIDIs.contains(midiValue));
    }
    
    /**
     * Returns true if the note specified by midiValue is in the scale associated with the current chord, 
     * false otherwise
     * @param midiValue - midiValue of pitch
     * @param currentChord - current chord
     * @return 
     */
    private static boolean isScaleTone(int midiValue, Chord currentChord)
    {
        ChordForm curChordForm = currentChord.getChordForm();
        String root = currentChord.getRoot();

        ArrayList<Integer> chordMIDIs = // the midi values for the notes in the chord
                chordToSuggestions(curChordForm.getScaleMIDIarray(root), midiValue);

        return (chordMIDIs.contains(midiValue));
    }
    
    /**
     * Returns true if the note specified by midiValue is a color tone of the current chord, false otherwise 
     * false otherwise
     * @param midiValue - midiValue of pitch
     * @param currentChord - current chord
     * @return 
     */
    private static boolean isColorTone(int midiValue, Chord currentChord)
    {
        ChordForm curChordForm = currentChord.getChordForm();
        String root = currentChord.getRoot();

        ArrayList<Integer> colorMIDIs = // the midi values for the color notes
                chordToSuggestions(curChordForm.getColorMIDIarray(root), midiValue);

        return (colorMIDIs.contains(midiValue));
    }   
    
    private static ArrayList<Integer> chordToSuggestions(ArrayList<Integer> MIDIarray, int reference)
    {
        ArrayList<Integer> newMIDIs = new ArrayList();
        int minNote = reference - (int) Math.floor(suggestionNum / 2.0);
        int maxNote = reference + (int) Math.ceil(suggestionNum / 2.0);

        if (suggestionNum >= suggestionNumMax) 
        {
            maxNote = C_EIGHTH;
        }


        for (int i = 0; i < MIDIarray.size(); i++)
        {
            int note = MIDIarray.get(i);

            while (note >= minNote)
            {
                note -= P_OCTAVE;
            }

            note += P_OCTAVE;
            while (note <= maxNote && note <= C_EIGHTH && note >= A)
            {
                newMIDIs.add(note);
                note += P_OCTAVE;
            }
        }

        return newMIDIs;
    }
    
    private static int findBass(String bass)
    {
        int midiValue = NoteSymbol.makeNoteSymbol(bass).getMIDI();
        int lowRange = LOW_BASS;
        int highRange = lowRange + P_OCTAVE - 1;

        while (midiValue > highRange)
        {
            midiValue -= P_OCTAVE;
        }

        return midiValue;
    }  
}
