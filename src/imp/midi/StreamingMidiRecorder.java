/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.midi;

import static imp.Constants.BEAT;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.midi.MidiMessage;

/**
 *
 * @author cssummer16
 */
public class StreamingMidiRecorder extends MidiRecorder {

    private MidiStreamHat streamHat;

    public StreamingMidiRecorder(Notate notate, Score score) {
        super(notate, score);
        streamHat = new MidiStreamHat();
    }

    /**
     * This function is called by others to send a MIDI message to this object
     * for processing.
     */
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println("midiRecorder received " + MidiFormatting.midiMessage2polylist(message));
        if (isSuspended) {
            return; // Don't process this message since we are suspended
        }
        byte[] m = message.getMessage();
        int note, channel, velocity;
        int highNibble = (m[0] & 0xF0) >> 4;
        int lowNibble = m[0] & 0x0F;

        switch (highNibble) {
            case 9: // note on
                lastEvent = getTick();

                if (lastEvent < 0) {
                    break;
                }

                channel = lowNibble;
                note = m[1];
                velocity = m[2];
                if ((velocity == 0 || note < this.notate.getLow() || note > this.notate.
                        getHigh()) && this.notate.getFilter()) {
                    // this is actually a note-off event, done to allow 
                    // 'running status': 
                    // http://www.borg.com/~jglatt/tech/midispec/run.htm
                    streamHat.noteOff(note, tickToSlots(lastEvent));
                    handleNoteOff(note, velocity, channel);
                } else {
//              System.out.println("Note: " + note + "; Velocity: " + velocity + "; Channel: " + channel);
                    streamHat.noteOn(note, snapStart(tickToSlots(lastEvent)));
                    handleNoteOn(note, velocity, channel);
                }
                break;

            case 8: // note off
                lastEvent = getTick();
                if (lastEvent < 0) {
                    break;
                }

                channel = lowNibble;
                note = m[1];
                velocity = m[2];
                streamHat.noteOff(note, tickToSlots(lastEvent));
                handleNoteOff(note, velocity, channel);
                break;
        }
        //System.out.println("done with " + MidiFormatting.midiMessage2polylist(message));
    }

    public void start(int countInOffset, int recordLatency) {

        notateMelodyPart = notate.getCurrentMelodyPart();
        quantum = notate.getQuantizationQuanta();
        gcd = MelodyPart.gcd(quantum[0], quantum[1]);
        swingEighths = notate.getQuantizationSwing();
        restAbsorption = notate.getQuantizationRestAbsorption();

        //System.out.println("realtime quanta = " + quantum[0] + "," + quantum[1] + ", gcd = " + gcd + ", restAbsorption = " + restAbsorption + ", swing = " + swingEighths);
        notesLost = 0;
        swingConversions = 0;

        this.sequencer = notate.getSequencer();
        if (sequencer == null || sequencer.getSequence() == null) {
            return;
        }
        resolution = sequencer.getSequence().getResolution();

        while ((noteOn = getTick()) < 0) {
        }

        noteOff = noteOn = getTick();
        streamHat.setStart(tickToSlots(getTick()));
        notePlaying = false;

        // Without the next statement, entered notes are offset by the amount
        // of countin.
        //notate.setCurrentSelectionStartAndEnd(0);
        this.recordLatency = recordLatency;
        this.countInOffset = countInOffset;

        unSuspend(); // make sure we aren't suspended
    }
    
    public BlockingQueue<Note> getMelodyQueue() {
        return streamHat.getMelodyQueue();
    }

    public void flushStream() {
        streamHat.flush(tickToSlots(getTick()) - getCountInBias());
    }
}
