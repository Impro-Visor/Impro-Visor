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
 * MelodySYmbol represents symbols that can occur in a melody.
 * Currently these are NoteSymbol and VolumeSymbol.
 * @author keller
 */

public class MelodySymbol
{
    
/**
 * Make a melody symbol from a String representing either a note, rest, or
 * volume.
 * @param string
 * @return 
 */
public static MelodySymbol makeMelodySymbol(String string)
  {
    char firstChar = string.charAt(0);
    switch (firstChar )
      {
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
            
        case 'r':
            return NoteSymbol.makeNoteSymbol(string);
            
        case 'v':
            return VolumeSymbol.makeVolumeSymbol(string);
            
        default:
            return null;
      }
  }
}
