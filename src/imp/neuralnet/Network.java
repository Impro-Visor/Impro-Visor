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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Network {
    
    private int numberLayers;
    private int lastLayer;
    private Layer[] layer;
    private int inputDimension;
    
    public Network(int numberLayers, int[] layerSize, ActivationFunction[] type, int inputDimension)
    {
        this.numberLayers = numberLayers;
        this.lastLayer = numberLayers - 1;
        this.inputDimension = inputDimension;

        layer = new Layer[numberLayers];
        if (numberLayers < 1)
        {
            System.out.println("Too few layers, aborting.");
            System.exit(1);
        }
        
        layer[0] = new Layer(0, layerSize[0], type[0], inputDimension);
        
        for (int i = lastLayer - 1; i > 0; i--)
            layer[i] = new Layer(i, layerSize[i], type[i], layerSize[i-1]);
        
        layer[lastLayer] = new Layer(lastLayer, layerSize[lastLayer], 
                                      type[lastLayer], layerSize[lastLayer-1]);   
    }
    
    /*
     * Fire all the Neurons in the Network based on the input Sample,
     * starting with the HiddenLayer and working forward.
     */
    public void fire(Sample sample)
    {
        layer[0].fire(sample);
        
        for (int i = 1; i < numberLayers; i++)
            layer[i].fire(layer[i-1]);
    }
     
    /*
     * Use all the Neurons in the Network based on the input Sample,
     * starting with the HiddenLayer and working forward.
     */
    public void use(Sample sample)
    {
        layer[0].use(sample);
        
        for (int i = 1; i < numberLayers; i++)
            layer[i].use(layer[i-1]);
    }
    
    /*
     * Compute the error as compared with the output of a given Sample.
     */
    public double computeError(Sample sample)
    {
        double sse = 0;
        int n = sample.getOutputDimension();
        
        for (int i = 0; i < n; i++)
        {
            double error = sample.getOutput(i) - layer[lastLayer].get(i);
            sse += error * error;
        }
        return sse / n;
    }
    
    /*
     * Compute the sign agreement as compared with the output of a given Sample.
     * This is for use with discrete outputs only.
     */
    public int computeUsageError(Sample sample)
    {
        int n = sample.getOutputDimension();
        for (int i = 0; i < n; i++)
        {
            if ( (sample.getOutput(i) > 0.5) != (layer[lastLayer].get(i) > 0.5)  )
            {
                return 1; // disagreement
            }
        }
        return 0; // No disagreement
    }
    
    /*
     * Set the sensitivities in the Network based on the values in a given Sample,
     * in preparation for adjusting the weights.
     */

    public void setSensitivity(Sample sample)
    {
        layer[lastLayer].setSensitivity(sample);

        for (int i = lastLayer-1; i >= 0; i--)
        {
            layer[i].setSensitivity((layer[i+1]));
        }
    }
    
    /*
     * Adjust the weights of a network based on error values of a given Sample and
     * previously set sensitivities. 
     */
    public void adjustWeights(Sample sample, double rate)
    {
        for (int i = lastLayer; i > 0; i--)
            layer[i].adjustWeights(layer[i-1], rate);
        
        layer[0].adjustWeights(sample, rate);
    }
    
    public void accumulateWeights(Sample sample, double rate)
    {
        for (int i = lastLayer; i > 0; i--)
            layer[i].accumulateWeights(layer[i-1], rate);
        
        layer[0].accumulateWeights(sample, rate);
    }
    
    public void accumulateGradient(Sample sample)
    {
        for (int i = lastLayer; i > 0; i--)
            layer[i].accumulateGradient(layer[i-1]);
        
        layer[0].accumulateGradient(sample);
    }
        
    public void clearAccumulation()
    {
        for (int i = lastLayer; i >= 0; i--)
            layer[i].clearAccumulation();
    }
    
    public void installAccumulation()
    {
        for (int i = lastLayer; i >= 0; i--)
            layer[i].installAccumulation();
    }
    
    public void adjustByRprop(double etaPlus, double etaMinus)
    {
        for (int i = lastLayer; i >= 0; i--)
            layer[i].adjustByRprop(etaPlus, etaMinus);
    }
  
    /*
     * Show the output of the network to the System.out stream.
     */
    public void showOutput()
    {
        layer[lastLayer].showOutput();
    }
    
    public double getSingleOutput()
    {
        return layer[lastLayer].getSingleOutput();
    }
    
    /*
     * Show the weights and sensitivities of all Neurons in the network.
     */
    public StringBuilder showWeights(String message)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < numberLayers; i++)
            layer[i].showWeights(message, output);
        return output;
    }
    
    /*
     * Print the weights and sensitivities of all Neurons in the network.
     */
    public void printWeights(String title, PrintWriter out)
    {
        for (int i = 0; i < numberLayers; i++)
            layer[i].printWeights(title, out);
    }
    
    /*
     * Sets the weights and sensitivities for all layers
     */
    public void fixWeights(BufferedReader in) throws IOException
    {
        for (int i = 0; i < numberLayers; i++)
        {
            String line;
            String mem = "";
            
            for(int j = 0; j < layer[i].getSize(); j++)
            {
                line = in.readLine().toLowerCase();

                if (line.contains("layer " + i))
                    mem += line + "\n";
            }

            layer[i].fixWeights(mem);       
        }
    }
    
    /*
     * Get statistics for the network
     */
    public StringBuilder getStatistics()
    {
        StringBuilder output = new StringBuilder();
        output.append("length of input: ").append(inputDimension).append("\n");
        output.append(numberLayers).append(" layers structured (from input to output) as: \n");
        for( int i = 0; i < numberLayers; i++ )
        {
            Layer thisLayer = layer[i];
            output.append("    ").append(thisLayer.getFunctionType().getName());
            output.append(" (").append(thisLayer.getSize()).append(" " + "neurons" + ")");
            output.append("\n");
        }
        
        return output;
    }
}
