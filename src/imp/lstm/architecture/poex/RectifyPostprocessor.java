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

package imp.lstm.architecture.poex;

import imp.data.Chord;
import imp.data.ChordForm;
import imp.data.ChordPart;
import imp.data.NoteSymbol;
import imp.lstm.io.leadsheet.Constants;
import mikera.vectorz.AVector;
import polya.Polylist;

/**
 * Postprocessor to rectify notes chosen by forcing them to be in the chord.
 * @author Daniel Johnson
 */
public class RectifyPostprocessor implements ProbabilityPostprocessor {
    private ChordPart chords;
    private int position;
    private boolean useColor;
    private int offset;
    
    public RectifyPostprocessor(){
        this(0);
    }
    
    public RectifyPostprocessor(int lowBound){
        offset = lowBound;
    }
    
    
    public void start(ChordPart c, boolean colorTones){
        chords = c;
        position = 0;
        useColor = colorTones;
    }
    
    @Override
    public AVector postprocess(AVector probabilities) {
        Chord curChord = chords.getCurrentChord(position);
        ChordForm form = curChord.getChordForm();
        String root = curChord.getRoot();

        Polylist usableTones = form.getSpell(root);
        if(useColor)
            usableTones = usableTones.append(form.getColor(root));

        for(int i=0; i<12; i++){
            NoteSymbol sym = NoteSymbol.makeNoteSymbol((i+offset)%12);
            boolean allowed = sym.enhMember(usableTones);
            if(!allowed){
                for(int pidx=2+i; pidx<probabilities.length(); pidx+=12){
                    probabilities.set(pidx, 0.0);
                }
            }
        }
        
        position += Constants.RESOLUTION_SCALAR;
        
        return probabilities;
    }
}
