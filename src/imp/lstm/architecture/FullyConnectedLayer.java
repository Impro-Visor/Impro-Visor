/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
