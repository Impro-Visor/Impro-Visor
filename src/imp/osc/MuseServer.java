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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Andy and Rachel
 */
public class MuseServer {
    
    OscP5 oscServer;

    double currentAccValue = 0.0;
    double currentAlphaValue = 0.0;

    int alphasSize = 220;
    public int windowSize = 10;
    ArrayList<Double> alphas = new ArrayList<Double>();
    Queue<Double> window = new LinkedList<Double>();
    
    double averageAlpha = 0.0;
    double standev = 0.0;
    double windowSum = 0.0;

    void oscEvent(OscMessage msg) {

        // Accelerometer Data
        if (msg.checkAddrPattern("/muse/acc") == true) {
            currentAccValue = msg.get(1).floatValue();
        }

        // Brain Data
    	if (msg.checkAddrPattern("/muse/elements/alpha_absolute") == true) {
            currentAlphaValue = msg.get(0).floatValue();
            updateWindow();

            // Saves a collection of alpha values to compute an average from (calibration phase)
            if (alphas.size() < alphasSize) {
                alphas.add(currentAlphaValue);
            } else {
                if (averageAlpha == 0.0) {
                    System.out.println("CALIBRATION COMPLETE");
                    calculateAverage();
                    calculateStandardDeviation();
                }
            }
    	}
    }

    void updateWindow() {
    	Double poppedSample = 0.0;
    	if (window.size() < windowSize) { //initializing queue
            window.add(currentAlphaValue);
            windowSum += currentAlphaValue;
    	}
    	else {
            window.add(currentAlphaValue);
            poppedSample = window.remove();
            windowSum += currentAlphaValue;
            windowSum -= poppedSample;
    	}
    }

    void calculateAverage() {
    	Double sum = 0.0;
    	for (Double alpha : alphas) {
            sum += alpha;
    	}
    	averageAlpha = sum / alphas.size();
    }

    void calculateStandardDeviation() {
    	Double sum = 0.0;
    	for (Double alpha : alphas) {
            sum += Math.pow((alpha - averageAlpha), 2);
    	}
    	standev = Math.sqrt(sum/alphas.size());
    }

    void resetCalibration() {
        alphas.clear();
        window.clear();
        averageAlpha = 0.0;
        standev = 0.0;
    }

    double getAccValue() { return currentAccValue; }

    double getAlphaValue() { return currentAlphaValue; }

    double getAverageAlpha() { return averageAlpha; }

    double getSD() { return standev; }

    double getWindowSum() { return windowSum; }

}
