/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture.poex;

import imp.lstm.encoding.ChordEncoder;
import static imp.lstm.encoding.ChordEncoder.CHORD_TYPES;
import static imp.lstm.encoding.ChordEncoder.DISTANCES_FROM_C;
import java.util.Map;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 *
 * @author cssummer16
 */
public class PassthroughChordEncoder extends ChordEncoder {
    @Override
    public AVector encode(String root, AVector chordData, String bass)
    {
        if(chordData == null)
            return null;
        else {
            AVector transposedData = transposeChordData(chordData, (int) DISTANCES_FROM_C.getValue(root).intValue() - (int) DISTANCES_FROM_C.getValue(bass).intValue());
            transposedData.set(0, 1);
            AVector allData = Vector.of(DISTANCES_FROM_C.getValue(bass).intValue());
            return allData.join(transposedData);
        }
    }
    
    @Override
    public String decode(AVector chordData) {
        double bass = chordData.get(0);
        AVector typeData = chordData.subVector(1, chordData.length()-1);
        if(typeData.isZero())
            return "NC";
        for(Map.Entry<String, AVector> entry : CHORD_TYPES.entrySet()) {
            if(typeData.equals(entry.getValue())){
                String type = entry.getKey();
                return DISTANCES_FROM_C.getKey(bass) + type;
            }
        }
        // Check slash chords
        int transposition = 0;
        AVector without_bass = Vector.of(0).join(typeData.subVector(1,typeData.length()-1));
        while (transposition < 12) {
            String type;
            for (Map.Entry<String, AVector> entry : CHORD_TYPES.entrySet()) {
                if (typeData.equals(entry.getValue()) || without_bass.equals(entry.getValue())) {
                    type = entry.getKey();
                    double root_note = (bass + transposition)%12;
                    return DISTANCES_FROM_C.getKey(root_note) + type + "/" + DISTANCES_FROM_C.getKey(bass);
                }
            }
            typeData = transposeChordData(typeData, -1);
            without_bass = transposeChordData(without_bass, -1);
            transposition++;
        }
        System.out.println("Chord not found! Substituting NC");
        return "NC";
    }
}
