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
package imp.roadmap.cykparser;
import imp.roadmap.brickdictionary.BrickLibrary;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.data.Advisor;
import polya.Polylist;

/** BinaryProduction
 *A production rule for a brick music grammar with two nonterminals as the body.
 * 
 * @author Xanda Schofield
 */



public class UnaryProduction extends AbstractProduction {
    
    public static final int TOTAL_SEMITONES = 12;
    
    // The block is assumed to be in the key of C, represented as the long 0.
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key;           // the symbol's key in a C-based block
    private long termKey;
    private String name;        // the symbol itself, a quality or brick
    private long cost;          // how much the header brick costs
    private long duration;	    // duration of header brick (left-hand side)
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    private boolean familyMatch = false; // whether the brick is matched by family
    private String variant;
    
    // NOTE: Assumes it's a production in C
    /** Unary Production / 6
     * Standard constructor based upon a block and production data
     * @param h, the head symbol (a String)
     * @param t, the type of production (a String)
     * @param b, the composing Block
     * @param p, whether the production results in a printable Brick
     * @param m, the mode (a String)
     */
    public UnaryProduction(String h, String t, long k, Block b, boolean p,
            String m, BrickLibrary bricks)
    {
        head = h;
        type = t;
        key = k; 
        name = b.getSymbol();
        termKey = b.getKey();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = b.getDuration();
    }
    /** Unary Production / 7 w/ variant
     * Standard constructor based upon a block and production data
     * @param h, the head symbol (a String)
     * @param t, the type of production (a String)
     * @param b, the composing Block
     * @param p, whether the production results in a printable Brick
     * @param m, the mode (a String)
     * @param v, the variant of the block (a String)
     */
    public UnaryProduction(String h, String t, long k, Block b, boolean p,
            String m, BrickLibrary bricks, String v)
    {
        head = h;
        type = t;
        key = k; 
        name = b.getSymbol();
        termKey = b.getKey();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
        duration = b.getDuration();
        variant = v;
    }
    public String getHead() {
        return head;
    }
    
    /** getBody
     * Returns the reconstructed body of the rule
     * @return a String of the body  
     */
    public String getBody() {
        return key + " " + name + " " + cost;
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
     * @param t, a TreeNode
     * @return an long representing the difference between the two chords (-1 if
     * failed, otherwise 0 through 11), and the scaled production duration (which is 
     * always equivalent to the TreeNode duration for the unary case.
     */
    public MatchValue checkProduction(TreeNode t, 
            EquivalenceDictionary e, SubstitutionDictionary s) 
   {
       if (matchFamily(t) || t.getSymbol().equals(name))
            return new MatchValue(modKeys(t.getKey() - termKey - key), this.getCost(),
                this.familyMatch);           
        // in the event that the production is incorrect (most of the time)
        return new MatchValue();
    }

    private boolean matchFamily(TreeNode t) {
        if (t.getBlock() instanceof ChordBlock) {
            String nodeFam = Advisor.getSymbolFamily(t.getSymbol());
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
        }
        return false;
    }
    
    /** modKeys
     * Takes a key and assures it to be a positive number between 0 and 11.
     * @param i, a long representing a key
     * @return a long representing a key in the correct range (0 to 11)
     */
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
    
    public String getVariant()
    {
        return variant;
    }
    // end of UnaryProduction class
}
