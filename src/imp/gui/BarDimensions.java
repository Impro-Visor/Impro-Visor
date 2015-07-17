/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
