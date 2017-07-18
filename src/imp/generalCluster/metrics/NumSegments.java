/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics;

import imp.data.Duration;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;

/**
 *
 * @author Cai Glencross
 */
public class NumSegments extends Metric{
    
    public NumSegments(double weight){
        super(weight, "numSegments", true);
    }
    
    public double getNumSegments(Polylist rule){
        int numSegments=0;
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
        
        return numSegments;
    }
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getNumSegments(rule);
        return this.value;
    }
    
}
