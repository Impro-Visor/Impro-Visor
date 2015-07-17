/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.com.InvertCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ReverseCommand;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.TransformLearning;
import java.util.Random;

/**
 *
 * @author muddCS15
 */
public class ResponseGenerator {
    
    private MelodyPart response;
    private static final int start = 0;
    private int stop;
    private ChordPart responseChords;
    private final BeatFinder beatFinder;
    private final TransformLearning flattener;
    
    public ResponseGenerator(MelodyPart response, ChordPart responseChords, int [] metre){
        this.response = response;
        this.stop = response.size()-1;
        this.responseChords = responseChords;
        this.beatFinder = new BeatFinder(metre);
        this.flattener = new TransformLearning();
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
        response = flattener.flattenByChord(response, responseChords, resolution, start, stop, false);
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
    public void rectifySolo(){
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseChords, false, false);
        cmd.execute();
    }
    
    //STEP 5 - retreive the response
    
    //retreive response
    public MelodyPart getResponse(){
        return response;
    }
    
    //ALL THE STEPS TOGETHER
    public MelodyPart getResponse(MelodyPart response, ChordPart responseChords, Transform musician){
        //STEP 0
        setResponse(response);
        setChords(responseChords);
        //STEP 1
        flattenSolo();
        //STEP 2
        modifySolo();
        //STEP 3
        transformSolo(musician);
        //STEP 4
        rectifySolo();
        //STEP 5
        return getResponse();
    }
    
    
}
