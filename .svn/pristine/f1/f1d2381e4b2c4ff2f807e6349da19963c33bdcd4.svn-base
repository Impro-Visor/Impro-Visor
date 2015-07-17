/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.voicing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *  Loads and saves automatic voicing settings objects in AVP files
 * @author Daniel Scanteianu
 */
public class AVPFileCreator
{
    /**
     * loads settings objects from file
     * @param file file to read
     * @param settings settings object to populate
     */
    public static void fileToSettings(File file, AutomaticVoicingSettings settings)
    {
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine())
            {
                String line=sc.nextLine();
                System.out.println(line);
                if(line.contains("Auto Voicing Preset File."));
                else if(!line.contains("("))
                    throw new Exception();
                else if(line.contains("LH Lower Limit"))
                {
                    line=line.substring(line.indexOf("LH Lower Limit")+"LH Lower Limit".length()+1,line.indexOf(")"));
                    settings.setLeftHandLowerLimit(Integer.parseInt(line));
                }
                else if(line.contains("RH Lower Limit"))
                {
                    line=line.substring(line.indexOf("RH Lower Limit")+"RH Lower Limit".length()+1,line.indexOf(")"));
                    settings.setRightHandLowerLimit(Integer.parseInt(line));
                }
                else if(line.contains("LH Upper Limit"))
                {
                    line=line.substring(line.indexOf("LH Upper Limit")+"LH Upper Limit".length()+1,line.indexOf(")"));
                    settings.setLeftHandUpperLimit(Integer.parseInt(line));
                }
                else if(line.contains("RH Upper Limit"))
                {
                    line=line.substring(line.indexOf("RH Upper Limit")+"RH Upper Limit".length()+1,line.indexOf(")"));
                    settings.setRightHandUpperLimit(Integer.parseInt(line));
                }
                else if(line.contains("LH Spread"))
                {
                    line=line.substring(line.indexOf("LH Spread")+"LH Spread".length()+1,line.indexOf(")"));
                    settings.setLeftHandSpread(Integer.parseInt(line));
                }
                else if(line.contains("RH Spread"))
                {
                    line=line.substring(line.indexOf("RH Spread")+"RH Spread".length()+1,line.indexOf(")"));
                    settings.setRightHandSpread(Integer.parseInt(line));
                }
                else if(line.contains("LH Min Notes"))
                {
                    line=line.substring(line.indexOf("LH Min Notes")+"LH Min Notes".length()+1,line.indexOf(")"));
                    settings.setLeftHandMinNotes(Integer.parseInt(line));
                }
                else if(line.contains("RH Min Notes"))
                {
                    line=line.substring(line.indexOf("RH Min Notes")+"RH Min Notes".length()+1,line.indexOf(")"));
                    settings.setRightHandMinNotes(Integer.parseInt(line));
                }
                else if(line.contains("LH Max Notes"))
                {
                    line=line.substring(line.indexOf("LH Max Notes")+"LH Max Notes".length()+1,line.indexOf(")"));
                    settings.setLeftHandMaxNotes(Integer.parseInt(line));
                }
                else if(line.contains("RH Max Notes"))
                {
                    line=line.substring(line.indexOf("RH Max Notes")+"RH Max Notes".length()+1,line.indexOf(")"));
                    settings.setRightHandMaxNotes(Integer.parseInt(line));
                }
                else if(line.contains("Pref Motion Range"))
                {
                    line=line.substring(line.indexOf("Pref Motion Range")+"Pref Motion Range".length()+1,line.indexOf(")"));
                    settings.setPreferredMotionRange(Integer.parseInt(line));
                }
                else if(line.contains("Pref Motion"))
                {
                    line=line.substring(line.indexOf("Pref Motion")+"Pref Motion".length()+1,line.indexOf(")"));
                    settings.setPreferredMotion(Integer.parseInt(line));
                }
                
                else if(line.contains("Prev Voicing Multiplier"))
                {
                    line=line.substring(line.indexOf("Prev Voicing Multiplier")+"Prev Voicing Multiplier".length()+1,line.indexOf(")"));
                    settings.setPreviousVoicingMultiplier(Integer.parseInt(line));
                }
                else if(line.contains("Half Step Multiplier"))
                {
                    line=line.substring(line.indexOf("Half Step Multiplier")+"Half Step Multiplier".length()+1,line.indexOf(")"));
                    settings.setHalfStepAwayMultiplier(Integer.parseInt(line));
                }
                else if(line.contains("Full Step Multiplier"))
                {
                    line=line.substring(line.indexOf("Full Step Multiplier")+"Full Step Multiplier".length()+1,line.indexOf(")"));
                    settings.setFullStepAwayMultiplier(Integer.parseInt(line));
                }
                else if(line.contains("LH Color Priority"))
                {
                    line=line.substring(line.indexOf("LH Color Priority")+"LH Color Priority".length()+1,line.indexOf(")"));
                    settings.setLeftColorPriority(Integer.parseInt(line));
                }
                else if(line.contains("RH Color Priority"))
                {
                    line=line.substring(line.indexOf("RH Color Priority")+"RH Color Priority".length()+1,line.indexOf(")"));
                    settings.setRightColorPriority(Integer.parseInt(line));
                }
                else if(line.contains("Max Priority"))
                {
                    line=line.substring(line.indexOf("Max Priority")+"Max Priority".length()+1,line.indexOf(")"));
                    settings.setMaxPriority(Integer.parseInt(line));
                }
                else if(line.contains("Priority Multiplier"))
                {
                    line=line.substring(line.indexOf("Priority Multiplier")+"Priority Multiplier".length()+1,line.indexOf(")"));
                    settings.setPriorityMultiplier(Integer.parseInt(line));
                }
                else if(line.contains("Repeat Multiplier"))
                {
                    line=line.substring(line.indexOf("Repeat Multiplier")+"Repeat Multiplier".length()+1,line.indexOf(")"));
                    settings.setRepeatMultiplier(Integer.parseInt(line));
                }
                else if(line.contains("Half Step Reducer"))
                {
                    line=line.substring(line.indexOf("Half Step Reducer")+"Half Step Reducer".length()+1,line.indexOf(")"));
                    settings.setHalfStepReducer(Integer.parseInt(line));
                }
                else if(line.contains("Full Step Reducer"))
                {
                    line=line.substring(line.indexOf("Full Step Reducer")+"Full Step Reducer".length()+1,line.indexOf(")"));
                    settings.setFullStepReducer(Integer.parseInt(line));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AVPFileCreator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"An error was encountered while reading the file.");
            
        }
    }
    /**
     * saves settings object to a file
     * @param settings settings to save
     * @param file file to write
     */
    public static void settingsToFile(AutomaticVoicingSettings settings, File file)
    {
        try{
                    PrintWriter writer = new PrintWriter(file, "UTF-8");
                    writer.println("(Auto Voicing Preset File. Please check parameter limits (in Impro-visor) before modifying.");
                    writer.println("(LH Lower Limit "+settings.getLeftHandLowerLimit()+")");
                    writer.println("(RH Lower Limit "+settings.getRightHandLowerLimit()+")");
                    writer.println("(LH Upper Limit "+settings.getLeftHandUpperLimit()+")");
                    writer.println("(RH Upper Limit "+settings.getRightHandUpperLimit()+")");
                    writer.println("(LH Spread "+settings.getLeftHandSpread()+")");
                    writer.println("(RH Spread "+settings.getRightHandSpread()+")");
                    writer.println("(LH Min Notes "+settings.getLeftHandMinNotes()+")");
                    writer.println("(LH Max Notes "+settings.getLeftHandMaxNotes()+")");
                    writer.println("(RH Min Notes "+settings.getRightHandMinNotes()+")");
                    writer.println("(RH Max Notes "+settings.getRightHandMaxNotes()+")");
                    //voice leading controls
                    writer.println("(Pref Motion "+settings.getPreferredMotion()+")");
                    writer.println("(Pref Motion Range "+settings.getPreferredMotionRange()+")");
                    writer.println("(Prev Voicing Multiplier "+(int)(settings.getPreviousVoicingMultiplier()*10)+")");// multiplier for notes used in previous voicing
                    writer.println("(Half Step Multiplier "+(int)(settings.getHalfStepAwayMultiplier()*10)+")");
                    writer.println("(Full Step Multiplier "+(int)(settings.getFullStepAwayMultiplier()*10)+")");
                    //voicing control
                    writer.println("(LH Color Priority "+settings.getLeftColorPriority()+")");//priority of any color note
                    writer.println("(RH Color Priority "+settings.getRightColorPriority()+")");//priority of any color note
                    writer.println("(Max Priority "+settings.getMaxPriority()+")");//max priority a note in the priority array can have
                    writer.println("(Priority Multiplier "+(int)(+settings.getPriorityMultiplier()*10)+")");//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
                    writer.println("(Repeat Multiplier "+(int)(settings.getRepeatMultiplier()*10)+")");
                    writer.println("(Half Step Reducer "+(int)(settings.getHalfStepReducer()*10)+")");
                    writer.println("(Full Step Reducer "+(int)(settings.getFullStepReducer()*10)+")");
                    writer.close();
                }
                catch(Exception f)
                {
                    
                }
    }
/*
    static void settingsFromFile(File openFile, AutomaticVoicingSettings avs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
}
