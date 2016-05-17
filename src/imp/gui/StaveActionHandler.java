/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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
package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.*;
import imp.data.*;
import imp.util.ErrorLog;
import imp.util.Trace;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.Icon;
import polya.Polylist;

/**
 * Initializes mouse and keyboard actions for a Stave.
 *
 * @author Aaron Wolin, Stephen Jones, Bob Keller (revised from code by Andrew Brown)
 * @version 1.0, 11 July 2005
 */
public class StaveActionHandler
  implements Constants, MouseListener, MouseMotionListener, KeyListener
{

/**
 * bias estimate for parallax
 * so that default is kept at 0
 */
private static int parallaxBias = 1;

/**
 * magic offset for bass-only staves
 */
private static final int bassOnlyOffset = 48;

/**
 * Shifts the mouse clicked position slightly for click accuracy
 */
private static final int verticalAdjustment = 2;

/**
 * Threshold for determining if additional vertical drag has occurred
 */
private static int VERTICAL_DRAG_THRESHOLD = 1;

Note lastAdviceNote;

Chord lastAdviceChord;

Chord lastAdviceNext;

/**
 * This is used in mapping Y offsets to pitches.
 * The accidental is based on the key signature.
 */
public static final int pitchFromSpacing[] =
 {
  c4, d4, e4, f4, g4, a4, b4, c5
 };

public static final int allPitchesFromSpacing[] =
 {
  c4, cs4, d4, ds4, e4, f4, fs4, g4, gs4, a4, as4, b4, c5
 };

/**
 * The stave to set all of the actions to
 */
private Stave stave;

/**
 * The notation window for the Stave
 */
private Notate notate;

/**
 * What single index is currently selected
 */
private int selectedIndex = OUT_OF_BOUNDS;

/**
 * What beat is currently selected
 */
private int selectedBeat = OUT_OF_BOUNDS;

/**
 * What measure is the mouse currently over
 */
private int selectedMeasure = OUT_OF_BOUNDS;

/**
 * The last measure the mouse was over
 */
private int lastMeasureSelected = OUT_OF_BOUNDS;

/**
 * Value for if the time signature is selected
 */
private boolean timeSelected = false;

/**
 * Value for if the key signature is selected
 */
private boolean keySelected = false;

/**
 * Last x position to have been clicked
 */
private int clickedPosX;

/**
 * Last y position to have been clicked
 */
private int clickedPosY;

/**
 * The current line the mouse is on
 */
private int currentLine;

/**
 * Last pitch to have been entered
 */
private Note storedNote = null;

/**
 * Indicates whether button1 has been clicked
 */
private boolean button1Down = false;

/**
 * Indicates if the last mouse click was on a construction line
 */
private boolean clickedOnCstrLine = false;

/**
 * Indicates if the last mouse click was on a beat bracket
 */
private boolean clickedOnBracket = false;

/**
 * The starting index of a dragging note
 */
private int startingIndex = OUT_OF_BOUNDS;

/**
 * Flag for if you can drag a note's pitch or not
 */
private boolean draggingPitch = false;

/**
 * Flag for if you can drag a note or not
 */
private boolean draggingNote = false;

/**
 * Flag for if you can drag a group of notes or not
 */
private boolean draggingGroup = false;

private int draggingGroupOffset = 0;

private int draggingGroupOrigSelectionStart = 0;

private int draggingGroupOrigSelectionEnd = 0;

/**
 * Flag for if you are currently dragging the selection box handles
 */
private boolean draggingSelectionHandle = false;

/**
 * Directional Flag for which handle is being dragged (true: left, false: right)
 */
private boolean draggingSelectionHandleLeft = false;

/**
 * Locks the dragging of a note to either pitch or position
 */
private boolean lockDragging = false;

/**
 * Flag for if the note is being dragged for the first time
 */
private boolean firstDrag = false;

/**
 * The starting x-axis position for dragging
 */
private int startDragX = OUT_OF_BOUNDS;

/**
 * The starting y-axis position for dragging
 */
private int startDragY = OUT_OF_BOUNDS;

/**
 * The ending x-axis position for dragging
 */
private int endDragX = OUT_OF_BOUNDS;

/**
 * The ending y-axis position for dragging
 */
private int endDragY = OUT_OF_BOUNDS;

/**
 * The most recent y-axis position for dragging
 */
private int lastDragY = OUT_OF_BOUNDS;

/**
 * The lowest slot index encountered during dragging
 */
private int dragMin;

/**
 * The highest slot index encountered during dragging
 */
private int dragMax;

/**
 * Flag for if the user is selecting notes
 */
private boolean selectingGroup = false;

/**
 * Flag for if the user is drawing a contour line
 */
private boolean drawing = false;

/**
 * Last index drawn with contour tool
 */
private int lastIndexDrawn = OUT_OF_BOUNDS;

/**
 * First index drawn with contour tool
 **/
private int firstIndexDrawn = OUT_OF_BOUNDS;

/**
 * Was the last tone added an approach tone?
 */
private boolean lastToneApproach = false;

/**
 * What was the last index approached?
 */
private int lastIndexApproached = OUT_OF_BOUNDS;

/**
 * Last approach drawn
 */
private int lastApproachPitch = OUT_OF_BOUNDS;

/**
 * Last point drawn with contour tool; for use with 'flat-lining' a curve
 * to extend a note's duration;
 */
private Point lastPointDrawn = null;

/**
 * Indices added during a draw stroke.  Any untriggered note additions
 * (due to fast mousing) will be determined by the list and redrawn on
 * release
 */
private java.util.List<Integer> firedIndices = new ArrayList<Integer>();

/**
 * Line that we're drawing on; we are restricted to drawing on a single line
 * per stroke.
 */
private int drawingLine = OUT_OF_BOUNDS;

/**
 * Flags for allowable tones in fitting notes to a drawn contour.
 */
private boolean drawScaleTones = true;

private boolean drawChordTones = true;

private boolean drawColorTones = false;

/**
 * Left bound of curve
 */
private int curveLeftBound;

/**
 * Right bound of curve
 */
private int curveRightBound;

/**
 * What was the last change on the x-axis of the curve?  This is used
 * to determine when a curve doubles back on itself and violates itself functionality
 */
private int oldDiff;

/**
 * Flag for if the user is shift-clicking
 */
private boolean shiftClicking = false;

/**
 * Flag for if the user is holding 'a' down - used for contour drawing
 */
private boolean aPressed = false;

/**
 * Cursors
 */
private final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

private final Cursor crosshair = new Cursor(Cursor.CROSSHAIR_CURSOR);

private final Cursor resizeEastCursor = new Cursor(Cursor.E_RESIZE_CURSOR);

private final Cursor resizeWestCursor = new Cursor(Cursor.W_RESIZE_CURSOR);

private final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);

private Cursor penCursor = null;

private Cursor noteCursor = null;

public int oldx = 0;

/**
 * Location of cursor during last mouseMove event, used to detect
 * when the cursor is inside hotspot rectangles such as the selection handles
 */
private Point cursorLocation = new Point(-1, -1);

/**
 * Flag for whether the handles should be displayed, when the flag changes
 * a repaint is needed
 */
private boolean overHandles = false;

private final String blueNoteCursorImg = "graphics/cursors/blueNoteCursor.gif";
private final String blackNoteCursorImg = "graphics/cursors/blackNoteCursor.gif";
private final String greenNoteCursorImg = "graphics/cursors/greenNoteCursor.gif";
private final String redNoteCursorImg = "graphics/cursors/redNoteCursor.gif";
private final String blackNoteLineCursorImg = "graphics/cursors/blackNoteLineCursor.gif";
private final String greenNoteLineCursorImg = "graphics/cursors/greenNoteLineCursor.gif";
private final String redNoteLineCursorImg = "graphics/cursors/redNoteLineCursor.gif";

private Cursor makeCursor(String filename, String cursorName, boolean offset)
 {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    
  Icon icon =
    new javax.swing.ImageIcon(getClass().getResource(filename));
 
  BufferedImage bi = new BufferedImage(icon.getIconWidth(),
                                       icon.getIconHeight(),
                                       BufferedImage.TYPE_INT_ARGB);
  
  icon.paintIcon(null, bi.getGraphics(), 0, 0);
  
  Point hotspot = 
      offset ? new Point(0, icon.getIconHeight() - 1) : new Point(0, icon.getIconHeight()/2);
  
  return toolkit.createCustomCursor(bi, hotspot, cursorName);

 }

/**
 * Constructs an action handler for a particular stave
 *
 * @param stave             the Stave for which to set the actions to
 * @param notate            the notation window for the Stave
 */
StaveActionHandler(Stave stave, Notate notate)
 {
  this.stave = stave;
  this.notate = notate;

  penCursor = makeCursor("graphics/toolbar/pencilCursor.png", "Pencil", true);
  
  noteCursor = notate.getUseNoteCursor()? makeCursor("graphics/cursors/blueNoteCursor.png", "Note", true) : defaultCursor;
 }

/***
 * Mouse Listener stubs
 **/

/**
 * Mouse entereds
 */
public void mouseEntered(MouseEvent e)
 {
   
 }

/**
 * Mouse exited
 */
public void mouseExited(MouseEvent e)
 {
  
 }

public void maybeSetCursor(MouseEvent e)
 {
  if( stave.getShowSheetTitle() )
   {
    if( stave.sheetTitleEditor.checkEvent(e) || stave.sheetComposerEditor.checkEvent(
      e) || stave.showTitleEditor.checkEvent(e) || stave.yearEditor.checkEvent(e) )
     {
      setCursor();
     }
   }

  if( stave.getShowPartTitle() )
   {
    if( stave.partTitleEditor.checkEvent(e) || stave.partComposerEditor.checkEvent(
      e) )
     {
      setCursor();
     }
   }
 }

/**
 * Mouse moved
 */
public void mouseMoved(MouseEvent e)
 { 
  
  Trace.log(2, "mouse moved " + e);

  cursorLocation = e.getPoint();

  boolean doRepaint = false;
  
  boolean withinNoteArea = inNoteArea(e);

  if( withinNoteArea && notate.getUseNoteCursor())
    chooseAndSetNoteCursor(e);

  else
    maybeSetCursor(e);   
  
  // draws construction lines for the current measure if the flag for
  // drawing them is active
  if( withinNoteArea && stave.getShowMeasureCL() && !stave.getShowAllCL() )
   {
    stave.mouseOverMeasure = findMeasure(e);
    Trace.log(4,
              "mouse over measure " + stave.mouseOverMeasure + " last measure selected was " + lastMeasureSelected);
    
    if( stave.mouseOverMeasure != lastMeasureSelected )
     {  
      stave.repaintLineFromCstrLine(
          lastMeasureSelected * stave.getMeasureLength());
          
      lastMeasureSelected = stave.mouseOverMeasure;

      stave.repaintLineFromCstrLine(
          lastMeasureSelected * stave.getMeasureLength());
     }
   }

  if( !drawing )
   {
    /* if over the selection or the activate regions of the handles (larger than
     * the actual handles) then the handles should be drawn, otherwise they
     * should be erased
     */
    if( activeHandles() != overHandles )
     {
      overHandles = !overHandles;
      doRepaint = true;
     }

    // if mouse is over a handle, change the cursor
    if( mouseOverLHandle() && !stave.nothingSelected() )
     {
      setCursor(resizeWestCursor);
     }
    else if( mouseOverRHandle() && !stave.nothingSelected() )
     {
      setCursor(resizeEastCursor);  // west and east should be the same, but just in case...
     }
    else
     {
         
        if( withinNoteArea && notate.getUseNoteCursor())
            chooseAndSetNoteCursor(e);
        else
            setCursor();
     }
   }

  if( doRepaint )
   {
    stave.repaint();
   }
  
  if (!notate.getUseNoteCursor() && withinNoteArea)
  {
    updateNoteCursorLabel(e);
  }
  else
  {
    stave.clearNoteCursorLabel();
  }
 }

private void updateNoteCursorLabel(MouseEvent e)
{
    int x = e.getX();
    int y = e.getY();
    int pitch;
    Note note;
    
    int curLine = getCurrentLine(y);
    ChordPart prog = stave.getChordProg();
    int cstrLine = stave.getNextCstrLine(searchForCstrLine(x, y));
    Chord currentChord = prog.getPrevChord(cstrLine);

    if (notate.getSmartEntry())
    {
        note =  yPosToRectifiedPitch(y - (notate.getParallax() + parallaxBias),
                            currentChord, curLine, e.isShiftDown());
        pitch = note.getPitch();
    }                 
    else
    {
        pitch = yPosToPitch(y - (notate.getParallax() + parallaxBias),
                            curLine);
        note = new Note(pitch);
    }
    
    if( notate.getShowNoteNamesAboveCursor() )
      {
      stave.setNoteCursorLabel(noteNameFromMidi(note), x, y);
      }
    
    if (currentChord != null && !currentChord.getName().equals(NOCHORD))
        stave.updateTempLegerLines(pitch, x,  curLine, stave.getGraphics());
}
  

/**
 * Chooses the appropriate note cursor to use based on the given event.
 * @param e 
 */
private void chooseAndSetNoteCursor(MouseEvent e)
{
    stave.clearNoteCursorLabel();
    
    int x = e.getX();
    int y = e.getY();
    
    ChordPart prog = stave.getChordProg();
    int cstrLine = stave.getNextCstrLine(searchForCstrLine(x, y));
    Chord currentChord = prog.getPrevChord(cstrLine);

    int pitch;
    Note note;
    
    MelodyPart melody = stave.getMelodyPart();
    
    Note oldNote = melody.getNote(searchForCstrLine(x, y));
    
    int curLine = getCurrentLine(y);

    // Get the pitch that would be input if the mouse was clicked here. If
    // smart entry is turned on, the pitch will be rectified, so the cursor
    // will be colored based on a different note than it would be otherwise
    if (notate.getSmartEntry())
    {
        note =  yPosToRectifiedPitch(y - (notate.getParallax() + parallaxBias),
                            currentChord, curLine, e.isShiftDown());
        pitch = note.getPitch();
    }                 
    else
    {
        pitch = yPosToPitch(y - (notate.getParallax() + parallaxBias),
                            curLine);
        note = new Note(pitch);
    }

    // What are we doing here?
    //
    // MAGIC VALUE
    if (oldNote != null &&
        Math.abs(oldNote.getPitch() - pitch) <= 2 &&
        stave.getSelectionStart() == searchForCstrLine(x, y))
            {
                setCursor(defaultCursor);
                return;
            }

    boolean noteOnLegerLine = noteOnLegerLine(pitch, curLine);

    // if we have a real chord
    if (currentChord != null && !currentChord.getName().equals(NOCHORD))
    {

        ChordForm curChordForm = currentChord.getChordForm();
        String root = currentChord.getRoot();

        ArrayList<Integer> chordMIDIs = curChordForm.getSpellMIDIarray(root);
        ArrayList<Integer> colorMIDIs = curChordForm.getColorMIDIarray(root);
        
        // Put all the pitches in the same octave so we can compare them
        for(int i = 0; i < chordMIDIs.size(); i++)
        {
            int chordNote = chordMIDIs.get(i);
            chordMIDIs.set(i, chordNote%OCTAVE);
        }

        for(int i = 0; i < colorMIDIs.size(); i++)
        {
            int colorNote = colorMIDIs.get(i);
            colorMIDIs.set(i, colorNote%OCTAVE);
        }

        // pitch is invalid
        if( pitch < stave.getMinPitch() || pitch > stave.getMaxPitch() )
        {
            noteCursor = makeCursor(blueNoteCursorImg, "Note", true);

            setCursor(noteCursor);
            stave.clearNoteCursorLabel();
            return;
        }
        // pitch is a chord tone
        else if (chordMIDIs.contains(pitch%OCTAVE))
        {
            if (noteOnLegerLine)
                noteCursor = makeCursor(blackNoteLineCursorImg, "Note", true);
            else
                noteCursor = makeCursor(blackNoteCursorImg, "Note", true);
        }
        
        // pitch is a color tone
        else if (colorMIDIs.contains(pitch%OCTAVE))
        {
            if (noteOnLegerLine)
                noteCursor = makeCursor(greenNoteLineCursorImg, "Note", true);
            else
                noteCursor = makeCursor(greenNoteCursorImg, "Note", true); 
        }
        
        // pitch is out of key
        else
        {
            if (noteOnLegerLine)
                noteCursor = makeCursor(redNoteLineCursorImg, "Note", true);
            else
                noteCursor = makeCursor(redNoteCursorImg, "Note", true); 
        }
    }

    else
    {
        if (noteOnLegerLine)
            noteCursor = makeCursor(blackNoteLineCursorImg, "Note", true);
        else
            noteCursor = makeCursor(blackNoteCursorImg, "Note", true);
    }

    stave.updateTempLegerLines(pitch, x,  curLine, stave.getGraphics());
    if( notate.getShowNoteNamesAboveCursor() )
      {
      stave.setNoteCursorLabel(noteNameFromMidi(note), x, y);
      }
    setCursor(noteCursor);
}

/**
 * Determines whether the given midi value would be input to the staff on a
 * ledger line.
 * 
 * @param midi
 * @return 
 */
private boolean noteOnLegerLine(int midi, int curLine)
{
    int norm = midi%24;
    
    Note note = new Note(norm);
    note.setEnharmonic(notate.getScore().getCurrentEnharmonics(curLine));

    if (note.getAccidental().equals(Accidental.FLAT))
        norm++;
    if (note.getAccidental().equals(Accidental.SHARP))
        norm--;

    return (norm == 2 || norm ==5 || norm==9 || norm == 12 ||
            norm == 16 || norm == 19 || norm == 23 );
}

private String noteNameFromMidi(Note note)
{
    int norm = note.getPitch()%OCTAVE;

    if (note.getAccidental().equals(Accidental.FLAT))
        norm++;
    if (note.getAccidental().equals(Accidental.SHARP))
        norm--;
    
    String str;
    
    switch (norm)
    {
        case 0:
            str = "c";
            break;
        case 2:
            str = "d";
            break;
        case 4:
            str = "e";
            break;
        case 5:
            str = "f";
            break;
        case 7:
            str = "g";
            break;      
        case 9:
            str = "a";
            break;       
        case 11:
            str = "b";
            break;        

        default:
            str = Integer.toString(norm);
            break;                
    }
    
    if (note.getAccidental().equals(Accidental.FLAT))
    {
        str += "b";
    }
    
    if (note.getAccidental().equals(Accidental.SHARP))
    {
        str += "#";
    }
    
    return str;
}

boolean isDrawing()
 {
  return drawing;
 }

// if mouse over selection or if we are currently dragging a handle or if
// the mouse is in the selection box, then return true to indicate the handles
// should be active (visible, in this case)

boolean activeHandles()
 {
  if( !stave.getSelectionBoxDrawn() )
   {
    return false;
   }

  if( draggingSelectionHandle
    || stave.selectionLHandle.contains(cursorLocation)
    || stave.selectionRHandle.contains(cursorLocation) )
   {
    return true;
   }
  for( Rectangle r: stave.selectionBox )
   {
    if( r.contains(cursorLocation) )
     {
      return true;
     }
   }
  return false;
 }

boolean mouseOverLHandle()
 {
  return stave.selectionLHandle.contains(cursorLocation);
 }

boolean mouseOverRHandle()
 {
  return stave.selectionRHandle.contains(cursorLocation);
 }

/**
 * Mouse clicked
 */
public void mouseClicked(MouseEvent e)
 {
     notate.staveRequestFocus();
  // update the clicked position
  clickedPosX = e.getX();
  clickedPosY = e.getY();

  /* If drawing we want to add a single note at this position */
  //if( drawing )
  if( !e.isShiftDown() && !e.isControlDown() && e.getButton() != MouseEvent.BUTTON3 )
   {
    //Trace.log(2, "point J");
    // get the current line the mouse is on
    currentLine = getCurrentLine(clickedPosY);
    selectedIndex = searchForCstrLine(clickedPosX, clickedPosY);

    if( selectedIndex != OUT_OF_BOUNDS )
     {
      stave.setSelection(selectedIndex);
      addNote(e, stave.getChordProg().getCurrentChord(selectedIndex));
      stave.repaint();
     }
   }
 }

/**
 * The maximum duration a note should sound on entry.
 */
private static int MAX_NOTE_ENTRY_LENGTH = BEAT / 2;

public static int getEntryDuration(Note note)
 {
   return Math.max(0, Math.min(note.getRhythmValue(), MAX_NOTE_ENTRY_LENGTH) - 1);
 }

/**
 * Add a note as determined by MouseEvent e.
 */
private int addNote(MouseEvent e)
 {
  return addNote(e.getX(), e.getY());
 }

/**
 * Add a note as determined by MouseEvent e.
 * Note that different methods are called, depending on whether or not there is a chord!
 */
private int addNote(int x, int y)
 {
  return addNote(x, y, true);
 }

/**
 * Add a note as determined by MouseEvent e.
 * Note that different methods are called, depending on whether or not there is a chord!
 */
private int addNote(int x, int y, boolean play)
 {

  clearPasteFrom();

  int pitch = yPosToKeyPitch(y, currentLine);

  //Accidental accidental = Accidental.NATURAL;
  Note note = new Note(pitch);
  note.setEnharmonic(notate.getScore().getCurrentEnharmonics(selectedIndex));

  // set the note in the original part
  notate.cm.execute(
    new SetNoteCommand(selectedIndex,
                       note,
                       stave.getMelodyPart()));

  Trace.log(2,
            "adding new note: " + note.toLeadsheet() + " at " + selectedIndex);

  // Allow the note's pitch to be dragged
  draggingPitch = true;
  // Allow the note itself to be dragged
  draggingNote = true;

  selectingGroup = false;

  redoAdvice(selectedIndex);

  int duration = getEntryDuration(note);

  notate.noCountIn();

  if( play )
   {
    stave.playSelection(selectedIndex, selectedIndex + duration, 0, false, "from StaveActionHandler addNote/3");
   }

  return note.getPitch();

 }

/**
 * Add and play a note as determined by MouseEvent e, within a particular chordal context.
 */
private int addNote(MouseEvent e, Chord chord)
 {
  return addNote(e, chord, true);
 }

/**
 * Add a note as determined by MouseEvent e, within a particular chordal context.
 */
private int addNote(MouseEvent e, Chord chord, boolean play)
 {
  return addNote(e.getX(), e.getY(), chord, e.isShiftDown(), play);
 }

/**
 * Add a note as determined by MouseEvent e, within a particular chordal context.
 */
private int addNote(int x, int y, Chord chord, boolean shiftDown, boolean play)
 {
  //System.out.println("adding note at " + x + ", " + y + " chord = " + chord);
  stave.setSelection(selectedIndex, selectedIndex);

  if( !notate.getSmartEntry() )
   {
    // Simple addition, ignoring chord.
    return addNote(x, y, play);
   }

  /* Default to context-free note addition if there are no chords. */
  if( chord == null || chord.getName().equals(Constants.NOCHORD) )
   {
    return addNote(x, y, play);
   }

  ChordPart prog = stave.getChordProg();

  Polylist approachTones = new Polylist();

  drawScaleTones = stave.notate.getScaleTonesSelected();
  drawChordTones = stave.notate.getChordTonesSelected();
  //drawColorTones = stave.notate.getColorTonesSelected();

  /* Don't add any note if there's nothing to pick from. */
  if( !(drawScaleTones || drawChordTones || drawColorTones) )
   {
    return OUT_OF_BOUNDS;
   }

  // Are approaches user-enabled?
  boolean approachEnabled = (aPressed && shiftDown);
  boolean apprch = false; // Is this particular note going to be an
  // approach tone?

  /* Lock in any chord tone that follows an approach tone as long as
   * the user is dragging over that index still.
   */
  if( selectedIndex == lastIndexDrawn && selectedIndex == lastIndexApproached )
   {
    return stave.getMelodyPart().getNote(selectedIndex).getPitch();
   }

  /* Is this index the one right before a chord change?  If it is, and
   * if we've enabled approaching with Shift-A, tag this as an
   * approach tone.
   */
  apprch =
    ((selectedIndex + stave.getMelodyPart().getUnitRhythmValue(selectedIndex)
    == prog.getNextUniqueChordIndex(selectedIndex)) && approachEnabled);


  Chord nextChord = prog.getNextUniqueChord(selectedIndex);

  clearPasteFrom();

  // add new note close to mouse clicked pitch

  int pitch =
    (lastToneApproach && !apprch) ? 
         lastApproachPitch 
       : yPosToAnyPitch(y - (notate.getParallax() + parallaxBias), currentLine);

  // reset the pitch to the max or min pitch of the Stave if
  // they are out of bounds

  if( pitch < stave.getMinPitch() )
   {
    pitch = stave.getMinPitch();
   }
  else if( pitch > stave.getMaxPitch() )
   {
    pitch = stave.getMaxPitch();
   }

  //Accidental accidental = Accidental.NATURAL;
  int keysig = stave.getKeySignature();

  // adjust pitch to respect chord!

  ChordForm form = chord.getChordSymbol().getChordForm();

  String root = chord.getRoot();

  Polylist scaleTones = form.getFirstScaleTones(root);
  Polylist chordTones = form.getSpell(root);
  Polylist colorTones = form.getColor(root);

  /* So far, the list of accpetable pitches to draw.
   * We want to disregard this in a moment if we're going to force
   * an approach tone.
   */
  Polylist m = new Polylist();
  if( drawScaleTones )
   {
    m = m.append(scaleTones);
   }
  if( drawChordTones )
   {
    m = m.append(chordTones);
   }
  if( drawColorTones )
   {
    // too liberal? m = m.append(colorTones);
   }

  if( apprch )
   {
    ChordForm nextForm = nextChord.getChordSymbol().getChordForm();

    // The list of lists of (chordTone approach1 approach2 etc)
    Polylist approachList = nextForm.getApproach(nextChord.getRoot());

    /* Build a list of approach tones to the next chord */
    Polylist tones = new Polylist();
    while( approachList.nonEmpty() )
     {
      tones = tones.append(((Polylist) approachList.first()).rest());
      approachList = approachList.rest();
     }

    /* If it isn't empty, use it */
    if( tones.nonEmpty() )
     {
      m = tones;
      lastToneApproach = true;
     }
   }

  // This must be a chord tone since it follows an approach tone.
  if( lastToneApproach && !apprch )
   {
    m = chordTones;

    lastToneApproach = (selectedIndex == lastIndexDrawn);
    lastIndexApproached = selectedIndex;
   }

  Note note = Note.getClosestMatch(pitch, m);

  pitch = note.getPitch();
  if( apprch )
   {
    lastApproachPitch = pitch;
   }

  // set the note in the original part
  notate.cm.execute(
    new SetNoteCommand(selectedIndex,
                       note,
                       stave.getMelodyPart()));

  Trace.log(2,
            "adding new note over chord: " + note.toLeadsheet()
    + " at " + selectedIndex);

  draggingPitch = false;
  draggingNote = false;
  selectingGroup = false;

  redoAdvice(selectedIndex);

  notate.noCountIn();

  int duration = getEntryDuration(note);

  if( play )
   {
    stave.playSelection(selectedIndex, selectedIndex + duration, 0, false, "from StaveActionHandler addNote/5");
   }

  return pitch;
 }

private int yPosToKeyPitch(int y, int currentLine)
 {

  // add new note close to mouse clicked pitch

  int pitch = yPosToPitch(y - (notate.getParallax() + parallaxBias), currentLine);

  // reset the pitch to the max or min pitch of the Stave if
  // they are out of bounds

  if( pitch < stave.getMinPitch() )
   {
    pitch = stave.getMinPitch();
   }
  else if( pitch > stave.getMaxPitch() )
   {
    pitch = stave.getMaxPitch();
   }

  int keysig = stave.getKeySignature();

  // adjust pitch to respect the key signature

  int adjustment = Key.adjustPitchInKey[keysig - MIN_KEY][pitch % OCTAVE];

  pitch += adjustment;

  return pitch;
 }

/**
 * Basic contains method for an array of integers.
 * Returns the matched index, or -1 if no match.
 */
private int arrayContains(int pitch, int[] pitches)
 {
  for( int i = 0; i < pitches.length; i++ )
   {
    if( pitches[i] == pitch )
     {
      return i;
     }
   }
  return OUT_OF_BOUNDS;
 }

/**
 * Clear the "paste from" selection.
 */
protected void clearPasteFrom()
 {
  stave.setPasteFromStart(OUT_OF_BOUNDS);
  stave.setPasteFromEnd(OUT_OF_BOUNDS);
 }

/**
 * Mouse pressed
 */
public void mousePressed(MouseEvent e)
 {
     if( !inNoteArea(e) )
    {
      stave.unselectAll();
      stave.repaint();
      return;
    }
     
  stave.getCurvePoints()[e.getX()] = e.getY();

  if( notate.justPasted )
   {
    Trace.log(2, "just pasted");
    clearPasteFrom();
    notate.justPasted = false;
   }

  // Set the focus to be in the Stave in order for the keyboard to
  // function properly

  stave.requestFocusInWindow();

  maybeSetCursor(e);

  if( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 && !stave.nothingSelected() )
   {
    if( mouseOverLHandle() )
     {
      draggingSelectionHandle = true;
      draggingSelectionHandleLeft = true;
      return;
     }
    if( mouseOverRHandle() )
     {
      draggingSelectionHandle = true;
      draggingSelectionHandleLeft = false;
      return;
     }
   }

  // if you begin to select a group of notes
  if( !selectingGroup && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 
          && searchForBracket(e) == OUT_OF_BOUNDS )
   {

    Trace.log(2, "set up for dragging");

    startDragX = e.getX();
    startDragY = e.getY();

    lastDragY = startDragY;

    clickedPosX = e.getX();
    clickedPosY = e.getY();

    // If contour drawing is off, dragging should select a group.
    // Otherwise, this setup info will be used for the curve
    drawing = (notate.getMode() == Notate.Mode.DRAWING);
    selectingGroup = !drawing;
   }

  if( drawing )
   {
    startDragX = curveLeftBound = curveRightBound = e.getX();
    startDragY = e.getY();

    drawingLine = currentLine = getCurrentLine(e.getY());
    firedIndices = new ArrayList<Integer>();
   }

  // if the button was a left click and within the note area
  if( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 && !drawing )
   {

    Trace.log(2, "clicked in note area");

    // get the current line the mouse is on
    currentLine = getCurrentLine(e.getY());

    clickedPosX = e.getX();
    clickedPosY = e.getY();

    selectedIndex = searchForCstrLine(e.getX(), e.getY());

    // search to see if a bracket was clicked on
    selectedBeat = searchForBracket(e);


    // Open a pop-up menu if ctrl + left click
    if( e.isControlDown() && !e.isShiftDown() )
     {

      Trace.log(2, "control (no shift) = requested popup");

      notate.popupMenu.show(e.getComponent(), e.getX(), e.getY());
      stave.currentLine = getCurrentLine(e.getY());
      
      selectingGroup = false;
      return;
     }
    // check to see if a construction line was clicked on
    else if( selectedIndex != OUT_OF_BOUNDS )
     {

      // see if selecting a line, without wanting a note

      if( e.isShiftDown() && e.isControlDown() )
       {
        stave.setSelection(selectedIndex);

        if( e.isControlDown() )  // shift + control => rest
         {
          Trace.log(2, "shift-control: adding rest");
          notate.addRest();
         }

        selectingGroup = false;
        clickedOnCstrLine = true;

        stave.repaint();
        return;
       }
      else if( e.isShiftDown() && stave.nothingSelected() )
       {

        Trace.log(2, "shift: single line selected");

        stave.setSelection(selectedIndex);

        selectingGroup = false;
        clickedOnCstrLine = true;

        stave.repaint();
        return;
       }

      // if 'shift' selecting a group of notes
      if( e.isShiftDown() && stave.somethingSelected() )
       {
        boolean selectionLocked = (stave.getLockSelectionWidth() != -1);
        if( selectedIndex < stave.getSelectionStart() || selectionLocked )
         {
          Trace.log(2, "shift: extending selection to the left");
          stave.setSelectionStart(selectedIndex);
         }
        else if( selectedIndex > stave.getSelectionEnd() )
         {
          Trace.log(2, "shift: extending selection to the right");
          stave.setSelectionEnd(selectedIndex);
         }
        else
         {
          stave.setSelectionStart(selectedIndex);
          stave.setSelectionEnd(selectedIndex);
         }
        shiftClicking = true;
        selectingGroup = false;
       }
      // or if the user has pressed within a selection of notes
      else if( selectedIndex >= stave.getSelectionStart()
        && selectedIndex <= stave.getSelectionEnd() /* && stave.getSelectionStart() != stave.getSelectionEnd() */ ) // with this clause in, cannot drag single notes laterally
       {
        Trace.log(2,
                  "pressing within an existing selection, maybe going to drag");

        draggingGroup = true;
        draggingGroupOrigSelectionStart = stave.getSelectionStart();
        draggingGroupOrigSelectionEnd = stave.getSelectionEnd();
        draggingGroupOffset =
          (selectedIndex - draggingGroupOrigSelectionStart);

        selectingGroup = false;
       }

      ChordPart chordProg = stave.getChordProg();
      if( chordProg != null )
        {
        Chord chord = chordProg.getCurrentChord(selectedIndex);
        if( chord != null && !chord.isNOCHORD() )
          {
          redoAdvice(selectedIndex);
         }
        }

      stave.repaint();
      return;
     }

    // check if the time signature is clicked
    int theTimeSpace = stave.leftMargin + stave.clefWidth + stave.keySigWidth;
    if( (e.getX() > theTimeSpace) && (e.getX() < theTimeSpace + 10) && (e.getY() < stave.headSpace + stave.lineSpacing) )
     {
      timeSelected = true;

      clickedPosX = e.getX();
      clickedPosY = e.getY();
     }

    // check if the key signature is clicked
    int theClefSpace = stave.leftMargin + stave.clefWidth;
    int minKeySpace = 10;
    if( stave.keySigWidth > minKeySpace )
     {
      minKeySpace = stave.keySigWidth;
     }
    if( (e.getX() > theClefSpace - 10) && (e.getX() < theClefSpace + minKeySpace) && (e.getY() < stave.headSpace + stave.lineSpacing) )
     {
      keySelected = true;

      clickedPosX = e.getX();
      clickedPosY = e.getY();
     }
   }
  // if the user has a 2+ button mouse and right clicks
  else if( (e.getModifiersEx()& MouseEvent.BUTTON3_DOWN_MASK) != 0 )
   {

    // get the current line the mouse is on
    currentLine = getCurrentLine(e.getY());

    // Open a pop-up menu if plain right clicking
    notate.popupMenu.show(e.getComponent(), e.getX(), e.getY());
    stave.currentLine = getCurrentLine(e.getY());
   }

 }

public boolean getDraggingSelection()
 {
  return draggingSelectionHandle;
 }

/**
 * Mouse dragged
 */
public void mouseDragged(MouseEvent e)
 {
  Trace.log(4, "mouse dragged in StaveActionHandler");

  if( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0 )
   {
    return;
   }

  int nearestLine = searchForCstrLine(e.getX(), e.getY());

  // draws construction lines for the current measure if the flag for
  // drawing them is active
  if( inNoteArea(e) && stave.getShowMeasureCL() )
   {
    stave.mouseOverMeasure = findMeasure(e);

    if( stave.mouseOverMeasure != lastMeasureSelected )
     {
      stave.repaintLineFromCstrLine(
        lastMeasureSelected * stave.getMeasureLength());
      lastMeasureSelected = stave.mouseOverMeasure;
      stave.repaintLineFromCstrLine(
        lastMeasureSelected * stave.getMeasureLength());
     }
   }

  boolean selectionLocked = (stave.getLockSelectionWidth() != -1);
  // first priority is if mouse is over selection handles
  if( !drawing && draggingSelectionHandle )
   {
    if( nearestLine != OUT_OF_BOUNDS )
     {
      int start = stave.getSelectionStart();
      int end = stave.getSelectionEnd();

      // user dragging the left handle
      if( draggingSelectionHandleLeft )
       {
        if( nearestLine <= end || selectionLocked )
         { // user still dragging left
          stave.setSelectionStart(nearestLine);
         }
        else
         { // switch directions, user dragged left handle to the right beyond the right handle
          stave.setSelectionEnd(nearestLine);
          stave.setSelectionStart(end);
          draggingSelectionHandleLeft = false;
         }
       }
      else
       { // user dragging the right handle
        if( selectionLocked )
         {
          stave.setLockedSelectionEnd(nearestLine);
         }
        else if( nearestLine >= start )
         {  // user still dragging right
          stave.setSelectionEnd(nearestLine);
         }
        else
         {    // switch directions, user dragged right handle to the left beyond the left handle
          stave.setSelectionStart(nearestLine);
          stave.setSelectionEnd(start);
          draggingSelectionHandleLeft = true;
         }
       }
      stave.repaint();
     }
    return;
   }

  // if you're selecting a group of notes
  if( selectingGroup && !timeSelected && !keySelected // with selectingGroup out, does not help dragging single notes
    && !draggingPitch && !draggingNote && !draggingGroup && !shiftClicking && !drawing )
   {
    Trace.log(2, "point A");
    // change the cursor to a crosshair
    stave.setCursor(crosshair);

    // draws a rectangle around the selection
    if( e.getX() > startDragX )
     {
      if( e.getY() > startDragY )
       {
        stave.getGraphics().drawRect(startDragX, startDragY,
                                     e.getX() - startDragX,
                                     e.getY() - startDragY);
       }
      else
       {
        stave.getGraphics().drawRect(startDragX, e.getY(),
                                     e.getX() - startDragX,
                                     startDragY - e.getY());
       }
     }
    else
     {
      if( e.getY() > startDragY )
       {
        stave.getGraphics().drawRect(e.getX(), startDragY,
                                     startDragX - e.getX(),
                                     e.getY() - startDragY);
       }
      else
       {
        stave.getGraphics().drawRect(e.getX(), e.getY(),
                                     startDragX - e.getX(),
                                     startDragY - e.getY());
       }
     }

    stave.repaint();
   }
  else if( drawing )
   {
    /* Might want to consider some of these conditions,
    as well.
    && !selectingGroup && !timeSelected &&
    !keySelected && !draggingPitch && !draggingNote &&
    !draggingGroup && !shiftClicking*/

    // Set the graphics for drawing the contour and draw the new
    // addition to the curve to give a real-time effect

//            Graphics2D g2d = (Graphics2D) stave.getGraphics();
//            g2d.setColor(stave.getCurveColor());
//            g2d.setStroke(stave.getCurveStroke());

    /* Try adding a note at the current position in the stave to the
     * contour */
    try
     {
      int x = e.getX();
      int y = e.getY();
      if( (currentLine = getCurrentLine(y)) < drawingLine )
       {
        currentLine = drawingLine;
        y =
          drawingLine * stave.lineSpacing + stave.headSpace - stave.lineOffset;
       }
      else if( currentLine > drawingLine )
       {
        currentLine = drawingLine;
        y =
          (drawingLine + 1) * stave.lineSpacing + stave.headSpace - stave.lineOffset;
       }

      int newPitch = yPosToPitch(y, currentLine);

      selectedIndex = searchForCstrLine(x, y);
      if( e.isShiftDown() && !aPressed && lastPointDrawn != null )
       {
        y += lastPointDrawn.y - y;
       }
      else
       {
        // get the current line the mouse is on
        currentLine = getCurrentLine(y);
       }

      /**
       * The following are the conditions for changing the part.
       * They are both necessary, and ensure that we only add notes
       * when we cross new slots, or when we vertically move within a
       * slot to change its pitch in-place. */
      Note lastDrawnNote = stave.getMelodyPart().getNote(lastIndexDrawn);

      if( (selectedIndex != lastIndexDrawn || (lastDrawnNote != null && lastDrawnNote.getPitch() != newPitch)) && selectedIndex != OUT_OF_BOUNDS )
       {

        if( firedIndices.size() == 0
          || (selectedIndex != lastIndexDrawn && selectedIndex != OUT_OF_BOUNDS && selectedIndex != firedIndices.get(
          firedIndices.size() - 1)) )
         {
          firedIndices.add(selectedIndex);
         }

        // If control is down, add a rest; otherwise a pitched note.
        if( e.isControlDown() )
         {
          notate.cm.execute(new SetRestCommand(selectedIndex,
                                               stave.getMelodyPart()));
         }
        else
         {
          // Adding note in drawing mode; don't play

          addNote(e, stave.getChordProg().getCurrentChord(selectedIndex), false);
         }

        if( firstIndexDrawn == OUT_OF_BOUNDS )
         {
          firstIndexDrawn = selectedIndex;
          stave.setSelectionStart(firstIndexDrawn);
          stave.setSelectionEnd(firstIndexDrawn);
         }

        lastPointDrawn = new Point(x, y);
        lastIndexDrawn = selectedIndex;

        /* When shift is held, we want to extend the current (locked)
         * note by a slot size.  To do this, we delete the new unit
         * after setting in place, which expands the previous note
         * while advancing the cursor.*/

        if( selectedIndex != OUT_OF_BOUNDS && e.isShiftDown()
          && selectedIndex >= 1 && !aPressed )
         {

          notate.cm.execute(new DeleteUnitsCommand(stave.getMelodyPart(),
                                                   selectedIndex, selectedIndex));
         }

        if( stave.getSelectionStart() > selectedIndex )
         {
          stave.setSelectionStart(selectedIndex);
         }

        if( stave.getSelectionEnd() < selectedIndex )
         {
          stave.setSelectionEnd(selectedIndex);
         }
       }

      Point p = new Point(x, y);

      if( p.x < curveLeftBound )
       {
        curveLeftBound = p.x;
       }

      if( p.x > curveRightBound )
       {
        curveRightBound = p.x;
       }

      int newDiff = p.x - startDragX;
      if( (newDiff > 0 && oldDiff < 0) || (newDiff < 0
        && oldDiff > 0) )
       {
       }

      oldDiff = ((newDiff != 0) ? newDiff : oldDiff);

      int[] curve = stave.getCurvePoints();

      /* Make sure we fill in the array of curve points to draw from.
       * Be sure not to add point outside the width of the stave, so
       * check to make sure the array is big enough to house the point.
       * Also, be sure to interpolate curve points between the mouse
       * events that fire, so we draw a continuous curve.
       */
      if( p.x < curve.length && p.x >= 0 )
       {
        curve[p.x] = p.y;

        if( p.x - startDragX > 1 )
         {
          for( int i = startDragX + 1; i < p.x; i++ )
           {
            curve[i] =
              curve[startDragX] + (i - startDragX) * (p.y - startDragY) / (p.x - startDragX);
           }
         }
        else
         {
          for( int i = startDragX - 1; i > p.x; i-- )
           {
            curve[i] =
              curve[startDragX] + (i - startDragX) * (p.y - startDragY) / (p.x - startDragX);
           }
         }

        startDragX = p.x;
        startDragY = p.y;
       }
      draggingNote = false;

     }
    catch( Exception ex )
     {
      ex.printStackTrace();
     }

    stave.repaint();
   }
  // otherwise use a movement cursor
  else
   {
    setCursor(moveCursor);
   }

  Trace.log(2, "point B");

  // lock the dragging the note to the x-axis direction
  if( (e.getX() > clickedPosX + 3 || e.getX() < clickedPosX - 3) && !lockDragging && !drawing )
   {
    Trace.log(2, "point B1");
    draggingPitch = false;
    lockDragging = true;
    draggingNote = true;

    dragMin = stave.getSelectionStart();
    dragMax = stave.getSelectionEnd();
   }
  // lock the dragging of the note's to it's pitch
  else if( (e.getY() > clickedPosY + 3 || e.getY() < clickedPosY - 3) && !lockDragging && !drawing )
   {
    Trace.log(2, "point B2");
    draggingNote = false;
    lockDragging = true;
    draggingPitch = true;

    // which direction?
   }

  // This is for repair of sub-divisions once dragging stops
  if( draggingNote )
   {

    int start = stave.getSelectionStart();
    int end = stave.getSelectionEnd();
    if( start < dragMin )
     {
      dragMin = start;
     }
    if( end > dragMax )
     {
      dragMax = end;
     }
   }

// if a beat bracket was clicked on
  if( selectedBeat != OUT_OF_BOUNDS && !drawing )
   {
    Trace.log(2, "point C");

    // increase the construction line resolution
    if( e.getX() > clickedPosX + 15 )
     {
      int newSubDivs = stave.incSubDivs(selectedBeat);

      // The maximum subdivisions can be 12 for now
      if( newSubDivs != stave.getSubDivs(selectedBeat)
        && newSubDivs <= 12 )
       {
        stave.setSubDivs(selectedBeat, newSubDivs);
        stave.repaint();
       }

      clickedPosX = e.getX();
     }

    // decrease the construction line resolution
    if( e.getX() < clickedPosX - 15 )
     {
      int newSubDivs = stave.decSubDivs(selectedBeat);

      // The minimum subdivisions is 1
      if( newSubDivs != stave.getSubDivs(selectedBeat) )
       {
        stave.setSubDivs(selectedBeat, newSubDivs);
        stave.repaint();
       }

      clickedPosX = e.getX();
     }

    return;
   }
  else if( selectedIndex != OUT_OF_BOUNDS // && stave.getSelectionStart() == selectedIndex //   && stave.getSelectionStart() == stave.getSelectionEnd()
    && stave.getMelodyPart().getNote(stave.getSelectionStart()) != null && draggingPitch && !drawing )
   {
    // dragging the selections's pitch

    if( e.getY() > lastDragY + VERTICAL_DRAG_THRESHOLD )
     {
      // Determine whether smart or simple transposition is to be used
      if( e.isAltDown() )
       {
        stave.transposeMelodyDownHarmonically();
       }
      else
       {
        stave.transposeMelodyDownSemitone();
       }
     }
    else if( e.getY() < lastDragY - VERTICAL_DRAG_THRESHOLD )
     {
      if( e.isAltDown() )
       {
        stave.transposeMelodyUpHarmonically();
       }
      else
       {
        stave.transposeMelodyUpSemitone();
       }
     }

    lastDragY = e.getY();

    Trace.log(2, "point D");

    MelodyPart part = stave.getMelodyPart();

    int index = stave.getSelectionStart();
    Note note = part.getNote(index);

    // calculate the new note pitch
    // int newPitch = yPosToKeyPitch(e.getY(), currentLine);

    // now done within within transpose above:   note.setPitch(newPitch);
    // so that notes state in same relative transposition

    // transposeMelody should do this now
    //note.setEnharmonic(notate.getScore().getCurrentEnharmonics(index));

    // store the note
    storedNote = note;

    stave.repaint();
    return;
   }
  // dragging the note's position
  else if( startingIndex != OUT_OF_BOUNDS && draggingNote )
   {

    Trace.log(2, "point E");
    if( nearestLine != OUT_OF_BOUNDS )
     {
      if( firstDrag == false )
       {
        firstDrag = true;
       }
      else
       {
        Trace.log(2, "undo command in StaveActionHandler");
        notate.cm.undo();
       }

      notate.cm.execute(new DragNoteCommand(stave.getMelodyPart(),
                                            startingIndex, nearestLine, true));

      Trace.log(2, "point K");
      //stave.setSelection(nearestLine);  Why?

      stave.repaint();
      return;
     }
   }
  else if( nearestLine != OUT_OF_BOUNDS && selectedIndex != OUT_OF_BOUNDS && draggingGroup )
   {
        //stave.setSelectionStart(stave.getNonRestSelectionStart());
        
        //stave.setSelectionEnd(stave.getNonRestSelectionEnd());
    // dragging a group of notes' position
    Trace.log(2, "point F");

    int pasteIndex = nearestLine - draggingGroupOffset;

    if( pasteIndex < 0 )
     {
      pasteIndex = 0;
     }
    
    // move the group of notes to the new section and select it
    if( pasteIndex < stave.getMelodyPart().size() )
     {
         // This replaces the last dragging command with the current one
         // so that when the user decides to undo the drag, it undoes it
         // all the way to the beginning of the drag, not just the last step.
      if( firstDrag == false )
       {
        firstDrag = true;
        draggingGroupOrigSelectionStart = stave.getNonRestSelectionStart();
        draggingGroupOrigSelectionEnd = stave.getSelectionEnd();
       }
      else
       {
        Trace.log(2, "undo command in StaveActionHandler");
        notate.cm.undo();
       }
      DragSetCommand DSC = new DragSetCommand(stave.getMelodyPart(),
                                           draggingGroupOrigSelectionStart,
                                           draggingGroupOrigSelectionEnd,
                                           pasteIndex);
      notate.cm.execute(DSC);
      stave.setSelection(pasteIndex,
                         pasteIndex + stave.getSelectionLength());

      stave.repaint();
     }
    else
     {
      System.out.println("Internal error with dragging group");
     }
   }

  // check for time signature change
  if( timeSelected )
   {
    // increase
    if( e.getY() < clickedPosY - 4 )
     {
      int top = stave.getMetre()[0];
      int bottom = stave.getMetre()[1];
      if( top + 1 > 12 && bottom < 8 )
       {
        stave.setMetre(1, bottom * 2);
       }
      else if( top + 1 > 12 && bottom == 8 )
       {
        stave.setMetre(12, 8);
       }
      else if( top + 1 < 1 && bottom > 1 )
       {
        stave.setMetre(1, bottom / 2);
       }
      else if( top + 1 < 1 && bottom == 1 )
       {
        stave.setMetre(1, 1);
       }
      else
       {
        stave.setMetre(top + 1, bottom);
       }
      clickedPosY -= 4;

      stave.repaint();
     }

    // decrease
    if( e.getY() > clickedPosY + 4 )
     {
      int top = stave.getMetre()[0];
      int bottom = stave.getMetre()[1];
      if( top - 1 < 1 && bottom > 1 )
       {
        stave.setMetre(12, bottom / 2);
       }
      else if( top - 1 < 1 && bottom == 1 )
       {
        stave.setMetre(1, 1);
       }
      else if( top - 1 > 12 && bottom < 8 )
       {
        stave.setMetre(1, bottom * 2);
       }
      else if( top - 1 > 12 && bottom == 8 )
       {
        stave.setMetre(12, 8);
       }
      else
       {
        stave.setMetre(top - 1, bottom);
       }
      clickedPosY += 4;

      stave.repaint();
     }
   }

  // check for key signature change
  if( keySelected )
   {
    int keySig = stave.getKeySignature();
    // increase
    if( e.getY() < clickedPosY - 4 )
     {
      if( stave.getKeySignature() < MAX_KEY )
       {
        stave.setKeySignature(keySig + 1);
       }
      clickedPosY -= 4;

      stave.repaint();
     }

    // decrease
    if( e.getY() > clickedPosY + 4 )
     {
      if( stave.getKeySignature() > MIN_KEY )
       {
        stave.setKeySignature(keySig - 1);
       }
      clickedPosY += 4;

      stave.repaint();
     }
   }
 }

void redoAdvice(int selectedIndex)
 {
  // checks to see if the advice frame is opened and the chord for
  // the selected index has changed or if the next chord is different

  //notate.redrawTriage(); // Needed?

  if( !ImproVisor.getShowAdvice() )
   {
    return;
   }
   /* nuisance requirement        if ( stave.oneSlotSelected() ) */ {

    /* If there is a slot selected, but the last click did not
    selected a new one (i.e. click between construction lines),
    do not try to give advice
     */
    if( selectedIndex < 0 )
     {
      return;
     }

    Chord currentChord =
      stave.getChordProg().getCurrentChord(selectedIndex);

    if( currentChord == null || currentChord.getName().equals(NOCHORD) )
     {
      ImproVisor.setShowAdvice(false);
      Notate window = ImproVisor.getCurrentWindow();
      if( window != null )
       {
        window.closeAdviceFrame();
       }

      notate.setStatus("To get advice, there must be a chord in effect.");

      return;
     }

    Chord currentNext =
      stave.getChordProg().getNextUniqueChord(selectedIndex);

    Note currentNote =
      stave.getMelodyPart().getNote(selectedIndex);
    /*
    Trace.log(2, "redoing Advice, note = " + currentNote + ", chord = " + currentChord);
    
    Trace.log(2, "checking redoAdvice at " + selectedIndex);
    Trace.log(2, "last chord: " + lastAdviceChord);
    Trace.log(2, "current chord: " + currentChord);
    Trace.log(2, "last next chord: " + lastAdviceNext);
    Trace.log(2, "current next chord: " + currentNext);
    Trace.log(2, "last advice note " + lastAdviceNote);
    Trace.log(2, "current advice note: " + currentNote);
    
     */

    if( ((lastAdviceChord == null)
      || (currentChord != null && !(currentChord.getName().equals(
      lastAdviceChord.getName())))) || ((lastAdviceNext == null)
      || (currentNext != null && !(currentNext.getName().equals(
      lastAdviceNext.getName())))) || ((lastAdviceNote == null)
      || (currentNote != null /* && !(lastAdviceNote.equals(currentNote) ) */)) )
     {
      int row = notate.adviceTree.getMaxSelectionRow();

      notate.displayAdviceTree(selectedIndex, 0, currentNote);
     }
    lastAdviceChord = currentChord;
    lastAdviceNext = currentNext;
    lastAdviceNote = currentNote;
   }
 }

/**
 * Mouse released
 * @param e
 */
public void mouseReleased(MouseEvent e)
 {
  Trace.log(2, "mouse released " + e + " in Stave");
  stave.requestFocusInWindow();

  if( draggingNote )
   {
    // Try setting the sub-divs small, to rectify any extraneous spaces introduced during drag.
    // This doesn't work so well. The display becomes jumpy.
    // setSubDivs(2, dragMin, dragMax);
    // System.out.println("dragMin = " + dragMin +", dragMax = " + dragMax);
   }


  // If we have changed the time signature, we need to update the time
  // signature of the score and set the length of the piece appropriately.
  if( timeSelected )
   {
    notate.initMetreAndLength(stave.getMetre()[0], stave.getMetre()[1], false);
   }

  if( draggingSelectionHandle )
   {
    draggingSelectionHandle = false;
    return;
   }

  int nearestLine = searchForCstrLine(e.getX(), e.getY());

  // select an individual construction line
  if( !shiftClicking && nearestLine == selectedIndex && !drawing )
   {

    stave.requestFocusInWindow();
    stave.repaint();
   }
  else if( selectingGroup && !timeSelected && !keySelected && !draggingPitch && !draggingNote && !draggingGroup && !drawing )
   // If the user is selecting a group of notes
   {
    Trace.log(2, "point G");
    endDragX = e.getX();
    endDragY = e.getY();

    // switch the x positions if selecting from right to left
    if( endDragX < startDragX )
     {
      int tempX = endDragX;
      endDragX = startDragX;
      startDragX = tempX;
     }

    boolean selectionStarted = false;
    int tempStart = OUT_OF_BOUNDS;
    int tempEnd = OUT_OF_BOUNDS;
    int endingWithTie = OUT_OF_BOUNDS;

    // Loop through all of the construction lines
    for( int i = 0; i < stave.cstrLines.length; i++ )
     {

      if( stave.cstrLines[i] != null )
       {
        // Find the starting index of the selection
        if( !selectionStarted && startDragY < stave.cstrLines[i].getY() + 80 && startDragY > stave.cstrLines[i].getY() - 80 && stave.cstrLines[i].getX() >= startDragX - 5 )
         {

          tempStart = i;
          selectionStarted = true;
         }

        // Find the ending index of the selection
        if( selectionStarted && endDragY < stave.cstrLines[i].getY() + 80 && endDragY > stave.cstrLines[i].getY() - 80 && stave.cstrLines[i].getX() <= endDragX )
         {

          tempEnd = i;
         }
       }
     }

    // if tempStart and tempEnd are valid
    if( tempStart != OUT_OF_BOUNDS && tempEnd != OUT_OF_BOUNDS && tempStart <= tempEnd )
     {
      Trace.log(2, "point I");

      stave.setSelection(tempStart, tempEnd);
      selectedIndex = stave.getSelectionStart();
     }

    stave.repaint();
   }

  if( drawing )
   {
    stave.clearCurvePoints();
    try
     {
      if( firedIndices.size() > 1 )
       {
        fitUnfiredNotes();
       }
     }
    catch( Exception j )
     {
     ErrorLog.log(ErrorLog.WARNING, j + ": Couldn't retrofit.");
      //j.printStackTrace();
     }

    firstIndexDrawn = lastIndexDrawn = OUT_OF_BOUNDS;
    firedIndices = new ArrayList<Integer>();

    return;
   }

  selectingGroup = false;
  shiftClicking = false;

  selectedBeat = OUT_OF_BOUNDS;
  selectedIndex = OUT_OF_BOUNDS;

  clickedOnCstrLine = false;
  clickedOnBracket = false;

  timeSelected = false;
  keySelected = false;

  draggingPitch = false;
  draggingNote = false;
  draggingGroup = false;
  lockDragging = false;
  firstDrag = false;

  lastIndexApproached = OUT_OF_BOUNDS;

  // update the menu and toolbar buttons
  notate.setItemStates();

  setCursor();
 }

/**
 * Will march through the drawing, determine which slots didn't fire during
 * the note addition phase (e.g. if you moved the mouse too quickly and the
 * mouseDragged didn't fire at the given index), and try to fit those to the
 * curve.  The current implementation deletes those notes repeatedly until
 * only the 'fired' slots contain notes, who durations may now be longer.
 * This works to 'fit' to the curve.  Another future variation may try to
 * re-insert proper pitches at those outlying slots.
 */
private void fitUnfiredNotes()
 {
  if( firedIndices.isEmpty() || firedIndices == null )
   {
    ErrorLog.log(ErrorLog.WARNING, "*** Warning: Trying to fit notes in an uninitialized or"
      + " empty curve.");
    return;

   }
  else
   {
    MelodyPart part = stave.getMelodyPart();

    /*
     * March through all the 'fired' indices of the drawing.  At each
     * step, determine whether this was the expected slot.  If it isn't,
     * there must be note(s) in between the indices that *didn't* fire.
     * Find these 'expected' slots and delete any current note in them.
     */
    for( int i = 0; i < firedIndices.size(); i++ )
     {
      int expectedSlot;
      int curSlot = firedIndices.get(i);
      int prevSlot = (i == 0 ? 0 : firedIndices.get(i - 1));

      // Did we draw left-to-right, or right-to-left?  This is important
      // in determining what the expected fired slot index is.
      boolean leftToRight = (curSlot - prevSlot) >= 0;

      if( i != 0 )
       {

        // While the current slot in the arrayList isn't the one we
        // expect, then compute expected slots.  Terminate the process
        // if somehow we get null notes from the part (I haven't been
        // able to figure out why this occurs, but I've seen it every
        // once in a while.)

        Note prevNote, curNote;
        while( (prevNote = part.getNote(prevSlot)) != null
          && (curNote = part.getNote(curSlot)) != null
          && curSlot
          != (expectedSlot =
          (leftToRight ? prevSlot + prevNote.getRhythmValue() : prevSlot - curNote.getRhythmValue())) )
         {

          // This only happens when right-to-left drawing, and deleting
          // the expected index won't change the expected indx we
          // compute.  It will nullify that note in the part, though,
          // so we play off that to find the next note.
          while( part.getNote(expectedSlot) == null )
           {
            expectedSlot--;
           }

          if( expectedSlot == 0 )
           {
            break;
           }

          // Delete the unit in the expected slot.
          try
           {
            notate.cm.execute(new DeleteUnitsCommand(stave.getMelodyPart(),
                                                     expectedSlot,
                                                     expectedSlot));
           }
          catch( Exception e )
           {
            //System.out.print("delete exception: ");
            e.printStackTrace();
           }
         }
       }
     }
   }
  stave.repaint();
 }

/**
 * Undo last action
 */
public void undo()
 {
  notate.undoMIActionPerformed(null);
 }

/**
 * Key pressed
 */
public void keyPressed(KeyEvent e)
 {
  //System.out.println("staveActionHandler keyPressed: " + (char)e.getKeyCode() );
  if( e.getKeyCode() != KeyEvent.VK_ENTER )
   {
    // Don't trace the shift key alone.
    Trace.log(3, "Key event: " + e);
   }
  
  if( stave.nothingSelected() )
    {
      stave.setSelection(0);
    }

  notate.resetAdviceUsed();

  int subDivs = 2;

  // Checks to see if a note or group of notes is selected
  if( stave.getSelectionStart() != OUT_OF_BOUNDS && stave.getSelectionEnd() != OUT_OF_BOUNDS )
   {

    /*
     * IMPORTANT:
     * Don't redo here stuff that has shortcuts/accelerators in Notate.
     * Otherwise it will get done TWICE
     * because Notate also gets these events, unless we explicitly disable.
     */

    if( e.isMetaDown() )
     {
      // This is done so that Mac command key behaves the same as control key.
      notate.controlDownBehavior(e);
     }
    else if( e.isControlDown() )
     {
      switch( e.getKeyCode() )
       {
        case KeyEvent.VK_SPACE:
          notate.toggleBothEnharmonics();
          return;
       }
     }
    else if( e.isShiftDown() )
     {
      switch( e.getKeyCode() )
       {
        case KeyEvent.VK_ENTER:
          // playSelection(); // Now handled by notate Key accelerator
          return;
        case KeyEvent.VK_Z:  // not effective?
          undo();
          return;
        case KeyEvent.VK_A:
          aPressed = true;
          return;
        case KeyEvent.VK_SPACE:        // toggle chord enharmonics
          notate.toggleChordEnharmonics();
          stave.repaint();
          return;
       }
     }
    else
     {
      switch( e.getKeyCode() )
       {
        // neither shift nor control

        /* Not wanted. Will duplicate work of accelerator:
        case KeyEvent.VK_Z:
        undo();
        return;
         */
        case KeyEvent.VK_ENTER:
            /* Done by accelerator in Notate
          notate.noCountIn();
          stave.playSelection(false, notate.getLoopCount(),
                              PlayScoreCommand.USEDRUMS);
             */
          return;
        case KeyEvent.VK_A:
          //if( notate.getMode() != Notate.Mode.DRAWING )
           {
            notate.moveLeft();
           }  // cursor motion
          return;
        case KeyEvent.VK_F:
          //if( notate.getMode() != Notate.Mode.DRAWING )
           {
            notate.moveRight();
           }
          return;
        case KeyEvent.VK_R:
//      Notate handles all of this, due to key accelerator
//          notate.cm.execute(new SetRestCommand(stave.getSelectionStart(),
//                                               stave.getMelodyPart()));
//          int index = notate.getCurrentSelectionStart();
//          int next = stave.getNextCstrLine(index);
//        
//          if (next >= 0)
//            stave.setSelection(next, next);
//
//          stave.repaint();

          return;
          
        case KeyEvent.VK_DELETE:                                   // same as x
        case KeyEvent.VK_BACK_SPACE:

          notate.cutBothMIActionPerformed(null);
          stave.repaint();
          return;

        case KeyEvent.VK_SPACE:        // toggle enharmonic

          notate.toggleMelodyEnharmonics();
          stave.repaint();
          return;

        case KeyEvent.VK_ESCAPE:        // unselect all
          notate.stopPlaying();
          stave.unselectAll();
          stave.repaint();
          return;
            
        case KeyEvent.VK_LEFT:
            notate.fileStepBackward();
            notate.staveRequestFocus();
            return;
            
        case KeyEvent.VK_RIGHT:
            notate.fileStepForward();
            notate.staveRequestFocus();
            return;

        default:                        // check for numeric keys
          handleGridLineSpacing(e);
       }
     }
    stave.requestFocusInWindow();
   }
 }

/**
 * Handle grid-line spacing short cuts.
 */
public void handleGridLineSpacing(KeyEvent e)
 {
  switch( e.getKeyCode() )
   {
    // Change the resolution of the a selected group of beats
    // 49 = VK_1, ... , 54 = VK_6, 56 = VK_8
    // 97 = VK_NUMPAD1, ... , 102 = VK_NUMPAD6, 104 = VK_NUMPAD8
    //
    // NOTE: Although it will set the subdivisions, when repainting if
    // the Stave notices that the subdivs are inadequate it will reset
    // them to a higher value


    case KeyEvent.VK_1:
    case KeyEvent.VK_NUMPAD1:

      setSubDivs(1);
      break;

    case KeyEvent.VK_2:
    case KeyEvent.VK_NUMPAD2:

      setSubDivs(2);
      break;

    case KeyEvent.VK_3:
    case KeyEvent.VK_NUMPAD3:

      setSubDivs(3);
      break;

    case KeyEvent.VK_4:
    case KeyEvent.VK_NUMPAD4:

      setSubDivs(4);
      break;

    case KeyEvent.VK_5:
    case KeyEvent.VK_NUMPAD5:

      setSubDivs(5);
      break;

    case KeyEvent.VK_6:
    case KeyEvent.VK_NUMPAD6:

      setSubDivs(6);
      break;

    /* Note: 7 is intentionally missing */

    case KeyEvent.VK_8:
    case KeyEvent.VK_NUMPAD8:

      setSubDivs(8);
      break;

    /* Note: 9 is intentionally missing */
   }
 }

/**
 * set every beat touched to the selected subdivisions
 */
void setSubDivs(int subDivs)
 {
  setSubDivs(subDivs, stave.getSelectionStart(), stave.getSelectionEnd());
 }

/**
 * set every beat touched to the subdivisions in a range
 */
void setSubDivs(int subDivs, int start, int end)
 {
  int beat = getBeatValue();
  for( int i = start; i <= end;
    i += beat )
   {
    stave.setSubDivs(i / beat, subDivs);
   }

  stave.repaint();
  
  StepEntryKeyboard stepKeyboard = notate.getCurrentStepKeyboard();
  
  if (stepKeyboard != null)
  {
      stepKeyboard.setSubDivComboBox();
  }
 }

/**
 * Key released
 */
public void keyReleased(KeyEvent e)
 {
  // update the menu and toolbar buttons
  notate.setItemStates();
  switch( e.getKeyCode() )
   {
    case KeyEvent.VK_A:
      if( e.isShiftDown() && aPressed )
       {
        aPressed = false;
       }
   }
 }

/**
 * Key typed
 */
public void keyTyped(KeyEvent e)
 {
 }

/**
 * Checks what line the y value corresponds to.
 *
 * @param y                 y-axis position
 * @return int              the line the mouse is currently on
 */
private int getCurrentLine(int y)
 {
  if( y < stave.headSpace - stave.lineOffset )
   {
    return -1;
   }

  int currLine = (y - (stave.headSpace - stave.lineOffset)) / stave.lineSpacing;

  return currLine;
 }

/**
 * Searches to see if the mouse has been clicked in the note area. The note
 * area consists of the width of the stave in the horizontal direction, and
 * a <code>lineSpacing</code> after the last stave line in the vertical
 * direction.
 *
 * @param e                 MouseEvent
 * @return boolean          true if the mouse is in the note area
 */
boolean inNoteArea(MouseEvent e)
 {
  // the maximum x
  int lastX = stave.STAVE_WIDTH;
  
  // the maximum y
  int lastY = stave.headSpace + ((stave.staveLine + 1) * stave.lineSpacing);

  int extraSpaceAtBottom = 40;
  
/* Not sure why this was needed.
 * // get the current line the mouse is on
  int currentLine = getCurrentLine(e.getY());



  // get the last slot index
  int i;
  if( stave.cstrLines.length != 0 )
   {
    i = stave.cstrLines.length - 1;
   }
  else
   {
    i = 0;
   }
  // a loop to find the last slot that holds a note
  if( currentLine == stave.staveLine && stave.cstrLines.length > 0 )
   {
    while( stave.cstrLines[i] == null )
     {
      i--;
     }
    lastX = stave.cstrLines[i].getX() + 50;
   }
*/
  
  boolean result =  (e.getX() >= stave.leftMargin)
                 && (e.getX() <= lastX) 
                 && (e.getY() < lastY + extraSpaceAtBottom);
  
  //System.out.println("inNoteArea = " + result );

  return result;
 }

/**
 * Searches to see if a construction line has been clicked on, returning the
 * selected slot if one has. The search algorithm looks in a small window of
 * pixels before and after the construction line in the x direction and 50
 * pixels above and below it's vertical midpoint.
 * <p>
 * The x-axis window is calculated by taking half of the construction line's
 * x-axis spacing (found using the getSpacing() method on the cstrLines
 * array) and subtracting 2 pixels from that value.
 * <p>
 * This allows the user to have a small amount of space (namely, 4 pixels),
 * in between each construction line that does not select anything. If that
 * space wasn't there, the sensitivity when selecting a construction line is
 * a little too low, and sometimes the user will select the wrong line by
 * accident.
 *
 * @param e             MouseEvent
 * @return int          the slot index corresponding to the construction
 *                      line clicked
 */
@SuppressWarnings("static-access")
private int searchForCstrLine(int x, int y)
 {
  int tempX;
  int tempY;

  // cycle throught the array of construction lines
  for( int i = 0; i < stave.cstrLines.length; i++ )
   {

    if( stave.cstrLines[i] != null )
     {
      tempX = stave.cstrLines[i].getX();
      tempY = stave.cstrLines[i].getY()
        - (stave.numPitchLines * stave.staveSpaceHeight) / 2;
      int xMargin = stave.cstrLines[i].getSpacing() / 2;
      int xLower = tempX - xMargin + 2;
      int xUpper = tempX + xMargin - 2;
      int yLower = tempY - stave.lineOffset;
      int yUpper = tempY - stave.lineOffset + stave.lineSpacing;

      // Had to change the x inequalities to non-strict so that we could click
      // at spacing 120. Without this the margins were too tight.

      // if it is within a small boundary in the x and y direction
      if( x >= xLower
        && x <= xUpper
        && y > yLower
        && y < yUpper )
       {
        // a construction line has been clicked on
        clickedOnCstrLine = true;

        // get out of loop ASAP
        return i;
       }
     }
   }

  return OUT_OF_BOUNDS;
 }

/**
 * Searches to see if a beat bracket has been clicked on, returning the
 * selected beat if one has. The user must click on or very close to the
 * bracket image in order to select it. The window in which the user can
 * click is specified in this function as well, and is set to be 5 pixels
 * above the bracket image and 5 pixels below the bracket image
 *
 * @param e             MouseEvent
 * @return int          the beat corresponding to the bracket clicked
 */
private int searchForBracket(MouseEvent e)
 {
  int tempX;
  int tempY;

  // cycle through the array of construction lines
  for( int i = 0; i < stave.cstrLines.length; i++ )
   {
    if( stave.cstrLines[i] != null )
     {
      tempX = stave.cstrLines[i].getX();
      tempY =
        stave.cstrLines[i].getY() + (stave.numPitchLines * stave.staveSpaceHeight) / 2 + 20;

      // if the mouse is clicked within the construction line's x
      // boundaries and the bracket's y boundaries
      if( e.getX() > tempX - (stave.cstrLines[i].getSpacing() / 2) && e.getX() < tempX + (stave.cstrLines[i].getSpacing() / 2) && e.getY() > tempY - 8 && e.getY() < tempY + 16 )
       {

        // a bracket has been clicked on
        clickedOnBracket = true;

        // update the clicked position
        clickedPosX = e.getX();
        clickedPosY = e.getY();

        // set the selected beat
        selectedBeat = i / getBeatValue();

        // get out of loop ASAP
        return i / getBeatValue();
       }
     }
   }

  return OUT_OF_BOUNDS;
 }

/**
 * Finds the measure that the mouse is currently over. Does this by finding
 * what construction line the mouse is closest to, and deducing the measure
 * from the construction line's index.
 *
 * @param e             MouseEvent
 * @return int          the current measure
 */
private int findMeasure(MouseEvent e)
 {
  // cycle throught the array of construction lines
  for( int i = 0; i < stave.cstrLines.length; i++ )
   {
    if( stave.cstrLines[i] != null )
     {

      int tempX = stave.cstrLines[i].getX();
      int tempY = stave.cstrLines[i].getY();

      // if the mouse is located next to the cstr line
      if( (e.getX() > tempX - stave.cstrLines[i].getSpacing()) && (e.getX() < tempX + stave.cstrLines[i].getSpacing()) && (e.getY() > tempY - 85) && (e.getY() < tempY + 85) )
       {

        // set the selected measure
        selectedMeasure = i / getMeasureLength();

        // get out of loop ASAP
        return selectedMeasure;
       }
     }
   }

  return OUT_OF_BOUNDS;
 }

/**
 * Finds the y-axis position for a given mouse position yPos and the current
 * line. First finds the y position's difference from middle C. It then
 * modulates the position by the amount of space between each octave (4
 * pitch lines) and uses if-then statements on the modulated difference to
 * find the given pitch. Adding the octaves back in and transposing the
 * note if necessary produces a correct pitch.
 * <p>
 * If the variable <code>staveSpaceHeight</code> ever changes, this method
 * will have to be altered as well.
 *
 * @param yPos          the current mouse position on the y-axis
 * @param currentLine   the current line the mouse is on
 * @return int          the pitch corresponding to the y-position
 */
private int yPosToPitch(int yPos, int currentLine)
 {

  // Subtract virtual offset if bass clef only

  if( stave.getStaveType() == StaveType.BASS )
   {
    yPos += bassOnlyOffset;
   }

  // Find the y-axis position for middle C

  int middleC =
    stave.headSpace + (currentLine * stave.lineSpacing) + ((10 * stave.staveSpaceHeight) / 2) - 1;

  // Find the difference between middle C position and the mouse position

  int yDif = middleC - yPos + verticalAdjustment;


  // Find the modulated y-difference. 28 is the number of pixels that
  // should be between each octave

  int modYDif =
    yDif % ((4 * stave.staveSpaceHeight) - (stave.staveSpaceHeight / 2));

  if( yDif < 0 )
   {
    modYDif =
      ((4 * stave.staveSpaceHeight) - (stave.staveSpaceHeight / 2)) + modYDif;
   }

  int index = (modYDif + 1) / 4;

  int pitch = pitchFromSpacing[index];

  // Get the octave difference between the modulated y difference and
  // the actual y difference
  int octaveDif = (int) Math.floor(yDif / 28) * 12;
  if( yDif < 0 )
   {
    octaveDif -= 12;
   }

  // Transpose the pitch by the difference in octaves
  pitch += octaveDif;

// System.out.println("yDif = " + yDif + ", modYDif = " + modYDif + ", index = " + index + ", unmodified pitch = " + pitch);

  return pitch;
 }

/**
 * Like yPosToPitch, but maps to all 12 pitches, rather than the 7 natural
 * tones.
 */
private int yPosToAnyPitch(int yPos, int currentLine)
 {

  // Subtract virtual offset if bass clef only

  if( stave.getStaveType() == StaveType.BASS )
   {
    yPos += bassOnlyOffset;
   }

  // Find the y-axis position for middle C

  int middleC =
    stave.headSpace + (currentLine * stave.lineSpacing) + ((10 * stave.staveSpaceHeight) / 2);

  // Find the difference between middle C position and the mouse position

  int yDif = middleC - yPos + verticalAdjustment;


  // Find the modulated y-difference. 28 is the number of pixels that
  // should be between each octave

  int modYDif =
    yDif % ((4 * stave.staveSpaceHeight) - (stave.staveSpaceHeight / 2));

  if( yDif < 0 )
   {
    modYDif =
      ((4 * stave.staveSpaceHeight) - (stave.staveSpaceHeight / 2)) + modYDif;
   }

  int index = (modYDif + 1) / 2;

  // HACK: I don't know why I get an out of bounds index, but this should
  // at least give me a guaranteed in-range value, should I get a bad one.
  if( index >= allPitchesFromSpacing.length )
   {
    index = allPitchesFromSpacing.length - 1;
   }
  else if( index < 0 )
   {
    index = 0;
   }

  int pitch = allPitchesFromSpacing[index];

  // Get the octave difference between the modulated y difference and
  // the actual y difference
  int octaveDif = (int) Math.floor(yDif / 28) * 12;
  if( yDif < 0 )
   {
    octaveDif -= 12;
   }

  // Transpose the pitch by the difference in octaves
  pitch += octaveDif;

// System.out.println("yDif = " + yDif + ", modYDif = " + modYDif + ", index = " + index + ", unmodified pitch = " + pitch);

  return pitch;
 }

/**
 * Given a y position on the staff and the context, finds the pitch that will
 * be input into the staff if rectification is turned on.
 * 
 * @param yPos
 * @param chord
 * @param staveLine
 * @param shiftDown
 * @return 
 */
private Note yPosToRectifiedPitch(int yPos, Chord chord, int staveLine, boolean shiftDown)
{

    // If there is no chord, we can't rectify the pitch
    if( chord == null || chord.getName().equals(NOCHORD) )
        return new Note(yPosToPitch(yPos, staveLine));

    ChordPart prog = stave.getChordProg();

    drawScaleTones = notate.getScaleTonesSelected();
    drawChordTones = notate.getChordTonesSelected();
    //drawColorTones = stave.notate.getColorTonesSelected();

    // Are approaches user-enabled?
    boolean approachEnabled = (aPressed && shiftDown);

    /* Is this index the one right before a chord change?  If it is, and
    * if we've enabled approaching with Shift-A, tag this as an
    * approach tone.
    */
    boolean apprch =
        ((selectedIndex + stave.getMelodyPart().getUnitRhythmValue(selectedIndex)
        == prog.getNextUniqueChordIndex(selectedIndex)) && approachEnabled);


    Chord nextChord = prog.getNextUniqueChord(selectedIndex);

    clearPasteFrom();

    // add new note close to mouse clicked pitch
    int pitch =
        (lastToneApproach && !apprch) ? 
            lastApproachPitch 
        : yPosToAnyPitch(yPos, staveLine);

    // adjust pitch to respect chord!

    ChordForm form = chord.getChordSymbol().getChordForm();

    String root = chord.getRoot();

    Polylist scaleTones = form.getFirstScaleTones(root);
    Polylist chordTones = form.getSpell(root);
    //Polylist colorTones = form.getColor(root);

    /* So far, the list of accpetable pitches to draw.
    * We want to disregard this in a moment if we're going to force
    * an approach tone.
    */
    Polylist m = new Polylist();
    if( drawScaleTones )
    {
        m = m.append(scaleTones);
    }
    if( drawChordTones )
    {
        m = m.append(chordTones);
    }
    if( drawColorTones )
    {
        // too liberal? m = m.append(colorTones);
    }

    if( apprch )
    {
        ChordForm nextForm = nextChord.getChordSymbol().getChordForm();

        // The list of lists of (chordTone approach1 approach2 etc)
        Polylist approachList = nextForm.getApproach(nextChord.getRoot());

        /* Build a list of approach tones to the next chord */
        Polylist tones = new Polylist();
        while( approachList.nonEmpty() )
        {
            tones = tones.append(((Polylist) approachList.first()).rest());
            approachList = approachList.rest();
        }

        /* If it isn't empty, use it */
        if( tones.nonEmpty() )
            m = tones;
    }

    // This must be a chord tone since it follows an approach tone.
    if( lastToneApproach && !apprch )
        m = chordTones;

    Note note = Note.getClosestMatch(pitch, m);

    return note;
}

/**
 * Moves the selected index to the right by one construction line
 * @param index             the index at which to start
 */
public void moveSelectionRight(int index)
 {
  while( index < stave.getMelodyPart().size() && stave.cstrLines[index] == null )
   {
    index++;
   }

  if( index < stave.getMelodyPart().size() && stave.cstrLines[index] != null )
   {
    stave.setSelection(index);
    stave.repaint();
   }
 }

/**
 * Moves the selected index to the left by one construction line
 * @param index             the index at which to start
 */
public void moveSelectionLeft(int index)
 {
  while( index >= 0 && stave.cstrLines[index] == null )
   {
    index--;
   }

  if( index >= 0 && stave.cstrLines[index] != null )
   {
    stave.setSelection(index);
    stave.repaint();
   }
 }

public void setCursor()
 {
     if (!notate.getUseNoteCursor())
     {
        setCursor(defaultCursor);
     }
}

public void setCursor(Cursor cursor)
 {
  //System.out.println("setCursor " + cursor);
  switch( notate.getMode() )
   {
    case NORMAL:
        stave.setCursor(cursor);
        break;
    case RECORDING:
      stave.setCursor(cursor);
      stave.clearNoteCursorLabel();
      break;
    case DRAWING:
      stave.setCursor(penCursor);
      stave.clearNoteCursorLabel();
      break;
   }
 }

public void setDrawScaleTones(boolean draw)
 {
  drawScaleTones = draw;
 }

public void setDrawChordTones(boolean draw)
 {
  drawChordTones = draw;
 }

// Returns the number of slots that a beat takes up in a given time signature.
// This basically scales 120 by the appropriate value (so 60 slots per beat for
// 6/8, etc.)

public int getBeatValue()
 {
  return WHOLE / stave.getMetre()[1];
 }

public int getMeasureLength()
 {
  return getBeatValue() * stave.getMetre()[0];
 }

public int getLastMeasureSelected()
{
    return lastMeasureSelected;
}

public void setLastMeasureSelected(int value)
{
    lastMeasureSelected = value;
}

public Cursor getNoteCursor()
{
    return noteCursor;
}

}
