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
package jm.audio;

import java.util.Enumeration;
import java.util.Stack;
import java.io.*;

import jm.music.data.*;
import jm.audio.io.*;
import jm.audio.Instrument;

/**
 * The Audio class provides a number of static methods to help pass
 * a jmusic score to the audio architecture and for putting a notes
 * sample information into the correct location in a global audio file.<br><br>
 * WARNING !!!!!!!
 * This class is an absolute disgrace ;)  It works but is very ugly
 * and I can't be bothered to clean it up at the moment. If anyone feels like
 * cleaning it up go for it ;)
 * @author Andrew Sorensen 
 * @version 1.0,Sun Feb 25 18:42:43  2001
 */
public final class Audio implements jm.JMC{
	/** Do we want to write the jpf file */
	private static boolean JPF = false;
	/**
	 * Makes an array which contains all the notes from all the phrases
	 * from all the instruments etc. solely based on start times.
	 * This method also writes a jpf file which is a text file containing
	 * the names of all the notes corresponding audio files with their
	 * start times and lengths.
	 * @param score the score to take data from
	 */
	public static void processScore(Score score, Instrument[] instList, String fileName){
        Stack inst = new Stack();
        // add an instrument to avoid errors from no instrument assignment by user
        inst.push(instList[0]);
        for (int i=0; i<instList.length; i++) {
            if(instList[i] != null) {
                if(! instList[i].getInitialised()) {
                    try {
                        if(instList[i].getInitialised() == false) {
                            instList[i].createChain();
                            instList[i].setInitialised(true);
                        }
                    } catch (AOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
		Enumeration enum1 = score.getPartList().elements();
		//set score tempo
		double score_ratio = 60.0 / score.getTempo();
        int partCounter = 0;
		/* Enumerate through all parts */
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();

			//set part tempo
			double part_ratio = score_ratio;
			if(part.getTempo() > 0.0)
		   	part_ratio = 60.0 / part.getTempo();

			/* Get the instrument being used for this part */
			if(part.getInstrument() != NO_INSTRUMENT){
				try{
					inst.push(instList[part.getInstrument()]);
				}catch(ArrayIndexOutOfBoundsException npe){
                    System.out.println("jMusic Audio warning: Can't find the instrument number "+
                                        part.getInstrument() + " that you have specified for "+
                                        "the part named " + part.getTitle()+".");
				}
			}
            System.out.println("Part "+ partCounter++ + " '"+part.getTitle()+"'. ");
            
			/* Enumerate through all phrases */
			Enumeration enum2 = part.getPhraseList().elements();
            int phraseCounter = 0;
			while(enum2.hasMoreElements()){
				Phrase phr = (Phrase) enum2.nextElement();
				//get phrase tempo
				double phrase_ratio = part_ratio;
				if(phr.getTempo() > 0.0){
					System.out.println("A: "+phrase_ratio);
					phrase_ratio = 60.0 / phr.getTempo();
					System.out.println("B: "+phrase_ratio);
				}
				/* Get the instrument being used for this phrase */
				if(phr.getInstrument() != NO_INSTRUMENT){
					try{
                        // add phrase instrument to stack
						inst.push(instList[phr.getInstrument()]); 
					}catch(ArrayIndexOutOfBoundsException npe){
                        System.out.println("jMusic Audio warning: Can't find the instrument number "+
                                            phr.getInstrument() + " that you have specified for" +  
                                            " the phrase named "+phr.getTitle()+".");
					}
				}
				double time=part_ratio * phr.getStartTime(); //start time of phrase
				double ntime = 0.0; //notes distance from phrases start time
				Enumeration enum3 = phr.getNoteList().elements();
                System.out.print("    Phrase "+ phraseCounter++ + " '"+ phr.getTitle()+"'" + 
                                 " starting at beat " + phr.getStartTime() + ": ");
				
                /* Enumerate through all notes */
				int phraseNoteCounter = 0;
				while(enum3.hasMoreElements()){
                                    
					Note note = (Note) enum3.nextElement();
					if(note.getFrequency() == (double)REST){ //This a rest ???
						ntime += phrase_ratio * note.getRhythmValue();
						continue;
					}
                    phraseNoteCounter++;
                    if (phraseNoteCounter%10 == 0) {
                    	System.out.print(phraseNoteCounter);
                    } else System.out.print(".");
					Note new_note = note.copy();
                    //System.out.println("new note pitch = " + new_note.getPitch());
					new_note.setDuration(phrase_ratio * note.getDuration());
					new_note.setRhythmValue(phrase_ratio * note.getRhythmValue());
                    Instrument currInst = (Instrument)inst.peek();
					currInst.setBlock(false);
					currInst.setFinished(true);
					currInst.renderNote(new_note,((double)time+ntime));
					currInst.setFinished(false);
					currInst.iterateChain();
					ntime += phrase_ratio * note.getRhythmValue();
				}
                                
				System.out.println();
                // remove phrase instrument from stack
                if(phr.getInstrument() != NO_INSTRUMENT) inst.pop();  
            }
            
		}
	}

	/**
	 * Combine converts the floating point audio file and combines them into an integer file.
	 */
	public static void combine(String fileJmp, String tmpFile, String fileOut, 
							   boolean deleteFiles, boolean multi){
		if(multi){
			Audio.sampleRate = SampleOut.samprate;
			Audio.channels = SampleOut.numofchan;
			System.out.println("Bit Depth: 16" + " Sample rate: " + SampleOut.samprate +
				" Channels: " + SampleOut.numofchan);
			Audio.addEmUp(tmpFile,fileOut,SampleOut.max);
			return;
		}else{
			int numofdot = 1; //For print outs only
			
			float max = (float) 0.0; //the largest sample value
			try{
				FileReader fr = new FileReader(fileJmp);
				StreamTokenizer st = new StreamTokenizer(fr);
				RandomAccessFile raf = new RandomAccessFile(tmpFile,"rw");
				
				double time1 = System.currentTimeMillis();			
				for(;;){
                    try{
                        st.nextToken();
                        String fileName = st.sval;
                        if(fileName == null){
                                //No more tokens 
                                break;
                        }
                        st.nextToken();
                        long position = (long)(st.nval * 4);
                        st.nextToken();
                        int length = (int)st.nval;
                        
                        float res = getAudio(fileName, position, length, max, raf); 
                        
                        if(max < res) {
                                max = res;
                                System.out.println("Max is smaller: " + max);
                        }
                        if(res < 0 && max < (res*(float)-1.0)){
                                max=(res*(float)-1.0);
                                System.out.println("MAX is bigger: " + max);
                        }
                        /*
                        if(deleteFiles){
                                if(DEBUG)System.out.println("Deleting " + fileName);
                                File fl = new File(fileName);
                                fl.delete();
                        }
                        */
                        if((numofdot % 10)==0){
                                if(VERBOSE) System.out.print(numofdot);
                        }else{
                                if(VERBOSE) System.out.print(".");
                        }
                        numofdot++;
                        // close random access file
                        raf.close();
                    }catch(EOFException eof){
                        //This is expected
                        break;
                    }catch(IOException ioe){
                        ioe.printStackTrace();
                    }
				}
				System.out.print("\n");
				double time2 = System.currentTimeMillis();
				System.out.println("Created tmp file in " + (((time2 - time1))/1000.0) + " seconds");
				
				double now = System.currentTimeMillis();
				//addEmUp(raf, fileOut, max);
				addEmUp(tmpFile, fileOut, max);
				double now2 = System.currentTimeMillis();
				System.out.println("Mixed to a single file in " + (((now2 - now))/1000.0) + " seconds");
				
				if(deleteFiles){
					File jmp = new File(fileJmp);
					File tpm = new File(tmpFile);
					jmp.delete();
					tpm.delete();
				}
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}
	
	//Provided so that I can set the number of channels in the addEmUp method 
	private static int channels;
	private static int sampleRate;
	private static float getAudio(String fileName, long position, int length, 
								   float max, RandomAccessFile raf){
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		try{
			fis = new FileInputStream(fileName);
			bis = new BufferedInputStream(fis, 4096);
			dis = new DataInputStream(bis);
			//Read the files header
			if(dis.readInt() != 0x2E736E64){
				System.out.println("jMusic SampleIn warning: This file is NOT in the .au/.snd file format");
				return max;
			}
			int offset = dis.readInt();
			int numOfBytes = dis.readInt();
			int format = dis.readInt();
			Audio.sampleRate = dis.readInt();
			Audio.channels = dis.readInt();
			fis.skip(offset - 24); //skip the rest of the header

			//adjust position and length for multiple channels
			position *= (long)channels;
			length *= channels;

		}catch(IOException ioe){
			ioe.printStackTrace();
		}			

		for(;;){
			try{
                raf.seek(position);
                //read in and convert to sample
                float sample = (float)((float)dis.readShort()/(float)32767);
                try{//if we can read from the file
                    float d = raf.readFloat();
                    raf.seek(position);
                    position += 4;
                    float tmp = d + sample;
                    if(max < tmp){
                            max = tmp;
                            System.out.println("MAX small: " + max);
                    }
                    if(tmp < 0 && max < (tmp*(float)-1.0)){
                            max=(tmp*(float)-1.0);
                            System.out.println("MAX large: " + max);
                    }
                    raf.writeFloat(tmp);
                }catch(EOFException eofe){
                    //This means that there is nothing to read so we can happily write
                    if(max < sample)max = sample;
                    if(sample < 0 && max < (sample * (float)-1.0)){
                            max=(sample*(float)-1.0);
                    }
                    raf.writeFloat(sample);
                    position += 4;
                }
			}catch(EOFException eofe){
				//we've run out of samples to read
				break;
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}

		try{
			dis.close();
			fis.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return max;
	}

    
	public static void addEmUp(String tmpFileName, String fileName, float max){
		if(VERBOSE) {
			System.out.println("MAX amplitude: " + max);
			System.out.println("Writing .au/.snd file '" + fileName + "' please wait...");
		}
		try{
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
			DataOutputStream dos = new DataOutputStream(bos);

			File tmpF = new File(tmpFileName);
    			FileInputStream fin = new FileInputStream(tmpF);
			BufferedInputStream bin = new BufferedInputStream(fin, 4096);
			DataInputStream dis = new DataInputStream(bin);

			int offset = 28;
      // Modified FP nov 2004
			int numOfBytes = 0; //int numOfBytes = ((int)tmpF.length()/2)+16;
			int format = 3;//6; 
			//write header
			dos.writeInt(0x2E736E64); // .snd
			dos.writeInt(offset); //offset from the beginning or the file				
			dos.writeInt(numOfBytes); //num of bytes in file (after this)
			dos.writeInt(format); //compression format
			dos.writeInt(Audio.sampleRate); //sampling rate
			dos.writeInt(Audio.channels); //num of channels
			dos.writeInt((short) 0); //add some padding
			//put RandomAccessFile back to the start

			//raf.seek(0);

			double tt = System.currentTimeMillis();
			try{
                for(;;){
                    float samp = dis.readFloat();
                    float outgoing = samp/max;
                    if(outgoing < (float)-1.0 || outgoing > (float)1.0){
                            System.out.println("Outgoing= " + outgoing +
                                    "  SAMPLE: " + samp + "  MAX: " + max +
                                    "  SampleOut.max: "+SampleOut.max);
                    }
                    //dos.writeFloat(outgoing);
                    dos.writeShort((short)(outgoing*32767));
                    numOfBytes += 2;
                    //System.out.println((short)(dis.readFloat()/max)*32767);
                }
			}catch(EOFException eofe){
				//expected
				double ttt = System.currentTimeMillis();
				System.out.println("Finished writing the audio file in " + (((ttt - tt))/1000.0) + " seconds");
				dos.flush();
				fos.flush();
				bos.flush();
				dos.close();
				fos.close();
				bos.close();
				fin.close();
				bin.close();
				dis.close();
				tmpF.delete();
        // Thanks to Francois Pinot for this work around
        if(tmpF.exists()){ // set to empty (to avoid subsequent overlaying)
           RandomAccessFile raf = new RandomAccessFile(tmpFileName, "rw");
           raf.setLength(0);
           raf.close();
        }
        // Added FP nov 2004
        RandomAccessFile auxraf = new RandomAccessFile(fileName, "rw");
        auxraf.seek(8);
        auxraf.writeInt(numOfBytes);
        auxraf.close();
        // End added FP nov 2004
        return;
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
			System.out.println(ioe);
		}
	}

}
