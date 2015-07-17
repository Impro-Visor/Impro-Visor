/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.roadmap;

/**
 * Contains the state of a roadmap. Used for undo/redo.
 * @author August Toman-Yih
 */
public class RoadMapSnapShot {
    /** Snapshot name */
    private String name;
    /** Stored roadmap */
    private RoadMap roadMap = new RoadMap();
    
    /**
     * Create a new snapshot
     * @param name snapshot name
     * @param roadMap state to store
     */
    public RoadMapSnapShot(String name, RoadMap roadMap)
    {
        this.name = name;
        this.roadMap = new RoadMap(roadMap);
    }
    
    /**
     * Get the stored roadmap
     * @return 
     */
    public RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    /**
     * Get the name of the snapshot
     * @return 
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * String printing
     * @return 
     */
    @Override
    public String toString()
    {
        return name;
    }
}
