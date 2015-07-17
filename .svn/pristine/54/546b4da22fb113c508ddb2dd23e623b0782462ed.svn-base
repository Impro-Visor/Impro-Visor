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
 * Chebyshev filters are a recursive or IIR (infinite impulse response) filter.  * They provide a fast mechanism for seperating frequencies using a
 * mathamatical process known as a z-transform.  This implementation is based
 * on the Chebyshev filter design in Steven W.Smith's excellent book 
 * "The Scientist and Engineer's Guide to Digital Signal Processing".
 * Band pass and resonant filters can be created by combining law and high pass filters.
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public final class Filter extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	public static final int LOW_PASS = 0, HIGH_PASS = 1;

	/** 
	 * The type of filter LOW_PASS or HIGH_PASS.
	 */
	private int type = LOW_PASS;
	/** The actual cutoff frequency for the filter */
	private double cutoff_frequency;
        /** The starting cutoff frequency for the filter 
         * from which filter modulation can deviate */
        private double initialCutoff;
	/** Cutoff frequency for the filter as a percentage of the sampling 
		rate*/
	private double cutoff_frq_percent;
	/** 
	 * The percent ripple to allow. The more ripple the faster the roll_off.
	 * 0.5 percent is a good standard to replicate analogue filter 
	 * performance 
	 */
	private double ripple = 0.5;
	/**
	 * The number of poles to allow in the filter. The more poles the faster
	 * the roll_off.  6.0 poles is a good standard to replicate analogue 
	 * filer performance.
	 */
	private double poles = 2.0;
	/** The filter coefficients calculated in coefficientCalc() */
	private double[] a = new double[22];
	/** Temporary nextWorking buffer for a */
	private double[] ta = new double[22];
	/** The filter coefficients calculated in coefficientCalc() */
	private double[] b = new double[22];
	/** Temporary nextWorking buffer for b */
	private double[] tb = new double[22];
	/** Input sample buffer */
	private double[][] xbuf;
	/** Output sample buffer */
	private double[][] ybuf;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
    /**
    * Create a new low pass filter audio object.
    * @param ao The previous Audio Object in the chain.
    * @param freq The low pass cutoff frequency. 
    */
	public Filter(final AudioObject ao, final double freq){
	    this(ao, freq, Filter.LOW_PASS, 0.5, 2.0);
	}
        
    /**
    * Create a new filter audio object of a specifiable type.
    * @param ao The previous Audio Object in the chain.
    * @param freq The low pass cutoff frequency. 
    * @param type The filter can be LOW_PASS or HIGH_PASS.
    */
	public Filter(final AudioObject ao, final double freq, final int type){
		this(ao, freq, type, 0.5, 2.0);
	}
        
    /**
    * Create a new filter audio object of a specifiable type and specification.
    * @param ao The previous Audio Object in the chain.
    * @param freq The low pass cutoff frequency. 
    * @param type The filter can be LOW_PASS or HIGH_PASS.
    * @param ripple The percentage of ripple to allow. 0.5 is usual.
    * @param poles The greater the number of poles the steeper the cutoff sloap.
    */
	public Filter(final AudioObject ao, final double freq, final int type, final double ripple,
					 double poles){
		super(ao, "[Filter]");
		this.type = type;
		this.cutoff_frequency = freq;
		this.ripple = ripple;
		this.poles = poles;
		if(this.poles > 20){
			System.err.println("More than 20 poles are not allowed (Sorry)");
			System.exit(1);
		}
	}
        
    /** Constructor to takes two audio objects, the second for cutoff modulation */
    public Filter(final AudioObject[] aoArray, final double freq, final int type){
		this(aoArray, freq, type, 0.5, 2.0);
	}
        
        /** Constructor to takes two audio objects, the second for cutoff modulation */
	public Filter(final AudioObject[] aoArray, final double freq, final int type,
            double ripple, double poles){
                super(aoArray,  "[Filter]");
		this.type = type;
		this.cutoff_frequency = freq;
                this.initialCutoff = freq;
		this.ripple = ripple;
		this.poles = poles;
		if(this.poles > 20){
			System.err.println("More than 20 poles are not allowed (Sorry)");
			System.exit(1);
		}
	}
	
	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 */
	public void build(){
		//Reset the input and output buffers for each new note?
		ybuf = new double[this.channels][22];
		xbuf = new double[this.channels][22];
		this.setCutOff(this.cutoff_frequency);
	}

	/**
	 * Print this filter's coefficients
	 */
	public void printCoefficients(){
	    for(int i=0;i<22;i++){
		    System.out.println("a["+i+"] "+a[i]+"    b["+i+"] "+b[i]);
	    }
	}

	/**
	 * Sets the cutoff frequency and adjusts the coefficients suitably
	 * @param freq the new cutoff frequency
	 */
	public void setCutOff(final double freq){
        this.cutoff_frequency = freq;
        if (freq <= 0f) {
                System.err.println("Filter error: You tried to use a "+
                    "cuttoff frequency of "+ freq +
                    " - woops! Frequency must be greater than zero. ");
                System.err.println("Exiting from Filter");
            System.exit(1);
        }
		//System.out.println("Freq = " + freq);
		if(freq > 0.5 * this.sampleRate){
                    System.err.println("Cutoff frequencies above the Nyquist"+
					" limit are BAD ;) SampleRate = " + sampleRate +
					" Frequency = " + freq);
		    System.err.println("Exiting from Filter");
                    System.exit(1);
		}
        this.cutoff_frq_percent = (1.0/this.sampleRate) * freq;
		this.coefficientCalc();
	}

        /**
        * Specify the filter pole variable. Updated immediatly.
        */
        public void setPoles(int newPole) {
            if(newPole < 0) newPole = 0;
            if(newPole > 20) newPole = 20;
            this.poles = newPole;
            setCutOff(this.cutoff_frequency);
        }
		
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
        * Processes the sample values.
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
        float[] cutoffs = null;
        if (this.previous.length > 1) { 
            cutoffs = new float[returned];
            this.previous[1].nextWork(cutoffs);
        }
		int i = 0;
		int chan = 0;
		for(;i<returned;i++){
            // reset the filter cutoff if necessary
            if (i%100 == 0 && this.previous.length > 1) { 
                setCutOff((double)cutoffs[i] + initialCutoff );
                //System.out.println("Cutoff = " + cutoffs[i] + " Initial = " + initialCutoff);
            }
			//Adjust the input buffer
			for(int j=(int)poles;j>0;j--){
				xbuf[chan][j] = xbuf[chan][j-1];
                    }
			xbuf[chan][0] = (double)buffer[i];
			//Adjust the output buffer
			for(int j=(int)poles;j>0;j--){
				ybuf[chan][j] = ybuf[chan][j-1]; 
			}
			//Zero input buffer
			ybuf[chan][0] = 0.0;
			//Convolve the output buffer input buffer and coefficients
			for(int j=0;j<poles+1;j++){
				ybuf[chan][0] += a[j]*xbuf[chan][j];
				if(j>0)ybuf[chan][0] += b[j]*ybuf[chan][j];
			}	
			//adjust the buffer to reflect the filters output
			buffer[i] = (float)(ybuf[chan][0]*1.0);
			//Make adjustments for multiple channels
			if(this.channels == ++chan){
				chan=0;
			}
		}
		return i;
	}

	/**
	 * Calculate the filter coefficients from a cascade of pole pairs.
	 */
	public void coefficientCalc(){
		for(int i=0;i<22;i++){
			a[i] = 0.0;
			b[i] = 0.0;
		}
		a[2] = 1.0;
		b[2] = 1.0;

		for(int p=1;p<=poles*0.5;p++){
			double[] nums = coefficientCalcSupport(p);
			for(int i=0;i<22;i++){
				ta[i] = a[i];
				tb[i] = b[i];
			}

			for(int i=2;i<22;i++){
				a[i] = (nums[0]*ta[i])+(nums[1]*ta[i-1])+(nums[2]*ta[i-2]);
				b[i] = tb[i]-(nums[3]*tb[i-1])-(nums[4]*tb[i-2]);
			}
		}
		b[2]=0.0;
		for(int i=0;i<20;i++){
			a[i] = a[i+2];
			b[i] = -b[i+2];
		}
		double sa = 0.0;
		double sb = 0.0;
		for(int i=0;i<20;i++){
			if(type==LOW_PASS)sa = sa + a[i];
			if(type==LOW_PASS)sb = sb + b[i];
			if(type==HIGH_PASS)sa = sa + (a[i]*Math.pow(-1.0,(double)i));
			if(type==HIGH_PASS)sb = sb + (b[i]*Math.pow(-1.0,(double)i));
		}
		double gain = sa / (1-sb);
		for(int i=0;i<20;i++){
			a[i] = a[i]/gain;
		}
	}

	/**
	 * Calculate the coefficients for 1 pair of poles
	 */
	private double[] coefficientCalcSupport(final int p){
		double[] returns = new double[5];
		double RP = -Math.cos(Math.PI / (poles * 2.0) + ((double)(p-1)) * Math.PI/poles);
		double IP = Math.sin(Math.PI / (poles * 2.0) + ((double)(p-1)) * Math.PI/poles);

		if(ripple != 0.0){
			double ES = Math.sqrt(Math.pow((100.0 / (100.0-ripple)),2)-1);
			double VX = (1 / poles) * Math.log((1.0 / ES)+
						Math.sqrt((1 / (ES*ES))+1));
			double KX = (1 / poles) * Math.log((1.0 / ES)+
						Math.sqrt((1 / (ES*ES))-1));
			KX = (Math.exp(KX) + Math.exp(-KX))*0.5;
			RP = RP * ((Math.exp(VX)-Math.exp(-VX))*0.5)/KX;
			IP = IP * ((Math.exp(VX)+Math.exp(-VX))*0.5)/KX;
		}
		double T = 2 * Math.tan(0.5);
		double W = 2 * Math.PI * cutoff_frq_percent;
		double M = (RP*RP) + (IP*IP);
		double D = 4 - 4 * RP * T + M * (T*T);
		double X0 = (T*T)/D;
		double X1 = 2*X0;
		double X2 = X0;
		double Y1 = (8-2*M*(T*T))/D;
		double Y2 = (-4-4*RP*T-M*(T*T))/D;

		double K = 0.0;
		if(type == HIGH_PASS)K=-Math.cos((W*0.5)+0.5)/Math.cos((W*0.5)-0.5);
		if(type == LOW_PASS)K=Math.sin(0.5-(W*0.5))/Math.sin(0.5+(W*0.5));
		D = 1+Y1*K-Y2*(K*K);
		returns[0] = (X0-X1*K+X2*(K*K))/D;
		returns[1] = (-2*X0*K+X1+X1*(K*K)-2*X2*K)/D;
		returns[2] = (X0*(K*K)-X1*K+X2)/D;
		returns[3] = (2*K+Y1+Y1*(K*K)-2*Y2*K)/D;
		returns[4] = (-(K*K)-Y1*K+Y2)/D;

		if(type == HIGH_PASS)returns[1]=-returns[1];
		if(type == HIGH_PASS)returns[3]=-returns[3];

		return returns;
	}
}
