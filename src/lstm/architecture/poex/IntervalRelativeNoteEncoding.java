/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture.poex;

import lstm.filters.Operations;
import java.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ZeroVector;
import lstm.nickd4j.NNUtilities;

/**
 *
 * @author cssummer16
 */
public class IntervalRelativeNoteEncoding implements RelativeNoteEncoding {
    private int relpos;
    private Random random;
    private int low_bound;
    private int high_bound;
    
    public IntervalRelativeNoteEncoding(int low_bound, int high_bound){
        this.random = new Random();
        this.low_bound = low_bound;
        this.high_bound = high_bound;
    }
    
    @Override
    public AVector reset() {
        this.relpos = this.low_bound + this.random.nextInt(this.high_bound-this.low_bound);
        return NNUtilities.onehot(0, this.activation_width());
    }

    @Override
    public AVector encode(int midi_number, int chord_root) {
        if(midi_number == -1)
            return NNUtilities.onehot(0, this.activation_width());
        else if(midi_number == -2)
            return NNUtilities.onehot(1, this.activation_width());
        else {
            int delta = midi_number - this.relpos;
            if (delta > 12 || delta < -12)
                delta = delta % 12;
            this.relpos = midi_number;
            int index = delta + 12 + 2;
            return NNUtilities.onehot(index, this.activation_width());
        }
    }

    @Override
    public int activation_width() {
        return 1+1+(12+1+12);
    }

    @Override
    public AVector getProbabilities(AVector activations, int chord_root, int low_bound, int high_bound) {
        AVector probs = Operations.Softmax.operate(activations);
        AVector absolute_probs = probs.subVector(0, 2);
        AVector relative_probs = probs.subVector(2, activations.length()-2);
        
        int start_diff = low_bound - (this.relpos - 12);
        int startidx = Math.max(0, start_diff);
        int startpadding = Math.max(0, -start_diff);
        int endidx = Math.min(25, high_bound - (this.relpos - 12));
        int endpadding = Math.max(0, high_bound - (this.relpos + 12 + 1));
        
        AVector cropped = relative_probs.subVector(startidx, endidx-startidx);
        AVector padded = absolute_probs;
        if(startpadding>0)
            padded = padded.join(ZeroVector.create(startpadding));
        padded = padded.join(cropped);
        if(endpadding>0)
            padded = padded.join(ZeroVector.create(endpadding));
        padded = padded.mutable();
        padded.divide(padded.elementSum());
        return padded;
    }

    @Override
    public int get_relative_position(int chord_root) {
        return this.relpos;
    }
    
}
