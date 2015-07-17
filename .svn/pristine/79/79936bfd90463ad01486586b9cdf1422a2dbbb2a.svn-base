/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Brandy McMenamy and James Thomas Herrold June 2007
 *
 * BassChronotonic: input: an ArrayList<String> where each String is a bass-line
 * style- specification of pitches and rhythms (like Impro-Visor's bass rules)
 *
 * Creates a two dimensional matrix that describes the chronotonic measurement
 * between each rule. The indices of the matrix directly correlate to the
 * indices of the rules in the original ArrayList and row k and col k refer to
 * the same rule.
 *
 * Only rules of the same length can have a chronotonic value. The default value
 * -1 is used for two rules of unequal length
 */
public class BassChronotonic
{

private ArrayList<ArrayList<String>> rhythmValues; //same as stringRules except no pitch info
private ArrayList<ArrayList<Double>> histoRules; //in histogram form
private OrderedPair[][] chronoValues; // the two-D matrix of all chronotonic values

/**
 * constructor
 *
 * @param info - ArrayList<String> where each String is a bass-line style
 * specification of pitches and rhythms @precondition:
 * MIDIBeast.originalBassRules does not contain any mal-formed rules
 */

public BassChronotonic()
  {
    /*
     * orignalBassRules, which is processed earlier by BassPatternGenerator,
     * should be formatted correctly. This protection is added just in case
     * something unexpected slips through
     */
    try
      {
        ArrayList<String> originalRules = MIDIBeast.originalBassRules;
        rhythmValues = new ArrayList<ArrayList<String>>();
        histoRules = new ArrayList<ArrayList<Double>>();

        for( int i = 0; i < originalRules.size(); i++ )
          {
            rhythmValues.add(MIDIBeast.stripPitch(originalRules.get(i)));
          }

        for( int i = 0; i < rhythmValues.size(); i++ )
          {
            histoRules.add(toHistoArray(rhythmValues.get(i)));
          }

        //fill chronoValues with default value of -1.0
        chronoValues = new OrderedPair[histoRules.size()][histoRules.size()];
        for( int i = 0; i < chronoValues.length; i++ )
          {
            for( int j = 0; j < chronoValues.length; j++ )
              {
                chronoValues[i][j] = new OrderedPair(originalRules.get(i), originalRules.get(j), -1.0);
              }
          }

        for( int i = 0; i < histoRules.size(); i++ )
          {
            for( int j = 0; j < histoRules.size(); j++ )
              { //int j = i or int j= i+1 would conserve memory
                if( histoRules.get(i).size() == histoRules.get(j).size() )
                  {
                    chronoValues[i][j] = new OrderedPair(originalRules.get(i), originalRules.get(j), chronoCompare(histoRules.get(i), histoRules.get(j)));
                  }
              }
          }
      }
    catch( Exception e )
      {
        MIDIBeast.addError("Sorry, there was an unknown internal error while extracting "
                + "the bass patterns.");
      }
  }

/**
 * @return chronoValues, the two-D matrix of all chronotonic values
 */

public OrderedPair[][] getChronoValues()
  {
    return chronoValues;
  }

/**
 * precondition: both i and j are within the bounds of chronoValues
 *
 * @param i - indice of a rule
 * @param j - indice of a rule
 * @return the chronotonic value between rule i and rule j
 */

public double getChronoValueAt(int i, int j)
  {
    return chronoValues[i][j].getValue();
  }

/**
 * @return rhythmValues, the original rules without pitch information
 */

public ArrayList<ArrayList<String>> getRhythmValues()
  {
    return rhythmValues;
  }

/**
 * prints an OrderedPair[][] to terminal
 *
 * @param values - any OrderedPair[][]
 */

public void printMatrix(OrderedPair[][] values)
  {
    for( int i = 0; i < values.length; i++ )
      {
        for( int j = 0; j < values[i].length; j++ )
          {
            System.out.print(values[i][j].getValue() + ", ");
          }
        System.out.println();
      }
  }

/**
 * prints the chronotonic values for a rule in increasing order
 *
 * @param r - the chronotonic values for a single rule (a row or column in
 * chronoValues)
 */

public void printRuleIncOrder(OrderedPair[] r)
  {
    Arrays.sort(r);
    for( int i = 0; i < r.length; i++ )
      {
        if( r[i].getValue() > -1 )
          {
            System.out.print(MIDIBeast.stripPitch(r[i].getRuleB()) + ": ");
            System.out.println(r[i].getValue());
          }
      }
  }

/**
 * @param aRule - the rhythm information only for a single bass rule
 * @return the histogram representation of aRule
 */

public ArrayList<Double> toHistoArray(ArrayList<String> aRule)
  {
    ArrayList<Double> rule = new ArrayList<Double>();

    for( int i = 0; i < aRule.size(); i++ )
      {
        String[] ithEntries = aRule.get(i).split("\\+");
        double sum = 0.0;
        for( int j = 0; j < ithEntries.length; j++ )
          {
            sum += MIDIBeast.getSlotValueFor(ithEntries[j]);
          }
        for( int j = 0; j < sum; j++ )
          {
            rule.add(sum);
          }
      }

    return rule;
  }

/**
 * Assumes both rules have the same size
 *
 * @param ruleOne
 * @param ruleTwo
 * @return the chronotonic value of ruleOne compared to ruleTwo
 */

public double chronoCompare(ArrayList<Double> ruleOne, ArrayList<Double> ruleTwo)
  {
    double value = 0.0;
    double volume1 = 0.0;
    double volume2 = 0.0;

    for( int i = 0; i < ruleOne.size(); i++ )
      {
        double volume = ruleOne.get(i) * ruleOne.get(i);
        if( ruleOne.get(i) < 0 )
          {
            volume1 += -volume;
          }
        else
          {
            volume1 += volume;
          }
      }
    for( int i = 0; i < ruleTwo.size(); i++ )
      {
        double volume = ruleTwo.get(i) * ruleTwo.get(i);
        if( ruleTwo.get(i) < 0 )
          {
            volume2 += -volume;
          }
        else
          {
            volume2 += -volume;
          }
      }

    return Math.abs(volume1 - volume2);
    //for(int i = 0; i < ruleOne.size(); i++) {
    //	value += Math.abs(ruleOne.get(i) - ruleTwo.get(i));
    //}
    //
    //return value;
  }

/**
 * @author Brandy McMenamy, Jim Herrold
 */
private class OrderedPair implements Comparable
{

private double value;
private String ruleA;
private String ruleB;

/**
 *
 * @param r1
 * @param r2
 * @param v the chronotonic value of r1 compared to r2
 */
public OrderedPair(String r1, String r2, double v)
  {
    ruleA = r1;
    ruleB = r2;
    value = v;
  }

public double getValue()
  {
    return value;
  }

public String getRuleA()
  {
    return ruleA;
  }

public String getRuleB()
  {
    return ruleB;
  }

public int compareTo(Object obj) throws ClassCastException
  {
    OrderedPair o = (OrderedPair) obj;
    if( value > o.getValue() )
      {
        return 1;
      }
    else if( value < o.getValue() )
      {
        return -1;
      }

    return 0;
  }

}
}
