/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
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
package imp.cluster.motif;

import java.util.Arrays;

/**
 *
 * @author Joseph Yaconelli
 */
public class MotifClusterKey {
    
    private final double[] _params;
    private static double TOLERANCE = 0.5;
    
    public MotifClusterKey(double[] params, double tolerance){
        _params = params;
        TOLERANCE = tolerance;
    }
    
    
    /**
     * Calculates the distance between this MotifClusterKey and another MotifClusterKey
     * @param other the MotifClusterKey to calculate distance to.
     * @return The log Euclidean distance between two MotifClusterKeys.
     */
    public double calcDistance(MotifClusterKey other){
        double distance = 0;
        
        int length = Math.min(_params.length, other.getParams().length);
        
        for(int i = 0; i < length; i++){
            
            distance += Math.pow(_params[i] - other.getParams()[i], 2);
            
        }
        
        distance = Math.pow(distance, 0.5);
        
        return Math.log(distance);
        
    }
    
    /**
     * Returns the array of parameters
     * @return 
     */
    public double[] getParams(){
        return _params;
    }
    
    /**
     * Uses {@link calcDistance} and returns true if distance is less than {@link TOLERANCE}
     * @param obj the MotifClusterKey to compare to
     * @return true is distance is less than TOLERANCE, otherwise false.
     */
    @Override
    public boolean equals(Object obj){
        
        if(obj == null){
            return false;
        }
        
        if (!MotifClusterKey.class.isAssignableFrom(obj.getClass()))
            return false;
        
        return this.calcDistance((MotifClusterKey) obj) <= TOLERANCE;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.hashCode(this._params);
        return hash;
    }
    
    
}
