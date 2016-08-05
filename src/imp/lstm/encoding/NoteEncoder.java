/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.encoding;

import mikera.vectorz.AVector;

/**
 *  Abstract class NoteEncoding allows for strategy implementation of the encoding process, 
 *  providing an interface for encoding and decoding notes between midiValues and bit vectors
 * @author Nicholas Weintraut
 */
public interface NoteEncoder {
    
    public AVector encode(int midiValue);
    
    public int getSustainKey();
    
    public boolean hasSustain(AVector input);
    
    public int decode(AVector input);
    
    public default int getNoteLength(){
        int sum = 0;
        for(Group group : getGroups())
            sum += group.length();
        return sum;
    }
    
    public Group[] getGroups();
    
    public AVector clean(AVector input);
    
}
