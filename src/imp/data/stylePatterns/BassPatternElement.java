/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.data.stylePatterns;

import imp.data.Duration;
import imp.data.Note;
import imp.util.ErrorLog;
import polya.Polylist;

/**
 *
 * @author keller
 */
public class BassPatternElement
{

public enum BassNoteType
{

BASS, PITCH, REPEAT, CHORD, SCALE, APPROACH, NEXT, REST, VOLUME, UNKNOWN

};

public enum AccidentalType
{

FLAT, NONE, SHARP

};

public enum DirectionType
{

UP, ANY, DOWN

};
BassNoteType noteType = BassNoteType.BASS;

AccidentalType accidental = AccidentalType.NONE;

int degree = 5;

DirectionType direction = DirectionType.ANY;

String durationString;

public BassPatternElement getCopy()
{
    return new BassPatternElement(noteType, degree, accidental, durationString, direction);
}

public BassPatternElement(String durationString)
  {
    this(BassNoteType.BASS, durationString);
  }

public BassPatternElement(BassNoteType noteType, String durationString)
  {
    this(noteType, durationString, DirectionType.ANY);
  }

public BassPatternElement(BassNoteType noteType, String durationString,
                          DirectionType direction)
  {
    this.noteType = noteType;
    this.durationString = durationString;
    this.direction = direction;
  }

public BassPatternElement(BassNoteType noteType, int degree,
                          AccidentalType accidental, String durationString,
                          DirectionType direction)
  {
    this.noteType = noteType;
    this.degree = degree;
    this.accidental = accidental;
    this.durationString = durationString;
    this.direction = direction;
  }

public BassNoteType getNoteType()
  {
    return noteType;
  }

public void setNoteType(BassNoteType noteType)
{
    this.noteType = noteType;
}

public AccidentalType getAccidental()
  {
    return accidental;
  }

public void setAccidental(AccidentalType accidental)
{
    this.accidental = accidental;
}

public int getDegree()
  {
    return degree;
  }

public void setDegree(int degree)
{
    this.degree = degree;
}

public DirectionType getDirection()
  {
    return direction;
  }

public void setDirection(DirectionType direction)
{
    this.direction = direction;
}

public int getSlots()
  {
    return Duration.getDuration(durationString);
  }

public void setDuration(int slots)
{
    durationString = Note.getDurationString(slots);
}

public static BassPatternElement makeBassPatternElement(Object ob)
  {
    if( ob instanceof String )
      {
        // String should be something like B4 or B4+8/3

        String stringOb = (String) ob;

        if( stringOb.equals("") )
          {
            return null; // should never happen
          }
        
        String durationString = stringOb.substring(1);

        if( stringOb.startsWith("V") )
          {
            // volume
            return new BassPatternElement(BassNoteType.VOLUME, durationString);
          }

        BassNoteType noteType = getBassNoteType(stringOb.charAt(0));

        if( noteType == BassNoteType.UNKNOWN )
          {
            ErrorLog.log(ErrorLog.WARNING,
                         "Unknown bass note type: " + stringOb);
            return null;

          }
        if( durationString.equals("") )
          {
            ErrorLog.log(ErrorLog.WARNING,
                         "Bass pattern element has no duration: " + stringOb);
            return null;
          }
        else
          {
            // FIX: What about error checking?
            // Should return null if bad value
            // Just for checking at creation time, rather than later.
            int duration = Duration.getDuration(durationString);

            if( duration == 0 )
              {
                ErrorLog.log(ErrorLog.WARNING,
                             "Bass pattern element has 0 duration: " + stringOb);
                return null;
              }
            else
              {
                return new BassPatternElement(noteType, durationString);
              }
          }
      }
    else if( ob instanceof Polylist )
      {
        Polylist listOb = (Polylist) ob;
        int len = listOb.length();
        if( len >= 3 && len <= 4 )
          {
            if( listOb.first().equals("X") )
              {
                // Handle scale note, e.g. (X 5 8+16/3 U)

                AccidentalType accidental = AccidentalType.NONE;

                // Get the scale degree

                Object second = listOb.second();

                int degreeValue = 1;

                if( second instanceof Long )
                  {
                    degreeValue = ((Long) listOb.second()).intValue();

                    if( degreeValue < 1 || degreeValue > 7 )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Scale degree out of range 1 to 7 in bass note : " + ob);
                        return null;
                      }
                  }
                else if( second instanceof String )
                  {
                    String secondString = (String) second;
                    String degreeString = secondString.substring(1);

                    try
                      {
                        degreeValue = Integer.parseInt(degreeString);
                      }
                    catch( Exception e )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Scale degree is wrong in bass note : " + ob);
                        return null;
                      }

                    switch( secondString.charAt(0) )
                      {
                        case 'b':
                            accidental = AccidentalType.FLAT;
                            break;   // flat

                        case '#':
                            accidental = AccidentalType.SHARP;
                            break; // sharp

                        default:
                            ErrorLog.log(ErrorLog.WARNING,
                                         "Scale degree is wrong in bass note : " + ob);
                            return null;
                      }
                  }

                // Get the duration

                String durationString = "";

                Object third = listOb.third();

                if( third instanceof Long || third instanceof String )
                  {

                    durationString = "" + third;

                    // for checking purposes

                    int duration = Duration.getDuration(durationString);

                    if( duration == 0 )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Bass pattern element has 0 duration: " + ob);
                        return null;
                      }
                  }


                // Ge the direction, if there is one.

                DirectionType direction = DirectionType.ANY;

                if( len == 4 )
                  {
                    if( listOb.fourth().equals("U") )
                      {
                        direction = DirectionType.UP;
                      }
                    else if( listOb.fourth().equals("D") )
                      {
                        direction = DirectionType.DOWN;
                      }
                    else
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Direction must be U or D in bass note : " + ob);
                        return null;
                      }

                  }

                return new BassPatternElement(BassNoteType.PITCH, degreeValue,
                                              accidental, durationString,
                                              direction);
              }
          }
      }
    ErrorLog.log(ErrorLog.WARNING, "Unrecognized bass note : " + ob);
    return null;
  }


// Classify bass note type
static public BassNoteType getBassNoteType(char c)
  {
    switch( c )
      {
        case 'B':
        case 'b':
        case 'X':
        case 'x':
            return BassNoteType.BASS;

        case '=':
            return BassNoteType.REPEAT;

        case 'C':
        case 'c':
            return BassNoteType.CHORD;

        case 'S':
        case 's':
            return BassNoteType.SCALE;

        case 'A':
        case 'a':
            return BassNoteType.APPROACH;

        case 'N':
        case 'n':
            return BassNoteType.NEXT;

        case 'R':
        case 'r':
            return BassNoteType.REST;

        case 'V':
        case 'v':
            return BassNoteType.VOLUME;

        default:
            return BassNoteType.UNKNOWN;
      }
  }

public String getDurationString()
  {
    return durationString;
  }

public String getDegreeString()
  {
    return getAccidentalString() + getDegree();
  }

public String getAccidentalString()
  {
    switch( accidental )
      {
        case FLAT:
            return "b";
        case SHARP:
            return "#";
        default:
            return "";
      }
  }

public String getDirectionString()
  {
    switch( direction )
      {
        case UP:
            return "U";
        case DOWN:
            return "D";
        default:
            return "";
      }
  }

public Object getText()
  {
    switch( noteType )
      {
        default:
        case BASS:
            return "B" + getDurationString();

        case REPEAT:
            return "=" + getDurationString();

        case CHORD:
            return "C" + getDurationString();

        case SCALE:
            return "S" + getDurationString();

        case APPROACH:
            return "A" + getDurationString();

        case NEXT:
            return "N" + getDurationString();
            
        case PITCH:
            if( isDirectional() )
              {
                return Polylist.list("X", getDegreeString(), getDurationString(),
                                     getDirectionString());
              }
            else
              {
                return Polylist.list("X", getDegreeString(), getDurationString());
              }
      }
  }

public boolean isDirectional()
  {
    return direction != DirectionType.ANY;
  }

public boolean nonRest()
  {
    return noteType != BassNoteType.REST;
  }

}
