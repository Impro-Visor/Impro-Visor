/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

package imp.midi;

import imp.Constants;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;

/**
 *
 * @author Martin Hunt. Robert Keller added countInOffset stuff 7/12/2010
 */

public class MidiRecorder implements Constants, Receiver
{
    Notate notate;
    MelodyPart melodyPart;
    MelodyPart notateMelodyPart;
    Score score;
    Sequencer sequencer = null;
    MelodyPart tradePart = null;
    int countInOffset;
    /* insertion is offset from onset detection due to latency */
    int recordLatency;
    long noteOn = 0, noteOff = 0, lastEvent = 0;
    boolean notePlaying = false;
    int prevNote = 0;
    int resolution;
    int snapTo;
    double latency = 0;
    
    int noteOnIndex;
    
    int quantum[] = {60, 40};
    int gcd = 20;
    
 
public MidiRecorder(Notate notate, Score score)
  {
        this.notate = notate;
        this.score = score;
        notateMelodyPart = notate.getCurrentMelodyPart();
    }

public double getLatency()
  {
        return latency;
    }

public void setLatency(double latency)
  {
        this.latency = latency;
    }

//public long getTime()
//  {
//    if( sequencer != null && sequencer.isRunning() )
//      {
//            return sequencer.getMicrosecondPosition();
//      }
//    else
//      {
//            //notate.stopRecording();
//            return -1;
//        }
//    }

public long getTick()
  {
    if( sequencer != null && sequencer.isRunning() )
      {
            double bpms = ((double) sequencer.getTempoInBPM()) / 60000;    // beats per millisecond
            long latencyTicks = Math.round(bpms * resolution * latency);

            return sequencer.getTickPosition() - latencyTicks;
      }
    else
      {
            //notate.stopRecording();
            return -1;
        }
    }

public void start(int countInOffset, int recordLatency) {
        snapTo = quantum[0]; // snapTo = BEAT / notate.getRecordSnapValue();
        this.sequencer = notate.getSequencer();
        if (sequencer == null || sequencer.getSequence() == null) {
            return;
        }
        resolution = sequencer.getSequence().getResolution();
        
         while ((noteOn = getTick()) < 0) {
        }

        noteOff = noteOn = getTick();
        notePlaying = false;

    // Without the next statement, entered notes are offset by the amount
        // of countin.
        //notate.setCurrentSelectionStartAndEnd(0);

       this.recordLatency = recordLatency;
       this.countInOffset = countInOffset;
    }

    int getCountInBias() {
        //System.out.println("firstChorus = " + notate.getFirstChorus());
        return notate.getFirstChorus() ? countInOffset : 0;
    }

    /**
     * This function is called by others to send a MIDI message to this object
     * for processing.
     */
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println("midiRecorder received " + MidiFormatting.midiMessage2polylist(message));

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
                if ((velocity == 0 || note < this.notate.getLow() || note > this.notate.getHigh()) && this.notate.getFilter()) {
                // this is actually a note-off event, done to allow 
                    // 'running status': 
                    // http://www.borg.com/~jglatt/tech/midispec/run.htm

                    handleNoteOff(note, velocity, channel);
                } else {
//              System.out.println("Note: " + note + "; Velocity: " + velocity + "; Channel: " + channel);
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

                handleNoteOff(note, velocity, channel);
                break;
        }
        //System.out.println("done with " + MidiFormatting.midiMessage2polylist(message));
    }

    void handleNoteOn(int note, int velocity, int channel) {
    //System.out.println("noteOn: " + noteOn + "; noteOff: " + noteOff + "; event: " + lastEvent);
        // new note played, so finish up previous notes or insert rests up to the current note
        int index;
        if (notePlaying) {
            handleNoteOff(prevNote, velocity, channel);

        } else {
            int duration = snapDuration(tickToSlots(lastEvent - noteOff));

            // this try is here because a function a few steps up in the call hierarchy tends to capture error messages
            try {
                index = snapStart(tickToSlots(noteOff)) - getCountInBias();

                // add rests since nothing was played between now and the previous note
                if (duration > 0 && index >= 0) {
                    Note noteToAdd = new Rest(duration);
                    setNote(index, noteToAdd);
                }

            } catch (Exception e) {
                //ErrorLog.log(ErrorLog.SEVERE, "Internal exception in MidiRecorder: " + e);
            }
        }

        noteOn = lastEvent;
        noteOnIndex = snapStart(tickToSlots(noteOn)) - getCountInBias();

        // add current note   MAYBE RIGHT HEREEREREREREREER
        Note noteToAdd = new Note(note, snapTo);

        try {
            setNote(noteOnIndex, noteToAdd);
        } catch (Exception e) {
            System.out.println("Internal exception in MidiRecorder: " + e);
        }

        prevNote = note;
        notePlaying = true;
        
        notate.repaint();
    }

    void handleNoteOff(int note, int velocity, int channel) {
        //System.out.println("noteOff: " + noteOff + "; event: " + lastEvent);

        if (note != prevNote) {
            return;
        }

        // use the one in constructor: Notate notate = imp.ImproVisor.getCurrentWindow();
        noteOff = lastEvent;
        notePlaying = false;

        if (noteOnIndex < 0) {
            return;
        }

        int duration = snapDuration(tickToSlots(noteOff - noteOn));

        if (duration != 0)
          {
            Note noteToAdd = new Note(note, duration);
            setNote(noteOnIndex, noteToAdd);
            noteOnIndex += duration;
          }

        notate.repaint();
    }

    
    /**
     * Setter for the instance variable tradePart;
     * this was added while implementing interactive trading functionality;
     * it allows one to record midi into some specified melody; 
     * when instance variable 'tradePart' is null, midi will be 
     * recorded into the current melodyPart of 'notate' - Zach Kondak.
     * 
     * @param destination melody part into which midi is actually recorded
     */
    public void setDestination(MelodyPart destination){
        this.tradePart = destination;
        //System.out.println("MidiRecorder:setDestination to " + destination);
    }

    /**
     * The index could be anywhere within the tune now, so taking mod relative
     * to part size is needed.
     *
     * @param index
     * @param noteToAdd
     */
    private void setNote(int index, Note noteToAdd) //THIS COULD BE It
    {
        noteToAdd.setEnharmonic(score.getCurrentEnharmonics(index));

        if (tradePart == null) {
            melodyPart = notateMelodyPart;
        }
        else {
            melodyPart = tradePart;
        }
        
        int actualIndex = (index%melodyPart.size()) - recordLatency;
        if( actualIndex < 0 )
          {
            actualIndex = 0;
          }
        melodyPart.setNote(actualIndex, noteToAdd);
    }

//    int microsecondsToSlots(long start, long finish) {
//        return microsecondsToSlots(finish - start);
//    }

//    int microsecondsToSlots(long duration) {
//        double tempo = score.getTempo();
//        return (int) (duration / 1000000.0 * (tempo / 60) * BEAT);
//    }

//    int tickToSlots(long start, long finish) {
//        return tickToSlots(finish - start);
//    }

    int tickToSlots(long duration) {
        return (int) (BEAT * duration / resolution);
    }

    int snapDuration(int slots) {
        slots = snapToMultiple(slots, snapTo);
        return slots;
    }
    
    int snapStart(int slot) {
        slot = snapToMultiple(slot, snapTo);
        return slot;
    }

    
    int snapToMultiple(int input, int base) {
        return base * (int) Math.round(((double) input) / base);
    }


    public void close() {
    }
}
