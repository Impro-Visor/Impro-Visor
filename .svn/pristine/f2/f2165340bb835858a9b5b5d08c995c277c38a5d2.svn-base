/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/
package jm.audio.synth;

import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;

/**
 * The Value object provides a mechanism for passing
 * audio objects static values.  This can be useful for seeding
 * wavetables with fixed frequency information for example (as in
 * the SimpleFM instrument example code).<br> 
 * Fixed values can use a value set by the constructor or can take one of a
 * notes attributes as its value by specifying that attributes index value in
 * the appropriate constructor (see the noteAttribute varibable for the
 * note attributes index).
 *
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:48  2001
 */

public final class Value extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** 
	 * A value which will be passed to any AudioObject which follow.
	 * The value can be fixed or linked to a Note attribute.
	 * This can useful for setting modulation settings in instruments.
	 */
	private float theValue; //default is to make no alteration
        /** multiplyer of the value. Useful for adjusting value 
        * in real time relative to a fixed note attribute. */
        private double changeRatio = 1.0;

	/**
	 * Which of the notes values do we wish to use as the NoteValue?<br>
	 * 0 = don't use any note attributes<br>
	 * 1 = pitch<br>
	 * 2 = dynamic<br>
	 * 3 = duration<br>
	 * 4 = rhythmValue<br>
	 */
	int noteAttribute = 0;
	// constants
	public static final int FIXED = 0,
		NOTE_PITCH = 1,
		NOTE_DYNAMIC = 2,
		NOTE_DURATION = 3,
		NOTE_RHYTHM_VALUE = 4;
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	 * Value must be a primary audio object as it can only
	 * return a fixed value.
	 * @param sampleRate SampleRate to use for this chain.
	 * @param channels Number of channels for this chain.
	 * @param fixedValue the value to use for this fixed value audio object.
	 */
	public Value(Instrument inst,int sampleRate, int channels, 
				float fixedValue){
		super(inst, sampleRate,"[Value]");
		this.theValue = fixedValue;
		this.channels = channels;
	}
	
	/**
	 * Value must be a primary audioobject as it can only return a fixed
	 * value.<br>
	 * The noteAttribute variable specifies the note attribute that this fixed
	 * value object will use.<br>  Please read the noteAttribute variable
	 * description for an index of note attribute variables.
	 * @param sampleRate SampleRate to use for this chain.
	 * @param channels Number of channels for this chain.
	 * @param noteAttribute An index number for note attribute to assign to
	 * this fixed value object.
	 */
	public Value(Instrument inst, int sampleRate, int channels, 
				int noteAttribute){
		super(inst, sampleRate,"[Value]");
		this.noteAttribute = noteAttribute;
		this.channels = channels;
	}
        
        /** Report the changeRatio value. */
        public double getChangeRation() {
            return this.changeRatio;
        }
        
        /** Specify the changeRatio value. 
        * @param newVal the ratio figure to update to
        */
        public void setChangeRation(double newVal) {
            this.changeRatio = newVal;
        }
	
	/**
	 * Will fill as many buffers as required with whatever value the fixed
	 * value variable of this object is set to.
	 * @param buffer The sample buffer.
	 * @return The number of samples processed.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = 0;
		for(;returned<buffer.length;returned++){
			buffer[returned] = theValue;
		}
		return returned;
	}

	/**
	 * Sets the fixed value for this object based on the noteAttribute
	 * variable. A value of 0 will ignore all note attributes where any other
	 * number will set the fixed value of the object to equal the note
	 * attribute which is being copied.
	 */
   	public void build(){
		switch(this.noteAttribute){
		case 0: //use the value set by the constructor (ignore the note)
			break;
		case 1:
			this.theValue = (float)(currentNote.getFrequency() * (double)changeRatio);
			break;
		case 2:
			this.theValue = (float)127.0 / (float)(currentNote.getDynamic() * changeRatio);
			break;
		case 3:
			this.theValue = (float)(currentNote.getDuration() * changeRatio);
			break;
		case 4:
			this.theValue = (float)(currentNote.getRhythmValue() * changeRatio);
			break;
		default:
			System.err.println(this.name+" A value setting of " + theValue + " is not supported yet");
			System.exit(1);
		}
	}
}
