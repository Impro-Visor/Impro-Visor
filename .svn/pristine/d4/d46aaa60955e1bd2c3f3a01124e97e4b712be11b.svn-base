/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:43  2001

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
package jm.midi;

import java.util.Enumeration;
import java.util.Vector; 

import jm.midi.event.*;

/**
 * The Track class is designed to hold a MIDI file Tracks SMF events 
 */
public class Track{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** A Vector to hold SMF events */
	private Vector eventList;
  
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	public Track(){
		this.eventList = new Vector();
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * Add a SMF event to the Track
	 * @param Event event - the SMF event to add
	 */
	public void addEvent(Event event){
		this.eventList.addElement(event);
	}

	/**
	 * Return the Tracks event Vector
	 * @return Vector - the event list
	 */
	public Vector getEvtList(){
		return this.eventList;
	}

	/**
	 * Print this Tracks event data to stdout
	 */
	public void print(){
		System.out.println("------------------");
		System.out.println("Track");
		Enumeration enum1 = eventList.elements();
		while(enum1.hasMoreElements()){
			Event event = (Event) enum1.nextElement();
			//event.print();
		}
		//System.out.println();
	}
}
