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
    
    int bassTransposition;
    int chordTransposition;
    int melodyTransposition;

    public Transposition(int bassTransposition, 
                         int chordTransposition, 
                         int melodyTransposition)
    {
        this.bassTransposition   = bassTransposition;
        this.chordTransposition  = chordTransposition;
        this.melodyTransposition = melodyTransposition;
    }

    public int getBassTransposition()
    {
        return bassTransposition;
    }

    public int getChordTransposition()
    {
        return chordTransposition;
    }

    public int getMelodyTransposition()
    {
        return melodyTransposition;
    }
    
    public Transposition newBassTransposition(int value)
    {
        return new Transposition(value, chordTransposition, melodyTransposition);
    }
        
    public Transposition newChordTransposition(int value)
    {
        return new Transposition(bassTransposition, value, melodyTransposition);
    }
    
    public Transposition newMelodyTransposition(int value)
    {
        return new Transposition(bassTransposition, chordTransposition, value);
    }
    
    public String toString()
    {
        return bassTransposition + " " + chordTransposition + " " + melodyTransposition;
    }
  }
