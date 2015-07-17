/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:46  2001

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
The CChange event is one of a set of events whose parent  
class is VoiceEvt.  In total these classes cover all voice
event types found in most MIDI file formats.
These classes will usually be added to a linked list as type
VoiceEvt.
(see class VoiceEvt for more information)	
@author Andrew Sorensen
************************************************************/
public final class CChange implements VoiceEvt, Cloneable{
	private final short id = 003;
	private short controllerNum;
	private short value;
	private short midiChannel;
	private int time;

	/**A public constructor used to create default control change events*/
	public CChange(){
		this.controllerNum = 0;
		this.value = 0;
		this.midiChannel = 0;
		this.time = 0;
	}
	/**A public constructor used create control change events containing
	contoller number, value and time information.*/
	public CChange(short controllerNum, short value, short midiChannel, int time){
		this.controllerNum = controllerNum;
		this.value = value;
		this.midiChannel = midiChannel;
		this.time = time;
	}
	//---------------------------------
	//controllerNum
	/**Returns a crontrol change events controller number*/
	public short getControllerNum(){
		return controllerNum;
	}
	/**Sets a control change events controllerNum*/
	public void setControllerNum(short controllerNum){
		this.controllerNum = controllerNum;
	}
	//----------------------------------
	//value
	/**Returns a control change events opperation value*/ 
	public short getValue(){
		return value;
	}
	/**Sets a control change events opperation value*/
	public void setValue(short value){
		this.value = value;
	}
	//-----------------------------------
	//MIDI Channel
	public short getMidiChannel(){
		return midiChannel;
	}
	public void setMidiChannel(short midiChannel){
		this.midiChannel = midiChannel;
	}
	//-----------------------------------
	//Time
	public int getTime(){
		return time;
	}
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
		int  bytes_out = jm.midi.MidiUtil.writeVarLength(this.time,dos);
		dos.writeByte((byte) (0xB0 + midiChannel));
		dos.writeByte((byte) controllerNum);
		dos.writeByte((byte) value);
		return bytes_out+3;
	}

	//---------------------------------------------- 
	// Read the contends of this objec in from disk 
	public int read(DataInputStream dis) throws IOException{
		this.controllerNum = (short) dis.readUnsignedByte();
		this.value = (short) dis.readUnsignedByte();
		return 2;
	}
	
	//------------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		CChange event;
		try{
			event = (CChange) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new CChange();
		}
		return event;
	}
	//-----------------------------------
	//Print
	public void print(){
		System.out.println("Contol Change(003):			 [time = " + time + "][midiChannel = " + midiChannel + "][contoller_num = " + controllerNum + "][value = " + value + "]");
	}
}
