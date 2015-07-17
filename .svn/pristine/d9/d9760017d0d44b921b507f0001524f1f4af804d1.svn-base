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
 * The Fast Grain Transform & Resynthesis all in one AO!
 * @author Timothy Opie
 * @version 1.0, Wednesday November 12, 2003
 * Last changed: 
 * Description: This class takes a stream of audio data and reduces 
 * it to grain information comprising solely of a start and finish 
 * time band, a top and bottom frequency band and an amplitude.
 */

public final class AllFGTR extends AudioObject{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	//private float[] grain = new float[buffer.length]; //grain buffer
	private float[][] FGTArray = new float [200][7];
	private float /*grainDuration,*/ bandwidthTop, bandwidthBottom;
	private float frequency, spatial, highestAmp = 0.0f;
	// interOnset is the space between the start of each grain
	private int grainsPerSecond, interOnset, grainDuration;
	private int bCounter=0, gCounter=0, dCounter=0;//This counter keeps track of the buffer
	private int grainsPerBuffer=0, sampleRate=44100, channels=2;

	// insert filter stuff here, if it will be filtered internally
	
	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	public AllFGTR(AudioObject ao, int gDuration, float bwidthTop, float bwidthBottom, int gps){
		super(ao, "[AllFGTR]"); 
		this.grainDuration = gDuration; //this is in samples 
		this.bandwidthTop = bwidthTop; //this filter info is purely for data collection puroposes
		this.bandwidthBottom = bwidthBottom;
		this.grainsPerSecond = gps;
		//System.out.println("bandwidthBottom: " + bandwidthBottom);
		//System.out.println("bandwidthTop: " + bandwidthTop);
		//System.out.println("grainDuration: " + grainDuration);
		//System.out.println("grainsPerSecond: " + grainsPerSecond);
	}
	
	
	/**
	 * --------------------------------------------
	 * Beginning
	 * --------------------------------------------
	 */
	public int work(float[] buffer)throws AOException{
	System.out.println("Point 1");
	//System.out.println("grainsPerSecond: " + grainsPerSecond);
		// Regarding the filter: I am thinking it will be easier to initiate the filter 
		// as part of the instrument, rather than in this section, so that FGT.java will 
		// assume it is using a filtered audio buffer. This may change in the future.....
		//Filter filterOne = new Filter(buffer, bandwidthBottom, Filter.LOW_PASS);
        //Filter filterDone = new Filter(filterOne, bandwidthTop, Filter.HIGH_PASS);
        highestAmp=0.0f;
        bCounter=0;
        gCounter=0;
        dCounter=0;
		grainsPerBuffer=(grainsPerSecond*buffer.length)/(sampleRate*channels);
		//System.out.println("grainsPerSecond: " + grainsPerSecond + "buffer.length: " + buffer.length + "sampleRate: " + sampleRate + "channels: " + channels + "grainsPerBuffer: " + grainsPerBuffer);
		interOnset=buffer.length/grainsPerBuffer;
		for(gCounter=0;gCounter<grainsPerBuffer;gCounter++){
			System.out.println("Point 1.1");
			bCounter=gCounter*interOnset;
			highestAmp=0;
			System.out.println("gCounter: " + gCounter);
			System.out.println("grainDuration1: " + grainDuration);
			System.out.println("dCounter: " + dCounter);
			for(dCounter=0;dCounter<grainDuration;dCounter++){
				//System.out.println("Point 1.2");
				//System.out.println("grainDuration: " + grainDuration);
				float temp=buffer[bCounter]*(float)(Math.sin(Math.PI*((float)dCounter/(float)grainDuration)));
				//System.out.println("temp: " + temp);
				//System.out.println("buffer[bCounter]: " + buffer[bCounter]);
				//System.out.println("bCounter: " + bCounter);
				//System.out.println("dCounter: " + dCounter);
				//System.out.println("grainDuration1: " + grainDuration);
				//System.out.println("Math.PI: " + Math.PI);
				//System.out.println("dCounter/grainDuration: " + (float)dCounter/(float)grainDuration);
				float temp2=temp; //just for bug checking
				if (temp<0.0f) temp=temp*-1;
				if (highestAmp < temp) highestAmp=temp;
				//System.out.println("Amp:" + temp2 + "Highest:" + highestAmp); //just for bug checking
				bCounter++;
			}
			System.out.println("Point 2");
			FGTArray[gCounter][0] = gCounter*interOnset;
			FGTArray[gCounter][1] = grainDuration;
			FGTArray[gCounter][2] = bandwidthTop;
			FGTArray[gCounter][3] = bandwidthBottom;
			FGTArray[gCounter][4] = 0.5f; //Stereo placement - will be improved later!
			FGTArray[gCounter][5] = highestAmp;
			FGTArray[gCounter][6] = grainsPerBuffer;
			
			//System.out.println("gCounter: " + gCounter + "bCounter: " + bCounter);
			//System.out.println("gCounter*interOnset2: " + gCounter*interOnset);
			//System.out.println("grainDuration2: " + grainDuration);
			//System.out.println("bandwidthTop2: " + bandwidthTop);
			//System.out.println("bandwidthBottom2: " + bandwidthBottom);
			//System.out.println("highestAmp2: " + highestAmp);
			//System.out.println("grainsPerBuffer2: " + grainsPerBuffer);
			//System.out.println("Point 3");
			if ((gCounter+1)*interOnset+grainDuration>buffer.length) gCounter=grainsPerBuffer; //safety backup
			//System.out.println("Point 4");
		}
		
		/**
		 * Transform has been created. Now time to resynthesise!
		 */
		// First reset all variables, just to be sure :)
		System.out.println("Point 5");
		grainDuration=0;
		bandwidthTop=0.0f;
		bandwidthBottom=0.0f;
		grainsPerBuffer=0; //can't reset this - it is important
		interOnset=0;
		highestAmp=0.0f;
		bCounter=0;
		gCounter=0;
		dCounter=0;
		for(int counter=0;counter<buffer.length;counter++){
			buffer[counter]=0.0f;
		}
		System.out.println("Point 6");
		//Now create new sound file in buffer and return
		grainDuration = 1936;//(int) FGTArray[gCounter][1];
		grainsPerBuffer = (int)FGTArray[gCounter][6];
		System.out.println("grainsPerBuffer6: " + grainsPerBuffer);
		System.out.println("grainDuration7: " + grainDuration);
		for(gCounter=0;gCounter<grainsPerBuffer;gCounter++){		
			bCounter = (int) FGTArray[gCounter][0];
			grainDuration = 1936;//(int) FGTArray[gCounter][1];
			bandwidthTop = FGTArray[gCounter][2];
			bandwidthBottom = FGTArray[gCounter][3];
			spatial = FGTArray[gCounter][4]; //Spatial placement - will be improved later!
			highestAmp = FGTArray[gCounter][5];
			frequency = (bandwidthTop + bandwidthBottom) * 0.5f; // to be improved later

			//System.out.println("Point 7");
			//System.out.println("gCounter: " + gCounter + "bCounter: " + bCounter);
			//System.out.println("bCounter7: " + bCounter);
			//System.out.println("grainDuration7: " + grainDuration);
			//System.out.println("bandwidthTop7: " + bandwidthTop);
			//System.out.println("bandwidthBottom7: " + bandwidthBottom);
			//System.out.println("highestAmp7: " + highestAmp);
			
			for(dCounter=0;dCounter<grainDuration;dCounter++){
				// This following short equation creates a sine wave and envelopes it at the correct duration
				buffer[bCounter]=buffer[bCounter]+(float)((Math.sin(2*Math.PI*dCounter*((frequency*buffer.length)/(sampleRate*channels)))) *
									(Math.sin(Math.PI*(dCounter/grainDuration))*highestAmp));
				// Or there is this method which generates a noise band between the specified top and bottom frequency range
			 	// and then envelopes it just like the previous equation.						
			 	
			 	// buffer[bCounter]=buffer[bCounter]+((bandwidthBottom+Math.random()*(bandwidthTop-bandwidthBottom))*
				//					(Math.sin(Math.PI*(dCounter/grainDuration))*highestAmp));
								
				bCounter++;
				//System.out.println("Point 8");
			}
			System.out.println("Point 9");
		}
		System.out.println("Point 10");	
		return buffer.length;
	}
}
