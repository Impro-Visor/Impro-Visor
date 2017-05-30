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

package imp.voicing;

/**
 *
 * This class can be used to calculate the distances between voicings for analytical purposes.
 * @author Daniel Scanteianu
 */
public class VoicingDistanceCalculator {
    public static int calculateDistance(int[] voicing1, int[] voicing2)
    {
        int sum1=0,sum2=0;
        for(int i:voicing1)
        {
            double currentMinimum=100;//can't really have much more than that as distance between two notes
            for(int j: voicing2)
                if(Math.abs(i-j)<currentMinimum)
                    currentMinimum=Math.abs(i-j);
            sum1+=currentMinimum;
        }
         for(int i:voicing2)
        {
            double currentMinimum=100;//can't really have much more than that as distance between two notes
            for(int j: voicing1)
                if(Math.abs(i-j)<currentMinimum)
                    currentMinimum=Math.abs(i-j);
            sum2+=currentMinimum;
        }
         if(sum1<sum2)
             return sum2;
        return sum1;
    }
    public static int calculateNotesChanged(int[] voicing1, int[] voicing2)
    {
        int sumChanged=0;
         for(int i:voicing1)
        {
            
            int changed=1;
            for(int j: voicing2)
                if(i-j==0)
                    changed=0;
            sumChanged+=changed;
        }
        return sumChanged;
    }
}
