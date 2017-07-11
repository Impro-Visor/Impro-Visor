/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
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

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Joseph Yaconelli
 */
public final class MotifCluster {
    
    
    private static final boolean TESTING = false;
    
    private static final MotifDistanceToCentroidComparer MDTCC = new MotifDistanceToCentroidComparer(); // comparator for _members TreeSet
    
    //NOTE: this is implemented in a sort of set like sense (aka never adds same element twice (that's the idea at least))
    private PriorityBlockingQueue<Motif> _melodies; // keeps track of most common Motif, used as Representative Motif
    private static final int QUEUE_SIZE = 10;  // initial capacity of queue (will grow)
    private ArrayList<Motif> _members; // al Motifs in MotifCluster instance
    private String _clusterName = null;
    private int _totalMotifsCount = 0;
    
    /**
     * Creates new MotifCluster with given initial cluster size and cluster name
     * @param size Initial cluster size
     * @param clusterName Initial cluster name
     */
    protected MotifCluster(int size, String clusterName){
        _melodies = new PriorityBlockingQueue(size, new MotifCountComparer());
        _members  = new ArrayList<>(size);
        _clusterName = clusterName;
    }
    
    /**
     * Creates new MotifCluster with default cluster size and cluster name {null}.
     */
    protected MotifCluster(){
        this(QUEUE_SIZE, null);
    }
    
    /**
     * Creates new MotifCluster with default cluster size and sets cluster name
     * @param clusterName name of cluster
     */
    protected MotifCluster(String clusterName){
        this(QUEUE_SIZE, clusterName);
    }
    
    /**
     * Creates new MotifCluster based on a "seed motif"
     * 
     * @param initialMotif "seed motif"
     */
    protected MotifCluster(Motif initialMotif){
        this();
        this.addMotif(initialMotif);
        this.setClusterName(initialMotif.getCluster());
        _totalMotifsCount++;
    }
    
    /**
     * Creates new MotifCluster, sets motif name, and populates cluster with an array of Motifs
     * @param initialMotifs array of Motifs to use
     * @param clusterName name of cluster
     * @see   Motif
     */
    protected MotifCluster(Motif[] initialMotifs, String clusterName){
        this(initialMotifs.length, clusterName);
        this.addMotifs(initialMotifs);
        
    }
    

    
    /**
     * Updates the cluster name to the cluster name of the Representative Motif
     * @return true update changed cluster name, false if cluster name did not change. 
     */
    public boolean updateName(){
        
        String temp = _clusterName;
        _clusterName = this.getMotif().getCluster();
        
        // true if update changed cluster name
        return !temp.equals(_clusterName); 
    }
    
    
    
    /**
     * Adds all motifs to MotifCluster using {@link addMotif}
     * @param motifs array of {@link Motif} to add.
     */
    public final void addMotifs(Motif[] motifs){
        for(Motif m : motifs){
            this.addMotif(m);
        }
    }
    
    /**
     * Adds a {@link Motif} to the MotifCluster. If Motif with same Abstract melody
     * already exists, it will just increase its count, otherwise adds it to cluster.
     * @param m Motif to add
     * @return true if Motif added, false if already existed.
     */
    public final boolean addMotif(Motif m){

        if(_melodies.contains(m)){
            
            Motif temp = _members.get(_members.indexOf(m));
            temp.incr();
            _melodies.remove(temp);
            _melodies.offer(temp);
            
            return false;
        }else{
            _members.add(m);
        }
        
        this.incr();
        return _melodies.offer(m);
    }
    
    /**
     * Increments the total motif count.
     * This will affect {@link MotifCluster#getTotalMotifsCount() getTotalMotifsCount()}
     */
    public final void incr(){
        _totalMotifsCount++;
    }
    
    /**
     * Decrements the total motif count.
     * If motif count is 0, then stays at 0.
     * This will affect {@link MotifCluster#getTotalMotifsCount() getTotalMotifsCount()}
     */
    public final void decr(){
        if(_totalMotifsCount > 0){
            _totalMotifsCount--;
        } else {
            _totalMotifsCount = 0;
        }
    }
    
    
    /**
     * Removes Motif from cluster
     * @param m Motif to remove
     * @return true if was in cluster and now removed, otherwise false.
     */
    public final boolean removeMotif(Motif m){
        
        boolean memRemoved = _members.remove(m);
        boolean melRemoved = _melodies.remove(m);
    
        return memRemoved && melRemoved;
    }
    
    /**
     * Decrements instance or removes if count == 1.
     * @param m Motif to remove 1 of
     * @return true if element decremented or removed. False if didn't exist.
     */
    public final boolean removeOneOf(Motif m){
        if(this.contains(m) && m.getCount() > 1){
            m.decr();
            return true;
        }else {
            return this.removeMotif(m);
        }
    }
    
    /**
     * Gives the cluster count (count of all Motifs in Cluster)
     * @return cluster count
     */
    public int getTotalMotifsCount(){
        return _totalMotifsCount;
    }
    
    /**
     * Checks if the MotifCluster contains a certain {@link Motif}
     * @param m The motif to check
     * @return true if MotifCluster contains Motif, otherwise false.
     */
    public boolean contains(Motif m){
        return this._members.contains(m);
    }
    
    /**
     * Gets {@link Motif} representative for MotifCluster.
     * <br>
     * <b>NOTE:</b>Representative is the {@link Motif} with the largest Count.
     * @return Motif representative for MotifCluster
     */
    public Motif getMotif(){
        return _melodies.peek();
    }
    
    /**
     * Gets Abstract Melody Representative for MotifCluster.
     * <br>
     * <b>NOTE:</b>Representative is the {@link Motif} with the largest Count.
     * @return Abstract Melody of Motif Representative for MotifCluster
     */
    public String getMelody(){
        return _melodies.peek().getAbstractMelody();
    }
    
    /**
     * Returns name of MotifCluster.
     * <br>
     * <b>NOTE</b>Cluster name might be {null}.
     * @return name of MotifCluster
     */
    public String getClusterName(){
        return _clusterName;
    }
    
    
    /**
     * Sets the representative {@link Motif} for MotifCluster.
     * Does this by setting the Motif's count to: the representative Motif's count + 1.
     * @param m Motif to set as representative Motif
     * @return {@code true} if representative motif is now {@code m}.
     */
    public boolean setMotif(Motif m){
        if(!this.contains(m)){
            this.addMotif(m);
        }

        m.setCount(this.getMotif().getCount() + 1);
        
        return m.getAbstractMelody().equalsIgnoreCase(this.getMotif().getAbstractMelody());
    }
    
    
    /**
     * Sets MotifCluster name
     * @param name Name to set
     */
    public final void setClusterName(String name){
        _clusterName = name;
    }
    
    
    /**
     * String representation contains information on each MotfiCluster and the Motifs contained within it.
     * @return The String representation
     */
    @Override
    public String toString(){
        String res = "MOTIF CLUSTER:\t" + this.getClusterName() + "\n";
        res += "REPRESENTATIVE MOTIF:\t" + this.getMotif().toString() + "\n";
        res = _members.stream().map((m)
                -> m.getCount() + "\t"  + m.getDistanceToCentroid() + "\t"+ m.toString() + "\n").reduce(res, String::concat);
        
        return res;
    }
    
    
    /**
     * Sets total motif count to the sum of all motif counts of Motifs in MotifCluster
     * @see imp.cluster.motif.Motif#getCount()
     */
    public void reCalcTotalCount(){
        int count = 0;
        
        count = _members.stream().map((m) -> m.getCount()).reduce(count, Integer::sum);
        
        _totalMotifsCount = count;
    }
    
    /**
     * Clears PriorityQueue and Members array of Motifs, then removes Motifs from Motif static collections.
     * @see Motif#reset()
     */
    void reset() {
        _melodies.clear();
        _members.clear();
    
        Motif.reset();
    }
    

}
