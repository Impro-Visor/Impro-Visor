/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading.tradingResponseModes;

import imp.data.MelodyPart;

/**
 *
 * @author cssummer17
 */
public class CorrectRhythmTRM extends BlockResponseMode{
    
    public CorrectRhythmTRM(String message) {
        super(message);
    }

    
    @Override
    public MelodyPart generateResponse(){
        
        System.out.println("in generateResponse");
  
        responseInfo.correctRhythm();
        return responseInfo.getResponse();
    }
    
    public String toString(){
        return "Rhythm Helper";
    }
    
    
}
