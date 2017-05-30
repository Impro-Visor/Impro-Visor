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

import imp.lstm.architecture.FullyConnectedLayer;
import imp.lstm.architecture.LSTM;
import imp.lstm.architecture.Loadable;
import imp.lstm.filters.Operations;
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
