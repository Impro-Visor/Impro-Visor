/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
