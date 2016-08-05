/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.encoding;
import java.util.Map.Entry;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Class ChordEncoder describes encoding and decoding procedures from chord names to bit vectors and vice-versa
 * @author Nicholas Weintraut
 */
public class ChordEncoder {
    
    public ChordEncoder(){}
    
    public AVector encode(String root, String type, String bass)
    {
        AVector chordData = CHORD_TYPES.getValue(type);
        return encode(root, chordData, bass);
    }
    public AVector encode(String root, AVector chordData, String bass)
    {
        //System.out.println();
        //for(int i = 0; i < chordData.length(); i++)
        //    System.out.print(chordData.getDouble(i) + " ");
        //System.out.println();
        //System.out.println(CHORD_TYPES.getKey(chordData));
        if(chordData == null)
            return null;
        else
        {
            AVector transposedData = transposeChordData(chordData.clone(), (int) DISTANCES_FROM_C.getValue(root).intValue());
            transposedData.set((int) DISTANCES_FROM_C.getValue(bass).intValue(), 1);
            return transposedData;
        }
    }
    
    public String decode(AVector chordData) {
        
        String type = null;
        boolean foundC = false;
        double transposition = 0;
        while(!foundC && transposition < 12) {
            //for(int i = 0; i < chordData.length(); i++)
            //        System.out.print(chordData.getDouble(i) + " ");
            //System.out.println("<- transposition " +  transposition);
            for(Entry<String, AVector> entry : CHORD_TYPES.entrySet())
            {
                if(chordData.equals(entry.getValue()))
                    type = entry.getKey();
            }
            //type = CHORD_TYPES.getKey(chordData);
            if(type != null) {
                foundC = true;
            }
            else {
                chordData = transposeChordData(chordData, -1);
                transposition++;
            }
        }
        if(transposition == 12)
        {
            System.out.println("Chord not found! (Might be a slash chord, which are not implemented for decode.) Substituting NC");
            return "NC";
        }
        if(("NC").equals(type))
            return "NC";
        else
            return DISTANCES_FROM_C.getKey(transposition) + type;
    }
    
    public AVector transposeChordData(AVector chordData, int distance)
    {
        //we check if distance is zero and simply return for simplicity, but also because Nd4j has a bug where passing (length, length) gives an AVector of size 1
        //also Nd4j concat, when given an AVector of size 1 and another AVector, returns a two dimensional array with only two ELEMENTS...no matter the size of the second array
        //yay nd4j $wag
        if(distance == 0)
            return chordData;
        else
        {
            AVector part1;
            AVector part2;
            if(distance > 0)
            {
                part1 = chordData.subVector(chordData.length() - (distance % chordData.length()), distance);
                part2 = chordData.subVector(0, chordData.length() - distance);
            }
            else
            {
                part1 = chordData.subVector((-1 * distance) % chordData.length(), chordData.length() + distance);
                part2 = chordData.subVector(0, (-1 * distance));          
            }
            //System.out.println(part1);
            //System.out.println(part2);
            AVector concatenated = part1.join(part2).dense();
            /*System.out.println();
            for(int i = 0; i < concatenated.length(); i++)
                System.out.print(concatenated.getDouble(i) + " ");
            System.out.println();*/
            return concatenated;
        }
    }
    
    public final static AVector NO_CHORD         = Vector.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    public final static AVector C_MAJOR          = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector C_MAJOR_7        = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1);
    public final static AVector C_MINOR_7        = Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_DOM_7          = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_MINOR_9        = Vector.of(1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector C_13             = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0);
    public final static AVector C_MINOR_7_FLAT_5 = Vector.of(1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector C_DOM_7_SHARP_9  = Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_DOM_7_FLAT_9   = Vector.of(1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_DIM_7          = Vector.of(1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0);
    public final static AVector C_9              = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_MAJOR_9        = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1);
    public final static AVector C_DOM_7_SHARP_11 = Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C_MAJOR_7_SHARP_11= Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1);
    public final static AVector C_6              = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0);
    public final static AVector C_7_ALT          = Vector.of(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector NC              = Vector.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    public final static AVector C		= Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector CM		= Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector Cm_sharp_5	= Vector.of(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector Cm_plus_        = Vector.of(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector Cm		= Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector Cm11_sharp_5	= Vector.of(1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0);
    public final static AVector Cm11		= Vector.of(1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector Cm11b5          = Vector.of(1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0);
    public final static AVector Cm13            = Vector.of(1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0);
    public final static AVector Cm6             = Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0);
    public final static AVector Cm69            = Vector.of(1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0);
    public final static AVector Cm7_sharp_5	= Vector.of(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector Cm7             = Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector Cm7b5           = Vector.of(1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector Ch7              = Vector.of(1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector Cm9_sharp_5	= Vector.of(1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector Cm9             = Vector.of(1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0);
    public final static AVector Cm9b5           = Vector.of(1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector CmM7            = Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1);
    public final static AVector CmM7b6          = Vector.of(1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1);
    public final static AVector CmM9            = Vector.of(1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1);
    public final static AVector Cmadd9          = Vector.of(1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector Cmb6            = Vector.of(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector Cmb6M7          = Vector.of(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1);
    public final static AVector Cmb6b9          = Vector.of(1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector CM_sharp_5	= Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector C_plus_         = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector Caug            = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector C_plus_7        = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector CM_sharp_5add9	= Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector CM7_sharp_5	= Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1);
    public final static AVector CM7_plus_	= Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1);
    public final static AVector CM9_sharp_5	= Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1);
    public final static AVector C_plus_add9	= Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0);
    public final static AVector C7              = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C7_sharp_5	= Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7_plus_        = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector Caug7           = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7aug           = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7_sharp_5_sharp_9	= Vector.of(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7alt           = Vector.of(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7b13           = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0);
    public final static AVector C7b5_sharp_9	= Vector.of(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7b5            = Vector.of(1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector C7b5b13         = Vector.of(1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7b5b9          = Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0);
    public final static AVector C7b5b9b13	= Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7b6            = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0);
    public final static AVector C7b9_sharp_11	= Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0);
    public final static AVector C7b9_sharp_11b13	= Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7b9            = Vector.of(1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C7b9b13_sharp_11	= Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7b9b13         = Vector.of(1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0);
    public final static AVector C7no5           = Vector.of(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
    public final static AVector C7_sharp_11	= Vector.of(1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0);
    public final static AVector C7_sharp_11b13	= Vector.of(1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7_sharp_5b9_sharp_11	= Vector.of(1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0);
    public final static AVector C7_sharp_5b9            = Vector.of(1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C7_sharp_9_sharp_11	= Vector.of(1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0);
    public final static AVector C7_sharp_9_sharp_11b13	= Vector.of(1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C7_sharp_9	= Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C7_sharp_9b13	= Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0);
    public final static AVector C9              = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0);
    public final static AVector C9_sharp_5	= Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C9_plus_        = Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0);
    public final static AVector C9_sharp_11	= Vector.of(1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0);
    public final static AVector C9_sharp_11b13	= Vector.of(1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C9_sharp_5_sharp_11	= Vector.of(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0);
    public final static AVector C9b13           = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0);
    public final static AVector C9b5            = Vector.of(1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0);
    public final static AVector C9b5b13         = Vector.of(1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0);
    public final static AVector C9no5           = Vector.of(1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0);
    public final static AVector C13_sharp_11	= Vector.of(1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0);
    public final static AVector C13_sharp_9_sharp_11	= Vector.of(1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0);
    public final static AVector C13_sharp_9	= Vector.of(1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0);
    public final static AVector C13             = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0);
    public final static AVector C13b5           = Vector.of(1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0);
    public final static AVector C13b9_sharp_11	= Vector.of(1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0);
    public final static AVector C13b9           = Vector.of(1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0);
    public final static AVector CMsus2          = Vector.of(1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector CMsus4          = Vector.of(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0);
    public final static AVector Csus2           = Vector.of(1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0);
    public final static AVector Csus4           = Vector.of(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0);
    public final static AVector Csusb9          = Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0);
    public final static AVector C7b9b13sus4	= Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0);
    public final static AVector C7b9sus         = Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0);
    public final static AVector C7b9sus4        = Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C7sus           = Vector.of(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C7sus4          = Vector.of(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C7sus4b9        = Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C7sus4b9b13	= Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0);
    public final static AVector C7susb9         = Vector.of(1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C9sus4          = Vector.of(1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C9sus           = Vector.of(1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C11             = Vector.of(1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0);
    public final static AVector C13sus          = Vector.of(1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0);
    public final static AVector C13sus4         = Vector.of(1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0);
    public final static AVector CBlues          = Vector.of(1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0);
    public final static AVector CBass           = Vector.of(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    public final static AVector Co              = Vector.of(1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0);
    public final static AVector CM6             = Vector.of(1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0);
    public final static AVector CM69            = Vector.of(1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0);
    
    public final static BidirectionalHashMap<String, AVector> CHORD_TYPES = new BidirectionalHashMap<>();
    static {
        CHORD_TYPES.put("NC", NO_CHORD);
        CHORD_TYPES.put("", C_MAJOR);
        CHORD_TYPES.put("M", CM);
        CHORD_TYPES.put("M7", C_MAJOR_7);
        CHORD_TYPES.put("m7", C_MINOR_7);
        CHORD_TYPES.put("7", C_DOM_7);
        CHORD_TYPES.put("m9", C_MINOR_9);
        CHORD_TYPES.put("13", C_13);
        CHORD_TYPES.put("M6", CM6);
        CHORD_TYPES.put("M69", CM69);
        CHORD_TYPES.put("m7b5", C_MINOR_7_FLAT_5);
        CHORD_TYPES.put("7#9", C_DOM_7_SHARP_9);
        CHORD_TYPES.put("7b9", C_DOM_7_FLAT_9);
        CHORD_TYPES.put("o", Co);
        CHORD_TYPES.put("o7", C_DIM_7);
        CHORD_TYPES.put("9", C_9);
        CHORD_TYPES.put("M9", C_MAJOR_9);
        CHORD_TYPES.put("7#11", C_DOM_7_SHARP_11);
        CHORD_TYPES.put("M7#11", C_MAJOR_7_SHARP_11);
        CHORD_TYPES.put("6", C_6);
        CHORD_TYPES.put("7alt", C_7_ALT);
        CHORD_TYPES.put("", C);
        CHORD_TYPES.put("m#5", Cm_sharp_5);
        CHORD_TYPES.put("m+", Cm_plus_);
        CHORD_TYPES.put("m", Cm);
        CHORD_TYPES.put("m11#5", Cm11_sharp_5);
        CHORD_TYPES.put("m11", Cm11);
        CHORD_TYPES.put("m11b5", Cm11b5);
        CHORD_TYPES.put("m13", Cm13);
        CHORD_TYPES.put("m6", Cm6);
        CHORD_TYPES.put("m69", Cm69);
        CHORD_TYPES.put("69", Cm69); //hey not sure if this is correct
        CHORD_TYPES.put("m7#5", Cm7_sharp_5);
        CHORD_TYPES.put("m7", Cm7);
        CHORD_TYPES.put("m7b5", Cm7b5);
        CHORD_TYPES.put("h7", Ch7);
        CHORD_TYPES.put("m9#5", Cm9_sharp_5);
        CHORD_TYPES.put("m9", Cm9);
        CHORD_TYPES.put("m9b5", Cm9b5);
        CHORD_TYPES.put("mM7", CmM7);
        CHORD_TYPES.put("mM7b6", CmM7b6);
        CHORD_TYPES.put("mM9", CmM9);
        CHORD_TYPES.put("madd9", Cmadd9);
        CHORD_TYPES.put("mb6", Cmb6);
        CHORD_TYPES.put("mb6M7", Cmb6M7);
        CHORD_TYPES.put("mb6b9", Cmb6b9);
        CHORD_TYPES.put("M#5", CM_sharp_5);
        CHORD_TYPES.put("+", C_plus_);
        CHORD_TYPES.put("aug", Caug);
        CHORD_TYPES.put("+7", C_plus_7);
        CHORD_TYPES.put("M#5add9", CM_sharp_5add9);
        CHORD_TYPES.put("M7#5", CM7_sharp_5);
        CHORD_TYPES.put("M7+", CM7_plus_);
        CHORD_TYPES.put("M9#5", CM9_sharp_5);
        CHORD_TYPES.put("+add9", C_plus_add9);
        CHORD_TYPES.put("7", C7);
        CHORD_TYPES.put("7#5", C7_sharp_5);
        CHORD_TYPES.put("7+", C7_plus_);
        CHORD_TYPES.put("aug7", Caug7);
        CHORD_TYPES.put("7aug", C7aug);
        CHORD_TYPES.put("7#5#9", C7_sharp_5_sharp_9);
        CHORD_TYPES.put("7alt", C7alt);
        CHORD_TYPES.put("7b13", C7b13);
        CHORD_TYPES.put("7b5#9", C7b5_sharp_9);
        CHORD_TYPES.put("7b5", C7b5);
        CHORD_TYPES.put("7b5b13", C7b5b13);
        CHORD_TYPES.put("7b5b9", C7b5b9);
        CHORD_TYPES.put("7b5b9b13", C7b5b9b13);
        CHORD_TYPES.put("7b6", C7b6);
        CHORD_TYPES.put("7b9#11", C7b9_sharp_11);
        CHORD_TYPES.put("7b9#11b13", C7b9_sharp_11b13);
        CHORD_TYPES.put("7b9", C7b9);
        CHORD_TYPES.put("7b9b13#11", C7b9b13_sharp_11);
        CHORD_TYPES.put("7b9b13", C7b9b13);
        CHORD_TYPES.put("7no5", C7no5);
        CHORD_TYPES.put("7#11", C7_sharp_11);
        CHORD_TYPES.put("7#11b13", C7_sharp_11b13);
        CHORD_TYPES.put("7#5b9#11", C7_sharp_5b9_sharp_11);
        CHORD_TYPES.put("7#5b9", C7_sharp_5b9);
        CHORD_TYPES.put("7#9#11", C7_sharp_9_sharp_11);
        CHORD_TYPES.put("7#9#11b13", C7_sharp_9_sharp_11b13);
        CHORD_TYPES.put("7#9", C7_sharp_9);
        CHORD_TYPES.put("7#9b13", C7_sharp_9b13);
        CHORD_TYPES.put("9", C9);
        CHORD_TYPES.put("9#5", C9_sharp_5);
        CHORD_TYPES.put("9+", C9_plus_);
        CHORD_TYPES.put("9#11", C9_sharp_11);
        CHORD_TYPES.put("9#11b13", C9_sharp_11b13);
        CHORD_TYPES.put("9#5#11", C9_sharp_5_sharp_11);
        CHORD_TYPES.put("9b13", C9b13);
        CHORD_TYPES.put("9b5", C9b5);
        CHORD_TYPES.put("9b5b13", C9b5b13);
        CHORD_TYPES.put("9no5", C9no5);
        CHORD_TYPES.put("13#11", C13_sharp_11);
        CHORD_TYPES.put("13#9#11", C13_sharp_9_sharp_11);
        CHORD_TYPES.put("13#9", C13_sharp_9);
        CHORD_TYPES.put("13", C13);
        CHORD_TYPES.put("13b5", C13b5);
        CHORD_TYPES.put("13b9#11", C13b9_sharp_11);
        CHORD_TYPES.put("13b9", C13b9);
        CHORD_TYPES.put("Msus2", CMsus2);
        CHORD_TYPES.put("Msus4", CMsus4);
        CHORD_TYPES.put("sus2", Csus2);
        CHORD_TYPES.put("sus4", Csus4);
        CHORD_TYPES.put("susb9", Csusb9);
        CHORD_TYPES.put("7b9b13sus4", C7b9b13sus4);
        CHORD_TYPES.put("7b9sus", C7b9sus);
        CHORD_TYPES.put("7b9sus4", C7b9sus4);
        CHORD_TYPES.put("7sus", C7sus);
        CHORD_TYPES.put("7sus4", C7sus4);
        CHORD_TYPES.put("7sus4b9", C7sus4b9);
        CHORD_TYPES.put("7sus4b9b13", C7sus4b9b13);
        CHORD_TYPES.put("7susb9", C7susb9);
        CHORD_TYPES.put("9sus4", C9sus4);
        CHORD_TYPES.put("9sus", C9sus);
        CHORD_TYPES.put("11", C11);
        CHORD_TYPES.put("13sus", C13sus);
        CHORD_TYPES.put("13sus4", C13sus4);
        CHORD_TYPES.put("Blues", CBlues);
        CHORD_TYPES.put("Bass", CBass);
    }
    
    public final static BidirectionalHashMap<String, Double> DISTANCES_FROM_C = new BidirectionalHashMap<>();
    static {
        DISTANCES_FROM_C.put("C", 0.0);
        DISTANCES_FROM_C.put("C#", 1.0);
        DISTANCES_FROM_C.put("Db", 1.0);
        DISTANCES_FROM_C.put("D", 2.0);
        DISTANCES_FROM_C.put("D#", 3.0);
        DISTANCES_FROM_C.put("Eb", 3.0);
        DISTANCES_FROM_C.put("E", 4.0);
        DISTANCES_FROM_C.put("F", 5.0);
        DISTANCES_FROM_C.put("F#", 6.0);
        DISTANCES_FROM_C.put("Gb", 6.0);
        DISTANCES_FROM_C.put("G", 7.0);
        DISTANCES_FROM_C.put("G#", 8.0);
        DISTANCES_FROM_C.put("Ab", 8.0);
        DISTANCES_FROM_C.put("A", 9.0);
        DISTANCES_FROM_C.put("A#", 10.0);
        DISTANCES_FROM_C.put("Bb", 10.0);
        DISTANCES_FROM_C.put("B", 11.0);
        // no chord ("NC"): no transposition
        DISTANCES_FROM_C.putKeyToValueOnly("NC", 0.0);
    }
}
