/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 */
public class MelodyGenerator {

    private double [][] probabilities;
    private double [][][] probabilities2;
    private MelodyPart rhythm;
    private ChordPart chords;
    private int [] range;
    //whether to rectify
    private boolean rectify;
    //types of notes to allow in rectification
    private boolean includeChord, includeColor, includeApproach;
    //whether to merge same notes
    private boolean merge;

    private static final int NO_DATA = Integer.MAX_VALUE;
    private static final boolean IN_RANGE = true;
    
    public static final String PRE = "PRE";
    public static final String POST = "POST";
    public static final String NONE = "NONE";
    
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
    
    public MelodyGenerator(IntervalLearner learner, Notate notate, ChordPart chords, int [] range, boolean merge, boolean rectify, boolean [] include){
        this(learner, polylistToMelodyPart(rhythm(notate)), chords, range, merge, rectify, include);
    }
    
    public static Polylist rhythm(Notate notate){
        Grammar gram = new Grammar(notate.getGrammarFileName());
        return justRhythm(gram.run(0, notate.getScoreLength(), notate, false, false, -1));
    }

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
            RectifyPitchesCommand cmd = new RectifyPitchesCommand(result, 0,
                                result.size()-1, chords,
                                false, false,
                                includeChord, includeColor, includeApproach);
            cmd.execute();
        }
        //merge same notes - good idea???
        if(merge){
            result = mergeSameNotes(result);
        }
        return result;
    }    
    
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
                toAdd = bestChoice(prevInterval, prevNote, duration, chord);
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
            RectifyPitchesCommand cmd = new RectifyPitchesCommand(result, 0,
                                result.size()-1, chords,
                                false, false,
                                includeChord, includeColor, includeApproach);
            cmd.execute();
        }
        //merge same notes - good idea???
        if(merge){
            result = mergeSameNotes(result);
        }
        return result;
    }
    
    
    
    private MelodyPart mergeSameNotes(MelodyPart unmerged){
        MelodyPart merged = new MelodyPart();
        int duration = unmerged.getNote(0).getRhythmValue();
        Note toAdd = unmerged.getNote(0);
        int lastIndex = unmerged.getLastActiveSlot();
        for(int i = 0; i + duration <= lastIndex; i += duration){
            
            Note curr = unmerged.getNote(i);
            duration = curr.getRhythmValue();
            Note next = unmerged.getNote(i + duration);
            try{
                if(curr.getPitch() == next.getPitch()){
                    toAdd.setRhythmValue(toAdd.getRhythmValue() + next.getRhythmValue());
                }else{
                    merged.addNote(toAdd.copy());
                    toAdd = next;
                }
            }catch(Exception e){
                System.out.println("Something went wrong. Info below:");
                System.out.println("i: "+i);
                System.out.println("duration: "+duration);
                System.out.println("curr: "+curr);
                System.out.println("next: "+next);
            }
            
        }
        //add the last note
        merged.addNote(toAdd);
        return merged;
    }
    
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
    
    private Note randomChordOrColorTone(Chord chord, int duration){
        if(chord.isNOCHORD()){
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

    private Note bestChoice(int prevInterval, Note prevNote, int duration, Chord chord) {
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
    
    private static int intervalToIndex(int interval){
        return interval + Constants.OCTAVE;
    }
    
    private static int indexToInterval(int index){
        return index - Constants.OCTAVE;
    }

    private boolean inRange(int pitchToAdd) {
        return pitchToAdd >= range[0] && pitchToAdd <= range[1];
    }
 
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
    
}
