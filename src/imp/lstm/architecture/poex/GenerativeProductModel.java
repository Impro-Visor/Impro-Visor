package imp.lstm.architecture.poex;

import imp.lstm.architecture.Loadable;
import imp.lstm.filters.Operations;
import imp.lstm.io.leadsheet.LeadSheetDataSequence;
import java.util.Random;
import imp.lstm.architecture.DataStep;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import imp.lstm.utilities.NNUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cssummer16
 */
public class GenerativeProductModel implements Loadable {
    private int low_bound;
    private int high_bound;
    private boolean normalizeArticOnly;
    private Expert[] experts;
    private RelativeInputPart[][] inputs;
    private RelativeNoteEncoding[] encodings;
    private PassthroughInputPart beat_part;
    private PassthroughInputPart last_output_parts[];
    private int num_experts;
    private Random rand;
    private double[] modifierExponents;
    private ProbabilityPostprocessor[] postprocessors;
    private String configuration;
    
    public GenerativeProductModel(int lowbound, int highbound) {
        this.low_bound = lowbound;
        this.high_bound = highbound;
        this.rand = new Random();
        
        this.experts = new Expert[3];
        this.experts[0] = new Expert(Operations.None);
        this.experts[1] = new Expert(Operations.None);
        this.experts[2] = new Expert(Operations.None);
        
        this.beat_part = new PassthroughInputPart();
        this.last_output_parts = new PassthroughInputPart[3];
        this.last_output_parts[0] = new PassthroughInputPart();
        this.last_output_parts[1] = new PassthroughInputPart();
        this.last_output_parts[2] = new PassthroughInputPart();
        
        this.inputs = new RelativeInputPart[3][4];
        this.inputs[0][0] = this.beat_part;
        this.inputs[0][1] = new PositionInputPart(lowbound, highbound, 2);
        this.inputs[0][2] = new ChordInputPart();
        this.inputs[0][3] = this.last_output_parts[0];
        this.inputs[1][0] = this.beat_part;
        this.inputs[1][1] = new PositionInputPart(lowbound, highbound, 2);
        this.inputs[1][2] = new ChordInputPart();
        this.inputs[1][3] = this.last_output_parts[1];
        this.inputs[2][0] = this.beat_part;
        this.inputs[2][1] = new PositionInputPart(lowbound, highbound, 2);
        this.inputs[2][2] = new ChordInputPart();
        this.inputs[2][3] = this.last_output_parts[2];
        
        this.modifierExponents = new double[]{1.0, 1.0, 1.0};
        
        this.modifierExponents = new double[]{1.0, 1.0};
        this.postprocessors = new ProbabilityPostprocessor[0];
        
        configure(null);
    }
    
    @Override
    public boolean configure(String configInfo){
        if(configInfo == null)
            configInfo = "generative_product_interval_chords";
                
        if(configInfo.equals(configuration))
            return true;
        
        this.postprocessors = new ProbabilityPostprocessor[0];
        switch(configInfo){
            case "generative_product_interval_chords":
                this.num_experts = 2;
                this.encodings = new RelativeNoteEncoding[2];
                this.encodings[0] = new IntervalRelativeNoteEncoding(this.low_bound, this.high_bound, true);
                this.encodings[1] = new ChordRelativeNoteEncoding(true);
                this.normalizeArticOnly = false;
                break;
            case "generative_product_interval_chords_rhythm":
                this.num_experts = 3;
                this.encodings = new RelativeNoteEncoding[3];
                this.encodings[0] = new IntervalRelativeNoteEncoding(this.low_bound, this.high_bound, false);
                this.encodings[1] = new ChordRelativeNoteEncoding(false);
                this.encodings[2] = new RhythmOnlyNoteEncoding();
                this.normalizeArticOnly = true;
                break;
            default:
                return false;
        }
        
        configuration = configInfo;
        reset();
        return true;
    }
    
    public double[] getModifierExponents(){
        return this.modifierExponents;
    }
    
    public ProbabilityPostprocessor[] getProbabilityPostprocessors(){
        return postprocessors;
    }
    public void setProbabilityPostprocessors(ProbabilityPostprocessor[] p){
        postprocessors = p;
    }
    
    public void reset(){
        for (int i = 0; i < this.num_experts; i++) {
            this.last_output_parts[i].provide(this.encodings[i].reset());
        }
    }
    
    @Override
    public boolean load(INDArray data, String loadPath) {
        // Expected format: #_<expert params>
        // i.e. enc_1_full_w
        String car = pathCar(loadPath);
        String cdr = pathCdr(loadPath);
        try {
            int expertIdx = Integer.parseInt(car);
            if(expertIdx >= 0 && expertIdx < num_experts)
                return this.experts[expertIdx].load(data, cdr);
            else
                return false;
        } catch(NumberFormatException e){
            return false;
        }
    }
    
    public AVector step(DataStep currStep) {
        AVector beat = currStep.get("beat");
        AVector raw_chord = currStep.get("chord");
        int chord_root = (int) raw_chord.get(0);
        AVector chord_type = raw_chord.subVector(1, 12);
        this.beat_part.provide(beat,this.num_experts);
        
        AVector accum_probabilities = null;
        for(int i=0; i<this.num_experts; i++) {
            RelativeNoteEncoding enc = this.encodings[i];
            int relpos = enc.get_relative_position(chord_root);
            
            AVector full_decoder_input = RelativeInputPart.combine(this.inputs[i], relpos, chord_root, chord_type);
            AVector activations = this.experts[i].process(full_decoder_input);
            AVector probabilities = enc.getProbabilities(activations, chord_root, this.low_bound, this.high_bound).copy().mutable();
            
            AVector articSlice = probabilities.subVector(2, probabilities.length()-2);
            double initArticSum = articSlice.elementSum();
            articSlice.pow(modifierExponents[i]);
            double finalArticSum = articSlice.elementSum();
            articSlice.multiply(initArticSum/finalArticSum);
            
            if(accum_probabilities == null)
                accum_probabilities = probabilities;
            else
                accum_probabilities.multiply(probabilities);
        }
        
        for(ProbabilityPostprocessor p : postprocessors)
            accum_probabilities = p.postprocess(accum_probabilities);
        
        if(normalizeArticOnly){
            AVector nonArticNotes = accum_probabilities.subVector(0,2);
            AVector articNotes = accum_probabilities.subVector(2,accum_probabilities.length()-2);
            articNotes.divide(articNotes.elementSum());
            articNotes.multiply(1-nonArticNotes.elementSum());
        }else{
            accum_probabilities.divide(accum_probabilities.elementSum());
        }
        int sampled = NNUtilities.sample(this.rand, accum_probabilities);
        int midival;
        if(sampled == 0)
            midival = -1;
        else if(sampled == 1)
            midival = -2;
        else
            midival = this.low_bound + (sampled-2);
        
        for(int i=0; i<this.num_experts; i++) {
            RelativeNoteEncoding enc = this.encodings[i];
            AVector prev_output = enc.encode(midival, chord_root);
            this.last_output_parts[i].provide(prev_output);
        }
        return Vector.of(midival);
    }
}
