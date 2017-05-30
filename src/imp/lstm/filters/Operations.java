/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
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

package imp.lstm.filters;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Enum operations specifies functionality on a number of in-place operations that can be performed on AVectors
 * @author cssummer16
 */
public enum Operations{
        Sigmoid, Tanh, Softmax, NormalSample, None;
        public AVector operate(AVector input)
        {
            // Note: This method consumes the input! It may modify in place
            // or return a new array
            switch(this)
            {
                case Sigmoid:   input.multiply(-1);
                                input.exp();
                                input.add(1.0);
                                input.reciprocal();
                                return input;
                case Tanh:      input.tanh();
                                return input;
                case Softmax:   AVector temp = input;
                                input.exp();
                                input.divide(temp.elementSum());
                                return input;
                case NormalSample:
                                if(input.length() % 2 != 0)
                                    throw new RuntimeException("Input length must be even!");
                                AVector means = input.subVector(0, input.length()/2);
                                AVector stdevs = input.subVector(input.length()/2, input.length()/2);
                                AVector samples = Vectorz.createSameSize(means).mutable();
                                Vectorz.fillGaussian(samples);
                                samples.multiply(stdevs);
                                samples.add(means);
                                return samples;
                case None:
                default:
                        return input;
                    
            }
        }
    }
