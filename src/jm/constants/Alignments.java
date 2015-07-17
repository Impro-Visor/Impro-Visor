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

import jm.music.data.Alignment;

/** An interface storing noise constants.
  * 
  * @see jm.music.data.Note
  * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
  */
public interface Alignments {
    
        public static final Alignment 
                START_TOGETHER = Alignment.START_TOGETHER,
                END_TOGETHER = Alignment.END_TOGETHER,
                AFTER = Alignment.AFTER,
                BEFORE = Alignment.BEFORE,
                CENTRE_ALIGN = Alignment.CENTRE_ALIGN,
                CENTER_ALIGN = Alignment.CENTRE_ALIGN,
                START_ON_CENTRE = Alignment.START_ON_CENTRE,
                START_ON_CENTER = Alignment.START_ON_CENTRE,
                END_ON_CENTRE = Alignment.END_ON_CENTRE,
                END_ON_CENTER = Alignment.END_ON_CENTRE,
                CENTRE_ON_START = Alignment.CENTRE_ON_START,
                CENTER_ON_START = Alignment.CENTRE_ON_START,
                CENTRE_ON_END = Alignment.CENTRE_ON_END,
                CENTER_ON_END = Alignment.CENTRE_ON_END;
   
}