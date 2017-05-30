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

import imp.lstm.encoding.Group;
import imp.lstm.encoding.NoteEncoder;
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
