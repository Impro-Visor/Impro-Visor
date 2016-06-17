/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.data.advice;

import imp.data.Key;
import imp.data.Leadsheet;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Part;
import imp.data.PitchClass;
import imp.data.Unit;
import polya.*;

/**
 * Describes and contains a piece of Advice that is a group of notes,
 * like a cell or a lick.
 * @see         Advice
 * @see         Advisor
 * @see         Polylist
 * @author      Stephen Jones
 */
public class AdviceForMelody extends Advice {

    /**
     * the notes in the cell
     */
    private Polylist notes;

    /**
     * the root of the chord to transpose to
     */
    private String chordRoot;
    
    /**
     * the key of the chord to transpose to
     */
    private Key key;
    
    private int[] metre = new int[2];

    MelodyPart newPart;

    Note firstNote;

    private int serial;

    private int profileNumber;

    boolean mark = false;

    

    public AdviceForMelody(String name, Polylist notes, String chordRoot, Key key, int[] metre, int profileNumber)
      {
      this(name, 0, notes, chordRoot, key, metre, null, profileNumber);
      }

    public AdviceForMelody(String name, int serial, Polylist notes, String chordRoot, Key key, int[] metre, int profileNumber)
      {
      this(name, serial, notes, chordRoot, key, metre, null, profileNumber);
      }

    /**
     * Creates new AdviceForMelody
     * @param name      a String that describes the advice
     * @param notes     a Polylist containing the cell
     * @param chordRoot the root of the chord to transpose to
     * @param key       the key of the chord to transpose to
     * @param firstNote optional first note for pitch registration (could be null)
     */
    public AdviceForMelody(String name, int serial, Polylist notes, String chordRoot, Key key, int[] metre, 
            Note firstNote, int profileNumber ) {
        super(name);
        this.notes = notes;
        this.chordRoot = chordRoot;
        this.key = key;
        this.metre[0] = metre[0];
        this.metre[1] = metre[1];
        this.profileNumber = profileNumber;
        this.serial = serial;
        newPart = new MelodyPart();
        newPart.setMetre(metre[0], metre[1]);

        int rise = 0;
        
        int beatValue = ((BEAT*4)/metre[1]);
        int measureLength = metre[0] * beatValue;

        //System.out.println("AdviceForMelody addToMelodyFromPolylist " + notes + ", rise = " + rise);
        
        Leadsheet.addToMelodyFromPolylist(notes, newPart, rise, measureLength, key);

        // See if we need to make first notes coincide.
        if( firstNote != null && startsWith(firstNote) )
          {
          int firstNotePitch = firstNote.getPitch();
          int melodyPitch = ((Note)getPart().getUnit(0)).getPitch();
          int diff =  firstNotePitch - melodyPitch;

          if( (diff != 0) && (diff % OCTAVE == 0) )
            {
            // redo with new rise
            rise += diff;
            newPart = new MelodyPart();  
            newPart.setMetre(metre[0], metre[1]);
            Leadsheet.addToMelodyFromPolylist(notes, newPart, rise, measureLength, key);
            
            }
          }
        
    }
    
    /**
     * Converts the Advice into a Part and returns that
     * @param targetMIDI  the center for which to transpose the part 
     * @return Part       the Advice in Part form, ready to be inserted
     */
    public MelodyPart getPart(int targetMIDI) {
        return transpose(targetMIDI);
    }
    
    /**
     * Converts the Advice into a Part and returns that
     * @return Part     the Advice in Part form, ready to be inserted
     */
    public MelodyPart getPart() {
        return newPart;
    }

    /**
     * Tells whether this Advice starts with the note named.
     */

    boolean startsWith(Note note)
      {
      if( notes.isEmpty() )  // no note in Score
	{
	return false;
	}

      Unit firstUnit = getPart().getUnit(0);

      if( firstUnit instanceof Note && ((Note)firstUnit).isRest() )
        {
        return false;
        }

      String firstNotePC = ((Note)firstUnit).getPitchClassName();

      String thisNotePC = note.getPitchClassName();

      return PitchClass.enharmonic(firstNotePC, thisNotePC);
      }
    
    /**
     * transpose is a method that centers the Part around a targetMIDI
     * value 
     * 
     * @see average()    -- finds the middle of the part 
     * @param targetMIDI -- the center to compare the phrase to 
     */
    public MelodyPart transpose(int targetMIDI)
    {
        int averageMIDI = average();
        
        int difference = targetMIDI - averageMIDI;
        //if neg, average is too high -- needs to be moved down
        //if pos, average is too low  -- needs to be moved up 
        
        int absoluteValue = Math.abs(difference); 
        int end = 0; 
        if (absoluteValue >  6 && absoluteValue < 12)
        {
            end = absoluteValue/6; 
        }
        else
        {
            end = Math.abs(difference) / 12 ;
        } 
        MelodyPart d = newPart.copy(); 
        Part.PartIterator j = d.iterator(); 
        // newPart is a MelodyPart
        
        while (j.hasNext())
        {
            Note next = (Note)(j.next()); 
            if (!next.isRest() && (absoluteValue > 6))
            {
                for(int i = 0; i<end; i++)
                {
                    if (difference<0)
                    {
                        next.shiftPitch(-12, 0);
                    }
                    else 
                    {
                        next.shiftPitch(12, 0);
                    }
                }
                
            }
        }
        return d; 
    }

    /**
     * the method average does exactly whats in the name. It calculates the average MIDI value by
     * using the midpoint of the lowest and highest MIDI value in the phrase 
     * 
     * @return averageMIDI          -- gives you average using all the notes (not just top and bottom) 
     * @see transpose(targetValue)  -- average was made for the transpose method 
     */
    public int average()
    {
        Part.PartIterator f = newPart.iterator();
        int highestPitch = 0;
        int lowestPitch = 128; 
        int averageMIDI = 0;  
        while(f.hasNext())
        {
            Note next = (Note)f.next(); 
            if (!next.isRest())
            {
                if(next.getPitch() > highestPitch)
                {
                    highestPitch = next.getPitch(); 
                }
                if(next.getPitch() < lowestPitch)
                {
                    lowestPitch = next.getPitch(); 
                }
            }     
        }
        averageMIDI = (highestPitch + lowestPitch) / 2 ; 
        return averageMIDI; 
    }

    /**
     * Tells whether this Advice contains the note named.
     */

    boolean contains(Note note)
      {
      String notePC = note.getPitchClassName();

      Part.PartIterator i = newPart.iterator();
      while( i.hasNext() )
        {
        Unit nextUnit = (Unit)i.next();

        if( nextUnit instanceof Note && ((Note)nextUnit).isRest() )
          {
          }
        else
          {
          String nextNotePC = ((Note)nextUnit).getPitchClassName();
          if( PitchClass.enharmonic(notePC, nextNotePC) )
            {
            return true;
            }
          }
        }
      return false;
      }

  public int getSerial()
    {
    return serial;
    }

  public int getProfileNumber()
    {
    return profileNumber;
    }

  public boolean getMark()
    {
    return mark;
    }

  public void toggleMark()
    {
    mark = !mark;
    }
}
