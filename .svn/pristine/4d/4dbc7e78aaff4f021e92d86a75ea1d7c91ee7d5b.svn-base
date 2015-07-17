/**
 * QuantisationException.java 0.1.6 8th November 2000
 *
 * Copyright (C) 2000 QUT
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.music.tools;

/**
 * Thrown to indicate a problem when trying to quantise a set of values.
 *
 * <p> By quantise, we mean to try and limit a set of values within a continuous
 * range, to a range of discrete values.  Generally this range of discrete
 * values is a set of multiples of a quantum value.
 *
 * <p> This exception may be thrown when a set of values within a continuous
 * range does not fit within the discrete values. It can also be thrown if the
 * quantum value is less than or equal to zero.
 *
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:52  2001
 *
 * @see jm.music.data.Phrase
 * @since   jMusic November 2000 Release
 */
public class QuantisationException extends Exception {
    /**
     * Constructs a <code>QuantisationException</code> with no detail message.
     * detail message. 
     */
    public QuantisationException() {
        super();
    }

    /**
     * Constructs a <code>QuantisationException</code> with the specified detail 
     * message. 
     *
     * @param s the detail message.
     */
    public QuantisationException(String s) {
        super(s);
    }
}
