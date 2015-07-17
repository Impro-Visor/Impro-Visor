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
 *
 * @author keller
 */

public class VolumeSymbol extends MelodySymbol
{
static final public int MAX_VOLUME = 127;

private int volume;

/**
 * construct a VolumeSymbol from an integer, respecting the range 0..MAX_VOLUME
 * @param volume 
 */

public VolumeSymbol(int volume)
  {
    this.volume = clipToRange(volume);
  }

/**
 * construct a VolumeSymbol from a string giving the volume. 
 * If the string is mal-formed, MAX_VOLUME results.
 * @param string 
 */

public VolumeSymbol(String string)
  {
    try 
      {
        int tentativeVolume = Integer.parseInt(string);
        this.volume = clipToRange(tentativeVolume);
      }
    catch( Exception e )
      {
        // error in string
        this.volume = MAX_VOLUME;
      }
  }

public static int clipToRange(int volume)
  {
    return volume > MAX_VOLUME ? MAX_VOLUME : volume < 0 ? 0 : volume;
  }

public int getVolume()
  {
    return volume;
  }

public static VolumeSymbol makeVolumeSymbol(String string)
  {
    return new VolumeSymbol(string.substring(1));
  }

@Override
public String toString()
  {
    return "v" + volume;
  }
}
