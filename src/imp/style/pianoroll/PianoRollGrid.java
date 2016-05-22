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

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Robert Keller, Sayuri Soejima
 * May-June 2008
 */
public class PianoRollGrid
  {
  static int barMargin = PianoRollBar.marginPixels;

    /**
     * Gap between bar ends
     */
  static int gap = 1;

  static int barNumberLabelRow[] = {9, 18, 27};


  int rows, slots, rowHeight, rowCushion;
  Color bgColor, gridColor;
  PianoRoll pianoRoll;
  int beatsPerRow = PianoRoll.BEATSPERROW;
  int slotsPerBeat = PianoRoll.SLOTSPERBEAT;

  int ymin = 0;

  int ymax;
  
  /**
   * Construct a grid.
   */

  public PianoRollGrid(int rows, int slots, int rowHeight, int rowCushion, PianoRoll pianoRoll) 
    {
    this.rows = rows;
    this.slots = slots;
    this.rowHeight = rowHeight;
    this.rowCushion = rowCushion;
    this.pianoRoll = pianoRoll;

    ymax = rows*(rowHeight + rowCushion);
    }

  public boolean inRange(int x, int y)
  {
      return x >= 0 && y >= ymin && y <= ymax;
  }


/**
 * Draw this PianoRollGrid on a Graphics, using specified colors.
 */
  
public void drawGrid(Graphics g, Color bgColor, Color gridColor)
  {
    int width = slotsToPixels(slots);
    int rowTotal = rowHeight + rowCushion;
    int height = rows * rowTotal;

    // Fill the background with the background color.
    g.setColor(bgColor);
    g.fillRect(0, 0, width, height);

    int pixelsPerBeat = pianoRoll.getPixelsPerBeat();

    int ticksPerBeat = pianoRoll.getTicksPerBeat();
    for( int i = 0; i <= beatsPerRow; i++ )
      {
        int x = i * ticksPerBeat * (pixelsPerBeat / ticksPerBeat);
        drawVerticalLine(g, gridColor, x);

        // Draw bar numbers on specified rows
        if( i >= 0 )
          {
            g.setColor(Color.black);
            for( int k = 0; k < barNumberLabelRow.length; k++ )
              {
                int labelY = barNumberLabelRow[k] * rowTotal;
                g.drawString("" + (i+1), x+1, labelY);
              }
          }
      }
  }


  /**
   * Draw a row on a Graphics, using specified colors and the widths.
   * Draws two horizontal lines that are rowHeight apart.
   */
  public void drawRow(Graphics g, Color gridColor, int y1, int tickHeight, int slotDivision) 
    {
    int ticksPerBeat = pianoRoll.getTicksPerBeat();
    int numTicks = ticksPerBeat * beatsPerRow;

    int pixelsPerBeat =  pianoRoll.getPixelsPerBeat();

    int pixelsPerTick = pixelsPerBeat/ticksPerBeat;

    int width = numTicks*pixelsPerTick;

    g.setColor(gridColor);
    
    // Draw the two horizontal lines that make up a row
    int y2 = y1 + rowHeight;
    g.drawLine(0, y1, width, y1);
    g.drawLine(0, y2, width, y2);    
    
    //Draw the ticks that represent the divisions in slots, which
    // can be customized by the user by the input into the jTextField.

    int upperTickTop = y1;
    int upperTickBottom = y1 + tickHeight;

    for (int j = 0; j < numTicks; ++j)
      {
      int x = j * pixelsPerTick;
      g.drawLine(x, upperTickTop, x, upperTickBottom);
      }
    }
  
  /**
   * Draw a vertical line on a Graphics, using specified colors,
   * the height, and the x-location of the line.
   */

  public void drawVerticalLine(Graphics g, Color lineColor, int x)
    {
    g.setColor(lineColor);
    int height = (rows * rowHeight) + (rows * rowCushion);

    g.drawLine(x, 0, x, height);
    }
  

  /**
   * @return the number of slots that each tick represents
   */
  private int getPixelsPerBeat() 
    {
    return pianoRoll.getPixelsPerBeat();
    }
 
  /**
   * Draw a bar on this grid, at a specified location, size, and color.
   * Size is specified by numSlots, in numbers of slots.
   */
  public void drawBar(Graphics g, Color barColor, Color borderColor, Color tabColor, int row, int startSlot, int numSlots)
    {
    g.setColor(barColor);

    int x = slotsToPixels(startSlot);
    int y = row * (rowHeight + rowCushion);

    int width = slotsToPixels(numSlots)-gap-gap;

    // Draw bar proper, leaving gaps around left and right ends.

    g.fillRect(x+gap, y, width, rowHeight);

    // Over-draw margins for adjusting bar.

    g.setColor(tabColor);

    // Draw left tab

    g.fillRect(x+gap, y, barMargin, rowHeight);

    // Draw right tab

    g.fillRect(x+gap+width-barMargin, y, barMargin, rowHeight);

    // Draw border around bar

    g.setColor(borderColor);
    g.drawRect(x+gap, y, width, rowHeight);
    }
 
 /**
  * 
  * @return the height of one row in the grid (not including the row cushions).
  */
  public int getRowHeight() 
    {
    return rowHeight;
    }
  
  /**
   * 
   * @param slots
   * @return the number of pixels equivalent to the slots number.
   */
  public int slotsToPixels(int slots) 
    {
    return (slots * getPixelsPerBeat())/slotsPerBeat;
    }

  /**
   * @return the number of slots in a row
   */
  
  public int getSlotsPerRow()
    {
    return slots;
    }
  
  
  /**
   * @return the number of rows
   */
  
  public int getRows()
    {
    return rows;
    }
}
