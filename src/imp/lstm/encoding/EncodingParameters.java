/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.encoding;

import imp.lstm.architecture.poex.PassthroughChordEncoder;
import imp.lstm.architecture.poex.PassthroughNoteEncoder;

/**
 *
 * @author cssummer16
 */
public class EncodingParameters {
    
    //must update
    public static final NoteEncoder noteEncoder = new PassthroughNoteEncoder();
    public static final ChordEncoder chordEncoder = new PassthroughChordEncoder();
}
