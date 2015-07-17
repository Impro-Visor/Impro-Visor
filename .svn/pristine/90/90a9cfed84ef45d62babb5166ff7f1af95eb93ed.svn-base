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
* This class keeps a sample buffer the length of the
 * current note and feeds them to the chain.
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public final class NoteBuffer extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	private float[] noteBuffer;
	private boolean flag = true;
	private int noteBufferPosition;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public NoteBuffer(AudioObject ao){
		super(ao, "[Volume]");
	}

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	public void build(){
		noteBuffer = new float[numOfSamples];
		noteBufferPosition = 0;
		this.flag = true;
	}
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	public int work(float[] buffer)throws AOException{
		if(flag){
			int returned = this.previous[0].nextWork(noteBuffer);
			flag = false;
		}
		int i = 0;
		int numof=((noteBufferPosition + buffer.length) < numOfSamples)
			? (numOfSamples - noteBufferPosition) : buffer.length;
		for(; i < numof; i++){
			buffer[i] = noteBuffer[noteBufferPosition++];
		}
		return i;
	}
}
