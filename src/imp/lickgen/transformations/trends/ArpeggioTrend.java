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

import imp.Constants;
import imp.data.*;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.TrendSegment;
import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public class ArpeggioTrend extends Trend{

    private static final int MAJOR_THIRD = 4;
    
    //stops when it encounters an interval greater than a major third
    public boolean stopCondition(Note n1, Note n2) {
        return absDist(n1, n2) > MAJOR_THIRD;
    }

    //returns whether a note is a chord tone of a chord (false if chord is no chord or note is rest)
    public static boolean chordTone(Note n, Chord c){
        return !c.isNOCHORD() && !n.isRest() && c.getTypeIndex(n) == Constants.CHORD_TONE;
    }
    
    //trend continues as long as the notes are chord tones
    public boolean stopCondition(Note n, Chord c) {
        return !chordTone(n, c);
    }

    //no weight given to priority, lots to strong beat, some to duration
    public double[] weights() {
        double [] weights = {0, 1, .5};
        return weights;
    }

    //2 sections for now - will change
    public int numberOfSections() {
        return 2;
    }

    public String getName() {
        return "ARPEGGIO";
    }

}
