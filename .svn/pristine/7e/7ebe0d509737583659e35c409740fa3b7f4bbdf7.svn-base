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
 * The <bl>Smooth</bl> Audio Object is a simple filter. Any samples
 * that pass through the <bl>Smooth</bl> Object will have their amplitudes
 * avergaed with the previous sample.
 * @author Andrew Brown
 * @version 1.0, July 2003.
 */

public final class Smooth extends AudioObject{
        private float[] prevSampleValues;
        
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	* The standard Smooth constructor takes a Single
	 * Audio Object as input.
	 * @param ao The single AudioObject taken as input.
	 */
	public Smooth(AudioObject ao){
	    super(ao, "[Smooth]");
	}
        
        public void build() {
            prevSampleValues = new float[channels];
            for(int i=0; i<prevSampleValues.length; i++) {
                prevSampleValues[i] = 0.0f;
            }
        }
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * The nextWork method for <bl>Smooth<bl> will average amplitude values.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
                for(int i=0;i<returned;i+=channels){
                    for(int j=0;j<channels;j++){
                        buffer[i+j] = buffer[i+j] * 0.5f + prevSampleValues[j] * 0.5f;
                        prevSampleValues[j] = buffer[i+j];
                    }
                }
		return returned;
	}
}
