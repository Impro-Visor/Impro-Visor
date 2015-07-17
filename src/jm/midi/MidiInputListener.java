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
package jm.midi;

import jm.midi.event.Event;

/**
 * Listener for receiving MIDI events
 * 
 * @author Andrew Sorensen
 */

public interface MidiInputListener{
	/** 
	 * This method will be called to receive a jm.midi.event.Event whenever a new
	 * MIDI event is received.
	 */
	public abstract void newEvent(Event event);
}
