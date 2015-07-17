/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:45  2001

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

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/***********************************************************
Description:
The ATouch event is one of a set of events whose parent  
class is voice_event.  In total these classes cover all voice
event types found in most MIDI file formats.
These classes will usually be added to a linked list as type
voice_event.
(see class VoiceEvt for more information)	
@author Andrew Sorensen
************************************************************/
public final class ATouch implements VoiceEvt, Cloneable{
	private final short id = 001;
	private short pitch;
	private short pressure;
 	private short midiChannel;
	private int time;

	/**public Constructor for creating default (empty) ATouch events*/
	public ATouch(){
		this.pitch = 0;
		this.pressure = 0;
		this.midiChannel = 0;
		this.time = 0;
	}
	/**public Constructor for creating ATouch events containing pitch, 
	pressure, MIdI channel and time information */
	public ATouch(short pitch, short pressure, short midiChannel, int time){
		this.pitch = pitch;
		this.pressure = pressure;
		this.midiChannel = midiChannel;
		this.time = time;
	}
	//--------------------------------------------
	//pitch
	/**Returns an ATouch events pitch*/
	public short getPitch(){
		return pitch;
	}
	/**Sets an ATouch events pitch*/
	public void setPitch(short pitch){
		this.pitch = pitch;
	}
	//----------------------------------------
	//pressure
	/**Returns an ATouch events pressure*/
	public short getPressure(){
		return pressure;
	}
	/**Sets an ATouch events pressure*/
	public void setPressure(short pressure){
		this.pressure = pressure;
	}

	//-----------------------------------------
	//MIdI Channel
	public short getMidiChannel(){
		return midiChannel;
	}

	public void setMidiChannel(short midiChannel){
		this.midiChannel = midiChannel;
	}

	//------------------------------------------
	//time
	public int getTime(){
		return time;
	}

	public void setTime(int time){
		this.time = time;
	}

	//-----------------------------------------
	//Return Id
	public short getID(){
		return id;
	}

	//-------------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		ATouch event;

		try{
			event = (ATouch) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new ATouch();
		}
		return event;
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
		this.pitch = (short)dis.readUnsignedByte();
		this.pressure = (short)dis.readUnsignedByte();
		return 2;
	}

	//-------------------------------------
	//Print
	public void print(){
		System.out.println("ATouch(001):    				   [time = " + time + "][midiChannel = " + midiChannel + "][pitch = " + pitch + "][pressure = " + pressure + "]");
	}
}
