/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lstm.architecture.poex;

import mikera.vectorz.AVector;

/**
 * Postprocessor to merge repeated pitches.
 * @author Daniel Johnson
 */
public class MergeRepeatedPostprocessor implements ProbabilityPostprocessor {
    private int lowBound;
    private int lastPlayedNote;
    
    public MergeRepeatedPostprocessor(int lowBound){
        this.lowBound = lowBound;
    }
    
    public void reset(){
        lastPlayedNote = -1;
    }
    
    public void noteWasPlayed(int note){
        if(note != -2){ // if it isn't sustain, then it is a new note
            lastPlayedNote = note;
        }
    }
    
    
    @Override
    public AVector postprocess(AVector probabilities) {
        //if last played was a rest, we don't care what the network does
        if(lastPlayedNote != -1){
            // find the probability that it plays the same note twice, change
            // it to a probability of sustain
            int repeatIndex = lastPlayedNote - lowBound + 2;
            double repeatProb = probabilities.get(repeatIndex);
            double sustainProb = probabilities.get(1);
            probabilities.set(repeatIndex, 0.0);
            probabilities.set(1, repeatProb+sustainProb);
        }
        return probabilities;
    }
    
}
