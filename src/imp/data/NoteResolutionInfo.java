/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.data;

import imp.Constants;

/**
 * Members of this class are records that indicate the resolution of notes
 * of various slot lengths. One use of them is in MidiImportFrame.
 * @author keller
 */

public class NoteResolutionInfo implements Constants
{
private int slots;
private String description;

public NoteResolutionInfo(int slots, String description)
  {
    this.slots = slots;
    this.description = description;
  }

public String getDescription()
    {
    return description;
    }

public int getSlots()
    {
    return slots;
    }

public int getBeatSubdivisions()
  {
    return BEAT/slots;
  }

public int getWholeNoteSubdivisions()
  {
    return 4*BEAT/slots;
  }

@Override
public String toString()
  {
    return description + ", " + slots + " slots)";
  }

private static NoteResolutionInfo commonResolutions[] =
  {
      new NoteResolutionInfo(  1, "1/120 note (1/120 beat"),
      new NoteResolutionInfo(  2, "1/60 note (1/60 beat"),
      new NoteResolutionInfo(  5, "1/64 note triplet (1/24 beat"),
      new NoteResolutionInfo( 10, "1/32 note triplet (1/12 beat"),
      new NoteResolutionInfo( 15, "1/32 note (1/8 beat"),
      new NoteResolutionInfo( 20, "1/16 note triplet (1/6 beat"),
      new NoteResolutionInfo( 30, "1/16 note (1/4 beat"),
      new NoteResolutionInfo( 40, "1/8 note triplet (1/3 beat"),
      new NoteResolutionInfo( 60, "1/8 note (1/2 beat"),
      new NoteResolutionInfo( 80, "1/4 note triplet (2/3 beat"),
      new NoteResolutionInfo(120, "1/4 note (1 beat"),
      new NoteResolutionInfo(160, "1/2 note triplet (4/3 beat"),
      new NoteResolutionInfo(240, "1/2 note (2 beats"),
      new NoteResolutionInfo(360, "dotted 1/2 note (3 beats"),
      new NoteResolutionInfo(480, "whole note (4 beats")
  };

public static NoteResolutionInfo[] getNoteResolutions()
  {
    return commonResolutions;
  }
}
