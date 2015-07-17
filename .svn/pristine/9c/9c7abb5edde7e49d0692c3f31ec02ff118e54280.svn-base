/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

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
