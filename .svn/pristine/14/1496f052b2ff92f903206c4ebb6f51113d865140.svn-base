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
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class attempts to boil the large number of original rules down into
 * fewer representative rules. The primary means of this is removing duplicates
 * and clustering the remaining rules.
 * 
 * 7/21/2007
 *
 * @author Brandy McMenamy and Jim Herold, Robert Keller did significant
 * debugging to get revision 936, because this just didn't work before. Also
 * took out lots of already-commented-out code.
 */

public class RepresentativeDrumRules
{

private boolean debug = false;

/**
 * This ArrayList contains Drum rules that exactly match the Improvisor style
 * specification. Ie: (drum-pattern (drum 35 X8 X8) (weight 5) )
 */

private ArrayList<String> representativeRules;

/**
 * This field contains the chronotonic values for all of the rules found in the
 * song
 */
private DrumChronotonic chronotonic;


/**
 * Any rules found to be unique will be grouped with rules that are found to be
 * chronotonicall similar. This field contains indices into the original rules
 * arraylist of all the rules which it contains.
 */

private ArrayList<Cluster> clusters;


/**
 * The String of Original Rules found in the song
 */
private ArrayList<String> originalRules;


/**
 * This ArrayList contains the indices into the original Rules ArrayList of all
 * rules that were not found to be duplicate
 */

private ArrayList<Integer> uniqueRuleIndex;


/**
 * The number of rules to be taken from the remaining unique rules is currently
 * hardcoded as a percentage.
 */
private double percentageOfClusters = 0.15;


/**
 * This field is derived from the representativeRules field and contains all the
 * drum rules obtained from the midi song in an ojbect form that can be easily
 * used by the style generator GUI
 */

private ArrayList<DrumPattern> drumPatterns;
private ArrayList<UniqueDrumPattern> uniquePatterns = new ArrayList<UniqueDrumPattern>();
private ArrayList<String> duplicates = new ArrayList<String>();
private int maxNumberOfClusters = -1;


/**
 * This the Style GUI access to makeDrumPattern
 * without generating new rules.
 */

public RepresentativeDrumRules(boolean thisIsAHack)
  {
    //do nothing!
  }

// Couldn't this constructor simply call the other??

public RepresentativeDrumRules()
  {
    try
      {
        ImportDrums im = new ImportDrums();
        if( im.canContinue )
          {
            DrumPatternExtractor dpg = new DrumPatternExtractor();
            if( dpg.canContinue )
              {
                chronotonic = new DrumChronotonic(dpg.getPatterns(), dpg.getNumberOfInstruments());
                if( debug )
                  {
                    System.out.println("## BEGINNING REPRESENTATIVE DRUM RULES ##");
                  }
                this.originalRules = dpg.getPatterns();
                if( debug )
                  {
                    System.out.println("##Original Rules##");
                    for( int i = 0; i < originalRules.size(); i++ )
                      {
                        System.out.println(originalRules.get(i) + "\n");
                      }
                  }
                processDuplicates();
                clusters = new ArrayList<Cluster>();
                findTenativeRepresentatives();
                cluster();
                findRepresentatives();
                representativeRules = new ArrayList<String>();
                createRepresentativePatterns();
                turnRepresentativesIntoObjects();
              }
          }
      }
    catch( Exception e )
      {
        ErrorLog.log(ErrorLog.SEVERE, 
                     "Sorry, there was an unknown internal error while generating "
                   + "the drum patterns.");
        e.printStackTrace();
      }
  }

public RepresentativeDrumRules(double startBeat, 
                               double endBeat, 
                               int maxNumberOfClusters, 
                               jm.music.data.Part selectedDrumPart)
  {
    this.maxNumberOfClusters = maxNumberOfClusters;
    try
      {
        ImportDrums im = new ImportDrums(startBeat, endBeat, selectedDrumPart);
        if( im.canContinue )
          {
            DrumPatternExtractor dpg = new DrumPatternExtractor();
            if( dpg.canContinue )
              {
                chronotonic = new DrumChronotonic(dpg.getPatterns(), dpg.getNumberOfInstruments());
                if( debug )
                  {
                    System.out.println("## BEGINNING REPRESENTATIVE DRUM RULES ##");
                  }
                originalRules = dpg.getPatterns();
                //representativeRules = new ArrayList<String>();
                //clusters = new ArrayList<Cluster>();
                //rulesWithoutDuplicates = new ArrayList<String>();
                //uniqueRuleIndex = new ArrayList<Integer>();
// This was the original, which wasn't working (no patterns returned).
// Just taking a stab at the code that follows, which is what is in the other
// constructor.
//                processDuplicates();
//                if( uniqueRuleIndex.size() > 0 )
//                  {
//                    findTenativeRepresentatives();
//                    cluster();
//                    findRepresentatives();
//                representativeRules = new ArrayList<String>();
//                createRepresentativePatterns();
//                  }
//                turnRepresentativesIntoObjects();
                
                processDuplicates();
                clusters = new ArrayList<Cluster>();
                findTenativeRepresentatives();
                cluster();
                findRepresentatives();
                representativeRules = new ArrayList<String>();
                createRepresentativePatterns();
                turnRepresentativesIntoObjects();
              }
          }
      }
    catch( Exception e )
      {
        ErrorLog.log(ErrorLog.SEVERE, 
                     "Sorry, there was an unknown internal error while generating "
                  + "the drum patterns.");
        e.printStackTrace();
      }
  }

public ArrayList<String> getRepresentativeRules()
  {
    return representativeRules;
  }

public ArrayList<DrumPattern> getRepresentativePatterns()
  {
    return drumPatterns;
  }

public ArrayList<Cluster> getClusters()
  {
    return clusters;
  }

public ArrayList<String> getDuplicates()
  {
    return duplicates;
  }

/**
 * This method iterates through each original rule and looks for every other
 * instance of that rule in the list. If it finds a duplicate, it creates a rule
 * from it and assigns it a weight equal to the number of times the rule was
 * found and it puts the String "Duplicate" in the ArrayList
 * rulesWithoutDuplicates.
 */

public void processDuplicates()
  {
    ArrayList<Integer> usedIndices = new ArrayList<Integer>();
    for( int i = 0; i < originalRules.size(); i++ )
      {
        if( usedIndices.contains(i) )
          {
            continue;
          }
        int multiplicity = 0;
        for( int j = 0; j < originalRules.size(); j++ )
          {
            if( originalRules.get(i).equals(originalRules.get(j)) )
              {
                multiplicity++;
                usedIndices.add(j);
              }
          }
        UniqueDrumPattern d = new UniqueDrumPattern(originalRules.get(i), i, multiplicity);
        uniquePatterns.add(d);
      }
    if( debug )
      {
        System.out.println("##After Process Duplicates##");
        System.out.println("Original Rules: " + originalRules.size());
        System.out.println("unique Rules: " + uniquePatterns.size());
        for( int i = 0; i < uniquePatterns.size(); i++ )
          {
            System.out.println(uniquePatterns.get(i));
          }
      }
  }

///**
// * @param String the rule from the ArrayList originalRules that is to be added
// * @param int the weight of the rule This method takes the two parameters and
// * puts them in a String that exactly matches the drum rule form that improvisor
// * is expecting
// */
//
//public void addRule(String rule, float weight)
//  {
//    if( debug )
//      {
//        System.out.println("Adding: " + rule);
//      }
//
//    String s = "";
//    String[] split = rule.split("\n");
//    for( int i = 0; i < split.length; i++ )
//      {
//        if( split[i].substring(split[i].indexOf(')') + 1).equals("R1 ") )
//          {
//            continue;
//          }
//        s += split[i].substring(split[i].indexOf('(') + 1, split[i].indexOf(')')) + " ";
//        s += split[i].substring(split[i].indexOf(')') + 1, split[i].length() - 1) + "\n";
//      }
//    s += String.valueOf((weight + 1));
//    if( debug )
//      {
//        System.out.println("Now represented as: " + s);
//      }
//    representativeRules.add(s);
//  }

/**
 * Each cluster is determined by an initial representative. Once these
 * representatives are chosen each rule is assigned to the cluster whos
 * representative is closest, as determined by the Chronotonic. This method
 * starts by choosing one of the unique rules by random. It then picks
 * representatives whos distance from each other representative is maximal.
 */

public void findTenativeRepresentatives()
  {
    ArrayList<Integer> selectedRules = new ArrayList<Integer>(); //Prevent a rule from being selected more than once
    int numberOfClusters = (int) (uniquePatterns.size() * percentageOfClusters);
    //Pick the first pattern randomly
    Random r = new Random();
    int randomIndex = r.nextInt(uniquePatterns.size());
    selectedRules.add(randomIndex);
    UniqueDrumPattern randomDrumPattern = uniquePatterns.get(randomIndex);
    Cluster firstCluster = new Cluster(randomDrumPattern);
    clusters.add(firstCluster);
    //Pick the remaining representatives with furthest distance from all other representatives
    for( int i = 1; i < numberOfClusters; i++ )
      {
        int maxIndex = -1;
        double maxDistance = Double.MIN_VALUE;
        for( int j = 0; j < uniquePatterns.size(); j++ )
          {
            if( selectedRules.contains(j) )
              {
                continue;
              }
            UniqueDrumPattern currentPattern = uniquePatterns.get(j);
            double distance = 0.0;
            for( int k = 0; k < clusters.size(); k++ )
              {
                Cluster currentCluster = clusters.get(k);
                distance += currentCluster.compareRepTo(currentPattern);
              }
            if( distance > maxDistance )
              {
                maxIndex = j;
                maxDistance = distance;
              }
          }
        if( maxIndex >= 0 )
          {
          selectedRules.add(maxIndex);
          UniqueDrumPattern nextRep = uniquePatterns.get(maxIndex);
          Cluster nextCluster = new Cluster(nextRep);
          clusters.add(nextCluster);
          }
      }
    if( debug )
      {
        System.out.println("##After Cluster##");
        for( int i = 0; i < clusters.size(); i++ )
          {
            System.out.println(clusters.get(i));
          }
      }
  }

/**
 * This method goes through each of teh unique rules and assigns them to the
 * cluster whose tenative representative is closest as determined by the
 * Chronotonic
 */

public void cluster()
  {
    for( int i = 0; i < uniquePatterns.size(); i++ )
      {
        UniqueDrumPattern currentPattern = uniquePatterns.get(i);
        int minIndex = -1;
        double minDistance = Double.MAX_VALUE;
        for( int j = 0; j < clusters.size(); j++ )
          {
            Cluster c = clusters.get(j);
            double distance = c.compareRepTo(currentPattern);
            if( distance < minDistance )
              {
                minIndex = j;
                minDistance = distance;
              }
          }
        Cluster c = clusters.get(minIndex);
        c.addRule(currentPattern);
      }
    if( debug )
      {
        System.out.println("##After Cluster##");
        for( int i = 0; i < clusters.size(); i++ )
          {
            System.out.println(clusters.get(i));
          }
      }
  }

/**
 * Each cluster gives one rule to the final representative rules ArrayList. Once
 * all of the rules have been put into their appropriate clusters, this method
 * finds the best representative for each cluster that will be then be put into
 * the representative rules field. The best rules is defined by the rule have
 * the minimum distance from each rule it is clustered with.
 */

public void findRepresentatives()
  {
    Random r = new Random();

    for( int i = 0; i < clusters.size(); i++ )
      {
        Cluster currentCluster = clusters.get(i);
        ArrayList<Integer> possibleReps = new ArrayList<Integer>();
        int maxMultiplicity = 0;
        for( int j = 0; j < currentCluster.size(); j++ )
          {
            UniqueDrumPattern currentPattern = currentCluster.getRule(j);
            if( currentPattern.getMultiplicity() > maxMultiplicity )
              {
                possibleReps = new ArrayList<Integer>();
                maxMultiplicity = currentPattern.getMultiplicity();
              }
            if( currentPattern.getMultiplicity() == maxMultiplicity )
              {
                possibleReps.add(j);
              }
          }
        int possibleRepIndex = r.nextInt(possibleReps.size());
        int repIndex = possibleReps.get(possibleRepIndex);
        currentCluster.setRep(repIndex);
      }
    if( debug )
      {
        System.out.println("##After Find Representatives##");
        for( int i = 0; i < clusters.size(); i++ )
          {
            System.out.println(clusters.get(i).getRep());
          }
      }
  }

/*
 * Takes the representative patterns and puts them in a text Format ready for
 * improvisor to read \ Ie: (drum-pattern (drum 35 X8 X8) (weight 5) )
 */
public void createRepresentativePatterns()
  {
    for( int i = 0; i < clusters.size(); i++ )
      {
        Cluster currentCluster = clusters.get(i);
        UniqueDrumPattern currentPattern = currentCluster.getRep();
        float weight = currentCluster.getWeight();
        String s = "(drum-pattern";
        s += currentPattern.improvisorString();
        s += "\n(weight " + weight + " )\n";
        s += ")";
        representativeRules.add(s);
      }
    if( debug )
      {
        System.out.println("##Final Rules");
        for( int i = 0; i < representativeRules.size(); i++ )
          {
            System.out.println(representativeRules.get(i));
          }
      }
  }

public class Cluster
{

private UniqueDrumPattern repPattern;
private ArrayList<UniqueDrumPattern> patterns;

public Cluster(UniqueDrumPattern d)
  {
    this.repPattern = d;
    patterns = new ArrayList<UniqueDrumPattern>();
  }

public double compareRepTo(UniqueDrumPattern d)
  {
    return d.compareTo(repPattern);
  }

public void addRule(UniqueDrumPattern d)
  {
    patterns.add(d);
  }

@Override
public String toString()
  {
    String s = "Cluster\nRep Pattern: " + repPattern + "\n";
    for( int i = 0; i < patterns.size(); i++ )
      {
        s += patterns.get(i);
      }
    return s;
  }

public int size()
  {
    return patterns.size();
  }

public UniqueDrumPattern getRule(int i)
  {
    return patterns.get(i);
  }

public UniqueDrumPattern getRep()
  {
    return repPattern;
  }

public float getWeight()
  {
    float weight = 0;
    for( int i = 0; i < patterns.size(); i++ )
      {
        weight += patterns.get(i).getMultiplicity();
      }
    weight = (float) (Math.floor((weight / originalRules.size()) * 100));
    if( weight < 1 )
      {
        return 1;
      }
    return weight;
  }

public void setRep(int i)
  {
    repPattern = patterns.get(i);
  }

public String[] getRules()
  {
    // Here is where serious problems occurred. 
    // former rulesWithoutDuplicates was always null.
    // switched to using patterns instead.
    
    // rk 29 April 2012: Also, invalid drum numbers are being
    // found, as if the incoming patterns are corrupted.
    // This seems to occur only on re-Extraction, so far.

    String[] toReturn = new String[patterns.size()];
    toReturn[0] = "";
    for( int i = 1; i < patterns.size(); i++ )
      {
        toReturn[i] = "(drum-pattern \n";

        UniqueDrumPattern pattern = patterns.get(i);

        String[] split = pattern.toString().split("\n"); // What if

        for( int j = 1; j < split.length; j++ )
          {
            int firstParensIndex = split[j].indexOf('(');
            int lastParensIndex = split[j].indexOf(')');
            String drumNumber = split[j].substring(firstParensIndex + 1, lastParensIndex);
            if( MIDIBeast.drumInstrumentNumberValid(Integer.parseInt(drumNumber)) )
              {
              String drumString = split[j].substring(lastParensIndex + 1);
              toReturn[i] += "(drum " + drumNumber + " " + drumString + ")\n";
              }
            else
              {
                System.err.println("Caution: non-GM percussion number " + drumNumber + " detected in pattern");
              }
          }
        
       //System.out.println("uniqueDrumPattern " + i + " = " + pattern + " converts to " + toReturn[i]);

      }

    return toReturn;
  }
}

/**
 * This method takes the String form of the representative rules and turns them
 * into DrumPattern objects for ease of use within improvisor.
 */
public void turnRepresentativesIntoObjects()
  {
    //System.out.println("representativeRules = " + representativeRules);
    drumPatterns = new ArrayList<DrumPattern>();
    for( int i = 0; i < representativeRules.size(); i++ )
      {
        DrumPattern drumPattern = new DrumPattern();
        String[] split = representativeRules.get(i).split("\n");
        for( int j = 1; j < split.length - 2; j++ )
          {
            DrumRule drumRule = new DrumRule();
            String[] split2 = split[j].split(" ");
            drumRule.setInstrumentNumber(Integer.parseInt(split2[1]));   
            for( int k = 2; k < split2.length - 1; k++ )
              {
                drumRule.addElement(split2[k]);
              }
            drumPattern.addRule(drumRule);
          }
        //Get Weight
        String weightString = split[split.length - 2];
        String[] split2 = weightString.split(" ");
        weightString = split2[1];
        drumPattern.setWeight(Float.parseFloat(weightString));
        drumPatterns.add(drumPattern);
      }
    if( debug )
      {
        System.out.println("## After turnRepresentativesIntoObjects ##");
        for( int i = 0; i < drumPatterns.size(); i++ )
          {
            System.out.println(drumPatterns.get(i));
          }
      }
  }

/**
 * This method returns and array list of each original rule in DrumPattern form,
 * in case the user wants each rule from the song as opposed to representative
 * ones
 */
public ArrayList<DrumPattern> getUnfilteredRules()
  {
    ArrayList<DrumPattern> unfilteredRules = new ArrayList<DrumPattern>();
    for( int i = 0; i < originalRules.size(); i++ )
      {
        DrumPattern temp = new DrumPattern();
        temp.setWeight(1);
        String[] individualRule = originalRules.get(i).split("\n");
        for( int j = 0; j < individualRule.length; j++ )
          {
            DrumRule tempDrumRule = new DrumRule();
            tempDrumRule.setInstrumentNumber(Integer.parseInt(individualRule[j].substring(1, 3)));
            String[] individualElement = individualRule[j].substring(4).split(" ");
            for( int k = 0; k < individualElement.length; k++ )
              {
                tempDrumRule.addElement(individualElement[k]);
              }
            temp.addRule(tempDrumRule);
          }
        unfilteredRules.add(temp);
      }
    if( debug )
      {
        System.out.println("## After getUnfilteredRules ##");
        for( int i = 0; i < unfilteredRules.size(); i++ )
          {
            System.out.println(unfilteredRules.get(i));
          }
      }

    return unfilteredRules;
  }

public DrumPattern makeDrumPattern()
  {
    return new DrumPattern();
  }

public DrumRule makeDrumRule()
  {
    return new DrumRule();
  }

public class UniqueDrumPattern
{

private ArrayList<UniqueDrumRule> drumRules;
private int multiplicity;
private int index;

public UniqueDrumPattern(String s, int index, int multiplicity)
  {
    this.index = index;
    this.multiplicity = multiplicity;
    drumRules = new ArrayList<UniqueDrumRule>();
    String[] a = s.split("\n");
    for( int i = 0; i < a.length; i++ )
      {
        int parensIndex = a[i].indexOf(")");
        String instNumberString = a[i].substring(1, parensIndex);
        int instNumber = Integer.parseInt(instNumberString);
        String rule = a[i].substring(parensIndex + 1, a[i].length());
        UniqueDrumRule r = new UniqueDrumRule(instNumber, rule);
        drumRules.add(r);
      }
  }

public int size()
  {
    return drumRules.size();
  }

public int getIndex()
  {
    return index;
  }

public int getMultiplicity()
  {
    return multiplicity;
  }

public double compareTo(UniqueDrumPattern d)
  {
    return chronotonic.getChronValueAt(index, d.getIndex());
  }

/*
 * (drum-pattern (drum 35 X8 X8) (weight 5) )
 */
public String improvisorString()
  {
    String s = "";
    for( int i = 0; i < drumRules.size(); i++ )
      {
        UniqueDrumRule currentRule = drumRules.get(i);
        if( currentRule.isRest() )
          {
            continue;
          }
        s += "\n(drum " + currentRule.getInstNumber() + " " + currentRule.getRule() + ")";
      }
    return s;
  }

@Override
public String toString()
  {
    String s = "Drum Pattern Index: " + index + " Multiplicity: " + multiplicity + "\n";
    for( int i = 0; i < this.size(); i++ )
      {
        s += drumRules.get(i).toString() + "\n";
      }
    return s;
  }

}

public class UniqueDrumRule
{

int instrumentNumber;
String rule;

public UniqueDrumRule(int i, String r)
  {
    instrumentNumber = i;
    rule = r;
  }

public String getRule()
  {
    return rule;
  }

public int getInstNumber()
  {
    return instrumentNumber;
  }

public boolean isRest()
  {
    return rule.trim().equals("R1");
  }

@Override
public String toString()
  {
    return "(" + instrumentNumber + ")" + rule;
  }

}

public class DrumPattern implements RepPattern
{

private ArrayList<DrumRule> drumRules;
private float weight;
private String patternName;

public DrumPattern()
  {
    drumRules = new ArrayList<DrumRule>();
  }

public void setWeight(float w)
  {
    weight = w;
  }

public float getWeight()
  {
    return weight;
  }

public void addRule(DrumRule r)
  {
    drumRules.add(r);
  }

public DrumRule getRule(int index)
  {
    return drumRules.get(index);
  }

public int getDuration()
  {
    return MIDIBeast.drumMeasureSize;
  }

public String getName()
{
    return patternName;
}

public void setName(String n)
{
    patternName = n;
}

@Override
public String toString()
  {
    String s = "(drum-pattern (pattern-name " + patternName + ")\n";
    for( int i = 0; i < drumRules.size(); i++ )
      {
        s += drumRules.get(i) + " ";
      }
    s += " (weight " + weight + ")\n";
    return s + ")\n";
  }

public String getDisplayRuleAt(int index)
  {
    return drumRules.get(index).getDisplayRule();
  }

public ArrayList<DrumRule> getRules()
  {
    return drumRules;
  }

public int size()
  {
    return drumRules.size();
  }

}

public class DrumRule
{

int drumInstrumentNumber = 35; //default
ArrayList<String> elements = new ArrayList<String>();
String ruleName;

public DrumRule()
  {
    elements = new ArrayList<String>();
  }

public void setInstrumentNumber(int i)
  {
    drumInstrumentNumber = i;
  }

public int getInstrumentNumber()
  {
    return drumInstrumentNumber;
  }

public void addElement(String s)
  {
    elements.add(s);
  }

public String getElement(int i)
  {
    return elements.get(i);
  }

public String getName()
{
    return ruleName;
}

public void setName(String name)
{
    ruleName = name;
}

@Override
public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("drum ");
    buffer.append(drumInstrumentNumber);
    buffer.append(" (name ");
    buffer.append(ruleName);
    buffer.append(")(rules ");
    for( int i = 0; i < elements.size(); i++ )
      {
        buffer.append(elements.get(i));
        buffer.append(" ");
      }
    buffer.append(")\n");
    String s = buffer.toString();
    return s;
  }

public String getDisplayRule()
  {
    String s = "";
    for( int i = 0; i < elements.size(); i++ )
      {
        s += elements.get(i) + " ";
      }
    return s.trim();
  }

}
}