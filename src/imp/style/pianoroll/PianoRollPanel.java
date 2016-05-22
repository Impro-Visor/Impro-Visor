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
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * PianoRollPanel is a JPanel extension for displaying
 * a piano roll editor using off-screen buffering.
 *
 * @author Robert Keller, Sayuri Soejima
 * June 2008
 */

public class PianoRollPanel extends JPanel 
             implements MouseListener, MouseMotionListener
  {
  int ENDBLOCK_QUANTIZATION = 480;
  int ENDBLOCK_WIDTH = 30;
  int FIRST_PERCUSSION_ROW = 2;

  private Image buffer;
  
  private PianoRollGrid grid;
  private PianoRoll pianoRoll;
  
  private ArrayList<PianoRollBar> bars = new ArrayList<PianoRollBar>();
  
  // Exists for the purpose of allowing copy/cut/paste for bars.
  private ArrayList<PianoRollBar> tempBars = new ArrayList<PianoRollBar>(); // not static!!
  
  private Color bgColor     = PianoRoll.BGCOLOR;
  private Color gridColor   = PianoRoll.GRIDCOLOR;
  private int numRows       = PianoRoll.NUMROWS;
  private int rowHeight     = PianoRoll.ROWHEIGHT;
  private int rowCushion    = PianoRoll.ROWCUSHION;
  private int tickHeight    = PianoRoll.TICKHEIGHT;
  private int slotDivisions = PianoRoll.SLOTDIVISIONS;
  private int beatsPerRow   = PianoRoll.BEATSPERROW;
  
  /**
   * The x- and y-coordinates of the mouse at the time of
   * the last significant event.
   */
  private int lastX;
  private int lastY;
  private PianoRollBar lastBar = null;
  private PianoRollBar currentBar = null;
  
  /**
   * The minimum number of pixels to be dragged.
   */  
  private int xDragMin = 1;
  private int movementAmplifier = 5; // So that bar roughly tracks drag position
  private int xStretchMin = 1;
  private int yDragMin = 10;

  /**
   * The different modes to separate different drags by the user, so that
   * the drags don't get confused or switches from one to the other mid-drag.
   */
  private static final int NONE = 0;
  private static final int DRAG = 1;
  private static final int RESIZE_RIGHT = 2;
  private static final int RESIZE_LEFT = 3;
  private int mode; // not static!!


  public PianoRollPanel()
  {
  }

  /**
   * Construct a PianoRollPanel
   */
  public PianoRollPanel(PianoRollGrid grid, PianoRoll pianoRoll) 
    {
    this.grid = grid;
    this.pianoRoll = pianoRoll;
    addMouseListener(this);
    addMouseMotionListener(this);
    }
  
  /**
   * Clear the array of bars.
   */
  public void clearBars()
    {
    bars.clear();
    }
  
 /**
  * Remove a particular bar from the array of bars.
  * @param bar - the bar that is to be removed
  */
 public void removeBar(PianoRollBar bar)
   {
   bars.remove(bar);
    pianoRoll.updatePlayablePercussion();
    //System.out.println("removed bar at row = " + bar.getRow() + ", startSlot = " + bar.getStartSlot() + ", slots = " + bar.getNumSlots());
   }
  
 /**
  * Add a particular bar to the array of bars.
  * @param bar - the bar that is to be added
  */
 public void addBar(PianoRollBar bar)
   {
   //System.out.println("added bar at row = " + bar.getRow() + ", startSlot = " + bar.getStartSlot() + ", slots = " + bar.getNumSlots());
   bars.add(bar);
   pianoRoll.updatePlayablePercussion();
   }

 /**
  * Add a particular bar to the array of bars.
  * @param bar - the bar that is to be added
  */
 public void addBarWithOptionalEndBlock(PianoRollBar bar)
   {
   int row = bar.getRow();
   
   PianoRollEndBlock endBlock = getEndBlockInRow(row);

   int position =
       row >= FIRST_PERCUSSION_ROW ? getCurrentPercussionEndBlockSlot()
       : (endBlock != null ? endBlock.getStartSlot()
       : 0);

   if( position == 0 || position < bar.getEndSlot() )
   {
   position = (1+(bar.getEndSlot()/ENDBLOCK_QUANTIZATION))*ENDBLOCK_QUANTIZATION;
   }


       // Add End Block if there isn't one there already, or move it if there is.
   if( !barExistsInRow(row, bars) )
   {
       endBlock = placeEndBlock(row, position);
   }
   else
   {
       endBlock = getEndBlockInRow(row);
       if( endBlock != null )
       {
           if( position > endBlock.getStartSlot() )
           {
           endBlock.setStartSlot(position);
           }
       }

       if( row >= FIRST_PERCUSSION_ROW )
       {
       // Reuses logic for dragging percussion bar

       }
   }

       if( row >= FIRST_PERCUSSION_ROW )
       {
       alignPercussionEndBlocks(bar, endBlock, row, position, position, true);
       }
   //System.out.println("added bar at row = " + bar.getRow() + ", startSlot = " + bar.getStartSlot() + ", slots = " + bar.getNumSlots());
   bars.add(bar);
   }

 /**
  * If the indicated row already has an endblock, move to position if position is
  * greater than its current position. If it has no endblock, put one at the position.
  @param row
  @param position
  @param bars
  */
 public PianoRollEndBlock placeEndBlock(int row, int position)
 {
 for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
     {
         PianoRollBar bar = e.next();
         if( (bar instanceof PianoRollEndBlock) && bar.getRow() == row)
         {
             // If there already is an end block, set its position
            bar.setStartSlot(Math.max(bar.getStartSlot(), position));
            return (PianoRollEndBlock)bar;
         }
     }

    PianoRollEndBlock endBlock = new PianoRollEndBlock(row, position, ENDBLOCK_WIDTH, pianoRoll);
    // If there is no end block, add one at the specified position
    bars.add(endBlock);
    return endBlock;
 }

 private static boolean barExistsInRow(int row, ArrayList<PianoRollBar> bars)
 {
     for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
     {
         if( e.next().getRow() == row )
         {
             return true;
         }
     }
     return false;
 }


 private PianoRollEndBlock getEndBlockInRow(int row)
 {
     for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
     {
         PianoRollBar bar = e.next();

         if( bar instanceof PianoRollEndBlock && bar.getRow() == row )
         {
             return (PianoRollEndBlock) bar;
         }
     }
     return null;
 }
  /**
   * Set the buffer of this PianoRollPanel.
   */
  public void setBuffer(Image buffer) 
    {
    this.buffer = buffer;
    }

  /**
   * override the paint method to draw the buffer image on this panel's graphics.
   * This method is called implicitly whenever repaint() is called.
   */
  @Override
  public void paint(Graphics g) 
    {
    g.drawImage(buffer, 0, 0, null);
    }
  
  /**
   * When the mouse is pressed, the X and Y coordinates are recorded, which
   * are put to use when the mouse is dragged.
   * If the mouse is pressed on top of a bar, set the right mode (resize, drag).
   * @param e
   */
  public void mousePressed(MouseEvent e) 
    {
    lastX = e.getX();  
    lastY = e.getY();

    if( !grid.inRange(lastX, lastY) )
    {
        currentBar = null;
        return;
    }
      
    // Set the mode so we know whether we're dragging
    // the edge (changing the size of the bar) or dragging the 
    // center (moving the bar), vertically or horizontally.
    PianoRollBar bar = findBar(lastX, lastY);    
        if (bar != null)
          {
            if (bar.inLeftMargin(lastX, lastY) && bar.isResizable() )  //left edge
              {
                mode = RESIZE_LEFT;
                currentBar = bar;
                //System.out.println("resize left");
              }
            else if (bar.inRIghtMargin(lastX, lastY) && bar.isResizable() )  //right edge
              {
                mode = RESIZE_RIGHT;
                currentBar = bar;
                //System.out.println("resize right");
            }
            else
              {
                mode = DRAG;
                currentBar = bar;
                //System.out.println("drag");
            }
            pianoRoll.updateBarEditor(bar);
          }
        else
          {
            mode = NONE;
            //System.out.println("none");
          }
      }

  public void mouseReleased(MouseEvent e) 
    {
    currentBar = null;
    pianoRoll.updatePlayablePercussion();
    }

  public void mouseEntered(MouseEvent e) 
    {
    }

  public void mouseExited(MouseEvent e) 
    {  
    }
  
  /**
   * Clicking the mouse brings up menus for bar actions. 
   * @param e
   *
   */
public void mouseClicked(MouseEvent e)
  {
    int x = e.getX();
    int y = e.getY();


    if( !grid.inRange(x, y) )
    {
        return;
    }

    //System.out.println("event = " + e);
    //System.out.println("clicked at x = " + x + ", y = " + y + ", finding " + findBar(x, y));

    // Saving the x and y coordinates of the click.
    pianoRoll.x = x;
    pianoRoll.y = y;


    if( e.getButton() == MouseEvent.BUTTON1 )
      {
        PianoRollBar bar = findBar(x, y);
        if( bar == null ) // clicked NOT on a bar
          {
            if( tempBars.size() > 0 )
              {
                pianoRoll.enablePasteBar();
              }
            else
              {
                pianoRoll.disablePasteBar();
              }
            pianoRoll.barCreatePopupMenu.show(this, x, y);
          }
        else // clicked on a bar
          {
            if( bar instanceof PianoRollEndBlock )
              {
                // Don't give menus for end blocks
                return;
              }
            if( bar.isSelected() )
              {
                pianoRoll.barEditPopupMenu.show(this, x, y);
              }
            else
              {
                pianoRoll.selectBar(bar);
              }

            if( bar instanceof PianoRollBassBar )
              {
                pianoRoll.updateBarEditor(bar);
              }
          }

      }
  }

  /**
   * Dragging the mouse on different parts of the bar either resizes
   * the bar or moves the bar around (horizontally and vertically).
   * However, the drags shouldn't allow the bars to end up on top of each other,
   * as there is collision-handling for the bars.
   * @param e
   */

public void mouseDragged(MouseEvent e)
  {
    int x = e.getX();
    int y = e.getY();

    PianoRollBar bar = findBar(lastX, lastY);

    // Mouse may have "slipped off" the bar.

    if( bar == null || bar != currentBar )
      {
        bar = currentBar;
      }

    if( bar != null )
      {
        pianoRoll.selectBar(bar);

        int oldStartSlot = bar.getStartSlot();
        int numSlots = bar.getNumSlots();

        // Note that oneTick is the number of Slots per tick mark

        int oneTick = PianoRoll.SLOTSPERBEAT / pianoRoll.getTicksPerBeat();
        int newStartSlot = 0;

        // Copy bar in case of collision with modified new bar

        PianoRollBar tempBar = bar.copy();

        long movement = pixelsToSlots(x - lastX)*movementAmplifier;

        //System.out.println("movement = " + movement + ", oneTick = "  + oneTick);

        int nearTick = 0;
        int newNumSlots = 0;

        switch( mode )
          {
            case RESIZE_LEFT: //left edge dragged
                  {
                    // Dragging on the left end of the bar,
                    // to change the size of the bar.

                    if( movement >= oneTick ) // left dragged right
                      {
                        lastX = x;
                        nearTick = nearestTick(oldStartSlot + oneTick, oneTick);
                        newNumSlots = oneTick * ((numSlots - oneTick) / oneTick);
                      }
                    else if( movement < -oneTick && oldStartSlot > 0 ) // left dragged left
                      {
                        lastX = x;
                        nearTick = nearestTick(oldStartSlot - oneTick, oneTick);
                        newNumSlots = oneTick * ((numSlots + oneTick) / oneTick);
                      }

                    if( newNumSlots > 0 )
                      {
                        bar.setStartSlot(nearTick);
                        bar.setNumSlots(newNumSlots);
                        if( collides(bar) )
                          {
                            // In case of collision, go back to previous bar
                            bars.remove(bar);
                            bars.add(tempBar);
                            currentBar = tempBar;
                          }
                      }
                  }
                break;

            case RESIZE_RIGHT:  //right edge dragged
                  {
                    // Dragging on the right end of the bar,
                    // to change the size of the bar.

                    if( movement >= oneTick ) // right dragged right
                      {
                        lastX = x;
                        nearTick = nearestTick(oldStartSlot + numSlots + oneTick,
                                               oneTick);
                        newNumSlots = oneTick * ((numSlots + oneTick) / oneTick);
                      }
                    else if( movement < -oneTick ) // right dragged left
                      {
                        lastX = x;
                        nearTick = nearestTick(oldStartSlot + numSlots - oneTick,
                                               oneTick);
                        newNumSlots = oneTick * ((numSlots - oneTick) / oneTick);
                      }

                    if( newNumSlots > 0 )
                      {
                        bar.setStartSlot(nearTick - newNumSlots);
                        bar.setNumSlots(newNumSlots);
                        if( collides(bar) )
                          {
                            // In case of collision, go back to previous bar
                            bars.remove(bar);
                            bars.add(tempBar);
                            currentBar = tempBar;
                          }
                      }
                  }
                break;

            case DRAG:
                  {
                    // Dragging on the midde of the bar, to move the bar
                    // but not change its size.

                    int halfLength = bar.getHalfLength();

                    boolean someMovement = false;

                        if( movement > oneTick )
                          {
                            someMovement = true;
                            lastX = x;
                            newStartSlot = nearestTick(pixelsToSlots(x-halfLength), oneTick);
                            bar.setStartSlot(newStartSlot);
                          }
                        else
                        if( movement <= -oneTick )
                          {
                            someMovement = true;
                            lastX = x;
                            newStartSlot = nearestTick(pixelsToSlots(pixelsToSlots(x-halfLength)) - 1, oneTick);
                            bar.setStartSlot(newStartSlot);
                          }

                    // Don't move if there will be a collision.
                    if( someMovement )
                      {
                        if( collides(bar) )// reset
                        {
                        bar.setStartSlot(oldStartSlot);
                        break;
                        }

                    int row = bar.getRow();

                    PianoRollEndBlock endBlock = getEndBlockInRow(row);

                    if( bar != endBlock && bar.getEndSlot() >= endBlock.getStartSlot() )
                      {
                       bar.setStartSlot(oldStartSlot);
                       break;
                      }

                    // Percussion end blocks need to be moved together

                    if( bar == endBlock && row >= FIRST_PERCUSSION_ROW )
                      {
                      alignPercussionEndBlocks(bar, endBlock, row, newStartSlot, oldStartSlot, x > lastX);
                      }
                    lastX = x;
                    }
                  }
                break;
          }

        drawAll(buffer.getGraphics());
        repaint();
      }

    lastY = y;
    lastBar = bar;
    pianoRoll.updateBarEditor(bar);
  }

public int computeHighMark(int highMark, PianoRollBar bar)
{
        for( Iterator<PianoRollBar> e1 = bars.iterator(); e1.hasNext(); )
          {
            PianoRollBar b = e1.next();
            if(  !(b instanceof PianoRollEndBlock) && b != bar && b.getRow() >= FIRST_PERCUSSION_ROW )
              {
                if( b.getEndSlot() > highMark )
                  {
                    highMark = b.getEndSlot();
                  }
              }
          }
   return highMark;
}

public int getCurrentPercussionEndBlockSlot()
  {
    int slot = 0;
    for( Iterator<PianoRollBar> e1 = bars.iterator(); e1.hasNext(); )
      {
        PianoRollBar b = e1.next();
        if( (b instanceof PianoRollEndBlock) && b.getRow() >= FIRST_PERCUSSION_ROW )
          {
            if( b.getStartSlot() > slot )
              {
                slot = b.getStartSlot();
              }
          }
      }
   return slot;
  }

public void alignPercussionEndBlocks(PianoRollBar bar, PianoRollEndBlock endBlock, int row, int newStartSlot, int oldStartSlot, boolean movingRight)
  {
        // see if we can move all end blocks
        int highMark = computeHighMark(bar.getStartSlot() - 1, bar);

        if( movingRight || highMark < newStartSlot )
          {

            for( Iterator<PianoRollBar> e2 = bars.iterator(); e2.hasNext(); )
              {
                PianoRollBar b = e2.next();
                if( b instanceof PianoRollEndBlock && b.getRow() >= FIRST_PERCUSSION_ROW )
                  {
                    b.setStartSlot(newStartSlot);
                  }
              }
          }
        else
          {
            bar.setStartSlot(oldStartSlot);
          }
  }

  public void mouseMoved(MouseEvent e) 
    {
    }

  /**
   * 
   * @param x - the x-coordinate at which we want to look for the bar
   * @param y - the y-coordiate
   * @return a Bar object at the coordiante x, y.
   */
  public PianoRollBar findBar(int x, int y) 
    {
    for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
      {
      PianoRollBar bar = e.next();
      if( bar != null && bar.contains(x, y) )
        return bar;
      } 
    return null; // not found
    }


  public PianoRollBassBar findBassBar(int x, int y)
  {
      PianoRollBar bar = findBar(x, y);
      if( bar == null )
      {
          return null;
      }

      if( bar instanceof PianoRollBassBar )
      {
          return (PianoRollBassBar) bar;
      }

      return null;
  }


  /**
   *
   * @param row - at which row to add the bar
   * @param col - at which column to add the bar
   * @param numSlots - how many slots the bar should represent
   * @return the Bar that is added the row and column specified, with the
   *         specified numSlots size.
   */
  public void addBar(int row, int startSlot, int numSlots, Object text, Color barColor, Color borderColor, int volume)
    {
    if( row >= numRows )
      {
        return; // can't add
      }
    //System.out.println("added bar at row = " + row + ", startSlot = " + startSlot + ", slots = " + numSlots);
    PianoRollBar bar = new PianoRollBar(row, startSlot, numSlots, text, barColor, borderColor, volume, true, grid, pianoRoll);
    addBarWithOptionalEndBlock(bar);
    }

  /**
   * 
   * @return The number of pixels that should be contained per beat, 
   *         by how many pixels each dotted vertical line should be spaced.
   */
  private int pixelsPerBeat() 
    {
    return pianoRoll.getPixelsPerBeat();
    }  

  /**
   * 
   * @param slots
   * @return the number of pixels equivalent to the slots number.
   */
  public int slotsToPixels(int slots) 
    {
    return (slots * pixelsPerBeat())/pianoRoll.SLOTSPERBEAT;
    }
  
  /**
   *
   * @param slots
   * @return the number of pixels equivalent to the slots number.
   */
  public int pixelsToSlots(int pixels)
    {
    return (pixels * pianoRoll.SLOTSPERBEAT)/pixelsPerBeat();
    }

  /**
   * Rounds off to the pixel value of the nearest tick, so that
   * the dragging, resizing, and adding of bars is locked to the tick divisions.
   * @param slots - the original slot value at which the bar was placed
   * @param slotsPerTick - the number of slots that one tick represents
   * @return The pixel value of where the bar *should* be.
   */
  public int nearestTick(int slots, int slotsPerTick)
    {
    int result = slotsPerTick * (slots/slotsPerTick); // integer division
    //System.out.println(slots + " --> " + result +"(" + slotsPerTick + " slots/tick)");
    return result;
    }
  
  /**
   * Draw the whole grid.
   * @param g
   */
  public void drawAll(Graphics g) 
    {
    grid.drawGrid(g, bgColor, gridColor);
    
    // Drawing all of the rows of the grid, as well as the slot division ticks
    for( int i = 0; i < numRows; i++ ) 
      {
      int y = (i * (rowHeight + rowCushion));
      grid.drawRow(g, gridColor, y, tickHeight, slotDivisions);
      }
    
    for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
      {
      e.next().draw(g);
      }

    repaint();
    }
  
  /**
   * 
   * @param bar
   * @return a bool that says whether or not the bar collides with another bar
   */
  public boolean collides(PianoRollBar bar)
  {
    for( Iterator<PianoRollBar> e = bars.iterator(); e.hasNext(); )
      {
      PianoRollBar that = e.next();
      if( that != bar && that.intersects(bar) )
      {
        return true; // collision detected
      }
      }
    return false;
  }
  
  /**
   * 
   * @return the vector of temporary bars, which hold the bars that have been
   *         cut or copied.
   */
  public ArrayList<PianoRollBar> getTempBars()
  {
    return tempBars;
  }
   
  /**
   * Adds a bar to tempBars, the vector of temporary bars.
   * @param bar - the temporary bar which we'd want to add 
   */
  public void addTempBar(PianoRollBar bar)
  {
    if(!tempBars.contains(bar))
      {
      tempBars.add(bar);
      }
  }

   /**
   *
   * @return Clone of the Vector containing all of the bars on the piano roll.
   */
  public ArrayList<PianoRollBar> getSortedBars()
    {
    ArrayList<PianoRollBar> barsCopy = (ArrayList<PianoRollBar>)bars.clone();
    Collections.sort(barsCopy, new PianoRollBarComparator());
    return barsCopy;
    }

  /**
   *
   * @return Clone of the Vector containing all of the bars on the piano roll.
   */
  public ArrayList<PianoRollBar> getSortedBarsInRow(int row)
    {
    ArrayList<PianoRollBar> barsInRow = new ArrayList<PianoRollBar>();
    
    for( PianoRollBar bar: bars )
      {
        if( bar.getRow() == row )
          {
            barsInRow.add(bar);
          }
      }
 
    Collections.sort(barsInRow, new PianoRollBarComparator());
    return barsInRow;
    }
  
  public PianoRollBar getPredecessor(PianoRollBar bar)
    {
      int row = bar.getRow();
      ArrayList<PianoRollBar> barsInRow = getSortedBarsInRow(row);
      PianoRollBar predecessor = null;
      for( PianoRollBar b: barsInRow )
        {
          if( b.equals(bar) )
            {
              return predecessor;
            }
          predecessor = b;
        }
      return null;
    }
  
  public int getImputedVolume(PianoRollBar bar)
    {
      int row = bar.getRow();
      ArrayList<PianoRollBar> barsInRow = getSortedBarsInRow(row);
      
      int volume = 127;
       PianoRollBar predecessor = null;
      for( PianoRollBar b: barsInRow )
        {
          if( !b.getVolumeImplied() )
            {
              volume = b.getVolume();
            }
          if( b.equals(bar) )
            {
              return volume;
            }
        }
      return volume;
    }
  
  /**
   * Gets the last element in the tempBars vector, which is the bar
   * that was last copied or cut.
   * @return the bar which was last copied/cut
   */
  public PianoRollBar getLastTempBar()
    {
    return tempBars.get(tempBars.size()-1);
    }
  
  /**
   * 
   * @return The vector containing all of the bars on the piano roll.
   */
  public ArrayList<PianoRollBar> getBars()
    {
    return bars;
    }
  
}

