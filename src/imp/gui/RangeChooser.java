/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.Constants;
import imp.data.PianoKey;
import imp.util.Preferences;
import javax.swing.Icon;
import javax.swing.JLabel;



/**
 *
 * @author muddCS15
 */
public class RangeChooser extends javax.swing.JDialog implements Constants{

    //Used when user does not have a preferred default range or minimum range
    private static final int NO_PREFERENCE = -1;
    
    //minimum range span that is allowed
    private int minimumRange;
    
    //range of keys which are clickable
    private static final int [] bassLimits = {C1, E5};
    private static final int [] trebleLimits = {A2, C7};
    private static final int [] grandLimits = {C1, C7};
    
    //stores range of keys which are clickable
    private final int [] limits;
    
    //the default keys that are initially clicked
    private static final int [] bassDefaults = {G2, C4};
    private static final int [] trebleDefaults = {C4, G5};
    private static final int [] grandDefaults = {G2, G5};
    
    //the midi values of two keys that are currently clicked
    private int [] clicked;
    
    //array of piano keys
    private PianoKey [] pkeys;
    
    //constants representing which key to replace
    private static int LOW = 0;
    private static int HIGH = 1;
    
    /**
    * Getting the piano key images.
    */
    public javax.swing.ImageIcon whiteKey = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/whitekey.jpg"));

    public javax.swing.ImageIcon whiteKeyPressed = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/whitekeypressed.jpg"));

    public javax.swing.ImageIcon blackKey = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackkey.jpg"));

    public javax.swing.ImageIcon blackKeyPressed = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackkeypressed.jpg"));

    public javax.swing.ImageIcon bassKey = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/rootkey.jpg"));

    public javax.swing.ImageIcon bassKeyPressed = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/rootkeypressed.jpg"));

    public javax.swing.ImageIcon blackBassKey = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackrootkey.jpg"));

    public javax.swing.ImageIcon blackBassKeyPressed = 
        new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackrootkeypressed.jpg"));
    
   //Piano key constants
    public final int WKWIDTH = 20;      // width of a white key
    public final int WKHEIGHT = 120;    // height of a white key
    public final int BKHEIGHT = 80;     // height of a black key
    public final int BKWIDTH = 14;      // width of a black key
    public final int OCTKEYS = 7;       // 7 white keys per octave
    public final int A = 21;            // MIDI value of 1st key on keyboard
    public final int P_OCTAVE = 12;     // 12 notes per octave
    public final int OCTAVE_WIDTH = 140;      // width of an octave
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh){
        this(parent, defaultLow, defaultHigh, NO_PREFERENCE);
    }
    
    /**
     * Constructor
     * @param lowLimit - lowest clickable key
     * @param highLimit - highest clickable key
     * @param parent - frame that spawned this dialog box
     */
    public RangeChooser(int lowLimit, int highLimit, java.awt.Frame parent){
        this(parent, NO_PREFERENCE, NO_PREFERENCE, lowLimit, highLimit);
    }
    
    /**
     * Constructor
     * @param lowLimit lowest clickable key
     * @param highLimit highest clickable key
     * @param minimumRange minimum size of range that user must pick
     * @param parent frame that spawned this dialog box
     */
    public RangeChooser(int lowLimit, int highLimit, int minimumRange, java.awt.Frame parent){
        this(parent, NO_PREFERENCE, NO_PREFERENCE, minimumRange, lowLimit, highLimit);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param fullKeyboard true to have the full keyboard be clickable,
     * false to have the keyboard range selected according to the current stave
     */
    public RangeChooser(java.awt.Frame parent, boolean fullKeyboard){
        this(A0, C8, parent);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     * @param fullKeyboard true to have the full keyboard be clickable,
     * false to have the keyboard range selected according to the current stave
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh, boolean fullKeyboard){
        this(parent, defaultLow, defaultHigh, A0, C8);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param minimumRange minimum size of range that user must pick
     * @param fullKeyboard true to have the full keyboard be clickable,
     * false to have the keyboard range selected according to the current stave
     */
    public RangeChooser(java.awt.Frame parent, int minimumRange, boolean fullKeyboard){
        this(parent, NO_PREFERENCE, NO_PREFERENCE, minimumRange, A0, C8);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     * @param minimumRange minimum size of range that user must pick
     * @param fullKeyboard true to have the full keyboard be clickable,
     * false to have the keyboard range selected according to the current stave
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh, int minimumRange, boolean fullKeyboard){
        this(parent, defaultLow, defaultHigh, minimumRange, A0, C8);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     */
    public RangeChooser(java.awt.Frame parent){
        this(parent, NO_PREFERENCE, NO_PREFERENCE);
    }

    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     * @param minimumRange minimum size of range that user must pick
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh, int minimumRange){
        this(parent, defaultLow, defaultHigh, minimumRange, NO_PREFERENCE, NO_PREFERENCE);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     * @param lowLimit lowest key that is clickable
     * @param highLimit highest key that is clickable
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh, int lowLimit, int highLimit){
        this(parent, defaultLow, defaultHigh, NO_PREFERENCE, lowLimit, highLimit);
    }
    
    /**
     * Constructor
     * @param parent frame that spawned this dialog box
     * @param defaultLow midi value of low key to be initially selected
     * @param defaultHigh midi value of high key to be initially selected
     * @param minimumRange minimum size of range that user must pick
     * @param lowLimit lowest key that is clickable
     * @param highLimit highest key that is clikable
     */
    public RangeChooser(java.awt.Frame parent, int defaultLow, int defaultHigh, int minimumRange, int lowLimit, int highLimit) {
        
        //RangeChooser is always modal
        super(parent, true);
        
        //set minimum range
        if(minimumRange<0){
            this.minimumRange = NO_PREFERENCE;
        }else{
            this.minimumRange = minimumRange;
        }   
        
        //Set range of clickable keys and default selected range
        //based on the current stave
        StaveType stave = Preferences.getStaveTypeFromPreferences();
        
        int [] programDefaults;
        int [] defaultLimits;
        
        if(stave==StaveType.TREBLE){
            defaultLimits = trebleLimits;
            programDefaults = trebleDefaults;
        }else if(stave==StaveType.BASS){
            defaultLimits = bassLimits;
            programDefaults = bassDefaults;
        }else{
            defaultLimits = grandLimits;
            programDefaults = grandDefaults;
        }
        
        int [] userLimits = {lowLimit, highLimit};
        if(limitsOkay(lowLimit, highLimit)){
            limits = userLimits;
            //use stave-determined program defaults for initial notes clicked
            //if those aren't within the not-blue notes, use the range limits
            if(!rangeOkay(programDefaults)){
                programDefaults = userLimits;
            }
        }else{
            limits = defaultLimits;
        }
        
        //set the two initially clicked keys
        //use the user defaults if they're valid, program defaults otherwise
        int [] userDefaults = {defaultLow, defaultHigh};
        clicked = rangeOkay(userDefaults) ? userDefaults : programDefaults;
        
        //initialize graphical components
        initComponents();
        
        //initialize the array of piano keys
        initKeys();
        
        //make the out-of-range keys blue & unclickable
        setBlueKeys();
        
        //click the default keys
        pressKey(midiToKey(clicked[LOW]));
        pressKey(midiToKey(clicked[HIGH]));
        
        //make dialog box visible
        this.setVisible(true);
    }

    /**
     * limitsOkay
     * returns whether the range specified by the limits is within the keyboard
     * and the lower limit is < than the upper limit
     * @param low the lower limit
     * @param high the upper limit
     * @return whether or not the limits are okay
     */
    private boolean limitsOkay(int low, int high){
        return low>=A0&&high<=C8&&low<high;
    }
    
    /**
     * getRange - for public use
     * @return an int array containing the midi values of the
     * two keys selected by the user
     */
    public int [] getRange(){
        return clicked;
    }
    
    /**
     * inRange
     * @param midi midi value of note
     * @return true if note is within click-able range
     */
    private boolean inRange(int midi){
        return midi >= limits[LOW] && midi <= limits[HIGH];
    }
    
    /**
     * rangeOkay
     * Determines whether a range given by a two-element array is okay
     * @param limits two-element array of ints
     * @return whether the range is okay (false if array doesn't contain 2 ints)
     */
    private boolean rangeOkay(int [] limits){
        if(limits.length==2){
            return rangeOkay(limits[LOW], limits[HIGH]);
        }else{
            return false;
        }
    }
    
    /**
     * rangeOkay
     * @param low midi value of low note
     * @param high midi value of high note
     * @return true if range from the low note to the high note is valid,
     * i.e. if both the low and high note are in range,
     * the low note is lower than the high note,
     * and the distance between the low and high notes is > the minimum range
     */
    private boolean rangeOkay(int low, int high){
        return  inRange(low)
                && inRange(high)
                && (low < high)
                && distance(low, high) >= minimumRange;
    }
    
    /**
     * distance
     * @param low midi value of low note
     * @param high midi value of high note
     * @return absolute distance between the two notes
     */
    private int distance(int low, int high){
        return Math.abs(high-low);
    }
    
    /**
     * pianoKeys
     * @return pkeys, the array of piano keys
     */
    public PianoKey[] pianoKeys(){
        return pkeys;
    }
    
    /**
     * setKeyboard
     * @param midiValue midi value of key clicked
     */
    public void setKeyboard(int midiValue){
        
        //If note within clickable range (not blue)
        if(midiValue >= limits[LOW] && midiValue <= limits[HIGH]){
            
            int keyIndex = closer(midiValue);
            replaceKey(keyIndex, midiValue);

        }
    }
    
    /**
     * closer
     * @param midiValue midi value of note
     * @return LOW if note is closer to the currently selected low limit,
     * HIGH otherwise (tie break)
     */
    private int closer(int midiValue){
        return distance(midiValue, clicked[LOW]) < distance(midiValue, clicked[HIGH]) ? LOW : HIGH;
    }
    
    /**
     * replaceKey
     * @param keyIndex index (LOW or HIGH) of key to be un-clicked
     * @param newMidi midi value of note to be clicked
     */
    private void replaceKey(int keyIndex, int newMidi){

        //midi value to be replaced
        int oldMidi = clicked[keyIndex];
        
        //potential new low and high range limits
        int newLow = keyIndex == LOW ? newMidi : clicked[LOW];
        int newHigh = keyIndex == LOW ? clicked[HIGH] : newMidi;
        int [] newRange = {newLow, newHigh};
        
        //if the proposed new range is not ok, return
        if(!rangeOkay(newRange)){
            return;
        }
        
        //The new key
        PianoKey newKey = midiToKey(newMidi);
        
        //The old key
        PianoKey oldKey = midiToKey(oldMidi);
        
        //click the new key
        pressKey(newKey);
        
        //unclick the old key
        pressKey(oldKey);
        
        //store the new range
        clicked = newRange;

    }
    
    /**
     * midiToKey
     * Returns Piano Key corresponding to midi value
     * @param midi midi value
     * @return Piano Key corresponding to midi value
     */
    private PianoKey midiToKey(int midi){
        return pkeys[index(midi)];
    }
    
    /**
     * index
     * @param midi midi value of note
     * @return index at which that note's piano key is stored in the pkeys array
     */
    private int index(int midi){
        return midi-A;
    }

    /**
    * pressKey changes the images of the keys based on whether they have been
    * pressed or not.
    * 
    * @param keyPlayed the key to be played / un-played
    */
   private void pressKey(PianoKey keyPlayed){
       
        //we use the isBass feature to mark keys as blue/unclickable
        if(keyPlayed.isBass()){
            return;
        }
        
        //get key's label, onIcon, and offIcon
        JLabel label = keyPlayed.getLabel();
        Icon onIcon = keyPlayed.getOnIcon();
        Icon offIcon = keyPlayed.getOffIcon();

        //if the key is pressed, unpress it
        if (keyPlayed.isPressed()){
            
            label.setIcon(offIcon);
            keyPlayed.setPressed(false);
            
        }
        
        //if the key is unpressed, press it
        else if (!keyPlayed.isPressed()){
            
            label.setIcon(onIcon);
            keyPlayed.setPressed(true);
            
        }
        
        //force paint so keys update
        forcePaint();
    }
   
    /**
     * setBlueKeys makes keys outside the range limits blue/un-clickable
     */
    private void setBlueKeys(){
        for(int i = 0; i < index(limits[LOW]); ++i){
            makeBlue(i);
        }

        for(int i = index(limits[HIGH])+1; i < pkeys.length; ++i){
            makeBlue(i);
        }
    }
    
    /**
     * makeBlue
     * @param index index of key in pkeys to make blue
     * makes the key blue/un-clickable
     */
    private void makeBlue(int index){
        PianoKey key = pkeys[index];
        key.setIsBass(true);
        key.getLabel().setIcon(key.getBassOnIcon());
    }
   
   /**
    * Force painting the window, without waiting for repaint to do it,
    * as repaints may be queued when the calling application sleeps.
    */
    private void forcePaint(){
        paint(getGraphics());
    }
    
    /**
     * Initialize the array of piano keys
     */
    private void initKeys(){
        
        pkeys = new PianoKey[88];
        // 0th octave keys
        pkeys[0] = new PianoKey(21, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA0);
        pkeys[1] = new PianoKey(22, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb0);
        pkeys[2] = new PianoKey(23, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB0);

        // 1st octave keys
        pkeys[3] = new PianoKey(24, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC1);
        pkeys[4] = new PianoKey(25, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp1);
        pkeys[5] = new PianoKey(26, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD1);
        pkeys[6] = new PianoKey(27, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb1);
        pkeys[7] = new PianoKey(28, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE1);
        pkeys[8] = new PianoKey(29, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF1);
        pkeys[9] = new PianoKey(30, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp1);
        pkeys[10] = new PianoKey(31, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG1);
        pkeys[11] = new PianoKey(32, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp1);
        pkeys[12] = new PianoKey(33, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA1);
        pkeys[13] = new PianoKey(34, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb1);
        pkeys[14] = new PianoKey(35, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB1);

        // 2nd octave keys
        pkeys[15] = new PianoKey(36, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC2);
        pkeys[16] = new PianoKey(37, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp2);
        pkeys[17] = new PianoKey(38, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD2);
        pkeys[18] = new PianoKey(39, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb2);
        pkeys[19] = new PianoKey(40, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE2);
        pkeys[20] = new PianoKey(41, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF2);
        pkeys[21] = new PianoKey(42, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp2);
        pkeys[22] = new PianoKey(43, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG2);
        pkeys[23] = new PianoKey(44, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp2);
        pkeys[24] = new PianoKey(45, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA2);
        pkeys[25] = new PianoKey(46, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb2);
        pkeys[26] = new PianoKey(47, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB2);

        // 3rd octave keys
        pkeys[27] = new PianoKey(48, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC3);
        pkeys[28] = new PianoKey(49, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp3);
        pkeys[29] = new PianoKey(50, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD3);
        pkeys[30] = new PianoKey(51, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb3);
        pkeys[31] = new PianoKey(52, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE3);
        pkeys[32] = new PianoKey(53, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF3);
        pkeys[33] = new PianoKey(54, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp3);
        pkeys[34] = new PianoKey(55, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG3);
        pkeys[35] = new PianoKey(56, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp3);
        pkeys[36] = new PianoKey(57, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA3);
        pkeys[37] = new PianoKey(58, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb3);
        pkeys[38] = new PianoKey(59, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB3);

        // 4th octave keys
        pkeys[39] = new PianoKey(60, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC4);
        pkeys[40] = new PianoKey(61, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp4);
        pkeys[41] = new PianoKey(62, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD4);
        pkeys[42] = new PianoKey(63, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb4);
        pkeys[43] = new PianoKey(64, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE4);
        pkeys[44] = new PianoKey(65, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF4);
        pkeys[45] = new PianoKey(66, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp4);
        pkeys[46] = new PianoKey(67, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG4);
        pkeys[47] = new PianoKey(68, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp4);
        pkeys[48] = new PianoKey(69, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA4);
        pkeys[49] = new PianoKey(70, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb4);
        pkeys[50] = new PianoKey(71, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB4);

        // 5th octave keys
        pkeys[51] = new PianoKey(72, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC5);
        pkeys[52] = new PianoKey(73, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp5);
        pkeys[53] = new PianoKey(74, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD5);
        pkeys[54] = new PianoKey(75, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb5);
        pkeys[55] = new PianoKey(76, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE5);
        pkeys[56] = new PianoKey(77, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF5);
        pkeys[57] = new PianoKey(78, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp5);
        pkeys[58] = new PianoKey(79, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG5);
        pkeys[59] = new PianoKey(80, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp5);
        pkeys[60] = new PianoKey(81, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA5);
        pkeys[61] = new PianoKey(82, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb5);
        pkeys[62] = new PianoKey(83, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB5);

        // 6th octave keys
        pkeys[63] = new PianoKey(84, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC6);
        pkeys[64] = new PianoKey(85, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp6);
        pkeys[65] = new PianoKey(86, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD6);
        pkeys[66] = new PianoKey(87, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb6);
        pkeys[67] = new PianoKey(88, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE6);
        pkeys[68] = new PianoKey(89, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF6);
        pkeys[69] = new PianoKey(90, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp6);
        pkeys[70] = new PianoKey(91, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG6);
        pkeys[71] = new PianoKey(92, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp6);
        pkeys[72] = new PianoKey(93, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA6);
        pkeys[73] = new PianoKey(94, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb6);
        pkeys[74] = new PianoKey(95, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB6);

        // 7th octave keys
        pkeys[75] = new PianoKey(96, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC7);
        pkeys[76] = new PianoKey(97, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp7);
        pkeys[77] = new PianoKey(98, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD7);
        pkeys[78] = new PianoKey(99, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb7);
        pkeys[79] = new PianoKey(100, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE7);
        pkeys[80] = new PianoKey(101, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF7);
        pkeys[81] = new PianoKey(102, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp7);
        pkeys[82] = new PianoKey(103, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG7);
        pkeys[83] = new PianoKey(104, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp7);
        pkeys[84] = new PianoKey(105, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA7);
        pkeys[85] = new PianoKey(106, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb7);
        pkeys[86] = new PianoKey(107, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB7);

        // 8th octave keys
        pkeys[87] = new PianoKey(108, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC8);
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        instructionsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        keyboardPanel = new javax.swing.JPanel();
        keyboardLP = new javax.swing.JLayeredPane();
        keyA0 = new javax.swing.JLabel();
        keyB0 = new javax.swing.JLabel();
        keyC1 = new javax.swing.JLabel();
        keyD1 = new javax.swing.JLabel();
        keyE1 = new javax.swing.JLabel();
        keyF1 = new javax.swing.JLabel();
        keyG1 = new javax.swing.JLabel();
        keyA1 = new javax.swing.JLabel();
        keyB1 = new javax.swing.JLabel();
        keyC2 = new javax.swing.JLabel();
        keyD2 = new javax.swing.JLabel();
        keyE2 = new javax.swing.JLabel();
        keyF2 = new javax.swing.JLabel();
        keyG2 = new javax.swing.JLabel();
        keyA2 = new javax.swing.JLabel();
        keyB2 = new javax.swing.JLabel();
        keyC3 = new javax.swing.JLabel();
        keyD3 = new javax.swing.JLabel();
        keyE3 = new javax.swing.JLabel();
        keyF3 = new javax.swing.JLabel();
        keyG3 = new javax.swing.JLabel();
        keyA3 = new javax.swing.JLabel();
        keyB3 = new javax.swing.JLabel();
        keyC4 = new javax.swing.JLabel();
        keyD4 = new javax.swing.JLabel();
        keyE4 = new javax.swing.JLabel();
        keyF4 = new javax.swing.JLabel();
        keyG4 = new javax.swing.JLabel();
        keyA4 = new javax.swing.JLabel();
        keyB4 = new javax.swing.JLabel();
        keyC5 = new javax.swing.JLabel();
        keyD5 = new javax.swing.JLabel();
        keyE5 = new javax.swing.JLabel();
        keyF5 = new javax.swing.JLabel();
        keyG5 = new javax.swing.JLabel();
        keyA5 = new javax.swing.JLabel();
        keyB5 = new javax.swing.JLabel();
        keyC6 = new javax.swing.JLabel();
        keyD6 = new javax.swing.JLabel();
        keyE6 = new javax.swing.JLabel();
        keyF6 = new javax.swing.JLabel();
        keyG6 = new javax.swing.JLabel();
        keyA6 = new javax.swing.JLabel();
        keyB6 = new javax.swing.JLabel();
        keyC7 = new javax.swing.JLabel();
        keyD7 = new javax.swing.JLabel();
        keyE7 = new javax.swing.JLabel();
        keyF7 = new javax.swing.JLabel();
        keyG7 = new javax.swing.JLabel();
        keyA7 = new javax.swing.JLabel();
        keyB7 = new javax.swing.JLabel();
        keyC8 = new javax.swing.JLabel();
        keyBb0 = new javax.swing.JLabel();
        keyCsharp1 = new javax.swing.JLabel();
        keyEb1 = new javax.swing.JLabel();
        keyFsharp1 = new javax.swing.JLabel();
        keyGsharp1 = new javax.swing.JLabel();
        keyBb1 = new javax.swing.JLabel();
        keyCsharp2 = new javax.swing.JLabel();
        keyEb2 = new javax.swing.JLabel();
        keyFsharp2 = new javax.swing.JLabel();
        keyGsharp2 = new javax.swing.JLabel();
        keyBb2 = new javax.swing.JLabel();
        keyCsharp3 = new javax.swing.JLabel();
        keyEb3 = new javax.swing.JLabel();
        keyFsharp3 = new javax.swing.JLabel();
        keyGsharp3 = new javax.swing.JLabel();
        keyBb3 = new javax.swing.JLabel();
        keyCsharp4 = new javax.swing.JLabel();
        keyEb4 = new javax.swing.JLabel();
        keyFsharp4 = new javax.swing.JLabel();
        keyGsharp4 = new javax.swing.JLabel();
        keyBb4 = new javax.swing.JLabel();
        keyCsharp5 = new javax.swing.JLabel();
        keyEb5 = new javax.swing.JLabel();
        keyFsharp5 = new javax.swing.JLabel();
        keyGsharp5 = new javax.swing.JLabel();
        keyBb5 = new javax.swing.JLabel();
        keyCsharp6 = new javax.swing.JLabel();
        keyEb6 = new javax.swing.JLabel();
        keyFsharp6 = new javax.swing.JLabel();
        keyGsharp6 = new javax.swing.JLabel();
        keyBb6 = new javax.swing.JLabel();
        keyCsharp7 = new javax.swing.JLabel();
        keyEb7 = new javax.swing.JLabel();
        keyFsharp7 = new javax.swing.JLabel();
        keyGsharp7 = new javax.swing.JLabel();
        keyBb7 = new javax.swing.JLabel();
        pointerC4 = new javax.swing.JLabel();
        okPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        instructionsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Select a range using the keyboard below.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(41, 0, 42, 0);
        instructionsPanel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        getContentPane().add(instructionsPanel, gridBagConstraints);

        keyboardPanel.setLayout(new java.awt.GridBagLayout());

        keyboardLP.setMinimumSize(new java.awt.Dimension(1045, 150));
        keyboardLP.setRequestFocusEnabled(false);
        keyboardLP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyboardLPMouseClicked(evt);
            }
        });

        keyA0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA0);
        keyA0.setBounds(0, 0, 20, 120);

        keyB0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB0);
        keyB0.setBounds(20, 0, 20, 120);

        keyC1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC1);
        keyC1.setBounds(40, 0, 20, 120);

        keyD1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD1);
        keyD1.setBounds(60, 0, 20, 120);

        keyE1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE1);
        keyE1.setBounds(80, 0, 20, 120);

        keyF1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF1);
        keyF1.setBounds(100, 0, 20, 120);

        keyG1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG1);
        keyG1.setBounds(120, 0, 20, 120);

        keyA1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA1);
        keyA1.setBounds(140, 0, 20, 120);

        keyB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB1);
        keyB1.setBounds(160, 0, 20, 120);

        keyC2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC2);
        keyC2.setBounds(180, 0, 20, 120);

        keyD2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD2);
        keyD2.setBounds(200, 0, 20, 120);

        keyE2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE2);
        keyE2.setBounds(220, 0, 20, 120);

        keyF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF2);
        keyF2.setBounds(240, 0, 20, 120);

        keyG2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG2);
        keyG2.setBounds(260, 0, 20, 120);

        keyA2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA2);
        keyA2.setBounds(280, 0, 20, 120);

        keyB2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB2);
        keyB2.setBounds(300, 0, 20, 120);

        keyC3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC3);
        keyC3.setBounds(320, 0, 20, 120);

        keyD3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD3);
        keyD3.setBounds(340, 0, 20, 120);

        keyE3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE3);
        keyE3.setBounds(360, 0, 20, 120);

        keyF3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF3);
        keyF3.setBounds(380, 0, 20, 120);

        keyG3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG3);
        keyG3.setBounds(400, 0, 20, 120);

        keyA3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA3);
        keyA3.setBounds(420, 0, 20, 120);

        keyB3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB3);
        keyB3.setBounds(440, 0, 20, 120);

        keyC4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC4);
        keyC4.setBounds(460, 0, 20, 120);

        keyD4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD4);
        keyD4.setBounds(480, 0, 20, 120);

        keyE4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE4);
        keyE4.setBounds(500, 0, 20, 120);

        keyF4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF4);
        keyF4.setBounds(520, 0, 20, 120);

        keyG4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG4);
        keyG4.setBounds(540, 0, 20, 120);

        keyA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA4);
        keyA4.setBounds(560, 0, 20, 120);

        keyB4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB4);
        keyB4.setBounds(580, 0, 20, 120);

        keyC5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC5);
        keyC5.setBounds(600, 0, 20, 120);

        keyD5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD5);
        keyD5.setBounds(620, 0, 20, 120);

        keyE5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE5);
        keyE5.setBounds(640, 0, 20, 120);

        keyF5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF5);
        keyF5.setBounds(660, 0, 20, 120);

        keyG5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG5);
        keyG5.setBounds(680, 0, 20, 120);

        keyA5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA5);
        keyA5.setBounds(700, 0, 20, 120);

        keyB5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB5);
        keyB5.setBounds(720, 0, 20, 120);

        keyC6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC6);
        keyC6.setBounds(740, 0, 20, 120);

        keyD6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD6);
        keyD6.setBounds(760, 0, 20, 120);

        keyE6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE6);
        keyE6.setBounds(780, 0, 20, 120);

        keyF6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF6);
        keyF6.setBounds(800, 0, 20, 120);

        keyG6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG6);
        keyG6.setBounds(820, 0, 20, 120);

        keyA6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA6);
        keyA6.setBounds(840, 0, 20, 120);

        keyB6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB6);
        keyB6.setBounds(860, 0, 20, 120);

        keyC7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC7);
        keyC7.setBounds(880, 0, 20, 120);

        keyD7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD7);
        keyD7.setBounds(900, 0, 20, 120);

        keyE7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE7);
        keyE7.setBounds(920, 0, 20, 120);

        keyF7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF7);
        keyF7.setBounds(940, 0, 20, 120);

        keyG7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG7);
        keyG7.setBounds(960, 0, 20, 120);

        keyA7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA7);
        keyA7.setBounds(980, 0, 20, 120);

        keyB7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB7);
        keyB7.setBounds(1000, 0, 20, 120);

        keyC8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC8);
        keyC8.setBounds(1020, 0, 20, 120);

        keyBb0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb0);
        keyBb0.setBounds(13, 0, 14, 80);
        keyboardLP.setLayer(keyBb0, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp1);
        keyCsharp1.setBounds(53, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb1);
        keyEb1.setBounds(73, 0, 14, 80);
        keyboardLP.setLayer(keyEb1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp1);
        keyFsharp1.setBounds(113, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp1);
        keyGsharp1.setBounds(133, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb1);
        keyBb1.setBounds(153, 0, 14, 80);
        keyboardLP.setLayer(keyBb1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp2);
        keyCsharp2.setBounds(193, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb2);
        keyEb2.setBounds(213, 0, 14, 80);
        keyboardLP.setLayer(keyEb2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp2);
        keyFsharp2.setBounds(253, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp2);
        keyGsharp2.setBounds(273, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb2);
        keyBb2.setBounds(293, 0, 14, 80);
        keyboardLP.setLayer(keyBb2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp3);
        keyCsharp3.setBounds(333, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb3);
        keyEb3.setBounds(353, 0, 14, 80);
        keyboardLP.setLayer(keyEb3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp3);
        keyFsharp3.setBounds(393, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp3);
        keyGsharp3.setBounds(413, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb3);
        keyBb3.setBounds(433, 0, 14, 80);
        keyboardLP.setLayer(keyBb3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp4);
        keyCsharp4.setBounds(473, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb4);
        keyEb4.setBounds(493, 0, 14, 80);
        keyboardLP.setLayer(keyEb4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp4);
        keyFsharp4.setBounds(533, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp4);
        keyGsharp4.setBounds(553, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb4);
        keyBb4.setBounds(573, 0, 14, 80);
        keyboardLP.setLayer(keyBb4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp5);
        keyCsharp5.setBounds(613, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb5);
        keyEb5.setBounds(633, 0, 14, 80);
        keyboardLP.setLayer(keyEb5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp5);
        keyFsharp5.setBounds(673, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp5);
        keyGsharp5.setBounds(693, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb5);
        keyBb5.setBounds(713, 0, 14, 80);
        keyboardLP.setLayer(keyBb5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp6);
        keyCsharp6.setBounds(753, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb6);
        keyEb6.setBounds(773, 0, 14, 80);
        keyboardLP.setLayer(keyEb6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp6);
        keyFsharp6.setBounds(813, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp6);
        keyGsharp6.setBounds(833, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb6);
        keyBb6.setBounds(853, 0, 14, 80);
        keyboardLP.setLayer(keyBb6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp7);
        keyCsharp7.setBounds(893, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb7);
        keyEb7.setBounds(913, 0, 14, 80);
        keyboardLP.setLayer(keyEb7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp7);
        keyFsharp7.setBounds(953, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp7);
        keyGsharp7.setBounds(973, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb7);
        keyBb7.setBounds(993, 0, 14, 80);
        keyboardLP.setLayer(keyBb7, javax.swing.JLayeredPane.PALETTE_LAYER);

        pointerC4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/pointer.png"))); // NOI18N
        keyboardLP.add(pointerC4);
        pointerC4.setBounds(460, 120, 19, 30);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1045;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        keyboardPanel.add(keyboardLP, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(keyboardPanel, gridBagConstraints);

        okPanel.setLayout(new java.awt.GridBagLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 26, 39, 27);
        okPanel.add(okButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(okPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void keyboardLPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_keyboardLPMouseClicked

        // Getting the position of the mouse click
        int y = evt.getY();
        int x = evt.getX();

        if (y < WKHEIGHT)
        {
            // True if the user clicked a black key.
            boolean blackPianoKey = false;

            // Determines the key number
            int keyNum = x / WKWIDTH;

            int note = keyNum;

            // gives the octave number (ex. 4 in C4 for middle C) by
            // determining where x is in relation to the pixel width of an octave
            int octave = ( (x + 5*WKWIDTH) / OCTAVE_WIDTH);

            // Only occurs if the click is at a y position that could be a black key
            if (y < BKHEIGHT) {
                // find the position of the click within the key
                int inKey = x - keyNum*WKWIDTH;

                // if click is in right half of black key
                if (inKey < (BKWIDTH/2 + 1)){
                    blackPianoKey = true;
                    note -= 1;

                    // not on a black key if note number is 1 or 4
                    if (note % OCTKEYS == 1 || note % OCTKEYS == 4) {
                        blackPianoKey = false;
                    }
                }

                // if click is in left half of black key
                else if (inKey > WKWIDTH - (BKWIDTH/2 + 1)) {
                    blackPianoKey = true;
                    note = keyNum;

                    // not on a black key if note number is 1 or 4
                    if (note % OCTKEYS == 1 || note % OCTKEYS == 4) {
                        blackPianoKey = false;
                    }
                }
            }

            // determine the MIDI value of the note clicked
            int baseMidi = 0;

            int oct = note - OCTKEYS*(octave - 1);

            if (octave == 0) {
                oct = note - OCTKEYS*octave;
            }

            // if the note is a black key
            if (blackPianoKey)
            {
                switch(oct) {
                    case 0:
                    baseMidi = A + 1;     //Bb
                    break;
                    case 2:
                    baseMidi = A + 4;     //C#
                    break;
                    case 3:
                    baseMidi = A + 6;     //Eb
                    break;
                    case 5:
                    baseMidi = A + 9;     //F#
                    break;
                    case 6:
                    baseMidi = A + 11;    //G#
                    break;
                    case 7:
                    baseMidi = A + 13;    //Bb
                    break;
                }
            }
            // if the note is not a black key
            else
            {
                switch(oct) {
                    case 0:
                    baseMidi = A;      //A
                    break;
                    case 1:
                    baseMidi = A + 2;  //B
                    break;
                    case 2:
                    baseMidi = A + 3;  //C
                    break;
                    case 3:
                    baseMidi = A + 5;  //D
                    break;
                    case 4:
                    baseMidi = A + 7;  //E
                    break;
                    case 5:
                    baseMidi = A + 8;  //F
                    break;
                    case 6:
                    baseMidi = A + 10; //G
                    break;
                    case 7:
                    baseMidi = A + 12; //A
                    break;
                    case 8:
                    baseMidi = A + 14; //B
                    break;
                }
            }

            // Adjust the MIDI value for different octaves
            int midiValue = baseMidi + P_OCTAVE*(octave - 1);

            if (octave == 0) {
                midiValue = baseMidi;
            }
            
            setKeyboard(midiValue);
        }
    }//GEN-LAST:event_keyboardLPMouseClicked

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel instructionsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel keyA0;
    private javax.swing.JLabel keyA1;
    private javax.swing.JLabel keyA2;
    private javax.swing.JLabel keyA3;
    private javax.swing.JLabel keyA4;
    private javax.swing.JLabel keyA5;
    private javax.swing.JLabel keyA6;
    private javax.swing.JLabel keyA7;
    private javax.swing.JLabel keyB0;
    private javax.swing.JLabel keyB1;
    private javax.swing.JLabel keyB2;
    private javax.swing.JLabel keyB3;
    private javax.swing.JLabel keyB4;
    private javax.swing.JLabel keyB5;
    private javax.swing.JLabel keyB6;
    private javax.swing.JLabel keyB7;
    private javax.swing.JLabel keyBb0;
    private javax.swing.JLabel keyBb1;
    private javax.swing.JLabel keyBb2;
    private javax.swing.JLabel keyBb3;
    private javax.swing.JLabel keyBb4;
    private javax.swing.JLabel keyBb5;
    private javax.swing.JLabel keyBb6;
    private javax.swing.JLabel keyBb7;
    private javax.swing.JLabel keyC1;
    private javax.swing.JLabel keyC2;
    private javax.swing.JLabel keyC3;
    private javax.swing.JLabel keyC4;
    private javax.swing.JLabel keyC5;
    private javax.swing.JLabel keyC6;
    private javax.swing.JLabel keyC7;
    private javax.swing.JLabel keyC8;
    private javax.swing.JLabel keyCsharp1;
    private javax.swing.JLabel keyCsharp2;
    private javax.swing.JLabel keyCsharp3;
    private javax.swing.JLabel keyCsharp4;
    private javax.swing.JLabel keyCsharp5;
    private javax.swing.JLabel keyCsharp6;
    private javax.swing.JLabel keyCsharp7;
    private javax.swing.JLabel keyD1;
    private javax.swing.JLabel keyD2;
    private javax.swing.JLabel keyD3;
    private javax.swing.JLabel keyD4;
    private javax.swing.JLabel keyD5;
    private javax.swing.JLabel keyD6;
    private javax.swing.JLabel keyD7;
    private javax.swing.JLabel keyE1;
    private javax.swing.JLabel keyE2;
    private javax.swing.JLabel keyE3;
    private javax.swing.JLabel keyE4;
    private javax.swing.JLabel keyE5;
    private javax.swing.JLabel keyE6;
    private javax.swing.JLabel keyE7;
    private javax.swing.JLabel keyEb1;
    private javax.swing.JLabel keyEb2;
    private javax.swing.JLabel keyEb3;
    private javax.swing.JLabel keyEb4;
    private javax.swing.JLabel keyEb5;
    private javax.swing.JLabel keyEb6;
    private javax.swing.JLabel keyEb7;
    private javax.swing.JLabel keyF1;
    private javax.swing.JLabel keyF2;
    private javax.swing.JLabel keyF3;
    private javax.swing.JLabel keyF4;
    private javax.swing.JLabel keyF5;
    private javax.swing.JLabel keyF6;
    private javax.swing.JLabel keyF7;
    private javax.swing.JLabel keyFsharp1;
    private javax.swing.JLabel keyFsharp2;
    private javax.swing.JLabel keyFsharp3;
    private javax.swing.JLabel keyFsharp4;
    private javax.swing.JLabel keyFsharp5;
    private javax.swing.JLabel keyFsharp6;
    private javax.swing.JLabel keyFsharp7;
    private javax.swing.JLabel keyG1;
    private javax.swing.JLabel keyG2;
    private javax.swing.JLabel keyG3;
    private javax.swing.JLabel keyG4;
    private javax.swing.JLabel keyG5;
    private javax.swing.JLabel keyG6;
    private javax.swing.JLabel keyG7;
    private javax.swing.JLabel keyGsharp1;
    private javax.swing.JLabel keyGsharp2;
    private javax.swing.JLabel keyGsharp3;
    private javax.swing.JLabel keyGsharp4;
    private javax.swing.JLabel keyGsharp5;
    private javax.swing.JLabel keyGsharp6;
    private javax.swing.JLabel keyGsharp7;
    private javax.swing.JLayeredPane keyboardLP;
    private javax.swing.JPanel keyboardPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okPanel;
    private javax.swing.JLabel pointerC4;
    // End of variables declaration//GEN-END:variables
}
