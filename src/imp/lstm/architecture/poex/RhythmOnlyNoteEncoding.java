/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture.poex;

import imp.lstm.filters.Operations;
import imp.lstm.utilities.NNUtilities;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 *
 * @author danieljohnson
 */
public class RhythmOnlyNoteEncoding implements RelativeNoteEncoding {

    @Override
    public AVector reset() {
        return NNUtilities.onehot(0, this.activation_width());
    }

    @Override
    public AVector encode(int midi_number, int chord_root) {
        if(midi_number == -1)
            return NNUtilities.onehot(0, this.activation_width());
        else if(midi_number == -2)
            return NNUtilities.onehot(1, this.activation_width());
        else
            return NNUtilities.onehot(2, this.activation_width());
    }

    @Override
    public int get_relative_position(int chord_root) {
        return chord_root;
    }

    @Override
    public int activation_width() {
        return 3;
    }

    @Override
    public AVector getProbabilities(AVector activations, int chord_root, int low_bound, int high_bound) {
        AVector probs = Operations.Softmax.operate(activations);
        AVector restOrSustain = probs.subVector(0, 2);
        AVector artic = Vector.createLength(high_bound - low_bound);
        artic.fill(probs.get(2));
        return restOrSustain.join(artic);
    }
    
}
