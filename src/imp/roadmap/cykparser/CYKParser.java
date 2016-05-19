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

package imp.roadmap.cykparser;
import imp.roadmap.brickdictionary.BrickLibrary;
import imp.roadmap.brickdictionary.Brick;
import imp.roadmap.brickdictionary.ChordBlock;
import imp.roadmap.brickdictionary.Block;
import imp.ImproVisor;
import imp.util.ErrorLog;
import java.io.*;
import java.util.*;
import polya.*;

/** CYKParser
 * 
 * A parser determining the lowest-cost division of a series of chords into
 * Bricks and ChordBlocks.
 * 
 * @author Xanda Schofield
 */

public class CYKParser
{
    private static boolean DEBUG = false;
    
    // Constants used by the CYKParser
    public static final String DICTIONARY_NAME = "My.substitutions";
    public static final String INVISIBLE = "Invisible";
    public static final long SUB_COST = 5;
    

    // Data members //
    private ArrayList<ChordBlock> chords;      // list of chords to be parsed    
    private LinkedList<TreeNode>[][] cykTable; // table for CYK analysis
    private boolean tableFilled;               // a boolean describing if the 
                                               // table is up to date
    
    // Terminal and Nonterminal grammar rules will be imported as lists of
    // strings. These will serve in separate phases of the parsing.
    
    // Nonterminal rules will be BinaryProductions, describing the union of
    // two Blocks into a Brick:
    private LinkedList<BinaryProduction> nonterminalRules;
    // Terminal rules will be UnaryProductions, describing the adoption of
    // one Block as a Brick.
    private LinkedList<UnaryProduction> terminalRules;
    
    // Additional dictionaries describing what chord qualities are equivalent
    // or substitutable are required for populating the table
    private EquivalenceDictionary edict;
    private SubstitutionDictionary sdict;

     
    // Constructors //
    
    /**
     * CYKParser / 0
     * The default CYKParser constructor; initializes it to an empty chord
     * sequence.
     */
    public CYKParser()
    {
        chords = new ArrayList<ChordBlock>();
        cykTable = null;
        nonterminalRules = new LinkedList<BinaryProduction>();
        terminalRules = new LinkedList<UnaryProduction>();
        tableFilled = false;
        edict = new EquivalenceDictionary();
        sdict = new SubstitutionDictionary();
        loadDictionaries(DICTIONARY_NAME);
    }
    
    /**
     * CYKParser / 1
     * A CYKParser built with chords.
     * 
     * @param c: An ArrayList of ChordBlocks to be used to populate the table
     */
    public CYKParser(ArrayList<ChordBlock> c)
    {
        chords = new ArrayList<ChordBlock>();
        chords.addAll(c);
        int size = chords.size();
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
        terminalRules = new LinkedList<UnaryProduction>();
        nonterminalRules = new LinkedList<BinaryProduction>();
        
        edict = new EquivalenceDictionary();
        sdict = new SubstitutionDictionary();
        loadDictionaries(DICTIONARY_NAME);
    }
    
    // Individual data initializers //
    
    /** newChords
     * newChords allows you to change the chords that a CYKParser is analyzing.
     * 
     * @param c: an ArrayList of ChordBlocks to repopulate the table
     */
    
    public void newChords(ArrayList<ChordBlock> c)
    {
        int size = c.size();
        
        chords.clear();
        chords.addAll(c);
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
    }
    
    /** loadDictionaries
     * Initializes the SubstitutionDictionary and EquivalenceDictionary used in
     * the parser.
     * 
     * @param filename, the file where the substitution data is stored
     */
    private void loadDictionaries(String filename)
    {
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
                    
                    // Check that polylist has enough fields to be a useful
                    // substitution rule.
                    // Rules must have a type ("sub" or "equiv") and at least
                    // two chords listed.
                    if (contents.length() < 3)
                    {
                        ErrorLog.log(ErrorLog.SEVERE, "Improper substitution "
                                + "dictionary format", true);
                    }
                    else
                    {
                        String eqCategory = contents.first().toString();
                        contents = contents.rest();
                        
                        // If the rule is a substitution rule, it gets added to
                        // the SubstitutionDictionary sdict
                        if (eqCategory.equals("sub"))
                        {
                            String head = contents.first().toString();
                            contents = contents.rest();
                            SubstitutionRule newSub = 
                                    new SubstitutionRule(head, contents);
                            sdict.addRule(newSub);
                        }
                                
                        // If the rule is an equivalence rule, it gets added to
                        // the EquivalenceDictionary edict
                        else if (eqCategory.equals("equiv"))
                        {
                            ArrayList<ChordBlock> newEq = new ArrayList<ChordBlock>();
                            while (contents.nonEmpty())
                            {
                                String chordName = contents.first().toString();
                                contents = contents.rest();
                                ChordBlock nextChord = new ChordBlock(chordName, 
                                                        SubstitutionRule.NODUR);
                                newEq.add(nextChord);
                            }
                            edict.addRule(newEq);
                        }
                                
                        // If it is neither type of rule, it is not a valid rule
                        // for this file.
                        else
                        {
                            ErrorLog.log(ErrorLog.WARNING, 
                                    "Incorrect rule type " + eqCategory, true);
                        }
                    }
                }
                        
                // if the rule does not even read in as a proper Polylist
                else
                {
                    ErrorLog.log(ErrorLog.WARNING, "Improper formatting for "
                            + "a token " + token.toString(), true);
                }
            }
        // if the dictionary file was not found at the path specified
        } catch (FileNotFoundException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Substitution dictionary not found: " 
                                          + file.getAbsolutePath(), 
                    true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ErrorLog.log(ErrorLog.FATAL, "Filestream cannot close", true);
            }
        }
        
    }
    
    /** createRules
     * Creates binary productions for a BrickLibrary and loads them in to the
     * CYKParser's rule list
     * @param lib: a BrickLibrary
     */
    public void createRules(BrickLibrary lib) {
        
        // Get a single list of all Bricks in the BrickLibrary, regardless of
        // qualifier, and iterate through them
        Collection<Brick> bricks = lib.getFullMap();
        Iterator bIter = bricks.iterator();
        terminalRules = new LinkedList<UnaryProduction>();
        nonterminalRules = new LinkedList<BinaryProduction>();
        // For each Brick in the BrickLibrary, make all the productions needed
        // to be able to produce it at parse time
        while (bIter.hasNext()) {
            Brick b = (Brick)bIter.next();
            String name = b.getName();

            // Rule parsing of rules is dependent on the number of subBlocks
            // in a given brick.
            ArrayList<Block> subBlocks = b.getSubBlocks();
            
            if( subBlocks == null )
              {
                ErrorLog.log(ErrorLog.WARNING, "Brick definition with empty sub-blocks: " + b, true);
                continue;
              }
            String currentName;
            int size = subBlocks.size();
            String mode = b.getMode();
            
            // Error case: a Brick with no contents. If such a Brick is somehow
            // defined, then the Brick will be rejected in parsing, as it could
            // not be displayed or found by parsing.
            if (size < 1)
              {
                ErrorLog.log(ErrorLog.WARNING, "Error: brick of size " + size, 
                        true);
              }
            
            // Unary case: single-Block bricks are added to a separate list of 
            // UnaryProductions, processed after a table cell is filled by the
            // parser
            else if (size == 1) {
                Block sb0 = subBlocks.get(0);
                
                // Avoid potential crash if either subBlock is null.
                // This might occur if a sub-brick is not recognized.
                
                if( sb0 != null )
                  {
                  UnaryProduction u = new UnaryProduction(name, b.getType(), 
                                        b.getKey(), sb0,
                                        true, mode, lib, b.getVariant());
                  terminalRules.add(u);
                  }
            }
            // Ideal case: a Brick of two subBricks. Only one rule is needed,
            // a BinaryProduction with the name of the resulting brick as its 
            // head and each of the subBricks in its body.
            else if (size == 2) {
                Block sb0 = subBlocks.get(0);
                Block sb1 = subBlocks.get(1);
                
                // Avoid potential crash if either subBlock is null.
                // This might occur if a sub-brick is not recognized.
                
                if( sb0 != null && sb1 != null )
                  {
                  BinaryProduction p = new BinaryProduction(name, b.getType(), 
                                         b.getKey(), sb0, sb1, 
                                         true, mode, lib, b.getVariant());
                  nonterminalRules.add(p);
                  }
            }
            
            // Larger case: a Brick of three or more subBricks. The Brick gets
            // divided into a starting Brick whose name won't be printed in a
            // parse tree. Each following rule will have two body blocks, one
            // being the name of the previous production and one being the next
            // subBlock. The last rule gets the name of the final Brick.
            else {
                
                // Note: Crash prevention code should be added to this
                // as in the previous two cases.
                
                // first rule
                currentName = name + b.getVariant() + "1";
                BinaryProduction[] prods = new BinaryProduction[size];
                prods[0] = new BinaryProduction(currentName, INVISIBLE, b.getKey(),
                        subBlocks.get(0), subBlocks.get(1), false, mode, lib, 
                        b.getVariant());
                nonterminalRules.add(prods[0]);
                // second through next to last rules
                for (int i = 2; i < size - 1; i++) {
                    currentName = name + b.getVariant() + i;
                    prods[i-1] = new BinaryProduction(currentName, INVISIBLE,
                            b.getKey(), prods[i-2], subBlocks.get(i), false, 
                            mode, lib, b.getVariant());
                    nonterminalRules.add(prods[i-1]);
                }
                // final rule
                prods[size-2] = new BinaryProduction(name, b.getType(), b.getKey(),
                        prods[size-3], subBlocks.get(size-1), true, mode, lib, 
                        b.getVariant());
                nonterminalRules.add(prods[size-2]);
            }
        
        }
    }
    
    // Parsing methods //
    
    /** fillTable
     * fillTable takes the chord sequence and duration lists and uses them to 
     * fill in the CYK algorithm table. First, it fills in the diagonal
     * with all the possible symbols which could produce the given terminals
     * with that diagonal's index; then, it constructs each level up by finding
     * pairwise combinations of terminals to assemble larger portions of the
     * chord sequence into one symbol.
     */
    public void fillTable()
    {
        // Create nodes for the terminals, and put them into the table.
        int size = this.chords.size();
        for (int i=0; i < size; i++)
          {
            findTerminal(i);
          }
        
        // Iterate through the table by degrees parallel to the diagonal.
        // We use the column where each diagonal starts to determine where
        // the next cell is. We start at 1 because the 0-column diagonal
        // is all ready by the previous step.
        for (int startCol = 1; startCol < size; startCol++) 
        {
            for (int startRow = 0; startRow < (size - startCol); 
                     startRow++) 
            {
                findNonterminal(startRow, startCol+startRow);
            }
        }        
        tableFilled = true;
        
    if( DEBUG )
      {
       System.out.println("CYK Parse Table:");
       System.out.println(printTable());
      }
    }
    
    /** findSolutions
     * findSolutions takes a filled-in table in a CYKParser and returns the 
     * best parse tree (the lowest cost one) as a list of Bricks.
     */
    public ArrayList<Block> findSolution(BrickLibrary lib) {
        
        // First, we assemble the table of minimum-value nodes.
        assert(tableFilled);
        int size = this.chords.size();
        TreeNode[][] minVals = new TreeNode[size][size];
        


        // This loops through every occupied cell in the cykTable and finds 
        // the lowest-cost Node in the list, for the construction of optimal
        // trees later.
        for (int row = 0; row < size; row++) {
            for (int col = row; col < size; col++) {
                
                    minVals[row][col] = new TreeNode();

                    ListIterator<TreeNode> node = cykTable[row][col].listIterator();
                    while (node.hasNext()) {
                        TreeNode nextNode = node.next();
                    if (nextNode.toShow() && 
                        nextNode.lessThan(minVals[row][col]))
                          {
                            minVals[row][col] = nextNode;
                          }
                 
                    }
                }
            }
        
        
        // This is a cost-minimization algorithm looking for the lowest cost
        // way to account for the chords in order with possible brick parses.
        for (int i = size - 2; i >= 0; i--) {
            for (int j = i + 1; j < size; j++) {
                for (int k = i + 1; k <= j; k++) {
                    if (minVals[i][k-1].getCost() + minVals[k][j].getCost() 
                            < minVals[i][j].getCost()){
                        minVals[i][j] = new TreeNode(minVals[i][k-1], minVals[k][j]);
                    }
                }    
            }
        }
    
        // The shortest path in the top right cell gets printed as the best
        // explanation for the whole chord progression
        if( DEBUG )
          {
          showMinVals(minVals);
          }
        return minVals[0][size - 1].toBlocks();
            
    }
    
    public void showMinVals(TreeNode[][] minVals)
      {
        System.out.println("minVals: ");
        for( int row = 0; row < minVals.length; row++ )
          {
            TreeNode[] rowContents = minVals[row];
            for( int col = row; col < rowContents.length; col++ )
              {
                TreeNode L = rowContents[col];
                
                System.out.println("-------------------------------------------\n"
                        + "minVals(" + row + ", " + col + "):\n" + L); 
                }
              }
            System.out.println();
          }

    /** findTerminal
     * findTerminal is a helper function which, for a given index i takes the
     * ith ChordBlock in chords and fills the [i, i] space in the 2D List 
     * array with the symbols which could generate that chord.
     * @param index, the int describing the index of the ChordBlock in the
     *        list of chords which is to be processed in the table
     * @param start, the starting slot in the piece of that ChordBlock
     */
    private void findTerminal(int index)
    {
        // First, the table cell is initialized and the original terminal placed
        // in it as a TreeNode.
        cykTable[index][index] = new LinkedList<TreeNode>();
        ChordBlock currentChord = chords.get(index);
        TreeNode currentNode = new TreeNode(currentChord);
        cykTable[index][index].add(currentNode);
        
        
//        // Then, every chord which the terminal could be substituted for is also
//        // added to that same cell as a TreeNode with the original Chord but a
//        // different name for the TreeNode.
//        SubstituteList subs = edict.checkEquivalence(currentChord);
//        subs.addAll(sdict.checkSubstitution(currentChord));
//        for (int i = 0; i < subs.length(); i++)
//        {
//            currentNode = new TreeNode(subs.getName(i), subs.getKey(i),
//                                                currentChord);
//            cykTable[index][index].add(currentNode);
//        }
        
        // If any UnaryProductions apply to just this ChordBlock, they will
        // be processed and appropriate TreeNodes will be added to the same
        // cell as the original ChordBlock's TreeNode.
        
        LinkedList<TreeNode> unaries = new LinkedList<TreeNode>();
        
        for (TreeNode t : cykTable[index][index])
          {
            for (UnaryProduction rule : terminalRules)
            {
                AbstractProduction.MatchValue match;
                match = rule.checkProduction(t, edict, sdict);
                if (!(match.chordDiff < 0))
                {
                    long cost = SUB_COST;
                    TreeNode newNode = new TreeNode(rule.getHead(),
                                    rule.getType(), rule.getMode(), 
                                    t, match.cost + cost, match.chordDiff, 
                                    rule.getVariant());
                    unaries.add(newNode);
                }
            }
          }
        cykTable[index][index].addAll(unaries);
        
    }
        
/**
 * findNonterminal findNontermimal looks at every single possible combination of
 * nonterminals from previously filled cells and sees if there is a production
 * which will generate those two nonterminals. If so, it adds that new single
 * symbol to the current cell as a TreeNode.
 *
 * @param row: the row number in the table currently being filled
 * @param col: the column number in the table currently being filled
 */
    
private void findNonterminal(int row, int col)
  {
    cykTable[row][col] = new LinkedList<TreeNode>();

    LinkedList<TreeNode> overlaps = new LinkedList<TreeNode>();

    // We make sure that the code loops through the different possible cell
    // pairs, starting at the leftmost and topmost.
    for( int index = 0; index < (col - row); index++ )
      {
        assert (row + index < this.chords.size());


        // We loop through the TreeNodes in each cell, with iter1 being
        // for the cell in the same row and iter2 being for the cell in the
        // same column as the current cell.
        ListIterator<TreeNode> iter1 = cykTable[row][row + index].listIterator();

        while( iter1.hasNext() )
          {
            try
              {
                // Have gotten ConcurrentModificationException here. Not sure why. RK
                TreeNode symbol1 = iter1.next();

                if( !symbol1.isSectionEnd() && !symbol1.isOverlap() )
                  {
                    ListIterator<TreeNode> iter2 = cykTable[row + index + 1][col].listIterator();

                    while( iter2.hasNext() )
                      {
                        // possible to get ConcurrentModificationException here!
                        // during fillTable()
                        TreeNode symbol2 = iter2.next();
                        if( !symbol2.isOverlap() )
                          {
                            // We check every rule against each pair of symbols.
                            ListIterator<BinaryProduction> iterRule = nonterminalRules.listIterator();

                            while( iterRule.hasNext() )
                              {
                                BinaryProduction rule = iterRule.next();

                                // checkProduction returns a long describing the key
                                // of the resulting brick if rule applies to symbol1
                                // and symbol2, or -1 if no such brick can be made.
                                AbstractProduction.MatchValue match;
                                match = rule.checkProduction(symbol1, symbol2);
                                // If newKey comes up with an appropriate key distance,
                                // make a new TreeNode for the current two TreeNodes.
                                if( !(match.chordDiff < 0) )
                                  {

                                    // The cost becomes larger for the final TreeNode if
                                    // either the first or second TreeNode uses a chord
                                    // substitute
                                    long cost = 0;
                                    if( symbol1.isSub() )
                                      {
                                        cost += SUB_COST;
                                      }
                                    if( symbol2.isSub() )
                                      {
                                        cost += SUB_COST;
                                      }
                                    if( symbol2.isOverlap() )
                                      {
                                        cost += TreeNode.OVERLAP_COST;
                                      }
                                    if( match.familyMatch )
                                      {
                                        cost += SUB_COST;
                                      }

                                    TreeNode newNode = new TreeNode(rule.getHead(),
                                                                    rule.getType(), rule.getMode(),
                                                                    symbol1, symbol2, match.cost + cost, 
                                                                    match.chordDiff, rule.getVariant());
                                    // Have gotten NullPointerException here. RK
                                    if( cykTable[row][col] != null )
                                      {
                                        cykTable[row][col].add(newNode);
                                      }

                                    // Additionally, if this block could overlap with 
                                    // another later one, then we store a TreeNode 
                                    // with a 0-duration final chord to put in the 
                                    // table later.
                                    if( !(rule.getType().equals("On-Off"))
                                            && !(rule.getType().equals("Off-On"))
                                            && !(symbol2.isSectionEnd())
                                            && !(symbol2.isOverlap())
                                            && !(symbol2.getDuration() == 0) )
                                      {
                                        overlaps.add(newNode.overlapCopy());
                                      }
                                  }
                              }
                          }
                      }
                  }
              }
            catch( ConcurrentModificationException e )
              {
              }
          }
      }
        
        LinkedList<TreeNode> unaries = new LinkedList<TreeNode>();
        
        // After the cell is filled up with all possible BinaryProduction 
        // results, the parser runs through each TreeNode in the cell itself
        // with all UnaryProductions to see if additional TreeNodes should 
        // be added for unary Bricks. Overlaps are not processed for these.
        for (TreeNode t : cykTable[row][col])
          {
            for (UnaryProduction rule : terminalRules)
            {
                AbstractProduction.MatchValue match;
                match = rule.checkProduction(t, edict, sdict);
                if (!(match.chordDiff < 0))
                {
                    long cost = 0;
                    if (match.familyMatch) cost += SUB_COST;
                    TreeNode newNode = new TreeNode(rule.getHead(),
                                    rule.getType(), rule.getMode(), 
                                    t, match.cost + cost, match.chordDiff, 
                                    rule.getVariant());
                    unaries.add(newNode);
                }
            }
          }
        cykTable[row][col].addAll(unaries);
        // TreeNodes in overlaps, due to the zero duration of the last chord, 
        // justify one fewer chords than those in [row][col]. They are placed
        // one cell to the left.
        cykTable[row][col-1].addAll(overlaps);
    }
    /** parse / 2
     * A method taking in blocks and a BrickLibrary and parsing them, returning
     * the parsed version.
     * @param blocks: an ArrayList of Blocks to be parsed
     * @param lib: a BrickLibrary of Bricks to guide parsing
     * @return parsed chords as bricks
     */
    public ArrayList<Block> parse(ArrayList<Block> blocks, BrickLibrary lib) {
        ArrayList<Block> solution = new ArrayList<Block>();
        // Load in chords
        
        ArrayList<ChordBlock> ch = new ArrayList<ChordBlock>();
        for (Block b: blocks) {
            ch.addAll(b.flattenBlock());
            if (b.isSectionEnd()) {
                newChords(ch);
                fillTable();
                solution.addAll(findSolution(lib));
                ch.clear();
            }
        }
                
        if (!ch.isEmpty()) {
            newChords(ch);
            fillTable();
            solution.addAll(findSolution(lib));
        }

        solution = PostProcessor.findLaunchers(solution);
         for ( Block b : solution)
        {
            b.setStyleName(blocks.get(0).getStyleName());
        }
        return solution;
    }
        
    // Miscellaneous methods
    
    /** printTable()
     * Prints out the entirety of the contents of the CYKTable. Used for 
     * debugging parser output.
     * @return a String describing the entire table, with the format for each
     *         cell:
     * 
     * (row, col)
     * [TreeNode1] in [key1] ([duration1]) [child TreeNodes, if any]
     * [TreeNode2] in [key2] ([duration2]) [child TreeNodes, if any]
     * ...
     * ----------------------
     */
    public String printTable() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < cykTable.length; i++)
          {
            for (int j = i; j < cykTable.length; j++)
            {
            
                buffer.append("-------------------------------------------\n"
                        +"CYK (" + i + ", " + j + "):\n");
                for (TreeNode t : cykTable[i][j])
                {
                    buffer.append(t);

                    buffer.append("\n");
                      }
                }         
            }
        return buffer.toString();
          }
    // end of CYKParser class
}