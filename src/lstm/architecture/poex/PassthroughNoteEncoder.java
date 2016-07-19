/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import lstm.encoding.Group;
import lstm.encoding.NoteEncoder;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 *
 * @author cssummer16
 */
public class PassthroughNoteEncoder implements NoteEncoder {

    static final int SUSTAIN_KEY = -2;
    
    @Override
    public AVector encode(int midiValue) {
        return Vector.of(midiValue);
    }

    @Override
    public int getSustainKey() {
        return SUSTAIN_KEY;
    }

    @Override
    public boolean hasSustain(AVector input) {
        return this.decode(input) == SUSTAIN_KEY;
    }

    @Override
    public int decode(AVector input) {
        return (int) input.get(0);
    }

    @Override
    public Group[] getGroups() {
        Group[] groups = new Group[1];
        groups[0] = new Group(0, 1, false);
        return groups;
//        throw new UnsupportedOperationException("PassthroughNoteEncoder does not have groups");
    }

    @Override
    public AVector clean(AVector input) {
        return input;
    }
    
}
