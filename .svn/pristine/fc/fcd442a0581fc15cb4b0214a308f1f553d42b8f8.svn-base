/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.cluster;

import java.io.Serializable;

/**
 *
 * @author Jon Gillick
 */
public class NGram implements Serializable{

    //The probability of an n-gram is the conditional probability of the
    //n states in the n-gram on the first n-1 states
    //so the probability is the number of appearances / the number of appearances
    //of the first n-1 states of the n-gram - so appearances/appearancesUptoLast
        
    private int[] chain;
    private int appearances;
    private int appearancesUpToLast;
    private float probability;    
    private float currentProb;
    private boolean ender;
    
    public NGram(int[] order) {
        chain = (int[])order.clone();
        appearances = 1;
        appearancesUpToLast = 1;
    }
    
    public NGram(int[] order, boolean last) {
        chain = (int[])order.clone();
        appearances = 1;
        appearancesUpToLast = 1;
        ender = last;
    }
    
    public NGram(int[] order, int numAppearances) {
        chain = (int[])order.clone();
        appearances = numAppearances;
        appearancesUpToLast = numAppearances;
    }
    
    public NGram(int[] order, int numAppearances, int numUpToLast) {
        chain = (int[])order.clone();
        appearances = numAppearances;
        appearancesUpToLast = numUpToLast;
    }
    
    public void setNumAppearances(int numAppearances) {
        appearances = numAppearances;
    }
    
    public void addAppearance() {
        appearances++;
    }
    
    public void setAppearancesUpToLast(int numUpToLast) {
        appearancesUpToLast = numUpToLast;
    }
    
    public void setProbability() {
        float x = (float)appearances;
        float y = (float)appearancesUpToLast;
        probability = x / y;
    }
    
    //returns whether the last datapoint of this ngram ends a chorus
    public boolean isEnder() {
        return ender;
    }
    
    public int[] getChain() {
        return chain;
    }
    
    public float getProbability() {
        return probability;
    }
    
    public float getCurrentProb() {
        return currentProb;
    }
    
    public void setCurrentProb(float p) {
        currentProb = p;
    }
    
    public int getNumAppearances() {
        return appearances;
    }
    
    public int[] getChainUpToLast() {
        int[] chainUpToLast = new int[chain.length-1];
        for(int i = 0; i < chainUpToLast.length; i++) {
            chainUpToLast[i] = chain[i];
        }
        return chainUpToLast;
    }
    
    public boolean equals(NGram other) {
        if(chain.length != other.chain.length)
            return false;
        
        boolean b = true;
        for(int i = 0; i < chain.length && b == true; i++) {
            if(chain[i] != other.chain[i]) b = false;
        }
        return b;
    }
    
    public boolean equalsUpToLast(NGram other) {
        if(chain.length != other.chain.length)
            return false;
        boolean b = true;
        for(int i = 0; i < chain.length-1 && b == true; i++) {
            if(chain[i] != other.chain[i]) b = false;
        }
        return b;      
    }
    
    public int getFirst() {
        return chain[0];
    }
    
    public int getLast() {
        return chain[chain.length-1];
    }
    
    @Override
    public String toString() {
        String outString = "Chain: ";
        for(int i = 0; i < chain.length; i++) {
            outString = outString.concat(Integer.toString(chain[i]));
            if(i < chain.length - 1) outString = outString.concat(",");
        }
        outString = outString.concat(" Number: " + appearances);
        outString = outString.concat(" Num Previous: " + appearancesUpToLast);
        outString = outString.concat(" Probability: " + probability);
        return outString;
    }
}
