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

package jm.gui.sketch;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;
import jm.music.data.*;
import jm.util.Write;
import jm.midi.*;

/**
* A jMusic tool which displays a score as a simple
* 'piano roll' display in a window.
* The tool displays a jMusic class as a simple piano roll view. To use it write:
* new ViewScore(scoreName);
* Where scoreName is the jMusic Score object.
* Alternately:
* new ViewScore(scoreName, xpos, ypos);
* Where xpos and ypos are intergers specifying the topleft position of the window. 
* This is useful if you want to use ViewScore in conjunction with some other GUI interface 
* which is already positioned in the top left corner of the screen.
* @author Andrew Brown and Andrew Troedson
 * @version 1.0,Sun Feb 25 18:43
*/

public class SketchScore extends Frame implements WindowListener, ActionListener {
	//attributes
	private static int maxWidth;
	private static int maxParts;
	protected static Score score;
	protected double beatWidth = 10.0;
	private Panel pan;
	private SketchScoreArea sketchScoreArea;
	private SketchRuler ruler;
	private MenuItem play, speedUp, slowDown, clear, saveMIDI, quit, openMIDI, openXML, saveXML;
	
	//--------------
	//constructors
	//--------------
	public SketchScore(Score score) {
		this(score, 0, 0);
	}
	
	public SketchScore(Score score, int xPos, int yPos) {
		super("jMusic Sketch: '" + score.getTitle() + "'");
		this.score = score;
		this.getWidthAndParts();
		
		//register the closebox event
		this.addWindowListener(this);
		
                pan = new Panel();
		pan.setLayout(new BorderLayout());
		sketchScoreArea = new SketchScoreArea(score, maxWidth, beatWidth);
                sketchScoreArea.setSketchScore(this);
		pan.add("Center", sketchScoreArea);
		
		//add a ruler
		ruler = new SketchRuler(this);
		pan.add("South", ruler);
                
                //add a scroll pane
		ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
		sp.getHAdjustable().setUnitIncrement(20); //set scroll speed
		sp.add(pan);
		this.add(sp);

		// menus
		MenuBar menus = new MenuBar();
		Menu fileMenu  = new Menu("Sketch", true);
                                
                play = new MenuItem("Play @ "+score.getTempo()+" bpm", new MenuShortcut(KeyEvent.VK_P));
		play.addActionListener(this);
		fileMenu.add(play);
                
                speedUp = new MenuItem("Speed Up");
		speedUp.addActionListener(this);
		fileMenu.add(speedUp);

                slowDown = new MenuItem("Slow Down");
		slowDown.addActionListener(this);
		fileMenu.add(slowDown);
                
                clear = new MenuItem("Clear notes");
		clear.addActionListener(this);
		fileMenu.add(clear);
                
                MenuItem dash = new MenuItem("-");
                fileMenu.add(dash);
                
                openMIDI = new MenuItem("Open a MIDI file...", new MenuShortcut(KeyEvent.VK_O));
		openMIDI.addActionListener(this);
		fileMenu.add(openMIDI);
                
                openXML = new MenuItem("Open a jMusic XML file...");
		openXML.addActionListener(this);
		fileMenu.add(openXML);

		saveMIDI = new MenuItem("Save as MIDI file", new MenuShortcut(KeyEvent.VK_S));
		saveMIDI.addActionListener(this);
		fileMenu.add(saveMIDI);
                
                saveXML = new MenuItem("Save as a jMusic XML file");
		saveXML.addActionListener(this);
		fileMenu.add(saveXML);

		quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
		quit.addActionListener(this);
		fileMenu.add(quit);

		menus.add(fileMenu);
		this.setMenuBar(menus);
		
		//construct and display						
		//this.pack();
                this.setSize(650, sketchScoreArea.getHeight() + ruler.getHeight());
		this.setLocation(xPos,yPos);							
		this.show();
	}
	
	//--------------
	// Class Methods
	//--------------
	
        
        public SketchScoreArea getSketchScoreArea() {
            return sketchScoreArea;
        }
        
	// Deal with the window closebox
	public void windowClosing(WindowEvent we) {
		this.dispose(); //System.exit(0);
	}
	//other WindowListener interface methods
	//They do nothing but are required to be present
	public void windowActivated(WindowEvent we) {};
	public void windowClosed(WindowEvent we) {};
	public void windowDeactivated(WindowEvent we) {};
	public void windowIconified(WindowEvent we) {};
	public void windowDeiconified(WindowEvent we) {};
	public void windowOpened(WindowEvent we) {};
	
        /**
        * recalulate and draw the sketch
        */
	public void update() {
            sketchScoreArea.setScore(score);
            pan.repaint();
            sketchScoreArea.setSize( (int)Math.round( score.getEndTime()*beatWidth), sketchScoreArea.getHeight());
            sketchScoreArea.setBeatWidth(beatWidth);
            sketchScoreArea.repaint();
            ruler.repaint();
            //this.repaint();
            this.setSize(this.getSize().width, sketchScoreArea.getHeight() + ruler.getHeight());
            this.pack();
        }
        
	private void getWidthAndParts() {
            Enumeration enum1 = score.getPartList().elements();
            while(enum1.hasMoreElements()){
                Part part = (Part) enum1.nextElement();
                maxParts++;
                Enumeration enum2 = part.getPhraseList().elements();
                while(enum2.hasMoreElements()){
                    Phrase phrase = (Phrase) enum2.nextElement();
                    Enumeration enum3 = phrase.getNoteList().elements();
                    maxWidth = (int) (phrase.getStartTime() * beatWidth);
                    while(enum3.hasMoreElements()){
                        Note aNote = (Note) enum3.nextElement();
                        maxWidth = maxWidth + (int)(aNote.getRhythmValue() * beatWidth);
                    }
                }
            }
	}

	// handle menu items
	public void actionPerformed(ActionEvent e){
            if(e.getSource() == play) playScore();
            if(e.getSource() == speedUp) speedItUp();
            if(e.getSource() == slowDown) slowItDown();
            if(e.getSource() == clear) clearNotes();
            if(e.getSource() == quit) System.exit(0);
            if(e.getSource() == saveMIDI) saveMidi();
            if(e.getSource() == openMIDI) openMidi();
            if(e.getSource() == saveXML) saveXMLFile();
            if(e.getSource() == openXML) openXMLFile();
	}
        
        private void playScore() {
            MidiSynth ms = new MidiSynth();
            try {
                ms.play(score);
            } catch (Exception e) {
                System.err.println("MIDI Playback Error:" + e);
                return;
            }
        }
        
        private void speedItUp() {
            double tempTempo = score.getTempo() + 10.0;
            if (tempTempo > 250.0) tempTempo = 250.0;
            score.setTempo(tempTempo);
            play.setLabel("Play @ "+tempTempo+" bpm");
        }
        
         private void slowItDown() {
            double tempTempo = score.getTempo() - 10.0;
            if (tempTempo < 20.0) tempTempo = 20.0;
            score.setTempo(tempTempo);
            play.setLabel("Play @ "+tempTempo+" bpm");
        }
        
        private void clearNotes() {
            score.removeAllParts();
            sketchScoreArea.repaint();
        }

	/**
        * Dialog to save score as a MIDI file.
	 */
	public void saveMidi() {
		FileDialog fd = new FileDialog(this, "Save score as a MIDI file ...", FileDialog.SAVE);
        fd.setFile("FileName.mid");
		fd.show();
		//write a MIDI file to disk
		if ( fd.getFile() != null) {
                    Write.midi(score,fd.getDirectory()+fd.getFile());
		}
	}
        
        /**
        * Save score as a jMusic XML file.
        */
        public void saveXMLFile() {
            FileDialog fd = new FileDialog(new Frame(), 
                "Save as a jMusic XML file...", 
                FileDialog.SAVE);
           fd.setFile("FileName.xml");
           fd.show();
            if (fd.getFile() != null) {
                jm.util.Write.xml(score, fd.getDirectory() + fd.getFile());
            }
        }
        
        /**
        * Read a MIDI file and display its data.
        */
        public void openMidi() {
            FileDialog fd = new FileDialog(new Frame(), 
                "Select a MIDI file to display.", 
                FileDialog.LOAD);
            fd.show();
            String fileName = fd.getFile();
            if (fileName != null) {
                Score score = new Score();
                jm.util.Read.midi(score, fd.getDirectory() + fileName);
                this.score = score;
                update();
            }
        }
        
        /**
        * Read a jMusic XML file and display its data.
        */
        public void openXMLFile() {
            FileDialog fd = new FileDialog(new Frame(), 
                "Select a jMusic XML file to display.", 
                FileDialog.LOAD);
            fd.show();
            String fileName = fd.getFile();
            if (fileName != null) {
                Score score = new Score();
                jm.util.Read.xml(score, fd.getDirectory() + fileName);
                this.score = score;
                update();
            }
        }
}
