/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;
import lstm.io.leadsheet.Constants;

/**
 * Class trading timer contains static methods to benchmark generation of the compressing autoencoder in regards to time ahead of or behind the realtime flow of music
 * @author cssummer16
 */
public class TradingTimer {
    
    private static int beatsPerMinute = 200; //what bpm rate are we benchmarking against?
    private static int soloSize = 192; //this is the size of a single trading portion in time steps
    
    private static long startTime; //the time we start executing and playing
    private static int timeStep; //the current timeStep we have generated
    
    private static long lastInputTime;
    private static double secondsPerStep;
    
    /**
     * Sets the starting time to the current system time
     */
    public static void initStart()
    {
        startTime = System.nanoTime();
        lastInputTime = 0;
        secondsPerStep = 1.0 / (beatsPerMinute * (Constants.BEAT / Constants.RESOLUTION_SCALAR) / 60.0);
    }
    
    /**
     * Waits until the realtime time of the next timestep
     */
    public static void waitForNextTimedInput()
    {
        while((System.nanoTime() - startTime) < lastInputTime + (secondsPerStep * 1000000000.00)){};
        lastInputTime += (secondsPerStep * 1000000000.00);
    }
    
    /**
     * Registers a new timeStep and prints out whether we are ahead or behind realtime
     */
    public static void logTimestep()
    {
        long elapsedTime = System.nanoTime() - startTime; //the time that has passed since last call to initStart
        double stepsPerBeat = Constants.BEAT / Constants.RESOLUTION_SCALAR; 
        int elapsedTimeStep = (int) (elapsedTime * beatsPerMinute * (stepsPerBeat) / 1000000000.00 / 60.00); //how many timeSteps have passed since last call to init start 
        double stepsPerSecond = stepsPerBeat * beatsPerMinute / 60.00; //the number of steps every second
        double timeDifference = (++timeStep / stepsPerSecond) - ((elapsedTimeStep - soloSize) / stepsPerSecond); //the difference in time between our generation and realtime
        if(timeDifference > 0.0)
        {
            System.out.println("Generation is " + timeDifference + " ahead of performance rate.");
        }
        else if(timeDifference == 0.0)
        {
            System.out.println("Generation is on time with performance rate.");
        }
        else
        {
            System.out.println("Generation is " + (-1 * timeDifference) + " behind performance rate.");
        }
    }
    
}
