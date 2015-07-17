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

package imp.util;

import java.io.File;
import java.beans.*;
import javax.swing.*;

// Use this to keep the filename constant as you traverse directories;  it doesn't
// work exceedingly well, but it's better than nothing.

public class SaveField extends JComponent implements PropertyChangeListener
{    
    JFileChooser fc;
    String name;
    
    public SaveField(JFileChooser fc, String name) {
        this.fc = fc;
        this.name = name;
        
        fc.addPropertyChangeListener(this);
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop))
            fc.setSelectedFile(new File(name));
        
        else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop) &&
                 !fc.getSelectedFile().isDirectory())
            name = fc.getSelectedFile().getName();
    }
}