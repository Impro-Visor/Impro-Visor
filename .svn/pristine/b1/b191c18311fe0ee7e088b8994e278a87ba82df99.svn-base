/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.gui;

import imp.util.MidiManager;
import java.util.LinkedHashSet;
import javax.sound.midi.MidiDevice;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * MIDI device chooser, for preferences window within Notate
 * @author Martin Hunt, reworked by Robert Keller, in accord with updated Java
 */

public class MidiDeviceChooser
    extends AbstractListModel
    implements ComboBoxModel
{
private MidiManager midiManager;

private LinkedHashSet<MidiDevice.Info> devices;

private Object selectedItem = null;

private String nullIndicator = "none";

public MidiDeviceChooser(MidiManager midiManager, LinkedHashSet<MidiDevice.Info> devices)
  {
    this.midiManager = midiManager;
    this.devices = devices;
  }

public int getSize()
  {
    return devices.size();
  }

public Object getElementAt(int index)
  {
   Object array[] = devices.toArray();
   MidiDevice.Info o = (MidiDevice.Info)array[index];

    if( o == null )
      {
        return nullIndicator;
      }
    return o;
  }

public void setSelectedItem(Object anItem)
  {
    selectedItem = anItem;
  }

public Object getSelectedItem()
  {
    return selectedItem;
  }
            
}
