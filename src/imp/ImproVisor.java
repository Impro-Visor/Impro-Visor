/**
 * This Java Class is part of the Impro-Visor Application v. 8.11
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

package imp;

import imp.midi.MidiManager;
import imp.midi.MidiSynth;
import imp.data.advice.Advisor;
import imp.voicing.AutomaticVoicingSettings;
import imp.com.LoadAdviceCommand;
import imp.data.*;
import imp.gui.FirstTimeDialog;
import imp.gui.Notate;
import imp.gui.ToolkitImages;
import imp.util.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import polya.*;

/**
 * Impro-Visor main class
 *
 * @author Stephen Jones, Aaron Wolin, Robert Keller
 * @version 8.11
 */

public class ImproVisor implements Constants {
    
    public static final String version = "8.11";
    
    private static boolean firstTime = false;
    
    private static String lastLeadsheetFileStem = null;
    
    static private Notate currentWindow = null;

    private static int initialXopen = 0;
    private static int initialYopen = 0;

    private static String ruleFilePath;
    private static String ruleFileName;

    private static File ruleFile;
    private static File mostRecentStyleFileEdited = null;
    
    private static String recentFilesFilename = "vocab" + File.separator + "recentFiles.txt";
    private static String prefsFileName = "My.prefs";
    private static String themesFileName = "My.themes";
    private static MidiManager midiManager;
    public static AutomaticVoicingSettings avs;
    public static boolean override;
    
    private static Color windowFrameColor = new Color(230, 230, 230);
     
    /**
     * Indicate whether or not to play notes that are inserted, modified, etc.
     */
    private static boolean playInsertions = false;
    
    /**
     * Insertion volume.
     */
    private static int entryVolume = 85;
    
   /**
     * Indicate whether or not to show advice.
     */
    private static boolean showAdvice = false;
    
    /**
     * Static int indicating chords should be pasted
     */
    public static int CHORDS = 0; 
    
    /**
     * Static int indicating notes should be pasted
     */
    public static int NOTES = 1; 
    
    /**
     * Global clipboard for cut, copy, and paste Melody
     */
    private MelodyPart melodyClipboard;
    
    /**
     * Global clibboard for cut, copy, and paste
     */
    private ChordPart chordsClipboard;
    
    /**
     * If the clipboard is holding a selection of chords or notes
     */
    private int pasteType;
        
    /**
     * Single Advisor for now.
     */

    private static Advisor advisor;
    
    private static RecentFiles recFiles;
    
    public static RecentFiles getRecentFiles()
      {
        return recFiles;
      }
    
    public static File getRuleFile()
      {
      return ruleFile;
      }
    
    public static File getRecentStyleFile()
      {
      if( mostRecentStyleFileEdited == null )
        {
         String fileName = Preferences.getPreference(Preferences.RECENT_STYLE_FILE);
         
         //System.out.println("fileName = " + fileName);
         
         mostRecentStyleFileEdited = new File(getStyleDirectory(), fileName);
        }
      return mostRecentStyleFileEdited;
      }
    
    public static void setRecentStyleFile(File file)
      {
        mostRecentStyleFileEdited = file;
        Preferences.setPreference(Preferences.RECENT_STYLE_FILE, file.getName());
      }
    
    public static File getStyleMixerFile()
      {
          return new File(getStyleDirectory(), Directories.styleMixerName);
      }

    public static MidiManager getMidiManager() {
        return midiManager;
    }
    
    public static MidiSynth getLastMidiSynth() {
        return getCurrentWindow().getMidiSynth();
    }

    /**
     * Get the version string of this version
     */

    public static String getVersion()
      {
      return version;
      }

    /**
     * Get the singleton Advisor for this instance of ImproVisor.
     */

    public static Advisor getAdvisor()
      {
      return advisor;
      }

    public static Polylist getChordNames()
      {
        return getAdvisor().getChordNames();
      }
    
    /**
     * Get the indication of whether to play insertions.
     */

    public static boolean getPlay()
      {
      return playInsertions;
      }
    
    /**
     * Play the current selection
     */

    public static void playCurrentSelection(boolean toEnd, int loop, boolean useDrums)
      {
      getCurrentWindow().getCurrentStave().playSelection(toEnd, loop, useDrums, "ImproVisor");
      }
    
    /**
     * Get the entry-note volume.
     */
    
    public static int getEntryVolume()
    {
        return entryVolume;
    }
    
    /**
     * Get the indication of whether to play insertions.
     */

    public static boolean getShowAdvice()
      {
      return showAdvice;
      }

    /**
     * Set the indication of whether to play insertions.
     * @param x
     */

    public static void setPlayEntrySounds(boolean x)
      {
      playInsertions = x;
      }
    
    /**
     * Set the entry volume.
     */
    
    public static void setEntryVolume(int x)
    {
        entryVolume = x;
    }

    /**
     * Set the indication of whether to show advice.
     * @param x
     */

    public static void setShowAdvice(boolean x)
      {
      showAdvice = x;
      }
    
    private static ImproVisor instance = null;
    
    public static ImproVisor getInstance() {
        if(instance == null) {
            instance = new ImproVisor();
        }
        return instance;
    }
    
    /** 
     * Creates a new instance of ImproVisor. Initializes the clipboard and 
     * creates a default Notation window with 64 blank, 4/4 
     * measures.
     */
    private ImproVisor() {
        this(null);
    }

/** 
 * Creates a new instance of ImproVisor. Initializes the clipboard and 
 * creates a default Notation window with 64 blank, 4/4 
 * measures.  
 * @param leadsheet to be initially loaded; if null, will open new leadsheet
 */
    
private ImproVisor(String leadsheet)
  {
    Trace.log(2, "construct ImproVisor");

//        try {
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (Exception e) {
//        }

    // Make sure to load preferences before loading the MidiManager and Advisor
    
    // Note that loadPreferences will generate a preference file if there is none.
    
    Preferences.loadPreferences();
    
    setChannelsFromPreferences();

    midiManager = new MidiManager();

    advisor = new Advisor();

    // Load the default rule file from the Preferences file
    ruleFilePath = Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE);
    if( ruleFilePath.lastIndexOf(File.separator) == -1 )
      {
        ruleFileName = ruleFilePath;
      }
    else
      {
        ruleFileName = ruleFilePath.substring(ruleFilePath.lastIndexOf(File.separator), ruleFilePath.length());
      }

    LoadAdviceCommand loadAdvice;

    //Trace.log(2, "Loading: " + ruleFileDir + " :: " + ruleFileName);
 
    ruleFile = new File(getVocabDirectory(), ruleFileName);
    
    loadAdvice = new LoadAdviceCommand(ruleFile, advisor, null, true, false);


    loadAdvice.setLoadDialogText("Loading Vocabulary ...");
    loadAdvice.execute();

    synchronized(loadAdvice)
          {
            while( !loadAdvice.hasLoaded() )
              {
                try
                  {
                    loadAdvice.wait();
                  }
                catch( InterruptedException e )
                  {
                  }
              }
          }
    
    // First open a blank Notate window

    // Create a score with default measures in default meter
    Score score = new Score(Notate.DEFAULT_BARS_PER_PART * (BEAT * DEFAULT_METRE[0]));

    String fontSizePref = Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE);

    score.setChordFontSize(Integer.valueOf(fontSizePref));

    // Create notate frame.
    
    Notate notate = new Notate(score, advisor, this, initialXopen, initialYopen);
    
    notate.setNotateFrameHeight();
    
    if( firstTime )
      {
        openFirstTimeDialog(notate);
      }
    
    boolean createRoadMap = Preferences.getPreference(Preferences.CREATE_ROADMAP).equals("y");
    
    notate.setRoadMapCheckBox(createRoadMap);
    
    // Close the splash window.

    loadAdvice.hideLoadDialog();
   
    
    // Load most recent file, if there is one.

    recFiles = new RecentFiles();
    String pathName = recFiles.getFirstPathName();
    if( pathName != null )
      {
        File f = new File(pathName);
        
        if( f.exists() )
          {
            notate.setupLeadsheet(f, false);
          }
      }
    
     notate.makeVisible(createRoadMap);

     currentWindow = notate;


//        Stuff from Julia Botev that was never integrated.
//        ComplexityFrame attributeFrame = new ComplexityFrame();
//        attributeFrame.setVisible(true);
//

    if( Trace.atLevel(3) )
      {
        advisor.listChords(System.out);	// option to list all chord types
      }
    
    //System.out.println("roadmap blocks = " + notate.getRoadMapBlocks());
    //System.out.println(getChordNames());
  }
       
    
 static public void windowHasFocus(Notate window)
  {
    currentWindow = window;
  }

static public Notate getCurrentWindow()
  {
    return currentWindow;
  }

/**
 * Returns the melody clipboard.
 * @return MelodyPart             the melody clipboard
 */
public MelodyPart getMelodyClipboard()
  {
    if( melodyClipboard == null )
      {
        melodyClipboard = new MelodyPart();
      }
    return melodyClipboard;
  }

/**
 * Returns the chord clipboard.
 * @return ChordPart             the clipboard
 */
public ChordPart getChordsClipboard()
  {
    if( chordsClipboard == null )
      {
        chordsClipboard = new ChordPart();
      }
    return chordsClipboard;
  }

/**
 * Indicates whether the melody clipboard is non-empty
 * @return indication of whether the melody clipboard is non-empty
 */
public boolean melodyClipboardNonEmpty()
  {
    return getMelodyClipboard().size() > 0;
  }

/**
 * Indicates whether the chord clipboard is non-empty
 * @return indication of whether the chord clipboard is non-empty
 */
public boolean chordsClipboardNonEmpty()
  {
    return getChordsClipboard().size() > 0;
  }

/**
 * Sets the pasting type to be for chords or notes
 * @param type              the type of paste
 */
public void setPasteType(int type)
  {
    this.pasteType = type;
  }

/**
 * Gets the pasting type
 * @return int              the type of paste
 */
public int getPasteType()
  {
    return pasteType;
  }

    
/**
 * Main Impro-Visor program. Creates an ImproVisor instance, which will 
 * initialize the array of Notate frames.
 * @param args
 */

public static void main(String[] args) throws InvalidMidiDataException
  {
        //Start up Impro-Visor
        avs=new AutomaticVoicingSettings();
        avs.setDefaults(); 
        String leadsheet = null;
        if( args.length > 0 )
            {
                //System.out.println("sees argument");
                leadsheet = args[0];
            }

        // preload images
        ToolkitImages.getInstance();
        getUserDirectory();
        instance = new ImproVisor(leadsheet);
   }

public void setAVS(AutomaticVoicingSettings avs){
    this.avs = avs;
}

public AutomaticVoicingSettings getAVS(){
    return avs;
}



/**
 * Get the directory where user Impro-Visor files are stored.
 * If this directory does not exist, then it is created and populated with
 * directories and files from the master installation.
 * @return 
 */

public static File getUserDirectory()
  {
  String userHome = System.getProperty("user.home");
  //System.out.println("User Home Path: "+ userHome);


  File homeDir = new File(userHome, Directories.improHome);
    
  if( !homeDir.exists() )
    {
      firstTime = true;
      establishUserDirectory(homeDir);
    }
  
  return homeDir;
  }

public static void openFirstTimeDialog(Notate notate)
  {
      FirstTimeDialog firstTimeDialog = new FirstTimeDialog(notate, true);
      firstTimeDialog.setSize(600, 750);
      firstTimeDialog.setVisible(true);    
  }

/**
 * Establish Impro-Visor home directory in user's space.
 * This should be done only once per installation.
 * @param homeDir 
 */
public static void establishUserDirectory(File homeDir)
  {
    System.out.println("Creating new folder in your home directory for impro-visor files: " + Directories.improHome);

    if( homeDir.mkdir() )
      {
      copyDir(Directories.vocabDirName,        homeDir);
      copyDir(Directories.leadsheetDirName,    homeDir);
      copyDir(Directories.grammarDirName,      homeDir);
      copyDir(Directories.transformDirName,    homeDir);
      copyDir(Directories.fractalDirName,      homeDir);
      copyDir(Directories.countsDirName,       homeDir);
      copyDir(Directories.styleDirName,        homeDir);
      copyDir(Directories.styleExtractDirName, homeDir);
      copyDir(Directories.midiDirName,         homeDir);
      copyDir(Directories.voicingDirName,      homeDir);
      }
    else
      {
        System.err.println("Fatal Error: Necessary folder creation in home directory failed.");
        System.exit(1);
      }
  }

/**
 * Copy master sub-directory into users home directory.
 * @param subDirName
 * @param homeDir
 */

public static void copyDir(String subDirName, File homeDir)
  {
    File masterDir = new File(subDirName);

    File userDir = new File(homeDir, subDirName);

        try
          {
            FileUtilities.copyDirectory(masterDir, userDir);
          }
        catch( IOException e )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Error in copying folder " 
                           + subDirName + " to user directory.");
          }
  }


public static File getErrorLogFile()
  {
  return new File(getUserDirectory(), Directories.errorLogFilename);
  }


public static File getVocabDirectory()
  {
  return new File(getUserDirectory(), Directories.vocabDirName);
  }

public static File getNNetDataDirectory()
{
    File f = new File(getUserDirectory(), Directories.vocabDirName);
    return new File(f, Directories.nnetTrainingDataDirName);
}

public static File getStyleRecognitionDirectory()
{
    File f = new File(getUserDirectory(), Directories.vocabDirName);
    return new File(f, Directories.styleRecognitionDirName);
}

public static File getLeadsheetDirectory()
  {
  return new File(getUserDirectory(), Directories.leadsheetDirName);
  }

public static File getStyleDirectory()
  {
   return new File(getUserDirectory(), Directories.styleDirName);
  }

public static File getVoicingDirectory()
  {
    return new File(getUserDirectory(), Directories.voicingDirName);
  }

public static File getStyleExtractDirectory()
  {
  return new File(getUserDirectory(), Directories.styleExtractDirName);
  }

public static File getMidiDirectory()
  {
  return new File(getUserDirectory(), Directories.midiDirName);
  }

public static File getGrammarDirectory()
  {
  return new File(getUserDirectory(), Directories.grammarDirName);
  }

public static File getTransformDirectory()
  {
  return new File(getUserDirectory(), Directories.transformDirName);
  }

public static File getFractalDirectory()
  {
  return new File(getUserDirectory(), Directories.fractalDirName);
  }

public static File getConnectomeDirectory()
  {
  return new File(getUserDirectory(), Directories.connectomeDirName);
  }

public static File getCountsDirectory()
  {
  return new File(getUserDirectory(), Directories.countsDirName);
  }

public static File getDictionaryDirectory()
  {
  return new File(getUserDirectory(), Directories.dictionaryDirName);
  }

public static File getProfileDirectory()
  {
  return new File(getUserDirectory(), Directories.profileDirName);
  }

public static File getPrefsFile()
  {
  return new File(getVocabDirectory(), prefsFileName); 
  }
    
public static File getThemesFile()
  {
  return new File(getVocabDirectory(), themesFileName); 
  }
    
public static File getGrammarFile()
  {
  return new File(getGrammarDirectory(), 
          Preferences.getPreference(Preferences.DEFAULT_GRAMMAR_FILE)); 
  }

public static File getTransformFile()
  {
    String filename = Preferences.getPreference(Preferences.DEFAULT_TRANSFORM_FILE);
    if (filename == null)
    {
        Preferences.setPreference(Preferences.DEFAULT_TRANSFORM_FILE, Preferences.DVF_TRANSFORM_VAL);
        filename = Preferences.DVF_TRANSFORM_VAL;
    }
    return new File(getTransformDirectory(), filename); 
  }

public static File getFractalFile()
  {
  return new File(getFractalDirectory(), 
                  Preferences.getPreference(Preferences.DEFAULT_FRACTAL_FILE));
  }

public static File getCountsFile()
  {
      String filename = Preferences.getPreference(Preferences.DEFAULT_COUNTS_FILE);
    if (filename == null)
    {
        System.out.println("filename was null");
        Preferences.setPreference(Preferences.DEFAULT_COUNTS_FILE, Preferences.DVF_COUNTS_VAL);
        filename = Preferences.DVF_COUNTS_VAL;
    }
    return new File(getCountsDirectory(), filename);
//  return new File(getCountsDirectory(), 
//          Preferences.getPreference(Preferences.DEFAULT_COUNTS_FILE)); 
  }
     
public static File getRecentFilesFile()
  {
    String filename = getUserDirectory() + File.separator + recentFilesFilename;
    File file = new File(filename);
    try
      {
        file.createNewFile();
        //System.out.println("recentFiles in " + filename);
        return file;
      }
    catch( IOException e )
      {
        ErrorLog.log(ErrorLog.WARNING, "Cannot create recentFiles file: " + filename);
        return null;
      }
  }

public static void setLastLeadsheetFileStem(String stem)
  {
    if( stem != null )
      {
      if( stem.endsWith(".ls") )
        {
          lastLeadsheetFileStem = stem.substring(0, stem.length()-3);
        }
      else
        {
          lastLeadsheetFileStem = stem;
        }
      }
  }

public static String getLastLeadsheetFileStem()
  {
    return lastLeadsheetFileStem;
  }

/*
* Note that these channel numbers are 1 less than what they appear as
* to the user.
*/
private static int melodyChannel = 0;

private static int chordChannel = 3;

private static int bassChannel = 6;

private static int drumChannel = 9;

private void setChannelsFromPreferences()
  {
    setMelodyChannel(Preferences.getMelodyChannel()-1);
    setChordChannel(Preferences.getChordChannel()-1);
    setBassChannel(Preferences.getBassChannel()-1);
    setDrumChannel(Preferences.getDrumChannel()-1);
  }

public static int getMelodyChannel()
  {
    return melodyChannel;
  }
    
public static int getChordChannel()
  {
    return chordChannel;
  }
    
public static int getBassChannel()
  {
    return bassChannel;
  }
    
public static int getDrumChannel()
  {
    return drumChannel;
  }
 
public static void setMelodyChannel(int value)
  {
    melodyChannel = value;
    Preferences.setPreference(Preferences.MELODY_CHANNEL, ""+(value+1));
  }

public static void setChordChannel(int value)
  {
    chordChannel = value;
    Preferences.setPreference(Preferences.CHORD_CHANNEL, ""+(value+1));
  }

public static void setBassChannel(int value)
  {
    bassChannel = value;
    Preferences.setPreference(Preferences.BASS_CHANNEL, ""+(value+1));
  }
   
public static void setDrumChannel(int value)
  {
    drumChannel = value;
    Preferences.setPreference(Preferences.DRUM_CHANNEL, ""+(value+1));
  }

public static Color getWindowFrameColor()
  {
    return windowFrameColor;
  }
}
