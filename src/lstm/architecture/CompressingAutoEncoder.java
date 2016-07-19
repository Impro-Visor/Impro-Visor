/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture;
import lstm.filters.Operations;
import lstm.filters.GroupedSoftMaxSampler;
import lstm.filters.NoteEncodingCleanFilter;
import lstm.filters.DataFilter;
import lstm.encoding.EncodingParameters;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Class CompressingAutoencoder describes a neural network architecture which bridges an LSTM encoder and decoder with a fragmented neural queue.
 * @author Nicholas Weintraut
 */
public class CompressingAutoEncoder implements Loadable {
    private int inputSize;
    private int featureVectorSize;
    private int outputSize;
    private AVector currOutput;
    private LSTM encoder1;
    private LSTM encoder2;
    private FullyConnectedLayer fullLayer1;
    private FragmentedNeuralQueue queue;
    private LSTM decoder1;
    private LSTM decoder2;
    private FullyConnectedLayer fullLayer2;
    private DataFilter finalSampler;
    private DataFilter outputCleaner;
    private AutoencoderInputManager inputManager;
    
    /**
     * Initializes an instance of CompressingAutoEncoder without initializing component weights and biases. Weights and biases should be loaded using AutoEncoderMeatPacker
     * @see AutoEncoderMeatPacker
     * @param inputManager The input manager for the CompressingAutoEncoder
     */
    public CompressingAutoEncoder(AutoencoderInputManager inputManager, int inputSize, int outputSize, int featureVectorSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.featureVectorSize = featureVectorSize;
        this.inputManager = inputManager;
        encoder1 = new LSTM();
        encoder2 = new LSTM();
        fullLayer1 = new FullyConnectedLayer(Operations.Sigmoid);
        queue = new FragmentedNeuralQueue();
        decoder1 = new LSTM();
        decoder2 = new LSTM();
        //op type is none because we will feed its result through a one hot softmax sampler
        fullLayer2 = new FullyConnectedLayer(Operations.None);
        
        finalSampler = new GroupedSoftMaxSampler(EncodingParameters.noteEncoder.getGroups());
        outputCleaner = new NoteEncodingCleanFilter();
    }
    
    public boolean hasDataStepsLeft() {
        return !queue.isEmpty();
    }
    private int timeStep = 0;
    public void encodeStep(AVector input) {
        if(input.length() == inputSize)
        {
            //for(int i = 0; i < input.length(); i++)
            //    System.out.print(input.getDouble(i) + " ");
            //System.out.println("<- networkInput at timeStep " + timeStep++);
            inputManager.takeInput(input);
            AVector managerInput = inputManager.retrieveEncoderInput();
            /*if(managerInput.equals(input))
                System.out.println("Matches");
            else
                System.out.println("err");*/
            //System.out.println(managerInput);
            AVector encoding1 = encoder1.step(managerInput);
            //System.out.println(encoding1.get(encoding1.length() - 1));
            AVector encoding2 = encoder2.step(encoding1);
            //System.out.println(encoding2.get(encoding2.length() - 1));
            AVector vectorEncoding = fullLayer1.forward(encoding2);
            //System.out.println(vectorEncoding);
            //System.out.println(vectorEncoding.get(0) + " strength <- timeStep " + timeStep++);
            AVector outputVector = vectorEncoding.subVector(1, featureVectorSize);

            queue.enqueueStep(outputVector, vectorEncoding.get(0));
            
        }
        else
        {
            throw new RuntimeException("Your input had a different size than this network is configured for!");
        }
    }
    
    public boolean canDecode() {
        return queue.hasFullBuffer();
    }
    public void perturbQueue(){
        queue.shuffleFeatures(true, false);
    }
    
    public void testQueue(){
        queue.testFill();
    }
    
    public void printFeatureGroups(){
        queue.printFeatureGroups();
    }
    
    public AVector decodeStep() {
        //current output at very beginning should be a rest
        AVector decoding1 = decoder1.step(inputManager.retrieveDecoderInput(queue.dequeueStep(), currOutput));
        AVector decoding2 = decoder2.step(decoding1);
        AVector decoding3 = fullLayer2.forward(decoding2);
        //use sampler, and then apply cleaning filter
        
        /*for(int i = 0; i < decoding2.length(); i++)
        {
            double element = decoding2.getDouble(i);
            System.out.print(String.format("%03f", element) + " ");
        }
        System.out.println("<- decoder LSTM2 output");
        for(int i = 0; i < decoding3.length(); i++)
        {
            double element = decoding3.getDouble(i);
            System.out.print(String.format("%03f", element) + " ");
        }
        System.out.println("<- decoder Full Layer output");*/
        currOutput = outputCleaner.filter(finalSampler.filter(decoding3));
        return currOutput;
    }
    
    public LSTM getEncoderLSTM1()
    {
        return encoder1;
    }
    public LSTM getEncoderLSTM2()
    {
        return encoder2;
    }
    public FullyConnectedLayer getEncoderFullLayer()
    {
        return fullLayer1;
    }
    public LSTM getDecoderLSTM1()
    {
        return decoder1;
    }
    public LSTM getDecoderLSTM2()
    {
        return decoder2;
    }
    public FullyConnectedLayer getDecoderFullLayer()
    {
        return fullLayer2;
    }
    
    public void setCurrOutput(int outputSize) {
        currOutput = Vector.createLength(outputSize);
    }
    
    @Override
    public boolean load(INDArray data, String loadPath) {
        String car = pathCar(loadPath);
        String cdr = pathCdr(loadPath);
        //System.out.println(car);
        if(car.equals("enc"))
        {
            car = pathCar(cdr);
            //System.out.println("\t " + car);
            cdr = pathCdr(cdr);
            switch(car)
            {
                case "full": return fullLayer1.load(data, cdr);
                case "lstm1": return encoder1.load(data, cdr);
                case "lstm2": return encoder2.load(data, cdr);
                default: return false;
            }
        }
        else if(car.equals("dec"))
        {
            car = pathCar(cdr);
            //System.out.println("\t " + car);
            cdr = pathCdr(cdr);
            switch(car)
            {
                case "full": return fullLayer2.load(data, cdr);
                case "lstm1": return decoder1.load(data, cdr);
                case "lstm2": return decoder2.load(data, cdr);
                default: return false;
            }
        }
        else
            return false;
    }
}
