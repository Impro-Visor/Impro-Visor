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

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Jon Gillick
 */
public class ClusterHierarchy {

    private Vector<DataPoint> dataPoints;
    private Vector<Node> nodes;
    private Node root;
    
    public ClusterHierarchy(Vector<DataPoint> data) {
        dataPoints = data;
        Cluster all = new Cluster(dataPoints);
        root = new Node(all,0);
    }

    public ClusterHierarchy(Node r) {
        root = r;
        dataPoints = r.getDataPoints();
    }
    
    public void cluster() {
        expand(root);
    }
    
    public void expand(Node r) {
        //the number of datapoints in the cluster at this node
        int size = r.getSize();
        //System.out.println("size: "+size);
        //System.out.println("Num DataPoints: " + size + " Level: " + getLevel(r));
        
        if(size <= 1) return;        
        
        //if the cluster at this node has more than 1 point, split this node into two clusters
        JCA jca = new JCA(2, size/2 + 1, r.getDataPoints());
        jca.startAnalysis();
        Cluster[] clusters = jca.getClusterOutput();
        
        int lev = getLevel(r);
        
        Node[] theseNodes = new Node[2];
        for(int i = 0; i < theseNodes.length; i++) {
            theseNodes[i] = new Node(clusters[i], lev + 1);
        }        
        r.setLeftChild(theseNodes[0]);
        r.setRightChild(theseNodes[1]);
        
        expand(r.getLeftChild());
        expand(r.getRightChild());
    }
    
    public int getLevel(Node n) {
        if(n.equals(root))
            return 0;
        
        int counter = 1;
        while( !(n.getParent() == root) )  {
            counter++;
            n = n.getParent();
        }
        return counter;
    }
    
    public Node getRoot() {
        return root;
    }
    
    public Vector<DataPoint> getDataPoints() {
        return dataPoints;
    }
    
    //returns a Vector of the leaves of the tree starting at the root
    public Vector<Node> getNodesAtLowestLevel() {
        Vector<Node> theseNodes = new Vector<Node>();
        
        theseNodes = getNodesAtLowestLevel(root);
        
        return theseNodes;
    }
       
    //returns a Vector of the leaves of the tree with root r
    public Vector<Node> getNodesAtLowestLevel(Node r) {
        Vector<Node> theseNodes = new Vector<Node>();
        if(r.getNumChildren() == 0) 
            theseNodes.add(r);
        else {
            if(r.hasLeftChild()) {
                theseNodes.addAll(getNodesAtLowestLevel(r.getLeftChild()));
            }
            if(r.hasRightChild()) {
                theseNodes.addAll(getNodesAtLowestLevel(r.getRightChild()));
            }
        }       
        return theseNodes;
    }
    
    public Vector<Node> getNodesAtNextLevelUp(Vector<Node> lowerNodes) {
        Vector<Node> theseNodes = new Vector<Node>();
        //get the parents of all nodes on this level
        for(int i = 0; i < lowerNodes.size(); i++) {
            Node parent = lowerNodes.get(i).getParent();
            if(! (theseNodes.contains(parent)))
                theseNodes.add(parent);          
        }
        //if a node is a descendant of any other, remove the descendent
        //for(int k = 0; k < 1; k++) {
       /* 
        Iterator it = theseNodes.iterator();
        Vector<Node> nodeCopy = (Vector<Node>) theseNodes.clone();
        Iterator it2 = nodeCopy.iterator();
        
        while (it.hasNext()) {
            Node current = (Node) it.next();
            while (it2.hasNext()) {
                Node otherNode = (Node) it2.next();
                if (current.isDescendantOf(otherNode)) {
                    current.markForRemoval();
                }
            }

        }
        
        it = theseNodes.iterator();
        while(it.hasNext()) {
            Node current = (Node)it.next();
            if (current.shouldBeRemoved()) {
                it.remove();
            }
        }
        */
        
        
        Collections.sort((List) theseNodes, new NodeComparer());
 
        for(int i = 0; i < theseNodes.size(); i++) {
            Node current = theseNodes.get(i);
            for(int j = i + 1; j < theseNodes.size(); j++) {
                Node other = theseNodes.get(j);
                if (other.isDescendantOf(current)) {
                    theseNodes.removeElementAt(j);
                    j--;
                }
            }
        }
        //}
         
        return theseNodes;
    }
    

        

}
