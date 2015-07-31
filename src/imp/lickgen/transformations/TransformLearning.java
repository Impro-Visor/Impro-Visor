/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
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

package imp.lickgen.transformations;

import imp.Constants;
import imp.com.RectifyPitchesCommand;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Part.PartIterator;
import imp.data.Rest;
import imp.data.Unit;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import imp.lickgen.NoteConverter;
import imp.lickgen.transformations.trends.Trend;
import java.util.ArrayList;
import polya.Polylist;
import polya.PolylistEnum;
import polya.PolylistIterator;

/**
 *
 * @author Alex Putman
 */
public class TransformLearning{
    
    
public static final int CHROMATIC_TREND = 1000;
public static final int DIATONIC_TREND  = 1001;
public static final int SKIP_TREND      = 1002;
public static final int NO_TREND        = 999;
public static final int NO_CHROMATIC_DATA   = 998;
public static final String NO_DIATONIC_DATA   = "";

private static final int STARTS_WITH_MINUS = 1;
private static final int DOESNT_START_WITH_MINUS = 0;
private static final int NO_DATA = 997;

private static final int WHOLE_STEP = 2;
private static final int HALF_STEP = 1;

private int windowResolution;

public TransformLearning()
{}

/**
* Divides a melody by chords and flattens each section by resolution.
* Not sure this does anything different then flattenByResolution.
* @param melody                      the melody to divide
* @param chords                      the chordPart the melody is played over
* @param resolution                  the minimum length of each note in result
* @param startingSlot                the slot in melody where flattening starts
* @param endingSlot                  the slot in melody where flattening ends
* @param concatRepeatPitches         combines repeat pitches in result if true
* @return MelodyPart                 returns the melody flattened by chord
*/
public MelodyPart flattenByChord(MelodyPart melody,
                                 ChordPart chords,
                                 int resolution,
                                 int startingSlot,
                                 int endingSlot,
                                 boolean concatRepeatPitches)
{
    MelodyPart flattened = new MelodyPart(melody.size());
    // trys to go through each chord in the melody. If skips a chord if it can't
    // get to the beginning of the chord in atleast resolution slots
    for(int slotIndex = startingSlot; slotIndex < endingSlot;)
    {
        Chord curChord = chords.getChord(slotIndex).copy();
        
        int nextSlotIndex = chords.getNextChordIndex(slotIndex);
        
        // find next chord that is at least a distance of resolution from
        // the current chord 
        while(nextSlotIndex - slotIndex < resolution && nextSlotIndex != -1)
            nextSlotIndex = chords.getNextChordIndex(nextSlotIndex);
        
        // Subtract one for extracting purposes
        if(nextSlotIndex == -1)
        {
            nextSlotIndex = chords.size()-1;
        }
        else
        {
            nextSlotIndex--;
        }
        
        // Since flatten by resolution takes in a chordPart, we need to put the
        // current chord in one. 
        curChord.setRhythmValue(resolution);
        ChordPart curChordPart = new ChordPart();
        curChordPart.addChord(curChord);
        
        int newEndSlot = (nextSlotIndex <= endingSlot)?
                            nextSlotIndex : endingSlot;
        
        MelodyPart flatByRes 
                = flattenByResolution(melody,
                                      curChordPart,
                                      resolution, 
                                      slotIndex,
                                      newEndSlot,
                                      concatRepeatPitches);
        
        MelodyPart justSection = flatByRes.extract(slotIndex, 
                                                   newEndSlot, 
                                                   true, 
                                                   true);
        flattened.pasteOver(justSection, slotIndex);
        
        // make the slot index now one slot after our previous ending slot
        slotIndex = nextSlotIndex + 1;
    }
    return flattened;
}

/**
* Iterates through a melody in slot lengths of resolution, decides the best note
* in each segment and sets the only note in the segment to the best note. 
* @param melody                      the melody to flatten
* @param chords                      the chordPart the melody is played over
* @param resolution                  the minimum length of each note in result
* @param startingSlot                the slot in melody where flattening starts
* @param endingSlot                  the slot in melody where flattening ends
* @param concatRepeatPitches         combines repeat pitches in result if true
* @return MelodyPart                 returns the melody flattened by resolution
*/
public MelodyPart flattenByResolution(MelodyPart melody, 
                                       ChordPart chords,
                                       int resolution,
                                       int startingSlot,
                                       int endingSlot,
                                       boolean concatRepeatPitches)
{
    MelodyPart flattenedPart = new MelodyPart();
    // This will be used to save the best note of the previous selection
    Note prevNote = null;
    // This will be used to save the best note for a resolution selection
    Note bestNote;
    
    // loop through segments of length resolution
    for(int slotIndex = startingSlot; 
        slotIndex + resolution - 1 <= endingSlot;
        slotIndex += resolution)
    {
        Chord chord = chords.getCurrentChord(slotIndex);
        ArrayList<Note> notes = getNotesInResolution(melody, 
                                                     resolution, 
                                                     slotIndex);
        
        //bestNote = getBestNote(notes, chord, resolution, startingSlot);
        bestNote = getAverageNote(notes, chord);
        
        // if we do not want repeat pitches
        if(concatRepeatPitches)
        {
            // See if the current best note equals the last best note
            if(prevNote == null)
            {
                prevNote = bestNote;
            }
            else if(prevNote.samePitch(bestNote))
            {
                // if it does, we just want to add the duration to that of 
                // the previous note
                prevNote.augmentRhythmValue(bestNote.getRhythmValue());
            }
            else
            {
                // else we know the prev note is as long as possible, so add
                // it to the part and try to do the same with the new best note
                flattenedPart.addNote(prevNote);
                prevNote = bestNote;
            }
        }
        else
        {
            // else just add the best note
            flattenedPart.addNote(bestNote);
        }
    }
    if(concatRepeatPitches)
    {
        // since we are only adding prev notes if concatRepeatPitches is true,
        // we need to add the last prevNote after everything is looped over
        flattenedPart.addNote(prevNote);
    }
    
    MelodyPart newMelody = melody.copy();
    // We only want to paste over the part we changed
    newMelody.pasteOver(flattenedPart, startingSlot);
    
    //move pitches to nearest chord tone
    RectifyPitchesCommand cmd = new RectifyPitchesCommand(newMelody, startingSlot, endingSlot, chords, false, false, true, false, false);
    cmd.execute();
    
    return newMelody;
}
/**
* Returns an arraylist with all the notes in a given section
* @param melody                      the melody to get notes from
* @param resolution                  the length of the selection
* @param startingSlot                the starting slot of the selection
* @return ArrayList<Note>            containing all the notes in the section
*/    
private ArrayList<Note> getNotesInResolution(MelodyPart melody,
                                             int resolution,
                                             int startingSlot)
{
    MelodyPart resolutionCutPart = melody.extract(startingSlot,
                                                  startingSlot + resolution - 1, 
                                                  true, 
                                                  true);
    
    PartIterator noteIt = resolutionCutPart.iterator();
    
    ArrayList<Note> notes = new ArrayList<Note>();
    
    while(noteIt.hasNext())
    {
        notes.add((Note)noteIt.next());
    }
    return notes;
}
/**
* Returns the "best" note to be that flattened note in a list of notes
* @param notes                       the ArrayList of notes to select from
* @param chord                       the Chord the notes are under
* @param resolution                  the resolution being used to flatten
* @param startingSlot                the slot of the first note in notes
* @return Note                       that is the optimal flattened note in notes
*/    
private Note getBestNote(ArrayList<Note> notes, 
                         Chord chord, 
                         int resolution, 
                         int startingSlot)
{
    Note bestNote = new Note(0);
    int bestScore = 0;
    int totalDur = 0;
    for(Note note: notes)
    {
        totalDur += note.getRhythmValue();
        int score = getNoteScore(note, chord, resolution, startingSlot);
        if(score > bestScore)
        {
            bestNote = note.copy();
            bestScore = score;
        }
        startingSlot += note.getRhythmValue();
    }
    // set the duration of the best note to that of the entire selection
    bestNote.setRhythmValue(totalDur);
    return bestNote;
}

/**
 * getAverageNote
 * Returns a note whose pitch is the average of all the notes in the list
 * (excluding rests) rectified to a chord tone
 * and whose duration is the total duration of the notes in the list
 * (including rests)
 * @param notes list of notes
 * @param chord chord the notes are being played over
 * @return note of average pitch rectified
 */
private Note getAverageNote(ArrayList<Note> notes,
                            Chord chord){
    int totalPitch = 0;
    int totalDuration = 0;
    int totalNotes = 0;
    for(Note n : notes){
        if(!n.isRest()){
          totalPitch += n.getPitch();  
          totalNotes++;
        }
        totalDuration += n.getRhythmValue();
    }
    Note average;
    if(totalNotes!=0){
        int averagePitch = totalPitch/totalNotes;
        //moving to closest chord tone now happens at a higher level up
        //using a rectify pitches command
        /*if(!chord.isNOCHORD()){
            average = Note.getClosestMatch(averagePitch, chord.getSpell());
            average.setRhythmValue(totalDuration);
        }else{
            average = new Note(averagePitch, totalDuration);
        }*/
        average = new Note(averagePitch, totalDuration);
    }else{
       average = new Rest(totalDuration);
    }
    
    return average;
}

/**
* Returns the score of a certain note to compare against other scores
* @param note                        the Note to score
* @param chord                       the Chord of note
* @param resolution                  the resolution being used to flatten
* @param startingSlot                the slot of note
* @return int                        that represents how important note is
*/  
private int getNoteScore(Note note, 
                         Chord chord, 
                         int resolution, 
                         int startingSlot)
{
    // This is definitely not optimized and can be improved
    int score = note.getRhythmValue();
    score += 120*(1 - (startingSlot%resolution)/(1.0*resolution));
    if(LickGen.classifyNote(note, chord) == LickGen.CHORD)
    {
        score += 120;
    }
    else if(LickGen.classifyNote(note, chord) == LickGen.COLOR)
    {
        score += 20;          
    }
    
    if(note.isRest())
        score += 40;
    return score;
}
/**
* Just creates a transform that transforms every outline note to its section
* in the original melody
* @param outline                     the flattened outline
* @param transformed                 the original melody to build from
* @param chords                      the chordPart of the leadsheet
* @param start                       the starting slot to learn from
* @param stop                        the stopping slot to learn from
* @return Polylist                   form of a Transform
*/  
public Transform createBlockTransform(MelodyPart outline, 
                                      MelodyPart transformed, 
                                      ChordPart chords, 
                                      int start, 
                                      int stop,
                                      int resolution)
{
    Transform transform = new Transform();
    
    this.windowResolution = resolution;
    
    for(int slot = start; slot < stop;)
    {
        int nextSlot = slot + windowResolution;
        if(nextSlot > stop){
            nextSlot = stop + 1;
        }

        MelodyPart outlinePart = outline.extract(slot, nextSlot-1,true,true);
        MelodyPart transPart = transformed.extract(slot, nextSlot-1,true,true);
        ChordPart chordPart = chords.extract(slot, nextSlot-1);
        Chord chord = chords.getCurrentChord(slot);
        
        Note origNote = outlinePart.getCurrentNote(0);
        Chord firstChord = chordPart.getCurrentChord(0);
        StringBuilder subName = new StringBuilder();
        
        //make duration of note learned from the start of the sub name
        int duration = origNote.getRhythmValue();
        if(duration == Constants.WHOLE){
            subName.append(Transform.wholePrefix);
        }else if(duration == Constants.HALF){
            subName.append(Transform.halfPrefix);
        }else if(duration == Constants.QUARTER){
            subName.append(Transform.quarterPrefix);
        }else if(duration == Constants.EIGHTH){
            subName.append(Transform.eighthPrefix);
        }else{
            subName.append(Transform.otherPrefix);
        }
        
        //then add relative pitch of note
        subName.append("first-rel-pitch-");
        
        NoteChordPair pair = new NoteChordPair(origNote, firstChord);
        subName.append(pair.getRelativePitch());
        
        String nameString = subName.toString();
        
        Substitution substitution = new Substitution();
        substitution.setName(nameString);

        //pass in empty substitution with relevant name to add new transformations to
        substitution = createBlockSubstitution(substitution,
                                                            outlinePart, 
                                                            transPart, 
                                                            chord,
                                                            chordPart);
        transform.addSubstitution(substitution);

        slot = nextSlot;
    }
    
    return transform;
}
/**
* Creates a substitution that transform the note in outline into the
* transformed notes.
* @param outline                     the flattened outline section
* @param transformed                 the original melody section
* @param chords                      the chordPart of the section
* @return Polylist                   form of a Substitution
*/  
private Substitution createBlockSubstitution(Substitution sub,
                                             MelodyPart outline, 
                                             MelodyPart transformed, 
                                             Chord chord,
                                             ChordPart chordPart)
{
    int flatNotes = 0;
    int outlineSlot = 0;
    while(outline.getCurrentNote(outlineSlot) != null)
    {
        flatNotes++;
        outlineSlot = outline.getNextIndex(outlineSlot);
    }
    
    Transformation transformation = createTwoNoteBlockTransformation(outline,
                                                                    transformed,
                                                                    chordPart,
                                                                    flatNotes);
    sub.addTransformation(transformation);
    
    return sub;
}
/**
 * Creates a substitution that transform the notes in outline into the
* transformed notes.
 * @param outline           the flattened outline section
 * @param transformed       the original melody section
 * @param chordPart         the chordPart of the section
 * @param numNotes          the number of notes in the outline section
 * @return Polylist         form of a transform
 */
private Transformation createTwoNoteBlockTransformation(MelodyPart outline,
                                                        MelodyPart transformed,
                                                        ChordPart chordPart,
                                                        int numNotes)
{
    Polylist transformation = Polylist.PolylistFromString("transformation");

    String description = makeDescription(outline, chordPart);
    
    Polylist descriptionList = Polylist.PolylistFromString(description);
    Polylist weight = Polylist.PolylistFromString("weight 1");
    
    transformation = transformation.addToEnd(descriptionList).addToEnd(weight);
    
    StringBuilder sourceNotes = new StringBuilder();
    sourceNotes.append("source-notes");
    for(int i = 0; i < numNotes; ++i){
        sourceNotes.append(" n");
        sourceNotes.append(i+1);
    }
    
    String sourceString = sourceNotes.toString();
    transformation = transformation.addToEnd(Polylist.PolylistFromString(sourceString));
    
    Polylist guardCondition = getTwoWindowGuardCondition(outline, chordPart);
    Polylist targetNotes = getWindowTwoTargetNotes(outline, transformed, chordPart);
    
    StringBuilder defaultNotes = new StringBuilder();
    sourceNotes.append("target-notes");
    for(int i = 0; i < numNotes; ++i){
        sourceNotes.append(" n");
        sourceNotes.append(i+1);
    }
    
    String defaultString = defaultNotes.toString();
    Polylist defaultTarget = Polylist.PolylistFromString(defaultString);
    
    transformation = transformation.addToEnd(guardCondition);
    
    if(targetNotes != null)
        transformation = transformation.addToEnd(targetNotes);
    else
        transformation = transformation.addToEnd(defaultTarget);
    
    Transformation trans = new Transformation();
    trans.setTransformation(transformation);
    return trans;
}

/**
* Creates a guard condition for Windowing, or just one note
* @param outline                     contains the outline note
* @param chord                       the chord of the outline note
* @return Polylist                   form of a guard condition
*/  
private Polylist getWindowGuardCondition(MelodyPart outline, Chord chord)
    {
        Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
        
        Note origNote = outline.getCurrentNote(0);
        Polylist andEquals = Polylist.PolylistFromString("and (not (rest? n1))");
        Polylist categoryEquals = Polylist.PolylistFromString("= (note-category n1)");
        Polylist chordFamilyEquals = Polylist.PolylistFromString("= (chord-family n1)");
        Polylist relPitchEquals = Polylist.PolylistFromString("= (relative-pitch n1)");
        
        int cat = LickGen.classifyNote(origNote, chord);
        switch (cat){
            case LickGen.CHORD:
                categoryEquals = categoryEquals.addToEnd("C");
                break;
            case LickGen.COLOR:
                categoryEquals = categoryEquals.addToEnd("L");
                break;
            case LickGen.APPROACH:
                categoryEquals = categoryEquals.addToEnd("A");
                break;
            default:
                categoryEquals = categoryEquals.addToEnd("X");
                break;
        }
        
        chordFamilyEquals = chordFamilyEquals.addToEnd(chord.getFamily());
        relPitchEquals = relPitchEquals.addToEnd(NoteConverter.noteToRelativePitch(origNote, chord).second());
        andEquals = andEquals.addToEnd(categoryEquals).addToEnd(chordFamilyEquals).addToEnd(relPitchEquals);
        guardCondition = guardCondition.addToEnd(andEquals);
        return guardCondition;
    }   
/**
 * Creates a guard condition for Windowing, or just one note
 * @param outline       contains the outline notes
 * @param chords        the chords of the outline notes
 * @return Polylist     form of a guard condition
 */
private Polylist getTwoWindowGuardCondition(MelodyPart outline, ChordPart chords)
{
    Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
    StringBuilder andEqString = new StringBuilder();
    andEqString.append("and ");

    int notes = 0;
    int slot = 0;
    while(outline.getCurrentNote(slot) != null){
        ++notes; 
        
        Note origNote = outline.getCurrentNote(slot);
        Chord currChord = chords.getCurrentChord(slot);
        
        Polylist duration = Polylist.PolylistFromString("duration>= (duration n"+notes+") "+Note.getDurationString(origNote.getRhythmValue()));
        andEqString = andEqString.append(duration);
        
        if(origNote.isRest()){
            Polylist rest = Polylist.PolylistFromString(
                    "rest? n" + notes);
            
            andEqString = andEqString.append(rest);
        }
        else {
            Polylist rest = Polylist.PolylistFromString(
                "not (rest? n" + notes + ")");
            Polylist categoryEquals = Polylist.PolylistFromString(
                "= (note-category n" + notes + ")");
            Polylist relPitchEquals = Polylist.PolylistFromString(
                    "= (relative-pitch n" + notes + ")");

            int cat = LickGen.classifyNote(origNote, currChord);
            switch (cat){
                case LickGen.CHORD:
                    categoryEquals = categoryEquals.addToEnd("C");
                    break;
                case LickGen.COLOR:
                    categoryEquals = categoryEquals.addToEnd("L");
                    break;
                case LickGen.APPROACH:
                    categoryEquals = categoryEquals.addToEnd("A");
                    break;
                default:
                    categoryEquals = categoryEquals.addToEnd("X");
                    break;
            }
            NoteChordPair ncp = new NoteChordPair(origNote, currChord);
            relPitchEquals = relPitchEquals.addToEnd(ncp.getRelativePitch());
            andEqString = andEqString.append(rest).append(categoryEquals).append(relPitchEquals);
        }

        slot = outline.getNextIndex(slot);
    }

    if(notes == 2 && 
      !outline.getCurrentNote(0).isRest() && 
      !outline.getNextNote(0).isRest()){
        
        Polylist pitchComp = makePitchComparison(outline, chords);
        andEqString.append(pitchComp);
    }
    
    Polylist andEquals = Polylist.PolylistFromString(andEqString.toString());
    guardCondition = guardCondition.addToEnd(andEquals);
    return guardCondition;
}

/**
 * Creates target-notes for Windowing
 * @param outline           contains the outline notes
 * @param transformed       what to transform the outline notes into
 * @param chords            the chordPart over the outline notes
 * @return Polylist         form of target notes
 */
private Polylist getWindowTwoTargetNotes(MelodyPart outline,
                                         MelodyPart transformed,
                                         ChordPart chords)
{
    Polylist targetNotes = Polylist.PolylistFromString("target-notes");
    
    int noteNum = 1;
    
    for(int slot = 0; slot < outline.getSize();){
        int nextSlot = outline.getNextIndex(slot);
        if(nextSlot == -1)
            nextSlot = windowResolution;
        if(windowResolution > outline.getSize())
            nextSlot = outline.getSize();
        
        MelodyPart outlinePart = outline.extract(slot, nextSlot-1,true,true);
        MelodyPart transPart = transformed.extract(slot, nextSlot-1,true,true);
        Chord chord = chords.getCurrentChord(slot);
        String noteString = "n" + noteNum;
        
        targetNotes = getTargetNotes(outlinePart, transPart, chord, noteString, targetNotes);
        
        slot = nextSlot;
        ++noteNum;
    }
    return targetNotes;
}
/**
 * Creates target-notes for Windowing
 * @param outline           Contains the outline note
 * @param transformed       what to transform the outline note to
 * @param chord             the chord of the outline note
 * @param noteString        the number of the note in the window
 * @param targetNotes       the form of target notes
 * @return Polylist         targets for individual notes
 */
private Polylist getTargetNotes(MelodyPart outline,
                                MelodyPart transformed,
                                Chord chord,
                                String noteString,
                                Polylist targetNotes)
{
    Note origNote = outline.getCurrentNote(0);
        
    PartIterator transNotes = transformed.iterator();

    if(origNote.isRest())
        return targetNotes.addToEnd(noteString);
    else{
        while(transNotes.hasNext())
        {
            Note toTransform = (Note)transNotes.next();
            
            //new code
            //use multiply-duration instead here: multiply_duration
            Polylist multiplyDuration = Polylist.PolylistFromString("multiply-duration");
            String subDur = Double.toString((double)toTransform.getRhythmValue()/(double)origNote.getRhythmValue());
            multiplyDuration = multiplyDuration.addToEnd(subDur);
            
            //old code
            //Polylist setDuration = Polylist.PolylistFromString("set-duration");
            //setDuration = setDuration.addToEnd(duration);
            Polylist result;
            if(chord.isNOCHORD()){
                result = getTransposeChromatic(origNote, toTransform, noteString);
            }else{
                result = getTransposeDiatonicFunction(origNote, 
                                                  toTransform, 
                                                  noteString, 
                                                  chord);
            }
            
            if(result == null)
            {
                return null;
            }

            //setDuration = setDuration.addToEnd(result);
            multiplyDuration = multiplyDuration.addToEnd(result);
            
            //targetNotes = targetNotes.addToEnd(setDuration);
            targetNotes = targetNotes.addToEnd(multiplyDuration);
        }

        return targetNotes ;
    }
}

/**
* Creates note function that transforms the outline note into the transformed
* note using transpose-diatonic
* @param outlineNote                 the note we want to put into the function   
* @param transNote                   the note we want to get out of the function
* @param var                         the string variable that represents
*                                    outlineNote
* @param chord                       the chord of outlineNote
* @return Polylist                   function that uses transpose-diatonic
*/  
private Polylist getTransposeDiatonicFunction(Note outlineNote, 
                                              Note transNote, 
                                              String var, 
                                              Chord chord)
{
    Evaluate eval = new Evaluate(new Polylist());
    eval.setNoteVar("n1", outlineNote, chord);
    eval.setNoteVar("n2", transNote, chord);
    Object result = eval.absoluteRelPitchDiff(transNote, outlineNote, chord);
    if(transNote.isRest())
        return Polylist.PolylistFromString("make-rest "+var);
    if(result == null)
    {
        return null;
    }
    String relPitch = result.toString();
    Polylist transposePitch = Polylist.PolylistFromString("transpose-diatonic " 
                                                          + relPitch 
                                                          + " " + var);
    return transposePitch;
}
/**
* Creates note function that transforms the outline note into the transformed
* note using transpose-chromatic
* @param outlineNote                 the note we want to put into the function   
* @param transNote                   the note we want to get out of the function
* @param var                         the string variable that represents
*                                    outlineNote
* @return Polylist                   function that uses transpose-chromatic
*/  
private Polylist getTransposeChromatic(Note outlineNote, 
                                       Note transNote, 
                                       String var)
{
    if(transNote.isRest())
        return Polylist.PolylistFromString("make-rest "+var);
    double diff = (transNote.getPitch() - outlineNote.getPitch())/2.0;
    if(Math.abs(diff) > 10)
        return null;
    Polylist transposePitch = Polylist.PolylistFromString("transpose-chromatic " 
                                                          + diff + " " + var);
    return transposePitch;
}
/**
* Creates condition function that checks that two notes are a certain diatonic
* distance away
* @param note1                       the first note to compare   
* @param note2                       the second note to compare   
* @param var1                        the string variable that represents note1
* @param var2                        the string variable that represents note2
* @param chord                       the chord under note1
* @return Polylist                   condition function
*/  
private Polylist getTransposeDiatonicCondition(Note note1, 
                                               Note note2, 
                                               String var1, 
                                               String var2, 
                                               Chord chord)
{
    Evaluate eval = new Evaluate(new Polylist());
    eval.setNoteVar(var1, note1, chord);
    eval.setNoteVar(var2, note2, chord);
    Polylist subHelper;
    subHelper = Polylist.PolylistFromString("pitch- (relative-pitch " + var2 + 
                                            ") " + var1);
    Object result = eval.absoluteRelPitchDiff(note2, note1, chord);
    if(note2.isRest())
        return null;
    if(result == null)
    {
        return null;
    }
    String relPitch = result.toString();
    Polylist transposePitch = Polylist.PolylistFromString("=");
    transposePitch = transposePitch.addToEnd(subHelper).addToEnd(relPitch);
    return transposePitch;
}

public Transform createTrendTransform(MelodyPart outline, 
                                     MelodyPart transformed, 
                                     ChordPart chords, 
                                     int startingSlot, 
                                     int endingSlot){
    return merge(createTrendTransform(outline, transformed, chords, startingSlot, endingSlot, CHROMATIC_TREND),
                createTrendTransform(outline, transformed, chords, startingSlot, endingSlot, DIATONIC_TREND));
}

/**
* Creates a substitution from a trend's outline and result
* @param outlineNCP                  polylist of NCPs of the outline
* @param resultNCP                   polylist of NCPs of the resulting line
* @param resultFromNCP               polylist of NCPs of the outline notes
*                                    each resulting note is build from
* @param trend                       the trend type 
* @return Polylist                   form of a Substitution
*/  
private Substitution createTrendSubstitution(Polylist outlineNCP, 
                                         Polylist resultNCP, 
                                         Polylist resultFromNCP, 
                                         int trend)
{
    Substitution sub = new Substitution();
    sub.setName(resultNCP.length() + "-changed-GENERATED"+((trend == CHROMATIC_TREND)? "-CHROMATIC": "-DIATONIC"));
    
    Transformation transformation = createTrendTransformation(outlineNCP, 
                                                              resultNCP, 
                                                              resultFromNCP, 
                                                              trend);
    if(transformation == null)
        return null;
    sub.addTransformation(transformation);
    return sub;
}
/**
* Creates a transformation from a trend's outline and result
* @param outlineNCP                  polylist of NCPs of the outline
* @param resultNCP                   polylist of NCPs of the resulting line
* @param resultFromNCP               polylist of NCPs of the outline notes
*                                    each resulting note is build from
* @param trend                       the trend type 
* @return Polylist                   form of a Transformation
*/ 
private Transformation createTrendTransformation(Polylist outlineNCP, 
                                           Polylist resultNCP, 
                                           Polylist resultFromNCP, 
                                           int trend)
{
    Polylist transformation = Polylist.PolylistFromString(
            "transformation" + 
            "(description generated-transformation)" + 
            "(weight 1)");
    Polylist sourceNotes = Polylist.PolylistFromString("source-notes");
    
    PolylistEnum outEn = outlineNCP.elements();
    
    while(outEn.hasMoreElements())
    {
        Polylist ncp = (Polylist)outEn.nextElement();
        String varName = (String)ncp.fourth();
        sourceNotes = sourceNotes.addToEnd(varName);
    }
    transformation = transformation.addToEnd(sourceNotes);
    
    Polylist importantNCPs = new Polylist();
    
    PolylistEnum resultFromEn = resultFromNCP.elements();
    while(resultFromEn.hasMoreElements())
    {
        Polylist ncpFrom = (Polylist)resultFromEn.nextElement();
        if(!importantNCPs.member(ncpFrom))
            importantNCPs = importantNCPs.addToEnd(ncpFrom);
    }
    Polylist guard = getTrendGuardCondition(importantNCPs, trend);
    Polylist target = getTrendTargetNotes((Polylist)outlineNCP.first(), 
                                          (Polylist)outlineNCP.last(), 
                                          resultNCP, 
                                          resultFromNCP, 
                                          trend);
    if(guard == null || target == null)
        return null;
    transformation = transformation.addToEnd(guard).addToEnd(target);
    
    Transformation trans = new Transformation();
    trans.setTransformation(transformation);
    
    return trans;
}
/**
* Creates a guard condition for a trend's important outline notes
* @param importantNotesWithChords    polylist of NCPs of the important outline
*                                    notes
* @param trend                       the trend type 
* @return Polylist                   form of a guard condition
*/ 
private Polylist getTrendGuardCondition(Polylist importantNotesWithChords, 
                                        int trend)
    {
        Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
        Polylist andEquals = Polylist.PolylistFromString("and");
        
        PolylistEnum en = importantNotesWithChords.elements();
        while(en.hasMoreElements())
        {
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note note = (Note)noteWithChord.first();
            Chord chord = (Chord)noteWithChord.second();
            String var = (String)noteWithChord.fourth();
            
            // make all conditions we want checked
            Polylist categoryEquals = Polylist.PolylistFromString("= (note-category "
                                                                  + var + ")");
            Polylist chordFamilyEquals = Polylist.PolylistFromString("= (chord-family "
                                                                     + var + ")");
            Polylist relPitchEquals = Polylist.PolylistFromString("= (relative-pitch "
                                                                  + var + ")");
            
            // fill conditions
            Object relPitch = NoteConverter.noteToRelativePitch(note, chord).second();
            relPitchEquals = relPitchEquals.addToEnd(relPitch);
            int cat = LickGen.classifyNote(note, chord);
            switch (cat){
                case LickGen.CHORD:
                    categoryEquals = categoryEquals.addToEnd("C");
                    break;
                case LickGen.COLOR:
                    categoryEquals = categoryEquals.addToEnd("L");
                    break;
                case LickGen.APPROACH:
                    categoryEquals = categoryEquals.addToEnd("A");
                    break;
                default:
                    categoryEquals = categoryEquals.addToEnd("X");
                    break;
            }
            chordFamilyEquals = chordFamilyEquals.addToEnd(chord.getFamily());
            
            // add all conditions to the and condition
            andEquals = andEquals.addToEnd(categoryEquals);
            andEquals = andEquals.addToEnd(chordFamilyEquals);
            andEquals = andEquals.addToEnd(relPitchEquals);
        }
        if(trend == CHROMATIC_TREND)
        {
            en = importantNotesWithChords.elements();
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note compNote = (Note)noteWithChord.first();
            String compVar = (String)noteWithChord.fourth();
            while(en.hasMoreElements())
            {
                noteWithChord = (Polylist)en.nextElement();
                Note note = (Note)noteWithChord.first();
                Chord chord = (Chord)noteWithChord.second();
                String var = (String)noteWithChord.fourth();
                
                //CHROMATIC SPECIFIC
                Polylist pitchMinus = Polylist.PolylistFromString("pitch-");
                pitchMinus = pitchMinus.addToEnd(compVar).addToEnd(var);
                
                double minus = (compNote.getPitch() - note.getPitch())/2.0;
                Polylist pitchMinusEquals = Polylist.PolylistFromString("=");
                pitchMinusEquals = pitchMinusEquals.addToEnd(pitchMinus).addToEnd(minus);
                //END CHROMATIC SPECIFIC
                
                andEquals = andEquals.addToEnd(pitchMinusEquals);
            }
        }
        else if(trend == DIATONIC_TREND)
        {
            en = importantNotesWithChords.elements();
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note compNote = (Note)noteWithChord.first();
            Chord compChord = (Chord)noteWithChord.second();
            String compVar = (String)noteWithChord.fourth();
            while(en.hasMoreElements())
            {
                noteWithChord = (Polylist)en.nextElement();
                Note note = (Note)noteWithChord.first();
                Chord chord = (Chord)noteWithChord.second();
                String var = (String)noteWithChord.fourth();
                //DIATONIC SPECIFIC
                Polylist diatonicCond = getTransposeDiatonicCondition(note, 
                                                                      compNote, 
                                                                      var, 
                                                                      compVar, 
                                                                      compChord);
                //END DIATONIC SPECIFIC
                andEquals = andEquals.addToEnd(diatonicCond);
            }
        }
        guardCondition = guardCondition.addToEnd(andEquals);
        return guardCondition;
    }    
/**
* Creates a transformation from a trend's outline and result
* @param firstOutlineNCP             NCP of the first outline note in a trend
* @param lastOutlineNCP              NCP of the last outline note in a trend
* @param resultNCPs                  polylist of NCPs of the notes to produce
* @param resultFromNCPs              polylist of NCPs of the notes each result
*                                    transformed from
* @param trend                       the trend type 
* @return Polylist                   form of target notes
*/ 
private Polylist getTrendTargetNotes(Polylist firstOutlineNCP, 
                                     Polylist lastOutlineNCP, 
                                     Polylist resultNCPs, 
                                     Polylist resultFromNCPs, 
                                     int trend)
    {
        Polylist targetNotes = Polylist.PolylistFromString("target-notes");
        
        PolylistEnum resultNCPen = resultNCPs.elements();
        PolylistEnum resultFromNCPen = resultFromNCPs.elements();
        Note firstOutNote = (Note)firstOutlineNCP.first();
        int firstOutSlot = (Integer)firstOutlineNCP.third();
        String firstOutVar = (String)firstOutlineNCP.fourth();
        Note lastOutNote = (Note)lastOutlineNCP.first();
        int lastOutSlot = (Integer)lastOutlineNCP.third();
        String lastOutVar = (String)lastOutlineNCP.fourth();
        boolean first = true;
        while(resultNCPen.hasMoreElements())
        {
            
            Polylist setDur = Polylist.PolylistFromString("set-duration");
            Polylist result = (Polylist)resultNCPen.nextElement();
            Polylist resultFrom = (Polylist)resultFromNCPen.nextElement();
            Note transNote = (Note)result.first();
            int startingSlot = (Integer)result.third();
            
            Note fromNote = (Note)resultFrom.first();
            Chord fromChord = (Chord)resultFrom.second();
            String fromVar = (String)resultFrom.fourth();
            
            // if the first note needs to be subtracted from and added
            if(first)
            {
                first = false;
                int slotsLeftFront = firstOutNote.getRhythmValue() - 
                                    (startingSlot - firstOutSlot);
                
                if(slotsLeftFront < firstOutNote.getRhythmValue())
                {
                    Polylist addToFront = Polylist.PolylistFromString("subtract-duration");
                    String subDur = Note.getDurationString(slotsLeftFront);
                    addToFront = addToFront.addToEnd(subDur).addToEnd(firstOutVar);
                    targetNotes = targetNotes.addToEnd(addToFront);
                }
            }
            
            setDur = setDur.addToEnd(Note.getDurationString(transNote.getRhythmValue()));
            if(transNote.samePitch(fromNote))
            {
                setDur = setDur.addToEnd(fromVar);
            }
            else if(transNote.isRest() || fromNote.isRest())
            {
                Polylist rest = Polylist.PolylistFromString("make-rest " + 
                                                            fromVar);
                setDur = setDur.addToEnd(rest);
            }
            else
            {
                Polylist transposeNote = new Polylist();

                //CHROMATIC SPECIFIC
                if(trend == CHROMATIC_TREND)
                {
                    transposeNote = getTransposeChromatic(fromNote, 
                                                          transNote, 
                                                          fromVar);
                }
                //DIATONIC SPECIFIC
                else if(trend == DIATONIC_TREND)
                {
                    transposeNote = getTransposeDiatonicFunction(fromNote, 
                                                                 transNote, 
                                                                 fromVar, 
                                                                 fromChord);
                }
                if(transposeNote == null)
                    return null;
                setDur = setDur.addToEnd(transposeNote);
            }
            targetNotes = targetNotes.addToEnd(setDur);
            
            // if the last note is not part of the actual trend or needs to be 
            // extended or reduced
            if(!resultNCPen.hasMoreElements())
            {
                int endingSlot = startingSlot + transNote.getRhythmValue();
                if(startingSlot < lastOutSlot)
                {
                    if( endingSlot > lastOutSlot)
                    {
                        Polylist subFromEnd = Polylist.PolylistFromString("add-duration");
                        String subDur = Note.getDurationString(lastOutNote.getRhythmValue() + 
                                                               lastOutSlot - endingSlot);
                        subFromEnd = subFromEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(subFromEnd);
                    }
                    else if(endingSlot < lastOutSlot)
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("add-duration");
                        String addDur = Note.getDurationString(lastOutNote.getRhythmValue() + 
                                                               lastOutSlot - endingSlot);
                        addToEnd = addToEnd.addToEnd(addDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                    else
                    {
                        targetNotes = targetNotes.addToEnd(lastOutVar);
                    }
                }
                else
                {
                    if(lastOutSlot + lastOutNote.getRhythmValue() !=  
                            startingSlot + transNote.getRhythmValue())
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("subtract-duration");
                        String subDur = Note.getDurationString(endingSlot - lastOutSlot);
                        addToEnd = addToEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                }
            }
        }
        
        return targetNotes;
    }

private boolean isDiatonicTrendConsistent(String oldTrend, String newTrend)
{
    return oldTrend.startsWith("-") ^ newTrend.startsWith("-");
}

/**
* Creates a Note Chord Pair that also contains the slot of the note
* @param note         
* @param chord         
* @param slot                
* @return Polylist                   NPC
*/ 
private Polylist createNCP(Note note, Chord chord, int slot)
{
    Polylist list = Polylist.PolylistFromString("");
    return list.addToEnd(note).addToEnd(chord).addToEnd(slot);
}

/**
* Creates a Note Chord Pair that also contains the slot of the note and its 
* String var representation used in the transformation
* @param note         
* @param chord         
* @param slot   
* @param var                         integer number representing the placement 
*                                    of the note in source-notes
* @return Polylist                   NPC
*/ 
private Polylist createNCP(Note note, Chord chord, int slot, int var)
{
    Polylist list = Polylist.PolylistFromString("");
    return list.addToEnd(note).addToEnd(chord).addToEnd(slot).addToEnd("n"+var);
}


public Transform merge(Transform t1, Transform t2){
    Transform merged = new Transform();
    
    ArrayList<Substitution> subs1 = t1.substitutions;
    ArrayList<Substitution> subs2 = t2.substitutions;
    
    for(Substitution s : subs1){
        merged.addSubstitution(s);
    }
    
    for(Substitution s : subs2){
        merged.addSubstitution(s);
    }
    
    return merged;
}



private boolean isValid(int trend){
    return trend == CHROMATIC_TREND || trend == DIATONIC_TREND;
}



public Transform createTrendTransform(MelodyPart outline, MelodyPart transformed, ChordPart chords, int startingSlot, int endingSlot, int trend){

    Transform transform = new Transform();

    //If trend is not a recognized trend type, return an empty transform
    if(!isValid(trend)){
        return transform;
    }

    //???
    int varNumber = 1;

    Polylist lastNCP = createNCP(transformed.getCurrentNote(startingSlot), 
                                 chords.getCurrentChord(startingSlot), 
                                 startingSlot);
    
    Polylist lastOutNCP = createNCP(outline.getCurrentNote(startingSlot), 
                                    chords.getCurrentChord(startingSlot), 
                                    startingSlot, 
                                    varNumber++);
    
    Polylist subOutline = Polylist.PolylistFromString("").addToEnd(lastOutNCP);
    Polylist subTransform = Polylist.PolylistFromString("").addToEnd(lastNCP);
    
    Polylist subTransformFrom = Polylist.PolylistFromString("");
    int outlineNoteSlot = outline.getNextIndex(startingSlot);

    int addLastToFrom = 1;

    //initialize trendData to NO_DATA
    int trendData = NO_DATA;
    
    //Go through solo and add substitutions to transform
    for(int slot = transformed.getNextIndex(startingSlot); 
            slot < endingSlot; 
            slot = transformed.getNextIndex(slot))
    {
        
        
        Note lastNote = (Note)lastNCP.first();
        
        Note curNote = transformed.getCurrentNote(slot);
        Chord curChord = chords.getCurrentChord(slot);
        Polylist curNCP = createNCP(curNote, curChord, slot);
        
        int newOutSlot = outline.getCurrentNoteIndex(slot);
        Note outNote = outline.getCurrentNote(newOutSlot);
        Chord outChord = chords.getCurrentChord(newOutSlot);
        Polylist newOutNCP = createNCP(outNote, 
                                       outChord, 
                                       newOutSlot, 
                                       varNumber);
 
        //get data
        int newData = data(trend, lastNote, curNote, curChord);
        
        //store new data if it keeps the trend alive or starts a new trend
        if(trendData != NO_DATA && isTrendConsistent(trendData, newData, trend)
                    ||  (trendData == NO_DATA && curNote.nonRest() && distOkay(newData, trend)))
        {
            addLastToFrom++;
            trendData = newData;
            subTransform = subTransform.addToEnd(curNCP);
        }

        //otherwise, make a substitution for the trend that just ended
        else{

            // create substitution for the trend that just ended
            if(addLastToFrom > 1)
            {
                //???
                for(int i = 0; i < addLastToFrom; i++)
                    subTransformFrom = subTransformFrom.addToEnd(lastOutNCP);

                if(lastNote.samePitch((Note)lastOutNCP.first()))
                {
                    subTransform = subTransform.allButLast();
                    subTransformFrom = subTransformFrom.allButLast();
                }
                Substitution substitution = 
                        createTrendSubstitution(subOutline, 
                                                subTransform, 
                                                subTransformFrom, 
                                                trend); //changed this to trend

                if(substitution != null)
                {
                    transform.addSubstitution(substitution);
                }   

                //reset data
                addLastToFrom = 1;
	        varNumber = 1;
                trendData = NO_DATA;
                
                //if the new data has a valid trend with the current note
                if(distOkay(newData, trend)){
                    // reset data but a note before because it has a valid trend
                    // with the current note

                    // not best implementation, could just set to newChromData
                    // and just move everything up a note

                    slot = transformed.getPrevIndex(slot);
                    curNCP = lastNCP;
                    newOutNCP = createNCP((Note)lastOutNCP.first(), 
                                          (Chord)lastOutNCP.second(), 
                                          (Integer)lastOutNCP.third(), 
                                          varNumber++);
                    newOutSlot = (Integer)lastOutNCP.third();
                    subTransform = new Polylist(lastNCP, new Polylist());

                }else{
                    // reset data
                    newOutNCP = createNCP((Note)newOutNCP.first(), 
                                          (Chord)newOutNCP.second(), 
                                          (Integer)newOutNCP.third(), 
                                          varNumber++);
                    subTransform = new Polylist(curNCP, new Polylist());
                }

                outlineNoteSlot = outline.getNextIndex((Integer)newOutNCP.third());
	        subOutline = new Polylist(newOutNCP, new Polylist());
                subTransformFrom = Polylist.PolylistFromString("");

            //don't create substitution, just reset data
            }else{
            	//reset data
            	trendData = NO_DATA;
                addLastToFrom = 1;
                varNumber = 1;
                
                
                newOutNCP = createNCP((Note)newOutNCP.first(), 
                                      (Chord)newOutNCP.second(), 
                                      (Integer)newOutNCP.third(), 
                                      varNumber++);
                outlineNoteSlot = outline.getNextIndex((Integer)newOutNCP.third());
                subTransform = new Polylist(curNCP, new Polylist());
                subOutline = new Polylist(newOutNCP, new Polylist());
                subTransformFrom = Polylist.PolylistFromString("");

            }     	

        }

        if(newOutSlot >= outlineNoteSlot)
        {
            outlineNoteSlot = outline.getNextIndex(newOutSlot);
            subOutline = subOutline.addToEnd(newOutNCP);
            varNumber++;
        }
        
        lastNCP = curNCP;
        lastOutNCP = (Polylist)subOutline.last();

    }

    return transform;
}

private static boolean isDiatonicTrendConsistent(double a, double b){
    return (a == STARTS_WITH_MINUS) ^ (b == STARTS_WITH_MINUS);
}

private static boolean isChromaticTrendConsistent(double trendData, double newData){
    return Math.abs(trendData - newData) < WHOLE_STEP;
}

private static boolean isTrendConsistent(double trendData, double newData, int trend){
    if(trend == CHROMATIC_TREND){
        return isChromaticTrendConsistent(trendData, newData);
    }else if(trend == DIATONIC_TREND){
        return isDiatonicTrendConsistent(trendData, newData);
    }else{
        return false; // shouldn't happen
    }
}

private static int directionalDist(Note lastNote, Note curNote){
    return curNote.getPitch() - lastNote.getPitch();
}

private static boolean distOkay(int dist, int trend){
    if(trend == CHROMATIC_TREND){
        return Math.abs(dist) < WHOLE_STEP;
    }else if(trend == DIATONIC_TREND){
        //return true;
        return Math.abs(dist) > HALF_STEP;
    }else{
        return false;
    }
}

private static int data(int trend, Note lastNote, Note curNote, Chord curChord){
    
    //CHROMATIC
    if(trend == CHROMATIC_TREND){
        
        return directionalDist(lastNote, curNote);
    
    //DIATONIC
    }else if(trend == DIATONIC_TREND){
        
        Evaluate eval = new Evaluate(new Polylist());
        Object result = eval.absoluteRelPitchDiff(curNote, lastNote, curChord);
        if(result != null && distOkay(directionalDist(lastNote, curNote), DIATONIC_TREND)){
            return result.toString().startsWith("-") ? STARTS_WITH_MINUS : DOESNT_START_WITH_MINUS;
        }else{
            return NO_DATA;
        }   
    
    //OTHER
    }else{
        return -1; //shouldn't happen
    }
}

public Transform trendTransform(MelodyPart melodyPart, ChordPart chordPart, int [] metre, Trend trend){
    Transform trendTransform = new Transform();
    
    TrendDetector detector = new TrendDetector(trend);
    ArrayList<TrendSegment> trends = detector.trends(melodyPart, chordPart);
    
    for(TrendSegment original : trends){
        TrendSegment flattened = trend.importantNotes(original, metre);
        Substitution sub = makeTrendSub(original, flattened, trend);
        trendTransform.addSubstitution(sub);
        
    }
    
    return trendTransform;
    
}

private Substitution makeTrendSub(TrendSegment original, TrendSegment flattened, Trend trend){
    Substitution trendSub = new Substitution();
    trendSub.setName(original.getSize() + "-changed-GENERATED-"+trend.getName());
    trendSub.addTransformation(makeTrendTransformation(original, flattened));
    return trendSub;
    
}

/**
 * makeTrendTransformation
 * @param original segment of the original melody
 * @param flattened flattened version of that segment
 * @return a learned transformation
 */
private Transformation makeTrendTransformation(TrendSegment original, TrendSegment flattened){

    
    //transformation, description, weight
    Polylist transformation = Polylist.PolylistFromString(
        "transformation" + 
        "(description generated-transformation)" + 
        "(weight 1)");
    
    //source notes
    Polylist sourceNotes = Polylist.PolylistFromString("source-notes");

    NCPIterator i = flattened.makeIterator();
    
    while(i.hasNext())
    {
        sourceNotes = sourceNotes.addToEnd(i.nextNCP().varName());
    }
    transformation = transformation.addToEnd(sourceNotes);
    
    //guard condition
    Polylist guard = getTrendGuardCondition(flattened);
    
    //target notes
    Polylist target = getTrendTargetNotes(original, flattened);
    
    //safety check
    if(guard == null || target == null)
        return null;
    
    //add guard condition and target notes
    transformation = transformation.addToEnd(guard).addToEnd(target);
    
    //debugging
    //System.out.println("Original:\n"+original);
    //System.out.println("Flattened:\n"+flattened);
    //System.out.println("Transformation:\n"+transformation);
    
    //make transformation out of the String
    return new Transformation(transformation);
    
}

    private Polylist getTrendGuardCondition(TrendSegment original){
        
        Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
        Polylist andEquals = Polylist.PolylistFromString("and");
        
        NCPIterator i = original.makeIterator();

        while(i.hasNext())
        {

            NoteChordPair ncp = i.nextNCP();
            Note note = ncp.getNote();
            Chord chord = ncp.getChord();
            String var = ncp.varName();
            
            // make all conditions we want checked
            Polylist categoryEquals = Polylist.PolylistFromString("= (note-category "
                                                                  + var + ")");
            Polylist chordFamilyEquals = Polylist.PolylistFromString("= (chord-family "
                                                                     + var + ")");
            Polylist relPitchEquals = Polylist.PolylistFromString("= (relative-pitch "
                                                                  + var + ")");
            
            // fill conditions
            Object relPitch = NoteConverter.noteToRelativePitch(note, chord).second();
            relPitchEquals = relPitchEquals.addToEnd(relPitch);
            int cat = LickGen.classifyNote(note, chord);
            switch (cat){
                case LickGen.CHORD:
                    categoryEquals = categoryEquals.addToEnd("C");
                    break;
                case LickGen.COLOR:
                    categoryEquals = categoryEquals.addToEnd("L");
                    break;
                case LickGen.APPROACH:
                    categoryEquals = categoryEquals.addToEnd("A");
                    break;
                default:
                    categoryEquals = categoryEquals.addToEnd("X");
                    break;
            }
            chordFamilyEquals = chordFamilyEquals.addToEnd(chord.getFamily());
            
            // add all conditions to the and condition
            andEquals = andEquals.addToEnd(categoryEquals);
            andEquals = andEquals.addToEnd(chordFamilyEquals);
            andEquals = andEquals.addToEnd(relPitchEquals);
        }

        guardCondition = guardCondition.addToEnd(andEquals);
        return guardCondition;
    }  
    
    //TODO
    private Polylist getTrendTargetNotes(TrendSegment original, TrendSegment flattened){
        //initialize
        Polylist targetNotes = Polylist.PolylistFromString("target-notes");
        
        //first note
        NoteChordPair firstOutNCP = flattened.firstNCP();
        
        Note firstOutNote = firstOutNCP.getNote();
        int firstOutSlot = firstOutNCP.getSlot();
        String firstOutVar = firstOutNCP.varName();
        
        //last note
        NoteChordPair lastOutNCP = flattened.lastNCP();
        
        Note lastOutNote = lastOutNCP.getNote();
        int lastOutSlot = lastOutNCP.getSlot();
        String lastOutVar = lastOutNCP.varName();
        
        //whether we're at the first note
        boolean first = true;
        
        //go through target notes, adding them
        NCPIterator targetNoteIterator = original.makeIterator();
        
        while(targetNoteIterator.hasNext()){
            Polylist setDur = Polylist.PolylistFromString("set-duration");
            
            //result note
            NoteChordPair result = targetNoteIterator.nextNCP();
            
            Note transNote = result.getNote();
            int startingSlot = result.getSlot();
            
            //source note
            NoteChordPair resultFrom = source(result, flattened);
            
            Note fromNote = resultFrom.getNote();
            Chord fromChord = resultFrom.getChord();
            String fromVar = resultFrom.varName();
            
            if(first){
                first = false;
                int slotsLeftFront = firstOutNote.getRhythmValue() - 
                                    (startingSlot - firstOutSlot);
                
                if(slotsLeftFront < firstOutNote.getRhythmValue())
                {
                    Polylist addToFront = Polylist.PolylistFromString("subtract-duration");
                    String subDur = Note.getDurationString(slotsLeftFront);
                    addToFront = addToFront.addToEnd(subDur).addToEnd(firstOutVar);
                    targetNotes = targetNotes.addToEnd(addToFront);
                }
            }
            setDur = setDur.addToEnd(Note.getDurationString(transNote.getRhythmValue()));
            if(transNote.samePitch(fromNote))
            {
                setDur = setDur.addToEnd(fromVar);
            }
            else if(transNote.isRest() || fromNote.isRest())
            {
                Polylist rest = Polylist.PolylistFromString("make-rest " + 
                                                            fromVar);
                setDur = setDur.addToEnd(rest);
            }
            else
            {
                Polylist transposeNote = new Polylist();

                //IMPORTANT: JUST PICKED ONE FOR NOW, COULD BE TREND SPECIFIC
                transposeNote = getTransposeChromatic(fromNote, transNote, fromVar);
                
//                //CHROMATIC SPECIFIC
//                if(trend == CHROMATIC_TREND)
//                {
//                    transposeNote = getTransposeChromatic(fromNote, 
//                                                          transNote, 
//                                                          fromVar);
//                }
//                //DIATONIC SPECIFIC
//                else if(trend == DIATONIC_TREND)
//                {
//                    transposeNote = getTransposeDiatonicFunction(fromNote, 
//                                                                 transNote, 
//                                                                 fromVar, 
//                                                                 fromChord);
//                }
                if(transposeNote == null)
                    return null;
                setDur = setDur.addToEnd(transposeNote);
            }
            targetNotes = targetNotes.addToEnd(setDur);
            
            // if the last note is not part of the actual trend or needs to be 
            // extended or reduced
            if(!targetNoteIterator.hasNext())
            {
                int endingSlot = startingSlot + transNote.getRhythmValue();
                if(startingSlot < lastOutSlot)
                {
                    if( endingSlot > lastOutSlot)
                    {
                        Polylist subFromEnd = Polylist.PolylistFromString("add-duration");
                        String subDur = Note.getDurationString(lastOutNote.getRhythmValue() + 
                                                               lastOutSlot - endingSlot);
                        subFromEnd = subFromEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(subFromEnd);
                    }
                    else if(endingSlot < lastOutSlot)
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("add-duration");
                        String addDur = Note.getDurationString(lastOutNote.getRhythmValue() + 
                                                               lastOutSlot - endingSlot);
                        addToEnd = addToEnd.addToEnd(addDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                    else
                    {
                        targetNotes = targetNotes.addToEnd(lastOutVar);
                    }
                }
                else
                {
                    if(lastOutSlot + lastOutNote.getRhythmValue() !=  
                            startingSlot + transNote.getRhythmValue())
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("subtract-duration");
                        String subDur = Note.getDurationString(endingSlot - lastOutSlot);
                        addToEnd = addToEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                }
            }
            
        }
        
        return targetNotes;
        
    }
    
    private NoteChordPair source(NoteChordPair dest, TrendSegment flattened){
        
        int slot = dest.getSlot();
        
        NCPIterator i = flattened.makeIterator();
        NoteChordPair bestSource = flattened.firstNCP();
        while(i.hasNext()){
            NoteChordPair source = i.nextNCP();
            //went too far - flattened note occurs later than original note
            if(slot < source.getSlot()){
                break;
            }else{
                //keep going
                bestSource = source;
            }
        }
        return bestSource;
    }
    /**
     * Used to generate the pitch comparison guard condition
     * @param outline
     * @param chords
     * @return 
     */
    private Polylist makePitchComparison(MelodyPart outline, ChordPart chords) 
    {
        Evaluate eval = new Evaluate(new Polylist());
        
        ArrayList<Chord> chordList = chords.getChords();
        
        NoteChordPair first = new NoteChordPair(outline.getCurrentNote(0), 
                                                chordList.get(0));
        
        NoteChordPair second = new NoteChordPair(outline.getNextNote(0), 
                                                 chordList.get(chordList.size() - 1));
        
        Polylist noteList = Polylist.list(first, second);
        
        if(eval.pitch_lt(noteList)){
            Polylist lessThan = Polylist.PolylistFromString("pitch< n1 n2");
            return lessThan;
        }
        else {
            Polylist greaterThan = Polylist.PolylistFromString("pitch>= n1 n2");
            return greaterThan;
        }
    }
    /**
     * Used to generated the description of a learned transformation
     * @param outline
     * @param chords
     * @return 
     */
    private String makeDescription(MelodyPart outline, ChordPart chords)
    {
        ArrayList<NoteChordPair> noteChordList = new ArrayList<NoteChordPair>();
        ArrayList<Note> noteList = outline.getNoteList();
        int slot = 0;
        
        for(Note note : noteList){
            Chord currChord = chords.getCurrentChord(slot);
            NoteChordPair newPair = new NoteChordPair(note, currChord);
            noteChordList.add(newPair);
            
            slot = outline.getNextIndex(slot);
        }
        
        StringBuilder description = new StringBuilder();
        description.append("description ");
        description.append("rel-pitch-");
        description.append(noteChordList.get(0).getRelativePitch());
        
        for(int i = 1; i < noteChordList.size(); ++i){
            description.append("-to-");
            description.append(noteChordList.get(i).getRelativePitch());
        }
        
        
        return description.toString();
    }
    
}


