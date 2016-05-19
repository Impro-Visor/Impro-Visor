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

import imp.roadmap.brickdictionary.ChordBlock;
import java.util.ArrayList;
import polya.*;
/** SubstitutionRule
 * A class meant to deal with finding terminal symbols corresponding
 * to a given chord. Used to deal with chord substitutions.
 * 
 * @author Xanda Schofield
 */

// 
public class SubstitutionRule {
    
    // Constants //
    public static final int NODUR = 0;
   
    // Data members
    private ChordBlock head;                   // the chord to replace
    private ArrayList<ChordBlock> terminals;   // the substitute chords possible
    
    /** Constructor / 2
     * Makes a SubstitutionRule based on a PolyList describing a substitution
     * @param h, the String describing a ChordBlock name
     * @param contents, the rest of the Substitution rule (the list of other
     *        ChordBlock names)
     */
    SubstitutionRule(String h, Polylist contents)
    {
        head = new ChordBlock(h, NODUR);
        
        // Each chord following the first one is read in as a subsitution
        terminals = new ArrayList<ChordBlock>(); 
        ChordBlock newChord;
        while (contents.nonEmpty()) {
            newChord = new ChordBlock(contents.first().toString(), NODUR);
            terminals.add(newChord);
            contents = contents.rest();
        }
    }

    /** getHead
     * returns the string of the ChordBlock whose substitutes are in the rule
     * @return a String of the chord's name
     */
    public String getHead() {
        return head.toString();
    }
    
    /** getBody
     * returns the String of the ChordBlock
     * @return a String of the substitute chords' names
     */
    public String getBody() {
        return terminals.toString();
    }
    
    /** checkSubstitution / 1
     * Checks a given chord against the substitution rule to see if could replace
     * the head.
     * 
     * @param c, a Chord which may or may not be a substitute for the head
     * @return a SubstituteList containing either the head - if the head could
     *         have c as a substitute - or no chords at all.
     */
    public SubstituteList checkSubstitution(ChordBlock c) {
        SubstituteList subs = new SubstituteList();
        
        long diff;
        for (ChordBlock sub : terminals) {
            diff = sub.matches(c);
            if (diff >= 0) {
                subs.add(head, diff);
            }
        }
        
        return subs;
    }
    
    // end of SubstitutionRule class
}
