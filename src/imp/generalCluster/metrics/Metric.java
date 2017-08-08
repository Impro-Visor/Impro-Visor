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
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
