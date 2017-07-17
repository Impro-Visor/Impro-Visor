/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics.MetricListFactories;

import imp.generalCluster.metrics.AverageMaxSlope;
import imp.generalCluster.metrics.Consonance;
import imp.generalCluster.metrics.ExactStart;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.NoteCount;
import imp.generalCluster.metrics.NumSegments;
import imp.generalCluster.metrics.RestDuration;
import imp.generalCluster.metrics.StartBeat;

/**
 *
 * @author Lukas Gnirke
 */
public class DefaultMetricListFactory implements MetricListFactory {

    @Override
    public Metric[] getNewMetricList() {
        return (new Metric[]{
                            new ExactStart(1.0),
                            new Consonance(0.5),
                            new NoteCount(1.2),
                            new RestDuration(1.0),
                            new AverageMaxSlope(1.0),
                            new StartBeat(1.3),
                            new NumSegments(1.1)
                            });
    }

    @Override
    public int getNumMetrics() {
        return 7;
    }
    
}
