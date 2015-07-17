/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.util;

import java.io.File;
import javax.swing.filechooser.*;

/**
 *
 * @author research
 */
public class ProfileFilter extends FileFilter {
    public static String EXTENSION = "soloProfile";

    public boolean accept(File f) {

        if (f.isDirectory())
            return true;

        if (f.getName().endsWith(EXTENSION))
            return true;
        else
            return false;
    }


    public String getDescription() {
        return "Solo profile files";
    }
}
