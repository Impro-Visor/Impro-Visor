/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.io;
import mikera.vectorz.AVector;

/**
 * Interface DataSequence describes commands to retrieve sequential AVector data
 * @author Nicholas Weintraut
 */
public interface DataSequence {
    public AVector retrieve();
    public boolean hasNext();
    public int entrySize();
    public<T extends DataSequence> T copy();
}
