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

/**
 * This class represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 * @author Shyam Sivaraman
 * @version 1.0
 * @see Cluster
 * from: http://www.codecodex.com/wiki/index.php?title=K-means_cluster_analysis_algorithm#Java
 */
public class Centroid implements Serializable{

    private double mCt, mCu, mCv, mCw,  mCx,  mCy,  mCz;
    private Cluster mCluster;
    private ArrayList<Metric> metricList;
    
    public Centroid(ArrayList<Metric> metricList) {
        this.metricList = metricList;
    }

    public Metric getMetricAtI(int i){
        return metricList.get(i);
    }
    
    
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
        
        
        float[] avgMetricVals = new float[mCluster.getCentroid().getMetrics().size()];

        int numDP = mCluster.getNumDataPoints();
        
        for (int i = 0; i < numDP; i++) {
            for (int j=0; j<avgMetricVals.length;j++){
                avgMetricVals[j] += mCluster.getDataPoint(i).getMetrics().get(j).getValue();
            }
            
        }
//        for (int j=0; j<avgMetricVals.length;j++){
//            System.out.println("avgMetricVals["+j+"]"+avgMetricVals[j]);
//         }
        //System.out.println("datapoints note count in centroid: "+mCluster.getDataPoint(0).getMetrics().get(2).getValue());
        //System.out.println("(before division) avgMetricVals[2] (notecount?): "+avgMetricVals[2]);

            
        
        //calculating the new Centroid
        for(int i = 0;i<avgMetricVals.length;i++){
            avgMetricVals[i] =  avgMetricVals[i] / numDP;
        }
        //System.out.println("avgMetricVals[2] (notecount?): "+avgMetricVals[2]);
        //System.out.println("numDP: "+numDP);
        
        for(int i = 0; i < avgMetricVals.length; i++){
            metricList.get(i).setValue(avgMetricVals[i]);
        }
        
        //calculating the new Euclidean Distance for each Data Point

        
        for (int i = 0; i < numDP; i++) {
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
    
    public ArrayList<Metric> getMetrics(){
        return metricList;
    }
    
    public float[] getnormalizedCentroidMetricValues(){
        float[] normalizedCentroidMetricValues = new float[metricList.size()];
        float totalMetricVectorLength = getLengthOfMetricVector();
        for(int i = 0; i < metricList.size(); i++){
            normalizedCentroidMetricValues[i] = metricList.get(i).getValue() / totalMetricVectorLength;
        }
        return normalizedCentroidMetricValues;
    }
    
    private float getLengthOfMetricVector(){
        float totalMetricListLength = 0;
        for(int i = 0; i < metricList.size(); i++){//get the sum of squares for all of the metric values in the metric list
            totalMetricListLength += Math.pow(metricList.get(i).getValue(), 2);
        }
        
        return (float) Math.sqrt(totalMetricListLength); 
    }
    
    public String toString(){
        String rtn = "";
        for (Metric m: metricList){
            rtn += "("+m.getName()+": "+m.getValue()+")";
         
        }
        return rtn;
    }

}