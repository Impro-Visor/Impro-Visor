/*
 * OnePointCrossover.java 0.1.1 11th December 2000
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

import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Phrase;

/**
 * @author  Adam Kirby
 * @version 0.1.1, 11th December 2000
 */
public class OnePointCrossover extends Recombiner {
    private static final int ROUNDS = 10;

    private static final int ELITISM_CONSTANT = 2;

    public OnePointCrossover() {
    }
    
    public Phrase[] recombine(Phrase[] population, double[] fitness,
                              double initialLength, int initialSize,
                              int beatsPerBar) {
        Phrase[] returnPop = population.length - ELITISM_CONSTANT >= 0
                             ? new Phrase[population.length - ELITISM_CONSTANT]
                             : new Phrase[0];
        if (returnPop.length > 1) {
            for (int i = 1; i < returnPop.length; i += 2) {
                int father = selectTournamentVictor(fitness, -1);
                int mother = selectTournamentVictor(fitness, father);
                int numOfFathersBars = (int) population[father].getEndTime()
                                       / beatsPerBar;
                int numOfMothersBars = (int) population[mother].getEndTime()
                                       / beatsPerBar;
                int numOfBars = numOfFathersBars > numOfMothersBars
                                ? numOfMothersBars
                                : numOfFathersBars;
                int crossoverBar = 0;
                while (crossoverBar < (int) (initialLength / beatsPerBar) + 1) {
                    crossoverBar = (int) (Math.random() * (double) numOfBars);
                }
                returnPop[i - 1] = crossover(crossoverBar, population[father],
                                             population[mother], true,
                                             beatsPerBar);
                returnPop[i] = crossover(crossoverBar, population[father],
                                         population[mother], false,
                                         beatsPerBar);
            }
        }
        if ((int) (returnPop.length / 2.0)
                    != Math.round(returnPop.length / 2.0)) {
            int father = selectTournamentVictor(fitness, -1);
            int mother = selectTournamentVictor(fitness, father);
            int numOfFathersBars = (int) population[father].getEndTime()
                                   / beatsPerBar;
            int numOfMothersBars = (int) population[mother].getEndTime()
                                   / beatsPerBar;
            int numOfBars = numOfFathersBars > numOfMothersBars
                            ? numOfMothersBars
                            : numOfFathersBars;
            int crossoverBar = (int) (Math.random() * (double) numOfBars);
            returnPop[returnPop.length - 1] = crossover(crossoverBar,
                                                        population[father],
                                                        population[mother],
                                                        true, beatsPerBar);
        }
        return returnPop;
    }

    private int selectTournamentVictor(double[] fitness, int pastVictor) {
        int champion = pastVictor;
        while (champion == pastVictor) {
            champion = (int) (Math.random() * (double) fitness.length);
        }
        for (int j = 0; j < ROUNDS; j++) {
            int challenger = champion;
            while (challenger == champion || challenger == pastVictor) {
                challenger = (int) (Math.random() * (double) fitness.length);
            }
            if (fitness[challenger] > fitness[champion]) {
                return challenger;
            }
        }
        return champion;
    }

    private Phrase crossover(int crossoverBar, Phrase mother, Phrase father,
                             boolean isFatherFirst, int beatsPerBar) {
        Phrase returnPhrase = new Phrase();

        Phrase currentPhrase = isFatherFirst ? father : mother;
        int currentNote = 0;

        while (returnPhrase.getEndTime()
               + currentPhrase.getNote(currentNote).getRhythmValue()
               < crossoverBar * beatsPerBar) {
            returnPhrase.addNote(currentPhrase.getNote(currentNote++).copy());
        }
        double rhythmValue = crossoverBar * beatsPerBar
                             - returnPhrase.getEndTime();
        returnPhrase.addNote(new Note(
                currentPhrase.getNote(currentNote).getPitch(),
                crossoverBar * beatsPerBar - returnPhrase.getEndTime()));
        currentNote = -1;
        currentPhrase = isFatherFirst ? mother : father;
        double currentRhythmValue = 0;
        while (currentRhythmValue <= crossoverBar * beatsPerBar) {
            currentNote++;
            currentRhythmValue +=
                    currentPhrase.getNote(currentNote).getRhythmValue();
        }
        returnPhrase.addNote(new Note(
                currentPhrase.getNote(currentNote++).getPitch(),
                currentRhythmValue - crossoverBar * beatsPerBar));
        while (currentNote < currentPhrase.size()) {
            returnPhrase.addNote(currentPhrase.getNote(currentNote++));
        }
        return returnPhrase;            
    }
}
