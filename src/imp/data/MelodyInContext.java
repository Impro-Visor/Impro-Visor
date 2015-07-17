/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.gui.Stave;

/**
 *
 * @author muddCS15
 */
/**
* Defines a selection in a stave from a melody with start and end slots.
*/
public class MelodyInContext {
    private MelodyPart melody;
    private Stave stave;
    private int start;
    private int stop;

    public MelodyInContext(MelodyPart melody,Stave stave, int start, int stop)
    {
        this.melody = melody;
        this.stave = stave;
        this.start = start;
        this.stop = stop;
    }

    public MelodyPart getMelody()
    {
        return this.melody.copy();
    }

    public Stave getStave()
    {
        return this.stave;
    }

    public int getStart()
    {
        return this.start;
    }

    public int getStop()
    {
        return this.stop;
    }
    
}
