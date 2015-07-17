/*
<This Java Class is part of the jMusic API version 1.5, March 2004.> Copyright (C) 2000 Andrew Sorensen & Andrew Brown This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
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
 * @author Andrew Sorensen
 */
public final class ReSample extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	private double baseFreq = 0.0; 
	private double newFreq = 0.0;
	private boolean noteFreq = true;
	
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public ReSample(AudioObject ao, double baseFreq){
		super(ao, "[ReSample]");		
		this.baseFreq = baseFreq;
	}

	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 */
	public void build(){
		if(noteFreq)this.newFreq = this.currentNote.getFrequency();
		this.finished = true;
	}

	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * This is nextWork method simply needs to skip over
	 * the right number of samples to cause a shorter
	 * wave period. This means that the pitch goes up.
	  The number of samples to skip is calculated by
	 * the setPhaseInt() method which is the real
	 * nextWork horse of this class.
	 * @param input a single Audio Object input.
	 */
	public int work(float[] buffer)throws AOException{
		double cfm = newFreq/baseFreq; 
		double skip = -1.0/((1.0-cfm)/cfm); 
		double remains = 0.0;
		int upSample = 0; //value 0 is upsample
		if(skip<0){skip=-1.0/skip; upSample=1;}//value 1 is downsample
		if(skip==0){upSample=2;skip=1;}//value 2 is no resample
		float[] tmpBuf = new float[(int)((double)(buffer.length*cfm)+0.5) + 1]; // +1
		int ret = this.previous[0].nextWork(tmpBuf);
		float tmp = 0.0f; //holds previous sample for interpolation
		int count = 0, index = 0;
		for(int i=0;true;i++){
			if(upSample==0){ //Remove Samples
				if(++index>=(int)(skip+remains)){
					remains=(skip+remains)%1.0;
					index=0;
					continue;
				}
				if(count>=buffer.length)break;
				buffer[count++]=tmpBuf[i];
			}else if(upSample==1){ //Add Samples
				if((skip+remains)>=1.0){ //add extra interpolated sample
					float increment = (tmpBuf[i]-tmp)/(float)(skip+remains);
					for(int k=0;k<(int)(skip+remains);k++){
						buffer[count++]=tmp+(increment*k);
						if(count==buffer.length)break;
					}
				}
				if(count==buffer.length)break;
				buffer[count++]=tmpBuf[i];
				tmp=tmpBuf[i];
				remains=(skip+remains)%1.0;
			}else{ //Do Nothing
				buffer[count++] = tmpBuf[i];
			}
			if(count==buffer.length)break;
		}
		return buffer.length;
	}
}
