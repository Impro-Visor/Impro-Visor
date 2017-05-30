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

package imp.lickgen.transformations;

import java.util.ArrayList;
/**
 *
 * @author muddCS15
 */
public class TrendSegment {
    

    private final ArrayList<NoteChordPair> ncps;
    
    public TrendSegment(){
        ncps = new ArrayList<NoteChordPair>();
    }
    
    public TrendSegment(ArrayList<NoteChordPair> noteList){
        ArrayList<NoteChordPair> copiedNotes = new ArrayList<NoteChordPair>();
        for(NoteChordPair ncp : noteList){
            copiedNotes.add(ncp);
        }
        ncps = copiedNotes;
    }

    public void add(NoteChordPair ncp){
        ncps.add(ncp);
    }

    public int getTotalDuration(){
        int total = 0;
        for(NoteChordPair ncp : ncps){
            total += ncp.getDuration();
        }
        return total;
    }

    public int getSize(){
        return ncps.size();
    }
    
    public int getStartSlot(){
        return ncps.get(0).getSlot();
    }
    
    @Override
    public String toString(){
        String toreturn = "";
        toreturn+="Trend:\n";
        for(NoteChordPair ncp : ncps){
            toreturn += (ncp.toString()+"\n");
        }
        return toreturn;
    }
    
    public void clear(){
        ncps.clear();
    }
    
    public ArrayList<TrendSegment> splitUp(int duration){
        ArrayList<TrendSegment> chunks = new ArrayList<TrendSegment>();
        TrendSegment currentChunk = new TrendSegment();
        int durationRemaining = duration;
        for(NoteChordPair ncp : ncps){
            currentChunk.add(ncp);
            durationRemaining -= ncp.getNote().getRhythmValue();
            if(durationRemaining <= 0 || ncp.equals(ncps.get(ncps.size()-1))){
                durationRemaining = duration;
                TrendSegment toAdd = currentChunk.copy();
                chunks.add(toAdd);
                currentChunk.clear();
            }
        }
        return chunks;
    }

    public NCPIterator makeIterator(){
        return new NCPIterator(ncps);
    }
    
    public NoteChordPair firstNCP(){
        if(!ncps.isEmpty()){
            return ncps.get(0);
        }
        else{
            return null;
        }
    }
    
    public NoteChordPair lastNCP(){
        if(!ncps.isEmpty()){
            return ncps.get(ncps.size()-1);
        }
        else{
            return null;
        }
    }
    
    public TrendSegment copy(){
        return new TrendSegment(this.ncps);
    }
    
    public void renumber(){
        if(ncps.isEmpty()){
            return;
        }
        for(int i = 0; i<ncps.size(); i++){
            ncps.get(i).setVar(i+1);
        }
    }
    
}
