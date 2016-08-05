/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
