/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;
import imp.data.Rest;
import imp.trading.TradingResponseInfo;
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
