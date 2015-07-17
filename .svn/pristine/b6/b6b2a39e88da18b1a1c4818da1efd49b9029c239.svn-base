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

import jm.audio.AOException;
import jm.audio.AudioObject;

/**
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public final class Window extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** algorithm to use for the window */
	private int type;
	/** is this window the input or output */
	private boolean direction;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	 * @param outputs the number of outputs to support.
	 * @param ao the single AudioObject taken as input. 
	 */
	public Window(AudioObject ao, int type, boolean direction){
		super(ao, "[Window]");
		this.type = type;
		this.direction = direction;
	}
	
	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
		if(direction){
			for(int i=0;i<returned;i++){
				buffer[i] = buffer[i] * (float)(Math.sin(Math.PI*i/returned));
			}
		}else{
			for(int i=0;i<returned;i++){
				buffer[i] = buffer[i] / (float)(Math.sin(Math.PI*i/returned));
			}
		}
		return returned;
	}
}
