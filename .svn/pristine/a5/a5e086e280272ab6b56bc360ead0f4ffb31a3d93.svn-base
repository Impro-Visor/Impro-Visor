/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.lickgen.transformations;

import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.gui.Notate;
import imp.gui.TransformPanel;
import java.io.File;
import java.io.FileNotFoundException;
import polya.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Holds an entire grammar for a transform
 * 
 * @author Alex Putman
 */
public class Transform 
{

private MelodyPart startingNotes;
public ArrayList<Substitution> substitutions;
public boolean debug;
public boolean hasChanged;

/**
 * Create an empty transform
 */
public Transform()
{
    debug = false;
    hasChanged = false;
    substitutions = new ArrayList<Substitution>();
}

public Transform copy(){
    Transform newTrans = new Transform();
    newTrans.debug = debug;
    newTrans.hasChanged = hasChanged;
    for(Substitution s : substitutions){
        newTrans.addSubstitution(s.copy());
    }
    return newTrans;
}

public Transform(File file){
    this(fileToString(file));
}

private static String fileToString(File file){
    String transformStr = "";
    try {
        transformStr = new Scanner(file).useDelimiter("\\Z").next();
    } catch (FileNotFoundException ex) {
        Logger.getLogger(TransformPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
    return transformStr;
}

/**
 * Create a transform from a string containing a transformational grammar
 * @param subs                  String containing the grammar
 */
public Transform(String subs)
{
    debug = false;
    hasChanged = false;
    substitutions = new ArrayList<Substitution>();
    Polylist polysubs = Polylist.PolylistFromString(subs);
    
    while(polysubs.nonEmpty())
    {
        if(polysubs.first() instanceof Polylist)
        {
            Substitution sub = new Substitution((Polylist) polysubs.first());
            substitutions.add(sub);
        }
        polysubs=polysubs.rest();
    }
}

/**
 * Create a new transformation with identity substitutions
 */
public static Transform makeTransformWithIdentities()
{
    Transform transform = new Transform();
    Substitution motifIdentity = Substitution.makeIdentity("motif");
    Substitution embIdentity = Substitution.makeIdentity("embellishment");
    
    transform.addSubstitution(motifIdentity);
    transform.addSubstitution(embIdentity);
    
    return transform;
}

/**
 * Adds a substitution to the list of substitutions
 * @param sub
 */
public void addSubstitution(Substitution sub)
{
    
    String name = sub.getName();
    int index = indexOf(name);
    if(index<0){
        //if there isn't another sub with that name, add to the end
        substitutions.add(sub); 
    }else{
        //remove old sub
        Substitution oldSub = substitutions.remove(index);
        Substitution merged = merge(oldSub, sub);
        merged.setName(name);
        substitutions.add(index, merged);
    }
    
}

public static Substitution merge(Substitution s1, Substitution s2){
    Substitution merged = new Substitution();
    
    ArrayList<Transformation> trans1 = s1.transformations;
    ArrayList<Transformation> trans2 = s2.transformations;
    
    for(Transformation t : trans1){
        merged.addTransformation(t);
    }
    
    for(Transformation t : trans2){
        merged.addTransformation(t);
    }
    
    return merged;
}

public int indexOf(String name){
    for(int i = 0; i<substitutions.size(); i++){
        if(substitutions.get(i).getName().equals(name)){
            return i;
        }
    }
    return -1;
}

public Substitution removeSubstitution(String name) 
{
    //if the substitution is there, remove it and return it
    for(Substitution sub : substitutions){
      if(sub.getName().equals(name)){
          substitutions.remove(sub);
          return sub;
      }
    }
    //else, make a new substitution with that name and return it
    Substitution newSub = new Substitution();
    newSub.setName(name);
    return newSub;
}

/**
 * Remove a substitution from the list of substitutions
 * @param sub
 */
public void removeSubstitution(Substitution sub)
{
    substitutions.remove(sub);
}

/**
 * Applies the substitutions randomly by weight to a melody
 * @param melody        Melody to apply substitutions to
 * @param chords        ChordPart of the melody
 * @return the transformed melody
 */
public MelodyPart applySubstitutionsToMelodyPart(MelodyPart melody, ChordPart chords, boolean enforceDuration)
{
    
    //TIMING
    long startTime = System.currentTimeMillis();
    
    startingNotes = melody.copy();
    
    ArrayList<ArrayList<Substitution>> subTypes = new ArrayList<ArrayList<Substitution>>();
    ArrayList<Substitution> substitutionsMotif = new ArrayList<Substitution>(); 
    ArrayList<Substitution> substitutionsEmbellish = new ArrayList<Substitution>(); 
    
    for(Substitution sub: substitutions)
    {
        String type = sub.getType();
        if(type.equals("motif"))
            substitutionsMotif.add(sub);
        else if(type.equals("embellishment"))
            substitutionsEmbellish.add(sub);
    }
    subTypes.add(substitutionsMotif);
    subTypes.add(substitutionsEmbellish);
    
    MelodyPart transMelody = melody.copy();
    MelodyPart transformed;
    for(ArrayList<Substitution> subs: subTypes)
    {
        if(subs.size()>0)
        {
            transformed = applySubstitutionType(subs, 
                                                transMelody, 
                                                chords,
                                                enforceDuration);
            transMelody = transformed.copy();
        }
        
    }
    
            
    //TIMING
    long stopTime = System.currentTimeMillis();
    double timeInSecs = .001*(stopTime-startTime);
    System.out.println("Time elapsed: "+timeInSecs);
    
    return transMelody;
}

/**
 * Apply a certain arraylist of substitutions to a melody
 * @param substitutions         substitutions to apply
 * @param transNotes            melody to transform
 * @param chords                chordPart of transNotes
 * @return                      transformed melody
 */
private MelodyPart applySubstitutionType(ArrayList<Substitution> substitutions, 
                                         MelodyPart transNotes, 
                                         ChordPart chords,
                                         boolean enforceDuration)
{
    
    MelodyPart subbedMP = new MelodyPart();
    
    int[] startingSlot = new int[2];
    // the returned slot to start pasting over
    startingSlot[0] = 0;
    // the returned slot to start transforming on again
    startingSlot[1] = 0;
    // go through each index, trying to apply the substitutions at the slot.
    // if a substitution is applied, change the index to the end of the applied
    // melody, if not, just go to the next note. 
    while(transNotes.size() > transNotes.getNextIndex(startingSlot[0]-1))
    {
        if(debug)
        {
            System.out.println("\tYet to parse: " + 
                               transNotes.extract(startingSlot[0], 
                                                  transNotes.getSize()) +
                               "\n\tSubstituted: " + 
                               subbedMP.extract(0, startingSlot[0] - 1));
        }
        
        // sort the substitutions by weight randomly
        ArrayList<Substitution> full = new ArrayList<Substitution>();
        for(Substitution sub : substitutions)
        {
            if(sub.getEnabled() && applicable(sub, transNotes, chords, startingSlot[0]))
            {
                int weight = sub.getWeight();
                for(int i = 0; i < weight; i++)
                    full.add(sub);
            }
        }
        
        
//        if(full.size() < 1){
//            System.out.println("No subs are applicable to this note");
//            //return transNotes;
//        }
            
        
        Collections.shuffle(full);

        ArrayList<Substitution> sortedSubs = new ArrayList<Substitution>();
        while(!full.isEmpty()){
            Substitution sub = full.get(0);
            sortedSubs.add(sub);
            full.removeAll(Collections.singleton(sub));
        }
        
        // go throught each substitution. If it can be applied at the starting 
        // slot, use that substitution, if not move onto the next.
        
        MelodyPart substituted = null;
        int initSlot = startingSlot[0];
        for(Substitution sub: sortedSubs)
        {
            if(debug)
            {
                System.out.println("\t\tTrying sub: " + sub.getName());
            }
            //SORT THE TRANSFORMATIONS, PUT THEM IN THE SUB'S TRANSFORM
            //sub.categorizeTransformations();
            substituted = sub.apply(transNotes, chords, startingSlot, enforceDuration);
            if(substituted != null)
            {
                break;
            }
            if(debug)
            {
                System.out.println("");
            }
        }
        // if no substitution worked, just keep the note at the starting index 
        // as is and try to apply to the next note. 
        if(substituted == null)
        {
            MelodyPart temp = new MelodyPart();
            Note addNote = transNotes.getNote(initSlot);
            if( addNote != null )
              {
              temp.addNote(addNote);
              }
            transNotes.pasteOver(temp, initSlot);
            startingSlot[0] = transNotes.getNextIndex(initSlot);
        }
        else
        {
            transNotes.pasteOver(substituted, startingSlot[0]);
            startingSlot[0] = startingSlot[1];
        }
    }
    return transNotes;
}

private boolean applicable(Substitution sub, MelodyPart notes, ChordPart chords, int startSlot){
    //if it's a sub named after the rel pitch condition, check sub name first
    String subName = sub.getName();
    
    String relPrefix = "first-rel-pitch-";
    if(subName.contains(relPrefix)){
        String relPitch = subName.substring(relPrefix.length());
        NoteChordPair ncp = new NoteChordPair(notes.getCurrentNote(startSlot), chords.getCurrentChord(startSlot));
        if(!ncp.getRelativePitch().equals(relPitch)){
            return false;
        }
    }
    return true;
}


/**
 * Find substitutions that have the same type and transformations, and delete
 * all but one, adding all of their weights together. Basically just combines
 * all substitutions that do the same thing. 
 */
public void findDuplicatesAndAddToWeight()
{
    for(int i = 0; i < substitutions.size(); i++)
    {
        Substitution sub = substitutions.get(i);
        substitutions.remove(sub);
        int newWeight = sub.getWeight();
        while(substitutions.contains(sub))
        {
            int subIndex = substitutions.indexOf(sub);
            Substitution copy = substitutions.remove(subIndex);
            newWeight += copy.getWeight();
            hasChanged = true;
        }
        sub.setWeight(newWeight);
        substitutions.add(i, sub);
    }
}

public void cleanSubs(){
    for(Substitution s : substitutions){
        s.clean();
    }
}

/**
 * get the total of adding all the weights of the substitutions of type motif
 * @return 
 */
public int getTotalMotifWeight()
{
    int totalWeight = 0;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("motif"))
            totalWeight += sub.getWeight();
    }
    return totalWeight;
}
/**
 * get the total of adding all the weights of the substitutions of type
 * embellishment
 * @return 
 */
public int getTotalEmbWeight()
{
    int totalWeight = 0;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("embellishment"))
            totalWeight += sub.getWeight();
    }
    return totalWeight;
}

/**
 * scale all of the weights of substitutions in this transform that are of type
 * motif
 * @param scale     double to multiply all motif weights by
 */
public void scaleMotifWeights(double scale)
{
    if(scale != 1.0)
        hasChanged = true;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("motif"))
        {
            double newWeight = sub.getWeight()*scale;
            sub.setWeight((int)newWeight);
        }
    }
}

/**
 * scale all of the weights of substitutions in this transform that are of type
 * embellishment
 * @param scale     double to multiply all motif weights by
 */
public void scaleEmbWeights(double scale)
{
    if(scale != 1.0)
        hasChanged = true;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("embellishment"))
        {
            double newWeight = sub.getWeight()*scale;
            sub.setWeight((int)newWeight);
        }
    }
}

/**
 * Turns a melody into Polylist of notes in the melody
 * @param melody
 * @return 
 */
public Polylist melodyPartToNoteList(MelodyPart melody)
{
    int slotvalue = 0;
    MelodyPart.PartIterator it = melody.iterator();
    Polylist nList = new Polylist();
    while(it.hasNext())
    {
        Object next = (Note) it.next();
        nList.addToEnd(next);
    }
    return nList;
}

/**
 *
 * @return the original melody that was transformed
 */
public MelodyPart getOriginalMelodyPart()
{
    return startingNotes;
}

/**
 * Prints a reader friendly string version of the transform to a string builder
 * @param buf   StringBuilder to write to.
 */
public void toFile(StringBuilder buf)
{
    for(Substitution sub: substitutions)
    {
        buf.append("\n");
        sub.toFile(buf);
    }
}

/**
 * add a blank substitution to the current transform
 */
public Substitution addNewSubstitution()
{
    Substitution newSub = new Substitution();
    addSubstitution(newSub);
    hasChanged = true;
    return newSub;
}

/**
 * detect if the current transform has changed since last saved.
 * @return 
 */
public boolean hasChanged()
{
    if(hasChanged)
        return true;
    else
    {
        for(Substitution sub: substitutions)
        {
            if(sub.hasChanged())
                return true;
        }
    }
    return false;
}
}
