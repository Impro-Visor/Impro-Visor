/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;

import lstm.architecture.NetworkMeatPacker;
import java.io.File;
import lstm.io.leadsheet.LeadSheetDataSequence;
import lstm.architecture.CompressingAutoEncoder;
import lstm.architecture.FullyConnectedLayer;
import lstm.architecture.LSTM;
import lstm.architecture.LeadsheetAutoencoderInputManager;
import lstm.architecture.Loadable;
import lstm.architecture.poex.ProductCompressingAutoEncoder;
import lstm.encoding.EncodingParameters;
import lstm.encoding.Group;
import lstm.filters.GroupedSoftMaxSampler;
import lstm.filters.Operations;
import lstm.io.leadsheet.LeadSheetIO;
import java.util.Random;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 *  Class Driver is an implementation test for CompressingAutoEncoder which reads a LeadSheet file and produces an equivalent length LeadSheet file
 * @author Nicholas Weintraut
 */
public class Driver {
    private static final boolean advanceDecoding = false; //should we start decoding as soon as possible?
    
    public static void main(String[] args) {
        //here is just silly code for generating name based on an LSTM lol $wag
        LSTM lstm = new LSTM();
        FullyConnectedLayer fullLayer = new FullyConnectedLayer(Operations.None);
        Loadable titleNetLoader = new Loadable(){
            @Override
            public boolean load(INDArray array, String path)
            {
                String car = pathCar(path);
                String cdr = pathCdr(path);
                switch(car)
                {
                    case "full": return fullLayer.load(array, cdr);
                    case "lstm": return lstm.load(array, cdr);
                    default:
                        return false;
                }
            }
        };
        
        String[] notFound1 = (new NetworkMeatPacker()).pack(args[3], titleNetLoader);
        for(String name : notFound1)
            System.out.println(name);
        
        Random rand = new Random();
        String characterString = " !\"'[],-.01245679:?ABCDEFGHIJKLMNOPQRSTUVWYZabcdefghijklmnopqrstuvwxyz";
        AVector charOut = Vector.createLength(characterString.length());
        GroupedSoftMaxSampler sampler = new GroupedSoftMaxSampler(new Group[]{new Group(0, characterString.length(), true)});
        String songTitle = "";
        for(int i = 0; i < 50; i++)
        {
            
            charOut = fullLayer.forward(lstm.step(charOut));
            //System.out.println(charOut);
            charOut = sampler.filter(charOut);
            
            int charIndex = 0;
            for(; charIndex < charOut.length(); charIndex++)
            {
                if(charOut.get(charIndex) == 1.0)
                    break;
            }
            songTitle += characterString.substring(charIndex, charIndex+1);
        }
        songTitle = songTitle.trim();
        //end stupid stuff, songTitle will be used later during writeCall
        LogTimer.initStartTime();
        LogTimer.log("Generated song name: " + songTitle);
        
        
        //check if we have three arguments (first is input file path, second is output folder path)
       
        if (args.length > 2) {
            
            //Initialization
            LogTimer.initStartTime();   //start our logging timer to keep track of our execution time
            File inputFile = new File(args[0]); //load input file
            LogTimer.log("Reading file...");
            LeadSheetDataSequence inputSequence = LeadSheetIO.readLeadSheet(inputFile);  //read our leadsheet to get a data vessel as retrieved in rbm-provisor
            LogTimer.log("Instantiating autoencoder...");
            int inputSize = 34;
            int outputSize = EncodingParameters.noteEncoder.getNoteLength();
            int featureVectorSize = 100;
            ProductCompressingAutoEncoder autoencoder = new ProductCompressingAutoEncoder(inputSequence, 24, 9, inputSize, outputSize, featureVectorSize, 48, 84+1, false); //create our network
            
            //"pack" the network from weights and biases file directory
            LogTimer.log("Packing autoencoder from files");
            String[] notFound = (new NetworkMeatPacker()).pack(args[2], autoencoder);
            if(notFound.length > 0)
            {
                System.err.println(notFound.length + " files were not able to be matched to the architecture!");
                for(String fileName : notFound)
                {
                    System.err.println("\t" + fileName);
                }
            }
            LeadSheetDataSequence outputSequence = inputSequence.copy();
            int j = 0;
            
            outputSequence.clearMelody();
            
            
            
            LogTimer.startLog("Encoding data...");
            //TradingTimer.initStart(); //start our trading timer to keep track our our generation versus realtime play
            while(inputSequence.hasNext()) { //iterate through time steps in input data
                //TradingTimer.waitForNextTimedInput();
                autoencoder.encodeStep(); //feed the resultant input vector into the network
                if(advanceDecoding) { //if we are using advance decoding (we start decoding as soon as we can)
                    if(autoencoder.canDecode()) { //if queue has enough data to decode from
                        outputSequence.pushStep(null, null, autoencoder.decodeStep()); //take sampled data for a timestep from autoencoder
                        //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are
                    }
                }
            }
            while(autoencoder.hasDataStepsLeft()) { //we are done encoding all time steps, so just finish decoding!{
                    outputSequence.pushStep(null, null, autoencoder.decodeStep()); //take sampled data for a timestep from autoencoder
                    //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are       
            }
            LogTimer.log("Writing file...");
            
            String outputFilename = args[1] + java.io.File.separator + inputFile.getName().replace(".ls", "_Output"); //we'll write our generated file with the same name plus "_Output"
            LeadSheetIO.writeLeadSheet(outputSequence, outputFilename, songTitle);
            System.out.println(outputFilename);
            LogTimer.log("Process finished"); //Done!

        }  
    }
}
