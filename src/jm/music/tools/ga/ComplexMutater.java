/*
 * ComplexMutater.java 0.1.2.0 8th February 2001
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

import java.awt.Choice;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Label;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.util.Vector;

import jm.music.data.Phrase;
import jm.music.data.Note;
import jm.music.tools.PhraseAnalysis;
import jm.music.tools.NoteListException;

/**
 * @author    Adam Kirby
 * @version   0.1.2.0, 8th February 2001
 */
public class ComplexMutater extends Mutater{
    private int[] MUTATE_PERCENTAGE = {0,40,1,40,60}; //{1, 50, 5, 10, 40}; // {1, 50, 0, 0, 40};

    private static final int SEMITONES_PER_OCTAVE = 12;

    private static final int TONIC = 60;

    protected static String label = "Mutater";

    protected Panel panel;
                                         
    protected Choice choice;

    protected Scrollbar scrollbar;

    protected Label mutateLabel;
    
    protected boolean modifyAll = false;

    public ComplexMutater() {
        panel = new Panel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setLayout(gbl);
        mutateLabel = new Label(Integer.toString(MUTATE_PERCENTAGE[0]));
        scrollbar = new Scrollbar(Scrollbar.HORIZONTAL,
                                  MUTATE_PERCENTAGE[0], 1, 0, 100);
        choice = new Choice();
        choice.add("Random pitch change");
        choice.add("Bar sequence mutations");
        choice.add("Split and merge");
        choice.add("Step interpolation");
        choice.add("Tonal Pauses");
        choice.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent evt) {
                    mutateLabel.setText(Integer.toString(
                            MUTATE_PERCENTAGE[choice.getSelectedIndex()]));
                    scrollbar.setValue(MUTATE_PERCENTAGE[choice.getSelectedIndex()]);
                }
        }
        );
        scrollbar.addAdjustmentListener(new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent evt) {
                    MUTATE_PERCENTAGE[choice.getSelectedIndex()] =
                            scrollbar.getValue();
                    mutateLabel.setText(Integer.toString(scrollbar.getValue()));
                    mutateLabel.repaint();
                }
        }
        );
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 2;
        gbl.setConstraints(choice, gbc);
        panel.add(choice);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(scrollbar, gbc);
        panel.add(scrollbar);

        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbl.setConstraints(mutateLabel, gbc);
        panel.add(mutateLabel);
    }

    public Phrase[] mutate(Phrase[] population, double initialLength,
                           int initialSize, int beatsPerBar) {
        double[] mutationArray = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            mutationArray[i] = population[i].getEndTime();
        }
        for (int i = 0; i < population.length; i++) {
            Phrase individual = population[i];
            
            //Change the seed portion of the extension
            if(modifyAll) {
                initialSize = 0;
                initialLength = 0.0;
            }
            
            // 1. Random Pitch Change

            int n = individual.size() - initialSize;
            double n2change1 = n * MUTATE_PERCENTAGE[0] / 100.0;
            int n2change = 0;
            if (n2change1 < 1.0) {
                if (Math.random() < n2change1) {
                    n2change = 1;
                } else {
                    n2change = 0;
                }
            } else {
                n2change = (int) Math.floor(n2change1);
            }
            for (int j = 0; j < n2change; j++) {
                int r = (int) (Math.random() * n);
                mutate(individual.getNote(initialSize + r));
            }

            // 2. Bar sequence mutations

            if (Math.random() < MUTATE_PERCENTAGE[1] / 100.0) { 
                int previousNoteOnBar = 0;
                double beatCount = 0;
                for (int j = 0; j < individual.size(); j++) {
                    beatCount += individual.getNote(j).getRhythmValue();
                }
                int[] notesOnBars = new int[(int) beatCount];
                int[] barNumber = new int[(int) beatCount];
                int index = 0;
                beatCount = 0;
                for (int j = 0; j < individual.size(); j++) {
                    if (beatCount / (double) beatsPerBar
                            == Math.floor(beatCount / (double) beatsPerBar)) {
                        notesOnBars[index] = j;
                        barNumber[index++] = (int) (beatCount * beatsPerBar);
                    }
                    beatCount += individual.getNote(j).getRhythmValue();
                }
                int countOfEncapsulatedBars = 0;
                int[] notesBeginningEncapsulatedBars = new int[index];
                if (index > 0) {
                    for (int j = 1; j < index; j++) {
                        if (barNumber[j] == barNumber[j - 1] + 1) {
                            notesBeginningEncapsulatedBars[
                                    countOfEncapsulatedBars++] = notesOnBars[j - 1];
                        }
                    }
                }
                if (countOfEncapsulatedBars > 0) {
                    int r2 = 0;
                    while (r2 < (int) (initialLength / beatsPerBar) - 1) {
                        r2 = (int) (Math.random() * countOfEncapsulatedBars);
                    }
                    int r3 = (int) (Math.random() * 2 + 1);
                    switch(r3) {
                    case 1:
                        int transpose = 0;
                        if (Math.random() < 0.5) {
                            transpose = 2;
                        } else {
                            transpose = -2;
                        }
                        beatCount = 0;
                        index = notesBeginningEncapsulatedBars[r2];
                        while (beatCount < beatsPerBar) {
                            shiftPitch(individual.getNote(index), transpose);
                            beatCount += individual.getNote(index++).getRhythmValue();
                        }
                        break;
                    case 2:
                    default:
                        index = notesBeginningEncapsulatedBars[r2];
                        beatCount = 0;
                        while (beatCount < beatsPerBar) {
                            beatCount += individual.getNote(index++).getRhythmValue();
                        }
                        int notesInBar = index - notesBeginningEncapsulatedBars[r2];
                        index = notesBeginningEncapsulatedBars[r2];
                        if (notesInBar > 0) {
                            int[] tempPitches = new int[notesInBar];
                            double[] tempRhythmValues = new double[notesInBar];
                            for (int j = 0; j < notesInBar; j++) {
                                tempPitches[j] = individual.getNote(j + index).getPitch();
                                tempRhythmValues[j] = individual.getNote(j + index).getRhythmValue();
                            }
                            for (int j = 0; j < notesInBar; j++) {
                                individual.getNote(j + index).setPitch(tempPitches[notesInBar - j - 1]);
                                individual.getNote(j + index).setRhythmValue(tempRhythmValues[notesInBar - j - 1]);
                            }
                        }
                    }
                }
            }

            // 3. Split and Merge
            
            int n1 = individual.size() - initialSize;
            double n2change2 = n1 * MUTATE_PERCENTAGE[2] / 100.0;
            int n2change3 = 0;
            if (n2change2 < 1.0) {
                if (Math.random() < n2change2) {
                    n2change3 = 1;
                } else {
                    n2change3 = 0;
                }
            } else {
                n2change3 = (int) Math.floor(n2change2);
            }
            Vector vector = (Vector) individual.getNoteList().clone();
            for (int j = 0; j < n2change3; j++) {
                int r1 = (int) (Math.random() * (n1 - 1));
                Note note5 = (Note) vector.elementAt(initialSize + r1);
                int pitch5 = note5.getPitch();
                double rhythmValue5 = note5.getRhythmValue();
                if (rhythmValue5 >= 1.0 && rhythmValue5%1.0 == 0 &&
                        rhythmValue5 * 2.0 == Math.ceil(rhythmValue5 * 2.0)) {
                    vector.removeElementAt(initialSize + r1);
                    vector.insertElementAt(new Note(pitch5,
                                                    rhythmValue5 / 2.0),
                                           initialSize + r1);
                    vector.insertElementAt(new Note(pitch5,
                                                    rhythmValue5 / 2.0),
                                           initialSize + r1);
                    n1++;
                } else {
                    double rhythmValue6 = rhythmValue5 + ((Note)
                            vector.elementAt(initialSize + r1 + 1))
                                  .getRhythmValue();
                    if (rhythmValue6 <= 2.0) {
                        vector.removeElementAt(initialSize + r1);
                        vector.removeElementAt(initialSize + r1);
                        vector.insertElementAt(new Note(pitch5,
                                                        rhythmValue6),
                                               initialSize + r1);
                        n1--;
                    }
                }
            }
            individual.addNoteList(vector, false);
            
            
            // 4. Step interpolation
            vector = (Vector) individual.getNoteList().clone();
            int currentPitch;
            double currentRV;
            int previousPitch = (int)Note.REST;
            double previousRV = 0;
            int index1 = initialSize;
            while (index1 < vector.size() && previousPitch == Note.REST) {
                previousPitch = ((Note) vector.elementAt(index1)).getPitch();
                previousRV = ((Note) vector.elementAt(index1)).getRhythmValue();
                index1++;
            }
            int k = index1;
            while (k < vector.size()) {
                currentPitch = ((Note) vector.elementAt(k)).getPitch();
                currentRV = ((Note) vector.elementAt(k)).getRhythmValue();
                if (currentPitch != Note.REST) {
                    int interval = currentPitch - previousPitch;
                    if ((Math.abs(interval) == 4 || Math.abs(interval) == 3)
                            && Math.random() < (MUTATE_PERCENTAGE[3] / 100.0)) {
                        int scalePitch = 0;
                        if (interval > 0) {
                            scalePitch = currentPitch - 1;
                            if (!isScale(scalePitch)) {
                                scalePitch--;
                            }
                        } else {
                            scalePitch = currentPitch + 1;
                            if (!isScale(scalePitch)) {
                                scalePitch++;
                            }
                        }
                        if (currentRV > previousRV) {
                            if (currentRV >= 0.5
                                    && (int) Math.ceil(currentRV * 2)
                                       == (int) (currentRV * 2)) {
                                vector.removeElementAt(k);
                                vector.insertElementAt(new Note(currentPitch,
                                        currentRV / 2.0), k);
                                vector.insertElementAt(new Note(scalePitch,
                                        currentRV / 2.0), k);
                                k++;
                            }
                        } else {
                            if (previousRV >= 0.5
                                    && (int) Math.ceil(previousRV * 2)
                                       == (int) (previousRV * 2)) {
                                vector.removeElementAt(k - 1);
                                vector.insertElementAt(new Note(scalePitch,
                                        previousRV / 2.0), k - 1);
                                vector.insertElementAt(new Note(previousPitch,
                                        previousRV / 2.0), k - 1);
                                k++;
                            }
                        }
                    }
                    previousPitch = currentPitch;
                    previousRV = currentRV;
                }
                k++;
            }

            individual.addNoteList(vector, false);

            // 5. Tonal Pauses (make well positioned primary pitches longer
                // by adding the value of two notes together)

            individual.addNoteList(applyTonalPausesMutation(individual,
                                                            initialLength,
                                                            initialSize,
                                                            beatsPerBar),
                                   false);

            // 6. Pitch Clean Up

            double cumulativeRV = 0;
            for (int j = initialSize; j < individual.size(); j++) {
                int pitch = individual.getNote(j).getPitch();
                double rv = individual.getNote(j).getRhythmValue();
                if (pitch != Note.REST) {
                    if (!isScale(pitch)) {
                        if ((int) Math.ceil(cumulativeRV / 2.0)
                                == (int) (cumulativeRV / 2.0)) {
                            if (Math.random() < rv) {
                                if (Math.random() < 0.5) {
                                    individual.getNote(j).setPitch(pitch + 1);
                                } else {
                                    individual.getNote(j).setPitch(pitch - 1);
                                }
                            }
                        } else {
                            if (Math.random() < (rv / 2.0)) {
                                if (Math.random() < 0.5) {
                                    individual.getNote(j).setPitch(pitch + 1);
                                } else {
                                    individual.getNote(j).setPitch(pitch - 1);
                                }
                            }
                        }    
                    }
                }
                cumulativeRV += rv;
            }
        }
        return population;
    }

    // Apply a small pitch shift to the note
    private void mutate(Note note) {
        int pitchShift = (int) (10 / (Math.random() * 6 + 2));
        if (Math.random() < 0.5) {
            shiftPitch(note, pitchShift);
        } else {
            shiftPitch(note, 0 - pitchShift);
        }
    }

    private Vector applyTonalPausesMutation(final Phrase phrase,
                                            double initialLength,
                                            int initialSize, int beatsPerBar) {
        Vector vector = (Vector) phrase.getNoteList().clone();
        double rhythmValueCount = initialLength;
        int count = 0;
        for (int j = initialSize; j < phrase.size() - 1; j++) {
            int pitch = phrase.getNote(j).getPitch();
            int degree = pitchToDegree(pitch, TONIC);
            double rhythmValue = phrase.getNote(j).getRhythmValue()
                    + phrase.getNote(j + 1).getRhythmValue();
            if (rhythmValueCount / (double) beatsPerBar
                    == Math.ceil(rhythmValueCount / (double) beatsPerBar)
                    && (degree == 0 || degree == 7)
                    && Math.random() < (2.0 / rhythmValue)
                                       * (MUTATE_PERCENTAGE[4] / 100.0)) {
                vector.removeElementAt(j - count);
                vector.removeElementAt(j - count);
                vector.insertElementAt(new Note(pitch, rhythmValue),
                                       j - count);
                rhythmValueCount += phrase.getNote(j).getRhythmValue();
                j++;
                count++;
            }
            rhythmValueCount += phrase.getNote(j).getRhythmValue();
        }
//        System.exit(-1);
        return vector;
    }

    private void shiftPitch(Note note, int shift) {
        note.setPitch(note.getPitch() + shift);
    }

    private boolean isScale(int pitch) {
        for (int j = 0; j < PhraseAnalysis.MAJOR_SCALE.length; j++) {
            if (pitch % 12 == PhraseAnalysis.MAJOR_SCALE[j]) {
                return true;
            }
        }
        return false;
    }

    private static int pitchToDegree(int pitch, final int tonic) {
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

    /**
    * This class incoproates a visual editor to change the amount of each mutation,
    * the returned awt panel can be put into a visible componenet to allow GUI editing 
    * of the mutation amounts.
    */
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
