/* 

<This Java Class is part of the jMusic API version 1.4, February 2003.>

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

package jm.audio.synth;

import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;

/**
 * An Audio Object for granulation
 * @author Timothy Opie
 * @version 1.0, Saturday, May 1, 2004
 */

public final class Grain2 extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------

	private int grainSampSize = 1000; // Size of grains in samples
	private int spaceSamp = 1000; // space between grains in samples
	private int grainCount = 0;
	private int spaceCount = 0;
	private int offset=0;
	private boolean grainOn = true; //To cheack whether a grain is on or not

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public Grain2(AudioObject ao, int grainSampSize, int spaceSamp, boolean grainOn, int chan, int offset){
		super(ao, "[Grain]");
		this.grainSampSize = grainSampSize * chan;
		this.spaceSamp = spaceSamp * chan;
		this.grainOn = grainOn;
		this.offset = offset;
	}


	/**
	 * @param buffer The sample buffer.
	 * @return The number of samples processed
	 */
	public int work(float[] buffer)throws AOException{
		int returned=this.previous[0].nextWork(buffer);
		int tempoffset=offset;
		if(offset>0) {
			for(int counter=0;counter<offset;counter++) {
				buffer[counter] = 0;
			}
			offset=0;
		}

		for(int counter=tempoffset;counter<returned;counter++) {
			if (grainOn) buffer[counter] = buffer[counter] * 
				(float)(Math.sin(Math.PI*grainCount/grainSampSize));
			if (grainOn && grainCount<grainSampSize) grainCount++;
			else if (grainOn) {
				grainOn=false;
				grainCount=0;
			}
				
			if (!grainOn) buffer[counter] = 0;
			if (!grainOn && spaceCount<spaceSamp) spaceCount++;
			else if (!grainOn) {
				grainOn=true;
				spaceCount=0;
			}
			//System.out.println(buffer[counter]);
		}

	return returned;
	}

	//Real-time calls

	// Set the Grain Duration
	public void setGrainDur(int gdur){
		grainSampSize = gdur;
		System.out.println("Space4: " + grainSampSize);
	}

	// Set the Space Duration
	public void setSpaceDur(int sdur){
		spaceSamp = sdur;
		System.out.println("Space4: " + spaceSamp);
	}
}


