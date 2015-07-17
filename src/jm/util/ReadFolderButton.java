/*
 * ReadFolderButton.java 0.1.0.3 24th January 2001
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

import java.awt.Button;
import java.awt.Frame;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import jm.music.data.Score;

/**
 * A button which allows user to import a folder of MIDI and jMusic files.
 *
 * <P>After each successful import of a Score, any registered ReadListeners
 * are notified and can update and use the score read.  The listeners are
 * guaranteed to be notified in a LILO (Last In Last Out) order.  As an example,
 * if you wanted a Score quantised and then analysed, you would the quantising
 * ReadListener first, then the analysing one. 
 *
 * @author Adam Kirby
 * @version 1.0,Sun Feb 25 18:43
 */
public class ReadFolderButton extends Button {
    /** List of ReadListener's associated with this button */
    private ReadListenerLinkedList readListenerList;

    /**
     * Constructs a button for reading in a folder of MIDI and jMusic files.
     *
     * @param owner {@link Frame} which is the owner of this button.  Access to
     *              this <CODE>Frame</CODE> will be suspended when the user is
     *              selecting a music file and when error messages are
     *              displayed.
     */
    public ReadFolderButton(final Frame owner) {
        super("Read Folder");
        final FileDialog load = new FileDialog(owner,
                "Select a file to read all Midi and JMusic within that"
                + " file's folder", FileDialog.LOAD);
        final FilenameFilter filter = new ReadFilenameFilter();
        load.setFilenameFilter(filter);

        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                load.show();

                String directoryName = load.getDirectory();
                if (directoryName == null) {
                    return;
                }
                String[] filenames = new File(directoryName).list(filter);
                for (int i = 0; i < filenames.length; i++) {
                    Score score = Read.midiOrJmWithAWTMessaging(directoryName,
                                                                filenames[i],
                                                                owner);
                    if (score != null && readListenerList != null) {
                        readListenerList.scoreRead(score);
                    }
                }
                if (readListenerList != null) {
                    readListenerList.finishedReading();
                }
            }
        }
        );
    }

    /**
     * Registers a ReadListener to recieve successful read notifications
     *
     * @param l ReadListener to add
     *
     * @see #removeReadListener
     */
    public void addReadListener(ReadListener l) {
        if (l == null) {
            return;
        }
        if (readListenerList == null) {
            readListenerList = new ReadListenerLinkedList(l);
        } else {
            readListenerList.add(l);
        }
    }

    /**
     * Unregisters a ReadListener from recieving read notifications
     *
     * @param l ReadListner to remove
     *
     * @see #addReadListener
     */
    public void removeReadListener(ReadListener l) {
        if (readListenerList == null) {
            return;
        }
        if (readListenerList.getListener() == l) {
            readListenerList = readListenerList.getNext();
        }
    }
}
