/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.Vector0;

/**
 *
 * @author cssummer16
 */
public abstract class RelativeInputPart {
    public abstract int get_width();
    public abstract AVector generate(int relativePosition, int chordRoot, AVector chordTypeData);
    public static AVector combine(RelativeInputPart[] parts, int relativePosition, int chordRoot, AVector chordTypeData) {
        AVector current = Vector0.INSTANCE;
        for(RelativeInputPart part : parts) {
            current = current.join(part.generate(relativePosition, chordRoot, chordTypeData));
        }
        return current;
    }
}
