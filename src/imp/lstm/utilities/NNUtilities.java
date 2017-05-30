/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
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
