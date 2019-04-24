/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2019 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package imp.osc;
import oscP5.*;

/**
 *
 * @author Andy and Rachel
 */
public class MuseReceiver {
    
    MuseServer museServer;
    int recvPort = 5003;	

    public MuseReceiver() {
        museServer = new MuseServer();
	museServer.oscServer = new OscP5(museServer, recvPort);
    }
    
    public Long getMuseValue(Long grammarMode)
    {       
        // _muse-head-tilt grammar
        if (grammarMode == 0) {    
            double currentAccValue = museServer.getAccValue();

            if (currentAccValue < -0.2) {
                System.out.println("LEFT");
                return 0L;
            } else if (currentAccValue > 0.2) {
                System.out.println("RIGHT");
                return 1L;
            } else {
                System.out.println("MIDDLE");               
                return (long)Math.round(Math.random());
            }
        }
        
        // _muse-brainwave grammar
        else {
            double currentAlphaValue = museServer.getAlphaValue();
            double averageAlpha = museServer.getAverageAlpha();
            double standev = museServer.getSD();
            
            // Only outputs when calibration is complete
            if (averageAlpha != 0.0) {
            	double zscore = (currentAlphaValue - averageAlpha)/standev;
            	if (zscore < -1) {
                    System.out.println("HIGH");
                    return 2L;
                }
                else if (zscore >= -1 && zscore <= 1) {
                    System.out.println("MEDIUM");
                    return 1L;
                }
                else {
                    System.out.println("LOW");
                    return 0L;
                }
            } 
            else {
            	return -1L;
            }
        }
    }
}