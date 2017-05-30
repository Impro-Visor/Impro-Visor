/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author keller
 */
public class PartList
{
private ArrayList<MelodyPart> theList;

public PartList(int size)
  {
  theList = new ArrayList<MelodyPart>(size);
  }

public ListIterator<MelodyPart> listIterator()
  {
    return theList.listIterator();
  }

public int size()
  {
    return theList.size();
  }

public MelodyPart get(int i)
  {
    return theList.get(i);
  }

/**
 * Caution: set seems not to work, for reasons I don't understand.
 * The capacity doesn't increase as prescribed.
 *
 @param i
 @param part
 */
public void set(int i, MelodyPart part)
  {
    theList.ensureCapacity(i);
    for( int j = theList.size(); j <= i; j++ )
      {
        theList.add(null);
      }
    theList.set(i, part);
  }

public void setSize(int size)
  {
    theList.ensureCapacity(size);
    for( int j = theList.size(); j < size; j++ )
      {
        theList.add(null);
      }
  }

public void add(MelodyPart part)
  {
    theList.add(part);
  }

public void remove(int i)
  {
    theList.remove(i);
  }

public void move(int i1, int i2){
    theList.add(i2,theList.remove(i1));
}
}
