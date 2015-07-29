/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College
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

package imp.data;

import imp.util.ErrorLog;
import imp.voicing.HandManager;
import imp.ImproVisor;
import imp.voicing.AutomaticVoicingSettings;
import imp.voicing.VoicingGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.PolylistEnum;

/**
 * Contains a rhythmic pattern for use in a chord accompaniment and methods
 * needed to realize that rhythmic pattern with voice leading according
 * to a chord progression.
 * @see Style
 * @author Stephen Jones, Robert Keller, Carli Lessard
 */

public class ChordPattern
        extends Pattern implements Serializable
{
/**
 * the rules for the pattern, stored as indices into the ruleTypes array
 */
private ArrayList<String> rules;

/**
 * the durations for the pattern, stored as leadsheet representation of
 * rhythm
 */
private ArrayList<String> durations;

/**
 * the hash map that carries the rules defined in the style
 */
private LinkedHashMap<String, Polylist> definedRules = 
        new LinkedHashMap<String, Polylist>();

/**
 * array containing the types of rules
 */
private static final String ruleTypes[] =
  {
  "X", "R", "V", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
  };

// indices into the ruleTypes array
private static final int STRIKE = 0;

private static final int REST = 1;

private static final int VOLUME = 2;

private static final String STRIKE_STRING = ruleTypes[STRIKE];

private static final String REST_STRING = ruleTypes[REST];

private static final String VOLUME_STRING = ruleTypes[VOLUME];

/**
 * array containing ChordPattern keywords
 */
private static String keyword[] =
  {
  "rules", "weight", "push", "name", "use"
  };

// indices into the keyword array
private static final int RULES = 0;

private static final int WEIGHT = 1;

private static final int PUSH = 2;

private static final int NAME = 3;

private static final int USE = 4;

private String patternName = "";

private String pushString = "";

private int pushAmount = 0; // push amount, in slots


/**
 * Creates a new ChordPattern (only used by the factory).
 */
public ChordPattern()
  {
  rules = new ArrayList<String>();
  durations = new ArrayList<String>();
  }


/**
 * A factory for creating a ChordPattern from a Polylist.
 * @param L         a Polylist containing ChordPattern information
 * @return the ChordPattern created from the Polylist, or null if there
 *         was a problem
 */
public static ChordPattern makeChordPattern(Polylist L)
  {
    // Example of L:
    // 	(chord-pattern (rules P8 X1 R4 X2 X4)(weight 5)(push 8/3)
    //
    // X = "hit", R = "rest"
    // The notation for push is the same as a duration.
    // For example, 8/3 is an eighth-note triplet
    
  Polylist original = L;
    
  ChordPattern cp = new ChordPattern();

  while( L.nonEmpty() )
    {
    Polylist item = (Polylist)L.first();
    L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case NAME:
        {
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               break;
           }
           else if(item.first() instanceof String) 
           {
               cp.patternName = (String) item.first();
           }
           else
           {
               cp.setError("Unrecognized name type in chord pattern: " + item.first());
               return cp;
           }
           break;
        }
      case RULES:
        {
        while( item.nonEmpty() )
          {
          Object entry = item.first();
          item = item.rest();
          
          if( entry instanceof String )
            {
          String s = (String)entry;

          String rule = s.substring(0, 1);
          String dur = s.substring(1);
          
          char c = rule.charAt(0);
          
          switch( c )
            {
              case 'X':
              case 'R':
              case 'V':
                  cp.addRule(rule, dur);
                  break;
                  
              default:
                  cp.setError("Unrecognized " + rule 
                            + " in chord pattern " + original);
                  return cp;
            }
          
          //item = item.rest();
          }
          
                        // check to see if it is an S-expression
          else if( entry instanceof Polylist )
          {
              //e.g. (X 5 4)
              Polylist plist = (Polylist)entry;
              int len = plist.length();
                  
              // make sure it has 3 or more elements for a valid expression
              if( len >= 3 && plist.first().equals(ruleTypes[STRIKE]) )
              {
                  String rule = plist.second().toString();
                  //System.out.println(rule);
                  String duration = plist.third().toString();
                  //System.out.println(duration);
                  cp.addRule(rule, duration);
              }
              else
              {
                  cp.setError("unrecognized " + entry + " in chord pattern: " + original);
                  return cp;
              }
          }
          
          else
            {
                  cp.setError("Unrecognized " + item.first()
                            + " in chord pattern " + original);
                  return cp;              
            }
          }
        break;
        }
          
      case WEIGHT:
        {
        try
          {
          Number w = (Number)item.first();
          cp.setWeight(w.intValue());
          break;
          }
        catch( Exception e )
          {
            cp.setError("Expected weight value, but found " + item.first()
                      + " in " + original);
          }
        break;
        }
          
      case PUSH:
        {
        if( item.nonEmpty() )
          {
          cp.pushString = item.first().toString();
          cp.pushAmount = Duration.getDuration(cp.pushString);
        //System.out.println("pushAmount " + pushString + " = " + cp.pushAmount + " slots");
          }
        break;
        }
          
      default:
          cp.setError("Error in chord pattern " + original);
          return cp;
      }
    }
  //System.out.println("makeChordPattern on " + original + " returns " + cp);
  return cp;
  }


/**
 * A method that adds rules and durations to an existing bass pattern
 * Used in place of makeChordPattern when the Style has pre-defined rules
 * @param L
 * @return 
 */
public ChordPattern makePattern(Polylist L)
  {
    // Example of L:
    // 	(chord-pattern (rules P8 X1 R4 X2 X4)(weight 5)(push 8/3)
    //
    // X = "hit", R = "rest"
    // The notation for push is the same as a duration.
    // For example, 8/3 is an eighth-note triplet
    
  Polylist original = L;
    
  ChordPattern cp = this;

  while( L.nonEmpty() )
    {
    Polylist item = (Polylist)L.first();
    L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case NAME:
        {
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               break;
           }
           else if(item.first() instanceof String) 
           {
               cp.patternName = (String) item.first();
           }
           else
           {
               cp.setError("Unrecognized name type in chord pattern: " + item.first());
               return cp;
           }
           break;
        }
      case USE:
      {
          if( item.first() instanceof String )
                {
                    String name = (String) item.first();
                    cp.patternName = name;
                    LinkedHashMap ruleDefinitions = cp.getDefinedRules();
                    Polylist rules = (Polylist)ruleDefinitions.get( name );
                    String first = (String)rules.first();
                    if( Leadsheet.lookup(first, keyword) == RULES )
                    {
                        rules = rules.rest();
                        while( rules.nonEmpty() )
          {
          Object entry = rules.first();
          rules = rules.rest();
          
          if( entry instanceof String )
            {
          String s = (String)entry;

          String rule = s.substring(0, 1);
          String dur = s.substring(1);
          
          char c = rule.charAt(0);
          
          switch( c )
            {
              case 'X':
              case 'R':
              case 'V':
                  cp.addRule(rule, dur);
                  break;
                  
              default:
                  cp.setError("Unrecognized " + rule 
                            + " in chord pattern " + original);
                  return cp;
            }
          
          //item = item.rest();
          }
          
                        // check to see if it is an S-expression
          else if( entry instanceof Polylist )
          {
              //e.g. (X 5 4)
              Polylist plist = (Polylist)entry;
              int len = plist.length();
                  
              // make sure it has 3 or more elements for a valid expression
              if( len >= 3 && plist.first().equals(ruleTypes[STRIKE]) )
              {
                  String rule = plist.second().toString();
                  //System.out.println(rule);
                  String duration = plist.third().toString();
                  //System.out.println(duration);
                  cp.addRule(rule, duration);
              }
              else
              {
                  cp.setError("unrecognized " + entry + " in chord pattern: " + original);
                  return cp;
              }
          }
          
          else
            {
                  cp.setError("Unrecognized " + item.first()
                            + " in chord pattern " + original);
                  return cp;              
            }
          }
                    }
                }
          break;
      }
      case RULES:
        {
        while( item.nonEmpty() )
          {
          Object entry = item.first();
          item = item.rest();
          
          if( entry instanceof String )
            {
          String s = (String)entry;

          String rule = s.substring(0, 1);
          String dur = s.substring(1);
          
          char c = rule.charAt(0);
          
          switch( c )
            {
              case 'X':
              case 'R':
              case 'V':
                  cp.addRule(rule, dur);
                  break;
                  
              default:
                  cp.setError("Unrecognized " + rule 
                            + " in chord pattern " + original);
                  return cp;
            }
          
          //item = item.rest();
          }
          
                        // check to see if it is an S-expression
          else if( entry instanceof Polylist )
          {
              //e.g. (X 5 4)
              Polylist plist = (Polylist)entry;
              int len = plist.length();
                  
              // make sure it has 3 or more elements for a valid expression
              if( len >= 3 && plist.first().equals(ruleTypes[STRIKE]) )
              {
                  String rule = plist.second().toString();
                  //System.out.println(rule);
                  String duration = plist.third().toString();
                  //System.out.println(duration);
                  cp.addRule(rule, duration);
              }
              else
              {
                  cp.setError("unrecognized " + entry + " in chord pattern: " + original);
                  return cp;
              }
          }
          
          else
            {
                  cp.setError("Unrecognized " + item.first()
                            + " in chord pattern " + original);
                  return cp;              
            }
          }
        break;
        }
          
      case WEIGHT:
        {
        try
          {
          Number w = (Number)item.first();
          cp.setWeight(w.intValue());
          break;
          }
        catch( Exception e )
          {
            cp.setError("Expected weight value, but found " + item.first()
                      + " in " + original);
          }
        break;
        }
          
      case PUSH:
        {
        if( item.nonEmpty() )
          {
          cp.pushString = item.first().toString();
          cp.pushAmount = Duration.getDuration(cp.pushString);
        //System.out.println("pushAmount " + pushString + " = " + cp.pushAmount + " slots");
          }
        break;
        }
          
      default:
          cp.setError("Error in chord pattern " + original);
          return cp;
      }
    }
  //System.out.println("makeChordPattern on " + original + " returns " + cp);
  return cp;
  }


/**
 * Adds a rule and duration to this ChordPattern.
 * @param rule      a String containing the rule
 * @param duration  a String containing the duration
 */
private void addRule(String rule, String duration)
  {

    rules.add(rule);
    durations.add(duration);
  }


@Override
/**
 * Get the duration, in slots
 * @return 
 */
public int getDuration()
  {
    int duration = 0;
    
    Iterator<String> r = rules.iterator();
    Iterator<String> d = durations.iterator();
    
    while( r.hasNext() )
      {
        String rule = r.next();
        String dur = d.next();
        if( !rule.equals(VOLUME_STRING) )
          {
            // Ignore volume in computing duration
            duration += Duration.getDuration(dur);
          }
      }
    
    return duration;
  }


/**
 * Realizes the Pattern into a sequencable Polylist.
 * @param chord     the ChordSymbol to voice
 * @param lastChord a Polylist containing the last chord voicing
 * @return A Polylist that can be sequenced.  This Polylist has two elements.
 * 
 *         The first element is another Polylist that contains
 *         a sequence of chord voicings (each of which is a Polylist of
 *         NoteSymbols, including possibly volume settings.)  
 * 
 *         The second element is a MelodyPart containing
 *         containing rests, each of which is a duration corresponding to
 *         the voicings.
 */

public ChordPatternVoiced applyRules(ChordSymbol chord, Polylist lastChord)
  {
  Iterator<String> i = rules.iterator();
  Iterator<String> j = durations.iterator();
  
  lastChord = BassPattern.filterOutStrings(lastChord);

  //System.out.println("applyRules in: Chord = " + chord + ", rules = " + rules + ", durations = " + durations);

  String chordRoot = chord.getRootString();
  ChordForm chordForm = chord.getChordForm();
  Key key = chordForm.getKey(chordRoot);
  int rise = PitchClass.findRise(chordRoot);

  // FIXME: this is sort of a hacky way to do the durations since we
  // don't really have a proper way to store music with multiple voices
  MelodyPart durationMelody = new MelodyPart();

  LinkedList<Polylist> chordLine = new LinkedList<Polylist>();
  
  int volume = 127;

  while( i.hasNext() )
    {
    String rule = i.next();
    String duration = j.next();

    //System.out.println("     rule = " + rule + ", duration = " + duration);
    // Process the symbols in the pattern into notes and rests,
    // inserting volume indication when the volume changes.
    
    if( rule.equals(STRIKE_STRING) )
    {
        durationMelody.addNote(new Rest(Duration.getDuration(duration)));
         
        Polylist voicing = findVoicing(chord, lastChord, style);

        if( voicing == null )
          {
          voicing = Polylist.nil;
          //break;
          }

        chordLine.add(voicing.cons(new VolumeSymbol(volume)));
        lastChord = voicing; 
    }
    
    else if( rule.equals(REST_STRING) )
    {
        durationMelody.addNote(new Rest(Duration.getDuration(duration)));
        chordLine.add(Polylist.nil); // was NoteSymbol.makeNoteSymbol("r" + duration));
    }
    
    else if( rule.equals(VOLUME_STRING) )
    {
        // Volume will take effect when next chord voicing is appended.
        volume = Integer.parseInt(duration);
    }
    
    else
    {
        int interval = Integer.parseInt(rule);
         
        durationMelody.addNote(new Rest(Duration.getDuration(duration)));
            
        // first, get the note that is the interval from the root
        Polylist scales = chordForm.getScales();
            
        Polylist scale = (Polylist) scales.first();
        NoteSymbol tonic = NoteSymbol.makeNoteSymbol( (String) scale.first() );
        String scaleType = Advisor.concatListWithSpaces(scale.rest());
        ScaleForm scaleForm = Advisor.getScale(scaleType);
            
        Polylist tones = scaleForm.getSpell(tonic);
        tones = NoteSymbol.transposeNoteSymbolList(tones, rise);

        tones = tones.reverse().rest().reverse();
        //System.out.println("The transposed notes are: " + tones);
            
        // with the note symbol, we can get the chord base, which will
        // be used for the chord
        NoteSymbol noteSymbol = BassPattern.getInterval(interval, tones);
        PitchClass pitchClass = noteSymbol.getPitchClass();
        String noteBass = pitchClass.getChordBase();
            
        String chordName = (String)noteBass.concat("Note");
        Chord newChord = new Chord(chordName);
            
        //System.out.println("The new chord is " + newChord);
            
        Polylist voicing = findVoicing(newChord.getChordSymbol(), lastChord, style);
            
        // then add the voicing of the chord to the chord line
        if( voicing == null )
        {
            voicing = Polylist.nil;
        }
            
        //System.out.println("The voicing for this chord is: " + voicing);
                        
        chordLine.add(voicing.cons(new VolumeSymbol(volume)));
        lastChord = voicing;
            
        //System.out.println("The duration melody is: " + durationMelody);
        //System.out.println("The chord line is: " + chordLine);
    }
    }

  ChordPatternVoiced result = new ChordPatternVoiced(chordLine, durationMelody);

  //System.out.println("applyRules: Chord = " + chord + ", rules = " + rules + ", durations = " + durations + ", result (chordline, durations) = " + result);
  return result;
  }


/**
 * Returns a boolean determining whether the given chord can be voiced
 * based on the given Style.
 * @param chord     the ChordSymbol to voice
 * @param style     the Style to use to voice the ChordSymbol
 * @return a boolean determining whether the given chord can be voiced
 *         based on the given Style
 */
public static boolean goodVoicing(ChordSymbol chord, Style style)
  {
  return goodVoicing(chord, style.getChordBase(), style);
  }


/**
 * Returns a boolean determining whether the given chord can be voiced
 * based on the given Style and a previous chord.
 * @param chord     the ChordSymbol to voice
 * @param lastChord a Polylist containing the last chord voicing
 * @param style     the Style to use to voice the ChordSymbol
 * @return a boolean determining whether the given chord can be voiced
 *         based on the given Style and a previous chord
 */
public static boolean goodVoicing(ChordSymbol chord, Polylist lastChord,
                                  Style style)
  {
  Polylist L = findVoicing(chord, lastChord, style, false);
  if( L == null )
    {
    return false;
    }
  else
    {
    return true;
    }
  }


/**
 * Returns a voicing for a chord.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @return a Polylist containing the chord voicing
 */
public static Polylist findVoicing(ChordSymbol chord, Polylist lastChord,
                                   Style style)
  {
  return findVoicing(chord, lastChord, style, true);
  }


/**
 * Returns a voicing for a chord.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing
 */
public static Polylist findVoicing(ChordSymbol chord, Polylist lastChord,
                                   Style style, boolean verbose)
  {
  Polylist voicing = findVoicingAndExtension(chord, lastChord, style, verbose);

  if( voicing == null )
    {
    return null;    // append the voicing and the extension
    }
  voicing = ((Polylist)voicing.first()).append(
          (Polylist)voicing.second());

  return voicing;
  }


/**
 * Returns A voicing for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist findVoicingAndExtension(ChordSymbol chord,
                                               Polylist lastChord, Style style,
                                               boolean verbose)
  {
  Polylist voicings = getVoicingAndExtensionList(chord, lastChord, style,
          verbose);
  if( voicings == null )
    {
    return null;
    }
  return (Polylist)BassPattern.getRandomItem(voicings);
  }


/**
 * Returns A voicing for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist findFirstVoicingAndExtension(ChordSymbol chord,
                                                    Polylist lastChord,
                                                    Style style, boolean verbose)
  {
  Polylist voicings = getVoicingAndExtensionList(chord, lastChord, style,
          verbose);
  if( voicings == null )
    {
    return null;
    }
  return (Polylist)voicings.first();
  }


/**
 * Returns a list of acceptable voicings for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist getVoicingAndExtensionList(ChordSymbol chord,
                                                  Polylist lastChord,
                                                  Style style, 
                                                  boolean verbose)
  {
    //Form Chord Voicing
    //Start
    //---------------------------------------------------------------------------------------------//
    
    /*Init Dan's Classes*/
    if(style.hasCustomVoicing()){
        AutomaticVoicingSettings avs= ImproVisor.avs;
        VoicingGenerator vgen=new VoicingGenerator();
        HandManager handyMan=new HandManager();
        //avs.setLeftHandLowerLimit(style.getChordLow().getMIDI());
        //avs.setRightHandUpperLimit(style.getChordHigh().getMIDI());
        handyMan.setLeftHandLowerLimit(avs.getLeftHandLowerLimit());
        handyMan.setLeftHandUpperLimit(avs.getLeftHandUpperLimit());
        handyMan.setLeftHandSpread(avs.getLeftHandSpread());
        handyMan.setRightHandLowerLimit(avs.getRightHandLowerLimit());
        handyMan.setRightHandUpperLimit(avs.getRightHandUpperLimit());
        handyMan.setRightHandSpread(avs.getRightHandSpread());
        handyMan.setLeftHandMinNotes(avs.getLeftHandMinNotes());
        handyMan.setLeftHandMaxNotes(avs.getLeftHandMaxNotes());
        handyMan.setRightHandMinNotes(avs.getRightHandMinNotes());
        handyMan.setRightHandMaxNotes(avs.getRightHandMaxNotes());
        handyMan.setPreferredMotion(avs.getPreferredMotion());
        handyMan.setPreferredMotionRange(avs.getPreferredMotionRange());
        handyMan.resetHands();
        vgen.setFullStepAwayMultiplier(avs.getFullStepAwayMultiplier());
        vgen.setHalfStepAwayMultiplier(avs.getHalfStepAwayMultiplier());
        vgen.setMaxPriority(avs.getMaxPriority());
        vgen.setLeftColorPriority(avs.getLeftColorPriority());
        vgen.setRightColorPriority(avs.getRightColorPriority());
        vgen.setPreviousVoicingMultiplier(avs.getPreviousVoicingMultiplier());
        vgen.setRepeatMultiplier(avs.getRepeatMultiplier());
        vgen.setHalfStepReducer(avs.getHalfStepReducer());
        vgen.setFullStepReducer(avs.getFullStepReducer());
        vgen.setInvertM9(avs.getInvertM9());
        vgen.setVoiceAll(avs.getVoiceAll());
        vgen.setRootless(avs.getRootless());
        vgen.setMinInterval(avs.getMinInterval());
        

        /*get values from the user*/

        /*process values*/
        //while loop with first item
        int[] lastVoicing=null;
        ArrayList<int[]>progressionVoicings=new ArrayList<int[]>();
        ArrayList<Integer> bassList=new ArrayList<Integer>();

        System.out.println("---------");
        System.out.println("chord: "+chord.toString());    //trace
        Chord chord1 = new Chord(chord.getName());
        Polylist spelling;                             //create voicing variable for first chord
        spelling = chord1.getSpell();                  //get chord1 voicing; assign to voicing
        System.out.println("spelling: " + spelling.toString());    //trace
        bassList.add(((NoteSymbol)spelling.first()).getMIDI()); //gets a list of bass notes
        Polylist priorityPoly=chord1.getPriority();     //create a polylist for chord priority notes
        Polylist colorPoly=chord1.getColor();           //create a polylist for chord color notes
        int[] color=new int[colorPoly.length()];        //array for color notes' midi values
        int [] priority = new int[priorityPoly.length()];   //array for priority notes' midi values
        for(int i=0; i<color.length; i++)               //for loop that gets midi value of corresponding color note in colorPoly and puts in color[]
        {
            color[i]=((NoteSymbol)colorPoly.nth(i)).getMIDI();
            //System.out.println("color num:" +color[i]);
        }

        for(int i=0; i<priority.length; i++)            //for loop that gets midi value of corresponding priority note in priorityPoly and puts in priority[]
        {
            priority[i]=((NoteSymbol)priorityPoly.nth(i)).getMIDI();
            //System.out.println("priority num:" +priority[i]);
        }
        System.out.println();
        System.out.println("New voicing:");
        //settings
        handyMan.repositionHands();
        vgen.setLowerLeftBound(handyMan.getLeftHandLowestNote());
        vgen.setUpperLeftBound(handyMan.getLeftHandLowestNote()+handyMan.getLeftHandSpread());
        vgen.setLowerRightBound(handyMan.getRightHandLowestNote());
        vgen.setUpperRightBound(handyMan.getRightHandLowestNote()+handyMan.getRightHandSpread());
        vgen.setNumNotesLeft(handyMan.getNumLeftNotes());
        vgen.setNumNotesRight(handyMan.getNumRightNotes());
        vgen.setColor(color);
        vgen.setPriority(priority);
        vgen.setRoot(chord1.getRootSemitones());

        System.out.println("Did settings");

        int index=0;
        if(lastVoicing!=null){
            for(Polylist a = lastChord; a.nonEmpty(); a=a.rest()){
            lastVoicing[index] = ((NoteSymbol)a.first()).getMIDI();
            index++;
            System.out.println(Arrays.toString(lastVoicing));
            }
        }

        vgen.setPreviousVoicing(lastVoicing);
        vgen.calculate();
        lastVoicing = vgen.getChord();
        progressionVoicings.add(lastVoicing);
        Polylist midiL;


        Integer[] lastVoicingObj=new Integer[lastVoicing.length];
        for(int i=0; i<lastVoicing.length; i++)
            lastVoicingObj[i]=new Integer(lastVoicing[i]);
        midiL=Polylist.PolylistFromArray(lastVoicingObj);       //creates polylist from lastVoicingObj to assign to a midi value list midiL
        System.out.println("midiL: "+midiL.toString());   //trace

        PolylistBuffer buffer = new PolylistBuffer();
        for( Polylist M = midiL; M.nonEmpty(); M = M.rest() )
        {
            NoteSymbol n = NoteSymbol.makeNoteSymbol(new Note((Integer)M.first()));
            buffer.append(n);
        }

        chord.setVoicing(buffer.toPolylist());
        System.out.println("Voicing: " + buffer.toPolylist().toString());
        System.out.println("----------");
        System.out.println();
    }
/*End*/
//----------------------------------------------------------------------------------------------------//
    
  Polylist voicing = chord.getVoicing();
  String chordRoot = chord.getRootString();
  ChordForm chordForm = chord.getChordForm();
  Key key = chordForm.getKey(chordRoot);
  int rise = PitchClass.findRise(chordRoot);
//System.out.println("getVoicingsAndExtensionList " + chord + " style = " + style + " getVoicing() = " + voicing);

  if( voicing.nonEmpty() )
    {
    // if the voicing is already specified in the chord, then
    // put the voicing and extension near the previous chord
    
    Polylist extension = chord.getExtension();
    int lowestNote=128;
    int highestNote=0;
            for(Object o:voicing.array())
            {
                
                if(((NoteSymbol)o).getMIDI()<lowestNote)
                    lowestNote=((NoteSymbol)o).getMIDI();
                if(((NoteSymbol)o).getMIDI()>highestNote)
                    highestNote=((NoteSymbol)o).getMIDI();
        
    
            }
            
    Polylist v = ChordPattern.placeVoicing(lastChord, voicing, extension,
           NoteSymbol.makeNoteSymbol(new Note(lowestNote)), NoteSymbol.makeNoteSymbol(new Note(highestNote)));
    //v=voicing;// fix here
    
    //System.out.println("Chord low, high"+style.getChordLow()+" , "+style.getChordHigh());
    if( v == null )
      {
      // if the specified voicing doesn't fit in range, error
      // and don't voice this chord
          
          if( verbose )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Voicing does not fit within range: " + voicing);
        }
      return null;
          
          //return Polylist.list(v);
      }
    else
      {
      return Polylist.list(v);
      }
          
    
        //return Polylist.list(v);
    }
  else
    {
    // if there is no voicing specified, then find one!
    // get the voicings from the vocabulary file for this chord type
    
    Polylist voicings = chordForm.getVoicings(chordRoot, key,
            style.getVoicingType());
//System.out.println("chord = " + chord + ", voicings = " + voicings);
    // pick out the good voicings based on the previous chord and
    // the range
    
    voicings = chooseVoicings(lastChord, voicings,
            style.getChordLow(), style.getChordHigh());
//System.out.println("chord = " + chord + ", voicings after choosing = " + voicings);

    // if none of the specified voicings fit in the range
    // or no voicings are specified, then generate voicings
    if( voicings.isEmpty() )
      {
      voicings = chordForm.generateVoicings(chordRoot, key);

      if( voicings.isEmpty() )
        {
        return null;
        }

      Polylist preferredVoicings = chooseVoicings(lastChord, voicings,
              style.getChordLow(), style.getChordHigh());

      if( preferredVoicings.nonEmpty() )
        {
        voicings = preferredVoicings;
        }

      // if there still is no good voicing, print out an error and return null
      if( voicings.isEmpty() )
        {
        if( verbose )
          {
          ErrorLog.log(ErrorLog.SEVERE,
                  "Range too small to voice chord: " + chord);
          }
        return null;
        }
      }
    return voicings;
    }
  }


/**
 * Choose appropriate voicings from a list of voicings.
 * @param lastChord a Polylist containing the last chord voicing
 * @param voicings  a Polylist containing voicings to choose from
 * @param lowNote   a NoteSymbol determining the lower end of the range
 * @param highNote  a NoteSymbol determining the high end of the range
 * @return a Polylist of appropriate voicings
 */
public static Polylist chooseVoicings(Polylist lastChord, Polylist voicings,
                                      NoteSymbol lowNote, NoteSymbol highNote)
  {
  PolylistBuffer goodVoicings = new PolylistBuffer();

  int smallestAverageLeap = 127;

  for( PolylistEnum venum = voicings.elements(); venum.hasMoreElements();)
    {
    Polylist voicing = (Polylist)venum.nextElement();
    
//System.out.println("in chooseVoicings " + voicing + ", lastChord = " + lastChord);
    Polylist v = (Polylist)voicing.first();
    Polylist e = (Polylist)voicing.second();

    // put the voicing near the previous chord and within range
    Polylist L = placeVoicing(lastChord, v, e, lowNote, highNote);

    // if the voicing can't be placed, it is bad, so we just continue 
    if( L == null )
      {
      continue;
      }

    v = (Polylist)L.first();
    e = (Polylist)L.second();

    // find the averageLeap between the last chord and this
    // voicing plus its extension

    int leap = averageLeap(v.append(e), lastChord);

    /*
    leap += averageLeap(lastChord,v.append(e));
    leap /= 2;
     */

    if( leap < smallestAverageLeap )
      {
      smallestAverageLeap = leap;
      goodVoicings.append(Polylist.list(v, e));
      }
    else if( leap == smallestAverageLeap )
      {
      goodVoicings.append(Polylist.list(v, e));
      }
    }
  return goodVoicings.toPolylist();
  }


/**
 * Takes a voicing and places it just above another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @return a Polylist containing the placed voicing
 */
 public static Polylist placeVoicingAbove(Polylist lastChord,
                                         Polylist voicing)
  {
  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int difference = lastNote.getMIDI() - voicingNote.getMIDI();

  Polylist newVoicing;
  if( difference > 0 )
    {
    int octaves = (difference / 12) + 1;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else if( difference <= -12 )
    {
    int octaves = difference / 12;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else
    {
    newVoicing = voicing;
    }
  return newVoicing;
  }


/**
 * Takes a voicing and places it just below another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @return a Polylist containing the placed voicing
 */
public static Polylist placeVoicingBelow(Polylist lastChord,
                                         Polylist voicing)
  {
  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int difference = lastNote.getMIDI() - voicingNote.getMIDI();

  Polylist newVoicing;
  
  if( difference < 0 )
    {
    int octaves = (difference / 12) - 1;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else if( difference >= 12 )
    {
    int octaves = difference / 12;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else
    {
    newVoicing = voicing;
    }
  return newVoicing;
  }


/**
 * Takes a voicing and places it near another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @param extension a Polylist containing a voicing extension
 * @param low       a NoteSymbol determining the low end of the range
 * @param high      a NoteSymbol determining the high end of the range
 * @return a Polylist containing the placed voicing and its extension
 */
public static Polylist placeVoicing(Polylist lastChord, Polylist voicing,
                                    Polylist extension,
                                    NoteSymbol low, NoteSymbol high)
  {
  NoteSymbol oldNote = (NoteSymbol)voicing.first();
  voicing = placeVoicing(lastChord, voicing, low, high);
  if( voicing == null )
    {
    return null;
    }
  NoteSymbol newNote = (NoteSymbol)voicing.first();
  int diff = newNote.getMIDI() - oldNote.getMIDI();
  extension = NoteSymbol.transposeNoteSymbolList(extension, diff);
  return Polylist.list(voicing, extension);
  }


/**
 * Takes a voicing and places it near another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @param low       a NoteSymbol determining the low end of the range
 * @param high      a NoteSymbol determining the high end of the range
 * @return a Polylist containing the placed voicing
 */
public static Polylist placeVoicing(Polylist lastChord, Polylist voicing,
                                    NoteSymbol low, NoteSymbol high)
  {
  if( lastChord.isEmpty() )
    {
    lastChord = Polylist.list(low, high); // rk 8/6/07
    }

  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int semitones = lastNote.getSemitonesAbove(voicingNote);
  Polylist v;

  if( semitones >= 6 )
    {
    v = placeVoicingBelow(lastChord, voicing);
    }
  else
    {
    v = placeVoicingAbove(lastChord, voicing);
    }
  NoteSymbol lowest = NoteSymbol.getLowest(v);
  NoteSymbol highest = NoteSymbol.getHighest(v);

  while( lowest.getMIDI() < low.getMIDI() )
    {
    v = NoteSymbol.transposeNoteSymbolList(v, 12);
    lowest = NoteSymbol.getLowest(v);
    highest = NoteSymbol.getHighest(v);
    if( highest.getMIDI() > high.getMIDI() )
      {
      return null;
      }
    }

  while( highest.getMIDI() > high.getMIDI() )
    {
    v = NoteSymbol.transposeNoteSymbolList(v, -12);
    lowest = NoteSymbol.getLowest(v);
    highest = NoteSymbol.getHighest(v);
    if( lowest.getMIDI() < low.getMIDI() )
      {
      return null;
      }
    }

  return v;
  }


/**
 * Takes two chord voicings and calculates the "average leap" 
 * between the two.
 * The "average leap" is the average "smallest leap" between individual
 * notes in chord2 and all of chord1.
 * @param chord1     a Polylist of a chord to compare
 * @param chord2     a Polylist of a chord to compare
 * @return the "average leap" between the two chords
 * @see #smallestLeap(Polylist,NoteSymbol)
 */
public static int averageLeap(Polylist chord1, Polylist chord2)
  {
  int sum = 0;
  int num = chord2.length();
  
  while( chord2.nonEmpty() ) //( int i = 0; i < chord2.length(); i++ )
    {
    NoteSymbol note = (NoteSymbol)chord2.first();
    int leap = smallestLeap(chord1, note);
    sum += leap;
    chord2 = chord2.rest();
    }

  return (int)((double)sum / num);
  }


/**
 * Takes a chord and a note and computes the smallest leap from
 * the note to a note in the chord.
 * @param chord     a Polylist containing the chord to compare
 * @param note      a NoteSymbol of the note to compare
 */
public static int smallestLeap(Polylist chord, NoteSymbol note)
  {
  int noteMIDI = note.getMIDI();
  int smallestLeap = 127;
  while( chord.nonEmpty() )
    {
    NoteSymbol chordNote = (NoteSymbol)chord.first();
    int leap = Math.abs(chordNote.getMIDI() - noteMIDI);
    if( leap < smallestLeap )
      {
      smallestLeap = leap;
      }
    chord = chord.rest();
    }

  return smallestLeap;
  }

//Added summer2007 for use with Style GUI
public String forGenerator()
  {
  StringBuilder rule = new StringBuilder();
  
  for( int i = 0; i < durations.size(); i++ )
    {
        if( rules.get(i).equals(STRIKE_STRING) || rules.get(i).equals(REST_STRING) || rules.get(i).equals(VOLUME_STRING) )
        {
            String nextNote = rules.get(i);
            rule.append(nextNote);
            rule.append(durations.get(i));
            rule.append(" "); 
        }
        else
        {
            String nextNote = rules.get(i);
            rule.append("(X ");
            rule.append(nextNote);
            rule.append(" ");
            rule.append(durations.get(i));
            rule.append(") ");
        }
    }
  return rule.toString();
  }


/**
 * Get the "push" amount for this pattern, in slots
 */

public int getPushAmount()
  {
    return pushAmount;
  }


public String getPushString()
  {
    return pushString;
  }

@Override
public String toString()
  {
    return "ChordPattern: " + rules + " " + durations;
  }

public String getName()
    {
    return patternName;
    }

public LinkedHashMap getDefinedRules()
{
    return definedRules;
}

public void setDefinedRules(LinkedHashMap map)
{
    if( map.isEmpty() )
    {
        return;
    }
    else
    {
        definedRules = map;
    //System.out.println("chord defined rules " + getDefinedRules());
    }
}

}
