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
package jm.audio;

 /**
  * This interface is informed whenever an audio chain completes
  * processing its current buffer.  The rate at which these buffers are
  * processed (the size of the buffer) creates the control rate.  The
  * control rate gives real time audioLines the chance to make adjustments
  * to audio controls or enter new note information into their associated
  * instrument.  
  * @author Andrew Sorensen 
  * @version 1.0,Sun Feb 25 18:42:43  2001
  */
public interface AudioChainListener{
	/**
	 * Control change is called whenever an instrument fills its sample
	 * buffer.  This effectively becomes the rate at which changes to an
	 * instrument can be made (including starting new notes).
	 */
	public abstract void controlChange(float[] buffer, int returned, boolean finished);
}
