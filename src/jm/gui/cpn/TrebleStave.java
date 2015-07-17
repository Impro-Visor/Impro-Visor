/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000, 2001 Andrew Brown, Adam Kirby

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

package jm.gui.cpn; 

import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.net.URL; 
import jm.JMC;
import jm.music.data.*;
import jm.util.*;

import jm.music.tools.PhraseAnalysis;
// import jm.music.tools.ChordAnalysis;


/**
 * Represents a treble clef stave.
 *
 * @author  Andrew Brown, Adam Kirby
 * @version 1.0.1, 8th July 2001
 */
public class TrebleStave extends Stave implements JMC{

    private static final class Accidental {

        public static final Accidental NONE = new Accidental("none");

        public static final Accidental SHARP = new Accidental("sharp");

        public static final Accidental NATURAL = new Accidental("natural");

        public static final Accidental FLAT = new Accidental("flat");

        private String name;

        // Due to a 1.1 compiler bug this constructor cannot be private 
        Accidental(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * Defines a type representing the rules and logic of when accidentals
     * should be dispalyed against notes on the stave.
     *
     * Note: In jMusic version 1.2 and earlier this inner class was previously 
     * called AccidentalDisplayStyle.
     */
    public static abstract class Style {

        int[] sharpPitches = {77, 72, 79, 74, 69, 76, 71};

        int[] flatPitches = {71, 76, 69, 74, 67, 72, 65};

        /**
         * Defines the standard style of displaying accidentals in a Common
         * Practice Notation stave.
         */
        public static final Style TRADITIONAL = new Trad();

        /**
         * Defines a style unique to jMusic, which displays an accidental in
         * all situations where the status (sharp/flat/natural) of a note may
         * be unclear.
         *     
         * Note: In jMusic version 1.2 and earlier this field was previously 
         * called SUPERFLUOUS_SHARPS_AND_FLATS.
         */
        public static final Style JMUSIC = new JMusic();

        private static final class Trad extends Style {

                    private boolean[] accidentalRequiredByKeySignature =
                            new boolean[12];

                    private static final int[] SHARP_ACCIDENTAL_PAIRS =
                            { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6 };

                    private static final int[] FLAT_ACCIDENTAL_PAIRS =
                            { 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6 };

                    private int[] degreeToAccidentalPair =
                            SHARP_ACCIDENTAL_PAIRS;

                    private boolean[] accidentalInEffect =
                            new boolean[7];

                    private int keySignature = 0;

                    public Trad() {
                        super("Traditional style");
                        this.initialise(0);
                    }

                    private void setBooleanArrayToFalse(boolean[] array) {
                        for (int i = 0; i < array.length; i++) {
                            array[i] = false;
                        }
                    }

                    void initialise(final int keySignature) {
                        this.keySignature = keySignature;
                        if (keySignature < 0) {
                            degreeToAccidentalPair = FLAT_ACCIDENTAL_PAIRS;
                        } else {
                            degreeToAccidentalPair = SHARP_ACCIDENTAL_PAIRS;
                        }
                        this.setBooleanArrayToFalse(
                                accidentalRequiredByKeySignature);
                        accidentalRequiredByKeySignature[1] = true;
                        accidentalRequiredByKeySignature[3] = true;
                        accidentalRequiredByKeySignature[6] = true;
                        accidentalRequiredByKeySignature[8] = true;
                        accidentalRequiredByKeySignature[10] = true;
                        for (int i = 0; i < Math.abs(keySignature); i++) {
                            if (keySignature < 0) {
                                accidentalRequiredByKeySignature[
                                        flatPitches[i] % 12] = true;
                                accidentalRequiredByKeySignature[
                                        (flatPitches[i] - 1) % 12] = false;
                            } else {
                                accidentalRequiredByKeySignature[
                                        sharpPitches[i] % 12] = true;
                                accidentalRequiredByKeySignature[
                                        (sharpPitches[i] + 1) % 12] = false;
                            }
                        }
                        this.setBooleanArrayToFalse(accidentalInEffect);
                    }
    
                    Accidental selectAccidental(
                                final int pitch,
                                final double rhythmValue) {
                        if (pitch == Note.REST
                                || rhythmValue == 0.0) {
                            return Accidental.NONE;
                        }

                        int degree = pitch % 12; // relative to C not tonic

                        int accidentalPair = degreeToAccidentalPair[degree];
                        if (accidentalRequiredByKeySignature[degree]
                                ^ accidentalInEffect[accidentalPair]) {

                            accidentalInEffect[accidentalPair] =
                                    ! accidentalInEffect[accidentalPair];

                            if (degree == 1 || degree == 3 || degree == 6
                                    || degree == 8 || degree == 10) {
                                if (keySignature > -1) {
                                    return Accidental.SHARP;
                                } else {
                                    return Accidental.FLAT;
                                }
                            } else {
                                return Accidental.NATURAL;
                            }
                        }
                        return Accidental.NONE;
                    }
    
                    void processBarLine() {
                        this.setBooleanArrayToFalse(accidentalInEffect);
                    }
                };

        private static final class JMusic extends Style {

                    private Vector chromaticallyAffectedPitches = new Vector();

                    /**
                     * Key signature encoded as a signed accidental count.  The
                     * following conditions apply:
                     * <ul>
                     *   <li />0 represents no sharps of flats.
                     *   <li />positive <i>n</i> represents <i>n</i> sharps
                     *   <li />negative <i>n</i> represents <i>n</n> flats
                     * </ul>
                     */
                    private int keySignature;

                    // had difficulty finding a better name because I don't
                    // really understand what this variable is and does.  It
                    // seems to be closely related to previouslyChromatic. It
                    // seems to be a count of accidentals, but in all staves of
                    // the range of pitches in the MIDI spec.  So a G Major
                    // scale would add 1 to the count for the F# in the treble
                    // stave, plus 1 for each F# in octaves above and below
                    // that.
                    // Odd.
                    private int keyAccidentals;
    
                    public JMusic() {
                        super("JMusic style (with superfluous sharps and "
                              + "flats)");
                        this.initialise(0);
                    }

                    void initialise(final int keySignature) {
                        chromaticallyAffectedPitches = new Vector();
                        this.keySignature = keySignature;
                        keyAccidentals = 0;
                        if (keySignature > 0 && keySignature < 8) {
                            for (int i = 0; i < keySignature; i++) {
                                int degree = sharpPitches[i] % 12;
                                for (int j = (int)Note.MIN_PITCH;
                                        j <= (int)Note.MAX_PITCH;
                                        j++) {
                                    if ((j % 12) == degree) {
                                        chromaticallyAffectedPitches.addElement(
                                                new Integer(j));
                                        keyAccidentals++;
                                    }
                                }
                            }
                        } else if (keySignature < 0 && keySignature > -8) {
                            for (int i = 0; i > keySignature; i--) {
                                int degree = flatPitches[-i] % 12;
                                for (int j = (int)Note.MIN_PITCH;
                                        j <= (int)Note.MAX_PITCH;
                                        j++) {
                                    if ((j % 12) == degree) {
                                        chromaticallyAffectedPitches.addElement(
                                                new Integer(j));
                                        keyAccidentals++;
                                    }
                                }
                            }
                        }
                    }
    
                    Accidental selectAccidental(
                                final int pitch,
                                final double rhythmValue) {
                        if (pitch == Note.REST
                                || rhythmValue == 0.0) {
                            return Accidental.NONE;
                        }
                        if ((pitch % 12) == 1 || (pitch % 12) == 3
                                || (pitch % 12) == 6 || (pitch % 12) == 8
                                || (pitch % 12) == 10) {
                            if (keySignature > -1) {
                                chromaticallyAffectedPitches.addElement(
                                        new Integer(pitch - 1));
                                return Accidental.SHARP;
                            } else {
                                chromaticallyAffectedPitches.addElement(
                                        new Integer(pitch + 1));
                                return Accidental.FLAT;
                            }
                        } else {
                            int size = chromaticallyAffectedPitches.size();
                            int temp;
                            for(int j = 0; j < size; j++) {
                                temp = ((Integer)
                                        chromaticallyAffectedPitches.elementAt(
                                                j)).intValue();
                                if (temp == pitch) {
                                    if (j > keyAccidentals-1) {
                                        chromaticallyAffectedPitches.
                                                removeElementAt(j);
                                    }
                                    return Accidental.NATURAL;
                                }
                            }
                        }
                        return Accidental.NONE;
                    }
    
                    void processBarLine() {
                        // do nothing
                    }
                };

        private String name;

        // Due to a 1.1 compiler bug this constructor cannot be private 
        Style(String name) {
            this.name = name;
        }

        public String toString() {
            return name + " of displaying accidentals";
        }

        abstract void initialise(final int keySignature);

        abstract Accidental selectAccidental(
                    final int pitch,
                    final double rhythmValue);

        abstract void processBarLine();
    }

    private Style style = new Style.JMusic();

    /**
     * Sets the display style of accidentals for this stave.
     *
     * @param ads   Style to be used by this stave.
     */
    public void setAccidentalDisplayStyle(Style ads) {
        if (ads == Style.TRADITIONAL) {
            this.style = new Style.Trad();
        } else if (ads == Style.JMUSIC) {
            this.style = new Style.JMusic();
        } else {
            throw new RuntimeException("Unknown Accidental Display Style");
        }
    }


    private int tonic = 0;

    protected int[] scale = JMC.MAJOR_SCALE;

    public static final int MAX_HEIGHT = 500;

    public static final int MAX_WIDTH = 2000;

    /**
     * Constructs a new treble stave to display a blank Phrase using the default
     * stave images.
     */
    public TrebleStave() {
        super();
    }

    /**
     * Constructs a new treble stave to display the specified
     * <code>phrase</code> using the default stave images.
     *
     * @param phrase    Phrase to be displayed in stave
     */
    public TrebleStave(final Phrase phrase) {
        super(phrase);
    }
    
    /**
     * Constructs a new treble stave to display a blank Phrase using the
     * specified stave <code>images</code>.
     *
     * @param images    Images representing notes, rest and other stave elements
     *                  to use within the compenent
     */
    public TrebleStave(final Images images) {
        super(images);
    }

    /**
     * Constructs a new treble stave to display the specified
     * <code>phrase</code> using the specified stave <code>images</code>.
     *
     * @param phrase    Phrase to be displayed in stave
     * @param images    Images representing notes, rest and other stave elements
     *                  to use within the compenent
     */
    public TrebleStave(final Phrase phrase, final Images images) {
        super(phrase, images);
    }

    private double beatCounter;

    public void paint(Graphics graphics) {
//        if (phrase == null) {
//            return;
//        }

        // set up for double buffering
        if(image == null) {
            image = this.createImage(MAX_WIDTH, MAX_HEIGHT);
	        g = image.getGraphics();
	    }
        // set font
        g.setFont(font);
        // keep track of the rhythmic values for bar lines
        beatCounter = 0.0;
        // reste note position locations
        notePositions.removeAllElements();
        // add a title if set to be visible
        if(getDisplayTitle()) g.drawString(title, rightMargin, bPos - 10);
        // insert key signature if required
        int keyOffset = 0;

        style.initialise(keySignature);

        // is the key signature using sharps or flats?
        if (keySignature > 0 && keySignature < 8) { // sharp
            for(int ks=0;ks<keySignature; ks++) {
                // claulate position
                int keyAccidentalPosition = notePosOffset[ sharps[ks]%12] + bPos - 4 + (( 5- sharps[ks]/12) * 24) + (( 6- sharps[ks]/12) * 4);
                // draw sharp on treble
                g.drawImage(sharp, rightMargin + clefWidth + keyOffset, keyAccidentalPosition, this);
                // indent position
                keyOffset += 10;

                keySigWidth = keyOffset;
            }
        } else {
            if (keySignature < 0 && keySignature > -8) { // flat
                for(int ks=0;ks< Math.abs(keySignature); ks++) {
                    // claulate position
                    int keyAccidentalPosition = notePosOffset[ flats[ks]%12] + bPos - 4 + (( 5- flats[ks]/12) * 24) + (( 6- flats[ks]/12) * 4);
                    // draw flat
                    g.drawImage(flat, rightMargin + clefWidth + keyOffset, keyAccidentalPosition, this);
                    // indent position
                    keyOffset += 10;
                }
            }
        }
        keySigWidth = keyOffset + 3;
        
        // insert time signature if required
        if ( metre != 0.0) {
            Image[] numbers = {one, two, three, four, five, six, seven, eight, nine};
            
            // top number
            g.drawImage(numbers[(int)metre - 1], rightMargin + clefWidth + keySigWidth, bPos + 13, this);
            //bottom number
            g.drawImage(four, rightMargin + clefWidth + keySigWidth , bPos + 29, this);
            timeSigWidth = 30;
        } else timeSigWidth = 5;
        // set indent position for first note
        totalBeatWidth = rightMargin + clefWidth + keySigWidth + timeSigWidth;

//        firstChords =
//                ChordAnalysis.getFirstPassChords(phrase, 1.0, tonic,
//                                                 scale);
//        secondChords =
//                ChordAnalysis.getSecondPassChords(phrase, 1.0, tonic,
//                                                  scale);
        lastChordDisplayed = -1;

        // draw notes and rests
        for(int i = 0; i < phrase.size();i++) {
            int notePitchNum = (int)phrase.getNote(i).getPitch();
            // reset pitch for rests
                        
            // position?
            int pitchTempPos;
            if ( notePitchNum == REST || phrase.getNote(i).getRhythmValue() == 0.0) { // rest or delete
                pitchTempPos = notePosOffset[71%12] + bPos - 4 + (( 5- 71/12) * 24) + (( 6- 71/12) * 4);
            } else {
                pitchTempPos = notePosOffset[notePitchNum%12] + bPos - 4 + (( 5- notePitchNum/12) * 24) + (( 6- notePitchNum/12) * 4);
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
                         pitchTempPos);
                rhythmValue -= rvToEndOfBar;
                rvToEndOfBar = metre - (beatCounter % metre);
            }


            drawNote(notePitchNum, rhythmValue, pitchTempPos);


        }
















        
        // draw treble stave
        for(int i = 0; i < 5;i++) {
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
//        for(int i = 6; i < 11;i++) {
//            g.drawLine( totalBeatWidth,
//                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight), 
//                     totalBeatWidth + 50, 
//                     (bPos + imageHeightOffset - (2* staveSpaceHeight)) +(i* staveSpaceHeight));
//        }
         g.setColor(Color.black);
        // add Clefs
        g.drawImage(trebleClef, rightMargin + 7, bPos - 4, this);
//        g.drawImage(bassClef, rightMargin + 7, bPos + staveSpaceHeight * 6, this);
        
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

//    private boolean isNote = false;

    private boolean firstAccidentalDisplayed = false;

    private boolean isTied = false;

//    private boolean isUp = true;

    private boolean semitoneShiftUp = false;

    private boolean extraImagesUsed;

//    private boolean requiresMoreThanOneImage;

//    private double excessRhythmValue;

    private int savedBeatWidth;

    private int savedBeatWidth2;

    private int lastChordDisplayed = -1;

    private int lastPosition = 0;

//    private int[] firstChords = new int[0];

//    private int[] secondChords = new int[0];

    private String[] chordStrings = {"I", "II", "III", "IV", "V", "VI", "VII",
                                     "."};

    private void drawNote(int notePitchNum, final double rhythmValue,
                          int pitchTempPos) {
        requiresMoreThanOneImage = false;
        excessRhythmValue = 0.0;

//        if ((beatCounter % 1.0) == 0.0) {
//            int currentBeat = (int) (beatCounter / 1.0);
//            int total = currentBeat - lastChordDisplayed;
//            int remaining = total;
//            while (lastChordDisplayed < currentBeat) {
//                lastChordDisplayed++;
//
//                remaining--;
//                g.drawString(chordStrings[firstChords[lastChordDisplayed]],
//                        (int) (totalBeatWidth - ((totalBeatWidth - lastPosition)
//                                                 * (remaining
//                                                    / (double) total))),
//                        20);
//                int index = secondChords[lastChordDisplayed];
//                String string = chordStrings[index];
////                g.drawString(chordStrings[secondChords[lastChordDisplayed]],
//                g.drawString(string,
//                        (int) (totalBeatWidth - ((totalBeatWidth - lastPosition)
//                                                 * (remaining
//                                                    / (double) total))),
//                        40);
//            }
//            lastPosition = totalBeatWidth;
//        }

        // choose graphic
        chooseImage( notePitchNum, rhythmValue, 71, 0, 71);

        drawNote2(notePitchNum, rhythmValue - excessRhythmValue,
                  pitchTempPos);
        if (requiresMoreThanOneImage) {
            drawNote(notePitchNum, excessRhythmValue,
                     pitchTempPos);
            extraImagesUsed = true;
        }
    }

    private void drawNote2(int pitch, final double rhythmValue,
                           int yCoordinate) {

        // draw accidental

        if (pitch != Note.REST && rhythmValue != 0.0) {
            Accidental accidental =
                    style.selectAccidental(pitch, rhythmValue);
            if (accidental == Accidental.SHARP) {
               if (! firstAccidentalDisplayed) {
                    displayImage(g, sharp, totalBeatWidth - 9, yCoordinate);
               }
               // enter the note made sharp i.e, F for an F#
            } else if (accidental == Accidental.FLAT) {
                yCoordinate -= 4; // to show the note a semitone higher for flats
                if (! firstAccidentalDisplayed) {
                    displayImage(g, flat, totalBeatWidth - 9, yCoordinate);
                }
                pitch++; // assume it is a semitone higher for legerlines etc...
                semitoneShiftUp = true;
            } else if (accidental == Accidental.NATURAL) {
                if (! firstAccidentalDisplayed) {
                    displayImage(g, natural, totalBeatWidth - 7, yCoordinate);
                }
            }
        }
    
        firstAccidentalDisplayed = true;
         
        // draw note/rest
        displayImage(g, currImage, totalBeatWidth, yCoordinate);

        // store position in a vector
        notePositions.addElement(new Integer(totalBeatWidth));
        notePositions.addElement(new Integer(yCoordinate));
        
        if (dottedNote) { 
            boolean dotFlag = true;
            for(int l = 0; l < lineNotes.length; l++) {
                if (lineNotes[l] + 12 == pitch
                        || lineNotes[l] + 36 == pitch
                        || lineNotes[l] + 60 == pitch
                        || lineNotes[l] + 84 == pitch
                        || lineNotes[l] + 108 == pitch
                        || pitch == REST) {
                    displayImage(g, dot, totalBeatWidth + 1, yCoordinate - 4);
                    dotFlag = false;
                    l = lineNotes.length;
                }
            }
            if (dotFlag) {
                displayImage(g, dot, totalBeatWidth + 1, yCoordinate);
            }
        }

        // leger lines down
        if (pitch <= 61 && pitch > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 52, totalBeatWidth+ 12, bPos + 52);}
        if ( pitch <= 58 && pitch > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 60, totalBeatWidth+ 12, bPos + 60);}
        if ( pitch <= 54 && pitch > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 68, totalBeatWidth+ 12, bPos + 68);}
        if ( pitch <= 51 && pitch > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 76, totalBeatWidth+ 12, bPos + 76);}
        if ( pitch <= 48 && pitch > -1 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 84, totalBeatWidth+ 12, bPos + 84);}
        // leger lines up
        if ( pitch >= 81 && pitch < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 4, totalBeatWidth+ 12, bPos + 4);}
        if ( pitch >= 84 && pitch < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 4, totalBeatWidth+ 12, bPos - 4);}
        if ( pitch >= 88 && pitch < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 12, totalBeatWidth+ 12, bPos - 12);}
        if ( pitch >= 91 && pitch < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 20, totalBeatWidth+ 12, bPos - 20);}
        if ( pitch >= 95 && pitch < 128 && rhythmValue != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 28, totalBeatWidth+ 12, bPos - 28);}
      
        // increment everything
        savedBeatWidth2 = totalBeatWidth;

        if ((isTied || extraImagesUsed) && isNote && ! isFirstNoteInTie) {

            int yPosition = yCoordinate + 19 - ((semitoneShiftUp) ? 4 : 0);

            if (isUp) {
                g.drawImage(tieUnder,
                            savedBeatWidth - 3 + 9,
                            yPosition + 17,
                            savedBeatWidth2 + 19 - 9, 
                            yPosition + 17 + tieUnder.getHeight(this), 
                            0, 0, tieUnder.getWidth(this),
                            tieUnder.getHeight(this),

                            this);
             } else {
                g.drawImage(tieOver,
                            savedBeatWidth - 3 + 9,
                            yPosition - 20, 
                            savedBeatWidth2 + 19 - 9, 
                            yPosition - 20 + tieOver.getHeight(this),
                            0, 0, tieOver.getWidth(this),
                            tieOver.getHeight(this),
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
        
        
        // draw bar line
        if (metre != 0.0) {
            if ( (beatCounter % metre) == 0.0) {
                g.drawLine( totalBeatWidth , bPos + 12, totalBeatWidth, bPos + 44);
                style.processBarLine();
                // add bar numbers?
                if (barNumbers) g.drawString( ""+(int)(beatCounter/metre +1 + phrase.getStartTime()) , totalBeatWidth - 4 , bPos);
                totalBeatWidth += 12;
            }
        }


    }

    private void displayImage(final Graphics g, final Image image, int xCoord,
                             int yCoord) {
        g.drawImage(image, xCoord, yCoord, this);
    }
}
