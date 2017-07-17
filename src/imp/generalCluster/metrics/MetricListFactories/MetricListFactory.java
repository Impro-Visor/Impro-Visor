/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster.metrics.MetricListFactories;

import imp.generalCluster.metrics.Metric;

/**
 *
 * @author Lukas Gnirke
 */
public interface MetricListFactory {
    
    public Metric[] getNewMetricList();
    
    public int getNumMetrics();
    
}
