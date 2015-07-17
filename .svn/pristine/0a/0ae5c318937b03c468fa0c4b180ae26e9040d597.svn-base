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

import imp.Constants;
import imp.ImproVisor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import polya.Polylist;

/**
 * Extracts chords from a MIDI input, given bass and chord channels
 * @author Kevin Choi Summer 2012
 */

public class ChordExtract implements Constants{
    
    private final static String[] chordNames = initializeChordNames();
    private final static boolean[][] chordBitMaps = initializeChordBitMaps();
    private final static String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private final static int importResolution = THIRTYSECOND_TRIPLET;
    private final static int slotResolution = EIGHTH;
    private final static int FIFTH = 7;
    private final static int DIMFIFTH = 6;
    private final static int AUGFIFTH = 8;
    
    private int chordResolution;
    private int bassChannel;
    private int chordChannel;
    private String chordFile;
    
    /**
     * Initializes a list of all chord names from the chords Polylist
     * @return String[]     the list of all chord names
     */
    private static String[] initializeChordNames()
    {
        //initialize a list of all chords names
        Polylist polylistOfChordNames = ImproVisor.getChordNames();
        polylistOfChordNames = polylistOfChordNames.reverse();
        String[] listOfChordNames = (String[]) polylistOfChordNames.toStringArray();
        return listOfChordNames;
    }
    
    /**
     * Initializes a list of all chords in bitmap form
     * @return boolean[][]      the list of all chord bitmaps
     */
    private static boolean[][] initializeChordBitMaps()
    {
        //initialize a list of chord bitmaps
        boolean[][] listOfChordBitMaps = new boolean[chordNames.length][OCTAVE];
        for (int i = 0; i < chordNames.length; i++)
        {
            String chord = chordNames[i];
            ChordSymbol chordsymbol = ChordSymbol.makeChordSymbol(chord);
            ChordForm chordform = chordsymbol.getChordForm();
            Key key = chordform.getKey();
            boolean[] chordBitMap = chordform.getSpellVector(CROOT, key);
            listOfChordBitMaps[i] = chordBitMap;
        }
        return listOfChordBitMaps;
    }
    
    /**
     * ChordExtract initializes the midi file name, bass channel number, and chord channel number
     * @param chordFileName     file path to the midi file
     * @param bassChannelNum        midi channel number from 0-15; channel number -1 means no channel selected
     * @param chordChannelNum       midi channel number from 0-15; channel number -1 means no channel selected
     */
    public ChordExtract(String chordFileName, int resolution, int bassChannelNum, int chordChannelNum)
    {
        chordFile = chordFileName;
        bassChannel = bassChannelNum;
        chordChannel = chordChannelNum;
        chordResolution = resolution;
        // for debugging
        /*
        System.out.println("Chord file name: " + chordFile);
        System.out.println("Bass channel number: " + bassChannel);
        System.out.println("Chord channel number: " + chordChannel);
        System.out.println("Chord resolution: " + chordResolution);
        * 
        */

        
    }
    
    /**
     * Extracts chords given the start beat and end beat with the specified chord resolution
     * @param startBeat
     * @param endBeat
     */
    public ChordPart extract(int startBeat, int endBeat)
    {
        int start = BEAT * (startBeat - 1);
        
        MidiImport newImport = new MidiImport();
        
        newImport.setResolution(importResolution);
        newImport.readMidiFile(chordFile);
        
        //take the accompaniment and bass channels from the midi file
        List<MelodyPart> bassMelodyParts = new ArrayList<MelodyPart>();
        List<MelodyPart> chordMelodyParts = new ArrayList<MelodyPart>();
        MelodyPart currentMelodyPart;
        LinkedList<MidiImportRecord> melodies = newImport.getMelodies();
        for (final MidiImportRecord record : melodies)
        {
            currentMelodyPart = record.getPart();
            int end = Math.min(currentMelodyPart.getSize() - 1, (BEAT * endBeat)-1);
            if (record.getChannel() == bassChannel && currentMelodyPart != null)
            {
                currentMelodyPart = currentMelodyPart.copy(start, end);
                bassMelodyParts.add(currentMelodyPart);
            }
            if (record.getChannel() == chordChannel && currentMelodyPart != null)
            {
                currentMelodyPart = currentMelodyPart.copy(start, end);
                chordMelodyParts.add(currentMelodyPart);
            }
        }

        MelodyPart[] arrayBassMelodyParts = bassMelodyParts.toArray(new MelodyPart[bassMelodyParts.size()]);
        MelodyPart[] arrayChordMelodyParts = chordMelodyParts.toArray(new MelodyPart[chordMelodyParts.size()]);
        
        //normalize the melodyparts
        normalize(arrayBassMelodyParts, importResolution);
        normalize(arrayChordMelodyParts, importResolution);
        
        int maxSize = Math.max(findMax(arrayBassMelodyParts),findMax(arrayChordMelodyParts));
        int numSlots = (maxSize / slotResolution) + 1;
        
        int[] bassline = getBassline(arrayBassMelodyParts, numSlots);
        boolean[][] comp = getComp(arrayChordMelodyParts, numSlots);
        
        // for debugging
        /*
        for(int i=0; i<numSlots; i=i+4)
        {
            System.out.println("Slot # " + i);
            if(bassline[i]!=-1)
            {
            System.out.println("Bass: " + notes[bassline[i]]);
            }
            System.out.println("Comp: " + NoteSymbol.showNoteContents(comp[i]));
        }
        * 
        */
        
        fixComp(bassline, comp);
        /*
        System.out.println("After comp normalization:");
        for(int i=0; i<numSlots; i=i+4)
        {
            System.out.println("Slot # " + i);
            if(bassline[i]!=-1)
            {
            System.out.println("Bass: " + notes[bassline[i]]);
            }
            System.out.println("Comp: " + NoteSymbol.showNoteContents(comp[i]));
        }
        * 
        */
        
        ChordPart chordpart = getChordPart(bassline, comp, maxSize);
        //remove duplicate chords
        chordpart.fixDuplicateChords(chordpart, chordResolution);
        return chordpart;
    }   
    
    /**
     * Extract chords from beginning of midi file to the end
     * @return 
     */
    public ChordPart extract()
    {
        MidiImport newImport = new MidiImport();
        newImport.readMidiFile(chordFile);
        newImport.setResolution(importResolution);
        
        List<MelodyPart> bassMelodyParts = new ArrayList<MelodyPart>();
        List<MelodyPart> chordMelodyParts = new ArrayList<MelodyPart>();
        MelodyPart currentMelodyPart;
        LinkedList<MidiImportRecord> melodies = newImport.getMelodies();
        for (final MidiImportRecord record : melodies)
        {
            currentMelodyPart = record.getPart();
            if (record.getChannel() == bassChannel && currentMelodyPart != null)
            {
                bassMelodyParts.add(currentMelodyPart);
            }
            if (record.getChannel() == chordChannel && currentMelodyPart != null)
            {
                chordMelodyParts.add(currentMelodyPart);
            }
        }
        
        MelodyPart[] arrayBassMelodyParts = bassMelodyParts.toArray(new MelodyPart[bassMelodyParts.size()]);
        MelodyPart[] arrayChordMelodyParts = chordMelodyParts.toArray(new MelodyPart[chordMelodyParts.size()]);
        
        normalize(arrayBassMelodyParts, importResolution);
        normalize(arrayChordMelodyParts, importResolution);
        
        int maxSize = Math.max(findMax(arrayBassMelodyParts),findMax(arrayChordMelodyParts));
        int numSlots = (maxSize / slotResolution) + 1;
        
        int[] bassline = getBassline(arrayBassMelodyParts, numSlots);
        boolean[][] comp = getComp(arrayChordMelodyParts, numSlots);
        
        fixComp(bassline, comp);
        
        ChordPart chordpart = getChordPart(bassline, comp, maxSize);
        chordpart.fixDuplicateChords(chordpart, chordResolution);
        return chordpart;
    }

    
    /**
     * Takes the lowest notes from an array of bass MelodyParts
     * @param bassMelodyParts
     * @param slotSize
     * @return 
     */
    private int[] getBassline(MelodyPart[] bassMelodyParts, int slotSize)
    {
        int length = findMax(bassMelodyParts);
        int[] bass = new int[length];
        //take the lowest note in array of bass melodyparts
        for (int i = 0; i < length; i++) {
            Note bassnote = bassMelodyParts[0].getNote(i);
            for (int j = 1; j < bassMelodyParts.length; j++) {
                Note currentnote = bassMelodyParts[j].getNote(i);
                if (bassnote != null && currentnote != null
                        && bassnote.nonRest() && currentnote.nonRest()
                        && currentnote.getPitch() < bassnote.getPitch())
                {
                    bassnote = currentnote.copy();
                }
            }
            bass[i] = noteToBitAddress(bassnote);
        }
        
        int[] bassline = new int[slotSize];
        for(int i = 0; i < length; i=i+slotResolution)
        {
            int slotCount = i / slotResolution;
            if(bass[i]!=-1)
            {
                bassline[slotCount] = bass[i];
            }
            else if(bass[i]==-1)
            {
                if (i == 0 && i + slotResolution < length) {
                    for (int j = i + 1; j < i + slotResolution; j++) {
                        if (bass[j] != -1) {
                            bassline[slotCount] = bass[j];
                            break;
                        }
                    }
                }
                else if (i + slotResolution < length) {
                    for (int j = i + 1; j < i + slotResolution; j++) {
                        if (bass[j] != -1) {
                            bassline[slotCount] = bass[j];
                            break;
                        }
                    }
                }
            }
        }
        return bassline;
    }
    
    /**
     * Converts the array of accompaniment MelodyParts to a 2d array of bitmaps
     * @param chordMelodyParts
     * @param slotSize
     * @return 
     */
    
    private boolean[][] getComp(MelodyPart[] chordMelodyParts, int slotSize) {
        int length = findMax(chordMelodyParts);
        boolean[][] chords = new boolean[length][SEMITONES];
        for (int i = 0; i < chordMelodyParts.length; i++)
        {
            for (int j = 0; j < chordMelodyParts[i].size(); j++)
            {
                Note note  = chordMelodyParts[i].getNote(j);
                int noteAddress = noteToBitAddress(note);
                if (noteAddress!=-1)
                {
                    chords[j][noteAddress] = true;
                }  
            }
        }
        
        //fix comp
        boolean[][] comp = new boolean[slotSize][SEMITONES];
        for(int i = 0; i < length; i=i+slotResolution)
        {
            int slotCount = i/slotResolution;
            if (numNotes(chords[i])>1)
            {
                comp[slotCount] = chords[i];
            }
            else if(numNotes(chords[i])<2)
            {
                if (i==0 && i + slotResolution < length) {
                    for (int j = i + 1; j < i + slotResolution; j++)
                    {
                        if (numNotes(chords[j])>1)
                        {
                            comp[slotCount] = chords[j];
                            break;
                        }
                    }
                }
                else if (i + slotResolution < length) {
                    for (int j = i + 1; j < i + slotResolution; j++)
                    {
                        if (numNotes(chords[j])>1)
                        {
                            comp[slotCount] = chords[j];
                            break;
                        }
                    }
                }
            }
        }
        return comp;
    }
    
    /**
     * Returns the length of the largest MelodyPart given an array of MelodyParts
     * @param arrayMelodyParts
     * @return 
     */
    private int findMax(MelodyPart[] arrayMelodyParts)
    {
        int max = 0;
        for(int i = 0; i < arrayMelodyParts.length; i++){
            int length = arrayMelodyParts[i].getSize();
        
            if(max<length){
                max = length;
            }
        }
        return max;
    }
    
    private int noteToBitAddress(Note note) {
        if(note==null||note.isRest())
        {
            return -1;
        }
        else
        {
            int num = note.getPitch();
            num = num % 12;
            return num;
        }
    }
    
    /**
     * Normalizes the array of MelodyParts such that it contains Units for each
     *  resolution slot. eg With a quarter note resolution, a whole note will
     *  be normalized into 4 quarter notes
     * @param arrayMelodyPart
     * @param resolution 
     */
    private void normalize(MelodyPart[] arrayMelodyPart, int resolution) {
        Note note;
        Note prevNote;
        for (int i = 0; i < arrayMelodyPart.length; i++)
        {
            for (int j = 0; j < arrayMelodyPart[i].size; j = j + resolution)
            {
                note = arrayMelodyPart[i].getNote(j);
                if (note == null && j == 0)
                {
                    //do nothing
                }
                if (note == null)
                {
                    prevNote = arrayMelodyPart[i].getNote(j - resolution);
                    prevNote.setRhythmValue(resolution);
                    arrayMelodyPart[i].setNote(j, prevNote);
                }
            }
        }
    }
    
    /**
     * Fixes offbeat comping in the accompaniment part
     * @param bass
     * @param comp 
     */
    private void fixComp(int[] bass, boolean[][] comp)
    {
        int count = chordResolution/slotResolution;
        int eighth = slotResolution/EIGHTH;
        int quarter = eighth + eighth;
        int length = bass.length;
        
        for (int i = 0; i < length; i = i+count)
        {
            if (bass[i] == -1) {
                if (i == 0 && i + count < length) {
                    for (int j = i + 1; j < i +count; j++) {
                        if (bass[j] != -1) {
                            bass[i] = bass[j];
                            break;
                        }
                    }
                } else if (i > quarter && bass[i - eighth] != -1 && bass[i - quarter] != bass[i - eighth]) {
                    bass[i] = bass[i - eighth];
                } else if (i + count < length) {
                    for (int j = i + 1; j < i + count; j++) {
                        if (bass[j] != -1) {
                            bass[i] = bass[j];
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < length; i = i + count) {
            if (numNotes(comp[i])<2 && bass[i] != -1) {
                if (i == 0 && i + count < length) {
                    for (int j = i + 1; j < i + count; j++) {
                        if (numNotes(comp[j])>1) {
                            comp[i] = comp[j];
                            break;
                        }
                    }
                } else if (i > quarter && numNotes(comp[i - eighth])>1
                        && numNotes(comp[i - quarter])>1
                        && !Arrays.equals(comp[i - eighth], comp[i - quarter])) {
                    comp[i] = comp[i - eighth];
                    comp[i-eighth] = null;
                } else if (i + count < length) {
                    for (int j = i+1; j < i+count; j++) {
                        if (numNotes(comp[j])>1) {
                            comp[i] = comp[j];
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns the number of notes in the bitmap chord
     * @param bitmap
     * @return 
     */
    private int numNotes(boolean[] bitmap)
    {
        int count = 0;
        if (bitmap==null)
        {
            return 0;
        }
        for(int i = 0; i < bitmap.length; i++)
        {
            if(bitmap[i]==true)
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns a ChordPart that includes the extract chords
     * @param bass
     * @param comp
     * @param size
     * @return 
     */
    private ChordPart getChordPart(int[] bass, boolean[][] comp, int size)
    {
        ChordPart chordpart = new ChordPart(size);

        boolean[] prevComp = null;
        for(int i = 0; i < size; i = i + chordResolution)
        {
            int chordCount = i/slotResolution;
            int root = bass[chordCount];
            if(bass[chordCount]!=-1)
            {
                boolean[] bitChord = comp[chordCount];
                if(Arrays.equals(prevComp, bitChord))
                {
                }
                else{
                prevComp = comp[chordCount];
                bitChord[root] = true;
                boolean shiftedBitChord[] = shiftBitsLeft(bitChord, root);
                Chord chord = matchChords(shiftedBitChord, root);
                chordpart.setChord(i, chord);
                }
            }
        }
        return chordpart;
    }
    
    /**
     * Shifts the bitmap chord a specific amount - used for transposing a bitmap
     *  chord, so it can be pattern matched with the chord dictionary
     * @param bitChord
     * @param shift
     * @return 
     */
    private boolean[] shiftBitsLeft(boolean[] bitChord, int shift)
    {
        int numShift = 0;
        while (numShift < shift)
        {
            boolean temp = bitChord[0];
            for (int i = 0; i < 11; i++)
            {
                bitChord[i] = bitChord[i+1];
            }
            bitChord[11] = temp;
            numShift++;
        }
        return bitChord;
    }
    
    /**
     * Matches the bitmap chord with the chord name from the dictionary
     * @param bitChord
     * @param transpose
     * @return 
     */
    private Chord matchChords(boolean[] bitChord, int transpose)
    {
        if(numNotes(bitChord)<1)
        {
            return null;
        }
        //quick fifth missing fix
        if(bitChord[FIFTH]==false&&bitChord[DIMFIFTH]==false&&bitChord[AUGFIFTH]==false)
        {
            bitChord[FIFTH]=true;
        }
        if(numNotes(bitChord)<3)
        {
            return null;
        }
        for (int i = 0; i < chordNames.length; i++) {
            if (Arrays.equals(bitChord, chordBitMaps[i])) {
                String chordName = chordNames[i];
                Chord chord = new Chord(chordName);
                chord.transpose(transpose);
                return chord;
            }
        }
        int root = transpose;
        int totalShift = 0;
        int numNotes = 0;
        for (int i = 0; i < 12; i++) {
            if (bitChord[i] == true) {
                numNotes = numNotes + 1;
            }
        }
        //inversions
        int count = numNotes - 1;
        while (count != 0) {
            int shift = 0;
            //get next
            for (int i = 1; i < 12; i++) {
                if (bitChord[i] == true) {
                    shift = i;
                    break;
                }
            }
            totalShift = shift + totalShift;
            //shift to next
            shiftBitsLeft(bitChord, shift);
            //match chords
            for (int j = 0; j < chordNames.length; j++) {
                //check simple pattern matching
                if (Arrays.equals(bitChord, chordBitMaps[j])) {
                    String chordName = chordNames[j];
                    //keeps track of which note is bass
                    //find bass location
                    Chord chord = new Chord(chordName);
                    chord.transpose((transpose + totalShift) % 12);
                    String name = chord.getName();
                    name = name + "/" + notes[root];
                    chord.setName(name);
                    return chord;
                }
            }
            count = count - 1;
        }
        return null;
    }
}