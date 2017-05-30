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
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import imp.lstm.utilities.NNUtilities;
import mikera.vectorz.impl.Vector0;

/**
 *
 * @author cssummer16
 */
public class ChordRelativeNoteEncoding implements RelativeNoteEncoding {
    boolean withArtic;
    
    public ChordRelativeNoteEncoding(boolean withArtic){
        this.withArtic = withArtic;
    }
    
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
        else {
            int rel_idx = (midi_number - chord_root) % 12;
            if (rel_idx<0)
                rel_idx += 12;
            return NNUtilities.onehot(rel_idx+2, this.activation_width());
        }
    }

    @Override
    public int activation_width() {
        return activation_width(withArtic);
    }
    
    public int activation_width(boolean includingArticBits) {
        if(includingArticBits)
            return 1+1+12;
        else
            return 12;
    }

    @Override
    public AVector getProbabilities(AVector activations, int chord_root, int low_bound, int high_bound) {
        AVector probs = Operations.Softmax.operate(activations);
        AVector absolute_probs,relative_probs;
        if(withArtic) {
            absolute_probs = probs.subVector(0, 2);
            relative_probs = probs.subVector(2, activations.length()-2);
        } else {
            absolute_probs = Vector.of(1,1);
            relative_probs = activations;
        }
        AVector rolled = NNUtilities.roll(relative_probs, chord_root-low_bound);
        int join_times = (high_bound - low_bound + 11)/12;
        AVector tiled = rolled;
        for(int i=1; i<join_times; i++)
            tiled = tiled.join(rolled);
        AVector full_slice = tiled.subVector(0, high_bound-low_bound);
        return absolute_probs.join(full_slice);
    }

    @Override
    public int get_relative_position(int chord_root) {
        return chord_root;
    }
    
}
