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
import jm.audio.AOException;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.music.data.Note;

/**
 * The Oscillator class can generate steady tones with
 * various wave shapes, inlcuding sine, cosine, triangle, sawtooth,
 * pulse wave, square, and more.
 * The Oscillator class can be used as a primary object at the start of
 * an audio chain, or as a object within the chain who's frequency or
 * amplitude is modulatoed by an earlier object.
 * @author Andrew Sorensen and Andrew Brown
 * @version 1.0,Sun Feb 25 18:42:52  2001
 */
public class Oscillator extends AudioObject{
	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	/** The constant that represents the sine wave form */
	public static final int SINE_WAVE = 0;
	/** The constant that represents the cosine wave form */
	public static final int COSINE_WAVE = 1;
	/** The constant that represents the triangle wave form */
	public static final int TRIANGLE_WAVE = 2;
	/** The constant that represents the square wave form */
	public static final int SQUARE_WAVE = 3;
	/** The constant that represents the sawtooth wave form */
	public static final int SAWTOOTH_WAVE = 4;
	/** The constant that represents the inverse sawtooth wave form */
	public static final int SAWDOWN_WAVE = 5;
	/** The constant that represents the exponential sawtooth wave form */
	public static final int SABERSAW_WAVE = 6;
	/** The constant that represents the sine wave form.
	* The pulse width can be set using setPulseWidth() method.
	*/
	public static final int PULSE_WAVE = 7;    
	/* modulation sources */
	/** Use the modulation source to change the amplitude of this oscillator */
	public static final int AMPLITUDE = 0;
	/** Use the modulation source to change the frequency of this oscillator */
	public static final int FREQUENCY = 1;
	/** how many samples to we skip while passing through the Oscillator */
	private float si;
	/** what is the phase of the Oscillator to start at */
	private float phase;
	/** If we have one input is at amp(0) or freq(1) ? */
	private int choice;
	/** Value to use as a fixed amplitude for the Oscillator.*/
	private float amp = (float) 1.0;
	/** Value to use as a fixed frequency for the Oscillator.*/
	private float frq = (float)-1.0;
	/** Frequency ratio allows an incoming note's pitch to be adjusted to a
	* fixed ratio amount*/
	private float frqRatio = (float)1.0;
	/** which waveform to use */
	private int waveType = SINE_WAVE;
	/** The width of the positive part of the pulse wave */
	private double pulseWidth = 0.15;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	* This constructor sets the Oscillator to act as
	* a processor object taking in two inputs. Input
	* one is defined as amplitude and input two is
	* defined as frequency. 
	* @param two AudioObjecs as input 
	* @exception AOException thrown when two many inputs are attached
	*/
	public Oscillator(AudioObject[] ao)throws AOException{
		super(ao, "[Oscillator]");
		if(ao.length > 2) throw new AOException(this.name,1);
	}

	/**
	* This constructor sets the Oscillator to act as
	* a processor object taking in one input. That
	* input can be either amplitude(0) or frequency(1)
	* and is defined by the choice variable (int).
	* @param ao the one input audio object
	* @param waveType the type of timbre to generate
	* @param choice Is this input amplitude(0) or frequency(1)
	*/
	public Oscillator(AudioObject ao, int waveType, int choice){
		super(ao, "[Oscillator]");
		this.waveType = waveType;
		this.choice = choice;
	}

	/**
	* This constructor sets the Oscillator to act as
	* a processor object taking in one input. That
	* input can be either amplitude(0) or frequency(1)
	* and is defined by the choice variable (int).
	* @param ao the one input audio object
	* @param waveType the type of timbre to generate
	* @param choice Is this input amplitude(0) or frequency(1)
	* @param val is used to set a fixed frequency or amplitude based 
        * 		on the result of choice 
	* 		(choice=0 for example will set a fixed frequency)
	*/
	public Oscillator(AudioObject ao, int waveType, int choice, double val){
		super(ao, "[Oscillator]");
        this.waveType = waveType;
		this.choice = choice;
		if(choice == 1){
			this.frq= (float)val;
		}else{
			this.amp= (float)val;
		}
	}
	
	/**
	 * This constructor sets this Oscillator up as a generator
	 * using default parameters
	 * @param Instrument the instance this is associated with 'this'
	 */
	public Oscillator(Instrument inst){
		this(inst, SINE_WAVE);
	}
	
	/**
	 * This constructor sets this Oscillator up as a generator
	 * specifying the type of waveform to use
	 * @param Instrument the instance this is associated with 'this'
	 * @param waveType an integer or constant sepcifying the noise type
	 */
	public Oscillator(Instrument inst, int waveType){
		this(inst, waveType, 44100);
	}
	
	/**
	 * This constructor sets this Oscillator up as a generator
	 * specifying the type of waveform and sample rate to use
	 * @param Instrument the instance this is associated with 'this'
	 * @param waveType an integer or constant sepcifying the noise type
	 * @param sampleRate an int that sets the sample rate in samples per second
	 */
	public Oscillator(Instrument inst, int waveType, int sampleRate){
		this(inst, waveType, sampleRate, 1);
	}
	
	/**
	 * This constructor sets this Oscillator up as a generator
	 * specifying the type of waveform and sample rate to use
	 * @param Instrument the instance this is associated with 'this'
	 * @param waveType an integer or constant sepcifying the noise type
	 * @param sampleRate an int that sets the sample rate in samples per second
	 * @param cahannels 1 for mono 2 for stereo etc.
	 */
	public Oscillator(Instrument inst, int waveType, int sampleRate, int channels){
		super(inst, sampleRate, "[Oscillator]");
                this.waveType = waveType;
		this.channels = channels;
	}		
    
	/**
	* This constructor sets this Oscillator up as a generator
	* specifying the type of wavetable and ferquency
	* @param Instrument the instance this is associated with 'this'
	* @param waveType an integer or constant sepcifying the noise type
	* @param sampleRate an int that sets the sample rate in samples per second
	* @param cahannels 1 for mono 2 for stereo etc.
	* @param fixedModChoice Is this input amplitude(0) or frequency(1)
	* @param freqVal is used to set a fixed frequency or amplitude based on the
	* result of choice (choice=0 for example will set a fixed frequency)
	*/
	public Oscillator(Instrument inst, int waveType, int sampleRate, int channels, int fixedModChoice, double freqVal){
		super(inst, sampleRate, "[Oscillator]");
		this.waveType = waveType;
		this.channels = channels;
		this.choice = fixedModChoice;
		if(choice == 1){
			this.frq= (float)freqVal;
		}else{
			this.amp= (float)freqVal;
		}              
	}	

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * Moves through the Oscillator array (noramally forwards but sometimes
	 * backwards) by increments set by si (sample increment value).  This nextWork
	 * method can take one or two inputs which are either amplitude, frequency
	 * or both (a single input can be assigned to either frequency or amplitude
	 * by assigning the choice value to either (0)Amp or (1)Frq in the
	 * appropriate constructor.  A Oscillator that takes two inputs expects the
	 * first input to be amplitude and the second input to be frequency. 
	 * @param buffer The sample buffer.
	 */
	public int work(float[] buffer)throws AOException{
		//because Oscillator contains mono sample data we need to pass the same
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
				setSI(freqbuf[i] * frqRatio); // FM
				float sample = getWaveSample()* this.amp * ampbuf[i]; // AM
				for(int j=0;j<channels;j++){ 
					buffer[ret++] = sample;
				}
			}
		}else if(inputs==1 && choice==AMPLITUDE){ //Amp only
			float[] ampbuf = new float[buffneed];
			int returned = this.previous[0].nextWork(ampbuf);
			for(int i=0;ret<buffer.length;i++){
				float sample = getWaveSample()*this.amp * ampbuf[i];	
				for(int j=0;j<channels;j++){ 
					buffer[ret++] = sample;
				}
			}
		}else if(inputs==1 && choice == FREQUENCY){ //Frq only
			//System.out.println("Frq only");
			float[] frqbuf = new float[buffneed];
			int returned = this.previous[0].work(frqbuf);
			for(int i=0;i<buffneed;i++){
				setSI(frqbuf[i] * frqRatio);
				float sample = getWaveSample()* this.amp;
				for(int j=0;j<channels;j++){
					buffer[ret++] = sample;
				}
			}
		}else{ //no inputs
                    //System.out.println("no inputs");
                    for(;ret<buffer.length;){
                        if(choice == FREQUENCY) setSI(this.frq);
                        float sample = getWaveSample() * this.amp;//Oscillator[(int)phase]*this.amp;			
                        for(int j=0;j<channels;j++){ 
                            try{
                                    buffer[ret++] = sample;
                            }catch(ArrayIndexOutOfBoundsException e){
                                //This can happen if a non mono signal chain wants
                                //to access the Oscillator as a mono signal
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
	    //this.numOfSamples = numOfSamples;  
        //System.out.println("Oscillator: NumOfSample = " + numOfSamples);
	    if(this.frq < (float)0.0){
			// get pitch
			float notesFrq = (float)currentNote.getFrequency();
			// adjust for ratio setting
			notesFrq *= frqRatio;
			this.setSI(notesFrq);
	    }else{
			// adjust for ratio setting
			this.frq *= frqRatio;
			this.setSI(this.frq);
	    }	
	}
    
    /**
	 * Set the parameter of this Oscillator to accept the fixed value
	 * @param choiceVal  0 = fixed amplitude, 1 = fixed frequency
	 */
	public void setChoice(int choiceVal){
		this.choice = choiceVal;
	}

	/**
	 * Set the fixed amp of this Oscillator
	 * @param amp Fixed value amplitude
	 */
	public void setAmp(float amp){
		this.amp = amp;
		this.choice = AMPLITUDE;
	}
	
	/**
	* Get the fixed amp of this Oscillator
	*/
	public float getAmp(){
		return this.amp;
	}

	/**
	 * Set the fixed Frequecy of this Oscillator
	 * @param frq Fixed value frequency
	 */
	public void setFrq(float frq){
		this.frq = frq;
		this.choice = FREQUENCY;
	}

	/**
	 * Sets the frequency ratio to alter a notes pitch by
	 * @param frqRatio Fixed ratio value to change frequency by
	 */
	public void setFrqRatio(double frqRatio){
		this.frqRatio = (float)frqRatio;
	}

	//------------------------------------------
	// Protected Methods
	//------------------------------------------
	/**
	 * Returns the sampling increment which is used
	 * to nextWork out how many samples in the Oscillator
	 * skip on each pass.
	 * @param frequency the frequency used to find si
	 */
	protected void setSI(double frequency){
	    // Revoved error check to allow more felxability in FM synthesis
	    /*if(frequency <= 0f) {
                System.err.println("Oscillator error: You tried to use a frequency less than zero - woops!");
                System.exit(1);
            } */
	    this.si = 2.0f*(float)Math.PI/((float)this.sampleRate/(float)frequency);
	}

	/**
	 * Returns a sample from any of the following waveforms
	 */
	protected float getWaveSample(){
		switch(waveType){
		case SINE_WAVE:
			if(phase < 0){
				phase += 2.0f*(float)Math.PI;
			}
			float sample=(float)Math.sin((double)(phase+(2.0f*(float)Math.PI)));
			phase += si;
			if(phase >= (2.0f*(float)Math.PI)){
				phase -= 2.0f*(float)Math.PI;
			}
			return sample;
              case COSINE_WAVE:
			if(phase < 0){
				phase += 2.0f*(float)Math.PI;
			}
			sample=(float)Math.cos((double)(phase+(2.0f*(float)Math.PI)));
			phase += si;
			if(phase >= (2.0f*(float)Math.PI)){
				phase -= 2.0f*(float)Math.PI;
			}
			return sample;
                
                case TRIANGLE_WAVE:
                        sample = 0f;
                        if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                        float position = 0.5f / (float)Math.PI * phase;
                        if (position <= 0.25f) {
                            sample = (float)(position * 4.0);
                        }
                        if (position > 0.25f && position <= 0.75f) {
                            sample = (float)(4.0 * (0.5 - position));
                        }
                        if (position > 0.75f) {
                            sample = (float)((position - 1.0) * 4.0);
                        }
                        phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
                case SQUARE_WAVE:
                        sample = 0f;
                        if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                        position = 0.5f / (float)Math.PI * phase;
                        if (position < 0.5f) {
                            sample = (float)(1.0);
                        } else sample = (float)(-1.0);
                        phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
                case SAWTOOTH_WAVE:
                         if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                         position = (float)(1.0/ Math.PI) * phase;
                         sample = (float)(position - 1.0);
                         phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
                case SAWDOWN_WAVE:
                         if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                         position = (float)(1.0/ Math.PI) * phase;
                         sample = (float)(1.0 - position);
                         phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
                case SABERSAW_WAVE:
                         if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                         position = (float)(0.5 / Math.PI) * phase;
                         sample = (float)Math.exp(position) - 2.0f;
                         //System.out.println("Position = " + position + " Sample = " + sample);
                         phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
                case PULSE_WAVE:
                        sample = 0f;
                        if(phase < 0){
                            phase += 2.0f*(float)Math.PI;
                        }
                        position =  0.5f / (float)Math.PI * phase;
                        if (position < (float)pulseWidth) {
                            sample = (float)(1.0);
                        } else sample = (float)(-1.0);
                        phase += si;
                        if(phase >= (2.0f*(float)Math.PI)){
                            phase -= 2.0f*(float)Math.PI;
                        }
                        return sample;
		default:
			System.err.println("Incorrect oscillator type selected.");
			System.exit(1);
			return 0f;
		}
	}
        
    /**
        * Specify the positive proportion of the pulse wave
        * @param width A value between 0.0 and 1.0
        */
    public void setPulseWidth(double width) {
        if(width >= 0.0 && width <= 1.0) {
            this.pulseWidth = width;
        } else System.err.println("Pulse wide must be between 0.0 and 1.0");
    }
    
    /**
        * Specify the initial phase of the waveform
        * @param phase The phase in radians (between 0.0 and 2 * PI)
        */
    public void setPhase(double phase) {
            this.phase = (float)phase;
    }
        
}
