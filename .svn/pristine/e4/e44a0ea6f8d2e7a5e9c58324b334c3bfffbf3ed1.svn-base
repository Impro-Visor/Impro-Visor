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

package jm.audio.synth;

import java.io.IOException;
import jm.audio.AudioObject;
import jm.audio.AOException;
import jm.music.data.Note;

/**
 * AllPass filters ...
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:46  2001
 */

public final class AllPass extends AudioObject{
	//The gain of this comb filter 
	private float decay;

	//The delay (in samples) to use with this comb filter
	private int delay;

	//Number of samples to delay by
	private float[] delayLine;

	//Delay line current index
	private int delayIndex;
	
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * @param delay delay in milliseconds
  	 * 
	 */
	public AllPass(AudioObject ao, int delay){
		this(ao,delay,0.5);
	}

	/**
	 * @param delay delay in milliseconds
	 * @param gain as a percent
	 */
	public AllPass(AudioObject ao, int delay, double decay){
		super(ao, "[AllPass]");
		this.decay = (float)decay;
		this.delay = delay;
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * @param buffer any number of incoming samples 
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
		int i=0;
		for(;i<returned;i++){
			buffer[i] += delayLine[delayIndex] * decay;
			float a = buffer[i] * -decay;
			float b = delayLine[delayIndex];
			delayLine[delayIndex] = buffer[i];
			buffer[i] = a+b;
			if(delayIndex >= delayLine.length){
				delayIndex = 0;
			}
		}
		return i;	
	}

	/**
	 * 
	 */
	public void build(){
		int sampleDelay = (int)((float)((float)this.delay/(float)1000) * this.sampleRate); 
		this.delayLine = new float[sampleDelay*this.channels];
		this.delayIndex = 0;
	}
}
