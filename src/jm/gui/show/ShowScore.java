/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:39  2001

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


/* --------------------
* A jMusic tool which displays a score as a
* piano roll dispslay on Common Practice Notation staves.
* @author Andrew Brown 
 * @version 1.0,Sun Feb 25 18:43
* ---------------------
*/
package jm.gui.show;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;
import jm.music.data.*;
import jm.util.Write;
import jm.util.Play;
import jm.midi.*;
import java.awt.image.*; //

/**
* The tool displays a jMusic class as music notation. To use it write:
* new ShowScore(scoreName);
* Where scoreName is the jMusic Score object.
* Alternately:
* new ShowScore(scoreName, xpos, ypos);
* Where xpos and ypos are intergers specifying the topleft position of the window. 
* This is useful if you want to use DrawScore in conjunction with some other GUI interface 
* which is already positioned in the top left corner of the screen.
*
*/

public class ShowScore extends Frame implements WindowListener, ActionListener {
	//attributes
	private Panel pan;
	private Score score = new Score();
	private MenuItem saveMIDI, quit, size7, size2, size3, size4, size5, size6, 
            size8, thin, medium, thick, play, saveXML, openXML, openMIDI;
        private ShowPanel sp;
	
	//--------------
	//constructors
	//--------------
	public ShowScore(Score score) {
		this(score, 0, 0);
	}
	
	public ShowScore(Score score, int xPos, int yPos) {
		super("jMusic Show: '" + score.getTitle() + "'");
		this.score = score;
		//register the closebox event
		this.addWindowListener(this);
		
		//add a scroll pane
		sp = new ShowPanel(this, score);
		this.setSize(650,sp.getHeight() + 25);
		
		this.add(sp);
		// menus
		MenuBar menus = new MenuBar();
		Menu fileMenu  = new Menu("Show", true);
		                
        size2 = new MenuItem("Size 2");
		size2.addActionListener(this);
		fileMenu.add(size2);
                
        size3 = new MenuItem("Size 3");
		size3.addActionListener(this);
		fileMenu.add(size3);
                
        size4 = new MenuItem("Size 4");
		size4.addActionListener(this);
		fileMenu.add(size4);
                
        size5 = new MenuItem("Size 5");
		size5.addActionListener(this);
		fileMenu.add(size5);
                
        size6 = new MenuItem("Size 6");
		size6.addActionListener(this);
		fileMenu.add(size6);
                
        size7 = new MenuItem("Size 7");
		size7.addActionListener(this);
		fileMenu.add(size7);
                
        size8 = new MenuItem("Size 8");
		size8.addActionListener(this);
		fileMenu.add(size8);
                
        MenuItem line = new MenuItem("-");
        fileMenu.add(line);
                
        thin = new MenuItem("Thin notes");
		thin.addActionListener(this);
		fileMenu.add(thin);
                
        medium = new MenuItem("Medium notes");
		medium.addActionListener(this);
		fileMenu.add(medium);
                
        thick = new MenuItem("Thick notes");
		thick.addActionListener(this);
		fileMenu.add(thick);

		MenuItem line2 = new MenuItem("-");
		fileMenu.add(line2);

		play = new MenuItem("Play MIDI", new MenuShortcut(KeyEvent.VK_P));
		play.addActionListener(this);
		fileMenu.add(play);
		
		openMIDI = new MenuItem("Open a MIDI file...", new MenuShortcut(KeyEvent.VK_O));
		openMIDI.addActionListener(this);
		fileMenu.add(openMIDI);
                
                openXML = new MenuItem("Open a jMusic XML file...");
		openXML.addActionListener(this);
		fileMenu.add(openXML);
                
		saveMIDI = new MenuItem("Save as MIDI file...", new MenuShortcut(KeyEvent.VK_S));
		saveMIDI.addActionListener(this);
		fileMenu.add(saveMIDI);
                
                saveXML = new MenuItem("Save as jMusic XML file...");
		saveXML.addActionListener(this);
		fileMenu.add(saveXML);
		
		quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
		quit.addActionListener(this);
		fileMenu.add(quit);
		
		menus.add(fileMenu);
		this.setMenuBar(menus);

		//construct and display						
		this.pack();
		this.setLocation(xPos,yPos);							
		this.show();
	}
	
	//--------------
	// Class Methods
	//--------------
	
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

	// handle menu items
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == play) playBackMidi(); 
        if(e.getSource() == quit) System.exit(0);
		if(e.getSource() == saveMIDI) saveMidi();
                if(e.getSource() == openMIDI) openMidi();
		if(e.getSource() == saveXML) saveXMLFile();
		if(e.getSource() == openXML) openXMLFile();
		if(e.getSource() == size2) resize(2);
		if(e.getSource() == size3) resize(3);
		if(e.getSource() == size4) resize(4);
		if(e.getSource() == size5) resize(5);
		if(e.getSource() == size6) resize(6);
		if(e.getSource() == size7) resize(7);
		if(e.getSource() == size8) resize(8);
		if(e.getSource() == thin) sp.getShowArea().setThinNote(2);
		if(e.getSource() == medium) sp.getShowArea().setThinNote(1);
		if(e.getSource() == thick) sp.getShowArea().setThinNote(0);
	}
        
        private void resize(int newSize) {
            sp.getShowArea().setNoteHeight(newSize);
            this.setSize(this.getSize().width,sp.getHeight() + 25);
            this.pack();
        }
        
	/**
	* Dialog to save phrase as a MIDI file.
	 */
	public void saveMidi() {
		Write.midi(score);
	}
        
	/**
	* Save score as a jMusic XML file.
	*/
	public void saveXMLFile() {
		FileDialog fd = new FileDialog(new Frame(), 
			"Save as a jMusic XML file...", 
			FileDialog.SAVE);
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
			"Select a MIDI file to display...", 
			FileDialog.LOAD);
		fd.show();
		String fileName = fd.getFile();
		if (fileName != null) {
			Score score = new Score();
			jm.util.Read.midi(score, fd.getDirectory() + fileName);
			this.score = score;
			sp.setScore(score);
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
			sp.setScore(score);
		}
	}
    
    private void playBackMidi() {
        Play.midi(score, false);
    }
	
	
}
