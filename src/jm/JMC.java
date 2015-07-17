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

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/
package jm;

import jm.constants.Alignments;
import jm.constants.DrumMap;
import jm.constants.Dynamics;
import jm.constants.Frequencies;
import jm.constants.Noises;
import jm.constants.Panning;
import jm.constants.Pitches;
import jm.constants.ProgramChanges;
import jm.constants.RhythmValues;
import jm.constants.Scales;
import jm.constants.Tunings;
import jm.constants.Waveforms;

/**
 * JMConstants holds constant values used across the whole
 * jMusic system.
 * @see jm.music.data.Note
 * @author Andrew Sorensen with lots of help from Andrew Brown and Andrew Troedson
 */
public interface JMC extends RhythmValues, Pitches, Frequencies, Tunings,
                             Dynamics, Panning, ProgramChanges, DrumMap, Scales,
                             Waveforms, Noises, Alignments {
                                 
	//----------------------------------------------
	// Programming Constants
	//----------------------------------------------
	/** A constant used to toggle debugging information */
	public static final boolean DEBUG = false;
	/** A constant used to toggle the verbosity of output */
	public static final boolean VERBOSE = true;

	/** Constant for 8 bit */
	public static final int EIGHT_BIT = 127;
	/** Constant for 16 bit */
	public static final int SIXTEEN_BIT = 32767;
	/** Constant for 32 bit */
	public static final int THIRTY_TWO_BIT = 214748647;
	/** Constant for program changes */
	public static final int PROG_EVT = 748394;//Integer.MIN_VALUE + 1;
	/** Constant for tempo changes */
	public static final int TEMP_EVT = PROG_EVT + 1;
	/** Constant for key signature events */
	public static final int KEY_SIG_EVT = TEMP_EVT +1;
	/** Constant for time signature events */
	public static final int TIME_SIG_EVT = KEY_SIG_EVT + 1;
	/** Constant for no key signature */
	public static final int NO_KEY_SIGNATURE = Integer.MIN_VALUE;
	/** Constant for no key quality */
	public static final int NO_KEY_QUALITY = Integer.MIN_VALUE;
	/** Constant for no numerator */
	public static final int NO_NUMERATOR = Integer.MIN_VALUE;
	/** Constant for no denominator */
	public static final int NO_DENOMINATOR = Integer.MIN_VALUE;
	/** Constant for no instrument */
	public static final int NO_INSTRUMENT = -1;

	//----------------------------------------------
	// Audio constants
	//----------------------------------------------        
        /* modulation sources */
        public static final int AMPLITUDE = 0;
        public static final int FREQUENCY = 1;
        
        /* channels */
        public static final int MONO = 1;
        public static final int STEREO = 2;
        public static final int QUADRAPHONIC = 4;
        public static final int OCTAPHONIC = 8;
        // What name should be given to the class containing the above four?
        // jm.constants.SoundSystems perhaps?
    
	//----------------------------------------------
	// Data type constants
	//----------------------------------------------
	public static final int PITCH = 0;
	public static final int RHYTHM = 1;
	public static final int DYNAMIC = 2;
	public static final int PAN = 3;
        
}
