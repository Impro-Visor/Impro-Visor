/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

package imp.generalCluster.metrics.MetricListFactories;

import imp.generalCluster.metrics.DiversityIndex;
import imp.generalCluster.metrics.LongestNoteLength;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.NoteCount;
import imp.generalCluster.metrics.NumContourChanges;
import imp.generalCluster.metrics.Syncopation;

/**
 *
 * @author Lukas Gnirke
 */
public class RhythmMetricListFactory implements MetricListFactory{
    

    @Override
    public Metric[] getNewMetricList() {
        
        return (new Metric[]{
                            new NoteCount(1.0),
                            new Syncopation(2.0),
                            new DiversityIndex(1.0),
                            new NumContourChanges(1.0),
                            new LongestNoteLength(1.0)
                            });
    }

    @Override
    public int getNumMetrics() {
        return 5;
    }
    
}
