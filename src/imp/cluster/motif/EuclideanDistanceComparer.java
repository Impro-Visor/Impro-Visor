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

import imp.cluster.DataPoint;
import java.util.Comparator;

/**
 *
 * @author Joseph Yaconelli
 */
public class EuclideanDistanceComparer implements Comparator<DataPoint> {

    @Override
    public int compare(DataPoint d1, DataPoint d2) {
        int res = 0;
        
        if(d1 == null || d2 == null)
            throw new NullPointerException("One of the motifs was null");
        
        if(d1.getCurrentEuDt() != d2.getCurrentEuDt())
            res = d1.getCurrentEuDt() > d2.getCurrentEuDt() ? 1 : -1;
        
        return res;
    }
    
}
