/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Brown and Andrew Sorensen

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
import java.lang.Integer;
import java.io.*;
import javax.sound.midi.*;
import jm.gui.cpn.JmMidiPlayer;
import jm.music.data.*;
import jm.midi.*;
import jm.util.Read;
import jm.util.Write;
import jm.gui.cpn.Stave;
import jm.util.Play;
import jm.JMC;

/**
* This class displays a frame with a common practice notation display
* of the score passed to it.
* The parameter and add data by text attributes only work on the first stave.
* Some GPL changes for jMusic CPN Written by Al Christians 
* (achrist@easystreet.com).
* Contributed by Trillium Resources Corporation, Oregon's
* leading provider of unvarnished software.
* @author Andrew Brown
*/

public class Notate extends Frame implements 
                ActionListener, 
                WindowListener, JMC {
    private Score score;
    //private Stave stave;
    //private Phrase phrase;
    private Phrase[] phraseArray;
    private Stave[] staveArray;
    private int scrollHeight = 130, locationX = 0, locationY = 0;
    private Dialog keyDialog, timeDialog;
    private MenuItem keySig, open, openJmXml, openjm, play, stop, delete, clear, 
                    newStave, close, timeSig, saveJmXml, saveJM, saveMidi, quit,
                    trebleStave, bassStave, pianoStave, grandStave, automaticStave,
                    
                    // Some menu options added
                    appendMidiFile, 
                    insertMidiFile,
                    setParameters,
                    playAll,
                    playMeasure,
                    repeatAll,
                    repeatMeasure,
                    stopPlay,
                    earTrain,
                    addNotes,
                    adjustTiming, viewDetails, viewTitle,
                    viewZoom, barNumbers; 	
    				
    public boolean timeToStop;                 				
    // the panel for all the stave panels to go in to
    private Panel scoreBG;
    // the constraints for the scoreBG layout
    private GridBagConstraints constraints;
    private GridBagLayout layout;
    // The scoreBg goes into this scroll pane to enable navigation
    private ScrollPane scroll;

    private String lastFileName   = "*.mid";
    private String lastDirectory  = "";
    private String fileNameFilter = "*.mid";

    private boolean     zoomed;
    private Phrase      beforeZoom = new Phrase();
    private Phrase      afterZoom = new Phrase();
    /* The height of the notate window */
    private int height = 0;
    private int width = 700;

    public Notate() {
        this(new Phrase(), 0, 0);
        clearZoom();
    }
    
    public Notate(int locX, int locY) {
        this(new Phrase(), locX, locY);
        clearZoom();
    }
    
    public Notate(Phrase phr) {
        this(phr, 0, 0);
        clearZoom();
    }
    
    private void clearZoom() {
        zoomed = false;
    }        

    public Notate(Phrase phrase, int locX, int locY) {
        super("CPN: "+ phrase.getTitle());
        clearZoom();
        this.score = new Score(new Part(phrase));
        locationX = locX;
        locationY = locY;
        score = new Score(new Part(phrase));
        init();
    }
    
    public Notate(Score score, int locX, int locY) {
        super("CPN: "+ score.getTitle());
        clearZoom();
        this.score = score;
        locationX = locX;
        locationY = locY;
        init();
    }
    
    public void init() {
        addWindowListener(this);
        // menus
        MenuBar menus = new MenuBar();
        Menu edit  = new Menu("File", true);
        Menu features = new Menu("Tools", true);
        Menu player   = new Menu("Play", true);
        Menu view     = new Menu("View", true);
        //------
        newStave = new MenuItem("New", new MenuShortcut(KeyEvent.VK_N));
        newStave.addActionListener(this);
        edit.add( newStave);
        
        //------
        open = new MenuItem("Open MIDI file...", new MenuShortcut(KeyEvent.VK_O));
        open.addActionListener(this);
        edit.add(open);
        //------
        openJmXml = new MenuItem("Open jMusic XML file...");
        openJmXml.addActionListener(this);
        edit.add(openJmXml);
        //------
        openjm = new MenuItem("Open jm file..");
        openjm.addActionListener(this);
        edit.add(openjm);
        //------
        close = new MenuItem("Close", new MenuShortcut(KeyEvent.VK_W));
        close.addActionListener(this);
        edit.add(close);
        //------
        edit.add("-");
        delete = new MenuItem("Delete last note", new MenuShortcut(KeyEvent.VK_D));
        delete.addActionListener(this);
        edit.add(delete);
        //------
        clear = new MenuItem("Clear all notes" , new MenuShortcut(KeyEvent.VK_C));
        clear.addActionListener(this);
        edit.add(clear);
        //------
        edit.add("-");
        //------
        keySig = new MenuItem("Key Signature", new MenuShortcut(KeyEvent.VK_K));
        keySig.addActionListener(this);
        edit.add(keySig);
        //------
        timeSig = new MenuItem("Time Signature", new MenuShortcut(KeyEvent.VK_T));
        timeSig.addActionListener(this);
        edit.add(timeSig);
        //------
        edit.add("-");
        //------
        saveMidi = new MenuItem("Save as a MIDI file...", new MenuShortcut(KeyEvent.VK_S));
        saveMidi.addActionListener(this);
        edit.add(saveMidi);
        //------
        saveJmXml = new MenuItem("Save as a jMusic XML file...", new MenuShortcut(KeyEvent.VK_S, true));
        saveJmXml.addActionListener(this);
        edit.add(saveJmXml);
        //------
        saveJM = new MenuItem("Save as a jm file...");
        saveJM.addActionListener(this);
        edit.add(saveJM);

        //------
        edit.add("-");
        //-Features Added by Al C -----

        //------     
        /*  
        insertMidiFile = new MenuItem("Insert a MIDI file..." );
        insertMidiFile.addActionListener(this);
        features.add(insertMidiFile);

        appendMidiFile = new MenuItem("Append a MIDI file..." );
        appendMidiFile.addActionListener(this);
        features.add(appendMidiFile);
        */
        setParameters = new MenuItem("Set Parameters..." );
        setParameters.addActionListener(this);
        features.add(setParameters);
        //------        
        //------        
        
        addNotes = new MenuItem("Add Notes by Letter" );
        addNotes.addActionListener(this);
        features.add(addNotes);
        
        //------        
        adjustTiming = new MenuItem("Quantize Timing");
        adjustTiming.addActionListener(this);
        features.add(adjustTiming);

        //------        
        playAll = new MenuItem("Play All", new MenuShortcut(KeyEvent.VK_P));
        playAll.addActionListener(this);
        player.add(playAll);
        repeatAll = new MenuItem("Repeat All" );
        repeatAll.addActionListener(this);
        player.add(repeatAll);

        //------        
        playMeasure = new MenuItem("Play Last Measure" );
        playMeasure.addActionListener(this);
        player.add(playMeasure);
        
        repeatMeasure = new MenuItem("Repeat Last Measure" );
        repeatMeasure.addActionListener(this);
        player.add(repeatMeasure);

        stopPlay = new MenuItem("Stop Playback", new MenuShortcut(KeyEvent.VK_P, true) );
        stopPlay.addActionListener(this);
        player.add(stopPlay);
        
        //earTrain = new MenuItem("Ear Train" );
        //earTrain.addActionListener(this);
        //player.add(earTrain);

        Menu staveMenu = new Menu( "Stave");
        edit.add(staveMenu);
        trebleStave = new MenuItem("Treble");
        trebleStave.addActionListener(this);
        staveMenu.add(trebleStave);
        bassStave = new MenuItem("Bass");
        bassStave.addActionListener(this);
        staveMenu.add(bassStave);
        pianoStave = new MenuItem("Piano");
        pianoStave.addActionListener(this);
        staveMenu.add(pianoStave);
        grandStave = new MenuItem("Grand");
        grandStave.addActionListener(this);
        staveMenu.add(grandStave);
        automaticStave = new MenuItem("Automatic");
        automaticStave.addActionListener(this);
        staveMenu.add(automaticStave);
        
        //------
        edit.add("-");
        
        //------
        quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
        quit.addActionListener(this);
        edit.add(quit);

        //------
        viewDetails = new MenuItem("Note data as text");
        viewDetails.addActionListener(this);
        view.add(viewDetails);
        
        viewZoom = new MenuItem("View phrase section", new MenuShortcut(KeyEvent.VK_V));
        viewZoom.addActionListener(this);
        view.add(viewZoom);
        
        barNumbers = new MenuItem("Bar Numbers", new MenuShortcut(KeyEvent.VK_B));
        barNumbers.addActionListener(this);
        view.add(barNumbers);
        
        viewTitle = new MenuItem("Stave Title");
        viewTitle.addActionListener(this);
        view.add(viewTitle);

        //-------
        menus.add(edit);
        menus.add(features);
        menus.add(player);
        menus.add(view);
        this.setMenuBar(menus);
        
        // components
        scroll = new ScrollPane(1);
       
        scroll.getHAdjustable().setUnitIncrement(10);
        scroll.getVAdjustable().setUnitIncrement(10);
                
        scoreBG = new Panel();
        layout = new GridBagLayout();
        scoreBG.setLayout(layout); //new GridLayout(score.size(), 1));
        constraints = new GridBagConstraints();
        setupConstraints();
        
        scroll.add(scoreBG);
        this.add(scroll); 
        
        setupArrays();
        makeAppropriateStaves();
               
        this.pack();
        this.setLocation(locationX, locationY);
       // calcHeight();
        //this.setSize(width, height + 40);
        /*
        // check window size against screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        if(scroll.getSize().height > d.height) {
            System.out.println("Adjusting height");
            scroll.setSize(new Dimension(this.width, d.height));
        }
        */
        this.show();
    }
    
    private void setupArrays() {
        // set up arrays
        phraseArray = new Phrase[score.size()];
        staveArray = new Stave[score.size()];

        for (int i=0; i<staveArray.length; i++) {
            phraseArray[i] = score.getPart(i).getPhrase(0);
            staveArray[i] = new PianoStave();
            staveArray[i].setKeySignature(score.getKeySignature());
            staveArray[i].setMetre(score.getNumerator());
            staveArray[i].setBarNumbers(true);
        }
    }
    
    private void setupConstraints() {
        constraints.weightx = 100;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        //constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
    }
    
    private void calcHeight() {
        // work out the height
        height = 0;
        for (int i=0; i<staveArray.length; i++) {
            height += staveArray[i].getSize().height;
        }

    }
    
    private void makeAppropriateStaves(){
        Stave[] tempStaveArray  = new Stave[staveArray.length];
        for(int i=0; i<score.size(); i++) {
            Phrase currentPhrase = score.getPart(i).getPhrase(0);
            tempStaveArray[i] = new PianoStave();
            if(currentPhrase.getHighestPitch() < A6 &&
                currentPhrase.getLowestPitch() > FS3) tempStaveArray[i] = new TrebleStave();
            else if(currentPhrase.getHighestPitch() < F4 &&
                currentPhrase.getLowestPitch() > B1) tempStaveArray[i] = new BassStave();
            else if(currentPhrase.getHighestPitch() > A6 ||
                currentPhrase.getLowestPitch() < B1) tempStaveArray[i] = new GrandStave();
        }
        updateAllStaves(tempStaveArray);

    }   

    private void makeTrebleStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<staveArray.length; i++) {
             tempStaveArray[i] = new TrebleStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    private void updateAllStaves(Stave[] tempStaveArray) {
        int gridyVal = 0;
        int gridheightVal = 0;
        int totalHeight = 0;
        scoreBG.removeAll();
        for(int i=0; i<staveArray.length; i++) {
            // store current phrase parameters in new stave object
            tempStaveArray[i].setKeySignature(staveArray[i].getKeySignature());
            tempStaveArray[i].setMetre(staveArray[i].getMetre());
            tempStaveArray[i].setBarNumbers(staveArray[i].getBarNumbers());
            tempStaveArray[i].setPhrase(phraseArray[i]);
            // create new stave panel
            staveArray[i] = tempStaveArray[i];
            tempStaveArray[i] = null;
            // set and add constraints
            constraints.gridy = gridyVal;
            if(staveArray[i].getClass().isInstance(new TrebleStave()) || 
                staveArray[i].getClass().isInstance(new BassStave())) {
                    gridheightVal = 1;
            } else if(staveArray[i].getClass().isInstance(new PianoStave())) {
                gridheightVal = 2;
            } else {
                gridheightVal = 3;
            }
            constraints.gridheight = gridheightVal;
            // add to display
            scoreBG.add(staveArray[i], constraints);
            gridyVal += gridheightVal;
            totalHeight += staveArray[i].getPanelHeight();
        }
        //calcHeight();
        scroll.setSize(new Dimension(width, totalHeight));
        // check window size against screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        this.setSize(new Dimension(this.width, Math.min(d.height-40, totalHeight+40)));
        //this.setResizable(true);
        this.pack();
    }        

    private void makeBassStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<staveArray.length; i++) {
             tempStaveArray[i] = new BassStave();
        }
        updateAllStaves(tempStaveArray);
    }
        /*
            // store current phrase
            phrase = stave.getPhrase().copy();
            int tempKey = stave.getKeySignature();
            double tempTime = stave.getMetre();
            boolean tempBarNumbers = stave.getBarNumbers();
            // create new stave panel
            stave = new BassStave();
            scoreBG.removeAll();
            scoreBG.add(stave);
            scroll.setSize(width, stave.getSize().height + 20);
            this.pack();
            // replace stave
            stave.setPhrase(phrase);
            stave.setKeySignature(tempKey);
            stave.setMetre(tempTime);
            stave.setBarNumbers(tempBarNumbers);
    }
    */
    
    private void makePianoStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<tempStaveArray.length; i++) {
             tempStaveArray[i] = new PianoStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    /*
            // store current phrase
            phrase = stave.getPhrase().copy();
            int tempKey = stave.getKeySignature();
            double tempTime = stave.getMetre();
            boolean tempBarNumbers = stave.getBarNumbers();
            // create new stave panel
            stave = new PianoStave();
            scoreBG.removeAll();
            scoreBG.add(stave);
            scroll.setSize(width, stave.getSize().height + 20);
            this.pack();
            // replace stave
            stave.setPhrase(phrase);
            stave.setKeySignature(tempKey);
            stave.setMetre(tempTime);
            stave.setBarNumbers(tempBarNumbers);
    }    
    */
        
    private void makeGrandStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<tempStaveArray.length; i++) {
             tempStaveArray[i] = new GrandStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    /*
            // store current phrase
            phrase = stave.getPhrase().copy();
            int tempKey = stave.getKeySignature();
            double tempTime = stave.getMetre();
            boolean tempBarNumbers = stave.getBarNumbers();
            // create new stave panel
            stave = new GrandStave();
            scoreBG.removeAll();
            scoreBG.add(stave);
            scroll.setSize(width, stave.getSize().height + 20);
            this.pack();
            // replace stave
            stave.setPhrase(phrase);
            stave.setKeySignature(tempKey);
            stave.setMetre(tempTime);
            stave.setBarNumbers(tempBarNumbers);
    }       
    */ 

    class PlayRepeater extends Thread {
        
        JmMidiPlayer midiPlayer;
        Notate       n; 
        
        public PlayRepeater(         
                String       str,
                Notate       nParm,                
                JmMidiPlayer midiPlayerParm ) {
            super(str);           
            n         = nParm;         
            midiPlayer = midiPlayerParm;                    
        }                    
        
        public void run() {
            do {
                midiPlayer.play();                                                        
            } while(!n.timeToStop);                
        }
    }        
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == close) dispose();
        else
        if(e.getSource() == newStave) new Notate();
        else
        if(e.getSource() == open) openMidi();
        else
        if(e.getSource() == openjm) openJM();
        else
        if(e.getSource() == openJmXml) openJMXML();
        else
        if(e.getSource() == keySig) {
            for(int i=0; i<staveArray.length; i++) {
                if(staveArray[i].getKeySignature() == 0) {
                    staveArray[i].setKeySignature(2);
                    staveArray[i].repaint();
                } else {
                    staveArray[i].setKeySignature(0);
                    staveArray[i].repaint();
                }
            }
        }
        else
        if(e.getSource() == timeSig) {
            for(int i=0; i<staveArray.length; i++) {
                if(staveArray[i].getMetre() == 0.0) {
                    staveArray[i].setMetre(4.0);
                    staveArray[i].repaint();
                } else {
                    staveArray[i].setMetre(0.0);
                    staveArray[i].repaint();
                }
            }
        }
        else
        if(e.getSource() == saveJM) saveJM();
        else
        if(e.getSource() == saveJmXml) saveJMXML();
        else
        if(e.getSource() == saveMidi) saveMidi();
        else
        if(e.getSource() == quit) System.exit(0);
        else
        if(e.getSource() == delete) {
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].deleteLastNote();
            }
        } else
        if(e.getSource() == clear) {
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].getPhrase().empty();
                staveArray[i].repaint();
            }
        }
        else
        if(e.getSource() == trebleStave) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            makeTrebleStave();
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        else
        if(e.getSource() == bassStave) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            makeBassStave();
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        else
        if(e.getSource() == pianoStave) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            makePianoStave();
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        else
        if(e.getSource() == grandStave) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            makeGrandStave();
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        else
        if(e.getSource() == automaticStave) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            makeAppropriateStaves();
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        /*
        else
        if(e.getSource() == insertMidiFile) { }
        
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            Phrase savePhrase = phrase;
        	phrase = readMidiPhrase();
            phrase.addNoteList(
                savePhrase.getNoteList(),
                true
            );        
            stave.setPhrase(phrase);
            for(int i=0; i<staveArray.length; i++) {
                staveArray[i].repaint();
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        else
        if(e.getSource() == appendMidiFile) { }
        
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            phrase.addNoteList(
                readMidiPhrase().getNoteList(),
                true
            );        
            stave.setPhrase(phrase);
            stave.repaint();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        */
        else if(e.getSource() == setParameters) { // only works on the top stave
            ParmScreen parmScreen = new ParmScreen(this);
            //for(int i=0; i<staveArray.length; i++) {
                double oldTempo = staveArray[0].getPhrase().getTempo(); 					
                parmScreen.getParms(staveArray[0].getPhrase(), 15, 15 );				
            //}
            repaint();
        }
        else if( e.getSource() == playAll )  {
                Play.midi(score, false);   
        }
        else if  (e.getSource() == repeatAll) Play.midiCycle(score); 
        else if(e.getSource() == stopPlay) {
        	
        	Play.stopMidi(); // stop a single playback
                Play.stopMidiCycle(); // stop a cycle playback
        }         
        else if (e.getSource() == repeatMeasure) {
            Play.midiCycle(getLastMeasure());  
        }
        else if(e.getSource() == playMeasure ) {
            Play.midi(getLastMeasure(), false);   
        }
        
        else if(e.getSource() == addNotes) { // only works on the top stave
            LetterNotesEditor notesScreen 
                    = new LetterNotesEditor(this);
            notesScreen.getNotes(staveArray[0]);
            staveArray[0].repaint();
        }
        else if(e.getSource() == adjustTiming) { // only works on the top stave
            adjustTimeValues(staveArray[0].getPhrase());
            staveArray[0].repaint();
        }
        else if(e.getSource() == viewDetails) { // only works on the top stave
            PhraseViewer phraseViewer 
                    = new PhraseViewer(this);
            phraseViewer.showPhrase(
                            staveArray[0],
                            staveArray[0].getPhrase(),
                            15, 15 );               
        }
        else if(e.getSource() == viewZoom) { // only works on the top stave
            if (!zoomed) {
                CpnZoomScreen zoomSelector 
                        = new CpnZoomScreen(this);
                beforeZoom = staveArray[0].getPhrase().copy();                        
                afterZoom  = staveArray[0].getPhrase().copy();                        
                beforeZoom.empty();
                afterZoom.empty();
                zoomSelector.zoomIn(
                            beforeZoom,
                            staveArray[0].getPhrase(),
                            afterZoom);               
                if (beforeZoom.size() +
                    afterZoom.size() > 0 ) {
                    zoomed = true;
                    viewZoom.setLabel("View complete phrase");                        
                    repaint();
                }                                                    
            }
            else {
                CpnZoomScreen.zoomOut(
                            beforeZoom,
                            staveArray[0].getPhrase(),
                            afterZoom );               
                zoomed = false;
                viewZoom.setLabel("View phrase section");                        
                repaint();
            }                                                            
        }
        else if (e.getSource() == barNumbers) {
            for(int i=0; i<staveArray.length; i++ ) {
                staveArray[i].setBarNumbers(!staveArray[i].getBarNumbers());
                staveArray[i].repaint();
            }
        } 
        else if(e.getSource() == viewTitle) toggleDisplayTitle();
        //else if (e.getSource() == earTrain ) {
        //    (new EarTrainer().show());
        //} 
                  
    }
    
    /**
    * Dialog to import a MIDI file
    */
     public void openMidi() {
        Score s = new Score();
        FileDialog loadMidi = new FileDialog(this, "Select a MIDI file.", FileDialog.LOAD);
        loadMidi.setDirectory( lastDirectory );
        loadMidi.setFile( lastFileName );
        loadMidi.show();
        String fileName = loadMidi.getFile();
        if (fileName != null) {
            lastFileName = fileName;
            lastDirectory = loadMidi.getDirectory();                        
            Read.midi(s, lastDirectory + fileName);  
            setNewScore(s);
        }
    }
    
    
    private void setNewScore(Score score) {
        this.score = score;
        // set up arrays
        setupArrays();        
        makeAppropriateStaves();
    }
    
    /**
     * Dialog to import a jm file
     */
     
     public void openJM() {
        FileDialog loadjm = new FileDialog(this, "Select a jm score file.", FileDialog.LOAD);
        loadjm.setDirectory( lastDirectory );
        loadjm.show();
        String fileName = loadjm.getFile();
        if (fileName != null) {
            Score s = new Score();
            lastDirectory = loadjm.getDirectory();  
            Read.jm(s, lastDirectory + fileName);
            setNewScore(s);
        }
    }
    
    /**
     * Dialog to import a jm XML file
     */
     
     public void openJMXML() {
        FileDialog loadjmxml = new FileDialog(this, "Select a jMusic XML score file.", FileDialog.LOAD);
        loadjmxml.setDirectory( lastDirectory );
        loadjmxml.show();
        String fileName = loadjmxml.getFile();
        if (fileName != null) {
            Score s = new Score();
            lastDirectory = loadjmxml.getDirectory(); 
            Read.xml(s, lastDirectory + fileName);
            setNewScore(s);
        }
    }
    

    
    /**
     * Dialog to save phrase as a MIDI file.
     */
    public void saveMidi() {
        FileDialog fd = new FileDialog(this, "Save as a MIDI file...",FileDialog.SAVE);
                fd.show();
                            
        //write a MIDI file and stave properties to disk
        if ( fd.getFile() != null) {
            Write.midi(score, fd.getDirectory()+fd.getFile());
            /*
            for(int i=0; i<staveArray.length; i++){
                System.out.println(i);
                StavePhraseProperties props =
                    new StavePhraseProperties(
                            staveArray[i], staveArray[i].getPhrase());
                try {    
                    System.out.println("props");
                    props.writeToFile(                                         
                        fd.getDirectory()+fd.getFile());   
                }
                catch ( Throwable e) {
                    System.out.println(
                        "Error Saving Properties " +
                        e.getMessage() );                                                
                }
            } 
            */
                                              
        }
    }
    
    /**
     * Dialog to save score as a jMusic serialized jm file.
     */
    public void saveJM() {
        FileDialog fd = new FileDialog(this, "Save as a jm file...",FileDialog.SAVE);
                fd.show();
                            
        //write a MIDI file to disk
        if ( fd.getFile() != null) {
            Write.jm(score, fd.getDirectory()+fd.getFile());
        }
    }
    
    /**
     * Dialog to save score as a jMusic XML file.
     */
    public void saveJMXML() {
        FileDialog fd = new FileDialog(this, "Save as a jMusic XML file...",FileDialog.SAVE);
                fd.show();
                            
        //write an XML file to disk
        if ( fd.getFile() != null) {
            Write.xml(score, fd.getDirectory()+fd.getFile());
        }
    }

    
    /**
    * Get the first phrase from a MIDI file.
    */
    public Phrase readMidiPhrase() {
        FileDialog loadMidi = new FileDialog(this, "Select a MIDI file.", FileDialog.LOAD);
        loadMidi.show();
        String fileName = loadMidi.getFile();
        Phrase phr = new Phrase(0.0);
        Score scr = new Score();
        if (fileName != null) {
            Read.midi(scr, loadMidi.getDirectory() + fileName); 
        }
        scr.clean();
        if (scr.size() > 0 && scr.getPart(0).size() > 0) phr = scr.getPart(0).getPhrase(0);
        //System.out.println("Size = " + phr.size());
        return phr;
    }
    
    private Score getLastMeasure() {
        double beats = phraseArray[0].getNumerator();
        double endTime = score.getEndTime();
        int numbOfCompleteBars = (int)(endTime / beats);
        double startOflastBar = beats * numbOfCompleteBars;
        if (startOflastBar == endTime) startOflastBar -= beats;
        Score oneBar = score.copy(startOflastBar, endTime);
        
        for(int i=0; i<oneBar.size();i++){
            oneBar.getPart(i).getPhrase(0).setStartTime(0.0);
        }
        return oneBar;
    }
    
    private static double getRhythmAdjustment(
                        double  beats,
                        double  beatIncrement ) {
        double increments;
        increments = beats/beatIncrement;
        double tolerance;                                   
        tolerance = 0.00001;
        double answer;  
        answer = 0.0;
        double n;
        n = Math.floor(increments);
        while(( Math.floor(increments+tolerance) > n ) 
                && (tolerance > 0.00000000000001)) {
            answer = tolerance;
            tolerance = tolerance / 2;
        }                        
        return answer * beatIncrement;                            
    }                            
    
    private static void adjustTimeValues(Phrase phr) {
        int i;
        double t, dt, st;
        for( i = 0; i < phr.size(); ++i) {        
            t  = phr.getNote(i).getRhythmValue();
            dt = getRhythmAdjustment( t, 1.0 / 256.0 ); 
            phr.getNote(i).setRhythmValue(t+dt);
        }
        
        st = 0.0;
        for( i = 0; i < phr.size(); ++i) {        
            t  = phr.getNote(i).getRhythmValue();
            st = st + t;
            dt = getRhythmAdjustment( st, 1.0 ); 
            phr.getNote(i).setRhythmValue(t+dt);
            st = st + dt;
        }
    }  
    
    /**
    * Toggle the phrase title display
    */
    public void toggleDisplayTitle() {
        for(int i=0; i<staveArray.length; i++) {
            staveArray[i].setDisplayTitle(!staveArray[i].getDisplayTitle());
        }
    }
    
    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == this) dispose();
        if(e.getSource() == keyDialog) keyDialog.dispose();
        if(e.getSource() == timeDialog) timeDialog.dispose();
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
