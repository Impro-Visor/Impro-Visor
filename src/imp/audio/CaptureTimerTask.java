/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.audio;

import java.util.TimerTask;

/**
 *
 * @author Brian Howell
 */
public class CaptureTimerTask extends TimerTask
{

    PitchExtractor extractor;

    public CaptureTimerTask(PitchExtractor extractor)
    {
        this.extractor = extractor;
    }

    public void run()
    {
        while (extractor.isCapturing())
        {
            try
            {
                Thread.sleep(1);
            } catch (Exception e)
            {
                System.out.println("Sleep error:\n" + e);
            }
        }
        long time = System.currentTimeMillis();
        synchronized (extractor.thisCapture)
        {
            extractor.thisCapture.notifyAll();
        }
        System.out.println("Timer triggered audio capture at time " + time);
    }
}