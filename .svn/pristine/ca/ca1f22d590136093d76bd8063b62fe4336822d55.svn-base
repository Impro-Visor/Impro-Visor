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

import java.util.*;

/**
 * A cache mapping implementation used for various aspects of Advice trees to avoid recomputation
 * @see         Advice
 * @author      Bob Keller
 */

public class Cache 
{
private int capacity;

private int currentSize;

CacheComparator comparator;

LinkedList cached = new LinkedList();

public class Entry
  {
  Object key;
  Object value;

  Entry(Object key, Object value)
    {
    this.key = key;
    this.value = value;
    }
  }

Cache(int capacity, CacheComparator comparator)
  {
  this.capacity = capacity;
  this.comparator = comparator;
  currentSize = 0;
  }

/**
 * returns null if keyed object not in cache
 */

Object get(Object key)
  {
  ListIterator it = cached.listIterator(0);

  while( it.hasNext() )
    {
    Entry entry = (Entry)it.next();
    
    if( comparator.compare(key, entry.key) )
      {
      it.remove();             // take entry from list
      cached.addFirst(entry);  // move it to the front
      return entry.value;
      }
    }

  return null;            // not found
  }

/**
 * Put object in the cache
 */

void put(Object key, Object value)
  {
  if( currentSize == capacity )
    {
    cached.removeLast();
    }    

  cached.addFirst(new Entry(key, value));
  }

 public int getCapacity()
 {
     return capacity;
 }
  
 public void setCapacity(int c)
 {
     capacity = c;
 } 

 public void clearCache()
 {
//     System.out.println("Cleared");
     cached.clear();
 }

}
