/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
