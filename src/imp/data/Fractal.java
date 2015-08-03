/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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
package imp.data;

import imp.Constants;
import imp.ImproVisor;
import imp.gui.GuideToneLineDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import polya.Polylist;

/**
 * This class holds the data used to fractally improvise a solo based on a
 * guide tone line.
 * Used by GuideLineGenerator
 * @author Carli Lessard
 */
public class Fractal implements Constants{
    
    private double tripletProb;
    private double quintupletProb;
    
    // Dividing Probabilities
    private double wholeDivProb;
    private double halfDivProb;
    private double quarterDivProb;
    private double eighthDivProb;
    private double sixteenthDivProb;
    private double defaultDivProb;
    
    // Rest probabilities
    private double wholeRestProb;
    private double halfRestProb;
    private double quarterRestProb;
    private double eighthRestProb;
    private double sixteenthRestProb;
    private double defaultRestProb;
    
    // Keywords used in the file parser to set probabilities
    private static String keyword[] = {"dividing-iterations",
                                       "dividing-probabilities",
                                       "rest-probabilities",
                                       "triplet-probability",
                                       "quintuplet-probability"};
    private static final int ITERATIONS = 0;
    private static final int DIVIDING_PROB = 1;
    private static final int REST_PROB = 2;
    private static final int TRIPLET_PROB = 3;
    private static final int QUINTUPLET_PROB = 4;
    
    private static String noteWords[] = {"whole", "half", "quarter", 
                                         "eighth", "sixteenth", "default"};
    private static final int WHOLE_NOTE = 0;
    private static final int HALF_NOTE = 1;
    private static final int QUARTER_NOTE = 2;
    private static final int EIGHTH_NOTE = 3;
    private static final int SIXTEENTH_NOTE = 4;
    private static final int DEFAULT_NOTE = 5;
    
    //Constants used for fractally dividing a line
    private static final int TUPLE = 2;
    private static final int TRIPLET = 3;
    private static final int QUINTUPLET = 5;
    
    public Fractal()
    {
        this(ImproVisor.getFractalFile());
    }
    
    public Fractal(String probs)
    {
        setProbabilities(probs);
        //debug();
    }
    
    public Fractal(File file)
    {
        this(fileToString(file));
    }
    
    private static String fileToString(File file){
    String probStr = "";
    try {
        probStr = new Scanner(file).useDelimiter("\\Z").next();
    } catch (FileNotFoundException ex) {
        Logger.getLogger(GuideToneLineDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    return probStr;
    }
    
    private void debug()
    {
        System.out.println("Triplet prob: " + tripletProb);
        System.out.println("Quintuplet prob: " + quintupletProb);
        System.out.println("Whole div prob: " + wholeDivProb);
        System.out.println("Half div prob: " + halfDivProb);
        System.out.println("Quarter div prob: " + quarterDivProb);
        System.out.println("Eighth div prob: " + eighthDivProb);
        System.out.println("Sixteenth div prob: " + sixteenthDivProb);
        System.out.println("Default div prob: " + defaultDivProb);
        System.out.println("Whole rest prob: " + wholeRestProb);
        System.out.println("Half rest prob: " + halfRestProb);
        System.out.println("Quarter rest prob: " + quarterRestProb);
        System.out.println("Eighth rest prob: " + eighthRestProb);
        System.out.println("Sixteenth rest prob: " + sixteenthRestProb);
        System.out.println("Default rest prob: " + defaultRestProb);
    }
    
    public void setProbabilities(String probs)
    {
        Polylist polyProbs = Polylist.PolylistFromString(probs);
        
        while(polyProbs.nonEmpty()){
            Polylist dispatcher = (Polylist)polyProbs.first();

            String category = (String)dispatcher.first();
            switch(Leadsheet.lookup(category, keyword)){
                case DIVIDING_PROB:
                    setDividingProbs((Polylist)dispatcher.second());
                    break;
                case REST_PROB:
                    setRestProbs((Polylist)dispatcher.second());
                    break;
                case TRIPLET_PROB:
                    Double tripDouble = ((Number)dispatcher.second()).doubleValue();
                    tripletProb = tripDouble;
                    break;
                case QUINTUPLET_PROB:
                    Double quintDoub = ((Number)dispatcher.second()).doubleValue();
                    quintupletProb = quintDoub;
                    break;
                default:
                    break;
            }
            
            polyProbs = polyProbs.rest();
        }
    }
    
    private void setDividingProbs(Polylist divProbs)
    {

        while(divProbs.nonEmpty()){
            Polylist noteValList = (Polylist)divProbs.first();
            String noteVal = (String)noteValList.first();
            
            switch(Leadsheet.lookup(noteVal, noteWords)){
                case WHOLE_NOTE:
                    Double prob = ((Number)noteValList.second()).doubleValue();
                    wholeDivProb = prob;
                    break;
                case HALF_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    halfDivProb = prob;
                    break;
                case QUARTER_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    quarterDivProb = prob;
                    break;
                case EIGHTH_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    eighthDivProb = prob;
                    break;
                case SIXTEENTH_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    sixteenthDivProb = prob;
                    break;
                case DEFAULT_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    defaultDivProb = prob;
                    break;
                default:
                    break;
            }
        divProbs = divProbs.rest();
        }       
    }
    
    private void setRestProbs(Polylist restProbs)
    {
        while(restProbs.nonEmpty()){
            Polylist noteValList = (Polylist)restProbs.first();
            String noteVal = (String)noteValList.first();
            
            switch(Leadsheet.lookup(noteVal, noteWords)){
                case WHOLE_NOTE:
                    Double prob = ((Number)noteValList.second()).doubleValue();
                    wholeRestProb = prob;
                    break;
                case HALF_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    halfRestProb = prob;
                    break;
                case QUARTER_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    quarterRestProb = prob;
                    break;
                case EIGHTH_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    eighthRestProb = prob;
                    break;
                case SIXTEENTH_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    sixteenthRestProb = prob;
                    break;
                case DEFAULT_NOTE:
                    prob = ((Number)noteValList.second()).doubleValue();
                    defaultRestProb = prob;
                    break;
                default:
                    break;
            }
            restProbs = restProbs.rest();
        }
    }
    
    public String probsToString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(dividing-probabilities (");
        buffer.append(divProbstoString());
        buffer.append(")) \n");
        buffer.append("(rest-probabilities (");
        buffer.append(restProbstoString());
        buffer.append(")) \n");
        buffer.append("(triplet-probability ");
        buffer.append(tripletProb);
        buffer.append(") \n");
        buffer.append("(quintuplet-probability ");
        buffer.append(quintupletProb);
        buffer.append(")");
        
        return buffer.toString();
    }
    
    private String divProbstoString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(whole ");
        buffer.append(wholeDivProb);
        buffer.append(") \n");
        buffer.append("\t (half ");
        buffer.append(halfDivProb);
        buffer.append(") \n");
        buffer.append("\t (quarter ");
        buffer.append(quarterDivProb);
        buffer.append(") \n");
        buffer.append("\t (eighth ");
        buffer.append(eighthDivProb);
        buffer.append(") \n");
        buffer.append("\t (sixteenth ");
        buffer.append(sixteenthDivProb);
        buffer.append(") \n");
        buffer.append("\t (default ");
        buffer.append(defaultDivProb);
        buffer.append(")");
        
        return buffer.toString();
    }
    
    private String restProbstoString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(whole ");
        buffer.append(wholeRestProb);
        buffer.append(") \n");
        buffer.append("\t (half ");
        buffer.append(halfRestProb);
        buffer.append(") \n");
        buffer.append("\t (quarter ");
        buffer.append(quarterRestProb);
        buffer.append(") \n");
        buffer.append("\t (eighth ");
        buffer.append(eighthRestProb);
        buffer.append(") \n");
        buffer.append("\t (sixteenth ");
        buffer.append(sixteenthRestProb);
        buffer.append(") \n");
        buffer.append("\t (default ");
        buffer.append(defaultRestProb);
        buffer.append(")");
        
        return buffer.toString();
    }
    
    /**
     * dist
     * returns the absolute distance between two notes' midi values
     * @param n1 note 1
     * @param n2 note 2
     * @return distance between n1 and n2's midi values
     */
    private int dist(Note n1, Note n2){
        return Math.abs(n1.getPitch() - n2.getPitch());
    }
    
    /**
     * Returns a double that can be used to determine probability of events
     * happening
     * @param rhythmValue
     * @return 
     */
    private double getProbability(int rhythmValue)
    {
        // These are randomly chosen probabilities, there could be better options
        
        // The double is used as a threshold for splitting a note. If a random 
        // double is less than the returned value, a note will not be divided.
        switch(rhythmValue){
            case WHOLE:
                return wholeDivProb;
            case HALF:
                return halfDivProb;
            case QUARTER:
                return quarterDivProb;
            case EIGHTH:
                return eighthDivProb;
            case SIXTEENTH:
                return sixteenthDivProb;
            default:
                return defaultDivProb;
        }
    }
    
    /**
     * Returns a double that can be used to determine probability of 
     * transforming a note into a rest
     * @param rhythmValue
     * @return 
     */
    private double getRestProbability(int rhythmValue)
    {
        // Again, these are randomly chosen probabilites and there are 
        // probably better options
        
        // These values are used to determine whether a note will turn into 
        // a rest or not. If a random double is less than the return value, a 
        // note will turn into a rest.
        switch(rhythmValue){
            case WHOLE:
                return wholeRestProb;
            case HALF:
                return halfRestProb;
            case QUARTER:
                return quarterRestProb;
            case EIGHTH:
                return eighthRestProb;
            case SIXTEENTH:
                return sixteenthRestProb;
            default:
                return defaultRestProb;
        }
    }
    
    /** 
     * Divides a guide tone line to create a fractal melody
     * @param gtl
     * @return MelodyPart that is the new melody
     */
    public MelodyPart fractalImprovise(MelodyPart gtl, int numTimes)
    {   
        for(int i = 0; i < numTimes; ++i){
            gtl = splitSolo(gtl);
        }
        return gtl;
    }
    
    /**
     * Used by fractalImprovise to get a note to be divided, and adds the new
     * notes to the melody part
     * @param solo
     * @param chords
     * @param rhythmValue
     * @return MelodyPart with all the added notes
     */
    private MelodyPart splitSolo(MelodyPart solo)
    {
        MelodyPart newSolo = new MelodyPart();
        Note prevNote = Note.makeRest(WHOLE);
        
        // iterate through the existing melody part and add notes that are
        // either the same or divided to the new melody part
        int slot = 0;
        while(slot < solo.getSize()){
            Note first = solo.getCurrentNote(slot);
            Note second = solo.getNextNote(slot);
            int nextIndex = solo.getNextIndex(slot);
            int rhythmValue = first.getRhythmValue();
            
            if(second == null)
                second = first;
            
            ArrayList<Note> newNotes = splitNotes(first,
                                                  second,
                                                  prevNote,
                                                  rhythmValue);
            for(Note note : newNotes){
                newSolo.addNote(note);
            }
            prevNote = first;
            slot = nextIndex;
        }
        return newSolo;
    }
    
    /**
     * Probabilistically divides a note 
     * @param firstNote
     * @param secondNote
     * @param rhythmValue
     * @return The notes to replace the first note passed in
     */
    private ArrayList<Note> splitNotes(Note firstNote, 
                                       Note secondNote,
                                       Note prevNote,
                                       int rhythmValue)
    {
        ArrayList<Note> newNotes = new ArrayList<Note>();
        
        if(firstNote.isRest()) {        
            newNotes.add(firstNote);
        }  else if(firstNote.getRhythmValue() <= THIRTYSECOND) {            
            newNotes.add(firstNote);
        } else {
            newNotes = determineNewNotes(firstNote,
                                         secondNote,
                                         prevNote,
                                         rhythmValue);
        }
        return newNotes;
    }
    
    /**
     * Used by split notes to determine how and when it should split a note
     * @param firstNote
     * @param secondNote
     * @param prevNote
     * @param rhythmValue
     * @return 
     */
    private ArrayList<Note> determineNewNotes(Note firstNote, 
                                          Note secondNote,
                                          Note prevNote,
                                          int rhythmValue)
    {
        ArrayList<Note> newNotes = new ArrayList<Note>();
        Random rand = new Random();
        int subdivs = TUPLE;

        double randDouble = rand.nextDouble();
        double divDouble = rand.nextDouble();
        double probability = getProbability(rhythmValue);
        
        if(isTriplet(firstNote)) {
            if(randDouble > probability){
                newNotes.add(noSplit(firstNote, prevNote, rhythmValue));
            } else {
                newNotes = getDividedNotes(firstNote,
                                           secondNote,
                                           subdivs);
            }
        } else if(isDotted(firstNote)) {
            subdivs = TRIPLET;
            if(randDouble > probability){
                newNotes.add(noSplit(firstNote, prevNote, rhythmValue));
            } else {
                newNotes = getDividedNotes(firstNote,
                                           secondNote,
                                           subdivs);
            }
        } else {
            if(divDouble < tripletProb){
                subdivs = TRIPLET;
            } 

            if(randDouble > probability){                
                newNotes.add(noSplit(firstNote, prevNote, rhythmValue));
            } else {
                newNotes = getDividedNotes(firstNote,
                                           secondNote,
                                           subdivs);
            }
        }
        return newNotes;
    }
    
    /**
     * Used by splitNotes, if a note is not going to be split, probabilistically
     * determines whether it will remain as a note or become a rest of the same
     * duration
     * @param note
     * @return Note/rest to be added to the melody
     */
    private Note noSplit(Note note, Note prevNote, int rhythmValue)
    {
        Random rand = new Random();
        double randDouble = rand.nextDouble();
            
        double probability = getRestProbability(rhythmValue);
        if(prevNote.isRest())
            probability = getRestProbability(WHOLE);
        
        if(randDouble < probability){
            return Note.makeRest(note.getRhythmValue());
        }
        else
            return note;
    }
        
    /**
     * Used by splitNotes, if a note is going to be split, it divides it into
     * either two or three new notes
     * @param firstNote
     * @param secondNote
     * @param subdivs
     * @param rhythmValue
     * @return ArrayList of notes to be added to the melody
     */
    private ArrayList<Note> getDividedNotes(Note firstNote, 
                                            Note secondNote,
                                            int subdivs)
    {
        int rhythmValue = firstNote.getRhythmValue();
        
        if(dist(firstNote, secondNote) < 3)
            return getCloseNotes(firstNote, secondNote);
        else if(subdivs == 2)
            return splitNoteInTwo(firstNote, secondNote, rhythmValue);
        else
            return splitNoteInThree(firstNote, secondNote, rhythmValue);
    }
    
    /**
     * Used by splitNtoes to split notes that are close together
     * @param firstNote
     * @param secondNote
     * @return 
     */
    private ArrayList<Note> getCloseNotes(Note firstNote,
                                          Note secondNote)
    {
        ArrayList<Note> newNotes = new ArrayList<Note>();
        int firstPitch = firstNote.getPitch();
        int secondPitch = secondNote.getPitch();
        int rhythmValue = firstNote.getRhythmValue();

        rhythmValue = rhythmValue / 2;
        int diff = firstPitch - secondPitch;
        
        if(firstPitch == secondPitch){
            Random rand = new Random();
            Boolean ranBool = rand.nextBoolean();
            
            if(ranBool)
                diff = 2; //move the note a whole step up
            else
                diff = -2; //move the note a whole step down
        }
        Note newFirstNote = new Note(firstPitch, rhythmValue);
        Note newSecondNote = new Note(firstPitch + diff, rhythmValue);
        newNotes.add(newFirstNote);
        newNotes.add(newSecondNote);

        
        return newNotes;
    }
    
    /**
     * Used by getDividedNotes to split one note into two new notes half the 
     * length of the original
     * @param firstNote
     * @param secondNote
     * @param rhythmValue
     * @return ArrayList of notes to be added
     */
    private ArrayList<Note> splitNoteInTwo(Note firstNote, Note secondNote, int rhythmValue)
    {
        ArrayList<Note> notes = new ArrayList<Note>();
        int firstPitch = firstNote.getPitch();
        int secondPitch = secondNote.getPitch();
        if(secondPitch == REST)
            secondPitch = (firstPitch + 2);
        
        int avgPitch = (firstPitch + secondPitch) / 2;
        
        notes.add(new Note(firstPitch, rhythmValue/2));
        notes.add(new Note(avgPitch, rhythmValue/2));
        
        return notes;
    }
    
    /**
     * Used by getDividedNotes to split one note into three new notes, each a 
     * third the length of the original
     * @param firstNote
     * @param secondNote
     * @param rhythmValue
     * @return ArrayList of the new notes to be added
     */
    private ArrayList<Note> splitNoteInThree(Note firstNote, Note secondNote, int rhythmValue)
    {
        ArrayList<Note> notes = new ArrayList<Note>();
        int firstPitch = firstNote.getPitch();
        int secondPitch = secondNote.getPitch();
        if(secondPitch == REST)
            secondPitch = firstPitch + 3;
        
        int thirdPitch = firstPitch + ((secondPitch-firstPitch) / 3);
        int fourthPitch = firstPitch + (((secondPitch-firstPitch) * 2) / 3);
        
        notes.add(new Note(firstPitch, rhythmValue/3));
        notes.add(new Note(thirdPitch, rhythmValue/3));
        notes.add(new Note(fourthPitch, rhythmValue/3));
        
        return notes;
    }
    
    /**
     * Returns a boolean of whether or not a note has a length equal to a
     * triplet
     * @param note
     * @return 
     */
    private boolean isTriplet(Note note)
    {
        int noteLength = note.getRhythmValue();
        
        return (noteLength == HALF_TRIPLET ||
                noteLength == QUARTER_TRIPLET ||
                noteLength == EIGHTH_TRIPLET ||
                noteLength == SIXTEENTH_TRIPLET ||
                noteLength == THIRTYSECOND_TRIPLET);
    }
    
    /**
     * Returns a boolean of whether or not a note has a note that is a 
     * dotted length
     * @param note
     * @return 
     */
    private boolean isDotted(Note note)
    {
        int noteLength = note.getRhythmValue();
        
        return (noteLength == DOTTED_HALF ||
                noteLength == DOTTED_QUARTER ||
                noteLength == DOTTED_EIGHTH ||
                noteLength == DOTTED_SIXTEENTH);
    }
    
    public void setTripletProb(double prob)
    {
        tripletProb = prob;
    }
    
    public double getTripletProb()
    {
        return tripletProb;
    }
    
    public void setQuintupletProb(double prob)
    {
        quintupletProb = prob;
    }
    
    public double getQuintupletProb()
    {
        return quintupletProb;
    }
    
    public void setWholeDivProb(double prob)
    {
        wholeDivProb = prob;
    }
    
    public double getWholeDivProb()
    {
        return wholeDivProb;
    }
    
    public void setHalfDivProb(double prob)
    {
        halfDivProb = prob;
    }
    
    public double getHalfDivProb()
    {
        return halfDivProb;
    }
    
    public void setQuarterDivProb(double prob)
    {
        quarterDivProb = prob;
    }
    
    public double getQuarterDivProb()
    {
        return quarterDivProb;
    }
    
    public void setEighthDivProb(double prob)
    {
        eighthDivProb = prob;
    }
    
    public double getEighthDivProb()
    {
        return eighthDivProb;
    }
    
    public void setSixteenthDivProb(double prob)
    {
        sixteenthDivProb = prob;
    }
    
    public double getSixteenthDivProb()
    {
        return sixteenthDivProb;
    }
    
    public void setDefaultDivProb(double prob)
    {
        defaultDivProb = prob;
    }
    
    public double getDefaultDivProb()
    {
        return defaultDivProb;
    }
    
    public void setWholeRestProb(double prob)
    {
        wholeRestProb = prob;
    }
    
    public double getWholeRestProb()
    {
        return wholeRestProb;
    }
    
    public void setHalfRestProb(double prob)
    {
        halfRestProb = prob;
    }
    
    public double getHalfRestProb()
    {
        return halfRestProb;
    }
    
    public void setQuarterRestProb(double prob)
    {
        quarterRestProb = prob;
    }
    
    public double getQuarterRestProb()
    {
        return quarterRestProb;
    }
    
    public void setEighthRestProb(double prob)
    {
        eighthRestProb = prob;
    }
    
    public double getEighthRestProb()
    {
        return eighthRestProb;
    }
    
    public void setSixteenthRestProb(double prob)
    {
        sixteenthRestProb = prob;
    }
    
    public double getSixteenthRestProb()
    {
        return sixteenthRestProb;
    }
    
    public void setDefaultRestProb(double prob)
    {
        defaultRestProb = prob;
    }
    
    public double getDefaultRestProb()
    {
        return defaultRestProb;
    }
}
