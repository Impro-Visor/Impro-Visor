/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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

package imp.guidetone;

import imp.Constants;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.PitchClass;
import static imp.Constants.OCTAVE;
import static imp.data.NoteSymbol.makeNoteSymbol;
import java.util.ArrayList;
import imp.lickgen.NoteConverter;
import polya.PolylistEnum;
import static imp.data.NoteSymbol.makeNoteSymbol;

/**
 * The GuideLineGenerator class is used to create a leadsheet of guide tones
 * for a given chord progression to help a player in their improvisation.
 * 
 * The generator uses a chord part to create a melody part that is added to
 * the choruses in notate.
 * 
 * @see ChordPart and MelodyPart
 * @author Mikayla Konst and Carli Lessard
 * 
 */

public class GuideLineGenerator implements Constants {
    
    //lowest note on keyboard
    private final int A = 21;
    
    //The chords used to create the guide tone line
    private final ChordPart chordPart;
    
    //Direction the user wants the guide tone line to go
    //The direction of the guide tone line is reset to the original direction
    //at the beginning of each section
    private final int originalDirection;

    //each guide tone line should have a separate direction
    //this fixes range issues
    private int direction1;
    private int direction2;
    
    private final int HIGHER = 1;
    private final int LOWER = -1;
    private final int SAME = 0;
    
    //start degrees for the two lines
    private final String startDegree1;
    private final String startDegree2;
    
    //Line identifiers so we know which direction to switch
    //private final int ONLY_LINE = 0;
    private final int LINE_ONE = 1;
    private final int LINE_TWO = 2;
    
    //constants that correspond to the integers that represent direction
    public static final int ASCENDING = 1;
    public static final int NOPREFERENCE = 0;
    public static final int DESCENDING = -1;
    private static final int NOCHANGE = -2;

    //Whether or not there is one line or two lines
    private final boolean mix;
    
    //alternating: Determines the shape of the two lines - 
    //if alternating, line looks like this: /\/\/\
    //Else, line looks like this: /////
    private final boolean alternating;
    
    //MIDI values of lower and upper limits for guide tone line
    private final int lowLimit, highLimit;
    
    //Max duration and whether or not a max duration was specified
    private final int maxDuration;
    private final boolean durationSpecified;
    
    //Constants that represent whether two of the same note in a row is allowed
    private static final boolean DISALLOW_SAME = true;
    private static final boolean SAME_OKAY = false;
    
    //Constants that represent where a note is w.r.t. a given pitch range
    private static final int IN_RANGE = 0;
    private static final int BELOW_RANGE = -1;
    private static final int ABOVE_RANGE = 1;
    
    //intervals
    private static final int SAME_NOTE = 0;
    private static final int HALF_STEP = 1;
    
    private final boolean allowColor;
    private boolean alwaysDisallowSame;
    private ArrayList<Integer> contour;
    private boolean contourBased;
    int contourIndex;
    
    //a score for each of the 6 possbile distances (same note through tritone)
    private static final int distanceScores[] = 
    //  same note   half step   whole step  minor 3rd   major 3rd   tritone
    {   1,          1,          1,          2,          2,          3};
    
    private static final int directionScores[][] = 
    //  down    same    up
    {   {0,     0,      1},         //DESCENDING
        {0,     0,      0},         //NOPREFERENCE
        {1,     0,      0}};        //ASCENDING
    
    public GuideLineGenerator()
    {
        chordPart = new ChordPart();
        originalDirection = 1;
        startDegree1 = "1";
        startDegree2 = "1";
        mix = true;
        alternating = true;
        lowLimit = 0;
        highLimit = 107;
        maxDuration = 1;
        durationSpecified = true;
        allowColor = true;
    }
    
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree1, String startDegree2, boolean alternating, int lowLimit, int highLimit, int maxDuration, boolean mix, boolean allowColor, boolean alwaysDisallowSame, String contour){
     this(inputChordPart, direction, startDegree1, startDegree2, alternating, lowLimit, highLimit, maxDuration, mix, allowColor);
     this.alwaysDisallowSame = alwaysDisallowSame;
     char [] chars = contour.toCharArray();
     //contour must be at least as long as chord part
     if(chars.length >= inputChordPart.getChords().size()-1){
        for(char c : chars){
            this.contour.add(c == '1' ? ASCENDING:DESCENDING);
        }
        contourBased = true;
        contourIndex = 0;
     }else{
         this.contour = new ArrayList<Integer>();
         contourBased = false;
         contourIndex = -1;
     }

    }
    
    /**
     * Constructor
     * @param inputChordPart ChordPart from score
     * @param direction ASCENDING, DESCENDING, or NOPREFERENCE
     * @param startDegree1 start degree for line 1
     * @param startDegree2 start degree for line 2
     * @param alternating true for /\/\, false for ////
     * @param lowLimit MIDI value of low limit
     * @param highLimit MIDI value of high limit
     * @param maxDuration maxDuration of any note, 0 or less if not specified
     * @param mix true for two lines, false for one
     * @param allowColor true to allow color notes, false to disallow
     */
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree1, String startDegree2, boolean alternating, int lowLimit, int highLimit, int maxDuration, boolean mix, boolean allowColor) 
    {
        //set chord part
        chordPart = inputChordPart;
        
        //set directions
        this.originalDirection = direction;
        this.direction1 = direction;
        this.direction2 = direction;
        
        //set alternating
        this.alternating = alternating;
        
        //set mix
        this.mix = mix;
       
        //set range limits
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        
        //set max duration of any note
        this.maxDuration = maxDuration;
        
        //pass in 0 or less to signify no duration specified
        durationSpecified = maxDuration>0;
        
        //set allow color
        this.allowColor = allowColor;
        
        //set start degrees
        this.startDegree1 = startDegree1;
        this.startDegree2 = startDegree2;

        this.alwaysDisallowSame = false;
        this.contour = new ArrayList<Integer>();
        contourBased = false;
        contourIndex = -1;
    }
    
    /**
     * chordTones
     * Returns an ArrayList of all the chord tones of a given chord
     * (includes color tones if allowColor is true)
     * @param chord chord from which chord tones are to be extracted
     * @param duration duration that these notes are to have
     * @return ArrayList of chord tones - NOTE: default pitches used
     */
    private ArrayList<Note> chordTones(Chord chord, int duration){
        PolylistEnum noteList = chord.getSpell().elements();
        ArrayList<Note> chordTones = new ArrayList<Note>();
        while(noteList.hasMoreElements()){
            Note note = ((NoteSymbol)noteList.nextElement()).toNote();
            note.setRhythmValue(duration);
            chordTones.add(note);
        }
        if(allowColor){
            PolylistEnum colorList = chord.getColor().elements();
            while(colorList.hasMoreElements()){
                Note note = ((NoteSymbol)colorList.nextElement()).toNote();
                note.setRhythmValue(duration);
                chordTones.add(note);
            }
        }
        return chordTones;
    }
    
    /**
     * closestChordTones
     * Returns an array list of chord tones (including color tones if allowColor)
     * that belong to the given chord with their pitches adjusted to be as close
     * as possible to prev's pitch
     * @param chord chord to extract tones from
     * @param prev note to make tones close to
     * @param duration duration notes should have
     * @param line line being affected - needed to break tritone ties
     * @return array list of closest chord/color tones
     */
    private ArrayList<Note> closestChordTones(Chord chord, Note prev, int duration, int line){
        ArrayList<Note> chordTones = chordTones(chord, duration);
        ArrayList<Note> closestChordTones = new ArrayList<Note>();
        for(Note note : chordTones){
            note = getClosest(prev, note, line);
            closestChordTones.add(note);
        }
        return closestChordTones;
    }
    
    /**
     * getMod
     * returns an int representing the pitch class of the note
     * (0 for C, ... , 11 for B)
     * @param note note to get pitch class identifier from
     * @return int representing a note's pitch class
     */
    private int getMod(Note note){
        return getMod(note.getPitch());
    }
    
    /**
     * getMod
     * returns an int representing the pitch class of the midi value
     * (0 for C, ... , 11 for B)
     * @param midi midivalue
     * @return int representing a midi value's pitch class
     */
    private int getMod(int midi){
        return midi%OCTAVE;
    }
    
    /**
     * Returns the note of the same pitch class as next that is closest to prev.
     * In the case that prev and next's pitch classes are a tritone apart,
     * uses line's direction to break the tie.
     * If the line's direction is NO_PREFERENCE,
     * chooses note a tritone *above* prev.
     * Note returned retains the RhythmValue of next.
     * @param prev the note that we're coming from
     * @param next a note that we're trying to change to be closer to prev
     * @return a note with the same pitch class and duration as next
     * that is closest to prev
     */
    private Note getClosest(Note prev, Note next, int line){
        
        int prevPitch = prev.getPitch();
        
        int prevMod = getMod(prev);
        int nextMod = getMod(next);
        int compareMods = compareTo(nextMod, prevMod);
        
        int dist1 = Math.abs(prevMod-nextMod);
        int dist2 = OCTAVE-dist1;
        int compareDists = compareTo(dist2, dist1);
        
        int pitch = prevPitch;
        //careful that pitch does not go out of range 
        //or worse, into the negatives / past the end of the keyboard!
        if(compareDists == HIGHER){//dist2>dist1 - use dist1
            if(compareMods == HIGHER){//nextMod>prevMod
                pitch+=dist1;
            }else if(compareMods == LOWER){//prevMod>nextMod
                pitch-=dist1;
            }else{
                //leave pitch the same
            }
        }else if(compareDists == LOWER){//dist1>dist2 - use dist2
            if(compareMods == HIGHER){//nextMod>prevMod
                pitch-=dist2;
            }else if(compareMods == LOWER){//prevMod>nextMod
                pitch+=dist2;
            }else{
                //leave pitch the same
            }
        }else{
            //tritone - tiebreak, go up for now - fix
            int lineDirection = getDirection(line);
            if(lineDirection==ASCENDING){
                pitch+=dist1;
            }else if(lineDirection==DESCENDING){
                pitch-=dist1;
            }else{
                pitch+=dist1;//ARBITRARY TIE BREAK
            }
            
        }
        
        //temporary fix
        //shouldn't happen unless guide tone line goes way out of range
        if(pitch<0){
            pitch = prevPitch;
        }
        //makes range limits hard as opposed to soft
        int inRange = inRange(pitch);
        if(inRange!=IN_RANGE){
            if(inRange==ABOVE_RANGE){
                pitch-=OCTAVE;
            }else{
                pitch+=OCTAVE;
            }
        }
        return new Note(pitch, next.getRhythmValue());
    }
    
    /**
     * compareTo
     * Compares two ints
     * @param a first int
     * @param b second int
     * @return 1 if a>b, 0 if a==b, -1 if a<b
     */
    private int compareTo(int a, int b){
        if(a>b){
            return HIGHER;
        }else if(b>a){
            return LOWER;
        }else{
            return SAME;
        }
    }
    
    /**
     * score
     * returns a note's score based on its distance and direction from
     * the previous note
     * @param prev the previous note
     * @param next the note to be scored
     * @param line the line being affected
     * @param disallowSame true if staying on the same note is to be
     * given a bad score
     * @return an int score
     */
    private int score(Note prev, Note next, int line, boolean disallowSame){
        return directionScore(prev, next, line)
                + distanceScore(prev, next, disallowSame);
        //distance score could be Integer.MAX_VALUE if disallowSame==true
        //even if this is the case, addition won't cause overflow 
        //because if distanceScore is Integer.MAX_VALUE,
        //directionScore will be 0
    }
    
    /**
     * bestNote
     * Returns the note from the list with the best score
     * @param prev the note that we're coming from
     * @param notes the potential notes we could go to
     * @param chord the chord the potential notes would be played over
     * @param line the line being affected
     * @param disallowSame true to make staying on the same note bad
     * @return the note with the best score - uses priority to break ties
     */
    private Note bestNote(Note prev, ArrayList<Note> notes, Chord chord, 
                                                int line, boolean disallowSame){
        Note bestNote = notes.get(0);
        int bestScore = score(prev, bestNote, line, disallowSame);
        for(Note note : notes){
            int score = score(prev, note, line, disallowSame);
            if(score<bestScore){
                bestScore = score;
                bestNote = note;
            }else if(score==bestScore){
                //use priority to tiebreak
                int compare = compareTo(priorityScore(note, chord), 
                                        priorityScore(bestNote, chord));
                if(compare==LOWER){
                    bestNote = note;
                }else if(compare==SAME){
                    //TODO: what happens when two notes have the same priority?
                    //currently arbitrarily chooses note that came first in list
                }
            }
        }
        return bestNote;
    }
    
    /**
     * nextNote
     * Finds the next note to be played
     * @param prev note we're coming from
     * @param chord chord we're going to
     * @param line line being played
     * @param duration duration of note to be returned
     * @param disallowSame true to make staying on the same note bad
     * @return the next note to be played
     */
    private Note nextNote(Note prev, Chord chord, int line, int duration, boolean disallowSame){
        if(chord.isNOCHORD()){
            return new Note(REST, Accidental.NOTHING, duration);
        }if(prev.isRest()){
            return firstNote(chord, getStartDegree(line), line, duration);
        }
        return bestNote(prev, closestChordTones(chord, prev, duration, line), chord, line, disallowSame);
    }
    
    /**
     * getStartDegree
     * returns startDegree of line passed in
     * @param line line whose startDegree is to be retrieved
     * @return startDegree of line as a string
     */
    private String getStartDegree(int line){
        if(line==LINE_ONE){
            return startDegree1;
        }else{
            return startDegree2;
        }
    }
    
    /**
     * directionScore
     * Returns appropriate direction score from the directionScores array
     * @param prev Note we're coming from
     * @param next Note we're going to
     * @param line the line we're picking a note for (need its direction)
     * @return currently, we give the following scores:
     * 0 for correct direction or same note, 1 for incorrect direction.
     * In the case of no direction preference, returns 0 for anything.
     */
    private int directionScore(Note prev, Note next, int line){
        int index1 = getDirection(line)+1;
        int index2 = compareTo(next.getPitch(), prev.getPitch())+1;
        return directionScores[index1][index2];
    }
    
    /**
     * distanceScore
     * Returns appropriate distance score from distanceScores array
     * Currently returns scores as follows:
     * 0 for same, 1 for half/whole step, 
     * 2 for minor/major 3rd, 3 for anything else.
     * @param prev note we're coming from
     * @param next note we're going to
     * @return score based on distance from prev to next
     */
    private int distanceScore(Note prev, Note next, boolean disallowSame){
        int score;
        int dist = dist(prev, next);
        int lastIndex = distanceScores.length-1;
        
        if(dist<=lastIndex&&dist>=0){//avoid array index out of bounds exception
            if(dist==SAME_NOTE){
                score = (disallowSame || alwaysDisallowSame)?Integer.MAX_VALUE:distanceScores[dist];//this score depends on disallowSame
            }else{
                score = distanceScores[dist]; 
            }
        }else{//if exception, use last score in array
            score = distanceScores[lastIndex];
        }
        return score;
    }
    
    /**
     * priorityScore
     * Returns a score based on a note's priority in the chord
     * @param next note to be scored
     * @param chord chord note is to be played over
     * @return a score based on a note's priority
     * the score returned is the index of the note in the chord's priority list
     * or the size of the priority list if the note is not in the list,
     * i.e. if the note is a color tone. This means that all color tones have
     * the same (bad) priority.
     */
    private int priorityScore(Note next, Chord chord){
        PolylistEnum priorityList = chord.getPriority().elements();
        int priority = 0;
        PitchClass nextPc = makeNoteSymbol(next).getPitchClass();
        while(priorityList.hasMoreElements()){
            NoteSymbol ns = ((NoteSymbol)priorityList.nextElement());
            if(ns.getPitchClass().enharmonic(nextPc)){
                break;
            }
            priority++;
        }
        //if the note is not in the chord's priority list (i.e. is a color tone)
        //return the size of the priority array
        return priority;
    }
    
    /**
     * dist
     * returns the absolute distance between two notes' midi values
     * @param n1 note 1
     * @param n2 note 2
     * @return distance between n1 and n2's midi values
     */
    private int dist(Note n1, Note n2){
        return dist(n1.getPitch(), n2.getPitch());
    }
    
    /**
     * dist
     * Returns the absolute distance between two ints/midiValues
     * @param n1 first int
     * @param n2 second int
     * @return distance between the two ints
     */
    private int dist(int n1, int n2){
        return Math.abs(n1-n2);
    }
    
    /**
     * getDirection
     * Gets the direction of a given line
     * @param line line to get direction of
     * @return direction of line
     */
    private int getDirection(int line){
        if(line==LINE_ONE){
            return direction1;
        }else{
            return direction2;
        }
    }

    /**
     * greaterThan
     * Tests whether a note's duration is greater than the given duration
     * @param n1 Note whose duration is being tested
     * @param duration duration to be tested against
     * @return true if n1's duration is strictly greater than duration, 
     * false otherwise
     */
    private boolean greaterThan(Note n1, int duration){
        int noteDuration = n1.getRhythmValue();
        return noteDuration>duration;
    }
    
    /**
     * splitUp
     * returns a list of durations that add up to the given duration
     * @param duration duration to be split up into smaller chunks
     * @return a list of durations created by chopping off chunks of maxDuration
     * from the total duration and then using the leftover as the last duration
     */
    private ArrayList<Integer> splitUp(int duration){
        int durationRemaining;
        ArrayList<Integer> durations = new ArrayList<Integer>();
        for(durationRemaining = duration; durationRemaining > maxDuration; durationRemaining -= maxDuration){
            durations.add(maxDuration);
        }
        durations.add(durationRemaining);
        return durations;
    }

    /**
     * notesToAdd
     * Returns a list of notes to add based on the note that would've been added
     * if it didn't have to get split up
     * @param note note to be split up into a bunch of notes to add
     * @param chord chord that these notes are to be played over
     * @param line line that these notes will be a part of
     * @return an ArrayList of notes to add to the line
     */
    private ArrayList<Note> notesToAdd(Note note, Chord chord, int line){
        ArrayList<Note> notesToAdd = new ArrayList<Note>();
        ArrayList<Integer> durations = splitUp(note.getRhythmValue());
        
        Note prevNote = new Note(note.getPitch(), durations.remove(0));
        notesToAdd.add(prevNote);
        
        for(int duration : durations){
            Note noteToAdd = nextNote(prevNote, chord, line, duration, DISALLOW_SAME);
            notesToAdd.add(noteToAdd);
            prevNote = noteToAdd;
            possibleDirectionSwitch(noteToAdd, line);
        }
        return notesToAdd;
    }
 
    /**
     * getLast
     * Returns last Note in an ArrayList of Notes
     * @param notes ArrayList of Notes
     * @return last note, or null if list is empty
     */
    private Note getLast(ArrayList<Note> notes){
        int size = notes.size();
        if(size>0){
            return notes.get(size-1);
        }else{
            return null; //if list is empty
        }
    }
    
    
    /**
     * makeGuideLine
     * Creates a MelodyPart that contains the guide tone line.
     * @return MelodyPart 
     */
    public MelodyPart makeGuideLine()
    {
        ArrayList<Note> guideLine;
        
        if(mix){
            guideLine = twoGuideLine();
        } else{
            guideLine = oneGuideLine();
        }
        
        MelodyPart guideLineMelody = new MelodyPart();
        for(Note n : guideLine){
            guideLineMelody.addNote(n);
        }
        return guideLineMelody;
    }
    
    /**
     * oneGuideLine
     * Used by makeGuideLine() to generate a list of notes to be used in
     * a single guide tone line
     * @return ArrayList<Note> The notes of the guide tone line
     */
    private ArrayList<Note> oneGuideLine()
    {
        //guideline, list of start indices of sections, list of chord durations
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        //initialize index
        int index = startIndices.get(0);
        Chord currentChord = chordPart.getChord(index);
        Note prevNote = firstNote(currentChord, startDegree1, LINE_ONE, currentChord.getRhythmValue());
        
        //iterate through chords using their durations
        for(Integer duration : durations){
            
            currentChord = chordPart.getChord(index);
            Note noteToAdd;
            
            //chord is at start of section
            if(startIndices.contains(index)){
                //at the start of each section, restore direction
                //to the user's original intended direction
                direction1 = originalDirection;
                //call firstNote method (as oppossed to nextNote method)
                noteToAdd = firstNote(currentChord, startDegree1, LINE_ONE, currentChord.getRhythmValue());
            }
            //chord is not at start of section
            else {
                //System.out.println("else");
                //System.out.println("prevNote: "+prevNote);
                //System.out.println("currentChord: "+currentChord);
                noteToAdd = nextNote(prevNote, currentChord, LINE_ONE, currentChord.getRhythmValue(), SAME_OKAY);
            }
            
            //need to split up note before adding it
            boolean noteTooLong = durationSpecified && greaterThan(noteToAdd, maxDuration);
            if(noteTooLong && !noteToAdd.isRest()){
                
                ArrayList<Note> notesToAdd = notesToAdd(noteToAdd, currentChord, LINE_ONE);
                for(Note n: notesToAdd){
                    guideLine.add(n);
                }
                prevNote = getLast(notesToAdd);
            //don't need to split up note before adding it
            }else{
                guideLine.add(noteToAdd);
                possibleDirectionSwitch(noteToAdd, LINE_ONE);
                prevNote = noteToAdd;
            }
            //increment index by chord duration to get to the next chord
            index+=duration;
        }
        
        return guideLine;
    }
    
    /**
     * possibleDirectionSwitch
     * Executes a possible direction switch if note being added is close to the
     * edge of the range (close = within a half step)
     * @param n Note to be tested
     * @param line Line whose direction could be switched
     */
    public void possibleDirectionSwitch(Note n, int line){
        
        int newDirection;
        if(!contourBased){
           newDirection = newDirection(n); 
        }else{
            if(contourIndex<contour.size()){
                newDirection = contour.get(contourIndex++);
            }else{
                newDirection = NOCHANGE;
            }
            
        }
        
        
        if(newDirection!=NOCHANGE){
            if(line==LINE_ONE){
                direction1 = newDirection;
            }else{
                direction2 = newDirection;
            }
        }
    }
    
    /**
     * newDirection
     * Returns the new direction that is to be used based on how close the note
     * is to the range limits
     * @param n note that might be close to (within a 1/2 step of) a range limit
     * @return ASCENDING if note close to low limit,
     * DESCENDING if close to high limit,
     * NO_CHANGE otherwise.
     */
    private int newDirection(Note n){
        int newDirection;
        int pitch = n.getPitch();
        if(dist(pitch, lowLimit)<=HALF_STEP){
            newDirection = ASCENDING;
        }else if(dist(pitch, highLimit)<=HALF_STEP){
            newDirection = DESCENDING;
        }else{
            newDirection = NOCHANGE;
        }
        return newDirection;
    }
    
    /**
     * inRange
     * returns whether or not a number lies within a given range (inclusive)
     * @param n number to test
     * @param low low end of range
     * @param high high end of range
     * @return 1 if n is above the range, -1 if below, 0 if within
     */
    private int inRange(int n){
        int toreturn = IN_RANGE;
        if(n>highLimit){
            toreturn = ABOVE_RANGE;
        }else if(n<lowLimit){
            toreturn = BELOW_RANGE;
        }
        return toreturn;
    }
    
    /**
     * twoGuideLine
     * Used by makeGuideLine() to generate a list of notes to be used in the
     * two guide tone lines
     * @return ArrayList<Note> The notes of the guide tone lines
     */
    private ArrayList<Note> twoGuideLine()
    {
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        int index = startIndices.get(0);
        Chord firstChord = chordPart.getChord(index);
        
        Note prevFirstNote = firstNote(firstChord, startDegree1, LINE_ONE, firstChord.getRhythmValue()/2);
        Note prevSecondNote = firstNote(firstChord, startDegree2, LINE_TWO, firstChord.getRhythmValue()/2);
        
        Note firstNoteToAdd;
        Note secondNoteToAdd;
        
        boolean threeFirst = true;

        for(Integer duration : durations){
            Chord currentChord = chordPart.getChord(index);
            
            if(startIndices.contains(index)){
                //sections always start on degree three
                threeFirst = true;
                //at the beginning of each section, restore directions to user's orignal choice
                direction1 = originalDirection;
                direction2 = originalDirection;
                
                //Set the two next notes to be half the length of the chord
                firstNoteToAdd = firstNote(currentChord, startDegree1, LINE_ONE, currentChord.getRhythmValue()/2);
                
                secondNoteToAdd = firstNote(currentChord, startDegree2, LINE_TWO, currentChord.getRhythmValue()/2);

            }
            else{
                firstNoteToAdd = nextNote(prevFirstNote, currentChord, LINE_ONE, currentChord.getRhythmValue()/2, SAME_OKAY);
                secondNoteToAdd = nextNote(prevSecondNote, currentChord, LINE_TWO, currentChord.getRhythmValue()/2, SAME_OKAY);
            }
            boolean firstNoteTooLong = greaterThan(firstNoteToAdd, maxDuration) && !firstNoteToAdd.isRest();
            boolean secondNoteTooLong = greaterThan(secondNoteToAdd, maxDuration) && !secondNoteToAdd.isRest();
            if(durationSpecified && (firstNoteTooLong || secondNoteTooLong)){
                if(threeFirst){
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord, LINE_ONE);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
                        prevFirstNote = firstNoteToAdd;
                    }
                    
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord, LINE_TWO);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                }else{
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord, LINE_TWO);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord, LINE_ONE);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
                        prevFirstNote = firstNoteToAdd;
                    }
                }
                
            }else{
                //add notes
                // Alternate the order the notes are added to the guide line
                if(threeFirst){
                    guideLine.add(firstNoteToAdd);
                    guideLine.add(secondNoteToAdd);
                }else{
                    guideLine.add(secondNoteToAdd);
                    guideLine.add(firstNoteToAdd);
                }
                
                //store notes
                prevFirstNote = firstNoteToAdd;
                prevSecondNote = secondNoteToAdd;
                
                //possibly switch direction if out of range
                possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
                possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
            }
            
            //if the lines are on the same note and in the same direction, alter their directions
            fixConvergingLines(prevFirstNote, prevSecondNote);
            
            //jump ahead to next chord
            index+=duration;
            //switch which line comes first if alternating
            if(alternating){
                threeFirst = !threeFirst;
            }
        }
        
        return guideLine;
    }

    /**
     * fixConvergingLines
     * Hack that makes line 1 ascending and line 2 descending if
     * convergence of lines is detected (same notes and same directions)
     * @param n1 note of first line
     * @param n2 note of second line
     */
    private void fixConvergingLines(Note n1, Note n2){
        if(n1.getPitch()==n2.getPitch()&&direction1==direction2){
            direction1 = ASCENDING;
            direction2 = DESCENDING;
        }
    }
    
    /**
     * firstNote
     * Creates the first note of a section
     * @param c The first chord of the section
     * @return Note of the scale degree that is chosen by the user.
     * If degree unavailable, returns note of best priority in the chord.
     * Returns a rest if the chord isNOCHORD.
     */
    private Note firstNote(Chord chord, String start, int line, int duration){
        if(chord.isNOCHORD()){
            return new Note(REST, Accidental.NOTHING, duration);
        }
        //returns the scale degree as a note in octave 0
        //or, if unavailable, the highest priority note in the chord
        Note first =  scaleDegreeToNote(start, chord, duration);
        
        //Check if it's a chord/color tone, 
        //change it to the highestPriority note if it's not
        if(!belongsTo(first, chord, CHORD_TONE)){
            if(allowColor){
                if(!belongsTo(first, chord, COLOR_TONE)){
                    first = NoteConverter.highestPriority(chord, duration);
                }
            }else{
                first = NoteConverter.highestPriority(chord, duration);
            }
            
        }
        
        //puts note close to middle
        first = closestToMiddle(first, line);//uses duration of first
        return first;
    }

    /**
     * belongsTo
     * Determines whether a given note is a chord tone or color tone
     * @param n note
     * @param c chord
     * @param chordOrColor CHORD_TONE or COLOR_TONE
     * @return Boolean - true if n's type matches chordOrColor
     */
    private boolean belongsTo(Note n, Chord c, int chordOrColor)
    {
        return (c.getTypeIndex(n)==chordOrColor && !c.isNOCHORD());
    }

    //IMPORTANT: does not specify whether to represent note as sharp or flat
    //if there is ambiguity - I think it defaults to sharp...
    /**
     * scaleDegreeToNote
     * Converts scale degree to note, using chord's rhythm value
     * IMPORTANT: does not specify whether to represent note as sharp or flat
     * @param degree
     * @param c
     * @return Note that is the specified degree of the chord in octave 0
     */
    private Note scaleDegreeToNote(String degree, Chord c, int duration){
        return NoteConverter.scaleDegreeToNote(degree, c, 0, duration);
    }  

    /**
     * middleOfRange
     * returns the midi value located at the middle of the range
     * (rounds down)
     * @return midi value of middle of range
     */
    private int middleOfRange(){
        return lowLimit+((highLimit-lowLimit)/2);//rounds down for odd numbers
    }
    
    /**
     * closestToMiddle
     * Returns the version of note n that is closest to the middle of the range
     * Below the middle if ascending, above is descending, closest if no pref
     * @param n Note
     * @param line line
     * @return version of note closest to middle of range
     */
    private Note closestToMiddle(Note n, int line){
        
        int rv = n.getRhythmValue();
        int lineDirection = getDirection(line);
        
        int closestBelow = closestBelowMiddle(n);
        boolean belowInRange = inRange(closestBelow)==IN_RANGE;
        int closestAbove = closestAboveMiddle(n);
        boolean aboveInRange = inRange(closestAbove)==IN_RANGE;
        
        int pitch;
        if(lineDirection == ASCENDING){
            if(belowInRange){
                pitch = closestBelow;
            }else{//above guaranteed to be in range because we limit the user to an octave
                pitch = closestAbove;
            }
        }else if(lineDirection == DESCENDING){
            if(aboveInRange){
                pitch = closestAbove;
            }else{//below guaranteed in range because we limit the user to an octave
                pitch = closestBelow;
            }
        }else{//NO PREFERENCE
            if(belowInRange && aboveInRange){
                int middle = middleOfRange();
                //closest of the two - tiebreak goes to above note if distances equal
                pitch = ((middle-closestBelow)<(closestAbove-middle)?closestBelow:closestAbove);
            }else if(belowInRange){
                pitch = closestBelow;
            }else{//above guaranteed to be in range because we limit the user to an octave
                pitch = closestAbove;
            }
        }
        return new Note(pitch, rv);
    }
    
    /**
     * closestBelowMiddle
     * Returns version of note n that is in the octave below the middle of range
     * @param n note
     * @return version of note in octave below middle of range
     */
    private int closestBelowMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch--){
                
        }
        return pitch;
    }
    
    /**
     * closestAboveMiddle
     * Returns version of note n that is in the octave above the middle of range
     * @param n note
     * @return version of note in octave above middle of range
     */
    private int closestAboveMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch++){
                
        }
        return pitch;
    }
    
    //problems if you pass in a negative pitch...
    /**
     * samePitchClass
     * Returns whether two pitches have the same pitch class
     * @param pitch1 first pitch
     * @param pitch2 second pitch
     * @return true if pitches have the same pitch class, false otherwise
     */
    private boolean samePitchClass(int pitch1, int pitch2){
        return getMod(pitch1) == getMod(pitch2);
    }
}
