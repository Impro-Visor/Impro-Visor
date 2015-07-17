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

package jm.constants;

/** An interface storing volume constants.
  * 
  * @see jm.music.data.Note
  * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
  */
public interface Volumes {
    
	public static final int
		SILENT = 0,
		PPP = 10,
		PP = 25, PIANISSIMO = 25,
		P = 50,
		MP = 60, MEZZO_PIANO = 60,
		MF = 70, MEZZO_FORTE = 70,
		F = 85, FORTE = 85, 
		FF = 100, FORTISSIMO = 100,
		FFF = 120;

}