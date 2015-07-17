/*
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

// GPL code for jMusic CPN.   
// Written by Al Christians (achrist@easystreet.com).
// Copyright  2002, Trillium Resources Corporation, Oregon's
// leading provider of unvarnished software.
package jm.gui.cpn;

import jm.music.data.*;
import java.awt.event.*;
import java.awt.*; 
import java.lang.Integer;
import java.lang.Double;
import java.util.Vector;
import java.util.StringTokenizer;
import java.text.DecimalFormat;
import jm.gui.cpn.Stave;

import java.awt.Dialog;

public class PhraseViewer extends Dialog 
    implements WindowListener  {

    private ScrollPane   scrollPane = new ScrollPane();
    private TextArea     textArea   = new TextArea(20, 120);
    private Phrase       phrase;  
    private Stave        stave;  
    
    private DecimalFormat decimalFormat 
            = new DecimalFormat( "#####.######" );
        
    public PhraseViewer(
            Frame   parentFrame ) {
        super( 
            parentFrame,                
            "Phrase Detail Display",
            true );
        setSize(500, 400);
        placeControls();
        addWindowListener(this);
        setVisible(false);
        pack();
    }
        

    private void placeControls() {
        scrollPane.add(textArea);        
        setLayout(new BorderLayout());
        add("Center", scrollPane ); 
    }        

    public void showPhrase(
                    Stave  theStave,
                    Phrase thePhrase, 
                    int locX, 
                    int locY) {
        stave     = theStave;
        phrase    = thePhrase;
        getPhraseText();                
        setLocation( locX, locY );                  
        show();
    }   
    
    private void getPhraseText() {
        getStaveText();
        textArea.append(
            "Phrase has " + phrase.size() + " notes.\n");
            
        textArea.append(
            "Tempo " 
            + decimalFormat.format(phrase.getTempo()));                                            
        textArea.append(
            "    Numerator " + phrase.getNumerator());                                            
        textArea.append(
            "    Denominator " + phrase.getDenominator());                                            
        textArea.append("\n" ); 
    
        for(int i =0; i < phrase.size(); ++i ) {
            getNoteText( phrase.getNote(i));
        }            
    }        

    private void getStaveText() {
        textArea.append(
            "Stave " + stave.getTitle() + 
            "   Metre " + 
            decimalFormat.format(stave.getMetre())
            + "\n" );
    }        
    
    private void getNoteText( Note n) {
        textArea.append(
            "Pitch " + n.getPitch());            
        textArea.append(
            "   Start " + 
                  decimalFormat.format(n.getSampleStartTime()));            
        textArea.append(
            "   Rhythm " + 
                  decimalFormat.format(n.getRhythmValue()));            
        textArea.append(
            "   Dur " + 
                  decimalFormat.format(n.getDuration()));            
        textArea.append(
            "   Offset " + 
                  decimalFormat.format(n.getOffset()));            
        textArea.append(
            "   Vol " + n.getDynamic());            
        textArea.append("\n");
    }
    
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == this) dispose();
        //System.exit(0);
    }

    

    /**
     * Invoked when a window has been closed.
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-iconified.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is activated.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-activated.
     */
    public void windowDeactivated(WindowEvent e) {
    }

}

