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
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.roadmap.brickdictionary.Brick;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

/**
 * A panel containing a modifiable preview of a single brick
 * @author August Toman-Yih
 */
public class PreviewPanel extends JPanel
{
    /** Buffer for drawing */
    private Image buffer;
    
    /** GraphicBrick containing modified brick */
    public GraphicBrick currentBrick;
    
    /** The original, unmodified brick */
    private Block protoBrick;
    
    /** Key of the contents of the panel */
    private long currentKey = 0;
    
    /** Duration (of shortest chord) in the brick in the panel */
    private int currentDuration = 480;
    
    /** Parent RoadMapFrame */
    RoadMapFrame view;
    
    /** Required if we are going to make a bean from this. */
    
    public PreviewPanel()
     {
      
     }

    /**
     * Creates new form PreviewPanel
     * @param view 
     */
    public PreviewPanel(RoadMapFrame view)
    {
        this.view = view;        
    }
  
   /**
    * Override the paint method to draw the buffer image on this panel's graphics.
    * This method is called implicitly whenever repaint() is called.
    * @param g graphics on which to paint
    */
    @Override
    public void paint(Graphics g) 
    {
        g.drawImage(buffer, 0, 0, null);
    }
    
    /**
     * Draw contents.
     */
    public void draw()
    {
        view.setBackground(buffer);
        
        Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        
        g2d.setFont(view.getSettings().basicFont);
        
        if( currentBrick != null ) {
            currentBrick.drawAt(g2d, 1, 1);
        }
        repaint();
    }
    
    /**
     * Sets the buffer used for drawing
     * @param buffer 
     */
    public void setBuffer(Image buffer)
    {
        this.buffer = buffer;
    }
    
    /**
     * Sets the base brick and previews the desired length and key
     * @param brick base brick to preview and modify
     */
    public void setBrick(Block brick)
    {
        RoadMapSettings settings = view.getSettings();
              
        if (brick instanceof Brick) {
            protoBrick = new Brick((Brick)brick);
        }
        else if (brick instanceof ChordBlock) {
            protoBrick = new ChordBlock((ChordBlock)brick);
        }
        brick = normalizeBlock(brick);
        brick.scaleDuration(currentDuration);
        brick.transpose(currentKey);
            
        currentBrick = new GraphicBrick(brick, settings);
    }
    
    /**
     * Normalizes arbitrary-length blocks so they are displayed with
     * a length of 1 instead
     * @param block with possible arbitrary duration
     * @return a normalized block
     */
    private Block normalizeBlock(Block block) 
    {
        if (block instanceof ChordBlock) {
            if (block.getDuration() == 0) {
                block.setDuration(1);
            }
        }
        if (block instanceof Brick) {
            ArrayList<Block> subBlocks = block.getSubBlocks();
            for (Block b : subBlocks) {
                if (b instanceof Brick) {
                    b = normalizeBlock(b);
                }
                if (b.getDuration() == 0) {
                    b.setDuration(1);
                }
            }
        }
        return block;
    }
    
    /**
     * Returns GraphicBrick containing the modified brick
     * @return
     */
    public GraphicBrick getBrick()
    {
        return currentBrick;
    }
    
    /**
     * Returns the modifed brick
     * @return 
     */
    public Block getBlock()
    {
      if( currentBrick != null )
        {
        return currentBrick.getBlock();
        }
      return null;
    }
    
    /**
     * Modifies the key/root of the brick
     * @param key root of the key as offset from C
     */
    public void setKey(long key)
    {
        if(currentBrick != null)
            currentBrick.getBlock().transpose(currentKey - key);
        currentKey = key;
        draw();
    }
    
    /**
     * Modifies the key/root of the brick
     * @param key name of the root note of the key
     */
    public void setKey(String key)
    {
        long newKey = BrickLibrary.keyNameToNum(key);
        if(currentBrick != null)
            currentBrick.getBlock().transpose(newKey - currentKey);
        currentKey = newKey;
        draw();
    }
    
    /**
     * Modifies the duration of the brick
     * @param duration slots per shortest chord
     */
    public void setDuration(int duration)
    {
        currentDuration = duration;
        if(protoBrick != null)
            setBrick(protoBrick);
    }

}