/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.filters;

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
