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

package jm.audio.io;

import java.io.*;
import jm.audio.AOException;

import javax.sound.sampled.*;
import java.io.InputStream;

/**
* Audio file writing class for jMusic.
* The class utilises the JavaSound file writing classes.
* It works indepdently of the jMusic audio architecture used
* for rendering scores, and is intended for use by simple file
* manipulation utility programs operating asychronously (non real time).
* @author Andrew Brown
*/

public class AudioFileOut {
    // the name of the file to read from.
    private String fileName;
    // the file object to read from
    private File file;
    // audio file type, au or aiff or wav
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.AU;
    // file attributes
    private AudioFormat format;
    // Is the file data little or big endian? 
    // au and aif are big-endian, wav is little-endian
    private boolean bigEndian;
    // the number of channels in the file, 1 == mono, 2 = stereo etc.
    private int channels;
    // The file's sample rate as samples per second.
    private int sampleRate;
    // The number of samples in the file
    private long duration;
    // The number of bytes long each sample is. 1 = 8 bit, 2 = 16 bit etc.
    private int sampleSize;
    // Audio intput stream, will hold the byte array of sample data.
    private AudioInputStream ais;
    // The float Array with the sample data in it
    private float[] sampleData;
    
    public AudioFileOut(float[] sampleData, String fileName) {
        this(sampleData, fileName, 1, 44100, 16);
    }
    
    public AudioFileOut(float[] sampleData, String fileName, int channels, 
                        int sampleRate, int sampleSizeInBits) {
        this.sampleData = sampleData;
        this.duration = sampleData.length;
        this.fileName = fileName;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSizeInBits / 8;
        // choose file type from file name
        if (fileName.endsWith(".au")) {
            fileType = AudioFileFormat.Type.AU;
            this.bigEndian = true;
        } else if (fileName.endsWith(".wav")) {
            fileType = AudioFileFormat.Type.WAVE;
            this.bigEndian = false;
        } else if (fileName.endsWith(".aif") || fileName.endsWith(".aiff")) {
            fileType = AudioFileFormat.Type.AIFF;
            this.bigEndian = true;
        } else { // default
            fileName = fileName + ".au";
            this.bigEndian = true;
        }
        // set up
        this.file = new File(this.fileName);
        // convert floats to bytes
        byte[] tmp = new byte[sampleData.length * this.sampleSize];
        for(int i=0; i<sampleData.length; i++) {
            int ival = -1;
            switch(sampleSize) {
                case 1: // 8 bit
                    tmp[i] = new Float(sampleData[i] * (float)Byte.MAX_VALUE).byteValue();
                    break;
                case 2: // 16 bit
                    short sval = new Float(sampleData[i] * (float)Short.MAX_VALUE).shortValue();
                    if(bigEndian) {
                        tmp[i*2] = (byte) ((sval & 0x0000ff00) >> 8);
                        tmp[i*2+1] = (byte) (sval & 0x000000ff);
                    } else {
                        tmp[i*2] = (byte) (sval & 0x000000ff);
                        tmp[i*2+1] = (byte) ((sval & 0x0000ff00) >> 8);
                    }
                    break;
                case 3: // 24 bit
                    ival = new Float(sampleData[i] * (float)8388608).intValue();    
                    if(bigEndian) {
                        tmp[i*3] = (byte) ((ival & 0x00ff0000) >> (8 * 2));
                        tmp[i*3+1] = (byte) ((ival & 0x0000ff00) >> 8);
                        tmp[i*3+2] = (byte) (ival & 0x000000ff);
                    } else {
                        tmp[i*3] = (byte) (ival & 0x000000ff);
                        tmp[i*3+1] = (byte) ((ival & 0x0000ff00) >> 8);
                        tmp[i*3+2] = (byte) ((ival & 0x00ff0000) >> (8 * 2));
                    }
                    break;   
                case 4: // 32 bit
                    ival = new Float(sampleData[i] * (float)Integer.MAX_VALUE).intValue();    
                    if(bigEndian) {
                        tmp[i*4] = (byte) ((ival & 0xff000000) >> (8 * 3));
                        tmp[i*4+1] = (byte) ((ival & 0x00ff0000) >> (8 * 2));
                        tmp[i*4+2] = (byte) ((ival & 0x0000ff00) >> 8);
                        tmp[i*4+3] = (byte) (ival & 0x000000ff);
                    } else {
                        tmp[i*4] = (byte) (ival & 0x000000ff);
                        tmp[i*4+1] = (byte) ((ival & 0x0000ff00) >> 8);
                        tmp[i*4+2] = (byte) ((ival & 0x00ff0000) >> (8 * 2));
                        tmp[i*4+3] = (byte) ((ival & 0xff000000) >> (8 * 3));
                    }
                    break;
                default:
                    System.err.println("jMusic AudioFileOut error: " +
                                       sampleSizeInBits + 
                                       " bit audio output file format not supported, sorry :(");
                    System.exit(0); // ugly but necessary.
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        // specify file format
        this.format = new AudioFormat(this.sampleRate, sampleSizeInBits, this.channels, true, this.bigEndian);
        AudioInputStream ais = new AudioInputStream(bis, this.format, this.duration / this.channels);
        // writing
        try {
        AudioSystem.write(ais, fileType, file);
        } catch (IOException ioe) {
            System.out.println("error writing audio file.");
        }
    }
}
