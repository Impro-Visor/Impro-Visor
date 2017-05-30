/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
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

package imp.gui;

/**
 * For use with the complexity graph panel
 * @author Julia Botev
 */
public class BarDimensions {
    private int barStart, upperBound, lowerBound;

    public BarDimensions(int b, int t, int l) {
        barStart = b;
        upperBound = t;
        lowerBound = l;
    }

    public void setBarStart(int b) {
        barStart = b;
    }
    public void setUpperBound(int t) {
        upperBound = t;
    }

    public void setLowerBound(int l) {
        lowerBound = l;
    }

    public int getBarStart() {
        return barStart;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }
}
