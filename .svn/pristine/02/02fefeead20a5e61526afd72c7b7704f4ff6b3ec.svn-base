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

package imp.gui;

import imp.data.BassPatternElement;

/**
 *
 * @author keller
 */
public class PianoRollBassBar extends PianoRollBar
{
    private BassPatternElement element;

    public PianoRollBassBar(int startSlot, BassPatternElement element, int volume, boolean volumeImplied, PianoRoll pianoRoll)
    {
        super(PianoRoll.BASS_ROW, startSlot, element.getSlots(), PianoRoll.BASSCOLOR, PianoRoll.BARBORDERCOLOR, volume, volumeImplied, pianoRoll.getGrid(), pianoRoll);
        this.element = element;
    }

    /*
     * Copy constructor
     */

public PianoRollBassBar(PianoRollBassBar bar)
    {
        this(bar.startSlot, bar.element, bar.volume, bar.volumeImplied, bar.pianoRoll);
    }

/**
 * Over-rides copy in PianoRollBar
 */

@Override
public PianoRollBassBar copy()
{
    return new PianoRollBassBar(this);
}

    public void setBassParameters(BassPatternElement.BassNoteType noteType, int duration, BassPatternElement.AccidentalType accidental, int degree, BassPatternElement.DirectionType direction)
    {
        element.setNoteType(noteType);
        element.setAccidental(accidental);
        element.setDegree(degree);
        element.setDirection(direction);
        setNumSlots(duration);

        //System.out.println("Setting bass parameters to " + getText());
    }

    @Override
    public void setNumSlots(int slots)
    {
        super.setNumSlots(slots);
        element.setDuration(slots);
    }

    public BassPatternElement.BassNoteType getNoteType()
    {
        return element.getNoteType();
    }

    public BassPatternElement.AccidentalType getAccidental()
    {
        return element.getAccidental();
    }

    public String getAccidentalString()
    {
        return element.getAccidentalString();
    }


    public int getDegree()
    {
        return element.getDegree();
    }

    public BassPatternElement.DirectionType getDirection()
    {
        return element.getDirection();
    }

    public String getDirectionString()
    {
        return element.getDirectionString();
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

    public BassPatternElement getElementCopy()
    {
        return element.getCopy();
    }
}
