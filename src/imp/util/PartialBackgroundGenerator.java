/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.util;

import imp.data.MelodyPart;
import java.util.concurrent.Future;

/**
 *
 * @author cssummer16
 */
public interface PartialBackgroundGenerator {
    public boolean canLazyGenerateMore();
    public Future<MelodyPart> lazyGenerateMore();
}
