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

package imp.lstm.architecture.poex;

import imp.lstm.architecture.NetworkConnectomeLoader;
import java.io.File;
import java.io.IOException;
import imp.lstm.io.leadsheet.LeadSheetDataSequence;
import imp.lstm.architecture.CompressingAutoEncoder;
import imp.lstm.architecture.FullyConnectedLayer;
import imp.lstm.architecture.LSTM;
import imp.lstm.architecture.LeadsheetAutoencoderInputManager;
import imp.lstm.architecture.Loadable;
import imp.lstm.architecture.poex.ProductCompressingAutoencoder;
import imp.lstm.encoding.EncodingParameters;
import imp.lstm.encoding.Group;
import imp.lstm.filters.GroupedSoftMaxSampler;
import imp.lstm.filters.Operations;
import imp.lstm.io.leadsheet.LeadSheetIO;
import imp.lstm.main.LogTimer;
import java.util.Random;
import imp.lstm.architecture.InvalidParametersException;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 *  Class Driver is an implementation test for CompressingAutoEncoder which reads a LeadSheet file and produces an equivalent length LeadSheet file
 * @author Nicholas Weintraut
 */
public class GenerativeProductDriver {
    private static final boolean advanceDecoding = false; //should we start decoding as soon as possible?
    
    public static void main(String[] args) throws InvalidParametersException, IOException {
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
        
        (new NetworkConnectomeLoader()).load(args[3], titleNetLoader);
        
        Random rand = new Random();
        String characterString = " !\"'(),-.01245679:?ABCDEFGHIJKLMNOPQRSTUVWYZabcdefghijklmnopqrstuvwxyz";
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
            GenerativeProductModel genmodel = new GenerativeProductModel(48, 84+1); //create our network
            
            //"pack" the network from weights and biases file directory
            LogTimer.log("Packing autoencoder from files");
            (new NetworkConnectomeLoader()).load(args[2], genmodel);
            LeadSheetDataSequence outputSequence = inputSequence.copy();
            outputSequence.clearMelody();
            
            LogTimer.startLog("Encoding data...");
            while(inputSequence.hasNext()) { //iterate through time steps in input data
                outputSequence.pushStep(null, null, genmodel.step(inputSequence.retrieve()));
            }
            LogTimer.log("Writing file...");
            
            String outputFilename = args[1] + java.io.File.separator + inputFile.getName().replace(".ls", "_Output_") + songTitle; //we'll write our generated file with the same name plus "_Output"
            LeadSheetIO.writeLeadSheet(outputSequence, outputFilename, songTitle);
            System.out.println(outputFilename);
            LogTimer.log("Process finished"); //Done!

        }  
    }
}
