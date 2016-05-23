/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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
package imp.lickgen;

import imp.Constants;
import static imp.Constants.REST;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.guidetone.GuideLineGenerator;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.PitchClass;
import imp.data.Unit;
import imp.gui.Notate;
import imp.util.ErrorLog;
import java.util.ArrayList;
import polya.Polylist;
import polya.PolylistEnum;

/**
 *
 * @author Mark Heimann
 */
public class NoteConverter {

    private static final int octave = 12; //number of semitones in an octave
    private static final int rootOffset = 60; //middle C (the MIDI number for middle C is 60, +- n for +- n semitones) for now
    
//    chord families: minor, minor7, major, dominant, half-diminished, diminished, augmented
//    create arrays for each chord family that store the scale degree corresponding to each number of half steps
 
//    Note: I tried to give each scale degree a name that was logical and/or corresponded to common practice.
//    It was less clear, however, to name some pitches (#5 or b13?  2 or 9, 4 or 11, 6 or 13?).
//    Some scales (e.g. diminished, augmented), by virtue of how their accidentals are arranged, also were strange.
//    Thus, there was definitely room for interpretation.
//    However, this nomenclature system should be consistent and thus suitable for its primary use, which is internal.
//    Further explanatory comments provided as needed.
    
    //minor (i.e. melodic minor)
    private static final String[] minorScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "b7", "7"};
    //minor 7 (i.e. Dorian)
    private static final String[] minor7ScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "7", "#7"};
    //major (i.e. Ionian)
    private static final String[] majorScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "5", "#5", "6", "b7", "7"};
    //dominant (i.e. Mixolydian)
    private static final String[] dominantScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "5", "#5", "6", "7", "#7"};
    //half diminished
    //note: 6 half steps above the root has to be b5--otherwise, what do you call a perfect 5? No, I think it should be 5.
    private static final String[] halfDimScaleDegrees = {"1", "2", "#2", "3", "#3", "4", "5", "#5", "6", "#6", "7", "#7"};
    //diminished
    //note: 8 half steps above root is a 6th because a fully diminished 7th is the same intervals as a major 6th
    //so also, nine half steps becomes a seventh
    private static final String[] dimScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "6", "7", "#7", "b8"};
    //augmented (e.g. major#5)
    //here, 7 half steps is called a b5 (because it's flat relative to the augmented fifth) even though the interval is a perfect fifth
    private static final String[] augScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "b5", "5", "6", "b7", "7"};

    /**
     * noteToRelativePitch Conversion of note to relative pitch
     *
     * @param note, an absolute pitch and duration
     * @param chord, the chord that note is over
     * @return the note in relative pitch form
     */
    public static Polylist noteToRelativePitch(Note note, Chord chord) {
        String noteLength = Note.getDurationString(note.getRhythmValue());
        String chordFamily = chord.getFamily(); //whether chord is major, minor, etc.
        Polylist relativeNote = Polylist.nil; //this will be our relative note

        //the root offset transposes notes so they're in a "normal" pitch range, though this probably isn't strictly necessary
        int pitch = note.getPitch() % octave + rootOffset;
        //gives number of semitones the pitch of the root is above a C, plus the offset (which makes it above middle C)
        int root = chord.getRootSemitones() + rootOffset; 
        //make sure the note is at least as high in pitch as the root (otherwise transpose up)
        if (pitch - root < 0) {
            pitch += octave;
        }

        //Part 1 of the note construction: add an X to signify that it's a relative pitch
        relativeNote = relativeNote.addToEnd("X");

        //Part 2 of the note construction: add scale degree
        int pitchOffset = pitch - root; //note: this has been normalized to be between 0 and 11
        if (chord.isNOCHORD()) {
            relativeNote = relativeNote.addToEnd("1");                            
        } else if (chordFamily.equals("minor")) {
            relativeNote = relativeNote.addToEnd(minorScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("minor7")) {
            relativeNote = relativeNote.addToEnd(minor7ScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("major")) {
            relativeNote = relativeNote.addToEnd(majorScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("dominant") 
                || chordFamily.equals("sus4") 
                || chordFamily.equals("alt")) { //treat sus4, alt chords like dominant
            relativeNote = relativeNote.addToEnd(dominantScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("half-diminished")) {
            relativeNote = relativeNote.addToEnd(halfDimScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("diminished")) {
            relativeNote = relativeNote.addToEnd(dimScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("augmented")) {
            relativeNote = relativeNote.addToEnd(augScaleDegrees[pitchOffset]);
        } else {
            relativeNote = relativeNote.addToEnd("0");                         
            ErrorLog.log(ErrorLog.COMMENT, "Unrecognized chord family: " + chordFamily);
        }

        //Part 3 of the note construction: add the rhythm amount
        relativeNote = relativeNote.addToEnd(noteLength);
        return relativeNote;
    }
    
    /**
     * Converts a scale degree to a Note
     * does not specify accidental - fix this
     * @param degree the scale degree to be converted
     * @param chord the chord that the note is over
     * @param octave the desired octave of the note
     * @param rhythmValue the desired rhythm value of the note
     * @return Note corresponding to the given scale degree, null if degree not in chord's scale
     */
    public static Note scaleDegreeToNote(String degree, Chord chord, int octave, int rhythmValue){
        if(chord.isNOCHORD()){
            return new Note(REST, Constants.Accidental.NOTHING, rhythmValue);//Chord is NC, return rest
        }
        PitchClass pc = scaleDegreeToPitchClass(degree, chord);
        if(pc!=null){
            NoteSymbol ns = new NoteSymbol(pc, octave, rhythmValue);
            int midi = ns.getMIDI();
            return new Note(midi, rhythmValue);
        }else{//scale degree could not be found in chord's scale or chord does not have an associated scale
            return highestPriority(chord, rhythmValue);
        }
        
    }
    
    public static Note highestPriority(Chord chord, int duration){
        PolylistEnum priorityList = chord.getPriority().elements();
        Note highestPriority = ((NoteSymbol)priorityList.nextElement()).toNote();
        highestPriority.setRhythmValue(duration);
        return highestPriority;
    }
    
    /**
     * Converts a scale degree to a pitch class, to be used to create a note
     * @param degree the scale degree
     * @param chord the chord that the scale degree corresponds to
     * @return Returns pitch class corresponding to the scale degree and chord,
     * returns pitch class of root if scale degree isn't in the chord's associated scale,
     * returns null if chord is no chord
     */
    public static PitchClass scaleDegreeToPitchClass(String degree, Chord chord){
        if(!chord.isNOCHORD()){
            PitchClass rootPc = chord.getRootPitchClass();
            String [] array = chordToArray(chord);
            if(array!=null){
                int semitonesAboveRoot = indexOf(degree, array);
                if(semitonesAboveRoot!=-1){
                    return PitchClass.transpose(rootPc, semitonesAboveRoot);
                }else{
                    //scale degree not in the scale associated with that chord, return null
                    return null; 
                }
            }else{
                //chord family does not have an associated scale, return null
                return null;
            }
        }
        else{
            //Chord is NC - return null. There is no PitchClass representing a rest, only a pitchClassName, "r", and a pitch, REST (-1)
            return null;
        }
    }
    
    /**
     * Given a scale family, it returns the array with the corresponding scale
     * degree.
     * @param family The type of scale
     * @return The array of the scale degrees
     */
    private static String [] chordToArray(Chord c){
        if(c.isNOCHORD()){
            return null;//chord is no chord, there is no corresponding array
        }
        String family = c.getFamily();
        if(family.equals("minor")){
            return minorScaleDegrees;
        }else if(family.equals("minor7")){
            return minor7ScaleDegrees;
        }else if(family.equals("major")){
            return majorScaleDegrees;
        }else if(family.equals("dominant")
                ||family.equals("sus4")
                ||family.equals("alt")){
            return dominantScaleDegrees;
        }else if(family.equals("half-diminished")){
            return halfDimScaleDegrees;
        }else if(family.equals("diminished")){
            return dimScaleDegrees;
        }else if(family.equals("augmented")){
            return augScaleDegrees;
        }else{
            return null;//chord family is note or bass, return null
        }
    }

    /**
     * Given a scale degree, indexOf returns the index of that scale 
     * degree in a scale
     * @param degree The scale degree
     * @param scale The scale that we are using to get the index value
     * @return index of scale degree in scale array, -1 if not found
     */
    private static int indexOf(String degree, String[]scale){
        int index = -1;
        for(int i=0; i<scale.length; i++){
            if(scale[i].equals(degree)){
                index = i;
            }
        }
        return index;
    }

    //method to test that the conversion works
    /**
     * testConversion Test the conversion process to see if it produces xNote,
     * the note you think it will
     *
     * @param pitch the pitch of a note to be converted
     * @param natural whether or not the note to be converted is natural
     * @param sharp whether or not the note to be converted is sharp or flat
     * @param length the length of the note to be converted
     * @param chordName the name of the corresponding chord
     * @param xNote the String representation of the desired relative pitch note
     * @return true if the conversion works
     */
    public static boolean testConversion(int pitch, boolean natural, boolean sharp, int length, String chordName, String xNote) {
        Note testNote = new Note(pitch, natural, sharp, length);
        Chord testChord = new Chord(chordName);
        Polylist testConvert = noteToRelativePitch(testNote, testChord);
        return testConvert.toString().equals(xNote);
    }

    /**
     * returns a string representation of a relative pitch melody, 
     * given a melody (use this, for example in writing productions)
     * Called once, from LickgenFrame.writeProduction.
     */
    public static String melStringToRelativePitch(int slotsPerSection, ChordPart chordProg, String exactMelody) {
        ArrayList<Chord> allChords = chordProg.getChords();

        //split up the string containing melody info
        String[] exactMelodyData = exactMelody.split(" ");

        StringBuilder relativePitchMelody = new StringBuilder();

        //first item is tells us the starting slot of this section of melody
        //int startSlot = Integer.parseInt(exactMelodyData[0]);

        //index of the i-th chord in this measure we've looked at as a possible match for this note
        int chordNumber = 0; 
        //total number of slots belonging to chords we've looked at as a possible match for this note
        int totalChordDurationInMelody = allChords.get(0).getRhythmValue(); 
        int totalNoteDurationInMelody = 0; //total number of slots that have gone by in this measure up to this note
        for (int i = 1; i < exactMelodyData.length; i += 2) {
            //System.out.println("exactMelodyData[" + i + "]: " + exactMelodyData[i] + " " + exactMelodyData[i+1]);
            int pitch = Integer.parseInt(exactMelodyData[i]); //every odd index item is a note
            int duration = Integer.parseInt(exactMelodyData[i + 1]); //every even index item (after 0) is a duration
            try {
                while (totalNoteDurationInMelody >= totalChordDurationInMelody) { //we need to move on to the next chord
                    chordNumber++;
                    totalChordDurationInMelody += allChords.get(chordNumber).getRhythmValue();
                } 
            } catch (Exception e) {
                System.out.println("Exception when matching notes to chords: " + e.toString());
                System.out.println("Exact melody: " + exactMelody);
                System.out.println("\n");
            }
            
            try {
                if (pitch >= 0) { //pitch is a note
                    Note note = new Note(pitch, duration);
                    Chord chord = allChords.get(chordNumber);
                    Polylist relativePitch = noteToRelativePitch(note, chord);
                    if (relativePitch == null) {
                        System.out.println("*** Internal error: relativePitch is null at note = "
                                + note + ", chord = " + chord);
                      }
                    else {
                      relativePitchMelody.append(relativePitch.toString());
                      }
                } else { //"pitch" is a rest
                    String rest = " R" + Note.getDurationString(duration) + " ";
                    relativePitchMelody.append(rest.toString());
                }
            } catch (Exception e) {
                System.out.println("Problem processing note: " + e.toString());
            }
            totalNoteDurationInMelody += duration;
        }
        return relativePitchMelody.toString();
    }
    
     /**
     * melPartToRelativePitch 
     * Convert a MelodyPart to a sequence of relative pitches
     *
     * @param melPart the melody part that needs to be converted
     * @param chordPart the corresponding chords
     */
    public static String melPartToRelativePitch(MelodyPart melPart, ChordPart chordPart) {
        StringBuilder relMel = new StringBuilder();
        int totalDuration = 0;
        int melodySize = melPart.getSize();
        while (totalDuration < melodySize) {
            Note note = melPart.getNote(totalDuration);
            if (note.getPitch() >= 0) { //"note" is actually a note
                Chord chord = chordPart.getCurrentChord(totalDuration);
                Polylist relativePitch = noteToRelativePitch(note, chord);
                relMel.append(relativePitch.toString());
            } else { //"note" is a rest
                String rest = " R" + Note.getDurationString(note.getRhythmValue()) + " ";
                relMel.append(rest.toString());
            }
            totalDuration += note.getRhythmValue();
        }
        return relMel.toString();
    }

    /**
     * Convert a MelodyPart over a ChordPart starting at a certain slot into
     * a relative-pitch melody. This is cumbersome, but was done after NoteConverter
     * was built. A lot of trouble could be avoided by not using strings.
     * Note that the chordSlot applies only to the ChordPart.
     * The melody is assumed to start in slot 0.
     * Added by Bob Keller 12 August 2014.
     * 
     * @param melody
     * @param chords
     * @param chordSlot 
     */
    public static Polylist melodyPart2Relative(MelodyPart melody, ChordPart chordProg, int chordSlot)
      {
       ArrayList<Integer> melodyData = melody.getArrayListForm();
       return melodyPart2Relative(melodyData, chordProg, chordSlot);
      }
    
     public static Polylist melodyPart2Relative(ArrayList<Integer> melodyData, ChordPart chordProg, int chordSlot)
      {
       int slotsPerSection = 480; // FIX: This is not universal
       StringBuilder buffer = new StringBuilder();
        // Note: Blank is critical in next statement.
        buffer.append(chordSlot);
        buffer.append(" ");
        for( Integer s: melodyData )
          {
            buffer.append(s);
            buffer.append(" ");
          }
        String melString = buffer.toString();
        //System.out.println("melString = " + melString);
        String relativePitchMelody = NoteConverter.melStringToRelativePitch(slotsPerSection, chordProg, melString);
        return Polylist.PolylistFromString(relativePitchMelody);
      }
    
      public static Polylist melodyPart2RelativeString(ArrayList<String> melodyData, ChordPart chordProg, int chordSlot)
      {
       int slotsPerSection = 480; // FIX: This is not universal
       StringBuilder buffer = new StringBuilder();
        // Note: Blank is critical in next statement.

        for( String s: melodyData )
          {
            buffer.append(s);
            buffer.append(" ");
          }
        String melString = buffer.toString();
        System.out.println("melString = " + melString);
        String relativePitchMelody = NoteConverter.melStringToRelativePitch(slotsPerSection, chordProg, melString);
        return Polylist.PolylistFromString(relativePitchMelody);
      }
         
    
     /**
     * noteToAbstract 
     * Convert a note in a tune to an abstract pitch
     *
     * @param noteIndex the index of the note in the tune that needs to be converted
     * @param notate used to get all the tune's information
     */
    public static Polylist noteToAbstract(int noteIndex, Notate notate) {
        //get type of note
        ChordPart chordProg = notate.getChordProg();
        LickGen lickgen = notate.getLickGen();
        MelodyPart part = notate.getCurrentMelodyPart().copy();
        Note note = part.getNote(noteIndex);
        Polylist rhythmString = Polylist.nil;
        StringBuilder sb = new StringBuilder();
        int value = note.getDurationString(sb, note.getRhythmValue()); //originally passed sb in as well
        int pitch = note.getPitch();
        if (note.isRest()) {
            rhythmString = rhythmString.addToEnd("R" + sb.substring(1));
        } else {
            //add pitch to notes
            //get note type
            char notetype;
            
            int[] notetone = lickgen.getNoteTypes(noteIndex, pitch, pitch,
                    chordProg);
            switch (notetone[0]) {
                case LickGen.CHORD:
                    notetype = 'C';
                    break;
                case LickGen.COLOR:
                    notetype = 'L';
                    break;
                default:
                    notetype = 'X';
                    break;
            }
            Note nextNote = part.getNextNote(noteIndex);
            if (notetype == 'X' && nextNote != null) {

                int nextPitch = nextNote.getPitch();
                int nextIndex = part.getNextIndex(noteIndex);
                if (nextIndex <= noteIndex) {
                    int pitchdiff = nextPitch - pitch;
                    if (Math.abs(pitchdiff) == 1) {
                        notetype = 'A';
                    }
                }
            }
            rhythmString = rhythmString.addToEnd(notetype + sb.substring(1));
        }
        return rhythmString;
    }

     /**
     * melodyToAbstract 
     * Convert a MelodyPart to abstract melody
     *
     * @param melPart the melody that will be converted
     * @param chordPart the corresponding chords
     * @param isSongStart if this MelodyPart is from the start of the song
     * @param notate we set its status
     * @param lickgen to get note types, etc.
     */
    public static String melodyToAbstract(MelodyPart melPart, ChordPart chordPart, boolean isSongStart, LickGen lickgen) {
        if (melPart.melodyIsEmpty(0, melPart.getSize())) {
            StringBuilder sb = new StringBuilder();
            Note n = new Note(72, 1);
            n.getDurationString(sb, melPart.getSize());
            String returnString = "((slope 0 0 R" + sb.substring(1) + "))";
            if (isSongStart) {
                returnString = returnString.concat("STARTER");
            }
            return returnString;
        }

        int current = 0;

        Polylist rhythmString = new Polylist();

        //pitches of notes in measure not including rests
        ArrayList<Integer> notes = new ArrayList<Integer>();

        boolean tiedAtStart = false, tiedAtEnd = false;

        //untie first note if it is tied from last measure
        Note prevNote = melPart.getPrevNote(current);
        int prevIndex = melPart.getPrevIndex(current);
        int melSize = melPart.getSize();
        if (prevNote != null && prevNote.getRhythmValue() > current - prevIndex) {

            tiedAtStart = true;
            //untie and set the previous note
            Note untiedNote = prevNote.copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = melSize - prevIndex % melSize;
            untiedNote.setRhythmValue(rhythmVal);
            melPart.setNote(prevIndex, untiedNote);

            //set the current note
            rhythmVal = originalRhythmVal - rhythmVal;
            Note currNote = prevNote.copy();
            currNote.setRhythmValue(rhythmVal);
            melPart.setNote(current, currNote);
        }
        Note lastNote = melPart.getPrevNote(melSize);
        int lastIndex = melPart.getPrevIndex(melSize);
        if (lastNote != null) {
            //untie notes at end of measure and beginning of next measure
            if (lastNote.getRhythmValue() > melSize - lastIndex) {
                tiedAtEnd = true;
                int tracker = lastIndex;
                Note trackerNote = melPart.getNote(tracker);
                Note untiedNote = trackerNote.copy();
                int originalRhythmVal = untiedNote.getRhythmValue();
                int rhythmVal = melSize - (tracker % melSize);
                untiedNote.setRhythmValue(rhythmVal);
                melPart.setNote(tracker, untiedNote);
                int secondRhythmVal = originalRhythmVal - rhythmVal;
                untiedNote = trackerNote.copy();
                untiedNote.setRhythmValue(secondRhythmVal);
                melPart.setNote(melSize, untiedNote);
            }
        }

        Note firstNote = melPart.getPrevNote(1);
        int firstIndex = melPart.getPrevIndex(1);
        if (firstNote != null) {
            if ((firstIndex != 0) && !(firstNote.isRest())) {
                return null;
            }
        }

        while (current < melPart.getSize()) {
            
            //if null note, make it a rest
            if (melPart.getNote(current) == null) {
                int next = melPart.getNextIndex(current);
                Note n = Note.makeRest(next - current);
                melPart.setNote(current, n);
            }

            StringBuilder sb = new StringBuilder();
            Note currentNote = melPart.getNote(current);


            int value = currentNote.getDurationString(sb, currentNote.getRhythmValue());

            int pitch = currentNote.getPitch();

            int rhythm = 0;



            if (currentNote.isRest()) {
                rhythmString = rhythmString.cons("R" + sb.substring(1));
            } else {

                //add pitch to notes
                notes.add(pitch);
                //get note type
                char notetype;
                int[] notetone = lickgen.getNoteTypes(current, pitch, pitch, chordPart); 
                switch (notetone[0]) {
                    case LickGen.CHORD:
                        notetype = 'C';
                        break;
                    case LickGen.COLOR:
                        notetype = 'L';
                        break;
                    default:
                        notetype = 'X';
                        break;
                }
                Note nextNote = melPart.getNextNote(current);
                if (notetype == 'X' && melPart.getNextNote(current) != null) {

                    int nextPitch = nextNote.getPitch();
                    int nextIndex = melPart.getNextIndex(current);
                    if (nextIndex <= melPart.getSize()) {
                        int pitchdiff = nextPitch - pitch;
                        if (Math.abs(pitchdiff) == 1) {
                            notetype = 'A';
                        }
                    }
                }
                rhythmString = rhythmString.cons(notetype + sb.substring(1));
            }


            current = melPart.getNextIndex(current);

        }

        rhythmString = rhythmString.reverse();

        //process intervals
        ArrayList<Integer> intervals = new ArrayList<Integer>();
        intervals.add(0);
        for (int i = 1; i < notes.size(); i++) {
            intervals.add(notes.get(i) - notes.get(i - 1));
        }

        //process slopes
        ArrayList<int[]> slopes = new ArrayList<int[]>();
        int[] slope = new int[3];
        int tracker = 0;


        //get the slope from the note before this section to the first note in the measure
        prevIndex = melPart.getPrevIndex(0);
        lastNote = melPart.getNote(prevIndex);
        while (lastNote != null && lastNote.isRest()) {
            prevIndex = melPart.getPrevIndex(prevIndex);
            lastNote = melPart.getNote(prevIndex);
        }
        int lastpitch = 0;
        if (lastNote != null && !lastNote.isRest()) {
            lastpitch = lastNote.getPitch();
        }
        int pitch = notes.get(0);
        int pitchChange;
        if (lastpitch == 0) {
            pitchChange = 0;
        } else {
            pitchChange = pitch - lastpitch;
        }
        int minPitchChange = 0, maxPitchChange = 0;
        //avoid random notes and repeated notes
        if (pitchChange != 0) {
            if (pitchChange == 1) {
                minPitchChange = 1;
                maxPitchChange = 2;
            } else if (pitchChange == -1) {
                minPitchChange = -2;
                maxPitchChange = -1;
            } else {
                minPitchChange = pitchChange - 1;
                maxPitchChange = pitchChange + 1;
            }
        }

        //if there is only 1 note, return it with its slope
        if (intervals.size() <= 1) {

            String rhythm = rhythmString.toString();
            rhythm = rhythm.substring(1, rhythm.length() - 1);

            //handle case of only 1 note
            if (rhythm.equals("")) {
                char thisPitch = lickgen.getNoteType(0, notes.get(0), notes.get(0), chordPart);
                String len = Note.getDurationString(melPart.getSize());
                rhythm = thisPitch + len;
            }
            String returnString =
                    "((slope " + minPitchChange + " " + maxPitchChange + " " + rhythm + "))";
            if (isSongStart) {
                returnString = returnString.concat("STARTER");
            }
            if (tiedAtEnd) {
                returnString = returnString.concat(" ENDTIED");
            }
            if (tiedAtStart) {
                returnString = returnString.concat(" STARTTIED");
            }
            return returnString;
        }

        for (int i = 0; i < intervals.size(); i++) {
            tracker = i;
            if (intervals.get(i) != 0) {
                i = intervals.size();
            }
        }


        //direction is -1 if slope is going down, 0 for repeated note, 1 for up
        int direction = 0;
        if (intervals.get(tracker) > 0) {
            direction = 1;
        } else if (intervals.get(tracker) < 0) {
            direction = -1;
        }
        //initialize stuff - first note is in its own slope
        slope[0] = minPitchChange;
        slope[1] = maxPitchChange;
        slope[2] = 1;
        slopes.add(slope.clone());

        slope[0] = intervals.get(1);
        slope[1] = intervals.get(1);
        slope[2] = 0;
        for (int i = 1; i < intervals.size(); i++) {
            //slope was going up but not any more
            if (direction == 1 && intervals.get(i) <= 0) {
                if (intervals.get(i) == 0) {
                    direction = 0;
                } else {
                    direction = -1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }

                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was going down but not any more
            } else if (direction == -1 && intervals.get(i) >= 0) {
                if (intervals.get(i) == 0) {
                    direction = 0;
                } else {
                    direction = 1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was 0 but not any more
            } else if (direction == 0 && intervals.get(i) != 0) {
                if (intervals.get(i) > 0) {
                    direction = 1;
                } else {
                    direction = -1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
            } else {
                slope[2]++;
                if (intervals.get(i) > slope[1]) {
                    slope[1] = intervals.get(i);
                }
                if (intervals.get(i) < slope[0]) {
                    slope[0] = intervals.get(i);
                }
            }

            if (i == intervals.size() - 1) {
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
            }
        }

        //add in slopes
        StringBuilder strbuf = new StringBuilder();
        strbuf.append("(");
        Polylist tempString = rhythmString;
        for (int i = 0; i < slopes.size(); i++) {
            slope = slopes.get(i);
            strbuf.append("(slope ");
            strbuf.append(slope[0]);
            strbuf.append(" ");
            strbuf.append(slope[1]);
            strbuf.append(" ");

            int j = 0;
            //get all of notes if last slope
            if (i == slopes.size() - 1) {
                while (tempString.nonEmpty()) {
                    strbuf.append(tempString.first().toString());
                    strbuf.append(" ");
                    tempString = tempString.rest();
                }
            } else {
                while (j < slope[2]) {
                    String temp = tempString.first().toString();
                    strbuf.append(temp);
                    strbuf.append(" ");
                    tempString = tempString.rest();
                    if (temp.charAt(0) != 'R') {
                        j++;
                    }
                }
            }
            strbuf.deleteCharAt(strbuf.length() - 1);
            strbuf.append(")");
        }
        strbuf.append(")");
        {
            //Mark measure as 'songStarter' if it is the first of a song
            if (isSongStart) {
                strbuf.append("STARTER");
            }
            strbuf.append("CHORDS ");

            ChordPart chords = chordPart.extract(0, chordPart.getSize());
            ArrayList<Unit> chordList = chords.getUnitList();
            if (chordList.isEmpty()) {
                System.out.println("No chords");
            }
            for (int i = 0; i < chordList.size(); i++) {
                String nextChord = ((Chord) chordList.get(i)).toLeadsheet();
                strbuf.append(nextChord);
                strbuf.append(" ");
            }

            return strbuf.toString();
        }
    }
}
