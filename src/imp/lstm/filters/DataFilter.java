/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.filters;
import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public interface DataFilter {
    public AVector filter(AVector input);
}
