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

/**
 * The gui package contains all of the classes relevant to the user interface and
 * the notation display.
 * <p>
 * The components are created with Java Swing, since Swing allows more creativity
 * and options in designing a GUI as well as more powerful methods for controlling
 * events. Swing also has been heavily adopted in the Java community, while strictly
 * AWT components have many depricated methods and much less documentation, helpful
 * references, and tutorials.
 * <p>
 * Notate is the main GUI class, and initializes a JFrame that contains a desktop-style
 * layout. In the layout is an internal frame that holds a JPanel to which a stave
 * is drawn.
 * <p>
 * Stave is the primary class for drawing a notation stave. It contains all of the methods
 * needed to resize and recalculate an array of construction lines. Stave extends JPanel,
 * and through it's paint() call the entire notation stave can be drawn.
 * <p>
 * StaveActionHandler handles all of the mouse movements and button presses. It handles
 * such actions as insertion/deletion of notes, dragging notes, dragging note pitch, and
 * some various keyboard commands. 
 * <p>
 * Images is an interface for loading images, and ToolkitImages does the actual loading
 * of images that are located in the directory /graphics.
 */
 
package imp.gui; 
