/*
 * Convert.java 0.0.1.1 10rd April 2001
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

package jm.util;

/**
 * Thrown to indicate some error while processes a Convert static method.
 * applets.
 *
 * @author Adam Kirby
 * @version 0.0.1.1, 10th April 2001
 */
public class ConversionException extends Exception {

    public ConversionException() {
        super();
    }

    public ConversionException(String string) {
        super(string);
    }
}

