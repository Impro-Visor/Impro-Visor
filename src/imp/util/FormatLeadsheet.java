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

import imp.*;
import imp.data.*;
import imp.com.*;

import java.io.*;

/* This class will recurse through directories opening and resaving
 * leadsheets in the most current format.
 */

public class FormatLeadsheet {

    static FileFilter dirFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    static FileFilter lsFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.getName().endsWith(".ls");
        }
    };
    
    public static void main(String[] args) {
        ImproVisor.getInstance();
        String directory = "leadsheets";
        formatDirectory(new File(directory));
        System.exit(0);
    }

    public static void formatDirectory(File dir) {
        File[] dirs = dir.listFiles(dirFilter);
        File[] sheets = dir.listFiles(lsFilter);

        for(int i = 0; i < sheets.length; i++) {
            System.out.println("Sheet: " + sheets[i].getName());
            formatLeadsheet(sheets[i]);
        }

        for(int i = 0; i < dirs.length; i++) {
            System.out.println("Dir: " + dirs[i].getName());
            formatDirectory(dirs[i]);
        }
    }
    
    public static void formatLeadsheet(File file) {
        Score score = new Score();
        new OpenLeadsheetCommand(file, score).execute();
        new SaveLeadsheetCommand(file, score, true).execute();
    }
}
