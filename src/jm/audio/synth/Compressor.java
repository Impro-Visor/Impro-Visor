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

import jm.audio.AudioObject;
import jm.audio.AOException;
import jm.music.data.Note;

/**
 * The <bl>Compressor</bl> Audio Object is a simple dynamic control. Any samples
 * that pass through the <bl>Compressor</bl> Object will have their amplitudes
 * above the thresghold adjusted by whatever compression ratio level is set. 
 * The <bl>Compresor</bl> can take one Audio Object as input. <P>
 * If ratio values are less than 1.0 (and greater than 0.0) this class will act
 * as an expander. Note however, thatcurrently expansion gain is linear although 
 * compression is logarithmic.
* @author Andrew Brown
 * @version 1.0, June 2003
 */

public final class Compressor extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** 
	 * A positive value above which the amplitude is attenuated.
	 */
	private float threshold = (float)1.0;
        
        /**
        * The attenuation value. 1.0 makes no change. 4.0 = 1:4 ratio.
        */
	private double ratio = 1.0;
        /** The amount of real gain reduction 
        * allowing for perceptual loudness effect */
        private float gainReduction = 1.0f;
        // compensate for compression volume reduction
        private float gain = 1.0f;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
        /**
        * The standard Compressor constructor takes a Single
        * Audio Object as input and sets a default threshold
        * and ratio.
        * @param ao The single AudioObject taken as input.
        */
	public Compressor(AudioObject ao){
	    this(ao, 0.5f);
	}

	/**
        * The standard Compressor constructor takes an
        * Audio Object and threshold value as input and 
        * sets a default ratio.
        * @param ao The single AudioObject taken as input.
        * @param thresh The value above which to compress.
        */
	public Compressor(AudioObject ao, double thresh){
	    this(ao, thresh, 2.0);
	}

	/**
        * The standard Compressor constructor takes a Single
        * Audio Object, threshold and ratio as inputs. 
        * @param ao The single AudioObject taken as input.
        * @param thresh The value above which to compress.
        * @param ratio The attenuation divisor value.
        */
	public Compressor(AudioObject ao, double thresh, double ratio){
        this(ao, thresh, ratio, 1.5);
    }
    
    /**
    * The standard Compressor constructor takes a Single
     * Audio Object, threshold and ratio as inputs. 
     * @param ao The single AudioObject taken as input.
     * @param thresh The value above which to compress.
     * @param ratio The attenuation divisor value.
     */
    public Compressor(AudioObject ao, double thresh, double ratio, double gain){
        
        super(ao, "[Compressor]");
        this.threshold = (float)thresh;
        this.ratio = ratio;
        this.gain = (float)gain;
        calcGainReduction();
    }
	

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	
	/**
	 */
	public void build(){
		//this.volume=(float)((1.0-(Math.log(128.0-
		//	(double)currentNote.getDynamic()))*0.2))*mainVolume;
	}
        
        // Reduces the raw ratio to a logrithmic reduction value
        // which compensates for the logarithmic nature of
        // loudness perception.
        private void calcGainReduction() {
            if (ratio == 1.0) {
                this.gainReduction = 1.0f;
            }
            else if (ratio > 1.0 ) {
                this.gainReduction = (float)Math.min(1.0, (Math.abs(Math.log(1.0 - 1.0/ratio) * 0.2 )));
            }
            else if (ratio > 0.0) { // ratio less than 1.0
                this.gainReduction = (float)(1.0 / ratio);
            }
            else {
                System.out.println("jMusic error: Compressor ratio values cannot be less than 0.0");
                System.exit(0);
            }
            
        }
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * The nextWork method for <bl>Compressor<bl> will divide
	 * the sample value above the threshold by the ratio. 
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
                for(int i=0;i<returned;i++){
                    // positive values
                    if(buffer[i] > threshold) {
                        buffer[i] = threshold + (buffer[i] - threshold) * this.gainReduction;
                    }
                    // negative values
                    if(buffer[i] < -threshold) {
                        buffer[i] = -threshold + (buffer[i] + threshold) * this.gainReduction;
                    }
                    buffer[i] *= gain;
                }
		return returned;
	}
        
        /**
        * Specify a new threshold value above which the audio will be compressed.
        * @param Thresh The value. 1.0 by default.
        */
        public void setThreshold(double thresh){
            this.threshold = (float)thresh;
        }
        
        /**
        * Specify a new ratio value - the amount of compression to apply.
        * @param ratio The ration value. 1.0 by default (no effect).
        */
        public void setRatio(double ratio){
            this.ratio = ratio;
            calcGainReduction();
            
        }
    
    /**
        * Specify a new ratio value - the amount of compression to apply.
     * @param ratio The ration value. 1.0 by default (no effect).
     */
    public void setGain(double gain){
        this.gain = (float)gain;        
    }
}
