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

import java.util.Queue;
import java.util.LinkedList;
import mikera.vectorz.AVector;
/**
 * Class AutoencoderInputManager takes in the initial input for an Autoencoder and handles 
 * chopping up data that should go to either the encoder, decoder, or both 
 * (Usually, the encoder will get all input, and then a portion of the input will be sent to the decoder for reconstruction).
 * @see CompressingAutoencoder
 * @author Nicholas Weintraut
 */
public abstract class AutoencoderInputManager {
    protected Queue<AVector> encoderQueue;
    protected Queue<AVector> decoderQueue;
    
    public AutoencoderInputManager()
    {
        encoderQueue = new LinkedList<AVector>();
        decoderQueue = new LinkedList<AVector>();
    }
    
    public void takeInput(AVector input)
    {
        encoderQueue.offer(input);
        decoderQueue.offer(input.copy());
    }
    
    public abstract int getEncoderInputSize();
    
    
    public abstract int getDecoderInputSize();
            
    
    public abstract AVector retrieveEncoderInput();
    
    public abstract AVector retrieveDecoderInput(AVector neuralQueueOutput, AVector decoderOutput);
}
