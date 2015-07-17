package imp.audio;

import imp.Constants;
import imp.data.MelodyPart;
import imp.data.MidiSynth;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import imp.gui.Notate.Mode;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.*;
//import org.apache.commons.math3.complex.Complex;

/**
 * Class to capture and interpret audio input. The McLeod Pitch Method is
 * implemented as a means of pitch detection.
 *
 * @author Brian Howell
 * @version 22 June, 2012
 */
public class PitchExtractor implements Constants
{

    public volatile boolean isCapturing;
    public volatile Boolean stopCapture;
    public final Boolean thisCapture = false;
    boolean stopAnalysis;
    AudioInputStream inputStream;
    AudioFormat format;
    //SourceDataLine source;
    TargetDataLine target;
    Score score;
    Notate notate;
    MidiSynth midiSynth;
//    private long startTime;
//    private int additionalSamples;
    private boolean processingStarted = false;
    private long delay;
    //private double swingVal;
    private int captureInterval;
    private int lastSlotNumber;
    private int startingPosition;
    private int currentIndex;
    int positionOffset;
    //timeDifference keeps track of time offset between captures
    double timeDifference = 0;
    List<Integer> oneSlot = new ArrayList<Integer>();
    int slotsFilled; //# of slots filled before pitch change is detected
    int lastPitch;
    int analysesCompleted; //# of analyses completed thus far.
    //if capture has trouble finishing the first capture, increase this value.
//    private final double OFFSET_ADJUSTMENT = 2.;
//    private final int FIRST_CAPTURE_TRUNCATION = 0;
    private final float SAMPLE_RATE = 44100.0F; //in Hertz
    private final int SAMPLE_SIZE = 16; //1 sample = SAMPLE_SIZE bits
    private int FRAME_SIZE; //# of BYTES examined per poll
    private float POLL_RATE; //in milliseconds
    private final int tenMSOffset = (int) ((10.0 / 1000.0) * SAMPLE_RATE * 2.0);
    private final double interval;
    private int RESOLUTION; //smallest subdivision allowed
    private int slotConversion;
    private boolean allowTriplets;
    private boolean noteOff; //flag indicating terminal note
    //only windows with a RMS above this threshold will be examined
    private double RMS_THRESHOLD;
    private double CONFIDENCE_THRESHOLD;
    private double CONFIDENCE;
    //sets threshold for detecting peaks in normalized SDF
    private double K_CONSTANT;
    private final Queue<byte[]> processingQueue;
    private int minPitch = 0;
    private int maxPitch = 127;
    AudioSettings settings;
    MelodyPart melodyPart;

    Mixer mainMixer;

    private boolean trading;
    private int tradeLength;

    public PitchExtractor(Notate notate,
                          Score score,
                          MidiSynth midiSynth,
                          AudioSettings settings,
                          int startingIndex,
                          int captureInterval,
                          boolean trading)
    {
        this.notate = notate;
        this.score = score;
        this.midiSynth = midiSynth;
        //swingVal = score.getChordProg().getStyle().getSwing();
        this.captureInterval = captureInterval;
        tradeLength = notate.getTradeLength();
        System.out.println("Trade length = " + tradeLength);
        this.trading = trading;
        startingPosition = startingIndex;
        currentIndex = startingIndex;
        melodyPart = notate.getCurrentMelodyPart();

        this.settings = settings;
        RMS_THRESHOLD = settings.getRMS_THRESHOLD();
        CONFIDENCE_THRESHOLD = settings.getCONFIDENCE_THRESHOLD();
        allowTriplets = settings.isTRIPLETS();
        RESOLUTION = settings.getRESOLUTION();
        K_CONSTANT = settings.getK_CONSTANT();
        POLL_RATE = settings.getPOLL_RATE();
        FRAME_SIZE = settings.getFRAME_SIZE();
        slotConversion = (score.getMetre()[0] * BEAT) / RESOLUTION;
        interval = (POLL_RATE / 1000.0) * SAMPLE_RATE * 2.0;

        format = getAudioFormat();
        processingQueue = new ArrayBlockingQueue<byte[]>(10);

//        for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo())
//            {
//                Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
//                if(thisMixer.getTargetLines().length > 0)
//                {
//                    mainMixer = thisMixer;
//                    System.out.println("Mixer: " + thisMixerInfo.getDescription()
//                        + " [" + thisMixerInfo.getName() + "] # TDLs = " + mainMixer.getTargetLines().length);
//                    break;
//                }
//                else
//                {
////                    System.out.println(thisMixerInfo.getName() + " (Mixer) "
////                            + thisMixerInfo.getName() + " has no TDLs.");
//                }
//            }
//        Mixer.Info mainMixerInfo = AudioSystem.getMixerInfo()[0];
//        mainMixer = AudioSystem.getMixer(mainMixerInfo);
//        System.out.println("Mixer = " + mainMixerInfo.getDescription()
//                + ", " +mainMixerInfo.getName());
//        for (Line.Info thisSourceInfo : mainMixer.getSourceLineInfo())
//            {
//                System.out.println("SourceLineInfo:" + thisSourceInfo.toString());
//            }
    }

    public void captureAudio()
    {
        stopCapture = false;
        stopAnalysis = false;
        //processingStarted = false;
//        currentIndex = 0;
//        if (analysesCompleted > 0)
//        {
            clearNotes(currentIndex); //erase old data if overwriting
            analysesCompleted = 0;
//        }
        openTargetLine();
        slotsFilled = 1;
        lastSlotNumber = 1;
        lastPitch = 0; //initialize most recent pitch to a rest
        minPitch = settings.getMIN_PITCH();
        maxPitch = settings.getMAX_PITCH();
        try
        {
            CaptureThread captureThread = new CaptureThread();
            captureThread.setPriority(Thread.MAX_PRIORITY);
            captureThread.start();
//            MixerGetterThread mixerThread = new MixerGetterThread();
//            mixerThread.setPriority(Thread.MAX_PRIORITY - 1);
//            mixerThread.start();
            AnalyzeThread analyzeThread = new AnalyzeThread();
            analyzeThread.setPriority(Thread.MAX_PRIORITY - 1);
            analyzeThread.start();
        } catch (Exception e)
        {
            System.out.println("Error initializing audio capture:\n" + e);
        }//end catch
    }//end captureAudio method

        public void openTargetLine()
    {
        try
        {
            //print out mixer & target/source line info
//            for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo())
//            {
//                System.out.println("Mixer: " + thisMixerInfo.getDescription()
//                        + " [" + thisMixerInfo.getName() + "]");
//                Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
//                for (Line.Info thisLineInfo : thisMixer.getSourceLineInfo())
//                {
//                    if (thisLineInfo.getLineClass().getName().equals(
//                            "javax.sound.sampled.Port"))
//                    {
//                        Line thisLine = thisMixer.getLine(thisLineInfo);
//                        thisLine.open();
//                        System.out.println("  Source Port: "
//                                + thisLineInfo.toString());
//                        thisLine.close();
//                    }
//                }
//            }

            DataLine.Info dataLineInfo =
                    new DataLine.Info(TargetDataLine.class, format);
            target = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            target.open(format);
            //System.out.println("TDL info:\n" + target.getLineInfo().toString());
            //target.start();

            //synchronizeLines();

        } catch (Exception e)
        {
            System.out.println("Error opening Target Line:\n" + e);
        }
    }

    public void synchronizeLines()
    {
        //synchronize the TargetDataLine with the main source line for audio output
        Line[] lines = new Line[2];
        if (mainMixer.getSourceLines()[0] != null)
        {
            lines[0] = mainMixer.getSourceLines()[0];
            lines[1] = target;
            mainMixer.synchronize(lines, false);
        }
    }

    public void closeTargetLine()
    {
        try
        {
            target.close();
            //target.stop();
        } catch (Exception e)
        {
            System.out.println("TargetLine stoppage error.");
        }
    }

    private void clearNotes(int startingIndex)
    {
//        int start;
//            for (int i = 0; i < analysesCompleted; i++)
//            {
//                start = startingIndex + i * captureInterval;
//                melodyPart.delUnits(start, start + captureInterval);
//            }
            melodyPart.delUnits(startingIndex, tradeLength);
    }

    public int getCaptureInterval()
    {
        return this.captureInterval;
    }

    /**
     * Breaks input data into frames and determines pitch for each frame.
     *
     * @param streamInput the array of bytes delivered by the TargetDataLine.
     */
    private void parseNotes(byte[] streamInput)
    {
        //where the first note is to be inserted in the melody
        //currentIndex = analysesCompleted * captureInterval;
        int index;
//        if (analysesCompleted == 0)
//        {
//            //positionOffset = 0;
//            index = positionOffset + tenMSOffset;
//            //firstMeasure = false;
//        } else
        { //ignore first 10ms of input data; begin polling thereafter
            index = tenMSOffset;
        }
        //System.out.println("index = " + index + ", positionOffset = " + positionOffset);
        int size = FRAME_SIZE / 2;
        //convert tempo to ms per measure
        float tempo = (float) (score.getMetre()[0] * 60000.0 / score.getTempo());
        int slotSize = RESOLUTION; //smallest subdivision allowed
        if (allowTriplets)
        { //adjust # of subdivisions if triplets are allowed
            slotSize *= 3;
        }
        //System.out.println("Triplets = " + allowTriplets + ", Resolution = " + slotSize);
        boolean firstSlot = true;

        int currentSlotNumber;
        int duration = 0;
        while (index + FRAME_SIZE < streamInput.length)
        {
            byte[] oneFrame = new byte[FRAME_SIZE];
            //break input into frames
            int limit = index + FRAME_SIZE;
            for (int i = index; i < limit; i++)
            {
                oneFrame[i - index] = streamInput[i];
            }
            double fundamentalFrequency = 0;
            if (oneFrame.length > 0)
            {
                ByteBuffer bBuf = ByteBuffer.wrap(oneFrame);
                //new array to store double values
                double[] preCorrelatedData = new double[size];
                for (int i = 0; i < size; i++)
                { //populate array
                    preCorrelatedData[i] = (double) bBuf.getShort();
                }
                //only attempt to determine pitch if RMS is above threshold
                if (checkRMS(preCorrelatedData))
                {
                    double[] correlatedData = computeAutocorrelation(preCorrelatedData);
                    double[] computedData = computeNSD(preCorrelatedData, correlatedData);
                    fundamentalFrequency = pickPeak(computedData);
                }
            }
            //otherwise, keep fundamental at zero (rest)
            //keeps track of how much time has elapsed within a capture interval
            double timeElapsed = 0;
//            if (analysesCompleted == 0)
//            {
//                timeElapsed = ((index - positionOffset) / interval) * POLL_RATE;
//            } else
            {
                timeElapsed = (index / interval * POLL_RATE);
                //System.out.println("Time elapsed = " + timeElapsed);
            }
            //System.out.println("Time elapsed = " + timeElapsed);
            currentSlotNumber = resolveSlot(timeElapsed,
                    tempo / slotSize, slotSize) + 1;
            //System.out.println(timeElapsed + ": Slot = " + currentSlotNumber);
            //calculate equivalent MIDI pitch value for freq.
            int slotPitch = 0;
            if (fundamentalFrequency > 8.)
            {
                slotPitch =
                        jm.music.data.Note.freqToMidiPitch(fundamentalFrequency);
                if (slotPitch < minPitch || slotPitch > maxPitch)
                { //check to see if pitch is valid
                    slotPitch = 0;
                }
            }
            //if all windows for this slot have been examined, determine pitch
            //check to see if this window is part of the current slot
            if (currentSlotNumber != lastSlotNumber)
            {
                System.out.println("Slot = " + lastSlotNumber);
                int pitch = calculatePitch(oneSlot);
                //check to see whether or not pitch has changed from that
                //which fills the previous slot
                if (!firstSlot || analysesCompleted > 0)
                {
                    if (pitch != lastPitch || noteOff)
                    {
                        duration = slotsFilled * slotConversion;
                        setNote(lastPitch,
                                currentIndex,
                                duration,
                                melodyPart);
                        incrementCurrentIndex(duration);
                        slotsFilled = 1; //reset slotsFilled when pitch changes
                    } //if this pitch is the same as that of the last slot,
                    //continue building duration until pitch changes
                    else
                    { //if pitch hasn't changed, increment # of slots filled
                        slotsFilled++;
                        System.out.println("Duration for " + pitch + " extended.");
                    }
                } else
                {
                    firstSlot = false;
                }
                lastPitch = pitch;
                if (currentSlotNumber % slotSize == 0)
                {
                    lastSlotNumber = RESOLUTION;
                } else
                {
                    lastSlotNumber = currentSlotNumber;
                }
                oneSlot.clear(); //get rid of old list
                oneSlot.add(slotPitch);

            //special cases to handle the last slot in this capture interval
            } else if (index + FRAME_SIZE + interval >= streamInput.length)
            {
                oneSlot.add(slotPitch);

                //Determine whether the first slot in the next capture is part
                //of the slot for which windows are currently being collected
                System.out.println("time elapsed = " + timeElapsed);
                double nextTime = timeElapsed
                        + ((interval + tenMSOffset) / interval * POLL_RATE);
                int nextSlot = resolveSlot(nextTime, tempo / slotSize, slotSize) + 1;
                System.out.println("Next time = " + nextTime + ", next slot = "
                        + nextSlot + ", current slot = " + currentSlotNumber);
                if (analysesCompleted == 0 && currentSlotNumber == nextSlot
                        && tradeLength / captureInterval > 1) //this is irrelevant if there is only one capture in this trade
                {
                    System.out.println("Time difference of " + timeElapsed + " calculated.");
                    timeDifference = timeElapsed;
                    lastSlotNumber = currentSlotNumber;
                } else
                {
                    int pitch = calculatePitch(oneSlot);
                    //if the last pitch in the capture interval is a continuation
                    //of the previous pitch...
                    if (pitch == lastPitch)
                    {
                        if (tradeLength == score.getMetre()[0] * BEAT
                                || analysesCompleted < (tradeLength / captureInterval) - 1)
//                                && notate.getMode() == Mode.RECORDING
//                                && (currentIndex + duration) % captureInterval != 0)
                        {
                            slotsFilled++;
                            System.out.println("Note " + pitch + " *possibly* "
                                    + "carried over between capture intervals.\n"
                                    + "Mode = " + notate.getMode());
                        } else
                        {
                            System.out.println("Reached end.");
                            duration = slotsFilled * slotConversion;
                            setNote(lastPitch,
                                    currentIndex,
                                    duration + ((analysesCompleted * captureInterval
                                    - (currentIndex + duration)) % captureInterval),
                                    melodyPart);
                            incrementCurrentIndex(duration);
                        }
                    } else
                    { //otherwise, handle previous pitch and last pitch in slot
                        //separately
                        duration = slotsFilled * slotConversion;
                        setNote(lastPitch,
                                currentIndex,
                                duration,
                                melodyPart);
                        incrementCurrentIndex(duration);
                        //System.out.println("Trade length = " + tradeLength);

                        //if this is the last capture or stop btn has been
                        //pressed, go ahead and set the note.
                        if (analysesCompleted ==
                                (tradeLength / captureInterval) - 1)
//                                || notate.getMode() != Mode.RECORDING)
                        {
                            System.out.println("Reached end. Mode = " + notate.getMode());
                            duration = slotConversion;
                            setNote(pitch,
                                    currentIndex,
                                    duration + ((analysesCompleted * captureInterval
                                    - (currentIndex + duration)) % captureInterval),
                                    melodyPart);
                            incrementCurrentIndex(duration);
                            oneSlot.clear();
                        } else
                        { //otherwise, remember this pitch and reset slots filled.
                            lastPitch = pitch;
                            slotsFilled = 1;
                            System.out.println("Note " + pitch + " carried over "
                                    + "between capture intervals.");
                        }
                    }
                }
//                System.out.println("At end of capture, last slot = " + lastSlotNumber
//                        + ", current slot = " + currentSlotNumber);
//                lastSlotNumber = currentSlotNumber;

            //if the slot hasn't changed, count this window as part of the slot
            } else if (currentSlotNumber == lastSlotNumber)
            {
                oneSlot.add(slotPitch); //if so, continue collecting data
            }
            //increase the index by the designated interval
            index += (int) interval;
        } //end while
        analysesCompleted++;
        System.out.println("Finished analyzing capture " + analysesCompleted + ".");
    }

    private void incrementCurrentIndex(int duration)
    {
        currentIndex += duration;
    }

    private void setNote(int pitch,
                         int currentIndex,
                         int duration,
                         MelodyPart melodyPart)
    {
        if (pitch < 25) //count as a rest if pitch is out of range
        {
            imp.data.Note newRest = new Rest(duration);
            melodyPart.setNoteFromCapture(currentIndex, newRest);
            System.out.println("______________________________\n"
                    + "rest, duration = " + duration + " slots.\nposition = "
                    + currentIndex + "\n______________________________");
        } else
        {
            imp.data.Note newNote = new imp.data.Note(pitch, duration);
            melodyPart.setNoteFromCapture(currentIndex, newNote);
            System.out.println("______________________________\n"
                    + newNote.getPitchClassName()
                    + (newNote.getPitch() / 12 - 1) + "(" + newNote.getPitch() + ")"
                    + "\nduration = " + duration + " slots\nposition = "
                    + currentIndex + ".\n" + "______________________________");
        }
        System.out.println("Note off = " + noteOff);
    }

    /**
     * Determines the root mean square for the input array.
     *
     * @param data The array time-domain data (in double format).
     * @return A boolean value representing whether or not the RMS is above the
     * threshold.
     */
    private boolean checkRMS(double[] data)
    {
        //calculate root mean square of this window
        //to check for adequate sample data
        double sum = 0.0;
        for (int i = 0; i < data.length; i++)
        {
            sum += (data[i] * data[i]);
        }
        double rms = Math.sqrt(sum / data.length);
        //System.out.println("RMS = " + rms);
        return rms > RMS_THRESHOLD;
    }

    /**
     * Examines the pitches detected for the current slot and determines pitch
     *
     * @param pitches The array of pitches detected for this slot
     */
    private int calculatePitch(List<Integer> pitchList)
    {
        //System.out.println("New pitch calculated...");
        noteOff = false;
        int size = pitchList.size();
        int[] pitches = new int[size];
        for (int a = 0; a < size; a++)
        {
            pitches[a] = pitchList.get(a);
        }
        //Check for discrepancies in this slot
        int testPitch = 0;
        int numZeros = 0;
        boolean allZero = true;
        boolean tie = false;
        boolean d = false; //discrepancy test boolean
        int i = 0;
        while (!d && i < size)
        { //search for discrepancies in data
            if (pitches[i] == 0)
            {
                numZeros++;
            } else
            {
                if (pitches[i] != testPitch && testPitch != 0)
                {
                    d = true; //more than one nonzero pitch has been found
                } else
                {
                    testPitch = pitches[i];
                }
                allZero = false;
            }
            i++;
        } //end while
        int[] occurrences = new int[size];
        int maxLoc = 0;
        int maxO = 0;
        int secondPlaceLoc = -1;
        int secondPlaceO = -1;
        if (!d || allZero) //if there are no discrepancies, return the pitch
        {
            return testPitch;
        } else
        { //otherwise, find most frequently detected pitch
            testPitch = 0;
            for (i = 0; i < size; i++)
            {
                if (pitches[i] != 0 && pitches[i] != testPitch)
                {
                    occurrences[i] = 1;
                    testPitch = pitches[i]; //don't check same pitch twice...
                    for (int j = i + 1; j < size; j++)
                    {
                        if (pitches[i] == pitches[j])
                        {
                            occurrences[i] += 1;
                        }
                    } //end for (j)
                } //end if
            } //end for (i)
            for (i = 0; i < occurrences.length; i++)
            {
                if (occurrences[i] > maxO)
                {
                    if (maxO > 0)
                    {
                        secondPlaceLoc = maxLoc;
                        secondPlaceO = occurrences[secondPlaceLoc];
                    }
                    maxO = occurrences[i];
                    maxLoc = i;
                } //end if
                else if (occurrences[i] != 0 && pitches[i] != pitches[maxLoc]
                        && occurrences[i] == maxO)
                {
                    tie = true;
                    secondPlaceLoc = i;
                    secondPlaceO = occurrences[i];
                }
            } //end for
        } //end else
        //check to see whether or not this tone is terminal
        if ((size > 2 && pitches[size - 2] == 0
                && pitches[size - 1] == 0)
                || (size < 3 && pitches[size - 1] == 0))
        {
            noteOff = true;
        }
        //if the pitch has at least 5 windows and no pitch
        if ((size > 4) && ((maxO < 3 && numZeros >= size
                * 0.75) || (maxO < 3 && pitches[maxLoc] < 40)))
        {
            return 0;
        } else if (secondPlaceO > 0)
        {
            int absDifference = Math.abs(pitches[maxLoc] - pitches[secondPlaceLoc]);
            if (!tie && maxO - secondPlaceO > 1 && absDifference < 11)
            {
                return pitches[maxLoc];
            } //check for false readings in lower octaves
            else if (!tie && absDifference > 11)
            {
                return Math.max(pitches[maxLoc], pitches[secondPlaceLoc]);
            } //end if
            else if (tie)
            {
                //algorithm is more prone to sub-fundamental errors
                if (secondPlaceLoc >= size / 2 && absDifference < 12)
                {
                    return pitches[secondPlaceLoc];
                } else
                {
                    return Math.max(pitches[maxLoc], pitches[secondPlaceLoc]);
                }
            }
        }
        return pitches[maxLoc];
    }

    /**
     * Determines which slot the current window falls into.
     *
     * @param timeElapsed The amount of time in milliseconds that has elapsed
     * since sampling for this audio capture interval began.
     * @param msPerSlot The number of milliseconds in each slot based on the
     * current minimum slot size (resolution).
     * @param slotSize The number of slots in each measure (also resolution).
     *
     * @return The slot in which this window falls.
     */
    private int resolveSlot(double timeElapsed,
                            double msPerSlot, int slotSize)
    {
        int slot = (int) (Math.floor((timeElapsed + timeDifference)
                / msPerSlot)) % slotSize;

        //I thought about accounting for swing. This hasn't been tested.
        //        if(swingVal > 0.5 && RESOLUTION >= 8)
        //        {
        //            if(slot % (2 ^ (RESOLUTION / 8)) == (2 ^ (RESOLUTION / 8 - 1)) + 1) {
        //                if(timeElapsed / msPerSlot < (slot + 1) * swingVal){
        //                    return slot - 1;
        //                }
        //            }
        //        }

        //if triplets are allowed, we must ignore invalid slots (lump them
        //in with the preceding slot).
        if (allowTriplets)
        {
            int tripCheck = RESOLUTION / 2;
            if (slot % tripCheck == tripCheck - 1 || slot % tripCheck == 1)
            {
                return slot - 1;
            }
        }
        return slot;
    }

    /**
     * Computes the autocorrelation function for the given input as a function
     * of lag (tau).
     *
     * @param input The array of audio samples.
     * @return An array representing the autocorrelation function of the input
     * data.
     */
    private double[] computeAutocorrelation(double[] input)
    {
        int size = input.length;
        double correlated[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size; tau++)
        {
            double sum = 0;
            for (int j = 0; j < size - tau; j++)
            {
                sum += input[j] * input[j + tau];
            }
            correlated[tau] = sum;
        }
        return correlated;
    }

    /**
     * Computes normalized square difference function for the given input.
     *
     * @param original The array of original time-domain data.
     * @param correlated The array holding the autocorrelation function for the
     * input data. Used to make calculating the NSDF more efficient.
     * @return The NSDF for the input data.
     */
    private double[] computeNSD(double[] original,
                                double[] correlated)
    {
        int size = original.length;
        double squared[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size; tau++)
        {
            double sum = 0;
            for (int j = 0; j < size - tau; j++)
            {
                sum += Math.pow(original[j], 2.0)
                        + Math.pow(original[j + tau], 2.0);
            }
            squared[tau] = sum;
        }
        //Normalization: n'(tau) = 2 * r'(tau) / m'(tau).
        //Range should be [-1, 1].
        for (int i = 0; i < size; i++)
        {
            original[i] = (2.0 * correlated[i]) / squared[i];
        }
        return original;
    }

    /**
     * Attempts to identify the fundamental frequency of the normalized data.
     * Implements the peak picking described in "A Smarter Way to Find Pitch,"
     * by Philip McLeod and Geoff Wyvill:
     * http://miracle.otago.ac.nz/tartini/papers/A_Smarter_Way_to_Find_Pitch.pdf
     *
     * @param input The array of normalized data derived from original
     * time-domain sample data.
     * @return The fundamental frequency determined for the input data.
     */
    private double pickPeak(double[] input)
    {
        boolean negativeZeroCrossing = false;   //<<--checks for negatively
        //double[] localMaxima = new double[1000];        //sloped zero crossings
        List<Double> localMaxima = new ArrayList<Double>();
        //indices holds locations of positively sloped zero crossings
        //int[] indices = new int[localMaxima.size()];
        List<Integer> indices = new ArrayList<Integer>();
        int numberOfMaxima = 0; //# of local maxima discovered thus far
        //look for positively sloped zero crossings in input data
        for (int i = 1; i < input.length; i++)
        {
            //if a pos. sloped zero crossing is found, mark its location
            if (input[i] > 0 && input[i - 1] <= 0)
            {
                indices.add(i);
                numberOfMaxima++;
                negativeZeroCrossing = !negativeZeroCrossing;
            } else if (input[i] <= 0 && input[i - 1] > 0)
            {
                negativeZeroCrossing = !negativeZeroCrossing;
            }
        }
        //look for local maxima between indices
        int index = 0;
        double localMax;
        int localMaxIndex;
        while (index < numberOfMaxima - 1 && indices.get(index) != null)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices.get(index); i < indices.get(index + 1); i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima.add(index, localMax);
            indices.remove(index);
            indices.add(index, localMaxIndex);
            index++;
        } //end while
        //if the last local max was followed by a negatively sloped
        //zero crossing, add it to the array.
        if (negativeZeroCrossing && index > 1)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices.get(index); i < input.length; i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima.add(index, localMax);
            indices.remove(index);
            indices.add(index, localMaxIndex);
            index++;
        } //end while
        else
        {
            numberOfMaxima--;  //otherwise, decrement # of maxima
        }
        if (numberOfMaxima > 0)
        {
            //find highest local maximum
            double highestMax = localMaxima.get(0);
            int j = 1;
            int size = localMaxima.size();
            while (j < size)
            {
                double toCheck = localMaxima.get(j);
                if (toCheck > highestMax)
                {
                    highestMax = toCheck;
                    CONFIDENCE = highestMax;
                }
                j++;
            }
            //System.out.println("Confidence = " + CONFIDENCE);
            if (highestMax < CONFIDENCE_THRESHOLD)
            { //clarity/confidence check
                return 0; //ignore frequency if confidence is below above value
            }
            double threshold = highestMax * K_CONSTANT;
            j = 0;
            double testPitch = localMaxima.get(0);
            while (testPitch < threshold)
            { //find first local maximum
                j++;                        //above threshold
                testPitch = localMaxima.get(j);
            }
            //perform cubic interpolation to refine index of pitch period
            double refinedIndex = SAMPLE_RATE;
            if (0 < indices.get(j) - 1 && indices.get(j) + 2 < input.length)
            {
                int currentMaxLocation = indices.get(j);
                double refinedMax = refineMax(input[currentMaxLocation - 1],
                        input[currentMaxLocation], input[currentMaxLocation + 1],
                        input[currentMaxLocation + 2]);
                if (refinedMax > 0)
                {
                    double newMaxLocation = (currentMaxLocation - 1)
                            + refinedMax;
                    refinedIndex = newMaxLocation;
                } else
                {
                    refinedIndex = currentMaxLocation;
                }
            }
            return SAMPLE_RATE / refinedIndex;
        } else
        {
            System.out.println("ALERT!!! No maxima found in correlated data.");
            return 0;
        }
    }

    /**
     * Uses cubic interpolation to refine the location of the lag value that
     * corresponds to the fundamental frequency. Adapted from code originally
     * written by Dominic Mazzoni.
     *
     * @param y0 The index of the sample taken just before the one that
     * corresponds to the fundamental frequency.
     * @param y1 The index of the sample that corresponds to the fundamental
     * frequency.
     * @param y2 The index of the sample taken just after the one that
     * corresponds to the fundamental frequency.
     * @param y3 The index of the second sample taken after the one that
     * corresponds to the fundamental frequency.
     * @return A refined value for the index of the sample that corresponds to
     * the fundamental frequency.
     */
    private double refineMax(double y0, double y1, double y2, double y3)
    {
        // Find coefficients of cubic
        double a = (y0 / -6.0 + y1 / 2.0 - y2 / 2.0 + y3 / 6.0);
        double b = (y0 - 5.0 * y1 / 2.0 + 2.0 * y2 - y3 / 2.0);
        double c = (-11.0 * y0 / 6.0 + 3.0 * y1 - 3.0 * y2 / 2.0 + y3 / 3.0);
        double d = y0;

        // Take derivative
        double da = 3 * a;
        double db = 2 * b;
        double dc = c;

        // Find zeroes of derivative using quadratic equation
        double discriminant = db * db - 4 * da * dc;
        if (discriminant < 0.0)
        {
            return -1; // error
        }
        double x1 = (-db + Math.sqrt(discriminant)) / (2 * da);
        double x2 = (-db - Math.sqrt(discriminant)) / (2 * da);

        // The one which corresponds to a local _maximum_ in the
        // cubic is the one we want - the one with a negative
        // second derivative
        double dda = 2 * da;
        double ddb = db;

        if (dda * x1 + ddb < 0)
        {
            return x1;
        } else
        {
            return x2;
        }
    }

    private AudioFormat getAudioFormat()
    {
        float sampleRate = (float) SAMPLE_RATE;
        int sampleSizeInBits = SAMPLE_SIZE;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    public void stopCapture()
    {
        stopCapture = true;
        closeTargetLine();
    }

    public boolean isCapturing()
    {
        return isCapturing;
    }

    public class CaptureThread extends Thread
    {

        public void run()
        {
            //number of bytes to capture before putting data in the queue
            int bytesToCapture = (int) (((SAMPLE_RATE * 2.) / (score.getTempo()
                    / score.getMetre()[0] / 60.)) * (captureInterval /
                    (score.getMetre()[0] * BEAT)));
            System.out.println(bytesToCapture + " bytes will be captured.");
            byte tempBuffer[] = new byte[target.getBufferSize() / 5];
            int limit = bytesToCapture / tempBuffer.length;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytesToCapture);
            int bytesRead;
            int captureLimit = tradeLength / captureInterval;
            //when trading, this is the amount of time to sleep while generating
            int sleepTimeMillis = (int) (tradeLength
                        * (60000. / (score.getTempo() * BEAT)));

            try
            {//Loop until stopCapture is set or trade length is reached.
                while (!stopCapture && analysesCompleted + processingQueue.size()
                       < captureLimit)
                {
                    //wait for notification from Notate/Timer
                    synchronized (thisCapture)
                    {
                        thisCapture.wait();
                    }
                    isCapturing = true;

//                    if (!processingStarted)
//                    {
//                        //the time at which audio capture begins
//                        startTime = System.nanoTime();
//                        System.out.println("Audio capture initialized at time "
//                                + startTime);
//                    } else
//                    {
//                        System.out.println("Next capture started at time "
//                                + System.nanoTime());
//                    }
                    //start the TargetDataLine, from which audio data is read
                    target.start();

                    long countInMicroseconds = score.getCountInTime() * 1000000;
//
//                    long delay = System.nanoTime();
//                    long usPos = midiSynth.getMicrosecond();
//                    while(!(usPos > 0)) {
//                        Thread.sleep(1);
//                        usPos = midiSynth.getMicrosecond();
//                    }
//                    delay = System.nanoTime() - delay;
//                    System.out.println("Delay = " + delay + " nanoseconds.");

                    //wait for sequencer to start
                    Sequencer sequencer = midiSynth.getSequencer();
                    while(sequencer == null) {
                        Thread.sleep(1);
                        sequencer = midiSynth.getSequencer();
                    }
                    long usPos = sequencer.getMicrosecondPosition();

                    if (!processingStarted)
                    {
                        delay = System.nanoTime();
                        while (!(usPos - countInMicroseconds > 0))
                        {
                            try
                            {
                                Thread.sleep(0, 500);
                            } catch (Exception e)
                            {
                                System.out.println("Sleep error:\n" + e);
                            }
                            //usPos = sequencer.getMicrosecondPosition();
                              usPos = midiSynth.getMicrosecond();
//                            System.out.println("CaptureThread waiting... "
//                                    + "Sequencer time = " + (usPos
//                                    - countInMicroseconds) + " microseconds.");
                        }
                        //Round delay time to nearest millisecond.
                        delay = (System.nanoTime() - delay) / 1000000;
                        System.out.println("Capture latency = " + delay + " ms.");
                    } else
                    {
                        try
                        {
                            CaptureThread.sleep(delay);
                        } catch (Exception e)
                        {
                            System.out.println("Sleep error:\n" + e);
                        }
                    }
                    //System.out.println("Capture ACTUALLY started at time " + System.nanoTime());
                    System.out.println("Capture started at time " + System.currentTimeMillis() + " ms.");
                    //collect 1 captureInterval's worth of data
                    for (int n = 0; n < limit; n++)
                    {
                        bytesRead = target.read(tempBuffer, 0, tempBuffer.length);
                        if (bytesRead > 0)
                        {   //Append data to output stream.
                            outputStream.write(tempBuffer, 0, bytesRead);
                        } else
                        {
                            System.out.println("No data read at frame " + n);
                        }
//                        if (n >= limit - 2)
//                        {
//                            System.out.println("Finished capturing frame " + n
//                                    + " at time " + System.nanoTime());
//                        }
                    }
                    //System.out.println("Audio capture interval finished.");
//                    if (!processingStarted)
//                    {
////                        startTime = System.nanoTime();
////                        startTime -= (long) ((1000000000. * bytesToCapture)
////                                / (SAMPLE_RATE * 2.));
////                        System.out.println("First capture start = "
////                                + startTime);
//                        long difference = (midiSynth.getPlaybackStartTime()
//                                + (long) score.getCountInTime() * 1000000000
//                                - startTime);
//                        System.out.println("difference = " + difference);
//                        if (difference < 0)
//                        {
//                            difference = 0;
//                        }
//                        positionOffset = (int) ((difference / 1000000000.)
//                                * SAMPLE_RATE * 2.);
//                        //positionOffset *= OFFSET_ADJUSTMENT;
//                        if (positionOffset % 2 != 0)
//                        {
//                            positionOffset += 1;
//                        }
//                        System.out.println("positionOffset = " + positionOffset);
//                    }
                    if (outputStream.size() > 0)
                    {
                        byte[] capturedAudioData = outputStream.toByteArray();
                        processingQueue.add(capturedAudioData);
                        System.out.println("Array containing "
                                + capturedAudioData.length + " elements added "
                                + "to processing queue. " + "Queue now contains "
                                + processingQueue.size() + " element(s).");
                        synchronized (processingQueue)
                        {
                            try
                            {
                                processingQueue.notify();
                            } catch (Exception e)
                            {
                                System.out.println("Processing queue error:\n" + e);
                            }
                        }
                        outputStream.reset();
                        isCapturing = false;
                    } else
                    {
                        //CaptureThread.sleep(0, 10);
                    }
                }//end while

                System.out.println("Capture stopped.");
                if(trading)
                {
                    //analysesCompleted = 0;
                    //notate.stopAudioCapture();
                    stopCapture();
                    closeTargetLine();
//                    System.out.println("Trade length = " + tradeLength
//                            + "\nCaptureThread will now sleep for "
//                            + sleepTimeMillis + " ms.");
                    CaptureThread.sleep(sleepTimeMillis - 100);
                    startingPosition += tradeLength * 2;
                    currentIndex = startingPosition;
                    clearNotes(startingPosition);
                    System.out.println("CaptureThread expired...");
                    captureAudio();
                }
            } catch (Exception e)
            {
                System.out.println("Capture thread error:\n" + e);
            }//end catch
        }//end run
    }//end inner class CaptureThread

    /**
     * Analyzes data from the processing queue.
     */
    public class AnalyzeThread extends Thread
    {

        public void run()
        {
            while (!stopAnalysis)
            {
                synchronized (processingQueue)
                {
                    try
                    {
                        processingQueue.wait();
                    } catch (Exception e)
                    {
                        System.out.println("wait error:\n" + e);
                    }
                }

                if (!processingQueue.isEmpty())
                {
                    byte result[] = processingQueue.poll();
                    System.out.println("Array removed from processing queue."
                            + "\n____________New Capture_____________");
                    processingStarted = true;
                    if (result.length > 0)
                    {
                        parseNotes(result);
                    }
                } else
                {
                    System.out.println("Queue is empty.");
                }
//                if(stopCapture && processingQueue.isEmpty())
//                {
//                    stopAnalysis = true;
//                    System.out.println("AnalyzeThread will now die.");
//                }
            }
            System.out.println("Analysis stopped.");
        }//end run
    }//end inner class AnalyzeThread


//    public class MixerGetterThread extends Thread
//    {
//
//        public void run()
//        {
//            while (mainMixer == null)
//            {
//                for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo())
//                {
//                    Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
//
//                    if (thisMixer.getTargetLines().length > 0)
//                    {
//                        mainMixer = thisMixer;
//                        System.out.println("Mixer: " + thisMixerInfo.getDescription()
//                                + " [" + thisMixerInfo.getName() + "]");
//                    } else
//                    {
//                        try
//                        {
//                            MixerGetterThread.sleep(0, 500);
//                        } catch (Exception e)
//                        {
//                            //Do it!
//                        }
//                    }
//                }
//            }
//        }
//    }//end MixerGetterThread

}