/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics;

import imp.data.Duration;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.IndexedMelodyPart;
import polya.Polylist;
import static imp.Constants.BEAT;

/**
 *
 * @author Cai Glencross
 */
public class PercentRests extends Metric{
    
    public PercentRests(float weight){
        super(weight, "percentRests", true);
    }
    
    public double getRestDuration(Polylist rule){
        double restDuration = 0;
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
               
                //loop through terminals of segments
                while (inner.nonEmpty()) {
                    String terminal = inner.first().toString();
                    if (terminal.charAt(0) == 'R') { 
                        restDuration += Duration.getDuration(terminal.substring(1));
                    }
                    inner = inner.rest();
                }
            }
            rule = rule.rest();
        }
        return restDuration;
    }
    
    public int getSegLength(Polylist rule){
        return Integer.parseInt(rule.first().toString().substring(CreateGrammar.SEG_LENGTH));
    }
    
    
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        double segLength = getSegLength(rule);
        this.value = getRestDuration(rule) / segLength * BEAT;
        
        return this.value;
    }
}
