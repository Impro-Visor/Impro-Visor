/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics;

import imp.generalCluster.IndexedMelodyPart;
import java.io.Serializable;
import polya.Polylist;

/**
 *
 * @author Lukas "No Money" Gnirke
 */
public abstract class Metric implements Serializable{
    protected Double value;
    protected double weight;
    protected String name;
    private boolean isLengthIndependent;
    
    public Metric(double weight, String name, boolean isLengthIndependent){
        this.weight = weight;
        if(name.contains(" ")){
            name = name.replace(" ","");
        }
        this.name = name;
        this.isLengthIndependent = isLengthIndependent;
    }
    
    public Metric(double value, float weight, String name, boolean isLengthIndependent){
        this.weight = weight;
        this.name = name;
        this.isLengthIndependent = isLengthIndependent;
        
    }
    
    public double getWeight(){
        return this.weight;
    }
    
    public boolean isLengthIndependent(){
        return this.isLengthIndependent;
    }
    
    public Double getValue(){
        return this.value;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setValue(double v){
        this.value = v;
    }
    
    /**@TODO: maybe don't avoid negative numbers, or do so more elegantly*/
    public void adjustValue(double f){
        double tmp = this.value + f;
        if(tmp < 0){
            tmp = 0;
        }
        this.value = tmp;
    }
    
    
    public void incrementValue(double f){
        double tmp;
        if (value!=null){
           tmp = this.value + f;
        }else{
            tmp =f;
        }
        
        this.value = tmp;
    }
    
    public void divideValue(double d){

        double tmp = this.value / d;

        this.value = tmp;
    }
    
    @Override
    public String toString(){
        return "("+name+": "+getValue()+")";
    }
    
    @Override
    public boolean equals(Object o) {
        if((o instanceof Metric) ){
            // If the names of the metrics are equal, return true
            return ((Metric) o).getName().equals( this.getName() );
        }
        
        return false;
    }
    
    
    
    public abstract double compute(String ruleString, IndexedMelodyPart exactMelody, Polylist rule);
    
    
}
