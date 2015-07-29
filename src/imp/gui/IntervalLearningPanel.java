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
import imp.util.CountsFilter;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author muddCS15
 */
public class IntervalLearningPanel extends javax.swing.JPanel {

    private Notate notate;
    private JLabel [][] probabilityLabels;
    private int [] range;
    private boolean displayProbabilities;
    private boolean displayOrder;
    private boolean addToRunningTotal;
    private final JFileChooser chooser;
    private String EXTENSION = ".counts";
    private String filename;
    private int interval1;
    private IntervalLearner learner;
    
    private static final boolean FIRST = true;
    private static final boolean SECOND = false;
    
    private static final Color cold = Color.white;
    private static final Color hot = Color.red;
    
    /**
     * IntervalLearningPanel constructor
     * Creates new form IntervalLearningPanel
     * @param notate - notate that this interval learning panel was spawned from
     */
    public IntervalLearningPanel(Notate notate) {
        learner = new IntervalLearner();
        filename = "newFile.counts";
        this.notate = notate;
        initComponents();
        filenameLabel.setText(filename);
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
                    probabilityLabels[row][column].setBackground(Color.GREEN);
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
        chooser.setFileFilter(new CountsFilter());
        
    }

    /**
     * color
     * Returns a color based on a probability
     * Used to create a heat map
     * @param probability - probability to find a color for
     * @return a color corresponding to the probability - more red for higher, more blue for lower
     */
    private static Color color(double probability){
        int r = cold.getRed() + (int)(probability*(hot.getRed()-cold.getRed()));
        int g = cold.getGreen() + (int)(probability*(hot.getGreen()-cold.getGreen()));
        int b = cold.getBlue() + (int)(probability*(hot.getBlue()-cold.getBlue()));
        return new Color(r, g, b);
    }
    
    /**
     * getDisplayFromButton
     * @return - true for probabilities, false for counts
     */
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
        learnFromGroup = new javax.swing.ButtonGroup();
        probabilitiesPanel = new javax.swing.JPanel();
        sourceIntervals = new javax.swing.JLabel();
        destinationIntervals = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        learnIntervalProbabilities = new javax.swing.JPanel();
        learnLabel = new javax.swing.JLabel();
        addToTotalPanel = new javax.swing.JPanel();
        addToTotalLabel = new javax.swing.JLabel();
        addToTotal = new javax.swing.JRadioButton();
        resetThenAdd = new javax.swing.JRadioButton();
        learnButtonsPanel = new javax.swing.JPanel();
        learnButton = new javax.swing.JButton();
        clearAll = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        learnFromPanel = new javax.swing.JPanel();
        learnFromLabel = new javax.swing.JLabel();
        thisChorusOption = new javax.swing.JRadioButton();
        allChorusesOption = new javax.swing.JRadioButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        viewOptions = new javax.swing.JPanel();
        OrderPanelLabel = new javax.swing.JLabel();
        togglePanel = new javax.swing.JPanel();
        orderViewLabel = new javax.swing.JLabel();
        toggleView = new javax.swing.JToggleButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        orderViewToggle = new javax.swing.JToggleButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        firstIntervalPanel = new javax.swing.JPanel();
        interval1Label = new javax.swing.JLabel();
        interval1Slider = new javax.swing.JSlider();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        filenameLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        bottomPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        soloOptions = new javax.swing.JPanel();
        optionsLabel = new javax.swing.JLabel();
        rangeAndMerge = new javax.swing.JPanel();
        chooseRange = new javax.swing.JButton();
        mergeCheckbox = new javax.swing.JCheckBox();
        rectifyPanel = new javax.swing.JPanel();
        rectifyCheckbox = new javax.swing.JCheckBox();
        chordBox = new javax.swing.JCheckBox();
        colorBox = new javax.swing.JCheckBox();
        approachBox = new javax.swing.JCheckBox();
        generateSoloPanel = new javax.swing.JPanel();
        generatePanelLabel = new javax.swing.JLabel();
        rhythmLabel = new javax.swing.JLabel();
        Eighth = new javax.swing.JRadioButton();
        Chorus1 = new javax.swing.JRadioButton();
        GrammarRhythm = new javax.swing.JRadioButton();
        generateSolo = new javax.swing.JButton();
        orderLabel = new javax.swing.JLabel();
        firstOrderButton = new javax.swing.JRadioButton();
        secondOrderButton = new javax.swing.JRadioButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));

        setMinimumSize(new java.awt.Dimension(800, 300));
        setPreferredSize(new java.awt.Dimension(800, 300));
        setLayout(new java.awt.GridBagLayout());

        probabilitiesPanel.setLayout(new java.awt.GridLayout(26, 26, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        add(probabilitiesPanel, gridBagConstraints);

        sourceIntervals.setText("Source Intervals");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(sourceIntervals, gridBagConstraints);

        destinationIntervals.setText("Destination Intervals");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        add(destinationIntervals, gridBagConstraints);

        topPanel.setLayout(new java.awt.GridBagLayout());

        learnIntervalProbabilities.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        learnIntervalProbabilities.setLayout(new java.awt.GridBagLayout());

        learnLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        learnLabel.setText("Learn Interval Probabilities");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        learnIntervalProbabilities.add(learnLabel, gridBagConstraints);

        addToTotalPanel.setLayout(new java.awt.GridBagLayout());

        addToTotalLabel.setText("Add to total?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        addToTotalPanel.add(addToTotalLabel, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        addToTotalPanel.add(addToTotal, gridBagConstraints);

        yesNo.add(resetThenAdd);
        resetThenAdd.setText("No");
        resetThenAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetThenAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        addToTotalPanel.add(resetThenAdd, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        learnIntervalProbabilities.add(addToTotalPanel, gridBagConstraints);

        learnButtonsPanel.setLayout(new java.awt.GridBagLayout());

        learnButton.setText("Learn!");
        learnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        learnButtonsPanel.add(learnButton, gridBagConstraints);

        clearAll.setText("Start Over");
        clearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        learnButtonsPanel.add(clearAll, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        learnButtonsPanel.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        learnIntervalProbabilities.add(learnButtonsPanel, gridBagConstraints);

        learnFromPanel.setLayout(new java.awt.GridBagLayout());

        learnFromLabel.setText("Learn from:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        learnFromPanel.add(learnFromLabel, gridBagConstraints);

        learnFromGroup.add(thisChorusOption);
        thisChorusOption.setSelected(true);
        thisChorusOption.setText("This Chorus");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        learnFromPanel.add(thisChorusOption, gridBagConstraints);

        learnFromGroup.add(allChorusesOption);
        allChorusesOption.setText("All Choruses");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        learnFromPanel.add(allChorusesOption, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        learnIntervalProbabilities.add(learnFromPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        topPanel.add(learnIntervalProbabilities, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        topPanel.add(filler7, gridBagConstraints);

        viewOptions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        viewOptions.setLayout(new java.awt.GridBagLayout());

        OrderPanelLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        OrderPanelLabel.setText("View Options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        viewOptions.add(OrderPanelLabel, gridBagConstraints);

        togglePanel.setLayout(new java.awt.GridBagLayout());

        orderViewLabel.setText("Toggle View:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        togglePanel.add(orderViewLabel, gridBagConstraints);

        toggleView.setText("Probabilities");
        toggleView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleViewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        togglePanel.add(toggleView, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        togglePanel.add(filler4, gridBagConstraints);

        orderViewToggle.setText("First Order");
        orderViewToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderViewToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        togglePanel.add(orderViewToggle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        togglePanel.add(filler11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        viewOptions.add(togglePanel, gridBagConstraints);

        firstIntervalPanel.setLayout(new java.awt.GridBagLayout());

        interval1Label.setText("1st Source Interval:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        firstIntervalPanel.add(interval1Label, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        firstIntervalPanel.add(interval1Slider, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        firstIntervalPanel.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        viewOptions.add(firstIntervalPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        topPanel.add(viewOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        add(topPanel, gridBagConstraints);

        filenameLabel.setText("newFile.counts");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(filenameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(filler3, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        bottomPanel.add(filler1, gridBagConstraints);

        soloOptions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        soloOptions.setLayout(new java.awt.GridBagLayout());

        optionsLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        optionsLabel.setText("Solo Options");
        soloOptions.add(optionsLabel, new java.awt.GridBagConstraints());

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
        soloOptions.add(rangeAndMerge, gridBagConstraints);

        rectifyPanel.setLayout(new java.awt.GridBagLayout());

        rectifyCheckbox.setSelected(true);
        rectifyCheckbox.setText("Rectify");
        rectifyCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectifyCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(rectifyCheckbox, gridBagConstraints);

        chordBox.setSelected(true);
        chordBox.setText("Chord");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(chordBox, gridBagConstraints);

        colorBox.setSelected(true);
        colorBox.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(colorBox, gridBagConstraints);

        approachBox.setSelected(true);
        approachBox.setText("Approach");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(approachBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        soloOptions.add(rectifyPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        bottomPanel.add(soloOptions, gridBagConstraints);

        generateSoloPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        generateSoloPanel.setLayout(new java.awt.GridBagLayout());

        generatePanelLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        generatePanelLabel.setText("Generate Solo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        generateSoloPanel.add(generatePanelLabel, gridBagConstraints);

        rhythmLabel.setText("Rhythm:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        generateSoloPanel.add(rhythmLabel, gridBagConstraints);

        rhythmGroup.add(Eighth);
        Eighth.setSelected(true);
        Eighth.setText("Eighths");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        generateSoloPanel.add(Eighth, gridBagConstraints);

        rhythmGroup.add(Chorus1);
        Chorus1.setText("Chorus 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        generateSoloPanel.add(Chorus1, gridBagConstraints);

        rhythmGroup.add(GrammarRhythm);
        GrammarRhythm.setText("Grammar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        generateSoloPanel.add(GrammarRhythm, gridBagConstraints);

        generateSolo.setText("Generate Solo");
        generateSolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateSoloActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        generateSoloPanel.add(generateSolo, gridBagConstraints);

        orderLabel.setText("Order:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        generateSoloPanel.add(orderLabel, gridBagConstraints);

        orderGroup.add(firstOrderButton);
        firstOrderButton.setSelected(true);
        firstOrderButton.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        generateSoloPanel.add(firstOrderButton, gridBagConstraints);

        orderGroup.add(secondOrderButton);
        secondOrderButton.setText("Second");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        generateSoloPanel.add(secondOrderButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        bottomPanel.add(generateSoloPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        add(bottomPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        add(filler10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler9, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * generateSolo
     * Generate a solo in a new chorus based on transition probabilities
     */
    public void generateSolo(){
        if(Chorus1.isSelected()){
            Chorus1Solo();
        }else if(Eighth.isSelected()){
            EighthNoteSolo();
        }else if(GrammarRhythm.isSelected()){
            GrammarSolo();
        }
        play();
    }
    
    /**
     * Chorus1Solo
     * Makes a solo with the same rhythm as chorus 1
     */
    private void Chorus1Solo(){
        ChordPart chords = notate.getChordProg();
        MelodyPart rhythm = notate.getScore().getPart(0);
        MelodyGenerator mgen;
        MelodyPart result;
        boolean rectify = rectifyCheckbox.isSelected();
        boolean [] include = {chordBox.isSelected(), colorBox.isSelected(), approachBox.isSelected()};
        mgen = new MelodyGenerator(learner, rhythm, chords, range, mergeCheckbox.isSelected(), rectify, include);
        result = firstOrderButton.isSelected() ? mgen.melody() : mgen.melody2();
        notate.getCurrentMelodyPart().newPasteOver(result, 0);
        //notate.addChorus(result);
    }
    
    /**
     * EighthNoteSolo
     * Makes a solo whose rhythm is just eighth notes
     */
    private void EighthNoteSolo(){
        ChordPart chords = notate.getChordProg();
        RhythmGenerator rgen = new RhythmGenerator(notate.getScore().getLength());
        MelodyPart rhythm = rgen.rhythm(Constants.EIGHTH);
        MelodyGenerator mgen;
        MelodyPart result;
        boolean rectify = rectifyCheckbox.isSelected();
        boolean [] include = {chordBox.isSelected(), colorBox.isSelected(), approachBox.isSelected()};
        mgen = new MelodyGenerator(learner, rhythm, chords, range, mergeCheckbox.isSelected(), rectify, include);
        result = firstOrderButton.isSelected() ? mgen.melody() : mgen.melody2();
        notate.getCurrentMelodyPart().newPasteOver(result, 0);
        //notate.addChorus(result);
    }
    
    /**
     * GrammarSolo
     * Makes a solo based on the currently selected grammar
     * NOTE: Currently, this works by reducing an abstract melody to just a rhythm
     * Ideally, just rhythmic information would be learned from the solos
     */
    private void GrammarSolo(){
        ChordPart chords = notate.getChordProg();
        MelodyGenerator mgen;
        MelodyPart result;
        boolean rectify = rectifyCheckbox.isSelected();
        boolean [] include = {chordBox.isSelected(), colorBox.isSelected(), approachBox.isSelected()};
        mgen = new MelodyGenerator(learner, notate, chords, range, mergeCheckbox.isSelected(), rectify, include);
        result = firstOrderButton.isSelected() ? mgen.melody() : mgen.melody2();
        notate.getCurrentMelodyPart().newPasteOver(result, 0);
        //notate.addChorus(result);
    }

    /**
     * play
     * play the melody in the current chorus
     */
    private void play(){
        notate.selectAll();
        notate.playCurrentSelection(true, 
                                        0, 
                                        PlayScoreCommand.USEDRUMS, 
                                        "interval-learning solo");
        ImproVisor.setPlayEntrySounds(true);
    }
    
    /**
     * saveCounts
     * Saves counts as .counts file
     * @return true if successful
     * @throws IOException 
     */
    public boolean saveCounts() throws IOException{
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
    
    /**
     * saveCountsFile
     * writes counts to file
     * @param filePath - absolute path of file to be written to
     * @throws IOException 
     */
    private void saveCountsFile(String filePath) throws IOException{
        FileWriter writer = new FileWriter(new File(filePath));
        String s = countsToString();
        writer.write(s);
        writer.close();
    }
    
    /**
     * countsToString
     * Returns String representation of 1st order and 2nd order counts data
     * 1st 25X25 array contains the 1st order counts
     * Next 25 25X25 arrays contain the 2nd order counts
     * @return String representation of counts data
     */
    private String countsToString(){
        return countsToString(learner.getDeg1Counts())+"\n"+countsToString(learner.getDeg2Counts());
    }
    
    /**
     * Converts an array to a String
     * @param array - array of counts
     * @return String representation of array
     */
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
    
    /**
     * Converts a 3D array to a String
     * (list of 2D arrays)
     * @param threeD - 3D counts array
     * @return String representation of array
     */
    private String countsToString(int [][][] threeD){
        String result = "";
        for(int [][] twoD : threeD){
            result += countsToString(twoD);
            result += "\n";
        }
        return result;
    }
    
    private void chooseRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseRangeActionPerformed
        RangeChooser rangeChooser = new RangeChooser(notate, range[0], range[1], Constants.OCTAVE);
        range = rangeChooser.getRange();
    }//GEN-LAST:event_chooseRangeActionPerformed

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

    /**
     * addAllToTotal
     * Learn from all choruses, adding to running total
     */
    private void addAllToTotal(){
        for(int i = 0; i < notate.getScore().size(); ++i){
            MelodyPart learnFromThis = notate.getMelodyPart(notate.getStaveAtTab(i));
            learner.learnFrom(learnFromThis);
        }
        refreshDisplay();
    }
    
    /**
     * addThisToTotal
     * add current chorus to total
     */
    private void addThisToTotal(){
        learner.learnFrom(notate.getCurrentMelodyPart());
        refreshDisplay();
    }
    
    private void clearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllActionPerformed
        clearAll();
    }//GEN-LAST:event_clearAllActionPerformed

    /**
     * clearAll
     * clear all data, refresh display
     */
    private void clearAll(){
        learner.clearAll();
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

    private void rectifyCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectifyCheckboxActionPerformed
        if(rectifyCheckbox.isSelected()){
            enableOptions(true);
        }else{
            enableOptions(false);
        }
    }//GEN-LAST:event_rectifyCheckboxActionPerformed

    private void learnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_learnButtonActionPerformed
        if(!addToRunningTotal){
            clearAll();
        }
        if(thisChorusOption.isSelected()){
            addThisToTotal();
        }else{
            addAllToTotal();
        }
    }//GEN-LAST:event_learnButtonActionPerformed

    /**
     * enableOptions
     * enable or disable the chord, color, and approach check boxes
     * @param enable - true to enable, false to disable
     */
    private void enableOptions(boolean enable){
        chordBox.setEnabled(enable);
        colorBox.setEnabled(enable);
        approachBox.setEnabled(enable);
    }
    
    /**
     * newFile
     * Clear All data, set file name to newFile
     */
    public void newFile(){
        clearAll();
        filename = "newFile.counts";
        filenameLabel.setText(filename);
    }
    
    /**
     * addFromFile
     * Add counts from a .counts file
     * @throws FileNotFoundException 
     */
    public void addFromFile() throws FileNotFoundException{
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            addToCountsAndUpdateProbabilities(f);
        }
    }
    
    /**
     * Add from file, adding to counts and updating the probabilities
     * @param f - file to add counts from
     * @throws FileNotFoundException 
     */
    private void addToCountsAndUpdateProbabilities(File f) throws FileNotFoundException{
        learner.learnFrom(f);
        refreshDisplay();
    }
    
    /**
     * open
     * Loads a .counts file (Clears all data first)
     * @throws FileNotFoundException 
     */
    public void open() throws FileNotFoundException{
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            filename = f.getName();
            filenameLabel.setText(filename);
            updateCountsAndProbabilities(f);
        }
    }
    
    /**
     * updateCountsAndProbabilities
     * Clear All data, then add counts from file
     * @param f - file to be loaded
     * @throws FileNotFoundException 
     */
    private void updateCountsAndProbabilities(File f) throws FileNotFoundException{
        clearAll();
        learner.learnFrom(f);
        refreshDisplay();
    }
    
    /**
     * refreshDisplay
     * refreshes the display matrix
     */
    private void refreshDisplay(){
        //int x = IntervalLearner.intervalToIndex(interval1);
        DecimalFormat df = new DecimalFormat("#.##");
        if(displayProbabilities){
            if(displayOrder == FIRST){
                double [][] probabilities = learner.getDeg1Probs();
                for(int row = 0; row < probabilities.length; row++){
                    for(int column = 0; column < probabilities[row].length; column++){
                        probabilityLabels[row+1][column+1].setText(df.format(probabilities[row][column]));
                        probabilityLabels[row+1][column+1].setBackground(color(probabilities[row][column]));
                    }
                }
            }else{
                double [][] crossSection = learner.getProbsCrossSection(interval1);
                for(int y = 0; y < crossSection.length; y++){
                    for(int z = 0; z < crossSection[y].length; z++){
                        probabilityLabels[y+1][z+1].setText(df.format(crossSection[y][z]));
                        probabilityLabels[y+1][z+1].setBackground(color(crossSection[y][z]));
                    }
                }
            }
            
        }else{
            if(displayOrder == FIRST){
                int [][] counts = learner.getDeg1Counts();
                double [][] probabilities = learner.getDeg1Probs();
                for(int row = 0; row < counts.length; row++){
                    for(int column = 0; column < counts[row].length; column++){
                        probabilityLabels[row+1][column+1].setText(Integer.toString(counts[row][column]));
                        probabilityLabels[row+1][column+1].setBackground(color(probabilities[row][column]));
                    }
                }
            }else{
                int [][] crossSection = learner.getCountsCrossSection(interval1);
                double [][] probsCrossSection = learner.getProbsCrossSection(interval1);
                for(int y = 0; y < crossSection.length; y++){
                    for(int z = 0; z < crossSection[y].length; z++){
                        probabilityLabels[y+1][z+1].setText(Integer.toString(crossSection[y][z]));
                        probabilityLabels[y+1][z+1].setBackground(color(probsCrossSection[y][z]));
                    }
                }
            }
            
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton Chorus1;
    private javax.swing.JRadioButton Eighth;
    private javax.swing.JRadioButton GrammarRhythm;
    private javax.swing.JLabel OrderPanelLabel;
    private javax.swing.JRadioButton addToTotal;
    private javax.swing.JLabel addToTotalLabel;
    private javax.swing.JPanel addToTotalPanel;
    private javax.swing.JRadioButton allChorusesOption;
    private javax.swing.JCheckBox approachBox;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton chooseRange;
    private javax.swing.JCheckBox chordBox;
    private javax.swing.JButton clearAll;
    private javax.swing.JCheckBox colorBox;
    private javax.swing.JLabel destinationIntervals;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel firstIntervalPanel;
    private javax.swing.JRadioButton firstOrderButton;
    private javax.swing.JLabel generatePanelLabel;
    private javax.swing.JButton generateSolo;
    private javax.swing.JPanel generateSoloPanel;
    private javax.swing.JLabel interval1Label;
    private javax.swing.JSlider interval1Slider;
    private javax.swing.JButton learnButton;
    private javax.swing.JPanel learnButtonsPanel;
    private javax.swing.ButtonGroup learnFromGroup;
    private javax.swing.JLabel learnFromLabel;
    private javax.swing.JPanel learnFromPanel;
    private javax.swing.JPanel learnIntervalProbabilities;
    private javax.swing.JLabel learnLabel;
    private javax.swing.JCheckBox mergeCheckbox;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.ButtonGroup orderGroup;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JLabel orderViewLabel;
    private javax.swing.JToggleButton orderViewToggle;
    private javax.swing.ButtonGroup preAndPost;
    private javax.swing.JPanel probabilitiesPanel;
    private javax.swing.JPanel rangeAndMerge;
    private javax.swing.JCheckBox rectifyCheckbox;
    private javax.swing.JPanel rectifyPanel;
    private javax.swing.JRadioButton resetThenAdd;
    private javax.swing.ButtonGroup rhythmGroup;
    private javax.swing.JLabel rhythmLabel;
    private javax.swing.JRadioButton secondOrderButton;
    private javax.swing.JPanel soloOptions;
    private javax.swing.JLabel sourceIntervals;
    private javax.swing.JRadioButton thisChorusOption;
    private javax.swing.JPanel togglePanel;
    private javax.swing.JToggleButton toggleView;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel viewOptions;
    private javax.swing.ButtonGroup yesNo;
    // End of variables declaration//GEN-END:variables

    /**
     * getOrderDisplayFromButton
     * @return true for first order, false for second order
     */
    private boolean getOrderDisplayFromButton() {
        return orderViewToggle.getText().equals("First Order");
    }
}
