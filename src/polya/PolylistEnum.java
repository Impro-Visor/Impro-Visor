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

/**
  *  PolylistEnum is an enumeration class for the class Polylist.  It 
  *  implements the interface java.util.Enumeration, i.e. the methods:
  *  hasMoreElements() and nextElement().  
 **/

public class PolylistEnum implements java.util.Enumeration
  {
  Polylist L;			// current list
 
  /**
    *  PolylistEnum constructs a PolylistEnum from a Polylist.
   **/

  public PolylistEnum(Polylist L)	// constructor
    {
    this.L = L;
    }


  /**
    *  hasMoreElements() indicates whether there are more elements left in 
    *  the enumeration.
   **/

  public boolean hasMoreElements()
    {
    return L.nonEmpty();
    }


  /**
    *  nextElement returns the next element in the enumeration.
   **/

  public Object nextElement() 
    {
    if( L.isEmpty() )
      throw new 
        java.util.NoSuchElementException("No next element in Polylist");

    Object result = L.first();
    L = L.rest();
    return result;
    }
  }

