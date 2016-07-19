/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.filters;

import mikera.vectorz.AVector;
import lstm.encoding.EncodingParameters;

/**
 *  This is a filter to clean unnecessary bits out of output data based on the current note encoding's switch pairings
 * @author cssummer16
 */
public class NoteEncodingCleanFilter implements DataFilter {
    
    public AVector filter(AVector input)
    {
        return EncodingParameters.noteEncoder.clean(input);
    }
    
}
