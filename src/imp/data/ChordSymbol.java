/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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

import imp.data.advice.Advisor;
import imp.Constants;
import imp.util.ErrorLog;
import java.io.Serializable;
import polya.Polylist;

/**
 * A ChordSymbol is an object representing a chord in lightweight form,
 * from which a Chord can be created given a rhythmValue or duration and
 * possibly other parameters. 
 * It is like a pre-parsed string version of a chord from a leadsheet.
 * The parsed components are:
 * <pre>
 * The type of chord, such as M7, 7, 7#5, etc.
 * The root of the chord
 * The bass note (defined to be the root if not otherwise specified).
 * The polychord component, which is another ChordSymbol over which this chord is placed.
 * @see         Chord
 * @see         Form
 * @author      Robert Keller
 */
public class ChordSymbol implements Constants, Serializable {

    /**
     * a String containing the chord name
     */
    private String name;

    /**
     * a String containing the type of the chord
     */

    private String type;

    /**
     * the ChordForm (which relates to scales, etc.) for this type of chord
     */

    private ChordForm chordForm;

    private Polylist voicing = Polylist.nil;
    private Polylist extension = Polylist.nil;

    /**
     * The root of the chord as a string (starting with upper case)
     */

    private String rootString = null;

    /**
     * The root of the chord, a pitch-class
     */

    private PitchClass root = null;

    /**
     * The bass note of the chord, a pitch-class
     */

    private PitchClass bass = null;

    /**
     * The polychord component, if any (a chord over which this chord is placed).
     */

    private ChordSymbol polybase = null;


    public ChordSymbol() {}

    /**
     * Use for fabricating ChordSymbol rather than parsing from String.
     */

    public ChordSymbol(PitchClass root, String type, PitchClass bass, ChordSymbol polybase)
      {
      this.rootString = capitalize(root.toString());
      this.type = type;
      this.root = root;
      this.bass = bass;
      this.polybase = polybase;
      StringBuilder nameBuffer = new StringBuilder();
      nameBuffer.append(rootString);
      nameBuffer.append(type);
      if( polybase != null ) 
        {
        nameBuffer.append(BACKSLASH);
        nameBuffer.append(polybase.toString());
        }
      else if( !bass.equals(root) )
        {
        nameBuffer.append(SLASHSTRING);
        nameBuffer.append(capitalize(bass.toString()));
        }
      name = nameBuffer.toString();

      // The following call is needed to set the ChordForm component.
      // Presumably the constructor constructs a syntactically-valid chord,
      // so the result is not actually used.

      checkValidity(Advisor.getAllChords());
      }

    public Polylist getVoicing() {
        return voicing;
    }

    public void setVoicing(Polylist voicing) {
        this.voicing = voicing;
    }
 
    public Polylist getExtension() {
        return extension;
    }

    public void setExtension(Polylist extension) {
        this.extension = extension;
    }
 
    
    /**
     * Creates a ChordSymbol with the specified name.
     * @param name          a String containing the chord name
     */
    public static ChordSymbol makeChordSymbol(String name)
      {
      if( name.equals("") )
	{
	return null;	// Error indicator
	}

      ChordSymbol chordSymbol = new ChordSymbol();
      chordSymbol.name = name;

      if( name.equals(NOCHORD) )
	{
        // for uniformity
        chordSymbol.type = NOCHORD;
        chordSymbol.root = PitchClass.cClass;
        chordSymbol.bass = PitchClass.cClass;
        chordSymbol.rootString = CROOT;
	return chordSymbol;
	}

      // Parse the name into components

      // (1) Start by getting the root.

      StringBuilder buffer1 = new StringBuilder();

      buffer1.append(name.charAt(0));

      if( !Key.isValidStem(buffer1.toString()) )  
	{
	return null;
	}

      int len = name.length();

      int index = 1;

      if( index < len )
	{
	char c = name.charAt(1);
	if( c == SHARP || c == FLAT )
	  {
	  buffer1.append(c);
	  index++;
	  }
	}

      chordSymbol.rootString = buffer1.toString();

      chordSymbol.root = PitchClass.getPitchClass(chordSymbol.rootString.toLowerCase());

      if( chordSymbol.root == null )
	{
	return null;	// root is not known a known pitch class
	}

      // (2)Get the type of the chord.

      StringBuilder buffer2 = new StringBuilder();

      while( index < len && name.charAt(index) != SLASH && name.charAt(index) != BACKSLASH )
	{
	buffer2.append(name.charAt(index));
	index++;
	}

      chordSymbol.type = buffer2.toString();

      // (3) handle slash and polychords
      // as mutually exclusive.

      if( index < len )
	{
	// We have a slash chord or poly chord.
  
         if( name.charAt(index) == SLASH )
           {
           StringBuilder buffer3 = new StringBuilder();

    	   index++;	// skip the slash

	   while( index < len )
	     {
	     buffer3.append(name.charAt(index));
	     index++;
	     }

      	   String bassString = buffer3.toString().toLowerCase();

           chordSymbol.bass = PitchClass.getPitchClass(bassString);

  	   if( chordSymbol.bass == null )
    	     {
	     return null;
	     }
	   }
         else
           {
           // we have a polychord
           index++;
           chordSymbol.polybase = makeChordSymbol(name.substring(index));
           chordSymbol.bass = chordSymbol.polybase.getBass();
           }
        }
      else
        {
        chordSymbol.bass = chordSymbol.root;
        }

      if( chordSymbol.checkValidity(Advisor.getAllChords()) )
        {
        return chordSymbol;
        }

      ErrorLog.log(ErrorLog.WARNING, chordSymbol + " is not a recognized chord");
      return null;
      }

   /**
    * Check whether chord is known in the list of all chords.  If it is, set the chordForm
    * for future access.
    */

   public boolean checkValidity(Polylist chords)
     {
     String Cversion = CROOT + type;
     chordForm = Advisor.getChordForm(Cversion);
     if( chordForm == null )
       {
       return false;
       }
     if( polybase != null )
       {
       return polybase.checkValidity(chords);
       }
     return true;
     }

    /**
     * Returns the ChordSymbol's name.
     * @return the ChordSymbol's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ChordSymbol's family (as derived from its chordForm).
     * @return the ChordSymbol's family.
     */
    public String getFamily() {
        if( chordForm == null )
          {
            return "other";
          }
        return chordForm.getFamily();
    }

    /**
     * Returns the ChordSymbol's root
     * @return the ChordSymbol's root
     */
    public PitchClass getRoot() {
        return root;
    }
    
    
    /**
     * Returns the ChordSymbol's generic quality, accounting for polychords
     * and slash chord
     * @return a String of the ChordSymbol's quality.
     */
    public String getQuality() {
        if (this.polybase != null)
            return this.polybase.getQuality();
        
        String quality = this.name;
        if (quality.length() > 1 && 
                (quality.charAt(1) == 'b' || quality.charAt(1) == '#'))
            quality = quality.substring(2);
        else
            quality = quality.substring(1);
        
        if (isSlashChord())
        {
            String[] qualitySplit = quality.split(SLASHSTRING);
            quality = qualitySplit[0];
        }

        return quality;
    }
    

    /**
     * Returns the ChordSymbol's root as String
     * @return the ChordSymbol's root as String
     */
    public String getRootString() {
        return rootString;
    }

    /**
     * Returns the ChordSymbol's type
     * @return the ChordSymbol's type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the ChordSymbol's ChordForm
     * @return the ChordSymbol's ChordForm
     */
    public ChordForm getChordForm() {
        return chordForm;
    }

    /**
     * Returns the ChordSymbol's bass note
     * @return the ChordSymbol's bass note
     */
    public PitchClass getBass() {
        return bass;
    }

    /**
     * Returns the ChordSymbol's polychord base
     * @return the ChordSymbol's polychord base
     */
    public ChordSymbol getPolybase() {
        return polybase;
    }

    /**
     * Returns a String representation of the ChordSymbol.
     * @return String   a String representation of the ChordSymbol
     */
    public String toString() {
        return name;
    }

    /**
     * Returns an indication of whether this chord is NOCHORD
     * @return an indication of whether this chord is NOCHORD
     */
    public boolean isNOCHORD() {
        return type.equals(NOCHORD);
    }

    /**
     * Returns an indication of whether this chord is slash-chord
     * @return an indication of whether this chord is slash-chord
     */
    public boolean isSlashChord() {
        return !root.enharmonic(bass);
    }

    /**
     * Returns an indication of whether this chord is enharmonically equivalent to the argument
     * @return an indication of whether this chord is NOCHORD
     */
    public boolean enhChord(ChordSymbol other) {
        return type.equals(other.type) && root.enharmonic(other.root);
    }

    /**
     * Returns an indication of whether this chord is enharmonically a member of a list of ChordSymbols
     * @return an indication of whether this chord is enharmonically a member of a list of ChordSymbols
     */
    public boolean enhMember(Polylist chordSymbols)
      {
      while( chordSymbols.nonEmpty() )
         {
          if( enhChord((ChordSymbol)chordSymbols.first()) )
            {
            return true;
            }
         chordSymbols = chordSymbols.rest();
         }
      return false;
      }

    /**
     * Returns an indication of whether two lists of chords are enharmonically the same
     * @return an indication of whether two lists of chords are enharmonically the same
     */
    public static boolean enhChordSequences(Polylist x, Polylist y)
      {
      while( x.nonEmpty() && y.nonEmpty() )
         {
         if( ! ((ChordSymbol)x.first()).enhChord((ChordSymbol)y.first()) )
           {
           return false;
           }
         x = x.rest();
         y = y.rest();
         }
      return x.isEmpty() && y.isEmpty();
      }

    /**
     * Returns a list of ChordSymbols from a list of Strings known to represent chords.
     * @return a list of ChordSymbols from a list of Strings known to represent chords.
     */
    public static Polylist chordSymbolsFromStrings(Polylist strings)
      {
      Polylist result = Polylist.nil;
      while( strings.nonEmpty() )
         {
         result = result.cons(makeChordSymbol((String)strings.first()));
         strings = strings.rest();
         }
      return result.reverse();
      }

  /** 
   * Return a new ChordSymbol that is the transpose of this one.
   */

  public ChordSymbol transpose(int semitones)
    {
    if( getName().equals(NOCHORD) )
        {
        return this;
        }
    return new ChordSymbol(root.transpose(semitones), 
                           type, 
                           bass.transpose(semitones), 
                           polybase == null ? null : polybase.transpose(semitones)
                           );
    }

  /** 
   * Return a list of new ChordSymbols that are the transposes of the argument list.
   */

  public static Polylist transpose(Polylist L, int semitones)
    {
    Polylist result = Polylist.nil;
    while( L.nonEmpty() )
      {
      result = result.cons(((ChordSymbol)L.first()).transpose(semitones));
      L = L.rest();
      }
    return result.reverse();
    }

  public static String capitalize(String arg)
    {
    if( arg.equals("") )
      {
      return arg;
      }
    return Character.toUpperCase(arg.charAt(0)) + arg.substring(1);
    }


     /**
      * Return the note type associated with a given note.
      */
     
     public int getTypeIndex(Note note)
     {
     if( chordForm == null )
       {
       return CHORD_TONE;
       }
     
     // Indicate transposition of the note so as to be relative
     // to the root of the chord.
 
     return chordForm.getTypeIndex(note, -root.getSemitones());
     }
 
}
