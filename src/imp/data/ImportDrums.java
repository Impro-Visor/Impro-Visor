/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

/**
* Brandy McMenamy and James Thomas Herold 7/18/2007
* Reformatted 23 April 2012 by R. Keller
* Imports the drum part of a given MIDI song.
* Specifications are read into MIDIBeast.java. 
*/
public class ImportDrums
{

public static final int DRUM_CHANNEL = 9;
private boolean debug = false;
public boolean canContinue = true;

/**
 * In MIDI, drums are typically put on channel 9. We iterate through every part
 * in the MIDI song and select the first one that is on channel 9 as the Drum
 * Part for the song.
 */

public ImportDrums()
  {
    for( int i = 0; i < MIDIBeast.allParts.size(); i++ )
      {
        if( MIDIBeast.allParts.get(i).getChannel() == DRUM_CHANNEL )
          {
            MIDIBeast.drumPart = MIDIBeast.allParts.get(i);
            if( debug )
              {
                System.out.println("## Initial ##\n" + MIDIBeast.drumPart);
              }
            return;
          }
      }
    MIDIBeast.drumPart = null;
    MIDIBeast.addError("Could not find a drum part.  Go to Generate-->"
            + "Preferences for Generation to choose a drum part from available instruments.");
    canContinue = false;
  }

/**
 * @param p The Part object which is to be selected as the drum part
 *
 * Picks a specific drum part from a song, which will be used in upper levels
 * when switching which part is read in as the drum part.
 */

public ImportDrums(jm.music.data.Part p)
  {
    MIDIBeast.drumPart = p;
  }

/**
 * @param startMeasure This method goes through each phrase of the selected drum
 * part. For each phrase that has a start time before the user selcted start
 * time, the method turns that phrase into a new phrase without all notes before
 * the specified start time.
 */

public ImportDrums(double startBeat)
  {
    new ImportDrums();
    setPhraseEnd(startBeat);
  }

public ImportDrums(double startBeat, jm.music.data.Part selectedPart)
  {
    MIDIBeast.drumPart = selectedPart;
    setPhraseStart(startBeat, 0);
  }

public ImportDrums(double startBeat, double endBeat)
  {
    new ImportDrums();
    setPhraseStart(startBeat, endBeat);
  }

public ImportDrums(double startBeat, double endBeat, jm.music.data.Part selectedPart)
  {
    MIDIBeast.drumPart = selectedPart;
    if( debug )
      {
        System.out.println("## Initial ##\n" + MIDIBeast.drumPart);
      }
    setPhraseStart(startBeat, endBeat);
    setPhraseEnd(endBeat);
  }

/**
 * @param endMeasure the measure signifying the end of the song This method
 * iterates through each phrase. For each one, it either removes the phrase,
 * leaves it be, or removes end notes based on the phrases start and end time in
 * relation to the specified last measure
 */

public void setPhraseEnd(double endBeat)
  {
    jm.music.data.Phrase[] phraseArray = MIDIBeast.drumPart.getPhraseArray();

    //if the phrase starts before the specified end, remove the phrase from the array
    for( int i = 0; i < phraseArray.length; i++ )
      {
        if( phraseArray[i].getEndTime() < endBeat )
          {
            continue;
          }
        if( phraseArray[i].getStartTime() > endBeat )
          {
            jm.music.data.Phrase[] newPhraseArray = new jm.music.data.Phrase[phraseArray.length - 1];
            for( int j = 0; j < phraseArray.length; j++ )
              {
                if( j < i )
                  {
                    newPhraseArray[j] = phraseArray[j];
                  }
                if( j > i )
                  {
                    newPhraseArray[j - 1] = phraseArray[j];
                  }
              }
            phraseArray = newPhraseArray;
            i--;
            continue;
          }
        int noteIndex = 0;
        double beatCount = phraseArray[i].getStartTime();
        jm.music.data.Note[] noteArray = phraseArray[i].getNoteArray();
        if( phraseArray[i].getEndTime() == endBeat )
          {
            noteIndex = noteArray.length;
          }
        else
          {
            while( true )
              {
                beatCount += noteArray[noteIndex].getRhythmValue();
                if( beatCount == endBeat )
                  {
                    noteIndex--;
                    break;
                  }
                if( beatCount > endBeat )
                  {
                    noteArray[noteIndex].setRhythmValue(noteArray[noteIndex].getRhythmValue() - (beatCount - endBeat));
                    break;
                  }
                noteIndex++;
              }
          }
        jm.music.data.Note[] newNoteArray = new jm.music.data.Note[noteIndex + 1];

        for( int j = 0; j < noteIndex; j++ ) // < was <=  rk: 4/28/2012
          {
            newNoteArray[j] = noteArray[j];
          }

        phraseArray[i] = new jm.music.data.Phrase(phraseArray[i].getStartTime());
        phraseArray[i].addNoteList(newNoteArray);

      }

    MIDIBeast.drumPart.removeAllPhrases();
    MIDIBeast.drumPart.addPhraseList(phraseArray);
    if( debug )
      {
        System.out.println("## After setPhraseStart ##");
        System.out.println(MIDIBeast.drumPart);
      }

  }

/**
 * @param startMeasure the measure signifying the end of the song
 * @param endMeasure the measure signifying the end of the song This method
 * iterates through each phrase. For each one, it either removes the phrase,
 * leaves it be, or removes front notes based on the phrases start and end time
 * in relation to the specified last measure
 */

public void setPhraseStart(double startBeat, double endBeat)
  {
    jm.music.data.Phrase[] phraseArray = MIDIBeast.drumPart.getPhraseArray();
    //if phrase ends before specified start measure, remove the phrase from the array
    for( int i = 0; i < phraseArray.length; i++ )
      {
        if( endBeat == 0 )
          {
            endBeat = phraseArray[i].getEndTime();
          }
        if( startBeat < phraseArray[i].getStartTime() )
          {
            continue;
          }
        if( startBeat >= phraseArray[i].getEndTime() )
          {
            jm.music.data.Phrase[] newPhraseArray = new jm.music.data.Phrase[phraseArray.length - 1];
            for( int j = 0; j < phraseArray.length - 1; j++ )
              {
                if( j < i )
                  {
                    newPhraseArray[j] = phraseArray[j];
                  }
                if( j > i )
                  {
                    newPhraseArray[j - 1] = phraseArray[j];
                  }
              }
            phraseArray = newPhraseArray;
            i--;
            continue;
          }
        int noteIndex = 0;
        double beatCount = phraseArray[i].getStartTime();
        jm.music.data.Note[] noteArray = phraseArray[i].getNoteArray();
        if( beatCount == startBeat )
          {
            noteIndex = 0;
          }
        else
          {
            while( true )
              {
                beatCount += noteArray[noteIndex].getRhythmValue();
                if( beatCount == startBeat )
                  {
                    noteIndex++;
                    break;
                  }
                if( beatCount > startBeat )
                  {
                    noteArray[noteIndex].setRhythmValue(beatCount - startBeat);
                    break;
                  }
                noteIndex++;
              }
          }

        jm.music.data.Note[] newNoteArray = new jm.music.data.Note[noteArray.length - noteIndex];

        for( int j = noteIndex, k = 0; j < noteArray.length; j++, k++ )
          {
            newNoteArray[k] = noteArray[j];
          }

        phraseArray[i] = new jm.music.data.Phrase(startBeat);
        phraseArray[i].addNoteList(newNoteArray);
      }
    MIDIBeast.drumPart.removeAllPhrases();
    MIDIBeast.drumPart.addPhraseList(phraseArray);
    if( debug )
      {
        System.out.println("## After setPhraseStart() ##");
        System.out.println(MIDIBeast.drumPart);
      }
  }

}