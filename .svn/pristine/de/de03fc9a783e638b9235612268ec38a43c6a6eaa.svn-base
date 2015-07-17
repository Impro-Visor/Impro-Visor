/*
 * ReadFilesJButton.java 0.1.1.0 20th February 2001
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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import jm.music.data.Score;

/**
 * A button which allows user to select MIDI and/or jMusic files to import.
 *
 * <P>After each successful import of a Score, any registered ReadListeners
 * are notified and can update and use the score read.  The listeners are
 * guaranteed to be notified in a LILO (Last In Last Out) order.  As an example,
 * if you wanted a Score quantised and then analysed, you would the quantising
 * ReadListener first, then the analysing one. 
 *
 * @author Adam Kirby
 * @version 1.0,Sun Feb 25 18:35:35  2001
 *
 * @see ReadListener
 */
public class ReadFilesJButton extends JButton {

    /**
     * Constant defining the state of a ReadFileJButton that reads in a
     * single jm or midi file.
     */
    public static final Mode SINGLE_FILE_MODE = new Mode("Single File");

    /**
     * Constant defining the state of a ReadFileJButton that reads in a
     * selection of jm and/or midi files.
     */
    public static final Mode MULTIPLE_FILES_MODE = new Mode("Multiple Files");

    /**
     * Constant defining the state of a ReadFileJButton that reads in a
     * folder of jm and/or midi files.
     */
    public static final Mode FOLDER_MODE = new Mode("Folder");

    /**
     * Class defining the states of a ReadFilesJButton
     *
     * @see {@link ReadFilesJButton#setMode
     */
    private static class Mode {
        /**
         * Unique identifier for instances of this class.
         */
        private final String name;

        /**
         * This constructor has the private access modifier.  Instances of this
         * class can only be accessed through the constants defined in this
         * class.  This ensures that no modes, other than the ones defined here,
         * can be created.
         *
         * @param name  Unique identifier for the instance
         */
        private Mode(String name) {
            this.name = name;
        }
    }

    /** The current import mode of this button */
    private Mode mode;

    /** List of ReadListener's associated with this button */
    private ReadListenerLinkedList readListenerList;

    /**
     * JFileChooser that generates the dialogs for selecting the music files
     * to import
     */
    private JFileChooser chooser = new JFileChooser();

    /**
     * Parent component for this button.  Control to this component will be
     * suspended when file chooser dialogs and error dialogs are displayed.
     */
    private Component owner;

    /**
     * Constructs a JButton for reading in MIDI or jMusic files.  Uses the
     * default mode of {@link #MULTIPLE_FILES_MODE} which allows
     * for a selection of files to be imported
     *
     * @param owner Component which is the owner of this button.  Access to this
     *              Component will be suspended when the user is selecting a 
     *              music file and when error messages are displayed.
     */
    public ReadFilesJButton(final Component owner) {
        this(owner, MULTIPLE_FILES_MODE);
    }

    /**
     * Constructs a JButton for reading in music files using the specified mode
     *
     * @param owner     Component which is the owner of this button.  Access to
     *                  this Component will be suspended when the user is
     *                  selecting a music file and when error messages are
     *                  displayed.
     * @param readMode  Mode specified the file/folder selection mode
     */
    public ReadFilesJButton(final Component owner, final Mode mode) {
        super();
        this.owner = owner;
        setMode(mode);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Runnable processRun = new Runnable() {
                    public void run() {
                        readListenerList.startedReading();

                        int chooserReturnValue = chooser.showOpenDialog(owner);
                        if (chooserReturnValue != chooser.APPROVE_OPTION) {
                            return;
                        }
        
                        if (mode == SINGLE_FILE_MODE) {
                            processFile(chooser.getSelectedFile());
                        } else if (mode == MULTIPLE_FILES_MODE) {
                            processFiles(chooser.getSelectedFiles());
                        } else if (mode == FOLDER_MODE) {
                            File file = chooser.getSelectedFile();
                            if (file.isDirectory()) {
                                /*
                                 * When jMusic supports only JDK1.2 and later,
                                 * the following code can be simplified to:
                                 *
                                 * processFiles(file.listFiles(
                                 *         new ReadFilenameFilter()));
                                 */
                                String[] filenames = file.list(
                                        new ReadFilenameFilter());
                                for (int i = 0; i < filenames.length; i++) {
                                    processFile(new File(file.getAbsolutePath(),
                                                         filenames[i]));
                                }
                            }
                        }
                             
                        if (readListenerList != null) {
                            readListenerList.finishedReading();
                        }
                    }
                };
                Thread processThread = new Thread(processRun, "processThread");
                processThread.start();
                
            }
        });
    }

    /**
     * Attempts to convert the <CODE>file</CODE> to a score, and if successful
     * notifies any registered {@link ReadListener ReadListeners}
     *
     * <P>The task of file reading and conversion is delegated to {@link
     * Read.midiOrJmWithSwingMessaging(File, Component)}.
     *
     * @param file  File to convert to a score
     *
     * @see #processFiles
     */
    private void processFile(final File file) {
        Score score = Read.midiOrJmWithSwingMessaging(file, owner);
        if (score == null) {
            return;
        }
        if (readListenerList != null) {
            score = readListenerList.scoreRead(score);
        }
    }

    /**
     * Attempts to convert the series of <CODE>files</CODE> to scores, notifying
     * any registered {@link ReadListener ReadListeners} after successful
     * imports.
     *
     * @param file  File to convert to a score
     *
     * @see #processFile
     */
    private void processFiles(final File[] files) {
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            processFile(files[i]);
        }
    }

    /**
     * Sets the mode of this button to <CODE>mode</CODE>
     *
     * @param mode  Mode for this button
     *
     * @see #getMode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == SINGLE_FILE_MODE) {
            setText("Read File");
            chooser.setDialogTitle("Select a MIDI or jMusic file to import");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else if (mode == MULTIPLE_FILES_MODE) {
            setText("Read Files");
            chooser.setDialogTitle("Select MIDI and/or jMusic files to import");
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else if (mode == FOLDER_MODE) {
            setText("Read Folder");
            chooser.setDialogTitle("Select a folder of MIDI or jMusic files to "
                                   + "import");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
    }

    /**
     * Returns the current mode of this button
     *
     * @return  the current mode of this button
     *
     * @see #setMode
     */
    public Mode getMode() {
        return mode;
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
