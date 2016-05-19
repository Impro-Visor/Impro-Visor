/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.data.advice;

import imp.data.Key;
import imp.data.Note;
import polya.*;

/**
 *
 * @author keller
 */
public class AdviceForQuote
        extends AdviceForMelody
  {
//  public AdviceForQuote(String name, Polylist notes, String chordRoot, Key key,
//                        int[] metre, int profileNumber)
//    {
//    super(name, 0, notes, chordRoot, key, metre, null, profileNumber);
//    }
//
//  public AdviceForQuote(String name, int serial, Polylist notes,
//                        String chordRoot, Key key, int[] metre,
//                        int profileNumber)
//    {
//    super(name, serial, notes, chordRoot, key, metre, null, profileNumber);
//    }
//
  public AdviceForQuote(String name, int serial, Polylist notes,
                        String chordRoot, Key key, int[] metre,
                        Note firstNote, int profileNumber)
    {
    super(name, serial, notes, chordRoot, key, metre, firstNote, profileNumber);
    }

  }
