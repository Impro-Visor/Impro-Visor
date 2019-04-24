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
    
    static MuseServer museServer;
    static int recvPort = 5000;	

    public MuseReceiver() {
        museServer = new MuseServer();
	museServer.oscServer = new OscP5(museServer, recvPort);
    }
    
    public static Long getMuseValue(Long arg)
    {
        // Ask server for message
        // Parse the received message
        // Return relevant value
        
        // Uncomment for muse installation
        //double currentAccValue = museServer.getAccValue();
        
        // For testing purposes only, to simulate a muse device:
        // If the argument to this function is 1, it will return 0, 2, or 1
        // depending on whether a random value is low, high, or mid-range
        // respectively.
        //
        // If the argument to this function is not 1, it will return -1.
        
        double currentAccValue = 0.4*Math.random() - 0.2; // dummy for testing

        if( arg.equals(1L) )
          {
            if (currentAccValue < -0.1) {
                return 0L;
            } else if (currentAccValue > 0.1) {
                return 2L;
            } else {
                return 1L;
            }
          }
        else
          {
            return -1L;
          }

    }
}
