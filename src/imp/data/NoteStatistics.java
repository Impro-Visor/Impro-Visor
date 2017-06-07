/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See thesetS
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.data;

/**
 *
 * @author Robert Keller and Samantha Long
 */
public class NoteStatistics {
    int color[];
    int redCount; //redCount gives the number of red notes on the leadsheet
    long redDuration; //redDuration gives the total duration length of the red notes on the leadsheet
    float redDurationPercent; //redDurationPercent gives the percentage of the duration of red notes over the total duration
    
    public NoteStatistics(int color[], int redCount, long redDuration, float redDurationPercent)
    {
        this.color = color;
        this.redCount = redCount;
        this.redDuration = redDuration;
        this.redDurationPercent = redDurationPercent;        
    }
    
    public int [] getColor()
    {
        return color;
    }
    
    public int getredCount()
    {
        return redCount;
    }
    
    public long getredDuration()
    {
        return redDuration;
    }
    
    public float getredDurationPercent()
    {
        return redDurationPercent;
    }
}
