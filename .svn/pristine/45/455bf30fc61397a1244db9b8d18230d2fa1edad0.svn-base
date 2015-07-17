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

import java.io.*;
import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;

/**
 * Reads in Audio data from an ascii text file and casts that data
 * into 32 floating point values which is the internal format used in
 * the jMusic audio architecture.<br>
 * @author Andrew Sorensen and Andrew Brown
 * @version 1.0,Sun Feb 25 18:42:41  2001
 */
public final class TextIn extends AudioObject implements jm.JMC{
	//----------------------------------------------
	// ATTRIBUTES
	//----------------------------------------------
	/** Debug constant specific to AUIn file */
	private boolean DEBUG_AUIn = DEBUG && true;
	/** The file associated with this class */
	private String fileName;
	/** The input stream to read the file from */
	private FileInputStream fis;
	/** Parser */
	private StreamTokenizer st;
	/** have we reached the end of the audio file */
	public boolean fin = false;

	
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * A Constructor whose single argument is the name
	 * of the file to read data from. 
	 * @param fileName the name of the file to read.
	 */
	public TextIn(Instrument inst, String fileName, int sampleRate, int channels){
		super(inst, sampleRate, "[TextIn]");
		this.fileName = fileName;
		try{
			this.fis = new FileInputStream(this.fileName);
		}catch(IOException ioe){System.out.println(ioe);}
		//Reader r = new BufferedReader(new InputStreamReader(fis));
		//this.st = new StreamTokenizer(r);
		this.channels = channels;
	}
	
	//----------------------------------------------
	// Public methods
	//----------------------------------------------
	/**
	 * Read in a sample from the file and pass it down
	 * the audio chain. We need to have the switch statement
	 * so that we can read in multple bit sizes. The input to
	 * this method is bogus as it is always the first in the
	 * audio chain and therefore receives no input.
	 * @param input bogus input here to fit in.
	 */
	public int work(float[] buffer)throws AOException{
            int count=0;
            boolean loop = true;
            float tmp = (float)0.0;
            while(loop){
                try {
                    buffer[count++] = (float)fis.read();
                    if(count >= buffer.length)loop = false;
                }catch(IOException ioe){
                        ioe.printStackTrace();
                        System.exit(1);
                }
                /*
                // this can read text more intellignetly but ...
                try{
                    float sample = 0.0f;
                    switch(st.nextToken()){
                        case StreamTokenizer.TT_WORD:
                            //System.out.print("W");
                            //Do Anything ?
                            String str = st.sval;
                            for(int i=0; i< str.length(); i++) {
                                buffer[count++] = (float)Character.getNumericValue(str.charAt(i));
                                if(count >= buffer.length) {
                                    loop = false;
                                    break;
                                }
                            }
                            break;
                        case StreamTokenizer.TT_NUMBER:
                            //System.out.print("#");
                            buffer[count++] = (float)st.nval;
                            if(count >= buffer.length)loop = false;
                            break;
                        default:
                        //System.out.print("D");
                        //Do something ?
                        buffer[count++] = 0.0f;
                        if(count >= buffer.length)loop = false;
                        break;
                    }
                    //buffer[count++] = sample;
                }catch(EOFException eofe){
                        this.fin = true;
                        count = buffer.length;
                        break;
                        //this is supposed to happen ;)
                }catch(IOException ioe){
                        ioe.printStackTrace();
                        System.exit(1);
                }
                 */
            }
           
            return count;
	}
    
    public void finalize() {
        if (fis != null) {
            try {
                fis.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
}
