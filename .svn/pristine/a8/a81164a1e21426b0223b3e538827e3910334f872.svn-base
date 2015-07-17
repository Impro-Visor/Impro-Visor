/**
 * Polya library: Implements Lisp-like structures in Java.
 *
 * Copyright (C) 2009 Robert Keller
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

package polya;

import java.util.Iterator;
import java.util.List;

/**
 * Get the elements of a Java Iterator into a Polylist,
 * @author keller
 */

public class PolylistIterator
{

public static Polylist iterator2Polylist(Iterator<Object> it)
  {
    PolylistBuffer buffer = new PolylistBuffer();
    
    while( it.hasNext() )
      {
        buffer.append(it.next());
      }

    return buffer.toPolylist();
  }

public static Polylist list2Polylist(List<Object> L)
  {
    return iterator2Polylist(L.iterator());
  }


}
