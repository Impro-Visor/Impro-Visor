/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import mikera.vectorz.AVector;

/**
 * Postprocessor to force the network to play. Used by LSTMGen to prevent long
 * rests.
 * @author Daniel Johnson
 */
public class ForcePlayPostprocessor implements ProbabilityPostprocessor {
    private boolean shouldForce = false;
    
    public void forcePlayNext(){
        shouldForce = true;
    }
    
    public void reset(){
        shouldForce = false;
    }
    
    @Override
    public AVector postprocess(AVector probabilities) {
        if(shouldForce){
            probabilities.set(0, 0.0);
            probabilities.set(1, 0.0);
            shouldForce = false;
        }
        return probabilities;
    }
}
