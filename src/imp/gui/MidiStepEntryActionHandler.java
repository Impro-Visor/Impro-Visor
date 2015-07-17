/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.gui;

import imp.data.*;
import imp.com.*;
import javax.sound.midi.*;

import imp.Constants;
/**
 *
 * @author Martin
 */
public class MidiStepEntryActionHandler implements Constants, Receiver {
    Notate notate;
    
    /** Creates a new instance of MidiStepEntryActionHandler */
    public MidiStepEntryActionHandler(Notate notate) {
        this.notate = notate;
    }
    
    /**
     * This function is called to send a MIDI message to this object for processing
     */
    public void send(MidiMessage message, long timeStamp) {
        
        byte[] m = message.getMessage();
        int note, channel, velocity;
        int highNibble = (m[0] & 0xF0) >> 4;
        int lowNibble = m[0] & 0x0F;

        switch(highNibble) {
            case 9: // note on                
                channel = lowNibble;
                note = m[1];
                velocity = m[2];
                if((velocity == 0  ||note<this.notate.getLow() ||note>this.notate.getHigh()) && this.notate.getFilter()) { // this is actually a note-off event, done to allow 'running status': http://www.borg.com/~jglatt/tech/midispec/run.htm
                    handleNoteOff(note, velocity, channel);
                } else 
                    handleNoteOn(note);
                break;
            case 8: // note off
                channel = lowNibble;
                note = m[1];
                velocity = m[2];
                handleNoteOff(note, velocity, channel);
                break;
        }
    }
    
    void handleNoteOn(int note) {
        
        VoicingKeyboard voicingKeyboard = notate.getCurrentVoicingKeyboard();
        
        if (voicingKeyboard != null && voicingKeyboard.isVisible())
        {
            voicingKeyboard.setKeyboard(voicingKeyboard.FROM_MIDI_KEYBOARD, note);
        }
             
        else
        {
            int index = notate.getCurrentSelectionStart();
            Stave stave = notate.getCurrentStave();
            Note newNote = new Note(note);
            newNote.setEnharmonic(notate.getScore().getCurrentEnharmonics(index));

            notate.cm.execute(new SetNoteCommand(index, newNote, notate.getCurrentMelodyPart()));
            int next = stave.getNextCstrLine(index);
        
            if(next > 0)
                stave.setSelection(next, next);
        
            notate.getCurrentStave().repaint();
        }
    }
    
    void handleNoteOff(int note, int velocity, int channel) {
        // do nothing
    }

    public void close() {
    }
}
