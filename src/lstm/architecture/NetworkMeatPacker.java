/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class NetworkMeatPacker {
    
    public void pack (String meatFolderOrZipPath, Loadable network) throws InvalidParametersException, IOException{
        refresh(meatFolderOrZipPath, network, "");
    }
    
    public void refresh (String meatFolderOrZipPath, Loadable network, String filter) throws InvalidParametersException, FileNotFoundException, IOException
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
            int numFound = 0;
            for(int i = 0; i < meatFiles.length; i++)
            {
                //System.out.println(meatFiles[i].getPath());
                found[i] = network.load(lstm.nickd4j.ReadWriteUtilities.readNumpyCSVFile(meatFiles[i].getPath()), network.pathCdr(meatFiles[i].getName()).replaceFirst(".csv", ""));
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
            FileInputStream fis = new FileInputStream(meatFileOrFolder);
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while((entry = zin.getNextEntry()) != null) {
                if(entry.getName().contains(filter) && !(entry.getName().contains(".DS_Store")))
                {
                    String loadPath = network.pathCdr(entry.getName()).replaceFirst(".csv", "");
                    Reader zreader = new InputStreamReader(zin);
                    boolean found = network.load(lstm.nickd4j.ReadWriteUtilities.readNumpyCSVReader(zreader), loadPath);
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
