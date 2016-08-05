/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.io;
import imp.lstm.architecture.DataStep;

/**
 * Interface DataSequence describes commands to retrieve sequential AVector data
 * @author Nicholas Weintraut
 */
public interface DataSequence {
    public DataStep retrieve();
    public boolean hasNext();
    public int entrySize();
    public<T extends DataSequence> T copy();
}
