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

package imp.data;

import imp.generalCluster.Centroid;
import imp.generalCluster.Cluster;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.MetricListFactories.RhythmMetricListFactory;

import java.util.ArrayList;
import java.util.Vector;
import polya.Polylist;

/**
 *
 * @author Cai Glencross and Lukas Gnirke
 */
public class RhythmCluster extends Cluster{
    private Centroid centroid;
    private ArrayList<Polylist> ruleStringArray;
    private Metric[] avgUserDataPointMetrics;
    private ArrayList<DataPoint> dataPointsFromUser;
    private double[] avgCentroidDatapointDifference;
    private int numMetrics;
    private int datapointCount = 0;
    private int clusterNumber;
    private Polylist centroidPL;
    private Polylist rhythmListPolylist;
    private ArrayList<DataPoint> selectedRhythms;
    private ArrayList<DataPoint> clusterDataPoints;
    
   
    public RhythmCluster(String name) {
        super(name);
    }
    

    public RhythmCluster(Vector<DataPoint> dataPoints) {
        super(dataPoints);
    }
    
    

    public RhythmCluster(Polylist p, int clusterNumber){
        super(p);
        //ignore the tag
        p = p.rest();
        //ignore name
        p = p.rest();
        
        this.clusterNumber = clusterNumber;
        this.selectedRhythms = new ArrayList<DataPoint>();
       
       this.centroidPL = (Polylist) p.first();
        
        centroid = centroidFromPolylist(centroidPL);
        numMetrics = centroid.getMetrics().length;
        
        this.rhythmListPolylist = (Polylist) p.second();
        
        ruleStringArray = getRhythmArrayFromPolylist(rhythmListPolylist);
     
        avgCentroidDatapointDifference = new double[numMetrics];
        
        avgUserDataPointMetrics = (new RhythmMetricListFactory()).getNewMetricList();
        
        dataPointsFromUser = new ArrayList<DataPoint>();
        
        resetData();
        

        
    }
    
    
    
    public ArrayList<Polylist> getRhythmList(){
        return ruleStringArray;
    }
    
    private Centroid centroidFromPolylist(Polylist centroidPL){
        Metric[] metrics = (new RhythmMetricListFactory()).getNewMetricList();
        int index = 0;
        
        //ignore tag
        centroidPL=centroidPL.rest();
       
        while(!centroidPL.isEmpty()){
            Polylist metricPL = (Polylist) centroidPL.first();           
            //System.out.println("what we think is the metric: "+centroidPL.first().toString());
            //System.out.println("what we think is the value: "+metricPL.second().toString());
            metrics[index].setValue((Double) metricPL.second());
            centroidPL = centroidPL.rest();
            index++;
        }  
        
        return new Centroid(metrics);
        
    }
    
    private ArrayList<Polylist> getRhythmArrayFromPolylist(Polylist rhythmListPolylist){
        //ignore tag
        rhythmListPolylist = rhythmListPolylist.rest();
        ArrayList<Polylist> rhythmList = new ArrayList<Polylist>();
        while(!rhythmListPolylist.isEmpty()){
            Polylist rhythmPolylist  = (Polylist) rhythmListPolylist.first();
            //skip tag
            rhythmPolylist = rhythmPolylist.rest();
            rhythmList.add(rhythmPolylist);
            rhythmListPolylist= rhythmListPolylist.rest();
        }
        
        
        
        
        return rhythmList;
    }
    
    public Centroid getCentroid(){
        return centroid;
    }
    
    public Polylist getFirstRhythm(){
        return ruleStringArray.get(0);
    }
    
    public Polylist getRandomRhythm(){
        int randomIndex = (int) (Math.random()*(ruleStringArray.size()));
        //System.out.println("random index is: "+randomIndex);
        //System.out.println("rhythm at random index is: "+ruleStringArray.get(randomIndex));
        return ruleStringArray.get(randomIndex);
    }
        
        
    
    public String toString(){
        
        String rtn = "Centroid: "+centroid.getMetrics().toString()+ "\n   Matched Data:\n";//+ "\nArray of rhythms: " + polylistRhythmArray.toString();
        for(int i = 0; i < dataPointsFromUser.size(); i++){
            rtn += "    data[" + i + "]: " + dataPointsFromUser.get(i).toString() + "\n";
        }
    
        return rtn;
    }
    
    public int getNumMatches(){
        return datapointCount;
    }
    
    
    public void resetData(){
        datapointCount = 0;
        
        avgUserDataPointMetrics = updateMetricVals(avgUserDataPointMetrics, 0);
       
        avgCentroidDatapointDifference = fillInitialArrayValues(avgCentroidDatapointDifference, 0);
    }
    
    private double[] fillInitialArrayValues(double[] a, double value){
        for (int i = 0; i < a.length; i++){
            a[i] = value;
        }
        return a;
    }
    /**
     * Calculates the euclidean distance from a datapoint metric value to a centroid metric value
     * @param dataValue
     * @param centroidValue
     * @return 
     */
    
    private double getDatapointCentroidDistanceForMetric(double dataValue, double centroidValue){
        return Math.abs(dataValue - centroidValue);
    }
    

    
    public Metric[] updateMetricVals(Metric[] avgUserDataPointMetrics, double val){
        for(int i = 0; i < avgUserDataPointMetrics.length; i++){
            avgUserDataPointMetrics[i].setValue(val);
        }
        return avgUserDataPointMetrics;
    }
    
    private void updateAvgDatapointCentroidDistance(double[] centroidDatapointDiff){
        for(int i = 0; i < numMetrics; i++){
            avgCentroidDatapointDifference[i] = 
                        (
                        (avgCentroidDatapointDifference[i] * (datapointCount - 1))//undo average
                        + centroidDatapointDiff[i]//add a new coordinate to the average difference vector
                        )
                        /
                        datapointCount;//redo average! :D
        }
    }
    
    public double[] getNormalizedDifference(ArrayList<Metric> dataMetricVector, ArrayList<Metric> centroidMetrics){
        double[] normalDiff = new double[dataMetricVector.size()];
        
        //get non-normalized difference vector
        double sumSquares = 0;
        for (int i = 0; i < normalDiff.length;i++){
            normalDiff[i] = Math.abs(dataMetricVector.get(i).getValue() - centroidMetrics.get(i).getValue());
            sumSquares += Math.pow(normalDiff[i], 2.0);
        }
        double length  = Math.sqrt(sumSquares);
        
        //normalize it (divide by length)
        for (int i = 0; i < normalDiff.length; i++){
            normalDiff[i] = normalDiff[i] / length ;
        }
        
        return normalDiff;
    }
    
    
    public double[] getCentroidDatapointDifference(ArrayList<Metric> dataMetricVector, ArrayList<Metric> centroidMetrics){
        double[] diff = new double[numMetrics];
        
        //get difference vector
        for (int i = 0; i < diff.length;i++){
           diff[i] = Math.abs(dataMetricVector.get(i).getValue() - centroidMetrics.get(i).getValue());
        }

 
        return diff;
    }
    
    private void printArray(double[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println("    `" + a[i]);
        }
    }
    
    
    public void addUserDataPoint(DataPoint d){
//        System.out.println("\nAdding dataPoint......");
        datapointCount++;
//        System.out.println("printing non-normalized centroid values: ");
//        System.out.println(centroid.toString());
        
        //double[] normalizedMetricVector = d.getNormalizedMetricVector();
        
//        System.out.println("printing non-normalized data values: ");
//        System.out.println(d.toString());
        
        //double[] centroidDatapointDifference = getCentroidDatapointDifference(d.getMetrics(), centroid.getMetrics());
        Metric[] dataMetricVector = d.getMetrics();
        Metric[] centroidMetrics = centroid.getMetrics();
        
        double[] centroidDatapointDifference = new double[numMetrics];
        
        //get difference vector
        for (int i = 0; i < numMetrics; i++){
            centroidDatapointDifference[i] = Math.abs(dataMetricVector[i].getValue() - centroidMetrics[i].getValue());
           
            double temp = avgUserDataPointMetrics[i].getValue();
            double newAvg = ((temp * (datapointCount - 1)) + d.getMetrics()[i].getValue())/datapointCount;
            avgUserDataPointMetrics[i].setValue(newAvg);
           
        }
        
        //update the average distance between the datapoint metric value and the centroid metric value 
        updateAvgDatapointCentroidDistance(centroidDatapointDifference);
       
        dataPointsFromUser.add(d);
        
        
//        for(int i = 0; i < centroid.getMetrics().size();i++){
//            double temp = avgUserDataPointMetrics.get(i).getValue();
//            double newAvg = ((temp*sizeForUnaveraging) + d.getMetrics().get(i).getValue())/dataPointsFromUser.size();
//            avgUserDataPointMetrics.get(i).setValue(newAvg);
////            
//            if(d.getMetrics().get(i).getValue()>maxUserMetrics[i]){
//                maxUserMetrics[i] = d.getMetrics().get(i).getValue();
//            }
//            
//            if(d.getMetrics().get(i).getValue()<minUserMetrics[i]){
//                minUserMetrics[i] = d.getMetrics().get(i).getValue();
//            }
//        }
    }
    
    
    public double[] getAvgCentroidDatapointDistance(){
        return avgCentroidDatapointDifference;
    }
    
    public Metric[] getAvgUserDataPointMetrics(){
        return avgUserDataPointMetrics;
    }
    
    public ArrayList<DataPoint> getUserDataPoints(){
        return dataPointsFromUser;
    }
    
    public Polylist getClusterMembersPolylist(){
        addSelectedRhythmsToRhythmsPolylist();
        return rhythmListPolylist;
    }
    
    private void addSelectedRhythmsToRhythmsPolylist(){ 
        for(int i = 0; i < selectedRhythms.size(); i++){
            rhythmListPolylist = rhythmListPolylist.addToEnd(selectedRhythms.get(i).getRelativePitchPolylist());
     
        }
    }
    
    private ArrayList<String> polylistArrayListToStringArrayList(ArrayList<Polylist> polylistArrayList){
        ArrayList<String> stringArrayList = new ArrayList<String>();
        for(int i = 0; i < polylistArrayList.size(); i++){
            stringArrayList.add(polylistArrayList.get(i).toString());
        }
        return stringArrayList;
    }
    
    
    @Override
    public Polylist selectivelyGetClusterMembersPolylist(ArrayList<Polylist> excludeList){
        Polylist newRhythmPL = Polylist.list("rhythmList"); 
        ArrayList<String> stringExcludeList = polylistArrayListToStringArrayList(excludeList);
//        System.out.println("\n\n\nstringExcludeList: " + stringExcludeList.toString());
//        System.out.println("This cluster: : " + toString());
//        System.out.println("rhythmListPolylist: " + rhythmListPolylist.toString());
//        System.out.println("selectedRhythms: " + selectedRhythms.toString());
        
        
        if(rhythmListPolylist.first() instanceof String){
            rhythmListPolylist = rhythmListPolylist.rest();
        }
      
        while(!rhythmListPolylist.isEmpty()){
            
       
            Polylist relativePitchPL = (Polylist) rhythmListPolylist.first();
            
            //test stuff
            int index = stringExcludeList.indexOf(relativePitchPL.toString());
      
            
            if(!stringExcludeList.contains(relativePitchPL.toString())){
                //System.out.println("adding " + relativePitchPL.toString());
                newRhythmPL = newRhythmPL.addToEnd(relativePitchPL);
            }else{
                System.out.println("removing " + relativePitchPL.toString());
            }
            

            
            rhythmListPolylist = rhythmListPolylist.rest();
            
            
                    
        }
        
        rhythmListPolylist = newRhythmPL;
        return newRhythmPL;
    }

    @Override
    public Polylist selectivelyGetClusterMembersRuleStringsPolylist(ArrayList<String> excludeList){
        Polylist ruleStrings = Polylist.list("ruleStringList");
        //ArrayList<String> stringExcludeList = polylistArrayListToStringArrayList(excludeList);

//        System.out.println("\n********************\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n\n\n\n\n"
//                + "polylistRhythmArray in selectivelyGetClusterMembersRuleStringsPolylist: " + polylistRhythmArray.toString());
//        
       //System.out.println("\nexcludeList: " + excludeList);
        
        //skip "ruleStringList" tag
        rhythmListPolylist = rhythmListPolylist.rest();
        
        
        while(!rhythmListPolylist.isEmpty()){
           Polylist ruleString = (Polylist) rhythmListPolylist.first();
            
//           //skip "ruleString" tag
//           Polylist ruleStringNoTag = ruleString.rest(); 
            
            
            //System.out.println("rhythmListPolylist.first(): " + ruleString.first().toString());
               
            if(!excludeList.contains(ruleString.toString())){
                //System.out.println("adding " + ruleString.toString());
                ruleStrings = ruleStrings.addToEnd(ruleString);
            }else{
               // System.out.println("\n+++++++++++++++\n***********************\nremoving " + ruleString.toString());
            }
         
            rhythmListPolylist = rhythmListPolylist.rest();          
        }
        
  
        rhythmListPolylist = ruleStrings;
        
        return ruleStrings;
    }
    
    public ArrayList<DataPoint> getClusterDataPoints(){
        return clusterDataPoints;
    }
    

    public void addSelectedDatapoint(DataPoint d) {
        selectedRhythms.add(d);
    }
    
    public void addSelectedRuleString(Polylist p){        
        rhythmListPolylist = rhythmListPolylist.addToEnd(p);
}

    public Polylist getClosestRhythm(DataPoint d) {
        int closestIndex = -1;
        double dist;
        double minEuclideanDist = Float.MAX_VALUE;
        for(int i = 0; i < clusterDataPoints.size(); i++){
            dist = d.calcEuclideanDistance(clusterDataPoints.get(i));
            if(dist < minEuclideanDist){
                closestIndex = i;
                minEuclideanDist = dist;
    }
        }
    
        Polylist ruleString = Polylist.PolylistFromString(clusterDataPoints.get(closestIndex).getRuleString());
        return ruleString;
}
    
    
    public void createDataPointsFromRuleStrings(Double[] maxVals, Double[] minVals){
        ArrayList<DataPoint> clusterDataPoints = new ArrayList<DataPoint>();
        for(int i = 0; i < ruleStringArray.size(); i++){
            Polylist ruleStringPL = ruleStringArray.get(i);
            String ruleString = ruleStringPL.toStringSansParens();
            //System.out.println("\nruleString: " + ruleString);
            Polylist rulePL = (Polylist) ruleStringPL.first();
            Polylist abstractMel = (Polylist) rulePL.third();
            //System.out.println("abstractMel: " + abstractMel.toString());
            Polylist SegLen = (Polylist) rulePL.second();
            Polylist rule = Polylist.list(SegLen.toStringSansParens()).append(abstractMel);
            //System.out.println("rule: " + rule.toString());
            DataPoint d = CreateGrammar.processRule(rule, ruleString, "0", (new RhythmMetricListFactory()) );
            //System.out.println("maxVals[0]: " + maxVals[0]);
            
            
            
            d.normalize(maxVals,minVals);
            
            clusterDataPoints.add(d);
        }
        this.clusterDataPoints = clusterDataPoints;
//        
//        System.out.println("\n****************\n************\n    ruleStringArray: " + ruleStringArray);
    }
    
}
