/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture.poex;

import imp.lstm.filters.Operations;
import java.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ZeroVector;
import imp.lstm.utilities.NNUtilities;
import mikera.vectorz.impl.Vector0;

/**
 *
 * @author cssummer16
 */
public class IntervalRelativeNoteEncoding implements RelativeNoteEncoding {
    private int relpos;
    private Random random;
    private int low_bound;
    private int high_bound;
    boolean withArtic;
    
    public IntervalRelativeNoteEncoding(int low_bound, int high_bound, boolean withArtic){
        this.random = new Random();
        this.low_bound = low_bound;
        this.high_bound = high_bound;
        this.withArtic = withArtic;
    }
    
    @Override
    public AVector reset() {
        this.relpos = this.low_bound + this.random.nextInt(this.high_bound-this.low_bound);
        return NNUtilities.onehot(0, this.activation_width(true));
    }

    @Override
    public AVector encode(int midi_number, int chord_root) {
        if(midi_number == -1)
            return NNUtilities.onehot(0, this.activation_width(true));
        else if(midi_number == -2)
            return NNUtilities.onehot(1, this.activation_width(true));
        else {
            int delta = midi_number - this.relpos;
            if (delta > 12 || delta < -12)
                delta = delta % 12;
            this.relpos = midi_number;
            int index = delta + 12 + 2;
            return NNUtilities.onehot(index, this.activation_width(true));
        }
    }

    @Override
    public int activation_width() {
        return activation_width(withArtic);
    }
    
    public int activation_width(boolean includingArticBits) {
        if(includingArticBits)
            return 1+1+(12+1+12);
        else
            return (12+1+12);
    }

    @Override
    public AVector getProbabilities(AVector activations, int chord_root, int low_bound, int high_bound) {
        AVector probs = Operations.Softmax.operate(activations);
        AVector absolute_probs,relative_probs;
        if(withArtic) {
            absolute_probs = probs.subVector(0, 2);
            relative_probs = probs.subVector(2, activations.length()-2);
        } else {
            absolute_probs = Vector0.INSTANCE;
            relative_probs = activations;
        }
        
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
        if(!withArtic) {
            padded = Vector.of(1,1).join(padded);
        }
        return padded;
    }

    @Override
    public int get_relative_position(int chord_root) {
        return this.relpos;
    }
    
}
