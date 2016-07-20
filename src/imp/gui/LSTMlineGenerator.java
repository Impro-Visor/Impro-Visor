/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2016 Robert Keller and Harvey Mudd College
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
package imp.gui;

import imp.Constants;
import imp.data.ChordPart;
import imp.data.Note;
import imp.data.MelodyPart;

/**
 * The LSTMlineGenerator class is used to create a melody from a ChordPart
 * using LSTM technology.
 *
 * @see ChordPart and MelodyPart
 * @author adopted from GuideLineGenerator by Mikayla Konst and Carli Lessard
 *
 */
public class LSTMlineGenerator implements Constants
  {

    ChordPart chordPart;

    public LSTMlineGenerator(ChordPart inputChordPart)
    {
        this.chordPart = inputChordPart;
    }

    /**
     * Creates a MelodyPart that contains the LSTM-generated line.
     *
     * @return MelodyPart
     */
    public MelodyPart makeLSTMline()
    {
        MelodyPart lstmMelody = new MelodyPart();

        lstmMelody.addNote(new Note(70, 120));
        lstmMelody.addNote(new Note(71, 120));
        lstmMelody.addNote(new Note(72, 240));
        return lstmMelody;
    }

  }
