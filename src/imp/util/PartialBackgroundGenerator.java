/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.util;

import imp.data.MelodyPart;
import java.util.concurrent.Future;

/**
 * Class to handle generation of a lick in the background, with partial results
 * @author Daniel Johnson
 */
public interface PartialBackgroundGenerator {
    
    /**
     * Check if there is more output that needs to be generated.
     * @return True if there is more output to generate, false if it is done
     * processing the output
     */
    public boolean canLazyGenerateMore();
    
    /**
     * Get a Future for the next version of the partial output. When called,
     * returns a Future of MelodyPart representing the next version of the
     * output, and starts generating the next version.
     * 
     * Only one Future should be active at one time. Callers should call .get()
     * on the Future before attempting to generate more.
     * 
     * When the Future completes, it will contain a new version of the output.
     * Note that this includes previous output, as it may need to revise earlier
     * notes (for example by extending a note into a tie across the bar line).
     * For example, the first call to lazyGenerateMore may produce a Future that
     * completes with a 4-bar MelodyPart. The next call would then produce a
     * Future that completes with an 8-bar MelodyPart, where the first 4 bars
     * are the revised version of the first 4 bar output, and the next 4 bars
     * are new content.
     * @return A Future that will complete with a longer version of the desired
     * output.
     */
    public Future<MelodyPart> lazyGenerateMore();
}
