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

package imp.lstm.architecture;

import imp.lstm.filters.Operations;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.matrixx.AMatrix;

/**
 * Class FullyConnectedLayer is an implementation of a simple neural network layer which multiplies inputs by a weight matrix and adds biases,
 * then performs an operation on the resultant vector such as sigmoid or tanh.
 * @author Nicholas Weintraut
 */
public class FullyConnectedLayer implements Loadable {
    
    private AMatrix weights;
    private AVector biases;
    private Operations type;
    
    private AVector multResult;
    
    public FullyConnectedLayer (Operations type)
    {
        this.type = type;
    }
    
    public AVector forward (AVector input)
    {
            
            multResult = weights.innerProduct(input);
            multResult.add(biases);
            return type.operate(multResult);
    }

    @Override
    public boolean load(INDArray data, String loadPath) {
        switch(loadPath)
        {
            case "b":   this.biases = (AVector) data;
                        return true;
            case "w":   this.weights = (AMatrix) data;
                        return true;
            default:    return false;
        }
    }
}
