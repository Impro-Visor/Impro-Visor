/**
 * Polya library: Implements Lisp-like structures in Java.
 *
 * Copyright (C) 2009-2012 Robert Keller
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
 *
 * @author keller
 */
public class Formatting
{
public static String prettyFormat(Object Ob)
  {
  StringBuffer buffer = new StringBuffer();
  prettyFormat(0, Ob, buffer);
  return buffer.toString();
  }

public static String prettyFormat(int indent, Object Ob)
  {
  StringBuffer buffer = new StringBuffer();
  prettyFormat(indent, Ob, buffer);
  return buffer.toString();
  }

public static void prettyFormat(int indent, Object Ob, StringBuffer buffer)
  {
  if( Ob instanceof Polylist )
    {
    Polylist L = (Polylist)Ob;
    if( !sublistFree(L) )
      {
      prettyFormatList(indent + 1, L, buffer);
      return;
      }
    }
  spaces(indent, buffer);
  buffer.append(Ob.toString());
  }


public static void prettyFormatList(int indent, Polylist L, StringBuffer buffer)
  {
  PolylistEnum items = L.elements();
  boolean continueLine = false;
  spaces(indent, buffer);
  buffer.append("(");
  if( items.hasMoreElements() )
    {
    Object element = items.nextElement();
    if( element instanceof Polylist )
      {
      spaces(indent, buffer);
      buffer.append("\n");
      prettyFormat(indent + 1, element, buffer);
      }
    else
      {
      //spaces(indent, buffer);
      buffer.append(element);
      continueLine = true;
      }
    }

  while( items.hasMoreElements() )
    {
    Object element = items.nextElement();
    if( element instanceof Polylist )
      {
      buffer.append("\n");
      prettyFormat(indent + 1, element, buffer);
      continueLine = false;
      }
    else
      {
      if( continueLine )
        {
        buffer.append(" ");
        }
      else
        {
        buffer.append("\n");
        spaces(indent, buffer);
        }
      buffer.append(element);
      continueLine = true;
      }
    }
  spaces(indent, buffer);
  buffer.append(")\n");
  }


public static void spaces(int n, StringBuffer buffer)
  {
  while( n > 0 )
    {
    buffer.append(' ');
    n--;
    }
  }


public static boolean sublistFree(Polylist L)
  {
  if( L.isEmpty() )
    {
    return true;
    }

  if( L.first() instanceof Polylist )
    {
    return false;
    }

  return sublistFree(L.rest());
  }

}
