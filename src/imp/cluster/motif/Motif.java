/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.cluster.motif;

import imp.generalCluster.IndexedMelodyPart;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import polya.Polylist;

/**
 *
 * @author Joseph Yaconelli
 */
public class Motif {
    
    /**
     * TODO: Enforce connection between abstract melody index and cluster index
     * 
     */
    private IndexedMelodyPart _melody;
    private static ArrayList<String> melodies = new ArrayList<>();
    private int _melodyIndex = -1;
    
    private int _clusterIndex = -1;
    private static ArrayList<String> clusters = new ArrayList<>();
    
    private int _count = 0;
    private int _originInst = 0;
    
    private static ArrayList<Motif> members = new ArrayList<>();
    
    /**
     * Creates a motif using the given {@link IndexedMelodyPart}
     * @param melody the explicit melody for the motif
     * @param abstractMelody    abstract melody associated with motif
     * @param cluster the cluster from which the motif came
     * @see IndexedMelodyPart
     */
    protected Motif(IndexedMelodyPart melody, String abstractMelody, String cluster){
        this.setMelody(melody);
        this.setOriginIndex(melody.getIndex());
        this.incr();
        this.setCluster(cluster);
        this.setAbstractMelody(abstractMelody);
        
        members.add(this);
    }
    
    /**
     * Creates a motif using the given {@link IndexedMelodyPart} and
     *  initializes count to count
     * @param melody    the explicit melody for the motif
     * @param abstractMelody    abstract melody associated with motif
     * @param cluster   cluster from which motif came
     * @param count     number of times motif is used
     * @see IndexedMelodyPart
     */
    protected Motif(IndexedMelodyPart melody, String abstractMelody, String cluster, int count){
        this(melody, abstractMelody, cluster);
        
        this.setCount(count);
        
        
        
        
    }
    
    
    /********** FOR TESTING ******************************/
    /**
     * Prints all motifs using the {@link toString} method
     */
    public static void printMotifs(){
        members.forEach((m) -> {
            System.out.println(m);            
        });
    }

    /******** END FOR TESTING *****************************/
    
    /**
     * Increments and returns count
     * @return  the incremented count
     */
    public final int incr(){
        return ++_count;
    }
    
    public final int decr(){
        return ++_count;
    }
    
    /**
     * Renames the cluster that the motif belongs to
     * @param name  New name of cluster
     * @return Returns true if name changed, otherwise false
     */
    public boolean renameMotifCluster(String name){
        if(clusters.set(_clusterIndex, name).equals(name)){
            return false;
        }
        
        return true;
    }
    
    /**
     * Clears all cluster information from all instances and empties cluster array
     */
    public static void resetClusters(){
        
        for(Motif m : members){
            m._clusterIndex = -1;
        }
        
        clusters.clear();
        
    }
    
    /**
     * Clears all abstract melody information from all instances and empties abstract melody array
     */
    public static void resetAbstractMelodies(){
        
        for(Motif m : members){
            m._melodyIndex = -1;
        }
        
        melodies.clear();
        
    }
    
    /**
     * Resets cluster and abstract melody information from all instances and empties cluster, abstract melody, and members arrays
     * @see resetClusters
     * @see resetAbstractMelodies
     */
    public static void reset(){
        Motif.resetClusters();
        Motif.resetAbstractMelodies();
        
        //clear list of 'active' Motif instances
        Motif.members.clear();
    }
    
    
    public void delete(){
        members.remove(this);
    }
    
    public static void remove(Motif m){
        members.remove(m);
    }
    
        /**
     * Gets shallow copy of ArrayList&lt;String&gt; of cluster names
     * @return shallow copy of shared ArrayList&lt;String&gt; of cluster names
     */
    public static ArrayList<String> getClusters(){
        
        return (ArrayList<String>) clusters.clone();
        
    }
    
    /**
     * Gets shallow copy of ArrayList&lt;String&gt; of abstract melodies
     * @return shallow copy of shared ArrayList&lt;String&gt; of abstract melodies
     */
    public static ArrayList<String> getAbstractMelodies(){
        return (ArrayList<String>) melodies.clone();
    }
    
    
    /**
     * Returns name of abstract melody associated with motif
     * @return abstract melody associated with motif
     * @throws NoSuchElementException   if motif is not assigned to a valid abstract melody
     */
    public String getAbstractMelody(){
        try{
            return melodies.get(_melodyIndex);
        }catch (Exception e){
            throw new NoSuchElementException("Motif not assigned to valid abstract melody.");
        }
    }
    
    /**
     * Returns count
     * @return count (number of times motif referenced)
     */
    public int getCount(){
        return _count;
    }
    
    /**
     * Returns melody
     * @return Explicit melody for motif
     * @see IndexedMelodyPart
     */
    public IndexedMelodyPart getMelody(){
        return _melody;
    }
    
    /**
     * Returns index in song of original instance of motif
     * @return index in song of original instance of the motif
     */
    public int getOrigin(){
        return _originInst;
    }
    
    /**
     * Returns name of cluster motif is from
     * @return name of cluster the motif comes from
     * @throws NoSuchElementException if motif is not assigned to valid cluster
     */
    public String getCluster(){
        try{
            return clusters.get(_clusterIndex);
        }catch (Exception e){
            throw new NoSuchElementException("Motif not assigned to valid cluster.");
        }
    }
    
    /**
     * Sets count
     * @param count 
     * @throws IllegalArgumentException if count is negative
     * 
     */
    public final void setCount(int count){
        if(count >= 0){
            _count = count;
        }else{
            throw new IllegalArgumentException("Count must be nonnegative.");
        }
    }
    
    /**
     * Sets a new name for the cluster containing the element. Adds cluster name
     * to list of clusters in necessary.
     * @param cluster 
     */
    public final void setCluster(String cluster){
        if(!(clusters.contains(cluster))){
            clusters.add(cluster);
        }
        _clusterIndex = clusters.indexOf(cluster);
    }
    
    
    
    /**
     * Sets a new abstract melody for this Motif instance. Adds to list of
     * abstract melodies if necessary.
     * @param abstractMelody new abstract melody
     */
    public final void setAbstractMelody(String abstractMelody) {
        if(!(melodies.contains(abstractMelody))){
            melodies.add(abstractMelody);
        }
        _melodyIndex = melodies.indexOf(abstractMelody);

    }
    
    
    
    /**
     * Sets original instance index of motif. NOTE: modifies melody to contain consistent information.
     * @param originIndex index of original motif
     * @throws IllegalArgumentException if count is negative
     * 
     */
    public final void setOriginIndex(int originIndex){
        if(originIndex >= 0){
            _originInst = originIndex;
            _melody.setIndex(originIndex);
        }else{
            throw new IllegalArgumentException("Original motif index must be nonnegative.");
        }
    }

    /**
     * Sets melody. NOTE: Does not change original index
     * @param melody    melody to set
     * @see IndexedMelodyPart
     */
    public final void setMelody(IndexedMelodyPart melody){
        _melody = melody;
    }
    
    /**
     * Sets all elements in static list of clusters to new list of names.
     * New list of cluster names must have same length as old list!
     * @param newClusters new list of cluster names.
     */
    public static void setClusters(String[] newClusters){
        if(newClusters.length != clusters.size()){
            System.out.println("Size of new ArrayList must equal size of old ArrayList. New size: "
                    + String.valueOf(newClusters.length) +
                    ". Original size: "
                    + String.valueOf(clusters.size()) + ".");
            throw new InputMismatchException("Size of new list must equal size of old list.");
        }
        clusters.clear();

        for(String s : newClusters){
            clusters.add(s);
        }
    }
    
    
    
    
    /**
     * Sets all elements in static list of abstract melodies to new list of abstract melodies.
     * New list of abstract melodies must have same length as old list!
     * @param newMelodies new list of abstract melodies.
     */
    public static void setAbstractMelodies(String[] newMelodies){
        if(newMelodies.length != melodies.size()){
            System.out.println("Size of new ArrayList must equal size of old ArrayList. New size: "
                    + String.valueOf(newMelodies.length) +
                    ". Original size: "
                    + String.valueOf(melodies.size()) + ".");
            throw new InputMismatchException("Size of new list must equal size of old list.");
        }
        melodies.clear();

        for(String s : newMelodies){
            melodies.add(s);
        }
    }
    
    
    
    /**
     * Converts Motif to string
     * @return format "[origin] -- [cluster name]"
     */
    @Override
    public String toString(){
        String str = String.valueOf(this.getOrigin());
        str += "\t" + this.getCluster();
        str += "\t" + this.getAbstractMelody();
        
        return str;
    }
    
    /**
     * Equality based on hash (abstract melody and cluster)
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        
        if (!Motif.class.isAssignableFrom(obj.getClass()))
            return false;
        
        final Motif other = (Motif) obj;
        
        return this.hashCode() == other.hashCode();
    }
    
    /**
     * Checks if Motif has the same abstract melody
     * @param m Motif to compare to
     * @return true if Motifs have the same abstract melody, otherwise false
     * @see getAbstractMelody
     */
    public boolean sameMelody(Motif m){
        if (m == null)
            return false;
        
        return m.getAbstractMelody().equalsIgnoreCase(this.getAbstractMelody());
    }
    
    /**
     * Checks if Motif is in the same cluster. <br/><b>NOTE:</b> "cluster" here refers
     *  to {@link Cluster}, and <b>NOT</b> {@link MotifCluster}
     * @param m Motif to compare to
     * @return true if the Motifs belong to the same cluster, otherwise false
     */
    public boolean sameCluster(Motif m){
        if(m == null)
            return false;
        
        return m.getCluster().equalsIgnoreCase(this.getCluster());
    }

    /**
     * Hash unique for unique pairs of abstract melody and cluster
     * @return hash for Motif
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + melodies.get(this._melodyIndex).hashCode();
        hash = 41 * hash + clusters.get(this._clusterIndex).hashCode();
        
        return hash;
    }
    
    /**
     * Creates grammar rule from abstract melody of Motif instance.
     * @return A Polylist describing the rule defined by the Motif instance
     * @see Polylist
     */
    public Polylist grammarRule(){
        
        String rule_name = this.getCluster();
        String rule_data = this.getAbstractMelody();
        
        Polylist rule = Polylist.list("rule", Polylist.PolylistFromString(rule_name), Polylist.PolylistFromString(rule_data), CreateMotifGrammar.MIN_PROB);
                
        return rule;
        
    }

    
}
