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
 * The Noise class contains various noise waveform generators, 
 * incluidng white noise and fractal noise.
 * @author Andrew Brown
 * @version 1.0,Sun Feb 25 18:42:52  2001
 */
public class Noise extends AudioObject{
    //----------------------------------------------
    // Attributes
    //----------------------------------------------
    /** 
     * A variable to choose different noise properties
     * 0 = white noise
     * 1 = low resolution (and frequency) noise
     * 2 = smoothed noise
     * 3 = brown noise
     * 4 = fractal noise
     * 5 = gaussian noise
     * 6 = random walk noise
     * 7 = gendyn noise (after Xenakis' stochastic synthesis)
     */
    private int noiseType = 0;
    private int noiseDensity = 10;
    private float amp = 1.0f;
    // for fractal math
    private static float sum;
    private static float[] rg = new float[16];
    private static int k, kg, ng, threshold;
    private static int np = 1;
    private static int nbits = 1;
    private static int numbPoints = 48000; //number of notes
    private static float nr = (float)(numbPoints);
    private static float result;
    private static int counter = 0;
    // for gaussian noise
    private double standardDeviation = 0.25;
    private double mean = 0.0;
    // for walk noise
    /** Current sample value of the random walk algorithm */
    private float walkLastValue = 0.0f;
    /** How large a jump from the current sample value is allowable */
    private float walkStepSize = 0.3f;
    /** The largest value a sample value can be in the random walk algorithm. */
    private float walkMax = 1.0f;
    /** The smallest value a sample value can be in the random walk algorithm. */
    private float walkMin = -1.0f;
    /** The number of steps of the same value before changing. Realted to frequency range of sound. */
    private int walkNoiseDensity = 500;
    /** Maintains a count of the number of samples processed */
    private long walkDensityCounter = 0;
    /** Will the denisty value vary as a random walk or remain stable. */
    private boolean walkVaryDensity = true;
    /** The smallest number of repeated sample values (quantise) for the walk density. */
    private int walkNoiseDensityMin = 1; // one or larger.
    /** The largest number of repeated sample values (quantise) for the walk density. */
    private int walkNoiseDensityMax = 1500; // 1 or larger.
    /** The size of the noise density change at each iteration. */
    private int walkNoiseDensityStepSize = 100;
    
    // gendyn noise variables
    private java.util.Random RandomGenerator = new java.util.Random();
    private int gendynAmpGranularity = 128;//50; // more or less quantisation noise
    private double gendynPrevTime = 50;
    private int gendynTimeMirror = 80;
    private int gendynAmpMirror = 80;
    private int tempAmpMirror;
    private boolean ampMirrorUpdate = false;
    /** The starting sample value for the wave */
    private int gendynPointSize = 4;
    private boolean pointSizeReset = false;
    private int newPointSize;
    private double[] gendynAmpArray = new double[gendynPointSize];
    private double[] gendynTimeArray = new double[gendynPointSize];
    private double gendynAmp0 = 0.0;
    private int gendynIntArray[];
    private double gendynIntArrayLength;
    private int gendynIntArrayCounter = 0;
    private double gendynTimeStepSize = 10.0;
    private double maxGendynTimeStepSize = 100.0; // 75 // Xenakis would have this at 100
    private double gendynAmpStepSize = 10.0; 
    private double maxGendynAmpStepSize = 100.0; // 10 // Xenakis would have this at 100
    private int mirrorMax = 100;
    /** Use a gaussian or normal random distribution */
    private boolean gendynGaussian = false;
    private double gendynPrimaryTimeStepSize = 10.0; 
    private double gendynPrimaryAmpStepSize = 10.0; //gendynAmpGranularity/10;
    private int gendynPrimaryTimeMirror = 100;
    private int gendynPrimaryAmpMirror = 100;
    private int gendynInterpolation = 1; // linear = 1, coside = 2, square = 3
    private boolean gendynGranularityUpdate = false;
    private int tempGendynGranularity;
    
        
    /** Different noise type constants */
    public static final int WHITE_NOISE = 0,
        STEP_NOISE = 1,
        SMOOTH_NOISE = 2,
        BROWN_NOISE = 3,
        FRACTAL_NOISE = 4,
        GAUSSIAN_NOISE = 5,
        WALK_NOISE = 6,
        GENDYN_NOISE = 7;
		
    //----------------------------------------------
    // Constructors
    //----------------------------------------------
    /**
     * Default constructor
     * @param Instrument the class instance 'this'
     */
    public Noise(Instrument inst){
        this(inst, WHITE_NOISE);
    }
	
    /**
     * This constructor sets this object up as a noise generator
     * allowing you to specify the type of noise
     * @param Instrument the class instance 'this'
     * @param sampleRate the sampling rate
     */
    public Noise(Instrument inst, int noiseType){
        this(inst, noiseType, 44100);
    }
	
    /**
     * This constructor sets this object up as a noise generator
     * allowing you to specify the type of noise and sample rate
     * @param Instrument the class instance 'this'
     * @param sampleRate the sampling rate
     * @param noiseType the flavour of noise to use
     */
    public Noise(Instrument inst, int noiseType, int sampleRate){
        this(inst, noiseType, sampleRate, 1);
    }
	
    /**
     * This constructor sets this object up as a noise generator
     * with all parameters
     * @param Instrument the class instance 'this'
     * @param sampleRate the sampling rate
     * @param noiseType the flavour of noise to use
     * @param channels the number of channels to use
     */
    public Noise(Instrument inst, int noiseType, int sampleRate, int channels){
        super(inst, sampleRate, "[WaveTable]");
        this.noiseType = noiseType;
        this.channels = channels;
        // setup math for fractal noise
        if (noiseType == FRACTAL_NOISE) setUpFractalMath();
        // setup array for gendyn noise
        if (noiseType == GENDYN_NOISE) makeGendynArray();
        for (int i=0; i<gendynPointSize; i++) {
            gendynAmpArray[i] = 50;
        }
    }

    /**
     * Set the fixed amp of this Noise instance
     * @param amp Fixed value amplitude
     */
    public void setAmp(float amp){
        this.amp = amp;
    }

    /**
     * Get the fixed amp of this Noise instance
     */
    public float getAmp(){
        return this.amp;
    }
	
	
    private void setUpFractalMath() {
        // setup math for fractal noise
        nr = nr/2;
    
        while (nr > 1) {
            nbits++;
            np = 2 * np;
            nr = nr/2;
        }
    
        for(kg=0; kg<nbits; kg++) {
            rg[kg] = (float)(Math.random());
        }
    }

    //----------------------------------------------
    // Methods
    //----------------------------------------------
    /**
     * Returns a random sample value to each channel 
     * @param buffer The sample buffer.
     */
    
    private float gnSampleVal;
    private int gnj;
    
    public int work(float[] buffer)throws AOException{
        int ret=0; //the number of samples to return
        // run the appropiate code for the chosen noise type
        //System.out.println("noise = " + noiseType);
        switch(this.noiseType){
        case WHITE_NOISE: 
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){ 
                    buffer[ret++] = (float)(Math.random()*2.0 - 1.0) * amp;
                }
            };
            break;
        case BROWN_NOISE: 
            float prev0 = 0.0f;
            float prev1 = 0.0f;
            float prev2 = 0.0f;
            float brownValue, current;
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){ 
                    current = (float)(Math.random()*2.0 - 1.0) * amp;
                    brownValue = (prev0 + prev1 + prev2  + current) / 4.0f;
                    buffer[ret++] = brownValue;
                    // update values
                    prev0 = prev1;
                    prev1 = prev2;
                    prev2 = current;
                }
            };
            break;
        case STEP_NOISE:
            // low sample resolution noise (RandH noise)
            // has greater energy in the low frequency spectrum
            int density = this.noiseDensity;
            float temp = (float)(Math.random()*2.0 - 1.0) * amp;
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){
                    if (ret % density == 0) temp = 
                        (float)(Math.random()*2.0 - 1.0) * amp;
                    buffer[ret++] = temp;
                }
            };
            break;
        case SMOOTH_NOISE:
            // interpolated noise (RandI noise)
            // has an even greater emphasis on low frrquency energy
            density = this.noiseDensity;
            temp = (float)(Math.random()*2.0 - 1.0) * amp;
            float temp2 = (float)(Math.random()*2.0 - 1.0) * amp;
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){
                    if ((ret + 1) % density == 0) {
                        buffer[ret++] = temp2;
                        temp = temp2;
                        temp2 = (float)(Math.random()*2.0 - 1.0) * amp;
                    } else {
                        buffer[ret++] = temp + 
                            ((temp2 - temp) / density * (ret % density));
                    }
                }
            };
            break;
        case FRACTAL_NOISE:
            for(;ret < buffer.length;){
                for(int j=0;j<channels;j++){ 
                    if (counter%noiseDensity == 0) { //recalculate
                        threshold = np;
                        ng = nbits;
                        while(k%threshold != 0) {
                            ng--;
                            threshold = threshold / 2;
                        }
                        sum = 0;
                        for(kg=0; kg<nbits; kg++) {
                            if(kg<ng) {rg[kg]=(float)(Math.random());}
                            sum += rg[kg];
                        }
                        result = (float)(((sum/nbits) - 0.17) * 2.85 - 1.0);
                        if(result > 1.0) result = (float)1.0;
                        else if(result < -1.0) result = (float)-1.0;
                    }
                    counter++;
                    buffer[ret++] = result * amp;
                }
                if (counter > 67000) counter = 0;      
            }
            break;
        case GAUSSIAN_NOISE:
            java.util.Random RNG = new java.util.Random();
            float gaussValue;
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){ 
                    gaussValue = (float)(RNG.nextGaussian() *
                                         standardDeviation + mean);
                    if (gaussValue < -1.0f) gaussValue = -1.0f;
                    else if (gaussValue > 1.0f) gaussValue = 1.0f;
                    buffer[ret++] = gaussValue * amp;
                }
            };
            break;
                
        case WALK_NOISE:
            for(;ret<buffer.length;){
                for(int j=0;j<channels;j++){
                    buffer[ret++] = walkLastValue;
                    walkDensityCounter ++;
                    if ((int)walkDensityCounter%walkNoiseDensity == 0) {
                        // update value
                        walkLastValue += (float)(Math.random()) * walkStepSize * 2.0f - walkStepSize;
                        while (walkLastValue > walkMax || walkLastValue < walkMin) {
                            if (walkLastValue > walkMax) walkLastValue -= (walkLastValue - walkMax) * 2.0f;
                            if (walkLastValue < walkMin) walkLastValue += (walkMin - walkLastValue) * 2.0f;
                        }
                        // vary the denisty value if required
                        if (walkVaryDensity) {
                            // a random walk of the denisty (root frequency)
                            walkNoiseDensity += (int)(Math.random() * 
                                                      walkNoiseDensityStepSize * 2.0 - walkNoiseDensityStepSize);
                            if (walkNoiseDensity < walkNoiseDensityMin) walkNoiseDensity = walkNoiseDensityMin;
                            else if (walkNoiseDensity > walkNoiseDensityMax) walkNoiseDensity = walkNoiseDensityMax;
                            //System.out.println("change density to " + walkNoiseDensity);
                        }
                    }
                }
            }
            break;
        case GENDYN_NOISE:
            gnSampleVal = 0.0f;
            for(;ret<buffer.length;){
                // System.out.println("array size " + gendynIntArray.length);
                gnSampleVal = (gendynIntArray[gendynIntArrayCounter] / (float)gendynAmpGranularity - 0.5f) * 2.0f;
                
                // clipping limter
                if (gnSampleVal > 1.0) gnSampleVal = 1.0f;
                else if (gnSampleVal < -1.0) gnSampleVal = -1.0f;
    
                for(gnj=0;gnj<channels;gnj++){
                    // add next value from array
                    buffer[ret++] = gnSampleVal;
                }
                gendynIntArrayCounter ++;
                if(gendynIntArrayCounter >= (int)gendynIntArrayLength) makeGendynArray();
            }
            break;
        default:
            System.err.println(this.name+"jMusic error: Noise type " 
                               + noiseType + " not supported yet.");
            System.exit(1);
        }
            
        return ret; 
    }
        
    private int mgaCounter;
    private double mgaInc;
    private int index, jindex;
    
    private void makeGendynArray() {
        // calculate new step sizes - second level change Xenakis had
        gendynTimeStepSize = randWalk(gendynTimeStepSize, 
                                      gendynPrimaryTimeStepSize, 
                                      gendynPrimaryTimeMirror, true);
        if (Math.abs(gendynTimeStepSize) > maxGendynTimeStepSize)
            gendynTimeStepSize = maxGendynTimeStepSize;
        gendynAmpStepSize = randWalk(gendynAmpStepSize, 
                                     gendynPrimaryAmpStepSize, 
                                     gendynPrimaryAmpMirror, false);
        if (Math.abs(gendynAmpStepSize) > maxGendynAmpStepSize)
            gendynAmpStepSize = maxGendynAmpStepSize;
            
        // calulate new break point values
        //System.out.print("t1-");
        for (index = 0; index<gendynPointSize; index++) {
            gendynTimeArray[index] = Math.abs(randWalk(gendynTimeArray[index], 
                                                   gendynTimeStepSize, 
                                                   gendynTimeMirror, true));
            // prevent zero time
            if(gendynTimeArray[index] < 1.0) gendynTimeArray[index] = 1.0;
            
            gendynAmpArray[index] = randWalk(gendynAmpArray[index], 
                                         gendynAmpStepSize, 
                                         gendynAmpMirror / 2 + 51, false);
        }
        //System.out.println(gendynTimeArray[0] + " " + gendynAmpArray[0] + " " +
        //                   gendynTimeArray[1] + " " + gendynAmpArray[1] + " " +
        //                   gendynTimeArray[2] + " " + gendynAmpArray[2] );
        //
        // interpolate int array
        //
        gendynIntArrayLength = 0;
        for (index = 0; index<gendynPointSize; index++) {
            gendynIntArrayLength += gendynTimeArray[index];
        }
        //System.out.println("length = " + gendynIntArrayLength);
        gendynIntArray = new int[(int)gendynIntArrayLength];
        mgaCounter = 0;
        // stage 1
        mgaInc = (gendynAmpArray[0] - gendynAmp0) / (double)gendynTimeArray[0];
        //first - continuing from previous
        for(jindex=0; jindex<(int)gendynTimeArray[0]; jindex++) {
            switch(gendynInterpolation) {
            case 2: 
                // cos
                double diff = (1.0 - (Math.cos(jindex / (double)gendynTimeArray[0] * 3.14) / 2.0 + 0.5)) * 
                    (gendynAmpArray[0] - gendynAmp0);
                gendynIntArray[mgaCounter++] = (int)((gendynAmp0 + diff) / 
                                                     100.0 * gendynAmpGranularity);
                break;
            case 1:                   
                // linear
                gendynIntArray[mgaCounter++] = (int)((gendynAmp0 + mgaInc * jindex) / 
                                                     100.0 * gendynAmpGranularity);
                break;
            case 3:
                // square
                gendynIntArray[mgaCounter++] = (int)(gendynAmp0 / 100.0 * gendynAmpGranularity);
            }
        }
        // remainder
        for (index = 1; index<gendynPointSize -1; index++) {
            mgaInc = (gendynAmpArray[index] - gendynAmpArray[index-1]) / (double)gendynTimeArray[index];
            for(jindex = 0; jindex<(int)gendynTimeArray[index]; jindex++) {
                switch(gendynInterpolation) {
                case 2: 
                    double diff = (1.0 - (Math.cos(jindex / (double)gendynTimeArray[index] * 3.14) / 2.0 + 0.5)) * 
                        (gendynAmpArray[index] - gendynAmpArray[index-1]);
                    gendynIntArray[mgaCounter++] = (int)((gendynAmpArray[index-1] + diff) / 
                                                         100.0 * gendynAmpGranularity);
                    break;
                case 1:
                    // linear
                    //System.out.println("gendynPointSize = " + gendynPointSize + " gendynIntArray = " + gendynIntArray.length + " mgaCounter = " + mgaCounter);
                    gendynIntArray[mgaCounter++] = (int)((gendynAmpArray[index-1] + mgaInc * jindex) / 
                                                         100.0 * gendynAmpGranularity);
                    break;
                case 3:
                    // square
                    gendynIntArray[mgaCounter++] = (int)(gendynAmpArray[index-1] / 
                                                         100.0 * gendynAmpGranularity);
                }
            }
        }
        // keep last value to start next wave
        gendynAmp0 = gendynAmpArray[gendynPointSize - 1];
        // reset counter
        gendynIntArrayCounter = 0;
        // update numberof points?
        if (pointSizeReset) resetPointSize();
        // upadage granularity?
        if (gendynGranularityUpdate) updateGranularity();
    }
	
    // the random walk function for gendyn noise
    private double rwNewVal;
        
    private double randWalk(final double prevVal, final double stepSize, final int mirror, final boolean timeWalk) {
        rwNewVal = 0;
        if(gendynGaussian) {
            rwNewVal = prevVal + (RandomGenerator.nextGaussian() * stepSize);
        } else rwNewVal = prevVal + (RandomGenerator.nextDouble() * stepSize * 2.0 - stepSize);
        // keep inside range
        if(timeWalk) {
            if (stepSize == 0) {
                rwNewVal = prevVal;
            } else {
                while (rwNewVal > mirror || rwNewVal < 0.0) {
                    if (rwNewVal > mirror) rwNewVal = mirror - (rwNewVal - mirror);
                    if (rwNewVal < 0.0) rwNewVal = (rwNewVal/2.0 * -1.0);
                }
            }
            // avoid less than (1 or) zero ????
            if (rwNewVal < 0.0) rwNewVal = 0.0;
        } else {
            int negMirror = mirrorMax - mirror;
            while (rwNewVal > mirror || rwNewVal < negMirror) {
                //System.out.println("." + " mirror = " + mirror + " newVal = " + newVal);
                if (rwNewVal > mirror) rwNewVal = mirror - (rwNewVal - mirror);
                if (rwNewVal < negMirror) rwNewVal = negMirror + (negMirror - rwNewVal);
            }
            // avoid zero ????
            if (rwNewVal < 0.0) rwNewVal = 0.0;
        }
            
        //System.out.println("Prev = " + prevVal + " stepSize = " + stepSize + " mirror = " + mirror + " newVal = " + newVal);
        return rwNewVal;
    }
        
    /**
     * Specify the number of samples to set the same in 
     * the low and high noise wave forms.
     * The greater the value the less high frequency spectrum
     * will be in the LOW and SMOOTH noise types.
     */
    public void setNoiseDensity(int newDensity) {
        this.noiseDensity = newDensity;
    }
        
    /**
     * Specify the standard deviation for gaussian noise.
     * The dafault for this in 0.25.
     */
    public void setStandardDeviation(double newValue) {
        this.standardDeviation = newValue;
    }
        
    /**
     * Specify the mean for gaussian noise.
     * The dafault for this in 0.0.
     */
    public void setMean(double newValue) {
        this.mean = newValue;
    }
        
    /**
     * Specify the maximum step size for same changes from
     * value to value. Value greater than 0.0
     * @param val The new step size value.
     */
    public void setWalkStepSize(double val) {
        if(val > 0) walkStepSize = (float)val;
        else System.err.println("Walk step size must be greater than zero.");
    }
        
    /**
     * Specify the maximum value for sample values.
     * Controls dynamic range. Values greater than 0.0 only.
     * @param val The new maximum sample value.
     */
    public void setWalkMax(double val) {
        if(val > 0) walkMax = (float)val;
        else System.err.println("Walk maximum value must be greater than zero.");
    }
        
    /**
     * Specify the minimum value for sample values.
     * Controls dynamic range. Values less than 0.0 only.
     * @param val The new maximum sample value.
     */
    public void setWalkMin(double val) {
        if(val < 0) walkMin = (float)val;
        else System.err.println("Walk minimum value must be less than zero.");
    }
        
    /**
     * Specify the maximum number of times the one sample value is repeat - quantise.
     * Controls frequency range. Values greater than 0.0 only.
     * If the walkVaryDenisty is set to flase, thios value will be absolute, rather than maximum.
     * @param val The new maximum repeats of each sample value.
     */
    public void setWalkNoiseDensity(int val) {
        if(val > 0) walkNoiseDensity = val;
        else System.err.println("walkNoiseDensity must be greater than zero.");
    }

    /**
     * Will the denisty value vary as a random walk or remain stable?
     * Yes if true, variable if false.
     * @param val The new state, true or false.
     */
    public void setWalkVaryDensity(boolean val) {
        walkVaryDensity = val;
    }
        
    /**
     * Specify the minimum number of sample cycles before the Noise Density changes.
     * Values greater than 0 only.
     * @param val The new minium number of sample steps before change.
     */
    public void setWalkNoiseDensityMin(int val) {
        if(val > 0) walkNoiseDensityMin = val;
        else System.err.println("walkNoiseDensityMin must be greater than zero.");
    }
        
    /**
     * Specify the maximum number of sample cycles before the Noise Density changes.
     * Values greater than 0 only.
     * @param val The new maximum number of sample steps before change.
     */
    public void setWalkNoiseDensityMax(int val) {
        if(val > 0) walkNoiseDensityMax = val;
        else System.err.println("walkNoiseDensityMax must be greater than zero.");
    }
        
    /**
     * Specify The size of the noise density change at each iteration.
     * Values greater than 0 only.
     * @param val The new maximum value.
     */
    public void setWalkNoiseDensityStepSize(int val) {
        if(val > 0) walkNoiseDensityStepSize = val;
        else System.err.println("walkNoiseDensityMax must be greater than zero.");
    }
        
    /**
     * Specify the size of the maximum sample time.
     * Values between 0 and 100 only.
     * @param val The new maximum value.
     */
    public void setGendynTimeMirror(double newVal) {
        if(newVal > 1.0 && newVal <= 100.0) {
            this.gendynTimeMirror = (int)newVal;
            //System.out.println(newVal);
        } else System.err.println("GendynTimeMirror must be between 3 and 100, not " + newVal);        }
        
    /**
     * Specify the size of the maximum rand amp value.
     * Values between 0 and 100 only.
     * @param val The new maximum value.
     */
    public void setGendynAmpMirror(double newVal) {
        if(newVal > 0 && newVal <= 100) {
            this.gendynAmpMirror = (int)newVal;
        } else System.err.println("GendynAmpMirror must be between 1 and 100, not " + newVal);
    }
    
    public double getGendynAmp0() {
        return gendynAmp0;
    }
    
    public int getGendynPointSize() {
        return gendynPointSize;
    }
    
    public void setGendynPointSize(int val) {
        pointSizeReset = true;
        newPointSize = val;
    }
    
    private void resetPointSize() {
        this.gendynPointSize = newPointSize;
        gendynAmpArray = new double[gendynPointSize];
        gendynTimeArray = new double[gendynPointSize];
        for (int i=0; i<gendynPointSize; i++) {
            gendynAmpArray[i] = 50;
            gendynTimeArray[i] = 30;
        }
        if (getGendynAmpStepSize() < 3) setGendynAmpStepSize(3);
        pointSizeReset = false;
    }
    
    public double getGendynAmpArray(int i) {
        return gendynAmpArray[i];
    }
    
    public double getGendynTimeArray(int i) {
        return gendynTimeArray[i];
    }
    
    public double getGendynAmpStepSize() {
        return this.gendynAmpStepSize;
    }
    
    public double getGendynTimeStepSize() {
        return this.gendynTimeStepSize;
    }
    
    public void setGendynAmpStepSize(int val) {
        if(val >= 0) this.gendynAmpStepSize = val;
    }
    
    public void setMaxGendynAmpStepSize(int val) {
        if(val >= 0) this.maxGendynAmpStepSize = val;
    }
    
    public void setGendynTimeStepSize(double val) {
        if(val >= 0.0) this.gendynTimeStepSize = val;
    }
    
    public void setMaxGendynTimeStepSize(int val) {
        if(val >= 0) this.maxGendynTimeStepSize = val;
    }
    
    public void setGendynPrimaryAmpStepSize(int val) {
        if(val >= 0) this.gendynPrimaryAmpStepSize = val;
    }
    
    public void setGendynPrimaryTimeStepSize(int val) {
        if(val >= 0) this.gendynPrimaryTimeStepSize = val;
    }
    
    public void setGendynAmpGranularity(int val) {
        this.gendynGranularityUpdate = true;
        this.tempGendynGranularity = val;
    }
    
    private void updateGranularity() {
        if(tempGendynGranularity > 0) this.gendynAmpGranularity = tempGendynGranularity;
    }
    
    public void setGendynPrimaryTimeMirror(int val) {
        if(val >= 0) this.gendynPrimaryTimeMirror = val;
    }
       
    public void setGendynPrimaryAmpMirror(int val) {
        if(val >= 0) this.gendynPrimaryAmpMirror = val;
    }
    
    public void setGendynInterpolation(int val) {
        this.gendynInterpolation = val;
        //System.out.println("interp set");
    }
    
    public int getGendynInterpolation() {
        return this.gendynInterpolation;
    }
    
    /**
     * Specify the use of linear or gaussian randomness for the Gendyn noise
     * @param val true = gaussian probability, false = linear probability
     */
    public void setGendynGaussian(boolean val) {
        this.gendynGaussian = val;
    }
}
