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

import java.io.*;
import javax.sound.midi.*;


// This class prints quite a few diagnostics.  
// This is because the true nature of Midi devices
// is a deep mystery. 

public class JmMidiPlayer extends OutputStream {

    private Sequencer   sequencer;
    private Transmitter transmitter;
    private Receiver    receiver;
    private MidiDevice  synthesizer;

    ByteArrayOutputStream os;
    
    private static MidiDevice getSynthesizer() 
            throws  MidiUnavailableException {

        MidiDevice.Info[]   devsInfo 
            = MidiSystem.getMidiDeviceInfo();
        MidiDevice.Info     devInfo;
        MidiDevice          dev;
        dev = null;
        boolean             success;
        success = false;
        for( int i = 0; i < devsInfo.length; ++i) {
            if ( !success ) {
                try {
                    devInfo = devsInfo[i-1];            
                    System.out.print(devInfo.toString());
                    System.out.print( " Getting Info ");  
                    dev = MidiSystem.getMidiDevice(devInfo);
                    if (!(dev instanceof Sequencer)) {
                        System.out.print( " Opening ");  
                        success = true;       
                        System.out.println( " Opened");  
                    }                        
                    else {
                        System.out.println( 
                            " Not a Sequencer");  
                    }                            
                }   
                catch(Throwable e) {
                    System.out.println( 
                        " Exception " + e.getMessage() );  
                }                    
            }                                    
        }
        if (success) {
            return dev;
        }
        else {
            System.out.println( 
                "No Synthesizer Device Found" );
            throw new MidiUnavailableException(
                "No Synthesizer Device Found" );
        }                        
    }        

    
	public JmMidiPlayer()  throws MidiUnavailableException {

        //System.out.println("Getting Sequencer");        
        sequencer   = MidiSystem.getSequencer();
        //System.out.println("Getting Synthesizer");        
        synthesizer = JmMidiPlayer.getSynthesizer();
        //System.out.println("Getting Transmitter");        
        transmitter = sequencer.getTransmitter();
        //System.out.println("Getting Receiver");        
        receiver    = synthesizer.getReceiver(); 
        //System.out.println("Connecting Receiver");        
        transmitter.setReceiver(receiver);      
        sequencer.open();
        if (sequencer.isOpen()) {
            //System.out.println("Sequencer is Open" );
        }
        else {
            //System.out.println("Sequencer is Not Open" );
        }                                    
        os = new ByteArrayOutputStream();
        //System.out.println("End of Midi Construction");        
        
        MidiDevice.Info synthInfo = synthesizer.getDeviceInfo();
        //System.out.println(
         //       "Synthesizer =" +  synthInfo.toString() 
        //);    
        
        MidiDevice.Info seqInfo   = sequencer.getDeviceInfo();
        //System.out.println( 
               // "Sequencer =" + seqInfo.toString() 
       // );    
        
        MidiDevice.Info[]   devsInfo 
            = MidiSystem.getMidiDeviceInfo();
        MidiDevice.Info     devInfo;
        MidiDevice          dev;
        for( int i=0; i <  devsInfo.length; ++i) {
            devInfo = devsInfo[i];            
            System.out.print(devInfo.toString());  
            try {
                dev = MidiSystem.getMidiDevice(devInfo);
                //System.out.println( " Available");  
            }
            catch(MidiUnavailableException e) {
                System.out.println( " Unavailable");  
            }                                    
        }            
        if (sequencer.isOpen()) {
            //System.out.println("Sequencer is Still Open" );
        }
        else {
           // System.out.println("Sequencer is Not Open" );
        }                                    
    }
	
	public void write(int b) throws IOException {
		byte[] bytes = new byte[1];
		bytes[0] = (new Integer(b)).byteValue();
		write( bytes );				
	}		
	
	public void write(byte[] b) throws IOException {
	    os.write( b );   
    }   
    
    public void play() {
        try {       
            ByteArrayInputStream is 
               = new ByteArrayInputStream(os.toByteArray());
            //System.out.println("Creating Sequence");        
            Sequence midiSeq = MidiSystem.getSequence(is);
            //System.out.println("Setting Sequence");        
            if (sequencer.isOpen()) {
                //System.out.println("Sequencer is Still Open" );
            }
            else {
                //System.out.println("Sequencer is Not Open" );
                sequencer.open();
                //System.out.println("Re-Opened" );
            }                                    
            sequencer.setSequence(midiSeq);                                  
            //System.out.println("Starting Sequencer");        
            sequencer.start();      
            while(sequencer.isRunning()) {
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }                       
            //System.out.println("Stopping Sequencer");        
            sequencer.stop();      
            //System.out.println("Sequencer Stopped");        
        }
        catch ( InvalidMidiDataException e) {
            System.out.println(
                    "Bad Midi Data " + e.getMessage());
        }           
        catch ( MidiUnavailableException e) {
            System.out.println(
              "Unable to Re-Open Sequencer " + 
               e.getMessage());
        }           
        catch ( IOException e) {
            System.out.println(
                "IO Exception in Midi " + e.getMessage());
        }           
        //System.out.println("End of Play");        
    }        

	public void close() {
		sequencer.close();
	}	
	
}
