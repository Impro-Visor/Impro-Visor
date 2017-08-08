/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

package imp.generalCluster.metrics;

import imp.data.Duration;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;

/**
 *
 * @author Cai Glencross
 */
public class AverageMaxSlope extends Metric  {
    
    public AverageMaxSlope(double weight){
        super(weight, "averageMaxSlope", true);
    }
    
    public double getAverageMaxSlope(Polylist rule){
        double averageMaxSlope = 0;
        int numSegments = 0;
         if (rule.last().equals("STARTTIED")) {
            rule = rule.allButLast();
        }

        //determine if a measure is tied at start or end
        if (rule.last().equals("ENDTIED")) {
            rule = rule.allButLast();
        }

        //determine if a rule is a 'song starter'
        if (rule.last().equals("STARTER")) {
            rule = rule.allButLast();
        }

        rule = rule.rest();

        while (rule.nonEmpty()) {
            if (rule.first() instanceof Polylist) {
                Polylist inner = (Polylist) rule.first();
                //get maximum slope
                //inner is the slope polylist
                int maxslope;
                if (Math.signum(Integer.parseInt(inner.second().toString())) == -1
                        || Math.signum(Integer.parseInt(inner.third().toString())) == -1) {
                    maxslope = Integer.parseInt(inner.second().toString());
                } else {
                    maxslope = Integer.parseInt(inner.third().toString());
                }
                averageMaxSlope += Math.abs(maxslope);
                //get rid of slopes
                inner = inner.rest().rest().rest();
               
                //increment number of segments
                numSegments++;
                //loop through terminals of segments
                while (inner.nonEmpty()) {
                    inner = inner.rest();
                }
            }
            rule = rule.rest();
        }
        return averageMaxSlope / numSegments;
    }
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getAverageMaxSlope(rule);
        return this.value;
    }
}
