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


package imp.roadmap.brickdictionary;
import imp.roadmap.cykparser.PostProcessor;
import imp.data.ChordSymbol;
import imp.util.ErrorLog;
import java.util.Collection;
import polya.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * purpose: Methods relating to the brick library (dictionary)
 * @author Zachary Merritt
 */
public class BrickLibrary {

    /**
     * Keyword used in defining bricks in a dictionary.
     */

    public static final String DEF_BRICK = "defbrick";
 
    /**
     *  This is the conjunction to be used to describe appendages to Cadences
     *  such as Overrun and Dropback
     */
    public static String CONJUNCTION = " + ";
    
    
    /**
     * The name for an Overrun
     */

    public static String OVERRUN = "Overrun";

        
    /**
     * The name for a Dropback
     */

    public static String DROPBACK = "Dropback";
    
    
    // Lists of key names, with indices corresponding to numeric key value
    
    private static final String[] KEY_NAME_ARRAY = 
        {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};
    
    private static final String[] KEY_NAME_ARRAY_SHARPS =
        {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    // the default cost of a type with no cost specified
    private static final long DEFAULT_COST = 40;
    
    // the string describing an invisible brick's type
    public static final String INVISIBLE = "Invisible";
   
    // Data Members //
    private LinkedHashMap<String, LinkedList<Brick>> brickMap; // all bricks
    
    private LinkedHashMap<String, Long> costMap;          // brick costs by type
    
    public PostProcessor processor;                  // the postprocessing unit
    
    
    /** BrickLibrary / 0
     * Default constructor for an empty BrickLibrary with no specified types
     */
    public BrickLibrary() {
        brickMap = new LinkedHashMap<String, LinkedList<Brick>>();
        costMap = new LinkedHashMap<String, Long>();
    }
    
    /** getNames
     * Returns the list of the names of all bricks stored in the dictionary
     * @return an array of Strings
     */
    public String[] getNames() {
        return brickMap.keySet().toArray(new String[0]);
    }
    
    /** addBrickDefinition
     * When a new brick is created in the library, adds its contents to the 
     * current brickMap and adds its definition to the dictionary file
     * @param brick, a newly created Brick
     */
    public void addBrickDefinition(Brick brick, String dictionaryFilename)
    {
        // add the brick to the current BrickLibrary
        boolean added = addBrick(brick);
        if (!added)
            return;
        
        Brick definitionBrick = new Brick(brick);
        // make a properly-formatted brick definition
        Polylist defn = definitionBrick.toBrickDefinition();
        String defnString = defn.toString();
        if (!brick.getVariant().equals(""))
            defnString.replaceFirst(" \\(", "\\(");    
        defnString = defnString.replaceAll(" \\(", "\n        \\(");
        
        // write out the string with the definition to the end of the file
        try {
            FileOutputStream out = new FileOutputStream(dictionaryFilename, true);
            out.write("\n".getBytes());
            out.write(defnString.getBytes());
            out.write("\n".getBytes());
            out.close();
        } catch (IOException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Cannot write to dictionary");
        }
        
    }
    
    /** addBrick
     * Adds a brick to the library's brickMap
     * @param brick, a created Brick
     */
    public boolean addBrick(Brick brick) {
        if(brickMap.containsKey(brick.getName()))
        {
            // first, we check if the brick is a duplicate or just a brick with
            // an already-used name but a different qualifier. Presently, a
            // doubly-defined brick will have both definitions added to the
            // dictionary but will warn a user if the two definitions are 
            // identical.
            LinkedList<Brick> sameStemList = brickMap.get(brick.getName());
            for (Brick sameStem : sameStemList)
            {
                if (sameStem.getVariant().equals(brick.getVariant()))
                {
                    ErrorLog.log(ErrorLog.WARNING, "Dictionary already contains " +
                    brick.getName() + "(" + brick.getVariant() + "): will not"
                            + " add to dictionary", true);
                    return false;
                }
            }
            sameStemList.add(brick);
        }
        else
        {
            // adding a brick with a new brickname
            LinkedList<Brick> brickList = new LinkedList<Brick>();
            brickList.add(brick);
            this.brickMap.put(brick.name, brickList);
        }
        
        // special rule for creating overruns
        if (brick.getType().equals("Cadence")) {
            String overrunName = brick.getName() + CONJUNCTION + OVERRUN;
            long overrunKeyNum = brick.getKey();
            String overrunType = OVERRUN;
            String overrunMode = brick.getMode();
            String overrunQualifier = brick.getVariant();

            // take blocks from regular cadence and add the next chord
            // in the circle of fifths with the same quality as the
            // resolution
            ArrayList<Block> overrunBlocks = new ArrayList<Block>();
            overrunBlocks.add(brick);
            ArrayList<ChordBlock> chords = brick.flattenBlock();
            ChordBlock prevChord = chords.get(chords.size() - 1);
            ChordBlock overrunChord = 
                       new ChordBlock(prevChord.transposeName(5), 
                                      prevChord.getDuration());
            overrunBlocks.add(overrunChord);

            // make a new brick from this list of blocks
            Brick overrun = new Brick(overrunName, overrunQualifier, overrunKeyNum,
                    overrunType, overrunBlocks, overrunMode);
            addBrick(overrun);
                    
            String dropbackName = brick.getName() + CONJUNCTION + DROPBACK;
            long dropbackKeyNum = brick.getKey();
            String dropbackType = DROPBACK;
            String dropbackMode = brick.getMode();
            String dropbackQualifier = brick.getVariant();

            // take blocks from regular cadence and add a relative VI dominant
            // to form a dropback.
            
            // FIX: This is not the only possible dropback!!
            // For example, we want to allow iii-IV's major and minor.
            
            ArrayList<Block> dropbackBlocks = new ArrayList<Block>();
            dropbackBlocks.add(brick);
            
            String dropbackChordName = keyNumToName((brick.getKey() + 9) % 12);
            
            // was wrong: String dropbackChordName = keyNumToName((prevChord.getKey() + 9) % 12);
            
            /* I think it was wrong to include this in the first place:
            
            if (dropbackMode.equals("minor"))
                dropbackChordName += "7b5";
            else
             */
            
            dropbackChordName += 7;
            
            
            ChordBlock dropbackChord = 
                       new ChordBlock(dropbackChordName, 
                                      prevChord.getDuration());
            dropbackBlocks.add(dropbackChord);

            // make a new brick from this list of blocks
            Brick dropback = new Brick(dropbackName, dropbackQualifier, dropbackKeyNum,
                    dropbackType, dropbackBlocks, dropbackMode);
            
            addBrick(dropback);
        }
        
        return true;
    }
    
    /** getBrick (definition)
     * Gets the default Brick with the given name from the dictionary in key k
     * @param s, the retrieved Brick's name, a String
     * @param k, the retrieved Brick's key, a long
     * @return the transposed Brick
     */
    public Brick getBrick(String s, long k) {
        if(brickMap.containsKey(s))
        {
            LinkedList<Brick> possibleBricks = brickMap.get(s);
            for (Brick oldBrick : possibleBricks)
                if (!oldBrick.getType().equals(INVISIBLE)) {
                    Brick brick = new Brick(oldBrick);
                    brick.transpose((k-brick.getKey() + 12)%12);
                    return brick;
                }
        }
        // if no brick is returned
        ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                s, true);
        return null;
    }
    
    /** getBrick (with qualifier)
     * Gets a particular Brick with a given name and qualifier from the 
     * dictionary in key k
     * @param s, the retrieved Brick's name, a String
     * @param q, the retrieved Brick's qualifier, a String
     * @param k, the retrieved Brick's key, a long
     * @return the transposed Brick
     */
    public Brick getBrick(String s, String q, long k) {
        if(brickMap.containsKey(s))
        {
            Brick brick = null;
            for (Brick b : brickMap.get(s))
                if (b.getVariant().equals(q)) {
                    brick = new Brick(b);
                    break;
                }
            
            if (brick != null) {
                brick.transpose((k-brick.getKey() + 12)%12);
                return brick;
            }
            else
            {
                ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                        s + " with qualifier " + q, true);
                return null;
            }
            
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
            return null;
        }
    }
    
    /** getBrick (with duration)
     * Gets a particular Brick with a given name and qualifier from the 
     * dictionary in key k
     * @param s, the retrieved Brick's name, a String
     * @param k, the retrieved Brick's key, a long
     * @param d, the duration of the Brick, an int
     * @return the transposed Brick
     */
    public Brick getBrick(String s, long k, int d) {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s).getFirst());
            brick.transpose((k-brick.getKey() + 12)%12);
            brick.setDuration(d);
            return brick;
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
            return null;
        }
    }
    
    /** getBrick (with qualifier)
     * Gets a particular Brick with a given name and qualifier from the 
     * dictionary in key k
     * @param s, the retrieved Brick's name, a String
     * @param q, the retrieved Brick's qualifier, a String
     * @param k, the retrieved Brick's key, a long
     * @param d, the duration of the Brick, an int
     * @return the transposed Brick
     */
    public Brick getBrick(String s, String q, long k, int d) {
        if(brickMap.containsKey(s))
        {
            Brick brick = null;
            for (Brick b : brickMap.get(s))
                if (b.getVariant().equals(q)) {
                    brick = new Brick(b);
                    break;
                }
            if (brick != null) {
                brick.transpose((k-brick.getKey() + 12)%12);
                brick.setDuration(d);
                return brick;
            }
            
            else {
                ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
                return null;
            }
                
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
            return null;
        }
    }
    
    /** hasBrick
     * Checks if a Brick with a given name is in the dictionary
     * @param s, the Brick name
     * @return a boolean
     */
    public boolean hasBrick(String s)
    {
        return (brickMap.containsKey(s));
    }
    
    /** getFullMap
     * Gets the list of all Bricks in the dictionary as a single collection
     * @return a Collection of Bricks
     */
    public Collection<Brick> getFullMap() {
        LinkedList<Brick> values = new LinkedList<Brick>();
        for (LinkedList<Brick> brickname : brickMap.values())
        {
            values.addAll(brickname);
        }
        
        return values;
    }
    
    /** getMap
     * Gets the organized list of all Bricks in the dictionary
     * @return a LinkedList of LinkedLists of Bricks
     */
    public LinkedList<LinkedList<Brick>> getMap() {
        LinkedList<LinkedList<Brick>> values = new LinkedList();
        for (LinkedList<Brick> brickname : brickMap.values())
        {
            LinkedList<Brick> newlist = new LinkedList<Brick>();
            for (Brick brick : brickname)
                if (!brick.getType().equals(INVISIBLE))
                    newlist.add(brick);
            if (!newlist.isEmpty())
                values.add(newlist);
        }
        
        return values;
    }
    
    /** removeBrick
     * Removes all Bricks matching a given Brick from the dictionary
     * @param brick, the Brick to be removed itself
     */
    public void removeBrick(Brick brick) {
        this.brickMap.remove(brick.name);
    }
    
    /** removeBrick
     * Removes all Bricks with a given name from the dictionary
     * @param brickName, the name of the Brick(s) to be removed
     */
    public void removeBrick(String brickName) {
        this.brickMap.remove(brickName);
    }
    
    /** exileBrick
     * Takes a Brick out of the visible brick library
     * @param brick, the brick to remove
     */
    public void exileBrick(Brick brick, String dictionaryFilename) {
        
        //brick.printBrick();
        
        // define the search term (the name) for the given brick
        String brickType = " " + brick.getType() + " ";
        String brickDefHead = DEF_BRICK + dashed(brick.getName());
        String qualifier = brick.getVariant();
        if (!qualifier.isEmpty())
            brickDefHead += "(" + qualifier + ")";
        brickDefHead += " ";
        
        // open the file and read in its contents
        try {
            File dictionary = new File(dictionaryFilename);
            FileReader in = new FileReader(dictionary);
            BufferedReader reader = new BufferedReader(in);
            
            String line = reader.readLine();
            String newfile = "";
            
            while(line != null)
            {
                // Exiling a brick is just taking the type and changing it
                // to be invisible
                if (line.contains(brickDefHead))
                {
                    line = line.replaceFirst(brickType, " " + INVISIBLE + " ");
                }
                
                newfile += line + "\r\n";
                line = reader.readLine();
            }
            
            // write out the modified dictionary
            FileWriter writer = new FileWriter(dictionaryFilename);
            writer.write(newfile);
            writer.close();
            processDictionary(dictionaryFilename);
            
        }
        catch (IOException ioe)
        {
            ErrorLog.log(ErrorLog.SEVERE, "Could not modify brick in dictionary");
        }
        
    }
    
    /** printDictionary
     * Prints every brick in the dictionary to System.err
     */
    public void printDictionary() {
        Iterator iter = getFullMap().iterator();
        
        while(iter.hasNext())
        {
            Brick currentBrick = (Brick)iter.next();
            currentBrick.printBrick();
        }
    }
    
    /** addType
     * Adds a given type to the dictionary with the default cost
     * @param t, a type, a String
     */
    public void addType(String t) {
        costMap.put(t, DEFAULT_COST);
    }
    
    /** addType
     * Adds a given type to the dictionary with the specified cost
     * @param t, a type, a String
     * @param c, a cost, a long
     */
    public void addType(String t, long c) {
        costMap.put(t, c);
    }
    
    /** getCost
     * Gets the cost associated with the given type
     * @param t, the type whose cost is desired (a String)
     * @return the cost, a long
     */
    public long getCost(String t) {
        if (!hasType(t)) 
            ErrorLog.log(ErrorLog.SEVERE, "Type does not exist, will register"
                    + "as an invisible brick: " + t);
        return costMap.get(t);    
    }
    
    /** hasType
     * Checks if a given type is contained in the dictionary
     * @param t, a type (a String)
     * @return a boolean
     */
    public boolean hasType(String t) {
        return costMap.containsKey(t);
    }
    
    /** getTypes
     * Gets the list of all types in the dictionary
     * @return an array of Strings of types
     */
    public String[] getTypes() {
        return costMap.keySet().toArray(new String[0]);
    }
    
    /** isValidKey
     * Checks if a given key's name actually describes a key
     * @param keyName, a key's name as a String
     * @return a boolean
     */
    public static Boolean isValidKey(String keyName) {
        return keyName.equals("C") || keyName.equals("B#") || 
                keyName.equals("C#") || keyName.equals("Db") ||
                keyName.equals("D") ||
                keyName.equals("D#") || keyName.equals("Eb") ||
                keyName.equals("E") || keyName.equals("Fb") ||
                keyName.equals("F") || keyName.equals("E#") ||
                keyName.equals("F#") || keyName.equals("Gb") ||
                keyName.equals("G") ||
                keyName.equals("G#") || keyName.equals("Ab") ||
                keyName.equals("A") ||
                keyName.equals("A#") || keyName.equals("Bb")||
                keyName.equals("B") || keyName.equals("Cb");
    }
    
    /** keyNameToNum
     * Takes a key by name and returns the long describing that key
     * @param keyName, a key as a String
     * @return the same key as a long
     */
    public static long keyNameToNum(String keyName) {
        if(keyName.equals(""))
            return -1;
        if(keyName.equals("C") || keyName.equals("B#"))
            return 0;
        if(keyName.equals("C#") || keyName.equals("Db"))
            return 1;
        if(keyName.equals("D"))
            return 2;
        if(keyName.equals("D#") || keyName.equals("Eb"))
            return 3;
        if(keyName.equals("E") || keyName.equals("Fb"))
            return 4;
        if(keyName.equals("F") || keyName.equals("E#"))
            return 5;
        if(keyName.equals("F#") || keyName.equals("Gb"))
            return 6;
        if(keyName.equals("G"))
            return 7;
        if(keyName.equals("G#") || keyName.equals("Ab"))
            return 8;
        if(keyName.equals("A"))
            return 9;
        if(keyName.equals("A#") || keyName.equals("Bb"))
            return 10;
        if(keyName.equals("B") || keyName.equals("Cb"))
            return 11;
        else
        {
            ErrorLog.log(ErrorLog.SEVERE, "Incorrect key formatting: " + keyName);
            return -1;
        }
    }
    
    /** keyNumToName
     * Takes a key as a long and converts it to a key as a String with flats if
     * accidentals are necessary
     * @param keyNum, a long describing a key
     * @return a String of the same key
     */
    public static String keyNumToName(long keyNum) {
        if(keyNum >= 0 && keyNum < 12) {
            return KEY_NAME_ARRAY[(int)keyNum];
        }
        else if (keyNum == -1)
            return "";
        else
        {
            ErrorLog.log(ErrorLog.FATAL, "Internal: Incorrect key number: " + keyNum);
            return "";
        }
    }
    
    /** keyNumToName
     * Takes a key as a long and whether or not to use sharps and converts it 
     * to a key as a String
     * @param keyNum, a long describing a key
     * @boolean sharps, a boolean describing whether or not to use sharps
     * @return a String of the same key
     */
    public static String keyNumToName(long keyNum, boolean sharps) {
        if(sharps) {
            if(keyNum >= 0 && keyNum < 12) {
            return KEY_NAME_ARRAY_SHARPS[(int)keyNum];
            }
            else if (keyNum == -1) {
                return "";
            }
            else
            {
                ErrorLog.log(ErrorLog.FATAL, "Internal: Incorrect key number: " + keyNum);
                return "";
            }
        }
        else {
            return keyNumToName(keyNum);
        }
    }
        
    /** processDictionary
     * Reads in all the dictionary information in the file to define appropriate
     * objects in the BrickDictionary.
     * @throws IOException 
     */
    public void processDictionary(String dictionaryFilename) throws IOException {
        
        FileInputStream fis = new FileInputStream(dictionaryFilename);
        Tokenizer in = new Tokenizer(fis);
        in.slashSlashComments(true);
        in.slashStarComments(true);
        Object token;
        
        ArrayList<Polylist> equivalenceRules = new ArrayList<Polylist>();
        ArrayList<Polylist> diatonicRules = new ArrayList<Polylist>();
        LinkedHashMap<String, LinkedList<Polylist>> polymap = 
                new LinkedHashMap<String, LinkedList<Polylist>>();
        brickMap.clear();
        
        // Read in S expressions until end of file is reached
        while ((token = in.nextSexp()) != Tokenizer.eof)
        {
          //System.out.println("Token = " + token);
            if (token instanceof Polylist) 
            {
                Polylist contents = (Polylist)token;
                
                // Check that polylist has enough fields to be a brick 
                // Needs BlockType (i.e. DEF_BRICK), name, key, and contents
                if (contents.length() < 2)
                {
                    ErrorLog.log(ErrorLog.WARNING, "Improper formatting for"
                            + " a BrickDictionary item: " + contents, true);
                }
                        
                else
                {
                    String blockCategory = contents.first().toString();
                    contents = contents.rest();
                    
                    // Equivalence rules for the postprocessor
                    if (blockCategory.equals("equiv"))
                    {
                        if(contents.isEmpty())
                        {
                            ErrorLog.log(ErrorLog.WARNING, "Empty equivalence "
                                    + "rule in dictionary");
                        }
                        else
                        {
                            Polylist chords = ChordSymbol.chordSymbolsFromStrings(contents);
//                            System.out.println(chords.toStringSansParens());
                            equivalenceRules.add(chords);
                        }
                    }
                    
                    // Diatonic rules for the postprocessor
                    else if (blockCategory.equals("diatonic"))
                    {
                        if(contents.isEmpty())
                        {
                            ErrorLog.log(ErrorLog.WARNING, "Empty diatonic "
                                    + "rule in dictionary");
                        }
                        else
                        {
                            String modeTag = contents.first().toString();
                            contents = contents.rest();
                            Polylist p = ChordSymbol.chordSymbolsFromStrings(contents);
                            p = p.cons(modeTag);
//                            System.out.println(p.toStringSansParens());
                            diatonicRules.add(p);
                        }
                    }
                    
                    // Type definitions with costs
                    else if (blockCategory.equals("brick-type"))
                    {
                        if (contents.length() != 2 && contents.length() != 1)
                            ErrorLog.log(ErrorLog.WARNING, "Not a correct "
                                    + "brick-type declaration: " + contents);
                        else {
                            String type = contents.first().toString();
                            contents = contents.rest();
                            if (contents.isEmpty()) {
                                addType(type);
                            }
                            else
                            {
                                Object cost = contents.first();
                                if (cost instanceof Long)
                                    addType(type, (Long)cost);
                                else {
                                    ErrorLog.log(ErrorLog.WARNING, "Incorrect "
                                            + "cost for brick type: " + type);
                                    addType(type);
                                }
                            }
                        }
                    }
                    
                    // Brick definitions themselves
                    else if (blockCategory.equals(DEF_BRICK) && contents.length() > 4)
                    {
                        // read in the information as a saved polylist to be 
                        // constructed properly as a Brick later
                        String brickName = dashless(contents.first().toString());
                        contents = contents.rest();
                        if (polymap.containsKey(brickName))
                                polymap.get(brickName).add((Polylist)token);
                        else {
                            LinkedList<Polylist> newKey = new LinkedList<Polylist>();
                            newKey.add((Polylist)token);
                            polymap.put(brickName, newKey);
                        }
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.WARNING, "Improper type for "
                            + "a BrickDictionary item: " + token, true);
                    }
                }
            }
            else
            {
                ErrorLog.log(ErrorLog.WARNING, "Improper formatting for "
                    + "a token: " + token, true);
            }
        }
        
        // for each set of bricks with the same name
        for (LinkedList<Polylist> brickStem : polymap.values()) {
            // for each unprocessed brick definition
            for (Polylist contents : brickStem) {
            
                // pull out the name, qualifier, and contents
                contents = contents.rest();
                String brickName = dashless(contents.first().toString());
                contents = contents.rest();

                String brickQualifier = "";
                if (contents.first() instanceof Polylist)
                {
                    brickQualifier = ((Polylist)contents.first()).toStringSansParens();
                    contents = contents.rest();
                }

                String brickMode = contents.first().toString();
                contents = contents.rest();

                String brickType = contents.first().toString();
                contents = contents.rest();
                if (!hasType(brickType))
                    ErrorLog.log(ErrorLog.WARNING, brickName + " is of "
                                + "uninitialized type " + brickType + 
                                "; will register as non-brick");

                String brickKeyString = contents.first().toString();
                contents = contents.rest();
                long brickKeyNum = keyNameToNum(brickKeyString);

                // add the brick, recursively defining subbricks
                Brick currentBrick = new Brick(brickName, brickQualifier, brickKeyNum,
                           brickType, contents, this, brickMode, polymap);
                addBrick(currentBrick);
            }
            
            
            
        }
        // initialize the postprocessor with the rules for diatonic key checking
        processor = new PostProcessor(equivalenceRules, diatonicRules);
    }
    
    /** dashless
     * Helper function to remove dashes from brick names
     * @param s, a String
     * @return a String with dashes replaced with spaces
     */
    public static String dashless(String s) {
        return s.replace('-', ' ');
    }
    
    public static String dashed(String s) {
        return s.replace(' ', '-');
    }
    
    /** writeDictionary
     * Writes out an entire dictionary of definitions. Currently deprecated.
     * @param filename, the file to write to
     */
    public void writeDictionary(String filename) {
        FileWriter fstream;
        try {
            fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            
            out.write("\\\\ Type Definitions\n\n");
            
            Set<String> types = costMap.keySet();
            for (String type : types)
            {
                long cost = costMap.get(type);
                Polylist brickType = Polylist.list("brick-type", type, cost);
                out.write(brickType.toString());
                out.write("\n");
            }
            
            out.write("\n\n\\\\ Brick Definitions\n\n");
            
            for (Brick brick : getFullMap())
            {
                out.write(brick.toPolylist().toString());
                out.write("\n\n");
            }
            
        } catch (IOException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Could not write dictionary file.");
        }  
    }
    
    // end of class BrickLibrary

    
}
