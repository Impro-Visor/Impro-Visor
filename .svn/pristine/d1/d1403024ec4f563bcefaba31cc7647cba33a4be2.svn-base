/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:53  2001

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

package jm.midi.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/***********************************************************
Description:
The PWheel event is one of a set of events whose parent 
class is VoiceEvt.  In total these classes cover all voice
event types found in most MIDI file formats.
These classes will usually be added to a linked list as type
VoiceEvt.
(see class VoiceEvt for more information)	
@author Andrew Sorensen
************************************************************/
public final class PWheel implements VoiceEvt, Cloneable{
	private final short id = 006;
	private int value;
	private short midiChannel;
	private int time;

	/**Public constructor for creating a default (empty) pitch wheel event*/ 
	public PWheel(){
		this.value = 0;
		this.midiChannel = 0;
		this.time = 0;
	}
	/**Public constructor for creating a pitch wheel event with pitch wheel value
	midi channel and time*/
	public PWheel(int value, short midiChannel, int time){
		this.value = value;
		this.midiChannel = midiChannel;
		this.time = time;
	}
	//------------------------------------
	//value
	/**Return pitch wheel events pitch wheel value*/
	public int getValue(){
		return value;
	}
	/**Set pitch wheel events pitch wheel value*/
	public void setValue(int value){
		this.value = value;
	}
	//-------------------------------------
	//MIDI Channel
	/**Return pitch wheel events MIDI channel*/
	public short getMidiChannel(){
		return midiChannel;
	}
	/**Set pitch wheel events MIDI channel*/
	public void setMidiChannel(short midiChannel){
		this.midiChannel = midiChannel;
	}
	//-------------------------------------
	//Time
	/**Return pitch wheel events time*/
	public int getTime(){
		return time;
	}
	/**Set pitch wheel events time*/
	public void setTime(int time){
		this.time = time;
	}
	//-------------------------------------
	//Return ID
	public short getID(){
		return id;
	}

        //---------------------------------------------- 
	// Write the contents of this object out to disk 
	//----------------------------------------------
	public int write(DataOutputStream dos) throws IOException{
		return 0;
	} 
	
	//----------------------------------------------
	// Read the contends of this objec in from disk
	public int read(DataInputStream dis) throws IOException{
        // Pitch wheel change is a 14 bit value stored in two bytes,
        // least significant first
		this.value = (int) dis.readUnsignedByte();
        this.value += ((int) dis.readUnsignedByte()) * 128;
		return 1;
	}

	//--------------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		PWheel event;
		try{
			event = (PWheel) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new PWheel();
		}
		return event;
	}
	//---------------------------------------
	//Print
	public void print(){
		System.out.println("Pitch Wheel(006):				  [time = " + time + "][midiChannel = " + midiChannel + "][value = " + value + "]");
	}
}
