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
    
    public DataPoint() {
    
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
        mEuDt = Math.sqrt(
                tWeight * Math.pow((mT - mCluster.getCentroid().getCt()), 2) +
                uWeight * Math.pow((mU - mCluster.getCentroid().getCu()), 2) +
                vWeight * Math.pow((mV - mCluster.getCentroid().getCv()), 2) +  
                wWeight * Math.pow((mW - mCluster.getCentroid().getCw()), 2) +  
                xWeight * Math.pow((mX - mCluster.getCentroid().getCx()), 2) + 
                yWeight * Math.pow((mY - mCluster.getCentroid().getCy()), 2) +
                zWeight * Math.pow((mZ - mCluster.getCentroid().getCz()), 2)
                );
    }

    public double calcEuclideanDistance(DataPoint point2) {
        return Math.sqrt(
                tWeight * Math.pow((mT - point2.getT()), 2) +
                uWeight * Math.pow((mU - point2.getU()), 2) +
                vWeight * Math.pow((mV - point2.getV()), 2) + 
                wWeight * Math.pow((mW - point2.getW()), 2) +  
                xWeight * Math.pow((mX - point2.getX()), 2) + 
                yWeight * Math.pow((mY - point2.getY()), 2) +
                zWeight * Math.pow((mZ - point2.getZ()), 2)
                );
    }
    
    public double testEuclideanDistance(Centroid c) {
        return Math.sqrt(
               tWeight*Math.pow((mT - c.getCt()), 2) +
               uWeight*Math.pow((mU - c.getCu()), 2) +
               vWeight*Math.pow((mV - c.getCv()), 2) + 
               wWeight*Math.pow((mW - c.getCw()), 2) + 
               xWeight*Math.pow((mX - c.getCx()), 2) + 
               yWeight*Math.pow((mY - c.getCy()), 2) + 
               zWeight*Math.pow((mZ - c.getCz()), 2));
    }

    public boolean equals(DataPoint otherPoint) {
        return this.getObjData().equals(otherPoint.getObjData());
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
    
    public void addDuplicate() {
        this.number++;
    }
    
    public Cluster getCluster() {
        return mCluster;
    }

    public String getClusterName() {
        return clusterName;
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
        String s = "Datapoint: " + getObjName() + "\n";
        //s = s.concat("ObjData: " + getObjData() + "\n");
        //s = s.concat("ClusterNumber: " + getCluster() + "\n");
        //s = s.concat("ClusterName: " + getClusterName() + "\n");
        s = s.concat("T: " + getT() + "\n");
        s = s.concat("U: " + getU() + "\n");
        s = s.concat("V: " + getV() + "\n");
        s = s.concat("W: " + getW() + "\n");
        s = s.concat("X: " + getX() + "\n");
        s = s.concat("Y: " + getY() + "\n");
        s = s.concat("Z: " + getZ() + "\n");
        return s;
    }
    
}