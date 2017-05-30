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
