/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.nickd4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

/**
 *
 * @author cssummer16
 */
public class ReadUtilities {
    public static INDArray readNumpyCSV(String filePath)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            ArrayList<String[]> contents = new ArrayList<>();
            boolean reading = true;
            while(reading)
            {
                String line = reader.readLine();
                if(line != null)
                    contents.add(line.split(","));
                else
                    reading = false;
            }
            //will get an error if you don't have any data, so don't do that lol
            int[] shape = new int[]{contents.size(), contents.get(0).length};
            AMatrix readArray = Matrix.create(shape);
            for(int rowIndex = 0; rowIndex < contents.size(); rowIndex++){
                String[] line = contents.get(rowIndex);
                for(int colIndex = 0; colIndex < line.length; colIndex++){
                    readArray.set(new int[]{rowIndex, colIndex}, Double.valueOf(line[colIndex]));
                }
            }
            if(shape[1] == 1) {
                AVector readVector = readArray.asVector();
                return readVector;
            }
            else {
                return readArray;
            }
            
            
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
