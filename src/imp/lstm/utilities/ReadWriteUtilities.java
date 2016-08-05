/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

/**
 *
 * @author cssummer16
 */
public class ReadWriteUtilities {
    public static INDArray readNumpyCSVFile(String filePath)
    {
            return getINDArray(getContentsFromNumpyCSVFile(filePath));
    }
    
    public static INDArray readNumpyCSVString(String numpyString)
    {
        return getINDArray(getContentsFromNumpyCSVString(numpyString));
    }
    
    public static INDArray readNumpyCSVReader(Reader reader)
    {
            return getINDArray(getContentsFromNumpyCSVReader(reader));
    }
    
    
    public static INDArray getINDArray(List<String[]> contents)
    {
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
    }
    
    public static List<String[]> getContentsFromNumpyCSVReader(Reader input_reader)
    {
        try {
            BufferedReader reader = new BufferedReader(input_reader);
            
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
            return contents;
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return null;
    }
    
    public static List<String[]> getContentsFromNumpyCSVFile(String filePath)
    {
        try {
            return getContentsFromNumpyCSVReader(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return null;
    }
    
    public static List<String[]> getContentsFromNumpyCSVString(String numpyString)
    {
        ArrayList<String[]> contents = new ArrayList<>();
        String[] lines = numpyString.split("\n");
        
        
        for(int i = 0;  i < lines.length; i++)
        {
            
            contents.add(lines[i].split(","));
        }
        //System.out.println("Contents rows: " + contents.size());
        //System.out.println("Contents columns: " + contents.get(0).length);
        return contents;
    }
    
    public static String getNumpyCSVString(INDArray dataArray)
    {
        
        int[] shape = dataArray.getShapeClone();
        
        if(shape.length <= 2)
        {
            
            int numRows = (shape.length > 0) ? shape[0] : 0;
            int numCols = (shape.length > 1) ? shape[1] : 1;
            String[] stringData = new String[numRows];
            //System.out.println("about to loop string");
            for(int row = 0; row < numRows; row++)
            {
                String[] rowData = new String[numCols];
                for(int col = 0; col < numCols; col++)
                {
                    double currVal;
                    if(numCols > 1)
                        currVal = dataArray.get(row, col);
                    else
                        currVal = dataArray.get(row);
                    rowData[col] = Double.toString(currVal);
                }
                stringData[row] = String.join(",", rowData);
                //System.out.println(row);
            }
            //System.out.println("write rows: " + stringData.length);
            //System.out.println("final join");
            String finalData = String.join("\n", stringData);
            return finalData;
        }
        else
            throw new RuntimeException("Converting 3+dimensional arrays to numpy csv format unsupported.");

    }
}
