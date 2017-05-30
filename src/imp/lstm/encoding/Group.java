/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
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

package imp.lstm.encoding;

// groups are used for bundling start and end indices

import java.io.Serializable;
import java.util.HashMap;

    // (basically a tuple). Indices are [startIndex, endIndex)
public class Group implements Serializable{
        public final int startIndex;    // inclusive
        public final int endIndex;      // exclusive
        public final boolean oneHot;
        public HashMap<Integer, Group[]> offSwitches; //a group can specify indexes, that if 1, turn off all of another group's indexes
        
        public Group(int startIndex, int endIndex){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.oneHot = true;
        }
        
        public Group(int startindex, int endIndex, boolean oneHot)
        {
            this.startIndex = startindex;
            this.endIndex = endIndex;
            this.oneHot = oneHot;
        }
        
        public boolean isOneHot()
        {
            return oneHot;
        }
        
        public int length()
        {
            return endIndex - startIndex;
        }

        @Override
        public String toString(){
            return "start index: " + startIndex + " end index: " + endIndex;
        }
}
