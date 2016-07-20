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

package imp.data;

import imp.Constants;
import java.io.Serializable;

/**
 * The PitchClass class deals with immutable pitch classes, which are like pitches but
 * without a specific octave.
 *
 * @see         Key
 * @see         Note
 * @author      Robert Keller
 */

public class PitchClass implements Constants, Serializable
{
/**
 * The pitch class is uniquely represented by its index.
 */

private int index;


/**
 * The pitch class has a name.
 */

private String name;


/**
 * the number of semitones above C
 */

private int semitonesAboveC;


/**
 * whether the PC is natural (has no sharp or flat)
 */

private boolean natural;


/**
 * If the PC is not natural, then whether it is sharp or flat.
 */

private boolean sharp;


/**
 * Whether sharp accidentals are preferred in the corresponding major scale.
 */

private boolean sharpPreference;


/**
 * Chord base corresponding to PC
 */

private String chordBase;

/**
 * Private constructor
 */

private PitchClass(String name, 
                   int index, 
                   int semitonesAboveC, 
                   boolean natural, 
                   boolean sharp, 
                   boolean sharpPreference, 
                   String chordBase)
  {
  this.index           = index;
  this.name            = name;
  this.semitonesAboveC = semitonesAboveC;
  this.natural         = natural;
  this.sharp           = sharp;
  this.sharpPreference = sharpPreference;
  this.chordBase       = chordBase;
  }


public static final PitchClass cClass = new PitchClass("c",   8,  0,  true, false, false, "C");

/**
 * Array of the only PitchClasses to be used.
 */

public static PitchClass pitchClass[] = 
                              {
			      new PitchClass("fb",  0,  4, false, false, false, "E"),
			      new PitchClass("cb",  1, 11, false, false, false, "B"),
			      new PitchClass("gb",  2,  6, false, false, false, "Gb"),
			      new PitchClass("db",  3,  1, false, false, false, "Db"),
			      new PitchClass("ab",  4,  8, false, false, false, "Ab"),
			      new PitchClass("eb",  5,  3, false, false, false, "Eb"),
			      new PitchClass("bb",  6, 10, false, false, false, "Bb"),
			      new PitchClass("f",   7,  5,  true, false, false, "F"),
			      cClass,
			      new PitchClass("g",   9,  7,  true, false, true,  "G"), 
			      new PitchClass("d",  10,  2,  true, false, true,  "D"),
			      new PitchClass("a",  11,  9,  true, false, true,  "A"),
			      new PitchClass("e",  12,  4,  true, false, true,  "E"),
			      new PitchClass("b",  13, 11,  true, false, true,  "B"),
			      new PitchClass("f#", 14,  6, false, true,  true,  "F#"),
			      new PitchClass("c#", 15,  1, false, true,  true,  "C#"),
			      new PitchClass("g#", 16,  8, false, true,  false, "Ab"),
			      new PitchClass("d#", 17,  3, false, true,  false, "Eb"),
			      new PitchClass("a#", 18, 10, false, true,  false, "Bb"),
			      new PitchClass("e#", 19,  5, false, true,  false, "F"),
			      new PitchClass("b#", 20,  0, false, true,  false, "C")
			      };

/**
 * Constants used in note rendering
 */

static public final int 
    fb = 12,  // use e rather than fb
    cb = 13,  // use b rather than cb
    gb = 2,
    db = 3,
    ab = 4,
    eb = 5,
    bb = 6,
    f  = 7,
    c  = 8,
    g  = 9,
    d  = 10,
    a = 11,
    e = 12,
    b = 13,
    fs = 14,
    cs = 15,
    gs = 16,
    ds = 5,   // use bb rather than d#
    as = 6,   // use bb rather than a#
    es = 7,   // use f rather than e#
    bs = 8;   // use c rather than b#

/* original version
static public final int 
    fb = 0,
    cb = 1,
    gb = 2,
    db = 3,
    ab = 4,
    eb = 5,
    bb = 6,
    f  = 7,
    c  = 8,
    g  = 9,
    d  = 10,
    a = 11,
    e = 12,
    b = 13,
    fs = 14,
    cs = 15,
    gs = 16,
    ds = 17,
    as = 18,
    es = 19,
    bs = 20;
 */


/*
static public final int flatPC[]  = {  c, db, d, eb, fb,  f, gb, g, ab, a, bb, cb};
static public final int sharpPC[] = { bs, cs, d, ds,  e, es, fs, g, gs, a, as,  b};
*/

static public final int indexFromSemitones[] = 
  {  c, db, d, eb, e,  f, gb, g, ab, a, bb, b, c, cs, d, ds,  e, f, fs, g, gs, a, as,  b, bs };
//{  c, db, d, eb, fb,  f, gb, g, ab, a, bb, cb, c, cs, d, ds,  e, es, fs, g, gs, a, as,  b, bs};

// In the table below, columns are tonalities, while rows correspond to transpositions of
// the tonality by some number of semitones (0-11).  This is an attempt to get sharps and
// flats to be coordinated when transposing.  We don't want to see sharps and flats mixed
// in the same column.

static public int[][] upTranspositions = 
  {
  /*  0 */ {fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es, bs},
  /*  1 */ { f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d,  a,  e,  b, fs, cs},
  /*  2 */ {fs, cs, gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d},
  /*  3 */ { g,  d,  a,  e,  b, fs, cs, gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds},
  /*  4 */ {gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d,  a,  e},
  /*  5 */ { a,  e,  b, fs, cs, gs, ds, as, es,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es},
  /*  6 */ {as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as,  es, bs,  g,  d,  a,  e,  b, fs},
  /*  7 */ { b, fs, cs, gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g},
  /*  8 */ { c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d,  a,  e,  b, fs, cs, gs},
  /*  9 */ {cs, gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d,  a},
  /* 10 */ { d,  a,  e,  b, fs, cs, gs, ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as},
  /* 11 */ {ds, as,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es,  bs,  g,  d,  a,  e,  b}
  };

static public int[][] downTranspositions = 
  {
  /*  0 */ {fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, fs, cs, gs, ds, as, es, bs},
  /*  1 */ {eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b},
  /*  2 */ { d,  a, fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb},
  /*  3 */ {db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f,  c,  g,  d,  a},
  /*  4 */ { c,  g,  d,  a, fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab},
  /*  5 */ {cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f,  c,  g},
  /*  6 */ {bb,  f,  c,  g,  d,  a,  e, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb},
  /*  7 */ { a, fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f},
  /*  8 */ {ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  f},
  /*  9 */ { g,  d,  a, fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb},
  /* 10 */ {gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db, ab, eb, bb,  f,  c,  g,  d},
  /* 11 */ { f,  c,  g,  d,  a, fb, cb, gb, db, ab, eb, bb,  f,  c,  g,  d,  a,  e,  b, gb, db}
  };


static private int BOTTOM_PITCH = 0;
static private int TOP_PITCH = 20;
static private int PITCH_NAME_SIZE = 21;


/**
 * Numbers used for key-independent pitches (e.g. in cell display).
 */

public static String numbers[] = {
				 "3", 
				 "7", 
				 "b5",
				 "b2",
				 "b6",
				 "b3",
				 "b7",
				 "4", 
				 "1", 
				 "5", 
				 "2", 
				 "6", 
				 "3", 
				 "7", 
				 "#4",
				 "#1",
				 "#5",
				 "#2",
				 "#6",
				 "4", 
				 "1"  
				  };


/**
 * Make an upper-case version of a note.  Only the first character
 * becomes upper-case, not the sharp or flat modifier.
 */

public static String upperCaseNote(String s)
  {
  StringBuffer buffer = new StringBuffer();

  int len = s.length();

  if( len > 0 )
    {
    buffer.append(Character.toUpperCase(s.charAt(0)));
    }
  if( len > 1 )
    {
    buffer.append(s.charAt(1));
    }

  return buffer.toString();
  }


/**
 * Tell whether a string is the name of a PitchClass.
 */

public static boolean isValidPitch(String s)
  {
  return PitchClass.getPitchClass(s) != null;
  }


/**
 * Tell whether a character can start a pitch class
 */

public static boolean isValidPitchStart(char c)
  {
  switch( c )
    {
    case 'a':
    case 'b':
    case 'c':
    case 'd':
    case 'e':
    case 'f':
    case 'g':
      		return true;
    default:
      		return false;
    }
  }


/**
 * Get a PitchClass given its name.
 */

public static PitchClass getPitchClass(String name)
  {
  if( name.length() == 0 )
    {
    return null;
    }

  String lcName = name.toLowerCase();
  
  int index = -1;

  switch( lcName.charAt(0) )
    {
    case 'f': index =  7; break;
    case 'c': index =  8; break;
    case 'g': index =  9; break;
    case 'd': index = 10; break;
    case 'a': index = 11; break;
    case 'e': index = 12; break;
    case 'b': index = 13; break;
    }

  if( lcName.length() > 1 )
    {
    switch( lcName.charAt(1) )
      {
      case 'b': index -= 7; break;
      case '#': index += 7; break;
      }
    }

  if( index < 0 )
    {
    System.out.println(" pitchclass " + name + " not found, returning null");
    return null;	// no such PitchClass
    }

  return pitchClass[index];
  }


/**
 * Get a PitchClass given its index
 */

public static PitchClass getPitchClass(int index)
  {
  assert( index >= BOTTOM_PITCH && index <= TOP_PITCH );
  return pitchClass[index];
  }


/**
 * Get a representative PitchClass given the midi note number.
 * The PitchClass of a midi note is not unique, so we choose
 * in the range gb to b on the line of fifths
 */

public static PitchClass getPitchClassFromMidi(int midi)
  {
  // FIX: midi should not be < 0, but we are somehow getting some out of range.
  while( midi < 0 )
    {
    midi += OCTAVE;
    }
  return pitchClass[8+((midi*7)%OCTAVE)];
  }


/**
 * Get the index of this PitchClass in the line of fifths.
 */

public int getIndex()
  {
  return index;
  }


/**
 * Get the number of Semitones above C.
 */

public int getSemitones()
  {
  return semitonesAboveC;
  }


/**
 * Get whether the pitch is natural (no sharp or flat).
 */

public boolean getNatural()
  {
  return natural;
  }


/**
 * If the pitch is not natural, returns true if sharp, false if flat.
 */

public boolean getSharp()
  {
  return sharp;
  }


/**
 * Get the chord base corresponding to this PitchClass.
 */

public String getChordBase()
  {
  return chordBase;
  }


/**
 * Get the name of this PitchClass.
 */

public String toString()
  {
  return name;
  }



/**
 * Find delta in line of fifths between two pitches in table.
 * (This might not be needed given findRise below.)
 * The pitches are specified by strings which should be names of pitches.
 */

public static int findDelta(String from, String to)
  {
  PitchClass fromClass = getPitchClass(from);
  PitchClass   toClass = getPitchClass(to);
  assert( fromClass != null );
  assert( toClass != null );

  return toClass.getIndex() - fromClass.getIndex();
  }


/**
 * Transpose a PitchClass up or down by some number of semitones.
 */

public static PitchClass transpose(PitchClass pc, int semitones)
  {
  return pc.transpose(semitones);
  }


/**
 * Transpose this PitchClass up or down by some number of semitones.
 */

public PitchClass transpose(int semitones)
  {
  int newIndex;
  if( semitones >= 0 )
    {
    semitones = semitones%12;
    if( semitones == 0 )
      {
      return this;
      }
    newIndex = upTranspositions[semitones][index];
    }
  else
    {
    semitones = (-semitones)%12;
    if( semitones == 0 )
      {
      return this;
      }
    newIndex = downTranspositions[semitones][index];
    }

  PitchClass newPC = pitchClass[newIndex];
//System.out.println("transposing index " + index + " " + this + " by " + semitones + " newIndex = " + newIndex + " giving " + newPC);
//System.out.println("transposing index " + index + " " + semitonesAboveC + " by " + semitones + " newIndex = " + newIndex + " giving " + newPC.semitonesAboveC);

  return newPC;
  }


/**
 * Find rise in semitones between two pitches in table, for purpose of 
 * transposing other pitches by the same interval as between the given two pitches.
 * The pitches are specified by strings which should be names of pitches.
 */

/**
 * Find rise in semitones between two pitches in table, for purpose of 
 * transposing other pitches by the same interval as between the given two pitches.
 * The pitches are specified by strings which should be names of pitches.
 */

public static int findRise(String from, String to)
  {
  PitchClass fromClass = getPitchClass(from);
  PitchClass   toClass = getPitchClass(to);
  assert( fromClass != null );
  assert( toClass != null );
 
  return findRise(fromClass, toClass);
  }

public static int findRise(PitchClass fromClass, PitchClass toClass)
  {
  int rise = toClass.getSemitones() - fromClass.getSemitones();

  if( rise >= OCTAVE )
    {
    rise = rise%OCTAVE;
    }
  else if( rise < 0 )
    {
    rise = OCTAVE - ((-rise)%OCTAVE);
    }

  // By making the rise negative, transposition will take this to a scale preferring flats

  if( rise > 0 && !toClass.sharpPreference )
    {
    rise = - (OCTAVE - rise);
    }
  else if( rise < 0 && toClass.sharpPreference )
    {
    rise = - (OCTAVE + rise);
    }

  //System.out.println("rise from " + from + " to " + to + " = " + rise);
  return rise;
  }


/**
 * Find rise in semitones between C and a pitch in table, for purpose of 
 * transposing other pitches by the same interval.
 * The pitches is specified by a String in this version.
 */

public static int findRise(PitchClass toClass)
  {
  return findRise(cClass, toClass);
  }

/**
 * Find rise in semitones between C and a pitch in table, for purpose of 
 * transposing other pitches by the same interval.
 * The pitches is specified by a String in this version.
 */

public static int findRise(String to)
  {
  PitchClass toClass = getPitchClass(to);
  assert( toClass != null );
  return findRise(toClass);
  }


/**
 * Find rise in semitones between a pitch and C in table, for purpose of 
 * transposing other pitches by the same interval.
 * The pitches is specified by a String in this version.
 */

public static int findRiseToC(PitchClass fromClass)
  {
  return findRise(fromClass, cClass);
  }

/**
 * Find rise in semitones between C and a pitch in table, for purpose of 
 * transposing other pitches by the same interval.
 * The pitches is specified by a String in this version.
 */

public static int findRiseToC(String from)
  {
  PitchClass fromClass = getPitchClass(from);
  assert( fromClass != null );
  return findRiseToC(fromClass);
  }


/**
 * enharmonic determines whether this pitch classes
 * is enharmonically equivalent to the argumen
 */

public boolean enharmonic(PitchClass other)
  {
  return semitonesAboveC == other.semitonesAboveC;
  }


/**
 * enharmonic determines whether this pitch classes
 * is enharmonically equivalent to an index
 */

public boolean enharmonic(int index)
  {
  return semitonesAboveC%12 == index%12;
  }



/**
 * enharmonic determines whether the pitches represented by
 * two strings representing pitch are enharmnoically equivalent
 */

public static boolean enharmonic(String x, String y)
  {
  return getPitchClass(x).enharmonic(getPitchClass(y));
  }


/**
 * Make a note from a PitchClass name.  The specific pitch for the note
 * is found by using the midiBase argument as C that beings the octave in
 * which the desired note occurs.
 *
 * If there is a problem with the PitchClass name, null is returned.
 */

public static Note makeNote(PitchClass pc, int midiBase, int duration)
  {
  return makeNoteAbove(pc, midiBase, 0, duration);
  }

public static Note makeNote(String pitchClassName, int midiBase, int duration)
  {
  return makeNoteAbove(pitchClassName, midiBase, 0, duration);
  }


/**
 * Make a note from a pitch class specified as one of the Strings
 * in the pitches table, above a minimum MIDI value.
 * A PitchClass name represents many
 * individual pitches.  The specific pitch is found by
 * using the midiBase argument as C that beings the octave in
 * which the desired note occurs.  However, this octave can be
 * modified by specifying a non-zero minimum.  This is used in
 * arpeggios, for example, to guarantee the notes keep ascending,
 * by specifying the previous midi value.
 *
 * If there is a problem with the PitchClassName, null is returned.
 */

public static Note makeNoteAbove(String pitchClassName, int midiBase, int minimum, int duration)
  {
  PitchClass pc = getPitchClass(pitchClassName);

  return makeNoteAbove(pc, midiBase, minimum, duration);
  }

public static Note makeNoteAbove(PitchClass pc, int midiBase, int minimum, int duration)
  {
  int midi = midiBase + pc.getSemitones();

  while( midi < minimum )
    {
    midi += OCTAVE;
    }

  boolean natural = pc.getNatural();

  boolean sharp = pc.getSharp();

  return new Note(midi, natural, sharp, duration);  
  }

    /**
     * Returns the Roman numeral equivalent of the pitch class as a String.
     */
    public static String romanNumerals[] = {"bIV", "bI", "bV", "bII", "bVI", "bIII", "bVII", "IV", "I", "V", "II", "VI", "III", "VII", "#IV", "#I", "#V", "#II", "#VI", "#III", "#VII"};

    public String keyToRomanNumeral(PitchClass homeKey) {
        int index = transpose(findRiseToC(homeKey)).getIndex();
        return romanNumerals[index];
    }

    public static String keyToRomanNumeral(PitchClass currentKey, PitchClass homeKey) {
        return currentKey.keyToRomanNumeral(homeKey);
    }

    public static String keyToRomanNumeral(String currentKeyName, PitchClass homeKey) {
        return keyToRomanNumeral(getPitchClass(currentKeyName), homeKey);
    }

}

