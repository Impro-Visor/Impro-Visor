/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public void add(NoteChordPair ncp){
        ncps.add(ncp);
    }
    
    public ArrayList<NoteChordPair> getNCPS(){
        return ncps;
    }
    
    public void clear(){
        ncps.clear();
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
    
}
