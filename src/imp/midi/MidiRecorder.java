/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2019 Robert Keller and Harvey Mudd College
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

/*
 * NOTE: This version of MidiRecorder is based on a reversion to what it was
 * in Version 8.11, rather than a continuation from Version 9.0.
 */
package imp.midi;

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

public class MidiRecorder implements imp.Constants, Receiver
{
    Notate notate;
    MelodyPart melodyPart;
    Score score;
    Sequencer sequencer = null;
    MelodyPart tradePart = null;
    int countInOffset;
    /* insertion is offset from onset detection due to latency */
    int insertionOffset;
    long noteOn = 0, noteOff = 0, lastEvent = 0;
    boolean notePlaying = false;
    int prevNote = 0;
    int resolution;
    double latency = 0;
    boolean isSuspended = false;
    int transposition = 0;
    private javax.swing.JToggleButton midiInputChannel[];
    
 
public MidiRecorder(Notate notate, Score score)
  {
        this.notate = notate;
        this.score = score;
    }

public double getLatency()
  {
        return latency;
    }

public void setLatency(double latency)
  {
        this.latency = latency;
    }

public long getTime()
  {
    if( sequencer != null && sequencer.isRunning() )
      {
      return sequencer.getMicrosecondPosition();
      }
    else
      {
      return -1;
      }
    }

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
      return -1;
       }
    }

/**
 * Start MIDI recording
 * @param countInOffset
 * @param insertionOffset
 * @param transposition 
 */

public void start(int countInOffset, int insertionOffset, int transposition) {
        this.countInOffset = countInOffset;
        this.insertionOffset = insertionOffset;
        this.transposition = transposition;
        //snapTo = notate.getRealtimeQuantizationGCD();
        //System.out.println("start snapTo = " + snapTo + " countInOffset = " + countInOffset + " insertionOffset = " + insertionOffset);
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

        unSuspend(); // make sure we aren't suspended
    }

    int getCountInBias() {
        //System.out.println("firstChorus = " + notate.getFirstChorus());
        return notate.getFirstChorus() ? countInOffset : 0;
    }

    public boolean getSuspended(){
        return isSuspended;
    }
    
    public void suspend(){
        if( !isSuspended )
        {
            isSuspended = true;
            if(notePlaying) {
                lastEvent = getTick();
                handleNoteOff(prevNote, 0, 0);
            }
        }
    }
    
    public void unSuspend(){
        if( isSuspended )
        {
            isSuspended = false;
            lastEvent = noteOff = noteOn = getTick();
        }
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
                note = m[1] + transposition;
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
                note = m[1] + transposition;
                velocity = m[2];

                handleNoteOff(note, velocity, channel);
                break;
        }
        //System.out.println("done with " + MidiFormatting.midiMessage2polylist(message));
    }

    void handleNoteOn(int note, int velocity, int channel) {
        //System.out.print("noteOn: " + note + " channel = " + channel + " ");
        if( !midiInputChannel[channel].isSelected() )
          {
            //System.out.println("skipped");
            return;
          }
        //System.out.println();
        // new note played, so finish up previous notes or insert rests up to the current note
        int index;
        if (notePlaying) {
            handleNoteOff(prevNote, velocity, channel);

        } else {
            int duration = notate.snapSlotsToDuration(tickToSlots(noteOff, lastEvent), getSelectedQuantumIndex(), quantum);

            // this try is here because a function a few steps up in the call hierarchy tends to capture error messages
            try {
                index = notate.snapSlotsToIndex(tickToSlots(noteOff), getSelectedQuantumIndex(), quantum);

                // add rests since nothing was played between now and the previous note
                if (duration > 0 && index >= 0) {
                    Note noteToAdd = new Rest(duration);
                    setNote(index, noteToAdd);
                }

//This is disastrous for improvisation because it messes up the selection.
//            if( index >= 0 )
//              {
//                //notate.setCurrentSelectionStartAndEnd(index);
//              }
            } catch (Exception e) {
                //ErrorLog.log(ErrorLog.SEVERE, "Internal exception in MidiRecorder: " + e);
            }
        }

        noteOn = lastEvent;
        index = notate.snapSlotsToIndex(tickToSlots(noteOn), getSelectedQuantumIndex(), quantum);

        // add current note   MAYBE RIGHT HEREEREREREREREER
        int duration = notate.snapSlotsToDuration(tickToSlots(noteOn, noteOff), getSelectedQuantumIndex(), quantum);
        Note noteToAdd = new Note(note, duration);

        try {
            noteToAdd.setEnharmonic(score.getCurrentEnharmonics(index));
            setNote(index, noteToAdd);
        } catch (Exception e) {
            System.out.println("Internal exception in MidiRecorder: " + e);
        }

        notate.repaint();

        prevNote = note;
        notePlaying = true;
    }
    
    /**
     * Setter for the instance variable tradePart;
     * this was added while implementing interactive trading functionality;
     * it allows one to record midi into some specified melody; 
     * when instance variable 'tradePart' is null, midi will be 
     * recorded into the current melodyPart of 'notate' - Zach Kondak.
     * 
     * Maybe this design should be revisited. - Bob Keller 5/20/2016
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
        if (tradePart == null) {
            this.melodyPart = notate.getCurrentMelodyPart();
        }
        else {
            this.melodyPart = tradePart;
        }

      //melodyPart.setNoteAndLength(index, noteToAdd, notate);
      // Avoid using notate, if possible.
        // However the version above does not shorten notes on release,
        // but rather only when a next note is pressed. We'd need to mark
        // the first place after the generator has played notes.
        // FIX: Revisit this issue after more refactoring.
        
        int actualIndex = (index% melodyPart.size()) - insertionOffset;
        if( actualIndex < 0 )
          {
            actualIndex = 0;
          }
        melodyPart.setNote(actualIndex, noteToAdd);
    }

    void handleNoteOff(int note, int velocity, int channel) {
        //System.out.print("noteOff: " + note + " channel = " + channel + " ");
        if( !midiInputChannel[channel].isSelected() )
          {
            //System.out.println("skipped");
            return;
          }
        //System.out.println();
        //System.out.println("noteOff: " + noteOff + "; event: " + lastEvent);

        if (note != prevNote) {
            return;
        }

        // use the one in constructor: Notate notate = imp.ImproVisor.getCurrentWindow();
        noteOff = lastEvent;
        notePlaying = false;

        int index = notate.snapSlotsToIndex(tickToSlots(noteOn), getSelectedQuantumIndex(), quantum);

        if (index < 0) {
            return;
        }

        int duration = notate.snapSlotsToDuration(tickToSlots(noteOn, noteOff), getSelectedQuantumIndex(), quantum);

        if (duration == 0) {
        } else {
            Note noteToAdd = new Note(note, duration);
            noteToAdd.setEnharmonic(score.getCurrentEnharmonics(index));
            setNote(index, noteToAdd);
        //System.out.println("at " + index + " add " + noteToAdd);
        }

        index += duration;

    //Does this mess up improvisation?
    //notate.setCurrentSelectionStartAndEnd(index);
    // System.out.println("duration: " + duration + "; corrected: " + ((double) slots) / BEAT);
        notate.repaint();
    }

    int roundToMultiple(int input, int base) {
        return base * (int) Math.round(((double) input) / base);
    }

    int floorToMultiple(int input, int base) {
        return base * (int) Math.floor(((double) input) / base);
    }
    int microsecondsToSlots(long start, long finish) {
        return microsecondsToSlots(finish - start);
    }

    int microsecondsToSlots(long duration) {
        double tempo = score.getTempo();
        return (int) (duration / 1000000.0 * (tempo / 60) * BEAT);
    }

    int tickToSlots(long start, long finish) {
        return tickToSlots(finish - start);
    }

    int tickToSlots(long duration) {
        return (int) (BEAT * duration / resolution);
    }

    boolean multipleOf(int value, int divisor)
    {
    return (value % divisor) == 0;  
    }
    
    static int quantum[] = {20, 30, 40, 60, 120, 180, 240, 360, 480};
    
    static String quantumString[] =             
      {
          "sixteenth note triplet",
          "sixteenth note",
          "eighth note triplet",
          "eighth note",
          "quarternote ",
          "dotted quarter note",
          "half note",
          "dotted half note",
          "whole note"          
      };
    
    static String intialQuantumString = "eighth note";
    
    public static String[] getQuantumString()
    {
        return quantumString;
    }
    
    public static String getInitialQuantumString()
    {
        return intialQuantumString;
    }
    
    int getSelectedQuantumIndex()
    {
        return notate.getRealtimeQuantizationIndex(notate.getRealtimeQuantizationString(), quantumString);
    }
    
    public void close() {
    }
    
    public void setMidiInputChannel(javax.swing.JToggleButton[] midiInputChannel)
    {
        this.midiInputChannel = midiInputChannel;
    }
}
