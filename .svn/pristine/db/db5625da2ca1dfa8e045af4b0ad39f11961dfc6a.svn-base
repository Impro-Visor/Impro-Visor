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
 * The Splitter AudioObject is responsible for splitting in input
 * signal into multiple output signals.
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public final class Splitter extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/**Stored buffer of samples*/
	float[] buf = null;
	/** The number of outputs that have currently requested buffers */
	int count = 0;
	/** The number of outputs to service */
	int outputs = 0;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	 * 
	 * @param ao the single AudioObject taken as input. 
	 * @param outputs the number of outputs to support.
	 */
	public Splitter(AudioObject ao){
		super(ao, "[Volume]");
	}
	
	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	
	/**
	 */
	public void build(){
		this.outputs = this.next.length;
	}
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 */
	public int work(float[] buffer)throws AOException{
		if(count == 0){
			this.buf = new float[buffer.length];
			this.previous[0].nextWork(buf);
		}
		if(++count == outputs)count = 0;
		for(int i=0;i<this.buf.length;i++){
			buffer[i] = this.buf[i];
		}
		return this.buf.length;
	}
}
