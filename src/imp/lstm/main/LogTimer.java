/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lstm.main;

import java.util.Stack;

/**
 * Class LogTimer handles printing messages to System.out that have a time step attached, as well as providing functionality for nested timing of code execution using a stack.
 * @author Nicholas Weintraut
 */
public class LogTimer {
   
    public static Stack<Long> startLogTimes = new Stack<Long>(); //our stack of logTimes
    public static Long startTime; //the first time we started
    
    /**
     * Sets the start time to the current system time
     */
    public static void initStartTime()
    {
        startTime = System.nanoTime();
    }
    
    /**
     * Starts a timed log message with the given message
     * @param message 
     */
    public static void startLog(String message)
    {
        
        for(int i = 0; i < startLogTimes.size(); i++)
        {
            System.out.print("\t");
        }
        startLogTimes.push(System.nanoTime() - startTime);
        System.out.println(((startLogTimes.peek()) / 1000000000.00) + "seconds: " + message);
    }
    
    /**
     * Ends the log time on top of the stack and prints out the elapsed time
     */
    public static void endLog()
    {
        
        for(int i = 0; i < startLogTimes.size(); i++)
        {
            System.out.print("\t");
        }
        System.out.println("took " + ((System.nanoTime() - startTime - startLogTimes.pop()) / 1000000000.00) + " seconds: ");
    }
    
    /**
     * Prints a normal log message with the current time in seconds since the last call to initStartTime
     * @param message 
     */
    public static void log(String message)
    {
        System.out.println(((System.nanoTime() - startTime) / 1000000000.00) + "seconds: " + message);
    }
    
}
