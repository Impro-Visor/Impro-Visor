/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011-2013 Robert Keller and Harvey Mudd College
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

package imp.brickdictionary;

import imp.data.Chord;
import java.util.ArrayList;
import polya.Arith;
import polya.Polylist;

/**
 * purpose: Definition definition
 * @author Zachary Merritt
 */


public class ChordBlock extends Block {

    /**
     * Suffix on chord symbol used to designate tonic.
     */

    public static final String TONIC_SUFFIX = "_";
    
    public static enum FlagType {NORMAL, SECTION_END, PHRASE_END} ;
    
    private Chord chord;               // an imp.data Chord object
    private long NC = -1;              // the key describing nonchords
    private String BACKSLASH = "\\";   // the String of a single backslash
    
    /** ChordBlock / 2
     * Creates a ChordBlock from a name and a duration
     * @param chordName, a String of a chord name
     * @param dur, an int duration
     */
    public ChordBlock (String chordName, int dur) {
        super(chordName);
        this.duration = dur;
        //setOverlap(this.duration == 0);
        chord = new Chord(chordName, this.duration);
        // In case the named chord does not exist, don't generate a backtrace.
        try
          {
        if (chordName.contains(BACKSLASH))
                {
                  key = fixRoot(chord.getChordSymbol().getPolybase().getRootString());
                }
        else
                {
                  key = fixRoot(chord.getRoot());
                }
          }
        catch( Exception e )
          {
            chord = new Chord("NC", this.duration);
          }
        endValue = Block.NO_END;
        mode = this.findModeFromQuality();
            
    }
    
    /** ChordBlock / 3
     * Creates a ChordBlock from a name, a duration and the type of section end
     * it is, if any
     * @param chordName, a String of a chord name
     * @param dur, an int duration
     * @param endValue, an int describing section end
     */
    public ChordBlock(String chordName, int dur, int endValue) {
        super(chordName);
        this.duration = dur;
        chord = new Chord(chordName, this.duration);
        if (chordName.contains(BACKSLASH))
          {
            key = fixRoot(chord.getChordSymbol().getPolybase().getRootString());
          }
        else
          {
            key = fixRoot(chord.getRoot());
          }
        this.endValue = endValue;
        mode = findModeFromQuality();
    }
    
    /** ChordBlock / 1
     * Copy constructor for a ChordBlock
     * @param ch, a ChordBlock
     */
    public ChordBlock(ChordBlock ch) {
        super(ch.name);
        this.duration = ch.getDuration();
        chord = new Chord(ch.name, this.duration);
        if (ch.name.contains(BACKSLASH))
          {
            key = fixRoot(chord.getChordSymbol().getPolybase().getRootString());
          }
        else
          {
            key = fixRoot(chord.getRoot());
          }
        endValue = ch.getSectionEnd();
        mode = findModeFromQuality();
    }

    /** ChordBlock / 1
     * Creates a ChordBlock corresponding to a Chord
     * @param ch, a Chord
     */
    public ChordBlock(Chord ch) {
        super(ch.getName());
        duration = ch.getRhythmValue();
        chord = ch.copy();
        if (ch.getName().contains(BACKSLASH))
          {
            key = fixRoot(chord.getChordSymbol().getPolybase().getRootString());
          }
        else
          {
            key = fixRoot(chord.getRoot());
          }
        mode = findModeFromQuality();
    }
    
    public String getRoot()
      {
        return chord.getRoot();
      }
    
    public int getRootSemitones()
      {
        return chord.getRootSemitones();
      }
    
    // Probably this is not adequate
    
    public boolean same(ChordBlock cb)
      {
        return chord.getRootSemitones() == cb.chord.getRootSemitones()
            && chord.getQuality().equals(cb.getQuality());
      }

    /** getDuration
     * Get the ChordBlock's duration
     * @return an int
     */
    @Override
    public int getDuration() {
        return this.duration;
    }
    
    /** getChord
     * Gets the Chord basis of the ChordBlock. Modifications affect the original
     * ChordBlock
     * @return the Chord
     */
    public Chord getChord() {
        return chord;
    }
    
    /** scaleDuration
     * Scales the duration by a given scaling number (positive for stretching, 
     * negative for shrinking)
     * @param scale, the scale factor as an int
     */
    @Override
    public void scaleDuration(int scale) {
        if(scale > 0)
          {
            duration = duration * scale;
          }
        else
          {
            duration = duration / -scale;
          }
        chord.setRhythmValue(duration);
    }
    
    /** changeChordDuration
     * Scales the chord by the given ratio
     * @param ratio, a float for the scaling factor
     */
    public void changeChordDuration(float ratio) {
        duration = Math.round(ratio * duration);
        chord.setRhythmValue(duration);
    }
    
    /** getQuality
     * Returns the quality of the ChordBlock
     * @return a String
     */
    public String getQuality() {
        if (name.equals(Chord.NOCHORD))
          {
            return name;
          }
        return chord.getQuality(); // doesn't work as well: parseChordName();
    }
    
    /** getSymbol
     * Returns the symbol of the ChordBlock (the quality without slash chords
     * or polychords)
     * @return a String
     */
    @Override
    public String getSymbol() {
        if (name.equals(Chord.NOCHORD))
          {
            return name;
          }
        return chord.getQuality();
    }
    
    /** isDiminished
     * Describes whether or not the ChordBlock represents a diminished chord
     * @return a boolean
     */
    public boolean isDiminished() 
      {
        String quality = getQuality();
        //System.out.println("quality of " + this + " is " + getSymbol() );
        
        return quality.startsWith("dim") || quality.startsWith("o");
      }

    /** 
     * FIX: Use the vocabulary instead of this kind of test.
     * Describes whether or not the ChordBlock represents a diminished chord
     * @return a boolean
     */
    public boolean isDominant() {
         String symbol = getSymbol();
        
        return symbol.startsWith("7") 
            || symbol.startsWith("9")
            || symbol.startsWith("11")
            || symbol.startsWith("13")
            || symbol.startsWith("sus");
    }
    
    
   /** 
     * FIX: Use the vocabulary instead of this kind of test.
     * Describes whether or not the ChordBlock represents a diminished chord
     * @return a boolean
     */
    public boolean isMinor7() {
         String symbol = getSymbol();
        
        return symbol.startsWith("m7") 
            || symbol.startsWith("m9")
            || symbol.startsWith("m11")
            || symbol.startsWith("m13");
    }
    
    
    /** 
     * FIX: Use the vocabulary instead of this kind of test.
     * Describes whether or not the ChordBlock represents a diminished chord
     * @return a boolean
     */
    public boolean isMinor() {
        return getSymbol().startsWith("m");
    }
    
    
   /** 
     * Describes whether or not the ChordBlock represents a stable tonic chord.
     * @return a boolean
     */
    public boolean isTonic() {
        String symbol = getSymbol();
        return symbol.equals("")
            || symbol.equals("6") 
            || symbol.equals("m")
            || symbol.startsWith("M")
            || symbol.startsWith("M6") 
            || symbol.startsWith("mM") 
            || symbol.startsWith("m6")
            || symbol.endsWith(TONIC_SUFFIX);    }
    
    public boolean isGeneralizedTonic() {
        String symbol = getSymbol();
        return isTonic() 
            || symbol.startsWith("m6")
            || symbol.startsWith("m7")
            || symbol.startsWith("m9")
            || symbol.startsWith("m11")
            || symbol.startsWith("m13")
            || symbol.endsWith(TONIC_SUFFIX);    }
    
    /** isSlashChord
     * Describes whether not the ChordBlock is a slash chord
     * @return a boolean
     */
    public boolean isSlashChord() {
        return chord.getChordSymbol().isSlashChord();
    }
    
    /** isOverlap
     * Describes whether the ChordBlock is part of an overlapping render
     * @return a boolean
     */
    @Override
    public boolean isOverlap() {
        return overlap;
    }
    
    /** getSubBlocks
     * Returns the ChordBlock as part of a list of subblocks
     * @return an ArrayList of Blocks, either empty or containing the ChordBlock
     *         depending on if the ChordBlock is an overlap or not
     */
    @Override
    public ArrayList<Block> getSubBlocks() {
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        if (!this.isOverlap())
          {
            subBlocks.add(this);
          }
        return subBlocks;
    }
    
    /** flattenBlock
     * Returns the ChordBlock as part of a list of chords
     * @return an ArrayList of ChordBlocks, either empty or containing the 
     *         ChordBlock depending on if the ChordBlock is of zero duration 
     */
    @Override
    public ArrayList<ChordBlock> flattenBlock() {
        ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();
        if (!this.isOverlap())
          {
            chordList.add(this);
          }
        
        return chordList;
    }
    
    /** transpose
     * Moves the key up by diff semitones
     * @param diff, a difference in key as a long
     */
    @Override
    public void transpose(long diff) {
        if(!this.chord.getName().equals(Chord.NOCHORD)) {
            this.key = moduloSteps(this.key + diff);
            this.chord.transpose(Arith.long2int(diff));
            this.name = chord.getName();
        }
    }
    /** transposeName
     * Gets the name of the ChordBlock if it was moved up by diff semitones
     * @param diff, a difference in key as an int
     * @return the String of the ChordBlock's name
     */
    public String transposeName(int diff) {
        this.chord.transpose(diff);
        String tranName = this.chord.getName().intern();
        this.chord.transpose(-1*diff);
        return tranName;
    }
    
    /** toString
     * Returns a String representation of the ChordBlock
     * @return a String
     */
    @Override
    public String toString() {
	String strDur = (this.duration == 0) ? "*" : Integer.toString(this.duration);
        return name + " " + strDur;
    }
    
    /** matches
     * Checks to see if the chords are effectively the same (have the same 
     * quality, excepting variations from polychords)
     * @param c, a ChordBlock to check for match
     * @return a boolean (true if 
     */
    public long matches(ChordBlock c) {
        if (c.getSymbol().equals(this.getSymbol()))
          {
            return moduloSteps(c.getKey() - key );
          }
        return NC;
    }
    
    /** parseChordName
     * Takes the chord name and processes it for key and quality
     * @return the quality as a String
     */
    private String parseChordName() {
        String chordName = this.getName();
        String quality;
        
        if(chordName.length() > 1 && (chordName.charAt(1) == 'b'|| 
                chordName.charAt(1) == '#'))
        {
            String chordKeyString = chordName.substring(0, 2);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (!(chordName.equals(chordKeyString))) {
                quality = chordName.substring(2);
            }
            else
              {
                quality = "";
              }
        }
        else
        {
            String chordKeyString = chordName.substring(0, 1);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (chordName.length() > 1) {
                quality = chordName.substring(1);
            }
            else
              {
                quality = "";
              }
        }
        
        return quality;
    }

    /** moduloSteps
     * Assures a positive modulus of a given key by the number of semitones
     * @param j, a long of a key
     * @return the positive remainder of j / 12
     */
    public long moduloSteps(long k) {
        return (k + 12)%12;
    }

    /** isChord
     * Describes whether the object is a ChordBlock.
     * @return a boolean
     */
    @Override
    public final boolean isChord()
    {
        return true;
    }
    
    /** isBrick
     * Describes whether the object is a Brick.
     * @return a boolean
     */
    @Override
    public final boolean isBrick()
    {
        return false;
    }
    
    /** toPolylist
     * returns a Polylist representation of a ChordBlock
     * @return a Polylist of the Chord's basic information
     */
    @Override
    public Polylist toPolylist()
    {
        return Polylist.list(Brick.CHORD_KEYWORD, name, duration);
    }
    
    public Polylist toRoadmapSave()
      {
        return toPolylist();
      }

    /**
     * Make a ChordBlock from a Polylist
     * @param blockPolylist
     * @return 
     */
    public static Block fromPolylist(Polylist blockPolylist)
      {
        int duration = ((Number)blockPolylist.third()).intValue();
        ChordBlock block = new ChordBlock((String)blockPolylist.second(), duration);
        return  block;
      }

    /** findModeFromQuality
     * Find mode of a block using quality of a chord
     * @param quality : String used to find mode
     * @return mode : String that determines overall tonicity of block
     */
    public final String findModeFromQuality() {
        String m;
        String q = this.getSymbol();
        
        if(q.startsWith("M") || q.equals("") || q.startsWith("6"))
          {
            m = "Major";
          }
        else if(q.startsWith("7") || q.startsWith("9") || q.startsWith("11") || 
                q.startsWith("13"))
          {
            m = "Dominant";
          }
        else
          {
            m = "Minor";
          }
        
        return m;
    }
    
    /** fixRoot
     * Takes a lower-case key and returns the numeric version of the key.
     * @param s, a String of a chord name
     * @return the key as a long
     */
    public static long fixRoot(String s) {
        s = s.replaceFirst(s.substring(0,1), s.substring(0, 1).toUpperCase());
        return BrickLibrary.keyNameToNum(s);
    }
    
    public int getLength()
      {
        return 1;
      }
        /**
     * @return whether this block has just one chord
     */
     public boolean singleChord()
      {
        return true;
      }
    
    // end of class ChordBlock
}
