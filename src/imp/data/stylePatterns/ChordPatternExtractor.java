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

import imp.Constants;
import imp.com.OpenLeadsheetCommand;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MIDIBeast;
import imp.data.Note;
import imp.data.Score;
import java.io.File;
import java.util.ArrayList;

/**
 * Given a string of notes (pitches and rhythm) and a string of n chords and
 * their durations, constructs n chord rules, one per every chord on the
 * leadsheet.
 *
 * @ Modeled after BassPatterns July 2007 - Sayuri Soejima
 * Reformatted 30 April 2012 by Robert Keller
 */

public class ChordPatternExtractor implements Constants
{

private boolean debug = false;
public boolean canContinue = true;
private ArrayList<String> rules;
/**
 * The list of all chord names, scale, and durations found in the
 * MIDIBeast.chordFileName
         *
 */
private ArrayList<ChordType> chords = new ArrayList<ChordType>();
private double startBeat = -1;
private double endBeat = -1;
private int minDuration = 0;

/**
 * constructor (without specifiedPart).
 *
 * @param midiFileName - the midi file from which to read the notes.
 * @param chordFileName - the leadsheet file from which to read the chord
 * progression.
 */

public ChordPatternExtractor(int minDuration) throws Exception
  {
    this.minDuration = minDuration;
    init();
    if( canContinue )
      {
        if( debug )
          {
            System.out.println("\n## After init() ##");
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
              }
          }
        if( debug )
          {
            System.out.println("Before Rules");
          }
        rules = rules(MIDIBeast.originalChordNotes, chords);
        MIDIBeast.originalChordRules = rules;
        if( debug )
          {
            System.out.println("\n## After rules() ##");
            for( int i = 0; i < rules.size(); i++ )
              {
                System.out.println(rules.get(i));
              }
          }
      }
  }

public ChordPatternExtractor(double startBeat, double endBeat, int minDuration) throws Exception
  {
    this.startBeat = startBeat;
    this.endBeat = endBeat;
    this.minDuration = minDuration;
    init();
    if( canContinue )
      {

        if( debug )
          {
            System.out.println("\n## After init() ##");
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
              }
          }

        rules = rules(MIDIBeast.originalChordNotes, chords);

        if( debug )
          {
            System.out.println("\n## After rules() ##");
            for( int i = 0; i < rules.size(); i++ )
              {
                System.out.println(rules.get(i));
              }
          }
      }
  }

/**
 *
 * @param chords - all chords from the chordNameFile
 * @return an ArrayList of each chord as a ChordType where duration is stored in
 * terms of number of slots per beat Format for chord file ex: C / / / |/ / / /
 * |E7 / / / |
 */

public void init() throws Exception
  {
      //modified for using chord extraction instead of leadsheet:
      String fileName = MIDIBeast.chordFileName;
      //use the extracted chords from MIDI, unless leadsheet is provided
      ChordPart c = MIDIBeast.extractedChordPart;
      //if leadsheet is provided, use it instead
      if (MIDIBeast.useLeadsheet) {
          File chordFile = new File(MIDIBeast.chordFileName);
          Score s = new Score();
          (new OpenLeadsheetCommand(chordFile, s)).execute();
          c = s.getChordProg();
      }
      //end
      
      int slotCount = 0;
    Chord chord = c.getChord(slotCount);
    while( chord != null )
      {
        double duration = ((chord.getRhythmValue() * 1.0) / MIDIBeast.slotsPerMeasure) * MIDIBeast.numerator;
        String name = chord.getName();
        chords.add(new ChordType(name, duration));
        slotCount += chord.getRhythmValue();
        chord = c.getChord(slotCount);
      }

    if( startBeat != -1 )
      {
        int startIndex = 0, endIndex = chords.size();
        boolean start = false;
        double count = 0.0;
        if( startBeat == 0 )
          {
            start = true;
          }
        for( int i = 0; i < chords.size(); i++ )
          {
            count += chords.get(i).getDuration();
            if( count > startBeat && !start )
              {
                startIndex = i;
                start = true;
                chords.get(i).setDuration(chords.get(i).getDuration() - (count - startBeat));
              }
            if( count > endBeat )
              {
                endIndex = i;
                chords.get(i).setDuration(chords.get(i).getDuration() - (count - endBeat));
                break;
              }
            if( count == startBeat )
              {
                startIndex = i + 1;
                start = true;
              }
            if( count == endBeat )
              {
                endIndex = i + 1;
                break;
              }
          }
        if( startIndex == endIndex )
          {
            ChordType newChord = chords.get(startIndex);
            chords = new ArrayList<ChordType>();
            chords.add(newChord);
          }
        else
          {
            ArrayList<ChordType> newChords = new ArrayList<ChordType>();
            for( int i = startIndex; i < endIndex; i++ )
              {
                newChords.add(chords.get(i));
              }
            chords = newChords;
          }
        if( debug )
          {
            System.out.println("## Chords After Changing Begin and End Beat ##");
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
              }
          }
      }
  }

/**
 * @return the rhythm rules for the chords
 */

public ArrayList<String> getRules()
  {
    return rules;
  }

/**
 * @param notes - the list of processed notes
 * @param chords - the list of processed chords
 * @return - a list of the rules
 */

private ArrayList<String> rules(ArrayList<imp.data.SlottedNote> notes, ArrayList<ChordType> chords)
  {
    ArrayList<String> ans = new ArrayList<String>();
    ChordType curChord;
    ChordType nextChord;
    imp.data.SlottedNote curNote;
    int slotCount;

    for( int i = 0; i < chords.size(); i++ )
      {
        String aRule = "("; //the rule for the current chord
        curChord = chords.get(i);
        if( i != chords.size() - 1 )
          {
            nextChord = chords.get(i + 1);
          }
        else
          {
            nextChord = null;
          }

        slotCount = 0;
        while( slotCount < curChord.getDurationInSlots() && curChord.getDurationInSlots() > 0 )
          {
            curNote = notes.get(0);
            if( curNote.getNumberOfSlots() == 0 )
              {
                notes.remove(0);
                continue;
              }
            slotCount += curNote.getNumberOfSlots();

            if( slotCount > curChord.getDurationInSlots() )
              {
                //a note spans more than one chord
                int inChordDuration = curNote.getNumberOfSlots() - (slotCount - curChord.getDurationInSlots());
                imp.data.SlottedNote inChordNote = new imp.data.SlottedNote(inChordDuration, curNote.getPitch());
                int outChordDuration = Math.abs(curNote.getNumberOfSlots() - inChordDuration);
                imp.data.SlottedNote outChordNote = new imp.data.SlottedNote(outChordDuration, curNote.getPitch());

                //create a rule for the part of the note in current chord
                aRule += formatRule(inChordNote, minDuration);
                //change duration of note to the amount in the next chord
                notes.set(0, outChordNote);
              }
            else
              {
                aRule += formatRule(curNote, minDuration);
                notes.remove(0);
              }
            if( notes.isEmpty() )
              {
                return ans;
              }
          }
        aRule = aRule.substring(0, aRule.length() - 1); //remove space at end
        ans.add(aRule + ")");

        //rk System.out.println("aRule = " + aRule + ")");
      }
    return ans;
  }

/**
 * formatRule now includes the capability of converting anything of less than
 * duration minDuration into a rest.
 *
 * @param note - a note
 * @return whether it is a hit (X) or a rest (R) and the duration of the note.
 */

private String formatRule(imp.data.SlottedNote note, int minDuration)
  {

    int slots = note.getNumberOfSlots();

    String s = Note.getDurationString(note.getNumberOfSlots()) + " ";

    String returnString;

    // slots < minDuration clause added on 8 June 2008 by rk

    if( note.getPitch().equalsIgnoreCase("r") || slots < minDuration )
      {
        returnString = "R" + s;
      }
    else
      {
        returnString = "X" + s;
      }

    if( debug )
      {
        System.out.println("Formatting rule " + note + ", minDuration = " + minDuration + " --> " + returnString);
      }

    return returnString;
  }

/**
 * ChordType class
 */

private class ChordType
{

private String name;
private double duration;

/**
 * @param n - the name of the chord.
 * @param d - the duration of the chord (not in slots, but as a double value).
 */

public ChordType(String n, double d)
  {
    name = n;
    duration = d;
  }

/**
 * @return the name of the chord.
 */

public String getName()
  {
    return name;
  }

/**
 * @return the duration of the chord (as a double).
 */

public double getDuration()
  {
    return duration;
  }

public void setDuration(double duration)
  {
    this.duration = duration;
  }

/**
 * @return the duration of the chord as a number of slots.
 */

public int getDurationInSlots()
  {
    return (int) (duration * BEAT);
  }

/**
 * @return a String with the name of the chord as well as the duration (as a
 * double).
 */

@Override
public String toString()
  {
    return name + "   " + duration;
  }

}
}