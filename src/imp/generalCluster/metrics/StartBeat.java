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
 * @author cssummer17
 */
public class StartBeat extends Metric{
    
    public StartBeat(double weight){
        super(weight, "startBeat", true);
    }
    
    public double getStartBeat(Polylist rule){
        int startBeat = -1;
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

        //remove extra junk from the rule string such as "(rule (Seg 4)"
        //if the Seg number is 2 digits, this will leave an extra parenthesis at the beginning
        //so we must chop that off too if it exists

        rule = rule.rest();

        while (rule.nonEmpty()) {
            if (rule.first() instanceof Polylist) {
                Polylist inner = (Polylist) rule.first();
                //get rid of slopes
                inner = inner.rest().rest().rest();
                //get startbeat if needed
                if (startBeat == -1) {
                    String terminal = inner.first().toString();
                    //check if first beat is rest
                    if (terminal.charAt(0) == 'R') {
                        //check if rest releases on a startbeat
                        if (Duration.getDuration(terminal.substring(1)) % 120 == 0) {
                            startBeat = 1;
                        } else {
                            startBeat = 0;
                        }
                    } else {
                        startBeat = 1;
                    }
                }
                //increment number of segments
                //loop through terminals of segments
                while (inner.nonEmpty()) {
                    inner = inner.rest();
                }
            }
            rule = rule.rest();
        }
        return startBeat;
    }
    
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        this.value = getStartBeat(rule);
        return this.value;
    }
    
}
