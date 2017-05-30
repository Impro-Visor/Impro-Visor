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

import imp.data.*;

/**
 *
 * @author muddCS15
 */
public class DescendingTrend extends Trend{

    private static final int WHOLE_STEP = 2;
    
    //staying on the same note is okay (not strictly descending)
    public static boolean descending(Note n1, Note n2){
        return dist(n1, n2) <= 0;
    }

    //trend continues so long as direction is descending
    //and so long as the motion is stepwise
    public boolean stopCondition(Note n1, Note n2) {
        return !descending(n1, n2) && absDist(n1, n2) > WHOLE_STEP;
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //equal weights
    public double[] weights() {
        double [] weights = {1, 5, 1};
        return weights;
    }

    //2 for descending
    public int numberOfSections() {
        return 2;
    }

    public String getName() {
        return "DESCENDING";
    }

}
