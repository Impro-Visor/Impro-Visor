/*
 * BestNSurvivorSelector.java 0.1.1 11th December 2000
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

import jm.music.data.Phrase;

/**
 * @author  Adam Kirby
 * @version 0.1.1, 11th December 2000
 */
public class BestNSurvivorSelector extends SurvivorSelector {
    public BestNSurvivorSelector() {
    }

    public Phrase[] selectSurvivors(Phrase[] population,
            double[] fitness, Phrase parents[], double[] parentsFitness) {
        Phrase[] returnPopulation = new Phrase[population.length];
        double[] combinedFitness = new double[fitness.length + parentsFitness.length];
        for (int i = 0; i < fitness.length; i++) {
            combinedFitness[i] = fitness[i];
        }
        for (int i = fitness.length; i < combinedFitness.length; i++) {
            combinedFitness[i] = parentsFitness[i - fitness.length];
        }
        int[] indices = new int[returnPopulation.length];
        int currentBestIndex;
        boolean flag;
        boolean[] isUsed = new boolean[combinedFitness.length];
        for (int i = 0; i < indices.length; i++) {
            currentBestIndex = combinedFitness.length - 1;
            for (int l = combinedFitness.length - 1; l >= 0; l--) {
                if (!isUsed[l]) {
                    currentBestIndex = l;
                }
            }
//            currentBestIndex = combinedFitness.length - 1;
            for (int j = 0; j < combinedFitness.length; j++) {
                flag = true;
                if (combinedFitness[j] > combinedFitness[currentBestIndex]) {
                    for (int k = 0; k < i; k++) {
                        if (j == indices[k]) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        currentBestIndex = j;
                    }
                }
            }
            indices[i] = currentBestIndex;
            isUsed[currentBestIndex] = true;

        }
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < population.length) {
                returnPopulation[i] = population[indices[i]];
            } else {
                returnPopulation[i] = parents[indices[i] - population.length];
            }
        }
        return returnPopulation;
    }
}
