/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics.MetricListFactories;

import imp.data.ContourData;
import imp.generalCluster.metrics.AverageMaxSlope;
import imp.generalCluster.metrics.Consonance;
import imp.generalCluster.metrics.DiversityIndex;
import imp.generalCluster.metrics.ExactStart;
import imp.generalCluster.metrics.LongestNoteLength;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.NoteCount;
import imp.generalCluster.metrics.NumContourChanges;
import imp.generalCluster.metrics.NumSegments;
import imp.generalCluster.metrics.RestDuration;
import imp.generalCluster.metrics.StartBeat;
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
