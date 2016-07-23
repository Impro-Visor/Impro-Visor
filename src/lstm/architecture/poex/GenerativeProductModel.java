package lstm.architecture.poex;

import lstm.architecture.Loadable;
import lstm.filters.Operations;
import lstm.io.leadsheet.LeadSheetDataSequence;
import java.util.Random;
import lstm.architecture.DataStep;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import lstm.nickd4j.NNUtilities;

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
    private int featureVectorSize;
    private int low_bound;
    private int high_bound;
    private Expert[] experts;
    private RelativeInputPart[][] inputs;
    private RelativeNoteEncoding[] encodings;
    private PassthroughInputPart beat_part;
    private PassthroughInputPart last_output_parts[];
    private int num_experts;
    private Random rand;
    private double[] modifierExponents;
    
    public GenerativeProductModel(int outputSize, int beatVectorSize, int featureVectorSize, int lowbound, int highbound) {
        this.featureVectorSize = featureVectorSize;
        this.low_bound = lowbound;
        this.high_bound = highbound;
        this.rand = new Random();
        this.num_experts = 2;
        
        this.experts = new Expert[2];
        this.experts[0] = new Expert(Operations.None);
        this.experts[1] = new Expert(Operations.None);
        
        this.beat_part = new PassthroughInputPart(beatVectorSize);
        this.last_output_parts = new PassthroughInputPart[2];
        this.last_output_parts[0] = new PassthroughInputPart(outputSize);
        this.last_output_parts[1] = new PassthroughInputPart(outputSize);
        
        this.inputs = new RelativeInputPart[2][4];
        this.inputs[0][0] = this.beat_part;
        this.inputs[0][1] = new PositionInputPart(lowbound, highbound, 2);
        this.inputs[0][2] = new ChordInputPart();
        this.inputs[0][3] = this.last_output_parts[0];
        this.inputs[1][0] = this.beat_part;
        this.inputs[1][1] = new PositionInputPart(lowbound, highbound, 2);
        this.inputs[1][2] = new ChordInputPart();
        this.inputs[1][3] = this.last_output_parts[1];
        
        this.encodings = new RelativeNoteEncoding[2];
        this.encodings[0] = new IntervalRelativeNoteEncoding(lowbound, highbound);
        this.encodings[1] = new ChordRelativeNoteEncoding();
        
        this.modifierExponents = new double[]{1.0, 1.0};
        
        reset();
    }
    
    public double[] getModifierExponents(){
        return this.modifierExponents;
    }
    
    public void reset(){
        for (int i = 0; i < this.num_experts; i++) {
            this.last_output_parts[i].provide(this.encodings[i].reset());
        }
    }
    
    @Override
    public boolean load(INDArray data, String loadPath) {
        // Expected format: (enc|dec)_#_<expert params>
        // i.e. enc_1_full_w
        String car = pathCar(loadPath);
        String cdr = pathCdr(loadPath);
        int expert_idx = Integer.parseInt(car);
        return this.experts[expert_idx].load(data, cdr);
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
            AVector probabilities = enc.getProbabilities(activations, chord_root, this.low_bound, this.high_bound).copy();
            probabilities.pow(modifierExponents[i]);
            
            if(accum_probabilities == null)
                accum_probabilities = probabilities.mutable();
            else
                accum_probabilities.multiply(probabilities);
        }
        
        accum_probabilities.divide(accum_probabilities.elementSum());
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
