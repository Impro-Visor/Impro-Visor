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
package imp.data;

import imp.gui.Notate;
import imp.lickgen.LickGen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import polya.Polylist;

/**
 * HAS NOTHING TO DO WITH PHRASE CLASS!!!
 * @author Zachary Kondak
 */
public class PhraseTable {
    private HashMap<Integer, LinkedList<MelodyPart>> phrases;
    private IntervalLearner intervals;
    private Notate notate;
    private LickGen lickGen;
    private static int intervalSoloLength = 12;
    
    
    PhraseTable(Notate notate){
        phrases = new HashMap<Integer, LinkedList<MelodyPart>>();
        this.notate = notate;
        this.lickGen = notate.getLickGen();
        MelodyPart intervalSolo = notate.genSolo(0, notate.getScoreLength() * intervalSoloLength);
        intervals = new IntervalLearner();
        intervals.learnFrom(intervalSolo);
    }
    
    public void addPhrase(MelodyPart phraseToAdd){
        LinkedList<Integer> pitches = getNotes(phraseToAdd);
        if (pitches.size() > 0){
            int newNoteKey = pitches.get(0);
            System.out.println("INSERT PITCH " + newNoteKey);
            LinkedList<MelodyPart> melodies = phrases.get(newNoteKey);
            if(melodies == null){
                melodies = new LinkedList<MelodyPart>();
                melodies.add(phraseToAdd);
                phrases.put(newNoteKey, melodies);
            } else{
                melodies.add(phraseToAdd);
                phrases.put(newNoteKey, melodies);
            }
        }
    }
    
    /**
     * Searches database of phrases, returns null if none found.
     * @param lastPhrase
     * @return 
     */
    public MelodyPart getNextPhrase(MelodyPart lastPhrase){
        LinkedList<Integer> notes = getNotes(lastPhrase);
        if(notes.size() >= 2){
            MelodyPart nextPhrase;
            int nextNote = getNextNote(notes);
            System.out.println("Searching for pitch" + nextNote);
            nextPhrase = getLastPhrase(nextNote);
            System.out.println("phrases: " + phrases.get(nextNote));
            return nextPhrase;
        }
        else {
            return null;
        }
    }

    public MelodyPart getLastPhrase(int nextNote) {
        LinkedList<MelodyPart> melodies = phrases.get(nextNote);
        if (melodies == null) {
            return null;
        } else if (melodies.isEmpty()) {
            return null;
        } else {
            return melodies.getLast();
        }
    }
    
    public static LinkedList<Integer> getNotes(MelodyPart aMelody){
        ArrayList<Note> notesWithRests = aMelody.getNoteList();
        LinkedList<Integer> notes = new LinkedList<Integer>();
        for(Note n : notesWithRests){
            int pitch = n.getPitch();
            if (pitch != -1){
                notes.add(pitch);
            }
        }
        return notes;
    }
    
    public int getNextNote(LinkedList<Integer> notes){
        return intervals.bestPitch(notes);
    }
}
