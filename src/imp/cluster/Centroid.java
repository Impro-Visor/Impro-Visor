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

/**
 * This class represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 * @author Shyam Sivaraman
 * @version 1.0
 * @see Cluster
 * from: http://www.codecodex.com/wiki/index.php?title=K-means_cluster_analysis_algorithm#Java
 */
class Centroid implements Serializable{

    private double mCt, mCu, mCv, mCw,  mCx,  mCy,  mCz;
    private Cluster mCluster;

    public Centroid(double ct, double cu, double cv, double cw, double cx, double cy, double cz) {
        this.mCt = ct;
        this.mCu = cu;
        this.mCv = cv;
        this.mCw = cw;
        this.mCx = cx;
        this.mCy = cy;
        this.mCz = cz;
    }

    public void calcCentroid() { //only called by CAInstance

        int numDP = mCluster.getNumDataPoints();
        double tempT = 0, tempU = 0, tempV = 0, tempW = 0, tempX = 0, tempY = 0, tempZ = 0;
        int i;
        //calculating the new Centroid
        for (i = 0; i < numDP; i++) {
            //total for t
            tempT = tempT + mCluster.getDataPoint(i).getT();
            //total for u
            tempU = tempU + mCluster.getDataPoint(i).getU();
            //total for v
            tempV = tempV + mCluster.getDataPoint(i).getV();
            //total for w
            tempW = tempW + mCluster.getDataPoint(i).getW();
            //total for x
            tempX = tempX + mCluster.getDataPoint(i).getX();
            //total for y
            tempY = tempY + mCluster.getDataPoint(i).getY();
            //total for z
            tempZ = tempZ + mCluster.getDataPoint(i).getZ();
        }
        this.mCt = tempT / numDP;
        this.mCu = tempU / numDP;
        this.mCv = tempV / numDP;
        this.mCw = tempW / numDP;
        this.mCx = tempX / numDP;
        this.mCy = tempY / numDP;
        this.mCz = tempZ / numDP;
        
        //calculating the new Euclidean Distance for each Data Point
        tempT = 0;
        tempU = 0;
        tempV = 0;
        tempW = 0;
        tempX = 0;
        tempY = 0;
        tempZ = 0;
        
        for (i = 0; i < numDP; i++) {
            mCluster.getDataPoint(i).calcEuclideanDistance();
        }
        //calculate the new Sum of Squares for the Cluster
        mCluster.calcSumOfSquares();
    }

    public void setCluster(Cluster c) {
        this.mCluster = c;
    }
    
    public double getCt() {
        return mCt;
    }
    
    public double getCu() {
        return mCu;
    }
    
    public double getCv() {
        return mCv;
    }
    
    public double getCw() {
        return mCw;
    }
    
    public double getCx() {
        return mCx;
    }

    public double getCy() {
        return mCy;
    }

    public double getCz() {
        return mCz;
    }
    
    public Cluster getCluster() {
        return mCluster;
    }
}