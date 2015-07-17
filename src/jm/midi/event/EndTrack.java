/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:56  2001

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

/**************************************************************
@author Andrew Sorensen
***************************************************************/

public final class EndTrack implements Event{
	private final short id;
	private int time;

	//------------------------------------------
	//Constructors
	/**Default Constructor*/
	public EndTrack(){
		this.id = 23;
		this.time = 0;
	}

	//-----------------------------------------------
	//time
	public int getTime(){	
		return time;
	}
	public void setTime(int time){
		this.time = time;
	}
	//----------------------------------------------
	//Return Id
	public short getID(){
		return id;
	}

        //---------------------------------------------- 
	// Write the contents of this object out to disk 
	//---------------------------------------------- 
	public int write(DataOutputStream dos) throws IOException{ 
		int bytes_out = jm.midi.MidiUtil.writeVarLength(this.time,dos);
		dos.writeByte((byte)0xFF); //META Event Type
		dos.writeByte((byte)0x2F); //TempoEvent Event Type
		dos.writeByte((byte)0x00);
		//bytes_out += jm.midi.MidiUtil.writeVarLength(0,dos);
		return bytes_out + 2;
	} 
	
	//----------------------------------------------
	// Read the contends of this objec in from disk
	public int read(DataInputStream dis) throws IOException{
		return 0;
	}

	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		TempoEvent event;
		try{
			event = (TempoEvent) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new TempoEvent();
		}
		return event;
	}
	//---------------------------------------------------
	//Print
	public void print(){
		System.out.println("EndTrack(023):             [time = " + this.time+"]"); }
}
