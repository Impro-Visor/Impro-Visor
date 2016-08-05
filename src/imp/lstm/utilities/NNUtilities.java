/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.utilities;

import java.util.Random;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.SparseImmutableVector;

/**
 *
 * @author cssummer16
 */
public class NNUtilities {
    public static AVector onehot(int index, int length) {
        AVector result = SparseImmutableVector.create(length, Index.of(index), Vector.of(1.0));
        return result;
    }
    public static AVector roll(AVector input, int distance) {
        distance = distance % input.length();
        if (distance < 0)
            distance += input.length();
        AVector part1 = input.subVector(input.length() - distance, distance);
        AVector part2 = input.subVector(0, input.length() - distance);
        AVector concatenated = part1.join(part2).dense();
        return concatenated;
    }
    public static int sample(Random random, AVector input) {
        double sample_val = random.nextDouble();
        for(int i=0; i<input.length(); i++){
            double val = input.get(i);
            sample_val -= val;
            if(sample_val<0)
                return i;
        }
        return 0;
    }
}
