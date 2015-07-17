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
 * Tap Delay ...
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:46  2001
 */

public final class TapDelay extends AudioObject{
	//The gain of this comb filter 
	private float decay;

	//The delay (in samples) to use with this comb filter
	private int delay;

	//Number of samples to delay by
	private float[] delayLine;

	//Delay line current index
	private int delayIndex;

	//number of taps in this delay line
	private int taps;

	//number of samples which delay is equal to
	private int sampleDelay;
	
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * @param delay delay in milliseconds
  	 * 
	 */
	public TapDelay(AudioObject ao, int delay, int taps){
		this(ao,delay,taps,0.5);
	}

	/**
	 * @param delay delay in milliseconds
	 * @param gain as a percent
	 */
	public TapDelay(AudioObject ao, int delay, int taps, double decay){
		super(ao, "[Tap Delay]");
        this.finished = false;
		this.decay = (float)decay;
		this.delay = delay;
		this.taps = taps;
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * @param buffer any number of incoming samples 
	 */
	public int work(float[] buffer)throws AOException{
		int returned = buffer.length;
		if(!this.inst.finishedNewData && this.inst.getFinished()) returned = this.previous[0].nextWork(buffer);
		int i=0;
		float max = 0.0f;
		for(;i<returned;i++){
			for(int k=1;k<=taps;k++){
				int tapIndex = delayIndex+(sampleDelay*this.channels*k);
				if(tapIndex>=delayLine.length)tapIndex-=delayLine.length; 
				delayLine[tapIndex] += buffer[i]*(decay/k);
			}
			buffer[i] += delayLine[delayIndex];
			delayLine[delayIndex] = 0.0f;
			delayIndex++;
			if(delayIndex >= delayLine.length){
				delayIndex = 0;
			}
			if(max < buffer[i])max=buffer[i];     
		}
		if(this.inst.iterations <= (0-delayLine.length)){
			this.finished=true;
		}
		return i;
	}

	/**
	 * 
	 */
	public void build(){
		if(delayLine == null){
			this.sampleDelay = (int)(((float)this.delay/1000.0f) * (float)this.sampleRate); 
			this.delayLine = new float[sampleDelay*this.channels*taps];
			this.delayIndex = 0;
		}
      this.finished=false;
	}
}
