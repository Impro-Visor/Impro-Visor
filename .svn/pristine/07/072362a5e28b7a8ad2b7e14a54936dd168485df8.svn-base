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
* @version 1.0, February 2003
* ---------------------
*/
package jm.gui.histogram;

import java.awt.*;
import java.awt.event.*;
import jm.JMC;
import jm.music.data.*;
import java.awt.image.*; //

/**
* The tool displays a jMusic class as bar graph of the spcified values. 
* To use it write:
* new HistorgamFrame(scoreName, dataTypeInteger);
* Where scoreName is the jMusic Score object and dataTypeInteger 0 , 1 , 2 etc.
* 1 = pitch, 2 = rhythm, 3 dynamic, 4 = pan
*/

public class HistogramFrame extends Frame implements WindowListener, ActionListener, JMC {
	//attributes
	private Score score = new Score();
	private MenuItem showPitch, showRhythm, showDynamic, showPan, open, openXml, saveAs, quit;
        private Histogram histo;
	//--------------
	//constructors
	//--------------
        /**
        * A basic constructor that opens an empty histogram frame.
        */
	public HistogramFrame() {
		this(new Score(), 0);
	}
        /**
        * A basic constructor that defaults to showing pitch info.
        * @param Score The score from which data is to be displayed.
        */
	public HistogramFrame(Score score) {
		this(score, 0);
	}
        
        /**
        * A constructor that showing any type of info, positioned
        * in the top left corner of the screen.
        * @param Score The score from which data is to be displayed.
        * @param int The type of data to display, 0 = pitch, 1 = rhythm, etc.
        */
        public HistogramFrame(Score score, int dataType) {
		this(score, dataType, 0, 0);
	}
	
        /**
        * A constructor that showing any type of info, positioned
        * in the top left corner of the screen.
        * @param Score The score from which data is to be displayed.
        * @param int The type of data to display, 0 = pitch, 1 = rhythm, etc.
        * @param int The X location for this frame.
        * @param int The Y location for this frame.
        */

	public HistogramFrame(Score score, int dataType, int xPos, int yPos) {
		super(score.getTitle());
		this.score = score;
                setTitle(dataType);
                this.setBackground(Color.white);
                this.setSize(350, 127 * 4 + 50);
                histo = new Histogram(score, dataType, 0, 0, this.getSize().width);
                this.setLocation(xPos, yPos);
                this.add(histo);
                
		//register the closebox event
		this.addWindowListener(this);
                    
		// menus
		MenuBar menus = new MenuBar();
		Menu histoMenu  = new Menu("Histogram", true);
		                            
                showPitch = new MenuItem("Pitch", new MenuShortcut(KeyEvent.VK_P));
		showPitch.addActionListener(this);
		histoMenu.add(showPitch);
                
                showRhythm = new MenuItem("Rhythm", new MenuShortcut(KeyEvent.VK_R));
		showRhythm.addActionListener(this);
		histoMenu.add(showRhythm);


                showDynamic = new MenuItem("Dynamic", new MenuShortcut(KeyEvent.VK_D));
		showDynamic.addActionListener(this);
		histoMenu.add(showDynamic);

                showPan = new MenuItem("Pan", new MenuShortcut(KeyEvent.VK_P, true));
		showPan.addActionListener(this);
		histoMenu.add(showPan);
                
                MenuItem line = new MenuItem("-");
                histoMenu.add(line);
                
                open = new MenuItem("Open MIDI file...", new MenuShortcut(KeyEvent.VK_O));
		open.addActionListener(this);
		histoMenu.add(open);
                
                openXml = new MenuItem("Open jMusic XML file...");
		openXml.addActionListener(this);
		histoMenu.add(openXml);
                
                saveAs = new MenuItem("Save data as...", new MenuShortcut(KeyEvent.VK_S));
		saveAs.addActionListener(this);
		histoMenu.add(saveAs);
                
                MenuItem line2 = new MenuItem("-");
                histoMenu.add(line2);
                
                quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
		quit.addActionListener(this);
		histoMenu.add(quit);

                menus.add(histoMenu);
		this.setMenuBar(menus);
                
                this.setVisible(true);
        }
        
        //--------------
	// Class Methods
	//--------------
	
        private void setTitle(int dataType) {
            if (dataType == PITCH) this.setTitle("jMusic Pitch Histogram: '" + score.getTitle() + "'");
            if (dataType == RHYTHM) this.setTitle("jMusic Rhythm Histogram: '" + score.getTitle() + "'");
            if (dataType == DYNAMIC) this.setTitle("jMusic Dynamic Histogram: '" + score.getTitle() + "'");
            if (dataType == PAN) this.setTitle("jMusic Pan Histogram: '" + score.getTitle() + "'");
        }
        
        // Manage switching to see new data from the same score
        private void changeDataType(int dataType) {
            setTitle(dataType);
            histo.setType(dataType);
            repaint();
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
        * Handle menu items
        */
	public void actionPerformed(ActionEvent e){
            if(e.getSource() == showPitch) changeDataType(PITCH);
            if(e.getSource() == showRhythm) changeDataType(RHYTHM);
            if(e.getSource() == showDynamic) changeDataType(DYNAMIC);
            if(e.getSource() == showPan) changeDataType(PAN);
            if(e.getSource() == open) openMIDIFile();
            if(e.getSource() == openXml) openXMLFile();
            if(e.getSource() == saveAs) histo.saveData();
            if(e.getSource() == quit) System.exit(0);
            
	}
        
        /**
        * Read a MIDI file and display its data.
        */
        public void openMIDIFile() {
            FileDialog fd = new FileDialog(new Frame(), "Select a MIDI file to display.", FileDialog.LOAD);
            fd.show();
            String fileName = fd.getFile();
            if (fileName != null) {
                Score score = new Score();
                jm.util.Read.midi(score, fd.getDirectory() + fileName);
                this.score = score;
                histo.setScore(score);
                this.changeDataType(PITCH);
            }
        }
        
        /**
        * Read a jMusic XML file and display its data.
        */
        public void openXMLFile() {
            FileDialog fd = new FileDialog(new Frame(), "Select a jMusic XML file to display.", FileDialog.LOAD);
            fd.show();
            String fileName = fd.getFile();
            if (fileName != null) {
                Score score = new Score();
                jm.util.Read.xml(score, fd.getDirectory() + fileName);
                this.score = score;
                histo.setScore(score);
                this.changeDataType(PITCH);
            }
        }

}
