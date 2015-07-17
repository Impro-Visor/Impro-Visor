/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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

import imp.util.ErrorLog;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 *
 * @author keller
 */
public class MidiSequence
{
public final static short DEFAULT_PPQ = 480;

Sequence sequence;
   
Track trackDrums[];

Track trackChords;

Track trackBass;

Track trackMelody;

public MidiSequence()
  {
  this(DEFAULT_PPQ);
  }

public MidiSequence(int ppqn)
  {
  try
    {
    sequence = new Sequence(Sequence.PPQ, ppqn); 
    trackDrums = new Track[MIDIBeast.spacelessDrumName.length];
    trackChords = sequence.createTrack();
    trackBass   = sequence.createTrack();
    trackMelody = sequence.createTrack();
    }
  catch( Exception e )
    {
    ErrorLog.log(ErrorLog.WARNING, "something wrong in MidiSequence");
    }
  }

public Sequence getSequence()
  {
    return sequence;
  }

/**
 * Creates a track for a specific drum number as needed.
 * @param midiNumber
 * @return 
 */
        
public Track getDrumTrack(int midiNumber)
  {
    int index = midiNumber - 35;
    
    Track track = trackDrums[index];
    
    if(  track == null )
      {
        trackDrums[index] = track = sequence.createTrack();
        //System.out.println("track created for " + MIDIBeast.getDrumInstrumentName(midiNumber));
      }
    return track;
  }
   
public Track getBassTrack()
  {
    return trackBass;
  }

public Track getChordTrack()
  {
    return trackChords;
  }

public Track getMelodyTrack()
  {
    return trackMelody;
  }

public int getResolution()
  {
    return sequence.getResolution();
  }
}
