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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import jm.JMC;
import jm.audio.AOException;
import jm.audio.AudioObject;

/**
 * The <bl>SampleOut</bl> class is responsible for writing sample
 * data to disk. It combines all notes into a single Random
 * Access File based on each notes start time. The output of samples
 * into this file is in 32 bit floating point format and a maximum
 * amplitude for this file is calculated on the fly and storied by
 * this class for easy normalisation when being rewritten to a 
 * standard Audio File Format.<br><br>
 * NOTE: One important thing to note about the class is that all
 * the notes of the score are written to the same file. Therefore
 * everytime this class is called for each new note the information
 * is continually written to the same temporary file often combining
 * new data with existing data.<br><br>
 * The SampleOut class only excepts a Single Audio Object as input
 * and should therefore have all Audio Streams combined before
 * being passed to the <bl>SampleOut</bl>.
 * @author Andrew Sorensen 
 * @version 1.0,Sun Feb 25 18:42:42  2001
 */
public final class SampleOut extends AudioObject implements JMC{
	//----------------------------------------------
	// ATTRIBUTES
	//----------------------------------------------
	/** Do we want to sync data to disk */
	private boolean sync;
	/** Max from all SampleOut classes */
	public static float max = (float)0.0;
	/** num of channels from all SampleOut classes */
	public static int numofchan;
	/** sample rate from all SampleOut classes*/
	public static int samprate;
	/** The stream used for writing the samples out to disk. */
	private RandomAccessFile raf;
	/** the given position for any sample being written to RAF*/
	private int position = 0;
	/** Count is used to monitor where in the bos buffer we are up to */
	private int count;
	/** Used to hold a buffer of information going to the byte array */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	/** Used to pass data from the floats to bytes. */
	private DataOutputStream dos = new DataOutputStream(bos);
	/** Debug constant specific to AU file */
	private boolean DEBUG_AU = DEBUG && true;
	/** The file associated with this class */
	private String fileName;
	/** number of sound samples */
	private int size = 0;

	/** Buffer to hold sample data before being used by the write method */
	//private float[] buffer = new float[4096];
	/** counter for cycling around the buffer */
	//private int bufcnt = 0;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * Excepts a single Audio Object only.
	 * @param ao a single Audio Object for input.
	 */
	public SampleOut(AudioObject ao){
		super(ao, "[SampleOut]");
		this.sync = true;
		this.fileName = "jmusic.tmp";
		try{
			this.raf = new RandomAccessFile(this.fileName, "rw");
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}

	/**
	 * Excepts a single Audio Object only. The filename
	 * will be the name used to write the temporary combined
	 * floating point data to disk. This file should be the 
	 * same for all instances of this object.
	 * @param ao a single Audio Object for input.
	 * @param fileName to use for all data written to disk.
	 */
	public SampleOut(AudioObject ao, String fileName){
		super(ao, "[SampleOut]");
		this.sync = true;
		this.fileName = fileName;
		try{
			this.raf = new RandomAccessFile(this.fileName, "rw");
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}

	/**
	 * Excepts a single Audio Object only. The filename
	 * will be the name used to write the temporary combined
	 * floating point data to disk. This file should be the 
	 * same for all instances of this object.
	 * @param ao a single Audio Object for input.
	 * @param fileName to use for all data written to disk.
	 * @param sync do we want to sync all data to disk?
	 */
	public SampleOut(AudioObject ao, String fileName, boolean sync){
		super(ao, "[SampleOut]");
		this.sync = sync;
		this.fileName = fileName;
		try{
			this.raf = new RandomAccessFile(this.fileName, "rw");
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}

	/**
	 * Excepts a single Audio Object only. The filename
	 * will be the name used to write the temporary combined
	 * floating point data to disk. This file should be the 
	 * same for all instances of this object.
	 * @param ao a single Audio Object for input.
	 * @param fileName to use for all data written to disk.
	 * @param position shows where in the RAF to write to (in bytes).
	 * @param sync do we want to sync all data to disk?
	 */
	public SampleOut(AudioObject ao, String fileName, int position,
				boolean sync){
		super(ao, "[SampleOut]");
		this.sync = sync;
		this.position = position;
		this.fileName = fileName;
		try{
			this.raf = new RandomAccessFile(this.fileName, "rw");
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	public void finalize(){
		/*
	    try{
	        this.raf.close();
	    }catch(IOException ioe){
	        ioe.printStackTrace();
	    }
		*/
	}

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 * set the position to write to in the RAF for this
	 * note based on the notes startTime.
	 * @param note the note that this objects Audio Chain is currently rendering
	 * @param startTime a notes startTime which is changed into a RAF position.
	 */
	public void build(){
		//Sync any outstanding data to disk before building the next
		//note.  This may be required on some OS's if the OS has
		//trouble syncing buffer data to disk fast enough.
		if(sync){
			try{
				this.raf.getFD().sync();
			}catch(IOException ioe){ioe.printStackTrace();}
		}
		this.position = (int)(currentNoteStartTime*
					(double)this.sampleRate)*4*channels;
		if(this.position < 0)this.position=0;				
		SampleOut.samprate = this.sampleRate;
		SampleOut.numofchan = this.channels;
                this.finished = false;
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
                if(this.inst.iterations < 0) this.finished = true;
		int returned = this.previous[0].nextWork(buffer);
		for(int i=0;i<returned;i++){
			float sample = buffer[i];
			//Check for max here so that we don't have to check when in
			//the catch statement of the write method NOTE: max is static
			if(SampleOut.max < Math.abs(sample)){
				SampleOut.max = Math.abs(sample);
			}
			try{
				this.dos.writeFloat(sample);
			}catch(IOException ioe){
				throw new AOException(this.name,ioe.toString());
			}
			//if((++count%4096==0)||((i+1)==returned/*this.numOfSamples<=count)*/)){System.out.println("NUM: "+i);write(i);}
		}
		write(returned);
		return returned; 
	}

	//----------------------------------------------
	// Private Methods
	//----------------------------------------------
	/**
	 * The write method is used to write sample
	 * information from the buffer (bos) to 
	 * the RAF based on the samples current position
	 * withing the jm.Score.  All of this is handled
	 * by this class in general. Sorry about the mess
	 * that is this class. 
	 */
	private void write(int numof){		
		int numofloats = numof;
		ByteArrayInputStream bis = null;
		DataInputStream dis = null;
		ByteArrayInputStream bis2 = null;
		DataInputStream dis2 = null;
		//int numofbytes = bos.size();
		int numofbytes = numofloats*4;
		//int numofloats = (int)numofbytes/4;
		try{//if we can read from the file
			this.raf.seek(position);
			//NOTE!!!!!
			//This next readFloat line is here because
			//read(inarray) does not seem to cause
			//an EOFException 
			//The seek on the line after the readFloat()
			//is to put us back in the right spot again
			float f = raf.readFloat();
			this.raf.seek(position);
			byte[] inarray = new byte[numofbytes];
			int err = raf.read(inarray);
			
			bis = new ByteArrayInputStream(inarray);
			dis = new DataInputStream(bis);
			bis2 = new ByteArrayInputStream(bos.toByteArray());
			dis2 = new DataInputStream(bis2);
			bos.reset();
			float[] outsample = new float[numofloats];
			for(int i=0;i<numofloats;i++){
				outsample[i]=dis.readFloat()+dis2.readFloat();
				if(SampleOut.max < Math.abs(outsample[i])){
					SampleOut.max = Math.abs(outsample[i]);
				}
				dos.writeFloat(outsample[i]);
			}
			dos.flush();
			raf.seek(position);
			raf.write(bos.toByteArray());
			bos.reset();
			position+=numofbytes;
			dis.close();bis.close();dis2.close();bis2.close();
		}catch(EOFException eofe){
			try{
				raf.seek(position);
				raf.write(bos.toByteArray());
				bos.reset();
				position+=numofbytes;
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
}
