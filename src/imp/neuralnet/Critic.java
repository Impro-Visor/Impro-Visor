/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

import imp.ImproVisor;
import imp.com.SetChordsCommand;
import imp.data.BitVectorGenerator;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.ChordSymbol;
import imp.data.Coloration;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.Score;
import imp.data.Unit;
import imp.gui.Notate;
import imp.lickgen.LickgenFrame;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.*;
import polya.Polylist;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern (Based on Robert Keller's C++ implementation of a 
 *                           multi-layer neural network for backpropagation)
 */
public class Critic implements imp.Constants {
    
    // output should always be 1
    private final int outputDimension = 1;
    private int lickLength = 0;
    
    public static final int ONLINE = 0;
    public static final int BATCH = 1;
    public static final int RPROP = 2;
    
    public enum LEARNING_MODE
    {
        ONLINE, BATCH, RPROP    
    }
    
    String modeName[] = {"on-line", "batch", "rprop"};
    
    public static final int NO_REASON = 0;
    public static final int GOAL_REACHED = 1;
    public static final int LIMIT_EXCEEDED = 2;
    public static final int LACK_OF_PROGRESS = 3;
    public static final int WEIGHTS_ALREADY_SET = 4;
    
    public enum TERMINATION_REASON 
    {
        NO_REASON, GOAL_REACHED, LIMIT_EXCEEDED, LACK_OF_PROGRESS, WEIGHTS_ALREADY_SET
    }
    
    String reasonName[] = {"", "goal reached", "limit exceeded", 
                           "lack of progress", "testing with set weights"};
    
    public final LEARNING_MODE defaultMode = LEARNING_MODE.ONLINE;
    public final double defaultGoal = 0.01;
    public final double defaultLearningRate = 0.01;
    public final int defaultEpochLimit = 20000;
    public final int defaultTrace = 1;
    public final int minimumParameters = 9;

    ActivationFunction hardlim  = new Hardlim();
    ActivationFunction hardlims = new Hardlims();
    ActivationFunction logsig   = new Logsig();
    ActivationFunction purelin  = new Purelin();
    ActivationFunction satlin   = new Satlin();
    ActivationFunction satlins  = new Satlins();
    ActivationFunction tansig   = new Tansig();
    ActivationFunction elliot   = new Elliot();
    ActivationFunction elliots  = new Elliots();
    
    private Network network;
    private Notate notate; 
    private LickgenFrame lickgenFrame;
    
    /*
     * Initializes network
     */
    public Critic()
    {
        network = null;
    }
    
    /*
     * Gets ActivationFunction from String
     */
    private ActivationFunction getLayerType(String name)
    {
        name = name.toLowerCase();
        if( "hardlim".equals(name) )  return hardlim;
        if( "hardlims".equals(name) ) return hardlims;
        if( "logsig".equals(name) )   return logsig;
        if( "purelin".equals(name) )  return purelin;
        if( "satlin".equals(name) )   return satlin;
        if( "satlins".equals(name) )  return satlins;
        if( "tansig".equals(name) )   return tansig;
        if( "elliot".equals(name) )   return elliot;
        if( "elliots".equals(name) )  return elliots;

        System.out.println("error, unrecognized function: " + name);
        return null;
    }

    /*
     * Initializes all of the samples from input
     */
    private void getSamples(String inputFile, AtomicInteger outputDimension, 
            AtomicInteger inputDimension, ArrayList<Sample> listOfSamples) throws Exception
    {
        File f = new File(inputFile);
        
        if (!f.exists())
        {
            //do something
        }
        
        Scanner in = new Scanner(f);
        
        // Save input and output dimensions
        outputDimension.set(in.nextInt());
        inputDimension.set(in.nextInt());
        in.nextLine();
        
        while (in.hasNextLine())
        {
            String line = in.nextLine();
            Scanner reader = new Scanner(line);
            Sample thisSample = new Sample(outputDimension.get(), inputDimension.get());
            
            for (int i = 0; i < outputDimension.get(); i++)
                thisSample.setOutput(i, reader.nextDouble());
            for (int j = 0; j < inputDimension.get(); j++)
                thisSample.setInput(j, reader.nextDouble());
            
            listOfSamples.add(thisSample);
        }
    }
    
    /*
     * Counts the samples
     */
    private String showAndCountSamples(String title, 
            ArrayList<Sample> samples, AtomicInteger nSamples)
    {
        StringBuilder output = new StringBuilder();
        
        if (Trace.atLevel(4))
            System.out.println("\n" + title + " samples are:\n");

        for (Sample s : samples)
        {
            nSamples.getAndIncrement();
            
            if (Trace.atLevel(4))
                System.out.println(nSamples.get() + ": "+ s.toString());
        }
        
        output.append(nSamples.get()).append(" ").append(title).append(" samples");
        output.append("\n");
        
        return output.toString();
    }
    
    /*
     * Parse a sample of data we're given
     */
    private Sample parseData (String data, Sample s)
    {
        String[] inputs = data.split(" ");
        for (int i = 0; i < inputs.length; i++)
        {
            if (i == 0)
            {
                double gradeOutput = Double.parseDouble(inputs[i]);
                s.setOutput(0, gradeOutput);
            }
            else if (inputs[i].length() != 1)
            {
                break;
            }
            else
            {
                double dataInput = Double.parseDouble(inputs[i]);
                
                try 
                {
                     s.setInput(i - 1, dataInput);
                }
                catch (Exception e)
                {
                    break;
                }
            }
        }
        return s;
    }
    
    
    /*
     * Used to grade a single lick from a sample
     */
    private double filter(String data)
    {
        //Get sample inputDimension
        int inputDimension = (data.length() - 4) / 2;
        
        Sample s = new Sample(outputDimension, inputDimension);
        
        parseData(data, s);
        network.use(s);
        
        // Multiple by 10 for correct output
        Double singleOutput = network.getSingleOutput() * 10;
        return singleOutput;
    }
    
    /*
     * Private Runnable class that can take arguments
     */
    private class MyRunnable implements Runnable
    {
        public MyRunnable (Object ... args)
        {}

        public void run() 
        {}  
    }
    
    //training
    private String trainingFile;
    
    //epoch
    private int epochLimit;
    
    //rate
    private double rate;
    
    //goal
    private double goal;
    
    //mode
    private int modeInt;
    private LEARNING_MODE mode;
    
    //weights
    private String weightFile;
    
    //layers
    private int numberLayers;
    private int[] layerSize;
    private ActivationFunction[] layerType;
    
    /*
     * Trains the network given the correct parameters.
     * Executes in a separate thread.
     */
    public void trainNetwork(final Object ... args)
    {  
        new Thread(new MyRunnable(args) {
            @Override
            public void run() {
                
        // If we have weights already, set this to true
        boolean fixWeights = false;
        
        notate = ImproVisor.getCurrentWindow();
        lickgenFrame = notate.getLickgenFrame();
        
        // Refresh output screen
        lickgenFrame.setNetworkOutputTextField("");
        
        //Asumed
        trainingFile = (String) args[0];
        
        if (args[1] != null)
            epochLimit = Integer.parseInt( (String) args[1]);
        else
            epochLimit = defaultEpochLimit;
        
        if (args[2] != null)
            rate = Double.parseDouble( (String) args[2]);
        else
            rate = defaultLearningRate;
        
        if (args[3] != null)
            goal = Double.parseDouble( (String) args[3]);
        else
            goal = defaultGoal;
        
        if (args[4] != null)
        {
            int modeTemp = Integer.parseInt( (String) args[4]);
            mode = LEARNING_MODE.values()[modeTemp];
        }
        else
            mode = defaultMode;
        
        if (args[5] != null)
            weightFile = (String) args[5];
        else
            weightFile = trainingFile + ".weights.save";
        
        numberLayers = (Integer) args[6];
        ArrayList<Object[]> layerData = (ArrayList<Object[]>) args[7];
        layerSize = new int[numberLayers];
        layerType = new ActivationFunction[numberLayers];
        
        int j = 0;
        for (Object[] items : layerData)
        {
            layerSize[j] = (Integer) items[0];
            layerType[j] = getLayerType((String) items[1]);
            j++;
        }
        
        // Check if we're using a file with already-set weights
        File f = new File(ImproVisor.getVocabDirectory(), weightFile);
        if (f.length() != 0)
            fixWeights = true;
        
        lickgenFrame.appendNetworkOutputTextField(numberLayers + " layers structured (from input to output) as: \n");

        for( int i = 0; i < numberLayers; i++ )
        {
            lickgenFrame.appendNetworkOutputTextField("    " + layerType[i].getName()
                    + " (" + layerSize[i] + " " + "neurons" + ")" + "\n");
        }

        lickgenFrame.appendNetworkOutputTextField("\n");
        lickgenFrame.appendNetworkOutputTextField("epoch limit = " + epochLimit + "\n");
        lickgenFrame.appendNetworkOutputTextField("specified rate = " + rate + "\n");
        lickgenFrame.appendNetworkOutputTextField("goal = " + goal + "\n");
        lickgenFrame.appendNetworkOutputTextField("mode = " + modeName[mode.ordinal()] + "\n");
        lickgenFrame.appendNetworkOutputTextField("\n");
            
        AtomicInteger inputD = new AtomicInteger();
        AtomicInteger outputD = new AtomicInteger();
        ArrayList<Sample> trainingSamples = new ArrayList<Sample>();
        
        try 
        {
            getSamples(trainingFile, outputD, inputD, trainingSamples);
        }
        catch (Exception e)
        {
            
        }
        
        AtomicInteger nTrainingSamples = new AtomicInteger();
        lickgenFrame.appendNetworkOutputTextField(showAndCountSamples("training", trainingSamples, nTrainingSamples));
        lickgenFrame.appendNetworkOutputTextField("Sample input size is " + inputD.get() + "\n");
        lickgenFrame.appendNetworkOutputTextField("Sample output size is " + outputD.get() + "\n");
        
        Network thisNetwork = new Network(numberLayers, layerSize, layerType, inputD.get());
     
        if (fixWeights)
        {
            try 
            {
                BufferedReader in = new BufferedReader(new FileReader(
                        new File(ImproVisor.getVocabDirectory(), weightFile)));
                thisNetwork.fixWeights(in);
                in.close();
            }
            catch (Exception e) 
            {
                
            }    
        }
        
        if (Trace.atLevel(4))
        {
            System.out.println("\nInitial Weights:");
            System.out.println(thisNetwork.showWeights("initial"));
        }
        
        lickgenFrame.appendNetworkOutputTextField("\nTraining begins with epoch 1.\n");
        
        int epoch = 1;
        double sse;
        double mse = 1 + goal;
        double oldmse = 2 + goal;
        double etaPlus = 1.2; //for rprop
        double etaMinus = 0.5;
        
        TERMINATION_REASON reason = TERMINATION_REASON.NO_REASON;
        
        if (fixWeights)
        {
            reason = TERMINATION_REASON.WEIGHTS_ALREADY_SET;
        }
        
        // The total number of output values across all samples and network outputs
        
        int interval = 1;
        
        // Training loop
        while ( reason == TERMINATION_REASON.NO_REASON)
        {
            sse = 0;
            switch (mode)
            {
                case RPROP:
                case BATCH:
                    thisNetwork.clearAccumulation();;
                case ONLINE:
            }
            
            for (Sample sample : trainingSamples)
            {
                // Forward propagation
                thisNetwork.fire(sample);
                double sampleSSE = thisNetwork.computeError(sample);
                sse += sampleSSE;
                
                if (Trace.atLevel(4))
                {
                    System.out.print("\nforward output: ");
                    thisNetwork.showOutput();
                    System.out.print(sample);
                    System.out.printf(", sample sse: % 6.3f\n", sampleSSE);
                }
                
                // backpropagation
                thisNetwork.setSensitivity(sample);
                
                switch (mode)
                {
                    case RPROP:
                        thisNetwork.accumulateGradient(sample);
                    case BATCH:
                        thisNetwork.accumulateWeights(sample, rate);
                    case ONLINE:
                        thisNetwork.adjustWeights(sample, rate);
                }
                
                if (Trace.atLevel(4))
                {
                    thisNetwork.showWeights("current");
                }
            }
            
            switch (mode)
            {
                case RPROP:
                    thisNetwork.adjustByRprop(etaPlus, etaMinus);
                case BATCH:
                    thisNetwork.installAccumulation();
                case ONLINE:
            }

            mse = sse / nTrainingSamples.get();

            double usageError = 0;

            // Evaluate with use
            for (Sample sample : trainingSamples)
            {
                thisNetwork.use(sample);
                usageError += (thisNetwork.computeUsageError(sample) != 0) ? 1 : 0;
            }
            
            lickgenFrame.appendNetworkOutputTextField(String.format("end epoch %d, mse: %10.8f %s, usage error: %d/%d (%5.2f%%)\n",
                                                        epoch, 
                                                        mse, 
                                                        mse < oldmse ? "decreasing" : "increasing",
                                                        (int)usageError,
                                                        nTrainingSamples.get(),
                                                        100 * usageError / nTrainingSamples.get()));
            lickgenFrame.appendNetworkOutputTextField("\n");

            epoch++;

            if (mse <= goal)
            {
                reason = TERMINATION_REASON.GOAL_REACHED;
            }
            else if (epoch > epochLimit)
            {
                reason = TERMINATION_REASON.LIMIT_EXCEEDED;
            }

            oldmse = mse;
        }
        
        lickgenFrame.appendNetworkOutputTextField("Training ends at epoch " + epoch
                + ", " + reasonName[reason.ordinal()] + ".");
        lickgenFrame.appendNetworkOutputTextField("\n" + "\n");
        
        if (Trace.atLevel(4))
        {
            lickgenFrame.appendNetworkOutputTextField("Final weights:");
            lickgenFrame.appendNetworkOutputTextField("\n");
            lickgenFrame.appendNetworkOutputTextField(thisNetwork.showWeights("Final").toString());
        }
     
        try 
        {
            PrintWriter out = new PrintWriter(new File(ImproVisor.getVocabDirectory(), weightFile));
            out.print(inputD.get() + " " + numberLayers);
            for (int i = 0; i < numberLayers; i++)
                out.print(" " + layerSize[i] + " " + layerType[i].getName());            
            out.println();
            thisNetwork.printWeights("final", out);
            out.close();
        } 
        catch (Exception e) 
        {
            
        }
            } // End of Runnable
        }).start(); // End of Thread
    }
     
    /*
     * Generate note and chord list from a score
     */
    public void generateNotesAndChords(ArrayList<Note> noteList, ArrayList<Chord> chordList,
                                          int start, int end)
    {
        // Fake grade for the lick, so the input for the critic is correct
        int grade = 1;
        
        // Get Notate panel
        notate = ImproVisor.getCurrentWindow();
   
        // Parse stave selection into notes and chords
        String lickStr = notate.getCurrentStave().extract("<Extracted Lick>", ExtractMode.LICK, grade, start, end);
        Polylist lick = Notate.parseListFromString(lickStr);
            if(lick.length() == 1 && lick.first() instanceof Polylist && ((Polylist) lick.first()).length() > 1) {
                lick = (Polylist) (lick.first());
            }
            Polylist notes = Polylist.list();
            Polylist chords = Polylist.list();
            String name = "";
            while(lick.nonEmpty()) {
                Object o = lick.first();
                if(o instanceof Polylist) {
                    Polylist p = (Polylist) o;
                    String s = (String) p.first();
                    if(s.equals("notes")) {
                        notes = p.rest();
                    } else if(s.equals("sequence")) {
                        chords = p.rest();
                    } else if(s.equals("name")) {
                        name = (String) p.rest().implode(" ");
                    }
                }
                lick = lick.rest();
            }
        
        // Generate score to get exact chord durations  
        ChordPart chordsList = new ChordPart(notate.getScoreLength() - 1);
        MelodyPart melody = new MelodyPart();
        Polylist combined = chords.append(notes);
        (new SetChordsCommand(0, combined, chordsList, melody)).execute();
        chordsList.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));
        Score score = new Score();
        score.setChordProg(chordsList);
        score.addPart(melody);
        ArrayList<ChordSymbol> symbols = score.getChordProg().getChordSymbols();
        ArrayList<Integer> durations = score.getChordProg().getChordDurations();
        
        // Add all chords to chord list
        for (int i = 0; i < symbols.size(); i++)
        {
            chordList.add(new Chord(symbols.get(i), durations.get(i)));
        }
        
        // Add all notes to note list
        while(!notes.isEmpty()) {
            while(!notes.isEmpty() && notes.first() == null) {
                notes = notes.rest();
            }
            
            if(!notes.isEmpty()) {
                noteList.add(NoteSymbol.toNote(notes.first().toString()));
                notes = notes.rest();
            }
        }
    }
    
    /*
     * Takes a list of chords and notes and will generate a grade
     */
    public Double gradeFromCritic(ArrayList<Note> noteList, ArrayList<Chord> chordList)
    {
        StringBuilder output = new StringBuilder();
        AtomicBoolean error = new AtomicBoolean(false);
               
        // Create new Melody Part
        MelodyPart melody = new MelodyPart();
        ChordPart chords = new ChordPart();
        for (Note n : noteList)
           melody.addNote(n);
        for (Chord c: chordList)
            chords.addChord(c);
   
        ArrayList<Double> grades = new ArrayList<Double>();
        int currStart = 0;
        int currEnd = BEAT * 8 - 1;
        int size = melody.size() - 1;
        int beatPosition;
        
        // Get classifications for all notes
        int [] classifications = Coloration.collectNoteColors(melody, chords);
    
        while (currEnd <= size && !error.get())
        {
            // Random grade needed for correct length of input
            int grade = 1;

            beatPosition = currStart;
            MelodyPart currMelody = melody.extract(currStart, currEnd);
            ArrayList<Unit> unitList = currMelody.getUnitList();
            ArrayList<Note> currNoteList = new ArrayList<Note>();
            for (Unit u : unitList)
                currNoteList.add((Note) u);
            
            output.append(String.valueOf(grade / 10.0));
            output.append(' ');

            // Print all note data for all notes within one lick
            for (int index = 0; index < currNoteList.size(); index++)
            {
                int indexPrev = index - 1;
                int currNoteClassification = Coloration.getNoteClassification(classifications[beatPosition]);

                if (indexPrev < 0)
                {
                    if (currStart == 0)
                    {
                        beatPosition = BitVectorGenerator.printNoteData(output, null, currNoteList.get(index), 
                                currNoteClassification, beatPosition, error);
                    }
                    else
                    {   
                        // For correct distance to preceding note
                        Note prevNote = melody.getPrevNote(currStart);
                        beatPosition = BitVectorGenerator.printNoteData(output, prevNote, currNoteList.get(index), 
                                currNoteClassification, beatPosition, error);
                    }
                }
                else
                {
                    beatPosition = BitVectorGenerator.printNoteData(output, currNoteList.get(indexPrev), 
                            currNoteList.get(index), currNoteClassification, beatPosition, error);
                }
            }
            
            if (!error.get())
            {     
                // Fix the length if the lick is too short or too long
                if (output.length() > lickLength)
                {
                    output.delete(lickLength, output.length());
                }

                while (output.length() < lickLength)
                {
                    // Add a whole note rest landing on beat 1 
                    output.append(BitVectorGenerator.WHOLE_REST);
                }

                grades.add(filter(output.toString()));
            }
            
            // Move two beats ahead
            currStart += BEAT * 2;
            currEnd += BEAT * 2;  
            
            output.delete(0, output.length());
        }
        
        if (!error.get() && !grades.isEmpty())
        {
            double accum = 0;
            for (Double d : grades)
            {
                accum += d;
            }
            return accum / grades.size();
        }
        else
        {
            // Represents an error
            return null;
        }
    }
    
    /*
     * Grade only two measures
     */
    public Double gradeTwoMeasures(ArrayList<Note> noteList, 
                         ArrayList<Chord> chordList, Note prevNote, int currBeatPos)
    {
        StringBuilder output = new StringBuilder();
        AtomicBoolean error = new AtomicBoolean(false);
        int beatPosition = 0;
               
        // Create new Melody Part
        MelodyPart melody = new MelodyPart();
        ChordPart chords = new ChordPart();
        for (Note n : noteList)
           melody.addNote(n);
        for (Chord c: chordList)
            chords.addChord(c);
        
        // Get classifications for all notes
        int [] classifications = Coloration.collectNoteColors(melody, chords);
        
        // Random grade needed for correct length of input
        int grade = 1;

        beatPosition = currBeatPos;

        output.append(String.valueOf(grade / 10.0));
        output.append(' ');

        // Print all note data for all notes within one lick
        for (int index = 0; index < noteList.size(); index++)
        {
            int indexPrev = index - 1;
            int currNoteClassification = Coloration.getNoteClassification(classifications[beatPosition - currBeatPos]);

            if (indexPrev < 0)
            {
                // For correct distance to preceding note
                beatPosition = BitVectorGenerator.printNoteData(output, prevNote, noteList.get(index), 
                       currNoteClassification, beatPosition, error);            
            }
            else
            {
                beatPosition = BitVectorGenerator.printNoteData(output, noteList.get(indexPrev), 
                        noteList.get(index), currNoteClassification, beatPosition, error);
            }
        }

        // Fix the length if the lick is too short or too long
        if (output.length() > lickLength)
        {
            output.delete(lickLength, output.length());
        }

        while (output.length() < lickLength)
        {
            // Add a whole note rest landing on beat 1 
            output.append(BitVectorGenerator.WHOLE_REST);
        }

        if (!error.get())
        {
            return filter(output.toString());
        }
        else
        {
            // Represents an error
            return null;
        }
    }
   
    /*
     * Initializes the network given a weight file
     */
    public StringBuilder prepareNetwork(String weights) throws Exception
    {
        try 
        {
            File file = new File(ImproVisor.getVocabDirectory(), weights);
            BufferedReader in = new BufferedReader(new FileReader(file));
            StringBuilder output = new StringBuilder();
            
            // Parse first line, containing network stats
            String[] networkInfo = in.readLine().split(" ");
            int currPos = 0;
            int thisInputDimension = Integer.parseInt(networkInfo[currPos++]);
            lickLength = (thisInputDimension * 2) + 4;
            int thisNumLayers = Integer.parseInt(networkInfo[currPos++]);
            int[] thisLayerSize = new int[thisNumLayers];
            ActivationFunction[] thisLayerType = new ActivationFunction[thisNumLayers];
            
            int j = 0;
            int layers = currPos + 2 * thisNumLayers; 
            for (int i = 2; i < layers; i+=2, j++)
            {
                thisLayerSize[j] = Integer.parseInt(networkInfo[currPos++]);
                thisLayerType[j] = getLayerType(networkInfo[currPos++]);
            }

            output.append("length of input: ").append(thisInputDimension).append("\n");
            output.append(thisNumLayers).append(" layers structured (from input to output) as: \n");
            for( int i = 0; i < thisNumLayers; i++ )
            {
                output.append("    ").append(thisLayerType[i].getName()).append(" (").append(thisLayerSize[i]).append(" " + "neurons" + ")");
                output.append("\n");
            }
            
            network = new Network(thisNumLayers, thisLayerSize, thisLayerType, thisInputDimension);
            network.fixWeights(in);
            in.close();
            
            return output;
        }
        catch (Exception e) 
        {
            throw new Exception(e);
        }
    }
    
    /*
     * Initializes the network given a specific weight file
     */
    public void prepareNetworkFromFile(File file) throws Exception
    {
        try 
        {
            BufferedReader in = new BufferedReader(new FileReader(file));
            
            // Parse first line, containing network stats
            String[] networkInfo = in.readLine().split(" ");
            int currPos = 0;
            int thisInputDimension = Integer.parseInt(networkInfo[currPos++]);
            lickLength = (thisInputDimension * 2) + 4;
            int thisNumLayers = Integer.parseInt(networkInfo[currPos++]);
            int[] thisLayerSize = new int[thisNumLayers];
            ActivationFunction[] thisLayerType = new ActivationFunction[thisNumLayers];
            
            int j = 0;
            int layers = currPos + 2 * thisNumLayers; 
            for (int i = 2; i < layers; i+=2, j++)
            {
                thisLayerSize[j] = Integer.parseInt(networkInfo[currPos++]);
                thisLayerType[j] = getLayerType(networkInfo[currPos++]);
            }
            
            network = new Network(thisNumLayers, thisLayerSize, thisLayerType, thisInputDimension);
            network.fixWeights(in);
            in.close();
        }
        catch (Exception e) 
        {
            throw new Exception(e);
        }
    }
    
    /*
     * Returns the network
     */
    public Network getNetwork()
    {
        return network;
    }
    
    /*
     * Reset the network
     */
    public void resetNetwork()
    {
        network = null;
    }
    
}
