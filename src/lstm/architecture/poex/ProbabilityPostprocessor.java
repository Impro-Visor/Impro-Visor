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
public interface ProbabilityPostprocessor {
    public AVector postprocess(AVector probabilities);
}
