/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2016 Robert Keller and Harvey Mudd College XML export code
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
package imp.data.activeTrading;

import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.activeTrading.tradingResponseModes.*;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zach Kondak
 */
public class TradingResponseContoller {

    TradingResponseInfo responseInfo;
    TradingResponseMode responseMode;

    public TradingResponseContoller(Notate notate, int[] metre, int tradeLength) {
        responseInfo = new TradingResponseInfo(notate, metre, tradeLength);
    }

    public void updateResponse(MelodyPart response, ChordPart soloChords, ChordPart responseChords, int nextSection, Transform musician) {
        responseInfo.updateResponse(response, soloChords, responseChords, nextSection, musician);
    }

    public MelodyPart extractFromGrammarSolo(int startSlot, int slotLength) {
        return responseInfo.extractFromGrammarSolo(startSlot, slotLength);
    }

    public MelodyPart response(String tradeMode) throws ExceptionTradeModeNotFound {
        if (tradeMode.equals("Repeat and Rectify")) {
            responseMode = new RepeatAndRectifyTRM(responseInfo, tradeMode);
        } else if (tradeMode.equals("Modify and Rectify")) {
            responseMode = new ModifyAndRectifyTRM(responseInfo, tradeMode);
        } else if (tradeMode.equals("Use Transform")) {
            responseMode = new TransformTRM(responseInfo, tradeMode);
        } else if (tradeMode.equals("Use Abstract Melody")) {
            responseMode = new AbstractTRM(responseInfo, tradeMode);
        } else if (tradeMode.equals("Use Grammar")) {
            responseMode = new GrammarTRM(responseInfo, tradeMode);
        } else if (tradeMode.equals("Chop and Memorize")) {
            responseMode = new ChopAndMemorizeTRM(responseInfo, tradeMode);
        } else {
            throw new ExceptionTradeModeNotFound("TRADE MODE \"" + tradeMode + "\" NOT FOUND");
        }

        try {
            return responseMode.generateResponse();
        } catch (ExceptionGenerateResponseNotDefined ex) {
            Logger.getLogger(TradingResponseContoller.class.getName()).log(Level.SEVERE, null, ex);
            return new MelodyPart();
        }
    }

}
