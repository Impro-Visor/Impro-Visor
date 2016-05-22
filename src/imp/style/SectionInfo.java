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

package imp.style;

import imp.style.Style;
import imp.midi.MidiSequence;
import imp.Constants;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.util.Preferences;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import polya.PolylistBuffer;
/**
 * SectionInfo was originally done by Stephen Jones when sections were
 * first added to the software. On July 27, 2011, Robert Keller refactored
 * the code, by transcribing the separate ArrayLists into a single ArrayList
 * of SectionRecord. The purpose was to enable phrases, and possibly other
 * information to be added more easily.
 * @author keller
 */

public class SectionInfo implements Constants, Serializable {
    private ChordPart chords;
    
    private int[] metre = new int[2];
    
    /**
     * Sets the metre of the SectionInfo
     * metre is now represented in most places as a 2-elt array, where
     * the first element is the top of the time signature, and the second
     * element is the bottom.
     */
    public void setMetre(int top, int bottom) {
        metre[0] = top;
        metre[1] = bottom;
//        chordProg.setMetre(top, bottom);
//        ListIterator<MelodyPart> i = partList.listIterator();
//	
//        while(i.hasNext())
//            {
//            i.next().setMetre(top, bottom);
//            }
    }
    
    public void setMetre(int metre[])
      {
        setMetre(metre[0],metre[1]);
      }
    
    /**
     * Copy this SectionInfo's metre setting into the argument array of dimension 2.
     * @param metre 
     */
    
    public void getMetre(int metre[])
      {
        metre[0] = this.metre[0];
        metre[1] = this.metre[1];
      }
    
    /**
     * Returns the SectionInfo's metre
     * @return int              the metre of the Score
     */
    public int[] getMetre() {
        return metre;
    }
    
    private ArrayList<SectionRecord> records = new ArrayList<SectionRecord>();

    public SectionInfo(ChordPart chords) {
        this.chords = chords;
        this.metre[0] = DEFAULT_METRE[0];
        this.metre[1] = DEFAULT_METRE[1];

        // RK 1/4/2010 The following was causing problems with countin resetting
        // the chord instrument, as reported by a user. It is not clear
        // why this was needed, but it seems to be causing an undesirable
        // instrument change through an indirect path.
        // It should be revisited.

        //style.setChordInstrument(chords.getInstrument(), "SectionInfo");

        addSection(Preferences.getPreference(Preferences.DEFAULT_STYLE), 0, false);
    }

    public SectionInfo copy() {
        SectionInfo si = new SectionInfo(chords);
        si.records = new ArrayList<SectionRecord>();
        
        for(SectionRecord record: records )
          {
            si.records.add(new SectionRecord(record));
          }

        return si;
    }
    
    
    public void addSection(String styleName, int n, boolean isPhrase) {
        ListIterator<SectionRecord> k = records.listIterator();
        
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            int index = record.getIndex();
            if( index == n )
              {
                k.remove();
                break;
              }
            else if( index > n )
              {
                k.previous();
                break;
              }
          }
        k.add(new SectionRecord(styleName, n, isPhrase));
    }
    
    public void modifySection(Style style, int n, boolean isPhrase)
      {
        SectionRecord record = getSectionRecordByIndex(n);
        record.setStyle(style);
      }
    
    public void reloadStyles() {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            k.remove();
            k.add(new SectionRecord(record.getStyleName(), 
                                    record.getIndex(), 
                                    record.getIsPhrase()));
          }
    }
    
    public void newSection(int index) {
        int measureLength = chords.getMeasureLength();
        
        SectionRecord record = records.get(index);
        int startIndex = record.getIndex();
        
        int endIndex = chords.size();
        
        if(index + 1 < size())
          {
          SectionRecord nextRecord = records.get(index+1);
          endIndex = nextRecord.getIndex();
          }
        
        int measure = (endIndex - startIndex) / measureLength;
        
        if(measure%2 == 0)
            measure /= 2;
        else
            measure = measure/2 + 1;
        
        addSection(Style.USE_PREVIOUS_STYLE, 
                   startIndex + measure*measureLength,
                   record.getIsPhrase());
    }

    /**
     * m = nq + r 
     * (m-r) sections would get q bars
     * r sections would get q+1 bars
     * 
     * m = the original number of bars
     * n = split
     * q = m/n
     * r = m%n
     */
    public boolean nWaySplit(int index, int split) {
        int m;        
        if(index + 1 < size())
            m = getSectionMeasure(index + 1) - getSectionMeasure(index);
        else
            m = measures() - getSectionMeasure(index) + 1;
        //System.out.println("m = " + m + ", split = " + split);        
        if( m < split )
          {
            return false;
          }
        int q = m/split;
        int r = m%split;
        
        SectionRecord delete = getSectionRecordByIndex(index);
        String styleName = delete.getStyleName();
        int newIndex = delete.getIndex(); //slots
        boolean isPhrase = delete.getIsPhrase();
        
        records.remove(index);
        
        for(int j = 0; j < split; j++)
        {
            if(j != 0)
                styleName = Style.USE_PREVIOUS_STYLE;
            records.add(index + j, new SectionRecord(styleName, newIndex, isPhrase));
            newIndex = measureToSlotIndex(slotIndexToMeasure(newIndex) + q);
        }
        
        return true;
    }
    
    public Integer getPrevSectionIndex(int n) {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            int index = record.getIndex();
            if( index > n )
              {
                k.previous();
                index = k.previous().getIndex();
                if( index == n && k.hasPrevious() )
                  {
                    return k.previousIndex();
                  }
                else if( index == n )
                  {
                    return -1;
                  }
                return index;
              }
          }

        return -1;
    }
    
    public Integer getNextSectionIndex(int n) {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            int index = k.next().getIndex();
            if( index > n )
              {
                return index;
              }
          }
        return null;
    }

    public int sectionAtSlot(int n) {
       Iterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            if( record.getIndex() == n )
              {
                if( record.getIsPhrase() )
                  {
                    return Block.PHRASE_END;
                  }
                else
                  {
                    return Block.SECTION_END;
                  }
              }
          }
        return Block.NO_END;
    }
    
public Style getStyleFromSlots(int n)
  {

    return getSectionRecordBySlot(n).getStyle();

  }

/**
 * Get section by slot index
 * @param slot
 * @return 
 */
public SectionRecord getSectionRecordBySlot(int slot)
  {
    ListIterator<SectionRecord> k = records.listIterator();
    SectionRecord s = k.next();
    SectionRecord previous = s;
    while( s.getIndex() <= slot && k.hasNext() )
      {
        previous = s;
        s = k.next();
      }

    s = s.getIndex() <= slot ? s : previous;

    //System.out.println("slot = " + slot + " using s = " + s);
    return s;
  }

public SectionRecord getSectionRecordByIndex(int n)
  {
    return records.get(n);
  }

    public SectionInfo extract(int first, int last, ChordPart chords) {
        SectionInfo si = new SectionInfo(chords);
        
        si.records = new ArrayList<SectionRecord>();
        
        Iterator<SectionRecord> k = records.iterator();
        
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            String styleName = record.getStyleName();
            int index = record.getIndex() - first;
            if( index < 0 )
              {
                si.records.add(new SectionRecord(styleName, 0, record.getIsPhrase()));
              }
            else if( index <= last - first )
              {
                si.records.add(new SectionRecord(styleName, index, record.getIsPhrase()));
              }
          }

        return si;
    }

/**
 * getStyleAtSlot returns Style operative at a given slot in the ChordPart
 * @param slot
 * @return 
 */
            
public Style getStyleAtSlot(int slot)
  {
    return Style.getStyle(getStyleNameAtSlot(slot));
  }


/**
 * getStyleNameAtSlot returns the name of the Style operative at a given slot
 * in the ChordPart
 * @param slot
 * @return 
 */
            
public String getStyleNameAtSlot(int slot)
  {
    String previousStyleName = "no-style";
    String styleName = previousStyleName;
    for( SectionRecord k: records )
      {
         styleName = k.getStyleName();
         if( styleName.equals("*") )
           {
            styleName = previousStyleName;
            }
         if( k.getIndex() >= slot )
          {
             return styleName;
          }
         previousStyleName = styleName;
      }
    return styleName;
  }

    public Style getStyle(int n) {
        if( records == null )
          {
            return null;
          }
        return records.get(n).getStyle();
    }

    public String getStyleName(int n) {
        if( records == null )
          {
            return null;
          }
        return records.get(n).getStyleName();
    }
        
    public int getStyleIndex(int n) {
        return records.get(n).getIndex();
    }
    
    public boolean getIsPhrase(int n) {
        return records.get(n).getIsPhrase();
    }    
    
    public void setIsPhrase(int n, boolean value)
      {
        records.get(n).setIsPhrase(value);
      }
    
    public int size() {
        return records.size();
    }
    
    public boolean hasOneSection()
      {
        return records.size() == 1;
      }

    public String getInfo(int index) {
        SectionRecord record = records.get(index);
        
        String styleName = record.getStyleName();
        int startIndex = getSectionMeasure(index);
        int endIndex = measures();
        if(index + 1 < size())
            endIndex = getSectionMeasure(index+1) - 1;
        
        String info = "mm. " + startIndex + "-" + endIndex + ": " + styleName;
        if(startIndex == endIndex)
            info = "m. " + startIndex + ": " + styleName;

        return info;
    }
    
    //markermarkermarker
    public void setSpecificCell(Object aValue, int row, int column){
        
        switch(column){
            case 1:
                int intValue;
                try
                {
                    intValue = Integer.parseInt(aValue.toString());
                    adjustSection(row, 
                                  intValue,
                                  records.get(row).getIsPhrase(), 
                                  false);
                }
                catch( NumberFormatException e )
                {
                }
                break;
            case 2:
                int endValue;
                try {
                    endValue = Integer.parseInt(aValue.toString());
                    adjustEndOfSection(row,
                                       endValue,
                                       records.get(row).getIsPhrase(),
                                       false);
                }
                catch( NumberFormatException e )
                {
                }
                break;
            case 3:
                int barValue;
                try {
                    barValue = getSectionMeasure(row)+(Integer.parseInt(aValue.toString()))-1;
                    adjustEndOfSection(row,
                                        barValue,
                                        records.get(row).getIsPhrase(),
                                        false);                                        
                }
                catch( NumberFormatException e)
                {                
                }
                break;
            default:
                getSectionRecordByIndex(row).setColumn(aValue, column);
        }
    }
    
    public int getSectionMeasure(int index) {
        return slotIndexToMeasure(records.get(index).getIndex());
    }
    
    public int slotIndexToMeasure(int index) {
        int measureLength = chords.getMeasureLength();
        return index / measureLength + 1;
    }
    
    public int measureToSlotIndex(int measure) {
        int measureLength = chords.getMeasureLength();
        return (measure - 1)*measureLength;
    }
    
    public int measures() {
        int measureLength = chords.getMeasureLength();
        return chords.size()/measureLength;
    }
                            //row      , startIndex
    public void adjustSection(int index, int newMeasure, boolean isPhrase, boolean usePreviousStyleChecked) {
         //System.out.println("1 records = " + records);
         
        // Do not move first record
        // Its phrase value can be set in place
        
        if( newMeasure <= 0 )
            return;
        
        if( index > 0 && newMeasure <= getSectionMeasure(index-1) )
           return;
         
        int endTemp = (index + 1 < size()) ? getSectionMeasure(index+1)-1 : measures();
        
        if( newMeasure > endTemp )
           return;
         
        SectionRecord record = records.get(index);
           //gets the start measure # of the section
        //if user wants to change start index to what it is already -_-
        if(getSectionMeasure(index) == newMeasure) 
        {
           record.setIsPhrase(isPhrase);
           if( usePreviousStyleChecked )
               record.setUsePreviousStyle();
           return;
        }
        
        String styleName = usePreviousStyleChecked ? Style.USE_PREVIOUS_STYLE : record.getStyleName();
        deleteSection(index);
        addSection(styleName, measureToSlotIndex(newMeasure), isPhrase);
    }
    
    //markermarkermarker
    public void adjustEndOfSection(int index, int endIndex, boolean isPhrase, boolean usePreviousStyleChecked) {
        //*
        if( endIndex <= 0 )
            return;
        
        SectionRecord record = records.get(index);
        
        int endTemp = (index + 1 < size()) ? getSectionMeasure(index+1)-1 : measures();
        
        int nextSectionEnd = (index + 2 < size()) ? getSectionMeasure(index + 2) - 1 : measures();
        
        if(index < size() - 1 && endIndex >= nextSectionEnd)
            return;
         
        if( endIndex < getSectionMeasure(index) )
            return;
        
        //if user wants to change end index to what it is already -_-
        if( endIndex == endTemp )
        {
            record.setIsPhrase(isPhrase);
            if( usePreviousStyleChecked )
                record.setUsePreviousStyle();
            return;
        }
        
        String styleName = usePreviousStyleChecked ? Style.USE_PREVIOUS_STYLE : record.getStyleName();
        
        if( size() <= 1)
            return;
        if( index < size() - 1 )
        {
            SectionRecord nextRecord = records.get( index+1 );
            nextRecord.setIndex( measureToSlotIndex(endIndex + 1) );
        }
        //*/
        
    }

    public void deleteSection(int index) {
        if(size() <= 1)
            return;
        SectionRecord fillSpot = null;
        if(index == 0)
            fillSpot = records.get(1);
        
        if(index < size()-1)
        {
            SectionRecord nextRecord = records.get(index+1);
            if(nextRecord.usePreviousStyle())
                nextRecord.setStyleName(records.get(index).getStyleName());
        }
        
        ListIterator<SectionRecord> k = records.listIterator(index);
        k.next();
        
        int newStartIndex = getSectionMeasure(index);
        
        k.remove();
        
        if(fillSpot != null)
        {
            fillSpot.setIndex(measureToSlotIndex(newStartIndex));
        }
        
    }
    
    public void setSize(int size) {
        
        Iterator<SectionRecord> k = records.iterator();
        
        while(k.hasNext()) {
            SectionRecord record = k.next();
            int n = record.getIndex();
            if( n >= size ) {
                k.remove();
            }
        }
    }

    public boolean setStyle(String name) {
        Style s = Style.getStyle(name);
        if(s == null)
          {
            return false;
          }
        else {
            setStyle(s);
            return true;
        }
    }
    
    public void setStyle(Style s) {
        if(s == null)
          {
            System.out.println("null Style; should not happen");
            return;
          }
        records = new ArrayList<SectionRecord>();
        addSection(s.getName(),0, false);
    }

    public Style getStyle() {
        return getStyle(0);
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
    //System.out.println("Sequencing SectionInfo time = "
    // + time + " endLimitIndex = " + endLimitIndex + " useDrums = " + useDrums);

    // Iterate over list of sections, each a Style

    int chordsSize = chords.size();
    
    int endIndex;
    
    // m is a second iterator intended to stay one step ahead of k
    // so as to get the start of the next section
    
    Style mostRecentStyle = new Style();
    
    ListIterator<SectionRecord> k = records.listIterator();
    ListIterator<SectionRecord> m = records.listIterator();
    if( m.hasNext() )
      {
        m.next();
      }
    
    while( k.hasNext() ) //&& (endLimitIndex == ENDSCORE || endIndex <= endLimitIndex) )
      { 
        SectionRecord record = k.next();        
        Style style;
        if( record.getUsePreviousStyle() )
          {
            style = mostRecentStyle;
            //System.out.println("using previous style " + style);
          }
        else
          {
            style = record.getStyle();
            mostRecentStyle = style;
          }
        
        int startIndex = record.getIndex();
        
        endIndex = m.hasNext() ? m.next().getIndex() : chordsSize;
        
        if( style != null )
          {
          time = style.render(seq, 
                              time, 
                              chords, 
                              startIndex, 
                              endIndex, 
                              transposition, 
                              useDrums, 
                              endLimitIndex,
                              constantBass);
          }
       }
    return time;
  }

public ArrayList<Block> toBlockList()
{
    ArrayList<Block> blocks = new ArrayList<Block>();
    int chordsSize = chords.size();
    
    int endIndex;
    
    // m is a second iterator intended to stay one step ahead of k
    // so as to get the start of the next section
    
    ListIterator<SectionRecord> k = records.listIterator();
    ListIterator<SectionRecord> m = records.listIterator();
    if( m.hasNext() )
      {
        m.next();
      }

    while( k.hasNext() ) //&& (endLimitIndex == ENDSCORE || endIndex <= endLimitIndex) )
      {
        SectionRecord record = k.next();
        String styleName = record.getStyleName();
        int startIndex = record.getIndex();
        
        endIndex = m.hasNext() ? m.next().getIndex() : chordsSize;
        
        ChordBlock block = null;
        
        for( int slot = startIndex; slot < endIndex; slot++ )
          {
          Chord chord = chords.getChord(slot);
          
          if( chord != null )
            {
              block = new ChordBlock(chord.getName(), chord.getRhythmValue());
              block.setStyleName(styleName);
              blocks.add(block);
            }
          }
        
        // For last block in section
        if( block != null )
            {
            block.setSectionEnd(record.getIsPhrase()? Block.PHRASE_END : Block.SECTION_END);
            }
       }
    
    return blocks;
}

/**
 * Determine whether a given index corresponds to the start of a Section.
 * This is done by iterating through styleIndices, accumulating slot counts,
 * until either the given index coincides with the start of a slot or
 * the accumulated count exceeds the index.
 * @param index
 * @return 
 */
public boolean isSectionStart(int index)
{
    int accumulatedSlots = 0;

    ListIterator<SectionRecord> k = records.listIterator();
    while( k.hasNext() && index >= accumulatedSlots )
    {
        if( index == accumulatedSlots )
        {
            return true;
        }
        accumulatedSlots += k.next().getIndex();
    }
    return false;
  
}

/**
 * Returns ArrayList of indices of section starts that are
 * not phrases.
 * @return 
 */
public ArrayList<Integer> getSectionStartIndices()
{
  ArrayList<Integer> result = new ArrayList<Integer>();
  boolean isStart = true; //The first Section Record must be a Section Start
  
  for( SectionRecord record: records )
    {
        if(isStart==true){
            result.add(record.getIndex());
        }
        if(!record.getIsPhrase()){
            //Any Section Record that follows a Section Record that is
            //not a phrase must be a Section Start
            isStart = true;
        }
        else{
            //Otherwise, if a Section Record is a Phrase, i.e.,
            //ends with a breath mark as opposed to a double bar line,
            //the next Section Record cannot be a Section Start
            isStart = false;
        }
    }
    return result;
}

@Override
public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    for( SectionRecord record: records )
    {
        buffer.append("(");
        buffer.append(record.getStyleName());
        buffer.append(" ");
        buffer.append(record.getIndex());
        buffer.append(" ");
        buffer.append(record.getIsPhrase());
        buffer.append(") ");
        buffer.append("\n");
    }
    return buffer.toString();
  }

public Iterator<SectionRecord> iterator()
  {
    return records.iterator();
  }
/**
 * Set the blocks' styles and add the sections.
 * @param blocks
 * @return blocks
 */
public ArrayList<Block> setAllStyles(ArrayList<Block> blocks)
  {         
      int lastIndex = 0;
      for (Block b : blocks)
      {  
          if (lastIndex < records.size())
          {
              b.setStyleName(records.get(lastIndex).getStyleName());     
              //System.out.println("block:" + b.getStyleName());
          }
          else if (lastIndex >= 1)
          {
              b.setStyleName(records.get(records.size()-1).getStyleName()); 
              //System.out.println("block:" + b.getStyleName());
          }
          if (b.isSectionEnd())
          {
              lastIndex++;
          }
      }
    return blocks;
  }

public ArrayList<Block> setAllStyles(ArrayList<Block> blocks, ArrayList<SectionRecord> secRecs)
  {         
      int lastIndex = 0;
      for (Block b : blocks)
      {  
          if (lastIndex < secRecs.size())
          {
              b.setStyleName(secRecs.get(lastIndex).getStyleName());     
              //System.out.println("block:" + b.getStyleName());
          }
          else if (lastIndex >= 1)
          {
              b.setStyleName(secRecs.get(secRecs.size()-1).getStyleName()); 
              //System.out.println("block:" + b.getStyleName());
          }
          if (b.isSectionEnd())
          {
              lastIndex++;
          }
      }
    return blocks;
  }

public ArrayList<SectionRecord> getSectionRecords()
{
    return records;
}

public ArrayList<String> getSectionRecordStyleNames()
{
    ArrayList<String> secRecStyles = new ArrayList<String>();
    for (SectionRecord sr : records)
    {
        secRecStyles.add(sr.getStyleName());
    }
    return secRecStyles;
}

//public void resetStylesInRecords(ArrayList<SectionRecord> secRecs)
//{
//    int i = 0;
//    for (SectionRecord sr :records)
//    {
//        sr.setStyleName(secRecs.get(i).getStyleName());        
//        i++;
//    }
//}

//public void setStyleNamesFromBlocks(ArrayList<Block> blocks)
//{
//   ArrayList<String> styleNames = new ArrayList<String>();
//   for (Block b : blocks)
//   {
//       styleNames.add(b.getStyleName());
//   }
//   int i = 0;
//   for (String s : styleNames)
//   {
//     if (i < records.size())
//     {
//        records.get(i).setStyleName(s);
//     }
//     i++;
//   }
//}
}
