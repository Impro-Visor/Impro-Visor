/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.cykparser;
import imp.brickdictionary.*;
import imp.data.ChordSymbol;
import imp.roadmap.*;
import java.util.ArrayList;
import java.util.Arrays;
import polya.*;


/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessor {

    public static boolean traceJoin = false;
    
    
    private static final String APPROACH = "Approach";
    
    private static final String LAUNCHER = "Launcher";

    private static final String STRAIGHT = "Straight";
    
    private static final String SAD = "Sad";
    
   /**
     * Temporary fix to static method issues
     */

    private static EquivalenceDictionary dict = null;
        
    public static final int DOM_ADJUST = 5; // adjustment to root (by semitones)
                                            // when dealing with dominants
    
    public static final int OCTAVE = 12;    // semitones in octave
    
    public static final int FOURTH = 5;      // perfect fourth in semitones
    
    // Names of joins, arranged by difference in keys between which they
    // transition (with reference to dominant in first block)
    
    public static final String[] JOINS = {"Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder", "Homer", 
        "Cherokee", "Woody", "Highjump", "Bauble"};
    
    // For launching other than straight across a section
    
    public static final String[] RESOLUTIONS = {"", "", "", "", /* "3rd Bartok Sub" */ 
        "", "", "Tritone", "", "Bauble", "Yardbird", 
        "", "Nowhere"};

    
    // Rules for finding representative chord in diatonicChordCheck
    
    private static ArrayList<Polylist> equivalenceRules;
    
    // Rules for which chords are diatonic depending on mode
    
    private static ArrayList<Polylist> diatonicRules;
    
    // Introduced to avoid reading the dictionary repeatedly in checkJoinability.
    
    static
      {
        setEquivalenceDictionary();
      }

    /** Default constructor
     * Constructs a new PostProcessor with empty rules lists
     */
    public PostProcessor() {
        equivalenceRules = new ArrayList<Polylist>();
        diatonicRules = new ArrayList<Polylist>();
    }
    
    /** PostProcessor / 2
     * Create rules lists from input
     * @param e : ArrayList of Polylists, each one of which is an equivalence rule
     * @param d : ArrayList of Polylists, each one of which is a diatonic rule
     */
    public PostProcessor(ArrayList<Polylist> e, ArrayList<Polylist> d) {
        equivalenceRules = e;
        diatonicRules = d;
    }
    
    /** getEquivalenceRules
     * Get the stored equivalence rules
     * @return a set of equivalence rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getEquivalenceRules() {
        return equivalenceRules;
    }
    
    /** getDiatonicRules
     * Get the stored diatonic rules
     * @return a set of diatoniv rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getDiatonicRules() {
        return diatonicRules;
    }
    
    /** setEquivalenceRules
     * set the equivalence rules based on input
     * @param e : a set of equivalence rules (ArrayList of Polylists)
     */
    public void setEquivalenceRules(ArrayList<Polylist> e) {
        equivalenceRules = e;
    }
    
    /** setDiatonicRules
     * set the diatonic rules based on input
     * @param e : a set of diatonic rules (ArrayList of Polylists)
     */
    public void setDiatonicRules(ArrayList<Polylist> d) {
        diatonicRules = d;
    }
    
/** 
 * Group consecutive block of same key for overarching key sections.
 * @param roadmap : a RoadMap
 * @return newMap : an altered RoadMap
 */
    
public static RoadMap findKeys(RoadMap roadmap)
  {
    //System.out.println("findKeys in " + roadmap);

    ArrayList<KeySpan> keymap = new ArrayList<KeySpan>();

    // Initialize key, mode, and duration of current block
    KeySpan current = new KeySpan();
    ArrayList<Block> blocks = roadmap.getBlocks();

    // Check for an empty roadmap
    if( blocks.isEmpty()
            || // special case for a new leadsheet
            (blocks.size() == 1 && blocks.get(0).isChord()
            && ((ChordBlock) blocks.get(0)).getChord().isNOCHORD()) )
      {
        roadmap.getKeyMap().clear();
        return roadmap;
      }

    // Create array so we can loop through correctly
    Block[] blockArray = blocks.toArray(new Block[0]);

    boolean ncFlag = false;
    int ncDuration = 0;

    int index = 1;
    Block lastBlock = blockArray[blockArray.length - index];

    while( lastBlock.isChord() && ((ChordBlock) lastBlock).getChord().isNOCHORD() )
      {
        ncDuration += lastBlock.getDuration();
        index++;
        lastBlock = blockArray[blockArray.length - index];
      }

    // Initialize KeySpan using last block
    
    current.setKey(lastBlock.getKey());
    current.setMode(lastBlock.getMode());
    current.setDuration(lastBlock.getDuration() + ncDuration);
    
    //System.out.println("initializing Keyspan to " + current);
    
    ncDuration = 0;

    // Loop through blocks backwards, 
    for( int i = blockArray.length - index - 1; i >= 0; i-- )
      {
        Block thisBlock = blockArray[i];

        // Create new KeySpan for new section. 

        if( thisBlock.isSectionEnd() )
          {
            // Note that a section end can still consist of a single chord

            KeySpan entry = current;
            keymap.add(0, entry);

            current = new KeySpan(thisBlock);
            
            //System.out.append("new KeySpan " + current);

            if( thisBlock.isChord() )
              {
                ChordBlock c = (ChordBlock) thisBlock;

                if( diatonicChordCheck(c, entry.getKey(), entry.getMode()) )
                  {
                    current.setKey(entry.getKey());
                    current.setMode(entry.getMode());
                    
                    //System.out.println("initializing new Keyspan to " + current);
                  }
                // End of current key -- add to the list
                else
                  {
                    current = new KeySpan(c);
                   //System.out.append("new KeySpan " + current);
                  }
              }
            // Match mode to second block if first block is an approach or
            // launcher that resolves to second block.
            else if( isApproachOrLauncher(thisBlock) )
              {
                ChordBlock cFirst = thisBlock.getLastChord();

                ChordBlock cSecond = blockArray[i + 1].getFirstChord();

                if( doesResolve(cFirst, cSecond) )
                  {
                    thisBlock.setMode(cSecond.getMode());
                  }
              }

            if( ncFlag )
              {
                current.augmentDuration(ncDuration);
                ncFlag = false;
                ncDuration = 0;
              }
          }
        // Case in which first block is a brick
        else if( thisBlock instanceof Brick )
          {
            // Check if current block can roll into current KeySpan
            if( current.getKey() == thisBlock.getKey()
             && current.getMode().equals(thisBlock.getMode()) )
              {
                current.augmentDuration(thisBlock.getDuration());
              }
            else if( thisBlock.singleChord() && diatonicChordCheck(thisBlock.getLastChord(), current.getKey(), current.getMode()))
              {
                // RK: In case the block has only one chord, the chord is checked for merging wiht the current key span using diatonicity.
                // This allows, for example, a ii chord in Major to be merged, even though it is incorporated into a Minor-On brick.
                current.augmentDuration(thisBlock.getDuration());
              }
            // Match mode to second block if first block is an approach or
            // launcher that resolves to second block
            else if( isApproachOrLauncher(thisBlock) )
              {
                ChordBlock cFirst = thisBlock.getLastChord();

                ChordBlock cSecond = blockArray[i + 1].getFirstChord();

                if( doesResolve(cFirst, cSecond) )
                  {
                    thisBlock.setMode(cSecond.getMode());
                    current.augmentDuration(thisBlock.getDuration());
                  }
                else
                  {
                    KeySpan entry = current;
                    keymap.add(0, entry);

                    current = new KeySpan(thisBlock);
                    
                    //System.out.append("new KeySpan " + current);

                    if( ncFlag )
                      {
                        current.augmentDuration(ncDuration);
                        ncFlag = false;
                        ncDuration = 0;
                      }
                  }
              }
            // End of current key -- add to the list
            else
              {
                KeySpan entry = current;
                keymap.add(0, entry);

                current = new KeySpan(thisBlock);
                
                //System.out.append("new KeySpan " + current);
                          

                if( ncFlag )
                  {
                    current.augmentDuration(ncDuration);
                    ncFlag = false;
                    ncDuration = 0;
                  }
              }
          }
        // Case in which first block is a chord
        else
          {
            ChordBlock c = (ChordBlock) thisBlock;

            if( c.getChord().isNOCHORD() )
              {
                ncDuration += c.getDuration();
                ncFlag = true;
              }
            // Check if chord is diatonically within current KeySpan
            else if( diatonicChordCheck(c, current.getKey(), current.getMode()) )
              {
                current.augmentDuration(c.getDuration());
              }
            // End of current key -- add to the list
            else
              {
                KeySpan entry = current;
                keymap.add(0, entry);

                current = new KeySpan(c);
                if( ncFlag )
                  {
                    current.augmentDuration(ncDuration);
                    ncFlag = false;
                    ncDuration = 0;
                  }
              }
          }
      }

    // Special case for NC chord at beginning of song
    if( ncFlag )
      {
        keymap.add(0, current);
        current = new KeySpan(-1, "", ncDuration);
        //System.out.append("new KeySpan " + current);
      }
    
    // Add first KeySpan in song to list
    
    keymap.add(0, current);
    blocks = new ArrayList<Block>(Arrays.asList(blockArray));

    // Replace current RoadMap with one that has properly merged KeySpans
    
    RoadMap newMap = new RoadMap(blocks);
    newMap.setKeyMap(keymap);

    return newMap;
  }


    /** 
     * Analyze ArrayList of Blocks to find approaches that could be launchers.
     * @param blocks : ArrayList of blocks
     * @return alteredList : ArrayList of blocks adjusted for any launchers 
     *                       found
     */

public static ArrayList<Block> findLaunchers(ArrayList<Block> blocks)
  {
    // Rebuilding original list, but with launchers in the appropriate 
    // places
    ArrayList<Block> alteredList = new ArrayList<Block>();

    for( int i = 0; i < blocks.size(); i++ )
      {

        Block thisBlock = blocks.get(i);
        
        //System.out.println("thisBlock = " + thisBlock);

        // If the current block is a brick, check if it could be a launcher
        if( thisBlock instanceof Brick )
          {
            Brick b = (Brick) thisBlock;
            ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();

            Block postBlock;

            // If the brick is not the last one in the list, get chords from
            // next block.

            if( i != blocks.size() - 1 )
              {
                postBlock = blocks.get(i + 1);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }
            // Otherwise, loop around and get chords from first block
            else
              {
                postBlock = blocks.get(0);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }

            String brickName = b.getName();

            if( brickName.endsWith(APPROACH) )
              {
               
                Long baseKey = b.getLastChord().getKey(); // alternate b.getKey()
                
                // This call is made in the event of an approach resolving 
                
                String altResolution = getAlternateResolution(baseKey, postBlock.getFirstRoot()); // was .getKey());
                
                //System.out.println(b + " vs " + postBlock + " altResolution = " + altResolution);
                
                 if( !altResolution.isEmpty() )
                  {
                    brickName = brickName.replace(STRAIGHT, altResolution);
                    brickName = brickName.replace(SAD, altResolution);
                    b.setName(brickName);
                    //b.setKey((postBlock.getKey() + OCTAVE - getAlternateOffset(baseKey, postBlock.getFirstRoot()))%OCTAVE);
                    b.setMode(postBlock.getMode());
                  }
                 else
                   {                
                   }
              }

            boolean isQualifiedApproach = brickName.contains(APPROACH) && !brickName.equals(APPROACH);
            boolean isSlowLauncher      = brickName.equals("Dominant Cycle") && b.getLength() == 2;
            boolean isDoglegApproach    = brickName.equals("Dogleg Approach");
            
            // Check if brick is an approach is actually a Launcher.
            // In "Insights" examples, resolution is not required.

            if( (isQualifiedApproach || isSlowLauncher || isDoglegApproach ) && b.isSectionEnd() )
                    /* && doesResolve(b, chordList.get(0))*/ 
              {
                // If the name ends in "Approach" but is not just plain "Approach", replace "Approach" with "Launcher"
                
                if( isDoglegApproach )
                  {
                   brickName = "Dogleg Slow Launcher";                   
                  }
                else if( isQualifiedApproach )
                  {
                    brickName = brickName.replace(APPROACH, LAUNCHER);
                  }
                else if( isSlowLauncher )
                  {
                  brickName = "Slow Launcher";
                  }

                b.setName(brickName);
                b.setType("Launcher");

                // Add altered brick to the list
                alteredList.add(b);
              }

            // If brick is not an approach or does not resolve, add it to 
            // the list 
            else
              {
                alteredList.add(b);
              }
          }
        // Case in which current block is a dominant chord:
        else if( ((ChordBlock)thisBlock).isDominant() )
          {

            ChordBlock c = (ChordBlock) thisBlock;
            Block postBlock;
            ArrayList<ChordBlock> chordList;

            // If the brick is not the last one in the list, get chords from
            // next block.

            if( i != blocks.size() - 1 )
              {
                postBlock = blocks.get(i + 1);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }
            // Otherwise, loop around and get chords from first block.
            else
              {
                postBlock = blocks.get(0);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }

            // This call is made in the event of a single dominant resolving
            // to a tonic.
            
            String altResolution = getAlternateResolution(c.getKey(), postBlock.getKey());

            Block b;
            if( !altResolution.isEmpty() )
              {
                b = new Brick(c, altResolution, "Dominant");
              }
            else
              {
                b = new ChordBlock(c);
              }

            // Check for single-chord launcher.

            if( doesResolve(c, chordList.get(0)) && c.isSectionEnd() )
              {
                Brick uniLauncher = new Brick(c, chordList.get(0).getMode());

                alteredList.add(uniLauncher);
              }
            else
              {
                alteredList.add(b);
              }
          }
        // If the block is a non-dominant chord, add it to the list
        else
          {
            alteredList.add(thisBlock);
          }
      }
    return alteredList;
  }
   
    
    /**
     * No longer used
     * Change the resolution of a Straight approach to some other kind,
     * depending on the target
     * @param approach
     * @param target
     * @return 

public static String getAlternateResolution(Block approach, ChordBlock target)
  {
    //System.out.println("getAlternate " + approach + " to " + target);
    
    long domRoot = approach.getLastChord().getKey();
    long resRoot = target.getKey();

    return getAlternateResolution(domRoot, resRoot);
  }
 */

/**
 * Change the resolution of a target as if from a dominant.
 * @param domRoot
 * @param resRoot
 * @return 
 */

public static String getAlternateResolution(long domRoot, long resRoot)
  {
    int BIAS = 7;
    
    int domRootInt = Long.valueOf(domRoot).intValue();
    int resRootInt = Long.valueOf(resRoot).intValue();

    int diff = (resRootInt + BIAS - domRootInt + OCTAVE) % OCTAVE;

    String altResolution = RESOLUTIONS[diff];

    //System.out.println("domRoot = " + domRoot + ", resRoot = " + resRoot + " diff = " + diff + " res = " + altResolution);

    return altResolution;
  }


public static int getAlternateOffset(long domRoot, long resRoot)
  {
    int BIAS = 7;
    
    int domRootInt = Long.valueOf(domRoot).intValue();
    int resRootInt = Long.valueOf(resRoot).intValue();

    return (resRootInt + BIAS - domRootInt + OCTAVE) % OCTAVE;
  }

 
      
/** 
 * A method that finds joins between two bricks, if any.
 * @param blocks : ArrayList of Blocks (Chords or Bricks)
 * @return joinList : ArrayList of joins between blocks
 */
      
public static ArrayList<String> findJoins(ArrayList<Block> blocks)
  {
    String[] joinArray = null;
    if( blocks.size() >= 1 )
      {
        joinArray = new String[blocks.size() - 1];
      }
    else
      {
        return null;
      }
    for( int i = 0; i < blocks.size() - 1; i++ )
      {
        joinArray[i] = getJoinString(blocks.get(i), blocks.get(i + 1));
      }
    ArrayList<String> joinList = new ArrayList(Arrays.asList(joinArray));
    return joinList;
  }



/**
 * Return a possibly-empty String representing representing a join between 
 * two blocks.
 */

public static String getJoinString(Block b, Block c)
  {
    if( traceJoin ) 
      {
        System.out.println("-------------------------------------------------");

        System.out.println("\nTrying to join " + b + " last chord = " + b.getLastChord()
                  + "\nto " + c + " first chord = " + c.getFirstChord() + ":");
      }

    if( b.getLastChord().same(c.getFirstChord()) )
      {
        if( traceJoin ) System.out.println("Not joinable because chords the same");
        return "";
      }

    long baseKey = b.getLastChord().getKey();  // alternate: b.getKey() will give different results, e.g. if overrun

    if( c instanceof Brick )
      {
        if( !checkFirstStability(b) )
          {
            if( checkDogleg(b, (Brick) c) )
              {
                if( traceJoin ) System.out.println("Dogleg");
                return "Dogleg";
              }
            if( traceJoin ) System.out.println("Not joinable because first unstable and not dogleg");
            return "";
          }
        
        // Check that the two bricks are joinable
        if( checkJoinability(b, (Brick) c) )
          {
                ArrayList<ChordBlock> chords = c.flattenBlock();

                if( !chords.isEmpty() )
                  {
                    ChordBlock previous = c.getFirstChord();

                    for( ChordBlock current : chords )
                      {
                        //System.out.println("previous = " + previous + " current = " + current);
                        //int diffPrevious = (OCTAVE + previous.getRootSemitones() - current.getRootSemitones()) % OCTAVE;

                        // Possible cyclic or chromatic descending dominant

                        if( current.isDominant() )
                          {
                            long domKey = current.getKey();
                            if( traceJoin ) System.out.println("domKey determined by first dominant " + current + " as: " + BrickLibrary.keyNumToName(domKey) );
                            return joinLookup(domKey, baseKey);
                          }
                        else if( current.isMinor7() )
                          {
                            long domKey = (current.getKey() + FOURTH) % OCTAVE;
                            if( traceJoin ) System.out.println("domKey determined by first minor7 " + current + " as: " + BrickLibrary.keyNumToName(domKey) );
                            return joinLookup(domKey, baseKey);
                           }
                      }
                  }

               if( traceJoin ) System.out.println("Not joinable because no dominant or minor7.");
               return "";
          }
        if( traceJoin ) System.out.println("Not joinable because checkJoinability failed.");
        return "";
      } // c instanceof Brick
            
    else
      // c is a ChordBlock, not a Brick
      {
        if( !checkFirstStability(b) )
          {
            if( traceJoin ) System.out.println("Not joinable because first unstable.");
            return "";
          }

        // Second block is a chord, but this does not mean not joinable.

        ChordBlock cb = (ChordBlock) c;

        long domKey;

        // First check for staring with minor 7 type chord

        if( cb.isMinor7() )
          {
            domKey = (cb.getKey() + FOURTH) % OCTAVE;
            if( traceJoin ) System.out.println("domkey determined by minor7 " + cb + " as " + BrickLibrary.keyNumToName(domKey));
            return joinLookup(domKey, baseKey);
          }
        // Otherwise try to use first dominant in second brick
        else if( cb.isDominant() )
          {
             domKey = cb.getKey();
             if( traceJoin ) System.out.println("domKey determined by first dominant " + cb + " as: " + BrickLibrary.keyNumToName(domKey) );
             return joinLookup(domKey, baseKey);
          }
        else
          {
            if( traceJoin ) System.out.println("Not joinable because neither a dominant nor minor 7");
            return "";
          }
      }
  }

    /**
     * Retrieve name of join based on difference between keys of two bricks.
     * @param keyDiff : difference of keys between two bricks
     * @return  Name of join
     */
    public static String joinLookup(long domKey, long baseKey) {
       int keyDiff = (int)((domKey - baseKey + OCTAVE) % OCTAVE);
       String joinType = JOINS[keyDiff];
       
       if( traceJoin )
       System.out.println("domKey = "  + BrickLibrary.keyNumToName(domKey) 
                       + ", baseKey = " + BrickLibrary.keyNumToName(baseKey)
                       + " joinType = " + joinType);
        
        return joinType;
    }
    
    /** 
 * Check whether a Block is joinable to a Brick.
 * @param first : a Block
 * @param second : a Brick
 * @return joinable : a boolean indicating whether or not first and second 
 *                    are joinable
 */
public static boolean checkJoinability(Block first, Brick second)
  {
     // Comparing last chord of first block and first chord of second block
    
    ChordBlock firstChord = first.getLastChord();
    ChordBlock secondChord = second.getFirstChord();

    // Don't join to a tonic directly

    if( secondChord.isTonic() )
      {
        if( traceJoin ) System.out.println("Not Joinable because second is tonic.");
        return false;
      }
    
    if( first.isOverlap() )
      {
        if( traceJoin ) System.out.println("Not Joinable because first is overlap brick.");
        return false;       
      }

    // Get equivalences for the two chords

    SubstituteList firstEquivs = dict.checkEquivalence(firstChord);
    SubstituteList secondEquivs = dict.checkEquivalence(secondChord);

    String firstMode = first.getMode();
    String secondMode = second.getMode();

    String firstType = first.getType();
    String secondType = second.getType();

    // Don't join chord to the same chord

    if( firstChord.same(secondChord) )
      {
        if( traceJoin ) System.out.println("Not joinable: Two chords the same.");
        return false;
      }

    if( !checkFirstStability(first) ) 
      {
        if( traceJoin ) System.out.println("Not joinable: first not stable.");
        return false; // No point in checking further
      }

    // Determine stability of second block

    if( checkSecondStability(second) )
      {
        if( traceJoin ) System.out.println(" Not jointable: second not unstable: " 
                        + secondChord + " type " + secondChord.getType() + ".");
        return false;
      }


    if( traceJoin ) System.out.println("Joinable.");
    return true;

  }

    /** 
     * Checks whether a possible join is a dogleg
     * @param first : a Block (before join)
     * @param second : a Brick (after join)
     * @return isDogleg : a boolean indicating whether or not join is dogleg
     */
    
    public static boolean checkDogleg(Block first, Brick second) {
        boolean isDogleg = false;
        
        ArrayList<ChordBlock> firstList  =  first.flattenBlock();
        ArrayList<ChordBlock> secondList = second.flattenBlock();
        
        // Comparing last chord of first block and first block of second block
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        long firstKey = firstToCheck.getKey();
        String firstSymbol = firstToCheck.getSymbol();
        
        long secondKey = secondToCheck.getKey();
        String secondSymbol = secondToCheck.getSymbol();
        
        // Dogleg join is characterized by a dominant to a m7
        if(firstKey == secondKey && firstSymbol.startsWith("7") && 
                secondSymbol.equals("m7")) {
            isDogleg = true;
        }
        
        return isDogleg;
    }
    
    
    /**
     * Check to see if chord fits diatonically within key.
     * @param c : chord to be checked
     * @param key : key that chord is checked against
     * @param mode : String that determines qualities of chords in key
     * @param dict : a BrickLibrary
     * @return isInKey : whether or not chord is in key
     */
    public static boolean diatonicChordCheck(ChordBlock c, Long key, 
            String mode) {
        
        //System.out.println("\ndiatonicChordCheck " + c + " " + key + " " + mode);
        
        if( c.isDiminished() )
          {
            return true;
          }
        
        boolean isInKey = false;
        ChordBlock cTemp = new ChordBlock(c);
        
        // Adjust for second brick's key
        cTemp.transpose(OCTAVE - key);
        Long offset = cTemp.getKey();
        
        // Transpose chord down to C
        cTemp.transpose(OCTAVE - offset);
        
        //System.out.println("cTemp = " + cTemp);
        
        ChordSymbol cSym = null;
        
        // Get representative chord for c and save it in cSym
         
        //System.out.println("rules = " + equivalenceRules);
        
        if( equivalenceRules != null )
        for(Polylist p : equivalenceRules)
        {
          // Don't barf if chord does not exist in vocabulary
          try
            {
            // RK discovered long-standing bug here 12/1/2011: cTemp was c
              
            if(cTemp.getChord().getChordSymbol().enhMember(p))
            {
                //System.out.println(cTemp + " enharmonic member of " + p);
                cSym = ChordSymbol.makeChordSymbol(p.first().toString());
                break;
            }
            }
          catch(Exception e)
            {
              
            }
        }
        
        ChordSymbol tcSym = null;
        
        // Transpose cSym and c back by offset saved earlier
        if(cSym != null) {
            tcSym = cSym.transpose(offset.intValue());
            //System.out.println("transposing " + cSym + " " + offset + " to " + tcSym);
        }
        
        // Check if cSym is diatonically within key according to mode of second
        // block
        
        //System.out.println("tcSym = " + tcSym + " diatonicRules = " + diatonicRules);
        if( diatonicRules != null )
        for(Polylist p : diatonicRules)
        {
            String modeTag = p.first().toString();
            
            //System.out.println(tcSym + " vs " + p);
            
            if(modeTag.equals(mode))
            {
                if(cSym != null && tcSym.enhMember(p.rest()))
                {
                  //System.out.println(tcSym + " is an enharmnoic member of " + p.rest());
                    isInKey = true;
                    break;
                }
            }
        }
        
        //System.out.println(" " + isInKey);
        return isInKey;
    }
    
    /** 
     * Check if brick resolves to a certain block.
     * @param b1 : brick to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    
    public static boolean doesResolve(Brick b1, Block b2) {

        if (b1.getKey() == b2.getKey()) {
            return true;
        }

        return false;
    }
       
    /** 
     * Check if ChordBlock resolves to a certain block
     * @param b1 : ChordBlock to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    public static boolean doesResolve(ChordBlock b1, Block b2) {
        
        // Adjust for the dominant
        if ((b1.getKey() + DOM_ADJUST)%OCTAVE == b2.getKey()) {
            return true;
        }

        return false;
    }
    
    /** 
     * Treating an array of Strings as a set, determines whether or not
     * element occurs in the array.
     * @param array
     * @param element
     * @return 
     */
    public static boolean member(String element, String array[])
      {
        for( String x: array )
          {
            if( x.equals(element) )
              {
                return true;
              }
          }       
        return false;
      }
    
    
    /**
     * This only needs to be called once.
     */
    
    public static void setEquivalenceDictionary()
      {       
        dict = new EquivalenceDictionary();
        dict.loadDictionary(CYKParser.DICTIONARY_NAME);
      }
    
    /**
     * Tell whether the argument block is an Approach or a Launcher type
     * @param b
     * @return 
     */
    public static boolean isApproachOrLauncher(Block b)
      {
        String type = b.getType();
        return type.equals(APPROACH) || type.equals(LAUNCHER);
      }
    
    
    public static boolean checkFirstStability(Block b)
      {
        return b.getLastChord().isGeneralizedTonic();
      }
 
    public static boolean checkSecondStability(Block b)
      {
        return b.getFirstChord().isTonic();
      }
}
     
