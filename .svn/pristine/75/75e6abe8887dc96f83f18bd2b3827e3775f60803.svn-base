/*               
 * PhraseAnalysis.java 0.2.1.0 9th March 2001
 *
 * Copyright (C) 2000 Adam Kirby
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

package jm.music.tools;

import jm.music.data.Note;
import jm.music.data.Phrase;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Provides class methods which return statistics about a {@link Phrase} or
 * {@link Note} array.  For each statistic there are two methods, accepting
 * either a Phrase or a Note array as a parameter.
 *
 * <P>This class also provides the constants used when generating those
 * statistics.
 *
 * <P>The methods are grouped under the following categories:
 *
 * <OL><LI>Bulk Statistics:
 * <OL>Methods that call multiple other methods, and return their statistics in
 * one group.  They never generate any new statistics themselves; every value
 * in the group can be accessed individually using the other methods in this
 * class. The method <CODE>getAllStatistics</CODE> is the only bulk statistic;
 * it returns all the feature statistics.</OL></LI>
 *
 * <LI><A NAME="Feature Statistics">Feature Statistics</A>:
 * <OL>Methods that return double values in the range of 0 to 1.  Usually these
 * take the form of a percentage measuring the degree to which the feature is
 * present, compared with the maximum value possible for the feature.
 * <br>Many feature statistics rely on the accessory statistics to generate
 * values used in their calculations.</OL></LI>
 *
 * <LI>Accessory Statistics:
 * <OL>Methods that return more direct statistics, such as the number of
 * distinct pitches in a melody.  These are generally more useful than the
 * feature statistics.  All of the accessory statistics are used by at least one
 * of the feature statistics.</OL></LI>
 * </LI></OL>
 *
 * <P>The bulk statistics include:
 * <UL><LI>{@link #getAllStatistics}</LI></UL>
 *
 * <P>The feature statistics include:
 * <UL><LI>{@link #noteDensity}</LI>
 * <LI>{@link #pitchVariety}</LI>
 * <LI>{@link #rhythmicVariety}</LI>
 * <LI>{@link #climaxStrength}</LI>
 * <LI>{@link #restDensity}</LI>
 * <LI>{@link #tonalDeviation}</LI>
 * <LI>{@link #keyCenteredness}</LI>
 * <LI>{@link #pitchRangePerSpan}</LI>
 * <LI>{@link #rhythmRangePerSpan}</LI>
 * <LI>{@link #repeatedPitchDensity}</LI>
 * <LI>{@link #repeatedRhythmicValueDensity}</LI>
 * <LI>{@link #melodicDirectionStability}</LI>
 * <LI>{@link #overallPitchDirection}</LI>
 * <LI>{@link #movementByStep}</LI>
 * <LI>{@link #dissonance}</LI>
 * <LI>{@link #leapCompensation}</LI>
 * <LI>{@link #syncopation}</LI>
 * <LI>{@link #repeatedPitchPatterns}</LI>
 * <LI>{@link #repeatedRhythmPatterns}</LI>
 * <LI>{@link #climaxPosition}</LI>
 * <LI>{@link #climaxTonality}</LI></UL>
 *
 * <P>The accessory statistics include:
 * <UL><LI>Everything else.</LI></UL>
 *
 * <P>For clarification, here are definitions of terms used in the method
 * descriptions.
 * <P><A NAME="Note">Note</A>
 * <UL>Unless otherwise specified the term 'note' or 'notes' do not include
 * rests.  This does not apply to the JMusic object Note, which will always
 * be capitalised.</UL>
 * <P><A NAME="Quantum">Quantum duration</A>
 * <UL>For a quantised melody, all rhythm values in the melody must be
 * multiples of the quantum duration. A note which has a rhythm value four
 * times the quantum duration can be described as being four quanta long.</UL>
 *
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:53  2001
 *
 * @see     Phrase
 * @see     Note
 * @since   jMusic November 2000 Release
 */
public final class PhraseAnalysis {
    /**
     * This class is not meant to be instantiated, as it contains only class
     * members.
     */
    private PhraseAnalysis () {}

    /**
     * Array of Strings giving the identifier number and descriptive name
     * of each feature as returned by the {@link #getAllStatistics
     * getAllStatistics} methods.
     */
    public static final String[] featureNames = {
            "01 - Pitch Variety",
            "02 - Pitch Range",
            "03 - Key Centeredness",
            "04 - Tonal Deviation",
            "05 - Dissonance",
            "06 - Overall Pitch Direction",
            "07 - Melodic Direction Stability",
            "08 - Pitch Movement By Tonal Step",
            "09 - Leap Compensation",
            "10 - Climax Strength",
            "11 - Climax Position",
            "12 - Climax Tonality",
            "13 - Note Density",
            "14 - Rest Density",
            "15 - Rhythmic Variety",
            "16 - Rhythmic Range",
            "17 - Syncopation",
            "18 - Repeated Pitch Density",
            "19 - Repeated Rhythmic Value Density",
            "20 - Repeated Pitch Patterns Of Three",
            "21 - Repeated Pitch Patterns Of Four",
            "22 - Repeated Rhythm Patterns Of Three",
            "23 - Repeated Rhythm Patterns Of Four" };

    public static final String NOTELIST_EXCEPTION_STRING = "NoteListException";

    public static final String QUANTISATION_EXCEPTION_STRING =
            "QuantisationException";

    public static final double NOTELIST_EXCEPTION_CONSTANT = -1.0;

    public static final double QUANTISATION_EXCEPTION_CONSTANT = -2.0;

    /**
     * Integer constant describing the number of statistical features returned
     * by the {@link #getAllStatistics getAllStatistics} methods.
     */
    public static final int FEATURE_COUNT = 23;

    /**
     * Integer constant describing the maximum pitch range that a reasonable
     * melody can span, measured in semitones.  This maximum is not an actual
     * maximum, but a value determined by those developing these methods as
     * suitably large enough that it would be a maximum for a reasonable
     * melody.
     *
     * <P>Within this class, this constant is only used by the {@link
     * #pitchRangePerSpan pitchRangePerSpan} method.
     *
     * <P>As such, this constant provides little benefit except to
     * implementations that use the <CODE>pitchRangePerSpan</CODE> method.
     */
    public static final int MAX_PITCH_RANGE = 24;

    /**
     * Double constant describing the maximum rhythm range that a reasonable
     * could utilise.  This measure is equal to the longest reasonable rhythm
     * value divided by the shortest.  This maximum is not an actual maximum,
     * but a value determined by those developing these methods as suitably
     * large enough suitably large enough that it would be a maximum for a
     * reasonable melody.
     *
     * <P>Within this class, this constant is only used by the {@link
     * #rhythmRangePerSpan rhythmRangePerSpan} method.
     *
     * <P>As such, this constant provides little benefit except to
     * implementations that use the <CODE>rhythmRangePerSpan</CODE> method.
     */
    public static final double MAX_RHYTHM_RANGE = 16;

    /**
     * Integer array containing the pitches representing a major scale.  These
     * pitches are relative to the tonic, are measured in semitones, and are
     * limited to one octave.  For instance, the dominant is represented by the
     * value 7, as it is 7 semitones above the tonic.
     *
     * <P>Many methods of this class require a scale of this form as parameters.
     * It is suggested that either this constant or the <CODE>MINOR_SCALE</CODE>
     * constant be passed to these methods, depending on the key of the melody
     * to be analysed.
     */
    public static final int[] MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11};

    /**
     * Integer array containing the pitches representing a minor scale.  These
     * pitches are relative to the tonic, are measured in semitones, and are
     * limited to one octave.  For instance, the dominant is represented by the
     * value 7, as it is 7 semitones above the tonic.
     *
     * <P>Many methods of this class require a scale of this form as parameters.
     * It is suggested that either this constant or the <CODE>MAJOR_SCALE</CODE>
     * constant be passed to these methods, depending on the key of the melody
     * to be analysed.
     */
    public static final int[] MINOR_SCALE = {0, 2, 3, 5, 7, 8, 10};

    /**
     * Integer array containing the pitches that represent primary pitches.  A
     * primary pitch is a pitch that is considered in strong accord to the
     * tonic, such as the tonic itself and the dominant. These pitches are
     * relative to the tonic, measured in semitones, and limited to one octave.
     *
     * <P>This constant is used by the {@link #primaryQuantumCount
     * primaryQuantumCount} and {@link #climaxTonality climaxTonality} methods.
     */
    public static final int[] PRIMARY_NOTES = {0, 7};

    /**
     * Integer defining the smallest interval, measured in semitones, that
     * constitutes a 'big jump'.  A 'big jump' is an interval that is generally
     * considered extreme or uncommon and hence requires a step back to 
     * compensate musically.
     *
     * This constant is used by the {@link #leapCompensation leapCompensation},
     * {@link #bigJumpFollowedByStepBackCount bigJumpFollowedByStepBackCount},
     * and {@link #bigJumpCount bigJumpCount} methods.
     */
    public static final int BIG_JUMP_INTERVAL = 8;

    /**
     * Integer defining the maximum number of distinct rhythm values that can
     * appear in the melody.
     *
     * This constant is only used by the {@link #rhythmicVariety
     * rhythmicVariety} method.
     */
    public static final int MAX_DISTINCT_RHYTHMS = 16;

    /**
     * Used by the {@link #pitchIntervals} method to indicate an interval with
     * one or more rests between its <A HREF="#Note">notes</A>.  That method
     * indicates a interval with a rest by adding this constant to the size
     * of the interval.
     *
     * This constant is guaranteed to be large enough not to cause any conflicts
     * between the value for extremely large rising intervals without rests, and
     * extremely large lowering intervals with rests.
     */
    public static final int INTERVAL_WITH_REST =
            ((int)Note.MAX_PITCH - (int)Note.MIN_PITCH) * 2 + 1;

    /**
     * Integer array containing the pitches that represent 'good' intervals.  A
     * 'good' interval is one that is generally considered acceptable when the
     * pitches of the interval are played sequentially.
     *
     * <P>These intervals are measured in semitones and should not be greater
     * than an octave as these are automatically considered unacceptable.
     * Descending intervals are treated as their ascending counterparts, so all
     * values returned by this array should be greater than zero.
     *
     * <P>This constant is used by the {@link #dissonance dissonance} and {@link
     * #checkDissonance checkDissonance} methods.
     */
    private static final int[] GOOD_INTERVALS = {0, 1, 2, 3, 4, 5, 7, 8, 9, 12};

    /**
     * Integer array containing the pitches that represent 'bad' intervals.  A
     * 'bad' interval is one that is generally considered unacceptable when the
     * pitches of the interval are played sequentially.
     *
     * <P>These intervals are measured in semitones and should not be greater
     * than an octave as these are automatically considered unacceptable.
     * Descending intervals are treated as their ascending counterparts, so all
     * values returned by this array should be greater than zero.
     *
     * <P>This constant is used by the {@link #dissonance dissonance} and {@link
     * #checkDissonance checkDissonance} methods.
     */
    private static final int[] BAD_INTERVALS = {6, 11};

    /**
     * Integer constant describing the number of semitone intervals in any
     * given octave.
     */
    private static final int SEMITONES_PER_OCTAVE = 12;

    public static String[] getAllStatisticsAsStrings(final Phrase phrase,
                                                     final double duration,
                                                     final int tonic,
                                                     final int[] scale) {
        return getAllStatisticsAsStrings(phrase.getNoteArray(), duration,
                                         tonic, scale);
    }

    public static String[] getAllStatisticsAsStrings(final Note[] noteArray,
                                                     final double duration,
                                                     final int tonic,
                                                     final int[] scale) {
        String[] statistics = new String[23];
        try {
            statistics[0] = Double.toString(pitchVariety(noteArray));
        } catch (NoteListException e) {
            statistics[0] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[1] = Double.toString(pitchRangePerSpan(noteArray));
        } catch (NoteListException e) {
            statistics[1] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[2] =
                    Double.toString(
                            keyCenteredness(noteArray, duration, tonic));
        } catch (NoteListException e) {
            statistics[2] = NOTELIST_EXCEPTION_STRING;
        } catch (QuantisationException e) {
            statistics[2] = QUANTISATION_EXCEPTION_STRING;
        }
        try {
            statistics[3] =
                    Double.toString(
                            tonalDeviation(noteArray, duration, tonic, scale));
        } catch (NoteListException e) {
            statistics[3] = NOTELIST_EXCEPTION_STRING;
        } catch (QuantisationException e) {
            statistics[3] = QUANTISATION_EXCEPTION_STRING;
        }
        statistics[4] = Double.toString(dissonance(noteArray));
        statistics[5] = Double.toString(overallPitchDirection(noteArray));
        try {
            statistics[6] = 
                    Double.toString(melodicDirectionStability(noteArray));
        } catch (NoteListException e) {
            statistics[6] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[7] =
                    Double.toString(movementByStep(noteArray, tonic, scale));
        } catch (NoteListException e) {
            statistics[7] = NOTELIST_EXCEPTION_STRING;
        }
        statistics[8] = Double.toString(leapCompensation(noteArray));
        try {
            statistics[9] = Double.toString(climaxStrength(noteArray));
        } catch (NoteListException e) {
            statistics[9] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[10] = Double.toString(climaxPosition(noteArray));
        } catch (NoteListException e) {
            statistics[10] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[11] =
                    Double.toString(climaxTonality(noteArray, tonic, scale));
        } catch (NoteListException e) {
            statistics[11] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[12] = Double.toString(noteDensity(noteArray, duration));
        } catch (NoteListException e) {
            statistics[12] = NOTELIST_EXCEPTION_STRING;
        } catch (QuantisationException e) {
            statistics[12] = QUANTISATION_EXCEPTION_STRING;
        }
        try {
            statistics[13] = Double.toString(noteDensity(noteArray, duration));
        } catch (NoteListException e) {
            statistics[13] = NOTELIST_EXCEPTION_STRING;
        } catch (QuantisationException e) {
            statistics[13] = QUANTISATION_EXCEPTION_STRING;
        }
        statistics[14] = Double.toString(rhythmicVariety(noteArray));
        try {
            statistics[15] = Double.toString(rhythmRangePerSpan(noteArray));
        } catch (NoteListException e) {
            statistics[15] = NOTELIST_EXCEPTION_STRING;
        }
        statistics[16] = Double.toString(syncopation(noteArray));
        try {
            statistics[17] = Double.toString(repeatedPitchDensity(noteArray));
        } catch (NoteListException e) {
            statistics[17] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[18] =
                    Double.toString(repeatedRhythmicValueDensity(noteArray));
        } catch (NoteListException e) {
            statistics[18] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[19] =
                    Double.toString(repeatedPitchPatterns(noteArray, 3));
        } catch (NoteListException e) {
            statistics[19] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[20] =
                    Double.toString(repeatedPitchPatterns(noteArray, 4));
        } catch (NoteListException e) {
            statistics[20] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[21] =
                    Double.toString(repeatedRhythmPatterns(noteArray, 3));
        } catch (NoteListException e) {
            statistics[21] = NOTELIST_EXCEPTION_STRING;
        }
        try {
            statistics[22] =
                    Double.toString(repeatedRhythmPatterns(noteArray, 4));
        } catch (NoteListException e) {
            statistics[22] = NOTELIST_EXCEPTION_STRING;
        }
        return statistics;
    }

    public static double[] getAllStatisticsAsDoubles(final Phrase phrase,
                                                     final double duration,
                                                     final int tonic,
                                                     final int[] scale) {
        return getAllStatisticsAsDoubles(phrase.getNoteArray(), duration,
                                         tonic, scale);
    }

    public static double[] getAllStatisticsAsDoubles(final Note[] noteArray,
                                                     final double duration,
                                                     final int tonic,
                                                     final int[] scale) {
        double[] statistics = new double[23];
        try {
            statistics[0] = pitchVariety(noteArray);
        } catch (NoteListException e) {
            statistics[0] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[1] = pitchRangePerSpan(noteArray);
        } catch (NoteListException e) {
            statistics[1] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[2] = keyCenteredness(noteArray, duration, tonic);
        } catch (NoteListException e) {
            statistics[2] = NOTELIST_EXCEPTION_CONSTANT;
        } catch (QuantisationException e) {
            statistics[2] = QUANTISATION_EXCEPTION_CONSTANT;
        }
        try {
            statistics[3] = tonalDeviation(noteArray, duration, tonic, scale);
        } catch (NoteListException e) {
            statistics[3] = NOTELIST_EXCEPTION_CONSTANT;
        } catch (QuantisationException e) {
            statistics[3] = QUANTISATION_EXCEPTION_CONSTANT;
        }
        statistics[4] = dissonance(noteArray);
        statistics[5] = overallPitchDirection(noteArray);
        try {
            statistics[6] = melodicDirectionStability(noteArray);
        } catch (NoteListException e) {
            statistics[6] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[7] = movementByStep(noteArray, tonic, scale);
        } catch (NoteListException e) {
            statistics[7] = NOTELIST_EXCEPTION_CONSTANT;
        }
        statistics[8] = leapCompensation(noteArray);
        try {
            statistics[9] = climaxStrength(noteArray);
        } catch (NoteListException e) {
            statistics[9] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[10] = climaxPosition(noteArray);
        } catch (NoteListException e) {
            statistics[10] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[11] = climaxTonality(noteArray, tonic, scale);
        } catch (NoteListException e) {
            statistics[11] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[12] = noteDensity(noteArray, duration);
        } catch (NoteListException e) {
            statistics[12] = NOTELIST_EXCEPTION_CONSTANT;
        } catch (QuantisationException e) {
            statistics[12] = QUANTISATION_EXCEPTION_CONSTANT;
        }
        try {
            statistics[13] = noteDensity(noteArray, duration);
        } catch (NoteListException e) {
            statistics[13] = NOTELIST_EXCEPTION_CONSTANT;
        } catch (QuantisationException e) {
            statistics[13] = QUANTISATION_EXCEPTION_CONSTANT;
        }
        statistics[14] = rhythmicVariety(noteArray);
        try {
            statistics[15] = rhythmRangePerSpan(noteArray);
        } catch (NoteListException e) {
            statistics[15] = NOTELIST_EXCEPTION_CONSTANT;
        }
        statistics[16] = syncopation(noteArray);
        try {
            statistics[17] = repeatedPitchDensity(noteArray);
        } catch (NoteListException e) {
            statistics[17] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[18] = repeatedRhythmicValueDensity(noteArray);
        } catch (NoteListException e) {
            statistics[18] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[19] = repeatedPitchPatterns(noteArray, 3);
        } catch (NoteListException e) {
            statistics[19] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[20] = repeatedPitchPatterns(noteArray, 4);
        } catch (NoteListException e) {
            statistics[20] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[21] = repeatedRhythmPatterns(noteArray, 3);
        } catch (NoteListException e) {
            statistics[21] = NOTELIST_EXCEPTION_CONSTANT;
        }
        try {
            statistics[22] = repeatedRhythmPatterns(noteArray, 4);
        } catch (NoteListException e) {
            statistics[22] = NOTELIST_EXCEPTION_CONSTANT;
        }
        return statistics;
    }


    /**
     * Returns a {@link Hashtable} containing all the <A
     * HREF="#Feature Statistics">feature statistics</A>.
     *
     * <P>The keys of the hashtable contain a String describing the feature
     * along with a numeric identification code for that feature.
     *
     * <P>The values are Strings representing the value returned by that
     * feature: either the double returned, or a description of the exception
     * thrown.
     *
     * @param phrase    the Phrase to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          hashtable whose keys contain a String describing the
     *                  feature statistics, and whose corresponding values are
     *                  the doubles returned by each statistic converted to a
     *                  String.
     */
    public static Hashtable getAllStatistics (final Phrase phrase,
                                              final double duration,
                                              final int tonic,
                                              final int[] scale) {
        return getAllStatistics (phrase.getNoteArray (), duration, tonic,
                                 scale);
    }

    /**
     * Returns a {@link Hashtable} containing all the <A
     * HREF="#Feature Statistics">feature statistics</A>.
     *
     * <P>The keys of the hashtable contain a String describing the feature
     * along with a numeric identification code for that feature.
     *
     * <P>The values are Strings representing the value returned by that
     * feature: either the double returned, or a description of the exception
     * thrown.
     *
     * @param noteList  the Note array to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          hashtable whose keys contain a String describing the
     *                  feature statistics, and whose corresponding values are
     *                  the doubles returned by each statistic converted to a
     *                  String.
     */
    public static Hashtable getAllStatistics (final Note[] noteList,
                                              final double duration,
                                              final int tonic,
                                              final int[] scale) {
        String[] statistics =
                getAllStatisticsAsStrings(noteList, duration, tonic, scale);
        Hashtable hashtable = new Hashtable();
        for (int i = 0; i < featureNames.length; i++) {
            hashtable.put(featureNames[i], statistics[i]);
        }
        return hashtable;
    }

    /**
     * Returns a double expressing the the number of notes per <A
     * HREF="#Quantum">quanta</A> in a {@link Phrase}. The number of notes
     * exludes rests.
     *
     * @param phrase    Phrase whose notes are to be counted
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          double describing the fraction of notes per quantum in
     *                  the Phrase
     * @exception NoteListException
     *                  if the number of quantum in the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double noteDensity (final Phrase phrase,
                                      final double duration)
                              throws NoteListException, QuantisationException {
        return noteDensity (phrase.getNoteArray (), duration);
    }

    /**
     * Returns a double expressing the the number of notes per <A
     * HREF="#Quantum">quanta</A> in a {@link Note} array. The number of notes
     * exludes rests.
     *
     * @param noteArray array of Notes to be examined
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          double describing the fraction of notes per quantum in
     *                  the Note array
     * @exception NoteListException
     *                  if the number of quantum in the Note array is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double noteDensity (final Note[] noteArray,
                                      final double duration)
                              throws NoteListException, QuantisationException {
        int quanta = quantumCount (noteArray, duration);
        if (quanta != 0) {
            return noteCount (noteArray) / (double) quanta;
        } else {
            throw new NoteListException ("The length of the melody should be"
                                         + " greater than 0.");
        }
    }

    /**
     * Returns a double expressing the number of distinct pitches per note in
     * the {@link Phrase}.
     *
     * @param phrase    Phrase whose pitches are to be counted
     * @return          double describing the fraction of distinct pitches per
     *                  note in the Phrase
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double pitchVariety (final Phrase phrase)
                               throws NoteListException {
        return pitchVariety (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the number of distinct
     * pitches and the total number of notes in the specified {@link Note Note}
     * array.
     *
     * @param noteArray array of Notes to be examined
     * @return          double describing the fraction of distinct pitches per
     *                  note in the Note array
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static double pitchVariety (final Note[] noteArray)
                               throws NoteListException {
        int noteCount = noteCount (noteArray);
        if (noteCount != 0) {
            return distinctPitchCount (noteArray) / (double) noteCount;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                          + " one note.");
        }
    }

    /**
     * Returns a double expressing the number of distinct rhythm Values used
     * over the maximum possible rhythmic values.  The maximum is set in the
     * constant {@link #MAX_DISTINCT_RHYTHMS}.
     *
     * @param phrase    Phrase to be examined
     * @return          double describing the fraction of distinct pitches per
     *                  note in the Phrase
     */
    public static double rhythmicVariety (final Phrase phrase) {
        return rhythmicVariety (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the number of distinct
     * rhythms and the total number of notes in the specified {@link Note Note}
     * array.
     *
     * @param noteArray array of Note objects to be examined
     * @return          double describing the fraction of distinct pitches per
     *                  note in the Note array
     */
    public static double rhythmicVariety (final Note[] noteArray) {
        return distinctRhythmCount (noteArray) / (double) MAX_DISTINCT_RHYTHMS;
    }

    /**
     * Returns a double expressing the inverse of the count of the number of
     * notes sharing the highest pitch in the specified Phrase.
     *
     * @param Phrase    Phrase to be examined
     * @return          double describing the climatic strength.
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double climaxStrength (final Phrase phrase)
                                 throws NoteListException {
        return climaxStrength (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the inverse of the count of the number of
     * notes sharing the highest pitch in the specified Note array.
     *
     * @param noteArray Note array to be examined
     * @return          double describing the climatic strength.
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static double climaxStrength (final Note[] noteArray)
                                 throws NoteListException {
        int count = 0;
        int highestPitch = (int)Note.MIN_PITCH;
        int currentPitch;

        for (int i = 0; i < noteArray.length; i++) {
            currentPitch = noteArray[i].getPitch ();
            if (currentPitch != jm.JMC.REST) {
                if (currentPitch > highestPitch) {
                    highestPitch = currentPitch;
                    count = 1;
                } else if (currentPitch == highestPitch) {
                    count++;
                }
            }
        }
        if (count != 0) {
            return 1 / (double) count;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " one note.");
        }
    }

    /**
     * Returns a double expressing the ratio between the number of rest
     * <A HREF="#Quantum">quanta</A> and the number of quanta in a Phrase.
     * That is, the percentage of the time taken up by rests.  
     *
     * @param phrase    Phrase to be examined
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          double describing the fraction of silent quanta per
     *                  quantum in the Phrase
     * @exception NoteListException
     *                  if the length of the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double restDensity (final Phrase phrase,
                                      final double duration)
                              throws NoteListException, QuantisationException {
        return restDensity (phrase.getNoteArray (), duration);
    }

    /**
     * Returns a double expressing the ratio between the number of rest
     * <A HREF="#Quantum">quanta</A> and the number of quanta in a Note array.
     * That is, the percentage of the time taken up by rests.  
     *
     * @param noteArray array of Note objects to be examined
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          double describing the fraction of silent quanta per
     *                  quantum in the Note array
     * @exception NoteListException
     *                  if the length of the melody is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double restDensity (final Note[] noteArray,
                                      final double duration)
                              throws NoteListException, QuantisationException {
        int quanta = quantumCount (noteArray, duration);
        if (quanta != 0) {
            return restQuantumCount (noteArray, duration) / (double) quanta;
        } else {
            throw new NoteListException ("The length of the melody should be"
                                         + " greater than 0.");
        }
    }

    /**
     * Returns a double expressing the total duration of all non-scale notes,
     * over the length of the melody.
     *
     * @param phrase    Phrase to be examined
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the fraction of non-scale quanta per
     *                  quantum in the Phrase
     * @exception NoteListException
     *                  if the number of quanta in the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero

     */
    public static double tonalDeviation (final Phrase phrase,
                                         final double duration, final int tonic,
                                         final int[] scale)
                                 throws NoteListException,
                                        QuantisationException {
        return tonalDeviation (phrase.getNoteArray (), duration, tonic, scale);
    }

    /**
     * Returns a double expressing the total duration of all non-scale notes,
     * over the length of the melody.
     *
     * @param noteArray array of Note objects to be examined
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the fraction of non-scale quanta per
     *                  quantum in the Note array
     * @exception NoteListException
     *                  if the number of quanta in the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double tonalDeviation (final Note[] noteArray,
                                         final double duration, final int tonic,
                                         final int[] scale)
                                 throws NoteListException,
                                        QuantisationException {
        int quanta = quantumCount (noteArray, duration);
        if (quanta != 0) {
            return nonScaleQuantumCount (noteArray, duration, tonic, scale)
                   / (double) quanta;
        } else {
            throw new NoteListException ("The length of the melody should be"
                                         + " greater than 0.");
        }
    }

    /**
     * Returns a double expressing the total duration of all primary notes,
     * over the length of the melody.  Primary notes are either tonic or
     * dominant.
     *
     * @param phrase    Phrase to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @return          double describing the fraction of primary quanta per
     *                  quantum in the melody
     * @exception NoteListException
     *                  if the number of quanta in the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double keyCenteredness (final Phrase phrase,
                                          final double duration,
                                          final int tonic)
                                  throws QuantisationException,
                                         NoteListException {
        return keyCenteredness (phrase.getNoteArray (), duration, tonic);
    }

    /**
     * Returns a double expressing the total duration of all primary notes,
     * over the length of the melody.  Primary notes are either tonic or
     * dominant.
     *
     * @param noteArray array of Notes to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @return          double describing the fraction of primary quanta per
     *                  quantum in the melody
     * @exception NoteListException
     *                  if the number of quanta in the Phrase is zero
     * @exception QuantisationException
     *                  if the notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static double keyCenteredness (final Note[] noteArray,
                                          final double duration,
                                          final int tonic)
                                  throws QuantisationException,
                                         NoteListException {
        int quanta = quantumCount (noteArray, duration);
        if (quanta > 0) {
            return primaryQuantumCount (noteArray, duration, tonic)
                   / (double) quanta;
        } else {
            throw new NoteListException ("The length of the melody should be"
                                         + " greater than 0.");
        }
    }

    /**
     * Returns a double expressing the ratio between the pitch range of the
     * specified phrase and {@link #MAX_PITCH_RANGE}.  If the range of the melody
     * is larger than this constant, this method will return 1 instead.
     *
     * <P><CODE>MAX_PITCH_RANGE</CODE> is an arbitrary value that was useful for
     * the developers of this method.  If this arbitrary constant means nothing
     * to your implementation consider using the {@link #pitchRange pitchRange}
     * method instead.
     *                 
     * @param phrase    Phrase to be analysed
     * @return          double descring the pitch range per
     *                  <CODE>MAX_PITCH_RANGE</CODE>
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double pitchRangePerSpan (final Phrase phrase)
                                    throws NoteListException {
        return pitchRangePerSpan (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the pitch range of the
     * specified Note array and {@link #MAX_PITCH_RANGE}.  If the range of the
     * melody is larger than this constant, this method will return 1 instead.
     *
     * <P><CODE>MAX_PITCH_RANGE</CODE> is an arbitrary value that was useful for
     * the developers of this method.  If this arbitrary constant means nothing
     * to your implementation consider using the {@link #pitchRange(Note[])
     * pitchRange} method instead.
     *
     * @param noteArray Note array to be analysed
     * @return          double descring the pitch range per
     *                  <CODE>MAX_PITCH_RANGE</CODE>
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static double pitchRangePerSpan (final Note[] noteArray)
                                    throws NoteListException {
        double temp = pitchRange (noteArray) / (double) MAX_PITCH_RANGE;
        return (temp < 1) ? temp : 1;
    }

    /**
     * Returns a double expressing the ratio between the rhythm range of the
     * specified phrase and {@link #MAX_RHYTHM_RANGE}.  The range of the melody
     * is equal to the longest rhythm value divided by the shortest. If the
     * range of the melody is larger than this constant, this method will return
     * 1 instead.
     *
     * <P><CODE>MAX_RHYTHM_RANGE</CODE> is an arbitrary value that was useful
     * for the developers of this method.  If this arbitrary constant means
     * nothing to your implementation consider using the {@link #rhythmRange
     * rhythmRange} method instead.
     *                 
     * @param phrase    Phrase to be analysed
     * @return          double descring the rhythm range per
     *                  <CODE>MAX_RHYTHM_RANGE</CODE>
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double rhythmRangePerSpan (final Phrase phrase)
                                     throws NoteListException {
        return rhythmRangePerSpan (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the rhythm range of the
     * specified Note array and {@link #MAX_RHYTHM_RANGE}.  The range of the
     * melody is equal to the longest rhythm value divided by the shortest. If
     * the range of the melody is larger than this constant, this method will
     * return 1 instead.
     *
     * <P><CODE>MAX_RHYTHM_RANGE</CODE> is an arbitrary value that was useful
     * for the developers of this method.  If this arbitrary constant means
     * nothing to your implementation consider using the {@link #rhythmRange
     * rhythmRange} method instead.
     *                 
     * @param noteArray Note array to be analysed
     * @return          double descring the pitch range per
     *                  <CODE>MAX_RHYTHM_RANGE</CODE>
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double rhythmRangePerSpan (final Note[] noteArray)
                                     throws NoteListException {
        double temp = rhythmRange (noteArray) / MAX_RHYTHM_RANGE;
        return (temp < 1) ? temp : 1;
    }

    /**
     * Returns a double expressing the ratio between the count of consecutive
     * notes of the same pitch and the count of all note to next note intervals
     * in the specified Phrase.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the repeated pitches per interval
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Phrase
     */
    public static double repeatedPitchDensity (final Phrase phrase)
                                       throws NoteListException {
        return repeatedPitchDensity (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the count of consecutive
     * notes of the same pitch and the count of all note to next note intervals
     * in the specified Note array.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the repeated pitches per interval
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Note array
     */
    public static double repeatedPitchDensity (final Note[] noteArray)
                                       throws NoteListException {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount != 0) {
            return consecutiveIdenticalPitches (noteArray)
                   / (double) intervalCount;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " two notes.");
        }
    }

    /**
     * Returns a double expressing the ratio between the count of consecutive
     * notes of the same rhythm value and the count of all note to next note
     * intervals in the specified Phrase.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the repeated rhythm values per
     *                  interval
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Phrase
     */
    public static double repeatedRhythmicValueDensity (final Phrase phrase)
                                               throws NoteListException {
        return repeatedRhythmicValueDensity (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the count of consecutive
     * notes of the same rhythm value and the count of all note to next note
     * intervals in the specified Note array.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the repeated rhythm values per
     *                  interval
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Note array
     */
    public static double repeatedRhythmicValueDensity (final Note[] noteArray)
                                               throws NoteListException {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount != 0) {
            return consecutiveIdenticalRhythms (noteArray)
                   / (double) intervalCount;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " two notes.");
        }
    }

    /**
     * Returns a double expressing the ratio between the number of consecutive
     * pitch steps in the same direction and the total number of steps in the
     * specified Phrase.  This checks the degree of melodic contour variation.
     * The feature measures pitch intervals, which means that three <A
     * HREF="#Note">notes</A> (two intervals) are required to measure a pattern.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing melodic direction stability
     * @exception NoteListException
     *                  if there are less than three <A HREF="#Note">notes</A>
     *                  in the Phrase
     */
    public static double melodicDirectionStability (final Phrase phrase)
                                            throws NoteListException {
        return melodicDirectionStability (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the number of consecutive
     * pitch steps in the same direction and the total number of steps in the
     * specified Note array.  This checks the degree of melodic contour
     * variation.  The feature measures pitch intervals, which means that three
     * <A HREF="#Note">notes</A> (two intervals) are required to measure a
     * pattern.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the melodic direction stability
     * @exception NoteListException
     *                  if there are less than three <A HREF="#Note">notes</A>
     *                  in the Note array
     */                                                                         
    public static double melodicDirectionStability (final Note[] noteArray)
                                            throws NoteListException {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount - 1 != 0) {
            return sameDirectionIntervalCount (noteArray)
                   / (double) intervalCount;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " three notes.");
        }
    }

    /**
     * Returns a double expressing the ratio between the sum of rising intervals
     * over the sum of all intervals in the specified Phrase.  For instance, the
     * sum of two 7 semitone intervals is 14 semitones.
     *
     * <P>This feature checks the overall tendency of the melodic contour.  A
     * melody starting and finishing on the same note will score 0.5, higher
     * indicates an overall rise, and lower scores indicate a descending
     * contour. 
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the overall pitch direction
     */
    public static double overallPitchDirection (final Phrase phrase) {
        return overallPitchDirection (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio between the sum of rising intervals
     * over the sum of all intervals in the specified Note array.  For instance,
     * the sum of two 7 semitone intervals is 14 semitones.
     *
     * <P>This feature checks the overall tendency of the melodic contour.  A
     * melody starting and finishing on the same note will score 0.5, higher
     * indicates an overall rise, and lower scores indicate a descending
     * contour. 
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the overall pitch direction
     */
    public static double overallPitchDirection (final Note[] noteArray) {
        double intervalSemitoneCount = intervalSemitoneCount (noteArray);
        if (intervalSemitoneCount != 0.0) {
            return risingSemitoneCount (noteArray) / intervalSemitoneCount;
        } else {
            return 0.5;
        }
    }            

    /**
     * Returns a double expressing the ratio between the number of pitch
     * movements by diatonic step over the total number of steps in the
     * specified Phrase.  This indicates the smoothness of the melody.  A high
     * score indicates a smooth melodic curve wtih few large leaps.  A step
     * interval is any diatonic interval and might be zero, one or two
     * semitones.
     *
     * @param phrase    Phrase to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the movement by step
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Phrase
     */
    public static double movementByStep (final Phrase phrase, final int tonic,
                                         final int[] scale)
                                 throws NoteListException {
        return movementByStep (phrase.getNoteArray (), tonic, scale);
    }

    /**
     * Returns a double expressing the ratio between the number of pitch
     * movements by diatonic step over the total number of steps in the
     * specified Note array.  This indicates the smoothness of the melody.  A
     * high score indicates a smooth melodic curve wtih few large leaps.  A step
     * interval is any diatonic interval and might be zero, one or two
     * semitones.
     *
     * @param noteArray Note array to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the movement by step
     * @exception NoteListException
     *                  if there are less than two <A HREF="#Note">notes</A> in
     *                  the Note array
     */
    public static double movementByStep (final Note[] noteArray,
                                         final int tonic, final int[] scale)
                                 throws NoteListException {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount > 0) {
            return stepIntervalCount (noteArray, tonic, scale)
                   / (double) intervalCount;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " two notes.");
        }
    }

    /**
     * Returns a double expressing the average dissonance rating of all
     * intervals in the specified Phrase.  The intervals are rated as shown by
     * the following table:
     *
     * <CENTER>
     * <TABLE BORDER="1"><TR><TH>Intervals</TH><TH>Dissonance rating</TH></TR>
     * <TR><TH>0, 1, 2, 3, 4, 5, 7, 8, 9</TH><TH>0.0</TH></TR>
     * <TR><TH>10</TH><TH>0.5</TH></TR>
     * <TR><TH>6, 11, 13 or greater</TH><TH>1.0</TH></TR></TABLE></CENTER>
     *
     * <P>If the Phrase has no intervals, it is considered to have no 
     * dissonance, and so 0 is returned.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the dissonance
     */
    public static double dissonance (final Phrase phrase) {
        return dissonance (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the average dissonance rating of all
     * intervals in the specified Note array.  The intervals are rated as shown
     * by the following table:
     *
     * <CENTER>
     * <TABLE BORDER="1"><TR><TH>Intervals</TH><TH>Dissonance rating</TH></TR>
     * <TR><TH>0, 1, 2, 3, 4, 5, 7, 8, 9</TH><TH>0.0</TH></TR>
     * <TR><TH>10</TH><TH>0.5</TH></TR>
     * <TR><TH>6, 11, 13 or greater</TH><TH>1.0</TH></TR></TABLE>
     * </CENTER>
     *
     * <P>If the Note array has no intervals, it is considered to have no
     * dissonance, and so 0 is returned.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the dissonance
     */
    public static double dissonance (final Note[] noteArray) {
        int[] intervals = pitchIntervals (noteArray);
        int dissonance = 0;
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] > Note.MAX_PITCH - Note.MIN_PITCH) {
                intervals[i] -= INTERVAL_WITH_REST;
            }
            intervals[i] = Math.abs (intervals[i]);
            if (intervals[i] > 12) {
                dissonance += 2;
            } else {
                dissonance += rateDissonance (intervals[i]);
            }
        }
        return dissonance / (2.0 * intervals.length);
    }

    /**
     * Returns a double expressing the ratio of 'large leaps' that are not
     * followed by an interval in the opposite direction that is no greater than
     * the leap in the specified Phrase.  An interval is a large leap if it is
     * equal to or greater than the constant {@link #BIG_JUMP_INTERVAL}.
     *
     * <p>If there are no large leaps this method return 0.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the movement by step
     */
    public static double leapCompensation (final Phrase phrase) {
        return leapCompensation (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio of 'large leaps' that are not
     * followed by an interval in the opposite direction that is no greater than
     * the leap in the specified Note array.  An interval is a large leap if it
     * is equal to or greater than the constant {@link #BIG_JUMP_INTERVAL}.
     *
     * <p>If there are no large leaps this method return 0.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the movement by step
     */
    public static double leapCompensation (Note[] noteArray) {
        int bigJumpCount = bigJumpCount (noteArray);
        if (bigJumpCount != 0) {
            return (bigJumpCount - bigJumpFollowedByStepBackCount (noteArray))
                   / (double) bigJumpCount;
        } else {
            return 0.0;
        }
    }

    /**
     * Returns a double expressing the ratio of the number of <A HREF="#Note">
     * notes</A> which start on the beat and have a rhythm value of a beat of
     * more, compared to the total number of beats in the specified Phrase.  In
     * simple time a beat is considered to be crotchet in length.  This feature
     * indicates the degree to which the beat pulse of the music is weakened.
     *
     * @param phrase    Phrase to be analysed.
     * @return          double describing the syncopation of the melody
     */
    public static double syncopation (final Phrase phrase) {
        return syncopation (phrase.getNoteArray ());
    }

    /**
     * Returns a double expressing the ratio of the number of <A HREF="#note">
     * notes</A> which start off the beat and have a rhythm value of a beat of
     * more, compared to the total number of beats less one in the specified
     * Note array.  We use the number of beats less one because that is the
     * number of opportunities for syncopation.
     *
     * <P>In simple time a beat is considered to be crotchet in length.  This
     * feature indicates the degree to which the beat pulse of the music is
     * weakened.
     *
     * @param noteArray Note array to be analysed.
     * @return          double describing the syncopation of the melody
     */
    public static double syncopation (final Note[] noteArray) {
        int count = 0;
        double position = 0.0;
        double rhythmValue;

        for (int i = 0; i < noteArray.length; i++) {
            rhythmValue = noteArray[i].getRhythmValue ();
            if (rhythmValue >= 1.0
                    && noteArray[i].getPitch () != jm.JMC.REST
                    && position % 1.0 != 0.0) {
                count++;
            }
            position += rhythmValue;
        }
        return count / Math.floor (rhythmValueCount (noteArray) - 1.0);
    }

    /**
     * Returns a double expressing the number of repetitions of pitch interval
     * patterns of a specified size, compared to the total possible number of
     * patterns in the specified Phrase.
     *
     * @param phrase    Phrase to be analysed.
     * @param chunkSize integer describing the size of patterns to count
     * @exception NoteListException 
     *                  if the melody does not have more <A HREF="#Note">
     *                  notes</A> than the size of the patterns searched for
     */
    public static double repeatedPitchPatterns (final Phrase phrase,
                                                final int chunkSize)
                                        throws NoteListException {
        return repeatedPitchPatterns (phrase.getNoteArray (), chunkSize);
    }

    /**
     * Returns a double expressing the number of repetitions of pitch interval
     * patterns of a specified size, compared to the total possible number of
     * patterns in the specified Note array.
     *
     * @param noteArray Note array to be analysed.
     * @param chunkSize integer describing the size of patterns to count
     * @exception NoteListException 
     *                  if the melody does not have more <A HREF="#Note">
     *                  notes</A> than the size of the patterns searched for
     */
    public static double repeatedPitchPatterns (final Note[] noteArray,
                                                final int chunkSize)
                                        throws NoteListException {
        int possiblePatterns = intervalCount (noteArray) - chunkSize;
        if (possiblePatterns > 0) {
            return pitchPatternCount (noteArray, chunkSize)
                   / (double) possiblePatterns;
        } else {
            throw new NoteListException ("The melody must contain more intervals"
                                         + " than the size of the pattern being"
                                         + " searched for.");
        }
    }

    /**
     * Returns a double expressing the number of repetitions of rhythm interval
     * patterns of a specified size, compared to the total possible number of
     * patterns in the specified Phrase.
     *
     * @param phrase    Phrase to be analysed.
     * @param chunkSize integer describing the size of patterns to count
     * @exception NoteListException 
     *                  if the melody does not have more <A HREF="#Note">
     *                  notes</A> than the size of the patterns searched for
     */
    public static double repeatedRhythmPatterns (final Phrase phrase,
                                                 final int chunkSize)
                                         throws NoteListException {
        return repeatedRhythmPatterns (phrase.getNoteArray (), chunkSize);
    }

    /**
     * Returns a double expressing the number of repetitions of rhythm interval
     * patterns of a specified size, compared to the total possible number of
     * patterns in the specified Note array.
     *
     * @param noteArray Note array to be analysed.
     * @param chunkSize integer describing the size of patterns to count
     * @exception NoteListException 
     *                  if the melody does not have more <A HREF="#Note">
     *                  notes</A> than the size of the patterns searched for
     */
    public static double repeatedRhythmPatterns (final Note[] noteArray,
                                                 final int chunkSize)
                                         throws NoteListException {
        int possiblePatterns = intervalCount (noteArray) - chunkSize;
        if (possiblePatterns > 0) {
            return rhythmPatternCount (noteArray, chunkSize)
                   / (double) possiblePatterns;
        } else {
            throw new NoteListException( "The melody must contain more"
                                         + " intervals than the size of the"
                                         + " pattern being searched for.");
        }
    }

    /**
     * Returns a double representing where the climax of the melody starts.  The
     * value is a percentage of the complete melody.  The exact formula is the 
     * sum of the rhythm values of all notes prior to the climax, divided by the
     * sum of all the rhythm values in the melody.
     *
     * <P>The climax is defined as the last highest note.
     *
     * @param phrase    Phrase to be analysed
     * @return          double describing the position of the climax
     */
    public static double climaxPosition (final Phrase phrase)
                                 throws NoteListException {
        return climaxPosition (phrase.getNoteArray ());
    }

    /**
     * Returns a double representing where the climax of the melody starts.  The
     * value is a percentage of the complete melody.  The exact formula is the 
     * sum of the rhythm values of all notes prior to the climax, divided by the
     * sum of all the rhythm values in the melody.
     *
     * <P>The climax is defined as the last highest note.
     *
     * @param noteArray Note array to be analysed
     * @return          double describing the position of the climax
     */
    public static double climaxPosition (final Note[] noteArray)
                                 throws NoteListException {
        if (noteCount (noteArray) > 0) {
            double length = 0;
            int highestPitch = 0;
            int index = 0;

            for (int i = 0; i < noteArray.length; i++) {
                length += noteArray[i].getRhythmValue ();
                int currentPitch = noteArray[i].getPitch ();
                if (currentPitch != jm.JMC.REST) {
                    if (currentPitch >= highestPitch) {
                        highestPitch = currentPitch;
                        index = i;
                    }
                }
            }
            double lengthToClimax = 0;
            for (int i = 0; i < index - 1; i++) {
                lengthToClimax += noteArray[i].getRhythmValue ();
            }
            return lengthToClimax / length;
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " one note.");
        }

    }

    /**
     * Returns a double rating the tonality of the specified Phrase.  The climax
     * is the last highest pitch, and given that pitch this method will return
     * a value according to the table:
     *
     * <CENTER>
     * <TABLE BORDER="1"><TR><TH>Pitch</TH><TH>Value returned</TH></TR>
     * <TR><TH>Tonic or Dominant</TH><TH>0.0</TH></TR>
     * <TR><TH>Other scale note</TH><TH>0.5</TH></TR>
     * <TR><TH>Non-scale note</TH><TH>1.0</TH></TR></TABLE>
     * </CENTER>
     *
     * <P>How the pitch rates depends on the scale of the melody.  This
     * information is passed to this method through an integer describing the
     * tonic, and a integer array describing the scale.
     *
     * @param phrase    Phrase to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double climaxTonality (Phrase phrase, int tonic, int[] scale)
                                 throws NoteListException {
        return climaxTonality (phrase.getNoteArray (), tonic, scale);
    }

    /**
     * Returns a double rating the tonality of the specified Note array.  The
     * climax is the last highest pitch, and given that pitch this method will
     * return a value according to the table:
     *
     * <CENTER>
     * <TABLE BORDER="1"><TR><TH>Pitch</TH><TH>Value returned</TH></TR>
     * <TR><TH>Tonic or Dominant</TH><TH>0.0</TH></TR>
     * <TR><TH>Other scale note</TH><TH>0.5</TH></TR>
     * <TR><TH>Non-scale note</TH><TH>1.0</TH></TR></TABLE>
     * </CENTER>
     *
     * <P>How the pitch rates depends on the scale of the melody.  This
     * information is passed to this method through an integer describing the
     * tonic, and a integer array describing the scale.
     *
     * @param noteArray Note array to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static double climaxTonality (Note[] noteArray, int tonic, 
                                         int[] scale)
                                 throws NoteListException {
        if (noteCount (noteArray) > 0) {
            int highestPitch = (int)Note.MIN_PITCH;
            int currentPitch;
    
            for (int i = 0; i < noteArray.length; i++) {
                currentPitch = noteArray[i].getPitch ();
                if (currentPitch != jm.JMC.REST) {
                    if (currentPitch > highestPitch) {
                        highestPitch = currentPitch;
                    }
                }
            }

            highestPitch = pitchToDegree (highestPitch, tonic);

            if (isElementOf (highestPitch, PRIMARY_NOTES)) {
                return 0.0;
            } else if (isElementOf (highestPitch, scale)) {
                return 0.5;
            } else {
                return 1.0;
            }
        } else {
            throw new NoteListException ("The melody should contain at least"
                                         + " one note.");
        }
    }

    /**
     * Returns the number of <A HREF="#Note">notes</A> in a specified {@link
     * Phrase}.  
     * 
     * @param phrase    Phrase whose notes are to be counted
     * @return          integer describing the number of non-rest Notes in the
     *                  Phrase
     */
    public static int noteCount (final Phrase phrase) {
        return noteCount (phrase.getNoteArray ());
    }

    /**
     * Returns the number of <A HREF="#Note">notes</A> in a specified {@link
     * Note} array.  
     * 
     * @param noteArray Note array whose notes are to be counted
     * @return          integer describing the number of non-rest Notes in the
     *                  Note array.
     */
    public static int noteCount (final Note[] noteArray) {
        int count = 0;
        for (int i = 0; i < noteArray.length; i++) {
            if (noteArray[i].getPitch() != jm.JMC.REST) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of <A HREF="#Quantum">quanta</A> in a specified {@link
     * Phrase}.  
     *
     * @param phrase    Phrase whose quanta are to be counted
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This value must be greater than 0.
     * @return          integer describing the length of the Phrase measured in
     *                  quanta.
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of the
     *                  specified quantum duration, or if quantum duration is
     *                  less than or equal to zero
     */
    public static int quantumCount (final Phrase phrase, final double duration)
                            throws QuantisationException {
        return quantumCount (phrase.getNoteArray(), duration);
    }

    /**
     * Returns the number of <A HREF="#Quantum">quanta</A> in a specified {@link
     * Note} array.  
     *
     * @param noteArray Note array whose quanta are to be counted
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This value must be greater than 0.
     * @return          integer describing the length of the Note array measured
     *                  in quanta.
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of the
     *                  specified quantum duration, or if quantum duration is
     *                  less than or equal to zero
     */
    public static int quantumCount (final Note[] noteArray,
                                    final double duration)
                            throws QuantisationException{
        if (isQuantised (noteArray, duration)) {
            int count = 0;
            for (int i = 0; i < noteArray.length; i++) {
                count += (int) (noteArray[i].getRhythmValue() / duration);
            }
            return count;
        } else {
            throw new QuantisationException ("Every rhythm value must be a"
                                             + " multiple of the quantum"
                                             + " duration.");
        }
    }

    /**
     * Returns the number of pitches that appear at least once in a specified
     * {@link Phrase}. 
     *
     * @param phrase    Phrase to be examined
     * @return          integer describing the number of distinct pitches in the
     *                  Phrase
     */
    public static int distinctPitchCount (final Phrase phrase) {
        return distinctPitchCount (phrase.getNoteArray ());
    }

    /**
     * Returns the number of pitches that appear at least once in a specified
     * {@link Note} array. 
     *
     * @param noteArray array of Note objects to be examined
     * @return          integer describing the number of distinct pitches in the
     *                  Note array
     */
    public static int distinctPitchCount (final Note[] noteArray) {
        int[] pitchArray = new int[noteArray.length];
        int index = 0;
        int pitch;

        for (int i = 0; i < noteArray.length; i++) {
            pitch = noteArray[i].getPitch ();
            if (pitch != jm.JMC.REST) {
                if (! isElementOf (pitch, pitchArray, index)) {
                    pitchArray[index] = pitch;
                    index++;
                }
            }
        }
        return index;
    }

    /**
     * Returns the number of rhythms that appear at least once in a specified
     * {@link Phrase}. 
     *
     * @param phrase    Phrase to be examined
     * @return          number of distinct pitches in the Note array
     */
    public static int distinctRhythmCount (final Phrase phrase) {
        return distinctRhythmCount (phrase.getNoteArray ());
    }

    /**
     * Returns the number of rhythms that appear at least once in a specified
     * {@link Note} array. 
     *
     * @param noteArray array of Note objects to be examined
     * @return          integer describing the number of distinct pitches in the
     *                  Note array
     */
    public static int distinctRhythmCount (final Note[] noteArray) {
        double[] rhythmArray = new double[noteArray.length];
        int index = 0;
        double rhythm;
        for (int i = 0; i < noteArray.length; i++) {
            if (noteArray[i].getPitch() <= jm.JMC.REST) {
                rhythm = noteArray[i].getRhythmValue ();
                if (! isElementOf (rhythm, rhythmArray, index)) {
                    rhythmArray[index] = rhythm;
                    index++;
                }
            }
        }
        return index;
    }

    /**
     * Returns the number of silent <A HREF="#Quantum">quanta</A> in the
     * specified {@link Phrase}.  Silent quanta are quanta representing rests.
     * Silent quanta do not include the quanta representing notes with a
     * dynamic, or volume, of zero.
     *
     * @param phrase    Phrase whose silent quanta are to be measured
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          integer describing the number of silent quanta in phrase
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static int restQuantumCount (final Phrase phrase,
                                        final double duration)
                                throws QuantisationException {
        return restQuantumCount (phrase.getNoteArray (), duration);
    }

    /**
     * Returns the number of silent <A HREF="#Quantum">quanta</A> in the
     * specified {@link Note} array.  Silent quanta are quanta representing
     * rests.  Silent quanta do not include the quanta representing notes with
     * a dynamic, or volume, of zero.
     *
     * @param noteArray Note array whose silent quanta are to be measured
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @return          integer describing the number of silent quanta in phrase
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static int restQuantumCount (final Note[] noteArray,
                                        final double duration)
                                throws QuantisationException {
        if (isQuantised (noteArray, duration)) {
            int count = 0;
            for (int i = 0; i < noteArray.length; i++) {
                if (noteArray[i].getPitch () == jm.JMC.REST) {
                    count += (int) (noteArray[i].getRhythmValue () / duration);
                }
            }
            return count;
        } else {
            throw new QuantisationException ("Every rhythm value must be a"
                                             + " multiple of the quantum"
                                             + " duration.");
        }
    }

    /**
     * Returns the count of the <A HREF="#Quantum">quanta</A> taken up by
     * non-scale notes in the specified Phrase.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param phrase    Phrase to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          integer describing the number of non-scale notes in the
     *                  specified Phrase
     */
    public static int nonScaleQuantumCount (final Phrase phrase,
                                            final double duration,
                                            final int tonic, final int[] scale)
                                    throws QuantisationException {
        return nonScaleQuantumCount (phrase.getNoteArray (), duration, tonic,
                                     scale);
    }

    /**
     * Returns the count of the <A HREF="#Quantum">quanta</A> taken up by
     * non-scale notes in the specified Note array.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param noteArray Note array to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          integer describing the number of non-scale notes in the
     *                  specified Note array
     */
    public static int nonScaleQuantumCount (final Note[] noteArray,
                                            final double duration,
                                            final int tonic, final int[] scale)
                                    throws QuantisationException {
        if (isQuantised (noteArray, duration)) {
            int count = 0;
            int pitch;
            for (int i = 0; i < noteArray.length; i++) {
                pitch = noteArray[i].getPitch ();
                if (pitch != jm.JMC.REST
                        && ! isElementOf (pitchToDegree (pitch, tonic),
                                          scale)) {
                    count += (int) (noteArray[i].getRhythmValue ()
                                    / duration);
                }
            }
            return count;
        } else {
            throw new QuantisationException ("Every rhythm value must be a"
                                             + " multiple of the quantum"
                                             + " duration.");
        }

    }

    /**
     * Returns the count of the <A HREF="#Quantum">quanta</A> taken up by
     * 'primary notes' in the specified Phrase.  Primary notes are notes whose
     * pitch relative to the tonic is listed in the {@link #PRIMARY_NOTES}
     * array.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param phrase    Phrase to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @return          integer describing the number of 'primary notes' in the
     *                  specified Phrase
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static int primaryQuantumCount (final Phrase phrase,
                                           final double duration, 
                                           final int tonic)
                                   throws QuantisationException {
        return primaryQuantumCount (phrase.getNoteArray (), duration, tonic);
    }

    /**
     * Returns the count of the <A HREF="#Quantum">quanta</A> taken up by
     * 'primary notes' in the specified Note array.  Primary notes are notes
     * whose pitch relative to the tonic is listed in the {@link #PRIMARY_NOTES}
     * array.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param noteArray Note array to be analysed
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0.
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @return          integer describing the number of 'primary notes' in the
     *                  specified Note array
     * @exception QuantisationException
     *                  if the Notes' rhythm values are not multiples of quantum
     *                  duration, or if quantum duration is less than or equal
     *                  to zero
     */
    public static int primaryQuantumCount (final Note[] noteArray,
                                           final double duration,
                                           final int tonic)
                                    throws QuantisationException {
        if (isQuantised (noteArray, duration)) {
            int count = 0;
            int pitch;

            for (int i = 0; i < noteArray.length; i++) {
                pitch = noteArray[i].getPitch ();
                if (pitch == jm.JMC.REST 
                        || isElementOf (pitchToDegree (pitch, tonic),
                                        PRIMARY_NOTES)) {
                    count += (int) (noteArray[i].getRhythmValue ()
                                    / duration);
                }
            }
            return count;
        } else {
            throw new QuantisationException ("Every rhythm value must be a"
                                             + " multiple of the quantum"
                                             + " duration.");
        }
    }        

    /**
     * Returns the difference between the highest and lowest pitches in the
     * specified {@link Phrase}.
     *
     * @param phrase    Phrase whose notes are to be examined
     * @return          integer describing the difference between the highest
     *                  and lowest pitches
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static int pitchRange (final Phrase phrase)
                          throws NoteListException {
        return pitchRange (phrase.getNoteArray ());
    }

    /**
     * Returns the difference between the highest and lowest pitches in the
     * specified {@link Note} array.
     *
     * @param noteArray Note array to be examined
     * @return          integer describing the difference between the highest
     *                  and lowest pitches
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static int pitchRange (final Note[] noteArray)
                          throws NoteListException {
        int highestPitch = (int)Note.MIN_PITCH;
        int lowestPitch = (int)Note.MAX_PITCH;
        int currentPitch;

        for (int i = 0; i < noteArray.length; i++) {
            currentPitch = noteArray[i].getPitch ();
            if (currentPitch != (int)jm.JMC.REST) {
                if (currentPitch > highestPitch) {
                    highestPitch = currentPitch;
                } else if (currentPitch < lowestPitch) {
                    lowestPitch = currentPitch;
                }
            }
        }
        if (highestPitch != (int)Note.MIN_PITCH && lowestPitch != (int)Note.MAX_PITCH) {
            return highestPitch - lowestPitch;
        } else {
            throw new NoteListException ("There are no notes in the melody.");
        }
    }

    /**
     * Returns the value of the longest rhythm value, divided by the shortest
     * in the specified Phrase.
     *
     * @param phrase    Phrase whose notes are to be examined
     * @return          double describing the longest rhythm value, divided by
     *                  the shortest
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Phrase
     */
    public static double rhythmRange (final Phrase phrase)
                              throws NoteListException {
        return rhythmRange (phrase.getNoteArray ());
    }

    /**
     * Returns the value of the longest rhythm value, divided by the shortest
     * in the specified Note array.
     *
     * @param phrase    Phrase whose notes are to be examined
     * @return          double describing the longest rhythm value, divided by
     *                  the shortest
     * @exception NoteListException
     *                  if there are no <A HREF="#Note">notes</A> in the Note
     *                  array
     */
    public static double rhythmRange (final Note[] noteArray)
                              throws NoteListException {
        double longestRhythm = Note.MIN_RHYTHM_VALUE;
        double shortestRhythm = Note.MAX_RHYTHM_VALUE;
        double currentRhythm;

        for (int i = 0; i < noteArray.length; i++) {
            currentRhythm = noteArray[i].getRhythmValue ();
            if (noteArray[i].getPitch() != jm.JMC.REST) {
                if (currentRhythm > longestRhythm) {
                    longestRhythm = currentRhythm;
                } else if (currentRhythm < shortestRhythm) {
                    shortestRhythm = currentRhythm;
                }
            }
        }
        if (longestRhythm != Note.MIN_RHYTHM_VALUE
                && shortestRhythm != Note.MAX_RHYTHM_VALUE) {
            return longestRhythm / shortestRhythm;
        } else {
            throw new NoteListException("There are no notes in the melody.");
        }
    }

    /**
     * Returns the count of intervals whose size is 0 semitones in the specified
     * Phrase.  If there are less than two notes in the Phrase, that is if there
     * are no intervals, then this method returns 0.
     *
     * <P>If there are no note to next note intervals in the melody, this method
     * returns 0 as there are no consecutive identical pitches.
     *
     * @param phrase    Phrase to be analysed
     * @return          integer describing the number of intervals with a size 
     *                  of 0 semitones.
     */
    public static int consecutiveIdenticalPitches (final Phrase phrase) {
        return consecutiveIdenticalPitches (phrase.getNoteArray ());
    }

    /**
     * Returns the count of intervals whose size is 0 semitones in the specified
     * Note array.  If there are less than two notes in the Note array, that is
     * if there are no intervals, then this method returns 0.
     *
     * <P>If there are no note to next note intervals in the melody, this method
     * returns 0 as there are no consecutive identical pitches.
     *
     * @param noteArray Note array to be analysed
     * @return          integer describing the number of intervals with a size 
     *                  of 0 semitones.
     */
    public static int consecutiveIdenticalPitches (final Note[] noteArray) {
        int[] intervals = pitchIntervals (noteArray);
        int count = 0;
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] == 0 || intervals[i] == INTERVAL_WITH_REST) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the count of intervals between Notes of the same rhythm value in
     * the specified Phrase.  If there are less than two Notes in the Phrase, 
     * that is if there are no intervals, then this method returns 0. Whether
     * the rhythm values match is independent of whether either is a rest, so a
     * note and a rest with the same rhythm value would match.
     *
     * <P>If there are no Notes in the melody, this method returns 0 as ther are
     * no consecutve indentical rhythm values.
     *
     * @param phrase    Phrase to be analysed
     * @return          integer describing the number of intervals with a size
     *                  of 0 semitones.
     */
    public static int consecutiveIdenticalRhythms (final Phrase phrase) {
        return consecutiveIdenticalRhythms (phrase.getNoteArray ());
    }

    /**
     * Returns the count of intervals between notes of the same rhythm value in
     * the specified Note array.  If there are less than two notes in the Note
     * array, that is if there are no intervals, then this method returns 0.
     *
     * @param noteArray Note array to be analysed
     * @return          integer describing the number of intervals with a size
     *                  of 0 semitones.
     */
    public static int consecutiveIdenticalRhythms (final Note[] noteArray) {
        double[] intervals = rhythmIntervals (noteArray);
        int count = 0;
        for (int i = 0; i < intervals.length - 1; i++) {
            if (intervals[i] == 1 && intervals[i + 1] > 0
                    || intervals[i] == -1 && intervals[i + 1] < 0) {
                count++;
                
            }
        }

        // The final note is always a note, so we don't need to check
        if (intervals[intervals.length - 1] == 1) {
            count++;
        }
        return count;
    }

    /**
     * Returns the count of consecutive intevals in the same direction, in the
     * specified Phrase.  If there are less than three notes in the Phrase, that
     * is if there are less than two consecutive intervals, this method returns
     * 0.
     *
     * @param phrase    Phrase to be analysed
     * @return          Integer describing the count of consecutive intervals in
     *                  the same direction
     */
    public static int sameDirectionIntervalCount (final Phrase phrase) {
        return sameDirectionIntervalCount (phrase.getNoteArray ());
    }

    /**
     * Returns the count of consecutive intevals in the same direction, in the
     * specified Note array.  If there are less than three notes in the Note
     * array, that is if there are less than two consecutive intervals, this 
     * method returns 0.
     *
     * @param noteArray Note array to be analysed
     * @return          Integer describing the count of consecutive intervals in
     *                  the same direction
     */
    public static int sameDirectionIntervalCount (final Note[] noteArray) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);
        if (intervalArray.length > 0) {

            // Remove the rest marker, if found
            if (intervalArray[0] > Note.MAX_PITCH - Note.MIN_PITCH) {
                intervalArray[0] -= INTERVAL_WITH_REST;
            }
            for (int i = 1; i < intervalArray.length; i++) {

                // Remove the rest marker, if found
                if (intervalArray[i] > Note.MAX_PITCH - Note.MIN_PITCH) {
                    intervalArray[i] -= INTERVAL_WITH_REST;
                }

                if ((intervalArray[i] > 0 && intervalArray[i - 1] > 0) 
                        || (intervalArray[i] == 0 && intervalArray[i - 1] == 0)
                        || (intervalArray[i] < 0 && intervalArray[i - 1] < 0)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns a count of the number of <A HREF="#Note">note</A> to next note
     * intervals in the specified Phrase.  These intervals can have 0 or more
     * rests between the notes.
     *
     * @param phrase    Phrase to be analysed
     * @return          Count of intervals
     */
    public static int intervalCount (final Phrase phrase) {
        return intervalCount (phrase.getNoteArray ());
    }

    /**
     * Returns a count of the number of <A HREF="#Note">note</A> to next note
     * intervals in the specified Phrase.  These intervals can have 0 or more
     * rests between the notes.
     *
     * @param noteArray Note array to be analysed
     * @return          Count of intervals
     */
    public static int intervalCount (final Note[] noteArray) {
        int intervalCount = noteCount (noteArray) - 1;
        return (intervalCount < 1) ? 0 : intervalCount;
    }

    /**
     * Returns an array of integers describing the size of intervals between the
     * <A HREF="#Note">notes</A> in the specified {@link Phrase}.  If an
     * interval has no rests between its notes the integer representing it is
     * unchanged.  However, if the interval has 1 or more rests between its
     * notes the integer is increased by the value of the
     * {@link #INTERVAL_WITH_REST} constant.
     *
     * <P>Given any value in the array returned you can determine if it
     * represents an interval with 1 or more rests between its notes in the
     * following manner.  If the value is greater than the largest valid rising
     * interval, namely ({@link Note#MAX_PITCH} - {@link Note#MIN_PITCH}), then
     * it represents an interval with a rest.  Otherwise it represents an
     * interval without a rest.
     *
     * <P>Given a value representing an interval with a rest, you can determine
     * the size and direction of the interval by subtracting INTERVAL_WITH_REST
     * from the value.
     *
     * @param phrase    Phrase to be examined
     * @return          array of integers describing the intervals 
     */
    public static int[] pitchIntervals (final Phrase phrase) {
        return pitchIntervals (phrase.getNoteArray ());
    }

    /**
     * Returns an array of integers describing the size of intervals between the
     * <A HREF="#Note">notes</A> in the specified {@link Note} array.  If an
     * interval has no rests between its notes the integer representing it is
     * unchanged.  However, if the interval has 1 or more rests between its
     * notes the integer is increased by the value of the
     * {@link #INTERVAL_WITH_REST} constant.
     *
     * <P>Given any value in the array returned you can determine if it
     * represents an interval with 1 or more rests between its notes in the
     * following manner.  If the value is greater than the largest valid rising
     * interval, namely ({@link Note#MAX_PITCH} - {@link Note#MIN_PITCH}), then
     * it represents an interval with a rest.  Otherwise it represents an
     * interval without a rest.
     *
     * <P>Given a value representing an interval with a rest, you can determine
     * the size and direction of the interval by subtracting INTERVAL_WITH_REST
     * from the value.
     *
     * @param noteArray Note array to be examined
     * @return          array of integers describing the intervals
     */
    public static int[] pitchIntervals (final Note[] noteArray) {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount > 0) {
            int[] intervalArray = new int[intervalCount];
            int index = -1;

            // Find the first non-rest note
            while (noteArray[++index].getPitch () == jm.JMC.REST) {
            }
            int previousPitch = noteArray[index].getPitch ();
            int currentPitch;

            for (int i = 0; i < intervalArray.length; i++) {
                currentPitch = noteArray[++index].getPitch ();

                // Intervals with one or more rests should be increased by
                // INTERVAL_WITH_REST
                while (currentPitch == jm.JMC.REST) {

                    // Only make the change for the first rest
                    if (noteArray[index - 1].getPitch () != jm.JMC.REST) {
                        intervalArray[i] += INTERVAL_WITH_REST;
                    }

                    currentPitch = noteArray[++index].getPitch ();
                }

                intervalArray[i] += currentPitch - previousPitch;
                previousPitch = currentPitch;
            }
            return intervalArray;
        } else {
            return new int[0];
        }
    }

    /**
     * Returns an array of doubles describing the change in rhythm value in
     * intervals in the specified {@link Phrase}.  The absolute value for any
     * given interval is the length of the second note, divided by the rhythm
     * value of the first.  Additionally, if the first note of the interval is a
     * rest the sign of the value is negative.
     *
     * <P>Intervals involving any trailing rests are ignored.  So the second
     * note of the final interval is guaranteed to not be a rest.
     *
     * @param phrase    Phrase to be examined
     * @return          array of integers describing the intervals 
     */
    public static double[] rhythmIntervals (final Phrase phrase) {
        return rhythmIntervals (phrase.getNoteArray ());
    }

    /**
     * Returns an array of doubles describing the change in rhythm value in
     * intervals in the specified {@link Note} array.  The absolute value for
     * any given interval is the length of the second note, divided by the
     * rhythm value of the first.  Additionally, if the first note of the
     * interval is a rest the sign of the value is negative.
     *
     * <P>Intervals involving any trailing rests are ignored.  So the second
     * note of the final interval is guaranteed to not be a rest.
     *
     * @param phrase    Phrase to be examined
     * @return          array of integers describing the intervals 
     */
    public static double[] rhythmIntervals (final Note[] noteArray) {
        int intervalCount = noteArray.length - 1;

        // Trailing rests do not count towards intervals
        for (int i = noteArray.length - 1; 
             noteArray[i].getPitch () == jm.JMC.REST && i > -1;
             i--) {
            intervalCount--;
        }

        if (intervalCount > 0) {
            double[] intervalArray = new double[intervalCount];
            for (int i = 0; i < intervalArray.length; i++) {
                intervalArray[i] = noteArray[i + 1].getRhythmValue ()
                                   / noteArray[i].getRhythmValue ();

                // Indicate a rest with a negative sign
                if (noteArray[i].getPitch () == jm.JMC.REST) {
                    intervalArray[i] *= -1;
                }
            }
            return intervalArray;
        } else  {
            return new double[0];
        }
    }        

    /**
     * Returns the sum of the absolute sizes of all intervals in the specified
     * Phrase.
     *
     * @param phrase    Phrase to be analysed
     * @return          sum of intervals
     */
    public static int intervalSemitoneCount (final Phrase phrase) {
        return intervalSemitoneCount (phrase.getNoteArray ());
    }        

    /**
     * Returns the sum of the absolute sizes of all intervals in the specified
     * Note array.
     *
     * @param noteArray Note array to be analysed
     * @return          sum of intervals
     */
    public static int intervalSemitoneCount (final Note[] noteArray) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);
        for (int i = 0; i < intervalArray.length; i++) {
            count += Math.abs (removeRestMarker (intervalArray[i]));
        }
        return count;
    }

    /**
     * Returns the sum of the sizes of all rising intervals in the specified
     * Phrase.
     *
     * @param phrase    Phrase to be analysed
     * @return          sum of rising intervals
     */
    public static int risingSemitoneCount (final Phrase phrase) {
        return risingSemitoneCount (phrase.getNoteArray ());
    }

    /**
     * Returns the sum of the sizes of all rising intervals in the specified
     * Note array.
     *
     * @param noteArray Note array to be analysed
     * @return          sum of rising intervals
     */
    public static int risingSemitoneCount (final Note[] noteArray) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);
        for (int i = 0; i < intervalArray.length; i++) {
            intervalArray[i] = removeRestMarker (intervalArray[i]);
            if (intervalArray[i] > 0) {
                count += intervalArray[i];
            }
        }
        return count;
    }

    /**
     * Returns a count of all the step intervals in the specified Phrase.  A
     * step interval is an interval between two scale <A HREF="#Note">notes</A>,
     * whose difference is at most one tone.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param phrase    Phrase to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the movement by step
     */
    public static int stepIntervalCount (final Phrase phrase, final int tonic,
                                         final int[] scale) {
        return stepIntervalCount (phrase.getNoteArray (), tonic, scale);
    }

    /**
     * Returns a count of all the step intervals in the specified Note array.  A
     * step interval is an interval between two scale <A HREF="#Note">notes</A>,
     * whose difference is at most one tone.
     *
     * <P>The key signature of the melody is specified by parameters passed to
     * this method, namely an integer describing the tonic, and a integer array
     * describing the scale.
     *
     * @param noteArray Note array to be analysed
     * @param tonic     integer representing the pitch of the tonic in
     *                  semitones, ranging from 0 (C natural) to 11 (B flat).
     * @param scale     array of integers describing the scale notes in the key
     *                  of the melody.  If the key is major use the {@link
     *                  #MAJOR_SCALE} constant of this class; if harmonic minor
     *                  use {@link #MINOR_SCALE}.  Arrays representing other
     *                  keys can also be created but they must fit the criteria
     *                  outlined in the descriptions of those constants.
     * @return          double describing the movement by step
     */
    public static int stepIntervalCount (final Note[] noteArray, final int tonic,
                                         final int[] scale) {
        int intervalCount = intervalCount (noteArray);
        if (intervalCount > 0) {
            int index = -1;
            int count = 0;

            // Find the first non-rest note
            while (noteArray[++index].getPitch () == jm.JMC.REST) {
            }
            int previousPitch = noteArray[index].getPitch ();
            int currentPitch;

            for (int i = 0; i < intervalCount; i++) {
                while (noteArray[++index].getPitch () == jm.JMC.REST) {
                }
                currentPitch = noteArray[index].getPitch ();

                if (Math.abs (currentPitch - previousPitch) < 3
                        && isElementOf (pitchToDegree (currentPitch, tonic),
                                       scale)
                        && isElementOf (pitchToDegree (previousPitch, tonic),
                                       scale)) {
                    count++;
                }
                previousPitch = currentPitch;
            }
            return count;
        } else {
            return 0;
        }
    }

    /**
     * Returns the number of big jumps followed by a step back in the specified
     * Phrase.  A big jump is an interval whose absolute size is equal to or
     * greater than {@link #BIG_JUMP_INTERVAL}.  A step back is a interval in
     * the opposite direction whose size is at least one semitone and less than
     * or equal to {@link #BIG_JUMP_INTERVAL}.
     *
     * @param phrase    Phrase to be analysed
     * @return          the number of big jumps
     */
    public static int bigJumpFollowedByStepBackCount (final Phrase phrase) {
        return bigJumpFollowedByStepBackCount (phrase.getNoteArray ());
    }

    /**
     * Returns the number of big jumps followed by a step back in the specified
     * Note array.  A big jump is an interval whose absolute size is equal to or
     * greater than {@link #BIG_JUMP_INTERVAL}.  A step back is a interval in
     * the opposite direction whose size is at least one semitone and less than
     * or equal to {@link #BIG_JUMP_INTERVAL}.
     *
     * @param noteArray Note array to be analysed
     * @return          the number of big jumps
     */
    public static int bigJumpFollowedByStepBackCount (final Note[] noteArray) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);
        if (intervalArray.length > 0) {
            intervalArray[0] = removeRestMarker (intervalArray[0]);
            for (int i = 1; i < intervalArray.length - 1; i++) {
                intervalArray[i] = removeRestMarker (intervalArray[i]);
                if ((intervalArray[i - 1] >= BIG_JUMP_INTERVAL
                            && intervalArray[i] < 0
                            && intervalArray[i] >= 0 - BIG_JUMP_INTERVAL)
                        || (intervalArray[i - 1] <= 0 - BIG_JUMP_INTERVAL
                            && intervalArray[i] > 0
                            && intervalArray[i] <= BIG_JUMP_INTERVAL)) {
                    count++;
                }
            }
            return count;
        } 
        return 0;
    }

    /**
     * Returns the number of big jumps in the specified Phrase.  A big jump
     * is an interval whose absolute size is equal to or greater than {@link
     * #BIG_JUMP_INTERVAL}.
     *
     * @param phrase    Phrase to be analysed
     * @return          the number of big jumps
     */
    public static int bigJumpCount (final Phrase phrase) {
        return bigJumpCount (phrase.getNoteArray ());
    }                    

    /**
     * Returns the number of big jumps in the specified Note array.  A big jump
     * is an interval whose absolute size is equal to or greater than {@link
     * #BIG_JUMP_INTERVAL}.
     *
     * @param phrase    Phrase to be analysed
     * @return          the number of big jumps
     */
    public static int bigJumpCount (final Note[] noteArray) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);
        if (intervalArray.length > 0) {
            for (int i = 0; i < intervalArray.length - 1; i++) {
                intervalArray[i] = removeRestMarker (intervalArray[i]);
                if (Math.abs (intervalArray[i]) >= BIG_JUMP_INTERVAL) {
                    count++;
                }
            }
            return count;
        }
        return count;
            
    }

    /**
     * Returns the number of 'pitch interval' patterns of size <CODE>
     * chunksize</CODE> in the specified Phrase.  A 'pitch interval' is simply
     * the difference between the pitches of the notes, however there is a 
     * distinction between intervals with no rests and intervals with one or
     * more rests in between the two notes of the interval.
     *
     * @param phrase    Phrase to be searched
     * @param chunksize The size of the pattern to search for
     * @return          integer describing the number of 'pitch interval' 
     *                  patterns
     */
    public static int pitchPatternCount (final Phrase phrase,
                                         final int chunkSize) {
        return pitchPatternCount (phrase.getNoteArray (), chunkSize);
    }

    /**
     * Returns the number of 'pitch interval' patterns of size <CODE>
     * chunksize</CODE> in the specified Note array.  A 'pitch interval' is
     * simply the difference between the pitches of the notes, however there is
     * a distinction between intervals with no rests and intervals with one or
     * more rests in between the two notes of the interval.
     *
     * @param noteArray Note array to be searched
     * @param chunksize The size of the pattern to search for
     * @return          integer describing the number of 'pitch interval' 
     *                  patterns
     */
    public static int pitchPatternCount (final Note[] noteArray,
                                         final int chunkSize) {
        int count = 0;
        int[] intervalArray = pitchIntervals (noteArray);

        if (intervalArray.length > chunkSize) {
            int[][] patterns =
                    new int[intervalArray.length - chunkSize][chunkSize];
            int index = 0;

            for (int i = 0; i < intervalArray.length - chunkSize; i++) {
                int[] match = new int[chunkSize];

                for (int j = 0; j < chunkSize; j++) {
                    match[j] = intervalArray[i + j];
                }
                if (!isAlreadyMatched (patterns, match, index)) {
                    for (int j = i + 1;
                         j < intervalArray.length - chunkSize + 1;
                         j++) {
                        if (matchPattern (intervalArray, i, j, chunkSize)) {
                            if (index == 0 || patterns[index - 1] != match) {
                                patterns[index++] = match;
                            }
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Returns the number of 'rhythm interval' patterns of size <CODE>
     * chunksize</CODE> in the specified Phrase.  A 'rhythm interval' is
     * calculated according to the following formula.  The absolute value of the
     * 'rhythm interval' is the length of the second note, divided by the rhythm
     * value of the first.  Additionally, if the interval has one or more rests
     * in between the notes, the sign of the value is negative.
     *
     * @param phrase    Phrase to be searched
     * @param chunksize The size of the pattern to search for
     * @return          integer describing the number of 'rhythm interval'
     *                  patterns
     */
    public static int rhythmPatternCount(final Phrase phrase,
                                         final int chunksize) {
        return rhythmPatternCount(phrase.getNoteArray (), chunksize);
    }

    /**
     * Returns the number of 'rhythm interval' patterns of size <CODE>
     * chunksize</CODE> in the specified Note array.  A 'rhythm interval' is
     * calculated according to the following formula.  The absolute value of the
     * 'rhythm interval' is the length of the second note, divided by the rhythm
     * value of the first.  Additionally, if the interval has one or more rests
     * in between the notes, the sign of the value is negative.
     *
     * @param noteArray Note array to be searched
     * @param chunksize The size of the pattern to search for
     * @return          integer describing the number of 'rhythm interval'
     *                  patterns
     */

    public static int rhythmPatternCount(final Note[] noteArray,
                                         final int chunkSize) {
        int count = 0;
        double[] intervalArray = rhythmIntervals(noteArray);

        if (intervalArray.length > chunkSize) {
            double[][] patterns =
                    new double[intervalArray.length - chunkSize][chunkSize];
            int index = 0;

            for (int i = 0; i < intervalArray.length - chunkSize; i++) {
                double[] match = new double[chunkSize];

                for (int j = 0; j < chunkSize; j++) {
                    match[j] = intervalArray[i + j];
                }
                if (!isAlreadyMatched (patterns, match, index)) {
                    for (int j = i + 1;
                         j < intervalArray.length - chunkSize + 1;
                         j++) {
                        if (matchPattern (intervalArray, i, j, chunkSize)) {
                            if (index == 0 || patterns[index - 1] != match) {
                                patterns[index++] = match;
                            }
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Returns the sum of all rhythm values in the specified Phrase.
     *
     * @param phrase    Phrase to be analysed
     * @return          integer describing the sum of all rhythm values
     */
    public static double rhythmValueCount (final Phrase phrase) {
        return rhythmValueCount (phrase.getNoteArray ());
    }

    /**
     * Returns the sum of all rhythm values in the specified Note array.
     *
     * @param noteArray Note array to be analysed
     * @return          integer describing the sum of all rhythm values
     */
    public static double rhythmValueCount (final Note[] noteArray) {
        double count = 0;
        for (int i = 0; i < noteArray.length; i++) {
            count = count + noteArray[i].getRhythmValue ();
        }
        return count;
    }

    /**
     * Removes the rest marker, if any, from integer which conform to the values
     * returned by {@link #pitchIntervals}.
     *
     * @param interval  integer representing the interval
     * @return          interval with rest marker removed (if present)
     */
    public static int removeRestMarker(final int interval) {
        return (interval > Note.MAX_PITCH - Note.MIN_PITCH)
               ? interval - INTERVAL_WITH_REST
               : interval;
    }

    /**
     * Tests whether a {@link Phrase Phrase} is quantised. If the rhythm value
     * of any Note within the Phrase is not a multiple of <CODE>duration</CODE>
     * then false is returned.
     *
     * @param phrase    Phrase to be tested.
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0, otherwise an
     *                  exception is thrown.
     * @return          true if length of notes are all multiples of
     *                  <CODE>duration</CODE>; otherwise false
     * @exception QuantisationException
     *                  if the quantum duration is less than or equal to zero.
     * @see Note
     * @see Phrase                                  
     */
    public static boolean isQuantised (final Phrase phrase,
                                       final double duration)
                               throws QuantisationException {
        return isQuantised (phrase.getNoteArray (), duration);
    }

    /**
     * Tests an array of {@link Note Notes} to see if they are quantised. If the
     * rhythm value of any of those Notes is not a multiple of
     * <CODE>duration</CODE> then false is returned.
     *
     * @param noteArray array of Notes to be tested.
     * @param duration  double representing the length of a quantum, where 1 is
     *                  a crotchet.  This must be greater than 0, otherwise an
     *                  exception is thrown.
     * @return          true if length of notes are all multiples of
     *                  <CODE>duration</CODE>; otherwise false
     * @exception QuantisationException
     *                  if the quantum duration is less than or equal to zero.
     * @see Note
     * @see Phrase                                  
     */
    public static boolean isQuantised (final Note[] noteArray,
                                       final double duration)
                               throws QuantisationException {
        if (duration > 0) {
            for (int i = 0; i < noteArray.length; i++) {
                if (noteArray[i].getRhythmValue () % duration != 0.0) {
                    return false;
                }
            }
            return true;
        } else {
            throw new QuantisationException ("The quantum duration must be"
                                             + " greater than zero.");
        }                
    }

    /**
     * Returns true if the first <CODE>n</CODE> elements after <CODE>
     * array[firstIndex]</CODE>, match the first <CODE>n</CODE> elements after
     * <CODE>array[secondIndex]</CODE>.
     *
     * @param array         double array whose elements contain the potential
     *                      pattern
     * @param firstIndex    index to the start of the first pattern
     * @param secondIndex   index to the start of the second pattern
     * @param n             the number of element to match
     * @return              true if the first pattern is the same as the second;
     *                      false otherwise
     */
    private static boolean matchPattern (final int[] array,
                                         final int firstIndex,
                                         final int secondIndex, final int n) {
        boolean flag = true;
        for (int i = 0; i < n; i++) {
            if (array[firstIndex + i] != array[secondIndex + i]) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Returns true if the first <CODE>n</CODE> elements after <CODE>
     * array[firstIndex]</CODE>, match the first <CODE>n</CODE> elements after
     * <CODE>array[secondIndex]</CODE>.
     *
     * @param array         double array whose elements contain the potential
     *                      pattern
     * @param firstIndex    index to the start of the first pattern
     * @param secondIndex   index to the start of the second pattern
     * @param n             the number of element to match
     * @return              true if the first pattern is the same as the second;
     *                      false otherwise
     */
    private static boolean matchPattern (final double[] array,
                                         final int firstIndex,
                                         final int secondIndex, final int n) {
        boolean flag = true;
        for (int i = 0; i < n; i++) {
            if (array[firstIndex + i] != array[secondIndex + i]) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Returns true if <CODE>match<CODE> is found within the first <CODE>
     * n</CODE> elements of <CODE>patterns</CODE>.
     *
     * @param patterns  array of integer arrays storing the patterns
     * @param match     integer array to search for
     * @param n         the number of elements of <CODE>patterns</CODE> to
     *                  search
     * @return          true if pattern is found; false otherwise
     */
    private static boolean isAlreadyMatched (final int[][] patterns,
                                             final int[] match,
                                             final int n) {
        search: for (int i = 0; i < n; i++) {
            for (int j = 0; j < match.length; j++) {
                if (patterns[i][j] != match[j]) {
                    continue search;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if <CODE>match<CODE> is found within the first <CODE>
     * n</CODE> elements of <CODE>patterns</CODE>.
     *
     * @param patterns  array of double arrays storing the patterns
     * @param match     double array to search for
     * @param n         the number of elements of <CODE>patterns</CODE> to
     *                  search
     * @return          true if pattern is found; false otherwise
     */   
    private static boolean isAlreadyMatched (final double[][] patterns,
                                             final double[] match,
                                             final int n) {
        search: for (int i = 0; i < n; i++) {
            for (int j = 0; j < match.length; j++) {
                if (patterns[i][j] != match[j]) {
                    continue search;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the dissonance rating of a specified pitch.  The pitch specified
     * must be relative to the tonic, such that a tonic is 0, and a dominant is
     * 7.
     *
     * <P>If the pitch is in the array {@link GOOD_INTERVALS} this method
     * returns 0, if in {@link BAD_INTERVALS} 2, otherwise 1.
     *
     * @param i     Pitch to be rated, relative to the tonic
     * @return      0 if good, 2 if bad, 1 otherwise
     */
    private static int rateDissonance (final int i) {
        for (int j = 0; j < GOOD_INTERVALS.length; j++) {
            if (i == GOOD_INTERVALS[j]) {
                return 0;
            }
        }
        for (int j = 0; j < BAD_INTERVALS.length; j++) {
            if (i == BAD_INTERVALS[j]) {
                return 2;
            }
        }
        return 1;
    }

    /**
     * Returns true if <CODE>element</CODE> is an element of <CODE>array</CODE>
     *
     * @param element   integer to search for
     * @param array     integer array to be searched
     * @return          true if element exists in array
     */
    private static boolean isElementOf (final int element, final int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if <CODE>element</CODE> is one of the first <CODE>n</CODE>
     * elements of <CODE>array</CODE>
     *
     * @param array     array of integers to be searched for match
     * @param element   integer to search for
     * @param n         integer indicating where to/ stop the search in the
     *                  array
     * @return          true if found; false otherwise
     */
    private static boolean isElementOf (final int element, final int[] array,
                                        final int n) {
        for (int i = 0; i < n; i++) {
            if (array[i] == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if <CODE>element</CODE> is one of the first <CODE>n</CODE>
     * elements of <CODE>array</CODE>
     *
     * @param element   double to search forinteger to search for
     * @param array     array of doubles to be searched for match
     * @param n         integer indicating where to stop the search in the array
     * @return          true if found; false otherwise
     */
    private static boolean isElementOf (final double element,
                                        final double[] array, final int n) {
        for (int i = 0; i < n; i++) {
            if (array[i] == element) {
                return true;
            }
        }
        return false;
    }

    public static int pitchToDegree (int pitch, final int tonic) {
        // Make pitch relative to the tonic
        pitch -= tonic;

        // Pitch must be positive for % function to work correctly
        if (pitch < 0) {

            // Give pitch a positive value with an equivalent degree of the 
            // scale
            pitch += ((-pitch / SEMITONES_PER_OCTAVE) + 1) 
                     * SEMITONES_PER_OCTAVE;
        }

        return pitch % SEMITONES_PER_OCTAVE;
    }

    public static boolean isScale (Note note, int tonic, int[] scale) {
        int pitch = note.getPitch ();
        if (pitch == jm.JMC.REST) {
            return true;
        }
        return isElementOf (pitchToDegree (pitch, tonic), scale, scale.length);
    }

}
