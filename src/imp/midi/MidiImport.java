/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012-2014 Robert Keller and Harvey Mudd College
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
import imp.data.MelodyPart;
import imp.midi.MIDIBeast;
import static imp.midi.MidiImport.DRUM_CHANNEL;
import imp.util.ErrorLog;
import java.io.File;
import java.util.*;

/**
 * Midi File Importing
 *
 * @author Robert Keller, partly adapted from code in MIDIBeast 
 * by Brandy McMenamy and Jim Herold
 */

public class MidiImport
{
public final static int DRUM_CHANNEL = 9;
File file;
private int defaultResolution = 1;
private int resolution;
private static jm.music.data.Score score;
private static ArrayList<jm.music.data.Part> allParts;
private LinkedList<MidiImportRecord> melodies;

public MidiImport()
  {
    setResolution(defaultResolution);
   }

/**
 * This is weird, and should be redesigned to avoid static.
 */
public MidiImport(jm.music.data.Score ascore)
  {
    score = ascore;
    setResolution(defaultResolution);
   }

public int getResolution()
  {
    return resolution;
  }

public final void setResolution(int newResolution)
  {
    resolution = newResolution;
    //System.out.println("setting resolution to " + resolution);
  }

public void importMidi(File file)
  {
    if( file != null )
      {
        readMidiFile(file.getAbsolutePath());
      }
  }


/**
 * @param String midiFileName
 * 
 */

public void readMidiFile(String midiFileName)
  {
    score = new jm.music.data.Score();
    
    try
      {
      jm.util.Read.midi(score, midiFileName);
      }
    catch( Error e )
      {
        ErrorLog.log(ErrorLog.WARNING, "reading of MIDI file " + midiFileName 
                     + " failed for some reason (jMusic exception).");
        return;
      }
    
    scoreToMelodies();
  }

    
public void scoreToMelodies()
  {
  //System.out.println("score from MIDI = " + score);
  if( score != null )
    {
    allParts = new ArrayList<jm.music.data.Part>();

    allParts.addAll(Arrays.asList(score.getPartArray()));

    ImportMelody importMelody = new ImportMelody(score);

    //System.out.println("importMelody = " + importMelody);

    melodies = new LinkedList<MidiImportRecord>();
    
    for( int i = 0; i < importMelody.size(); i++ )
      {
      try
        {
        jm.music.data.Part part = importMelody.getPart(i);
        int channel = part.getChannel();
        //System.out.println("part " + i + " raw = " + part);
        int numTracks = part.getSize();
        
        for( int j = 0; j < numTracks; j++ )
          {
            MelodyPart partOut = new MelodyPart();
            ImportMelody.convertToImpPart(part, j, partOut, resolution);
            
              // For Testing of impMelody2jmPart only
              
              // jm.music.data.Part tempPart = impMelody2jmPart(partOut);
              // MelodyPart echoedPart = new MelodyPart();
              // ImportMelody.convertToImpPart(tempPart, 0, echoedPart, 1);
              // System.out.println(echoedPart);
              // System.out.println();
            
            String instrumentString = MIDIBeast.getInstrumentForPart(part);
            
            if( channel != DRUM_CHANNEL )
               {
                partOut.setInstrument(part.getInstrument());
               }
            
            MidiImportRecord record = new MidiImportRecord(channel, j, partOut, instrumentString);
            melodies.add(record);            
          }
        }
      catch( java.lang.OutOfMemoryError e )
        {
        ErrorLog.log(ErrorLog.SEVERE, "There is not enough memory to continue importing this MIDI file.");
        return;
        }
      }
    
    Collections.sort(melodies);
    
//    for( MidiImportRecord record: melodies )
//      {
//        System.out.println(record);
//      }    
    }
  }
    
    
    public jm.music.data.Score getScore() {
        return score;
    }

    public LinkedList<MidiImportRecord> getMelodies() {
        return melodies;
    }
    
    /**
     * Get the ith melody from the Import.
     * @param i
     * @return 
     */
    
    public MelodyPart getMelody(int i)
      {
        MidiImportRecord record = melodies.get(i);
        if( record == null )
          {
            return null;
          }
        return record.getPart();
      }
    
   /**
    * Convert an Impro-Visor MelodyPart into a jMusic Score
    * @param melodyPart
    * @return 
    */
    static public jm.music.data.Score impMelody2jmScore(MelodyPart melodyPart)
      {
        return ImportMelody.impMelody2jmScore(melodyPart);
      }
    
   /**
    * Convert an Impro-Visor MelodyPart into a jMusic Part
    * @param melodyPart
    * @return 
    */
   static public jm.music.data.Part impMelody2jmPart(MelodyPart melodyPart)
      {
         jm.music.data.Score tempScore = impMelody2jmScore(melodyPart);
         return tempScore.getPart(0);
      }
    
 
}
