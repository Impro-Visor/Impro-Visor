/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.io.leadsheet;

import imp.data.Chord;

/**
 *
 * @author cssummer16
 */
public class ChordInfo {
    private int duration;
    private String root;
    private String type;
    private String bass;
    
    public ChordInfo(int duration, String root, String type, String bass) {
        this.duration = duration;
        this.root = root;
        this.type = type;
        this.bass = (bass == null) ? root : bass;
    }
    
    public ChordInfo(int duration, String root, String type) {
        this(duration, root, type, null);
    }

    ChordInfo(Chord chord) {
        this.duration = chord.getRhythmValue() / Constants.RESOLUTION_SCALAR;
        this.root = chord.getRoot().toUpperCase();
        this.bass = chord.getChordSymbol().getBass().toString().toUpperCase();
        this.type = chord.getQuality();
    }
    
    public int getDuration()
    {
        return duration;
    }
    
    public void setDuration(int duration)
    {
        this.duration = duration;
    }
    
    public String getRoot()
    {
        return root;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getBass()
    {
        return bass;
    }
}
