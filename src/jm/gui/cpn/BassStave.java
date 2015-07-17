/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Brown

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
//import jm.gui.cpn.CPNFrame;
//import javax.swing.*;

public class BassStave extends Stave implements JMC{ 
    
    public BassStave() {
        super();
        staveDelta = staveSpaceHeight*11/2;
    }
    
    public BassStave(Phrase phrase) {
        super(phrase);
        staveDelta = staveSpaceHeight*11/2;
    }
    
    public void paint(Graphics graphics) {
        // set up for double buffering
        if(image == null) {
            image = this.createImage(this.getSize().width, this.getSize().height);
	        g = image.getGraphics();
        }
        g.setFont(font);
        // keep track of the rhythmic values for bar lines
        double beatCounter = 0.0;
        // reset the chromatic vector
        previouslyChromatic.removeAllElements();
        // reste note position locations
        notePositions.removeAllElements();
        int keyAccidentals = 0;
        // add a title if set to be visible
        if(getDisplayTitle()) g.drawString(title, rightMargin, bPos - 10);
        // insert key signature if required
        int keyOffset = 0;
        // is the key signature using sharps or flats?
        if (keySignature > 0 && keySignature < 8) { // sharp
            for(int ks=0;ks<keySignature; ks++) {
                // claulate position
                int keyAccidentalPosition = notePosOffset[ sharps[ks]%12] + bPos - 4 + (( 5- sharps[ks]/12) * 24) + (( 6- sharps[ks]/12) * 4);
                // draw sharp
                g.drawImage(sharp, rightMargin + clefWidth + keyOffset, keyAccidentalPosition + staveSpaceHeight, this);
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
                    g.drawImage(flat, rightMargin + clefWidth + keyOffset, keyAccidentalPosition + staveSpaceHeight, this);
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
            //bottom number
            g.drawImage(four, rightMargin + clefWidth + keySigWidth , bPos + 29, this);
            timeSigWidth = 30;
        } else timeSigWidth = 5;
        // set indent position for first note
        totalBeatWidth = rightMargin + clefWidth + keySigWidth + timeSigWidth;
       
        // draw notes and rests
        for(int i = 0; i < phrase.size();i++) {
            int notePitchNum = (int)phrase.getNote(i).getPitch();
            // choose graphic
            chooseImage( notePitchNum, phrase.getNote(i).getRhythmValue(), 50, 0, 50);
            // reset pitch for rests
                        
            // position?
            int pitchTempPos;
            if ( notePitchNum == REST || phrase.getNote(i).getRhythmValue() == 0.0) { // rest or delete
                pitchTempPos = notePosOffset[71%12] + bPos - 4 + (( 5- 71/12) * 24) + (( 6- 71/12) * 4);
            } else {
                pitchTempPos = notePosOffset[notePitchNum%12] + bPos - 4 + (( 5- notePitchNum/12) * 24) + (( 6- notePitchNum/12) * 4 - staveSpaceHeight * 6);
            }
        
            // accidental?
            if (((notePitchNum % 12) == 1 || (notePitchNum % 12) == 3 || (notePitchNum % 12) == 6 || (notePitchNum % 12) == 8 || (notePitchNum % 12) == 10) && notePitchNum != REST && phrase.getNote(i).getRhythmValue() != 0.0) {
                   if(keySignature > -1) {
                        g.drawImage(sharp, totalBeatWidth - 9, pitchTempPos, this);
                        previouslyChromatic.addElement(new Integer(notePitchNum - 1)); // enter the note made sharp i.e, F for an F#
                   } else { // flat
                        pitchTempPos -= 4; // to show the note a semitone higher for flats
                        g.drawImage(flat, totalBeatWidth - 9, pitchTempPos, this);
                        previouslyChromatic.addElement(new Integer(notePitchNum + 1));
                        notePitchNum++; // assume it is a semitone higher for legerlines etc...
                   }
            } else { // check for a natural
                // check vector
                int size = previouslyChromatic.size();
                for(int j=0; j<size; j++) {
                Integer temp = (Integer)previouslyChromatic.elementAt(j);
                    if (temp.intValue() == notePitchNum && notePitchNum != REST && phrase.getNote(i).getRhythmValue() != 0.0) {
                        // add natural
                        g.drawImage(natural, totalBeatWidth - 7, pitchTempPos, this);
                        // remove element if not in key signature
                        if (j>keyAccidentals-1) previouslyChromatic.removeElementAt(j);
                        j = size;
                    }
                }
            }
             
            // draw note/rest
            g.drawImage(currImage, totalBeatWidth, pitchTempPos, this);
            // store position in a vector
            notePositions.addElement(new Integer(totalBeatWidth));
            notePositions.addElement(new Integer(pitchTempPos + staveDelta)); // stave delta required for bass clef offset from treble
            //System.out.println("Position "+i+" "+totalBeatWidth + " "+ pitchTempPos);
            
            if (dottedNote) { 
                boolean dotFlag = true;
                for(int l=0;l<lineNotes.length;l++) {
                    if ( lineNotes[l] + 12 == notePitchNum || lineNotes[l] + 36 == notePitchNum || lineNotes[l] + 60 == notePitchNum || lineNotes[l] + 84 == notePitchNum || lineNotes[l] + 108 == notePitchNum || notePitchNum == REST) {
                        g.drawImage(dot, totalBeatWidth + 1, pitchTempPos - 4, this);
                        dotFlag = false;
                        l = lineNotes.length;
                    }
                }
                if (dotFlag) g.drawImage(dot, totalBeatWidth + 1, pitchTempPos, this);
            }
            // leger lines down
            if ( notePitchNum <= 40 && notePitchNum > -1 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 52, totalBeatWidth+ 12, bPos + 52);}
            if ( notePitchNum <= 37 && notePitchNum > -1 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 60, totalBeatWidth+ 12, bPos + 60);}
            if ( notePitchNum <= 34 && notePitchNum > -1 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 68, totalBeatWidth+ 12, bPos + 68);}
            if ( notePitchNum <= 30 && notePitchNum > -1 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 76, totalBeatWidth+ 12, bPos + 76);}
            if ( notePitchNum <= 26 && notePitchNum > -1 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 84, totalBeatWidth+ 12, bPos + 84);}
            // leger lines up
            if ( notePitchNum >= 60 && notePitchNum < 128 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos + 4, totalBeatWidth+ 12, bPos + 4);}
            if ( notePitchNum >= 64 && notePitchNum < 128 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 4, totalBeatWidth+ 12, bPos - 4);}
            if ( notePitchNum >= 67 && notePitchNum < 128 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 12, totalBeatWidth+ 12, bPos - 12);}
            if ( notePitchNum >= 71 && notePitchNum < 128 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 20, totalBeatWidth+ 12, bPos - 20);}
            if ( notePitchNum >= 74 && notePitchNum < 128 && phrase.getNote(i).getRhythmValue() != 0.0) {g.drawLine( totalBeatWidth - 3, bPos - 28, totalBeatWidth+ 12, bPos - 28);}
            
            // increment everything
            totalBeatWidth += currBeatWidth;
            dottedNote = false;
            // quantised to semiquvers!
            // (int)((phrase.getNote(i).getRhythmValue()/0.25) * 0.25);
            beatCounter += (int)(phrase.getNote(i).getRhythmValue()/0.25) * 0.25;
            
            // add bar line if required
            if (metre != 0.0) {
                if ( (beatCounter % metre) == 0.0) {
                    g.drawLine( totalBeatWidth , bPos + 12, totalBeatWidth, bPos + 44);
                    // add bar numbers?
                    if (barNumbers) g.drawString( ""+(int)(beatCounter/metre +1 + phrase.getStartTime()) , totalBeatWidth - 4 , bPos);
                    totalBeatWidth += 12;
                }
            }
        }
        
        // draw stave
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
         g.setColor(Color.black);
        // add Clef
        g.drawImage(bassClef, rightMargin + 7, bPos, this);
        
        /* Draw completed buffer to g */

        graphics.drawImage(image, 0, 0, null); 
        // clear image
        // clear
	    g.setColor(this.getBackground());
	    g.fillRect(0,0, getSize().width, getSize().height);
	    g.setColor(this.getForeground());
	    //repaint();
	    //g.dispose();
    }
}
        
