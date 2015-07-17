/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:55  2001

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

import java.util.Vector;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**************************************************************
SysEx is an interfaced by Event and contains a tracks System 
Exclusive events.  The system exclusive message is saved in a
ByteList called message.
@author Andrew Sorensen
***************************************************************/

public final class SysEx implements SysComEvt{
	private Vector message;
	private int time;
	private final short id = 010;
				//-------------------------------------
	//Constructors
	/**Default Constructor*/
	public SysEx(){
			message = new Vector();
			//message = new String("SysEx message List");
	}
	//--------------------------------------
	//Message List
	/**Add elements to the messge List*/
	public void addToList(byte thisByte){
			//message.addElement(thisByte);
	}
	/**Returns the SysEx message List*/
	public Vector getList(){
		return message;
	}
	//--------------------------------------
	//time
	public int getTime(){
		return time;
	}
	public void setTime(int time){
		this.time = time;
	}
	//-------------------------------------
	//Return Id
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
		return 0;
	}

	//---------------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		SysEx event = null;
		try{
			event = (SysEx) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new SysEx();
		}
		return event;
	}

	//-------------------------------------
	//Print
	public void print(){
		System.out.println("System Exclusive(010): [time =" + this.time + "]");
	}
}
