/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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

package imp.lickgen;

import imp.Constants;
import imp.ImproVisor;
import imp.cluster.*;
import imp.data.*;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.NonExistentParameterException;
import static imp.lickgen.Terminals.isScaleDegree;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import polya.Polylist;
import polya.PolylistEnum;

/**
 @author David Morrison
 * Includes additions by Ryan Wieghard to support Outside playing.
 * Includes additions by Jack Davison for triadic grammar elements, 
 * and by David Halpern for expectancy, although the latter produce errors
 * at the moment.
 * Includes additions by Hayden Blauzvern for neural network critic.
 */

public class LickGen implements Constants 
{
    // Some notes are initialized to these pitch values; use really big numbers to
    // stay out of the way of midi numbers.

    public static final int NOTE       = 1000;
    public static final int CHORD      = 1001;
    public static final int SCALE      = 1002;
    public static final int COLOR      = 1003;
    public static final int APPROACH   = 1004;
    public static final int RANDOM     = 1005;
    public static final int BASS       = 1006;
    public static final int GOAL       = 1007;
    public static final int OUTSIDE    = 1008;
    public static final int EXPECTANCY = 1009;
    
    // Parameter strings
    // Strings used as labels in the grammar file
    
    public static final String MIN_PITCH_STRING = "min-pitch";
    public static final String MIN_PITCH_DEFAULT = "54";
    public static final String MAX_PITCH_STRING = "max-pitch";
    public static final String MAX_PITCH_DEFAULT = "84";
    public static final String MIN_INTERVAL = "min-interval";
    public static final String MIN_INTERVAL_DEFAULT = "0";
    public static final String MAX_INTERVAL = "max-interval";
    public static final String MAX_INTERVAL_DEFAULT = "9";
    public static final String MIN_DURATION = "min-duration";
    public static final String MIN_DURATION_DEFAULT = "8";
    public static final String MAX_DURATION = "max-duration";
    public static final String MAX_DURATION_DEFAULT = "2";
    public static final String REST_PROB = "rest-prob";
    public static final String REST_PROB_DEFAULT = "0.1";
    public static final String LEAP_PROB = "leap-prob";
    public static final String LEAP_PROB_DEFAULT = "0.01";
    public static final String CHORD_TONE_WEIGHT = "chord-tone-weight";
    public static final String CHORD_TONE_WEIGHT_DEFAULT = "0.7";
    public static final String COLOR_TONE_WEIGHT = "color-tone-weight";
    public static final String COLOR_TONE_WEIGHT_DEFAULT = "0.2";
    public static final String SCALE_TONE_WEIGHT = "scale-tone-weight";
    public static final String SCALE_TONE_WEIGHT_DEFAULT = "0.1";
    public static final String CHORD_TONE_DECAY = "chord-tone-decay";
    public static final String SCALE_TYPE = "scale-type";
    public static final String SCALE_TYPE_DEFAULT = "Use First Scale";
    public static final String CHORD_TONE_DECAY_DEFAULT = "0.0";
    public static final String SCALE_ROOT = "scale-root";
    public static final String SCALE_ROOT_DEFAULT = "C";
    public static final String USE_GRAMMAR = "use-grammar";
    public static final String AVOID_REPEATS = "avoid-repeats";
    public static final String AVOID_REPEATS_DEFAULT = "true";
    public static final String AUTO_FILL = "auto-fill";
    public static final String AUTO_FILL_DEFAULT = "true";
    public static final String RECTIFY = "rectify";
    public static final String RECTIFY_DEFAULT = "true";
    public static final String USE_SYNCOPATION       = "use-syncopation";
    public static final String SYNCOPATION_TYPE      = "syncopation-type";
    public static final String SYNCOPATION_MULTIPLIER = "syncopation-multiplier";
    public static final String SYNCOPATION_CONSTANT     = "syncopation-constant"; 
    public static final String EXPECTANCY_MULTIPLIER = "expectancy-multiplier";
    public static final String EXPECTANCY_CONSTANT   = "expectancy-constant";
    
    // Syncopation and Expectancy Parameters
private static final boolean defaultUseSyncopation      = false;
private static final String defaultSyncopationType      = "C";
private static final double defaultSyncopationMultiplier    = 0;
private static final double defaultSyncopationConstant    = 0.2;
private static final double defaultExpectancyMultiplier = 0;
private static final double defaultExpectancyConstant   = 0.7;

private boolean useSyncopation      = defaultUseSyncopation;
private String syncopationType      = defaultSyncopationType;
private double syncopationConstant     = defaultSyncopationConstant;
private double syncopationMultiplier     = defaultSyncopationMultiplier;
private double expectancyMultiplier = defaultExpectancyMultiplier;
private double expectancyConstant   = defaultExpectancyConstant;

public Integer integer;

    private int lengthOfTrade;
    private static int SLOTS_PER_MEASURE = 480;
    private static int QUARTER_NOTE = 120;
    private static int BASE_WEIGHT = 15;
    private static int QUARTER_WEIGHT = 5;
    //used in chooseNote - should be able to be varied
    public static final double REPEAT_PROB = 1.0 / 512.0;
    public static final int PERCENT_REPEATED_NOTES_TO_REMOVE = 98;
    public static final int MIN_JUMP_UPPER_BOUND = 6;
    //probability to make a random note a goal note
    public static final int GOAL_PROB = 0;
    //probabilities for goal notes
    public static final int THIRD_SEVENTH = 20;
    public static final int ROOT_FIFTH = 15;
    public static final int NINTH_THIRTHEENTH = 7;
    public static final int SHARP11_11 = 5;
    public static final int FLAT9_SHARP9_FLAT13 = 3;
    public int[] typeMap = {CHORD, COLOR, RANDOM, SCALE};
    public static final String NOTE_SYMBOL = "N";
    public static final String REST_SYMBOL = "R";
    private static final int MAX_EXPECTANCY = 216;
    private static final int MAX_SYNCO = 12;
    public static int MELODY_GEN_LIMIT = 15;
    private int NOTE_GEN_LIMIT = 100;    
    public ArrayList<double[]> probs;  // Array of note probabilities
    private Grammar grammar;
    private double[] pitchUsed = new double[TOTALPITCHES];
    private Polylist preferredScale = Polylist.nil;
    ArrayList<String> chordUsed = new ArrayList<String>();
    // Indices that are global to an instance
    ArrayList<Integer> chordUsedSection = new ArrayList<Integer>();    
    int position = 0;
    int oldPitch = 0;
    int oldOldPitch = 0;
    double expectancy = -1;
    private static int DEFAULT_EXPECTANCY_LIMIT = 30;
    private static int MINIMAX_EXPECTANCY = 128;
    boolean useOutlines = false;
    boolean soloistLoaded = false;
    
    //soloist file data
    private Vector<DataPoint> dataPoints;
    private Cluster[] clusters;
    private Vector<ClusterSet> clusterSets;
    private Vector<NGramWithTransitions> transitions;
    private Vector<NGramWithTransitions> reverseTransitions;
    private Vector<Vector<ClusterSet>> outlines;
    
    private int slotsPerMeasure;
    
    //head file data
    private ArrayList<Score> headData = new ArrayList<Score> ();
    
    //fillmelody parameters from notate
    private int mMinPitch = 60;
    private int mMaxPitch = 82;
    private int mMinInterval;
    private int mMaxInterval;
    private int mBeatValue;
    private double mLeapProb;
    private ChordPart mChordProg;
    private int mStart;
    private boolean mAvoidRepeats;
    
    String rhythmGeneratedFromOutline = null; 
    
    //passed to NoteChooser object. If this is true, don't transpose by octave
    //to stay within pitch bounds
    private boolean doNotSwitchOctave = false;
    
    //used in in building a solo from outline
    private boolean lastWasTied = false;

    private Notate notate;
    private int prevPitch = (int)(Math.random() * 20 + 60);
    private int prevPrevPitch = 0;
    private int prevQuarter = prevPitch;
    private int prevPrevQuarter = prevPrevPitch;
    
    /**
     * Constructor -- loads the grammar in from the specified filename, and
     * sets all note probabilities to 1.
     */
    public LickGen(String grammarFile, Notate notate) {
    //System.out.println("Lickgen constructor grammarFile = " + grammarFile);
        this.notate = notate;
        grammar = new Grammar(grammarFile);
        loadGrammar(grammarFile);
        probs = new ArrayList<double[]>();
        
        chordUsed.clear();
        chordUsedSection.clear();
        
        loadHeadData(ImproVisor.getGrammarDirectory().getAbsolutePath() 
                + File.separator + "HeadData.data");
        
        String soloistFileName = grammarFile.replace(".grammar", ".soloist"); 
        File soloistFile = new File(soloistFileName);
        if(soloistFile.exists()) {
            soloistLoaded = true;
            loadSoloist(soloistFileName);
        notate.setLickGenStatus("Loaded soloist file: " + soloistFileName);
        }
        else
        {
            soloistLoaded = false;
         //System.out.println("LickGen constructor, no soloist file = " + soloistFileName);
       }
        if(notate.getAutoImprovisation())
        {
            lengthOfTrade = notate.getTradeLength();
        }
    }

    public Polylist getRhythmFromSoloist() {
        return Polylist.PolylistFromString(rhythmGeneratedFromOutline);
    }

    //load the Score objects of the transcriptions with heads into memory
    public void loadHeadData(String file) {
    //System.out.println("loadHeadData" + file);
        FileInputStream fis;
        ObjectInputStream in;
        
        try {
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
            Object o = in.readObject();
            while (true) {
                if (o instanceof Score) {
                    //System.out.println(((Score)o).getTitle());
                    headData.add((Score) o);
                    o = in.readObject();
                }
                else break;
            }
            
            in.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    //load soloist file
    public void loadSoloist(String soloistFile) {
        FileInputStream fis;
        ObjectInputStream in;

        try {
            fis = new FileInputStream(soloistFile);
            in = new ObjectInputStream(fis);
            dataPoints =  (Vector<DataPoint>) in.readObject();
            clusters = (Cluster[]) in.readObject();
            clusterSets = (Vector<ClusterSet>) in.readObject();
            transitions = (Vector<NGramWithTransitions>) in.readObject();
            reverseTransitions = (Vector<NGramWithTransitions>) in.readObject();
            outlines = (Vector<Vector<ClusterSet>>) in.readObject();
            in.close();
        } catch (IOException ex) {
           notate.setLickGenStatus("There was an IO exception reading the soloist file: " + soloistFile + " " + ex);
           soloistLoaded = false;
        } catch (ClassNotFoundException ex) {
           notate.setLickGenStatus("There was a class-not-found exception reading the soloist file: " + soloistFile + " " + ex);
           soloistLoaded = false;
        } catch (Exception ex) {
           notate.setLickGenStatus("There was an exception reading the soloist file: " + soloistFile + " " + ex);
           soloistLoaded = false;
        }
    }
    
    
/**
 * adds all notes in curr to the end of melpart
 */
    
private void addMelody(MelodyPart melpart,
                       MelodyPart curr,
                       boolean startTied,
                       boolean endTied)
  {
    //System.out.println("Melody: " + curr + "length: " + curr.getSize());

    if( curr.getSize() > slotsPerMeasure )
      {
        //System.out.println("MEASURE TOO BIG");
        curr = curr.extract(0, slotsPerMeasure);
      }

    if( curr.getSize() < slotsPerMeasure )
      {
        //System.out.println("MEASURE TOO SMALL");
        int s = curr.getSize();
        Rest r = new Rest(slotsPerMeasure - s);
        curr.addRest(r);
      }

    ArrayList<Unit> units = curr.getUnitList();
    //if prev measure and current measure were both tied originally, tie them together

    if( startTied && lastWasTied )
      {
        //System.out.println("Should be tying.");
        //System.out.println(position/480);
        //System.out.println("Pitch 1: " + ((Note)units.get(0)).getPitch() +
        //        "Pitch 2: " + melpart.getLastNote().getPitch());
        if( ((Note) units.get(0)).getPitch() != -1 && melpart.getLastNote().getPitch() != -1 )
          {
            //System.out.println("Tying");
            //System.out.println(position / 480);
            //System.out.println("Pitch 1: " + ((Note) units.get(0)).getPitch()
            //        + "Pitch 2: " + melpart.getLastNote().getPitch());
            Note last = melpart.getLastNote();
            //System.out.println("First time: " + last.getRhythmValue());
            last.setRhythmValue(last.getRhythmValue() + ((Note) units.get(0)).getRhythmValue());
            melpart.setSize(melpart.getSize() + ((Note) units.get(0)).getRhythmValue());
            last = melpart.getLastNote();
            //System.out.println("Second time: " + last.getRhythmValue());
            units.remove(0);
          }
      }

    for( int i = 0; i < units.size(); i++ )
      {
        melpart.addNote((Note) units.get(i));
      }
    //if the point we just added is tied at the end, set the flag
    lastWasTied = endTied;
  }
    
    //return the NGramWithTransitions object for the desired cluster
    private NGramWithTransitions getTransitionObject(int clusterNumber) {
        NGramWithTransitions t = null;
        for (int j = 0; j < transitions.size(); j++) {
            //find the transition object for the first cluster
            if (transitions.get(j).getState() == clusterNumber) {
                t = transitions.get(j);
                break;
            }
        }
        return t;
    }
    
    private NGramWithTransitions getReverseTransitionObject(int clusterNumber) {
        NGramWithTransitions t = null;
        for (int j = 0; j < reverseTransitions.size(); j++) {
            //find the transition object for the first cluster
            if (reverseTransitions.get(j).getState() == clusterNumber) {
                t = reverseTransitions.get(j);
                break;
            }
        }
        return t;
    }
    
    //Takes a ArrayList of possible outlines and extends an outline
    //by 1 measure at the beginning or end if it is 1 measure too short
    private void completeOutlines(Vector<Vector<ClusterSet>> possibleOutlines, int numMeasures) {
        
        for(int i = 0; i < possibleOutlines.size(); i++) {
            Vector<ClusterSet> outline = possibleOutlines.get(i);
            if(outline.size() == numMeasures - 1) {
                if(bernoulli(.5)) {
                    //add one measure at the beginning
                    int firstMeasure = outline.get(0).getOriginal().getNumber();
                    NGramWithTransitions first = getReverseTransitionObject(firstMeasure);
                    int newBeginning = first.getNextState();
                    ClusterSet s = null;
                    for(int j = 0; j < clusterSets.size(); j++) {
                        //find the clusterSet for this cluster
                        if(clusterSets.get(j).getOriginal().getNumber() == newBeginning) {
                            s = clusterSets.get(j);
                            break;
                        }
                    }
                    //add the new clusterSet at the beginning of the outline
                    outline.insertElementAt(s, 0);
                }
                else {
                    //add one measure at the end
                    int lastMeasure = outline.lastElement().getOriginal().getNumber();
                    NGramWithTransitions last = getTransitionObject(lastMeasure);
                    int newEnding = last.getNextState();
                    ClusterSet s = null;
                    for(int j = 0; j < clusterSets.size(); j++) {
                        //find the clusterSet for this cluster
                        if(clusterSets.get(j).getOriginal().getNumber() == newEnding) {
                            s = clusterSets.get(j);
                            break;
                        }
                    }
                    //add the new clusterSet at the end of the outline
                    outline.add(s);
                }
                //outlines.remove(i);
                //outlines.insertElementAt(outline, i);
            }
        }
        //return outlines;
    }
    
    public void getFillMelodyParameters(int minPitch, int maxPitch, int minInterval,
            int maxInterval, int beatValue, double leapProb, ChordPart chordProg,
            int start, boolean avoidRepeats) {
        
            mMinPitch = minPitch;
            mMaxPitch = maxPitch;
            mMinInterval = minInterval;
            mMaxInterval = maxInterval;
            mBeatValue = beatValue;
            mLeapProb = leapProb;
            mChordProg = chordProg;
            mStart = start;
            mAvoidRepeats = avoidRepeats;
    }
    
    public MelodyPart generateSoloFromOutline(int totalSlots) {
        
        if(!soloistLoaded) {
            notate.setLickGenStatus("No soloist file is available.");
            return null;
        }

        try
        {

        useOutlines = true;
        doNotSwitchOctave = true;
        
        int segLength = dataPoints.get(0).getSegLength();
        slotsPerMeasure = segLength * 120;
        int numMeasures = totalSlots / slotsPerMeasure;
        int numLeftOverSlots = totalSlots - (numMeasures * slotsPerMeasure);
        
        
        Vector<Vector<ClusterSet>> possibleOutlines = new Vector<Vector<ClusterSet>> ();
        for(int i = 0; i < outlines.size(); i++) {
            Vector<ClusterSet> outline = outlines.get(i);
            //System.out.println("Considering outline of size " + outline.size());

            if(outline.size() == numMeasures )//|| outline.size() == numMeasures - 1)
            {
                possibleOutlines.add(outline);
            }
        }

        notate.setLickGenStatus("Generating melody from outline ("
                              + possibleOutlines.size()
                              + " outlines of size " + numMeasures
                              + " available in the soloist file)."
                              + " This takes a little longer.");

        completeOutlines(possibleOutlines, numMeasures);


        if(possibleOutlines.size() == 0) {
            useOutlines = false;
            doNotSwitchOctave = false;
            oldPitch = 0;
            return null;
        }
        
        //choose a random outline from the possibilities
        Random rand = new Random();
        int outlineIndex = rand.nextInt(possibleOutlines.size());
        Vector<ClusterSet> outline = possibleOutlines.get(outlineIndex);
        
        MelodyPart solo = buildSolo(outline);
        mergeTies(solo);
        return solo;
        }
        catch( Exception ex )
        {
            notate.setLickGenStatus("Failure in constructing solo from outline due to exception: " + ex);
            return null;
        }
    }
    
    private void mergeTies(MelodyPart p) {
        //System.out.println("Calling mergeties.");
        int segLength = dataPoints.get(0).getSegLength() * BEAT;
        int tracker = p.getNextIndex(0);
        int lastIndex = 0;
        Note lastNote = new Note(0);
        while (tracker < p.getSize()) {
            Note currentNote = p.getNote(tracker);
            if(tracker % segLength == 0 && currentNote != null && lastNote != null && currentNote.getPitch() == lastNote.getPitch()) {
                //System.out.println("MERGING TIES.");
                p.delUnit(tracker);
                lastNote.setRhythmValue(lastNote.getRhythmValue() + currentNote.getRhythmValue());
            }
            lastIndex = tracker;
            lastNote = currentNote;
            tracker = p.getNextIndex(tracker);
        }
    }
    
/**
 * Takes an outline, builds an abstract melody and fills it.
 * It calls fillMelody twice.
 */

private MelodyPart buildSolo(Vector<ClusterSet> outline)
  {

    /*
     * This hashtable will keep track of the clusters and abstract melodies that
     * have been used. The keys are the cluster numbers and the values are the
     * abstract melodies
     */
    Hashtable usedMelodies = new Hashtable();

    //build a string of abstract melodies from datapoints in each step of the outline
    //filling each one in turn
    String rhythmString = "";
    MelodyPart melpart;

    ClusterSet start = outline.get(0);
    Vector<Cluster> starters = start.getStarterClusters();
    Cluster first;
    //look for a starting cluster that contains a starting measure, otherwise pick randomly
    if( !starters.isEmpty() )
      {
        first = pickRandomCluster(starters);
      }
    else
      {
        first = start.getRandomPoint().getCluster();
      }

    //System.out.println(first.getName() + ": " + first.getNumDataPoints());

    //add first abstract melody
    String currentAbstractMelody;
    Polylist currentSection;

    int startIndex = mStart;

    //start creating the MelodyPart
    DataPoint point1;
    do
      {
        oldPitch = 0;
        point1 = first.getRandomDataPoint();
        currentAbstractMelody = point1.getAbstractMelody();
        currentSection = Polylist.PolylistFromString(currentAbstractMelody);
        melpart = fillMelody(mMinPitch, 
                             mMaxPitch, 
                             mMinInterval, 
                             mMaxInterval, 
                             mBeatValue,
                             mLeapProb, 
                             currentSection, 
                             mChordProg, 
                             startIndex, 
                             mAvoidRepeats);
      }
    while( goesOutOfBounds(melpart) );

    usedMelodies.put(point1.getCluster().getNumber(), currentAbstractMelody);

    //add the abstract melody to the rhythm string
    rhythmString = rhythmString.concat(currentAbstractMelody);

    //increment the starting index
    int sectionSize = dataPoints.get(0).getSegLength() * mBeatValue;
    startIndex += sectionSize;

    Cluster current = first;
    MelodyPart curr;
    DataPoint p;


    //loop through the rest of the outline, picking clusters and points from them,
    //and calling fillMelody on each measure
    //if a datapoint makes the lick go out of bounds, pick another point
    for( int i = 1; i < outline.size(); i++ )
      {
        //System.out.println("i="+i);
        current = getNextCluster(current, outline.get(i));
        //System.out.println(current.getName() + ": " + current.getNumDataPoints());
        int counter = 0;
        //need to reset oldPitch each time we retry
        int previousPitch = 0;
        do
          {
            if( counter == 0 )
              {
                previousPitch = oldPitch;
              }
            else
              {
                oldPitch = previousPitch;
              }

            int segLength = dataPoints.get(0).getSegLength();
            int measureLength = segLength * 120;
            ChordPart chords = mChordProg.extract(startIndex, startIndex + measureLength);
            current.getRandomDataPointWithMatchingChords(chords);

            p = current.getRandomDataPoint();
            currentAbstractMelody = p.getAbstractMelody();
            //starters have slope 0 0 for the first note, so change that here
            if( p.isStarter() )
              {
                //System.out.println("fixing starter");
                if( currentAbstractMelody.contains("slope 0 0") )
                  {
                    if( bernoulli(.5) )
                      {
                        currentAbstractMelody = currentAbstractMelody.replace("0 0", "1 3");
                      }
                    else
                      {
                        currentAbstractMelody = currentAbstractMelody.replace("0 0", "-1 -3");
                      }
                  }
              }
            currentSection = Polylist.PolylistFromString(currentAbstractMelody);
            //System.out.println("Abstract melody: " + currentAbstractMelody);
            //System.out.println("Abstract mel: " + currentAbstractMelody);
            curr = fillMelody(mMinPitch, 
                              mMaxPitch, 
                              mMinInterval, 
                              mMaxInterval, 
                              mBeatValue,
                              mLeapProb, 
                              currentSection, 
                              mChordProg, 
                              startIndex, 
                              mAvoidRepeats);
            counter++;
          }
        while( goesOutOfBounds(curr) && counter < MELODY_GEN_LIMIT );

        //System.out.println("counter: " + counter);            
        if( counter >= MELODY_GEN_LIMIT )
          {
            //System.out.println("STARTING OVER STARTING OVER STARTING OVER");
            oldPitch = 0;
            lastWasTied = false;
            usedMelodies = new Hashtable();
            return buildSolo(outline);
          }

        addMelody(melpart, curr, p.isTiedAtStart(), p.isTiedAtEnd());
        //increment the index for the next time we call fillmelody
        startIndex += sectionSize;
        rhythmString = rhythmString.concat(currentAbstractMelody);
      }

    rhythmGeneratedFromOutline = rhythmString;

    useOutlines = false;
    doNotSwitchOctave = false;
    oldPitch = 0;

    return melpart;
  }
    

private Cluster getNextCluster(Cluster current, ClusterSet nextSet)
  {
    NGramWithTransitions t = getTransitionObject(current.getNumber());

    //put the numbers of the clusters in the next set in an array
    int[] nextClusters = new int[nextSet.getNumRelatives() + 1];
    Vector<Cluster> relatives = nextSet.getSimilarClusters();
    for( int i = 0; i < relatives.size(); i++ )
      {
        nextClusters[i] = relatives.get(i).getNumber();
      }
    nextClusters[nextClusters.length - 1] = nextSet.getOriginal().getNumber();

    int next = t.getNextState(nextClusters);
    //if the ngram probabilities don't point to any of the possibilities for the 
    //next cluster, choose a random cluster from nextSet
    if( next == -1 )
      {
        return nextSet.getRandomCluster();
      }
    //otherwise pick a cluster probabalistically from the next set
    else
      {
        return clusters[next];
      }
  }
     

private Cluster pickRandomCluster(Vector<Cluster> clusterList)
  {
    Random rand = new Random();
    int p = rand.nextInt(clusterList.size());
    return clusterList.get(p);
  }
    

/**
 * Load in rules from the specified file
 *
 * @param grammarFile
 */

public void loadGrammar(String grammarFile)
  {
    //System.out.println("LickGen loadGrammar: " + grammarFile);
    
    // Setup defaults for syncopation and expectation values.
    useSyncopation       = defaultUseSyncopation;
    syncopationType      = defaultSyncopationType;
    syncopationConstant    = defaultSyncopationConstant;
    syncopationMultiplier   = defaultSyncopationMultiplier;
    expectancyMultiplier = defaultExpectancyMultiplier;
    expectancyConstant   = defaultExpectancyConstant;
    
    grammar.clear();
    grammar.loadGrammar(grammarFile);

    notate.setLickGenStatus("Grammar loaded: " + grammarFile);

    String soloistFileName = grammarFile.replace(".grammar", ".soloist");

    File soloistFile = new File(soloistFileName);
    if( soloistFile.exists() )
      {
        soloistLoaded = true;
        notate.setLickGenStatus("Grammar loaded with companion soloist file: " + soloistFileName);
        loadSoloist(soloistFileName);
      }
    else
      {
        soloistLoaded = false;
        //System.out.println("in loadGrammar, no soloist file named:" + soloistFileName);
      }
    
     // Load syncopation and expectation parameters if they are present.
     try
      {
      useSyncopation = Boolean.parseBoolean(getParameterQuietly(USE_SYNCOPATION));
      }
    catch(NonExistentParameterException e)
      {
      setParameter(USE_SYNCOPATION, defaultUseSyncopation);
      }
    
    try
      {
      syncopationType = getParameterQuietly(SYNCOPATION_TYPE);
       }
    catch(NonExistentParameterException e)
      {
      setParameter(SYNCOPATION_TYPE, defaultSyncopationType);
      }
    
    try
      {
      syncopationMultiplier = Double.parseDouble(getParameterQuietly(SYNCOPATION_MULTIPLIER));
      //System.out.println("Syncopation multiplier: " + syncopationMultiplier);
      }
    catch(NonExistentParameterException e)
      {
      setParameter(SYNCOPATION_MULTIPLIER, defaultSyncopationMultiplier);
      }
    
    try
      {
      syncopationConstant = Double.parseDouble(getParameterQuietly(SYNCOPATION_CONSTANT)) * MAX_SYNCO * (lengthOfTrade/SLOTS_PER_MEASURE);
      //System.out.println("Syncopation constant: " + syncopationConstant);
      }
    catch(NonExistentParameterException e)
      {
      setParameter(SYNCOPATION_CONSTANT, defaultSyncopationConstant);
      }
    
    try
      {
      expectancyMultiplier = Double.parseDouble(getParameterQuietly(EXPECTANCY_MULTIPLIER));
      }
    catch(NonExistentParameterException e)
      {
      setParameter(EXPECTANCY_MULTIPLIER, defaultExpectancyMultiplier);
      }
    
    try
      {
      expectancyConstant = Double.parseDouble(getParameterQuietly(EXPECTANCY_CONSTANT)) * MAX_EXPECTANCY;
      }
    catch(NonExistentParameterException e)
      {
      setParameter(EXPECTANCY_CONSTANT, defaultExpectancyConstant);
      }
  }

public void saveGrammar(String grammarFile)
  {
    notate.setLickGenStatus("Saving grammar to file: " + grammarFile);
    grammar.saveGrammar(grammarFile);
    if( soloistLoaded )
      {
        saveSoloist(grammarFile);
        notate.setLickGenStatus("Saving soloist file: " + grammarFile);
      }
    else
      {
        //System.out.println("in saveGrammar, not saving soloist:" + grammarFile);
      }
  }

public void saveSoloist(String grammarFile)
  {
    String soloistFileName = grammarFile.replace(".grammar", ".soloist");
    File soloistFile = new File(soloistFileName);
    notate.setLickGenStatus("Saving soloist file: " + soloistFileName);
    CreateGrammar.createSoloistFile(dataPoints, clusters, clusterSets,
                                    transitions, reverseTransitions, outlines, soloistFile);
  }

public boolean getUseSyncopation()
  {
    return useSyncopation;
  }

public String getSyncopationType()
  {
    return syncopationType;
  }

public Double getSyncopationConstant()
  {
    return syncopationConstant;
  }

public Double getSyncopationMultiplier()
  {
    return syncopationMultiplier;
  }

public Double getExpectancyMultiplier()
  {
    return expectancyMultiplier;
  }

public Double getExpectancyConstant()
  {
    return expectancyConstant;
  }

public void clearParams()
  {
    grammar.clearParams();
  }

/**
 * Get the normalized values in our probability array.
 */

public ArrayList<double[]> getProbs()
  {
    return probs;
  }

//public void showProbs(String msg)
//  {
//    System.out.println(msg);
//    for( int i = 0; i < probs.size(); i++ )
//      {
//        double[] row = probs.get(i);
//        System.out.print("row " + i + ": " + row.length + " elements: ");
//        for( int j = 0; j < row.length; j++ )
//          {
//            System.out.print(" " + row[j]);
//          }
//        System.out.println();
//      }
//  }

/**
 * Set note probabilites according to the values specified in the input array.
 */
public void setProbs(ArrayList<double[]> p)
  {
    // Clear out all the old probabilities
    probs.clear();

    // For each set of probabilities, normalize them and add them to our array.
    for( int i = 0; i < p.size(); ++i )
      {
        double total = 0;
        double[] pArray = new double[12];
        for( int j = 0; j < 12; ++j )
          {
            pArray[j] = p.get(i)[j];
            total += pArray[j];
          }

        if( total != 0 )
          {
            for( int j = 0; j < 12; ++j )
              {
                pArray[j] /= total;
              }
          }
        probs.add(pArray);
      }
    //showProbs("setProbs");
  }

/**
 * Calculate the probabilities based on the current chord progression.
 *
 * @param chordProg
 * @param chordToneProb
 * @param scaleToneProb
 * @param colorToneProb
 * @param chordToneDecayRate
 * @param selStart
 * @param length
 * @return
 */
public ArrayList<double[]> fillProbs(ChordPart chordProg, 
                                  double chordToneProb,
                                  double scaleToneProb, 
                                  double colorToneProb,
                                  double chordToneDecayRate,
                                  int selStart, 
                                  int length)
  {
    probs.clear();
    int nextIndex = selStart;

    // Keep track of which chords we've already calculated probabilities for
    chordUsed.clear();

    // Loop through the chord progression as long as
    // we haven't gone past the end of the selection.
    while( nextIndex < selStart + length && nextIndex != -1 )
      {
        Chord currentChord = chordProg.getCurrentChord(nextIndex);
        if( currentChord != null )
          {
            // If we've already calculated probabilites for the current chord, then we
            // don't need to do anything -- advance to the next chord.

            if( chordUsed.contains(currentChord.getName()) )
              {
                nextIndex = chordProg.getNextUniqueChordIndex(nextIndex);
                currentChord = chordProg.getCurrentChord(nextIndex);
                continue;
              } // Otherwise, add it to the list of chords that we've examined.
            else
              {
                chordUsed.add(currentChord.getName());
                // For right now, just set the probabilities in decreasing order based on
                // the order they appear in the "priority" entry in the vocabulary file.
              }

            // Init all probabilities to a small value

            double[] p = new double[12];
            for( int i = 0; i < 12; ++i )
              {
                p[i] = 0.0;
              }

            Polylist scaleTones;

            // Get the preferred scale type if it is present.

            if( preferredScale.isEmpty()
                    || ((String) preferredScale.second()).equals(NONE) )
              {
                scaleTones = Polylist.nil;
              }
            else if( ((String) preferredScale.second()).equals(FIRST_SCALE) )
              {
                scaleTones = currentChord.getFirstScale();
              }
            else
              {
                // Get the priority for the chord tones and the color tones for the
                // current chords

                scaleTones = Advisor.getScale((String) preferredScale.first(),
                                              (String) preferredScale.second());
              }

            Polylist chordTones = currentChord.getPriority();
            Polylist colorTones = currentChord.getColor();

            /*
            System.out.println("currentChord = " + currentChord.toString()
            + ", chordTones = " + chordTones + ", colorTones = "
            + colorTones + ", scaleTones = " + scaleTones);
             */


            // Get all the various tone types and set the corresponding probabilities.

            // Note that the notes in the chords themselves can have
            // probabilities, which over-ride the ones in the lick generator.
            // Also, the order of setting probabilities means that chord
            // tones can over-ride scale tones, and color tones can over-ride
            // both.

            accumulateProbs(scaleTones, scaleToneProb, p);
            accumulateProbs(chordTones, chordToneProb, p);
            accumulateProbs(colorTones, colorToneProb, p);

            // Advance to the next chord, and add the probabilities to our ArrayList.

            probs.add(p);
          }
      // 2 April 2012, this was positioned inside the loop above,
      // causing an infinite loop. It may have come to fore because
      // of changes made to ChordPart in release 5.12
        
      nextIndex = chordProg.getNextUniqueChordIndex(nextIndex);
      }

    return probs;
  }

    /**
     * An auxiliary function to assist in getting probabilities for the
     * tone categories: scale, chord, and color
     * Note that this deals with the new over-ride feature in the vocabulary,
     * that the tones in any of these categories can be accompanied by
     * a probability
     @param tones
     @param prob
     @param p
     */

private void accumulateProbs(Polylist tones, double categoryProb, double p[])
  {
  if( tones != null )
      {
        PolylistEnum e = tones.elements();
        while( e.hasMoreElements() )
          {
            NoteSymbol ns = (NoteSymbol) e.nextElement();

            double noteProb = ns.getProbability();

            // accumulate: add to the probability whatever is specified
            p[ns.getSemitones()] += noteProb*categoryProb;
          }
      }
  }

/**
 * Use the loaded grammar file to generate a rhythm
 * TODO: it might be nice to be able to specify a minDuration and a maxDuration here.
 */

public Polylist generateRhythmFromGrammar(int startSlot, int slots)
  {
    //TEMPORARY FIX - never trade. Trading is handled differently now.
    return grammar.run(startSlot, 
                       slots, 
                       notate, 
                       notate.getWhetherToTrade(),
                       //false,
                       notate.getImprovisorTradeFirst(),
                       //false,
                       notate.getTradingQuantum());
                       //-1);

  }

/**
 * Randomly generate a rhythm based on a minimum and maximum allowed duration.
 */

public Polylist generateRandomRhythm(int slots, 
                                     int minDuration, 
                                     int maxDuration,
                                     double restProb)
  {
    Polylist rhythmString = new Polylist();
    // compute the number of distinct durations: 1, 2, 4, 8, 16, 32
    // note that regarding durations, min is larger than max.

    if( !isPowerOf2(minDuration) || !isPowerOf2(maxDuration) )
      {
        ErrorLog.log(ErrorLog.SEVERE,
                     "Note durations must be powers of two; please fix and try again.");
        return null;
      }

    int distinctDurations = 0;
    ArrayList<Integer> noteDurations = new ArrayList<Integer>();
    for( int i = minDuration; i >= maxDuration; i /= 2 )
      {
        noteDurations.add(i);
        distinctDurations++;
      }

    int pos = 0;
    while( pos < slots )
      {
        int duration = noteDurations.get(randomIndex(distinctDurations));
            String toCons;
            if( bernoulli(restProb) )
              {
                toCons = REST_SYMBOL + duration;
              }
            else
              {
                toCons = NOTE_SYMBOL + duration;
              }
            rhythmString = rhythmString.cons(toCons);
        pos += (4 * BEAT) / duration; // Only works for 4-meter?? FIX
        }
    rhythmString = rhythmString.reverse();
    return rhythmString;
  }


/**
 * Takes a polylist containing a series of grammar terminals and returns the
 * number of slots in the list
 */

public int getNumSlots(Polylist rhythmString)
  {
    int slots = 0;
    for( Polylist L = rhythmString; L.nonEmpty(); L = L.rest() )
      {
        Polylist first = (Polylist) L.first();
        if( first.toString().length() < 7 )
          {  //doesn't contain a slope
            slots += Duration.getDuration(first.first().toString().substring(1));
          }
        else
          {
            //get rid of the slopes
            Polylist rest = first.rest().rest().rest();
            //loop through the rest of the notes
            for( Polylist R = rest; R.nonEmpty(); R = R.rest() )
              {
                slots += Duration.getDuration(R.first().toString().substring(1));
              }

          }

      }
    return slots;
  }

public boolean goesOutOfBounds(MelodyPart lick)
  {
    int l = lick.getLowestPitch();
    int h = lick.getHighestPitch();
    if( l < mMinPitch || h > mMaxPitch )
      {
        //System.out.println("Out of bounds. Choosing new datapoint.");
        return true;
      }
    else
      {
        return false;
      }
  }

public static boolean isPolylistStartingWith(String keyword, Object ob)
  {
    if( !(ob instanceof Polylist) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist)ob;
    
    if( oblist.isEmpty() )
      {
        return false;
      }
    
    Object first = oblist.first();
    
    if( !(first instanceof String) )
      {
        return false;
      }
    
    return keyword.equals((String)first);
  }

/*
 * These are the various methods and values needed for the implementation
 * of George Garzone's Triadic Chromatic approach to improvisation.
 * 
 * @author Jack Davison 2012
 */

public static final int ROOT_STORAGE_NUMBER = 6; // number of roots remembered to not repeat

public boolean useSame = true; // whether or not you should be able to begin a triad with 

public boolean useExpectancyChooser = true; // whether or not to use the expectancy algorithm in choosing which octave in which to play inversions

public Note[] recentRoots = new Note[ROOT_STORAGE_NUMBER]; // this is for keeping track of recently used roots

public int[] recentTypes = new int[ROOT_STORAGE_NUMBER+1]; // for keeping track of the corresponding chordtype for recentRoots; 5,6,7,8 correspond to maj,min,dim,aug. Is +1 to store potential type

public int rootCounter = 0; // for keeping the previous two lists up-to-date

public boolean lastMajor; // for keeping track of whether or not the last triad was a major triad

public int maxTriadicPitch = 70; // maximum pitch to be used in the segment

public int minTriadicPitch = 50; // minimum pitch to be used in the segment

public Note[] newtriad = new Note[3]; // the triad

public int lastInversion; // the most recent inversion

public void setMaxTriadic (int value) {     
        maxTriadicPitch = value;
}

public void setMinTriadic (int value) {      
        minTriadicPitch = value;
}

private void trueSetPitch (Note noteIn, int pitch) {
    noteIn.setPitch(pitch);
    noteIn.setAccidentalFromPitch();
    noteIn.setDrawnPitch(noteIn.getPitch());
}

private int rand (int Min, int Max) {
    int randOut;
    int range = Max - Min;
    randOut = Min + (int)(Math.random() * (range + 1));
    return randOut;
}

public Note getBase (Note noteIn) {
    int randNum = rand(0,19);
    Note noteOut = noteIn.copy();
    
    do {
    if (randNum <= 8) {
        if (noteIn.getPitch()-1 >= minTriadicPitch) {
            trueSetPitch(noteOut, noteIn.getPitch()-1);
        } else {
            trueSetPitch(noteOut, noteIn.getPitch()+1);
        }
    } else if (randNum <= 17) {
        if (noteIn.getPitch()+1 <= maxTriadicPitch) {
            trueSetPitch(noteOut, noteIn.getPitch()+1);
        } else {
            trueSetPitch(noteOut, noteIn.getPitch()-1);
        }
    randNum = rand(0,17); // prevents the infinite loop; in fact, ensures the loop will only repeat once at most
    } } while (!useSame && noteOut.getPitch() == noteIn.getPitch());
    
    return noteOut;    
}

public void adjustTriadicPitch(Note noteIn) {
    if (noteIn.isRest()) {
        return;
    }
    if (noteIn.getPitch() + 12 <= mMaxPitch) {
                     setMaxTriadic(noteIn.getPitch() + 12);
                     } else {
                     setMaxTriadic(mMaxPitch);
                     } 
    if (noteIn.getPitch() - 12 >= mMinPitch) {
                     setMinTriadic(noteIn.getPitch() - 12);
                     } else {
                     setMinTriadic(mMinPitch);  
                     } // range surrounds noteIn with 1.5 octaves on either side
}

public Note getTrueRoot (Note base, int inversion) {

	Note trueRoot = base.copy();

	switch(inversion) {
		case 4: trueSetPitch(trueRoot, base.getPitch());
			break;
		case 3: trueSetPitch(trueRoot, base.getPitch());
			break;
		case 2: trueSetPitch(trueRoot, base.getPitch()+5);
			break;
		case 1: if (recentTypes[ROOT_STORAGE_NUMBER] == 5) {
				trueSetPitch(trueRoot, base.getPitch()+8);
			} else {
				trueSetPitch(trueRoot, base.getPitch()+9);
			}
			break;
		case 0: trueSetPitch(trueRoot, base.getPitch());
	}

	return trueRoot;

}

public boolean inRootList (Note base, int inversion) {

	Note trueRoot = getTrueRoot(base,inversion);
        
	for (int i = 0; i < ROOT_STORAGE_NUMBER; i++) {
		if (recentRoots[i] != null && trueRoot.getPitch() == recentRoots[i].getPitch() && recentTypes[ROOT_STORAGE_NUMBER] == recentTypes[i]) {
                    return true;
                }
	}
        
        return false;
}

public void addToRootList (Note base, int inversion) {

	recentRoots[rootCounter] = getTrueRoot(base,inversion);
        recentTypes[rootCounter] = recentTypes[ROOT_STORAGE_NUMBER];
	rootCounter = (rootCounter+1) % ROOT_STORAGE_NUMBER;
}

public void putInRange (Note note) {
    
        if (note.isRest())
        {
            return;
        }


            if (note.getPitch() > maxTriadicPitch) {
                trueSetPitch(note, maxTriadicPitch - (12 - ((note.getPitch() - maxTriadicPitch) % 12)));
            } else if (note.getPitch() < minTriadicPitch) {
                trueSetPitch(note, minTriadicPitch + (12 - ((minTriadicPitch - note.getPitch()) % 12)));
            }
}

public Note[] makeTriad (Note base, int inversion) {

	int newInversion = 11; // got an error that I needed to initialize this (even though it's been assigned by the time the while is called)
	int newInversion2;
	int[] returnTriad = new int[3];
        Note[] actualReturnTriad = new Note[3];
        actualReturnTriad[0] = base.copy();
        actualReturnTriad[1] = base.copy();
        actualReturnTriad[2] = base.copy();
	do {
		newInversion2 = rand(0,10);
		switch(newInversion2) {
			case 0:
			case 1:
			case 2: newInversion = 0;
                                if(rand(0,1) == 0) {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 5;
                                } else {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 6;
                                }
				break;
			case 3:
			case 4:
			case 5: newInversion = 1;
                                if(rand(0,1) == 0) {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 5;
                                } else {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 6;
                                }
				break;
			case 6:
			case 7:
			case 8: newInversion = 2;
                                if(rand(0,1) == 0) {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 5;
                                } else {
                                    recentTypes[ROOT_STORAGE_NUMBER] = 6;
                                }
				break;
			case 9: newInversion = 3;
                                recentTypes[ROOT_STORAGE_NUMBER] = 7;
				break;
			case 10: newInversion = 4;
                                recentTypes[ROOT_STORAGE_NUMBER] = 8;
				break;
                        default: System.out.println("default in newInversion2 switch");
                                break;
		}
	} while (newInversion == inversion || inRootList(base,newInversion));
        returnTriad[0] = base.getPitch();

	switch (newInversion) {
		case 4: returnTriad[1] = base.getPitch()+4;
                        returnTriad[2] = base.getPitch()+8; //augmented
			break;
		case 3: returnTriad[1] = base.getPitch()+3;
                        returnTriad[2] = base.getPitch()+6; //diminished
			break;
		case 2: returnTriad[1] = base.getPitch()+5;
                        if (recentTypes[ROOT_STORAGE_NUMBER] == 5) {
				returnTriad[2] = base.getPitch()+9;
			} else {
				returnTriad[2] = base.getPitch()+8;
			} //2nd inversion
			break;
		case 1: if (recentTypes[ROOT_STORAGE_NUMBER] == 5) {
				returnTriad[1] = base.getPitch()+3;
                                returnTriad[2] = base.getPitch()+8;
			} else {
				returnTriad[1] = base.getPitch()+4;
                                returnTriad[2] = base.getPitch()+9;
			} //1st inversion
			break;
		case 0: returnTriad[2] = base.getPitch()+7;
                        if (recentTypes[ROOT_STORAGE_NUMBER] == 5) {
				returnTriad[1] = base.getPitch()+4;
			} else {
				returnTriad[1] = base.getPitch()+3;
			} //No inversion
			break;
                    
                default: System.out.println("default case: newInversion = " + newInversion);
	}

	addToRootList(base, newInversion);
	lastInversion = newInversion;
	
	int temp;

	if (rand(0,1) == 0) {
		temp = returnTriad[2];
		returnTriad[2] = returnTriad[1];
		returnTriad[1] = temp;
	}

	if (rand(0,1) == 0) {
		returnTriad[1] = returnTriad[1] - 12;
	}
        
        if (!useExpectancyChooser || returnTriad[0] - returnTriad[1] == 4 || returnTriad[1] - returnTriad[0] == 4) {
            if (rand(0,1) == 0) {
		returnTriad[2] = returnTriad[2] - 12;
            }
        } else if (returnTriad[0] - returnTriad[1] <= 3 || returnTriad[1] - returnTriad[0] <=3) {
            if (returnTriad[0] > returnTriad[1] && returnTriad[2] > returnTriad[1]) {
                returnTriad[2] = returnTriad[2] - 12;
            } else if (returnTriad[0] < returnTriad[1] && returnTriad[2] < returnTriad[1]) {
                returnTriad[2] = returnTriad[2] + 12;
            }
        } else {
            if (returnTriad[0] < returnTriad[1] && returnTriad[1] < returnTriad[2]) {
                returnTriad[2] = returnTriad[2] - 12;
            } else if (returnTriad[0] > returnTriad[1] && returnTriad[2] < returnTriad[1]) {
                returnTriad[2] = returnTriad[2] + 12;
            }
        }
        
        trueSetPitch(actualReturnTriad[0], returnTriad[0]);
        trueSetPitch(actualReturnTriad[1], returnTriad[1]);
        trueSetPitch(actualReturnTriad[2], returnTriad[2]);

        putInRange(actualReturnTriad[0]);
	putInRange(actualReturnTriad[1]);
	putInRange(actualReturnTriad[2]);

	return actualReturnTriad;
}

/**
 * This fillMelody is called by LickgenFrame, ThemeWeaver, and
 * twice elsewhere in this class (LickGen).
 * 
 * @param minPitch
 * @param maxPitch
 * @param minInterval
 * @param maxInterval
 * @param beatValue
 * @param leapProb
 * @param rhythmString
 * @param chordProg
 * @param start
 * @param avoidRepeats
 * @return 
 */

public MelodyPart fillMelody(int minPitch, 
                             int maxPitch, 
                             int minInterval,
                             int maxInterval,
                             int beatValue, 
                             double leapProb,
                             Polylist rhythmString, 
                             ChordPart chordProg,
                             int start, 
                             boolean avoidRepeats)
  {
    //if we are using outlines, we call this method multiple times, so we want
    //to keep oldPitch

    //System.out.println("Oldpitch:" + oldPitch);
    //System.out.println("outer rhythmString = " + rhythmString);

    if( !useOutlines )
      {
        oldPitch = 0;
      }

    MelodyPart melPart = new MelodyPart();
    
    lengthOfTrade = notate.getTradeLength();

   //Generates a rhythm with matched syncopation
    if(useSyncopation)
    {
        MelodyPart melody = notate.getCurrentMelodyPart();
        int currentSlot = grammar.getCurrentSlot();
        ChordPart chords = notate.getChordProg();
        MelodyPart currMelody = melody.extract(currentSlot - lengthOfTrade, currentSlot);
        int[] rhythmArray = Generator.getArray(rhythmString);
        int[] syncVector = currMelody.getSyncVector(15, lengthOfTrade);
        int measures = lengthOfTrade/SLOTS_PER_MEASURE;
        int origSynco = Tension.getSyncopation(syncVector, measures);
        int synco = (int)(syncopationMultiplier * origSynco + syncopationConstant);
        if(synco > MAX_SYNCO * measures)
        {
            synco = MAX_SYNCO * measures;
        }
        int[] rhythm = Generator.generateSyncopation(measures, synco, rhythmArray);
        int newSynco = Tension.getSyncopation(rhythm, measures);
        String[] rhythmList = Generator.generateString(rhythm, "E");
        rhythmString = Polylist.PolylistFromArray(rhythmList);
    }
    
    Polylist newRhythmString = new Polylist();
    Polylist section = new Polylist();
    for( Polylist L = rhythmString; L.nonEmpty(); L = L.rest() )
      {
        Polylist first;
        //System.out.println("L = " + L);
        if( L.first() instanceof Polylist )
          {
            first = (Polylist) L.first();
            //System.out.println("1 first: " + first);
          }
        else
          {
            first = Polylist.list(L.first());
            //System.out.println("2 first: " + first);
          }
        if( L.length() == 1 )
          {
            if( isPolylistStartingWith("slope", first) 
             || isPolylistStartingWith("triadic", first) 
             || isPolylistStartingWith("X", first) )
            //if( first.toString().startsWith("(slope") )
              {
                section = section.addToEnd(first);
              }
            else
              {
                section = section.append(first);
              }
            newRhythmString = newRhythmString.addToEnd(section);
          }
        else
          {
              if( isPolylistStartingWith("slope", first) 
               || isPolylistStartingWith("triadic", first)
               || isPolylistStartingWith("X", first) )
               {
                  section = section.addToEnd(first);
                }
              else
                {
                  section = section.append(first);
                }
          }
      }
    
    //System.out.println("newRhythmString = " + newRhythmString);
    
    // 6/25/13 - Hayden Blauzvern
    // In response to potential null notes, possibly generated from
    // fillPartOfMelody(), we simply repeat the fill until we create
    // a melody with no null notes.
    // 7-9-13 - HB
    // Found this to get into an infinte loop sometimes, so I've
    // added a count so it can get out of the loop. Not sure
    // what's the issue, seems to involve the lick generator having
    // issues with licks ending in rests.
    boolean repeatFill = true;
    int count = 0;
    while (repeatFill && count < MELODY_GEN_LIMIT)
    {
        count++;
        if (count == MELODY_GEN_LIMIT)
            notate.setLickGenStatus("Too many lick generations, aborting.");

        try  // I have seen this fail once, hence the try/catch. RK
        // However, someone needs to follow up on what happens if this
        // return null.
        // 6/25/13 HB - If an exception is thrown, the melody part will
        // be incomplete and not generate or paint correctly.
        {
          for( Polylist L = newRhythmString; L.nonEmpty(); L = L.rest() )
            {
              Polylist first = (Polylist) L.first();
              //System.out.println("Abstract Melody: " + L);
              MelodyPart p = fillPartOfMelody(minPitch, 
                                              maxPitch, 
                                              minInterval,
                                              maxInterval,
                                              beatValue, 
                                              leapProb,
                                              first, 
                                              chordProg,
                                              start, 
                                              avoidRepeats);
              ArrayList<Unit> units = p.getUnitList();
              for( int i = 0; i < units.size(); i++ )
                {
                    Note n = (Note) units.get(i);
                    melPart.addNote(n);
                }
              start = position;
            }

          repeatFill = false;
        }
        catch( NullPointerException e )
        {
            notate.setLickGenStatus("Fill melody failed");
            continue;
        }
    }
    return melPart;
  }
    
    
    
public MelodyPart fillPartOfMelody(int minPitch, 
                                   int maxPitch, 
                                   int minInterval,
                                   int maxInterval,
                                   int beatValue, 
                                   double leapProb,
                                   Polylist rhythmString, 
                                   ChordPart chordProg,
                                   int start, 
                                   boolean avoidRepeats)
  {
    MelodyPart lick = null;

    //try MELODY_GEN_LIMIT times to get a lick that doesn't go outside the pitch bounds

    if(useSyncopation || expectancyMultiplier != 0)
    {
        expectancy = (getExpectancyPerNote() * expectancyMultiplier) + (expectancyConstant * MAX_EXPECTANCY);
    }
    else
    {
        expectancy = expectancyConstant * MAX_EXPECTANCY;
    }
    if(expectancy > MAX_EXPECTANCY)
    {
        expectancy = MAX_EXPECTANCY;
    }
    
    //System.out.println("Expectancy: " + expectancy);
    
    int previousPitch = oldPitch;

    for( int i = 0; i < MELODY_GEN_LIMIT; i++ )
      {
        //System.out.println("Try: " + i);
        lick = new MelodyPart();
        if( start == -1 )
          {
            return null;
          }
        position = start;

        //when trying multiple times, we need to preserve the value of oldPitch
        oldPitch = previousPitch;

        fillMelodyHelper(lick, 
                   minPitch, 
                   maxPitch, 
                   minInterval, 
                   maxInterval,
                   beatValue, 
                   leapProb, 
                   rhythmString, 
                   chordProg,
                   avoidRepeats, 
                   i);
        
        //System.out.println("Lick size: " + lick.size() + " Position: " + position + " Start: " + start);
        lick.setSize(position - start);
        //System.out.println("New Lick Size: " + lick.size());

        //System.out.println("Lowest pitch:  " + lick.getLowestPitch() + " Highest pitch: " + lick.getHighestPitch());

        if( (lick.getLowestPitch() >= minPitch - 1 && lick.getHighestPitch() <= maxPitch + 1) )
          {
            return lick;
          }
        //System.out.println("lowest pitch: " + lick.getLowestPitch() + " highest pitch: " + lick.getHighestPitch());
        //return lick;
      }

    // Returns the last attempt
    //System.out.println("Note: Melody generation limit exceeded in lick generator.");
    return lick;
  }

    boolean traceLickGen = false;
    /**
     * Track previous note for purposes of rest merging.
     * This should be moved into addNote of MelodyPart eventually.
     */
    Note prevNote = null;

public void initializeAdditionOfNotes()
{
    prevNote = null;
}
/**
 * A single method for note insertion into a lick. Can be traced by setting
 * traceLickGen true or false.
 *
 * @param note
 * @param part
 * @param rhythmString
 */
private void addNote(Note note, 
                     MelodyPart part, 
                     Polylist rhythmString,
                     boolean avoidRepeats, 
                     String reason, 
                     Object item)
  {
    //System.out.println("addNote " + note);
    int slotsInserted = note.getRhythmValue();
    position += slotsInserted;
    if( note.isRest() )
      {
        // Deal with rests
        if( prevNote != null && prevNote.isRest() )
          {
            // Merge this rest with previous, without adding a new note.

            int prevRhythmValue = prevNote.getRhythmValue();

            prevNote.setRhythmValue(prevRhythmValue + slotsInserted);

            // Since no new note is being added, must take into account by lengthening part.

            part.setSize(part.size() + slotsInserted);

            if( traceLickGen )
              {
                System.out.println(
                        "rests merged: " + prevRhythmValue + " + " + slotsInserted + " = " + prevNote.getRhythmValue());
              }
          }
        else
          {
            // Rest not following another rest.
            part.addNote(note);
            prevNote = note;
          }
      }
    else
      {
        // Deal with non-rests
        if( note.isBlack() )
          {
            note.setAccidental(Constants.Accidental.SHARP);
          }

        /*
         * begin new rk 5/15/08
         */
        /*
         * hack to avoid repeated pitches by merging them into one.
         */

        if( avoidRepeats && prevNote != null && !prevNote.isRest() && prevNote.getPitch() == note.getPitch() )
          {
            // repeated pitch

            int prevRhythmValue = prevNote.getRhythmValue();

            // Make previous note longer

            prevNote.setRhythmValue(prevNote.getRhythmValue() + slotsInserted);

            // Since no new note is being added, must take into account by lengthening part.

            part.setSize(part.size() + slotsInserted);

            if( traceLickGen )
              {
                System.out.println(
                        "notes merged: " + prevRhythmValue + " + " + slotsInserted + " = " + prevNote.getRhythmValue());
              }
          }
        else
          {
            // non-repeated pitch

            setPitchUsed(note.getPitch(), REPEAT_PROB);
            oldPitch = note.getPitch();
            part.addNote(note);
            prevNote = note;
          }
      }

    if( traceLickGen )
      {
        System.out.println(
                note.toLeadsheet() + " from " + item + " by " + reason + ", net " + part + rhythmString);
      }

  }

/**
 * Fill in a given abstract melody with notes.
 * fillMelodyHelper is called from LickGen once only.
 */

private boolean fillMelodyHelper(MelodyPart lick, 
                          int minPitch, 
                          int maxPitch,
                          int minInterval, 
                          int maxInterval,
                          int beatValue, 
                          double leapProb, 
                          Polylist rhythmString,
                          ChordPart chordProg,
                          boolean avoidRepeats, 
                          int attempt)
  {
    if( traceLickGen )
      {
        System.out.println("\nrhythmString: " + rhythmString);
      }
    initializeAdditionOfNotes();
    int quarter = 0;
    int[] quarterLevel = new int[4];
    double highestExpectancy = 0;
    int highestIndex = 0;
    int qLIndex = 0;
            
    //debug System.out.println("lickgen: fillMelody");
    
    int section = 0;
    int index;
    chordUsed.clear();
    chordUsedSection.clear();

    //System.out.println("Oldpitch: " + oldPitch);

    int pitch = oldPitch;

    // If this is the first note in the lick, generate a random starting pitch
    if( oldPitch == 0 )
      {
        //System.out.println("Random starting pitch");
        pitch = getRandomNote((maxPitch + minPitch) / 2, minInterval, maxInterval,
                              minPitch, maxPitch, section);
      }

    oldPitch = pitch;

    // Set all pitchUsed values to 1.
    initPitchArray();

    if( rhythmString == null || rhythmString.isEmpty() )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Rules did not parse.  Aborting!");
        return false;
      }

    chordUsed.add(chordProg.getCurrentChord(position).getName());
    chordUsedSection.add(section);
    index = chordProg.getNextUniqueChordIndex(position);

    // Loop through the string and assign pitch values to each rhythm.
    while( rhythmString.nonEmpty() )
      {
        // The item we're processing now:

        Object item = rhythmString.first();

        // Set up for APPROACH or subsequent iterations

        rhythmString = rhythmString.rest();

        // Find out what section we're in.
        section = calcSection(chordProg, position, index, section);

        // Recalculate probabilities to reuse a given pitch
        recalcPitchArray();

        //System.out.println("item = " + item);

        if( isScaleDegree(item) )
           {
           // first is of the form (X degree duration)
           Chord chord = chordProg.getCurrentChord(position);
                        
           Note note = makeRelativeNote(item, chord);
                        
           //adjust so that pitch jump from previous note isn't more than an octave
           //unless that would make you exceed the minimum/maximum allowed pitches
           //note: if the interval is too small, it's possible octave transposing won't be able
           //to compress the pitches sufficiently (e.g. F to C from above or below is still more than a third)
           while (note.getPitch() - oldPitch > maxInterval && note.getPitch() - 12 >= minPitch) {
               note.setPitch(note.getPitch() - 12);
           }
           
           while (oldPitch - note.getPitch() > maxInterval && note.getPitch() + 12 <= maxPitch) {
               note.setPitch(note.getPitch() + 12);
           }
                        
           //System.out.println("generated note is " + note.toLeadsheet());
                        
           addNote(note, lick, rhythmString, avoidRepeats, "relative", item);
           }
        
        // New code 10 June 2008:
        else if( item instanceof Polylist )
          {
            // Handle inner-structure by calling fillMelody recursively
            // Example of inner is (slope 1 3 S16 S16 S16 S16)
            // Meaning a render of four sixteenth notes with a minimum rise
            // of 1 between and a maximum rise of 3.

            Polylist inner = (Polylist) item;
            if( inner.nonEmpty() )
              {
                Object first = inner.first();
                //System.out.println("inner = " + inner + ", first = " + first);
                 if( first.equals("slope") )
                  {
                    // Process "slope" specification, a phrase of a specified slope
                    // (slope Min Max ... notes ...)
                    // Min is the minimum gap to the next note, Max is the maxium
                    // If downward slope is desired, the lower negative value should come first.
                    // Examples:
                    //     (slope 1 4 S8 S8 S8 S8)  
                    //     means 4 eigth-notes, upward, with interval between 1 and 4 semitones
                    //
                    //     (slope -4 -1 S8 S8 S8 S8)  
                    //     means 4 eigth-notes, downward, with interval between 1 and 4 semitones

                    // Get rid of "slope" keyword for further processing

                    inner = inner.rest();

                    // Check for Min and Max as numbers

                    if( inner.length() >= 2 && inner.first() instanceof Long && inner.second() instanceof Long )
                      {
                        int lowerLimit = ((Long) inner.first()).intValue();
                        int upperLimit = ((Long) inner.second()).intValue();
                        // Replace inner with just the notes part
                        inner = inner.rest().rest();


                        while( inner.nonEmpty() )
                          {
                            Object innerItem = inner.first();

                            if( innerItem instanceof String )
                              {
                                String innerString = (String) innerItem;

                                Note note = parseNote(innerString, beatValue);

                                int type = note.getPitch();

                                switch( type )
                                  {
                                    case REST:
                                      {
                                        addNote(note, lick, rhythmString, avoidRepeats, "slope/rest", innerItem);
                                      }
                                    break;

                                    case APPROACH:
                                      {
                                        // Assuming we have more notes to process, get the next note, and figure
                                        // out its rhythm value.
                                        if( inner.rest().nonEmpty() )
                                          {
                                            inner = inner.rest();
                                            //get next note

                                            Note nextNote = parseNote((String) inner.first(), beatValue);
                                            int nextType = nextNote.getPitch();

                                            // Set the next note....

                                            int nextPitch = chooseNote(position + note.getRhythmValue(),
                                                                       oldPitch + lowerLimit, 
                                                                       oldPitch + upperLimit,
                                                                       chordProg, 
                                                                       nextType, 
                                                                       oldPitch, 
                                                                       minPitch, 
                                                                       maxPitch, 
                                                                       attempt);

                                            nextNote.setPitch(nextPitch);

                                            // We have a 50% probability to approach from above, and a 
                                            // 50% probability to approach from below.
                                            // TODO: eventually this should be determined from a user setting or
                                            // possibly the grammar.
                                            // Don't choose same note

                                            if( nextPitch + 1 == oldPitch )
                                              {
                                                note.setPitch(nextPitch - 1);
                                              }
                                            else if( nextPitch - 1 == oldPitch )
                                              {
                                                note.setPitch(nextPitch + 1);
                                              }
                                            else
                                              {
                                                if( lowerLimit == 0 || upperLimit == 0 )
                                                  {
                                                    if( upperLimit > 0 )
                                                      {
                                                        note.setPitch(nextNote.getPitch() - 1);
                                                      }
                                                    if( lowerLimit < 0 )
                                                      {
                                                        note.setPitch(nextNote.getPitch() + 1);
                                                      }
                                                  }
                                                else if( lowerLimit > 0 )
                                                  {
                                                    note.setPitch(nextNote.getPitch() - 1);
                                                  }
                                                else if( upperLimit < 0 )
                                                  {
                                                    note.setPitch(nextNote.getPitch() + 1);
                                                  }
                                                else
                                                  {
                                                    note.setPitch(nextNote.getPitch() - 1);
                                                  }
                                              }

                                            addNote(note, 
                                                    lick, 
                                                    rhythmString, 
                                                    avoidRepeats, 
                                                    "slope/approach", 
                                                    innerItem);
                                            
                                            addNote(nextNote, 
                                                    lick, 
                                                    rhythmString, 
                                                    avoidRepeats, 
                                                    "slope/approach target", 
                                                    innerItem);
                                            
                                            pitch = nextPitch;
                                          } // If there's nothing left in the string, we'll just pick a 
                                        // random note.
                                        else
                                          {
                                            pitch = chooseNote(position, 
                                                               oldPitch + lowerLimit,
                                                               oldPitch + upperLimit, 
                                                               chordProg, 
                                                               SCALE, 
                                                               oldPitch,
                                                               minPitch, 
                                                               maxPitch, 
                                                               attempt);
                                             note.setPitch(pitch);
                                            addNote(note, 
                                                    lick, 
                                                    rhythmString, 
                                                    avoidRepeats, 
                                                    "slope/approach random", 
                                                    innerItem);
                                          }
                                      }
                                    break;

                                    default:
                                      {
                                        // If it's not a rest or approach tone, treat it normally.
                                        pitch = chooseNote(position, 
                                                           oldPitch + lowerLimit,
                                                           oldPitch + upperLimit, 
                                                           chordProg, 
                                                           type, 
                                                           oldPitch,
                                                           minPitch, 
                                                           maxPitch, 
                                                           attempt);
                                        note.setPitch(pitch);
                                        addNote(note, 
                                                lick, 
                                                rhythmString, 
                                                avoidRepeats, 
                                                "slope/default", 
                                                innerItem);
                                      }
                                  }
                                // Remember old pitch in case it is needed.
                                oldOldPitch = oldPitch;
                                oldPitch = pitch;

                                // Move on to next note in inner, if any.
                                inner = inner.rest();
                              }
                          }
                      }
                  } // end of slope processing
                 else if ( first.equals("triadic") ) {
                     MelodyPart melodyPart = notate.getCurrentMelodyPart();
                     int lengthInSlots = Duration.getDuration(inner.second().toString());
                     Note noteIn = prevNote;
                     if (prevNote != null) {
                     //System.out.println("prevNote = " + prevNote.toLeadsheet()); 
                     }
                     if (noteIn == null || noteIn.isRest() || noteIn.getPitch() > mMaxPitch || noteIn.getPitch() < mMinPitch) {
                         noteIn = new Note((mMinPitch + (mMaxPitch - mMinPitch)/2),60);
                     } 
                     // the following code is to ensure recentRoots and recentTypes from older triadic sections don't get in the way of this section
                     for (int j = 0; j < ROOT_STORAGE_NUMBER; j++)
                     {
                         recentRoots[j] = null;
                         recentTypes[j] = 0;
                     }
                     recentTypes[ROOT_STORAGE_NUMBER] = 0;
                     Note base = getBase(noteIn);
                     int abort = 0; // to avoid really messy improvisation with faulty inputs
                     int lengthInEighths = lengthInSlots / 60;
                     int lengthInEightTriples = lengthInSlots / 180;
                     int lengthInEighthNoteTriplets = lengthInSlots / 40;
                     int lengthInEighthTripletTriples = lengthInSlots / 120;
                     int lengthInSixteenths = lengthInSlots / 30;
                     int lengthInSixteenthTriples = lengthInSlots / 90;
                     int lengthInSixteenthNoteTriplets = lengthInSlots / 20;
                     int lengthInSixteenthTripletTriples = lengthInSlots / 60;
                     int lengthToUse;
                     int remainder = 0;
                     lastInversion = rand(0,4);
                     String whatLength = inner.third().toString();
                     
                     if (whatLength.equals("16") && (lengthInSlots % 30 == 0)) {
                         lengthToUse = lengthInSixteenthTriples;
                         remainder = lengthInSixteenthTriples % lengthInSixteenths;
                         base.setRhythmValue(30);
                     } else if (whatLength.equals("16/3") && (lengthInSlots % 20 == 0)) {
                         lengthToUse = lengthInSixteenthTripletTriples;
                         remainder = lengthInSixteenthTripletTriples % lengthInSixteenthNoteTriplets;
                         base.setRhythmValue(20);
                     } else if (whatLength.equals("8") && (lengthInSlots % 60 == 0)) {
                         lengthToUse = lengthInEightTriples;
                         remainder = lengthInEightTriples % lengthInEighths;
                         base.setRhythmValue(60);
                     } else if (whatLength.equals("8/3") && (lengthInSlots % 40 == 0)) {
                         lengthToUse = lengthInEighthTripletTriples;
                         remainder = lengthInEighthTripletTriples % lengthInEighthNoteTriplets;
                         base.setRhythmValue(40);
                     } else {
                         lengthToUse = 0;
                         abort = 1;
                     }

                     if (abort != 1) {
                     for (int x = 0; x < lengthToUse; x++) {
                        adjustTriadicPitch(base);
                        newtriad = makeTriad(base,lastInversion);
                        addNote(newtriad[0], lick, Polylist.nil, avoidRepeats, "triadic", item);
                        addNote(newtriad[1], lick, Polylist.nil, avoidRepeats, "triadic", item);
                        addNote(newtriad[2], lick, Polylist.nil, avoidRepeats, "triadic", item);
                        //System.out.println("triad: {" + newtriad[0].toLeadsheet() + ", " + newtriad[1].toLeadsheet() + ", " + newtriad[2].toLeadsheet() + "}");
                        base = getBase(newtriad[2]);
                     }
                     if (remainder == 1) {
                         newtriad = makeTriad(base,lastInversion);
                         addNote(newtriad[0], lick, Polylist.nil, avoidRepeats, "triadic", item);
                        //System.out.println("triad: {" + newtriad[0].toLeadsheet() + "}");                         
                     } else if (remainder == 2) {
                         newtriad = makeTriad(base,lastInversion);
                         addNote(newtriad[0], lick, Polylist.nil, avoidRepeats, "triadic", item);
                         addNote(newtriad[1], lick, Polylist.nil, avoidRepeats, "triadic", item);
                        //System.out.println("triad: {" + newtriad[0].toLeadsheet() + ", " + newtriad[1].toLeadsheet() + "}");
                     }
                     } else {
                         //System.out.println("Aborted: Faulty inputs");
                     }
                     
              }
              } // end of non-empty inner processing

          } // end of Polylist processing
        else if( item instanceof String )
          {
            String rhythmVal = (String) item;

            // Get the note corresponding to the string.
            Note note = parseNote(rhythmVal, beatValue);
            int type = note.getPitch();

            if( note == null )
              {
                ErrorLog.log(ErrorLog.WARNING,
                             "Invalid note string: " + rhythmVal + "; no melody will be generated.");
                return false;
              }
                    
            switch( type )
              {
                case REST:
                  {
                    addNote(note, lick, rhythmString, avoidRepeats, "rest", item);
                  }
                break;

                case BASS:
                  {
                    makeBassNote(note, position, chordProg);
                    addNote(note, lick, rhythmString, avoidRepeats, "bass", item);
                  }
                break;

                case APPROACH:
                  {
                    // Assuming we have more notes to process, get the next note, and figure
                    // out its rhythm value.
                    if( rhythmString.nonEmpty() && !(rhythmString.first() instanceof Polylist) )
                      {
                        Note nextNote = parseNote((String) rhythmString.first(), beatValue);

                        // Force the next type to be a chord tone for now -- can change this later.
                        int nextType = CHORD;
                        int nextPitch = pitch;
                        boolean validNote;

                        // Figure out what section the next note is in.
                        section = calcSection(chordProg, position, index, section);

                        // Get a note value for the next note.
                        for( int i = 0; i < NOTE_GEN_LIMIT; ++i )
                          {
                            nextPitch = getRandomNote(oldPitch, minInterval, maxInterval,
                                                      minPitch, maxPitch, section);
                            String pitchString =
                                    PitchClass.getPitchClassFromMidi(nextPitch).toString();
                            validNote =
                                    checkNote(position, nextPitch, pitchString, chordProg,
                                              nextType);
                            if( (!avoidRepeats || bernoulli(pitchWasUsed(pitch))) && validNote )
                              {
                                break;
                              }
                          }

                        // Set the next note....
                        nextNote.setPitch(nextPitch);
                        setPitchUsed(nextPitch, REPEAT_PROB);

                        // We have a 50% probability to approach from above, and a 
                        // 50% probability to approach from below.
                        // TODO: eventually this should be determined from a user setting or
                        // possibly the grammar.
                        // Don't choose same note
                        if( nextPitch + 1 == oldPitch )
                          {
                            note.setPitch(nextPitch - 1);
                          }
                        else if( nextPitch - 1 == oldPitch )
                          {
                            note.setPitch(nextPitch + 1);
                          }
                        else
                          {
                            if( bernoulli(.5) )
                              {
                                note.setPitch(nextNote.getPitch() + 1);
                              }
                            else
                              {
                                note.setPitch(nextNote.getPitch() - 1);
                              }
                          }

                        oldOldPitch = oldPitch;
                        oldPitch = nextPitch;

                        addNote(note, lick, rhythmString, avoidRepeats, "approach", item);
                        rhythmString = rhythmString.rest(); // ONLY for the APPROACH case
                        addNote(nextNote, lick, rhythmString, avoidRepeats, "approach target", item);
                      }
                    else
                      {
                        pitch = getRandomNote(oldPitch, minInterval, maxInterval,
                                              minPitch, maxPitch, section);
                        note.setPitch(pitch);
                        oldOldPitch = oldPitch;
                        oldPitch = pitch;
                        addNote(note, lick, rhythmString, avoidRepeats, "approach->random", item);
                      }

                  } // approach
                break;

                case RANDOM:
                  // If we just want to put down a random note, we're going to ignore
                  // all probabilities; however, pay attention to the min/max pitch values
                  // and the min/max interval values
                  {
                    int low = Math.max(minPitch, oldPitch - maxInterval);
                    int high = Math.min(maxPitch, oldPitch + maxInterval);
                    while( true )
                      {
                        pitch = randomNote(low, high);
                        if( !(pitch > oldPitch - minInterval && pitch < oldPitch + minInterval) )
                          {
                            break;
                          }
                      }
                    note.setPitch(pitch);
                    oldOldPitch = oldPitch;
                    oldPitch = pitch;
                    addNote(note, lick, rhythmString, avoidRepeats, "random", item);
                  }
                break;
                /*
                 * case OUTSIDE: // Right now this does the exact same thing
                 * random does. { int low = Math.max(minPitch, oldPitch -
                 * maxInterval); int high = Math.min(maxPitch, oldPitch +
                 * maxInterval); System.out.println("you have reached Case
                 * OutSide in fill melody"); while (true) { pitch =
                 * randomNote(low, high); if (!(pitch > oldPitch - minInterval
                 * && pitch < oldPitch + minInterval)) { break; } }
                 * note.setPitch(pitch); oldPitch = pitch; addNote(note, lick,
                 * rhythmString, avoidRepeats, "random", item); } break;
                 *
                 */

                case OUTSIDE:
                  // Transposes a semitone from the default
                  {
                    oldOldPitch = oldPitch;
                    if( bernoulli(leapProb) )
                      {
                        if( Math.abs(oldPitch - maxPitch) > Math.abs(oldPitch - minPitch) )
                          {
                            oldPitch = Math.min(oldPitch + 12, maxPitch);
                          }
                        else
                          {
                            oldPitch = Math.max(oldPitch - 12, minPitch);
                          }
                      }
                    boolean validNote;

                    for( int i = 0; i < NOTE_GEN_LIMIT; ++i )
                      {
                        pitch = getRandomNote(oldPitch, minInterval, maxInterval,
                                              minPitch, maxPitch, section);
                        String pitchString =
                                PitchClass.getPitchClassFromMidi(pitch).toString();
                        validNote = checkNote(position, pitch, pitchString, chordProg, type);
                        if( (!avoidRepeats || bernoulli(pitchWasUsed(pitch))) && validNote )
                          {
                            break;
                          }
                      }
                    //System.out.println("Color Chord Pitch: "+ pitch);
                    pitch = pitch + 1; //adds one to what the default returns
                    //System.out.println("Color Chord Pitch Plus one: " + pitch);
                    note.setPitch(pitch);
                    addNote(note, lick, rhythmString, avoidRepeats, "outside", item);
                  }
                break;

                //Generates a note based on the expectancy value
                case EXPECTANCY:
                {
                    if(expectancy <= 0)
                    {
                        expectancy = MAX_EXPECTANCY;
                    }
                    ArrayList<Integer> midiArray = new ArrayList<Integer>();
                    ArrayList<Double> expectDiffs = new ArrayList<Double>();
                    double expectDiffSum = 0;
                    //Gets all of the possible pitches and their expectancy values
                    for (int midi = minPitch ; midi <= maxPitch; midi++)
                    {
                        if(prevPrevPitch == 0)
                        {
                            prevPrevPitch = prevPitch;
                        }
                        Chord currentChord = chordProg.getCurrentChord(position);
                        double expect = Expectancy.getExpectancy(midi, prevPitch, prevPrevPitch, currentChord);
                        //Getting higher order expectancies
                        if(quarter > QUARTER_NOTE)
                        {
                            prevQuarter = quarterLevel[highestIndex];
                            prevPrevQuarter = prevQuarter;
                            quarter = quarter % QUARTER_NOTE;
                            quarterLevel = new int[4];
                            highestExpectancy = 0;
                        }
                        double quarterExpect = Expectancy.getExpectancy(midi, prevQuarter, prevPrevQuarter, currentChord);
                        double weightedExpectancy = ((BASE_WEIGHT * expect) + (QUARTER_WEIGHT * quarterExpect)) / (BASE_WEIGHT + QUARTER_WEIGHT);
                        quarterLevel[qLIndex] = midi;
                        if(expect > highestExpectancy)
                        {
                            highestIndex = qLIndex;
                            highestExpectancy = expect;
                        }
                        quarter += note.getRhythmValue();
                        int limit = DEFAULT_EXPECTANCY_LIMIT;
                        if(expectancy > MINIMAX_EXPECTANCY + DEFAULT_EXPECTANCY_LIMIT)
                        {
                            limit = (int)expectancy - MINIMAX_EXPECTANCY;
                        }
//                        double invExpectDiff = limit - Math.abs(expectancy - expect);
////                        if(Math.abs(expectancy - expect) < .1)
////                        {
////                            invExpectDiff = 15;
////                        }
//                        if(expect > expectancy)
//                        {
//                            invExpectDiff = limit;
//                        }
                        double invSurpriseTension =  weightedExpectancy;
                        if(invSurpriseTension > expectancy)
                        {
                            invSurpriseTension = expectancy;
                        }
                        else if(Math.abs(expectancy - weightedExpectancy) > limit || invSurpriseTension < 0)
                        {
                            invSurpriseTension = 0;
                        }
                        midiArray.add(midi);
                        expectDiffs.add(invSurpriseTension);
                        expectDiffSum += invSurpriseTension;
                    }
                    //Chooses a note with a probability derived from expectancy value
                    double rand = Math.random();
                    double offset = 0;
                    for( int i = 0; i < expectDiffs.size(); ++i )
                    {
                        // If the random number falls between the range of the probability 
                        // for that rule, we choose it and break out of the loop.
                        if( rand >= offset && rand < offset + (expectDiffs.get(i) / expectDiffSum) )
                        {
                            note.setPitch(midiArray.get(i));
                            prevPrevPitch = prevPitch;
                            prevPitch = midiArray.get(i);
                            break;
                        }
                        offset += (expectDiffs.get(i) / expectDiffSum);
                    }
                    if( note.getPitch() > 127 )
                        {
                            //System.out.println("Bad note in fillMelody " + note);
                            note = new Rest(note.getRhythmValue());
                        }
                    addNote(note, lick, rhythmString, avoidRepeats, "expectancy", item);
                }
                break;

                default:
                  // If it's not a rest or approach tone, treat it normally.
                  // What is "normally"??
                  {
                    if( bernoulli(leapProb) )
                      {
                        if( Math.abs(oldPitch - maxPitch) > Math.abs(oldPitch - minPitch) )
                          { //drop an octave
                            oldPitch = Math.min(oldPitch + 12, maxPitch);
                          }
                        else
                          {
                            oldPitch = Math.max(oldPitch - 12, minPitch);
                          }
                      }
                    boolean validNote;

                    for( int i = 0; i < NOTE_GEN_LIMIT; ++i )
                      {
                        pitch = getRandomNote(oldPitch, minInterval, maxInterval,
                                              minPitch, maxPitch, section);
                        String pitchString =
                                PitchClass.getPitchClassFromMidi(pitch).toString();
                        validNote = checkNote(position, pitch, pitchString, chordProg, type);
                        if( (!avoidRepeats || bernoulli(pitchWasUsed(pitch))) && validNote )
                          {
                            break;
                          }
                      }
                    note.setPitch(pitch);
                    addNote(note, lick, rhythmString, avoidRepeats, "default", item);
                  }
                break;
              } // switch

          } // end of String case
        else
          {
            // unknown item in rhythmString
            notate.setLickGenStatus("Error: unknown item in abstract melody: " + item);
          }
      } // while

    return false;
  }

    /**
    * Gets the average expectancy per note of the previous 4 bars
    * @return 
    */
    private double getExpectancyPerNote()
    {
        //Gets first two notes of melody
        MelodyPart melody = notate.getCurrentMelodyPart();
        int currentSlot = grammar.getCurrentSlot();
        ChordPart chords = notate.getChordProg();
        MelodyPart currMelody = melody.extract(currentSlot - lengthOfTrade, currentSlot);
        //System.out.println("GEPN slot: " + currentSlot);
        return Expectancy.getExpectancyPerNote(currMelody, lengthOfTrade, chords, currentSlot);
    }
    
    
/**
 * The following is based on code from BassPatternElement.
 * It makes a note based on the scale degree notation,
 * e.g. (X 4 8) means an 8th note on the 4th scale degree.
 */
    
public static Note makeRelativeNote(Object ob, Chord chord)
  {
  int rootOffset = 60; // middle C for now
  int root = chord.getRootSemitones() + rootOffset;

  int pitch = root;
  int duration = 60;
  //System.out.println("makeRelativeNote " + ob); 
  if( ob instanceof Polylist )
      {
        Polylist listOb = (Polylist) ob;
        int len = listOb.length();
        if( len == 3 )
          {
            if( listOb.first().equals("X") )
              {
                // Handle scale note, e.g. (X 5 8+16/3 U)

                BassPatternElement.AccidentalType accidental = BassPatternElement.AccidentalType.NONE;

                // Get the scale degree

                Object second = listOb.second();

                int degreeValue = 1;

                if( second instanceof Long )
                  {
                    degreeValue = ((Long) listOb.second()).intValue();

                    if( degreeValue < -7 || degreeValue > 13 )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Scale degree out of range 1 to 7 in grammar note : " + ob);
                        return null;
                      }
                  }
                else if( second instanceof String )
                  {
                    String secondString = (String) second;
                    String degreeString = secondString;

                    
                    while(degreeString.charAt(0) == 'b' || degreeString.charAt(0) == '#' )
                    {
                        switch( degreeString.charAt(0) )
                          {
                            case 'b':
                                pitch--;
                                degreeString = degreeString.substring(1);
                                break;   // flat

                            case '#':
                                pitch++;
                                degreeString = degreeString.substring(1);
                                break; // sharp

                            default:
                          }
                    }
                    try
                      {
                        degreeValue = Integer.parseInt(degreeString);
                      }
                    catch( Exception e )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Scale degree is wrong in grammar note : " + ob);
                        return null;
                      }
                  }
              String chordFamily = chord.getFamily();
              //System.out.println(chord + " is family " + chordFamily);
              
              if( chordFamily.equals("minor") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=1; break;
                        case  1:  pitch+=0; break;
                        case  2:  pitch+=2; break;
                        case  3:  pitch+=3; break;
                        case  4:  pitch+=5; break;
                        case  5:  pitch+=7; break;
                        case  6:  pitch+=9; break;
                        case  7:  pitch+=11; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=15; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=19; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
                }
              else if( chordFamily.equals("minor7") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=0;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=3;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=7;  break;
                        case  6:  pitch+=9;  break;
                        case  7:  pitch+=10; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=15; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=19; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
                }
              else if( chordFamily.equals("dominant") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=2;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=4;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=7;  break;
                        case  6:  pitch+=9;  break;
                        case  7:  pitch+=10; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=16; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=19; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
                }
              else if( chordFamily.equals("half-diminished") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=2;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=3;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=7;  break;
                        case  6:  pitch+=9;  break;
                        case  7:  pitch+=10; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=15; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=18; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
                }
              else if( chordFamily.equals("diminished") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=3;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=3;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=6;  break;
                        case  6:  pitch+=8;  break;
                        case  7:  pitch+=9;  break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=15; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=18; break;
                        case 13:  pitch+=20; break;
                        default:
                      }
                }
              else if( chordFamily.equals("augmented") )
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=2;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=4;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=8;  break;
                        case  6:  pitch+=9;  break;
                        case  7:  pitch+=10; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=16; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=20; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
                }
              else // major, or anything else for now
                {
                  switch(degreeValue)
                      {
                        case -1:  pitch-=1;  break;
                        case  1:  pitch+=0;  break;
                        case  2:  pitch+=2;  break;
                        case  3:  pitch+=4;  break;
                        case  4:  pitch+=5;  break;
                        case  5:  pitch+=7;  break;
                        case  6:  pitch+=9;  break;
                        case  7:  pitch+=11; break;
                        case  8:  pitch+=12; break;
                        case  9:  pitch+=14; break;
                        case 10:  pitch+=16; break;
                        case 11:  pitch+=17; break;
                        case 12:  pitch+=19; break;
                        case 13:  pitch+=21; break;
                        default:
                      }
              }
                  
                // Get the duration

                String durationString;

                Object third = listOb.third();

                if( third instanceof Long || third instanceof String )
                  {

                    durationString = "" + third;

                    // for checking purposes

                    duration = Duration.getDuration(durationString);

                    if( duration == 0 )
                      {
                        ErrorLog.log(ErrorLog.WARNING,
                                     "Grammar relative element has incorrect duration: " + ob);
                        return null;
                      }
                  }

//
//                // Ge the direction, if there is one.
//
//                BassPatternElement.DirectionType direction = BassPatternElement.DirectionType.ANY;
//
//                if( len == 4 )
//                  {
//                    if( listOb.fourth().equals("U") )
//                      {
//                        direction = BassPatternElement.DirectionType.UP;
//                      }
//                    else if( listOb.fourth().equals("D") )
//                      {
//                        direction = BassPatternElement.DirectionType.DOWN;
//                      }
//                    else
//                      {
//                        ErrorLog.log(ErrorLog.WARNING,
//                                     "Direction must be U or D in bass note : " + ob);
//                        return null;
//                      }
//
//                  }

                return new Note(pitch, duration);
              }
          }
      }
    ErrorLog.log(ErrorLog.FATAL, "Unrecognized relative note : " + ob);
    return null;
  }


    /**
     * Use to avoid array out of bounds in getting value of pitchUsed
    @param 
    @return
     */
    private double pitchWasUsed(int pitch) {
        if (pitch >= 0 && pitch < pitchUsed.length) {
            return pitchUsed[pitch];
        }

        // FIX: I don't know whether this is a good idea.
        // It should be better than an array index error.

        return 1;
    }

    public int parseLength(Polylist rhythm) {
        int len = 0;
        while (rhythm.nonEmpty()) {
            if (rhythm.first() instanceof String) {
                len += Duration.getDuration(((String) rhythm.first()).substring(1));
            } else if (rhythm.first() instanceof Polylist) {
                // FIX for general case!!
                len += parseLength(((Polylist) rhythm.first()).rest().rest().rest());
            }
            rhythm = rhythm.rest();
        }

        return len;
    }

    public void setPreferredScale(String r, String s) {
        preferredScale = Polylist.nil;
        preferredScale = preferredScale.cons(s);
        preferredScale = preferredScale.cons(r);

    }

    public Polylist getPreferredScale() {
        return preferredScale;
    }


/**
 * Set a grammar or lickgen parameter.
 *
 * @param paramName
 * @param param
 */
    
public void setParameter(String paramName, Object param)
  {
    grammar.addRule(Polylist.list(Grammar.PARAM, Polylist.list(paramName, param)));
  }


/**
 * Get a grammar or lickgen parameter. Conversion from String is up to the client.
 * Complains through the ErrorLog if parameter does not exist.
 *
 * @param paramName
  */

public String getParameter(String paramName)
  {
    try
      {
        return getParameterQuietly(paramName);
      }
    catch( NonExistentParameterException e )
      {
        String defaultParameter = getDefaultParameter(paramName);
        if( defaultParameter == null )
          {
           ErrorLog.log(ErrorLog.WARNING, paramName + " does not exist.");
           return null;
          }
      return defaultParameter;
      }
  }

/**
 * Get a grammar or lickgen parameter. Conversion from String is up to the client.
 * Throws NonExistentParameterException if not present.
 *
 * @param paramName
  */

public String getParameterQuietly(String paramName) throws NonExistentParameterException
  {
    ArrayList<Polylist> params = grammar.getParams();
    for( int i = 0; i < params.size(); ++i )
      {
        Polylist p = params.get(i);
        if( ((String) p.first()).equals(paramName) && p.length() >= 2 )
          {
            return Advisor.concatListWithSpaces(p.rest());
          }
      }

    throw new NonExistentParameterException(paramName);
  }


public String getDefaultParameter(String paramName)
  {
    if( paramName.equals(MIN_PITCH_STRING) )
      {
        return MIN_PITCH_DEFAULT;
      }
    if( paramName.equals(MAX_PITCH_STRING) )
      {
        return MAX_PITCH_DEFAULT;
      }
    if( paramName.equals(MIN_DURATION) )
      {
        return MIN_DURATION_DEFAULT;
      }
    if( paramName.equals(MAX_DURATION) )
      {
        return MAX_DURATION_DEFAULT;
      }
    if( paramName.equals(MIN_INTERVAL) )
      {
        return MIN_INTERVAL_DEFAULT;
      }
    if( paramName.equals(MAX_INTERVAL) )
      {
        return MAX_INTERVAL_DEFAULT;
      }
    if( paramName.equals(REST_PROB) )
      {
        return REST_PROB_DEFAULT;
      }
    if( paramName.equals(LEAP_PROB) )
      {
        return LEAP_PROB_DEFAULT;
      }
    if( paramName.equals(CHORD_TONE_WEIGHT) )
      {
        return CHORD_TONE_WEIGHT_DEFAULT;
      }
    if( paramName.equals(COLOR_TONE_WEIGHT) )
      {
        return COLOR_TONE_WEIGHT_DEFAULT;
      }
    if( paramName.equals(SCALE_TONE_WEIGHT) )
      {
        return SCALE_TONE_WEIGHT_DEFAULT;
      }
    if( paramName.equals(CHORD_TONE_DECAY) )
      {
        return CHORD_TONE_DECAY_DEFAULT;
      } 
     if( paramName.equals(AUTO_FILL) )
      {
        return AUTO_FILL_DEFAULT;
      }
     if( paramName.equals(RECTIFY) )
      {
        return RECTIFY_DEFAULT;
      } 
     if( paramName.equals(AVOID_REPEATS) )
      {
        return AVOID_REPEATS_DEFAULT;
      } 
     if( paramName.equals(SCALE_TYPE) )
      {
        return SCALE_TYPE_DEFAULT;
      }
     if( paramName.equals(SCALE_ROOT) )
      {
        return SCALE_ROOT_DEFAULT;
      }
     return null;
  }


/**
 * Use to compute the current section.
 */

private int calcSection(ChordPart chordProg, int pos, int index, int oldSection)
  {
    int section = oldSection;

    // Calculate what section we're in if we've gone past the end of the
    // old section:
    if( pos >= index && index != -1 )
      {
        // If we've already seen the chord, then get the index of that occurence, and
        // use that as the current section; otherwise, we mark the current chord as seen,
        // and increment the section.

        // An out-of-range problem arose here May 11, 2012. RK tried to clean it up.

        String name = chordProg.getCurrentChord(pos).getName();
        int nameIndex = chordUsed.indexOf(name);
        if( nameIndex != -1 && nameIndex < chordUsedSection.size() )
          {
            section = chordUsedSection.get(nameIndex);
          }
        else
          {
            section = chordUsedSection.size();
            chordUsed.add(name);
            chordUsedSection.add(section);
          }
      }
//System.out.println("calcSection, index = " + index + " oldSection = " + oldSection + " section = " + section);
    return section;
  }


/**
 * Make certain types of notes, such as bass (modeled on checkNote)
 */

private void makeBassNote(Note note, int pos, ChordPart chordProg)
  {
    note.setPitch(
            chordProg.getCurrentChord(pos).getRootPitchClass().getSemitones() + 48); // FIX!
  }

// Sets probabilities of note types
    private int[] setProb(int chord, int color, int random, int scale) {
        int[] result = new int[4];
        result[0] = chord;
        result[1] = color;
        result[2] = random;
        result[3] = scale;
        return result;
    }

// Take in absolute lower/upper note interval, chordPart, and type, and choose a note based on
// this information
    private int chooseNote(int pos, int low, int high, ChordPart chordProg, int type,
            int lastPitch, int minPitch, int maxPitch, int attempt) {

        //if(type == GOAL) type = CHORD;
        
        Random rand = new Random();

        //change a percentage of notes to goal notes
        if (rand.nextInt(100) < GOAL_PROB) {
            type = GOAL;
        }

        
        //Deal with (slope 0 0) 
        //TODO: make this less arbitrary - should depend on extracted data
        boolean slope00 = false;
        if(low == lastPitch && high == lastPitch) {
            slope00 = true;
            //if(bernoulli(.5)) {
            //    high += 4;
            //}
            //else low  -=4;
        }
        
        //relax constraints for constant interval
        if (low == high) {
            if(low != oldPitch + 1)
                low--;
            if(high != oldPitch - 1)
                high++;
            }
        
        
        //relax constraints for very high maximum jumps
        //if(high - lastPitch >= 12) {
        //    high = lastPitch + 11;
        //}
        
        //if(low - lastPitch <= 12) {
        //    low = lastPitch - 11;
        //}
          
        //relax constraints for very high minimum jumps
        if (low - lastPitch >= MIN_JUMP_UPPER_BOUND) {
            low -= MIN_JUMP_UPPER_BOUND / 2;
        }

        if (high - lastPitch <= -MIN_JUMP_UPPER_BOUND) {
            high += MIN_JUMP_UPPER_BOUND / 2;
        }

        
        //remove repeated notes by excluding 0 from interval
        //if (slope00 && rand.nextInt(100) < PERCENT_REPEATED_NOTES_TO_REMOVE) {
        //    if (low == lastPitch) {
        //        low++;
        //    }
        //    if (high == lastPitch) {
        //        high--;
        //    }
        //}

        //make sure low isn't higher than high and vice versa
        if(low > high && low > lastPitch) low = high;
        if(high < low && high < lastPitch) high = low;
        
        //notes of type NOTE are basically equivalent to scale tones
        if (type == NOTE) {
            type = SCALE;
        }

        //get current chord
        Chord chord = chordProg.getCurrentChord(pos);

        if (chord == null) {
            type = RANDOM;
        } else //if there is no chord sounding, look for next or previous chord
        if (chord.getName().equals(NOCHORD)) {
            chord = chordProg.getNextUniqueChord(pos);
        } else if (chord.getName().equals(NOCHORD)) {
            chord = chordProg.getPrevChord(pos);
        }

        //if there is no next or previous chord, set the type to random
        if (chord == null || chord.getName().equals(NOCHORD)) {
            type = RANDOM;
        }

        //random tones
        //if (type == RANDOM) {
        //    return rand.nextInt(high - low + 1) + low;
        //}

        int[] numTypes = new int[4];
        int[] noteTypes = null;
        //if we are looking for a chord tone and we can't find one, expand the interval up to 3 times
        for (int j = 0; j == 0 || (type == CHORD && j < 3 && numTypes[0] == 0); j++) {
            //if(j > 0) System.out.println("Expanding interval: " + j);
            noteTypes = getNoteTypes(pos, low, high, chordProg);
            



            //get distribution of note types
            //index 0 = chord tones, 1 = color tones, 2 = random tones, 3 = scale tones
            for (int i = 0; i < noteTypes.length; i++) {
                switch (noteTypes[i]) {
                    case CHORD:
                        numTypes[0]++;
                        numTypes[3]++;
                        break;
                    case COLOR:
                        numTypes[1]++;
                        numTypes[3]++;
                        break;
                    default:
                        numTypes[2]++;
                        break;
                }
            }
            if(type == CHORD && numTypes[0] == 0) {
                if(low != oldPitch +1) {
                    low--; 
                }
                if(high != oldPitch -1) {
                    high++;
                }
            }
        }
    
        if(type == GOAL) {

                // No longer used??

                int finalPitch = lastPitch;
                int loop = 0;
                while (finalPitch == lastPitch && loop < 100) {
                    Polylist chordNotes = chord.getSpell();
                    int numNotes = chordNotes.length();
                    PitchClass root = chord.getRootPitchClass();
                    PitchClass[] goalNotes = new PitchClass[numNotes];

                    int[] goalProbs = new int[numNotes];
                    int sumOfProbs = 0;
                    for (int i = 0; i < numNotes; i++) {
                        NoteSymbol noteSymbol = (NoteSymbol) chordNotes.first();
                        chordNotes = chordNotes.rest();
                        goalNotes[i] = noteSymbol.getPitchClass();
                        //System.out.println("Pitch Class: " + goalNotes[i]);
                        int diff = PitchClass.findRise(root, goalNotes[i]);
                        if (diff < 0) {
                            diff += 12;
                        }
                        if ((diff == 3 || diff == 4) || (diff == 10 || diff == 11)) {//3rd or 7th 
                            goalProbs[i] = THIRD_SEVENTH;          //40%
                        } else if ((diff == 0) || (diff == 7)) {                  //root or 5th
                            goalProbs[i] = ROOT_FIFTH;          //30%
                        } else if ((diff >= 1 && diff <= 3) || (diff == 9)) {         //9th or 13th(6th)   
                            goalProbs[i] = NINTH_THIRTHEENTH;           //14%
                        } else if ((diff == 6 || diff == 5)) {                      //#11th or 11th
                            goalProbs[i] = SHARP11_11;           //10%
                        } else {                                                    //b9, #9, b13   1, 3, 8
                            goalProbs[i] = FLAT9_SHARP9_FLAT13;            //6%
                        }
                        sumOfProbs += goalProbs[i];
                    }
                    int randomNumber = rand.nextInt(sumOfProbs) + 1;
                    int index = -1;
                    for (int i = 0; i < numNotes; i++) {
                        randomNumber -= goalProbs[i];
                        if (randomNumber <= 0) {
                            index = i;
                            i = numNotes;
                        }
                    }
                    finalPitch = goalNotes[index].getSemitones();
                    while (Math.abs(finalPitch - oldPitch) > 6) {
                        finalPitch += 12;
                    }
                    loop++;
                }
                return finalPitch;
            }
            
        else {
            NoteChooser noteChooser = new NoteChooser(doNotSwitchOctave);
            return noteChooser.getNote(minPitch, maxPitch, low, high, type, numTypes, noteTypes, attempt);
        }

        
    }

    /**
     * Create an array of note types in a given interval.
     */
    public int[] getNoteTypes(int pos, int low, int high, ChordPart chordProg) {
        int pitch;
        int[] noteTypes = new int[high - low + 1];
        for (int i = low; i <= high; i++) {
            pitch = i;
            String pitchString =
                    PitchClass.getPitchClassFromMidi(pitch).toString();
            //test chord/color
            if (checkNote(pos, pitch, pitchString, chordProg, CHORD)) {
                noteTypes[i - low] = CHORD;
            } else if (checkNote(pos, pitch, pitchString, chordProg, COLOR)) {
                noteTypes[i - low] = COLOR;
            } else {
                noteTypes[i - low] = RANDOM;
            }
        }
        return noteTypes;
    }

        /**
    * 
    * @param location - the location in the part
    * @param low - lowest allowed pitch
    * @param high - highest allowed pitch
    * @param chordProg
    * @return - returns character value of the note type in a particular location
    */   
        public char getNoteType(int location, int low, int high, ChordPart chordProg) {
       char notetype;
        int[] notetone = getNoteTypes(location, low, high, chordProg);
        switch (notetone[0]) {
            case LickGen.CHORD:
                notetype = 'C';
                break;
            case LickGen.COLOR:
                notetype = 'L';
                break;
            default:
                notetype = 'X';
                break;
        }
        return notetype;
    }
    
 
    
    /**
     * See if a given pitch is a chord tone, scale tone, or color tone.
    @param pos
    @param pitch
    @param pitchString
    @param chordProg
    @param type
    @return
     */
private boolean checkNote(int pos, int pitch, String pitchString,
                          ChordPart chordProg, int type)
  {
    Chord currentChord = chordProg.getCurrentChord(pos);
    if( currentChord == null
     || currentChord.getName().equals(NOCHORD)
     || currentChord.getScales().isEmpty() )
      {
        return true;
      }

    switch( type )
      {
        case NOTE:
            return true;

        case BASS:
          {
            PitchClass rootClass = currentChord.getRootPitchClass();
            return rootClass.enharmonic(pitch);
          }

        case CHORD:
              {
                Polylist chordTones = currentChord.getSpell();

                while( chordTones.nonEmpty() )
                  {
                    if( (pitch % 12) ==
                        ((NoteSymbol) chordTones.first()).getSemitones() )
                      {
                        return true;
                      }
                    chordTones = chordTones.rest();
                  }
              }
            break;

        case SCALE:
              {
                Polylist scaleTones = new Polylist();
                if( preferredScale.isEmpty() ||
                    ((String) preferredScale.second()).equals(NONE) )
                  {
                    return true;
                  }
                else if( ((String) preferredScale.second()).equals(FIRST_SCALE) )
                  {
                    scaleTones = currentChord.getFirstScale();
                  }
                else
                  {
                    scaleTones = Advisor.getScale(
                        (String) preferredScale.first(),
                        (String) preferredScale.second());
                  }

                while( scaleTones.nonEmpty() )
                  {
                    if( (pitch % 12) ==
                        ((NoteSymbol) scaleTones.first()).getSemitones() )
                      {
                        return true;
                      }
                    scaleTones = scaleTones.rest();
                  }
              }
            break;

        case COLOR:
              {
                Polylist colorTones = currentChord.getColor();

                while( colorTones.nonEmpty() )
                  {
                    if( (pitch % 12) ==
                        ((NoteSymbol) colorTones.first()).getSemitones() )
                      {
                        return true;
                      }
                    colorTones = colorTones.rest();
                  }
              }
            break;
      } // switch

    return false;
  }

/**
 * Classify note relative to chord.
 * @param note
 * @param chord
 * @return BASS, CHORD, COLOR, SCALE, or NOTE
 * in order of specificity
 */

public static int classifyNote(Note note, Chord chord)
  {
    if( chord == null
     || chord.getName().equals(NOCHORD)
     || chord.getScales().isEmpty() )
      {
        return NOTE;
      }
    
    // Get pitch class
    int pitch = note.getPitch() % 12;
    PitchClass rootClass = chord.getRootPitchClass();
    
    // Check for chord tone
    Polylist chordTones = chord.getSpell();

    while( chordTones.nonEmpty() )
      {
        if( pitch == ((NoteSymbol) chordTones.first()).getSemitones() )
          {
            return CHORD;
          }
        chordTones = chordTones.rest();
      }

    // Not a chord tone. Check for color
    Polylist colorTones = chord.getColor();

    while( colorTones.nonEmpty() )
      {
        if( pitch == ((NoteSymbol) colorTones.first()).getSemitones() )
          {
            return COLOR;
          }
        colorTones = colorTones.rest();
      }

    // Not a chord nor color tone. 
    return NOTE;
  }

    
// Returns a note corresponding to the correct terminal value.
// If the terminal dictates a rest, the pitch of the note will be
// set to REST.  Otherwise, the pitch is set to 0.  We're assuming
// this will be changed later.
    private Note parseNote(String rhythmVal, int beatValue) {
        Note note;
        int duration = Duration.getDuration(rhythmVal.substring(1));

        switch (rhythmVal.charAt(0)) {
            case T_NOTE:
                note = new Note(NOTE, Constants.Accidental.NATURAL, duration);
                break;
            case T_CHORD:
                note = new Note(CHORD, Constants.Accidental.NATURAL, duration);
                break;
            case T_SCALE:
                note = new Note(SCALE, Constants.Accidental.NATURAL, duration);
                break;
            case T_COLOR:
                note = new Note(COLOR, Constants.Accidental.NATURAL, duration);
                break;
            case T_APPROACH:
                note = new Note(APPROACH, Constants.Accidental.NATURAL, duration);
                break;
            case T_RANDOM:
                note = new Note(RANDOM, Constants.Accidental.NATURAL, duration);
                break;
            case T_OUTSIDE:
                note = new Note(OUTSIDE, Constants.Accidental.NATURAL, duration);
                break;
            case T_EXPECTANCY:
                note = new Note(EXPECTANCY, Constants.Accidental.NATURAL, duration);
                break;
            case T_REST:
                note = Note.makeRest(duration);
                break;
            case T_BASS:
                note = new Note(BASS, Constants.Accidental.NATURAL, duration);
                break;
            case T_GOAL:
                note = new Note(GOAL, Constants.Accidental.NATURAL, duration);
                break;
            default:
                note = new Note(NOTE, Constants.Accidental.NATURAL, duration);
                break;
        }

        return note;
    }

/**
 * Get a note within a certain range of the given pitch that is
 * selected based on the given note probabilities.
 */
    
private int getRandomNote(int pitch,
                          int minStep,
                          int maxStep,
                          int minPitch,
                          int maxPitch,
                          int section)
  {
    ArrayList<Integer> availPitches = new ArrayList<Integer>();
    ArrayList<Double> availProbs = new ArrayList<Double>();
    double probSum = 0.0;

    try
      {
        // Loop through all the probabilities and add in any notes that have
        // probability greater than 0.
        for( int i = 0; i < 12; ++i )
          {
            // Temporary HACK ALERT; Preventing index out of range. 
            // Moving to rev. 790
            // May have something to do with transition from Vector to ArrayList
            if( section >= probs.size() )
              {
                section = probs.size() - 1;
              }

            if( probs.get(section)[i] != 0 )
              {
                // We only need to look at pitches that are greater than the minimum
                // pitch value we want.
                int pitchToAdd = ((minPitch / 12) * 12) + i;

                // While we haven't exceeded the maximum range specified...
                while( pitchToAdd <= maxPitch )
                  {
                    // If the pitch we're considering is within a specified range of the last
                    // pitch, add it to the array of possibilities.
                    if( pitchToAdd >= minPitch 
                     && ((   pitchToAdd >= pitch - maxStep
                          && pitchToAdd <= pitch - minStep) 
                            
                         || (pitchToAdd >= pitch + minStep
                          && pitchToAdd <= pitch + maxStep)) )
                      {
                        availPitches.add(pitchToAdd);
                        availProbs.add(probs.get(section)[i]);
                        probSum += probs.get(section)[i];
                      }
                    pitchToAdd += 12;
                  }
              }
          }
      }
    catch( IndexOutOfBoundsException e )
      {
        // shouldn't happen, but has.
        // Not sure what to do.
      }

    // Adjust the probabilities based on how many pitches just got added.
    for( int i = 0; i < availProbs.size(); ++i )
      {
        availProbs.set(i, availProbs.get(i) / probSum);    // Now loop through all the possibilities, and select a random one
        // based on the calculated probability values.
      }
    double rand = Math.random();
    double offset = 0;

    for( int i = 0; i < availProbs.size(); ++i )
      {
        if( rand >= offset && rand < offset + availProbs.get(i) )
          {
            return availPitches.get(i);
          }
        offset += availProbs.get(i);
      }

    return pitch;
  }


/**
 * Choose a random index less than num.
 */
    private int randomIndex(int num) {
        int value = (int) (num * Math.random());
        return value < num
                ? value
                : num - 1;
    }

// Comment: This is not going to give a uniform discrete
// distribution, because Math.random() will generate 1 fairly
// seldom. We need to modify it to make it closer to uniform.
// rk, 12 June 2008

// Generate a random number between min and max, inclusive.
    private int randomNote(int min, int max) {
        return (int) (min + (max - min) * Math.random());
    }

    private boolean bernoulli(double prob) {
        return Math.random() > (1. - prob);
    }

// Fill in all pitchUsed values with false.
    private void initPitchArray() {
        for (int i = 0; i < TOTALPITCHES; ++i) {
            pitchUsed[i] = 1;
        }
    }

    private void setPitchUsed(int i, double value)
    {
        if( i >= 0 && i < pitchUsed.length )
        {
            pitchUsed[i] = value;
        }
    }

    private void recalcPitchArray() {
        for (int i = 0; i < TOTALPITCHES; ++i) {
            if (pitchUsed[i] < 1) {
                pitchUsed[i] *= 2;
            } else {
                pitchUsed[i] = 1;
            }
        }
    }

    private boolean isPowerOf2(int x) {
        // trust me, it works!
        return ((x > 0) && ((x & (x - 1)) == 0));
    }
    
    public Grammar getGrammar() {
        return grammar;
    }
    
    public ArrayList<Score> getHeadData() {
        return headData;
    }
    
    public boolean soloistIsLoaded()
      {
        return soloistLoaded;
      }
}
