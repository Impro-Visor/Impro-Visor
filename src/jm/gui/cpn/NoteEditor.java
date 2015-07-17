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

import java.text.DecimalFormat;
import java.awt.Dialog;
import java.awt.List;
import java.awt.Button;
import java.awt.TextField;
import java.util.StringTokenizer;


// This class is a little editor to change the properties of a note
// Added as a right-click option to CPN

public class NoteEditor extends Dialog  
    implements ActionListener, WindowListener {

    private Button      
        okButton        = new Button("Apply"), 
        cancelButton    = new Button("Cancel");
    
    private Note        note;
    
    private List        noteList,
                        octaveList;
                        
    private TextField   
        durationEdit  = new TextField(15),
        dynamicEdit   = new TextField(15),
        rhythmEdit    = new TextField(15),
        panEdit       = new TextField(15),
        offsetEdit    = new TextField(15);

    private Label       
       noteLabel     = new Label("Note"),
       dynamicLabel  = new Label("Volume (1-127)"),
       rhythmLabel   = new Label("Rhythm Value"),
       durationLabel = new Label("Duration Factor"),
       panLabel      = new Label("Pan"),
       offsetLabel   = new Label("Offset"),
       octaveLabel   = new Label("Octave");                
       
    private static  DecimalFormat decimalFormat
               = new DecimalFormat("###.###########");       
        
    public NoteEditor( Frame  parentFrame ) {
        super( parentFrame,  "Edit Note", true );
        initializeLists();            
        placeControls();
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        addWindowListener(this);
        setVisible(false);
        pack();
    }    

    
    private static String getOctaveStringValue(int pitch) {
        int octave = -1;
        int note = pitch;
        while (note > 11) {
            ++octave;              
            note -= 12;
        }              
        return (new Integer(octave)).toString();                                                       
    }                                    
    
    private static String getPitchStringValue(
                                int pitch) {
        if ( pitch == Note.REST ) {
            return "Rest";
        }                 
        else {
            int k;
            k = pitch;
            while (k >= 12) {
                k -= 12;                   
            }    
            switch(k) {
                case 0:  
                    return "C";
                case 1:  
                    return "C#";
                case 2:  
                    return "D";
                case 3:  
                    return "D#";
                case 4:  
                    return "E";
                case 5:  
                    return "F";
                case 6:  
                    return "F#";
                case 7:  
                    return "G";
                case 8:  
                    return "G#";
                case 9:  
                    return "A";
                case 10:  
                    return "A#";
                case 11:  
                    return "B";
                default: 
                    return "Rest";
            }                    
        }            
    }                                    
    
    private static void setListToMatch(
                List   list,
                String valueWanted 
            ) {
        for (int i=list.getItemCount()-1; i>=0; --i) {
            if (list.getItem(i).equals(valueWanted)) {
                list.select(i);
            }                             
        }                            
    }                                     
    

    private void initializeNoteListValue( int pitch ) {
        setListToMatch( 
            noteList,
            getPitchStringValue(pitch)
        );                    
    }        

    private void initializeOctaveListValue( int pitch ) {
        setListToMatch( 
            octaveList,
            getOctaveStringValue(pitch)
        );                    
    }        
    
    private static void initializeDoubleEdit(
                            TextField theEdit,
                            double    theValue ) {
        theEdit.setText(decimalFormat.format(theValue));                                            
    }        
            
    private static void initializeIntEdit(
                            TextField theEdit,
                            int       theValue ) {
        theEdit.setText(
                new Integer(theValue).toString());                                            
    }        
            

    private void initializeData() {
        initializeNoteListValue(note.getPitch());        
        
        initializeOctaveListValue(note.getPitch());        
        
        initializeDoubleEdit( 
            durationEdit, 
            note.getDuration()/note.getRhythmValue() 
        );            
        
        initializeDoubleEdit( 
            rhythmEdit, note.getRhythmValue() 
        );            

        initializeDoubleEdit( 
            offsetEdit, note.getOffset() 
        );            
        
        initializeDoubleEdit( 
            panEdit, note.getPan() 
        );            

        initializeIntEdit( 
            dynamicEdit, note.getDynamic() 
        );            
    }        
    
    public void editNote(
                    Note theNote, 
                    int locX, 
                    int locY) {
        note      = theNote;
        setLocation( locX, locY );                  
        initializeData();
        show();
    }   

    private void initializeLists() {
        noteList = new List(6);
        noteList.add( "Rest" );        
        noteList.add( "A" );        
        noteList.add( "A#" );        
        noteList.add( "B" );        
        noteList.add( "C" );        
        noteList.add( "C#" );        
        noteList.add( "D" );        
        noteList.add( "D#" );        
        noteList.add( "E" );        
        noteList.add( "F" );        
        noteList.add( "F#" );        
        noteList.add( "G" );        
        noteList.add( "G#" );        
        
        octaveList = new List(6);
        octaveList.add("-1");
        octaveList.add("0");
        octaveList.add("1");
        octaveList.add("2");
        octaveList.add("3");
        octaveList.add("4");
        octaveList.add("5");
        octaveList.add("6");
        octaveList.add("7");
        octaveList.add("8");
        octaveList.add("9");
    }        
    
    private static boolean validateFloatEdit(
                TextField   theField,
                double      minValue,
                double      maxValue) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        if (!fieldTokenizer.hasMoreElements()) {
            theField.setText("Error--No Data");
            return false;
        }                    
        else {
            String fieldString = 
                    fieldTokenizer.nextToken();             
            try {
                double fieldValue = 
                    new Double(fieldString)
                        .doubleValue();  
                if (fieldValue < minValue ) {
                    theField.setText("Value Too Low");
                    return false;
                }                                                          
                else if (fieldValue < minValue ) {
                    theField.setText("Value Too High");
                    return false;
                }                                                          
            }                            
            catch (Throwable e ) {
                theField.setText("Data Error");
                return false;
            }                            
        }            
        if (fieldTokenizer.hasMoreElements()) {
            theField.setText("Data Error");
            return false;
        }   
        else {
            return true;                
        }                             
    }                    

    
    private static double getFieldDouble(
                TextField   theField
            ) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        String fieldString = 
                    fieldTokenizer.nextToken();             
        return (new Double(fieldString)).doubleValue();  
    }        

    
    private static boolean validateIntegerEdit(
                TextField   theField,
                int         minValue,
                int         maxValue) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        if (!fieldTokenizer.hasMoreElements()) {
            theField.setText("Error--No Data");
            return false;
        }                    
        else {
            String fieldString = 
                    fieldTokenizer.nextToken();             
            try {
                int fieldValue = 
                    new Integer(fieldString)
                        .intValue();  
                if (fieldValue < minValue ) {
                    theField.setText("Value Too Low");
                    return false;
                }                                                          
                else if (fieldValue > maxValue ) {
                    theField.setText("Value Too High");
                    return false;
                }                                                          
            }                            
            catch (Throwable e ) {
                theField.setText("Data Error");
                return false;
            }                            
        }            
        if (fieldTokenizer.hasMoreElements()) {
            theField.setText("Data Error");
            return false;
        }   
        else {
            return true;                
        }                             
    }                    
                
    
    private static int getFieldInt(
                TextField   theField
            ) {
        StringTokenizer fieldTokenizer                    
            = new StringTokenizer(theField.getText());
        String fieldString = 
                    fieldTokenizer.nextToken();             
        return (new Integer(fieldString)).intValue();  
    }        

    private boolean inputIsValid() {
        return 
            validateFloatEdit(
                    durationEdit, 
                    0.0000,
                    1.0000) &&
            validateIntegerEdit(
                    dynamicEdit,
                    0, 
                    127) &&
            validateFloatEdit(
                    rhythmEdit,
                    0.00001,
                    64.0000) &&
            validateFloatEdit(
                    panEdit,
                    0.0,
                    1.0 ) &&
            validateFloatEdit(
                    offsetEdit,
                    -999.999, 999.999); 
    }        

    
    private void placeControls() {
        GridBagLayout      layout = new GridBagLayout();        
        GridBagConstraints c      = new GridBagConstraints();       
        setLayout(layout);

        c.fill  = GridBagConstraints.BOTH;
        c.weightx = 0.5;
                
        c.gridwidth  = 2;
        c.gridheight = 1;

        c.gridx      = 0;
        c.gridy      = 0;
        c.gridheight = 1;
        layout.setConstraints(noteLabel, c); 
        add( noteLabel);
        
        c.gridx      = 0;
        c.gridy      = 2;
        c.gridheight = 4;
        layout.setConstraints(noteList, c); 
        add( noteList);
                
        c.gridx      = 0;
        c.gridy      = 7;
        c.gridheight = 1;
        layout.setConstraints(octaveLabel, c); 
        add( octaveLabel);
        
        c.gridx      = 0;
        c.gridy      = 8;
        c.gridheight = 4;
        layout.setConstraints(octaveList, c); 
        add( octaveList);
        
        c.gridx      = 0;
        c.gridy      = 15;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(rhythmLabel, c); 
        add(rhythmLabel);                        
        c.gridx      = 1;
        layout.setConstraints(rhythmEdit, c); 
        add(rhythmEdit);                        
        
        c.gridx      = 0;
        c.gridy      = 17;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(dynamicLabel, c); 
        add(dynamicLabel);                        
        c.gridx      = 1;
        layout.setConstraints(dynamicEdit, c); 
        add(dynamicEdit);                        

        c.gridx      = 0;
        c.gridy      = 19;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(durationLabel, c); 
        add(durationLabel);                        
        c.gridx      = 1;
        layout.setConstraints(durationEdit, c); 
        add(durationEdit);                        

        c.gridx      = 0;
        c.gridy      = 21;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(offsetLabel, c); 
        add(offsetLabel);                        
        c.gridx      = 1;
        layout.setConstraints(offsetEdit, c); 
        add(offsetEdit);                        
                
        c.gridx      = 0;
        c.gridy      = 23;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(panLabel, c); 
        add(panLabel);                        
        c.gridx      = 1;
        layout.setConstraints(panEdit, c); 
        add(panEdit);                        
        
        c.gridx      = 0;
        c.gridy      = 25;
        c.gridheight = 1;
        c.gridwidth  = 1;
        layout.setConstraints(okButton, c); 
        add(okButton);                        
        c.gridx      = 1;
        layout.setConstraints(cancelButton, c); 
        add(cancelButton);                        

    }        

    private int getSelectedPitch() {
        String noteString = noteList.getSelectedItem();;
        if ( noteString.equals("Rest") ) {
            return (int)Note.REST;                
        }                    
        else {
            int answer;
            if ( noteString.equals("C") ) {
                answer = 0;
            }                             
            else if ( noteString.equals("C#")) {
                answer = 1;
            }                             
            else if ( noteString.equals("D") ) {
                answer = 2;
            }                             
            else if ( noteString.equals("D#") ) {
                answer = 3;
            }                             
            else if ( noteString.equals("E") ) {
                answer = 4;
            }                             
            else if ( noteString.equals("F") ) {
                answer = 5;
            }                             
            else if ( noteString.equals("F#") ) {
                answer = 6;
            }                             
            else if ( noteString.equals("G") ) {
                answer = 7;
            }                             
            else if ( noteString.equals("G#") ) {
                answer = 8;
            }                             
            else if ( noteString.equals("A") ) {
                answer = 9;
            }                             
            else if ( noteString.equals("A#")) {
                answer = 10;
            }                             
            else if ( noteString.equals("A") ) {
                answer = 11;
            }                             
            else {
                answer = 0;
            }                             
            int octave 
                = (new Integer(
                      octaveList.getSelectedItem()))
                      .intValue(); 
            while( octave > -1) {
                answer += 12;
                --octave;                    
            }     
            return answer;                                 
        }                
    }        

    private void updateTheNote() {
        note.setPitch(getSelectedPitch());
        note.setRhythmValue(getFieldDouble(rhythmEdit));
        note.setDuration(note.getRhythmValue() *
                        getFieldDouble(durationEdit));
        note.setDynamic(getFieldInt(dynamicEdit));
        note.setPan(getFieldDouble(panEdit));
        note.setOffset(getFieldDouble(offsetEdit));
    }        

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == okButton) {
            if (inputIsValid()) {        
                updateTheNote();
                dispose();            
            }
        }                     
        else if (e.getSource() == cancelButton) {
            dispose();            
        }                     
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

     
}
