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

/**
 * Brandy McMenamy and James Thomas Herold 7/18/2007 This class uses the
 * Chronotonic method to construct a matrix. Each indice contains the
 * Chronotonic value between any two rules in a given drum Part.
 */
public class DrumChronotonic
{

private boolean debug = false;
/**
 * This is the ArrayList of original rules as created by DrumPatternGenerator
 */
private ArrayList<String> originalRules;
/**
 * The original rules will be taken and represented in this field as squares
 * whose sides are of a length that corresponds to their duration (this is the
 * Chronotonic distance metric.) Example using 16th note divisions: X8 X8 X4 X2
 * --> [2,2,2,2,4,4,4,4,8,8,8,8,8,8,8,8]
 */
private ArrayList<ArrayList<ArrayList<Integer>>> histogram;
/**
 * The number of drum instruments found for the drum Part
         *
 */
private int numberOfInstruments;
/**
 * Contains the chronotonic measurement between pair of rules
         *
 */
private double[][] chronotonicMatrix;

public DrumChronotonic(ArrayList<String> originalRules, int numberOfInstruments)
  {
    /*
     * orignalRules, which is processed earlier by DrumPatternGenerator, should
     * be formatted correctly. This protection is added just in case something
     * unexpected slips through
     */
    try
      {
        this.originalRules = originalRules;
        this.numberOfInstruments = numberOfInstruments;
        makeHistogram();
        makeChronotonicMatrix();
      }
    catch( Exception e )
      {
        MIDIBeast.addError("Sorry, there was an unknown internal error while generating "
                + "the drum patterns.");
      }
  }

/**
 * @return the chronotonic value at (i,j) in the matrix
         *
 */
public double getChronValueAt(int i, int j)
  {
    return chronotonicMatrix[i][j];
  }

/*
 * @return numberOfInstruments
         *
 */
public int getNumberOfInstruments()
  {
    return numberOfInstruments;
  }

/**
 * Creates an array for each rule representing the rhythms as squares whose
 * sides are of length equal to their duration
 */
public void makeHistogram()
  {
    histogram = new ArrayList<ArrayList<ArrayList<Integer>>>();

    for( int i = 0; i < originalRules.size(); i++ )
      {
        histogram.add(new ArrayList<ArrayList<Integer>>());
        String[] individualInstrument = originalRules.get(i).split("\n");
        for( int j = 0; j < individualInstrument.length; j++ )
          {
            histogram.get(i).add(new ArrayList<Integer>());
            String[] ruleElement = individualInstrument[j].substring(individualInstrument[j].indexOf(')') + 1).split(" ");
            for( int k = 0; k < ruleElement.length; k++ )
              {
                int index = ruleElement[k].indexOf('R');
                char type = 'R';
                if( index == -1 )
                  {
                    index = ruleElement[k].indexOf('X');
                    type = 'X';
                  }
                if( index != -1 )
                  {
                    String[] rhythmElement = ruleElement[k].substring(index + 1).split("\\+");
                    int total = 0;
                    for( int l = 0; l < rhythmElement.length; l++ )
                      {
                        total += MIDIBeast.getSlotValueFor(rhythmElement[l]);
                      }
                    for( int m = 0; m < total; m++ )
                      {
                        if( type == 'R' )
                          {
                            histogram.get(i).get(j).add(-total);
                          }
                        else
                          {
                            histogram.get(i).get(j).add(total);
                          }
                      }
                  }
              }
          }
      }
    if( debug )
      {
        System.out.println("## After makeHistogram() ##");
        for( int i = 0; i < histogram.size(); i++ )
          {
            System.out.println("\tMeasure(" + i + ")");
            for( int j = 0; j < histogram.get(i).size(); j++ )
              {
                System.out.print("\t\tInstrument(" + j + ") [");
                for( int k = 0; k < histogram.get(i).get(j).size(); k++ )
                  {
                    if( k != 0 )
                      {
                        System.out.print(", ");
                      }
                    System.out.print(histogram.get(i).get(j).get(k));
                  }
                System.out.println("]");
              }
          }
      }
  }

/**
 * This method compares each histogram to each other histogram and stores the
 * resulting values in a matrix
 */
public void makeChronotonicMatrix()
  {
    chronotonicMatrix = new double[histogram.size()][histogram.size()];
    for( int i = 0; i < histogram.size(); i++ )
      {
        for( int j = 0; j < histogram.size(); j++ )
          {
            double value = 0.0;
            for( int k = 0; k < histogram.get(i).size(); k++ )
              {
                for( int l = 0; l < histogram.get(i).get(k).size(); l++ )
                  {
                    value += Math.abs(histogram.get(i).get(k).get(l)
                            - histogram.get(j).get(k).get(l));
                  }
              }
            chronotonicMatrix[i][j] = value;
          }
      }

    if( debug )
      {
        System.out.println("## After makeChronotonicMatrix ##");
        for( int i = 0; i < histogram.size(); i++ )
          {
            for( int j = 0; j < histogram.size(); j++ )
              {
                System.out.print(originalRules.get(i) + "-vs-\n" + originalRules.get(j) + "= " + chronotonicMatrix[i][j] + "\n\n");
              }

          }
      }
  }

}