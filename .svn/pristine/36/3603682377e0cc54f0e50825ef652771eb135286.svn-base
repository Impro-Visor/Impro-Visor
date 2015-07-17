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

import imp.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Modeled after the RepresentativeBassRules class.
 *
 * July 2007 - Sayuri Soejima
 * Reformatted by Robert Keller, 10 April 2012
 */
public class RepresentativeChordRules implements Constants
{

private boolean debug = false;
private double percentageOfClusters = 0.25;
private int numberOfUniqueRules;
private ChordChronotonic c;
private ChordPatternExtractor b;
private ArrayList<String> simplifiedPitchesRules;
private ArrayList<String> sansDuplicatesRules;
private ArrayList<Section> sections;
private ArrayList<ChordPattern> ChordPatternGenerator;
private String midiFileName;
private String chordFileName;
private ArrayList<String> duplicates = new ArrayList<String>();
private ArrayList<RawRule> uniqueRules = new ArrayList<RawRule>();
private int numberOfRules = 0;
private int maxNumberOfClusters;
private int minDuration;

public ArrayList<ChordPattern> getChordPattern()
  {
    return ChordPatternGenerator;
  }

public ArrayList<Section> getSections()
  {
    return sections;
  }

public ArrayList<ChordPattern> getChordRules()
  {
    return ChordPatternGenerator;
  }


/**
 * This gives the Style GUI access to makeChordPattern
 * without generating new rules.
 */
public RepresentativeChordRules(boolean thisIsAHack, int minDuration)
  {
    this.minDuration = minDuration;
  }

/**
 * Constructor!
 *
 * @param midiFileName - the name of the midi file to be read
 * @param chordFileName - the name of the leadsheet file to be read
 * @throws Exception
 */
public RepresentativeChordRules(int minDuration)
  {
    this.minDuration = minDuration;
    try
      {
        ImportChords im = new ImportChords();
        if( im.canContinue == true )
          {
            this.midiFileName = MIDIBeast.midiFileName;
            this.chordFileName = MIDIBeast.chordFileName;
            b = new ChordPatternExtractor(minDuration);
            if( b.canContinue == true )
              {
                c = new ChordChronotonic(b);
                initialize();
                if( debug )
                  {
                    System.out.println("\n## Initial ##");
                    for( int i = 0; i < c.getOriginalRules().size(); i++ )
                      {
                        System.out.println(c.getOriginalRules().get(i));
                      }
                  }

                simplifyRulePitches();
                if( debug )
                  {
                    System.out.println("\n## After simplifyRulePitches() ##");
                    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
                      {
                        System.out.println(simplifiedPitchesRules.get(i));
                      }
                  }
                if( MIDIBeast.getMaxChordPatternLengthInSlots() != 0 )
                  {
                    truncateChordPatterns();
                  }
                
                processDuplicateRules();
                if( debug )
                  {
                    System.out.println("\n## After processDuplicateRules() ##");
                    for( int i = 0; i < uniqueRules.size(); i++ )
                      {
                        System.out.println(uniqueRules.get(i));
                      }
                  }

                splitUpIntoSections();
                if( debug )
                  {
                    System.out.println("\n## After splitUpIntoSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        Section s = sections.get(i);
                        System.out.println("Rules of Section: " + i);
                        for( int j = 0; j < s.size(); j++ )
                          {
                            System.out.println(s.getRule(j));
                          }
                      }
                  }

                pruneSections();

                if( debug )
                  {
                    System.out.println("\n## After pruneSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                      }
                  }

                findTentativeRepresentatives();
                if( debug )
                  {
                    System.out.println("\n## After findTentativeRepresenatives() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Representative rules for section: " + i);
                        for( int j = 0; j < sections.get(i).getClusters().size(); j++ )
                          {
                            System.out.println(sections.get(i).getCluster(j).getRepRule());
                          }
                      }
                  }
                cluster();
                if( debug )
                  {
                    System.out.println("\n## After cluster() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Section: " + i);
                        for( int j = 0; j < sections.get(i).getNumberOfClusters(); j++ )
                          {
                            System.out.println("\tCluster: " + j);
                            for( int k = 0; k < sections.get(i).getCluster(j).size(); k++ )
                              {
                                System.out.println("\t\t" + sections.get(i).getCluster(j).getRule(k));
                              }
                          }
                      }
                  }

                getRepresentativeRules();
                if( debug )
                  {
                    System.out.println(ChordPatternGenerator.size());
                    System.out.println("\n\n### Resulting Patterns ###");
                    for( int i = 0; i < ChordPatternGenerator.size(); i++ )
                      {
                        System.out.println(ChordPatternGenerator.get(i));
                      }
                  }
              }
          }
      }
    catch( Exception e )
      {
        e.printStackTrace();
        MIDIBeast.addError("Sorry, there was an unknown internal error while generating "
                + "the chord patterns.");
      }
  }

public RepresentativeChordRules(double startBeat, 
                                double endBeat, 
                                int maxNumberOfClusters, 
                                jm.music.data.Part selectedPart, 
                                int minDuration)
  {
    this.maxNumberOfClusters = maxNumberOfClusters;
    this.minDuration = minDuration;
    try
      {
        if( maxNumberOfClusters != 0 )
          {
            this.maxNumberOfClusters = maxNumberOfClusters;
          }
        ImportChords im = new ImportChords(startBeat, endBeat, selectedPart);
        if( im.canContinue )
          {
            ChordPatternExtractor cpg = new ChordPatternExtractor(startBeat, endBeat, minDuration);
            if( cpg.canContinue )
              {
                c = new ChordChronotonic(cpg);
                initialize();

                if( debug )
                  {
                    System.out.println("\n## Initial ##");
                    for( int i = 0; i < MIDIBeast.originalChordRules.size(); i++ )
                      {
                        System.out.println(MIDIBeast.originalChordRules.get(i));
                      }
                  }

                simplifyRulePitches();

                if( debug )
                  {
                    System.out.println("\n##After simplifyRulePitches() ##");
                    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
                      {
                        System.out.println(simplifiedPitchesRules.get(i));
                      }
                  }

               // Apparently this was omitted from the original version. rk 29 April 2012
               if( MIDIBeast.getMaxChordPatternLengthInSlots() != 0 )
                  {
                    truncateChordPatterns();
                  }
                
                processDuplicateRules();

                if( debug )
                  {
                    System.out.println("\n##After processDuplicateRules() ##");
                    for( int i = 0; i < sansDuplicatesRules.size(); i++ )
                      {
                        System.out.println(sansDuplicatesRules.get(i));
                      }
                    System.out.println("Resulting Patterns");
                    for( int i = 0; i < ChordPatternGenerator.size(); i++ )
                      {
                        System.out.println("\t" + ChordPatternGenerator.get(i));
                      }
                  }

                splitUpIntoSections();

                if( debug )
                  {
                    System.out.println("\n##After splitUpIntoSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Section " + i);
                        for( int j = 0; j < sections.get(i).size(); j++ )
                          {
                            System.out.println(sections.get(i).getRule(j));
                          }
                      }
                  }

                pruneSections();

                if( debug )
                  {
                    System.out.println("\n##After pruneSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                      }
                  }

                findTentativeRepresentatives();

                if( debug )
                  {
                    System.out.println("\n##After findTenativeRepresentatives() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Representative rules for section: " + i);
                        for( int j = 0; j < sections.get(i).getClusters().size(); j++ )
                          {
                            System.out.println(sansDuplicatesRules.get(sections.get(i).getClusters().get(j).getRepIndex()));
                          }
                      }
                  }

                cluster();

                if( debug )
                  {
                    System.out.println("\n##After cluster() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Section: " + i);
                        for( int j = 0; j < sections.get(i).getNumberOfClusters(); j++ )
                          {
                            System.out.println("\tCluster: " + j);
                            for( int k = 0; k < sections.get(i).getClusters().get(j).getRules().size(); k++ )
                              {
                                System.out.println("\t\t" + sections.get(i).getCluster(j).getRule(k));
                              }
                          }
                      }
                  }

                getRepresentativeRules();

                if( debug )
                  {
                    System.out.println("\n\n## Resulting Patterns ##");
                    for( int i = 0; i < ChordPatternGenerator.size(); i++ )
                      {
                        System.out.println(ChordPatternGenerator.get(i));
                      }
                  }
              }
          }
        if( debug )
          {
            System.out.println("\n## Errors that occured during chord generation ##");
            ArrayList<String> err = MIDIBeast.errors;
            for( int i = 0; i < err.size(); i++ )
              {
                System.out.println(i + 1 + ": " + err.get(i));
              }
          }
      }
    catch( Exception e )
      {
        e.printStackTrace();
        MIDIBeast.addError("Sorry there was an unknown internal error while generating the chord patterns.");
      }
  }

/**
 * This method simply initializes the field members of the class and sets number
 * of rules to the desired fraction of the original set of rules
 */

public void initialize()
  {
    numberOfUniqueRules = (int) (percentageOfClusters * MIDIBeast.originalChordRules.size());
    simplifiedPitchesRules = new ArrayList<String>();
    sansDuplicatesRules = new ArrayList<String>();
    sections = new ArrayList<Section>();
    ChordPatternGenerator = new ArrayList<ChordPattern>();
  }

/**
 * This method iterates through the original set of rules and simplifies the
 * pitch info contained by calling on the simplifyPitchInfo method.
 */
public void simplifyRulePitches()
        
  {
    for( int i = 0; i < c.getOriginalRules().size(); i++ )
      {
        simplifiedPitchesRules.add(simplifyPitchInfo(c.getOriginalRules().get(i)));
      }
  }

public void truncateChordPatterns()
  {
    ArrayList<String> tempRules = new ArrayList<String>();
    int maxSlotLength = MIDIBeast.getMaxChordPatternLengthInSlots();
    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
      {
        String currentRule = simplifiedPitchesRules.get(i);
        if( MIDIBeast.numBeatsInRule(currentRule) > maxSlotLength )
          {
            String[] split = currentRule.split(" ");
            String temp = "";
            int duration = 0;
            for( int j = 0; j < split.length; j++ )
              {
                duration += MIDIBeast.numBeatsInRule(split[j]);
                if( duration < maxSlotLength )
                  {
                    temp += split[j] + " ";
                  }
                if( duration == maxSlotLength )
                  {
                    tempRules.add(temp + split[j]);
                    temp = "";
                    duration = 0;
                  }
                if( duration > maxSlotLength )
                  {
                    String type = Character.toString(split[j].charAt(0));
                    int slotLength = (int) (MIDIBeast.numBeatsInRule(split[j]) - (duration - maxSlotLength));
                    int slotLength2 = (int) (MIDIBeast.numBeatsInRule(split[j]) - slotLength);
                    String length = MIDIBeast.stringDuration(slotLength);
                    String length2 = MIDIBeast.stringDuration(slotLength2);
                    temp += type + length;
                    tempRules.add(temp);
                    temp = type + length2 + " ";
                    duration = slotLength2;
                    while( duration >= maxSlotLength )
                      {
                        tempRules.add(type + MIDIBeast.stringDuration(maxSlotLength));
                        int slotLength3 = duration - maxSlotLength;
                        duration = slotLength3;
                        if( duration == 0 )
                          {
                            temp = "";
                          }
                        else
                          {
                            String length3 = MIDIBeast.stringDuration(slotLength3);
                            temp = type + length3 + " ";
                          }
                      }
                  }
              }
          }
        else
          {
            tempRules.add(currentRule);
          }
      }
    simplifiedPitchesRules = tempRules;
    if( debug )
      {
        System.out.println("####After Truncate Chord Patterns####");
        for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
          {
            System.out.println(simplifiedPitchesRules.get(i));
          }
      }
  }

public ArrayList<String> getDuplicates()
  {
    return duplicates;
  }


/**
 * This method is called on each rule by simplifyRulePitches() and handles the
 * logic involved in simplifying the pitch info
 *
 * @param String - The pattern element that is to be simplified
 * @return String - The simplified string
 */

public String simplifyPitchInfo(String s)
  {
    s = s.substring(1, s.length() - 1); //Remove Parens
    String[] ruleElements = s.split(" ");
    String returnString = "";
    for( int i = 0; i < ruleElements.length; i++ )
      {
        int rhythmIndex = 0;
        for( int j = 0; j < ruleElements[i].length(); j++ )
          {
            if( ruleElements[i].charAt(j) == '(' )
              {
                rhythmIndex = j + 3;
                break;
              }
            if( ruleElements[i].charAt(j) > 47 && ruleElements[i].charAt(j) < 58 )
              {
                rhythmIndex = j;
                break;
              }
          }
        int xIndex = ruleElements[i].indexOf('X');
        if( ruleElements[i].indexOf('X') != -1 )
          {
            returnString += "X" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
          }
        else if( ruleElements[i].indexOf('R') != -1 )
          {
            returnString += "R" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
          }
        else if( xIndex != -1 )
          {
            returnString += ruleElements[i].substring(xIndex, xIndex + 4) + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
          }
      }
    return returnString;
  }


/**
 * This method searches through the set of rules with simplified pitch
 * information, removes all repeats, and creates a chord pattern for each set of
 * repeats by assigning that pattern a weight equal to the number of repeats
 * corresponding to that pattern
 */

public void processDuplicateRules()
  {
    ArrayList<Integer> repeats = new ArrayList<Integer>();

    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
      {

        if( repeats.contains(i) )
          {
            continue;
          }

        String rule = simplifiedPitchesRules.get(i);
        int multiplicity = 0;

        for( int j = 0; j < simplifiedPitchesRules.size(); j++ )
          {
            if( rule.trim().equals(simplifiedPitchesRules.get(j).trim()) )
              {
                repeats.add(j);
                multiplicity++;
              }
          }
        uniqueRules.add(new RawRule(rule, multiplicity, i));
        numberOfRules += multiplicity;
      }
  }


/**
 * When it comes time to cluster, it would be disadvantageous to have patterns
 * of different beat durations in the same cluster (eg (B8 B8) should not be in
 * the same group as (B4 B4)). This method will assign each of the non duplicate
 * patterns to a section which contains only patterns of equal beat duration.
 */

public void splitUpIntoSections()
  {
    ArrayList<Integer> usedSlotCounts = new ArrayList<Integer>();
    for( int i = 0; i < uniqueRules.size(); i++ )
      {
        RawRule currentRule = uniqueRules.get(i);

        if( usedSlotCounts.contains(currentRule.getSlots()) )
          {
            for( int j = 0; j < sections.size(); j++ )
              {
                if( sections.get(j).getSlotCount() == currentRule.getSlots() )
                  {
                    sections.get(j).addRule(uniqueRules.get(i));
                  }
              }
          }
        else
          {
            Section section = new Section(currentRule.getSlots(), uniqueRules.get(i));
            sections.add(section);
            usedSlotCounts.add(section.getSlotCount());
          }
      }
  }

/**
 * This function is used in splitUpIntoSections() and calculates the beat
 * duration of a given pattern.
 *
 * @param String - the pattern whose beat duration is to be calculated 
 * (eg (B4 B8 B8))
 * @return Double - the duration of the given pattern (eg 2.0)
 */

public Double calculateBeats(String s)
  {
    if( debug )
      {
        System.out.println("Caclculating Beat of: " + s);
      }
    String[] split = s.split(" ");
    int rhythmIndex = 0;
    double beats = 0.0;
    double slots = 0.0;
    for( int i = 0; i < split.length; i++ )
      {
        if( split[i].indexOf('(') != -1 )
          {
            rhythmIndex = split[i].indexOf('(') + 3;
          }
        else
          {
            rhythmIndex = 1;
          }
        String rhythm = split[i].substring(rhythmIndex, split[i].length());
        String[] rhythmArray = rhythm.split("\\+");
        for( int j = 0; j < rhythmArray.length; j++ )
          {
            if( debug )
              {
                System.out.println("\t" + rhythmArray[j] + " -> " + MIDIBeast.getSlotValueFor(rhythmArray[j]));
              }
            slots += MIDIBeast.getSlotValueFor(rhythmArray[j]);
          }
      }
    return slots / BEAT;
  }

/**
 * This method iterates through each section and determines how many clusters it
 * is to have. The number of clusters is determined by finding the number of
 * rules the section contains and setting that as a fraction to how many total
 * patterns there are, and then multiplying that fraction by how many rules are
 * requested as determined in the initialize() method
 */

public void pruneSections()
  {
    for( int i = 0; i < sections.size(); i++ )
      {
        int numClusters = (int) Math.round(numberOfUniqueRules * ((sections.get(i).size() * 1.0) / MIDIBeast.originalChordRules.size()));
        if( numClusters == 0 )
          {
            numClusters = 1;
          }
        sections.get(i).setNumClustersAllowed(numClusters);
      }
  }

/**
 * This method finds the tentative representatives for the clusters of each
 * section. One representative is found for each of the clusters that a sections
 * is supposed to have. The first representative is picked non
 * deterministically, and all proceeding representatives are chosen by finding
 * the pattern with the greatest distance to all other representatives
 */

public void findTentativeRepresentatives()
  {
    for( int i = 0; i < sections.size(); i++ )
      {
        Section s = sections.get(i);
        ArrayList<Integer> usedRules = new ArrayList<Integer>();
        Random r = new Random();
        int randomRuleIndex = r.nextInt(s.size());
        RawRule randomRule = s.getRule(randomRuleIndex);
        s.addCluster(randomRule);
        usedRules.add(randomRuleIndex);
        for( int j = 1; j < s.getNumberOfClusters(); j++ )
          {
            double maxDistance = Double.MIN_VALUE;
            int maxIndex = -1;
            for( int k = 0; k < s.size(); k++ )
              {
                if( !usedRules.contains(k) )
                  {
                    RawRule currentRule = s.getRule(k);
                    double distance = 0.0;
                    for( int l = 0; l < usedRules.size(); l++ )
                      {
                        RawRule repRule = s.getRule(usedRules.get(l));
                        distance += currentRule.compareTo(repRule);
                      }
                    if( distance > maxDistance )
                      {
                        maxDistance = distance;
                        maxIndex = k;
                      }
                  }
              }
            if( maxIndex > -1 )
              {
              s.addCluster(s.getRule(maxIndex));
              usedRules.add(maxIndex);
              }
          }
      }
  }

/**
 * This method iterates through each rule of a section and assigns it to a
 * cluster whose representative rule is the closest.
 */

public void cluster()
  {
    for( int i = 0; i < sections.size(); i++ )
      {
        Section s = sections.get(i);
        for( int j = 0; j < s.size(); j++ )
          {
            RawRule currentRule = s.getRule(j);
            int minIndex = -1;
            double minDistance = Double.MAX_VALUE;
            for( int k = 0; k < s.getNumberOfClusters(); k++ )
              {
                Cluster currentCluster = s.getCluster(k);
                double distance = currentRule.compareTo(currentCluster.getRepRule());
                if( distance < minDistance )
                  {
                    minDistance = distance;
                    minIndex = k;
                  }
              }
            s.getCluster(minIndex).addRule(currentRule);
          }
      }
  }

/**
 * This method goes through each cluster and finds the rule whose distance is
 * least from all the other rules. This rule is said to be the best
 * representative of the cluster and is added as a chord pattern with a weight
 * equal to the number of rules in its cluster, or the number of rules that it
 * 'represents'
 */

public void getRepresentativeRules()
  {
    for( int i = 0; i < sections.size(); i++ )
      {
        Section s = sections.get(i);
        for( int j = 0; j < s.getNumberOfClusters(); j++ )
          {
            Cluster c = s.getCluster(j);
            int maxWeight = Integer.MIN_VALUE;
            ArrayList<RawRule> potentialRepList = new ArrayList<RawRule>();
            for( int k = 0; k < c.size(); k++ )
              {
                RawRule r = c.getRule(k);
                if( r.getWeight() > maxWeight )
                  {
                    potentialRepList = new ArrayList<RawRule>();
                  }
                if( r.getWeight() >= maxWeight )
                  {
                    potentialRepList.add(r);
                  }
              }
            Random r = new Random();
            int randomIndex = r.nextInt(potentialRepList.size());
            RawRule selectedRule = potentialRepList.get(randomIndex);
            ChordPatternGenerator.add(new ChordPattern(selectedRule.getRule(), c.calculateWeight(), ""));
          }
      }
  }

public ChordPattern makeChordPattern(String r, float w)
  {
    return new ChordPattern(r, w, "");
  }

public ChordPattern makeChordPattern(String r, float w, String n)
{
    return new ChordPattern(r, w, "", n);
}

public ChordPattern makeChordPattern(String r, float w, String p, String n)
{
    return new ChordPattern(r, w, p, n);
}

public ChordPattern makeChordPattern(imp.data.ChordPattern cp)
  {
    return new ChordPattern(cp.forGenerator(), cp.getWeight(), cp.getPushString(), cp.getName());
  }

/**
 * ChordPattern class
 */

public class ChordPattern implements RepPattern
{

String rule;
float weight;
int duration;
String push;
String patternName;

/**
 * @param r - rule
 * @param w - weight
 */
public ChordPattern(String r, float w, String push)
  {
    rule = r;
    weight = w;
    duration = new Double(MIDIBeast.numBeatsInRule(rule)).intValue();
    this.push = push;
    patternName = "";
  }

public ChordPattern(String r, float w, String push, String name)
{
    rule = r;
    weight = w;
    duration = new Double(MIDIBeast.numBeatsInRule(rule)).intValue();
    this.push = push;
    patternName = name;
}

/**
 * Allows for the printing of the chord-pattern in the format that Impro-Visor
 * understands.
 */

@Override
public String toString()
  {
    return "(chord-pattern (name " + patternName + ")(rules " + rule + ")(weight " + weight + ")(push " + push + ")";
  }

public String getRule()
  {
    return rule;
  }

public float getWeight()
  {
    return weight;
  }

public int getDuration()
  {
    return duration;
  }

public String getPush()
  {
    return push;
  }

public String getName()
{
    return patternName;
}

}

public class RawRule
{

private String rule;
private float weight;
private int slots;
private int index;

public RawRule(String rule, float weight, int index)
  {
    this.rule = rule;
    this.weight = weight;
    this.index = index;

    double beats = MIDIBeast.numBeatsInRule(rule);
    slots = MIDIBeast.doubleValToSlots(beats);
  }

public String getRule()
  {
    return rule;
  }

public float getWeight()
  {
    return weight;
  }

public int getSlots()
  {
    return slots;
  }

public int getIndex()
  {
    return index;
  }

public double compareTo(RawRule that)
  {
    ArrayList<Double> rhythm1;
    ArrayList<Double> rhythm2;
    ArrayList<String> rule1 = new ArrayList<String>();
    ArrayList<String> rule2 = new ArrayList<String>();
    String[] split = this.rule.split(" ");
    rule1.addAll(Arrays.asList(split));
    split = that.getRule().split(" ");
    rule2.addAll(Arrays.asList(split));
    rhythm1 = c.toHistoArray(rule1);
    rhythm2 = c.toHistoArray(rule2);
    return c.chronoCompare(rhythm1, rhythm2);
  }

//For testing purposes as of now
@Override
public String toString()
  {
    return "\nRaw Rule\n\t-rule " + rule
            + "\n\t-weight: " + weight
            + "\n\t-slots: " + slots
            + "\n\t-index: " + index;
  }

}

/**
 * Section class
 */

public class Section
{

ArrayList<Cluster> clusters;
ArrayList<RawRule> rules;
int slotCount;
int numClustersAllowed;

public Section()
  {
  }

public Section(int slotCount, RawRule rule)
  {
    rules = new ArrayList<RawRule>();
    clusters = new ArrayList<Cluster>();
    this.slotCount = slotCount;
    addRule(rule);
  }

public int getSlotCount()
  {
    return slotCount;
  }

public ArrayList<Cluster> getClusters()
  {
    return clusters;
  }

public ArrayList<RawRule> getRules()
  {
    return rules;
  }

public Cluster getCluster(int i)
  {
    return clusters.get(i);
  }

public RawRule getRule(int i)
  {
    return rules.get(i);
  }

public int getNumberOfClusters()
  {
    return numClustersAllowed;
  }

public void addRule(RawRule i)
  {
    rules.add(i);
  }

public void setNumClustersAllowed(int i)
  {
    numClustersAllowed = i;
    if( maxNumberOfClusters > 0 && numClustersAllowed > maxNumberOfClusters )
      {
        numClustersAllowed = maxNumberOfClusters;
      }
  }

public int size()
  {
    return rules.size();
  }

public void addCluster(RawRule repRule)
  {
    clusters.add(new Cluster(repRule));
  }

public void addRuleToCluster(RawRule newRule, int repIndex)
  {
    for( int i = 0; i < clusters.size(); i++ )
      {
        if( clusters.get(i).getRepIndex() == repIndex )
          {
            clusters.get(i).addRule(newRule);
          }
      }
  }

}

/**
 * Cluster class
 */

public class Cluster
{

private RawRule repRule;
private ArrayList<RawRule> rules = new ArrayList<RawRule>();

public Cluster()
  {
  }

public Cluster(RawRule initialRule)
  {
    rules.add(initialRule);
    repRule = initialRule;
  }

public RawRule getRule(int i)
  {
    return rules.get(i);
  }

public String getStringRule(int i)
  {
    return rules.get(i).getRule();
  }

public void setBetterRep(RawRule repRule)
  {
    this.repRule = repRule;
  }

public ArrayList<RawRule> getRules()
  {
    return rules;
  }

public RawRule getRepRule()
  {
    return repRule;
  }

public int getRepIndex()
  {
    return repRule.getIndex();
  }

public void addRule(RawRule rule)
  {
    rules.add(rule);
  }

public int size()
  {
    return rules.size();
  }

public double compare(int i, int j)
  {
    return rules.get(i).compareTo(rules.get(j));
  }

public float calculateWeight()
  {
    float weight = 0;
    for( int i = 0; i < rules.size(); i++ )
      {
        weight += rules.get(i).getWeight();
      }
    weight = (float) (Math.floor(((weight / numberOfRules) * 100) / 2));
    if( weight < 1 )
      {
        return 1;
      }
    return weight;

  }

@Override
public String toString()
  {
    String s = "Rep Rule: ";
    if( repRule != null )
      {
        s += repRule.getRule();
      }
    s += "\n";
    RawRule r = rules.get(0);
    for( int i = 0; i < rules.size(); r = rules.get(i++) )
      {
        s += "\t" + r.getRule() + "\n";
      }
    return s;
  }

}
}