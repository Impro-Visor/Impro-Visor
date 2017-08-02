/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import java.util.ArrayList;
import javax.swing.AbstractListModel;


/**
 *
 * @author Lukas Gnirke
 */
public class LeadsheetImageListModel extends AbstractListModel {
    ArrayList<RhythmSelecterEntry> entries;
    int size;
    boolean session;
    
    
    public LeadsheetImageListModel(ArrayList<RhythmSelecterEntry> rhythmEntries){
        super();
        entries = rhythmEntries;
    }
    
    @Override
    public int getSize() {
        return entries.size();
        //return entries.size();
    }

    
    @Override
    public RhythmSelecterEntry getElementAt(int index) {
        return entries.get(index);
    }

    
    public void addListElement(RhythmSelecterEntry rse){
        entries.add(rse);
    }


    public void removeElementAtIndex(int i) {
        entries.remove(i);
    }
    
    public ArrayList<RhythmSelecterEntry> getList(){
        return this.entries;
    }
    
    public void removeListElements(int[] indices){
        int numRemovedSoFar = 0;
        for(int i = 0; i < indices.length; i++){
            int indexToRemove = indices[i] - numRemovedSoFar;
            if(indexToRemove < 0){
                indexToRemove = 0;
            }
            entries.remove(indexToRemove);
            numRemovedSoFar++;
        }
    }
    
    public ArrayList<RhythmSelecterEntry> getEntries(){
        return this.entries;
    }
}

