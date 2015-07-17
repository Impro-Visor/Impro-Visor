/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.gui;

import java.awt.Color;

/**
 * Used to provide end-blocks on the PianoRoll rows
 * @author keller
 */
public class PianoRollEndBlock extends PianoRollBar
{

static int END_BLOCK_LENGTH = 60;

static Color END_BLOCK_COLOR = Color.red;


 public PianoRollEndBlock(int row, int startSlot, int numSlots, PianoRoll pianoRoll)
    {
    super(row, startSlot, END_BLOCK_LENGTH, END_BLOCK_COLOR, END_BLOCK_COLOR, Color.black, 0, false, pianoRoll.getGrid(), pianoRoll);

    resizable = false;
    }
 
 public Color getTabColor()
  {
      return END_BLOCK_COLOR;
  }


}
