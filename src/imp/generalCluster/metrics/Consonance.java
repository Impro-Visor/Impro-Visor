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

import imp.data.Unit;
import static imp.generalCluster.CreateGrammar.removeTrailingSpaces;
import imp.generalCluster.IndexedMelodyPart;
import java.util.ArrayList;
import java.util.Vector;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class Consonance extends Metric{
    
    
    public Consonance(double weight){
        super(weight, "consonance", true);
    }
    
    private double getConsonance(String ruleString, IndexedMelodyPart p) {
        int consonance = 0;
        
        Vector<Character> noteTypes = new Vector<Character>();

        for (int i = 0; i < ruleString.length(); i++) {
            char c = ruleString.charAt(i);
            if (c == 'C' || c == 'L' || c == 'A' || c == 'X' || c == 'R' || c == 'G') {
                noteTypes.add(c);
            }
        }

        //this should only happen for all rests, where rests were not properly merged
        ArrayList<Unit> units = p.getUnitList();
        if (units.size() != noteTypes.size()) {
            return 0;
        }
        
        for (int i = 0; i < noteTypes.size(); i++) {
            int noteLength = units.get(i).getRhythmValue();
            switch (noteTypes.get(i)) {
                case 'G':
                    consonance += 1 * noteLength;
                    break;
                case 'C':
                    consonance += .8 * noteLength;
                    break;
                case 'A':
                    consonance += .6 * noteLength;
                    break;
                case 'L':
                    consonance += .4 * noteLength;
                    break;
                case 'X':
                    consonance += .1 * noteLength;
                    break;
                default:
                    break;
            }
        }
        
       // System.out.println("consonance is: " + consonance);

        return consonance;
    }
    
    public double getTheConsonance(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
          //extract chord data
        int stopIndex = ruleString.length();
        if (ruleString.contains("CHORDS")) {
            stopIndex = ruleString.indexOf("CHORDS");
            ruleString = ruleString.substring(0, stopIndex);
        }

        ruleString = removeTrailingSpaces(ruleString);

        //extract exact melody data
        if (ruleString.contains("Head")) {
            stopIndex = ruleString.indexOf("Head");
        } else if (ruleString.contains("Chorus")) {
            stopIndex = ruleString.indexOf("Chorus");
            String rest = ruleString.substring(stopIndex);
            int firstSpaceIndex = rest.indexOf(" ");
        }


        //remove the exact melody from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);
        
        //extract brick type data
        //string "Brick-type" denotes where the brick type information is
        stopIndex = ruleString.indexOf("(Brick-type ");
        
        //remove the brick type data from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);

        //extract X notation melody data
        //string "Xnotation" denotes start of the X notation.  
        stopIndex = ruleString.indexOf("(Xnotation"); //find "Xnotation" delimiter

        //remove the X notation from the string now that we've extracted it
        ruleString = ruleString.substring(0, stopIndex - 1);
        ruleString = removeTrailingSpaces(ruleString);

        
        
        if (rule.last().equals("STARTTIED")) {
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.indexOf("STARTTIED") - 1).concat(" )");
        }

        //determine if a measure is tied at start or end
        if (rule.last().equals("ENDTIED")) {
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.indexOf("ENDTIED") - 1).concat(" )");
        }

        //determine if a rule is a 'song starter'
        if (rule.last().equals("STARTER")) {
            rule = rule.allButLast();
            ruleString = ruleString.substring(0, ruleString.length() - 9).concat(" )");
        }

        //remove extra junk from the rule string such as "(rule (Seg 4)"
        ruleString = ruleString.substring(ruleString.indexOf("Seg") + 7, ruleString.length() - 3);
        //if the Seg number is 2 digits, this will leave an extra parenthesis at the beginning
        //so we must chop that off too if it exists
        if (ruleString.startsWith("((")) {
            ruleString = ruleString.substring(1, ruleString.length());
        }
        //System.out.println("ruleString before getConsonance: "+ruleString);
        return getConsonance(ruleString, exactMelody);

        
    }
    
    @Override
    public double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule){
        //System.out.println("ruleString in consonance: "+ruleString);
        this.value = getTheConsonance(ruleString, exactMelody, rule);
        return this.value;
    }
}
