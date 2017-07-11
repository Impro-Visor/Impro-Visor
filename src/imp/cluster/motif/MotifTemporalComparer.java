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

import imp.cluster.IndexedMelodyPart;
import java.util.Comparator;

/**
 * {@link Comparator} for {@link Motif Motifs}, ordering based on the temporal order of Motifs in the original {@link IndexedMelodyPart}.
 * @author Joseph Yaconelli
 * @see Motif#getOrigin() 
 */
public class MotifTemporalComparer implements Comparator<Motif> {

    @Override
    public int compare(Motif m1, Motif m2) {
        
            int res = 0;
            
            int indx1 = ((Motif)m1).getOrigin();
            int indx2 = ((Motif)m2).getOrigin();
            
            if(indx1 > indx2){
                res = 1;
            }else if (indx2 > indx1){
                res = -1;
            }
            
            return res;    }
}
