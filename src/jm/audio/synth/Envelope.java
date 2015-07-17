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
import java.io.EOFException;
import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;
import jm.JMC;

/**
 * Envelope which can be set with an arbitrary number of points
 * the envelope is constructed with linear lines between each 
 * specifed point.
 * The points excepted by this class are positioned as a percent
 * of the total length of the sound data being worked on and 
 * the envelope itself is constructed in the build() method.<br>
 * Envelope objects can be used as either Generator Audio Objects
 * (ie the first in the chain) or as processor Audio Objects (ie
 * in the centre or the chain) depending on the constructor used.<br>
 * As a generator the Envelope can be used to pass each envelope position
 * onto another Audio Object as input data.<br>
 * As a processor the Envelope object is used to change the Amplitude
 * of incoming samples to reflect the shape of the envelope.<br>
 * NOTE: The important distinction here is that when being used as
 * a processor object the envelopes only possible function is to 
 * alter amplitude. But when used as a generator the Envelope can
 * be used to send data to any AudioObjects input. (the volume of a
 * volume object for example for doing crescendos on each note)
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:47  2001
 */
public class Envelope extends AudioObject implements JMC{
	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	/** points on the graph */
	private EnvPoint[] graphPoints;
	/** a calculated graph with all points filled in */
	private float[] graphShape;
	/** how far through the envelope shape we are */
	private int position = 1;
	/** is the a primary object? */
	private boolean primary;
	/* Indicate waether of not to use values from the note */
	private boolean useNotePoints = false;
	/* The index in the note's breakPoint array to use.
	* See the Note class for specified constant values.
	*/
	 private int notePointIndex;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * An Envelope object can be used as a generator. This
	 * is a method to call to do this. 
	 * @param sampleRate the sampleRate for this AudioObject.
	 * @param graphPoints the points to construct the envelope from
	 */
	public Envelope(Instrument inst,int sampleRate, int channels, EnvPoint[] graphPoints){
		super(inst, sampleRate, "[Envelope]");
		this.channels = channels;
		this.graphPoints = graphPoints;
		this.primary = true;
	}
	
	/**
	 * An Envelope object can be used as a generator. This
	 * is a method to call to do this. 
	 * @param sampleRate the sampleRate for this AudioObject.
	 * @param @param breakPoints the data to construct the envelope from
	 */
	public Envelope(Instrument inst,int sampleRate, int channels, double[] breakPoints){
		super(inst, sampleRate, "[Envelope]");
		this.channels = channels;
		breakPointsToGraphPoints(breakPoints);
		this.primary = true;
	}

	/**
	* An Envelope object can be used as a generator. This
	 * is a method to call to do this.
	 * @param sampleRate the sampleRate for this AudioObject.
	 * @param notePointIndex The note's break point envelope values to use.
	 */
	public Envelope(Instrument inst,int sampleRate, int channels, int notePointIndex){
		super(inst, sampleRate, "[Envelope]");
		this.channels = channels;
		this.useNotePoints = true; // use note's values
		this.notePointIndex = notePointIndex;
		this.primary = true;
	}
	

	/**
	* This constructor takes a single AudioObject as input
	* and in this form becomes a processor object which
	* changes the amplitude of incoming samples based on
	* the envelope, it uses the note's amp env values.
	* @param ao the single AudioObject to use as input.
	*/
	public Envelope(AudioObject ao){
		super(ao, "[Envelope]");
		this.useNotePoints = true; // use note's values
		this.notePointIndex = Note.AMP_ENV;
		this.primary = false;
	}

	/**
	* This constructor takes a single AudioObject as input
	 * and in this form becomes a processor object which
	 * changes the amplitude of incoming samples based on
	 * the envelope, it uses the note's amp env values.
	 * @param ao the single AudioObject to use as input.
	 * @param notePointindex The index in the note's breakPoint array to use.
	 */
	public Envelope(AudioObject ao, int notePointIndex){
		super(ao, "[Envelope]");
		this.useNotePoints = true; // use note's values
		this.notePointIndex = notePointIndex;
		this.primary = false;
	}
	
	/**
	 * This constructor takes a single AudioObject as input
	 * and in this form becomes a processor object which
	 * changes the amplitude of incoming samples based on
	 * the envelope. 
	 * @param graphPoints the points to construct the envelope from
	 * @param ao the single AudioObject to use as input. 
	 */
	public Envelope(AudioObject ao, EnvPoint[] graphPoints){
		super(ao, "[Envelope]");
		this.graphPoints = graphPoints;
		this.primary = false;
	}
        
        /**
	 * This constructor takes a single AudioObject as input
	 * and in this form becomes a processor object which
	 * changes the amplitude of incoming samples based on
	 * the envelope. 
	 * @param breakPoints The data to construct the envelope from.
	 * @param ao the single AudioObject to use as input. 
	 */
	public Envelope(AudioObject ao, double[] breakPoints){
		super(ao, "[Envelope]");
		breakPointsToGraphPoints(breakPoints);
        this.primary = false;
	}
    
    /**
    * Update the envelope shape immediately.
     *@param breakPoints The data to construct the envelope from.
     */
    public void setBreakPoints(double[] breakPoints) {
        breakPointsToGraphPoints(breakPoints);
    }
	
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	* This method used to convert double value arrays into
	* a graphPoint array.
	*/
	private void breakPointsToGraphPoints(double[] breakPoints) {
		this.graphPoints = new EnvPoint[breakPoints.length /2];
		int counter = 0;
		for (int i = 0; i < (breakPoints.length / 2); i++) {
			graphPoints[i] = new EnvPoint((float)breakPoints[counter],
					(float)breakPoints[counter + 1]);
			counter += 2;
		}
	}
	
	/**
	 * Alter the samples value so that it meets the 
	 * shape of the graph, then send the new sample 
	 * onto the next audio object.<br>
	 * NOTE: if the nextWork method receives a value
	 * of 1.0 the graphs current positional value
	 * will be passed on unchanged.
	 * @param input input data 
	 */
	public int work(float[] buffer)throws AOException{
                // pass on data unchanged after the end of the envelope
		if(this.finished==true && this.inst.iterations<=0)return buffer.length;
                // process data
                if (primary) {
                //System.out.println("in primary. Graph size = " + graphShape.length + " Postion = " + position);
	  		int returned = buffer.length;
	  		int chancount=1;
	  		for(int i=0;i<returned;i++){
	  			try{
	  				buffer[i] = graphShape[this.position];
	  				//System.out.println("buffer = " + buffer[i]);
	  			}catch(ArrayIndexOutOfBoundsException aob){
	                              buffer[i] = 0.0f;
	  			}
	  			if(chancount==channels){
	  				chancount=1;
	  				this.position++;
	  				//System.out.println("Position incremeted to" + position);
	  			}else{chancount++;}
	  		}
	  		return returned;
	  	} else {
	  		//System.out.println("in NOT primary");
	  		int returned = this.previous[0].nextWork(buffer);
	  		int chancount=1;
	  		for(int i=0;i<returned;i++){
	  			try{
	  				buffer[i]=buffer[i] * graphShape[this.position];
	  			}catch(ArrayIndexOutOfBoundsException aob){
	  				//System.out.println("POS: "+this.position+"  LENGTH: "+graphShape.length);
	  				//System.exit(1);
	                              buffer[i] = 0.0f;
	  			}
	  			if(chancount==channels){
	  				chancount=1;
	  				this.position++;
	  			}else{chancount++;}
	  		}
	  		return returned;
		}
	}	

	//----------------------------------------------
	// Private Methods
	//----------------------------------------------
	/** 
	 * Calculates the sampleData for this Envelope
	 */
	public void build(){
		if (this.useNotePoints) 
      breakPointsToGraphPoints(this.currentNote.getBreakPoints(notePointIndex));
    //System.out.println("samps = " + numOfSamples + " Graph points = " + graphPoints.length);
    graphShape = new float[numOfSamples];
    // flat envelope for VERY short notes
		if(numOfSamples <= graphPoints.length * 4){
      for(int i=0; i<graphShape.length; i++) {
        graphShape[i] = 1.0f;
      }
      return;
		}
        // otherwise
		//Adjust the Envelope to suit the length of the waveform
		if(this.graphPoints[0].x != -1.0){
			for(int i=0; i<this.graphPoints.length; i++){
                this.graphPoints[i].X = 
                ((int)((float)(numOfSamples) * 
                       this.graphPoints[i].x));
				this.graphPoints[i].X -= 1;
			}
		}
		
		//Make the new wave shape
		//Calculate linear lines between EnvPoints
		//graphShape = new float[(numOfSamples)];
		int j = 0;
		for(int i=0;i<(this.graphPoints.length)-1;i++){
			float gradient = 
                (this.graphPoints[i].y - this.graphPoints[i+1].y) / 
                (this.graphPoints[i].X - this.graphPoints[i+1].X);
			float yintercept = this.graphPoints[i+1].y - 
				(gradient * this.graphPoints[i+1].X);
			for(;;){
				//This could be a problem as I don't think the last sample is
				//set properly.
				//moving it to other side of graphShape setting.
				//if(j == this.graphPoints[i+1].X) break;
				this.graphShape[j] = (gradient * (float)j) + yintercept;
                //System.out.println(this.graphShape[j] + " " + j);
                if(j >= this.graphPoints[i+1].X) break;
                j++;
			}
		}
        this.position = 0; //this is set to -1 to start channels at 0
        //System.out.println("START OF POS: "+this.position);
	}
}
