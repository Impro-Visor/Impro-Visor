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

/**
* This class describes a fuzzy number with a triangular memebership function.
 * Fuzzy numbers can be added to a fuzzy set. The number has three main
 * properties, peak, minimum, and maximum, which describe the apex, 
 * left corner and right corner of the triangle. The triangle is normalised in
 * the vertical direction, so membership values will be between 0.0 and 1.0.
 * The degree of membership is computed by the getMembership method.
 *
 * @author Andrew Brown, October 2003
 */
public class FuzzyNumber {
    private double peak, min, max, diff, membership;
    
    /**
    * Create a new fuzzy number.
     * @param peak - The center value for the triangular membership function.
     * @param min - The leftmost or minimum value for the triangular membership function.
     * @param max - The rightmost or maximum value for the triangular membership function.
     */
    public FuzzyNumber(double peak, double min, double max) {
        this.peak = peak;
        this.min = min;
        this.max = max;
    }
    
    /**
     * Calculate the degree of membership of a value within this fuzzy number.
     * @param value - The number to be tested for its membership.
     * @return The degree of membership within this fuzzy number.
     */
    public double getMembership(double value) {
        if (value < min || value > max) return 0.0;
        diff = peak - value;
        if(diff >= 0.0) membership = 1.0 - (diff / (peak - min));
        else membership = 1.0 + (diff / (max - peak));
        return membership;
    }
    
    /**
    * Specify a value for the centre of the triangular membership function.
     * @param newValue - The number to set the peak to.
     */
    public void setPeak(double newValue) {
        this.peak = newValue;
        if(min > newValue) min = newValue;
        if(max < newValue) max = newValue;
    }
    
    /**
    * Return the current value for the centre of the triangular membership function.
    * @return peak - The number currently set as the triangle's peak.
    */
    public double getPeak() {
        return this.peak;
    }
    
    /**
    * Specify a value for the left corner of the triangular membership function.
     * @param newValue - The number to set the minimum to.
     */
    public void setMin(double newValue) {
        this.min = newValue;
        if(peak < newValue) peak = newValue;
        if(max < newValue) max = newValue;
    }
    
    /**
    * Return the current value for the left corner of the triangular membership function.
     * @return min - The number currently set as the triangle's left corner.
     */
    public double getMin() {
        return this.min;
    }
    
    /**
    * Specify a value for the right corner of the triangular membership function.
     * @param newValue - The number to set the peak to.
     */
    public void setMax(double newValue) {
        this.max = newValue;
        if(min > newValue) min = newValue;
        if(peak > newValue) peak = newValue;
    }
    
    /**
    * Return the current value for the right corner of the triangular membership function.
     * @return max - The number currently set as the triangle's right corner.
     */
    public double getMax() {
        return this.max;
    }    
}