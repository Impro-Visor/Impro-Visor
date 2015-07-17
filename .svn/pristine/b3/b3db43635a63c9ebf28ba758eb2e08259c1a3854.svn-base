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

import java.io.*;
import java.util.Vector;

import jm.JMC;
import jm.midi.event.*;

/**
 * A number of helpful static methods used for various MIDI purposes.
 * @author Andrew Sorensen and Sean T. Hayes
 */

public final class MidiUtil implements JMC{

    private static final boolean VERBOSE = false;
	//--------------------------------
	//Variable length Stuff
	//--------------------------------
	/**
	 * Read variable length data
	 * @param DataInputStream dis
	 * @return int - the result we want 
	 * @throws IOException
	 */
	public static int readVarLength(DataInputStream dis) throws IOException{
            short c = (short) dis.readUnsignedByte();
            int value = c;				
            if((c & 0x80) != 0){
                value &= 0x7F;
                do{
                    c = (short) dis.readUnsignedByte();
                    value = (value << 7) + (c & 0x7F);
                }while((c & 0x80) != 0);  
            }    
            return(value);
	}

	/**
	 * Write variable length value
	 * @param int value - value to make into variable length
	 * @return the number of bytes written
	 * @exception IOException
	 */
	public static int writeVarLength(int value, DataOutputStream dos) 
            throws IOException{
                            
            int bytes_out = 0;
            long buffer = value & 0x7F;
            while((value >>= 7) > 0){
                buffer <<= 8;
                buffer |= ((value & 0x7F) | 0x80);
            }
            while(true){
                dos.writeByte((byte) buffer);
                bytes_out++;
                if((buffer & 0x80) != 0){
                        buffer >>= 8;
                }else{
                        break;
                }
            }    
            return bytes_out;
	}

	/**
	 * Return number of bytes used making variable length value
	 * @param int value - value being written
	 * @return int - the number of bytes written
	 */
	public static int varLengthBytes(int value){
            int numOfBytes = 0;
            long buffer = value & 0x7F;
            while((value >>=7) > 0){
                buffer <<= 8;
                buffer |= ((value & 0x7F) | 0x80);
            }
            while(true){
                numOfBytes++;
                if((buffer & 0x80) != 0){
                        buffer >>= 8;
                }else{
                        break;
                }
            }
            return numOfBytes;
	}

	/**
	 * Returns the time of a MIDI event by finding its end event
	 * and calculating the time between the start and end events
	 * @param Node node
	 */
	public static double getEndEvt(short pitch, Vector evtList, int index){
            double time = 0.0;
            //we don't need to read the first note as we allready know it
            index++; 
            for(;index<evtList.size();index++){
                Event evt = (Event) evtList.elementAt(index);
                time += evt.getTime();
                switch(evt.getID()){
                case 005:
                    NoteOn noteOn = (NoteOn) evt;
                    if(noteOn.getPitch() == pitch && 
                                noteOn.getVelocity() == 0){
                        //is this a second (or third) voice
                        if(time > 0){ 
                            //once assigned turn it off
                            noteOn.setPitch((short)255);
                            return time;
                        }
                    }
                    break;
                case 004:
                    NoteOff noteOff = (NoteOff) evt;
                    if(noteOff.getPitch() == pitch){
                        //is this a second (or third) voice
                        if(time > 0){ 
                            //once assigned turn it off
                            noteOff.setPitch((short)255); 
                            return time;
                        }
                    }
                    break;
                }
            }
            System.out.println("Error reading file - sorry!");
            System.out.println("Try to continue reading anyway");
            //System.exit(1);
            return 0.0;
	}

	/**
	 * Creates a new MIDI Voice Event
	 */
	public static Event createVoiceEvent(int id)throws IOException{
            switch(id){
            case 0x8:
                    //System.out.println("NoteOFF");
                    return new NoteOff();
            case 0x9:
                    //System.out.println("NoteON");
                    return new NoteOn();
            case 0xA:
                    //System.out.println("ATouch");
                    return new ATouch();
            case 0xB:
                    //System.out.println("CChange");
                    return new CChange();
            case 0xC:
                    //System.out.println("PChange");
                    return new PChange();
            case 0xD:
                    //System.out.println("CPRES");
                    return new CPres();
            case 0xE:
                    //System.out.println("PWHEEL");
                    return new PWheel();
            default:
                    return null;
            }
	} 

    /**
     * Creates a new MIDI meta Event
     * Sean Hayes: added support for TimSig and KeySigs
     * @param id
     * @return
     * @throws java.io.IOException
     */
	public static Event createMetaEvent(int id)throws IOException{ 
            switch(id){
            case 0x51:
                if (VERBOSE) System.out.println("META EVENT: TempoEvent: "+id);
                return new TempoEvent();
            case 0x2F:
                if (VERBOSE) System.out.println("End of Track");
                return new EndTrack();
            case 0x58:
                if (VERBOSE) System.out.println( "Time Signature Event" );
                return new TimeSig();
            case 0x59:
                if (VERBOSE) System.out.println( "Key Signature Event" );
                return new KeySig();
            default: 
                if (VERBOSE) System.out.println("META: "+id);
                return null;
            }
	} 

	/**
	 * Create a new MIDI SysEx event
	 */
	public static Event createSysExEvent(int id) throws IOException{
            switch(id){
            default: 
                    return null;
            }
	} 
}
