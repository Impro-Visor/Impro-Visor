/*
 * LineGraphCanvas.java 0.0.1 1st September 2000
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

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Dimension;

/**
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43
 */
public class LineGraphCanvas extends GraphCanvas {
    protected boolean hasMusicChanged = true;

    protected Dimension preferredSize = new Dimension(800, 800);

    public LineGraphCanvas() {
        super();
    }

    public LineGraphCanvas(Statistics stats) {
        super(stats);
    }

    public LineGraphCanvas(Statistics[] statsArray) {
        super(statsArray);
    }

    public LineGraphCanvas(StatisticsList statsList) {
        super(statsList);
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void paintBuffer() {
        int size = 1000;
        double largestValue = 1000;
        for (int i = 0; i < statsList.size(); i++) {
            if (statsList.get(i).size() > size) {
                size = statsList.get(i).size();
            }
            if (statsList.get(i).largestValue() != Double.POSITIVE_INFINITY
                    && statsList.get(i).largestValue() > largestValue) {
                largestValue = statsList.get(i).largestValue();
            }
        }
        image = createImage(size, (int) largestValue);
        graphics = image.getGraphics();

        for (int i = 0; i < statsList.size(); i++) {
            graphics.setColor(new Color((float) Math.random(),
                                        (float) Math.random(),
                                        (float) Math.random()));
            for (int j = 1; j < statsList.get(i).size(); j++) {
                if (statsList.get(i).largestValue()
                        != Double.POSITIVE_INFINITY) {
                    if (i == 2) {             
                        graphics.drawLine((int) ((j - 1) * .5), (int) (statsList.get(i).get(j - 1) * 10000),
                                          (int) (j * .5), (int) (statsList.get(i).get(j) * 10000));
                    } else {
                        graphics.drawLine((int) ((j - 1) * .5), (int) (statsList.get(i).get(j - 1) * 300),
                                          (int) (j * .5), (int) (statsList.get(i).get(j) * 300));
                    }
                }
            }
        }
    }
}
