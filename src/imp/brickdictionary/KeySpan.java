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

package imp.brickdictionary;

import polya.Polylist;

/**
 * purpose: Object for key/mode pairs with durations -- mainly for drawing
 * @author Zachary Merritt
 */
public class KeySpan {
    private KeyMode keymode = new KeyMode();    // the KeyMode during the span
    private long duration = 0;                  // the duration of the span
    
    /** KeySpan / 2
     * Constructs a KeySpan based upon a constructed KeyMode and duration
     * @param km, a KeyMode
     * @param d, a duration (a long)
     */
    public KeySpan(KeyMode km, long d) {
        keymode = km;
        duration = d;
    }
    
    /** KeySpan / 3
     * Constructs a KeySpan based upon the key, mode and duration
     * @param k, the key (a long)
     * @param m, the mode (a String)
     * @param d, the duration (a long) 
     */
    public KeySpan(long k, String m, long d) {
        this(new KeyMode(k, m), d);
    }
    
     /** KeySpan
     * Constructs a KeySpan based on a Block
     */
    
    public KeySpan(Block b)
      {
        this(b.getKey(), b.getMode(), b.getDuration());
      }
    
    /** KeySpan / 0
     * Constructs a default KeySpan for no chord and no duration
     */
    public KeySpan() {}
    
    /** getKey
     * Get the key of the KeySpan
     * @return the key, a long
     */
    
    public long getKey() {
        return keymode.getKey();
    }
    
    /** setKey
     * Sets the key of the KeySpan to a specified key
     * @param k, the key as a long
     */
    public void setKey(long k) {
        keymode.setKey(k);
        //System.out.println("setting key to " + k + ": " + this);
    }
    
    /** getMode
     * Gets the mode of a KeySpan
     * @return the mode, a String
     */
    public String getMode() {
        return keymode.getMode();
    }
    
    /** setMode
     * Sets the mode of the KeySpan to a given mode
     * @param m, the mode as a String
     */
    public void setMode(String m) {
        keymode.setMode(m);
    }
    
    /** getDuration
     * Gets the duration of the KeySpan
     * @return the duration, a long
     */
    public long getDuration() {
        return duration;
    }
    
    /** setDuration
     * Sets the duration of the KeySpan to the given duration 
     * @param d, the duration as a long
     */
    public void setDuration(long d) {
       duration = d;
       //System.out.println("setting duration to " + d + ": " + this);
    }
    
    /** 
     * Augment the duration of the KeySpan by the argument
     * @param d, the duration as a long
     */
    public void augmentDuration(long d) {
        setDuration(duration + d);
    }
    
    /** toPolylist
     * Returns a Polylist representation of a KeySpan.
     * @return to 
     */
    public Polylist toPolylist()
      {
        return keymode.toPolylist().addToEnd(duration);
      }
    
     /** toString 
     * Returns a String representation of a KaySpan.
     * @return 
     */
    @Override
    public String toString()
      {
        return toPolylist().toString();
      }
   
    // end of class KeySpan
}
