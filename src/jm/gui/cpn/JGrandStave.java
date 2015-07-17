/*
 * JGrandStave.java 0.0.1.1 23rd February 2001
 *
 * Copyright (C) 2000 Andrew Brown, Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.gui.cpn; 

import java.awt.Color;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.awt.image.IndexColorModel;

import javax.swing.JPanel;

import jm.music.tools.PhraseAnalysis;
import jm.music.tools.ChordAnalysis;

import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.net.URL; 
import jm.JMC;
import jm.music.data.*;
import jm.util.*;

import jm.gui.cpn.Stave;



//import jm.gui.cpn.CPNFrame;
//import javax.swing.*;

/**
 *
 *
 * @author  Andrew Brown, Adam Kirby
 * @version 1.0,Sun Feb 25 18:35:32  2001
 *
 * @since   jMusic February 2000 Release
 */
public class JGrandStave extends JPanel implements JMC {
    private int tonic = 0;

    RGBImageFilter filter = new RGBImageFilter() {
        {
            canFilterIndexColorModel = true;
        }
    
        public int filterRGB(int x, int y, int rgb) {
            return (rgb | 0xffff0000);
        }
    };

    class RedFilter {
        public Image image;
        public Image redImage;

        RedFilter() {
        }

        RedFilter(Image image) {
            this.image = image;
            redImage = createImage(new FilteredImageSource(image.getSource(),
                                                           filter));
        }
    }

    public final static int[] keys =
            {11, 6, 1, 8, 3, 10, 5, 0, 7, 2, 9, 4, 11, 6, 1};

    protected int[] scale = JMC.MAJOR_SCALE;

    private boolean isNormalColor = true;

    protected KeyChangeListener keyChangeListener = null;

    // for double buffering
    public Image image;
    protected Graphics g;
    // attributes
    protected RedFilter crotchetUp = new RedFilter();
    protected RedFilter crotchetDown = new RedFilter();
    protected RedFilter quaverDown = new RedFilter();
    protected RedFilter quaverUp = new RedFilter();
    protected RedFilter semiquaverDown = new RedFilter();
    protected RedFilter semiquaverUp = new RedFilter();
    protected RedFilter minimDown = new RedFilter();
    protected RedFilter minimUp = new RedFilter();
    protected RedFilter semibreve = new RedFilter();
    protected RedFilter dot = new RedFilter();
    protected RedFilter semiquaverRest = new RedFilter();
    protected RedFilter quaverRest = new RedFilter();
    protected RedFilter crotchetRest = new RedFilter();
    protected RedFilter minimRest = new RedFilter();
    protected RedFilter semibreveRest = new RedFilter();
    protected RedFilter sharp = new RedFilter();
    protected RedFilter flat = new RedFilter();
    protected RedFilter natural = new RedFilter();
    protected RedFilter delete = new RedFilter();
    protected RedFilter tie = new RedFilter(
        Toolkit.getDefaultToolkit().getImage(
                Stave.class.getResource("graphics/tie.gif")));

    protected Image trebleClef, bassClef, one, two, three, four, five, six,
                    seven, eight, nine;
    public int staveSpaceHeight = 8, rightMargin = 20, beatWidth = 43, staveWidth = beatWidth*15,  
            imageHeightOffset = 28, clefWidth = 38, timeSigWidth = 5, keySigWidth = 5;
    public int bPos = 28;
    protected Phrase phrase;
    protected RedFilter currImage;
    protected int currBeatWidth, totalBeatWidth;
    protected boolean dottedNote = false;
    protected int[] notePosOffset = {24,24,20,20,16,12,12,8,8,4,4,0}; // chromatic scale
    protected double metre = 4.0;
    protected int keySignature = 0; // postive numbers = sharps, negative numbers = flats
    protected int[] sharps = {77, 72, 79, 74, 69, 76, 71};
    protected int[] flats = {71, 76, 69, 74, 67, 72, 65};  
    protected Vector previouslyChromatic = new Vector();
    protected int[] lineNotes = {0, 1, 4, 7, 8, 11, 14, 15, 17, 18, 21, 22};
    public Vector notePositions = new Vector();
    protected int maxPitch = 127, minPitch = 0;
    protected String title;
    protected boolean barNumbers = false, editable = true, qtOn = false;
    protected int panelHeight = 110, staveDelta = 0;
    



    public static final int MAX_HEIGHT = 500;

    public static final int MAX_WIDTH = 2000;
    
    public JGrandStave() {
        this(new Phrase());
        bPos = 110;
        panelHeight = 310;
        this.setSize((int)(beatWidth*40), panelHeight);
    }
    
    public JGrandStave(Phrase phrase) {
        super();
        this.phrase = phrase;
        title = phrase.getTitle();
        // change 'paper' colour
        this.setBackground(Color.getHSBColor((float)0.14,(float)0.09,(float)1.0)); // .17, .1, 1
        // set the appropriate size (at least 8 bars of 4/4) for the stave
        this.setSize((int)(beatWidth*40), panelHeight);
        if (this.getSize().width < (int)(phrase.getEndTime()* beatWidth * 1.5) ) this.setSize( (int)(phrase.getEndTime()* beatWidth * 1.5), panelHeight);
        // compensate for overly large images - pain!!
        //if (this.getSize().width > 5000) {
         //   this.setSize(5000, panelHeight);
        //    System.out.println("Not all the phrase can be shown due to overly large image requirements - sorry");
        //}
        //System.out.println("Max size is "+this.getMaximumSize().width +" "+ this.getMaximumSize().height);
        // register the listerners
        JStaveActionHandler handleActions = new JStaveActionHandler(this);
        this.addMouseListener(handleActions);
        this.addMouseMotionListener(handleActions);

        /*
         * There appears to be a problem with my use of MediaTracker under
         * JDK1.1.  It works fine under JDK1.2 and these calls can be
         * reimplemented once we shift completely to JDK1.2.
         *
         * MediaTracker tracker = new MediaTracker(this);
         */
        //this.addKeyListener(handleActions);
	    // load gif files
	     try {
	        // get location of graphics
        
            trebleClef= Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/trebleClef.gif"));
            bassClef= Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/bassClef.gif"));
            crotchetDown.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/crotchetDown.gif"));
            crotchetUp.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/crotchetUp.gif"));
            quaverDown.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/quaverDown.gif"));
            quaverUp.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/quaverUp.gif"));
            semiquaverDown.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/semiquaverDown.gif"));
            semiquaverUp.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/semiquaverUp.gif"));
            minimDown.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/minimDown.gif"));
            minimUp.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/minimUp.gif"));
            semibreve.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/semibreve.gif"));
            dot.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/dot.gif"));
            semiquaverRest.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/semiquaverRest.gif"));
            quaverRest.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/quaverRest.gif"));
            crotchetRest.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/crotchetRest.gif"));
            minimRest.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/minimRest.gif"));
            semibreveRest.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/semibreveRest.gif"));
            sharp.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/sharp.gif"));
            flat.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/flat.gif"));
            natural.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/natural.gif"));
            one = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/one.gif"));
            two = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/two.gif"));
            three = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/three.gif"));
            four = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/four.gif"));
            five = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/five.gif"));
            six = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/six.gif"));
            seven = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/seven.gif"));
            eight = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/eight.gif"));
            nine = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/nine.gif"));
            delete.image = Toolkit.getDefaultToolkit().getImage( Stave.class.getResource("graphics/delete.gif"));

		    //MediaTracker tracker = new MediaTracker(this);
		    //tracker.addImage(nine, 0);
      
	        //tracker.waitForID(0); // still incomplete media tracker implementation
        }
        catch (Exception e) {
            System.out.println("Error while loading pictures...");
            e.printStackTrace();
        }

        RGBImageFilter filter = new RGBImageFilter() {
            {
                canFilterIndexColorModel = true;
            }
        
            public int filterRGB(int x, int y, int rgb) {
                return (rgb | 0xffff0000);

//                return (rgb & 0xffff0000);
            }
        };

        crotchetDown.redImage = createImage(new FilteredImageSource(
                crotchetDown.image.getSource(),
                filter));
        crotchetUp.redImage = createImage(new FilteredImageSource(
                crotchetUp.image.getSource(),
                filter));
        quaverDown.redImage = createImage(new FilteredImageSource(
                quaverDown.image.getSource(),
                filter));
        quaverUp.redImage = createImage(new FilteredImageSource(
                quaverUp.image.getSource(),
                filter));
        semiquaverDown.redImage = createImage(new FilteredImageSource(
                semiquaverDown.image.getSource(),
                filter));
        semiquaverUp.redImage = createImage(new FilteredImageSource(
                semiquaverUp.image.getSource(),
                filter));
        minimDown.redImage = createImage(new FilteredImageSource(
                minimDown.image.getSource(),
                filter));
        minimUp.redImage = createImage(new FilteredImageSource(
                minimUp.image.getSource(),
                filter));
        semibreve.redImage = createImage(new FilteredImageSource(
                semibreve.image.getSource(),
                filter));
        dot.redImage = createImage(new FilteredImageSource(
                dot.image.getSource(),
                filter));
        semiquaverRest.redImage = createImage(new FilteredImageSource(
                semiquaverRest.image.getSource(),
                filter));
        quaverRest.redImage = createImage(new FilteredImageSource(
                quaverRest.image.getSource(),
                filter));
        crotchetRest.redImage = createImage(new FilteredImageSource(
                crotchetRest.image.getSource(),
                filter));
        minimRest.redImage = createImage(new FilteredImageSource(
                minimRest.image.getSource(),
                filter));
        sharp.redImage = createImage(new FilteredImageSource(
                sharp.image.getSource(),
                filter));
        flat.redImage = createImage(new FilteredImageSource(
                flat.image.getSource(),
                filter));
        natural.redImage = createImage(new FilteredImageSource(
                natural.image.getSource(),
                filter));
        delete.redImage = createImage(new FilteredImageSource(
                delete.image.getSource(),
                filter));

        /*
         * There appears to be a problem with my use of MediaTracker under
         * JDK1.1.  It works fine under JDK1.2 and these calls can be
         * reimplemented once we shift completely to JDK1.2.
         *
         * tracker.addImage(trebleClef, 0);
         * tracker.addImage(bassClef, 1);
         * tracker.addImage(crotchetDown.image, 2);
         * tracker.addImage(crotchetUp.image, 3);
         * tracker.addImage(quaverDown.image, 4);
         * tracker.addImage(quaverUp.image, 5);
         * tracker.addImage(semiquaverDown.image, 6);
         * tracker.addImage(semiquaverUp.image, 7);
         * tracker.addImage(minimDown.image, 8);
         * tracker.addImage(minimUp.image, 9);
         * tracker.addImage(semibreve.image, 10);
         * tracker.addImage(dot.image, 11);
         * tracker.addImage(semiquaverRest.image, 12);
         * tracker.addImage(quaverRest.image, 13);
         * tracker.addImage(crotchetRest.image, 14);
         * tracker.addImage(minimRest.image, 15);
         * tracker.addImage(semibreveRest.image, 16);
         * tracker.addImage(sharp.image, 17);
         * tracker.addImage(flat.image, 18);
         * tracker.addImage(natural.image, 19);
         * tracker.addImage(one, 20);
         * tracker.addImage(two, 21);
         * tracker.addImage(three, 22);
         * tracker.addImage(four, 23);
         * tracker.addImage(five, 24);
         * tracker.addImage(six, 25);
         * tracker.addImage(seven, 26);
         * tracker.addImage(eight, 27);
         * tracker.addImage(nine, 28);
         * tracker.addImage(delete.image, 29);
         * // tracker.addImage(trebleClef.redImage, 30);
         * // tracker.addImage(bassClef.redImage, 31);
         * tracker.addImage(crotchetDown.redImage, 32);
         * tracker.addImage(crotchetUp.redImage, 33);
         * tracker.addImage(quaverDown.redImage, 34);
         * tracker.addImage(quaverUp.redImage, 35);
         * tracker.addImage(semiquaverDown.redImage, 36);
         * tracker.addImage(semiquaverUp.redImage, 37);
         * tracker.addImage(minimDown.redImage, 38);
         * tracker.addImage(minimUp.redImage, 39);
         * tracker.addImage(semibreve.redImage, 40);
         * tracker.addImage(dot.redImage, 41);
         * tracker.addImage(semiquaverRest.redImage, 42);
         * tracker.addImage(quaverRest.redImage, 43);
         * tracker.addImage(crotchetRest.redImage, 44);
         * tracker.addImage(minimRest.redImage, 45);
         * tracker.addImage(semibreveRest.redImage, 46);
         * tracker.addImage(sharp.redImage, 47);
         * tracker.addImage(flat.redImage, 48);
         * tracker.addImage(natural.redImage, 49);
         * // tracker.addImage(one, 20);
         * // tracker.addImage(two, 21);
         * // tracker.addImage(three, 22);
         * // tracker.addImage(four, 23);
         * // tracker.addImage(five, 24);
         * // tracker.addImage(six, 25);
         * // tracker.addImage(seven, 26);
         * // tracker.addImage(eight, 27);
         * // tracker.addImage(nine, 28);
         * tracker.addImage(delete.redImage, 50);
         * tracker.addImage(tie.image, 51);
         * tracker.addImage(tie.redImage, 52);
         * 
         * try {
         *     System.out.println("Loading images...");
         *     tracker.waitForAll();
         * } catch (InterruptedException e) {
         * }
         * System.out.println("...Finished loading images");
         */


        bPos = 110;
        panelHeight = 310;
        this.setSize((int)(beatWidth*40), panelHeight);
    }

    private double beatCounter;
    
    public void paintComponent(Graphics graphics) {
        if (phrase == null) {
            return;
        }

        // set up for double buffering
        if(image == null) {
            image = this.createImage(MAX_WIDTH, MAX_HEIGHT);
	        g = image.getGraphics();
	    }
        // keep track of the rhythmic values for bar lines
        beatCounter = 0.0;
        // reset the chromatic vector
        previouslyChromatic.removeAllElements();
        // reste note position locations
        notePositions.removeAllElements();
        int keyAccidentals = 0;
        // add a title if present
        if(title != null) g.drawString(title, rightMargin, bPos - 50);
        // insert key signature if required
        int keyOffset = 0;
        // is the key signature using sharps or flats?
        if (keySignature > 0 && keySignature < 8) { // sharp
            for(int ks=0;ks<keySignature; ks++) {
                // claulate position
                int keyAccidentalPosition = notePosOffset[ sharps[ks]%12] + bPos - 4 + (( 5- sharps[ks]/12) * 24) + (( 6- sharps[ks]/12) * 4);
                // draw sharp on treble
                g.drawImage(sharp.image, rightMargin + clefWidth + keyOffset, keyAccidentalPosition, this);
                // draw sharp on bass
                g.drawImage(sharp.image, rightMargin + clefWidth + keyOffset, keyAccidentalPosition + staveSpaceHeight * 7, this);
                // indent position
                keyOffset += 10;
                //add note to accidental vector
               int theModValue = sharps[ks]%12;
                for(int pc=0;pc<128;pc++) {
                    if ((pc%12) == theModValue) {
                        previouslyChromatic.addElement(new Integer(pc));
                        keyAccidentals++;
                    }
                }
                keySigWidth = keyOffset;
            }
        } else {
            if (keySignature < 0 && keySignature > -8) { // flat
                for(int ks=0;ks< Math.abs(keySignature); ks++) {
                    // claulate position
                    int keyAccidentalPosition = notePosOffset[ flats[ks]%12] + bPos - 4 + (( 5- flats[ks]/12) * 24) + (( 6- flats[ks]/12) * 4);
                    // draw flat
                    g.drawImage(flat.image, rightMargin + clefWidth + keyOffset, keyAccidentalPosition, this);
                    // draw flat on bass stave
                    g.drawImage(flat.image, rightMargin + clefWidth + keyOffset, keyAccidentalPosition + staveSpaceHeight * 7, this);
                    // indent position
                    keyOffset += 10;
                    //add note to accidental vector
                    int theModValue = flats[ks]%12;
                    for(int pc=0;pc<128;pc++) {
                        if ((pc%12) == theModValue) {
                            previouslyChromatic.addElement(new Integer(pc));
                            keyAccidentals++;
                        }
                    }
                }
            }
        }
        keySigWidth = keyOffset + 3;
        
        // insert time signature if required
        if ( metre != 0.0) {
            Image[] numbers = {one, two, three, four, five, six, seven, eight, nine};
            
            // top number
            g.drawImage(numbers[(int)metre - 1], rightMargin + clefWidth + keySigWidth, bPos + 13, this);
            g.drawImage(numbers[(int)metre - 1], rightMargin + clefWidth + keySigWidth, bPos + 13 + staveSpaceHeight * 6, this);
            //bottom number
            g.drawImage(four, rightMargin + clefWidth + keySigWidth , bPos + 29, this);
            g.drawImage(four, rightMargin + clefWidth + keySigWidth , bPos + 29 + staveSpaceHeight * 6, this);
            timeSigWidth = 30;
        } else timeSigWidth = 5;
        // set indent position for first note
        totalBeatWidth = rightMargin + clefWidth + keySigWidth + timeSigWidth;

        firstChords =
                ChordAnalysis.getFirstPassChords(phrase, 1.0, tonic,
                                                 scale);
        secondChords =
                ChordAnalysis.getSecondPassChords(phrase, 1.0, tonic,
                                                  scale);
        lastChordDisplayed = -1;

        // draw notes and rests
        for(int i = 0; i < phrase.size();i++) {
            int notePitchNum = phrase.getNote(i).getPitch();
            // reset pitch for rests
                        
            // position?
            int pitchTempPos;
            if ( notePitchNum == REST || phrase.getNote(i).getRhythmValue() == 0.0) { // rest or delete
                pitchTempPos = notePosOffset[71%12] + bPos - 4 + (( 5- 71/12) * 24) + (( 6- 71/12) * 4);
            } else {
                pitchTempPos = notePosOffset[notePitchNum%12] + bPos - 4 + (( 5- notePitchNum/12) * 24) + (( 6- notePitchNum/12) * 4);
            }

            if (notePitchNum == REST
                    || PhraseAnalysis.isScale(phrase.getNote(i),
                                              tonic,
                                              scale)) {
                isNormalColor = true;
            } else {
                isNormalColor = false;
            }

            firstAccidentalDisplayed = false;

            semitoneShiftUp = false;
            isTied = false;
            isFirstNoteInTie = true;
            extraImagesUsed = false;
            savedBeatWidth = totalBeatWidth;
            savedBeatWidth2 = 0;
            double rhythmValue = phrase.getNote(i).getRhythmValue();
            double rvToEndOfBar = metre - (beatCounter % metre);

            while (rvToEndOfBar < rhythmValue) {
                isTied = true;
                drawNote(notePitchNum, rvToEndOfBar,
                         pitchTempPos, keyAccidentals);
                rhythmValue -= rvToEndOfBar;
                rvToEndOfBar = metre - (beatCounter % metre);
            }


            drawNote(notePitchNum, rhythmValue, pitchTempPos, keyAccidentals);


        }
















        
        // draw treble stave
        for(int i = 0; i < 5;i++) {
            g.drawLine( rightMargin, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), totalBeatWidth, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
        
        // draw bass stave
        for(int i = 6; i < 11;i++) {
            g.drawLine( rightMargin, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), totalBeatWidth, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
       
        g.setColor(Color.darkGray);
        // draw upper treble stave
        for(int i = -7; i < -2;i++) {
            g.drawLine( rightMargin, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), totalBeatWidth, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
        
        // draw lower bass stave
        for(int i = 13; i < 18;i++) {
            g.drawLine( rightMargin, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), totalBeatWidth, (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
        // draw neext note stave area
        // draw stave
        g.setColor(Color.lightGray);
        for(int i = 0; i < 5;i++) {
            g.drawLine( totalBeatWidth,
                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), 
                     totalBeatWidth + 50, 
                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
        for(int i = 6; i < 11;i++) {
            g.drawLine( totalBeatWidth,
                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), 
                     totalBeatWidth + 50, 
                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
        }
         g.setColor(Color.black);
        // add Clefs
        g.drawImage(trebleClef, rightMargin + 7, bPos - 4, this);
        g.drawImage(bassClef, rightMargin + 7, bPos + staveSpaceHeight * 6, this);
        
        /* Draw completed buffer to g */

        graphics.drawImage(image, 0, 0, null); 
        // clear image
        // clear
	    g.setColor(this.getBackground());
        g.fillRect(0,0, MAX_WIDTH, MAX_HEIGHT);
	    g.setColor(this.getForeground());
        //repaint();               
        //g.dispose();
    }

    private boolean isFirstNoteInTie = true;

    private boolean isNote = false;

    private boolean firstAccidentalDisplayed = false;

    private boolean isTied = false;

    private boolean isUp = true;

    private boolean semitoneShiftUp = false;

    private boolean extraImagesUsed;

    private boolean requiresMoreThanOneImage;

    private double excessRhythmValue;

    private int savedBeatWidth;

    private int savedBeatWidth2;

    private int lastChordDisplayed = -1;

    private int lastPosition = 0;

    private int[] firstChords = new int[0];

    private int[] secondChords = new int[0];

    private String[] chordStrings = {"I", "II", "III", "IV", "V", "VI", "VII",
                                     "."};

    private void drawNote(int notePitchNum, final double rhythmValue,
                          int pitchTempPos, final int keyAccidentals) {
        requiresMoreThanOneImage = false;
        excessRhythmValue = 0.0;

        if ((beatCounter % 1.0) == 0.0) {
            int currentBeat = (int) (beatCounter / 1.0);
            int total = currentBeat - lastChordDisplayed;
            int remaining = total;
            while (lastChordDisplayed < currentBeat) {
                lastChordDisplayed++;

                remaining--;
                g.drawString(chordStrings[firstChords[lastChordDisplayed]],
                        (int) (totalBeatWidth - ((totalBeatWidth - lastPosition)
                                                 * (remaining
                                                    / (double) total))),
                        20);
                int index = secondChords[lastChordDisplayed];
                String string = chordStrings[index];
//                g.drawString(chordStrings[secondChords[lastChordDisplayed]],
                g.drawString(string,
                        (int) (totalBeatWidth - ((totalBeatWidth - lastPosition)
                                                 * (remaining
                                                    / (double) total))),
                        40);
            }
            lastPosition = totalBeatWidth;
        }

        // choose graphic
        chooseImage( notePitchNum, rhythmValue, 71, 60, 50);

        drawNote2(notePitchNum, rhythmValue - excessRhythmValue,
                  pitchTempPos, keyAccidentals);
        if (requiresMoreThanOneImage) {
            drawNote(notePitchNum, excessRhythmValue,
                     pitchTempPos, keyAccidentals);
            extraImagesUsed = true;
        }
    }

    private void drawNote2(int notePitchNum, final double rhythmValue,
                           int pitchTempPos, final int keyAccidentals) {

        // accidental?
        if (((notePitchNum % 12) == 1 || (notePitchNum % 12) == 3 || (notePitchNum % 12) == 6 || (notePitchNum % 12) == 8 || (notePitchNum % 12) == 10) && notePitchNum != REST && rhythmValue != 0.0) {
               if(keySignature > -1) {
               if (! firstAccidentalDisplayed) {
                    displayImage(g, sharp, totalBeatWidth - 9, pitchTempPos);
               }
//                        g.drawImage(sharp, totalBeatWidth - 9, pitchTempPos, this);
                    previouslyChromatic.addElement(new Integer(notePitchNum - 1)); // enter the note made sharp i.e, F for an F#
               } else { // flat
                    pitchTempPos -= 4; // to show the note a semitone higher for flats
                    if (! firstAccidentalDisplayed) {
                        displayImage(g, flat, totalBeatWidth - 9, pitchTempPos);
                            }
//                        g.drawImage(flat, totalBeatWidth - 9, pitchTempPos, this);
                    previouslyChromatic.addElement(new Integer(notePitchNum + 1));
                    notePitchNum++; // assume it is a semitone higher for legerlines etc...
                    semitoneShiftUp = true;
               }
        } else { // check for a natural
            // check vector
            int size = previouslyChromatic.size();
            for(int j=0; j<size; j++) {
            Integer temp = (Integer)previouslyChromatic.elementAt(j);
                if (temp.intValue() == notePitchNum && notePitchNum != REST && rhythmValue != 0.0) {
                    // add natural
                    if (! firstAccidentalDisplayed) {
                        displayImage(g, natural, totalBeatWidth - 7, pitchTempPos);
                    }
//                        g.drawImage(natural, totalBeatWidth - 7, pitchTempPos, this);
                    // remove element if not in key signature
                    if (j>keyAccidentals-1) previouslyChromatic.removeElementAt(j);
                    j = size;
                }
            }
        }

        firstAccidentalDisplayed = true;
         
        // draw note/rest
        displayImage(g, currImage, totalBeatWidth, pitchTempPos);
//            g.drawImage(currImage, totalBeatWidth, pitchTempPos, this);
        // store position in a vector
        notePositions.addElement(new Integer(totalBeatWidth));
        notePositions.addElement(new Integer(pitchTempPos));
        //System.out.println("Position "+i+" "+totalBeatWidth + " "+ pitchTempPos);
        
        if (dottedNote) { 
            boolean dotFlag = true;
            for(int l=0;l<lineNotes.length;l++) {
                if ( lineNotes[l] + 12 == notePitchNum || lineNotes[l] + 36 == notePitchNum || lineNotes[l] + 60 == notePitchNum || lineNotes[l] + 84 == notePitchNum || lineNotes[l] + 108 == notePitchNum || notePitchNum == REST) {
                    displayImage(g, dot, totalBeatWidth + 1, pitchTempPos - 4);
//                        g.drawImage(dot, totalBeatWidth + 1, pitchTempPos - 4, this);
                    dotFlag = false;
                    l = lineNotes.length;
                }
            }
            if (dotFlag) {
                displayImage(g, dot, totalBeatWidth + 1, pitchTempPos);
//                    g.drawImage(dot, totalBeatWidth + 1, pitchTempPos, this);
            }
        }
//        isNormalColor = true;

        // leger lines middle C
        if ( notePitchNum == 60 || notePitchNum == 61 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 52, totalBeatWidth+ 12, bPos + 52);}

        // leger lines down
        if ( notePitchNum <= 40 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 100, totalBeatWidth+ 12, bPos + 100);}
        if ( notePitchNum <= 37 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 108, totalBeatWidth+ 12, bPos + 108);}
        // leger lines down low
        if ( notePitchNum <= 16 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 156, totalBeatWidth+ 12, bPos + 156);}
        if ( notePitchNum <= 13 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 164, totalBeatWidth+ 12, bPos + 164);}
        if ( notePitchNum <= 10 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 172, totalBeatWidth+ 12, bPos + 172);}
        if ( notePitchNum <= 6 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 180, totalBeatWidth+ 12, bPos + 180);}
        if ( notePitchNum <= 3 && notePitchNum > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 188, totalBeatWidth+ 12, bPos + 188);}
        // leger lines up
        if ( notePitchNum >= 81 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 4, totalBeatWidth+ 12, bPos + 4);}
        if ( notePitchNum >= 84 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 4, totalBeatWidth+ 12, bPos - 4);}
        // leger lines up high
        if ( notePitchNum >= 105 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 52, totalBeatWidth + 12, bPos - 52);}
        if ( notePitchNum >= 108 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 60, totalBeatWidth + 12, bPos - 60);}
        if ( notePitchNum >= 112 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 68, totalBeatWidth + 12, bPos - 68);}
        if ( notePitchNum >= 115 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 76, totalBeatWidth + 12, bPos - 76);}
        if ( notePitchNum >= 119 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 84, totalBeatWidth + 12, bPos - 84);}
        if ( notePitchNum >= 122 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 92, totalBeatWidth + 12, bPos - 92);}
        if ( notePitchNum >= 125 && notePitchNum < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 100, totalBeatWidth + 12, bPos - 100);}
        
        // increment everything
        savedBeatWidth2 = totalBeatWidth;

        if ((isTied || extraImagesUsed) && isNote && ! isFirstNoteInTie) {
            Image tieImage = tie.image;
            if (! isNormalColor) {
                tieImage = tie.redImage;
            }

            int yPosition = pitchTempPos + 19 - ((semitoneShiftUp) ? 4 : 0);

            if (isUp) {
                g.drawImage(tieImage,
                            savedBeatWidth - 3 + 9,
                            yPosition + 17 + tieImage.getHeight(this), 
                            savedBeatWidth2 + 19 - 9, 
                            yPosition + 17,
                            0, 0, tieImage.getWidth(this),
                            tieImage.getHeight(this),

                            this);
             } else {
                g.drawImage(tieImage,
                            savedBeatWidth - 3 + 9,
                            yPosition - 20, 
                            savedBeatWidth2 + 19 - 9, 
                            yPosition - 20 + tieImage.getHeight(this),
                            0, 0, tieImage.getWidth(this),
                            tieImage.getHeight(this),
                            this);
             }
        }

        if (isFirstNoteInTie = true) {
            isFirstNoteInTie = false;
        }

        savedBeatWidth = totalBeatWidth;


        totalBeatWidth += currBeatWidth;
        dottedNote = false;
        // quantised to semiquvers!
        // (int)((rhythmValue/0.25) * 0.25);
        beatCounter += (int)(rhythmValue/0.25) * 0.25;
        
        // add bar line if required
        if (metre != 0.0) {
            if ( (beatCounter % metre) == 0.0) {
                g.drawLine( totalBeatWidth , bPos + 12 - staveSpaceHeight*7 , totalBeatWidth, bPos + 44 + staveSpaceHeight * 13);
                // add bar numbers?
                if (barNumbers) g.drawString( ""+(int)(beatCounter/metre +1) , totalBeatWidth - 4 , bPos - 50);
                totalBeatWidth += 12;
            }
        }


    }

    public void displayImage(final Graphics g, final RedFilter image, int xCoord,
                             int yCoord) {
        if (isNormalColor) {
            g.drawImage(image.image, xCoord, yCoord, this);
        } else {
            g.drawImage(image.redImage, xCoord, yCoord, this);
        }
    }
/*

            g.drawImage(createImage(new FilteredImageSource(image.getSource(),
                    new RGBImageFilter() {
                        {
                            canFilterIndexColorModel = true;
                        }

                        public int filterRGB(int x, int y, int rgb) {
                            return (rgb & 0xffff0000);
                        }
                    })), xCoord, yCoord, this);
                                                
            g.drawIma
            int w = image.getWidth(this);
            int h = image.getHeight(this);


            int[] pixels = new int[w * h];
            PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
                System.err.println("interrupted waiting for pixels!");
                return;
            }
            System.out.println("Status: " + pg.getStatus());
            System.out.println("ImageObserver.ABORT : " + ImageObserver.ABORT);
            if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                System.err.println("image fetch aborted or errored");
                return;
            }
            for(int i = 0; i < pixels.length; i++){
                System.out.println(pixels[i]);
            }


            IndexColorModel colorModel = new IndexColorModel(8, pixels.length,
                    pixels, 0, DataBuffer.TYPE_BYTE, null);
            byte[] reds = new byte[pixels.length];
            colorModel.getReds(reds);
            byte[] alphas = new byte[pixels.length];
            colorModel.getAlphas(alphas);

            IndexColorModel redModel = new IndexColorModel(8, pixels.length,
                    reds, new byte[pixels.length], new byte[pixels.length],
                    alphas);
            BufferedImage bufImage = new BufferedImage(w, h,
                    BufferedImage.TYPE_BYTE_INDEXED, redModel);
            g.drawImage(bufImage, xCoord, yCoord, this);
        }
    }
*/


    public Dimension getPreferredSize() {
        return new Dimension(MAX_WIDTH, MAX_HEIGHT);
    }

    
    /**
	 * Sets the current Phrase for this Stave instance
	 * @param Phrase
	 */
    public void setPhrase(Phrase phr) {
        this.phrase = phr;
        previouslyChromatic.removeAllElements();
	setTitle(phr.getTitle());
        repaint();
    }
    
    /**
	 * Returns the current Phrase of this Stave instance
	 */
    public Phrase getPhrase() {
        return phrase;
    }
    
    /**
	 * Sets the name for this Stave instance
	 * @param String
	 */
    public void setTitle(String title) {
        this.title = title;
    }
    
     /**
	 * Returns the name for this Stave instance
	 * @return String
	 */
    public String getTitle() {
        return title;
    }
    /**
	 * Emptys the name of this Stave instance
	 */
    public void removeTitle() {
        this.title = null;
    }
        
    /**
	 * Sets the current metre for this Stave instance
	 * This effects the displayed time signature. 4.0 = 4/4 etc.
	 * @param double
	 */
    public void setMetre(double timeSig) {
        this.metre = timeSig;
    }
    
    /**
	 * returns the current metre for this Stave instance as a double
	 */
    public double getMetre() {
        return this.metre;
    }

    public void setScale(int[] scale) {
        this.scale = scale;
        setTonic(tonic);
    }

    /**
	 * returns the current major key for this Stave instance as a integer
	 * 0 is C, 1 is C#/Db major, 2 is D major, etc
	 */
    public int getTonic() {
        return tonic;
    }

    public void setKey(final int degree, final int[] scale) {
        this.scale = scale;
        setTonic(degree);
    }

    public void setTonic(int degree) {
        if (degree < 0 || degree > 11)  {
            return;
        }

        if (scale == JMC.MAJOR_SCALE) {
            tonic = degree;
        } else if (scale == JMC.NATURAL_MINOR_SCALE) {
            tonic = degree;
            degree = (degree + 3) % 12;
        } else {
            return;
        }

        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == degree) {
                keySignature = i - 7;
                continue;
            }
        }
        repaint();
        if (keyChangeListener == null) {
            return;
        }
        keyChangeListener.keyChanged();
    }

    public void setKeyChangeListener(KeyChangeListener listener) {
        this.keyChangeListener = listener;
    }

    /**
	 * Sets the current key signature for this Stave instance
	 * This effects the displayed key signature. 1 = F# etc.
	 * 0 is no key signature, + numbers for sharps, - numbers for flats
	 * @param int
	 */
    public void setKeySignature(int key) {
        this.keySignature = ((key > 7) ? 7 : ((key < -7)
                                              ? -7
                                              : key));
        if (keyChangeListener == null) {
            return;
        }
        keyChangeListener.keyChanged();

    }
    
    /**
	 * returns the current key signature for this Stave instance as a double
	 */
    public int getKeySignature() {
        return this.keySignature;
    }
    
    /**
	 * Decide to show bar numbers or not
	 * @param boolean
	 */
    public void setBarNumbers(boolean show) {
        this.barNumbers = show;
    }
    
    /**
	 * Decide to allow stave to be editable or not
	 * @param boolean
	 */
    public void setEditable(boolean state) {
        this.editable = state;
    }
    
    /**
	 * returns the current minimum MIDI pitch number
	 */
    public int getMinPitch() {
        return this.minPitch;
    }
    /**
	 * Decide the minimum MIDI pitch number for this stave
	 * @param int
	 */
    public void setMinPitch(int min) {
        this.minPitch = min;
    }
    
    /**
	 * returns the current maximum MIDI pitch number
	 */
    public int getMaxPitch() {
        return this.maxPitch;
    }
    /**
	 * Decide the maxinum MIDI pitch number for this stave
	 * @param int
	 */
    public void setMaxPitch(int max) {
        this.maxPitch = max;
    }
    
    /**
	 * Returns the current next note position in pixels
	 */
    public int getTotalBeatWidth() {
        return this.totalBeatWidth;
    }
    /**
	 * Sets the current width of the stave in pixels
	 * @param int
	 */
    public void setTotalBeatWidth(int width) {
        this.totalBeatWidth = width;
    }
    
    /**
	 * Returns the current state of barNumber showing
	 */
    public boolean getBarNumbers() {
        return barNumbers;
    }
    
    /**
	 * Returns the current state of QuickTime Playback
	 */
    public boolean getQtOn() {
        return qtOn;
    }
    /**
	 * Sets the current state of QuickTime
	 * @param boolean
	 */
    public void setQtOn(boolean state) {
        this.qtOn = state;
    }
    
    /**
	 * Called by stave action on mouseUp
	 * Can be overridden by extending classes
	 * to add functionality
	 */
    public void updateChange() {}
    
    // override update for double buffering
//    public void update(Graphics g) {
//        paint(g);
//    };
    
    
    /**
    * Remove the last note from the phrase
    */
    public void deleteLastNote() {
        if(phrase.size() > 0) {
            phrase.removeNote(phrase.size() -1);
            repaint();
            updateChange();
        }
    }
    
    protected void chooseImage(int pitch, double rhythmValue, int upPitch1, int downPitch, int upPitch2) {
        //System.out.println("Pitch = " + pitch + " RV = " + rhythmValue);
        if(pitch == REST) { // pick a rest
            isNote = false;
            if ( rhythmValue <= 0.0) {
                currImage = delete;
                currBeatWidth  = (int)( beatWidth * 0.5);
              }
           if (  rhythmValue > 0.0  && rhythmValue <= 0.25) {
                currImage = semiquaverRest;
                currBeatWidth  = (int)( beatWidth * 0.5);
              }
           if ( rhythmValue > 0.25 && rhythmValue <= 0.5) {
                currImage = quaverRest;
                currBeatWidth  = (int)(beatWidth * 0.67);
              }
           if ( rhythmValue > 0.5 && rhythmValue <= 0.75) {
                currImage = quaverRest;
                currBeatWidth  = (int)(beatWidth * 0.67);
                dottedNote = true;
              }
           if ( rhythmValue > 0.75 && rhythmValue <= 1.0) {
                currImage = crotchetRest;
                currBeatWidth  = beatWidth;
              }
           if ( rhythmValue > 1.0 && rhythmValue <= 1.25) {
                currImage = crotchetRest;
                currBeatWidth  = beatWidth;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 1.0;
           }
           if ( rhythmValue > 1.25 && rhythmValue <= 1.5) {
                currImage = crotchetRest;
                currBeatWidth  = (int)( beatWidth * 1.5);
                dottedNote = true;
              }
           if (rhythmValue > 1.5 && rhythmValue <= 1.75) {
                currImage = crotchetRest;
                currBeatWidth  = (int)( beatWidth * 1.5);
                dottedNote = true;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 1.5;
           }

           if ( rhythmValue > 1.75 && rhythmValue <= 2.0) {
                currImage = minimRest;
                currBeatWidth  = (int)( beatWidth * 1.7);
              }
           if ( rhythmValue > 2.0 && rhythmValue <= 2.75) {
                currImage = minimRest;
                currBeatWidth  = (int)( beatWidth * 1.7);
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 2.0;
           }
           if ( rhythmValue > 2.75 && rhythmValue <= 3.0) {
                currImage = minimRest;
                currBeatWidth  = (int)( beatWidth * 1.9);
                dottedNote = true;
              }
           if ( rhythmValue > 3.0 && rhythmValue <= 3.75) {
                currImage = minimRest;
                currBeatWidth  = (int)( beatWidth * 1.9);
                dottedNote = true;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue -  3.0;
           }                   

           if ( rhythmValue > 3.75 && rhythmValue <= 4.0) {
                currImage = semibreveRest;
                currBeatWidth  = (int)( beatWidth * 0.5);
              }                   
        } else { // a note rather than a rest
            isNote = true;
            if ((pitch < upPitch1 && pitch >= downPitch) || pitch < upPitch2 ) { // stems down 
                if ( rhythmValue <= 0.0) {
                    currImage = delete;
                    currBeatWidth  = (int)( beatWidth * 0.5);
                    isUp = true;
                }
                if ( rhythmValue > 0.0  && rhythmValue <= 0.25) {
                        currImage = semiquaverUp;
                        currBeatWidth  = (int)( beatWidth * 0.5);
                    isUp = true;
                      }
                if ( rhythmValue > 0.25 && rhythmValue <= 0.5) {
                        currImage = quaverUp;
                        currBeatWidth  = (int)( beatWidth * 0.67);
                    isUp = true;
                   }
               if ( rhythmValue > 0.5 && rhythmValue <= 0.75) {
                    currImage = quaverUp;
                    currBeatWidth  = (int)(beatWidth * 0.67);
                    dottedNote = true;
                    isUp = true;
                  }
	           if ( rhythmValue > 0.75 && rhythmValue <= 1.0) {
	                currImage = crotchetUp;
	                currBeatWidth  = beatWidth;
                    isUp = true;
	              }
               if ( rhythmValue > 1.0 && rhythmValue <= 1.25) {
	                currImage = crotchetUp;
	                currBeatWidth  = beatWidth;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.0;
                    isUp = true;
               }

               if ( rhythmValue > 1.25 && rhythmValue <= 1.5) {
                    currImage = crotchetUp;
                    currBeatWidth  = (int)( beatWidth * 1.5);
                    dottedNote = true;
                    isUp = true;
                  }
               if ( rhythmValue > 1.5 && rhythmValue <= 1.75) {
                    currImage = crotchetUp;
                    currBeatWidth  = (int)( beatWidth * 1.5);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.5;
                    isUp = true;
               }
               if ( rhythmValue > 1.75 && rhythmValue <= 2.0) {
	                currImage = minimUp;
	                currBeatWidth  = (int)( beatWidth * 1.7);
                    isUp = true;
	              }
               if ( rhythmValue > 2.0 && rhythmValue <= 2.75) {
	                currImage = minimUp;
	                currBeatWidth  = (int)( beatWidth * 1.7);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 2.0;
                    isUp = true;
               }
               if ( rhythmValue > 2.75 && rhythmValue <= 3.0) {
                    currImage = minimUp;
                    currBeatWidth  = (int)( beatWidth * 1.9);
                    dottedNote = true;
                    isUp = true;
                  }
               if ( rhythmValue > 3.0 && rhythmValue <= 3.75) {
                    currImage = minimUp;
                    currBeatWidth  = (int)( beatWidth * 1.9);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 3.0;
                    isUp = true;
                  }

               if ( rhythmValue > 3.75 && rhythmValue <= 4.0) {
	                currImage = semibreve;
	                currBeatWidth  = (int)( beatWidth * 2.25);
                    isUp = true;
	              }
            } else { // stem down
                if ( rhythmValue <= 0.0) {
                    currImage = delete;
                    currBeatWidth  = (int)( beatWidth * 0.5);
                    isUp = false;
                }
                if (  rhythmValue > 0.0  && rhythmValue <= 0.25) {
                        currImage = semiquaverDown;
                        currBeatWidth  = (int)( beatWidth * 0.5);
                    isUp = false;
                      }
                if ( rhythmValue > 0.25 && rhythmValue <= 0.5) {
                        currImage = quaverDown;
                        currBeatWidth  = (int)( beatWidth * 0.67);
                    isUp = false;
                    }
               if ( rhythmValue > 0.5 && rhythmValue <= 0.75) {
                    currImage = quaverDown;
                    currBeatWidth  = (int)(beatWidth * 0.67);
                    dottedNote = true;
                    isUp = false;
                }
	           if ( rhythmValue > 0.75 && rhythmValue <= 1.0) {
	                currImage = crotchetDown;
	                currBeatWidth  = beatWidth;
                    isUp = false;
	              }
               if ( rhythmValue > 1.0 && rhythmValue <= 1.25) {
	                currImage = crotchetDown;
	                currBeatWidth  = beatWidth;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.0;
                    isUp = false;
               }

               if ( rhythmValue > 1.25 && rhythmValue <= 1.5) {
                    currImage = crotchetDown;
                    currBeatWidth  = (int)( beatWidth * 1.5);
                    dottedNote = true;
                    isUp = false;
                  }
               if ( rhythmValue > 1.5 && rhythmValue <= 1.75) {
                    currImage = crotchetDown;
                    currBeatWidth  = (int)( beatWidth * 1.5);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.5;
                    isUp = false;
                  }
               if ( rhythmValue > 1.75 && rhythmValue <= 2.0) {
	                currImage = minimDown;
	                currBeatWidth  = (int)( beatWidth * 1.7);
                    isUp = false;
	              }
               if ( rhythmValue > 2.0 && rhythmValue <= 2.75) {
	                currImage = minimDown;
	                currBeatWidth  = (int)( beatWidth * 1.7);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 2.0;
                    isUp = false;
	              }

               if ( rhythmValue > 2.75 && rhythmValue <= 3.0) {
                    currImage = minimDown;
                    currBeatWidth  = (int)( beatWidth * 1.9);
                    dottedNote = true;
                    isUp = false;
                  }
               if ( rhythmValue > 3.0 && rhythmValue <= 3.75) {
                    currImage = minimDown;
                    currBeatWidth  = (int)( beatWidth * 1.9);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 3.0;
                    isUp = false;

                  }

               if ( rhythmValue > 3.75 && rhythmValue <= 4.0) {
	                currImage = semibreve;
	                currBeatWidth  = (int)( beatWidth * 2.25);
                    isUp = false;
	              }
            }
        }
    }

}
        
