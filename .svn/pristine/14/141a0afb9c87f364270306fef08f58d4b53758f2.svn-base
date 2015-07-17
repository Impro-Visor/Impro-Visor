/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2001 Andrew Sorensen & Andrew Brown

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

import java.lang.InterruptedException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Vector;
import java.util.Enumeration;
import javax.sound.midi.*;
import java.io.IOException;

import jm.midi.event.Event;
import jm.midi.event.VoiceEvt;
import jm.midi.MidiUtil;
import jm.midi.MidiInputListener;

/**
 * Real time midi input.
 *
 * @author Andrew Sorensen 
  */

public class RTMidiIn implements Receiver {

	/** Used to hold running status state information */
	private int oldStatus;
	/** contains a list of listeners for this object */
	private Vector listeners;
	/** The transmitter which sends this receiver its events */
	private Transmitter trans = null;
	
	/**
	 * Constructor
	 */
	public RTMidiIn(){
		listeners = new Vector();
		this.init();
	}

	/**
	 * Attached Listeners
	 */
	public void addMidiInputListener(MidiInputListener mil){
		listeners.add(mil);
	}

	/**
	 * Notify all listeners of a new event
	 */
	public void notifyListeners(Event event){
		Enumeration en = listeners.elements();
		while(en.hasMoreElements()){
			((MidiInputListener)en.nextElement()).newEvent(event);
		}
	}

	/**
	 * Called from the JavaSound MIDI Input Port for each new MIDI event
	 */
	public void send(MidiMessage message, long deltaTime){
		System.out.println("New MIDI message");
		Event event = null;
		ByteArrayInputStream bais=new ByteArrayInputStream(message.getMessage());
		DataInputStream dis = new DataInputStream(bais);
		try{
			dis.mark(2);
			int status = dis.readUnsignedByte();
			int length = 0;
			//check running status
			if(status < 0x80){
				status = oldStatus;
				dis.reset();
			}
			if(status >= 0xFF){//MetaEvent
				int type = dis.readUnsignedByte();
				length = MidiUtil.readVarLength(dis);
				event = MidiUtil.createMetaEvent(type);
			}else if(status >= 0xF0){ //System Exclusive -- Not Supported
				System.err.println("SysEX not supported");
				length = MidiUtil.readVarLength(dis);
			}else if(status >= 0x80){ //MIDI voice event
				short selection = (short) (status /0x10);
				short midiChannel = (short) (status - (selection * 0x10));
				VoiceEvt evt = (VoiceEvt)MidiUtil.createVoiceEvent(selection);
				evt.setMidiChannel(midiChannel);
				event = evt;
				if(event == null){
					throw new IOException("Read Error");
				}
			}
			if(event != null){
				event.setTime((int)deltaTime);
				event.read(dis);
			}
			oldStatus = status;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		this.notifyListeners(event);
	}

	/**
	 * Close method to release resources
	 */
	public void close(){
		this.trans.close();
	}
	
	/**
	 * Initialise the input source
	 */
   	private boolean init() {
   		if (trans == null) {
      		try {
         		if (MidiSystem.getReceiver() == null) {
            			System.err.println("MidiSystem Receiver Unavailable");
              			 return false;
          		}
			MidiDevice.Info[] mdi=MidiSystem.getMidiDeviceInfo();
			for(int i=0;i<mdi.length;i++){
				System.out.println(mdi[i]);
			}
			trans = MidiSystem.getTransmitter();
			trans.setReceiver(this);
        	}catch (MidiUnavailableException e) {
         		System.err.println("Midi System Unavailable:" + e);
           		return false;
         	}
   	}
     		return true;
	}
}// MidiSynth
