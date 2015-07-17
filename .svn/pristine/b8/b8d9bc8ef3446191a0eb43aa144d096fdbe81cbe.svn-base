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
import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import polya.Polylist;

/**
 * This class is used to take a Drum Part object as obtained from the Import
 * class, and create a set of Patterns that can be used in the
 * RepresentativeDrumRules class.
 * 
 * Brandy McMenamy and James Thomas Herold 7/18/2007
 * Robert Keller refactored and reimplemented some methods, 4/10/2012
 */

public class RepresentativeBassRules implements Constants
{

private boolean debug = false;
/**
 * The number of rules to be taken from the remaining unique rules is currently
 * hardcoded as a percentage.
 */
private double percentageOfClusters = 0.25;
private int maxNumberOfClusters = -1;
private int numberOfUniqueRules;
private int numberOfRules = 0; //Used in normalizing weights 0-100
private BassChronotonic c;

/**
 * The original rules with only one pitch value per note according to the "most
 * useful" value ex: BNA8 stored as B8??
 */

private ArrayList<String> simplifiedPitchesRules = new ArrayList<String>();

/**
 * Every rule in this array list is unique
 */

private ArrayList<String> sansDuplicatesRules = new ArrayList<String>();

/**
 * Each Section contains rules of the same length, one Section per every unique
 * length
 */

private ArrayList<Section> sections = new ArrayList<Section>();


/**
 * The final array accessed by the StyleGenerator GUI
 */

private ArrayList<BassPattern> bassPatterns = new ArrayList<BassPattern>();

private ArrayList<RawRule> uniqueRules = new ArrayList<RawRule>();

/**
 * This gives the Style GUI access to
 * makeBassPatternObj without generating new rules.
 */

public RepresentativeBassRules(boolean thisIsAHack)
  {
    //do nothing!
  }

public ArrayList<String> getSimplifiedPitchesRules()
  {
    return simplifiedPitchesRules;
  }

public RepresentativeBassRules()
  {
    try
      {
        ImportBass im = new ImportBass();
        if( im.canContinue == true )
          {
            BassPatternExtractor b = new BassPatternExtractor();
            if( b.canContinue == true )
              {
                c = new BassChronotonic();

                initialize();

                if( debug )
                  {
                    System.out.println("\n## Initial ##");
                    for( int i = 0; i < MIDIBeast.originalBassRules.size(); i++ )
                      {
                        System.out.println(MIDIBeast.originalBassRules.get(i));
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

                if( MIDIBeast.getMaxBassPatternLengthInSlots() != 0 )
                  {
                    truncateBassPatterns();
                  }

                processDuplicateRules();
                if( debug )
                  {
                    System.out.println("\n## After processDuplicateRules() ##");
                    for( int i = 0; i < sansDuplicatesRules.size(); i++ )
                      {
                        System.out.println(sansDuplicatesRules.get(i));
                      }
                    System.out.println("Resulting patterns");
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
                    System.out.println("\n## After pruneSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                      }
                  }

                findTenativeRepresentatives();
                if( debug )
                  {
                    System.out.println("\n## After findTenativeRepresenatives() ##");
                    System.out.println("sections.size(): " + sections.size());
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Representative rules for section: " + i);
                        for( int j = 0; j < sections.get(i).getClusters().size(); j++ )
                          {
                            System.out.println(sections.get(i).getCluster(j));
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
                    System.out.println("\n\n### Resulting Patterns ###");
                    for( int i = 0; i < bassPatterns.size(); i++ )
                      {
                        System.out.println(bassPatterns.get(i));
                      }
                  }
              }
          }

        if( debug )
          {
            System.out.println("\n### Errors that occurred during bass generation: ###");
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
        MIDIBeast.addError("Sorry, there was an unknown internal error while generating "
                + "the bass patterns.");
      }
  }

public RepresentativeBassRules(double startBeat, 
                               double endBeat, 
                               int maxNumberOfClusters, 
                               jm.music.data.Part selectedPart)
  {
    
    try
      {
        if( maxNumberOfClusters != 0 )
          {
            this.maxNumberOfClusters = maxNumberOfClusters;
          }
        ImportBass im = new ImportBass(startBeat, endBeat, selectedPart);
        if( im.canContinue == true )
          {
            BassPatternExtractor b = new BassPatternExtractor(startBeat, endBeat);
            if( b.canContinue == true )
              {
                c = new BassChronotonic();

                initialize();

                if( debug )
                  {
                    System.out.println("\n## Initial ##");
                    for( int i = 0; i < MIDIBeast.originalBassRules.size(); i++ )
                      {
                        System.out.println(MIDIBeast.originalBassRules.get(i));
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

                // This was left out of the original version. rk 29 Apri 2012
                if( MIDIBeast.getMaxBassPatternLengthInSlots() != 0 )
                  {
                    truncateBassPatterns();
                  }

                processDuplicateRules();
                if( debug )
                  {
                    System.out.println("\n## After processDuplicateRules() ##");
                    for( int i = 0; i < sansDuplicatesRules.size(); i++ )
                      {
                        System.out.println(sansDuplicatesRules.get(i));
                      }
                    System.out.println("Resulting patterns");
                    for( int i = 0; i < bassPatterns.size(); i++ )
                      {
                        System.out.println("\t" + bassPatterns.get(i));
                      }
                  }

                splitUpIntoSections();
                if( debug )
                  {
                    System.out.println("\n## After splitUpIntoSections() ##");
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
                    System.out.println("\n## After pruneSections() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                      }
                  }

                findTenativeRepresentatives();
                if( debug )
                  {

                    System.out.println("\n## After findTenativeRepresenatives() ##");
                    for( int i = 0; i < sections.size(); i++ )
                      {
                        System.out.println("Representative rules for section: " + i);
                        for( int j = 0; j < sections.get(i).getClusters().size(); j++ )
                          {
                            System.out.println(sections.get(i).getCluster(j));
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
                    System.out.println("\n\n### Resulting Patterns ###");
                    for( int i = 0; i < bassPatterns.size(); i++ )
                      {
                        System.out.println(bassPatterns.get(i));
                      }
                  }
              }
          }

        if( debug )
          {
            System.out.println("\n### Errors that occurred during bass generation: ###");
            ArrayList<String> err = MIDIBeast.errors;
            for( int i = 0; i < err.size(); i++ )
              {
                System.out.println(i + 1 + ": " + err.get(i));
              }
          }
      }
    catch( Exception e )
      {
        ErrorLog.log(ErrorLog.WARNING,
                    "Sorry, there was an unknown internal error while generating "
                  + "the bass patterns.");
        e.printStackTrace();
      }
  }

public ArrayList<BassPattern> getBassRules()
  {
    return bassPatterns;
  }

public void setSimplifiedPitchesRules(ArrayList<String> s)
  {
    this.simplifiedPitchesRules = s;
  }


/**
 * This method simply initializes the field members of the class and sets number
 * of rules to the desired fraction of the original set of rules
 */

public void initialize()
  {
    numberOfUniqueRules = (int) (percentageOfClusters * uniqueRules.size());
    simplifiedPitchesRules = new ArrayList<String>();
    sansDuplicatesRules = new ArrayList<String>();
    sections = new ArrayList<Section>();
    bassPatterns = new ArrayList<BassPattern>();
  }

/**
 * This method iterates through the original set of rules and simplifies the
 * pitch info contained.
 */

public void simplifyRulePitches()
  {
    for( int i = 0; i < MIDIBeast.originalBassRules.size(); i++ )
      {
        simplifiedPitchesRules.add(simplifyPitchInfo(MIDIBeast.originalBassRules.get(i)));
      }
  }

/**
 * Return the duration, in slots, of an item in a pattern. The types of items 
 * are the form Cd, where C is a letter and d is a duration string, and (X s d),
 * where s is a scale degree string and d is a duration string. An example of a
 * duration string is 4+16/3 etc.
 *
 * @param ob
 * @return
 */

public static int getPatternItemDuration(Object ob)
  {
    int result = 0;
    if( ob instanceof Polylist )
      {
        Polylist listOb = (Polylist) ob;

        result = Duration.getDuration(listOb.third().toString());
      }
    else if( ob instanceof String )
      {
        String stringOb = (String) ob;

        result = Duration.getDuration(stringOb.substring(1));
      }

    //System.out.println("Duration of " + ob + " is " + result);
    return result;
  }


/**
 * Produce a duration string from a given duration string, by truncating the
 * latter to a specific length in slots.
 *
 * @param durationString
 * @param length
 * @return
 */

public static String truncateDurationString(String durationString, int length)
  {
    int duration = Duration.getDuration(durationString);

    duration -= length;

    if( duration <= 0 )
      {
        // No truncation necessary in this case
        return durationString;
      }

    return Note.getDurationString(length);
  }


/**
 * Truncate a pattern item to a maximum duration. The types of items are of
 * the form Cd, where C is a letter and d is a duration string, and Polylist (X
 * s d), where s is a scale degree string and d is a duration string. An example
 * of a duration string is 4+16/3 etc.
 *
 * @param ob
 * @param length
 * @return
 */

public static Object truncatePatternItem(Object ob, int length)
  {
    Object result = null;
    if( ob instanceof String )
      {
        String stringOb = (String) ob;

        char firstChar = stringOb.charAt(0);

        result = firstChar + truncateDurationString(stringOb.substring(1), length);
      }
    else if( ob instanceof Polylist )
      {
        Polylist listOb = (Polylist) ob;

        String first = (String) listOb.first();

        String degree = listOb.second().toString();

        String durationString = listOb.third().toString();

        durationString = truncateDurationString(durationString, length);

        result = Polylist.list(first, degree, durationString).toString();
      }
    else
      {
      assert false;
      }
    //System.out.println("\ntruncate item " + ob + " to " + length + " = " + result.toString());
    return result;
  }

/**
 * Truncate a pattern to a maximum duration, expressed as a number of slots.
 * The pattern comes in as a String and the new pattern is returned as a String.
 * The types of items are of the form Cd, where C is a letter and d is a
 * duration string, and Polylist (X s d), where s is a scale degree string and d
 * is a duration string. An example of a duration string is 4+16/3 etc.
 * Internally, the pattern is converted to a Polylist for ease in parsing, 
 * so this could eventually be changed to accept a Polylist rather than 
 * a String.
 *
 * @param ob
 * @param length
 * @return
 */

public static String truncatePattern(int maxLength, String currentRule)
  {
    //System.out.println("\nTruncate pattern, desired length " + maxLength + ", rule: " + currentRule + ", length " + getBassRuleLength(currentRule));
    int residualLength = maxLength;

    // L is for convenience in decomposition, so don't
    // need to use substring, etc.

    Polylist L = Polylist.PolylistFromString(currentRule);

    // buffer will accumulate the resulting string.

    StringBuilder buffer = new StringBuilder();

    while( L.nonEmpty() && residualLength > 0 )
      {
        Object ob = L.first();

        int obLength = getPatternItemDuration(ob);

        if( obLength <= residualLength )
          {
            buffer.append(ob);
            buffer.append(" ");
            residualLength -= obLength;
          }
        else
          {
            Object newOb = truncatePatternItem(ob, residualLength);
            buffer.append(newOb);
            buffer.append(" ");
            residualLength = 0;
          }

        L = L.rest();
      }

    String result = buffer.toString();

    //System.out.println("result = " + result + ", length " + getBassRuleLength(result));
    return result;
  }

/**
 * Return the length of a rule in slots.
 * @param currentRule
 * @return 
 */
static int getBassRuleLength(String currentRule)
  {
    // L is for convenience in decomposition, so don't
    // need to use substring, etc.

    Polylist L = Polylist.PolylistFromString(currentRule);

    // buffer will accumulate the resulting string.

    int result = 0;

    while( L.nonEmpty() )
      {
        result += getPatternItemDuration(L.first());
        L = L.rest();
      }

    return result;
  }

/**
 * If a maximum bass pattern length is specified, this method will 
 * truncate each bass pattern in excess of the desired length
 * to the specified length.
 */

private void truncateBassPatterns()
  {
    //System.out.println("\nTruncateBassPatterns");
    
    ArrayList<String> tempRules = new ArrayList<String>();
    int maxSlotLength = MIDIBeast.getMaxBassPatternLengthInSlots();
    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
      {
        String currentRule = simplifiedPitchesRules.get(i);

        tempRules.add(truncatePattern(maxSlotLength, currentRule));
      }
    simplifiedPitchesRules = tempRules;
    if( debug )
      {
        System.out.println("####After Truncate Bass Rules####");
        for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
          {
            System.out.println(simplifiedPitchesRules.get(i));
          }
      }
  }

/**
 * rk 10 April 2012: This had been broken for a long time. The problem is that
 * e.g. X(5)4 is no longer used. Now it would be (X 5 4) In any case, it would
 * be better to treat the string as a Polylist, rather than re-parsing it
 * character by character.
 *
 * I am not sure what AX is supposed to mean; delving into it.
 *
 * This method is called on each rule by simplifyRulePitches() it handles the
 * logic involved in simplifying the pitch info
 *
 * @param String - The pattern element(eg NAX(5)4) that is to be simplified
 * @return String - The simplified string (eg X(5)4)
 *
 */

public String simplifyPitchInfo(String s)
  {
    Polylist L = (Polylist) (Polylist.PolylistFromString(s).first()); // due to extra level of nesting
    //System.out.print("string = " + s + ", polylist = " + L);
    StringBuilder buffer = new StringBuilder();
    while( L.nonEmpty() )
      {
        Object item = L.first();
        if( item instanceof String )
          {
            String stringItem = (String) item;
            char firstChar = stringItem.charAt(0);
            switch( firstChar )
              {
                case 'X':
                    // Convert old-style X rules to new
                    //i.e. X(5)8/3 becomes (X 5 8/3)

                    L = L.rest();
                    Polylist P = (Polylist) L.first();
                    L = L.rest();

                    buffer.append("(X ");
                    buffer.append(P.first());
                    buffer.append(" ");
                    buffer.append(L.first());
                    buffer.append(") ");
                    break;


                default:
                    buffer.append(stringItem);
                    buffer.append(" ");
                    break;
              }
          }
        else
          {
            assert false;
          }

        L = L.rest();
      }
    //System.out.println(", result = " + buffer.toString());
    return buffer.toString();
  }

/**
 * This method searches through the set of rules with simplified pitch
 * information, removes all repeats, and creates a bass pattern for each set of
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

        // If there already exists a section corresponding to the number
        // of slots that the current rule contains, then add the current
        // rule to that section.
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
        //If there does not exist a section corresponding to the number 
        // of slots that the current rule contains, then create such a
        // section and add the current rule to that section.
        else
          {
            Section newSection = new Section(currentRule.getSlots(), uniqueRules.get(i));
            sections.add(newSection);
            usedSlotCounts.add(newSection.getSlotCount());
          }
      }

  }

/**
 * This should be changed to use S expression parsing.
 * 
 * This function is used in splitUpIntoSections() and calculates the beat
 * duration of a given pattern.
 *
 * @param String - the pattern whose beat duration is to be caclulated (eg (B4
 * B8 B8))
 * @return Double - the duration of the given pattern (eg 2.0)
 */

public Double calculateBeats(String s)
  {
    if( debug )
      {
        System.out.println("Calculating Beat of: " + s);
      }
    String[] split = s.split(" ");
    int rhythmIndex;
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
 * requested as determined in the initialize() method.
 */

public void pruneSections()
  {
    for( int i = 0; i < sections.size(); i++ )
      {
        int numClusters = (int) Math.round(numberOfUniqueRules * ((sections.get(i).size() * 1.0) / MIDIBeast.originalBassRules.size()));
        if( numClusters == 0 )
          {
            numClusters = 1;
          }
        sections.get(i).setNumClustersAllowed(numClusters);
      }
  }

public ArrayList<Section> getSections()
  {
    return sections;
  }

/**
 * This method finds the tenative representatives for the clusters of each
 * section. One representative is found for each of the clusters that a sections
 * is supposed to have. The first representative is picked non
 * determanistically, and all proceeding representatives are choosen by finding
 * the pattern with the greatest distance to all other representatives
 */

public void findTenativeRepresentatives()
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
            if( maxIndex >= 0 )
              {
              s.addCluster(s.getRule(maxIndex));
              usedRules.add(maxIndex);
              }
          }
      }
  }

/**
 * This method iterates through each rule of a seciton and assigns it to a
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
            numberOfRules += currentRule.weight;
          }
      }
  }


/**
 * This method goes through each cluster and finds the rule whose distance is
 * least from all the other rules. This rule is said to be the best
 * representative of the cluster and is added as a bass pattern with a weight
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
            bassPatterns.add(new BassPattern(selectedRule.getRule(), c.calculateWeight()));
            c.setBetterRep(selectedRule);
          }
      }
  }

/**
 * If the user so wishes, he/she may receive every rule generated from the song,
 * and this is the method to retrieve that list of rules
 */

public ArrayList<BassPattern> getUnfilteredRules()
  {
    ArrayList<BassPattern> temp = new ArrayList<BassPattern>();
    for( int i = 0; i < simplifiedPitchesRules.size(); i++ )
      {
        temp.add(new BassPattern(simplifiedPitchesRules.get(i), 1));
      }
    if( debug )
      {
        System.out.println("## After getUnfilteredRules() ##");
        for( int i = 0; i < temp.size(); i++ )
          {
            System.out.println(temp.get(i));
          }
      }
    return temp;
  }

public BassPattern makeBassPatternObj(String r, float w)
  {
    return new BassPattern(r, w);
  }

public BassPattern makeBassPatternObj(String r, float w, String n)
  {
    return new BassPattern(r, w, n);
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

    double beats = getBassRuleLength(rule);
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

public class BassPattern implements RepPattern
{

private String rule;
private float weight;
private int duration;
private String patternName;

public BassPattern(String r, float w)
  {
    rule = r.trim();
    weight = w;
    duration = getBassRuleLength(rule);
    patternName = "";
  }

public void setDuration(int duration)
  {
    this.duration = duration;
  }

public BassPattern(String r, float w, int d)
  {
    rule = r.trim();
    weight = w;
    duration = d;
    patternName = "";
  }

public BassPattern(String r, float w, String n)
{
    rule = r.trim();
    weight = w;
    duration = getBassRuleLength(rule);
    patternName = n;
}

@Override
public String toString()
  {
    return "(bass-pattern (name " + patternName + ")(rules " + rule + ")(weight " + weight + "))";
  }

public String getBareRule()
  {
    return rule + "(weight " + weight + ")";
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

public int getNewDuration()
  {
    return getBassRuleLength(rule);
  }

public String getName()
{
    return patternName;
}

}


public class Section
{

ArrayList<Cluster> clusters;
ArrayList<RawRule> rules;
int slotCount;
int numClustersAllowed;

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

// Takes an index into the uniqueRules ArrayList and returns
// the corresponding rules index into the original array of rules
public RawRule getRule(int i)
  {
    return rules.get(i);
  }

public Cluster getCluster(int i)
  {
    return clusters.get(i);
  }

public ArrayList<RawRule> getRules()
  {
    return rules;
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

//Need: String method
//      Compare Method

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

public RawRule getRule(int index)
  {
    return rules.get(index);
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

public int getRepIndex()
  {
    return repRule.getIndex();
  }

public float calculateWeight()
  {
    float weight = 0;
    for( int i = 0; i < rules.size(); i++ )
      {
        weight += rules.get(i).getWeight();
      }
    weight = (float) (Math.floor(((weight / numberOfRules) * 100) / 2));
    if( weight < 1.0 )
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
