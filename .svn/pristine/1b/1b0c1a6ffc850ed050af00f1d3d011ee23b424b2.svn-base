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

import java.io.*;
	
/**
  *  Implementation of list cells for Polya.
 */

class polycell implements Serializable
  {
  private Object First;
  private Object Rest;


  /**
    *  first() returns the first element of a NonEmptyList.
   **/ 

  Object first()
    {
    return First;
    }


  /**
    *  rest() returns the rest of a NonEmptyList.
   **/ 

  Polylist rest()
    {
    if( Rest instanceof Seed ) 
      {
      Rest = ((Seed)Rest).grow();
      }
    return (Polylist)Rest;
    }


  /**
    *  polycell is the constructor for the cell of a Polyist, 
    *  given a First and a Rest.
    *
    *  Use static method cons of class Polylist to avoid using 'new' 
    *  explicitly.
   **/ 

  polycell(Object First, Object Rest)
    {
    this.First = First;
    this.Rest = Rest;
    }


  /**
    *  setFirst() sets the first element of a NonEmptyList.
   **/ 

  void setFirst(Object value)
    {
    First = value;
    }
  }  // class polycell
