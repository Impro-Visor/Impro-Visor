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
 * The <bl>Invert</bl> Audio Object is a simple 180 degree phase inverter.
 * any signal that passes through it will be filpped such that all positive
 * values will eb negative and vise versa.
 * @author Andrew brown
 * @version 1.0, March 2003
 */

public final class Invert extends AudioObject{
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	* The standard Invert constructor takes a Single
	 * Audio Object as input. The audio object
         * passed to it is the prior one in the chain.
	 */
	public Invert(AudioObject ao){
	   super(ao, "[Invert]");
	}
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * The work method for <bl>Invert<bl> will change
	 * the phase of each sample in the buffer.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
		for(int i=0;i<returned;i++){
                    buffer[i]=buffer[i] * -1.0f;
		}
		return returned;
	}
}
