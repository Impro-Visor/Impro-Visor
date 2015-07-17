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
import jm.music.data.Note;
import jm.audio.AudioObject;
import jm.audio.AOException;

/**
 * The <bl>StereoPan</bl> class is used to set each of two
 * channels amplitudes such that the combined amplitudes of each
 * suggest an accurate stereo positioning.
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:50  2001
 */

public final class StereoPan extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** 
	 * The variable pan can sit anywhere between 0.0(full left) 
	 * and 1.0 (full right)
	 */
	private float pan;
	/** a marker for channels */
	private int channel = 1;
	/** Indicator that pan is in a set position */
	boolean panSet = false;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	 * Takes only a single AudioObject as input and
	 * defines a default pan of 0.5 which is to say
	 * dead centre.
	 * @param ao a single AudioObject input.
	 */
	public StereoPan(AudioObject ao){
		super(ao, "[StereoPan]");
		this.pan = (float)0.5;

	}

	/**
	 * Sets pan to a fixed point and ignores the pan
	 * position in the Note object. 
	 * @param pan where to place the stereo image
	 * @param ao a single AudioObject input.
	 */
	public StereoPan(AudioObject ao, double pan){
		super(ao, "[StereoPan]");
		this.panSet = true;
		if(pan < (float)0.0){
			this.pan = (float)0.0;
		}else if(pan > (float)1.0){
			this.pan = (float)1.0;
		}else{
			this.pan = (float)pan;
		}
	}

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 * A simple method to set the pan of this object.
	 * Some simple checking is put in place to ensure
	 * that the pan is never outside the range of
	 * 0.0 and 1.0
	 */
	public void build(){
		float tmpPan = (float) currentNote.getPan();
		if(!this.panSet) {
			if(tmpPan < (float)0.0){
				this.pan = (float)0.0;
			}else if(tmpPan > (float)1.0){
				this.pan = (float)1.0;
			}else{
				this.pan = tmpPan;
			}
		}
        channel = 1;
	}
	
    //----------------------------------------------
    // Methods
    //----------------------------------------------
    /**
     * The work method takes an input and changes its
     * amplitude based on which channel it is from
     * and where the pan is set.
     * @param input the incoming data. 
     */
    public int work(float[] buffer)throws AOException{
        int returned = this.previous[0].work(buffer);
        // don't do anything for mono data
        if(channels == 1) return returned;
        // change volume for stereo files
        for(int i=0;i<returned;i++){
            if(channel==1) {
                if (this.pan > 0.5) {
                    buffer[i] = buffer[i] * (1.0f - (this.pan - 0.5f) * 2.0f);
                } 
                channel=2;
            }else{
                if(this.pan < 0.5) {
                    buffer[i] = buffer[i] * this.pan * 2.0f;
                }      
                channel=1;
            }
            
        }
        return returned;
    }
        
    /*
     * Set a new pan position.
     * @param pan The new pan position (0.0 - 1.0)
     */
    public void setPan(double pan){
        if(pan >= 0.0 && pan <= 1.0) {
            this.pan=(float)pan;
        }
    }
}

