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
public class NCPIterator {
    
    private int index;
    private ArrayList<NoteChordPair> ncps;
    
    public NCPIterator(ArrayList<NoteChordPair> ncps){
        index = 0;
        this.ncps = ncps;
    }
    
    public NoteChordPair nextNCP(){
        return ncps.get(index++);
    }
    
    public boolean hasNext(){
        return index < ncps.size();
    }
    
}
