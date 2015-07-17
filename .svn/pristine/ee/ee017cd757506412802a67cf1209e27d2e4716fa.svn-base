/**
 * NoteListException.java 0.1.6 8th November 2000
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

package jm.music.tools;

/**
 * Thrown by a method when a set of {@link jm.music.data.Note Notes} does not fit
 * its criteria.  The set of Notes might be a Vector stored within a {@link
 * jm.music.data.Phrase Phrase} or a array of Notes.
 *
 * <p> The actual problem with the Notes will be dependent on the function of
 * the method throwing this error.  For instance, a method might require that
 * the notes must contain at least one non-rest.  Another method might require
 * only that it has a least one Note, regardless of whether its a rest or not.
 *
 * <p> See the particular method for full details.
 *
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:34  2001
 *
 * @since   jMusic November 2000 Release
 */
public class NoteListException extends Exception {
    /**
     * Constructs a <code>NoteListException</code> with no 
     * detail message. 
     */
    public NoteListException() {
        super();
    }

    /**
     * Constructs a <code>NoteListException</code> class 
     * with the specified detail message. 
     *
     * @param s the detail message.
     */
    public NoteListException(String s) {
        super(s);
    }
}
