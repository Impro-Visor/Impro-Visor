/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
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
