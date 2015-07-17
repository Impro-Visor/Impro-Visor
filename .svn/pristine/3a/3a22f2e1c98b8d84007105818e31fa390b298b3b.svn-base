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

package jm.audio.synth;

import java.io.IOException;
import jm.audio.AudioObject;
import jm.audio.AOException;
import jm.music.data.Note;

/**
 * Distorts the input by a specified transfer function.
 * @author Andrew Brown
 */

public final class Waveshaper extends AudioObject{
        /** Specify shaping function
         * 0 = normal polynomial
         * 1 = Chebyshev polynomial
         */
        private int shapeType = 1;
        /** The number of polynomials to sum for shaping function */
        private int stages = 4;
        /** array of weightings for the polynomial strengths
         * values between 0.0 and 1.0 are typical. */
        private double[] weights;
        /** constant for use with shape type */
        public static final int POLYNOMIAL = 0;
        /** constant for use with shape type */
        public static final int CHEBYSHEV = 1;
	//----------------------------------------------
	// Constructors
	//----------------------------------------------
	/**
	 * This constructor takes any AudioObject
	 * as input and distorts it via the
	 * default shaping (transfer) function.
	 * @param ao an Audio Object.
	 */
	public Waveshaper(AudioObject ao){
		super(ao, "[Waveshaper]");
                this.shapeType = 1;
                this.stages = 4;
                double[] defaultWeights = {0.3, 0.8, 0.6, 0.4};
                this.weights = defaultWeights;
	}
        
        /**
	 * This constructor takes any AudioObject
	 * as input and distorts it via a specifed
	 * shaping (transfer) function to a particular depth.
	 * @param ao an Audio Object.
         * @param shape The type of waveshape function to use
         * @param depth The number of polynomial stages to use (1-10)
	 */
	public Waveshaper(AudioObject ao, int shape, int depth){
		super(ao, "[Waveshaper]");
                this.shapeType = shape;
                this.stages = depth;
		double[] defaultWeights = {0.3, 0.8, 0.6, 0.4};
                this.weights = defaultWeights;
	}
        
        /**
	 * This constructor takes any AudioObject
	 * as input and distorts it via a specifed
	 * shaping (transfer) function to a particular depth.
	 * @param ao an Audio Object.
         * @param shape The type of waveshape function to use
         * @param depth The number of polynomial stages to use (1-10)
         * @param weights An array of volume scaling values for the polynomials
         *                  (make sure the length of the array equals the depth
                            minum 1. As the fundamental is assumed to be 1.0)
	 */
	public Waveshaper(AudioObject ao, int shape, int depth, double[] weights){
		super(ao, "[Waveshaper]");
                this.shapeType = shape;
                this.stages = depth;
                this.weights = weights;
	}

	//----------------------------------------------
	// Methods
	//----------------------------------------------
	/**
	 * This nextWork method adds all inputs together 
	 * and passes on a normalised result of the
	 * sum.
	 * @param input any number of incoming samples 
	 */
	public int work(float[] buffer)throws AOException{
		int returned = this.previous[0].nextWork(buffer);
                if (shapeType == 0) { // x + x2 + x3 = x4 + x n
                    for(int i=0;i<returned;i++){
                        float currentValue = Math.abs(buffer[i]);
                        float result = currentValue;
                        for(int s = 1; s < stages; s++) {
                            float product = currentValue;
                            for(int j = 0; j < s; j++) {
                                product *= currentValue;
                            }
                            result += product * weights[s];
                        }
                        if (buffer[i] < 0.0) result *= (float)-1.0;
                        buffer[i] = result;
                    }
                } else if (shapeType == 1) { // Chebyshev polynomials
                    for(int i=0;i<returned;i++){
                        float currentValue = Math.abs(buffer[i]);
                        float result = currentValue;
                        if (stages > 1) { //T2
                            result += weights[0] * ((float)2 * currentValue * currentValue - 1);
                        }
                        if (stages > 2) { //T3
                            result += weights[1] * ((float)4 * (float)(Math.pow((double)currentValue, 3.0)) - 
                                (float)3 * currentValue);
                        }
                        if (stages > 3) { //T4
                            result += weights[2] * (8 * (float)(Math.pow((double)currentValue, 4.0)) - 
                                (float)8 * (float)(Math.pow((double)currentValue, 2.0)) + 1);
                        }
                        if (stages > 4) {  // T5
                            result += weights[3] * (16 * (float)(Math.pow((double)currentValue, 5.0)) - 
                                (float)20.0 * (float)(Math.pow((double)currentValue, 3.0)) + 
                                (float)5.0 * currentValue);
                        }
                        if (stages > 5) {  // T6
                            result += weights[4] * (32 * (float)(Math.pow((double)currentValue, 6.0)) - 
                                (float)48.0 * (float)(Math.pow((double)currentValue, 4.0)) + 
                                (float)18.0 * (float)(Math.pow((double)currentValue, 2.0)) - 1);
                        }
                        if (stages > 6) {  // T7
                            result += weights[5] * (64 * (float)(Math.pow((double)currentValue, 7.0)) - 
                                (float)112.0 * (float)(Math.pow((double)currentValue, 5.0)) + 
                                (float)56.0 * (float)(Math.pow((double)currentValue, 3.0)) - 
                                (float)7.0 * currentValue);
                        }
                        if (stages > 7) {  // T8
                            result += weights[6] * (128 * (float)(Math.pow((double)currentValue, 8.0)) - 
                                (float)256.0 * (float)(Math.pow((double)currentValue, 6.0)) + 
                                (float)160.0 * (float)(Math.pow((double)currentValue, 4.0)) - 
                                (float)32.0 * (float)(Math.pow((double)currentValue, 2.0)) + 1);
                        }
                        if (stages > 8) {  // T9
                            result += weights[7] * (256 * (float)(Math.pow((double)currentValue, 9.0)) - 
                                (float)576.0 * (float)(Math.pow((double)currentValue, 7.0)) + 
                                (float)432.0 * (float)(Math.pow((double)currentValue, 5.0)) - 
                                (float)120.0 * (float)(Math.pow((double)currentValue, 3.0)) +
                                (float)9.0 * currentValue);
                        }
                        if (stages > 9) {  // T10
                            result += weights[8] * (512 * (float)(Math.pow((double)currentValue, 10.0)) - 
                                (float)1280.0 * (float)(Math.pow((double)currentValue, 8.0)) + 
                                (float)1120.0 * (float)(Math.pow((double)currentValue, 6.0)) - 
                                (float)400.0 * (float)(Math.pow((double)currentValue, 4.0)) +
                                (float)50.0 * (float)(Math.pow((double)currentValue, 2.0)) - 1);
                        }
                        if (buffer[i] < 0.0) result *= (float)-1.0;
                        buffer[i] = result;
                    } 
                }
		return returned;	
	}
}
