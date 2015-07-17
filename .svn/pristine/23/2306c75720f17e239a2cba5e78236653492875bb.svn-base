/*
 * AbstractReadButton.java 0.0.4 30th December 2000
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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.FileDialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;

import jm.midi.SMF;
import jm.music.data.Score;
import jm.util.Read;

/**
 * A abstract button class which handles reading of MIDI and jMusic files.
 *
 * This is designed for user interfaces.  If exceptions occur a dialog box is
 * created and displayed detailing the problem.
 *
 * If you want the errors to be transparent to your user, or you want to
 * manage exception-handling try using the {@link jm.util.Read} class instead.
 *
 * @author Adam Kirby
 * @version 1.0,Sun Feb 25 18:43:56  2001
 */
public abstract class AbstractReadButton extends Button {
    /**
     * The owner whose control is to be suspended when an error dialog or
     * FileDialog appears. 
     */
    protected Frame owner = null;

    /**
     * Checks to see whether the file specified is a valid JM or MIDI and if so
     * returns its data as a Score.  If an error is encountered a dialog box is
     * displayed describing the error, and the method returns null.
     *
     * @param directoryName String describing the directory structure of the
     *                      file to be read
     * @param fileName      String describing the full name of the file to be
     *                      read
     * @return              {@link Score} describing the music data in the file,
     *                      or null if an error was encountered.
     */
    public Score readFile(final String directoryName, final String fileName) {
        if (directoryName == null || fileName == null) {
            return null;
        }

        Score score = null;
        String message = null;

        /** Attempt to read file */
        try {
	    SMF smf = new SMF();
            score = new Score (fileName);
	    InputStream is = new FileInputStream(directoryName + fileName);
            smf.read(is);        
	    jm.midi.MidiParser.SMFToScore(score,smf);
        } catch (IOException e1) {
            message = e1.getMessage ();
            if (message == null) {
                message = "Unknown IO Exception";
            } else if (message.equals ("Track Started in wrong place!!!!"
                                       + "  ABORTING")) {
                message = "The MIDI file corrupted.  Track data started in the"
                          + " wrong place.";
            } else if (message.equals ("This is NOT a MIDI file !!!")) {
                try {
                    FileInputStream fis = new FileInputStream(directoryName
                                                              + fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    score = (Score) ois.readObject ();
                    ois.close ();
                    fis.close ();
                } catch (SecurityException e2) {
                    message = "Read access not allowed to " + fileName;
                } catch (ClassNotFoundException e2) {
                    message = "The file " + fileName
                              + " is neither a jm or MIDI file";
                } catch (ClassCastException e2) {
                    message = "The file " + fileName 
                              + " is neither a jm or MIDI file";
                } catch (StreamCorruptedException e2) {
                    message = "The file " + fileName 
                              + " is neither a jm or MIDI file";
                } catch (IOException e2) {
                    message = e2.getMessage ();
                    if (message == null) {
                        message = "Unknown Exception";
                    }
                }
            }
        }

        /** Display error if any */
        if (message != null) {
            final String finalMessage = message;
            new Dialog (owner, "Not a valid MIDI or jMusic File", true) {
                {
                    add (new Label (finalMessage));
                    addWindowListener (new WindowAdapter () {
                            public void windowClosing (WindowEvent evt) {
                                dispose ();
                            }
                        }
                    );
                    pack ();
                }
            }.show ();
        }

        return score;
    }
}
