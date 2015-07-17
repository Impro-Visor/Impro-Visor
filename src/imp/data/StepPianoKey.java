/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.data;

import java.lang.Object.*;
import javax.sound.midi.*;
import javax.swing.*;

/**
 *
 * The StepPianoKey class defines a piano key. Each piano key has a MIDI value, a name,
 * and a boolean showing whether it has been pressed or not.
 *
 * @author Audrey Musselman-Brown
 * Modified from the PianoKey class
 */
public class StepPianoKey {
    
    static final int NOTE_VELOCITY = 63;
    static final int NOTE_DURATION = 4;
    
    public javax.swing.ImageIcon whiteKey = 
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"));

    public javax.swing.ImageIcon whiteKeyChord = 
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekeypressedgrayscale.jpg"));

    public javax.swing.ImageIcon whiteKeyColor = 
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekeypressed.jpg"));
    
    public javax.swing.ImageIcon whiteKeyPressed = 
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekeypurple.jpg"));
    
    public javax.swing.ImageIcon bassKey = 
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/rootkey.jpg"));
    
    /**
     *  Data members: MIDI value of the note, its name, and whether it has been pressed.
     */
    
    private int midiValue;
    
    private boolean pressed;
    
    private String name;
    
    private JLabel label;
    
    private JLabel numLabel;
    
    private Icon chordIcon;
    
    private Icon colorIcon;
    
    private Icon pressedIcon;
    
    private Icon offIcon;
    
    private Icon bassIcon;
    
    private int defaultMIDI = 60;
    
    private JLabel defaultLabel = new JLabel(offIcon);
    
    private JLabel defaultNumLabel = new JLabel("");
    
    private Icon defaultChordIcon = whiteKeyChord;
    
    private Icon defaultColorIcon = whiteKeyColor;
    
    private Icon defaultPressedIcon = whiteKeyPressed;
    
    private Icon defaultOffIcon = whiteKey;
    
    private Icon defaultBassIcon = bassKey;
    
    
    
    /**
     * Constructor for a piano key given a MIDI value.
     * 
     * @param midiValue, onIcon, offIcon, label
     */
    
    public StepPianoKey(int midiValue, Icon chordIcon, Icon colorIcon, Icon pressedIcon,
                        Icon offIcon, Icon bassIcon, JLabel label, JLabel numLabel)
    {
        this.midiValue = midiValue;
        this.label = label;
        this.numLabel = numLabel;
        this.chordIcon = chordIcon;
        this.colorIcon = colorIcon;
        this.pressedIcon = pressedIcon;
        this.bassIcon = bassIcon;
        this.offIcon = offIcon;
        this.pressed = false;
        
        // get the String representation of the NoteSymbol & remove the number at the end
        Note n = new Note(midiValue);
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        String s = ns.toString();
        int len = s.length() - 1;
        
        this.name = s.substring(0,len);
    }
    
    public StepPianoKey()
    {
        new StepPianoKey(
                defaultMIDI, defaultChordIcon, defaultColorIcon, defaultPressedIcon,
                defaultOffIcon, defaultBassIcon, defaultLabel, defaultNumLabel);
    }
    
    /**
     * isPressed() is a boolean displaying whether a note has been pressed or not.
     * 
     * @return true if the note has been pressed
     */
    public boolean isPressed()
    {
        return this.pressed;
    }
    
    /**
     * getMIDI() gets the MIDI value of a certain note
     * 
     * @return an integer, the MIDI value
     */
    public int getMIDI()
    {
        return this.midiValue;
    }
    
    /**
     * getName() returns the string representation of a note's name for use
     * with voicings
     * 
     * @return the name of the note. ex. d-- for D two octaves below middle C.
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * getLabel() finds the label connected to a piano key
     * 
     * @return a JLabel ex. keyCsharp4
     */
    public JLabel getLabel()
    {
        return this.label;
    }
    
    public JLabel getNumLabel()
    {
        return this.numLabel;
    }
    
    /**
     * getChordIcon() gives the "pressed" icon of the key
     * 
     * @return an icon, whiteKeyPressed or blackKeyPressed
     */
    public Icon getChordIcon()
    {
        return this.chordIcon;
    }
    
    public Icon getColorIcon()
    {
        return this.colorIcon;
    }
    
    public Icon getPressedIcon()
    {
        return this.pressedIcon;
    }
    
    /**
     * getOffIcon() gives the "released" icon of the key
     * 
     * @return an icon, whiteKey or blackKey
     */
    public Icon getOffIcon()
    {
        return this.offIcon;
    }
    
    public Icon getBassIcon()
    {
        return this.bassIcon;
    }
    
    /**
     * setPressed sets a note as being pressed or not
     * @param on
     * @return on
     */
    public boolean setPressed(boolean on)
    {
        this.pressed = on;
        return on;
    }
    
    /**
     * Playing a single note.
     * 
     * @param midiValue
     * @throws javax.sound.midi.MidiUnavailableException
     */
    public static void playNote(int midiValue)
            throws MidiUnavailableException
    {
        Synthesizer synth = MidiSystem.getSynthesizer();
        if(!(synth.isOpen())) {
            try {
                synth.open();
            }
            catch (MidiUnavailableException e) {
                throw new MidiUnavailableException("Midi unavailable");
            }
        }
        MidiChannel chan[] = synth.getChannels();
        if (chan[4] != null) {
            chan[4].noteOn(midiValue, NOTE_VELOCITY);
            
            chan[4].noteOff(midiValue, NOTE_VELOCITY);
        }
    }

    
}
