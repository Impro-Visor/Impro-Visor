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
public class PassthroughInputPart extends RelativeInputPart {
    private AVector next_result;
    private int valid_ct;
    
    public PassthroughInputPart() {
        this.next_result = null;
        this.valid_ct = 0;
    }
    
    public void provide(AVector next_result, int valid_for) {
        this.next_result = next_result;
        this.valid_ct = valid_for;
    }
    public void provide(AVector next_result) {
        this.provide(next_result,1);
    }


    @Override
    public AVector generate(int relativePosition, int chordRoot, AVector chordTypeData) {
        if(this.valid_ct > 0) {
            this.valid_ct--;
            return this.next_result;
        } else {
            throw new RuntimeException("generate called without providing a result to return!");
        }
    }
    
}
