/*
 * Convert.java 0.2 30th June 2002
 *
 * Copyright (C) 2001 Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.util;

import java.util.Enumeration;
import java.util.Vector;
import java.lang.reflect.Field;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.constants.Frequencies;
import jm.constants.Pitches;

/**
  * Static methods allowing conversion of standard JMusic data types (like 
  * Phrase) to and from XML.
  *
  * Also supports a separate custom encoding for pitch and rhythm values pairs
  * which is much terser than XML.
  *
  * @author Adam Kirby
  * @version 0.2, 30th June 2002
  */
public class Convert {
    
    private Convert() {
    }

    //--- Key signature/scale degree conversions ---//

//    /**
//     * Integer constant describing the number of semitone intervals in any
//     * given octave.
//     */
//    private static final int SEMITONES_PER_OCTAVE = 12;
//
//    /**
//     * Returns the scale degree of the specified pitch in the specified key
//     * signature.
//     *
//     * @param note          Note whose pitch is to represented as a degree.
//     * @param keySignature  KeySignature to be compared with
//     * @return              Integer describing the pitch of the note, relative
//     *                      to the tonic of the key signature.
//     */
//    private static int noteToScaleDegree(
//                    final Note note,
//                    final KeySignature keySignature)
//                throws
//                    ConversionException {
//        int tonic = keySignature.getTonic();
//        int pitch = note.getPitch();
//        if (pitch == Note.REST) {
//            throw new ConversionException(
//                    "Note is a rest.  Cannot convert to scale degree.");
//        }
//
//        // Make pitch relative to the tonic
//        pitch -= tonic;
//
//        // Pitch must be positive for % function to work correctly
//        if (pitch < 0) {
//
//            // Give pitch a positive value with an equivalent degree of the 
//            // scale
//            pitch += ((-pitch / SEMITONES_PER_OCTAVE) + 1) 
//                     * SEMITONES_PER_OCTAVE;
//        }
//
//        return pitch % SEMITONES_PER_OCTAVE;
//    }
      
    //--- String encoding conversions ---//

    public static final String DEFAULT_SEPARATOR = ",";

    public static final String LEFT_BRACKET = "[";

    public static final String RIGHT_BRACKET = "]";

    /**
     * Converts a list of pitch and rhythm-value pairs, each value separated by
     * a non-digit, to the Phrase that those values represent.
     *
     * @param string    String of value separated by commas
     * @return          Phrase described by the String.
     */
    public static Phrase pitchAndRhythmStringToPhrase(final String string) {
        StringProcessor processor = new StringProcessor(string);
        Phrase phrase = new Phrase();
        try {
            while (true) {
                phrase.addNote(new Note(
                        (int) processor.getNextRhythm(), processor.getNextRhythm()));
            }
        } catch (EOSException e) {
            /* This is okay.  Continue. */
        }
        return phrase;
    }

    /**
     * Converts a Phrase, to a list of comma separated values alternately
     * describing the pitch and rhyhtm value of each Note in the phrase.
     *
     * @param phrase    Phrase to be converted
     * @return          String describing the Phrase
     */
    public static String phraseToPitchAndRhythmString(final Phrase phrase) {
        Note[] noteArray = phrase.getNoteArray();

        /*
         * Assuming each pitch and rhythm value pair take no more than 10
         * characters to describe.
         */
        StringBuffer stringBuffer = new StringBuffer(noteArray.length * 10);

        /* Describe all but the last note */
        for (int i = 0; i < (noteArray.length - 1); i++) {
//            stringBuffer.append(LEFT_BRACKET);
            stringBuffer.append(noteArray[i].getPitch());
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(
                    limitDecimalPlaces(noteArray[i].getRhythmValue(), 3));
//            stringBuffer.append(RIGHT_BRACKET);
            stringBuffer.append(DEFAULT_SEPARATOR);
        }

        if (noteArray.length > 0) {
            /* Describe the final note */
//            stringBuffer.append(LEFT_BRACKET);
            stringBuffer.append(noteArray[noteArray.length - 1].getPitch());
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(
                    limitDecimalPlaces(
                            noteArray[noteArray.length - 1].getRhythmValue(),
                            3));
//            stringBuffer.append(RIGHT_BRACKET);
        }

        return stringBuffer.toString();
    }

    /**
     * Converts a list of pitch, rhythm-value and dynamic sets, each value
     * separated by a non-digit, to the Phrase that those values represent.
     *
     * @param string    String of value separated by commas
     * @return          Phrase described by the String.
     */
    public static Phrase pitchRhythmAndDynamicStringToPhrase(final String string) {
        StringProcessor processor = new StringProcessor(string);
        Phrase phrase = new Phrase();
        try {
            while (true) {
                phrase.addNote(new Note(
                        (int) processor.getNextRhythm(),
                        processor.getNextRhythm(),
                        (int) processor.getNextRhythm()));
            }
        } catch (EOSException e) {
            /* This is okay.  Continue. */
        }
        return phrase;
    }

    /**
     * Converts a Phrase, to a list of comma separated values alternately
     * describing the pitch, rhyhtm value and dynamic of each Note in the
     * phrase.
     *
     * @param phrase    Phrase to be converted
     * @return          String describing the Phrase
     */
    public static String phraseToPitchRhythmAndDynamicString(
                final Phrase phrase) {
        Note[] noteArray = phrase.getNoteArray();

        /*
         * Assuming each pitch and rhythm value pair take no more than 10
         * characters to describe.
         */
        StringBuffer stringBuffer = new StringBuffer(noteArray.length * 12);

        /* Describe all but the last note */
        for (int i = 0; i < (noteArray.length - 1); i++) {
            stringBuffer.append(LEFT_BRACKET);
            stringBuffer.append(noteArray[i].getPitch());
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(
                    limitDecimalPlaces(noteArray[i].getRhythmValue(), 3));
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(noteArray[i].getDynamic());
            stringBuffer.append(RIGHT_BRACKET);
            stringBuffer.append(DEFAULT_SEPARATOR);
        }

        if (noteArray.length > 0) {
            /* Describe the final note */
            stringBuffer.append(LEFT_BRACKET);
            stringBuffer.append(noteArray[noteArray.length - 1].getPitch());
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(
                    limitDecimalPlaces(
                            noteArray[noteArray.length - 1].getRhythmValue(),
                            3));
            stringBuffer.append(DEFAULT_SEPARATOR);
            stringBuffer.append(noteArray[noteArray.length - 1].getDynamic());
            stringBuffer.append(RIGHT_BRACKET);
        }

        return stringBuffer.toString();
    }

//    /** Old, to be removed */
//    public static String scoreToHttpGetEncodedString(final Score score) {
//        StringBuffer buffer = new StringBuffer();
//        int count = 0;
//        int scoresize = score.size();
//        for (int i = 0; i < scoresize; i++) {
//            Part part = score.getPart(i);
//            int partsize = part.size();
//            for (int j = 0; j < partsize; j++) {
//                Phrase phrase = part.getPhrase(j);
//                count++;
//                buffer.append("PnRV");
//                buffer.append(count);
//                buffer.append("=");
//                buffer.append(Convert.phraseToPitchAndRhythmString(phrase));
//                buffer.append("&");
//            }
//        }
//        return buffer.toString();
//    }

    static String limitDecimalPlaces(final double d,
                                     final int places) {
        String dString = Double.toString(d);
        int lastIndex = dString.lastIndexOf(".") + places + 1;
        if (lastIndex > dString.length()) {
            lastIndex = dString.length();
        }
        return dString.substring(0, lastIndex);
    }

    // previously EOSException,
    // length reduced to resolve pre-OSX MacOS filename length limitations
    private static class EOSException extends Exception {
    }

    // previously PitchAndRhythmStringProcessor,
    // length reduced to resolve pre-OSX MacOS filename length limitations
    private static class StringProcessor {
        private int i = 0;

        private String string;
        
        StringProcessor(final String string) {
            this.string = string;
        }

        private int getNextPitch() throws ConversionException,
                                          EOSException {
            StringBuffer buffer = new StringBuffer();
            try {
                /* Ignore leading non-digit characters */
                while (! Character.isDigit(string.charAt(i++))) {
                }

                buffer.append(string.charAt(i - 1));
                while (Character.isDigit(string.charAt(i++))) {
                    buffer.append(string.charAt(i - 1));
                }

                if (string.charAt(i - 1) == '.') {
                    throw new ConversionException("Double value not expected");
                }

                return Integer.parseInt(buffer.toString());

            } catch (IndexOutOfBoundsException e) {
                if (buffer.length() > 0) {
                    return Integer.parseInt(buffer.toString());
                }
                throw new EOSException();
            }
        }

        private double getNextRhythm() throws EOSException {
            StringBuffer buffer = new StringBuffer();
            try {
                /* Ignore leading non-digit characters */
                while (! Character.isDigit(string.charAt(i++))
                        && string.charAt(i) != '.') {
                }

                buffer.append(string.charAt(i - 1));
                while (Character.isDigit(string.charAt(i))
                        || string.charAt(i) == '.') {
                    buffer.append(string.charAt(i));
                    i++;
                }

                return Double.valueOf(buffer.toString()).doubleValue();

            } catch (IndexOutOfBoundsException e) {
                if (buffer.length() > 0) {
                    return Double.valueOf(buffer.toString()).doubleValue();
                }
                throw new EOSException();
            }
        }
    }

    //--- XML conversions ---//

    public static String scoreToXMLString(final Score score) {
            return XMLParser.scoreToXMLString(score);
    }

    public static String partToXMLString(final Part part) {
            return XMLParser.partToXMLString(part);
    }

    public static String phraseToXMLString(final Phrase phrase) {
            return XMLParser.phraseToXMLString(phrase);
    }

    public static String noteToXMLString(final Note note) {
            return XMLParser.noteToXMLString(note);
    }


    public static Score xmlStringToScore(final String string)
                                  throws ConversionException 
    {
            return XMLParser.xmlStringToScore(string);
    }
    
    public static Part xmlStringToPart(final String string) 
                                throws ConversionException 
    {
            return XMLParser.xmlStringToPart(string);
    }
    
    public static Phrase xmlStringToPhrase(final String string) 
                                    throws ConversionException
    {
            return XMLParser.xmlStringToPhrase(string);
    }
    
    public static Note xmlStringToNote(final String string)
                                throws ConversionException
    {
            return XMLParser.xmlStringToNote(string);
    }

    // Addition by Andrew Brown

    /**
     * Get the frequency of a given MIDI pitch
     *
     * @param midiPitch pitch value from 0 - 127
     * @return frequency a value in hertz
     * @see Class description of {@link Pitches}.
     * @see Class description of {@link Frequencies}.
     */
    public static final float getFrequencyByMidiPitch(final int midiPitch) {
	float freq = -1.0f;
	if (midiPitch >= 0 && midiPitch <= 127) {
	    freq = (float)(6.875 * Math.pow(2.0, ((3 + midiPitch) / 12.0)));
	}
	return freq;
    }

    /*
     * Pitch and frequency conversions
     * These methods contributed by Marcel Karras, Oct 2008.
     */

    /**
     * Get the midi pitch of a given frequency.
     *
     * @param frequency frequency value in Hz
     * @return pitch a value from 0..127
     * @see Class description of {@link Pitches}.
     * @see Class description of {@link Frequencies}.
     */
    public static final int getMidiPitchByFrequency(final float frequency) {
	// check frequency bounds
	// lower bound: frequency(CN1) / (2^(1/12))
	// upper bound: frequency(G9) * (2^(1/12))
	float powerOfTwo = (float) Math.pow(2, (1f / 12f));
	if (frequency <(Frequencies.FRQ[Pitches.CN1] / powerOfTwo) ||
	    frequency > (Frequencies.FRQ[Pitches.G9] * powerOfTwo)) {
	    return -1;
	}
	// round to the best matching pitch value (0..127)
	int pitch = Math.round((float) (12 *(Math.log(frequency / 6.875) / Math.log(2)) - 3));
	return pitch;
    }
    
    /**
     * Get the midi note name for a given pitch value.
     *
     * @param pitch a value from 0..127
     * @return midi note name like in {@link Pitches}
     * @see Class description of {@link Pitches}.
     * @see Class description of {@link Frequencies}.
     */
    public static final String getNameOfMidiPitch(final int pitch) {
	// use java reflection API
	final Field[] fields = Pitches.class.getFields();
	if (fields != null) {
	    for (int i=0; i<fields.length; i++) {
		Field f = fields[i];
		// try to find the pitch member variable
		try {
		    if (f.getInt(null) == pitch)
			return f.getName();
		} catch (IllegalArgumentException e) {
		    return "";
		} catch (IllegalAccessException e) {
		    return "";
		}
	    }
	}
	return "";
    }
}