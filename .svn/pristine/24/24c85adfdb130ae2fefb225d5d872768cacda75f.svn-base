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

package jm.midi.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/***********************************************************
Description:
The PChange event is one of a set of events whose parent 
class is VoiceEvt.  In total these classes cover all voice
event types found in most MIDI file formats.
These classes will usually be added to a linked list as type
VoiceEvt.
(see class VoiceEvt for more information)	
@author Andrew Sorensen
************************************************************/

public final class PChange implements VoiceEvt, Cloneable{
	private final short id = 007;
	private short value;
	private short midiChannel;
	private int time;

	/**Create a program change event with no initial values*/
	public PChange(){
		this.value = 0;
		this.midiChannel = 0;
		this.time = 0;
	}
	/**Create a program change event with a program change value, midiChannel and time.*/ 
	public PChange(short value, short midiChannel, int time){
		this.value = value;
		this.midiChannel = midiChannel;
		this.time = time;
	}
	//---------------------------------
	//value
	/**Returns a program change events program change value*/
	public short getValue(){
		return value;
	}
	/**Sets a program change events program change value*/
	public void setValue(short value){
		this.value = value;
	}
	//--------------------------------
	//MIDI Channel
	/**Returns a program change events MIDI channel*/
	public short getMidiChannel(){
		return midiChannel;
	}
	/**Sets a program change events MIDI channel*/
	public void setMidiChannel(short midiChannel){
		this.midiChannel = midiChannel;
	}
	//--------------------------------
	//Time
	/**Returns a program change events time*/
	public int getTime(){
		return time;
	}
	/**Sets a program change events time*/
	public void setTime(int time){
		this.time = time;
	}
	//--------------------------------
	//Return ID
	public short getID(){
		return id;
	}

        //---------------------------------------------- 
	// Write the contents of this object out to disk 
	//----------------------------------------------
	public int write(DataOutputStream dos) throws IOException{
        //System.out.println("Writing program change");
		int bytes_out = jm.midi.MidiUtil.writeVarLength(this.time,dos);
		dos.writeByte((byte)(0xC0 + midiChannel));	
		dos.writeByte((byte) value);
		return bytes_out+2;
	} 
	
	//----------------------------------------------
	// Read the contends of this objec in from disk
	public int read(DataInputStream dis) throws IOException{
		this.value = (short) dis.readUnsignedByte();
		return 1;
	}

	//--------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		PChange event;
		try{
			event = (PChange) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new PChange();
		}
		return event;
	}
	//--------------------------------
	//Print
	public void print(){
		System.out.println(toString());
	}
        
        public String toString(){
		return new String("Program Change(007): [time = " + time + "][midiChannel = " + midiChannel + "][value = " + value + "]");
	}
}

