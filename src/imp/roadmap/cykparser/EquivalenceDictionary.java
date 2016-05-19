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
import imp.ImproVisor;
import java.io.*;
import imp.util.ErrorLog;
import polya.*;
import java.util.ArrayList;
import java.util.LinkedList;

/** EquivalenceDictionary
 *
 * Handles equivalent ChordBlocks in interpretation of bricks as equivalence classes
 * of 2 or more ChordBlocks.
 * 
 * @author Xanda Schofield
 */
public class EquivalenceDictionary {
    
    
    // EquivalenceDictionary has one data member, dict. It is a list of
    // ArrayLists of Chords, where each ArrayList represents an equivalence
    // class of chords.
    private LinkedList<ArrayList<ChordBlock>> dict; // the storage mechanism
                                                    // for equivalence classes
    
    
    /** Default constructor
     * Constructs a new EquivalenceDictionary with an empty dict.
     */
    public EquivalenceDictionary() 
    {
        dict = new LinkedList<ArrayList<ChordBlock>>();
    }
    
    
    
    /** addRule / 1
     * Adds a single rule as an equivalence class to the dictionary
     * @param rule, an ArrayList of equivalent ChordBlocks.
     */
    public void addRule(ArrayList<ChordBlock> rule) 
    {
        dict.add(rule);
    }
    
    /** checkEquivalences / 1
     * Takes in a string for a quality and checks its equivalence classes for 
     * an appropriate class.
     * 
     * @param c: a ChordBlock whose equivalence is to be checked
     * @return a SubstituteList of possible ChordBlocks equivalent to c, 
     * including c itself.
     */
    public SubstituteList checkEquivalence(ChordBlock c)
    {
        SubstituteList equivalences = new SubstituteList();
        
        for (ArrayList<ChordBlock> rule : dict)
        {
            for (ChordBlock eq : rule)
            {
                long diff = eq.matches(c);
                if (diff >= 0)
                {
                    for (ChordBlock sub : rule)
                    {
                        if (sub.matches(c) < 0)
                            equivalences.add(sub, diff);
            }
                    break;
        }
    }
        }
        return equivalences;
    }
    
    /** loadDictionary / 1
     * Loads only the equivalences into the EquivalenceDictionary.
     * 
     * @param filename: a String describing the path to the source file for the 
     * equivalence rules.
     */
    public void loadDictionary(String filename) {
        FileInputStream fis = null;
        File file = new File(ImproVisor.getDictionaryDirectory(), filename);
        try {
            fis = new FileInputStream(file);
            Tokenizer in = new Tokenizer(fis);
            in.slashSlashComments(true);
            in.slashStarComments(true);
            Object token;

            // Read in S expressions until end of file is reached
            while ((token = in.nextSexp()) != Tokenizer.eof)
            {
                if (token instanceof Polylist) 
                {
                    Polylist contents = (Polylist)token;
                    
                    // Check that polylist has enough fields to be an
                    // equivalence rule
                    if (contents.length() < 3)
                    {
                        ErrorLog.log(ErrorLog.WARNING, 
                                "Error: incorrect equivalence: " + token, true);
                    }
                    // for appropriate rules
                    else
                    {
                        String eqCategory = contents.first().toString();
                        contents = contents.rest();
                        
                        // We only check equivalence rules
                        if (eqCategory.equals("equiv"))
                        {
                            // Take every equivalent chord and add it to
                            // the list of equivalent chords, then add
                            // that as a rule to the dictionary.
                            ArrayList<ChordBlock> newEq = new ArrayList<ChordBlock>();
                            while (contents.nonEmpty())
                            {
                                String chordName = contents.first().toString();
                                contents = contents.rest();
                                ChordBlock nextChord = new ChordBlock(chordName, 
                                                        SubstitutionRule.NODUR);
                                newEq.add(nextChord);
                            }
                            addRule(newEq);
                        }
                    }
                }
                // if a Polylist was incomplete or unparseable
                else
                {
                    ErrorLog.log(ErrorLog.WARNING, 
                            "Improper formatting for a token: " + token, true);
                }
            }
        } catch (FileNotFoundException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Dictionary file not found: " 
                                           + file.getAbsolutePath(), true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ErrorLog.log(ErrorLog.FATAL, "Filestream cannot close", true);
            }
        }
    }
    
    // end of EquivalenceDictionary class
    
}
