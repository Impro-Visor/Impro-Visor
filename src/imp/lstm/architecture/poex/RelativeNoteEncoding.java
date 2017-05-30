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
 *
 * @author cssummer16
 */
public interface RelativeNoteEncoding {

    /**
     * Reset the encoding to its initial state, and return the initial encoded
     * form
     * @return The initial encoded form for the encoding
     */
    public AVector reset();

    /**
     * Encode a midi note, possibly relative to the current chord root.
     * Successive calls to encode after a reset must correspond to the same
     * melody, so state can be saved internally.
     * @param midi_number The midi number of the current note
     * @param chord_root The current chord root
     * @return
     */
    public AVector encode(int midi_number, int chord_root);
    
    /**
     * Get the current relative position of the encoding
     * @param chord_root The current chord root
     * @return The current relative position (midi number)
     */
    public int get_relative_position(int chord_root);

    /**
     * Get the desired width of the activations from the neural network layer.
     * @return Activation width
     */
    public int activation_width();

    /**
     * Transform a set of activations into a set of probabilities for each absolute note.
     * The activations may be considered to be relative to the position as of
     * the last call to encode.
     * @param activations The output activations from the nn layer
     * @param chord_root The current chord root
     * @param low_bound Midi number of lowest note in returned probabilities
     * @param high_bound Midi number of one past highest note in returned probabilities
     * @return A vector of length 2+(high_bound-low_bound), where index 0 gives
     * probability of rest, 1 gives probability of sustain, and (i+2) gives
     * probability of playing note (i+low_bound).
     */
    public AVector getProbabilities(AVector activations, int chord_root, int low_bound, int high_bound);
}
