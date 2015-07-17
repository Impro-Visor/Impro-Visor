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
 You should have received a copy of the GNU General Public Licens
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package jm.music.data;

/**
* Creates a silent object that can be added to a phrase amongst Notes.
 * @author Andrew Brown, September 2003.
 */
public class Rest extends Note {
    /**
    * Simple constructor that creates a rest of one beat length.
     */
    public Rest(){
        this(1.0);
    }
    
    /**
     * A constructor that creates a new Rest with the specified rhythm value.
     * @param rhythmValue The number of beats long this rest is.
     */
    public Rest(double rhythmValue) {
        super(REST, rhythmValue);
    }
}