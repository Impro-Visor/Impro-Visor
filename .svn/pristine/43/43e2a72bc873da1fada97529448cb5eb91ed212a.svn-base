/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011-2013 Robert Keller and Harvey Mudd College
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

import imp.brickdictionary.Block;
import imp.brickdictionary.Brick;
import imp.brickdictionary.ChordBlock;
import imp.brickdictionary.KeySpan;
import imp.cykparser.PostProcessor;
import java.util.ArrayList;
import java.util.List;
//import polya.Formatting;
import polya.Polylist;
import polya.PolylistBuffer;

/**
 * This class contains the musical data of a roadmap, i.e. a sequence of blocks,
 * keys and joins.
 * @author August Toman-Yih
 */

public class RoadMap {
    public static String ROADMAP_KEYWORD = "roadmap";
    public static String BLOCKS_KEYWORD  = "blocks";
    public static String JOINS_KEYWORD   = "joins";
    public static String KEYMAP_KEYWORD  = "keymap";
    
    /** List of blocks contained in the roadmap */
    private ArrayList<Block> blocks;
    /** Key map in the form of key, duration pairs */
    private ArrayList<KeySpan> keyMap = new ArrayList<KeySpan>();
    /** List of joins between each brick */
    private ArrayList<String> joins = new ArrayList<String>();
    
    /**
     * Argumentless constructor that creates an empty roadmap.
     */
    public RoadMap()
    {
        blocks = new ArrayList<Block>();
    }
    
    /**
     * Constructs a roadmap from a list of blocks.
     * @param blocks 
     * list of blocks
     */
    public RoadMap(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }
    
    /**
     * Copy constructor
     * @param roadMap 
     */
    public RoadMap(RoadMap roadMap)
    {
        blocks = cloneBlocks(roadMap.getBlocks());
    }
    
    /**
     * Construct RoadMap from Polylist representation
     * @param polylist 
     */
//    public RoadMap(Polylist polylist)
//      {
//        blocks = new ArrayList<Block>();
//        System.out.println("Constructing RoadMap from saved information "); // + Formatting.prettyFormat(polylist));
//        // Get the list of blocks
//        Polylist found = polylist.assoc(BLOCKS_KEYWORD);
//        if( found == null || found.isEmpty() )
//          {
//            System.out.println("Unable to construct RoadMap from saved information");
//          }
//        else
//          {
//          Polylist blockList = found.rest();
//          //System.out.println("blockList = " + blockList);
//          while( blockList.nonEmpty() )
//            {
//            Polylist blockPolylist = (Polylist)blockList.first();
//            // Here we need to make a Block from blockPoly.
//            Block block = Block.fromPolylist(blockPolylist);
//            blocks.add(block);
//            blockList = blockList.rest();
//            }
//          process();
//          System.out.println("RoadMap constructed from saved information");
//          }
//      }
    
    public static RoadMap fromPolylist(Polylist polylist)
      {
        RoadMap roadmap = new RoadMap();
        roadmap.blocks = new ArrayList<Block>();
        //System.out.println("Constructing RoadMap from saved information "); // + Formatting.prettyFormat(polylist));
        // Get the list of blocks
        Polylist found = polylist.assoc(BLOCKS_KEYWORD);
        if( found == null || found.isEmpty() )
          {
            //System.out.println("Unable to construct RoadMap from saved information");
            return null;
          }
        Polylist blockList = found.rest();
        //System.out.println("blockList = " + blockList);
        while( blockList.nonEmpty() )
          {
          Polylist blockPolylist = (Polylist)blockList.first();
          // Here we need to make a Block from blockPoly.
          Block block = Block.fromPolylist(blockPolylist);
          roadmap.blocks.add(block);
          blockList = blockList.rest();
          }
        roadmap.process();
        //System.out.println("RoadMap constructed from saved information");
        return roadmap;
      }
    
    /**
     * Returns the number of blocks in the roadmap
     * @return the number of blocks in the roadmap
     */
    public int size()
    {
        return blocks.size();
    }
    
    /**
     * Returns whether the roadmap is empty
     * @return 
     */
    public boolean isEmpty()
    {
        return blocks.isEmpty();
    }
    
    /**
     * Returns the keymap.
     * @return 
     * Keymap
     */
    public ArrayList<KeySpan> getKeyMap()
    {
        return keyMap;
    }
    
    /**
     * returns the list of joins.
     * @return 
     * list of joins
     */
    public ArrayList<String> getJoins()
    {
        return joins;
    }
    
    /**
     * returns the block at the requested index
     * @param index index of the desired block
     * @return the block
     */
    public Block getBlock(int index)
    {
        return blocks.get(index);
    }
    
    /**
     * returns the list of bricks.
     * @return
     * list of bricks
     */
    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }
    
    /**
     * Returns the bricks between start and end (excluding end)
     * @param start 
     * start index
     * @param end
     * end index
     * @return  
     * list of bricks
     */
    public ArrayList<Block> getBlocks(int start, int end)
    {
        return new ArrayList<Block>(blocks.subList(start, end));
    }
    
    /**
     * Sets the blocks to b
     * @param b the new blocks
     */
    public void setBlocks(ArrayList<Block> b)
    {
        this.blocks = b;
    }
    
    /**
     * Sets the keymap to km
     * @param km the new keymap
     */
    public void setKeyMap(ArrayList<KeySpan> km) {
        this.keyMap = km;
    }
    
    /**
     * Sets the joins to j
     * @param j the joins
     */
    public void setJoins(ArrayList<String> j) {
        this.joins = j;
    }
    
    /**
     * removes and returns all bricks in the roadmap
     * @return 
     * bricks contained in the roadmap
     */
    public ArrayList<Block> removeBlocks()
    {
        ArrayList<Block> bricks = new ArrayList<Block>(blocks);
        blocks.clear();
        if( keyMap != null )
          {
            keyMap.clear();
          }
        if( joins != null )
          {
            joins.clear();
          }
        return bricks;
    }
    
    /**
     * removes and returns all bricks within a range
     * @param start
     * start index (inclusive)
     * @param end
     * end index (exclusive)
     * @return 
     * list of bricks
     */
    public ArrayList<Block> removeBlocks(int start, int end)
    {
        ArrayList<Block> bricks = new ArrayList<Block>(blocks.subList(start, end));
        blocks.subList(start, end).clear();
        process();
        return bricks;
    }
    
    /**
     * returns the flattened chords in the roadmap
     * @return 
     * list of chords
     */
    public ArrayList<ChordBlock> getChords()
    {
        return getChords(blocks);
    }
    
    /**
     * returns the flattened chords contained in a range
     * @param start
     * start index (inclusive)
     * @param end
     * end index (exclusive)
     * @return 
     * list of chords
     */
    public ArrayList<ChordBlock> getChordsInRange(int start, int end)
    {
        return getChords(blocks.subList(start, end));
    }
    
    /**
     * returns the chords contained in a list of blocks
     * @param blocks
     * blocks to get chords from
     * @return 
     * list of chords
     */
    public static ArrayList<ChordBlock> getChords(List<Block> blocks)
    {
        ArrayList<ChordBlock> chords = new ArrayList<ChordBlock>();
        for( Block block : blocks )
          {
            chords.addAll(block.flattenBlock());
          }
        return chords;
    }
    
    /**
     * Deep copies a list of blocks
     * @param blocks to be copied
     * @return resultant blocks
     */
    public static ArrayList<Block> cloneBlocks(ArrayList<Block> blocks)
    {
        ArrayList<Block> clones = new ArrayList<Block>();
        for( Block block : blocks ) {
            if( block instanceof Brick ) 
              {
                clones.add(new Brick((Brick)block));
              }
            if( block instanceof ChordBlock )
              {
                clones.add(new ChordBlock((ChordBlock)block));
              }
        }
        return clones;
    }
    
    /**
     * add a block to the end of the roadmap
     * @param block 
     * block to be added
     */
    public void add(Block block)
    {
        blocks.add(block);
        process();
    }
    
    /**
     * insert a block at the specified index
     * @param ind
     * where to insert the block
     * @param block 
     * block to be inserted
     */
    public void add(int ind, Block block)
    {
        blocks.add(ind, block);
        process();
    }
    
    /**
     * add a list of blocks to the end of the the roadmap
     * @param blocks 
     * blocks to be added
     */
    public void addAll(List<Block> blocks)
    {
        this.blocks.addAll(blocks);
        process();
    }
    
    /**
     * insert a list of blocks at the specified index
     * @param ind
     * where to insert the blocks
     * @param blocks 
     * blocks to be inserted
     */
    public void addAll(int ind, List<Block> blocks)
    {
        this.blocks.addAll(ind, blocks);
        process();
    }
    
    /**
     * Updates the keymap and join list.
     */
    public void process()
    {
        joins = PostProcessor.findJoins(blocks);
        RoadMap newMap = PostProcessor.findKeys(this);
        this.setBlocks(newMap.getBlocks());
        this.setKeyMap(newMap.getKeyMap());
    }
    
    
    /**
     * The String representation of a RoadMap is String version of
     * the Polylist version.
     * @return 
     */
    
    @Override
    public String toString()
    {
        return toPolylist().toString();
    }
  
   /**
     * The Polylist representation of a RoadMap captures the essential
     * elements as a single polylist. It includes the blocks,
     * joins, and keymap in that order.
     * @return 
     */
    
    public Polylist toPolylist()
      {
        PolylistBuffer buffer = new PolylistBuffer();
        
        buffer.append(ROADMAP_KEYWORD);
        
        PolylistBuffer innerBuffer = new PolylistBuffer();
        
        for( Block b: blocks )
          {
            innerBuffer.append(b.toRoadmapSave());
          }
        
        buffer.append(innerBuffer.toPolylist().cons(BLOCKS_KEYWORD));
        
//        innerBuffer = new PolylistBuffer();
//        
//        for( String s: joins )
//          {
//            innerBuffer.append(Brick.dashed(s));
//          }
//        
//        buffer.append(innerBuffer.toPolylist().cons(JOINS_KEYWORD));
//        
//        innerBuffer = new PolylistBuffer();
//        
//        for( KeySpan k: keyMap )
//          {
//            innerBuffer.append(k.toPolylist());
//          }
//        
//        buffer.append(innerBuffer.toPolylist().cons(KEYMAP_KEYWORD));
  
        return buffer.toPolylist();
      }
   
    public Block getBlockAtSlot(int slot)
      {
        int accumulator = 0;
        for( Block block:blocks )
          {
            if( accumulator == slot )
              {
                return block;
              }
            if( accumulator > slot )
              {
                return null;
              }
            accumulator += block.getDuration();
          }
        return null;
      }
}
