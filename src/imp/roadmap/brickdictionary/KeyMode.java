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

package imp.roadmap.brickdictionary;

import polya.Polylist;
import static polya.Polylist.list;

/**
 * purpose: Object for key/mode pairs corresponding to blocks
 * @author Zachary Merritt
 */
public class KeyMode {
    
    private long key = -1;      // the key of a given KeyMode
    private String mode = "";   // the mode of a given KeyMode
    
    /** KeyMode / 2
     * Constructs a properly initialized KeyMode
     * @param k, a key (a long)
     * @param m, a mode (a String)
     */
    public KeyMode(long k, String m) {
        key = k;
        mode = m;
    }
    
    /** KeyMode / 0
     * Default constructor for a KeyMode. Returns a no-chord modeless KeyMode.
     */
    public KeyMode() {}
    
    /** getKey
     * Gets the value of the key
     * @return the key, a long
     */
    public long getKey() {
        return key;
    }
    
    /** setKey
     * Sets the key to the specified value
     * @param k, a key as a long
     */
    public void setKey(long k) {
        key = k;
    }
    
    /** getMode
     * Gets the mode of the KeyMode
     * @return the mode, a String
     */
    public String getMode() {
        return mode;
    }
    
    /** setMode
     * Sets the mode to the specified value
     * @param m, a mode as a String
     */
    public void setMode(String m) {
        mode = m;
    }
    
    /** toPolylist
     * Returns a Polylist representation of a KeyMode.
     * @return a Polylist of form (mode, key)
     */
    public Polylist toPolylist()
      {
        return list(mode, key);
      }
    
    /** toString
     * Returns a String representation of a KeyMode.
     * @return a String of the Polylist of the KeyMode
     */
    @Override
    public String toString()
      {
        return toPolylist().toString();
      }
    
    // end of class KeyMode

}
