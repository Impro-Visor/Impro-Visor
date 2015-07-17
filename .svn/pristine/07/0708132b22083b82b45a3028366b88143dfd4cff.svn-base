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
package jm.util;

import jm.audio.io.*;
import jm.audio.Instrument;
import jm.audio.synth.*;
import jm.music.data.Note;
import jm.audio.AudioObject;

/**
* This instrument is used for playabck of .au files in the Play.au() method.
* @author Andrew Brown
*/
public final class AudioSampleInst extends jm.audio.Instrument{
	private String fileName;

	public AudioSampleInst(String fileName){
		this.fileName = fileName;
	}

	public void createChain(){
        SampleIn sin = new SampleIn(this, fileName, true, true); //, 4, 44100, 88200); // cahce and wholefile booleans
        // Envelope env = new Envelope(sin, new double[] {0.0, 0.0, 0.001, 1.0, 0.999, 1.0, 1.0, 0.0});
	}
}