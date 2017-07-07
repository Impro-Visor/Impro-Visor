/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.generalCluster.Centroid;
import imp.generalCluster.Cluster;
import imp.generalCluster.DataPoint;
import imp.generalCluster.Metric;
import java.util.ArrayList;
import java.util.Vector;
import polya.Polylist;

/**
 *
 * @author Cai Glencross and Lukas Gnirke
 */
public class RhythmCluster extends Cluster{
    private Centroid centroid;
    private ArrayList<Polylist> polylistRhythmArray;
    private ArrayList<Metric> avgUserDataPointMetrics;
    private ArrayList<DataPoint> dataPointsFromUser;
    private float[] avgCentroidDatapointDifference;
    private int numMetrics;
    private int datapointCount = 0;
    private int clusterNumber;
    private Polylist centroidPL;
    private Polylist rhythmListPolylist;
    private ArrayList<DataPoint> selectedRhythms;
    
   
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
        numMetrics = centroid.getMetrics().size();
        
        this.rhythmListPolylist = (Polylist) p.second();
        
        polylistRhythmArray = getRhythmArrayFromPolylist(rhythmListPolylist);
     
        avgCentroidDatapointDifference = new float[numMetrics];
        
        avgUserDataPointMetrics = new ArrayList<Metric>();
        dataPointsFromUser = new ArrayList<DataPoint>();
        
        for(int i = 0; i < centroid.getMetrics().size();i++){
            String name = centroid.getMetricAtI(i).getName();
            avgUserDataPointMetrics.add(new Metric(0,1,name,true));
        }
        

        
    }
    
    
    
    public ArrayList<Polylist> getRhythmList(){
        return polylistRhythmArray;
    }
    
    private Centroid centroidFromPolylist(Polylist centroidPL){
        ArrayList<Metric> centroidMetricList = new ArrayList<Metric>();
        //ignore tag
        centroidPL=centroidPL.rest();
       
        
        while(!centroidPL.isEmpty()){
            Polylist metricPL = (Polylist) centroidPL.first();           
            Metric m = new Metric(((Double) metricPL.second()).floatValue(), 1, (String) metricPL.first(), Boolean.parseBoolean((String) metricPL.third()));
            centroidMetricList.add(m);
            centroidPL = centroidPL.rest();
        }  
        
        return new Centroid(centroidMetricList);
        
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
        return polylistRhythmArray.get(0);
    }
    
    public Polylist getRandomRhythm(){
        int randomIndex = (int) (Math.random()*(polylistRhythmArray.size()));
        //System.out.println("random index is: "+randomIndex);
        //System.out.println("rhythm at random index is: "+polylistRhythmArray.get(randomIndex));
        return polylistRhythmArray.get(randomIndex);
    }
        
        
    
    public String toString(){
        
        return "Centroid: "+centroid.getMetrics().toString() ;//+ "\nArray of rhythms: " + polylistRhythmArray.toString();
    }
    
    public int getNumMatches(){
        return datapointCount;
    }
    
    
    public void resetData(){
        datapointCount = 0;
        
        avgUserDataPointMetrics = updateMetricVals(avgUserDataPointMetrics, 0);
       
        avgCentroidDatapointDifference = fillInitialArrayValues(avgCentroidDatapointDifference, 0);
    }
    
    private float[] fillInitialArrayValues(float[] a, float value){
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
    
    private float getDatapointCentroidDistanceForMetric(float dataValue, float centroidValue){
        return Math.abs(dataValue - centroidValue);
    }
    

    
    public ArrayList<Metric> updateMetricVals(ArrayList<Metric> avgUserDataPointMetrics, float val){
        for(int i = 0; i < avgUserDataPointMetrics.size(); i++){
            avgUserDataPointMetrics.get(i).setValue(val);
        }
        return avgUserDataPointMetrics;
    }
    
    private void updateAvgDatapointCentroidDistance(float[] centroidDatapointDiff){
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
    
    public float[] getNormalizedDifference(ArrayList<Metric> dataMetricVector, ArrayList<Metric> centroidMetrics){
        float[] normalDiff = new float[dataMetricVector.size()];
        
        //get non-normalized difference vector
        float sumSquares = 0;
        for (int i = 0; i < normalDiff.length;i++){
            normalDiff[i] = Math.abs(dataMetricVector.get(i).getValue() - centroidMetrics.get(i).getValue());
            sumSquares += Math.pow(normalDiff[i], 2.0);
        }
        float length  = (float) Math.sqrt(sumSquares);
        
        //normalize it (divide by length)
        for (int i = 0; i < normalDiff.length; i++){
            normalDiff[i] = normalDiff[i] / length ;
        }
        
        return normalDiff;
    }
    
    
    public float[] getCentroidDatapointDifference(ArrayList<Metric> dataMetricVector, ArrayList<Metric> centroidMetrics){
        float[] diff = new float[numMetrics];
        
        //get difference vector
        for (int i = 0; i < diff.length;i++){
           diff[i] = Math.abs(dataMetricVector.get(i).getValue() - centroidMetrics.get(i).getValue());
        }

 
        return diff;
    }
    
    private void printArray(float[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println("    `" + a[i]);
        }
    }
    
    
    public void addUserDataPoint(DataPoint d){
        System.out.println("\nAdding dataPoint......");
        datapointCount++;
        System.out.println("printing non-normalized centroid values: ");
        System.out.println(centroid.toString());
        
        //float[] normalizedMetricVector = d.getNormalizedMetricVector();
        
        System.out.println("printing non-normalized data values: ");
        System.out.println(d.toString());
        
        //float[] centroidDatapointDifference = getCentroidDatapointDifference(d.getMetrics(), centroid.getMetrics());
        ArrayList<Metric> dataMetricVector = d.getMetrics();
        ArrayList<Metric> centroidMetrics = centroid.getMetrics();
        
        float[] centroidDatapointDifference = new float[numMetrics];
        
        //get difference vector
        for (int i = 0; i < numMetrics; i++){
            centroidDatapointDifference[i] = Math.abs(dataMetricVector.get(i).getValue() - centroidMetrics.get(i).getValue());
            float temp = avgUserDataPointMetrics.get(i).getValue();
            float newAvg = ((temp * (datapointCount - 1)) + d.getMetrics().get(i).getValue())/datapointCount;
            avgUserDataPointMetrics.get(i).setValue(newAvg);
           
        }
        
        //update the average distance between the datapoint metric value and the centroid metric value 
        updateAvgDatapointCentroidDistance(centroidDatapointDifference);
       
        dataPointsFromUser.add(d);
        
        
//        for(int i = 0; i < centroid.getMetrics().size();i++){
//            float temp = avgUserDataPointMetrics.get(i).getValue();
//            float newAvg = ((temp*sizeForUnaveraging) + d.getMetrics().get(i).getValue())/dataPointsFromUser.size();
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
    
    
    public float[] getAvgCentroidDatapointDistance(){
        return avgCentroidDatapointDifference;
    }
    
    public ArrayList<Metric> getAvgUserDataPointMetrics(){
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
    
    
    public Polylist selectivelyGetClusterMembersPolylist(ArrayList<Polylist> excludeList){
        Polylist newRhythmPL = Polylist.list("rhythmList"); 
        ArrayList<String> stringExcludeList = polylistArrayListToStringArrayList(excludeList);
        System.out.println("\n\n\nstringExcludeList: " + stringExcludeList.toString());
        System.out.println("This cluster: : " + toString());
        System.out.println("rhythmListPolylist: " + rhythmListPolylist.toString());
        System.out.println("selectedRhythms: " + selectedRhythms.toString());
        
        
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


    public void addSelectedDatapoint(DataPoint d) {
        selectedRhythms.add(d);
    }
    
}
