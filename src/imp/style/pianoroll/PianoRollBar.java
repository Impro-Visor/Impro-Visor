/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.style.pianoroll;

import imp.data.Note;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Robert Keller, Sayuri Soejima
 * June 2008
 */
public class PianoRollBar 
  {  
  int row;
  int startSlot;
  int numSlots;
  Object text;
  PianoRollGrid grid;
  PianoRoll pianoRoll;
  static int rowCushion = PianoRoll.ROWCUSHION;
  int margin;

  boolean selected = false;

  Color barColor, borderColor;

  Color tabColor = Color.black;

  public static int marginPixels = 5;

  static public String HIT_STRING = "X";

  protected boolean resizable = true;

  int volume = 127;
  
  boolean volumeImplied = true;

  public PianoRollBar(int row, 
                      int startSlot, 
                      int numSlots, 
                      Color barColor, 
                      Color borderColor, 
                      int volume,
                      boolean volumeImplied,
                      PianoRollGrid grid, 
                      PianoRoll pianoRoll)
    {
    this(row, startSlot, numSlots, "X", barColor, borderColor, volume, volumeImplied, grid, pianoRoll);
    }
  
  public PianoRollBar(int row, 
                      int startSlot, 
                      int numSlots, 
                      Object text, 
                      Color barColor, 
                      Color borderColor, 
                      int volume,
                      boolean volumeImplied,
                      PianoRollGrid grid, 
                      PianoRoll pianoRoll)
    {
    this.row           = row;
    this.startSlot     = startSlot;
    this.numSlots      = numSlots;
    this.text          = text;
    this.grid          = grid;
    this.pianoRoll     = pianoRoll;
    this.barColor      = barColor;
    this.borderColor   = borderColor;
    this.volume        = volume;
    this.volumeImplied = volumeImplied;
    }

/**
 * Copy constructor
 @param bar
 */

public PianoRollBar(PianoRollBar bar)
    {
    this.row           = bar.row;
    this.startSlot     = bar.startSlot;
    this.numSlots      = bar.numSlots;
    this.text          = bar.text;
    this.grid          = bar.grid;
    this.pianoRoll     = bar.pianoRoll;
    this.barColor      = bar.barColor;
    this.borderColor   = bar.borderColor;
    this.volume        = bar.volume;
    this.volumeImplied = bar.volumeImplied;
    }

/**
 * Copy is over-riddent in PianoRollBassBar
 @return
 */

public PianoRollBar copy()
{
    return new PianoRollBar(this);
}

public boolean isResizable()
{
    return resizable;
}

public void setSelected(boolean bit)
{
    selected = bit;
}

public boolean isSelected()
{
    return selected;
}

public void setVolumeImplied(boolean value)
  {
    volumeImplied = value;
  }

public boolean getVolumeImplied()
  {
    return volumeImplied;
  }

  /**
   * Draw the bar onto the grid.
   * @param g
   * @param color
   */
  public void draw(Graphics g) 
    {
    if( numSlots > 0 )
    {
       // For now, don't draw "invisible" bars, such as U and D for bass

    Color color = selected? PianoRoll.SELECTEDCOLOR : barColor;
    grid.drawBar(g, color, borderColor, getTabColor(), row, startSlot, numSlots);
    }
    }
    
  /**
   * Detect whether point x, y in Panel is within this PianoRollBar.
   @param x
   @param y
   @return
   */
  public boolean contains(int x, int y) 
    {
    int rowHeight = grid.getRowHeight();
    
    int pixelsPerBeat = pianoRoll.getPixelsPerBeat();
    int pixelsOfBar = slotsToPixels(numSlots);
    
    return     x >= slotsToPixels(startSlot)
            && x <= slotsToPixels(startSlot) + pixelsOfBar
            && y >= row * (rowHeight + rowCushion)
            && y <= (row + 1) * (rowHeight + rowCushion);
    }
  
  /**
   * Detect whether point x, y in Panel is within the left end of this PianoRollBar.
   * If so, resizing, rather than dragging is indicated.
   @param x
   @param y
   @return
   */
  public boolean inLeftMargin(int x, int y)
    {
    int rowHeight = grid.getRowHeight();

    int pixelsPerBeat = pianoRoll.getPixelsPerBeat();
    int pixelsOfBar = slotsToPixels(numSlots);

    return     x >= slotsToPixels(startSlot)
            && x <= slotsToPixels(startSlot) + marginPixels
            && y >= row * (rowHeight + rowCushion)
            && y <= (row + 1) * (rowHeight + rowCushion);
    }


  /**
   * Detect whether point x, y in Panel is within the left end of this PianoRollBar.
   * If so, resizing, rather than dragging is indicated.
   @param x
   @param y
   @return
   */
  public boolean inRIghtMargin(int x, int y)
    {
    int rowHeight = grid.getRowHeight();

    int pixelsPerBeat = pianoRoll.getPixelsPerBeat();
    int pixelsOfBar = slotsToPixels(numSlots);

    return     x >= slotsToPixels(startSlot) + pixelsOfBar - marginPixels
            && x <= slotsToPixels(startSlot) + pixelsOfBar
            && y >= row * (rowHeight + rowCushion)
            && y <= (row + 1) * (rowHeight + rowCushion);
    }


  /**
   * Detect whether this bar intersects another.
   */
  public boolean intersects(PianoRollBar other)
  {
    if( row != other.row )
    {
      return false;
    }
    int otherEndSlot = other.startSlot + other.numSlots;
    if( startSlot > other.startSlot && startSlot < otherEndSlot )
    {
      // Other overlaps on the left.
      return true;
    }
    
    int endSlot = startSlot + numSlots;
    if( endSlot > other.startSlot && endSlot < otherEndSlot )
    {
      // Other overlaps on the right.
      return true;
    }
    
    if( startSlot <= other.startSlot && endSlot >= otherEndSlot )
    {
      // Other is within this.
      return true;
    }
    if( other.startSlot <= startSlot && otherEndSlot >= endSlot )
    {
      // This is within other.
      return true;
    }
  return false;
  }
  
  /**
   * Convert the number of slots into the equivalent number of pixels.
   * @param slots
   * @return the number of pixels equivalent to the slots number.
   */  
  public int slotsToPixels(int slots) 
    {
    int pixelsPerBeat = pianoRoll.getPixelsPerBeat();
    return (slots * pixelsPerBeat)/pianoRoll.SLOTSPERBEAT;
    }
  
  /**
   * 
   * @return the value of the starting slot for the bar
   */
  public int getStartSlot() 
    {
    return startSlot;
    }
   
  /**
   *
   * @return the value of the starting slot for the bar
   */
  public int getEndSlot()
    {
    return startSlot + numSlots - 1;
    }

   /**
   * Sets the starting slot where the bar is placed.
   * @param startSlot - the slot number which you want to set as the start
   *                    of the bar.
   */
  public void setStartSlot(int startSlot) 
    {
    if( startSlot < 0 )
      {
      // Can't move off the left end.
      startSlot = 0;
      }
    else if( startSlot + numSlots > grid.getSlotsPerRow() )
      {
      // Can't move off the left end.
      startSlot = grid.getSlotsPerRow() - numSlots;
      }
    
    this.startSlot = startSlot;
    }
  
   /**
   *
   * @return the value of a slot approximately in the middle of the bar
   *  (used for dragging)
   */
  public int getMidSlot()
    {
    return startSlot + numSlots/2;
    }

   /**
   * Sets the starting slot where the bar is placed.
   * @param startSlot - the slot number which you want to set as the start
   *                    of the bar.
   */
  public void setMidSlot(int midSlot)
    {
    setStartSlot(midSlot - numSlots/2);
    }


/**
   * 
   * @return the number of the row at which the bar is placed.
   */
  public int getRow() 
    {
    return row;
    }
  
  /**
   * Sets the row at which the bar is placed.
   * @param row - the number of the row at which you now want to place the bar.
   */
  public void setRow(int row) 
    {
    if( row < 0 )
      {
      // Can't move off the top.
      this.row = 0;
      return;
      }
    if( row >= grid.getRows() )
      {
      // Can't move off the bottom.
      row = grid.getRows() - 1;
      }
    
   this.row = row;
    }
  
  /**
   * @return the x-coordinate of the left edge of the bar, in pixels
   */
  public int getX() 
    {
    return slotsToPixels(startSlot);
    }
     
  public int getHalfLength()
  {
      return slotsToPixels(numSlots)/2;
  }

  /**
   * Return the numSlots of the bar
   */
  public int getNumSlots() 
    {
    return numSlots;
    }
   
  /**
   * Return the text of the bar
   */
  public Object getText()
    {
    return HIT_STRING + Note.getDurationString(numSlots);
    }

  /**
   * Return the text of the bar
   */
  public void setText(Object text)
    {
    this.text = text;
    }

  /**
   * Set the width of the bar, in numSlots
   */
  public void setNumSlots(int numSlots) 
    {
    this.numSlots = numSlots;        
    }
   
  /**
   *
   * @return a string with information about the bar's location and width.
   */
  @Override
  public String toString() 
    {
    return "Bar at row = " + row + ", startSlot = " + startSlot + ", slots = " + numSlots;
    }

  public String getDuration()
  {
      return Note.getDurationString(numSlots);       // FIX!!!
  }

  /**
   * Intended to be over-ridden in PianoRollEndBlock
   @return
   */
  public Color getTabColor()
  {
      return tabColor;
  }
  
  public int getVolume()
    {
      return volume;
    }
  
  public void setVolume(int volume)
    {
      this.volume = volume;
    }
  }
