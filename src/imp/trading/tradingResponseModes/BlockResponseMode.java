/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.Rest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Class BlockResponseMode is a TradingResponseMode which waits until signalFinish() is called before completing the future melody part
 * @author Nicholas Weintraut
 */
public abstract class BlockResponseMode extends TradingResponseMode {
    
    //futureResponse is a completable future; Accessing its value will cause a thread to wait until the value is initialized
    private CompletableFuture<MelodyPart> futureResponse; 
    
    public BlockResponseMode(String message) {
        super(message);
    }
    
    public void onStartTrading() {
        
    }
    
    /**
     * Generates an array of future melody parts of size 1, which will eventually contain the entire generated melody part, and waits until signalFinish is called to complete
     * @return 
     */
    @Override
    public Future<MelodyPart>[] generateResponseStructure()
    {
        //CompletableFuture won't evaluate until complete() is called
        futureResponse = new CompletableFuture();
        return (Future<MelodyPart>[]) new Future[]{futureResponse};
    }
    
    public MelodyPart getDefaultMelodyPart(){
        MelodyPart restMelody = new MelodyPart();
        restMelody.addRest(new Rest(responseInfo.getResponseChords().size()));
        return restMelody;
    }
    
    public void endGeneration() {
        //no support for ending generation on "instantaneous" trading modes yet
    }
    
    /**
     * On finish signal, completes the future melody part with value from generateResponse()
     */
    @Override
    public void signalFinish()
    {
        futureResponse.complete(generateResponse());
        System.out.println(generateResponse());
    }
    
    public abstract MelodyPart generateResponse();
    
    
    
}
