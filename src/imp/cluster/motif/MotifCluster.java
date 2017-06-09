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

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * @author Joseph Yaconelli
 */
public final class MotifCluster {
    
    
    private static final MotifCountComparer mcc = new MotifCountComparer(); // comparator for priority queue
    //NOTE: this is implemented in a sort of set like sense (aka never adds same element twice (that's the idea at least))
    private PriorityQueue<Motif> _melodies; // keeps track of most common Motif, used as Representative Motif
    private static final int QUEUE_SIZE = 10;  // initial capacity of queue (will grow)
    private ArrayList<Motif> _members; // al Motifs in MotifCluster instance
    private String _clusterName = null;
    
    /**
     * Creates new MotifCluster with given initial cluster size and cluster name
     * @param size Initial cluster size
     * @param clusterName Initial cluster name
     */
    protected MotifCluster(int size, String clusterName){
        _melodies = new PriorityQueue(size, mcc);
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
    
    
    protected MotifCluster(Motif initialMotif){
        this();
        this.addMotif(initialMotif);
        this.setClusterName(initialMotif.getCluster());
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
            m.incr();
            return false;
        }
        
        _members.add(m);
        return _melodies.offer(m);
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
    
    public final boolean removeOneOf(Motif m){
        if(this.contains(m) && m.getCount() > 1){
            m.decr();
            return true;
        }else {
            return this.removeMotif(m);
        }
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
     * @param m
     * @return 
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
    
    
    
    @Override
    public String toString(){
        String res = "MOTIF CLUSTER:\t" + this.getClusterName() + "\n";
        res += "REPRESENTATIVE MOTIF:\t" + this.getMotif().toString() + "\n";
        res = _members.stream().map((m)
                -> m.getCount() + "\t" + m.toString() + "\n").reduce(res, String::concat);
        return res;
    }
    

}
