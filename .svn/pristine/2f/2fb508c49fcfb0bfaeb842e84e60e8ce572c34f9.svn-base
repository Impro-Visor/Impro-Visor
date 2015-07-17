/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>


Copyright (C) 2000 Andrew Sorensen, Andrew Brown, Adam Kirby

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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;

import jm.audio.Instrument;
import jm.audio.Audio;
import jm.JMC;
import jm.midi.*;
import jm.util.*;
import jm.gui.show.*;

/**
 * The Score class is used to hold score data.  Score data includes
 * is primarily made up of a vector of Part objects. Commonly score
 * data is algorithmically generated or read from a standard MIDI file, but can also be read and saved to 
 * file using Java's object serialization. In this way a Score's data can
 * be saved in a more native context.
 * To find out how to read from and write to standard MIDI files
 * or to use object serializationcheck out 
 * the jm.util.Read and km.util.Write classes.
 * 
 * @see Part 
 * @see jm.midi.SMF
 * @author Andrew Sorensen, Andrew Brown, Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:33  2001
 */
public class Score implements JMC, Cloneable, Serializable{

	//----------------------------------------------
    // Defaults
	//----------------------------------------------

    public static final String DEFAULT_TITLE = "Untitled Score";

    public static final double DEFAULT_TEMPO = 60.0;

    public static final int DEFAULT_KEY_SIGNATURE = 0;

    public static final int DEFAULT_KEY_QUALITY = 0;

    public static final int DEFAULT_NUMERATOR = 4;

    public static final int DEFAULT_DENOMINATOR = 4;

	//----------------------------------------------
	// Attributes
	//----------------------------------------------

	/** the name assigned to a Score */
	private String title = "Unnamed Score";
	/** 
	 * a Vector containing the Part objects associated with this score 
	 */
	private Vector partList;
	/** the speed for this score */
	private double tempo;
	
    // Possible Alternative:
    //      Consider using the jm.music.data.KeySignature class and modifying
    //      it to support the keyQuality field.

	/** the number of accidentals this score 
	* negative numbers are Flats, positive numbers are Sharps
	*/
	private int keySignature; 
	/** 0 = major, 1 = minor, others modes not specified */
	private int keyQuality;

    // Possible Alternative:
    //      Consider making a TimeSignature class, containing the following
    //      two fields.

	/** the top number of the time signature */
	private int numerator;
	/** the bottom number of the time signature */
	private int denominator;

	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * Constructs an empty score with a default name
	 */
	public Score(){
        this(DEFAULT_TITLE);
	}

	/**
	 * Constructs an empty score.
	 * @param title Give the score a name
	 */
	public Score(String title){
        this(title, DEFAULT_TEMPO);
	}
    
	/**
	 * Constructs an empty score at the specified tempo
	 * @param tempo The speed for this score in beats per minute
	 */
	public Score(double tempo){
        this(DEFAULT_TITLE, tempo);
	}
	
	/**
	 * Constructs an empty score.
	 * @param title Give the score a name
	 * @param tempo Define speed of playback in bpm
	 */
	public Score(String title, double tempo){
		this.title = title;
		this.tempo = tempo;
		this.partList = new Vector();
        this.keySignature = DEFAULT_KEY_SIGNATURE;
        this.keyQuality = DEFAULT_KEY_QUALITY;
        this.numerator = DEFAULT_NUMERATOR;
        this.denominator = DEFAULT_DENOMINATOR;
	}

	/**
	* Constructs a Score containing the specified <CODE>part</CODE>.
	*
	* @param Part to be contained in the Score
	*/
	public Score(Part part) {
		this();
		if (part.getTempo() > 0) {
			this.tempo = part.getTempo();
		} else this.tempo = tempo;
        this.addPart(part);
	}
    
	/**
	* Constructs a Score containing the specified <CODE>part</CODE>.
	* @param title Give the score a name
	* @param tempo Define speed of playback in bpm
	* @param part The Part to be contained in the Score
	*/
	public Score(String title, double tempo, Part part) {
            this(title, tempo);
            this.addPart(part);
	}


    /**
     * Constructs a Score containing the specified <CODE>parts</CODE>.
     *
     * @param parts - array of Parts to be contained in the Score
     */
    public Score(Part[] parts) {
        this();
        addPartList(parts);
    }

    /**
     * Constructs a Score containing the specified <CODE>part</CODE> with
     * the specified <CODE>title</CODE>.
     *
     * @param part  Part to be contained in the Score
     * @param title String describing the title of the Score
     */
    public Score(Part part, String title) {
        this(title, part.getTempo());
        addPart(part);
    }

    /**
     * Constructs a Score containing the specified <CODE>parts</CODE> with
     * the specified <CODE>title</CODE>.
     *
     * @param parts Array of Parts to be contained in the Score
     * @param title String describing the title of the Score
     */
    public Score(Part[] parts, String title) {
        this(title);
        addPartList(parts);
    }

    /**
     * Constructs a Score containing the specified <CODE>part</CODE> with
     * the specified <CODE>title</CODE> and the specified <CODE>tempo</CODE>.
     *
     * @param part  Part to be contained in the Score
     * @param title String describing the title of the Score
     * @param tempo A double describing the tempo of the Score
     */
    public Score(Part part, String title, double tempo) {
        this(title, tempo);
        addPart(part);
    }

    /**
     * Constructs a Score containing the specified <CODE>parts</CODE> with
     * the specified <CODE>title</CODE> and the specified <CODE>tempo</CODE>.
     *
     * @param parts array of Parts to be contained in the Score
     * @param title String describing the title of the Score
     * @param tempo double describing the tempo of the Score
     */
    public Score(Part[] parts, String title, double tempo) {
        this(title, tempo);
        addPartList(parts);
    }
    

	//----------------------------------------------
	// Data Methods
	//----------------------------------------------
	/**
	 * Add a Track object to this Score
	 */
	public void add(Part part){
		this.addPart(part);
	}
        
        /**
	 * Add a Track object to this Score
	 */
	public void addPart(Part part){
		part.setMyScore(this);
		this.partList.addElement(part);
	}

    /**
     * Inserts <CODE>part</CODE> at the specified position, shifting all parts
     * with indices greater than or equal to <CODE>index</CODE> up one position.
     *
     * @param part  Part to be added
     * @param index where it is to be inserted
     * @throws ArrayIndexOutOfBoundsException
     *              when <CODE>index</CODE> is beyond the range of current
     *              parts.
     */
    public void insertPart(Part part, int index)
                    throws ArrayIndexOutOfBoundsException {
        this.partList.insertElementAt(part, index);
		part.setMyScore(this);
    }
	
	/**
	 * Adds multiple parts to the score from an array of parts
	 * @param partArray
	 */
	public void addPartList(Part[] partArray){
	    for(int i=0;i< partArray.length;i++){
			this.addPart( partArray[i]);
		}
    }
	
	/**
	 * Deletes the specified Part in the Score
	 * @param int partNumb the index of the part to be deleted
	 */
	 public void removePart(int partNumb) {
	    Vector vct = (Vector)this.partList;
	    try{
	        vct.removeElement(vct.elementAt(partNumb));
	    } catch (RuntimeException re){
                System.err.println("The Part index to be deleted must be within the score.");
            }
	}
    
    /**
    * Deletes the first occurence of the specified part in the Score.
    * @param part  the Part object to be deleted.
    */
    public void removePart(Part part) {
        this.partList.removeElement(part);
    }
    
    /**
	 * Deletes the last Part added to the Score
	 */
	 public void removeLastPart() {
	    Vector vct = (Vector)this.partList;
	    vct.removeElement(vct.lastElement());
	}
	
	/**
	 * Deletes all the parts previously added to the score
	 */
	 public void removeAllParts() {
	    this.partList.removeAllElements();
	}

	/**
	 * Returns the Scores List of Tracks
	 */
	public Vector getPartList(){
		return partList;
	}
	
	/**
	 * Returns the all Parts in this Score as a array
	 * @return Part[] An array containing all Part objects in this score
	 */
	public Part[] getPartArray(){
		Vector vct = (Vector) this.partList.clone();
		Part[] partArray = new Part[vct.size()];
		for(int i=0;i< partArray.length;i++){
		    partArray[i] = (Part) vct.elementAt(i);
		}
		return partArray;
	}

	/**
	 * Get an individual Track object from its title 
	 * @param String title - the name of the Track to return
	 * @return Track answer - the Track to return
	 */
	public Part getPart(String title){
		Enumeration enum1 = partList.elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
			if(part.getTitle().equalsIgnoreCase(title)){
				return part;
			}
		}
		return null;
	}
	
	/**
	 * Get an individual Track object from its number 
	 * @param int number - the number of the Track to return
	 * @return Track answer - the Track to return
	 */
	public Part getPart(int number){
		Enumeration enum1 = partList.elements();
		int counter = 0;
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
			if(counter == number){
				return part;
			}
			counter++;
		}
		return null;
	}
	
	

	//----------------------------------------------
	// Utility Methods
	//----------------------------------------------
	/**
	 * Return the title of this Score
	 * @return String title - the name of this Score
	 */
	public String getTitle(){
		return title;
	}
	/**
	 * Assign a title to this Score
	 * @param String title - the name of this Score
	 */
	public void setTitle(String title){
		 this.title = title;
	}

	/**
	 * Returns the Score's tempo 
	 * @return double tempo
	 */
	public double getTempo(){
		return this.tempo;
	}
	
	/**
	 * Sets the Score's tempo 
	 * @param double tempo
	 */
	public void setTempo(double tempo){
		this.tempo = tempo;
	}
	
	/**'
	 * Returns the Score's key signature 
	 * The number of sharps (+) or flats (-)
	 * @return int key signature
	 */
	public int getKeySignature(){
		return this.keySignature;
	}
	
	/**
	 * Specifies the Score's key signature 
	 * The number of sharps (+) or flats (-)
	 * @param int key signature
	 */
	public void setKeySignature(int newSig){
		this.keySignature = newSig;
	}
	
	/**
	 * Returns the Score's key quality 
	 * 0 is Major, 1 is minor
	 * @return int key quality
	 */
	public int getKeyQuality(){
		return this.keyQuality;
	}
	
	/**
	 * Specifies the Score's key quality 
	 * 0 is Major, 1 is minor
	 * @param int key quality (modality)
	 */
	public void setKeyQuality(int newQual){
		this.keyQuality = newQual;
	}
		
    /**
    * Specifies the Score's time signature
     * @param num - Time signature numerator
     * @param dem - Time signature denominator
     */
    public void setTimeSignature(int num, int dem){
        this.numerator = num;
        this.denominator= dem;
    }
    
    /**
    * Returns the Score's time signature elements.
     * @return Point The Time Signature elements packaged together with the 
     * numerator as'x' and denominator as 'y'. You may prefer to use the 
     * getNumerator and getDenomintor methods directly, rather than use 
     * this convenience method which requires importing the java.awt package 
     * to extract the elements : )
     */
    public java.awt.Point getTimeSignature(){
        return new java.awt.Point(this.numerator, this.denominator);
    }
    
    /**
        * Specifies the Score's time signature numerator 
	 * @param int time signature numerator
	 */
	public void setNumerator(int num){
		this.numerator = num;
	}
	
	/**
        * Specifies the Score's time signature denominator
	 * @param int time signature denominator
	 */
	public void setDenominator(int dem){
		this.denominator= dem;
	}
    
    
    
    /**
    * Retrieves the numerator element of the Score's time signature.
     * @return num - Time signature numerator
     */
    public int getNumerator(){
        return this.numerator;
    }
    
    /**
    * Retrieves the denominator element of the Score's time signature.
     * @return int Time signature denominator
     */
    public int getDenominator(){
        return this.denominator;
    }
    
	/**
	 * Make a copy of this Score object
	 * @return Score - return a new Score Object
	 */
	public Score copy() {
		Score newScore = new Score(title + " copy");
		newScore.setTempo(this.tempo);
		newScore.setTimeSignature(this.numerator, this.denominator);
		Enumeration enum1 = this.partList.elements();
		while(enum1.hasMoreElements()){
			Part oldPart = (Part) enum1.nextElement();
			newScore.addPart((Part) oldPart.copy());
		}
		return (Score)newScore;
	}

    public Score copy(final double startTime, final double endTime) {
        Score score = this.copy();
        score.removeAllParts();
		score.setTempo(this.tempo);
		score.setTimeSignature(this.numerator, this.denominator);
        int scoresize = this.size();
        for (int i = 0; i < scoresize; i++) {
            score.addPart(this.getPart(i).copy(startTime, endTime));
        }
        return score;
    }
	
	/**
	 * Return the beat where score ends. Where it's last Part ends.
	 * @return double the Parts endTime
	 */
	public double getEndTime(){		
		double endTime = 0.0;
		Enumeration enum1 = this.partList.elements();
		while(enum1.hasMoreElements()){
			Part nextPart = (Part)enum1.nextElement();
			double partEnd = nextPart.getEndTime();
			if (partEnd > endTime) endTime = partEnd;
		}
		return endTime;
	}	
		
		
	/**
	 * Print the titles of all tracks to stdout
	 */
	public String toString(){
		String scoreData = new String("***** jMusic SCORE: '" + title + 
			"' contains " + this.size() + " parts. ****" + '\n');
		scoreData += "Score Tempo = " + this.tempo + " bpm" +'\n';
		Enumeration enum1 = partList.elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
			scoreData = scoreData + part.toString() + '\n';
		}
		return scoreData;
	}
	
	/**
	* Empty removes all elements in the vector
	*/
	public void empty(){
		this.empty(false);
	}
        
        /**
	* Empty removes all elements in the vector.
        * @param nullObjects If ture this sets all jMusic data objects to null
        *			priot to removing. This facilitates garbage collection.
	*/
	public void empty(boolean nullObjects){
		if(nullObjects) {
                    Enumeration enum1 = getPartList().elements();
                    while(enum1.hasMoreElements()){
                        Part part = (Part) enum1.nextElement();
                        Enumeration enum2 = part.getPhraseList().elements();
                        while(enum2.hasMoreElements()){
                            Phrase phrase = (Phrase) enum2.nextElement();
                            Enumeration enum3 = part.getPhraseList().elements();
                            while(enum3.hasMoreElements()){
                                Note note = (Note) enum3.nextElement();
                                note = null;
                            }
                            phrase = null;
                        }
                        part = null;
                    }
                }
                partList.removeAllElements();
	}
         
        /**
	 * Get the number of Parts in this score
	 * @return int  The number of parts
	 */
	public int length(){
            return size();
        }
                                           
	/**
	 * Get the number of Parts in this score
	 * @return int  length - the number of parts
	 */
	public int size(){
		return(partList.size());
	}
    
    /**
	 * Get the number of Parts in this score
	 * @return int  length - the number of parts
	 */
	public int getSize(){
		return(partList.size());
	}
    
    /**
	 * Remove any empty Parts or phrases from the Score.
	 */
	 public void clean() {
             Enumeration enum1 = getPartList().elements();
             while(enum1.hasMoreElements()){
                 Part part = (Part) enum1.nextElement();
                 // pass on the part to have phases cleaned
                part.clean();
                // check if part is empty
                 if (part.getPhraseList().size() == 0) {
                     this.removePart(part);
                 }
             }
	}
    
    /**
	 * Return the value of the highest note in the Score.
     */
    public int getHighestPitch() {
        int max = 0;
        Enumeration enum1 = getPartList().elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
            if(part.getHighestPitch() > max) max = part.getHighestPitch();
        }
        return max;
    }
    
    /**
	 * Return the value of the lowest note in the Score.
     */
    public int getLowestPitch() {
        int min = 127;
        Enumeration enum1 = getPartList().elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
            if(part.getLowestPitch() < min) min = part.getLowestPitch();
        }
        return min;
    }
    
    /**
	 * Return the value of the longest rhythm value in the Score.
     */
    public double getLongestRhythmValue() {
        double max = 0.0;
        Enumeration enum1 = getPartList().elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
            if(part.getLongestRhythmValue() > max) max = part.getLongestRhythmValue();
        }
        return max;
    }
    
    /**
	 * Return the value of the shortest rhythm value in the Score.
     */
    public double getShortestRhythmValue() {
        double min = 1000.0;
        Enumeration enum1 = getPartList().elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
            if(part.getShortestRhythmValue() < min) min = part.getShortestRhythmValue();
        }
        return min;
    }

	/**
	* Determine the pan position for all notes in this Score.
	 * @param double the phrase's pan setting
	 */
	public void setPan(double pan){
		Enumeration enum1 = partList.elements();
		while(enum1.hasMoreElements()){
			Part part = (Part) enum1.nextElement();
			part.setPan(pan);
		}
	}
        
        /**
        * Generates and returns a new empty part 
        * and adds it to the score.
        */
        public Part createPart() {
            Part p = new Part();
            this.addPart(p);
            return p;
        }
}
