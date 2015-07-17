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

import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.Instrument;
import jm.audio.AOException;

/**
* @author Andrew Sorensen and Andrew Brown
 * @version 1.0,Sun Feb 25 18:42:48  2001
 * @version 2.0 July 2003
 */

public final class Pluck extends AudioObject{
    //----------------------------------------------
    // Attributes 
    //----------------------------------------------
    private int index = 0;
    float[] kernel = null;
    /** Is this object a primary generator of data in the chain? */
    private boolean primary = true;
    private float prevSample = 0.0f;
    // The amount of signal reused, 0.5 is max sustain.
    private float feedback = 0.49f;
    // all pass variables
    //The gain of this comb filter 
    private float decay = 0.5f;
    //The delay (in samples) to use with this comb filter
    private int delay = 1;
    //Number of samples to delay by
    private float[] delayLine;
    //Delay line current index
    private int delayIndex;
    
    
    //----------------------------------------------
    // Constructors 
    //----------------------------------------------
    /**
        * This constructor sets up pluck as a generator of samples.
     * @param inst The containing instrument, usually passed 'this'
     * @param sampleRate The integer number of samples per second (higher rates produce brighter timbre).
     * @param channels The interger number of channels, 1 = mono, 2 = stereo, etc.
     */
    
    public Pluck(Instrument inst,int sampleRate, int channels){ 
        this(inst, sampleRate, channels, 0.49);
    }
    
    /**
        * This constructor sets up pluck as a generator of samples.
     * @param inst The containing instrument, usually passed 'this'
     * @param sampleRate The integer number of samples per second (higher rates produce brighter timbre).
     * @param channels The interger number of channels, 1 = mono, 2 = stereo, etc.
     * @param feedback The sustain level (0.5 is maximum, changes as small as 0.001 are significant)
     */
    public Pluck(Instrument inst,int sampleRate, int channels, double feedback){ 
        super(inst, sampleRate,"[Pluck]");
        this.channels=channels;
        this.feedback = (float)feedback;
    }
    
    /**
        * This constructor takes a single AudioObject as input
     * and in this becomes a processor object which
     * filters it's input through  the Karpulus-Strong algorithm.
     * @param ao the single AudioObject to use as input.
     */
    public Pluck(AudioObject ao){ // not yet working
        this(ao, 0.5);
    }
    
    /**
        * This constructor takes a single AudioObject as input
     * and in this becomes a processor object which
     * filters it's input through  the Karpulus-Strong algorithm.
     * @param ao the single AudioObject to use as input.
     * @param feedback The sustain level (0.5 is maximum, changes as small as 0.001 are significant)
     */
    public Pluck(AudioObject ao, double feedback){ // not yet working
        super(ao, "[Pluck]");
        this.primary = false;
        this.feedback = (float)feedback;
    }
    
    
    /**
        * Specify the new value of the time it takes for the sound to decay.
     * @param feedback New interation feedback value, equals the sustain level 
     * (0.5 is maximum, changes as small as 0.001 are significant)
     */
    public void setFeedback(double feedback) {
        this.feedback = (float)feedback;
    }
    
    /**
    * Called at the start of each note.
     */
   	public void build(){
        double freq = currentNote.getFrequency();
        int length = (int)((double)sampleRate / freq);
        this.kernel = new float[length];
        for(int i=0;i<length;i++){
            if (primary) kernel[i] = (float)(Math.random()*2.0 - 1.0); // start with noise
            else kernel[i] = 0.0f; // fill with slience
        }
        // setup all pass values
        int sampleDelay = (int)((float)((float)this.delay/(float)1000) * this.sampleRate); 
        this.delayLine = new float[sampleDelay*this.channels];
        this.delayIndex = 0;
    }
    
    /**
    * Create or process samples through the filter.
    * Called for each buffer of smaples.
    */
    public int work(float[] buffer)throws AOException{
        int i= 0;
        float temp = 0.0f;
        if (primary) {
            if(index >= kernel.length) index = 0;
            for(;i<buffer.length;){
                temp = kernel[index];
                for(int j=0; j<channels;j++) {
                    buffer[i] = kernel[index];
                    // all pass
                    try {
                        buffer[i] += delayLine[delayIndex] * decay;
                    } catch (IndexOutOfBoundsException e ) {
                        System.err.println("jMusic Pluck audio object error: i = " + i + " delayIndex = " + delayIndex);
                    }
                    float a = buffer[i] * -decay;
                    float b = delayLine[delayIndex];
                    delayLine[delayIndex] = buffer[i];
                    buffer[i] = a+b;
                    if(delayIndex >= delayLine.length){
                        delayIndex = 0;
                    }
                    i++;
                }
                kernel[index] = (kernel[index]+prevSample) * feedback;
                prevSample = temp;
                index++;
                if(index >= kernel.length) index = 0;
            }
        } else {
            if(index >= kernel.length) index = 0;
            for(;i<buffer.length;){
                temp = buffer[i];
                kernel[index] = (buffer[i] + prevSample) * feedback;
                for(int j=0; j<channels;j++) {
                    buffer[i++] = kernel[index];
                }
                prevSample = temp;
                index++;
                if(index >= kernel.length) index = 0;
            }
        }
        return i;
    }
}