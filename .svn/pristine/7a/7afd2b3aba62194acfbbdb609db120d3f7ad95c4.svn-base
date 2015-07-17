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
 * AudioObjectException is a generic exception used for a range of AudioObject
 * excpeptions including incorrect number of inputs, unbalanced sample buffer 
 * return sizes etc.
 * @author Andrew Sorensen
 * @version 1.1 July 18:42:42  2001
 */

public final class AOException extends java.lang.Exception{
		private static String[] MESSAGES = new String[2];
	static{
		MESSAGES[0] = "Unbalanced number of returned samples from "+
			"multiple inputs.";
		MESSAGES[1] = "Wrong number of inputs for this AudioObject."; 
	}

	/**
	 * Simple Constructor which excepts a custom message
	 * @param name the name of the AudioObject throwing this exception
	 * @param message message to assign this Exception.
	 */
	public AOException(String name, String message){
		super(name+message);
	}

	/**
	 * This Constructor writes a standard message from the MESSAGES
	 * array using the int as an index.
	 * @param name the name of the AudioObject throwing this exception
	 * @param int message is the index to use to retrieve the stored message
	 */
	public AOException(String name, int message){
		super(name+MESSAGES[message]);
	}
}
		
