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



/**
 * Written by Martin Hunt
 *
 * Encapsulates all events of the playback slider and corresponding time labels
 * To use:
 *    Pass in correct components into the constructor
 *    Call setPlayStatus when the playStatus changes
 *    Call setTotalTime when the score or total time changes
 */

package imp.util;

import imp.midi.MidiPlayListener;
import imp.midi.MidiSynth;
import imp.data.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author Martin
 */
public class PlaybackSliderManager implements MidiPlayListener, ChangeListener, ActionListener {
    MidiSynth midiSynth;
    JLabel currentTimeLabel;
    JLabel totalTimeLabel;
    JSlider slider;
    javax.swing.Timer timer = null;
    ActionListener secondaryListener = null;
    boolean ignoreEvent = false;

    long totalTimeMicroseconds = 0;

    static long million = 1000000;
    static double dmillion = million;

    /**
     * This time is used to update the playback slider.
     * It is also used to cut off playback at a certain number of slots, which
     * is why the resolution is so high (1 ms.). Without that, the playback
     * tends to over-run. The checking for this is done within Notate
     * (see setPlaybackStop method).
     *
     * A better solution for the latter is likely possible using the sequencer
     * loopEnd parameter, but this will take additional work.
     */
    static int timerInterval = 1; // interval delay for timer, in milliseconds

    //loat rememberedTempo;

    MidiPlayListener.Status status = MidiPlayListener.Status.STOPPED;

    /** Creates a new instance of PlaybackSliderManager */

    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider) {
        this(midiSynth, currentTime, totalTime, slider, null);
    }

    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider,
                                 ActionListener playbackRefreshTimerListener) {
        this.midiSynth = midiSynth;
        this.currentTimeLabel = currentTime;
        this.totalTimeLabel = totalTime;
        this.slider = slider;

        this.secondaryListener = playbackRefreshTimerListener;

        slider.addChangeListener(this);
        timer = new javax.swing.Timer(timerInterval, this);
    }

     /**
     * Called on playback position slider change,
     * since this class implements ChangeListener
     */

    public void stateChanged(ChangeEvent evt) {
        if(ignoreEvent)
            return;

        double fraction = getSliderFraction();

        long newValue = (long) (totalTimeMicroseconds * fraction);

        //System.out.println("slider fraction = " + fraction + ", midiSynth fraction = " + midiSynth.getFraction() + ", new time = " + newValue + " total = " + totalTimeMicroseconds);

        if(!slider.getValueIsAdjusting())
          {
            //System.out.println("stateChanged, fraction = " + fraction);

          midiSynth.setFraction(fraction);
          setCurrentTimeMicroseconds(newValue);
          }
    }
    
    /**
     * Called on timer firing
     */

public void actionPerformed(ActionEvent e)
  {
    final ActionEvent evt = e;

    SwingUtilities.invokeLater(new Runnable()
    {

    public void run()
      {
        if( status != MidiPlayListener.Status.STOPPED )
          {

            if( !slider.getValueIsAdjusting() )
              {

                long microsecond = midiSynth.getMicrosecond();

                updateTimeSlider(true);

                if( secondaryListener != null )
                  {
                    secondaryListener.actionPerformed(evt);
                  }
              }
          }
        else
          {
            timer.stop();
          }
      }
    });
  }
    
    public long getMicrosecondsFromSlider()
    {
          return (long)((getSliderFraction())*(midiSynth.getTotalMicroseconds()));
    }

    public double getSliderFraction()
    {
        double fraction = ((double)slider.getValue())/slider.getMaximum();
        return fraction;
    }
    
    public long getTotalTime()
    {
        return totalTimeMicroseconds;
    }
    
    public int getTotalTimeSeconds()
    {
        return (int)Math.round((double)totalTimeMicroseconds/million);
    }

    /**
     * sets the totalTime, given the current tempo
     @param seconds
     */

    public void setTotalTime(long microseconds) {
        this.totalTimeMicroseconds = microseconds;
        int seconds = (int)Math.round((double)microseconds/million);
        setTotalTimeSeconds(seconds);

        //System.out.println("totalTime = " + microseconds + ", seconds = " + seconds);

        long currentTime = (long)(getSliderFraction()*microseconds);

        setCurrentTimeMicroseconds(currentTime);
    }

    /**
     * sets the total time label on the right of the time slider
     @param seconds
     */

    public void setTotalTimeSeconds(int seconds) {
        totalTimeLabel.setText(formatSecond(seconds));
    }


   /**
     * sets the current time showing on the slider
     @param seconds
     */

    public void setCurrentTimeMicroseconds(long microseconds) {
        currentTimeLabel.setText(formatSecond((int)Math.round((double)(microseconds/million))));
    }

    /**
     * Update the time slider, according to specified number of microseconds
     * into the piece.
     @param microseconds
     @param updateSlider
     */

public void updateTimeSlider(boolean updateSlider)
  {
    setCurrentTimeMicroseconds(
        (long) (midiSynth.getFraction() * totalTimeMicroseconds));

    if( updateSlider && !slider.getValueIsAdjusting() )
      {
        ignoreEvent = true;
        slider.setValue((int) (midiSynth.getFraction() * slider.getMaximum()));
        ignoreEvent = false;

      }
  }

    public void setPlaying(MidiPlayListener.Status playing, int transposition) {
        MidiPlayListener.Status oldStatus = status;
        status = playing;
        switch(playing) {
            case PLAYING:
                setTotalTime(midiSynth.getTotalMicroseconds());
                timer.start();
                break;
            case STOPPED:
                timer.stop();
                if(oldStatus != MidiPlayListener.Status.STOPPED)
                    slider.setValue(0);
                   break;
            case PAUSED:
                timer.start();
                break;
        }
    }

    public MidiPlayListener.Status getPlaying() {
        return status;
    }
    
    public static String formatSecond(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
    
    public static String formatMicrosecond(long microseconds) {
        return formatSecond((int) (microseconds / million));
    }

}
