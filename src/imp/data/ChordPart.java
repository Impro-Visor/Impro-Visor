/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College
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
import imp.midi.MidiSequence;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.roadmap.RoadMap;
import imp.roadmap.RoadMapFrame;
import imp.util.ErrorLog;
import imp.util.Trace;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 * An extension of the Part class that contains only Chord objects.
 * This is useful to separate simple chord drawing (names only,) from
 * the more complex melody drawing.
 * @see         Chord
 * @see         Part
 * @author      Stephen Jones
*/
public class ChordPart extends Part implements Serializable{
    
    Polylist roadmapPoly = Polylist.list(RoadMap.ROADMAP_KEYWORD);
    
    RoadMap roadmap = null;

    private SectionInfo sectionInfo = new SectionInfo(this);

    /**
     * the default chord volume
     */
    public static final int DEFAULT_CHORD_VOLUME = DEFAULT_VOLUME - 20;

    /**
     * Creates an empty ChordPart.
     */
    public ChordPart() {
        super();
        volume = DEFAULT_CHORD_VOLUME;
    }

    /**
     * Creates a ChordPart with the given size.
     * @param size      the number of slots in the ChordPart
     */
    public ChordPart(int size) {
        super(size);
        Trace.log(3, "creating new chord part of size " + size);
        if(size != 0)
          {
            slots.set(0, new Chord(size));
          }
        volume = DEFAULT_CHORD_VOLUME;
    }
    
    /**
     * Sets the given chord at the specified slot index.
     * @param slotIndex         the index to put the chord at
     * @param chord             the Chord to put at the index
     */
    public void setChord(int slotIndex, Chord chord) {
        setUnit(slotIndex, chord);
    }
    
  @Override
    public void setSize(int size) {
        super.setSize(size);
        sectionInfo.setSize(size);
    }
    
  @Override
    public int getMeasureLength() {
        int beatVal = WHOLE/metre[1];
        int beatsPerBar = metre[0];
        return beatsPerBar * beatVal;
    }
    
    /**
     * Adds a chord to the end of the ChordPart.
     * @param chord     the Chord to add
     */

    public void addChord(String symbol, int duration) {
        Chord chord = Chord.makeChord(symbol, duration);
        if( chord != null )
          {
          addUnit(chord);
          return;
          }

	Polylist exploded = Key.explodeChord(symbol);
	if( exploded != null )
	  {
	  String bassNote = 
	      PitchClass.upperCaseNote((String)exploded.fourth());

	  Chord bassChord = Chord.makeChord(bassNote);
	  if( bassChord != null )
	    {
	    ErrorLog.log(ErrorLog.WARNING,
			 "Chord symbol " + symbol
			 + " is unknown, using the bass note: " 
			 + bassNote);
	    addUnit(bassChord);
	    return;
	    }

	  ErrorLog.log(ErrorLog.WARNING,
		       "Chord symbol and bass " + symbol
		       + " are unknown, using " + NOCHORD);

	  addUnit(new Chord(NOCHORD, duration));
          }
	}


    /**
     * Adds a chord to the end of the ChordPart.
     * @param chord     the Chord to add
     */
    public void addChord(Chord chord) {
        addUnit(chord);
    }

    /**
     * Returns the Chord at the specified index.
     * @param slotIndex         the index of the chord to get
     * @return Chord            the chord at the index
     */
    public Chord getChord(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          {
          return null;
          }
        if (getUnit(slotIndex) instanceof Chord){
            return (Chord)getUnit(slotIndex);
        }
        return null;
    }

    /**
     * Returns the Chord sounding at this index.
     * @param slotIndex         the index to check at
     * @return Chord            the chord sounding at the index
     */
    public Chord getCurrentChord(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          {
          return null;
          }
        return getChord(getCurrentChordIndex(slotIndex));
    }

    /**
     * Returns the index of the Chord sounding at this index.
     * @param slotIndex         the index to check at
     * @return int              the index of the sounding chord
     */
    public int getCurrentChordIndex(int slotIndex) {
        if(getChord(slotIndex) != null)
          {
            return slotIndex;
          }
        else
          {
            return getPrevIndex(slotIndex);
    }
    }
   
    public Chord getNextUniqueChord(int slotIndex) {
        int nextUniqueChordIndex = getNextUniqueChordIndex(slotIndex);
        if( nextUniqueChordIndex < 0 )
          {
          return null;
          }
        return getChord(nextUniqueChordIndex);
    }
    
/**
 * Returns index of next unique chord, or -1 if none.
 */
public int getNextUniqueChordIndex(int slotIndex)
  {
    if( slotIndex < 0 || slotIndex >= size )
      {
        return -1;
      }
    
    int currentChordIndex = getCurrentChordIndex(slotIndex);
    Chord currentChord = getChord(currentChordIndex);
    
    if( currentChord == null )
      {
        return -1;
      }
    
    Chord nextChord = currentChord;
    int nextChordIndex = currentChordIndex;
    while( nextChord == null || nextChord.getName().equals(currentChord.getName()) )
      {
        nextChordIndex = getNextChordIndex(nextChordIndex);
        if( nextChordIndex >= size )
          {
            return -1;
          }
        nextChord = getChord(nextChordIndex);
      }
  if( nextChordIndex >= size )
          {
            return -1;
          }
  return nextChordIndex;
  }
    
    public int getPrevUniqueChordIndex(int slotIndex)
    {
        if(slotIndex < 0 || slotIndex >= size)
          {
            return -1;
          }
        int currentChordIndex = getCurrentChordIndex(slotIndex);
        Chord currentChord = getChord(currentChordIndex);
        Chord prevChord = currentChord;
        int prevChordIndex = currentChordIndex;
        
        while(prevChord.getName().equals(currentChord.getName()))
        {
            prevChordIndex = getPrevIndex(prevChordIndex);
            if(prevChordIndex<= -1)
            {
                return -1;
            }
            prevChord = getChord(prevChordIndex);
        }
        return prevChordIndex;
    }
    
    /**
     * Returns the Chord after the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord after the specified index
     */
    public Chord getNextChord(int slotIndex) {
        return (Chord)getNextUnit(slotIndex);
    }
    
    /**
     * Returns the Chord after the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord after the specified index
     */
    public int getNextChordIndex(int slotIndex) {
      for( int i = slotIndex + 1; i < size; i++ )
        {
        Unit unit = slots.get(i);
        if( unit != null && unit instanceof Chord )
          {
           return i;
          }
      }
   return size;
    }

    /**
     * Returns the Chord before the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord before the specified index
     */
    public Chord getPrevChord(int slotIndex) {
        for( int i = slotIndex-1; i >= 0; i-- )
          {
            Unit unit = slots.get(i);
            if( unit != null && unit instanceof Chord )
              {
                return (Chord)unit;
              }
          }
        return null;
    }
    
    public void getMetre(int metre[]){
    //    System.out.println("SectionInfo = " + sectionInfo);
        sectionInfo.getMetre(metre);
    }
    
    public void setMetre(int metre[]){
        sectionInfo.setMetre(metre);
    }
    
/**
 * Returns an exact copy of this Part
 *
 * @return Part copy
 */

@Override
public ChordPart copy()
  {
    ChordPart newPart = new ChordPart(size);
    PartIterator i = iterator();
    while( i.hasNext() )
      {
        int nextIndex = i.nextIndex();
        Unit unit = i.next();
        if( unit == null )
          {
            newPart.slots.set(nextIndex, null);
          }
        else
          {
            newPart.slots.set(nextIndex, unit.copy());
          }
      }

    newPart.sectionInfo = sectionInfo.copy();
    newPart.unitCount = unitCount;
    newPart.title = title;
    newPart.composer = composer;
    newPart.volume = volume;
    newPart.setInstrument(instrument);
    newPart.keySig = keySig;
    newPart.setMetre(metre[0], metre[1]);
    newPart.swing = swing;

    return newPart;
  }

public void setChordInstrument(int instrument)
  {
    //System.out.println("chordPart setChordInstrument to " + instrument);
    super.setInstrument(instrument);
  }

public boolean setStyle(String name)
  {
    return sectionInfo.setStyle(name);
  }

public void setStyle(Style s)
  {
    sectionInfo.setStyle(s);
  }

public void addSection(String styleName, int n, boolean isPhrase)
  {
    sectionInfo.addSection(styleName, n, isPhrase);
  }


/**
 * This should be deprecated, as the ChordPart can contain numerous styles,
 * one for each section. However, lots of uses remain as of 14 July 2013.
 * See getStyleAtSlot.
 * @return 
 */

public Style getStyle()
  {
    return sectionInfo.getStyle();
  }
public ArrayList<Block> setAllStyles(ArrayList<Block> blocks)
{
    return sectionInfo.setAllStyles(blocks);
}

//public ArrayList<Block> setAllStyles(ArrayList<Block> blocks, ArrayList<SectionRecord> secRecs)
//{
//    return sectionInfo.setAllStyles(blocks, secRecs);
//}


public String getStyleName(int n)
{
    return sectionInfo.getStyleName(n);
}

/**
 * getStyleAtSlot returns Style operative at a given slot in the ChordPart
 * @param slot
 * @return 
 */
            
public Style getStyleAtSlot(int slot)
  {
  return sectionInfo.getStyleAtSlot(slot);
  }


public SectionInfo getSectionInfo()
  {
    return sectionInfo;
  }

public void setSectionInfo(SectionInfo si)
  {
    sectionInfo = si;
  }

public boolean hasOneSection()
  {
    return sectionInfo.hasOneSection();
  }

public long render(MidiSequence seq,
                   long time,
                   Track track,
                   int transposition,
                   boolean useDrums,
                   int endLimitIndex,
                   boolean constantBass)
        throws InvalidMidiDataException
  {
    // to trace sequencing info:
    // System.out.println("ChordPart time = " + time + ", endLimitIndex = " + endLimitIndex);
    long result = sectionInfo.render(seq, 
                                     time, 
                                     track, 
                                     transposition, 
                                     useDrums, 
                                     endLimitIndex, 
                                     constantBass);
    return result;
  }


    /**
     * Returns a ChordPart that contains the Units within the slot range specified.
     * @param first     the first slot in the range
     * @param last      the last slot in the range
     * @return Part     the Part that contains the extracted chunk
     */
    @Override
    public ChordPart extract(int first, int last) {
        ChordPart newPart = new ChordPart();

        // If there is no chord to start, search backward and use the previous chord.
        // Since there is always a chord (possibly NC) in the first slot, we are guaranteed
        // to get one.
        
        if( getUnit(first) != null )
          {
          newPart.addUnit(getUnit(first).copy());
          }
        else
          {
          for( int j = first-1; j >= 0; j-- )
            {
            if( getUnit(j) != null )
              {
              // chop of the beginning of this chord's duration that
              // isn't selected
              Unit unit = getUnit(j).copy();
              unit.setRhythmValue(getUnitRhythmValue(first));
              newPart.addUnit(unit);
              break;
              }
            }
          }

        // Complete with the remainder of the chords.

        for(int i = first+1; i <= last; i++)
          {
            if(getUnit(i) != null)
                {
                newPart.addUnit(getUnit(i).copy());
                }
          }

        // We don't want the accompaniment to play past the end
        if(newPart.size() > last - first + 1)
          {
            newPart.setSize(last - first + 1);
          }
        
        newPart.setSectionInfo(sectionInfo.extract(first,last,newPart));
        
        return newPart;
    }

    /**
     * Returns a copy of this ChordPart transposed by rise and targeting key
     * @return ChordPart   copy
     */
    public ChordPart transpose(int rise, Key key) {
        ChordPart newPart = new ChordPart(size);
        PartIterator i = iterator();
        while(i.hasNext()) {
            Chord oldChord = (Chord)i.next();
            Chord newChord = oldChord.copy();
            newChord.setName(Key.transposeChord(oldChord.getName(), rise, key));
        }

        newPart.unitCount = unitCount;
        newPart.title = title;
        newPart.composer = composer;
        newPart.volume = volume;
        newPart.setInstrument(instrument);
        newPart.keySig = keySig;
        newPart.setMetre(metre[0], metre[1]);
        newPart.swing = swing;
        newPart.sectionInfo = sectionInfo.copy();

        return newPart;
    }
 
    
/**
 * Get the ChordSymbols of this ChordPart as an ArrayList<ChordSymbol>
 * @return 
 */
    
public ArrayList<ChordSymbol> getChordSymbols()
  {
    ArrayList<ChordSymbol> result = new ArrayList<ChordSymbol>();

    PartIterator i = iterator();
    while( i.hasNext() )
      {
        Chord chord = (Chord) i.next();
        result.add(chord.getChordSymbol());
      }
    return result;
  }


/**
 * Get the durations of chords of this ChordPart as an ArrayList<ChordSymbol>
 * @return 
 */

public ArrayList<Integer> getChordDurations()
  {
    ArrayList<Integer> result = new ArrayList<Integer>();

    PartIterator i = iterator();
    while( i.hasNext() )
      {
        Chord chord = (Chord) i.next();
        result.add(chord.getRhythmValue());
      }
    return result;
  }

/**
 * Get all the chords in the progression
 * @author Mark Heimann
 * @return ArrayList of all chords
 */
public ArrayList<Chord> getChords() {
    ArrayList<Chord> allChords = new ArrayList<Chord>();

    PartIterator chordIter = iterator();
    while (chordIter.hasNext()) {
        allChords.add((Chord) chordIter.next());
    }
    
    return allChords;
}


public ArrayList<imp.roadmap.brickdictionary.Block> toBlockList()
{
return sectionInfo.toBlockList();
}

/**
 * Populate a RoadMapFrame with this ChordPart
 * @param roadmap 
 */

public void toRoadMapFrame(RoadMapFrame roadmapFrame)
  {
    roadmapFrame.addBlocks(0, toBlockList());
    this.roadmap = roadmapFrame.getRoadMap();
  }

/**
 * Add chords in the current selection in RoadMapFrame to this ChordPart.
 */

public void addFromRoadMapChordBlocks(ArrayList<imp.roadmap.brickdictionary.ChordBlock> chords, String previousStyleName)
  {
    Iterator<imp.roadmap.brickdictionary.ChordBlock> i = chords.iterator();

    int totalSlots = 0;
    int sectionStart = 0;
    
    while( i.hasNext() )
      {
        ChordBlock chordBlock = i.next();
        
        Chord chord = new Chord(chordBlock);
        
        //String name = chord.getName();
        if( chord.getRhythmValue() > 0 ) {
            // Note: 0 duration causes addUnit to fail.
            totalSlots += chordBlock.getDuration();
            addChord(chord);
            
            String styleName = chordBlock.getStyleName();
            if( styleName.equals(Style.USE_PREVIOUS_STYLE) )
              {
                styleName = previousStyleName;
              }
            else
              {
                previousStyleName = styleName;
              }
            // The second condition below is needed in case the last block
            // in the selection is not a section end in the roadmap.
            // In this case we still want to create a proper section with
            // a style.
            
            if( chordBlock.isSectionEnd() || !i.hasNext() ) {
                addSection(styleName,
                           sectionStart,
                           chordBlock.isPhraseEnd());
                sectionStart = totalSlots;
            }
        }
      }
  }

    public void fixDuplicateChords(ChordPart chordpart, int resolution) {
        Chord prevChord = null;
        String prevChordName;
        Chord thisChord;
        String thisChordName;
        for (int i = 0; i < chordpart.size; i = i + resolution) {
            thisChord = chordpart.getChord(i);
            if (prevChord != null && thisChord != null) {
                prevChordName = prevChord.getName();
                thisChordName = thisChord.getName();
                if (prevChordName.equals(thisChordName)) {
                    chordpart.delUnit(i);
                }
            }
            prevChord = chordpart.getCurrentChord(i);
        }
    }

    public void setRoadmapPoly(Polylist roadmapPoly)
    {
        this.roadmapPoly = roadmapPoly;
        if( roadmapPoly.nonEmpty() && roadmapPoly.first() instanceof Polylist )
          {
          roadmap = RoadMap.fromPolylist(roadmapPoly);
          }
    }
    
    public Polylist getRoadmapPoly()
    {
        return roadmap.toPolylist();
    }
    
    public RoadMap getRoadMap()
      {
       //System.out.println("Getting Roadmap Poly as " + Formatting.prettyFormat(roadmapPoly));
        //roadmap = new RoadMap(roadmapPoly);
        //System.out.println("The reconstructed roadmap is " + Formatting.prettyFormat(roadmap.toPolylist()));
        return roadmap;
      }
    
    public void setRoadmap(RoadMap roadmap)
      {
        this.roadmap = roadmap;
      }
    
    public Block getBlock(int index)
      {
        if( roadmap == null )
          {
            return null;
          }
        return roadmap.getBlock(index);
      }
    
    public Block getBlockAtSlot(int slot)
      {
       if( roadmap == null )
          {
            return null;
          }
        return roadmap.getBlockAtSlot(slot);
      }
    
    public ArrayList<SectionRecord> getSectionRecords()
    {
        return sectionInfo.getSectionRecords();
    }
    
    public ArrayList<String> getSectionRecordStyleNames()
    {
        return sectionInfo.getSectionRecordStyleNames();
    }
    
    public void reloadStyles()
    {
        sectionInfo.reloadStyles();
    }

}
