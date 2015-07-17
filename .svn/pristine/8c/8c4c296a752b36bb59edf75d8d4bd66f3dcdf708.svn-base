/*
 * Handles conversion of duration values form the leadsheet
 */
package imp.data;

import imp.util.ErrorLog;
import java.io.Serializable;


/**
 *
 * @author keller
 */

public class Duration
       implements imp.Constants, Serializable
{

public static int default_numerator = 8;	// eighth note default

  /* This overlaps noteFromLeadsheet and should be refactored. */
  public static int durationFromLeadsheet(String string)
    {
    int len = string.length();

    if( len == 0 )
      {
      return 0;
      }

    char c = string.charAt(0);

    if( c == RESTCHAR )
      {
      int duration = getDuration(string.substring(1));
      return duration;
      }

    if( !PitchClass.isValidPitchStart(c) )
      {
      return 0;
      }

    int index = 1;

    boolean natural = true;
    boolean sharp = false;

    StringBuilder noteBase = new StringBuilder();

    noteBase.append(c);

    if( index < len )
      {
      char second = string.charAt(1);
      if( second == SHARP || second == FLAT )
        {
        index++;
        noteBase.append(second);
        natural = false;
        sharp = (second == SHARP);
        }
      }

    // Check for any octave shifts specified in the notation

    boolean more = true;
    while( index < len && more )
      {
      switch( string.charAt(index) )
        {
        case PLUS:
          index++;
          break;

        case MINUS:
          index++;
          break;
            
        case 'u':
            index++;
            break;

        default:
          more = false;
        }
      }

    return getDuration(string.substring(index));
    }

  /**
   * Use this to get duration from duration string such as
   * "4+8" as in leadsheet notation, except that a null 
   * string returns 0 rather than the default value.
   */

  public static int getDuration0(String item)
  {
    if( item.trim().equals("") )
      {
      return 0;
      }
    else 
      {
      return getDuration(item);
      }
  }
  
  /**
   * This method provides part of the functionality of noteFromLeadhsheet,
   * namely getting the duration part of a note or rest.
   */
  public static int getDuration(String item)
    {
    int len = item.length();
    int index = 0;
    if( len == 0 || !Character.isDigit(item.charAt(index)) )
      {
      return DEFAULT_DURATION;
      }
    
    // Check for zero
    // by trying to convert to a number.
    // If conversion is unsuccessful or value is 0, 0 will be returned
    
    int value = 1;
    try 
      {
        value = Integer.parseInt(item);
      }
    catch (Exception ex )
      {
      }
    
    if( value == 0 )
      {
        return 0;
      }

    int duration = 0;
    boolean firsttime = true;

    // Example of input is 2.+8/3+32 meaning the value of a dotted halfnote
    // eighth note triplet, and 32nd note.
    // Note that there is no + to start with.

    while( index < len && ((item.charAt(index) == PLUS) || firsttime || (item.charAt(index) == 'u')) )
      {
      int numerator;
      int denominator = 1;
      int this_duration;

      if( firsttime )
        {
        firsttime = false; // no leading +
        }
      else
        {
        index++;		  // skip infix +'s
        }

      boolean hasDigit = false;

      // Accumulate digits part
      StringBuilder dur = new StringBuilder();
      while( index < len && Character.isDigit(item.charAt(index)) )
        {
        hasDigit = true;
        dur.append(item.charAt(index));
        index++;
        }

      if( hasDigit )
        {
        numerator = new Integer(dur.toString()).intValue();
        }
      else
        {
        numerator = default_numerator;
        }

      int slots = WHOLE;  // 1 whole note = 4 quarter  notes

      // Check for tuplet
      if( index < len && item.charAt(index) == SLASH )
        {
        index++;
        if( index >= len || !Character.isDigit(item.charAt(index)) )
          {
          /* FIX: suppress warning for now, as the app will hang.
          ErrorLog.log(ErrorLog.WARNING,
                  "Expected digit after / in " + item + " returning default value ");
                  *
          */
          return DEFAULT_DURATION;
          }

        StringBuilder tuplet = new StringBuilder();
        while( index < len && Character.isDigit(item.charAt(index)) )
          {
          tuplet.append((Character)item.charAt(index));
          index++;
          }

        denominator = new Integer(tuplet.toString()).intValue();
        }

      if( denominator > 1 )
        {
        slots *= (denominator - 1); // was 2
        }

      /* FIX: suppress warning for now, as the app will hang.
      if( slots % (numerator*denominator) != 0 )
      {
      ErrorLog.log(ErrorLog.WARNING, "Tuplet value is not exact: " + item
      + ", doing the best we can");
      }
       */

      this_duration = slots / (numerator * denominator);

      // Handle dotted notes, which add to individual duration.
      
      int increment = this_duration;

      while( index < len && item.charAt(index) == DOT )
        {
        increment /= 2;
        this_duration += increment;
        index++;
        }

      duration += this_duration;
      }

    if( index < len )
      {
      /* FIX: suppress warning for now, as the app will hang.
      ErrorLog.log(ErrorLog.WARNING,
              "Ignoring garbage after end of note duration: " + item);
      */
      }

    if( duration <= 0 )
      {
      duration = DEFAULT_DURATION;
      }
//System.out.println("item = " + item + ", duration = " + duration);
    return duration;
    }    
  
   /**
   * Determines whether item is a valid duration string.
   */
  
  public static boolean isDuration(String item)
    {
    int len = item.length();
    int index = 0;
    if( len == 0 || !Character.isDigit(item.charAt(index)) )
      {
      return false;
      }
    
    // Check for zero
    // by trying to convert to a number.
    // If conversion is unsuccessful or value is 0, false will be returned
    
    int value = 1;
    try 
      {
        value = Integer.parseInt(item);
      }
    catch (Exception ex )
      {
      }
    
    if( value == 0 )
      {
        return false;
      }

    int duration = 0;
    boolean firsttime = true;

    // Example of input is 2.+8/3+32 meaning the value of a dotted halfnote
    // eighth note triplet, and 32nd note.
    // Note that there is no + to start with.

    while( index < len && ((item.charAt(index) == PLUS) || firsttime || (item.charAt(index) == 'u')) )
      {
      int numerator;
      int denominator = 1;
      int this_duration;

      if( firsttime )
        {
        firsttime = false; // no leading +
        }
      else
        {
        index++;		  // skip infix +'s
        }

      boolean hasDigit = false;

      // Accumulate digits part
      StringBuilder dur = new StringBuilder();
      while( index < len && Character.isDigit(item.charAt(index)) )
        {
        hasDigit = true;
        dur.append(item.charAt(index));
        index++;
        }

      if( hasDigit )
        {
        numerator = new Integer(dur.toString()).intValue();
        }
      else
        {
        numerator = default_numerator;
        }

      int slots = WHOLE;  // 1 whole note = 4 quarter  notes

      // Check for tuplet
      if( index < len && item.charAt(index) == SLASH )
        {
        index++;
        if( index >= len || !Character.isDigit(item.charAt(index)) )
          {
          return false;
          }

        StringBuilder tuplet = new StringBuilder();
        while( index < len && Character.isDigit(item.charAt(index)) )
          {
          tuplet.append((Character)item.charAt(index));
          index++;
          }

        denominator = new Integer(tuplet.toString()).intValue();
        }

      if( denominator > 1 )
        {
        slots *= (denominator - 1); // was 2
        }

      this_duration = slots / (numerator * denominator);

      // Handle dotted notes, which add to individual duration.
      
      int increment = this_duration;

      while( index < len && item.charAt(index) == DOT )
        {
        increment /= 2;
        this_duration += increment;
        index++;
        }

      duration += this_duration;
      }

    if( index < len )
      {
      return false;
      }

    if( duration <= 0 )
      {
      return false;
      }
    return true;
    }    
}
