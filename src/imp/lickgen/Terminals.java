/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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

package imp.lickgen;

import imp.data.Duration;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 * Methods relating to terminals in the grammar model
 * 
 * @author keller
 */

public class Terminals
{
/**
 * Returns true if ob is considered a terminal in the grammar.
 * @param ob
 * @return 
 */
public static boolean isTerminal(Object ob)
  {
    return isAbstractNote(ob)
        || isScaleDegree(ob) 
        || isSlope(ob) 
        || isTriadic(ob) 
        || isWrappedTerminal(ob);
  }

/**
 * An abstract note is a note of the type in the original abstract melody.
 * This means one of the letters {A, C, H, L, R, S, X, Y} followed by
 * a duration expression, such as 2+4.
 * @param ob
 * @return 
 */
public static boolean isAbstractNote(Object ob)
  {
    if( !(ob instanceof String) )
      {
        return false;
      }
    
    String string = (String)ob;
    int len = string.length();
    if( len == 0 )
      {
        return false;
      }
    
    switch( string.charAt(0) )
      {
        case 'A': // Approach
        case 'C': // Chord
        case 'H': // Helpful = Chord or Color
        case 'L': // Color
        case 'R': // Rest
        case 'S': // Scale
        case 'X': // Arbitrary
        case 'Y': // Outside
            break;
        default:
            return false;
      }
        //System.out.print("string = " + string);
        boolean isDuration  = Duration.isDuration(string.substring(1));
        //System.out.println(" isAbstractNote = " + isDuration);
        return  isDuration;
  }

/**
 * A wrapped terminal is an abstract note as a single element of a list
 * S expression.
 * @param ob
 * @return 
 */

public static boolean isWrappedTerminal(Object ob)
  {
    if( !(ob instanceof Polylist) )
      {
        return false;
      }
    
    if( isScaleDegree(ob) || isSlope(ob) || isTriadic(ob) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist) ob;
    
    if( oblist.isEmpty() )
      {
        return false;
      }
    
    return  isAbstractNote(oblist.first()); //isTerminalSpecifiedInFile(oblist.first());
  }

public static boolean isSlope(Object ob)
  {
    if( !(ob instanceof Polylist) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist)ob;
    
    if( oblist.length() < 4 )
      {
        return false;
      }
    
    if( !oblist.first().equals("slope") )
      {
        return false;
      }
    
    return true;
  }

public static boolean isTriadic(Object ob)
  {
    if( !(ob instanceof Polylist) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist)ob;
    
    if( oblist.length() != 3 )
      {
        return false;
      }
    
    if( !oblist.first().equals("triadic") )
      {
        return false;
      }
    
    return true;
  }

public static boolean isScaleDegree(Object ob)
  {
    if( !(ob instanceof Polylist ) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist)ob;
    
    if( oblist.length() != 3 )
      {
        return false;
      }
    
    Object first = oblist.first();
    
    if( !(first instanceof String) )
      {
        return false;
      }
       
    if( !("X".equals((String)first)) )
      {
        return false;
      }
    
    Object second = oblist.second();
    
    if( !(second instanceof Long || second instanceof String ) )
      {
        return false;
      }
    
    Object third = oblist.third();
 
    if( !(third instanceof Long || third instanceof String ) )
      {
        return false;
      }
    return true;
  }

/**
 * Get the duration of various terminals.
 * This is used in calculating chordSlot, for example.
 * @param ob
 * @return 
 */
public static int getDuration(Object ob)
  {
    //System.out.print("duration of " + ob + " = ");
    int result = 0;
    
    if( ob instanceof String )
      {
        result = Duration.getDuration(((String)ob).substring(1));
      }
    else if( isScaleDegree(ob) )
      {
        result = Duration.getDuration(((Polylist) ob).third().toString());
      }
    else if( isSlope(ob) )
      {
        // Get the tail from the fourth element on
        Polylist body = ((Polylist)ob).rest().rest().rest();
        int sum = 0;
        while( body.nonEmpty() )
          {
            sum += getDuration(body.first());
            body = body.rest();
          }
        result = sum;
      }
    else if( isTriadic(ob) )
      {
        result = Duration.getDuration(((Polylist)ob).second().toString());
      }
    
    //System.out.println(result);
    
    return result;
  }

public static int getDurationAbstractMelody(Polylist L)
  {
    int duration = 0;
    while( L.nonEmpty() )
      {
        duration += getDuration(L.first());
        L = L.rest();
      }
    return duration;
  }

public static Polylist truncateAbstractMelody(Polylist L, int desiredDuration)
  {
    PolylistBuffer buffer = new PolylistBuffer();
    int duration = 0;
    while( L.nonEmpty() )
      {
        Object first = L.first();
        int dur = getDuration(first);
        if( duration + dur > desiredDuration )
          {
            break;
          }
        buffer.append(first);
        duration += dur;
        L = L.rest();
      }
    return buffer.toPolylist();    
  }    
}
