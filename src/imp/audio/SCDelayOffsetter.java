/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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
package imp.audio;

//Imports
import imp.gui.Notate;
import imp.data.Score;
import imp.Constants;
import imp.util.Preferences;

/**
 * This class calculates necessary delay compensation based on user input.
 * 
 * The user inputs desired MIDI note delay compensation in terms of beats,
 * and Impro-Visor calculates the equivalent in milliseconds.
 * 
 * This is then set as Midirecorder delay.
 * 
 *
 * @author Anna Turner
 * @since June 24 2013
 */
public class SCDelayOffsetter {

    private Notate notate;
    private double tempo;
    private Score score;
    private int resolution;
    private double offsetSlots;//See determineOmniOffsetSlots for explanation

    /**
     * Constructor. Assigns variable values.
     *
     * @param notate same notate used everywhere
     */
    public SCDelayOffsetter(Notate notate1) {
        notate = notate1;
        score = notate.getScore();
        tempo = score.getTempo();
    }

    /**
     * Reads user delay offset input and translates it into delay in ms.
     * Accounts for tempo.
     *
     * @return latency in milliseconds
     */
    public double gatherDelay(double userDefinedBeatDelay) {
        //Calculate latency from beat offset in audioInLatency.
        double msPerBeat = (1 / tempo) * 60 * 1000;//min per beat times sec per min times ms per sec
        double userDefinedSecondMSLatency = msPerBeat * userDefinedBeatDelay;
        System.out.println("User defined is: " + Preferences.getAudioInLatency() + "msPerBeat is: " + msPerBeat);
        return userDefinedSecondMSLatency;
    }

    /**
     * UNUSED.
     * 
     * Calculates certain delay for each note when capturing audio through
     * SuperCollider.
     *
     * This UNUSED method does it by milliseconds rather than beats and is
     * replaced by the gatherDelay() method above.
     *
     * Due to snapping from note values, depending on the tempo, we can pull
     * each note back (so it is earlier on the score) by a certain value.
     *
     * Because each machine is different, we also allow the user to determine
     * another constant latency that is added on top of the regular midi
     * latency.
     */
    //@TODO *could* consolidate 'else = 0's.
    //@TODO finetune range boundaries?
    public double determineOffsetSlots() {
        //First, convert to ms/slot    
        double slotsPerBeat = (480 / (score.getMetre()[1]));//480 slots in whole note - see Constants.java
        double msPerSlot = 1 / (slotsPerBeat * tempo / 60 / 1000);//converting tempo to ms

        if (tempo < 50) {
            if (resolution >= 16) {
                offsetSlots = Constants.SIXTEENTH;
            } else {
                offsetSlots = 0;
            }
        } else if (tempo < 125) {
            if (resolution >= 8) {
                offsetSlots = Constants.EIGHTH;
            } else {
                offsetSlots = 0;
            }
        } else if (tempo < 145) {
            if (resolution >= 16) {
                offsetSlots = 3 * (Constants.SIXTEENTH);
            } else if (resolution < 16 && resolution >= 8) {
                offsetSlots = Constants.EIGHTH;
            } else {
                offsetSlots = 0;
            }
        } else {
            if (resolution >= 4) {
                offsetSlots = Constants.QUARTER;
            } else {
                offsetSlots = 0;
            }
        }

        offsetSlots = (offsetSlots * msPerSlot);

        return offsetSlots; //in ms.
    }
}