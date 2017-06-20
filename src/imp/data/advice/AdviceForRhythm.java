/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

package imp.data.advice;

import imp.data.Key;
import imp.data.Note;
import imp.data.NoteSymbol;
import polya.*;

/**
 *
 * @author Samantha Long
 */
public class AdviceForRhythm 
        extends AdviceForMelody
{
   public AdviceForRhythm(String name, Polylist notes, String chordRoot, Key key,
                        int[] metre, int profileNumber)
    {
    super(name, 0, notes, chordRoot, key, metre, null, profileNumber);
    }

  public AdviceForRhythm(String name, int serial, Polylist notes,
                        String chordRoot, Key key, int[] metre,
                        int profileNumber)
    {
    super(name, serial, notes, chordRoot, key, metre, null, profileNumber);
    }

  public AdviceForRhythm(String name, int serial, Polylist notes,
                        String chordRoot, Key key, int[] metre,
                        Note firstNote, int profileNumber)
    {
    super(name, serial, notes, chordRoot, key, metre, firstNote, profileNumber);
    }

  public AdviceForMelody makeAdviceForMelody(Polylist newNoteSymbols)
  {
      AdviceForMelody parent = (AdviceForMelody)this;
      if( newNoteSymbols.isEmpty() )
        {
          return parent;
        }
      Polylist L = parent.notes;
      Polylist M = Polylist.nil;
      PolylistBuffer buffer = new PolylistBuffer();
      int i = 0;
      while( L.nonEmpty() )
        {
        if( M.isEmpty() )
          {
            M = newNoteSymbols;
          }
        NoteSymbol noteSymbol = (NoteSymbol)L.first();
        if( noteSymbol.isRest() )
          {
            buffer.append(noteSymbol);
          }
        else
          {
            NoteSymbol newPitchNoteSymbol = (NoteSymbol)M.first();
            int dur = noteSymbol.getDuration();
            if( newPitchNoteSymbol.isRest() )
              {
              buffer.append(NoteSymbol.getRestSymbol(dur));
              }
            else
              {
              buffer.append(NoteSymbol.makeNoteSymbol(newPitchNoteSymbol.getMIDI(), dur));
              }
          }
        L = L.rest();
        M = M.rest();
        }
      return new AdviceForMelody(name, serial, buffer.toPolylist(), chordRoot, key, metre, profileNumber);
  }
}
