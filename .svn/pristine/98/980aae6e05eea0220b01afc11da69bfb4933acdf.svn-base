/*
<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.
This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public Licens
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package jm.music.data;

import java.io.Serializable;

/**
 * The Note class is representative of notes in standard western
 * music notation. It contains data relavent to music note information
 * such as time, duration and pitch. Notes get contained in Phrase
 * objects. Like in tradtional music notation (CPN) notes get played
 * one after the other in the order in which they are added to the
 * Phrase. <br>
 * !IMPORTANT: notes with a pitch of the minimum integer are rests,
 * those between 0 <> 127 are exactly the same as normal sounding
 * notes numbered like the MIDI specification. Notes with a pitch
 * specified as a double value, e.g., 440.0, are frequency values
 * for the note pitch. In general, note pitch refers to the chromatic
 * key number (as per MIDI) as an int and the note frequency
 * refer to the value in hertz as a double value. <br>
 * Notes can be added to a phrase like this...
 * <pre>
 *     Phrase phrase = new Phrase(0.0);
 *     Note note = new Note(C4, CROTCHET, MF);
 *     phrase.addNote(note);
 * </pre>
 * The note also has the option to create notes with reasonable default values.
 * This allows a user who is not interested in any performance details to work
 * with notes solely using pitch value and rythmValue like this....
 * <pre>
 *     Note note = new Note(C4, CROTCHET);
 * </pre>
 * The final option for building a note also includes the dynamic
 * parameter for minimal performace information.
 * <pre>
 *     Note note = new Note(C4, CROTCHET, F);
 * </pre>
 *
 * <B>Comments about the offset parameter:</B>
 * The intention of offset was to allow for 'feel' in score playback.
 * For example, a snare drum might be played slightly ahead or behind
 * the beat, or a sound with a slow attack might need to be triggered
 * early in order to sound in time with other parts.
 *
 * With this in mind, offset should have no influence on rhythmValue or
 * duration calculations within the jMusic data structure, only when
 * translated to another format (eg MIDI). In your
 * example (offset 0.1, duration 0.8, and rv 1.0) the rendering
 * (MIDI or other that does not support the offset concept) should
 * consider the start time of the note 0.1 bpm later than
 * 'normal' but the duration of the note will be 0.8 bpm from that
 * offset start time. The next note should start 1.0 bpm after the
 * 'normal' startTime (ignoring offset). This allows for
 * offset to be used in cases where performance of music varies
 * from beat to beat, eg., the downbeat is anticipated slightly. In
 * extreme cases this could cause overlapping notes in
 * MIDI translation which need to be 'handled' but we consider this a
 * weakness of the MIDI data specification rather than a problem for
 * jMusic : )
 *
 * In short, think of offset as "A displacement from the normal start
 * time of a note that allows performance interpretation without distorting
 * the score for compositional or analysis purposes."
 *---------------
 * @see Phrase
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:43:31  2001
 */

public class Note implements Cloneable, Serializable{
    //----------------------------------------------
    // Defaults
    //-----------------------------------------------

    /** default pitch value */
    public static final int DEFAULT_PITCH = 60;

    /** default rhythmValue*/
    public static final double DEFAULT_RHYTHM_VALUE = 1.0;
	
    /** default dynamic*/
    public static final int DEFAULT_DYNAMIC = 85;

    /** default pan value*/
    public static final double DEFAULT_PAN = 0.5;

    /** default duration multiplier */
    // should be deprecated in favour of the more musically meaning term
    // DEFAULT_ARTICULATION
    public static final double DEFAULT_DURATION_MULTIPLIER = 0.9;

    /** Default articulation.  This field is meant to replace
     * DEFAULT_DURATION_MULTIPLIER.
     */
    public static final double DEFAULT_ARTICULATION = 0.9;

    /** default duration*/
    public static final double DEFAULT_DURATION =
	DEFAULT_RHYTHM_VALUE * DEFAULT_DURATION_MULTIPLIER;

    /** default offset value*/
    public static final double DEFAULT_OFFSET = 0.0;

    /** The number of seconds into a sample to begin reading data */
    public static final double DEFAULT_SAMPLE_START_TIME = 0.0;

    //----------------------------------------------
    // Constants
    //-----------------------------------------------
	
    /** The smallest value for a pitch. */
    public final static int MIN_PITCH = 0;
    /** The smallest value for a frequency. */
    public final static double MIN_FREQUENCY = 0.00000000000000001;
    /** The largest value for a pitch. */
    public final static double MAX_MIDI_PITCH = 127.0;
    /** The largest value for a pitch. */
    public final static int MAX_PITCH = 127;
    /** The smallest value for a rhythmValue. */
    public final static double MIN_RHYTHM_VALUE = 0.0;
    /** The largest value for a rhythValue. */
    public final static double MAX_RHYTHM_VALUE= Double.MAX_VALUE;
    /** The smallest value for a dynamic. */
    public final static int MIN_DYNAMIC = 0;
    /** The largest value for a dynamic. */
    public final static int MAX_DYNAMIC = 127;
    /** The smallest pan value for a note. */
    public final static double MIN_PAN = 0.0;
    /** The largest pan value for a note. */
    public final static double MAX_PAN = Double.MAX_VALUE;
    /** The smallest value for a note's duration. */
    public final static double MIN_DURATION = 0.0;
    /** The largest value for a note's duration. */
    public final static double MAX_DURATION = Double.MAX_VALUE;
    /** The pitch value which indicates a rest. */
    public static final int REST = Integer.MIN_VALUE;
    /** Pitch type flag - indicates that pitch should be a frequency in hertz.*/
    public static final boolean FREQUENCY = true;
    /** Pitch type flag - indicates that pitch should be MIDI note number. */
    public static final boolean MIDI_PITCH = false;
    /** Specified envelope break point value indexes */
    public static final int AMP_ENV = 0;
    public static final int PITCH_ENV = 1;
    public static final int FILTER_ENV = 2;
    public static final int PAN_ENV = 3;

    /** string constants for keys on the keyboard */
    public static final String C = "C";
    public static final String G = "G";
    public static final String D = "D";
    public static final String A = "A";
    public static final String E = "E";
    public static final String B = "B";
    public static final String F_SHARP = "F#";
    public static final String C_SHARP = "C#";
    public static final String G_SHARP = "Ab";
    public static final String D_SHARP = "Eb";
    public static final String A_SHARP = "Bb";
    public static final String A_FLAT = "Ab";
    public static final String E_FLAT = "Eb";
    public static final String B_FLAT = "Bb";
    public static final String F = "F";
	
    /** the string that this note maps to being one of the 12 string constants */
    private String noteString = "";
	
    //----------------------------------------------
    // Attributes
    //----------------------------------------------
    /** Pitch/frequency value of the note */
    private double pitch;
    /** Dynamic value ranging from 0-127 (0 = off; 1 = quiet; 127=loud) */
    private int dynamic;
    /**
     * The length of the note. Constant values representing standard note
     * lengths are defined in JMC
     */
    private double rhythmValue;
    /** 0.0 (full left) 0.5 (center) 1.0 (full right) */
    private double pan;
    /** A notes duration of time in beats */
    private double duration;
    /**
     * A note's offset time. How far in front or behind of the rhythmic value
     * should we place this note
     */
    private double offset;
    /** The time into a sample for playback to begin in milliseconds - for audio only */
    private double sampleStartTime;
    /** The type of value that pitch is representing - frequency (true) or MIDI note (false) */
    private boolean pitchType;
    /** The phrase that this note has been added to. */
    private Phrase myPhrase = null;
    /** An array of break point envelope values to be used by this note
     * - see Note constants for specified index allocations */
    private double[][] breakPoints = new double[64][];

    /**
     * Default constructor assigns null values to all note attributes
     */
    public Note() {
        this(DEFAULT_PITCH, DEFAULT_RHYTHM_VALUE);
        this.pitch = DEFAULT_PITCH;
        this.pitchType = MIDI_PITCH;
        this.rhythmValue = DEFAULT_RHYTHM_VALUE;
        this.dynamic = DEFAULT_DYNAMIC;
        this.pan = DEFAULT_PAN; //centre pan
        this.duration = DEFAULT_DURATION;
        this.offset = DEFAULT_OFFSET;
    }

    /**
     * Assigns pitch and rhythmic values to the note object upon creation
     * Other values (e.g. dynamic) are given reasonable defaults
     * @param MIDI pitch range is 0-127 (middle c = 60): Constant values
     *              representing pitch values can be found in JMC
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                 duration types can be found in JMC
     */
    public Note(int pitch, double rhythmValue){
        this(pitch, rhythmValue, DEFAULT_DYNAMIC);
    }

    /**
     * Assigns pitch and rhythmic values to the note object upon creation
     * Other values (e.g. dynamic) and given reasonable defaults
     * @param MIDI pitch range is 0-127 (middle c = 60): Constant values
     *              representing pitch values can be found in JMC
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                    duration types can be found in JMC
     * @param dynamic range is 0-127 (0 = off; 127 = loud): Constant values
     *               representing some basic dynamic types can be found in JMC
     */
    public Note(int pitch, double rhythmValue, int dynamic){
        this(pitch, rhythmValue, dynamic, DEFAULT_PAN);
    }
	
    /**
     * Assigns pitch and rhythmic values to the note object upon creation
     * Other values (e.g. dynamic) and given reasonable defaults
     * @param MIDI pitch range is 0-127 (middle c = 60): Constant values
     *              representing pitch values can be found in JMC
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                    duration types can be found in JMC
     * @param dynamic range is 0-127 (0 = off; 127 = loud): Constant values
     *               representing some basic dynamic types can be found in JMC
     * @param pan Specifies the balance between output channels;
     * 		usually between 0 - left, and 1 - right.
     */
    public Note(int pitch, double rhythmValue, int dynamic, double pan){
        if (pitch < MIN_PITCH && pitch > REST + 2) {
            // bit of a hack to cater for casting error
            System.err.println("jMusic Note constructor error: Pitch is"
                               + " " +pitch+", it must be no less than "
                               +  MIN_PITCH + " (REST = " + REST+ ")");
            System.exit(1);
        }
        this.pitchType = MIDI_PITCH;
        this.pitch = (double)pitch;
        this.rhythmValue = rhythmValue;
        this.dynamic = (dynamic < MIN_DYNAMIC)
            ? MIN_DYNAMIC
            : ((dynamic > MAX_DYNAMIC) ? MAX_DYNAMIC : dynamic);
        this.pan = pan;
        this.duration = rhythmValue * DEFAULT_DURATION_MULTIPLIER;
        this.offset = DEFAULT_OFFSET;
        //this.sampleStartTime = DEFAULT_SAMPLE_START_TIME;
    }

    /**
     * Assigns frequency and rhythmic values to the note object upon creation
     * @param frequency Pitch in hertz, any double value (A4 = 400)
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                 duration types can be found in JMC
     */
    public Note(double frequency, double rhythmValue){
        this(frequency, rhythmValue, DEFAULT_DYNAMIC);
    }

    /**
     * Assigns frequency and rhythmic values to the note object upon creation
     * @param frequency Pitch in hertz, any double value (e.g., A4 = 440.0)
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                 duration types can be found in JMC
     * @param dynamic range is 0-127 (0 = off; 127 = loud): Constant values
     *               representing some basic dynamic types can be found in JMC
     */
    public Note(double frequency, double rhythmValue, int dynamic){
        this(frequency, rhythmValue, dynamic, DEFAULT_PAN);
    }

    /**
     * Assigns frequency and rhythmic values to the note object upon creation
     * @param frequency Pitch in hertz, any double value (e.g., A4 = 440.0)
     * @param rhythmValue 0.5 = quaver: constant values representing most
     *                 duration types can be found in JMC
     * @param dynamic range is 0-127 (0 = off; 127 = loud): Constant values
     *               representing some basic dynamic types can be found in JMC
     * @param pan Specifies the balance between output channels;
     * 		usually between 0 - left, and 1 - right.
     */
    public Note(double frequency, double rhythmValue, int dynamic, double pan){
        if (frequency > MIN_FREQUENCY) {
            this.pitch = frequency;
        } else {
            System.err.println("jMusic Note constructor error: Frequency is " +
                               frequency + ", it must be greater than " + MIN_FREQUENCY + " hertz.");
            System.exit(1);
        }
        this.pitchType = FREQUENCY;
        this.rhythmValue = rhythmValue;
        this.dynamic = (dynamic < MIN_DYNAMIC)
            ? MIN_DYNAMIC
            : ((dynamic > MAX_DYNAMIC) ? MAX_DYNAMIC : dynamic);
        this.pan = pan;
        this.duration = rhythmValue * DEFAULT_DURATION_MULTIPLIER;
        this.offset = DEFAULT_OFFSET;
        //this.sampleStartTime = DEFAULT_SAMPLE_START_TIME;
    }
	
    public Note(String note) {
        super();
        noteString = note;
        setPitch(getPitchValue());
    }


    //----------------------------------------------
    // Data Methods
    //----------------------------------------------
    /**
     * Retrieve note's pitch type
     * @return boolean notes pitch type
     */
    public boolean getPitchType(){
        return this.pitchType;
    }

    /**
     * Specifies the note's pitch type.
     * There are constants for FREQUENCY and MIDI_PITCH
     * @param boolean note's pitch type
     */
    public void setPitchType(boolean newType){
        this.pitchType = newType;
    }



    /**
     * Retrieve the note's pitch as a frequency
     * @return double note's pitch
     */
    public double getFrequency(){
        double frq = this.pitch;
        if (this.pitchType == MIDI_PITCH && this.pitch != (double)REST && 
			this.pitch <= MAX_PITCH && this.pitch >= MIN_PITCH)
            frq = jm.JMC.FRQ[(int)this.pitch];
        if (this.pitch == (double)REST) frq = (double)REST;
        return frq;
    }

    /**
     * Assign notes pitch as a frequency.  If the parameter <CODE>pitch</CODE> is less than
     * {@link #MIN_FREQUENCY} then the pitch of this note will be set to MIN_FREQUENCY.
     *
     * @param double note pitch as a frequency in hertz
     */
    public void setFrequency(double freq){
        try {
            this.pitch = (pitch < MIN_FREQUENCY)
                ? MIN_FREQUENCY : freq;
            pitchType = FREQUENCY;
        }catch(RuntimeException re){
            System.err.println("Error setting note value: " +
                               "You must enter frequency values above "+ MIN_FREQUENCY);
            System.exit(1);
        }
		
    }

    /**
     * Retrieve the note's pitch as an integer.
     * Useful for working with pitch as MIDI note numbers.
     * @return int note's pitch
     */
    public int getPitch(){
        if (pitchType == FREQUENCY && this.pitch != (double)REST) {
            System.err.println("jMusic error getting Note pitch: Pitch is a frequency - " +
                               "getPitch() can't be used.");
            System.exit(1);
            //return 0; // to do - calculation to work out MIDI note from freq.
        }
        int val;
        if (this.pitch < (double)(REST + 2)) // allow for some rounding error
            val = REST;
        else val = (int)this.pitch;
        return val;
    }

    /**
     * Assign notes pitch.  If the parameter <CODE>pitch</CODE> is less than
     * {@link #MIN_PITCH} then the pitch of this note will be set to MIN_PITCH.
     * Likewise, if <CODE>pitch</CODE> is greater than {@link #MAX_MIDI_PITCH},
     * pitch will be set to MAX_MIDI_PITCH.
     *
     * @param int notes pitch
     */
    public void setPitch(int pitch){
        if (pitch == REST)
            this.pitch = (double)REST;
        else {
            try {
                this.pitch = (pitch < MIN_PITCH)
                    ? MIN_PITCH
                    : ((pitch > MAX_MIDI_PITCH) ? MAX_MIDI_PITCH : pitch);
            }catch(RuntimeException re){
                System.err.println("Error setting pitch value: " +
                                   "You must enter pitch values between "
                                   + MIN_PITCH + " and " + MAX_MIDI_PITCH);
            }
        }
        pitchType = MIDI_PITCH;
    }

    /**
     * Retrieve note's rhythmValue
     * @return float notes rhythmValue
     */
    public double getRhythmValue(){
        return this.rhythmValue;
    }

    /**
     * Assign notes rhythmValue
     * @param float notes rhythmValue
     */
    public void setRhythmValue(double rhythmValue){
        this.rhythmValue = (rhythmValue < MIN_RHYTHM_VALUE)
            ? MIN_RHYTHM_VALUE
            : ((rhythmValue > MAX_RHYTHM_VALUE)
               ? MAX_RHYTHM_VALUE
               : rhythmValue);
    }
	
    /**
     * Retrieve notes dynamic
     * @return int notes dynamic
     */
    public int getDynamic(){
        return this.dynamic;
    }

    /**
     * Assign notes dynamic
     * @param int notes dynamic
     */
    public void setDynamic(int dynamic){
        this.dynamic = (dynamic < MIN_DYNAMIC)
            ? MIN_DYNAMIC
            : ((dynamic > MAX_DYNAMIC) ? MAX_DYNAMIC : dynamic);
    }

    /**
     * Retrieves note's pan. 0.0 (full left) 0.5 (center) 1.0 (full right)
     * @return notes pan
     */
    public double getPan(){
        return this.pan;
    }

    /**
     * Assign notes pan. 0.0 (full left) 0.5 (center) 1.0 (full right).
     * @param double note's pan
     */
    public void setPan(double pan){
        this.pan = (pan < MIN_PAN)
            ? MIN_PAN
            : ((pan > MAX_PAN) ? MAX_PAN : pan);
    }

    /**
     * Return note duration. 1.0 = Crotchet (Quater Note), 0.5 = Quaver (Eighth Note), etc...
     * @return double note's duration
     */
    public double getDuration(){
        return this.duration;
    }

    /**
     * Set notes duration. 1.0 = Crotchet (Quater Note), 0.5 = Quaver (Eighth Note), etc...
     * @param double note's duration
     */
    public void setDuration(double duration){
        this.duration = (duration < MIN_DURATION)
            ? MIN_DURATION
            : ((duration > MAX_DURATION) ? MAX_DURATION : duration);
    }
	
    /**
     * Return note offset.
     * The range is 0 = no change, positive number delay the note, negative values rush (advance) it
     * @return double note's offset
     */
    public double getOffset(){
        return this.offset;
    }
	
    /**
     * Set notes offset.
     * The range is 0 = no change, positive number delay the note, negative values rush (advance) it
     * @param double note's offset
     */
    public void setOffset(double offset){
        this.offset = offset;
    }
	
    /**
     * Return note sampleStartTime
     * @return int note's sampleStartTime
     */
    public double getSampleStartTime(){
        return this.sampleStartTime;
    }

    /**
     * Set notes sampleStartTime
     * @param int note's sampleStartTime
     */
    public void setSampleStartTime(double sampleStartTime){
        this.sampleStartTime = sampleStartTime;
    }
	
    /** Sets a reference to the phrase that contains this note */
    public void setMyPhrase(Phrase phr){
        this.myPhrase = phr;
    }
	
    /** Return a reference to the phrase containing this note */
    public Phrase getMyPhrase(){
        return this.myPhrase;
    }
	
    /**
     * Returns a copy of this note
     * @return Note
     */
    public Note copy(){
        Note note;
        if (pitchType == MIDI_PITCH) {
            note = new Note(this.getPitch(), this.rhythmValue, this.dynamic);
        } else note = new Note(this.getFrequency(), this.rhythmValue, this.dynamic);
        note.setPan(this.pan);
        note.setDuration(this.duration);
        note.setOffset(this.offset);
        note.setSampleStartTime(this.sampleStartTime);
        note.setMyPhrase(this.myPhrase);
        for (int i=0; i<breakPoints.length; i++) {
            if (this.breakPoints[i] != null ) note.setBreakPoints(i, this.getBreakPoints(i));
        }
        return note;
    }
	
    /**
     * Specify the values for a break point envelope.
     * Some indexes of the breakPoints array are reserved for
     * specific purposes. See the Note constants for a list of these.
     * @param index The specific breakPoint number to set.
     * @param points The values for this break point array.
     */
    public void setBreakPoints(int index, double[] points) {
        if (index < 0 || index > breakPoints.length) {
            System.err.println("jMusic Note error: BreakPoint index "+ index +
                               " is out of range when setting.");
            System.exit(1);
        }
        this.breakPoints[index] = points;
    }
	
    /**
     * Retrieve the break point envelope values.
     * Some indexes of the breakPoints array are reserved for
     * specific purposes. See the Note constants for a list of these.
     * @return double[] The break oint values for the specified index
     */
    public double[] getBreakPoints(int index) {
        if (index < 0 || index > breakPoints.length) {
            System.err.println("jMusic Note error: BreakPoint index "+ index +
                               "is out of range when getting.");
            System.exit(1);
        }
        if (breakPoints[index] == null) {
            System.err.println("jMusic Note error: Breakpoint index "+ index + " is empty.");
            System.exit(1);
        }
        return this.breakPoints[index];
    }

    //----------------------------------------------
    // Utility Methods
    //----------------------------------------------
    /**
     * Collects a string of the notes attributes
     */
    public String toString(){
        String noteDetails;
        if(pitchType == MIDI_PITCH) {
            noteDetails =  new String("jMusic NOTE: " +
                                      "[Pitch = " + (int)pitch +
                                      "][RhythmValue = " + rhythmValue +
                                      "][Dynamic = " + dynamic +
									"][Duration = " + duration + 
                                      "][Pan = " + pan + "]");
        } else {
            noteDetails =  new String("Note: " +
                                      "[Frequency = " + pitch +
                                      "][RhythmValue = " + rhythmValue +
                                      "][Dynamic = " + dynamic +
									"][Duration = " + duration + 
                                      "][Pan = " + pan + "]");
        }
        return noteDetails;
    }
	
    /**
     * Checks if the note is within a particular scale
     * There are a number of scale constants specified in the JMC
     * which can be used with this method,
     * these include MAJOR_SCALE, MINOR_SCALE, and PENTATONIC_SCALE
     * @param int[] - an array of scale degrees
     * @return boolean - true means it is in the scale
     */
    public boolean isScale(int[] scale) {
        for (int j = 0; j < scale.length; j++) {
            if (this.pitch % 12 == scale[j]) return true;
        }
        return false;
    }


    /**
     * Sets the rhythmValue, and optionally change the duration
     * at the same time.
     * @param factorDuration wether or not to change the duration
     * to be a multiple of the rhythm value as well
     */
    public void setRhythmValue(double rv, boolean factorDuration) {
        setRhythmValue(rv);
        if(factorDuration) {
            setDuration(rv*DEFAULT_DURATION_MULTIPLIER);
        }
    }
	
    /**
     * Convert a frequency into a MIDI note pitch.
     * Assumes A440 and equal tempered intonation.
     * Adapted from C code written by Andrew Botros.
     * @param freq The frequency value to convert.
     * @return int The MIDI pitch number closest to the input frequency.
     */
    public static int freqToMidiPitch(double freq) {
        /* input frequency must be between A0 and A9 */
        if((freq < 26.73) || (freq > 14496.0)) {
            System.err.println("freqToMidiPitch error: "+
                               "Frequency " + freq + " is not within the MIDI note range.");
            return -1;
        }
        // A semitone higher than a given frequency
        // is 2^(1/12) times the frequency.
        double r = Math.pow(2, 1.0/12.0);
        // A cent higher than a given frequency
        // is 2^(1/1200) times the frequency
        double cent = Math.pow(2, 1.0/1200.0);
        int r_index = 0;
        int cent_index = 0;
        int side;
        /* search for input ratio against A4 to the nearest cent
           in range -49 to +50 cents around closest note */
        double referenceFreq = 440.0;
        if(freq >= referenceFreq) {
            while(freq > r*referenceFreq) {
                referenceFreq = r*referenceFreq;
                r_index++;
            }
            while(freq > cent*referenceFreq) {
                referenceFreq = cent*referenceFreq;
                cent_index++;
            }
            if((cent*referenceFreq - freq) < (freq - referenceFreq))
                cent_index++;
            if(cent_index > 50) {
                r_index++;
                cent_index = 100 - cent_index;
            }
        }
        else {
            while(freq < referenceFreq/r) {
                referenceFreq = referenceFreq/r;
                r_index--;
            }
            while(freq < referenceFreq/cent) {
                referenceFreq = referenceFreq/cent;
                cent_index++;
            }
            if((freq - referenceFreq/cent) < (referenceFreq - freq))
                cent_index++;
            if(cent_index >= 50) {
                r_index--;
                cent_index = 100 - cent_index;
            }
        }
		
        return 69 + r_index;
    }
	
    /**
     * Calculate the frequency in hertz of a MIDI note pitch.
     * Assumes an A440.0 reference and equal tempered intonation.
     * Written by Andrew Brown based on C code by Andrew Botros.
     * @param midiPitch The note pitch value to convert.
     * @return double The frequency equivalent in cycles per second.
     */
    public static double midiPitchToFreq(int midiPitch) {
        //range OK
        if (midiPitch < 0 || midiPitch > 127) {
            System.err.println("jMusic Note.midiPitchToFreq error:" +
                               "midiPitch of " +midiPitch + " is out side valid range.");
            return -1.0;
        }
        // A semitone higher than a given frequency
        // is 2^(1/12) times the frequency.
        double r = Math.pow(2, 1.0/12.0);
        int pitchOffset = midiPitch - 69;
        double freq = 440.0;
        if (midiPitch > 69) {
            for (int i=69; i<midiPitch; i++) {
                freq = freq*r;
            }
        } else {
            for (int i=69; i>midiPitch; i--) {
                freq = freq/r;
            }
        }
        // rounding to get more reasonable values
        freq = Math.round(freq * 1000.0) / 1000.0;
		
        return freq;
    }

    /**
     * Check if the note is a rest or a pitched note.
     * @return boolean True if the note is a rest otherwise false.
     */
    public boolean isRest() {
        if (this.getPitch() == REST) return true;
        else return false;
    }

    /**
     * Change both the rhythmValue and duration of a note in the one step.
     * @param newLength The new rhythmValue for the note (Duration is a proportion of this value)
     */
    public void setLength(double newLength) {
        this.setRhythmValue(newLength);
        this.setDuration(newLength * DEFAULT_DURATION_MULTIPLIER);
    }

    /**
     * tells whether the note is a sharp or not
     * @return
     */
    public boolean isSharp() {
        return (getNote().equals("C#") || getNote().equals("F#"));
    }

    /**
     * tells whether the note is a sharp or not by using its string value
     * @return
     */
    public boolean isFlat() {
        return getNote().equals("Eb")
            || getNote().equals("Ab")
            || getNote().equals("Bb");
    }
	
    /** tells whether the note is natural or not
     *
     * @return
     */
    public boolean isNatural() {
        return !isSharp() && !isFlat();
    }
	
    public boolean samePitch(Note note){
        return this.getPitch() == note.getPitch();
    }
	
    public boolean sameDuration(Note note){
        return this.getDuration() == note.getDuration();
    }
	
    //same pitch and same duration
    public boolean equals(Note note){
        return samePitch(note) && sameDuration(note);
    }

	
    /**
     * gives the next note in the scale
     *
     * For example, if inputting a D note for a DMinor scale, the next note returned is an E
     * if inputing a Bb for a C
     * @param scale a constant from Scales.java goes here
     * @return
     */
    public Note nextNote(int[] scale){
	//int[] scaleType = scale.getScaleType();
	Note nextNote =null;
	for (int i = 0; i < scale.length; i++) {
////	    this.getPitch()
////	    //scale[i]
//Note note = (Note)scale.get(i);
	    //System.out.println("Scale " + scale[i]);
	    //System.out.println("Mod This Note " + this.getPitchValue() % 12);
            if(this.getPitchValue() % 12 == 0){
                nextNote = new Note(this.getPitch() + scale[i],DEFAULT_RHYTHM_VALUE);
            }
////			Note note2 = (Note)scale.get(i+1);
////			return note2;
////		}
//	
	}
//	return null;
	int nextpitch = this.getPitch() + scale[1];
	System.out.println("NEXT PITCH " + nextpitch + " " + this.getPitch() + " " + scale[1]);
        return new Note(nextpitch,DEFAULT_RHYTHM_VALUE);
    }

/** returns the pitches for the middle scale(default) on a keyboard */
    public int getPitchValue() {
	int pitch = 0;
	if (noteString.equals(C)) {
            pitch = 60;
	} else if (noteString.equals(C_SHARP)) {
            pitch = 61;
	} else if (noteString.equals(D)) {
            pitch = 62;
	} else if (noteString.equals(E_FLAT)) {
            pitch = 63;
	} else if (noteString.equals(E)) {
            pitch = 64;
	} else if (noteString.equals(F)) {
            pitch = 65;
	} else if (noteString.equals(F_SHARP)) {
            pitch = 66;
	} else if (noteString.equals(G)) {
            pitch = 67;
	} else if (noteString.equals(A_FLAT)) {
            pitch = 68;
	} else if (noteString.equals(A)) {
            pitch = 69;
	} else if (noteString.equals(B_FLAT)) {
            pitch = 70;
	} else if (noteString.equals(B)) {
            pitch = 71;
	}
	return pitch;
    }

/**
 * gets the string representation for a note for a given MIDI pitch (0-127)
 * @return
 */
    public String getNote() {
	//String note = "";
	if (this.getPitch() % 12 == 0)
            noteString = "C";
	else if (this.getPitch() % 12 == 1)
            noteString = "C#";
	else if (this.getPitch() % 12 == 2)
            noteString = "D";
	else if (this.getPitch() % 12 == 3)
            noteString = "Eb";
	else if (this.getPitch() % 12 == 4)
            noteString = "E";
	else if (this.getPitch() % 12 == 5)
            noteString = "F";
	else if (this.getPitch() % 12 == 6)
            noteString = "F#";
	else if (this.getPitch() % 12 == 7)
            noteString = "G";
	else if (this.getPitch() % 12 == 8)
            noteString = "Ab";
	else if (this.getPitch() % 12 == 9)
            noteString = "A";
	else if (this.getPitch() % 12 == 10)
            noteString = "Bb";
	else if (this.getPitch() % 12 == 11)
            noteString = "B";
	else
            noteString = "N/A"; //throw an exception
	return noteString;
    }

/**
 * gets the string representation for a note for a given MIDI pitch (0-127)
 * @return
 */
    public static String getNote(int pitch) {
	String noteString = "";
	if (pitch % 12 == 0)
            noteString = "C";
	else if (pitch % 12 == 1)
            noteString = "C#";
	else if (pitch % 12 == 2)
            noteString = "D";
	else if (pitch % 12 == 3)
            noteString = "Eb";
	else if (pitch % 12 == 4)
            noteString = "E";
	else if (pitch % 12 == 5)
            noteString = "F";
	else if (pitch % 12 == 6)
            noteString = "F#";
	else if (pitch % 12 == 7)
            noteString = "G";
	else if (pitch % 12 == 8)
            noteString = "Ab";
	else if (pitch % 12 == 9)
            noteString = "A";
	else if (pitch % 12 == 10)
            noteString = "Bb";
	else if (pitch % 12 == 11)
            noteString = "B";
	else
            noteString = "N/A"; //throw an exception
	return noteString;
    }
}