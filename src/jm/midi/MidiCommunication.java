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
package jm.midi;

import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
* This class provides an easy interface to the MIDI input and output facilities
* in JavaSound. Classes that extend this abstract class will need to implement
* the handleMidiInput() method to respond to MIDI data from external sources.
* MIDI data can be sent using the senMidiOutput() method. The default constructor
* will prompt the user (via  a GUI interface) to choose the MIDI ports to use. To
* prevent this from hapenning pass the boolean false as an argument to the 
* constructor using the super call from th extending class. A third constructor
* can be used if you know the ports you want to use, it accepts the input and 
* output port IDs as arguments.
* @author Andrew Brown
* @version 1.0, August 2004
*/

public abstract class MidiCommunication implements Receiver {
    private Receiver midiReceiver;
    private boolean waitingToSetup = true;
    
    /**
    * A constructor that automatically prompts for the MIDI i/o ports.
     */
    public MidiCommunication() {
        setupMidiInput();
        while(waitingToSetup) {
            try{
                Thread.sleep(100);
            } catch(Exception e) {}
        }
    }
    
    /**
     * A constructor that supresses the automatic prompting of ports,
     * allowing the programmer to call midiSetup when it suits them.
     * Warning: Attempting to send a MIDI message before setting up ports will
     * cause a NullPointerException.
     */
    public MidiCommunication(boolean prompt) {
        if(prompt) {
            setupMidiInput();
            while(waitingToSetup) {
                try{
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
        }
    }
    
    /**
    * A constructor that automatic assigns MIDI input and output to the ports
     * you define. Use this as a convenient way of setting up MIDI IO if your 
     * application is running on a hardware setup that never (or rarely) changes.
     * Warning: Attempting to assigning unavailible devicePortID's will cause 
     * a NullPointerException.
     * @param inputDeviceID The MIDI port number to use for input to Java.
     * @param outputDeviceID The MIDI port number to use for output from Java.
     */
    public MidiCommunication(int inputDeviceID, int outputDeviceID) {
        final MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        setMidiInputSelection(inputDeviceID, info);
        setMidiOutputSelection(outputDeviceID, info);
    }
    
    /**
    * Choose the MIDI ports to use. This method will prompt you with a list of
     * the availible MIDI inputs and outputs, double click on the desired ports.
     */
    public void midiSetup() {
        setupMidiInput();
    }
    
    /**
     * Handle external MIDI events sent to this computer. This method should be
     * overridden in the class that implements this interface, and the messages
     * handled as appropriate.
     * @param status The MIDI status type. 11 = Controller, ? = Note on, etc.
     * @param channel The MIDi channel this message is sent on.
     * @param data1 The first byte of data in the message. Often controller number of Note pitch.
     * @param data2 The second byte of data. Usually the controller value or Note velocity.
     */
    public abstract void handleMidiInput(int status, int channel, int data1, int data2);
    
    /**
     * Construct a MIDI message and pass to the designated MIDi output.
     * @param status The MIDI status type. 11 = Controller, 144 = Note on, 128 = Note Off, 192 = Program Change, etc.
     * @param channel The MIDi channel this message is sent on.
     * @param data1 The first byte of data in the message. Often controller number of Note pitch.
     * @param data2 The second byte of data. Usually the controller value or Note velocity.
     */
    public void sendMidiOutput(int status, int channel, int data1, int data2) {
        try {
            ShortMessage message = new ShortMessage ();
            message.setMessage(status, channel, data1, data2);
            this.midiReceiver.send(message, -1L);
        } catch (javax.sound.midi.InvalidMidiDataException e) {};
    }
    
    /**
     * The method required by the receive interface - It has a stupid name so we
     * pass data for short messages to handleMidiInput (a better name).
     */
    // This method is based on test code in Plumbstone - OSX Core MIDI package.
    public void send(MidiMessage message, long timeStamp) {
        // Get the message bytes
        byte [] m = message.getMessage ();
        int status = message.getStatus();
        // Is it a short message ?
        if (message instanceof ShortMessage) {
            // Get command and channel data
            int type = (m [0] & 0xFF) >> 4;
            int channel = m [0] & 0xF;
            int data1 = m[1];
            int data2 = -1;
            if (m.length > 2) {
                data2 = m[2];
            }
            
            // Is it a channel message?
            if (type != 15) {
                handleMidiInput(status - channel, channel, data1, data2);
            }
            else {
                // System message - some need special handling due to vast quanitity
                if (status == ShortMessage.TIMING_CLOCK) {
                    System.out.print ("MIDI Clock message");
                }
                else if (status == ShortMessage.ACTIVE_SENSING) {
                    System.out.print ("MIDI Active sensing message");
                }
                else {
                    // Some other system message - just print status byte
                    System.out.print ("A non-identified MIDI system message " + status);
                }
            }
        }
        
        // System exclusive message?
        else if (message instanceof SysexMessage) {
            // Print out sysex message
            System.out.println ();
            System.out.print ("Sysex MIDI message <<");
            for (int i=0; i<m.length; i++) {
                System.out.print (" " + m [i]);
            }
            System.out.println (">>");
        }
        
        // Meta message (probably shouldn't ever get this)
        else if (message instanceof MetaMessage) {
            // Print out meta message
            System.out.println ();
            System.out.print ("Meta MIDI Message {");
            for (int i=0; i<m.length; i++) {
                System.out.print (" " + m [i]);
            }
            System.out.println ("}");
        }
        
        // Uncast message
        else {
            System.out.println ("Unknown MIDI message [");
            for (int i=0; i<m.length; i++) {
                System.out.print (" " + m [i]);
            }
            System.out.println ("]");
        }
    }         
    
    public void close() {}
    
    //--------------------------------------
    // Private methods
    //--------------------------------------
    
    private void setupMidiInput() {
        try { 
            // display MIDI device picker
            final Frame f = new Frame("MIDI Input port: Double-click to select.");
            // See all the midi devices on the system
            final MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
            final List dataList = new List();
            fillFrame(f, dataList, info);
            f.setVisible(true);
            // hanlde double clicking
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = dataList.getSelectedIndex();
                        setMidiInputSelection(index, info);
                        f.setVisible(false);
                        setupMidiOutput();
                    }
                }
            };
            dataList.addMouseListener(mouseListener);
            
        } catch (Exception e) {
            System.out.println (e);
            System.exit (0);
        }
    }
    
    private void setupMidiOutput() {
        try {
            // See all the midi devices on the system
            final MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
            // display MIDI device picker
            final Frame f = new Frame("MIDI Output port: Double-click to select.");
            final List dataList = new List();
            fillFrame(f, dataList, info);
            // hanlde double clicking
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = dataList.getSelectedIndex();
                        setMidiOutputSelection(index, info);
                        f.setVisible(false);
                        waitingToSetup = false;
                    }
                }
            };
            dataList.addMouseListener(mouseListener);
            f.setVisible(true);
        } catch (Exception e) {
            System.out.println (e);
            System.exit (0);
        }
    }

    // GUI prompt
    private void fillFrame(Frame f, List dataList, MidiDevice.Info[] info) {
        try {
            f.setSize(340, 200);
            f.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 170, 
                          Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 100);
            String[] data = new String[info.length];
            data[0] = "" + info[0];
            data[1] = "" + info[1];
            for(int i=2; i< info.length; i++) {
                data[i] = MidiSystem.getMidiDevice(info[i]).toString();
            }
            for(int i=0; i< info.length; i++) {
                dataList.add(data[i]);
            }
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.add(dataList);
            f.add(scrollPane);
        } catch (Exception e) {
            System.out.println (e);
            System.exit (0);
        }
        
    }    
    
    
    /*
     * Set up the MIDI input
     */
    private void setMidiInputSelection(int inputDeviceID, MidiDevice.Info[] info) {
        try {
            // inut setup
            MidiDevice inputPort = MidiSystem.getMidiDevice (info [inputDeviceID]);
            //System.out.println (inputPort);
            inputPort.open ();
            Transmitter t = inputPort.getTransmitter ();
            t.setReceiver(this);
        } catch (Exception e) {
            // Oops! Should never get here
            System.out.println ("Exception in PlumStone main ()");
            System.out.println (e);
            System.exit (0);
        }
    }
    
    /*
     * Set up the MIDI output
     */
    private void setMidiOutputSelection(int outputDeviceID, MidiDevice.Info[] info) {
        try {
            // output setup
            MidiDevice outputPort = MidiSystem.getMidiDevice(info [outputDeviceID]);
            outputPort.open();
            this.midiReceiver = outputPort.getReceiver();
            //System.out.println("setMR = " + midiReceiver);
        } catch (Exception e) {
            // Oops! Should never get here
            System.out.println ("Exception in PlumStone main ()");
            System.out.println (e);
            System.exit (0);
        }
    }
}