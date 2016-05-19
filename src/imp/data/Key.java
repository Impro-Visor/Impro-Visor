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

import imp.data.advice.Advisor;
import imp.Constants;
import imp.util.ErrorLog;
import java.io.Serializable;
import polya.Polylist;

/**
 * The Key class deals with keys, their creation from strings, transposition, etc.
 * Keys are immutable objects and will be created only at initialization.
 *
 * @see         Note
 * @see         Part
 * @author      Robert Keller
 */
public class Key
        implements Constants, Serializable
  {
  /**
   * The key is uniquely represented by its index.
   */
  private int index;

  /**
   * Each key has a unique name as well.
   */
  private String name;

  /**
   * The position of c (or b#) in this key
   */
  private int cPosition;

  public static int default_numerator = 8;	// eighth note default

  public static final int gbkey = 0, dbkey = 1, abkey = 2,  ebkey = 3, bbkey = 4,  
                          fkey = 5,  ckey = 6,  gkey = 7,   dkey = 8,  akey = 9,  
                          ekey = 10, bkey = 11, fskey = 12, cskey = 13;

  private static int CINDEX = 6;

  private static int CINDEX_OFFSET = 2;	// Difference between two tables

  /**
   * table of all Keys organized as a line of fifths
   */
  public static Key key[] = {new Key(0, "gb", 6),
                               new Key(1, "db", 11),
                               new Key(2, "ab", 4),
                               new Key(3, "eb", 9),
                               new Key(4, "bb", 2),
                               new Key(5, "f", 7),
                               new Key(6, "c", 0),
                               new Key(7, "g", 5),
                               new Key(8, "d", 10),
                               new Key(9, "a", 3),
                               new Key(10, "e", 8),
                               new Key(11, "b", 1),
                               new Key(12, "f#", 6),
                               new Key(13, "c#", 11)
  };

  public static Key Ckey = key[CINDEX];

  /**
   * names for PitchClass indices
   */
  static public final int fb = 0,  cb = 1,  gb = 2,  db = 3,  ab = 4,  eb = 5,  
          bb = 6,  f = 7,  c = 8,  g = 9,  d = 10,  a = 11,  e = 12,  b = 13,  
          fs = 14,  cs = 15,  gs = 16,  ds = 17,  as = 18,  es = 19,  bs = 20;


  /* scales corresponding to keys */
  public static int cycleIndices[][] = {
    {gb, db, ab, eb, bb, f, c, g, d, a, fb, cb},
    {db, ab, eb, bb, f, c, g, d, a, fb, cb, gb},
    {ab, eb, bb, f, c, g, d, a, fb, cb, gb, db},
    {eb, bb, f, c, g, d, a, fb, cb, gb, db, ab},
    {bb, f, c, g, d, a, fb, cb, gb, db, ab, eb},
    {f, c, g, d, a, e, cb, gb, db, ab, eb, bb},
    {c, g, d, a, e, b, gb, db, ab, eb, bb, f},
    {g, d, a, e, b, fs, cs, gs, ds, bb, es, c},
    {d, a, e, b, fs, cs, gs, ds, bb, es, bs, g},
    {a, e, b, fs, cs, gs, ds, as, es, bs, g, d},
    {e, b, fs, cs, gs, ds, as, es, bs, g, d, a},
    {b, fs, cs, gs, ds, as, es, bs, g, d, a, e},
    {fs, cs, gs, ds, as, es, bs, g, d, a, e, b},
    {cs, gs, ds, as, es, bs, g, d, a, e, b, fs}};

  public static int chromaticIndices[][] = {
    {c, db, d, eb, fb, f, gb, g, ab, a, bb, cb}, /* fb */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, cb}, /* cb */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, cb}, /* gb */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, cb}, /* db */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, b}, /* ab */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, b}, /* eb */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, b}, /* bb */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, b}, /* f  */
    {c, db, d, eb, e, f, gb, g, ab, a, bb, b}, /* c  */
    {c, db, d, eb, e, f, fs, g, ab, a, bb, b}, /* g  */
    {c, cs, d, eb, e, f, fs, g, ab, a, bb, b}, /* d  */
    {c, cs, d, ds, e, f, fs, g, gs, a, bb, b}, /* a  */
    {c, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* e  */
    {c, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* b  */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* f# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* c# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* g# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* d# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* a# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b}, /* e# */
    {bs, cs, d, ds, e, f, fs, g, gs, a, as, b} /* b# */};

  /**
   * table used to adjust input notes from staves, depending on key signature
   */
  public static int adjustPitchInKey[][] =
          {
    /*  c      d       e   f      g      a      b */
    {-1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1}, /* gb -6 */
    {0, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1}, /* db -5 */
    {0, 0, -1, 0, -1, 0, 0, 0, 0, -1, 0, -1}, /* ab -4 */
    {0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, -1}, /* eb -3 */
    {0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1}, /* bb -2 */
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1}, /* f  -1 */
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, /* c   0 */
    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, /* g   1 */
    {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, /* d   2 */
    {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0}, /* a   3 */
    {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0}, /* e   4 */
    {1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0}, /* b   5 */
    {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0}, /* f#  6 */
    {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1} /* c#  7*/ 
    /*  c      d       e   f      g      a      b */};

  /** transpositions of one key to another */
  public static int transpositions[][] =
          {
    {gbkey, gkey, abkey, akey, bbkey, bkey, ckey, dbkey, dkey, ebkey, ekey, fkey},
    {dbkey, dkey, ebkey, ekey, fkey, gbkey, gkey, abkey, akey, bbkey, bkey, ckey},
    {abkey, akey, bbkey, bkey, ckey, dbkey, dkey, ebkey, ekey, fkey, gbkey, gkey},
    {ebkey, ekey, fkey, gbkey, gkey, abkey, akey, bbkey, bkey, ckey, dbkey, dkey},
    {bbkey, bkey, ckey, dbkey, dkey, ebkey, ekey, fkey, gbkey, gkey, abkey, akey},
    {fkey, gbkey, gkey, abkey, akey, bbkey, bkey, ckey, dbkey, dkey, ebkey, ekey},
    {ckey, dbkey, dkey, ebkey, ekey, fkey, gbkey, gkey, abkey, akey, bbkey, bkey},
    {gkey, abkey, akey, bbkey, bkey, ckey, cskey, dkey, ebkey, ekey, fkey, fskey},
    {dkey, ebkey, ekey, fkey, fskey, gkey, abkey, akey, bbkey, bkey, ckey, cskey},
    {akey, bbkey, bkey, ckey, cskey, dkey, ebkey, ekey, fkey, fskey, gkey, abkey},
    {ekey, fkey, fskey, gkey, abkey, akey, bbkey, bkey, ckey, cskey, dkey, ebkey},
    {bkey, ckey, cskey, dkey, ebkey, ekey, fkey, fskey, gkey, abkey, akey, bbkey},
    {fskey, gkey, abkey, akey, bbkey, bkey, ckey, cskey, dkey, ebkey, ekey, fkey},
    {cskey, dkey, ebkey, ekey, fkey, fskey, gkey, abkey, akey, bbkey, bkey, ckey}};

  static public int OCTAVE = 12;

  static private int BOTTOM_KEY = 0;

  static private int TOP_KEY = 13;

  static private int KEY_SIZE = 14;

  static private int BOTTOM_PITCH = 0;

  static private int TOP_PITCH = 20;

  static private int PITCH_NAME_SIZE = 21;

  
  /**
   * the difference between semitone indices in the pitches table
   */
  public static final int SEMITONEOFFSET = 7;

  
  /**
   * Construct the static keys.
   */
  private Key(int index, String name, int cPosition)
    {
    this.index = index;
    this.name = name;
    this.cPosition = cPosition;
    }

  
  /**
   * Get a key given its name
   */
  public static Key getKey(String name)
    {
    String lcName = name.toLowerCase();

    for( int i = BOTTOM_KEY; i <= TOP_KEY; i++ )
      {
      if( lcName.equals(key[i].name) )
        {
        return key[i];
        }
      }
    return null;	// no such key
    }

  
  /**
   * Get a key given its number of sharps (flats if negative);
   */
  public static Key getKey(int sharps)
    {
    int index = sharps + CINDEX;

    if( index < BOTTOM_KEY || index > TOP_KEY )
      {
      return null;	// no such key
      }

    return key[index];
    }

  
  /**
   * Render chromatic pitch as it would be in this key.
   */
  public PitchClass renderInKey(String name)
    {
    int position = PitchClass.getPitchClass(name).getIndex();

    if( position < 0 )
      {
      return null;
      }

    int pitchIndex = (key[index].cPosition + position) % OCTAVE;

    return PitchClass.getPitchClass(chromaticIndices[index][pitchIndex]);
    }

  
  /**
   * Defines how an offset of some number of semitones from the tonic of the key 
   * maps into pitch names.
   */
  public PitchClass rep(int offset)
    {
    if( offset < 0 )
      {
      offset += (-offset / OCTAVE + 1) * OCTAVE;
      }

    assert (offset >= 0);

    offset = offset % OCTAVE;

    return PitchClass.getPitchClass(chromaticIndices[index][offset]);
    }

  
  /**
   * Get the index of this key. (might not be needed)
   */
  public int getIndex()
    {
    return index;
    }

  
  /**
   * Transpose this key to another, by the given number of semitones.
   */
  public Key transpose(int semitones)
    {
    if( semitones < 0 )
      {
      semitones = OCTAVE - (-semitones) % OCTAVE;
      }

    assert (semitones >= 0);

    semitones = semitones % OCTAVE;

    int newIndex = transpositions[index][semitones];

    assert (newIndex >= 0 && newIndex < KEY_SIZE);

    return key[newIndex];
    }

  
  /**
   * Transpose a PitchClass in this key.
   */
  public static PitchClass transpose(PitchClass pc, int semitones)
    {
    return pc.transpose(semitones);
    }

  public static String getKeyName(int index)
    {
    assert (index >= BOTTOM_KEY);
    assert (index <= TOP_PITCH);

    return key[index].name;
    }

  @Override
  public String toString()
    {
    return name;
    }

  
  /**
   * Get the delta in the line of fifths, corresponding to a key
   * represented by a number of sharps (or negative for flats)
   * and a number of semitones transposition.
   */
  public static int getKeyDelta(int sharps, int rise)
    {
    int newSharps = sharps + rise * SEMITONEOFFSET;

    while( newSharps < MIN_KEY )
      {
      newSharps += OCTAVE;
      }

    while( newSharps > MAX_KEY )
      {
      newSharps -= OCTAVE;
      }

    return newSharps;
    }

  
  public static Polylist transposeChordList(Polylist chordSeq, int rise)
    {
    return transposeChordList(chordSeq, rise, Ckey);
    }

  
  public static Polylist transposeChordList(Polylist chordSeq, int rise,
                                              Key key)
    {
    if( rise == 0 || chordSeq.isEmpty() )
      {
      return chordSeq;
      }

    // make a new list of the transposed chords in the render
    Polylist newChords = Polylist.nil;
    while( chordSeq.nonEmpty() )
      {
      String item = (String)chordSeq.first();

      // For now, we are skipping bar-line info
      if( !Advisor.licksIgnore.member(item) )
        {
        newChords = newChords.cons(transposeChord(item, rise, key));
        }
      chordSeq = chordSeq.rest();
      }

    return newChords.reverse();
    }

  
  public static String transposeChord(String chord, int rise, Key key)
    {
    if( rise == 0 || chord.equals(NOCHORD) )
      {
      return chord;
      }

    Polylist exploded = explodeChord(chord);

    String root = (String)exploded.first();
    String body = (String)exploded.second();
    String afterSlash = (String)exploded.third();

    PitchClass pc = PitchClass.getPitchClass(root);

    assert (pc != null);

    PitchClass newRoot = pc.transpose(rise);

    if( afterSlash.equals("") )
      {
      return newRoot.getChordBase() + body;
      }

    // Deal with slash-chord

    PitchClass bass = PitchClass.getPitchClass((String)exploded.fourth());
    
    assert (bass != null);

    String newBass = bass.transpose(rise).getChordBase();

    return newRoot.getChordBase() + body + "/" + newBass;
    }


// Change the root of the chord to C
  public static String makeCroot(String chord)
    {
    return makeRoot(CROOT, chord);
    }


// Change the root of the chord to specified note
  public static String makeRoot(String root, String chord)
    {
    Polylist exploded = explodeChord(chord);

    String body = (String)exploded.second();
    String afterSlash = (String)exploded.third();

    if( afterSlash.equals("") )
      {
      return root + body;
      }

    String origRoot = (String)exploded.first();

    int rise = PitchClass.findRise(root.toLowerCase(), origRoot);

    // Deal with slash-chord

    PitchClass bass = PitchClass.getPitchClass((String)exploded.fourth());
    assert (bass != null);

    String newBass = bass.transpose(rise).getChordBase();

    return root + body + "/" + newBass;
    }

  
  static String getRoot(String chord)
    {
    return (String)explodeChord(chord).first();
    }

  
  static boolean sameRoot(String chord1, String chord2)
    {
    return getRoot(chord1).equals(getRoot(chord2));
    }

  
  static boolean isValidStem(String stem)
    {
    switch( stem.charAt(0) )
      {
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
        return true;
      default:
        return false;
      }
    }

  
  static boolean hasValidStem(String chord)
    {
    return explodeChord(chord) != null;
    }

  
  /**
   * Explode a chord from the leadsheet notation into four parts:
   * the root, the type of chord, the string after a slash, if any,
   * and the bass note.
   * If there is no slash, the third component is the null string, and
   * the bass note is the same as the root.
   *
   *
   * If the chord doen't make sense, then null is returned.
   *
   * @param chord the string naming the chord.
   */
  public static Polylist explodeChord(String chord)
    {
    if( chord.equals("") )
      {
      return null;	// Error indicator
      }

    StringBuilder buffer1 = new StringBuilder();

    buffer1.append(chord.charAt(0));

    if( !isValidStem(buffer1.toString()) )
      {
      return null;
      }

    int len = chord.length();

    int index = 1;

    if( index < len )
      {
      char c = chord.charAt(1);
      if( c == SHARP || c == FLAT )
        {
        buffer1.append(c);
        index++;
        }
      }

    String root = buffer1.toString();

    if( !PitchClass.isValidPitch(root.toLowerCase()) )
      {
      return null;	// root is not known
      }

    // Get the type of the chord.

    StringBuilder buffer2 = new StringBuilder();

    while( index < len && chord.charAt(index) != SLASH )
      {
      buffer2.append(chord.charAt(index));
      index++;
      }

    String body = buffer2.toString();

    String bass = root.toLowerCase();	// default
    String afterSlash = "";		// default

    StringBuilder buffer3 = new StringBuilder();

    if( index < len )
      {
      // We have a slash chord.
      index++;	// skip the slash

      while( index < len )
        {
        buffer3.append(chord.charAt(index));
        index++;
        }

      afterSlash = buffer3.toString();

      if( !PitchClass.isValidPitch(afterSlash.toLowerCase()) )
        {
        return null;
        }
      bass = afterSlash.toLowerCase();
      }

    return Polylist.list(root, body, afterSlash, bass);
    }

  
  /**
   * Look up a string in a table.
   * Eventually uses of this could be replaced with a map of some kind.
   */
  static int lookup(String arg, String[] table)
    {
    for( int i = 0; i < table.length; i++ )
      {
      if( table[i].equals(arg) )
        {
        return i;
        }
      }
    return -1;	// Not Found
    }


/**
 * Return list of any invalid notes in the argument list.
@param L
@return
 */

public static Polylist invalidNotes(Polylist L)
  {
    if( L.isEmpty() )
      {
        return Polylist.nil;
      }

    Object first = L.first();
    
    if( first instanceof String && NoteSymbol.isValidNote((String)first) )
      {
        return invalidNotes(L.rest());
      }
    else if( first instanceof Polylist
        && ((Polylist)first).length() == 2
        && ((Polylist)first).first() instanceof String
        && NoteSymbol.isValidNote((String)((Polylist)first).first())
        && ((Polylist)first).second() instanceof Number )
      {
        return invalidNotes(L.rest());
      }
    else
    {
       return invalidNotes(L.rest()).cons(first);

    }
  }


  /**
   * Get midi pitch from a leadsheet melody note.
   * Duration is ignored.  -1 is returned if the note is a rest.
   */

  public static int pitchFromLeadsheet(String string)
    {
    return pitchFromLeadsheet(string, 0);
    }

  public static int pitchFromLeadsheet(String string, int rise)
    {
    Note note = noteFromLeadsheet(string, rise, BEAT);
    if( note == null )
      {
      return -1;
      }
    return note.getPitch();
    }

  
  /**
   * Transform a leadsheet melody note or rest given as a string to 
   * a Note, e.g. for insertion into a Score.  Note that octave
   * shifts and durations are accepted in leadsheet melody notation.
   */
  public static Note noteFromLeadsheet(String string, int rise,
                                         int slotsPerBeat)
    {
    return noteFromLeadsheet(string, rise, BEAT, Ckey);
    }

  public static Note noteFromLeadsheet(String string, int rise,
                                         int slotsPerBeat, Key key)
    {
    return NoteSymbol.makeNoteSymbol(string, rise).toNote();
    }


  /**
   * Return a profile of a list of note Strings.
   */
  public static String profileNoteStringList(Polylist L,
                                               boolean includeTrailer)
    {
    char UP = '/';
    char DOWN = '\\';
    char NEUTRAL = ' ';
    String LEADER = " ";
    String GAP = " ";
    String TRAILER = "-note:";

    Polylist R = Polylist.nil;
    StringBuilder buffer = new StringBuilder();
    int noteCount = 0;

    int previousPitch = -1;
    char previousSymbol = NEUTRAL;

    buffer.append(LEADER);

    // get the first pitch in the list
    while( L.nonEmpty() )
      {
      Object ob = L.first();
      if( ob instanceof NoteSymbol || ob instanceof String )
        {
        noteCount++;
        int pitch =
                (ob instanceof String) ? pitchFromLeadsheet((String)ob) : ((NoteSymbol)ob).getMIDI();
        noteCount++;
        if( pitch >= 0 )
          {
          previousPitch = pitch;
          L = L.rest();
          break;
          }
        }
      L = L.rest();
      }

    while( L.nonEmpty() )
      {
      Object ob = L.first();
      if( ob instanceof String || ob instanceof NoteSymbol )
        {
        noteCount++;
        int pitch =
                (ob instanceof String) ? pitchFromLeadsheet((String)ob) : ((NoteSymbol)ob).getMIDI();
        if( pitch >= 0 )
          {
          if( pitch > previousPitch )
            {
            if( previousSymbol != UP )
              {
              buffer.append(UP);
              previousSymbol = UP;
              }
            }
          else if( pitch < previousPitch )
            {
            if( previousSymbol != DOWN )
              {
              buffer.append(DOWN);
              previousSymbol = DOWN;
              }
            }
          previousPitch = pitch;
          }
        }
      L = L.rest();
      }

    if( includeTrailer )
      {
      buffer.append(GAP);
      buffer.append(noteCount);
      buffer.append(TRAILER);
      }

    return buffer.toString();
    }

  
  /**
   * Transpose list of note Strings in leadsheet form by a certain rise
   * returning a list of note Strings.
   */
  public static Polylist transposeNoteStringList(Polylist L,
                                                   int rise,
                                                   Key key)
    {
    Polylist R = Polylist.nil;

    while( L.nonEmpty() )
      {
      if( !(L.first() instanceof String) )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Unexpected item " + L.first() + " preceding: " + L.rest());
        }
      else
        {
        R = R.cons(transposeNoteString((String)L.first(), rise, key));
        }
      L = L.rest();
      }

    return R.reverse();
    }

 
  /**
   * Transpose a note String in leadsheet form by a certain rise
   * returning a String.  Returns null if the note String is not well-formed.
   */
  public static String transposeNoteString(String noteString, int rise,
                                             Key key)
    {
    assert (!noteString.equals(""));

    char c = noteString.charAt(0);

    assert (Character.isLowerCase(c));

    if( c == RESTCHAR )
      {
      return noteString;		// rests are unchanged
      }

    Polylist item = Polylist.explode(noteString).rest();

    boolean natural = true;
    boolean sharp = false;

    StringBuilder noteBase = new StringBuilder();
    noteBase.append(c);

    if( item.nonEmpty() )
      {
      Character second = (Character)item.first();
      if( second.equals(SHARP) || second.equals(FLAT) )
        {
        item = item.rest();
        noteBase.append(second);
        }
      }

    PitchClass pc = PitchClass.getPitchClass(noteBase.toString());

    PitchClass newPC = pc.transpose(rise);

    // Get octave info from original note.

    int octavesUp = 0;

    while( item.nonEmpty() )
      {
      Character x = (Character)item.first();
      if( x.equals(PLUS) )
        {
        octavesUp++;
        item = item.rest();
        }
      else if( x.equals(MINUS) )
        {
        octavesUp--;
        item = item.rest();
        }
      else
        {
        break;
        }
      }

    String duration = item.implode();

    // Adjust for the possibility that transposition will change octaves.

    if( (rise > 0) && (pc.getSemitones() > newPC.getSemitones()) )
      {
      octavesUp++;
      }
    else if( (rise < 0) && (pc.getSemitones() < newPC.getSemitones()) )
      {
      octavesUp--;
      }

    // Create the new octave string.

    StringBuilder octaveString = new StringBuilder();
    while( octavesUp > 0 )
      {
      octaveString.append(PLUS);
      octavesUp--;
      }
    while( octavesUp < 0 )
      {
      octaveString.append(MINUS);
      octavesUp++;
      }

    String result = newPC.toString() + octaveString.toString() + duration;

    return result;
    }

  
  /**
   * Transpose list of note Strings in leadsheet form by a certain rise
   * returning a list of numbers representing the notes independent of key.
   */
  public static Polylist transposeNoteStringListToNumbers(Polylist L,
                                                            int rise,
                                                            Key key)
    {
    Polylist R = Polylist.nil;

    while( L.nonEmpty() )
      {
      R = R.cons(transposeNoteStringToNumbers((String)L.first(), rise, key));
      L = L.rest();
      }

    return R.reverse();
    }

  
  /**
   * Transpose a note String in leadsheet form by a certain rise
   * returning a String representing a number, such as "3", "b5", "#2", etc.
   */
  public static String transposeNoteStringToNumbers(String noteString,
                                                      int rise,
                                                      Key key)
    {
    assert (!noteString.equals(""));

    char c = noteString.charAt(0);

    assert (Character.isLowerCase(c));

    if( c == RESTCHAR )
      {
      return noteString;		// rests are unchanged
      }

    Polylist item = Polylist.explode(noteString).rest();

    StringBuilder noteBase = new StringBuilder();
    noteBase.append(c);

    if( item.nonEmpty() )
      {
      Character second = (Character)item.first();
      if( second.equals(SHARP) || second.equals(FLAT) )
        {
        item = item.rest();
        noteBase.append(second);
        }
      }

    PitchClass pc = PitchClass.getPitchClass(noteBase.toString());

    PitchClass newPC = pc.transpose(rise);

    String newNote = PitchClass.numbers[newPC.getIndex()];

    // Get octave info from original note.

    int octavesUp = 0;

    while( item.nonEmpty() )
      {
      Character x = (Character)item.first();
      if( x.equals(PLUS) )
        {
        octavesUp++;
        item = item.rest();
        }
      else if( x.equals(MINUS) )
        {
        octavesUp--;
        item = item.rest();
        }
      else
        {
        break;
        }
      }

    // Creat the new octave string.

    StringBuilder octaveString = new StringBuilder();
    while( octavesUp > 0 )
      {
      octaveString.append(PLUS);
      octavesUp--;
      }
    while( octavesUp < 0 )
      {
      octaveString.append(MINUS);
      octavesUp++;
      }

    // Leave off duration for now, to keep it from being too busy.

    return newNote + octaveString.toString();
    }


  /*
   * transposeOne transposes one pitch class
   */
  public static String transposeOne(String from, String to, String p, Key key)
    {
    if( p.charAt(0) == 'r' )
      {
      return p; // rest
      }

    int rise = PitchClass.findRise(from, to);

    PitchClass pc = PitchClass.getPitchClass(p);

    PitchClass newPC = pc.transpose(rise);

    return newPC.toString();
    }


  /*
   * transposeKey transposes a key by the difference between to and from pitches
   */
  public Key transposeKey(String from, String to)
    {
    int rise = PitchClass.findRise(from, to);

    return transpose(rise);
    }


  /*
   * transposeList is a list version of transposePitch.
   * It transposes a whole list of pitches by the interval between
   * the from and to strings.
   */
  public static Polylist transposeList(String from, String to, Polylist L,
                                         Key key)
    {
    if( L.isEmpty() )
      {
      return Polylist.nil;
      }

    from = from.toLowerCase();
    to = to.toLowerCase();

    if( from.equals(to) )
      {
      return L;	// No actual transposition needed.
      }

    PitchClass fromPC = PitchClass.getPitchClass(from);
    assert (fromPC != null);

    PitchClass toPC = PitchClass.getPitchClass(to);
    assert (toPC != null);

    int rise = toPC.getSemitones() - fromPC.getSemitones();

    Polylist R = Polylist.nil;
    while( L.nonEmpty() )
      {
      assert (L.first() instanceof String);
      String p = (String)L.first();

      if( p.charAt(0) == 'r' )
        {
        R = R.cons(p);
        }
      else
        {
        PitchClass pc = PitchClass.getPitchClass(p);

        PitchClass newPC = pc.transpose(rise);

        R = R.cons(newPC.toString());
        }
      L = L.rest();
      }

    return R.reverse();
    }

  
  /**
   * enharmonic determines whether the pitches represented by
   * two strings representing pitch are enharmonically equivalent
   */
  public static boolean enharmonic(String x, String y)
    {
    return PitchClass.findRise(x, y) == 0;
    }

  
  /**
   * enMember determines whether the first pitch is enharmonically
   * equivalent to some member of a list
   */
  public static boolean enhMember(String x, Polylist L)
    {
    while( L.nonEmpty() )
      {
      if( enharmonic(x, (String)L.first()) )
        {
        return true;
        }
      L = L.rest();
      }
    return false;
    }

  
  /**
   * Auxiliary unit test method for Key.
   */
  static boolean test(String name)
    {
    Key key = getKey(name);
    assert (key != null);
    if( key.toString().equals(name.toLowerCase()) )
      {
      System.out.println("Key test passed for " + name);
      return true;
      }
    else
      {
      System.out.println("Key test failed for " + name);
      return false;
      }
    }

  
  /**
   * Make a note from a pitch class name specified as one of the Strings
   * in the pitches table.  A pitch class represents many
   * individual pitches.  The specific pitch is found by
   * using the midiBase argument as C that beings the octave in
   * which the desired note occurs.
   *
   * If there is a problem with the String, null is returned.
   */
  
  public static Note makeNote(String pitchClassName, int midiBase,
                                int duration)
    {
    return makeNoteAbove(pitchClassName, midiBase, 0, duration);
    }

  
  /**
   * Make a note from a pitch class specified as one of the Strings
   * in the pitches table, above a minimum MIDI value.
   *  A pitch class represents many
   * individual pitches.  The specific pitch is found by
   * using the midiBase argument as C that beings the octave in
   * which the desired note occurs.  However, this octave can be
   * modified by specifying a non-zero minimum.  This is used in
   * arpeggios, for example, to guarantee the notes keep ascending,
   * by specifying the previous midi value.
   *
   * If there is a problem with the String, null is returned.
   */
  
  public static Note makeNoteAbove(String pitchClassName, int midiBase,
                                     int minimum, int duration)
    {
    PitchClass pc = PitchClass.getPitchClass(pitchClassName);

    int midi = midiBase + pc.getSemitones();

    while( midi < minimum )
      {
      midi += OCTAVE;
      }

    boolean natural = pc.getNatural();

    boolean sharp = !natural && pc.getSharp();

    return new Note(midi, natural, sharp, duration);
    }

  
  /**
   * Unit test method for Key.
   */
  public static void main(String arg[])
    {
    if( test("gb") && test("db") && test("ab") && test("eb") && test("bb") && test("f") && test("c") && test("g") && test("d") && test("a") && test("e") && test("b") && test("f#") && test("c#") && test("Gb") && test("Db") && test("Ab") && test("Eb") && test("Bb") && test("F") && test("C") && test("G") && test("D") && test("A") && test("E") && test("B") && test("F#") && test("C#") )
      {
      System.out.println("All tests passed.");
      }
    else
      {
      System.out.println("Some test failed.");
      }

    System.out.println("Major scales:");

    for( int i = 0; i < 14; i++ )
      {
      System.out.print("key " + key[i].toString() + ": ");
      System.out.print(key[i].rep(0) + " ");
      System.out.print(key[i].rep(2) + " ");
      System.out.print(key[i].rep(4) + " ");
      System.out.print(key[i].rep(5) + " ");
      System.out.print(key[i].rep(7) + " ");
      System.out.print(key[i].rep(9) + " ");
      System.out.print(key[i].rep(11) + " ");
      System.out.println();
      System.out.println();
      }

    System.out.println("Chromatic scales:");

    for( int i = 0; i < 14; i++ )
      {
      System.out.print("key " + key[i].toString() + ": ");
      for( int j = -12; j <= 12; j++ )
        {
        System.out.print(key[i].rep(j) + " ");
        }
      System.out.println();
      System.out.println();
      }

    for( int i = 0; i < 14; i++ )
      {
      for( int j = 0; j <= 12; j++ )
        {
        System.out.println("key " + key[i].toString() + " transposed " + j + " = " + key[i].transpose(j));
        }
      System.out.println();
      }

    // render each pitch in each key

    for( int i = 0; i < 14; i++ )
      {
      System.out.print("key " + key[i].toString() + ": ");
      for( int j = BOTTOM_PITCH; j <= TOP_PITCH; j++ )
        {
        System.out.print(key[i].renderInKey(PitchClass.getPitchClass(j).toString()) + " ");
        }
      System.out.println();
      }
    }

  }

