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

import java.util.*;

/**
 *
 * @author Jon Gillick
 */

public class ChainComparer implements Comparator {

    public int compare(Object a, Object b) {
        float[] c = (float[]) a;
        float[] d = (float[]) b;

        boolean first = false;
        boolean equals = true;
        
        for(int i = c.length-1; i >= 0 ; i--) {
            if(c[i] < d[i]){ 
                first = true;
                equals = false;
        }
            if(c[i] > d[i]){ 
                first = false;
                equals = false;
            }
        }
        
        if(equals) return 0;    
        else if(first) return -1;
        else return 1;
        
        
    }
}