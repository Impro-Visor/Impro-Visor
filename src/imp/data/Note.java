/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

import imp.midi.MidiSynth;
import imp.Constants;
import imp.util.ErrorLog;
import imp.util.Trace;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import polya.Polylist;

/**
 * The Note class represents a note in a melody.
 * It stores the rhythm value, the MIDI pitchName number, and a flag
 * that indicates the accidental on the note.  Dynamic might be
 * something added in the future.
 * @see         Unit
 * @see         Part
 * @author      Stephen Jones
 */
public class Note
        implements Constants, Unit, Serializable
{
/**
 * the default pitchName constants for a Note
 */
public static final int DEFAULT_PITCH = 60;

public static final int UNDEFINED = -2;

/**
 * an int that stores the MIDI pitchName number
 */
private int pitch;

/**
 * The MIDI Volume (velocity) of this note, range 0-127
 */
private int volume = 127;

/**
 * a flag that indicates the accidental on the Note
 */
private Accidental accidental;

/**
 * an int that stores the rhythmValue
 */
protected int rhythmValue;

/**
 * pitchName of the note, disregarding accidentals, used to determine graphic
 */
private int drawnPitch = UNDEFINED;

/**
 * a boolean that is true if tied, false if not tied (used for display)
 */
private boolean tied;

/**
 * a boolean that is true the first in a tie, false otherwise
 */
private boolean firstTie;

/**
 * used in formatting output lines
 */
private static int accumulatedRhythmValue = 0;

public static int maxRhythmValuePerLine = 480;	// 4 beats


/**
 * Creates a Note with the specified pitchName, accidental, and rhythm value
 * @param pitch
 * @param accidental   a flag indicating the Note's accidental
 * @param rhythmValue  the rhythm value for the Note
 */
public Note(int pitch, Accidental accidental, int rhythmValue)
  {
  this.pitch = pitch;
  this.accidental = accidental;
  this.rhythmValue = rhythmValue;
  this.tied = false;
  this.firstTie = false;
  }


/**
 * Creates a Note with the specified pitchName and accidental
 * @param pitch
 * @param accidental   a flag indicating the Note's accidental
 */
public Note(int pitch, Accidental accidental)
  {
  this(pitch, accidental, DEFAULT_RHYTHM_VALUE);
  }


/**
 * Creates a Note with the specified pitchName, SHARP or FLAT, and rhythm value
     * @param pitch
 * @param natural      true if natural, false if sharp or flat
 * @param sharp        true for SHARP, false for FLAT
 * @param rhythmValue  the rhythm value for the Note
 */
public Note(int pitch, boolean natural, boolean sharp, int rhythmValue)
  {
  this.pitch = pitch;
  this.accidental = natural
          ? Accidental.NATURAL
          : getSharpOrFlat(sharp);
  this.rhythmValue = rhythmValue;
  this.tied = false;
  this.firstTie = false;
  }


/**
 * Creates a Note with the specified pitchName, SHARP or FLAT, and rhythm value
 * @param pitch
 * @param sharp        true for SHARP, false for FLAT
 * @param rhythmValue  the rhythm value for the Note
 */
public Note(int pitch, boolean sharp, int rhythmValue)
  {
  this.pitch = pitch;
  this.accidental = getSharpOrFlat(sharp);
  this.rhythmValue = rhythmValue;
  this.tied = false;
  this.firstTie = false;
  }


/**
 * Creates a Note with the specified pitchName, SHARP or FLAT, 
 * and default rhythm value
 * @param pitch       an int containing the MIDI number for the Note
 * @param sharp        true for SHARP, false for FLAT
 */
public Note(int pitch, boolean sharp)
  {
  this(pitch, sharp, DEFAULT_RHYTHM_VALUE);
  }


/**
 * Creates a Note with the specified pitchName and rhythm value
 * @param pitch       an int containing the MIDI number for the Note
 * @param rhythmValue  the Note's rhythmValue
 */
public Note(int pitch, int rhythmValue)
  {
  this(pitch,
          black[pitch % 12]
          ? Accidental.SHARP
          : Accidental.NATURAL,
          rhythmValue);
  }


/**
 * Creates a Note with the specified pitchName
 * @param pitch
 */
public Note(int pitch)
  {
  this(pitch, DEFAULT_RHYTHM_VALUE);
  }

/**
 * makeNote is a factory method for making a note out of a pitch and a
 * rhythmvalue. The difference between it and a constructor is that if
 * pitch == REST (defined in Constants.java) the returned value will be
 * a Rest (which is a kind of note), whereas if pitch != REST, the returned
 * value will be a regular note. This is intended to be more convenient to use
 * when doing things like copying melodyParts.
 * @param pitch the pitch of the returned Note (or Rest if pitch == REST)
 * @param rhythmvalue the duration of the returned Note or Rest
 * @return 
 */

public static Note makeNote(int pitch, int rhythmvalue)
{
    if( pitch == REST )
      {
        return new Rest(rhythmvalue);
      }
    else
      {
        return new Note(pitch, rhythmvalue);
      }
}

public void setVolume(int volume)
  {
    if( volume > 127 )
      {
        this.volume = 127;
      }
    else if( volume < 0 )
      {
        this.volume = 0;
      }
    else this.volume = volume;
  }

public int getVolume()
  {
    return volume;
  }

/**
 * Checks if the current note has the same pitch and duration as another note.
 */
public boolean equalsBasic(Note compare)
  {
  return (compare != null) && samePitch(compare) && (getRhythmValue() == compare.getRhythmValue());
  }

/**
 * Creates a rest with the indicated rhythmValue.
 */
public static Note makeRest(int duration)
  {
  return new Note(REST, Accidental.NOTHING, duration);
  }


/**
 * Sets the Note's rhythm value
 * @param rhythmValue       the Note's rhythmValue to set
 */
public void setRhythmValue(int rhythmValue)
  {
  /*
  if( pitchName != REST && Trace.atLevel(3) )
    {
    System.out.println("setting rhythm value of " + this + " to " + rhythmValue);
    }
   */

  this.rhythmValue = rhythmValue;
  }


/**
 * Sets the Note's rhythm value
 */
public void augmentRhythmValue(int increment)
  {
  /*
  if( pitchName != REST && Trace.atLevel(3) )
    {
    System.out.println("setting rhythm value of " + this + " to " + rhythmValue);
    }
  */

  this.rhythmValue += increment;
  }


/**
 * Return the Note's rhythm value
 * @return int      the Note's rhythmValue
 */
public int getRhythmValue()
  {
  return rhythmValue;
  }

public boolean samePitch(Note other)
{
    return getPitch() == other.getPitch();
}

public boolean adjacentPitch(Note other)
{
    int thisPitch = getPitch();
    int otherPitch = other.getPitch();

    return thisPitch == otherPitch + 1 || thisPitch == otherPitch - 1;
}

/**
 * Sets the Note's accidental based on it's pitchName (in key of C-Major)
 */
public void setAccidentalFromPitch()
  {
  if( pitch < 0 )
    {
    return;
    }
  accidental = black[pitch % 12]
          ? Accidental.SHARP
          : Accidental.NATURAL;
  }


/**
 * Sets the Note's accidental
 * @param acc        the Note's accidental to set
 */
public void setAccidental(Accidental acc)
  {
  accidental = acc;
  }


/**
 * Return the Note's accidental
 * @return Accidental       the Note's accidental
 */
public Accidental getAccidental()
  {
  return accidental;
  }


/**
 * Sets the Note's pitchName.
 * @param pitchName     the pitchName to set
 */
public void setPitch(int pitch)
  {
  this.pitch = pitch;
  }


/**
 * Returns the Note's pitchName.
 * @return int      the Note's pitchName
 */
public int getPitch()
  {
  return pitch;
  }


/**
 * Sets the Note's tied flag (Used for display.)
 * @param tied      a boolean indicating if the Note is tied or not
 */
public void setTie(boolean tied)
  {
  this.tied = tied;
  }


/**
 * Returns the Note's tied flag.
 * @return boolean  the Note's tied flag
 */
public boolean isTied()
  {
  return tied;
  }


static Accidental getSharpOrFlat(boolean value)
  {
  return value
          ? Accidental.SHARP
          : Accidental.FLAT;
  }


/**
 * Gets the closest match Note to a given pitchName, from a polylist of NoteSymbols
 * @param pitch
 * @param tonesPL
 * @return Note  an instance of the 'closest' note in list from the pitchName
 */
public static Note getClosestMatch(int pitch, Polylist tonesPL)
  {
  int originalPitch = pitch;
  //System.out.println("getClosestMatch to " + pitchName + " " + tonesPL);

  if( !tonesPL.nonEmpty() )
    {
    ErrorLog.log(ErrorLog.WARNING, "*** Error: No tones list to match against.");
    }

  Polylist list = tonesPL;
  int[] tones = new int[list.length()];

  /* Make an array of pitches of all acceptable tones to match to */
  for( int i = 0; list.nonEmpty(); i++ )
    {
    try
      {
      tones[i] = ((NoteSymbol)list.first()).getMIDI() % OCTAVE;
      list = list.rest();
      }
    catch( Exception ep )
      {
      //ep.printStackTrace();
      }
    }

  /* Search incrementally within the tones list for the 'closest'
   * acceptable pitchName.  This will fit our contour best.
   */
  int stepSearch = 0;
  int indexMatch;

  while( (indexMatch = arrayContains((pitch % OCTAVE), tones)) == OUT_OF_BOUNDS )
    {
    stepSearch++;
    if( stepSearch % 2 == 0 )
      {
      pitch -= stepSearch;
      }
    else
      {
      pitch += stepSearch;
      }
    }

  Note note = ((NoteSymbol)tonesPL.nth(indexMatch)).toNote();
  note.setPitch(pitch);
  /*
  if( pitchName != originalPitch )
  {
      System.out.println("closest match to " + new Note(originalPitch).toLeadsheet() + " is " + note.toLeadsheet() + " among " + tonesPL);
  }
   */
  return note;
  }


/**
 * Gets the closest match Note at or above a given pitchName, from a polylist of NoteSymbols
 * @param pitch
 * @param tonesPL
 * @param upward
 * @return Note  an instance of the 'closest' note in list from the pitchName
 */
public static Note getClosestMatchDirectional(int pitch, 
                                              Polylist tonesPL,
                                              boolean upward)
  {
  //System.out.println("getClosestMatchDirectional to " + pitchName + " " + tonesPL);
  if( !tonesPL.nonEmpty() )
    {
    ErrorLog.log(ErrorLog.WARNING, "*** Error: No tones list to match against.");
    }

  Polylist list = tonesPL;
  int[] tones = new int[tonesPL.length()];

  /* Make an array of pitches of all acceptable tones to match to */
  for( int i = 0; i < tones.length; i++ )
    {
    try
      {
      tones[i] = ((NoteSymbol)tonesPL.first()).getMIDI() % OCTAVE;
      tonesPL = tonesPL.rest();
      }
    catch( Exception ep )
      {
      //ep.printStackTrace();
      }
    }

  /* Search incrementally within the tones list for the 'closest'
   * acceptable pitchName.  This will fit our contour best.
   */

  int indexMatch;

  if( upward )
    {
    pitch++;
    }
  else
    {
    pitch--;
    }
  while( (indexMatch = arrayContains((pitch % OCTAVE), tones)) == OUT_OF_BOUNDS )
    {
    if( upward )
      {
      pitch++;
      }
    else
      {
      pitch--;
      }
    }

  Note note = ((NoteSymbol)list.nth(indexMatch)).toNote();
  note.setPitch(pitch);
  return note;
  }


/**
 * Basic contains method for an array of integers.
 * Returns the matched index, or -1 if no match.
 */
static int arrayContains(int pitch, int[] pitches)
  {
  for( int i = 0; i < pitches.length; i++ )
    {
    if( pitches[i] == pitch )
      {
      return i;
      }
    }
  return OUT_OF_BOUNDS;
  }

static boolean black[] =
  {
  false, true, false, true, false, false,
  true, false, true, false, true, false
  };


static public boolean isBlack(int pitch)
  {
  // There was a problem with pitchName being negative. It shouldn't happen.
  while( pitch < 0 )
    {
      pitch += OCTAVE;
    }
  return black[pitch % 12];
  }


public boolean isBlack()
  {
  return isBlack(pitch);
  }


public boolean isRest()
  {
  return pitch == REST;
  }

public boolean nonRest()
  {
  return pitch != REST;
  }

private static final Accidental S = Accidental.SHARP;

private static final Accidental F = Accidental.FLAT;

private static final Accidental N = Accidental.NATURAL;

public static final Accidental accidentalByKey[][] =
  {
  {
    N, F, N, F, F, N, F, N, F, N, F, F
    }, /* gb */
  {
    N, F, N, F, F, N, F, N, F, N, F, F
    }, /* db */
  {
    N, F, N, F, F, N, F, N, F, N, F, F
    }, /* ab */
  {
    N, F, N, F, F, N, F, N, F, N, F, F
    }, /* eb */
  {
    N, F, N, F, F, N, F, N, F, N, F, F
    }, /* bb */
  {
    N, F, N, F, N, N, F, N, F, N, F, F
    }, /* f  */
  {
    N, F, N, F, N, N, F, N, F, N, F, N
    }, /* c  */
  {
    N, S, N, S, N, S, S, N, S, N, S, N
    }, /* g  */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    }, /* d  */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    }, /* a  */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    }, /* e  */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    }, /* b  */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    }, /* f# */
  {
    S, S, N, S, N, S, S, N, S, N, S, N
    } /* c# */

  };


/**
 * Shifts the pitchName up by the amount specified, adjusting the accidental
 * if necessary.
 * @param shift     the amount to shift the pitchName
 * @param keySig    the key signature (to prefer flats or sharps)
 */
public void shiftPitch(int shift, int keySig)
  {
//System.out.println("shiftPitch " + shift + " " + this);
  if( pitch == REST )
    {
    return;
    }

  pitch = shift + pitch;

  if( shift % OCTAVE == 0 )
    {
    return;	// leave octave accidentals alone
    }

  if( keySig == 0 )
    {
    accidental =
            isBlack(pitch)
            ? getSharpOrFlat(shift > 0)
            : Accidental.NATURAL;
    }
  else
    {
    accidental = accidentalByKey[keySig + 6][pitch % 12];
    }
//System.out.println("new Pitch " + shift + " " + this);
  }


/**
 * Sets the Note's firstTie flag (Used for display.)
 * @param firstTie      a boolean indicating if the Note is the first \
 *                      tied or not
 */
public void setFirstTie(boolean firstTie)
  {
  this.firstTie = firstTie;
  }


/**
 * Returns the Note's firstTie flag.
 * @return boolean  the Note's firstTie flag
 */
public boolean firstTied()
  {
  return firstTie;
  }


/**
 * Toggles the enharmonic of the Note.
 * @return boolean  true if the enharmonic was toggled, false if the 
 *                  Note is set as NATURAL or NOTHING
 */
public boolean toggleEnharmonic()
  {
  if( pitch % SEMITONES == MODF && accidental == Accidental.SHARP ||
          pitch % SEMITONES == MODE && accidental == Accidental.FLAT ||
          pitch % SEMITONES == MODB && accidental == Accidental.FLAT ||
          pitch % SEMITONES == MODC && accidental == Accidental.SHARP )
    {
    accidental = Accidental.NATURAL;
    return true;
    }
  else if( accidental == Accidental.SHARP ||
          accidental == Accidental.NATURAL && (pitch % SEMITONES == MODE ||
          pitch % SEMITONES == MODB) )
    {
    accidental = Accidental.FLAT;
    return true;
    }
  else if( accidental == Accidental.FLAT ||
          accidental == Accidental.NATURAL && (pitch % SEMITONES == MODF ||
          pitch % SEMITONES == MODC) )
    {
    accidental = Accidental.SHARP;
    return true;
    }
  return false;
  }


public void setEnharmonic(boolean[] enh)
  {
  setAccidentalFromPitch();

  if( accidental != Accidental.NATURAL )
    {
    String pitchName = getPitchClassName();

    switch( pitchName.charAt(0) )
      {
      case 'c':
        if( accidental == Accidental.SHARP && enh[CSHARP] == false )
          {
          toggleEnharmonic();
          }
        break;
      case 'd':
        if( accidental == Accidental.FLAT && enh[CSHARP] == true )
          {
          toggleEnharmonic();
          }
        else if( accidental == Accidental.SHARP && enh[DSHARP] == false )
          {
          toggleEnharmonic();
          }
        break;
      case 'e':
        if( accidental == Accidental.FLAT && enh[DSHARP] == true )
          {
          toggleEnharmonic();
          }
        break;
      case 'f':
        if( accidental == Accidental.SHARP && enh[FSHARP] == false )
          {
          toggleEnharmonic();
          }
        break;
      case 'g':
        if( accidental == Accidental.SHARP && enh[GSHARP] == false )
          {
          toggleEnharmonic();
          }
        else if( accidental == Accidental.FLAT && enh[FSHARP] == true )
          {
          toggleEnharmonic();
          }
        break;
      case 'a':
        if( accidental == Accidental.SHARP && enh[ASHARP] == false )
          {
          toggleEnharmonic();
          }
        else if( accidental == Accidental.FLAT && enh[GSHARP] == true )
          {
          toggleEnharmonic();
          }
        break;
      case 'b':
        if( accidental == Accidental.FLAT && enh[ASHARP] == true )
          {
          toggleEnharmonic();
          }
        break;
      }
    }
  }


/**
 * Returns a copy of the Note
 * @return Note     a copy of the Note
 */
public Note copy()
  {
  Note newNote = new Note(pitch, accidental, rhythmValue);
  newNote.drawnPitch = drawnPitch;
  newNote.tied = tied;
  newNote.firstTie = firstTie;
  newNote.volume = volume;
  return newNote;
  }

/**
 * Converts a note, given the chord the note is over, to a relative pitch
 * @param chord the chord the note is played over
 * @return Polylist a polylist representing the corresponding relative pitch
 */
public Polylist toRelativePitch(Chord chord) {
    return imp.lickgen.NoteConverter.noteToRelativePitch(this, chord);
}

/**
 * Returns a String representation of the Note
 * @return String   the String representation of the Note
 */

@Override
public String toString()
  {
  if( pitch == REST )
    {
      return "REST: [RhythmValue = " + rhythmValue + "]";
    }
  
  return "NOTE: [" + getPitchClassName() + ", Pitch = " + pitch +
          ", " + drawnPitch + "][Accidental = " + accidental +
          "][RhythmValue = " + rhythmValue + "][volume = " + volume + "]";
  }


/**
 * Writes the Note to the passed BufferedWriter.
 * @param out       the BufferedWriter to write the Note to
 */
public void save(BufferedWriter out) throws IOException
  {
  out.write(accidental.toString() + ' ' + pitch + ' ' +
          rhythmValue);
  out.newLine();
  }


/**
 * Writes the Note to the passed BufferedWriter in Leadsheet format.
 * @param out       the BufferedWriter to write the Note to
 * @param metre
 * @throws java.io.IOException
 */
public void saveLeadsheet(BufferedWriter out, int[] metre) throws IOException
  {
  saveLeadsheet(out, metre, true); // use linebreaks
  }


public void saveLeadsheet(BufferedWriter out, int[] metre, boolean lineBreaks)
        throws IOException
  {
  String outString = toLeadsheet();

  Trace.log(3, "saving note to leadsheet: " + outString);

  // Format for a maximum rhythm value per line.

  if( lineBreaks && accumulatedRhythmValue >= maxRhythmValuePerLine )
    {
    out.newLine();
    out.newLine();
    accumulatedRhythmValue -= maxRhythmValuePerLine;
    }

  out.write(" ");
  out.write(outString);
  accumulatedRhythmValue += rhythmValue;
  }


/**
 * Initialize the counter of rhythm value per line.
 */
public static void initializeSaveLeadsheet()
  {
  accumulatedRhythmValue = 0;
  }

/** 
 * Table used to give names of notes in the case that the accidental is flat.
 */
static String flatPitchFromMidi[] =
  {
  "c", "db", "d", "eb", "fb", "f", "gb", "g", "ab", "a", "bb", "cb"
  };

/** 
 * Table used to give names of notes in the case that the accidental is sharp.
 */
static String sharpPitchFromMidi[] =
  {
  "b#", "c#", "d", "d#", "e", "e#", "f#", "g", "g#", "a", "a#", "b"
  };

/** 
 * Table used to give names of notes in the case that there is no accidental.
 */
static String naturalPitchFromMidi[] =
  {
  "c", "c", "d", "d", "e", "f", "f", "g", "g", "a", "a", "b"
  };

/** 
 * Table used by method isAccidentalInKeyto determine which pitches
 * are accidental in sharp keys.
 */
static int sharpInKeysAbove[] =
  {
  6, 1, 9, 3, 5, 9, 0, 9, 2, 9, 4, 9
  };	// 9 is like infinity
//b# c# d  d# e# f  f# g  g# a  a# b

/** 
 * Table used by method isAccidentalInKeyto determine which pitches
 * are accidental in flat keys.
 */
static int flatInKeysBelow[] =
  {
  9, 4, 9, 2, 7, 9, 5, 9, 3, 9, 1, 6
  };	// 9 is like infinity
//c  db d  eb fb f  gb g  ab a  bb cb


/**
 * Determine whether this note would be accidental in the indicated
 * key signature.
 * @param keySig
 * @return 
 */
public boolean isAccidentalInKey(int keySig)
  {
  if( pitch >= 0 )
    {
    if( keySig > 0 )
      {
      return accidental == Accidental.FLAT //           || accidental == Accidental.NATURAL)  // See if this can be fixed.
              && keySig > sharpInKeysAbove[pitch % 12];

      }
    if( keySig < 0 )
      {
      return accidental == Accidental.SHARP //           || accidental == Accidental.NATURAL) // See if this can be fixed.
              && keySig <= -flatInKeysBelow[pitch % 12];
      }
    }

  return false;
  }


/**
 * Get name of the PitchClass of this note
 * @return 
 */
public String getPitchClassName()
  {
  if( pitch == REST )
    {
    return "r";
    }

  int pitch_within_octave = pitch % OCTAVE;

  StringBuilder buffer = new StringBuilder();

  if( null != accidental )
    switch( accidental )
      {
        case SHARP:
            buffer.append(sharpPitchFromMidi[pitch_within_octave]);
            break;
        case FLAT:
            buffer.append(flatPitchFromMidi[pitch_within_octave]);
            break;
        default:
            buffer.append(naturalPitchFromMidi[pitch_within_octave]);
            break;
      }
  return buffer.toString();
  }


/**
 * Sets the pitchName of the note used to draw the part
 * @param p
 */
public void setDrawnPitch(int p)
  {
  drawnPitch = p;
  }


/**
 * Gets the the drawn pitchName
 * @return 
 */
public int getDrawnPitch()
  {
  if( drawnPitch == UNDEFINED )
    {
    //ErrorLog.log(ErrorLog.WARNING, "*** Error: Trying to draw a note before determining how it" +
    //        " should appear. Printing rest.");
    drawnPitch = REST;
    }

  if( isRest() )
    {
    drawnPitch = REST;
    }

  return drawnPitch;
  }


/**
 * Tell if drawn as a rest.
 * @return 
 */
public boolean isDrawnRest()
  {
  return getDrawnPitch() == REST;
  }


/**
 * Creates a String representing a note in Leadsheet notation.
 * @return 
 */
public String toLeadsheet()
  {
  StringBuilder buffer0 = new StringBuilder();

  int octave = pitch / 12 - 5;

  if( pitch < 0 )
    {
    buffer0.append("r");	// handle rest
    }
  else
    {
    buffer0.append(getPitchClassName());

    if( octave > 0 )
      {
      while( octave > 0 )
        {
        buffer0.append("+");
        octave--;
        }
      }
    else if( octave < 0 )
      {
      while( octave < 0 )
        {
        buffer0.append("-");
        octave++;
        }
      }
    }

  StringBuilder buffer = new StringBuilder();

  int value = getDurationString(buffer, rhythmValue);

  // We don't handle less than 32nd note triplets currently.  For example,
  // 480 is not divisible by 64. However, we could handle 64th note triplets.
  if( value > 0 )
    {
    /* Annoying: eliminate for now
    ErrorLog.log(ErrorLog.WARNING,
    "There is residual note value of " + value + " in rendering a note "
    + buffer0 + " of duration " + rhythmValue);
     */
    }
  if( buffer.length() > 1 )
    {
    buffer0.append(buffer.substring(1));	// discard initial +
    }
  return buffer0.toString();
  }

public static String getDurationString(int value)
{
  if( value <= 0 )
    {
        return "";
    }
  StringBuilder buffer = new StringBuilder();
  getDurationString(buffer, value);
  // note that return value above should be 0;
  return buffer.toString().substring(1);
}

public static int getDurationString(StringBuilder bufferOut, int value)
  {
  // Note: The first '+' in buffer will eventually be discarded.
  // Try decomposing in two different orders, then pick the shorter description
  StringBuffer buffer = new StringBuffer();
  int saved_value = value;

  // First decomposition

  value = accumulateValue(WHOLE, value, buffer, "+1");
  value = accumulateValue(HALF, value, buffer, "+2");
  value = accumulateValue(QUARTER, value, buffer, "+4");
  value = accumulateExactValue(QUARTER_QUINTUPLET, value, buffer, "+4/5");
  value = accumulateValue(EIGHTH, value, buffer, "+8");
  value = accumulateExactValue(EIGHTH_QUINTUPLET, value, buffer, "+8/5");
  value = accumulateValue(SIXTEENTH, value, buffer, "+16");
  value = accumulateExactValue(SIXTEENTH_QUINTUPLET, value, buffer, "+16/5");
  value = accumulateValue(THIRTYSECOND, value, buffer, "+32");
  value = accumulateExactValue(THIRTYSECOND_QUINTUPLET, value, buffer, "+32/5");
  value = accumulateValue(HALF_TRIPLET, value, buffer, "+2/3");
  value = accumulateValue(QUARTER_TRIPLET, value, buffer, "+4/3");
  value = accumulateValue(EIGHTH_TRIPLET, value, buffer, "+8/3");
  value = accumulateValue(SIXTEENTH_TRIPLET, value, buffer, "+16/3");
  value = accumulateValue(THIRTYSECOND_TRIPLET, value, buffer, "+32/3");

  // To make any residual concise
  value = accumulateValue(SIXTIETH, value, buffer, "+60");
  value = accumulateValue(ONETWENTIETH, value, buffer, "+120");
  value = accumulateValue(TWOFORTIETH, value, buffer, "+240");
  value = accumulateValue(FOUREIGHTIETH, value, buffer, "+480");
  int residue1 = value;
  String string1 = buffer.toString();

  //Second decomposition
  buffer = new StringBuffer();
  value = saved_value;

  value = accumulateValue(HALF_TRIPLET, value, buffer, "+2/3");
  value = accumulateValue(QUARTER_TRIPLET, value, buffer, "+4/3");
  value = accumulateValue(EIGHTH_TRIPLET, value, buffer, "+8/3");
  value = accumulateValue(SIXTEENTH_TRIPLET, value, buffer, "+16/3");
  value = accumulateValue(THIRTYSECOND_TRIPLET, value, buffer, "+32/3");
  value = accumulateValue(WHOLE, value, buffer, "+1");
  value = accumulateValue(HALF, value, buffer, "+2");
  value = accumulateValue(QUARTER, value, buffer, "+4");
  value = accumulateValue(EIGHTH, value, buffer, "+8");
  value = accumulateValue(SIXTEENTH, value, buffer, "+16");
  value = accumulateValue(THIRTYSECOND, value, buffer, "+32");

  // To make any residual concise
  value = accumulateValue(SIXTIETH, value, buffer, "+60");
  value = accumulateValue(ONETWENTIETH, value, buffer, "+120");
  value = accumulateValue(TWOFORTIETH, value, buffer, "+240");
  value = accumulateValue(FOUREIGHTIETH, value, buffer, "+480");
  int residue2 = value;
  String string2 = buffer.toString();

  int choice;  // This is the choice of which decomposition to use.

  // Both residues are 0, so make the choice based on length
  if( string1.length() <= string2.length() )
    {
    choice = 1;
    }
  else
    {
    choice = 2;
    }

  switch( choice )
    {
    default:
    case 1:
      bufferOut.append(string1);
      return residue1;

    case 2:
      bufferOut.append(string2);
      return residue2;
    }
  }


static int accumulateValue(int value, int duration, StringBuffer buffer,
                           String string)
  {
  while( duration >= value )
    {
    buffer.append(string);
    duration -= value;
    }
  return duration;
  }


static int accumulateExactValue(int value, int duration, StringBuffer buffer,
                                String string)
  {
  if( duration % value == 0 )
    {
    return accumulateValue(value, duration, buffer, string);
    }
  return duration;
  }


/**
 * Adds this Note at the specified time on the specified Track and
 * channel in the specified Sequence, then returns the time that a
 * sequential Note should be added.
 * @param seq           the Sequence to which to add this Note
 * @param track         the Track in the Sequence to which to add this Note
 * @param time          the time at which to start this Note
 * @param ch            the channel on which to put this Note 
 * @param transposition amount by which to transpose this note in semitones
 * @param sendBankSelect
 * @return 
 * @throws javax.sound.midi.InvalidMidiDataException 
 */

public long render(Sequence seq, 
                   Track track, 
                   long time, 
                   int ch, 
                   int transposition,
                   boolean sendBankSelect)
        throws InvalidMidiDataException
  {
    if( sendBankSelect )
      {
        
      }
    int dur = getRhythmValue();
    long offTime = time + dur * seq.getResolution() / BEAT;
    render(seq, track, time, offTime, ch, transposition);
    
    return offTime;
  }

/**
 * Adds this Note at the specified time on the specified Track and
 * channel in the specified Sequence, then returns the time that a
 * sequential Note should be added.
 * @param seq       the Sequence to which to add this Note
 * @param track     the Track in the Sequence to which to add this Note
 * @param time      the time at which to start this Note
 * @param offTime   the time at which to end this Note
 * @param ch        the channel on which to put this Note 
 * @param transposition amount by which to transpose this note in semitones
 * @throws javax.sound.midi.InvalidMidiDataException
 */

public void render(Sequence seq, 
                   Track track, 
                   long time, 
                   long offTime,
                   int ch,
                   int transposition)
        throws InvalidMidiDataException
  {

  // To trace rendering info:
//  Trace.log(2, "\nchannel = " + ch 
//             + " track = " + track 
//             + " beat = " + time/480.0 
//             + " pitch = " + pitch
//             + " rhythmValue = " + rhythmValue);
  
  if( pitch == REST )
    {
    // if this Note is a rest, do nothing

      return;
    }

  int actualPitch = pitch + transposition;

  // Prevent exceptions in synth

  if( actualPitch > MAX_PITCH )
    {
    actualPitch = MAX_PITCH;
    }
  else if( actualPitch < MIN_PITCH )
    {
    actualPitch = MIN_PITCH;
    }

  // create a note on event at the current time
  MidiEvent evt = MidiSynth.createNoteOnEvent(ch, actualPitch, volume, time);
  track.add(evt);
  //Trace.log(0, " channel = " + ch + " time = " + time + " note on " + " pitch = " + actualPitch + " velocity = " + volume);

  // advance the time and call the note off event

  evt = MidiSynth.createNoteOffEvent(ch, actualPitch, volume, offTime);
  track.add(evt);
  // Trace.log(0, " channel = " + ch + " offTime = " + time + " note off " + " pitch = " + actualPitch + " velocity = " + volume);
  }

}
