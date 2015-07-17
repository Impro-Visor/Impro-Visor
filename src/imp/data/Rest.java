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

/**
 * The Rest class represents a rest.
 * This class is just Note with a different constructor.
 */
public class Rest extends Note {

    /**
     * Creates a new Rest with the specified rhythm value.
     * @param rhythmValue       the rhythm value of the rest
     */
    public Rest(int rhythmValue) {
        super(REST, Accidental.NOTHING, rhythmValue);
    }

    /**
     * Creates a new Rest with no rhythm value
     */
    public Rest() {
        super(REST, Accidental.NOTHING);
    }

    /**
     * Returns a String representation of the Note
     * @return String   the String representation of the Note
     */
    public String toString() {
        String noteData = new String("REST: [RhythmValue = " + 
                                                 rhythmValue + "]");
        return noteData;
    }

}
