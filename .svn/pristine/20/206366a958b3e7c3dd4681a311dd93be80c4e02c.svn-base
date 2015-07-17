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

import java.util.Vector;

/**
 *
 * @author Jon Gillick
 */
public class Node {

    private Cluster cluster; 
    private Vector<DataPoint> dataPoints;
    private Node parent;
    private Node leftChild;
    private Node rightChild;
    private int level;
    private boolean toBeRemoved = false;
    
    public Node(Cluster c, int lev) {
        cluster = c;
        dataPoints = c.getDataPoints();
        parent = null;
        leftChild = null;
        rightChild = null;
        level = lev;
    }
    
    public Node(Vector<DataPoint> points, int lev) {
        dataPoints = points;
        cluster = new Cluster(dataPoints);
        parent = null;
        leftChild = null;
        rightChild = null;
        level = lev;
    }
    
    public void setLeftChild(Node n) {
        leftChild = n;
        n.setParent(this);
    }
    
    public void setRightChild(Node n) {
        rightChild = n;
        n.setParent(this);
    }
    
    public void setParent(Node n) {
        parent = n;
    }
    
    public Node getLeftChild() {
        return leftChild;
    }
    
    public Node getRightChild() {
        return rightChild;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public Cluster getCluster() {
        return cluster;
    }
    
    public Vector<DataPoint> getDataPoints() {
        return dataPoints;
    }
    
    public int getSize() {
        return dataPoints.size();
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean hasLeftChild() {
        return leftChild != null;
    }
    
    public boolean hasRightChild() {
        return rightChild != null;
    }
    
    public boolean hasNoLeftChild() {
        return leftChild == null;
    }
    
    public boolean hasNoRightChild() {
        return rightChild == null;
    }
    
    public boolean isRoot() {
        return parent == null;
    }
    
    public void markForRemoval() {
        toBeRemoved = true;
    }
    
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }
    
    public boolean isDescendantOf(Node otherNode) {
        Node n = this;
        
        while(! n.isRoot()) {
            n = n.parent;
            if (n.equals(otherNode))
                return true;
        }
        return false;
    }
    
    public int getNumChildren() {
        if(leftChild == null && rightChild == null) {
            return 0;
        }
        
        if(leftChild == null) {
            return 1 + rightChild.getNumChildren();
        }
        
        if(rightChild == null) {
            return 1 + leftChild.getNumChildren();
        }
        
        return 1 + leftChild.getNumChildren() + rightChild.getNumChildren();
    }
}
