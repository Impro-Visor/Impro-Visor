/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster;

import java.io.Serializable;

/**
 *
 * @author Lukas "No Money" Gnirke
 */
public class Metric implements Serializable{
    private float value;
    private float weight;
    private String name;
    boolean isLengthIndependent;
    
    public Metric(float value, float weight, String name, boolean isLengthIndependent){
        this.value = value;
        this.weight = weight;
        this.name = name;
        this.isLengthIndependent = isLengthIndependent;
    }
    
    public float getWeight(){
        return this.weight;
    }
    
    public boolean isLengthIndependent(){
        return this.isLengthIndependent;
    }
    
    public float getValue(){
        return this.value;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setValue(float v){
        this.value = v;
    }
    
    public void adjustValue(float f){
        float tmp = this.value + f;
        if(tmp < 0){
            tmp = 0;
        }
        this.value = tmp;
    }

    
    @Override
    public String toString(){
        return "("+name+": "+value+")";
    }
    
    @Override
    public boolean equals(Object o) {
        if((o instanceof Metric) ){
            // If the names of the metrics are equal, return true
            return ((Metric) o).getName().equals( this.getName() );
        }
        
        return false;
    }
    
    
}
