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

package imp.lstm.io.leadsheet;

import polya.Tokenizer;
import polya.EOF;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import imp.lstm.encoding.EncodingParameters;
import imp.lstm.encoding.NoteEncoder;
import imp.lstm.encoding.ChordEncoder;
import imp.data.Note;
import imp.data.NoteSymbol;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mikera.vectorz.Vector;
import mikera.vectorz.AVector;


/**
 *
 * @author cssummer16
 */
public class LeadSheetIO {
    public static LeadSheetDataSequence readLeadSheet(File filename) {
        return readLeadSheet(filename.getAbsolutePath());
    }

    public static LeadSheetDataSequence readLeadSheet(String filename) {
        NoteSymbol[] currMelody = readLeadSheetMelody(filename);
        ChordInfo[] currChords = LeadSheetIO.readLeadSheetChords(filename);
        
        LeadSheetDataSequence sequence = new LeadSheetDataSequence();
        NoteEncoder noteEncoder = EncodingParameters.noteEncoder;
        int noteSteps = 0;
        for(NoteSymbol note : currMelody)
        {
            if(note.isRest())
            {
                AVector encoding = noteEncoder.encode(note.getMIDI());
                for(int remaining = (note.getDuration() /Constants.RESOLUTION_SCALAR); remaining > 0 ; remaining--) {
                    sequence.pushStep(null, null, encoding);
                    noteSteps++;
                }
            }
            else
            {
                noteSteps++;
                sequence.pushStep(null, null, noteEncoder.encode(note.getMIDI()));
                for(int remaining = (note.getDuration() /Constants.RESOLUTION_SCALAR) - 1; remaining > 0 ; remaining--) {
                    sequence.pushStep(null, null, noteEncoder.encode(noteEncoder.getSustainKey()));
                    noteSteps++;
                }
            }
        }
        int chordSteps = 0;
        ChordEncoder chordEncoder = EncodingParameters.chordEncoder;
        for(ChordInfo chord : currChords)
        {
            //System.out.println(chord.getRoot() + chord.getType());
            //System.out.println(chord.getDuration());
            AVector chordData = chordEncoder.encode(chord.getRoot(), chord.getType(), chord.getBass());
            if(chordData == null)
            {
                //System.out.println(chord.getType());
            }
            //System.out.println(chordData);
            for(int remaining = chord.getDuration(); remaining > 0; remaining--) {
                chordSteps++;
                sequence.pushStep(null, chordData.copy(), null);
            }
        }
        //System.out.println("Note steps: " + noteSteps + " Chord steps: " + chordSteps);
        
        for(int timeStep = 0; timeStep < noteSteps; timeStep++)
        {
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
        
        if(sequence.isBalanced())
            return sequence;
        else
            throw new RuntimeException("Dude...the chord, beat, and note sequence lengths don't match");
    }
    
    public static NoteSymbol[] readLeadSheetMelody(File file) {
        return readLeadSheetMelody(file.getAbsolutePath());
    }
    
    public static NoteSymbol[] readLeadSheetMelody(String filename) {
        ArrayList<NoteSymbol> melody = new ArrayList<NoteSymbol>();
        try {
            Tokenizer tokenizer = new Tokenizer(new FileInputStream(filename));
            Object temp;
            while( (temp = tokenizer.nextSexp()) != Tokenizer.eof ){
                if(temp instanceof String){ 
                    String noteString = (String)temp;
                    char firstChar = noteString.charAt(0);
                    if(!Character.isLowerCase(firstChar)){
                        continue;
                    }
                    NoteSymbol noteSymbol = NoteSymbol.makeNoteSymbol(noteString);
                    // if it was an invalid note, there is no reason to add it to the melody
                    if(noteSymbol == null){
                        continue;
                    }
                    melody.add(noteSymbol);          
                }        
            } // end of EOF while
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        NoteSymbol[] notes = new NoteSymbol[melody.size()];
        return melody.toArray(notes);
    } // end of method parseLeadSheet

    public static ChordInfo[] readLeadSheetChords(File file) {
        return LeadSheetIO.readLeadSheetChords(file.getAbsolutePath());
    }
    
    //TODO: make this support more than 8th note resolution files
    public static ChordInfo[] readLeadSheetChords(String filename) {

        ArrayList<ChordInfo> chords = new ArrayList<ChordInfo>();
        ArrayList<ChordInfo> partialChordList = new ArrayList<ChordInfo>();
        ChordInfo lastChord = null;
        Pattern p = Pattern.compile("([A-G](?:#|b)?)([^/]*)(?:/(.+))?");
        try {
            Tokenizer tokenizer = new Tokenizer(new FileInputStream(filename));
            Object temp = tokenizer.nextSexp();
            while( !(temp instanceof EOF) ){
                if(temp instanceof String){
                    String strToken = (String)temp;
                    char firstChar = strToken.charAt(0);
                    if (Character.isUpperCase(firstChar)) { //Check for chord symbols   
                        ChordInfo chord;
                        if(strToken.equals("NC")) {
                            chord = new ChordInfo(0,"NC","NC");
                        } else {
                            Matcher m = p.matcher(strToken);
                            if(m.matches()){
                                String root = m.group(1);
                                String type = m.group(2);
                                String slash_bass = m.group(3);
                                chord = new ChordInfo(0,root,type,slash_bass);
                            } else {
                                throw new RuntimeException("Malformed chord symbol " + strToken);
                            }
                        }
                        lastChord = chord;
                        partialChordList.add(chord);
                    }else if(strToken.equals("/")) {
                        partialChordList.add(lastChord);
                    }
                    if(strToken.equals("|")) {
                        int numChords = partialChordList.size();
                        //Only supports certain even chord divisions, i.e.  Constants.WHOLE/(numChords*Constants.RESOLUTION_SCALAR)
                        //must be a whole number
                        for (int j = 0; j < numChords; j++) {
                            partialChordList.get(j).setDuration(Constants.WHOLE/(numChords*Constants.RESOLUTION_SCALAR));     
                        }
                        chords.addAll(partialChordList);
                        partialChordList.clear();
                    }
                }
                temp = tokenizer.nextSexp();
            } // end of EOF while
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        ChordInfo[] chordArray = new ChordInfo[chords.size()];
        return chords.toArray(chordArray);
    } 
    
    public static int writtenChordLength;
    
    public static void writeLeadSheet(LeadSheetDataSequence data, String filename, String songTitle)
    {
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(filename)));
            outputWriter.write("(title " + songTitle + ")");
            outputWriter.newLine();
            //set style to ska because it is style bae $wag
            outputWriter.write("(tempo " + data.getTempo() + ")");
            outputWriter.newLine();
            outputWriter.write("(section (style " + data.getStyle() + "))");
            outputWriter.newLine();
            outputWriter.write("(part ");
            outputWriter.newLine();
            outputWriter.write("(type melody)");
            outputWriter.newLine();
            outputWriter.write("(instrument 57)");
            outputWriter.newLine();
            outputWriter.write(")");
            outputWriter.newLine();
            if(data.hasMelodyLeft()) {
                NoteEncoder noteEncoder = EncodingParameters.noteEncoder;
                int noteValue = 0;  //The variable to keep track of the current note's midi value
                AVector firstMelodyStep = data.pollMelody();
                //for(int i = 0; i < firstMelodyStep.length(); i++)
                        //System.out.print(firstMelodyStep.get(i) + " ");
                    //System.out.println();
                if(noteEncoder.hasSustain(firstMelodyStep)) {
                    System.err.println("ERROR: first beat of bit-vector sustained");
                    noteValue = -1;
                }
                else {
                    noteValue = noteEncoder.decode(firstMelodyStep);
                }
                int duration = 1;
                while(data.hasMelodyLeft()) {
                    AVector nextStep = data.pollMelody();
                    /*for(int i = 0; i < nextStep.length(); i++)
                        System.out.print(nextStep.get(i) + " ");
                    System.out.println("<- generated melody step");
                    if(noteEncoder.hasSustain(nextStep))
                        System.out.println("sustain");*/
                    if(noteEncoder.hasSustain(nextStep) || ((noteValue == -1) && noteEncoder.decode(nextStep) == -1))
                    {
                        //System.out.println("adding to duration for " + noteValue);
                        duration++;
                    }
                    else {
                        //System.out.println(noteValue + " is note value that is ending!");
                        Note note;
                        if(noteValue == -1)
                            note = Note.makeRest(duration * Constants.RESOLUTION_SCALAR); //construct a LeadSheet Note from the the midiValue and duation in timeSteps
                        else
                            note = new Note(noteValue, duration * Constants.RESOLUTION_SCALAR);
                        outputWriter.write(note.toLeadsheet() + " ");
                        noteValue = noteEncoder.decode(nextStep);
                        //System.out.println(noteValue + " is new noteValue!");
                        duration = 1;
                    }  
                }
                Note note;
                if(noteValue == -1)
                    note = Note.makeRest(duration * Constants.RESOLUTION_SCALAR); //construct a LeadSheet Note from the the midiValue and duation in timeSteps
                else
                    note = new Note(noteValue, duration * Constants.RESOLUTION_SCALAR);
                outputWriter.write(note.toLeadsheet() + " ");
            }
            else {
                throw new RuntimeException("Umm...there was no melody data in the data sequence you were writing from...");
            }
            outputWriter.newLine();
            int chordLength = 0;
            writtenChordLength = 0;
            if(data.hasChordsLeft()) {
                ChordEncoder chordEncoder = EncodingParameters.chordEncoder;
                
                String lastChordName = chordEncoder.decode(data.pollChords());
                //System.out.println(lastChordName);
                String chordName = lastChordName;
                int currMeasureDuration = 0;
                int duration = 1;
                chordLength++;
                while(data.hasChordsLeft()) {
                    chordLength++;
                    chordName = chordEncoder.decode(data.pollChords());
                    //System.out.println(chordName);
                    if(chordName.equals(lastChordName))
                    {
                        duration++;
                    }
                    else {
                        
                        currMeasureDuration += duration * Constants.RESOLUTION_SCALAR;
                        currMeasureDuration = writeChordString(lastChordName, currMeasureDuration, outputWriter);
                        lastChordName = chordName;
                        duration = 1;
                    }
                }
                currMeasureDuration += duration * Constants.RESOLUTION_SCALAR;
                currMeasureDuration = writeChordString(chordName, currMeasureDuration, outputWriter);

                
            }
            else {
                throw new RuntimeException("Umm...there was no chord data in the data sequence you were writing from...");
            }
            outputWriter.close();
       } catch(Exception e) {
           e.printStackTrace();
       }
    }
    
    public static int writeChordString(String chordName, int currMeasureDuration, BufferedWriter outputWriter)
    {
        try {
                        //currMeasureDuration is expressed in full resolution so as to be a direct comparison with Constants.WHOLE
                        if(currMeasureDuration < Constants.WHOLE) {
                            outputWriter.write(chordName + " ");
                            writtenChordLength += currMeasureDuration / Constants.RESOLUTION_SCALAR;
                        }
                        else if(currMeasureDuration == Constants.WHOLE) {
                            outputWriter.write(chordName + " | ");
                            currMeasureDuration = 0;
                            writtenChordLength += 48;
                        }
                        else {
                            outputWriter.write(chordName + " | ");
                            currMeasureDuration -= Constants.WHOLE;
                            writtenChordLength += 48;
                            while(currMeasureDuration >= Constants.WHOLE) {
                                outputWriter.write("/ | ");
                                currMeasureDuration -= Constants.WHOLE;
                                writtenChordLength += 48;
                            }
                            if(currMeasureDuration > 0) {
                                outputWriter.write("/ ");
                                writtenChordLength += currMeasureDuration / Constants.RESOLUTION_SCALAR;
                            }
                        }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return currMeasureDuration;
    }
}
