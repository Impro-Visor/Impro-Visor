/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author David Halpern 2012
 */
public class Tension 
{
    private static int[] WEIGHTS = {0, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5, -1, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5};
    private static int[] WEIGHTS_4 = {0, -2, -1, -2};
    private static double[] C1SAME_C2SAME = {0.00001, 0};
    private static double[] C1NOT_C2SAME = {1, 0};
    private static double[] C1SAME_C2NOT = {0.00001, 1};
    private static double[] C1UP_C2DOWN = {0.83, 0.17};
    private static double[] C1DOWN_C2UP = {0.71, 0.29};
    private static double[] C1UP_C2UP = {0.33, 0.67};
    private static double[] C1DOWN_C2DOWN = {0.67, 0.33};
    private static int SLOTS_PER_MEASURE = 32;
    private static int SLOTS_PER_MEASURE2 = 480;
    private static int SECONDS_PER_MINUTE = 60;
    private static double TAU = 0.5;
    private static double ACCENT_INDEX = 2;
    private static int WHOLE_NOTE = 480;
    private static int WHOLE_NOTE_VALUE = 0;
    private static int HALF_NOTE = 240;
    private static int HALF_NOTE_VALUE = -1;
    private static int QUARTER_NOTE = 120;
    private static int QUARTER_NOTE_VALUE = -2;
    private static int EIGHTH_NOTE = 60;
    private static int EIGHTH_NOTE_VALUE = -3;
    private static int EIGHTH_NOTE_TRIPLET = 40;
    private static int EIGHTH_NOTE_TRIPLET_VALUE = -4;
    private static int SIXTEENTH_NOTE = 30;
    private static int SIXTEENTH_NOTE_VALUE = -4;
    private static int SIXTEENTH_NOTE_TRIPLET = 20;
    private static int SIXTEENTH_NOTE_TRIPLET_VALUE = -5;
    private static int THIRTY_SECOND_NOTE = 15;
    private static int THIRTY_SECOND_NOTE_VALUE = -5;
    private static int THIRTY_SECOND_NOTE_TRIPLET = 10;
    private static int THIRTY_SECOND_NOTE_TRIPLET_VALUE = -6;
    private static int OFFSET = 6;
    
    public Tension()
    {
    }
    
    /**
     * Calculates the percentage of angular intervals in notes
     * @param notes - ArrayList of notes
     * @return 
     */
    public static double getAngularity(int[] notes)
    {
        double angularIntervals = 0;
        for(int i = 1; i < notes.length; i ++)
        {
            if(Math.abs(notes[i] - notes[i-1]) > 3) 
            {
                angularIntervals ++;
            }
        }
        return angularIntervals / (notes.length - 1);
    }
    
    /**
     * Returns average pitch height
     * @param notes
     * @return 
     */
    public static int getAveragePitchHeight(int[] notes)
    {
        int averageNote = 0;
        for(int i: notes)
        {
            averageNote += i;
        }
        return averageNote/notes.length;
    }
    
    /**
     * Returns meter accent based on Eerola 2003 (The Dynamics of Musical Expectancy: Cross-Cultural and 
     * Statistical Approaches to Melodic Expectations. Doctoral dissertation. Jyvskyla Studies in Arts) 
     * and Jones 1987 Dynamic pattern structure in music: Recent theory and research.
     * Perception and Psychophysics, 41, 621-634.
     * @param onsets
     * @param notes
     * @param tempo
     * @return 
     */
    public double getMeterAccent(int[] onsets, int[] notes, int tempo)
    {
        int[] mh = getMetricalHierarchyList(onsets);
        double[] ma = getMelodicAccent(notes);
        double[] du = getDurationalAccent(onsets, tempo);
        int sum = 0;
        for(int i = 0; i < mh.length; i ++)
        {
            sum += (mh[i] * ma[i] * du[i]);
        }
        return sum/mh.length;
    }
    
    /**
     * Returns a list of metrical hierarchies for each note
     * @param onsets
     * @return 
     */
    public static int[] getMetricalHierarchyList(int[] onsets)
    {
        int[] metricalHierarchyList = new int[onsets.length];
        int listIndex = 0;
        for(int i = 0; i < onsets.length; i ++)
        {
            if(onsets[i] == 1)
            {
                metricalHierarchyList[listIndex] = getMetricalHierarchy(i) + OFFSET;
                listIndex ++;
            }
        }
        return metricalHierarchyList;
    }
    
    /**
     * Given an index within a rhythm array, returns the metrical hierarchy value of that metrical position
     * (Based on Lerdahl and Jackendoff 1983)
     * @param index
     * @return 
     */
    public static int getMetricalHierarchy(int index)
    {
        if(index % WHOLE_NOTE == 0)
        {
            return WHOLE_NOTE_VALUE;
        }
        else if(index % HALF_NOTE == 0)
        {
            return HALF_NOTE_VALUE;
        }
        else if(index % QUARTER_NOTE == 0)
        {
            return QUARTER_NOTE_VALUE;
        }
        else if(index % EIGHTH_NOTE == 0)
        {
            return EIGHTH_NOTE_VALUE;
        }
        else if(index % EIGHTH_NOTE_TRIPLET == 0)
        {
            return EIGHTH_NOTE_TRIPLET_VALUE;
        }
        else if(index % SIXTEENTH_NOTE == 0)
        {
            return SIXTEENTH_NOTE_VALUE;
        }
        else if(index % SIXTEENTH_NOTE_TRIPLET == 0)
        {
            return SIXTEENTH_NOTE_TRIPLET_VALUE;
        }
        else if(index % THIRTY_SECOND_NOTE == 0)
        {
            return THIRTY_SECOND_NOTE_VALUE;
        }
        else if(index % THIRTY_SECOND_NOTE_TRIPLET == 0)
        {
            return THIRTY_SECOND_NOTE_TRIPLET_VALUE;
        }
        else
        {
            return THIRTY_SECOND_NOTE_TRIPLET_VALUE;
        }
    }
    
    /**
     * Computes the melodic accent of a group of notes according to Thomassen(1982)
     * @param notes
     * @return 
     */
    public static double[] getMelodicAccent(int[] notes)
    {
        double[][] mel2 = new double[notes.length - 3][2];
        for(int i = 0; i < notes.length - 3; i++)
        {            
            int motion1 = notes[i+1] - notes[i];
            int motion2 = notes[i+2] - notes[i+1];
            if(motion1 == 0 && motion2 == 0)
            {
                mel2[i] = C1SAME_C2SAME;
            }
            else if(motion1 != 0 && motion2 == 0)
            {
                mel2[i] = C1NOT_C2SAME;
            }
            else if(motion1 == 0 && motion2 != 0)
            {
                mel2[i] = C1SAME_C2NOT;
            }
            else if(motion1 > 0 && motion2 < 0)
            {
                mel2[i] = C1UP_C2DOWN;
            }
            else if(motion1 < 0 && motion2 > 0)
            {
                mel2[i] = C1DOWN_C2UP;
            }
            else if(motion1 < 0 && motion2 < 0)
            {
                mel2[i] = C1UP_C2UP;
            }
            else if(motion1 < 0 && motion2 < 0)
            {
                mel2[i] = C1DOWN_C2DOWN;
            }
        }
        double[] p2 = new double[notes.length];
        p2[0] = 1;
        p2[1] = mel2[0][0];
        for(int k = 2; k < notes.length - 2; k ++)
        {
            double first = mel2[k-2][2];
            double second = mel2[k-1][1];
            if(first == 0)
            {
                p2[k] = second;
            }
            else if(second == 0)
            {
                p2[k] = first;
            }
            else
            {
                p2[k] = first * second;
            }
        }
        p2[notes.length - 1] = mel2[mel2.length - 1][1];
        return p2;
    }
    
    /**
     * Calculates durational accent according to Parncutt(1994)
     * @param onsets
     * @param tempo
     * @return 
     */
    public static double[] getDurationalAccent(int[] onsets, int tempo)
    {
        double[] durations = getNoteDurationsInSecs(onsets, tempo);
        double[] dAccents = new double[durations.length];
        for(int i = 0; i < durations.length; i ++)
        {
            double exp = Math.exp((durations[i] * -1)/TAU);
            dAccents[i] = Math.pow((1 - exp), ACCENT_INDEX);
        }
        return dAccents;
    }
    
    /**
     * Calculates the duration of each note in seconds given an array of slots
     * @param onsets
     * @param tempo
     * @return 
     */
    private static double[] getNoteDurationsInSecs(int[] onsets, int tempo)
    {
        double bps = tempo/SECONDS_PER_MINUTE;
        int noteLength = 0;
        boolean note = false;
        double[] durations = new double[onsets.length];
        int dIndex = 0;
        for(int i = 0; i < onsets.length; i ++)
        {
            if(onsets[i] == 1)
            {
                if(note == true)
                {
                    double beats = noteLength/SLOTS_PER_MEASURE2/4;
                    durations[dIndex] = beats/bps;
                    dIndex ++;
                }
                note = true;
                noteLength = 1;
            }
            else if(note == true)
            {
                noteLength ++;
            }
        }
        return durations;
    }
    
    /**
     * Returns the syncopation value for every set of bars of size windowSize
     * @param onsets
     * @param measures
     * @param windowSize
     * @return 
     */        
    public static int[] getWindowedSyncopation(int[] onsets, int measures, int windowSize)
    {
        int slotsPerWindow = SLOTS_PER_MEASURE * windowSize;
        int outputIndex = 0;
        int[] output = new int[measures/windowSize];
        for(int onsetIndex = 0; onsetIndex < onsets.length - 1; onsetIndex += slotsPerWindow)
        {
            int[] syncoArray = new int[slotsPerWindow];
            System.arraycopy(onsets, onsetIndex, syncoArray, 0, slotsPerWindow);
            output[outputIndex] = getSyncopation(syncoArray, windowSize);
            outputIndex ++;
        }
        return output;
    }
    
    /**
     * Returns the syncopation value of a melody according to the Longuet-Higgins and Lee (1984) algorithm
     * @param onsets - array of containing a slot for each 32nd note with a 1 if there is an onset and a 0 otherwise
     * @param measures - number of measures in onset array
     * @return - syncopation value
     */
    public static int getSyncopation(int[] onsets, int measures)
    {
        int[] w = getWeightArray(measures, WEIGHTS);
        int synco = 0;
        try
          {
        for(int i = 0; i < onsets.length; i ++)
        {
            if(onsets[i] == 0)
            {
                int nPos = i;
                while(onsets[nPos] == 0 && nPos > 0)
                {
                    nPos = nPos - 1;
                }
                if(!(onsets[nPos] == 0))
                {
                    int syncoValue = w[i] - w[nPos];
                    if(syncoValue > 0)
                    {
                        synco = synco + syncoValue;
                    }
                }
            }
        }
          }
        catch(Exception e)
          {
            System.out.println("Exception caught in getSyncopation: " + e);
          }
        return synco;
    }
    
    /**
     * Returns syncopation using a different (better) method than before. Can actually handle triplets
     * @param onsets
     * @return 
     */
    public static int getSyncopation2(int[] onsets)
    {
        int synco = 0;
        for(int i = 0; i < onsets.length; i ++)
        {
            if(onsets[i] == 0)
            {
                int nPos = i;
                while(onsets[nPos] == 0 && nPos > 0)
                {
                    nPos = nPos - 1;
                }
                if(!(onsets[nPos] == 0))
                {
                    int syncoValue = getMetricalHierarchy(i) - getMetricalHierarchy(nPos);
                    if(syncoValue > 0)
                    {
                        synco = synco + syncoValue;
                    }
                }
            }
        }
        return synco;
    }
    
    /**
     * Returns windowed syncopation using same method as getSyncopation2
     * @param onsets
     * @param measures
     * @param windowSize
     * @return 
     */
     public static int[] getWindowedSyncopation2(int[] onsets, int measures, int windowSize)
    {
        int slotsPerWindow = SLOTS_PER_MEASURE2 * windowSize;
        int outputIndex = 0;
        int[] output = new int[measures/windowSize];
        for(int onsetIndex = 0; onsetIndex < onsets.length - 1; onsetIndex += slotsPerWindow)
        {
            int[] syncoArray = new int[slotsPerWindow];
            System.arraycopy(onsets, onsetIndex, syncoArray, 0, slotsPerWindow);
            output[outputIndex] = getSyncopation2(syncoArray);
            outputIndex ++;
        }
        return output;
    }
    
    /**
     * Returns a weight array for a specific number of measures
     * @param measures
     * @return 
     */
    private static int[] getWeightArray(int measures, int[] weights)
    {
        int totalLength = weights.length * measures;
        int[] weightArray = Arrays.copyOf(weights, totalLength);
        int offset = weights.length;
        for(int i = measures; i > 1; i --)
        {
            System.arraycopy(weights, 0, weightArray, offset, weights.length);
            offset += weights.length;
        }
        return weightArray;
    }
}
