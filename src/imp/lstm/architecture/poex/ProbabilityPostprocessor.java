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
