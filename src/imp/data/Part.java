/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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

import imp.style.SectionInfo;
import imp.style.SectionRecord;
import imp.style.Style;
import imp.Constants;
import static imp.data.Part.isRest;
import imp.util.ErrorLog;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * The Part class is representative of an arbitrarily long melody or chord part, 
 * played by a specific instrument.
 * A Part contains a sequence of Units stored in the ArrayList<Unit> slots.
 * 
 * It also contains information about the Part, such as volume, instrument, etc.
 * Units should be added using the setUnit method, which will automatically
 * adjust rhythm values.  In a Part, the 0 slot must never be empty.  In
 * its default state it contains a Rest or NC.  
 * If delUnit is called on the 0 slot, then it is replaced with a Rest or NC.
 * @see         Unit
 * @see         Note
 * @see         Chord
 * @see         Rest
 * @author      Stephen Jones (rewritten from code written by Andrew Sorensen 
 *              and Andrew Brown)
 */
public class Part implements Constants, Serializable {

/**
 * an ArrayList containing the slots in this Part, each of which contains either
 * null or a Unit object (can be note, rest, or chord)
 */

protected ArrayList<Unit> slots;


/**
 * an int containing the number of slots in the Part
 */

protected int size;


/**
 * an int containing the number of slots that contain a Unit object
 */

protected int unitCount;


/**
 * a String containing the title of this Part
 */

protected String title;


/**
 * The default composer
 */

public static String DEFAULT_COMPOSER = "";


/**
 * a String containing the composer of this Part
 */

protected String composer;


/**
 * an int representing the instrument of this Part
 */

protected int instrument;


/**
 * an int representing the volume of this Part
 */

protected int volume;


/**
 * the key signature of the part
 */

protected int keySig;


/**
 * the metre of the part
 */

protected int metre[] = new int[2];
protected int beatValue;
protected int measureLength;


/**
 * the swingValue constant for the Part
 */

protected double swing;


/**
 * the default title
 */

public static final String DEFAULT_TITLE = "";


/**
 * the default instrument
 */

public static final int DEFAULT_INSTRUMENT = 0;


/**
 * the default volume
 */

public static final int DEFAULT_VOLUME = 85;


/**
 * the default number of slots
 */

public static final int DEFAULT_SIZE = 0;


/**
 * the default key signature
 */
public int DEFAULT_KEYSIG = 0;

/**
 * swingValue constant
 */

public static final double DEFAULT_SWING = 0.67; // Don't use 2./3


protected Constants.StaveType staveType = Preferences.getStaveTypeFromPreferences();

/**
 * Creates a Part with the specified number of slots and a rest in the first
 * slot.
 *
 * @param size the number of slots to create in the Part
 */
    
public Part(int size)
  {
    title = DEFAULT_TITLE;
    volume = DEFAULT_VOLUME;
    instrument = DEFAULT_INSTRUMENT;

    setMetre(DEFAULT_METRE[0], DEFAULT_METRE[1]);

    keySig = DEFAULT_KEYSIG;
    swing = DEFAULT_SWING;
    composer = DEFAULT_COMPOSER;

    this.size = size;
    try
      {
        slots = new ArrayList<Unit>(size);
        for( int k = 0; k < size; k++ )
          {
            slots.add(null);
          }
        //slots.setSize(size);
        if( size != 0 )
          {
            slots.set(0, new Rest(size));     // empty Part has one long rest
            unitCount = 1;
          }
      }
    catch( OutOfMemoryError e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Not enough memory to create part of size " + size);
      }
  }


Constants.StaveType getPreferredStaveType()
  {
    return staveType;
  }


/**
 * Creates a Part with the default size.
 */

public Part()
  {
    this(DEFAULT_SIZE);
  }


/**
 * Sets the instrument of this Part.
 *
 * @param instrument an int representing the instrument
 */

public void setInstrument(int instrument)
  {
    //System.out.println("setting instrumcnt to " + instrument + " in " + this);
    this.instrument = instrument;
  }


/**
 * Sets the current volume for this Part.
 *
 * @param volume an int representing the volume
 */

public void setVolume(int volume)
  {
    this.volume = volume;
  }


/**
 * Gets the volume for this Part.
 *
 * @return int the volume
 */

public int getVolume()
  {
    return volume;
  }


/**
 * Sets the title of this Part.
 *
 * @param title a String containing the title
 */

public void setTitle(String title)
  {
    this.title = title;
  }


/**
 * Returns the title of this Part.
 *
 * @return String title
 */

public String getTitle()
  {
    return title;
  }


/**
 * Sets the composer of the Part
 *
 * @param composer a String representing the composer of the Part
 */

public void setComposer(String composer)
  {
    this.composer = composer;
  }


/**
 * Returns the composer of the Part
 *
 * @return composer the composer of the Part
 */

public String getComposer()
  {
    return composer;
  }


/**
 * Sets the staveType of the Part
 *
 * @param staveType a String representing the staveType of the Part
 */

public void setStaveType(Constants.StaveType staveType)
  {
    this.staveType = staveType;
  }


/**
 * Returns the staveType of the Part
 *
 * @return staveType the staveType of the Part
 */

public Constants.StaveType getStaveType()
  {
    return staveType;
  }


public int getMeasureLength()
  {
    return measureLength;
  }


/**
 * Sets the metre of this Part
 *
 * @param metre the metre to set the part to
 */

public void setMetre(int top, int bottom)
  {
    metre[0] = top;
    metre[1] = bottom;
    beatValue = WHOLE / metre[1];
    measureLength = beatValue * metre[0];
  }


/**
 * Returns the metre of this Part
 *
 * @return int the metre
 */

public int[] getMetre()
  {
    return metre;
  }


/**
 * Sets the key signature of this Part
 *
 * @param keySig the key signature to set the part to
 */

public void setKeySignature(int keySig)
  {
    this.keySig = keySig;
  }

/**
 * Sets the swingValue of this Part
 *
 * @param swingValue the swingValue value
 */

public void setSwing(double swing)
  {
    this.swing = swing;
  }


/**
 * Returns the key signature of this Part
 *
 * @return int the key signature
 */

public int getKeySignature()
  {
    return keySig;
  }


/**
 * Returns the swingValue value for this part
 *
 * @return the swingValue value for this part
 */

public double getSwing()
  {
    return swing;
  }


/**
 * Returns the number of Beats in this Part.
 *
 * @return int number of Beats
 */

public int getNumBeats()
  {
    return size / beatValue;
  }


/**
 * Returns the number of slots in this Part.
 *
 * @return int number of slots
 */

public int getSize()
  {
    return size;
  }


/**
 * Returns the number of slots in this Part.
 *
 * @return int number of slots
 */

public int size()
  {
    return size;
  }


/**
 * Returns the end time of the Part.
 *
 * @return int the end time of the Part
 */

public int getEndTime()
  {
    return size;
  }


/**
 * Returns the number of measures
 */

public int getBars()
  {
    int bars = (int) Math.ceil(size / measureLength);
    //System.out.println("bars = " + bars);
    return bars;
  }


/**
 * Returns the number of active measures, meaning ones that contain the start of
 * a chord or non-rest note
 *
 * @return int the end time of the Part
 */

public int getActiveBars()
  {
    int lastActiveSlot = getLastActiveSlot();
    //System.out.println("last active slot = " + lastActiveSlot);
    if( lastActiveSlot < 0 )
      {
        return 0;
      }

    int activeBars = 1 + (int) Math.ceil(lastActiveSlot / measureLength);
    return activeBars;
  }


/**
 * Returns the last active slot, if any. If none is active, then return -1.
 */

public int getLastActiveSlot()
  {
    for( int j = slots.size() - 1; j >= 0; j-- )
      {
        Object ob = slots.get(j);
        if( ob instanceof Chord
                || ob instanceof Note )
          {
            return j;
          }
      }
    return -1;
  }


/**
 * Returns an int representing the instrument of this Part.
 *
 * @return int instrument
 */

public int getInstrument()
  {
    return instrument;
  }


/**
 * Returns an ArrayList containing every Unit in this Part.
 *
 * @return ArrayList unitList
 */

public ArrayList<Unit> getUnitList()
  {
    ArrayList<Unit> unitList = new ArrayList<Unit>(unitCount);
    Part.PartIterator i = iterator();
    while( i.hasNext() )
      {
        unitList.add(i.next());
      }
    return unitList;
  }


/**
 * Change the size (number of slots) in this Part, and also set unitCount.
 * @param newSize 
 */

public void setSize(int newSize)
  {
    //Trace.log(0, "setting size of part to " + newSize + "(" + newSize / BEAT + " beats)");

    if( newSize == size )
      {
        return;
      }
    
    ArrayList<Unit> newSlots = new ArrayList<Unit>(newSize);
    
    int slotsToCopy = Math.min(size, newSize);
    
    int lastUnitIndex = 0;
    
    int newUnitCount = 0;
    
    int j;
    
    Unit lastUnit = null;
    
    for( j = 0; j < slotsToCopy; j++ )
      {
        Unit unit = slots.get(j);
        
        if( unit == null )
          {
            newSlots.add(null);
          }
        else
          {
            unit = unit.copy();
            newSlots.add(unit);
            newUnitCount++;
            lastUnitIndex = j;
            lastUnit = unit;
          }
      }
    
    // u, if not null, is the last unit and lastUnitIndex is its index.
    
    // If newSize > size, fill the rest of newSlots.
    
    for( ; j < newSize; j++ )
      {
        newSlots.add(null);
      }
    
    // If there is a last unit, its rhythmValue may need lengthening or
    // shortening to conform to the length of the new part.
    
    if( lastUnit != null )
      {
      lastUnit.setRhythmValue(newSize - lastUnitIndex);
      }

    
    // Install the new slots, etc.
    
    slots = newSlots;
    
    size = newSize;
    
    unitCount = newUnitCount;

    //System.out.println("size of part is now " + slots.size());
    //System.out.println(toString());
  }


/**
 * Returns an exact copy of this Part
 *
 * @return Part copy
 */

public Part copy()
  {
    Trace.log(3, "copying part of size " + size);

    Part newPart = new Part(size);
    /*
     * PartIterator i = iterator(); while(i.hasNext())
     * newPart.slots.set(i.nextIndex(), i.next().copy());
     */

    for( int i = 0; i < size; i++ )
      {
        Unit unit = slots.get(i);
        if( unit != null )
          {
            unit = unit.copy();
          }
        newPart.slots.set(i, unit);
      }

    newPart.unitCount = unitCount;
    newPart.title = title;
    newPart.volume = volume;
    newPart.instrument = instrument;
    newPart.swing = swing;
    newPart.setMetre(metre[0], metre[1]);
    newPart.keySig = keySig;
    return newPart;
  }


/**
 * Returns a String representation of this Part. The String contains information
 * on every Unit in the Part.
 *
 * @return String representation of Part
 */

@Override
public String toString()
  {
    StringBuilder partData = new StringBuilder();
    partData.append("\nPart with unitCount = ");
    partData.append(unitCount);
    partData.append(": ");
    Part.PartIterator i = iterator();
    while( i.hasNext() )
      {
        int index = i.nextIndex();
        partData.append("Beat ");
        partData.append(index / beatValue);
        partData.append(" + ");
        partData.append(index % beatValue);
        partData.append(" slots :");
        partData.append(i.next().toString());
        partData.append('\n');
      }
    return partData.toString();
  }




/**
 * Adds a Unit to the end of the Part, extending the length as it goes. Note
 * that 0 duration units should not be added, as they will cause this to fail.
 *
 * @param unit the Unit to add
 */

public void addUnit(Unit unit)
  {
 
    int rv = unit.getRhythmValue();
    if( rv <= 0 )
      {
        // Not sure how a unit can have 0 as its rhythm value, but it does 
        // happen, and it causes problems.
        return;
      }
    //Trace.log(0, "adding unit to end of part " + unit.toLeadsheet() + ", rv = " + rv + ", slots.size() = " + slots.size());

    int index = slots.size();
    int newSize = slots.size() + rv;
    size = newSize;

    slots.ensureCapacity(newSize);

    for( int k = index; k < newSize; k++ )
      {
        slots.add(null);
      }
     //Trace.log(0, "now slots.size() = " + slots.size() + ", index = " + index);
   
    if( slots.get(index) == null && unit != null )
      {
        unitCount++;
      }

    slots.set(index, unit);

    //rk Hack to remove accidental if key signature covers it.
    // This tends to reduce the number of accidentals in the notation.

    if( unit instanceof Note )
      {
        Note note = (Note) unit;
        if( note.isAccidentalInKey(keySig) )
          {
            // If the note shows as accidentaly in the key,
            // see if toggling it will help, and if not, 
            // toggle back.

            note.toggleEnharmonic();
            if( note.isAccidentalInKey(keySig) )
              {
                note.toggleEnharmonic();
              }
          }
      }
  }

    
/**
 * Sets the slot at the specified unitIndex to the specified Unit.
 *
 * @param unitIndex the index of the slot to put the Unit in
 * @param unit the Unit to set
 */
    
public void setUnit(int unitIndex, Unit unit)
  {
  if( unit != null )
  //System.out.println("setUnit " + unitIndex + " to " + unit);
    if( unitIndex >= size || unitIndex < 0 )
      {
        return; // shouldn't happen, but can.
      }

    //Trace.log(0, "setting Unit at " + unitIndex + " to " + (unit == null ? null : unit.toLeadsheet()));
    // if we want to set it to empty, we are effectively deleting it
    if( unit == null )
      {
        delUnit(unitIndex);	// Tracing this produces too much output
        return;
      }

    //Trace.log(3, "setting Unit at " + unitIndex + " to " + unit.toLeadsheet());

    //rk: I really do not follow the logic having to do with old note values.

    Unit oldUnit = slots.get(unitIndex);

    int rv = getUnitRhythmValue(unitIndex);

    // if the slot is empty, we need to find what the rhythm value should be

    if( oldUnit == null )
      {
        // Note: When next unit is a rest, the above may had the effect of cutting the inserted note short!!
        // See compensating code below.

        int nextIndex = getNextIndex(unitIndex);

        unitCount++;
        Unit prevUnit = getPrevUnit(unitIndex);
        if( prevUnit != null )
          {
            //Trace.log(3, "in setUnit - A, setting rhythmValue");
            // we also need to change the rv of the previous Unit
            prevUnit.setRhythmValue(prevUnit.getRhythmValue() - rv);
          }
      }
    else
      {
        // if there was already a Unit there, we already know the rv
        rv = oldUnit.getRhythmValue();
      }

    //Trace.log(3, "in setUnit - B, setting rhythmValue");
    unit.setRhythmValue(rv);
    slots.set(unitIndex, unit);
  }


/**
 * Sets the slot at the specified unitIndex to the specified Unit.
 *
 * @param unitIndex the index of the slot to put the Unit in
 * @param unit the Unit to set
 */

public void newSetUnit(int unitIndex, Unit unit)
  {
    if( unit != null )
      {
      //System.out.println("\nnewSetUnit " + unitIndex + " to " + unit);
      }
    //checkConsistency("start of newSetUnit");
    if( unitIndex >= size || unitIndex < 0 )
      {
        return; // shouldn't happen, but can?
      }

    if( unit == null )
      {
        delUnit(unitIndex);
        return;
      }
    else
      {
      }

    // Pre-conditioning: If unit is too long for part, truncate it first:

    int unitDuration = unit.getRhythmValue();
    
    int nextUnitStart = unitIndex + unitDuration;

    if( nextUnitStart >= size )
      {
        unit = unit.copy();
        unit.setRhythmValue(size - unitIndex);
        nextUnitStart = size;
      }

    // If this unit overlays one or more units, set them to null.
    // Let makeConsistent fix things up.
    
    for( int index = unitIndex; index < nextUnitStart; index++ )
      {
        slots.set(index, null);
      }
    
    // If the new unit overlays part of another unit, replace the latter
    // with a rest.
    if( nextUnitStart < size && slots.get(nextUnitStart) == null )
      {
        int nextUnitEnd = getNextIndex(nextUnitStart);
        if( this instanceof MelodyPart )
          {
          slots.set(nextUnitStart, new Rest(nextUnitEnd - nextUnitStart));
          }
        else
          {
          // HB - Disabled as of 7-8-13, not sure this is needed when pasting
          //       chords back into leadsheet.
          // slots.set(nextUnitStart, new Chord(nextUnitEnd - nextUnitStart));            
          }
      }

    // Place the new unit
    setSlot(unitIndex, unit, "leaving newSetUnit");
    
    // Re-eneable this!! checkConsistency("end of newSetUnit");
  }


private void setSlot(int index, Unit unit, String message)
  {
    //System.out.println("\nsetting slot at " + message + " index " + index + " to " + unit);
    if( unit == null )
      {
        return;
      }

    slots.set(index, unit);
    
    // need to account for implicitly deleted last unit

    makeConsistent();
    //System.out.println("part after setting slot:  " + this);
  }

static public boolean isRest(Unit unit)
  {
    return unit != null && unit instanceof Note && ((Note)unit).isRest();
  }

/**
 * Check whether or not the part is consistent.
 */

public boolean checkConsistency(String message)
  {
    int sum = 0;
    int count = 0;
    for( int i = 0; i < size; i++ )
      {
        Unit unit = slots.get(i);
        if( unit != null )
          {
            //System.out.println("non-null unit " + unit);
            count++;
            sum += unit.getRhythmValue();
          }
      }
    if( sum != size || count != unitCount )
      {
        System.out.println("*** In " + message + " consistency check failed: size = " + size + " vs. duration sum = " + sum + " unitCount = " + unitCount + " vs " + count + " " + this);
        assert false;
        return false;
      }
    //System.out.println("In " + message + " integrity check succeeded: size = " + size + " unitCount = " + unitCount);
    return true;
  }

/**
 * Force the part to be consistent.
 */

public void makeConsistent()
  {
    if( size == 0 )
      {
        unitCount = 0;
        return;
      }
    //checkConsistency("start makeConsistent");
    int prevIndex = 0;

    Unit prevUnit = slots.get(prevIndex);
    Unit thisUnit;

    if( prevUnit == null )
      {
        prevUnit = new Rest(60);
        slots.set(prevIndex, prevUnit);
      }

    int count = 1;
    
    int index = 1;
    for( ; index < size ; index++ )
      {
        thisUnit = slots.get(index);
        if( thisUnit != null )
          {
            // Found another unit
            count++;
            int diff = index - prevIndex;
            if( isRest(thisUnit) && isRest(prevUnit) )
              {
                // merge adjacent rests
                count--;
                prevUnit.setRhythmValue(prevUnit.getRhythmValue() + thisUnit.getRhythmValue());
                slots.set(index, null);
              }
            else if( diff != prevUnit.getRhythmValue() )
              {
                // Adjust prevUnit to fill gap
                prevUnit.setRhythmValue(diff);
              }
          prevUnit = thisUnit;
          prevIndex = index;
          }
      }

    prevUnit.setRhythmValue(index - prevIndex);

    unitCount = count;
    //System.out.println("makeConsistent result " + this);
    // re-enable this checkConsistency("end makeConsistent");
  }


public void splitUnit(int slotIndex)
  {
    Unit origSplitUnit = getUnit(slotIndex);

    // check to see if there is a unit to split
    if( origSplitUnit != null )
      {
        return;
      }

    int prevIndex = getPrevIndex(slotIndex);
    origSplitUnit = getUnit(prevIndex);
    int origRhythmValue = origSplitUnit.getRhythmValue();

    // if previous unit extends into this slot, we need to split it
    if( prevIndex + origRhythmValue >= slotIndex )
      {
        Unit splitUnit = origSplitUnit.copy();
        splitUnit.setRhythmValue(origRhythmValue + prevIndex - slotIndex);
        setUnit(slotIndex, splitUnit);  // this call shortens the original note too
      }
    else
      {
        // nothing to split, but if we actually did get here, things would be 
        // broken since the slot to split at is null
        //Trace.log(3, "Error: SplitUnitCommand found inconsistencies with the Part");
      }
  }


/**
 * Return the index at the start of the measure in which index occurs.
 */
int startMeasure(int index, int measuresOffset)
  {
    return measureLength * (measuresOffset + index / measureLength);
  }

    
/**
 * Deletes the unit at the specified index, adjusting the rhythm value of the
 * preceding Unit.
 *
 * @param unitIndex the slot containing the Unit to delete
 */
    
public void delUnit(int unitIndex)
  {
    if( unitIndex < 0 )
      {
        return;
      }
    // FIX: he try-catch is because there is an occasional out-of-range error
    // for the index. This is clearly a hack to avoid being affected by it.
    try
      {
        Unit unit = slots.get(unitIndex);

        if( unit != null )
          {
            //Trace.log(0, "delUnit at " + unitIndex + ", was " + unit);

            int rv = unit.getRhythmValue();
            slots.set(unitIndex, null);
            unitCount--;
            Unit prevUnit = getPrevUnit(unitIndex);

            //Trace.log(3, "prevUnit = " + prevUnit);

            // If there was a Unit before it, we need to adjust its rv.

            if( prevUnit != null )
              {
                //Trace.log(0, "in delUnit, setting rhythmValue");
                prevUnit.setRhythmValue(prevUnit.getRhythmValue() + rv);
              }
            // If there was no Unit before it, then we just deleted the
            // 0 slot, which must never be empty, so put something appropriate there.
            else if( this instanceof imp.data.MelodyPart )
              {
                setUnit(0, new Rest());
              }
            else if( this instanceof imp.data.ChordPart )
              {
                setUnit(0, new Chord(NOCHORD));
              }
          }
      }
    catch( Exception e )
      {
      }
  }


/**
 * Deletes all Units in the specified range, adjusting the rhythm value of the
 * preceding Unit.
 *
 * @param first the index of the first Unit in the range
 * @param last the index of the last Unit in the range
 */

public void delUnits(int first, int last)
  {
    //Trace.log(2, "delUnits from " + last + " down to " + first);
    
    // It seems like this approach could be fairly slow
    
    for( int i = last; i >= first; i-- )
      {
        delUnit(i);
      }
  }


/**
 * Totally empties the Part and resets the size to zero.
 */

public void empty()
  {
    size = 0;
    unitCount = 0;
    slots = new ArrayList<Unit>(0);
    //slots.setSize(0);
  }

/**
 * Returns the Unit before the slot specified by the index. The function
 * iterates backwards from the specified slot and returns the first Unit it
 * reaches. If no Unit is reached, it returns null.
 *
 * @param slotIndex the slot of the Unit to start on
 * @return Unit the previous Unit
 */

public Unit getPrevUnit(int slotIndex)
  {
    int i = getPrevIndex(slotIndex);
    if( i == -1 )
      {
        return null;
      }
    return slots.get(i);
  }


/**
 * Returns the index of the Unit previous to the index indicated.
 *
 * @param slotIndex the index of the slot to start searching from
 * @return int the index of the previous Unit
 */

public int getPrevIndex(int slotIndex)
  {
    if( slotIndex < 0 )
      {
        return -1;
      }
    ListIterator<Unit> i = slots.listIterator(slotIndex);
    while( i.hasPrevious() )
      {
        Unit unit = i.previous();
        if( unit != null )
          {
            return i.nextIndex();
          }
      }
    return -1;
  }


/**
 * Returns the next Unit after the indicated slot.
 *
 * @param slotIndex the index of the slot to start searching from
 * @return Unit the next Unit
 */

public Unit getNextUnit(int slotIndex)
  {
    int i = getNextIndex(slotIndex);
    if( i >= size )
      {
        return null;
      }
    return slots.get(i);
  }


/**
 * Return the first index of a slot not containing null.
 * If no such index, return -1.
 * @return 
 */
public int getFirstIndex()
  {
    for( int i = 0; i < size; i++ )
      {
        Unit unit = slots.get(i);
        if( unit != null )
          {
            return i;
          }
      }
    return -1;
  }

/**
 * Return the first (non-null) Unit.
 * If there is none, return null;
 * @return 
 */
public Unit getFirstUnit()
  {
    int i = getFirstIndex();
    if( i < 0 )
      {
        return null;
      }
    return slots.get(i);
  }

/**
 * Returns the index of the next Unit after the indicated slot.
 *
 * @param slotIndex the index of the slot to start searching from
 * @return int the index of the next Unit
 */

public int getNextIndex(int slotIndex)
  {
    for( int i = slotIndex + 1; i < size; i++ )
      {
        Unit unit = slots.get(i);
        if( unit != null )
          {
           return i;
          }
      }
   return size;
    

//    
//    Unit unit = slots.get(slotIndex);
//    int nextIndex = size;
//    if( unit != null )
//      {
//        nextIndex = slotIndex + unit.getRhythmValue();
//      }
//    else
//      {
//        ListIterator<Unit> i = slots.listIterator(slotIndex);
//        while( i.hasNext() )
//          {
//            nextIndex = i.nextIndex();
//            if( i.next() != null )
//              {
//                break;
//              }
//          }
//      }
//    if( nextIndex >= size )
//      {
//        return size;     // What to do in this case?
//      }
//    return nextIndex;
  }


/**
 * Returns the rhythm value of a unit starting at the unitIndex. This function
 * can be called on an empty slot to see what rhythm value a Unit would have if
 * it was in that slot.
 *
 * @param unitIndex the index of the Unit in question
 * @return int the rhythm value of the Unit
 */

public int getUnitRhythmValue(int unitIndex)
  {
     //System.out.print("getUnitRhythmValue " + unitIndex + " in part of length " + size);
     if( unitIndex >= size )
      {
        return 0;
      }
    // Start on the next Unit over, since we can call this on an 
    // empty slot
    int i = unitIndex + 1;
    
    while( i < size )
      {
        if( getUnit(i) != null )
          {
            //System.out.println(" " + (i - unitIndex));
            return i - unitIndex;
          }
        i++;
      }
    //System.out.println(" " + (size - unitIndex));
    return size - unitIndex;
  }


//public int getUnitRhythmValue(int unitIndex)
//  {
//    // Start on the next Unit over, since we can call this on an 
//    // empty slot
//    int rv = 1;
//    ListIterator<Unit> i = slots.listIterator(++unitIndex);
//    while( i.hasNext() )
//      {
//        if( i.next() != null )
//          {
//            return rv;
//          }
//        rv++;
//      }
//    return rv;
//  }

    
    public Part fitPart(int freeSlots) {
        if(freeSlots == 0)
            return null;
        Part fitPart = this.copy();
        while(fitPart.size() > freeSlots) {
            Part scaledPart = new Part();
            Part.PartIterator i = fitPart.iterator();
            while(i.hasNext()) {
                Unit unit = i.next();
                if(unit.getRhythmValue() != 1) {
                   Trace.log(3, "in fitPart, setting rhythmValue");
                    unit.setRhythmValue(unit.getRhythmValue()/2);
                    if(unit.getRhythmValue() <= MIN_RHYTHM_VALUE)
                        return null;
                }
                scaledPart.addUnit(unit);
            }
            fitPart = scaledPart;
        }
        return fitPart;
    }
    
    public void makeSwing(SectionInfo sectionInfo) {
        // The index here iterates through the start of every beat

        Style previousStyle = Style.getStyle("swing"); // TEMP: FIX!
        for(int i = 0; i+beatValue-1 < size; i += beatValue) {
            SectionRecord record = sectionInfo.getSectionRecordBySlot(i);
            
            Style s;
            if( record.getUsePreviousStyle() )
                {
                s = previousStyle;
                }
            else
                {
                s = record.getStyle();
                previousStyle = s;
                }
                        
            if( s == null )
              {
                ErrorLog.log(ErrorLog.FATAL, "It will not be possible to continue");
              }
            
            double swingValue = s.getSwing();

            //System.out.println("i = " + i + ", style = " + s + " swing = " + swingValue);

            // FIX: Notice the problem here when i < size, the original condition, is used.

            // we get the Units where a second sixteenth note would fall,
            // an eighth note, and a fourth sixteenth note

            Unit unit1 = slots.get(i+1*beatValue/4);
            Unit unit2 = slots.get(i+1*beatValue/2);
            Unit unit3 = slots.get(i+3*beatValue/4);

            // we only use swingValue if there is no second sixteenth note
            // (we don't want to swingValue a beat of four sixteenths)
            
            if(unit1 == null && unit2 != null) {

                /* formerly:
                // swingValue if there is a second eighth note
                if(unit2.getRhythmValue() == beatValue/2) {
                    slots.set(i+beatValue/2, null);
                    slots.set(i+(int)(beatValue*swingValue), unit2);
                }
                */

                int trailingRhythm = unit2.getRhythmValue();

                // swingValue if there is a second eighth note or longer
                if( trailingRhythm >= beatValue/2) {
                    
                    int offset = (int)(beatValue*swingValue);
                    Unit unit2mod = unit2.copy();
                    unit2mod.setRhythmValue(unit2.getRhythmValue()-offset);
                    slots.set(i+beatValue/2, null);
                    slots.set(i+offset, unit2mod);
                }

            }
        }

        try
          {
          // After the Units are shifted, go through and reset each rhythm value
          Part.PartIterator i = iterator();
          while(i.hasNext()) {
            int index = i.nextIndex();
            slots.get(index).setRhythmValue(getUnitRhythmValue(index));
            i.next();
            }
          }
        catch( Exception e )
          {
            
          }
    }
    
    /**
     * Shifts Units in the slots Vector to make each beat swingValue.
     * This is currently weird code that could probably be improved upon.
     * We only want certain rhythm blocks to swingValue.  This code will
     * only swingValue 8th 8th and 8th 16th 16th.  We can add in code to do
     * more if there are other rhythms that we want to swingValue, or we
     * can make the code look more general rather than being series
     * of special cases.
     */
    public void makeSwing() {
        // The index here iterates through the start of every beat

        for(int i = 0; i+beatValue-1 < size; i += beatValue) {

            // FIX: Notice the problem here when i < size, the original condition, is used.

            // we get the Units where a second sixteenth note would fall,
            // an eighth note, and a fourth sixteenth note

            Unit unit1 = slots.get(i+1*beatValue/4);
            Unit unit2 = slots.get(i+1*beatValue/2);
            Unit unit3 = slots.get(i+3*beatValue/4);

           if(unit1 == null && unit2 != null) {

                /* formerly:
                // swingValue if there is a second eighth note
                if(unit2.getRhythmValue() == beatValue/2) {
                    slots.set(i+beatValue/2, null);
                    slots.set(i+(int)(beatValue*swingValue), unit2);
                }
                */

               // copied from above in revision 1253
                int trailingRhythm = unit2.getRhythmValue();

                // swingValue if there is a second eighth note or longer
                if( trailingRhythm >= beatValue/2) {

                    int offset = (int)(beatValue*swing);
                    Unit unit2mod = unit2.copy();
                    unit2mod.setRhythmValue(unit2.getRhythmValue()-offset);
                    slots.set(i+beatValue/2, null);
                    slots.set(i+offset, unit2mod);
                }

            }
        }

        // After the Units are shifted, go through and reset each rhythm value
        Part.PartIterator i = iterator();
        while(i.hasNext()) {
            int index = i.nextIndex();
            slots.get(index).setRhythmValue(getUnitRhythmValue(index));
            i.next();
        }
    }

    /**
     * Returns the Unit at the specified slotIndex
     * @param slotIndex     the index of the Unit to get
     * @return Unit         the Unit at the specified index
     */
    public Unit getUnit(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          {
          return null;
          }
        else
          {
          return slots.get(slotIndex);
          }
    }
    
    /**
     * Returns a Part that contains the Units within the slot range specified.
     * This is abstract. It is instantiated in ChordPart and MelodyPart.
     * @param first     the first slot in the range
     * @param last      the last slot in the range
     * @return Part     the Part that contains the extracted chunk
     */
    
    public Part extract(int first, int last) {
      return null;
    }

    /**
     * Returns a Part that contains the slots with in the range specified.
     * @param first     the first slot in the range
     * @param last      the last slot in the range
     * @return Part     the Part that contains the extracted chunk
     */
    public Part extractSlots(int first, int last) {
      Trace.log(3, "extractSlots from " + first + " to " + last);
      if(last > size-1)
            last = size - 1;
                
        Part newPart = new Part(last-first+1);

        for(int i = first; i < last; i++) // was <=, changed May 13 2012
            newPart.slots.set(i - first, slots.get(i));
        // I'm getting an ArrayIndexOutOfBoundsException -1 here, 21 June 2012

        
        return newPart;
    }

    /**
     * Overwrites a section of the Part (starting at the specified index) 
     * with the Units contained in the given Part.
     * WARNING: DOES NOT WORK IF GIVEN PART WILL EXTEND OVER END OF THIS PART.
     * TODO: Fix this.  //rk Maybe I fixed it
     * 
     * @param part      the Part to paste into this
     * @param index     the slot at which to start pasting over
     */
    public void pasteOver(Part part, int index) {
        //Trace.log(3, "pasteOver " + part + " onto " + index);
        int limit = size();
        int incoming = part.size();
        if( index + incoming < limit )
          {
          limit = index + incoming;
          }

        for(int i = 0, j = index; j < limit; i++, j++) {
            Unit unit = part.getUnit(i);
            if( unit != null )
              {
              Unit newUnit = unit.copy();
              setUnit(j, newUnit);
              }
            else
              {
              setUnit(j, null);
              }
        }
    }
    
     /**
     * A rewritten version of pastOver
     * 
     * @param part      the Part to paste into this
     * @param index     the slot at which to start pasting over
     */
    public void newPasteOver(Part part, int index) {
        Trace.log(3, "pasteOver " + part + " onto " + index + " " + this.hashCode());
        int limit = size();
        int incoming = part.size();
//        if( index + incoming < limit )
//          {
//          limit = index + incoming;
//          }

        int i = 0;
        int j = index;
        for( ; i < incoming && j < limit; i++, j++) {
            Unit unit = part.getUnit(i);
            if( unit == null )
              {
              newSetUnit(j, null);
              }
            else
              {
              newSetUnit(j, unit.copy());
              }
        }
    }

    /**
     * Modified from pasteOver for time warping
     * Overwrites a section of the Part (starting at the specified index) 
     * with the Units contained in the given Part.
     * 
     * @param part      the Part to paste into this
     * @param index     the slot at which to start pasting over
     */
    public void altPasteOver(Part part, int index) {
        int limit = size();
        int incoming = part.size();
        if( index + incoming < limit )
          {
          limit = index + incoming;
          }

        // How to prevent long duration last note??

        //   int lastSlot = index + incoming - 1;

        //   setUnit(lastSlot, new Rest());

        for(int i = 0, j = index; j < limit; i++, j++) {
            Unit unit = part.getUnit(i);
            if( unit != null )
              {
              Unit newUnit = unit.copy();
              //int rhythmValue = newUnit.getRhythmValue();
              setUnit(j, newUnit);
              }
            else
              {
              setUnit(j, null);
              }
        }
    }

    /**
     * Overwrites a section of the Part (starting at the specified index)
     * with the slots contained in the given Part.
     *
     * @param part      the Part to paste into this
     * @param index     the slot at which to start pasting over
     */
    public void pasteSlots(Part part, int index) {
        if( part == null )
          return;
        Trace.log(2, "pasting part of length " + part.size() + " at index " + index);

        for(int i = 0; i < part.size(); i++) {
            if(slots.get(index+i) != null)
                unitCount--;
            if(part.slots.get(i) != null)
                unitCount++;
            slots.set(index+i, part.slots.get(i));
        }

        int prevIndex = this.getPrevIndex(index);
        if(prevIndex == -1)
            prevIndex = 0;
        int rv = getUnitRhythmValue(prevIndex);
        Trace.log(3, "in pasteSlots, setting rhythmValue");
        getUnit(prevIndex).setRhythmValue(rv);
    }
   
    /**
     * Writes the Part to the passed BufferedWriter.
     * @param out       the BufferedWriter to write the Part onto
     */
    public void save(BufferedWriter out) throws IOException {
        out.write(title);
        out.newLine();
        out.write(composer);
        out.newLine();
        out.write(Integer.toString(keySig));
        out.newLine();
        out.write(Integer.toString(metre[0]));
        out.newLine();
        out.write(Integer.toString(metre[1]));
        out.newLine();
        out.write(Double.toString(swing));
        out.newLine();
        out.write(Integer.toString(instrument) + 1);
        out.newLine();
        out.write(Integer.toString(volume));
        out.newLine();
        
    	Part.PartIterator i = iterator();
    	while(i.hasNext())
    		i.next().save(out);
    }
    
    /**
     * Writes the Part to the passed BufferedWriter in Leadsheet notation.
     * @param out       the BufferedWriter to write the Part onto
     */
    
public void saveLeadsheet(BufferedWriter out, String type) throws IOException
  {
    out.write("(part");
    out.newLine();
    out.write("    (type " + type + ")");
    out.newLine();
    out.write("    (title " + title + ")");
    out.newLine();
    out.write("    (composer " + composer + ")");
    out.newLine();
    out.write("    (instrument " + instrument + ")");
    out.newLine();
    out.write("    (volume " + volume + ")");
    out.newLine();
    out.write("    (key " + keySig + ")");
    out.newLine();
    
    if( this instanceof MelodyPart )
      {
        out.write("    (stave " + staveType.toString().toLowerCase() + ")");
        out.newLine();
      }
// For now, saving roadmaps is disabled
//    else
//    {
//        out.write(Formatting.prettyFormat(4, ((ChordPart)this).getRoadMap() == null ? "" : ((ChordPart)this).getRoadmapPoly()));
//        out.newLine();
//    }

    out.write(")");
    out.newLine();

    Note.initializeSaveLeadsheet();

    Part.PartIterator i = iterator();

    // Should be refactored into separate methods for each derived class
    
    if( this instanceof MelodyPart )
        {
        while( i.hasNext() )
          {
            i.next().saveLeadsheet(out, metre);
          }
      }
   else
      {
        SectionInfo sectionInfo = ((ChordPart) this).getSectionInfo();
        
        Iterator<SectionRecord> sec = sectionInfo.iterator();
        
        SectionRecord record = sec.next();
        
        boolean lastSection = !sec.hasNext();
        
        Chord residualChord = null;
        
        int slot = 0;
        
        int slotLimit = size();
        
        int nextSectionStart;
        
        //iSystem.out.println("slotLimit = " + slotLimit);
        
        Chord chord = null;
        
        int sectionsToGo = sectionInfo.size();
        
        do // do-while
          {
            //System.out.println("\nrecord = " + record);
            
            // Save the section record
            saveSectionInfo(out, record);
            
            // Get the next section record, if any.
            if( sec.hasNext() )
              {
              record = sec.next();
              nextSectionStart = record.getIndex();
              }
            else
              {
              nextSectionStart = slotLimit;
              }
            
            //System.out.println("next section start = " + nextSectionStart);
            
            // Pack Chords into section
            
            while( (chord != null || i.hasNext()) && slot < nextSectionStart ) 
              {
                if( chord == null )
                  {
                    Chord nextChord = (Chord)i.next();
                    if( nextChord != null )
                      {
                        chord = nextChord.copy();
                      }
                  }
                // Otherwise use the residue of previous chord
                
                assert chord != null;
                // Where the next slot would normally be
                int nextSlot = slot + chord.getRhythmValue();
                
                if( nextSlot <= nextSectionStart )
                  {
                  // This chord fits in the current section.
                    
                  chord.saveLeadsheet(out, metre);
                  chord = null;
                  slot = nextSlot;
                  }
                else
                  {
                    // This chord does not fit in the current section.
                    // Calculate how much of this section can be used.
                    int available = nextSectionStart - slot;
                    chord.setRhythmValue(available);
                    chord.saveLeadsheet(out, metre);
                    
                    // Determine what is left over.
                    int residual = nextSlot - nextSectionStart;
                    chord.setRhythmValue(residual);

                    //System.out.println("overflow at slot " + slot + ", next section start = " + nextSectionStart + " " + chord + ", residual = " + residual);
                    
                    // This should force the end of this while, among other things
                    
                    slot = nextSectionStart;
                  }
                
              }
          sectionsToGo--;
          }
        while( sectionsToGo > 0 ); // end of do-while
      }
  }


private void saveSectionInfo(BufferedWriter out, SectionRecord record) throws IOException
  {
    String styleName = record.getUsePreviousStyle() ? "" : " " + record.getStyleName();
    
    if( record.getIsPhrase() )
      {
        out.newLine();
        out.write("(phrase (style" + styleName + ")) ");
        out.newLine();
      }
    else
      {
        out.newLine();
        out.newLine();
        out.write("(section (style" + styleName + ")) ");
        out.newLine();
        out.newLine();
      }
  }


  
    /**
     * Returns a PartIterator pointing to the start of the Part that
     * can iterate over the entire Part
     * @return PartIterator   iterator pointing to the start of this Part
     */
    public Part.PartIterator iterator() {
        return iterator(0);
    }


    /**
     * Returns a PartIterator pointing to the indicated index.
     * @param index             the index to start iterating at
     * @return PartIterator     iterator pointing to the indicated index
     */
    public Part.PartIterator iterator(int index) {
        return new Part.PartIterator(slots, unitCount, index);
    }

    /**
     * The PartIterator iterates forward (but not backward) over the Unit
     * objects in a Part, skipping over the empty slots.
     * @see     Part
     * @see     Unit
     * @author  Stephen Jones
     */
    public class PartIterator implements ListIterator {

        /**
         * points to the set of Units to iterate over
         */
        protected ArrayList<Unit> slots;

        /**
         * the index of the Unit object returned with a call to next()
         */
        protected int unitIndex = 0;

        /**
         * the slot index of the Unit object returned with a call to next()
         */
        protected int nextIndex = 0;

        /**
         * the slot index of the Unit object returned with a call to previous()
         */
        protected int prevIndex = -1;

        /**
         * the total number of Unit objects contained in slots
         */
        protected int unitCount;

        /**
         * Creates a new PartIterator that iterates over the specified
         * slots.
         * @param slots         a Vector<Unit> containing Units to iterate over
         * @param unitCount     the number of Unit objects in the Vector<Unit>
         * @param nextIndex     the index of the first Unit to point to
         */
        public PartIterator(final ArrayList<Unit> slots, int unitCount, 
                                                        int nextIndex) {
            this.slots = slots;
            this.unitCount = unitCount;
            if(nextIndex != 0) {
                int i = 0;
                while(i < nextIndex) {
                    if(slots.get(i) != null)
                        unitIndex++;
                    i++;
                }
            }
            this.nextIndex = nextIndex;
            this.prevIndex = nextIndex - 1;
        }

        /**
         * Returns true if there is a next Unit object
         * @return boolean      true if there is a next() Unit, false otherwise
         */
        public boolean hasNext() {
            return (unitIndex < unitCount && nextIndex < slots.size() );
        }

        /**
         * Returns true if there is a previous Unit object
         */
        public boolean hasPrevious() {
            return (unitIndex > 0);
        }

        /**
         * Returns the next Unit object in the slots Vector<Unit>
         * @return Unit         the next Unit object
         */
        public Unit next() {
            if(hasNext() ) {
                prevIndex = nextIndex;
                Unit unit = slots.get(nextIndex);
                nextIndex = getNextIndex(nextIndex);
                unitIndex++;
                return unit;
            }
            throw new IndexOutOfBoundsException("No next() Unit");
        }

        /**
         * Returns the previous Unit object in the slots Vector<Unit>
         * @return Unit         the previous Unit object
         */
        public Unit previous() {
            if(hasPrevious()) {
                nextIndex = prevIndex;
                Unit unit = slots.get(prevIndex);
                prevIndex = getPrevIndex(prevIndex);
                unitIndex--;
                return unit;
            }
            throw new IndexOutOfBoundsException("No previous() Unit");
        }

        /**
         * Returns the slot index of the next Unit object in the Vector<Unit>
         * @return int          the index of the next Unit
         */
        public int nextIndex() {
            if(hasNext())
                return nextIndex;
            throw new IndexOutOfBoundsException("No next() Unit");
        }

        /**
         * Returns the slot index of the previous Unit object in slots.
         * @return int          the index of the previous Unit
         */
        public int previousIndex() {
            if(hasPrevious())
                return prevIndex;
            throw new IndexOutOfBoundsException("No previous() Unit");
        }

        /**
         * Unsupported operation.
         */
        public void add(Object object) {
            throw new UnsupportedOperationException("Unsupported operation.");
        }
        
        /**
         * Unsupported operation.
         */
        public void set(Object object) {
            throw new UnsupportedOperationException("Unsupported operation.");
        }
        
        /**
         * Unsupported operation.
         */
        public void remove() {
            throw new UnsupportedOperationException("Unsupported operation.");
        }
    }
}
