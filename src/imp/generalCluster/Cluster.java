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

import imp.data.ChordPart;
import imp.data.Unit;
import java.io.Serializable;
import java.util.*;
import polya.Polylist;

/**
 * This class represents a Cluster in a Cluster Analysis Instance. A Cluster is associated
 * with one and only one JCA Instance. A Cluster is related to more than one DataPoints and
 * one centroid.
 * @author Shyam Sivaraman
 * @version 1.1
 * @see DataPoint
 * @see Centroid
 * from: http://www.codecodex.com/wiki/index.php?title=K-means_cluster_analysis_algorithm#Java
 */

public class Cluster implements Serializable{

    private String mName;
    private Centroid mCentroid;
    private double mSumSqr;
    private Vector<DataPoint> mDataPoints;
    private int number;
    
    public Cluster(String name) {
        mName = name;
        mCentroid = null; //will be set by calling setCentroid()
        number = Integer.parseInt(name.substring(7)); 
        mDataPoints = new Vector();
    }
    
    public Cluster(Polylist p){
        
    }

    public Cluster(Vector<DataPoint> dataPoints) {
        mDataPoints = dataPoints;
    }
    
    public void setName(String name) {
        this.mName = name;
    }
    
    public void setCentroid(Centroid c) {
        mCentroid = c;
    }

    public Centroid getCentroid() {
        return mCentroid;
    }

    public void addDataPoint(DataPoint dp) { //called from CAInstance

        dp.setCluster(this); //initiates a inner call to calcEuclideanDistance() in DP.

        this.mDataPoints.addElement(dp);
        calcSumOfSquares();
    }

    public void removeDataPoint(DataPoint dp) {
        this.mDataPoints.removeElement(dp);
        calcSumOfSquares();
    }

    public int getNumDataPoints() {
        return this.mDataPoints.size();
    }

    public DataPoint getDataPoint(int pos) {
        return (DataPoint) this.mDataPoints.elementAt(pos);
    }

    public void calcSumOfSquares() { //called from Centroid

        int size = this.mDataPoints.size();
        double temp = 0;
        for (int i = 0; i < size; i++) {
            temp = temp + ((DataPoint) this.mDataPoints.elementAt(i)).getCurrentEuDt();
        }
        this.mSumSqr = temp;
    }

    public double getSumSqr() {
        return this.mSumSqr;
    }

    public String getName() {
        return this.mName;
    }
    
    public int getNumber() {
        return number;
    }

    public Vector getDataPoints() {
        return this.mDataPoints;
    }
    
    public DataPoint getRandomDataPoint() {
        Random rand = new Random();
        int r = rand.nextInt(mDataPoints.size());
        return mDataPoints.get(r);
    }
    
    /*if there are any data points in the cluster whose chords transpose to the
     * chords in the current measure, return one of those with high probability
     */
     public DataPoint getRandomDataPointWithMatchingChords(ChordPart chords) {
        Vector<DataPoint> pointsWithMatchingChords = new Vector<DataPoint>();
        for (int i = 0; i < mDataPoints.size(); i++) {
            boolean matches = true;
            //chords in the datapoint
            Vector<String> dpChords = mDataPoints.get(i).getChords();
            //chords in the song
            ArrayList<Unit> songChords = chords.getUnitList();
            if (chordsMatch(dpChords, songChords)) {
                pointsWithMatchingChords.add(mDataPoints.get(i));
            }
        }
        
        //if none have matching chords, just return a random one from the cluster
        if(pointsWithMatchingChords.size() == 0)
            return getRandomDataPoint();
        else {
            //make it twice as likely to pick one with matching chords
            float percentMatching = (float)pointsWithMatchingChords.size() / (float)mDataPoints.size();
            if(Math.random() < (percentMatching * 3) ) {
                //System.out.println("Match.");
                Random rand = new Random();
                int r = rand.nextInt(pointsWithMatchingChords.size());
                return pointsWithMatchingChords.get(r);
            }
            else
                return getRandomDataPoint();
        }
        
    }
    
    /* Returns true if the the chords in dpChords are equivalent to some 
     * transposition of the chords in songChords
     */
    
    public boolean chordsMatch(Vector<String> dpChords, ArrayList<Unit> songChords) {
        if(dpChords.size() != songChords.size()) return false;
        boolean match = true;
        for(int i = 0; i < dpChords.size(); i++) {
            String dpChord = dpChords.get(i);
            String songChord = songChords.get(i).toLeadsheet();
            //check that the distances from roots of previous chord are equal
            if(i > 0) {
                if(getDistance(dpChord, dpChords.get(i-1)) != 
                        getDistance(songChord, songChords.get(i-1).toLeadsheet()))
                    match = false;
            }
            //check that types of chords match
            if(!typesMatch(dpChord, songChord))
                match = false;
        }
        //System.out.println("Thing: ");
        //for(int i = 0; i < dpChords.size(); i++) {
        //    System.out.print(dpChords.get(i) + ", ");
        //} System.out.print("\n");
        //for(int i = 0; i < songChords.size(); i++) {
        //    System.out.print(songChords.get(i).toLeadsheet() + ", ");
        //}   System.out.print("\n");
        //System.out.println(match);
        return match;
    }
    
    //returns true if the chord types match - e.g. G7b5 and Db7b5
    public boolean typesMatch(String chord1, String chord2) {
        String type1, type2;
        
        if(chord1.length() == 1) 
            type1 = "maj";
        else if(chord1.charAt(1) == '#' || chord1.charAt(1) == 'b')
            type1 = chord1.substring(2);
        else
            type1 = chord1.substring(1);
        
        if(chord2.length() == 1) 
            type2 = "maj";
        else if(chord2.charAt(1) == '#' || chord2.charAt(1) == 'b')
            type2 = chord2.substring(2);
        else
            type2 = chord2.substring(1);
        
        return type1.equals(type2);
    }
    
    //returns the number of half steps between the roots of chord1 and chord2
    public int getDistance(String chord1, String chord2) {
        if(chord1.equals("NC") || chord2.equals("NC")) return 0;
        //create a hashtable to map root notes to values
        Hashtable roots = new Hashtable();
        roots.put("C", new Integer(0));
        roots.put("C#",new Integer(1));
        roots.put("Db",new Integer(1));
        roots.put("D",new Integer(2));
        roots.put("D#",new Integer(3));
        roots.put("Eb",new Integer(3));
        roots.put("E",new Integer(4));
        roots.put("F",new Integer(5));
        roots.put("F#",new Integer(6));
        roots.put("Gb",new Integer(6));
        roots.put("G",new Integer(7));
        roots.put("G#",new Integer(8));
        roots.put("Ab",new Integer(8));
        roots.put("A",new Integer(9));
        roots.put("A#",new Integer(10));
        roots.put("Bb",new Integer(10));
        roots.put("B",new Integer(11));
        
        String r1, r2;
        int root1, root2;
        
        //get the root note from the strings describing the chords
        if(chord1.length() == 1) 
            r1 = chord1;
        else if(chord1.charAt(1) == '#' || chord1.charAt(1) == 'b')
            r1 = chord1.substring(0, 2);
        else
            r1 = chord1.substring(0,1);
        
        if(chord2.length() == 1)
            r2 = chord2;
        else if(chord2.charAt(1) == '#' || chord2.charAt(1) == 'b')
            r2 = chord2.substring(0, 2);
        else
            r2 = chord2.substring(0,1);
        
        if(roots.get(r1) == null) System.out.println("BOOBOBOBOB: " + r1);
        if(roots.get(r2) == null) System.out.println("BOOBOBOBOB: " + r2);
        
        
        root1 = (Integer)roots.get(r1);
        root2 = (Integer)roots.get(r2);
        
        return root1 - root2;
    }
    
    public boolean containsStarter() {
        for(int i = 0; i < mDataPoints.size(); i++) {
            if(mDataPoints.get(i).isStarter())
                return true;
        }
        return false;
    }
    
    public Polylist getClusterMembersPolylist(){
        Polylist rhythmPL = Polylist.list("rhythmList");
  
        DataPoint[] clusterMembers = (DataPoint[]) getDataPoints().toArray(new DataPoint[getDataPoints().size()]);
              
        for(int i = 0; i < clusterMembers.length; i++){
            rhythmPL = rhythmPL.addToEnd(clusterMembers[i].getRelativePitchPolylist());
        }
        
       
        return rhythmPL;
    }
    
    public Polylist selectivelyGetClusterMembersPolylist(ArrayList<Polylist> excludeList){
        Polylist rhythmPL = Polylist.list("rhythmList");

  
        DataPoint[] clusterMembers = (DataPoint[]) getDataPoints().toArray(new DataPoint[getDataPoints().size()]);
        
        
        for(int i = 0; i < clusterMembers.length; i++){
            Polylist relativePitchPL = clusterMembers[i].getRelativePitchPolylist();
                             
            if(!excludeList.contains(relativePitchPL)){
                rhythmPL = rhythmPL.addToEnd(clusterMembers[i].getRelativePitchPolylist());
            }
        }
        
       
        return rhythmPL;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    cluster of size " + mDataPoints.size());
        for( Enumeration<DataPoint> e = mDataPoints.elements(); e.hasMoreElements(); )
        {
            buffer.append("    ");
            buffer.append(e.nextElement().toString());
            buffer.append("\n");
        }
        return buffer.toString();
    }
}