/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.architecture;

import mikera.arrayz.INDArray;

/**
 *
 * @author cssummer16
 */
public interface Loadable {

    public static String SEPARATOR = "_";
    
    public boolean load(INDArray data, String loadPath);
    
    public default String pathCar(String loadPath)
    {
        return loadPath.replaceFirst(SEPARATOR + ".*", "");
    }
    
    public default String pathCdr(String loadPath)
    {
        return loadPath.replaceFirst("[^" + SEPARATOR + "]*" + SEPARATOR, "");
    }
}
