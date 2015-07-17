/*
 * Prob.java 18th February 2003
 *
 * Copyright (C) 2003  Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *                             
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.music.tools;

import java.util.Random;
import jm.music.data.Note;

/** A utility class for generating random pitches, durations, etc in a gaussian
  * distribution.
  *
  * @author  Adam Kirby
  * @version $Revision: 1.1.1.1 $, $Date: 2009/05/30 07:56:50 $
  */
public final class Prob {

	/**
	  * The Prob class provides static methods and is not meant to be
	  * instansiated.
	  */
	private Prob() {
	}
        
        private static final Random RNG = new Random();
        
        /** Returns random pitches dispersed by a gaussian distribution around 
          * the mean specified.  Around 68% of the pitches returned will be no 
          * greater than the mean plus the standard deviation specified and no
          * less than the mean minus the standard deviation. Around 95% of 
          * pitches will be within twice this range.
          * 
          * @param meanPitch    the pitch that should be most commonly returned 
          *                     by this method.
          * @param stdDeviation the standard deviation measured in semitones.
          */
        public static final int gaussianPitch(final int meanPitch,  
                                              final int stdDeviation) {
                long nextPitch;
                do {
                        nextPitch = Math.round(
                               (RNG.nextGaussian() * stdDeviation) + meanPitch);
                } while (nextPitch < Note.MIN_PITCH 
                                || nextPitch > Note.MAX_PITCH);
                return (int) nextPitch;
        }
        
        /** Returns a random frequency dispersed by a gaussian distribution 
          * around the mean specified.  Around 68% of the frequencies returned 
          * will be no greater than the mean plus the standard deviation 
          * specified and no less than the mean minus the standard deviation. 
          * Around 95% of frequencies will be within twice this range.
          * 
          * @param meanFrequency values returned should average this frequency 
          * @param stdDeviation  the standard deviation. See method comment
          */
        public static final double gaussianFrequency(
                        final double meanFrequency, final double stdDeviation) {
                double nextFrequency;
                do {
                        nextFrequency = (RNG.nextGaussian() * stdDeviation) 
                                        + meanFrequency;
                } while (nextFrequency < Note.MIN_FREQUENCY);
                return nextFrequency;
        }
        
        /** Returns random rhythm values dispersed by a gaussian distribution 
          * around the mean specified.  Only rhythm values which are multiples 
          * of the <code>granularity</code> RV will be returned.  Thus if you 
          * specify a granularity of JMC.CROTCHET quavers and dotted crotchets
          * will not be returned.
          * <p>Around 68% of the rhythm values returned will be no greater than 
          * the mean plus the standard deviation specified and no less than the 
          * mean minus the standard deviation. Around 95% of rhythm values will 
          * be within twice this range.
          * 
          * @param meanRV      the average rhythm value returned 
          * @param stdDev      an rhythm interval defining the standard 
          *                    deviation.
          * @param granularity a rhythm value representing the minimum sort of 
          *                    value returned here.  If you only want quavers
          *                    and multiples of quavers, use JMC.QUAVER.
          */        
        public static final double gaussianRhythmValue(final double meanRV,
                                                       final double stdDev,
                                                       final double granularity)
        {
                double nextRV;
                do {
                        nextRV = (RNG.nextGaussian() * stdDev) + meanRV;
                        nextRV /= granularity;
                        nextRV = (double) Math.round(nextRV);
                        nextRV *= granularity;
                } while (nextRV < Note.MIN_RHYTHM_VALUE
                                || nextRV > Note.MAX_RHYTHM_VALUE);
                return nextRV;
        }
        
        /** Returns random dynamics dispersed by a gaussian distribution around 
          * the mean specified.  Around 68% of the dynamics returned will be no 
          * greater than the mean plus the standard deviation specified and no
          * less than the mean minus the standard deviation. Around 95% of 
          * dynamics will be within twice this range. Dynamic values in jMusic
          * are the same as MIDI, values between 1 (softest) and 127 (loudest).
          *
          * @param meanDynamic  the dynamic that should be most commonly 
          *                     returned by this method.
          * @param stdDeviation the standard deviation. See method comment.
          */
        public static final int gaussianDynamic(final int meanDynamic,  
                                                final int stdDeviation) {
                long nextDynamic;
                do {
                        nextDynamic = Math.round(
                             (RNG.nextGaussian() * stdDeviation) + meanDynamic);
                } while (nextDynamic < Note.MIN_DYNAMIC
                                || nextDynamic > Note.MAX_DYNAMIC);
                return (int) nextDynamic;
        }        

        /** Returns random pans dispersed by a gaussian distribution around 
          * the mean specified.  This method limits values returned between the 
          * range of 0.0 and 1.0.  There is another method which allows you to 
          * specify a different max value for arrangments beyond stereo.
          *
          * <p>Around 68% of the pans returned will be no greater than the mean 
          * plus the standard deviation specified and no less than the mean 
          * minus the standard deviation. Around 95% of pans will be within 
          * twice this range.
          * 
          * @param meanPan      the pan that should be most commonly 
          *                     returned by this method.
          * @param stdDeviation the standard deviation. See method comment.
          */
        public static final double gaussianPan(final double meanPan,  
                                               final double stdDeviation) {
                return gaussianPan(meanPan, stdDeviation, 1.0);
        }
        
        /** Returns random pans dispersed by a gaussian distribution around 
          * the mean specified.  This method limits values returned between the 
          * range of 0.0 and <code>maxPan</code>. This method is useful if 
          * output from more than two speakers is required.
          *
          * <p>Around 68% of the pans returned will be no greater than the mean 
          * plus the standard deviation specified and no less than the mean 
          * minus the standard deviation. Around 95% of pans will be within 
          * twice this range.
          * 
          * @param meanPan      the pan that should be most commonly 
          *                     returned by this method.
          * @param stdDeviation the standard deviation. See method comment.
          */
        public static final double gaussianPan(final double meanPan,
                                               final double stdDeviation,
                                               double maxPan) {
                maxPan = (maxPan >= 0.0) ? maxPan : 0.0;
                long nextPan;
                do { 
                        nextPan = Math.round((RNG.nextGaussian() * stdDeviation) 
                                             + meanPan);
                } while (nextPan < Note.MIN_PAN || nextPan > maxPan);
                return (int) nextPan;
        }                
}