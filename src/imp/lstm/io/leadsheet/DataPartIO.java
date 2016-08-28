/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.io.leadsheet;

import imp.lstm.encoding.ChordEncoder;
import imp.lstm.encoding.EncodingParameters;
import imp.lstm.encoding.NoteEncoder;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.Key;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.PitchClass;
import java.util.ArrayList;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
/**
 *
 * @author cssummer16
 */
public class DataPartIO {
    
    public static LeadSheetDataSequence readChords(ChordPart chords) {
        return readChords(chords, 0);
    }
    public static LeadSheetDataSequence readChords(ChordPart chords, int beat_timestep_start) {
        MelodyPart restPart = new MelodyPart();
        restPart.addNote(Note.makeRest(chords.size()));
        return readParts(chords, restPart, beat_timestep_start);
    }
    
    public static LeadSheetDataSequence readJustChords(ChordPart chords, int beat_timestep_start) {
        return readParts(chords, null, beat_timestep_start);
    }
    
    public static LeadSheetDataSequence readParts(ChordPart chords, MelodyPart melody) {
        return readParts(chords, melody, 0);
    }
    public static LeadSheetDataSequence readParts(ChordPart chords, MelodyPart melody, int slice_start, int slice_end) {
        return readParts(chords.extract(slice_start, slice_end), melody.extract(slice_start, slice_end, true, true), slice_start/Constants.RESOLUTION_SCALAR);
    }
    public static LeadSheetDataSequence readParts(ChordPart chords, MelodyPart melody, int beat_timestep_start) {
        LeadSheetDataSequence sequence = new LeadSheetDataSequence();
        NoteEncoder noteEncoder = EncodingParameters.noteEncoder;
        
        int noteSteps = 0;
        if(melody != null) {
            for(Note note : melody.getNoteList()) {
                if(note.isRest())
                {
                    AVector encoding = noteEncoder.encode(-1);
                    for(int remaining = (note.getRhythmValue()/Constants.RESOLUTION_SCALAR); remaining > 0 ; remaining--) {
                        sequence.pushStep(null, null, encoding);
                        noteSteps++;
                    }
                }
                else
                {
                    noteSteps++;
                    sequence.pushStep(null, null, noteEncoder.encode(note.getPitch()));
                    for(int remaining = (note.getRhythmValue() /Constants.RESOLUTION_SCALAR) - 1; remaining > 0 ; remaining--) {
                        sequence.pushStep(null, null, noteEncoder.encode(noteEncoder.getSustainKey()));
                        noteSteps++;
                    }
                }
            }
        }
        
        int chordSteps = 0;
        ChordEncoder chordEncoder = EncodingParameters.chordEncoder;
        for(Chord chord : chords.getChords())
        {
            String root = PitchClass.upperCaseNote(chord.getRoot());
            String bass = PitchClass.upperCaseNote(chord.getChordSymbol().getBass().toString());
            boolean[] spellVec;
            if(chord.isNOCHORD()){
                spellVec = new boolean[12];
            } else {
                spellVec = chord.getChordForm().getSpellVector("c", Key.Ckey);
            }
            AVector typeData = Vector.createLength(spellVec.length);
            for(int i=0; i<spellVec.length; i++){
                if(spellVec[i])
                    typeData.set(i, 1.0);
            }
            AVector chordData = chordEncoder.encode(root, typeData, bass);
            for(int remaining = chord.getRhythmValue()/Constants.RESOLUTION_SCALAR; remaining > 0; remaining--) {
                chordSteps++;
                sequence.pushStep(null, chordData.copy(), null);
            }
        }
//        System.out.println("Note steps: " + noteSteps + " Chord steps: " + chordSteps);
        
        int numSteps = (melody != null) ? noteSteps : chordSteps;
        //System.out.println(numSteps);
        for(int i = 0; i < numSteps; i++)
        {
            int timeStep = beat_timestep_start + i;
            AVector beat = Vector.createLength(9);
            if(timeStep % (Constants.WHOLE / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(0, 1.0);
            if(timeStep % (Constants.HALF / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(1, 1.0);
            if(timeStep % (Constants.QUARTER / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(2, 1.0);
            if(timeStep % (Constants.EIGHTH / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(3, 1.0);
            if(timeStep % (Constants.SIXTEENTH / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(4, 1.0);
            if(timeStep % (Constants.HALF_TRIPLET / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(5, 1.0);
            if(timeStep % (Constants.QUARTER_TRIPLET / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(6, 1.0);
            if(timeStep % (Constants.EIGHTH_TRIPLET / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(7, 1.0);
            if(timeStep % (Constants.SIXTEENTH_TRIPLET / Constants.RESOLUTION_SCALAR) == 0)
                beat.set(8, 1.0);
            sequence.pushStep(beat, null, null);    
        }
        
        return sequence;
    }
    
    public static MelodyPart getMelodyPart(LeadSheetDataSequence seq) {
        MelodyPart part = new MelodyPart();
        addToMelodyPart(seq, part);
        return part;
    }
    
    public static void addToMelodyPart(LeadSheetDataSequence data, MelodyPart dest) {
        if (data.hasMelodyLeft()) {
            NoteEncoder noteEncoder = EncodingParameters.noteEncoder;
            int noteValue = 0;  //The variable to keep track of the current note's midi value
            AVector firstMelodyStep = data.pollMelody();
            if (noteEncoder.hasSustain(firstMelodyStep)) {
                System.err.println("ERROR: first beat of bit-vector sustained");
                noteValue = -1;
            } else {
                noteValue = noteEncoder.decode(firstMelodyStep);
            }
            int duration = 1;
            while (data.hasMelodyLeft()) {
                AVector nextStep = data.pollMelody();
                if (noteEncoder.hasSustain(nextStep) || ((noteValue == -1) && noteEncoder.decode(nextStep) == -1)) {
                    duration++;
                } else {
                    Note note;
                    if (noteValue == -1) {
                        note = Note.makeRest(duration * Constants.RESOLUTION_SCALAR); //construct a LeadSheet Note from the the midiValue and duation in timeSteps
                    } else {
                        note = new Note(noteValue, duration * Constants.RESOLUTION_SCALAR);
                    }
                    dest.addNote(note);
                    // Set up this new note
                    noteValue = noteEncoder.decode(nextStep);
                    duration = 1;
                }
            }
            Note note;
            if (noteValue == -1) {
                note = Note.makeRest(duration * Constants.RESOLUTION_SCALAR); //construct a LeadSheet Note from the the midiValue and duation in timeSteps
            } else {
                note = new Note(noteValue, duration * Constants.RESOLUTION_SCALAR);
            }
            dest.addNote(note);
        } else {
            throw new RuntimeException("There was no melody data in the data sequence you were writing from!");
        }
    }
}
