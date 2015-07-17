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

import java.awt.Dialog;
import java.awt.List;
import java.awt.Button;

// This class is a little editor to set the properties
// of a Phrase in the jMusic CPN. 

public class ParmScreen extends Dialog 
	implements ActionListener, WindowListener  {
	
	private List   instrumentList,
				   volumeList,
				   tempoList;	 			
	
	private Button instrumentButton,
				   volumeButton,
				   tempoButton,
				   closeButton;	
	
	private Label  instrumentLabel,  
				   volumeLabel,
				   tempoLabel;

	private Phrase phrase;

	public ParmScreen( Frame parentFrame ) {
		super( parentFrame, "Set Music Parameters", true );
		initializeLists();
		initializeButtons();
		initializeLabels();
		setSize(500, 400);
		placeControls();
		addWindowListener(this);
		setVisible(false);
		pack();
	}

    public void windowOpened(WindowEvent e) {  }

    public void windowClosing(WindowEvent e) {
        if(e.getSource() == this) dispose();
    }


    public void windowClosed(WindowEvent e) { }

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e) { }

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

    // Use this editor class to update a phrase
	public void getParms(
                Phrase thePhrase, 
                int locX, 
                int locY) {
					   
            phrase = thePhrase;
            setLocation( locX, locY );					
            show();
	}	
	
	
    public void actionPerformed(ActionEvent e){
    	if (e.getSource() == tempoButton ) {
			System.out.print( "Adjusting Tempo ");    			
    		System.out.print(tempoList.getSelectedItem());
   			double oldTempo = phrase.getTempo();
   			if ( oldTempo < 10.0 ) {
   				oldTempo = 60.0;
			}   				
    		double newTempo = getTempo(tempoList.getSelectedItem());
    		phrase.setTempo(newTempo);
			multiplyTimesBy( oldTempo / newTempo );    		
		}
		else     		
    	if (e.getSource() == volumeButton ) {
    		setVolume(
    			getVolume(volumeList.getSelectedItem())
			);
		}
		else     		
    	if (e.getSource() == instrumentButton ) {
    		phrase.setInstrument(
    			getInstrument(
    				instrumentList.getSelectedItem()));
		}
		else     		
    	if (e.getSource() == closeButton ) {
    		dispose();
		}
    }
	
	private void initializeLists() {
		initializeInstrumentList();
		initializeVolumeList();
		initializeTempoList();
	}		

    
	// This instrument list is copied from jMusic.  IDK
	// if this matches with what any particular Midi will
	// do or what it takes to make it happen.
	// Mine sounds like it tries but doesn't come close
	// on many of the instruments
  	private void initializeInstrumentList()
  	{
  		instrumentList = new List();
        instrumentList.add("Accordion             21");
        instrumentList.add("Applausen            126");
        instrumentList.add("Bandneon              23");
        instrumentList.add("Banjo                105");
        instrumentList.add("Bagpipes             109");
        instrumentList.add("Bass   (Acoustic)     32");
        instrumentList.add("Bass   (Fingerd)      33");
		instrumentList.add("Bass   (Fretless)     35");
		instrumentList.add("Bass   (Picked)       34");
		instrumentList.add("Bass   (Slap)         36");
        instrumentList.add("Bass   (Synth)        38");
        instrumentList.add("Bass   (Synth)        38");
        instrumentList.add("Bassoon               70");
        instrumentList.add("Bottle                76");
        instrumentList.add("Brass  (Synthetic)    62");
        instrumentList.add("Calliope              82");
        instrumentList.add("Celeste                8");
        instrumentList.add("Cello                 42");
        instrumentList.add("Charang               84");
        instrumentList.add("Choir                 52");
        instrumentList.add("Clarinet              71");
        instrumentList.add("Clavinet               7");
        instrumentList.add("Contrabass            43");
        instrumentList.add("English Horn          69");
        instrumentList.add("Fiddle               110");
        instrumentList.add("French Horn           60");
        instrumentList.add("Flute                 73");
        instrumentList.add("Glockenspiel           9");
		instrumentList.add("Guitar (Clean)        27");
		instrumentList.add("Guitar (Distorted)    30");
		instrumentList.add("Guitar Harmonics      31");
		instrumentList.add("Guitar (Jazz)         26");
		instrumentList.add("Guitar (Muted)        28");
		instrumentList.add("Guitar (Nylon)        24");
		instrumentList.add("Guitar (Overdrive)    29");
		instrumentList.add("Guitar (Steel)        25");
		instrumentList.add("Harmonica             22");
		instrumentList.add("Harp                  46");
		instrumentList.add("Harpsichord           76");
		instrumentList.add("Marimba               12");
		instrumentList.add("Music Box             10");
        instrumentList.add("Oboe                  68");
        instrumentList.add("Ocarina               79");
        instrumentList.add("Orchestra Hit         55");
        instrumentList.add("Organ                 16");
        instrumentList.add("Organ (Church)        19"); 
		instrumentList.add("Organ (Reed)          20");
        instrumentList.add("Pan Flute             75");
        instrumentList.add("Piano                  0");
        instrumentList.add("Piano (Electric)       4");
        instrumentList.add("Piano (Honkeytonk)     3");
        instrumentList.add("Piccolo               72");
        instrumentList.add("Recorder              74");
        instrumentList.add("Saxophone (Alto)      65");
        instrumentList.add("Saxophone (Soprano)   64");
        instrumentList.add("Saxophone (Tenor)     66");
        instrumentList.add("Saxophone (Baritone)  67");
        instrumentList.add("Shakuhachi            77");
        instrumentList.add("Steel Drums          114");
        instrumentList.add("Strings               48");
        instrumentList.add("Strings (Pizzicato)   45");
		instrumentList.add("Strings (Slow)        51");
		instrumentList.add("Strings (Synth)       50");
		instrumentList.add("Strings (Tremolo)     44");
        instrumentList.add("Tom-Tom              119");
        instrumentList.add("Trombone              57");
        instrumentList.add("Trumpet               56");
        instrumentList.add("Trumpet (Muted)       59");
        instrumentList.add("Tuba                  58");
        instrumentList.add("Tubular Bell          14");
        instrumentList.add("Timpani               47");
		instrumentList.add("Vibraphone            11");
        instrumentList.add("Viola                 41");
        instrumentList.add("Violin                40");
        instrumentList.add("Voice                 53");
        instrumentList.add("Vox                   56");
        instrumentList.add("Whistle               78");
        instrumentList.add("Wood Block           115");
        instrumentList.add("Xylophone             13");
		
    }
  	
  	
	private void initializeVolumeList()
  	{
  		volumeList = new List();
		int   minVolume   = 7;  			
		int   maxVolume   = 256;  			
		int   volumeStep  = 6;  			
  		for ( int i = minVolume; i <= maxVolume; i += volumeStep)
  		{
			volumeList.add( (new Integer(i)).toString() );
		}			   			
    }
    
	private void initializeTempoList()
  	{
  		tempoList = new List();
  		for (double x = 36.0;  x <143.0;  x+= 2.0) {
			tempoList.add( (new Double(x)).toString() );
		}			   			
  		for (double y = 144.0;  y <250.0;  y+= 4.0) {
			tempoList.add( (new Double(y)).toString() );
		}			   			
        for (double z = 256.0;  z < 404.0;  z+= 8.0) {
            tempoList.add( (new Double(z)).toString() );
        }                       
    }

	private void initializeButtons()
	{
		instrumentButton = new Button("Apply");
		volumeButton     = new Button("Apply");
		tempoButton      = new Button("Apply");
		closeButton      = new Button("Close");
	}
			
	private void initializeLabels()
	{
		instrumentLabel = new Label("Instrument");
		volumeLabel     = new Label("Volume");
		tempoLabel      = new Label("Tempo");
	}		
    
    private void placeControls() {
		GridBagLayout      layout = new GridBagLayout();    	
		GridBagConstraints c      = new GridBagConstraints();    	
		setLayout(layout);

		c.fill  = GridBagConstraints.BOTH;
		c.weightx = 0.5;		
		c.gridwidth  = 1;
		c.gridheight = 1;
		
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridheight = 3;
		layout.setConstraints(instrumentLabel, c); 
		add( instrumentLabel);
		c.gridx     = 1;
		layout.setConstraints(instrumentList, c); 
		add( instrumentList);
		c.gridwidth = GridBagConstraints.REMAINDER; 		
		c.gridx     = 2;
                Panel ibPanel = new Panel();
                ibPanel.add(instrumentButton);
		layout.setConstraints(ibPanel, c); 
		add(ibPanel);      	
		c.gridheight = 1;
		
		c.gridwidth  = 1;
		c.gridx     = 0;
		c.gridy     = 3;
		layout.setConstraints(volumeLabel, c); 
		add( volumeLabel);
		c.gridx     = 1;
		layout.setConstraints(volumeList, c); 
		add( volumeList);
		c.gridwidth = GridBagConstraints.REMAINDER; 		
		c.gridx     = 2;
                Panel vbPanel = new Panel();
                vbPanel.add(volumeButton);
		layout.setConstraints(vbPanel, c); 
		add(vbPanel);      	
		
		c.gridwidth  = 1;
		c.gridx     = 0;
		c.gridy     = 4;
		layout.setConstraints(tempoLabel, c); 
		add( tempoLabel);
		c.gridx     = 1;
		layout.setConstraints(tempoList, c); 
		add( tempoList);
		c.gridwidth = GridBagConstraints.REMAINDER; 		
		c.gridx     = 2;
                Panel tbPanel = new Panel();
                tbPanel.add(tempoButton);
		layout.setConstraints(tbPanel, c); 
		add(tbPanel);      	
		
		c.gridwidth  = 1;
		c.gridx = 1;
		c.gridy = 5;
		layout.setConstraints(closeButton, c); 
		add(closeButton);

		instrumentButton.addActionListener(this);
		volumeButton.addActionListener(this);     
		tempoButton.addActionListener(this);      
		closeButton.addActionListener(this);      

	}    	
	
	private static double getTempo(String s)		
	{
		return (new Double(pullFirst(s))).doubleValue();
	}		

	private static int getVolume(String s)		
	{
		return (new Integer(pullLast(s))).intValue();
	}		
	
	private static int getInstrument(String s)		
	{
		return (new Integer(pullLast(s))).intValue();
	}		
	
	private void setVolume(int newVol)		
	{
		Vector noteList = phrase.getNoteList();			
		for (int i=0; i < noteList.size(); i++ ) {
			if (phrase.getNote(i).getDynamic() != 0){
				phrase.getNote(i).setDynamic(newVol);
			}						
		}							
	}		

	
	private void multiplyTimesBy(double newFactor)		
	{
		Vector noteList = phrase.getNoteList();			
		System.out.println( newFactor );
		for (int i=0; i < noteList.size(); i++ ) {
		}							
	}		
	
	private static String pullFirst( String s ) 
	{
		StringTokenizer t = new StringTokenizer(s);
		String answer = "";
		while((answer == "") && (t.hasMoreTokens())){
			answer = t.nextToken();
		}						
		return answer;
	}	
	
	private static String pullLast( String s ) 
	{
		StringTokenizer t = new StringTokenizer(s);
		String answer  = "";
		String tString = "";
		while(t.hasMoreTokens()){
			tString = t.nextToken();
			if (tString != "") {
				answer = tString;
			}							
		}						
		return answer;
	}	
}
