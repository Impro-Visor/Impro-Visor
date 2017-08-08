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

import imp.data.ContourData;
import imp.generalCluster.IndexedMelodyPart;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author Cai Glencross
 */
public class NumContourChanges extends Metric{
    
    public NumContourChanges(double weight){
        super(weight, "numContourChanges", false);
    }
    
    private static Polylist getSlopePolylist(Polylist rule){
        //get rid of segment part
        rule = rule.rest();
        
        Polylist slopePolylist = new Polylist();
        while(rule.nonEmpty()){
            if(rule.first() instanceof Polylist){
                if(((Polylist)rule.first()).first().equals("slope")){
                    slopePolylist = slopePolylist.addToEnd((Polylist) rule.first());
                }
            }
            rule = rule.rest();
        }
        return slopePolylist;
    }
    
    
    private static ContourData getContourData(Polylist rule){
        Polylist slopePolylist = getSlopePolylist(rule);
        ArrayList<Short> contours = new ArrayList<Short>();
        short prev = -1;
        int numChanges = -1;
        
        while(slopePolylist.nonEmpty()){
            if( ((Polylist) slopePolylist.first()).length() > 4){//if the slope has more than one note
                long minSlope = ((long) ((Polylist) slopePolylist.first()).second());
                if (minSlope<0&&prev!=ContourData.DOWN){
                    contours.add(ContourData.DOWN);
                    prev = ContourData.DOWN;
                    numChanges++;
                }else if (minSlope>=0&&prev!=ContourData.UP){
                    contours.add(ContourData.UP);
                    prev = ContourData.UP;
                    numChanges++;
                }
            }else if(prev!=ContourData.FLAT){
                contours.add(ContourData.FLAT);
                prev = ContourData.FLAT;
                numChanges++;
            }
            slopePolylist=slopePolylist.rest();
        }
        
        return new ContourData(contours, numChanges);
    }
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = (double) getContourData(rule).getNumContourChanges();
        return this.value;
    }
}
