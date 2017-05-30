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
