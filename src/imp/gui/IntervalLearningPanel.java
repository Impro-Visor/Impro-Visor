/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.ChordPart;
import imp.data.IntervalLearner;
import imp.data.MelodyGenerator;
import imp.data.MelodyPart;
import imp.data.RhythmGenerator;
import imp.lickgen.Grammar;
import imp.lickgen.LickGen;
import imp.lickgen.transformations.Transform;
import imp.util.CountsFilter;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import polya.Polylist;

/**
 *
 * @author muddCS15
 */
public class IntervalLearningPanel extends javax.swing.JPanel {

    private Notate notate;
    private JLabel [][] probabilityLabels;
    private double[][] probabilities;
    private double [][][] probabilities2;
    private int [] range;
    private boolean displayProbabilities;
    private boolean displayOrder;
    private boolean addToRunningTotal;
    int [][] counts;
    int [][][] counts2;
    private JFileChooser chooser;
    private String EXTENSION = ".counts";
    private String filename;
    private int interval1;
    
    private static final boolean FIRST = true;
    private static final boolean SECOND = false;
    //private boolean unsavedChanges;
//    private JLabel [] sourceLabels;
//    private JLabel [] destLabels;
    
    /**
     * Creates new form IntervalLearningPanel
     */
    public IntervalLearningPanel(Notate notate) {
        //unsavedChanges = false;
        filename = "newFile.counts";
        this.notate = notate;
        initComponents();
        filenameLabel.setText(filename);
        counts = new int[IntervalLearner.intervals][IntervalLearner.intervals];
        for(int [] row : counts){
            for(int c = 0; c < row.length; c++){
                row[c] = 0;
            }
        }
        probabilities = IntervalLearner.probabilities(counts);
        counts2 = new int[IntervalLearner.intervals][IntervalLearner.intervals][IntervalLearner.intervals];
        for(int [][] x : counts2){
            for(int [] y : x){
                for(int z = 0; z < y.length; z++){
                    y[z] = 0;
                }
            }
        }
        probabilities2 = IntervalLearner.probabilities(counts2);
        range = new int [2];
        //default
        range[0] = Constants.G3;
        range[1] = Constants.G5;
        probabilityLabels = new JLabel[26][26];
        for(int row = 0; row<probabilityLabels.length; row++){
            for(int column = 0; column<probabilityLabels[row].length; column++){
                
                probabilityLabels[row][column] = new JLabel();
                if(row == 0 && column == 0){
                    probabilityLabels[row][column].setText("X");
                }else if(row == 0){
                    probabilityLabels[row][column].setText(Integer.toString(column-Constants.OCTAVE-1));
                }else if(column == 0){
                    probabilityLabels[row][column].setText(Integer.toString(row-Constants.OCTAVE-1));
                }else{
                    //probabilityLabels[row][column].setText("n/a"); 
                }
                
                if(row == 0 || column == 0){
                    probabilityLabels[row][column].setOpaque(true);
                    probabilityLabels[row][column].setBackground(Color.MAGENTA);
                }
                else if(row == Constants.OCTAVE+1 || column == Constants.OCTAVE+1){
                    probabilityLabels[row][column].setOpaque(true);
                    probabilityLabels[row][column].setBackground(Color.green);
                }else{
                    probabilityLabels[row][column].setOpaque(true);
                    probabilityLabels[row][column].setBackground(Color.WHITE);
                }
                probabilitiesPanel.add(probabilityLabels[row][column]);
                
                
            }
            
        }
        displayProbabilities = getDisplayFromButton();
        displayOrder = getOrderDisplayFromButton();
        interval1 = interval1Slider.getValue();
        refreshDisplay();
        addToRunningTotal = addToTotal.isSelected();
        
        //file chooser - same as in transform panel
        chooser = new JFileChooser(){
            @Override
            public void approveSelection(){
                File f = getSelectedFile();
                if(!f.getAbsolutePath().endsWith(EXTENSION))
                    f = new File(f.getAbsolutePath()+EXTENSION);
                if(f.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this,
                                                               "The file exists, overwrite?",
                                                               "Existing file",
                                                               JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }        
        };
        
        chooser.setCurrentDirectory(ImproVisor.getCountsDirectory());
        
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("Counts Files",
        //                                                             "counts");
        //chooser.setFileFilter(filter);
        
        chooser.setFileFilter(new CountsFilter());
        
    }

    private boolean getDisplayFromButton(){
        return toggleView.getText().equals("Probabilities");
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

        preAndPost = new javax.swing.ButtonGroup();
        yesNo = new javax.swing.ButtonGroup();
        rhythmGroup = new javax.swing.ButtonGroup();
        orderGroup = new javax.swing.ButtonGroup();
        probabilitiesPanel = new javax.swing.JPanel();
        sourceIntervals = new javax.swing.JLabel();
        destinationIntervals = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        learnButtonPanel = new javax.swing.JPanel();
        thisChorus = new javax.swing.JButton();
        allChoruses = new javax.swing.JButton();
        learnLabel = new javax.swing.JLabel();
        addToTotal = new javax.swing.JRadioButton();
        resetThenAdd = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        optionsLabel = new javax.swing.JLabel();
        rangeAndMerge = new javax.swing.JPanel();
        chooseRange = new javax.swing.JButton();
        mergeCheckbox = new javax.swing.JCheckBox();
        rectifyPanel = new javax.swing.JPanel();
        rectifyLabel = new javax.swing.JLabel();
        preRectifyButton = new javax.swing.JRadioButton();
        postRectifyButton = new javax.swing.JRadioButton();
        noneButton = new javax.swing.JRadioButton();
        generateButtonsPanel = new javax.swing.JPanel();
        generatePanelLabel = new javax.swing.JLabel();
        rhythmLabel = new javax.swing.JLabel();
        Eighth = new javax.swing.JRadioButton();
        Chorus1 = new javax.swing.JRadioButton();
        GrammarRhythm = new javax.swing.JRadioButton();
        generateSolo = new javax.swing.JButton();
        orderLabel = new javax.swing.JLabel();
        firstOrderButton = new javax.swing.JRadioButton();
        secondOrderButton = new javax.swing.JRadioButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        filenameLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        bottomPanel = new javax.swing.JPanel();
        fileSavePanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        loadSaveLabel = new javax.swing.JLabel();
        addFromFile = new javax.swing.JButton();
        newFileButton = new javax.swing.JButton();
        otherOptionsPanel = new javax.swing.JPanel();
        toggleLabel = new javax.swing.JLabel();
        toggleView = new javax.swing.JToggleButton();
        clearAll = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        OrderPanel = new javax.swing.JPanel();
        OrderPanelLabel = new javax.swing.JLabel();
        orderViewLabel = new javax.swing.JLabel();
        orderViewToggle = new javax.swing.JToggleButton();
        interval1Slider = new javax.swing.JSlider();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));

        setMinimumSize(new java.awt.Dimension(800, 300));
        setPreferredSize(new java.awt.Dimension(800, 300));
        setLayout(new java.awt.GridBagLayout());

        probabilitiesPanel.setLayout(new java.awt.GridLayout(26, 26, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        add(probabilitiesPanel, gridBagConstraints);

        sourceIntervals.setText("Source Intervals");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        add(sourceIntervals, gridBagConstraints);

        destinationIntervals.setText("Destination Intervals");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        add(destinationIntervals, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        learnButtonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        learnButtonPanel.setLayout(new java.awt.GridBagLayout());

        thisChorus.setText("This Chorus");
        thisChorus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thisChorusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        learnButtonPanel.add(thisChorus, gridBagConstraints);

        allChoruses.setText("All Choruses");
        allChoruses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allChorusesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        learnButtonPanel.add(allChoruses, gridBagConstraints);

        learnLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        learnLabel.setText("Learn Interval Probabilities");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        learnButtonPanel.add(learnLabel, gridBagConstraints);

        yesNo.add(addToTotal);
        addToTotal.setSelected(true);
        addToTotal.setText("Yes");
        addToTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToTotalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        learnButtonPanel.add(addToTotal, gridBagConstraints);

        yesNo.add(resetThenAdd);
        resetThenAdd.setText("No");
        resetThenAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetThenAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        learnButtonPanel.add(resetThenAdd, gridBagConstraints);

        jLabel5.setText("Add to total?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        learnButtonPanel.add(jLabel5, gridBagConstraints);

        jLabel7.setText("Learn from:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        learnButtonPanel.add(jLabel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        buttonsPanel.add(learnButtonPanel, gridBagConstraints);

        optionsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        optionsPanel.setLayout(new java.awt.GridBagLayout());

        optionsLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        optionsLabel.setText("Solo Options");
        optionsPanel.add(optionsLabel, new java.awt.GridBagConstraints());

        rangeAndMerge.setLayout(new java.awt.GridBagLayout());

        chooseRange.setText("Choose Range");
        chooseRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseRangeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rangeAndMerge.add(chooseRange, gridBagConstraints);

        mergeCheckbox.setSelected(true);
        mergeCheckbox.setText("Merge same notes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        rangeAndMerge.add(mergeCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        optionsPanel.add(rangeAndMerge, gridBagConstraints);

        rectifyPanel.setLayout(new java.awt.GridBagLayout());

        rectifyLabel.setText("Rectification:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(rectifyLabel, gridBagConstraints);

        preAndPost.add(preRectifyButton);
        preRectifyButton.setSelected(true);
        preRectifyButton.setText("Pre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(preRectifyButton, gridBagConstraints);

        preAndPost.add(postRectifyButton);
        postRectifyButton.setText("Post");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(postRectifyButton, gridBagConstraints);

        preAndPost.add(noneButton);
        noneButton.setText("None");
        noneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noneButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(noneButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        optionsPanel.add(rectifyPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        buttonsPanel.add(optionsPanel, gridBagConstraints);

        generateButtonsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        generateButtonsPanel.setLayout(new java.awt.GridBagLayout());

        generatePanelLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        generatePanelLabel.setText("Generate Solo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        generateButtonsPanel.add(generatePanelLabel, gridBagConstraints);

        rhythmLabel.setText("Rhythm:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        generateButtonsPanel.add(rhythmLabel, gridBagConstraints);

        rhythmGroup.add(Eighth);
        Eighth.setSelected(true);
        Eighth.setText("Eighths");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        generateButtonsPanel.add(Eighth, gridBagConstraints);

        rhythmGroup.add(Chorus1);
        Chorus1.setText("Chorus 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        generateButtonsPanel.add(Chorus1, gridBagConstraints);

        rhythmGroup.add(GrammarRhythm);
        GrammarRhythm.setText("Grammar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        generateButtonsPanel.add(GrammarRhythm, gridBagConstraints);

        generateSolo.setText("Generate Solo");
        generateSolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateSoloActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        generateButtonsPanel.add(generateSolo, gridBagConstraints);

        orderLabel.setText("Order:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        generateButtonsPanel.add(orderLabel, gridBagConstraints);

        orderGroup.add(firstOrderButton);
        firstOrderButton.setSelected(true);
        firstOrderButton.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        generateButtonsPanel.add(firstOrderButton, gridBagConstraints);

        orderGroup.add(secondOrderButton);
        secondOrderButton.setText("Second");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        generateButtonsPanel.add(secondOrderButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        buttonsPanel.add(generateButtonsPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        buttonsPanel.add(filler7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        buttonsPanel.add(filler9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        add(buttonsPanel, gridBagConstraints);

        filenameLabel.setText("newFile.counts");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(filenameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(filler3, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        fileSavePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fileSavePanel.setLayout(new java.awt.GridBagLayout());

        saveButton.setText("Save Probabilities");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        fileSavePanel.add(saveButton, gridBagConstraints);

        openButton.setText("Load Saved File");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        fileSavePanel.add(openButton, gridBagConstraints);

        loadSaveLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        loadSaveLabel.setText("Load or Save Probabilities");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        fileSavePanel.add(loadSaveLabel, gridBagConstraints);

        addFromFile.setText("Add from Saved File");
        addFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFromFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        fileSavePanel.add(addFromFile, gridBagConstraints);

        newFileButton.setText("New File");
        newFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        fileSavePanel.add(newFileButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomPanel.add(fileSavePanel, gridBagConstraints);

        otherOptionsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        otherOptionsPanel.setLayout(new java.awt.GridBagLayout());

        toggleLabel.setText("Toggle View:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        otherOptionsPanel.add(toggleLabel, gridBagConstraints);

        toggleView.setText("Probabilities");
        toggleView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleViewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        otherOptionsPanel.add(toggleView, gridBagConstraints);

        clearAll.setText("Clear All Probabilities");
        clearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        otherOptionsPanel.add(clearAll, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Other Options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        otherOptionsPanel.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomPanel.add(otherOptionsPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        bottomPanel.add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        bottomPanel.add(filler1, gridBagConstraints);

        OrderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        OrderPanel.setLayout(new java.awt.GridBagLayout());

        OrderPanelLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        OrderPanelLabel.setText("View Options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        OrderPanel.add(OrderPanelLabel, gridBagConstraints);

        orderViewLabel.setText("Toggle View:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        OrderPanel.add(orderViewLabel, gridBagConstraints);

        orderViewToggle.setText("First Order");
        orderViewToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderViewToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        OrderPanel.add(orderViewToggle, gridBagConstraints);

        interval1Slider.setMajorTickSpacing(3);
        interval1Slider.setMaximum(12);
        interval1Slider.setMinimum(-12);
        interval1Slider.setMinorTickSpacing(1);
        interval1Slider.setPaintLabels(true);
        interval1Slider.setPaintTicks(true);
        interval1Slider.setSnapToTicks(true);
        interval1Slider.setToolTipText("");
        interval1Slider.setValue(0);
        interval1Slider.setEnabled(false);
        interval1Slider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                interval1SliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        OrderPanel.add(interval1Slider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomPanel.add(OrderPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        add(bottomPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        add(filler6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        add(filler8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        add(filler10, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void thisChorusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thisChorusActionPerformed

//        IntervalLearner learner = new IntervalLearner(notate.getCurrentMelodyPart());
//            counts = learner.counts();
//            probabilities = IntervalLearner.probabilities(counts);
//            refreshDisplay();
//            probabilitiesPanel.repaint();
            if(!addToRunningTotal){
                clearAll();
            }
            addThisToTotal();
            //refreshDisplay();
            //probabilitiesPanel.repaint();
//        if(!addToRunningTotal){
//            IntervalLearner learner = new IntervalLearner(notate.getCurrentMelodyPart());
//            counts = learner.counts();
//            probabilities = IntervalLearner.probabilities(counts);
//            refreshDisplay();
//        }else{
//            IntervalLearner learner = new IntervalLearner(notate.getCurrentMelodyPart());
//            int [][] countsToAdd = learner.counts();
//            for(int i = 0; i < counts.length; i++){
//                for(int j = 0; j < counts.length; j++){
//                    counts[i][j] += countsToAdd[i][j];
//                }
//            }
//            probabilities = IntervalLearner.probabilities(counts);
//            refreshDisplay();
//        }
        
        
        
        //QNsoloButton.setEnabled(true);
        //rhythmSolo.setEnabled(true);
        //grammarRhythmSolo.setEnabled(true);
        //probabilitiesPanel.repaint();
    }//GEN-LAST:event_thisChorusActionPerformed

    private void allChorusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allChorusesActionPerformed
        if(!addToRunningTotal){
            clearAll();
        }
        addAllToTotal();
        //refreshDisplay();
        //probabilitiesPanel.repaint();

//        counts = new int[IntervalLearner.intervals][IntervalLearner.intervals];
//        for(int [] row : counts){
//            for(int c = 0; c < row.length; c++){
//                row[c] = 0;
//            }
//        }
//        for(int i = 0; i < notate.getScore().size(); ++i){
//            MelodyPart learnFromThis = notate.getMelodyPart(notate.getStaveAtTab(i));
//            IntervalLearner learner = new IntervalLearner(learnFromThis);
//            int [][] specificCounts = learner.counts();
//            for(int row = 0; row < counts.length; row++){
//                for(int c = 0; c < counts[row].length; c++){
//                    counts[row][c] += specificCounts[row][c];
//                }
//            }
//        }
//        probabilities = IntervalLearner.probabilities(counts);
//        refreshDisplay();
        
        //QNsoloButton.setEnabled(true);
        //rhythmSolo.setEnabled(true);
        //grammarRhythmSolo.setEnabled(true);
        //probabilitiesPanel.repaint();
    }//GEN-LAST:event_allChorusesActionPerformed

    private void generateSolo(){
        if(Chorus1.isSelected()){
            Chorus1Solo();
        }else if(Eighth.isSelected()){
            EighthNoteSolo();
        }else if(GrammarRhythm.isSelected()){
            GrammarSolo();
        }
        play();
    }
    
    private void Chorus1Solo(){
        ChordPart chords = notate.getChordProg();
        MelodyPart rhythm = notate.getScore().getPart(0);
        MelodyGenerator mgen;
        MelodyPart result = null;
        if(firstOrderButton.isSelected()){
            mgen = new MelodyGenerator(probabilities, rhythm, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody();
        }else{
            mgen = new MelodyGenerator(probabilities2, rhythm, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody2();
        }
        notate.addChorus(result);
    }
    
    private void EighthNoteSolo(){
        ChordPart chords = notate.getChordProg();
        RhythmGenerator rgen = new RhythmGenerator(notate.getScore().getLength());
        MelodyPart rhythm = rgen.rhythm(Constants.EIGHTH);
        MelodyGenerator mgen;
        MelodyPart result = null;
        if(firstOrderButton.isSelected()){
            mgen = new MelodyGenerator(probabilities, rhythm, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody();
        }else{
            mgen = new MelodyGenerator(probabilities2, rhythm, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody2();
        }
        notate.addChorus(result);
    }
    
    private void GrammarSolo(){
        ChordPart chords = notate.getChordProg();
        MelodyGenerator mgen;
        MelodyPart result = null;
        if(firstOrderButton.isSelected()){
            mgen = new MelodyGenerator(probabilities, notate, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody();
        }else{
            mgen = new MelodyGenerator(probabilities2, notate, chords, range, getRectificationFromButtons(), mergeCheckbox.isSelected());
            result = mgen.melody2();
        }
        notate.addChorus(result);
    }

    private void play(){
        
        //notate.playAscore(notate.getScore());
        notate.selectAll();
        notate.playCurrentSelection(true, 
                                        0, 
                                        PlayScoreCommand.USEDRUMS, 
                                        "interval-learning solo");
        ImproVisor.setPlayEntrySounds(true);
    }
    
    private boolean saveCounts() throws IOException{
        chooser.setSelectedFile(new File(filename));
        
        if( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            if( chooser.getSelectedFile().getName().endsWith(
                EXTENSION) )
              {
                filename = chooser.getSelectedFile().getName();
                
                saveCountsFile(chooser.getSelectedFile().getAbsolutePath());
              }
            else
              {
                filename = chooser.getSelectedFile().getName() + EXTENSION;

                saveCountsFile(chooser.getSelectedFile().getAbsolutePath() + EXTENSION);
              }
            filenameLabel.setText(filename);
            return true;
          }
        else
        {
            return false;
        }
    }
    
    private void saveCountsFile(String filePath) throws IOException{
        FileWriter writer = new FileWriter(new File(filePath));
        String s = countsToString();
        writer.write(s);
        writer.close();
    }
    
    private String countsToString(){
        return countsToString(counts);
    }
    
    private String countsToString(int [][] array){
        String result = "";
        for(int [] row : array){
            for(int count : row){
                result += count+"\t";
            }
            result += "\n";
        }
        return result;
    }
    
    private void chooseRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseRangeActionPerformed
        RangeChooser chooser = new RangeChooser(notate, range[0], range[1], Constants.OCTAVE);
        range = chooser.getRange();
    }//GEN-LAST:event_chooseRangeActionPerformed

    private String getRectificationFromButtons(){
        String selection = "";
        for(Enumeration<AbstractButton> buttons = preAndPost.getElements(); buttons.hasMoreElements();){
            AbstractButton button = buttons.nextElement();
            if(button.isSelected()){
                selection = button.getText();
            }
        }
        if(selection.equals(preRectifyButton.getText())){
            return MelodyGenerator.PRE;
        }else if(selection.equals(postRectifyButton.getText())){
            return MelodyGenerator.POST;
        }else if(selection.equals(noneButton.getText())){
            return MelodyGenerator.NONE;
        }else{
            //shouldn't happen
            return null;
        }
    }
    
    private void toggleViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleViewActionPerformed
        if(toggleView.getText().equals("Probabilities")){
            toggleView.setText("Counts");
            displayProbabilities = false;
        }else{
            toggleView.setText("Probabilities");
            displayProbabilities = true;
        }
        
        refreshDisplay();
    }//GEN-LAST:event_toggleViewActionPerformed

    private void addAllToTotal(){
        for(int i = 0; i < notate.getScore().size(); ++i){
            MelodyPart learnFromThis = notate.getMelodyPart(notate.getStaveAtTab(i));
            IntervalLearner learner = new IntervalLearner(learnFromThis);
            //1st Order Markov Chain
            int [][] countsToAdd = learner.counts();
            for(int row = 0; row < counts.length; row++){
                for(int c = 0; c < counts[row].length; c++){
                    counts[row][c] += countsToAdd[row][c];
                }
            }
            //2nd Order Markov Chain
            int [][][] counts2ToAdd = learner.counts2();
            for(int x = 0; x < counts2.length; x++){
                for(int y = 0; y < counts2[x].length; y++){
                    for(int z = 0; z < counts2[x][y].length; z++){
                        counts2[x][y][z] += counts2ToAdd[x][y][z];
                    }
                }
            }
        }
        probabilities = IntervalLearner.probabilities(counts);
        probabilities2 = IntervalLearner.probabilities(counts2);
        refreshDisplay();
    }
    
    private void addThisToTotal(){
        IntervalLearner learner = new IntervalLearner(notate.getCurrentMelodyPart());
        //1st Order Markov Chain
        int [][] countsToAdd = learner.counts();
       
        for(int i = 0; i < counts.length; i++){
            for(int j = 0; j < counts.length; j++){
                counts[i][j] += countsToAdd[i][j];
            }
        }
        //2nd Order Markov Chain
        int [][][] counts2ToAdd = learner.counts2();
        for(int x = 0; x < counts2.length; x++){
            for(int y = 0; y < counts2[x].length; y++){
                for(int z = 0; z < counts2[x][y].length; z++){
                    counts2[x][y][z] += counts2ToAdd[x][y][z];
                }
            }
        }
        probabilities = IntervalLearner.probabilities(counts);
        probabilities2 = IntervalLearner.probabilities(counts2);
        refreshDisplay();
    }
    
    private void clearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllActionPerformed
        clearAll();
    }//GEN-LAST:event_clearAllActionPerformed

    private void clearAll(){
        counts = new int[IntervalLearner.intervals][IntervalLearner.intervals];
        for(int [] row : counts){
            for(int c = 0; c < row.length; c++){
                row[c] = 0;
            }
        }
        probabilities = IntervalLearner.probabilities(counts);
        refreshDisplay();
    }
    
    private void addToTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToTotalActionPerformed
        addToRunningTotal = true;
    }//GEN-LAST:event_addToTotalActionPerformed

    private void resetThenAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetThenAddActionPerformed
        addToRunningTotal = false;
    }//GEN-LAST:event_resetThenAddActionPerformed

    private void generateSoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSoloActionPerformed
        generateSolo();
    }//GEN-LAST:event_generateSoloActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            saveCounts();
        } catch (IOException ex) {
            Logger.getLogger(IntervalLearningPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        try {
            open();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IntervalLearningPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void addFromFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFromFileActionPerformed
        try {
            addFromFile();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IntervalLearningPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addFromFileActionPerformed

    
    
    private void newFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileButtonActionPerformed
        newFile();
    }//GEN-LAST:event_newFileButtonActionPerformed

    private void noneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noneButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noneButtonActionPerformed

    private void orderViewToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderViewToggleActionPerformed
        if(orderViewToggle.getText().equals("First Order")){
            orderViewToggle.setText("Second Order");
            displayOrder = SECOND;
            interval1Slider.setEnabled(true);
        }else{
            orderViewToggle.setText("First Order");
            displayOrder = FIRST;
            interval1Slider.setEnabled(false);
        }
        
        refreshDisplay();
        
    }//GEN-LAST:event_orderViewToggleActionPerformed

    private void interval1SliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_interval1SliderMouseReleased
        interval1 = interval1Slider.getValue();
        refreshDisplay();
    }//GEN-LAST:event_interval1SliderMouseReleased

    private void newFile(){
        clearAll();
        filename = "newFile.counts";
        filenameLabel.setText(filename);
    }
    
    private void addFromFile() throws FileNotFoundException{
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            addToCountsAndUpdateProbabilities(f);
        }
    }
    
    private void addToCountsAndUpdateProbabilities(File f) throws FileNotFoundException{
        Scanner scan = new Scanner(f);
        for(int [] row : counts){
            for(int column = 0; column < row.length; column++){
                row[column] += scan.nextInt();
            }
        }
        scan.close();
        probabilities = IntervalLearner.probabilities(counts);
        refreshDisplay();
    }
    
    private void open() throws FileNotFoundException{
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            filename = f.getName();
            filenameLabel.setText(filename);
            updateCountsAndProbabilities(f);
        }
    }
    
    private void updateCountsAndProbabilities(File f) throws FileNotFoundException{
        Scanner scan = new Scanner(f);
        for(int [] row : counts){
            for(int column = 0; column < row.length; column++){
                row[column] = scan.nextInt();
            }
        }
        scan.close();
        probabilities = IntervalLearner.probabilities(counts);
        refreshDisplay();
    }
    
    private void refreshDisplay(){
        int x = IntervalLearner.intervalToIndex(interval1);
        DecimalFormat df = new DecimalFormat("#.##");
        if(displayProbabilities){
            if(displayOrder == FIRST){
                for(int row = 0; row < probabilities.length; row++){
                    for(int column = 0; column < probabilities[row].length; column++){
                        probabilityLabels[row+1][column+1].setText(df.format(probabilities[row][column]));
                    }
                }
            }else{
                for(int y = 0; y < probabilities2[x].length; y++){
                    for(int z = 0; z < probabilities2[x][y].length; z++){
                        probabilityLabels[y+1][z+1].setText(df.format(probabilities2[x][y][z]));
                    }
                }
            }
            
        }else{
            if(displayOrder == FIRST){
                for(int row = 0; row < counts.length; row++){
                    for(int column = 0; column < counts[row].length; column++){
                        probabilityLabels[row+1][column+1].setText(Integer.toString(counts[row][column]));
                    }
                }
            }else{
                for(int y = 0; y < counts2[x].length; y++){
                    for(int z = 0; z < counts2[x][y].length; z++){
                        probabilityLabels[y+1][z+1].setText(df.format(counts2[x][y][z]));
                    }
                }
            }
            
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton Chorus1;
    private javax.swing.JRadioButton Eighth;
    private javax.swing.JRadioButton GrammarRhythm;
    private javax.swing.JPanel OrderPanel;
    private javax.swing.JLabel OrderPanelLabel;
    private javax.swing.JButton addFromFile;
    private javax.swing.JRadioButton addToTotal;
    private javax.swing.JButton allChoruses;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton chooseRange;
    private javax.swing.JButton clearAll;
    private javax.swing.JLabel destinationIntervals;
    private javax.swing.JPanel fileSavePanel;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JRadioButton firstOrderButton;
    private javax.swing.JPanel generateButtonsPanel;
    private javax.swing.JLabel generatePanelLabel;
    private javax.swing.JButton generateSolo;
    private javax.swing.JSlider interval1Slider;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel learnButtonPanel;
    private javax.swing.JLabel learnLabel;
    private javax.swing.JLabel loadSaveLabel;
    private javax.swing.JCheckBox mergeCheckbox;
    private javax.swing.JButton newFileButton;
    private javax.swing.JRadioButton noneButton;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.ButtonGroup orderGroup;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JLabel orderViewLabel;
    private javax.swing.JToggleButton orderViewToggle;
    private javax.swing.JPanel otherOptionsPanel;
    private javax.swing.JRadioButton postRectifyButton;
    private javax.swing.ButtonGroup preAndPost;
    private javax.swing.JRadioButton preRectifyButton;
    private javax.swing.JPanel probabilitiesPanel;
    private javax.swing.JPanel rangeAndMerge;
    private javax.swing.JLabel rectifyLabel;
    private javax.swing.JPanel rectifyPanel;
    private javax.swing.JRadioButton resetThenAdd;
    private javax.swing.ButtonGroup rhythmGroup;
    private javax.swing.JLabel rhythmLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JRadioButton secondOrderButton;
    private javax.swing.JLabel sourceIntervals;
    private javax.swing.JButton thisChorus;
    private javax.swing.JLabel toggleLabel;
    private javax.swing.JToggleButton toggleView;
    private javax.swing.ButtonGroup yesNo;
    // End of variables declaration//GEN-END:variables

    private boolean getOrderDisplayFromButton() {
        return orderViewToggle.getText().equals("First Order");
    }
}
