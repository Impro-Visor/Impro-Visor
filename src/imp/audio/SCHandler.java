package imp.audio;

import java.io.*;

/**
 *
 * @author Anna Turner
 * @author Brian Kwak
 * @author mkyong, from
 * http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 * @author
 * http://stackoverflow.com/questions/54686/how-to-get-a-list-of-current-open-windows-process-with-java
 * @since June 27 2013
 */
public class SCHandler {

    private static String opSystem = System.getProperty("os.name").toLowerCase();
    private static boolean isWindows, isOsX, isLinux;
    private static Process process;
    private static boolean firstTimeOpen = true;

    /**
     * According to OS, open SuperCollider.
     *
     * The Startup file included in the download runs automatically upon opening
     * of SC.
     */
    public void openSC() {
        //Making sure to reset the booleans
        isWindows = false;
        isOsX = false;
        isLinux = false;

        //Now check for os
        if (opSystem.contains("win")) {
            isWindows = true;
        } else if (opSystem.contains("mac")) {
            isOsX = true;
        } else if (opSystem.contains("linux")) {
            isLinux = true;
        } else {
            System.out.println("Your OS is not currently supported.");
            return;
        }

        //Handle SuperCollider opening based on OS
        try {
            Runtime runTime = Runtime.getRuntime();

            // @TODO fix Linux. Works, but NO SOUND OUTPUT.
            if (isWindows) {
                if (firstTimeOpen) { // Exists no process
                    openSCHelperWindows();
                    firstTimeOpen = false;
                } else if (!isRunning(process)) { // OK to open again
                    openSCHelperWindows();
                } 
            } else if (isOsX) {
                String dir = System.getProperty("user.dir");
//                System.out.println("Attempting to open Pitch Tracker : " + dir + "/sc/PitchTracker.app");
                process = runTime.exec("open " + dir + "/sc/PitchTracker619.app");
                
            } else { // is linux
                process = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper function to make ProcessBuilder and process to open SuperCollider.
     */
    private void openSCHelperWindows() {
        try {
            String dir = System.getProperty("user.dir");
            String myFile = dir + "\\sc\\standalone_windows\\SuperCollider-3.6.6\\start.bat";
            process = Runtime.getRuntime().exec(myFile);
            /*
            String[] command = {"cmd", "/c", "C:\\Program Files (x86)\\SuperCollider-3.6.5\\scide.exe"};
            ProcessBuilder probuilder = new ProcessBuilder(command);
            process = probuilder.start();
            */
        } catch (Exception e) {
            System.out.println("Error opening SuperCollider");
        }
    }

    /**
     * Checks whether a process has been initiated and is running.
     *
     * @param process the process
     * @returns true if still running
     */
    private boolean isRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}