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

package imp.lstm.main;

import mikera.vectorz.*;
import mikera.matrixx.*;

/**
 *
 * @author cssummer16
 */
public class VectorzTest {
    public static void main(String[] args)
    {
        
        AMatrix m1 = Matrixx.create(new double[][]{new double[]{1.0, 2.0}, new double[]{0.3, 1.0}});
        AVector v = Vector.of(0.1, 0.9);

        System.out.println("m: \n" + m1 + "\n");
        System.out.println("v: \n" + v + "\n");
        
        //use inner product for any matrix multiplication, no need to transpose vectors :)
        AVector v1 = m1.innerProduct(v);
        System.out.println("(m * v): \n" + v1 + "\n");
        
        //some operations, such as tanh, are in-place. There is no sigmoid or softmax, but they can be implemented
        v1.tanh();
        System.out.println("(m * v).tanh(): \n" + v1 + "\n");
        
        //lets try making softmax
        AVector v2 = Vector.of(1.0, 3.0, 4.0, 2.0, 1.0);
        AVector softMaxxedV2 = v2.clone();
        softMaxxedV2.exp();
        softMaxxedV2.divide(v2.elementSum());
        System.out.println("Soft-maxed (m * v): \n" + softMaxxedV2);
        System.out.println("Soft-maxed (m * v) sum: \n" + softMaxxedV2.elementSum());
        
        //v2.divide(0);
        
        v = v.getTransposeCopy();
        System.out.println("v transposed: \n" + v);
        
        AMatrix m2 = Matrixx.create(new double[][]{new double[]{3.0, 4.0, 1.0, 2.0}, new double[]{2.5, 2.5, 5.0, 1.3}});
        AMatrix m3 = Matrixx.create(new double[][]{new double[]{1.0, 2.0}, new double[]{1.0, 2.0}, new double[]{3.0, 4.0}, new double[]{4.0, 5.0}});
        AMatrix m4 = m2.innerProduct(m3);
        System.out.println("mmul1 result: \n" + m4 + "\n");
        AMatrix m5 = m3.innerProduct(m2);
        System.out.println("mmul2 result: \n" + m5 + "\n");
        
        AVector v3 = Vector.of(1.0, 1.0, 2.0, 0.0);
        System.out.println(v3.get(0));
        v3.set(0, 2.0);
        v3.set(3, 5.0);
        System.out.println(v3);
    }
}
