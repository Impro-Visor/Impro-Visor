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
import imp.com.OpenLeadsheetCommand;
import imp.util.ErrorLog;
import java.io.File;
import java.util.ArrayList;

/**
 * Given a string of notes (pitches and rhythm) and a string of n chords and their durations,
 * constructs n bass rules, one per every cord
 * @author Brandy McMenamy, Jim Herold
 * 7/21/2007
 */

public class BassPatternExtractor implements Constants
{
private boolean debug = false;
public boolean canContinue = true;
/**
 * The list of all chord names, scale, and durations found in the
 * MIDIBeast.chordFileName
         *
 */
private ArrayList<ChordType> chords = new ArrayList<ChordType>();
/**
 * An array of 12 indices that store enharmonic information ex: indice 0 has b#
 * and c
         *
 */
private ArrayList<ArrayList<String>> halfSteps = new ArrayList<ArrayList<String>>();
private ArrayList<String> rules = new ArrayList<String>();
private double startBeat = -1;
private double endBeat = -1;

public BassPatternExtractor()
  {
    createChordInfo();
    if( canContinue )
      {
        makeHalfStepArray();

        if( debug )
          {
            int totalChordInfo = 0;
            System.out.println("\n## After createChordInfo() ##");
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
                totalChordInfo += chords.get(i).getDurationInSlots();
              }
            System.out.println("Total Chord Info: " + totalChordInfo);
          }

        rules = rules();
        if( debug )
          {
            System.out.println("\n## After rules() ##");
            for( int i = 0; i < rules.size(); i++ )
              {
                System.out.println(rules.get(i));
              }
          }
        try
          {
            MIDIBeast.originalBassRules = removeMinorRhythms();
          }
        catch( Exception e )
          {
            e.printStackTrace();
          }

        if( debug )
          {
            System.out.println("\n## After removeMinorRhythms() ##");
            for( int i = 0; i < MIDIBeast.originalBassRules.size(); i++ )
              {
                System.out.println(MIDIBeast.originalBassRules.get(i));
                System.out.println("\tDuration: " + MIDIBeast.numBeatsInBassRule(MIDIBeast.originalBassRules.get(i)));
              }
          }
      }
  }

public BassPatternExtractor(double startBeat, double endBeat)
  {
    this.startBeat = startBeat;
    this.endBeat = endBeat;
    createChordInfo();
    if( canContinue )
      {
        makeHalfStepArray();

        if( debug )
          {
            System.out.println("\n## After createChordInfo() ##");
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
              }
          }

        MIDIBeast.originalBassRules = rules();

        if( debug )
          {
            System.out.println("\n## After rules() ##");
            for( int i = 0; i < MIDIBeast.originalBassRules.size(); i++ )
              {
                System.out.println(MIDIBeast.originalBassRules.get(i));
              }
          }
      }
  }

private void makeHalfStepArray()
  {
    ArrayList<ArrayList<String>> hs = new ArrayList<ArrayList<String>>();
    for( int i = 0; i < 12; i++ )
      {
        hs.add(new ArrayList<String>());
      }
    //array should start at b#/c to be compliant with Impro-Visor
    hs.get(0).add("b#");
    hs.get(0).add("c");
    hs.get(1).add("c#");
    hs.get(1).add("db");
    hs.get(2).add("d");
    hs.get(3).add("d#");
    hs.get(3).add("eb");
    hs.get(4).add("e");
    hs.get(4).add("fb");
    hs.get(5).add("e#");
    hs.get(5).add("f");
    hs.get(6).add("f#");
    hs.get(6).add("gb");
    hs.get(7).add("g");
    hs.get(8).add("g#");
    hs.get(8).add("ab");
    hs.get(9).add("a");
    hs.get(10).add("a#");
    hs.get(10).add("bb");
    hs.get(11).add("b");
    hs.get(11).add("cb");

    halfSteps = hs;
  }

/**
 *
 * @param chords all chords from the chordNameFile
 * @return an ArrayList of each chord as a ChordType where duration is stored in
 * terms of number of slots per beat Format for chord file ex: C / / / |/ / / /
 * |E7 / / / |
 */
  private void createChordInfo()
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
            double totalChordLength = 0.0;
            for( int i = 0; i < chords.size(); i++ )
              {
                System.out.println(chords.get(i));
                totalChordLength += chords.get(i).getDuration();
              }
            System.out.println("Total Chord Length: " + totalChordLength);
          }
      }
  }

/**
 * @param notes - the list of processed notes
 * @param chords - the list of processed chords
 * @return - a list of the rules
 */
private ArrayList<String> rules()
  {
    ArrayList<String> ans = new ArrayList<String>();
    ArrayList<SlottedNote> notes = MIDIBeast.originalBassNotes;
    ChordType curChord;
    ChordType nextChord;
    SlottedNote curNote, nextNote = null;
    int slotCount = 0;


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
        int noteNumber = 0;
        while( slotCount < curChord.getDurationInSlots() 
            && curChord.getDurationInSlots() > 0 
            && noteNumber < notes.size() )
          {
            noteNumber++;
            curNote = notes.get(0);
            try
              {
                nextNote = notes.get(1);
              }
            catch( Exception e )
              {
                nextNote = null;
              }

            slotCount += curNote.getNumberOfSlots();

            if( slotCount > curChord.getDurationInSlots() )
              {
                //a note spans more than one chord
                int inChordDuration = curNote.getNumberOfSlots() - (slotCount - curChord.getDurationInSlots());
                
               SlottedNote inChordNote = new SlottedNote(inChordDuration, curNote.getPitch());
               int outChordDuration = Math.abs(curNote.getNumberOfSlots() - inChordDuration);
             
               SlottedNote outChordNote = new SlottedNote(outChordDuration, curNote.getPitch());

                //create a rule for the part of the note in current chord
                String s = formatRule(inChordNote, nextNote, curChord, nextChord);
                if( debug )
                  {
                    System.out.println("Resulting rule: " + s);
                  }
                aRule += s;
                //change duration of note to the amount in the next chord
                notes.set(0, outChordNote);
              }
            else
              {
                String t = formatRule(curNote, nextNote, curChord, nextChord);
                if( debug )
                  {
                    System.out.println("Restulting rule: " + t);
                  }
                aRule += t;
                notes.remove(0);
              }
            if( notes.isEmpty() )
              {
                return ans;
              }
          }
        try
          {
            aRule = aRule.substring(0, aRule.length() - 1); //remove space at end
            ans.add(aRule + ")");
          }
        catch( IndexOutOfBoundsException e )
          {
            e.printStackTrace();

            ErrorLog.log(ErrorLog.WARNING, "An unknown error occured when interpretting bass rules, "
                    + "probably due to an incorrect format of the chord file.  Ignored malformed rule.");
          }
      }
    return ans;
  }

private ArrayList<String> removeMinorRhythms()
  {
    for( int i = 0; i < rules.size(); i++ )
      {
        boolean firstElementRemoved = false, lastElementRemoved = false;
        String[] split = rules.get(i).split(" ");
        if( split.length == 1 )
          {
            continue;
          }
        String rhythmString = MIDIBeast.getRhythmString(split[0]);
        if( MIDIBeast.belowRoundingThreshold(rhythmString) )
          {
            firstElementRemoved = true;
            split[1] = addRhythm(rhythmString, split[1]);
            split = MIDIBeast.removeRule(0, split);
          }
        if( split.length == 1 )
          {
            continue;
          }
        rhythmString = MIDIBeast.getRhythmString(split[split.length - 1]);
        if( MIDIBeast.belowRoundingThreshold(rhythmString) )
          {
            lastElementRemoved = true;
            split[split.length - 2] = addRhythm(rhythmString, split[split.length - 2]);
            split = MIDIBeast.removeRule(split.length - 1, split);
          }
        String s = "";
        if( firstElementRemoved )
          {
            s += "(";
          }
        s += split[0];
        for( int j = 1; j < split.length; j++ )
          {
            s += " " + split[j];
          }
        if( lastElementRemoved )
          {
            s += ")";
          }
        rules.set(i, s);
      }
    return rules;
  }

        public String addRhythm(String rhythmString, String elementString){
            return elementString + "+" +  rhythmString;
        }

/**
 *
 * @param note - a note
 * @param curChord - the chord the note occurs in
 * @param nextChord - the next chord
 * @return every pitch value of the note with respect to the chords
 */

private String formatRule(SlottedNote note, SlottedNote nextNote, ChordType curChord, ChordType nextChord)
  {
    if( debug )
      {
        System.out.println("Formatting rule");
        System.out.println("Current Note: " + note);
        System.out.println("Next Chord: " + nextChord);
        System.out.println("Current Chord: " + curChord);
        System.out.println("Next Note: " + nextNote);
      }
    try
      {
        int slots = note.getNumberOfSlots();
        if( slots <= 0 )
          {
            return ""; // Not sure how this arises, or what to do with it.
          }
        
        String s = MIDIBeast.stringDuration(slots) + " ";

        if( note.getPitch().equalsIgnoreCase("r") ) //rest
          {
            return "R" + s;
          }

        //case that handles slash chords
        if( curChord.isSlashChord() )
          {
            if( note.getPitch().equalsIgnoreCase(curChord.getSlashBass()) )
              {
                return "B" + s;
              }
            if( nextChord != null )
              {
                if( nextChord.isSlashChord() )
                  {
                    if( note.getPitch().equalsIgnoreCase(nextChord.getSlashBass()) ) //root of next chord
                      {
                        return "C" + s; // FIX: was "N" + s;
                      }
                    int dist = distanceInHalfSteps(note.getPitch(), nextChord.getSlashBass());
                    if( dist == 1 || dist == 11 ) //approach tone to next chord (within 2 half steps either side)
                      {
                        return "A" + s;
                      }
                  }
                else
                  {
                    if( note.getPitch().equalsIgnoreCase(nextChord.getChordRoot()) ) //root of next chord
                      {
                        return "C" + s; // FIX: was "N" + s;
                      }

                    int dist = distanceInHalfSteps(note.getPitch(), nextChord.getChordRoot());
                    if( dist == 1 || dist == 11 ) //approach tone to next chord (within 1 half step either side)
                      {
                        return "A" + s;
                      }
                  }
              }
          }
        else
          {
            if( note.getPitch().equalsIgnoreCase(curChord.getChordRoot()) ) //root of current chord
              {
                return "B" + s;
              }
            if( nextChord != null )
              {
                if( note.getPitch().equalsIgnoreCase(nextChord.getChordRoot()) ) //root of next chord
                  {
                    return "C" + s; // FIX: was "N" + s;
                  }

                int dist = distanceInHalfSteps(note.getPitch(), nextChord.getChordRoot());
                if( dist == 1 || dist == 11 ) //approach tone to next chord (within 1 half step either side)
                  {
                    return "A" + s;
                  }
              }
          }

        if( !curChord.isSlashChord || (curChord.isSlashChord() && !(curChord.getSlashBass().equalsIgnoreCase(note.getPitch()))) )
          {
            int sv = getScaleValue(note, curChord);
            if( sv == 5 )
              {
                return "X(" + sv + ")" + s; // The format will be changed in postprocessing
              }
            else
              {
                if( MIDIBeast.chordTones )
                  {
                    imp.data.Note thisNote = new imp.data.Note(note.getPitchNumber(), false, note.getNumberOfSlots());
                    imp.data.Note upcomingNote = null;
                    if( nextNote != null )
                      {
                        upcomingNote = new imp.data.Note(nextNote.getPitchNumber(), false, note.getNumberOfSlots());
                      }
                    imp.data.Chord thisChord = new imp.data.Chord(curChord.getName());
                    imp.data.Chord upcomingChord = null;
                    if( nextChord != null )
                      {
                        upcomingChord = new imp.data.Chord(nextChord.getName());
                      }
                    else if( upcomingNote != null && thisChord.classify(thisNote, upcomingNote, upcomingChord) == 0 )
                      {
                        return "C" + s;
                      }
                  }
                if( sv > 1 )	//position within scale
                  {
                    return "X(" + sv + ")" + s; // The format will be changed in postprocessing
                  }

                return "C" + s; //a note not in the scale
              }
          }

      }
    catch( Exception e )
      {
        e.printStackTrace();
        MIDIBeast.addError("An unknown error occured when interpretting bass rules, "
                + "probably due to an incorrect format of the chord file.");
        return "";
      }
    return "";
  }


/**
 * @param noteOne
 * @param noteTwo
 * @return the halfstep distance between noteOne and noteTwo
 */
private int distanceInHalfSteps(String noteOne, String noteTwo)
  {
    int startIndex = 0;
    for( int i = 0; i < halfSteps.size(); i++ )
      {
        for( int j = 0; i < halfSteps.size() && j < halfSteps.get(i).size(); j++ )
          {
            if( halfSteps.get(i).get(j).equals(noteOne) )
              {
                startIndex = i;
                //break loops
                j = halfSteps.get(i).size() + 100;
                i = halfSteps.size() + 100;
              }
          }
      }

    for( int i = 0; i < halfSteps.size(); i++ )
      {
        for( int j = 0; j < halfSteps.get((i + startIndex) % 12).size(); j++ )
          {
            if( (halfSteps.get((i + startIndex) % 12).get(j)).equals(noteTwo) )
              {
                return i;
              }
          }
      }

    return -1; //note doesn't exist in music
  }

/**
 * @param note
 * @param chord
 * @return the scale value of note with respect to a scale played over chord
 */
private int getScaleValue(SlottedNote note, ChordType chord)
  {
    int startIndex = 0;
    for( int i = 0; i < halfSteps.size(); i++ )
      {
        for( int j = 0; i < halfSteps.size() && j < halfSteps.get(i).size(); j++ )
          {
            if( halfSteps.get(i).get(j).equals(chord.getChordRoot()) )
              {
                startIndex = i;
                //break loops
                j = halfSteps.get(i).size() + 1;
                i = halfSteps.size() + 1;
              }
          }
      }

    for( int i = 0; i < halfSteps.size(); i++ )
      {
        for( int j = 0; j < halfSteps.get((i + startIndex) % 12).size(); j++ )
          {
            if( halfSteps.get((startIndex + i) % 12).get(j).equals(note.getPitch()) )
              {
                return chord.getScale()[i];
              }
          }
      }
    return -1;
  }

private class ChordType
{

private String name;
private double duration;
private int[] scale;
private boolean isSlashChord;
private String slashBass;
private boolean unknownChord = false;
/**
 * scale types and their patterns *
 */
//supported major scales:
private int[] majorScale =
  {
    1, -1, 2, -1, 3, 4, -1, 5, -1, 6, -1, 7
  };
private int[] majorFlatFiveScale =
  {
    1, -1, 2, -1, 3, 4, 5, -1, -1, 6, -1, 7
  };
private int[] majorFlatSixScale =
  {
    1, -1, 2, -1, 3, 4, -1, 5, 6, -1, -1, 7
  };
private int[] majorSharpFiveScale =
  {
    1, -1, 2, -1, 3, 4, -1, -1, 5, 6, -1, 7
  };
//supported minor scales:
private int[] naturalMinorScale =
  {
    1, -1, 2, 3, -1, 4, -1, 5, 6, -1, 7, -1
  };
private int[] minorSharpFiveScale = naturalMinorScale;
//private int[] minorFlatSixScale = naturalMinorScale;
private int[] minorFlatFiveScale =
  {
    1, -1, 2, 3, -1, 4, 5, -1, 6, -1, 7, -1
  };
private int[] minorMajorSevenScale =
  {
    1, -1, 2, 3, -1, 4, -1, 5, 6, -1, -1, 7
  };
//supported dominant scales:
private int[] dominantScale =
  {
    1, -1, 2, -1, 3, 4, -1, 5, -1, 6, 7, -1
  };
private int[] dominantFlatFiveScale =
  {
    1, -1, 2, -1, 3, 4, 5, -1, -1, 6, 7, -1
  };
private int[] dominantSharpFiveScale =
  {
    1, -1, 2, -1, 3, 4, -1, -1, 5, 6, 7, -1
  };
private int[] dominantFlatSixScale =
  {
    1, -1, 2, -1, 3, 4, -1, 5, 6, -1, 7, -1
  };

public ChordType(String n, double d)
  {
    name = n;
    duration = d;
    isSlashChord = false;
    slashBass = "";
    scale = findScaleType();
  }

public boolean getChordStatus()
  {
    return unknownChord;
  }

public String getName()
  {
    return name;
  }

public double getDuration()
  {
    return duration;
  }

public int getDurationInSlots()
  {
    return (int) (duration * BEAT);
  }

public void setDuration(double d)
  {
    duration = d;
  }

public int[] getScale()
  {
    return scale;
  }

public String getChordRoot()
  {
    return String.valueOf(name.charAt(0)).toLowerCase();
  }

public String getSlashBass()
  {
    return slashBass;
  }

public boolean isSlashChord()
  {
    return isSlashChord;
  }

public String toString()
  {
    return name + " " + duration;
  }

/**
 * @return the scale that best fits a chord
 */
private int[] findScaleType()
  {
    String type = name.substring(1);
    if( type.length() > 0 && (type.charAt(0) == '#' || type.charAt(0) == 'b') )
      {
        type = type.substring(1);
      }

    //Any chords that modify a chord by adding notes above the octave will return the
    //unmodified chord because these notes are considered irrelevant for the base line

    //Slash Chords
    if( type.indexOf("/") >= 0 )
      {
        isSlashChord = true;
        String[] slash = type.split("/");
        //find name of bottom chord
        slashBass = slash[1];
        type = slash[0];

        if( slashBass.length() > 1 && (slashBass.charAt(1) == '#' || slashBass.charAt(1) == 'b') )
          {
            slashBass = String.valueOf(slashBass.charAt(0)) + String.valueOf(slashBass.charAt(1));
          }
        else
          {
            slashBass = String.valueOf(slashBass.charAt(0));
          }
      }

    //Major Chords
    if( type.length() == 0 || type.equals("M") || type.equals("2")
            || type.equals("6") || type.equals("69") || type.equals("M13#11")
            || type.equals("M13") || type.equals("M6") || type.equals("M69#11")
            || type.equals("M69") || type.equals("M7#11") || type.equals("M7")
            || type.equals("M7add13") || type.equals("M9#11") || type.equals("M9")
            || type.equals("Madd9") || type.equals("add2") || type.equals("add9")
            || type.equals("M7b9") || type.equals("5") || type.equals("4") || type.contains("maj")
            || type.contains("Maj") )
      {
        return majorScale;
      }
    else if( type.equals("M7b5") || type.equals("M9b5") || type.equals("Mb5") )
      {
        return majorFlatFiveScale;
      }
    else if( type.equals("M7b6") || type.equals("Mb6") )
      {
        return majorFlatSixScale;
      }
    //Minor Chords
    else if( type.equals("m")    || type.equals("m11") || type.equals("m13")
            || type.equals("m6") || type.equals("m69") || type.equals("m7")
            || type.equals("m9") || type.equals("mM9") || type.equals("madd9") )
      {
        return naturalMinorScale;
      }
    else if( type.equals("m11b5") || type.equals("m7b5") || type.equals("m9b5")
          || type.equals("h")     || type.contains("dim") )
      { //h for Ch = Cm7b5
        return minorFlatFiveScale;
      }
    else if( type.equals("m#5") || type.equals("m+") || type.equals("m11#5")
          || type.equals("m7#5") || type.equals("9#5") )
      {
        return minorSharpFiveScale;
      }
    else if( type.equals("mM7b6") || type.equals("mM7") || type.equals("mb6M7") )
      {
        return minorMajorSevenScale;
      }
    //Augmented Chords
    else if( type.equals("M#5") || type.equals("+") || type.equals("aug")
            || type.equals("+7") || type.equals("M#5add9") || type.equals("M7#5")
            || type.equals("M7+") || type.equals("M9#5") || type.equals("+add9") )
      {
        return majorSharpFiveScale;
      }
    //Dominant Chords
    else if( type.equals("7") || type.equals("7b13") || type.equals("7b9#11")
            || type.equals("7b9#11b13") || type.equals("7b9") || type.equals("7b9b13#11")
            || type.equals("7b9b13") || type.equals("7no5") || type.equals("7#11")
            || type.equals("7#11b13") || type.equals("7#9#11") || type.equals("7#9#11b13")
            || type.equals("7#9") || type.equals("7#9b13") || type.equals("9")
            || type.equals("9#11") || type.equals("9#11b13") || type.equals("9b13")
            || type.equals("9no5") || type.equals("13#11") || type.equals("13#9#11")
            || type.equals("13#9") || type.equals("13") || type.equals("13b9#11")
            || type.equals("13b9") )
      {
        return dominantScale;
      }
    else if( type.equals("7b5#9") || type.equals("7b5") || type.equals("7b5b13")
            || type.equals("7b5b9") || type.equals("7b5b9b13") || type.equals("9b5")
            || type.equals("9b5b13") || type.equals("13b5") )
      {
        return dominantFlatFiveScale;
      }
    else if( type.equals("7#5") || type.equals("7+") || type.equals("aug7")
            || type.equals("7aug") || type.equals("7#5#9") || type.equals("7alt")
            || type.equals("7#5b9#11") || type.equals("7#5b9") || type.equals("9#5")
            || type.equals("9+") || type.equals("9#5#11") )
      {
        return dominantSharpFiveScale;
      }
    else if( type.equals("7b6") )
      {
        return dominantFlatSixScale;
      }
    //Suspension Chords
    //ex: (c f g) or (c e g)
    else if( type.equals("Msus2") || type.equals("Msus4") || type.equals("sus2")
            || type.equals("sus4") || type.equals("susb9") )
      {
        return majorScale;
      }
    //ex: (c e g bb) or (c f g bb)
    else if( type.equals("11") || type.equals("7sus4") || type.equals("7sus4b9")
            || type.equals("7sus4b9b13") || type.equals("7susb9") || type.equals("7b9sus")
            || type.equals("9sus4") || type.equals("13sus4") || type.equals("7b9b13sus4")
            || type.equals("7b9sus4") || type.equals("7b9sus4") || type.equals("7sus")
            || type.equals("9sus") || type.equals("13sus") )
      {
        return dominantScale;
      }
    unknownChord = true;
    return majorScale;
  }

}
}