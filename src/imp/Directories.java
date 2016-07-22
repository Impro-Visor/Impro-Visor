/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College
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

package imp;

/**
 * This is a single file with important directory names.
 * @author keller
 */

public class Directories 
{
/**
 * name of where Impro-Visor files will be stored in the user's directory.
 */
public static String improHome = "impro-visor-version-"
        + ImproVisor.version
        + "-files";
/**
 * Standard sub-directory for vocabulary
 */
public static String vocabDirName = "vocab";

/**
 * Standard sub-directory for dictionaries
 */
public static String dictionaryDirName = "vocab";

/**
 * Standard sub-directory for grammars
 */
public static String grammarDirName = "grammars"; // was "grammars";


/**
 * Standard sub-directory for transforms
 */
public static String transformDirName = "transforms";

/**
 * Standard sub-directory for transforms
 */
public static String fractalDirName = "fractals";

/**
 * Standard sub-directory for connectomes
 */
public static String connectomeDirName = "connectomes";

/**
 * Standard sub-directory for transforms
 */
public static String countsDirName = "counts";

/**
 * Standard sub-directory for soloist files
 */
public static String soloistDirName = "grammars"; // was "soloists";

/**
 * Name of standard sub-directory for Solo profile
 * optionally used in lick generation.
 */
public static String profileDirName = "grammars";


/**
 * Name of standard sub-directory for styles
 */
public static String styleDirName = "styles";

/**
 * Name of file for the StyleMixer.
 * 
 */
public static String styleMixerName = "_styleMixer.txt";

/**
 * Name of standard sub-directory for importing styles 
 * from combination of midi and leadsheet
 */
public static String styleExtractDirName = "styleExtract";

/**
 * Name of standard sub-directory for leadsheets 
 */
public static String leadsheetDirName = "leadsheets";

/**
 * Name of standard sub-directory for MIDI files 
 */
public static String midiDirName = "midi";

/**
 * Name of standard sub-directory for voicing files
 */
public static String voicingDirName = "voicings";

/**
 * Name of sub-directory stored in "vocab" for
 * neural network training data
 */
public static String nnetTrainingDataDirName = "nnet-training-data";

/**
 * Name of sub-director stored in "vocab" for
 * weights used for style recognition 
 */
public static String styleRecognitionDirName = "style-recognition-weights";

/**
 * Name of the error log file.
 */

static String errorLogFilename = "Impro-Visor-ErrorLog.txt";


}


