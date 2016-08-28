/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2015 Nicolas Froment (aka Lasconic).
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

import imp.trading.TradingResponseInfo;
import imp.data.MelodyPart;
import java.util.concurrent.Future;

/**
 * 
 * @author Zach Kondak
 * @author Nicholas Weintraut Redesign for concurrency and data streaming result
 */
public abstract class TradingResponseMode {

    protected TradingResponseInfo responseInfo;
    protected String message;
    
    TradingResponseMode(String message) {
        this.message = message;
    }
    
    public void setResponseInfo(TradingResponseInfo responseInfo)
    {
        this.responseInfo = responseInfo;
    }
    
    public abstract void onStartTrading();
    
    public abstract Future<MelodyPart>[] generateResponseStructure();
    
    public abstract void signalFinish();
    
    public abstract void endGeneration();
    
    public abstract MelodyPart getDefaultMelodyPart();
    
    public void printTradeType() {
        System.out.println(message);
    }
}
