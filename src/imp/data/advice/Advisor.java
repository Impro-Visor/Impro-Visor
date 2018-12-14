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

package imp.data.advice;

import imp.Constants;
import imp.ImproVisor;
import imp.data.Chord;
import imp.data.ChordForm;
import imp.data.ChordPart;
import imp.style.stylePatterns.ChordPattern;
import imp.data.ChordSymbol;
import imp.data.Key;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.PitchClass;
import imp.data.Score;
import imp.style.Style;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ListIterator;
import polya.Polylist;
import polya.PolylistEnum;

/**
 * Contains a set of rules read and processed during construction that can
 * be queried for Advice.
 * @see         Advice
 * @see         Polylist
 * @author      Stephen Jones, Bob Keller
 */
public class Advisor
        implements Constants
{
static private int numCellBuckets = 21;	 // half-beat buckets in which to sort cells by length

static private int numLickBuckets = 33;	 // half-beat buckets in which to sort cells by length

static private int numProfileBins = 10;

static private int adviceTreeCacheLimit = 1000;

// was Integer.parseInt(Preferences.getPreference(Preferences.ADV_CACHE_SIZE));

static private int depthLimit = 5; // to limit circularity in "same" scale or chord

static private int slotsPerHalfBeat = 60;	// FIX!

static String title1 =
        " with same chord type (or having this type as an extension)";

static String title2 = " with related chord type (or extending this chord type)";

static String title3 =
        " with same first chord type (or having this type as an extension)";

static String title4 =
        " with related first chord type (or extending this chord type)";

static ArrayList<String> styleWarnings = new ArrayList<String>();

private int interChordTolerance = 3; // half-beats, used in matching

/**
 * A counter used to number licks and cell advice items
 */
private int serialNo = 0;

/**
 * an association list from chord names to ChordForms
 */
private static Polylist chords;

/**
 * an association list from scale names to ScaleForms
 */
private static Polylist scales;


// cell flavors
static final int PLAIN_CELL = 0;

static final int IDIOM_CELL = 1;

static final int RHYTHM_CELL = 2;

// lick flavors
static final int PLAIN_LICK = 0;

static final int QUOTE_LICK = 1;

static final int BRICK_LICK = 2;

private static Polylist rhythms = Polylist.nil;

/**
 * contains "cells" of notes
 */
private static Polylist cells[] = new Polylist[2]; // indexed by cell flavor

/**
 * contains licks
 */
private static Polylist licks[] = new Polylist[3]; // indexed by lick flavor


/**
 * the table listing, for each chord, the chords it extends
 * (either directly, or indirectly)
 */
private static Polylist extensionsTable;

/**
 * the table listing, for each chord, the chords that extend it
 * (either directly, or indirectly)
 */
private static Polylist invertedExtensionsTable[];

private static int numChords;

// Used for collapsing or expanding advice to fit different time signatures.
int[] metre = new int[2];

static ArrayList<Polylist> ruleArray;

static ArrayList<Boolean> markArray;

// FIX:  Some or all of these strings should move to Constants
static final String ADVICE = "advice";

static final String APPROACH = "approach";

static final String BRICK = "brick";

static final String RHYTHM = "rhythm";

static final String CELL = "cell";

static final String CHORD = "chord";

static final String CHORDS = "chords";

static final String COLOR = "color";

static final String DRUM_PATTERN = "drum-pattern";

static final String EXTENSIONS = "extensions";

static final String FAMILY = "family";

static final String IDIOM = "idiom";

static final String KEY = "key";

static final String LICK = "lick";

static final String MARKED = "marked";

static final String NAME = "name";

static final String NOTES = "notes";

static final String PRIORITY = "priority";

static final String QUOTE = "quote";

static final String SAME = "same";

static final String SCALE = "scale";

static final String SCALES = "scales";

static final String SEQUENCE = "sequence";

static final String SERIAL = "serial";

static final String SPELL = "spell";

static final String STYLE = "style";

static final String SUBS = "substitute";

static final String VOICINGS = "voicings";

static final String BAR = "|";

static final String SLASH = "/";

public static final Polylist licksIgnore = Polylist.list(BAR, SLASH);

// Figure out which components of advice to show;
static final int NONE = 0;

static final int ALL = 4195;

static final int CHORDTREE = 1;

static final int COLORTREE = 2;

static final int SCALETREE = 4;

static final int APPROACHTREE = 8;

static final int SUBSTREE = 16;

static final int EXTSTREE = 32;

static final int RHYTHMTREE = 64;

static final int CELLTREE = 128;

static final int IDIOMTREE = 256;

static final int LICKTREE = 512;

static final int QUOTETREE = 1024;

static final int BRICKTREE = 2048;

private static boolean styleProcessed = false;


public Advisor()
  {
  }


/**
 * Creates a new Advisor based on a set of rules that can be queried for
 * Advice.
 * @param rules     a Polylist containing Advisor rules
 */
public Advisor(Polylist rules)
  {
  setRules(rules);

  }


public void setRules(Polylist rules)
  {
  if( rules == null )
    {
    return;
    }

  // Save all rules in an array for indexing in culling items

  int rulesLength = rules.length();
  ruleArray = new ArrayList<Polylist>(rulesLength);
  markArray = new ArrayList<Boolean>(rulesLength);
  PolylistEnum it = rules.elements();

  // Put all rule items in array for subsequent indexing.

  for( int i = 0; it.hasMoreElements(); i++ )
    {
    ruleArray.add((Polylist)it.nextElement());
    markArray.add(Boolean.FALSE);
    }

  if( Trace.atLevel(2) )
    {
    System.out.println("Rules as input: ");
    showRules(System.out);
    }

  chords = Polylist.nil;
  scales = Polylist.nil;

  rhythms = Polylist.nil;
  // Don't redo styles on new vocabulary
  //styles = Polylist.nil;
  //backupStyles = Polylist.nil;

  cells[PLAIN_CELL] = Polylist.nil;
  cells[IDIOM_CELL] = Polylist.nil;

  licks[PLAIN_LICK] = Polylist.nil;
  licks[QUOTE_LICK] = Polylist.nil;
  licks[BRICK_LICK] = Polylist.nil;

  extensionsTable = Polylist.nil;

  addRules();

  updateColors(chords);
  chords = chords.reverse();
  scales = scales.reverse();

  cells[PLAIN_CELL] = cells[PLAIN_CELL].reverse();
  cells[IDIOM_CELL] = cells[IDIOM_CELL].reverse();
  //rhythms = rhythms.reverse();

  licks[PLAIN_LICK] = licks[PLAIN_LICK].reverse();
  licks[QUOTE_LICK] = licks[QUOTE_LICK].reverse();
  licks[BRICK_LICK] = licks[BRICK_LICK].reverse();


  if( Trace.atLevel(2) )
    {
    System.out.println("Vocabulary items");

    System.out.println("\nscales:");
    it = scales.elements();
    while( it.hasMoreElements() )
      {
      System.out.println(it.nextElement());
      }
    System.out.println("\nchords:");
    it = chords.elements();
    while( it.hasMoreElements() )
      {
      Polylist chordElement = (Polylist)it.nextElement();
      ChordForm form = (ChordForm)chordElement.second();
      form.showForm(System.out);
      }

    System.out.println("\nrhythms:");
    it = rhythms.reverse().elements();
    while( it.hasMoreElements() )
      {
      System.out.println(it.nextElement());
      }
    System.out.println("\ncells:");
    it = cells[PLAIN_CELL].reverse().elements();
    while( it.hasMoreElements() )
      {
      System.out.println(it.nextElement());
      }
    System.out.println("\nidioms:");
    it = cells[IDIOM_CELL].elements();
    while( it.hasMoreElements() )
      {
      System.out.println(it.nextElement());
      }
    System.out.println("\nlicks:");
    it = licks[PLAIN_LICK].elements();
    while( it.hasMoreElements() )
      {
      LickForm form = (LickForm)it.nextElement();
      form.showForm(System.out);
      }

    System.out.println("\nquotes:");
    it = licks[QUOTE_LICK].elements();
    while( it.hasMoreElements() )
      {
      LickForm form = (LickForm)it.nextElement();
      form.showForm(System.out);
      }
    System.out.println("\nbricks:");
    it = licks[BRICK_LICK].elements();
    while( it.hasMoreElements() )
      {
      LickForm form = (LickForm)it.nextElement();
      form.showForm(System.out);
      }
    }
  }


/**
 * List all chords on out for reference purposes.
 */
public void listChords(PrintStream out)
  {
  Polylist L = chords;

  while( L.nonEmpty() )
    {
    Polylist entry = (Polylist)L.first();
    ChordForm form = (ChordForm)entry.second();
    form.showForm(out);
    L = L.rest();
    }
  }


/**
 * List all chords on out for reference purposes.
 */
public void listChordNames(PrintStream out)
  {
  Polylist L = chords;

  while( L.nonEmpty() )
    {
    Polylist entry = (Polylist)L.first();
    ChordForm form = (ChordForm)entry.second();
    form.showName(out);
    L = L.rest();
    }
  }

public Polylist getChordNames()
  {
  Polylist L = chords;
  Polylist R = Polylist.nil;

  while( L.nonEmpty() )
    {
    Polylist entry = (Polylist)L.first();
    // Do not include synonyms
    //System.out.print("entry = " + entry);
    if( !((ChordForm)entry.second()).isSynonym() )
      {
      //System.out.println(" accepted");
      R = R.cons(entry.first());
      }
    else
      {
      //System.out.println(" ignored");
      }
    L = L.rest();
    }   
  return R;
  }

/**
 * Reads a Polylist of rules and saves the rules as various other
 * organized Polylists.
 * @param rules     a Polylist containing Advisor rules
 */
public void addRules()
  {
  int numRules = ruleArray.size();
  for( int serial = 0; serial < numRules; serial++ )
    {
    addOneRule(ruleArray.get(serial),
            serial, false, true); // don't check for duplicates
    }

  // Build the extensionsTable

  PolylistEnum e = chords.elements();

  while( e.hasMoreElements() )
    {

    String chordName = (String)((Polylist)e.nextElement()).first();

    if( Polylist.assoc(chordName, extensionsTable) == null )
      {
      traverseExtensions(chordName, Polylist.nil);
      }
    }

  if( Trace.atLevel(2) )
    {
    // Display the extensions table

    System.out.println("Chords that each chord extends");
    for( Polylist L = extensionsTable; L.nonEmpty(); L = L.rest() )
      {
      System.out.println(L.first());
      }
    }
  // Create the inverted extensions table

  numChords = chords.length();

  invertedExtensionsTable = new Polylist[numChords];

  e = chords.elements();
  int index = 0;

  // seed the table
  while( e.hasMoreElements() )
    {
    invertedExtensionsTable[index] = Polylist.list(
            (String)(((Polylist)e.nextElement()).first()));
    index++;
    }

  e = extensionsTable.elements();

  while( e.hasMoreElements() )
    {
    Polylist entry = (Polylist)e.nextElement();
    String extension = (String)entry.first();
    entry = entry.rest();

    while( entry.nonEmpty() )
      {
      String extendee = (String)entry.first();

      // find extendee in inverted table, if it is there

      for( int extendeeIndex = 0; extendeeIndex < numChords; extendeeIndex++ )
        {
        String head = (String)invertedExtensionsTable[extendeeIndex].first();
        if( head.equals(extendee) )
          {
          // add the extension
          invertedExtensionsTable[extendeeIndex] =
                  invertedExtensionsTable[extendeeIndex].rest().cons(extension).cons(
                  head);
          break;
          }
        }
      entry = entry.rest();
      }
    }

  if( Trace.atLevel(2) )
    {
    // Display the inverted extensions table
    System.out.println("\nChords that extend each chord");
    for( int i = 0; i < numChords; i++ )
      {
      System.out.println(invertedExtensionsTable[i]);
      }
    }
  //listChordNames(System.out);
  //System.out.println(getChordNames());
  }


/**
 * Add a single "rule" (chord, scale, rhythm, cell, idiom, lick, or quote)
 * to the database.
 */
public static boolean addOneRule(Object ob, int serial, boolean marked,
                                 boolean allowDuplicates)
  {
  Trace.log(2, "addOneRule " + serial + ": " + ob);
  if( ob instanceof Polylist )
    {
    Polylist first = (Polylist)ob;

    Trace.log(2, "length of " + ob + " is " + first.length());

    if( first.isEmpty() )
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Ignoring empty list () or extra right paren found in vocabulary");
      return false;
      }

    String dispatch = (String)first.first();

    int grade = 0;
    for( int i = 0; i < first.length(); ++i )
      {
      Object nth = first.nth(i);
      if( nth instanceof Polylist && ((Polylist)nth).nonEmpty() )
        {
        if( ((String)((Polylist)nth).first()).equals("grade") )
          {
          grade = ((Number)((Polylist)nth).second()).intValue();
          }
        }
      }

    Trace.log(4, "dispatching on " + dispatch);

    if( dispatch.equals(CHORD) )
      {
      // Strip off the word "chord" and put it in the chords list

      boolean result = addChord(first.rest());
      if( !result )
        {
        ErrorLog.log(ErrorLog.WARNING, "rejecting chord form: " + first);
        }
      return result;
      }
    else if( dispatch.equals(SCALE) )
      {
      // Strip off the word "scale" and the letter "C" and put
      // it in the scales list

      return addScale(first.rest());
      }
    else if( dispatch.equals(STYLE) )
      {

      if( !(styleProcessed) )
        {
        styleProcessed = true;
        readStyles();
        }
      return true;
      }
    else if( dispatch.equals(RHYTHM) )
      {
      return addCell(RHYTHM_CELL, first.rest(), serial, marked);
      }
    else if( dispatch.equals(CELL) )
      {
      return addCell(PLAIN_CELL, first.rest(), serial, marked);
      }
    else if( dispatch.equals(IDIOM) )
      {
      return addCell(IDIOM_CELL, first.rest(), serial, marked);
      }
    else if( dispatch.equals(LICK) )
      {
      return addLick(PLAIN_LICK, first, serial, grade, marked,
              marked || allowDuplicates, QUOTE_LICK);
      }
    else if( dispatch.equals(QUOTE) )
      {
      return addLick(QUOTE_LICK, first, serial, grade, marked,
              marked || allowDuplicates, PLAIN_LICK);
      }
    else if( dispatch.equals(BRICK) )
      {
      return addLick(BRICK_LICK, first, serial, grade, marked,
              marked || allowDuplicates, PLAIN_LICK);
      }
    else if( dispatch.equals(MARKED) )
      {
      return addOneRule(first.second(), serial, true, allowDuplicates);
      }
    }
  ErrorLog.log(ErrorLog.WARNING,
          "Unidentified item attempted as a rule: " + ob + " class is " + ob.getClass());
  return false;
  }


public static void readStyles()
  {
  File styleDir = ImproVisor.getStyleDirectory();
  
  //new File(Preferences.getPreference(
  //   Preferences.DEFAULT_STYLE_DIRECTORY));
  
  if( styleDir.canRead() )
    {
    File[] styleFiles = styleDir.listFiles();
    
    // 7-26-13 HB
    // Fix for Linux, where the file list is not in alphabetic order
    Arrays.sort(styleFiles,  new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return f1.getName().toUpperCase().compareTo(f2.getName().toUpperCase());
            } 
        });
    
    for( int i = 0; i < styleFiles.length; i++ )
      {
      readStyleFile(styleFiles[i]);
      }
    }
  else
    {
    ErrorLog.log(ErrorLog.WARNING, "Styles Directory Not Found");
    }
  }


/**
 * Read one style file
@param file
 */
public static void readStyleFile(File theFile)
  {
  if( !(theFile.getName().endsWith(Constants.STYLE_EXTENSION)) )
    {
    return;
    }
  try
    {
    FileReader file = new FileReader(theFile);
    BufferedReader reader = new BufferedReader(file);
    String fileContents = "";
    boolean eof = false;
    while( !eof )
      {
      String in = reader.readLine();
      if( in == null )
        {
        eof = true;
        }
      else
        {
        fileContents += in;
        }
      }
    Polylist p = Notate.parseListFromString(fileContents);
    if( p == null || p.isEmpty() )
      {
      throw new IOException("empty file");
      }
    Polylist polyListContent = (Polylist)p.first();
    Polylist styleContent = polyListContent.rest();
    updateStyle(styleContent);
    }
  catch( IOException e )
    {
    ErrorLog.log(ErrorLog.WARNING,
            "Unable to read: " + theFile.getName());
    }
  }


/**
 * Create a single string out of a list of separate strings,
 * introducing a space to separate each item in the list.
 * This is so, for example, a list of things can be used as
 * a single name, without using underscores and the like.
 */
public static String concatListWithSpaces(Polylist L)
  {
  StringBuffer buffer = new StringBuffer();
  if( L.nonEmpty() )
    {
    buffer.append(L.first().toString());

    L = L.rest();
    while( L.nonEmpty() )
      {
      buffer.append(" ");
      buffer.append(L.first().toString());
      L = L.rest();
      }
    }
  return buffer.toString();
  }


/**
 * Create chord from raw polylist definition and add it to
 * the set of chords, indexed by name.  (Most of the creation
 * proper is done by makeChordForm.)
 */
static boolean addChord(Polylist chordDef)
  {
  Trace.log(2, "adding chord definition " + chordDef);
  Polylist nameElement = chordDef.assoc(NAME);
  if( nameElement == null )
    {
    ErrorLog.log(ErrorLog.WARNING,
            "Chord definition needs a name: " + chordDef + ", ignoring");
    return false;
    }

  if( nameElement.length() != 2 || !(nameElement.second() instanceof String) )
    {
    ErrorLog.log(ErrorLog.WARNING,
            "Ignoring definition with error in chord name: " + chordDef + ", must be length 2 with second item a string.");
    return false;
    }

  ChordForm form = ChordForm.makeChordForm(chordDef);

  if( form == null )
    {
    return false;
    }

  Polylist item = Polylist.list(form.getName(), form);
  
  //System.out.println("adding " + item);

  chords = chords.cons(item);
  return true;
  }


/**
 * Create scale from raw polylist definition and add it to
 * the set of scales, indexed by name.  (Most of the creation
 * proper is done by makeScaleForm.)
 */
static boolean addScale(Polylist scale)
  {
  ScaleForm form = ScaleForm.makeScaleForm(scale);

  if( form == null )
    {
    return false;
    }

  scales = scales.cons(Polylist.list(form.getName(), form));
  return true;
  }



/**
 * Create style from raw polylist definition and if it exists, replace the 
 * existing style with the new one. Otherwise, add it to the list. 
 * The returned value indicates whether the style was actually made.
 */
public static boolean updateStyle(Polylist style)
  {
  Style s = Style.makeStyle(style);

  if( s == null )
    {
    return false;
    }

  String name = s.getName();
  
  Style.setStyle(name, s);

  return true;
  }


/**
 * Add a Cell, Idiom, or Rhythm form to the database.
 */
static boolean addCell(int flavor, Polylist cell, int serial, boolean marked)
  {
  if( flavor == RHYTHM_CELL )
    {
      //System.out.println("adding cell " + serial + " of flavor " + flavor + ": " + cell);
      rhythms = CellForm.makeCellForms(cell, serial, marked, rhythms,
          flavor);
    }
  else
    {
    cells[flavor] = CellForm.makeCellForms(cell, serial, marked, cells[flavor],
          flavor);
    }

  return true;
  }


/**
 * Add a Lick or Quote form to the database.
 */
static boolean addLick(int flavor, Polylist lick, int serial, int grade,
                       boolean marked,
                       boolean allowDuplicates, int otherFlavor)
  {
  Trace.log(3, "adding lick of type " + flavor + ": " + lick);

  Polylist oldList = licks[flavor];

  licks[flavor] = LickForm.makeLickForm(lick, serial, marked, oldList, flavor,
          grade,
          allowDuplicates, licks[otherFlavor]);

  // If they're the same, then we don't need to do anything -- nothing got added.
  if( licks[flavor] == oldList )
    {
    return false;
    }
  else
    {
    // If they're different but the same length, then something got overwritten, 
    // and we need to remove the old copy.
    if( licks[flavor].length() == oldList.length() )
      {
      removeRule(lick);      // Now we need to save.
      }
    return true;
    }
  }


/**
 * Returns a Polylist tree containing Advice that can be converted
 * into a JTree for display.
 * @param score     a Score to get Advice about
 * @param index     the slot to get Advice about
 * @return Polylist the tree of Advice created
 */
public Polylist getAdviceTree(Score score, int index, Note note)
  {
  if( score == null || index < 0 )
    {
    return null;
    }

  // Get this area's chord, if any

  ChordPart chordPart = score.getChordProg();

  Chord chord = chordPart.getCurrentChord(index);

  int chordIndex = chordPart.getCurrentChordIndex(index);

  // Find the next unique chord (if no next chord, set nextChord to null)

  int nextChordIndex = chordPart.getNextUniqueChordIndex(index);

  Chord nextChord = null;

  ChordSymbol nextChordSymbol = null;

  int interChordHalfBeats = 0;

  metre = score.getMetre();

  if( nextChordIndex > 0 )
    {
    nextChord = chordPart.getChord(nextChordIndex);
    nextChordSymbol = nextChord.getChordSymbol();
    interChordHalfBeats = (nextChordIndex - index + 1) / slotsPerHalfBeat;
    }

  // Treat a rest as no note.

  if( note != null && note.isRest() )
    {
    note = null;
    }

  Key scoreKey = Key.getKey(score.getKeySignature());

  if( Preferences.getPreference(Preferences.ADV_CACHE_ENABLED).equals("true") )
    {
    return getAdviceTreeWithCaching(chord.getChordSymbol(),
            nextChordSymbol,
            interChordHalfBeats,
            note,
            true,
            scoreKey);
    }
  else
    {
    return getAdviceTree(chord.getChordSymbol(),
            nextChordSymbol,
            interChordHalfBeats,
            note,
            true,
            scoreKey);
    }
  }

/**
 * Implements a simple cache for combinations chord, nextChord, note, subs)
 */
static Cache adviceTreeCache = new Cache(adviceTreeCacheLimit,
        new AdviceTreeCacheComparator());


public static int getCacheCapacity()
  {
  return adviceTreeCache.getCapacity();
  }


public static void setCacheCapacity(int capacity)
  {
  adviceTreeCache.setCapacity(capacity);
  }


public static void purgeCache()
  {
  adviceTreeCache.clearCache();
  }


/**
 * Get the advice tree from cache if possible.  Otherwise, compute it and put
 * the result in the cache.
 */
public Polylist getAdviceTreeWithCaching(ChordSymbol chordSymbol,
                                         ChordSymbol nextChordSymbol,
                                         int interChordHalfBeats,
                                         Note note,
                                         boolean subs,
                                         Key targetKey)
  {
  if( Preferences.getPreference(Preferences.ADV_CACHE_ENABLED).equals("false") )
    {
    return null;
    }
  if( chordSymbol == null || chordSymbol.isNOCHORD() )
    {
    return null;
    }

  int notePitch = note == null
          ? -1
          : note.getPitch();

  String chordName = chordSymbol.getName();
  String nextChordName = nextChordSymbol == null
          ? null
          : nextChordSymbol.getName();

  AdviceTreeCacheKey key = new AdviceTreeCacheKey(chordName,
          nextChordName,
          interChordHalfBeats,
          notePitch,
          subs);

  Polylist tree = (Polylist)adviceTreeCache.get(key);

  if( tree == null )
    {
    Trace.log(2,
            "from scratch: " + chordName + " " + nextChordName + " " + notePitch + " " + subs);

    // compute
    tree = getAdviceTree(chordSymbol,
            nextChordSymbol,
            interChordHalfBeats,
            note,
            subs,
            targetKey);

    //System.out.println("to cache: " + key);
    adviceTreeCache.put(key, tree);
    }
  else
    {
    Trace.log(2,
            "from cache: " + chordSymbol + " " + nextChordSymbol + " " + notePitch + " " + subs);
    }

  return tree;
  }


public Polylist getAdviceTree(ChordSymbol chordSymbol,
                              ChordSymbol nextChordSymbol,
                              int interChordHalfBeats,
                              Note note,
                              boolean subs,
                              Key targetKey)
  {

  Trace.log(2, "seeking advice on" + " chordSymbol = " + (chordSymbol == null
          ? "null"
          : chordSymbol.getName()) + ", note = " + (note == null
          ? "null"
          : note.toLeadsheet()));

  int mask = Integer.parseInt(Preferences.getPreference(
          Preferences.VIS_ADV_COMPONENTS));

  if( chordSymbol.isNOCHORD() )
    {
    Trace.log(2, "returning with no advice");
    return null;
    }

  // Get the info on this specific chord, if chord is not in table,
  // set chordForm to null

  ChordForm chordForm = chordSymbol.getChordForm();

  if( chordForm == null )
    {
    System.out.println(
            "**** severe error, chordForm for " + chordSymbol + " is null ");
    }


  // Create the label for the advice menu

  String adviceLabel;

  // NOTE: Leave space at start so it won't be selected by key strokes

  if( nextChordSymbol == null )
    {
    if( note == null )
      {
      adviceLabel =
              " Advice for " + chordSymbol.getName() + " (unspecified starting note)";
      }
    else
      {
      adviceLabel =
              " Advice for " + chordSymbol.getName() + " starting with note " + note.getPitchClassName();
      }
    }
  else
    {
    if( note == null )
      {
      adviceLabel =
              " Advice for " + chordSymbol.getName() + " -> " + nextChordSymbol.getName() + " (unspecified starting note)";
      }
    else
      {
      adviceLabel =
              " Advice for " + chordSymbol.getName() + " -> " + nextChordSymbol.getName() + " starting with " + note.getPitchClassName();
      }
    }
  // The root is called "Advice for chord" where chord is the chord
  Polylist advice = Polylist.list(adviceLabel);

  String chordName = chordSymbol.getName();
  String chordRoot = chordSymbol.getRootString();
  PitchClass rootClass = chordSymbol.getRoot();

  // If there is a next chord, get its info
  String nextChordName = null;
  String nextChordRoot = null;

  ChordForm nextChordInfo = null;

  if( nextChordSymbol != null )
    {
    nextChordName = nextChordSymbol.getName();
    nextChordRoot = nextChordSymbol.getRootString();
    nextChordInfo = nextChordSymbol.getChordForm();
    }

  // Generate Advice based on the chord info
  // Various items will be consed to a list representing a tree
  // and reversed at the end.

  if( chordForm != null )
    {

    Key key = chordForm.getKey(chordRoot);

    // add chord tones to tree

    if( (CHORDTREE & mask) != 0 )
      {
      advice = advice.cons(getChordTree(chordSymbol,
              nextChordInfo,
              nextChordRoot,
              nextChordName,
              key));      // add color tones to tree
      }
    if( (COLORTREE & mask) != 0 )
      {
      advice = advice.cons(getColorTree(chordSymbol,
              nextChordInfo,
              nextChordRoot,
              nextChordName,
              key));      // add approach tones to tree (note different protocol, 
      // because the approach tree nodes are spliced in rather than added as a unit
      }
    if( (APPROACHTREE & mask) != 0 )
      {
      advice = getApproachTree(chordSymbol,
              nextChordInfo,
              nextChordRoot,
              nextChordName,
              note).append(advice);      // add scale tones to tree
      }
    if( (SCALETREE & mask) != 0 )
      {
      advice = advice.cons(getScaleTree(chordSymbol, note, key));      // Only include cells, idioms, licks, and quotes, substitions,
      // and extensions at the top level, not for substitutions and extensions.
      // That is what the boolean 'subs' controls.
      }
    if( subs )
      {
      int rise = PitchClass.findRise(chordRoot);

      // add substitutions to tree

      Polylist substitutions = chordForm.getSubstitutions();

      // old  way substitutions = Key.transposeChordList(substitutions, rise, key);

      substitutions = ChordSymbol.chordSymbolsFromStrings(substitutions);

      substitutions = ChordSymbol.transpose(substitutions, rise);

      /* Temporarily omit
      Polylist subsTree = getSubsTree(substitutions, 
      chordSymbol,
      nextChordSymbol, 
      note, 
      key).cons(" chord substitutions");
      
      if(subsTree.rest().nonEmpty() && (SUBSTREE & mask) != 0)
      {
      advice = advice.cons(subsTree);
      }
       */
      /* For now, just list the names of the substitutions. */

      if( substitutions.nonEmpty() )
        {
        advice = advice.cons(substitutions.cons(" chord substitutions"));
        }

      // add extensions to tree

      String finalChordName = getFinalName(chordName);

      Polylist extensions = getExtensions(finalChordName);

      extensions = ChordSymbol.chordSymbolsFromStrings(getExtensions(
              finalChordName));

      extensions = ChordSymbol.transpose(extensions, rise);

      // extensions = Key.transposeChordList(extensions, rise, key);

      /* Temporarily omit
      Polylist extTree = getSubsTree(extensions, 
      chordSymbol,
      nextChordSymbol, 
      note, 
      key).cons(" chord extensions");
      
      if(extTree.rest().nonEmpty() && (EXTSTREE & mask) != 0)
      {
      advice = advice.cons(extTree);
      }
       */

      /* For now, just list the names of the extensions */

      if( extensions.nonEmpty() )
        {
        advice = advice.cons(extensions.cons(" chord extensions"));
        }

      // add rhythms to tree

      Polylist rhythmTree = getCellTree(RHYTHM_CELL,
              chordSymbol,
              note,
              extensions,
              Polylist.nil,
              key);
      if( rhythmTree.nonEmpty() && (RHYTHMTREE & mask) != 0 )
        {
        advice = advice.cons(rhythmTree.cons(" rhythms"));
        }

      // add cells to tree

      Polylist subExtensions = ChordSymbol.chordSymbolsFromStrings(
              getSubExtensions(finalChordName));

      Polylist cellTree = getCellTree(PLAIN_CELL,
              chordSymbol,
              note,
              extensions,
              subExtensions,
              key);

      if( cellTree.nonEmpty() && (CELLTREE & mask) != 0 )
        {
        advice = advice.cons(cellTree.cons(" cells"));
        }

      // add idioms to tree

      Polylist idiomTree = getCellTree(IDIOM_CELL,
              chordSymbol,
              note,
              extensions,
              subExtensions,
              key);

      if( idiomTree.nonEmpty() && (IDIOMTREE & mask) != 0 )
        {
        advice = advice.cons(idiomTree.cons(" idioms"));
        }

      // add licks to tree

      Polylist lickTree = getLickTree(PLAIN_LICK,
              chordSymbol,
              nextChordSymbol,
              interChordHalfBeats,
              note,
              extensions,
              subExtensions,
              key);

      if( lickTree.nonEmpty() && (LICKTREE & mask) != 0 )
        {
        advice = advice.cons(lickTree.cons(" licks"));
        }

      // add quotes to tree

      Polylist quotesTree = getLickTree(QUOTE_LICK,
              chordSymbol,
              nextChordSymbol,
              interChordHalfBeats,
              note,
              extensions,
              subExtensions,
              key);

      if( quotesTree.nonEmpty() && (QUOTETREE & mask) != 0 )
        {
        advice = advice.cons(quotesTree.cons(" quotes"));
        }
      
      // add bricks to tree

      Polylist bricksTree = getLickTree(BRICK_LICK,
              chordSymbol,
              nextChordSymbol,
              interChordHalfBeats,
              note,
              extensions,
              subExtensions,
              key);

      if( bricksTree.nonEmpty() || (BRICKTREE & mask) != 0 )
        {
        advice = advice.cons(bricksTree.cons(" bricks"));        
        }
      }
    }

  
  return removeStubs(advice.reverse());
  }

/**
 * Add a new voicing to a ChordSymbol
 *
   @param chordSymbol
   @param id
   @param type
   @param notes
   @param extension
   */

public void addVoicing(String chordString, String id, String type, Polylist notes, Polylist extension)
{
  ChordSymbol symbol = ChordSymbol.makeChordSymbol(chordString);
  addVoicing(symbol, id, type, notes, extension);
}

        
private void addVoicing(ChordSymbol chordSymbol, String id, String type, Polylist notes, Polylist extension)
{
if( chordSymbol == null )
  {
    return;
  }
ChordForm chordForm = chordSymbol.getChordForm();
chordForm.addVoicing(id, type, notes, extension);
}

public void removeVoicing(String chordString, String id)
{
ChordSymbol chordSymbol = ChordSymbol.makeChordSymbol(chordString);
ChordForm chordForm = chordSymbol.getChordForm();
if( chordForm.isSynonym() )
  {
  return; // can't remove synonym
  }
chordForm.removeVoicing(id);
}


public void removeNthVoicing(String chordString, int position)
{
ChordSymbol chordSymbol = ChordSymbol.makeChordSymbol(chordString);
ChordForm chordForm = chordSymbol.getChordForm();
chordForm.removeNthVoicing(position);
}


/**
 * Get extensions of the chord type root in C.
 * In general, extensions will need to be transposed by the caller.
 */
public static Polylist getExtensions(String chordName)
  {
  chordName = Key.makeCroot(chordName);
  for( int i = 0; i < numChords; i++ )
    {
    Polylist entry = invertedExtensionsTable[i];
    if( entry.first().equals(chordName) )
      {
      return entry.rest();
      }
    }
  return Polylist.nil;
  }


Polylist getSubExtensions(String chordName)
  {
  chordName = Key.makeCroot(chordName);
  Polylist L = extensionsTable;
  while( L.nonEmpty() )
    {
    Polylist entry = (Polylist)L.first();
    if( entry.first().equals(chordName) )
      {
      return entry.rest();
      }
    L = L.rest();
    }
  return Polylist.nil;
  }


/**
 * Based on the current chord and the next chord, this method constructs
 * the 'chord tone' advice tree.
 * @param chordForm         a ChordForm containing info on this chord
 * @param nextChordInfo     a ChordForm containing info on next chord
 * @param chordRoot         a String containing the root of this chord
 * @param nextChordRoot     a String containing the root of next chord
 * @param nextChordName         a String containing the untransposed name
 *                          of the next chord for display
 * @return Polylist         the 'chord tone' advice tree
 */
public Polylist getChordTree(ChordSymbol chordSymbol,
                             ChordForm nextChordInfo,
                             String nextChordRoot,
                             String nextChordName,
                             Key key)
  {

  String chordRoot = chordSymbol.getRootString();

  ChordForm chordForm = chordSymbol.getChordForm();

  NoteSymbol rootSymbol = NoteSymbol.makeNoteSymbol(chordRoot);

  Polylist rootPosition = chordForm.getSpell(chordRoot, key);
  
  // Kevin and Audrey: For illustration: The next two statements should be commented out later.
  
  boolean vector[] = chordForm.getSpellVector(chordRoot, key);
  
  ArrayList<Integer> chordMIDI = chordForm.getSpellMIDIarray(chordRoot);
  ArrayList<Integer> colorMIDI = chordForm.getColorMIDIarray(chordRoot);
 
//  System.out.println("ChordSymbol: " + chordSymbol + " bit vector: " + NoteSymbol.showContents(vector) 
//                     + ", chord MIDI: " + chordMIDI
//                     + ", color MIDI: " + colorMIDI);
  
  int rise = PitchClass.findRise(chordRoot);

  assert (rootPosition != null);

  // the root of the tree describes it
  // put a space first so it will not be selected by key stroke

  Polylist chordTree = Polylist.list(" chord tones");

  // get the note spelling in priority order if available, otherwise
  // just use the root position

  Polylist chordSpell = chordForm.getPriority(chordRoot, key);

  ChordSymbol polybase = chordSymbol.getPolybase();

  if( polybase != null )
    {
    // Special handling for polychords

    String polybaseRoot = polybase.getRoot().toString();
    ChordForm polybaseForm = polybase.getChordForm();

    Polylist polybaseSpell = polybaseForm.getSpell(polybaseRoot, key);
    chordSpell = NoteSymbol.closedVoicing(polybaseSpell.append(rootPosition));
    rootPosition = chordSpell;
    }
  else if( chordSymbol.isSlashChord() )
    {
    NoteSymbol bassNote = new NoteSymbol(chordSymbol.getBass());
    chordSpell = NoteSymbol.closedVoicing((bassNote.enhDrop(rootPosition)).cons(
            bassNote));
    rootPosition = chordSpell;
    }

  Trace.log(2, "chordSpell = " + chordSpell + ", key = " + key);

  // if there is a next chord, process its tones,
  // then see what is in common

  if( nextChordInfo != null )
    {
    Polylist nextChordSpell = nextChordInfo.getPriority(nextChordRoot);

    // get the key to transpose the next chord to

    Polylist common = Polylist.nil;
    Polylist uncommon = Polylist.nil;

    // go through each chord tone
    PolylistEnum e = chordSpell.elements();
    while( e.hasMoreElements() )
      {
      NoteSymbol ob = (NoteSymbol)e.nextElement();
      Object commonTone = null;

      // go through each chord tone from the next chord
      PolylistEnum f = nextChordSpell.elements();
      while( f.hasMoreElements() )
        {
        NoteSymbol nextOb = (NoteSymbol)f.nextElement();
        if( ob.enharmonic(nextOb) )
          {
          commonTone = ob;
          }
        }

      // organize common and uncommon tones
      if( commonTone == null )
        {
        uncommon = uncommon.cons(ob);
        }
      else
        {
        common = common.cons(commonTone);
        }
      }
    common = common.reverse();
    uncommon = uncommon.reverse();

    // build trees for each category
    // put a space first so it will not be selected by key stroke

    chordTree = chordTree.cons(
            getExtendedNoteAdvice(" chord", rootPosition, chordSpell, key));

    if( common.nonEmpty() )
      {
      chordTree = chordTree.cons(
              Polylist.cons(" common with next chord (" + nextChordName + ")",
              getNoteAdvice(common)));
      }
    if( uncommon.nonEmpty() )
      {
      chordTree = chordTree.cons(
              Polylist.cons(
              " not common with next chord (" + nextChordName + ")",
              getNoteAdvice(uncommon)));
      }
    }
  else
    // if there is no next chord, just generate advice for the chord tones
    {
    chordTree = chordTree.cons(
            getExtendedNoteAdvice(" chord",
            rootPosition,
            chordSpell,
            key));
    }
  return chordTree.reverse();
  }


static Polylist makeAscending(Polylist tones)
  {
  // Strategy: In the processing of creating a new list, compute the high and low offsets
  // after transformation.
  // If the high is above a threshold, but the low is not below, lower everything.
  // Everything will initially be transformed to the octave above middle C.
  // Otherwise, leave as is.

  int highThreshold = 21; // a
  int lowThreshold = 7;  // g

  if( tones.isEmpty() )
    {
    return Polylist.nil;
    }

  NoteSymbol previous = (NoteSymbol)tones.first();
  tones = tones.rest();

  Polylist result = Polylist.list(previous);

  int steps = previous.getMIDI();

  int low = steps;
  int high = steps;

  while( tones.nonEmpty() )
    {
    NoteSymbol next = (NoteSymbol)tones.first();
    int steps2 = next.getMIDI();

    int trans = 0;
    while( steps2 < steps )
      {
      steps2 += OCTAVE;
      trans += OCTAVE;
      }

    result = result.cons(next.transpose(trans));

    if( steps2 > high )
      {
      high = steps2;
      }
    else if( steps2 < low )
      {
      low = steps2;
      }
    previous = next;
    steps = steps2;
    tones = tones.rest();
    }

  // note: tones == nil.
  // note: result has notes stacked in reverse order.

  return result.reverse();

  /*
  if( high > highThreshold && low >= lowThreshold )
  {
  // lower everything
  
  while( result.nonEmpty() )
  {
  NoteSymbol next = (NoteSymbol) result.first();
  
  tones = tones.cons(next);
  
  result = result.rest();
  }
  return tones;
  }
  else
  {
  return result.reverse();
  }
   */
  }


static Polylist makeDecending(Polylist tones)
  {
  return makeAscending(tones).reverse();
  }


/**
 * Based on the current chord and the next chord, this method constructs
 * the 'color tone' advice tree.
 * @param chordForm         a ChordForm containing info on this chord
 * @param nextChordInfo     a ChordForm containing info on next chord
 * @param chordRoot         a String containing the root of this chord
 * @param nextChordRoot     a String containing the root of next chord
 * @param nextChordName         a String containing the untransposed name
 *                          of the next chord for display
 * @return Polylist         the 'chord tone' advice tree
 */
public Polylist getColorTree(ChordSymbol chordSymbol,
                             ChordForm nextChordInfo,
                             String nextChordRoot,
                             String nextChordName,
                             Key key)
  {

  ChordForm chordForm = chordSymbol.getChordForm();
  String chordRoot = chordSymbol.getRootString();

  Polylist colorTree = Polylist.list(" color tones (\"tensions\")");

  // get the chord's color notes and transpose them

  Polylist ex = chordForm.getColor(chordRoot); //buildExtensions(chordForm, Polylist.nil).reverse();

// Transposition is handled within getColor
//  int rise = PitchClass.findRise(chordRoot);
//  ex = NoteSymbol.transposeNoteSymbolList(ex, rise);

  // if there is a next chord, process its tones and extensions, 
  // then see what is in common

  if( nextChordInfo != null )
    {

    Polylist nextChordSpell = nextChordInfo.getPriority(nextChordRoot);

    //Key nextKey = nextChordInfo.getKey();

    // get the extensions from the next chord

    Polylist nextEx = nextChordInfo.getColor(chordRoot); // buildExtensions(nextChordInfo, Polylist.nil).reverse();

// Transposition is handled within getColor
//    int rise1 = PitchClass.findRise(nextChordRoot);
//    nextEx = NoteSymbol.transposeNoteSymbolList(nextEx, rise1);

    Polylist commonColor = Polylist.nil;
    Polylist uncommonColor = Polylist.nil;

    // go through each color tone

    PolylistEnum e = ex.elements();
    while( e.hasMoreElements() )
      {
      NoteSymbol ob = (NoteSymbol)e.nextElement();
      Object commonTone = null;

      // go through each chord and color tone in the next chord
      PolylistEnum f = nextChordSpell.append(nextEx).elements();
      while( f.hasMoreElements() )
        {
        NoteSymbol nextOb = (NoteSymbol)f.nextElement();
        if( ob.enharmonic(nextOb) )
          {
          commonTone = ob;
          }
        }

      // organize common and uncommon tones
      if( commonTone == null )
        {
        uncommonColor = uncommonColor.cons(ob);
        }
      else
        {
        commonColor = commonColor.cons(commonTone);
        }
      }

    commonColor = commonColor.reverse();
    uncommonColor = uncommonColor.reverse();

    // build trees for each category
    colorTree = colorTree.cons(
            Polylist.cons(" general",
            getNoteAdvice(ex)));

    if( commonColor.nonEmpty() )
      {
      colorTree = colorTree.cons(
              Polylist.cons(" common with next chord (" + nextChordName +
              ") or its extensions",
              getNoteAdvice(commonColor)));
      /*
      if(uncommonColor.nonEmpty())
      colorTree = colorTree.cons(
      Polylist.cons(" not common w/next chord or its extensions",
      getNoteAdvice(uncommonColor)));
       */
      }
    }
  else
    // if there is no next chord, just show the color tones
    {
    colorTree = colorTree.cons(
            Polylist.cons(" general",
            getNoteAdvice(ex)));
    }
  return colorTree.reverse();
  }


/**
 * Based on the current chord and next chord, this method constructs
 * the 'approach tone' advice tree.
 * @param chordForm         a ChordForm containing info on this chord
 * @param nextChordInfo     a ChordForm containing info on next chord
 * @param chordRoot         a String containing the root of this chord
 * @param nextChordRoot     a String containing the root of next chord
 * @param nextChordName         a String containing the untransposed name
 *                          of the next chord for display
 * @return Polylist         the 'approach tone' advice tree
 */
public Polylist getApproachTree(ChordSymbol chordSymbol,
                                ChordForm nextChordInfo,
                                String nextChordRoot,
                                String nextChordName,
                                Note note)
  {

  ChordForm chordForm = chordSymbol.getChordForm();
  String chordRoot = chordSymbol.getRootString();

  Polylist approachTree = Polylist.nil;
  Polylist thisAppTones = chordForm.getApproach(chordRoot);

  Polylist currentTree = Polylist.list(
          " tones approaching target in current chord");

  // go through each target tone in the current chord

  Polylist chordSpell = chordForm.getSpell(chordRoot);

  PolylistEnum e = thisAppTones.elements();

  while( e.hasMoreElements() )
    {

    Polylist atones = (Polylist)e.nextElement();

    // tone is the tone approached

    NoteSymbol approached = (NoteSymbol)atones.first();

    Polylist targetTree = Polylist.list(
            " approaching " + approached.getPitchString());

    // for each approach to the current target, maybe generate advice

    PolylistEnum approaches = atones.rest().elements();

    while( approaches.hasMoreElements() )
      {

      // appoaching is the approaching tone

      NoteSymbol approaching = (NoteSymbol)approaches.nextElement();

      // If current note selected, only use approach tones that match it.

      if( note == null || approaching.enharmonic(NoteSymbol.makeNoteSymbol(note)) )
        {
        targetTree = targetTree.cons(
                new ApproachAdvice(approaching.getPitchString(),
                approaching,
                approached));
        }
      }
    currentTree = currentTree.cons(targetTree.reverse());
    }
  approachTree = approachTree.cons(currentTree.reverse());

  if( nextChordInfo != null )
    {
    Polylist nextChordSpell = nextChordInfo.getSpell(nextChordRoot);

    Polylist nextAppTones = nextChordInfo.getApproach(nextChordRoot);

    Polylist nextTree =
            Polylist.list(
            " chord tones approaching target in next chord (" + nextChordName + ")");

    Polylist nextTree2 =
            Polylist.list(
            " non-chord tones approaching target in next chord (" + nextChordName + ")");

    // go through each target tone in the next chord

    e = nextAppTones.elements();
    while( e.hasMoreElements() )
      {
      Polylist atones = (Polylist)e.nextElement();

      NoteSymbol tone = (NoteSymbol)atones.first();

      Polylist targetTree = Polylist.nil;
      Polylist secondaryTargetTree = Polylist.nil;

      // go through each approach tone
      PolylistEnum approaches = atones.rest().elements();
      while( approaches.hasMoreElements() )
        {

        NoteSymbol approach = (NoteSymbol)approaches.nextElement();

        if( note == null || approach.enharmonic(NoteSymbol.makeNoteSymbol(note)) )
          {
          PolylistEnum spelling = chordSpell.elements();

          boolean inThisChord = false;
          while( spelling.hasMoreElements() )
            {
            NoteSymbol letter = (NoteSymbol)spelling.nextElement();
            if( approach.enharmonic(letter) )
              {
              inThisChord = true;
              }
            }

          // generate advice and put it in the proper category
          // if the approach tone is in this chord or not

          ApproachAdvice approachAdvice =
                  new ApproachAdvice(approach.getPitchClass().toString(),
                  approach,
                  tone);
          if( inThisChord )
            {
            targetTree = targetTree.cons(approachAdvice);
            }
          else
            {
            secondaryTargetTree = secondaryTargetTree.cons(approachAdvice);
            }
          }
        }


      if( secondaryTargetTree.nonEmpty() )
        {
        nextTree2 = nextTree2.cons(
                secondaryTargetTree.reverse().cons(
                " approaching " + tone.getPitchString()));
        }


      if( targetTree.nonEmpty() )
        {
        nextTree = nextTree.cons(
                targetTree.reverse().cons(
                " approaching " + tone.getPitchString()));
        }
      }

    if( nextTree.rest().nonEmpty() )
      {
      approachTree = approachTree.cons(nextTree.reverse());
      }

    if( nextTree2.rest().nonEmpty() )
      {
      approachTree = approachTree.cons(nextTree2.reverse());
      }
    }
  return approachTree;
  }


/**
 * Checks the existence of a style
 */
public static boolean styleExists(String name)
  {
  return getStyle(name) != null;
  }


/**
 * Get style from list of all styles
 */
public static Style getStyle(String name)
  {
  if( Style.noStyles() )
    {
    ErrorLog.log(ErrorLog.SEVERE,
            "There are no styles. This will be a major problem.");
    return null;
    }
  
  Style style = Style.getStyle(name);
  
  if( style == null )
    {
    String defaultStyle = Preferences.getPreference(Preferences.DEFAULT_STYLE);
    
    // Don't warn more than once about a given style.
    if( !name.equals(Style.USE_PREVIOUS_STYLE) && !styleWarnings.contains(name) )
      {
      styleWarnings.add(name);
      ErrorLog.log(ErrorLog.WARNING,
              "Requested style '" + name + "' not found, using default: " + defaultStyle + ".");
      }
    style = Style.getStyle(defaultStyle);
    
    if( style == null )
      {
      ErrorLog.log(ErrorLog.SEVERE,
              "Default style '" + defaultStyle + "' not found. This will be a problem.");
      return null;
      }
    }

  return style;
  }

public static void addStyle(String name, Style style)
  {
    Style.setStyle(name, style);
  }

public static void addStyle(Style style)
  {
    addStyle(style.getName(), style);
  }

public static ArrayList<Polylist> getVoicingTable(String chordRoot, String bass,
                                               Style style)
  {
  ArrayList<Polylist> data = new ArrayList<Polylist>();

  PolylistEnum cenum = chords.elements();
  while( cenum.hasMoreElements() )
    {
    Polylist item = (Polylist)cenum.nextElement();

    String nameInTable = (String)item.first();

    ChordSymbol symbol = ChordSymbol.makeChordSymbol(nameInTable);

    ChordForm form = (ChordForm)item.second();

    int rise = PitchClass.findRise(chordRoot);

    symbol = symbol.transpose(rise);

    if( !bass.equals("") )
      {
      symbol = new ChordSymbol(symbol.getRoot(), symbol.getType(),
              PitchClass.getPitchClass(bass), symbol.getPolybase());
      }

    // Get voicings from vocabulary
    
    Polylist voicingInfo = form.getVoicingInfo(chordRoot);

    // Get generated voicing

    Polylist voicing = 
        ChordPattern.findVoicing(symbol, style.getChordBase(), style, false);

    if( voicing != null )
      {
      String id = GENERATED_VOICING_NAME;
      String type = GENERATED_VOICING_TYPE;

      if( form.isSynonym() )
        {
        // Special handling in case the chord name is a synonym for another.

        String finalName = getFinalName(nameInTable);
        ChordSymbol redirectSymbol = ChordSymbol.makeChordSymbol(finalName);
        redirectSymbol = redirectSymbol.transpose(rise);

        String transposedName = redirectSymbol.toString();

        id = VOICING_REDIRECT_PREFIX + transposedName + ")";
        type = "";
        voicing = Polylist.nil;
        }

      // Add voicing to the list.
      voicingInfo =
              voicingInfo.cons(Polylist.list(id, type, voicing, Polylist.nil));
      }

   
    PolylistEnum venum = voicingInfo.elements();

    while( venum.hasMoreElements() )
      {
      voicing = (Polylist)venum.nextElement();

      symbol = ChordSymbol.makeChordSymbol(symbol.toString());

      if( !voicing.first().equals("generated") )
        {
        symbol.setVoicing((Polylist)voicing.third());
        symbol.setExtension((Polylist)voicing.fourth());
        }

      boolean good = ChordPattern.goodVoicing(symbol, style);

      voicing = Polylist.list(
              symbol,
              voicing.first(),
              voicing.second(),
              NoteSymbol.makePitchStringList(
              (Polylist)voicing.third()),
              NoteSymbol.makePitchStringList(
              (Polylist)voicing.fourth()),
              good);

      data.add(voicing);
      }
    }

  return data;
  }


Polylist makeVoicing(String id, String type, Polylist notes, Polylist extension)
{
        return Polylist.list(id, type, notes, extension);
}

/**
 * Checks the existence of a scale type (no tonic is named)
 */
boolean scaleExists(String name)
  {
  return getScale(name) != null;
  }


/**
 * Get scale from list of all scales
 */
public static ScaleForm getScale(String name)
  {
  int depth = 0;
  while( depth < depthLimit )
    {
    Polylist item = scales.assoc(name);
    if( item == null )
      {
      return null;
      }
    ScaleForm form = (ScaleForm)item.second();
    String same = form.getSame();
    if( same == null )
      {
      return form;
      }
    else
      {
      name = same;	// iteration
      }
    depth++;
    }
  return null;
  }


/**
 * Returns a Polylist containing the spelling of a scale.
 * We have to transpose the key twice.  All scales start
 * out with a tonic of 'c'.  Then we need to transpose it to
 * a tonic relative to a chord with the root of 'c'.  Then we
 * need to transpose it to a tonic relative to a chord with the
 * original chord root.
 * @param scaleType         a String containing the scale type
 * @param tonics            a Polylist containing the scale root
 *                          and chord root, for transposing
 * @return Polylist         the scale spelling list
 */
public Polylist getScaleSpell(String scaleType,
                              NoteSymbol scaleTonic,
                              String chordRoot,
                              ChordForm chordForm,
                              Note firstNote,
                              Key targetKey)
  {

  ScaleForm form = getScale(scaleType);

  if( form == null )
    {
    return null;
    }

  // transpose the scale to the root specified in the "scales" section
  // This is essential, because not all scales have the same root in that section.

  Polylist tones = form.getSpell(scaleTonic);

  assert (tones != null);

  // transpose scale tones to the key of the chord
  // Making the key below Ckey made a big difference in the right direction,
  // but it is still not totally correct.  Want something like the key sig of the tonic
  // Making it key1 seems better.

  // get the keys for the transpositions

  Key key1 = chordForm.getKey(chordRoot);

  int rise1 = PitchClass.findRise(chordRoot);

  tones = NoteSymbol.transposeNoteSymbolList(tones, rise1);

  // Note: we could do the above in one transposition by "composing" first and second

  String tonic = ((NoteSymbol)tones.first()).getPitchClass().toString();

  // If first note is specified, then only include scale
  // if first note is contained within.  In this case,
  // rotate the scale so that it begins with first note
  // specified, which might not be the tonic.

  if( firstNote != null )
    {
    NoteSymbol firstNoteSymbol = NoteSymbol.makeNoteSymbol(firstNote);

    if( !firstNoteSymbol.enhMember(tones) )
      {
      return null;
      }

    tones = rotateScale(tones.rest(), firstNoteSymbol);

    // rest is because tones is assumed to include a duplicate tonic,
    // one at each end.
    }

  Polylist scaleName = Polylist.list(tonic, " ", scaleType);

  return getExtendedNoteAdvice(" " + scaleName.implode(), tones, tones,
          targetKey);
  }


/**
 * Extended note advice is like note advice, except that the 
 * notes are also made available as groups, but ascending and descending
 * @param name     the name of this subtree
 * @param tones    a Polylist containing notes
 * @param keyName  name of the target key
 * @return an advice sub-tree as a Polylist
 */
public Polylist getExtendedNoteAdvice(String name, Polylist rootPosition,
                                      Polylist priority, Key key)
  {
  Polylist ascending = makeAscending(rootPosition);
  Polylist descending = ascending.reverse();
  return getNoteAdvice(priority).cons(new AdviceForScale(
          " " + name + ", descending, then ascending ", descending.append(
          ascending.rest()), LCROOT, key, metre, 1)).cons(new AdviceForScale(
          " " + name + ", ascending, then descending ", ascending.append(
          descending.rest()), LCROOT, key, metre, 1)).cons(new AdviceForScale(
          " " + name + ", descending ", descending, LCROOT, key, metre, -1)).cons(new AdviceForScale(
          " " + name + ", ascending ", ascending, LCROOT, key, metre, 1)).cons(
          name);
  }


/**
 * Converts a Polylist containing notes to a Polylist containing
 * AdviceForNote objects.
 * @param noteList  a Polylist containing notes
 * @return Polylist a Polylist containing AdviceForNote objects
 */
public Polylist getNoteAdvice(Polylist noteList)
  {
  if( noteList.isEmpty() )
    {
    return Polylist.nil;
    }
  NoteSymbol noteSymbol = (NoteSymbol)noteList.first();
  return Polylist.cons(new AdviceForNote(noteSymbol, metre),
          getNoteAdvice(noteList.rest()));
  }


/**
 * Rotate the scale so that noteName is first and last
 * Only call when noteSymbol known to be in elements.
 */
static Polylist rotateScale(Polylist elements, NoteSymbol noteSymbol)
  {
  Polylist stack = Polylist.nil;
  while( elements.nonEmpty() )
    {
    NoteSymbol scaleElement = (NoteSymbol)elements.first();
    if( scaleElement.enharmonic(noteSymbol) )
      {
      return elements.append(stack.cons(scaleElement).reverse());
      }
    stack = stack.cons(scaleElement);
    elements = elements.rest();
    }
  return Polylist.nil;  // should not happen 
  }


/**
 * Takes info on a chord, and a Polylist containing the chordRoot
 * and returns a 'scale tone' advice tree.
 * @param chordForm         a ChordForm containing chord info

 * @return Polylist         the 'scale tone' advice tree
 */
public Polylist getScaleTree(ChordSymbol chordSymbol, Note firstNote,
                             Key targetKey)
  {

  ChordForm chordForm = chordSymbol.getChordForm();
  String chordRoot = chordSymbol.getRootString();

  Polylist scaleKnow = chordForm.getScales();
  Polylist scaleTree = Polylist.list(" scale tones");

  // go through each scale and generate advice for those scales

  while( scaleKnow.nonEmpty() )
    {

    Polylist scaleElement = (Polylist)scaleKnow.first();

    NoteSymbol tonic = NoteSymbol.makeNoteSymbol((String)scaleElement.first());

    String scaleType = concatListWithSpaces(scaleElement.rest());

    Polylist scaleSpell = getScaleSpell(scaleType,
            tonic,
            chordRoot,
            chordForm,
            firstNote,
            targetKey);
    if( scaleSpell != null )
      {
      scaleTree = scaleTree.cons(scaleSpell);
      }
    scaleKnow = scaleKnow.rest();
    }

  return scaleTree.reverse();
  }


/**
 * Tell whether a chord symbol is actually punctuation
 */
static boolean isPunctuation(String chord)
  {
  return chord.equals(BAR) || chord.equals(SLASH);
  }


static Polylist dePunctuate(Polylist L)
  {
  if( L.isEmpty() )
    {
    return L;
    }

  Polylist rest = dePunctuate(L.rest());

  Object first = L.first();

  if( first instanceof String )
    {
    String firstString = (String)L.first();

    if( isPunctuation(firstString) )
      {
      return rest;
      }
    }

  return rest.cons(L.first());
  }


/**
 * Based on the current chord, this method constructs the 'cell'
 * advice tree.
 * @param chordForm         a ChordForm  containing info on this chord
 * @param chordRoot         a String containing the root of this chord
 * @return Polylist         the 'cell' advice tree
 */
public Polylist getCellTree(int flavor, ChordSymbol chordSymbol, Note firstNote,
                            Polylist extensions, Polylist subExtensions, Key key)
  {
  ChordForm chordForm = chordSymbol.getChordForm();
  String chordRoot = chordSymbol.getRootString();
  Polylist cellTreeId[] = new Polylist[numCellBuckets + 1];

  Polylist cellTreeRel[] = new Polylist[numCellBuckets + 1];

  Polylist rhythmTree = Polylist.nil;
  for( int i = 0; i <= numCellBuckets; i++ )
    {
    cellTreeId[i] = Polylist.nil;
    cellTreeRel[i] = Polylist.nil;
    }

  String chordNameOnSheet = chordForm.getName();

  // find the rise to the root of the chord 

  int rise = PitchClass.findRise(chordRoot);

  Trace.log(2,
          "getCellTree " + chordRoot + " " + chordNameOnSheet + " key: " + key + ", rise = " + rise);

  // get an enumerationof all cells

  PolylistEnum e = flavor == RHYTHM_CELL ? rhythms.elements() :
          cells[flavor].elements();

  // Use lastUsedSerialNo to eliminate duplicates. The same original 
  // cell could be in more than one list entry because of multiple
  // chord possibilities.  We only want to use a given cell once
  // in a given scenario.

  int lastUsedSerialNo = -1;

  while( e.hasMoreElements() )
    {
    CellForm cell = (CellForm)e.nextElement();

    int serialNo = cell.getSerial();

    // A cell should be used at most once for a given chord

    int caseNo = 0;
      
    if( serialNo > lastUsedSerialNo )
      {
       String cellName = cell.getName();

      AdviceForMelody advice = null;

       if( flavor == RHYTHM_CELL)
          {
            advice = 
                new AdviceForRhythm(cellName,
                serialNo,
                cell.getNotes(chordRoot, key),
                chordRoot,
                key,
                metre,
                firstNote,
                cell.getProfileNumber());
          rhythmTree = rhythmTree.cons(advice);
          }
      else
         {
      Trace.log(2,
              "testing cell " + cell + " against chord on sheet " + chordNameOnSheet);

      String CcellChord = cell.getChordName();

      String CchordNameOnSheet = Key.makeRoot(CROOT, chordNameOnSheet);

      // There are three possible cases relating the chord specified
      // in the cell to the chord on the sheet.
      // case 0 is no relation.

      // case 1 is that the chords are the same, or 
      // the chord on the sheet is an extension of the one in the cell

      ChordSymbol CcellChordSymbol = ChordSymbol.makeChordSymbol(CcellChord);

      if( equivChord(CcellChord, CchordNameOnSheet, subExtensions) )
        {
        caseNo = 1;
        Trace.log(2, "case 1: " + CcellChord + " same as " + CchordNameOnSheet);
        }      // case 2 is that the chords are in the same family, but not case 1
      else if( inFamily(chordForm.getFamily(), CcellChord) || CcellChordSymbol.enhMember(
              extensions) )
//					old	    else if( inFamily(chordForm.getFamily(), CcellChord) || extensions.member(CcellChord) )
        {
        caseNo = 2;
        Trace.log(3,
                "case 2: " + CcellChord + " related to " + CchordNameOnSheet);
        }

      if( caseNo > 0 )
        {
        Polylist transposedNotes = cell.getNotes(chordRoot, key);

        String transposedCellName =
                cell.getProfile() + "  [" + Key.makeRoot(chordRoot, CcellChord) + "] " + cellName + " " + transposedNotes.toString();

        advice = // Cell vs. Idiom; should be refactored
                flavor == PLAIN_CELL
                ? new AdviceForCell(transposedCellName,
                serialNo,
                transposedNotes,
                chordRoot,
                key,
                metre,
                firstNote,
                cell.getProfileNumber())
                : 
                flavor == IDIOM_CELL ? new AdviceForIdiom(transposedCellName,
                serialNo,
                transposedNotes,
                chordRoot,
                key,
                metre,
                firstNote,
                cell.getProfileNumber())
                :
                null;
        }

         }
       
        int index = cell.getHalfBeats();

        if( index > numCellBuckets )	// last index catches all larger values
          {
          index = numCellBuckets;
          }

        if( advice != null )
          {
          if ((firstNote == null) || advice.startsWith(firstNote))
            {
          switch( caseNo )
            {
            case 1:
              cellTreeId[index] = cellTreeId[index].cons(advice);
              lastUsedSerialNo = serialNo;
              break;

            case 2:
              cellTreeRel[index] = cellTreeRel[index].cons(advice);
              lastUsedSerialNo = serialNo;
              break;
            }
          }
        }
      }
    }

    if( flavor == 2 )
      { // Returning EMPTY BAD
        //System.out.println("returning rhythmTree = " + rhythmTree);
        return rhythmTree;
      }

  
  Polylist collectiveTree = Polylist.nil;
  
  Polylist subtree = buildCellTree(cellTreeRel);

  // Collect second sub-tree, cells starting with identical chords

  if( subtree.nonEmpty() )
    {
    collectiveTree = collectiveTree.cons(subtree.cons(title2));
    }

  subtree = buildCellTree(cellTreeId);

  if( subtree.nonEmpty() )
    {
    collectiveTree = collectiveTree.cons(subtree.cons(title1));
    }

  return collectiveTree;
  }


public static Polylist buildCellTree(Polylist array[])
  {
  Polylist subtree = Polylist.nil;

  int len = array.length;

  String titlePrefix = " > " + ((len - 2) * 0.5) + " beats ";

  subtree = buildCellTreeHelper(titlePrefix, array[len - 1], subtree);

  for( int i = len - 2; i >= 0; i-- )
    {
    titlePrefix = " " + (i * 0.5) + " beats ";

    subtree = buildCellTreeHelper(titlePrefix, array[i], subtree);
    }

  return subtree;
  }


public static Polylist buildCellTreeHelper(String titlePrefix, Polylist L,
                                           Polylist subtree)
  {
  if( L.nonEmpty() )
    {
    Polylist profileBin[] = new Polylist[2 * numProfileBins + 3];

    for( int i = 2 * numProfileBins + 2; i >= 0; i-- )
      {
      profileBin[i] = Polylist.nil;
      }

    while( L.nonEmpty() )
      {
      AdviceForMelody advice = (AdviceForMelody)L.first();

      int number = advice.getProfileNumber();

      if( number < -numProfileBins )
        {
        number = -numProfileBins;
        }
      else if( number > numProfileBins )
        {
        number = numProfileBins;
        }

      profileBin[number + numProfileBins + 1] =
              profileBin[number + numProfileBins + 1].cons(advice);

      L = L.rest();
      }

    int total = 0;

    Polylist subsubtree = Polylist.nil;

    // build profiles from profile numbers

    boolean parity = true;

    String neg = " /";
    String pos = " \\";

    char up = '/';
    char down = '\\';

    for( int i = 1; i < numProfileBins + 1; i++ )
      {
      int index = numProfileBins + 1 - i;
      int count = profileBin[index].length();
      if( count > 0 )
        {
        // FIX!!
        int profileNumber =
                ((AdviceForMelody)(profileBin[index].first())).getProfileNumber();
        String title = neg + " (" + (count > 1
                ? (count + " choices)")
                : "1 choice)");
        subsubtree = subsubtree.cons(profileBin[index].cons(title));
        total += count;
        }
      neg = neg + (parity
              ? down
              : up);

      index = numProfileBins + 1 + i;
      count = profileBin[index].length();
      if( count > 0 )
        {
        // FIX!!
        int profileNumber =
                ((AdviceForMelody)(profileBin[index].first())).getProfileNumber();
        String title = pos + " (" + (count > 1
                ? (count + " choices)")
                : "1 choice)");
        subsubtree = subsubtree.cons(profileBin[index].cons(title));
        total += count;
        }
      pos = pos + (parity
              ? up
              : down);
      parity = !parity;
      }

    int count = L.length();

    String title = titlePrefix + " (" + (total > 1
            ? (total + " choices)")
            : "1 choice)");

    subtree = subtree.cons(subsubtree.reverse().cons(title));
    }
  return subtree;
  }


/**
 * Add to the explicitly-specified colors those that are implied
 * by extensions.  This has to be done after chords are read
 * and extensions are computed.
 */
public void updateColors(Polylist chords)
  {
  while( chords.nonEmpty() )
    {
    Polylist item = (Polylist)chords.first();
    ChordForm form = (ChordForm)item.second();
    form.addColor(Polylist.nil); // included buildColors(form));
    //form.addColor(buildExtensions(form, Polylist.nil));
    chords = chords.rest();
    }
  }


/**
 * Builds a list of color tones given chordForm.
 * @param chordForm         a ChordForm containing chord info
 * @return Polylist         a Polylist containing a list of color tones
 */

public Polylist buildColors(ChordForm chordForm)
{
    return buildExtensions(chordForm, Polylist.nil).reverse();
}


    /**
 * Recursively builds a list of color tones using the extensions field of a
 * chordForm.
 * @param chordForm         a ChordForm containing chord info
 * @param extensions        a Polylist containing a list of extensions
 * @return Polylist         a Polylist containing a list of extensions
 */
public Polylist buildExtensions(ChordForm chordForm, Polylist extensions)
  {
  return chordForm.getColor(); // .append(extensions);

  /*
   This was for avoiding duplicates, but maybe we don't care much anymore.

  Polylist color = chordForm.getColor();

      if( color != null )
        {
          PolylistEnum e = color.elements();
          while( e.hasMoreElements() )
            {
              NoteSymbol tone = (NoteSymbol) e.nextElement();
              if( !tone.enhMember(extensions) )
                {
                  extensions = extensions.cons(tone);
                }
            }
        }
  */

  /* This code infers color tones from extensions.
     In retrospect, this might not be good, as it makes it harder to control
     what the color tones are.

  Polylist extend = chordForm.getExtensions();

  // Stop if the chord has no listed extensions, or lists
  // itself as an extension
  if( extend == null || extend.member(chordForm.getName()) )
    {
    return extensions;    // go through each listed extension and add its tones to
    // extensions if they are not already there.
    }
  while( extend.nonEmpty() )
    {

    String extension = (String)extend.first();
    ChordSymbol extensionSymbol = ChordSymbol.makeChordSymbol(extension);
    ChordForm exChordInfo = extensionSymbol.getChordForm();

    if( exChordInfo == null )
      {
      ErrorLog.log(ErrorLog.WARNING, "Extension not found: " + extension);
      extend = extend.rest();
      continue;
      }

    Polylist spell = chordForm.getPriority();
    if( spell == null )
      {
      spell = chordForm.getSpell(CROOT);
      }
    Polylist exSpell = exChordInfo.getPriority();

    PolylistEnum e = exSpell.elements();
    while( e.hasMoreElements() )
      {
      NoteSymbol ob = (NoteSymbol)e.nextElement();

      if( !ob.enhMember(spell) && !ob.enhMember(extensions) )
        {
        extensions = extensions.cons(ob);
        }
      }
    extensions = buildExtensions(exChordInfo, extensions);
    extend = extend.rest();
    }

   */
  /*
  return extensions;
   */
  }


/**
 * Takes a chord object and returns a polylist containing its spelling.
 * This is used in playback.
 * @param chord     a Chord object to get the spelling of
 * @return Polylist a list of chord tones
 */
public static Polylist getChordTones(ChordSymbol symbol)
  {
  if( symbol.isNOCHORD() )
    {
    return null;
    }

  ChordForm topInfo = symbol.getChordForm();

  ChordSymbol polybase = symbol.getPolybase();
  if( polybase == null )
    {
    // Ordinary (non-poly) chord
    return topInfo.getPriority(symbol.getRootString());
    }

  // polychord

  ChordForm bottomInfo = polybase.getChordForm();

  String bottomRoot = polybase.getRootString();
  String topRoot = symbol.getRootString();

  Polylist bottom = bottomInfo.getPriority(bottomRoot);

  // transpose top up for better sonority
  Polylist top =
          NoteSymbol.transposeNoteSymbolList(topInfo.getSpell(topRoot), 0);
  Trace.log(2, "bottom = " + bottom + " top = " + top);

  return top.append(bottom);
  }


/**
 * Goes through all the extensions of a chord and builds the extensionsTable
 * for that chord and its extensions (works recursively)
 * @param chordName         a String specifying the chord to process
 * @param extensions        a Polylist containing the extension path
 */
public void traverseExtensions(String chordName, Polylist extensions)
  {
  ChordSymbol chordSymbol = ChordSymbol.makeChordSymbol(chordName);

  ChordForm chordForm = chordSymbol.getChordForm();

  // don't add invalid chords or chords that are the "same"
  if( chordForm == null )
    {
    return;
    }

  // add this chord and the current extension path to the table
  addExtensions(chordName, extensions);

  Polylist extend = chordForm.getExtensions();

  // return if there are no extensions, or this chord only extends
  // itself
  if( extend == null || extend.member(chordName) )
    {
    return;    // recursively traverse extensions
    }
  PolylistEnum ex = extend.elements();

  while( ex.hasMoreElements() )
    {

    String extension = (String)ex.nextElement();
    traverseExtensions(extension, extensions.cons(chordName));
    }
  }


/**
 * Adds the chord and its extension path to the extensionsTable.
 * @param chordName         the name of the chord to add
 * @param extensions        the extensions path to add
 */
public void addExtensions(String chordName, Polylist extensions)
  {
  // if there is no extensions path, just add the chord
  if( extensions.isEmpty() )
    {
    extensionsTable = extensionsTable.cons(Polylist.list(chordName));
    return;
    }

  // as we find this chord's entry in the extensionsTable, we
  // keep track of the before and theRest so we can
  // take out the old entry and put in the new one

  Polylist before = Polylist.nil;
  Polylist ex = null;
  Polylist theRest = extensionsTable;
  while( theRest.nonEmpty() )
    {
    ex = (Polylist)theRest.first();
    if( ex.first().equals(chordName) )
      {
      theRest = theRest.rest();
      break;
      }
    before = before.cons(ex);
    theRest = theRest.rest();
    }

  // if we didn't find it in the extensionsTable, set ex to null
  if( ex != null && !ex.first().equals(chordName) )
    {
    ex = null;
    }
  while( extensions.nonEmpty() )
    {
    String extension = (String)extensions.first();

    // if there is no entry, start a new one
    if( ex == null )
      {
      ex = Polylist.list(chordName, extension);      // if this extension isn't in this entry, put it in
      }
    else if( !ex.member(extension) )
      {
      ex = ex.reverse().cons(extension).reverse();
      }
    extensions = extensions.rest();
    }

  // put the list back together, leaving out the old entry
  // and putting the new one in its place
  extensionsTable = before.reverse().append(theRest.cons(ex));
  }


/**
 * Returns a Polylist containing the licks advice tree.
 * @param chordForm         a ChordForm with the info of the first chord
 * @param nextChordInfo     a ChordForm with the info of the next chord
 * @param chordRoot         a String with the original root of the chord
 * @param nextChordRoot     a String with tht original root of the next chord
 */
public Polylist getLickTree(int flavor,
                            ChordSymbol chord1Symbol,
                            ChordSymbol chord2Symbol,
                            int interChordHalfBeats,
                            Note firstNote,
                            Polylist extensions,
                            Polylist subExtensions,
                            Key key)
  {


  ChordForm chord1info = chord1Symbol.getChordForm();
  String chord1root = chord1Symbol.getRootString();

  if( chord1info == null )
    {
    return Polylist.nil;
    }

  Polylist lickTreeId[] = new Polylist[numLickBuckets + 1];

  Polylist lickTreeRel[] = new Polylist[numLickBuckets + 1];

  for( int i = 0; i <= numLickBuckets; i++ )
    {
    lickTreeId[i] = Polylist.nil;
    lickTreeRel[i] = Polylist.nil;
    }

  String chord1name = chord1info.getName();

  String family1 = chord1info.getFamily();

  ChordForm chord2info = null;

  String chord2root = null;
  String chord2name = null;
  String family2 = null;

  if( chord2Symbol != null )
    {
    chord2root = chord2Symbol.getRootString();
    chord2info = chord2Symbol.getChordForm();
    chord2name = chord2info.getName();
    family2 = chord2Symbol.getFamily(); // getChordFamily(chord2name);
    }

  Trace.log(2,
          "chordNames on sheet = " + chord1name + " " + chord2name + ", families = " + family1 + " " + family2);

  // go through each lick and check if the chord sequences match
  // where the first chord is in the same family as the chord on the sheet

  PolylistEnum e;

  e = licks[flavor].elements();		// Get the list of all licks from the rules
  
  int rise1 = PitchClass.findRise(chord1root);

  while( e.hasMoreElements() )
    {
    LickForm lickForm = (LickForm)e.nextElement();
    Polylist lickSequence = dePunctuate(lickForm.getChordSequence(chord1root,
            key));

    int index = lickForm.getHalfBeats();

    if( index > numLickBuckets )	// last index catches all larger values
      {
      index = numLickBuckets;
      }

    // Currently only the first 1 or 2 chords of the lick are examined.
    // This may be changed.

    String lickChord1 = lickForm.getChord(0, chord1root, key);
    String lickChord2 = lickForm.getChord(1, chord1root, key);
    String lickChord1inC = Key.makeCroot(lickChord1);

    if( (family2 == null && lickChord2 == null) || (family2 != null && lickChord2 != null && inFamily(
            family2, lickChord2) && Math.abs(interChordHalfBeats - lickForm.getOffset(
            1)) <= interChordTolerance // within so many half-beats
            && Key.enharmonic(
            ChordSymbol.makeChordSymbol(lickChord2).getRootString(),
            chord2root)) )
      {
      if( equivChord(lickChord1inC, chord1name, subExtensions) )
        {

// System.out.println("case 1: lickChord1inC = " + lickChord1inC + ", chord1name = " + chord1name + ", lickChord2 = " + lickChord2 + ", interChordHalfBeats = " + interChordHalfBeats + ", offset = " + lickForm.getOffset(1) );

        // Create advice for lick

        Polylist notes = lickForm.getNotes(chord1root, key);

        int serial = lickForm.getSerial();

        String transposedLickName =
                lickForm.getProfile() + "  [" + lickChord1 + (lickChord2 == null
                ? ""
                : (" -> " + lickChord2)) + "] " + lickForm.getName() + " " + notes.toString();

        
        //AdviceForMelody advice = flavor == PLAIN_LICK  // plain lick vs. quote; should refactor
//                ? new AdviceForLick(transposedLickName,
//                serial,
//                notes,
//                chord1root,
//                key,
//                metre,
//                firstNote,
//                lickForm.getProfileNumber())
//                : new AdviceForQuote(transposedLickName,
//                serial,
//                notes,
//                chord1root,
//                key,
//                metre,
//                firstNote,
//                lickForm.getProfileNumber());
        
        AdviceForMelody advice = null;
        
        switch (flavor) {
            case PLAIN_LICK: advice = new AdviceForLick(transposedLickName,
                serial,
                notes,
                chord1root,
                key,
                metre,
                firstNote,
                lickForm.getProfileNumber());
                break;
                
            case QUOTE_LICK: advice = new AdviceForQuote(transposedLickName,
                serial,
                notes,
                chord1root,
                key,
                metre,
                firstNote,
                lickForm.getProfileNumber());
                break;
                
            case BRICK_LICK: advice = new AdviceForBrick(transposedLickName,
                serial,
                notes,
                chord1root,
                key,
                metre,
                firstNote,
                lickForm.getProfileNumber());
                break;
                
            default: ErrorLog.log(ErrorLog.WARNING, "Inappropriate lick flavor: " + flavor);
        }

        //advice Include if first note unspeicified or matches the lick's first note
        if( (firstNote == null) || advice.startsWith(firstNote) )
          {
          lickTreeId[index] = lickTreeId[index].cons(advice);
          }
        }
      
      else if( inFamily(family1, lickChord1) || extensions.member(lickChord1inC) )
        {

// System.out.println("case 2: lickChord1inC = " + lickChord1inC + ", chord1name = " + chord1name + ", lickChord2 = " + lickChord2);

        // Create advice for lick

        Polylist notes = lickForm.getNotes(chord1root, key);

        int serial = lickForm.getSerial();

        String transposedLickName =
                lickForm.getProfile() + "  [" + lickChord1 + (lickChord2 == null
                ? ""
                : (" -> " + lickChord2)) + "] " + lickForm.getName() + " " + notes.toString();

        AdviceForMelody advice =
                new AdviceForLick(transposedLickName, serial, notes, chord1root,
                key, metre,
                firstNote, lickForm.getProfileNumber());

        // Include if first note unspecified or matches the lick's first note

        if( (firstNote == null) || advice.startsWith(firstNote) )
          {
          lickTreeRel[index] = lickTreeRel[index].cons(advice);
          }
        }
      }
    else
      {
      //System.out.println("rejecting: lickChord1inC = " + lickChord1inC + ", chord1name = " + chord1name + ", lickChord2 = " + lickChord2);          
      }
    }

  Polylist collectiveTree = Polylist.nil;

  Polylist subtree = buildCellTree(lickTreeRel);

  // Collect second sub-tree, cells starting with identical chords

  if( subtree.nonEmpty() )
    {
    collectiveTree = collectiveTree.cons(subtree.cons(title4));
    }

  subtree = buildCellTree(lickTreeId);

  if( subtree.nonEmpty() )
    {
    collectiveTree = collectiveTree.cons(subtree.cons(title3));
    }

  return collectiveTree;
  }


public Polylist getSubsTree(Polylist otherChords,
                            ChordSymbol chordSymbol,
                            ChordSymbol nextChordSymbol,
                            Note note,
                            Key targetKey)
  {
  ChordForm chordForm = chordSymbol.getChordForm();
  String chordRoot = chordSymbol.getRootString();
  return getChordsTree(otherChords, chordRoot, nextChordSymbol, note, targetKey);
  }


public Polylist getChordsTree(Polylist chords,
                              String chordRoot,
                              ChordSymbol nextChordSymbol,
                              Note note,
                              Key targetKey)
  {
  Polylist tree = Polylist.nil;

  while( chords.nonEmpty() )
    {
    ChordSymbol first = (ChordSymbol)chords.first();

    Polylist subtree =
            getAdviceTreeWithCaching(first,
            nextChordSymbol,
            -1,
            note,
            false,
            targetKey).rest();
    tree = tree.cons(subtree.cons(first));
    chords = chords.rest();
    }

  return tree;
  }


public static Polylist removeStubs(Polylist tree)
  {
  if( tree.isEmpty() )
    {
    return Polylist.nil;
    }
  Polylist newTree = Polylist.list(tree.first());

  tree = tree.rest();
  while( tree.nonEmpty() )
    {
    Object ob = tree.first();

    if( ob instanceof Polylist )
      {
      Polylist subTree = removeStubs((Polylist)ob);
      if( subTree.rest().nonEmpty() )
        {
        newTree = newTree.cons(subTree);
        }
      }
    else
      {
      newTree = newTree.cons(ob);
      }
    tree = tree.rest();
    }
 
  return newTree.reverse();
  }


/**
 * Takes a chord name (in the key of C), looks it up in the chord list,
 * and returns a ChordForm containing information on that chord (or,
 * if there is a "same" chord, the information on that one).
 * @param chordName         the name of the chord to get information on
 * @return                  a ChordForm containing info on the chord
 */
public static ChordForm getChordForm(String name)
  {
  if( name == null || chords == null )
    {
    return null;
    }

  Trace.log(2, "getChordForm " + name);

  int depth = 0;

  while( depth < depthLimit )
    {
    Polylist found = chords.assoc(name);

    if( found == null )
      {
      return null;
      }

    ChordForm chordForm = (ChordForm)found.second();

    String same = chordForm.getSame();

    if( same == null )
      {
      return chordForm;
      }
    name = same;
    depth++;
    }

  ErrorLog.log(ErrorLog.SEVERE,
          "Chord 'same' chain exceeds depth limit of " + depthLimit + ": " + name);
  return null;
  }


/**
 * Gets the formally-defined family of the chord.
 * If not family is specified, then the name (sans root)
 * is taken as the family.  If the name sans root is
 * empty, the implied name is "M" for major.
 */
public static String getChordFamily(String chordName)
  {
  // The given root might not be C
  Polylist parts = Key.explodeChord(chordName);
  
  try { 
      chordName = CROOT + (String)parts.second();
  } catch (NullPointerException e) {
      return chordName;
  }
  
  Polylist entry = chords.assoc(chordName);
  if( entry == null )
    {
    return null;
    }

  ChordForm form = (ChordForm)entry.second();

  String same = form.getSame();

  if( same != null )
    {
    return getChordFamily(same);
    }

  return form.getFamily();
  }

/**
 * Gets the formally-defined family of a chord symbol.
 * If no family is specified, then the name (sans root)
 * is taken as the family.  If the name sans root is
 * empty, the implied name is "M" for major.
 */
public static String getSymbolFamily(String chordSymbol)
  {
 
      String chordName = CROOT + chordSymbol;

  
  Polylist entry = chords.assoc(chordName);
  if( entry == null )
    {
    return chordSymbol;
    }

  ChordForm form = (ChordForm)entry.second();

  String same = form.getSame();

  if( same != null )
    {
    return getChordFamily(same);
    }

  return form.getFamily();
  }
/**
 * Some chord families are defined in terms of others
 * using "same".  This gets the final basic name of
 * the chord.  The entry is the stuff in the chord
 * database.  Hopefully there is no loop, otherwise this
 * will diverge.
 */
public static String getFinalName(String chordName)
  {
  Polylist entry = chords.assoc(chordName);
  if( entry != null )
    {
    ChordForm form = (ChordForm)entry.second();

    String same = form.getSame();

    if( same != null )
      {
      return getFinalName(same);
      }
    }

  return chordName;
  }


/**
 * Tell whether a chord is the same or an extension of another
 * but possibly with different names.
 */
boolean equivChord(String x, String y, Polylist subExtensions)
  {
  String xName = getFinalName(x);
  String yName = getFinalName(y);

  ChordSymbol xSymbol = ChordSymbol.makeChordSymbol(xName);
  ChordSymbol ySymbol = ChordSymbol.makeChordSymbol(yName);

  boolean result = xSymbol.enhChord(ySymbol) || xSymbol.enhMember(subExtensions);

  return result;
  }


/**
 * Tell whether a chord name is in the indicated family.
 */
boolean inFamily(String family, String chord)
  {
  return getChordFamily(chord).equals(family);
  }


/**
 * Returns type, the chord name without the root
 */
String getType(String chordName)
  {
  if( chordName.length() == 1 )
    {
    return "M";	// major
    }

  char second = chordName.charAt(1);
  if( second == SHARP || second == FLAT )
    {
    return chordName.substring(2);
    }
  else
    {
    return chordName.substring(1);
    }
  }


public static boolean getMark(int i)
  {
  return markArray.get(i).equals(Boolean.TRUE);
  }


public void setMark(int i)
  {
  if( !getMark(i) )
    {
    Trace.log(2, "Marking item " + i + ": " + ruleArray.get(i));
    markArray.set(i, Boolean.TRUE);
    addOneRule(ruleArray.get(i), i, true, false);	// re do the element
    }
  }


public void unsetMark(int i)
  {
  if( getMark(i) )
    {
    Trace.log(2, "Unmarking item " + i + ": " + ruleArray.get(i));
    markArray.set(i, Boolean.FALSE);
//			 FIX     ruleArray.set(i, (Polylist)((Polylist)ruleArray.elementAt(i)).second());  // remove mark
    }
  }


public void showMarkedItems()
  {
  System.out.println("Marked items:");
  int num = markArray.size();
  for( int i = 0; i < num; i++ )
    {
    if( getMark(i) )
      {
      System.out.println("item " + i + ": " + ruleArray.get(i));
      }
    }
  }


/**
 * Save all rules in a file.
 */
public void saveRules(File file)
  {
    System.out.println("saving to " + file.getAbsolutePath());
  try
    {
    saveRules(new PrintStream(new FileOutputStream(file)));
    }
  catch( IOException e )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Saving rules in file failed: " + file);
    }
  }


/**
 * Create a new rule file.
 */
public void saveRules(PrintStream out)
  {
  
  // print scales first
  ListIterator v = ruleArray.listIterator();
  for( int i = 0; v.hasNext(); i++ )
    {
    Object ob = v.next();
    if( ob instanceof Polylist )
      {
      Polylist listob = (Polylist)ob;
      if( listob.nonEmpty() )
        {
          if( listob.first().equals(SCALE) )
            {
            pprint(out, listob, "    ");
            }
           }
        }
      }

  // Next print chords
  PolylistEnum c = chords.elements();
  while( c.hasMoreElements() )
  {
    pprint(out, ((ChordForm)(((Polylist)c.nextElement()).second())).toPolylist(), "    ");
  }

  // Then print everything else
  v = ruleArray.listIterator();
  for( int i = 0; v.hasNext(); i++ )
    {
    Object ob = v.next();
    if( ob instanceof Polylist )
      {
      Polylist listob = (Polylist)ob;
      if( listob.nonEmpty() )
        {
        if( getMark(i) )
          {
          out.println("(marked " + listob + ")");
          }
        else
          {
          if( listob.first().equals(CHORD) )
            {
             }
          else if( listob.first().equals(SCALE) )
            {
             }
          else if( listob.first().equals(STYLE) )
            {
            pprint(out, listob, "    ");
            }
          else
            {
            out.println(listob);
            }
          }
        }
      }
    else
    {
      System.out.println("non-Polylist: " + ob);
    }
    }
  
  }


public static void saveStyles(PrintWriter out)
  {
  ListIterator v = ruleArray.listIterator();
  for( int i = 0; v.hasNext(); i++ )
    {
    Object ob = v.next();
    if( ob instanceof Polylist )
      {
      Polylist listob = (Polylist)ob;

      if( !((String)listob.first()).equals(STYLE) )
        {
        continue;
        }
      if( listob.nonEmpty() )
        {
        if( getMark(i) )
          {
          out.println("(marked " + listob + ")");
          }
        else
          {
          if( /* listob.first().equals(CHORD) || */ listob.first().equals(STYLE) )
            {
            pprint(out, listob, "    ");
            }
          else
            {
            out.println(listob);
            }
          }
        out.println();
        }
      }
    }
  }


/**
 * pretty-print
 */
public static void pprint(PrintWriter out, Polylist L, String tab)
  {
  out.println("(" + L.first());
  L = L.rest();
  while( L.nonEmpty() )
    {
    Object first = L.first();
    if( first instanceof Polylist && ((Polylist)L.first()).first().equals(SCALES) )
      {
      out.println(tab + "(scales");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else if( first instanceof Polylist && ((Polylist)L.first()).first().equals(
            VOICINGS) )
      {
      out.println(tab + "(voicings");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else if( first instanceof Polylist && ((Polylist)L.first()).first().equals(
            DRUM_PATTERN) )
      {
      out.println(tab + "(drum-pattern");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else
      {
      out.println(tab + L.first());
      }
    L = L.rest();
    }
  out.println(tab + ")");
  }


/**
 * pretty-print
 */
public static void pprint(PrintStream out, Polylist L, String tab)
  {
  out.println("(" + L.first());
  L = L.rest();
  while( L.nonEmpty() )
    {
    Object first = L.first();
    if( first instanceof Polylist && ((Polylist)L.first()).first().equals(SCALES) )
      {
      out.println(tab + "(scales");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else if( first instanceof Polylist && ((Polylist)L.first()).first().equals(
            VOICINGS) )
      {
      out.println(tab + "(voicings");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else if( first instanceof Polylist && ((Polylist)L.first()).first().equals(
            DRUM_PATTERN) )
      {
      out.println(tab + "(drum-pattern");
      Polylist M = ((Polylist)L.first()).rest();
      while( M.nonEmpty() )
        {
        out.println(tab + tab + M.first());
        M = M.rest();
        }
      out.println(tab + tab + ")");
      }
    else
      {
      out.println(tab + L.first());
      }
    L = L.rest();
    }
  out.println(tab + ")");
  }


/**
 * Dump out everything, primarily for development.
 */
public void showRules(PrintStream out)
  {
  ListIterator v = ruleArray.listIterator();
  for( int i = 0; v.hasNext(); i++ )
    {
    out.println("" + i + " " + v.next());
    }
  }


/**
 * Add lick or cell rule requested by user.
 */
public static boolean addUserRule(Polylist rule)
  {
  if( addOneRule(rule, ruleArray.size(), false, false) )
    {
    // If successful, retain the rule in raw form in
    // the array for later saving.
    ruleArray.add(rule);
    markArray.add(Boolean.FALSE);
    return true;
    }

  return false;
  }


public static void addMoreRules(Polylist rules)
  {
  while( rules.nonEmpty() )
    {
    Object first = rules.first();
    if( first instanceof Polylist )
      {
      addUserRule((Polylist)first);
      }
    }
  }


private static void removeRule(Polylist rule)
  {
  Polylist ruleNotes = new Polylist();
  for( int i = 0; i < rule.length(); ++i )
    {
    if( rule.nth(i) instanceof Polylist &&
            ((String)((Polylist)rule.nth(i)).first()).equals("notes") )
      {
      ruleNotes = ((Polylist)rule.nth(i)).rest();
      }
    }
  for( int i = 0; i < ruleArray.size(); ++i )
    {
    Polylist r = ruleArray.get(i);
    Polylist rNotes = new Polylist();
    for( int j = 0; j < r.length(); ++j )
      {
      if( r.nth(j) instanceof Polylist &&
              ((String)((Polylist)r.nth(j)).first()).equals("notes") )
        {
        rNotes = ((Polylist)r.nth(j)).rest();
        }
      }

    if( NoteSymbol.isomorphicNoteSequences(NoteSymbol.makeNoteSymbolList(rNotes),
            NoteSymbol.makeNoteSymbolList(ruleNotes)) )
      {
      ruleArray.remove(i);
      markArray.remove(i);
      }
    }
  }


public static Polylist getAllScales()
  {
  return scales;
  }


public static Polylist getScale(String root, String type)
  {
  NoteSymbol tonic = NoteSymbol.makeNoteSymbol(root);
  ScaleForm sf = getScale(type);
  if( sf == null )
  {
      return Polylist.nil;
  }
  Polylist scaleTones = sf.getSpell(tonic);
  return scaleTones;
  }


public static Polylist getAllChords()
  {
  return chords;
  }


}
