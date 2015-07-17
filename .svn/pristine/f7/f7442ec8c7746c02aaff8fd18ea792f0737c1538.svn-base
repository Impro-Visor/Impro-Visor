/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Brown, Adam Kirby

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

// Constants representing rhythm values adjusted by 
// Al Christians to try to make CPN look better after 
// tempo had been changed, etc.  Changes questionable.  
// IDK.

package jm.gui.cpn; 

import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.net.URL; 
import jm.JMC;
import jm.music.data.*;


/**
 * An AWT Component for displaying a Common Practice Notation stave.
 *
 * @author Andrew Brown, Adam Kirby
 * @version 8th July 2001
 */
public abstract class Stave extends Panel implements JMC, KeyListener {

// Commented out due to a JDK1.1 compiler bug
//    public static final Images DEFAULT_IMAGES = new ToolkitImages();

    protected boolean requiresMoreThanOneImage = false;

    protected double excessRhythmValue = 0.0;

    protected boolean isUp = true;

    protected boolean isNote = false;


    // for double buffering
    public Image image;
    protected Graphics g;
    // attributes
    protected Image trebleClef, bassClef, crotchetUp, crotchetDown, quaverDown, quaverUp, 
                    semiquaverDown, semiquaverUp, minimDown, minimUp, semibreve, dot,
                    semiquaverRest, quaverRest, crotchetRest, minimRest, semibreveRest,
                    sharp, flat, natural, one, two, three, four, five, six,
                    seven, eight, nine, delete, tieOver, tieUnder;
    public int staveSpaceHeight = 8, rightMargin = 20, beatWidth = 43, staveWidth = beatWidth*15,  
            imageHeightOffset = 28, clefWidth = 38, timeSigWidth = 5, keySigWidth = 5;
    public int bPos = 28;
    protected Phrase phrase;
    protected Image currImage;
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
    protected boolean displayTitle = false;
    protected Font font = new Font("Helvetica", Font.PLAIN, 10);
    protected int spacingValue = 70;


    // constructor
    /**
     * Constructs a new stave to display a blank Phrase using the default stave
     * images.
     */
    public Stave () {
       this(new Phrase(), new ToolkitImages());
    }

    /**
     * Constructs a new stave to display the specified <code>phrase</code> using
     * the default stave images.
     *
     * @param phrase    Phrase to be displayed in stave
     */
    public Stave(Phrase phrase) {
        this(phrase, new ToolkitImages());
    }

    /**
     * Constructs a new stave to display a blank Phrase using the specified
     * stave <code>images</code>.
     *
     * @param images    Images representing notes, rest and other stave elements
     *                  to use within the compenent
     */
    public Stave(Images images) {
        this(new Phrase(), images);
    }

    /**
     * Constructs a new stave to display the specified <code>phrase</code> using
     * the specified stave <code>images</code>.
     *
     * @param phrase    Phrase to be displayed in stave
     * @param images    Images representing notes, rest and other stave elements
     *                  to use within the compenent
     */
    public Stave (Phrase phr, Images images) {
        super();
        title = phr.getTitle();
        this.phrase = addRequiredRests(phr);
        // change 'paper' colour
        this.setBackground(Color.getHSBColor((float)0.14,(float)0.09,(float)1.0)); // .17, .1, 1
        // set the appropriate size (at least 8 bars of 4/4) for the stave
        this.setSize((int)(beatWidth*spacingValue), panelHeight);
        if (this.getSize().width < (int)(phrase.getEndTime()* beatWidth * 1.5) ) 
            this.setSize( (int)(phrase.getEndTime()* beatWidth * 1.5), panelHeight);

        // compensate for overly large images - pain!!
        //if (this.getSize().width > 5000) {
         //   this.setSize(5000, panelHeight);
        //    System.out.println("Not all the phrase can be shown due to overly large image requirements - sorry");
        //}
        //System.out.println("Max size is "+this.getMaximumSize().width +" "+ this.getMaximumSize().height);

        // register the listerners
        StaveActionHandler handleActions = new StaveActionHandler(this);
        this.addMouseListener(handleActions);
        this.addMouseMotionListener(handleActions);
//        this.addKeyListener(handleActions);


        trebleClef = images.getTrebleClef();
        bassClef = images.getBassClef();
        crotchetDown = images.getCrotchetDown();
        crotchetUp = images.getCrotchetUp();
        quaverDown = images.getQuaverDown();
        quaverUp = images.getQuaverUp();
        semiquaverDown = images.getSemiquaverDown();
        semiquaverUp = images.getSemiquaverUp();
        minimDown = images.getMinimDown();
        minimUp = images.getMinimUp();
        semibreve = images.getSemibreve();
        dot = images.getDot();
        semiquaverRest = images.getSemiquaverRest();
        quaverRest = images.getQuaverRest();
        crotchetRest = images.getCrotchetRest();
        minimRest = images.getMinimRest();
        semibreveRest = images.getSemibreveRest();
        sharp = images.getSharp();
        flat = images.getFlat();
        natural = images.getNatural();
        one = images.getOne();
        two = images.getTwo();
        three = images.getThree();
        four = images.getFour();
        five = images.getFive();
        six = images.getSix();
        seven = images.getSeven();
        eight = images.getEight();
        nine = images.getNine();
        delete = images.getDelete();
        tieOver = images.getTieOver();
        tieUnder = images.getTieUnder();
    } 
    
    /*
    * Puts rests at the start of an phrase that does not
    * start at time 0.0.
    */
    public Phrase addRequiredRests(Phrase phrase) {
        // add rests if required at the start
        if (phrase.getStartTime() > 0.0) {
            Phrase tempPhrase = new Phrase(0.0);
            double remTime = phrase.getStartTime();
            while(remTime >= 4.0) {
                tempPhrase.addNote(REST, 4.0);
                remTime -= 4.0;
            }
            while(remTime >= 1.0) {
                tempPhrase.addNote(REST, 1.0);
                remTime -= 1.0;
            }
            tempPhrase.addNote(REST, remTime);
            jm.music.tools.Mod.append(tempPhrase, phrase);
            phrase = tempPhrase;
        }
        return phrase;
    }
    
    /**
     * Sets the current Phrase for this Stave instance
    * @param Phrase
    */
    public void setPhrase(Phrase phr) {
        this.phrase = addRequiredRests(phr);
        previouslyChromatic.removeAllElements();
	//setTitle(phr.getTitle());
        repaint();
    }
    
    /**
    * Returns the current Phrase of this Stave instance
    */
    public Phrase getPhrase() {
        return this.phrase;
    }
    
    /**
    * Sets the name for this Stave instance
    * @param String Specify the title of the score
    */
    public void setTitle(String title) {
        this.title = title;
        if(this.phrase != null) this.phrase.setTitle(title);
    }
    
    /**
    * Returns the name for this Stave instance
    * @return String The title of the score
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
    * Show the title or not.
    * @param value True or false
    */
    public void setDisplayTitle(boolean value) {
        this.displayTitle = value;
        this.repaint();
    }
    
    /**
    * Is the title displayed or not.
    * @param value True or false
    */
    public boolean getDisplayTitle() {
        return this.displayTitle;
    }
    
    /**
    *	 Return the recommended height for this stave.
    */
    public int getPanelHeight() {
        return panelHeight;
    }
    /**
	 * Sets the current metre for this Stave instance
	 * This effects the displayed time signature. 4.0 = 4/4 etc.
	 * @param double
	 */
    public void setMetre(double timeSig) {
    	/*
    	System.out.print("Time Sig =");
    	System.out.println(timeSig);
    	System.out.print("Numerator =");
    	System.out.println(phrase.getNumerator());
    	System.out.print("Denominator =");
    	System.out.println(phrase.getDenominator());
    	*/
        this.metre = timeSig;
    }
    
    /**
	 * returns the current metre for this Stave instance as a double
	 */
    public double getMetre() {
        return this.metre;
    }
    
    /**
	 * returns the current major key for this Stave instance as a integer
	 * 0 is C, 1 is C#/Db major, 2 is D major, etc
	 */
    public int getMajorKey() {
        int[] keys = {11, 6, 1, 8, 3, 10, 5, 0, 7, 2, 9, 4, 11, 6, 1};
        return keys[keySignature + 7];
    }
    /**
	 * Sets the current key signature for this Stave instance
	 * This effects the displayed key signature. 1 = F# etc.
	 * 0 is no key signature, + numbers for sharps, - numbers for flats
	 * @param int
	 */
    public void setKeySignature(int key) {
        this.keySignature = key;
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
	 * Called by outer containers
	 */
    public Dimension getPreferredSize() {
        return new Dimension( this.getSize().width, this.getSize().height);
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
    public void update(Graphics g) {
        paint(g);
    };
    
    public void paint(Graphics graphics) {
        // overridden by each class which extends Stave
    }
    
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
    
    protected void chooseImage(int pitch,
                               double rhythmValue,
                               int upPitch1,
                               int downPitch,
                               int upPitch2) {
        if (pitch == Note.REST) { 
            isNote = false;
            if (rhythmValue <= 0.0) {
                currImage = delete;
                currBeatWidth = (int) (beatWidth * 0.5);
            } else if (rhythmValue <= 0.2501) {
                currImage = semiquaverRest;
                currBeatWidth  = (int) (beatWidth * 0.5);
            } else if (rhythmValue <= 0.501) {
                currImage = quaverRest;
                currBeatWidth  = (int) (beatWidth * (2.0 / 3.0));
            } else if (rhythmValue <= 0.7501) {
                currImage = quaverRest;
                currBeatWidth  = (int) (beatWidth * (2.0 / 3.0));
                dottedNote = true;
            } else if (rhythmValue <= 1.001) {
                currImage = crotchetRest;
                currBeatWidth  = beatWidth;
            } else if (rhythmValue <= 1.2501) {
                currImage = crotchetRest;
                currBeatWidth  = beatWidth;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 1.0;
            } else if (rhythmValue <= 1.501) {
                currImage = crotchetRest;
                currBeatWidth  = (int) (beatWidth * 1.5);
                dottedNote = true;
            } else if (rhythmValue <= 1.7501) {
                currImage = crotchetRest;
                currBeatWidth  = (int) (beatWidth * 1.5);
                dottedNote = true;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 1.5;
            } else if (rhythmValue <= 2.001) {
                currImage = minimRest;
                currBeatWidth  = (int) (beatWidth * 1.7);
            } else if (rhythmValue <= 2.7501) {
                currImage = minimRest;
                currBeatWidth  = (int) (beatWidth * 1.7);
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 2.0;
            } else if (rhythmValue <= 3.001) {
                currImage = minimRest;
                currBeatWidth  = (int) (beatWidth * 1.9);
                dottedNote = true;
            } else if (rhythmValue <= 3.7501) {
                currImage = minimRest;
                currBeatWidth  = (int) (beatWidth * 1.9);
                dottedNote = true;
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue -  3.0;
            } else if (rhythmValue <= 4.001) {
                currImage = semibreveRest;
                currBeatWidth  = (int) (beatWidth * 0.5);
            } else {
                currImage = semibreveRest;
                currBeatWidth = (int) (beatWidth * 0.5);
                requiresMoreThanOneImage = true;
                excessRhythmValue = rhythmValue - 4.0;
            }
        } else { // a note rather than a rest
            isNote = true;
            if ((pitch < upPitch1 && pitch >= downPitch)
                    || pitch < upPitch2 ) { // stems down
                isUp = true;
                if (rhythmValue <= 0.001) {
                    currImage = delete;
                    currBeatWidth  = (int) (beatWidth * 0.5);
                } else if (rhythmValue <= 0.2501) {
                    currImage = semiquaverUp;
                    currBeatWidth  = (int) (beatWidth * 0.5);
                } else if (rhythmValue <= 0.501) {
                    currImage = quaverUp;
                    currBeatWidth  = (int) (beatWidth * (2.0 / 3.0));
                } else if (rhythmValue <= 0.7501) {
                    currImage = quaverUp;
                    currBeatWidth  = (int) (beatWidth * 0.67);
                    dottedNote = true;
                } else if (rhythmValue <= 1.001) {
                    currImage = crotchetUp;
	                currBeatWidth  = beatWidth;
                } else if (rhythmValue <= 1.2501) {
                    currImage = crotchetUp;
	                currBeatWidth  = beatWidth;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.0;
                } else if (rhythmValue <= 1.501) {
                    currImage = crotchetUp;
                    currBeatWidth  = (int) (beatWidth * 1.5);
                    dottedNote = true;
                } else if (rhythmValue <= 1.7501) {
                    currImage = crotchetUp;
                    currBeatWidth  = (int) (beatWidth * 1.5);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.5;
                } else if (rhythmValue <= 2.001) {
                    currImage = minimUp;
                    currBeatWidth  = (int) (beatWidth * 1.7);
                } else if (rhythmValue <= 2.7501) {
                    currImage = minimUp;
                    currBeatWidth  = (int) (beatWidth * 1.7);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 2.0;
                } else if (rhythmValue <= 3.001) {
                    currImage = minimUp;
                    currBeatWidth  = (int) (beatWidth * 1.9);
                    dottedNote = true;
                } else if (rhythmValue <= 3.7501) {
                    currImage = minimUp;
                    currBeatWidth  = (int) (beatWidth * 1.9);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 3.0;
                } else if (rhythmValue <= 4.001) {
                    currImage = semibreve;
                    currBeatWidth = (int) (beatWidth * 2.25);
                } else {
                    currImage = semibreve;
                    currBeatWidth = (int) (beatWidth * 2.25);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 4.0;
                }
            } else { // stem down
                isUp = false;
                if (rhythmValue <= 0.001) {
                    currImage = delete;
                    currBeatWidth = (int) (beatWidth * 0.5);
                } else if (rhythmValue <= 0.2501) {
                    currImage = semiquaverDown;
                    currBeatWidth = (int) (beatWidth * 0.5);
                } else if (rhythmValue <= 0.501) {
                    currImage = quaverDown;
                    currBeatWidth = (int) (beatWidth * (2.0 / 3.0));
                } else if (rhythmValue <= 0.7501) {
                    currImage = quaverDown;
                    currBeatWidth = (int) (beatWidth * (2.0 / 3.0));
                    dottedNote = true;
                } else if (rhythmValue <= 1.001) {
                    currImage = crotchetDown;
                    currBeatWidth = beatWidth;
                } else if (rhythmValue <= 1.2501) {
                    currImage = crotchetDown;
                    currBeatWidth = beatWidth;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.0;
                } else if (rhythmValue <= 1.501) {
                    currImage = crotchetDown;
                    currBeatWidth  = (int) (beatWidth * 1.5);
                    dottedNote = true;
                } else if (rhythmValue <= 1.7501) {
                    currImage = crotchetDown;
                    currBeatWidth  = (int) (beatWidth * 1.5);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 1.5;
                } else if (rhythmValue <= 2.001) {
                    currImage = minimDown;
                    currBeatWidth  = (int) (beatWidth * 1.7);
                } else if (rhythmValue <= 2.7501) {
                    currImage = minimDown;
                    currBeatWidth  = (int) (beatWidth * 1.7);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 2.0;
                } else if (rhythmValue <= 3.001) {
                    currImage = minimDown;
                    currBeatWidth  = (int) (beatWidth * 1.9);
                    dottedNote = true;
                } else if (rhythmValue <= 3.7501) {
                    currImage = minimDown;
                    currBeatWidth  = (int) (beatWidth * 1.9);
                    dottedNote = true;
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 3.0;
                } else if (rhythmValue <= 4.001) {
                    currImage = semibreve;
                    currBeatWidth  = (int) (beatWidth * 2.25);
                } else {
                    currImage = semibreve;
                    currBeatWidth = (int) (beatWidth * 2.25);
                    requiresMoreThanOneImage = true;
                    excessRhythmValue = rhythmValue - 4.0;
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        System.out.println(e.getKeyChar());
    }
}
