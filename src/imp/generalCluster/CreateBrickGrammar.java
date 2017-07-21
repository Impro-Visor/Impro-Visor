/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster;

import static imp.Constants.BEAT;
import imp.roadmap.brickdictionary.Block;
import imp.roadmap.brickdictionary.Brick;
import static imp.generalCluster.CreateGrammar.getRuleStringsFromWriter;
import static imp.generalCluster.CreateGrammar.getRulesFromWriter;
import static imp.generalCluster.CreateGrammar.processRule;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.MetricListFactories.MetricListFactory;
import imp.lickgen.LickgenFrame;
import imp.gui.Notate;
import imp.gui.Stave;
import imp.gui.StaveScrollPane;
import imp.lickgen.NoteConverter;
import imp.util.ErrorLog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import polya.Polylist;

/**
 *
 * @author Mark Heimann
 */
public class CreateBrickGrammar {
    private static HashSet<String> brickKinds = new HashSet<String>();
    private static String[] brickKindsArray;
    private static HashSet<Integer> brickDurations = new HashSet<Integer>();
    private static int[] brickDurationsArray;
    private static ArrayList<Block> blocks = new ArrayList<Block>();
    private static List<Vector<DataPoint>> brickLists = new ArrayList<Vector<DataPoint>>();
    private static ArrayList<Cluster[]> allClusters = new ArrayList<Cluster[]>();
    private static ArrayList<Vector<ClusterSet>> allClusterSets = new ArrayList<Vector<ClusterSet>>();
    private static ArrayList<Vector<Vector<ClusterSet>>> allOutlines = new ArrayList<Vector<Vector<ClusterSet>>>();
    private static ArrayList<DataPoint[]> allReps = new ArrayList<DataPoint[]>();
    private static MetricListFactory metricListFactory;
    
    private static int MEASURE_LENGTH; //so we can determine if bricks start on measures
    
   /**
     * processByBrick 
     * Process corpus one brick at a time
     *
     * @param notate used to get melody information from leadsheet
     * @param chorusNumber which chorus of the tune we want to process
     * @param frame used to write productions
     */
    public static void processByBrick(Notate notate, int chorusNumber, LickgenFrame frame) {
        //step 1: roadmap the tune to find out what bricks it uses and where
        ArrayList<Block> currentBlocks = notate.getRoadMapBlocks(); //blocks from the current tune we're processing
        for (Block block : currentBlocks) {
            blocks.add(block); //blocks keeps track of all blocks from all tunes in the corpus
        }
        //PartIterator iterates through choruses Score.size() Score.getPart()
        MEASURE_LENGTH = notate.getCurrentMelodyPart().getMeasureLength();
        StaveScrollPane ssp = notate.getStaveScrollPane()[chorusNumber];
            Stave s = ssp.getStave();
            MelodyPart melPart = notate.getMelodyPart(s);
            ChordPart chordProg = notate.getChordProg();

            //step 2: scan melodies one brick at a time
            int totalDuration = 0; //so we can keep track of where we are in the tune
            for (int i = 0; i < currentBlocks.size(); ++i) {
                Block currentBlock = currentBlocks.get(i); //the block that we're currently processing within the current tune
                
                //-1 to prevent spillover into next measure
                int totalDurationPlusThisBlock = totalDuration + currentBlock.getDuration() - 1; 
                                                
                //if we only want to learn based on bricks not general chordBlocks also;
                //otherwise leave this condition out
                //note: we only want to use bricks that start at the beginning of measures
                //for the sake of QC (who knows what's up with little fractional measure bricks)
                if (currentBlock instanceof Brick
                    && (totalDuration % MEASURE_LENGTH == 0)
                    && (currentBlock.getDuration() % MEASURE_LENGTH == 0)) { 
                    
                    //this will keep track of what kind of bricks we have in the tune
                    brickKinds.add(currentBlock.getDashedName());
                    brickDurations.add(currentBlock.getDuration());
                    MelodyPart blockMelody = melPart.extract(totalDuration, totalDurationPlusThisBlock, true); 
                    ChordPart blockChords = chordProg.extract(totalDuration, totalDurationPlusThisBlock);
                    String blockAbstract = NoteConverter.melodyToAbstract(blockMelody, 
                                                                                blockChords, 
                                                                                (i == 0), 
                                                                                notate.getLickGen());
                    int location = totalDuration % melPart.size(); //tell production writing method how far we are
                                                                   //in a given chorus and which chorus
                    if (blockAbstract != null) {
                        frame.writeProductionForBricks(blockAbstract,
                                currentBlock.getDuration()/BEAT,
                                location,
                                true,
                                currentBlock.getDashedName(),
                                chorusNumber); //was chorusCount
                    }
                }
                totalDuration += currentBlock.getDuration();
            }
        
        //for convenience (to make it easier to refer to a specific brick type), store brick types in array
        brickKindsArray = new String[brickKinds.size()];
        int index = 0;
        Iterator iter = brickKinds.iterator();
        while (iter.hasNext()) {
            brickKindsArray[index] = (String) iter.next();
            ++index;
        }
        
        brickDurationsArray = new int[brickDurations.size()];
        int indexDur = 0;
        Iterator iterDur = brickDurations.iterator();
        while (iterDur.hasNext()) {
            brickDurationsArray[indexDur] = (Integer) iterDur.next();
            ++indexDur;
        }
        Arrays.sort(brickDurationsArray); //so that we have unique durations in sorted order
    }

    /**
     * create 
     * Learn a grammar based on fragments gleaned from solos clustered
     * together by similarity within types of harmonic bricks
     *
     * @param chordProg the chord progression
     * @param inWriter the StringWriter we're getting initial rules from
     * @param outfile the file we're writing the grammar to
     * @param repsPerCluster how many representatives to choose from each kind
     * of cluster
     * @param useRelative whether or not we're using relative pitches
     * @param useAbstract whether or not we're using abstract melodies
     * @param notate we set its status
     */
    public static void create(ChordPart chordProg,
            StringWriter inWriter,
            String outFile,
            int repsPerCluster,
            boolean useRelative,
            boolean useAbstract,
            Notate notate,
            MetricListFactory mlf) {
        
        metricListFactory = mlf;
        

        //do processing by brick
        if (brickKindsArray.length == 0) { //must be a pretty strange tune for this to happen...
            ErrorLog.log(ErrorLog.COMMENT, "No bricks found in the tune. "
                    + "Please try again with windows");
            return;
        }
        
        //initial overhead
        notate.setLickGenStatus("Writing grammar rules: " + outFile);

        //if useHead is true, we will add datapoints from the head into
        //a separate ArrayList, and we will not use them in clustering
        boolean useHead = false;

        //make initial calls to read from the file
        Polylist[] rules = getRulesFromWriter(inWriter);
        String[] ruleStrings = getRuleStringsFromWriter(inWriter);
        
        //create a list of lists, each holding DataPoints corresponding to a certain type of brick
        ArrayList<DataPoint> headData = new ArrayList<DataPoint>();
        int brickListsSize = brickKindsArray.length + 1; //an extra one to hold non-bricks if needed
        for (int i = 0; i < brickListsSize; ++i) {
            brickLists.add(new Vector<DataPoint>());
        }
        
        //store the data
        //NOTE: vectors are out of date, but we continue to use them to build off existing cluster methods that use them
        for (int i = 0; i < rules.length; i++) {
            DataPoint temp = processRule(rules[i], ruleStrings[i], Integer.toString(i), metricListFactory);
            String brickName = temp.getBrickType();
            
            //if we care about separating out the head, AND if rule belongs to the head, store its data separately
            if (useHead && temp.isHead()) { 
                headData.add(temp);
            } else {
                //store data in the vector in the list of vectors
                //corresponding to a specific brick type (as indexed in the brick types array)
                if (!brickName.equals("None")) {
                    int brickTypeIndex = java.util.Arrays.asList(brickKindsArray).indexOf(brickName);
                    brickLists.get(brickTypeIndex).add(temp);
                }
                else {
                    brickLists.get(brickLists.size() - 1).add(temp);
                }
            }
        }
        notate.setLickGenStatus("Wrote " + rules.length + " grammar rules.");

        //note: no need for a .soloist file
        writeBrickGrammar(useRelative, useAbstract, outFile);
    }

    /**
     * writeBrickGrammar
     * Write the grammar to a file
     *
     * @param useRelative whether or not we're using relative pitch
     * @param useAbstract whether or not we're using abstract melodies
     * @param outfile the file we're writing the grammar to
     */
    public static void writeBrickGrammar(boolean useRelative, boolean useAbstract, String outfile) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfile, true));
            //overhead rules that specify how many duration slots to subtract off for bricks of different durations
            for (int dur = 0; dur < brickDurationsArray.length; ++dur) {
                out.write("\n(rule (P Y) ((BRICK "
                        + brickDurationsArray[dur]
                        + ") (P (- Y "
                        + brickDurationsArray[dur]
                        + "))) "
                        + Math.pow(10, dur) + ")");
            }
            out.write("\n");
            
            //write rules by brick type
            for (Vector<DataPoint> list : brickLists) {
                for (DataPoint point : list) {
                    if (useRelative) { //determine how we want to represent melody info
                        writeRule(point.getRelativePitchMelody(), point, out);
                    }
                    if (useAbstract) {
                        writeRule(point.getObjData(), point, out);
                    }
                    if (!(useRelative || useAbstract)) {
                        ErrorLog.log(ErrorLog.COMMENT, "No note option specified."
                                + "Please try again using relative pitches and/or abstract melodies for bricks");
                        return;
                    }
                }
             }
            
            out.close();
        } catch (Exception e) {
            System.out.println("Exception writing grammar: " + e.toString());
            e.printStackTrace();
        }
    }
    
    /**
     * writeRule
     * actually write a rule out
     *
     * @param rule the rule we're writing out
     * @param rep the data point whose information needs to be written
     * @param out the BufferedWriter we're writing to
     */
    public static void writeRule(String rule, DataPoint rep, BufferedWriter out) {
        try {
            out.write("(rule (BRICK "
                            + (rep.getSegLength()*BEAT)
                            + ") ("
                            + rule
                            + ") (builtin brick " //evaluates to 1 if brick type is this brick's type; 0 otherwise
                            + rep.getBrickType()
                            + ") 1.0)\n");        // artificial probability
                        
        } catch (Exception e) {
            System.out.println("IO exception: " + e.toString());
        }
    }
}
