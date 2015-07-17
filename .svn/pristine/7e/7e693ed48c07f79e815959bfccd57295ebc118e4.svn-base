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

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Sample implements Source{
    
    private int outputDim;
    private int inputDim;
    private double[] output;
    private double[] input;
    
    public Sample(int outputDim, int inputDim)
    {
        this.outputDim = outputDim;
        this.inputDim = inputDim;
        
        output = new double[outputDim];
        input = new double[inputDim];
    }
    
    public void setOutput(int i, double value)
    {
        output[i] = value;
    }
    
    public void setInput(int i, double value)
    {
        input[i] = value;
    }
    
    public double getOutput(int i)
    {
        return output[i];
    }
    
    public double getInput(int i)
    {
        return input[i];
    }
    
    public int getOutputDimension()
    {
        return outputDim;
    }
    
    public int getInputDimension()
    {
        return inputDim;
    }
    
    /**
     *
     * @param i
     * @return
     */
    @Override
    public double get(int i)
    {
        return input[i];
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("Outputs:");

        for( int i = 0; i < outputDim; i++ )
        {
            String s = String.format(" % 6.3f", output[i]);
            result.append(s);
        }

        result.append("\nInputs:");

        for( int i = 0; i < inputDim; i++ )
        {
             String s = String.format(" % 6.3f", input[i]);
             result.append(s);
        }
        
        return result.toString();
    }
    
}
