/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations;

import imp.Constants;
import imp.data.Chord;
import imp.data.Note;
import imp.data.NoteSymbol;
import static imp.data.NoteSymbol.makeNoteSymbol;
import imp.data.PitchClass;
import polya.PolylistEnum;

/**
 *
 * @author muddCS15
 */
public class Scorer {
    
    private final double priorityWeight;
    private final double strongBeatWeight;
    private final double durationWeight;
    private final int [] metre;
    
    /**
     * Scorer
     * scores a note chord pair based on weights passed in
     * @param priorityWeight weight of note's priority in the chord
     * @param strongBeatWeight weight of whether a note falls on a strong beat
     * @param durationWeight weight of the how much of the trend's duration the note takes up
     * @param metre time signature (used to determine strong beat weight)
     */
    public Scorer(double priorityWeight, double strongBeatWeight, double durationWeight, int [] metre){
        this.priorityWeight = priorityWeight;
        this.strongBeatWeight = strongBeatWeight;
        this.durationWeight = durationWeight;
        this.metre = metre;
    }
    
    /**
     * score
     * returns an ncp's score
     * @param ncp note chord pair to be scored
     * @param trend trend ncp is a part of
     * @return score
     */
    public double score(NoteChordPair ncp, TrendSegment trend){
        int score = 0;

        score += priorityWeight*priorityScore(ncp);
        score += strongBeatWeight*strongBeatScore(ncp);
        score += durationWeight*durationScore(ncp, trend);
        
        
        return score;
    }
    
    /**
     * priorityScore
     * Returns a score based on a note's priority in the chord
     * @return a score based on a note's priority
     * the score returned is the length of the chord's priority list minus
     * the index of the note in the list
     * or 0 if the list does not contain the note,
     * i.e. if the note is a color tone. This means that all color tones have
     * the same (bad) priority.
     */
    private double priorityScore(NoteChordPair ncp){
        Note note = ncp.getNote();
        Chord chord = ncp.getChord();
        PolylistEnum priorityList = chord.getPriority().elements();
        double maxScore = chord.getPriority().length();
        double priority = maxScore;
        PitchClass nextPc = makeNoteSymbol(note).getPitchClass();
        while(priorityList.hasMoreElements()){
            NoteSymbol ns = ((NoteSymbol)priorityList.nextElement());
            if(ns.getPitchClass().enharmonic(nextPc)){
                break;
            }
            priority--;
        }
        //if the note is not in the chord's priority list (i.e. is a color tone)
        //return 0
        return normalize(priority, maxScore);
    }
    
    /**
     * normalize
     * normalizes score from 0 to 1 based on max score
     * assumes no negative scores ever given
     * @param score score to be normalized
     * @param maxScore maximum score possible
     * @return normalized score
     */
    public double normalize(double score, double maxScore){
        return score / maxScore;
    }
    
    /**
     * durationScore
     * returns a score between 0 and 1 
     * based on what fraction of a trend's total duration it takes up
     * (including repeats of the note)
     * @param ncp
     * @param trend
     * @return score from 0 to 1
     */
    private double durationScore(NoteChordPair ncp, TrendSegment trend){
        
        double totalDuration = trend.getTotalDuration();
        Note note = ncp.getNote();
        double duration = 0;
        
        NCPIterator i = trend.makeIterator();
        
        
        while(i.hasNext()){
            Note compare = i.nextNCP().getNote();
            if(note.samePitch(compare)){
                duration += compare.getRhythmValue();
            }
        }

        return normalize(duration, totalDuration);
    }
    
    /**
     * strongBeatScore
     * scores an ncp based on what beat it falls on
     * the first beat of a measure is the best, then any strong beat, 
     * then any beat, then off-the-beat
     * NOTE: this assumes the starting slot of the first ncp was numbered 0
     * @param ncp note chord pair to be scored
     * @return score
     */
    private double strongBeatScore(NoteChordPair ncp){
        int slot = ncp.getSlot();
        double maxScore = 3;
        double score;
        
        if(slot % measureLength() == 0){
            score = 3;
        }
        else if(slot % timeBetweenStrongBeats() == 0){
            score = 2;
        }else if(slot % beatLength() == 0){
            score = 1;
        }else{
            score = 0;
        }
        
        return normalize(score, maxScore);
    }
    
    /**
     * beatsPerMeasure
     * @return number of beats in a measure
     */
    private int beatsPerMeasure(){
        return metre[0];
    }
     
    /**
     * beatLength
     * @return length in slots of a single beat
     */
    private int beatLength(){
        return Constants.WHOLE/metre[1];
    }
    
    /**
     * strongBeatsPerMeasure
     * Determines number of strong beats per measure based on the top
     * number in the time signature
     * Could be improved. Right now, if there is some question as to whether
     * something should be felt in two or in three, like in 6/8 or 12/8, 
     * it default to a two feel
     * @return number of strong beats per measure
     */
    private int strongBeatsPerMeasure(){
        int beatsPerMeasure = beatsPerMeasure();
        int strongBeats;
   
        if(beatsPerMeasure <= 3){               //  2/4, 3/4, ...
            strongBeats = 1;
        }else if(beatsPerMeasure % 2 == 0){     //  4/4, 6/8, 12/8, ...
            strongBeats = 2;
        }else if(beatsPerMeasure % 3 == 0){     //  9/8, ...
            strongBeats = 3;
        }else{                                  //  7/8, ...
            strongBeats = 1;
        }
        
        return strongBeats;
    }

    /**
     * measureLength
     * @return length of a measure in slots
     */
    private int measureLength(){
        return beatLength()*beatsPerMeasure();
    }
    
    /**
     * timeBetweenStrongBeats
     * @return length in slots between one strong beat and the next
     */
    private int timeBetweenStrongBeats(){
        return measureLength() / strongBeatsPerMeasure();
    }
    
}
