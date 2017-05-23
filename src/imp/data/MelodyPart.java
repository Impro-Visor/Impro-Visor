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

import imp.style.Style;
import imp.midi.MidiSynth;
import imp.midi.MidiImport;
import imp.midi.MidiSequence;
import static imp.Constants.knownNoteValue;
import imp.ImproVisor;
import imp.com.InsertPartCommand;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;


/**
 * An extension of the Part class that contains only Note (or, by extension,
 * Rest) objects.  This is useful to contain drawing functions that are
 * specific to Notes, such as ties and accidentals.
 * @see         Note
 * @see         Rest
 * @see         Part
 * @author      Stephen Jones
 */
@SuppressWarnings("serial")

public class MelodyPart
        extends Part
  {
  /**
   * This is a "fudge-factor" to keep display from getting weird
   * during extraction. It limits the smallest note to a set
   * duration.  If the copied duration is less, then it is artificially
   * set to this value. Otherwise, the duration could be, e.g. 1,
   * which creates a really weird display with 120 sub-divisions, and
   * from which it is hard to recover.
   */
  public static int MIN_EXTRACT_DURATION = BEAT / 4; //

  public static int magicFactor = 4;

  /**
   * Lowest pitch in the MelodyPart
   */
  private int lowestPitch = 127;

  /**
   * Highest pitch in the MelodyPart
   */
  private int highestPitch = 0;

  private static int[] knownRestValue =
          {0, 10, 12, 15, 20, 24, 30, 40, 60, 80, 120, 160, 240, 480};

  //private boolean fudgeEnding = false;
  
  private int noteLength = 60;
  private boolean autoFill = true;
  
  //private int defaultNoteLength = 60;
  
  /* Move to Constants.java
  // 48 is a 5-tuple eigth note...
  private static int[] knownNoteValue =
          {0, 10, 12, 15, 20, 24, 30, 40, 45, 48, 50, 60, 80, 90, 100, 120, 160, 180,
           240, 360, 480
  };
*/
  
  private static int melodyPartNumber = 0;
  
  private String id;
  
  //For use in TransformPanel:
  //null originalVersion indicates that this melody part is the original version
  private Stack <MelodyInContext> originalVersions = new Stack<MelodyInContext>();
  
  public MelodyInContext getRecentVersion(){
      return originalVersions.pop();
  }
  
  public void pushOriginalVersion(MelodyInContext originalVersion){
      originalVersions.push(originalVersion);
  }
  
  public boolean isOriginal(){
      return originalVersions.isEmpty();
  }
  
  public String getMelodyPartId()
    {
      return id;
    }
  
  public void setMelodyPartId(String id)
    {
      this.id = id;
    }

  /**
   * Volume to be explicitly given for one-time melodies, for
   * use with entered melodies.  "Playback" melodies will instead
   * rely on the volume slider in the toolbar.
   */
  public int getSpecifiedVolume()
    {
    return volume;
    }

  /**
   * Creates an empty MelodyPart.
   */
  public MelodyPart()
    {
    super();
    setMelodyPartId("Melody Part # " + melodyPartNumber++);

    this.volume = ImproVisor.getEntryVolume();
    makeConsistent();
    }

  /**
   * Creates a MelodyPart with the given size.
   * @param size      the number of slots in the MelodyPart
   */
  public MelodyPart(int size)
    {
    super(size);
    setMelodyPartId("Melody Part # " + melodyPartNumber++);
    Trace.log(3, "creating new melody part of size " + size);

    // Use the entry volume as the default.  Any non-entered/pasted/generated
    // sample of code will will override this volume setting from the
    // playback slider, but for instant sounds we want the entry volume.
    this.volume = ImproVisor.getEntryVolume();
    makeConsistent();
    }

  /**
   * Creates a MelodyPart from a given string of notes
   * @param notes     the list of notes
   */
  public MelodyPart(String notes)
    {
    super();
    setMelodyPartId("Melody Part # " + melodyPartNumber++);
    this.volume = ImproVisor.getEntryVolume();
    String[] noteList = notes.split(" ");
    for( int i = 0; i < Array.getLength(noteList); ++i )
      {
      addNote(NoteSymbol.makeNoteSymbol(noteList[i]).toNote());
      }
    makeConsistent();
    }

/**
 * Adds a Note to the end of the MelodyPart, extending its length.
 *
 * @param note the Note to add
 */
public void addNote(Note note)
  {
    // Ideally, if the note being added is a rest, and the last note is a rest,
    // the new rest should be merged in with the existing one. But my attempt
    // at this failed for some reason.

    if( note.isRest() )
      {
      }
    else
      {
        if( note.getPitch() < lowestPitch )
          {
            lowestPitch = note.getPitch();
          }
        if( note.getPitch() > highestPitch )
          {
            highestPitch = note.getPitch();
          }
      }
    addUnit(note);
    //System.out.println("melody part gets note " + note);
  }

  /**
   * Adds a Rest to the end of the MelodyPart, extending its length.
   * @param rest      the Rest to add
   */
  public void addRest(Rest rest)
    {
    addUnit(rest);
    }

  /**
   * Sets the specified slot to the given note.
   * @param slotIndex         the index of the slot to set at
   * @param note              the note to put at the slot index
   */
  public void setNote(int slotIndex, Note note)
    {
    if( slotIndex < 0 )
      {
        slotIndex = 0;
      }
    if( note != null && note.nonRest() )
      {
      int currentMeasure = slotIndex / measureLength;
      
      int stopIndex;
      if (autoFill)  
        stopIndex = measureLength * (currentMeasure + 2);
      else
        stopIndex = slotIndex + noteLength;

      if( stopIndex < size && getNextIndex(slotIndex) > stopIndex )
        {
        setRest(stopIndex);
        }

      if( note.getPitch() < lowestPitch )
        {
        lowestPitch = note.getPitch();
        }
      else if( note.getPitch() > highestPitch )
        {
        highestPitch = note.getPitch();
        }
      }
    setUnit(slotIndex, note);
    }

  /**
   * Just like setNote(), but without the stopIndex check.
   * @param slotIndex         the index of the slot to set at
   * @param note              the note to put at the slot index
   */
  public void setNoteFromCapture(int slotIndex, Note note)
    {
    if( slotIndex < 0 )
      {
        slotIndex = 0;
      }
    if( note != null && note.nonRest() )
      {
      if( note.getPitch() < lowestPitch )
        {
        lowestPitch = note.getPitch();
        }
      else if( note.getPitch() > highestPitch )
        {
        highestPitch = note.getPitch();
        }
      }
    newSetUnit(slotIndex, note);
    }

  /**
   *
   */
  public synchronized void setNoteAndLength(int slotIndex, Note note,
                                              Notate parent)
    {
    int origLength = note.getRhythmValue();

    if( slotIndex + origLength >= size )
      {
      int measureLength = getMeasureLength();
      double lengthInMeasures =
              (slotIndex + origLength - size) / (double)measureLength;
      int measuresToAdd = (int)Math.ceil(lengthInMeasures);

      Part newMeasures = new Part(measuresToAdd * measureLength);
      (new InsertPartCommand(parent, this, size, newMeasures)).execute();
      }

    // make room if needed by setting rests at the locations of notes that will be covered by the new note
    setRest(slotIndex);
    int freeSlots = getFreeSlots(slotIndex);
    int nextIndex = slotIndex + freeSlots;
    while( freeSlots < origLength )
      {
      setRest(slotIndex + freeSlots);
      freeSlots = getFreeSlots(slotIndex);
      }

    synchronized( slots )
      {
      // merge the rests
      mergeFreeSlots(slotIndex);

      // add the note
      setNote(slotIndex, note);
      }

    if( note.getRhythmValue() > origLength )
      {
      // cut the note length to correct length
      setRest(slotIndex + origLength);
      }
    }


  /**
   * Sets the specified slot to the given rest
   * @param slotIndex         the index of the slot to set at
   * @param rest              the rest to put at the slot index
   */
  public void setRest(int slotIndex, Rest rest)
    {
    Trace.log(2, "setRest at " + slotIndex + " to " + rest);
    setUnit(slotIndex, rest);
    }


  /**
   * sets a Rest at the given slot index
   * @param slotIndex         the index of the slot to set at
   */
  public void setRest(int slotIndex)
    {
    setRest(slotIndex, new Rest());
    }


  /**
   * Returns the note at the given slot index
   * @param slotIndex         the index of the note to get
   * @return Note             the Note at the given index
   */
  public Note getNote(int slotIndex)
    {
    return (Note)getUnit(slotIndex);
    }

  /**
   * return the first note (non-rest).
   * If there is none, returns null.
   * @return 
   */
  
public Note getFirstNote()
  {
    PartIterator it = iterator();
    while( it.hasNext() )
      {
        Note note = (Note)it.next();
        if( note.nonRest() )
          {
            return note;
          }
      }
    return null;
  }


public int getLastNoteIndex()
{
    int index = this.getPrevIndex(this.getSize());

    Note n = this.getNote(index);

     while(n.isRest()) {
         index = this.getPrevIndex(index);
         n = this.getNote(index);
     }

    return index;
}

/**
 * Returns the Note sounding at this index.
 * @param slotIndex         the index to check at
 * @return Note            the note sounding at the index
 */
public Note getCurrentNote(int slotIndex) {
    if( slotIndex < 0 || slotIndex >= size )
    {
      return null;
    }
    return getNote(getCurrentNoteIndex(slotIndex));
}

/**
     * Returns the index of the Chord sounding at this index.
     * @param slotIndex         the index to check at
     * @return int              the index of the sounding chord
     */
    public int getCurrentNoteIndex(int slotIndex) {
        if(getNote(slotIndex) != null)
          {
            return slotIndex;
          }
        else
          {
            return getPrevIndex(slotIndex);
    }
    }

  /**
   * Returns the Note after the indicated slot index.
   * @param slotIndex         the index to start searching at
   * @return Note             the Note after the given index
   */
  public Note getNextNote(int slotIndex)
    {
    return (Note)getNextUnit(slotIndex);
    }


  /**
   * Returns the Note before the indicated slot index.
   * @param slotIndex         the index to start searching at
   * @return Note             the Note before the given index
   */
  public Note getPrevNote(int slotIndex)
    {
    return (Note)getPrevUnit(slotIndex);
    }


  /**
   * Gets the lowest pitch in the MelodyPart
   * @return int              lowest pitch
   */
  public int getLowestPitch()
    {
    return lowestPitch;
    }


  /**
   * Gets the highest pitch in the MelodyPart
   * @return int              highest pitch
   */
  public int getHighestPitch()
    {
    return highestPitch;
    }


  /**
   * Returns the number of slots from the index to the next Note.
   * @param index     the index to get the free slots
   * @return int      the number of free slots
   */
  public int getFreeSlots(int index)
    {
    if( getNote(index) != null && getNote(index).nonRest() )
      {
      return 0;
      }

    if( index < 0 )
      {
        return size;
      }
    int nextIndex = getNextIndex(index);
    while( nextIndex < size - 1 )
      {
      if( getNote(nextIndex) != null &&
              getNote(nextIndex).nonRest() )
        {
        return nextIndex - index;
        }
      nextIndex = getNextIndex(nextIndex);
      }
    return size - index;
    }


  public int getFreeSlotsFromEnd()
    {
    int slotIndex = getSize() - 1;
    int restSlots = 0;
    Note current;

    while( slotIndex >= 0 )
      {
      current = getNote(slotIndex);
      // if we find a unit that is not a rest, we return the rest slots found so far
      if( current != null )
        {
        if( current.nonRest() )
          {
          return restSlots;
          }
        restSlots += current.getRhythmValue();
        }
      slotIndex--;
      }

    if( restSlots != getSize() )
      {
      Trace.log(0,
              "Possible error calculating number of rests from the end of the piece in Part.getRestSlotsFromEnd");
      }
    return restSlots;
    }


  public synchronized void mergeFreeSlots(int index)
    {
    if( index < 0 )
      {
        return;
      }
    if( getNote(index) == null )
      {
      index = getPrevIndex(index);
      if( getNote(index).nonRest() )
        {
        return;
        }
      }
    else if( getNote(index).nonRest() )
      {
      return;
      }

    Note firstNote = getNote(index);

    int nextIndex = getNextIndex(index);
    Note nextNote = getNote(nextIndex);

    while( nextIndex < size - 1 )
      {
      if( nextNote != null && nextNote.nonRest() )
        {
        return;
        }

      slots.set(nextIndex, null);
      unitCount--;

      firstNote.setRhythmValue(firstNote.getRhythmValue() + nextNote.getRhythmValue());

      nextIndex = getNextIndex(nextIndex);
      nextNote = getNote(nextIndex);
      }
    }


  /**
   * Returns an exact copy of this Part
   * @return Part   copy
   */
  @Override
  public MelodyPart copy()
    {
      return copy(0);
    }

  /**
   * Returns an exact copy of this Part from startingIndex
   * @return Part   copy
   */

  public MelodyPart copy(int startingIndex)
{
  return copy(startingIndex, size-1);
}

  /**
   * Returns an exact copy of this Part from startingIndex to endingIndex
   * @return
   */

public MelodyPart copy(int startingIndex, int endingIndex)
{
      int newSize = endingIndex + 1 - startingIndex;

      if( newSize <= 0 )
        {
          return new MelodyPart(0);
        }

      int newUnitCount = 0;

      try
      {
      MelodyPart newPart = new MelodyPart(newSize);
      newPart.setMelodyPartId(newPart.getMelodyPartId() + " (copied from " + getMelodyPartId() + ")");
      //System.out.println("melodyPart with size " + newPart.getSize() + " start = " + startingIndex + ", end = " + endingIndex);
       int i = startingIndex;

       if( slots.get(startingIndex) == null )
         {
           // Find first non-null slot, and place a rest at
           // startingIndex.

           i++;

           for( ; i <= endingIndex; i++ )
             {
               if( slots.get(i) != null )
                 {
                   break;
                 }
             }

           newPart.slots.set(0, new Rest(i - startingIndex));

           unitCount = 1;

         }

       for( ; i <= endingIndex && i < size ; i++ )
        {
          Unit unit = slots.get(i);

          if( unit != null )
            {
            unit = unit.copy();
            newUnitCount++;
            if( i + unit.getRhythmValue() - 1 > endingIndex )
              {
              // In case the last unit extends beyond endingIndex;
              unit.setRhythmValue(endingIndex - i + 1);
              }
            }
          newPart.slots.set(i - startingIndex, unit);

        }

    newPart.unitCount = newUnitCount;
    newPart.title = title;
    newPart.volume = volume;
    newPart.keySig = keySig;
    newPart.metre[0] = metre[0];
    newPart.metre[1] = metre[1];
    newPart.beatValue = beatValue;
    newPart.measureLength = measureLength;
    newPart.swing = swing;
    newPart.instrument = instrument;
    return newPart;
      }
    catch( Error e )
      {
        ErrorLog.log(ErrorLog.FATAL,
                     "Not enough memory to copy part of size " + newSize + ".");
        return null;
      }
    }


      public int getPitchSounding(int index) {
        //if there is a note struck at the index, return its pitch
        Note curr = this.getNote(index);
        if (curr != null) {
            return curr.getPitch();        //otherwise look for the last pitch
        }
        int prevIndex = 0;
        try {
            prevIndex = this.getPrevIndex(index);
        } catch (Exception e) {
            System.out.println("error in MelodyPart.getPitchSounding: index: " + index);
            //e.printStackTrace();
        }
        Note prevNote = this.getNote(prevIndex);
        if (prevNote.isRest()) {
            return -1;        //if previous note is still sounding, return its pitch
        }
        if (prevNote.getRhythmValue() > (index - prevIndex)) {
            return prevNote.getPitch();
        //default case
        }
        return -1;
    }


  /**
   * Returns a reverse copy of this Part
   * @return Part   copy
   */
  public MelodyPart copyReverse()
    {
    Trace.log(3, "copying in  reverse melody part of size " + size);

    MelodyPart newPart = extractReverse(0, size);

    newPart.unitCount = unitCount;
    newPart.title = title;
    newPart.volume = volume;
    newPart.keySig = keySig;
    newPart.metre[0] = metre[0];
    newPart.metre[1] = metre[1];
    newPart.beatValue = beatValue;
    newPart.measureLength = measureLength;
    newPart.swing = swing;
    newPart.instrument = instrument;
    return newPart;
    }

  public void dump(PrintStream out)
    {
    PartIterator i = iterator();
    try
      {
      while( i.hasNext() )
        {
        Note note = (Note)i.next();
        out.print(note.getRhythmValue());
        out.print(" ");
        out.print(note.getPitch());
        out.println();
        }
      }
     catch(Exception e)
      {
      // Both NullpointerExecption and ArrayIndexOutOfBoundsException have occurred
      // here. This should be investigated.
      // For now, let's use it as an early exit.
      }
    }

  /**
   * Changes Notes in the Part so ties are proper and the Part
   * can be drawn.
   */
  public void makeTies()
    {
    PartIterator i = iterator();
    while( i.hasNext() )
      {
      int j = i.nextIndex();

     try {
      Note n = (Note)i.next();
      tieThis(j, n, 0);
     }
     catch(Exception e)
       {
         // RK 6/11/2010 Getting exception here on MIDI entry.
         // This might not be the best solution, however.
       return;
       }
      }
    }

  /**
   * This is a recursive routine, originally created by Martin Hunt, I think.
   @param slotIndex
   @param note
   @param level
   */

  public void tieThis(int slotIndex, Note note, int level)
    {
    if( note == null )
      {
      // Don't know why we are getting here, but maybe this should be looked into - Martin, 2006-07-12
      Trace.log(2,
              "Warning: tieThis attempted to tie a null note.  This is probably a bug.");
      return;
      }

    int rhythmValue = note.getRhythmValue();

    // Note: we need rest index even if it is a note, for the purpose of
    // non-tied exit.

    int knownRestIndex = java.util.Arrays.binarySearch(knownRestValue, rhythmValue);
    int knownNoteIndex = java.util.Arrays.binarySearch(knownNoteValue, rhythmValue);
    int knownTupletIndex = java.util.Arrays.binarySearch(knownTupletValue, rhythmValue);

    int knownRhythmIndex = note.isRest() ? knownRestIndex : knownNoteIndex;

    // Use integer division to get the start of the current measure and the
    // next measure.
    int currentMeasure = (slotIndex / measureLength) * measureLength;
    int nextMeasure = currentMeasure + measureLength;

    // Use integer division to get the start of the current beat and the next
    // beat.
    int currentBeat = (slotIndex / beatValue) * beatValue;
    int nextBeat = currentBeat + beatValue;

    int firstNoteRV = -1;
    int secondNoteRV = -1;

    if( slotIndex + rhythmValue <= nextMeasure  && (knownTupletIndex >= 0 ) )
      {
      return;
      }

     // If the note starts on an off beat and crosses over the next beat, then
    // we want to cut off the note at the beat, and create a new note with the
    // residual value.

    // What about notes such as tuplets? Not sure how it works then.

    if( slotIndex % beatValue != 0 && slotIndex + rhythmValue > nextBeat )
      {
      firstNoteRV = nextBeat - slotIndex;
      secondNoteRV = rhythmValue - firstNoteRV;

      //DEBUG
      //if(note.getPitch() != Note.REST)
      //    System.out.println("BEAT: " + firstNoteRV + " " + secondNoteRV);
      }

    // If the note crosses a barline, then we cut the note off at the end of the
    // measure and create a new note with the residual duration.
    // Ok.

    else if( slotIndex + rhythmValue > nextMeasure )
      {
      firstNoteRV = nextMeasure - slotIndex;
      secondNoteRV = rhythmValue - firstNoteRV;

      //DEBUG
//            if(note.getPitch() != Note.REST)
//                System.out.println("MEASURE: " + firstNoteRV + " " + secondNoteRV);
      }

    // If the note is longer than a beat, and the next note is not on a beat ...
    // Do we always want to do this? Why?

    else if( rhythmValue > beatValue && (slotIndex + rhythmValue) % beatValue != 0 )
      {
      secondNoteRV = rhythmValue % beatValue;
      firstNoteRV = rhythmValue - secondNoteRV;

      //DEBUG
//            if(note.getPitch() != Note.REST)
//                System.out.println("Overlap: beat: " + beatValue + ", 1: " + firstNoteRV + ", 2: " + secondNoteRV + ", total: " + rhythmValue);
      }

    // special case:  triplets:  value: 5/8 * BEAT
    // Why this case ??

    else if( !note.isRest() && rhythmValue < beatValue )
      {
      if( knownRhythmIndex < 0 )
        {
        //System.out.println("Resolving note (" + rhythmValue + "), beatValue: " + beatValue);

        knownRhythmIndex = -knownRhythmIndex - 2;

        // if it's less than a beat, try to split it evenly
        if( rhythmValue < beatValue )
          {
          for( int i = 2; i < 20; i++ )
            {
            int div = beatValue / i;
            // if partitioning a BEAT into i pieces evenly divides the rhythmValue...
            if( (rhythmValue / div) * div == rhythmValue )
              {
              firstNoteRV = div * (i / 2);    // this could be changed to simply div, this would shift the ties so the smallest tied section in a tied tuplet note comes first
              secondNoteRV = rhythmValue - firstNoteRV;
              break;
              }
            }
          }
        }
      }

    // for now, only apply the next bit of code to rests...
    // If the note is not a known value, we need to split it into known
    // values
    else if( note.isRest() && (knownRhythmIndex =
            java.util.Arrays.binarySearch(knownRestValue, rhythmValue)) < 0 )
      {
      //System.out.println("Resolving note (" + rhythmValue + "), beatValue: " + beatValue);

      // if it's less than a beat, try to split it evenly
      if( rhythmValue < beatValue )
        {
        for( int i = 2; i < 20; i++ ) // Why the magic number 20 ???
          {
          int div = beatValue / i;
          // System.out.println(" -> "+i+" / "+div);
          // if partitioning a BEAT into i pieces evenly divides the rhythmValue...
          if( (rhythmValue / div) * div == rhythmValue )
            {
            firstNoteRV = div;
            secondNoteRV = rhythmValue - div;
            break;
            }
          }
        }
      }

    // If the note is longer than a whole note, we need to split it up (but only if it's
    // an actual note -- this keeps with the convention that a full measure of rests only
    // has one whole rest in it).
    if( (firstNoteRV > measureLength || (firstNoteRV == -1 && rhythmValue > measureLength)) && !note.isRest() )
      {
      firstNoteRV = measureLength;
      secondNoteRV = rhythmValue - firstNoteRV;
      }

    /* if none of the other cases get caught, this should probably be the
     * LAST check... since the other cases are more important, and this is
     * a final verification
     *
     * Check that each note is a known valid value
     *
     * Notes larger than a beatValue get split using the largest known note
     *    that is a multiple of a beatValue
     *
     * Notes smaller than a beatValue are split using the largest beatValue
     */
    // Choose one of two arrays:

    int[] knownRhythmValue =
            note.isRest() ? knownRestValue : knownNoteValue;

    // Get the duration of the note or rest;
    // If if it is a note and too long, then we need to tie.


//        if(note.nonRest())
//            System.out.println(rhythmValue);

    if( firstNoteRV == -1 && (knownRhythmIndex =
            java.util.Arrays.binarySearch(knownRhythmValue, rhythmValue)) < 0 )
      {
      // not a known rhythm value, so figure out the index of the largest known rhythmValue
      // not larger than the current RhythmValue:
      //  "-knownRhythmIndex - 1" is the first index with value greater than rhythmValue
      // so "-knownRhythmIndex - 2" is the first index with largest value not greater than rhythmValue

      knownRhythmIndex = -knownRhythmIndex - 2;

//            System.out.println("Note of value " + rhythmValue);

      // this is the secondNoteRV... assuming we continue to split
      int remainder = rhythmValue - knownRhythmValue[knownRhythmIndex];

      if( rhythmValue > beatValue )
        {
        // if the note is greater than a beat, we take the largest multiple of the beat
        for( int i = knownRhythmIndex; i > 2; i-- )
          {
          if( (knownRhythmValue[i] / beatValue) * beatValue == knownRhythmValue[i] )
            {
            firstNoteRV = (knownRhythmValue[i] / beatValue) * beatValue;
            secondNoteRV = rhythmValue - firstNoteRV;
            break;
            }
          }
        }
      else
        {
        // Only continue with the split if it doesn't lead to an extremely small note (the remainder has to be at least as large
        // as the first known note
        if( knownRhythmIndex > 0 && remainder >= knownRhythmValue[1] )
          {
          firstNoteRV = knownRhythmValue[knownRhythmIndex];
          secondNoteRV = remainder;

          // I don't think this line ever gets called cause it was
          // not commented out... and I never saw it print anything
          // System.out.println("Note: " + firstNoteRV + " " + secondNoteRV);
          }
        else
          {
          //FIX: ErrorLog.log(ErrorLog.WARNING, "Possible rendering error: small rhythm value found: " + rhythmValue, false);
          }
        }
      }

    // If none of the above cases are hit, we don't want to tie anything.
    if( firstNoteRV <= 0 || secondNoteRV <= 0 )
      {
      return;
      }

    // If it's the first note, set it as the first tie.
    if( !note.isTied() )
      {
      note.setTie(true);
      note.setFirstTie(true);
      }

    // Set the note to the truncated duration we calculated above.
    note.setRhythmValue(firstNoteRV);

    // Create the new note as a copy of the first with the residual duration.
    Note newNote = note.copy();
    newNote.setFirstTie(false);
    newNote.setAccidental(Accidental.NOTHING);
    newNote.setRhythmValue(secondNoteRV);


    // Add the new note to our array at the appropriate position.
    slots.set(slotIndex + firstNoteRV, newNote);
    unitCount++;

    tieThis(slotIndex, note, level + 1);

    // Check to see if the new note needs to be tied.
    tieThis(slotIndex + firstNoteRV, newNote, level + 1);
    }

  /**
   * Edits the Accidental field on each Note to represent what should be
   * drawn.
   */
  public void makeAccidentals()
    {
    ArrayList<Accidental> accidentalVector = getKeySigVector();

    // Go through each slot so that we can reset the accidentalVector
    // at the start of each measure
    for( int i = 0; i < size; i++ )
      {
      int thisBeat = beatValue * (i / beatValue);

      // Reset the accidentalVector at the start of each measure
      if( thisBeat == i && (i / beatValue) % metre[0] == 0 )
        {
        accidentalVector = getKeySigVector();
        }

      Note note;

      // No accidental stuff in an empty slot or a rest
      if( getUnit(i) == null || ((Note)getUnit(i)).isRest() )
        {
        continue;
        }
      note = (Note)getUnit(i);

      int pitch = note.getPitch();
      
      if( pitch < 0 || pitch > 127 )
        {
          // This has happened. It is unclear how, but it seems to be 
          // associated with the syncopation grammr.
          System.out.println("out-of-range pitch " + pitch + " in makeAccidentals");
          return;
        }

      // The easiest way to do this is to adjust the pitch so that
      // it is on the letter, independent of accidental
      if( note.getAccidental() == Accidental.SHARP )
        {
        note.setDrawnPitch(pitch - 1);
        }
      else if( note.getAccidental() == Accidental.FLAT )
        {
        note.setDrawnPitch(pitch + 1);
        }
      else
        {
        note.setDrawnPitch(pitch);
        }

      // If the note's accidental has already been set
      // correctly by a previous note in the measure, or
      // by the key signature, draw nothing
      if( note.getAccidental() == accidentalVector.get(note.getDrawnPitch()) )
        {
        note.setAccidental(Accidental.NOTHING);
        }

      // If the note's accidental needs to be drawn, leave
      // the accidental field as it is and make the accidentalVector
      // reflect that note's new default accidental
      else
        {
        accidentalVector.set(note.getDrawnPitch(), note.getAccidental());
        }
      }
    }
  
    public long render(MidiSequence seq,
            int ch,
            long time,
            Track track,
            int transposition,
            int endLimitIndex)
            throws InvalidMidiDataException {
        return render(seq,
                ch,
                time,
                track,
                transposition,
                endLimitIndex,
                false);
    }

  /**
   * Creates a new Track for this Part on the specified channel, and adds
   * it to the specified Sequence, called by Score.render.
   * @param seq     the Sequence to add a Track to
   * @param ch      the channel to put the Track on
   */
  public long render(MidiSequence seq,
                     int ch,
                     long time,
                     Track track,
                     int transposition,
                     int endLimitIndex,
                     boolean isTradingMelody)
          throws InvalidMidiDataException
    {

    boolean sendBankSelect = Preferences.getMidiSendBankSelect();
    // to trace sequencing:
    //System.out.println("Sequencing MelodyPart on track " + track + " time = " + time + " endLimitIndex = " + endLimitIndex);
    //System.out.println("MelodyPart: rendering - isTradingMelody = "+ isTradingMelody);

    PartIterator i = iterator();

    // Note that you can't have two instruments playing on the same
    // channel at the same time.  So we can never have more than 16
    // instruments playing at once, and we should add code somewhere
    // to make sure instruments are sorted to the right channels

    // Select Bank 0 before program change.
    // Not sure this is correct. Check before releasing
    // both here and in Style.java

    // set program change. Do we need this for every call?
    // We need this for trading, as long as more than one instrument
    // is playing on the same track.

    // track.add(MidiSynth.createProgramChangeEvent(ch, instrument, time));

    if( sendBankSelect )
      {
      track.add(MidiSynth.createBankSelectEventMSB(0, time, isTradingMelody));
      track.add(MidiSynth.createBankSelectEventLSB(0, time, isTradingMelody));
      }

    // the absolute time is advanced and returned by the next render
    // function

    endLimitIndex *= magicFactor;

    while( i.hasNext() && Style.limitNotReached(time,  endLimitIndex) )
      {
      track.add(MidiSynth.createProgramChangeEvent(ch, instrument, time));
      Note note = (Note)i.next();
      time = note.render(seq.getSequence(), track, time, ch, transposition, sendBankSelect);
      }

    return time;
    }


  /**
   * Returns an ArrayList of Accidentals corresponding to all possible pitches.
   * The weird thing about this is that we only end up marking the
   * accidental independent pitches: C1, D1, E1, G4, A4, etc.
   * This is to make an easy mapping between the pitches as they are
   * stored for playback and elements of this Vector.
   * @return ArrayList<Accidental>       the accidental vector
   */
  public ArrayList<Accidental> getKeySigVector()
    {
    ArrayList<Accidental> keySigVector = new ArrayList<Accidental>(TOTALPITCHES);

     for( int i = 0; i < TOTALPITCHES; i++ )
      {
        keySigVector.add(Accidental.NOTHING);
      }

    switch( keySig )
      {
      case CBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case GBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case DBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case ABMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case EBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case BBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case FMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case CMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          keySigVector.set(i, Accidental.NATURAL);
          }
        break;

      case GMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODF )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case DMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case AMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case EMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case BMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case FSMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;

      case CSMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Accidental.NATURAL);
            }
          }
        break;
      }
    return keySigVector;
    }

//  /**
//   * Sets whether in extracting a melodypart, the ending should be truncated
//   * if necessary
//   * @param b   boolean whether or not to truncate the endings
//   */
//  public void truncateEndings(boolean b) {
//      fudgeEnding = b;
//  }

/**
   * Returns a MelodyPart that contains the Units within the slot range specified.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */

  @Override
  public MelodyPart extract(int first, int last)
    {
    return extract(first, last, false);
    }
      
  /**
   * Returns a MelodyPart that contains the Units within the slot range specified.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @param fudgeEnding cut off the last note to fit into size last - first
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */

    public MelodyPart extract(int first, int last, boolean fudgeEnding)
    {
    return extract(first, last, fudgeEnding, false);
    }
    
  /**
   * Returns a MelodyPart that contains the Units within the slot range specified.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @param fudgeEnding cut off the last note to fit into size last - first
   * @param fudgeStart  get the first Note previously played and fit it into
   *                    new melody part
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */

    public MelodyPart extract(int first, 
                              int last, 
                              boolean fudgeEnding, 
                              boolean fudgeStart)
    {
    //System.out.println("extract melody from " + first + " to " + last);
    MelodyPart newPart = new MelodyPart();
    int i = first;
    int lastUnitIndex = first;
    if( getUnit(first) == null )
      {
        // Create a rest up to the first non-null unit
        for( i = first + 1; i <= last; i++ )
          {
          if( getUnit(i) != null )
            {
            break;
            }
          }
        // Add the synthetic rest to the new part
        // equivalent to space before the first actual note
        if(fudgeStart)    
        {
            if(getCurrentNote(first) != null)
            {
                Note prevNote = getCurrentNote(first).copy();
                prevNote.setRhythmValue(i-first);
                newPart.addNote(prevNote);
            }
            else
            {
                newPart.addNote(new Rest(i - first));
            }
        }
        else
        {

          newPart.addNote(new Rest(i - first));
        }
      }

    // Add other Units

    for(; i <= last; i++ )
      {
      if( getUnit(i) != null )
        {
        newPart.addUnit((Note)getUnit(i).copy());
        lastUnitIndex = i;
        }
      }

    if(fudgeEnding) {
        // Truncate the last Unit, if necessary.
        Unit lastUnit = newPart.getPrevUnit(newPart.getSize());

        if( lastUnit != null && lastUnitIndex + lastUnit.getRhythmValue() > last)
        {
            int oldRhythmValue = lastUnit.getRhythmValue();
            lastUnit.setRhythmValue(last + 1 - lastUnitIndex);
            int amountTruncated = oldRhythmValue - lastUnit.getRhythmValue();
            //set the size of newPart to account for the truncation
            newPart.setSize(newPart.size() - amountTruncated);
        }
    }


    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but in reverse order.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractReverse(int first, int last)
    {
    MelodyPart newPart = new MelodyPart();
    for( int i = last; i >= first; i-- )
      {
      if( getUnit(i) != null )
        {
        newPart.addUnit(getUnit(i).copy());
        }
      }
    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but time-warped by num/denom
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractTimeWarped(int first, int last, int num, int denom)
    {
    MelodyPart newPart = new MelodyPart();
    Trace.log(2,
            "extractTimeWarped from " + first + " to " + last + " by " + num + "/" + denom);
    for( int i = first; i < last; i++ )
      {
      if( getUnit(i) != null )
        {
        Unit newUnit = getUnit(i).copy();
        newUnit.setRhythmValue((newUnit.getRhythmValue() * num) / denom);
        newPart.addUnit(newUnit);
        }
      }
    newPart.addUnit(new Rest(15));
    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but inverted.
   * Inversion is with respect to the highest and lowest notes in the series
   * and is done by ordering the notes, not using a mathematical formula.
   *
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractInverse(int first, int last)
    {
    // insertion sort pitches into sorted linked list

    LinkedList<Note> ordered = new LinkedList<Note>();
    for( int i = first; i <= last; i++ )
      {
      Unit unit = getUnit(i);
      if( unit != null && unit instanceof Note )
        {
        Note note = (Note)unit;
        int pitch = note.getPitch();
        if( pitch != REST )
          {
          boolean found = false;
          ListIterator<Note> it = ordered.listIterator(0);
          int position = 0;
          while( !found && it.hasNext() )
            {
            Note element = it.next();
            int thatPitch = element.getPitch();
            if( pitch < thatPitch )
              {
              ordered.add(position, note);
              found = true;
              }
            else if( thatPitch == pitch )
              {
              found = true;
              }
            position++;
            }
          if( !found )
            {
            // add at end
            ordered.add(note);
            }
          }
        }
      }

    Object orderedArray = ordered.toArray();

    int length = Array.getLength(orderedArray);

    MelodyPart newPart = new MelodyPart();

    // invert each element

    for( int i = first; i <= last; i++ )
      {
      if( getUnit(i) != null )
        {
        Note note = (Note)getUnit(i);
        Note newNote = null;
        int pitch = note.getPitch();
        if( pitch == REST )
          {
          newNote = note.copy();
          }
        else
          {
          boolean found = false;
          // Find this notes pitch in the array
          for( int j = 0; !found && j < length; j++ )
            {
            if( ((Note)Array.get(orderedArray, j)).getPitch() == pitch )
              {
              Note inverse = (Note)Array.get(orderedArray, length - 1 - j);

              newNote = inverse.copy();

              // New note has the same rhythm value as old,
              // but the pitch is inverted.

              newNote.setRhythmValue(note.getRhythmValue());
              found = true;
              break;
              }
            }
          assert (found);
          }
        newPart.addUnit(newNote);
        }
      }
    return newPart;
    }

 /**
  * returns the last note in the melodypart that is not a rest
  * @return 
  */
  
 public Note getLastNote() {
     int tracker = this.getPrevIndex(size);

     Note n = this.getNote(tracker);

     while(n.isRest()) {
         tracker = this.getPrevIndex(tracker);
         n = this.getNote(tracker);
     }

     return n;
 }


     /**
     * The only current use is in LickgenFrame.
     * @param selectionStart
     * @param numSlots
     * @returns whether a melody is empty from selectionStart to selectionStart + numSlots
     */
     public boolean melodyIsEmpty(int selectionStart, int numSlots) {
        if(selectionStart < 0) return false;
        int tracker = this.getPrevIndex(selectionStart);
        Note n = this.getNote(tracker);
        //if note is held from before, it's not empty
        if(n != null && (!n.isRest()) && n.getRhythmValue() > selectionStart - tracker) {
            return false;
        }
        //if there is a note at the start, it's not empty
        n = this.getNote(selectionStart);
        if(n != null && (!n.isRest()) )
            return false;

        //now check for notes in the rest
        tracker = this.getNextIndex(selectionStart);
        while(tracker < selectionStart + numSlots) {
            n = this.getNote(tracker);
            tracker = this.getNextIndex(tracker);
            if(n == null) return true; //there is no next note
            if(!n.isRest()) return false;
        }

        return true;
    }

public int getInitialBeatsRest()
  {
  int count = 0;
  for( Unit note: slots )
    {
      if( note != null && !((Note)note).isRest() )
        {
          break;
        }
      count++;
    }
   return count/BEAT;
   }

 @Override
 public String toString()
  {
  StringBuilder buffer = new StringBuilder();

  int n = slots.size();
  
  buffer.append("MelodyPart with ");
  buffer.append(n);
  buffer.append(" slots, ");
  buffer.append(unitCount);
  buffer.append(" notes and rests: ");

  int currentVolume = 127;

  for( int i = 0; i < n; i++ )
    {
    Note note = (Note)slots.get(i);
    if( note != null )
      {
      //System.out.println("note volume = " + note.getVolume());
      if( note.getVolume() != currentVolume )
        {
          currentVolume = note.getVolume();
          buffer.append("v");
          buffer.append(currentVolume);
          buffer.append(" ");
        }
      buffer.append(note.toLeadsheet());
      buffer.append(" ");
      }
    }
  return buffer.toString();
  }

 public void printNotesAtSlots(){
     int n = slots.size();
     for(int i = 0; i < n; i++){
         Note note = (Note) slots.get(i);
         if(note != null){
             System.out.println("Slot: "+i+" contains "+note);
         }
     }
 }
 
 /**
  * Get the melody as a list of just note symbols in leadsheet notation
  * @return 
  */
 
public ArrayList<Integer> getArrayListForm()
  {
  ArrayList<Integer> result = new ArrayList<Integer>();

  int n = slots.size();

  for( int i = 0; i < n; i++ )
    {
    Note note = (Note)slots.get(i);
    if( note != null )
      {
        result.add(note.getPitch());
        result.add(note.getRhythmValue());
      }
    }
  return result;
  }


/**
 * getSyncVector gets an array of 1's and 0's from a MelodyPart
 * representing note onsets, for the purpose of a synchronization
 * algorithm being implemented by David Halpern. Currently every slotSpacing-th
 * slot is polled to see whether there is a note onset there. If so,
 * a 1 is returned in that position. Otherwise a 0 is returned.
 * For this purpose, rests are not considered to be notes.
 * For example, slotSpacing might be 15.
 * @param slotSpacing
 * @param maxSize
 * @return
 */

public int[] getSyncVector(int slotSpacing, int maxSize)
  {
    int n = Math.min(size(), maxSize);
    int[] result = new int[(int)Math.ceil(n/slotSpacing)];
    for( int i = 0, j = 0; i < n; i+= slotSpacing, j++ )
      {
        Note note = (Note)slots.get(i);
        result[j] = note != null && note.nonRest() ? 1 : 0;
      }
    return result;
  }

public int getNoteLength()
{
    return noteLength;
}

public void setNoteLength(int len, boolean triplet, boolean dotted)
{
    int length = len;
    if(triplet) {
        length = (length * 2) / 3;
    } else if(dotted) {
        length = (length * 3) / 2;
    }
    noteLength = length;
}

public boolean getAutoFill()
{
    return autoFill;
}

public void setAutoFill(boolean fill)
{
    autoFill = fill;
}

        /**
     * prints the pitch, slot, duration, and cost of each Note in a MelodyPart
     * and creates a LinkedList of these Notes
     * @return LinkedList <Note> result
     */
    public LinkedList<Note> getNotes()
    {
        LinkedList<Note> result = new LinkedList<Note>();
        int slot = 0;
        PartIterator i = iterator();
        while( i.hasNext() )
        {
            Note note = (Note)i.next();
            result.add(note);
            int value = note.getRhythmValue();
            System.out.print("pitch "+note.getPitch());
            System.out.print(", ");
            System.out.print("slot: "+slot);
            System.out.print(", ");
            System.out.print("duration: "+value);
            //System.out.print(", ");
            //System.out.println("cost: "+getUnitCost(note));
            slot += value;
        }
        return result;   
    }
    
     public static LinkedList<Note> getNotes(MelodyPart m)
    {
        return m.getNotes();   
    }
     
     /**
      * getNoteList
      * @return the ArrayList of Notes
      */
     public ArrayList<Note> getNoteList()
    {
        ArrayList<Note> result = new ArrayList<Note>();
        PartIterator i = iterator();
        while( i.hasNext() )
        {
            Note note = (Note)i.next();
            result.add(note);
        }
        return result;   
    }
     
     public int getSize()
     {
         return size;
     }
    
    /**
     * Quantization: accounting for the human element of user-generated audio
     * input by modifying the computer output
     *
     * @author Becki Yukman
     * @date July 2014
     *
     */
    
    /**
     * returns the known Note closest to a give irregular Note (from above)
     * @param n
     * @return int cost
     */
    
    public int closestKnownUnitValueAbove(Note n)
    {
        if (n.nonRest())
        {
            for (int i = 0; i<knownNoteValue.length; i++)
            {
                if (knownNoteValue[i]-n.getRhythmValue() >= 0)
                {
                    return knownNoteValue[i];
                }               
            }
        }
        else
        {
            for (int i = 0; i<knownRestValue.length; i++)
            {
                if (knownRestValue[i]-n.getRhythmValue() >= 0)
                {
                    return knownRestValue[i];
                }               
            }
        }
        return -1;
    }
    
    /**
     * returns the known Note closest to a give irregular Note (from below)
     * @param n
     * @return int cost
     */
    
    public int closestKnownUnitValueBelow (Note n)
    {
        if (n.nonRest())
        {
            for (int i = knownNoteValue.length-1; i>0; i--)
            {
                if (knownNoteValue[i]-(n.getRhythmValue()) <= 0)
                {
                    return knownNoteValue[i];
                }               
            }
        }
        else
        {
            for (int i = knownRestValue.length-1; i>0; i--)
            {
                if (knownRestValue[i]-(n.getRhythmValue()) <= 0)
                {
                    return knownRestValue[i];
                }               
            }
        }
        return -1;
    } 
        
    /**
     * calculates the sum of all the Note rhythm values
     * @param MelodyPart m
     * @return int sum
     */
    
    public int getNoteSum()
    {
        MelodyPart m = this;
        int sum = 0;
        
        PartIterator iter = m.iterator();
        while(iter.hasNext())
        {
            if(((Note)iter.next()).nonRest())
            {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * iterates through the melody Note array and returns the median note length 
     * @return int median
     */
    
    public int getMedianNoteLength()
    {
        MelodyPart m = this;
        int median = 0;
        int length = m.getNoteSum();
        if (length > 0) {
           
            //System.out.print("Length: "+length+", ");
            int[] rhythmValues = new int[length];
        
            int i = 0;
        
            PartIterator iter = m.iterator();
            while(iter.hasNext())
            {
                Note n = ((Note)iter.next());
                if(n.nonRest())
                {
                    rhythmValues[i] = n.getRhythmValue();
                    i++;
                }
            }              
            Arrays.sort(rhythmValues);
            median = rhythmValues[(length-1)/2];
            //System.out.println("Median: "+median);
            return median;
        }
        else
        {
            return 480;
        }
    }
    
    public boolean isValidNoteLength(int length)
    {
        if (java.util.Arrays.binarySearch(knownNoteValue, length) >= 0)
            {
               return true; 
            }
        return false;
    }
    
    public boolean isValidRestLength(int length)
    {
        if (java.util.Arrays.binarySearch(knownRestValue, length) >= 0)
            {
               return true; 
            }
        return false;
    }
    
    public boolean isValidUnitLength(int length, boolean isRest)
    {
        if (isRest)
        {
            return isValidRestLength(length);
        }
        else
        {
            return isValidNoteLength(length);
        }
    }
    
    /**
     * calculates the distance between a note and the closest known Note rhythmically
     * @param note
     * @return int cost
     */
    
    public int getUnitCost (Note note)
    {
        int length = note.getRhythmValue();
        if (isValidUnitLength(length, note.isRest()))
        {
            return 0;
        }
        else
        {
            // calculate known Notes on either side using helper functions (above)
            int highValue = closestKnownUnitValueAbove(note);
            int lowValue = closestKnownUnitValueBelow(note);
     
            // calculate distance between note and known Notes on each side
            int highDist = highValue - note.getRhythmValue();
            int lowDist = lowValue - note.getRhythmValue();

            if (Math.abs(highDist) < Math.abs(lowDist))
            {
                return highDist;
            }
            // choice to favor lower known Note in equal distance cases for less 
            // overlap conditions
            else 
            {
                return lowDist;
            }
        }
    }
    
    /**
     * applies a given resolution to a given melodyPart and returns 
     * the int noteSum 
     * @param restAbsorption int
     * @return newMelody MelodyPart 
     */
    
    public MelodyPart absorbRests (int restAbsorption) {
        MelodyPart newMelody = new MelodyPart();
        PartIterator melodyIterator = this.iterator();
        Note thisNote = null;
        Note nextNote = null;
        if (melodyIterator.hasNext())
        {
            nextNote = (Note)melodyIterator.next();
        }
        while (melodyIterator.hasNext()){
            thisNote = nextNote;
            nextNote = (Note)melodyIterator.next();
            if (nextNote.isRest() && nextNote.rhythmValue%(2*restAbsorption) <= restAbsorption && thisNote != null){
                int extraLength = nextNote.rhythmValue%(2*restAbsorption);
                nextNote.setRhythmValue(nextNote.getRhythmValue()-extraLength);
                thisNote.setRhythmValue(thisNote.getRhythmValue()+extraLength);
            }
            newMelody.addNote(thisNote);
        }
        newMelody.addNote(thisNote);
        return newMelody;
    }
    
    public MelodyPart newAbsorbRests(int restAbsorption)
    {
        MelodyPart result = new MelodyPart();
        PartIterator it = iterator();
        if( it.hasNext() )
          {
            Note previousNote = ((Note)it.next()).copy();
            while( it.hasNext() )
              {
                Note note = (Note)it.next();
                int duration = note.getRhythmValue();
                if( note.isRest() 
                 && duration <= restAbsorption 
                 && previousNote.nonRest()
                  )
                  {
                    previousNote.setRhythmValue(previousNote.getRhythmValue() + duration);
                  }
                else
                  {
                    result.addNote(previousNote);
                    previousNote = note;
                  }
              }
            result.addNote(previousNote);
            return result;
          }
        else
          {
            return this;
          }
    }
    
//    /**
//     * applies a given resolution to a given melodyPart and returns 
//     * the int noteSum 
//     * @param resolution int 
//     * @param quantum 
//     * @param toSwing 
//     * @return NoteSum int 
//     */
//    
//    public MelodyPart applyResolution(int resolution, 
//                                      int quantum[], 
//                                      boolean toSwing, 
//                                      int restAbsorption)
//    {
//        //System.out.println("resolution = " + resolution);
//        MelodyPart melody = this.copy();
//        
//        // converts the improvisor melody to a jMusic score
//        jm.music.data.Score score = MidiImport.impMelody2jmScore(melody);
//        
//        // sets the resolution of the new score to the value given
//        MidiImport midiImport = new MidiImport(score);
//        midiImport.setResolution(resolution);
//        
//        // extracts the melody from the score
//        if( toSwing )
//        {
//        int swingQuantum[] = {60, 40};
//        midiImport.scoreToMelodies(swingQuantum); 
//        MelodyPart tempMelody = midiImport.getMelody(0);
//        PartIterator iterator = tempMelody.iterator();
//        int slots = 0;
//        melody = new MelodyPart();
//        while( iterator.hasNext() )
//            {
//            Note thisNote = (Note)iterator.next();
//            Boolean startOnBeat = slots%BEAT == 0;
//            int duration = thisNote.getRhythmValue();
//            if( startOnBeat )
//            {
//                if( duration % 120 == 80 )
//                {
//                    Note firstNote = thisNote.isRest() ? new Rest(duration-20) : new Note(thisNote.getPitch(), duration-20);                   
//                    if( iterator.hasNext() )
//                    {
//                        Note nextNote = (Note)iterator.next();
//                        int nextDuration = nextNote.getRhythmValue();
//                        slots += nextDuration;
//                        
//                        if( nextDuration % 120 == 40 )
//                        {
//                            nextDuration += 20;
//                            Note secondNote = nextNote.isRest()? new Rest(nextDuration) : new Note(nextNote.getPitch(), nextDuration);
//                            melody.addNote(firstNote);
//                            melody.addNote(secondNote);
//                        }
//                        else
//                        {
//                            // triplet sixteenth note
//                            if ( nextDuration == 20 )
//                            {
//                                if( iterator.hasNext() )
//                                {
//                                    Note followingNote = (Note)iterator.next();
//                                    int followingDuration = followingNote.getRhythmValue();
//                                    slots += followingDuration;
//                                    if( followingDuration % 120 == 20 )
//                                    {
//                                        nextDuration += 10;
//                                        followingDuration += 10;
//                                        Note secondNote = nextNote.isRest()? new Rest(nextDuration) : new Note(nextNote.getPitch(), nextDuration);
//                                        Note thirdNote = followingNote.isRest()? new Rest(followingDuration) : new Note(followingNote.getPitch(), followingDuration);
//                                        //System.out.println("is followingNote a rest?");
//                                        //System.out.println(followingNote.isRest());
//                                        melody.addNote(firstNote);
//                                        melody.addNote(secondNote);
//                                        melody.addNote(thirdNote);
//                                    }
//                                    else
//                                    {
//                            melody.addNote(thisNote.copy());
//                            melody.addNote(nextNote.copy());
//                                        melody.addNote(followingNote.copy());
//                        }
//                    }
//                            }
//                    else
//                    {
//                        melody.addNote(thisNote.copy());
//                                melody.addNote(nextNote.copy());
//                    }
//                }
//                    }
//                else
//                {
//                    melody.addNote(thisNote.copy());
//                }
//            }
//            else
//            {
//                melody.addNote(thisNote.copy());
//            }
//            }
//            else
//            {
//                melody.addNote(thisNote.copy());
//            }
//            //System.out.println(startOnBeat + " " + thisNote);
//            slots += duration;
//            }
//        }
//        else
//        {
//        midiImport.scoreToMelodies(quantum);
//        melody = midiImport.getMelody(0);
//        }
//        melody = melody.absorbRests(restAbsorption);
//        return melody;
//    }

    
/**
 * New version of quantizing melody, 21 June 2016.
 * Self-contained and does not rely on classes in jMusic.
 * Also preserves accidentals in the original melody.
 * See "Attempt ..." to see case of swing not handled yet.
 * @param quanta
 * @param toSwing
 * @param restAbsorption
 * @return a quantized melody part
 */
public MelodyPart quantizeMelody(int quanta[], boolean toSwing, int restAbsorption)
{
    int gcd = gcd(quanta[0], quanta[1]);
    
    //System.out.println("quantize melody to " + quanta[0] + " & " + quanta[1] + ", gcd = " + gcd + ", restAbsorb = " + restAbsorption);
    
    MelodyPart result = this; // will be replaced if part is non-empty
    
    int notesLost = 0;
    PartIterator it = iterator();
    
    // Only quanitize non-empty melody part that is not maximum quantization
    if( it.hasNext() && gcd != 1 )
      {
         result = new MelodyPart();
         int inputSlot = 0;
         int outputSlot = 0;
         int endOfLastPlacement = 0;
         while( it.hasNext() )
           {
             Note thisNote = (Note)it.next();
             //System.out.println("inputSlot = " + inputSlot + ", outputSlot = " + outputSlot + ", thisNote = " + thisNote);
             if( !thisNote.isRest() )
               {
                 // thisNote is an actual Note, not a Rest.
                 // Can't place note until inputSlot has caught up to
                 // outputSlot.
                 
                 // Find next slot that is a multiple of one of the quanta
                 while( outputSlot%quanta[0] != 0 && outputSlot%quanta[1] != 0 )
                   {
                     outputSlot += gcd;
                   }
                 
                 if( inputSlot < outputSlot )
                   {
                     // Lose thisNote
                     System.out.println("note lost at beat " + (inputSlot/BEAT) + ": " + thisNote.toLeadsheet());
                     notesLost++;
                   }
                 else
                   {
                     if( outputSlot < inputSlot )
                       {
                         outputSlot = quantizeDown(inputSlot, gcd);
                       }

                     int gap = quantizeDown(inputSlot - endOfLastPlacement, gcd);
                     if( gap >= 0 )
                       {
                           //System.out.println("gap = " + gap);
                           Rest newRest = new Rest(gap);
                           result.addRest(newRest);
                       }
                     
                     Note newNote = thisNote.copy();
                     // Copying, rather than constructing anew, will preserve accidental
                     int noteDuration = quantizeUp(thisNote.getRhythmValue(), gcd);
                     newNote.setRhythmValue(noteDuration);
                     result.addNote(newNote);
                     //System.out.println("placing at " + outputSlot + " " + newNote);
                     outputSlot += noteDuration;
                     endOfLastPlacement = outputSlot;
                   }
               }
            inputSlot += thisNote.getRhythmValue();
           } // while
      }
   //System.out.println("quantized melody: " + result);
   //System.out.println(notesLost + " notes lost in quantization");

    // Handle converting swing-eighth situations to appear as normal eights
    // including when second third of triplet is sixteenths etc.
    if( toSwing )
      { 
        int swingFirst = (2*BEAT)/3;
        int halfBeat = BEAT/2;
        int sixthBeat = BEAT/6;
        MelodyPart unswung = result;
        result = new MelodyPart();
        it = unswung.iterator();
        int slot = 0;
        while( it.hasNext() )
          {
            // Use the note left over from previous iterationr, or a new one
            Note unswungNote = (Note)it.next();
            int unswungDuration = unswungNote.getRhythmValue();
            if( slot % BEAT == 0 
              && (unswungDuration % BEAT) == swingFirst 
              && it.hasNext() 
              )
              {
              // We may have a swing situation. 
              // Keep track of "swung" and "unswung" values, until we
              // know for sure.
                
              Note swungNote = unswungNote.copy();
              ArrayList<Note> unswungNotes = new ArrayList<Note>();
              ArrayList<Note> swungNotes = new ArrayList<Note>();
              unswungNotes.add(unswungNote);
              swungNote.setRhythmValue(swungNote.getRhythmValue() - sixthBeat);
              swungNotes.add(swungNote);
               
              // See if the notes after the first fit the swing pattern.              
              // Adjust note or notes following first swing note, 
              // as long as they exactly fit into 1/2 of a beat
 
              unswungNote = (Note)it.next();
              swungNote = unswungNote.copy();
              unswungDuration = unswungNote.getRhythmValue();
              int swungDuration = unswungDuration + unswungDuration/2;
              // Above converts value of 1/3 into 1/2 by multiplying by 1.5
              swungNote.setRhythmValue(swungDuration);
              unswungNotes.add(unswungNote);
              swungNotes.add(swungNote);
              int remainingSpace = halfBeat - swungDuration;
              
              // Adjust durations in the second half of a swing figure
              while( it.hasNext() && remainingSpace > 0 )
                {
                  unswungNote = (Note)it.next();
                  swungNote = unswungNote.copy();                 
                  unswungDuration = unswungNote.getRhythmValue();
                  swungDuration = unswungDuration + unswungDuration/2;                  
                  swungNote.setRhythmValue(swungDuration);
                  unswungNotes.add(unswungNote);
                  swungNotes.add(swungNote);          
                  remainingSpace -= swungDuration;
                  }
              /*
              System.out.println("slot = " + slot + ", remainingSpace) = " + remainingSpace);
              System.out.println("unswungNotes = " + unswungNotes);
              System.out.println("swungNotes = " + swungNotes);
              System.out.println("");
              */
              
              // If swing situation fits
              if( remainingSpace == 0 )
                {
                for( Note n: swungNotes )
                  {
                    result.addNote(n);
                  }
                }
              else
                {
                for( Note n: unswungNotes )
                  {
                    result.addNote(n);
                    slot += n.getRhythmValue();
                  }                  
                }
              } // end of swing situation
            else
              {
                // Not a swing situation. Just use the note as is.
                result.addNote(unswungNote);
                slot += unswungDuration;
              } // end handling possible swing situation
          } // end while
      }
    if( restAbsorption > 0 )
      {
        result = result.newAbsorbRests(restAbsorption);
      }
    result.setInstrument(getInstrument());
    return result;
}

public static int quantizeDown(int duration, int quantum)
{
    int result = quantum * Math.floorDiv(duration, quantum);
    return result;
}

public static int quantizeUp(int duration, int quantum)
{
    return quantum * (int)Math.ceil(((double)duration)/quantum);
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
       
    public void mergeAdjacentRests() 
    {
        int index = getNextIndex(0);
        int nextIndex = getNextIndex(index);

        Note note = getNote(index);
        Note nextNote = getNote(nextIndex);
        
        //variable declarations for the test conditions for merging rests
        boolean bothRest;
        boolean validRestLength;

        while (nextNote != null) 
        {
            // true if both Notes are Rests, false otherwise 
            bothRest = note.isRest() && nextNote.isRest();
            // true if two Rests can be combined into a valid Rest Length
            validRestLength = java.util.Arrays.binarySearch(knownRestValue, 
                              note.getRhythmValue() 
                              + nextNote.getRhythmValue()) >= 0;
            
            if (bothRest && validRestLength) 
            {
                // slots.set(nextIndex, null);
                unitCount--;

                //System.out.println("Correcting rests in measure "+((index/measureLength)+1));
                
                note.setRhythmValue(note.getRhythmValue() + nextNote.getRhythmValue());
                
                // skips over swung while allowing the next iteration to 
                // compare against the note most recently corrected
                nextNote = getNote(getNextIndex(nextIndex));
            }
            else 
            {
                //increments indices and notes
                index = getNextIndex(index);
                nextIndex = getNextIndex(nextIndex);
                
                note = getNote(index);
                nextNote = getNote(nextIndex); 
            }
        }
        //System.out.println("Merge rests quantization completed.");
    }


    /**
     * removeRepeatedNotes() returns a new melody based on the current one,
     * except that repeated notes are merged together into single notes.
     * @return 
     */
    public MelodyPart removeRepeatedNotes()
    {
        MelodyPart result = new MelodyPart();
        PartIterator it = iterator();
        Note previous = null;
        int duration = 0;
        while( it.hasNext() )
          {
            Note current = (Note) it.next();
            if( previous == null )
              {
                /* initialize */
                previous = current.copy();
                duration = current.getRhythmValue();
              }
            else 
              {
                /* possibly accumulate */
                boolean currentRest = current.isRest();
                boolean previousRest = previous.isRest();
                if( currentRest && previousRest )
                  {
                    duration += current.getRhythmValue();
                  }
                else if( currentRest && !previousRest 
                      || !currentRest && previousRest
                      || !currentRest && !previousRest && current.getPitch() != previous.getPitch() )
                  {
                  /* end accumulation and start a new one */
                  previous.setRhythmValue(duration);
                  result.addNote(previous);
                  previous = current.copy();
                  duration = current.getRhythmValue();                    
                  }
                else
                  {
                  /* add to the accumulation */
                  duration += current.getRhythmValue();
                  previous = current.copy();
                  }
                }
          }
        if( previous != null )
          {
            previous.setRhythmValue(duration);
            result.addNote(previous);
          }
        //System.out.println("old melody = " + this + "\nnew melody = " + result);
        return result;
    }
    
     /**
     * removeRepeatedNotes() returns a new melody based on the current one,
     * except that repeated notes are merged together into single notes.
     */
    public void removeRepeatedNotesInPlace()
    {
        int n = slots.size();
        Note previousNote = null;
        int previousIndex = 0;
        for( int i = 0; i < n; i++ )
          {
            Unit thisUnit = slots.get(i);
            if( thisUnit != null )
              {
              if( previousNote != null && ((Note)thisUnit).nonRest() )
                {
                Note thisNote = (Note)thisUnit;
                if( thisNote.samePitch(previousNote) )
                  {
                    Note newNote = previousNote.copy();
                    newNote.setRhythmValue(previousNote.getRhythmValue() + thisNote.getRhythmValue());
    //System.out.println("in beat " + i/BEAT + " merging notes " + previousNote.toLeadsheet() + " -> " + newNote.toLeadsheet());
                    slots.set(i, null);
                    slots.set(previousIndex, newNote);
                    previousNote = newNote;
                  }
                else
                  {
                  previousNote = thisNote;
                  previousIndex = i;
                  }
                }
              else
                {
                  previousNote = (Note)thisUnit;
                  previousIndex = i;
                }
              }
          }
    }
}