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

package jm.audio.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import jm.JMC;
import jm.audio.AudioObject;
import jm.audio.AOException;
import jm.music.data.Note;

/**
 * Print synthesis data to the standard output.
 * @author Andrew Brown 
 * @version 1.0,Sun Feb 25 18:42:42  2001
 */
public final class PrintOut extends AudioObject implements JMC{
	//----------------------------------------------
	// ATTRIBUTES
	//----------------------------------------------
        /* the number of charracters across which to display the wave */
        private int width; 

        //----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * Excepts a single Audio Object only. 
	 */
	public PrintOut(AudioObject ao){
            this(ao, 80);
        }
        /**
	 * Excepts an Audio Object and screen width. 
	 */
	public PrintOut(AudioObject ao, int width){
		super(ao, "[PrintOut]");
                this.width = width;
                // create dummy temp file to avoid crash
                try {
                    RandomAccessFile f = new RandomAccessFile("jmusic.tmp", 
                                                              "rw");
                    try{
                        f.close();
                    } catch (IOException e) {};
                } catch (IOException e) {};
	}
	
	public void finalize(){

	}

	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * Write any incoming samples out to disk
	 * using the position (which is set by the note and
	 * then moved appropriately for each sample written)
	 * to place the sample into the RAF.<br>
	 * NOTE: Remember that only one AudioObject can ever
	 * be an input to this Object.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
		for(int i=0;i<returned;i++){
			float sample = buffer[i];
			//fill left side of dot
                        String dot = "";
                        
                        if (i%((int)((double)sampleRate / 8000.0) + 1) == 0) {
                            int counter = 0;
                            for (int j = 0; j < (int)((sample + 1.0) * (width * 0.5 - 4)); j++) {
                                    dot += " ";
                                    counter++;
                            }
                            // draw the dot
                            dot += "o";
                            // fill right side of dot
                            for (int k = counter; k < width - 4; k++) {
                                dot += " ";
                            }
                            // add value
                            sample *= 1000;
                            dot += (double)((int)(sample) /1000.0);
                            System.out.println(dot);
                        }                      
                  }
                  return returned;                  
        }
}
