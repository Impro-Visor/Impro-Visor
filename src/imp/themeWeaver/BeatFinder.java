/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2015-2016 Robert Keller and Harvey Mudd College
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

package imp.themeWeaver;

import imp.Constants;

/**
 *
 * @author muddCS15
 */
public class BeatFinder implements Constants{
    
    public static final String EVERY_BEAT = "Every Beat";
    public static final String MEASURE_LENGTH = "Measure Length";
    public static final String STRONG_BEATS = "Strong Beats";
    
    private final int [] metre;
    
    public BeatFinder(int [] metre){
        this.metre = metre;
    }
    
    /**
     * beatLength
     * @return length in slots of a single beat
     */
    private int beatLength(){
        return WHOLE/metre[1];
    }
    
    /**
     * beatsPerMeasure
     * @return number of beats in a measure
     */
    private int beatsPerMeasure(){
        return metre[0];
    }
    
    /**
     * measureLength
     * @return length of a measure in slots
     */
    private int measureLength(){
        return beatLength()*beatsPerMeasure();
    }
    
    /**
     * strongBeatsPerMeasure
     * Determines number of strong beats per measure based on the top
     * number in the time signature
     * Could be improved. Right now, if there is some question as to whether
     * something should be felt in two or in three, like in 6/8 or 12/8, 
     * it default to a two feel
     * @return number of strong beats per measure
     */
    private int strongBeatsPerMeasure(){
        int beatsPerMeasure = beatsPerMeasure();
        int strongBeats;
   
        if(beatsPerMeasure <= 3){               //  2/4, 3/4, ...
            strongBeats = 1;
        }else if(beatsPerMeasure % 2 == 0){     //  4/4, 6/8, 12/8, ...
            strongBeats = 2;
        }else if(beatsPerMeasure % 3 == 0){     //  9/8, ...
            strongBeats = 3;
        }else{                                  //  7/8, ...
            strongBeats = 1;
        }
        
        return strongBeats;
    }
    
    /**
     * timeBetweenStrongBeats
     * @return length in slots between one strong beat and the next
     */
    private int timeBetweenStrongBeats(){
        return measureLength() / strongBeatsPerMeasure();
    }
    
    public int getResolution(String flattenValue)
    {
        if(flattenValue.equals("Whole Note"))
            return WHOLE;
        else if(flattenValue.equals("Half Note"))
            return HALF;
        else if(flattenValue.equals("Quarter Note"))
            return QUARTER;
        else if(flattenValue.equals("Eighth Note"))
            return EIGHTH;
        else if(flattenValue.equals("Sixteenth Note"))
            return SIXTEENTH;
        else if(flattenValue.equals(EVERY_BEAT))
            return beatLength();
        else if(flattenValue.equals(MEASURE_LENGTH))
            return measureLength();
        else if(flattenValue.equals(STRONG_BEATS))
            return timeBetweenStrongBeats();
        else
            return WHOLE;
    }
    
}
