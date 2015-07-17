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
package jm.gui.histogram;

import jm.JMC;
import jm.music.data.*;

import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.FileOutputStream;
import java.io.IOException;

/**
* Display a visual histogram of values from a jMusic score.
* The display can show one of several features from the jMusic
* notes in the specified score.
* Usae the constants, PITCH etc. as convenient arguments to the constructor.
* @author Andrew Brown
*/
public class Histogram extends Component implements JMC {
    /** The score to be displayed. */
    private Score score;
    /** variables for the largest values. */
    private int maxPitchValue;
    private int maxRhythmValue;
    private int maxDynamicValue;
    private int maxPanValue;
    /** Arrays for the data. */
    private int[] pitchValues;
    private int[] rhythmValues;
    private int[] dynamicValues;
    private int[] panValues;
    private Font f = new Font("Helvetica", Font.PLAIN, 9);
    /** The thickness of the bra graphs. */
    private int barWidth = 4;
    /** The amount of space for the ruler and labels. */
    private int lableSpace = 24;
    /** The type of note data to be displayed. */
    private int type = 0;
    /** The name for the displayed window. */
    private String title = "Pitch Histogram";
    /** The horizontal window location. */
    private int xPos = 0;
    /** The vertical window location. */
    private int yPos = 0;
    
    /**
    * Histogram constructor specifying the score to be displayed.
    * @param score the score to be displayed 
    */ 
    public Histogram(Score score) {
        this(score, 0);
    }
    
    /**
    * Histogram constructor specifying the score and type of 
    * data to be displayed.
    * @param score the score to be displayed 
    * @param type the note attribute to be displayed, 0 = pitch etc.
    */    
    public Histogram(Score score, int type) {
        this(score, type, 0, 0);
    }
    
    /**
    * Histogram constructor with all elements.
    * @param score the score to be displayed 
    * @param type the note attribute to be displayed, 0 = pitch etc.
    * @param xPos the horizonal position for the window to be displayed
    * @param yPos the vertical position for the window to be displayed
    */
    public Histogram(Score score, int type, int xPos, int yPos) {
        this(score, type, xPos, yPos, 200);
    }
    
    /**
    * Histogram constructor with all elements.
    * @param score the score to be displayed 
    * @param type the note attribute to be displayed, 0 = pitch etc.
    * @param xPos the horizonal position for the window to be displayed
    * @param yPos the vertical position for the window to be displayed
    */
    public Histogram(Score score, int type, int xPos, int yPos, int width) {
        this.score = score;
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
        if (type == RHYTHM) title = "Rhythm Histogram";
        if (type == DYNAMIC) title = "Dynamic Histogram";
        if (type == PAN) title = "Pan Histogram";
        this.setSize(width, barWidth * 127 + lableSpace);
        
        analysis();
    }
    
    private void analysis() {
    // rest values
    pitchValues = new int[128];
    rhythmValues = new int[66];
    dynamicValues = new int[127];
    panValues = new int[100];
    maxPitchValue = 0;
    maxRhythmValue = 0;
    maxDynamicValue = 0;
    maxPanValue = 0;
    // get data values
    Enumeration enum1 = score.getPartList().elements();
        while(enum1.hasMoreElements()){
            Part part = (Part) enum1.nextElement();
            Enumeration enum2 = part.getPhraseList().elements();
            while(enum2.hasMoreElements()){
                Phrase phrase = (Phrase) enum2.nextElement();
                Enumeration enum3 = phrase.getNoteList().elements();
                while(enum3.hasMoreElements()){
                    Note note = (Note) enum3.nextElement();
					// ignore notes with frequency as a pitch
					if (note.getPitchType() == Note.MIDI_PITCH) {
						if (note.getPitch() != REST) {
							// pitch
							pitchValues[note.getPitch()]++;
							if(pitchValues[note.getPitch()] > maxPitchValue) maxPitchValue = 
								pitchValues[note.getPitch()];
							// rhythm
							int val = (int)(note.getRhythmValue() / 0.125);
							if (val >= rhythmValues.length) val = rhythmValues.length - 1;
							rhythmValues[val]++;
							if (rhythmValues[val] > maxRhythmValue) 
								maxRhythmValue = rhythmValues[val];
							// velocities
							dynamicValues[note.getDynamic()]++;
							if(dynamicValues[note.getDynamic()] > maxDynamicValue) maxDynamicValue = 
								dynamicValues[note.getDynamic()];
							// pan
							panValues[(int)(note.getPan()*100)]++;
							if(panValues[(int)(note.getPan() * 100)] > maxPanValue) maxPanValue = 
								panValues[(int)(note.getPan() * 100)];
						}
					}
                }
            }
        }
    }
    
    /** Pass on the current title */
    public String getTitle() {
        return this.title;
    }
    
    /**
    * Update the type of data to show in the historgram.
    */
    public void setType(int type){
        this.type = type;
        repaint();
    }
    
    /** Pass on the horizontal position for the window. */
    public int getXPos() {
        return this.xPos;
    }
    
    /** Pass on the vertical position for the window. */
    public int getYPos() {
        return this.yPos;
    }
    
    /** Display values for a new score. */
    public void setScore(Score s) {
        this.score = s;
        analysis();
        repaint();
    }
    
    /** 
    * Save the histogram data to a tab delimited text file
    * with a file name to be specified by a dialog box.
    */
    public void saveData() {
        FileDialog fd = new FileDialog(new Frame(), 
            "Save histogram data as...", FileDialog.SAVE);
        fd.show();
        String fileName = fd.getFile();
        if (fileName != null) {
            saveDataAs(fd.getDirectory() + fileName);
        }
    }
    /** 
    * Save the histogram data to a tab delimited text file
    * of the specified name
    * @param fileName the name of the file. 
    */
    public void saveDataAs(String fileName) {
        try{
            FileOutputStream out = new FileOutputStream(fileName);
            String headerText = "Pitch value" + String.valueOf("\t") + "Pitch data"  + 
                String.valueOf("\t") + "Rhythm value" + String.valueOf("\t") + "Rhythm data" +
                    String.valueOf("\t") + "Dynamic value" + String.valueOf("\t") + "Dynamic data" +
                    String.valueOf("\t") + "Pan value" + String.valueOf("\t") + "Pan data" + 
                    String.valueOf("\n");
            out.write(headerText.getBytes());
            for(int i=0; i < pitchValues.length; i++) {
                String data = String.valueOf(i) + String.valueOf("\t") + String.valueOf(pitchValues[i]);
                if (i < rhythmValues.length) data += String.valueOf("\t") + String.valueOf(i * 0.125) + 
                    String.valueOf("\t") + String.valueOf(rhythmValues[i]);
                if (i < dynamicValues.length) data += String.valueOf("\t") + String.valueOf(i) + 
                    String.valueOf("\t") + String.valueOf(dynamicValues[i]);
                if (i < panValues.length) data += String.valueOf("\t") + String.valueOf(i / 100.0) + 
                    String.valueOf("\t") + String.valueOf(panValues[i]);
                data += String.valueOf("\n");
                out.write(data.getBytes());
            }
            out.close();
        } catch (IOException e) {}
    }
    
    public void paint(Graphics g) {
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        int ascent = fm.getAscent() / 2;
        // ruler
        int maxValue = 0;
        if (type == PITCH) maxValue = maxPitchValue;
        else if (type == RHYTHM) maxValue = maxRhythmValue;
        else if (type == DYNAMIC) maxValue = maxDynamicValue;
        else if (type == PAN) maxValue = maxPanValue;
        
        for (int i=0; i<5; i++) {
            g.setColor(Color.green);
            String lString = "" + maxValue/4 * i;
            g.drawString(lString, this.getSize().width / 5 * i + lableSpace - 
                fm.stringWidth(lString)/2, lableSpace - fm.getAscent()/2);
            g.setColor(Color.gray);
            g.drawLine(this.getSize().width / 5 * i + lableSpace, lableSpace, 
                this.getSize().width / 5 * i + lableSpace, this.getSize().height);
        }
        
        // paint
        switch (type) {
            case PITCH:  {
                paintPitches(g);
                break;
            }
            case RHYTHM: {
                paintRhythms(g);
                break;
            }
            case DYNAMIC: {
                paintDynamics(g);
                break;
            }
            case PAN:{
                paintPans(g);
                break;
            }
        }
    }
    
    private void paintPitches(Graphics g) {
        // graph pitches
        FontMetrics fm = g.getFontMetrics(f);
        int ascent = fm.getAscent() / 2;
        
        for (int i=0; i< 127; i++) {
             if (i % 12 == 0) { g.setColor(Color.red);
             } else if (i % 12 == 4) { g.setColor(Color.orange);
             } else if (i % 12 == 7) { g.setColor(Color.blue);
             } else g.setColor(Color.black);
            g.fillRect(lableSpace, i*barWidth + lableSpace, 
                (int)((double)pitchValues[i] / (double)maxPitchValue * 
                    ((double)this.getSize().width - lableSpace)), barWidth - 1);
            if (i % 12 == 0) {
                 g.setColor(Color.red);
                g.drawString("C" + (i/12 - 1), 2, i*barWidth + ascent + lableSpace);
            }
            if (i % 12 == 4) {
                 g.setColor(Color.orange);
                g.drawString("E" + (i/12 - 1), 2, i*barWidth + ascent + lableSpace);
            }
            if (i % 12 == 7) {
                 g.setColor(Color.blue);
                g.drawString("G" + (i/12 - 1), 2, i*barWidth + ascent + lableSpace);
            }
        }
    }
    
     private void paintRhythms(Graphics g) {
        // graph rhythms
        FontMetrics fm = g.getFontMetrics(f);
        int ascent = fm.getAscent();
        
        for (int i=1; i< 65 ; i++) {
            if (i % 8 == 0) { g.setColor(Color.red);
            } else if (i % 8 == 4) { g.setColor(Color.orange);
            } else if (i % 8 == 2 || i % 8 == 6) { g.setColor(Color.blue);
            } else g.setColor(Color.black);
            g.fillRect(lableSpace, i*barWidth * 2 + lableSpace, 
                (int)((double)rhythmValues[i] / (double)maxRhythmValue * 
                    ((double)this.getSize().width - lableSpace)), barWidth * 2 - 1);
            if (i % 8 == 0) {
                 g.setColor(Color.red);
                g.drawString("" + i/8.0, 2, i*barWidth * 2 + ascent + lableSpace);
            }
            if (i % 8 == 4) {
                 g.setColor(Color.orange);
                g.drawString("" + i/8.0, 2, i*barWidth * 2 + ascent + lableSpace);
            }
            if (i % 8 == 2 || i%8 == 6) {
                 g.setColor(Color.blue);
                g.drawString("" + i/8.0, 2, i*barWidth * 2 + ascent + lableSpace);
            }
        }
    }
    
     private void paintDynamics(Graphics g) {
        // graph dynamics
        FontMetrics fm = g.getFontMetrics(f);
        int ascent = fm.getAscent() / 2;
        
        for (int i=1; i< 127; i++) {
             if (i % 10 == 0) { g.setColor(Color.red);
             } else if (i % 10 == 5) { g.setColor(Color.orange);
             } else g.setColor(Color.black);
            g.fillRect(lableSpace, i*barWidth + lableSpace, 
                (int)((double)dynamicValues[i] / (double)maxDynamicValue * 
                    ((double)this.getSize().width - lableSpace)), barWidth - 1);
            if (i % 10 == 0) {
                 g.setColor(Color.red);
                g.drawString("" + i, 2, i*barWidth + ascent + lableSpace);
            }
            if (i % 10 == 5) {
                 g.setColor(Color.orange);
                g.drawString("" + i, 2, i*barWidth + ascent + lableSpace);
            }
        }
    }
    
    private void paintPans(Graphics g) {
        // graph dynamics
        FontMetrics fm = g.getFontMetrics(f);
        int ascent = fm.getAscent() / 2;
        
        for (int i=1; i< 100; i++) {
             if (i % 10 == 0 && i != 50) { g.setColor(Color.red);
             } else if (i % 10 == 5 && i != 50) { g.setColor(Color.orange);
             } else if (i == 50) { g.setColor(Color.blue);
             } else g.setColor(Color.black);
            g.fillRect(lableSpace, i*barWidth + lableSpace, 
                (int)((double)panValues[i] / (double)maxPanValue * 
                    ((double)this.getSize().width - lableSpace)), barWidth - 1);
            if (i % 10 == 0 && i != 50) {
                g.setColor(Color.red);
                g.drawString("" + (i / 100.0), 2, i*barWidth + ascent + lableSpace);
            } else if (i % 10 == 5 && i != 50) {
                g.setColor(Color.orange);
                g.drawString("" + (i / 100.0), 2, i*barWidth + ascent + lableSpace);
            } else if (i == 50) {
                g.setColor(Color.blue);
                g.drawString("" + (i / 100.0), 2, i*barWidth + ascent + lableSpace);
            }
        }
    }


}
