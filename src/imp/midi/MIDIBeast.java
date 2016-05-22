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

import imp.style.stylePatterns.RepresentativeChordRules;
import imp.style.stylePatterns.RepresentativeDrumRules;
import imp.style.stylePatterns.RepresentativeBassRules;
import imp.Constants;
import imp.data.ChordExtract;
import imp.data.ChordPart;
import imp.data.Duration;
import imp.data.Note;
import imp.data.SlottedNote;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utilities for MIDI
 * @author Brandy McMenamee, Jim Herold, Robert Keller
 */
public class MIDIBeast implements Constants
{

public static final int DRUM_CHANNEL = 9;

/**
 * All of these variables, except for beat, outght to be specifically set before
 * each main style generator occurs. They are initialize to defaults in order to
 * avoid potential un-anticipated bugs.
 */
public static jm.music.data.Score score;
public static int numerator = 4;
public static int denominator = 4;
public static String midiFileName = "";
public static String chordFileName = "";
public static ArrayList<jm.music.data.Part> allParts = new ArrayList<jm.music.data.Part>();
public static jm.music.data.Part drumPart = new jm.music.data.Part();
public static jm.music.data.Part bassPart = new jm.music.data.Part();
public static jm.music.data.Part chordPart = new jm.music.data.Part();
public static int whole;
public static int half;
public static int halftriplet; // rk
public static int quarter;
public static int quartertriplet; //rk
public static int eighth;
public static int eighthtriplet;
public static int sixteenth;
public static int sixteenthtriplet;
public static int thirtysecond;
public static int thirtysecondtriplet;
public static int sixtyfourth;
public static int sixtyfourthtriplet;
public static int quarterquintuplet;
public static int eighthquintuplet;
public static int sixteenthquintuplet;
public static int thirtysecondquintuplet;
public static int minPrecision = 10;
private static int precision = minPrecision;
private static int roundThreshold = 10; // Used in rounding bass patterns
public static int slotsPerMeasure = 480;
public static int drumMeasureSize = 480;

/**
 * Note that these lengths are in beats, but are converted to slots by getters.
 */
private static double maxBassPatternLength = 4;  // default
private static double maxChordPatternLength = 4; // default
private static double maxDrumPatternLength = 4;  // default

public static String[] pitches =
  {
    "c", "c#", "d", "d#", "e", "f",
    "f#", "g", "g#", "a", "a#", "b"
  };
public static ArrayList<SlottedNote> originalBassNotes = new ArrayList<SlottedNote>();
public static ArrayList<String> originalBassRules = new ArrayList<String>();
public static ArrayList<String> originalChordRules = new ArrayList<String>();
public static ArrayList<String> errors;
public static ArrayList<String> savingErrors;
public static ArrayList<SlottedNote> originalChordNotes;
private static RepresentativeBassRules repBassRules;
private static RepresentativeDrumRules repDrumRules;
private static RepresentativeChordRules repChordRules;
//Generation Preferences
public static boolean showExtraction = true;
public static boolean chordTones = true;
public static boolean mergeBassRests = false;
public static boolean importDrums = true;
public static boolean importBass = true;
public static boolean importChords = true;
public static boolean doubleDrumLength = false;
public static ArrayList<RepresentativeBassRules.BassPattern> selectedBassRules;
public static ArrayList<RepresentativeDrumRules.DrumPattern> selectedDrumRules;
public static ArrayList<RepresentativeChordRules.ChordPattern> selectedChordRules;
public static boolean invoked = false;

//used for chord extraction
public static boolean useLeadsheet = false;
public static ChordPart extractedChordPart = null;
//end

//chord extraction channel nums
private static int chordChannel;
private static int bassChannel;
private static int chordResolution = WHOLE;

public static final String[] GMinstrumentNames = 
  {
    "Acoustic_Grand_Piano",
    "Bright_Acoustic_Piano",
    "Electric_Grand_Piano",
    "Honky-tonk_Piano",
    "Electric_Piano_1",
    "Electric_Piano_2",
    "Harpsichord",
    "Clavi",
    "Celesta",
    "Glockenspiel",
    "Music_Box",
    "Vibraphone",
    "Marimba",
    "Xylophone",
    "Tubular_Bells",
    "Dulcimer",
    "Drawbar_Organ",
    "Percussive_Organ",
    "Rock_Organ",
    "Church_Organ",
    "Reed_Organ",
    "Accordion",
    "Harmonica",
    "Tango_Accordion",
    "Acoustic_Guitar_(nylon)",
    "Acoustic_Guitar_(steel)",
    "Electric_Guitar_(jazz)",
    "Electric_Guitar_(clean)",
    "Electric_Guitar_(muted)",
    "Overdriven_Guitar",
    "Distortion_Guitar",
    "Guitar_harmonics",
    "Acoustic_Bass",
    "Electric_Bass_(finger)",
    "Electric_Bass_(pick)",
    "Fretless_Bass",
    "Slap_Bass_1",
    "Slap_Bass_2",
    "Synth_Bass_1",
    "Synth_Bass_2",
    "Violin",
    "Viola",
    "Cello",
    "Contrabass",
    "Tremolo_Strings",
    "Pizzicato_Strings",
    "Orchestral_Harp",
    "Timpani",
    "String_Ensemble_1",
    "String_Ensemble_2",
    "SynthStrings_1",
    "SynthStrings_2",
    "Choir_Aahs",
    "Voice_Oohs",
    "Synth_Voice",
    "Orchestra_Hit",
    "Trumpet",
    "Trombone",
    "Tuba",
    "Muted_Trumpet",
    "French_Horn",
    "Brass_Section",
    "SynthBrass_1",
    "SynthBrass_2",
    "Soprano_Sax",
    "Alto_Sax",
    "Tenor_Sax",
    "Baritone_Sax",
    "Oboe",
    "English_Horn",
    "Bassoon",
    "Clarinet",
    "Piccolo",
    "Flute",
    "Recorder",
    "Pan_Flute",
    "Blown_Bottle",
    "Shakuhachi",
    "Whistle",
    "Ocarina",
    "Lead_1_(square)",
    "Lead_2_(sawtooth)",
    "Lead_3_(calliope)",
    "Lead_4_(chiff)",
    "Lead_5_(charang)",
    "Lead_6_(voice)",
    "Lead_7_(fifths)",
    "Lead_8_(bass_+_lead)",
    "Pad_1_(new_age)",
    "Pad_2_(warm)",
    "Pad_3_(polysynth)",
    "Pad_4_(choir)",
    "Pad_5_(bowed)",
    "Pad_6_(metallic)",
    "Pad_7_(halo)",
    "Pad_8_(sweep)",
    "FX_1_(rain)",
    "FX_2_(soundtrack)",
    "FX_3_(crystal)",
    "FX_4_(atmosphere)",
    "FX_5_(brightness)",
    "FX_6_(goblins)",
    "FX_7_(echoes)",
    "FX_8_(sci-fi)",
    "Sitar",
    "Banjo",
    "Shamisen",
    "Koto",
    "Kalimba",
    "Bag_pipe",
    "Fiddle",
    "Shanai",
    "Tinkle_Bell",
    "Agogo",
    "Steel_Drums",
    "Woodblock",
    "Taiko_Drum",
    "Melodic_Tom",
    "Synth_Drum",
    "Reverse_Cymbal",
    "Guitar_Fret_Noise",
    "Breath_Noise",
    "Seashore",
    "Bird_Tweet",
    "Telephone_Ring",
    "Helicopter",
    "Applause",
"Gunshot"  
  };

static
  {
    invoke();
  }


/**
 * All drum instruments supported by general midi. The indices + 35 correspond
 * to the general midi number of the instrument (e.g.: index 0 is "acoustic Bass
 * Drum," which has midi number 35).
 *
 * These names are used in style files, so that spaces are avoided.
 */
public static final String[] spacelessDrumName =
  {
    "Acoustic_Bass_Drum",
    "Bass_Drum_1",
    "Side_Stick",
    "Acoustic_Snare",
    "Hand_Clap",
    "Electric_Snare",
    "Low_Floor_Tom",
    "Closed_Hi-Hat",
    "High_Floor_Tom",
    "Pedal_Hi-Hat",
    "Low_Tom",
    "Open_Hi-Hat",
    "Low-Mid_Tom",
    "Hi-Mid_Tom",
    "Crash_Cymbal_1",
    "High_Tom",
    "Ride_Cymbal_1",
    "Chinese_Cymbal",
    "Ride_Bell",
    "Tambourine",
    "Splash_Cymbal",
    "Cowbell",
    "Crash_Cymbal_2",
    "Vibraslap",
    "Ride_Cymbal_2",
    "Hi_Bongo",
    "Low_Bongo",
    "Mute_Hi_Conga",
    "Open_Hi_Conga",
    "Low_Conga",
    "High_Timbale",
    "Low_Timbale",
    "High_Agogo",
    "Low_Agogo",
    "Cabasa",
    "Maracas",
    "Short_Whistle",
    "Long_Whistle",
    "Short_Guiro",
    "Long_Guiro",
    "Claves",
    "Hi_Wood_Block",
    "Low_Wood_Block",
    "Mute_Cuica",
    "Open_Cuica",
    "Mute_Triangle",
    "Open_Triangle"
  };

public static void setResolution(int resolution)
  {
    minPrecision = resolution;
    precision = resolution;
  }

public static void setChordResolution(int resolution)
  {
    chordResolution = resolution;
  }

public static int getResolution()
  {
    return precision;
  }

/**
 * @param String midiFile
 * @param String chordFile This method needs to be called before anything else
 * is done with MIDIBeast. It will take the midi and chord file and get basic
 * info about the song, Time Signature, note rhythm values, and all instruments
 * found in the song.
 */

public static void initialize(String midiFile, String chordFile)
  {
    invoke();
    midiFileName = midiFile;
    chordFileName = chordFile;

    jm.util.Read.midi(score, midiFileName);

    numerator = score.getNumerator();
    denominator = score.getDenominator();

      jm.music.data.Part[] temp = score.getPartArray();

      //use chord extraction, check if leadsheet is available
      if (chordFileName.isEmpty()||!useLeadsheet) {
          MidiChannelInfo midiChannelInfo = new MidiChannelInfo(midiFileName);
          bassChannel = midiChannelInfo.getBassChannel() + 1;
          chordChannel = midiChannelInfo.getChordChannel() + 1;
          ChordExtract chordextract = new ChordExtract(midiFileName, MIDIBeast.chordResolution,
                  bassChannel, chordChannel);
          extractedChordPart = chordextract.extract();
      }
    //end
    
    allParts.addAll(Arrays.asList(temp));

    drumMeasureSize = slotsPerMeasure = BEAT * numerator;
    if( maxDrumPatternLength != 0.0 )
      {
        drumMeasureSize = (int) (maxDrumPatternLength * BEAT);
      }
  }

    public static int getChordChannel() {
        return chordChannel;
    }

    public static int getBassChannel() {
        return bassChannel;
    }


/**
 * Calls null constructors on various objects of this class
 */
        
public static void invoke()
  {
    errors = new ArrayList<String>();
    savingErrors = new ArrayList<String>();
    score = new jm.music.data.Score();
    allParts = new ArrayList<jm.music.data.Part>();
    invoked = true;
  }

    public static void extractChords() {
        //use chord extraction, check if leadsheet is available
        //add error stuff if chord part or bass part can't be found

        //use midi import to extract melody parts from midi file
        if (chordFileName.isEmpty()||!MIDIBeast.useLeadsheet) {
            ChordExtract chordextract = new ChordExtract(midiFileName, chordResolution,
                    bassChannel, chordChannel);
            extractedChordPart = chordextract.extract();
        }
    }

public static void setChordChannel(int num)
{
    chordChannel = num;
}

public static void setBassChannel(int num)
{
    bassChannel = num;
}
/**
 * Changes the value of the denominator and recalculates the values of note
 * rhythms
 */

public static void changeDenomSig(int denom)
  {
    denominator = denom;
  }


/**
 * Changes the value of the numerator and recalculates the values of note
 * rhythms
 */

public static void changeNumSig(int num)
  {
    numerator = num;
  }


/**
 * Changes both the numerator and the denominator and recalculates the value of
 * note rhythms
 */

public static void changeTimeSig(int num, int denom)
  {
    numerator = num;
    denominator = denom;
  }




/**
 * @param numberOfSlots Takes an integer number of slots and returns a string
 * representation of it that Impro-Visor's style specification will understand.
 */

public static String stringDuration(int numberOfSlots)
  {
     if( numberOfSlots <= 0 )
      {
       //System.out.print("stringDuration(" + numberOfSlots +")");
       String result = "";
        System.out.println("exception noted, empty string returned" + result);
        new Exception("non-positive duration").printStackTrace();
        return result;
      }
     
    //System.out.print("stringDuration(" + numberOfSlots +")");
    
    StringBuilder buffer = new StringBuilder();
    Note.getDurationString(buffer, numberOfSlots);
    String result = buffer.toString();

    //System.out.println(" = " + result);
    return buffer.toString().substring(1);
  }



public static boolean drumInstrumentNumberValid(int number)
  {
  int index = number-35;
    
  if( index < 0 || index >= spacelessDrumName.length )
    {
      return false;
    }
  return true;
  }

public static String spacelessDrumNameFromNumber(int number)
  {
    int index = number-35;
    
    if( index < 0 || index >= spacelessDrumName.length )
      {
        return "#" + number + "_not_GM";
      }
    
    return spacelessDrumName[index];
  }


public static int numberFromSpacelessDrumName(String name)
  {
    for( int index = 0; index < spacelessDrumName.length; index++ )
      {
        if( name.equals(spacelessDrumName[index]) )
          {
            return index + 35;
          }
      }
    return -1; // not found
  }


/**
 * @param num Given an integer MIDI instrument number, this function returns the
 * String name of that instrument.
 */

public static String getInstrumentName(int num)
  {
    if( num < 0 || num >= GMinstrumentNames.length )
      {
        return "Unknown";
      }
    return GMinstrumentNames[num];
  }


/**
 * @param pitchNumber Returns the letter representation of a note given a MIDI
 * pitch number.
 */

public static String pitchOf(int pitchNumber)
  {
    int i = pitchNumber % 12;
    try
      {
        return pitches[i];
      }
    catch( ArrayIndexOutOfBoundsException e )
      {
      }

    return "r"; //rest
  }


/**
 * @param duration
 * @param precision Rounds a rhythm in double form to slots with a given
 * precision.
 * Note: precision can't be more than 10 to convert sixteenth notes correctly,
 * since 1/16 th is duration .25 and (120*.25 / 10)*10 = 30,
 * but (120*.25 / 20)*20 = 40, which is wrong.
 */

public static int findSlots(double duration, int precision)
  {
    int slots = (int) Math.round(BEAT * duration / precision) * precision;
 //System.out.println("duration " + duration + " -> " + slots + " slots, precision " + precision);
    return slots;
  }


/**
 * @param d Takes a double rhythm duration and turns it into an integer value
 * representing how many slots that note takes up.
 */

public static int doubleValToSlots(double duration)
  {
    return findSlots(duration, precision);
  }


/**
 * @param s Returns the number of slots a given string rhythm representation
 * takes.
 */

public static int getSlotValueForElement(String s)
  {
    String[] split = s.split("\\+");
    int slotTotal = 0;
    for( int i = 0; i < split.length; i++ )
      {
        slotTotal += getSlotValueFor(split[i]);
      }
    return slotTotal;
  }


public static boolean belowRoundingThreshold(String s)
  {
    return getSlotValueForElement(s) < roundThreshold;
  }

/**
 * @param ruleElement Takes a rule element (eg X8+32) and returns just the
 * rhythm part of it. (eg 8+32).
 */

public static String getRhythmString(String ruleElement)
  {
    if( ruleElement.length() == 0 ) // This CAN happen!
      {
        return "";
      }

    if( ruleElement.charAt(ruleElement.length() - 1) == ')' )
      {
        ruleElement = ruleElement.substring(0, ruleElement.length() - 1);
      }


    int rhythmIndex = ruleElement.indexOf(")");
    if( rhythmIndex == -1 )
      {
        for( int i = 0; i < ruleElement.length(); i++ )
          {
            if( ruleElement.charAt(i) > 47 && ruleElement.charAt(i) < 58 )
              {
                rhythmIndex = i;
                break;
              }
          }
      }
    else
      {
        rhythmIndex++;
      }

    // FIX!!! The following can generate an index out of range exception.
    // Putting on a temporary wrapper that returns "" for now.

    try
      {
        return ruleElement.substring(rhythmIndex, ruleElement.length());
      }
    catch( StringIndexOutOfBoundsException e )
      {
      }

    return "";
  }


/**
 * @param removeIndex
 * @param s Takes an array of Strings and removes the specified index
 */

public static String[] removeRule(int removeIndex, String[] s)
  {
    String[] toReturn = new String[s.length - 1];
    for( int i = 0, index = 0; i < s.length; i++ )
      {
        if( i != removeIndex )
          {
            toReturn[index++] = s[i];
          }
      }
    return toReturn;
  }


/**
 * @param rhythmString
 * @param elementString Takes a rule element(eg X8) and adds a rhythm element to
 * it (eg 32) TODO: Simplify the string after the rhythm is added
 */

public static String addRhythm(String rhythmString, String elementString)
  {
    return elementString + "+" + rhythmString;
  }


/**
 * @param rule Finds how many slots an entire string bass rule contains
 */

public static int numBeatsInBassRule(String rule)
  {
    int slotCount = 0;
    try
      {
        if( rule.length() == 0 )
          {
            return -1;
          }
        String[] split = rule.split(" ");
        for( int i = 0; i < split.length; i++ )
          {
            String rhythmString = "";
            if( split[i].equals("(X") )
              {
                rhythmString = getRhythmString(split[i + 2]);
                slotCount += getSlotValueForElement(rhythmString);
                i += 2;
              }
            else
              {
                rhythmString = getRhythmString(split[i]);
                slotCount += getSlotValueForElement(rhythmString);
              }
          }
      }
    catch( Exception e )
      {
        e.printStackTrace();
      }
    return slotCount;

  }
        

/**
 * @param s Takes a single string rhythm value (ie 8 or 16, not 16+32) and
 * returns the number of slots it contains
 */

public static int getSlotValueFor(String s)
  {
    return Duration.getDuration0(s);

  }


/**
 * @param s
 * @param note Takes the pitch number from a provided note object and returns a
 * String representation of it (eg D++)
 */

public static String pitchToString(String s, Note note)
  {
    if( note.getPitch() < 0 )
      {
        return "r";
      }
    int offset = 57;
    char octave = '+';
    s += pitchOf(note.getPitch() % 12);
    if( note.getPitch() > 71 || note.getPitch() < 60 )
      {
        if( note.getPitch() > 71 )
          {
            offset = 71;
            octave = '+';
          }
        else if( note.getPitch() < 60 )
          {
            offset = 60;
            octave = '-';
          }
        int lcv = (((offset - note.getPitch()) / 12) + 1);
        for( int i = 0; i < lcv; i++ )
          {
            s += octave;
          }
      }
    return s;
  }


/**
 * @param r Takes a string rule and returns the rhythm value in double form.
 */

public static double numBeatsInRule(String r)
  {
    ArrayList<String> rhythms = MIDIBeast.stripPitch(r);
    double sum = 0;
    for( int i = 0; i < rhythms.size(); i++ )
      {
        String[] note = rhythms.get(i).split("\\+");
        for( int j = 0; j < note.length; j++ )
          {
            double curVal = MIDIBeast.getSlotValueFor(note[j]);
            if( curVal == -1 )
              {
                return -1;
              }
            sum += curVal;
          }
      }
    return sum;
  }


/**
 * @param s - a bass rule
 * @return s without any octave or pitch value information
 */

public static ArrayList<String> stripPitch(String s)
  {
    String[] delimited = s.split(" ");
    ArrayList<String> rule = new ArrayList<String>();
    for( int i = 0; i < delimited.length; i++ )
      {
        String value = "";
        for( int j = 1; j < delimited[i].length(); j++ )
          {
            if( delimited[i].charAt(j) > 47 && delimited[i].charAt(j) < 58 && delimited[i].charAt(j - 1) != '(' )
              {
                value = delimited[i].substring(j, delimited[i].length());
                break;
              }
          }
        if( value.length() != 0 && value.charAt(value.length() - 1) == ')' )
          {
            value = value.substring(0, value.length() - 1);
          }

        rule.add(value);
      }
    return rule;
  }

public static void addError(String error)
  {
    errors.add(error);
  }

public static void savingErrors(String error)
  {
    savingErrors.add(error);
  }

public static void newSave()
  {
    savingErrors = new ArrayList<String>();
  }

public static void addSaveError(String error)
  {
    savingErrors.add(error + "\n");
  }

public static String getInstrumentForPart(int index)
  {
    return getInstrumentForPart(allParts.get(index));
  }

public static String getInstrumentForPart(jm.music.data.Part part)
  {
    if( part.getChannel() == DRUM_CHANNEL )
      {
        return spacelessDrumNameFromNumber(part.getInstrument());
      }
    else
      {
      return getInstrumentName(part.getInstrument());
      }
  }

public static void setMaxBassPatternLength(double beats)
  {
    maxBassPatternLength = beats;
  }

public static void setMaxChordPatternLength(double beats)
  {
    maxChordPatternLength = beats;
  }

public static void setMaxDrumPatternLength(double beats)
  {
    maxDrumPatternLength = beats;
  }

public static int getMaxBassPatternLengthInSlots()
  {
    return (int)(BEAT*maxBassPatternLength);
  }

public static int getMaxChordPatternLengthInSlots()
  {
    return (int)(BEAT*maxChordPatternLength);
  }

public static int getMaxDrumPatternLengthInSlots()
  {
    return (int)(BEAT*maxDrumPatternLength);
  }

public static RepresentativeBassRules getRepBassRules()
  {
    return repBassRules;
  }

public static RepresentativeChordRules getRepChordRules()
  {
    return repChordRules;
  }

public static RepresentativeDrumRules getRepDrumRules()
  {
    return repDrumRules;
  }

public static void setRepBassRules(RepresentativeBassRules rules)
  {
    repBassRules = rules;
  }

public static void setRepChordRules(RepresentativeChordRules rules)
  {
    repChordRules = rules;
  }

public static void setRepDrumRules(RepresentativeDrumRules rules)
  {
    repDrumRules = rules;
  }

}

