/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.generalCluster.DataPoint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import polya.Polylist;

/**
 *
 * @author Lukas Gnirke & Cai Glencross
 */
public class RhythmSelecterEntry {
    private boolean isSelected;
    private JLabel rhythmRep;
    private boolean added;
    private boolean session;
    private boolean delete;
    private Polylist ruleStringPL;
    private ImageIcon rhythmRepresentation;
    private boolean hide;
    private String realMelody;
    private boolean alreadyReturned;
    private DataPoint dataPoint;
    
    
    
   
    public RhythmSelecterEntry(ImageIcon rhythmRep, boolean session, String realMelody, Polylist ruleStringPL){
        //this.rhythmRep = rhythmRep;
        this.rhythmRepresentation = rhythmRep;
        //this.button = button;
        this.added = false;
        this.isSelected = false;
        this.session = session;
        this.delete = false;
        this.ruleStringPL = ruleStringPL;
        this.hide = false;
        this.realMelody = realMelody;
        this.dataPoint = null;
        
    }
    
    public ImageIcon getRhythmRepresentation(){
        return this.rhythmRepresentation;
    }
    
    public String getRealMelody(){
        return this.realMelody;
    }

    
    public boolean isSession(){
        return this.session;
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
    public void setDelete(boolean d){
        this.delete = true;
    }
    
    public Polylist getRuleStringPL(){
        return this.ruleStringPL;
    }

    public void setAdded(boolean b) {
        this.added = b;
    }

    public void setChecked(boolean b) {
        this.isSelected = false;
    }
    
    public boolean isAdded(){
        return this.added;
    }

    public boolean isDeleted() {
        return this.delete;
    }
    
    public void hide(){
        System.out.println("rse hidden....");
        this.hide = true;
    }
    
    public boolean isHidden(){
        return this.hide;
    }
    
    public void markNotReturned(){
        this.alreadyReturned = false;
    }
    
    public void markAsReturned(){
        this.alreadyReturned = true;
    }
    
    public boolean hasBeenReturned(){
        return this.alreadyReturned;
    }

    public void addDataPoint(DataPoint d) {
        this.dataPoint = d;
    }
    
    public DataPoint getDataPoint(){
        if(this.dataPoint == null){
            System.out.println("This rhythmSelecterEntry doesn't have a dataPoint assigned to it!");
        }
        return this.dataPoint;   
    }

    
}
