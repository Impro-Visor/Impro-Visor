/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:47  2001

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
The CPres event is one of a set of events whose parent 
class is VoiceEvt.  In total these classes cover all voice
event types found in most MIDI file formats.  These 
classes will usually be added to a linked list as type
VoiceEvt.(see class VoiceEvt for more information)	
@author Andrew Sorensen
************************************************************/
public final class CPres implements VoiceEvt, Cloneable{
	private final short id = 002;
	private short pressure;
	private short midiChannel;
	private int time;
				
	/**A public constructor for creating default (empty) channel pressure 
	   events.*/
	public CPres(){
		this.pressure = 0;
		this.midiChannel = 0;
		this.time = 0;
	}
	/**A public constructor for creating channel pressure events 
	   which contain pressure, MIDI channel and time information.*/
	public CPres(short pressure, short midiChannel, int time){
		this.pressure = pressure;
		this.midiChannel = midiChannel;
		this.time = time;
	}
	//-------------------------------------
	//pressure
	/**Returns a channel pressure events pressure value*/
	public short getPressure(){
		return pressure;
	}
	/**Sets a channel pressure events pressure value*/
	public void setPressure(short pressure){
		this.pressure = pressure;
	}
	//---------------------------------
	//MIDI Channel
	public short getMidiChannel(){
		return midiChannel;
	}
	public void setMidiChannel(short midiChannel){
		this.midiChannel = midiChannel;
	}
	//---------------------------------
	//Time
	public int getTime(){
		return time;
	}
	public void setTime(int time){
		this.time = time;
	}
	//-----------------------------------
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
		this.pressure = (short) dis.readUnsignedByte();
		return 1;
	}

	//--------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		CPres event;
		try{
			event = (CPres) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new CPres();
		}
		return event;
	}
	//--------------------------------
	//Print
	public void print(){
		System.out.println("Channel Pressure(002):	[time = " + time + "][midiChannel = " + midiChannel + "][pressure = " + pressure + "]");
	}
}
