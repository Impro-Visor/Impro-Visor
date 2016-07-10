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
 * A class to hold all Impro-Visor constants.
 * To use this interface, import imp.Constants and make sure your class
 * implements Constants.  Then you can just use BEAT, REST, etc. in your
 * code.
 * Includes additions by Ryan Wieghard to support Outside playing.
 */

public interface Constants {
        
    public static final int MAX_VOLUME = 127;
    public static final int NUM_CHANNELS = 16;
    public static final int MAX_BEATS_PER_BAR = 12;
    public static final int DEFAULT_BEATS_PER_BAR = 4;
    public static final int MAX_BEAT_DENOMINATOR = 32;
    public static final int DEFAULT_BEAT_DENOMINATOR = 4;
    
    /**
     * the number of slots in a single beat
     */
    
    public static final int BEAT = 120;
    public static final int THIRD_BEAT = 40;
    public static final int HALF_BEAT = 60;
    
    
    /**
     * CUSTOM is used to indicate custom voicings
     */
    
    public static final String CUSTOM = "fluid";
    
    
    /** Note: All of the following must have no fractional remainder,
     *  Hence no SIXTYFOURTH for example. SIXTYFOURTH_TRIPLET is possible
     *  in principle, but we don't have an image for it yet.
     */
    
    public static final int WHOLE                = 480; // slots in a whole note
    public static final int HALF                 = WHOLE/2;              // 240
    public static final int QUARTER              = WHOLE/4;              // 120
    public static final int EIGHTH               = WHOLE/8;              //  60
    public static final int SIXTEENTH            = WHOLE/16;             //  30
    public static final int THIRTYSECOND         = WHOLE/32;             //  15
    
    public static final int HALF_TRIPLET         = 2*HALF/3;             // 160
    public static final int QUARTER_TRIPLET      = 2*QUARTER/3;          //  80
    public static final int EIGHTH_TRIPLET       = 2*EIGHTH/3;           //  40
    public static final int SIXTEENTH_TRIPLET    = 2*SIXTEENTH/3;        //  20
    public static final int THIRTYSECOND_TRIPLET = 2*THIRTYSECOND/3;     //  10

    public static final int QUARTER_QUINTUPLET      = 4*QUARTER/5;       //  96
    public static final int EIGHTH_QUINTUPLET       = 4*EIGHTH/5;        //  48
    public static final int SIXTEENTH_QUINTUPLET    = 4*SIXTEENTH/5;     //  24
    public static final int THIRTYSECOND_QUINTUPLET = 4*THIRTYSECOND/5;  //  12

    public static final int DOTTED_HALF           = 3*HALF/2;            // 360
    public static final int DOTTED_QUARTER        = 3*QUARTER/2;         // 180
    public static final int DOTTED_EIGHTH         = 3*EIGHTH/2;          //  90
    public static final int DOTTED_SIXTEENTH      = 3*SIXTEENTH/2;       //  45

    public static final int FOUREIGHTIETH         = 1; // WHOLE/WHOLE    //   1
    public static final int TWOFORTIETH           = 2;                   //   2
    public static final int ONETWENTIETH          = 4;                   //   4
    public static final int SIXTIETH              = 8;                   //   8

    //added July 2014
    public static final int SIXTYFOURTH_TRIPLET = WHOLE/QUARTER_QUINTUPLET; // 5
   
  /**
   * Note: These must be in ascending order for binary search to work.
   * What about double dotted?
   */ 
  static int[] knownNoteValue =
    {
    0,
    THIRTYSECOND_TRIPLET,       //  10
    THIRTYSECOND_QUINTUPLET,    //  12
    THIRTYSECOND,               //  15
    
    SIXTEENTH_TRIPLET,          //  20
    SIXTEENTH_QUINTUPLET,       //  24
    SIXTEENTH,                  //  30
     
    EIGHTH_TRIPLET,             //  40
    DOTTED_SIXTEENTH,           //  45
    EIGHTH_QUINTUPLET,          //  48
    EIGHTH,                     //  60
    
    QUARTER_TRIPLET,            //  80
    DOTTED_EIGHTH,              //  90
    QUARTER_QUINTUPLET,         //  96
    QUARTER,                    // 120
    
    HALF_TRIPLET,               // 160
    DOTTED_QUARTER,             // 180
    HALF,                       // 240
    DOTTED_HALF,                // 360
    WHOLE                       // 480
   };
  
 /**
   * Note: These  must be in ascending order for binary search to work.
   * Rests are not dotted.
   */ 
  static int[] knownRestValue =
    {
    0,
    THIRTYSECOND_TRIPLET,       // 10
    THIRTYSECOND_QUINTUPLET,    // 12
    THIRTYSECOND,               // 15
    
    SIXTEENTH_TRIPLET,          // 20
    SIXTEENTH_QUINTUPLET,       // 24
    SIXTEENTH,                  // 30
    
    EIGHTH_TRIPLET,             // 40
    EIGHTH_QUINTUPLET,          // 48
    EIGHTH,                     // 60
    
    QUARTER_TRIPLET,            // 80
    QUARTER_QUINTUPLET,         // 96
    QUARTER,                    // 120
    
    HALF_TRIPLET,               // 160
    HALF,                       // 240
    
    WHOLE                       // 480
   };
  
  /**
   * These are the values that can be used in tuplets, either as rests or notes.
   * Note: These must be in ascending order for binary search to work.
   */ 
  static int[] knownTupletValue =
    {
    0,
    THIRTYSECOND_TRIPLET,       //  10
    THIRTYSECOND_QUINTUPLET,    //  12
    
    SIXTEENTH_TRIPLET,          //  20
    SIXTEENTH_QUINTUPLET,       //  24
     
    EIGHTH_TRIPLET,             //  40
    EIGHTH_QUINTUPLET,          //  48
    
    QUARTER_TRIPLET,            //  80
    QUARTER_QUINTUPLET,         //  96
    
    HALF_TRIPLET               // 160
   };
  
  static int knownResolutionValue [] =
    {
    WHOLE,                      //  480
    
    DOTTED_HALF,                //  360
    HALF,                       //  240
    HALF_TRIPLET,               //  160
    
    QUARTER,                    //  120
    QUARTER_TRIPLET,            //  80
    
    EIGHTH,                     //  60
    EIGHTH_TRIPLET,             //  40
    
    SIXTEENTH,                  //  30
    SIXTEENTH_TRIPLET,          //  20
    
    THIRTYSECOND,               //  15
    THIRTYSECOND_TRIPLET,       //  10
    
    SIXTYFOURTH_TRIPLET,        //  5     
    TWOFORTIETH,                //  2
    FOUREIGHTIETH               //  1
  };
  
  
   // was in MelodyPart.java 
   //  private static int[] knownNoteValue =
   // {0, 10, 12, 15, 20, 24, 30, 40, 45, 48, 50, 60, 80, 90, 100, 120, 160, 180,
   //        240, 360, 480
   //  private static int[] knownRestValue =
   //       {0, 10, 12, 15, 20, 24, 30, 40, 60, 80, 120, 160, 240, 480};

    public static int DEFAULT_DURATION = BEAT/2;	// eighth note value

    public static int[] DEFAULT_METRE = {4, 4};

    /**
     * Types of notes
     */

    public static final int CHORD_TONE    = 0;
    public static final int COLOR_TONE    = 1;
    public static final int APPROACH_TONE = 2;
    public static final int FOREIGN_TONE  = 3;
    
    /**
     * Colors for the notes
     */

    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
    
    /**
     * Enharmonics
     */    
    
    public static final int CSHARP = 0;
    public static final int DSHARP = 1;
    public static final int FSHARP = 2;
    public static final int GSHARP = 3;
    public static final int ASHARP = 4;
    
    /**
     * Which portions of the leadsheet to export
     */
    
    public static final int ALL = 0;
    public static final int CHORDS_ONLY = 1;
    public static final int MELODY_ONLY = 2;
    
    /**
     * the minimum allowed rhythm value (not fully implemented)
     */
    public static final int MIN_RHYTHM_VALUE = BEAT/12;

    /**
     * the value indicating a variable is out of bounds or invalid
     */
    public static final int OUT_OF_BOUNDS = -1;
    
    /**
     * the "pitch" of a rest
     */
    public static final int REST = -1;

    /**
     * enum indicating which stave the current window should display
     */
    public static enum StaveType {GRAND, TREBLE, BASS, AUTO, NONE};
    
    /**
     * a flag so a Note knows whether it should be sharp, flat, or natural
     */
    public static enum Accidental {NATURAL, SHARP, FLAT, NOTHING};

    /**
     * Positions of black notes in the chromatic scale (starting at 0)
     */

    public static final int[] blackNotes = {1, 3, 6, 8, 10};


    /** "minimum" key signature used by Impro-Visor is six flats (Gb) */

    public static final int MIN_KEY = -6;


    /** "maximum" key signature used by Impro-Visor is seven sharps (C#) */

    public static final int MAX_KEY = 7;
    
    public static final int MIN_TS = 1;
    public static final int MAX_TS = 12;


    /**
     * The number of semitones in an octave.
     */

    public static final int OCTAVE = 12;


    /**
     * The one-and-only symbol for no chord
     */

    public static String NOCHORD = "NC";


    /**
     * The bar (measure) symbol
     */

    public static String BARSTRING = "|";


    /**
     * Comma is also usable as a measure symbol
     */

    public static String COMMASTRING = ",";


    /**
     * The slash symbol, used for slash-chords and chord repetition
     */

    public static String SLASHSTRING = "/";


    /**
     * The common root for chord table lookup.
     */

    public static String CROOT = "C";

    /**
     * Lower case version of pitch class or root c.
     */

    public static String LCROOT = "c";

    /**
     * The default duration of a note when none is specified.
     */


    public static String DEFAULT_DURATION_MSG = "eighth note";

    /**
     * The midi number for middle C
     */

    static final int CMIDI = 60;


    /**
     * The string representing a rest
     */

    static final String REST_STRING = "r";


    /**
     * The string representing an eighth-rest
     */

    static final String EIGHTH_REST_STRING = "r8";


    /**
     * Input and output symbols used for chords and notes
     */

    public static final char SHARP     = '#';
    public static final char FLAT      = 'b';
    public static final char BAR       = '|';
    public static final char COMMA     = ',';
    public static final char SLASH     = '/';
    public static final char BACKSLASH = '\\';
    public static final char DOT       = '.';
    public static final char PLUS      = '+';
    public static final char MINUS     = '-';
    public static final char RESTCHAR  = 'r';

    /**
     * Modes of extracting stuff for transfer
     */

    public static enum ExtractMode {CELL, IDIOM, LICK, QUOTE, BRICK, CHORDS, MELODY, BOTH};


    /**
     * maps absolute notes to MIDI numbers
     */
    public static final int
        G9 = 127,
           GF9 = 126,
           FS9 = 126,
           F9 = 125,
           FF9 = 124,
           ES9 = 125,
           E9 = 124,
           EF9 = 123,
           DS9 = 123,
           D9 = 122,
           DF9 = 121,
           CS9 = 121,
           C9 = 120,
           CF9 = 119,
           BS8 = 120,
           B8 = 119,
           BF8 = 118,
           AS8 = 118,
           A8 = 117,
           AF8 = 116,
           GS8 = 116,
           G8 = 115,
           GF8 = 114,
           FS8 = 114,
           F8 = 113,
           FF8 = 112,
           ES8 = 113,
           E8 = 112,
           EF8 = 111,
           DS8 = 111,
           D8 = 110,
           DF8 = 109,
           CS8 = 109,
           C8 = 108,
           CF8 = 107,
           BS7 = 108,
           B7 = 107,
           BF7 = 106,
           AS7 = 106,
           A7 = 105,
           AF7 = 104,
           GS7 = 104,
           G7 = 103,
           GF7 = 102,
           FS7 = 102,
           F7 = 101,
           FF7 = 100,
           ES7 = 101,
           E7 = 100,
           EF7 = 99,
           DS7 = 99,
           D7 = 98,
           DF7 = 97,
           CS7 = 97,
           C7 = 96,
           CF7 = 95,
           BS6 = 96,
           B6 = 95,
           BF6 = 94,
           AS6 = 94,
           A6 = 93,
           AF6 = 92,
           GS6 = 92,
           G6 = 91,
           GF6 = 90,
           FS6 = 90,
           F6 = 89,
           FF6 = 88,
           ES6 = 89,
           E6 = 88,
           EF6 = 87,
           DS6 = 87,
           D6 = 86,
           DF6 = 85,
           CS6 = 85,
           C6 = 84,
           CF6 = 83,
           BS5 = 84,
           B5 = 83,
           BF5 = 82,
           AS5 = 82,
           A5 = 81,
           AF5 = 80,
           GS5 = 80,
           G5 = 79,
           GF5 = 78,
           FS5 = 78,
           F5 = 77,
           FF5 = 76,
           ES5 = 77,
           E5 = 76,
           EF5 = 75,
           DS5 = 75,
           D5 = 74,
           DF5 = 73,
           CS5 = 73,
           C5 = 72,
           CF5 = 71,
           BS4 = 72,
           B4 = 71,
           BF4 = 70,
           AS4 = 70,
           A4 = 69,
           AF4 = 68,
           GS4 = 68,
           G4 = 67,
           GF4 = 66,
           FS4 = 66,
           F4 = 65,
           FF4 = 64,
           ES4 = 65,
           E4 = 64,
           EF4 = 63,
           DS4 = 63,
           D4 = 62,
           DF4 = 61,
           CS4 = 61,
           C4 = 60,
           CF4 = 59,
           BS3 = 60,
           B3 = 59,
           BF3 = 58,
           AS3 = 58,
           A3 = 57,
           AF3 = 56,
           GS3 = 56,
           G3 = 55,
           GF3 = 54,
           FS3 = 54,
           F3 = 53,
           FF3 = 52,
           ES3 = 53,
           E3 = 52,
           EF3 = 51,
           DS3 = 51,
           D3 = 50,
           DF3 = 49,
           CS3 = 49,
           C3 = 48,
           CF3 = 47,
           BS2 = 48,
           B2 = 47,
           BF2 = 46,
           AS2 = 46,
           A2 = 45,
           AF2 = 44,
           GS2 = 44,
           G2 = 43,
           GF2 = 42,
           FS2 = 42,
           F2 = 41,
           FF2 = 40,
           ES2 = 41,
           E2 = 40,
           EF2 = 39,
           DS2 = 39,
           D2 = 38,
           DF2 = 37,
           CS2 = 37,
           C2 = 36,
           CF2 = 35,
           BS1 = 36,
           B1 = 35,
           BF1 = 34,
           AS1 = 34,
           A1 = 33,
           AF1 = 32,
           GS1 = 32,
           G1 = 31,
           GF1 = 30,
           FS1 = 30,
           F1 = 29,
           FF1 = 28,
           ES1 = 29,
           E1 = 28,
           EF1 = 27,
           DS1 = 27,
           D1 = 26,
           DF1 = 25,
           CS1 = 25,
           C1 = 24,
           CF1 = 23,
           BS0 = 24,
           B0 = 23,
           BF0 = 22,
           AS0 = 22,
           A0 = 21,
           AF0 = 20,
           GS0 = 20,
           G0 = 19,
           GF0 = 18,
           FS0 = 18,
           F0 = 17,
           FF0 = 16,
           ES0 = 17,
           E0 = 16,
           EF0 = 15,
           DS0 = 15,
           D0 = 14,
           DF0 = 13,
           CS0 = 13,
           C0 = 12,
           CF0 = 11,
           BSN1 = 12,
           BN1 = 11,
           BFN1 = 10,
           ASN1 = 10,
           AN1 = 9,
           AFN1 = 8,
           GSN1 = 8,
           GN1 = 7,
           GFN1 = 6,
           FSN1 = 6,
           FN1 = 5,
           FFN1 = 4,
           ESN1 = 5,
           EN1 = 4,
           EFN1 = 3,
           DSN1 = 3,
           DN1 = 2,
           DFN1 = 1,
           CSN1 = 1,
           CN1 = 0,
           g9 = 127,
           gf9 = 126,
           fs9 = 126,
           f9 = 125,
           ff9 = 124,
           es9 = 125,
           e9 = 124,
           ef9 = 123,
           ds9 = 123,
           d9 = 122,
           df9 = 121,
           cs9 = 121,
           c9 = 120,
           cf9 = 119,
           bs8 = 120,
           b8 = 119,
           bf8 = 118,
           as8 = 118,
           a8 = 117,
           af8 = 116,
           gs8 = 116,
           g8 = 115,
           gf8 = 114,
           fs8 = 114,
           f8 = 113,
           ff8 = 112,
           es8 = 113,
           e8 = 112,
           ef8 = 111,
           ds8 = 111,
           d8 = 110,
           df8 = 109,
           cs8 = 109,
           c8 = 108,
           cf8 = 107,
           bs7 = 108,
           b7 = 107,
           bf7 = 106,
           as7 = 106,
           a7 = 105,
           af7 = 104,
           gs7 = 104,
           g7 = 103,
           gf7 = 102,
           fs7 = 102,
           f7 = 101,
           ff7 = 100,
           es7 = 101,
           e7 = 100,
           ef7 = 99,
           ds7 = 99,
           d7 = 98,
           df7 = 97,
           cs7 = 97,
           c7 = 96,
           cf7 = 95,
           bs6 = 96,
           b6 = 95,
           bf6 = 94,
           as6 = 94,
           a6 = 93,
           af6 = 92,
           gs6 = 92,
           g6 = 91,
           gf6 = 90,
           fs6 = 90,
           f6 = 89,
           ff6 = 88,
           es6 = 89,
           e6 = 88,
           ef6 = 87,
           ds6 = 87,
           d6 = 86,
           df6 = 85,
           cs6 = 85,
           c6 = 84,
           cf6 = 83,
           bs5 = 84,
           b5 = 83,
           bf5 = 82,
           as5 = 82,
           a5 = 81,
           af5 = 80,
           gs5 = 80,
           g5 = 79,
           gf5 = 78,
           fs5 = 78,
           f5 = 77,
           ff5 = 76,
           es5 = 77,
           e5 = 76,
           ef5 = 75,
           ds5 = 75,
           d5 = 74,
           df5 = 73,
           cs5 = 73,
           c5 = 72,
           cf5 = 71,
           bs4 = 72,
           b4 = 71,
           bf4 = 70,
           as4 = 70,
           a4 = 69,
           af4 = 68,
           gs4 = 68,
           g4 = 67,
           gf4 = 66,
           fs4 = 66,
           f4 = 65,
           ff4 = 64,
           es4 = 65,
           e4 = 64,
           ef4 = 63,
           ds4 = 63,
           d4 = 62,
           df4 = 61,
           cs4 = 61,
           c4 = 60,
           cf4 = 59,
           bs3 = 60,
           b3 = 59,
           bf3 = 58,
           as3 = 58,
           a3 = 57,
           af3 = 56,
           gs3 = 56,
           g3 = 55,
           gf3 = 54,
           fs3 = 54,
           f3 = 53,
           ff3 = 52,
           es3 = 53,
           e3 = 52,
           ef3 = 51,
           ds3 = 51,
           d3 = 50,
           df3 = 49,
           cs3 = 49,
           c3 = 48,
           cf3 = 47,
           bs2 = 48,
           b2 = 47,
           bf2 = 46,
           as2 = 46,
           a2 = 45,
           af2 = 44,
           gs2 = 44,
           g2 = 43,
           gf2 = 42,
           fs2 = 42,
           f2 = 41,
           ff2 = 40,
           es2 = 41,
           e2 = 40,
           ef2 = 39,
           ds2 = 39,
           d2 = 38,
           df2 = 37,
           cs2 = 37,
           c2 = 36,
           cf2 = 35,
           bs1 = 36,
           b1 = 35,
           bf1 = 34,
           as1 = 34,
           a1 = 33,
           af1 = 32,
           gs1 = 32,
           g1 = 31,
           gf1 = 30,
           fs1 = 30,
           f1 = 29,
           ff1 = 28,
           es1 = 29,
           e1 = 28,
           ef1 = 27,
           ds1 = 27,
           d1 = 26,
           df1 = 25,
           cs1 = 25,
           c1 = 24,
           cf1 = 23,
           bs0 = 24,
           b0 = 23,
           bf0 = 22,
           as0 = 22,
           a0 = 21,
           af0 = 20,
           gs0 = 20,
           g0 = 19,
           gf0 = 18,
           fs0 = 18,
           f0 = 17,
           ff0 = 16,
           es0 = 17,
           e0 = 16,
           ef0 = 15,
           ds0 = 15,
           d0 = 14,
           df0 = 13,
           cs0 = 13,
           c0 = 12,
           cf0 = 11,
           bsn1 = 12,
           bn1 = 11,
           bfn1 = 10,
           asn1 = 10,
           an1 = 9,
           afn1 = 8,
           gsn1 = 8,
           gn1 = 7,
           gfn1 = 6,
           fsn1 = 6,
           fn1 = 5,
           ffn1 = 4,
           esn1 = 5,
           en1 = 4,
           efn1 = 3,
           dsn1 = 3,
           dn1 = 2,
           dfn1 = 1,
           csn1 = 1,
           cn1 = 0;

    public static final int TOTALPITCHES = G9 + 1;
    public static final int SEMITONES = 12;
    
    public static final int MIN_PITCH = 0;
    public static final int MAX_PITCH = 127;
    public static final int MIN_INTERVAL_SIZE = 0;
    public static final int MAX_INTERVAL_SIZE = 24;
    public static final int MIN_NOTE_DURATION = 32;
    public static final int MAX_NOTE_DURATION = 1;

    public static final int MODF = 5, 
                            MODC = 0,
                            MODG = 7,
                            MODD = 2,
                            MODA = 9,
                            MODE = 4,
                            MODB = 11;

    public static final int CBMAJOR = -7,
                            GBMAJOR = -6,
                            DBMAJOR = -5,
                            ABMAJOR = -4,
                            EBMAJOR = -3,
                            BBMAJOR = -2,
                            FMAJOR = -1,
                            CMAJOR = 0,
                            GMAJOR = 1,
                            DMAJOR = 2,
                            AMAJOR = 3,
                            EMAJOR = 4,
                            BMAJOR = 5,
                            FSMAJOR = 6,
                            CSMAJOR = 7;
    
    // Terminal values for the grammar
    public static final char T_NOTE = 'H',
			       T_CHORD = 'C',
                               T_SCALE = 'S',
			       T_COLOR = 'L',
			       T_APPROACH = 'A',
			       T_REST = 'R',
                               T_RANDOM = 'X',
                               T_OUTSIDE = 'Y',
                               T_EXPECTANCY = 'E',
                               T_BASS = 'B',
                               T_GOAL = 'G';
   
    public static final String NONE = "None";
    public static final String FIRST_SCALE = "Use_First_Scale";
    
    public static final int IGNORE = 0,
			    OVERWRITE = 1,
			    SAVE = 2;
    
    public static final String STYLE_EXTENSION = ".sty";
    
    public static final String VOICING_REDIRECT_PREFIX = "(uses ";
    public static final String GENERATED_VOICING_NAME = "generated";
    public static final String GENERATED_VOICING_TYPE = "closed";

    /**
     * Used to indicate score is to be played to end.
     */
    public static final int ENDSCORE = -1;
    
}
