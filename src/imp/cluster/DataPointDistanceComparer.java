/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.cluster;

import java.util.Comparator;
import imp.cluster.Cluster;
import imp.cluster.DataPoint;
import imp.cluster.IndexedMelodyPart;
import imp.cluster.JCA;
import imp.cluster.ClusterSet;

/**
 *
 * @author Jon Gillick
 */

public class DataPointDistanceComparer implements Comparator {
    
    //sorts points by distance from Average Point
    public int compare(Object a, Object b) {
        
        DataPoint point1 = (DataPoint)a;
        DataPoint point2 = (DataPoint)b;
        
        double distance1 = CreateGrammar.getAveragePoint().calcEuclideanDistance(point1);
        double distance2 = CreateGrammar.getAveragePoint().calcEuclideanDistance(point2);
        
        return (int) (distance1 - distance2);
        
    }
    
}