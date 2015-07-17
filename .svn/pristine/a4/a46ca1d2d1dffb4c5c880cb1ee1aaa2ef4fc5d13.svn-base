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

import java.awt.event.*;
import java.awt.*; 
import java.lang.Integer;
import java.lang.Double;
import java.util.Vector;
import java.util.StringTokenizer;

import java.awt.Dialog;
import java.awt.List;
import java.awt.Button;
import jm.music.data.Phrase;
import jm.music.data.Note;

// Class to zoom in on a few measures of a Phrase
// Needed because the CPN screen doesn't display
// long Phrases well.

public class CpnZoomScreen extends Dialog

    implements ActionListener, WindowListener {
        
    private Phrase   phrase, beforeZoom, afterZoom;     

    private static TextField  
        startMeasureEdit  = new TextField(8),  // 1st Measure is 1
        measureCountEdit  = new TextField(8);  // Measures to show
                        
    private static Label   startMeasureLabel
                        = new Label("Start at Measure");
    
    private static Label   measureCountLabel
                        = new Label("Number of Measures");
        
    private Button     okButton
                        = new Button( "Update View:" ),
                       cancelButton
                       = new Button("Cancel"); 

    public CpnZoomScreen(Frame parentFrame) {
        // TODO: Add your code here
        
        super( 
            parentFrame,                
            "Select the Measures to Show",
            true );
        setSize(500, 400);
        placeControls();
        addWindowListener(this);
        setVisible(false);
        pack();
    }    
    

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        if(e.getSource() == this) dispose();
    }


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

    // zoomIn is called after the parts before and after have
    // already been detached from the Phrase.  They are
    // re-attached when we zoom out
    public void zoomIn( 
                    Phrase before,
                    Phrase thePhrase,
                    Phrase after) {
        phrase = thePhrase;
        beforeZoom = before;
        afterZoom  = after;
        beforeZoom.empty();                
        afterZoom.empty();                
        setLocation( 20, 20 );                  
        show();
    }                        


    // Recombine before, Phrase and after into longer Phrase                     
    static public void zoomOut( 
                    Phrase before,
                    Phrase thePhrase,
                    Phrase after) {
        for(int i = 0; i < thePhrase.size(); ++i  ) {
            before.addNote(thePhrase.getNote(i));            
        }            
        
        for(int i = 0; i < after.size(); ++i  ) {
            before.addNote(after.getNote(i));            
        }            
        
        thePhrase.empty();
        for(int i = 0; i < before.size(); ++i  ) {
            thePhrase.addNote(before.getNote(i));            
        }            

        before.empty();
        after.empty();
    }                        
    
    // Put the controls on the screen
    private void placeControls() {
        GridBagLayout      layout = new GridBagLayout();        
        GridBagConstraints c      = new GridBagConstraints();       
        setLayout(layout);

        c.fill  = GridBagConstraints.BOTH;
        c.weightx    = 0.5;        
        c.gridwidth  = 1;
        c.gridheight = 1;
        
        c.gridx      = 0;
        c.gridy      = 0;
        c.gridheight = 1;
        layout.setConstraints(startMeasureLabel, c); 
        add( startMeasureLabel);
        
        c.gridx      = 1;
        c.gridy      = 0;
        c.gridheight = 1;
        layout.setConstraints(startMeasureEdit, c); 
        add( startMeasureEdit);

        c.gridx      = 0;
        c.gridy      = 1;
        c.gridheight = 1;
        layout.setConstraints(measureCountLabel, c); 
        add( measureCountLabel);
        
        c.gridx      = 1;
        c.gridy      = 1;
        c.gridheight = 1;
        layout.setConstraints(measureCountEdit, c); 
        add( measureCountEdit);

        c.gridx      = 0;
        c.gridy      = 3;
        c.gridheight = 1;
        layout.setConstraints(okButton, c); 
        add(okButton);
        okButton.addActionListener(this);
        
        c.gridx      = 1;
        c.gridy      = 3;
        c.gridheight = 1;
        add(cancelButton);
        layout.setConstraints(cancelButton, c); 
        
        cancelButton.addActionListener(this);
    }        
    
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == okButton ) {
            // Zoom in if there are no input errors
            if (startFieldError()) {
                startMeasureEdit.setText("Error");
            }                        
            else if (countFieldError()) {
                measureCountEdit.setText("Error");
            }                        
            else {
                zoom();       
                this.dispose();            
            }
        }            
        if (e.getSource() == cancelButton ) {
            this.dispose();            
        }            
    }        


    // How many measures in a Phrase ?
    private double countMeasures(Phrase  p){
        double answer = 0.0;
        for (int i=0; i < p.size(); ++i) {
            answer = answer 
                    + p.getNote(i).getRhythmValue();
        }            
        return answer / p.getNumerator();        
    }        

    // Check int input edit field for an error
    private static boolean intFieldError( 
                        TextField theField,
                        double minValue,
                        double maxValue ) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        if (!fieldTokenizer.hasMoreElements()) {
            theField.setText("Error");
            return true;
        }                    
        else {
            String fieldString = 
                    fieldTokenizer.nextToken();             
            try {
                int fieldValue = 
                    new Integer(fieldString)
                        .intValue();  
                if (fieldValue < minValue ) {
                    theField.setText("Error");
                    return true;
                }                                                          
                else if (fieldValue > maxValue ) {
                    theField.setText("Error");
                    return true;
                }                                                          
            }                            
            catch (Throwable e ) {
                theField.setText("Error");
                return true;
            }                            
        }            
        if (fieldTokenizer.hasMoreElements()) {
            theField.setText("Error");
            return true;
        }   
        else {
            return false;                
        }            
    }                            
    
    // Extract int value from input edit field 
    private static int getIntegerValue( 
                        TextField theField ) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        String fieldString = 
                   fieldTokenizer.nextToken();             
        return new Integer(fieldString).intValue();  
    }                            
    
    
    // See if the start measure is a good measure number 1-n
    private boolean startFieldError() {
        return intFieldError(
                    startMeasureEdit,
                    1, 
                    countMeasures(phrase) + 0.99
               );                    
    }        
    
    // See if the number of measures is ok or not
    private boolean countFieldError() {
        return intFieldError(
                    measureCountEdit, 
                    1.0, 99999.99 
               );
    }        

    // Move measures from the start of one Phrase to another
    private void moveMeasures(
                    Phrase fromPhrase,
                    Phrase toPhrase,
                    double nMeasures ) {
        double beatCount = nMeasures * 
                            fromPhrase
                            .getNumerator();
        double beatValue;
        Note   theNote;
        while ( ( beatCount > 0.005 ) && 
                ( fromPhrase.size() > 0)) {
            theNote = fromPhrase.getNote(0);
            beatValue = theNote.getRhythmValue();
            toPhrase.addNote(theNote);
            fromPhrase.removeNote(0);
            beatCount -= beatValue;                                                                          
        }
    }                                
                        

    // Move all notes from one Phrase to another
    private void moveAll(
                    Phrase fromPhrase,
                    Phrase toPhrase ) {
        Note   theNote;
        while ( ( fromPhrase.size() > 0)) {
            theNote = fromPhrase.getNote(0);
            toPhrase.addNote(theNote);
            fromPhrase.removeNote(0);
        }
    }                                
    
                    
    // Zoom in on the selected measures
    private void zoom() {
        int n = getIntegerValue(startMeasureEdit)
                - 1;
        int m = getIntegerValue(measureCountEdit);
            
        beforeZoom.empty();                
        afterZoom.empty();                
        
        moveMeasures( phrase, beforeZoom, n);        
        moveAll( phrase, afterZoom );
        moveMeasures( afterZoom, phrase,  
            n + m - countMeasures(beforeZoom));        
    }        
}
