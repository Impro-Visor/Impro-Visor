/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000-2003 Andrew Sorensen & Andrew Brown

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

package jm.constants;

/** An interface storing tuning constants.
  * 
  * @see jm.music.data.Note
  * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
  */
public interface Tunings {
    
        public static final double[]
                
                EQUAL = {1.0, 1.059, 1.122, 1.189, 1.26, 1.335, 1.414, 
                         1.498, 1.587, 1.682, 1.782, 1.888},

                JUST = {1.0, 1.067, 1.125, 1.2, 1.25, 1.333, 1.406, 1.5, 1.6, 
                        1.667, 1.8, 1.875},

                PYTHAGOREAN = {1.0, 1.053, 1.125, 1.185, 1.265, 1.333, 1.404, 
                               1.5, 1.58, 1.687, 1.778, 1.898},

                MEAN = {1.0, 1.07, 1.118, 1.196, 1.25, 1.337, 1.398, 1.496, 
                        1.6, 1.672, 1.789, 1.869};

}