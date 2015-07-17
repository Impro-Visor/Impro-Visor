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

import java.awt.*;
import java.awt.event.*;

import jm.midi.SMF;
import jm.music.data.*;
import jm.JMC;
import jm.gui.show.*;
import jm.gui.cpn.*;
import jm.gui.sketch.*;
import jm.gui.histogram.*;
import jm.gui.wave.*;


public class View implements JMC{
    
    public View() {}
   
    //----------------------------------------------
    // ShowScore
    //----------------------------------------------
    /**
    * Display the jMusic score in a ShowScore window
    * @param Score
    */ 
    public static void show(Score s) {
        show(s, 0, 0);
    }
    
    /**
    * Display the jMusic score in a ShowScore window
    * @param Score
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void show(Score  s, int xLoc, int yLoc) {
        new ShowScore(s, xLoc, yLoc);
    }
    /**
    * Display the jMusic Part in a ShowScore window
    * @param Part
    */
    public static void show(Part p) {
        show(p, 0, 0);
    }
    /**
    * Display the jMusic Part in a ShowScore window
    * @param Part
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void show(Part p, int xLoc, int yLoc) {
        Score s = new Score("Part: "+ p.getTitle());
        s.addPart(p);
        new ShowScore(s, xLoc, yLoc);
    }
    
    /**
    * Display the jMusic CPhrase in a ShowScore window
    * @param CPhrase
    */
    public static void show(CPhrase cphr) {
        show(cphr, 0, 0);
    }
    
    /**
    * Display the jMusic CPhrase in a ShowScore window
    * @param CPhrase
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void show(CPhrase cphr, int xLoc, int yLoc) {
        Score s = new Score("Phrase: "+ cphr.getTitle());
        Part p = new Part();
        p.addCPhrase(cphr);
        s.addPart(p);
        new ShowScore(s, xLoc, yLoc);
    }
    
    /**
    * Display the jMusic Phrase in a ShowScore window
    * @param Phrase
    */
    public static void show(Phrase phr) {
        show(phr, 0, 0);
    }
    
    /**
    * Display the jMusic Phrase in a ShowScore window
    * @param Phrase
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void show(Phrase phr, int xLoc, int yLoc) {
        Score s = new Score("Phrase: "+ phr.getTitle());
        Part p = new Part();
        p.addPhrase(phr);
        s.addPart(p);
        new ShowScore(s, xLoc, yLoc);
    }
    //----------------------------------------------
    // Common Practice Notation
    //----------------------------------------------
    /**
    * Display the jMusic Phrase in a CPN window
    * @param Phrase
    */
    public static void notate(Phrase phr) {
        new Notate(phr, 0, 0);
    }
    /**
    * Display the jMusic Phrase in a CPN window
    * @param Phrase
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void notate(Phrase phr, int xLoc, int yLoc) {
        new Notate(phr, xLoc, yLoc);
    }
    
    /**
    * Display the jMusic Part in a CPN window.
    * At presetn on the first phrase of the part will be displayed.
    * @param Part
    */
    public static void notate(Part p) {
        new Notate(p.getPhrase(0), 0, 0);
    }
    
    /**
    * Display the jMusic Part in a CPN window.
    * At present only the first phrase in the part will be displayed.
    * @param Part
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void notate(Part p, int xLoc, int yLoc) {
        new Notate(p.getPhrase(0), xLoc, yLoc);
    }
    
    /**
    * Display the jMusic Score in a CPN window.
    * At presetn on the first phrase of each part in the score will be displayed.
    * @param Score
    */
    public static void notate(Score s) {
        new Notate(s, 0, 0);
    }

    /**
    * Display the jMusic Score in a CPN window.
    * At present only the first phrase from each part will be displayed.
    * @param Score
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void notate(Score s, int xLoc, int yLoc) {
        new Notate(s, xLoc, yLoc);
    }
    //----------------------------------------------
    // Sketch
    //----------------------------------------------
    /**
    * Display the jMusic score in a ShowScore window
    * @param Score
    */
    public static void sketch(Score s) {
        sketch(s, 0, 0);
    }
    
    /**
    * Display the jMusic score in a ShowScore window, at the specified x and<br>
    * y coordinates, measured in pixels.
    * @param Score
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void sketch(Score  s, int xLoc, int yLoc) {
        new SketchScore(s, xLoc, yLoc);
    }
    /**
    * Display the jMusic Part in a ShowScore window
    * @param Part
    */
    public static void sketch(Part p) {
        sketch(p, 0, 0);
    }
    
    /**
    * Display the jMusic Part in a ShowScore window, at the specified x and<br>
    * y coordinates, measured in pixels.
    * @param Part
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void sketch(Part p, int xLoc, int yLoc) {
        Score s = new Score("Part: "+ p.getTitle());
        s.addPart(p);
        new SketchScore(s, xLoc, yLoc);
    }
    /**
    * Display the jMusic Phrase in a ShowScore window
    * @param Phrase
    */
    public static void sketch(Phrase phr) {
        sketch(phr, 0, 0);
    }
    
    /**
    * Display the jMusic Phrase in a ShowScore window, at the specified x <br>
    * and y coordinates, measured in pixels.
    * @param Phrase
    * @param xLoc the left-right location of the window
    * @param yLoc the up-down location of the window
    */
    public static void sketch(Phrase phr, int xLoc, int yLoc) {
        Score s = new Score( "Phrase: "+ phr.getTitle());
        Part p = new Part();
        p.addPhrase(phr);
        s.addPart(p);
        new SketchScore(s, xLoc, yLoc);
    }
    //----------------------------------------------
    // Print to stout
    //----------------------------------------------
    /**
    * Print the jMusic Note in standard output
    * @param Note
    */
    public static void print(Note note) {
        System.out.println(note.toString());
    }
    /**
    * Print the jMusic Phrase in standard output
    * @param Phrase
    */
    public static void print(Phrase phrase) {
        System.out.println(phrase.toString());
    }
    /**
    * Print the jMusic CPhrase in standard output
    * @param CPhrase
    */
    public static void print(CPhrase cphrase) {
        System.out.println(cphrase.toString());
    }
    /**
    * Print the jMusic Part in standard output
    * @param Part
    */
    public static void print(Part part) {
        System.out.println(part.toString());
    }
    /**
    * Print the jMusic Score in standard output
    * @param Score
    */
    public static void print(Score score) {
        System.out.println(score.toString());
    }
    //----------------------------------------------
    // Display score note data as a historgram
    //----------------------------------------------
    /**
    * Display a histogram of jMusic Note data in the score
    * @param score the score to be displayed 
    */
    public static void histogram() {
        FileDialog fd = new FileDialog(new Frame(), "Select a MIDI file to display.", FileDialog.LOAD);
        fd.show();
        String fileName = fd.getFile();
        if (fileName != null) {
            Score score = new Score();
            jm.util.Read.midi(score, fd.getDirectory() + fileName);
            HistogramFrame hf = new HistogramFrame(score);
        }
    }
    
    /**
    * Display a histogram of jMusic Note data in the score
    * @param score the score to be displayed 
    */
    public static void histogram(Score score) {
        histogram(score, 0);
    }
    
    /**
    * Display a histogram of the score.
    * @param score the score to be displayed 
    * @param type the note attribute to be displayed, 0 = pitch etc.
    */
    public static void histogram(Score score, int dataType) {
        histogram(score, dataType, 0, 0);
    }
    
    /**
    * Display a histogram of the score.
    * @param score the score to be displayed 
    * @param type the note attribute to be displayed, 0 = pitch etc.
    * @param xPos the horizonal position for the window to be displayed
    * @param yPos the vertical position for the window to be displayed
    */
    public static void histogram(Score score, int dataType, int xPos, int yPos) {
        new HistogramFrame(score, dataType, xPos, yPos);
        /**
        Histogram h = new Histogram(score, dataType, xPos, yPos);
        Frame f = new Frame(h.getTitle());
        f.setLocation(h.getXPos(), h.getYPos());
        f.setSize(400, 127 * 4 + 50);
        f.add(h);
        f.setVisible(true);
        */
    }
    
    /**
    * Display an au file stored on the disk.
    * @param filename The name (and directory path) of the file.
    */
    public static void au(String filename) {
        new WaveView(filename);
    }
    
    /**
    * Display an au file stored on the disk at a specified location on screen.
    * @param filename The name (and directory path) of the file.
    * @param xLoc The horizontal position for the display.
    * @param yLoc The vertical position for the display.
    */
    public static void au(String filename, int xLoc, int yLoc) {
        new WaveView(filename, xLoc, yLoc);
    }

}
