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
import imp.midi.MIDIBeast;
import java.util.ArrayList;
import jm.music.data.Phrase;

/**
 * ImportBass.java
 * Reads and parses a channel of a midi file as a bass line.
 * The program assumes that all notes are played in sequential order (no
 * chords) and therefore only reads the first Phrase.
 * It then interprets the rhythm duration of each note as the closest
 * musical value that can be represented by 120 slots per beat for the
 * given time signature.  Pitches are kept as integers representing
 * Hertz values.  The final bassline is stored in MIDIBeast.originalBassNotes.
 * 
 * Brandy McMenamy and James Thomas Herold
 * 7/18/2007
 * 
 * This should be reworked to avoid using public members of MIDIBeast.
 */

public class ImportBass{
	boolean debug = false;
        /**
         * false if an error occurs that is serious enough that the rest of the
         * style generation for the bass line must be prevented from firing.
         **/
        public boolean canContinue = true;
	/**
	 * The phrase of sequential, non-chordal notes for the bass line
	 */
	private jm.music.data.Phrase mainPhrase;
	/**
	 * The initial notes from the bass line
	 */
	private ArrayList<jm.music.data.Note> noteArray = new ArrayList<jm.music.data.Note>();
	/**
	 * The final notes that are used as the "original notes" by each
	 * successive step in the style generation.  (Contains corrected
	 * rhythm durations).
	 */
	private ArrayList<SlottedNote> roundedNoteArray = new ArrayList<SlottedNote>();
	/**
	 * constructor.  Reads a score and adjusts the rhythm durations
	 * in the bass line.
	 */
	public ImportBass(){
            createBassPart();
            execute();
	}
        
        private void execute() {
            noteArray = new ArrayList<jm.music.data.Note>();
	    roundedNoteArray = new ArrayList<SlottedNote>();
            if(canContinue) {
                try{
                    mainPhrase = MIDIBeast.bassPart.getPhraseArray()[0];
                    getNoteArray();
                    if(MIDIBeast.mergeBassRests) mergeRests();
                    roundDurations(MIDIBeast.getResolution());
                    if(roundedNoteArray.size() == 0) {
                        if(debug) System.out.println("note array of size zero, unable to continue");
                        MIDIBeast.addError("The bass part is corrupted...no notes were found.  Go to Generate-->" +
                        "Preferences for Generation to choose a different bass part from available instruments.");
                        canContinue = false;      
                    }
                    else
                        MIDIBeast.originalBassNotes = roundedNoteArray;
                    }
                catch(ArrayIndexOutOfBoundsException e) {
                        if(debug) System.out.println("An array index out of bounds exception was raised");
                        MIDIBeast.addError("The bass part is corrupted.  Go to Generate-->" +
                            "Preferences for Generation to choose a different bass part from available instruments.");
                        canContinue = false;	
                }
            }
        }
	
	/**
	 * constructor.  Reads a score and adjusts the rhythm durations
	 * in the bass line for the Part specifiedPart
	 * @param specifiedPart
	 */
	public ImportBass(jm.music.data.Part specifiedPart){
		MIDIBeast.bassPart = specifiedPart;
		execute();
	}
        
       /**
         * @param startMeasure where the imported bass should start
         * If the user wishes to not include a beginning part of the song
         * they may specify how many measures to skip in the beginning
         */
        public ImportBass(double startBeat){
            noteArray = new ArrayList<jm.music.data.Note>();
            roundedNoteArray = new ArrayList<SlottedNote>();
            createBassPart();
            mainPhrase = MIDIBeast.bassPart.getPhraseArray()[0];
            if(debug){
                System.out.println("## Before changing start position ##");
                System.out.println(mainPhrase);
            }
            setPhraseStartAndEnd(startBeat,0);
            getNoteArray();
            roundDurations(MIDIBeast.getResolution());
		
            MIDIBeast.originalBassNotes = roundedNoteArray;
        }
        
       /**
         * @param startMeasure
         * @param selectedPart
         * Constructor used when the start measure and part are specified
         * by the user
         */
        public ImportBass(double startBeat, jm.music.data.Part selectedPart){
            noteArray = new ArrayList<jm.music.data.Note>();
            roundedNoteArray = new ArrayList<SlottedNote>();
            MIDIBeast.bassPart = selectedPart;
            mainPhrase = MIDIBeast.bassPart.getPhraseArray()[0];
            setPhraseStartAndEnd(startBeat,0);
            getNoteArray();
            roundDurations(MIDIBeast.getResolution());
		
            MIDIBeast.originalBassNotes = roundedNoteArray;
        }
        
        public ImportBass(double startBeat, double endBeat){
            noteArray = new ArrayList<jm.music.data.Note>();
            roundedNoteArray = new ArrayList<SlottedNote>();
            createBassPart();
            mainPhrase = MIDIBeast.bassPart.getPhraseArray()[0];
            if(debug){
                System.out.println("## Before changing start position ##");
                System.out.println(mainPhrase);
            }
            setPhraseStartAndEnd(startBeat, endBeat);
            getNoteArray();
            roundDurations(MIDIBeast.getResolution());
		
            MIDIBeast.originalBassNotes = roundedNoteArray;
        }
                
        public ImportBass(double startBeat, double endBeat, jm.music.data.Part selectedPart){
            noteArray = new ArrayList<jm.music.data.Note>();
            roundedNoteArray = new ArrayList<SlottedNote>();
            MIDIBeast.bassPart = selectedPart;
            mainPhrase = MIDIBeast.bassPart.getPhraseArray()[0];
            if(debug){
                System.out.println("## Before changing start position ##");
                System.out.println(mainPhrase);
            }
            setPhraseStartAndEnd(startBeat, endBeat);
            getNoteArray();
            roundDurations(MIDIBeast.getResolution());
		
            MIDIBeast.originalBassNotes = roundedNoteArray;
        }
        
       /**
         * @param startMeasure
         * @param endMeasure
         * This method chops off the beginning and end of the main phrase
         * to match the user selected start and end measures
         */
        public void setPhraseStartAndEnd(double startBeat, double endBeat){
            jm.music.data.Note[] noteArray = mainPhrase.getNoteArray();
            double beatCount = 0;
            int noteIndex = 0, endNoteIndex = 0, startNoteIndex = 0; 
            if(endBeat == 0) endBeat = mainPhrase.getEndTime();
            boolean start = false;
            while(beatCount < endBeat && noteIndex < noteArray.length){
                beatCount += noteArray[noteIndex].getRhythmValue();
                if(beatCount > startBeat && !start){
                    double remainder = beatCount - startBeat;
                    noteArray[noteIndex] = new jm.music.data.Note(noteArray[noteIndex].getPitch(), remainder);
                    startNoteIndex = noteIndex;
                    start = true;
                }
                noteIndex++;
            }
            endNoteIndex = noteIndex;
            jm.music.data.Note[] newNoteArray = new jm.music.data.Note[endNoteIndex - startNoteIndex];
            for(int i = startNoteIndex, j = 0; i < endNoteIndex; i++, j++)
                newNoteArray[j] = noteArray[i];
            mainPhrase = new Phrase(startBeat);
            mainPhrase.addNoteList(newNoteArray);
             if(debug){
             System.out.println("## After setPhraseStartAndEnd() ##");
             System.out.println(mainPhrase);
            }
            
        }
        

      /**
	* Finds the first bass instrument and returns the part
	* 
	* NOTE: This method could be a lot more intelligent, such as
	* find the longest bass part or "the most likely bass part".
        */
	public void createBassPart(){
		jm.music.data.Part[] parts = MIDIBeast.score.getPartArray();
		for(int i = 0; i < parts.length; i++){
			int currentInstrument = parts[i].getInstrument();
			if(currentInstrument >= 32 && currentInstrument <= 38){
				MIDIBeast.bassPart = parts[i];
				return;
			}
		}
		MIDIBeast.bassPart = null;
                MIDIBeast.addError("Could not find a bass part.");
                // Doesn't exist? Need to be able to handle an empty bass part
                //Go to Generate-->" +
                //        "Preferences for Generation to choose a bass part from available instruments.");
		canContinue = false;	
                if(debug)System.out.println("Unable to find a bass part");
	}
	
	/**
	 * Reads the notes of the main phrase into noteArray
	 */
	public void getNoteArray(){
		jm.music.data.Note[] notes = mainPhrase.getNoteArray();
		for(int i = 0; i < notes.length; i++)
			noteArray.add(notes[i]);
		
	}
	
        public void mergeRests(){
            for(int i = 1; i < noteArray.size(); i ++){
                if(!(noteArray.get(i).isRest())) continue;
                noteArray.get(i-1).setRhythmValue(noteArray.get(i).getRhythmValue() + noteArray.get(i-1).getRhythmValue());
                noteArray.remove(i);
                i--;
            }
        }
	/**
	 * Rounds the rhythm duration of each note to its closest musical value
	 * equivalent using a 120 slots per beat format.
	 * @param precision
	 */
	public void oldRoundDurations(int precision){
		for(int i = 0; i < noteArray.size(); i++){
			int numberOfSlots = MIDIBeast.doubleValToSlots(noteArray.get(i).getRhythmValue());
			String pitch = MIDIBeast.pitchOf(noteArray.get(i).getPitch());
			SlottedNote toBeAdded = new SlottedNote(numberOfSlots, pitch);
			roundedNoteArray.add(toBeAdded);
		}
		
		if(debug) {
			System.out.println("## After roundDurations() ##");
			int totalNoteDuration = 0;
                        for(int i = 0; i < roundedNoteArray.size(); i++) {
				System.out.println(roundedNoteArray.get(i));
                                totalNoteDuration += roundedNoteArray.get(i).getNumberOfSlots();
			}
                        System.out.println("Total note duration: " + totalNoteDuration);
                                 
		}
	}
        
/**
 * New version modeled after noteArray2ImpPart in ImportMelody.
 * @param precision 
 */       
        
public void roundDurations(int precision)
  {
    int BEAT = 120;
    double time = 0;
    int slot = 0;
    for( jm.music.data.Note note : noteArray ) 
      {
        double origRhythmValue = note.getRhythmValue();
        int rhythmValue;
        SlottedNote toBeAdded;
         if( note.isRest() )
          {
          rhythmValue = precision*(int)((BEAT*(time + origRhythmValue) - slot)/precision);
	  toBeAdded = new SlottedNote(rhythmValue, "r");
         }
        else
          {
          String pitch = MIDIBeast.pitchOf(note.getPitch());
          rhythmValue = precision*(int)Math.round((BEAT * origRhythmValue) / precision);
          toBeAdded = new SlottedNote(rhythmValue, pitch);
          }
        roundedNoteArray.add(toBeAdded);
        slot += rhythmValue;
        time += origRhythmValue;
      }  
  }        
}