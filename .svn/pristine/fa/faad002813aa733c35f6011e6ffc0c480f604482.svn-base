/*               
 * ChordAnalysis.java 0.2.0.6 28th February 2001
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
 *
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:35  2001
 *
 * @since   jMusic February 2000 Release
 */
public final class ChordAnalysis {
    public static final int[] RATINGS = {1, 4, 4, 3, 2, 5, 7};

    // previously called PossibleChords
    private static class Possible {
        int[] chords = null;

        Possible() {
        }

        Possible(int[] chords) {
            this.chords = chords;
        }
        
        int getBestChord() {
            if (chords == null) {
                return -1;
            }

            int currentBest = 6;
            for (int i = 0; i < chords.length; i++) {
                if (RATINGS[chords[i]] < RATINGS[currentBest]) {
                    currentBest = chords[i];
                }
            }
            return currentBest;
        }
    }

    /**
     * This class is not meant to be instantiated, as it contains only class
     * members.
     */
    private ChordAnalysis () {}

    /**
     */
    public static Possible[] getChords(final Phrase phrase,
                                       final double beatLength,
                                       final int tonic, final int[] scale) {
        int[][] triads = new int[scale.length][3];
        for (int i = 0; i < scale.length; i++) {
            triads[i][0] = scale[i];
            triads[i][1] = scale[(i + 2) % scale.length];
            triads[i][2] = scale[(i + 4) % scale.length];
        }

        double startTime = phrase.getStartTime();
        if (startTime < 0.0) {
            startTime = 0.0;
        }
        double rvCount = 0.0;
        int noteCount = 0;
        double endTime = phrase.getEndTime();
        if (endTime == 0.0) {
            return new Possible[0];
        }
        Note downBeat = new Note();
        Note halfBeat = new Note();

        int size = phrase.size();
        final Possible[] chords =
                new Possible[(int) Math.ceil(endTime / beatLength)];

        int i = 0;
        beatLoop: for (i = 0; i < chords.length; i++) {
            if (rvCount == i * beatLength) {
                downBeat = phrase.getNote(noteCount);
            } else {
                downBeat = null;
            }

            while (rvCount < (i + 0.5) * beatLength) {
                rvCount += phrase.getNote(noteCount).getRhythmValue();
                noteCount++;
                if (noteCount >= size) {
                    halfBeat = null;
                    break beatLoop;
                }
            }

            if (rvCount == (i + 0.5) * beatLength) {
                halfBeat = phrase.getNote(noteCount);
            } else {
                halfBeat = null;
            }

            while (rvCount < (i + 1) * beatLength) {
                rvCount += phrase.getNote(noteCount).getRhythmValue();
                noteCount++;
                if (noteCount >= size) {
                    break beatLoop;
                }
            }                         

            chords[i] = firstPass(downBeat, halfBeat, tonic, scale, triads);
        }
        chords[i] = firstPass(downBeat, halfBeat, tonic, scale, triads);

        return chords;
    }

    public static int[] getFirstPassChords(final Phrase phrase,
                                           final double beatLength,
                                           final int tonic,
                                           final int[] scale) {
        Possible[] chords = getChords(phrase, beatLength, tonic, scale);
        int[] returnChords = new int[chords.length];

        for (int j = 0; j < chords.length; j++) {
            if (chords[j] != null) {
                returnChords[j] = chords[j].getBestChord();
            } else {
                returnChords[j] = 7;
            }
        }
        return returnChords;
    }

    public static int[] getSecondPassChords(final Phrase phrase,
                                            final double beatLength,
                                            final int tonic,
                                            final int[] scale) {
        Possible[] chords = getChords(phrase, beatLength, tonic, scale);
        int[] returnChords = new int[chords.length];
        int index = chords.length - 1;
        if (index < 0) {
            return new int[0];
        }
        while (chords[index] == null) {
            returnChords[index] = 7;
            index--;
            if (index < 0) {
                return returnChords;
            }
        }
        returnChords[index] = chords[index].getBestChord();
        int previousChord = returnChords[index];
        index--;
        outerLoop: while (index > 0) {
            while (chords[index] == null) {
                returnChords[index] = 7;
                index--;
                if (index < 1) {
                    break outerLoop;
                }
            }
            int dominantIndex = (previousChord + 4) % scale.length;
            if (acceptableChange(chords[index].chords, dominantIndex,
                                 chords[index].getBestChord())) {
                returnChords[index] = dominantIndex;
            } else {
                returnChords[index] = chords[index].getBestChord();
            }
            previousChord = returnChords[index];
            index--;
        }
        if (chords[0] == null) {
            returnChords[0] = 7;
        } else {
            returnChords[0] = chords[0].getBestChord();
        }
        return returnChords;
    }

    private static boolean acceptableChange(final int[] chords,
                                            final int dominantIndex,
                                            final int previous) {
        for (int i = 0; i < chords.length; i++) {
            if (chords[i] == dominantIndex
                    && !(RATINGS[chords[i]] > 2 + RATINGS[previous])) {
                return true;
            }
        }
        return false;
    }

    private static Possible firstPass(final Note downBeat,
                                      final Note halfBeat,
                                      final int tonic, final int[] scale,
                                      final int[][] triads) {
        if (isBad(downBeat, tonic, scale)) {
            if (isBad(halfBeat, tonic, scale)) {
                return null;
            } else {
                return firstPassChords(halfBeat, tonic, scale, triads);
            }
        } else {
            if (isBad(halfBeat, tonic, scale)) {
                return firstPassChords(downBeat, tonic, scale, triads);
            } else {
                if (PhraseAnalysis.pitchToDegree(downBeat.getPitch(),
                                                 tonic)
                        == PhraseAnalysis.pitchToDegree(halfBeat.getPitch(),
                                                        tonic)) {
                    return firstPassChords(downBeat, tonic, scale, triads);
                } else {
                    return firstPassChords(downBeat, halfBeat, tonic, scale,
                                              triads);
                }
            }
        }
    }

    private static boolean isBad(final Note note, final int tonic,
                                 final int[] scale) {
        if (note  == null) {
            return true;
        }

        if (note.getPitch() == Note.REST) {
            return true;
        }

        if (PhraseAnalysis.isScale(note, tonic, scale)) {
            return false;
        }

        return true;
    }

    private static Possible firstPassChords(final Note note,
                                            final int tonic,
                                            final int[] scale,      
                                            final int[][] triads) {
        Possible returnChords = new Possible(new int[3]);
        int index = 0;
        int degree = PhraseAnalysis.pitchToDegree(note.getPitch(), tonic);
        for (int i = 0; i < triads.length; i++) {
            if (isInTriad(degree, triads[i])) {
                returnChords.chords[index++] = i;
            }
        }
        return returnChords;   
    }

    private static Possible firstPassChords(final Note note1,
                                            final Note note2,
                                            final int tonic,
                                            final int[] scale,
                                            final int[][] triads) {
        Possible firstChords =
                firstPassChords(note1, tonic, scale, triads);
        Possible secondChords =
                firstPassChords(note2, tonic, scale, triads);
        Possible commonChords =
                findCommonChords(firstChords.chords, secondChords.chords);
        return (commonChords == null) ? firstChords : commonChords;
    }

    private static boolean isInTriad(final int degree, final int[] triad) {
        for (int i = 0; i < triad.length; i++) {
            if (triad[i] == degree) {
                return true;
            }
        }
        return false;
    }

    private static Possible findCommonChords(final int[] firstChords,
                                                   final int[] secondChords) {
        Possible returnChords = new Possible(new int[2]);
        int index = 0;
        for (int i = 0; i < firstChords.length; i++) {
            for (int j = 0; j < secondChords.length; j++) {
                if (firstChords[i] == secondChords[j]) {
                    returnChords.chords[index++] = firstChords[i];
                }
            }
        }
        if (index == 0) {
            return null;
        } else if (index == 2) {
            return returnChords;
        } else if (index == 1) {
            int[] value = new int[1];
            value[0] = returnChords.chords[0];
            return new Possible(value);
        }
        throw new Error("Unexpected value for index");
    }
}
