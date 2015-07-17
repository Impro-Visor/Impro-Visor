/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

import polya.*;
import imp.*;
import imp.util.*;

/**
 * A CellForm houses the form information for a cell.
 * The actual scale information is determined by supplying a tonic pitch.
 * @see         Advisor
 * @author      Robert Keller
 */
public class CellForm implements Constants
{
    static final String NAME       = "name";
    static final String CHORDS     = "chords";
    static final String NOTES      = "notes";

    static final String typeName[] = {"cell", "idiom"};

static int nominalSlotsPerBeat = 120;
static double halfBeatsPerSlot = 60.0;

String name = null;

String chordName = null;

Polylist notes = null;

String profile = null;

int profileNumber;

int numSlots;

int serial;

int type;

/**
 * The constructor is private. A factory is used so that
 * error-checking can be done prior to construction
 *
 * accum is the accumulated list of cells, to which this factory may add.
 */

static public Polylist makeCellForms(Polylist arg, int serial, boolean marked, Polylist accum, int type)
  {
  String name = null;

  String chordName = null;

  Polylist notes = null;

  Polylist nameElement = arg.assoc(NAME);
  if( nameElement == null )
    {
    ErrorLog.log(ErrorLog.WARNING, "Cell or idiom definition needs a name: " + arg + ", ignoring");
    return accum;
    }
  
  nameElement = nameElement.rest();

  if( nameElement.isEmpty() )
    {
    ErrorLog.log(ErrorLog.WARNING, "Error cell or idiom without a name: " + arg + ", ignoring");
    return accum;
    }
  
  name = Advisor.concatListWithSpaces(nameElement);

  Polylist chordElement = Polylist.assoc(CHORDS, arg);

  if( chordElement == null || chordElement.isEmpty() )
	{
	ErrorLog.log(ErrorLog.SEVERE, "Ignoring cell or idiom without chords: " + arg);
        return accum;
	}

  // In case there is punctuation from saving through the GUI:

  Polylist chordList = Advisor.dePunctuate(chordElement.rest());


  Polylist notesElement = Polylist.assoc(NOTES, arg);

  if( notesElement == null || notesElement.rest().isEmpty() )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Ignoring cell or idiom without notes: " + arg);
    return accum;
    }

  notes = notesElement.rest();

  // NOTE: An input cell can create one CellForm per chord listed, 
  // so we return a list rather than a single form

  while( chordList.nonEmpty() )
      {
      String raw = (String)chordList.first();

      if( !raw.equals(NOCHORD) )
        {
	// transpose the chord to C

        ChordSymbol chordSymbol = ChordSymbol.makeChordSymbol(raw);

        if( chordSymbol == null )
          {
          ErrorLog.log(ErrorLog.SEVERE, "Ignoring unparsable chord: " + raw + " in cell " + arg);
          continue;
          }

        CellForm form = new CellForm();

        form.type = type;

	// get the chord and key info

	form.chordName = chordSymbol.getName();

	ChordForm chordInfo = chordSymbol.getChordForm();

	if( chordInfo == null )
	  {
	  ErrorLog.log(ErrorLog.SEVERE, "Ignoring unknown chord: " + form.chordName + " in cell " + arg);
	  continue;
	  }

	Key key = chordInfo.getKey();

	// transpose the notes
	String chordRoot = chordSymbol.getRootString();

	int rise = PitchClass.findRiseToC(chordRoot);

        form.notes = NoteSymbol.makeNoteSymbolList(notes, rise);

        form.chordName = Key.makeCroot(form.chordName);

        form.numSlots = NoteSymbol.getDuration(form.notes);

	// Note that notes are created BEFORE the advice
	// menu is specialized to a root.

	form.name = " " + (marked ? "* " : "") + name;

        form.serial = serial;
        Advisor.purgeCache();     // so that new cell will be seen
        accum = accum.cons(form);
        }
     chordList = chordList.rest();
     }
   return accum;
   }


public void showForm(java.io.PrintStream out)
  {
  out.println("CellForm serial = " + serial + ", name = " + getName() + ", chord = " + chordName + ", notes = " + getNotes(LCROOT));
  }

String getName() { return name; }

String getChordName() { return chordName; }

Polylist getNotes(String tonic)
  {
  return getNotes(tonic, Key.Ckey);
  }

Polylist getNotes(String tonic, Key key)
  {
  int rise = PitchClass.findRise(tonic);
  Polylist newNotes = NoteSymbol.transposeNoteSymbolList(notes, rise);

//  Trace.log(0, "notes before transposition: " + notes + " to tonic " + tonic + " (rise " + rise + "): " + newNotes);

  return newNotes;
  }

/**
 *  profile is lazily evaluated
 */

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
  return (int)Math.ceil(numSlots / halfBeatsPerSlot);
  }

private CellForm()
  {
  }

public String toString()
  {
  return "(" + typeName[type] + " "	// i.e. lick or quote
             + notes.cons("notes") 
             + " (name" + getName() + ") "
             + "(chords " + chordName + ")"
             +")";
  }

}
