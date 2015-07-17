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


package imp.data;

import polya.Polylist;

/*
 * MelodyContour.java
 *
 * Encapsulates information about a melody contour for comparison,
 * query, etc purposes.
 * As of Aug 11, 2006, only integrated into Impro-visor for research purposes,
 * and not appearing in public software.
 *
 * Created on July 14, 2006, 2:02 PM
 *
 * @author Steven Gomez
 */

public class MelodyContour {
    final static int NOTE_RESOLUTION = Note.EIGHTH;
    final boolean USE_RESOLUTION = true;
    
    private MelodyPart part;
    private Polylist noteChain;
    private Polylist intervalChain;
    
    /** Creates a new instance of MelodyContour */
    public MelodyContour(MelodyPart part) {
        this.part = part;
        
        noteChain = makeNoteChain(USE_RESOLUTION);
        intervalChain = makeIntervalChain(noteChain);
        
        //System.out.println("int chain: " + intervalChain);
        //System.out.println("Parsons chain: " + parsons(intervalChain));
    }
    
    /**
     * Builds the polylist of notes at the n resolution slots
     */
    private Polylist makeNoteChain(boolean useResolution) {
        Polylist noteChain = new Polylist();
        int curPartSlot = 0;
        Note added;
        while (curPartSlot < part.getSize()) {
            //System.out.println("slot = " + curPartSlot + ", within " + part.getSize() + " slots");
            added = (Note)part.getNote(curPartSlot);
            
            /* Never enters 'while' here when not using resolution, since slot
             * calculation should yield only (non-null) note attacks */
            int nonNullIndex = curPartSlot;
            while (added == null) {
                added = (Note)part.getNote(nonNullIndex--);
            }
            
            noteChain = noteChain.cons(added);
            if (useResolution) {
                curPartSlot += NOTE_RESOLUTION;
            } else {
                curPartSlot += added.getRhythmValue();
            }
        }
        return noteChain.reverse();
    }
    
    /**
     * Builds a polylist of n-1 intervals between the n notes in the
     * notechain.
     */
    private Polylist makeIntervalChain(Polylist noteChain) {
        Polylist intervalChain = new Polylist();
        Polylist notes = noteChain;
        
        while (notes.rest().nonEmpty()) {
            
            Note car = (Note)notes.first();
            Note cadr = (Note)notes.second();
            //System.out.println("car: " + car + "; cadr: " + cadr);
            intervalChain = intervalChain.cons((int)(cadr.getPitch() - car.getPitch()));
            
            notes = notes.rest();
            //System.out.println("notes: " + notes);
        }
        return intervalChain.reverse();
    }
    
    /**
     * Computes similarity of this contour to another contour.
     */
    public int similarity(MelodyContour c1) {
        return similarity(this, c1);
    }
    
    /**
     * Computes similarity of two contours
     */
    public static int similarity(MelodyContour c1, MelodyContour c2) {
        int a1, a2, a3, b1, b2, b3;
        a1 = a2 = a3 = b1 = b2 = b3 = 1;
        
        /** FOR NOW,
         * similarity is a combination of melodic, harmonic, and rhythmic similarity
         * where each has a scalar coefficient and an int exponent for weighting purposes.
         * Currently, coefficients and exponents are all set to 1.
         */
        return (int)(Math.pow( a1*melodicSim(c1, c2), b1) +
                     Math.pow( a2*rhythmicSim(c1, c2), b2) +
                     Math.pow( a3*harmonicSim(c1, c2), b3));      
    }
    
    /**
     * Computes melodic similarity.
     * First gets a parsons encoding of the melody, then applies the edit distance
     * metric to compared the string encodings.
     */
    public static int melodicSim(MelodyContour c1, MelodyContour c2) {
        Polylist par1 = parsons(c1);
        Polylist par2 = parsons(c2);
        
        char[] par1chars = new char[par1.length()];
        char[] par2chars = new char[par2.length()];
        
        int i = 0;
        
        // Encode the interval information.
        while (par1.nonEmpty()) {
            par1chars[i] = (Character)par1.first();
            par1 = par1.rest();
            i++;
        }
        i = 0;
        while (par2.nonEmpty()) {
            par2chars[i] = (Character)par2.first();
            par2 = par2.rest();
            i++;
        }
        
        Levenshtein metric = new Levenshtein();
        
        return metric.computeLev(par1chars, par2chars);
    }
    
    /**
     * Computes rhythmic similarity.
     * To be written.
     */
    public static int rhythmicSim(MelodyContour c1, MelodyContour c2) {
        return 0;
    }
    
    /**
     * Computes harmonic similarity.
     * To be written.
     */
    public static int harmonicSim(MelodyContour c1, MelodyContour c2) {
        return 0;
    }
    
    
    /* Parsons encoding code ------------------ */
    
    /**
     * Returns a polylist of parsons symbols from a MelodyContour
     */
    public static Polylist parsons(MelodyContour c) {
        return parsons(c.getIntervalChain());
    }
    
    /**
     * Returns a polylist of parsons symbols from a polylist of note intervals
     */
    public static Polylist parsons(Polylist intervals) {
        if (intervals.equals(Polylist.nil))
            return intervals;
        else
            return Polylist.cons(
                    parsonsChar(((Integer)intervals.first())),
                    parsons(intervals.rest()));
    }
    
    /**
     * Given an interval (positive or negative integer), returns a character
     * based on the direction.
     */
    private static char parsonsChar(int d) {
        char code;
        
        if (d > 0)
            code = 'U';
        else if (d == 0)
            code = 'R';
        else
            code = 'D';
        
        return code;
    }
    
    /* Accessors ----------------- */
    
    /**
     * Gets the note chain for this contour.
     */
    public Polylist getNoteChain() {
        return noteChain;
    }
    
    /**
     * Gets the interval chain for this contour.
     */
    public Polylist getIntervalChain() {
        return intervalChain;
    }
    
    /**
     * Gets the MelodyPart that was passed to this contour.
     */
    public MelodyPart getMelodyPart() {
        return part;
    }
    
    /* Edit distances class --------------- */
    
    private static class Levenshtein {
        
        // Costs of the operations used in edit
        final int DEL_COST = 1;
        final int INS_COST = 1;
        final int SUB_COST = 1;
        
        public Levenshtein() {}
        
        // Dynamic programming implementation of edit distance.
        public int computeLev(char[] w, char[] z) {
            int[][] distance = new int[w.length+1][];       // Make the table
            
            // Make all the rows, and initialize the first column
            for(int i = 0; i <= w.length; i++) {
                distance[i] = new int[z.length+1];
                distance[i][0] = i;
            }
            
            // Initialize the first row
            for(int j = 0; j <= z.length; j++) {
                distance[0][j]=j;
            }
            
            // Compute the minimum cost of editting to match through each substring
            for(int i = 1; i <= w.length; i++) {
                for(int j = 1; j <= z.length; j++) {
                    distance[i][j] = min(
                            distance[i-1][j] + DEL_COST,
                            distance[i][j-1] + INS_COST, 
                            distance[i-1][j-1] + ((w[i-1] == z[j-1]) ? 0 : SUB_COST)
                            );
                }
            }
            
            // The value at the end of the table is the total min edit cost for
            // editting one string into the other, so we return this value
            return distance[w.length][z.length];
        }
        
        private int min(int x, int y, int z) {
            return Math.min(x, Math.min(y, z));
        }
        
    }
}
