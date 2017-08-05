/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.com;

import imp.Constants;
import imp.ImproVisor;
import imp.data.Score;
import imp.data.Transposition;
import imp.util.ErrorLog;
import java.io.*;
import javax.sound.midi.*;

/**
 * A Command that exports a score to a midi file
 * Author: David Morrison
 */
public class ExportToMidiCommand implements Command, Constants 
{
    // The file to export to
    private File file;
    
    // The score we want to export (contains a midi render that we can parse)
    private Score score;
    
    private int toExport = 0;
    
    // Midi Settings
    private static final int BEGIN_MIDI_HEADER = 0x4D546864;
    private static final int BEGIN_TRACK = 0x4D54726B;
    private static final int MIDI_FORMAT_1 = 1;
    private static final int HEADER_LENGTH = 6;
    private static final short PPQN = 480; //BEAT;
    
    // Midi Messages
    private static final byte[] END_OF_TRACK = {0xffffffff, 0x2f, 0x0};
    private static final byte[] TEMPO = {0xffffffff, 0x51, 0x03, 0, 0, 0};
    private static final byte[] 
            TIME_SIGNATURE = {0xffffffff, 0x58, 0x04, 0x04, 0x02, 0x40, 0x08};

    /**
     * stores error if exception during save
     */
    Exception error = null;
    
    /**
     * false since this Command cannot be undone
     */
    private final boolean undoable = false;
    
    private final Transposition transposition;

    /**
     * Creates a new Command that can save a Score to a File.
     * file      the File to save to
     * score     the Score to save
     */
    public ExportToMidiCommand(File file, Score score, int toExport, Transposition transposition) 
    {
        this.file = file;
        this.score = score;
        this.toExport = toExport;
        this.transposition = transposition;
    }
    
    public Exception getError() {
        return error;
    }

    // Save the Score to the File
    public void execute()
    {
	try
        {
            OutputStream os = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(os);
            
            write(dos, transposition);
            
            os.flush();
            os.close();
            dos.flush();
            dos.close();
	} 
        catch(IOException e)
        {
            error = e;
            ErrorLog.log(ErrorLog.WARNING, "Internal Error: " + e);
	}
    }
    
    // Initialize the sequence and write the header chunk.
    private void write(DataOutputStream dos, Transposition transposition) throws IOException
    {
        Sequence seq = null;
        
        // We want to export the "swung" version, not the straight version
        score = score.copy();
        score.makeSwing();

        // Try to get the render
       try
        {
            seq = score.render(PPQN, transposition);  
            //seq = score.getCachedSequence();
        }
        catch (InvalidMidiDataException e)
        {
            System.out.println("InvalideMidiDataException " + e);
        }
        
        //System.out.println("sequence in Midi = " + seq);
        
        // Get the array of tracks from the render.
        Track[] tracks = seq.getTracks();
        int numOfTracks = tracks.length;

        // Not sure that we currently use any variant but ALL
        //System.out.println("toExport = " + toExport);
        switch (toExport) {
            case ALL:
                break;
            case CHORDS_ONLY:
                numOfTracks = 1;  // We only export one track if we export chords only
                break;
            case MELODY_ONLY:
                 // We don't export the chords track if we export melody only
                numOfTracks = numOfTracks - 1; 
                break;
            default:
                break;
        }
            
        
        // Write the header chunk; this always follows the format
        // <chunk type> <length> <format> <ntrks> <division>
        try
        {
                dos.writeInt(BEGIN_MIDI_HEADER);        // <chunk type> is MThd (midi header chunk)
                dos.writeInt(HEADER_LENGTH);            // <length> is 6 bytes
                dos.writeShort(MIDI_FORMAT_1);          // <format> is 1 (one or more simultaneous tracks)
                dos.writeShort(numOfTracks + 1);        // <ntrks> is the number of tracks + 1 track to hold tempo information
                dos.writeShort(PPQN);                   // <division> is pulses per quarter note.
        }
        catch(Exception e)
        {
                e.printStackTrace();
        }	
                
        // write volume messages to tracks

        // Why are these all added to track 0?
       
        try {
            ShortMessage volMsg = new ShortMessage();
            volMsg.setMessage(ShortMessage.CONTROL_CHANGE, ImproVisor.getBassChannel(), 7, score.getBassMuted()?0:score.getBassVolume());
            tracks[0].add(new MidiEvent(volMsg, 0));
        } catch(InvalidMidiDataException e) {}
        
        try {
            ShortMessage volMsg = new ShortMessage();
            volMsg.setMessage(ShortMessage.CONTROL_CHANGE, ImproVisor.getDrumChannel(), 7, score.getDrumMuted()?0:score.getDrumVolume());
            tracks[0].add(new MidiEvent(volMsg, 0));
        } catch(InvalidMidiDataException e) {}
        
        try {
            ShortMessage volMsg = new ShortMessage();
            volMsg.setMessage(ShortMessage.CONTROL_CHANGE, ImproVisor.getChordChannel(), 7, score.getChordMuted()?0:score.getChordVolume());
            tracks[0].add(new MidiEvent(volMsg, 0));
        } catch(InvalidMidiDataException e) {}
        
        try {
            ShortMessage volMsg = new ShortMessage();
            volMsg.setMessage(ShortMessage.CONTROL_CHANGE, ImproVisor.getMelodyChannel(), 7, score.getMelodyMuted()?0:score.getMelodyVolume());
            tracks[0].add(new MidiEvent(volMsg, 0));
        } catch(InvalidMidiDataException e) {}
        
        // The first track contains tempo and time-signature information.
        writeFirstTrack(dos);
        
        //System.out.println("writing Sequence of " + tracks.length + " tracks ");
        
        // Write all of the data tracks
        for (int i = 0; i < numOfTracks; ++i)
        {
            // The chord track is track 0, so if we're only exporting the chords, then ignore
            // everything after i = 0.
            switch( toExport )
            {
                case CHORDS_ONLY:
                    if( i > 0 )
                      {
                      writeTrack(i, dos, tracks[i]);
                      }
                    break;
                    
                case MELODY_ONLY:
                    if( i == 0 )
                      {
                        writeTrack(i, dos, tracks[i]);
                      }
                    break;
                default:
                    writeTrack(i, dos, tracks[i]);
                    break;
            }
        }
    }
        
    // The first track of the file will contain tempo and time signature information.
    private void writeFirstTrack(DataOutputStream dos) throws IOException
    {
        // First we have to write everything to a buffer stream
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        
        // First we write the time signature
        writeVarLength(0x0, buffer);
	TIME_SIGNATURE[3] = (byte)score.getMetre()[0];
	TIME_SIGNATURE[4] = (byte)(Math.log(score.getMetre()[1])/Math.log(2));
        byte[] timeSig = TIME_SIGNATURE;
        buffer.write(timeSig);
        
        // Next we output the tempo; this is a meta-message, so it takes up zero
        // time in the render.
        writeVarLength(0, buffer);
        
        // Tempo is expressed in terms of microseconds per quarter note.
        int upq = (int)(60000000 / score.getTempo());
        
        // Construct the tempo message and write it to the buffer.
        TEMPO[3] = (byte)(upq >> 16);
        TEMPO[4] = (byte)(upq >> 8);
        TEMPO[5] = (byte)(upq);
        buffer.write(TEMPO);
        
        // Track is done, so we write the end of track message..
        writeVarLength(0, buffer);
        buffer.write(END_OF_TRACK);
        
        // Now we copy everything from the buffer to the actual output stream.
        dos.writeInt(BEGIN_TRACK);
        
        // Length of the buffer.
        dos.writeInt(buffer.size());
        buffer.writeTo(dos);
    }
    
    // Output an individual track.
    private void writeTrack(int num, DataOutputStream dos, Track track) throws IOException
    {       
        // First we need to load everything into a buffer.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        // Write the first message in the track to the buffer.
        writeVarLength(0, buffer);
        MidiMessage firstMsg = track.get(0).getMessage();
        buffer.write(firstMsg.getMessage());

        long size = track.size();

        //System.out.println("writing track " + num + " size " + size);
        
        // Write the remaining messages to the buffer.
        for (int i = 1; i < size; ++i) // why was it size-1???
        {           
            // Events are expressed in the following format:
            // <delta-time> <event>
            // where delta-time is the amount of time that passes until the event
            // occurs.
            //
            // getTick() returns the absolute time of a given event, so to convert
            // this to deltaTime, we simply subtract the absolute time of the previous event
            // from the absolute time of the current event. 
            
            long tick = track.get(i).getTick();
            long prevTick = track.get(i - 1).getTick();
            long deltaTime = tick - prevTick;
            
            // deltaTime is written out as a variable-length value.
            writeVarLength(deltaTime, buffer);            
            
            // Get the event from the track and write it to the buffer.
            MidiMessage msg = track.get(i).getMessage();           
            buffer.write(msg.getMessage());
        }
        
        // Hack -- adds on a little time to the last note in the render so that things don't get
        // truncated in Quicktime.  To use, uncomment the stuff below, and change the for loop above
        // to read "for (int i = 1; i < track.size() - 2; ++i)"
        
/*        if (track.size() > 2)
        {
            long tick = track.get(track.size() - 2).getTick();
            long prevTick = track.get(track.size() - 3).getTick();
            long deltaTime = tick - prevTick + PPQN/2;
        
            writeVarLength(deltaTime, buffer);
        
            MidiMessage msg = track.get(track.size() - 1).getMessage();
            buffer.write(msg.getMessage(), 0, msg.getMessage().length);
        }*/
        
        // Now we end the track.
        
        writeVarLength(0, buffer);
        buffer.write(END_OF_TRACK);
        
        // Now we write everything from the buffer onto the actual output stream.
        dos.writeInt(BEGIN_TRACK);        
        dos.writeInt(buffer.size());
        buffer.writeTo(dos);
    }
    
    // The length of a track event message is stored as a variable length quantity.
    // The following routine will convert a long to a var-length quantity.
    //
    // The 7th bit of the number is always a location bit; so every byte has the
    // seventh bit set, except for the last byte.  The remaining 7 bits are filled
    // in from the number itself.
    //
    // Examples:
    // 7F = 0111 1111 = 7F var-length.
    // 80 = 1000 0000 = 81 00 var-length.
    // 
    // Code adapted from the Midi Documentation at www.omega-art.com/midi/mfiles.html
    
    public static void writeVarLength(long value, ByteArrayOutputStream baos) 
    {
        long buffer = value & 0x7F;
        while((value >>= 7) > 0)
        {
            buffer <<= 8;
            buffer |= ((value & 0x7F) | 0x80);
        }
        
        while(true)
        {
            baos.write((byte) buffer);
            
            if((buffer & 0x80) != 0)
                buffer >>= 8;
            else
                break;
        }    
    }
        

    /**
     * Undo unsupported for SaveLeadsheetCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for ExportToMidi.");
    }

    /**
     * Redo unsupported for SaveLeadsheetCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for ExportToMidi.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}

