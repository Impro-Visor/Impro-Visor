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
    static final int DIVISOR = 60000;
    Notate notate;
    MelodyPart melodyPart;
    MelodyPart notateMelodyPart;
    Score score;
    Sequencer sequencer = null;
    MelodyPart tradePart = null;
    int countInOffset;
    /*
     * insertion is offset from onset detection due to latency
     */
    int recordLatency;
    long noteOn = 0, noteOff = 0, lastEvent = 0;
    boolean notePlaying = false;
    int prevNote = 0;
    int resolution;
    double latency = 0;

    int noteOnIndex;

    int quantum[];
    int gcd;
    boolean swingEighths;
    int restAbsorption;
    
    Note lastNoteAdded = null;
    Note lastNoteAdded2 = null;
    int lastIndex = -1;
    int lastIndex2 = -1;
    
    int notesLost;
    int swingConversions;

    public MidiRecorder(Notate notate, Score score)
    {
        this.notate = notate;
        this.score = score;
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
    public void setDestination(MelodyPart destination)
    {
        this.tradePart = destination;
        //System.out.println("MidiRecorder:setDestination to " + destination);
    }

    public double getLatency()
    {
        return latency;
    }

    public void setLatency(double latency)
    {
        this.latency = latency;
    }

    public void start(int countInOffset, int recordLatency)
    {
        
        notateMelodyPart = notate.getCurrentMelodyPart();
        quantum = notate.getRealtimeQuanta();
        gcd = MelodyPart.gcd(quantum[0], quantum[1]);
        swingEighths = notate.getRealtimeSwing();
        restAbsorption = notate.getRealtimeRestAbsorption();
        
        System.out.println("realtime quanta = " + quantum[0] + "," + quantum[1] + ", gcd = " + gcd + ", restAbsorption = " + restAbsorption + ", swing = " + swingEighths);
        
        notesLost = 0;
        swingConversions = 0;
        
        this.sequencer = notate.getSequencer();
        if( sequencer == null || sequencer.getSequence() == null )
          {
            return;
          }
        resolution = sequencer.getSequence().getResolution();

        while( (noteOn = getTick()) < 0 )
          {
          }

        noteOff = noteOn = getTick();
        notePlaying = false;

        // Without the next statement, entered notes are offset by the amount
        // of countin.
        //notate.setCurrentSelectionStartAndEnd(0);
        this.recordLatency = recordLatency;
        this.countInOffset = countInOffset;
    }

    int getCountInBias()
    {
        //System.out.println("firstChorus = " + notate.getFirstChorus());
        return notate.getFirstChorus() ? countInOffset : 0;
    }

    /**
     * This function is called by others to send a MIDI message to this object
     * for processing.
     */
    public void send(MidiMessage message, long timeStamp)
    {
        //System.out.println("midiRecorder received " + MidiFormatting.midiMessage2polylist(message));

        byte[] m = message.getMessage();
        int note, channel, velocity;
        int highNibble = (m[0] & 0xF0) >> 4;
        int lowNibble = m[0] & 0x0F;

        switch( highNibble )
          {
            case 9: // note on
                lastEvent = getTick();
                if( lastEvent < 0 )
                  {
                    break;
                  }

                channel = lowNibble;
                note = m[1];
                velocity = m[2];
                if( (velocity == 0 || note < this.notate.getLow() || note > this.notate.
                        getHigh()) && this.notate.getFilter() )
                  {
                    // this is actually a note-off event, done to allow 
                    // 'running status': 
                    // http://www.borg.com/~jglatt/tech/midispec/run.htm

                    handleNoteOff(note, velocity, channel);
                  }
                else
                  {
//              System.out.println("Note: " + note + "; Velocity: " + velocity + "; Channel: " + channel);
                    handleNoteOn(note, velocity, channel);
                  }
                break;

            case 8: // note off
                lastEvent = getTick();
                if( lastEvent < 0 )
                  {
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

    void handleNoteOn(int note, int velocity, int channel)
    {
        //System.out.println("noteOn: " + noteOn + "; noteOff: " + noteOff + "; event: " + lastEvent);
        // new note played, so finish up previous notes or insert rests up to the current note
        int stopIndex = snapStart(tickToSlots(noteOff)) - getCountInBias();
        if( notePlaying )
          {
            handleNoteOff(prevNote, velocity, channel);
          }
        else
          {
            int restDuration = snapRest(tickToSlots(lastEvent - noteOff));

            // add rests since nothing was played between now and the previous note
            if( restDuration > restAbsorption && stopIndex >= 0 )
              {
                Note restToAdd = new Rest(restDuration);
                setNote(stopIndex, restToAdd);
              }
          }

        noteOn = lastEvent;
        noteOnIndex = snapStart(tickToSlots(noteOn)) - getCountInBias();

        // add placeholder note until duration can be established
        Note noteToAdd = new Note(note, BEAT);

        setNote(noteOnIndex, noteToAdd);

        prevNote = note;
        notePlaying = true;

        notate.repaint();
    }

    void handleNoteOff(int note, int velocity, int channel)
    {
        //System.out.println("noteOff: " + noteOff + "; event: " + lastEvent);

        if( note != prevNote )
          {
            return;
          }

        // use the one in constructor: Notate notate = imp.ImproVisor.getCurrentWindow();
        noteOff = lastEvent;
        notePlaying = false;

        if( noteOnIndex < 0 )
          {
            return;
          }

        int duration = snapDuration(tickToSlots(noteOff - noteOn));

        if( duration != 0 )
          {
            Note noteToAdd = new Note(note, duration);
            setNote(noteOnIndex, noteToAdd);
            noteOnIndex += duration;
          }

        notate.repaint();
    }

    
    /**
     * The stopIndex could be anywhere within the tune now, so taking mod
     * relative to part size is needed.
     *
     * @param index
     * @param noteToAdd
     */
    private void setNote(int index, Note noteToAdd) //THIS COULD BE It
    {
        if( index == lastIndex )
          {
            return;
          }
        try
          {
            noteToAdd.setEnharmonic(score.getCurrentEnharmonics(index));

            if( tradePart == null )
              {
                melodyPart = notateMelodyPart;
              }
            else
              {
                melodyPart = tradePart;
              }

            index = (index % melodyPart.size()) - recordLatency;
            if( index < 0 )
              {
                index = 0;
              }
            //System.out.println("considering at " + index + ": " + noteToAdd.toLeadsheet());
            
            if( lastNoteAdded != null && lastNoteAdded.samePitch(noteToAdd) && lastNoteAdded.getRhythmValue() == noteToAdd.getRhythmValue() )
              {
                //System.out.println("avoid setting note " + noteToAdd.toLeadsheet());
              }
            else
              {
              //System.out.println("setting at " + index + ": " + noteToAdd.toLeadsheet());
              Note oldNote = melodyPart.getNote(index);
              if( oldNote != null && oldNote.nonRest() && oldNote.getPitch() != noteToAdd.getPitch() )
                {
                  notesLost++;
                  System.out.println("note lost at beat " + (index/BEAT) + ": " + oldNote.toLeadsheet());
                }
              
//              System.out.println(
//                     "lastIndex2 = " + lastIndex2 
//                      + ", lastIndex = " + lastIndex 
//                      + ", index = " + index 
//                      + ", lastNoteAdded2 = " + (lastNoteAdded2 == null ? "null" : lastNoteAdded2.toLeadsheet())
//                      + ", lastNoteAdded = " + (lastNoteAdded == null ? "null" : lastNoteAdded.toLeadsheet())
//                      + ", noteToAdd = " + noteToAdd.toLeadsheet());
//              
              if( swingEighths 
               && index%BEAT == 0
               && lastNoteAdded != null 
               && lastNoteAdded2 != null
               && lastNoteAdded.nonRest()
               && lastIndex2 == index - BEAT
               && lastIndex == index - 40
                 )
                {
                Note firstSwingNote = lastNoteAdded2.copy();
                Note secondSwingNote = lastNoteAdded.copy();
            System.out.print("\nswing detected at beat " + lastIndex/BEAT
                   + " converting " + firstSwingNote.toLeadsheet() + " & "
                   + secondSwingNote.toLeadsheet());
                firstSwingNote.setRhythmValue(BEAT/2);
                secondSwingNote.setRhythmValue(BEAT/2);
            System.out.println(" to " 
                   + firstSwingNote.toLeadsheet() + " & "
                   + secondSwingNote.toLeadsheet());
            //System.out.println("melody before swing:   " + melodyPart);
                melodyPart.setNoteFromCapture(lastIndex2, firstSwingNote);
                melodyPart.setNoteFromCapture(lastIndex2 + BEAT/2, secondSwingNote);
            //System.out.println("melody after swing:    " + melodyPart);
                swingConversions++;
                }
              
              melodyPart.setNote(index, noteToAdd);
              lastNoteAdded2 = lastNoteAdded;
              lastNoteAdded = noteToAdd;
              lastIndex2 = lastIndex;
              lastIndex = index;
              }
          }
        catch( Exception e )
          {
            System.out.println("unexpected exception in MidiRecorder.setNote " + e);
          }
    }

long getTick()
    {
        if( sequencer != null && sequencer.isRunning() )
          {
            double bpms = ((double) sequencer.getTempoInBPM()) / DIVISOR;    // beats per millisecond
            long latencyTicks = Math.round(bpms * resolution * latency);

            return sequencer.getTickPosition() - latencyTicks;
          }
        else
          {
            //notate.stopRecording();
            return -1;
          }
    }
        
int tickToSlots(long duration)
    {
        return (int) (BEAT * duration / resolution);
    }

// Not sure this is the right direction to quantize in each case
int snapDuration(int slots)
    {
        slots = MelodyPart.quantizeUp(slots, gcd);
        return slots;
    }

int snapRest(int slots)
    {
        slots = MelodyPart.quantizeDown(slots, gcd);
        return slots;
    }

int snapStart(int slot)
    {
        slot = MelodyPart.quantizeDown(slot, gcd);
        while( slot % quantum[0] != 0 && slot % quantum[1] != 0 )
          {
            slot += gcd;
            //System.out.println("advancing: " + (slot-gcd) + " -> " + slot);
          }
        return slot;
    }

    public void close()
    {
    }
  }
