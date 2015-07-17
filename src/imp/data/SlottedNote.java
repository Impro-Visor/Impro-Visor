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

package imp.data;

/**
 * Brandy McMenamy and James Thomas Herold
*  7/18/2007
 * The SlottedNote class is an abstraction used to describe a note.
 */
public class SlottedNote{
	/**
	 * As opposed to showing duration as some decimal value of a beat, instead
	 * each note is given a certain number of slots that tell its duration.
	 * The number of slots per beat is determined by the time signature in
	 * the MIDIBeast class
	 */
	private int numberOfSlots;
	/**
	 * The String form of the pitch is simply the letter corresponding to the pitch of
	 * a note, such as C, or A. This is used primarily in finding bass style, where
	 * the octave of the note is irrelevant, and only the notes relationship to the
	 * current chord is considered.
	 */
	private String pitch;
	/**
	 * The pitchNumber is the midi number for the pitch of the note.
	 */
	private int pitchNumber;
	
	/**
	 * 
	 * @param n the number of slots the note spans
	 * @param p the pitch of the note in String form
	 */
	public SlottedNote(int n, String p){
		numberOfSlots = n;
		pitch = p;
	}
	
	/**
	 * 
	 * @param n the number of slots the note spans
	 * @param p the midi pitch number of the note
	 */
	public SlottedNote(int n, int p){
		numberOfSlots = n;
		pitchNumber = p;
	}
	
	/**
	 * 
	 * @param n number of slots
	 */
	public void setNumberOfSlots(int n){
		numberOfSlots = n;
	}
	
	/**
	 * 
	 * @return numberOfSlots
	 */
	public int getNumberOfSlots(){
		return numberOfSlots;
	}
	
	/**
	 * 
	 * @return pitch
	 */
	public String getPitch(){
		return pitch;
	}
	
	/**
	 * 
	 * @return pitchNumber
	 */
	public int getPitchNumber(){
		return pitchNumber;
	}
	
	/**
	 * @return "Pitch: " + pitch + "\t Slots Occupied: " + numberOfSlots;
	 */
	public String toString(){
		return "Pitch: " + pitch + "\tPitch Number: " + pitchNumber + "\t Slots Occupied: " + numberOfSlots;
	}
	
}
	
	