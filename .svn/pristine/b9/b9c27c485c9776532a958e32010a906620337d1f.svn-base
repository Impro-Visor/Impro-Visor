/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.cykparser;

import polya.Polylist;

/**AbstractProduction
 * An abstract class for production rules
 * @author Xanda
 */


public abstract class AbstractProduction {

    // Getters for data members of the production
    abstract public String getHead();
    abstract public String getBody();
    
    // Getter for the cost of a brick/node produced by this rule
    abstract public long getCost();
    abstract public String getType();
    abstract public String getMode();
    
    // Dictionary specifying asymmetrical chord substitutions
    protected static Polylist adict = Polylist.list(Polylist.list("bass", "any"), 
        /*Polylist.list("major", "dominant"), */ Polylist.list("half-diminished", "minor7"),
        Polylist.list("sus4", "dominant"), Polylist.list("augmented", "major"), 
        Polylist.list("minor7", "minor"));

    // Composite class for production checking
    public class MatchValue {
        public long chordDiff; // interval difference between production and chord 
        public long cost;  // duration of production being compared
        public boolean familyMatch; 
        // constructors
        public MatchValue() {
            chordDiff = -1;
            cost = Long.MAX_VALUE;
            familyMatch = false;
        }
        public MatchValue(long cd, long c, boolean b) {
            chordDiff = cd;
            cost = c;
            familyMatch = b;
        }
    }
}
