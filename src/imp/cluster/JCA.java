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
import java.util.Vector;

/**

This class is the entry point for constructing Cluster Analysis objects.
Each instance of JCA object is associated with one or more clusters,
and a Vector of DataPoint objects. The JCA and DataPoint classes are
the only classes available from other packages.
@see DataPoint

 **/
public class JCA implements Serializable{

    private Cluster[] clusters;
    private int miter;
    private Vector<DataPoint> mDataPoints = new Vector();
    private double mSWCSS;

    public JCA(int k, int iter, Vector dataPoints) {
        clusters = new Cluster[k];
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster("Cluster" + i);
        }
        this.miter = iter/5;
        this.mDataPoints = dataPoints;
    }

    private void calcSWCSS() {
        double temp = 0;
        for (int i = 0; i < clusters.length; i++) {
            temp = temp + clusters[i].getSumSqr();
        }
        mSWCSS = temp;
    }

    public void startAnalysis() {
        //set Starting centroid positions - Start of Step 1
        setInitialCentroids();
        int n = 0;
        //assign DataPoint to clusters
        loop1:
        while (true) {
            for (int l = 0; l < clusters.length; l++) {
                clusters[l].addDataPoint((DataPoint) mDataPoints.elementAt(n));
                n++;
                if (n >= mDataPoints.size()) {
                    break loop1;
                }
            }
        }

        //calculate E for all the clusters
        calcSWCSS();

        //recalculate Cluster centroids - Start of Step 2
        for (int i = 0; i < clusters.length; i++) {
            clusters[i].getCentroid().calcCentroid();
        }

        //recalculate E for all the clusters
        calcSWCSS();

        for (int i = 0; i < miter; i++) {
            //enter the loop for cluster 1
            for (int j = 0; j < clusters.length; j++) {
                for (int k = 0; k < clusters[j].getNumDataPoints(); k++) {

                    //pick the first element of the first cluster
                    //get the current Euclidean distance
                    double tempEuDt = clusters[j].getDataPoint(k).getCurrentEuDt();
                    Cluster tempCluster = null;
                    boolean matchFoundFlag = false;

                    //call testEuclidean distance for all clusters
                    for (int l = 0; l < clusters.length; l++) {

                        //if testEuclidean < currentEuclidean then
                        if (tempEuDt > clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid())) {
                            tempEuDt = clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid());
                            tempCluster = clusters[l];
                            matchFoundFlag = true;
                        }
                    //if statement - Check whether the Last EuDt is > Present EuDt

                    }
                    //for variable 'l' - Looping between different Clusters for matching a Data Point.
                    //add DataPoint to the cluster and calcSWCSS

                    if (matchFoundFlag) {
                        tempCluster.addDataPoint(clusters[j].getDataPoint(k));
                        clusters[j].removeDataPoint(clusters[j].getDataPoint(k));
                        for (int m = 0; m < clusters.length; m++) {
                            clusters[m].getCentroid().calcCentroid();
                        }

                        //for variable 'm' - Recalculating centroids for all Clusters

                        calcSWCSS();
                    }

                //if statement - A Data Point is eligible for transfer between Clusters.
                }
            //for variable 'k' - Looping through all Data Points of the current Cluster.
            }   //for variable 'j' - Looping through all the Clusters.

        }//for variable 'i' - Number of iterations.

    }

    public Cluster[] getClusterOutput() {
        return clusters;
    }

    private void setInitialCentroids() {
        if(this.getKValue() == 2) {
            //this case handles splitting into two clusters, each a standard dev from the mean
            double tMean = 0, uMean = 0, vMean = 0, wMean = 0, xMean = 0, yMean = 0, zMean = 0;
            for(int i = 0; i < mDataPoints.size(); i++) {
                DataPoint d = mDataPoints.get(i);
                tMean += d.getT();
                uMean += d.getU();
                vMean += d.getV();
                wMean += d.getW();
                xMean += d.getX();
                yMean += d.getY();
                zMean += d.getZ();
            }
            tMean /= mDataPoints.size();
            uMean /= mDataPoints.size();
            vMean /= mDataPoints.size();
            wMean /= mDataPoints.size();
            xMean /= mDataPoints.size();
            yMean /= mDataPoints.size();
            zMean /= mDataPoints.size();
            double tVar = 0, uVar = 0, vVar = 0, wVar = 0, xVar = 0, yVar = 0, zVar = 0;
            for(int i = 0; i < mDataPoints.size(); i++) {
                DataPoint d = mDataPoints.get(i);
                tVar += Math.pow(d.getT() - tMean, 2);
                uVar += Math.pow(d.getU() - uMean, 2);
                vVar += Math.pow(d.getV() - vMean, 2);
                wVar += Math.pow(d.getW() - wMean, 2);
                xVar += Math.pow(d.getX() - xMean, 2);
                yVar += Math.pow(d.getY() - yMean, 2);
                zVar += Math.pow(d.getZ() - zMean, 2);
            }
            tVar /= mDataPoints.size();
            uVar /= mDataPoints.size();
            vVar /= mDataPoints.size();
            wVar /= mDataPoints.size();
            xVar /= mDataPoints.size();
            yVar /= mDataPoints.size();
            zVar /= mDataPoints.size();
            double tDev = Math.sqrt(tVar);
            double uDev = Math.sqrt(uVar);
            double vDev = Math.sqrt(vVar); 
            double wDev = Math.sqrt(wVar);
            double xDev = Math.sqrt(xVar); 
            double yDev = Math.sqrt(yVar);
            double zDev = Math.sqrt(zVar);
            double t1 = tMean + tDev, t2 = tMean - tDev;
            double u1 = uMean + uDev, u2 = uMean - uDev;
            double v1 = vMean + vDev, v2 = vMean - vDev;
            double w1 = wMean + wDev, w2 = wMean - wDev;
            double x1 = xMean + xDev, x2 = xMean - xDev;
            double y1 = yMean + yDev, y2 = yMean - yDev;
            double z1 = zMean + zDev, z2 = zMean - zDev;
            Centroid c1 = new Centroid (t1, u1, v1, w1, x1, y1, z1);
            /*
            System.out.println("Centroid 1: " + " V: " + v1 + " W: " + w1 +
                    " X: " + x1 + " Y: " + y1 + " Z: " + z1);
            System.out.println("Centroid 2: " + " V: " + v2 + " W: " + w2 +
                    " X: " + x2 + " Y: " + y2 + " Z: " + z2);
             */
            clusters[0].setCentroid(c1);
            c1.setCluster(clusters[0]);
            Centroid c2 = new Centroid (t2, u2, v2, w2, x2, y2, z2);
            clusters[1].setCentroid(c2);
            c2.setCluster(clusters[1]);
            return;
        }
        
        else {
        //kn = (round((max-min)/k)*n)+min where n is from 0 to (k-1).
        double ct = 0, cu = 0, cv = 0, cw = 0, cx = 0, cy = 0, cz = 0;
        for (int n = 1; n <= clusters.length; n++) {
            ct = (((getMaxTValue() - getMinTValue()) / (clusters.length + 1)) * n) + getMinTValue();
            cu = (((getMaxUValue() - getMinUValue()) / (clusters.length + 1)) * n) + getMinUValue();
            cv = (((getMaxVValue() - getMinVValue()) / (clusters.length + 1)) * n) + getMinVValue();
            cw = (((getMaxWValue() - getMinWValue()) / (clusters.length + 1)) * n) + getMinWValue();
            cx = (((getMaxXValue() - getMinXValue()) / (clusters.length + 1)) * n) + getMinXValue();
            cy = (((getMaxYValue() - getMinYValue()) / (clusters.length + 1)) * n) + getMinYValue();
            cz = (((getMaxZValue() - getMinZValue()) / (clusters.length + 1)) * n) + getMinZValue();
            Centroid c1 = new Centroid(ct, cu, cv, cw, cx, cy, cz);
            //System.out.println("Centroid " + n + ": V: " + cv + " W: " + cw +
            //        " X: " + cx + " Y: " + cy + " Z: " + cz);
            clusters[n - 1].setCentroid(c1);
            c1.setCluster(clusters[n - 1]);
        }
        }
    }
    
    private double getMaxTValue() {
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getT();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getT() > temp) ? dp.getT() : temp;
        }
        return temp;
    }

    private double getMinTValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getT();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getT() < temp) ? dp.getT() : temp;
        }
        return temp;
    }
    
    private double getMaxUValue() {
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getU();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getU() > temp) ? dp.getU() : temp;
        }
        return temp;
    }

    private double getMinUValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getU();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getU() < temp) ? dp.getU() : temp;
        }
        return temp;
    }    
    
    private double getMaxVValue() {
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getV();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getV() > temp) ? dp.getV() : temp;
        }
        return temp;
    }

    private double getMinVValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getV();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getV() < temp) ? dp.getV() : temp;
        }
        return temp;
    }
    
    private double getMaxWValue() {
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getW();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getW() > temp) ? dp.getW() : temp;
        }
        return temp;
    }

    private double getMinWValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getW();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getW() < temp) ? dp.getW() : temp;
        }
        return temp;
    }

    private double getMaxXValue() {
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getX();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getX() > temp) ? dp.getX() : temp;
        }
        return temp;
    }

    private double getMinXValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getX();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getX() < temp) ? dp.getX() : temp;
        }
        return temp;
    }

    private double getMaxYValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getY();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getY() > temp) ? dp.getY() : temp;
        }
        return temp;
    }

    private double getMinYValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getY();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getY() < temp) ? dp.getY() : temp;
        }
        return temp;
    }

    private double getMaxZValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getZ();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getZ() > temp) ? dp.getZ() : temp;
        }
        return temp;
    }

    private double getMinZValue() {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getZ();
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getZ() < temp) ? dp.getZ() : temp;
        }
        return temp;
    }

    public int getKValue() {
        return clusters.length;
    }

    public int getIterations() {
        return miter;
    }

    public int getTotalDataPoints() {
        return mDataPoints.size();
    }

    public double getSWCSS() {
        return mSWCSS;
    }

    public Cluster getCluster(int pos) {
        return clusters[pos];
    }
}