/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
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

package imp.lickgen.transformations.trends;

import polya.Polylist;
import imp.data.*;

/**
 *
 * @author muddCS15
 */
public class DiatonicTrend extends Trend{

    private static final int MAJOR_THIRD = 4;
    
    //stops when it encounters a big interval
    public boolean stopCondition(Note n1, Note n2) {
        return absDist(n1, n2) > MAJOR_THIRD;
    }

    //returns whether a note belongs to a chord's primary scale
    public boolean diatonic(Note n, Chord c){
        
        if(n.isRest() || c.isNOCHORD()){
            return false;
        }
        
        Polylist firstScale = c.getFirstScale();
        if(firstScale == null){
            return false;
        }
        
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        return ns.enhMember(firstScale);
    }
    
    //trend continues so long as notes belong to chord's first scale
    public boolean stopCondition(Note n, Chord c) {
        return !diatonic(n, c);
    }

    //priority important, then strong beat and duration
    public double[] weights() {
        double [] weights = {1, .5, .5};
        return weights;
    }

    //2 for now - will change
    public int numberOfSections() {
        return 2;
    }

    public String getName() {
        return "DIATONIC";
    }
    
}
