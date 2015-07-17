/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations;
import imp.Constants;
import imp.Constants.Accidental;
import java.util.ArrayList;
import imp.data.*;
import imp.lickgen.transformations.trends.Trend;
/**
 *
 * @author muddCS15
 */
public class TrendDetector {
    
    private final Trend trend;
    
    //use this instead of -1 because we might want different behaviour for rests
    //(rests use a pitch value of -1)
    private static final int NOT_A_PITCH = -2;
    
    public static final Note NOT_IN_TREND = new Note(NOT_A_PITCH, Accidental.NOTHING, Constants.DEFAULT_DURATION);

    /**
     * TrendDetector (constructor)
     * Detects a given trend
     * @param trend the trend to be detected
     */
    public TrendDetector(Trend trend){
        this.trend = trend;
    }
    
    /**
     * trends
     * Returns a list of trends in the solo
     * @param solo the melody part to detect trends in
     * @param chords the chord part the melody is played over
     * @return a list of trends in the solo as melody parts
     */
    public ArrayList<TrendSegment> trends(MelodyPart solo, ChordPart chords){
        
        //initialize list of trends
        ArrayList<TrendSegment> trends = new ArrayList<TrendSegment>();
        
        //all notes in solo
        ArrayList<Note> notes = solo.getNoteList();
        
        //the current trend - cleared and added to trends at the end of each trend
        TrendSegment currentTrend = new TrendSegment();
        
        //careful: even if a note can't be added to a trend, it could start the next one!
        
        //the previous note that is already part of the trend
        Note prevNote = NOT_IN_TREND;
        
        //the var number (corresponds to n1, n2, etc in source notes)
        int varNumber = 1;
        
        //the slot number we're at
        int slotNumber = solo.getFirstIndex();
        
        for(Note currNote : notes){
            
            Chord currChord = chords.getCurrentChord(slotNumber);
            
            //End of trend detected.
            //(if prevNote is null, stopCondition bases its decision only on how currNote fits in the chord)
            if(trend.stopCondition(prevNote, currNote, currChord)){
                
                //If the trend is at least 2 notes long, add it to the trend list.
                if(currentTrend.getNCPS().size() >= 2){
                    trends.add(currentTrend);
                }
                
                //Clear the current trend.
                currentTrend = new TrendSegment();
                
                //Clear the prevNote
                prevNote = NOT_IN_TREND;
                
                //reset var number to 1
                varNumber = 1;
                
                //Even though currNote couldn't be added to the end of the previous trend,
                //it could still start a new trend. If it can, add it to the current trend
                //and set prevNote to currNote.
                if(!trend.stopCondition(prevNote, currNote, currChord)){
                    currentTrend.add(new NoteChordPair(currNote, currChord, slotNumber, varNumber++));
                    prevNote = currNote;
                }
            
            //Note can be added to trend.
            }else{
                currentTrend.add(new NoteChordPair(currNote, currChord, slotNumber, varNumber++));
                prevNote = currNote;
            }
            
            slotNumber += currNote.getRhythmValue();
            
        }

        return trends;
    }
    
}
