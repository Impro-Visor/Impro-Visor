/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012-2016 Robert Keller and Harvey Mudd College
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
 * Wenbo Cao contributed to noteArray2ImpPart.
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
 * @param quantum
 * @param precision 
 */
public static void convertToImpPart(jm.music.data.Part melodyPart,
                                    int trackNumber,
                                    MelodyPart partOut,
                                    int quantum[],
                                    int precision)
  {
     convertToImpPart(melodyPart.getPhraseArray()[trackNumber], 
                      partOut,
                      quantum,
                      precision);
  }
    

/*
 * Convert a phrase of a jm Part to an Impro-Visor part
 */
public static void convertToImpPart(jm.music.data.Phrase phrase,
                                    MelodyPart partOut,
                                    int quantum[],
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

   noteArray2ImpPart(origNoteArray, time, partOut, slot, quantum, precision);
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
                                int quantum[],
                                int precision)
  {
    // Counter for the number of notes lost in the quantization proces    
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
    
    // Assuming selected note value is quarter, eighth, sixteenth
    // subdivisions will be 1, 2, 4
    // quantum[0] will be 120, 60, 30
    // quantum[1] (for triplets will be) 80, 40, 20
    // gcd will be 40, 20, 10
    // When either regular notes or triplets are not wanted, the corresponding
    // quantum will be set to a large value such as 480, the number of slots
    // in 4 beats.
    
    int gcd = gcd(quantum[0], quantum[1]);
    int smallerQuantum = Math.min(quantum[0], quantum[1]);
    //If no quantization is specified, skip it altogether
    if (gcd == 1){
        jm.music.data.Note thisNote;
        while (origNotes.hasNext()){
            thisNote = origNotes.next();
            int duration = quantize(thisNote.getRhythmValue(), 1);
            if (thisNote.isRest())
            {
                Rest noteCopy = new Rest(duration);
                partOut.addRest(noteCopy);
            }
            else
            {
                int pitch = thisNote.getPitch();
                Note noteCopy = new Note(pitch,duration);
                partOut.addNote(noteCopy);
            }
        }
        return slot;
    }
    
    // Consider moving reporting to the Quantize Chorus dialog
    // The remainder of the line is printed after quantization
    System.out.print("quanta = " + quantum[0] + " & " + quantum[1] 
                     + ", gcd = " + gcd + ": " );
 
    while( origNotes.hasNext() )
      {
        // Catch up the number of slots up to quantized time.
        int timeInSlots = quantizeDown(time, precision);
        slot = Math.max(slot, timeInSlots);
        
        while( !((slot%quantum[0] == 0) || (slot%quantum[1] == 0)) )
          {
            //System.out.println("skipping slot " + slot);
            slot += 1;
            // use 1 rather than gcd, because otherwise this might not terminate
          }
        
        // Get the next Note to be placed, or a rest.
        jm.music.data.Note longNote = origNotes.next();
        
        //System.out.println("time = " + time + " note = " + longNote);
        time += longNote.getRhythmValue();
        
        // time now represents the time at which longNote nominally ends.
        
        if( !longNote.isRest() )
          {

          // Iterate as long as there are more notes that will fit in
          // the current interval.
            
          while( time*FACTOR < slot + gcd && origNotes.hasNext() )
            {
            // Consider the next note.
            jm.music.data.Note note = origNotes.next();
            
            // Account for the time elapsed in that note (or rest). 
            // We still need to advance the time, even if only one of note
            // or longNote gets used, because that space is used in the
            // input melody.
            
            time += note.getRhythmValue();
            
            if( !note.isRest() )
              {
              // If the next note is longer than the previous long one
              // replace the long note in the slot with the current one.
              // The idea here is the we cannot fit both notes, so the
              // longer one is regarded as the more important.
                
              if( note.getRhythmValue() > longNote.getRhythmValue() )
                {
                //System.out.println("losing " + longNote);
                longNote = note;
                // The note replaced is thus lost.
                }
              else
                {
                // The next note is not longer than longNote, so we just lose it.
                //System.out.println("losing " + note);
                notesLost++;
                }
              }
            } // end of inner while, finding the longest note.
          
          // Place the longest placeable note in the current slot.
          int duration = quantize(longNote.getRhythmValue(), gcd);
          int pitch = longNote.getPitch();
          if (duration < smallerQuantum)
          {
              duration = smallerQuantum;
          }
          else
          {
              //This is to get rid of the extra durations on the notes that
              //have an acceptably long duration but are tied to a shorter note
              duration -= (duration%smallerQuantum);
          }
          
        
          // However, we may need to place a rest first, in case there is a
          // gap between the end of the previous note and the current slot.
          
          if( slot > usedUpToSlot )
            {
              partOut.addRest(new Rest(slot-usedUpToSlot));
            }
          
          // Create and place the new note.
          Note newNote = new Note(pitch, duration);
          //System.out.println("slot = " + slot + " adding pitch = " + pitch 
          // + " duration = " + duration + " " + newNote + "\n");
          partOut.addNote(newNote);
          
          // Update the slot to reflect the end of the note just placed.
          slot += duration;
          usedUpToSlot = slot;
          }
      } // end of outer while

    //System.out.println("notes lost in quantization: " + notesLost);
    return slot;
  }

/**
 * Finds the Greatest Common Denominator of two integers, a & b
 * @param a         first integer
 * @param b         second integer
 * @return int      the GCD of a and b
 */
public static int gcd(int a, int b)
  {
    if( b == 0 )
      {
        return a;
      }
    else
      {
        return gcd(b, a % b);
      }
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