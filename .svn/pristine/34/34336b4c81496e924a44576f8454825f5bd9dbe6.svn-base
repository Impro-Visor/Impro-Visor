/*
 <This Java Class is part of the jMusic API version 1.5, March 2004.>
 
 Copyright (C) 2000 Andrew Sorensen & Andrew Brown
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or any
 later version.
 
 This program is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 
 */

package jm.music.tools.fuzzy;

import java.util.*;

/**
* This class describes a fuzzy set that contains one or more fuzzy numbers.
* FuzzyNumber objects can be added to the set with the add method. Defuzzification
* output uses the centriod scheme and is provided by the getOutput method.
*
* @author Andrew Brown, October 2003
*/
public class FuzzySet {
    private Vector numberList;
    private double productSum, memberSum;
    
    /**
    * Constructs an empty fuzzy set.
    */
    public FuzzySet() {
        numberList = new Vector();
    }
    
    /**
    * Add a fuzzy number object to the set.
    * @fNumb - The fuzzy number to be added to this set.
    */
    public void add(FuzzyNumber fNumb) {
        numberList.addElement(fNumb);
    }
    
    /**
    * Remove a fuzzy number object from the set.
    * @fNumb - The fuzzy number to be removed from this set.
    */
    public void remove(FuzzyNumber fNumb) {
        numberList.removeElement(fNumb);
    }
    
    /**
    * Computes the real-valued fuzzy centroid (centre of gravity) as a
    * non-normalised average of the degree of membership in each fuzzy number.
    * @param input - The number for which the fuzzy rules are to be applied.
    */
    public double getOutput(double input) {
        productSum = 0.0;
        memberSum = 0.0;
        //sum the product of the fuzzy number peak and membership
        for (Enumeration e = numberList.elements() ; e.hasMoreElements() ;) {
            FuzzyNumber fn = (FuzzyNumber)e.nextElement();
            productSum += fn.getPeak() * fn.getMembership(input);
            memberSum += fn.getMembership(input);
        }
        // divide by the sum of the memberships
        return productSum / memberSum;
    }
}
    