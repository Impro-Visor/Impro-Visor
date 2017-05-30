/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;
import imp.Constants;
import static imp.Constants.OCTAVE;
import imp.com.RectifyPitchesCommand;
import imp.gui.Notate;
import imp.lickgen.Grammar;
import java.util.ArrayList;
import java.util.Random;
import polya.Polylist;
import polya.PolylistEnum;

/**
 *
 * @author Mikayla Konst 2015
 * A MelodyGenerator generates a melody based on transition probabilities
 * between intervals. It is constructed using an IntervalLearner.
 * 
 * Important Note about Pre-Rectification:
 * This class used to be capable of Pre-Rectification.
 * Pre-Rectification means only choosing among notes that are chord or color tones
 * each time that you choose a note to travel to.
 * This differs from Post-Rectification, in which you choose whatever notes you want
 * and then rectify them to chord/color/approach tones.
 * To reinstate Pre-Rectification, alter the bestChoice functions to only choose from
 * chord and color tones in exactly the same way that they only choose from notes that are in range,
 * i.e. by only adding in-range, chord/color tones with non-zero probabilities to the list and then adjusting the probabilities.
 * If no notes meet this criteria, allow non-chord/color tones. If still no notes meet this criteria,
 * allow out-of-range notes.
 */
public class MelodyGenerator {

    //Instance Variables
    
    //1st order probabilities
    private double [][] probabilities;
    //2nd order probabilities
    private double [][][] probabilities2;
    
    //The desired rhythm of the generated melody
    private MelodyPart rhythm;
    //The chords that the melody will be generated over
    private ChordPart chords;
    //The desired range of the generated melody
    private int [] range;
    //whether to rectify
    private boolean rectify;
    //types of notes to allow in rectification
    private boolean includeChord, includeColor, includeApproach;
    //whether to merge same notes
    private boolean merge;

    //Constants
    
    //used to denote no knowledge of which interval we're coming from
    private static final int NO_DATA = Integer.MAX_VALUE;
    //constant indicating in range
    private static final boolean IN_RANGE = true;
    
    //Constructors
    
    /**
     * MelodyGenerator
     * Constructs a MelodyGenerator given a MelodyPart containing the desired rhythm.
     * @param learner an IntervalLearner
     * @param rhythm a MelodyPart that represents the desired rhythm
     * @param chords the ChordPart that the melody will be generated over
     * @param range the desired range of the melody
     * @param merge whether to merge adjacent same notes
     * @param rectify whether to rectify
     * @param include array of three booleans representing whether to include chord, color, approach in rectification
     */
    public MelodyGenerator(IntervalLearner learner, MelodyPart rhythm, ChordPart chords, int [] range, boolean merge, boolean rectify, boolean [] include){
        probabilities = learner.getDeg1Probs();
        probabilities2 = learner.getDeg2Probs();
        this.rhythm = rhythm;
        this.chords = chords;
        this.range = range;
        this.merge = merge;
        this.rectify = rectify;
        this.includeChord = include[0]; this.includeColor = include[1]; this.includeApproach = include[2];
    }
    
    /**
     * MelodyGenerator
     * Constructs a MelodyGenerator using a Notate to generate a grammar-based rhythm
     * @param learner an IntervalLearner
     * @param notate a Notate whose selected grammar will be used to generate a rhythm
     * @param chords the ChordPart that the melody will be generated over
     * @param range the desired range of the melody
     * @param merge whether to merge adjacent same notes
     * @param rectify whether to rectify
     * @param include array of three booleans representing whether to include chord, color, approach in rectification
     */
    public MelodyGenerator(IntervalLearner learner, Notate notate, ChordPart chords, int [] range, boolean merge, boolean rectify, boolean [] include){
        this(learner, polylistToMelodyPart(rhythm(notate)), chords, range, merge, rectify, include);
    }
    
    //Rhythm Utility Methods
    
    /**
     * rhythm
     * Creates a rhythm Polylist from an instance of Notate
     * @param notate Notate whose selected Grammar will be used in rhythm generation
     * @return a Polylist representing a rhythm
     */
    public static Polylist rhythm(Notate notate){
        Grammar gram = new Grammar(notate.getGrammarFileName());
        return justRhythm(gram.run(0, notate.getScoreLength(), notate, false, false, -1));
    }

    /**
     * justRhythm
     * Extracts just a rhythm from an abstract Melody
     * This method should probably live somewhere else so that other people
     * can use it as well.
     * @param abstractMelody - an abstract melody that is to be reduced to just a rhythm.
     * @return just a rhythm (as a Polylist)
     */
    private static Polylist justRhythm(Polylist abstractMelody){
        PolylistEnum iterator = abstractMelody.elements();
        Polylist justRhythm = new Polylist();
        while(iterator.hasMoreElements()){
            Object nextElem = iterator.nextElement();
            try{
                Polylist note = (Polylist)nextElem;
                if(note.first().toString().equals("triadic")){
                    int totalDur = Duration.getDuration(note.second().toString());
                    int smallDur = Duration.getDuration(note.third().toString());
                    int numberOfNotes = totalDur/smallDur;
                    for(int i = 0; i<numberOfNotes; i++){
                        justRhythm = justRhythm.addToEnd("X"+note.third().toString());
                    }
                }else{
                    PolylistEnum i = note.elements();
                    while(i.hasMoreElements()){
                        try{
                            String elem = i.nextElement().toString();
                            if(elem.matches("[A-Z].*")){
                                if(elem.charAt(0)=='R'){
                                    justRhythm = justRhythm.addToEnd(elem);
                                }else{
                                    justRhythm = justRhythm.addToEnd("X"+elem.substring(1));
                                }
                            }
                        }catch(Exception e){

                        }
                    }
                }
                
            }catch(Exception ex){
                try{
                    String elem = nextElem.toString();
                    if(elem.matches("[A-Z].*")){
                        if(elem.charAt(0)=='R'){
                            justRhythm = justRhythm.addToEnd(elem);
                        }else{
                            justRhythm = justRhythm.addToEnd("X"+elem.substring(1));
                        }
                    }
                }catch(Exception exp){
                    
                }
            }
        }
        return justRhythm;
    }
    
    /**
     * polylistToMelodyPart
     * Converts a Polylist containing just a rhythm to a MelodyPart
     * containing rests and notes.
     * @param rhythm a Polylist containing a rhythm
     * @return a MelodyPart containing a C for every note in the rhythm
     * and a rest for every rest.
     */
    public static MelodyPart polylistToMelodyPart(Polylist rhythm){
        MelodyPart result = new MelodyPart();
        PolylistEnum iterator = rhythm.elements();
        while(iterator.hasMoreElements()){
            String s = (String)iterator.nextElement();
            char restOrNote = s.charAt(0);
            String RorX = Character.toString(restOrNote);
            
            int duration = Duration.getDuration(s.substring(1));
            
            Note n;
            if(RorX.equals("R")){
                n = new Rest(duration);
            }else{//N, C, L, A, X, S
                n = new Note(Constants.C4, duration);
            }
            result.addNote(n);
        }
        return result;
    }

    //Melody Generation Functions
    
    /**
     * melody
     * @return a MelodyPart based on a 1st order Markov Chain
     */
    public MelodyPart melody() {
        MelodyPart result = new MelodyPart();
        int prevInterval = NO_DATA;
        Note prevNote = null; //always a note, never a rest
        int slot = 0;
        Note n = rhythm.getNote(slot);
        int duration = n.getRhythmValue();
        Chord chord = chords.getCurrentChord(slot);

        while(slot < rhythm.size()){
            Note toAdd;
            
            if(n.isRest()){
                toAdd = n.copy();
            }
            //should only be true for first note
            else if(prevNote == null){
                toAdd = randomChordOrColorTone(chord, duration);
                prevInterval = NO_DATA;
                
            //should only be true for second note (we ignore rests)
            }else if(prevInterval == NO_DATA){
                toAdd = randomChordOrColorTone(chord, duration);
                prevInterval = toAdd.getPitch() - prevNote.getPitch();
            }
            else{
                toAdd = bestChoice(prevInterval, prevNote, duration);
                prevInterval = toAdd.getPitch() - prevNote.getPitch();
            }
            
            //if n is a rest, skip right over it like it was never there
            if(!n.isRest()){
               prevNote = toAdd; 
            }
            
            result.addNote(toAdd);
            
            slot += duration;
            if(slot < rhythm.size()){
                n = rhythm.getNote(slot);
                duration = n.getRhythmValue();
                chord = chords.getCurrentChord(slot);
            }

            
        }
        if(rectify){
            //post-rectification to chord, color, and approach tones
            RectifyPitchesCommand cmd = 
                    new RectifyPitchesCommand(result, 
                                              0,
                                              result.size()-1, 
                                              chords,
                                              false, 
                                              false,
                                              includeChord, 
                                              includeColor, 
                                              includeApproach,
                                              merge);
            cmd.execute();
        }
        //merge same notes - good idea???
        if(merge){
            result.removeRepeatedNotesInPlace();
        }
        return result;
    }
    
    
    /**
     * melody2
     * @return a MelodyPart based on a 2nd order Markov Chain
     */
    public MelodyPart melody2() {
        MelodyPart result = new MelodyPart();
        int prevInterval1 = NO_DATA;
        int prevInterval2 = NO_DATA;
        Note prevNote = null; //always a note, never a rest
        int slot = 0;
        Note n = rhythm.getNote(slot);
        int duration = n.getRhythmValue();
        Chord chord = chords.getCurrentChord(slot);

        while(slot < rhythm.size()){
            Note toAdd;
            
            if(n.isRest()){
                toAdd = n.copy();
            }
            //should only be true for first note
            else if(prevNote == null){
                toAdd = randomChordOrColorTone(chord, duration);
                
            //should only be true for second note (we ignore rests)
            }else if(prevInterval1 == NO_DATA){
                toAdd = randomChordOrColorTone(chord, duration);
                prevInterval1 = toAdd.getPitch() - prevNote.getPitch();
            //should only be true for third note
            }else if(prevInterval2 == NO_DATA){
                toAdd = randomChordOrColorTone(chord, duration);
                prevInterval2 = toAdd.getPitch() - prevNote.getPitch();
            }
            else{
                toAdd = bestChoice(prevInterval1, prevInterval2, prevNote, duration, chord);
                prevInterval1 = prevInterval2;
                prevInterval2 = toAdd.getPitch() - prevNote.getPitch();
            }
            
            //if n is a rest, skip right over it like it was never there
            if(!n.isRest()){
               prevNote = toAdd; 
            }
            
            result.addNote(toAdd);
            
            slot += duration;
            if(slot < rhythm.size()){
                n = rhythm.getNote(slot);
                duration = n.getRhythmValue();
                chord = chords.getCurrentChord(slot);
            }

            
        }
        if(rectify){
            //post-rectification to chord, color, and approach tones
            RectifyPitchesCommand cmd = 
                    new RectifyPitchesCommand(result, 
                                              0,
                                              result.size()-1, 
                                              chords,
                                              false, 
                                              false,
                                              includeChord, 
                                              includeColor, 
                                              includeApproach,
                                              merge);
            cmd.execute();
        }
        //merge same notes - good idea???
        if(merge){
            result.removeRepeatedNotesInPlace();
        }
        return result;
    }    
    
//    //Merge Same Notes
//    
//    /**
//     * mergeSameNotes
//     * Merges consecutive notes that have the same pitch.
//     * This should probably be a command so that more people can use it.
//     * @param unmerged a MelodyPart whose consecutive same notes are to be merged.
//     * @return a MelodyPart whose consecutive same notes have been merged.
//     */
//    private MelodyPart mergeSameNotes(MelodyPart unmerged){
//        MelodyPart merged = new MelodyPart();
//        int duration = unmerged.getNote(0).getRhythmValue();
//        Note toAdd = unmerged.getNote(0);
//        int lastIndex = unmerged.getLastActiveSlot();
//        for(int i = 0; i + duration <= lastIndex; i += duration){
//            
//            Note curr = unmerged.getNote(i);
//            duration = curr.getRhythmValue();
//            Note next = unmerged.getNote(i + duration);
//            try{
//                if(curr.getPitch() == next.getPitch()){
//                    toAdd.setRhythmValue(toAdd.getRhythmValue() + next.getRhythmValue());
//                }else{
//                    merged.addNote(toAdd.copy());
//                    toAdd = next;
//                }
//            }catch(Exception e){
//                System.out.println("Something went wrong. Info below:");
//                System.out.println("i: "+i);
//                System.out.println("duration: "+duration);
//                System.out.println("curr: "+curr);
//                System.out.println("next: "+next);
//            }
//            
//        }
//        //add the last note
//        merged.addNote(toAdd);
//        return merged;
//    }
    
    //Utilities for choosing the first 2-3 notes of a melody:
    
    /**
     * middleOfRange
     * returns the midi value located at the middle of the range
     * (rounds down)
     * @return midi value of middle of range
     */
    private int middleOfRange(){
        return range[0]+((range[1]-range[0])/2);//rounds down for odd numbers
    }
    
    /**
     * inRange
     * Tests if a pitch is in range (being at a range limit is okay)
     * @param pitchToAdd pitch to check
     * @return true if in range, false otherwise
     */
    private boolean inRange(int pitchToAdd) {
        return pitchToAdd >= range[0] && pitchToAdd <= range[1];
    }
 
    
    /**
     * closestToMiddle
     * Returns the version of note n that is closest to the middle of the range
     * Below the middle if ascending, above is descending, closest if no pref
     * @param n Note
     * @param line line
     * @return version of note closest to middle of range
     */
    private Note closestToMiddle(Note n){
        
        int rv = n.getRhythmValue();
        
        int closestBelow = closestBelowMiddle(n);
        boolean belowInRange = inRange(closestBelow)==IN_RANGE;
        int closestAbove = closestAboveMiddle(n);
        boolean aboveInRange = inRange(closestAbove)==IN_RANGE;
        
        int pitch;

        if(belowInRange && aboveInRange){
            int middle = middleOfRange();
            //closest of the two - tiebreak goes to above note if distances equal
            pitch = ((middle-closestBelow)<(closestAbove-middle)?closestBelow:closestAbove);
        }else if(belowInRange){
            pitch = closestBelow;
        }else{//above guaranteed to be in range because we limit the user to an octave
            pitch = closestAbove;
        }

        return new Note(pitch, rv);
    }
    
    /**
     * closestBelowMiddle
     * Returns version of note n that is in the octave below the middle of range
     * @param n note
     * @return version of note in octave below middle of range
     */
    private int closestBelowMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch--){
                
        }
        return pitch;
    }
    
    /**
     * closestAboveMiddle
     * Returns version of note n that is in the octave above the middle of range
     * @param n note
     * @return version of note in octave above middle of range
     */
    private int closestAboveMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch++){
                
        }
        return pitch;
    }
    
    //problems if you pass in a negative pitch...
    /**
     * samePitchClass
     * Returns whether two pitches have the same pitch class
     * @param pitch1 first pitch
     * @param pitch2 second pitch
     * @return true if pitches have the same pitch class, false otherwise
     */
    private boolean samePitchClass(int pitch1, int pitch2){
        return getMod(pitch1) == getMod(pitch2);
    }
    
    /**
     * getMod
     * returns an int representing the pitch class of the midi value
     * (0 for C, ... , 11 for B)
     * @param midi midivalue
     * @return int representing a midi value's pitch class
     */
    private int getMod(int midi){
        return midi%OCTAVE;
    }
    
    /**
     * randomChordOrColorTone
     * Used to choose the first 2-3 notes of the generated solo.
     * @param chord - chord the note is to be played over
     * @param duration - duration desired
     * @return a note that is a random chord or color tone of the chord
     * and is close to the middle of the range
     */
    private Note randomChordOrColorTone(Chord chord, int duration){
        if(chord == null || chord.isNOCHORD()){
            return new Note(middleOfRange(), duration);
        }
        ArrayList<Note> chordAndColorTones = chordAndColorTones(chord, duration);
        int size = chordAndColorTones.size();
        Random r = new Random();
        int choice = r.nextInt(size);
        Note toReturn = chordAndColorTones.get(choice);
        return closestToMiddle(toReturn);
    }
    
     /**
     * chordTones
     * Returns an ArrayList of all the chord and color tones of a given chord
     * @param chord chord from which chord tones are to be extracted
     * @param duration duration that these notes are to have
     * @return ArrayList of chord tones - NOTE: default pitches used
     */
    private static ArrayList<Note> chordAndColorTones(Chord chord, int duration){
        PolylistEnum noteList = chord.getSpell().elements();
        ArrayList<Note> chordAndColorTones = new ArrayList<Note>();
        while(noteList.hasMoreElements()){
            Note note = ((NoteSymbol)noteList.nextElement()).toNote();
            note.setRhythmValue(duration);
            chordAndColorTones.add(note);
        }
        PolylistEnum colorList = chord.getColor().elements();
        while(colorList.hasMoreElements()){
            Note note = ((NoteSymbol)colorList.nextElement()).toNote();
            note.setRhythmValue(duration);
            chordAndColorTones.add(note);
        }
        return chordAndColorTones;
    }

    //Choosing the best note to come next:
    
    /**
     * bestChoice
     * @param prevInterval The interval you're coming from
     * @param prevNote The note you're coming from
     * @param duration The duration of the note you're going to
     * @param chord The chord the note you're going to will be played over
     * @return a good note to go to
     */
    private Note bestChoice(int prevInterval, Note prevNote, int duration) {
        int prevPitch = prevNote.getPitch();
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        //include only those notes that are in range and have nonzero probabilities
        int sourceIndex = intervalToIndex(prevInterval);
        for(int destIndex = 0; destIndex < probabilities[sourceIndex].length; destIndex ++){
            double prob = probabilities[sourceIndex][destIndex];
            int pitchToAdd = prevPitch + indexToInterval(destIndex);
            if(prob != 0 && inRange(pitchToAdd)){
                pitches.add(pitchToAdd);
                pitchProbs.add(prob);
            }
        }

        //if there are no intervals that have nonzero probability that are in range,
        //allow notes out of range
        if(pitchProbs.isEmpty()){
            for(int destIndex = 0; destIndex < probabilities[sourceIndex].length; destIndex ++){
                double prob = probabilities[sourceIndex][destIndex];
                int pitchToAdd = prevPitch + indexToInterval(destIndex);
                if(prob != 0){
                    pitches.add(pitchToAdd);
                    pitchProbs.add(prob);
                }
            }
        }
        
        //readjust probabilities so that they sum to one again
        //they might not sum to 1 anymore because we eliminated options that were out of range
        double total = 0;
        for(double prob : pitchProbs){
            total += prob;
        }
        for(int i = 0; i<pitchProbs.size(); i++){
            pitchProbs.set(i, pitchProbs.get(i)/total);
        }
        
        Random r = new Random();
        double decision = r.nextDouble();
        double totalProb = 0;
        
        //this'll be unaltered if for some reason the probabilites don't sum
        //exactly to 1 and the random number generator produce exactly 1 (unlikely)
        int bestPitch = pitches.get(0);
        
        for(int i = 0; i < pitchProbs.size(); i++){
            totalProb += pitchProbs.get(i);
            if(totalProb > decision){
                bestPitch = pitches.get(i);
                break;
            }
        }
        
        return new Note(bestPitch, duration);
    }
    
    /**
     * bestChoice
     * Uses 2nd order Markov Chain to find a good note to go to
     * @param prevInterval1 The first interval you're coming from
     * @param prevInterval2 The second interval you're coming from
     * @param prevNote The note you're coming from
     * @param duration The duration of the note you're going to
     * @param chord The chord the note you're going to will be played over
     * @return a good note to go to
     */
    private Note bestChoice(int prevInterval1, int prevInterval2, Note prevNote, int duration, Chord chord) {
        int prevPitch = prevNote.getPitch();
        ArrayList<Integer> pitches = new ArrayList<Integer>();
        ArrayList<Double> pitchProbs = new ArrayList<Double>();
        
        int x = intervalToIndex(prevInterval1);
        int y = intervalToIndex(prevInterval2);
        for(int z = 0; z < probabilities2[x][y].length; z ++){
            double prob = probabilities2[x][y][z];
            int pitchToAdd = prevPitch + indexToInterval(z);
            if(prob != 0 && inRange(pitchToAdd)){
                pitches.add(pitchToAdd);
                pitchProbs.add(prob);
            }
        }

        //if there are no intervals that have nonzero probability that are in range,
        //allow notes out of range
        if(pitchProbs.isEmpty()){
            for(int z = 0; z < probabilities2[x][y].length; z ++){
                double prob = probabilities2[x][y][z];
                int pitchToAdd = prevPitch + indexToInterval(z);
                if(prob != 0){
                    pitches.add(pitchToAdd);
                    pitchProbs.add(prob);
                }
            }
        }
        
        //readjust probabilities so that they sum to one again
        //they might not sum to 1 anymore because we eliminated options that were out of range
        double total = 0;
        for(double prob : pitchProbs){
            total += prob;
        }
        for(int i = 0; i<pitchProbs.size(); i++){
            pitchProbs.set(i, pitchProbs.get(i)/total);
        }
        
        Random r = new Random();
        double decision = r.nextDouble();
        double totalProb = 0;
        
        //this'll be unaltered if for some reason the probabilites don't sum
        //exactly to 1 and the random number generator produce exactly 1 (unlikely)
        int bestPitch = pitches.get(0);
        
        for(int i = 0; i < pitchProbs.size(); i++){
            totalProb += pitchProbs.get(i);
            if(totalProb > decision){
                bestPitch = pitches.get(i);
                break;
            }
        }
        
        return new Note(bestPitch, duration);
    }
    
    //Utilities:
    
    /**
     * intervalToIndex
     * Adds an Octave to convert an interval to its array index
     * @param interval directional interval from -12 to 12
     * @return its index (0 to 24)
     */
    private static int intervalToIndex(int interval){
        return interval + Constants.OCTAVE;
    }
    
    /**
     * indexToInterval
     * Subtracts an Octave to convert an index to its interval
     * @param index array index from 0 to 24
     * @return a directional interval from -12 to 12
     */
    private static int indexToInterval(int index){
        return index - Constants.OCTAVE;
    }

}
