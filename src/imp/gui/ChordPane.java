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
package imp.gui;

import static imp.gui.VoicingKeyboard.OFFSETX;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * ChordPane is a class that holds one Panel in displayPane
 * and has a few methods useful for organizing the notes on the panel
 */
class ChordPane extends JPanel
  {

    boolean debug = false;

    private String chordName;
    private JLabel chordLabel;
    public JLabel[] notes;             //stores all notes displayed on panel
    public JLabel[] accidentals;       //stores all accidentals displayed on panel
    public JSeparator[] ledgerLines;   //contains all the ledger lines 
    public Graphics g;

    private static final int NOTE_VALUES = 128;

    private final java.awt.Rectangle chordLabelBounds
            = new java.awt.Rectangle(70, 0, 100, 100);

    private final java.awt.Rectangle chordPaneBounds
            = new java.awt.Rectangle(100, 10, 162, 370);

    private final java.awt.Dimension chordPaneDimension
            = new java.awt.Dimension(162, 370);

    public final javax.swing.ImageIcon wholeNoteIcon
            = new javax.swing.ImageIcon(
                    getClass().getResource("/imp/gui/graphics/wholeNote.gif"));

    static int ledgerLineWidth = 30;         // Width of ledger lines

    java.awt.Font chordLabelFont = new java.awt.Font("Arial", 1, 18);

    /**
     * constructor for ChordPane
     */
    public ChordPane(String chordName)
    {
        super();
        setChordName(chordName);
        notes = new JLabel[NOTE_VALUES];
        accidentals = new JLabel[NOTE_VALUES];
        ledgerLines = new JSeparator[17];
        setBounds(chordPaneBounds);
        setMaximumSize(chordPaneDimension);
        setMinimumSize(chordPaneDimension);
        setPreferredSize(chordPaneDimension);
        setDoubleBuffered(true);
        setOpaque(false);
        setLayout(null);
        g = getGraphics();
        setChordName(chordName);
    }

    public JLabel makeChordLabel(String chordName)
    {
        JLabel label = new JLabel(chordName);
        label.setForeground(Color.BLACK);
        label.setBounds(chordLabelBounds);
        label.setFont(chordLabelFont);
        label.setVisible(true);
        return label;
    }

    public void setChordName(String chordName)
    {
    if( chordLabel != null )
      {
        remove(chordLabel);
      }
    this.chordName = chordName;
    chordLabel = makeChordLabel(chordName);
    add(chordLabel);
    repaint();
    }

    /**
     * addNote adds the note to the panel and to the notes array
     *
     * @param MIDIvalue midi value of the note
     * @param label     the actual note being added
     */
    public void addNote(int MIDIvalue, JLabel label)
    {
    add(label);
    notes[MIDIvalue] = label;
    }

    /**
     * getNotes allows an outsider to access the notes array
     *
     * @return array of all notes in panel
     */
    public JLabel[] getNotes()
    {
    return notes;
    }

    /**
     * getAccidentals allows an outsider to access the accidental array
     *
     * @return array of all accidental in panel
     */
    public JLabel[] getAccidentals()
    {
    return accidentals;
    }

    /**
     * getLedgerLine allows an outsider to access the ledger lines array
     *
     * @return array of all ledger lines in panel
     */
    public JSeparator[] getLedgerLines()
    {
    return ledgerLines;
    }

    /**
     * getLedgerLine allows an outsider to access the ledger lines array
     *
     * @return ledgerLine with at specific index
     */
    public JSeparator getLedgerLine(int index)
    {
    return ledgerLines[index];
    }

    /**
     * addAccidental adds the accidental to the panel
     * and to the accidental array in the index at the same MIDI value
     *
     * @param MIDIvalue midi value of the note
     * @param label     the actual accidental being added
     */
    public void addAccidental(int MIDIvalue, JLabel label)
    {
    add(label);
    accidentals[MIDIvalue] = label;
    }

    /**
     * copyFrom clears this ChordPane, then copies all the data from
     * ChordPane p into this one
     *
     * @param p panel with all the data to move over
     */
    public void copyFrom(ChordPane p)
    {
    clear();
    chordName = p.chordName;
    chordLabel = makeChordLabel(chordName);
    add(chordLabel);
    for( int i = 0; i < notes.length; i++ )
      {
        notes[i] = p.notes[i];
        accidentals[i] = p.accidentals[i];
        if( notes[i] != null )
          {
            add(notes[i]);
          }
        if( accidentals[i] != null )
          {
            add(accidentals[i]);
          }
      }
    repaint();
    }

    /**
     * clears the panel and the arrays
     */
    public void clear()
    {
    removeAll();
    chordLabel = null;
    for( int i = 0; i < notes.length; i++ )
      {
        notes[i] = null;
        accidentals[i] = null;
      }
    repaint();
    }

    /**
     * removeNote removes the note from the panel and from the arrays
     *
     * @param MIDIvalue uses the MIDIvalue to determine what note to remove
     */
    public void removeNote(int MIDIvalue)
    {
    if( notes[MIDIvalue] == null )
      {
        //check to make sure there is something there to take out
      }
    else
      {
        remove(notes[MIDIvalue]);
        notes[MIDIvalue] = null;
      }
    if( accidentals[MIDIvalue] == null )
      {
        //check to make sure there is something there to take out
      }
    else
      {
        remove(accidentals[MIDIvalue]);
        accidentals[MIDIvalue] = null;
      }
    }

    public void drawLedgerLine(int xStart, int yStart)
    {
        if( g != null )
          {
          g.drawLine(xStart, yStart, xStart + ledgerLineWidth, yStart);
          }
    }

    public void clearLedgerLine(int xStart, int yStart)
    {
        g.clearRect(xStart, yStart, 0, 0);
    }

    /**
     * hasBorder check to see if there are notes around the note with that
     * midi value
     * if the Y offset is less than 6 away, then it will move it over
     *
     * @param MIDIvalue the note that we are going to compare to
     * @param offsetY   where on the staff it is
     * @return true if any note is around it
     */
    public boolean hasBorderY(int MIDIvalue, int offsetY)
    {
        //check the notes around it  
        for( int i = MIDIvalue - 2; i <= MIDIvalue + 2; i++ )
          {
            if( notes[i] != null && i != MIDIvalue )
              {
                int yPos = notes[i].getY();
                int differenceY = offsetY - yPos;
                if( Math.abs(differenceY) <= 6 && notes[i].getX() != OFFSETX[2] )
                  {
                    return true;
                  }
              }
          }
        return false;
    }

    /**
     * hasBorderX checks if there is a note already in the 3rd X spot
     *
     * @param MIDIvalue the note that we are going to compare to
     * @param offsetX   where on the staff it is
     * @return true if any note is around it
     */
    public boolean hasBorderX(int MIDIvalue, int offsetX)
    {
        //check the notes around it  
        int center = getWidth() / 2 - (wholeNoteIcon.getIconWidth() / 2);
        for( int i = MIDIvalue - 2; i <= MIDIvalue + 2; i++ )
          {
            if( notes[i] != null && i != MIDIvalue )
              {
                int xPos = notes[i].getX();
                if( xPos == center )
                  {
                    //make sure you aren't comparing to the center of the panel
                  }
                else
                  {
                    int differenceX = offsetX - xPos;
                    if( differenceX == 0 )
                      {
                        if( debug )
                          {
                            System.out.println("differenceX: " + differenceX);
                          }
                        return true;
                      }
                  }

              }
          }
        return false;
    }

    /**
     * isAccidental check to see if the note is an accidental
     *
     * @param MIDIvalue the note to check
     * @return true if accidental
     */
    public boolean isAccidental(int MIDIvalue)
    {
        return accidentals[MIDIvalue] != null;
    }

    public void setLedgerLine(JSeparator line, int index)
    {
        ledgerLines[index] = line;
        if( debug )
          {
            System.out.println("activated" + index);
          }
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        if( chordLabel != null )
          {
            buffer.append(chordLabel.getText());
            buffer.append(": ");
          }
        int index = 0;
        for( JLabel n : notes )
          {
            if( n != null )
              {
                buffer.append(index);
                buffer.append(" ");
              }
            index++;
          }

        return buffer.toString();
    }
  } // end class ChordPane
