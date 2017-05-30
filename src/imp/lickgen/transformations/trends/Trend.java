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

package imp.lickgen.transformations.trends;
import imp.data.*;
import imp.lickgen.transformations.NCPIterator;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.Scorer;
import imp.lickgen.transformations.TrendDetector;
import imp.lickgen.transformations.TrendSegment;
import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public abstract class Trend {

    //stop condition based on interval and note's role in chord
    public boolean stopCondition(Note n1, Note n2, Chord c){
        
        //if no chord or one of the notes absent or rest, stop the trend
        if(c == null || c.isNOCHORD() || n1 == null || n1.isRest() || n2 == null || n2.isRest()){
            return true;
        }
        
        //previous note was not part of trend. n2 could start a new trend.
        if(n1 == TrendDetector.NOT_IN_TREND){
            return stopCondition(n2, c);
        }
        
        //return whether a stop condition is satisfied
        return stopCondition(n1, n2) || stopCondition(n2, c);
    }
    
    //method to extract important / outline notes from the identified trend
    public TrendSegment importantNotes(TrendSegment trend, int [] metre){
        int flatNotes = numberOfSections();
        int totalNotes = trend.getSize();
        //always flatten to a smaller number of notes than we started with
        if(totalNotes <= flatNotes){
            //flatten to half the total number of notes (int division rounds down)
            flatNotes = totalNotes/2;
        }
        int duration = trend.getTotalDuration()/flatNotes;
        ArrayList<TrendSegment> sections = trend.splitUp(duration);

        TrendSegment importantNotes = new TrendSegment();
        for(TrendSegment currTrend : sections){
            //each of these has length equal to the length of currTrend
            //and start slot equal to the start slot of currTrend
            importantNotes.add(importantNote(currTrend, metre));
        }
        //RENUMBER BEFORE RETURNING!!!
        importantNotes.renumber();
        
        return importantNotes;
    }
    
    //method to extract a single important note from a trend
    public NoteChordPair importantNote(TrendSegment trend, int [] metre){
        double bestScore = -1;
        NoteChordPair bestNCP = null;
        NCPIterator i = trend.makeIterator();
        while(i.hasNext()){
            NoteChordPair currNCP = i.nextNCP();
            double currScore = score(currNCP, trend, metre);
            //tie break - only replace old score if new score is striclty better
            if(currScore > bestScore){
                bestScore = currScore;
                bestNCP = currNCP;
            }
        }
        //MAKE COPY OF NCP TO RETURN
        NoteChordPair toReturn = bestNCP.copy();
        
        //ADJUST LENGTH OF NOTE TO BE LENGTH OF THE ENTIRE TREND
        toReturn.setDuration(trend.getTotalDuration());
        
        //ADJUST THE STARTING SLOT OF THE NOTE TO BE THE STARTING SLOT OF THE ENTIRE TREND
        toReturn.setSlot(trend.getStartSlot());
        
        return toReturn;
    }
    
    //score function
    public double score(NoteChordPair ncp, TrendSegment trend, int [] metre){
        double [] weights = weights();
        Scorer scorer = new Scorer(weights[0], weights[1], weights[2], metre);
        return scorer.score(ncp, trend);
    }
    
    //directional distance between two notes
    public static int dist(Note n1, Note n2){
        return n2.getPitch() - n1.getPitch();
    }
    
    //absolute distance between two notes
    public static int absDist(Note n1, Note n2){
        return Math.abs(dist(n1, n2));
    }
    
    //weights given to priority, strong beat, and duration
    public abstract double [] weights ();
    
    //stop condition based on interval
    public abstract boolean stopCondition(Note n1, Note n2);
    
    //stop condition based on note's role in the chord
    public abstract boolean stopCondition(Note n, Chord c);
    
    //number of sections to split trend up into
    public abstract int numberOfSections();
    
    //return name of trend in all caps
    public abstract String getName();
    
}
