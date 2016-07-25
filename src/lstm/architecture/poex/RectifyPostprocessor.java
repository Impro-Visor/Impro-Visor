/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import imp.data.Chord;
import imp.data.ChordForm;
import imp.data.ChordPart;
import imp.data.NoteSymbol;
import lstm.io.leadsheet.Constants;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import polya.Polylist;

/**
 *
 * @author cssummer16
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
