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

import java.io.Serializable;
import java.util.*;


public class ClusterSet implements Serializable {

    private final int numberOfRelatedPoints = 20;
    
    private Cluster original;
    private Vector<Cluster> relatives = new Vector<Cluster>();
    Cluster[] allClusters;
    private Vector<DataPoint> points = new Vector<DataPoint> ();
    
    public ClusterSet(Cluster[] all, Cluster orig) {
        original = orig;
        allClusters = all;
        calcRelatives();
        setDataPoints();
        
    }
    
    public Vector<Cluster> getSimilarClusters() {
        return relatives;
    }

    private void setDataPoints() {
        //add data points in original cluster
        points.addAll(original.getDataPoints());
        //add all data points in relatives
        for (int i = 0; i < relatives.size(); i++) {
            Cluster c = relatives.get(i);
            points.addAll(c.getDataPoints());
        }
    }
    
    private void calcRelatives() {
        Vector<double[]> similarities = getSimilarities();
        Collections.sort((List) similarities, new ClusterSimilarityComparer());
        int numRelatives = 6;
        for(int i = 0; i < numRelatives && i < similarities.size(); i++) {
            if (this.getNumPointsInRelatives() >= numberOfRelatedPoints) break;
            int index = (int) similarities.get(i)[0];
            Cluster c = allClusters[index];
            //System.out.println("C" + c.getNumber() + ": " + c.getNumDataPoints() + " data points.");
            relatives.add(c);
        }
    }
    
    private Vector<double[]> getSimilarities() {
        //returns a vector of doubles with the first elt representing the index of the cluster
        //and the second the similarity value
        Vector<double[]> sims = new Vector<double[]>();
        for(int i = 0; i < allClusters.length; i++) {
            Cluster other = allClusters[i];
            if( !(other.equals(original))  ) {
                double[] sim = new double[2];
                sim[0] = other.getNumber();
                sim[1] = getPairwiseSimilarity(original, other);
                sims.add(sim);
            }
        }
        return sims;
    }
    
    private double getPairwiseSimilarity(Cluster a, Cluster b) {
        double distance = 0;
        int pairsToCheck = 100;
        //check the similarity between 10 random pairs of points and return the average
        for(int i = 0; i < pairsToCheck; i++) {
            DataPoint pointA = a.getRandomDataPoint();
            DataPoint pointB = b.getRandomDataPoint();
            distance += pointA.calcEuclideanDistance(pointB);
        }
        distance /= (double)pairsToCheck;
        return distance; 
    }
    
    public int getNumPointsInRelatives() {
        int num = 0;
        for(int i = 0; i < relatives.size(); i++) {
            num += relatives.get(i).getNumDataPoints();
        }
        return num;
    }
    
    public int getNumRelatives() {
        return relatives.size();
    }
            
    
    public Cluster getOriginal() {
        return original;
    }
    
    public Cluster getRandomCluster() {
        Random rand = new Random();
        int p = rand.nextInt(relatives.size() + 1);
        if (p == relatives.size()) {
            return original;
        } else {
            return relatives.get(p);
        }
    }
    
    public Vector<Cluster> getStarterClusters() {
        Vector<Cluster> starters = new Vector<Cluster>();
        if (original.containsStarter()) {
            starters.add(original);
        }
        for (int i = 0; i < relatives.size(); i++) {
            Cluster c = relatives.get(i);
            if (c.containsStarter()) {
                starters.add(c);
            }
        }
        return starters;
    }
    
    public DataPoint getRandomPoint() {
        Random rand = new Random();
        int p = rand.nextInt(points.size());
        return points.get(p);
    }

       public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("cluster set of size " + relatives.size() + "\n");
        for( Enumeration<Cluster> e = relatives.elements(); e.hasMoreElements(); )
        {
            buffer.append("");
            buffer.append(e.nextElement().toString());
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
