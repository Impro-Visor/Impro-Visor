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
import javax.sound.sampled.*;

/**
 * Real Time audio input
 * @author Andrew Sorensen 
 * @version 1.0,Sun Feb 25 18:42:41  2001
 */
public final class RTIn extends AudioObject{
	//----------------------------------------------
	// ATTRIBUTES
	//----------------------------------------------
	/** have we reached the end of the audio file */
	public boolean finished = false;
	/** the size of the holding buffer used by the TargetSource */
	private int bufsize;
	/** the audio input data source */
	private TargetDataLine dline;
	/** have we started running yet ? */
	private boolean started = false;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 */
	public RTIn(Instrument inst,int sampleRate, int channels, int bufsize){
		super(inst, sampleRate, "[RTIn]");
		this.sampleRate = sampleRate;
		this.channels = channels;
		this.bufsize = bufsize;
		this.init();
	}

	//----------------------------------------------
	// Public methods
	//----------------------------------------------
	/**
	 * @param input bogus input here to fit in.
	 */
	public int work(float[] buffer)throws AOException{
		if(!started){this.dline.start();started=true;}
		int ret = 0;
		int bc = 0; //byte counter
		int amount = buffer.length*2;
		byte[] data = new byte[amount];
		dline.read(data,0,amount);
		for(;ret<buffer.length;ret++){
			short input=(short)((data[bc++]<<8)+(data[bc++]));
			buffer[ret]=(float)((float)input/
						(float)Short.MAX_VALUE);
		}
		return ret;
	}

	public void init(){
		AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED
, (float)this.sampleRate,16, this.channels,this.channels*2, this.sampleRate,true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
                System.out.println("Setting for audio line: "+info);
                if(!AudioSystem.isLineSupported(info)){
                        System.out.println(info);
                        System.err.println("JMF Line not supported ... exiting .. sothere");
                        System.exit(1);
                }
                try{
                        this.dline = (TargetDataLine)AudioSystem.getLine(info);
                        //multiply buffersize by 2 because this is bytes not shorts
                        this.dline.open(af,bufsize*2);
                }catch(Exception e){e.printStackTrace();}
	}
}
