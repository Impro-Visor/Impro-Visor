/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.voicing;

import imp.ImproVisor;
import imp.data.advice.Advisor;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.gui.RangeChooser;
import imp.style.StyleEditor;
import imp.gui.WindowRegistry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *Graphical user interface to allow user to modify voicing settings
 * @author Daniel Scanteianu, Errick Jackson
 */
public class ControlPanelFrame extends javax.swing.JFrame implements Serializable{

    /**
     * Creates new form ControlPanelFrame
     */
    public ControlPanelFrame(AutomaticVoicingSettings avs1) {
        WindowRegistry.registerWindow(this);
        this.avs=avs1;
        initComponents();
        associateSliders();
        syncFromSettings();
        setSlidersToVariables();
        setLabels();
        System.out.println("Lower Left hand: "+avs.getLeftHandLowerLimit());
        this.setTitle("Automated Voicing Generator Settings Editor");
        setTitle(AVPFileCreator.getLastFileName());
        File openFile=new File(ImproVisor.getVoicingDirectory(),AVPFileCreator.getLastFileName());
        //JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
        AVPFileCreator.fileToSettings( openFile, avs);
        setSlidersToVariables();
        setTitle(openFile.getName());
        setLabels();
        /*jButton1.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               if(voiceAll)
                    if(rightHandSpread<12)
                    {
                        rightHandSpread=12;
                        handSpreads[1].setValue(rightHandSpread);
                    }
               saveSlidersToVariables();
           }
       });  
         jButton2.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               setSlidersToVariables();
           }
       });*/
        jButton3.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              resetValues();
           }
       }); 
       /*loadFile.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               File openFile=null;
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile=chooser.getSelectedFile();
                }
                
                AVPFileCreator.fileToSettings( openFile, avs);
                setSlidersToVariables();
                setTitle(openFile.getName());
                styleEditor.setVoicingFileName(openFile.getName());
                
       
                 
            
            //setSlidersToVariables();
            
           }
       });*/
       /*
        jButton5.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
                
                saveSlidersToVariables();
                
                File saveFile=null;
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile=chooser.getSelectedFile();
                    if(!saveFile.getName().toLowerCase().endsWith(".avp"))
                        saveFile=new File(saveFile.getAbsolutePath()+".avp");
                    if(saveFile.getName().contains(" "))
                        saveFile=new File(saveFile.getParent(),saveFile.getName().replaceAll(" ", "-"));
                }
                syncToSettings();
                AVPFileCreator.settingsToFile(avs,saveFile);
                
            }
       });*/
         LHRangeButton.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              saveSlidersToVariables();
              RangeChooser rc=new RangeChooser(null, leftHandLowerLimit,leftHandUpperLimit, 21, 108);
              int range[]=rc.getRange();
              System.out.println(leftHandUpperLimit+"limit old");
              leftHandLowerLimit=range[0];
              leftHandUpperLimit=range[1];
              syncToSettings();
              //LHLLSpinner.setValue(leftHandLowerLimit);
              //LHULSpinner.setValue(leftHandUpperLimit);
              //System.out.println(leftHandUpperLimit+"limit set");
              setSlidersToVariables();
            }
       });
         RHRangeButton.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              saveSlidersToVariables();
              RangeChooser rc=new RangeChooser(null, rightHandLowerLimit,rightHandUpperLimit, 21, 108);
              int range[]=rc.getRange();
              //System.out.println(leftHandUpperLimit+"limit old");
              rightHandLowerLimit=range[0];
              rightHandUpperLimit=range[1];
              syncToSettings();
              //LHLLSpinner.setValue(leftHandLowerLimit);
              //LHULSpinner.setValue(leftHandUpperLimit);
              //System.out.println(leftHandUpperLimit+"limit set");
              setSlidersToVariables();
            }
       });
       
       loadDefault.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              File openFile=new File(ImproVisor.getVoicingDirectory(),styleEditor.getVoicingFileName());
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                AVPFileCreator.fileToSettings( openFile, avs);
                setSlidersToVariables();
                setTitle(openFile.getName());
           }
       });
       
       /* saveToNew.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e)
           {
                
                saveSlidersToVariables();
                syncToSettings();
                File saveFile=new File(ImproVisor.getVoicingDirectory(),styleEditor.getVoicingFileName());
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile=chooser.getSelectedFile();
                    if(!saveFile.getName().toLowerCase().endsWith(".avp"))
                        saveFile=new File(saveFile.getAbsolutePath()+".avp");   
                    if(saveFile.getName().contains(" "))
                        saveFile=new File(saveFile.getParent(),saveFile.getName().replaceAll(" ", "-"));
                }
                //syncToSettings();
                //AVPFileCreator.fileToSettings(openFile,avs);
                AVPFileCreator.settingsToFile(avs,saveFile);
                syncFromSettings();
                styleEditor.setVoicingFileName(saveFile.getName());
                setTitle(saveFile.getName());
            }
       });
       saveToDefault.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
                
                saveSlidersToVariables();
                
                File saveFile=new File(ImproVisor.getVoicingDirectory(),styleEditor.getVoicingFileName());
                
                syncToSettings();
                AVPFileCreator.settingsToFile(avs,saveFile);
                
            }
       });*/
       for(JSpinner slider:handLimits)
       {
           slider.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e)
           {
              setLabels();
           }
       });
           
       }
       
    }

    public ControlPanelFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
        public void dispose()
          {



          WindowRegistry.unregisterWindow(this);
          super.dispose();
          }
    private void setVoicingFile(File file)
    { AVPFileCreator.settingsToFile(avs, file);
                setSlidersToVariables();
    }
    /**
     * hard coded defaults for when defaults button is pressed.
     */
    /*
    private void setDefaults()
    {
        leftHandLowerLimit=46;
        rightHandLowerLimit=60;
        leftHandUpperLimit=67;
        rightHandUpperLimit=81;
        leftHandSpread=9;
        rightHandSpread=9;
        leftHandMinNotes=1;
        leftHandMaxNotes=2;
        rightHandMinNotes=1;
        rightHandMaxNotes=4;
        //voice leading controls
        preferredMotion=0;
        preferredMotionRange=3;
        previousVoicingMultiplier=4;// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=3;
        fullStepAwayMultiplier=2;
        //voicing control
        leftColorPriority=0;//priority of any color note
        rightColorPriority=0;
        maxPriority=6;//max priority a note in the priority array can have
        priorityMultiplier=.667;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=.3;
        halfStepReducer=0;
        fullStepReducer=.7;
        syncToSettings();
    }*/
    /**
     * Sets the values in the automatic voicing settings object to the variables
     */
    public void syncToSettings()
    {
        avs.setLeftHandLowerLimit(leftHandLowerLimit);
        avs.setRightHandLowerLimit(rightHandLowerLimit);
        avs.setLeftHandUpperLimit(leftHandUpperLimit);
        avs.setRightHandUpperLimit(rightHandUpperLimit);
        avs.setLeftHandSpread(leftHandSpread);
        avs.setRightHandSpread(rightHandSpread);
        avs.setLeftHandMinNotes(leftHandMinNotes);
        avs.setLeftHandMaxNotes(leftHandMaxNotes);
        avs.setRightHandMinNotes(rightHandMinNotes);
        avs.setRightHandMaxNotes(rightHandMaxNotes);
        //voice leading controls
        avs.setPreferredMotion(preferredMotion);
        avs.setPreferredMotionRange(preferredMotionRange);
        avs.setPreviousVoicingMultiplier(previousVoicingMultiplier);
        avs.setHalfStepAwayMultiplier(halfStepAwayMultiplier);
        avs.setFullStepAwayMultiplier(fullStepAwayMultiplier);
        //voicing control
        avs.setLeftColorPriority(leftColorPriority);
        avs.setRightColorPriority(rightColorPriority);
        avs.setMaxPriority(maxPriority);
        avs.setPriorityMultiplier(priorityMultiplier);
        avs.setRepeatMultiplier(repeatMultiplier);
        avs.setHalfStepReducer(halfStepReducer);
        avs.setFullStepReducer(fullStepReducer);
        avs.setInvertM9(invertM9);
        avs.setVoiceAll(voiceAll);
        avs.setRootless(rootless);
        avs.setLeftMinInterval(leftMinInterval);
        avs.setRightMinInterval(rightMinInterval);
    }
    /**
     * sets the variables to the values in the settings object
     */
    public void syncFromSettings()
    {
        leftHandLowerLimit=avs.getLeftHandLowerLimit();
        rightHandLowerLimit=avs.getRightHandLowerLimit();
        leftHandUpperLimit=avs.getLeftHandUpperLimit();
        rightHandUpperLimit=avs.getRightHandUpperLimit();
        leftHandSpread=avs.getLeftHandSpread();
        rightHandSpread=avs.getRightHandSpread();
        leftHandMinNotes=avs.getLeftHandMinNotes();
        leftHandMaxNotes=avs.getLeftHandMaxNotes();
        rightHandMinNotes=avs.getRightHandMinNotes();
        rightHandMaxNotes=avs.getRightHandMaxNotes();
        //voice leading controls
        preferredMotion=avs.getPreferredMotion();
        preferredMotionRange=avs.getPreferredMotionRange();
        previousVoicingMultiplier=avs.getPreviousVoicingMultiplier();// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=avs.getHalfStepAwayMultiplier();
        fullStepAwayMultiplier=avs.getFullStepAwayMultiplier();
        //voicing control
        leftColorPriority=avs.getLeftColorPriority();//priority of any color note
        rightColorPriority=avs.getRightColorPriority();
        maxPriority=avs.getMaxPriority();//max priority a note in the priority array can have
        priorityMultiplier=avs.getPriorityMultiplier();//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=avs.getRepeatMultiplier();
        halfStepReducer=avs.getHalfStepReducer();
        fullStepReducer=avs.getFullStepReducer();
        invertM9=avs.getInvertM9();
        voiceAll=avs.getVoiceAll();
        rootless=avs.getRootless();
        leftMinInterval=avs.getLeftMinInterval();
        rightMinInterval=avs.getRightMinInterval();
    }
        /*
    private void setupHandParams()
    {
        handLimits=new JSlider[4];
        handLimits[0]=new JSlider(21,108,leftHandLowerLimit);
        handLimits[1]=new JSlider(21,108,leftHandUpperLimit);
        handLimits[2]=new JSlider(21,108,rightHandLowerLimit);
        handLimits[3]=new JSlider(21,108,rightHandUpperLimit);
        handSpreads=new JSlider[2];
        handSpreads[0]=new JSlider(7,19,leftHandSpread);
        handSpreads[1]=new JSlider(7,19,rightHandSpread);
        handNotes=new JSlider[4];
        handNotes[0]=new JSlider(0,5, leftHandMinNotes);
        handNotes[1]=new JSlider(0,5, leftHandMaxNotes);
        handNotes[2]=new JSlider(0,5, rightHandMinNotes);
        handNotes[3]=new JSlider(0,5, rightHandMaxNotes);
        
    }
    private void setupVoicingParams()
    {
        colorPrioritySlider=new JSlider(0,10,colorPriority);
        maxPrioritySlider=new JSlider(0,10,maxPriority);
        repeatedNoteProbability=new JSlider(0,10,(int)(repeatMultiplier*10));
        priorityWeighting=new JSlider(0,15,(int)(colorPriority*10));
    }
    private void setupLeadingParams()
    {
        voiceLeadingWeights=new JSlider[3];//index=change in half steps
        voiceLeadingWeights[0]=new JSlider(0,50,(int)(previousVoicingMultiplier*10));//0 prevents previous note from being played
        voiceLeadingWeights[1]=new JSlider(0,50,(int)(halfStepAwayMultiplier*10));
        voiceLeadingWeights[2]=new JSlider(0,50,(int)(fullStepAwayMultiplier*10));
        preferredMotionSlider=new JSlider(-5,5,preferredMotion);
        motionRange=new JSlider(0,5,preferredMotionRange);
    }
        */
    public void resetValues()
    {
        avs.setDefaults();
        this.syncFromSettings();
        setSlidersToVariables();
    }
    /**
     * updates the note limit labels to the note on the sliders
     */
    public void setLabels()
    {
        for(int i=0; i<4; i++)
        {
            limitLabels[i].setText((NoteSymbol.makeNoteSymbol(new Note(Integer.parseInt(handLimits[i].getValue().toString()))).toString().replaceAll("4", "")));
        }
    }
    /**
     * Sets the sliders to the values in the variables
     */
    public void setSlidersToVariables()
    {
        syncFromSettings();
        handLimits[0].setValue(leftHandLowerLimit);
        handLimits[2].setValue(rightHandLowerLimit);
        handLimits[1].setValue(leftHandUpperLimit);
        handLimits[3].setValue(rightHandUpperLimit);
        handSpreads[0].setValue(leftHandSpread);
        handSpreads[1].setValue(rightHandSpread);
        handNotes[0].setValue(leftHandMinNotes);
        handNotes[1].setValue(leftHandMaxNotes);
        handNotes[2].setValue(rightHandMinNotes);
        handNotes[3].setValue(rightHandMaxNotes);
        //voice leading controls
        preferredMotionSlider.setValue(preferredMotion);
        motionRange.setValue(preferredMotionRange);
        voiceLeadingWeights[0].setValue((int)(previousVoicingMultiplier*10));// multiplier for notes used in previous voicing
        voiceLeadingWeights[1].setValue((int)(halfStepAwayMultiplier*10));
        voiceLeadingWeights[2].setValue((int)(fullStepAwayMultiplier*10));
        //voicing control
        leftColorPrioritySlider.setValue(leftColorPriority);//priority of any color note
        rightColorPrioritySlider.setValue(rightColorPriority);//priority of any color note
        maxPrioritySlider.setValue(maxPriority);//max priority a note in the priority array can have
        priorityWeighting.setValue((int)(priorityMultiplier*10));//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatedNoteProbability.setValue((int)(repeatMultiplier*10));
        closeNoteReducer[0].setValue((int)(halfStepReducer*10));
        closeNoteReducer[1].setValue((int)(fullStepReducer*10));
        invertBox.setState(invertM9);
        voiceAllNotes.setState(voiceAll);
        rootlessBox.setState(rootless);
        leftMinIntervalSpinner.setValue(leftMinInterval);
        rightMinIntervalSpinner.setValue(rightMinInterval);
    }
    /**
     * points sliders we declared and named to auto-created sliders from gui designer.
     */
     public void associateSliders()
    {
        handLimits=new JSpinner[4];
        handSpreads=new JSpinner[2];
        handNotes=new JSpinner[4];
        voiceLeadingWeights=new JSlider[3];
        //handLimits[0]=LHLLSpinner;
        //handLimits[1]=LHULSpinner;
        //handLimits[2]=RHLLSpinner;
        //handLimits[3]=RHULSpinner;
        handLimits[0]=new JSpinner();
        handLimits[1]=new JSpinner();
        handLimits[2]=new JSpinner();
        handLimits[3]=new JSpinner();
        handSpreads[0]=LHStretchSpinner;
        handSpreads[1]=RHStretchSpinner;
        handNotes[0]=LHMinNotesSpinner;
        handNotes[1]=LHMaxNotesSpinner;
        handNotes[2]=RHMinNotesSpinner;
        handNotes[3]=RHMaxNotesSpinner;
        
        //voice leading controls
        preferredMotionSlider=PrefMotionDirSlider;
        motionRange=PrefMotionDistSlider;
        voiceLeadingWeights[0]=PrevChordPrioritySlider;// multiplier for notes used in previous voicing
        voiceLeadingWeights[1]=HalfStepAwaySlider;
        voiceLeadingWeights[2]=WholeStepAwaySlider;
        //voicing control
        leftColorPrioritySlider=LHColorPriority;//priority of any color note
        rightColorPrioritySlider=RHColorPriority;
        maxPrioritySlider=ChordToneMaxPrioritySlider;
        priorityWeighting=ChordTonePriorityWeightSlider;
        repeatedNoteProbability=ProbSameNoteTwoOctavesSlider;
        limitLabels=new JLabel[4];
        limitLabels[0]=LHLLNote;
        limitLabels[1]=LHULNote;
        limitLabels[2]=RHLLNote;
        limitLabels[3]=RHULNote;
        closeNoteReducer=new JSlider[2];
        closeNoteReducer[0]=HalfStepRedSlider;
        closeNoteReducer[1]=WholeStepRedSlider;
        
        
    }
     /**
      * ensures the user's settings make sense logically (avoids sending the voicing program impossible settings
     */
    public void checkSliders()
    {
       if(Integer.parseInt(handLimits[1].getValue().toString())<Integer.parseInt(handLimits[0].getValue().toString())+Integer.parseInt(handSpreads[0].getValue().toString()))
           handLimits[1].setValue(Integer.parseInt(handLimits[0].getValue().toString())+Integer.parseInt(handSpreads[0].getValue().toString()));
       if(Integer.parseInt(handLimits[3].getValue().toString())<Integer.parseInt(handLimits[2].getValue().toString())+Integer.parseInt(handSpreads[1].getValue().toString()))
           handLimits[3].setValue(Integer.parseInt(handLimits[3].getValue().toString())+Integer.parseInt(handLimits[2].getValue().toString()));
       if(Integer.parseInt(handNotes[0].getValue().toString())>Integer.parseInt(handNotes[1].getValue().toString()))
           handNotes[0].setValue(Integer.parseInt(handNotes[1].getValue().toString()));
       if(Integer.parseInt(handNotes[2].getValue().toString())>Integer.parseInt(handNotes[3].getValue().toString()))
           handNotes[2].setValue(Integer.parseInt(handNotes[3].getValue().toString()));
       
    }
    /**
     * saves values in sliders to variables.
     */
    public void saveSlidersToVariables()
    {
        checkSliders();
        leftHandLowerLimit=Integer.parseInt(handLimits[0].getValue().toString());
        rightHandLowerLimit=Integer.parseInt(handLimits[2].getValue().toString());
        leftHandUpperLimit=Integer.parseInt(handLimits[1].getValue().toString());
        rightHandUpperLimit=Integer.parseInt(handLimits[3].getValue().toString());
        leftHandSpread=Integer.parseInt(handSpreads[0].getValue().toString());
        rightHandSpread=Integer.parseInt(handSpreads[1].getValue().toString());
        leftHandMinNotes=Integer.parseInt(handNotes[0].getValue().toString());
        leftHandMaxNotes=Integer.parseInt(handNotes[1].getValue().toString());
        rightHandMinNotes=Integer.parseInt(handNotes[2].getValue().toString());
        rightHandMaxNotes=Integer.parseInt(handNotes[3].getValue().toString());
        //voice leading controls
        preferredMotion=preferredMotionSlider.getValue();
        preferredMotionRange=motionRange.getValue();
        previousVoicingMultiplier=voiceLeadingWeights[0].getValue()/10.0;// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=voiceLeadingWeights[1].getValue()/10.0;
        fullStepAwayMultiplier=voiceLeadingWeights[2].getValue()/10.0;
        //voicing control
        leftColorPriority=leftColorPrioritySlider.getValue();//priority of any color note
        rightColorPriority=rightColorPrioritySlider.getValue();
        maxPriority=maxPrioritySlider.getValue();//max priority a note in the priority array can have
        priorityMultiplier=priorityWeighting.getValue()/10.0;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=repeatedNoteProbability.getValue()/10.0;
        halfStepReducer=closeNoteReducer[0].getValue()/10.0;
        fullStepReducer=closeNoteReducer[1].getValue()/10.0;
        invertM9=invertBox.getState();
        voiceAll=voiceAllNotes.getState();
        rootless=rootlessBox.getState();
        leftMinInterval=Integer.parseInt(leftMinIntervalSpinner.getValue().toString());
        rightMinInterval=Integer.parseInt(rightMinIntervalSpinner.getValue().toString());
        syncToSettings();
        
    }
    private JSpinner handLimits[];
    private JSpinner handSpreads[];
    private JSpinner handNotes[];
    private JSlider voiceLeadingWeights[];//index=change in half steps
    private JSlider preferredMotionSlider;
    private JSlider motionRange;
    private JSlider leftColorPrioritySlider;
    private JSlider rightColorPrioritySlider;
    private JSlider maxPrioritySlider;
    private JSlider repeatedNoteProbability;
    private JSlider priorityWeighting;
    private JSlider closeNoteReducer[];
    private JLabel limitLabels[];
    //hand params
    private int leftHandLowerLimit;
    private int rightHandLowerLimit;
    private int leftHandUpperLimit;
    private int rightHandUpperLimit;
    private int leftHandSpread;
    private int rightHandSpread;
    private int leftHandMinNotes;
    private int leftHandMaxNotes;
    private int rightHandMinNotes;
    private int rightHandMaxNotes;
    //voice leading controls
    private int preferredMotion;
    private int preferredMotionRange;
    private double previousVoicingMultiplier;// multiplier for notes used in previous voicing
    private double halfStepAwayMultiplier;
    private double fullStepAwayMultiplier;
    //voicing control
    private int leftColorPriority;//priority of any color note
    private int rightColorPriority;//priority of any color note
    private int maxPriority;//max priority a note in the priority array can have
    private double priorityMultiplier;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
    private double repeatMultiplier;
    private double halfStepReducer;
    private double fullStepReducer;
    private boolean invertM9;//true if minor ninths should be inverted to major sevenths
    private boolean voiceAll;// ensures all notes are voiced at least once
    private boolean rootless;// omits root note from voicing.
    private int leftMinInterval;
    private int rightMinInterval;

    public int getLeftMinInterval() {
        return leftMinInterval;
    }

    public void setLeftMinInterval(int leftMinInterval) {
        this.leftMinInterval = leftMinInterval;
    }

    public int getRightMinInterval() {
        return rightMinInterval;
    }

    public void setRightMinInterval(int rightMinInterval) {
        this.rightMinInterval = rightMinInterval;
    }

    public boolean isRootless() {
        return rootless;
    }
    public boolean getRootless() {
        return rootless;
    }
    public void setRootless(boolean rootless) {
        this.rootless = rootless;
    }
    private StyleEditor styleEditor;

    public StyleEditor getStyleEditor() {
        return styleEditor;
    }

    public void setStyleEditor(StyleEditor styleEditor) {
        this.styleEditor = styleEditor;
    }
    private AutomaticVoicingSettings avs;

    public AutomaticVoicingSettings getVoicingSettings() {
        return avs;
    }

    public void setAutomaticVoicingSettings(AutomaticVoicingSettings avs) {
        this.avs = avs;
    }

    public double getHalfStepReducer() {
        return halfStepReducer;
    }

    public void setHalfStepReducer(double halfStepReducer) {
        this.halfStepReducer = halfStepReducer;
    }

    public double getFullStepReducer() {
        return fullStepReducer;
    }

    public void setFullStepReducer(double fullStepReducer) {
        this.fullStepReducer = fullStepReducer;
    }

    public int getLeftHandLowerLimit() {
        return leftHandLowerLimit;
    }

    public void setLeftHandLowerLimit(int leftHandLowerLimit) {
        this.leftHandLowerLimit = leftHandLowerLimit;
    }

    public int getRightHandLowerLimit() {
        return rightHandLowerLimit;
    }

    public void setRightHandLowerLimit(int rightHandLowerLimit) {
        this.rightHandLowerLimit = rightHandLowerLimit;
    }

    public int getLeftHandUpperLimit() {
        return leftHandUpperLimit;
    }

    public void setLeftHandUpperLimit(int leftHandUpperLimit) {
        this.leftHandUpperLimit = leftHandUpperLimit;
    }

    public int getRightHandUpperLimit() {
        return rightHandUpperLimit;
    }

    public void setRightHandUpperLimit(int rightHandUpperLimit) {
        this.rightHandUpperLimit = rightHandUpperLimit;
    }

    public int getLeftHandSpread() {
        return leftHandSpread;
    }

    public void setLeftHandSpread(int leftHandSpread) {
        this.leftHandSpread = leftHandSpread;
    }

    public int getRightHandSpread() {
        return rightHandSpread;
    }

    public void setRightHandSpread(int rightHandSpread) {
        this.rightHandSpread = rightHandSpread;
    }

    public int getLeftHandMinNotes() {
        return leftHandMinNotes;
    }

    public void setLeftHandMinNotes(int leftHandMinNotes) {
        this.leftHandMinNotes = leftHandMinNotes;
    }

    public int getLeftHandMaxNotes() {
        return leftHandMaxNotes;
    }

    public void setLeftHandMaxNotes(int leftHandMaxNotes) {
        this.leftHandMaxNotes = leftHandMaxNotes;
    }

    public int getRightHandMinNotes() {
        return rightHandMinNotes;
    }

    public void setRightHandMinNotes(int rightHandMinNotes) {
        this.rightHandMinNotes = rightHandMinNotes;
    }

    public int getRightHandMaxNotes() {
        return rightHandMaxNotes;
    }

    public void setRightHandMaxNotes(int rightHandMaxNotes) {
        this.rightHandMaxNotes = rightHandMaxNotes;
    }

    public int getPreferredMotion() {
        return preferredMotion;
    }

    public void setPreferredMotion(int preferredMotion) {
        this.preferredMotion = preferredMotion;
    }

    public int getPreferredMotionRange() {
        return preferredMotionRange;
    }

    public void setPreferredMotionRange(int preferredMotionRange) {
        this.preferredMotionRange = preferredMotionRange;
    }

    public double getPreviousVoicingMultiplier() {
        return previousVoicingMultiplier;
    }

    public void setPreviousVoicingMultiplier(double previousVoicingMultiplier) {
        this.previousVoicingMultiplier = previousVoicingMultiplier;
    }

    public double getHalfStepAwayMultiplier() {
        return halfStepAwayMultiplier;
    }

    public void setHalfStepAwayMultiplier(double halfStepAwayMultiplier) {
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
    }

    public double getFullStepAwayMultiplier() {
        return fullStepAwayMultiplier;
    }

    public void setFullStepAwayMultiplier(double fullStepAwayMultiplier) {
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
    }

    public int getLeftColorPriority() {
        return leftColorPriority;
    }

    public void setLeftColorPriority(int leftColorPriority) {
        this.leftColorPriority = leftColorPriority;
    }

    public int getRightColorPriority() {
        return rightColorPriority;
    }

    public void setRightColorPriority(int rightColorPriority) {
        this.rightColorPriority = rightColorPriority;
    }

   

    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public double getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(double priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }

    public double getRepeatMultiplier() {
        return repeatMultiplier;
    }

    public void setRepeatMultiplier(double repeatMultiplier) {
        this.repeatMultiplier = repeatMultiplier;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        LHULNote = new javax.swing.JLabel();
        LHLLNote = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        LHStretchSpinner = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        LHMinNotesSpinner = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        LHMaxNotesSpinner = new javax.swing.JSpinner();
        LHRangeButton = new javax.swing.JButton();
        jPanel30 = new javax.swing.JPanel();
        leftMinIntervalSpinner = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        RHULNote = new javax.swing.JLabel();
        RHLLNote = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        RHStretchSpinner = new javax.swing.JSpinner();
        jPanel12 = new javax.swing.JPanel();
        RHMinNotesSpinner = new javax.swing.JSpinner();
        jPanel13 = new javax.swing.JPanel();
        RHMaxNotesSpinner = new javax.swing.JSpinner();
        jPanel29 = new javax.swing.JPanel();
        rightMinIntervalSpinner = new javax.swing.JSpinner();
        RHRangeButton = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        PrevChordPrioritySlider = new javax.swing.JSlider();
        jPanel16 = new javax.swing.JPanel();
        HalfStepAwaySlider = new javax.swing.JSlider();
        jPanel17 = new javax.swing.JPanel();
        WholeStepAwaySlider = new javax.swing.JSlider();
        jPanel18 = new javax.swing.JPanel();
        PrefMotionDirSlider = new javax.swing.JSlider();
        jPanel19 = new javax.swing.JPanel();
        PrefMotionDistSlider = new javax.swing.JSlider();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        ChordToneMaxPrioritySlider = new javax.swing.JSlider();
        jPanel22 = new javax.swing.JPanel();
        ChordTonePriorityWeightSlider = new javax.swing.JSlider();
        jPanel23 = new javax.swing.JPanel();
        LHColorPriority = new javax.swing.JSlider();
        jPanel24 = new javax.swing.JPanel();
        RHColorPriority = new javax.swing.JSlider();
        jPanel25 = new javax.swing.JPanel();
        ProbSameNoteTwoOctavesSlider = new javax.swing.JSlider();
        jPanel26 = new javax.swing.JPanel();
        HalfStepRedSlider = new javax.swing.JSlider();
        jPanel27 = new javax.swing.JPanel();
        WholeStepRedSlider = new javax.swing.JSlider();
        jPanel28 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        loadDefault = new javax.swing.JButton();
        closeB = new javax.swing.JButton();
        invertBox = new java.awt.Checkbox();
        voiceAllNotes = new java.awt.Checkbox();
        rootlessBox = new java.awt.Checkbox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setBounds(new java.awt.Rectangle(0, 0, 1300, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pianist Hand Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Left Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N
        jPanel4.setName(""); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Range"));
        jPanel2.setToolTipText("Absolute Upper note limits for voicings in a hand.");
        jPanel2.setLayout(new java.awt.GridBagLayout());

        LHULNote.setText("A#1");
        LHULNote.setMinimumSize(null);
        LHULNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        jPanel2.add(LHULNote, gridBagConstraints);

        LHLLNote.setText("A#1");
        LHLLNote.setMinimumSize(null);
        LHLLNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        jPanel2.add(LHLLNote, gridBagConstraints);

        jLabel1.setText("  to  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jPanel2, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel5.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");
        jPanel5.setLayout(new java.awt.GridBagLayout());

        LHStretchSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel5.add(LHStretchSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel6.setToolTipText("Minimum number of notes in the hand's voicing.");
        jPanel6.setLayout(new java.awt.GridBagLayout());

        LHMinNotesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel6.add(LHMinNotesSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel7.setToolTipText("Maximum number of notes in the hand's voicing.");
        jPanel7.setLayout(new java.awt.GridBagLayout());

        LHMaxNotesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel7.add(LHMaxNotesSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jPanel7, gridBagConstraints);

        LHRangeButton.setText("Choose Range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(LHRangeButton, gridBagConstraints);

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Interval"));

        leftMinIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel30.add(leftMinIntervalSpinner);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jPanel30, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel4, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Right Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N
        jPanel8.setName(""); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Range"));
        jPanel9.setToolTipText("Absolute Upper note limits for voicings in a hand.");
        jPanel9.setLayout(new java.awt.GridBagLayout());

        RHULNote.setText("A#1");
        RHULNote.setMinimumSize(null);
        RHULNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        jPanel9.add(RHULNote, gridBagConstraints);

        RHLLNote.setText("A#1");
        RHLLNote.setMinimumSize(null);
        RHLLNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        jPanel9.add(RHLLNote, gridBagConstraints);

        jLabel2.setText("  to  ");
        jPanel9.add(jLabel2, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel9, gridBagConstraints);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel11.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");
        jPanel11.setLayout(new java.awt.GridBagLayout());

        RHStretchSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel11.add(RHStretchSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel11, gridBagConstraints);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel12.setToolTipText("Minimum number of notes in the hand's voicing.");
        jPanel12.setLayout(new java.awt.GridBagLayout());

        RHMinNotesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel12.add(RHMinNotesSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel12, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel13.setToolTipText("Maximum number of notes in the hand's voicing.");
        jPanel13.setLayout(new java.awt.GridBagLayout());

        RHMaxNotesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel13.add(RHMaxNotesSpinner, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel13, gridBagConstraints);

        jPanel29.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Interval"));

        rightMinIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jPanel29.add(rightMinIntervalSpinner);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel29, gridBagConstraints);

        RHRangeButton.setText("Choose Range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(RHRangeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voice Leading Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel14.setLayout(new java.awt.GridBagLayout());

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Previous Chord Priority"));
        jPanel15.setToolTipText("Rating of last chord's significance for voice leading.");
        jPanel15.setLayout(new java.awt.GridBagLayout());

        PrevChordPrioritySlider.setMajorTickSpacing(10);
        PrevChordPrioritySlider.setMaximum(50);
        PrevChordPrioritySlider.setMinorTickSpacing(1);
        PrevChordPrioritySlider.setPaintTicks(true);
        PrevChordPrioritySlider.setSnapToTicks(true);
        PrevChordPrioritySlider.setMinimumSize(null);
        PrevChordPrioritySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(PrevChordPrioritySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jPanel15, gridBagConstraints);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Away Priority"));
        jPanel16.setToolTipText("Mulitplier for any note a half-step away from previously voiced note.");
        jPanel16.setLayout(new java.awt.GridBagLayout());

        HalfStepAwaySlider.setMajorTickSpacing(10);
        HalfStepAwaySlider.setMaximum(50);
        HalfStepAwaySlider.setMinorTickSpacing(1);
        HalfStepAwaySlider.setPaintTicks(true);
        HalfStepAwaySlider.setSnapToTicks(true);
        HalfStepAwaySlider.setMinimumSize(null);
        HalfStepAwaySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel16.add(HalfStepAwaySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jPanel16, gridBagConstraints);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole Step Away Priority"));
        jPanel17.setToolTipText("Mulitplier for any note a whole step away from previously voiced note.");
        jPanel17.setLayout(new java.awt.GridBagLayout());

        WholeStepAwaySlider.setMajorTickSpacing(10);
        WholeStepAwaySlider.setMaximum(50);
        WholeStepAwaySlider.setMinorTickSpacing(1);
        WholeStepAwaySlider.setPaintTicks(true);
        WholeStepAwaySlider.setSnapToTicks(true);
        WholeStepAwaySlider.setMinimumSize(null);
        WholeStepAwaySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel17.add(WholeStepAwaySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jPanel17, gridBagConstraints);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Direction"));
        jPanel18.setToolTipText("Preferred direction of chord progression");
        jPanel18.setLayout(new java.awt.GridBagLayout());

        PrefMotionDirSlider.setMajorTickSpacing(5);
        PrefMotionDirSlider.setMaximum(5);
        PrefMotionDirSlider.setMinimum(-5);
        PrefMotionDirSlider.setMinorTickSpacing(1);
        PrefMotionDirSlider.setPaintTicks(true);
        PrefMotionDirSlider.setSnapToTicks(true);
        PrefMotionDirSlider.setMinimumSize(null);
        PrefMotionDirSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel18.add(PrefMotionDirSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jPanel18, gridBagConstraints);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Variation"));
        jPanel19.setToolTipText("Preferred amount of position variation of chord progression");
        jPanel19.setLayout(new java.awt.GridBagLayout());

        PrefMotionDistSlider.setMaximum(5);
        PrefMotionDistSlider.setMinorTickSpacing(1);
        PrefMotionDistSlider.setPaintLabels(true);
        PrefMotionDistSlider.setPaintTicks(true);
        PrefMotionDistSlider.setSnapToTicks(true);
        PrefMotionDistSlider.setMinimumSize(null);
        PrefMotionDistSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel19.add(PrefMotionDistSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel14.add(jPanel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel14, gridBagConstraints);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voicing Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel20.setLayout(new java.awt.GridBagLayout());

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Maximum Priority"));
        jPanel21.setToolTipText("Highest priority weighting for any note in the chord.");
        jPanel21.setLayout(new java.awt.GridBagLayout());

        ChordToneMaxPrioritySlider.setMaximum(10);
        ChordToneMaxPrioritySlider.setMinorTickSpacing(1);
        ChordToneMaxPrioritySlider.setPaintLabels(true);
        ChordToneMaxPrioritySlider.setPaintTicks(true);
        ChordToneMaxPrioritySlider.setSnapToTicks(true);
        ChordToneMaxPrioritySlider.setMinimumSize(null);
        ChordToneMaxPrioritySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel21.add(ChordToneMaxPrioritySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel21, gridBagConstraints);

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Priority Weighting"));
        jPanel22.setToolTipText("Desired weighting for priority notes in a chord");
        jPanel22.setLayout(new java.awt.GridBagLayout());

        ChordTonePriorityWeightSlider.setMajorTickSpacing(10);
        ChordTonePriorityWeightSlider.setMaximum(15);
        ChordTonePriorityWeightSlider.setMinorTickSpacing(1);
        ChordTonePriorityWeightSlider.setPaintTicks(true);
        ChordTonePriorityWeightSlider.setSnapToTicks(true);
        ChordTonePriorityWeightSlider.setMinimumSize(null);
        ChordTonePriorityWeightSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel22.add(ChordTonePriorityWeightSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel22, gridBagConstraints);

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Hand Color Note Priority"));
        jPanel23.setToolTipText("Desired weighting for color notes in this hand.");
        jPanel23.setLayout(new java.awt.GridBagLayout());

        LHColorPriority.setMaximum(10);
        LHColorPriority.setMinorTickSpacing(1);
        LHColorPriority.setPaintLabels(true);
        LHColorPriority.setPaintTicks(true);
        LHColorPriority.setSnapToTicks(true);
        LHColorPriority.setMinimumSize(null);
        LHColorPriority.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel23.add(LHColorPriority, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel23, gridBagConstraints);

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Hand Color Note Priority"));
        jPanel24.setToolTipText("Desired weighting for color notes in this hand.");
        jPanel24.setLayout(new java.awt.GridBagLayout());

        RHColorPriority.setMaximum(10);
        RHColorPriority.setMinorTickSpacing(1);
        RHColorPriority.setPaintTicks(true);
        RHColorPriority.setSnapToTicks(true);
        RHColorPriority.setMinimumSize(null);
        RHColorPriority.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel24.add(RHColorPriority, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel24, gridBagConstraints);

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Probability of Same Note In Two Octaves"));
        jPanel25.setToolTipText("Probability of playing the same note in two separate octaves.");
        jPanel25.setLayout(new java.awt.GridBagLayout());

        ProbSameNoteTwoOctavesSlider.setMaximum(10);
        ProbSameNoteTwoOctavesSlider.setMinorTickSpacing(1);
        ProbSameNoteTwoOctavesSlider.setPaintLabels(true);
        ProbSameNoteTwoOctavesSlider.setPaintTicks(true);
        ProbSameNoteTwoOctavesSlider.setSnapToTicks(true);
        ProbSameNoteTwoOctavesSlider.setMinimumSize(null);
        ProbSameNoteTwoOctavesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel25.add(ProbSameNoteTwoOctavesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel25, gridBagConstraints);

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Allowance"));
        jPanel26.setToolTipText("Probability that two notes a half-step apart will be played.");
        jPanel26.setLayout(new java.awt.GridBagLayout());

        HalfStepRedSlider.setMaximum(10);
        HalfStepRedSlider.setMinorTickSpacing(1);
        HalfStepRedSlider.setPaintTicks(true);
        HalfStepRedSlider.setSnapToTicks(true);
        HalfStepRedSlider.setMinimumSize(null);
        HalfStepRedSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel26.add(HalfStepRedSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel26, gridBagConstraints);

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole Step Allowance"));
        jPanel27.setToolTipText("Probability that two notes a whole-step apart will be played.");
        jPanel27.setLayout(new java.awt.GridBagLayout());

        WholeStepRedSlider.setMaximum(10);
        WholeStepRedSlider.setMinorTickSpacing(1);
        WholeStepRedSlider.setPaintTicks(true);
        WholeStepRedSlider.setSnapToTicks(true);
        WholeStepRedSlider.setMinimumSize(null);
        WholeStepRedSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel27.add(WholeStepRedSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jPanel27, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel20, gridBagConstraints);

        jPanel28.setLayout(new java.awt.GridBagLayout());

        jButton3.setText("Go To Factory Default");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(jButton3, gridBagConstraints);

        loadDefault.setText("Revert to Saved Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(loadDefault, gridBagConstraints);

        closeB.setText("Close");
        closeB.setMinimumSize(null);
        closeB.setPreferredSize(null);
        closeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(closeB, gridBagConstraints);

        invertBox.setLabel("Invert Minor 9ths");
        invertBox.setName("invertMinorNinth"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(invertBox, gridBagConstraints);

        voiceAllNotes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        voiceAllNotes.setLabel("Voice All Notes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(voiceAllNotes, gridBagConstraints);

        rootlessBox.setLabel("Rootless Voicings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel28.add(rootlessBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel28, gridBagConstraints);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Save As...");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void closeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBActionPerformed
        Advisor.readStyles();
        this.dispose();
    }//GEN-LAST:event_closeBActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       File openFile=null;
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fluid Voicings", "fv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile=chooser.getSelectedFile();
                }
                
                AVPFileCreator.fileToSettings( openFile, avs);
                setSlidersToVariables();
                setTitle(openFile.getName());
                styleEditor.setVoicingFileName(openFile.getName());
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
         saveSlidersToVariables();
                syncToSettings();
                File saveFile=new File(ImproVisor.getVoicingDirectory(),styleEditor.getVoicingFileName());
                JFileChooser chooser = new JFileChooser(ImproVisor.getVoicingDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fluid Voicings", "fv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile=chooser.getSelectedFile();
                    if(!saveFile.getName().toLowerCase().endsWith(".fv"))
                        saveFile=new File(saveFile.getAbsolutePath()+".fv");   
                    if(saveFile.getName().contains(" "))
                        saveFile=new File(saveFile.getParent(),saveFile.getName().replaceAll(" ", "-"));
                }
                //syncToSettings();
                //AVPFileCreator.fileToSettings(openFile,avs);
                AVPFileCreator.settingsToFile(avs,saveFile);
                syncFromSettings();
                styleEditor.setVoicingFileName(saveFile.getName());
                setTitle(saveFile.getName());
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        saveSlidersToVariables();
                
                File saveFile=new File(ImproVisor.getVoicingDirectory(),styleEditor.getVoicingFileName());
                
                syncToSettings();
                AVPFileCreator.settingsToFile(avs,saveFile);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlPanelFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider ChordToneMaxPrioritySlider;
    private javax.swing.JSlider ChordTonePriorityWeightSlider;
    private javax.swing.JSlider HalfStepAwaySlider;
    private javax.swing.JSlider HalfStepRedSlider;
    private javax.swing.JSlider LHColorPriority;
    private javax.swing.JLabel LHLLNote;
    private javax.swing.JSpinner LHMaxNotesSpinner;
    private javax.swing.JSpinner LHMinNotesSpinner;
    private javax.swing.JButton LHRangeButton;
    private javax.swing.JSpinner LHStretchSpinner;
    private javax.swing.JLabel LHULNote;
    private javax.swing.JSlider PrefMotionDirSlider;
    private javax.swing.JSlider PrefMotionDistSlider;
    private javax.swing.JSlider PrevChordPrioritySlider;
    private javax.swing.JSlider ProbSameNoteTwoOctavesSlider;
    private javax.swing.JSlider RHColorPriority;
    private javax.swing.JLabel RHLLNote;
    private javax.swing.JSpinner RHMaxNotesSpinner;
    private javax.swing.JSpinner RHMinNotesSpinner;
    private javax.swing.JButton RHRangeButton;
    private javax.swing.JSpinner RHStretchSpinner;
    private javax.swing.JLabel RHULNote;
    private javax.swing.JSlider WholeStepAwaySlider;
    private javax.swing.JSlider WholeStepRedSlider;
    private javax.swing.JButton closeB;
    private java.awt.Checkbox invertBox;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSpinner leftMinIntervalSpinner;
    private javax.swing.JButton loadDefault;
    private javax.swing.JSpinner rightMinIntervalSpinner;
    private java.awt.Checkbox rootlessBox;
    private java.awt.Checkbox voiceAllNotes;
    // End of variables declaration//GEN-END:variables
}
