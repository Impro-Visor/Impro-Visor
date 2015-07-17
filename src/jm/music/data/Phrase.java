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

import jm.JMC;
import jm.util.*;
import jm.gui.cpn.Notate;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;

/**
* The Phrase class is representative of a single musical phrase.
 * Phrases are held in Parts and can be played at any time
 * based on there start times. They may be played sequentially or in parallel.
 * Phrases can be added to an Part like this...
 * <pre>
 *     Part inst = new Part("Flute");
 *     //Phrase for the right hand
 *     Phrase rightHand = new Phrase(0.0) //start this phrase on the first beat
 *     //Phrase for the left hand
 *     Phrase leftHane = new Phrase(4.0) //start this phrase on the fifth beat
 *     inst.addPhrase(rightHand);
 *     inst.addPhrase(leftHand);
 * </pre>
 * @see Note
 * @see Part
 * @author Andrew Sorensen and Andrew Brown
 * @version 1.0,Sun Feb 25 18:43:32  2001
 */

public class Phrase implements JMC, Cloneable, Serializable{
    //----------------------------------------------
    // Limits
    //-----------------------------------------------
    /** The smallest start time in beats */
    public static final double MIN_START_TIME = 0.0;

    //----------------------------------------------
    // Default constants
    //----------------------------------------------

    public static final String DEFAULT_TITLE = "Untitled Phrase";

    public static final double DEFAULT_START_TIME = MIN_START_TIME;

    public static final int DEFAULT_INSTRUMENT = NO_INSTRUMENT;

    public static final boolean DEFAULT_APPEND = false;

    public static final double DEFAULT_TEMPO = -1.0;

    public static final double DEFAULT_PAN = Note.DEFAULT_PAN;

    public static final int DEFAULT_NUMERATOR = 4;

    public static final int DEFAULT_DENOMINATOR = 4;

	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	/** An array containing mutiple voices */
	private Vector noteList;
	/** The title/name given to this phrase */
	private String title = "Unnamed Phrase";

    //	/** The phrases start time in beats */
    //	private double startTime;

    private Position position;
	
	/** instrumet / MIDI program change number for this phrase */
	private int instrument;
	/** speed in beats per minute for this phrase */
	private double tempo;
	/** Setting the phrase to append when added to a part
        * rather than use its start time.
        */
	private boolean append = false;
	/** A phrase to have a relative start time with if required. */
	private Phrase linkedPhrase;
	/** The pan position for notes in this phrase.
        * This must be set delibertley to override a note's pan position. */
	private double pan = DEFAULT_PAN;
	/** the top number of the time signature */
	private int numerator;
	/** the bottom number of the time signature */
	private int denominator;
	/** A reference to this phrases part */
	private Part myPart = null;
    /** Weather the phrase should play or not */
    private boolean mute = false;
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
        * Creates an empty Phrase.
	 * The default start time is a flag which means the phrase will be
	 * appended to the end of any part it is added to.
	 */
	public Phrase(){
		this(DEFAULT_START_TIME);
		this.append = true;
	}

	/**
        * Creates an empty Phrase starting at the specified beat.
	 * @param startTime The beat at which the phrase will be positioned in its part.
	 */
	public Phrase(double startTime){
		this(startTime, DEFAULT_INSTRUMENT);
	}

	/**
        * Creates an empty Phrase
	 * @param startTime The beat at which the phrase will be positioned in its part.
	 * @param instrument The sound or instrument number to be used for this phrase.
	 */
	public Phrase(double startTime, int instrument) {
		this(DEFAULT_TITLE, startTime, instrument);
	}
	
	/**
        * Creates an empty Phrase
	 * @param title The name for the phrase.
	 */
	public Phrase(String title){
		this(title, DEFAULT_START_TIME);
		this.append = true;
	}
	
	/**
        * Creates an empty Phrase.
	 * @param title The name for the phrase.
	 * @param startTime The beat at which the phrase will be positioned in its part.
	 */
	public Phrase(String title, double startTime){
		this(title, startTime, DEFAULT_INSTRUMENT);
	}
	
	/**
        * Creates an empty Phrase.
	 * @param title The name for the phrase.
	 * @param startTime The beat at which the phrase will be positioned in its part.
	 * @param instrument The sound or instrument number to be used for this phrase.
	 */
	public Phrase(String title, double startTime, int instrument){
		this(title, startTime, instrument, DEFAULT_APPEND);
	}
	
	/**
        * Creates an empty Phrase.
	 * @param title The name for the phrase.
	 * @param startTime The beat at which the phrase will be positioned in its part.
	 * @param instrument The sound or instrument number to be used for this phrase.
	 * @param append A flag specifying wheather or not this phrase should be added to the
	 * end of the part it is added too, or should use its start time value.
	 */
	public Phrase(String title, double startTime, int instrument, boolean append){
		this.title = title;
        //		this.startTime = startTime;
        this.position = new Position(startTime, this);
		this.append = append;
		if(instrument < NO_INSTRUMENT){
			System.err.println(new Exception("jMusic EXCEPTION: instrument " +
											 "value must be greater than 0"));
			(new Exception()).printStackTrace();
			System.exit(1); //crash ungracefully
		}
		this.instrument = instrument;
		this.noteList = new Vector();
		this.numerator = DEFAULT_NUMERATOR;
		this.denominator = DEFAULT_DENOMINATOR;
        this.tempo = DEFAULT_TEMPO;
	}

    /**
        * Constructs a new Phrase containing the specified <CODE>note</CODE>.
     *
     * @param note  Note to be containing by the phrase.
     */
    public Phrase(Note note) {
        this();
        addNote(note);
    }

    /**
        * Constructs a new Phrase containing the specified <CODE>notes</CODE>.
     *
     * @param notes array of Note to be contained by the phrase.
     */
    public Phrase(Note[] notes) {
        this();
        addNoteList(notes);
    }

    /**
        * Constructs a new Phrase containing the specified <CODE>note</CODE> with
     * the specified <CODE>title</CODE>.
     *
     * @param note  Note to be containing by the phrase.
     * @param title String describing the title of the Phrase.
     */
    public Phrase(Note note, String title) {
        this(title);
        addNote(note);
    }

    /**
        * Constructs a new Phrase containing the specified <CODE>notes</CODE> with
     * the specified <CODE>title</CODE>.
     *
     * @param notes array of Note to be contained by the phrase.
     * @param title String describing the title of the Phrase.
     */
    public Phrase(Note[] notes, String title) {
        this(title);
        this.addNoteList(notes);
    }

    /**
        * Constructs a new Phrase containing the specified <CODE>note</CODE> with
     * the specified <CODE>title</CODE>.
     *
     * @param note  Note to be containing by the phrase.
     * @param title String describing the title of the Phrase.
     */
    public Phrase(Note note, double startTime) {
        this(note);
        this.setStartTime(startTime);
    }

	//----------------------------------------------
	// Data Methods
	//----------------------------------------------

	/**
        * Return the program change assigned by this phrase
	 * @return int
	 */
	public int getInstrument(){
		return this.instrument;
	}

	/**
        * Sets the program change value
	 * @param int program change
	 */
	public void setInstrument(int value){
		this.instrument = value;
	}
	
	/**
        * Add a note to this Phrase
	 * @param Note note - add a note to this phrase
	 */
	public void addNote(Note note){
		note.setMyPhrase(this);
		noteList.addElement(note);
	}


    /**
        * Add a note to this Phrase
     * @param pitch -the pitch of the note
     *@param rv - the rhythmValue of the note
     */
    public void addNote(int pitch, double rv){
        Note note = new Note(pitch, rv);
        this.addNote(note);
    }

    /**
        * Add a note to this Phrase
	 * @param Note note - add a note to this phrase
	 */
	public void add(Note note){
		this.addNote(note);
	}

    /**
        * Add a rest to this Phrase
     * @param Rest rest - The rest to be added to this phrase
     */
    public void addRest(Rest newRest){
        newRest.setMyPhrase(this);
        noteList.addElement(newRest);
    }


    /**
        * Appends the specified notes to the end of this Phrase.
     *
     * @param   array of Notes to append.
     */
    public void addNoteList(Note[] notes) {
        for (int i = 0; i < notes.length; i++) {
            if( notes[i] != null )
              {
                this.addNote(notes[i]);
              }
        }
    }

	/**
        * Adds a vector of notes to the phrase.
	 * A boolean option when true appends the notes to the end of the list,
     * if false the notes in noteVector will replace the notes currently in the phrase.
	 * @param noteVector the vector of notes to add
	 * @param append do we append or not?
	 */
	public void addNoteList(Vector noteVector, boolean append){
		Enumeration enum1 = noteVector.elements();
		if(!append) this.noteList.removeAllElements();				
		while(enum1.hasMoreElements()){
			try{
				Note note = (Note) enum1.nextElement();
                this.addNote(note);
				//note.setMyPhrase(this);
			}catch(RuntimeException re){
				System.err.println("The vector passed to this method must " + "contain Notes only!");
			}
		}
	}

    /**
        * Adds an array of notes to the phrase.
     * A boolean option when true appends the notes to the end of the list.
     * @param noteArray the array of notes to add
     * @param append do we append or not?
     */
    public void addNoteList(Note[] noteArray, boolean append){
        if(!append) this.noteList.removeAllElements();
        for(int i=0;i<noteArray.length;i++){
            this.addNote(noteArray[i]);
        }
    }

    /**
        * Adds Multiple notes to the phrase from a pitch array and rhythm value
	 * @param pitchArray array of pitch values
	 * @param rhythmValue a rhythmic value
	 */
	public void addNoteList(int[] pitchArray, double rhythmValue){
		double[] rvArray = new double[pitchArray.length];
		for(int i=0;i<rvArray.length;i++){
			rvArray[i] = rhythmValue;
		}
		this.addNoteList(pitchArray, rvArray);
	}

    /**
        * Adds Multiple notes to the phrase from a pitch array, rhythm value, and dynmaic value
     * @param pitchArray - An array of pitch values
     * @param rhythmValue - A rhythmic value
     * @param dynamic - A dynmaic value (1-127)
     */
    public void addNoteList(int[] pitchArray, double rhythmValue, int dynamic){
        double[] rvArray = new double[pitchArray.length];
        int[] dynArray = new int[pitchArray.length];
        for(int i=0;i<rvArray.length;i++){
            rvArray[i] = rhythmValue;
            dynArray[i] = dynamic;
        }
        this.addNoteList(pitchArray, rvArray, dynArray);
    }

	/**
        * Adds Multiple notes to the phrase from an array of frequency values
	 * @param freqArray array of freequency values
	 * @param rhythmValue a rhythmic value
	 */
	public void addNoteList(double[] freqArray, double rhythmValue){
		double[] rvArray = new double[freqArray.length];
		for(int i=0;i<rvArray.length;i++){
			rvArray[i] = rhythmValue;
		}
		this.addNoteList(freqArray, rvArray);
	}
	


	/**
        * Adds Multiple notes to the phrase from several arrays
	 * @param pitchArray array of pitch values
	 * @param rhythmArray array of rhythmic values
	 */
	public void addNoteList(int[] pitchArray, double[] rhythmArray){
		int[] dynamic = new int[pitchArray.length];
		for(int i=0;i<pitchArray.length;i++){
			dynamic[i] = Note.DEFAULT_DYNAMIC;
		}
		this.addNoteList(pitchArray, rhythmArray, dynamic);
	}

	/**
        * Adds Multiple notes to the phrase from several arrays
	 * @param freqArray array of frequency values
	 * @param rhythmArray array of rhythmic values
	 */
	public void addNoteList(double[] freqArray, double[] rhythmArray){
		int[] dynamic = new int[freqArray.length];
		for(int i=0;i<freqArray.length;i++){
			dynamic[i] = Note.DEFAULT_DYNAMIC;
		}
		this.addNoteList(freqArray, rhythmArray, dynamic);
	}

	/**
        * Adds Multiple notes to the phrase from several arrays
	 * @param pitchArray array of pitch values
	 * @param rhythmArray array of rhythmic values
	 * @param dynmaic array of dynamic values
	 */
	public void addNoteList(int[] pitchArray, double[] rhythmArray,
							int[] dynamic){
		this.addNoteList(pitchArray, rhythmArray, dynamic, true);
	}

	/**
        * Adds Multiple notes to the phrase from several arrays
	 * @param freqArray array of frequency values
	 * @param rhythmArray array of rhythmic values
	 * @param dynmaic array of dynamic values
	 */
	public void addNoteList(double[] freqArray, double[] rhythmArray,
                            int[] dynamic){
		this.addNoteList(freqArray, rhythmArray, dynamic, true);
	}

	/**
        * Adds Multiple notes to the phrase from several arrays
	 * A boolean option when true appends the notes to the end of the list
	 * if non true the current list is errased and replaced by the new notes
	 * @param pitchArray array of pitch values
	 * @param rhythmArray array of rhythmic values
	 * @param dynamic int
	 * @param append do we append or not?
	 */
	public void addNoteList(int[] pitchArray, double[] rhythmArray,
							int[] dynamic, boolean append){
		if(!append) this.noteList.removeAllElements();
		for(int i=0;i<pitchArray.length;i++){
			try{
				Note knote = new Note(pitchArray[i],rhythmArray[i],dynamic[i]);
				this.addNote(knote);
			}catch(RuntimeException re){
				System.err.println("You must enter arrays of even length");
			}
		}
	}

	/**
        * Adds Multiple notes to the phrase from several arrays
	 * A boolean option when true appends the notes to the end of the list
	 * if non true the current list is errased and replaced by the new notes
	 * @param freqArray array of frequency values
	 * @param rhythmArray array of rhythmic values
	 * @param dynamic int
	 * @param append do we append or not?
	 */
	public void addNoteList(double[] freqArray, double[] rhythmArray,
                            int[] dynamic, boolean append){
		if(!append) this.noteList.removeAllElements();
		for(int i=0;i<freqArray.length;i++){
			try{
				Note knote = new Note(freqArray[i],rhythmArray[i],dynamic[i]);
				this.addNote(knote);
			}catch(RuntimeException re){
				System.err.println("jMusic Phrase error: You must enter arrays of even length");
			}
		}
	}
	
	/**
        * Adds Multiple notes to the phrase from one array of pitch, rhythm pairs
	 * @param pitchAndRhythmArray  - an array of pitch and rhythm values
	 */
	public void addNoteList(double[] pitchAndRhythmArray){
	    for(int i=0;i< pitchAndRhythmArray.length;i+=2){
	        try {
                Note knote = new Note((int)pitchAndRhythmArray[i],pitchAndRhythmArray[i+1]);
                this.addNote(knote);
            }catch(RuntimeException re) {
                System.err.println("Error adding note list: Possibly the wrong number of values in the pitch and rhythm array.");
            }
        }
    }

	/**
        * Adds Multiple notes to the phrase from one pitch and an array of rhythm values
	 *@param pitch The pitch values for the notes
	 * @param rhythms An array of rhythm values
	 */
	public void addNoteList(int pitch, double[] rhythms){
		for(int i=0; i<rhythms.length; i++) {
			this.addNote(new Note(pitch, rhythms[i]));
		}
	}

	/**
        * Adds Multiple notes to the phrase from one pitch and an array of rhythm values
	 *@param frequency The pitch values for the notes in hertz
	 * @param rhythms An array of rhythm values
	 */
	public void addNoteList(double frequency, double[] rhythms){
		for(int i=0; i<rhythms.length; i++) {
			this.addNote(new Note(frequency, rhythms[i]));
		}
	}
	

    /**
        * Adds Multiple notes to the phrase all of which start at the same time
     * and share the same duration.
     * @param pitches An array of pitch values
     * @param rv the rhythmValue
     */
    public void addChord(int[] pitches, double rv) {
        for(int i=0; i<pitches.length - 1; i++) {
            Note n = new Note(pitches[i], 0.0);
            n.setDuration(rv * Note.DEFAULT_DURATION_MULTIPLIER);
            this.addNote(n);
        }
        this.addNote(pitches[pitches.length - 1], rv);

        //System.out.println("In phrase" + this.toString());
    }

    public int[] getPitchArray(){
        Note[] notes = this.getNoteArray();
        int[] pitches = new int[notes.length];
        for (int i = 0; i < notes.length; i++) {
            pitches[i]=notes[i].getPitch();
        }
        return pitches;
    }

    public double[] getRhythmArray(){
        Note[] notes = this.getNoteArray();
        double[] rhythms = new double[notes.length];
        for (int i = 0; i < notes.length; i++) {
            rhythms[i]=notes[i].getRhythmValue();
        }
        return rhythms;
    }

    public int[] getDynamicArray(){
        Note[] notes = this.getNoteArray();
        int[] dynamics = new int[notes.length];
        for (int i = 0; i < notes.length; i++) {
            dynamics[i]=notes[i].getPitch();
        }
        return dynamics;
    }

	/**
        * Deletes the specified note in the phrase
	 * @param int noteNumb the index of the note to be deleted
	 */
    public void removeNote(int noteNumb) {
	    Vector vct = (Vector)this.noteList;
	    try{
	        vct.removeElement(vct.elementAt(noteNumb));
	    } catch (RuntimeException re){
			System.err.println("Note index to be deleted must be within the phrase.");
		}
	}

    /**
        * Deletes the first occurence of the specified note in the phrase
	 * @param note  the note object to be deleted.
	 */
    public void removeNote(Note note) {
        this.noteList.removeElement(note);
    }		

    /**
        * Deletes the last note in the phrase
	 */
    public void removeLastNote() {
	    Vector vct = (Vector)this.noteList;
	    vct.removeElementAt(vct.size()-1);
	}
	
	/**
        * Returns the entire note list contained in a single voice
	 * @return Vector A vector containing all Note objects in this phrase
	 */
	public Vector getNoteList(){
		return this.noteList;
	}
	
	/**
        * Replaces the entire note list with a new note list vector
	 * @param Vector of notes
	 */
	public void setNoteList(Vector newNoteList){
		this.noteList = newNoteList;
	}
	
	/**
        * Returns the all notes in the phrase as a array of notes
	 * @return Note[] An array containing all Note objects in this phrase
	 */
	public Note[] getNoteArray(){
		Vector vct = (Vector) this.noteList;
		Note[] noteArray = new Note[vct.size()];
		for(int i=0;i< noteArray.length;i++){
		    noteArray[i] = (Note) vct.elementAt(i);
		}
		return noteArray;
	}

    /**
    * Return the phrase's startTime
    * @return double The phrases startTime in beats from the beginning of the part or score.
    */
	public double getStartTime(){
        return position.getStartTime();
        //		return this.startTime;
	}


	/**
        * Sets the phrases startTime
     *
     * <p>This positions the phrase absolutely.  If this phrase is currently
     * positioned relative to another phrase that anchoring will be lost.
     *
     * <p>To position this relative to another class use the
     * <code>anchor</code> method instead.
     *
	 * @param double the time at which to start the phrase
	 */
	public void setStartTime(double startTime){
        if(startTime >= MIN_START_TIME){
            position.setStartTime(startTime);
            //            this.startTime = startTime;
            this.setAppend(false);
        }else{
            System.err.println("Error setting phrase start time value: You must enter values greater than "+MIN_START_TIME);
        }
	}

    /** <p>The positions tries the phrase relative to another using the
    * alignment specified.  If the arrangement causes this class to start
    * before a start time of 0.0, the repositioning is considered invalid
    * and will fail. The original positioning will be restored and this
    * method will return false.
    *
    * If successful, the previous positioning whether absolute or relative
    * will be lost.
    *
    * <p>To position this absolutely use the <code>setStartTime</code>
    * method instead.
    *
    * @param anchor    the phrase against which this should be positioned
    * @param alignment how this should be positioned relative to anchor
    * @returns         false if anchoring failed due to being positioned
    *                  before the 0.0 start time barrier.  True otherwise.
    */
	public boolean attemptAnchoringTo(final Phrase anchor,
                                      final Alignment alignment,
                                      final double offset) {
        Position newPosition = new Position(anchor.position, alignment,
                                            offset, this);
        if (newPosition.getStartTime() < 0.0) {
            return false;
        } else {
            position = newPosition;
            return true;
        }

    }

    /** Returns details of how this is aligned relative to another phrase.
    * Alternatively, if this phrase is aligned absolutely returns null.
    *
    * @returns null if aligned with setStartTime(), or details of alignment
    *          if aligned with attemptAnchoringTo()
    */
    public Anchoring getAnchoring() {
        return position.getAnchoring();
    }
	
	/**
    * Return the phrases endTime
	 * @return double the phrases endTime
	 */
	public double getEndTime(){	
	    double tempStartTime = (getStartTime() < MIN_START_TIME) ? MIN_START_TIME : getStartTime();
	    double endTime = tempStartTime;
		Enumeration enum1 = this.noteList.elements();
		while(enum1.hasMoreElements()){
			Note nextNote = (Note)enum1.nextElement();
			endTime += nextNote.getRhythmValue();
		}		
		return endTime;
	}

	/**
    * Returns the length of the whole phrase in beats.
     * @return double duration in beats
     */
	final double getTotalDuration(){	
	    double cumulativeLength = 0.0;
		Enumeration enum1 = this.noteList.elements();
		while(enum1.hasMoreElements()){
			Note nextNote = (Note)enum1.nextElement();
			cumulativeLength += nextNote.getRhythmValue();
		}		
		return cumulativeLength;
	}

    /**
    * Return this phrases title
	 * @return String the phrases title
	 */
	public String getTitle(){
		return this.title;
	}
	
	/**
    * Gives the Phrase a new title
	 * @param phrases title
	 */
	public void setTitle(String title){
		this.title = title;
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
    * Return the pan position for this phrase
	 * @return double the phrases pan setting
	 */
	public double getPan(){
		return this.pan;
	}
	
	/**
    * Determine the pan position for all notes in this phrase.
	 * @param double the phrase's pan setting
	 */
	public void setPan(double pan){
		this.pan = pan;
		Enumeration enum1 = noteList.elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
			note.setPan(pan);
	    }
	}

    /**
    * Return the tempo in beats per minute for this phrase
	 * @return double the phrase's tempo setting
	 */
	public double getTempo(){
		return this.tempo;
	}

    /**
        * Determine the tempo in beats per minute for this phrase
	 * @param double the phrase's tempo
	 */
	public void setTempo(double newTempo){
        this.tempo = newTempo;
	}

	/**
        * Get an individual note object by its number
	 * @param int number - the number of the Track to return
	 * @return Note answer - the note object to return
	 */
	public Note getNote(int number){
		Enumeration enum1 = noteList.elements();
		int counter = 0;
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
			if(counter == number){
				return note;
			}
			counter++;
		}
		return null;
	}
	

    /**
        * Get the number of notes in this phrase
	 * @return int  The number of notes
	 */
	public int length(){
        return size();
    }

	/**
        * Get the number of notes in this phrase
	 * @return int  length - the number of notes
	 */
	public int size(){
		return(noteList.size());
	}

    /**
        * Get the number of notes in this phrase
	 * @return int  length - the number of notes
	 */
	public int getSize(){
		return(noteList.size());
	}

	
	/**
        * Returns the numerator of the Phrase's time signature
	 * @return int time signature numerator
	 */
	public int getNumerator(){
		return this.numerator;
	}
	
	/**
        * Specifies the numerator of the Phrase's time signature
	 * @param int time signature numerator
	 */
	public void setNumerator(int num){
		this.numerator = num;
	}
	
	/**
        * Returns the denominator of the Phrase's time signature
	 * @return int time signature denominator
	 */
	public int getDenominator(){
		return this.denominator;
	}
	
	/**
        * Specifies the denominator of the Phrase's time signature
	 * @param int time signature denominator
	 */
	public void setDenominator(int dem){
		this.denominator= dem;
	}

	/** Sets a reference to the part containing this phrase */
	public void setMyPart(Part part){
	    this.myPart = part;
	}

	/** returns a reference to the part that contains this phrase */
	public Part getMyPart(){
	    return myPart;
	}

	/**
        * Returns a copy of the entire Phrase
     * @return Phrase a copy of the Phrase
     */
	public Phrase copy(){
		Phrase phr = new Phrase();
        copyAttributes(phr);
		Enumeration enum1 = this.noteList.elements();
		while(enum1.hasMoreElements()){
			phr.addNote( ((Note) enum1.nextElement()).copy() );
		}
		return phr;
	}

    private void copyAttributes(Phrase phr) {
        // NB: start time now covered by position
        phr.position = this.position.copy(phr);
        phr.setTitle(this.title + " copy");
        phr.setInstrument(this.instrument);
        phr.setAppend(this.append);
        phr.setPan(this.pan);
        phr.setLinkedPhrase(this.linkedPhrase);
        phr.setMyPart(this.getMyPart());
        phr.setTempo(this.tempo);
        phr.setNumerator(this.numerator);
        phr.setDenominator(this.denominator);
    }

    /**
        * Returns a copy of a specified section of the Phrase,
	 * pads beginning and end with shortedend notes and rests
	 * if notes or phrase boundaries don't align with locations.
	 * @param double start location
	 * @param double end location
	 * @return Phrase a copy of the Phrase
	 */
    public Phrase copy(double startLoc, double endLoc){
        Phrase phr = this.copy(startLoc, endLoc, true);
        return phr;
    }

    /**
        * Returns a copy of a specified section of the Phrase,
	 * pads beginning and end with shortedend notes and rests
	 * if notes or phrase boundaries don't align with locations.
	 * @param double start location
	 * @param double end location
     * @param boolean requireNoteStart If true, only notes that start inside
     * the copy range are included in the copy. Notes starting prior but
     * overlapping are replaced by rests. Otherwise sections of notes inseide
     * the bounds are included.
	 * @return Phrase a copy of the Phrase
	 */

	public Phrase copy(double startLoc, double endLoc, boolean requireNoteStart){
	    // are the arguments valid?
	    if (startLoc >= endLoc || endLoc < this.getStartTime()) {
            return null;
        }
	    Phrase tempPhr = new Phrase(0.0);
	    copyAttributes(tempPhr);
	    double beatCounter = this.getStartTime();
	    if (beatCounter < 0.0) beatCounter = 0.0;
	    //is it before the phrase?
	    if(startLoc < beatCounter) {
	        Note r = new Note(REST, beatCounter - startLoc);
	        tempPhr.addNote(r);
            endLoc += beatCounter - startLoc;
	    }
	
	    // are there notes before the startLoc to pass up?
	    for(int i=0; i< this.size(); i++) {

	        if (beatCounter < startLoc) { // this note starts before the space
	            if((beatCounter + this.getNote(i).getRhythmValue() > startLoc) &&
                   (beatCounter + this.getNote(i).getRhythmValue() <= endLoc)) { // ends within the space
                    if (requireNoteStart) {
                        Note n = new Note( REST, beatCounter +
                                           this.getNote(i).getRhythmValue() - startLoc);
                        tempPhr.addNote(n);
                    } else {
                        if(this.getNote(i).getPitchType() == Note.MIDI_PITCH) {
                            Note n = new Note( this.getNote(i).getPitch(), beatCounter +
                                               this.getNote(i).getRhythmValue() - startLoc,
                                               this.getNote(i).getDynamic());
                            tempPhr.addNote(n);
                        } else {
                            Note n = new Note( this.getNote(i).getFrequency(), beatCounter +
                                               this.getNote(i).getRhythmValue() - startLoc,
                                               this.getNote(i).getDynamic());
                            tempPhr.addNote(n);
                        }
                    }
                }
	            if(beatCounter + this.getNote(i).getRhythmValue() > endLoc) { // ends after the space
	                if (requireNoteStart) {
                        Note n = new Note( REST, beatCounter +
                                           this.getNote(i).getRhythmValue() - startLoc, this.getNote(i).getDynamic());
                        tempPhr.addNote(n);
                    } else {
                        if(this.getNote(i).getPitchType() == Note.MIDI_PITCH) {
                            Note n = new Note( this.getNote(i).getPitch(), beatCounter +
                                               endLoc - startLoc, this.getNote(i).getDynamic());
                            tempPhr.addNote(n);
                        } else {
                            Note n = new Note( this.getNote(i).getPitch(), beatCounter +
                                               endLoc - startLoc, this.getNote(i).getDynamic());
                            tempPhr.addNote(n);
                        }					
                    }
	            }
	        }

	        if ( beatCounter >= startLoc && beatCounter < endLoc) { // this note starts in the space
	            if (beatCounter + this.getNote(i).getRhythmValue() <= endLoc) { // also ends in it
	                tempPhr.addNote(this.getNote(i));
	            } else { //ends after the end. Make up last note.
	                Note n = new Note(this.getNote(i).getPitch(), endLoc - beatCounter, this.getNote(i).getDynamic());
	                tempPhr.addNote(n);
	            }
	        }
	        beatCounter += this.getNote(i).getRhythmValue();
	    }
	    // is there more space past the end of the phrase?
	    if (beatCounter < endLoc) { // make up a rest to fill the space
	        Note r = new Note(REST, endLoc - beatCounter);
	        tempPhr.addNote(r);
	    }
	    // done!
	    return tempPhr;
	}


    /**
        * Returns a copy of a specified section of the Phrase,
     * pads beginning and end with shortedend notes and rests
     * if notes or phrase boundaries don't align with locations.
     * @param boolean trimmed wether to truncte notes (as per the
                                                       * other versions of copy) or not
     * @param boolean startTimeShifts wether to shift the start
     * time or to add a rest if if the start is afte startloc
     * @param double start location
     * @param double end location
     * @return Phrase a copy of the Phrase
     */
    public Phrase copy(double startLoc, double endLoc,
                       boolean trimmed, boolean truncated, boolean startTimeShifts) {
        // are the arguments valid?
        if (startLoc >= endLoc || endLoc < this.getStartTime()) {
            System.out.println("invalid arguments in Phrase.copy");
            return null;
        }
        Phrase tempPhr = new Phrase( "", startLoc, this.instrument);
        //this.title + " copy", startLoc, this.instrument);
        tempPhr.setAppend(this.append);
        tempPhr.setPan(this.pan);
        tempPhr.setLinkedPhrase(this.linkedPhrase);
        tempPhr.setMyPart(this.getMyPart());
        double beatCounter = this.getStartTime();
        if (beatCounter < 0.0) beatCounter = 0.0;
        //is it before the phrase?

        //make beatCounter add up to the right amount before going though the segment
        Enumeration noteEnum = this.getNoteList().elements();
        while(startLoc > beatCounter && noteEnum.hasMoreElements()) {
            Note n = (Note)noteEnum.nextElement();
            beatCounter += n.getRhythmValue();
        }

        // now it is in the segment, should a rest be added in the begining because
        // a note overlaps?
        if(startLoc < beatCounter) {
            if(beatCounter < endLoc) {
                if(startTimeShifts) {
                    tempPhr.setStartTime(beatCounter+this.getStartTime());
                } else {
                    Note r = new Note(REST, beatCounter - startLoc);
                    tempPhr.addNote(r);
                }
            } else {
                Note r = new Note(REST, endLoc - startLoc);
                tempPhr.addNote(r);
                return tempPhr;
            }
        }
        double addedCounter = 0.0;

        // go through the rest of the notes in the segment, until it equals the
        // end or runs out of notes
        while(noteEnum.hasMoreElements() && beatCounter < endLoc) {
            Note n = ((Note)noteEnum.nextElement()).copy();
            //if the note goes over the end
            if((n.getRhythmValue()+beatCounter) > endLoc && trimmed) {
                //trimm it back
                n.setRhythmValue(endLoc - beatCounter, truncated);
            }
            tempPhr.addNote(n);
            addedCounter += n.getRhythmValue();
            beatCounter += n.getRhythmValue();
        }

        // is there more space past the end of the phrase?
        if (beatCounter < endLoc) { // make up a rest to fill the space
            Note r = new Note(REST, endLoc - beatCounter);
            tempPhr.addNote(r);
        } else if (addedCounter == 0.0) { // or if nothing was added at all
            Note r = new Note(REST, endLoc - startLoc);
            tempPhr.addNote(r);
        }
        // done!
        return tempPhr;
        }

    /**
        * Returns a copy of the entire Phrase only ontaining notes
     * between highest and lowset specified pitch.
     * @ param highestPitch The top MIDI pitch to include in the copy
     * @ param lowestPitch The bottom MIDI pitch to include in the copy
     * @return Phrase a partical copy of the Phrase
     */
	public Phrase copy(int highestPitch, int lowestPitch){
        if (lowestPitch >= highestPitch) {
            System.err.println("jMusic Phrase copy error: "+
                               "lowset pitch is not lower than highest pitch");
            System.exit(0);
        }
		Phrase phr = new Phrase(this.title + " copy");
        //		phr.setStartTime(this.startTime);
        phr.position = this.position.copy(phr);
		phr.setInstrument(this.instrument);
		phr.setAppend(this.append);
		phr.setPan(this.pan);
		phr.setLinkedPhrase(this.linkedPhrase);
		phr.setMyPart(this.getMyPart());
		Enumeration enum1 = this.noteList.elements();
		while(enum1.hasMoreElements()){
            Note n = ((Note)enum1.nextElement()).copy();
            if (n.getPitch() > highestPitch && n.getPitch() < lowestPitch) n.setPitch(REST);
            phr.addNote(n);
		}
		return phr;
	}


	/**
        * Prints the tracks attributes to stdout
	 */
	public String toString(){
		String phraseData = new String("-------- jMusic PHRASE: '" +
                                       title + "' contains " + this.size() + " notes.  Start time: " +
                                       getStartTime() +" --------" +'\n');
        if(this.tempo > 0) phraseData += "Phrase Tempo = "+ this.tempo + '\n';
		Enumeration enum1 = getNoteList().elements();
		int counter = 0;
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
			phraseData = phraseData + note.toString() + '\n';
		}
		return phraseData;
	}
	
	/**
        * Empty removes all elements in the note list vector
     */
	public void empty(){
		noteList.removeAllElements();
	}
	
	/**
        * Returns a carbon copy of a specified Phrase
	 * Changes to notes in the original or the alias will be echoed in the other.
	 * Note: that for this to work other phrase classes must to change the
	 * noteList attribute to point to another object, but instead
	 * should always update the noteList itself. See shuffle() as an example.
	 */
	public Phrase alias() {
	    Phrase phr = new Phrase(this.title + " alias", this.getStartTime(), this.instrument);
        phr.setTempo(this.tempo);
        phr.setAppend(this.append);
	    phr.noteList = this.noteList;
	    return phr;
	}

	/**
        * Return the pitch value of the highest note in the phrase.
     */
	public int getHighestPitch() {
		int max = -1;
		Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
			if(note.getPitchType() == Note.MIDI_PITCH)
                if (note.getPitch() > max) max = note.getPitch();
		}
		return max;
	}

	/**
        * Return the pitch value of the lowest note in the phrase.
     */
	public int getLowestPitch() {
		int min = 128;
		Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            if(note.getPitchType() == Note.MIDI_PITCH)
                if(note.getPitch() < min && note.getPitch() >= 0 ) min = note.getPitch();;
		}
		return min;
	}

	/**
        * Return the value of the longest rhythm value in the phrase.
     */
	public double getLongestRhythmValue() {
		double max = 0.0;
		Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
			if(note.getRhythmValue() > max) max = note.getRhythmValue();
		}
		return max;
	}

    /**
        * Return the value of the shortest rhythm value in the phrase.
     */
    public double getShortestRhythmValue() {
        double min = 1000.0;
        Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            if(note.getRhythmValue() < min) min = note.getRhythmValue();
        }
        return min;
    }

    /**
        * Change the dynamic value of each note in the phrase.
     */
    public void setDynamic(int dyn) {
        Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            note.setDynamic(dyn);
        }
    }

    /**
        * Change the pitch value of each note in the phrase.
     */
    public void setPitch(int val) {
        Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            note.setPitch(val);
        }
    }

    /**
        * Change the rhythmValue value of each note in the phrase.
     */
    public void setRhythmValue(double val) {
        Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            note.setRhythmValue(val);
        }
    }

	/**
        * Change the Duration value of each note in the phrase.
     */
	public void setDuration(double val) {
		Enumeration enum1 = getNoteList().elements();
		while(enum1.hasMoreElements()){
			Note note = (Note) enum1.nextElement();
            note.setDuration(val);
		}
	}

	/**
        * Return the Duration of the phrase in beats.
	 */
	public double getBeatLength() {
		return getEndTime();
	}

    private final class Position implements Serializable{
        private double startTime = 0.0;

        private final Phrase phrase;

        private boolean isAbsolute = false;

        private Position anchor;

        private Alignment alignment = Alignment.AFTER;

        private double offset;

        private Position(final double startTime, final Phrase phrase) {
            this.isAbsolute = true;
            this.startTime  = startTime;
            this.phrase     = phrase;
        }

        private Position(final Position anchor,
                         final Alignment alignment,
                         final double offset,
                         final Phrase phrase) {
            this.isAbsolute = false;
            this.anchor     = anchor;
            this.alignment  = alignment;
            this.offset     = offset;
            this.phrase     = phrase;
        }

        private final Anchoring getAnchoring() {
            if (isAbsolute) {
                return null;
            }
            return new Anchoring(anchor.phrase, alignment, offset);
        }

        private final void setStartTime(final double startTime) {
            this.isAbsolute = true;
            this.startTime  = startTime;
        }

        private final double getStartTime() {
            if (isAbsolute) {
                return startTime;
            } else {
                return alignment.determineStartTime(
                                                    phrase.getTotalDuration(),
                                                    anchor.getStartTime(),
                                                    anchor.getEndTime())
                + offset;
            }
        }

        private final double getEndTime() {
            return phrase.getEndTime();
        }

        private final Position copy(final Phrase newCopy) {
            return (isAbsolute)
            ? new Position(startTime, newCopy)
            : new Position(anchor, alignment, offset,
                           newCopy);
        }
    }

    /**
        * Generates and returns a new note with default values
     * and adds it to this phrase.
     */
    public Note createNote() {
        Note n = new Note();
        this.addNote(n);
        return n;
    }

    /*
     * Replace one note with another.
     * @param Note the new note
     * @param index the phrase position to replace
     */
    public void setNote(Note n, int index) {
        if(index >= this.getSize()) {
            System.out.println("jMusic error: Phrase setNote index is too large.");
            return;
        }
        this.noteList.removeElementAt(index);
        this.noteList.insertElementAt(n, index);
    }

    /**
    * Specify the mute status of this phrase.
    * @param state True or False, muted or not.
    */
    public void setMute(boolean state) {
        this.mute = state;
    }

    /**
    * Retrieve the current mute status.
    * @return boolean True or False, muted ot not.
    */
    public boolean getMute() {
        return this.mute;
    }

    /**
    * Change both the rhythmValue and duration of each note in the phrase.
    * @param newLength The new rhythmValue for the note (Duration is a proportion of this value)
    */
    public void setLength(double newLength) {
        this.setRhythmValue(newLength);
        this.setDuration(newLength * Note.DEFAULT_DURATION_MULTIPLIER);
    }

    /**
    * Calculate the start time, in beats, of the note at the specified index.
    * @param noteIndex The note's position in the phrase.
    * @return double The absolute (taking into account phrase start time) beat
    * position where the note starts. -1 is returned when an index out of range is encounterd.
    */
    public double getNoteStartTime(int noteIndex) {
        if (noteIndex >= this.size()) return -1.0;
        double startLoc = this.getStartTime();
        for (int i=0; i<noteIndex; i++) {
            startLoc += this.getNote(i).getRhythmValue();
        }
        return startLoc;
    }
}
