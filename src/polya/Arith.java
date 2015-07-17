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

// file:    Arith.java
// author:  Robert Keller
// purpose: Class Arith of polya package
//          Implements "polymorphic arithmetic"

public class Arith
  {
  static public boolean 
  greaterThan(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return ((Long)v1).longValue() > ((Long)v2).longValue();
        }
      else
        {
        return ((Long)v1).longValue() > ((Number)v2).doubleValue();
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return ((Number)v1).doubleValue() > ((Long)v2).longValue();
        }
      else
        {
        return ((Number)v1).doubleValue() > ((Number)v2).doubleValue();
        }
      }
    }

  static public boolean 
  greaterThanOrEqual(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return ((Long)v1).longValue() >= ((Long)v2).longValue();
        }
      else
        {
        return ((Long)v1).longValue() >= ((Number)v2).doubleValue();
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return ((Number)v1).doubleValue() >= ((Long)v2).longValue();
        }
      else
        {
        return ((Number)v1).doubleValue() >= ((Number)v2).doubleValue();
        }
      }
    }

  static public boolean 
  equal(Object v1, Object v2)
    {
    if( v1.equals(v2) )
      return true;

    if( v1 instanceof Polylist )
      {
      if( v2 instanceof Polylist )
        return Polylist.equals((Polylist)v1, (Polylist)v2); 
      else 
        return false;
      }

    if( v2 instanceof Polylist )
      {
      return false;
      }

    if( v1 instanceof String )
      {
      if( v2 instanceof String )
        return v1.equals(v2);
      else
        return false;
      }

    if( v2 instanceof String )
      {
      return false;
      }

    if( v1 instanceof Boolean )
      {
      if( v2 instanceof Boolean )
        return v1.equals(v2);
      else
        return false;
      }

    if( v2 instanceof Boolean )
      {
      return false;
      }

    if( v1 instanceof Number )
      {
      if( v1 instanceof Long )
	{
	if( v2 instanceof Long )
	  {
	  return ((Long)v1).longValue() == ((Long)v2).longValue();
	  }
	else
	  {
	  return ((Long)v1).longValue() == ((Number)v2).doubleValue();
	  }
	}
      else
	{
	if( v2 instanceof Long )
	  {
	  return ((Number)v1).doubleValue() == ((Long)v2).longValue();
	  }
	else
	  {
	  return ((Number)v1).doubleValue() == ((Number)v2).doubleValue();
	  }
	}
      }

    return false;
    }

  static public Number
  add(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return new Long(  ((Long)v1).longValue() 
                        + ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Long)v1).longValue() 
                          + ((Double)v2).doubleValue());
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return new Double(  ((Double)v1).doubleValue() 
                          + ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Double)v1).doubleValue() 
                          + ((Double)v2).doubleValue());
        }
      }
    }

  static public Number 
  subtract(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return new Long(  ((Long)v1).longValue() 
                        - ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Long)v1).longValue() 
                          - ((Double)v2).doubleValue());
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return new Double(  ((Double)v1).doubleValue() 
                          - ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Double)v1).doubleValue() 
                          - ((Double)v2).doubleValue());
        }
      }
    }

  static public Number 
  multiply(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return new Long(  ((Long)v1).longValue() 
                        * ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Long)v1).longValue() 
                          * ((Double)v2).doubleValue());
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return new Double(  ((Double)v1).doubleValue() 
                          * ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Double)v1).doubleValue() 
                          * ((Double)v2).doubleValue());
        }
      }
    }

  static public Number 
  divide(Object v1, Object v2)
    {
    if( !(v1 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Number) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    if( v1 instanceof Long )
      {
      if( v2 instanceof Long )
        {
        return new Long(  ((Long)v1).longValue() 
                        / ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Long)v1).longValue() 
                          / ((Double)v2).doubleValue());
        }
      }
    else
      {
      if( v2 instanceof Long )
        {
        return new Double(  ((Double)v1).doubleValue() 
                          / ((Long)v2).longValue());
        }
      else
        {
        return new Double(  ((Double)v1).doubleValue() 
                          / ((Double)v2).doubleValue());
        }
      }
    }
  static public Number 
  mod(Object v1, Object v2)
    {
    if( !(v1 instanceof Long) ) 
      throw new IllegalArgumentException("NotNumber: " + v1);

    if( !(v2 instanceof Long) ) 
      throw new IllegalArgumentException("NotNumber: " + v2);

    return new Long( ((Long)v1).longValue() 
                   % ((Long)v2).longValue());
    }
  
  /**
   * Convert a long (as used for fixed precision values in Polya) to an
   * int, assuming no digits will be lost. If digits will be lost, this will
   * throw an IllegalArgumentException
   * @param value
   * @return 
   */
  static public int long2int(long value)
    {
    if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) 
       {
       throw new IllegalArgumentException
            ("Loss of precision converting " + value + " to int");
       }
    else
      {
        return (int) value;
      }

    }
  }
