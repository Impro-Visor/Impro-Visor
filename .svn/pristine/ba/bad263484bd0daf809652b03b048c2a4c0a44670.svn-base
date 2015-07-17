/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import imp.Constants;
import java.util.Iterator;

/**
 * Records relevant information about one track, including
 * the Impro-Visor MelodyPart to which the track is translated
 * @author keller
 */
public class MidiImportRecord implements Comparable, Constants
{
int channel;
int trackNumber;
MelodyPart melodyPart;
int distinctPitches;
int lowPitch;
int highPitch;
int restSlots;
int nonRestSlots;
int initialRestSlots;
double occupancy;
int totalBeats;
int initialRestBeats;
int startBeat;
int stopBeat;
String instrumentString;


public MidiImportRecord(int channel, int trackNumber, MelodyPart melodyPart, String instrumentString)
{
  this.channel = channel;
  this.trackNumber = trackNumber;
  this.instrumentString = instrumentString;
  this.melodyPart = melodyPart;
  lowPitch = 256;
  highPitch = -1;
  getStatistics();
}

/**
 * Compute various statistics for the part inferred from this channel and track.
 */

private void getStatistics()
  {
    distinctPitches = 0;
    restSlots = 0;
    nonRestSlots = 0;
    initialRestSlots = 0;
    
    int pitchCount[] = new int[128];
    
    for( int j = 0; j < pitchCount.length; j++ )
      {
        pitchCount[j] = 0;
      }
    
    boolean hasNonRest = false;
    Iterator<Unit> it = melodyPart.iterator();
    
    while( it.hasNext() )
      {
        Note note = (Note)it.next();
        if( note != null )
          {
            int rhythmValue = note.getRhythmValue();
            if( note.isRest() )
              {
                restSlots += rhythmValue;
                if( !hasNonRest )
                  {
                  initialRestSlots += rhythmValue;
                  }
              }
            else
              {
              nonRestSlots += rhythmValue;
              int pitch = note.getPitch();
              pitchCount[pitch]++;
              if( pitch > highPitch )
                {
                  highPitch = pitch;
                }
              if( pitch < lowPitch )
                {
                  lowPitch = pitch;
                }
              hasNonRest = true;
              }
          }
      }
    
    int totalSlots = restSlots + nonRestSlots;
    
    occupancy = 0;
    
    if( totalSlots > 0 )
      {
        occupancy = ((double)nonRestSlots)/totalSlots;
      }
    
    for( int j = 0; j < pitchCount.length; j++ )
      {
        if( pitchCount[j] > 0 )
          {
            distinctPitches++;
          }
      }
    
    totalBeats = (int)Math.ceil(((double)(restSlots + nonRestSlots))/BEAT);

  initialRestBeats = initialRestSlots/BEAT;
  startBeat = initialRestBeats+1;  
  }

public MelodyPart getPart()
  {
    return melodyPart;
  }

public int getChannel()
  {
    return channel;
  }

public int getBeats()
  {
    return totalBeats;
  }

public int getStartBeat()
  {
    return startBeat;
  }

public int getInitialRestSlots()
  {
    return initialRestSlots;
  }

@Override
public String toString()
  {
  int occupancyPercent = (int)(100*occupancy);
  
  String rangeString;
  
  switch(distinctPitches)
    {
      case 0:  rangeString = ""; break;
      case 1:  rangeString = "1 distinct pitch: " + lowPitch + ", "; break;
      default: rangeString = distinctPitches + " distinct MIDI pitches: " + lowPitch + " to " + highPitch + ", ";
    }
  
  return "channel " + (channel+1) + ", track " + trackNumber 
          + " " + instrumentString
          + " [beats " + startBeat + " to " + totalBeats + ", "
          + rangeString
          + occupancyPercent + "% occupied]: " 
          + melodyPart.toString();   
  }

@Override
public int compareTo(Object ob)
  {
   if( !(ob instanceof Comparable) )
     {
       return -1;
     }
   MidiImportRecord that = (MidiImportRecord)ob;
   if( channel < that.channel )
     {
       return -1;
     }
   if( channel == that.channel )
     {
       if( trackNumber < that.trackNumber )
         {
           return -1;
         }
       if( trackNumber == that.trackNumber )
         {
           return 0;
         }
     }
   return 1;
  }
}
