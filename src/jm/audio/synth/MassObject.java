/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

package jm.audio.synth;

import java.awt.*;

/* 
* The mass class for a spring mass netwwork
* @author Andrew Brown
*/

public class MassObject {
	/* weight of the mass */
    private double massSize = 1.0;
	/* reduce the amplitude over time */ 
    private double friction = 0.000003; 
    /** remember the previous force */
    private double inertia = 0.0;
    /* the number of time intervals since the last calulation */
    private double deltaTime = 1.0;
    /* the virtical pixel position of this mass */
    private double yPosition;
    
    //////////////////
    // constructors
    /////////////////
    public MassObject() {
            this(1.0);
    }
    
    public MassObject(double friction) {
            this(friction, 1.0);
    }
    
    public MassObject(double friction, double size) {
            this.massSize = size;
            this.friction = friction;
    }
	
    /** specify the virtical pixel location for the top of this mass */
    public void setYPosition(double newPos) {
        this.yPosition = newPos;
    }
    
    /** return the virtical pixel location of the top of this mass */
    public double getYPosition() {
        return this.yPosition;
    }
	
    public double getDisplacement(double force) {
        force += inertia; // add feedback loop to maintain momentum
        if ((inertia < 0.0 && friction > 0.0) ||  (inertia > 0.0 && friction < 0.0) ) {
        	friction = friction * -1;
        } 
        if (Math.abs(friction) > Math.abs(force)) {
         	inertia = 0.0;
        } else inertia = force - friction; // original math
        double accel = force/massSize;
        double displacement = accel / (deltaTime * deltaTime);
        
        return displacement;
    }
}
