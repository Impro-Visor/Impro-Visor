/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>


Copyright (C) 2000 Andrew Sorensen and Andrew Brown

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
package jm.music.tools;

import java.util.Vector;
import jm.music.data.Phrase;
import jm.music.data.Note;
import jm.JMC;

/**
 * The PhraseMatrix class holds AdaptiveArrays for each Note parameter
 * type and is also responsible for any mappings that need to occur
 * between Note types and the AdaptiveMatrix
 *
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:43:52  2001
 */
public final class PhraseMatrix implements JMC{
	//-------------------------------------------------
	// Attributes
	//-------------------------------------------------
	/**
	 * The matrix associated with pitch data
	 */
	private AdaptiveMatrix pitchAM;

	/**
	 * The matrix associated with rhythm data
	 */
	private AdaptiveMatrix rhythmAM;

	/**
	 * The matrix associated with dynamic
	 */
	private AdaptiveMatrix dynamicAM;

	/**
	 * The depth to make the pitch adaptive matrix's
	 */
	private int pitchDepth;
        /**
	 * The depth to make the rhythm adaptive matrix's
	 */
	private int rhythmDepth;
        /**
	 * The depth to make the pitch adaptive matrix's
	 */
	private int dynamicDepth;


	/**
	 * The array of notes associated with the original 
	 * phrase.  This information is kept so that we
	 * can generate new notes while still keeping any
	 * of the phrases original note attributes.
	 */
	private Note[] notes;

	/**
 	* A map from rhythm values to AdaptiveMatrix index 
 	* values.
 	*/
 	private final double[] rhythmMap = {SB, MD, M, C, CT, CD, Q, QD, QT, SQ, DSQ};

	//--------------------------------------------------
	// Constructors
	//--------------------------------------------------
	/**
	 * Takes a phrase as input and creates matrix's for
	 * pitch, rhythm and dynamic from the phrase's notes
         * at the specifed depth.
	 */
	public PhraseMatrix(Phrase phrase, int depth){
            this(phrase, depth, depth, depth);
        }
        
        /**
	 * Takes a phrase as input and creates matrix's for
	 * pitch, rhythm and dynamic from the phrase's notes
         * at different depths of each attribute.
	 */
	public PhraseMatrix(Phrase phrase, int pDepth, int rDepth, int dDepth){
		this.pitchDepth = pDepth;
                this.rhythmDepth = rDepth;
                this.dynamicDepth = dDepth;
		this.notes = phrase.getNoteArray();	
		calcPitch();
		calcRhythm();
		calcDynamic();
	}

	//--------------------------------------------------
	// Public Methods
	//--------------------------------------------------
	/**
	 * calculates a pitch based adaptive matrix from
	 * the phrases note array
	 */
	public void calcPitch(){ 
		int[] numArray = new int[notes.length];
		for(int i=0;i<notes.length;i++){
			numArray[i]=notes[i].getPitch();
		}
		pitchAM = new AdaptiveMatrix(numArray, this.pitchDepth, 127);
	}

	/**
	 * calculates a rhythm based adaptive matrix from
	 * the phrases note array
	 */
	public void calcRhythm(){ 
		int[] numArray = new int[notes.length];
		for(int i=0;i<notes.length;i++){
			boolean flag =  false;
			for(int j=0;j<rhythmMap.length;j++){
				if(notes[i].getRhythmValue() == rhythmMap[j]){
					flag = true;
					numArray[i] = j;
					break;
				}
			}
			if(flag == false){
				System.err.print("[WARNING] PhraseMatrix only supports ");
				System.err.println("rhythm values supported in the JMC file");
			}
		}
		rhythmAM = new AdaptiveMatrix(numArray, this.rhythmDepth, this.rhythmMap.length);
	}


	/**
	 * calculates a dynamic based adaptive matrix from
	 * the phrases note array
	 */
	public void calcDynamic(){ 
		int[] numArray = new int[notes.length];
		for(int i=0;i<notes.length;i++){
			numArray[i]=notes[i].getDynamic();
		}
		dynamicAM = new AdaptiveMatrix(numArray, this.dynamicDepth, 127);
	}

	/**
	 * Generates a the number of notes requrested using
	 * a combination of generated note attributes and
	 * existing note attributes.
	 * This method works on the existing length of note
	 * data available from the existing phrase.
         * @param p boolean consider the pitches or not
         * @param r boolean consider the rhythmValues or not
         * @param d boolean consider the dynamics or not
	 * @return a phrase containing the new note data
	 */
	public Phrase generate(boolean p, boolean r, boolean d){
		return this.generate(p, r, d, this.notes.length);
	}

	/**
	 * Generates a the number of notes requrested using
	 * a combination of generated note attributes and
	 * existing note attributes.
         * @param p boolean consider the pitches or not
         * @param r boolean consider the rhythmValues or not
         * @param d boolean consider the dynamics or not
	 * @return a phrase containing the new note data
	 */
	public Phrase generate(boolean p, boolean r, boolean d, int numOfNotes){
		int[] pitch = new int[this.pitchDepth];
		int[] rhythm = new int[this.rhythmDepth];
		int[] dynamic = new int[this.dynamicDepth];
		//Make an array of default notes as long as required by numOfNotes
		Note[] noteList = new Note[numOfNotes];
		for(int i=0;i<numOfNotes;i++){
			noteList[i] = new Note();
		}
                /*
		for(int i=0;i<this.depth;i++){
			pitch[i] = notes[i].getPitch();
			dynamic[i] = notes[i].getDynamic();
			for(int j=0;j<rhythmMap.length;j++){
				if(notes[i].getRhythmValue() == rhythmMap[j]){
					rhythm[i] = j;
					break;
				}
			}
		}
                */
                // pitch
		for(int i=0;i<this.pitchDepth;i++){
			pitch[i] = notes[i].getPitch();
		}

                // rhythm
                for(int i=0;i<this.rhythmDepth;i++){
			for(int j=0;j<rhythmMap.length;j++){
				if(notes[i].getRhythmValue() == rhythmMap[j]){
					rhythm[i] = j;
					break;
				}
			}
		}
                // dynamic
                for(int i=0;i<this.dynamicDepth;i++){
			dynamic[i] = notes[i].getDynamic();
		}
                
		int[] retPitch = pitchAM.generate(numOfNotes, pitch);
		int[] retDynamic = dynamicAM.generate(numOfNotes, dynamic);
		int[] retRhythm = rhythmAM.generate(numOfNotes, rhythm);

		if(p){
			for(int i=0;i<numOfNotes;i++){
				noteList[i].setPitch(retPitch[i]);
			}
		}
                if(r){
			for(int i=0;i<numOfNotes;i++){
				noteList[i].setRhythmValue(rhythmMap[retRhythm[i]]);
				noteList[i].setDuration(rhythmMap[retRhythm[i]] * 0.9);
			}
		}
                if(d){
			for(int i=0;i<numOfNotes;i++){
				noteList[i].setDynamic(retDynamic[i]);
			}
		}

		Phrase phrase = new Phrase();
		phrase.addNoteList(noteList);
		return phrase;
	}
}

//===========================================================================
// TODO LIST:
// Be able to generate notes based on an existing matrix from an Array of 
// notes. This should allow note information in the phrases to be potentially 
// partially used along with any new generated material
// This change will probably also make the Note[] notes array redundant.
//============================================================================

