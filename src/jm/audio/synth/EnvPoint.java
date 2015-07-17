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

/**
 * A trivial class to act as an Envelope cooridnate
 * x is the time axis (in beats) and y is the amplitude
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:47  2001
 */

public final class EnvPoint{
	/** y coordinate */
	public float y;
	/** x coordinate */
	public float x = (float)-1.0;

	public int X = -1;

	public EnvPoint(float x, float y){
		this.y = y;
		this.x = x;
	}

	public EnvPoint(int X, float y){
		this.y = y;
		this.X = X;
	}
}
