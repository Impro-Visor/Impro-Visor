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


package imp.lstm.architecture;

import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public class LeadsheetAutoencoderInputManager extends AutoencoderInputManager{
    

    private int noteSize;
    private int inputSize;
    private int featureVectorSize;
    
    public LeadsheetAutoencoderInputManager(int inputSize, int noteSize, int featureVectorSize)
    {
        this.inputSize = inputSize;
        this.noteSize = noteSize;
        this.featureVectorSize = featureVectorSize;
    }
    
    public LeadsheetAutoencoderInputManager(int noteSize)
    {
        this.noteSize = noteSize;
    }
    
    public void setInputSizes(int inputSize, int featureVectorSize)
    {
        this.inputSize = inputSize;
        this.featureVectorSize = featureVectorSize;
    }

    @Override
    public AVector retrieveEncoderInput() {
        return encoderQueue.poll();
    }

    @Override
    public AVector retrieveDecoderInput(AVector neuralQueueOutput, AVector decoderOutput) {
        //currently we don't do anything with the decoder size
        return decoderQueue.peek().subVector(0, decoderQueue.poll().length() - noteSize).join(neuralQueueOutput);
    }

    @Override
    public int getEncoderInputSize() {
        return inputSize;
    }

    @Override
    public int getDecoderInputSize() {
        return featureVectorSize + inputSize;
    }
    
}
