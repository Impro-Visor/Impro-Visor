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

package jm.constants;

/** An interface storing program change constants.
  * 
  * @see jm.music.data.Note
  * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
  */
public interface ProgramChanges {
    
        public static final int
                PIANO = 0, ACOUSTIC_GRAND = 0,
                BRIGHT_ACOUSTIC = 1,
                ELECTRIC_GRAND = 2,
                HONKYTONK = 3, HONKYTONK_PIANO = 3,
                EPIANO = 4, ELECTRIC_PIANO = 4, ELPIANO = 4, RHODES = 4,
                EPIANO2 = 5, DX_EPIANO = 5,
                HARPSICHORD = 6,
                CLAV = 7, CLAVINET = 7,
                CELESTE = 8, CELESTA = 8,
                GLOCKENSPIEL = 9, GLOCK = 9,
                MUSIC_BOX = 10,
                VIBRAPHONE = 11, VIBES = 11,
                MARIMBA = 12,
                XYLOPHONE = 13,
                TUBULAR_BELL = 14, TUBULAR_BELLS = 14,
                ORGAN = 16, ELECTRIC_ORGAN = 16,
                ORGAN2 = 17, JAZZ_ORGAN = 17,
                ORGAN3 = 18, HAMMOND_ORGAN = 17,
                CHURCH_ORGAN = 19, PIPE_ORGAN = 19,
                REED_ORGAN = 20,
                ACCORDION = 21, PIANO_ACCORDION = 21, CONCERTINA = 21,
                HARMONICA = 22,
                BANDNEON = 23,
                NYLON_GUITAR = 24, NGUITAR = 24, GUITAR = 24, 
                                ACOUSTIC_GUITAR = 24, AC_GUITAR = 24,
                STEEL_GUITAR = 25, SGUITAR = 25,
                JAZZ_GUITAR = 26, JGUITAR = 26,
                CLEAN_GUITAR = 27, CGUITAR = 27, ELECTRIC_GUITAR = 27, 
                                EL_GUITAR = 27,
                MUTED_GUITAR = 28, MGUITAR = 28,
                OVERDRIVE_GUITAR = 29, OGUITAR = 29,
                DISTORTED_GUITAR = 30, DGUITAR = 30, DIST_GUITAR = 30,
                GUITAR_HARMONICS = 31, GT_HARMONICS = 31, HARMONICS = 31,
                ACOUSTIC_BASS = 32, ABASS = 32, 
                FINGERED_BASS = 33, BASS = 33, FBASS = 33, ELECTRIC_BASS = 33, 
                                EL_BASS = 33, EBASS = 33,
                PICKED_BASS = 34, PBASS = 34,
                FRETLESS_BASS = 35, FRETLESS = 35,
                SLAP_BASS = 36, SBASS = 36, SLAP = 36, SLAP_BASS_1 = 36, 
                SLAP_BASS_2 = 37, 
                SYNTH_BASS = 38, SYNTH_BASS_1 = 38,
                SYNTH_BASS_2 = 39,
                VIOLIN = 40,
                VIOLA = 41,
                CELLO = 42, VIOLIN_CELLO = 42,
                CONTRABASS = 43, CONTRA_BASS = 43, DOUBLE_BASS = 43,
                TREMOLO_STRINGS = 44, TREMOLO = 44,
                PIZZICATO_STRINGS = 45, PIZZ = 45, PITZ = 45, PSTRINGS = 45, 
                HARP = 46,
                TIMPANI = 47, TIMP = 47,
                STRINGS = 48, STR = 48, STRING_ENSEMBLE_1 = 48,
                STRING_ENSEMBLE_2 = 49,
                SYNTH_STRINGS = 50, SYNTH_STRINGS_1 = 50,
                SLOW_STRINGS = 51, SYNTH_STRINGS_2 = 51,
                AAH = 52, AHHS = 52, CHOIR = 52, 
                OOH = 53, OOHS = 53, VOICE = 53,
                SYNVOX = 54, VOX = 54, 
                ORCHESTRA_HIT = 55,
                TRUMPET = 56,
                TROMBONE = 57,
                TUBA = 58,
                MUTED_TRUMPET = 59,
                FRENCH_HORN = 60, HORN = 60,
                BRASS = 61,
                SYNTH_BRASS = 62, SYNTH_BRASS_1 = 62,
                SYNTH_BRASS_2 = 63,
                SOPRANO_SAX = 64, SOPRANO = 64, SOPRANO_SAXOPHONE = 64, 
                                SOP = 64,
                ALTO_SAX = 65, ALTO = 65, ALTO_SAXOPHONE = 65,
                TENOR_SAX = 66, TENOR = 66, TENOR_SAXOPHONE = 66, SAX = 66, 
                                SAXOPHONE = 66,
                BARITONE_SAX = 67, BARI = 67, BARI_SAX = 67, BARITONE = 67,
                                BARITONE_SAXOPHONE = 67, 
                OBOE = 68,
                ENGLISH_HORN = 69,
                BASSOON = 70,
                CLARINET = 71, CLAR = 71,
                PICCOLO = 72, PIC = 72, PICC = 72,
                FLUTE = 73,
                RECORDER = 74,
                PAN_FLUTE = 75, PANFLUTE = 75,
                BOTTLE_BLOW = 76, BOTTLE = 76,
                SHAKUHACHI = 77, 
                WHISTLE = 78,
                OCARINA = 79,
                GMSQUARE_WAVE = 80, SQUARE = 80,
                GMSAW_WAVE = 81, SAW = 81, SAWTOOTH = 81,
                SYNTH_CALLIOPE = 82, CALLOPE = 82, SYN_CALLIOPE = 82,
                CHIFFER_LEAD = 83, CHIFFER = 83,
                CHARANG = 84,
                SOLO_VOX = 85,
                FANTASIA = 88,
                WARM_PAD = 89, PAD = 89,
                POLYSYNTH = 90, POLY_SYNTH = 90,
                SPACE_VOICE = 91,
                BOWED_GLASS = 92,
                METAL_PAD = 93,
                HALO_PAD = 94, HALO = 94,
                SWEEP_PAD = 95, SWEEP = 95,
                ICE_RAIN = 96, ICERAIN = 96,
                SOUNDTRACK = 97,
                CRYSTAL = 98,
                ATMOSPHERE = 99,
                BRIGHTNESS = 100,
                GOBLIN = 101,
                ECHO_DROPS = 102, DROPS = 102, ECHOS = 102, ECHO = 102, 
                                ECHO_DROP = 102,
                STAR_THEME = 103,
                SITAR = 104,
                BANJO = 105,
                SHAMISEN = 106,
                KOTO = 107,
                KALIMBA = 108, THUMB_PIANO = 108,
                BAGPIPES = 109, BAG_PIPES = 109, BAGPIPE = 109, PIPES = 109,
                FIDDLE = 110,
                SHANNAI = 111,
                TINKLE_BELL = 112, BELL = 112, BELLS = 112,
                AGOGO = 113,
                STEEL_DRUMS = 114, STEELDRUMS = 114, STEELDRUM = 114,
                                STEEL_DRUM = 114,
                WOODBLOCK = 115, WOODBLOCKS = 115,
                TAIKO = 116, DRUM = 116,
                TOM = 119, TOMS = 119, TOM_TOM = 119, TOM_TOMS = 119,
                SYNTH_DRUM = 118, SYNTH_DRUMS = 118,
                REVERSE_CYMBAL = 119, CYMBAL = 119,
                FRETNOISE = 120, FRET_NOISE = 120, FRET = 120, FRETS = 120,
                BREATHNOISE = 121, BREATH = 121,
                SEASHORE = 122, SEA = 122, RAIN = 122, THUNDER = 122, 
                                WIND = 122, STREAM = 122, SFX = 122, 
                                SOUNDEFFECTS = 122, SOUNDFX = 122,
                BIRD = 123,
                TELEPHONE = 124, PHONE = 124,
                HELICOPTER = 125,
                APPLAUSE = 126,
                GUNSHOT = 127;
}