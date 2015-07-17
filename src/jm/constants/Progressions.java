/*
 * Chords.java 0.1.0.3 5th November 2004
 *
 * Copyright (C) 2004 David Turner
 *
 * <This Java Class is part of the jMusic API version 1.4, November 2004.>
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
package jm.constants;

public interface Progressions {

    /** common chord progression templates */
	public static final int[] I_IV_V7_I = { 0, 3, 4, 0 };
	public static final int[] ii_V7_I = { 1, 4, 0 };
	public static final int[] I_ii_iii_IV = { 0, 1, 2, 3 };
	public static final int[] i_iv_v = { 0, 3, 4 };
	public static final int[] ii_V_I = { 1, 4, 0 };
	
	//bridge progressions
	public static final int[] I_IV_II_V = {0,3,1,4};
	public static final int[] IV_I_IV_V = {3,0,3,4};
	public static final int[] IV_I_II_V = {3,0,1,4};
	public static final int[] IV_V = {3,4};
	
	//rock progressions
	public static final int[] I_IV_V= {0,3,4};
	
	//spanish, flamenco touch
//	public static final int[] i_VIIb_VIb_V = {
//	};
	
	//standard progression
	public static final int[] I_vi_ii_V = {0,5,1,4};


}