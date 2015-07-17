/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp;

import imp.data.Note;
import imp.data.NoteSymbol;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author muddCS15
 */
public class ControlPanelFrame extends javax.swing.JFrame implements Serializable{

    /**
     * Creates new form ControlPanelFrame
     */
    public ControlPanelFrame(AutomaticVoicingSettings avs1) {
        this.avs=avs1;
        initComponents();
        associateSliders();
        syncFromSettings();
        setSlidersToVariables();
        setLabels();
        System.out.println("Lower Left hand: "+avs.getLeftHandLowerLimit());
        this.setTitle("Automated Voicing Generator Settings Editor");
        jButton1.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               saveSlidersToVariables();
           }
       });  
         jButton2.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               setSlidersToVariables();
           }
       });
        jButton3.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              resetValues();
           }
       }); 
       jButton4.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               File openFile=null;
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile=chooser.getSelectedFile();
                }
                
                AVPFileCreator.fileToSettings( openFile, avs);
                setSlidersToVariables();
                
       
                 
            
            //setSlidersToVariables();
            
           }
       });
        jButton5.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
                File saveFile=null;
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile=chooser.getSelectedFile();
                    if(!saveFile.getName().toLowerCase().endsWith(".avp"))
                        saveFile=new File(saveFile.getAbsolutePath()+".avp");
                }
                syncToSettings();
                AVPFileCreator.settingsToFile(avs,saveFile);
                
            }
       });
       for(JSlider slider:handLimits)
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
    private void setVoicingFile(File file)
    { AVPFileCreator.settingsToFile(avs, file);
                setSlidersToVariables();
    }
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
        leftColorPriority=1;//priority of any color note
        rightColorPriority=3;
        maxPriority=6;//max priority a note in the priority array can have
        priorityMultiplier=.667;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=.3;
        halfStepReducer=0;
        fullStepReducer=.7;
        syncToSettings();
    }
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
    }
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
        setDefaults();
        setSlidersToVariables();
    }
    public void setLabels()
    {
        for(int i=0; i<4; i++)
        {
            limitLabels[i].setText((NoteSymbol.makeNoteSymbol(new Note(handLimits[i].getValue())).toString()));
        }
    }
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
    }
     public void associateSliders()
    {
        handLimits=new JSlider[4];
        handSpreads=new JSlider[2];
        handNotes=new JSlider[4];
        voiceLeadingWeights=new JSlider[3];
        handLimits[0]=LHUL;
        handLimits[1]=jSlider2;
        handLimits[2]=jSlider3;
        handLimits[3]=jSlider4;
        handSpreads[0]=jSlider5;
        handSpreads[1]=jSlider6;
        handNotes[0]=jSlider7;
        handNotes[1]=jSlider8;
        handNotes[2]=jSlider9;
        handNotes[3]=jSlider10;
        
        //voice leading controls
        preferredMotionSlider=jSlider14;
        motionRange=jSlider15;
        voiceLeadingWeights[0]=jSlider11;// multiplier for notes used in previous voicing
        voiceLeadingWeights[1]=jSlider12;
        voiceLeadingWeights[2]=jSlider13;
        //voicing control
        leftColorPrioritySlider=jSlider18;//priority of any color note
        rightColorPrioritySlider=jSlider20;
        maxPrioritySlider=jSlider16;
        priorityWeighting=jSlider17;
        repeatedNoteProbability=jSlider19;
        limitLabels=new JLabel[4];
        limitLabels[0]=LHULNote;
        limitLabels[1]=LHLLNote;
        limitLabels[2]=jLabel26;
        limitLabels[3]=jLabel27;
        closeNoteReducer=new JSlider[2];
        closeNoteReducer[0]=jSlider21;
        closeNoteReducer[1]=jSlider22;
        
        
    }
    public void checkSliders()
    {
       if(handLimits[1].getValue()<handLimits[0].getValue()+handSpreads[0].getValue())
           handLimits[1].setValue(handLimits[0].getValue()+handSpreads[0].getValue());
       if(handLimits[3].getValue()<handLimits[2].getValue()+handSpreads[1].getValue())
           handLimits[3].setValue(handLimits[2].getValue()+handSpreads[1].getValue());
       
    }
    public void saveSlidersToVariables()
    {
        checkSliders();
        leftHandLowerLimit=handLimits[0].getValue();
        rightHandLowerLimit=handLimits[2].getValue();
        leftHandUpperLimit=handLimits[1].getValue();
        rightHandUpperLimit=handLimits[3].getValue();
        leftHandSpread=handSpreads[0].getValue();
        rightHandSpread=handSpreads[1].getValue();
        leftHandMinNotes=handNotes[0].getValue();
        leftHandMaxNotes=handNotes[1].getValue();
        rightHandMinNotes=handNotes[2].getValue();
        rightHandMaxNotes=handNotes[3].getValue();
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
        syncToSettings();
    }
    private JSlider handLimits[];
    private JSlider handSpreads[];
    private JSlider handNotes[];
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

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        LHUL = new javax.swing.JSlider();
        LHULNote = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jSlider2 = new javax.swing.JSlider();
        LHLLNote = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jSlider5 = new javax.swing.JSlider();
        jPanel6 = new javax.swing.JPanel();
        jSlider7 = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        jSlider8 = new javax.swing.JSlider();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jSlider4 = new javax.swing.JSlider();
        jLabel27 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jSlider3 = new javax.swing.JSlider();
        jLabel26 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jSlider6 = new javax.swing.JSlider();
        jPanel12 = new javax.swing.JPanel();
        jSlider9 = new javax.swing.JSlider();
        jPanel13 = new javax.swing.JPanel();
        jSlider10 = new javax.swing.JSlider();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jSlider11 = new javax.swing.JSlider();
        jPanel16 = new javax.swing.JPanel();
        jSlider12 = new javax.swing.JSlider();
        jPanel17 = new javax.swing.JPanel();
        jSlider13 = new javax.swing.JSlider();
        jPanel18 = new javax.swing.JPanel();
        jSlider14 = new javax.swing.JSlider();
        jPanel19 = new javax.swing.JPanel();
        jSlider15 = new javax.swing.JSlider();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jSlider16 = new javax.swing.JSlider();
        jPanel22 = new javax.swing.JPanel();
        jSlider17 = new javax.swing.JSlider();
        jPanel23 = new javax.swing.JPanel();
        jSlider18 = new javax.swing.JSlider();
        jPanel24 = new javax.swing.JPanel();
        jSlider20 = new javax.swing.JSlider();
        jPanel25 = new javax.swing.JPanel();
        jSlider19 = new javax.swing.JSlider();
        jPanel26 = new javax.swing.JPanel();
        jSlider21 = new javax.swing.JSlider();
        jPanel27 = new javax.swing.JPanel();
        jSlider22 = new javax.swing.JSlider();
        closeB = new javax.swing.JButton();

        jButton1.setText("Apply");

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Reset Defaults");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Load Preset");

        jButton5.setText("Save Settings");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pianist Hand Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Left Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Upper Limit"));
        jPanel2.setToolTipText("Absolute Upper note limits for voicings in a hand.");

        LHUL.setMajorTickSpacing(12);
        LHUL.setMaximum(108);
        LHUL.setMinimum(21);
        LHUL.setMinorTickSpacing(1);
        LHUL.setPaintLabels(true);
        LHUL.setPaintTicks(true);
        LHUL.setSnapToTicks(true);

        LHULNote.setText("A#1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(LHUL, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LHULNote)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(LHUL, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LHULNote)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Lower Limit"));
        jPanel3.setToolTipText("Absolute Lower note limits for voicings in a hand.");

        jSlider2.setMajorTickSpacing(12);
        jSlider2.setMaximum(108);
        jSlider2.setMinimum(21);
        jSlider2.setMinorTickSpacing(1);
        jSlider2.setPaintLabels(true);
        jSlider2.setPaintTicks(true);
        jSlider2.setSnapToTicks(true);

        LHLLNote.setText("A#1");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LHLLNote)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LHLLNote)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel5.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");

        jSlider5.setMajorTickSpacing(7);
        jSlider5.setMaximum(19);
        jSlider5.setMinimum(7);
        jSlider5.setMinorTickSpacing(1);
        jSlider5.setPaintLabels(true);
        jSlider5.setPaintTicks(true);
        jSlider5.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel6.setToolTipText("Minimum number of notes in the hand's voicing.");

        jSlider7.setMajorTickSpacing(5);
        jSlider7.setMaximum(5);
        jSlider7.setMinorTickSpacing(1);
        jSlider7.setPaintLabels(true);
        jSlider7.setPaintTicks(true);
        jSlider7.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel7.setToolTipText("Maximum number of notes in the hand's voicing.");

        jSlider8.setMajorTickSpacing(5);
        jSlider8.setMaximum(5);
        jSlider8.setMinorTickSpacing(1);
        jSlider8.setPaintLabels(true);
        jSlider8.setPaintTicks(true);
        jSlider8.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Right Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Upper Limit"));
        jPanel9.setToolTipText("Absolute Upper note limits for voicings in a hand.");

        jSlider4.setMajorTickSpacing(12);
        jSlider4.setMaximum(108);
        jSlider4.setMinimum(21);
        jSlider4.setMinorTickSpacing(1);
        jSlider4.setPaintLabels(true);
        jSlider4.setPaintTicks(true);
        jSlider4.setSnapToTicks(true);

        jLabel27.setText("A#1");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Lower Limit"));
        jPanel10.setToolTipText("Absolute Lower note limits for voicings in a hand.");

        jSlider3.setMajorTickSpacing(12);
        jSlider3.setMaximum(108);
        jSlider3.setMinimum(21);
        jSlider3.setMinorTickSpacing(1);
        jSlider3.setPaintLabels(true);
        jSlider3.setPaintTicks(true);
        jSlider3.setSnapToTicks(true);

        jLabel26.setText("A#1");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel11.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");

        jSlider6.setMajorTickSpacing(5);
        jSlider6.setMaximum(19);
        jSlider6.setMinimum(7);
        jSlider6.setMinorTickSpacing(1);
        jSlider6.setPaintLabels(true);
        jSlider6.setPaintTicks(true);
        jSlider6.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel12.setToolTipText("Minimum number of notes in the hand's voicing.");

        jSlider9.setMajorTickSpacing(5);
        jSlider9.setMaximum(5);
        jSlider9.setMinorTickSpacing(1);
        jSlider9.setPaintLabels(true);
        jSlider9.setPaintTicks(true);
        jSlider9.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider9, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel13.setToolTipText("Maximum number of notes in the hand's voicing.");

        jSlider10.setMajorTickSpacing(5);
        jSlider10.setMaximum(5);
        jSlider10.setMinorTickSpacing(1);
        jSlider10.setPaintLabels(true);
        jSlider10.setPaintTicks(true);
        jSlider10.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider10, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voice Leading Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Previous Chord Priority"));
        jPanel15.setToolTipText("Rating of last chord's significance for voice leading.");

        jSlider11.setMajorTickSpacing(10);
        jSlider11.setMaximum(50);
        jSlider11.setMinorTickSpacing(1);
        jSlider11.setPaintTicks(true);
        jSlider11.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jSlider11, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Away Priority"));
        jPanel16.setToolTipText("Mulitplier for any note a half-step away from previously voiced note.");

        jSlider12.setMajorTickSpacing(10);
        jSlider12.setMaximum(50);
        jSlider12.setMinorTickSpacing(1);
        jSlider12.setPaintTicks(true);
        jSlider12.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jSlider12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Full Step Away Priority"));
        jPanel17.setToolTipText("Mulitplier for any note a whole step away from previously voiced note.");

        jSlider13.setMajorTickSpacing(10);
        jSlider13.setMaximum(50);
        jSlider13.setMinorTickSpacing(1);
        jSlider13.setPaintTicks(true);
        jSlider13.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jSlider13, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Direction"));
        jPanel18.setToolTipText("Preferred direction of chord progression");

        jSlider14.setMaximum(5);
        jSlider14.setMinimum(-5);
        jSlider14.setMinorTickSpacing(1);
        jSlider14.setPaintLabels(true);
        jSlider14.setPaintTicks(true);
        jSlider14.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jSlider14, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Distance"));
        jPanel19.setToolTipText("Preferred distance of chord progression");

        jSlider15.setMaximum(5);
        jSlider15.setMinorTickSpacing(1);
        jSlider15.setPaintLabels(true);
        jSlider15.setPaintTicks(true);
        jSlider15.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider15, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voicing Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Maximum Priority"));
        jPanel21.setToolTipText("Highest priority weighting for any note in the chord.");

        jSlider16.setMaximum(10);
        jSlider16.setMinorTickSpacing(1);
        jSlider16.setPaintLabels(true);
        jSlider16.setPaintTicks(true);
        jSlider16.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider16, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Priority Weighting"));
        jPanel22.setToolTipText("Desired weighting for priority notes in a chord");

        jSlider17.setMajorTickSpacing(10);
        jSlider17.setMaximum(15);
        jSlider17.setMinorTickSpacing(1);
        jSlider17.setPaintTicks(true);
        jSlider17.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider17, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Hand Color Note Priority"));
        jPanel23.setToolTipText("Desired weighting for color notes in this hand.");

        jSlider18.setMaximum(10);
        jSlider18.setMinorTickSpacing(1);
        jSlider18.setPaintLabels(true);
        jSlider18.setPaintTicks(true);
        jSlider18.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider18, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Hand Color Note Priority"));
        jPanel24.setToolTipText("Desired weighting for color notes in this hand.");

        jSlider20.setMaximum(10);
        jSlider20.setMinorTickSpacing(1);
        jSlider20.setPaintTicks(true);
        jSlider20.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider20, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(146, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Probability of Same Note In Two Octaves"));
        jPanel25.setToolTipText("Probability of playing the same note in two separate octaves.");

        jSlider19.setMaximum(10);
        jSlider19.setMinorTickSpacing(1);
        jSlider19.setPaintLabels(true);
        jSlider19.setPaintTicks(true);
        jSlider19.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider19, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Reduction"));
        jPanel26.setToolTipText("Probability that two notes a half-step apart will be played.");

        jSlider21.setMaximum(10);
        jSlider21.setMinorTickSpacing(1);
        jSlider21.setPaintTicks(true);
        jSlider21.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider21, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole Step Reduction"));
        jPanel27.setToolTipText("Probability that two notes a whole-step apart will be played.");

        jSlider22.setMaximum(10);
        jSlider22.setMinorTickSpacing(1);
        jSlider22.setPaintTicks(true);
        jSlider22.setSnapToTicks(true);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider22, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closeB.setText("Close");
        closeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeB, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(2, 2, 2)
                        .addComponent(jButton2)
                        .addGap(6, 6, 6)
                        .addComponent(jButton3)
                        .addGap(6, 6, 6)
                        .addComponent(jButton4)
                        .addGap(6, 6, 6)
                        .addComponent(jButton5)
                        .addGap(6, 6, 6)
                        .addComponent(closeB))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void closeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeBActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlPanelFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LHLLNote;
    private javax.swing.JSlider LHUL;
    private javax.swing.JLabel LHULNote;
    private javax.swing.JButton closeB;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSlider jSlider10;
    private javax.swing.JSlider jSlider11;
    private javax.swing.JSlider jSlider12;
    private javax.swing.JSlider jSlider13;
    private javax.swing.JSlider jSlider14;
    private javax.swing.JSlider jSlider15;
    private javax.swing.JSlider jSlider16;
    private javax.swing.JSlider jSlider17;
    private javax.swing.JSlider jSlider18;
    private javax.swing.JSlider jSlider19;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider20;
    private javax.swing.JSlider jSlider21;
    private javax.swing.JSlider jSlider22;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JSlider jSlider5;
    private javax.swing.JSlider jSlider6;
    private javax.swing.JSlider jSlider7;
    private javax.swing.JSlider jSlider8;
    private javax.swing.JSlider jSlider9;
    // End of variables declaration//GEN-END:variables
}
