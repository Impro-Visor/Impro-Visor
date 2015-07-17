/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:57  2001

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

// Updates by Sean Hayes

package jm.midi.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Andrew Sorensen and Sean T. Hayes
 */

public final class TimeSig implements Event{
	public static final short ID = 021;
	private int time;
	private int numerator;
	private int denominator;
        
        /**
         * Specifies how often the metronome should click in terms of the number 
         * of clock signals per click, which come at a rate of 24 per 
         * quarter-note.  For example, a value of 24 would mean to click once 
         * every quarter-note (beat) and a value of 48 would mean to click once 
         * every half-note (2 beats)
         */
        private int metronomePulse;
        private int thirtySecondNotesPerBeat;

	//------------------------------------------
	//Constructors
	/**Default Constructor*/
	public TimeSig()
        {
                this( 0, 4, 4 );
	}
        
	public TimeSig(int numerator, int denominator)
        {
            this( 0, numerator, denominator );
	}
        
	public TimeSig(int time, int numerator, int denominator){
            this.time = time;
            this.numerator = numerator;
            this.denominator = denominator;
            metronomePulse = 24;
            thirtySecondNotesPerBeat = 32;
	}
	//--------------------------------------------
	public int getDenominator(){
		return this.denominator;
	}
	public void setDenominator(int denom){
		this.denominator = denom;
	}
	public int getNumerator(){
		return this.numerator;
	}
	public void setNumerator(int numer){
		this.numerator = numer;
	}
        
        /**
         * By Sean Hayes
         * @return metronome pulse
         * @see sp.midi.event.TimeSig
         */
        public int getMetronomePulse()
        {
            return metronomePulse;
        }
        
        /**
         * Specifies how often the metronome should click in terms of the number 
         * of clock signals per click, which come at a rate of 24 per 
         * quarter-note.  For example, a value of 24 would mean to click once 
         * every quarter-note (beat) and a value of 48 would mean to click once 
         * every half-note (2 beats)<br />
         * by Sean Hayes
         * @param metronomePulse
         */
        public void setMetronomePulse( int metronomePulse )
        {
            this.metronomePulse = metronomePulse;
        }
        
        public int getThirtySecondNotesPerBeat()
        {
            return thirtySecondNotesPerBeat;
        }
        
        public void setThirtySecondNotesPerBeat( int thirtySecondNotesPerBeat )
        {
            this.thirtySecondNotesPerBeat = thirtySecondNotesPerBeat;
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
		return ID;
	}

    //---------------------------------------------- 
	// Write the contents of this object out to disk 
	//----------------------------------------------
	public int write(DataOutputStream dos) throws IOException{
		int bytes_out = jm.midi.MidiUtil.writeVarLength(this.time,dos); 
		dos.writeByte(0xFF);
		dos.writeByte(0x58);	
		bytes_out += jm.midi.MidiUtil.writeVarLength(4,dos); 
		dos.writeByte((byte) this.numerator);
		int num = this.denominator;
        int cnt = 0;
		while(num%2==0){num /= 2;cnt++;}
        dos.writeByte((byte)cnt);
        dos.writeByte(0x18);
		dos.writeByte(0x08);
		return bytes_out+6;
	} 

        /**
         * Read the contends of this objec in from disk
         * Implemented by Sean T. Hayes
         * @param dis a stream of MIDI data
         * @return the number of bytes read
         * @throws java.io.IOException
         */
	public int read(DataInputStream dis) throws IOException
        {
            numerator = (short)dis.readUnsignedByte();
            int power = (short)dis.readUnsignedByte();
            denominator = 1 << power; //  = Math.pow( 2, power )
            metronomePulse = dis.readUnsignedByte();
            thirtySecondNotesPerBeat = dis.readUnsignedByte();
            return 4;
	}

	//------------------------------------------------
	//Copy Object
	public Event copy() throws CloneNotSupportedException{
		TimeSig event;
		try{
			event = (TimeSig) this.clone();
		}catch(CloneNotSupportedException e){
			System.out.println(e);
			event = new TimeSig();
		}
		return event;
	}
	//---------------------------------------------------
	//Print
	public void print(){
		System.out.println("TimeSig(021):             [time = " + this.time + "][numerator = " + this.numerator + "][denominator = "+this.denominator+"]");
	}
}
