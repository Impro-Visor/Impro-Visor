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


import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;
/**
 * The Wavelet Transform
 * @author Timothy Opie
 * @version 1.0, Thursday October 23, 2003
 * Last changed:
 */

public final class Wavelet extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------



	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public Wavelet(AudioObject ao){
		super(ao, "[FGT]"); 

	}
	
	
	/**
	 * --------------------------------------------
	 * Beginning
	 * --------------------------------------------
	 */
	public int work(float[] buffer)throws AOException{
	
		return buffer.length;
	}

}
