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

package jm.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

// inspired by http://www.developer.com/java/other/article.php/2173111

   class AudioFilePlayThread extends Thread {
		byte tempBuffer[] = new byte[1024];
		private AudioInputStream audioInputStream;

		public AudioFilePlayThread(AudioInputStream audioInputStream) {
			this.audioInputStream = audioInputStream;	
		}

		public void run(){
	    	try{
				AudioFormat audioFormat  = audioInputStream.getFormat();
				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
				SourceDataLine sourceDataLine =(SourceDataLine)AudioSystem.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();

				int cnt;
				while((cnt = audioInputStream.read(tempBuffer,0,tempBuffer.length)) != -1){
		    		if(cnt > 0){
						sourceDataLine.write(tempBuffer, 0, cnt);
		    		}
				}
				sourceDataLine.drain();
				sourceDataLine.stop();
				sourceDataLine.close();
				sourceDataLine.close();
				audioInputStream.close();
			} catch (Exception e) {
				System.out.println("jMusic AudioFilePlayThread error");
				e.printStackTrace();
			}
		}
	}