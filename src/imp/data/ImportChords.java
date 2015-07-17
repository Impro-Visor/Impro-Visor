/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.data;

import imp.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import jm.music.data.Phrase;
import jm.util.Read;

/**
 * Modeled after ImportBass
 *
 * July 2007 - Sayuri Soejima
 * Reformatted 23 April 2011, R. Keller
 */

public class ImportChords implements Constants
{
public static final int DRUM_CHANNEL = 9;

private boolean debug = false;

/**
 * false if an error occurs that is serious enough that the rest of the style
 * generation for the chord line must be prevented from firing.
 *
 */

public boolean canContinue = true;
public static jm.music.data.Score score;
private jm.music.data.Phrase mainPhrase;
private ArrayList<jm.music.data.Note> noteArray = new ArrayList<jm.music.data.Note>();
private ArrayList<SlottedNote> roundedNoteArray;
private ArrayList<String> melodyStringArray;

/**
 * ImportChords method with a specified instrumentation for the chords
 *
 * @param midiFileName - a midi file
 * @param specifiedPart - a specified part (optional) @creates an array of the
 * notes' pitches and durations.
 */

public ImportChords(jm.music.data.Part specifiedPart)
  {
    noteArray = new ArrayList<jm.music.data.Note>();
    roundedNoteArray = new ArrayList<SlottedNote>();
    MIDIBeast.chordPart = specifiedPart;
    melodyStringArray = new ArrayList<String>();
    score = new jm.music.data.Score();
    Read.midi(score, MIDIBeast.midiFileName);

    createMainPhrase();
    getNoteArray();
    roundDurations(MIDIBeast.getResolution());
    MIDIBeast.originalChordNotes = roundedNoteArray;
  }

/**
 * ImportChords method without a specified instrumentation for the chords
 *
 * @param midiFileName - a midi file @creates an array of the notes' pitches and
 * durations.
 */

public ImportChords()
  {
    noteArray = new ArrayList<jm.music.data.Note>();
    roundedNoteArray = new ArrayList<SlottedNote>();
    melodyStringArray = new ArrayList<String>();
    score = new jm.music.data.Score();
    Read.midi(score, MIDIBeast.midiFileName);

    getchordPart();
    createMainPhrase();
    getNoteArray();
    roundDurations(MIDIBeast.getResolution());
    MIDIBeast.originalChordNotes = roundedNoteArray;
  }

public ImportChords(double startBeat, double endBeat, jm.music.data.Part selectedPart)
  {
    noteArray = new ArrayList<jm.music.data.Note>();
    roundedNoteArray = new ArrayList<SlottedNote>();
    MIDIBeast.chordPart = selectedPart;
    mainPhrase = MIDIBeast.chordPart.getPhraseArray()[0];
    if( debug )
      {
        System.out.println("## Before changing start position ##");
        System.out.println(mainPhrase);
      }
    setPhraseStartAndEnd(startBeat, endBeat);
    getNoteArray();
    roundDurations(MIDIBeast.getResolution());

    MIDIBeast.originalChordNotes = roundedNoteArray;
  }

/**
 * @return
 */

public ArrayList<String> getMelodyStringArray()
  {
    return melodyStringArray;
  }

/**
 * @return the numerator of the time signature
 */

public double getNumerator()
  {
    return MIDIBeast.numerator;
  }

/**
 * @return the denominator of the time signature
 */

public double getDenominator()
  {
    return MIDIBeast.denominator;
  }


/**
 * @return the array of the notes whose slot numbers have been rounded to the
 * nearest number based on the precision value.
 */

public ArrayList<SlottedNote> getOriginalNotes()
  {
    return roundedNoteArray;
  }

/**
 * Extract a part from the array of parts that is most likely to be the chords
 * part, by looking at all of the instrumentation.
 */

public void getchordPart()
  {
    jm.music.data.Part[] parts = score.getPartArray();
    if( debug )
      {
        System.out.println("Found Instrument Numbers:");
        for( int i = 0; i < parts.length; i++ )
          {
            if( parts[i].getChannel() == DRUM_CHANNEL )
              //Channel 9 is reserved for drums, so make sure it catches the part on channel 9 as drums.
              {
                System.out.println("DRUM");
              }
            else
              {
                System.out.println(MIDIBeast.getInstrumentForPart(parts[i]));
              }
          }
      }
    for( int i = 0; i < parts.length; i++ )
      {
        int currentInstrument = parts[i].getInstrument();
        if( currentInstrument >= 0 && currentInstrument <= 31 && parts[i].getChannel() != DRUM_CHANNEL )
          {
            //If the instrument is a kind of keyboard or guitar, it is read as a chords part.
            MIDIBeast.chordPart = parts[i];
            if( debug )
              {
                System.out.println("Returning Instrument " + MIDIBeast.getInstrumentForPart(MIDIBeast.chordPart) + " as chords instrument");
                System.out.println("Original Notes");
                for( int j = 0; j < MIDIBeast.chordPart.getPhraseArray().length; j++ )
                  {
                    for( int k = 0; k < MIDIBeast.chordPart.getPhraseArray()[j].getNoteArray().length; k++ )
                      {
                        System.out.println(MIDIBeast.chordPart.getPhraseArray()[j].getNoteArray()[k]);
                      }
                  }
              }
            return;
          }
      }
    MIDIBeast.chordPart = null;
    MIDIBeast.addError("Could not find a chord part.  Go to Generate-->Preferences for Generation to choose a chord part from available instruments.");
    canContinue = false;
  }

/**
 * Selects which phrase is the main phrase to be read to create the rhythm
 * patterns, based on which phrase has the longest duration.
 */

public void createMainPhrase()
  {
    if( canContinue )
      {
        try
          {
            Phrase[] phraseArray = MIDIBeast.chordPart.getPhraseArray();
            double longestPhraseLength = 0.0;
            int longestPhraseIndex = -1;
            if( debug )
              {
                System.out.println("Number of phrases: " + phraseArray.length);
              }
            for( int i = 0; i < phraseArray.length; i++ )
              {
                double phraseLength = phraseArray[i].getEndTime() - phraseArray[i].getStartTime();
                if( debug )
                  {
                    System.out.println("Phrase(" + i + ") Start - " + phraseArray[i].getStartTime() + " End - " + phraseArray[i].getEndTime() + " Length - " + phraseLength);
                  }
                if( phraseLength > longestPhraseLength )
                  {
                    longestPhraseLength = phraseLength;
                    longestPhraseIndex = i;
                  }
              }
            if( debug )
              {
                System.out.println("Phrase " + longestPhraseIndex + " found to be longest with length " + longestPhraseLength);
              }
            //if(phraseArray[longestPhraseIndex].getStartTime() != 0){
            //	Phrase p = new Phrase(new Note(Integer.MIN_VALUE, phraseArray[longestPhraseIndex].getStartTime()));
            //	p.addNoteList(phraseArray[longestPhraseIndex].getNoteArray());
            //	phraseArray[longestPhraseIndex] = p;
            //	if(debug) System.out.println("Phrase did not start at 0.0");
            //}
            mainPhrase = phraseArray[longestPhraseIndex];
          }
        catch( ArrayIndexOutOfBoundsException e )
          {
            MIDIBeast.addError("The chord part is corrupted.  Go to Generate-->Preferences for Generation to choose a different chord part from available instruments.");
            canContinue = false;
          }
      }
  }

/**
 * Create an array of notes from the main phrase (selected in
 * createMainPhrase()).
 */

public void getNoteArray()
  {
    jm.music.data.Note[] notes = mainPhrase.getNoteArray();
    noteArray.addAll(Arrays.asList(notes));
  }

///**
// * Convert the duration (rhythm value) of each note to a specific number of
// * slots (there are 120 slots per quarter note beat).
// */
//
//public void oldRoundDurations(int precision)
//  {
//    for( int i = 0; i < noteArray.size(); i++ )
//      {
//        int numberOfSlots = findSlots(noteArray.get(i).getRhythmValue(), precision);
//        String pitch = pitchOf(noteArray.get(i).getPitch());
//        SlottedNote toBeAdded = new SlottedNote(numberOfSlots, pitch);
//        roundedNoteArray.add(toBeAdded);
//      }
//    if( debug )
//      {
//        System.out.println("## After roundDurations() ## ");
//        for( int i = 0; i < roundedNoteArray.size(); i++ )
//          {
//            System.out.println(roundedNoteArray.get(i));
//          }
//      }
//  }

/**
 * New version modeled after noteArray2ImpPart in ImportMelody.
 * @param precision 
 */       
        
public void roundDurations(int precision)
  {
    double time = 0;
    int slot = 0;
    for( jm.music.data.Note note : noteArray ) 
      {
        double origRhythmValue = note.getRhythmValue();
        int rhythmValue;
        SlottedNote toBeAdded;
         if( note.isRest() )
          {
          rhythmValue = precision*(int)((BEAT*(time + origRhythmValue) - slot)/precision);
	  toBeAdded = new SlottedNote(rhythmValue, "r");
         }
        else
          {
          String pitch = MIDIBeast.pitchOf(note.getPitch());
          rhythmValue = precision*(int)Math.round((BEAT * origRhythmValue) / precision);
          toBeAdded = new SlottedNote(rhythmValue, pitch);
          }
        roundedNoteArray.add(toBeAdded);
        slot += rhythmValue;
        time += origRhythmValue;
      }  
   }  
   

/**
 *
 * @param duration - duration of the note
 * @param precision - precision to which it should round (in slots)
 * @return the number of slots that the value of the duration represents.
 */

public int findSlots(double duration, int precision)
  {
    return (int) Math.round(BEAT * duration / precision) * precision;
  }

/**
 * Converts the pitch numbers of the notes into actual letter pitches.
 * ?? Use the static method in MIDIBeast?
 */

public String pitchOf(int pitchNumber)
  {
    int i = pitchNumber % 12;
    switch( i )
      {
        case 0:
            return "c";
        case 1:
            return "c#";
        case 2:
            return "d";
        case 3:
            return "d#";
        case 4:
            return "e";
        case 5:
            return "f";
        case 6:
            return "f#";
        case 7:
            return "g";
        case 8:
            return "g#";
        case 9:
            return "a";
        case 10:
            return "a#";
        case 11:
            return "b";
      }
    return "r"; //error
  }

/*
 * @param startMeasure @param endMeasure This method chops off the beginning and
 * end of the main phrase to match the user selected start and end measures
 * This method will need to be changed if chords are to be
 * as multiple phrased entities.
 */

public void setPhraseStartAndEnd(double startBeat, double endBeat)
  {
    jm.music.data.Note[] noteArray = mainPhrase.getNoteArray();
    double beatCount = 0;
    int noteIndex = 0, endNoteIndex = 0, startNoteIndex = 0;
    if( endBeat == 0 )
      {
        endBeat = mainPhrase.getEndTime();
      }
    boolean start = false;
    while( beatCount < endBeat && noteIndex < noteArray.length )
      {
        beatCount += noteArray[noteIndex].getRhythmValue();
        if( beatCount > startBeat && !start )
          {
            double remainder = beatCount - startBeat;
            noteArray[noteIndex] = new jm.music.data.Note(noteArray[noteIndex].getPitch(), remainder);
            startNoteIndex = noteIndex;
            start = true;
          }
        noteIndex++;
      }
    endNoteIndex = noteIndex;
    jm.music.data.Note[] newNoteArray = new jm.music.data.Note[endNoteIndex - startNoteIndex];
    for( int i = startNoteIndex, j = 0; i < endNoteIndex; i++, j++ )
      {
        newNoteArray[j] = noteArray[i];
      }
    mainPhrase = new Phrase(startBeat);
    mainPhrase.addNoteList(newNoteArray);
    if( debug )
      {
        System.out.println("## After setPhraseStartAndEnd() ##");
        System.out.println(mainPhrase);
      }
  }

}