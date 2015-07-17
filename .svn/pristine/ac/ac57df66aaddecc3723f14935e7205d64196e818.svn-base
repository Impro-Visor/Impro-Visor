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

public class StartRoundingFactor implements Constants
{
private int factor;
private String description;

public StartRoundingFactor(int factor, String description)
  {
    this.factor = factor;
    this.description = description;
  }

public String getDescription()
    {
    return description;
    }

public int getFactor()
    {
    return factor;
    }

@Override
public String toString()
  {
    return description;
  }

private static StartRoundingFactor commonFactors[] =
  {
      new StartRoundingFactor(  6, "1/16 note triplet (1/6 beat, 20 slots)"),
      new StartRoundingFactor(  4, "1/16 note (1/4 beat, 30 slots)"),
      new StartRoundingFactor(  3, "1/8 note triplet (1/3 beat, 40 slots)"),
      new StartRoundingFactor(  2, "1/8 note (1/2 beat, 60 slots)"),
      new StartRoundingFactor(  1, "1/4 note (1 beat, 120 slots)")
   };

public static StartRoundingFactor[] getFactors()
  {
    return commonFactors;
  }
}
