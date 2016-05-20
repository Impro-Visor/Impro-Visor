/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011-2016 Robert Keller and Harvey Mudd College
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

package imp.roadmap;
import imp.roadmap.brickdictionary.BrickLibrary;
import imp.roadmap.brickdictionary.Brick;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.KeySpan;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.data.Chord;
import imp.data.Note;
import imp.data.PitchClass;
import imp.util.ErrorLog;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import javax.swing.JPanel;
import polya.*;

/** The panel where this roadmap is drawn. This class deals mostly with modifying
 * and drawing the roadmap.
 * @author August Toman-Yih
 */
public class RoadMapPanel extends JPanel {
    /** Number of lines in the roadmap (for grid drawing) */
    private int numLines = 1;
    
    /* HOW SELECTION IS IMPLEMENTED:
     * The indices of the first and last brick in the selection are kept of here
     * Within a brick, a data member keeps track of selection, and another keeps
     * track of which chord (if any) is selected.
     */
    
    /** DITTO_STYLE style name is not to be printed. */
   
    public static final String DITTO_STYLE = "*";
    
 
    /** Index of the start of selection (inclusive) */
    private int selectionStart = -1;
    
    /** Index of the end of selection (inclusive) */
    private int selectionEnd = -1;
    
    /** Index of the insertion line */ //TODO change to slot, maybe
    private int insertLineIndex = -1;
    
    /** Slot offset of the play line */
    private int playLineSlot = -1;
    
    /** Line of the play line */
    private int playLineLine = -1;
    
    /** Keeps track of the offset (in slots) of the playline (for when playback starts at a chord in the middle) */
    private int playLineOffset = 0;
    
    /** Index of the start of the play section */
    private int playSectionStart = 0;
    
    /** Index of the end of the play section */
    private int playSectionEnd = 0;
    
    /** Position of the rollover (prevents flickering during playback)*/
    private Point rolloverPos = null;
    
    /** Buffer for drawing */
    private Image buffer;
    
    /** Roadmap. Stores the chords and junk. */
    private RoadMap roadMap = new RoadMap();
   
    /** Graphic representation of the roadmap */
    private ArrayList<GraphicBrick> graphicMap = new ArrayList<GraphicBrick>();
    
    /** Section breaks list. Only used for keymapping. Possibly unideal. */
    private ArrayList<Long> sectionBreaks = new ArrayList<Long>();
 
    public ArrayList<Block> allBlocks = new ArrayList<Block>();
    
    /** Keeps track of graphical settings for the roadmap */
    RoadMapSettings settings;
    
    /** RoadMapFrame containing this panel */
    RoadMapFrame view;
    
    /** Playline in RoadMapPanel */
    Rectangle playline;
    
    String margin = "";
    
    Graphics g;
    
    Graphics2D g2d;
    
    /** Creates new form RoadMapPanel */
    protected RoadMapPanel(RoadMapFrame view)
    {
        this.view = view;
        settings = view.getSettings();
    }
    
    /** Returns the roadmap */
    protected RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    /** Sets the roadmap and reanalyzes */
    protected void setRoadMap(RoadMap roadMap)
    {
        //System.out.println("setting RoadMap and reanalyzing " + Formatting.prettyFormat(roadMap.toPolylist()));
        this.roadMap = roadMap;
        graphicMap = makeBricks(roadMap.getBlocks());
        roadMap.process();
    }
    
    public void rawSetRoadMap(RoadMap roadMap)
    {
        //System.out.println("setting RoadMap and reanalyzing " + Formatting.prettyFormat(roadMap.toPolylist()));
        removeBlocks();
        this.roadMap = roadMap;
        graphicMap = makeBricks(roadMap.getBlocks());
        rebuildRoadMap();
    }
    

    /** Returns the number of blocks in the roadmap */
    protected int getNumBlocks()
    {
        return roadMap.size();
    }
    
    /** Puts the bricks in the correct position onscreen based on sequence and line breaks */
    protected void placeBricks()
    {
        long currentSlots = 0;
        int lines = 0;
        long lineBeats = 0;
        
        sectionBreaks.clear();
        
        for( GraphicBrick brick : graphicMap ) {
            brick.setSlot(lineBeats);
            brick.setLine(lines);
            currentSlots += brick.getDuration();
            lineBeats += brick.getDuration();
            
            int[] wrap = settings.wrapFromSlots((int)lineBeats);
            lineBeats = wrap[0];
            lines += wrap[1];
            
            if(brick.getBlock().getSectionEnd() == Block.SECTION_END &&
                    lineBeats != 0) {
                lineBeats = 0;
                lines++;
                sectionBreaks.add(currentSlots);
            }
            
            if(lineBeats == 0 && brick == graphicMap.get(graphicMap.size()-1)) {
                lines--;
            }
        }
        
        numLines = lines+1;
        
        setPanelSize();
        
        draw();
    }
    
    /** Process and draw bricks */
    protected void updateBricks()
    {
        roadMap.process();
        draw();
    }
    
    /** Updates graphicMap to match the roadmap.
     * You shouldn't need to use this; using the other methods will keep them in sync*/
    protected void rebuildRoadMap()
    {
        graphicMap = makeBricks(roadMap.getBlocks());
        placeBricks();
    }
    
    /** Adds a block to the roadmap. */
    protected void addBlock(Block block)
    {
        addBlock(block,false);
    }
    /** Adds a block to the roadmap
     * @param block block to be added
     * @param selectBlocks Whether the block is selected after insertion
     */
    protected void addBlock(Block block, Boolean selectBlocks)
    {
        roadMap.add(block);
        graphicMap.add(new GraphicBrick(block, settings));
        if(selectBlocks)
          {
            selectBrick(roadMap.size() - 1);
          }
    }
    
    /** Adds a list of blocks to the roadmap */
    protected void addBlocks(ArrayList<Block> blocks)
    {
        addBlocks(blocks, false);
    }
    /** Adds a list of blocks to the roadmap
     * @param blocks blocks to be added
     * @param selectBlocks Whether the blocks are selected after insertion
     */
    
protected void addBlocks(ArrayList<Block> blocks, Boolean selectBlocks)
  {
    roadMap.addAll(blocks);
    graphicMap.addAll(makeBricks(blocks));

    if( selectBlocks )
      {
        selectBricks(roadMap.size() - 1);
      }
  }
    
    /** Adds a list of blocks at the specified position */
    protected void addBlocks(int ind, ArrayList<Block> blocks)
    {
        addBlocks(ind, blocks, false);
    }
    
    /** Adds a list of blocks at the specified position
     * @param ind Index to insert the blocks
     * @param blocks Blocks to be inserted
     * @param selectBlocks Whether the blocks are selected after insertion
     */
    protected void addBlocks(int ind, ArrayList<Block> blocks, Boolean selectBlocks)
    {
        roadMap.addAll(ind, blocks);
        graphicMap.addAll(ind, makeBricks(blocks));
        if(selectBlocks) {
            selectBricks(ind, ind + blocks.size() - 1 );
        }
    }
    
    /** Adds a list of blocks before the selection. Updates the selection */
    protected void addBlocksBeforeSelection(ArrayList<Block> blocks, Boolean selectBlocks)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            addBlocks(selectionStart, blocks, selectBlocks);
            if(!selectBlocks) {
                selectionStart+=blocks.size();
                selectionEnd+=blocks.size();
            }
        } else {
            addBlocks(blocks, true);
        }
    }
            
    /** Method that takes a list of blocks and creates GraphicBrick counterparts*/
    protected ArrayList<GraphicBrick> makeBricks(ArrayList<Block> blocks)
    {
        ArrayList<GraphicBrick> bricks = new ArrayList<GraphicBrick>();
        
        for( Block block : blocks )
          {
            bricks.add(new GraphicBrick(block, settings));
          }
        
        return bricks;
    }
    
    /** Method takes a list of GraphicBricks and gets the blocks contained within them*/
    protected ArrayList<Block> makeBlocks(ArrayList<GraphicBrick> bricks)
    {
        ArrayList<Block> blocks = new ArrayList<Block>();
        
        for( GraphicBrick brick : bricks )
          {
            blocks.add(brick.getBlock());
          }
        
        return blocks;
    }
    
    /** Changes the chord at the selection.
     * @param name Chord name
     * @param dur Chord duration
     */
    protected void changeChord(String name, int dur)
    {
        int chordInd = graphicMap.get(selectionStart).getSelected();
        Block block = removeSelection().get(0);
        ArrayList<Block> newBlocks = new ArrayList<Block>(block.flattenBlock());
        ChordBlock chord = (ChordBlock)newBlocks.get(chordInd);
        newBlocks.set(chordInd, new ChordBlock(name, dur));
        
        addBlocks(selectionStart, newBlocks);
        selectBrick(selectionStart + chordInd);
        placeBricks();
    }
    
    /** Returns whether the selection contains the brickInd */
    protected boolean isSelection(int ind)
    {
        return ind >= selectionStart && ind <= selectionEnd;
    }
    
    /** Returns a list of the selected blocks*/
    protected ArrayList<Block> getSelection()
    {
        if(graphicMap != null && !graphicMap.isEmpty() && selectionStart != -1 && selectionEnd != -1 ) {
            int chordInd = graphicMap.get(selectionStart).getSelected();
            if(chordInd != -1) {
                ArrayList<Block> block = new ArrayList<Block>();
                block.add(roadMap.getBlock(selectionStart).getChord(chordInd));
                return block;
            }
            return getBlocks(selectionStart, selectionEnd+1);
        }
        return new ArrayList<Block>();
    }
    
    /** Removes and returns the selected blocks*/
    protected ArrayList<Block> removeSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
          {
            return removeBlocks(selectionStart, selectionEnd+1);
          }
        return new ArrayList<Block>();
    }
    
    /** Removes and returns the selected blocks <b>without post processing</b>. */
    protected ArrayList<Block> removeSelectionNoUpdate()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = new ArrayList<Block>(roadMap.getBlocks().subList(selectionStart, selectionEnd+1));
            roadMap.getBlocks().subList(selectionStart, selectionEnd+1).clear();
            graphicMap.subList(selectionStart, selectionEnd+1).clear();
            return blocks;
        }
        return new ArrayList<Block>();
    }
    
    /** Returns the GraphicBrick at the specified brickInd
     * @param brickInd
     * @return 
     */
    protected GraphicBrick getBrick(int index)
    {
        return graphicMap.get(index);
    }
    
    /** Returns the Blocks between the two indices
     * @param start
     * @param end
     * @return 
     */
    protected ArrayList<Block> getBlocks(int start, int end)
    {
        return roadMap.getBlocks(start, end);
    }
    
    /** Removes and returns all blocks.*/
    protected ArrayList<Block> removeBlocks()
    {
        graphicMap.clear();
        return roadMap.removeBlocks();
    }
    
    /**
     * Removes and returns the blocks between the two indices
     * @param start
     * @param end
     * @return 
     */
    protected ArrayList<Block> removeBlocks(int start, int end)
    {
        ArrayList<Block> blocks = roadMap.removeBlocks(start, end);
        graphicMap.subList(start, end).clear();
        return blocks;
    }
    
    /**
     * Select a chord within a brick
     * @param brickInd
     * @param chordInd 
     */
    protected void selectChord(int brickInd, int chordInd)
    {
        selectBrick(brickInd);
        GraphicBrick brick = getBrick(brickInd);
        
        brick.selectChord(chordInd);
        draw();
    }
    
    /** Selects the brick at the specified brickInd with proper selection behavior.
     * <p> If the brickInd is outside of the current selection, extend the selection.
     * If not, just select that brick.
     */
    protected void selectBricks(int index)
    {
        if(selectionStart == -1 && selectionEnd == -1)
          {
            selectionStart = selectionEnd = index;
          }
        else {
            if(index < selectionStart) {
                getBrick(selectionEnd).setSelected(true);
                selectionStart = index;
            }
            else if (index > selectionEnd) {
                getBrick(selectionStart).setSelected(true);
                selectionEnd = index;
            }
            else {
                selectBrick(index);
            }
        }
        

        for(GraphicBrick brick : graphicMap.subList(selectionStart, selectionEnd + 1))
          {
            brick.setSelected(true);
          }
        
        draw();
    }
    
    /** Selects the bricks between the start and end indices, inclusive */
    protected void selectBricks(int start, int end)
    {
        deselectBricks();
        selectionStart = start;
        selectionEnd = end;
        for(int ind = start; ind <= end; ind++)
          {
            graphicMap.get(ind).setSelected(true);
          }
        draw();
    }
    
    /** Selects the brick at the specified brickInd. Deselects all other bricks. */
    protected void selectBrick(int index)
    {
        deselectBricks();
        if(index != -1) {
            selectionStart = selectionEnd = index;
            graphicMap.get(index).setSelected(true);
            draw();
        }
    }
    
    /** Selects all bricks */
    protected void selectAll()
    {
        if(!roadMap.isEmpty())
          {
            selectBricks(0, roadMap.size()-1);
          }

    }
    
    /** Deselects all bricks */
    protected void deselectBricks()
    {
        for(GraphicBrick brick : graphicMap)
          {
            brick.setSelected(false);
          }
        
        draw();
        
        selectionStart = selectionEnd = -1;
    }
  
    /** Returns true if there are bricks currently selected */
    protected boolean hasSelection()
    {
        return selectionStart != -1 && selectionEnd != -1;
    }
    
    /** Transpose all the bricks in the selection by the desired number of semitones */
    protected void transposeSelection(long diff)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBlocks(selectionStart, selectionEnd + 1))
              {
                block.transpose(diff);
              }
            roadMap.process();
            draw();
        }
        
    }
    
    /** Scale all bricks in the selection by the given amount */
    protected void scaleSelection(int scale)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBlocks(selectionStart, selectionEnd + 1))
              {
                block.scaleDuration(scale);
              }
            roadMap.process();
        }
        placeBricks();
    }
    
    /** Delete all bricks in the selection (or the chord if it is selected)*/
    protected void deleteSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            int chordSelected = graphicMap.get(selectionStart).getSelected();
            if(chordSelected != -1)
              {
                deleteChord(roadMap.getBlock(selectionStart), chordSelected);
              }
            else
              {
                deleteRange(selectionStart, selectionEnd+1);
              }
        }
        selectionStart = selectionEnd = -1;
    }
    
    /** Delete a chord within a brick */
    protected void deleteChord(Block block, int chordInd)
    {
        removeSelectionNoUpdate();
        ArrayList<Block> newBlocks = new ArrayList<Block>(block.flattenBlock());
        newBlocks.remove(chordInd);
        
        addBlocks(selectionStart, newBlocks);
        selectBrick(selectionStart + chordInd);
        placeBricks();
    }
    
    /** Delete all bricks within the two indices
     * @param start start brickInd (inclusive)
     * @param end end brickInd (exclusive)
     */
    protected void deleteRange(int start, int end)
    {
        roadMap.removeBlocks(start, end);
        graphicMap.subList(start, end).clear();
        placeBricks();
    }
    
    /** Replaces the selection with a collection of blocks */
    protected void replaceSelection(ArrayList<Block> blocks)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            removeSelection();
            addBlocks(selectionStart,blocks);
            placeBricks();
            selectBricks(selectionStart, selectionStart + blocks.size() - 1);
        }
    }
    
    /** Breaks the selected bricks into component bricks */
    protected void breakSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            allBlocks = getBlocks(0, roadMap.size());
            ArrayList<Block> blocks = getSelection();
            ArrayList<Block> newBlocks = new ArrayList<Block>();
            
            for( Block block : blocks )
              {
                newBlocks.addAll(block.getSubBlocks());
              }
             for ( Block b : newBlocks )
              {
                b.setStyleName(blocks.get(0).getStyleName());
              }
                 
            replaceSelection(newBlocks);
        }       
    }
    
    /** Copies chords of the selection to the text entry window */
    public String copySelectionToTextWindow() {
        String text = "";
        if (selectionStart != -1 && selectionEnd != -1) {
            Writer writer = new StringWriter();
            try {
                BufferedWriter out = new BufferedWriter(writer);
                Chord.initSaveToLeadsheet();
                Chord c;
                for (ChordBlock chord : roadMap.getChordsInRange(selectionStart, selectionEnd + 1)) {
//                    String chordText = chord.getName() + " " + chord.getDuration() + " | ";
//                    text += chordText;
                    c = chord.getChord().copy();
                    //System.out.println(c);
                    c.saveLeadsheet(out, view.getMetre(), false);
                    //Chord.flushChordBuffer(out, view.getMetre(), false, false);
                }
                out.close();
            } catch (IOException e) {
                ErrorLog.log(ErrorLog.SEVERE, "Internal Error");
            }
            text = writer.toString();
        }
        return text;
    }
    
    @Override
    public String toString()
    {
              return Formatting.prettyFormat(roadMap.toPolylist());
    }
    
    /**
     * Returns a Polylist representation of the Roadmap in this panel
     * @return a Polylist representation of the Roadmap in this panel
     */
    
    public Polylist getRoadmapPoly()
    {
              return roadMap.toPolylist();
    }
    
    /** Flattens the selected bricks to individual chords */
    protected void flattenSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> newBlocks = new ArrayList<Block>(RoadMap.getChords(getSelection()));
            replaceSelection(newBlocks);
        }
    }
    
    /** Makes the selection into a new brick with the given parameters
     * @param name
     * @param key
     * @param mode
     * @param type
     * @return the new brick
     */
    protected Brick makeBrickFromSelection(String name, String variant, long key, 
                                        String mode, String type)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = getSelection();
            ArrayList<Block> newBlock = new ArrayList<Block>();
            newBlock.add(new Brick(name, variant, key, type, blocks, mode));
            replaceSelection(newBlock);
            return (Brick)newBlock.get(0);
        }
        ErrorLog.log(ErrorLog.SEVERE, "Cannot create new brick from selection");
        return null;
    }
    
    /** Returns the brickInd of the brick containing the point(x,y)*/
    protected int getBrickIndexAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap) {
            if( brick.contains(x, y) )
              {
                return index;
              }
            index++;
        }
        return -1;
    }
    
    /** Returns the brickInd in the roadmap containing the point (x,y) */
    protected int getIndexAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap ) {
            if( brick.contains(x, y) )
              {
                return index;
              }
            
            if( y < brick.y() || x < brick.x() && y > brick.y() && y < brick.y() + settings.lineHeight )
              {
                return index;
              }
            
            index++;
        }
        
        return index;
    }
    
    /** Adds a section end to the end of the selection */
    protected void toggleSection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            int chordInd = graphicMap.get(selectionStart).getSelected();
            int blockInd = selectionStart;
            if(chordInd != -1) {
                ArrayList<Block> chords = new ArrayList<Block>(removeSelectionNoUpdate().get(0).flattenBlock());
                chords.get(chordInd).setSectionEnd(chords.get(chordInd).getSectionEnd() != 1);
                addBlocks(blockInd,chords);
                selectChord(blockInd+chordInd,0);
            } else
              {
                roadMap.getBlock(selectionEnd).setSectionEnd(roadMap.getBlock(selectionEnd).getSectionEnd()!=1);
              }
            roadMap.process();
            placeBricks();
            draw();
        }
    }
    
    /** Adds a phrase end to the end of the selection */
    protected void togglePhrase()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            int chordInd = graphicMap.get(selectionStart).getSelected();
            int blockInd = selectionStart;
            if(chordInd != -1) {
                ArrayList<Block> chords = new ArrayList<Block>(removeSelectionNoUpdate().get(0).flattenBlock());
                chords.get(chordInd).setPhraseEnd(chords.get(chordInd).getSectionEnd() != 2);
                addBlocks(blockInd,chords);
                selectChord(blockInd+chordInd,0);
            } else 
              {
                roadMap.getBlock(selectionEnd).setPhraseEnd(roadMap.getBlock(selectionEnd).getSectionEnd()!=2);
              }
            roadMap.process();
            placeBricks();
            draw();
        }
    }
    
    /** Sets the last brick to be a section end */
    protected void endSection()
    {
        roadMap.getBlock(roadMap.size()-1).setSectionEnd(true);
    }
    
    /** Sets the insertion line to the point (x,y) */
    protected void setInsertLine(int x, int y)
    {
        insertLineIndex = getIndexAt(x,y);
    }
    
    /** Sets the insertion line to the desired brickInd */
    protected void setInsertLine(int index)
    {
        insertLineIndex = index;
    }
    
    /** Sets the play section to the current selection */
    protected void setPlaySection()
    {
        playSectionStart = selectionStart;
        playSectionEnd = selectionEnd;
    }
    
    /** Sets the offset of the playline to the first brick */
    protected void setPlayLineOffset()
    {
        playLineOffset = getSlotFromIndex(selectionStart);
    }
    
    /** Sets the playline to the given slot */
    protected void setPlayLine(int slot)
    {
        int[] wrap = findLineAndSlot(slot + playLineOffset);
        playLineSlot = wrap[0]; //position in stave
        playLineLine = wrap[1]; //stave number
    }
    
    private int getSlotFromIndex(int ind)
    {
        int slot = 0;
        for(int i = 0; i < ind; i++)
          {
            slot += roadMap.getBlock(i).getDuration();
          }
        return slot;
    }
    
    private void setPanelSize()
    {
        if( buffer == null ) 
          {
            return;
          }
        int width = settings.getCutoff() + settings.xOffset;
        int height = settings.getLineOffset()*numLines + settings.yOffset;
        int maxWidth = buffer.getWidth(null);
        int maxHeight = buffer.getHeight(null);
        
        if(width > maxWidth)
          {
            width = maxWidth;
          }
        if(height > maxHeight)
          {
            height = maxHeight;
          }
        setPreferredSize(new Dimension(width, height));
        revalidate();
    }
    
    /** Returns the position of the given slot with line breaks
     * @param slots
     * @return a two element int array where the first element is the slot offset
     * and the second the line
     */
    private int[] findLineAndSlot(int slots)
    {
       int totalSlots = 0;
       int slotOffset = 0;
       int line = 0;
       
       for(GraphicBrick brick : graphicMap) {
           totalSlots += brick.getDuration();
           slotOffset = (int)brick.getSlot() + brick.getDuration() + slots - totalSlots;
           line = brick.getLine();
           
           if(slots < totalSlots)
             {
               break;
             }
       }
       int[] wrap = settings.wrapFromSlots(slotOffset);
       return new int[]{wrap[0],line + wrap[1]};
    }
    
    /** Sets the rollover to the given point */
    protected void setRolloverPos(Point point)
    {
        rolloverPos = point;
    }
    
    /** Returns a point containing the current position of the rollover */
    protected Point getRolloverPos()
    {
        return rolloverPos;
    }
    
    /* Drawing */
    
    /** Assign this panel a buffer */
    /* This could be called with buffer = null 
     * so as to allow buffer etc. to be collected. */
    protected void setBuffer(Image buffer)
    {
        this.buffer = buffer;
        if( buffer == null )
          {
          g = null;
          g2d = null;
           }
        else
          {
          g = buffer.getGraphics();
          g2d = (Graphics2D)g;    
          }
    }

    /** Draw all elements of the roadmap*/
    protected void draw()
    { 
      if( buffer == null )
        {
          return;
        }
 
       view.setBackground(buffer);
       drawGrid();
       drawText();
       drawBricks(settings.showJoins);
       if (settings.showStyles)
       {
            drawStyles();
       }
       if( settings.showKeys )
       {
         drawKeyMap();
       }
       if ( settings.showStartingNote)
       {
        drawStartingNote();
       }
       if(view.isPlaying()) {
           drawPlaySection();
           setPlayLine(view.getMidiSlot() - view.getMidiSlot()%(settings.slotsPerBeat/2));
           drawPlayLine();
       }
       drawRollover();
       repaint();
    }
    
//    /** Draws a cursor line of the desired color at the desired slot */
//    private void drawCursorLine(int slot, Color color)
//    {
//        int[] wrap = findLineAndSlot(slot);
//        drawCursorLine(wrap[0], wrap[1], color);
//    }
    
    /** Draws a cursor line of the desired color at the desired line/slot point */
    private void drawCursorLine(int slotOffset, int line, Color color)
    {
        //Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        g2d.setColor(color);
        g2d.setStroke(settings.cursorLine);
        
        int x = settings.getLength(slotOffset) + settings.xOffset;
        int y = line * settings.getLineOffset() + settings.yOffset;
        
        playline = new Rectangle(x,y-5,1,settings.lineHeight+10);      
        g2d.drawLine((int)playline.getX(),(int)playline.getY(),
                (int)playline.getX(),(int)playline.getY()+(int)(playline.getHeight()));
        
    }
    
    public Rectangle getPlayline()
    {
        return playline;
    }
    
    
    /** Draw the lines for the play section */
    private void drawPlaySection()
    {
        GraphicBrick startBrick = graphicMap.get(playSectionStart);
        GraphicBrick endBrick = graphicMap.get(playSectionEnd);
        
        Color color = settings.playSectionColor;
        
        drawCursorLine((int)startBrick.getSlot(), startBrick.getLine(), color);
        
        int[] wrap = settings.wrapFromSlots((int)endBrick.getSlot() + endBrick.getDuration());
        if(wrap[0] == 0 && wrap[1] > 0) { // TODO, maybe making a wrap method that doesn't wrap until it's over the edge?
            wrap[0] = settings.getSlotsPerLine();
            wrap[1]--;
        }
        
        drawCursorLine(wrap[0], wrap[1]+endBrick.getLine(), color);
    }
    
    /** Draw a playline */ 
    private void drawPlayLine()
    { 
        drawCursorLine(playLineSlot, playLineLine, settings.playLineColor);
        //Maybe we should use the draw cursor line method from slots instead of two data members
    }
    
    /** Draws the grid */
    private void drawGrid()
    {
      //Graphics2D g = (Graphics2D)buffer.getGraphics();

        for(int i = 0; i < numLines; i++) {
            g2d.setColor(settings.gridBGColor);
            g2d.fillRect(settings.xOffset,
                    settings.yOffset + i*(settings.lineHeight + settings.lineSpacing),
                    settings.getLineLength(), settings.lineHeight);
            
            for(int j = 0; j <= settings.barsPerLine; ) {
                g2d.setColor(settings.gridLineColor);
                g2d.drawLine(settings.xOffset + j*settings.measureLength,
                        settings.yOffset + i*(settings.lineHeight + settings.lineSpacing) - 5,
                        settings.xOffset + j*settings.measureLength,
                        settings.yOffset + (i+1)*settings.lineHeight + i*settings.lineSpacing + 5);

                // Draw double lines to make more prominent
                g2d.drawLine(settings.xOffset + j*settings.measureLength - 1,
                        settings.yOffset + i*(settings.lineHeight + settings.lineSpacing) - 5,
                        settings.xOffset + j*settings.measureLength - 1,
                        settings.yOffset + (i+1)*settings.lineHeight + i*settings.lineSpacing + 5);
                
                j++;
            }
        }
    }
    
    /** Draws the roadmap text (title, style, tempo, etc) */
    private void drawText()
    {
       //Graphics2D g = (Graphics2D)this.g; 
       g2d.setFont(settings.titleFont);
       g2d.setColor(settings.textColor);
       g2d.drawString(view.roadMapTitle, settings.xOffset, settings.yOffset - settings.lineSpacing);
       String composerName = view.getComposer(); 
       g2d.setFont(settings.basicFont);
       g2d.drawString( composerName, 
                     settings.xOffset, 
                     settings.yOffset - settings.lineSpacing / 2);
        /* 
        g.setFont(settings.basicFont);
        FontMetrics metrics = g.getFontMetrics();
        String text = view.style + " " + view.tempo + " bpm";
        int width = metrics.stringWidth(text);
        g2d.drawString(text,settings.getCutoff() - width, settings.yOffset - 5); 
        //g.drawString(text,settings.xOffset,settings.yOffset-5);
        */
        
    }
    
//    /** Draws the brick at the given brickInd */
//    private void drawBrick(int ind)
//    {
//        graphicMap.get(ind).draw(g); //buffer.getGraphics());
//        repaint();
//    }
    
    
    /** Draws all bricks */
    private void drawBricks(boolean showJoins)
    {        
        //Graphics2D g = (Graphics2D)buffer.getGraphics();
        g2d.setFont(settings.basicFont);
        
        ArrayList<String> joinList = roadMap.getJoins();
        ArrayList<String> styleList = view.getStyleNames();
        
        int yAdjust = 1;
        
        //Random r = new Random(System.nanoTime());
        for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);      
            //g.shear((r.nextDouble()-.5)/100, (r.nextDouble()-.5)/100);
            //g.setStroke(new BasicStroke(r.nextFloat()*4));
            
            int x = brick.x();
            int y = brick.y();
            
            brick.draw(g2d);
            
            if( showJoins )
              {
            
            if(ind < joinList.size() && !joinList.get(ind).isEmpty()) { //JOINS
                String joinName =  margin + joinList.get(ind) + margin;
                int length = settings.getBlockLength(brick.getBlock());
                
                FontMetrics metrics = g2d.getFontMetrics();
                
                int width = metrics.stringWidth(joinName) + 4;
                int offset = metrics.getAscent();
                
                int joinX = x + length - width - 4 - settings.xOffset;
                int joinY = y + joinX/settings.getLineLength() * settings.getLineOffset() +
                        settings.lineHeight;
                joinX = joinX%settings.getLineLength() + settings.xOffset;
        
                g2d.setColor(settings.joinBGColor);
                g2d.setStroke(settings.basicLine);
                
                g2d.fillRect(joinX+1,joinY+yAdjust, width, offset + 2);
        
                g2d.setColor(settings.lineColor);
                g2d.drawRect(joinX+1,joinY+yAdjust, width, offset + 2);
                
                g2d.setColor(settings.textColor);
                g2d.drawString(joinName,joinX+3, joinY+yAdjust+offset);
            }
              }
            
            if( ind == insertLineIndex ) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x, y-5, x, y+settings.lineHeight+5);
                g2d.setStroke(new BasicStroke(1));
            }
            

        }
        

        //drawKeyMap();
    }
    
    /** Draws the given bricks at the given point */
    protected void drawBricksAt(ArrayList<GraphicBrick> bricks, int x, int y)
    {
        //Graphics g = buffer.getGraphics();
        
        int xOffset = x;
        
        for(GraphicBrick brick : bricks) {
            brick.drawAt(g2d, xOffset, y);
            xOffset+=brick.getLength();
        }
    }

    /** Draws the keymap */
    private void drawKeyMap()
    {
        //Graphics g = buffer.getGraphics();
        
        long currentBeats = 0;
        long lines = 0;
        long lineBeats = 0;
        g2d.setFont(settings.basicFont);
        for( KeySpan keySpan : roadMap.getKeyMap() ) {
            drawKeySpan(keySpan, settings.xOffset + settings.getLength((int)lineBeats),
                    settings.yOffset + (int)(settings.getLineOffset() * lines));
            
            currentBeats += keySpan.getDuration();
            lineBeats += keySpan.getDuration();
            
            int[] wrap = settings.wrapFromSlots((int)lineBeats);
            lineBeats = wrap[0];
            lines += wrap[1];
            
            if(sectionBreaks.contains(currentBeats)) {
                lineBeats = 0;
                lines++;
                sectionBreaks.add(currentBeats);
            }
        }
            
    }
    
    /** Draws an individual keySpan */
    private void drawKeySpan(KeySpan keySpan, int x, int y)
    {       //TODO this should really be using beats and junk to find the end
            //Graphics2D g2d = (Graphics2D)g;
        
            g2d.setStroke(settings.brickOutline);
            
            int blockHeight = settings.getBlockHeight();
            FontMetrics metrics = g2d.getFontMetrics();
            int fontOffset = (blockHeight + metrics.getAscent())/2;
            long key = keySpan.getKey();
            String keyName = BrickLibrary.keyNumToName(key);
            PitchClass homeKey = settings.getRomanNumeralHomeKey();
            keyName = homeKey != null ? PitchClass.keyToRomanNumeral(keyName,homeKey) : keyName;
            keyName += " " + keySpan.getMode();
            long dur = keySpan.getDuration();
            int cutoff = settings.getCutoff();
            
            Color keyColor = settings.brickBGColor;
            
            if(key != -1)
              {
                keyColor = settings.getKeyColor(keySpan);
              }
            
            int[] wrap = settings.wrap(x+settings.getLength((int)keySpan.getDuration()));

            int endX = wrap[0];
            int lines = wrap[1];
            
            if(endX == settings.xOffset) {  // This is to prevent the last line
                endX = settings.getCutoff();// from being on the next line
                lines--;
            }
            int endY = y+lines*settings.getLineOffset();
            
            if(lines > 0) {
                
                if( settings.showKeys )
                  {
                  g2d.setColor(keyColor);
                  g2d.fillRect(x, y, cutoff - x, blockHeight);
                  g2d.fillRect(settings.xOffset, endY,
                          endX-settings.xOffset, blockHeight);
                  }
                
                g2d.setColor(settings.lineColor);
                g2d.drawLine(x,y,cutoff,y);
                g2d.drawLine(x,y+blockHeight,cutoff,y+blockHeight);
                
                g2d.drawLine(settings.xOffset, endY, endX, endY);
                g2d.drawLine(settings.xOffset, endY+blockHeight,
                                       endX, endY+blockHeight);
                for(int line = 1; line < lines; line++) {
                    g2d.setColor(keyColor);
                    g2d.fillRect(settings.xOffset, y+line*settings.getLineOffset(),
                            settings.getLineLength(), blockHeight);
                    
                    g2d.setColor(settings.lineColor);
                    g2d.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset(),
                            cutoff,
                            y+line*settings.getLineOffset());
                    g2d.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset() + blockHeight,
                            cutoff,
                            y+line*settings.getLineOffset() + blockHeight);
                }
            } else {
                if( settings.showKeys )
                  {
                  g2d.setColor(keyColor);
                  g2d.fillRect(x,y, endX - x, blockHeight);
                  }
                
                g2d.setColor(settings.textColor);
                g2d.drawLine(x,y,endX,y);
                g2d.drawLine(x,y+blockHeight,endX,y+blockHeight);
            }
            
            g2d.drawLine(endX, endY, endX, endY+blockHeight);
            g2d.drawLine(x, y, x, y+blockHeight);
            
            if( settings.showKeys )
              {
              g2d.setColor(settings.textColor);
              keyName = RoadMapSettings.trimString(keyName, cutoff - x, metrics);
              keyName = RoadMapSettings.trimString(keyName, settings.getLength((int)dur), metrics);
              //briankwak
              g2d.drawString(margin + keyName, x+2, y+fontOffset);
              }
    }
   private void drawStartingNote()
    { 
       g.setFont(settings.titleFont);
       g.setColor(Color.BLACK);
       
      Note firstNote = view.getFirstNote();
      if (firstNote != null)
      {
      
        g2d.drawString("Starting Note: " + firstNote.getPitchClassName(), 
                     settings.getLineLength()-settings.measureLength, 
                     settings.yOffset - settings.lineSpacing);
    }
        
    }
        
    /** Draws the rollover */
    private void drawRollover()
    {
        if(rolloverPos != null) {
            int x = rolloverPos.x;
            int y = rolloverPos.y;
            int brickInd = getBrickIndexAt(x,y);
            
            if(brickInd != -1) {
                int chordInd = graphicMap.get(brickInd).getChordAt(x, y);
                String text = roadMap.getBlock(brickInd).getName();
                
                if(chordInd != -1)
                  {
                    text = roadMap.getBlock(brickInd).getChord(chordInd).getName();
                  }

                //Graphics g = buffer.getGraphics();
                FontMetrics metrics = g.getFontMetrics();
                int width = metrics.stringWidth(text) + 4;
                int height = metrics.getAscent() + 2;

                g2d.setColor(settings.rolloverBGColor);

                g2d.fillRect(x, y, width, height);

                g2d.setColor(settings.lineColor);
                g2d.drawString(text, x+2, y+height - 2);
                g2d.drawRect(x, y, width, height);
                repaint();
            }
        }
    }
    
    /**
    * Override the paint method to draw the buffer image on this panel's graphics.
    * This method is called implicitly whenever repaint() is called.
    */
    @Override
    public void paint(Graphics g) 
    {
        g.drawImage(buffer, 0, 0, null);
    }
    
    public void reset()
      {
        removeBlocks();
      }
    
    public ArrayList<Block> getBlocks()
      {
        return roadMap.getBlocks();
      }
    
    public void drawStyles()
    {
        ArrayList<String> styleList = view.getStyleNames();
        int yAdjust = 1;
        int temp = 0;

       for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);  
            int x = brick.x();
            int y = brick.y();
            String styleName = "style";
            brick.draw(g2d);        
        
             
            if (ind == 0 || (ind > 0 && graphicMap.get(ind - 1).getBlock().isSectionEnd()))
                {
                    if (temp < styleList.size())
                    {
                
                    styleName = styleList.get(temp);
                    
                    String stylePrint = margin + styleName + margin;
                    
                    //int length = settings.getBlockLength(brick.getBlock());
                
                    FontMetrics metrics = g2d.getFontMetrics();
                
                    int width = metrics.stringWidth(stylePrint) + 4;
                    int offset = metrics.getAscent();
                
                    int styleX = settings.xOffset;
                    int styleY = y + styleX/settings.getLineLength() * settings.getLineOffset()
                      + settings.lineHeight - settings.getBlockHeight() * 4;
                     
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(settings.basicLine);
                
                    g2d.fillRect(styleX+1, styleY+yAdjust, width, offset + 2);
                
                    g2d.setColor(settings.lineColor);                    
                
                    g2d.setColor(settings.textColor);
                    if( !styleName.equals(DITTO_STYLE) )
                        g2d.drawString(stylePrint, styleX, styleY+yAdjust+offset);
                    temp++;
                    }
                }
            
        
        }
    }
}
