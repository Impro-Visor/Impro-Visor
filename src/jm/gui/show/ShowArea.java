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

/**
* A java canvas object which displays a score as a
* psudo Common Practice Notation in a window.
* Used as part of jMusic ShowScore, and other apps.
* @author Andrew Brown 
*/
package jm.gui.show;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;
import jm.music.data.*;
import jm.midi.*;

//--------------
//second class!!
//--------------
public class ShowArea extends Canvas {
	//attributes
	private int oldX;
	private int maxColours = 8;
	private float[][] theColours = new float[maxColours][3];
        private int noteHeight = 5;
        private int w = 2 * noteHeight; //width between stave lines
        private int ePos = 5 * noteHeight; // position of e in the treble stave
        private int e = ePos + noteHeight * 33;
        private int areaHeight = 77 * noteHeight;
	private int[] noteOffset = {0,0,noteHeight,noteHeight,noteHeight*2,
            noteHeight*3,noteHeight*3,noteHeight*4,noteHeight*4,
            noteHeight*5,noteHeight*5,noteHeight*6};
	private Font font = new Font("Helvetica",Font.PLAIN, 10);
	private ShowPanel sp;
        private double beatWidth;
        private int thinNote = 2; // thin value
	
	public ShowArea(ShowPanel sp){
		super();
		this.sp = sp;
		//width and height of score notation area
		this.setSize((int)(sp.score.getEndTime()*sp.beatWidth),areaHeight);
		
		for (int i=0; i<maxColours;i++) {
			theColours[i][0] = (float)(Math.random()/ maxColours/2) + (float)(1.0/ maxColours *i);
			theColours[i][1] = (float)(Math.random()/ maxColours) + (float)(1.0/ maxColours *i);
		}
	}
        
        private void reInit() {
            w = 2 * noteHeight; //width between stave lines
            ePos = 5 * noteHeight; // position of e in the treble stave
            e = ePos + noteHeight * 33;
            areaHeight = 77 * noteHeight;
            noteOffset = new int[] {0,0,noteHeight,noteHeight,noteHeight*2,
            noteHeight*3,noteHeight*3,noteHeight*4,noteHeight*4,
            noteHeight*5,noteHeight*5,noteHeight*6};
            this.setSize(new Dimension(this.getSize().width, areaHeight));
            sp.updatePanelHeight();
        }
        
        /**
        * Report the current height of this canvas.
        */
        public int getHeight() {
            return areaHeight;
        }
        
        /**
        * Specify the size of the notation.
        * @param int The new note height
        */
        public void setNoteHeight(int val){
            noteHeight = val;
            reInit();
            repaint();
        }
        
        /**
        * Display notes thinner than stave width or not.
        * @param int The thinNote value ( 0, 1, 2, 3  etc.)
        */
        public void setThinNote(int newVal){
            if (newVal >= 0) thinNote = newVal;
            repaint();
        }
        
        /**
        * Returns the current value of the booean variable thinNote.
        */
        public int getThinNote() {
            return thinNote;
        }
	
	//public void update(Graphics g) {
	//	paint(g);
	//}
	
	public void paint(Graphics offScreenGraphics) {
	    //Image offScreenImage = this.createImage(this.getSize().width, this.areaHeight);
		//Graphics offScreenGraphics = g.create();
                //offScreenImage.getGraphics();
		int rectLeft, rectTop, rectRight, rectBot;
		//clear
		offScreenGraphics.setColor(Color.white);
		offScreenGraphics.fillRect(0, 0, this.getSize().width, this.getSize().height);
		//get current maxWidth
		offScreenGraphics.setFont(font);
		//paint staves
		offScreenGraphics.setColor(Color.black);
		// e above middle C is at 255
		//treble
                beatWidth = sp.beatWidth;
		int maxWidth = (int)Math.round(sp.score.getEndTime()*beatWidth);		
		offScreenGraphics.drawLine(0,(e),maxWidth,(e));
		offScreenGraphics.drawLine(0,(e - w),maxWidth,(e - w));
		offScreenGraphics.drawLine(0,(e - w*2),maxWidth,(e - w*2));
		offScreenGraphics.drawLine(0,(e - w*3),maxWidth,(e - w*3));
		offScreenGraphics.drawLine(0,(e - w*4),maxWidth,(e - w*4));
		//bass
		offScreenGraphics.drawLine(0,(e + w*2),maxWidth,(e + w*2));
		offScreenGraphics.drawLine(0,(e + w*3),maxWidth,(e + w*3));
		offScreenGraphics.drawLine(0,(e + w*4),maxWidth,(e + w*4));
		offScreenGraphics.drawLine(0,(e + w*5),maxWidth,(e + w*5));
		offScreenGraphics.drawLine(0,(e + w*6),maxWidth,(e + w*6));
		// upper treble
		offScreenGraphics.setColor(Color.lightGray);
		offScreenGraphics.drawLine(0,(e - w*7),maxWidth,(e - w*7));
		offScreenGraphics.drawLine(0,(e - w*8),maxWidth,(e - w*8));
		offScreenGraphics.drawLine(0,(e - w*9),maxWidth,(e - w*9));
		offScreenGraphics.drawLine(0,(e - w*10),maxWidth,(e - w*10));
		offScreenGraphics.drawLine(0,(e - w*11),maxWidth,(e - w*11));
		//lower bass
		offScreenGraphics.drawLine(0,(e + w*9),maxWidth,(e + w*9));
		offScreenGraphics.drawLine(0,(e + w*10),maxWidth,(e + w*10));
		offScreenGraphics.drawLine(0,(e + w*11),maxWidth,(e + w*11));
		offScreenGraphics.drawLine(0,(e + w*12),maxWidth,(e + w*12));
		offScreenGraphics.drawLine(0,(e + w*13),maxWidth,(e + w*13));
		// leger lines
		for (int k=0; k<maxWidth; k+=10) {
			offScreenGraphics.drawLine(k,(e + w),k+1,(e + w)); // middle C
			// above treble
			offScreenGraphics.drawLine(k,(e - w*5),k+1,(e - w*5));
			offScreenGraphics.drawLine(k,(e - w*6),k+1,(e - w*6));
			// above upper treble
			offScreenGraphics.drawLine(k,(e - w*12),k+1,(e - w*12));
			offScreenGraphics.drawLine(k,(e - w*13),k+1,(e - w*13));
			offScreenGraphics.drawLine(k,(e - w*14),k+1,(e - w*14));
			offScreenGraphics.drawLine(k,(e - w*15),k+1,(e - w*15));
			offScreenGraphics.drawLine(k,(e - w*16),k+1,(e - w*16));
			offScreenGraphics.drawLine(k,(e - w*17),k+1,(e - w*17));
			offScreenGraphics.drawLine(k,(e - w*18),k+1,(e - w*18));
			// below bass
			offScreenGraphics.drawLine(k,(e + w*7),k+1,(e + w*7));
			offScreenGraphics.drawLine(k,(e + w*8),k+1,(e + w*8));
			// below lower bass
			offScreenGraphics.drawLine(k,(e + w*14),k+1,(e + w*14));
			offScreenGraphics.drawLine(k,(e + w*15),k+1,(e + w*15));
			offScreenGraphics.drawLine(k,(e + w*16),k+1,(e + w*16));
			offScreenGraphics.drawLine(k,(e + w*17),k+1,(e + w*17));
			offScreenGraphics.drawLine(k,(e + w*18),k+1,(e + w*18));
		}
		//Paint each phrase in turn
		Enumeration enum1 = sp.score.getPartList().elements();
		int i = 0;
		while(enum1.hasMoreElements()){
                    Part part = (Part) enum1.nextElement();
                    
                    Enumeration enum2 = part.getPhraseList().elements();
                    while(enum2.hasMoreElements()){
                        Phrase phrase = (Phrase) enum2.nextElement();
                        
                        Enumeration enum3 = phrase.getNoteList().elements();
                        double oldXBeat = phrase.getStartTime();
                        oldX = (int) (Math.round (oldXBeat * beatWidth));
                        // calc the phrase rectangles
                        rectLeft = oldX;
                        rectTop = 100000;
                        rectRight = oldX;
                        rectBot = 0;
                        
                        while(enum3.hasMoreElements()){
                            Note aNote = (Note) enum3.nextElement();
                            int currNote = -1;
                            if (aNote.getPitchType() == Note.MIDI_PITCH) currNote = aNote.getPitch();
                            else currNote = Note.freqToMidiPitch(aNote.getFrequency());
                            if ((currNote <= 127) && (currNote >= 0)) {
                                    // 10 - numb of octaves, 12 notes in an octave, 21 
                                    // (octavePixelheight) is the height of 
                                    // an octave, 156 is offset to put in position
                                    int octavePixelheight = noteHeight * 7;
                                    int y = (int)(((10 - currNote / 12) * octavePixelheight + 
                                            (ePos)) - noteOffset[currNote % 12]);
                                    int x = (int)(Math.round(aNote.getDuration() * beatWidth)); //480 ppq note
                                    int xRV = (int)(Math.round(aNote.getRhythmValue() * beatWidth)); //480 ppq note
                                    // check if the width of the note is less than 1 so 
                                    // that it will be displayed
                                    if (x<1) x=1;
                                    if(y<rectTop) rectTop = y;
                                    if(y>rectBot) rectBot = y;//update values to phrase rectangle
                                    //set the colour change brightness for dynamic
                                    offScreenGraphics.setColor(Color.getHSBColor(theColours[i% maxColours][0],
                                            theColours[i% maxColours][1],
                                            (float)(0.7-(aNote.getDynamic()*0.004))));
                                    // draw note inside
                                    if (aNote.getPitchType() == Note.MIDI_PITCH) {
                                        offScreenGraphics.fillRect(oldX,y-noteHeight + thinNote,x,
                                                        noteHeight * 2 - 2 * thinNote );
                                    } else { // draw frequency derrived note
                                        int heightOffset = 7;
                                        for(int j=oldX; j<oldX + x - 4; j+=4) {
                                                offScreenGraphics.drawLine(j, y-noteHeight + heightOffset,
                                                    j+2, y-noteHeight + heightOffset - 3);
                                                offScreenGraphics.drawLine(j+2, y-noteHeight + heightOffset - 3,
                                                    j+4, y-noteHeight + heightOffset);
                                        }
                                    }
                                    
                                    // draw note ouside
                                    offScreenGraphics.setColor(Color.getHSBColor(theColours[i% maxColours][0],
                                            theColours[i% maxColours][1],(float)(0.4)));
                                    offScreenGraphics.drawRect(oldX,y-noteHeight+ thinNote,xRV,
                                            noteHeight * 2 - 2 * thinNote);
                                    //add a sharp if required
                                    if ((currNote % 12) == 1 || (currNote % 12) == 3 || 
                                            (currNote % 12) == 6 || (currNote % 12) == 8 || 
                                            (currNote % 12) == 10) { 
                                                    offScreenGraphics.setColor(Color.getHSBColor(
                                                            theColours[i% maxColours][0], 
                                                            theColours[i% maxColours][1],(float)(0.3)));
                                                    offScreenGraphics.drawString("#", oldX - 7, y + 5);
                                            }
                                    }
                                    oldXBeat += aNote.getRhythmValue();
                                    oldX = (int) (Math.round (oldXBeat * beatWidth));
                                    rectRight = oldX - rectLeft; //update value for phrase rectangle
                        }
                        // draw the phrase rectangle
                        //offScreenGraphics.setColor(Color.lightGray);
                        offScreenGraphics.setColor(Color.getHSBColor(theColours[i% maxColours][0], 
                            theColours[i% maxColours][1],(float)(0.9)));
                        offScreenGraphics.drawRect(rectLeft - 1, rectTop-noteHeight - 1, rectRight+1, 
                            rectBot - rectTop + noteHeight * 2 + 2);
                    }
                    i++; //increment the part index
		}
		//g.drawImage(offScreenImage, 0, 0, this);
		//offScreenGraphics.dispose();
	}
}
