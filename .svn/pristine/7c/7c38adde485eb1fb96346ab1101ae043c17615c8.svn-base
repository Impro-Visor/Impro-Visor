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
import jm.audio.math.*;

/**
 * An FFT transformation from time to frq.
 * 
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:46  2001
 */

public final class FFT extends AudioObject{
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 *
	 */
	public FFT(AudioObject ao){
		super(ao, "[FFT]");
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * Process each buffer of samples in turn.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
		RealFloatFFT_Radix2 fft = null;
		fft=new RealFloatFFT_Radix2(inst.getBufSize());
		fft.transform(buffer);
		return returned;
	}
}
