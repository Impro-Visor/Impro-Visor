/*
 * LineGraph.java 0.0.2 5th September 2000
 *
 * Copyright (C) 2000 Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.gui.graph;

import java.awt.ScrollPane;

/**
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43
 */
public class LineGraph extends ScrollPane {
    protected GraphCanvas graphCanvas;

    public LineGraph() {
        this(new Statistics());        
    }

    public LineGraph(Statistics stats) {
        super();
        graphCanvas = new LineGraphCanvas(stats);
        add(graphCanvas);
    }

    public LineGraph(Statistics[] statsArray) {
        super();
        graphCanvas = new LineGraphCanvas(statsArray);
        add(graphCanvas);
    }

    public LineGraph(StatisticsList statsList) {
        super();
        graphCanvas = new LineGraphCanvas(statsList);
        add(graphCanvas);
    }

    public void addStatistics(Statistics statistics) {
        graphCanvas.addStatistics(statistics);
    }
}
