/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.cykparser;

import imp.brickdictionary.ChordBlock;
import java.util.ArrayList;

/** SubstituteList
 * A class describing a list of possible chords to replace a chord of a given
 * key and quality
 * 
 * @author Xanda Schofield
 */
public class SubstituteList {
    
    // Data Members //
    private ArrayList<String> names; // a list containing first the quality of
                                     // each substitute chord
    
    private ArrayList<Long> keys;    // a list carrying keys corresponding to
                                     // chord qualities in names
   
   /** Default constructor for a SubstituteList
     * 
     * Constructs an empty SubstituteList
     */
    public SubstituteList() {
        names = new ArrayList<String>();  
        keys = new ArrayList<Long>();
    }
    
    /** getNames
     * Gets the list of all chord qualities contained in the SubstituteList
     * @return an ArrayList of Strings describing the chord names
     */
    public ArrayList<String> getNames() {
        return names;
    }
    
    /** getKeys
     * Gets the list of all chord keys pertaining the the chord qualities listed
     * in the SubstituteList
     * @return an ArrayList of longs describing the keys of the SubstituteList's
     *         described chords
     */
    public ArrayList<Long> getKeys() {
        return keys;
    }
    
    /** getName
     * Gets the ith chord quality in the SubstituteList
     * @param i, the index of the chord
     * @return a String of the chord quality
     */
    public String getName(int i) {
        return names.get(i);
    }
    
    /** getKey
     * Gets the ith chord key in the SubstituteList
     * @param i, the index of the chord
     * @return a String of the chord quality
     */
    public long getKey(int i) {
        return keys.get(i);
    }
    
    /** length
     * Returns the length of the SubstituteList
     * @return an int of the SubstituteList's size; 
     */
    public int length() {
        assert(keys.size() == names.size());
        return keys.size();
    }
    
    /** add / 2
     * Adds a single chord the appropriate difference away to a SubstituteList
     * @param c, a Chord
     * @param diff, the transposition required of the Chord
     */
    public void add(ChordBlock c, long diff) {
        names.add(c.getSymbol());
        keys.add(modKeys(c.getKey() + diff));
    }
    
    /** addAll / 1
     * Adds all chords as described by a SubstituteList to itself
     * 
     * @param l, a filled in and appropriately transposed SubstituteList
     */
    public void addAll(SubstituteList l)
    {
        names.addAll(l.getNames());
        keys.addAll(l.getKeys());
    }
    
    /** hasMode / 1
     * Checks if a given mode is contained in the list of chords
     * 
     * @param mode, a String describing a mode (as a chord quality)
     * @return whether mode is in the list of chord qualities
     */
    public boolean hasMode(String mode)
    {
        String tempMode = mode.toLowerCase();
        ArrayList<String> tempList = new ArrayList<String>();
        String temp;
        for(String n : names) {
            temp = n.toLowerCase();
            tempList.add(temp);
        }
        return tempList.contains(tempMode);
    }
    
    /** isEmpty
     * Describes is a SubstituteList is empty
     * @return a boolean describing whether the SubstituteRule hasMode no chords
     */
    public boolean isEmpty() {
        if(names.isEmpty() || keys.isEmpty())
            return true;
        else
            return false;
    }
    
    /** nonEmpty
     * Describes is a SubstituteList is nonempty
     * @return a boolean describing whether the SubstituteRule hasMode any chords
     */
    public boolean nonEmpty() {
        return !isEmpty();
    }
    
   /** modKeys
     * Converts a calculated key into a number between 0 and 11, inclusive
     * @param i, the key's value as a long without applying a modulus
     * @return the correct key as a long between 0 and 11
     */
    private long modKeys(long i) {
        return (i + BinaryProduction.TOTAL_SEMITONES)
                % BinaryProduction.TOTAL_SEMITONES;
    }
 
    public String toString()
      {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("(");
        for( String name: names )
          {
            buffer.append(name);
            buffer.append(" ");
          }
        buffer.append(")");
        
        return buffer.toString();
      }
    // end of SubstituteList class 
}
