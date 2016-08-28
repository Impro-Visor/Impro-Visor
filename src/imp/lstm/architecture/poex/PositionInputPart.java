/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture.poex;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.RangeVector;

/**
 *
 * @author cssummer16
 */
public class PositionInputPart extends RelativeInputPart {
    private int lowBound;
    private int highBound;
    private int num_divisions;
    
    public PositionInputPart(int lowBound, int highBound, int num_divisions) {
        this.lowBound = lowBound;
        this.highBound = highBound;
        this.num_divisions = num_divisions;
    }

    
    @Override
    public AVector generate(int relativePosition, int chordRoot, AVector chordTypeData) {
        double delta = (double)(this.highBound - this.lowBound) / (this.num_divisions - 1);
        AVector indicator = RangeVector.create(0, this.num_divisions).mutable();
        indicator.multiply(delta);
        indicator.add(this.lowBound);
        indicator.negate();
        indicator.add(relativePosition);
        indicator.divide(delta);
        indicator.abs();
        indicator.negate();
        indicator.add(1.0);
        indicator.clampMin(0.0);
        return indicator;
    }
    
}
