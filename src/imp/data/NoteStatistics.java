/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

/**
 *
 * @author cssummer17
 */
public class NoteStatistics {
    int color[];
    int redCount; //redCount gives the number of red notes on the leadsheet
    long redDuration; //redDuration gives the total duration length of the red notes on the leadsheet
    
    public NoteStatistics(int color[], int redCount, long redDuration)
    {
        this.color = color;
        this.redCount = redCount;
        this.redDuration = redDuration;
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
    
}
