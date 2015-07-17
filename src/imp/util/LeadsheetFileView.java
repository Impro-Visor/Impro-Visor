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
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author mhunt
 */
public class LeadsheetFileView extends FileView {
    ImageIcon leadsheetIcon = new ImageIcon(getClass().getResource("/imp/gui/graphics/icons/leadsheet.png"));
    ImageIcon styleIcon = new ImageIcon("src/imp/gui/graphics/icons/goodpattern.png");

    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String extension = getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals("ls")) {
                type = "Impro-Visor Leadsheet";
            } else if (extension.equals("mid") || extension.equals("midi")){
                type = "MIDI File";
            } else if (extension.equals("voc")) {
                type = "Impro-Visor Vocabulary";
            } else if (extension.equals("prefs")) {
                type = "Impro-Visor Preferences";
            } else if (extension.equals("sty")) {
                type = "Impro-Visor Style";
            }          
        }
        return type;
    }

    public Icon getIcon(File f) {
        String extension = getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals("ls")) {
                icon = leadsheetIcon;
            } else if (extension.equals("mid") || extension.equals("midi")){
                icon = null;
            } else if (extension.equals("voc")) {
                icon = null;
            } else if (extension.equals("prefs")) {
                icon = null;
            } else if (extension.equals("sty")) {
                icon = styleIcon;

            }  
        }
        return icon;
    }
    
    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
