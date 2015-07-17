/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2013 Robert Keller and Harvey Mudd College
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

package imp.neuralnet;

import imp.util.Trace;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Neuron {
    
    private int neuronIndex;
    private int layerIndex;
    private ActivationFunction type;
    private int numberOfInputs;
    private double[] weight;
    private double[] accumulated;
    private double[] oldAccumulated;
    private double[] updateValue;
    private double output;
    private double sensitivity;
    private double net;
    private double deriv;

  
    public Neuron(int layerIndex, int neuronIndex, ActivationFunction type, int numberOfInputs)
    {
        this.neuronIndex = neuronIndex;
        this.layerIndex = layerIndex;
        this.type = type;
        this.numberOfInputs = numberOfInputs;

        double initialUpdate = 0.1;
        
        weight = new double[numberOfInputs + 1];
        accumulated = new double[numberOfInputs + 1];
        oldAccumulated = new double[numberOfInputs + 1];
        updateValue = new double[numberOfInputs + 1];
        
        // Initialize all weights randomly.
        
        for (int j = 0; j <= numberOfInputs; j++)
        {
            setWeight(j, Math.random() - 0.5);
            updateValue[j] = initialUpdate;
            accumulated[j] = 0;
            oldAccumulated[j] = 0;
        }
    }
    
    /*
     * Fire this neuron using a Source as input.
     * Remember the output value and derivative.
     */
    public void fire(Source source)
    {
        net = weight[numberOfInputs]; // bias component
        
        for (int j = 0; j < numberOfInputs; j++)
            net += weight[j] * source.get(j); // compute the net value
        
        output = type.act(net); // set output
        deriv = type.deriv(net, output); // set the function derivative
    }
    
    public void use(Source source)
    {
        net = weight[numberOfInputs]; // bias component
        
        for (int j = 0; j < numberOfInputs; j++)
            net += weight[j] * source.get(j); // compute the net value
        
        output = type.use(net); // set the output
    }
    
    /*
     * Get the output from the network based on the last firing
     */
    public double getOutput()
    {
        return output;
    }
    
    /*
     * Adjust the weights of the neuron, based on the values in source.
     */
    public void adjustWeights(Source source, double rate)
    {
        double factor = -rate * sensitivity;
        for (int j = 0; j < numberOfInputs; j++)
            addWeight(j, factor, source.get(j));
        
        addWeight(numberOfInputs, factor, 1); // bias
    }
    
    /*
     * Accumulate weight adjustment without changing weight.
     */
    public void accumulateWeights(Source source, double rate)
    {
        double factor = -rate * sensitivity;
        for (int j = 0; j < numberOfInputs; j++)
            accumulated[j] += factor * source.get(j);
        
        accumulated[numberOfInputs] += factor; // bias
    }
    
    /*
     * Accumulate gradient adjustment (without learning rate)
     */
    public void accumulateGradient(Source source)
    {
        for( int j = 0 ; j < numberOfInputs; j++ )
            accumulated[j] += sensitivity * source.get(j);

        accumulated[numberOfInputs] += sensitivity;
    }
    
    public void clearAccumulation()
    {
        for (int j = 0; j <= numberOfInputs; j++)
            accumulated[j] = 0;
    }
    
    public void installAccumulation()
    {
        for (int j = 0; j <= numberOfInputs; j++)
            weight[j] += accumulated[j];
    }
    
    public double max(double x, double y)
    {
        return x > y ? x : y;
    }
    
    public double min(double x, double y)
    {
        return x > y ? y : x;
    }
    
    public double sign(double x)
    {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }
    
    public void adjustByRprop(double etaPlus, double etaMinus)
    {
        double deltaMax = 50;
        double deltaMin = 1e-6;
        
        for (int j = 0; j <= numberOfInputs; j++)
        {
            double product = oldAccumulated[j] * accumulated[j];
            
            if (product > 0)
            {
                updateValue[j] = min(etaPlus * updateValue[j], deltaMax);  
                weight[j] -= updateValue[j] * sign(accumulated[j]);
                oldAccumulated[j] = accumulated[j];
            }
            else if (product < 0)
            {
                updateValue[j] = max(etaMinus * updateValue[j], deltaMin);
                oldAccumulated[j] = 0;
                // Note no weight change
            }
            else
            {
                weight[j] -= updateValue[j] * sign(accumulated[j]);
                oldAccumulated[j] = accumulated[j];
            }
            
            accumulated[j] = 0; // Reset
        }
    }
    
    /*
     * Get the ith weight of this neuron
     */
    public double getWeight(int i)
    {
        return weight[i];
    }
    
    /*
     * Get the product of this neuron's sensitivity times the input weight
     */
    public double getWeightedSensitivity(int i)
    {
        return sensitivity * weight[i];
    }
    
    /*
     * Set a specified weight
     */
    public void setWeight(int j, double weight) // FIX: Private or public?
    {
        this.weight[j] = weight;
    }
    
    /*
     * Set the sensitivity of this neuron, first multiplying the argument by the derivative
     * from the most recent activation.
     */
    public void setSensitivity(double factor)
    {
        sensitivity = deriv * factor;
    }
    
    /*
     * Add an increment to the weight
     */
    public void addWeight(int weightIndex, double factor, double input)
    {
        double delta = factor * input;
        double newWeight = weight[weightIndex] + delta;
     
        if (Trace.atLevel(5))
        {
            System.out.printf("backward layer %d neuron %d input: % 7.4f add % 7.4f (from % 7.4f to % 7.4f)\n",
               layerIndex, neuronIndex, input, delta, weight[weightIndex], newWeight);
        }
        
        weight[weightIndex] = newWeight;
    }

    /*
     * Show the sensivtivity and weights of this neuron
     */
    public void showWeights(String title, StringBuilder output) 
    {
        output.append(String.format("Layer %d Neuron %d %s Weights: ", layerIndex, neuronIndex, title));
        
        for(double d : weight)
            output.append(String.format("%9.4f", d));
        
        output.append(String.format(" (Bias) Sensitivity: % 7.4f \n", sensitivity));
    }

    /*
     * Save the sensitivity and weights of this neuron.
     */
    public void printWeights(String title, PrintWriter out)
    {
        out.printf("layer %d neuron %d %s weights: ", layerIndex, neuronIndex, title);
        
        for (int j = 0; j <= numberOfInputs; j++)
        {
            out.printf("% 9.4f ", weight[j]);
        }
        
        out.printf("(bias) sensitivity: % 7.4f \n", sensitivity);
    }
    
    /*
     * Sets the weights and sensitivities of this neuron
     */
    public void fixWeights(String input) 
    {
        Scanner in = new Scanner(input);
        String line;
        
        // Throw away wording before weights.
        for (int i = 0; i < 6; i++)
            in.next(); //Might need whitespace delimeter
        
        for (int i = 0; i <= numberOfInputs; i++)
        {
            line = in.next(); // Should work for any amount of whitespace
            double weightValue = Double.parseDouble(line);
            this.weight[i] = weightValue;
        }
        
        // Throw away wording before the bias.
        for (int i = 0; i < 2; i++)
            in.next();
        
        line = in.next();
        sensitivity = Double.parseDouble(line);
    }
}
