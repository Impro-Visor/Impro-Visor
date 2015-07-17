/*
 * ReadListenerLinkedList.java 0.1.0.3 24th January 2001
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

import jm.music.data.Score;

/**
 * A singularly linked list storing ReadListeners
 *
 * @author Adam Kirby
 * @version 1.0,Sun Feb 25 18:44
 */
class ReadListenerLinkedList {
    /** ReadListener stored at this node of the list */
    private ReadListener listener;

    /** Reference to the next ReadListenerLinkedList in the list */
    private ReadListenerLinkedList next;

    /**
     * Consturcts a new list with one node
     *
     * @param listener  the first ReadListener in the list
     */
    public ReadListenerLinkedList(ReadListener listener) {
        this.listener = listener;
        next = null;
    }

    /**
     * Returns the next ReadListenerLinkedList in the list.
     *
     * @return  next ReadListenerLinkedList in the list
     */
    public ReadListenerLinkedList getNext() {
        return next;
    }

    /**
     * Returns the ReadListener associated with this entry in the list
     *
     * @return  ReadListener at this node in the list
     */
    public ReadListener getListener() {
        return listener;
    }

    /**
     * Appends <CODE>l</CODE> to the end of the list.  If <CODE>l</CODE> is null
     * this method does nothing.
     *
     * @param l ReadListener to add to the list.
     */
    public void add(ReadListener l) {
        if (l == null) {
            return;
        }
        if (next == null) {
            next = new ReadListenerLinkedList(l);
        }
        next.add(l);
    }

    /**
     * Removes <CODE>l</CODE> from the list if found.  
     *
     * @param l ReadListener to remove from the list.
     */
    public void remove(ReadListener l) {
        if (next == null) {
            return;
        }
        if (l == next.getListener()) {
            next = next.getNext();
        }
    }

    /**
     * Triggers the score read notification of all listeners in the list
     *
     * <P>The listeners are notified according to LILO (Last In Last Out)
     * ordering.  Thus, the first in the list updates the score, then passes its
     * updated version to the next in the list, and it gets updated and passed
     * on until the last listener to be added gets the score.
     *
     * @param score Score being imported
     * @return      Score updated by listeners
     */
    public Score scoreRead(Score score) {
        if (listener == null) {
            return score;
        }
        if (next == null) {
            return listener.scoreRead(score);
        }
        return next.scoreRead(listener.scoreRead(score));
    }

	public void startedReading(){
		if (listener == null) {
			return;
		}
		if (next == null) {
			listener.startedReading();
			return;
		}
		listener.startedReading();
		next.startedReading();
	} 
    /**
     * Triggers the finished reading notification of all listeners in the list
     *
     * <P>The listeners are notified according to LILO (Last In Last Out)
     * ordering.  That is, the first listener to added will execute, then the
     * second, and so forth until the all have.
     *
     * @param scores    Score array being imported
     * @return          Score array updated by listeners
     */
    public void finishedReading() {
        if (listener == null) {
            return;
        }
        if (next == null) {
            listener.finishedReading();
            return;
        }
        listener.finishedReading();
        next.finishedReading();
    }
}
