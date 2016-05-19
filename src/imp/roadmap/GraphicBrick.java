/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2015-2016 Robert Keller and Harvey Mudd College
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

import java.awt.*;
import java.util.ArrayList;
import imp.brickdictionary.*;

/**
 * This class contains the graphical representation of a Block.
 * Contains positional information, selection, and drawing methods
 * @author August Toman-Yih
 */
public class GraphicBrick {
    /** contained block */
    private final Block block;
    /** is the block selected? */
    private boolean isSelected = false;
    /** which chord in the block is selected */
    private int selected = -1;
    /** slot offset on line in the roadmap*/
    private long slot = 0;
    /** line that block is on in the roadmap */
    private int line = 0;
    
    String margin = " ";
    
    private final RoadMapSettings settings;
    
    /**
     * Constructor to create a GraphicBrick from a block
     * @param block block to be graphically represented
     * @param settings settings of the current roadmap
     */
    public GraphicBrick(Block block, RoadMapSettings settings)
    {
        this.block = block;
        this.settings = settings;
    }
    
    /**
     * Returns the name of the brick
     * @return name
     */
    public String getName()
    {
        return block.getName();
    }
    
    /**
     * Returns the duration of the brick
     * @return duration
     */
    public int getDuration()
    {
        return block.getDuration();
    }
    
    /**
     * Returns the block
     * @return block
     */
    public Block getBlock()
    {
        return block;
    }
    
    /**
     * Sets the slot offset of the brick
     * @param slots horizontal offset, size defined by the settings
     */
    public void setSlot(long slots)
    {
        slot = slots;
    }
    
    /**
     * Sets the line offset of the brick
     * @param line vertical offset, size defined by the settings
     */
    public void setLine(int line)
    {
        this.line = line;
    }
    
    /**
     * returns the slot offset
     * @return slot offset
     */
    public long getSlot()
    {
        return slot;
    }
    
    /**
     * returns the line that the brick is on
     * @return line offset
     */
    public int getLine()
    {
        return line;
    }
    
    /**
     * returns the x position
     * @return the x position
     */
    public int x()
    {
        return settings.xOffset + settings.getLength((int)slot);
    }
    
    /**
     * returns the y position
     * @return the y position
     */
    public int y()
    {
        return settings.yOffset + settings.getLineOffset()*line;
    }

    /**
     * returns whether or not the brick is currently selected
     * @return whether the brick is selected
     */
    public boolean isSelected()
    {
        return isSelected;
    }
    
    /**
     * Sets whether the brick is selected
     * @param value selected or not
     */
    public void setSelected(boolean value)
    {
        isSelected = value;
        selected = -1;
    }
    
    /**
     * Selects the chord at the specified index
     * @param index 
     */
    public void selectChord(int index)
    {
        isSelected = true;
        selected = index;
    }
    
    /**
     * Get chord at specific index
     * @param index desired chord index
     * @return ChordBlock
     */
    public ChordBlock getChord(int index)
    {
        return block.flattenBlock().get(index);
    }
    
    /**
     * Returns the index of the selected chord (-1 if none are selected)
     * @return 
     */
    public int getSelected()
    {
        return selected;
    }
    
    /**
     * Checks if the point x,y is contained within the brick
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return if the point is within the brick
     */
    public boolean contains(int x, int y)
    {
        int cutoffLine = settings.getCutoff();
        
        if( x > cutoffLine || x < settings.xOffset )
            return false;
        
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = x();
        int yOffset = y();
        int ind = 0;

        for( ChordBlock chord : chords ) {
            int length = settings.getBlockLength(chord);
            
            if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + settings.lineHeight)
                return true;
            
            while ( xOffset + length >= cutoffLine )
            {
                xOffset -= settings.getLineLength();
                yOffset += settings.lineHeight + settings.lineSpacing;
                
                if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + settings.lineHeight)
                    return true;
            }
            
            xOffset += length;
            ind++;
        }
        
        return false;
    }
    
    /**
     * Returns the index of the chord at the specified point
     * @param x
     * @param y
     * @return 
     */
    public int getChordAt(int x, int y)
    {
        int cutoffLine = settings.getCutoff();
        int blockHeight = settings.getBlockHeight();
        
        if( x > cutoffLine || x < settings.xOffset )
            return -1;
        
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = x();
        int yOffset = y() + 2*blockHeight;
        int ind = 0;

        for( ChordBlock chord : chords ) {
            int length = settings.getBlockLength(chord);
            
            if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + blockHeight)
                return ind;
            
            while ( xOffset + length >= cutoffLine )
            {
                xOffset -= settings.getLineLength();
                yOffset += settings.lineHeight + settings.lineSpacing;
                
                if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + blockHeight)
                    return ind;
            }
            
            xOffset += length;
            ind++;
        }
        
        return -1;
    }
    
    /**
     * returns the pixel width of this block
     * @return 
     */
    public int getLength()
    {
        return settings.getBlockLength(block);
    }
    
    /* Drawing and junk lies below. DANGER: Extreme ugliness ahead */
    
    /**
     * Draws the brick at its current position
     * @param g graphics on which to draw the brick
     */
    public void draw(Graphics g)
    {
        // I split this up like this because drawing junk is a pain in the monk
        // - August
        drawBrick(g);
        drawChords(g);
    }
    
    /**
     * Draws the background and outline of the brick
     * @param g graphics on which to draw
     */
    private void drawBrick(Graphics g)
    {   
        Color bgColor;
        if(!isSelected || selected != -1)
            bgColor = settings.brickBGColor;
        else
            bgColor = settings.selectedColor;
        
        int blockHeight = settings.getBlockHeight();
        int cutoff = settings.getCutoff();
        
        int x = settings.xOffset + settings.getLength((int)slot);
        int y = settings.yOffset + settings.getLineOffset() * line;
        
        int[] wrap = settings.wrapFromSlots((int)slot+block.getDuration());
        int endX = settings.xOffset + settings.getLength(wrap[0]);
        long lines = wrap[1];
        
        if(endX == settings.xOffset) {  // This is to prevent the last line
            endX = cutoff;              // from being on the next line
            lines--;
        }
        
        int endY = y+(int)(lines*settings.getLineOffset());
        
        Graphics2D g2d = (Graphics2D)g;
        if( g2d == null )
          {
            // Can happen on Windows, not sure why.
            return;
          }
        g2d.setStroke(settings.brickOutline);
        g2d.setColor(bgColor);
        
        if(lines > 0) {
            g2d.fillRect(x, y+blockHeight, cutoff - x, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(x, y+blockHeight, x, y+3*blockHeight);
            g2d.drawLine(x, y+3*blockHeight, cutoff, y+3*blockHeight);
            
            for( int line = 1; line < lines; line++ ) {
                int currentY = y+line*settings.getLineOffset();
                g2d.setColor(bgColor);
                g2d.fillRect(settings.xOffset,
                        currentY + blockHeight,
                        settings.getLineLength(), 2*blockHeight);
                
                g2d.setColor(settings.lineColor);
                g2d.drawLine(settings.xOffset, currentY + 3*blockHeight,
                        cutoff, currentY + 3*blockHeight);
            }
            
            g2d.setColor(bgColor);
            g2d.fillRect(settings.xOffset,
                    endY + blockHeight,
                    endX-settings.xOffset, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(settings.xOffset, endY + 3*blockHeight,
                    endX, endY + 3*blockHeight);
            g2d.drawLine(endX, endY + blockHeight, endX, endY + 3*blockHeight);
        } else {
            g2d.fillRect(x, y+blockHeight, endX - x, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(x, y+blockHeight, x, y+3*blockHeight);
            g2d.drawLine(x, y + 3*blockHeight,
                    endX, y + 3*blockHeight);
            g2d.drawLine(endX, y + blockHeight, endX, y + 3*blockHeight);
        }
        if(block.getSectionEnd() == 2)
            g2d.setStroke(settings.basicLine);
        else
            g2d.setStroke(settings.brickOutline);
        
        if(block.isSectionEnd())
            g2d.drawLine(endX-3, endY + blockHeight, endX-3, endY + 3*blockHeight);
    }
    
    /**
     * Draws the chords of the brick, including lines and text.
     * @param g graphics on which to draw
     */
    private void drawChords(Graphics g)
    {
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
 
        int x = settings.xOffset + settings.getLength((int)slot);
        int y = settings.yOffset + (int)(line * settings.getLineOffset());
        
        int blockHeight = settings.getBlockHeight();
        int cutoff = settings.getCutoff();
        int xOffset = settings.xOffset;
        int yOffset = settings.yOffset;
        
        Graphics2D g2d = (Graphics2D)g;
        if( g2d == null )
          {
            // Can happen on Windows, not sure why.
            return;
          }
        FontMetrics metrics = g2d.getFontMetrics();
        int fontOffset = (blockHeight + metrics.getAscent())/2;
        long currentBeats = 0;
        
        
        g2d.setStroke(settings.basicLine);
        
        g2d.setColor(settings.lineColor);
        
        int ind = 0;
        
        for( ChordBlock chord : chords ) {
            //int[] wrap = settings.wrap(x + settings.getLength(currentBeats+1));
            int[] wrap = settings.wrapFromSlots((int)(slot+currentBeats));
            int currentX = xOffset+settings.getLength(wrap[0]);
            int currentY = yOffset + (int)((line + wrap[1]) * settings.getLineOffset()) + 2*blockHeight;
            
            int chordDur = Math.abs(chord.getDuration());
            // abs is used because * durations coded as negative
            int[] endWrap = settings.wrapFromSlots((int)(slot+currentBeats+chordDur));
            int endX = xOffset + settings.getLength(endWrap[0]);
            int lines = (int)(endWrap[1] - wrap[1]);
            
                if (lines > 0) {
                    if (selected == ind) {
                        g2d.setColor(settings.selectedColor);
                        g2d.fillRect(currentX + 1, currentY,
                                cutoff - currentX - 1, blockHeight - 1);
                        g2d.fillRect(xOffset, currentY + lines * settings.getLineOffset(),
                                endX - xOffset - 1, blockHeight - 1);

                        for (int line = 1; line < lines; line++) {
                            g2d.fillRect(xOffset, currentY + line * settings.getLineOffset(),
                                    cutoff - xOffset, blockHeight - 1);
                        }
                    }
                    g2d.setColor(settings.lineColor);
                    g2d.drawLine(currentX, currentY, cutoff, currentY);
                    g2d.drawLine(xOffset, currentY + lines * settings.getLineOffset(),
                            endX, currentY + lines * settings.getLineOffset());

                    for (int line = 1; line < lines; line++) {
                        g2d.drawLine(xOffset, currentY + line * settings.getLineOffset(),
                                cutoff, currentY + line * settings.getLineOffset());
                    }
                } else {
                    if (selected == ind) {
                        g2d.setColor(settings.selectedColor);
                        g2d.fillRect(currentX + 1, currentY + 1, endX - currentX - 1, blockHeight - 2);
                        g2d.setColor(settings.lineColor);
                    }
                    g2d.drawRect(currentX, currentY, endX - currentX, blockHeight);
                }

                g2d.setColor(settings.textColor);
                String name = RoadMapSettings.trimString(toSymbols(chord.getName()),
                        settings.getBlockLength(chord), metrics);
                name = RoadMapSettings.trimString(name, cutoff - currentX, metrics);
                try
                {
                g2d.drawString(margin + name, currentX + 2, currentY + fontOffset);
                }
                catch( Exception e )
                {
                    // Can happen on Windows. Not sure why.
                }

                currentBeats += chordDur;
                ind++;

        }
        
        if(block.isBrick() && settings.showBrickNames) {
            int totalLength = settings.getBlockLength(block);
            
            String name = block.getName();
            
            String variant = ((Brick)block).getVariant();
            if(!variant.isEmpty() && settings.showVariants)
                name += " ("+variant+")";
            
            name = RoadMapSettings.trimString(name,cutoff - x, metrics);
            name = RoadMapSettings.trimString(name,totalLength, metrics);
            
            g2d.setColor(settings.textColor);
            g2d.drawString(margin + name, x+2, y+blockHeight + fontOffset);
        }
    }
    
    
    /**
     * Draws the bricks at a specified location without wrapping
     * @param g graphics on which to draw
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void drawAt(Graphics g, int x, int y)
    {
        Graphics2D g2d = (Graphics2D)g;
        
        if( g2d == null )
        {
            return;
        }
        
        FontMetrics metrics = g2d.getFontMetrics();
        
        if(isSelected)
            g2d.setColor(settings.selectedColor);
        else
            g2d.setColor(settings.brickBGColor);

        int totalLength = settings.getBlockLength(block);
        int blockHeight = settings.getBlockHeight();
        
        g2d.fillRect(x, y, totalLength, settings.lineHeight);

        if( block.isBrick() ) {
            g2d.setColor(settings.getKeyColor((int)block.getKey(),block.getMode()));
        
            g2d.fillRect(x, y, totalLength, blockHeight);
            
            //Key
            g2d.setColor(settings.lineColor);
            g2d.drawRect(x, y, totalLength, blockHeight);
            if( settings.showKeys )
              {
                String key = RoadMapSettings.trimString(block.getKeyName()+" "+block.getMode(),
                        totalLength, metrics);
                g2d.setColor(settings.textColor);
                g2d.drawString(margin + key, x+5, y+blockHeight/2+5);
              }

            //Name
            if( settings.showBrickNames )
              {
                String name = RoadMapSettings.trimString(block.getName(), totalLength, metrics);
                String variant = ((Brick)block).getVariant();
                if(!variant.isEmpty() && settings.showVariants)
                    name += " ("+variant+")";
                g2d.setColor(settings.lineColor);
                g2d.drawRect(x, y+blockHeight, totalLength, blockHeight);
                g2d.setColor(settings.textColor);
                g2d.drawString(margin + name, x+5, y+3*blockHeight/2+5);
              }
        }
        
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
        
        int xOffset;

        long totalSlots = 0;
        
        for( ChordBlock chord : chords ) {    
            xOffset = settings.getLength((int)totalSlots);
            int chordDur = Math.abs(chord.getDuration());
            // abs is used, because * duration coded as negative
            int length = settings.getLength((int)totalSlots + chordDur) - 
                    xOffset;
            
            g2d.setColor(settings.lineColor);
            g2d.drawRect(x+xOffset, y+2*blockHeight, length, blockHeight);
            
            g2d.setColor(settings.textColor);
            String chordName = toSymbols(chord.getName());
            g2d.drawString(margin + chordName, x+xOffset+5, y+5*blockHeight/2+5);

            totalSlots += chordDur;
        }
        
        g2d.setStroke(settings.brickOutline);
        g2d.drawRect(x, y, totalLength, 3*blockHeight);
    }
    
    
    
    public String toSymbols(String chordName) {
        String result = chordName;
        if (settings.getDelta()) {
            result = toDelta(result);
        }
        if (settings.getPhi()) {
            result = toPhi(result);
        }

        if (result.length() == 0) {
            result = chordName;
        }

        return result;
    }

    public String toPhi(String chordName) {
        if (!contains(chordName, "m7b5")) {
            return chordName;
        } else {
            if (chordName.substring(0, 4).equals("m7b5")) {
                return "\u03D5" + toPhi(chordName.substring(4));
            } else if (chordName.substring(1, 5).equals("m7b5")) {
                return chordName.substring(0, 1) + "\u03D5" + toPhi(chordName.substring(5));
            } else if (chordName.substring(2, 6).equals("m7b5")) {
                return chordName.substring(0, 2) + "\u03D5" + toPhi(chordName.substring(6));
            } else if (chordName.substring(3, 7).equals("m7b5")) {
                return chordName.substring(0, 3) + "\u03D5" + toPhi(chordName.substring(7));
            } else {
                return chordName.substring(0, 4) + toPhi(chordName.substring(4));
            }
        }
    }

    public String toDelta(String chordName) {
        if (!contains(chordName, "M7")) {
            return chordName;
        } else {
            if (chordName.substring(0, 2).equals("M7")) {
                return "\u0394" + toDelta(chordName.substring(2));
            } else if (chordName.substring(1, 3).equals("M7")) {
                return chordName.substring(0, 1) + "\u0394" + toDelta(chordName.substring(3));
            } else {
                return chordName.substring(0, 2) + toDelta(chordName.substring(2));
            }
        }
    }

    public boolean contains(String chordName, String target) {
        for (int j = 0; j <= chordName.length() - target.length(); j++) {
            if (chordName.substring(j, j + target.length()).equals(target)) {
                return true;
            }
        }
        return false;
    }
}
