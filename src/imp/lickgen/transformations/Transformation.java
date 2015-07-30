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
import imp.data.Unit;
import imp.gui.TransformPanel;
import imp.util.ErrorLog;
import polya.*;
import java.util.*;
/**
 * Transformation that is part of a Substitution
 * 
 * @author Alex Putman
 */
public class Transformation {
    
private Polylist sourceNotes;
private Polylist conditionGuard;
private Polylist targetNotes;
private int weight;
private boolean enabled;
private String description;
private boolean hasChanged;
public boolean debug;

/**
 * Creates a default transformation that will always be able to transform a note
 * with weight = 1
 */
public Transformation()
{
    debug = false;
    description = (String) "new-transformation"; 
    sourceNotes = Polylist.PolylistFromString("n1");
    targetNotes = Polylist.PolylistFromString("n1");
    conditionGuard = Polylist.PolylistFromString("duration>= n1 128");
    weight = 1;
    hasChanged = false;
    enabled = true;
}

/**
 * Creates a transformation from a string containing a transformation grammar
 * @param transString 
 */
public Transformation(String transString)
{
    this();
    setTransformation((Polylist)Polylist.PolylistFromString(transString).first());
    hasChanged = false;
}

/**
 * Returns true if the transformations are the same, false otherwise
 * (Same defined to mean same description, source notes, target notes, condition guard, AND enabled - good idea???)
 * (IMPORTANT: Does not take debug or weight into account)
 * (This method is used when combining two transformations and adding their weights)
 * @param other the transformation to compare this to
 * @return true/false
 */
public boolean sameAs(Transformation other){
    return description.equals(other.getDescription()) 
            && sourceNotes.toString().equals(other.sourceNotes.toString()) 
            && targetNotes.toString().equals(other.targetNotes.toString())
            && conditionGuard.toString().equals(other.conditionGuard.toString())
            && enabled == other.enabled;
}

/**
 * Creates a transformation from a polylist containing a transformation grammar
 * @param trans 
 */
public Transformation(Polylist trans)
{
    this();
    setTransformation(trans);
    hasChanged = false;
}

/**
 * returns a copy of the current transformation
 * @return 
 */
public Transformation copy()
{
    StringBuilder copyString = new StringBuilder();
    toFile(copyString, "");
    Transformation copy = new Transformation(copyString.toString());
    return copy;
}

/**
 * Set the grammar of the current transformation to a new grammar
 * @param trans         Polylist containing the new grammar to set
 * @return              whether or not the new grammar was able to be set
 */
public boolean setTransformation(Polylist trans)
{   
    StringBuilder copyString = new StringBuilder();
    hasChanged = true;
    toFile(copyString, "");
    String transCopy = copyString.toString();
    try{
        description = (String) trans.assoc("description").second(); 
        sourceNotes = (Polylist) trans.assoc("source-notes").rest();
        targetNotes = (Polylist) trans.assoc("target-notes").rest();
        conditionGuard = (Polylist) trans.assoc("guard-condition").second();
        weight = ((Long) trans.assoc("weight").second()).intValue();
        if(trans.assoc("enabled") != null)
        {
            enabled = Boolean.parseBoolean(trans.assoc("enabled").second().toString());
        }
        else
        {
            enabled = true;
        }
        return true;
    }
    catch( Exception e )
    {
        setTransformation((Polylist)Polylist.PolylistFromString(transCopy).first());
        ErrorLog.log(ErrorLog.SEVERE, "Syntax Error in Transformation File");
        return false;
    }
}

/**
 * Try to apply this transformation to a set of notes
 * @param notes             MelodyPart to apply to
 * @param chords            ChordPart of notes
 * @param startingSlot      slot to start applying at
 * @return                  transformed melody
 */
public MelodyPart apply(MelodyPart notes, 
                        ChordPart chords, 
                        int startingSlot,
                        boolean enforceDuration)
{
    Evaluate eval = new Evaluate(new Polylist());

    PolylistEnum transSourceNotes = new PolylistEnum(sourceNotes);
    
    if(!changesFirstNote() && notes.getPrevIndex(startingSlot) > -1)
        startingSlot = notes.getPrevIndex(startingSlot);
    
    int newStartingSlot = startingSlot;
    int totalDurBefore = 0;
    // First get the notes that would be transformed in this transformation
    // and save the total duration of the notes before the application
    while(transSourceNotes.hasMoreElements() && newStartingSlot < notes.getSize())
    {
        
        String varName = (String) transSourceNotes.nextElement();
        
        Note varNote = notes.getCurrentNote(newStartingSlot);
        
        totalDurBefore += varNote.getRhythmValue();
        
        Chord varChord = chords.getCurrentChord(newStartingSlot);
        
        // Sometimes, the chordpart length is shorter than the melodypart,
        // so the notes under the last chord can't just use getCurrentChord
        if(varChord == null)
        {
            int lastChordIndex = newStartingSlot;
            while(varChord == null)
            {
                lastChordIndex = notes.getPrevIndex(lastChordIndex);
                varChord = chords.getCurrentChord(lastChordIndex);
            }
        }
        
        eval.setNoteVar(varName, varNote, varChord);
        
        newStartingSlot = notes.getNextIndex(newStartingSlot);
    }
    if(debug)
    {
        System.out.println("\t\t\tTrying Trans On: " + 
                           notes.extract(startingSlot, newStartingSlot - 1).toString());
    }
    if (transSourceNotes.hasMoreElements())
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Too Few Notes For Trans");
        }
        return null;
    }
    // Check that the melody satisfies the condition guard
    Boolean condition = (Boolean) eval.evaluate(conditionGuard);
    // if not, just return null, since the transformation could not be applied
    if(condition == null)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Condition Error");
        }
        return null;
    }
    if(!condition)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Condition False");
        }
        return null;
    }
    
    // if the condition guard is satisfied, try to evaluate the target notes
    Evaluate targetEval = new Evaluate(eval.getFrame(),"get-note");
    Polylist result = targetNotes.map(targetEval).flatten();//this line is causing problems
    MelodyPart resultingMP = new MelodyPart();
    PolylistEnum resultEnum = result.elements();

    // get the total duration of the transformed notes
    int totalDurFinal = 0;
    while(resultEnum.hasMoreElements())
    {
        Note finalNote = (Note) resultEnum.nextElement();
        if(finalNote != null)
        {
            totalDurFinal += finalNote.getRhythmValue();
            resultingMP.addNote(finalNote);
        }
    }
    if(debug)
    {
        System.out.println("\t\t\t\tBefore time: " + 
                           totalDurBefore + 
                           "\t\t\t\tAfter time: " + 
                           totalDurFinal);
    }
    
    // if the before and after duration are different, return null since 
    // transforming this melody will not keep time the same.
    // else return the transformed melody. 
    if(totalDurBefore != totalDurFinal && enforceDuration)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Incorrect Time");
        }
        return null;
    }
    else
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Result: " + resultingMP.toString());
        }
        return resultingMP;
    }
}

/**
 * See if this transformation ever changes the first note it takes in
 * @return 
 */
public boolean changesFirstNote()
{
    if(numSourceNotes() > 1 && sourceNotes.first().equals(targetNotes.first()))
        return false;
    return true;
}
/**
 * See if the transformation ever changes the last note it takes in
 */
public boolean changesLastNote()
{
    if(numSourceNotes() > 1 && sourceNotes.last().equals(targetNotes.last()))
        return false;
    return true;
}

/**
 * 
 * @return the number of notes the transformation attempts to transform
 */
public int numSourceNotes()
{
    return sourceNotes.length();
}

public int getWeight()
{
    return weight;
}

public void setWeight(int weight)
{
    if(this.weight != weight)
        hasChanged = true;
    this.weight = weight;
}

public boolean getEnabled()
{
    return enabled;
}

public void setEnabled(boolean en)
{
    if(enabled != en)
        hasChanged = true;
    enabled = en;
}

public String getDescription()
{
    return description;
}

public void setDescription(String des)
{
    description = des;
}

/**
 * 
 * @return whether the transformation has been changed since it was last saved.
 */
public boolean hasChanged()
{
    return hasChanged;
}
/**
 * See if this transformation has the same source-notes, guard-condition and
 * target notes as another transformation
 * @param ob        Other transformation
 * @return 
 */
public boolean equals(Object ob)
{
    if(!(ob instanceof Transformation))
        return false;
    Transformation other = (Transformation) ob;
    if(!sourceNotes.equals(other.sourceNotes))
        return false;
    if(!conditionGuard.equals(other.conditionGuard))
        return false;
    if(!targetNotes.equals(other.targetNotes))
        return false;
    return true;
}

/**
 * String version of a transformation that could be used for debugging
 * @return 
 */
public String toString()
{
    StringBuilder buf = new StringBuilder();
    buf.append(description);
    buf.append("       weight = ");
    buf.append(weight);
    
    return buf.toString();
}

/**
 * Writes a reader friendly version of this transform to a StringBuilder
 * @param buf       StringBuilder to write to
 * @param tabs      The String of tabs to add to the beginning of every line
 */
public void toFile(StringBuilder buf, String tabs)
{
    if(tabs.length()>0)
        buf.append("\n");
    buf.append(tabs).append("(transformation");
    buf.append("\n").append(tabs).append("\t(description ").append(description).append(")");
    buf.append("\n").append(tabs).append("\t(weight ").append(weight).append(")");
    buf.append("\n").append(tabs).append("\t(enabled ").append(enabled).append(")");
    buf.append("\n").append(tabs).append("\t");
    Polylist printSourceNotes = new Polylist("source-notes",sourceNotes);
    printPrettyPolylist("", tabs+"\t", buf, printSourceNotes);
    buf.append("\n").append(tabs).append("\t");
    Polylist printGuardCondition = Polylist.list("guard-condition",conditionGuard);
    printPrettyPolylist("", tabs+"\t", buf, printGuardCondition);
    buf.append("\n").append(tabs).append("\t");
    Polylist printTargetNotes = new Polylist("target-notes",targetNotes);
    printPrettyPolylist("", tabs+"\t", buf, printTargetNotes);
    buf.append(")");
}

/**
 * Helper function for toFile that writes polylists pretty and easy to read in
 * transformation form. 
 * @param leftSide
 * @param tabs
 * @param buf
 * @param list 
 */
public void printPrettyPolylist(String leftSide, 
                                String tabs, 
                                StringBuilder buf, 
                                Polylist list)
{
    Object first = list.first();
    String newLeftSide = "(" + first.toString();
    
    buf.append(newLeftSide);
    newLeftSide += "_";
    boolean separate = false;
    list = list.rest();
    int size = list.length();
    
    PolylistEnum elements = list.elements();
    while(elements.hasMoreElements())
    {
        if(elements.nextElement() instanceof Polylist && size > 1)
                separate = true;
    }
    if(separate)
    {   
        while((newLeftSide.length() + (leftSide.length() % 8)) % 8 != 0)
            newLeftSide = newLeftSide + "_";
    }
    else
        newLeftSide = newLeftSide + "_";
    int numTabs = (leftSide + newLeftSide).length()/8;
    String addTabs = "";
    while(numTabs > 0)
    {
        addTabs = addTabs + "\t";
        numTabs--;
    }
    elements = list.elements();
    
    if(separate)
        buf.append("\t");
    else
        buf.append(" ");
    
    while(elements.hasMoreElements())
    {
        
        if(!separate)
            buf.append(" ");
        Object elem = elements.nextElement();
        if(elem instanceof Polylist)
        {
            printPrettyPolylist(leftSide + newLeftSide, tabs,buf,(Polylist) elem);
        }
        else
        {
            buf.append(elem.toString());
        }
        if(elements.hasMoreElements())
        {
            if(separate)
                buf.append("\n").append(tabs).append(addTabs);
        }
    }
    buf.append(")");
}
}
