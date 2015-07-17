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
package jm.audio;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import jm.music.data.Note;

/**
 * Audio Object is the super class of all audio reading, writing, processing
 * and generating units.  An AudioObject in the jMusic audio architecture
 * is any class which is used to form part of an audio chain, be that a
 * generator object (such as a wavetable), a reader object (such as an audio
 * file reader), a processor object (such as a filter), or a writer object (
 * such as a file writer).<br><br>
 * Audio chains are created by linking together AudioObjects into lists
 * where every AudioObject knows which AudioObjects come before and after it.
 * This link is achieved by passing an AudioObjects immediate predicesors in 
 * to its constructor as an array of AudioObjects.  The number of inputs
 * accepted by an AudioObject is a direct relation to the size of the
 * AudioObject array passed to an AudioObject's constructor.  AudioObjects also
 * know who their successor is so that information (such as sample rate) 
 * can be deceminated from the top to the bottom of the audio chain.<br><br>
 *
 * The chain works using a pull mechanism where the <b>final</b> AudioObject 
 * (defined in an Instrument object as the <i>finalAO</i> object) requests a
 * number of samples from it's previous AudioObject(s).  This request is then
 * passed on up the chain until the requested number of samples is retrieved 
 * from each chains first AudioObject (defined in an Instrument as an array of
 * <i>primaryAO</i> objects).  These requests for samples are made by passing
 * a float[] to each AudioObjects work() method and requesting that it fill the
 * float[] with sample data (if it is a primary AudioObject) or process that
 * float[] in some manner.
 * 
 * @author Andrew Sorensen 
 * @version 1.0,Sun Feb 25 18:42:44  2001
 */
public abstract class AudioObject implements jm.JMC{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** the AudioObject previous to this one */
	protected AudioObject[] previous;
	/** the AudioObject following to this one */
	protected AudioObject[] next;
	/** The name of this Audio Object. */
	protected String name;
	/** This audio objects sampling rate */
	protected int sampleRate;
	/** The number of audio channels for this audio object. */
	protected int channels;
	/** how many inputs are attached to this object */
	protected int inputs=0;
	/** the note currently being rendered */
	protected Note currentNote = null;
	/** the currentNotes startTime */
	protected double currentNoteStartTime;
	/** the number of samples which need to be processed for this note
	 * to be rendered (in mono, for stereo multiply by 2 etc.)
	 */
	protected int numOfSamples = 0;
	/** The Instrument which currently implements this AudioObject */
	protected Instrument inst = null;
    /* Is this audio object finished */
    protected boolean finished = true;
    /* Local buffer counter */
    private int returned;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------	
	/**
	 * This constructor is used for all audio processors and writers
	 * that need to accept input from a single stream (ie Audio Object).
	 * @param previous The AudioObject previous to this one
	 * @param name A name to associate with this Audio Object
	 */
	protected AudioObject(AudioObject previous, String name){
		AudioObject[] tmp = {previous};
		this.name = name;
		this.previous = tmp;
		this.previous[0].setNext(this);
		this.inputs=1;
	}

	/**
	 * This constructor is used for all audio processors and writers
	 * that need to accept input from multiple streams (ie Audio Objects).
	 * @param previous The AudioObjects previous to this one
	 * @param name A name to associate with this Audio Object.
	 */
	protected AudioObject(AudioObject[] previous, String name){
		this.name = name;
		this.previous = previous;
		for(int i=0;i<previous.length;i++){
			previous[i].setNext(this);
		}
		inputs=previous.length;
	}
		
	/**
	 * This constructor is used for generator and reader audio
	 * units where no previous audio objects exist in the stream.
	 * This constructor sets the sampleRate which 
	 * will then propogate through to all following Audio Objects
	 * when their build methods are called.  Any AudioObjects that
  	 * make use of this constructor should be defined as primaryAO
 	 * objects in the Instrument createChain() method.
	 * @param sampleRate the sample rate to use for all objects
	 * @param name A name to associate with this Audio Object
	 */
	protected AudioObject(Instrument inst, int sampleRate, String name){
		this.inst = inst;
		this.name = name;
		this.sampleRate = sampleRate;
		this.inst.addPrimaryAO(this);
	}

	//----------------------------------------------
	// Abstract Methods
	//----------------------------------------------
	/**
 	 * The work method is responsible for handling the processing done
 	 * by an AudioObject.  This processing maybe as trivial as reading 
 	 * samples from an audio file or as complex as performing real-time 
     * fft transformations.  The work method accepts an empty float[] as
 	 * input and is responsible for filling and/or processing the samples
	 * contained in the array.  The size of the array is defined in the
	 * Instrument class and is generally set to 4096.<br><br> 
	 * @throws AOException 
	 */
	public abstract int work(float[] buffer)throws AOException;

	//----------------------------------------------
	// Private Methods
	//----------------------------------------------
	/**
	 * The setNext method is called from the constructor
	 * to set the next variable for the previous object
	 * in the object chain (essentially this creates a 
	 * double linked list so every object in the chain
	 * knows which objects came before and which are
	 * to follow.
	 * @param next The audio object which follows this Audio Object.
	 */
	private void setNext(AudioObject next){
            if(this.next == null){ 
                    this.next = new AudioObject[1];
                    this.next[0] = next;
            }else{
                    AudioObject[] tmp = new AudioObject[this.next.length+1];
                    for(int i=0;i<this.next.length;i++)tmp[i]=this.next[i];
                    tmp[this.next.length]=next;
                    this.next = tmp;
            }
 	}

	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 *
	 */
	public int nextWork(float[] buffer)throws AOException{
            returned = 0;
            returned = this.work(buffer);
            //if(!this.finished)System.out.println(this.name);
            this.inst.setFinished(this.finished);
            return returned;
	}

	/**
	 * The buildNext method decimates information down
	 * through the audio chain passing data such as 
	 * sampleRate, number channels, number of samples
	 * required etc.
	 * The buildNext method is also responsible for 
	 * calling each audio objects build method which
	 * provides any additional initialization that the
	 * object may require.
	 * The buildNext method should be called by the 
	 * first audio object in the audio chain to initialise
	 * all following audio objects.
	 * @param Note The note this chain is currently processing. 
	 * @param startTime The startTime of the note.
	 * @param int The number of samples which need to be processed.
	 */
	protected void buildNext(final Note note, final double startTime, final int numOfSamples){
            if(next != null){
                    for(int i=0;i<next.length;i++){
                            next[i].numOfSamples = numOfSamples;
                            next[i].inst = this.inst;
                            next[i].channels = this.channels;
                            next[i].sampleRate = this.sampleRate;
                            next[i].newNote(note, startTime, numOfSamples);
                    }
            }else{
                    try{
                            this.inst.setFinalAO(this);
                    }catch(AOException aoe){
                            System.out.println(aoe);
                            System.exit(1);
                    }
            }
	}

	/**
	 * The build method can be overriden by Audio Objects
	 * to provide any extra initialization that may be
	 * required before the Audio Object can be used for
	 * each new note that needs rendering. The AudioObject parameters
	 * currentNote, currentNoteStartTime and numOfSamples will provide
	 * any note specific information that the build method may need to 
	 * utilise.
	 */
  	protected void build(){
		//default build method does nothing.
	}

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 * The newNote method is called everytime a new note is passed to the
	 * AudioObject.  newNote sets a number of AudioObject parameters which
	 * are produced based on the new note information.
	 * @param note sets the parameter currentNote
	 * @param startTime sets the parameter currentNoteStartTime
	 * @param numOfSamples sets the parameter numOfSamples
	 */
	public void newNote(Note note, double startTime, int numOfSamples){
		this.currentNote = note;
		this.currentNoteStartTime = startTime;
		this.numOfSamples = numOfSamples;
		this.build();
		this.buildNext(this.currentNote,this.currentNoteStartTime,
				this.numOfSamples);
	}
	
	/**
	 * getSampleRate returns an Audio Objects samples rate
	 * @return An audio objects sampleRate.
	 */
	public int getSampleRate(){
		return this.sampleRate;
	}
        
        /**
	 *  Returns the number of channels in this Audio Object
	 * @return An audio object's channels.
	 */
	public int getChannels(){
		return this.channels;
	}
}
