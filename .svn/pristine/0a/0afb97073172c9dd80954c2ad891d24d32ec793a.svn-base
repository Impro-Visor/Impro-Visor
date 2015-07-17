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

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Layer implements Source{
    
    private int layerIndex;
    private int numberInLayer;
    private ActivationFunction type;
    private Neuron[] neuron;
    
    public Layer(int layerIndex, int numberInLayer, ActivationFunction type, int numberOfInputs)
    {
        this.layerIndex = layerIndex;
        this.numberInLayer = numberInLayer;
        this.type = type;
        
        neuron = new Neuron[numberInLayer];
       
        for (int i = 0; i < numberInLayer; i++)
        {
            neuron[i]= new Neuron(layerIndex, i, type, numberOfInputs);
        }
    }
    
    /*
     * Return the index of this layer
     */
    public int getIndex()
    {
        return layerIndex;
    }
    
    /*
     * Return the number of neurons in this layer
     */
    public int getSize()
    {
        return numberInLayer;
    }
    
    /*
     * Return the ActivationFunction type
     */
    public ActivationFunction getFunctionType()
    {
        return type;
    }
    
    /*
     * Get the output value of the ith neuron
     */
    public double get(int i)
    {
        return neuron[i].getOutput();
    }
    
    /*
     * Fire all the Neurons in this layer, based on the Source, 
     * setting the output value and derivative evaluated at the net value.
     */
    public void fire(Source source)
    {
        for (Neuron n : neuron)
            n.fire(source);
    }
    
    /*
     * Use all the Neurons in this layer, based on the Source, 
     * setting the output value at the net value.
     */
    public void use(Source source)
    {
        for (Neuron n : neuron)
            n.use(source);
    }
    
    /*
     * Get the sum of the weighted sensitivities 
     * from the ith neuron of the previous layer.
     */
    public double getSumWeightedSensitivity(int i)
    {
        double sum = 0;
        for (Neuron n : neuron)
            sum += n.getWeightedSensitivity(i);
        
        return sum;
    }
    
    /*
     * Set the weight to a specific value
     */
    public void setWeight(int i, int j, double weight)
    {
        neuron[i].setWeight(j, weight);
    }
    
    /*
     * Adjust the weights on each neuron in this layer
     */
    public void adjustWeights(Source source, double rate)
    {
        for (Neuron n : neuron)
            n.adjustWeights(source, rate);
    }
    
    /*
     * Accumulate the weight changes on each neuron in this layer.
     */
    public void accumulateWeights(Source source, double rate)
    {
        for (Neuron n : neuron)
            n.accumulateWeights(source, rate);
    }
    
    /*
     * Accumulate the gradient on each neuron in this layer.
     */
    public void accumulateGradient(Source source)
    {
        for (Neuron n : neuron)
            n.accumulateGradient(source);
    }
    
    public void clearAccumulation()
    {
        for (Neuron n : neuron)
            n.clearAccumulation();
    }

    public void installAccumulation()
    {
        for (Neuron n : neuron)
            n.installAccumulation();
    }

    public void adjustByRprop(double etaPlus, double etaMinus)
    {
        for (Neuron n : neuron)
            n.adjustByRprop(etaPlus, etaMinus);
    }

    /*
     * Set the sensitivity of the Neurons in the HiddenLayer,
     * based on the value in the next Layer.
     */
    public void setSensitivity(Layer nextLayer)
    {
        for (int i = 0; i < numberInLayer; i++)
            neuron[i].setSensitivity(nextLayer.getSumWeightedSensitivity(i));
    }
    
    /*
     * Set the sensitivity of the Neuron in the Output Layer,
     * based on the value in Sample.
     */
    public void setSensitivity(Sample sample)
    {
        for (int i = 0; i < numberInLayer; i++)
        {
            double error = sample.getOutput(i) - neuron[i].getOutput();
            neuron[i].setSensitivity(-2 * error);
        }
    }
    
    /*
     * Compute the error of the Output Layer, based on the value in Sample.
     */
    public double computeError(Sample sample)
    {
        double sse = 0;
        for (int i = 0; i < numberInLayer; i++)
        {
            double error = sample.getOutput(i) - neuron[i].getOutput();
            sse += error * error;
        }
        return sse;
    }

    /*
     * Show the outputs on the System.out stream
     */
    public void showOutput()
    {
        for (Neuron n : neuron)
            System.out.println(n.getOutput() +" ");
    }
    
    // To be used only with a single neuron in a layer
    public double getSingleOutput()
    {
        return neuron[0].getOutput();
    }
    
    /*
     * Show the weights on each neuron to the System.out stream
     */
    public void showWeights(String message, StringBuilder output)
    {
        for (Neuron n : neuron)
            n.showWeights(message, output);
    }
    
    /*
     * Print the weights on each neuron in this layer to a file
     */
    public void printWeights(String title, PrintWriter out)
    {
        for (Neuron n : neuron)
            n.printWeights(title, out);
    }
    
    /*
     * Save all weights and sensitivities for all neurons in the layer
     */
    public void fixWeights(String input)
    {
        Scanner in = new Scanner(input);
        
        for (Neuron n : neuron)
        {
            String line = in.nextLine();
            n.fixWeights(line);
        }
    }
}
