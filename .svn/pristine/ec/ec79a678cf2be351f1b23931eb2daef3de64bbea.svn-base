/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2013 Robert Keller and Harvey Mudd College
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

import imp.Constants;
import imp.util.Preferences;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 * 
 * Class to help with classifying notes from chords
 */
public class Coloration implements Constants {
    
    private static String noteColorString = Preferences.getPreference(Preferences.NOTE_COLORING);

    public static int[] collectNoteColors(MelodyPart part, ChordPart chordProg) {
        int number = part.size();
        int[] color = new int[number];
        for (int i = 0; i < number; i++) {
            Note curNote = part.getNote(i);
            if (curNote != null) {
                Note origNote = part.getNote(i);
                color[i] = determineColor(curNote, origNote, i, false, color, part, chordProg);
            }
        }
        for (int i = 0; i < number; i++) {
            Note curNote = part.getNote(i);
            if (curNote != null && curNote.isTied() && !curNote.firstTied() && part.getPrevIndex(i) >= 0) {
                color[i] = color[part.getPrevIndex(i)];
            }
        }
        return color;
    }

    public static int determineColor(Note note, Note pitchDeterminer, int i, boolean isApproach, int[] colorArray, MelodyPart melody, ChordPart chordProg) {
        Chord c = chordProg.getCurrentChord(i);
        // Deal with note coloration
        int noteType;
        noteType = (c == null || pitchDeterminer == null) ? CHORD_TONE : c.getTypeIndex(pitchDeterminer);
        if (noteType == FOREIGN_TONE && isApproach) {
            noteType = APPROACH_TONE;
        }
        int prevIndex = melody.getPrevIndex(i);
        Note prevNote = melody.getNote(prevIndex);
        boolean approachable = noteType == CHORD_TONE || noteType == COLOR_TONE;
        if (c != null 
            && (note != null) 
            && approachable && !note.isRest() 
            && !c.getName().equals("NC") 
            && prevNote != null 
            && !prevNote.isRest() 
            && !isApproach) {
            if (prevNote.getPitch() == note.getPitch() - 1 
                || prevNote.getPitch() == note.getPitch() + 1) {
                colorArray[prevIndex] = determineColor(prevNote, melody.getNote(prevIndex), 
                        prevIndex, true, colorArray, melody, chordProg);
            }
        }
        int noteColor = Integer.parseInt("" + noteColorString.charAt(noteType)) - 1;
        return noteColor;
    }

    public static int getNoteClassification(int color) {
        switch (color) {
            case BLACK:
                return CHORD_TONE;
            case GREEN:
                return COLOR_TONE;
            case BLUE:
                return APPROACH_TONE;
            case RED:
                return FOREIGN_TONE;
        }
        return CHORD_TONE;
    } 
}
