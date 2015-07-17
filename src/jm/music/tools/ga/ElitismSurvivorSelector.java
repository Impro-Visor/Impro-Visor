/*
 * ElitismSurvivorSelector.java 0.1.1 11th December 2000
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
 * @author    Adam Kirby and Andrew Brown
 * @version   0.1.1, 11th December 2000
 */
public class ElitismSurvivorSelector extends SurvivorSelector {
    // How many of the best population to keep
    private static final int ELITISM_CONSTANT = 2;
    
    public ElitismSurvivorSelector() {
    }

    public Phrase[] selectSurvivors(Phrase[] population,
            double[] fitness, Phrase children[], double[] childrensFitness) {
        Phrase[] returnPopulation = new Phrase[population.length];

        int[] eliteIndices = new int[ELITISM_CONSTANT];
        // which phrase in the population is the best
        int currentBestIndex = -1;
        boolean flag;
        boolean[] isUsed = new boolean[fitness.length];
        for (int i = 0; i < eliteIndices.length; i++) {
            currentBestIndex = fitness.length - 1;
            for (int j = fitness.length - 1; j >= 0; j--) {
                if (!isUsed[j]) {
                    currentBestIndex = j;
                }
            }
            for (int j = 0; j < fitness.length - 1; j++) {
                flag = true;
                if (fitness[j] > fitness[currentBestIndex]) {
                    for (int k = 0; k < i; k++) {
                        if (j == eliteIndices[k]) {
                            flag = false;
                        }
                    }
                    if (flag == true)  {
                        currentBestIndex = j;
                    }
                }
                eliteIndices[i] = currentBestIndex;
                isUsed[currentBestIndex] = true;
            }
        }
        
        // sort phrases into return population based on fitness
        for (int i = 0; i < eliteIndices.length; i++) {
            returnPopulation[i] = population[eliteIndices[i]];
        }
        
        // replace all but the best with thier children
        for (int i = 0; i < returnPopulation.length - ELITISM_CONSTANT; i++) {
            returnPopulation[i + ELITISM_CONSTANT] = children[i];
        }
        //display top score
        //System.out.println("Elitism  best Index = " + currentBestIndex + " score = " + fitness[currentBestIndex]);
        return returnPopulation;
    }
}
