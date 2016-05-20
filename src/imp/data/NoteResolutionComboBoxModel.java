/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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

import imp.midi.MIDIBeast;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author keller
 */
@SuppressWarnings("serial")

public class NoteResolutionComboBoxModel extends DefaultComboBoxModel
{
private static NoteResolutionComboBoxModel theModel = null;

private static int selectedIndex = 3;

public static NoteResolutionComboBoxModel getNoteResolutionComboBoxModel()
  {
    if( theModel == null )
      {
       theModel = new NoteResolutionComboBoxModel(); 
      }
    return theModel;
  }

private NoteResolutionComboBoxModel()
  {
    super(NoteResolutionInfo.getNoteResolutions());
  }
    
public static int getSelectedIndex()
  {
    return selectedIndex;
  }

public static void setSelectedIndex(int index)
  {
    selectedIndex = index;
    int newResolution = ((NoteResolutionInfo)theModel.getElementAt(index)).getSlots();
    MIDIBeast.setResolution(newResolution);
  }

public static int getResolution()
  {
    return ((NoteResolutionInfo)theModel.getElementAt(selectedIndex)).getSlots();
  }

}
