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

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 *
 * @author keller
 */
public class MidiFormatting
{
static public Polylist sequence2polylist(Sequence seq)
  {
  Track track[] = seq.getTracks();

  PolylistBuffer buffer = new PolylistBuffer();

  for( int i = 0; i < track.length; i++ )
  {
      buffer.append(track2polylist(track[i]).cons(i).cons("track"));
  }

  return buffer.toPolylist();
  }

static public Polylist track2polylist(Track track)
  {
  PolylistBuffer buffer = new PolylistBuffer();

  buffer.append(Polylist.list("ticks", track.ticks()));

  long len = track.size();

  buffer.append(Polylist.list("events", len));

  for( int i = 0; i < len; i++ )
  {
      buffer.append(midiEvent2polylist(i, track.get(i)));
  }

  return buffer.toPolylist();
  }

static public Polylist midiEvent2polylist(int number, MidiEvent event)
  {
  PolylistBuffer buffer = new PolylistBuffer();
  
  buffer.append("event");
  
  buffer.append(number);

  buffer.append(Polylist.list("tick", event.getTick()));
  
  buffer.append(Polylist.list("status", decodeMidiStatus(event.getMessage().getStatus())));

  buffer.append(midiMessage2polylist(event.getMessage()));
 
  return buffer.toPolylist();
  }

static public Polylist midiMessage2polylist(MidiMessage message)
  {
    byte[] data = message.getMessage();
    int messageLength = message.getLength();
    PolylistBuffer buffer = new PolylistBuffer();
    // getStatus() returns status as an integer rather than byte.
    // As status is the first byte in the array, it is skipped below.
    buffer.append("message");
    for( int i = 1; i < messageLength; i++ )
      {
        buffer.append(data[i]);
      }
  return buffer.toPolylist();
  }

static public String decodeMidiStatus(int number)
  {
    String result;
    switch(number)
      {
        case 128: result = "Channel 1 note on"; break;
        case 129: result = "Channel 2 note on"; break;
        case 130: result = "Channel 3 note on"; break;
        case 131: result = "Channel 4 note on"; break;
        case 132: result = "Channel 5 note on"; break;
        case 133: result = "Channel 6 note on"; break;
        case 134: result = "Channel 7 note on"; break;
        case 135: result = "Channel 8 note on"; break;
        case 136: result = "Channel 9 note on"; break;
        case 137: result = "Channel 10 note on"; break;
        case 138: result = "Channel 11 note on"; break;
        case 139: result = "Channel 12 note on"; break;
        case 140: result = "Channel 13 note on"; break;
        case 141: result = "Channel 14 note on"; break;
        case 142: result = "Channel 15 note on"; break;
        case 143: result = "Channel 16 note on"; break;
 
        case 144: result = "Channel 1 note off"; break;
        case 145: result = "Channel 2 note off"; break;
        case 146: result = "Channel 3 note off"; break;
        case 147: result = "Channel 4 note off"; break;
        case 148: result = "Channel 5 note off"; break;
        case 149: result = "Channel 6 note off"; break;
        case 150: result = "Channel 7 note off"; break;
        case 151: result = "Channel 8 note off"; break;
        case 152: result = "Channel 9 note off"; break;
        case 153: result = "Channel 10 note off"; break;
        case 154: result = "Channel 11 note off"; break;
        case 155: result = "Channel 12 note off"; break;
        case 156: result = "Channel 13 note off"; break;
        case 157: result = "Channel 14 note off"; break;
        case 158: result = "Channel 15 note off"; break;
        case 159: result = "Channel 16 note off"; break;


        case 192: result = "Channel 1 program change"; break;
        case 193: result = "Channel 2 program change"; break;
        case 194: result = "Channel 3 program change"; break;
        case 195: result = "Channel 4 program change"; break;
        case 196: result = "Channel 5 program change"; break;
        case 197: result = "Channel 6 program change"; break;
        case 198: result = "Channel 7 program change"; break;
        case 199: result = "Channel 8 program change"; break;
        case 200: result = "Channel 9 program change"; break;
        case 201: result = "Channel 10 program change"; break;
        case 202: result = "Channel 11 program change"; break;
        case 203: result = "Channel 12 program change"; break;
        case 204: result = "Channel 13 program change"; break;
        case 205: result = "Channel 14 program change"; break;
        case 206: result = "Channel 15 program change"; break;
        case 207: result = "Channel 16 program change"; break;
        
        default: result = "? " + number;
      }
    return result;
  }
    
}
