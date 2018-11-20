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
 * merchantability or fitness for a particular purpose.  See thesetS
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.gui;

import imp.style.SectionRecord;
import imp.style.SectionInfo;
import imp.style.Style;
import imp.Constants;
import static imp.Constants.ExtractMode.BRICK;
import static imp.Constants.ExtractMode.QUOTE;
import imp.roadmap.brickdictionary.Block;
import imp.com.MergeSameNotesCommand;
import imp.com.PlayScoreCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ShiftChordsCommand;
import imp.com.ShiftPitchesCommand;
import imp.data.*;
import imp.util.ErrorLog;
import imp.midi.MidiPlayListener;
import imp.util.Preferences;
import imp.util.Trace;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 * A Java Swing Component for displaying a Common Practice Notation stave.
 * Stave Contains all of the information on a particular Stave, such as what
 * part it is displaying, its key signature, etc. It also contains all of the
 * functions associated with setting construction lines for a Stave, and all of
 * the functions for drawing a Stave.
 *
 *
 * @author Aaron Wolin, Stephen Jones, Bob Keller
 *         (revised from code by Andrew Brown & Adam Kirby)
 * @version 1.0, 28th June 2005
 */
public class Stave
        extends JPanel
        implements Constants, FocusListener
{
static int MAX_LICK_CHORDS = 1000;
    
String noteColorString = Preferences.getPreference(Preferences.NOTE_COLORING);
   
/**
 * Style and phrase layout parameters
 */
private String PHRASE_MARK = ",";
private String STYLE_MARK = "Style: ";

private static int DOUBLE_BAR_OFFSET = 5;
private static int PHRASE_MARK_X_OFFSET = 30;
private static int PHRASE_MARK_Y_OFFSET = 15;
private static int styleXoffset = 20;
private static int styleYoffset = 50;

/**
 * The maximum width alloted to time signature. Longer than this causes too
 * few measures in the first line for the basic 4 bars per line if there
 * are many sharps or flats in the key signature. However, this causes
 * scrunching with time signatures with 2-digits, so a better FIX is in
 * order. It is only used in drawTimeSig, where it is invariably returned.
 */
private static final int maxTimeSigWidth = 6;
private boolean beamingNotes = true;
public static final int maxMeasuresPerLine = 64;
private static final int selectionBoxPadding = 8;

public static int sheetTitleYoffset    = 20;
public static int sheetComposerYoffset = 38;
public static int partTitleYoffset     = 25;
public static int partComposerYoffset  = 40;
public static int showTitleYoffset     = 25;
public static int yearYoffset          = 40;

static int tupletBracketInset = 3;
static int tupletBracketHeight = 10;
static int beatBracketHeight = 10;
/** Constants for beaming
 * Some of these are necessary due to the original uses of images with note
 * stems. The stems were edited off the images to achieve note heads, but
 * the position of the head in the image was not changed.
 */
public static int beamThickness = 3;
public static int beamUpXoffset = 4;
public static int beamDownXoffset = -6;
public static int downStepCorrection = 54;
public static int upStemCorrection = 26;
public static int downStemCorrection = -26;
public static int beamSpacing = 6;
public static int upStemBracketCorrection = 15;

/**
 * Action handler for the Stave.
 */
private StaveActionHandler staveActionHandler;

ArrayList<BeamNote> beamNotes = null;
/**
 * The panel width
 */
private int panelWidth = 1000;
/**
 * The panel height
 */
private int panelHeight = 1384;
/**
 * Default sub divisions
 */
protected static int defaultSubDivs = 2;
/**
 * The type of Stave to be drawn
 */
private StaveType type;

/*
 * Colors for drawing stems
 * Not used currently 
 * java.awt.Color stemColor[] = {Color.black, Color.red, Color.green, Color.blue};
 */
/**
 * Images to be used for drawing notes, accidentals, and other stave components
 */
static private Image trebleClef,
        bassClef,
        dot,
        demisemiquaverRest,
        semiquaverRest,
        quaverRest,
        crotchetRest,
        minimRest,
        semibreveRest,
        one,
        two,
        three,
        four,
        five,
        six,
        seven,
        eight,
        nine,
        zero,
        delete,
        tieOver,
        tieUnder,
        smallbox;
/**
 * There is an array for each of these images, as they come in multiple colors
 * for note coloration purposes.
 */
static private Image[] crotchetUp,
        crotchetDown,
        quaverDown,
        quaverUp,
        semiquaverDown,
        semiquaverUp,
        demisemiquaverUp,
        demisemiquaverDown,
        filledNoteHead,
        minimDown,
        minimUp,
        semibreve,
        sharp,
        flat,
        natural;
/**
 * The notation window containing this Stave
 */
protected Notate notate;
/**
 * The original Part passed to the Stave
 */
private MelodyPart origPart;
/**
 * The Part to be displayed on the Stave
 */
private MelodyPart displayPart;
/**
 * The chord progression to be displayed on the Stave
 */
private ChordPart chordProg;
/**
 * The array of construction lines
 */
protected CstrLine cstrLines[];
// the last note entered, used with drawing ties
Note lastNote = null;
// The index of lastNote
int ilast = -1;
// Whether or not lastNote was beamed
boolean lastNoteBeamed = false;
// Whether or not lastNote had stem up (used for beaming)
boolean lastNoteStemUp = true;
// Whether stems are up in a beam
boolean beamStemUp = true;
// Next note: use for beaming
Note nextNote = null;
/**
 * The metre of the Stave
 */
private int[] metre = new int[2];
private int beatValue = BEAT;                // default for 4/4
private int measureLength = 4 * beatValue;        // default for 4/4

private boolean doubleBar = false;

/**
 * The key signature of the Stave. Positive numbers indicate sharps,
 * negative indicate flats
 */
private int keySignature = 0;
/**
 * Sets the maximum pitch for the Stave.
 * NOTE: Was set to 127, but had to set it lower to reflect ledger lines.
 */
private int maxPitch = 97;
/**
 * Sets the minimum pitch for the Stave.
 * NOTE: Was set to 0, but had to set it higher to reflect ledger lines.
 */
private int minPitch = 24;
// Used for setting the key signature
static final private int[] notePosOffset =
  {
    24, 24, 20, 20, 16, 12, 12, 8, 8, 4, 4, 0
  };
static final private int[] sharps =
  {
    77, 72, 79, 74, 69, 76, 71
  };
static final private int[] flats =
  {
    71, 76, 69, 74, 67, 72, 65
  };
static final private int[] lineNotes =
  {
    0, 1, 4, 7, 8, 11, 14, 15, 17, 18, 21, 22
  };
// Arrays of pitches corresponding to treble, bass, and grand values
// Determines if a ledger line should be drawn
// NOTE: These values were in jMusic, I just moved them
// into arrays and ignored the ugliness
static final int[] treblePitches =
  {
    61, 58, 54, 51, 48, 81, 84, 88, 91, 95
  };
static final int[] bassPitches =
  {
    40, 37, 34, 30, 26, 60, 64, 67, 71, 74
  };
static final int[] grandPitches =
  {
    40, 37, 34, 30, 27, 81, 84, 88, 91, 95
  };
// Arrays of the y-axis offsets corresponding to each stave
static final int[] trebleOffset =
  {
    40, 48, 56, 64, 72, -8, -16, -24, -32, -40
  };
static final int[] bassOffset =
  {
    40, 48, 56, 64, 72, -8, -16, -24, -32, -40
  };
static final int[] grandOffset =
  {
    88, 96, 104, 112, 120, -8, -16, -24, -32, -40
  };
/**
 * The title for the leadsheet
 */
private String sheetTitle;
/**
 * The title for the Stave
 */
private String partTitle;
/**
 * The composer for the Stave
 */
private String composer;
/**
 * The title for the "show", if any
 */
private String showTitle;
/**
 * The year
 */
private String year;

JLabel noteCursorLabel = new javax.swing.JLabel();
/**
 * The font for the Title
 */
private Font sheetTitleFont = new Font("Helvetia", Font.BOLD, 20);
private Font partTitleFont = new Font("Helvetia", Font.BOLD, 14);
/**
 * The font for the bar numbers
 */
private Font barNumFont = new Font("Helvetica", Font.PLAIN, 12);
private Font composerFont = barNumFont;
private Font phraseMarkFont = new Font("TimesRoman", Font.PLAIN, 24);
/**
 * The font for the chord symbols
 */
private Font chordFont;
/**
 * Flag for if bar numbers should be shown on the Stave
 */
private boolean showBarNums = false;
/**
 * Flag for if a measure's construction lines should be drawn on the Stave
 */
private boolean showMeasureCL = true;
/**
 * Flag for if all construction lines should be drawn on the Stave
 */
private boolean showAllCL = false;
/**
 * Flag for if the Stave's title should be displayed
 */
private boolean showSheetTitle = true;
private boolean showPartTitle = true;
private boolean showEmptyTitles = true;
/**
 * Flag for if the Stave should be editable
 */
private boolean editable = true;
private Color titleColor = new Color(0, 0, 40);
private Color titleBGHighlightColor = new Color(250, 250, 250);
/**
 * The measure that the mouse is currently over.
 * For use with StaveActionHandler and drawing construction lines.
 */
protected int mouseOverMeasure = OUT_OF_BOUNDS;
/**
 * Contains the Rectangle the mouse is over in the selection area, or null
 * for when the mouse is not over a selection
 */
protected Rectangle mouseOverSelection = null;
/**
 * Starting index of note selection
 */
private int selectionStart = OUT_OF_BOUNDS;
/**
 * Ending index of note selection
 */
private int selectionEnd = OUT_OF_BOUNDS;
/**
 * Starting index the user is pasting from
 */
private int pasteFromStart = OUT_OF_BOUNDS;
/**
 * Ending index the user is pasting from
 */
private int pasteFromEnd = OUT_OF_BOUNDS;
/**
 * Starting index the user is pasting to
 */
protected int pasteToStart = OUT_OF_BOUNDS;
/**
 * Ending index the user is pasting to
 */
protected int pasteToEnd = OUT_OF_BOUNDS;
/**
 * The current line the user is on
 */
protected int currentLine = OUT_OF_BOUNDS;
/**
 * Maximum duration for last chord of an extracted part
 */
int durationMax = 4 * BEAT;
/**
 * Minimum duration for last chord of an extracted part
 */
int durationMin = BEAT;
/**
 * The bounds of the part title
 */
private Rectangle partTitleBounds = null;
/**
 * Flag to determine if printing
 */
private boolean printing = false;
/**
 * Size of selection, -1 is unlocked, any other value forces selection to
 * a particular width (measured in slots)
 */
private int lockSelectionSize = -1;
/**
 * Pen stroke for curve drawing, 4 pixels wide for good measure
 */
private Stroke curveStroke = new BasicStroke(3);
/**
 * Contour curve color
 */
private Color curveColor = new Color(180, 180, 215, 120);
/**
 * Points in contour curve; COORDINATE-INDEXED, with x as ind and y as val
 */
int[] curvePoints = new int[this.getWidth()];
/**
 * Selection cache
 */
private int firstBoxX, firstBoxStave, lastBoxX, lastBoxStave;
private boolean firstBoxFound = false;
private boolean transparentFill = false;
ArrayList<Rectangle> selectionBox = new ArrayList<Rectangle>();
private Color selectionColor = new Color(110, 160, 255, 10);
private Color selectionHandleColor = new Color(110, 160, 255, 25);
private Color selectionBorderColor = new Color(80, 100, 205, 90);
private Color selectionHighlightColor = new Color(226, 241, 255);
private Color selectionHighlightBorderColor = new Color(60, 140, 255);
private Color marqueeBorderColor = new Color(110, 160, 255, 50);
private Color boxColor = new Color(255, 165, 0, 80);
// selection handles:
Rectangle selectionLHandle = new Rectangle(6, 20);
Rectangle selectionRHandle = new Rectangle(6, 20);
// Triangle selectors
Polygon selectionHandle[] =
  {
    new Polygon(new int[]
      {
        0, 8, 0
      },
                new int[]
      {
        0, 0, 8
      },
                3),// Left top

    new Polygon(new int[]
      {
        0, 0, 8
      },
                new int[]
      {
        0, -8, 0
      },
                3), // Left bottom

    new Polygon(new int[]
      {
        0, -8, 0
      },
                new int[]
      {
        0, 0, 8
      },
                3), // Right top

    new Polygon(new int[]
      {
        0, -8, 0
      },
                new int[]
      {
        0, 0, -8
      },
                3) // Right bottom
  };
// rectangle for the marquee
Rectangle marqueeSelection = new Rectangle(0, 0);

/*
 * Popup editor for the leadsheet title
 */
EntryPopup sheetTitleEditor;

/*
 * Popup editor for the leadsheet composer
 */
EntryPopup sheetComposerEditor;

/*
 * Popup editor for the part title
 */
EntryPopup partTitleEditor;

/*
 * Popup editor for the part composer
 */
EntryPopup partComposerEditor;

/*
 * Popup editor for the "show"
 */
EntryPopup showTitleEditor;

/*
 * Popup editor for the year
 */
EntryPopup yearEditor;

/**
 * This class is from Sun's documentation.
 */
public static class MyOwnFocusTraversalPolicy
        extends FocusTraversalPolicy
{

ArrayList<EntryPopup> order;

public MyOwnFocusTraversalPolicy(ArrayList<EntryPopup> order)
  {
    this.order = new ArrayList<EntryPopup>(order.size());
    this.order.addAll(order);
  }

public Component getComponentAfter(Container focusCycleRoot,
                                   Component aComponent)
  {
    int idx = (order.indexOf(aComponent) + 1) % order.size();
    return order.get(idx);
  }

public Component getComponentBefore(Container focusCycleRoot,
                                    Component aComponent)
  {
    int idx = order.indexOf(aComponent) - 1;
    if( idx < 0 )
      {
        idx = order.size() - 1;
      }
    return order.get(idx);
  }

public Component getDefaultComponent(Container focusCycleRoot)
  {
    return order.get(0);
  }

public Component getLastComponent(Container focusCycleRoot)
  {
    return order.get(order.size()-1);
  }

public Component getFirstComponent(Container focusCycleRoot)
  {
    return order.get(0);
  }

}

/**
 * Constructs a new <code>Stave</code> to display a blank <code>Part</code>
 * using the default stave <code>images</code>.
 * Calls the constructor Stave(Part, String, Notate)
 *
 * @param notate        the notation window for the Stave
 */
public Stave(Notate notate)
  {
    this(new MelodyPart(), notate, "");
  }

/**
 * Constructs a new <code>Stave</code> to display a blank <code>Part</code>
 * with a specified <code>type</code>.
 * Calls the constructor Stave(Part, String, Notate).
 *
 * @param type          an enum representing the type of Stave
 * @param notate        the notation window for the Stave
 */
public Stave(StaveType type, Notate notate, String staveTitle)
  {
    this(new MelodyPart(), type, notate, staveTitle);
  }

/**
 * Constructs a new <code>Stave</code> to display a given <code>Part</code>
 * using the default <code>type</code> GRAND.
 * Calls the constructor Stave(Part, String, Notate).
 *
 * @param part          Part to be displayed in stave
 * @param notate        the notation window for the Stave
 */
public Stave(MelodyPart part, Notate notate, String staveTitle)
  {
    this(part, StaveType.GRAND, notate, staveTitle);
  }

/**
 * Constructs a new <code>Stave</code> to display the specified
 * <code>part</code> using the specified stave <code>images</code>.
 *
 * @param part          Part to be displayed in stave
 * @param type          an enum representing the type of Stave
 * @param notate        the notation window for the Stave
 */
public Stave(MelodyPart part, StaveType type, Notate notate,
             String staveTitle)
  {
    super();
    this.origPart = part;
    this.partTitle = part.getTitle();
    this.sheetTitle = staveTitle;

    // set the notate frame
    this.notate = notate;

    chordFont = new Font("Helvetica", Font.BOLD, notate.getScore().getChordFontSize());

    // change 'paper' color
    this.setBackground(Color.white);
    this.setSize(panelWidth, panelHeight);

    // register the listerners
    staveActionHandler = new StaveActionHandler(this, notate);
    this.addMouseListener(staveActionHandler);
    this.addMouseMotionListener(staveActionHandler);
    this.addKeyListener(staveActionHandler);

    this.setLayout(null);

    // necessary for the keyboard to work
    this.requestFocus();

    // make space for any contour curve ahead of time
    this.clearCurvePoints();

    // Create specialized editors on this Stave for various data components
    // such as title, composer, etc.
    
    sheetTitleEditor = new EntryPopup("leadsheet title", 
                                      sheetTitleFont, 
                                      barNumFont,
                                      EntryPopup.CENTER, 
                                      this)
      {
      // Make the title the focus root

      @Override
      public void textToStave(String text)
        {
          Stave.this.notate.setTitle(text);
        }

      @Override
      public String staveToText()
        {
          return Stave.this.notate.getTitle();
        }
      };

    // I'm not sure of the way to make the focus cycle around.
    // The documentation is unclear to me.
    // I'm trying to use the approach specified in the Sun documentation.

    focusOrder.add(sheetTitleEditor);

    sheetComposerEditor = new EntryPopup("leadsheet composer", 
                                         composerFont, 
                                         barNumFont,
                                         EntryPopup.CENTER,  
                                         this)
        {

        @Override
        public void textToStave(String text)
            {
            Stave.this.notate.getScore().setComposer(text);
            }

        @Override
        public String staveToText()
            {
            return Stave.this.notate.getScore().getComposer();
            }
        };

    focusOrder.add(sheetComposerEditor);


    partTitleEditor = new EntryPopup("part title", 
                                     partTitleFont, 
                                     barNumFont, 
                                     EntryPopup.LEFT,  
                                     this)
      {
      @Override
      public void textToStave(String text)
        {
          Stave.this.setPartTitle(text);
          Stave.this.notate.refreshCurrentTabTitle();
        }

      @Override
      public String staveToText()
        {
          return Stave.this.getPartTitle();
        }
      };

    focusOrder.add(partTitleEditor);


    partComposerEditor = new EntryPopup("part composer", 
                                        composerFont, 
                                        barNumFont, 
                                        EntryPopup.LEFT,  
                                        this)
        {
        @Override
        public void textToStave(String text)
            {
            Stave.this.setComposer(text);
            }

        @Override
        public String staveToText()
            {
            return Stave.this.getComposer();
            }

        };

    focusOrder.add(partComposerEditor);


    showTitleEditor = new EntryPopup("show", 
                                     partTitleFont, 
                                     barNumFont, 
                                     EntryPopup.RIGHT, 
                                     this)
        {
        @Override
        public void textToStave(String text)
            {
            Stave.this.notate.getScore().setShowTitle(text);
            }

        @Override
        public String staveToText()
            {
            return Stave.this.notate.getScore().getShowTitle();
            }

        };

    showTitleEditor.setAlignment(EntryPopup.RIGHT);

    focusOrder.add(showTitleEditor);

    yearEditor = new EntryPopup("year", 
                                composerFont, 
                                barNumFont, 
                                EntryPopup.RIGHT,  
                                this)
        {
        @Override
        public void textToStave(String text)
        {
            Stave.this.notate.getScore().setYear(text);
        }

        @Override
        public String staveToText()
        {
            return Stave.this.notate.getScore().getYear();
        }

        };

    yearEditor.setAlignment(EntryPopup.RIGHT);

    focusOrder.add(yearEditor);

    notate.setFocusTraversalPolicy(new MyOwnFocusTraversalPolicy(focusOrder));



    // initializes all of the images associated with the Stave

    Images images;

    if( trebleClef == null )
      {
        images = ToolkitImages.getInstance();
        trebleClef = images.getTrebleClef();
        bassClef = images.getBassClef();
        crotchetDown = images.getCrotchetDown();
        crotchetUp = images.getCrotchetUp();
        filledNoteHead = images.getFilledNoteHead();
        quaverDown = images.getQuaverDown();
        quaverUp = images.getQuaverUp();
        semiquaverDown = images.getSemiquaverDown();
        semiquaverUp = images.getSemiquaverUp();
        demisemiquaverDown = images.getDemisemiquaverDown();
        demisemiquaverUp = images.getDemisemiquaverUp();
        minimDown = images.getMinimDown();
        minimUp = images.getMinimUp();
        semibreve = images.getSemibreve();
        dot = images.getDot();
        demisemiquaverRest = images.getDemisemiquaverRest();
        semiquaverRest = images.getSemiquaverRest();
        quaverRest = images.getQuaverRest();
        crotchetRest = images.getCrotchetRest();
        minimRest = images.getMinimRest();
        semibreveRest = images.getSemibreveRest();
        sharp = images.getSharp();
        flat = images.getFlat();
        natural = images.getNatural();
        smallbox = images.getSmallBox();
        one = images.getOne();
        two = images.getTwo();
        three = images.getThree();
        four = images.getFour();
        five = images.getFive();
        six = images.getSix();
        seven = images.getSeven();
        eight = images.getEight();
        nine = images.getNine();
        zero = images.getZero();
        tieOver = images.getTieOver();
        tieUnder = images.getTieUnder();
      }

    // Sets the type of Stave, such as GRAND or TREBLE.
    changeType(type);

    // Select the first slot on the stave.

    setSelection(0);
    
    this.add(noteCursorLabel);
    noteCursorLabel.setForeground(Color.DARK_GRAY);
    Font font = noteCursorLabel.getFont();
    noteCursorLabel.setFont(new Font(font.getName(), font.getStyle(), 24));
    noteCursorLabel.setSize(30, 30);
    
  }

/**
 * Every Stave is attached to a Notate window.
 * This method gives access to it.
 * @return 
 */

public Notate getNotate()
  {
    return notate;
  }


@Override
public boolean requestFocusInWindow()
  {
    if( notate.adviceVisible() )
      {
        notate.setStatus("Select Advice.");
      }

    boolean result = true;
    super.requestFocusInWindow(); // uncommented on r1418 to fix problem with listeners
    Trace.log(2, "stave has focus");
    return result;
  }

/**
 * Changes the Stave type and sets the spacing variables associated with
 * the type of Stave. Returns the previous Stave type.
 *
 * @param type              type of Stave to be displayed
 * @see #setSpacingVars()
 */
public StaveType changeType(StaveType type)
  {
    StaveType oldType = this.type;
    if( type == StaveType.GRAND
            || type == StaveType.TREBLE
            || type == StaveType.BASS )
      {
        this.type = type;
      }
    else if( type == StaveType.AUTO )
      {
        if( origPart.getLowestPitch() > notate.getBreakpoint() )
          {
            this.type = StaveType.TREBLE;
          }
        else if( origPart.getHighestPitch() <= notate.getBreakpoint() )
          {
            this.type = StaveType.BASS;
          }
        else
          {
            this.type = StaveType.GRAND;
          }
      }
    else
      {
        ErrorLog.log(ErrorLog.WARNING, "Unknown stave type passed: " + type
                + "Setting to GRAND by default");
        this.type = StaveType.GRAND;
      }

    setSpacingVars();
    repaint();
    return oldType;
  }

public static String staveTypeToString(StaveType type)
{
    switch( type )
      {
        case GRAND: return "Grand";
        case TREBLE: return "Treble";
        case BASS: return "Bass";
        case AUTO: return "Auto";
        case NONE:
        default: return "None";
      }
}

public static StaveType staveTypeFromString(String type)
{
    switch( type )
      {
        case "Grand":  return StaveType.GRAND;
        case "Treble": return StaveType.TREBLE;
        case "Bass":   return StaveType.BASS;
        case "Auto":   return StaveType.AUTO;
        case "None": 
              default: return StaveType.NONE;
      }
}

public void setPrinting(boolean printing)
  {
    this.printing = printing;
  }

public boolean getPrinting()
  {
    return printing;
  }

/***
 * Getter & Setter Functions for Stave
 **/
/**
 * Returns the action handler for the Stave
 * @return StaveActionHandler       the current action handler
 */
public StaveActionHandler getActionHandler()
  {
    return staveActionHandler;
  }

/**
 * Returns the type of the Stave
 * @return StaveType                the type of the stave
 */
public StaveType getStaveType()
  {
    return type;
  }

/**
 * Sets the current chord progression for this Stave instance
 * @param chordProg     the chord progression Part to be set in the Stave
 */
public void setChordProg(ChordPart chordProg)
  {
    this.chordProg = chordProg;
  }

/**
 * Returns the chord progression for the stave
 * @return Part         returns the chord progression
 */
public ChordPart getChordProg()
  {
    return chordProg;
  }

public void setOrigPart(MelodyPart part)
{
    System.out.println("setOrigPart " + part);
    origPart = part;
}

/**
 * Sets the current Part for this Stave instance.
 * @param part          the Part to be set in the Stave
 */
public void setPart(MelodyPart part)
  {
    setPartTitle(part.getTitle());
    setComposer(part.getComposer());

    // make a copy of the part and put tie flags on notes that are needed
    this.origPart = part;
    this.displayPart = part.copy();

    // Set the display part to have the correct accidentals and ties
    this.displayPart.makeAccidentals();
    this.displayPart.makeTies();

    try
      {
      // initialize the CstrLine array
      initCstrLines();
      }
    catch( OutOfMemoryError e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Out of Memory");
        return;
      }

    repaint();
  }

public int getNextCstrLine(int index)
  {
    do
      {
        index++;
      }
    while( index < cstrLines.length && cstrLines[index] == null );

    if( index >= cstrLines.length || cstrLines[index] == null )
      {
        return -1;
      }

    return index;
  }

public int getPreviousCstrLine(int index)
{
    do
    {
        index--;
    }
    while( index > 0 && cstrLines[index] == null);
    
    if( index < 0)
    {
        index = 0;
    }
            
    return index;
}

/**
 * Returns the original Part of this Stave instance
 * @return Part         the original Part in the Stave
 */
public MelodyPart getMelodyPart()
  {
    return this.origPart;
  }


/**
 * Returns the display Part of this Stave instance
 * @return Part         the displayed Part in the Stave
 */
public MelodyPart getDisplayPart()
  {
    return this.displayPart;
  }

/**
 * Sets the name for this Stave instance
 * @param title         the title of the part
 */
public void setPartTitle(String title)
  {
    this.partTitle = title;
    if( this.origPart != null )
      {
        this.origPart.setTitle(title);
      }
  }

/**
 * Sets the composer for this Stave instance
 * @param composer         the composer of this part
 */
public void setComposer(String composer)
  {
    this.composer = composer;
    if( this.origPart != null )
      {
        this.origPart.setComposer(composer);
      }
  }

/**
 * Sets the show
 * @param title         the show
 */
public void setShowTitle(String title)
  {
    this.showTitle = title;
    if( this.notate.score != null )
      {
        this.notate.score.setShowTitle(title);
      }
  }

/**
 * Sets the year
 * @param title         the year
 */
public void setYear(String year)
  {
    this.year = year;
    if( this.notate.score != null )
      {
        this.notate.score.setYear(year);
      }
  }

/**
 * Returns the show title
 * @return String       the title of the show, if any
 */
public String getShowTitle()
  {
    return showTitle;
  }

/**
 * Returns the year
 * @return String       the year, if known
 */
public String getYear()
  {
    return year;
  }

/**
 * Returns the name for this Stave instance
 * @return String       the title of the score
 */
public String getPartTitle()
  {
    return partTitle;
  }

/**
 * Returns the font of the part title so the
 * handler knows how to draw while the user edits
 */
public Font getPartTitleFont()
  {
    return partTitleFont;
  }

/**
 * Returns the composer for this Stave instance
 * @return String       the composer of this stave part
 */
public String getComposer()
  {
    return composer;
  }

/**
 * Empties the name of this Stave instance
 */
public void removeTitle()
  {
    this.partTitle = null;
  }

/**
 * Return the recommended height for this Stave.
 * @return int          returns the heigh of the Stave
 */
public int getPanelHeight()
  {
    return panelHeight;
  }

public int getPanelWidth()
  {
    return panelWidth;
  }

public int getHeadSpace()
  {
    return headSpace;
  }

/**
 * Sets the current metre for this Stave instance. This effects the
 * displayed time signature. 4 = 4/4, 3 = 3/4, etc.
 * @param timeSig       the metre for the Stave to be set to
 */
public void setMetre(int top, int bottom)
  {
    metre[0] = top;    // beats per measure
    metre[1] = bottom; // note value of one beat

    beatValue = WHOLE / bottom;
    measureLength = top * beatValue;

    origPart.setMetre(top, bottom);
  }

public int getMeasureLength()
  {
    return measureLength;
  }

/**
 * Returns the current metre for this Stave instance as a double
 * @return int         the metre of the Stave
 */
public int[] getMetre()
  {
    return this.metre;
  }

/**
 * Returns the current major key for this Stave instance as a integer
 * 0 is C, 1 is C#/Db major, 2 is D major, etc
 * @return int[]       the major key of the Stave
 */
public int getMajorKey()
  {
    int[] keys =
      {
        11, 6, 1, 8, 3, 10, 5, 0, 7, 2, 9, 4, 11, 6, 1
      };
    return keys[keySignature + 7];
  }

/**
 * Sets the current key signature for this Stave instance
 * This effects the displayed key signature. 1 = F# etc.
 * 0 is no key signature, + numbers for sharps, - numbers for flats
 * @param key           the key to set the key signature to
 */
public void setKeySignature(int key)
  {
    this.keySignature = key;
    notate.setKeySignature(key);
  }

/**
 * Sets the current key signature for this Stave instance
 * This effects the displayed key signature. 1 = F# etc.
 * 0 is no key signature, + numbers for sharps, - numbers for flats
 * @param key           the key to set the key signature to
 */
public void setKeySignatureNonRecursively(int key)
  {
    this.keySignature = key;
    origPart.setKeySignature(key);
  }

/**
 * Returns the current key signature for this Stave instance as a double
 * @return double       the key signature of the Stave
 */
public int getKeySignature()
  {
    return this.keySignature;
  }

/**
 * Shows the title of the Sheet and the Composer on the Stave if the boolean value equals true
 * @param show          true or false for if the title should be displayed
 */
public void setShowSheetTitle(boolean show)
  {
    showSheetTitle = show;
  }

/**
 * Shows the title of the Part on the Stave if the boolean value equals true
 * @param show          true or false for if the title should be displayed
 */
public void setShowPartTitle(boolean show)
  {
    showPartTitle = show;
  }

public void setShowEmptyTitles(boolean show)
  {
    showEmptyTitles = show;
  }

/**
 * Is the title displayed or not?
 * @return boolean      true or false for if the title is displayed
 */
public boolean getShowSheetTitle()
  {
    return showSheetTitle;
  }

public boolean getShowPartTitle()
  {
    return showPartTitle;
  }

public boolean getShowEmptyTitles()
  {
    return showEmptyTitles;
  }

/**
 * Decide to show bar numbers or not
 * @param show          true or false to show the bar numbers
 */
public void setShowBarNums(boolean show)
  {
    this.showBarNums = show;
  }

/**
 * Returns the current state of whether bar numbers should be showing
 * @return boolean      true or false for if the bar numbers are displayed
 */
public boolean getShowBarNums()
  {
    return showBarNums;
  }

/**
 * Decide to show a measure's construction lines or not
 * @param show          true or false to show the construction lines
 */
public void setShowMeasureCL(boolean show)
  {
    this.showMeasureCL = show;
  }

/**
 * Returns whether the current measure's construction lines should be shown
 * @return boolean      true or false for if construction lines are shown
 */
public boolean getShowMeasureCL()
  {
    return showMeasureCL;
  }

/**
 * Decide to show all construction lines or not
 * @param show          true or false to show the construction lines
 */
public void setShowAllCL(boolean show)
  {
    this.showAllCL = show;
  }

/**
 * Returns whether all construction lines should be showing
 * @return boolean      true or false for if construction lines are shown
 */
public boolean getShowAllCL()
  {
    return showAllCL;
  }

/**
 * Decide the minimum MIDI pitch number for this stave
 * @param min           the minimum pitch desired
 */
public void setMinPitch(int min)
  {
    this.minPitch = min;
  }

/**
 * Returns the current minimum MIDI pitch number
 * @return int          the minimum pitch allowed
 */
public int getMinPitch()
  {
    return this.minPitch;
  }

/**
 * Decide the maxinum MIDI pitch number for this stave
 * @param max           the maximum pitch desired
 */
public void setMaxPitch(int max)
  {
    this.maxPitch = max;
  }

/**
 * Returns the current maximum MIDI pitch number
 * @return int          the maximum pitch allowed
 */
public int getMaxPitch()
  {
    return this.maxPitch;
  }

/**
 * Decide to allow stave to be editable or not
 * @param state         true or false for the stave to be editable
 */
public void setEditable(boolean state)
  {
    this.editable = state;
  }

/**
 * Gets the size of the Stave
 * @return Dimension    set with a width and a height
 */
@Override
public Dimension getPreferredSize()
  {
    return new Dimension(this.getSize().width, this.getSize().height);
  }

/**
 * Sets the starting selection index
 * @param index         the index to start selection
 */
public void setSelectionStart(int index)
  {
    //debug System.out.pritln("setSelectStart " + index);
    this.selectionStart = index;

    if( lockSelectionSize != -1 )
      {
        this.selectionEnd = index + lockSelectionSize - 1;
      }
    notate.updateSelection();
  }

/**
 * Sets the ending selection index
 * @param index         the index to end selection
 */
public void setSelectionEnd(int index)
  {
  //debug System.out.println("setSelectEnd " + index);
  if( lockSelectionSize != -1 )
      {
        this.selectionEnd = selectionStart + lockSelectionSize - 1;
      }
    else
      {
        this.selectionEnd = index;
      }
    notate.updateSelection();
  }

/**
 * Sets both starting and ending selection indices
 * @param start the start index
 * @param end the end index
 */
public void setSelection(int start, int end)
  {
    //debug System.out.println("setSelection " + start + " to " + end);
    selectionStart = start;

    if( lockSelectionSize != -1 )
      {
        selectionEnd = start + lockSelectionSize - 1;
      }
    else
      {
        selectionEnd = end;
      }
    notate.updateSelection();
    
  }

/**
 * Sets both starting and ending selection indices to the same value
 * @param index the new start and end index
 * @param end the end index
 */
public void setSelection(int index)
  {
    selectionStart = index;

    if( lockSelectionSize != -1 )
      {
        selectionEnd = index + lockSelectionSize - 1;
      }
    else
      {
        selectionEnd = index;
      }
    notate.updateSelection();
  }

/**
 * Gets the selection length, where the length is 0
 * if both start and end coincide
 * @return int length of the selection
 */
public int getSelectionLength()
  {
    return selectionEnd - selectionStart;
  }

/**
 * Gets the starting selection index
 * @return int          the index to start selection
 */
public int getSelectionStart()
  {
    return selectionStart;
  }

public void setSelectionToEnd()
  {
    setSelectionEnd(origPart.size() - 1);
  }

/**
 * Gets the first non-rest index after the start of the selection
 * up to the end selection.
 * @return int          the index to start selection
 */
public int getNonRestSelectionStart()
  {
    for( int index = selectionStart; index <= selectionEnd; index++ )
      {
        Unit unit = origPart.getUnit(index);
        if( unit instanceof Note && ((Note) unit).nonRest() )
          {
            return index;
          }
      }
    return selectionEnd;
  }

/**
 * Gets the first non-reset index before the end of the selection
 * @return int          the index to start selection
 */
public int getNonRestSelectionEnd()
  {
    for( int index = selectionEnd; index >= selectionStart; index-- )
      {
        Unit unit = origPart.getUnit(index);
        if( unit instanceof Note )
          {
            Note note = (Note) unit;
            if( note.nonRest() )
              {
                return index + note.getRhythmValue() - 1;
              }
          }
      }
    return selectionStart;
  }

/**
 * Trim any rests from ends of selection.
 * Also, selection is expanded to include residual part of final note,
 * but not cut part of initial note.
 */
// This is only (and should only?) be used for determining if all rests.
boolean trimSelection()
  {
    int newStart = getNonRestSelectionStart();
    if( newStart >= selectionEnd )
      {
        return false; // all rest
      }

    int newEnd = getNonRestSelectionEnd();

    setSelection(newStart, newEnd);
    return true;
  }

public void setLockedSelectionEnd(int index)
  {
    this.selectionEnd = index;
    this.selectionStart = index - lockSelectionSize + 1;
    if( this.selectionStart < 0 )
      {
        setSelectionStart(0);
      }
  }

public void lockSelectionWidth(int slots)
  {
    lockSelectionSize = slots;
    setSelectionEnd(selectionEnd);
  }

public int getLockSelectionWidth()
  {
    return lockSelectionSize;
  }

public void unlockSelectionWidth()
  {
    lockSelectionSize = -1;
  }

/**
 * Indicates whether or not something is selected.
 */
public boolean somethingSelected()
  {
    return !nothingSelected();
  }

/**
 * Indicates whether just one slot is selected
 */
public boolean oneSlotSelected()
  {
    return selectionStart != OUT_OF_BOUNDS && selectionStart == selectionEnd;
  }

/**
 * Indicates whether or not nothing is selected.
 */
public boolean nothingSelected()
  {
    return selectionStart == OUT_OF_BOUNDS;
  }

/**
 * Gets the ending selection index
 * @return int          the index to end selection
 */
public int getSelectionEnd()
  {
    int trialSelectionEnd = selectionEnd;
// System.out.println("selectionEnd = " + selectionEnd);
//    Note note = (Note)getMelodyPart().getUnit(trialSelectionEnd);
//    if( selectionEnd != -1 && (note == null || note.isRest()) )
//      {
//        // round up to end of beat
//        if( trialSelectionEnd % BEAT != 0 )
//          {
//            trialSelectionEnd = (BEAT)*(1 + trialSelectionEnd/BEAT);
//          }
//      }
//    
//     System.out.println("trialSelectionEnd = " + trialSelectionEnd);

    return trialSelectionEnd;
  }

/**
 * Gets the ending selection index, taking into account the value of the last actual note.
 * Note that a little is reamed off the end, according to the value of reamSpinner
 * to reduce MIDI noise at the end of playback.
 * @return int 
 */
public int getSelectionEndNote(int selectionEnd)
  {

    // System.out.println("selectionEnd = " + ((float)selectionEnd)/BEAT);
    MelodyPart melody = getMelodyPart();

    ChordPart chords = getChordProg();

    int start = getSelectionStart();
    if( selectionEnd == start )
      {
        Note note = melody.getNote(start);
        if( note != null )
          {
            return start + StaveActionHandler.getEntryDuration(note);
          }
      }

    // Find the start of the last note in selection, if any.

    for( int i = selectionEnd; i >= selectionStart; i-- )
      {
        Note unit = melody.getNote(i);

        if( unit != null && !unit.isRest() )
          {
            int lastNoteStart = i;
            int lastNoteEnd = lastNoteStart + unit.getRhythmValue() - 1;

            // Determine if there is a chord change after the last note starts.
            // If so, end the selection there.

            for( int j = lastNoteStart + 1; j <= lastNoteEnd; j++ )
              {
                if( chords.getUnit(j) != null )
                  {
                    return j - 1;
                  }
              }

            // No chord change found. End the selection at the last note

            return lastNoteEnd;
          }
        return selectionEnd;
      }

    //System.out.println("selectionEnd = " + selectionEnd);

    // No note was found, so just return what was given

    return selectionEnd;
  }

public int getSelectionEndNote()
  {
    return getSelectionEndNote(getSelectionEnd());
  }

/**
 * Sets the starting index of the paste from area
 * @param index         the index starting the paste from area
 */
public void setPasteFromStart(int index)
  {
    this.pasteFromStart = index;
  }

/**
 * Gets the starting index of the paste from area
 * @return int          the index starting the paste from area
 */
public int getPasteFromStart()
  {
    return pasteFromStart;
  }

/**
 * Sets the ending index of the paste from area
 * @param index         the index ending the paste from area
 */
public void setPasteFromEnd(int index)
  {
    this.pasteFromEnd = index;
  }

/**
 * Gets the ending index of the paste from area
 * @return int          the index ending the paste from area
 */
public int getPasteFromEnd()
  {
    return pasteFromEnd;
  }

/***
 * Construction line methods
 **/
/**
 * Initializes the cstrLine array
 */
private void initCstrLines()
  {
    int numCstrLines = displayPart.size();
    
    int beats = numCstrLines/beatValue;
    
    cstrLines = new CstrLine[numCstrLines];
    
    //System.out.println("numCstrLines = " + numCstrLines);

    // set the subdivisions for each beat
    for( int i = 0; i < beats; i++ )
      {
        int subDivs = calcSubDivs(i);
        if( subDivs < defaultSubDivs && defaultSubDivs % subDivs == 0 )
          {
            subDivs = defaultSubDivs;
          }
        setSubDivs(i, subDivs);
      }
  }

/**
 * Resizes the construction line array to the part's size
 * @param part          the part whose size the cstrLines' length needs
 *                      to equal
 */
private void resizeCstrLines(MelodyPart part)
  {
    CstrLine[] tempCstrLines = new CstrLine[(part.size())];

    // increment cstrLines' length
    if( part.size() > cstrLines.length )
      {
        for( int i = 0; i < cstrLines.length; i++ )
          {
            if( cstrLines[i] != null )
              {
                tempCstrLines[i] = cstrLines[i];
              }
            else
              {
                tempCstrLines[i] = null;
              }
          }

        int oldLength = cstrLines.length;
        cstrLines = tempCstrLines;

        for( int i = oldLength / beatValue; i < part.size() / beatValue; i++ )
          {
            int subDivs = calcSubDivs(i);
            if( subDivs < defaultSubDivs && defaultSubDivs % subDivs == 0 )
              {
                subDivs = defaultSubDivs;
              }
            setSubDivs(i, subDivs);
          }
      }
    // decrement cstrLines' length
    else if( cstrLines.length > part.size() )
      {
        for( int i = 0; i < part.size(); i++ )
          {
            if( cstrLines[i] != null )
              {
                tempCstrLines[i] = cstrLines[i];
              }
            else
              {
                tempCstrLines[i] = null;
              }
          }

        cstrLines = tempCstrLines;
      }
  }

/**
 * Sets the subdivisions for a certain beat
 * @param beat          the beat on which to set the subdivisions
 * @param subdivs       the subdivisions to set for that beat
 */
public void setSubDivs(int beat, int subdivs)
  {
    int slotsPerDiv = beatValue / subdivs;
    if( subdivs == 0 || slotsPerDiv == 0 )
      {
        return;
      }  // prevent divide-by-0 error

    for( int i = beatValue * beat; i < cstrLines.length && i < beatValue * (beat + 1); i++ )
      {
        if( i % slotsPerDiv == 0 )
          {
            cstrLines[i] = new CstrLine(slotsPerDiv);
          }
        else
          {
            cstrLines[i] = null;
          }
      }
  }

/**
 * Returns the subdivisions for a certain beat
 * @param beat          the beat to get the subdivisions for
 * @return int          the subdivisions for that beat
 */
public int getSubDivs(int beat)
  {
    int subdivs = 0;
    for( int i = beatValue * beat; i < cstrLines.length && i < beatValue * (beat + 1); i++ )
      {
        if( cstrLines[i] != null )
          {
            subdivs++;
          }
      }

    return subdivs;
  }

/**
 * Increments the subdivisions for a certain beat if it can
 * @param beat          the beat on which to increment the subdivisions
 * @return int          the new subdivisions for that beat
 */
public int incSubDivs(int beat)
  {
    int curSubDivs = getSubDivs(beat);
    int minSubDivs = calcSubDivs(beat);

    int i;
    for( i = curSubDivs + 1; (i % minSubDivs != 0) || (beatValue % i != 0); i++ )  // beatValue was 120
      {
        continue;
      }
    return i;
  }

/**
 * Decrements the subdivisions for a certain beat if it can
 * @param beat          the beat on which to decrement the subdivisions
 * @return int          the new subdivisions for that beat
 */
public int decSubDivs(int beat)
  {
    int curSubDivs = getSubDivs(beat);
    int minSubDivs = calcSubDivs(beat);
    int i;
    for( i = curSubDivs - 1; (i > minSubDivs) && ((i % minSubDivs != 0) || (beatValue % i != 0)); i-- )                                        // beatValue was 120
      {
        continue;
      }

    if( i < minSubDivs )
      {
        return minSubDivs;
      }


    return i;
  }

/**
 * Calculates the subdivisions needed for a certain beat
 * @param beat          the beat to find the subdivisions for
 * @return int          the subdivisions needed for that beat
 */
public int calcSubDivs(int beat)
  {
    int index = beatValue * beat;

    int noteGcd = beatValue;
    while( index < beatValue * (beat + 1) && index < displayPart.size() )
      {
        Unit note = displayPart.getUnit(index);

        if( note != null )
          {
            int noteRV = note.getRhythmValue();
            if( index + noteRV > beatValue * (beat + 1) )
              {
                noteRV = beatValue * (beat + 1) - index;
              }
            noteGcd = gcd(noteGcd, noteRV);
            index += noteRV;
          }
        else
          {
            index++;
          }
      }

    index = beatValue * beat;
    int chordGcd = beatValue;
    while( index < beatValue * (beat + 1) && index < chordProg.size() )
      {
        Unit chord = chordProg.getUnit(index);

        if( chord != null )
          {
            int chordRV = chord.getRhythmValue();
            if( index + chordRV > beatValue * (beat + 1) )
              {
                chordRV = beatValue * (beat + 1) - index;
              }
            chordGcd = gcd(chordGcd, chordRV);
            index += chordRV;
          }
        else
          {
            index++;
          }
      }

    //System.out.println(beat + " - n: " + noteGcd + ", c: " + chordGcd);
    int gcd = gcd(noteGcd, chordGcd);

    if( gcd == 0 || beatValue / gcd == 0 )
      {
        return 1;
      }
    else
      {
        return beatValue / gcd;
      }
  }

/**
 * Finds the Greatest Common Denominator of two integers, a & b
 * @param a         first integer
 * @param b         second integer
 * @return int      the GCD of a and b
 */
public int gcd(int a, int b)
  {
    if( b == 0 )
      {
        return a;
      }
    else
      {
        return gcd(b, a % b);
      }
  }

/***
 * Drawing Code
 **/
/**
 * How many pixels a page is vertically
 */
protected static final int pageHeight = 1384;
/**
 * How many pixels each stave line should have between each other
 */
protected static final int staveSpaceHeight = 8;
/**
 * How many pixels from the left should the Stave start to be drawn
 */
protected static final int leftMargin = 20;
/**
 * How many pixels from the left should the show and title be
 */
protected static final int showTitleXoffset = 980;
/**
 * How many pixels from the left should the show and title be
 */
protected static final int yearXoffset = 980;
/**
 * How wide the Stave should be
 */
protected int STAVE_WIDTH = panelWidth - leftMargin;
/**
 * y-axis offset for an image, in pixels
 */
protected static final int imageHeightOffset = 28;
/**
 * How much space a clef should be allocated, in pixels
 */
protected static final int clefWidth = 38;
/**
 * How much space a time signature should be allocated, in pixels
 */
protected int timeSigWidth = 5;
/**
 * How much initial space a key signature should be allocated, in pixels
 */
protected int keySigWidth = 5;
/**
 * A y-axis spacing variable from the top of the panel
 */
protected static final int headSpace = 100;
/**
 * Distance from top of stave lines to the bottom of the next stave line area
 */
protected static final int lineOffset = 50;
/**
 * How many pixels a stave should be transposed between treble and bass
 */
private int staveDelta = 0;
/**
 * The current stave line the drawing is on.
 */
protected int staveLine = 0;
/**
 * The current page the drawing is on
 */
protected int page = 0;
/**
 * How far apart (in pixels) lines should be spaced
 */
protected int lineSpacing;
/**
 * The number of stave lines that should be drawn
 */
protected int numPitchLines;
/**
 * Current x-axis position on the Stave
 */
private int xCoordinate;
/**
 * Current y-axis position on the Stave
 */
private int yCoordinate;
/**
 * How the bar line should be modified in height depending on numPitchLines
 */
private int barHeightMod;
/**
 * Flag to indicate moving to the next stave line
 */
private boolean toNextLine;
/**
 * Flag if that Note is the first accidental displayed
 */
private boolean firstAccidentalDisplayed = false;
/**
 * Starting x-axis position of a tie
 */
private int startOfTie;
/**
 * Ending x-axis position of a tie 
*/
private int endOfTie;
/**
 * How much pixel spacing the construction lines should get
 */
private int cstrLineSpacing = 30;
/**
 * How should the construction line spacing be changed for each line.
 * Negative means scrunch the spacing, positive means widen it.
 */
private int spacingMod = 0;
/**
 * Number of measures on each line
 */
private int[] lineMeasures;
/**
 * Flag for if the previous line had only one measure
 */
private boolean prevLineHadOne = false;
/**
 * Flag for if a Note is dotted
 */
private boolean dottedNote = false;
/**
 * The highest y-axis position of a note in a beat. A lower number means
 * higher on the stave.
 */
private int highestYPos = panelHeight;
/**
 * Flag for if a Note has a tail up
 */
private boolean isUp = true;
/**
 * Flag for if the Unit is a Note (vs. a rest)
 */
private boolean isNote = false;

/**
 * Gets the array of points for the curve
 */
public int[] getCurvePoints()
  {
    return curvePoints;
  }

/**
 * Returns the lineMeasures array
 * @return int[]            the array indicating how many measures are in
 *                          each line
 */
public int[] getLineMeasures()
  {
    return lineMeasures;
  }

/**
 * Set the lineMeasures array
 */
public void setLineMeasures(int[] _lineMeasures)
  {
    lineMeasures = _lineMeasures;
  }

/**
 * Returns the lineMeasures array as a Polylist
 * @return Polylist indicating how many measures are in each line
 */
public Polylist getLayoutList()
  {
    Polylist result = Polylist.nil;
    for( int i = lineMeasures.length - 1; i >= 0; i-- )
      {
        int lineLength = lineMeasures[i];
        if( lineLength >= maxMeasuresPerLine )
            {
            // Quietly set the length to the max
            lineLength = maxMeasuresPerLine-1;
            }
        result = result.cons(new Long(lineLength));
      }
    return result;
  }

/**
 * Initializes some spacing variables, such as how much spacing should be
 * between the staves on each line and how should the notes be shifted
 * depending on if the stave is transposed to treble or bass.
 *
 * Assumes a valid <code>type</code> has already been set.
 */
private void setSpacingVars()
  {
    if( type == StaveType.TREBLE || type == StaveType.BASS )
      {
        numPitchLines = 5;
        lineSpacing = (numPitchLines + 15) * staveSpaceHeight;
        barHeightMod = 1;
        if( type == StaveType.BASS )
          {
            staveDelta = staveSpaceHeight * 6;
          }
        else
          {
            staveDelta = 0;
          }
      }
    else
      {
        numPitchLines = 11;
        lineSpacing = (numPitchLines + 15) * staveSpaceHeight;
        barHeightMod = 6;
        staveDelta = 0;
      }
  }

/**
 * Selection cache allows us to store the selection coordinates as it is
 * drawn to a buffer, and then draw a box around the selection (after
 * drawing has finished).
 */
/**
 * Stores the current location of the stave drawing as a selection point
 * Keeps track of the first and last selection points
 */
private void selectionCacheSet(int width)
  {
    if( !firstBoxFound )
      {
        firstBoxFound = true;
        firstBoxX = xCoordinate;
        firstBoxStave = staveLine;
      }

    if( staveLine > lastBoxStave || staveLine == lastBoxStave && xCoordinate + width > lastBoxX )
      {
        lastBoxX = xCoordinate + width;
        lastBoxStave = staveLine;
      }
  }

/**
 * Resets selection cache (called when a draw begins)
 */
private void selectionCacheReset()
  {
    firstBoxFound = false;
    lastBoxX = 0;
    lastBoxStave = 0;
    selectionBox.clear();
  }

private ArrayList<EntryPopup> focusOrder = new ArrayList<EntryPopup>();



/**
 * Draws the stave with all components onto the screen. This methods
 * originally created one stave line, but has since been edited to
 * incorporate multiple stave lines.
 * <p>
 * Assumes that a valid stave <code>type</code> has already been set.
 *
 * @param g             draws a stave on the graphics passed to paint
 * @see #drawClef(Graphics)
 * @see #drawKeySig(int, Graphics)
 * @see #drawTimeSig(Graphics)
 * @see #drawPart(MelodyPart, Graphics)
 */

@Override
protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // background color
    g.setColor(this.getBackground());
    int height = this.getHeight(), width = this.getWidth();
    if( panelHeight > height )
      {
        height = panelHeight;
      }
    if( panelWidth > width )
      {
        width = panelWidth;
      }

    g.fillRect(0, 0, width, height);
    g.setColor(Color.black);

    // Every time a paint occurs, an entire part is copied!

    this.displayPart = this.origPart.copy();
    this.displayPart.makeAccidentals();
    this.displayPart.makeTies();

    // initialize the stave line
    staveLine = 0;
    toNextLine = false;

    // initialize the pages
    // TODO: Need to add ALL other page code
    page = 0;


    // add a title for the part if set to be visible

    if( showSheetTitle )
      {
        sheetTitleEditor.draw(g, panelWidth / 2, sheetTitleYoffset);
        sheetComposerEditor.draw(g, panelWidth / 2, sheetComposerYoffset);
        showTitleEditor.draw(g, showTitleXoffset, showTitleYoffset);
        yearEditor.draw(g, yearXoffset, yearYoffset);
      }

    if( showPartTitle )
      {
        partTitleEditor.draw(g, leftMargin, partTitleYoffset);
        partComposerEditor.draw(g, leftMargin, partComposerYoffset);
      }

    // set the font for bar numbers
    g.setFont(barNumFont);

    // draw the first bar line for the measure
    drawBarLine(leftMargin, staveLine, g);

    // draw the stave lines for the first line
    drawStave(staveLine, leftMargin, STAVE_WIDTH, g);

    // add the proper Clef
    drawClef(g);

    // insert key signature if required
    int keyOffset = 0;

    // draws the key signature, returns pixel spacing in the x-axis
    keyOffset = drawKeySig(keyOffset, g);
    keySigWidth = keyOffset + 5;

    // insert time signature if required
    if( metre[0] != 0 && metre[1] != 0 )
      {
        timeSigWidth = drawTimeSig(g);
      }
    else
      {
        timeSigWidth = 5;
      }

    // set indent position for first note (in pixels)

    int firstNoteOffset = 5;
    int totalIndentation =
            leftMargin + clefWidth + keySigWidth + timeSigWidth + firstNoteOffset;

    xCoordinate = totalIndentation;

    //System.out.println("xCoordinate = " + xCoordinate);

    selectionCacheReset();

    // draw the notes, rests, and stave for each note in the part
    if( !drawPart(displayPart, g) )
      {
        return;
      }

    if( notate.noLockedMeasures() )
      {
        notate.setLockedMeasures(lineMeasures, "paint");
      }

    if( notate.getMode() == Notate.Mode.DRAWING )
      {
        paintContour(g);
      }

    if( firstBoxFound )
      {
      if( notate.getShowConstructionLinesAndBoxes() )
        {
        for( int lineCounter = firstBoxStave; lineCounter <= lastBoxStave; lineCounter++ )
          {
            Rectangle r = new Rectangle(firstBoxX - selectionBoxPadding,
                                        headSpace + lineCounter * lineSpacing - lineOffset,
                                        lastBoxX - firstBoxX + selectionBoxPadding / 2, lineSpacing);
            if( lineCounter != firstBoxStave )
              {
                r.width += r.x - (leftMargin - selectionBoxPadding);
                r.x = (leftMargin - selectionBoxPadding);
              }
            if( lineCounter != lastBoxStave )
              {
                r.width = STAVE_WIDTH + selectionBoxPadding - r.x;
              }
            selectionBox.add(r);
          }
        
        drawSelectionBox(g);
        }
      
      selectionBoxDrawn = true;
      }
    else
      {
        selectionBoxDrawn = false;
      }
  }

public void setNoteCursorLabel(String str, int x, int y)
{
    
    noteCursorLabel.setLocation(x-24, y-24);
    noteCursorLabel.setText(str);

}

public void clearNoteCursorLabel()
{
    setNoteCursorLabel("", 0, 0);
}

private boolean selectionBoxDrawn = false;

public boolean getSelectionBoxDrawn()
  {
    return selectionBoxDrawn;
  }

public void paintContour(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setColor(curveColor);
    g2d.setStroke(curveStroke);

    for( int i = 0; i < curvePoints.length - 1; i++ )
      {

        if( curvePoints[i] != -1 && curvePoints[i + 1] != -1 )
          {
            g2d.drawLine(i, curvePoints[i], i + 1, curvePoints[i + 1]);
          }
      }

    g2d.setStroke(new BasicStroke(1));
  }

public void clearCurvePoints()
  {
    curvePoints = new int[this.getWidth()];
    for( int i = 0; i < curvePoints.length; i++ )
      {
        curvePoints[i] = -1;
      }
  }

public Color getCurveColor()
  {
    return curveColor;
  }

public Stroke getCurveStroke()
  {
    return curveStroke;
  }

/**
 * draw the selection box, and the handles (if needed)
 */
int oldSelectionFirstX = 0, oldSelectionFirstY = 0;

private void drawSelectionBox(Graphics g)
  {
    for( Rectangle r : selectionBox )
      {
        if( transparentFill )
          {
            g.setColor(selectionColor);
            g.fillRect(r.x, r.y, r.width, r.height);
          }
        g.setColor(selectionBorderColor);
        g.drawRect(r.x, r.y, r.width, r.height);
      }

    Rectangle first = selectionBox.get(0);
    Rectangle last = selectionBox.get(selectionBox.size()-1);

    selectionLHandle.x = first.x - 3;
    selectionLHandle.y = first.y;
    selectionLHandle.height = lineSpacing;

    selectionRHandle.x = last.x + last.width - 3;
    selectionRHandle.y = last.y;
    selectionRHandle.height = lineSpacing;
  }

private int playingSlot = -1;

public void repaintDuringPlayback(int currentSlot)
  {
    if( notate.getPlaying() != MidiPlayListener.Status.STOPPED )
      {
        if( currentSlot < 0 )
          {
            currentSlot = 0;
          }

        while( cstrLines[currentSlot] == null )
          {
            currentSlot--;
          }

        if( currentSlot != playingSlot )
          {
            repaintLineFromCstrLine(playingSlot);

            playingSlot = currentSlot;
            repaintLineFromCstrLine(playingSlot);
          }
      }
  }

public void repaintLineFromCstrLine(int i)
  {
    if( i < 0 || i >= cstrLines.length )
      {
        repaint();
        return;
      }

    int j = i;
    int y1 = 0, y2 = panelHeight;
    while( cstrLines[i] == null && i > 0 )
      {
        i--;
      }

    if( cstrLines[i] != null )
      {
        y1 = cstrLines[i].getY() - headSpace;
        y1 = (y1 / lineSpacing) * lineSpacing;
      }

    while( cstrLines[j] == null && j < cstrLines.length - 1 )
      {
        j++;
      }

    if( cstrLines[j] != null )
      {
        y2 = cstrLines[j].getY() - headSpace;
        y2 = (y2 / lineSpacing) * lineSpacing;
      }

    /* Note: This is not actually correct. y1 + headSpace should be the top
     * of the selection box on the line in question, but clearly it is not...
     * The selection box code shifts itself vertically -50px, so this code
     * does the same.  Just to be safe, we add 50px back to the height, meaning
     * a portion of the next line will be redrawn too.
     */

    repaint(0, y1 + headSpace - lineOffset, panelWidth,
            y2 - y1 + lineSpacing + lineOffset);
  }

private boolean usePhi = false;
private boolean useDelta = false;
public boolean getPhi() {
    return usePhi;
}
public boolean getDelta() {
    return useDelta;
}
public void setPhi(boolean phi) {
    usePhi = phi;
}
public void setDelta(boolean delta) {
    useDelta = delta;
}

/**
 * changes m7b5 into a phi symbol
 * changes M7 into a delta symbol
 */
public String toSymbols(String chordName)
{
    String result = chordName;
    if(!(useDelta+"").equals(notate.getDeltaStatus()+""))
        setDelta(notate.getDeltaStatus());
    if(!(usePhi+"").equals(notate.getPhiStatus()+""))
        setPhi(notate.getPhiStatus());
    if(useDelta)
        result = toDelta(result);
    if(usePhi)
        result = toPhi(result);
   
    if(result.length()==0)
        result = chordName;

    return result;
}

public String toPhi(String chordName)
{
    if(!contains(chordName, "m7b5"))
        return chordName;
    else
    {
        if(chordName.substring(0,4).equals("m7b5"))
            return "\u03D5" + toPhi(chordName.substring(4));
        else if(chordName.substring(1,5).equals("m7b5"))
            return chordName.substring(0,1) + "\u03D5" + toPhi(chordName.substring(5));
        else if(chordName.substring(2,6).equals("m7b5"))
            return chordName.substring(0,2) + "\u03D5" + toPhi(chordName.substring(6));
        else if(chordName.substring(3,7).equals("m7b5"))
            return chordName.substring(0,3) + "\u03D5" + toPhi(chordName.substring(7));
        else
            return chordName.substring(0,4) + toPhi(chordName.substring(4));
    }
}

public String toDelta(String chordName)
{
    if(!contains(chordName, "M7"))
        return chordName;
    else
    {
        if(chordName.substring(0,2).equals("M7"))
            return "\u0394" + toDelta(chordName.substring(2));
        else if(chordName.substring(1,3).equals("M7"))
            return chordName.substring(0,1) + "\u0394" + toDelta(chordName.substring(3));
        else
            return chordName.substring(0,2) + toDelta(chordName.substring(2));
    }
}

public boolean contains(String chordName, String target)
{
    for(int j = 0; j <= chordName.length() - target.length(); j++)
        if(chordName.substring(j,j+target.length()).equals(target))
            return true;
    return false;
}

NoteStatistics noteStat;

/**
 * Cycles through the slots in a given MelodyPart, part, and draws the
 * entire stave consisting of the construction lines, notes, accidentals,
 * chords, and various brackets. It can only draw a unit (note, rest, or
 * chord), if there is a construction line on the unit's slot. If there is
 * not a construction line there, drawPart will call calcSubDivs(int) on
 * the slot's beat to reset the subdivisions to incorporate the unit.
 * <p>
 * Assumes that the part has already been made with makeAccidentals() and
 * makeTies();
 *
 * @param part          the part to draw on the Stave
 * @param g             the panel to draw onto
 *
 * @see #drawNote(Note, boolean, Graphics)
 * @see #findSpacing(int, MelodyPart)
 */
private boolean drawPart(MelodyPart part, Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

    // initialize the total number of measures to 0
    int totalMeasureCount = 0;
    // initialize the number of measures on the line to 0
    int lineMeasureCount = 0;

    // initialize the lineMeasures array
    setLineMeasures(new int[1]);
    // reset the prevLineHadOne flag
    prevLineHadOne = false;
    // find the # of measures on the first line, and the initial spacingMod
    int measuresOnLine = findSpacing(0, staveLine, part);

    // start drawing a little after the time signature
    xCoordinate += cstrLineSpacing + spacingMod;

    // initialize the construction line bracket position variables
    int bracketStart = xCoordinate - 10;
    int bracketEnd = bracketStart;

    // initialize the tuplet bracket start and end position variables
    int tupletStart = xCoordinate - 10;
    int tupletEnd = tupletStart;
    int tupletEndIndex = OUT_OF_BOUNDS;
    int tupletStartIndex = OUT_OF_BOUNDS;

    // how many notes are in a certain tuplet
    int tupletValue = 0;

    // the last note entered, used with drawing ties
    lastNote = null;

    // The index of lastNote
    ilast = -1;

    // Whether or not lastNote was beamed
    lastNoteBeamed = false;

    // Whether or not lastNote had stem up (used for beaming)
    lastNoteStemUp = true;

    // Next note: use for beaming
    Note nextNote;

    int inext;

    // the color indices for the notes in the part
    noteStat = collectNoteColors(part);

    // box a group of tied notes
    boolean boxTies = false;

    // resize the cstrLines array if its length does not equal part's length
    if( cstrLines.length != part.size() )
      {
        resizeCstrLines(part);
      }

//    Note pitchDeterminer = null;
    
    SectionInfo sectionInfo = chordProg.getSectionInfo();

    Iterator<SectionRecord> sectionIter = sectionInfo.iterator();
    
    if( !sectionIter.hasNext() )
      {
        return false;
      }
    
    SectionRecord record = sectionIter.next();

    Style style = record.getStyle(); // sectionInfo.getStyleFromSlots(0);

    int previousSectionType = record.getSectionType(); // sectionInfo.sectionAtSlot(0);
    
    int sectionType;
    
    int nextSectionStart;
    
    String styleName = record.getStyleName();
    
    boolean indicateStyle = true;
    
    if( sectionIter.hasNext() )
      {
        record = sectionIter.next();
        nextSectionStart = record.getIndex();
      }
    else
      {
        nextSectionStart = chordProg.getSize(); // i.e. "infinity"
      }

    Style previousStyle = null;
//System.out.println();    
    // cycle through the entire part
    for( int i = 0; i < cstrLines.length; i++ )
      {
         if( indicateStyle && style != previousStyle && !styleName.equals(Style.USE_PREVIOUS_STYLE) )
           {
//  System.out.println("style = " + style + " vs. " + previousStyle);
           g.drawString(STYLE_MARK + styleName, 
                        xCoordinate - styleXoffset, 
                        headSpace + (staveLine * lineSpacing) - styleYoffset);
                      
           indicateStyle = false;
           previousStyle = style;
           }
                  
        Note note = part.getNote(i);
        Note orignote = origPart.getNote(i);

//        if( orignote != null )
//          {
//            pitchDeterminer = orignote;
//          }

        // Handle sections within or at start of line.

        int xSection = xCoordinate - 25;
        
          if( i+1 == nextSectionStart )
            {
              // Starting a new Section

              Style newStyle = record.getStyle();
              
              if( newStyle != null )
                {
                  previousStyle = style;
                  style = newStyle;
                  styleName = newStyle.getName();
                }

               sectionType = record.getSectionType();
              
              if( sectionIter.hasNext() )
                {
                // Have gotten java.util.ConcurrentModificationException here
                record = sectionIter.next();
                nextSectionStart = record.getIndex();
                }
              else
                {
                nextSectionStart = chordProg.getSize(); // i.e. "infinity"
                }

              if( previousSectionType == Block.SECTION_END || previousSectionType == Block.PHRASE_END )
                {
                  int ySection = headSpace + (staveLine * lineSpacing) - styleYoffset;
/*
                  System.out.println("staveLine = " + staveLine
                          + ", xSection = " + xSection
                          + ", ySection = " + ySection
                          + ", i = " + i + ", sectionType = "
                          + (sectionType == Block.SECTION_END ? "section" : "phrase")
                          + ", previousSectionType = "
                          + (previousSectionType == Block.SECTION_END ? "section" : "phrase"));
*/
                  if( previousSectionType == Block.PHRASE_END )
                    {
                      // Possibly draw phrase mark.
                      if( i > 0 && notate.showPhrasemarks() )
                        {
                          Font saveFont = g.getFont();
                          g.setFont(phraseMarkFont);
                          g.drawString(PHRASE_MARK,  
                                       PHRASE_MARK_X_OFFSET + xSection, 
                                       ySection + PHRASE_MARK_Y_OFFSET);
                          g.setFont(saveFont);
                        }
                    }

                  if( previousSectionType == Block.SECTION_END /* && lineMeasureCount > 1*/ )
                    {
                      // Set up to draw double barline when barline is drawn.
                      // Also possibly indicate style at start of next section.
                      doubleBar = true;
                      indicateStyle = true;
                     }
                 }
              

              previousSectionType = sectionType;
            }
        
            
        int noteValue = orignote == null ? 0 : orignote.getRhythmValue();

        // Check to see if the proper resolution is set for a beat.
        // beatValue is, e.g. 120

        // Tuplets can start on beatres indices.

        int beatres = beatValue / 4;

        if( i % beatres == 0 )
          {
            // We need this to set the construction lines, even tho' sdivs
            // is no longer used.

            int sdivs = checkBeatResolution(i / beatres);

            // This chain of tests can be folded into an iteration, probably.

            if( noteValue == THIRTYSECOND_TRIPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 3;
                tupletEndIndex = i + 3 * THIRTYSECOND_TRIPLET - 1;
              }
            else if( noteValue == THIRTYSECOND_QUINTUPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 5;
                tupletEndIndex = i + 5 * THIRTYSECOND_QUINTUPLET - 1;
              }
            else if( noteValue == SIXTEENTH_TRIPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 3;
                tupletEndIndex = part.getNote(i + SIXTEENTH) != null ? i + SIXTEENTH - 1 : i + 3 * SIXTEENTH_TRIPLET - 1;
              }
            else if( noteValue == SIXTEENTH_QUINTUPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 5;
                tupletEndIndex = i + 5 * SIXTEENTH_QUINTUPLET - 1;
              }
            else if( noteValue == EIGHTH_TRIPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 3;
                tupletEndIndex = part.getNote(i + EIGHTH) != null ? i + EIGHTH - 1 : i + 3 * EIGHTH_TRIPLET - 1;
              }
            else if( noteValue == EIGHTH_QUINTUPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 5;
                tupletEndIndex = i + 5 * EIGHTH_QUINTUPLET - 1;
              }
            else if( noteValue == QUARTER_TRIPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 3;
                tupletEndIndex = part.getNote(i + QUARTER) != null ? i + QUARTER - 1 : i + 3 * QUARTER_TRIPLET - 1;
              }
            else if( noteValue == QUARTER_QUINTUPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 5;
                tupletEndIndex = i + 5 * QUARTER_QUINTUPLET - 1;
              }
            else if( noteValue == HALF_TRIPLET )
              {
                tupletStart = xCoordinate - 10;
                tupletStartIndex = i;
                tupletValue = 3;
                tupletEndIndex = part.getNote(i + HALF) != null ? i + HALF - 1 : i + 3 * HALF_TRIPLET - 1;
              }
            else if( tupletEndIndex < i )
              {
                tupletValue = OUT_OF_BOUNDS;
              }
          }

        // if there is a construction line at the given index...
        if( cstrLines[i] != null )
          {
          if( notate.getShowConstructionLinesAndBoxes() )
            {
            // draw a construction line orange/red if selected

            if( i >= selectionStart && i <= selectionEnd )
              {
                if( i % beatValue == 0 )
                  {
                    drawCstrLine(xCoordinate, staveLine,
                                 Color.red, 2, g);
                  }
                else
                  {
                    drawCstrLine(xCoordinate, staveLine,
                                 Color.orange, 2, g);
                  }

                selectionCacheSet(10);
              }
            // else draw the constuction line at index i if showAllCL true
            else if( showAllCL || (showMeasureCL && mouseOverMeasure == totalMeasureCount) )
              {
                if( i % beatValue == 0 )
                  {
                    drawCstrLine(xCoordinate, staveLine,
                                 Color.DARK_GRAY, 1, g);
                  }
                else
                  {
                    drawCstrLine(xCoordinate, staveLine,
                                 Color.LIGHT_GRAY, 1, g);
                  }
              }
            }
          
          if( i == playingSlot && notate.getPlaying() != MidiPlayListener.Status.STOPPED )
              {
                drawPlayLine(xCoordinate, staveLine, Color.GREEN, 3, g,
                             notate.showPlayLine());
              }

            // set the position of the construction line in cstrLines
            cstrLines[i].setX(xCoordinate);
            cstrLines[i].setY(headSpace + (staveLine * lineSpacing) + (numPitchLines * staveSpaceHeight) / 2);

            // if there is a note at the given index, draw it
            if( note != null )
              {
                boolean stemUp = lastNoteBeamed ? lastNoteStemUp : isStemUp(note, type);

                // Beamed is true when there is a beam from this note to the next

                inext = part.getNextIndex(i);

                nextNote = part.getNote(inext);

                // We need to look at the next note, if any, to determine whether to get a stem
                // for beaming or a regular stem.

                // conditions for beaming forward:
                // These are used to determined whether a note stands alone or has a beam.
                // A note having a beam will not also have a flag.

                boolean beamed = beamingNotes // beaming desired
                        && sameBeat(i, inext) // in same beat interval
                        && note.getRhythmValue() < 80 // less than quarternote
                        && nextNote != null // next note exists
                        && isaNote(nextNote.getPitch())
                        && sameStemDirection(note, nextNote, type)
                        && note.getRhythmValue() == nextNote.getRhythmValue(); // compatibility

                boolean beamThis = beamed || lastNoteBeamed;

                //System.out.println("beamed = " + beamed + ", i = " + i + ", inext = " + inext + ", sameBeat = " + sameBeat(i, inext));
                if( isNote 
                        && i >= selectionStart
                        && i <= selectionEnd
                        && (note.firstTied() || !note.isTied())
                        && note.getPitch() != Note.REST // May seem redundant, but this is need to control boxing
                        )
                  {
                    //System.out.println("Case A " + beamed + " " + note);
                    drawNote(note, true, i, g, g2, noteStat.getColor(i), part, beamThis, inext, stemUp);
                    boxTies = true;
                  }
                else if( boxTies == true && !note.firstTied() && note.isTied() )
                  {
                    //System.out.println("Case B " + beamed + " " + note);
                    drawNote(note, true, i, g, g2, noteStat.getColor(i), part, beamThis, inext, stemUp);
                  }
                else
                  {
                    //System.out.println("Case C " + beamed + " " + note);
                    drawNote(note, false, i, g, g2, noteStat.getColor(i), part, beamThis, inext, stemUp);
                    boxTies = false;
                  }

                lastNote = note;
                lastNoteBeamed = isaNote(note.getPitch()) && beamed; // remember for next time around
                lastNoteStemUp = stemUp;
                ilast = i;

                // in case a bracket needs to be drawn

                int noteTop = yCoordinate - (stemUp ? upStemBracketCorrection : 0);

                if( noteTop < highestYPos )
                  {
                    highestYPos = noteTop;
                  }

                cstrLines[i].setHasNote(true);
              }
            else
              {
                cstrLines[i].setHasNote(false);
              }

            // if there's a chord progression at the given index, draw the
            // name of the chord
            if( i < chordProg.size() && chordProg.getChord(i) != null )
              {
                // calculate the height to draw the chord at
                int chordHeight = headSpace + (staveLine * lineSpacing) - 25;
                if( yCoordinate < chordHeight + 5 )
                  {
                    chordHeight = yCoordinate + 5;
                  }

                // If the chord symbol is getting drawn up a line for some
                // reason, we're just going to bring it down an arbitrary distance.
                if( chordHeight < (staveLine - 1) * lineSpacing + 100 )
                  {
                    chordHeight += lineSpacing - 20;
                  }

                // Hack; if the note is a quarter-note triplet that crosses over a barline,
                // we need to adjust the height of the chord so it doesn't run into the bracket.
                if( note != null && note.isTied() && note.getRhythmValue() == beatValue / 3 )
                  {
                    chordHeight -= 20;
                  }

                // draw the chord red if selected, black if not
                if( i >= selectionStart && i <= selectionEnd )
                  {
                    g.setColor(Color.RED);
                  }
                else
                  {
                    g.setColor(Color.BLACK);
                  }

                g.setFont(chordFont);
                
                String chordName = toSymbols(chordProg.getChord(i).getName());
                
                g.drawString(chordName,
                             xCoordinate - 4,
                             chordHeight);
                
                g.setFont(barNumFont);

                g.setColor(Color.BLACK);

                cstrLines[i].setHasChord(true);
              }

            cstrLines[i].setSpacing(cstrLineSpacing + spacingMod);

            // increments the pixel spacing
            xCoordinate += (cstrLineSpacing + spacingMod);

            // incremement where the beatBracket might end
            // goes back one cstr line and sets it to 10 pixels to the right
            bracketEnd = xCoordinate - cstrLineSpacing - spacingMod + 10;
            tupletEnd = bracketEnd;
          }

        // if the next index is on the start of a new beat...
        if( (i + 1) % beatValue == 0 )
          {
            // draw a bracket for the beat
            if( showAllCL || (showMeasureCL && mouseOverMeasure == totalMeasureCount) )
              {
                drawBeatBracket(getSubDivs(i / beatValue), bracketStart,
                                bracketEnd, staveLine, g);
              }
          }

//FIX: There is an error in drawing tuplet brackets. Just because the first
// note in a group has the value that could be a tuplet does not mean that
// all notes do. All values need to be checked.

        // draw a bracket for the tuplet
        if( tupletValue != OUT_OF_BOUNDS && tupletEndIndex == i )
          {
            int tupletBeatValue =
                    ((tupletEndIndex + 1) - tupletStartIndex) / tupletValue;

            for( int j = 0; j <= tupletValue; ++j )
              {
                int index;

                  /* Not sure why there was a conditional here.
                  
                  if( j * tupletBeatValue >= beatValue )
                  {
                  index = (tupletStartIndex + j * tupletBeatValue) - 1;
                  }
                  else
                   */
                  {
                    index = tupletStartIndex + j * tupletBeatValue;
                  }

                // Conditional to draw the tuplet bracket.  We only draw the tuplet bracket if
                // a) the note is not null AND
                // b) the note is not a rest, the note is different from a beat ago, the note value is
                //    divisible by the triplet value OR
                // c) the end index - the start index is greater than the beat value; this means we are
                //    dealing with quarter note triplets.

                Note noteAtIndex = part.getNote(index);

                if( noteAtIndex != null
                        // why? && !noteAtIndex.isRest() 
                        // && part.getNote(index - 1) != noteAtIndex
                        && noteAtIndex.getRhythmValue() < tupletBeatValue * tupletValue
                        && noteAtIndex.getRhythmValue() % tupletBeatValue == 0 // why? || tupletEndIndex - tupletStartIndex > beatValue
                        )
                  {
                    drawTupletBracket(tupletValue, tupletStart, tupletEnd, staveLine,
                                      g);
                    break;
                  }
              }
          }

        if( (i + 1) % beatValue == 0 )
          {
            highestYPos = panelHeight;
            bracketStart = xCoordinate - 10;
          }

        // check to see if the next index starts a measure, indicating
        // the current measure is full
        if( (i + 1) % measureLength == 0 )
          {
            // increment the number of total measures
            totalMeasureCount++;
            // increment the number of measures on the line
            lineMeasureCount++;

            // add bar numbers?
            if( showBarNums && (lineMeasureCount != measuresOnLine) )
              {
                g.drawString("" + (totalMeasureCount + 1),
                             xCoordinate - 4,
                             headSpace + (staveLine * lineSpacing) - 10);
              }

            // if the line measures is maxed out draw a bar line and set
            // the toNextLine flag
            if( lineMeasureCount == measuresOnLine )
              {
                drawBarLine(STAVE_WIDTH, staveLine, g);
                toNextLine = true;
              }
            // otherwise draw the bar line at the current location
            else
              {
                drawBarLine(xCoordinate, staveLine, g);
                // set the bracketStart to 10 pixels before the next cstr
                bracketStart = xCoordinate + (cstrLineSpacing + spacingMod) - 10;

                if( i == part.size() - 1 )
                  {
                    // white out any extra stave space
                    whiteOutStave(staveLine, xCoordinate + 2, STAVE_WIDTH, g);
                  }
                else
                  {
                    // give the stave a little space after the bar line
                    xCoordinate += (cstrLineSpacing + spacingMod);
                  }
              }
          }

        // if the stave should go to the next line...
        if( toNextLine == true && i != part.size() - 1 )
          {
            // increment the stave position on the y-axis and draw the new
            // line's pitch lines
            staveLine++;

            // increase the stave's size if it is too small
            if( headSpace + (lineSpacing * (staveLine + 1)) > panelHeight )
              {
                panelHeight += pageHeight;
                this.setSize(panelWidth, panelHeight);
              }

            drawStave(staveLine, leftMargin, STAVE_WIDTH, g);

            // draw a bar line at the right margin, with bar numbers if needed

            drawBarLine(leftMargin, staveLine, g);

            xSection = xCoordinate - 25;

            if( sectionInfo.sectionAtSlot(i) == Block.SECTION_END )
              {
                g.drawString(STYLE_MARK
                        + sectionInfo.getStyleFromSlots(i),
                             xSection,
                             headSpace + (staveLine * lineSpacing) - styleYoffset);
              }

            if( showBarNums )
              {
                g.drawString("" + (totalMeasureCount + 1),
                             leftMargin - 4,
                             headSpace + (staveLine * lineSpacing) - 10);
              }

            // if the last note is tied and the note on the next measure is
            // tied, draw a tie from the end of the note to a little past
            // the end of the line

            int totalLength = totalMeasureCount * measureLength;

            if( isNote && totalLength < part.size() )
              {
              Note aNote = part.getNote(totalLength);
              if(   lastNote != null
                    && lastNote.isTied()
                    && aNote != null
                    && aNote.isTied()
                    && !aNote.firstTied()
                    )
                {
                drawTie(startOfTie, STAVE_WIDTH + 10, isStemUp(lastNote, type), g);
                // set the next tie to start a little before the new line
                startOfTie = leftMargin - 10;
                }
              }

            // set the spacingMod variable and the new number of measures
            // on the next line
            measuresOnLine = findSpacing(i + 1, staveLine, part);

            // set the number of measures on the line to be 0
            lineMeasureCount = 0;

            // set the start position for the next cstr line
            xCoordinate = leftMargin + cstrLineSpacing + spacingMod;
            bracketStart = xCoordinate - 10;

            // turn off the flag
            toNextLine = false;
          }

      }   // end of for loop

    // shrink the stave panel in size if it is too large
    while( headSpace + (lineSpacing * (staveLine + 1)) < panelHeight - pageHeight )
      {
        panelHeight -= pageHeight;
      }

    this.setSize(panelWidth, panelHeight);
   return true;

  }

/**
 * Checks to see where the tuplet bracket should end
 */
private int checkForTupletEnd(int startBeat, int tVal, int sdivs, MelodyPart part)
  {
    return (startBeat + beatValue / tVal * sdivs - 1);
  }

/**
 * Draws a note of a particular pitch and rhythm value at a yCoordinate
 * already found. Handles ties by reading whether a note is tied from a Note
 * flag, and if the note is tied and not the first note in the tie it will
 * draw a tieOver or tieUnder image from the previous tied note to the
 * current note.
 * <p>
 * This method also draws accidentals, dots, and ledger lines for a note.
 * Can only draw one accidental and one dot per note. The maximum number of
 * ledger lines above or below the stave is 5.
 *
 * @param note          the note to draw
 * @param boxed         if the note is selected or not
 * @param index         the index of the note.
 * @param g             the panel to draw the note on
 *
 * @see #drawPart(MelodyPart, Graphics)
 * @see #chooseImage(int, int, int, int, int)
 * @see #drawLedgerLine(int, int, int, Graphics)
 * @see #drawTie(int, int, Graphics)
 */
private void drawNote(Note note, boolean boxed, int i, Graphics g, Graphics2D g2,
                      int noteColor, MelodyPart part, boolean beamed, int inext, boolean stemUp)
  {
    int beatToPlace = metre[0] / 2;

    Chord c = chordProg.getCurrentChord(i);

    // get the rhythm value and pitch of the note
    int rhythmValue = note.getRhythmValue();

    int pitch = note.getDrawnPitch();

    if( note.isTied() && !note.firstTied() )
      {
        pitch = part.getPrevNote(i).getDrawnPitch();
      }

    dottedNote = false;

    isNote = isaNote(pitch);

    Image unitImage = chooseImage(pitch, rhythmValue, noteColor, beamed, stemUp);

    // find the pitch position of the note

    yCoordinate = pitchToYPos(pitch, staveLine, isNote);

    int drawWidth = xCoordinate - 5;

    // draw the ledger lines for the note

    if( isNote )
      {
        drawLedgerLine(pitch, drawWidth, staveLine, g);
      }

    // draw the accidental for the note

    switch( note.getAccidental() )
      {
        case SHARP:
            g.drawImage(sharp[noteColor], drawWidth - 9, yCoordinate, this);
            break;

        case FLAT:
            g.drawImage(flat[noteColor], drawWidth - 9, yCoordinate, this);
            break;

        case NATURAL:
            g.drawImage(natural[noteColor], drawWidth - 9, yCoordinate, this);
            break;
      }

    // draw note at the yCoordinate
    if( boxed )
      {
        selectionCacheSet(unitImage.getWidth(this) - 3);
      }

    // Draw whole rests in the centre of the measure
    if( rhythmValue >= beatValue * metre[0] && pitch == REST )
      {
        unitImage = semibreveRest;

        // Try to draw it at the construction line in the center;
        int x = cstrLines[i + beatToPlace * beatValue].getX();

        // If the construction line is being moved, then find the previous one, get its
        // spacing, and draw it at its position plus its spacing.  This isn't perfect,
        // but it looks pretty good.
        if( x == -1 )
          {
            int j = i;
            while( cstrLines[j + beatToPlace * beatValue] == null
                    || cstrLines[j + beatToPlace * beatValue].getX() == -1 )
              {
                --j;
              }

            x = cstrLines[j + beatToPlace * beatValue].getX()
                    + cstrLines[j + beatToPlace * beatValue].getSpacing();
          }

        g.drawImage(unitImage, x, yCoordinate, this);
      }
    else
      {
        g.drawImage(unitImage, drawWidth - 1, yCoordinate, this);
      }

    // if it's a dotted note, draw the dot
    if( dottedNote )
      {
        boolean dotFlag = true;
        for( int l = 0; l < lineNotes.length; l++ )
          {
            if( lineNotes[l] + 12 == pitch
                    || lineNotes[l] + 36 == pitch
                    || lineNotes[l] + 60 == pitch
                    || lineNotes[l] + 84 == pitch
                    || lineNotes[l] + 108 == pitch
                    || pitch == REST )
              {
                g.drawImage(dot, drawWidth + 1, yCoordinate - 4, this);
                dotFlag = false;
                l = lineNotes.length;
              }
          }
        if( dotFlag )
          {
            g.drawImage(dot, drawWidth + 1, yCoordinate, this);
          }
      }

    dottedNote = false;

    Note nextNote = part.getNote(inext);

    if( beamingNotes )
      {
        if( beamed
                && isNote
                && note.getRhythmValue() >= THIRTYSECOND_TRIPLET // 10 slots at present
                && sameBeat(i, inext)
                && note.getRhythmValue() == nextNote.getRhythmValue()
                && !nextNote.isDrawnRest() // Don't beam to rest
                && sameStemDirection(note, nextNote, type) )
          {
            // First note in the beamed group
            if( beamNotes == null )
              {
                // Create the beamed group

                beamNotes = new ArrayList<BeamNote>();
                beamStemUp = stemUp;
              }

            int x1Correction = beamStemUp ? beamUpXoffset : beamDownXoffset;
            int yCorrection = beamStemUp ? 0 : downStepCorrection;
            int directionalStemCorrection = beamStemUp ? upStemCorrection : downStemCorrection;

            if( !beamNotes.isEmpty() )
              {
                // Reset the x position of the last beamed note, if necessary.
                beamNotes.get(beamNotes.size()-1).setX(cstrLines[ilast].getX() + x1Correction);
              }

            // Get the x position of this note
            int x1 = cstrLines[i].getX() + x1Correction;
            int y1 = yCoordinate + yCorrection;

            // Add the current note to the beamed group
            beamNotes.add(new BeamNote(x1, y1, directionalStemCorrection, note.toLeadsheet()));
          }
        else
          {
            if( beamNotes != null && !beamNotes.isEmpty() )
              {
                int x1Correction = beamStemUp ? beamUpXoffset : beamDownXoffset;
                int yCorrection = beamStemUp ? 0 : downStepCorrection;
                int directionalStemCorrection = beamStemUp ? upStemCorrection : downStemCorrection;

                // Get the x position of this note
                int x1 = cstrLines[i].getX() + x1Correction;
                int y1 = yCoordinate + yCorrection;

                // The last note in a beamed group
                beamNotes.add(new BeamNote(x1, y1, directionalStemCorrection, note.toLeadsheet()));

                // Beam-drawing heuristics

                int numNotes = beamNotes.size();

                // Compute the maximum (lowest) and minimum (highest) y value for notes in the group
                int ymax = beamNotes.get(0).y;
                int ymin = ymax;
                for( Iterator<BeamNote> e = beamNotes.iterator(); e.hasNext(); )
                  {
                    int y = e.next().y;
                    if( y > ymax )
                      {
                        ymax = y;
                      }
                    else if( y < ymin )
                      {
                        ymin = y;
                      }
                  }

                // Get the first and last notes in the group, which are the beam ends

                BeamNote note1 = beamNotes.get(0);
                BeamNote note2 = beamNotes.get(beamNotes.size()-1);

                int xStart = note1.x;
                int xEnd = note2.x;

                int yStart = note1.y;
                int yEnd = note2.y;

                if( numNotes > 2 )
                  {
                    if( beamStemUp )
                      {
                        if( ymin < yStart && ymin < yEnd )
                          {
                            yStart = ymin;
                            yEnd = ymin;
                          }
                      }
                    else /* beamed stems down */

                      {
                        if( ymax > yStart && ymax > yEnd )
                          {
                            yStart = ymax;
                            yEnd = ymax;
                          }
                      }
                  }

                // Compute slope of beam

                float slope = ((float) (yEnd - yStart)) / (xEnd - xStart);

                // Draw stems of beamed notes.

                g.setColor(Color.black);

                for( Iterator<BeamNote> e = beamNotes.iterator(); e.hasNext(); )
                  {
                    // Draw one stem.
                    // Interpolate between yStart and yEnd.
                    // One end of the beam is determined by pitch and is stored in the BeamNote.
                    // The other end is determined by the beam itself.

                    BeamNote n = e.next();

                    int yInterp = (int) (yStart + (n.x - xStart) * slope);
                    n.drawStem(g, yInterp);
                  }

                // Draw stacked beams.

                int xs[] =
                  {
                    xStart, xStart, xEnd + 1, xEnd + 1
                  };
                int ys[] =
                  {
                    yStart, yStart + beamThickness, yEnd + beamThickness, yEnd
                  };

                int numBeams = getNumBeams(note.getRhythmValue());

                int actualBeamSpacing = beamStemUp ? beamSpacing : -beamSpacing;

                // Use g2 instead of g for antialiased beams.

                g2.setColor(Color.black);

                g2.fill(new Polygon(xs, ys, 4));

                // additional beams, e.g. for 16th notes

                for( int b = 1; b < numBeams; b++ )
                  {
                    for( int k = 0; k < ys.length; k++ )
                      {
                        ys[k] += actualBeamSpacing;
                      }
                    g2.fill(new Polygon(xs, ys, 4));
                  }

                // Finished with this beamed group
                beamNotes = null;
              }
          }
      }

    // sets the x2 position of the tie to be the draw position this note
    endOfTie = drawWidth;

    // draws a tie if necessary
    if( note.isTied() && !note.firstTied() && isNote )
      {
        drawTie(startOfTie, endOfTie, stemUp, g);
      }

    // sets the x1 position of the tie to be the current draw position
    // the next note in a tie will draw from *this* startOfTie to it's
    // current endOfTie, or drawWidth
    startOfTie = drawWidth;
  }

/*
 * Keep track of note info for beaming, where we need to know the
 * entire set for beaming before drawing.
 */
class BeamNote
{

public int x;
public int y;
public int stemCorrection;
String name;

BeamNote(int x, int y, int stemCorrection, String name)
  {
    this.x = x;
    this.y = y;
    this.stemCorrection = stemCorrection;
    this.name = name;
  }

void drawStem(Graphics g, int yInterp)
  {
    g.drawLine(x, y + stemCorrection, x, yInterp);
  }

void setX(int x)
  {
    this.x = x;
  }

}

static int getNumBeams(int rhythmValue)
  {
    if( rhythmValue <= 12 )
      {
        return 3;  // 1/32nd note, 10-
      }
    else if( rhythmValue <= 15 )
      {            // 1/32nd note,
        return 3;
      }
    else if( rhythmValue <= 20 )
      {            // 1/16th note, 6-
        return 2;
      }
    else if( rhythmValue <= 24 )
      {            // 1/16th note, 5-
        return 2;
      }
    else if( rhythmValue <= 30 )
      {            // Even 1/16th note 
        return 2;
      }
    else if( rhythmValue <= 40 )
      {            // 1/8th note, 3-
        return 1;
      }
    else if( rhythmValue <= 45 )
      {            // A dot 1/16th note 
        return 1;
      }
    else if( rhythmValue <= 60 )
      {            // Even 1/8th note 
        return 1;
      }
    else if( rhythmValue <= 80 )
      {            // 1/4 note, 3-
        return 1;
      }
    else if( rhythmValue <= 90 )
      {            // A dot 1/8th note 
        return 1;
      }
    else
      {           // Even 1/4 note 
        return 0;
      }
  }

/**
 * Test whether two indices are in the same beat
 */
boolean sameBeat(int i, int j)
  {
    return (i / BEAT) == (j / BEAT);
  }

/**
 * Returns a NoteStatistics containing the array of note
 * colors as well as statistics such as the number of red notes
 * and the amount of space they entail.
 */
public NoteStatistics collectNoteColors(MelodyPart displayPart)
{
    int number = displayPart.size();
    long redDuration = 0;
    long totalDuration = 0;
    final int RED_VALUE = 1;
    final int BLUE_VALUE = 3;
   int counts[]= {0, 0, 0, 0};
    
    MelodyPart origPart = this.getMelodyPart();
    int[] color = new int[number];
    for( int i = 0; i < number; i++ )
      {
        Note curNote = displayPart.getNote(i);
        if( curNote != null && curNote.nonRest() )
          {
          Note origNote = origPart.getNote(i);
          color[i] = determineColor(curNote, origNote, i, false, color);
          }
      }
    // Establishes coloration for tied notes.
    for( int i = 0; i < number; i++ )
      {
        Note curNote = displayPart.getNote(i);
        if( curNote != null && curNote.isTied() && !curNote.firstTied() && displayPart.
                getPrevIndex(i) >= 0 )
          {
            color[i] = color[displayPart.getPrevIndex(i)];
          }
      }

    for( int i = 0; i < number; i++ )
      {
        Note curNote = displayPart.getNote(i);
        if( curNote != null && curNote.nonRest() )
          {
           counts[color[i]]++;
          //System.out.print(color[i]+" ");
          if( color[i] == RED_VALUE )
            {
            redDuration += curNote.getRhythmValue();  
            }
          totalDuration += curNote.getRhythmValue();   
          }
      }
    //System.out.println(counts[0] + " " + counts[1] + " " + counts[2] + " " + counts[3] + " ");

    return new NoteStatistics(color, counts[RED_VALUE], redDuration, totalDuration, counts[BLUE_VALUE]);
}

/**
 * Determines the color of a given note.  Takes in the note you're coloring,
 * the original part note in that place, the index, Graphics object, a flag
 * for whether it is an approach (this is always called externally with a 
 * false boolean, and if this method determines that a previous note is an
 * approach, it will recursively called it with that prior index.  We do this
 * because determining an approach note requires a look-behind from all determined
 * chord tones), and a color array to modify.
 */
public int determineColor(Note note, 
                          Note pitchDeterminer, 
                          int i, 
                          boolean isApproach, 
                          int[] colorArray)
  {
    if( !notate.getColoration() )
      {
        return 0;
      }
    
    Chord c = chordProg.getCurrentChord(i);

    // Deal with note coloration

    int noteType;

    noteType = (c == null || pitchDeterminer == null) ? CHORD_TONE
            : c.getTypeIndex(pitchDeterminer);

    // Approach tone coloring has a higher priority than foreign tone coloring,
    // but less than chord or color tones.
    if( noteType == FOREIGN_TONE && isApproach )
      {
        noteType = APPROACH_TONE;
      }

    MelodyPart melPart = getMelodyPart();
    int prevIndex = melPart.getPrevIndex(i);
    Note prevNote = melPart.getNote(prevIndex);

    boolean approachable = (noteType == CHORD_TONE || noteType == COLOR_TONE);

    if( c != null 
     && (note != null) 
     && approachable 
     && !note.isRest() 
     && !c.getName().equals("NC")
     && prevNote != null 
     && !prevNote.isRest() 
     && !isApproach )
      {
        int diff = prevNote.getPitch() - note.getPitch();
        if( diff == 1 || diff == -1 )
          {
          colorArray[prevIndex] = determineColor(prevNote,
                                                 melPart.getNote(prevIndex),
                                                 prevIndex, 
                                                 true, 
                                                 colorArray);
          }
      }

    // Avoid re-parsing.  Also, should do some range checking on digits
    // in the user preferences.

    int noteColor = Integer.parseInt("" + noteColorString.charAt(noteType)) - 1;
    return noteColor;
  }

/**
 * Compares equality between the pitch class of a given note and a note symbol,
 * including all enharmonics of the symbol.
 * 
 * @return boolean  Is this note an instance of that symbol, or an equivalent one?
 */
private boolean compareNoteSymbolAndNotePitches(Note note, NoteSymbol ns)
  {

    int nsPitch = ns.getMIDI() % OCTAVE;
    int notePitch = note.getPitch() % OCTAVE;

    //System.out.println("comparison = " + (nsPitch == notePitch) + ", nsPitch: " + nsPitch + ", notePitch: " + notePitch +
    //        " for note: " + note.getPitchClassName());
    return nsPitch == notePitch;
  }

/**
 * Gets the y-axis position for a note of a given pitch on a particular
 * stave line. The method first finds the pitch relative to the octave
 * above middle C, and finds the spacing associated with that new pitch. It
 * then transposes the new pitch up or down octaves until the new pitch
 * equals the original pitch.
 * <p>
 * The note is also transposed vertically by <code>staveDelta</code>,
 * depending on the Stave's <code>type</code>.
 *
 * @param pitch             the pitch of the note
 * @param staveLine         the particular stave line the note is on
 * @param isNote            if the item passed was a note
 * @return int              the y-axis position of the note
 */
private int pitchToYPos(int pitch, int staveLine, boolean isNote)
  {
    // set the initial y-position
    int yPos = headSpace + (staveLine * lineSpacing);

    // if the unit is a note...
    if( isNote )
      {
        // calculate what octave the pitch is in (values 0-8)
        int octave = pitch / 12;
        // calculated a modulated pitch with respect to middle C, or C4
        int newPitch = (pitch % 12) + C4;

        // find the new, modulated pitch
        if( newPitch == c4 || newPitch == cs4 )
          {
            yPos += (5 * staveSpaceHeight);
          }
        else if( newPitch == d4 || newPitch == ds4 )
          {
            yPos += (5 * staveSpaceHeight) - (staveSpaceHeight / 2);
          }
        else if( newPitch == e4 )
          {
            yPos += (5 * staveSpaceHeight) - (1 * staveSpaceHeight);
          }
        else if( newPitch == f4 || newPitch == fs4 )
          {
            yPos +=
                    (5 * staveSpaceHeight) - (1 * staveSpaceHeight) - (staveSpaceHeight / 2);
          }
        else if( newPitch == g4 || newPitch == gs4 )
          {
            yPos += (5 * staveSpaceHeight) - (2 * staveSpaceHeight);
          }
        else if( newPitch == a4 || newPitch == as4 )
          {
            yPos +=
                    (5 * staveSpaceHeight) - (2 * staveSpaceHeight) - (staveSpaceHeight / 2);
          }
        else if( newPitch == b4 )
          {
            yPos += (5 * staveSpaceHeight) - (3 * staveSpaceHeight);
          }
        else if( newPitch == bs4 || newPitch == c5 )
          {
            yPos +=
                    (5 * staveSpaceHeight) - (3 * staveSpaceHeight) - (staveSpaceHeight / 2);
          }

        // shift the y-position depending on the note's octave
        yPos += ((5 - octave) * ((3 * staveSpaceHeight) + (staveSpaceHeight / 2)));

        // shift the note by a staveDelta value for the stave type position
        yPos -= staveDelta;
      }
    // if the unit is a rest, place it in the middle of the stave line
    else
      {
        yPos = headSpace + (staveLine * lineSpacing) + (2 * staveSpaceHeight);
      }

    // offset the height by half of the image's height
    yPos -= imageHeightOffset;

    return yPos;
  }

/**
 * Checks that the proper resolution is set for the beat. That is, that the
 * beat has at least enough construction lines to encompass all of its
 * notes and chords.
 *
 * @param beat              the current beat
 */
private int checkBeatResolution(int beat)
  {
    int old_sdivs = getSubDivs(beat);
    int new_sdivs = calcSubDivs(beat);

    //System.out.println("old = " + old_sdivs + ", new = " + new_sdivs);

    if( new_sdivs > old_sdivs || (old_sdivs % new_sdivs != 0) )
      {
        setSubDivs(beat, new_sdivs);
        return new_sdivs;
      }
    else
      {
        return old_sdivs;
      }
  }

/**
 * Finds what the spacing between notes should be for a particular index, i,
 * until the width of the notes added would be greater than the total width
 * allowed. The total width allowed is equal to the static integer <code>
 * STAVE_WIDTH</code> plus a small <code>widthOver</code> space (in pixels).
 * <p>
 * The way it is used is by calling it on a certain slot index. The function
 * will go through the part and find each note, adding it's pixel width to a
 * <code>currentWidth</code>. When the total width of the part exceeds that
 * alloted by the stave, it will set a spacing modifier, <code>spacingMod
 * </code> for each note needed to "fill" the line and return the number of
 * measures that line will have.
 * <p>
 * The automatic adjustment algorithm can be overriden if the stave is set
 * to be "locked" by having the notate variable for continuous
 * auto-adjustment set to false. The stave will then use Notate's
 * lockedMeasures array to determine the number of measures for the line.
 * <p>
 * If the previous line had only one measure, the current line findSpacing
 * is checking for is only allowed to have one measure in order to keep the
 * measures in each line as even numbered as possible.
 *
 * @param i             an index pertaining to slots, and what slot you are
 *                      currently on
 * @param part          the part to find the spacing for
 * @return int          the number of measures allocated for the line
 */
private int findSpacing(int i, int staveLine, MelodyPart part)
  {
    // System.out.println("findSpacing, i = " + i + ", staveline = " + staveLine + ", part.size() = " + part.size());

    // current width of the line

    int currentWidth = 0;

    // current amount of measures filled

    int lineMeasureCount = 0;

    // current number of construction lines

    int numCstrLines = 0;

    // the amount of space the width is allowed to go over for "scrunching"
    // the line

    int widthOver = 240;

    // Array for the width of each measure. Assumes no line will have more
    // than maxMeasuresPerLine measures

    int[] measureWidth = new int[maxMeasuresPerLine+1];

    // Array for how many cstr lines are in each measure. Assumes no line
    // will have more than maxMeasuresPerLine measures

    int[] measureCstrLines = new int[maxMeasuresPerLine+1];

    // initialize the start of each measure array to 0

    measureWidth[0] = 0;
    measureCstrLines[0] = 0;

    // initial width before the start of notes
    int initWidth;

    // Find the initial amount of pixel space on the line before a note can
    // be found. Differentiate between the line with clef and signatures
    // vs. other lines.

    if( staveLine == 0 )
      {
        initWidth =
                leftMargin + clefWidth + keySigWidth + timeSigWidth + cstrLineSpacing;
      }
    else
      {
        initWidth = leftMargin + cstrLineSpacing;
      }

    // increase the lineMeasures array if it is full
    if( staveLine > lineMeasures.length - 1 )
      {
        int[] tempLineMeasures = new int[lineMeasures.length + 1];
        for( int k = 0; k < lineMeasures.length; k++ )
          {
            tempLineMeasures[k] = lineMeasures[k];
          }
        setLineMeasures(tempLineMeasures);
      }

    if( !notate.getAutoAdjust() 
            && notate.hasLockedMeasures() 
            && staveLine < notate.getLockedMeasures().length )   // may get out of bounds index without this
      {
        int lockedMeasures = notate.getLockedMeasures()[staveLine];
        while( lineMeasureCount < lockedMeasures && i < part.size() )
          {

            // if there is a construction line
            if( i < cstrLines.length && cstrLines[i] != null )
              {
                // increase the current width, measure width, and the
                // amount of cstr lines in that measure
                measureCstrLines[lineMeasureCount]++;
                currentWidth += cstrLineSpacing;
                measureWidth[lineMeasureCount] += cstrLineSpacing;
              }

            // if the next index is a new measure
            if( (i + 1) % measureLength == 0 )
              {
                // consider the bar line to be a cstr line
                measureCstrLines[lineMeasureCount]++;

                // increase the currentWidth
                currentWidth += cstrLineSpacing;

                // the previous measure has then been filled
                lineMeasureCount++;
                if( lineMeasureCount >= maxMeasuresPerLine )
                    {
                    lineMeasureCount = lockedMeasures;
                    }
                // initialize the next units in the measure arrays
                 measureWidth[lineMeasureCount] = 0;
                measureCstrLines[lineMeasureCount] = 0;
              }

            // increase the index
            i++;
          }
      }
    // while the total width is less than the maximum allowed width or
    // there isn't one full measure
    else if( (notate.getAutoAdjust() || notate.noLockedMeasures()) )
      {
        while( ((initWidth + currentWidth < STAVE_WIDTH + widthOver) || (lineMeasureCount == 0)) && (i < part.size()) )
          {

            //rk: added.  This one conditional to prevent crash in going from 2 or fewer
            // measures in part to more.  Don't know if it is the best thing,
            // so consider it temporary.

            // if there is a construction line
            // resize the cstrLines array if its length does not equal part's length
            if( cstrLines.length != part.size() )
              {
                resizeCstrLines(part);
              }

            if( cstrLines[i] != null )
              {
                // increase the current width, measure width, and the
                // amount of cstr lines in that measure
                measureCstrLines[lineMeasureCount]++;
                currentWidth += cstrLineSpacing;
                measureWidth[lineMeasureCount] += cstrLineSpacing;
              }

            // if the next index is a new measure
            if( (i + 1) % measureLength == 0 )
              {
                // consider the bar line to be a cstr line
                measureCstrLines[lineMeasureCount]++;

                // increase the currentWidth
                currentWidth += cstrLineSpacing;

                // the previous measure has then been filled
                lineMeasureCount++;
                
                if( lineMeasureCount >= maxMeasuresPerLine )
                    {
                    lineMeasureCount = notate.getLockedMeasures()[staveLine];
                    }

                // initialize the next units in the measure arrays
                measureWidth[lineMeasureCount] = 0;
                measureCstrLines[lineMeasureCount] = 0;
              }

            // increase the index
            i++;
          }
      }


    // take off any width that is not in a measure
    if( measureCstrLines[lineMeasureCount] != 0 )
      {
        currentWidth -= measureWidth[lineMeasureCount];
      }

    // while a measure is not a multiple of 2, is a multiple of 3, and > 1  //rk was >= 1
    while( (notate.getAutoAdjust() || notate.noLockedMeasures()) && ((lineMeasureCount % 2 != 0) || (lineMeasureCount % 3 == 0)) && (lineMeasureCount > 1) )
      {
        // reduce the number of full measures
        lineMeasureCount--;
        currentWidth -= measureWidth[lineMeasureCount];

        // While this seems to be working out of order, lineMeasureCount
        // keeps track of how many measures have been filled. So if 3
        // measures have been filled, that would correspond to the array
        // index of 2, which is why we subtract lineMeasureCount first.
      }

    // if the previous line had a single measure in it
    if( notate.getAutoAdjust() && prevLineHadOne )
      {
        // make this line have a single measure as well
        for( int k = lineMeasureCount; k > 1; k-- )
          {
            // reduce the number of full measures
            lineMeasureCount--;
            // subtract the width of the last measure off
            currentWidth -= measureWidth[lineMeasureCount];
          }
        prevLineHadOne = false;
      }
    // if this line has one measure and the last line did not
    else if( !prevLineHadOne && lineMeasureCount == 1 )
      {
        prevLineHadOne = true;
      }

    // put the number of measures into the lineMeasures array
    lineMeasures[staveLine] = lineMeasureCount;

    // get the total number of construction lines in the remaining measures
    if( lineMeasureCount != 0 )
      {
        for( int k = 0; k < lineMeasureCount; k++ )
          {
            numCstrLines += measureCstrLines[k];
          }
      }
    else
      {
        numCstrLines = measureCstrLines[0];
      }

    // take off one cstr line for the last bar line
    numCstrLines--;

    // get the remaining width
    int remainderWidth = STAVE_WIDTH - (initWidth + currentWidth);

    // if the score isn't complete and there are still some remaining notes,
    // don't modify their spacing
    if( (i == part.size() && lineMeasureCount == 0) || numCstrLines == 0 )
      {
        spacingMod = 0;
      }
    // if the cstr lines will overextend too far cut the spacing mod back
    else if( remainderWidth < 0 && (((remainderWidth / numCstrLines) + cstrLineSpacing) * (numCstrLines - 2)) + initWidth > STAVE_WIDTH )
      {
        spacingMod = (remainderWidth / numCstrLines) - 1;
      }
    // otherwise just divide the remainder width up
    else
      {
        spacingMod = remainderWidth / numCstrLines;
      }

    // return how many measures this line will need
    return lineMeasureCount;
  }

/**
 * Draws a stave with numPitchLines pitch lines, where staveLine corresponds
 * to the number of the stave lineto be drawn. The <code>lineSpacing</code>
 * is the space inbetween staves, and <code>staveSpaceHeight</code> is the
 * space inbetween stave pitch lines.
 *
 * @param staveLine     stave number corresponding to the wrap-around line
 * @param x1            first x-coord where drawing the stave begins
 * @param x2            second x-coord where drawing the stave ends
 * @param g             panel to draw onto
 */
private void drawStave(int staveLine, int x1, int x2, Graphics g)
  {
    for( int i = (staveLine * lineSpacing); i < ((staveLine * lineSpacing)
            + (numPitchLines * staveSpaceHeight));
            i += staveSpaceHeight )
      {

        // checks to see if the line shouldn't be drawn for middle C in a
        // grand staff
        if( i != (staveLine * lineSpacing) + (5 * staveSpaceHeight) )
          {
            g.drawLine(x1, headSpace + i, x2, headSpace + i);
          }
      }
  }

/**
 * White-outs a stave starting from an initial x position and ending at a
 * final x position. This eliminates any stave space unused from the
 * drawing. Assumes that the Stave's background color is white.
 *
 * @param staveLine     stave number corresponding to the wrap-around line
 * @param x1            first x-coord where drawing the stave begins
 * @param x2            second x-coord where drawing the stave ends
 * @param g             panel to draw onto
 */
private void whiteOutStave(int staveLine, int x1, int x2, Graphics g)
  {
    g.setColor(Color.white);
    for( int i = (staveLine * lineSpacing); i < ((staveLine * lineSpacing) + (numPitchLines * staveSpaceHeight));
            i += staveSpaceHeight )
      {

        // checks to see if the line shouldn't be drawn for middle C in a
        // grand staff
        if( i != (staveLine * lineSpacing) + (5 * staveSpaceHeight) )
          {
            g.drawLine(x1, headSpace + i, x2, headSpace + i);
          }
      }
    g.setColor(Color.black);
  }

/**
 * Draws the clef or clefs corresponding to the type of stave.
 * Assumes only a treble or bass clef will be drawn, or both for a grand
 * stave.
 *
 * @param g             the panel to draw onto
 */
private void drawClef(Graphics g)
  {
    if( type == StaveType.TREBLE )
      {
        g.drawImage(trebleClef, leftMargin + 7, headSpace - 16, this);
      }
    else if( type == StaveType.BASS )
      {
        g.drawImage(bassClef, leftMargin + 7, headSpace - 10, this);
      }
    else if( type == StaveType.GRAND )
      {
        g.drawImage(trebleClef, leftMargin + 7, headSpace - 16, this);
        g.drawImage(bassClef, leftMargin + 7,
                    headSpace + (staveSpaceHeight * barHeightMod) - 10, this);
      }
  }

/**
 * Draws the key signature for the specified stave. A negative key signature
 * corresponds to flats, while a positive key signature corresponds to
 * sharps.
 *
 * TODO: Clean this code to not use the notePosOffset[] voodoo from JMusic.
 *
 * @param keyOffset     pixel spacing allocated for keySigWidth
 * @param g             the panel to draw onto
 * @return int          the new keyOffset increased if there are any sharps
 *                      or flats
 */
private int drawKeySig(int keyOffset, Graphics g)
  {
    int keyOffsetInc = 10;

    // draw if sharps
    if( keySignature > 0 && keySignature < 8 )
      {
        for( int ks = 0; ks < keySignature; ks++ )
          {
            // calculate position
            int keyAccidentalPosition =
                    notePosOffset[sharps[ks] % 12] + headSpace - 16 + ((5 - sharps[ks] / 12) * 24) + ((6 - sharps[ks] / 12) * 4);

            if( type == StaveType.TREBLE )
              {
                g.drawImage(sharp[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition, this);
              }
            else if( type == StaveType.BASS )
              {
                g.drawImage(sharp[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition + staveSpaceHeight, this);
              }
            else if( type == StaveType.GRAND )
              {
                g.drawImage(sharp[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition, this);
                g.drawImage(sharp[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition + staveSpaceHeight + (staveSpaceHeight * barHeightMod),
                            this);
              }

            // indent position
            keyOffset += keyOffsetInc;
          }
      }
    // draw if flats
    else if( keySignature < 0 && keySignature > -8 )
      {
        for( int ks = 0; ks < Math.abs(keySignature); ks++ )
          {
            // calculate position
            int keyAccidentalPosition =
                    notePosOffset[flats[ks] % 12] + headSpace - 16 + ((5 - flats[ks] / 12) * 24) + ((6 - flats[ks] / 12) * 4);

            if( type == StaveType.TREBLE )
              {
                g.drawImage(flat[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition, this);
              }
            else if( type == StaveType.BASS )
              {
                g.drawImage(flat[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition + staveSpaceHeight, this);
              }
            else if( type == StaveType.GRAND )
              {
                g.drawImage(flat[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition, this);
                g.drawImage(flat[0], leftMargin + clefWidth + keyOffset,
                            keyAccidentalPosition + staveSpaceHeight + (staveSpaceHeight * barHeightMod),
                            this);
              }

            // indent position
            keyOffset += keyOffsetInc;
          }
      }

    keySigWidth = keyOffset;  // this formerly was only in the first branch, which seems like a mistake

    return keyOffset;
  }

/**
 * Draws the time signature for the required stave and returns the time
 * signature width. The bottom number is always '4', while the top can be
 * between '1' and '12'.
 *
 * @param g             the panel to draw onto
 * @return int          the width of the time signature
 */
private int drawTimeSig(Graphics g)
  {
    Image[] numbers =
      {
        one, two, three, four, five, six, seven, eight,
        nine, zero
      };

    if( metre[0] <= 12 && metre[0] > 0 )
      {
        // If the top number is 10 or greater, we need to draw the digits individually
        if( metre[0] > 9 )
          {
            // Top number
            g.drawImage(numbers[0], leftMargin + clefWidth + keySigWidth,
                        headSpace + 1, this);
            // Need to get zero separately
            if( metre[0] == 10 )
              {
                g.drawImage(numbers[9], leftMargin + clefWidth + keySigWidth + 14,
                            headSpace + 1, this);
              }
            else
              {
                g.drawImage(numbers[metre[0] - 11],
                            leftMargin + clefWidth + keySigWidth + 14,
                            headSpace + 1, this);
              }

            // Bottom number
            g.drawImage(numbers[metre[1] - 1],
                        leftMargin + clefWidth + keySigWidth + 7,
                        headSpace + (staveSpaceHeight * 2) + 1, this);
          }
        else
          {
            // top number (for a treble or bass stave)
            g.drawImage(numbers[metre[0] - 1], leftMargin + clefWidth + keySigWidth,
                        headSpace + 1, this);

            //bottom number (for a treble or bass stave)
            g.drawImage(numbers[metre[1] - 1], leftMargin + clefWidth + keySigWidth,
                        headSpace + (staveSpaceHeight * 2) + 1, this);
          }

        // if it is a grand stave, the second stave time signature needs to be
        // drawn as well
        if( type == StaveType.GRAND )
          {
            if( metre[0] > 9 )
              {
                // Top number
                g.drawImage(numbers[0], leftMargin + clefWidth + keySigWidth,
                            headSpace + (staveSpaceHeight * 6) + 1, this);
                g.drawImage(numbers[metre[0] - 11],
                            leftMargin + clefWidth + keySigWidth + 14,
                            headSpace + (staveSpaceHeight * 6) + 1, this);

                // Bottom number
                g.drawImage(numbers[metre[1] - 1],
                            leftMargin + clefWidth + keySigWidth + 7,
                            headSpace + (staveSpaceHeight * 8) + 1, this);
              }
            else
              {
                g.drawImage(numbers[metre[0] - 1], leftMargin + clefWidth + keySigWidth,
                            headSpace + (staveSpaceHeight * 6) + 1,
                            this);
                g.drawImage(numbers[metre[1] - 1], leftMargin + clefWidth + keySigWidth,
                            headSpace + (staveSpaceHeight * 8) + 1, this);
              }
          }
      }

    return maxTimeSigWidth;
  }

/**
 * Draws a bar line at a given x-position and a given stave line. Draws the
 * bar Line 2 pixels wide.
 *
 * @param x             int for the x position of the bar
 * @param staveLine     int for the stave number corresponding to the
 *                      wrap-around line
 * @param g             the panel to draw onto
 */
private void drawBarLine(int x, int staveLine, Graphics g)
  {
      for( int i = 0; i < 2; i++ )
        {
          g.drawLine(x + i, headSpace + (staveLine * lineSpacing),
                     x + i,
                     headSpace + ((numPitchLines - 1) * staveSpaceHeight) + (staveLine * lineSpacing));
        }

      if( doubleBar )
        {
          for( int i = 0; i < 2; i++ )
            {
              g.drawLine(x + i - DOUBLE_BAR_OFFSET, headSpace + (staveLine * lineSpacing),
                         x + i - DOUBLE_BAR_OFFSET,
                         headSpace + ((numPitchLines - 1) * staveSpaceHeight) + (staveLine * lineSpacing));
            }
          
          doubleBar = false;
        }
  }

/**
 * Draws a construction line at a given x-position and a given stave line.
 * Colors the construction with the given color, and draws the construction
 * line <code>thickness</code> pixels wide.
 *
 * @param x             int for the x position of the bar
 * @param staveLine     int for the stave number corresponding to the
 *                      wrap-around line
 * @param color         the color of the particular construction line
 * @param thickness     how thick the construction line should appear
 * @param g             the panel to draw onto
 */
private void drawCstrLine(int x, int staveLine, Color color, int thickness,
                          Graphics g)
  {
    g.setColor(color);
    for( int i = 0; i < thickness; i++ )
      {
        g.drawLine(x + i, headSpace + (staveLine * lineSpacing),
                   x + i,
                   headSpace + (staveLine * lineSpacing) + ((numPitchLines + 1) * staveSpaceHeight));
      }
    g.setColor(Color.black);
  }

private Rectangle playLine = new Rectangle(0, 0, 3, 0);

private void drawPlayLine(int x, int staveLine, Color color, int thickness,
                          Graphics g, boolean showLine)
  {
    g.setColor(color);
    int y1 = headSpace + (staveLine * lineSpacing) - 20;
    int y2 =
            headSpace + (staveLine * lineSpacing) + ((numPitchLines + 1) * staveSpaceHeight);
    if( showLine )
      {
        for( int i = 0; i < thickness; i++ )
          {
            g.drawLine(x + i, y1, x + i, y2);
          }
      }

    playLine.y = headSpace + staveLine * lineSpacing - lineOffset;
    playLine.x = x;
    playLine.height = ((numPitchLines + 1) * staveSpaceHeight) + 20;
    g.setColor(Color.black);
  }

public Rectangle getPlayLine()
  {
    return playLine;
  }

/**
 * Draws a beatBracket image under a beat given a start and an end x-axis
 * position. The bracket is drawn 20 pixels below the last pitch line, which
 * is assumed to be enough room for the user to enter notes.
 *
 * @param subdivs       how many subdivisions are in the beat
 * @param start         int for the starting x-axis position, in pixels
 * @param end           int for the ending x-axis position, in pixels
 * @param staveLine     int for the stave number currently on
 * @param g             the panel to draw onto
 */
private void drawBeatBracket(int subdivs, int start, int end, int staveLine,
                             Graphics g)
  {

    int bracketHeight = headSpace + (staveLine * lineSpacing)
            + (numPitchLines * staveSpaceHeight) + 20;

    g.drawLine(start, bracketHeight, end, bracketHeight);

    g.drawLine(start, bracketHeight - beatBracketHeight, start, bracketHeight);
    g.drawLine(end, bracketHeight - beatBracketHeight, end, bracketHeight);

    g.drawString("" + subdivs, start + ((end - start) / 2) - 4,
                 bracketHeight + 10);
  }

/**
 * Draws a bracket image for an n-tuplet given a start and an end x-axis
 * position. The bracket will increase in height if any note in the tuplet
 * is taller than the bracket's position, so no notes will be overlapped by
 * the bracket.
 *
 * @param n             the number of subdivs in the tuplet
 * @param start         int for the starting x-axis position, in pixels
 * @param end           int for the ending x-axis position, in pixels
 * @param staveLine     int for the stave number currently on
 * @param g             the panel to draw onto
 */
private void drawTupletBracket(int n,
                               int start,
                               int end,
                               int staveLine,
                               Graphics g)
  {
    int bracketHeight = (headSpace + (staveLine * lineSpacing) - 20);

    // If the tuplet bracket crosses multiple lines
    if( end < start )
      {
        int topEnd = leftMargin + STAVE_WIDTH + 10;

        int previousBracketHeight =
                (headSpace + ((staveLine - 1) * lineSpacing) - 20);

        // Draw the first half of the bracket
        int width = (topEnd - start) / 2 - 10;
        int x1 = start + tupletBracketInset;
        int x2 = x1 + width;
        int x3 = x2 + 15;
        int x4 = x3 + 34;
        int y1 = previousBracketHeight;
        int y2 = y1 + tupletBracketHeight;

        g.drawLine(x1, y1, x2, y1); // horizontal left
        g.drawLine(x1, y1, x1, y2); // vertical left
        g.drawLine(x3, y1, x4, y1); // small extension, connecting to next line

        g.drawString("" + n, start + ((topEnd - start) / 2) - 4,
                     previousBracketHeight + 5);

        start = leftMargin - 15;

        bracketHeight = (headSpace + (staveLine * lineSpacing) - 20);
        if( highestYPos < bracketHeight )
          {
            bracketHeight = highestYPos + 8;
          }
        // Draw the second half of the bracket
        x4 = end - tupletBracketInset;
        x3 = start + 30;
        y1 = bracketHeight;
        y2 = y1 + tupletBracketHeight;

        g.drawLine(x3, y1, x4, y1); // horizontal right
        g.drawLine(x4, y1, x4, y2); // vertical right
      }
    else
      {
        // Bracket is all on one line.
        if( highestYPos < bracketHeight )
          {
            bracketHeight = highestYPos + 8;
          }

        // Draw bracket

        int width = (end - start) / 2 - 10;
        int x1 = start + tupletBracketInset;
        int x2 = x1 + width;
        int x4 = end - tupletBracketInset;
        int x3 = x4 - width;
        int y1 = bracketHeight;
        int y2 = y1 + tupletBracketHeight;

        g.drawLine(x1, y1, x2, y1); // horizontal left
        g.drawLine(x1, y1, x1, y2); // vertical left
        g.drawLine(x3, y1, x4, y1); // horizontal right
        g.drawLine(x4, y1, x4, y2); // horizontal left

        g.drawString("" + n, start + ((end - start) / 2) - 4, bracketHeight + 5);
      }
  }

/**
 *  Updates the the temporary ledger lines associated with the note cursor
 * @param pitch         pitch associated with the cursor
 * @param x             where the pitch is
 * @param staveLine     the line we're on
 * @param g             the graphics we're writing to
 */
public void updateTempLedgerLines(int pitch, int x, int staveLine, Graphics g)
{
    StaveActionHandler handler = getActionHandler();
    int oldx = handler.oldx;
    if (Math.abs(oldx - x) > 2)
    {
        repaint(); 
        handler.oldx = x;
    }
    drawLedgerLine(pitch, getActionHandler().oldx, staveLine, g);
}

/**
 * Draws a ledger line for given integers for x-position, stave number, and
 * beat position offset. Assumes that pitch > -1 and rhythmValue != 0.0.
 * Only 5 ledger lines can be drawn above or below a given Stave type.
 *
 * @param pitch         the pitch of the note
 * @param x             the x position of the ledger line
 * @param staveLine     the stave number corresponding to the wrap-around
 *                      line
 * @param g             the panel to draw onto
 */
private void drawLedgerLine(int pitch, int x, int staveLine, Graphics g)
  {

    if( null != type )
      // Checks for the appropriate stave and draws the ledger lines
    switch (type) {
        case TREBLE:
            // draw ledger lines down
            for( int i = 0; i < 5; i++ )
            {
                if( pitch <= treblePitches[i] )
                {
                    g.drawLine(x - 3,
                            headSpace + trebleOffset[i] + (staveLine * lineSpacing),
                            x + 12,
                            headSpace + trebleOffset[i] + (staveLine * lineSpacing));
                }
            } // draw ledger lines up
            for( int i = 5; i < 10; i++ )
            {
                if( pitch >= treblePitches[i] )
                {
                    g.drawLine(x - 3,
                            headSpace + trebleOffset[i] + (staveLine * lineSpacing),
                            x + 12,
                            headSpace + trebleOffset[i] + (staveLine * lineSpacing));
                }
            } break;
        case BASS:
            // draw ledger lines down
            for( int i = 0; i < 5; i++ )
            {
                if( pitch <= bassPitches[i] )
                {
                    g.drawLine(x - 3, headSpace + bassOffset[i]
                            + +(staveLine * lineSpacing), x + 12, headSpace
                                    + +bassOffset[i] + (staveLine * lineSpacing));
                }
            } // draw ledger lines up
            for( int i = 5; i < 10; i++ )
            {
                if( pitch >= bassPitches[i] )
                {
                    g.drawLine(x - 3,
                            headSpace + bassOffset[i] + (staveLine * lineSpacing), x + 12,
                            headSpace + bassOffset[i] + (staveLine * lineSpacing));
                }
            } break;
        case GRAND:
            // middle C
            if( pitch == 60 || pitch == 61 )
            {
                g.drawLine(x - 3,
                        headSpace + (5 * staveSpaceHeight) + (staveLine * lineSpacing),
                        x + 12,
                        headSpace + (5 * staveSpaceHeight) + (staveLine * lineSpacing));
            } // draw ledger lines down
            for( int i = 0; i < 5; i++ )
            {
                if( pitch <= grandPitches[i] )
                {
                    g.drawLine(x - 3,
                            headSpace + grandOffset[i] + (staveLine * lineSpacing), x + 12,
                            headSpace + grandOffset[i] + (staveLine * lineSpacing));
                }
            } // draw ledger lines up
            for( int i = 5; i < 10; i++ )
            {
                if( pitch >= treblePitches[i] )
                {
                    g.drawLine(x - 3,
                            headSpace + grandOffset[i] + (staveLine * lineSpacing), x + 12,
                            headSpace + grandOffset[i] + (staveLine * lineSpacing));
                }
            } break;
        default:
            break;
    }
  }

/**
 * Draws a tie from startTie to endTie. Assumes a valid start and ending
 * position have been given.
 *
 * @param startOfTie        the x-position for where the tie should begin
 * @param endOfTie          the x-position for where the tie should end
 * @param g                 the panel to draw onto
 */
private void drawTie(int startOfTie, int endOfTie, boolean stemUp, Graphics g)
  {
    // calculate the y-axis position for the note based on the y coordinate
    // I had to make yCoordinate a private class variable for this to work
    int yPosition = yCoordinate + 15;

    // if the note has an upward tail
    if( stemUp )
      {
        g.drawImage(tieUnder,
                    startOfTie + 3,
                    yPosition + 20,
                    endOfTie + 7,
                    yPosition + 20 + tieUnder.getHeight(this),
                    0, 0, tieUnder.getWidth(this),
                    tieUnder.getHeight(this),
                    this);
      }
    // if the note has a downward tail
    else
      {
        g.drawImage(tieOver,
                    startOfTie + 3,
                    yPosition - 16,
                    endOfTie + 7,
                    yPosition - 16 + tieOver.getHeight(this),
                    0, 0, tieOver.getWidth(this),
                    tieOver.getHeight(this),
                    this);
      }
  }

boolean isStemUp(Note note, StaveType type)
  {
    return isStemUp(note.getPitch(), type);
  }

boolean isStemUp(int pitch, StaveType type)
  {
    // initialize for GRAND:

    int upPitch1 = 71;
    int upPitch2 = 50;
    int downPitch = 60;

    switch( type )
      {
        default:
        // FIX this case when possible

        case TREBLE:
            return pitch < upPitch1;

        case BASS:
            return pitch < upPitch2;

        case GRAND:
            return (pitch < upPitch1 && pitch >= downPitch) || pitch < upPitch2;
      }
  }

boolean sameStemDirection(Note note1, Note note2, StaveType type)
  {
    return sameStemDirection(note1.getPitch(), note2.getPitch(), type);
  }

boolean sameStemDirection(int pitch1, int pitch2, StaveType type)
  {
    return pitch1 - pitch2 <= 2
            || pitch2 - pitch1 <= 2;
  }

boolean isaNote(int pitch)
  {
    return pitch != Note.REST;
  }

/**
 * Chooses an image for a note of a particular pitch and rhythm value. Also
 * sets some variables such as if the image has a dot, if it is part of an
 * n-tuplet, and how many notes are in that n-tuplet. Chooses if the image
 * has a tail up or a tail down depending on the upPitch's and downPitch's
 * given.
 *
 * @param pitch             the pitch of the note
 * @param rhythmValue       the rhythmValue of the note
 * @param upPitch1          the minimum pitch where the note will have a
 *                          stem up
 * @param downPitch         the maximum pitch where the note will have a
 *                          stem down
 * @param upPitch2          another maximum pitch for a stem up (used for a
 *                          grand stave)
 *
 * @see #drawNote(Note, boolean, Graphics)
 */
private Image chooseImage(int pitch, int rhythmValue, int color, boolean beamed, boolean stemUp)
  {

    Image unitImage;

    // Is the "note" a rest?
    if( !isaNote(pitch) )
      {
        if( rhythmValue < 10 )
          {            // rest too small for normal rendering
            unitImage = smallbox;
          }
        else if( rhythmValue <= 10 )
          {            // 1/32nd note rest, 12-
            unitImage = demisemiquaverRest;
          }
        else if( rhythmValue <= 12 )
          {            // 1/32nd note rest, 10-
            unitImage = demisemiquaverRest;
          }
        else if( rhythmValue <= 15 )
          {            // 1/32nd note rest,
            unitImage = demisemiquaverRest;
          }
        else if( rhythmValue <= 20 )
          {            // 1/16th note rest, 6-
            unitImage = semiquaverRest;
          }
        else if( rhythmValue <= 24 )
          {            // 1/16th note rest, 5-
            unitImage = semiquaverRest;
          }
        else if( rhythmValue <= 30 )
          {            // Even 1/16th note rest
            unitImage = semiquaverRest;
          }
        else if( rhythmValue <= 40 )
          {            // 1/8th note rest, 3-
            unitImage = quaverRest;
          }
        else if( rhythmValue <= 45 )
          {            // A dot 1/16th note rest
            unitImage = semiquaverRest;
            dottedNote = true;
          }
        else if( rhythmValue <= 48 )
          {            // An eighth-note quintuplet
            unitImage = quaverRest;
          }
        else if( rhythmValue <= 60 )
          {            // Even 1/8th note rest
            unitImage = quaverRest;
          }
        else if( rhythmValue <= 80 )
          {            // 1/4 note rest, 3-
            unitImage = crotchetRest;
          }
        else if( rhythmValue <= 90 )
          {            // A dot 1/8th note rest
            unitImage = quaverRest;
            dottedNote = true;
          }
        else if( rhythmValue <= 96 )
          {            // A quarter note quintuplet
            unitImage = crotchetRest;
          }
        else if( rhythmValue <= 120 )
          {           // Even 1/4 note rest
            unitImage = crotchetRest;
          }
        else if( rhythmValue <= 160 )
          {           // A 1/2 note rest, 3-
            unitImage = minimRest;
          }
        else if( rhythmValue <= 180 )
          {           // A dotted 1/4 note rest
            unitImage = crotchetRest;
            dottedNote = true;
          }
        else if( rhythmValue <= 240 )
          {           // An even 1/2 note rest
            unitImage = minimRest;
          }
        else if( rhythmValue <= 360 )
          {           // A dotted 1/2 note rest
            unitImage = minimRest;
            dottedNote = true;
          }
        else
          {           // A whole rest
            unitImage = semibreveRest;
          }
      }
    else
      {
        // a note rather than a rest
        if( rhythmValue <= 0 )
          {
            unitImage = delete;
          }
        else if( beamed )
          {
            // A beamed note doesn't get any flags, just a stem
            unitImage = filledNoteHead[color];
          }
        else if( stemUp )
          {
            // stem of the note is up
            if( rhythmValue < 10 )
              {        // very small note value
                unitImage = filledNoteHead[color];
              }
            else if( rhythmValue <= 10 )
              {        // 1/32nd note, 12-
                unitImage = demisemiquaverUp[color];
              }
            else if( rhythmValue <= 12 )
              {        // 1/32nd note, 10-
                unitImage = demisemiquaverUp[color];
              }
            else if( rhythmValue <= 15 )
              {        // 1/32nd note
                unitImage = demisemiquaverUp[color];
              }
            else if( rhythmValue <= 20 )
              {        // 1/16th note, 6-
                unitImage = semiquaverUp[color];
              }
            else if( rhythmValue <= 24 )
              {        // 1/16th note, 5-
                unitImage = semiquaverUp[color];
              }
            else if( rhythmValue <= 30 )
              {        // An even 1/16th note
                unitImage = semiquaverUp[color];
              }
            else if( rhythmValue <= 40 )
              {        // 1/8th note, 3-
                unitImage = quaverUp[color];
              }
            else if( rhythmValue <= 45 )
              {        // A dotted 1/16th note
                unitImage = semiquaverUp[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 48 )
              {        // An eighth-note quint
                unitImage = quaverUp[color];
              }
            else if( rhythmValue <= 60 )
              {        // An even 1/8th note
                unitImage = quaverUp[color];
              }
            else if( rhythmValue <= 80 )
              {        // 1/4 note, 3-
                unitImage = crotchetUp[color];
              }
            else if( rhythmValue <= 90 )
              {        // A dotted 1/8th note
                unitImage = quaverUp[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 96 )
              {        // A quarter-note quint
                unitImage = crotchetUp[color];
              }
            else if( rhythmValue <= 120 )
              {       // An even 1/4 note
                unitImage = crotchetUp[color];
              }
            else if( rhythmValue <= 160 )
              {       // A 1/2 note, 3-
                unitImage = minimUp[color];
              }
            else if( rhythmValue <= 180 )
              {       // A dotted 1/4 note
                unitImage = crotchetUp[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 240 )
              {       // An even 1/2 note
                unitImage = minimUp[color];
              }
            else if( rhythmValue <= 360 )
              {       // A dotted 1/2 note
                unitImage = minimUp[color];
                dottedNote = true;
              }
            else
              {                               // A whole note
                unitImage = semibreve[color];
              }
          }
        else
          {
            // stem of the note is down
            if( rhythmValue < 10 )
              {        // very small note value
                unitImage = filledNoteHead[color];
              }
            else if( rhythmValue <= 10 )
              {        // 1/32nd note, 12-
                unitImage = demisemiquaverDown[color];
              }
            else if( rhythmValue <= 12 )
              {        // 1/32nd note, 10-
                unitImage = demisemiquaverDown[color];
              }
            else if( rhythmValue <= 15 )
              {        // 1/32nd note
                unitImage = demisemiquaverDown[color];
              }
            else if( rhythmValue <= 20 )
              {        // 1/16th note, 6-
                unitImage = semiquaverDown[color];
              }
            else if( rhythmValue <= 24 )
              {        // 1/16th note quint, 5-
                unitImage = semiquaverDown[color];
              }
            else if( rhythmValue <= 30 )
              {        // An even 1/16th note
                unitImage = semiquaverDown[color];
              }
            else if( rhythmValue <= 40 )
              {        // 1/8th note, 3-
                unitImage = quaverDown[color];
              }
            else if( rhythmValue <= 45 )
              {        // A dotted 1/16th note
                unitImage = semiquaverDown[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 48 )
              {        // An eighth-note quint
                unitImage = quaverDown[color];
              }
            else if( rhythmValue <= 60 )
              {        // An even 1/8th note
                unitImage = quaverDown[color];
              }
            else if( rhythmValue <= 80 )
              {        // 1/4 note, 3-
                unitImage = crotchetDown[color];
              }
            else if( rhythmValue <= 90 )
              {        // A dotted 1/8th note
                unitImage = quaverDown[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 96 )
              {        // An quarter-note quint
                unitImage = crotchetDown[color];
              }
            else if( rhythmValue <= 120 )
              {       // An even 1/4 note
                unitImage = crotchetDown[color];
              }
            else if( rhythmValue <= 160 )
              {       // A 1/2 note, 3-
                unitImage = minimDown[color];
              }
            else if( rhythmValue <= 180 )
              {       // A dotted 1/4 note
                unitImage = crotchetDown[color];
                dottedNote = true;
              }
            else if( rhythmValue <= 240 )
              {       // An even 1/2 note
                unitImage = minimDown[color];
              }
            else if( rhythmValue <= 360 )
              {       // A dotted 1/2 note
                unitImage = minimDown[color];
                dottedNote = true;
              }
            else
              {                               // A whole note
                unitImage = semibreve[color];
              }
          }
      }

    return unitImage;
  }  // end chooseImage

public void unselectAll()
  {
    Trace.log(2, "unselect all");
    setSelection(OUT_OF_BOUNDS);
  }

void redoAdvice()
  {
    getActionHandler().redoAdvice(getSelectionStart());
  }

void transposeMelodyHarmonically()
  {
    Trace.log(2, "applying smart transpose");
    rectifySelection(getSelectionStart(),
                 getSelectionEnd(), false, false);
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose harmonically");
  }

void transposeMelodyUpHarmonically()
  {
    Trace.log(2, "applying harmonic transpose up");
    rectifySelection(getSelectionStart(), getSelectionEndNote(), true, true);
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose up harmonically");
  }

void transposeMelodyDownHarmonically()
  {
    Trace.log(2, "applying harmonic transpose down");
    rectifySelection(getSelectionStart(),
                 getSelectionEndNote(), true, false);
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose down harmonically");
  }

void transposeMelodyUpSemitone()
  {
    Trace.log(2, "transpose up semitone");
    shiftPitch(getSelectionStart(),
               getSelectionEndNote(),
               true, // up
               false); // not octave
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose up semitone");
  }

void transposeMelodyDownSemitone()
  {
    Trace.log(2, "transpose down semitone");
    shiftPitch(getSelectionStart(),
               getSelectionEndNote(),
               false, // down
               false); // not octave
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose down semitone");
  }

void transposeMelodyUpOctave()
  {
    Trace.log(2, "transpose up octave");
    shiftPitch(getSelectionStart(),
               getSelectionEndNote(),
               true, // up
               true); // octave
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose up octave");
  }

void transposeMelodyDownOctave()
  {
    Trace.log(2, "transpose down octave");
    shiftPitch(getSelectionStart(),
               getSelectionEndNote(),
               false, // down
               true); // octave
    notate.noCountIn();
    playSelection(false, 0, PlayScoreCommand.USEDRUMS, "transpose down octave");
  }

void transposeChordsUpSemitone()
  {
    Trace.log(2, "transpose chords up semitone");
    shiftChords(getSelectionStart(),
                getSelectionEnd(),
                true);  // up
  }

void transposeChordsDownSemitone()
  {
    Trace.log(2, "transpose chords down semitone");
    shiftChords(getSelectionStart(),
                getSelectionEnd(),
                false);  // down
  }

/**
 * Shifts the pitch of a note or notes up by a certain amount of half steps
 *
 * @param startIndex       starting index of the selection of notes
 * @param endIndex         ending index of the selection of notes
 * @param keyPressed       the key that was pressed
 * @param octave           if it should be shifted an octave or not
 */
public void shiftPitch(int startIndex, int endIndex, boolean up,
                       boolean octave)
  {
    int shift;

    if( up )
      {
        // shifting pitch up
        if( octave )
          {
            shift = 12;
          }
        else
          {
            shift = 1;
          }
      }
    else
      {
        if( octave )
          {
            shift = -12;
          }
        else
          {
            shift = -1;
          }
      }

    notate.cm.execute(
            new ShiftPitchesCommand(shift,
                                    getMelodyPart(),
                                    startIndex,
                                    endIndex,
                                    getMinPitch(),
                                    getMaxPitch(),
                                    getKeySignature()));

    if( !octave && startIndex == endIndex )
      {
        redoAdvice();
      }

    repaint();
  }

public void rectifySelection(int startIndex, int endIndex, boolean directional, boolean direction)
  {
    boolean [] options = notate.getLickgenFrame().getRectifyOptions();
    MelodyPart melodyPart = getMelodyPart();
    notate.cm.execute(
            new RectifyPitchesCommand(melodyPart,
                                      startIndex,
                                      endIndex,
                                      getChordProg(),
                                      directional,
                                      direction,
                                      options[0], 
                                      options[1], 
                                      options[2],
                                      false));

    if( startIndex == endIndex )
      {
        redoAdvice();
      }

    repaint();
  }

public void mergeSelection(int startIndex, int endIndex){
    notate.cm.execute(new MergeSameNotesCommand(getMelodyPart(), startIndex, endIndex));
    if( startIndex == endIndex )
      {
        redoAdvice();
      }
    repaint();
}



public void play(int startAt)
  {
    playSelection(startAt, notate.score.getTotalLength()-1, notate.getLoopCount(), true, "from Stave play/1");
  }


public void playSelection(boolean playToEndOfChorus, int loopCount, boolean useDrums, String message){
    playSelection(playToEndOfChorus, loopCount, useDrums, message, false);  // WAS true before 23 May 2017
}
/**
 * Plays from the start of the current selection through to the end of chorus.
 *
     * @param playToEndOfChorus
     * @param loopCount
     * @param useDrums
     * @param message
 */
public void playSelection(boolean playToEndOfChorus, int loopCount, boolean useDrums, String message, boolean shouldHotSwap)
  {

    //System.out.println("\nStave: playSelection, playToEndOfChorus = " + playToEndOfChorus);

    if( !somethingSelected() )
      {
        return;
      }
    //System.out.println("\nplaySelection, toEND = " + playToEndOfChorus + " loopCount = " + loopCount + ", useDrums = " + useDrums + " " + message);
    //debug System.out.println("playing " + message);
    
    int partSize = getChordProg().getSize();

    int startIndex = getSelectionStart();
    int stopIndex = (playToEndOfChorus ? (partSize - 1) : getSelectionEndNote(selectionEnd));
    
    // Attempt at fixing the stopIndex so that 
    // looping is on a multiple number of whole beats
    
    if( loopCount != 1 && stopIndex % BEAT != 0 )
      {
        stopIndex = BEAT*(1 + stopIndex/BEAT);
      }

    playSelection(startIndex, stopIndex, loopCount, useDrums, "from Stave playSelection/4", shouldHotSwap);
  }

public void playSelection(int startIndex, int stopIndex, int loopCount, boolean useDrums, String message)
  {
      playSelection(startIndex, stopIndex, loopCount, useDrums, message, false);
  }
public void playSelection(int startIndex, int stopIndex, int loopCount, boolean useDrums, String message, boolean shouldHotSwap)
  {
    if( startIndex != 0 || !notate.getFirstChorus() )
      {
        notate.noCountIn();
      }
    
    notate.chordVolumeChanged();

    //System.out.println("*** Play Selection from startIndex = " + startIndex + " to stopIndex = " + stopIndex + ", loopCount = " + loopCount + " " + message);

    int partSize = getChordProg().getSize();

    int chorusStart = partSize * notate.getCurrTabIndex();

    startIndex += chorusStart;

    stopIndex += chorusStart;

    notate.initCurrentPlaybackTab(startIndex);

    notate.setPlaybackStop(stopIndex, "in Stave: playSelection");

    notate.setShowPlayLine(true);
    notate.setKeyboardPlayback(true);
    
    Score score = notate.getScore();
    new PlayScoreCommand(score, 
                         startIndex, 
                         true,
                         notate.getMidiSynth(), 
                         notate, 
                         loopCount, 
                         notate.getTransposition(), 
                         useDrums, 
                         stopIndex,
                         false,
                         shouldHotSwap).execute();

    //System.out.println("score = " + score);
    
    repaint();
  }

public void playSelection(int startIndex, int stopIndex, int loopCount, boolean useDrums, String message, Style style)
  {
    if( startIndex != 0 || !notate.getFirstChorus() )
      {
        notate.noCountIn();
      }

    notate.chordVolumeChanged();

    //System.out.println("*** Play Selection from startIndex = " + startIndex + " to stopIndex = " + stopIndex + ", loopCount = " + loopCount + " " + message);

    int partSize = getChordProg().getSize();

    int chorusStart = partSize * notate.getCurrTabIndex();

    startIndex += chorusStart;

    stopIndex += chorusStart;

    notate.initCurrentPlaybackTab(startIndex);

    notate.setPlaybackStop(stopIndex, "in Stave: playSelection");

    notate.setShowPlayLine(true);
    notate.setKeyboardPlayback(true);
    
    Score score = notate.getScore();

    new PlayScoreCommand(score,
                         style,
                         startIndex, 
                         true,
                         notate.getMidiSynth(), 
                         notate, 
                         loopCount, 
                         notate.getTransposition(), 
                         useDrums, 
                         stopIndex).execute();

    //System.out.println("score = " + score);
    
    repaint();
  }

/**
 * Plays the current selection
 * This is the old way, for which the keyboard does not work.
 * However, it is better for note entry, where we don't need to see the keys
 * working.
 *
 *
 * @param startIndex       starting index of the selection of notes
 * @param endIndex         ending index of the selection of notes
 */
public void playSelectionNote(Note note, int selectedIndex)
  {
    //System.out.println("\nStave: playSelectionNote: selectedIndex = " + selectedIndex + " note = " + note);
    int i = selectedIndex + 1;
    int stopper = selectedIndex + StaveActionHandler.getEntryDuration(note);

    ChordPart chords = getChordProg();
    MelodyPart part = getMelodyPart();

    for( ; i < stopper; i++ )
      {
        if( chords.getUnit(i) != null )
          {
            break;
          }

        if( part.getUnit(i) != null && !part.getNote(i).isRest() )
          {
            break;
          }
      }

    playSelection(selectedIndex, i - 1, 0, false, "from Stave playSelectionNote/2");

  }


public String getSaveSelection(String title, ExtractMode mode)
  {
    return getSaveSelection(title, mode, 0);
  }

public String getSaveSelection(String title, ExtractMode mode, int grade)
  {
    int startIndex = getSelectionStart();
    int stopIndex = getSelectionEnd();
    return extract(title, mode, grade, startIndex, stopIndex);
  }

/**
 * Saves the current selection to the vocabulary file.
 * Unlike other extracts, this extracts text, not a Part.
 * @param title the title of the selection
 * @param asLick true if saving as lick, false if as cell
 */
public String extract(String title, ExtractMode mode, int grade,
                      int startIndex, int stopIndex)
  {
    if( !somethingSelected() )
      {
        return null;
      }

    MelodyPart melody = getMelodyPart();

    ChordPart chords = getChordProg();

    Chord.initSaveToLeadsheet();
    Note.initializeSaveLeadsheet();

    Writer writer = new StringWriter();

    try
      {
        // true below means that results will be appended.
        BufferedWriter out = new BufferedWriter(writer);

        // show item type and title

        switch( mode )
          {
            case RHYTHM:
                out.write("rhythm (notes ");
                break;
                
            case CELL:
                out.write("cell (notes ");
                break;
            case IDIOM:
                out.write("idiom (notes ");
                break;

            case LICK:
                out.write("lick (notes ");
                break;
            case QUOTE:
                out.write("quote (notes ");
                break;
                
            case BRICK:
                out.write("brick (notes ");
                break;
          }

        // get notes

        switch( mode )
          {
            case RHYTHM:
            case CELL:
            case IDIOM:
            case LICK:
            case QUOTE:
            case MELODY:
            case BRICK:
            case BOTH:

                int prevIndex = startIndex;
                Note prevNote = (Note) melody.getUnit(startIndex);
                if( prevNote == null )
                  {
                    prevNote = new Rest(1);
                  }
                else
                  {
                    prevNote = prevNote.copy();
                    if( mode == ExtractMode.RHYTHM && !(prevNote instanceof Rest))
                          {
                          prevNote.setPitch(60);
                          }
                  }

                int index = startIndex + 1;

                for( ; index <= stopIndex; index++ )
                  {
                    Note unit = (Note) melody.getUnit(index);
                    if( unit != null )
                      {
                        unit = unit.copy();
                        //System.out.println("unit = " + unit + " prev = " + prevNote);
                        if( mode == ExtractMode.RHYTHM && !(unit instanceof Rest) )
                          {
                          unit.setPitch(60); // C above middle C
                          }
                        int duration = index - prevIndex;
                        //System.out.println("stopIndex = " + stopIndex + ", prevIndex = " + prevIndex + ", duration = " + duration);
                        prevNote.setRhythmValue(duration);
                        prevNote.saveLeadsheet(out, metre, false); // no linebreaks

                        prevNote = unit;
                        prevIndex = index;
                        //unit.saveLeadsheet(out, metre, false); // no linebreaks
                      }
                  }

                // For a final rest, the duration is determined by adding up durations
                // associated with the construction lines.
                // This is necessary, because the value of a rest will generally extend
                // beyond the stopIndex.

                if( prevNote.isRest() )
                  {
                    int duration = 0;
                    for( int k = Math.min(index, cstrLines.length-1); k >= prevIndex; k-- )
                      {
                        if( cstrLines[k] != null )
                          {
                            duration += cstrLines[k].getSlotsPerDivision();
                          }
                      }
                    // Fix for final rest durations being too long.
                    prevNote.setRhythmValue(duration - BEAT/2);
                  }

                // Save the last note, whether it is a rest or not. Check that the note exists.
                if ( prevNote.getRhythmValue() != 0 )
                {
                    prevNote.saveLeadsheet(out, metre, false); // no linebreaks
                    out.write(" "); // in case following melody with chords, e.g. in control-J from notate
                }
            }

        // Get chord or render
        switch( mode )
          {
            case RHYTHM:
                out.write(")");
                break;
            case CELL:
            case IDIOM:
                out.write(")(chords ");
                break;

            case LICK:
            case QUOTE:
            case BRICK:
                out.write(")(sequence ");
                break;
          }

        // We buffer the output of chords, because in certain contexts,
        // we may want to truncate the last chord if it has a long duration.

        int chordCount = 0;
        Chord prevChord = (Chord) chords.getUnit(startIndex);
        if( prevChord == null )
          {
            prevChord = new Chord("NC", 1);
          }
        else
          {
            prevChord = prevChord.copy();
            chordCount++;
          }
        int prevIndex = startIndex;
        int index = startIndex + 1;

        switch( mode )
          {
            case RHYTHM:
                break;
            case CELL:
            case IDIOM:
            case LICK:
            case QUOTE:
            case CHORDS:
            case BRICK:
            case BOTH:
              {
                boolean done = false;

                for( ; !done && index <= stopIndex; index++ )
                  {
                    Chord unit = (Chord) chords.getUnit(index);
                    if( unit != null )
                      {
                        chordCount++;
                        prevChord.setRhythmValue(index - prevIndex);

                        switch( mode )
                          {
                            // Cell and idiom only get one chord
                            case CELL:
                            case IDIOM:
                                if( chordCount > 0 )
                                  {
                                    done = true;
                                  }
                                break;

                            case QUOTE:
                                if( chordCount > 1 )
                                  {
                                    done = true;
                                  }
                            case BRICK:
                            case LICK:
                                // Changed to 16 in order to recognize longer 
                                // chord progressions for lick critic.
                                if ( chordCount > MAX_LICK_CHORDS )
                                {
                                    done = true;
                                }
                            
                            // No break here!
                            case CHORDS:
                            case BOTH:
                                prevChord.saveLeadsheet(out, metre, false); // false means no linebreaks

                                prevChord = unit.copy();
                                prevIndex = index;

                                out.write(" ");
                                break;
                          }
                      }
                    if( done )
                      {
                        break;
                        // leave loop before incrementing index
                      }
                  }
              }
          }

        switch( mode )
          {
            case RHYTHM:
                out.write(") (name " + title);
                break;
            case CELL:
            case IDIOM:
                //prevChord.saveLeadsheet(out, metre, false);
                out.write((prevChord).toLeadsheet());
                out.write(") (name " + title);
                break;

            case LICK:
            case QUOTE:
            case BRICK:
                prevChord.setRhythmValue(roundToMultiple(index - prevIndex, 2 * BEAT));
                prevChord.saveLeadsheet(out, metre, false);
                Chord.flushChordBuffer(out, metre, false, false);
                out.write(") (name " + title);
                break;

            case CHORDS:
            case BOTH:
                prevChord.setRhythmValue(roundToMultiple(index - prevIndex, BEAT));
                // Truncate last chord if really long
                if( prevChord.getRhythmValue() > durationMax )
                  {
                    System.out.println("Duration limit exceeded by " + prevChord.getRhythmValue());
                    prevChord.setRhythmValue(durationMax);
                  }
                else if( prevChord.getRhythmValue() < durationMin )
                  {
                    System.out.println("Duration limit under by " + prevChord.getRhythmValue());
                    prevChord.setRhythmValue(durationMin);
                  }

                prevChord.saveLeadsheet(out, metre, false);
                Chord.flushChordBuffer(out, metre, false, false);
                break;
          }

        if( grade != 0 )
          {
            out.write(") (grade " + grade + ")\n");
          }
        else
          {
            out.write("\n");
          }

        out.close();
      }
    catch( IOException e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Internal Error in Stave");
      }

    String result = writer.toString();

    Trace.log(2, "Constructed: " + result);
    return result;
  }

/**
 * Saves the current selection to the vocabulary file.
 * Unlike other extracts, this extracts text, not a Part.
     * @param startIndex
     * @param stopIndex
     * @return 
 */
public Polylist extractChordNamePolylist(int startIndex, int stopIndex)
  {
    if( startIndex > stopIndex )
      {
        return Polylist.nil;
      }
    ChordPart chords = getChordProg();

    startIndex %= chords.size();
    stopIndex %= chords.size();

    PolylistBuffer buffer = new PolylistBuffer();

    for( int index = startIndex; index <= stopIndex; index++ )
      {
        Chord unit = (Chord) chords.getUnit(index);
        if( unit != null )
          {
            buffer.append(unit.toLeadsheet());
          }
      }

    return buffer.toPolylist();
  }

/**
 * round an integer value to the nearest multiple of base,
 * but if rounded value is 0, return base.
 * This is used for rounding chord durations.
@param value
@param base
@return
 */
public int roundToMultiple(int value, int base)
  {
    int quotient = value / base;
    int lower = quotient * base;
    int upper = (quotient + 1) * base;
    if( value - lower < upper - value )
      {
        if( lower == 0 )
          {
            return base;
          }
        else
          {
            return lower;
          }
      }
    else
      {
        return upper;
      }
  }

/**
 * Shifts chords up or down by one half step
 *
 * @param startIndex       starting index of the selection of chords
 * @param endIndex         ending index of the selection of chords
 * @param up               whether to transpose up or down
 * @param octave           if it should be shifted an octave or not
 */
public void shiftChords(int startIndex, int endIndex, boolean up)
  {

    int shift = up ? +1 : -1;

    notate.cm.execute(
            new ShiftChordsCommand(shift,
                                   getChordProg(),
                                   startIndex,
                                   endIndex,
                                   Key.Ckey));                        // FIX later

    redoAdvice();
    repaint();
  }

private class SelectionControls
        extends JPanel
        implements MouseListener
{

SelectionButton TransposeUp, TransposeDown;
int height = 25;
int width = 50;
boolean mouseOver = false;

SelectionControls()
  {
    TransposeUp = new SelectionButton(new ImageIcon(getClass().getResource("/imp/gui/graphics/one.png")))
    {

    private int transposeDistance;

    @Override
    public void dragEvent(MouseEvent e)
      {
        int newDistance = (e.getY() - dragStart.y) / 4;
        while( transposeDistance > newDistance )
          {
            transposeMelodyUpSemitone();
            transposeDistance--;
          }
        while( transposeDistance < newDistance )
          {
            transposeMelodyDownSemitone();
            transposeDistance++;
          }
      }

    };
    TransposeDown =
            new SelectionButton(new ImageIcon(getClass().getResource("/imp/gui/graphics/one.png")));

    setLayout(new FlowLayout(FlowLayout.CENTER));
    setHeight(height);

    add(TransposeUp);
    add(TransposeDown);

    setOpaque(false);
    addMouseListener(this);
  }

public void setHeight(int height)
  {
    this.height = height;
    setSize(width, height);
  }

public void mouseClicked(MouseEvent e)
  { 
  
  }

public void mousePressed(MouseEvent e)
  {

  }

public void mouseReleased(MouseEvent e)
  {
  }

public void mouseEntered(MouseEvent e)
  {
    mouseOver = true;
    requestFocus();
  }

public void mouseExited(MouseEvent e)
  {
    mouseOver = false;
  }

}

private class SelectionButton
        extends JPanel
        implements MouseListener, MouseMotionListener
{

ImageIcon icon;
Dimension size = new Dimension(16, 16);
private boolean mouseOver = false;
private boolean drag = false;
public Point dragStart;

SelectionButton(ImageIcon icon)
  {
    setPreferredSize(size);
    setSize(size);
    addMouseListener(this);
    addMouseMotionListener(this);

    this.icon = icon;
  }

public boolean isDragging()
  {
    return drag;
  }

public boolean isMouseOver()
  {
    return mouseOver;
  }

@Override
protected void paintComponent(Graphics g)
  {
    if( mouseOver )
      {
        g.setColor(selectionHighlightColor);
      }
    else
      {
        g.setColor(selectionColor);
      }
    g.fillRoundRect(0, 0, size.width - 1, size.height - 1, 10, 10);

    if( mouseOver )
      {
        g.setColor(selectionHighlightBorderColor);
      }
    else
      {
        g.setColor(selectionBorderColor);
      }
    g.drawRoundRect(0, 0, size.width - 1, size.height - 1, 10, 10);

    g.drawImage(icon.getImage(), 1, 1, this);
  }

public void mouseClicked(MouseEvent e)
  {
  }

public void mousePressed(MouseEvent e)
  {
  }

public void mouseReleased(MouseEvent e)
  {
    drag = false;
  }

public void mouseEntered(MouseEvent e)
  {
    mouseOver = true;
    repaint();
  }

public void mouseExited(MouseEvent e)
  {
    mouseOver = false;
    repaint();
  }

public void mouseDragged(MouseEvent e)
  {
    if( e.getID() == MouseEvent.MOUSE_DRAGGED )
      {
        if( !drag )
          {
            dragStart = e.getPoint();
          }
        drag = true;
        dragEvent(e);
      }
  }

public void mouseMoved(MouseEvent e)
  {
    drag = false;
  }

public void dragEvent(MouseEvent e)
  {
    // do override here
  }

} // class SelectionButton

void setBeaming(boolean sense)
  {
    beamingNotes = sense;
    repaint();
  }

public int getClefWidth()
  {
    return clefWidth;
  }

public void setChordFontSize()
  {
    chordFont = new Font("Helvetica", Font.BOLD, notate.getScore().getChordFontSize());
  }

/**
 * return the number of staff lines in this Stave.
 * @return 
 */
public int getNumLines()
  {
    return lineMeasures.length;
  }

public int getNumMeasures()
  {
    if( lineMeasures == null )
      {
        return 0;
      }
    
    int sum = 0;
    for( int x: lineMeasures )
      {
        sum += x;
      }
    return sum;
  }
    public void focusGained(FocusEvent e) {
       //notate.setBorderColor(new java.awt.Color(50, 50, 255));
    }

    public void focusLost(FocusEvent e) {
     //notate.setBorderColor(new java.awt.Color(255, 255, 255));   
    }
    
    public void partTitleFocus(){
        partTitleEditor.setVisible(true);
    }
    
}
