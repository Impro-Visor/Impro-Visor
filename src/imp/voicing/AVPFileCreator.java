/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015 Robert Keller and Harvey Mudd College.
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
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

package imp.voicing;

import imp.util.ErrorLog;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

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
    private static String lastFileName="default.fv";
    public static void fileToSettings(File file, AutomaticVoicingSettings settings)
    {
        lastFileName=file.getName();
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine())
            {
                String line=sc.nextLine();
                //System.out.println(line);
                if(line.contains("Auto Voicing Preset File."));
                else if(!line.contains("("))
                    throw new Exception();
                else if(line.contains("LH-lower-limit"))
                {
                    line=line.substring(line.indexOf("LH-lower-limit")+"LH-lower-limit".length()+1,line.indexOf(")"));
                    settings.setLeftHandLowerLimit(Integer.parseInt(line));
                }
                else if(line.contains("RH-lower-limit"))
                {
                    line=line.substring(line.indexOf("RH-lower-limit")+"RH-lower-limit".length()+1,line.indexOf(")"));
                    settings.setRightHandLowerLimit(Integer.parseInt(line));
                }
                else if(line.contains("LH-upper-limit"))
                {
                    line=line.substring(line.indexOf("LH-upper-limit")+"LH-upper-limit".length()+1,line.indexOf(")"));
                    settings.setLeftHandUpperLimit(Integer.parseInt(line));
                }
                else if(line.contains("RH-upper-limit"))
                {
                    line=line.substring(line.indexOf("RH-upper-limit")+"RH-upper-limit".length()+1,line.indexOf(")"));
                    settings.setRightHandUpperLimit(Integer.parseInt(line));
                }
                else if(line.contains("LH-spread"))
                {
                    line=line.substring(line.indexOf("LH-spread")+"LH-spread".length()+1,line.indexOf(")"));
                    settings.setLeftHandSpread(Integer.parseInt(line));
                }
                else if(line.contains("RH-spread"))
                {
                    line=line.substring(line.indexOf("RH-spread")+"RH-spread".length()+1,line.indexOf(")"));
                    settings.setRightHandSpread(Integer.parseInt(line));
                }
                else if(line.contains("LH-min-notes"))
                {
                    line=line.substring(line.indexOf("LH-min-notes")+"LH-min-notes".length()+1,line.indexOf(")"));
                    settings.setLeftHandMinNotes(Integer.parseInt(line));
                }
                else if(line.contains("RH-min-notes"))
                {
                    line=line.substring(line.indexOf("RH-min-notes")+"RH-min-notes".length()+1,line.indexOf(")"));
                    settings.setRightHandMinNotes(Integer.parseInt(line));
                }
                else if(line.contains("LH-max-notes"))
                {
                    line=line.substring(line.indexOf("LH-max-notes")+"LH-max-notes".length()+1,line.indexOf(")"));
                    settings.setLeftHandMaxNotes(Integer.parseInt(line));
                }
                else if(line.contains("RH-max-notes"))
                {
                    line=line.substring(line.indexOf("RH-max-notes")+"RH-max-notes".length()+1,line.indexOf(")"));
                    settings.setRightHandMaxNotes(Integer.parseInt(line));
                }
                else if(line.contains("pref-motion-range"))
                {
                    line=line.substring(line.indexOf("pref-motion-range")+"pref-motion-range".length()+1,line.indexOf(")"));
                    settings.setPreferredMotionRange(Integer.parseInt(line));
                }
                else if(line.contains("pref-motion"))
                {
                    line=line.substring(line.indexOf("pref-motion")+"pref-motion".length()+1,line.indexOf(")"));
                    settings.setPreferredMotion(Integer.parseInt(line));
                }
                
                else if(line.contains("prev-voicing-multiplier"))
                {
                    line=line.substring(line.indexOf("prev-voicing-multiplier")+"prev-voicing-multiplier".length()+1,line.indexOf(")"));
                    settings.setPreviousVoicingMultiplier(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("half-step-multiplier"))
                {
                    line=line.substring(line.indexOf("half-step-multiplier")+"half-step-multiplier".length()+1,line.indexOf(")"));
                    settings.setHalfStepAwayMultiplier(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("full-step-multiplier"))
                {
                    line=line.substring(line.indexOf("full-step-multiplier")+"full-step-multiplier".length()+1,line.indexOf(")"));
                    settings.setFullStepAwayMultiplier(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("LH-color-priority"))
                {
                    line=line.substring(line.indexOf("LH-color-priority")+"LH-color-priority".length()+1,line.indexOf(")"));
                    settings.setLeftColorPriority(Integer.parseInt(line));
                }
                else if(line.contains("RH-color-priority"))
                {
                    line=line.substring(line.indexOf("RH-color-priority")+"RH-color-priority".length()+1,line.indexOf(")"));
                    settings.setRightColorPriority(Integer.parseInt(line));
                }
                else if(line.contains("max-priority"))
                {
                    line=line.substring(line.indexOf("max-priority")+"max-priority".length()+1,line.indexOf(")"));
                    settings.setMaxPriority(Integer.parseInt(line));
                }
                else if(line.contains("priority-multiplier"))
                {
                    line=line.substring(line.indexOf("priority-multiplier")+"priority-multiplier".length()+1,line.indexOf(")"));
                    settings.setPriorityMultiplier(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("repeat-multiplier"))
                {
                    line=line.substring(line.indexOf("repeat-multiplier")+"repeat-multiplier".length()+1,line.indexOf(")"));
                    settings.setRepeatMultiplier(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("half-step-reducer"))
                {
                    line=line.substring(line.indexOf("half-step-reducer")+"half-step-reducer".length()+1,line.indexOf(")"));
                    settings.setHalfStepReducer(Integer.parseInt(line)/10.0);
                }
                else if(line.contains("full-step-reducer"))
                {
                    line=line.substring(line.indexOf("full-step-reducer")+"full-step-reducer".length()+1,line.indexOf(")"));
                    settings.setFullStepReducer(Integer.parseInt(line)/10.0);
                }
                 else if(line.contains("left-min-interval"))
                {
                    line=line.substring(line.indexOf("min-interval")+"min-interval".length()+1,line.indexOf(")"));
                    settings.setLeftMinInterval(Integer.parseInt(line));
                }
                  else if(line.contains("right-min-interval"))
                {
                    line=line.substring(line.indexOf("min-interval")+"min-interval".length()+1,line.indexOf(")"));
                    settings.setRightMinInterval(Integer.parseInt(line));
                }
                else if(line.contains("invert-9th"))
                {
                    if(line.contains("on"))
                        settings.setInvertM9(true);
                    if(line.contains("off"))
                        settings.setInvertM9(false);
                }
                else if(line.contains("voice-all"))
                {
                    if(line.contains("on"))
                        settings.setVoiceAll(true);
                    if(line.contains("off"))
                        settings.setVoiceAll(false);
                }
                else if(line.contains("rootless"))
                {
                    if(line.contains("on"))
                        settings.setRootless(true);
                    if(line.contains("off"))
                        settings.setRootless(false);
                }
            }
        } catch (Exception ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Error finding or reading Voicing File: " + lastFileName);
            
        }
    }

    public static String getLastFileName() {
        return lastFileName;
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
                    writer.println("(comments \"Auto Voicing Preset File. Please check parameter limits (in Impro-visor) before modifying.\")");
                    writer.println("(LH-lower-limit "+settings.getLeftHandLowerLimit()+")");
                    writer.println("(RH-lower-limit "+settings.getRightHandLowerLimit()+")");
                    writer.println("(LH-upper-limit "+settings.getLeftHandUpperLimit()+")");
                    writer.println("(RH-upper-limit "+settings.getRightHandUpperLimit()+")");
                    writer.println("(LH-spread "+settings.getLeftHandSpread()+")");
                    writer.println("(RH-spread "+settings.getRightHandSpread()+")");
                    writer.println("(LH-min-notes "+settings.getLeftHandMinNotes()+")");
                    writer.println("(LH-max-notes "+settings.getLeftHandMaxNotes()+")");
                    writer.println("(RH-min-notes "+settings.getRightHandMinNotes()+")");
                    writer.println("(RH-max-notes "+settings.getRightHandMaxNotes()+")");
                    //voice leading controls
                    writer.println("(pref-motion "+settings.getPreferredMotion()+")");
                    writer.println("(pref-motion-range "+settings.getPreferredMotionRange()+")");
                    writer.println("(prev-voicing-multiplier "+(int)(settings.getPreviousVoicingMultiplier()*10)+")");// multiplier for notes used in previous voicing
                    writer.println("(half-step-multiplier "+(int)(settings.getHalfStepAwayMultiplier()*10)+")");
                    writer.println("(full-step-multiplier "+(int)(settings.getFullStepAwayMultiplier()*10)+")");
                    //voicing control
                    writer.println("(LH-color-priority "+settings.getLeftColorPriority()+")");//priority of any color note
                    writer.println("(RH-color-priority "+settings.getRightColorPriority()+")");//priority of any color note
                    writer.println("(max-priority "+settings.getMaxPriority()+")");//max priority a note in the priority array can have
                    writer.println("(priority-multiplier "+(int)(+settings.getPriorityMultiplier()*10)+")");//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
                    writer.println("(repeat-multiplier "+(int)(settings.getRepeatMultiplier()*10)+")");
                    writer.println("(half-step-reducer "+(int)(settings.getHalfStepReducer()*10)+")");
                    writer.println("(full-step-reducer "+(int)(settings.getFullStepReducer()*10)+")");
                    writer.println("(left-min-interval "+(int)(settings.getLeftMinInterval())+")");
                    writer.println("(right-min-interval "+(int)(settings.getRightMinInterval())+")");
                    if(settings.getInvertM9())
                        writer.println("(invert-9th on)");
                    else
                        writer.println("(invert-9th off)");
                    if(settings.getVoiceAll())
                        writer.println("(voice-all on)");
                    else
                        writer.println("(voice-all off)");
                    if(settings.getRootless())
                        writer.println("(rootless on)");
                    else
                        writer.println("(rootless off)");
                    writer.close();
                }
                catch(Exception f)
                {
                ErrorLog.log(ErrorLog.SEVERE, "Error writing Voicing File: " + file.getName());

                }
    }

}
