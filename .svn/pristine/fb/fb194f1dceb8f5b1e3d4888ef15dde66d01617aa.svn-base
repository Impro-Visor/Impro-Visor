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


package jm.music.tools;

import jm.music.data.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * AdaptiveMatrix is responsible for hold Markov information in an internal 
 * matrix which may be written to disk as a serialized java object
 * The matrix itself will contain two arrays.  The first array will
 * contain historic information. The second array will contain 
 * weightings.  These arrays will both allow for n level markov
 * Matrix's.
 *
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:43:35  2001
 */

public final class AdaptiveMatrix{
	//------------------------------------------
	// Attributes
	//------------------------------------------
	/**
	 * The depth of the markov array (ie The number
	 * of prior states to use in this matrix).
	 */
	private int depth;

	/**
	 * The Markov matrix stored in a Hashtable
	 * Prefixe is stored as a string of numbers
	 * equal to the depth attribute
	 * Postfix is an double[1000] - the indexes
	 * of this double are not known inside 
	 * adaptive matrix but should be know by
	 * whatever generative class uses 
	 * AdaptiveMatrix.
	 */
	private Hashtable weightMatrix;

	/**
	 * The counts matrix stores the Markov matrix
	 * in a count format.
	 * This needs to be converted to the weightings
	 * matrix before any number generation can happen
	 */
	private Hashtable countMatrix;

	/**
	 * The index range to use for the matrix.
	 * This value is very important as it defines
	 * the values for the table. (i.e the index range
	 * to use for a pitch table is 127 - the range
	 * of values which could be used for a given pitch)
	 */
	private int indexRange;

	//--------------------------------------------
	// Constructors
	//--------------------------------------------
	/** 
	 * This constructor is used when the Matrix
	 * is being created for the first time. The score parameter is the
	 * jMusic score to used in building the matrix.
	 * The depth attribute is the number of prior states
	 * to use in creating the Matrix.  The fileName is 
	 * a string containing the name of the file to use
	 * for storing a copy of this matrix.  This contructor
	 * should be used when a Matrix is being created for the
	 * first time.  For matrix's that have already been
	 * written to disk the AdaptiveMatrix(fileName) constructor
	 * should be used.
	 * @param numArray are the values to use in the matriX 
	 * @param depth the number of prior states.
	 * @param indexRange the range of numbers to use for indexing
	 */
	public AdaptiveMatrix(int[] numArray, int depth, int indexRange){
		this.countMatrix = new Hashtable();
		this.weightMatrix = new Hashtable();
		this.depth = depth;
		this.indexRange = indexRange;
		this.calcCount(numArray);
		this.calcWeight();
	}

	/**
	 * This constructor is used when the matrix is being
	 * read from an existing file.  The file is read by default
	 * upon calling this constructor which then assigns all 
	 * instance variables to those of the AdaptiveMatrix being
	 * read back from disk.
	 * @param fileName the fileName to read an existing Matrix from
	 */
	public AdaptiveMatrix(String fileName){
		read(fileName);
	}
	
	//----------------------------------------------------
	// Public Methods
	//----------------------------------------------------
	/**
	 * update an existing AdaptiveMatrix with new data
	 * @param numArray are the valuew to use in the matrix
	 */
	public void update(int[] numArray){
		this.calcCount(numArray);
		this.calcWeight();
	}

	/**
	 * Generate returns an array of generated index values
	 * @param length the number of indexes to return. The 
	 * seed data is included in the length value.
	 * @param seed the data to use as a starting point
	 * @return an array of indexes equal to the length 
	 * required.
	 */
	public int[] generate(int length, int[] seed){
		//Check to make sure seed is the correct depth
		if(seed.length != this.depth){
			System.err.println("[WARNING] Wrong seed length for this Matrix depth");
			return null;
		}
		int[] array = new int[length];
		String seedString = "";
		int[] seedbak = new int[seed.length];
		//change seed into a string and add seed to the outgoing array
		for(int i=0;i<seed.length;i++){
			array[i] = seed[i];
			seedbak[i] = seed[i];
			seedString += seed[i]+" "; //create the prefix
		}
		String bak = seedString;
		//Check that this seed is available from the matrix
		if(!weightMatrix.containsKey(seedString)){
			System.err.println("[WARNING] This seed is unavailable .. try another");
			return null;
		}
		//Calculate the new index values
		for(int i=seed.length;i<array.length;i++){
			if(!weightMatrix.containsKey(seedString)){ //If there are no other choices
				seedString = bak;//select the original seed;	
				seed = seedbak;
			}
			double[] tmp = (double[])weightMatrix.get(seedString);
			seedString = ""; //reset the prefix
			for(int k=1;k<seed.length;k++){
				seedString += seed[k]+" ";
				seed[k-1]=seed[k]; 
			}
			double rand = Math.random();
			double count = 0.0;
			for(int j=0;j<tmp.length;j++){
				count += tmp[j];
				if(count>rand){
					array[i] = j;
					seedString += j+" ";
					seed[depth-1] = j;
					break;
				}
			}
		}
		return array;
	}

	/**
	 * Read the matrix to file
	 */
	public void read(String fileName){
		AdaptiveMatrix am = null;
		try{
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			am = (AdaptiveMatrix) ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.depth = am.getDepth();
		this.indexRange = am.getIndexRange();
		this.countMatrix = am.getCountMatrix();
		this.weightMatrix = am.getWeightMatrix();
	}

	/**
	 * Saves the matrix to file
	 */
	public void write(String fileName){
		try{
			File file = new File(fileName);
			FileOutputStream fis = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fis);
			oos.writeObject(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * A simple print method for displaying the contents
	 * of a matrix.
	 */
	public void print(){
		System.out.println();
		System.out.println("MATRIX");
		System.out.println("----------------");
		Enumeration enum1 = this.weightMatrix.keys();
		while(enum1.hasMoreElements()){
			String prefix = (String)enum1.nextElement();
			double[] postfix = (double[])this.weightMatrix.get(prefix);
			System.out.print(prefix + "\t: ");
			for(int i=0;i<postfix.length;i++){
				System.out.print(" " + postfix[i]);
			}
			System.out.println();
		}
	}

	/**
	 * get matrix's depth
	 * @return depth the number prior states
	 */
	public int getDepth(){
		return this.depth;
	}

	/**
	 * retrieve the matrix
	 * @return Hashtable
	 */
	public Hashtable getWeightMatrix(){
		return this.weightMatrix;
	}

	/**
	 * retrieve the count matrix
	 * @return Hashtable
	 */
	public Hashtable getCountMatrix(){
		return this.countMatrix;
	}

	/**
	 * retrieve the index range
	 * @return indexRange
	 */
	public int getIndexRange(){
		return this.indexRange;
	}

	//-------------------------------------------------------
	// Private Methods
	//-------------------------------------------------------
	/**
	 * calcCount takes an array of double values and
	 * calculates how many unique prefixes exist in the list
	 * (based on the depth of the matrix) and how many 
	 * times each number inside the index range occurs.
	 * The numbers in numArray MUST fall within the 1000 
	 * range.  These numbers are treated as indexes not
	 * as values as such.
	 * The prefixes are stored as Strings so that they 
	 * are more easily checked for "equality".
	 * @param numArray a number of ints which must fall within
	 * the index range of this Matrix.
	 */
	private void calcCount(int[] numArray){
		for(int i=this.depth-1;i<numArray.length-1;i++){
			String prefix = "";
			int[] post = new int[indexRange]; 
			for(int j=0,k=this.depth-1;j<this.depth;j++,k--){
				prefix += numArray[i-k]+" ";
			}
			if(this.countMatrix.containsKey(prefix)){
				int[] postfix = (int[])countMatrix.get(prefix);
				postfix[numArray[i+1]]++;
				countMatrix.put(prefix,postfix);
			}else{
				post[numArray[i+1]]++;
				countMatrix.put(prefix,post);
			}
		}
	}

	/**
	 * This method takes the counts established in the
	 * createMatrix method and turns these into percentage
	 * weightings based on a division between an individual index count
	 * and the total index count for that row.
	 */
	private void calcWeight(){
		Enumeration enum1 = this.countMatrix.keys();
		while(enum1.hasMoreElements()){
			String prefix = (String)enum1.nextElement();
			int[] postfix = (int[])this.countMatrix.get(prefix);
			int count = 0;
			for(int i=0;i<postfix.length;i++){
				count += postfix[i];
			}
			double[] postfix2 = new double[indexRange];
			for(int i=0;i<postfix.length;i++){
				postfix2[i] = (double)postfix[i]/(double)count;
			}
			this.weightMatrix.put(prefix,postfix2);
		}
	}
}
