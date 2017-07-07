/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * author: Cai "Get Money" Glencross
 */
package imp.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author cssummer17
 */
public class ContourData implements Serializable{
    private int numSlopeChanges;
    private ArrayList<Short> slopeTypes;
    
    //shorts for types of slope changes
    public static final short FLAT = 0;
    public static final short UP = 1;
    public static final short DOWN = 2;
    
    public ContourData(ArrayList<Short> slopeTypes, int numSlopeChanges){
        this.slopeTypes=slopeTypes;
        this.numSlopeChanges = numSlopeChanges;
    }
    
    public int getNumContourChanges(){
        return numSlopeChanges;
    }
    
    public ArrayList<Short> getContourArray(){
        return slopeTypes;
    }
    
}
