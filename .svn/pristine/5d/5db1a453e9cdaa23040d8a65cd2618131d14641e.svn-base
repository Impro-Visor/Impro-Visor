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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.BufferedReader;
import java.io.FileReader;

import jm.midi.SMF;
import jm.music.data.*;
import jm.JMC;
import jm.gui.show.*;
import jm.gui.cpn.*;
import jm.gui.sketch.*;
import jm.audio.Instrument;
import jm.audio.Audio;
import jm.audio.io.AudioFileIn;
import jm.util.XMLParser;


import javax.swing.JOptionPane;

/**
 * Reads data files and stores the musical information in the supplied Score.
 *
 * @author Unascribed, Adam Kirby
 * @version 0.0.0.1, 27th February 2001
 */
public class Read implements JMC{

    /** Not meant to be instantiated, only provides static members */
    protected Read() {
    }
   
    //----------------------------------------------
    // MIDI
    //----------------------------------------------
    
    /**
    * Import to a jMusic score a standard MIDI file
    * Assume the MIDI file name is the same as the score title with .mid appended
    * prompt for a fileName
    * @param Score
    */ 
    public static void midi(Score score) {
        FileDialog fd = new FileDialog(new Frame(), 
                                       "Select a MIDI file to open.", 
                                       FileDialog.LOAD);
        fd.show();
        if (fd.getFile() != null) {
            Read.midi(score, fd.getDirectory() + fd.getFile());                
        }
    }
    
    /**
    * Import to a jMusic score a standard MIDI file
    * @param Score
    * @param String fileName
    */ 
    public static void midi(Score s, String fileName) {
        if (s == null) {
                System.err.println("jMusic Read.midi error: The score is not initialised! I'm doing it for you.");
                s = new Score();
        }
        s.empty();
        SMF smf = new SMF();
        //smf.setVerbose(true);
        try{
                //System.out.println("--------------------- Reading MIDI File ---------------------");
                InputStream is = new FileInputStream(fileName);
                smf.read(is);
                jm.midi.MidiParser.SMFToScore(s,smf);
                //System.out.println("MIDI file '"+fileName+"' read into score '" +s.getTitle()+"' Tempo = " + s.getTempo());
                //System.out.println("-------------------------------------------------------------");
        }catch(IOException e){
                System.err.println(e);
        }
    }
    
    
    /**
    * Read the first track from a standard MIDI file into a jMusic part
    * @param Part
    */ 
    public static void midi(Part p) {  
        Score s = new Score();
        midi(s);
        p = s.getPart(0);
        
    }


    /**
    * Read the first track from a standard MIDI file into a jMusic part
    * @param Part
    * @param String fileName
    */ 
    public static void midi(Part p, String fileName) {  
        Score s = new Score();
        midi(s, fileName);
        p = s.getPart(0);
    }
    
    /**
    * Read the first phrase of the first track from a standard MIDI file
    * into a jMusic phrase
    * @param Phrase
    */ 
    public static void midi(Phrase phr) {  
        Score s = new Score();
        midi(s);
        phr = s.getPart(0).getPhrase(0);
    }

    /**
    * Read the first phrase of the first track from a standard MIDI file
    * into a jMusic phrase
    * @param Phrase
    * @param String fileName
    */ 
    public static void midi(Phrase phr, String fileName) {  
        Part p = new Part();
        midi(p, fileName);
        phr = p.getPhrase(0);
    }
    
    /**
    * Read the first track from a standard MIDI file into a jMusic cphrase
    * @param Part
    * @param String fileName
    */ 
    public static void midi(CPhrase cphr, String fileName) {  
        Score s = new Score();
        midi(s, fileName);
        Part p = new Part();
        p = s.getPart(0);
        for(int i=0;i<p.size(); i++) {
            cphr.addPhrase(p.getPhrase(i));
        }
    }
    
    //----------------------------------------------
    // jm
    //----------------------------------------------
    /**
    * Import the jm file as a jMusic score
    * Use the score title as a the fileName
    * @param Score
    */ 
    public static void jm(Score s) {
        jm(s, s.getTitle()+".jm");
    }
    
    /**
    * Import the jm file as a jMusic score
    * @param Score
    * @param String fileName
    */ 
    public static void jm(Score s, String fileName) {        
        if (s == null) {
                System.err.println("jMusic Read.jm error: The score is not initialised! I'm doing it for you.");
                s = new Score();
        }
        s.empty();
        try{
            System.out.println("--------------------- Reading .jm File ---------------------");
                    InputStream is = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(is);
            try {
                    s.addPartList(((Score) ois.readObject()).getPartArray());
                    System.out.println("reading");
            } catch (ClassNotFoundException e) {System.err.println(e);}
                System.out.println("jm file '"+fileName+"' read into score '" +s.getTitle()+"'");
                System.out.println("-------------------------------------------------------------");
            }catch(IOException e){
            System.err.println(e);
        }
    }
    
    /**
    * Read the first part from a jm file into a jMusic part
    * @param Part
    * @param String fileName
    */ 
    public static void jm(Part p, String fileName) {  
      if (p == null) {
            System.err.println("jMusic Read.jm error: The part is not initialised! I'm doing it for you.");
            p = new Part();
        }
        p.empty();
        Score s = new Score();
        jm(s, fileName);
        p.addPhraseList(s.getPart(0).getPhraseArray());
    }
    
    /**
    * Read the first phrase of the first part from jm file
    * into a jMusic phrase
    * @param Phrase
    * @param String fileName
    */ 
    public static void jm(Phrase phr, String fileName) {
    	if (phr == null) {
            System.err.println("jMusic Read.jm error: The phrase is not initialised! I'm doing it for you.");
            phr = new Phrase();
        }
        phr.empty();
        Part p = new Part();
        jm(p, fileName);
        phr.addNoteList(p.getPhrase(0).getNoteArray());    
    }
    
    /**
    * Read the first part from a jm file into a jMusic cphrase
    * @param Part
    * @param String fileName
    */ 
    public static void jm(CPhrase cphr, String fileName) {  
    	if (cphr == null) {
            System.err.println("jMusic Read.jm error: The CPhrase is not initialised! I'm doing it for you.");
            cphr = new CPhrase();
        }
        cphr.empty();
        Score s = new Score();
        jm(s, fileName);
        Part p = new Part();
        p = s.getPart(0);
        for(int i=0;i<p.size(); i++) {
      	cphr.addPhrase(p.getPhrase(i));
      }
    }
    
    //----------------------------------------------
    // XML
    //----------------------------------------------
    
    /**
    * Import the xml file as a jMusic score.
    * Prompt for a fileName
    * @param Score
    */ 
    public static void xml(Score score) {
        FileDialog fd = new FileDialog(new Frame(), 
                "Select a jMusic XML file to open.", 
                FileDialog.LOAD);
        fd.show();
        if (fd.getFile() != null) {
            Read.xml(score, fd.getDirectory() + fd.getFile());                
        }
    }
    
    /**
    * Import the xml file as a jMusic score
    * @param Score
    * @param String fileName
    */ 
    public static void xml(Score s, String fileName) {  
    	if (s == null) {
            System.err.println("jMusic Read.xml error: The score is not initialised! I'm doing it for you.");
            s = new Score();
        }
        s.empty();
        try{
            System.out.println("--------------------- Reading .xml File ---------------------");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            try {
                s.addPartList(XMLParser.xmlStringToScore(br.readLine()).getPartArray());
                System.out.println("reading");
            } catch (ConversionException e) {System.err.println(e);}
                System.out.println("xml file '"+fileName+"' read into score '" +s.getTitle()+"'");
                System.out.println("-------------------------------------------------------------");
            }catch(IOException e){
            System.err.println(e);
        }
    }
    
    
    /**
    * Read the first part from a xml file into a jMusic part
    * @param Part
    * @param String fileName
    */ 
    public static void xml(Part p, String fileName) {  
        if (p == null) {
            System.err.println("jMusic Read.xml error: The part is not initialised! I'm doing it for you.");
            p = new Part();
        }
        p.empty();
        Score s = new Score();
        xml(s, fileName);
        p.addPhraseList(s.getPart(0).getPhraseArray());
    }
    
    /**
    * Read the first phrase of the first part from xml file
    * into a jMusic phrase
    * @param Phrase
    * @param String fileName
    */ 
    public static void xml(Phrase phr, String fileName) {
    	if (phr == null) {
            System.err.println("jMusic Read.xml error: The phrase is not initialised! I'm doing it for you.");
            phr = new Phrase();
        }
        phr.empty();
        Part p = new Part();
        xml(p, fileName);
        phr.addNoteList(p.getPhrase(0).getNoteArray());    
    }
    
    /**
    * Read the first part from a xml file into a jMusic cphrase
    * @param Part
    * @param String fileName
    */ 
    public static void xml(CPhrase cphr, String fileName) {  
    	if (cphr == null) {
            System.err.println("jMusic Read.xml error: The CPhrase is not initialised! I'm doing it for you.");
            cphr = new CPhrase();
        }
        cphr.empty();
        Score s = new Score();
        xml(s, fileName);
        Part p = new Part();
        p = s.getPart(0);
        for(int i=0;i<p.size(); i++) {
      	cphr.addPhrase(p.getPhrase(i));
      }
    }
    
    //----------------------------------------------
    // Audio
    //----------------------------------------------
    
    public static float[] audio(String fileName) {
        System.out.println("-------------------- Reading Audio File ---------------------");
        AudioFileIn afi = new AudioFileIn(fileName);
        float[] sampleData = afi.getSampleData();
        System.out.println("File '"+fileName+"' read in. Details:");
        System.out.println("Channels = " + afi.getChannels()
                           + " Samples per channel = " + afi.getDuration() / afi.getChannels()
                           +" Sample rate = " + afi.getSampleRate() 
                           + " Bit depth = " + afi.getSampleBitDepth());
        System.out.println("-------------------------------------------------------------");
        return sampleData;
    }
        
    public static void audio(float[] sampleData, String fileName) {
        System.out.println("-------------------- Reading Audio File ---------------------");
        AudioFileIn afi = new AudioFileIn(fileName);
        sampleData = afi.getSampleData();
        System.out.println("Audio file '"+fileName+"' read in. Details:");
         System.out.println("Channels = " + afi.getChannels()
                            + " Samples per channel = " + afi.getDuration() / afi.getChannels()
                            +" Sample rate = " + afi.getSampleRate() 
                            + " Bit depth = " + afi.getSampleBitDepth());
        System.out.println("-------------------------------------------------------------");
    }

    //--------- Simultaneous midi and jm reading with error messaging --------//

    /**
     * Handles methods common to the main static methods.
     */
    protected static class JmMidiProcessor {
        /**
         * Error message to display, any method who alters this field should be
         * syncrhronized.
         */
        private String message = null;

        /**
         * Score describing the musical information extracted from the file.
         */
        private Score score = new Score();

        /**
         * Creates a new processor for reading jm and midi information from a
         * specified File.
         *
         * @param file  File storing the musical information
         */
        public JmMidiProcessor(final File file) {
            if (file == null) {
                message = "The selected file is null.  No JM/MIDI information "
                          + "could be imported.";
                score = null;
            } else if (file.isDirectory()) {
                message = "The selected file is a directory.  No JM/MIDI "
                          + "information could be imported.";
                score = null;
            } else {
                JmMidiProcessor processor = new JmMidiProcessor(
                        file.getParent() + file.separator, file.getName());
                message = processor.getMessage();
                score = processor.getScore();
            }
        }

        /**
         * Creates a new processor for reading jm and midi information from the
         * specified directory and file Strings.
         *
         * @param directory String describing the path of the directory, this
         *                  must be terminated with a file separator.
         * @param filename  String describing the name of the file to be read
         */
        public JmMidiProcessor(final String directory, final String filename) {
            if (filename == null) {
                message = "The filename String is null.  No JM/MIDI information"
                          + " could be imported.";
                score = null;
                return;
            } 
    
            /** Attempt to read file */
            try {
                score.setTitle(filename);
		SMF smf = new SMF();
                if (directory == null) {
		   InputStream is = new FileInputStream(filename);
                    smf.read(is);
			jm.midi.MidiParser.SMFToScore(score,smf);
                } else {
		   InputStream is = new FileInputStream(directory+filename);
                    smf.read(is);
	 		jm.midi.MidiParser.SMFToScore(score,smf);
                }
            } catch (IOException e1) {
                message = e1.getMessage();
                if (message == null) {
                    message = "Unknown IO Exception";
                    score = null;
                    return;
                } else if (message.equals("Track Started in wrong place!!!!"
                                          + "  ABORTING")) {
                    message = "The MIDI file corrupted.  Track data started in "
                              + "the wrong place.";
                    score = null;
                    return;
                } else if (message.equals("This is NOT a MIDI file !!!")) {
                    try {
                        FileInputStream fis = new FileInputStream(directory
                                                                  + filename);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        score = (Score) ois.readObject();
                        ois.close();
                        fis.close();
                    } catch (SecurityException e2) {
                        message = "Read access not allowed to " + filename;
                        score = null;
                        return;
                    } catch (ClassNotFoundException e2) {
                        message = "The file " + filename
                                  + " is neither a jm nor a MIDI file";
                        score = null;
                        return;
                    } catch (ClassCastException e2) {
                        message = "The file " + filename 
                                  + " is neither a jm nor a MIDI file";
                        score = null;
                        return;
                    } catch (StreamCorruptedException e2) {
                        message = "The file " + filename 
                                  + " is neither a jm nor a MIDI file";
                        score = null;
                        return;
                    } catch (IOException e2) {
                        message = e2.getMessage();
                        if (message == null) {
                            message = "Unknown Exception.  No musical "
                                      + "information could be imported.";
                        }
                        score = null;
                        return;
                    }
                } else {
                    score = null;
                    return;
                }
            }
        }

        /**
         * Returns the score extracted from the file.
         *
         * @return score read, or null if an error occured
         */
        public Score getScore() {
            return score;
        }

        /**
         * Returns the error message.
         *
         * @return description of error, or null if no error occured
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Returns a Score read from a MIDI or JM file, without displaying error
     * messages.
     *
     * @param file  File to read
     * @return      Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithNoMessaging(String, String)
     */
    public static Score midiOrJmWithNoMessaging(final File file) {
        return new JmMidiProcessor(file).getScore();
    }

    /**
     * Returns a Score read from a MIDI or JM file, without displaying error
     * messages.
     *
     * <P>The path of the file is separated into <CODE>directory</CODE> and
     * <CODE>filename</CODE> so that the latter can be used as the title of the
     * score.  If <CODE>directory</CODE> is null then this method attempts
     * to read the file specified by <CODE>filename</CODE>.
     *
     * @param directory String describing the directory structure of the file to
     *                  be read, which must include the terminating separator
     * @param filename  String describing the file name
     * @return          Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithNoMessaging(File)
     */
    public static Score midiOrJmWithNoMessaging(final String directory,
                                                final String filename) {
        return new JmMidiProcessor(directory, filename).getScore();
    }

    /**
     * Returns a Score read from a MIDI or JM file, displaying errors in a
     * {@link Dialog}.
     *
     * @param file  File to read
     * @param owner Frame whose control is to be suspended while the error
     *              messages are displayed
     * @return      Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithAWTMessaging(String, String, Frame)
     */
    public static Score midiOrJmWithAWTMessaging(final File file,
                                                 final Frame owner) {
        JmMidiProcessor processor = new JmMidiProcessor(file);
        displayErrorDialog(owner, processor.getMessage());
        return processor.getScore();
    }

    /**
     * Returns a Score read from a MIDI or JM file, displaying errors in a
     * {@link Dialog}.
     *
     * <P>The path of the file is separated into <CODE>directory</CODE> and
     * <CODE>filename</CODE> so that the latter can be used as the title of the
     * score.  If <CODE>directory</CODE> is null then this method attempts
     * to read the file specified by <CODE>filename</CODE>.
     *
     * @param directory String describing the directory structure of the file to
     *                  be read, which must include the terminating separator
     * @param filename  String describing the file name
     * @param owner     Frame whose control is to be suspended while the error
     *                  messages are displayed
     * @return          Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithAWTMessaging(File, Frame)
     */
    public static Score midiOrJmWithAWTMessaging(final String directory,
                                                 final String filename,
                                                 final Frame owner) {
        JmMidiProcessor processor = new JmMidiProcessor(directory, filename);
        displayErrorDialog(owner, processor.getMessage());
        return processor.getScore();
    }

    /**
     * Displays an error message in a {@link Dialog}.  The message displayed is
     * retrieved from the class variable {@link #message}.  This method is
     * designed for use by the {@link #midiOrWithAWTMessaging()} methods.
     *
     * <P>This method is for Dialogs whose control is to be taken from a Frame.
     * If the owner is a Dialog use {@link #displayErrorDialog(Dialog)} instead.
     *
     * @param owner     Frame whose control is to be suspended while the error
     *                 messages are displayed
     * @param message   String to be displayed
     *
     * @see displayErrorDialog(Frame)
     * @see #midiOrJmWithAWTMessaging(File, Dialog);
     * @see #midiOrJmWithAWTMessaging(String, String, Dialog);
     */
    private static void displayErrorDialog(final Frame owner,
                                           final String message) {
        if (message == null) {
            return;
        }
        Dialog dialog = new Dialog(owner, "Not a valid MIDI or jMusic File",
                                   true);
        completeErrorDialog(dialog, message);
    }

    /**
     * Adds supplementary components to the error dialog and displays it.  This
     * method is executed by both {@link displayErrorDialog(Frame)} and {@link
     * dispayedErrorDialog(Dialog)} and stores the code common to both.
     *
     * @param dialog    Dialog that is to display the error message.
     * @param message   String to be displayed
     */
    private static void completeErrorDialog(final Dialog dialog,
                                            final String message) {
        dialog.add(new Label(message), BorderLayout.CENTER);

        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            }
        );
        Panel buttonPanel = new Panel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dialog.dispose();
                }
            }
        );
        dialog.pack();
        dialog.show();
    }

    //--------- Simultaneous midi and jm reading with error messaging --------//

    /**
     * Returns a Score read from a MIDI or JM file, displaying errors in a
     * {@link javax.swing.JDialog}.
     *
     * @param file  File to read
     * @param owner Component whose control is to be suspended while the error
     *              messages are displayed
     * @return      Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithSwingMessaging(String, String, Component)
     */
    public static Score midiOrJmWithSwingMessaging(final File file,
                                                   final Component owner) {
        JmMidiProcessor processor = new JmMidiProcessor(file);
        displayErrorJDialog(owner, processor.getMessage());
        return processor.getScore();
    }

    /**
     * Returns a Score read from a MIDI or JM file, displaying errors in a
     * {@link javax.swing.JDialog}.
     *
     * <P>The path of the file is separated into <CODE>directory</CODE> and
     * <CODE>filename</CODE> so that the latter can be used as the title of the
     * score.  If <CODE>directory</CODE> is null then this method attempts
     * to read the file specified by <CODE>filename</CODE>.
     *
     * @param directory String describing the directory structure of the file to
     *                  be read, which must include the terminating separator
     * @param filename  String describing the file name
     * @param owner     Component whose control is to be suspended while the
     *                  error messages are displayed
     * @return          Score read from file, or null if an error occured
     *
     * @see #midiOrJmWithSwingMessaging(File, Component)
     */
    public static Score midiOrJmWithSwingMessaging(final String directory,
                                                   final String filename,
                                                   final Component owner) {
        JmMidiProcessor processor = new JmMidiProcessor(directory, filename);
        displayErrorJDialog(owner, processor.getMessage());
        return processor.getScore();
    }                        

    /**
     * Displays an error message in a {@link javax.swing.JDialog}.  The message displayed is
     * retrieved from the class variable {@link #message}.  This method is
     * designed for use by the {@link #midiOrWithSwingMessaging()} methods.
     *
     * @param owner     Component whose control is to be suspended while the
     *                  error messages are displayed
     * @param message   String to display   
     *
     * @see #midiOrJmWithSwingMessaging(File, Component);
     * @see #midiOrJmWithSwingMessaging(String, String, Component);
     */
    private static void displayErrorJDialog(final Component owner,
                                            final String message) {
        if (message == null) {
            return;
        }
        JOptionPane.showMessageDialog(owner, message,
                                      "Not a valid MIDI or jMusic File",
                                      JOptionPane.ERROR_MESSAGE);
    }
}
                             
