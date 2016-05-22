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

package imp.style.pianoroll;

import java.util.Comparator;

/**
 * Compare one PianoRollBar to another, for sorting purposes.
 * Comparision is first by row, then by slot within row.
 * @author keller
 */
public class PianoRollBarComparator
        implements Comparator
{
public int compare(Object obj1, Object obj2)
  {
  if( obj1.equals(obj2) )
  {
      return 0;
  }
  PianoRollBar bar1 = (PianoRollBar)obj1;
  PianoRollBar bar2 = (PianoRollBar)obj2;
  
  int row1 = bar1.getRow();
  int row2 = bar2.getRow();

  if( row1 < row2 )
    {
    return -1;
    }

  if( row1 == row2 )
    {
    int slot1 = bar1.getStartSlot();
    int slot2 = bar2.getStartSlot();
    
    if(  slot1 < slot2 )
      {
      return -1;
      }

    return 1;
    }
  
  return 1;
  }

}

