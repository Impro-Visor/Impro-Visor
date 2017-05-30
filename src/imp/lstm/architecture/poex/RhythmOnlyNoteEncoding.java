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
