/*
  
<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

package jm.music.data;

/**
* This meta structure allows for the entry of chord sequences.
* The class hides the fact that these chords are really just an
* amalgam of standard Phrase objects.
* @author Andrew Sorensen and Andrew Brown
 * @version 1.0,Sun Feb 25 18:43:30  2001
*/

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;

public class CPhrase implements JMC, Cloneable, Serializable{
	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	/** Phrase list */
	private Vector phraseList;
	/** the current rhythm value time of the phraseList */
	private double currentTime;
	/** the CPhrase's startTime */
	private double startTime;
	/** the CPhrase's title */
	private String title;
	/** Program change to use for this CPhrase */
	private int instrument = NO_INSTRUMENT;
	/** Setting the phrase to append when added to a part
	* rather than use its start time.
	*/
	private boolean append = false;
	/** A phrase to have a relative start time with if required. */
    private Phrase linkedPhrase;
    /** The pan position for all notes in this CPhrase. */
    private double pan = Note.DEFAULT_PAN;
    /** The speed of the CPhrase */
    private double tempo = Phrase.DEFAULT_TEMPO;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	* Default Constructor
	* the append flag is true which means the phrase will be
	* appended to the end of any part it is added to.
	*/
	public CPhrase(){
		this( "Untitled CPhrase", 0.0, NO_INSTRUMENT, true);
	}
	/**
	* Constructor with start time
	*/
	public CPhrase(double startTime){
		this( "Untitled CPhrase", startTime, NO_INSTRUMENT, false);
	}
	
	/**
	* Constructor with start time
	* the append flag is true which means the phrase will be
	* appended to the end of any part it is added to.
	*/
	public CPhrase(String title){
		this(title, 0.0, NO_INSTRUMENT, true);
	}
	
	/**
	* Constructor with title and start time
	*/
	public CPhrase(String title, double startTime){
		this(title, startTime, NO_INSTRUMENT, false);
	}
	
	/**
	* Constructor with progChg
	*/
	public CPhrase(double startTime, int instrument){
		this("Untitled CPhrase", startTime, instrument, false);
	}
	
	/**
	* Constructor with title, startTime, instrument
	*/
	public CPhrase(String title, double startTime, int instrument){
	    this(title, startTime, instrument, false);
	}
	
	/**
	* Constructor with everything
	*/
	public CPhrase(String title, double startTime, int instrument, boolean append){
		this.title = title;
		this.startTime = startTime;
		this.currentTime = startTime;
		this.instrument = instrument;
		this.append = append;
		this.phraseList = new Vector();
	}
	
	//----------------------------------------------
	// Accessor Methods
	//----------------------------------------------
	/**
	* Get Phrase List
	* @return Vector
	*/
	public Vector getPhraseList(){
		return this.phraseList;
	}

	/**
	* Set Phrase List
	* @param phraseList
	*/
	public void setPhraseList(Vector phraseList){
		this.phraseList = phraseList;
	}

	/**
	 * Add a phrase to this CPhrase
	 * If a phrase has a 'true' append flag then it is assumed
	 * that the appending is vertical for chords. So the
	 * start time is made to be equal to the CPhrase's start time.
	 * @param Phrase phrase - add a phrase to this CPhrase
	 */
	public void addPhrase(Phrase phrase){
	    // check  the append status
	    if(phrase.getAppend()) phrase.setStartTime(this.startTime);
	    // check if the startTime is before the CPhrase start

		if (phrase.getStartTime() >= this.startTime) {
		    phraseList.addElement(phrase);
		} else System.err.println("Phrase to added to CPhrase: Phrases added" +
		        " to a CPhrase must have a start time at ot after the CPhrase start time.");
	}

    /**
	 * Deletes the first occurence of the specified phrase in the CPhrase
	 * @param phrase  the Phrase object to be deleted.
	 */
	 public void removePhrase(Phrase phrase) {
        this.phraseList.removeElement(phrase);
    }

	/**
	* Add a new Chord
	* @param pitchArray short[]
	* @param rhythmValue double
	*/
	public void addChord(int[] pitchArray, double rhythmValue){
		this.addChord(pitchArray,rhythmValue,MF);
	}

	/**
	* Add a new Chord
	* @param pitchArray short[]
	* @param rhythmValue short
	* @param dynmaic short
	*/
	public void addChord(int[] pitchArray, double rhythmValue, int dynamic){
		//this.currentTime = this.getEndTime();
		//If we have more notes in the chord than we have available phrases
		//we will need to make more phrases
		if(this.phraseList.size() < pitchArray.length){
			int num = pitchArray.length - this.phraseList.size();
			for(int i=0;i<num;i++){
				Phrase phr = new Phrase(this.getEndTime(), this.instrument);
				//phr.addNote(new Note(REST,currentTime,SILENT));
				this.phraseList.addElement(phr);
			}
		}
		int i = 0;
        // add notes
		for(;i<pitchArray.length;i++){
			Note newNote = new Note(pitchArray[i],rhythmValue,dynamic);
			((Phrase)this.phraseList.elementAt(i)).addNote(newNote);
		}
        // pad remaining phrases with rests
		for(;i<phraseList.size();i++){
			Note newNote = new Note(REST, rhythmValue);
			((Phrase)this.phraseList.elementAt(i)).addNote(newNote);
		}
		//this.currentTime += rhythmValue;
	}
	
	/**
	 * checks against the phrase list for a particular note
	 * @param note
	 * @return
	 */
	public boolean hasNote(Note note){
	    for (int i = 0; i < phraseList.size(); i++) {
	        Phrase phr = (Phrase)phraseList.get(i);
	        Note[] notes = phr.getNoteArray();
            for (int j = 0; j < notes.length; j++) {
                Note n = (Note)notes[j];
                if(note.getNote().equals(n.getNote()))
                    return true;
            }
            }
	    return false;
	}
	
	/**
	* Add a new Chord usfroming an array of note objects
	* It assumes the notes have equal rhythmValues, if not the
	* rv of the first note is used.
	* @param noteArray Note[]
	*/
	public void addChord(Note[] noteArray){
		this.currentTime = this.getEndTime();
		double rhythmValue = noteArray[0].getRhythmValue();
		//If we have more notes in the chord than we have available phrases
		//we will need to make more phrases
		if(this.phraseList.size() < noteArray.length){
			int num = noteArray.length - this.phraseList.size();
			for(int i=0;i<num;i++){
				Phrase phr = new Phrase(this.getEndTime(), this.instrument);
				//phr.addNote(new Note(REST,currentTime,SILENT));
				this.phraseList.addElement(phr);
			}
		}
		int i = 0;
		for(;i<noteArray.length;i++){
		    // enseure all notes have the same rhythmValue
		    noteArray[i].setRhythmValue(rhythmValue);
		    // add the notes tlo the phrases
			((Phrase)this.phraseList.elementAt(i)).addNote(noteArray[i]);
		}
		// fill extra phrases with a rest
		for(;i<phraseList.size();i++){
			Note newNote = new Note(REST,rhythmValue);
			((Phrase)this.phraseList.elementAt(i)).addNote(newNote);
		}
		this.currentTime += rhythmValue;
	}
		
	/**
	* Return the CPhrases startTime
	* @return double the CPhrases startTime
	*/
	public double getStartTime(){
		return this.startTime;
	}

	/**
	* Sets the CPhrases startTime
	* @param double the time at which to start the CPhrase
	*/
	public void setStartTime(double startTime){
		double startOffset = startTime - this.startTime;
		Enumeration enum1 = this.phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase ph;
			ph = (Phrase) enum1.nextElement();
			ph.setStartTime(ph.getStartTime() + startOffset);
		}
		this.startTime = startTime;
        append = false;
	}
	
	/**
	* Return the CPhrases endTime
	* @return double the CPhrases endTime
	*/
	public double getEndTime(){	
		double endTime = this.getStartTime();
		Enumeration enum1 = this.phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase nextPhr = (Phrase)enum1.nextElement();
			double phraseEnd = nextPhr.getEndTime();
			if (phraseEnd > endTime) endTime = phraseEnd;
		}
		return endTime;
	}
	
	/**
	 * Return this CPhrases title
	 * @return String the phrases title
	 */
	public String getTitle(){
		return this.title;
	}
	
	/**
	 * Gives the CPhrase a new title
	 * @param phrases title
	 */
	public void setTitle(String title){
		this.title = title;
	}
	
	/**
	 * Return this CPhrases instrument
	 * @return int the phrases instrumnet number
	 */
	public int getInstrument(){
		return this.instrument;
	}
	
	/**
	 * Gives the CPhrase a new instrument
	 * @param phrases instrument number
	 */
	public void setTitle(int instrument){
		if (instrument < NO_INSTRUMENT) this.instrument = instrument;
	}
	
	/**
	 * Return this phrases append status
	 * @return boolean the phrases append value
	 */
	public boolean getAppend(){
		return this.append;
	}
	
	/**
	 * Gives the Phrase a new append status
	 * @param boolean the append status
	 */
	public void setAppend(boolean append){
		this.append = append;
	}
	
	/**
	 * Return this phrases this phrase is linked to
	 * @return Phrase the phrases linked to
	 */
	public Phrase getLinkedPhrase(){
		return this.linkedPhrase;
	}
	
	/**
	 * Make a link from this phrase to another
	 * @param Phrase the phrase to link to
	 */
	public void setLinkedPhrase(Phrase link){
		this.linkedPhrase = link;
	}
	
	/**
	 * Return the pan position for this CPhrase
	 * @return double the CPhrases pan setting
	 */
	public double getPan(){
		return this.pan;
	}
	
	/**
	 * Determine the pan position for all notes in this CPhrase.
	 * @param double the CPhrase's pan setting
	 */
	public void setPan(double pan){
		this.pan = pan;
		Enumeration enum1 = phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
			phrase.setPan(pan);
	    }
	}

    /**
	 * Return the value of the highest note in the cphrase.
     */
    public int getHighestPitch() {
        int max = 0;
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            if(phrase.getHighestPitch() > max) max = phrase.getHighestPitch();
        }
        return max;
    }

    /**
	 * Return the value of the lowest note in the cphrase.
     */
    public int getLowestPitch() {
        int min = 127;
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            if(phrase.getLowestPitch() < min) min = phrase.getLowestPitch();
        }
        return min;
    }

    /**
	 * Return the value of the longest rhythm value in the cphrase.
     */
    public double getLongestRhythmValue() {
        double max = 0.0;
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            if(phrase.getLongestRhythmValue() > max) max = phrase.getLongestRhythmValue();
        }
        return max;
    }

    /**
	 * Return the value of the shortest rhythm value in the cphrase.
     */
    public double getShortestRhythmValue() {
        double min = 1000.0;
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            if(phrase.getShortestRhythmValue() < min) min = phrase.getShortestRhythmValue();
        }
        return min;
    }

	
	//----------------------------------------------
	// UTILITIES
	//----------------------------------------------
	

	/**
	* Returns a copy of the CPhrase
	* @return CPhrase a copy of the CPhrase
	*/
	public CPhrase copy(){
		CPhrase cp;
		Vector tempVect = new Vector();
		cp = new CPhrase( this.title + " copy", this.startTime, this.instrument);
		Enumeration enum1 = this.phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase ph = ((Phrase) enum1.nextElement()).copy();
			tempVect.addElement(ph);			
		}
		cp.setPhraseList(tempVect);
        cp.setAppend(this.append);
        cp.setLinkedPhrase(this.linkedPhrase);
		return (CPhrase)cp;
	}
	
	/**
	* Returns a copy of the CPhrase between specified loactions
	* @param double start of copy section in beats
	* @param double end of copy section in beats
	* @return CPhrase a copy of the CPhrase
	*/
	public CPhrase copy(double startLoc, double endLoc){
	    CPhrase cp;
		Vector tempVect = new Vector();
		cp = new CPhrase(this.title + " copy", startLoc, this.instrument);
		Enumeration enum1 = this.phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase ph = ((Phrase) enum1.nextElement()).copy(startLoc, endLoc);
			ph.setStartTime(0.0);
			tempVect.addElement(ph);			
		}
		cp.setPhraseList(tempVect);
        cp.setAppend(this.append);
        cp.setLinkedPhrase(this.linkedPhrase);
		return (CPhrase)cp;
	}
		
	/**
	* Empty removes all elements in the vector
	*/
	public void empty(){
		phraseList.removeAllElements();
	}
	
    /**
	 * Collects the CPhrase attributes to a string
	 */
	public String toString(){
		String cphraseData = new String("---- jMusic CPHRASE: '" + title +
                    "' Start time: " + startTime + " ----" +'\n');
		Enumeration enum1 = phraseList.elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
			cphraseData = cphraseData + phrase.toString() + '\n';
		}
		return cphraseData;
	}

    /**
	 * Change the dynamic value of each note in the CPhrase.
     */
    public void setDynamic(int dyn) {
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            phrase.setDynamic(dyn);
        }
    }

    /**
	 * Change the Pitch value of each note in the CPhrase.
     */
    public void setPitch(int val) {
        Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
            phrase.setPitch(val);
        }
    }

	/**
	* Change the rhythmValue value of each note in the CPhrase.
	*/
	public void setRhythmValue(int val) {
		Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
		phrase.setRhythmValue(val);
		}
	}
	
	/**
	* Change the duration value of each note in the CPhrase.
	*/
	public void setDuration(double val) {
		Enumeration enum1 = getPhraseList().elements();
		while(enum1.hasMoreElements()){
			Phrase phrase = (Phrase) enum1.nextElement();
		phrase.setDuration(val);
		}
	}

        /**
        * Changes the offset value of the notes in each phrase of the cphrase
        * to cause a strumming-like effect.
        */
        public void flam(){
            flam(0.05);
        }

        /**
        * Changes the offset value of the notes in each phrase of the cphrase
        * to cause a strumming-like effect.
        * @param offsetAmount The time (in beats) to use as the offset value (<0.1 recommended)
        */
        public void flam(final double offsetAmount){
            int phraseCounter = 0;
            Enumeration enum1 = this.phraseList.elements();
            while(enum1.hasMoreElements()){
                double currOffset = offsetAmount * phraseCounter;
                Phrase phr = (Phrase) enum1.nextElement();
                Enumeration enum2 = phr.getNoteList().elements();
                while(enum2.hasMoreElements()){
                    Note n = (Note) enum2.nextElement();
                    n.setOffset(currOffset);
                }
                phraseCounter++;
            }
        }

        /**
        * Specify the tempo this CPhrase.
        * Therefore of all the phrases within it.
        */
        public void setTempo(double val) {
            if(val > 0.0) {
                this.tempo = val;
                Enumeration enum1 = this.phraseList.elements();
                while(enum1.hasMoreElements()){
                    Phrase phr = (Phrase) enum1.nextElement();
                    phr.setTempo(val);
                }
            }
        }

        /**
        * Return the current tempo setting for this CPhrase.
        */
        public double getTempo() {
            return this.tempo;
        }
}