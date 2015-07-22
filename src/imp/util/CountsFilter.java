/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author muddCS15
 */
public class CountsFilter extends FileFilter{
    
    public static String EXTENSION = ".counts";
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        if (f.getName().endsWith(EXTENSION))
            return true;
        else
            return false;
    }

    @Override
    public String getDescription() {
        return "Counts files";
    }
    
}
