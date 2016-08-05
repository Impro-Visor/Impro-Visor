/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture.poex;

import mikera.vectorz.AVector;

/**
 * A ProbabilityPostprocessor takes as input a set of probabilities for choosing
 * each note, and modifies those probabilities. This is used to constrain the
 * choices the network can make.
 * @author Daniel Johnson
 */
public interface ProbabilityPostprocessor {
    /**
     * Postprocess a set of probabilities.
     * @param probabilities The probabilities produced by the network. As in the
     * encoding, index 0 corresponds to rest, index 1 to sustain, and each
     * following index to a particular pitch, starting with the lower bound of
     * the network. This parameter can be clobbered here, as it is no longer
     * used.
     * @return The new probabilities. If you modify the input in place, just
     * return the original vector.
     */
    public AVector postprocess(AVector probabilities);
}
