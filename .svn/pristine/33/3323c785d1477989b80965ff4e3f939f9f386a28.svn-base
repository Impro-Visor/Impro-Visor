/*
 * GraphCanvas.java 0.0.1 1st September 2000
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
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;

/**
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43
 */
public abstract class GraphCanvas extends Canvas {
    protected StatisticsList statsList;

    protected Image image;

    protected Graphics graphics;

    protected Dimension preferredSize = new Dimension(1, 1);

    protected Dimension minimumSize = new Dimension(1, 1);

    public GraphCanvas() {
        this(new Statistics());        
    }

    public GraphCanvas(Statistics stats) {
        statsList = new StatisticsList();
        statsList.add(stats);
    }

    public GraphCanvas(Statistics[] statsArray) {
        statsList = new StatisticsList(statsArray.length * 110 / 100);
        for (int i = 0; i < statsArray.length; i++) {
            statsList.add(statsArray[i]);
        }
    }

    public GraphCanvas(StatisticsList statsList) {
        this.statsList = statsList;
    }

    public Dimension getMinimumSize() {
        return this.minimumSize;
    }

    public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void addStatistics(Statistics stats) {
        statsList.add(stats);
    }

    protected abstract void paintBuffer();

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (image == null) {
            image = createImage(1, 1);
            graphics = image.getGraphics();
        }

        paintBuffer();

        g.drawImage(image, 0, 0, this);
    }
}
