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

package jm.gui.wave;

import java.io.*;
import jm.music.data.Note;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.io.InputStream;

/**
* Audio file reading for jMusic.
* The class utilises the JavaSound file reading classes.
* It supports the audio waveform display package, jm.gui.wave
 * Written February 2004
* @author Andrew Sorensen and Andrew Brown
*/

public class WaveFileReader implements jm.JMC{
    private File file;
    private AudioFileFormat fileFormat;
    private AudioFormat format;
    /** The numnber of bits that represent one sample, e.g. 1 = 8 bits, 2 = 16 bits etc.*/
    private int bits;
    /* The file format name, e.g. WAV, AIFF, AU */
    private String fileType;
    /** Should we be cacheing the files audio data */
    private boolean cache;
    /** Duration of the sound file in samples */
    private long duration;
    /** Input Stream */
    private InputStream is;
    /** Play a whole file, or only note length? */
    private boolean wholeFile = false;
    /** Endianess */
    private boolean bigEndian;
    /** The number of channels */
    private int channels;
    private int sampleRate;
       
    /** 
    * Read in the data from the specified file as input to
    * an audio ren dering process.
    * @param inst - The instrument for which this audio object is a part.
    * @param fileName - The name of the file to be used.
    * @param cache - A flag to say weather or not to hold sample data in memory.
    * @param wholeFile - A flag to indicate weather the file should be played all the way through, regardless of the note duration.
    * @param loop - The number of time to reapeatedly playback the file (0 is no loop, -1 is infinite).
    */
    public WaveFileReader(String fileName) {
        try{
            this.file = new File(fileName);
            this.fileFormat = AudioSystem.getAudioFileFormat(this.file);
            this.format = this.fileFormat.getFormat();
            bigEndian = this.format.isBigEndian();
            channels = format.getChannels();
            sampleRate = (int)format.getSampleRate();
            this.duration = (long)this.fileFormat.getFrameLength() * this.channels;
            this.bits = (format.getSampleSizeInBits())/8;
            fileType = fileFormat.toString();
            this.is = AudioSystem.getAudioInputStream(this.file);
        }catch(UnsupportedAudioFileException uafe) {
            //??
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }    
    
    public void finalize() {
        try{
            this.is.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /** 
    * Provide an array of samples from a specific sample start point.
    * @param segmentSize - The size of the array of samples to return
    * @param startPos - The number of samples from the beginning of the file to
    *   start reading from.
    * @return An array of samples as floats.
    */
    public float[] getSamples(int segmentSize, int startPos) {
        float[] samples = new float[segmentSize];
        // reset to start of file
        try{
            this.is = AudioSystem.getAudioInputStream(this.file);
            // jump to the read start location
            this.is.read(new byte[startPos * this.bits]);
            //read the data
            byte[] byteArray = new byte[this.bits * segmentSize];
            this.is.read(byteArray);
            // process into floats
            for (int i=0, k=0; i<segmentSize; i++ ) {
                byte[] tempBytes = new byte[this.bits];
                for(int j=0; j<this.bits; j++) {
                    tempBytes[j] = byteArray[k++];
                }
                samples[i] = getFloat(tempBytes);
            }
        }catch(UnsupportedAudioFileException uafe) {
            System.out.println("jMusic WaveFileReader error: This file format is not supported.");
            System.exit(0);
        }catch(IOException  ioe) {
            ioe.printStackTrace();
        }
        return samples;
    }
    
    
    /*
    * Returns the number of samples in one track of the file.
    * i.e., the wave length, or size.
    */
    public int getWaveSize() {
        return (int)(duration / channels);
    }
    
    /**
    * Return the length of the sample in Bytes.
    */
    public int getNumOfBytes() {
        return (int)(this.duration * this.bits);
    }
    
    /**
    * Provide the bits value of this file as the number of bytes per sample.
     * @return The number of bytes per sample, 1 = 8 bit, 2 = 16 bit, etc.
     */
    public int getBits() {
        return this.bits;
    }
    
    /**
    * Return the number of channels in the audio file
     */
    public int getChannels() {
        return this.channels;
    }
    
    /**
    * Return the sample rate of the current audio file
     */
    public int getSampleRate() {
        return this.sampleRate;
    }
    
    /**
    * Provide the bit size of the current audio file.
    * @return The bit depth, 8, 16, 24, or 32.
     */
    public int getBitResolution() {
        int depth = -1;
        switch (this.bits) {
            case 1:
                depth = 8;
                break;
            case 2:
                depth = 16;
                break;
            case 3:
                depth = 24;
                break;
            case 4:
                depth = 32;
                break;
        }
        return depth;
    }    
    
    /**
    * BigEndian conversion 
    */
    private float getFloat(byte[] b) {
        float sample = 0.0f;
        int ret = 0;
        int length = b.length;
        for(int i=0;i<b.length;i++,length--) {
            ret |= ((int)(b[i] & 0xFF) << ((((bigEndian) ? length : (i+1)) * 8) - 8));
        }
        switch(bits) {
        case 1:
            if(ret > 0x7F){
                ret = ~ret + 1;
                ret &= 0x7F;
                ret = ~ret + 1;
            }
            sample = (float)((float)ret/(float)Byte.MAX_VALUE);
            break;
        case 2:
            if(ret > 0x7FFF) {
                ret = ~ret + 1;
                ret &= 0x7FFF;
                ret = ~ret + 1;
            }
            sample = (float)((float)ret/(float)Short.MAX_VALUE);
            break;
        case 3:
            if(ret > 0x7FFFFF) {
                ret = ~ret + 1;
                ret &= 0x7FFFFF;
                ret = ~ret + 1;
            }
            sample = (float)((float)ret/8388608f);
            break;
        case 4:
            sample = (float)((float)ret/(float)Integer.MAX_VALUE);
            break;
        default:
            System.err.println("Format not accepted");
        }
        return sample;
    }
}
