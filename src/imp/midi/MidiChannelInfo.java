/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
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

package imp.midi;

import imp.data.ImportMelody;
import imp.util.ErrorLog;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author research
 */
public class MidiChannelInfo {
    
    public class ChannelInfo {

        private int channelNum;
        private String channelName;

        public ChannelInfo(int num, String name) {
            this.channelNum = num;
            this.channelName = name;
        }

        public ChannelInfo() {
            this.channelNum = 0;
            this.channelName = null;
        }

        public int getChannelNum() {
            return this.channelNum;
        }

        public void setChannelNum(int num) {
            this.channelNum = num;
        }

        public void setChannelName(String name) {
            this.channelName = name;
        }

        @Override
        public String toString() {
            if (channelNum != 0) {
                return channelNum + " : " + channelName;
            } else {
                return channelName;
            }
        }
    }
    
    private Map<Integer, String> channelNames = new HashMap<Integer, String>();
    private int chordChannel = -1;
    private int bassChannel = -1;
    
    public MidiChannelInfo(String filename) {
        jm.music.data.Score score = new jm.music.data.Score();
        jm.util.Read.midi(score, filename);
        jm.music.data.Part[] parts = score.getPartArray();
        ImportMelody importMelody = new ImportMelody(score);
        for (int i = 0; i < importMelody.size(); i++) {
            try {
                jm.music.data.Part part = importMelody.getPart(i);
                int channel = part.getChannel();
                //System.out.println("part " + i + " raw = " + part);
                int numTracks = part.getSize();

                //add instrument names to channel
                if (part != null && channel != 9) {
                    String instrumentName = MIDIBeast.getInstrumentForPart(part);
                    instrumentName = instrumentName.replaceAll("_", " ");
                    channelNames.put(channel + 1, instrumentName);

                    //choose chord and bass channel
                    int instrumentNum = part.getInstrument();
                    int channelNum = part.getChannel();
                    if (instrumentNum >= 0 && instrumentNum <= 5) {
                        //If the instrument is a kind of keyboard or guitar, it is read as a chords part.
                        chordChannel = channelNum;
                    } else if (instrumentNum >= 24 && instrumentNum <= 28) {
                        chordChannel = channelNum;
                    } else if (instrumentNum >= 32 && instrumentNum <= 38) {
                        bassChannel = channelNum;
                    }
                }
            } catch (java.lang.OutOfMemoryError e) {
                ErrorLog.log(ErrorLog.SEVERE, "There is not enough memory to continue importing this MIDI file.");
                return;
            }
        }
    }
    
    public ChannelInfo[] getChannelInfo() {
        ChannelInfo[] channelInfo = new ChannelInfo[channelNames.size() + 1];
        int index = 0;
        channelInfo[index] = new ChannelInfo();
        channelInfo[index].setChannelNum(0);
        channelInfo[index].setChannelName("None");
        index = index + 1;
        for (Map.Entry<Integer, String> entry : channelNames.entrySet()) {
            channelInfo[index] = new ChannelInfo();
            channelInfo[index].setChannelNum(entry.getKey());
            channelInfo[index].setChannelName(entry.getValue());
            index = index + 1;
        }
        return channelInfo;
    }
     
    public int getBassChannel()
    {
        return bassChannel;
    }
    
    public int getChordChannel()
    {
        return chordChannel;
    }
    //create channel names
    //channelNames.clear();
}
