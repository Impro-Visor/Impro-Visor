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

/** 
* TempoEvent Events are contained in the MidiFile class's tempoList 
* unlike most of the other events which are track data stored in
* the track Event List. 
* @author Andrew Sorensen
*/

public final class TempoEvent implements Event{
	private short id = 020;
	private int time = 0;
	private double tempo = 60.0;

	//------------------------------------------
	//Constructors
	/**Default Constructor*/
	public TempoEvent(){
		this(0,0.0);
	}
        
	public TempoEvent(double tempo){
		this(0,tempo);
	}
        
	public TempoEvent(int time, double tempo){
		this.time = time;
		this.tempo = tempo;
	}
	//--------------------------------------------
	//TempoEvent 
	/**Returns a tempo value*/
	public double getTempo(){
		return tempo;
	}
        
	/**Sets the tempo events tempo field*/
	public void setTempo(double tempo){
		this.tempo = tempo;
	}
        
	//-----------------------------------------------
	/** return time */
	public int getTime(){	
		return time;
	}
        
        /** Specify the time */
	public void setTime(int time){
		this.time = time;
	}
        
	//----------------------------------------------
	/** Return Id */
	public short getID(){
		return id;
	}

        //---------------------------------------------- 
	/** Write the contents of this object out to disk */
	//---------------------------------------------- 
	public int write(DataOutputStream dos) throws IOException{ 
		int bytes_out = jm.midi.MidiUtil.writeVarLength(this.time,dos);
		dos.writeByte((byte)0xFF); //META Event Type
		dos.writeByte((byte)0x51); //TempoEvent Event Type
		bytes_out += jm.midi.MidiUtil.writeVarLength(3,dos);
		int temp = (int)(((float)60/(float)this.tempo)*(float)1000000);
		dos.writeByte((byte) (((temp>>16)&255)));
		dos.writeByte((byte) (((temp>>8)&255)));
		dos.writeByte((byte) ((temp&255)));
		return bytes_out + 5;
	} 
	
	//----------------------------------------------
	/** Read the contends of this object in from disk */
	public int read(DataInputStream dis) throws IOException{
		int b1 = (int)dis.readUnsignedByte();
		int b2 = (int)dis.readUnsignedByte();
		int b3 = (int)dis.readUnsignedByte();
		int temp = (b1<<16) + (b2<<8) + b3;
		this.tempo = (double) (1000000f / (float)temp * 60.0f);
		return 3;
	}

	/** Copy Object */
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
	/** Print data */
	public void print(){
		System.out.println("TempoEvent(020):             [time = " + this.time + "][tempo = " + this.tempo + "]");
	}
}
