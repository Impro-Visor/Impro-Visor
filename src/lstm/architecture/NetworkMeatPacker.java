/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import mikera.arrayz.INDArray;

/**
 *
 * @author cssummer16
 */
public class NetworkMeatPacker {
    
    public String[] pack (String meatFolderPath, Loadable network)
    {
        String[] namesNotFound = null;
        try {
            File meatFolder = new File(meatFolderPath);
            if(meatFolder.isDirectory())
            {
                File[] meatFiles = meatFolder.listFiles(new FilenameFilter() {
                                        @Override
                                        public boolean accept(File dir, String name) { return !name.equals(".DS_Store");}
                                    });
                boolean[] found = new boolean[meatFiles.length];
                int numFound = 0;
                for(int i = 0; i < meatFiles.length; i++)
                {
                    //System.out.println(meatFiles[i].getPath());
                    found[i] = network.load(lstm.nickd4j.ReadUtilities.readNumpyCSV(meatFiles[i].getPath()), network.pathCdr(meatFiles[i].getName()).replaceFirst(".csv", ""));
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
                return namesNotFound;

            }
            else
            {
                throw new RuntimeException("File was not a directory!!!");
            }
            
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return namesNotFound;
    }
}
