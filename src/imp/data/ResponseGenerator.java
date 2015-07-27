/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.Constants;
import static imp.Constants.BEAT;
import imp.com.InvertCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ReverseCommand;
import static imp.data.AbstractMelodyExtractor.getAbstractMelody;
import imp.data.Part.PartIterator;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.TransformLearning;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import javafx.util.Pair;
import polya.Polylist;

/**
 *
 * @author muddCS15
 */
public class ResponseGenerator {
    
    private MelodyPart response;
    private MelodyPart grammarSolo;
    private MelodyPart originalMelody;
    private static final int start = 0;
    private int[] metre;
    private int stop;
    private int nextSection;
    private ChordPart soloChords;
    private ChordPart responseChords;
    private Notate notate;
    private LickGen lickGen;
    private final BeatFinder beatFinder;
    private final TransformLearning flattener;
    private static final boolean ONLY_CHORD_TONES = true;
    private static final boolean ALL_TONES = false;
    //HAS NOTHING TO DO WITH PHRASE CLASS
    private PhraseTable phrases;
    
    public void showGrammarSolo(){
        notate.addChorus(grammarSolo);
    }
    
    public ResponseGenerator(Notate notate, int [] metre){
        this.notate = notate;
        this.lickGen = notate.getLickGen();
        this.beatFinder = new BeatFinder(metre);
        this.flattener = new TransformLearning();
        this.metre = metre;
        Polylist rhythm = lickGen.generateRhythmFromGrammar(0, notate.getScoreLength());
        grammarSolo = notate.generateLick(rhythm, 0, notate.getScoreLength());
        phrases = new PhraseTable(notate);
    }
    
    public void newResponse(MelodyPart response,ChordPart soloChords, ChordPart responseChords, int nextSection){
        this.response = response;
        this.originalMelody = response.copy();
        this.stop = response.size()-1;
        this.soloChords = soloChords;
        this.responseChords = responseChords;
        this.nextSection = nextSection;
    }
    
    //STEP 0 - load the solo and the chords the response will be played over
    
    //set response
    public void setResponse(MelodyPart response){
        this.response = response;
        this.stop = response.size()-1;
    }

    //set chords
    public void setChords(ChordPart responseChords){
        this.responseChords = responseChords;
    }
    
    //STEP 1 - flatten the solo
    
    //Flatten a solo to the default resolution
    //currently flatten to every beat
    public void flattenSolo(){
        flattenSolo(beatFinder.EVERY_BEAT);
    }

    //Flatten solo to a specified resolution
    //Resolutions specified by strings must be converted
    //Examples:
    //beatFinder.EVERY_BEAT
    //beatFinder.MEASURE_LENGTH
    //beatFinder.STRONG_BEATS
    public void flattenSolo(String resolution){
        flattenSolo(beatFinder.getResolution(resolution));
    }

    //Flatten solo to specified resolution
    //(flattens based on response chords)
    //Examples:
    //Constants.WHOLE
    //Constants.HALF
    public void flattenSolo(int resolution){
        response = flattener.flattenByResolution(response, responseChords, resolution, start, stop, false);
    }

    //STEP 2 - modify the flattened solo (inversion/retrograde/retrograde inversion/no change)
    
    //Modify the solo in a simple way
    //i.e. invert, reverse, transpose
    public void modifySolo(){
        int options = 4;
        Random r = new Random();
        int selection = r.nextInt(options);
        switch(selection){
            case 0:
                //inversion
                invertSolo();
                break;
            case 1:
                //retrograde
                reverseSolo();
                break;
            case 2:
                //retrograde inversion
                invertSolo();
                reverseSolo();
                break;
            case 3:
                //original
                break;
        }
    }
    
    public MelodyPart extractFromGrammarSolo(int startSlot, int slotLength){
        MelodyPart mp = new MelodyPart();
        mp = grammarSolo.extract(startSlot, startSlot + slotLength - 1, true, true);
        return mp;
    }

    public void abstractify() {
        String abstractMelody = AbstractMelodyExtractor.getAbstractMelody(
                0,
                responseChords.getSize() / BEAT,
                false,
                false,
                response,
                soloChords
        );
        response = notate.getLickgenFrame().fillAndReturnMelodyFromText(abstractMelody, responseChords);
    }

    public MelodyPart lookupAndPlay() {
        MelodyPart[] parts = chopResponse();
        MelodyPart finalMelody = new MelodyPart();
        for (MelodyPart mp: parts ) {
            phrases.addPhrase(mp);
            MelodyPart nextPhrase = phrases.getNextPhrase(mp);
            if (nextPhrase == null) {
                nextPhrase = mp;
            }
            ArrayList<Note> notes = nextPhrase.getNoteList();
            for(Note n : notes){
                finalMelody.addNote(n);
            }
        }
        return finalMelody;
    }
    
    public MelodyPart userRhythm(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        return mash(choppedGrammar, choppedResponse);
        
        //System.out.println("User chopped: " + Arrays.toString(choppedResponse));
        //System.out.println("Computer chopped: " + Arrays.toString(choppedGrammar));
    }
    
    public MelodyPart userMelody(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        return mash(choppedResponse, choppedGrammar);
        
        //System.out.println("User chopped: " + Arrays.toString(choppedResponse));
        //System.out.println("Computer chopped: " + Arrays.toString(choppedGrammar));
    }
    
    public MelodyPart swapMelodyRhythm(){
        MelodyPart[] choppedResponse = chopResponse();
        MelodyPart[] choppedGrammar = chopResponse(grammarSolo, nextSection);
        
        Pair<MelodyPart[], MelodyPart[]> swappedAndChopped = randomSwap(choppedResponse, choppedGrammar);
        
        MelodyPart[] melody = swappedAndChopped.getKey();
        MelodyPart[] rhythm = swappedAndChopped.getValue();
                
        return mash(melody, rhythm);
    }
    
    public Pair<MelodyPart[], MelodyPart[]> randomSwap(MelodyPart[] part1, MelodyPart[] part2) {
        Random generator = new Random();
        for (int i = 0; i < part1.length; i++) {
            int isSwap = generator.nextInt(2);
            if (isSwap == 1) {
                MelodyPart savePart1 = part1[i].copy();
                MelodyPart savePart2 = part2[i].copy();
                part1[i] = savePart2;
                part2[i] = savePart1;
            }
        }
        return new Pair(part1, part2);
    }
    
    public MelodyPart mash(MelodyPart[] melody, MelodyPart[] rhythm){
        Vector<Integer>[] responseMelodies = extractMelodies(melody);
        MelodyPart mp = new MelodyPart();
        for(int i = 0; i < rhythm.length; i++){
            if (!responseMelodies[i].isEmpty()){
                Vector<Integer> thisMelody = responseMelodies[i];
                LinkedList<Note> thisRhythm = rhythm[i].getNotes();
                int nonRestIndex = 0;
                for(int j = 0; j < thisRhythm.size(); j++){
                    Note n = thisRhythm.get(j);
                    if(n.isRest());
                    else{
                        int melodyiIndex = nonRestIndex % thisMelody.size();
                        n.setPitch(thisMelody.get(melodyiIndex));
                        nonRestIndex++;
                    }
                    mp.addNote(n);
                }
            }
        }
        return mp;
    }
    
    public MelodyPart[] chopResponse(){
        return chopResponse(response, 0);
    }
    
    public MelodyPart[] chopResponse(MelodyPart mp, int start){
        int beat = (Constants.WHOLE / metre[1]);
        int measure = (metre[0] * beat);
        int numMeasures = (response.getSize() / measure);
        MelodyPart[] mpa = new MelodyPart[numMeasures];
        for(int i = 0; i < numMeasures; i++){
            int measureStartSlot = (start + (i * measure));
            int measureEndSlot = measureStartSlot + measure - 1;
            mpa[i] = mp.extract(measureStartSlot, measureEndSlot, true, true);
        }
        return mpa;
    }

    public Vector<Integer>[] extractMelodies(MelodyPart[] mpa) {
        int beat = (Constants.WHOLE / metre[1]);
        int measure = (metre[0] * beat);
        int numMeasures = (response.getSize() / measure);
        Vector<Integer>[] melodies;
        melodies = new Vector[numMeasures];
        for (int i = 0; i < numMeasures; i++) {
            Vector<Integer> newMelody = new Vector();
            ArrayList<Note> notes = mpa[i].getNoteList();
            for (Note n : notes) {
                if (!n.isRest()) {
                    newMelody.add(n.getPitch());
                }
            }
            melodies[i] = newMelody;
        }
        return melodies;
    }

    public void rhythmicGuideLine(boolean generatedContour){
        int direction = direction();
        int [] limits = limits(direction);
        //System.out.println(limits[0]+"\t"+limits[1]);

        ChordPart rhythmicChords = rhythmicChords();
        String startDegree;
        if(response.getFirstNote()!=null){
            Note lastNote = response.getLastNote();
            Chord firstChord = rhythmicChords.getChord(0);
            NoteChordPair ncp = new NoteChordPair(lastNote, firstChord);
            startDegree = ncp.getRelativePitch();
        }else{
            startDegree = "3";
        }
        
        //                                                  chords direction / deg1 deg2 altern / low high / maxdur mix allowColor / alwaysDisallowSame
        //one line starting on three no preferred direction allow color tones. Use range limits of user solo.
        
        GuideLineGenerator generator;
        if(!generatedContour){
            generator = new GuideLineGenerator(
                                            rhythmicChords, direction, 
                                            startDegree, "", true, 
                                            limits[0], limits[1], 
                                            GuideLineGenerator.NOPREFERENCE, false, true,
                                            true, "");
        
        }else{
            int intervals = rhythmicChords.getChords().size()-1;
            //System.out.println("number of intervals: "+(intervals));
            int log2OfIntervals = logBase2(intervals);
            //round up to nearest power of 2
            if(Math.pow(2, log2OfIntervals)<intervals){
                log2OfIntervals++;
            }
            //System.out.println("log base 2 of intervals: "+log2OfIntervals);
            ContourGenerator gen = new ContourGenerator(log2OfIntervals);
            generator = new GuideLineGenerator(
                                            rhythmicChords, direction, 
                                            startDegree, "", true, 
                                            Constants.A0, Constants.C8, 
                                            GuideLineGenerator.NOPREFERENCE, false, true,
                                            true, gen.contour());
        }
        
        MelodyPart noRestsGTL = generator.makeGuideLine();
        
        //add rests back in
        MelodyPart finalResult = new MelodyPart();
        
        ArrayList<Note> yesRests = response.getNoteList();
        ArrayList<Note> noRests = noRestsGTL.getNoteList();
        
        for(int y = 0, n = 0; n<noRests.size()||y<yesRests.size(); y++){
            boolean yListDone = y>=yesRests.size();
            //boolean nListDone = y>=noRests.size();
            if(yListDone || !yesRests.get(y).isRest()){
                finalResult.addNote(noRests.get(n).copy());
                n++;
            }else{
                finalResult.addNote(yesRests.get(y).copy());
            }
        }
        
        response = finalResult;
    }
    
    public static int logBase2(int n){
        return (int)(Math.log(n)/Math.log(2));
    }
    
    /**
     * Returns the opposite direction of the user solo
     * @return 
     */
    public int direction(){
        if(response.getFirstNote()==null){
            return GuideLineGenerator.NOPREFERENCE;
        }
        int first = response.getFirstNote().getPitch();
        int last = response.getLastNote().getPitch();
        int diff = last-first;
        if(diff>0){
            return GuideLineGenerator.DESCENDING;
        }else if(diff<0){
            return GuideLineGenerator.ASCENDING;
        }else{
            return GuideLineGenerator.NOPREFERENCE;
        }
    }
    
    public ChordPart rhythmicChords(){
        ChordPart rhythmic = new ChordPart();
        int duration;
        for(int i = 0; i<response.getSize(); i += duration){
            Note n = response.getNote(i);
            duration = n.getRhythmValue();
            Chord copy = responseChords.getCurrentChord(i).copy();
            copy.setRhythmValue(duration);
            //DON'T ADD THE CHORD IF THE NOTE IT GOES WITH IS A REST
            //THIS PREVENTS THE GTL FROM JUMPING AROUND A LOT
            //ADD RESTS BACK AT THE END
            if(!n.isRest()){
                rhythmic.addChord(copy);
            }
        }
        return rhythmic;
    }
    
    public int [] limits(int direction){
        int [] limits = new int[2];
        ArrayList <Note> notes = response.getNoteList();
        if(notes.isEmpty()){
            //default range limits
            limits[0] = Constants.C4;
            limits[1] = Constants.C5;
        }else{
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for(Note n : notes){
                int pitch = n.getPitch();
                if(!n.isRest()){
                    if(pitch<min){
                        min = pitch;
                    }
                    if(pitch>max){
                        max = pitch;
                    }
                }

            }
            //guide tone lines need a range of at least an octave
            //extend range in a certain direction based on direction of response
            int range = max-min;
            if(range<Constants.OCTAVE){
                switch(direction){
                    case GuideLineGenerator.ASCENDING:
                        max = min+Constants.OCTAVE;
                        break;
                    case GuideLineGenerator.DESCENDING:
                        min = max-Constants.OCTAVE;
                        break;
                    case GuideLineGenerator.NOPREFERENCE:
                        int extendBy = Constants.OCTAVE-range;
                        max += extendBy/2;
                        min += extendBy/2;
                        if(extendBy % 2 != 0){
                            max += 1;
                        }
                        break;
                }
                
            }
            limits[0] = min;
            limits[1] = max;
        }
        
        return limits;
    }

    //invert the solo
    public void invertSolo(){
        InvertCommand cmd = new InvertCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //reverse the solo
    public void reverseSolo(){
        ReverseCommand cmd = new ReverseCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //STEP 3 - transform/embellish the solo (in the style of a particular musician)
    
    //transform solo using specified transform
    //(in gui, select this from a drop down menu)
    public void transformSolo(Transform musician){
        response = musician.applySubstitutionsToMelodyPart(response, responseChords, true);
    }
    
    //STEP 4 - rectify the solo to chord/color tones
    
    //rectify solo to response chords
    //allows chord, color, and approach tones
    //allows repeat pitches
    public void rectifySolo(boolean onlyChordTones){
        RectifyPitchesCommand cmd;
        boolean chord, color, approach;
        chord = true;
        if(onlyChordTones){
            color = false;
            approach = false;
        }else{
            color = true;
            approach = true;
        }
        cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseChords, false, false, chord, color, approach);
        cmd.execute();
    }
    
    public void rectifySolo(){
        rectifySolo(false);
    }

    //STEP 5 - retreive the response
    
    //retreive response
    public MelodyPart getResponse(){
        return response;
    }
    
    //ALL THE STEPS TOGETHER
    public MelodyPart musicianResponse(Transform musician){

        //STEP 1
        flattenSolo();
        //STEP 2
        modifySolo();
        rectifySolo(ONLY_CHORD_TONES);
        //STEP 3
        transformSolo(musician);
        //STEP 4
        rectifySolo(ALL_TONES);
        //STEP 5
        return getResponse();
    }
    
    public MelodyPart response(Transform musician, String tradeMode){
        if (tradeMode.equals("Flatten")){
            flattenSolo();
        } else if (tradeMode.equals("Repeat and Rectify")) {
            rectifySolo();
        } else if (tradeMode.equals("Random Modify")){
            modifySolo();
            rectifySolo(ALL_TONES);
        } else if (tradeMode.equals("Flatten, Modify, Rectify")) {
            flattenSolo();
            modifySolo();
            rectifySolo();
        } else if (tradeMode.equals("Trade with a Musician")) {
            musicianResponse(musician);
            rectifySolo();
        } else if (tradeMode.equals("Abstract")) {
            abstractify();
        } else if(tradeMode.equals("Rhythmic Response")){
            rhythmicGuideLine(false);
        }else if(tradeMode.equals("Contour Test")){
            rhythmicGuideLine(true);
            
//            //learn from the rest of the choruses
//            int [][] counts = new int[IntervalLearner.intervals][IntervalLearner.intervals];
//            for(int [] row : counts){
//                for(int c = 0; c < row.length; c++){
//                    row[c] = 0;
//                }
//            }
//            //don't include current melodyPart
//            for(int i = 1; i < notate.getScore().size(); ++i){
//                MelodyPart learnFromThis = notate.getMelodyPart(notate.getStaveAtTab(i));
//                IntervalLearner learner = new IntervalLearner(learnFromThis);
//                int [][] specificCounts = learner.counts();
//                for(int row = 0; row < counts.length; row++){
//                    for(int c = 0; c < counts[row].length; c++){
//                        counts[row][c] += specificCounts[row][c];
//                    }
//                }
//            }
//            double [] [] probabilities = IntervalLearner.probabilities(counts);
//            
//            //RhythmGenerator rgen = new RhythmGenerator(response.size());
//            //MelodyPart rhythm = rgen.rhythm(Constants.EIGHTH);
//            int []range = new int[2];
//            range[0] = Constants.C4; range[1] = Constants.C5;
//            //use the response as the rhythm
//            MelodyGenerator mgen = new MelodyGenerator(probabilities, response, responseChords, range);
//            response = mgen.melody();
        } else if (tradeMode.equals("Zach 1 - Gen Solo")){
            response = grammarSolo.extract(nextSection, nextSection + stop, false, true);
        } else if (tradeMode.equals("Zach 2 - User Melody")){
            response = userMelody();
        } else if (tradeMode.equals("Zach 3 - User Rhythm")){
            response = userRhythm();
        } else if (tradeMode.equals("Zach 4 - Last Two")){
            response = swapMelodyRhythm();
        } else if (tradeMode.equals("Zach 5 - Store")){
            System.out.println("probe");
            response = lookupAndPlay();
        }
        
        
        
        else {
            //System.out.println("did nothing");
        }
        return getResponse();
    }
    
    
}
