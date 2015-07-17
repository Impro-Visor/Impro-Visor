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
 * Sums any number of inputs.
 * @author Andrew Brown
 */

public final class Multiply extends AudioObject{
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * This constructor takes any number of AudioObjects
	 * as input expecting each of them to be passing
	 * in sample data to be summed before output.
	 * @param ao any number of Audio Objects.
	 */
	public Multiply(AudioObject[] ao){
		super(ao, "[Multiply]");
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * This nextWork method Multiplys all inputs together 
	 * and passes on a normalised result of the
	 * sum.
	 * @param input any number of incoming samples 
	 */
	public int work(float[] buffer)throws AOException{
		float[][] buf = new float[this.inputs][];
		buf[0] = new float[buffer.length];
		int returned = this.previous[0].nextWork(buf[0]);
		for(int i=1;i<inputs;i++){
			buf[i] = new float[returned];
			if(returned != this.previous[i].nextWork(buf[i])){
				throw new AOException(this.name,0);
			}
		}
		int ret=0;
		for(;ret<returned;ret++){
			buffer[ret] = buf[0][ret];
			for(int j=1;j<inputs;j++){
				buffer[ret]*=buf[j][ret];
			}
		}
		return ret;	
	}
}
