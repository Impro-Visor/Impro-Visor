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



import jm.audio.AOException;

import jm.audio.AudioObject;

import jm.audio.Instrument;



/**

 * Wavetable lookup creates an efficient means for resampling data

 * into any frequency. It is particularly useful for holding simple

 * wave information such as sinewaves and is often used as an

 * oscillator.<br>

 * This WaveTable implementation can accept either one or two inputs.  Two

 * inputs expects that amplitude is the left input and frequency is the right

 * input. One input allows the user to specify whether the input is for

 * amplitude or frequency by setting the aoDestination variable((0)amplitude

 * (1)frequency.<br>

 * A WaveTable can use fixed variables for both amplitude and frequency.  The

 * default is to use a default amplitude of 1.0 and a frequency based upon the

 * value of the build methods Note.pitch(). These fixed variables are

 * amp(amplitude) and frq(frequency) and both have set methods. NOTE (please do

 * not add more constructors to set amp and frq but leave them as set

 * methods).<br> 

 * The frqRatio variable is used to produce a frequency which is a ratio

 * against the current notesFrq (the build methods note.getFrequency()) variable 

 * setting.<br>

 * It is common to use WaveTables as oscillators and even more common for these

 * oscillators to based on simple wave forms.  Simple wave forms in jMusic can

 * be retrieved using static method calls to the Oscillator class.<br>

 * 

 * @author Andrew Sorensen

 * @version 1.0,Sun Feb 25 18:42:52  2001

 */

public class WaveTable extends AudioObject{

	//----------------------------------------------

	// Attributes

	//----------------------------------------------

	/** this contains the wavetable data as samples */

	private float[] waveTable;

	/** how many samples to we skip while passing through the wavetable */

	private float si;

	/** what is the phase of the wavetable to start at */

	private float phase;

	/** If we have one audio object input is at amp(0) or freq(1) ? */

	private int aoDestination;

	/** Value to use as a fixed amplitude for the waveTable.*/

	private float amp = (float) 1.0;

	/** Value to use as a fixed frequency for the waveTable.*/

	private float frq = (float)-1.0;

	/** Frequency ratio allows an incoming note's pitch to be adjusted to a

 	 * fixed ratio amount*/

	private float frqRatio = (float)1.0;

        /** constant for use with aoDestination */

        public static final int AMPLITUDE = 0;

        /** constant for use with aoDestination */

        public static final int FREQUENCY = 1;

        /** constant for use with channels */

        public static final int MONO = 1;

        /** constant for use with channels */

        public static final int STEREO = 2;

	//----------------------------------------------

	// Constructors

	//----------------------------------------------

	/**

	 * This constructor sets the WaveTable to act as

	 * a processor object taking in two inputs. Input

 	 * one is defined as amplitude and input two is

 	 * defined as frequency. 

	 * @param ao AudioObject as input 

	 * @param waveTable the lookup table data

	 * @exception AOException thrown when two many inputs are attached

	 */

	public WaveTable(AudioObject[] ao, float[] waveTable)throws AOException{

		super(ao, "[WaveTable]");

		if(ao.length > 2) throw new AOException(this.name,1);

		this.waveTable = waveTable;

	}



	/**

 	 * This constructor sets the WaveTable to act as

	 * a processor object taking in one input. That

	 * input can be either amplitude(0) or frequency(1)

	 * and is defined by the aoDestination variable (int).

         * @param ao the one input audio object

	 * @param wavetable the wave table data

 	 * @param aoDestination Is this input amplitude(0) or frequency(1)

	 */

	public WaveTable(AudioObject ao, float[] waveTable, int aoDestination){

		super(ao, "[WaveTable]");

		this.waveTable = waveTable;

		this.aoDestination = aoDestination;

	}



	/**

 	 * This constructor sets the WaveTable to act as

	 * a generator object taking in one input. That

	 * input can be either amplitude(0) or frequency(1)

	 * and is defined by the aoDestination variable (int).

	 * @param inst the parent instrument (usually "this")

	 * @param wavetable the wave table data

 	 * @param aoDestination Is this input amplitude(0) or frequency(1)

	 * @param val is used to set a fixed frequency or amplitude based on the

	 * result of aoDestination (aoDestination=1 for example will set a fixed frequency)

	 */

	public WaveTable(Instrument inst, int sampleRate, float[] waveTable, 

            int channels, int aoDestination, float val){

		super(inst, sampleRate, "[WaveTable]");

		this.waveTable = waveTable;

                this.channels = channels;

		this.aoDestination = aoDestination;

		if(aoDestination == 1){

			this.frq=val;

		}else{

			this.amp=val;

		}

	}



	/**

	 * This constructor sets this wavetable up as a generator

	 * object meaning that it will pass sample information 

	 * down the chain based on its wave table data.<br>

	 * Set WaveTable with some initial values including 

	 * the sampling rate and the samples to use for this

	 * wave table

	 * @param inst the parent instrument (usually "this")

	 * @param sampleRate the sampling rate

	 * @param waveTable the wave table data

	 * @param channels the number of channels to use

	 */

	public WaveTable(Instrument inst, int sampleRate, float[] waveTable,

				int channels){

		super(inst, sampleRate, "[WaveTable]");

		this.waveTable = waveTable;

		this.channels = channels;

	}		



	//----------------------------------------------

	// Methods

	//----------------------------------------------

	/**

	 * Moves through the WaveTable array (noramally forwards but sometimes

	 * backwards) by increments set by si (sample increment value).  This nextWork

	 * method can take one or two inputs which are either amplitude, frequency

	 * or both (a single input can be assigned to either frequency or amplitude

	 * by assigning the aoDestination value to either (0)Amp or (1)Frq in the

	 * appropriate constructor.  A WaveTable that takes two inputs expects the

	 * first input to be amplitude and the second input to be frequency. 

	 * @param buffer The sample buffer.

	 */

	public int work(float[] buffer)throws AOException{

		//because wavetable contains mono sample data we need to pass the same

		//sample information to as many channels as are present.

		int buffneed=buffer.length/channels;

		int ret=0; //the number of samples to return 



		if(inputs==2){ //Amp and Freq

			float[] ampbuf = new float[buffneed];

			int returned = this.previous[0].nextWork(ampbuf);

			float[] freqbuf = new float[returned];

			if(returned != this.previous[1].work(freqbuf)){

				throw new AOException(this.name,0);

			}

			for(int i=0;ret<buffer.length;i++){

				setSI((int)freqbuf[i]);

				if(phase < 0){

					phase = this.waveTable.length+phase;

				} // amplitude values assumed to be between -1 and 1

				float sample = waveTable[(int)phase]*(this.amp * ampbuf[i]);

				this.phase += si;

				if(phase >= this.waveTable.length){ 

					phase -= this.waveTable.length;

				}

				for(int j=0;j<channels;j++){ 

					buffer[ret++] = sample;

				}

			}

		}else if(inputs==1 && aoDestination==0){ //Amp only

			float[] ampbuf = new float[buffneed];

			int returned = this.previous[0].nextWork(ampbuf);

			for(int i=0;ret<buffer.length;i++){

				float sample = waveTable[(int)phase]*(this.amp * ampbuf[i]);

				this.phase += si;

				if(phase >= this.waveTable.length){ 

					phase -= this.waveTable.length;

				}

				for(int j=0;j<channels;j++){ 

					buffer[ret++] = sample;

				}

			}

		}else if(inputs==1 && aoDestination==1){ //Frq only

			float[] frqbuf = new float[buffneed];

			int returned = this.previous[0].work(frqbuf);

			for(int i=0;i<buffneed;i++){

				setSI((int)frqbuf[i]);

				if(phase < 0){

					phase = this.waveTable.length+phase;

				}

				float sample = waveTable[(int)phase]*this.amp;

				this.phase += si;

				if(phase >= this.waveTable.length){

					phase -= this.waveTable.length;

				}

				for(int j=0;j<channels;j++){

					buffer[ret++] = sample;

				}

			}

		}else{ //no inputs

			for(;ret<buffer.length;){

				float sample = waveTable[(int)phase]*this.amp;			

				this.phase += si;

				if(phase >= this.waveTable.length){ 

					phase -= this.waveTable.length;

				}

				for(int j=0;j<channels;j++){ 

					try{

						buffer[ret++] = sample;

					}catch(ArrayIndexOutOfBoundsException e){

						//This can happen if a non mono signal chain wants

						//to access the wavetable as a mono signal

						//Ignore and skip over

						//

						//We do need to remove one back off ret though to return

						//the right number of samples to return

						ret--;

					}

				}

			}

		}

		return ret; 

	}



	/**

	 */

	public void build(){

		float notesFrq = (float)currentNote.getFrequency() * frqRatio;

		if(this.frq<(float)0.0){

			this.setSI(notesFrq); 	

		}else{

			this.setSI(this.frq);

		}	

	}



	/**

	 * Set the fixed amp of this wavetable

	 * @param amp Fixed value amplitude

	 */

	public void setAmp(float amp){

		this.amp = amp;

	}



	/**

	 * Set the fixed Frequecy of this wavetable

	 * @param frq Fixed value frequency

	 */

	public void setFrq(float frq){

		this.frq = frq;

	}



	/**

	 * Sets the frequency ratio to alter a notes pitch by

	 * @param frqRatio Fixed ratio value to change frequency by

	 */

	public void setFrqRatio(float frqRatio){

		this.frqRatio = frqRatio;

	}



	//------------------------------------------

	// Protected Methods

	//------------------------------------------

	/**

	 * Returns the sampling increment which is used

	 * to nextWork out how many samples in the wavetable

	 * skip on each pass.

	 * @param frequency the frequency used to find si

	 */

	protected void setSI(float frequency){

	    this.si = (frequency / (float)this.sampleRate) * (float)this.waveTable.length;

	}

}

