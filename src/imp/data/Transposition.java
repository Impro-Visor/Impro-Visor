/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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
 * Transposition is a an object for packaging and communicating transpositions
 * of chord, bass, and melody.
 * @author keller
 */
public class Transposition
  {
    public static Transposition none = new Transposition(0, 0, 0);
    
    int chordTransposition;
    int bassTransposition;
    int melodyTransposition;

    public Transposition(int chordTransposition, int bassTransposition,
                         int melodyTransposition)
    {
        this.chordTransposition  = chordTransposition;
        this.bassTransposition   = bassTransposition;
        this.melodyTransposition = melodyTransposition;
    }

    public int getChordTransposition()
    {
        return chordTransposition;
    }

    public int getBassTransposition()
    {
        return bassTransposition;
    }

    public int getMelodyTransposition()
    {
        return melodyTransposition;
    }
  }
