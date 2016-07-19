/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import lstm.architecture.FullyConnectedLayer;
import lstm.architecture.LSTM;
import lstm.architecture.Loadable;
import lstm.filters.Operations;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public class Expert implements Loadable {
    private LSTM lstm1;
    private LSTM lstm2;
    private FullyConnectedLayer fullLayer;

    public Expert(Operations outputOperation) {
        this.lstm1 = new LSTM();
        this.lstm2 = new LSTM();
        this.fullLayer = new FullyConnectedLayer(outputOperation);
    }
    
    public AVector process(AVector input) {
        AVector val1 = lstm1.step(input);
        AVector val2 = lstm2.step(val1);
        AVector val3 = fullLayer.forward(val2);
        return val3;
    }
    
    @Override
    public boolean load(INDArray data, String loadPath) {
        String car = pathCar(loadPath);
        String cdr = pathCdr(loadPath);
        switch(car)
        {
            case "full": return fullLayer.load(data, cdr);
            case "lstm1": return lstm1.load(data, cdr);
            case "lstm2": return lstm2.load(data, cdr);
            default: return false;
        }
    }
}
