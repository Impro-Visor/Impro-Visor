/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
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

package imp.style;

import imp.midi.MidiSequence;
import imp.midi.MidiSynth;
import imp.style.stylePatterns.BassPattern;
import imp.style.stylePatterns.Pattern;
import imp.style.stylePatterns.DrumPattern;
import imp.style.stylePatterns.Interpolant;
import imp.style.stylePatterns.ChordPatternVoiced;
import imp.style.stylePatterns.ChordPattern;
import imp.style.stylePatterns.Interpolable;
import imp.style.stylePatterns.Substitution;
import imp.data.advice.Advisor;
import imp.Constants;
import static imp.Constants.BEAT;
import static imp.Constants.CUSTOM;
import static imp.Constants.ENDSCORE;
import static imp.Constants.MAX_VOLUME;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.ChordSymbol;
import imp.data.DrumLine;
import imp.data.Duration;
import imp.data.Leadsheet;
import imp.data.MelodyPart;
import imp.data.MelodySymbol;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.Part;
import imp.data.PitchClass;
import imp.data.Rest;
import imp.data.Transposition;
import imp.data.VolumeSymbol;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.util.Preferences;
import imp.voicing.AVPFileCreator;
import imp.voicing.AutomaticVoicingSettings;
import imp.voicing.HandManager;
import imp.voicing.VoicingGenerator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.PolylistEnum;


/**
 * An object that contains patterns and parameters for generating an
 * accompaniment.
 * Contains functions to create a Style from text, output a Style to text,
 * and, given a ChordPart, arrange patterns to construct an accompaniment.
 * @see         Pattern
 * @see         BassPattern
 * @see         DrumPattern
 * @see         ChordPattern
 * @see         ChordPart
 * @author      Stephen Jones, Robert Keller
 */
public class Style
        implements Constants, Serializable
  {
  private static LinkedHashMap<String, Style> allStyles = new LinkedHashMap<String, Style>();
  
  private static ArrayList<Style> orderedStyles = null;
  
  private static String defaultStyleName = "no-style";
  
  public static final String USE_PREVIOUS_STYLE = "*";
  
  private static int defaultDrumPatternDuration = 480;
  
  private AutomaticVoicingSettings avs;

  private VoicingGenerator vgen;
  
  private HandManager handyMan;

  /**
   * the random number generator for styles
   */
  private static Random gen = new Random();

  /**
   * a String containing the name
   */
  private String name = defaultStyleName;

  /**
   * a String containing the default name of a Style (a so-called NULL Style)
   */
  public static String NULL = "";

  /**
   * a boolean that determines whether to use "no-style" behavior
   */
  private boolean noStyle = false;

  /**
   * a String containing comments on the Style
   */
  private String comments = "";

  /**
   * a double containing the swing value
   */
  private double swing = 0.5;

  /**
   * a double containing the swing value
   */
  private double accompanimentSwing = 0.5;

  /**
   * a String determining the voicing type
   */
  private String voicingType = "closed";
  
  /**
   * The name of the voicing file to use for the style
   */  
  private String voicingFileName = "default.fv";
  /**
   * a boolean that determines whether to automatically extend chords
   */
  private boolean useExtensions = false;

  /**
   * a Polylist of NoteSymbol objects that determine the base chord from 
   * which to voice-lead
   */
  private Polylist chordBase = Polylist.list(
          NoteSymbol.makeNoteSymbol("c-"),
          NoteSymbol.makeNoteSymbol("e-"),
          NoteSymbol.makeNoteSymbol("g-"));

  /**
   * a NoteSymbol determining the lower range of the chord progression
   */
  private NoteSymbol chordLow = NoteSymbol.makeNoteSymbol("c-");

  /**
   * a NoteSymbol determining the upper range of the chord progression
   */
  private NoteSymbol chordHigh = NoteSymbol.makeNoteSymbol("a");

 
  /**
   * an int determining the MIDI instrument for chords
   */
  static private int chordInstrument = 1;

  /**
   * an int determining the MIDI instrument for bass
   */
  static private int bassInstrument = 33;

  /**
   * a NoteSymbol determining the lower range for bass
   */
  private NoteSymbol bassLow = NoteSymbol.makeNoteSymbol("g---");

  /**
   * a NoteSymbol determining the higher range for bass
   */
  private NoteSymbol bassHigh = NoteSymbol.makeNoteSymbol("g-");

  /**
   * a ArrayList of this Style's BassPattern objects
   */
  private ArrayList<BassPattern> bassPatterns = new ArrayList<BassPattern>();

  /**
   * a ArrayList of this Style's DrumPattern objects
   */
  private ArrayList<DrumPattern> drumPatterns = new ArrayList<DrumPattern>();

  /**
   * a ArrayList of this Style's ChordPattern objects
   */
  private ArrayList<ChordPattern> chordPatterns = new ArrayList<ChordPattern>();
  
  private Polylist interpolations = Polylist.nil;
  
  private Polylist interpolables = Polylist.nil;
  
  private Polylist substitutions = Polylist.nil;
  
  private Polylist definedInterpolations = Polylist.nil;
  
  private Polylist definedInterpolables = Polylist.nil;
  
  private Polylist definedSubstitutions = Polylist.nil;
  
  /**
   * HashMaps for each of the different instruments to save the rules defined
   * outside the patterns
   */
  private LinkedHashMap<String, Polylist> bassDefinedRules = 
          new LinkedHashMap<String, Polylist>();
  
  private LinkedHashMap<String, Polylist> chordDefinedRules = 
          new LinkedHashMap<String, Polylist>();
  
  private LinkedHashMap<String, Polylist> drumDefinedRules = 
          new LinkedHashMap<String, Polylist>();

  /**
   * a String array containing keywords used in Style specifications
   */
  private static String keyword[] = {"name", "bass-pattern", "bass-high",
                                       "bass-low", "bass-base", "swing",
                                       "drum-pattern", "chord-pattern",
                                       "chord-high", "chord-low", "chord-base",
                                       "use-extensions", "no-style",
                                       "voicing-type", "comments",
                                       "comp-swing", "define-rule", "bass",
                                       "chord", "drum", "voicing-name", "interpolate",
                                       "interpolable", "substitute"
  };

  // indices into the keyword array
  private static final int NAME = 0;

  private static final int BASS_PATTERN = 1;

  private static final int BASS_HIGH = 2;

  private static final int BASS_LOW = 3;

  private static final int BASS_BASE = 4;

  private static final int SWING = 5;

  private static final int DRUM_PATTERN = 6;

  private static final int CHORD_PATTERN = 7;

  private static final int CHORD_HIGH = 8;

  private static final int CHORD_LOW = 9;

  private static final int CHORD_BASE = 10;

  private static final int USE_EXTENSIONS = 11;

  private static final int NO_STYLE = 12;

  private static final int VOICING_TYPE = 13;

  private static final int COMMENTS = 14;

  private static final int ACCOMPANIMENT_SWING = 15;
  
  private static final int DEFINE_RULE = 16;
  
  private static final int BASS = 17;
  
  private static final int CHORD = 18;
  
  private static final int DRUM = 19;
  
  private static final int VOICING_FILE = 20;

  private static final int INTERPOLATE = 21;
  
  private static final int INTERPOLABLE = 22;

  private static final int SUBSTITUTE = 23;

  public boolean usePreviousStyle()
    {
      return name.equals(USE_PREVIOUS_STYLE);
    }
  
  public static Style getStyle(String name)
    {
    //System.out.println("getStyle " + name);
     Style s = allStyles.get(name);

      return s;
    }
  
  public static void setStyle(String name, Style style)
    {
      allStyles.put(name, style);
    }
  
  public static boolean noStyles()
    {
      return numberOfStyles() == 0;
    }
  
  public static int numberOfStyles()
    {
      ensureStyleArray();
      return orderedStyles.size(); 
    }
  
  /**
   * Used by StyleList in Notate.
   * @param index
   * @return 
   */
  
    public static Style getNth(int index)
      {
        ensureStyleArray();
        return orderedStyles.get(index);
      }
    
    private static void ensureStyleArray()
      {
        //if( orderedStyles == null )
            {
            orderedStyles = new ArrayList<Style>(allStyles.values());
            }       
      }
  
  /**
   * Gets the voicing type.
   * @return the voicing type
   */
  public String getVoicingType()
    {
    return voicingType;
    }

  public boolean hasCustomVoicing()
    {
      return voicingType.equals(CUSTOM);
    }
  
  public ArrayList<BassPattern> getBP()
    {
    return bassPatterns;
    }

  public ArrayList<DrumPattern> getDP()
    {
    return drumPatterns;
    }

  public ArrayList<ChordPattern> getCP()
    {
    return chordPatterns;
    }

  public Polylist getInterpolations()
  {
      return interpolations;
  }
  
  public Polylist getInterpolables()
  {
      return interpolables;
  }
  
  public Polylist getSubs()
  {
      return definedSubstitutions;
  }
  
  public Polylist getDefinedInterpolations()
  {
      return definedInterpolations;
  }
  
  public Polylist getDefinedInterpolables()
  {
      return definedInterpolables;
  }
  
  public Polylist getDefinedSubs()
  {
      return definedSubstitutions;
  }

  public int getDrumPatternDuration()
    {
    if( drumPatterns.size() > 0 )
      {
      return drumPatterns.get(0).getDuration();
      }
    else
      {
      return defaultDrumPatternDuration;
      }
    }

  /**
   * Returns the number of total patterns--all of bass, chords, and drums.
   *
   */
  public int getTotalPatterns()
    {
    return bassPatterns.size() + chordPatterns.size() + drumPatterns.size();
    }


  /**
   * Gets the name.
   * @return the name
   */
  public String getName()
    {
    return name;
    }
  
  public void setName(String name)
    {
      this.name = name;
    }

  /**
   * Gets the comments.
   * @return the comments
   */
  public String getComments()
    {
    return comments;
    }

  /**
   * Sets the comments.
   * @param c         a String containing the comments
   */
  public void setComments(String c)
    {
    comments = c;
    }

  /**
   * Returns the name.
   * @return the name of this Style
   */
  @Override
  public String toString()
    {
    return getName();
    }

  /**
   * Returns the swing value.
   * @return the swing value
   */
  public double getSwing()
    {
    return swing;
    }

  /**
   * Returns the accompaniment swing value.
   * @return the accompaniment swing value
   */
  public double getAccompanimentSwing()
    {
    //System.out.println("accompanimentSwing = " + accompanimentSwing);
    return accompanimentSwing;
    }

  /**
   * Sets the swing value.
   * @param s         a double containing the swing value
   */
  public void setSwing(double s)
    {
      //System.out.println("setting swing of " + name + " to " + s);
    swing = s;
    }

  /**
   * Sets the accompaniment swing value.
   * @param s         a double containing the accompaniment swing value
   */
  public void setAccompanimentSwing(double s)
    {
    accompanimentSwing = s;
    }

  /**
   * Sets the chord instrument.
   * @param inst      an int containing the chord instrument
   */
  public void setChordInstrument(int inst, String caller)
    {
    chordInstrument = inst;
    }

  /**
   * Gets the chord instrument.
   * @return the chord instrument
   */
  public int getChordInstrument()
    {
    return chordInstrument;
    }

  /**
   * Sets the bass instrument.
   * @param inst      an int containing the chord instrument
   */
  public void setBassInstrument(int inst)
    {
    bassInstrument = inst;
    }

  /**
   * Gets the bass instrument.
   * @return the bass instrument
   */
  public int getBassInstrument()
    {
    return bassInstrument;
    }
  
  /**
   * gets the defined rules for each instrument
   * @return the Linked Hash Map of rules
   */
  public LinkedHashMap getBassDefinedRules()
  {
      return bassDefinedRules;
  }
  
  public LinkedHashMap getChordDefinedRules()
  {
      return chordDefinedRules;
  }
  
  public LinkedHashMap getDrumDefinedRules()
  {
      return drumDefinedRules;
  }

    /**
     * 
     * @return voicing file name to search for in voicing directory
     */
    public String getVoicingFileName() {
        return voicingFileName;
    }
    /**
     * 
     * @param voicingFileName voicing file name to search for in voicing directory
     */
    public void setVoicingFileName(String voicingFileName) {
        this.voicingFileName = voicingFileName;
    }
    
  /**
   * Returns the noStyle parameter.
   * @return determines whether this is a "no-Style"
   */
  public boolean noStyle()
    {
    return noStyle;
    }

  /**
   * Sets the noStyle parameter.
   * @param basslineSegment         a boolean determining whether this is a "no-style"
   */
  public void setNoStyle(boolean b)
    {
    noStyle = b;
    }

  /**
   * Creates a default Style (considered a NULL Style).
   */
  public Style()
    {
    }

  /**
   * Returns a copy of the Style.
   * @return a copy of the Style
   */
  public Style copy()
    {
    Style style = new Style();

    style.noStyle = noStyle;
    style.setSwing(swing);
    style.setAccompanimentSwing(accompanimentSwing);
    style.chordBase = chordBase;
    style.chordLow = chordLow;
    style.chordHigh = chordHigh;
    style.bassLow = bassLow;
    style.bassHigh = bassHigh;
    style.comments = comments;
    style.voicingType = voicingType;
    style.voicingFileName=voicingFileName;
    style.useExtensions = useExtensions;

    style.name = name;

    style.bassPatterns = bassPatterns;
    style.drumPatterns = drumPatterns;
    style.chordPatterns = chordPatterns;
    style.interpolations = interpolations;
    style.interpolables = interpolables;
    style.substitutions = substitutions;
    return style;
    }

  /**
   * A factory for creating a new Style from a Polylist.
   * @param  L        a Polylist containing Style information
   * @return the Style created from the Polylist, or null if there
   *         was a problem
   */
  public static Style makeStyle(Polylist L)
    {
    Style style = new Style();
    //interpolations = Polylist.nil;
    //style.voicingFileName="default.avp";
    while( L != null && L.nonEmpty() )
      {
      if( (L.first() instanceof Polylist) )
        {
        Polylist item = (Polylist)L.first();
        L = L.rest();

        if( item.nonEmpty() )
          {
          Object dispatcher = item.first();
          item = item.rest();

          switch( Leadsheet.lookup((String)dispatcher, keyword) )
            {
              case DEFINE_RULE:
              {
                  style.makeDefinedRules(item); 
                  break;
              }
            case CHORD_PATTERN:
              {
                  ChordPattern cp = new ChordPattern();
                  cp.setStyle(style);
                  cp.setDefinedRules(cp.getStyle().getChordDefinedRules());
                  cp.makePattern(item);
                style.chordPatterns.add(cp);
              break;
              }
            case DRUM_PATTERN:
              {
              DrumPattern dp = new DrumPattern();
              dp.setStyle(style);
              dp.setDefinedRules(dp.getStyle().getDrumDefinedRules());
              dp.makePattern(item);
                style.drumPatterns.add(dp);
              break;
              }
            case BASS_PATTERN:
              {
                  BassPattern bp = new BassPattern();
                  bp.setStyle(style);
                  bp.setDefinedRules(bp.getStyle().getBassDefinedRules());
                  bp.makePattern(item);
                style.bassPatterns.add(bp);
              break;
              }
            case VOICING_TYPE:
              {
              style.voicingType = (String)item.first();
              break;
              }
            case COMMENTS:
              {
              String commentsString = Leadsheet.concatElements(item);
              style.comments = commentsString;
              break;
              }
            case NAME:
              {
              style.name = (String)item.first();
              break;
              }
            case VOICING_FILE:
              {
              style.voicingFileName = (String)item.first();
              break;
              }
            case INTERPOLATE:
            {
              //  System.out.println(item);
                Interpolant interpolant = Interpolant.makeInterpolantFromExp(item);
                style.definedInterpolations = style.definedInterpolations.cons(item.cons("interpolate "));
              //  System.out.println(interpolant);
                style.interpolations = style.interpolations.cons(interpolant);
                break;
            }
            case INTERPOLABLE:
            {
                Interpolable interpolable = Interpolable.makeInterpolableFromExp(item);
                style.definedInterpolables = style.definedInterpolables.cons(item.cons("interpolable "));
                style.interpolables = style.interpolables.cons(interpolable);
                break;
            }
            case SUBSTITUTE:
            {
              //  System.out.println(item);
                Substitution sub = Substitution.makeSubstitutionFromExp(item);
                style.definedSubstitutions = style.definedSubstitutions.cons(item.cons("substitute "));
              //  System.out.println(sub);
                style.substitutions = style.substitutions.cons(sub);
                break;
            }
            default:
              {
              style.load((String)dispatcher, item);
              break;
              }
            }
          }
        }
      else
        {
        L = L.rest();
        }
      }
    
    if( style.hasCustomVoicing() )
        {
        String vfn = ImproVisor.getVoicingDirectory() + File.separator + style.voicingFileName;
        AutomaticVoicingSettings av = new AutomaticVoicingSettings();
        AVPFileCreator.fileToSettings(new File(vfn), av);
        style.avs = av;
        
        style.handyMan = new HandManager();
        style.handyMan.getSettings(av);
        
        style.vgen = new VoicingGenerator();
        style.vgen.getVoicingSettings(av);
        }
    
     return style;
    }

  public HandManager getHandManager()
  {
      return handyMan;
  }
  
  public VoicingGenerator getVoicingGenerator()
  {
      return vgen;
  }
  
  /**
   * A method to change parameters of an already constructed Style
   * from text specification.
   * @param dispatcher        a String containing a Style keyword
   * @param item              a Polylist containing the arguments for
   *                          dispatcher's Style keyword
   */
  public void load(String dispatcher, Polylist item)
    {
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case BASS_HIGH:
        {
        bassHigh = NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case BASS_LOW:
        {
        bassLow = NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
       case CHORD_HIGH:
        {
        chordHigh = NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case CHORD_LOW:
        {
        chordLow = NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case CHORD_BASE:
        {
        PolylistEnum chord = item.elements();
        
        PolylistBuffer base = new PolylistBuffer();
        
        while( chord.hasMoreElements() )
          {
          NoteSymbol note =
                  NoteSymbol.makeNoteSymbol((String)chord.nextElement());
          
          base.append(note);
          }
        chordBase = base.toPolylist();
        break;
        }
      case SWING:
        {
        swing = (Double)item.first();
        break;
        }
      case ACCOMPANIMENT_SWING:
        {
        accompanimentSwing = (Double)item.first();
        break;
        }
      case USE_EXTENSIONS:
        {
        useExtensions = true;
        break;
        }
      case NO_STYLE:
        {
        noStyle = true;
        break;
        }
      }
    }
  
  /**
   * A method to add defined rules to the hash map that tracks them
   * @param L 
   */
  public void makeDefinedRules(Polylist L)
  {
      //e.g. L is (drum name (rules X4 R4 X4 R4))
      
      if( L.nonEmpty() )
      {
          if( L.first() instanceof String )
          {
              String dispatcher = (String) L.first();
              Polylist item = L.rest();
              
              switch( Leadsheet.lookup(dispatcher, keyword) )
              {
                  case BASS:
                  {
                      if( item.first() instanceof String )
                      {
                          String ruleName = (String) item.first();
                          Polylist rules = (Polylist) item.second();
                          bassDefinedRules.put(ruleName, rules);
                      }
                      break;
                  }
                      
                  case CHORD:
                  {
                      if( item.first() instanceof String )
                      {
                          String ruleName = (String) item.first();
                          Polylist rules = (Polylist) item.second();
                          chordDefinedRules.put(ruleName, rules);
                      }
                      break;
                  }
                      
                  case DRUM:
                  {
                      if( item.first() instanceof String )
                      {
                          String ruleName = (String) item.first();
                          Polylist rules = (Polylist) item.second();
                          drumDefinedRules.put(ruleName, rules);
                      }
                      break;
                  }
              }
          }
      }
  }

  /**
   * Saves a Style to text format used in Leadsheets.
   * @param out       a BufferedWriter to write the Style to
   */
  public void saveLeadsheet(BufferedWriter out) throws IOException
    {
    out.write("(style " + name);
    out.newLine();
    out.write("    (" + keyword[SWING] + " " + swing + ")");
    out.newLine();
    out.write("    (" + keyword[ACCOMPANIMENT_SWING] + " " + swing + ")");
    out.newLine();
    out.write("    (" + keyword[BASS_HIGH] + " " + bassHigh.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[BASS_LOW] + " " + bassLow.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[CHORD_HIGH] + " " + chordHigh.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[CHORD_LOW] + " " + chordLow.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[VOICING_FILE] + " " + this.voicingFileName + ")");
    out.newLine();
    out.write("    " + NoteSymbol.makePitchStringList(chordBase).cons(keyword[CHORD_BASE]));
    out.newLine();

    if( noStyle )
      {
      out.write("    (" + keyword[NO_STYLE] + ")");
      }

    out.write(")");
    out.newLine();
    }

  /**
   * Sets the base chord.
   * @param list      a Polylist containing NoteSymbol objects that make up
   *                  the base chord
   */
  public void setChordBase(Polylist list)
    {
    chordBase = list;
    }

  /**
   * Sets the lower range for the chords.
   * @param low       a NoteSymbol determining the lower range for the chords
   */
  public void setChordLow(NoteSymbol low)
    {
    chordLow = low;
    }

  /**
   * Sets the higher range for the chords.
   * @param high      a NoteSymbol determining the higher range for the
   *                  chords
   */
  public void setChordHigh(NoteSymbol high)
    {
    chordHigh = high;
    }

  /**
   * Gets the chord base.
   * @return the chord base
   */
  public Polylist getChordBase()
    {
    return chordBase;
    }

  /**
   * Gets the upper range NoteSymbol.
   * @return the upper range
   */
  public NoteSymbol getChordHigh()
    {
    return chordHigh;
    }

  /**
   * Gets the lower range NoteSymbol.
   * @return the lower range
   */
  public NoteSymbol getChordLow()
    {
    return chordLow;
    }

  /**
   * Gets the upper range bass note.
   * @return the bass upper range
   */
  public NoteSymbol getBassHigh()
    {
    return bassHigh;
    }

  /**
   * Gets the lower range bass note.
   * @return the bass lower range
   */
  public NoteSymbol getBassLow()
    {
    return bassLow;
    }

  /**
   * Function that takes a ArrayList of Pattern objects and a duration, 
   * randomly chooses from the largest Patterns that will fit in that
   * duration, and returns that Pattern.
   * @param <T>       a type variable (referring to a type of Pattern)
   * @param patterns  a ArrayList of T objects to choose from
   * @param desiredDuration  an int determining the desiredDuration to fill
   * @return the Pattern chosen
   */
  private <T extends Pattern> T getPattern(ArrayList<T> patterns,
                                           int desiredDuration)
    {
    ArrayList<T> goodPatterns = new ArrayList<>();

    // find the largest pattern desiredDuration that is less than desiredDuration
    int largestDuration = 0;
    for( int i = 0; i < patterns.size(); i++ )
      {
      T temp = patterns.get(i);
      int tempDuration = temp.getDuration();

      if( tempDuration > largestDuration &&
              tempDuration <= desiredDuration )
        {
        largestDuration = tempDuration;
        }
      }

    if( largestDuration == 0 )
      {
      // NEW: Instead of playing nothing, find the shortest pattern
      // that is longer than desiredDuration and splitChordPattern its desiredDuration.
      T shortestPattern = patterns.get(0);
      int shortestDuration = shortestPattern.getDuration();

      for( int i = 1; i < patterns.size(); i++ )
        {
        T temp = patterns.get(i);
        int tempDuration = temp.getDuration();

        if( tempDuration >= desiredDuration &&
                tempDuration < shortestDuration )
          {
          shortestPattern = temp;
          shortestDuration = tempDuration;
          }
        }
      return shortestPattern;
      }

    // sum the weights of the patterns we are choosing from
    double sum = 0;
    for( int i = 0; i < patterns.size(); i++ )
      {
      if( patterns.get(i).getDuration() == largestDuration )
        {
        sum += patterns.get(i).getWeight();
        goodPatterns.add(patterns.get(i));
        }
      }

    // randomly choose one of the "good patterns"
    int random = gen.nextInt((int)sum);
    double weights = 0;
    for( int i = 0; i < goodPatterns.size(); i++ )
      {
      weights += goodPatterns.get(i).getWeight();
      if( random < weights )
        {
        return goodPatterns.get(i);
        }
      }
    // should not occur
    return null;
    }
  
  ChordPattern residualChordPattern = null;
  
  private void setResidualChordPattern(ChordPattern pattern)
  {
      if( pattern != null && pattern.getDuration() == 0 )
        {
          pattern = null;
        }
      //System.out.println("residualChordPattern = " + pattern);
      residualChordPattern = pattern;
  }
  
  /**
   * Similar to getPattern, but specialized to ChordPattern
   * @param <T>       a type variable (referring to a type of Pattern)
   * @param patterns  a ArrayList of T objects to choose from
   * @param desiredDuration  an int determining the desiredDuration to fill
   * @return the Pattern chosen
   */
  private ChordPattern getChordPattern(ArrayList<ChordPattern> patterns,
                                           int desiredDuration)
    {
        //System.out.println("\ngetChordPattern of duration " + desiredDuration);
        // If there is a residual pattern, use it.
        if( residualChordPattern != null )
          {
            if( residualChordPattern.getDuration() <= desiredDuration )
              {
                ChordPattern result = residualChordPattern;
                //System.out.println("Using all of residual: " + result);
                setResidualChordPattern(null);
                return result;
              }
            ArrayList<ChordPattern> result = residualChordPattern.
                    splitChordPattern(desiredDuration);
            setResidualChordPattern(result.get(1));
            return result.get(0);
          }

        // Otherwise search for a pattern of duration >= the desired duration.
        ArrayList<ChordPattern> goodPatterns = new ArrayList<>();

        /*
         * begin commented out portion
         */
        double goodSum = 0;
        double allSum = 0;
        for( ChordPattern pattern : patterns )
          {
            double weight = pattern.getWeight();
            allSum += weight;
            if( pattern.getDuration() > desiredDuration )
              {
                goodPatterns.add(pattern);
                goodSum += weight;
              }
          }

        double weights = 0;
        if( goodSum > 0 )
          {
            // randomly choose one of the "good patterns"
            int random = gen.nextInt((int) goodSum);
            for( ChordPattern pattern : goodPatterns )
              {
                weights += pattern.getWeight();
                if( random <= weights )
                  {
                    ChordPattern selectedPattern = pattern;
                    if( selectedPattern.getDuration() == desiredDuration )
                      {
                        // If selectedPattern fits exactly, use it.
                        return selectedPattern;
                      }
                    // Otherwise split the selected pattern and use the first half.
                    ArrayList<ChordPattern> result = selectedPattern.
                            splitChordPattern(desiredDuration);
                    setResidualChordPattern(result.get(1));
                    return result.get(0);
                  }
              }
          }
        else
          {
            if( allSum == 0 )
              {
                return null; // no pattern
              }
            int random = gen.nextInt((int) allSum);
            for( ChordPattern pattern : patterns )
              {
                weights += pattern.getWeight();
                if( random <= weights )
                  {
                    if( pattern.getDuration() <= desiredDuration )
                      {
                        return pattern;
                      }
                    // Otherwise split the selected pattern and use the first half.
                    ArrayList<ChordPattern> result = pattern.splitChordPattern(desiredDuration);
                    setResidualChordPattern(result.get(1));
                    return result.get(0);
                  }
              }
          }
    
    // As a last result, select from all patterns regardless of duration
    
    System.out.println("no ChordPattern of duration " + desiredDuration + " found");
    return null;
    }


  /**
   * Called from render in this file.
   * 
   * Using the DrumPattern objects of this Style, sequences a drumline
   * of a specified duration onto the track.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put drum events on
   * @param time      a long containing the time to start the drumline
   * @param duration  an int containing the duration of the drumline
   */
  private void makeDrumline(MidiSequence seq, 
                            long time,
                            int duration, 
                            int endLimitIndex )
          throws InvalidMidiDataException
    {
    // tracing render info
    //System.out.println("drumline: time = " + time + " duration = " + duration
    // + " endLimitIndex = " + endLimitIndex);

    // loop until we've found patterns to fill up the duration
    while( duration > 0 )
      {
      // Get a drum pattern, if any

      DrumPattern pattern = getPattern(drumPatterns, duration);
      //System.out.println("pattern = " + pattern + ", duration = " + duration);
      // if there's no suitable pattern, play nothing
      if( pattern == null )
        {
        break;
        }

      int patternDuration = pattern.getDuration();
      duration -= patternDuration;

      // we get a Polylist containing drum parts
      
      DrumLine drumline = pattern.applyRules();
      
      //System.out.println("drumline = " + drumline);

      // Each element of the Polylist is a drum part in the form of a MelodyPart
      // so we go through and render each element
      
      for( MelodyPart d: drumline.getParts() )
        {
        d.setSwing(accompanimentSwing);
        Track track = seq.getDrumTrack(d.getInstrument());
        d.makeSwing();
        d.render(seq, ImproVisor.getDrumChannel(), time, track, 0, endLimitIndex);
        }
      
      time += (patternDuration * seq.getResolution()) / BEAT;
      }
    }

  /**
   * Below is a check to decide whether to continue sequencing.
   * It is used in multiple files.
   * Sequencing should continue if either play-to-end was specified
   * or the end of select is reached.
   * In the latter case, is not desired to generate a midi render for the
   * full score, as that would have to be cut off and causes blips in the
   * sound.
   */

  public static int magicFactor = 4;

  public static boolean limitNotReached(long time, int endLimitIndex)
  {
  return true || endLimitIndex == ENDSCORE // i.e. play to end
      || time <= magicFactor*endLimitIndex; // limit not reached
  }

  public static int getMagicFactor()
  {
      return magicFactor;
  }

  /**
   * Using the ChordPattern objects of this Style, sequences a chordline
   * of a specified duration onto the track.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put currentChord events on
   * @param time      a long containing the time to start the chordline
   * @param currentChord     a ChordSymbol containing the currentChord currentChord to render
   * @param previousChord a Polylist containing the previous currentChord
   * @param duration  an int containing the duration of the chordline
   * @return a Polylist containing the last currentChord used in the chordline
   */
private Polylist makeChordline(
        MidiSequence seq,
        long time,
        Chord currentChord,
        Polylist previousChord,
        int duration,
        Transposition transposition,
        int endLimitIndex,
        boolean constantBass)
        throws InvalidMidiDataException
  {
    // To trace rendering info:
    //System.out.println("makeChordLine: time = " + time + " duration = "
    //    + duration + " endLimitIndex = " + endLimitIndex);

    // Because we have no data structure to hold multi-voice parts, 
    // we manually render polylists for each currentChord in this method.

    // Select Bank 0 before program change. 
    // Not sure this is correct. Check before releasing!
    
    Track track = seq.getChordTrack();
    
    if( Preferences.getMidiSendBankSelect())
      {
      track.add(MidiSynth.createBankSelectEventMSB(0, time));
      track.add(MidiSynth.createBankSelectEventLSB(0, time));
      }

    track.add(MidiSynth.createProgramChangeEvent(ImproVisor.getChordChannel(),
                                                 chordInstrument, 
                                                 time));

    ChordSymbol symbol = currentChord.getChordSymbol();

    // The while loop is in case one pattern does not fill
    // the required duration. We may need multiple patterns.
    
    boolean beginning = true;
    while( duration > 0 && limitNotReached(time, endLimitIndex) )
      {
        // Get a pattern for this chord.
        // A pattern can contain volume information.
        
        ChordPattern pattern = getChordPattern(chordPatterns, duration);
        //System.out.println("chord pattern = " + pattern);
        ChordPatternVoiced c;
        
        if( pattern == null  || constantBass )
          {
            // if there's no pattern, and we haven't used a previous
            // pattern on this currentChord, then just play the currentChord for the 
            // duration
            //System.out.println("pattern == null case in Style");
              
            if( !beginning )
              {
                break;
              }
            Polylist v = ChordPattern.findVoicing(symbol, previousChord, this);
            MelodyPart dM = new MelodyPart();
            dM.addNote(new Rest(duration));
            duration = 0;
            LinkedList<Polylist> L = new LinkedList<>();
            L.add(v);
            c = new ChordPatternVoiced(L, dM);
          }
        else
          {
            if( beginning )
              {
               // Accommodate possible "pushing" of first currentChord.
               // The amount is given in slots.
               int pushAmount = pattern.getPushAmount();
               int deltaT = pushAmount * seq.getResolution() / BEAT;
               time -= deltaT;
               if( time < 0 )
                  {
                    time = 0;
                    deltaT = 0;
                  }
                
                duration -= (deltaT + pattern.getDuration());
              }
            else
              {
              duration -= pattern.getDuration();
              }
            // we get a polylist containing the chords (each in a polylist)
            // and a "duration melody" which is a MelodyPart representing
            // the durations of each currentChord
            c = pattern.applyRules(symbol, previousChord, this);
            //System.out.println("\nResult of applyRules to " + symbol + ": " + c);
          }
        
        // Uncomment to show how chord patterns are voiced:
        
        // System.out.println("chord " + currentChord.getName() + ": " + c);
        
        // since we can't run the swing algorithm on a Polylist of 
        // NoteSymbols, we can use this "duration melody" which
        // corresponds to the chords in the above Polylist to find
        // the correct swung durations of the notes

        // chords is a list of lists of notes, each outer list representing
        // a chord voicing. Note that volume settings can be amongst these
        // notes.
        
        // durationMelody is the pattern, consisting of rests of various
        // durations.
        
        LinkedList<Polylist> voicings = c.getVoicings();
        MelodyPart durationMelody = c.getDurations();
        
        durationMelody.setSwing(accompanimentSwing);
        durationMelody.makeSwing();
        //System.out.println("duration melody = " + durationMelody);
        Part.PartIterator i = durationMelody.iterator();

        //System.out.println("chord line = " + chords);

        int volume = 127;
        
        //System.out.println("voicings for " + currentChord.getName() + ": " + voicings + ", durationMelody = " + durationMelody);
        for( Polylist voicing: voicings )
          {
          // Note that voicing should be a Polylist, and may contain volume
                    
            Note note = (Note) i.next();      // Note from the "duration melody"
            
           //System.out.println("voicing = " + voicing + ", note = " + note);

            int dur = note.getRhythmValue();  // A single currentChord's duration

            long offTime = time + dur * seq.getResolution() / BEAT;
        
            // render each NoteSymbol in the currentChord
            if( voicing instanceof Polylist )
              {
                Polylist filtered = filterOutVolumes(voicing);
                // Without this qualification, the voicing keyboard 
                // sometimes shows on the bass note. Not sure why yet.
                if( filtered.nonEmpty() )
                  {
                  currentChord.setVoicing(filtered);
                  }
                Polylist L = voicing;
                //System.out.println("bar " + (1 + time/1920) + ": chord = " + currentChord);            
                // All notes in the voicing are rendered at the same start time
                
                Sequence ms = seq.getSequence();
                
                while( L.nonEmpty() )
                  {
                    Object ob = L.first();
                    if( ob instanceof NoteSymbol )
                      {
                      NoteSymbol ns = (NoteSymbol)ob;
                      note = ns.toNote();
                      note.setRhythmValue(dur);
                      note.setVolume(volume);  // note of chord
                      note.render(ms, 
                                  seq.getChordTrack(), 
                                  time, offTime, 
                                  ImproVisor.getChordChannel(), 
                                  transposition.getChordTransposition());
                      }
                    else if( ob instanceof VolumeSymbol )
                      {
                       volume = ((VolumeSymbol)ob).getVolume();
                      }
                    
                    L = L.rest();
                  }

                previousChord = filtered;
              }

            time = offTime;
          }

        beginning = false;
      }

    // Un-comment this to see voicings
    //System.out.println("voicing " + currentChord + " as " + previousChord);

    //System.out.println("MIDI sequence = " + MidiFormatting.prettyFormat(sequence2polylist(seq)));
    return previousChord;
  }


static Polylist filterOutVolumes(Polylist L)
  {
    if( L.isEmpty() )
      {
        return L;
      }
    
    if( L.first() instanceof VolumeSymbol )
      {
        return filterOutVolumes(L.rest());
      }
    
    return filterOutVolumes(L.rest()).cons(L.first());
  }

  /**
   * Using the BassPattern objects of this Style, sequences a bassline
   * of a specified duration onto the track.
   * 
   * This method is called only once, from render in this same class.
   * 
   * @param bassline  a LinkedList of NoteSymbols making up the bassline so far
   * @param chord     a ChordSymbol containing the currentChord chord to render
   * @param nextChord a ChordSymbol containing the next chord
   * @param previousBassNote  a NoteSymbol containing the previous note
   * @param duration  an int containing the duration of the bassline
   * @return a Polylist of NoteSymbols to be sequenced
   */
  private void addToBassline(
          LinkedList<MelodySymbol> bassline,
          ChordSymbol chord, 
          ChordSymbol nextChord,
          NoteSymbol previousNote, 
          int duration,
          int transposition,
          boolean constantBass)
          throws InvalidMidiDataException
    {
    //System.out.println("addToBassline " + chord);
    while( duration > 0 )
      {
      BassPattern pattern = getPattern(bassPatterns, duration);
//System.out.println("makeBassLine pattern = " + pattern + ", duration = " + duration);

      // If there is no pattern, or we want to play the chord with the voicings 
      // of the current style, but without a bass pattern, as in the case
      // of stepping through chord voicings, 
      // setting constantBass to true bypasses the pattern
      // and just plays one base note, in midi range 48-59 (octave -1)
      
      if( pattern == null || constantBass )
        {
        PitchClass bassPitchClass = chord.getBass();
        int octave = -1; //See NoteSymbol for explanation
        NoteSymbol bassNote = new NoteSymbol(bassPitchClass, octave, duration);
        //System.out.println("bassNote = " + bassNote);
        if( constantBass )
          {
            bassline.add(bassNote);
          }
        break;
        }

      duration -= pattern.getDuration();

      // we get a Polylist of NoteSymbols back from the applyRules 
      // function
      LinkedList<Object> basslineSegment = duration > 0?
                              pattern.applyRules(chord, chord, previousNote)
                            : pattern.applyRules(chord, nextChord, previousNote);

      // System.out.println("basslineSegment = " + basslineSegment);

      // Find the last non-rest in the segment.
      
      Iterator<Object> it = basslineSegment.descendingIterator();
      while( it.hasNext() )
        {
           Object ob = it.next();
           if( ob instanceof NoteSymbol )
             {
               NoteSymbol ns = (NoteSymbol)ob;
               if( !ns.isRest() )
                 {
                   previousNote = ns;
                   break;
                 }
             }
        }
      
      // What does this do?
      
      if( !bassline.isEmpty() )
        {
        Object lastOb = bassline.getLast();
        if( lastOb instanceof Polylist && basslineSegment.getFirst() instanceof NoteSymbol )
          {
System.out.println("mystery code on bassline " + bassline);
            Polylist L = (Polylist)lastOb;
            String dur = (String)L.first();
            bassline.removeLast();
            NoteSymbol ns = (NoteSymbol)basslineSegment.getFirst();
            int pDur = Duration.getDuration0(dur) + ns.toNote().getRhythmValue();
            ns = new NoteSymbol(ns.getPitchClass(), ns.getOctave(), pDur, ns.getVolume());
            basslineSegment.set(0, ns);
          }
        }
      //System.out.println("new basslineSegment = " + basslineSegment);

      for( Object ob: basslineSegment )
        {
          bassline.add((MelodySymbol)ob);
        }
      
      }
    }

private Polylist prepProgList(Polylist results, Polylist acc) {
    //extract the chords and interpolations from results in order
    if (results.isEmpty()) return acc;
    return prepProgList(results.rest(), acc.append((Polylist)results.first()));
}

//depending on the boolean lists from willInterplate, map adds a PC
//to the chord part list
private Polylist mapPC(Polylist results) {
    if (results.isEmpty()) {
        return Polylist.nil;
    }
   if(results.rest().isEmpty()) {
       return mapPC(results.rest()).cons(((Polylist)results.first()).allButLast());
   }
   
   Chord leftbound = (Chord)((Polylist)results.first()).first();
   Chord rightbound = (Chord)((Polylist)results.second()).first();
   boolean action = (boolean)((Polylist)((Polylist)results.first()).last()).first();
   //System.out.println("leftbound: " + leftbound + " rightbound: " + rightbound + " action: " + action);
   
   if (action){
       Polylist newFirst = getPC(rightbound, leftbound);
       Polylist newRight = Polylist.list(newFirst.last(), ((Polylist)results.second()).last());
       newFirst = newFirst.allButLast();
       return mapPC(results.rest().rest().cons(newRight)).cons(newFirst);
   }
   
   return mapPC(results.rest()).cons(((Polylist)results.first()).allButLast());
       
}

 /* find the appropriate passing chord given a target chord*/
  private Polylist getPC(Chord c, Chord prev) {
     Polylist pchords = Polylist.nil;
     double sum = 0;
     String root = "";
     
     if (interpolations.isEmpty())
     {
         return Polylist.list(prev,c);
     }
     for (Polylist P = interpolations; P.nonEmpty(); P = P.rest()) 
     {
        Interpolant pc = null; 
        Interpolant first = (Interpolant)P.first();
        Polylist target = Polylist.list(first.getCHORDS().first(),first.getCHORDS().last());
        //the representive quality for the right boundary
        String targetright = "";
        //representative quality for the left boundary
        String targetleft;
        String rootleft;
        int targetsdistance = 0;
        
        //searches for wildchords
         if (target.member("_")) {
             rootleft = null;
             targetleft = null;
             if (((String) target.first()).equals("_")) {
                 Polylist right = getTargetAndRoot((String)target.last());
                 targetright = (String)right.first();
                 root = (String)right.last();
             }

             if (((String) target.last()).equals("_")) {
                 Polylist right = getTargetAndRoot((String)target.first());
                 targetright = (String)right.first();
                 root = (String)right.last();
             }
         }
         else {
           //if both boundary chords are defined in a interpolant
           //this gets the representative quality and root for each one
           
            Polylist left = getTargetAndRoot((String)target.first());
            Polylist right = getTargetAndRoot((String)target.last());
             targetright = (String)right.first();
             targetleft = (String)left.first();
             
             rootleft =  ((String)left.last());
             root  = ((String)right.last());
             targetsdistance = PitchClass.findRise(rootleft, root);
         }
        Chord leftchord = null;
       //checks the interpolant with the boundary chords to determine if it
       //matches the criteria to be a valid interpolation
        switch (c.getFamily()) {
            case "augmented":
            case "major":
                if (rootleft != null) {
                    if (targetleft != null) {
                        leftchord = new Chord(rootleft.toUpperCase() + targetleft);
                    }
                    if ((targetright.equals("") && targetleft != null)
                            || targetright.equals("") && 
                            prev.getFamily().equals(leftchord.getFamily())
                            && (targetsdistance == PitchClass.findRise(
                                    prev.getRoot(),c.getRoot()))) 
                    {
                        
                        pc = (Interpolant) P.first();
                    }
                } else if (targetright.equals("")) {
     
                    pc = (Interpolant) P.first();
                }
                break;
            case "half-diminished":
            case "minor7":
            case "minor": 
                if (rootleft != null) {
                    if (targetleft != null) {
                        leftchord = new Chord(rootleft.toUpperCase() + targetleft);
                    }
                    if (targetright.equals("m") && targetleft != null) {
                            if (prev.getFamily().equals(leftchord.getFamily())
                                && (targetsdistance == PitchClass.findRise(
                                        prev.getRoot(), c.getRoot()))) {
 
                        pc = (Interpolant) P.first();
                    }
                    }
                } else if (targetright.equals("m")) {

                    pc = (Interpolant) P.first();
                }
                break;
            case "sus4":
            case "dominant":
                if (rootleft != null) {
                    if (targetleft != null) {
                        leftchord = new Chord(rootleft.toUpperCase() + targetleft);
                    }
                    if (targetright.equals("7") || targetright.equals("7+") && targetleft != null) {
                        if (prev.getFamily().equals(leftchord.getFamily())
                                && (targetsdistance == PitchClass.findRise(
                                        prev.getRoot(), c.getRoot()))) {
                            if (targetright.equals("7")) {
                                pc = (Interpolant) P.first();
                            } else if (targetright.equals("7+")) {

                                pc = (Interpolant) P.first();
                            }
                        }
                    }
                } else if (targetright.equals("7")) {

                    pc = (Interpolant) P.first();
                } else if (targetright.equals("7+")) {

                    pc = (Interpolant) P.first();
                }
                break;
            default: break;
        }

          if (pc != null) {
              pchords = pchords.cons(pc);
              sum+= pc.getWEIGHT();
          }
      }

   int transposition = PitchClass.findRise(root, c.getRoot());
   //System.out.println(" left: " + prev + " right: " + c +" \n pchords "+ pchords + "\n");
  
  //sends the list of passing chords to choosePC for 1 to be chosen, along
  //with transposition and given the correct durations to each chord
  if(pchords.nonEmpty())
  {
   pchords = choosePC(pchords, prev, c, sum, transposition);
  }
  else pchords = Polylist.list(prev,c);
   
    return pchords;
  }
  
  private Polylist getTargetAndRoot(String target) {
      Chord c = new Chord(target);
      String t = "";
      String r = c.getRoot();
      String family = c.getFamily();
      switch (family) {
          case "major":
          case "augmented":
              break;
          case "minor":
          case "minor7":
          case "half-diminished":
              t = "m";
              break;
          case "dominant":
              String quality = c.getQuality();
              if (quality.contains("+") || quality.contains("#") ||
                      quality.contains("b9") || quality.contains("alt")) {
                  t = "7+";
      }
              else t = "7";
              break;
          default:
              t = "7";
              break;
      }

      return Polylist.list(t,r);
  }
  
  private Polylist choosePC(Polylist plist, Chord leftbound, 
          Chord rightbound, double sum, int transposition) {
    
    Chord pc;
    Interpolant chosen;
    //choose one interpolant by the list by weight
          double random = gen.nextDouble();

          double weights = 0;
          chosen = (Interpolant) plist.first();
          for (Polylist P = plist; P.nonEmpty(); P = P.rest()) {
              if ((int)sum == 1) { 
              weights += ((Interpolant) P.first()).getWEIGHT();
              }
              else weights += ((Interpolant) P.first()).getWEIGHT()/2;
              
              if (random < weights) {
                  chosen = (Interpolant) P.first();
                  break;
              }
          }
    int rhythmValue;
      
    Polylist targets = Polylist.list(chosen.getCHORDS().first(),
            chosen.getCHORDS().last());
    Polylist chords = chosen.getCHORDS().rest().allButLast();
    //searches for wild chord interpolations with only one insertion
    if(targets.member("_") && chords.length() == 1)
    {
        pc = new Chord((String)chords.first());
        pc.transpose(transposition);
        if (((String) targets.first()).equals("_")) {
            int leftRhythmVal = leftbound.getRhythmValue();
            int dividor = chosen.getDivide().length() - 2;
            if (leftRhythmVal / dividor >= chosen.getMINSLOTS()) {
                rhythmValue = leftbound.getRhythmValue() / dividor;
                leftbound.setRhythmValue(leftRhythmVal - rhythmValue);
                pc.setRhythmValue(rhythmValue);
            } else {
                return Polylist.list(leftbound, rightbound);
            }

        } else if (((String) targets.last()).equals("_")) {
            if (rightbound.getRhythmValue() / 2 >= chosen.getMINSLOTS()) {
                rhythmValue = rightbound.getRhythmValue() / 2;
                rightbound.setRhythmValue(rhythmValue);
                pc.setRhythmValue(rhythmValue);
            } else {
                return Polylist.list(leftbound, rightbound);
            }
        }
    }
    //sends chords with multiple chord insertions to be given the right durations
    else return chooseMultiPC(leftbound,chosen,rightbound, transposition); 
       //System.out.println("left: " + leftbound + " pc: " + pc + " rightbound: " + rightbound);
       return Polylist.list(leftbound,pc,rightbound);
  }
  
    private Polylist chooseMultiPC(Chord leftbound, Interpolant interp, Chord rightbound,
        int transposition) {
        
        Polylist targets = Polylist.list(interp.getCHORDS().first(),
                interp.getCHORDS().last());
        Polylist chords = interp.getCHORDS().rest().allButLast();
        Polylist result = Polylist.nil;
        
        if(targets.member("_")) {
            if (((String)targets.first()).equals("_"))
            {
                if (leftbound.getRhythmValue() % (1+chords.length()) != 0
                        || leftbound.getRhythmValue()/(1 + chords.length()) < interp.getMINSLOTS())
                {
                    return Polylist.list(leftbound,rightbound);
                }
                
                int rhythmValue = leftbound.getRhythmValue() / (1+chords.length());
                leftbound.setRhythmValue(rhythmValue);
                result = result.cons(leftbound);
                for(Polylist P = chords; P.nonEmpty(); P = P.rest()) {
                    Chord pc = new Chord((String)P.first());
                    pc.setRhythmValue(rhythmValue);
                    pc.transpose(transposition);
                    result = result.append(Polylist.list(pc));
                }
               return result.append(Polylist.list(rightbound));
              
            }
            else if (((String)targets.last()).equals("_"))
            {
                if (rightbound.getRhythmValue() % (1+chords.length()) != 0
                        || rightbound.getRhythmValue()/(1+chords.length()) < interp.getMINSLOTS())
                {
                    return Polylist.list(leftbound,rightbound);
                }
                
                int rhythmValue = rightbound.getRhythmValue() / (1+chords.length());
                rightbound.setRhythmValue(rhythmValue);
                result = result.cons(leftbound);
                for(Polylist P = chords; P.nonEmpty(); P = P.rest()) {
                    Chord pc = new Chord((String)P.first());
                    pc.setRhythmValue(rhythmValue);
                    pc.transpose(transposition);
                    result = result.append(Polylist.list(pc));
                }
               return result.append(Polylist.list(rightbound));
            }
        }
        int totalSpace = rightbound.getRhythmValue() + leftbound.getRhythmValue();
        int totalchordslength = chords.length() + 2;
        if(totalSpace % totalchordslength != 0 ||
                totalSpace / totalchordslength < interp.getMINSLOTS())
        {
            return Polylist.list(leftbound,rightbound);
        }
        int rhythmValue = totalSpace/ totalchordslength;
        leftbound.setRhythmValue(rhythmValue);
        result = result.cons(leftbound);
        
        for(Polylist P = chords; P.nonEmpty(); P = P.rest()) {
            Chord pc = new Chord((String)P.first());
            pc.setRhythmValue(rhythmValue);
            pc.transpose(transposition);
            result = result.append(Polylist.list(pc));
        }
        
        rightbound.setRhythmValue(rhythmValue);
        return result.append(Polylist.list(rightbound));
    }
  
    private Polylist substituteChords(Polylist original)
    {
        if (original.isEmpty())
        {
            return original;
        }
        Chord first = (Chord)original.first();
        Polylist sublist;
                
        for(Polylist P = substitutions; P.nonEmpty(); P = P.rest())
        {
            //match and check qualifications of a given sub
            Substitution sub = (Substitution)P.first();
            Chord subchord = new Chord((String)sub.getOriginals().first());
            if (!subchord.getQuality().equals(first.getQuality()))
                    continue;
         
            int random = (int)(Math.random()*100+1);
            if (random > (int)(sub.getWeight()*100))
                continue;
        
           int length = sub.getOriginals().length();
           //get a sublist of thats the same length of the original chords
           //specified in the Substitution
           
           Polylist cutList = original.prefix(length);
           
           //makes sure the list of that length is actually in the original
           //chordlist
           if(cutList.isEmpty())
               continue;
           
           //checks the sub chords
           sublist = checkSubChords(cutList, sub);       
           if(sublist.length() != length) 
               continue;
           
           //transposes, sets duration, and returns the correct substitution
           //chords
           int transposition = PitchClass.findRise(
                   subchord.getRoot(), first.getRoot());
           sublist = setSubChords(cutList, sub, transposition);
          
           return substituteChords(original.coprefix(length)).cons(sublist);
        }
        
        return substituteChords(original.rest()).cons(first);
    }
    
    private Polylist checkSubChords(Polylist sublist, Substitution sub) {
            Polylist subchords = sub.getOriginals();
            Chord firstchord = new Chord((String)subchords.first());
            
            Polylist checker = Polylist.list(firstchord);
            subchords = subchords.rest();
            //makes sure every chord in the sublist matches with
            //every chord specified in the Subsitution
            for(Polylist L = sublist.rest(); L.nonEmpty(); L = L.rest())
            {   
                Chord checkchord = new Chord((String)subchords.first());
                if(checkchord.getQuality().equals(((Chord)L.first()).getQuality()))
                {
                    checker = checker.append(Polylist.list(L.first()));
                } else return checker;
                subchords = subchords.rest();
            }
            
            return checker;
    }
    
    private Polylist setSubChords(Polylist sublist, Substitution sub, 
            int transposition) {
        int totalSpace = 0;
        //find the total musical space that can be used for the substitution
        for (Polylist P = sublist; P.nonEmpty(); P = P.rest())
        {
            totalSpace+= ((Chord)P.first()).getRhythmValue();
        }
        //System.out.println("sublist: " + sublist +  " totalspace is: " + totalSpace);
        int rhythmValue = totalSpace / sub.getSubs().length();
        //divide the total space evenly amound the substitutions and check to 
        //make sure that the given rhythm values aren't triplet
        //values
        if (rhythmValue < sub.getMinSlots() || (rhythmValue % 120 != 0)
                || (rhythmValue*2 % 480 != 0))
        {
            
            return sublist;
        }
        Polylist chordList = Polylist.nil;
        
        for(Polylist L = sub.getSubs(); L.nonEmpty(); L = L.rest())
        {
            Chord newChord = new Chord((String)L.first());
            newChord.setRhythmValue(rhythmValue);
            newChord.transpose(transposition);
            chordList = chordList.append(Polylist.list(newChord));
        }
        
        return chordList;
    }
    
    private Polylist extractSub(Polylist original, Polylist acc)
    {
        //take the substitutions out of their polylists to
        //return a polylist of chords
        if (original.isEmpty())
        {
            return acc;
        }
        
        if(original.first() instanceof Polylist)
        {
            return extractSub(original.rest(),
                    acc.reverse().append((Polylist)original.first()).reverse());
        }
        
        return extractSub(original.rest(),acc.cons(original.first()));
    }
  
    private ChordPart createInterpolatedChordPart(ChordPart partA) {
        if (interpolations.isEmpty() && substitutions.isEmpty()) {
            return partA;
        }
        ChordPart part = partA.copy();
        part.setRoadmap(partA.getRoadMap());
        part.setSectionInfo(partA.getSectionInfo()); // Seems like this shouldn't be necessary, but apparently is.
        Polylist sectionInfo = Interpolate.ArraytoPoly(part.getSectionInfo().getSectionRecords());

        //returns a polylist of paired chords and boolean polylists 
        //that tells where each interpolant will fall
        Polylist results = Polylist.nil;
        if (interpolations.nonEmpty()) {
            results = Interpolate.willInterpolate(
                part, 0, ((Interpolant) interpolations.first()).getMINSLOTS(),
                ((Interpolable) interpolables.first()).getPROBABILITY());        
            //System.out.println("1 " + results);

        } else {
            results = Interpolate.willInterpolate(
                    part, 0, ((Substitution) substitutions.first()).getMinSlots(),
                    ((Interpolable) interpolables.first()).getPROBABILITY());
       
        }
        //insert interpolations
        results = mapPC(results);
        results = prepProgList(results, Polylist.nil);
        //insert substitutions
        results = extractSub(substituteChords(results), Polylist.nil).reverse();
        
        ChordPart newChordPart = new ChordPart();
        //place these chords into a ChordPart
        for (Polylist P = results; P.nonEmpty(); P = P.rest()) {
            Chord chord = (Chord) P.first();
            newChordPart.addChord(chord.getName(), chord.getRhythmValue());
        }

        //insert the section info from the original chordPart into the new chordPart
        newChordPart.setSectionInfo(partA.getSectionInfo());
        for (Polylist L = sectionInfo; L.nonEmpty(); L = L.rest()) {
            SectionRecord first = (SectionRecord) L.first(); // L was sectionInfo
            newChordPart.addSection(first.styleName, first.index, first.isPhrase);
        }

        return newChordPart;
    }
  
  
/**
 * This is called from SectionInfo.
 * 
 * Using the Pattern objects of this Style, sequences an accompaniment for the
 * given ChordPart.
 *
 * @param seq the Sequence that contains the Track
 * @param track the Track to put the accompaniment on
 * @param time a long containing the time to start the accompaniment
 * @param chordPart the ChordPart to render
 * @return a long containing the ending time of the accompaniment
 */
  
public long render(MidiSequence seq, // called from SectionInfo
                   long time,
                   ChordPart chordPart,
                   int startIndex,
                   int endIndex,
                   Transposition transposition,
                   boolean useDrums,
                   int endLimitIndex,
                   boolean constantBass)
        throws InvalidMidiDataException
  {
    int chordTransposition = transposition.getChordTransposition();
    int bassTransposition = transposition.getBassTransposition();
    setResidualChordPattern(null);
    boolean hasStyle = !noStyle();
    // to trace sequencing info:
    //System.out.println("Sequencing Style: " + this + " startIndex = " + startIndex
    // + " endIndex = " + endIndex + " endLimitIndex = " + endLimitIndex + " useDrums = " + useDrums + " hasStyle = " + hasStyle);
    // i iterates over the Chords in the ChordPart.
    ChordPart chordPart2 = createInterpolatedChordPart(chordPart);
   // System.out.println("\n chord part is: \n" + chordPart);
   // System.out.println("\nintrpolated chord part is: \n" + chordPart2);
    
   // imp.data.Score newScore = new imp.data.Score(chordPart2.size());

   // newScore.setChordProg(chordPart2);
   // newScore.setSectionInfo(chordPart2.getSectionInfo());
   // imp.gui.Notate newNotate = new imp.gui.Notate(newScore);
   // newNotate.setVisible(true);
   chordPart = chordPart2;
    
    Part.PartIterator i =
            chordPart.iterator(chordPart.getCurrentChordIndex(startIndex));

    long startTime = time;

    if( hasStyle && useDrums && !drumPatterns.isEmpty() )
      {
        // Introduce drums, if there is a Style

        makeDrumline(seq, startTime, endIndex - startIndex, endLimitIndex);
      }

    Chord next = null;
    Chord prev = null;

    LinkedList<MelodySymbol> bassline = new LinkedList<>();

    int index = startIndex;
    ChordSymbol chord;
    ChordSymbol nextChord;
    ChordSymbol previousExtension = null;
    NoteSymbol previousBassNote = 
        NoteSymbol.makeNoteSymbol((bassHigh.getMIDI() + bassLow.getMIDI()) / 2);
    Polylist previousChord = Polylist.nil;

    // Iterating over one ChordPart with i

    while( (i.hasNext() || next != null) 
        && (endLimitIndex == ENDSCORE || index <= endLimitIndex) )
      {
        if( next == null )
          {
            index = i.nextIndex();
            next = (Chord) i.next();
          }

        Chord currentChord = next;

        int rhythmValue = currentChord.getRhythmValue();
        if( startIndex > index )
          {
            rhythmValue -= startIndex - index;
          }

        if( i.hasNext() )
          {
            index = i.nextIndex();

            next = (Chord) i.next();
          }
        else
          {
            next = null;
            index = chordPart.size();
          }

        if( endIndex <= index )
          {
            rhythmValue -= index - endIndex;
          }

        if( !hasStyle )
          {
            time = currentChord.render(seq,
                                       time,
                                       ImproVisor.getChordChannel(),
                                       this,
                                       prev,
                                       rhythmValue,
                                       chordTransposition,
                                       endLimitIndex);
            prev = currentChord;
            if( endIndex <= index )
              {
                break;
              }
            else
              {
                continue;
              }
          }

        chord = currentChord.getChordSymbol();
        if( next == null || next.getChordSymbol().isNOCHORD() )
          {
            nextChord = chord;
          }
        else
          {
            nextChord = next.getChordSymbol();
          }

        if( !chord.isNOCHORD() && hasStyle )
          {
            if( useExtensions )
              {
                if( gen.nextInt(3) == 0 )
                  {
                    chord = extend(chord, previousExtension);
                  }
                previousExtension = chord;
              }

            //System.out.println("current chord " + currentChord.getName() + " and index " + index);
            previousChord = makeChordline(seq,
                                          time,
                                          currentChord,
                                          previousChord,
                                          currentChord.getRhythmValue(),
                                          transposition,
                                          endLimitIndex,
                                          constantBass);
          }
        
        if( !constantBass )
        {
        //System.out.println("previousBassNote " + previousBassNote + " low = " + getBassLow() + " high = " + getBassHigh());
        // adjust bass octave between patterns only, not within
        if( previousBassNote.higher(getBassHigh()) )
          {
            previousBassNote = previousBassNote.transpose(-12);
            //System.out.println("downward to " + previousBassNote);
          }
        else if( getBassLow().higher(previousBassNote) )
          {
            previousBassNote = previousBassNote.transpose(12);
            //System.out.println("upward to " + previousBassNote);
          }
        }
        
//System.out.println("\nAbout to add to bassline, chord = " + chord + ", hasStyle = " + hasStyle);
        if( !chord.isNOCHORD() && hasStyle )
          {
            addToBassline(bassline,
                        chord,
                        nextChord,
                        previousBassNote,
                        rhythmValue,
                        bassTransposition,
                        constantBass);

            // Sets previousBassNote to last NoteSymbol in bassline
 
            for( Iterator<MelodySymbol> it = bassline.descendingIterator(); it.hasNext(); )
              {
                MelodySymbol ob = it.next();
                if( ob instanceof NoteSymbol )
                  {
                    NoteSymbol ns = (NoteSymbol)ob;
                    
                    if( !ns.isRest() )
                      {
                      previousBassNote = ns;
                      break;
                      }
                  }
              }
          }
        else
          {
            Rest r = new Rest(rhythmValue);
            NoteSymbol rest = NoteSymbol.makeNoteSymbol(r.toLeadsheet());
            bassline.add(rest);
          }

        time += currentChord.getRhythmValue() * seq.getResolution() / BEAT;
        
        if( endIndex <= index )
          {
            break;
          }
      }
    //System.out.println("bassline = " + bassline);
    
    // Finished iterating over ChordPart
    
    if( !bassline.isEmpty() )
      {
        MelodyPart bassMelody = new MelodyPart();

        Object last = bassline.getLast();

        if( last instanceof Polylist )
          {
            Polylist L = (Polylist) last;
            NoteSymbol ns = (NoteSymbol) L.second();
            bassline.removeLast();
            bassline.add(ns);
          }

        int volume = MAX_VOLUME;

        // add each note to our bassline melody
        for( Object ob: bassline )
          {
            if( ob instanceof NoteSymbol )
              {
                NoteSymbol noteSymbol = (NoteSymbol) ob;
                Note note = noteSymbol.toNote();
                note.setVolume(volume);
                bassMelody.addNote(note);
              }
            else if( ob instanceof VolumeSymbol )
              {
                VolumeSymbol volumeSymbol = (VolumeSymbol) ob;
                volume = volumeSymbol.getVolume();
                //System.out.println("setting bassMelodyVolume to " + volumeSymbol);
              }
            else
              {
                assert false;
              }
          }

        bassMelody.setSwing(accompanimentSwing);
        bassMelody.setInstrument(bassInstrument);
        bassMelody.makeSwing();
        bassMelody.render(seq,
                          ImproVisor.getBassChannel(),
                          startTime,
                          seq.getBassTrack(),
                          bassTransposition,
                          endLimitIndex);
      }

    return time;
  }

  /**
   * Extend the currentChord chord based on a previous chord.
   * @param chord     a ChordSymbol containing the chord to extend
   * @param previousChord a ChordSymbol containing the previous chord
   * @return a ChordSymbol containing the extended chord
   */
  public static ChordSymbol extend(ChordSymbol chord, ChordSymbol lastChord)
    {
    int rise = PitchClass.findRise(chord.getRootString());
    Polylist extensions;

    // get a random extension if there is no previous chord
    if( lastChord == null )
      {
      extensions = chord.getChordForm().getExtensions();
      extensions = ChordSymbol.chordSymbolsFromStrings(extensions);
      extensions = ChordSymbol.transpose(extensions, rise);
      extensions = extensions.cons(chord);

      return (ChordSymbol)BassPattern.getRandomItem(extensions);
      }

    extensions = Advisor.getExtensions(Advisor.getFinalName(
            chord.toString()));
    extensions = ChordSymbol.chordSymbolsFromStrings(extensions);
    extensions = ChordSymbol.transpose(extensions, rise);
    extensions = extensions.cons(chord);

    // check for appropriate extensions based on previous chord
    Polylist goodExtensions = Polylist.nil;
    int highCommon = -20;
    while( extensions.nonEmpty() )
      {
      ChordSymbol c = (ChordSymbol)extensions.first();
      extensions = extensions.rest();
          int common = commonPitches(lastChord, c) -
              uncommonPitches(lastChord, c);

      if( common == highCommon )
        {
        goodExtensions = goodExtensions.cons(c);
        }
      else if( common > highCommon )
        {
        highCommon = common;
        goodExtensions = Polylist.list(c);
        }
      }

    return (ChordSymbol)BassPattern.getRandomItem(goodExtensions);
    }

  /**
   * Takes two chords and returns the number of pitches the second one
   * has that the first one doesn't.
   * @param c1        a ChordSymbol to compare
   * @param c2        a chordSymbol to compare
   * @return an int containing the number of pitches the second chord
   *         has that the first one doesn't
   */
  public static int uncommonPitches(ChordSymbol c1, ChordSymbol c2)
    {
    Polylist s1 = c1.getChordForm().getSpell(c1.getRootString());
    Polylist s2 = c2.getChordForm().getSpell(c2.getRootString());
    
    int sum = 0;
    while( s2.nonEmpty() )
      {
      NoteSymbol n = (NoteSymbol)s2.first();
      s2 = s2.rest();

      if( !n.enhMember(s1) )
        {
        sum++;
        }

      }
    return sum;
    }

  /**
   * Returns the number of pitches two chords have in common.
   * @param c1        a ChordSymbol to compare
   * @param c2        a ChordSymbol to compare
   * @return an int containing the number of pitches the two chords have
   *         in common
   */
  public static int commonPitches(ChordSymbol c1, ChordSymbol c2)
    {
    Polylist s1 = c1.getChordForm().getSpell(c1.getRootString());
    Polylist s2 = c2.getChordForm().getSpell(c2.getRootString());

    int sum = 0;
    while( s2.nonEmpty() )
      {
      NoteSymbol n = (NoteSymbol)s2.first();
      s2 = s2.rest();

      if( n.enhMember(s1) )
        {
        sum++;
        }

      }
    return sum;
    }

    @Override
    public boolean equals(Object obj)
      {
        if( obj == null )
          {
            return false;
          }
        if( getClass() != obj.getClass() )
          {
            return false;
          }
        final Style other = (Style) obj;
        if( (name == null && other.name != null) || !name.equals(other.name) )
          {
            return false;
          }
        return true;
      }

    @Override
    public int hashCode()
      {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
      }

  }
