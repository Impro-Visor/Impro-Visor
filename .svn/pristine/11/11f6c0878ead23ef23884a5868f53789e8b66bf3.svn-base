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
import java.util.ArrayList;
import imp.data.Advisor;
import polya.Polylist;

/** BinaryProduction
 * A class used to describe production rules for a brick music grammar with 
 * two nonterminals as the body of the production.
 * 
 * @author Xanda Schofield
 */



public class BinaryProduction extends AbstractProduction {
    
    // Constants //
    public static final int TOTAL_SEMITONES = 12; // the number of semitones in 
                                                  // an octave
    public static final long NC = -1;             // the key of a No Chord
    
    private int inexact_match_factor = 100;
    
    
    // NB: All production rules are put into the key of C when they are 
    // constructed. This does not necessitate the original Brick being written
    // in C.
    
    // Data Members //
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key1;          // the first symbol's key in a C-based block
    private long key2;          // the second symbol's key in a C-based block
    private String name1;       // the first symbol itself, a quality or brick
    private String name2;       // the second symbol itself, a quality or brick
    private long cost;          // how much the header brick costs
    private long duration;      // duration of the header brick
    private long dur1;          // duration of the first composing symbol
    private long dur2;          // duration of the second composing symbol
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    private boolean arbitrary = false; // whether the brick contains a block of
                                       // arbitrary duration
    private boolean familyMatch = false; // if match is made with chord family
    private String variant; // the variant type of the block
    
    // BinaryProduction Constructors // 
    
    /** Standard constructor
     * Constructs a BinaryProduction from two blocks and circumstantial data
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param b1, the first Block forming the resulting Brick
     * @param b2, the second Block forming the resulting brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     */
    public BinaryProduction(String h, String t, long k, Block b1, Block b2, boolean p,
            String m, BrickLibrary bricks)
    {
        head = h;
        type = t;
        
        key1 = modKeys(b1.getKey() - k);
        name1 = b1.getSymbol();
        dur1 = b1.getDuration(); 
        key2 = modKeys(b2.getKey() - k);
        name2 = b2.getSymbol();
        dur2 = b2.getDuration();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = dur1 + dur2;
        checkArbitrary(b1);
        checkArbitrary(b2);
    }
    
    /** Standard constructor w/ variant
     * Constructs a BinaryProduction from two blocks and circumstantial data
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param b1, the first Block forming the resulting Brick
     * @param b2, the second Block forming the resulting brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     * @param v, a String of the variant type of the Brick
     */
    public BinaryProduction(String h, String t, long k, Block b1, Block b2, boolean p,
            String m, BrickLibrary bricks, String v)
    {
        head = h;
        type = t;
        
        key1 = modKeys(b1.getKey() - k);
        name1 = b1.getSymbol();
        dur1 = b1.getDuration(); 
        key2 = modKeys(b2.getKey() - k);
        name2 = b2.getSymbol();
        dur2 = b2.getDuration();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = dur1 + dur2;
        checkArbitrary(b1);
        checkArbitrary(b2);
        variant = v;
    }
    
    /** Higher-level constructor
     * Constructs a BinaryProduction from a BinaryProduction of the first part 
     * of the Brick and a Block of the second part of the Brick.
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param pStart, a BinaryProduction describing the first Block of this 
     *        production's Brick
     * @param b, the second Block forming the resulting Brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     */
    public BinaryProduction(String h, String t, long k, BinaryProduction pStart, 
            Block b, boolean p, String m, BrickLibrary bricks) {
        head = h;
        type = t;
        key1 = 0;
        name1 = pStart.getHead();
        dur1 = pStart.getDuration();
        key2 = modKeys(b.getKey() - k);
        name2 = b.getSymbol();
        dur2 = b.getDuration();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = dur1 + dur2;
        if (pStart.isArbitrary()) setArbitrary(true);
        checkArbitrary(b);
    }
    
    /** Higher-level constructor w/ variant
     * Constructs a BinaryProduction from a BinaryProduction of the first part 
     * of the Brick and a Block of the second part of the Brick.
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param pStart, a BinaryProduction describing the first Block of this 
     *        production's Brick
     * @param b, the second Block forming the resulting Brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     */
    public BinaryProduction(String h, String t, long k, BinaryProduction pStart, 
            Block b, boolean p, String m, BrickLibrary bricks, String v) {
        head = h;
        type = t;
        key1 = 0;
        name1 = pStart.getHead();
        dur1 = pStart.getDuration();
        key2 = modKeys(b.getKey() - k);
        name2 = b.getSymbol();
        dur2 = b.getDuration();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = dur1 + dur2;
        variant = v;
        if (pStart.isArbitrary()) setArbitrary(true);
        checkArbitrary(b);
        
    }
    
    // Getters for a BinaryProduction //
    
    /** getHead
     * Returns the header symbol for the production
     * @return a String of the head
     */
    public String getHead() {
        return head;
    }
    
    /** getBody
     * Returns the reconstructed body of the rule
     * @return a String of the body with form
     * "[firstBlockName] [firstBlockKey] [secondBlockName] [secondBlockKey] [cost]"
     */
    public String getBody() {
        return name1 + " " + key1 + " " + name2 + " " + key2 + " " + cost;
    }
    
    /** getCost
     * Returns the base cost for a parser to use the resulting Brick
     * @return a long of the Brick's cost
     */
    public long getCost() {
        return cost;
    }
    
    /** getType
     * Gets the type of Brick formed (e.g. "Cadence")
     * @return a String of the Brick's type
     */
    public String getType() {
        return type;
    }
    
    /** getMode
     * Gets the mode of the Brick formed (e.g. "Major")
     * @return a String of the Brick's mode
     */
    public String getMode() {
        return mode;
    }

     /** getDuration
     * Gets the duration of the Brick formed (reminder: not in absolute units)
     * @return a long of the Brick's duration
     */
    public long getDuration() {
	    return duration;
    } 
    
    /** checkProduction
     * Tests whether a production fits with a given ordered pair of TreeNodes. 
     * If so, it returns a positive chord difference between these and the 
     * rule's original key (C). Otherwise, it returns -1.
     * 
     * @param a, the first TreeNode
     * @param b, the second TreeNode
     * @return a MatchValue with a long representing the difference between the 
     * two chords (-1 if failed, otherwise 0 through 11), and the scaled duration 
     * of the production.
     */
    public MatchValue checkProduction(TreeNode a, TreeNode b) 
    {
        // Conditions:
        // - TreeNodes a and b must have a key
        // - a must not mark a section end (the block cannot span a section end)
        // - the relative difference in key must be the same for the TreeNodes
        //   and the two halves of the production
        // - either production is a chord that matches the family of the produciton chord,
        //   or the productions have the same name, e.g. both are SPOT
        // - the production rule contains an arbitrary-length brick or
        //   the durations of the composing bricks can be scaled to match right-hand rules
        if (a.getKey() != NC && b.getKey() != NC &&
            modKeys(key2 - key1) == modKeys(b.getKey() - a.getKey()) &&
            matchFamily(a, b)) {
            if (durationScales(a.getDuration(), b.getDuration()))
                return new MatchValue(modKeys(b.getKey() - key2), 
                    this.getCost(), this.familyMatch);
            // non-scaling rules that still match are accepted at a scaled cost
            return new MatchValue(modKeys(b.getKey() - key2), 
                this.getCost() * inexact_match_factor, this.familyMatch);
        } 
        // In the event that the production is incorrect (most of the time)
        return new MatchValue();
    }

    /** matchFamily
     * Whether the Blocks represented in two treenodes match the names of the
     * Blocks referenced in the binary production, matching chords with against
     * their chord families and bricks against literal brick names
     *
     * @param a, the first TreeNode
     * @param b, the second TreeNode
     * @return true if the blocks are matches, false otherwise
     */
    private boolean matchFamily(TreeNode a, TreeNode b) {
        boolean matchA = a.getBlock() instanceof ChordBlock ?
                         matchNode(a, name1) : matchName(a, name1);
        if (!matchA) return false;
        boolean matchB = b.getBlock() instanceof ChordBlock ?
                         matchNode(b, name2) : matchName(b, name2);
        return matchB;
    }
    
    /** matchNode
     * Whether two literal chord names are members of the same family
     * 
     * @param t, the TreeNode being matched
     * @param name, the name of the production chord being matched
     * @return true if the chords are members of the same family, else false
     */
    private boolean matchNode(TreeNode t, String name) {
        String nodeSym = t.getSymbol().equals(t.getTrimmedSymbol()) ?
                         t.getSymbol() : t.getTrimmedSymbol();
        String nodeFam = Advisor.getSymbolFamily(nodeSym);
        String prodFam = Advisor.getSymbolFamily(name); 
        if (nodeFam.equals(prodFam)) {
            this.familyMatch = false;
            return true;
        }
        Polylist matchVal = adict.assoc(nodeFam);
        if (matchVal != null && (prodFam.equals(matchVal.second()) ||
                                 matchVal.second().equals("any"))) {
            this.familyMatch = true;
            return true;
        } 
        return false;
    }   

    /** matchName
     * Match two literal brick names
     * 
     * @param t
     * @param name
     * @return whether the brick names match
     */
    private boolean matchName(TreeNode t, String name) {
        return t.getSymbol().equals(name) || t.getTrimmedSymbol().equals(name);
    }

    /** durationScales
     * Checks whether a production is of arbitrary duration, and if not whether
     * two durations can be scaled to match it
     *
     * @param durA, duration of the first TreeNode
     * @param durB, duration of the second TreeNode
     * @return whether duration is arbitrary or can be scaled to match
     */
    private boolean durationScales (long durA, long durB) {
        return isArbitrary() || (durA * dur2 == durB * dur1);
    }
    
    /** modKeys
     * Takes a key and assures it to be a positive number between 0 and 11.
     * @param i, a long representing a key
     * @return a long representing a key in the correct range (0 to 11)
     */
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
    
    /** checkArbitrary
     * Takes in a block and determines whether it is/contains a block of
     * arbitrary duration, setting a flag in the production rule if so.
     * @param block 
     */
    private void checkArbitrary(Block block) {
        if (block.getDuration() == 0) setArbitrary(true);
        ArrayList<Block> subBlocks = block.getSubBlocks();
        for (Block b : subBlocks) {
            if (b.getDuration() == 0) setArbitrary(true);
            if (b instanceof Brick) checkArbitrary(b);
        }
    }
    
    /** isArbitrary
     * Whether production rule contains a brick of arbitrary duration
     * @return 
     */
    public boolean isArbitrary() {
        return this.arbitrary;
    }
    
    /** set Arbitrary
     * Set the arbitrary flag for the production rule
     * @param arbitrary 
     */
    public void setArbitrary(boolean arbitrary) {
        this.arbitrary = arbitrary;
    }
    public String getVariant()
    {
        return variant;
    }
    // end of BinaryProduction class
}
