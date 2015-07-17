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
 * of the total length of the sound data being nextWorked on and 
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
 * @author Andrew Sorensen, Andrew Brown, Joel Joslin
 * @version 1.0,Sun Feb 25 18:42:47  2001
 */
public class ADSR extends AudioObject implements JMC{
	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	/** points on the graph */
	private EnvPoint[] graphPoints;
	/** a calculated graph with all points filled in */
	private float[] graphShape;
	/** is the a primary object? */
	private boolean primary;
	// ADSR values
	private int attack, decay, release;
	private double sustain;
    // the number of samples that takes into account the release time
    private int totalSamples;
    // keep track of the number of samples processed
    private int sampleCounter = 0;
    // keep track of the location along the envelope array
    private int position = 0;
    // keep the number of samples for attack, decay and release
    private double attackSamps, decaySamps, releaseSamps;
    // the previous rhythmValue
    private double prevRV = 0.0;
    // variables for samples per section
    int maxAttackCount;
    int maxDecayCount;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	
    /**
    * An ADSR object can be used as a generator. This
    * is a method to call to do this. 
    * @param The instrumt to use - Usually put 'this' here.
    * @param sampleRate the sampleRate for this AudioObject.
    * @param channels The number of tracks for this file (1 = mono , 2 = stereo)
    * @param attack The number of milliseconds for the attack portion of the envelope
    * @param decay The number of milliseconds for the decay portion of the envelope
    * @param sustain The percentage lavel for the sustain portion of the envelope (0.0 - 1.0)
    * @param release The number of milliseconds for the release portion of the envelope
    */
	public ADSR(Instrument inst, int sampleRate, int channels,
             int attack, int decay, double sustain, int release){
            super(inst, sampleRate, "[ADSR]");
            this.channels = channels;
            this.attack = attack;
            this.decay = decay;
            this.sustain = sustain;
            this.release = release;            
            this.primary = true;
			this.finished = false;
            calcSamps();
	}
	
    /**
    * This constructor takes a single AudioObject as input
    * and in this form becomes a processor object which
    * changes the amplitude of incoming samples based on
    * the envelope. 
    * @param The instrumt to use - Usually put 'this' here.
    * @param sampleRate the sampleRate for this AudioObject.
    * @param channels The number of tracks for this file (1 = mono , 2 = stereo)
    * @param attack The number of milliseconds for the attack portion of the envelope
    * @param decay The number of milliseconds for the decay portion of the envelope
    * @param sustain The percentage lavel for the sustain portion of the envelope (0.0 - 1.0)
    * @param release The number of milliseconds for the release portion of the envelope
    */
	public ADSR(AudioObject ao, int attack, int decay, double sustain, int release){
            super(ao, "[ADSR]");
            // ADSR -> points
            this.attack = attack;
            this.decay = decay;
            this.sustain = sustain;
            this.release = release;
            this.primary = false;
			this.finished = false;
	}
        
       
	
    //----------------------------------------------
    // Protected Methods
    //----------------------------------------------
    /**
    * Alter the samples value so that it meets the 
    * shape of the graph, then send the new sample 
    * onto the next audio object.<br>
    * NOTE: if the nextWork method receives a value
    * of 1.0 the graphs current poitional value
    * will be passed on unchanged.
    * @param input input data 
    */
    public int work(float[] buffer)throws AOException{
        // claculate the number of samples processed
        if (sampleCounter > totalSamples) {
            this.finished = true;
        }
        
                
        // process data
        if (primary) {
            int returned = buffer.length;
            //int chancount=1;
            for(int i=0;i<returned;i+=channels){
                for(int j=0; j<channels; j++) {
                    try{
                        buffer[i+j] = graphShape[this.position];
                        //System.out.println("buffer = " + buffer[i]);
                    }catch(ArrayIndexOutOfBoundsException aob){
                        buffer[i+j] = 0.0f;
                    }
                }
                this.position++;
            }
            sampleCounter += buffer.length;
            return returned;
        } else {
            //System.out.println("in NOT primary");
            //
            int returned = this.previous[0].nextWork(buffer);
            for(int i=0;i<buffer.length;i+=channels){
                for(int j=0; j<channels; j++) {
                    try{
                        if(sampleCounter < maxAttackCount) //attack
                            buffer[i+j] = buffer[i+j] * (float)(sampleCounter * 1.0 / maxAttackCount);
                        else if (sampleCounter < maxDecayCount + maxAttackCount) //decay
                            buffer[i+j] = buffer[i+j] * (float)(1.0 - (sampleCounter - maxAttackCount) * (1.0 - sustain) / maxDecayCount);
                        else if (sampleCounter < this.numOfSamples) //sustain
                            buffer[i+j] = buffer[i+j] * (float)sustain;
                        else if(sampleCounter < totalSamples) // release
                            buffer[i+j] = buffer[i+j] * (float)(sustain - (sampleCounter - numOfSamples) * sustain / releaseSamps);
                        else buffer[i+j] = 0.0f;
                        //buffer[i+j] = buffer[i+j] * graphShape[this.position];
                    }catch(ArrayIndexOutOfBoundsException aob){
                        buffer[i+j] = 0.0f;
                    }
                }
                sampleCounter++;
                this.position++;
            }            
            //
            /*
            int returned = this.previous[0].nextWork(buffer);
            int chancount=1;
            for(int i=0;i<buffer.length;i+=channels){
                for(int j=0; j<channels; j++) {
                    try{
                        buffer[i+j] = buffer[i+j] * graphShape[this.position];
                    }catch(ArrayIndexOutOfBoundsException aob){
                        buffer[i+j] = 0.0f;
                    }
                }
                this.position++;
            }
             sampleCounter += buffer.length;
             */
            return returned;
        }
    }	


	//----------------------------------------------
	// Private Methods
	//----------------------------------------------
        
         private void calcSamps() {
            this.attackSamps = getSamps(this.attack);
            this.decaySamps = getSamps(this.decay);
            this.releaseSamps = getSamps(this.release);            
        }
        
	private double getSamps(int milli){
	    return ((double)milli/1000.0) * (double)sampleRate;
	}
	
	/** 
	 * Calculates the sampleData for this Envelope
	 */
	public void build(){
		//this.inst.finishedNewData = false;
		sampleCounter = 0;
		this.position = 0;
		if(numOfSamples == 0){
			return;
		}
		// avoid recalc?
		if (currentNote.getRhythmValue() == prevRV) return;

		calcSamps();
		// extend note to account for release
		// note; numOfSamples is in mono
		totalSamples = this.numOfSamples + (int)releaseSamps;

		graphShape = new float[totalSamples];
		// Attack
        maxAttackCount = Math.min((int)attackSamps, this.numOfSamples);
        //if (primary) {
            double inc = 1.0 / (double)maxAttackCount;
            for(int i=0; i< maxAttackCount; i++) {
                graphShape[i] = (float)(inc * i);
            }
        //}
		// decay
        maxDecayCount = maxAttackCount;
		if (sustain < 1.0) {
			maxDecayCount = Math.min((int)attackSamps+(int)decaySamps, this.numOfSamples);
            //if(primary) {
                double diff = (1.0 - sustain)/(double)(maxDecayCount - maxAttackCount);
                for(int i=maxAttackCount; i< maxDecayCount; i++) {
                    graphShape[i] = (float)(1.0 - diff * (i - maxAttackCount));
                }
            //}
		}
		// sustain
        //if (primary) {
            for(int i=maxDecayCount; i < this.numOfSamples; i++) {
                graphShape[i] = (float)(sustain);
            }
        //}
		// release
        //if (primary) {
            double startVal = (double)graphShape[this.numOfSamples - 1];
            double inc2 = startVal/releaseSamps;
            for(int i=this.numOfSamples; i < totalSamples; i++) {
                graphShape[i] = (float)(startVal - inc2 * (i - this.numOfSamples));
            }
        //}
		this.finished = false;
	}
}
