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

package imp.generalCluster;

import java.io.Serializable;
import java.util.ArrayList;
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
    private int numMetrics;
    private String[] names;
    private boolean[] isLengthIndependentBools;

    public JCA(int k, int iter, Vector dataPoints) {
        clusters = new Cluster[k];
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster("Cluster" + i);
        }
        this.miter = iter/5;
        //this.miter = iter;
        System.out.println("miter is: " + miter);
        this.mDataPoints = dataPoints;
        if(mDataPoints.isEmpty()){
            numMetrics = 0;
        }else{
            numMetrics = mDataPoints.get(0).getMetrics().size();
            names = new String[numMetrics];
            isLengthIndependentBools = new boolean[numMetrics];
            
            int i =0;
            for (Metric m: mDataPoints.get(0).getMetrics()){
                names[i] = m.getName();
                isLengthIndependentBools[i] = m.isLengthIndependent();
                i++;
            }
        }
        
        System.out.println("Constructor finished!");
        
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
            
            float[] avgMetricVals = new float[numMetrics];

            int numDP = mDataPoints.size();
        
            for (int i = 0; i < numDP; i++) {
                for (int j=0; j<numMetrics;j++){
                    avgMetricVals[j] += mDataPoints.get(i).getMetrics().get(j).getValue();
                }

            }
            //calculating the new Centroid
            for(int i = 0;i<numMetrics;i++){
                avgMetricVals[i] /= numDP;
            }
            //this case handles splitting into two clusters, each a standard dev from the mean
            
            float[] varianceArray = new float[numMetrics];
        
            for (int i = 0; i < numDP; i++) {
                for (int j=0; j<numMetrics;j++){
                    varianceArray[j] += Math.pow(mDataPoints.get(i).getMetrics().get(j).getValue()-avgMetricVals[j],2);
                }
            }
            for(int i = 0;i<numMetrics;i++){
                varianceArray[i] /= numDP;
            }
            
            float[] deviationArray = new float[numMetrics];
            for(int i = 0;i<numMetrics;i++){
                deviationArray[i] = (float) Math.sqrt(varianceArray[i]);
            }
            
            ArrayList<Metric> centroid1Params = new ArrayList<Metric>();
            ArrayList<Metric> centroid2Params = new ArrayList<Metric>();
            for(int i = 0;i<numMetrics;i++){
                centroid1Params.add(new Metric(avgMetricVals[i]+deviationArray[i],1,names[i],isLengthIndependentBools[i]));
                centroid2Params.add(new Metric(avgMetricVals[i]-deviationArray[i],1,names[i],isLengthIndependentBools[i]));
            }

            Centroid c1 = new Centroid(centroid1Params);
            clusters[0].setCentroid(c1);
            c1.setCluster(clusters[0]);
            
            Centroid c2 = new Centroid (centroid2Params);
            clusters[1].setCentroid(c2);
            c2.setCluster(clusters[1]);
            
        }
        
        else {
            float[] maxValues = getMaxValues();
            float[] minValues = getMinValues();
            
            //System.out.println("Max values: " + maxValues.toString());
            //System.out.println("Min values: " + minValues.toString());
            

            for (int n = 1; n <= clusters.length; n++) {
                ArrayList<Metric> centroidParams = new ArrayList<Metric>();


                for(int i=0;i<numMetrics;i++){
                    float param = (((maxValues[i] - minValues[i]) / (clusters.length + 1)) * n) + minValues[i];
                    Metric metric = new Metric(param, 1, names[i],isLengthIndependentBools[i]);
                    //System.out.println("Bout to add: " + metric.toString());
                    centroidParams.add(metric);
                }
                
                //System.out.println("Centroid params: " + centroidParams.toString());


                Centroid c1 = new Centroid(centroidParams);

                clusters[n - 1].setCentroid(c1);
                c1.setCluster(clusters[n - 1]);
            }
        }
    }

    
    private float[] getMaxValues(){
        float[]  max = new float[numMetrics];
        
        for(int i = 0; i < numMetrics; i++){
            float maxVal = Integer.MIN_VALUE;
            for(int j = 0; j < mDataPoints.size(); j++){
                if(mDataPoints.get(j).getMetrics().get(i).getValue() > maxVal){
                    maxVal = mDataPoints.get(j).getMetrics().get(i).getValue();
                }
               
            }
            
            max[i] = maxVal;
            
        }
        
        return max;
         
    }
    
    private float[] getMinValues(){
        float[]  min = new float[numMetrics];
        
        for(int i = 0; i < numMetrics; i++){
            float minVal = Integer.MAX_VALUE;
            for(int j = 0; j < mDataPoints.size(); j++){
                if(mDataPoints.get(j).getMetrics().get(i).getValue() < minVal){
                    minVal = mDataPoints.get(j).getMetrics().get(i).getValue();
                }
               
            }
            
            min[i] = minVal;
            
        }
        
        return min;
         
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