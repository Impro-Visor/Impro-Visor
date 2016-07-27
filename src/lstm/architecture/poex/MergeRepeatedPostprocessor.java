/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
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
