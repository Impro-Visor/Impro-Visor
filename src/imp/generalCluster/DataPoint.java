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

import imp.data.ContourData;
import imp.data.MelodyRhythmCount;
import imp.data.RhythmCluster;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;
import polya.Polylist;

/**
This class represents a candidate for Cluster analysis. A candidate must have
a name and five independent variables on the basis of which it is to be clustered.
A Data Point must have two variables and a name. A Vector of Data Point object
is fed into the constructor of the JCA class. JCA and DataPoint are the only
classes which may be available from other packages.
@author Shyam Sivaraman, modified by Jon Gillick
@version 1.0
@see JCA
@see Cluster
 * from: http://www.codecodex.com/wiki/index.php?title=K-means_cluster_analysis_algorithm#Java
 */
public class DataPoint implements Serializable{
    
    private double tWeight = 1, uWeight = 0.5;
    private double vWeight = 1.2, wWeight = 1, xWeight = 1.0, yWeight = 1.3, zWeight = 1.1;
    private double mT, mU, mV,  mW,   mX,  mY,   mZ;
    private int mSegLength;
    private String mObjName, mObjData, relativePitchMelody;
    private String ruleString;
    private Cluster mCluster;
    private double mEuDt;
    private String clusterName;
    private String brickType = "None";
    private int number = 1;
    private boolean starter;
    private IndexedMelodyPart melody;
    private boolean head;
    private double similarityToHead;
    private int chorusNumber;
    private Vector<String> chordList;
    private boolean tiedAtStart;
    private boolean tiedAtEnd;
    private float syncopation; 
    private float numContourChanges; 
    private float diversityIndex;
    private MelodyRhythmCount melodyRhythmCount;
    private ContourData contourData;
    private RhythmCluster rhythmCluster;
    private float restPercent;
    
    private ArrayList<Metric> metricList;
    
   
    
    public DataPoint() {
    
    }
    
    //this is the improved constructor supporting a variable number of metrics to cluster on 
    //using an array list of Metric objects
    public DataPoint(ArrayList<Metric> metricList, String name, String data, int segLength, boolean start, 
            IndexedMelodyPart exactMelody, String relativePitchMelody, String brickType, boolean isHead, int chorusNumber, 
            Vector<String> chords, boolean startTied, boolean endTied,float restPercent, String ruleString){
        
        this.metricList = metricList;
        this.mObjName = name;
        

        this.mObjData = data;
        this.mCluster = null;
        this.rhythmCluster = null;
        this.mSegLength = segLength;
        this.starter = start;
        this.melody = exactMelody;
        this.relativePitchMelody = relativePitchMelody;
        this.brickType = brickType;
        this.head = isHead;
        this.chorusNumber = chorusNumber;
        this.chordList = chords;
        this.tiedAtStart = startTied;
        this.tiedAtEnd = endTied;
        this.restPercent = restPercent;
        this.ruleString = ruleString;
        //System.out.println("notecount for datapoint "+mObjName+": " + metricList.get(2).toString());
        
    }
    
    public ArrayList<Metric> getMetrics(){
        return this.metricList;
    }
    
    public String getRuleString(){
        return this.ruleString;
    }

    //not in use
    public DataPoint(double w, double x, double y, double z, String name) {
        this.mV = 0;
        this.mW = w;
        this.mX = x;
        this.mY = y;
        this.mZ = z;
        this.mObjName = name;
        this.mObjData = "";
        this.mCluster = null;
        this.mSegLength = 0;
    }
    
    public DataPoint(double t, double u, double v, double w, double x, double y,double z, String name) {
        this.mT = t;
        this.mU = u;        
        this.mV = v;
        this.mW = w;
        this.mX = x;
        this.mY = y;
        this.mZ = z;
        this.mObjName = name;
        this.mCluster = null;
    }
    
    public DataPoint(double t, double u, double v, double w, double x, double y,double z, 
            String name, String data, int segLength, boolean start, 
            IndexedMelodyPart exactMelody, boolean isHead, int chorusNumber, 
            Vector<String> chords, boolean startTied, boolean endTied) {
        this.mT = t;
        this.mU = u;
        this.mV = v;
        this.mW = w;
        this.mX = x;
        this.mY = y;
        this.mZ = z;
        this.mObjName = name;
        this.mObjData = data;
        this.mCluster = null;
        this.mSegLength = segLength;
        this.starter = start;
        this.melody = exactMelody;
        this.head = isHead;
        this.chorusNumber = chorusNumber;
        this.chordList = chords;
        this.tiedAtStart = startTied;
        this.tiedAtEnd = endTied;
    }
    
    //updated version of above constructor that also initializes relative pitch melody and brick type
    //above version left for now so as not to break anything that already exists
    public DataPoint(double t, double u, double v, double w, double x, double y,double z, 
            String name, String data, int segLength, boolean start, 
            IndexedMelodyPart exactMelody, String relativePitchMelody, String brickType, boolean isHead, int chorusNumber, 
            Vector<String> chords, boolean startTied, boolean endTied) {
        this.mT = t;
        this.mU = u;
        this.mV = v;
        this.mW = w;
        this.mX = x;
        this.mY = y;
        this.mZ = z;
        this.mObjName = name;
        this.mObjData = data;
        this.mCluster = null;
        this.mSegLength = segLength;
        this.starter = start;
        this.melody = exactMelody;
        this.relativePitchMelody = relativePitchMelody;
        this.brickType = brickType;
        this.head = isHead;
        this.chorusNumber = chorusNumber;
        this.chordList = chords;
        this.tiedAtStart = startTied;
        this.tiedAtEnd = endTied;
    }
    
   
    /**
     * 
     * @param syncopation = Percent of notes that are on offbeat
     * @param melodyRhythmCount = object storing rhythm duration data such as most
     *                      frequent duration and a list of all durations w/ frequency
     * @param contourData  = object storing data about contour such as number of slope shifts
     *                          and an ArrayList of slope type
     */
    public void addRhythmData(MelodyRhythmCount melodyRhythmCount, ContourData contourData, float syncopation){
        this.syncopation = syncopation;
        this.melodyRhythmCount=melodyRhythmCount;
        this.contourData = contourData;
    }
    
    public void setCluster(Cluster cluster) {
        this.mCluster = cluster;
        calcEuclideanDistance();
    }

    public void setClusterName(String name) {
        this.clusterName = name;
    }
    
    public void setObjectName(String name) {
        mObjName = name;
    }
    
    public void setRelativePitchMelody(String relativePitchMelody) {
        this.relativePitchMelody = relativePitchMelody;
    }
    
    public void setBrickType(String brickType) {
        this.brickType = brickType;
    }
    
    public void calcEuclideanDistance() {
    //called when DP is added to a cluster or when a Centroid is recalculated.
        float sumOfSquares = 0;
         //System.out.println("Metric list for datapoint " + this.mObjName + ": " + metricList);
         //System.out.println("Metric list for centroid : " + mCluster.getCentroid().getMetrics());
        
        for(int i = 0; i < metricList.size(); i++){
            Metric m = metricList.get(i);
           
            
            float centroidVal = mCluster.getCentroid().getMetricAtI(i).getValue();
            sumOfSquares += m.getWeight() * Math.pow((m.getValue() - centroidVal), 2);
        }
        
        mEuDt = Math.sqrt(sumOfSquares);
    }

    public double calcEuclideanDistance(DataPoint point2) {
        
    float sumOfSquares = 0;
        
        for(int i = 0; i < metricList.size(); i++){
            Metric m = metricList.get(i);
            float point2Val = point2.getMetrics().get(i).getValue();
            
            sumOfSquares += m.getWeight() * Math.pow((m.getValue() - point2Val), 2);
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    public static Polylist getNotePolylistWLeadingX(String s){
        String[] sParts = s.split("\\s+");
        return Polylist.list(sParts[0], sParts[sParts.length - 1]);
    }
    
    public static Polylist getNotePolylistWLeadingR(String s){
        s = combineRIfNecessary(s);
        return Polylist.list(s.charAt(0), s.substring(1));
    }
    
    public static String combineRIfNecessary(String input){//used to handle case where we get "R8  R4" as input
        String rtn = ""+input.charAt(0);
        String s = input;
        
        s = s.substring(1);
        
        if(!s.contains("R")){//if you don't have case with 2 R's
            return input;
        }
        
        
        
          
        for(int i = 0; i < s.length(); i++){      
            if( Character.isDigit(s.charAt(i))){//if we have a number
                rtn += s.charAt(i) + "+";//add it to the rest
            }
        }
        
        if(rtn.charAt(rtn.length() -1) == '+'){
            rtn = rtn.substring(0, rtn.length()-1);//remove the last plus
        }
        
        return rtn;
        
    }
    
    public Polylist getRelativePitchPolylist(){
        String[] part = relativePitchMelody.split("\\(|\\)");
            
        Polylist relativePL = Polylist.list("rhythm");
        for (String s: part){
            s = s.trim();//remove trailing and leading whitespace
            if(s.length() == 0){continue;}//skip empty strings
            if(s.charAt(0) == 'R'){   
                Polylist note = getNotePolylistWLeadingR(s);
                        
                relativePL = relativePL.addToEnd(note);
            }else if(s.charAt(0) == 'X'){
                Polylist note = getNotePolylistWLeadingX(s);
                relativePL = relativePL.addToEnd(note);
            }
        }
        
//        for (String s: part){
//            s = s.replaceAll("\\s+", "");
//            if (s.length() >= 3){
//                int dur = Character.getNumericValue(s.charAt(s.length() - 1));
//                if(dur != -1){
//                    Polylist note = Polylist.list(s.charAt(0),s.charAt(s.length() - 1));
//                    relativePL = relativePL.addToEnd(note);
//                }      
//            }   
//        }
//        
        return relativePL;
    }
    
    
    public double testEuclideanDistance(Centroid c) {
        float sumOfSquares = 0;
        
        for(int i = 0; i < metricList.size(); i++){
            Metric m = metricList.get(i);
            
            float centroidVal = c.getMetricAtI(i).getValue();
            sumOfSquares += m.getWeight() * Math.pow((m.getValue() - centroidVal), 2);
        }
        
        return Math.sqrt(sumOfSquares);
       
    }
    public boolean hasCluster(){
        return mCluster == null;
    }
    public double getEuclideanDistanceToCentroid(Centroid c) {
        float sumOfSquares = 0;
        
        for(int i = 0; i < metricList.size(); i++){
            Metric m = metricList.get(i);
            
            float centroidVal = c.getMetricAtI(i).getValue();
            sumOfSquares += m.getWeight() * Math.pow((m.getValue() - centroidVal), 2);
        }
        
        return Math.sqrt(sumOfSquares);
       
    }

    public boolean equals(DataPoint otherPoint) {
        for(int i = 0; i < metricList.size(); i++){
            if(metricList.get(i).getValue() != otherPoint.getMetrics().get(i).getValue()){
                return false;
            }
        }
        return true;
    }
    
    public double getT() {
        return mT;
    }
    
    public void setT(double t) {
        this.mT = t;
    }
    
    public double getU() {
        return mU;
    }
    
    public void setU(double u) {
        this.mU = u;
    }
    
    public double getV() {
        return mV;
    }
    
    public void setV(double v) {
        this.mV = v;
    }
    
    public double getW() {
        return mW;
    }
    
    public void setW(double w) {
        this.mW = w;
    }
    
    public double getX() {
        return mX;
    }
    
    public void setX(double x) {
        this.mX = x;
    }

    public double getY() {
        return mY;
    }
    
    public void setY(double y) {
        this.mY = y;
    }

    public double getZ() {
        return mZ;
    }
    
    public void setZ(double z) {
        this.mZ = z;
    }
    
    public float getRestPercent(){
        return this.restPercent;
    }
    
    public void addDuplicate() {
        this.number++;
    }
    
    public Cluster getCluster() {
        return mCluster;
    }

    public String getClusterName() {
        return clusterName;
    }
    
    public RhythmCluster getRhythmCluster(){
        return rhythmCluster;
    }
    
    public void setRhythmCluster(RhythmCluster rc){
        rhythmCluster = rc;
    }
    public double getCurrentEuDt() {
        return mEuDt;
    }

    public String getObjName() {
        return mObjName;
    }
    
    public String getObjData() {
        return mObjData;
    }
    
    public int getSegLength() {
        return mSegLength;
    }
    
    public int getNumber() {
        return number;
    }
    
    public IndexedMelodyPart getMelody() {
        return melody;
    }
    
    public boolean isStarter() {
        return starter;
    }
    
    public boolean isHead() {
        return head;
    }
    
    public boolean isTiedAtStart() {
        return tiedAtStart;
    }
    
    public boolean isTiedAtEnd() {
        return tiedAtEnd;
    }
    
    public void computeSimilarityToHead(DataPoint other) {
        if(other.getSegLength() != this.getSegLength()) {
            System.out.println("sizes of sections to be compared don't match.");
            return;
        }
        IndexedMelodyPart otherMelody = other.getMelody();
        int numSlots = this.getSegLength() * 120;
        //System.out.println("numSlots: " + numSlots);
        int index = melody.getIndex();
        int counter = 0;
        for(int i = 0; i < numSlots; i++) {
            //System.out.println("i: " + i);
            //System.out.println("Goose: " + melody.getPitchSounding(i));
            //System.out.println("DoubleGoose: " + otherMelody.getPitchSounding(i));
            if( (melody.getPitchSounding(i) % 12) == (otherMelody.getPitchSounding(i) % 12)) 
                counter++;
        }
        double similarity = (double)counter / (double)numSlots;
        similarityToHead = similarity;
    }
    
    public double getSimilarityToHead() {
        return similarityToHead;
    }
    
    public int getChorusNumber() {
        return chorusNumber;
    }
    
    public String getAbstractMelody() {
        //int startIndex = mObjData.indexOf("slope") - 1;
        //int endIndex = mObjData.lastIndexOf(")");
        return mObjData;//.substring(1, mObjData.length()-2);//.substring(startIndex);
    }
    
    public String getRelativePitchMelody() {
        return relativePitchMelody;
    }
    
    public String getBrickType() {
        return brickType;
    }
    
    public Vector<String> getChords() {
        return chordList;
    }
    
    @Override
    public String toString() {
        String s = "Datapoint " + getObjName()+ ": ";
        
        for(int i = 0; i < metricList.size(); i++){
            s = s.concat(metricList.get(i).getName() + ": " + metricList.get(i).getValue() + ", ");
        }
        
        s = s.concat("\n");
        
        return s;
    }
    
    public void setMetricAtI(int i, float value){
        if (i>metricList.size()-1||i<0){
            System.out.println("invalid index to set metric list");
        }else{
            Metric m = new Metric(value, metricList.get(i).getWeight(),metricList.get(i).getName(),metricList.get(i).isLengthIndependent());
            metricList.set(i, m);
        }
    }
    
    public float[] getNormalizedMetricVector(){
        float[] normalizedMetricVector = new float[metricList.size()];
        float totalMetricVectorLength = getLengthOfMetricVector();
        for(int i = 0; i < metricList.size(); i++){
            normalizedMetricVector[i] = metricList.get(i).getValue() / totalMetricVectorLength;
        }
        return normalizedMetricVector;
    }
    
    private float getLengthOfMetricVector(){
        float totalMetricListLength = 0;
        for(int i = 0; i < metricList.size(); i++){//get the sum of squares for all of the metric values in the metric list
            totalMetricListLength += Math.pow(metricList.get(i).getValue(), 2);
        }
        
        return (float) Math.sqrt(totalMetricListLength); 
    }
    
    public ArrayList<Metric> scaleMetrics(float normalizingRatio){
//        float normalizingRatio = targetSegLength / mySegLength;
//        System.out.println("mySegLength: " + mySegLength + ", targetSegLength: " + targetSegLength + ", normalizingRatio: " + normalizingRatio);
        System.out.println("metricList was: " + metricList.toString());
        for(int i = 0; i < metricList.size(); i++){
            if(!metricList.get(i).isLengthIndependent()){
                System.out.println("hit the contains conditional");
                float newVal = metricList.get(i).getValue() * normalizingRatio;
                metricList.get(i).setValue(newVal);
            }
        }
        
        System.out.println("metricList is now: " + metricList.toString());
        
        return metricList;
        
    }
    
}