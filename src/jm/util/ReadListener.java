/*
 * ReadListener.java 0.1.0.3 24th January 2001
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
 * The listener interface for receiving notification of scores being
 * successfully imported from a file.
 *
 * @author Adam Kirby
 * @version 1.0,Sun Feb 25 18:44
 */
public interface ReadListener {
    /**
     * Implementations of this method have the opportunity to alter the score
     * after each has been imported.  Additionally non-score related tasks can
     * be performed.
     *
     * @param score Score imported by the object initiating this event
     * @return      updated score to send back to the initiating object
     */
    public Score scoreRead(final Score score);

	public void startedReading();

    /**
     * Implementations of this method can alter all the scores imported by the
     * initiating object.  Additionally non-score related tasks can be
     * performed.
     *
     * @param scores    all Scores imported by the object initiating this event
     * @return          updated scores to send back to the initiating object
     */
    public void finishedReading();
}
