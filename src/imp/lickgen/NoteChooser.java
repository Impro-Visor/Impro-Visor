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

package imp.lickgen;

import polya.*;
import java.util.Random;

/**
 *
 * @author Jon Gillick
 */
public class NoteChooser {
    public static final int NOTE = 1000;
    public static final int CHORD = 1001;
    public static final int SCALE = 1002;
    public static final int COLOR = 1003;
    public static final int APPROACH = 1004;
    public static final int RANDOM = 1005;
    public static final int BASS = 1006;
    public static final int GOAL = 1007;// Parameter strings
    
    public int[] typeMap = {
        CHORD, COLOR, RANDOM, SCALE
    };
    
    private Polylist probabilities;
    private boolean doNotSwitchOctave;
    
    public NoteChooser(boolean noOctaveSwitch) {        
        
        //if this is true, never transpose pitch an octave if out of bounds
        doNotSwitchOctave = noOctaveSwitch;
        
        
        /* The first number in each line represents which type of note we are 
         * looking for - 0 for chord, 1 for color, 2 for random, 3 for scale
         * The next three numbers represent which notes are present of chord,
         * color, and then random
         * The last 4 numbers are probabilities for which will be chosen:
         * in order - chord, color, random, scale
        */
        String probString = 
                "( (0 1 1 1 100 0 0 0) " +    //looking for chord, have chord, color, random
                  "(0 1 1 0 100 0 0 0) " +    //looking for chord, have chord, color
                  "(0 1 0 1 100 0 0 0) " +    //looking for chord, have chord, random
                  "(0 1 0 0 100 0 0 0) " +    //looking for chord, have chord
                  "(0 0 1 1 0 100 0 0) " +    //looking for chord, have color, random
                  "(0 0 1 0 0 100 0 0) " +    //looking for chord, have color
                  "(0 0 0 1 0 0 100 0) " +    //looking for chord, have random
                  
                  "(1 1 1 1 0 100 0 0) " +    //looking for color, have chord, color, random 
                  "(1 1 1 0 10 90 0 0) " +    //looking for color, have chord, color
                  "(1 1 0 1 85 0 15 0) " +    //looking for color, have chord, random
                  "(1 1 0 0 100 0 0 0) " +    //looking for color, have chord
                  "(1 0 1 1 0 100 0 0) " +    //looking for color, have color, random
                  "(1 0 1 0 0 100 0 0) " +    //looking for color, have color
                  "(1 0 0 1 0 0 100 0) " +    //looking for color, have random
                  
                  "(3 1 1 1 0 0 0 100) " +    //looking for scale, have chord, color, random 
                  "(3 1 1 0 0 0 0 100) " +    //looking for scale, have chord, color
                  "(3 1 0 1 0 0 0 100) " +    //looking for scale, have chord, random
                  "(3 1 0 0 0 0 0 100) " +    //looking for scale, have chord
                  "(3 0 1 1 0 0 0 100) " +    //looking for scale, have color, random
                  "(3 0 1 0 0 0 0 100) " +    //looking for scale, have color
                  "(3 0 0 1 0 0 100 0) " +    //looking for scale, have random
                  
                  
                  "(2 1 1 1 50 25 25 0) " +    //looking for random, have chord, color, random 
                  "(2 1 1 0 80 20 0 0) " +    //looking for random, have chord, color
                  "(2 1 0 1 50 0 50 0) " +     //looking for random, have chord, random
                  "(2 1 0 0 100 0 0 0) " +    //looking for random, have chord
                  "(2 0 1 1 0 50 50 0) " +     //looking for random, have color, random
                  "(2 0 1 0 0 100 0 0) " +    //looking for random, have color
                  "(2 0 0 1 0 0 100 0) )";    //looking for random, have random
        
                   
       probabilities = (Polylist)(Polylist.PolylistFromString(probString)).first();       
                          
    }
    
    /* Called from chooseNote in Lickgen
     * Given an interval, the desired type of note,
     * the types of notes in the interval, and the occurences of each type of note,
     * looks up the probabilities for what type of note to choose in a table
     * and returns a note chosen based on the probabilities
     * attempts is a counter for the number of times we have tried to get all pitches within range
     */ 
    public int getNote(int minPitch, int maxPitch, 
            int low, int high, int type, int[] numTypes, int[] noteTypes, int attempts) {
        
        
        
        if(type == CHORD) type = 0;
        if(type == COLOR) type = 1;
        if(type == RANDOM) type = 2;
        if(type == SCALE) type = 3;
        
        Polylist prob = new Polylist();
        
        //integers representing whether a certain type is present
        int haveChord, haveColor, haveRandom;
        if(numTypes[0] != 0) haveChord = 1;   else haveChord = 0;
        if(numTypes[1] != 0) haveColor = 1;   else haveColor = 0;
        if(numTypes[2] != 0) haveRandom = 1;  else haveRandom = 0;
        
        //special case for chord tones - they are more important than staying
        //in the interval
        //if(type == 0 && haveChord == 0) {
         //   for(int i = low - 4; i <= low + 4; i++) {
         //       
         //   }
         //}
        
        Polylist identifier = Polylist.list(type, haveChord, haveColor, haveRandom);
        
        //look for match
        for( Polylist L = probabilities; L.nonEmpty(); L = L.rest() )
        {
             //get first list in probabilities
             Polylist tempProb = (Polylist) L.first();
             //chop off the probabilities so we just have the identifier
             Polylist tempIdentifier = tempProb.prefix(4);
             if(identifier.equals(tempIdentifier)) {
                 prob = tempProb.coprefix(4);
             }
        }
        
        
        //put the matched probabilities into an array
        int[] probs = setProb(prob);
                
        Random rand = new Random();
        
        //generate note type from probabilities
        int randNum = rand.nextInt(100) + 1;
        int newType = 0;  //-1;
        for (int i = 0; i < probs.length; i++) {
            randNum = randNum - probs[i];
            if (randNum <= 0) {
                newType = i;
                i = probs.length;
            }
        }
        
        //get note pitch
        randNum = rand.nextInt(numTypes[newType]) + 1;
        int pitchdiff = 0;
        for (int i = 0; i < noteTypes.length; i++) {
            if (noteTypes[i] == typeMap[newType] || (newType == 3 && (noteTypes[i] == CHORD || noteTypes[i] == COLOR))) {
                randNum--;
            }
            if (randNum <= 0) {
                pitchdiff = i;
                i = noteTypes.length;
            }
        }        
        int finalPitch = low + pitchdiff;
        
        if (attempts >= LickGen.MELODY_GEN_LIMIT - 1 && doNotSwitchOctave == false) {
            //raise or lower by an octave if outside bounds
            while (finalPitch > maxPitch) {
                finalPitch -= 12;
            }
            while (finalPitch < minPitch) {
                finalPitch += 12;
            }
        }
        return finalPitch;
    }
    
    
    // Sets probabilities of note types into an array from a polylist
    private int[] setProb(Polylist probs) {
       
        int chord, color, random, scale;
        chord = Integer.parseInt((String)probs.first().toString());
        color = Integer.parseInt((String)probs.second().toString());
        random = Integer.parseInt((String)probs.third().toString());
        scale = Integer.parseInt((String)probs.fourth().toString());        
        
        int[] result = new int[4];
        result[0] = chord;
        result[1] = color;
        result[2] = random;
        result[3] = scale;
        return result;
    }
}