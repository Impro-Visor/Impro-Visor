/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.cluster;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Jon Gillick
 */

public class NGramWithTransitions implements Serializable{
        
        //Currently this class only makes sense for bigrams
    
    
        private int state;
        private Vector<Integer> nextStates = new Vector<Integer> ();
        private Vector<Float> probabilities = new Vector<Float> ();
        private Vector<Float> probSums = new Vector<Float> (); //use in getting a random next state
        
        public NGramWithTransitions(int thisState, Vector<NGram> chains) {
            state = thisState;
            for(int i = 0; i < chains.size(); i++) {
                NGram ngram = chains.get(i);
                if(ngram.getFirst() == state)  {
                    nextStates.add(ngram.getLast());
                    probabilities.add(ngram.getProbability());
                }
            }
            setProbSums();
        }
        
        private void setProbSums() {
            float sum = 0;
            for(int i = 0; i < probabilities.size(); i++) {
                sum += probabilities.get(i);
                probSums.add(sum);
            }
        }
        
        public int getNextState() {
            double rand = Math.random();
            for(int i = 0; i < probSums.size()-1; i++) {
                if(rand < probSums.get(i))
                    return nextStates.get(i);
            }
            return nextStates.lastElement();
        }
        
        public int getNextState(int[] possibilities) {
            Vector<Integer> states = new Vector<Integer>();
            Vector<Float> probs = new Vector<Float>();
            Vector<Float> sums = new Vector<Float>();
            
            //fill in new vectors of states and probs based on the possibilities
            for (int i = 0; i < possibilities.length; i++) {
                for (int j = 0; j < nextStates.size(); j++) {
                    if(nextStates.get(j) == possibilities[i]) {
                        states.add(nextStates.get(j));
                        probs.add(probabilities.get(j));
                    }
                }
            }
            
            //if the there are no states both in nextStates and possibilities, return -1
            if(states.size() == 0)
                return -1;
            
            //normalize the probabilites in probs to add to 1
            float sum = 0;
            for(int i = 0; i < probs.size(); i++) 
                sum += probs.get(i);
            float multiplier = 1/sum;
            for(int i = 0; i < probs.size(); i++) {
                float p = probs.get(i);
                p *= multiplier;
            }
            
            //set the sums for the new Vectors
            float s = 0;
            for(int i = 0; i < probs.size(); i++) {
                s += probs.get(i);
                sums.add(s);
            }
            
            //choose a next state probabilistically from the possibilities
            double rand = Math.random();
            for(int i = 0; i < sums.size()-1; i++) {
                if(rand < sums.get(i))
                    return states.get(i);
            }
            return states.lastElement();
        }
       
        
        public Vector<Integer> getStates() {
            return nextStates;
        }
        
        public Vector<Float> getProbabilities() {
            return probabilities;
        }
    
        public int getState() {
            return state;
        }
    
        public Vector<Float> getProbSums() {
            return probSums;
        }
        
        public float getSumOfProbs() {
            float s = 0;
            for(int i = 0; i < probabilities.size(); i++) {
                s += probabilities.get(i);
            }
            return s;
        }
}
