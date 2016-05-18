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

package imp.data;

import imp.Constants;
import imp.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javax.sound.midi.*;

/**
 * Created: Mon May 07 11:21:30 2001
 *
 * @author Mark Elston (enhanced by Andrew Brown and
 *                      endSequence by Stephen Jones)
 *
 * Integrated with the MidiManager by Martin Hunt, June 2006
 *    added volume control
 *    support for multiple windows, each with its own mixer
 *
 * Notes:
 *  - if you close the sequencer, you can't just reopen it because the transmitter
 *    is no longer in existence, so you need to get a new transmitter and
 *    register it with the MidiSystem so that receiver can play the notes
 *    transmitted
 */

public class MidiSynth implements Constants, MetaEventListener
{

/** Pulses per quarter note value */
private short m_ppqn;

/** The overall (Score) tempo value */
private float tempo;

/** sets the MIDI resolution */
private final static short DEFAULT_PPQ = 480;

private final static double MAGIC120 = 120.0;

/** end of track */
private final static int STOP_TYPE = 47;

private MidiManager midiManager;

private Mixer volumeControl = null;

private Sequencer sequencer = null;

private boolean paused = false;

private boolean playing = false;

private MidiPlayListener playListener = null;

private static long playCounter = 0;

private int countInOffset = 0;

private long playbackStartTime;

ArrayList<MidiNoteListener> noteListeners = new ArrayList<MidiNoteListener>();


public MidiSynth(MidiManager midiManager)
  {
    this(midiManager, DEFAULT_PPQ);
  }

public MidiSynth(MidiManager midiManager, short ppqn)
  {
    this.midiManager = midiManager;
    m_ppqn = ppqn;

    volumeControl = new Mixer(NUM_CHANNELS);

    /* the instance receives messages, sends them through the mixer (volumeControl),
     * and the volumeControl transmits them to the midiManager for tranmission to
     * the actual device
     */
    midiManager.registerTransmitter(volumeControl);
  }


/**
 * Apparently the setTempo method of the java sequencer has a bug and we are
 * advised to use setTempoFactor to control tempo instead of setTempoInBPM
 * or other.
 * See http://www.jsresources.org/faq_midi.html#tempo_methods
 @param fBPM
 */

public void setTempo(float fBPM)
{
    if( sequencer != null )
    {
    float fCurrent = sequencer.getTempoInBPM();
    float fFactor = fBPM / fCurrent;
    sequencer.setTempoFactor(fFactor);
    }

    tempo = fBPM;
}


public float getTempo()
  {
    return tempo;
  }


public void setCountInOffset(int countInOffset)
{
    this.countInOffset = countInOffset;
}


public int getCountInOffset()
{
    return countInOffset;
}


public void setPastCountIn()
{
    setSlot(countInOffset);
}


public long getMicrosecond()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return (long) (sequencer.getMicrosecondPosition() * MAGIC120 / getTempo() );
  }


public long getTotalMicroseconds()
  {
    if( sequencer == null )
      {
        return 0;
      }

    return (long) (sequencer.getMicrosecondLength()*(1-getCountInFraction()) * MAGIC120 / getTempo());
  }


/**
 @return the fraction of the countIn over the total length, including countIn
 */

public double getCountInFraction()
  {
    if( sequencer == null )
      {
        return 0;
      }

    int totalSlots = getTotalSlots();

    if( totalSlots == 0 )
      {
        return 0;
      }

    int offset = getCountInOffset();

    double fraction = ((double)offset) / (totalSlots+offset);
    //System.out.print("countIn Fraction = " + fraction);
    return fraction;
  }


/**
 @return the length of the countIn in microseconds
 */

public long getCountInMicroseconds()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return (long) (getCountInFraction() * getTotalMicrosecondsWithCountIn());
  }


public long getTotalMicrosecondsWithCountIn()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return (long) (sequencer.getMicrosecondLength() * MAGIC120 / tempo );
  }


public long getMicrosecondsRemaining()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return getTotalMicrosecondsWithCountIn() - getMicrosecond();    
  }

public long getSlotsPerMicrosecond()
{
    return sequencer.getTickLength();
}


public void setMicrosecond(long position)
  {
    if( sequencer == null )
      {
        return;
      }
    midiManager.sendAllSoundsOffMsg();
    long value = (long) (position * tempo / MAGIC120);

    value += getCountInMicroseconds();

    //System.out.println("setMicrosecond position = " + position + ", value = " + value + ", tempo = " + tempo);

    sequencer.setMicrosecondPosition(value);
 }


public void setFraction(double fraction)
{
    int totalSlots = getTotalSlots();
    long slot = (long)(fraction*totalSlots)  + getCountInOffset();
    //System.out.println("setFraction = " + fraction + " totalSlots = " + totalSlots + " slot = " + slot );
    setSlot(slot);
}


public double getFraction()
  {
    if( sequencer == null )
      {
        return 0;
      }

    int totalSlots = getTotalSlots();

    if( totalSlots == 0 )
      {
        return 0;
      }

    return (double) getSlot() / totalSlots;
  }


public void setSlot(long slot)
  {
    //System.out.println("setSlot = " + slot);
    if( sequencer == null )
      {
        return;
      }

    if( slot < 0 )
      {
        slot = 0;
      }
    long value = slot * m_ppqn / BEAT;

    sequencer.setTickPosition(value);
  }


/**
 @return the slot number, starting after any count-in
 */

public int getSlot()
  {
    if( playing )
      {
        int slot = (int) Math.floor(
                BEAT * sequencer.getTickPosition() / (double) m_ppqn);

        slot -= getCountInOffset();

        if( slot < 0 )
          {
            slot = 0;
          }

        return slot;
      }
    else
      {
        return 0;
      }
  }


/**
 @return total number of slots, not counting count-in
 */

public int getTotalSlots()
  {
    if( playing )
      {
        double value = BEAT * sequencer.getTickLength() / (double) m_ppqn;
        //System.out.println("tickLength = " + sequencer.getTickLength() + " value = " + (int) Math.floor(value));
        return (int) (Math.round(value) - getCountInOffset());
      }
    else
      {
        return 0;
      }
  }

public long getPlaybackStartTime() {
    return playbackStartTime;
}


public boolean finishedPlaying()
  {
    return sequencer == null || sequencer.getTickPosition() >= sequencer.getTickLength()-1;
  }

public boolean almostFinishedPlaying(int n)
  {
    return sequencer == null || sequencer.getTickPosition() - sequencer.getTickLength()-1 >= -n;
  }

public void play(Score score,
                 long startTime,
                 int loopCount,
                 int transposition)
    throws InvalidMidiDataException
  {
    play(score,
         startTime,
         loopCount,
         transposition,
         true);
  }


public void play(Score score,
                 long startTime,
                 int loopCount,
                 int transposition,
                 boolean useDrums)
    throws InvalidMidiDataException
  {
    play(score,
         startTime,
         loopCount,
         transposition,
         useDrums,
         -1); // RK 6/11/2010 The last parameter was omitted, causing an infinite loop. I hope this value makes sense.
  }


public void play(Score score,
                 long startIndex,
                 int loopCount,
                 int transposition,
                 boolean useDrums,
                 int endLimitIndex)
    throws InvalidMidiDataException
  {
    play(score,
         startIndex,
         loopCount,
         transposition,
         useDrums,
         endLimitIndex,
         0);
}


/**
 * Plays the score data via a MIDI synthesizer
 * @param score   Score data to change to SMF
     * @param startIndex
     * @param loopCount
     * @param transposition
     * @param useDrums
     * @param endLimitIndex
     * @param countInOffset
     * @throws javax.sound.midi.InvalidMidiDataException
 */
public void origPlay(Score score,
                 long startIndex,
                 int loopCount,
                 int transposition,
                 boolean useDrums,
                 int endLimitIndex,
                 int countInOffset)
    throws InvalidMidiDataException
  {
//   Trace.log(0,
//              (++playCounter) + ": Starting MIDI sequencer, startTime = "
//              + startIndex + " loopCount = " + loopCount + " endIndex = "
//              + endLimitIndex);

    if( sequencer == null )
      {
        setSequencer();
      }

    setCountInOffset(countInOffset);

    tempo = (float) score.getTempo();

    Sequence seq = score.render(m_ppqn, transposition, useDrums, endLimitIndex);

    int magicFactor = Style.getMagicFactor();

    if( null != seq )
      {
        try
          {
            sequencer.open();
          }
        catch( MidiUnavailableException e )
          {
            ErrorLog.log(ErrorLog.SEVERE, "MIDI System Unavailable:" + e);
            return;
          }
        sequencer.setSequence(seq);
        // needed? sequencer.addMetaEventListener(this);

        setTempo(tempo);

        // Clear possible old values

        setLoopStartPoint(0);
        setLoopEndPoint(ENDSCORE);

        setLoopCount(0);

        setSlot(startIndex);

        //System.runFinalization();
        //System.gc();

        if( loopCount != 0 )
        {
         // set end time first, otherwise there might be an exception because
         // the start time is too large.

// CAUTION: Changing this code may break the combination of countIn with looping.

        if( endLimitIndex == ENDSCORE )
          {
          setLoopEndPoint(ENDSCORE);
          }
        else
          {
          setLoopEndPoint((endLimitIndex /*+ countInOffset*/) * magicFactor);
          }

        if( countInOffset > 0 )
        {
            setLoopStartPoint((1+ countInOffset) * magicFactor);
        }
        else
        {
            setLoopStartPoint(startIndex * magicFactor);
        }

        if( loopCount < 0 )
          {
            setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
          }
        else if( loopCount > 0 )
          {
            try
              {
                setLoopCount(loopCount);

              }
            catch( IllegalArgumentException e )
              {
                ErrorLog.log(ErrorLog.SEVERE,
                             "internal problem looping: start = "
                             + startIndex + ", end = " + endLimitIndex
                             + " tempoFactor = " + sequencer.getTempoFactor());
              }
          }
        }

        // Here's where the playback actually starts:

        sequencer.start();

        playbackStartTime = System.nanoTime();
        //System.out.println("Playback started at time " + playbackStartTime);

        playing = true;
        paused = false;
        if( playListener != null && sequencer.isRunning() )
          {
            playListener.setPlaying(MidiPlayListener.Status.PLAYING,
                                    transposition);
          }
      }
}

/**
 * Refactored version of the original play method.
 * Separates into prePlay and actualPlay, for purposes of reducing latency
 * when playback starts.
 * 
 * @param score
 * @param startIndex
 * @param loopCount
 * @param transposition
 * @param useDrums
 * @param endLimitIndex
 * @param countInOffset
 * @throws InvalidMidiDataException 
 */
public void play(Score score,
                 long startIndex,
                 int loopCount,
                 int transposition,
                 boolean useDrums,
                 int endLimitIndex,
                 int countInOffset)
    throws InvalidMidiDataException
  {
    prePlay(score,
            startIndex,
            loopCount,
            transposition,
            useDrums,
            endLimitIndex,
            countInOffset);
    
    actualPlay(transposition);
  }

public void prePlay(Score score,
                    long startIndex,
                    int loopCount,
                    int transposition,
                    boolean useDrums,
                    int endLimitIndex,
                    int countInOffset)
        throws InvalidMidiDataException
  {
      prePlay(score,
              startIndex,
              loopCount,
              transposition,
              useDrums,
              endLimitIndex,
              countInOffset,
              false);
  }


/**
 * Does most of what's in play(...) except for starting the sequencer.
 * This is so priming can take place, and start will have less latency.
 * @param score   Score data to change to SMF
     * @param startIndex
     * @param loopCount
     * @param transposition
     * @param useDrums
     * @param endLimitIndex
     * @param countInOffset
     * @param isTradingMelody
     * @throws javax.sound.midi.InvalidMidiDataException
 */
public void prePlay(Score score,
                    long startIndex,
                    int loopCount,
                    int transposition,
                    boolean useDrums,
                    int endLimitIndex,
                    int countInOffset,
                    boolean isTradingMelody)
        throws InvalidMidiDataException
  {
      //System.out.println("MidiSynth: isTradingMelody = " + isTradingMelody);
//   Trace.log(0,
//              (++playCounter) + ": Starting MIDI sequencer, startTime = "
//              + startIndex + " loopCount = " + loopCount + " endIndex = "
//              + endLimitIndex);
      
    if( sequencer == null )
      {
        setSequencer();
      }

    setCountInOffset(countInOffset);

    tempo = (float) score.getTempo();

    Sequence seq = score.render(m_ppqn, transposition, useDrums, endLimitIndex, isTradingMelody);

    int magicFactor = Style.getMagicFactor();

    if( null != seq )
      {
        try
          {
            sequencer.open();
          }
        catch( MidiUnavailableException e )
          {
            ErrorLog.log(ErrorLog.SEVERE, "MIDI System Unavailable:" + e);
            return;
          }
        sequencer.setSequence(seq);
        // needed? sequencer.addMetaEventListener(this);

        setTempo(tempo);

        // Clear possible old values

        setLoopStartPoint(0);
        setLoopEndPoint(ENDSCORE);

        setLoopCount(0);

        setSlot(startIndex);

        System.runFinalization();
        System.gc();

        if( loopCount != 0 )
          {
            // set end time first, otherwise there might be an exception because
            // the start time is too large.

// CAUTION: Changing this code may break the combination of countIn with looping.

            if( endLimitIndex == ENDSCORE )
              {
                setLoopEndPoint(ENDSCORE);
              }
            else
              {
                setLoopEndPoint((endLimitIndex /*
                         * + countInOffset
                         */) * magicFactor);
              }

            if( countInOffset > 0 )
              {
                setLoopStartPoint((1 + countInOffset) * magicFactor);
              }
            else
              {
                setLoopStartPoint(startIndex * magicFactor);
              }

            if( loopCount < 0 )
              {
                setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
              }
            else if( loopCount > 0 )
              {
                try
                  {
                    setLoopCount(loopCount);

                  }
                catch( IllegalArgumentException e )
                  {
                    ErrorLog.log(ErrorLog.SEVERE,
                                 "internal problem looping: start = "
                            + startIndex + ", end = " + endLimitIndex
                            + " tempoFactor = " + sequencer.getTempoFactor());
                  }
              }
          }

      }
  }


public void actualPlay(int transposition)
  {
    // Here's where the playback actually starts:

    sequencer.start();

    //playbackStartTime = System.nanoTime();
    //System.out.println("Playback started at time " + playbackStartTime);

    playing = true;
    paused = false;
    if( playListener != null && sequencer.isRunning() )
      {
        playListener.setPlaying(MidiPlayListener.Status.PLAYING,
                                transposition);
      }
  }



public boolean isRunning()
  {
    return sequencer != null && sequencer.isRunning();
  }

public void setLoopStartPoint(long point)
{
    //System.out.println("setLoopStartPoint " + point);
    sequencer.setLoopStartPoint(point);

}

public void setLoopEndPoint(long point)
{
    //System.out.println("setLoopEndPoint " + point);
    sequencer.setLoopEndPoint(point);
}


public void setLoopCount(int count)
{
    sequencer.setLoopCount(count);
    //System.out.println("sequencer.setLoopCount " + count);
}


/**
 * Invoked when a Sequencer has encountered and processed a MetaMessage
 * in the Sequence it is processing.
 * @param metaEvent      the MetaMessage that the sequencer encountered
 */

public void meta(MetaMessage metaEvent)
  {
    Trace.log(3, playCounter + ": MidiSynth metaEvent: " + metaEvent);
    //if( metaEvent.getType() == StopType )
      {
        System.out.print("meta event of type " + metaEvent.getType()
                       + " encountered, data: ");

        for( byte x: metaEvent.getData() )
          {
            System.out.print(x);
            System.out.print(" ");
          }
        System.out.println();
        //stop("meta");
      }
  }


public void pause()
  {
    Trace.log(3, playCounter + ": Pausing MidiSynth, paused was " + paused);
    if( paused )
      {
        sequencer.start();
        paused = false;
        playListener.setPlaying(MidiPlayListener.Status.PLAYING, 0);
      }
    else
      {
        sequencer.stop();
        paused = true;
        playListener.setPlaying(MidiPlayListener.Status.PAUSED, 0);
      }
  }


public void setPlayListener(MidiPlayListener listener)
  {
    Trace.log(3, playCounter + ": Setting MidiPlayListener ");
    //I don't know why this was here - Brian
//    if( playListener != null )
//      {
//
//        playListener.setPlaying(MidiPlayListener.Status.STOPPED, 0);
//        System.out.println("Sequencer stopped from setPlayListener()");
//      }
    playListener = listener;
  }


/**
 * Stop sequencer object
     * @param reason
 */
public void stop(String reason)
  {

    //debug System.out.println("Stopping sequencer because: " + reason);

    if( sequencer != null )
      {
      //showSequencerStatus();
      }

    playing = false;
    paused = false;

    // This seems to be causing problems in the style editor cell and column play
    if( sequencer != null && sequencer.isOpen() )
      {
        sequencer.stop();
      }

    // this should be the LAST thing this function does before returning
    if( playListener != null )
      {
        playListener.setPlaying(MidiPlayListener.Status.STOPPED, 0);
        //System.out.println("Sequencer stopped from stop()");
      }

  }

public void close()
  {
    stop("close");

    if( sequencer != null && sequencer.isOpen() )
      {
        sequencer.close();
      }
  }


/**
 * Create a Note On Event
 * @param channel   the channel to change
 * @param pitch     the pitch of the note
 * @param velocity  the velocity of the note
 * @param tick      the time this event occurs
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException 
 */
protected static MidiEvent createNoteOnEvent(int channel,
                                             int pitch, int velocity,
                                             long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x90 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);
    return evt;
  }


/**
 * Create a Note Off Event
 * @param channel   the channel to change
 * @param pitch     the pitch of the note
 * @param velocity  the velocity of the note
 * @param tick      the time this event occurs
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException
 */
protected static MidiEvent createNoteOffEvent(int channel,
                                              int pitch, int velocity,
                                              long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x80 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

    protected static MidiEvent createBankSelectEventMSB(int bankMSB,
            long tick)
            throws InvalidMidiDataException {
        return createBankSelectEventMSB(bankMSB,
                tick,
                false);
    }

/**
 * Not sure this is correct:
 * Create a Bank Select MSB Event
 * @param bankMSB  MSB of the bank to select
 * @param tick     the time this event occurs
     * @param isTradingMelody
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException 
 */
protected static MidiEvent createBankSelectEventMSB(int bankMSB,
                                                    long tick,
                                                    boolean isTradingMelody)
    throws InvalidMidiDataException
  {
    // Bank change is accomplished through control change, using controller
    // 32 for MSB, and controller 0 for LSB
    return createCChangeEvent(0, 32, bankMSB, tick, isTradingMelody);
  }

protected static MidiEvent createBankSelectEventLSB(int bankLSB,
            long tick)
            throws InvalidMidiDataException {
        return createBankSelectEventLSB(bankLSB,
                tick,
                false);
    }

/**
 * Not sure this is correct:
 * Create a Bank Select LSB Event
     * @param bankLSB
 * @param bankMSB  LSB of the bank to select
     * @param isTradingMelody
 * @param value    the new value to use
 * @param tick     the time this event occurs
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException 
 */
protected static MidiEvent createBankSelectEventLSB(int bankLSB,
                                                    long tick,
                                                    boolean isTradingMelody)
    throws InvalidMidiDataException
  {
    // Bank change is accomplished through control change, using controller
    // 32 for MSB, and controller 0 for LSB
    return createCChangeEvent(0, 0, bankLSB, tick, isTradingMelody);
  }


/**
 * Create a Program Change Event
 * @param channel  the channel to change
 * @param value    the new value to use
 * @param tick     the time this event occurs
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException 
 */
protected static MidiEvent createProgramChangeEvent(int channel,
                                                    int value,
                                                    long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xC0 + channel, value, 0);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

protected static MidiEvent createCChangeEvent(int channel,
                                              int controlNum,
                                              int value,
                                              long tick)
    throws InvalidMidiDataException
  {
      return createCChangeEvent(channel,
              controlNum,
              value,
              tick,
              false);
  }


/**
 * Create a Control Change event
 * @param channel     the channel to use
 * @param controlNum  the control change number to use
 * @param value       the value of the control change
 * @param tick        the time this event occurs
     * @param isTradingMelody
     * @return 
     * @throws javax.sound.midi.InvalidMidiDataException 
 */
protected static MidiEvent createCChangeEvent(int channel,
                                              int controlNum,
                                              int value,
                                              long tick, 
                                              boolean isTradingMelody)
    throws InvalidMidiDataException
  {
    if(isTradingMelody) return null;
    else{
    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xB0 + channel, controlNum, value);
    MidiEvent evt = new MidiEvent(msg, tick);
    return evt;
    }
  }


/**
 * Takes the specified Sequence, finds the longest track and
 * adds an event to end the Sequence.
 * @param seq       the Sequence to end
     * @throws javax.sound.midi.InvalidMidiDataException
 */
protected static void endSequence(Sequence seq)
    throws InvalidMidiDataException
  {
    Track longestTrack = null;
    long longestTime = 0;
    List<Track> tracks = Arrays.asList(seq.getTracks());
    ListIterator<Track> i = tracks.listIterator();
    while( i.hasNext() )
      {
        Track track = i.next();
        if( track.ticks() > longestTime )
          {
            longestTime = track.ticks();
            longestTrack = track;
          }
      }

    if( longestTime > 0 && longestTrack != null )
      {
        MetaMessage msg = new MetaMessage();
        byte[] data = new byte[0];
        msg.setMessage(STOP_TYPE, data, 0);
        MidiEvent evt = new MidiEvent(msg, longestTime);
        longestTrack.add(evt);
      }

  }


public Sequencer getSequencer()
  {
    return sequencer;
  }


private void setSequencer()
  {
    //Trace.log(2, "Getting MIDI sequencer");
    sequencer = null;
    try
      {
        /* pass getSequencer false so that it doesn't autoconnect to
         * default receiver, we hook it into our own MidiManager that
         * will update the transmitter when the receiver changes
         */
        sequencer = MidiSystem.getSequencer(false);

        /* add the transmitter to the midi manager so that it is always
         * hooked to the correct receiver (allows the user to change
         * the receiver by changing the MIDI out device)
         */
        registerTransmitter(sequencer.getTransmitter());
      }
    catch( MidiUnavailableException e )
      {
        ErrorLog.log(ErrorLog.WARNING, "Couldn't get sequencer:" + e.getMessage());
      }
  }


public int getMasterVolume()
  {
    return volumeControl.getMasterVolume();
  }


public void setMasterVolume(int volume)
  {
    volumeControl.setMasterVolume(volume);
  }


public void setChannelVolume(int channel, int volume)
  {
    //System.out.println("setting volume of channel " + channel + " to " + volume);
    volumeControl.setChannelVolume(channel, volume);
  }


public void registerReceiver(Receiver r)
  {
    midiManager.registerReceiver(r);
  }


public void unregisterReceiver(Receiver r)
  {
    midiManager.unregisterReceiver(r);
  }


void registerTransmitter(Transmitter t)
  {
    t.setReceiver(volumeControl);
  }


public void registerNoteListener(MidiNoteListener m)
  {
    noteListeners.add(m);
  }


public void unregisterNoteListener(MidiNoteListener m)
  {
    while( noteListeners.contains(m) )
      {
        noteListeners.remove(m);
      }
  }

private class Mixer implements Receiver, Transmitter
{
Receiver receiver;

private final int numChannels;

private final double[] channelVolume;

private double volume = 1;

public Mixer(int numChannels)
  {
    this.numChannels = numChannels;
    channelVolume = new double[numChannels];
    for( int i = 0; i < numChannels; i++ )
      {
        channelVolume[i] = 1;
      }
  }

public void setMasterVolume(int volume)
  {
    this.volume = (double) volume / MAX_VOLUME;
  }

public int getMasterVolume()
  {
    return (int) (this.volume * MAX_VOLUME);
  }

public void setChannelVolume(int channel, int volume)
  {
    if( channel < numChannels )
      {
        channelVolume[channel] = (double) volume / MAX_VOLUME;
      }
  }

public void send(MidiMessage message, long timeStamp)
  {
    byte[] msg = message.getMessage();
    int highNibble = (msg[0] & 0xF0) >> 4;
    
    if( highNibble == 9 )
      {
        int channel = msg[0] & 0x0F;
        int note = msg[1];
        int velocity = (int) (msg[2] * channelVolume[channel] * volume);

        //DEBUG: 
        //System.out.println(note + " " + channel + " " + msg[2] + " -> " + velocity);

        ShortMessage newMsg = new ShortMessage();
        try
          {
            newMsg.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
            receiver.send(newMsg, timeStamp);
          }
        catch( Exception e )
          {
          }

        /* TODO: should this be threaded to prevent lag?
         * as long as the noteOn function calls don't take too long this
         * should be ok...
         */
        for( MidiNoteListener n: noteListeners )
            {
                n.noteOn(note, channel);
            }
        } else {
            if (highNibble == 11) {
                //IMPORTANT NOTE!!! THIS DISABLES CC MIDI MESSAGES!!!!!!!!!!
                //ignore control messages. this is to prevent autogenerated 
                //messages such as "all notes off"
            } // process note on events only
            else {
                try {
                    receiver.send(message, timeStamp);
                } catch (Exception e) {
                }
            }
        }
    } // end of inner class Mixer


public void close()
  {
  }


public void setReceiver(Receiver receiver)
  {
    this.receiver = receiver;
  }


public Receiver getReceiver()
  {
    return receiver;
  }

}


public void showSequencerStatus()
{
System.out.println("Sequencer/sequence status:");
System.out.println("    isRunning = "            + sequencer.isRunning());
System.out.println("    Tick Position = "        + sequencer.getTickPosition());
System.out.println("    Tick Length = "          + sequencer.getTickLength());
System.out.println("    loopCount = "            + sequencer.getLoopCount());
System.out.println("    loopStart = "            + sequencer.getLoopStartPoint());
System.out.println("    loopEnd = "              + sequencer.getLoopEndPoint());
System.out.println("    tempoInBPM = "           + sequencer.getTempoInBPM());
System.out.println("    tempoInMPQ = "           + sequencer.getTempoInMPQ());
System.out.println("    TempoFactor = "          + sequencer.getTempoFactor());
System.out.println("    Microsecond Position = " + sequencer.getMicrosecondPosition());
System.out.println("    Microsecond Length = "   + sequencer.getMicrosecondLength());
System.out.println("    division type = "        + sequencer.getSequence().getDivisionType());
System.out.println("    resolution = "           + sequencer.getSequence().getResolution());
System.out.println("    microsecond/tick = "     + sequencer.getMicrosecondLength()/sequencer.getTickLength());
}

/*
protected void printSeqInfo(Sequence seq) {
//System.out.println("Score Title: " + scoreTitle);
//System.out.println("Score TempoEvent: " + tempo + " BPM");
//System.out.print("Sequence Division Type = ");
float type = seq.getDivisionType();
if (Sequence.PPQ == type)
System.out.println("PPQ");
else if (Sequence.SMPTE_24 == type)
System.out.println("SMPTE 24 (24 fps)");
else if (Sequence.SMPTE_25 == type)
System.out.println("SMPTE 25 (25 fps)");
else if (Sequence.SMPTE_30 == type)
System.out.println("SMPTE 30 (30 fps)");
else if (Sequence.SMPTE_30DROP == type)
System.out.println("SMPTE 30 Drop (29.97 fps)");
else
System.out.println("Unknown");

System.out.println("Sequence Resolution = " +
seq.getResolution());
System.out.println("Sequence TickLength = " +
seq.getTickLength());
System.out.println("Sequence Microsecond Length = " +
seq.getMicrosecondLength());
System.out.println("Sequencer TempoEvent (BPM) = " +
sequencer.getTempoInBPM());
sequencer.System.out.println("Sequencer TempoEvent (MPQ) = " +
sequencer.getTempoInMPQ());
System.out.println("Sequencer TempoFactor = " +
sequencer.getTempoFactor());
}
 */
}// MidiSynth
