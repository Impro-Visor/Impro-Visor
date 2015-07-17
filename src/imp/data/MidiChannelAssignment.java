/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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
 * Encapsulates a channel assignment for Impro-Visor channels.
 * Note that the user numbers are 1 higher than the internal numbers.
 * These are internal numbers.
 * @author keller
 */
public class MidiChannelAssignment
{
/**
 * default values
 */
private int melodyChannel = 0;
private int chordChannel  = 3;
private int bassChannel   = 6;
private int drumChannel   = 9;

public MidiChannelAssignment()
  {
  // use defaults   
  }

public MidiChannelAssignment(int melody, int chord, int bass, int drum)
  {
    setMelodyChannel(melody);
    setChordChannel(chord);
    setBassChannel(bass);
    setDrumChannel(drum);
  }

public int getMelodyChannel()
  {
    return melodyChannel;
  }

public int getChordChannel()
  {
    return chordChannel;
  }

public int getBassChannel()
  {
    return bassChannel;
  }

public int getDrumChannel()
  {
    return drumChannel;
  }

public final void setMelodyChannel(int value)
  {
    melodyChannel = value;
  }

public final void setChordChannel(int value)
  {
    chordChannel = value;
  }

public final void setBassChannel(int value)
  {
    bassChannel = value;
  }

public final void setDrumChannel(int value)
  {
    drumChannel = value;
  }
}
