/*
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
package imp.gui;

import imp.data.stylePatterns.ChordPatternElement;

/**
 *
 * @author Carli Lessard
 */
public class PianoRollChordBar extends PianoRollBar
{
    
    private ChordPatternElement element;
    
    public PianoRollChordBar(int startSlot, ChordPatternElement element, int volume, 
            boolean volumeImplied, PianoRoll pianoRoll)
    {
        super(PianoRoll.CHORD_ROW, startSlot, element.getSlots(), PianoRoll.CHORDCOLOR,
                PianoRoll.BARBORDERCOLOR, volume, volumeImplied, pianoRoll.getGrid(), pianoRoll);
        this.element = element;
    }
    
    public PianoRollChordBar(PianoRollChordBar bar)
    {
        this(bar.startSlot, bar.element, bar.volume, bar.volumeImplied, bar.pianoRoll);
    }
    
    @Override
    public PianoRollChordBar copy()
    {
        return new PianoRollChordBar(this);
    }
    
    public void setChordParameters(ChordPatternElement.ChordNoteType noteType, int duration, int degree)
    {
        element.setNoteType(noteType);
        element.setDegree(degree);
        setNumSlots(duration);
    }
    
    @Override
    public void setNumSlots(int slots)
    {
        super.setNumSlots(slots);
        element.setDuration(slots);
    }
    
    public ChordPatternElement.ChordNoteType getNoteType()
    {
        return element.getNoteType();
    }
    
    public int getDegree()
    {
        return element.getDegree();
    }
    
    @Override
    public Object getText()
    {
        return element.getText();
    }
    
    public int getSlots()
    {
        return element.getSlots();
    }
    
    public ChordPatternElement getElementCopy()
    {
        return element.getCopy();
    }
}
