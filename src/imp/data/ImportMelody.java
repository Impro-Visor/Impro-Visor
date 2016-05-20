/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012-2014 Robert Keller and Harvey Mudd College
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
import static imp.Constants.BEAT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * ImportMelody is adapted by Robert Keller from ImportBass, by Brandy McMenamy
 * and James Herold 7/18/2007 Reads and parses a channel of a midi file as a
 * melody line. The program assumes that all notes are played in sequential
 * order (no chords) and therefore only reads the first Phrase. It then
 * interprets the rhythm duration of each note as the closest musical value that
 * can be represented by 120 slots per beat for the given time signature.
 * Pitches are kept as integers representing Hertz values. The final melody was
 * originally stored in MIDIBeast.originalBassNotes, which of course breaks
 * encapsulation.
 */
public class ImportMelody implements Constants
{
static int FACTOR = 120;

//private ArrayList<Note> originalMelodyNotes = new ArrayList<Note>();

private jm.music.data.Part parts[];

/**
 * constructor. Reads a score and adjusts the rhythm durations in the melody
 * line.
 */

public ImportMelody(jm.music.data.Score score)
  {
    parts = score.getPartArray();
  }


/**
 * Convert the designated track of a jm Part to an Impro-Visor part
 * @param melodyPart
 * @param trackNumber
 * @param partOut
 * @param precision 
 */
public static void convertToImpPart(jm.music.data.Part melodyPart,
                                    int trackNumber,
                                    MelodyPart partOut,
                                    int precision)
  {
     convertToImpPart(melodyPart.getPhraseArray()[trackNumber], 
                      partOut, 
                      precision);
  }
    

/*
 * Convert a phrase of a jm Part to an Impro-Visor part
 */
public static void convertToImpPart(jm.music.data.Phrase phrase,
                                    MelodyPart partOut,
                                    int precision)
  {
    jm.music.data.Note[] notes = phrase.getNoteArray();
    
    ArrayList<jm.music.data.Note> origNoteArray = new ArrayList<jm.music.data.Note>();
    
    origNoteArray.addAll(Arrays.asList(notes));

    //if(MIDIBeast.mergeMelodyRests) mergeRests();

    // This is a key step in getting melodies to be acceptable to Impro-Visor

    // Handle the case where the phrase does not start immediately.

    double startTime = phrase.getStartTime();
    double time = startTime;
    int slot = 0;

    if( startTime > 0 )
      {
      int restSlots = precision*(int)((BEAT*startTime)/precision);
 
      if( restSlots > 0 )
        {
        partOut.addRest(new Rest(restSlots));
        }
      
      slot += restSlots; 
      }

   noteArray2ImpPart(origNoteArray, time, partOut, slot, precision);
   // returned slot is ignored.
  }


/**
 * Compute the slot corresponding to time given a quantum
 * @param time
 * @param quantum
 * @return 
 */

public static int quantize(double time, int quantum)
  {
    return quantum*(int)Math.ceil((time*FACTOR)/quantum);
  }

public static int quantizeDown(double time, int quantum)
  {
    return quantum*(int)Math.floor((time*FACTOR)/quantum);
  }

/**
 * Convert notes in jm Note array to Impro-Visor notes and add them to melody part.
 * @param origNoteArray
 * @param time
 * @param partOut
 * @param slot
 * @param quantum 
 */

public static int noteArray2ImpPart(ArrayList<jm.music.data.Note> origNoteArray,
                                double time,
                                MelodyPart partOut,
                                int slot,
                                int quantum)
  {
    //System.out.println("\nquantum = " + quantum);
    
    // Counter for the number of notes lost in the quantization process
    
    int notesLost = 0;
    
    // The time from the jMusic part and the slot in the Impro-Visor part
    // are tracked separately:
    //     For jMusic incoming notes' rhythmValues are doubles that are added
    //         for approximate continuous time.
    //     For Impro-Visor outgoing notes' rhythmValues are ints that are added
    //         to keep track of slots.
    
    // jMusic iterator to parse out and return input notes to add one by one to 
    // Improvisor's MelodyPart
    Iterator<jm.music.data.Note> origNotes = origNoteArray.iterator();

    int usedUpToSlot = slot;
    
    while( origNotes.hasNext() )
      {
        // Catch up the number of slots up to quantized time.
        int timeInSlots = quantizeDown(time, quantum);
        slot = Math.max(slot, timeInSlots);
        
        // Get the next Note to be placed, or a rest.
        jm.music.data.Note longNote = origNotes.next();
        
        //System.out.println("time = " + time + " note = " + longNote);
        time += longNote.getRhythmValue();
        
        if( !longNote.isRest() )
          {
          while( time*FACTOR < slot + quantum && origNotes.hasNext() )
            {
            // The note starting at time is placeable in the current slot.
            jm.music.data.Note note = origNotes.next();
            //System.out.println("time = " + time + " note = " + note);
            
            // Account for the time elapsed in that note (or rest).
            time += note.getRhythmValue();
            
            // Incoming rests are ignored, except that time is still advanced
            // on account of them.
            
            if( !note.isRest() )
              {
              // If the current note is longer than the previous long one
              // replace the long note in the slot with the current one.
              if( note.getRhythmValue() > longNote.getRhythmValue() )
                {
                //System.out.println("losing " + longNote);
                longNote = note;
                // The note replaced is thus lost.
                }
              else
                {
                //System.out.println("losing " + note);
                }
              notesLost++;
              }
            }
          // No further notes starting at time is not placeable in the current slot.
          // Therefore we will place the longest placeable note in the current slot.
          int duration = quantize(longNote.getRhythmValue(), quantum);
          int pitch = longNote.getPitch();
        
          // However, we may need to place a rest first, to fill the intervening
          // gap between the end of the previous note and the current slot.
          
          if( slot > usedUpToSlot )
            {
              partOut.addRest(new Rest(slot-usedUpToSlot));
            }
          // Place the new note.
          Note newNote = new Note(pitch, duration);
          //System.out.println("slot = " + slot + " adding pitch = " + pitch 
          // + " duration = " + duration + " " + newNote + "\n");
          partOut.addNote(newNote);
          
          // Update the slot to reflect the end of the note just placed.
          slot += duration;
          usedUpToSlot = slot;
          }
      }

    //System.out.println("notes lost in quantization: " + notesLost);
    return slot;
  }

public int size()
  {
    return parts.length;
  }

/**
 * Finds the ith part.
 */

public jm.music.data.Part getPart(int i)
  {
    return parts[i];
  }


public void mergeRests(ArrayList<jm.music.data.Note> origNoteArray)
  {
    for( int i = 1; i < origNoteArray.size(); i++ )
      {
        if( !(origNoteArray.get(i).isRest()) )
          {
            continue;
          }
        origNoteArray.get(i - 1).setRhythmValue(origNoteArray.get(i).getRhythmValue() 
                                              + origNoteArray.get(i - 1).getRhythmValue());
        origNoteArray.remove(i);
        i--;
      }
  }

/**
 * Convert an Impro-Visor MelodyPart to a jMusic score
 */
public static jm.music.data.Score impMelody2jmScore(MelodyPart partIn)
  {
    jm.music.data.Phrase phrase = new jm.music.data.Phrase();

    int num_slots = partIn.size();
    
    for( int slot = 0; slot < num_slots; slot++ )
      {
        Note impNote = partIn.getNote(slot);
        //System.out.println("note: " + impNote);
        if( impNote != null )
          {
            double duration = ((double)impNote.getRhythmValue())/FACTOR;
            if( impNote.isRest() )
              {
                phrase.addRest(new jm.music.data.Rest(duration));
              }
            else
              {
              phrase.addNote(impNote.getPitch(), duration);
              }
          }
      }

   jm.music.data.Part part = new jm.music.data.Part(phrase);
   return new jm.music.data.Score(part);
  }

}