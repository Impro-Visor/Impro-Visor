/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:42  2001

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

package jm.gui.sketch;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;
import jm.music.data.*;
import jm.music.tools.Mod;
import jm.midi.*;

public class SketchScoreArea extends Canvas implements JMC, KeyListener, MouseListener, MouseMotionListener{
	//attributes
	private Score score;
        private int scoreChannels;
        private int currentChannel;
	private int oldY = 0;
	private Color[] theColors = new Color[10];
	private int maxWidth;
	//private int maxParts;
	private double beatWidth;
	private int x, y;
        private int newWidth = 650;
	private Vector drawPoints = new Vector();
	private int myHeight = 127;
        private SketchScore sc;
		
	public SketchScoreArea(Score score, int maxWidth, double beatWidth) {
		super();
		this.score = score;
                scoreChannels = score.size();
                currentChannel = scoreChannels;
		this.maxWidth = maxWidth;
		this.beatWidth = beatWidth;
		//this.maxParts = score.size();
		
		this.setSize(maxWidth, myHeight);
		this.score = score;
		this.maxWidth = maxWidth;
		//register the keyboard listener
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setBackground(new Color(250, 250, 250));
		
		//set up a set of colours to use
		for (int i=0; i<10;i++) {
			Color colour = new Color((int)(Math.random()*256),
				(int)(Math.random()*256),
				(int)(Math.random()*256));
			theColors[i] = colour;
		}
	}
	/**
	* Update the beatWidth of the panel
	*/
	public void setBeatWidth(double beatWidth) {
	    this.beatWidth = beatWidth;
	}
	/**
	* Update the score of this panel
	*/
	public void setScore(Score score) {
	    this.score = score;
	}
        
        /**
        * Report on the set height of this panel
        */
        public int getHeight() {
            return myHeight;
        }
        
        /**
        * Register a sketch frame.
        */
        public void setSketchScore(SketchScore sc) {
            this.sc = sc;
        }
        
        /*
        * Returns the starting and then maximum panel width
        */
        public int getNewWidth() {
            return newWidth;
        }
	
	/**
	* Draw the panel
	*/
	public void paint(Graphics g) {
		score.clean();  // erase any empty data elements
		//Paint each phrase in turn
		Enumeration enum1 = score.getPartList().elements();
		int i = 0;
		// resize
		//if( score.getEndTime() > 0.0 && newWidth < (int)(score.getEndTime()* beatWidth)) {
		//	this.setSize((int)(score.getEndTime()* beatWidth), myHeight);
		//} else {
		//	this.setSize(newWidth, myHeight); 
		//} 
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
			//Make each part a different colour
			g.setColor(theColors[i%10]);
			i++;
			
			Enumeration enum2 = part.getPhraseList().elements();
			while(enum2.hasMoreElements()){
				Phrase phrase = (Phrase) enum2.nextElement();
				Enumeration enum3 = phrase.getNoteList().elements();
				double oldStartTime = phrase.getStartTime();
				
				while(enum3.hasMoreElements()){
					Note aNote = (Note) enum3.nextElement();
					// avoid rests and draw notes
					int currNote = -1;
					if (aNote.getPitchType() == Note.MIDI_PITCH) currNote = aNote.getPitch();
					else currNote = Note.freqToMidiPitch(aNote.getFrequency());
					if( currNote != REST) {
					    int x = 127 - currNote;
					    int y = (int) Math.round(aNote.getDuration() * beatWidth);
					    int oldY = (int) Math.round(oldStartTime * beatWidth);
					    g.drawLine(oldY,x,oldY+y,x);
					}
					oldStartTime += aNote.getRhythmValue();
				}
			}			
		}
		// draw new arc
		g.setColor(Color.black);
		Enumeration drawEnum = drawPoints.elements();
		while(drawEnum.hasMoreElements()) {
		    int x1 = ((Integer)(drawEnum.nextElement())).intValue();
		    int y1 = ((Integer)(drawEnum.nextElement())).intValue();
		    int x2 = ((Integer)(drawEnum.nextElement())).intValue();
		    int y2 = ((Integer)(drawEnum.nextElement())).intValue();
		    //System.out.println("Values are "+x1+" "+y1+" "+x2+" "+y2);
		    g.drawLine(x1, y1, x2, y2);
		}
	}
	// key listener stubs
	public void keyPressed(KeyEvent e) { }
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == '\b') {
		    if(score.getPart(0).size() < 1) return;
		    Vector v = score.getPartList();
		    v.removeElementAt(v.size() -1); // last part
		    if (sc == null) {
                        repaint();
                    } else sc.update();
		    newWidth = 50;
		}
	}
    
    // Mouse Listener stubs
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	//mouseMotionListener stubs
	public void mouseMoved(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
	    x = e.getX();
	    y = e.getY();
	}
	
	public void mouseDragged(MouseEvent e) {
	    // start loc
	    drawPoints.addElement(new Integer(x));
	    drawPoints.addElement(new Integer(y));
	    // current loc
	    drawPoints.addElement(new Integer(e.getX()));
	    drawPoints.addElement(new Integer(e.getY()));
	    // update old locations
	    x = e.getX();
	    y = e.getY();
	    // check if off the canvas
	    if(e.getX() > (int)(score.getEndTime()* beatWidth) && e.getX() > newWidth) newWidth = e.getX();
	    // paint
	    repaint();
	    
	}
	
	public void mouseReleased(MouseEvent e) {
	    //System.out.println(e.getModifiers());
	    // convert line to phrase then add to the score
	    if(drawPoints.size() > 0 ) {
	        if( e.getModifiers() == 24){convertLineToPhrase(true);} // option key held down?
	        else convertLineToPhrase(false);
	    }
        drawPoints.removeAllElements();
        newWidth = 50;
        repaint();
	}
	
	private void convertLineToPhrase(boolean hiRes) {
	    Phrase phr = null;
	    double[][] storer = new double[drawPoints.size()/4][3];
	    Enumeration drawEnum = drawPoints.elements();
	    int counter = 0;
		while(drawEnum.hasMoreElements()) {
		    int x1 = ((Integer)(drawEnum.nextElement())).intValue();
		    int y1 = ((Integer)(drawEnum.nextElement())).intValue();
		    int x2 = ((Integer)(drawEnum.nextElement())).intValue();
		    int y2 = ((Integer)(drawEnum.nextElement())).intValue();
		    //System.out.println("Values are "+x1/beatWidth+" "+y1 /beatWidth+" "+x2 /beatWidth+" "+y2 /beatWidth);
		    if(hiRes) { // was the option key held down
			    // fine grade
			    for(int i=0; i < Math.abs(y1-y2); i++) {
			        storer[counter][0] = (double)(((Integer)drawPoints.elementAt(counter * 4)).
                                    intValue()) / beatWidth + 
					( (double)Math.abs(x2-x1)/beatWidth / (double)(Math.abs(y1-y2)) + 1.0) / 
                                        (Math.abs(y1-y2) + 1); //onset time
			            if ( (int)((Integer)drawPoints.elementAt(counter * 4)).intValue() >
						(int)((Integer)drawPoints.elementAt(counter * 4 +2)).intValue() ) {
				        //revised onset time for drawing backwards
				        storer[counter][0] = (double)(((Integer)drawPoints.elementAt(counter * 4 + 2)).
                                            intValue()) / beatWidth / (double)(Math.abs(y1-y2)) + 1.0;
				    }
                                    //pitch
				    storer[counter][1] = (double)(127-y1 +i); 
                                    //duration
                                    storer[counter][2] = (double)Math.abs(x2-x1)/beatWidth / 
                                        (double)(Math.abs(y1-y2)) + 1.0; 
			    }
			} else {
			    
			    // course grade
			    storer[counter][0] = (double)(((Integer)drawPoints.elementAt(counter * 4)).
                                intValue()) / beatWidth; //onset time
			    if ( (int)((Integer)drawPoints.elementAt(counter * 4)).intValue() > 
                                    (int)((Integer)drawPoints.elementAt(counter * 4 +2)).intValue() ) {
                                //revised onset time for drawing backwards
			        storer[counter][0] = (double)(((Integer)drawPoints.elementAt(counter * 4 + 2)).
                                    intValue()) / beatWidth;
			    }
			    storer[counter][1] = (double)(127-y1); //pitch
                            storer[counter][2] = (double)Math.abs(x2-x1)/beatWidth; //duration
			}
		    counter++;
		}
		// sort notes into onset order
		quickSort(storer, 0, storer.length - 1);
		
		// put notes into a phrase
		if(drawPoints.size() > 0 ) phr = new Phrase(storer[0][0]);
		for(int i=0; i< storer.length - 1; i++) { 
                    // prevent out of range notes being generated at the edges
                    if(storer[i][1] < 0) storer[i][1] = 0;
                    if(storer[i][1] > 127) storer[i][1] = 127;
		    Note n = new Note((int)storer[i][1], storer[i+1][0] - storer[i][0]);
			// avoid unnecessary repeat notes
			if ( i > 0 && n.getPitchType() == Note.MIDI_PITCH &&
			    ((Note)(phr.getNoteList().lastElement())).getPitchType() == Note.MIDI_PITCH) {
				if(phr.size() > 0 && n.getPitch() ==
                                    ((Note)(phr.getNoteList().lastElement())).getPitch()) {
					    Mod.append(((Note)(phr.getNoteList().lastElement())), n);
				} else {
					phr.addNote(n);
				}
			} else phr.addNote(n);
		}
		// last note
                if(storer[ storer.length - 1][1] < 0) storer[ storer.length - 1][1] = 0;
                if(storer[ storer.length - 1][1] > 127) storer[ storer.length - 1][1] = 127;
		Note n = new Note((int)storer[ storer.length - 1][1], storer[ storer.length - 1][2]);
		n.setDuration(storer[ storer.length - 1][2]);
		phr.addNote(n);
		
		// add phrase to the score as a new part
		if (phr != null) {
                    currentChannel++;
                    if (currentChannel > 15)  currentChannel = scoreChannels;
		    Part p = new Part("Sketch Part", 0, currentChannel);
		    p.addPhrase(phr);
		    score.addPart(p);
		}
	}
	
	private void quickSort(double[][] storer, int left, int right) {
	    int i, last;
	    if(left >= right) return; // already sorted
	    swap( storer, left, (int)(Math.random() * (right - left)) + left); // choose new pivot point
	    last = left;
	    for (i = left+1; i<=right; i++) {
	        if( storer[i][0] <= storer[left][0]) swap( storer, ++last, i);
	    }
	    swap( storer, left, last); // restore pivot
	    quickSort( storer, left, last-1);
	    quickSort( storer, last+1, right);
	}
	
	static void swap(double[][] storer, int i, int j) {
	    double temp;
	    for(int a=0;a<3;a++){
	        temp = storer[i][a];
	        storer[i][a] = storer[j][a];
	        storer[j][a] = temp;
	    }
	}
} 
