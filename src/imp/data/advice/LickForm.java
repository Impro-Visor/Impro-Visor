/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
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

package imp.data.advice;

import imp.gui.LickLog;
import polya.*;
import imp.*;
import imp.data.ChordSymbol;
import imp.data.Key;
import imp.data.NoteSymbol;
import imp.data.PitchClass;
import imp.util.*;

/**
 * A LickForm houses the form information for a lick.
 * The actual scale information is determined by supplying a tonic pitch.
 * @see         Advisor
 * @author      Robert Keller
 */
public class LickForm implements Constants
{
    static final String NAME       = "name";
    static final String SEQUENCE   = "sequence";
    static final String NOTES      = "notes";

    // Added "brick" to typeName to test
    static final String typeName[] = {"lick", "quote", "graded-lick", "brick"};

public static enum ExistentLickStatus {NEW, DIFFERENT_SEQUENCE, SAME_SEQUENCE};

static int maxDiscriminators = 2;
static int beatsPerBar = 4;
static double slotsPerHalfBeat = 60.0;

String name = null;

Polylist chordSequence = null;

int numDiscriminators = 0;

String discriminator[] = new String[maxDiscriminators];

int discriminatorOffset[] = new int[maxDiscriminators];

Polylist notes = null;

String profile = null;

int profileNumber;

int numSlots;

int halfBeats;

int serial;

int type;

int grade;

/**
 * The constructor is private. A factory is used so that
 * error-checking can be done prior to construction
 *
 * accum is the accumulated list of licks, to which this factory may add.
 */

static public Polylist makeLickForm(Polylist arg, int serial, boolean marked, Polylist accum, int type, int grade,
	boolean allowDuplicates, Polylist otherAccum)
  {
  Polylist restArg = arg.rest();

  String name = null;

  String chordName = null;

  Polylist notes = null;

  Polylist nameElement = restArg.assoc(NAME);
  if( nameElement == null )
    {
    ErrorLog.log(ErrorLog.WARNING, "Lick or quote definition needs a name: " + arg + ", ignoring");
    return accum;
    }
  
  nameElement = nameElement.rest();

  if( nameElement.isEmpty() )
    {
    ErrorLog.log(ErrorLog.WARNING, "Error lick or quote without a name: " + arg + ", ignoring");
    return accum;
    }
  
  name = Advisor.concatListWithSpaces(nameElement);

  Polylist sequenceElement = Polylist.assoc(SEQUENCE, restArg);

  if( sequenceElement == null )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Ignoring lick or quote without sequence: " + arg);
    return accum;
    }

  // In case there is punctuation from saving through the GUI:

  Polylist sequenceList = Advisor.dePunctuate(sequenceElement.rest());


  Polylist notesElement = Polylist.assoc(NOTES, arg);

  if( notesElement == null || notesElement.rest().isEmpty() )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Ignoring lick or quote without notes: " + arg);
    return accum;
    }

  notes = notesElement.rest();

  int rise = 0;

  Polylist  L = sequenceList;

  if( L.isEmpty() )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Lick must have chords: " + arg);
    return accum;
    }

  String firstChord = (String)L.first();

  LickForm form = new LickForm();

  form.chordSequence = ChordSymbol.chordSymbolsFromStrings(sequenceList);

  ChordSymbol firstChordSymbol = ChordSymbol.makeChordSymbol(firstChord);

  if( firstChordSymbol == null )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Unknown chord: " + firstChord + " in lick or quote: " + arg);
    return accum;
    }
  
/*
  ChordForm chordInfo = firstChordSymbol.getChordForm();

  if( chordInfo == null )
    {
    ErrorLog.log(ErrorLog.SEVERE, "No chord info for: " + firstChordSymbol + " in lick or quote: " + arg);
    return accum;
    }
*/
  
  String firstChordRoot = firstChordSymbol.getRootString();

  Key key = Key.Ckey;  // was chordInfo.getKey();

  form.type = type;

  // transpose the chords

  rise = PitchClass.findRiseToC(firstChordRoot);

  form.chordSequence = ChordSymbol.transpose(form.chordSequence, rise);

  // transpose the notes by the same rise

  form.notes = NoteSymbol.makeNoteSymbolList(notes, rise);

  form.numSlots = NoteSymbol.getDuration(form.notes);

  form.halfBeats = (int)Math.ceil(form.numSlots / slotsPerHalfBeat);


  // record the chords used for matching

  int index = 0;

  for( Polylist M = sequenceList; index < maxDiscriminators && M.nonEmpty(); M = M.rest() )
    {
    String chord = (String)M.first();

    if( !chord.equals(NOCHORD) && !Advisor.licksIgnore.member(chord) )
      {
      form.discriminator[index] = Key.transposeChord(chord, rise, key);
      index++;
      }
    }

  form.numDiscriminators = index;

  if( form.numDiscriminators <= 0 )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Chords must be specified in order to save lick.");
    return accum;
    }

  int halfBeatIncrement = (int)Math.floor(form.halfBeats/form.numDiscriminators);

  int offset = 0;

  // The following should be refined later, as it is just an approximation to offsets.

  for( int i =  0; i < form.numDiscriminators; i++ )
    {
      {
      form.discriminatorOffset[i] = offset;
      }
    offset += halfBeatIncrement;
    }

  // initialize the unused positions in the discriminator.

  for( ; index < maxDiscriminators; index++ )
    {
    form.discriminator[index] = null;
    }

  form.name = " " + (marked ? "* " : "") + name;

  form.serial = serial;
  
  form.grade = grade;

  if( marked ) // already exists
    {
    return accum;
    }

  if( !allowDuplicates )
    {
    int exists = lickExists(arg, form, accum);
    int existsOther = lickExists(arg, form, otherAccum);
    if( exists == IGNORE || existsOther == IGNORE)
      {
      // don't add existing lick again
      return accum;
      }
    else if (exists == OVERWRITE || existsOther == OVERWRITE)
      {
      accum = removeLick(form, accum);
      }
    }
  Advisor.purgeCache();    // So that new licks will be seen
  return accum.cons(form);
  }


static int lickExists(Polylist arg, LickForm form, Polylist accum)
  {
  LickForm existent = findLick(form, accum);

  if( existent != null )
    {
    int noteRise = ((NoteSymbol)form.notes.first()).getRise((NoteSymbol)existent.notes.first());

    // Transpose chords same as notes

    Polylist transposedChords = ChordSymbol.transpose(form.chordSequence, noteRise);

    boolean sameChordSequence = ChordSymbol.enhChordSequences(transposedChords, existent.chordSequence);

    if( sameChordSequence )
      {
      return LickLog.log("This lick already exists:\n\n    " + arg
		       + "\n\nwhen transposed to:\n\n     " + form
		       + "\n\nit is the same as the existing (in transposed form):\n\n    " + existent
		       + "\n\n(Impro-Visor transposes all licks so that the first chord has root C.)");

      }
    else
      {
      return LickLog.log("This lick already exists:\n\n    " + arg
		       + "\n\nwhen transposed to:\n\n     " + form
		       + "\n\nbut the existing one would have a different chord sequence: " + transposedChords
		       + "\n\nexisting (in transposed form):\n\n    " + existent
		       + "\n\n(Impro-Visor transposes all licks so that the first chord has root C.)");
      }
    }
  return SAVE;
  }

/**
 * Search for a lick that has the same note sequence, possibly transposed, as the first argument.
 * If such a lick is found, it is returned.  Otherwise null is returned.
 */

static LickForm findLick(LickForm form, Polylist lickForms)
  {
  while( lickForms.nonEmpty() )
    {
    LickForm existent = (LickForm)lickForms.first();
    if( NoteSymbol.isomorphicNoteSequences(form.notes, existent.notes) )
      {
      return existent;
      }
    lickForms = lickForms.rest();
    }

  return null;
  }

static Polylist removeLick(LickForm form, Polylist lickForms)
{
    LickForm inList = findLick(form, lickForms);
    Polylist removed = new Polylist();
    while (lickForms.nonEmpty())
    {
	if ((LickForm)lickForms.first() != inList)
	    removed = removed.cons(lickForms.first());
	lickForms = lickForms.rest();
    }
    removed = removed.reverse();
    return removed;
}

public boolean equals(LickForm other)
  {
  return other.notes.equals(notes);  
  }

public static  void showType(Polylist L)
  {
  while( L.nonEmpty() )
    {
    System.out.print(L.first() + ": " + L.first().getClass() + " ");
    L = L.rest();
    }
  System.out.println();
  }

public void showForm(java.io.PrintStream out)
  {
  out.print("LickForm serial = " + serial 
         + ", name = " + getName() 
         + ", chord sequence = " + chordSequence 
         + ", notes = " + getNotes(LCROOT) 
         + ", disc = ");
  
  for( int i = 0; i < numDiscriminators; i++)
    {
    out.print(discriminator[i] + " at " + discriminatorOffset[i] + " ");
    }
  out.println();
  }

String getName() { return name; }

Polylist getChordSequence(String tonic, Key key) 
  { 
  return ChordSymbol.transpose(chordSequence, PitchClass.findRise(tonic));
  }

Polylist getNotes()
  {
  return getNotes(LCROOT);
  }

Polylist getNotes(String tonic)
  {
  return getNotes(tonic, Key.Ckey);
  }

String getChord(int index, String tonic, Key key)
  {
  if( index >= numDiscriminators )
    {
    return null;
    }
  int rise = PitchClass.findRise(tonic);  
  return Key.transposeChord(discriminator[index], rise, key);
  }

int getOffset(int index)
  {
  if( index >= numDiscriminators )
    {
    return -1;
    }
  return discriminatorOffset[index];
  }


Polylist getNotes(String tonic, Key key)
  {
  int rise = PitchClass.findRise(tonic);
  Polylist newNotes = NoteSymbol.transposeNoteSymbolList(notes, rise);

  Trace.log(2, "notes before transposition: " + notes 
             + " to tonic " + tonic 
             + " (rise " + rise 
             + "): " + newNotes);

  return newNotes;
  }

// profile is lazily evaluated

String getProfile()
  {
  getProfileNumber();
  return profile;
  }

/**
 * profileNumber is lazily evaluated
 *
 * If the length of the profile is 0, then the profile number is 0.
 * Otherrwise, if the profile starts ascending (/) then the number
 * is positive, while if it starts descending (\) then the number is
 * negative.  The absolute value is the length of the profile.
 */

int getProfileNumber()
  {
  if( profile == null )
    {
    profile = Key.profileNoteStringList(notes, false);
    profileNumber = profile.length() - 1;
    if( profileNumber > 0 )
      {
      if( profile.charAt(1) == '/' ) // descending
        {
        profileNumber = - profileNumber;
        }
      }
    }
  return profileNumber;
  }

int getSerial()
  {
  return serial;
  }

int getHalfBeats()
  {
  return halfBeats;
  }

private LickForm()
  {
  }

public String toString()
  {
    String s = "(" + typeName[type] + " "	// i.e. lick or quote
		+ notes.cons("notes") + " "
		+ chordSequence.cons("sequence") 
		+ " (name" + getName();
    if (grade != 0)
	s += ") (grade " + grade + ")\n";
    else
	s += ")\n";
    return s;
  }
}
