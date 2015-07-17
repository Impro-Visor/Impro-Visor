/*
 * TestFitnessEvaluater.java 0.1.2.0 8th February 2001
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
import java.awt.Label;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

import jm.music.data.Phrase;
import jm.music.tools.PhraseAnalysis;
import jm.music.tools.NoteListException;
import jm.music.tools.QuantisationException;

/**
 * @author    Adam Kirby
 * @version   0.1.2.0, 8th February 2001
 */
public class NormalDistributionFE extends FitnessEvaluater {
    protected static String label = "Normal Distribution Fitness Evaluater";

    protected Panel panel;

    private double[] weighting = {1.0,  // Note Density
                                  1.0,  // Pitch Variety
                                  1.0,  // Rhythmic Variety
                                  0.1,  // Climax Strength
                                  1.0,  // Rest Density
                                  1.0,  // Tonal Deviation
                                  0.5,  // Key Centeredness
                                  0.5,  // Pitch Range
                                  0.5,  // Rhythm Range
                                  1.0,  // Repeated Pitch Density
                                  0.5,  // Repeated Rhythm Density
                                  0.5,  // Melodic Direction Stability
                                  1.0,  // Overall Pitch Direction
                                  0.5,  // Pitch Movement by step
                                  1.0,  // Dissonance
                                  0.1,  // Leap Compensation
                                  1.0,  // Syncopation
                                  1.0,  // Repeated Pitch Patterns of 3
                                  1.0,  // Repeated Pitch Patterns of 4
                                  0.5,  // Repeated Rhythm Patterns of 3
                                  0.1,  // Repeated Rhythm Patterns of 4
                                  0.1,  // Climax Position
                                  0.1}; // Climax Tonality

    protected Label F1Label;

    protected Label F2Label;

    protected Label F3Label;

    protected Label F4Label;

    protected Label F5Label;

    protected Label F6Label;

    protected Label F7Label;

    protected Label F8Label;

    protected Label F9Label;

    protected Label F10Label;

    protected Label F11Label;

    protected Label F12Label;

    protected Label F13Label;

    protected Label F14Label;

    protected Label F15Label;

    protected Label F16Label;

    protected Label F17Label;

    protected Label F18Label;

    protected Label F19Label;

    protected Label F20Label;

    protected Label F21Label;

    protected Label F22Label;

    protected Label F23Label;

    public NormalDistributionFE() {
        panel = new Panel();
        panel.setLayout(new GridLayout(23, 3));
        F1Label = new Label(Integer.toString(
                (int) (weighting[0] * 100)));
        panel.add(new Label("Note Density", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[0] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[0] = getValue() / 100.0;
                        F1Label.setText(Integer.toString(getValue()));
                        F1Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F1Label);

        F2Label = new Label(Integer.toString(
                (int) (weighting[1] * 100)));
        panel.add(new Label("Pitch Variety", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[1] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[1] = getValue() / 100.0;
                        F2Label.setText(Integer.toString(getValue()));
                        F2Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F2Label);

        F3Label = new Label(Integer.toString(
                (int) (weighting[1] * 100)));
        panel.add(new Label("Rhythmic Variety", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[2] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[2] = getValue() / 100.0;
                        F3Label.setText(Integer.toString(getValue()));
                        F3Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F3Label);

        F4Label = new Label(Integer.toString(
                (int) (weighting[3] * 100)));
        panel.add(new Label("Climax Strength", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[3] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[3] = getValue() / 100.0;
                        F4Label.setText(Integer.toString(getValue()));
                        F4Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F4Label);

        F5Label = new Label(Integer.toString(
                (int) (weighting[4] * 100)));
        panel.add(new Label("Rest Density", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[4] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[4] = getValue() / 100.0;
                        F5Label.setText(Integer.toString(getValue()));
                        F5Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F5Label);

        F6Label = new Label(Integer.toString(
                (int) (weighting[5] * 100)));
        panel.add(new Label("Tonal Deviation", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[5] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[5] = getValue() / 100.0;
                        F6Label.setText(Integer.toString(getValue()));
                        F6Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F6Label);

        F7Label = new Label(Integer.toString(
                (int) (weighting[6] * 100)));
        panel.add(new Label("Key Centeredness", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[6] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[6] = getValue() / 100.0;
                        F7Label.setText(Integer.toString(getValue()));
                        F7Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F7Label);

        F8Label = new Label(Integer.toString(
                (int) (weighting[7] * 100)));
        panel.add(new Label("Pitch Range", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[7] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[7] = getValue() / 100.0;
                        F8Label.setText(Integer.toString(getValue()));
                        F8Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F8Label);

        F9Label = new Label(Integer.toString(
                (int) (weighting[8] * 100)));
        panel.add(new Label("Rhythm Range", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[8] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[8] = getValue() / 100.0;
                        F9Label.setText(Integer.toString(getValue()));
                        F9Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F9Label);

        F10Label = new Label(Integer.toString(
                (int) (weighting[9] * 100)));
        panel.add(new Label("Repeated Pitch Density", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[9] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[9] = getValue() / 100.0;
                        F10Label.setText(Integer.toString(getValue()));
                        F10Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F10Label);

        F11Label = new Label(Integer.toString(
                (int) (weighting[10] * 100)));
        panel.add(new Label("Repeated Rhythm Density", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[10] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[10] = getValue() / 100.0;
                        F11Label.setText(Integer.toString(getValue()));
                        F11Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F11Label);

        F12Label = new Label(Integer.toString(
                (int) (weighting[11] * 100)));
        panel.add(new Label("Melodic Direction Stability", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[11] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[11] = getValue() / 100.0;
                        F12Label.setText(Integer.toString(getValue()));
                        F12Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F12Label);

        F13Label = new Label(Integer.toString(
                (int) (weighting[12] * 100)));
        panel.add(new Label("Overall Pitch Direction", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[12] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[12] = getValue() / 100.0;
                        F13Label.setText(Integer.toString(getValue()));
                        F13Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F13Label);

        F14Label = new Label(Integer.toString(
                (int) (weighting[13] * 100)));
        panel.add(new Label("Pitch Movement", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[13] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[13] = getValue() / 100.0;
                        F14Label.setText(Integer.toString(getValue()));
                        F14Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F14Label);

        F15Label = new Label(Integer.toString(
                (int) (weighting[14] * 100)));
        panel.add(new Label("Dissonance", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[14] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[14] = getValue() / 100.0;
                        F15Label.setText(Integer.toString(getValue()));
                        F15Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F15Label);

        F16Label = new Label(Integer.toString(
                (int) (weighting[15] * 100)));
        panel.add(new Label("Leap Compensation", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[15] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[15] = getValue() / 100.0;
                        F16Label.setText(Integer.toString(getValue()));
                        F16Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F16Label);

        F17Label = new Label(Integer.toString(
                (int) (weighting[16] * 100)));
        panel.add(new Label("Syncopation", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[16] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[16] = getValue() / 100.0;
                        F17Label.setText(Integer.toString(getValue()));
                        F17Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F17Label);

        F18Label = new Label(Integer.toString(
                (int) (weighting[17] * 100)));
        panel.add(new Label("Repeated Pitch Patterns of 3", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[17] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[17] = getValue() / 100.0;
                        F18Label.setText(Integer.toString(getValue()));
                        F18Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F18Label);

        F19Label = new Label(Integer.toString(
                (int) (weighting[18] * 100)));
        panel.add(new Label("Repeated Pitch Patterns of 4", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[18] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[18] = getValue() / 100.0;
                        F19Label.setText(Integer.toString(getValue()));
                        F19Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F19Label);

        F20Label = new Label(Integer.toString(
                (int) (weighting[19] * 100)));
        panel.add(new Label("Repeated Rhythm Patterns of 3", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[19] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[19] = getValue() / 100.0;
                        F20Label.setText(Integer.toString(getValue()));
                        F20Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F20Label);

        F21Label = new Label(Integer.toString(
                (int) (weighting[20] * 100)));
        panel.add(new Label("Repeated Rhythm Patterns of 4", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[20] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[20] = getValue() / 100.0;
                        F21Label.setText(Integer.toString(getValue()));
                        F21Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F21Label);

        F22Label = new Label(Integer.toString(
                (int) (weighting[21] * 100)));
        panel.add(new Label("Climax Position", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[21] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[21] = getValue() / 100.0;
                        F22Label.setText(Integer.toString(getValue()));
                        F22Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F22Label);

        F23Label = new Label(Integer.toString(
                (int) (weighting[22] * 100)));
        panel.add(new Label("Climax Tonality", Label.RIGHT));
        panel.add(new Scrollbar(Scrollbar.HORIZONTAL,
                                 (int) (weighting[22] * 100), 1, 0, 100) {
            {
                addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent evt) {
                        weighting[22] = getValue() / 100.0;
                        F23Label.setText(Integer.toString(getValue()));
                        F23Label.repaint();
                    }
                }
                );
            }
        }
        );
        panel.add(F23Label);

    }

    private double[] mean = {0.307,  // Note Density
                              0.308,  // Pitch Variety
                              0.021,  // Rhythmic Variety
                              0.669,  // Climax Strength
                              0.021,   // Rest Density
                              0.079,  // Tonal Deviation
                              0.652,  // Key Centeredness
                              0.545,  // Pitch Range
                              0.383,  // Rhythm Range
                              0.130,  // Repeated Pitch Density
                              0.562,  // Repeated Rhythm Density
                              0.411,  // Melodic Direction Stability
                              0.495,  // Overall Pitch Direction
                              0.601,  // Pitch Movement
                              0.013,  // Dissonance
                              0.252,  // Leap Compensation
                              0.066,  // Syncopation
                              0.183,  // Repeated Pitch Patterns of 3
                              0.112,  // Repeated Pitch Patterns of 4
                              0.538,  // Repeated Rhythm Patterns of 3
                              0.439,  // Repeated Rhythm Patterns of 4
                              0.523,  // Climax Position
                              0.346};  // Climax Tonality

    private double[] standardDeviation = {0.115, // Note Density
                                          0.129, // Pitch Variety
                                          0.038, // Rhythmic Variety
                                          0.318, // Climax Strength
                                          0.044,  // Rest Density
                                          0.137, // Tonal Deviation
                                          0.148, // Key Centeredness
                                          0.166, // Pitch Range
                                          0.211, // Rhythm Range
                                          0.130, // Repeated Pitch Density
                                          0.210, // Repeated Rhythm Density
                                          0.139, // Melodic Direction Stability
                                          0.059, // Overall Pitch Direction
                                          0.218, // Pitch Movement
                                          0.047, // Dissonance
                                          0.399, // Leap Compensation
                                          0.105, // Syncopation
                                          0.146, // Repeated Pitch Patterns of 3
                                          0.125, // Repeated Pitch Patterns of 4
                                          0.227, // Repeated Rhythm Patterns of 3
                                          0.246, // Repeated Rhythm Patterns of 4
                                          0.261, // Climax Position
                                          0.275}; // Climax Tonality

    public static final double duration = 0.25;

    public static final int tonic = 60;

    public static final int[] scale = PhraseAnalysis.MAJOR_SCALE;

    public double[] evaluate(Phrase[] population) {
        double count;
        double[] fitness = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            count = 0.0;
            for (int j = 0; j < mean.length; j++) {
                count += calculateFitness(getValue2(j, population[i]),
                                          mean[j], standardDeviation[j],
                                          weighting[j]);
            }
            fitness[i] = 1 / (count / (weighting.length - 1) + 1);
        }
        return fitness;
    }

    private double calculateFitness(double value, double mean,
                                    double standardDeviation,
                                    double weighting) {
        return Math.abs((value - mean) / standardDeviation) * weighting;
    }

    private double getValue2(int index, Phrase phrase) {
        try {
             switch (index) {
            default:
            case 0:
                return PhraseAnalysis.noteDensity(phrase, duration);
            case 1:
                return PhraseAnalysis.pitchVariety(phrase);
            case 2:
                return PhraseAnalysis.rhythmicVariety(phrase);
            case 3:
                return PhraseAnalysis.climaxStrength(phrase);
            case 4:
                return PhraseAnalysis.restDensity(phrase, duration);
            case 5:
                return PhraseAnalysis.tonalDeviation(phrase, duration, tonic,
                                                     scale);
            case 6:
                return PhraseAnalysis.keyCenteredness(phrase, duration, tonic);
            case 7:
                return PhraseAnalysis.pitchRangePerSpan(phrase);
            case 8:
                return PhraseAnalysis.rhythmRangePerSpan(phrase);
            case 9:
                return PhraseAnalysis.repeatedPitchDensity(phrase);
            case 10:
                return PhraseAnalysis.repeatedRhythmicValueDensity(phrase);
            case 11:
                return PhraseAnalysis.melodicDirectionStability(phrase);
            case 12:
                return PhraseAnalysis.overallPitchDirection(phrase);
            case 13:
                return PhraseAnalysis.movementByStep(phrase, tonic, scale);
            case 14:
                return PhraseAnalysis.dissonance(phrase);
            case 15:
                return PhraseAnalysis.leapCompensation(phrase);
            case 16:
                return PhraseAnalysis.syncopation(phrase);
            case 17:
                 return PhraseAnalysis.repeatedPitchPatterns(phrase, 3);
            case 18:
                return PhraseAnalysis.repeatedPitchPatterns(phrase, 4);
            case 19:
                return PhraseAnalysis.repeatedRhythmPatterns(phrase, 3);
            case 20:
                return PhraseAnalysis.repeatedRhythmPatterns(phrase, 4);
            case 21:
                return PhraseAnalysis.climaxPosition(phrase);
            case 22:
                return PhraseAnalysis.climaxTonality(phrase, tonic, scale);
            }
        } catch (NoteListException e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(-1);
        } catch (QuantisationException e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(-1);
        }
        /*
         * Unless I'm mistaken this should never occur, however, the compiler
         * seems to require it.
         */
        return 0.0;
    }

    public Panel getPanel() {
        return panel;
    }

    public String getLabel() {
        return label;
    }
}
/*
*/
