/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package imp.data;

/**
 * MelodyPartAccompanied is an extension of the MelodyPart class that is used
 * so that a MelodyPart can access information contained in the ChordPart class.
 * Initially implemented to help with the implementation of mixed meters.
 * @author Jack Davison
 */
public class MelodyPartAccompanied extends MelodyPart {
    
    private ChordPart chordProg;
    
    public MelodyPartAccompanied(int size, ChordPart chordProg){
        super(size);
        this.chordProg = chordProg;
    }
    
    public ChordPart getChordProg() {
        return chordProg;
    }
    
    
    @Override
    public int[] getMetre() {
        return chordProg.getMetre();
    }
    
    public void getMetre(int metre[]) {
        chordProg.getMetre(metre);
    }
    
    @Override
    public void setMetre(int top, int bottom){
        chordProg.setMetre(top, bottom);
    }
    
    public void setMetre(int metre[]) {
        chordProg.setMetre(metre);
    }
    
}
