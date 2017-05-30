/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
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
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lstm.architecture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import mikera.arrayz.INDArray;

/**
 *
 * @author cssummer16
 */
public class NetworkConnectomeLoader {
    
    public void load(String meatFolderOrZipPath, Loadable network) throws InvalidParametersException, IOException{
        load(meatFolderOrZipPath, network, "", true);
    }
    
    public void refresh(String meatFolderOrZipPath, Loadable network, String filter) throws InvalidParametersException, FileNotFoundException, IOException{
        load(meatFolderOrZipPath, network,filter,false);
    }

    public void load(String meatFolderOrZipPath, Loadable network, String filter, boolean configure) throws InvalidParametersException, FileNotFoundException, IOException
    {
        String[] namesNotFound = null;
        int numEntries = 0;
        File meatFileOrFolder = new File(meatFolderOrZipPath);
        if(meatFileOrFolder.isDirectory())
        {
            File[] meatFiles = meatFileOrFolder.listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) { return !name.equals(".DS_Store") && name.contains(filter);}
                                });
            boolean[] found = new boolean[meatFiles.length];
            numEntries = meatFiles.length;
            
            if(configure){
                boolean configured = false;
                for(int i = 0; i < meatFiles.length; i++)
                {
                    if(meatFiles[i].getName().contains("config.txt")){
                        boolean configureSuccess = network.configure(imp.lstm.utilities.ReadWriteUtilities.readTextLine(new FileReader(meatFiles[i])));
                        if(configureSuccess) {
                            configured=true;
                            break;
                        } else
                            throw new InvalidParametersException("Parameters file contents were invalid for this type of network.");
                    }
                }
                if(!configured){
                    boolean configureSuccess = network.configure(null);
                    if (!configureSuccess) {
                        throw new InvalidParametersException("Parameters file contents were invalid for this type of network.");
                    }
                }
            }
            
            int numFound = 0;
            for(int i = 0; i < meatFiles.length; i++)
            {
                if(meatFiles[i].getName().contains("config.txt"))
                    continue;
                //System.out.println(meatFiles[i].getPath());
                found[i] = network.load(imp.lstm.utilities.ReadWriteUtilities.readNumpyCSVFile(meatFiles[i].getPath()), network.pathCdr(meatFiles[i].getName()).replaceFirst(".csv", ""));
                if(found[i])
                    numFound++;
            }
            namesNotFound = new String[meatFiles.length - numFound];
            int j = 0;
            for(int i = 0; i < found.length; i++)
            {
                if(!found[i])
                    namesNotFound[j++] = meatFiles[i].getPath();
            }
        }
        else if(meatFileOrFolder.isFile())
        {
            // Try to read it as a zip file of params
            ArrayList<String> namesNotFoundList = new ArrayList<String>();
            ZipInputStream zin;
            ZipEntry entry;
            if(configure){
                boolean configured = false;
                zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(meatFileOrFolder)));
                while((entry = zin.getNextEntry()) != null) {
                    if (entry.getName().contains("config.txt")) {
                        boolean configureSuccess = network.configure(imp.lstm.utilities.ReadWriteUtilities.readTextLine(new InputStreamReader(zin)));
                        if (configureSuccess) {
                            configured = true;
                            break;
                        } else {
                            throw new InvalidParametersException("Parameters file contents were invalid for this type of network.");
                        }
                    }
                }
                if(!configured){
                    boolean configureSuccess = network.configure(null);
                    if (!configureSuccess) {
                        throw new InvalidParametersException("Parameters file contents were invalid for this type of network.");
                    }
                }
            }
            zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(meatFileOrFolder)));
            while((entry = zin.getNextEntry()) != null) {
                if(entry.getName().contains(filter) && !(entry.getName().contains(".DS_Store")) && !entry.getName().contains("config.txt"))
                {
                    String loadPath = network.pathCdr(entry.getName()).replaceFirst(".csv", "");
                    Reader zreader = new InputStreamReader(zin);
                    boolean found = network.load(imp.lstm.utilities.ReadWriteUtilities.readNumpyCSVReader(zreader), loadPath);
                    if(!found)
                        namesNotFoundList.add(entry.getName());
                    numEntries++;
                }
            }
            namesNotFound = new String[namesNotFoundList.size()];
            namesNotFound = namesNotFoundList.toArray(namesNotFound);
        }
        else
        {
            throw new InvalidParametersException("No parameters file found.");
        }
        if(namesNotFound.length > 0)
            throw new InvalidParametersException("Parameters file contents were invalid for this type of network.", namesNotFound);
        
        // If the user selects a file with .ctome extension, but that file is not
        // an appropriately formatted .ctome file, the ZipInputStream will open
        // it but not find any entries. We know that any valid .ctome file must
        // have some entries, since those are the parameter files. So if the
        // number of found entries is 0, this must be an invalid parameters file.
        if(numEntries == 0)
            throw new InvalidParametersException("Parameters file was not a proper connectome file.", namesNotFound);
        
        // With the current Loadable interface, it is not possible to find out
        // if any parameters that should have been loaded weren't provided. This
        // means that if someone prepares a .ctome file with SOME valid
        // parameters but not all of them, the network may be loaded in an
        // invalid state. Right now, we assume this doesn't happen.
    }
}
