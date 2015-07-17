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
import java.util.ArrayList;
import jm.music.data.Phrase;

/**
* Brandy McMenamy and James Thomas Herold
* 7/18/2007
* Takes the jMusic Part object that corresponds to the drums of a particular song
* and turns it into an ArrayList of Strings, each of which closely resembles an improvisor drum style rule.
*/

public class DrumPatternExtractor{
	private boolean debug = false;
        public boolean canContinue = true;
	
	/**
	* The number of different durm pieces found from all of the rules
	*/
	private ArrayList<Integer> uniqueDrumNumbers;
	
	/**
	* Puts the Phrase Arrays and Note Arrays that jMusic constructs into an 
	* ArrayList for ease of processing later
	*/
	private ArrayList<ArrayList<SlottedNote>> drumNoteArray;
	
	/**
	* Each rule to be generated will span the length of one measure.
	* The notes of the phrases are taken and divided into each measure in which
	* they are played so that rules may be obtained for each individual measure
	*/
	private ArrayList<Measure> measures;
	
	/**
	* This class, MeasurePattern, holds an ArrayList of Rule objects, where each Rule contains
	* an individual improvisor stlye rule for each instrument within the measure
	*/
	private ArrayList<MeasurePattern> measurePatterns;
	
	/**
	* This ArrayList is a number of strings, each of which resemebles a complete drum rule in 
	* improvisor. Each one spans a measures length and contains an element for
	* every instrument that is in the song, even if the instrument does not do anything
	* in this measure. This is so that each rule may be easily checked against other rules
	* as Strings in order to find duplicates.
	*/
	private ArrayList<String> drumPatterns;
	
	/**
	* @param Part the durm part to be processed
	*/
	public DrumPatternExtractor(){
		processDrums();
		createMeasures();
		getMeasurePatterns();
		getDrumPatterns();
	}
	
        /**
         * @return drumPatterns
         **/
	public ArrayList<String> getPatterns(){
		return drumPatterns;
	}
        
        /**
         * @return number of instruments
         **/
	public int getNumberOfInstruments(){
		return uniqueDrumNumbers.size();
	}
	 
	/**
	* This is simply wrapper method to group together all methods that 
	* deal with manipulating the notes and phrases contained in the original
	* jMusic part object. 
	*/
	public void processDrums(){
		createDrumNoteArray();
                if(canContinue) {
                    processDrumNotes();
                    equalizePhrases();
                }
	}
	
	/**
	* One of the methods in the processDrums() group. Jmusic Part objects
	* consist of an array of Phrases which consist of an array of Notes. This
	* method turns both of those arrays into ArrayLists and changes the notes
	* into slotted notes, in which durations are rounded to a certain precision
	* as determined in MIDIBeast
	*/
	public void createDrumNoteArray(){
		drumNoteArray = new ArrayList<ArrayList<SlottedNote>>();
		Phrase[] p = MIDIBeast.drumPart.getPhraseArray();
                if(p.length == 0) {
                    canContinue = false;
                     MIDIBeast.addError("The drum part is corrupted...no notes were found.  Go to Generate-->" +
                        "Preferences for Generation to choose a different chord part from available instruments.");
                    return;
                }
                canContinue = false; //Changes back to true if at least one note is found in the drum part
		for(int i = 0; i < p.length; i++){
			drumNoteArray.add(new ArrayList<SlottedNote>());
			jm.music.data.Note[] n = p[i].getNoteArray();
			for(int j = 0; j < n.length; j++){
                                canContinue = true;
				int pitch = n[j].getPitch();
				int numberOfSlots = MIDIBeast.doubleValToSlots(n[j].getRhythmValue());
				drumNoteArray.get(i).add(new SlottedNote(numberOfSlots, pitch));
			}
                }
                if(!canContinue) {
                    MIDIBeast.addError("The drum part is corrupted...no notes were found.  Go to Generate-->" +
                        "Preferences for Generation to choose a different chord part from available instruments.");
                }
                
		if(debug){
			System.out.println("## After createDrumNoteArray() ##");
			for(int i = 0; i < drumNoteArray.size(); i++){
				System.out.println("\t Phrase(" + i + ")");
				for(int j = 0; j < drumNoteArray.get(i).size(); j++)
					System.out.println("\t\t" + drumNoteArray.get(i).get(j));
			}
		}
	}
	
	/**
	* In jMusics method of reading in MIDI, most rests generated
	* have been found to actually be a reamainder duration of the note preceeding it. 
	* For this reason we go through EVERY rest, assign its rhythm value to the note before
	* it and then remove the rest. This method causes minimal issues for drums when actual rests
	* are found and a method for determining 'real' rests is underway. 
	* This method also keeps track of how many different drum instruments are contained within
	* the song and assigns their numbers to an ArrayList.
	*/
	public void processDrumNotes(){
		uniqueDrumNumbers = new ArrayList<Integer>();
		for(int i = 0; i < drumNoteArray.size(); i++){
			ArrayList<SlottedNote> notes = drumNoteArray.get(i);
			for(int j = 0; j < notes.size(); j++){
				SlottedNote currentNote = notes.get(j);
				if(j+1 < notes.size() && notes.get(j+1).getPitchNumber() < 0){
					SlottedNote nextNote = notes.get(j+1);
					notes.set(j, new SlottedNote(currentNote.getNumberOfSlots() + nextNote.getNumberOfSlots(), currentNote.getPitchNumber()));
					notes.remove(j+1);
				}
				if(!uniqueDrumNumbers.contains(currentNote.getPitchNumber()))
					uniqueDrumNumbers.add(currentNote.getPitchNumber());
			}
		}
		if(debug){
			System.out.println("## After processDrumNotes() ##");
			for(int i = 0; i < drumNoteArray.size(); i++){
				System.out.println("\t Phrase(" + i + ")");
				for(int j = 0; j < drumNoteArray.get(i).size(); j++)
					System.out.println("\t\t" + drumNoteArray.get(i).get(j));
			}
		}
	}
	
	/**
	* Each of the Phrases of a jMusic Part potentially have different start and end times.
	* This method appends rests to the beginning and end of each phrases in order to make 
	* them all the same length to aid in comparing them later.
	*/
	public void equalizePhrases(){
		for(int i = 0; i < MIDIBeast.drumPart.getPhraseArray().length; i++){
			Phrase currentPhrase = MIDIBeast.drumPart.getPhraseArray()[i];
                        int equalizingRhythm = MIDIBeast.doubleValToSlots(currentPhrase.getStartTime() - MIDIBeast.drumPart.getPhraseArray()[0].getStartTime());
                        if(equalizingRhythm != 0)
                        	drumNoteArray.get(i).add(0, new SlottedNote(equalizingRhythm, Integer.MIN_VALUE)); 
                        equalizingRhythm = MIDIBeast.doubleValToSlots(MIDIBeast.drumPart.getPhraseArray()[0].getEndTime() - MIDIBeast.drumPart.getPhraseArray()[i].getEndTime());
			if(equalizingRhythm != 0)
				drumNoteArray.get(i).add(drumNoteArray.get(i).size(), new SlottedNote(equalizingRhythm, Integer.MIN_VALUE));
		}
		if(debug){
			System.out.println("## After equalizePhrases() ##");
			for(int i = 0; i < drumNoteArray.size(); i++){
				System.out.println("\t Phrase(" + i + ")");
				for(int j = 0; j < drumNoteArray.get(i).size(); j++)
					System.out.println("\t\t" + drumNoteArray.get(i).get(j));
			}
		}
				
	}
	
	
	/**
	* The Phrases in a drum Part are meant to be played in parrallel, not in sequence
	* For that reason, each Phrase is broken up into a measure. The notes from all 
	* Phrases in the same measure are then put together into one Measure object.
	*/
public void createMeasures()
  {
    int drumMeasureSize = MIDIBeast.drumMeasureSize;
    measures = new ArrayList<Measure>();
    for( int i = 0; i < drumNoteArray.size(); i++ )
      {
        ArrayList<SlottedNote> currentPhrase = drumNoteArray.get(i);
        int measureNumber = 0;
        int slotsSoFar = 0;
        ArrayList<SlottedNote> notes = new ArrayList<SlottedNote>();
        for( int j = 0; j < currentPhrase.size(); j++ )
          {
            SlottedNote currentNote = currentPhrase.get(j);
            slotsSoFar += currentNote.getNumberOfSlots();
            if( slotsSoFar < drumMeasureSize )
              {
                notes.add(currentNote);
              }
            else
              {
                if( slotsSoFar > drumMeasureSize )
                  {
                    currentPhrase.add(j + 1, new SlottedNote(slotsSoFar - drumMeasureSize, currentNote.getPitchNumber()));
                    currentNote.setNumberOfSlots(currentNote.getNumberOfSlots() - (slotsSoFar - drumMeasureSize));
                  }
                notes.add(currentNote);
                if( measureNumber >= measures.size() ) // was i == 0 )
                  {
                    measures.add(new Measure());
                  }
                measures.get(measureNumber++).addNotes(notes);
                slotsSoFar = 0;
                notes = new ArrayList<SlottedNote>();
              }
          }
      }
    if( debug )
      {
        System.out.println("## After createMeasures() ##");
        for( int i = 0; i < measures.size(); i++ )
          {
            System.out.println(measures.get(i));
          }
      }
  }

	
	/**
	* and comes up with a improvisor rule for what the instrument is doing in the measure.
	* This method iterates through each measure once for each instrument in the entire song.,
	*/
	public void getMeasurePatterns(){
		measurePatterns = new ArrayList<MeasurePattern>();
		for(int i = 0; i < measures.size(); i++){
			measurePatterns.add(new MeasurePattern(uniqueDrumNumbers));
			Measure currentMeasure = measures.get(i);
			for(int j = 0; j < uniqueDrumNumbers.size(); j++){
				int currentDrumNumber = uniqueDrumNumbers.get(j);
                                
                                if(currentDrumNumber == -2147483648) continue;
                                
                                for(int k = 0; k < currentMeasure.getNotesInMeasure().size(); k++){
					ArrayList<SlottedNote> currentNoteArray = currentMeasure.getNotesInMeasure().get(k);
					int currentSlotCount = 0; 
					for(int l = 0; l < currentNoteArray.size(); l++){
						SlottedNote currentNote = currentNoteArray.get(l);
						if(currentNote.getPitchNumber() == currentDrumNumber)
							measurePatterns.get(i).markAsHit(currentSlotCount, currentDrumNumber);
						currentSlotCount += currentNote.getNumberOfSlots();
					}
				}
			}
		}
		if(debug){
			System.out.println("## After getMeasurePatterns() ##");
			for(int i = 0; i < measurePatterns.size(); i++){
				System.out.print("        ");
				for(int j = 0; j < MIDIBeast.drumMeasureSize; j++)
					System.out.print(j + " ");
				System.out.println("\n" + measurePatterns.get(i));
				
			}
		}
	}
	
	
	/**
	* The form that the rules are in in the MeasurePattern object are not as similar
	* to improvisor as would be liked. This method processes each rule and formats 
	* in such a way that it will be easier to process in the RepresentativeDrumRules class
	*/
	public void getDrumPatterns(){
		drumPatterns = new ArrayList<String>();	
		for(int i = 0; i < measurePatterns.size(); i++){
			MeasurePattern currentPattern = measurePatterns.get(i);
			String s = "";
			for(int j = 0; j < currentPattern.getRules().size(); j++){
				Rule currentRule = currentPattern.getRules().get(j);
				s += "(" + currentRule.getDrumNumber() + ")";
				
				ArrayList<String> temp = new ArrayList<String>();
				int rsInARow = 0;
				for(int k = 0; k < currentRule.getRules().length; k++){
					if(currentRule.getRules()[k] == 'X'){
						if(rsInARow > 0){
							temp.add("R" + rsInARow + " ");
							rsInARow = 0;
						}
						temp.add("X1 ");
					}
					else{
						rsInARow++;
						if(k == currentRule.getRules().length-1)
							temp.add("R" + rsInARow + " ");
					}
				}	
				//In the following for loop, rests are assimilated by hits that preceed them
				//In improvisor X8 R8 is the same as X4 and by making this change it is
				//easier to compare rules later on.
				for(int k = 0; k < temp.size(); k++){
					if(k != temp.size()-1 && temp.get(k).charAt(0) == 'X' && temp.get(k+1).charAt(0) == 'R'){
						int xDuration = Integer.parseInt(temp.get(k).substring(1, temp.get(k).length() - 1));
						int rDuration = Integer.parseInt(temp.get(k+1).substring(1, temp.get(k+1).length() - 1));
						String newValue = MIDIBeast.stringDuration(xDuration + rDuration);
						temp.set(k, temp.get(k).charAt(0) + newValue + " ");
						temp.remove(k+1);
					}
					else{
						int duration = Integer.parseInt(temp.get(k).substring(1, temp.get(k).length() - 1));
						String newValue = MIDIBeast.stringDuration(duration);
						temp.set(k, temp.get(k).charAt(0) + newValue + " ");
					}
					s += temp.get(k);
				}
				s += "\n";
			}
			if(debug){
				System.out.println(s);
			}
			drumPatterns.add(s);
		}
	}

	private class Measure{
		ArrayList<ArrayList<SlottedNote>> notesInMeasure;
	
		public Measure(){
			notesInMeasure = new ArrayList<ArrayList<SlottedNote>>();
		}
		
		public ArrayList<ArrayList<SlottedNote>> getNotesInMeasure(){
			return notesInMeasure;
		}
		
		public void addNotes(ArrayList<SlottedNote> notes){
			notesInMeasure.add(notes);
		}
		
		public String toString(){
			String s = "MEASURE";
			for(int i = 0; i < notesInMeasure.size(); i++){
				s += " \n";
				for(int j = 0; j < notesInMeasure.get(i).size(); j++)
					s += notesInMeasure.get(i).get(j).getPitchNumber() + "(" + notesInMeasure.get(i).get(j).getNumberOfSlots() + ") ";
			}
			return s;
		}
	}
	
	private class MeasurePattern{
		ArrayList<Rule> rules;
		
		public MeasurePattern(ArrayList<Integer> uniqueDrumNumbers){
			rules = new ArrayList<Rule>();
			for(int i = 0; i < uniqueDrumNumbers.size(); i++){
				if(uniqueDrumNumbers.get(i) == -2147483648) continue;
                                rules.add(new Rule(uniqueDrumNumbers.get(i)));
                        }
		}
		
		public ArrayList<Rule> getRules(){
			return rules;
		}
		
		public void markAsHit(int slot, int drumNumber){
			for(int i = 0; i < rules.size(); i++)
				if(rules.get(i).getDrumNumber() == drumNumber)
					rules.get(i).markAsHit(slot);
		}
	
                @Override
		public String toString(){
			String s = "";
			for(int i = 0; i < rules.size(); i++)
				s += rules.get(i) + "\n";
			return s;
		}
	
	}
	
	private class Rule{
		private int drumNumber;
		private char[] rules;
		
		public Rule(int drumNumber){
			this.drumNumber = drumNumber;
			rules = new char[MIDIBeast.drumMeasureSize];
		}
		
		public int getDrumNumber(){
			return drumNumber;
		}
		
		public char[] getRules(){
			return rules;
		}
		
		public char getRule(int i){
			return rules[i];
		}
		
		public void markAsHit(int index){
			rules[index] = 'X';
		}
		
                @Override
		public String toString(){
			String s = "[ " + drumNumber + " ][ ";
			for(int i = 0; i < rules.length; i++){
				if(rules[i] == 'X')
					s += "X  ";
				else
					s += "R  ";
			}
			return s;
		}
	}
}