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
 * An AudioObject for granulating input.
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:48  2001
 * Revised and modified extensively by Timothy Opie
 * Last changed v1.4 27/04/2003
 */

public final class Granulator extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	private int grainDuration = 1323;
	private int envelopeType = 1;
	private int nog; //number of grains
	private float cfm; //current frequency modifier
	private int cgd; //counter for grain duration
	private float[] grain; //grain buffer
	private float[] newbuf; //output buffer
	private int grainCnt = 0; 
	private int grainsPerSecond = 10;
	private float[] tailBuf; //so we don't get cut off between buffers
	private float freqMod = 1.0f;
	private float[] inBuffer = null;
	private boolean inBufActive = false;
	private boolean ri = false; //random indexing
	private boolean rgd = false; //random grain duration
	private int rdist = 0; //random distribution within cloud
	private int rdisttemp = 0;
	private int gdb = 1000; //the smallest random grainduration
	private int gdt = 1000; //the highest random grain duration + gdb
	private boolean rf = false; //random frequency
	private float rfb = 0.99f; //the lowest random frequency
	private float rft = 1.01f; //the highest random frequency + rfb
	
	private float[] durationArray; //premapped grain duration
	private float[] gpsArray; //premapped grains per second
	private float[] freqArray; //premapped frequency
	private boolean premapped = false; //premapped flag so the program
					   //knows when it is premapped or not.
	
	
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public Granulator(AudioObject ao, int sampleRate, int channels, int duration, int gps){
		super(ao, "[Granulator]"); 
		this.grainDuration = duration;
		this.grainsPerSecond = gps;
		this.cgd = 0;
		this.grain = new float[this.grainDuration];
		tailBuf = new float[0];
		this.sampleRate = sampleRate;
		this.channels = channels;
	}
	
	public Granulator(AudioObject ao, int sampleRate, int channels, float[] durationArr, float[] gpsArr, 
			  float[] freqArr, boolean rif, boolean rgdf, boolean rff, int rd){
		super(ao, "[Granulator]"); 
		this.durationArray = durationArr;
		this.gpsArray = gpsArr;
		this.freqArray = freqArr;
		this.grain = new float[(int)this.durationArray[0]];
		this.ri = rif;
		this.rgd = rgdf;
		this.rf = rff;
		tailBuf = new float[0];
		this.rdist = rd;
		premapped = true;
                this.sampleRate = sampleRate;
                this.channels = channels;
	}
	
	
	/**
	 * @param buffer The sample buffer.
	 * @return The number of samples processed.
	 */
	public int work(float[] buffer)throws AOException{
		if(inBuffer == null){
			newbuf = new float[buffer.length];
			this.previous[0].nextWork(newbuf);
		}else{
			newbuf = new float[buffer.length];
			for(int i=0;(i<inBuffer.length)&&(i<newbuf.length);i++){
				newbuf[i] = inBuffer[i];
			}
			inBuffer = null;
		}
		//number of grains to fit in buffer
		if (grainsPerSecond <= 0) grainsPerSecond = 1;
		//if (premapped) {
		//    nog = (int)((float)newbuf.length/
		//	((float)(sampleRate*channels)/gpsArray[grainCnt])); 
		//} else {
		    nog = (int)((float)newbuf.length/
			((float)(sampleRate*channels)/(float)grainsPerSecond));
		//}
		if (nog <= 0) nog = 1;		
		//time between grains
		int tbg=(newbuf.length/nog);
		//add any grain tails 
		for(int i=0;(i<buffer.length)&&(i<tailBuf.length);i++){
			buffer[i]+=tailBuf[i];
		}
		tailBuf = new float[newbuf.length];
		inBufActive = true;
		//add all new grains
		for(int i=0;i<nog;i++){
			if (rdist > 0) rdisttemp = (int) (Math.random()*rdist);
			else rdisttemp = 0;
			int index = ((i*tbg)+rdisttemp);
			setGrain(index-rdisttemp);
			for(int j=0;j<grain.length;j++){
				if(index >= buffer.length){
					tailBuf[index-buffer.length]+=grain[j];
				}else{
					buffer[index] += grain[j];
				}
				index++;
				grainCnt++;
			}
		}
		inBufActive = false;
		return buffer.length;
	}

	/**
	 * Deviation from the input frequency
	 */
	public void setFreqMod(float fmod){
		this.freqMod = fmod;
	}

	/**
	 * Set the grains duration 
	 */
	public void setGrainDuration(int gdur){
		this.grainDuration = gdur;
	}

	/**
	 * Set the number of grains per second
	 */
	public void setGrainsPerSecond(int gps){
		this.grainsPerSecond = gps;
	}
	
         /**
          * Set the envelope type
          */
         public void setEnvelopeType(int et){
                 this.envelopeType = et;
         }


	/**
 	 * Set a random grain duration
	 */
	public void setRandomGrainDuration(boolean bool){
		this.rgd = bool;
	}

	/**
	 * Set the random grain durations bottom value
	 */
	public void setRandomGrainBottom(int b){
		this.gdb = b;
	}

	/**
	 * Set the random grain durations top value
	 */
	public void setRandomGrainTop(int t){
		this.gdt = t;
	}

	/**
	 * Set random index position
	 */ 
	public void setRandomIndex(boolean bool){
		this.ri = bool;
	}

	/**
	 * set random frequency
	 */
	public void setRandomFreq(boolean bool){
		this.rf = bool;
	}

	/**
	 * set random distribution within cloud
	 */
	public void setRandomDist(int rd){
		this.rdist = rd;
	}

	/**
	 * Set the random frequency bottom value
	 */
	public void setRandomFreqBottom(float fb){
		this.rfb = fb;
	}

	/**
	 * Set the random frequency bottom value
	 */
	public void setRandomFreqTop(float ft){
		this.rft = ft;
	}
	
	//---------------------------------------
	// Private Methods
	//----------------------------------------
	/**
	 * Set the grain 
	 */
	private void setGrain(int index) throws AOException{
		if(ri) index=(int)(Math.random()*(double)newbuf.length);
		float[] buf = newbuf; //reference to the active buffer
		if(rgd) this.cgd = gdb+(int)(Math.random()*gdt);
		else /* { if (premapped) {
			this.cgd = (int)durationArray[grainCnt];
		    } else { */
			this.cgd = this.grainDuration;
		//    }
		//}
		//if (premapped) {
		//    this.cfm = this.freqArray[grainCnt];
		//} else {
		this.cfm = this.freqMod;
		//	//System.out.println("cfm" + cfm);
		//}
		if(rf) cfm = (float)(rfb+(Math.random()*(rft-rfb)));
		if(inBufActive){
			inBuffer = new float[newbuf.length];
			int ret = this.previous[0].nextWork(inBuffer);
			inBufActive=false;
		}
		this.grain = new float[cgd];
		int count = 0;
		float tmp = 0.0f;
		//positive values of skip are the iterations to skip
		//negative values of skip are the iterations to add between
		double skip = -1.0/((1-cfm)/cfm);
		double remains = 0.0;
		int upSample = 0;
		if(skip<0){skip=-1.0/skip; upSample=1;}
		if(skip==0)upSample=2;
		int ind=0;
		//System.out.println("skip" + skip + "cfm" + cfm);
		for(int i=index;true;i++){
			if(i==buf.length){i=0;buf=inBuffer;}
			if(upSample==0){//remove samples (up sample)
				//if(i%((int)(skip+remains))==0)continue;
				if(++ind>=(int)(skip+remains)){
					remains=(skip+remains)%1.0;
					ind=0;
					continue;
				}	
				if(count >= cgd)break;
				grain[count++]=buf[i];	
			}else if(upSample==1){//add samples (downsample)
				if((skip+remains)>=1.0){
					float p=(tmp-buf[i])/((int)skip+1);
					for(int k=0;k<(int)(skip+remains);k++){
						grain[count++]=p*k+buf[i];
						if(count==cgd)break;
					}
				}
				if(count==cgd)break;
				grain[count++]=buf[i];
				tmp=buf[i];	
				remains = (skip+remains)%1.0;
			}else{ //no resample ;)
				grain[count++]=buf[i];
			}
			if(count==cgd)break;
		}

		//Envelope our new grain
		if (envelopeType <= 1) {
			for(int i=0;i<cgd;i++){
                               this.grain[i] = this.grain[i] * (float)(0.5 - 0.5 * 
                                               Math.cos(2 * Math.PI * i/cgd));
                       }
		}
                 //else if (envelopeType == 2) //{
                 //       for(int i=0;i<cgd;i++){
                 //                this.grain[i] = this.grain[i] * (float)
		 //				  (Math.sin(Math.PI*i/cgd));
                 //        }
                 //}
               	else if (envelopeType == 3) {
                    	for(int i=0;i<cgd/2;i++){
                                 this.grain[i] = this.grain[i] * 2;
                       	}
                       	for(int i=cgd/2;i<cgd;i++){
                                 this.grain[i] = this.grain[i] * -2 + 2;
                      	}
             	}
             	else {
                         for(int i=0;i<cgd;i++){
                                 this.grain[i] = this.grain[i] * (float)
						(Math.sin(Math.PI*i/cgd));
                                 
                         }
                }
	}
}
