/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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

package imp.audio;

import imp.gui.Notate;

/**
 * Handles settings for audio capture.
 *
 * @version 27 July, 2012
 * @author Brian Howell and Robert Keller
 */

public class AudioSettings
{
    private int FRAME_SIZE              = DEFAULT_FRAME_SIZE; //# of BYTES examined per poll
    private int POLL_RATE               = DEFAULT_POLL_RATE; //in milliseconds
    private int RESOLUTION              = DEFAULT_RESOLUTION; //smallest subdivision allowed
    private boolean TRIPLETS            = DEFAULT_TRIPLETS;
    private double RMS_THRESHOLD        = DEFAULT_RMS_THRESHOLD;
    private double CONFIDENCE_THRESHOLD = DEFAULT_CONFIDENCE_THRESHOLD;
    //sets threshold for detecting peaks in normalized SDF
    private double K_CONSTANT           = DEFAULT_K_CONSTANT;

    private int MIN_PITCH              = DEFAULT_MIN_PITCH;
    private int MAX_PITCH              = DEFAULT_MAX_PITCH;

    private static int     DEFAULT_FRAME_SIZE = 2048; //# of BYTES examined per poll
    private static int     DEFAULT_POLL_RATE = 20; //in milliseconds
    private static int     DEFAULT_RESOLUTION = 8; //smallest subdivision allowed
    private static boolean DEFAULT_TRIPLETS = true;
    private static double  DEFAULT_RMS_THRESHOLD = 4.75;
    private static double  DEFAULT_CONFIDENCE_THRESHOLD = 0.45;
    private static double  DEFAULT_K_CONSTANT = 0.875;

    private static int     DEFAULT_MIN_PITCH = 45;
    private static int     DEFAULT_MAX_PITCH = 100;

     Notate notate;

public AudioSettings(Notate notate)
  {
    this.notate = notate;
    setDefaults();
  }

public final void setDefaults()
  {
    setRESOLUTION(DEFAULT_RESOLUTION);
    setFRAME_SIZE(DEFAULT_FRAME_SIZE);
    setPOLL_RATE(DEFAULT_POLL_RATE);
    setTRIPLETS(DEFAULT_TRIPLETS);
    setRMS_THRESHOLD(DEFAULT_RMS_THRESHOLD);
    setCONFIDENCE_THRESHOLD(DEFAULT_CONFIDENCE_THRESHOLD);
    setK_CONSTANT(DEFAULT_K_CONSTANT);
    setMIN_PITCH(DEFAULT_MIN_PITCH);
    setMAX_PITCH(DEFAULT_MAX_PITCH);
  }

    public void setCONFIDENCE_THRESHOLD(double confidence_threshold)
    {
        this.CONFIDENCE_THRESHOLD = confidence_threshold;
        notate.setConfidenceThreshold(confidence_threshold);
    }

    public void setFRAME_SIZE(int frame_size)
    {
        this.FRAME_SIZE = frame_size;
        notate.setFrameSize(frame_size);
    }

    public void setK_CONSTANT(double k_constant)
    {
        this.K_CONSTANT = k_constant;
        notate.setKconstantSlider(k_constant);
    }

    public void setPOLL_RATE(int poll_rate)
    {
        this.POLL_RATE = poll_rate;
        notate.setPollRate(poll_rate);
    }

    public void setRESOLUTION(int resolution)
    {
        this.RESOLUTION = resolution;
        notate.setAudioNoteResolution(resolution);
    }

    public void setRMS_THRESHOLD(double rms_threshold)
    {
        this.RMS_THRESHOLD = rms_threshold;
        notate.setRMSThreshold(rms_threshold);
    }

    public void setTRIPLETS(boolean triplets)
    {
        this.TRIPLETS = triplets;
        notate.setAudioplayTriplets(triplets);
    }

    public void setMAX_PITCH(int maxPitch)
    {
        MAX_PITCH = maxPitch;
        notate.setMaxPitch(maxPitch);
    }

    public void setMIN_PITCH(int minPitch)
    {
        MIN_PITCH = minPitch;
        notate.setMinPitch(minPitch);
    }

    public int getMIN_PITCH()
    {
        return MIN_PITCH;
    }

    public int getMAX_PITCH()
    {
        return MAX_PITCH;
    }

    public double getCONFIDENCE_THRESHOLD()
    {
        return CONFIDENCE_THRESHOLD;
    }

    public int getFRAME_SIZE()
    {
        return FRAME_SIZE;
    }

    public double getK_CONSTANT()
    {
        return K_CONSTANT;
    }

    public float getPOLL_RATE()
    {
        return POLL_RATE;
    }

    public int getRESOLUTION()
    {
        return RESOLUTION;
    }

    /**
     * Note that this getter returns the exp of the stored value
     * @return
     */
    public double getRMS_THRESHOLD()
    {
        return Math.exp(RMS_THRESHOLD);
    }

    public boolean isTRIPLETS()
    {
        return TRIPLETS;
    }
}