/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.main;

import imp.lstm.architecture.NetworkConnectomeLoader;
import java.io.File;
import imp.lstm.io.leadsheet.LeadSheetDataSequence;
import imp.lstm.architecture.FragmentedNeuralQueue;
import imp.lstm.architecture.FullyConnectedLayer;
import imp.lstm.architecture.LSTM;
import imp.lstm.architecture.Loadable;
import imp.lstm.architecture.poex.ProductCompressingAutoEncoder;
import imp.lstm.encoding.EncodingParameters;
import imp.lstm.encoding.Group;
import imp.lstm.filters.GroupedSoftMaxSampler;
import imp.lstm.filters.Operations;
import imp.lstm.io.leadsheet.LeadSheetIO;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import imp.lstm.architecture.InvalidParametersException;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Class Driver is an implementation test for CompressingAutoEncoder which reads
 * a LeadSheet file and produces an equivalent length LeadSheet file
 *
 * @author Nicholas Weintraut
 */
public class Driver {

    private static final boolean advanceDecoding = false; //should we start decoding as soon as possible?

    public static void main(String[] args) throws FileNotFoundException, IOException, ConfigurationException, InvalidParametersException {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder
                = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFileName(args[0])
                        .setThrowExceptionOnMissing(true)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                        .setIncludesAllowed(false));
        Configuration config = builder.getConfiguration();

        String inputSongPath = config.getString("input_song");
        String outputFolderPath = config.getString("output_folder");
        String autoEncoderParamsPath = config.getString("auto_encoder_params");
        String nameGeneratorParamsPath = config.getString("name_generator_params");
        String queueFolderPath = config.getString("queue_folder");
        String referenceQueuePath = config.getString("reference_queue", "nil");
        String inputCorpusFolder = config.getString("input_corpus_folder");
        boolean shouldWriteQueue = config.getBoolean("should_write_generated_queue");
        boolean frankensteinTest = config.getBoolean("queue_tests_frankenstein");
        boolean interpolateTest = config.getBoolean("queue_tests_interpolation");
        boolean iterateOverCorpus = config.getBoolean("iterate_over_corpus", false);
        boolean shouldGenerateSongTitle = config.getBoolean("generate_song_title");
        boolean shouldGenerateSong = config.getBoolean("generate_leadsheet");

        LogTimer.initStartTime();   //start our logging timer to keep track of our execution time
        LogTimer.log("Creating name generator...");

        //here is just silly code for generating name based on an LSTM lol $wag
        LSTM lstm = new LSTM();
        FullyConnectedLayer fullLayer = new FullyConnectedLayer(Operations.None);
        Loadable titleNetLoader = new Loadable() {
            @Override
            public boolean load(INDArray array, String path) {
                String car = pathCar(path);
                String cdr = pathCdr(path);
                switch (car) {
                    case "full":
                        return fullLayer.load(array, cdr);
                    case "lstm":
                        return lstm.load(array, cdr);
                    default:
                        return false;
                }
            }
        };

        LogTimer.log("Packing name generator from files...");
        (new NetworkConnectomeLoader()).pack(nameGeneratorParamsPath, titleNetLoader);

        String characterString = " !\"'[],-.01245679:?ABCDEFGHIJKLMNOPQRSTUVWYZabcdefghijklmnopqrstuvwxyz";

        //Initialization
        LogTimer.log("Creating autoencoder...");
        int inputSize = 34;
        int outputSize = EncodingParameters.noteEncoder.getNoteLength();
        int featureVectorSize = 100;
        ProductCompressingAutoEncoder autoencoder = new ProductCompressingAutoEncoder(24, 9, inputSize, outputSize, featureVectorSize, 48, 84 + 1, false); //create our network

        int numInterpolationDivisions = 5;

        //"pack" the network from weights and biases file directory
        LogTimer.log("Packing autoencoder from files");
        (new NetworkConnectomeLoader()).pack(autoEncoderParamsPath, autoencoder);

        File[] songFiles;
        if (iterateOverCorpus) {
            songFiles = new File(inputCorpusFolder).listFiles();
        } else {
            songFiles = new File[]{new File(inputSongPath)};
        }
        for (File inputFile : songFiles) {
            (new NetworkConnectomeLoader()).refresh(autoEncoderParamsPath, autoencoder, "initialstate");
            String songTitle;
            if (shouldGenerateSong) {
                Random rand = new Random();
                AVector charOut = Vector.createLength(characterString.length());
                GroupedSoftMaxSampler sampler = new GroupedSoftMaxSampler(new Group[]{new Group(0, characterString.length(), true)});
                songTitle = "";
                for (int i = 0; i < 50; i++) {
                    charOut = fullLayer.forward(lstm.step(charOut));
                    charOut = sampler.filter(charOut);
                    int charIndex = 0;
                    for (; charIndex < charOut.length(); charIndex++) {
                        if (charOut.get(charIndex) == 1.0) {
                            break;
                        }
                    }
                    songTitle += characterString.substring(charIndex, charIndex + 1);
                }
                songTitle = songTitle.trim();

                LogTimer.log("Generated song name: " + songTitle);
            } else {
                songTitle = "The Song We Never Name";
            }
            LogTimer.log("Reading file...");
            LeadSheetDataSequence inputSequence = LeadSheetIO.readLeadSheet(inputFile);  //read our leadsheet to get a data vessel as retrieved in rbm-provisor
            LeadSheetDataSequence outputSequence = inputSequence.copy();

            outputSequence.clearMelody();
            if (interpolateTest) {
                LeadSheetDataSequence additionalOutput = outputSequence.copy();
                for (int i = 0; i < numInterpolationDivisions; i++) {
                    outputSequence.concat(additionalOutput.copy());
                }
            }
            LeadSheetDataSequence decoderInputSequence = outputSequence.copy();

            LogTimer.startLog("Encoding data...");
            //TradingTimer.initStart(); //start our trading timer to keep track our our generation versus realtime play
            while (inputSequence.hasNext()) { //iterate through time steps in input data
                //TradingTimer.waitForNextTimedInput();
                autoencoder.encodeStep(inputSequence.retrieve()); //feed the resultant input vector into the network
                if (advanceDecoding) { //if we are using advance decoding (we start decoding as soon as we can)
                    if (autoencoder.canDecode()) { //if queue has enough data to decode from
                        outputSequence.pushStep(null, null, autoencoder.decodeStep(decoderInputSequence.retrieve())); //take sampled data for a timestep from autoencoder
                        //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are
                    }
                }
            }
            LogTimer.endLog();

            if (shouldWriteQueue) {
                String queueFilePath = queueFolderPath + java.io.File.separator + inputFile.getName().replace(".ls", ".q");
                FragmentedNeuralQueue currQueue = autoencoder.getQueue();
                currQueue.writeToFile(queueFilePath);
                LogTimer.log("Wrote queue " + inputFile.getName().replace(".ls", ".q") + " to file...");
            }
            if (shouldGenerateSong) {
                if (interpolateTest) {

                    FragmentedNeuralQueue refQueue = new FragmentedNeuralQueue();
                    refQueue.initFromFile(referenceQueuePath);

                    FragmentedNeuralQueue currQueue = autoencoder.getQueue();
                    //currQueue.writeToFile(queueFilePath);

                    autoencoder.setQueue(currQueue.copy());
                    while (autoencoder.hasDataStepsLeft()) { //we are done encoding all time steps, so just finish decoding!{
                        outputSequence.pushStep(null, null, autoencoder.decodeStep(decoderInputSequence.retrieve())); //take sampled data for a timestep from autoencoder
                        //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are       
                    }

                    for (int i = 1; i <= numInterpolationDivisions; i++) {
                        System.out.println("Starting interpolation " + ((1.0 / numInterpolationDivisions) * (i)));
                        (new NetworkConnectomeLoader()).refresh(autoEncoderParamsPath, autoencoder, "initialstate");
                        FragmentedNeuralQueue currCopy = currQueue.copy();
                        currCopy.basicInterpolate(refQueue, (1.0 / numInterpolationDivisions) * (i));
                        autoencoder.setQueue(currCopy);
                        int timeStep = 0;
                        while (autoencoder.hasDataStepsLeft()) { //we are done encoding all time steps, so just finish decoding!{
                            System.out.println("interpolation " + i + " step " + ++timeStep);
                            outputSequence.pushStep(null, null, autoencoder.decodeStep(decoderInputSequence.retrieve())); //take sampled data for a timestep from autoencoder
                            //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are       
                        }
                    }

                }
                if (frankensteinTest) {
                    LogTimer.startLog("Loading queues");
                    File queueFolder = new File(queueFolderPath);
                    int numComponents = config.getInt("frankenstein_num_components", 5);
                    int numCombinations = config.getInt("frankenstein_num_combinations", 6);
                    double interpolationMagnitude = config.getDouble("frankenstein_magnitude", 2.0);
                    if (queueFolder.isDirectory()) {
                        File[] queueFiles = queueFolder.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.contains(".q");
                            }
                        });

                        List<File> fileList = new ArrayList<>();
                        for (File file : queueFiles) {
                            fileList.add(file);
                        }
                        Collections.shuffle(fileList);
                        int numSelectedFiles = (numComponents > queueFiles.length) ? queueFiles.length : numComponents;

                        for (int i = 0; i < queueFiles.length - numSelectedFiles; i++) {
                            fileList.remove(fileList.size() - 1);
                        }
                        List<FragmentedNeuralQueue> queuePopulation = new ArrayList<>(fileList.size());
                        songTitle += " - a mix of ";
                        for (File file : fileList) {
                            FragmentedNeuralQueue newQueue = new FragmentedNeuralQueue();
                            newQueue.initFromFile(file.getPath());
                            queuePopulation.add(newQueue);
                            songTitle += file.getName().replaceAll(".ls", "") + ", ";
                        }
                        LogTimer.endLog();

                        LeadSheetDataSequence additionalOutput = outputSequence.copy();
                        for (int i = 1; i < numCombinations; i++) {
                            outputSequence.concat(additionalOutput.copy());
                        }
                        decoderInputSequence = outputSequence.copy();

                        FragmentedNeuralQueue origQueue = autoencoder.getQueue();

                        for (int i = 0; i < numCombinations; i++) {
                            
                            LogTimer.startLog("Performing queue interpolation...");
                            AVector combinationStrengths = Vector.createLength(queuePopulation.size());
                            Random vectorRand = new Random(i);
                            for (int j = 0; j < combinationStrengths.length(); j++) {
                                combinationStrengths.set(j, vectorRand.nextDouble());
                            }
                            combinationStrengths.divide(combinationStrengths.elementSum());
                            FragmentedNeuralQueue currQueue = origQueue.copy();
                            for (int k = 0; k < combinationStrengths.length(); k++) {
                                currQueue.basicInterpolate(queuePopulation.get(k), combinationStrengths.get(k) * interpolationMagnitude);
                            }
                            LogTimer.endLog();
                            autoencoder.setQueue(currQueue);
                            LogTimer.startLog("Refreshing autoencoder state...");
                            (new NetworkConnectomeLoader()).refresh(autoEncoderParamsPath, autoencoder, "initialstate");
                            LogTimer.endLog();
                            LogTimer.startLog("Decoding segment...");
                            while (autoencoder.hasDataStepsLeft()) { //we are done encoding all time steps, so just finish decoding!{
                                outputSequence.pushStep(null, null, autoencoder.decodeStep(decoderInputSequence.retrieve())); //take sampled data for a timestep from autoencoder
                                //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are       
                            }
                            LogTimer.endLog();
                        }

                    }
                }

                while (autoencoder.hasDataStepsLeft()) { //we are done encoding all time steps, so just finish decoding!{
                    outputSequence.pushStep(null, null, autoencoder.decodeStep(decoderInputSequence.retrieve())); //take sampled data for a timestep from autoencoder
                    //TradingTimer.logTimestep(); //log our time to TradingTimer so we can know how far ahead of realtime we are       
                }
                LogTimer.log("Writing file...");

                String outputFilename = outputFolderPath + java.io.File.separator + inputFile.getName().replace(".ls", "_Output"); //we'll write our generated file with the same name plus "_Output"
                LeadSheetIO.writeLeadSheet(outputSequence, outputFilename, songTitle);
                System.out.println(outputFilename);
            } else {
                autoencoder.setQueue(new FragmentedNeuralQueue());
            }
        }
        LogTimer.log("Process finished"); //Done!

    }
}
