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

    public Long getMuseValue(Long grammarMode) {

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
            double averageAlpha = museServer.getAverageAlpha();
            double standev = museServer.getSD();
            double sampledAlpha = museServer.getWindowSum() / museServer.windowSize;

            // Only outputs when calibration is complete
            if (averageAlpha != 0.0) {
                double zscore = (sampledAlpha - averageAlpha)/standev;
                System.out.println("\nZ Score: " + zscore);
                System.out.println("Average Alpha: " + averageAlpha);
                System.out.println("Sampled Alpha: " + sampledAlpha);
                System.out.println("Standard Dev: " + standev);

                if (zscore < -2.0) {
                    System.out.println("VERY HIGH");
                    return 4L;
                }
                else if (zscore >= -2.0 && zscore <= -0.75)
                {
                    System.out.println("HIGH");
                    return 3L;
                }
                else if (zscore > -0.75 && zscore <= 0.70) {
                    System.out.println("MEDIUM");
                    return 2L;
                }
                else if (zscore > 0.70 && zscore <= 2.0) {
                    System.out.println("LOW");
                    return 1L;
                }
                else {
                    System.out.println("VERY LOW");
                    return 0L;
                }
            }
            else {
            	return -1L;
            }
        }
    }

    public void resetCalibration() {        
        museServer.resetCalibration();
    }
    
}
