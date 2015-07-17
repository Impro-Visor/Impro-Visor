/*
 * ClimaticPopInitialiser.java 0.1.2.0 8th February 2001
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

package jm.music.tools.ga;

import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

import jm.music.data.Phrase;
import jm.music.data.Note;
import jm.music.tools.PhraseAnalysis;
import jm.music.tools.NoteListException;

/**
 * @author  Adam Kirby
 * @version 0.1.2.0 8th February 2001
 */
public class ClimaticPopInitialiser extends PopulationInitialiser {
    protected static String label = "Climatic Population Initialiser";

    public final int TONIC = 60;

    public static final int MIN_POPULATION_SIZE = 2;

    public static final int MAX_POPULATION_SIZE = 100;

    public static final int DEFAULT_POPULATION_SIZE = 50;

    public static final double CLIMAX_AVERAGE = 0.523;

    public static final double CLIMAX_ST_DEV = 0.261;

    protected Panel panel;

    protected int populationSize;

    protected Label populationLabel;
    
    protected boolean modifyAll = false;

    public ClimaticPopInitialiser() {
        this(DEFAULT_POPULATION_SIZE);
    }

    public ClimaticPopInitialiser(int population) {
        populationSize = population;
        panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        populationLabel = new Label(Integer.toString(populationSize));
        panel.add(new Label("Population Size", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL, populationSize, 1,
                                MIN_POPULATION_SIZE, MAX_POPULATION_SIZE) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        populationSize = getValue();
                        populationLabel.setText(Integer.toString(getValue()));
                        populationLabel.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(populationLabel);
    }        
    
    public Phrase[] initPopulation(final Phrase phrase, final int beatsPerBar) {
        Phrase seed = completeFinalBeat(phrase, beatsPerBar);
        int size;
        if(modifyAll) {
            size = 0;
        } else size = seed.size();
        double[][] beatRhythmArray = generateBeatRhythmArray(seed, beatsPerBar);
        int[] intervalArray = generateIntervalArray(seed);


        Phrase[] population = new Phrase[populationSize];
        for (int i = 0; i < populationSize; i++) {
            population[i] = seed.copy();

            Note target;
            int targetBeat;
            int climax = 0;

    
            // Set target
            if (isClimaxAccepted(seed, beatsPerBar)) {
                climax = findClimax(seed);
                target = new Note(TONIC, (double) beatsPerBar);
                targetBeat = 7 * beatsPerBar;
            } else {
                int lowestPitch = Note.MAX_PITCH;
                for (int j = 0; j < seed.size(); j++) {
                    int currentPitch = seed.getNote(j).getPitch();
                    if (currentPitch != Note.REST && currentPitch < lowestPitch) {
                        lowestPitch = currentPitch;
                    }
                }
                target = generateClimax(lowestPitch);
                climax = target.getPitch();
                targetBeat = 4 * beatsPerBar;
            }


            /* Find the absolute minimum pitch which is the lower of an octave
             * below the starting note or a fifth below middle C (53),.
             */
            int lowerlimit = -1;
            for (int j = 0; j < seed.size(); j++) {
                int pitch = seed.getNote(j).getPitch();
                if (pitch != Note.REST) {
                    lowerlimit = pitch - 12;
                    break;
                }
            }
            if (lowerlimit < 53) {
                lowerlimit = 53;
            }


            // Extend to target
            extend(population[i], target, targetBeat, beatRhythmArray,
                   intervalArray, climax, beatsPerBar, lowerlimit);
            addAppropriateTarget(population[i], target);
//            population[i].addNote(target);


            // If the melody isn't complete, extend to final note
            if (population[i].getEndTime() != 8 * beatsPerBar) {
                target = new Note(TONIC, (double) beatsPerBar);
                targetBeat = 7 * beatsPerBar;
                extend(population[i], target, targetBeat,
                              beatRhythmArray, intervalArray, climax,
                              beatsPerBar, lowerlimit);

                /* Check last note */
                int noteIndex = population[i].size() - 1;
                int previousPitch = population[i].getNote(noteIndex).getPitch();
                while (previousPitch == Note.REST) {
                    previousPitch = population[i].getNote(--noteIndex).getPitch();
                }

                /* Find tonic closest to previous pitch */
                int targetPitch = target.getPitch();
                if (previousPitch < targetPitch) {
                    if (targetPitch - previousPitch > 6) {
                        target.setPitch(targetPitch - 12);
                    }
                } else if (previousPitch > targetPitch) {
                    if (previousPitch - targetPitch > 6) {
                        target.setPitch(targetPitch + 12);
                    }
                }
                population[i].addNote(target);
            }


            cleanMelody(population[i], size);


        }
        // confirm done
        //System.out.println("Created new population.");
        
        return population;
    }

    private Phrase completeFinalBeat(final Phrase phrase, int beatsPerBar) {
        Phrase returnPhrase = phrase.copy();

        double length = returnPhrase.getEndTime();

        // Because beats are integer multiples of a 1, a crotchet 
        double rhythmValueToCompleteBeat = Math.ceil(length) - length;

        // If the melody's length isn't a whole number of beats
        if (rhythmValueToCompleteBeat > 0) {

            int[] intervals =  generateIntervalArray(phrase);
            int counter = returnPhrase.size() - 1;
            int pitch = (int)Note.REST;
            while (pitch == Note.REST) {
                pitch = returnPhrase.getNote(counter--)
                                        .getPitch();
            }
            pitch += intervals[(int) (Math.random() * intervals.length)];
            if (!isScale(pitch)) {
                if (Math.random() < 0.5) {
                    ++pitch;
                } else {
                    --pitch;
                }
            }

            // Complete the beat
            returnPhrase.addNote(new Note(pitch,
                                          rhythmValueToCompleteBeat));
        }
        return returnPhrase;
    }        

    private double[][] generateBeatRhythmArray(Phrase phrase, int beatsPerBar) {
        double[][] tempBeatRVArray =
                new double[(int) phrase.getEndTime() * beatsPerBar][];
        int beatCount = 0;
        int absoluteNotesProcessed = 0;    
        double absoluteCumulativeRV = 0;
        Note note;

        while (absoluteNotesProcessed < phrase.size()) {
            int originalNotesProcessed = absoluteNotesProcessed;
            double originalCumulativeRV = absoluteCumulativeRV;

            int notesProcessed = absoluteNotesProcessed;
            double cumulativeRV = absoluteCumulativeRV;

            double[] tempRVArray = new double[phrase.size()];
            int count = 0;
            note = phrase.getNote(notesProcessed++);
            double rhythmValue = note.getRhythmValue();
            tempRVArray[count++] = rhythmValue;
            if (note.getPitch() == Note.REST) {
                tempRVArray[count - 1] *= -1;
            }


            cumulativeRV += rhythmValue;
            while (cumulativeRV != Math.ceil(cumulativeRV)) {
                note = phrase.getNote(notesProcessed++);
                rhythmValue = note.getRhythmValue();
                tempRVArray[count++] = rhythmValue;
                if (note.getPitch() == Note.REST) {
                    tempRVArray[count - 1] *= -1;
                }

                cumulativeRV += rhythmValue;
            }
            double[] RVArray = new double[count];
            System.arraycopy(tempRVArray, 0, RVArray, 0, count);
            tempBeatRVArray[beatCount++] = RVArray;

            absoluteNotesProcessed = notesProcessed;
            absoluteCumulativeRV = cumulativeRV;

            while (cumulativeRV < originalCumulativeRV + (double) beatsPerBar
                    && notesProcessed < phrase.size()) {
                note = phrase.getNote(notesProcessed++);
                rhythmValue = note.getRhythmValue();
                tempRVArray[count++] = rhythmValue;
                if (note.getPitch() == Note.REST) {
                    tempRVArray[count - 1] *= -1;
                }

                cumulativeRV += rhythmValue;
                while (cumulativeRV != Math.ceil(cumulativeRV)) {
                    note = phrase.getNote(notesProcessed++);
                    rhythmValue = note.getRhythmValue();
                    tempRVArray[count++] = rhythmValue;
                    if (note.getPitch() == Note.REST) {
                        tempRVArray[count - 1] *= -1;
                    }

                    cumulativeRV += rhythmValue;
                }
                if (cumulativeRV <= originalCumulativeRV + (double) beatsPerBar) {
                    RVArray = new double[count];
                    System.arraycopy(tempRVArray, 0, RVArray, 0, count);
                    tempBeatRVArray[beatCount++] = RVArray;
                }
            }
        }
        double[][] beatRVArray = new double[beatCount][];
        System.arraycopy(tempBeatRVArray, 0, beatRVArray, 0, beatCount);
        return beatRVArray;
    }            

    private int[] generateIntervalArray(Phrase phrase) {
        int[] intervals = new int[0];
        try {
            intervals = PhraseAnalysis.pitchIntervals(phrase);
        } catch (ArrayStoreException e) {
            System.exit(0);
        }

        // Add intervals in opposite direction, and remove rest indicator
        int[] tempIntervalArray = new int[intervals.length * 2];
        System.arraycopy(intervals, 0, tempIntervalArray, 0, intervals.length);
        for (int i = 0; i < intervals.length; i++) {
            if (tempIntervalArray[i] > Note.MAX_PITCH - Note.MIN_PITCH) {
                tempIntervalArray[i] -= PhraseAnalysis.INTERVAL_WITH_REST;
            }
            tempIntervalArray[intervals.length + i] = 0 - tempIntervalArray[i];
        }

        return tempIntervalArray;
    }

    private boolean isClimaxAccepted(Phrase phrase, int beatsPerBar) {
        int lastHighestPitch = 0;
        int repetitions = 0;
        double cumulativeRV = 0;
        double location = 0;
        for (int i = 0; i < phrase.size(); i++) {
            int pitch = phrase.getNote(i).getPitch();
            if (pitch != Note.REST) {
                if (pitch > lastHighestPitch) {
                    lastHighestPitch = pitch;
                    location = cumulativeRV;
                    repetitions = 0;
                } else if (pitch == lastHighestPitch) {
                    repetitions++;
                    location = cumulativeRV;
                }
            }
            cumulativeRV += phrase.getNote(i).getRhythmValue();
        }
        if (location < 8 * beatsPerBar  * (CLIMAX_AVERAGE - CLIMAX_ST_DEV)
                || location > 8 * beatsPerBar * (CLIMAX_AVERAGE + CLIMAX_ST_DEV)) {
            return false;
        }
        if (lastHighestPitch > phrase.getNote(0).getPitch() + 12
                && repetitions <= 1) {
            return true;
        }
        return false;
    }

    private int findClimax(Phrase phrase) {
        int lastHighestPitch = 0;
        for (int i = 0; i < phrase.size(); i++) {
            int pitch = phrase.getNote(i).getPitch();
            if (pitch != Note.REST) {
                if (pitch > lastHighestPitch) {
                    lastHighestPitch = pitch;
                }
            }
        }
        return lastHighestPitch;
    }

    private Note generateClimax(int lowestPitch) {
        int pitch = 0;
        int currentPitch = lowestPitch + 13;
        while (pitch == 0) {
            if (currentPitch % 12 == TONIC % 12
                    || currentPitch % 12 == (TONIC + 7) % 12) {
                pitch = currentPitch;
            }
            currentPitch++;
        }
        while (pitch > 88) {
            if (currentPitch % 12 == TONIC % 12
                    || currentPitch % 12 == (TONIC + 7) % 12) {
                pitch = currentPitch;
            }
            currentPitch--;
        }

        return new Note(pitch, 1.0);
    }

    private void extend(Phrase phrase, Note target, int targetBeat, 
                        double[][] beatArray, int[] intervalArray, int climax,
                        int beatsPerBar, final int lowerlimit) {
        int length = (int) phrase.getEndTime();
        while (length < targetBeat) {
            if (length == 2 * beatsPerBar) {
                int noteIndex = phrase.size() - 1;
                int previousPitch = phrase.getNote(noteIndex).getPitch();
                while (previousPitch == Note.REST) {
                    previousPitch = phrase.getNote(--noteIndex).getPitch();
                }
                /* if previousPitch is not harmonious (a tonic or dominant) */
                if (! (previousPitch % 12 == TONIC % 12
                        || previousPitch % 12 == (TONIC + 7) % 12)) { 
                    int nextHarmoniousNote = previousPitch + 1;
                    while (! (nextHarmoniousNote % 12 == TONIC % 12
                            || nextHarmoniousNote % 12 == (TONIC + 7) % 12)) {
                        nextHarmoniousNote++;
                    }
                    int prevHarmoniousNote = previousPitch - 1;
                    while (! (prevHarmoniousNote % 12 == TONIC % 12
                            || prevHarmoniousNote % 12 == (TONIC + 7) % 12)) {
                        prevHarmoniousNote--;
                    }
                    if (nextHarmoniousNote > climax) {
                        previousPitch = prevHarmoniousNote;
                    } else if (prevHarmoniousNote < lowerlimit) {
                        previousPitch = nextHarmoniousNote;
                    } else if (nextHarmoniousNote - previousPitch
                                > previousPitch - prevHarmoniousNote) {
                        previousPitch = prevHarmoniousNote;
                    } else if (previousPitch - prevHarmoniousNote
                                > nextHarmoniousNote - previousPitch) {
                        previousPitch = nextHarmoniousNote;
                    } else {
                        previousPitch = nextHarmoniousNote;
                    }
                }
                phrase.addNote(new Note(previousPitch, 2.0));
//                for (int i = 0; i < beatsPerBar - 2; i++) {                     
//                    addNote(phrase, target, targetBeat, 1.0, intervalArray, climax,
//                            lowerlimit);
//                }
            } else {
                int beatsInArray = targetBeat;
                int beatIndex = 0;
    
                int counter = 0;
    
                // Don't add group of beats that will not cross the next bar line
                while (counter < 30 && length + beatsInArray > ((int) (length
                                                                      / beatsPerBar)
                                                                + 1)
                                                               * beatsPerBar) {
                    beatIndex = (int) (Math.random() * beatArray.length);
                    double tempRVCount = 0;
                    for (int i = 0; i < beatArray[beatIndex].length; i++) {
                        
                        tempRVCount += (beatArray[beatIndex][i] < 0)
                                       ? 0 - beatArray[beatIndex][i]
                                       : beatArray[beatIndex][i];
                    }
    
                    beatsInArray = (int) tempRVCount;
                    counter++;
    
                }
    
                if (counter != 30) {
                    // Add beats to melody
                    for (int i = 0; i < beatArray[beatIndex].length; i++) {
                        addNote(phrase, target, targetBeat, beatArray[beatIndex][i],
                                intervalArray, climax, lowerlimit);
                    }
                } else {
                    addNote(phrase, target, targetBeat,
                            (((int) (length / beatsPerBar) + 1) * beatsPerBar) - length,
                            intervalArray, climax, lowerlimit);
                }
            }
            length = (int) phrase.getEndTime();


        }
//        phrase.addNote(target);

    }

    private void addAppropriateTarget(final Phrase phrase, final Note target) {
        Note targetToAdd = target.copy();
        int lastIndex = phrase.size();
        int lastPitch;
        do {
            lastPitch = phrase.getNote(--lastIndex).getPitch();
        } while (lastPitch == Note.REST);
        int targetPitch = target.getPitch();
        if (lastPitch + 7 < targetPitch) {
            do {
                targetPitch -= 12;
            } while (targetPitch - 12 > lastPitch);
            targetToAdd.setPitch(targetPitch);
        }
        phrase.addNote(targetToAdd);
    }

    private void addNote(Phrase phrase, Note target, int targetBeat,
                         double rhythmValue, int[] intervalArray, int climax,
                         final int lowerlimit) {
        if (rhythmValue < 0.0) {
            phrase.addNote(new Note(Note.REST, 0.0 - rhythmValue));
        } else {
            // select previous non rest
            int noteIndex = phrase.size() - 1;
            int previousPitch = phrase.getNote(noteIndex).getPitch();
            while (previousPitch == Note.REST) {
                previousPitch = phrase.getNote(--noteIndex).getPitch();
            }
            
            double originalRatio = (target.getPitch() - previousPitch)
                                   / (targetBeat - phrase.getEndTime());
            int selectedInterval = intervalArray[
                    (int) (Math.random() * intervalArray.length)];
            int currentInterval = target.getPitch() - previousPitch;
            double intervalRatio = selectedInterval / (double) currentInterval;
            if (intervalRatio < 0) {
                if (Math.random() < (2.5 / (targetBeat - phrase.getEndTime()))) {
                    selectedInterval = 0 - selectedInterval;
                }
            }
            double currentRatio = currentInterval
                                  / (targetBeat - phrase.getEndTime());
            double ratioOfRatios = currentRatio / originalRatio;
            if (ratioOfRatios >= 2.0 || ratioOfRatios <= 0.5) {
                selectedInterval /= 2;
            }

            int pitch = previousPitch + selectedInterval;
            if (pitch >= climax || pitch < lowerlimit) {
                pitch = previousPitch - selectedInterval;
            }
            if (pitch >= climax || pitch < lowerlimit) {
                pitch = previousPitch - selectedInterval / 2;
            }
            if (pitch >= climax || pitch < lowerlimit) {
                pitch = previousPitch - selectedInterval / 4;
            }

            phrase.addNote(new Note(pitch, rhythmValue));
        }
    }        

    private void cleanMelody(Phrase phrase, int size) {
        for (int i = size; i < phrase.size(); i++) {
            int pitch = phrase.getNote(i).getPitch();
            if (pitch != Note.REST) {
                if (!isScale(pitch)) {
                    if (Math.random() < 0.5) {
                        phrase.getNote(i).setPitch(pitch + 1);
                    } else {
                        phrase.getNote(i).setPitch(pitch - 1);
                    }
                }
            }
        }
    }

    private boolean isScale(int pitch) {
        for (int j = 0; j < PhraseAnalysis.MAJOR_SCALE.length; j++) {
            if (pitch % 12 == PhraseAnalysis.MAJOR_SCALE[j]) {
                return true;
            }
        }
        return false;
    }

    public Panel getPanel() {
        return panel;
    }

    public String getLabel() {
        return label;
    }
    
    public void setModifyAll(boolean val) {
        this.modifyAll = val;
    }
}
