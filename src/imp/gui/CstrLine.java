/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.gui;


/**
 * Holds information pertaining to the position of a construction line and 
 * whether or not a construction line has a note.
 *
 * @author      Aaron Wolin
 * @version     1.0, 8th July 2005
 */
public class CstrLine {
    
    /**
     * Default x position of the construction line
     */
    protected static int DEFAULT_X = -1;
    
    /**
     * Default x position of the construction line
     */
    protected static int DEFAULT_Y = -1;
    
    /**
     * Default construction line spacing
     */
    protected static int DEFAULT_SPACING = 30;
    
    /**
     * Default value for if a construction line has a note
     */
    protected static boolean DEFAULT_HASNOTE = false;
    
    /**
     * Default value for if a construction line has a chord
     */
    protected static boolean DEFAULT_HASCHORD = false;
    
    /**
     * The x-axis position of the construction line
     */
    protected int x;
    
    /**
     * The old x-position of the construction line
     */
    protected int oldX;
    
    /**
     * The y-axis position of the construction line
     */
    protected int y;
    
    /**
     * If a construction line has a note
     */
    protected boolean hasNote;
    
    /**
     * If a construction line has a chord
     */
    protected boolean hasChord;
    
    /**
     * The pixel width of the construction line
     */
    protected int spacing;
    
    
   /**
     * The pixel width of the construction line
     */
    protected int slotsPerDivision;
    
    
    /**
     * Default CstrLine constructor
     */
    public CstrLine(int slotsPerDivision) {
        this(DEFAULT_X, DEFAULT_Y, DEFAULT_HASNOTE, DEFAULT_HASCHORD, slotsPerDivision);
    }
    
    
    /** 
     * Constructs a CstrLine with a given x, y position and a given hasNote
     *
     * @param x             x-axis position for the construction line
     * @param y             y-axis position for the construction line
     * @param hasNote       true if the construction line has a note on it
     * @param hasChord       true if the construction line has a chord on it
     */
    public CstrLine(int x, int y, boolean hasNote, boolean hasChord, int slotsPerDivision) {
        this.x = x;
	this.oldX = x;
        this.y = y;
        this.hasNote = hasNote;
        this.hasChord = hasChord;
        this.spacing = DEFAULT_SPACING;
        this.slotsPerDivision = slotsPerDivision;
    }
        
    
    /**
     * Sets the x-axis position of the CstrLine
     * @param x             x-axis position for the construction line
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /** 
     * Set the previous position of the CstrLine.
     * Note that this is just used for calculating whole rest positions currently.
     */
    public void setOldX(int x) {
	this.oldX = x;
    }
    
    
    /**
     * Gets the x-axis position of the CstrLine
     * @return int          the x-axis position for the construction line
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the old x-axis position of the CstrLine
     */
    
    public int getOldX() {
	return oldX;
    }
    
    
    /**
     * Sets the y-axix position of the CstrLine
     * @param y             y-axis position for the construction line
     */
    public void setY(int y) {
        this.y = y;
    }
    
    
    /**
     * Gets the y-axix position of the CstrLine
     * @return int          the y-axis position for the construction line
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the amount of pixel spacing following the construction line
     * @param spacing       spacing following the construction line
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
    
    /**
     * Gets the amount of pixel spacing following the construction line
     * @return int          spacing following the construction line
     */
    public int getSpacing() {
        return spacing;
    }
    
    
     /**
     * Gets the number of rhythmic slots corresponding to the spacing
     * @return int          spacing following the construction line
     */
    public int getSlotsPerDivision() {
        return slotsPerDivision;
    }
    
    
   /**
     * Sets whether the CstrLine has a note or not
     * @param hasNote       boolean for if the construction line has a note
     */
    public void setHasNote(boolean hasNote) {
        this.hasNote = hasNote;
    }
    
    
    /**
     * Gets whether the CstrLine has a note or not
     * @return boolean      true if the construction line has a note
     */
    public boolean hasNote() {
        return hasNote;
    }
    
    
    /**
     * Sets whether the CstrLine has a chord or not
     * @param hasChord       boolean for if the construction line has a chord
     */
    public void setHasChord(boolean hasChord) {
        this.hasChord = hasChord;
    }
    
    
    /**
     * Gets whether the CstrLine has a chord or not
     * @return boolean      true if the construction line has a chord
     */
    public boolean hasChord() {
        return hasChord;
    }
}
