/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College.
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

/*
 * LickgenFrame.java
 *
 * Created on Jun 24, 2010, 4:57:53 PM
 */
package imp.lickgen;

import imp.data.advice.Advisor;
import imp.ImproVisor;
import imp.cluster.PolylistComparer;
import imp.com.*;
import imp.data.*;
import imp.gui.IntervalLearningPanel;
import imp.gui.LogDialog;
import imp.gui.Notate;
import imp.gui.WindowMenuItem;
import imp.gui.WindowRegistry;
import imp.lickgen.Grammar;
import imp.lickgen.LickGen;
import imp.lickgen.NoteConverter;
import imp.neuralnet.Critic;
import imp.util.ProfileFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.table.DefaultTableModel;
import polya.Polylist;
import polya.Tokenizer;
import imp.generalCluster.Cluster;
import imp.generalCluster.DataPoint;
import imp.generalCluster.IndexedMelodyPart;
import imp.generalCluster.JCA;
import imp.generalCluster.ClusterSet;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.metrics.Metric;
import imp.generalCluster.metrics.MetricListFactories.DefaultMetricListFactory;
import imp.generalCluster.metrics.MetricListFactories.MetricListFactory;
import imp.generalCluster.metrics.MetricListFactories.RhythmMetricListFactory;
import imp.trading.UserRhythmSelecterDialog;
import imp.util.Preferences;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author David Morrison, Robert Keller
 * Includes additions by Mark Heimann to support additional grammars.
 * Includes additions by Joseph Yaconelli to support motifs.
 */
public class LickgenFrame
        extends javax.swing.JFrame
        implements imp.Constants {

    private final int themeLength = 8;
    private final double themeProb = 0.4;
    private final double transposeProb = 0.5;
    private final double invertProb = 0.1;
    private final double reverseProb = 0.1;
    private final Notate notate;
    private ArrayList<String> melodyData = new ArrayList<String>();
    private int minPitch = 60;
    private int maxPitch = 82;
    private int minInterval = 0;
    private int maxInterval = 6;
    private int minDuration = 8;
    private int maxDuration = 8;
    private double totalBeats = 8;
    private int totalSlots = (int) (BEAT * totalBeats);
    private double restProb = 0.1;
    private double leapProb = 0.2;
    private double chordToneWeight = 0.7;
    private double scaleToneWeight = 0.1;
    private double colorToneWeight = 0.05;
    private double chordToneDecayRate = 0.1;
    private boolean avoidRepeats = true;
    private boolean useGrammar = true;
    private boolean autoFill = true;
    private final LickGen lickgen;
    private final CommandManager cm;
    private final StringWriter brickProductionsWriter = new StringWriter();
    private final StringWriter windowProductionsWriter = new StringWriter();
    
    private Double gradeFromCritic = null;

    /**
     * File extension for solo profiles
     */
    private String profileExt;

    /**
     * JFile Chooser for saving solo profiles
     */
    private JFileChooser saveCWFC;
    /**
     * JFile Chooser for opening solo profiles
     */
    private JFileChooser openCWFC;
    private boolean rectify = true;
    private boolean useCritic = false;
    private static final int DEFAULT_GRADE = 7; //Default criticGrade for filter
    private int criticGradeThreshold = DEFAULT_GRADE;
    private boolean continuallyGenerate = true;
    /**
     * ArrayList of JTextField arrays, used to display probabilities used in
     * lick generation
     */
    private final ArrayList<JTextField[]> lickPrefs = new ArrayList<JTextField[]>();
    /**
     * this will be set to true during extraction of all measures in a corpus
     */
    private final boolean allMeasures = false;

    /*
     * Initialize critic, from Notate leadsheet.
     */
    private final Critic critic;
    /*
     * TreeMap for usage with style recognition
     */
    private TreeMap<String, Critic> critics;
    /*
     * Number of expected weight files, will be used to encourage users to
     * download the rest of the weights if they desire style recognition
     * FIX: Magic Number!
     */
    private static final int numCritics = 22;
    /**
     * Create IntervalLearningPanel
     */
    private IntervalLearningPanel intervalLearningTab;    

    private DocumentListener windowSlideDocListener;

    /**
     * Creates new LickgenFrame
     * @param notate
     * @param lickgen
     * @param cm
     */
    public LickgenFrame(Notate notate, LickGen lickgen, CommandManager cm) {
        this.notate = notate;
        this.lickgen = lickgen;
        this.cm = cm;

        critic = notate.getCritic();
        initComponents();
        setGrammarName(notate.getGrammarName());
        setUpWindowSlideDocListener();
    }


    /**
     * Initializes the solo profile file choosers.
     */
    private void initCompFileChoosers() {
        ProfileFilter pFilter = new ProfileFilter();
        profileExt = ProfileFilter.EXTENSION;

        saveCWFC = new JFileChooser();
        openCWFC = new JFileChooser();

        saveCWFC.setDialogType(JFileChooser.SAVE_DIALOG);
        saveCWFC.setDialogTitle("Save Solo Profile");
        saveCWFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveCWFC.resetChoosableFileFilters();
        saveCWFC.addChoosableFileFilter(pFilter);

        openCWFC.setDialogType(JFileChooser.OPEN_DIALOG);
        openCWFC.setDialogTitle("Open Solo Profile");
        openCWFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        openCWFC.resetChoosableFileFilters();
        openCWFC.addChoosableFileFilter(pFilter);
    }
    
private void gradeBadBtnActionPerformed(java.awt.event.ActionEvent evt)
 {
     grade2BtnActionPerformed(evt);
 }

private void gradeAverageBtnActionPerformed(java.awt.event.ActionEvent evt)
 {
     grade5BtnActionPerformed(evt);
 }
  
private void gradeGoodBtnActionPerformed(java.awt.event.ActionEvent evt)
 {
     grade8BtnActionPerformed(evt);
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

        learningBaseButtonGroup = new javax.swing.ButtonGroup();
        generatorPane = new javax.swing.JTabbedPane();
        lickGenPanel = new javax.swing.JPanel();
        rhythmPanel = new javax.swing.JPanel();
        rhythmScrollPane = new javax.swing.JScrollPane();
        rhythmField = new javax.swing.JTextArea();
        relativePanel = new javax.swing.JPanel();
        relativeScrollPane = new javax.swing.JScrollPane();
        relativeField = new javax.swing.JTextArea();
        lickGenerationButtonsPanel = new javax.swing.JPanel();
        generateLickButton = new javax.swing.JButton();
        genRhythmButton = new javax.swing.JButton();
        fillMelodyButton = new javax.swing.JButton();
        fillRelativePitchMelodyButton = new javax.swing.JButton();
        getAbstractMelodyButton = new javax.swing.JButton();
        getSelRhythmButton = new javax.swing.JButton();
        playLickButton = new javax.swing.JButton();
        stopLickButton = new javax.swing.JButton();
        saveLickButton = new javax.swing.JButton();
        chooseGrammarButton = new javax.swing.JButton();
        currentGrammarLabel = new javax.swing.JLabel();
        lickgenParametersPanel = new javax.swing.JPanel();
        pitchLabel = new javax.swing.JLabel();
        maxLabel = new javax.swing.JLabel();
        maxPitchField = new javax.swing.JTextField();
        minPitchField = new javax.swing.JTextField();
        intervalLabel = new javax.swing.JLabel();
        minIntervalField = new javax.swing.JTextField();
        maxIntervalField = new javax.swing.JTextField();
        durationLabel = new javax.swing.JLabel();
        minDurationField = new javax.swing.JTextField();
        maxDurationField = new javax.swing.JTextField();
        totalBeatsField = new javax.swing.JTextField();
        totalBeatsLabel = new javax.swing.JLabel();
        restProbLabel = new javax.swing.JLabel();
        restProbField = new javax.swing.JTextField();
        leapProbLabel = new javax.swing.JLabel();
        leapProbField = new javax.swing.JTextField();
        avoidRepeatsCheckbox = new javax.swing.JCheckBox();
        recurrentCheckbox = new javax.swing.JCheckBox();
        generationGapLabel = new javax.swing.JLabel();
        gapField = new javax.swing.JTextField();
        useSoloistCheckBox = new javax.swing.JCheckBox();
        useHeadCheckBox = new javax.swing.JCheckBox();
        regenerateHeadDataBtn = new javax.swing.JButton();
        generationSelectionButton = new javax.swing.JButton();
        minLabel = new javax.swing.JLabel();
        rectifyPanel = new javax.swing.JPanel();
        rectifyCheckBox = new javax.swing.JCheckBox();
        chordBox = new javax.swing.JCheckBox();
        colorBox = new javax.swing.JCheckBox();
        approachBox = new javax.swing.JCheckBox();
        motifnessGenerationPanel = new javax.swing.JPanel();
        motifnessGenerationSlider = new javax.swing.JSlider();
        scaleChoicePanel = new javax.swing.JPanel();
        scaleLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        rootLabel = new javax.swing.JLabel();
        rootComboBox = new javax.swing.JComboBox();
        toneProbabilityPanel = new javax.swing.JPanel();
        chordToneProbLabel = new javax.swing.JLabel();
        colorToneProbLabel = new javax.swing.JLabel();
        scaleToneProbLabel = new javax.swing.JLabel();
        chordToneDecayRateLabel = new javax.swing.JLabel();
        chordToneWeightField = new javax.swing.JTextField();
        colorToneWeightField = new javax.swing.JTextField();
        scaleToneWeightField = new javax.swing.JTextField();
        chordToneDecayField = new javax.swing.JTextField();
        lickGradeButtonsPanel = new javax.swing.JPanel();
        gradeLabel = new javax.swing.JLabel();
        saveLickTF = new javax.swing.JTextField();
        saveLickWithLabelLabel = new javax.swing.JLabel();
        grade1Btn = new javax.swing.JButton();
        grade1Btn.setUI(new MetalButtonUI());
        grade2Btn = new javax.swing.JButton();
        grade2Btn.setUI(new MetalButtonUI());
        grade3Btn = new javax.swing.JButton();
        grade3Btn.setUI(new MetalButtonUI());
        grade4Btn = new javax.swing.JButton();
        grade4Btn.setUI(new MetalButtonUI());
        grade5Btn = new javax.swing.JButton();
        grade5Btn.setUI(new MetalButtonUI());
        grade6Btn = new javax.swing.JButton();
        grade6Btn.setUI(new MetalButtonUI());
        grade7Btn = new javax.swing.JButton();
        grade7Btn.setUI(new MetalButtonUI());
        grade8Btn = new javax.swing.JButton();
        grade8Btn.setUI(new MetalButtonUI());
        grade9Btn = new javax.swing.JButton();
        grade9Btn.setUI(new MetalButtonUI());
        grade10Btn = new javax.swing.JButton();
        grade10Btn.setUI(new MetalButtonUI());
        gradeBadBtn = new javax.swing.JButton();
        gradeAverageBtn = new javax.swing.JButton();
        gradeGoodBtn = new javax.swing.JButton();
        continuallyGenerateCheckBox = new javax.swing.JCheckBox();
        styleRecognitionButton = new javax.swing.JButton();
        ProbFillClearPanel = new javax.swing.JPanel();
        clearProbsButton = new javax.swing.JButton();
        FillProbsButton = new javax.swing.JButton();
        autoFillCheckBox = new javax.swing.JCheckBox();
        soloCorrectionPanel = new javax.swing.JPanel();
        offsetByMeasureGradeSoloButton = new javax.swing.JButton();
        forwardGradeSoloButton = new javax.swing.JButton();
        backwardGradeSoloButton = new javax.swing.JButton();
        resetSelectionButton = new javax.swing.JButton();
        gradeAllMeasuresButton = new javax.swing.JButton();
        regenerateLickForSoloButton = new javax.swing.JButton();
        gradeLickFromStaveButton = new javax.swing.JButton();
        lickFromStaveGradeTextField = new javax.swing.JTextField();
        useCriticCheckBox = new javax.swing.JCheckBox();
        criticGradeTextField = new javax.swing.JTextField();
        counterForCriticTextField = new javax.swing.JTextField();
        criticGradeLabel = new javax.swing.JLabel();
        counterForCriticLabel = new javax.swing.JLabel();
        loadRandomGrammarButton = new javax.swing.JButton();
        pitchProbabilitiesPanel = new javax.swing.JPanel();
        chordProbPanel = new javax.swing.JPanel();
        grammarLearningPanel = new javax.swing.JPanel();
        finalLabel = new javax.swing.JLabel();
        topGrammarLearningPanel = new javax.swing.JPanel();
        learningStep0Label = new javax.swing.JLabel();
        emptyBaseLearningButton = new javax.swing.JRadioButton();
        selectBaseLearningButton = new javax.swing.JRadioButton();
        emptyMotifBaseLearningButton = new javax.swing.JRadioButton();
        windowParametersPanel = new javax.swing.JPanel();
        windowSizeLabel = new javax.swing.JLabel();
        windowSlideLabel = new javax.swing.JLabel();
        numClusterRepsLabel = new javax.swing.JLabel();
        windowSizeField = new javax.swing.JTextField();
        windowSlideField = new javax.swing.JTextField();
        useRelativeWindowsCheckbox = new javax.swing.JCheckBox();
        useBricksCheckbox = new javax.swing.JCheckBox();
        numClusterRepsField = new javax.swing.JTextField();
        useMarkovCheckbox = new javax.swing.JCheckBox();
        MarkovLengthField = new javax.swing.JTextField();
        markovChainLengthLabel = new javax.swing.JLabel();
        useWindowsCheckbox = new javax.swing.JCheckBox();
        useRelativeBricksCheckbox = new javax.swing.JCheckBox();
        useAbstractBricksCheckbox = new javax.swing.JCheckBox();
        useAbstractWindowsCheckbox = new javax.swing.JCheckBox();
        userRhythmCheckBox = new javax.swing.JCheckBox();
        rhythmClusterCheckbox = new javax.swing.JCheckBox();
        motifParametersPanel = new javax.swing.JPanel();
        useMotifsCheckbox = new javax.swing.JCheckBox();
        motifnessSlider = new javax.swing.JSlider();
        saveGrammarAsButton = new javax.swing.JButton();
        openCorpusBtn = new javax.swing.JButton();
        toGrammarBtn = new javax.swing.JButton();
        testGeneration = new javax.swing.JButton();
        rhythmClusterPanel = new javax.swing.JPanel();
        neuralNetworkPanel = new javax.swing.JPanel();
        nnetOutputPanel = new javax.swing.JPanel();
        nnetScrollPane = new javax.swing.JScrollPane();
        nnetOutputTextField = new javax.swing.JTextArea();
        layerInfoScrollPane = new javax.swing.JScrollPane();
        layerDataTable = new javax.swing.JTable();
        nnetWeightGenerationPanel = new javax.swing.JPanel();
        generateWeightFileButton = new javax.swing.JButton();
        getNetworkStatsButton = new javax.swing.JButton();
        clearWeightFileButton = new javax.swing.JButton();
        loadWeightFileButton = new javax.swing.JButton();
        resetNnetInstructionsButton = new javax.swing.JButton();
        resetDefaultValuesButton = new javax.swing.JButton();
        resetNetworkButton = new javax.swing.JButton();
        nnetParametersPanel = new javax.swing.JPanel();
        trainingFileButton = new javax.swing.JButton();
        trainingFileTextField = new javax.swing.JTextField();
        epochLimitLabel = new javax.swing.JLabel();
        epochLimitTextField = new javax.swing.JTextField();
        learningRateLabel = new javax.swing.JLabel();
        learningRateTextField = new javax.swing.JTextField();
        mseGoalLabel = new javax.swing.JLabel();
        mseGoalTextField = new javax.swing.JTextField();
        modeLabel = new javax.swing.JLabel();
        modeComboBox = new javax.swing.JComboBox();
        weightFileTextField = new javax.swing.JTextField();
        numberOfLayersLabel = new javax.swing.JLabel();
        numberOfLayersTextField = new javax.swing.JTextField();
        addLayerToTableButton = new javax.swing.JButton();
        removeLayerFromTableButton = new javax.swing.JButton();
        moveLayerUpTableButton = new javax.swing.JButton();
        moveLayerDownTableButton = new javax.swing.JButton();
        weightFileButton = new javax.swing.JButton();
        generatorMenuBar1 = new javax.swing.JMenuBar();
        grammarMenu1 = new javax.swing.JMenu();
        openGrammarMI1 = new javax.swing.JMenuItem();
        showLogMI1 = new javax.swing.JMenuItem();
        saveGrammarMI1 = new javax.swing.JMenuItem();
        editGrammarMI1 = new javax.swing.JMenuItem();
        reloadGrammarMI1 = new javax.swing.JMenuItem();
        toCriticMI1 = new javax.swing.JCheckBoxMenuItem();
        showCriticMI1 = new javax.swing.JMenuItem();
        useGrammarMI1 = new javax.swing.JCheckBoxMenuItem();
        generatorWindowMenu1 = new javax.swing.JMenu();
        closeWindowMI2 = new javax.swing.JMenuItem();
        cascadeMI2 = new javax.swing.JMenuItem();
        windowMenuSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Grammar Controls");
        setMinimumSize(new java.awt.Dimension(1000, 850));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        generatorPane.setBackground(new java.awt.Color(218, 215, 215));
        generatorPane.setMinimumSize(new java.awt.Dimension(1200, 700));
        generatorPane.setPreferredSize(new java.awt.Dimension(1200, 700));

        lickGenPanel.setMinimumSize(new java.awt.Dimension(1450, 903));
        lickGenPanel.setPreferredSize(new java.awt.Dimension(1450, 903));
        lickGenPanel.setLayout(new java.awt.GridBagLayout());

        rhythmPanel.setBackground(new java.awt.Color(218, 215, 215));
        rhythmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abstract Melody", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        rhythmPanel.setMinimumSize(new java.awt.Dimension(500, 200));
        rhythmPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        rhythmPanel.setLayout(new java.awt.GridBagLayout());

        rhythmScrollPane.setBorder(null);
        rhythmScrollPane.setMinimumSize(new java.awt.Dimension(223, 180));
        rhythmScrollPane.setPreferredSize(new java.awt.Dimension(223, 180));

        rhythmField.setColumns(20);
        rhythmField.setLineWrap(true);
        rhythmField.setRows(500);
        rhythmField.setBorder(null);
        rhythmField.setMinimumSize(new java.awt.Dimension(440, 100));
        rhythmField.setPreferredSize(new java.awt.Dimension(440, 1000));
        rhythmScrollPane.setViewportView(rhythmField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        rhythmPanel.add(rhythmScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        lickGenPanel.add(rhythmPanel, gridBagConstraints);

        relativePanel.setBackground(new java.awt.Color(218, 215, 215));
        relativePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Relative-Pitch Melody", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        relativePanel.setMinimumSize(new java.awt.Dimension(850, 200));
        relativePanel.setPreferredSize(new java.awt.Dimension(850, 200));
        relativePanel.setLayout(new java.awt.GridBagLayout());

        relativeScrollPane.setBorder(null);
        relativeScrollPane.setMinimumSize(new java.awt.Dimension(223, 180));
        relativeScrollPane.setPreferredSize(new java.awt.Dimension(223, 180));

        relativeField.setColumns(20);
        relativeField.setLineWrap(true);
        relativeField.setRows(500);
        relativeField.setBorder(null);
        relativeField.setMinimumSize(new java.awt.Dimension(700, 100));
        relativeField.setPreferredSize(new java.awt.Dimension(800, 1000));
        relativeScrollPane.setViewportView(relativeField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        relativePanel.add(relativeScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 0.3;
        lickGenPanel.add(relativePanel, gridBagConstraints);

        lickGenerationButtonsPanel.setBackground(new java.awt.Color(218, 215, 215));
        lickGenerationButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lick Generation and Extraction", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickGenerationButtonsPanel.setMaximumSize(new java.awt.Dimension(500, 400));
        lickGenerationButtonsPanel.setMinimumSize(new java.awt.Dimension(440, 200));
        lickGenerationButtonsPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        lickGenerationButtonsPanel.setLayout(new java.awt.GridBagLayout());

        generateLickButton.setToolTipText("Generate a melody using the current grammar.");
        generateLickButton.setLabel("Generate and Fill Melody");
        generateLickButton.setMaximumSize(new java.awt.Dimension(180, 29));
        generateLickButton.setMinimumSize(new java.awt.Dimension(120, 29));
        generateLickButton.setPreferredSize(new java.awt.Dimension(120, 29));
        generateLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(generateLickButton, gridBagConstraints);

        genRhythmButton.setToolTipText("Generate the rhythm pattern for a lick, without the actual notes.");
        genRhythmButton.setLabel("Generate without Filling");
        genRhythmButton.setMaximumSize(new java.awt.Dimension(180, 29));
        genRhythmButton.setMinimumSize(new java.awt.Dimension(180, 29));
        genRhythmButton.setPreferredSize(new java.awt.Dimension(180, 29));
        genRhythmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genRhythmButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(genRhythmButton, gridBagConstraints);

        fillMelodyButton.setText("Fill Abstract Melody");
        fillMelodyButton.setToolTipText("Fill the notes for the given pattern.");
        fillMelodyButton.setMinimumSize(new java.awt.Dimension(120, 29));
        fillMelodyButton.setPreferredSize(new java.awt.Dimension(120, 29));
        fillMelodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillMelodyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(fillMelodyButton, gridBagConstraints);

        fillRelativePitchMelodyButton.setToolTipText("Fill the notes for the given pattern.");
        fillRelativePitchMelodyButton.setLabel("Fill Relative-Pitch Melody");
        fillRelativePitchMelodyButton.setMinimumSize(new java.awt.Dimension(180, 29));
        fillRelativePitchMelodyButton.setPreferredSize(new java.awt.Dimension(180, 29));
        fillRelativePitchMelodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillRelativePitchMelodyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(fillRelativePitchMelodyButton, gridBagConstraints);

        getAbstractMelodyButton.setToolTipText("Extract the abstract melody and the relative-pitch melody from the leadsheet selection.");
        getAbstractMelodyButton.setLabel("Extract Abstract and Relative-Pitch Melodies from Leadsheet");
        getAbstractMelodyButton.setMaximumSize(new java.awt.Dimension(500, 29));
        getAbstractMelodyButton.setMinimumSize(new java.awt.Dimension(250, 29));
        getAbstractMelodyButton.setPreferredSize(new java.awt.Dimension(420, 29));
        getAbstractMelodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAbstractMelodyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(getAbstractMelodyButton, gridBagConstraints);

        getSelRhythmButton.setToolTipText("Extract the rhythm from the leadsheet.");
        getSelRhythmButton.setLabel("Extract Rhythm Only");
        getSelRhythmButton.setMaximumSize(new java.awt.Dimension(500, 29));
        getSelRhythmButton.setMinimumSize(new java.awt.Dimension(420, 29));
        getSelRhythmButton.setPreferredSize(new java.awt.Dimension(420, 29));
        getSelRhythmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getSelRhythmButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(getSelRhythmButton, gridBagConstraints);

        playLickButton.setText("Play");
        playLickButton.setToolTipText("Play the lick again.");
        playLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(playLickButton, gridBagConstraints);

        stopLickButton.setText("Stop");
        stopLickButton.setToolTipText("Stop playing.");
        stopLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(stopLickButton, gridBagConstraints);

        saveLickButton.setText("Save");
        saveLickButton.setToolTipText("Save the lick in the vocabulary.");
        saveLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(saveLickButton, gridBagConstraints);

        chooseGrammarButton.setText("Choose Grammar");
        chooseGrammarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseGrammarButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lickGenerationButtonsPanel.add(chooseGrammarButton, gridBagConstraints);

        currentGrammarLabel.setText("Current Grammar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        lickGenerationButtonsPanel.add(currentGrammarLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(lickGenerationButtonsPanel, gridBagConstraints);

        lickgenParametersPanel.setBackground(new java.awt.Color(218, 215, 215));
        lickgenParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Generation Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickgenParametersPanel.setMinimumSize(new java.awt.Dimension(600, 300));
        lickgenParametersPanel.setPreferredSize(new java.awt.Dimension(600, 300));
        lickgenParametersPanel.setLayout(new java.awt.GridBagLayout());

        pitchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pitchLabel.setText("Pitch");
        pitchLabel.setToolTipText("Pitch of a note in the lick\n");
        pitchLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pitchLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(pitchLabel, gridBagConstraints);

        maxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxLabel.setText("Max");
        maxLabel.setToolTipText("");
        maxLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        maxLabel.setMaximumSize(new java.awt.Dimension(30, 15));
        maxLabel.setMinimumSize(new java.awt.Dimension(30, 15));
        maxLabel.setPreferredSize(new java.awt.Dimension(30, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lickgenParametersPanel.add(maxLabel, gridBagConstraints);

        maxPitchField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxPitchField.setText("84");
        maxPitchField.setToolTipText("The maximum pitch in a generated lick.");
        maxPitchField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxPitchField.setPreferredSize(new java.awt.Dimension(60, 24));
        maxPitchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxPitchFieldFocusLost(evt);
            }
        });
        maxPitchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxPitchFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxPitchField, gridBagConstraints);

        minPitchField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minPitchField.setText("54");
        minPitchField.setToolTipText("The minimum pitch in a generated lick.");
        minPitchField.setMinimumSize(new java.awt.Dimension(60, 24));
        minPitchField.setPreferredSize(new java.awt.Dimension(60, 24));
        minPitchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                minPitchFieldFocusLost(evt);
            }
        });
        minPitchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minPitchFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minPitchField, gridBagConstraints);

        intervalLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        intervalLabel.setText("Interval");
        intervalLabel.setToolTipText("The maximum interval between two pitches in the lick");
        intervalLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        intervalLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(intervalLabel, gridBagConstraints);

        minIntervalField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minIntervalField.setText("0");
        minIntervalField.setToolTipText("The minimum interval from one note to the next, if not a leap.");
        minIntervalField.setMinimumSize(new java.awt.Dimension(60, 24));
        minIntervalField.setPreferredSize(new java.awt.Dimension(60, 24));
        minIntervalField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                minIntervalFieldFocusLost(evt);
            }
        });
        minIntervalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minIntervalFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minIntervalField, gridBagConstraints);

        maxIntervalField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxIntervalField.setText("9");
        maxIntervalField.setToolTipText("The maximum interval from one note to the next, if not a leap.");
        maxIntervalField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxIntervalField.setPreferredSize(new java.awt.Dimension(60, 24));
        maxIntervalField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxIntervalFieldFocusLost(evt);
            }
        });
        maxIntervalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxIntervalFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxIntervalField, gridBagConstraints);

        durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        durationLabel.setText("Duration");
        durationLabel.setToolTipText("Duration of beats in generated lick\n");
        durationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        durationLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(durationLabel, gridBagConstraints);

        minDurationField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minDurationField.setText("8");
        minDurationField.setToolTipText("The minimum duration of a generated note.");
        minDurationField.setEnabled(false);
        minDurationField.setMinimumSize(new java.awt.Dimension(60, 24));
        minDurationField.setPreferredSize(new java.awt.Dimension(60, 24));
        minDurationField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                minDurationFieldFocusLost(evt);
            }
        });
        minDurationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minDurationFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minDurationField, gridBagConstraints);

        maxDurationField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxDurationField.setText("2");
        maxDurationField.setToolTipText("The minimum duration of a generated note.");
        maxDurationField.setEnabled(false);
        maxDurationField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxDurationField.setPreferredSize(new java.awt.Dimension(60, 24));
        maxDurationField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxDurationFieldFocusLost(evt);
            }
        });
        maxDurationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDurationFieldActionPerformed(evt);
            }
        });
        maxDurationField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                maxDurationFieldenterLickKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxDurationField, gridBagConstraints);

        totalBeatsField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        totalBeatsField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        totalBeatsField.setToolTipText("The number of beats in the lick.");
        totalBeatsField.setMinimumSize(new java.awt.Dimension(60, 24));
        totalBeatsField.setPreferredSize(new java.awt.Dimension(60, 24));
        totalBeatsField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                totalBeatsFieldGetsFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalBeatsFieldFocusLost(evt);
            }
        });
        totalBeatsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalBeatsFieldActionPerformed(evt);
            }
        });
        totalBeatsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                totalBeatsFieldenterLickKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(totalBeatsField, gridBagConstraints);

        totalBeatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalBeatsLabel.setText("Beats");
        totalBeatsLabel.setToolTipText("The total number of beats for the lick.");
        totalBeatsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        totalBeatsLabel.setMaximumSize(new java.awt.Dimension(160, 16));
        totalBeatsLabel.setMinimumSize(new java.awt.Dimension(160, 16));
        totalBeatsLabel.setPreferredSize(new java.awt.Dimension(160, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
        lickgenParametersPanel.add(totalBeatsLabel, gridBagConstraints);

        restProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        restProbLabel.setText("Rest Probability");
        restProbLabel.setToolTipText("The probability of generating a rest");
        restProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        restProbLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
        lickgenParametersPanel.add(restProbLabel, gridBagConstraints);

        restProbField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        restProbField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        restProbField.setText("0.1");
        restProbField.setToolTipText("The probability of a rest vs. a note.");
        restProbField.setEnabled(false);
        restProbField.setMinimumSize(new java.awt.Dimension(60, 24));
        restProbField.setPreferredSize(new java.awt.Dimension(60, 24));
        restProbField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restProbFieldActionPerformed(evt);
            }
        });
        restProbField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                restProbFieldGetsFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                restProbFieldFocusLost(evt);
            }
        });
        restProbField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                restProbFieldenterLickKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(restProbField, gridBagConstraints);

        leapProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        leapProbLabel.setText("Leap Probability");
        leapProbLabel.setMaximumSize(new java.awt.Dimension(220, 16));
        leapProbLabel.setMinimumSize(new java.awt.Dimension(220, 16));
        leapProbLabel.setPreferredSize(new java.awt.Dimension(220, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        lickgenParametersPanel.add(leapProbLabel, gridBagConstraints);

        leapProbField.setText("0.05");
        leapProbField.setToolTipText("The probability of making a leap outside the maximum interval.");
        leapProbField.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        leapProbField.setMinimumSize(new java.awt.Dimension(60, 24));
        leapProbField.setPreferredSize(new java.awt.Dimension(60, 24));
        leapProbField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leapProbFieldActionPerformed(evt);
            }
        });
        leapProbField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                leapProbFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(leapProbField, gridBagConstraints);

        avoidRepeatsCheckbox.setSelected(true);
        avoidRepeatsCheckbox.setToolTipText("Avoid generating repeated pitches");
        avoidRepeatsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        avoidRepeatsCheckbox.setLabel("Avoid repeat pitches");
        avoidRepeatsCheckbox.setMaximumSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.setMinimumSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.setPreferredSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avoidRepeatsCheckboxActionPerformed(evt);
            }
        });
        lickgenParametersPanel.add(avoidRepeatsCheckbox, new java.awt.GridBagConstraints());

        recurrentCheckbox.setText("Recurrent");
        recurrentCheckbox.setToolTipText("If checked, keep generating licks until stop is pressed. Licks may be recovered using undo. This will eventually fill up memory.\n");
        recurrentCheckbox.setMaximumSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.setMinimumSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.setPreferredSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recurrentCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        lickgenParametersPanel.add(recurrentCheckbox, gridBagConstraints);
        recurrentCheckbox.getAccessibleContext().setAccessibleName("");

        generationGapLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        generationGapLabel.setText("Recurrent Lead (beats):");
        generationGapLabel.setToolTipText("Gap in beats before end of chorus, at which point the next chorus is generated\n");
        generationGapLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        generationGapLabel.setMaximumSize(new java.awt.Dimension(220, 16));
        generationGapLabel.setMinimumSize(new java.awt.Dimension(220, 16));
        generationGapLabel.setPreferredSize(new java.awt.Dimension(220, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(generationGapLabel, gridBagConstraints);
        generationGapLabel.getAccessibleContext().setAccessibleName("");
        generationGapLabel.getAccessibleContext().setAccessibleDescription("");

        gapField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        gapField.setText("0.5");
        gapField.setToolTipText("Sets the leading gap between when the next lick is generated and the previous one ends.");
        gapField.setMaximumSize(new java.awt.Dimension(45, 24));
        gapField.setMinimumSize(new java.awt.Dimension(45, 24));
        gapField.setPreferredSize(new java.awt.Dimension(45, 24));
        gapField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gapFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        lickgenParametersPanel.add(gapField, gridBagConstraints);

        useSoloistCheckBox.setText("Use Soloist");
        useSoloistCheckBox.setMaximumSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.setMinimumSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.setPreferredSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSoloistCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(useSoloistCheckBox, gridBagConstraints);

        useHeadCheckBox.setLabel("Use Head");
        useHeadCheckBox.setMaximumSize(new java.awt.Dimension(100, 23));
        useHeadCheckBox.setMinimumSize(new java.awt.Dimension(100, 23));
        useHeadCheckBox.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(useHeadCheckBox, gridBagConstraints);

        regenerateHeadDataBtn.setText("Regenerate Head Data");
        regenerateHeadDataBtn.setMaximumSize(new java.awt.Dimension(200, 29));
        regenerateHeadDataBtn.setMinimumSize(new java.awt.Dimension(200, 29));
        regenerateHeadDataBtn.setPreferredSize(new java.awt.Dimension(200, 29));
        regenerateHeadDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenerateHeadDataBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        lickgenParametersPanel.add(regenerateHeadDataBtn, gridBagConstraints);

        generationSelectionButton.setText("Size of Selection");
        generationSelectionButton.setToolTipText("Lock the selection for lick generation.");
        generationSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generationSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        lickgenParametersPanel.add(generationSelectionButton, gridBagConstraints);

        minLabel.setText("Min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lickgenParametersPanel.add(minLabel, gridBagConstraints);

        rectifyPanel.setMinimumSize(new java.awt.Dimension(400, 23));
        rectifyPanel.setOpaque(false);
        rectifyPanel.setPreferredSize(new java.awt.Dimension(400, 23));
        rectifyPanel.setLayout(new java.awt.GridBagLayout());

        rectifyCheckBox.setSelected(true);
        rectifyCheckBox.setText("Rectify\n");
        rectifyCheckBox.setToolTipText("Rectify the generated melody.\n");
        rectifyCheckBox.setMaximumSize(new java.awt.Dimension(200, 25));
        rectifyCheckBox.setMinimumSize(new java.awt.Dimension(90, 25));
        rectifyCheckBox.setPreferredSize(new java.awt.Dimension(90, 25));
        rectifyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectifyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rectifyPanel.add(rectifyCheckBox, gridBagConstraints);

        chordBox.setSelected(true);
        chordBox.setText("Chord");
        chordBox.setMaximumSize(new java.awt.Dimension(200, 25));
        chordBox.setMinimumSize(new java.awt.Dimension(90, 25));
        chordBox.setPreferredSize(new java.awt.Dimension(90, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        rectifyPanel.add(chordBox, gridBagConstraints);

        colorBox.setSelected(true);
        colorBox.setText("Color");
        colorBox.setMaximumSize(new java.awt.Dimension(200, 25));
        colorBox.setMinimumSize(new java.awt.Dimension(90, 25));
        colorBox.setPreferredSize(new java.awt.Dimension(90, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        rectifyPanel.add(colorBox, gridBagConstraints);

        approachBox.setSelected(true);
        approachBox.setText("Approach");
        approachBox.setMaximumSize(new java.awt.Dimension(200, 25));
        approachBox.setMinimumSize(new java.awt.Dimension(90, 25));
        approachBox.setPreferredSize(new java.awt.Dimension(90, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        rectifyPanel.add(approachBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(rectifyPanel, gridBagConstraints);

        motifnessGenerationPanel.setBackground(new java.awt.Color(218, 215, 215));
        motifnessGenerationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Motifness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        motifnessGenerationPanel.setToolTipText("");
        motifnessGenerationPanel.setMinimumSize(new java.awt.Dimension(300, 67));
        motifnessGenerationPanel.setPreferredSize(new java.awt.Dimension(300, 67));
        motifnessGenerationPanel.setLayout(new java.awt.GridBagLayout());

        motifnessGenerationSlider.setToolTipText("Amount of motifs to incorporate into solo");
        motifnessGenerationSlider.setMinimumSize(new java.awt.Dimension(150, 48));
        motifnessGenerationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                motifnessGenerationSliderStateChanged(evt);
            }
        });
        motifnessGenerationPanel.add(motifnessGenerationSlider, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.1;
        lickgenParametersPanel.add(motifnessGenerationPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(lickgenParametersPanel, gridBagConstraints);

        scaleChoicePanel.setBackground(new java.awt.Color(218, 215, 215));
        scaleChoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Scale Tone Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        scaleChoicePanel.setMinimumSize(new java.awt.Dimension(500, 100));
        scaleChoicePanel.setPreferredSize(new java.awt.Dimension(500, 100));
        scaleChoicePanel.setLayout(new java.awt.GridBagLayout());

        scaleLabel.setText("Scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 20);
        scaleChoicePanel.add(scaleLabel, gridBagConstraints);

        typeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        scaleChoicePanel.add(typeLabel, gridBagConstraints);

        scaleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Use_First_Scale" }));
        scaleComboBox.setSelectedIndex(0);
        scaleComboBox.setToolTipText("The type of scale to use in scale tones. The default is the first scale associated with the chord.\n");
        scaleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scaleChoicePanel.add(scaleComboBox, gridBagConstraints);

        rootLabel.setText("Root:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        scaleChoicePanel.add(rootLabel, gridBagConstraints);

        rootComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#", "Gb", "G", "G#/Ab", "A", "A#/Bb", "B" }));
        rootComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        scaleChoicePanel.add(rootComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(scaleChoicePanel, gridBagConstraints);

        toneProbabilityPanel.setBackground(new java.awt.Color(218, 215, 215));
        toneProbabilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Category Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        toneProbabilityPanel.setMinimumSize(new java.awt.Dimension(500, 100));
        toneProbabilityPanel.setPreferredSize(new java.awt.Dimension(500, 100));
        toneProbabilityPanel.setLayout(new java.awt.GridBagLayout());

        chordToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chordToneProbLabel.setText("<html>Chord <br>Tone</html");
        chordToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(chordToneProbLabel, gridBagConstraints);

        colorToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colorToneProbLabel.setText("<html>Color<br>Tone</html>");
        colorToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        colorToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(colorToneProbLabel, gridBagConstraints);

        scaleToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scaleToneProbLabel.setText("<html>Scale <br>Tone</html>");
        scaleToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scaleToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(scaleToneProbLabel, gridBagConstraints);

        chordToneDecayRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chordToneDecayRateLabel.setText("<html><align=center>Chord Tone <br> Decay Rate </align></html>");
        chordToneDecayRateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordToneDecayRateLabel.setMinimumSize(new java.awt.Dimension(120, 32));
        chordToneDecayRateLabel.setPreferredSize(new java.awt.Dimension(120, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(chordToneDecayRateLabel, gridBagConstraints);

        chordToneWeightField.setToolTipText("The amount of weight to give to chord tones (vs. scale or color tones).");
        chordToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
        chordToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
        chordToneWeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordToneWeightFieldActionPerformed(evt);
            }
        });
        chordToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                chordToneWeightFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(chordToneWeightField, gridBagConstraints);

        colorToneWeightField.setToolTipText("The amount of weight to give to color tones (vs. chord or scale tones).");
        colorToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
        colorToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
        colorToneWeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToneWeightFieldActionPerformed(evt);
            }
        });
        colorToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                colorToneWeightFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(colorToneWeightField, gridBagConstraints);

        scaleToneWeightField.setToolTipText("The amount of weight to give to scale tones (vs. chord or color tones).");
        scaleToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
        scaleToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
        scaleToneWeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleToneWeightFieldActionPerformed(evt);
            }
        });
        scaleToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                scaleToneWeightFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(scaleToneWeightField, gridBagConstraints);

        chordToneDecayField.setToolTipText("Decrease chord tone probability by this amount for each tone.");
        chordToneDecayField.setMinimumSize(new java.awt.Dimension(40, 24));
        chordToneDecayField.setPreferredSize(new java.awt.Dimension(40, 24));
        chordToneDecayField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordToneDecayFieldActionPerformed(evt);
            }
        });
        chordToneDecayField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                chordToneDecayFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(chordToneDecayField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 0.1;
        lickGenPanel.add(toneProbabilityPanel, gridBagConstraints);

        lickGradeButtonsPanel.setBackground(new java.awt.Color(218, 215, 215));
        lickGradeButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lick Saving and Grading", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickGradeButtonsPanel.setMaximumSize(new java.awt.Dimension(500, 300));
        lickGradeButtonsPanel.setMinimumSize(new java.awt.Dimension(300, 175));
        lickGradeButtonsPanel.setPreferredSize(new java.awt.Dimension(300, 175));
        lickGradeButtonsPanel.setLayout(new java.awt.GridBagLayout());

        gradeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gradeLabel.setText("Save Lick with Grade:");
        gradeLabel.setToolTipText("Provides a grade for the quality of lick. Used in machine learning experiments.");
        gradeLabel.setMaximumSize(new java.awt.Dimension(130, 25));
        gradeLabel.setMinimumSize(new java.awt.Dimension(110, 25));
        gradeLabel.setPreferredSize(new java.awt.Dimension(110, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        lickGradeButtonsPanel.add(gradeLabel, gridBagConstraints);

        saveLickTF.setText("<Generated Lick>");
        saveLickTF.setMinimumSize(new java.awt.Dimension(250, 25));
        saveLickTF.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        lickGradeButtonsPanel.add(saveLickTF, gridBagConstraints);

        saveLickWithLabelLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saveLickWithLabelLabel.setText("Save Lick with Label:");
        saveLickWithLabelLabel.setToolTipText("The label that will be used when graded licks are saved.");
        saveLickWithLabelLabel.setMinimumSize(new java.awt.Dimension(110, 14));
        saveLickWithLabelLabel.setPreferredSize(new java.awt.Dimension(110, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 0);
        lickGradeButtonsPanel.add(saveLickWithLabelLabel, gridBagConstraints);

        grade1Btn.setText("1");
        grade1Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade1Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade1Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade1BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade1Btn, gridBagConstraints);

        grade2Btn.setText("2");
        grade2Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade2Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade2Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade2Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade2BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade2Btn, gridBagConstraints);

        grade3Btn.setText("3");
        grade3Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade3Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade3Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade3Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade3BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade3Btn, gridBagConstraints);

        grade4Btn.setText("4");
        grade4Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade4Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade4Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade4Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade4BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade4Btn, gridBagConstraints);

        grade5Btn.setText("5");
        grade5Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade5Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade5Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade5Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade5BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade5Btn, gridBagConstraints);

        grade6Btn.setText("6");
        grade6Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade6Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade6Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade6Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade6BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade6Btn, gridBagConstraints);

        grade7Btn.setText("7");
        grade7Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade7Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade7Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade7Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade7BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade7Btn, gridBagConstraints);

        grade8Btn.setText("8");
        grade8Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade8Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade8Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade8Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade8BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade8Btn, gridBagConstraints);

        grade9Btn.setText("9");
        grade9Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade9Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade9Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade9Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade9BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade9Btn, gridBagConstraints);

        grade10Btn.setText("10");
        grade10Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade10Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade10Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade10Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade10BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade10Btn, gridBagConstraints);

        gradeBadBtn.setText("Bad");
        gradeBadBtn.setToolTipText("Grade for a bad jazz lick.");
        gradeBadBtn.setMaximumSize(new java.awt.Dimension(70, 29));
        gradeBadBtn.setMinimumSize(new java.awt.Dimension(70, 29));
        gradeBadBtn.setPreferredSize(new java.awt.Dimension(70, 29));
        gradeBadBtn.setVisible(false);
        gradeBadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeBadBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        lickGradeButtonsPanel.add(gradeBadBtn, gridBagConstraints);

        gradeAverageBtn.setText("Average");
        gradeAverageBtn.setToolTipText("Grade for an average jazz lick.");
        gradeAverageBtn.setVisible(false);
        gradeAverageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeAverageBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        lickGradeButtonsPanel.add(gradeAverageBtn, gridBagConstraints);

        gradeGoodBtn.setText("Good");
        gradeGoodBtn.setToolTipText("Grade for a good jazz lick.");
        gradeGoodBtn.setVisible(false);
        gradeGoodBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeGoodBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        lickGradeButtonsPanel.add(gradeGoodBtn, gridBagConstraints);

        continuallyGenerateCheckBox.setSelected(true);
        continuallyGenerateCheckBox.setToolTipText("After grading, continually generate new licks.");
        continuallyGenerateCheckBox.setLabel("Continuous");
        continuallyGenerateCheckBox.setMaximumSize(new java.awt.Dimension(170, 23));
        continuallyGenerateCheckBox.setMinimumSize(new java.awt.Dimension(170, 23));
        continuallyGenerateCheckBox.setPreferredSize(new java.awt.Dimension(170, 23));
        continuallyGenerateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continuallyGenerateCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        lickGradeButtonsPanel.add(continuallyGenerateCheckBox, gridBagConstraints);

        styleRecognitionButton.setText("Prepare Critics");
        styleRecognitionButton.setToolTipText("Attempts to guess the musician of the selection based off parellel trained networks.");
        styleRecognitionButton.setMaximumSize(new java.awt.Dimension(150, 29));
        styleRecognitionButton.setMinimumSize(new java.awt.Dimension(150, 29));
        styleRecognitionButton.setPreferredSize(new java.awt.Dimension(150, 29));
        styleRecognitionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleRecognitionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        lickGradeButtonsPanel.add(styleRecognitionButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(lickGradeButtonsPanel, gridBagConstraints);

        ProbFillClearPanel.setBackground(new java.awt.Color(218, 215, 215));
        ProbFillClearPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Probabilities Fill and Clear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        ProbFillClearPanel.setMinimumSize(new java.awt.Dimension(300, 67));
        ProbFillClearPanel.setPreferredSize(new java.awt.Dimension(300, 67));
        ProbFillClearPanel.setLayout(new java.awt.GridBagLayout());

        clearProbsButton.setToolTipText("Clear all pitch probabilities.");
        clearProbsButton.setLabel("Clear All Probabilities");
        clearProbsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearProbsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        ProbFillClearPanel.add(clearProbsButton, gridBagConstraints);

        FillProbsButton.setText("Fill");
        FillProbsButton.setToolTipText("Fill pitch probabilities from chords.\n");
        FillProbsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FillProbsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        ProbFillClearPanel.add(FillProbsButton, gridBagConstraints);

        autoFillCheckBox.setSelected(true);
        autoFillCheckBox.setText("Auto-Fill");
        autoFillCheckBox.setMinimumSize(new java.awt.Dimension(89, 100));
        autoFillCheckBox.setPreferredSize(new java.awt.Dimension(89, 100));
        autoFillCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoFillCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        ProbFillClearPanel.add(autoFillCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.1;
        lickGenPanel.add(ProbFillClearPanel, gridBagConstraints);

        soloCorrectionPanel.setBackground(new java.awt.Color(218, 215, 215));
        soloCorrectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Critic Options (Using Neural Network)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        soloCorrectionPanel.setMinimumSize(new java.awt.Dimension(900, 160));
        soloCorrectionPanel.setPreferredSize(new java.awt.Dimension(900, 160));
        soloCorrectionPanel.setLayout(new java.awt.GridBagLayout());

        offsetByMeasureGradeSoloButton.setText("Offset By Measure");
        offsetByMeasureGradeSoloButton.setToolTipText("Moves the selection one measure forward. To be used with automated correction if there is an odd number of measures.");
        offsetByMeasureGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offsetByMeasureGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(offsetByMeasureGradeSoloButton, gridBagConstraints);

        forwardGradeSoloButton.setText("Step Forward");
        forwardGradeSoloButton.setToolTipText("Move the selection two measures forward.");
        forwardGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(forwardGradeSoloButton, gridBagConstraints);

        backwardGradeSoloButton.setText("Step Backward");
        backwardGradeSoloButton.setToolTipText("Move the selection two measures back.");
        backwardGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(backwardGradeSoloButton, gridBagConstraints);

        resetSelectionButton.setText("Reset Selection");
        resetSelectionButton.setToolTipText("Undo a change.");
        resetSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(resetSelectionButton, gridBagConstraints);

        gradeAllMeasuresButton.setText("Correct All");
        gradeAllMeasuresButton.setToolTipText("Moves two measures at a time, correcting licks if the correct grade is insufficient.");
        gradeAllMeasuresButton.setMaximumSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.setMinimumSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.setPreferredSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeAllMeasuresButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(gradeAllMeasuresButton, gridBagConstraints);

        regenerateLickForSoloButton.setText("Generate Better Lick");
        regenerateLickForSoloButton.setToolTipText("Generate a lick that passes through the filter, with a grade that is high enough..");
        regenerateLickForSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenerateLickForSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(regenerateLickForSoloButton, gridBagConstraints);

        gradeLickFromStaveButton.setText("Grade Selected Lick");
        gradeLickFromStaveButton.setToolTipText("Use the critic to grade the current two measure selection.");
        gradeLickFromStaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeLickFromStaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(gradeLickFromStaveButton, gridBagConstraints);

        lickFromStaveGradeTextField.setEditable(false);
        lickFromStaveGradeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lickFromStaveGradeTextField.setText("Grade");
        lickFromStaveGradeTextField.setToolTipText("Grade from the critic for the current lick.");
        lickFromStaveGradeTextField.setMinimumSize(new java.awt.Dimension(156, 27));
        lickFromStaveGradeTextField.setPreferredSize(new java.awt.Dimension(156, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(lickFromStaveGradeTextField, gridBagConstraints);

        useCriticCheckBox.setText("Use Critic");
        useCriticCheckBox.setToolTipText("Filter lick generation with a trained network.");
        useCriticCheckBox.setMaximumSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.setMinimumSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.setPreferredSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                useCriticCheckBoxMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(useCriticCheckBox, gridBagConstraints);

        criticGradeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        criticGradeTextField.setText("Grade");
        criticGradeTextField.setToolTipText("Lowest grade acceptable by the filter.");
        criticGradeTextField.setEnabled(false);
        criticGradeTextField.setMinimumSize(new java.awt.Dimension(60, 24));
        criticGradeTextField.setPreferredSize(new java.awt.Dimension(60, 24));
        criticGradeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                criticGradeTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                criticGradeTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(criticGradeTextField, gridBagConstraints);

        counterForCriticTextField.setEditable(false);
        counterForCriticTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        counterForCriticTextField.setText("Counter");
        counterForCriticTextField.setToolTipText("Counter for how many times the critic generates a lick.");
        counterForCriticTextField.setEnabled(false);
        counterForCriticTextField.setMinimumSize(new java.awt.Dimension(80, 24));
        counterForCriticTextField.setName(""); // NOI18N
        counterForCriticTextField.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(counterForCriticTextField, gridBagConstraints);

        criticGradeLabel.setText("Grade");
        criticGradeLabel.setToolTipText("Lowest grade acceptable by the filter.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        soloCorrectionPanel.add(criticGradeLabel, gridBagConstraints);

        counterForCriticLabel.setText("Counter");
        counterForCriticLabel.setToolTipText("Counter for how many times the critic generates a lick.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        soloCorrectionPanel.add(counterForCriticLabel, gridBagConstraints);

        loadRandomGrammarButton.setText("Load Random");
        loadRandomGrammarButton.setToolTipText("Loads the random grammar for lick generation.");
        loadRandomGrammarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadRandomGrammarButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(loadRandomGrammarButton, gridBagConstraints);

        soloCorrectionPanel.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(soloCorrectionPanel, gridBagConstraints);

        pitchProbabilitiesPanel.setBackground(new java.awt.Color(218, 215, 215));
        pitchProbabilitiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Probabilities by Chord", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pitchProbabilitiesPanel.setMinimumSize(new java.awt.Dimension(950, 200));
        pitchProbabilitiesPanel.setPreferredSize(new java.awt.Dimension(950, 200));
        pitchProbabilitiesPanel.setLayout(new java.awt.GridBagLayout());

        chordProbPanel.setBackground(new java.awt.Color(218, 215, 215));
        chordProbPanel.setMinimumSize(new java.awt.Dimension(800, 50));
        chordProbPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        chordProbPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pitchProbabilitiesPanel.add(chordProbPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        lickGenPanel.add(pitchProbabilitiesPanel, gridBagConstraints);

        generatorPane.addTab("Melody Generator", lickGenPanel);

        grammarLearningPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grammar Learning", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        grammarLearningPanel.setMinimumSize(new java.awt.Dimension(500, 300));
        grammarLearningPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        grammarLearningPanel.setLayout(new java.awt.GridBagLayout());

        finalLabel.setBackground(Color.green
        );
        finalLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        finalLabel.setText("<html>You can try your grammar at generation immediately without further loading, on the current or any other leadsheet,<br>however it will not appear in the main window until you restart the program.</html>");
        finalLabel.setMaximumSize(new java.awt.Dimension(400, 9999));
        finalLabel.setMinimumSize(new java.awt.Dimension(400, 40));
        finalLabel.setPreferredSize(new java.awt.Dimension(400, 40));
        finalLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(finalLabel, gridBagConstraints);

        topGrammarLearningPanel.setLayout(new java.awt.GridBagLayout());

        learningStep0Label.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        learningStep0Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        learningStep0Label.setText("<html>Following these steps will learn a new grammar from a corpus of solos as a folder of leadsheets.  <br>The new grammar is built based on either an empty grammar or an existing grammar. <br>Click the rectangular buttons below from top to bottom.</html>  ");
        learningStep0Label.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        learningStep0Label.setMaximumSize(new java.awt.Dimension(2147483647, 60));
        learningStep0Label.setMinimumSize(new java.awt.Dimension(700, 60));
        learningStep0Label.setOpaque(true);
        learningStep0Label.setPreferredSize(new java.awt.Dimension(700, 60));
        learningStep0Label.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        topGrammarLearningPanel.add(learningStep0Label, gridBagConstraints);

        learningBaseButtonGroup.add(emptyBaseLearningButton);
        emptyBaseLearningButton.setSelected(true);
        emptyBaseLearningButton.setText("Use _empty.grammar as a base.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 0.5;
        topGrammarLearningPanel.add(emptyBaseLearningButton, gridBagConstraints);

        learningBaseButtonGroup.add(selectBaseLearningButton);
        selectBaseLearningButton.setLabel("Select grammar to be used as base.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 0.5;
        topGrammarLearningPanel.add(selectBaseLearningButton, gridBagConstraints);

        learningBaseButtonGroup.add(emptyMotifBaseLearningButton);
        emptyMotifBaseLearningButton.setText("Use _emptyMotif.grammar as a base.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 0.5;
        topGrammarLearningPanel.add(emptyMotifBaseLearningButton, gridBagConstraints);

        grammarLearningPanel.add(topGrammarLearningPanel, new java.awt.GridBagConstraints());

        windowParametersPanel.setBackground(new java.awt.Color(255, 204, 102));
        windowParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 2: (Optional) Set the parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        windowParametersPanel.setMinimumSize(new java.awt.Dimension(500, 200));
        windowParametersPanel.setPreferredSize(new java.awt.Dimension(1272, 200));
        windowParametersPanel.setLayout(new java.awt.GridBagLayout());

        windowSizeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        windowSizeLabel.setText("Window Size (beats)");
        windowSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        windowSizeLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        windowSizeLabel.setMinimumSize(new java.awt.Dimension(225, 30));
        windowSizeLabel.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSizeLabel, gridBagConstraints);

        windowSlideLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSlideLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        windowSlideLabel.setText("Window Slide (beats)");
        windowSlideLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        windowSlideLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        windowSlideLabel.setMinimumSize(new java.awt.Dimension(225, 30));
        windowSlideLabel.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSlideLabel, gridBagConstraints);

        numClusterRepsLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        numClusterRepsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        numClusterRepsLabel.setText("Number of Representatives per Cluster");
        numClusterRepsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        numClusterRepsLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        numClusterRepsLabel.setMinimumSize(new java.awt.Dimension(350, 30));
        numClusterRepsLabel.setPreferredSize(new java.awt.Dimension(350, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(numClusterRepsLabel, gridBagConstraints);

        windowSizeField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSizeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        windowSizeField.setText("4");
        windowSizeField.setToolTipText("The number of beats for the size of the window");
        windowSizeField.setMaximumSize(null);
        windowSizeField.setMinimumSize(new java.awt.Dimension(90, 30));
        windowSizeField.setPreferredSize(new java.awt.Dimension(90, 30));
        windowSizeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                windowSizeFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSizeField, gridBagConstraints);

        windowSlideField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSlideField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        windowSlideField.setText("4");
        windowSlideField.setToolTipText("The number of beats to slide window by");
        windowSlideField.setMaximumSize(null);
        windowSlideField.setMinimumSize(new java.awt.Dimension(90, 30));
        windowSlideField.setPreferredSize(new java.awt.Dimension(90, 30));
        windowSlideField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                windowSlideFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSlideField, gridBagConstraints);

        useRelativeWindowsCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useRelativeWindowsCheckbox.setSelected(true);
        useRelativeWindowsCheckbox.setText("Use relative pitches for windows");
        useRelativeWindowsCheckbox.setToolTipText("Make productions use relative pitches");
        useRelativeWindowsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useRelativeWindowsCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useRelativeWindowsCheckbox.setMinimumSize(new java.awt.Dimension(350, 30));
        useRelativeWindowsCheckbox.setPreferredSize(new java.awt.Dimension(400, 30));
        useRelativeWindowsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useRelativeWindowsCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useRelativeWindowsCheckbox, gridBagConstraints);

        useBricksCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useBricksCheckbox.setSelected(true);
        useBricksCheckbox.setText("Use Bricks");
        useBricksCheckbox.setToolTipText("Create productions based on bricks");
        useBricksCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useBricksCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useBricksCheckbox.setMinimumSize(new java.awt.Dimension(150, 30));
        useBricksCheckbox.setPreferredSize(new java.awt.Dimension(150, 30));
        useBricksCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useBricksCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useBricksCheckbox, gridBagConstraints);

        numClusterRepsField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        numClusterRepsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        numClusterRepsField.setText("12");
        numClusterRepsField.setToolTipText("The number of beats for the size of the window");
        numClusterRepsField.setMaximumSize(null);
        numClusterRepsField.setMinimumSize(new java.awt.Dimension(100, 30));
        numClusterRepsField.setPreferredSize(new java.awt.Dimension(60, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(numClusterRepsField, gridBagConstraints);

        useMarkovCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useMarkovCheckbox.setSelected(true);
        useMarkovCheckbox.setText("Use Markov (ordered connection of phrases)");
        useMarkovCheckbox.setToolTipText("Use Markov chains when adding productions to Grammar");
        useMarkovCheckbox.setBorder(null);
        useMarkovCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useMarkovCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useMarkovCheckbox.setMinimumSize(new java.awt.Dimension(350, 30));
        useMarkovCheckbox.setPreferredSize(new java.awt.Dimension(400, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useMarkovCheckbox, gridBagConstraints);

        MarkovLengthField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        MarkovLengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        MarkovLengthField.setText("4");
        MarkovLengthField.setToolTipText("The number of previous states on which the Markov chain depends.");
        MarkovLengthField.setMaximumSize(new java.awt.Dimension(9999, 9999));
        MarkovLengthField.setMinimumSize(new java.awt.Dimension(100, 30));
        MarkovLengthField.setPreferredSize(new java.awt.Dimension(60, 30));
        MarkovLengthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MarkovLengthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        windowParametersPanel.add(MarkovLengthField, gridBagConstraints);

        markovChainLengthLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        markovChainLengthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        markovChainLengthLabel.setText("Markov chain length");
        markovChainLengthLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        markovChainLengthLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        markovChainLengthLabel.setMinimumSize(new java.awt.Dimension(350, 30));
        markovChainLengthLabel.setPreferredSize(new java.awt.Dimension(350, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(markovChainLengthLabel, gridBagConstraints);

        useWindowsCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useWindowsCheckbox.setSelected(true);
        useWindowsCheckbox.setText("Use Windows");
        useWindowsCheckbox.setToolTipText("Create productions based on bricks");
        useWindowsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useWindowsCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useWindowsCheckbox.setMinimumSize(new java.awt.Dimension(150, 30));
        useWindowsCheckbox.setPreferredSize(new java.awt.Dimension(150, 30));
        useWindowsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useWindowsCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useWindowsCheckbox, gridBagConstraints);

        useRelativeBricksCheckbox.setBackground(new java.awt.Color(255, 204, 102));
        useRelativeBricksCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useRelativeBricksCheckbox.setSelected(true);
        useRelativeBricksCheckbox.setText("Use relative pitches for bricks");
        useRelativeBricksCheckbox.setToolTipText("Make productions use relative pitches");
        useRelativeBricksCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useRelativeBricksCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useRelativeBricksCheckbox.setMinimumSize(new java.awt.Dimension(350, 30));
        useRelativeBricksCheckbox.setPreferredSize(new java.awt.Dimension(400, 30));
        useRelativeBricksCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useRelativeBricksCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useRelativeBricksCheckbox, gridBagConstraints);

        useAbstractBricksCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useAbstractBricksCheckbox.setSelected(true);
        useAbstractBricksCheckbox.setText("Use abstract pitches for bricks");
        useAbstractBricksCheckbox.setToolTipText("Make productions use relative pitches");
        useAbstractBricksCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useAbstractBricksCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useAbstractBricksCheckbox.setMinimumSize(new java.awt.Dimension(350, 30));
        useAbstractBricksCheckbox.setPreferredSize(new java.awt.Dimension(400, 30));
        useAbstractBricksCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAbstractBricksCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useAbstractBricksCheckbox, gridBagConstraints);

        useAbstractWindowsCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useAbstractWindowsCheckbox.setSelected(true);
        useAbstractWindowsCheckbox.setText("Use abstract pitches for windows");
        useAbstractWindowsCheckbox.setToolTipText("Make productions use relative pitches");
        useAbstractWindowsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useAbstractWindowsCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useAbstractWindowsCheckbox.setMinimumSize(new java.awt.Dimension(350, 30));
        useAbstractWindowsCheckbox.setPreferredSize(new java.awt.Dimension(400, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(useAbstractWindowsCheckbox, gridBagConstraints);

        userRhythmCheckBox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        userRhythmCheckBox.setText("Use Saved Melodies");
        userRhythmCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        userRhythmCheckBox.setPreferredSize(new java.awt.Dimension(400, 30));
        userRhythmCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userRhythmCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(userRhythmCheckBox, gridBagConstraints);

        rhythmClusterCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        rhythmClusterCheckbox.setText("Generate Rhythm Cluster");
        rhythmClusterCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rhythmClusterCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rhythmClusterCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        windowParametersPanel.add(rhythmClusterCheckbox, gridBagConstraints);

        motifParametersPanel.setBackground(new java.awt.Color(255, 204, 102));
        motifParametersPanel.setOpaque(false);
        motifParametersPanel.setLayout(new java.awt.GridBagLayout());

        useMotifsCheckbox.setSelected(true);
        useMotifsCheckbox.setText("Use Motifs");
        useMotifsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useMotifsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useMotifsCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        motifParametersPanel.add(useMotifsCheckbox, gridBagConstraints);
        useMotifsCheckbox.getAccessibleContext().setAccessibleName("Use Motifs for grammar");
        useMotifsCheckbox.getAccessibleContext().setAccessibleDescription("Incorporate motifs into productions");

        motifnessSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        motifnessSlider.setMinimumSize(new java.awt.Dimension(100, 48));
        motifnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                motifnessSliderStateChanged(evt);
            }
        });
        motifnessSlider.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                motifnessSliderCaretPositionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        motifParametersPanel.add(motifnessSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        windowParametersPanel.add(motifParametersPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.02;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        grammarLearningPanel.add(windowParametersPanel, gridBagConstraints);

        saveGrammarAsButton.setBackground(new java.awt.Color(255, 255, 0));
        saveGrammarAsButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        saveGrammarAsButton.setText("<html>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; This step will use <b>Save as . . .</b> in the Grammar menu to save your new grammar under a new name, <br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; in case you want to preserve the old grammar. <br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; It will also ask you to save your leadsheet if you need it, as the leadsheet window will be used as a workspace.</html>  ");
        saveGrammarAsButton.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 1: Start the Process", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        saveGrammarAsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveGrammarAsButton.setMaximumSize(new java.awt.Dimension(9999, 9999));
        saveGrammarAsButton.setOpaque(true);
        saveGrammarAsButton.setPreferredSize(new java.awt.Dimension(173, 60));
        saveGrammarAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGrammarAsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 5);
        grammarLearningPanel.add(saveGrammarAsButton, gridBagConstraints);

        openCorpusBtn.setBackground(new java.awt.Color(255, 153, 0));
        openCorpusBtn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        openCorpusBtn.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Each solo is a leadsheet file.\n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Note: Selecting any leadsheet file in a folder is equivalent to selecting the entire folder. </b>\n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end.  \n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>The process is complete when the last chorus of that leadsheet appears</b>.</html>");
        openCorpusBtn.setActionCommand("<html>Each solo is a leadsheet file.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Selecting any file any a folder is equivalent to selecting the entire folder.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end. The process is over when the last chorus appears.</html>");
        openCorpusBtn.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 3: Select a corpus of solos from which to learn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        openCorpusBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        openCorpusBtn.setMaximumSize(new java.awt.Dimension(9999, 9999));
        openCorpusBtn.setMinimumSize(new java.awt.Dimension(240, 120));
        openCorpusBtn.setOpaque(true);
        openCorpusBtn.setPreferredSize(new java.awt.Dimension(240, 120));
        openCorpusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCorpusBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        grammarLearningPanel.add(openCorpusBtn, gridBagConstraints);

        toGrammarBtn.setBackground(new java.awt.Color(204, 204, 255));
        toGrammarBtn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        toGrammarBtn.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;There are two <b>other alternatives</b> at this point: <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a, Quit by closing the window, with no changes. <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. Return to Step 4 and learn from other corpuses of solos. </html>");
        toGrammarBtn.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 4: Click this button to create and save the grammar and Soloist file", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        toGrammarBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toGrammarBtn.setMaximumSize(new java.awt.Dimension(9999, 100));
        toGrammarBtn.setMinimumSize(new java.awt.Dimension(240, 100));
        toGrammarBtn.setOpaque(true);
        toGrammarBtn.setPreferredSize(new java.awt.Dimension(240, 100));
        toGrammarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toGrammarBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        grammarLearningPanel.add(toGrammarBtn, gridBagConstraints);

        testGeneration.setBackground(new java.awt.Color(130, 217, 151));
        testGeneration.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        testGeneration.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Press this button to create solos with your learned grammar.</html>");
        testGeneration.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 5:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        testGeneration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        testGeneration.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        testGeneration.setMaximumSize(new java.awt.Dimension(9999, 9999));
        testGeneration.setMinimumSize(new java.awt.Dimension(240, 50));
        testGeneration.setOpaque(true);
        testGeneration.setPreferredSize(new java.awt.Dimension(240, 50));
        testGeneration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testGenerationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        grammarLearningPanel.add(testGeneration, gridBagConstraints);

        rhythmClusterPanel.setBackground(new java.awt.Color(255, 255, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        grammarLearningPanel.add(rhythmClusterPanel, gridBagConstraints);

        generatorPane.addTab("Grammar Learning", grammarLearningPanel);

        neuralNetworkPanel.setLayout(new java.awt.GridBagLayout());

        nnetOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetOutputPanel.setMinimumSize(new java.awt.Dimension(850, 200));
        nnetOutputPanel.setPreferredSize(new java.awt.Dimension(850, 200));
        nnetOutputPanel.setLayout(new java.awt.GridBagLayout());

        nnetScrollPane.setBorder(null);
        nnetScrollPane.setMinimumSize(new java.awt.Dimension(223, 180));
        nnetScrollPane.setPreferredSize(new java.awt.Dimension(223, 180));

        nnetOutputTextField.setColumns(20);
        nnetOutputTextField.setLineWrap(true);
        nnetOutputTextField.setRows(20000);
        nnetOutputTextField.setText("To generate a weight file:\n-Select training file (File name will end with \".training.data\")\n-Weight file name with automatically be set\n--Weight file will save to personal settings folder, in vocab\n-Change the epoch limit if desired\n-Change the default learning rate if desired\n-Change the default MSE goal if desired\n-Change the default mode if desired\n-In the table to the right:\n--Set the layer size for each layer\n---Input (first) layer size determinted at runtime from input size\n---The last layer, for output, should be of size 1\n--Set the function for each layer\n--Reorder rows as desired. Empty rows will be ignored.\n-Press \"Generate Weight File\"\n\nTo load network:\n-Select the weight file, from the vocab folder, under \"Weight File\"\n-Press \"Load Weight\"\n-Network will be initialized per leadsheet\n\nTo clear a weight file:\n-Select the weight file, from the vocab folder, under \"Weight File\"\n-Press \"Clear Weight File\"\n\n***There is a sample weight file in impro-visor-version-X.xx-files/vocab\n   for general use. The licks used to create it were subjectively graded,\n   and therefore may not reflect the preferences of the user.");
        nnetOutputTextField.setBorder(null);
        nnetOutputTextField.setMinimumSize(new java.awt.Dimension(800, 100));
        nnetScrollPane.setViewportView(nnetOutputTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        nnetOutputPanel.add(nnetScrollPane, gridBagConstraints);

        layerInfoScrollPane.setMinimumSize(new java.awt.Dimension(469, 402));

        layerDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1),  new Integer(64), "Logsig"},
                { new Integer(2),  new Integer(1), "Logsig"}
            },
            new String [] {
                "Layer Index", "Layer Size", "Layer Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 1
                    && rowIndex == layerDataTable.getRowCount() - 1)
                {
                    return false;
                }
                return canEdit [columnIndex];
            }
        });
        layerDataTable.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        layerDataTable.setMinimumSize(new java.awt.Dimension(150, 900));
        layerDataTable.setPreferredSize(new java.awt.Dimension(150, 900));
        layerDataTable.getTableHeader().setReorderingAllowed(false);
        layerDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                layerDataTableMouseClicked(evt);
            }
        });
        layerInfoScrollPane.setViewportView(layerDataTable);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Logsig");
        comboBox.addItem("Tansig");
        comboBox.addItem("Elliot");
        comboBox.addItem("Elliots");
        comboBox.addItem("Hardlim");
        comboBox.addItem("Hardlims");
        comboBox.addItem("Purelin");
        comboBox.addItem("Satlin");
        comboBox.addItem("Satlins");
        layerDataTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        nnetOutputPanel.add(layerInfoScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        neuralNetworkPanel.add(nnetOutputPanel, gridBagConstraints);

        nnetWeightGenerationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetWeightGenerationPanel.setMinimumSize(new java.awt.Dimension(300, 230));
        nnetWeightGenerationPanel.setPreferredSize(new java.awt.Dimension(300, 230));
        nnetWeightGenerationPanel.setLayout(new java.awt.GridBagLayout());

        generateWeightFileButton.setText("Generate Weight File");
        generateWeightFileButton.setToolTipText("Generate a weight file from the input to the neural network.");
        generateWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(generateWeightFileButton, gridBagConstraints);

        getNetworkStatsButton.setText("Get Network Statistics");
        getNetworkStatsButton.setToolTipText("Show network statistics.");
        getNetworkStatsButton.setMaximumSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.setMinimumSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.setPreferredSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getNetworkStatsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(getNetworkStatsButton, gridBagConstraints);

        clearWeightFileButton.setText("Delete Weight File");
        clearWeightFileButton.setToolTipText("Delete the selected weight file.");
        clearWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(clearWeightFileButton, gridBagConstraints);

        loadWeightFileButton.setText("Load Weight File");
        loadWeightFileButton.setToolTipText("Load the selected weight file.");
        loadWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(loadWeightFileButton, gridBagConstraints);

        resetNnetInstructionsButton.setText("Reset Instructions");
        resetNnetInstructionsButton.setToolTipText("Reset the instructions for how to use the neural network.");
        resetNnetInstructionsButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetNnetInstructionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetNnetInstructionsButton, gridBagConstraints);

        resetDefaultValuesButton.setText("Reset Default Values");
        resetDefaultValuesButton.setToolTipText("Reset all default values for all fields.");
        resetDefaultValuesButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDefaultValuesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetDefaultValuesButton, gridBagConstraints);

        resetNetworkButton.setText("Reset Network");
        resetNetworkButton.setToolTipText("Reset the network to load a new network.");
        resetNetworkButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetNetworkButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetNetworkButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        neuralNetworkPanel.add(nnetWeightGenerationPanel, gridBagConstraints);

        nnetParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetParametersPanel.setMinimumSize(new java.awt.Dimension(520, 220));
        nnetParametersPanel.setPreferredSize(new java.awt.Dimension(520, 220));
        nnetParametersPanel.setLayout(new java.awt.GridBagLayout());

        trainingFileButton.setText("Training File");
        trainingFileButton.setToolTipText("Select the training file for the network.");
        trainingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainingFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(trainingFileButton, gridBagConstraints);

        trainingFileTextField.setEditable(false);
        trainingFileTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        trainingFileTextField.setPreferredSize(new java.awt.Dimension(115, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(trainingFileTextField, gridBagConstraints);

        epochLimitLabel.setText("Epoch Limit");
        epochLimitLabel.setToolTipText("Limit the amount of iterations during training.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(epochLimitLabel, gridBagConstraints);

        epochLimitTextField.setText("20000");
        epochLimitTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        epochLimitTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(epochLimitTextField, gridBagConstraints);

        learningRateLabel.setText("Learning Rate");
        learningRateLabel.setToolTipText("Set the learning rate.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(learningRateLabel, gridBagConstraints);

        learningRateTextField.setText("0.01");
        learningRateTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        learningRateTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(learningRateTextField, gridBagConstraints);

        mseGoalLabel.setText("MSE Goal");
        mseGoalLabel.setToolTipText("MSE goal for training. Training will stop once this goal is reached.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(mseGoalLabel, gridBagConstraints);

        mseGoalTextField.setText("0.01");
        mseGoalTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        mseGoalTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(mseGoalTextField, gridBagConstraints);

        modeLabel.setText("Mode");
        modeLabel.setToolTipText("Select the mode for the training from the dropdown menu.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(modeLabel, gridBagConstraints);

        modeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "On-Line", "Batch", "RProp" }));
        modeComboBox.setSelectedIndex(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(modeComboBox, gridBagConstraints);

        weightFileTextField.setEditable(false);
        weightFileTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        weightFileTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(weightFileTextField, gridBagConstraints);

        numberOfLayersLabel.setText("Num Layers");
        numberOfLayersLabel.setToolTipText("Number of layers of the network. Automatically set from the table.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(numberOfLayersLabel, gridBagConstraints);

        numberOfLayersTextField.setEditable(false);
        numberOfLayersTextField.setText("2");
        numberOfLayersTextField.setMinimumSize(new java.awt.Dimension(68, 27));
        numberOfLayersTextField.setPreferredSize(new java.awt.Dimension(68, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(numberOfLayersTextField, gridBagConstraints);

        addLayerToTableButton.setText("Add Layer");
        addLayerToTableButton.setToolTipText("Add a layer to the end of the network. If a layer is selected, add it below that one.");
        addLayerToTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLayerToTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(addLayerToTableButton, gridBagConstraints);

        removeLayerFromTableButton.setText("Remove Layer");
        removeLayerFromTableButton.setToolTipText("Delete the selected layer.");
        removeLayerFromTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLayerFromTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(removeLayerFromTableButton, gridBagConstraints);

        moveLayerUpTableButton.setText("Layer Up");
        moveLayerUpTableButton.setToolTipText("Move the selected layer up.");
        moveLayerUpTableButton.setMaximumSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.setMinimumSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.setPreferredSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLayerUpTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(moveLayerUpTableButton, gridBagConstraints);

        moveLayerDownTableButton.setText("Layer Down");
        moveLayerDownTableButton.setToolTipText("Move the selected layer down.");
        moveLayerDownTableButton.setMaximumSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.setMinimumSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.setPreferredSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLayerDownTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(moveLayerDownTableButton, gridBagConstraints);

        weightFileButton.setText("Weight File");
        weightFileButton.setToolTipText("Name automatically set from Training File. If you are only loading weights into the critic, use this.");
        weightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(weightFileButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        neuralNetworkPanel.add(nnetParametersPanel, gridBagConstraints);

        generatorPane.addTab("Neural Network Learning", neuralNetworkPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(generatorPane, gridBagConstraints);

        generatorMenuBar1.setMinimumSize(new java.awt.Dimension(115, 23));

        grammarMenu1.setMnemonic('G');
        grammarMenu1.setText("Grammar Options");
        grammarMenu1.setToolTipText("Edit or change the current grammar file.");
        grammarMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grammarMenu1ActionPerformed(evt);
            }
        });

        openGrammarMI1.setText("Load Grammar");
        openGrammarMI1.setToolTipText("Selects which grammar file to used.");
        openGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(openGrammarMI1);

        showLogMI1.setText("Show Log");
        showLogMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLogMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(showLogMI1);

        saveGrammarMI1.setText("Save Grammar As ...");
        saveGrammarMI1.setToolTipText("Saves the grammar file under a specified name.");
        saveGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(saveGrammarMI1);

        editGrammarMI1.setText("Edit Grammar");
        editGrammarMI1.setToolTipText("Edit the current grammar using a text editor.");
        editGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(editGrammarMI1);

        reloadGrammarMI1.setText("Reload Grammar");
        reloadGrammarMI1.setToolTipText("Reloads the grammar file.");
        reloadGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(reloadGrammarMI1);

        toCriticMI1.setText("Send Licks to Critic");
        toCriticMI1.setToolTipText("Copies licks in a special format for learning by critic (a separate tool).");
        toCriticMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toCriticMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(toCriticMI1);

        showCriticMI1.setText("Show Critic Exporter");
        showCriticMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCriticMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(showCriticMI1);

        useGrammarMI1.setSelected(true);
        useGrammarMI1.setText("Use Grammar");
        useGrammarMI1.setToolTipText("Indicates whether or not a grammar should be used in lick generation. Without this, generation will be governed only by probabilities set in the fields below.");
        useGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(useGrammarMI1);

        generatorMenuBar1.add(grammarMenu1);

        generatorWindowMenu1.setLabel("Window");
        generatorWindowMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                generatorWindowMenu1MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        closeWindowMI2.setMnemonic('C');
        closeWindowMI2.setText("Close Window");
        closeWindowMI2.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMI2ActionPerformed(evt);
            }
        });
        generatorWindowMenu1.add(closeWindowMI2);

        cascadeMI2.setMnemonic('A');
        cascadeMI2.setText("Cascade Windows");
        cascadeMI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMI2ActionPerformed(evt);
            }
        });
        generatorWindowMenu1.add(cascadeMI2);
        generatorWindowMenu1.add(windowMenuSeparator2);

        generatorMenuBar1.add(generatorWindowMenu1);

        setJMenuBar(generatorMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Get the pitch-class probabilities from array of text fields.
     *
     * @return pitch-class probabilities
     */
    public ArrayList<double[]> readProbs() {
        ArrayList<double[]> probs = new ArrayList<double[]>();

        for (int i = 0; i < lickPrefs.size(); ++i) {
            double[] p = new double[12];

            JTextField tf[] = lickPrefs.get(i);

            for (int j = 0; j < tf.length; ++j) {
                p[j] = Notate.quietDoubleFromTextField(tf[j], 0.0,
                        Double.POSITIVE_INFINITY, 0.0);
            }
            probs.add(p);
        }

        return probs;
    }

    /**
     * Make sure that the values in the probability fields are between 0.0 and
     * 1.0
     */
    public void verifyProbs() {

        for (int i = 0; i < lickPrefs.size(); ++i) {
            JTextField tf[] = lickPrefs.get(i);
            for (int j = 0; j < tf.length; ++j) {
                Notate.doubleFromTextField(tf[j], 0.0,
                        Double.POSITIVE_INFINITY, 1.0);
            }
        }
    }

    /**
     * Redraw the triage frame based on where we are and how much of the current
     * chord progression we're examining.
     */
    public void redrawTriage() {
        
        chordProbPanel.removeAll();

        GridBagLayout gbl = new GridBagLayout();

        JPanel panel = new JPanel(gbl);

        // We need to keep track of both the chords we've already looked at
        // and the old probability values.

        ArrayList<String> chordUsed = new ArrayList<String>();

        ArrayList<JTextField[]> oldProbs = (ArrayList<JTextField[]>) lickPrefs.clone();


        int start = notate.getCurrentSelectionStart();

        if (start == -1) {
            return;
        }

        int end = notate.getCurrentSelectionEnd(); // start + notate.getTotalSlots();


        // Add the locations of every chord change in the section that we're
        // examining.

        ChordPart chordPart = notate.getChordProg();

        ArrayList<Integer> chordChanges = new ArrayList<Integer>();

        chordChanges.add(start);


        int next = chordPart.getNextUniqueChordIndex(start);

        while (next < end && next != -1) {
            chordChanges.add(next);

            next = chordPart.getNextUniqueChordIndex(next);
        }

        // Clear out the old values.

        lickPrefs.clear();


        // Loop through every chord

        int numChords = chordChanges.size();

        for (int i = 0; i < numChords; ++i) {

            // If we've added stuff for this chord already, move on; otherwise,

            // add it to the list of chords that we've processed.

            Chord currentChord = chordPart.getCurrentChord(chordChanges.get(i));

            if (currentChord != null) {
                String currentChordName = currentChord.getName();

                if (chordUsed.contains(currentChordName)) {
                    continue;
                } else {
                    chordUsed.add(currentChordName);
                }

                // Add in a label specifing which chord these text boxes correspond to.

                GridBagConstraints labelConstraints = new GridBagConstraints();

                labelConstraints.gridx = 0;

                labelConstraints.gridwidth = 4;

                labelConstraints.gridy = (i * 3);

                labelConstraints.fill = GridBagConstraints.HORIZONTAL;

                labelConstraints.ipadx = 5;

                labelConstraints.insets = new Insets(5, 5, 5, 5);

                labelConstraints.weightx = 1.0;


                JLabel label = new JLabel(currentChordName + " probabilities:");

                panel.add(label, labelConstraints);



                // Create a new array of text boxes and note labels

                JTextField[] prefs = new JTextField[12];

                String[] notes = notate.getNoteLabels(chordChanges.get(i));

                // Since there are twelve chromatic pitches we need to consider,
                // loop through twelve times.

                for (int j = 0; j < 12; ++j) {
                    // First we need to draw the note labels; set up the drawing constraints

                    // for them.  They get added to every other row, just above the text

                    // boxes we're about to draw.

                    GridBagConstraints lbc = new GridBagConstraints();

                    lbc.anchor = GridBagConstraints.CENTER;

                    lbc.gridx = j;

                    lbc.gridy = (i * 3) + 1;

                    lbc.fill = GridBagConstraints.NONE;

                    lbc.ipadx = 15;

                    lbc.weightx = 1.0;



                    JLabel l = new JLabel(notes[j], JLabel.CENTER);

                    panel.add(l, lbc);


                    // Create the text field and set the value to the old value, if
                    // it exists.

                    prefs[j] = new JTextField(1);

                    prefs[j].setHorizontalAlignment(javax.swing.JTextField.TRAILING);

                    if (oldProbs == null || oldProbs.size() > i) {
                        prefs[j].setText(oldProbs.get(i)[j].getText());
                    } else {
                        prefs[j].setText("1.0");
                    }

                    prefs[j].setCaretPosition(0);



                    // Add event listeners to watch this field's input; we
                    // need to make sure that we don't allow bad strings to be
                    // input.

                    prefs[j].addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            verifyProbs();
                        }
                    });

                    prefs[j].addFocusListener(new java.awt.event.FocusAdapter() {
                        @Override
                        public void focusLost(java.awt.event.FocusEvent evt) {
                            verifyProbs();
                        }
                    });


                    // Add the new box just below its corresponding label.

                    GridBagConstraints gbc = new GridBagConstraints();

                    gbc.gridx = j;

                    gbc.gridy = (i * 3) + 2;

                    gbc.fill = GridBagConstraints.NONE;

                    gbc.ipadx = 25;

                    gbc.weightx = 1.0;

                    panel.add(prefs[j], gbc);
                }
                lickPrefs.add(prefs);
            }
        }

        JScrollPane sp = new JScrollPane(panel);

        sp.getVerticalScrollBar().setUnitIncrement(10);

        chordProbPanel.add(sp);

        chordProbPanel.setPreferredSize(new Dimension(600, 200));

        // We have to call validate before anything will appear on the screen.

        validate();

        // If we have auto-fill turned on, then calculate the new probabilities

        if (autoFill) {
            FillProbsButtonActionPerformed(null);
        } // Otherwise, we need to store the old ones and use those instead, but we need
        // to make sure that we don't try to write into text fields that aren't there.
        else {
            ArrayList<double[]> probs = new ArrayList<double[]>();

            for (int i = 0; i < lickPrefs.size(); ++i) {

                double[] p = new double[12];

                for (int j = 0; j < 12; ++j) {
                    p[j] = Notate.quietDoubleFromTextField(lickPrefs.get(i)[j], 0.0, Double.POSITIVE_INFINITY, 0.0);
                }
                probs.add(p);
            }
            lickgen.setProbs(probs);

        }
        // This causes the frame to be resized, which is annoying: generatorFrame.pack();
    }

    /**
     * Set the abstract melody field (formerly called "rhythm" field).
     *
     * @param string
     */
    public void setRhythmFieldText(String string) {
        rhythmField.setText(string);
        rhythmField.setCaretPosition(0);
        rhythmScrollPane.getViewport().setViewPosition(new Point(0, 0));
    }
    
    public void setRelativeFieldText(String string) {
        relativeField.setText(string);
        relativeField.setCaretPosition(0);
        relativeScrollPane.getViewport().setViewPosition(new Point(0, 0));
    }  

    /**
     * Interface to fillMelody in LickGen
     * @param beatValue
     * @param rhythmString
     * @param chordProg
     * @param start
     * @return 
     */
    public MelodyPart fillMelody(int beatValue,
                                 Polylist rhythmString,
                                 ChordPart chordProg,
                                 int start) 
      {
        MelodyPart result = lickgen.fillMelody(minPitch,
                maxPitch,
                minInterval,
                maxInterval,
                beatValue,
                leapProb,
                rhythmString,
                chordProg,
                start,
                avoidRepeats);

        return result;
    }

    private void playSelection() {
        notate.getCurrentStave().playSelection(false, notate.getLoopCount(), PlayScoreCommand.USEDRUMS, "LickGenFrame");
    }

    public void stopPlaying() {
        notate.stopPlaying();
    }

    private void verifyAndSaveTriageFields()
    {
        verifyTriageFields();
        notate.saveGrammar();  // Save when field changed!
    }
    /**
     * Make sure the user has entered acceptable values for each of the other
     * fields in the triage frame.
     */
    public void verifyTriageFields() {
        //notate.toCritic();

        minPitch = Notate.intFromTextField(minPitchField, LickGen.MIN_PITCH,
                maxPitch, minPitch);

        maxPitch = Notate.intFromTextField(maxPitchField, minPitch,
                LickGen.MAX_PITCH, maxPitch);

        minInterval = Notate.intFromTextField(minIntervalField,
                LickGen.MIN_INTERVAL_SIZE, maxInterval,
                minInterval);

        maxInterval = Notate.intFromTextField(maxIntervalField, minInterval,
                LickGen.MAX_INTERVAL_SIZE, maxInterval);

        minDuration = Notate.intFromTextField(minDurationField, maxDuration,
                LickGen.MIN_NOTE_DURATION, minDuration);

        maxDuration = Notate.intFromTextField(maxDurationField,
                LickGen.MAX_NOTE_DURATION, minDuration,
                maxDuration);

        restProb = Notate.doubleFromTextField(restProbField, 0.0, 1.0, restProb);

        leapProb = Notate.doubleFromTextField(leapProbField, 0.0, 1.0, leapProb);

        chordToneWeight = Notate.doubleFromTextField(chordToneWeightField, 0.0,
                Double.POSITIVE_INFINITY,
                chordToneWeight);

        scaleToneWeight = Notate.doubleFromTextField(scaleToneWeightField, 0.0,
                Double.POSITIVE_INFINITY,
                scaleToneWeight);

        colorToneWeight = Notate.doubleFromTextField(colorToneWeightField, 0.0,
                Double.POSITIVE_INFINITY,
                colorToneWeight);

        chordToneDecayRate = Notate.doubleFromTextField(chordToneDecayField, 0.0,
                Double.POSITIVE_INFINITY,
                chordToneDecayRate);
        
        saveTriageParameters();
        
        totalBeats = Notate.doubleFromTextField(totalBeatsField, 0.0,
                Double.POSITIVE_INFINITY, 0.0);
   
        totalBeats = Math.round(totalBeats);

        totalSlots = (int) (BEAT * totalBeats);

        notate.getCurrentStave().repaint();
    }

    public void resetTriageParameters(boolean menu) {
        try {
            minPitchField.setText(lickgen.getParameter(LickGen.MIN_PITCH_STRING));

            minPitch = Integer.parseInt(lickgen.getParameter(LickGen.MIN_PITCH_STRING));

            maxPitchField.setText(lickgen.getParameter(LickGen.MAX_PITCH_STRING));

            maxPitch = Integer.parseInt(lickgen.getParameter(LickGen.MAX_PITCH_STRING));

            minDurationField.setText(lickgen.getParameter(LickGen.MIN_DURATION));

            minDuration = Integer.parseInt(lickgen.getParameter(LickGen.MIN_DURATION));

            maxDurationField.setText(lickgen.getParameter(LickGen.MAX_DURATION));

            maxDuration = Integer.parseInt(lickgen.getParameter(LickGen.MAX_DURATION));

            minIntervalField.setText(lickgen.getParameter(LickGen.MIN_INTERVAL));

            minInterval = Integer.parseInt(lickgen.getParameter(LickGen.MIN_INTERVAL));

            maxIntervalField.setText(lickgen.getParameter(LickGen.MAX_INTERVAL));

            maxInterval = Integer.parseInt(lickgen.getParameter(LickGen.MAX_INTERVAL));

            restProbField.setText(lickgen.getParameter(LickGen.REST_PROB));

            restProb = Double.parseDouble(lickgen.getParameter(LickGen.REST_PROB));

            leapProbField.setText(lickgen.getParameter(LickGen.LEAP_PROB));

            leapProb = Double.parseDouble(lickgen.getParameter(LickGen.LEAP_PROB));

            chordToneWeightField.setText(lickgen.getParameter(LickGen.CHORD_TONE_WEIGHT));

            chordToneWeight = Double.parseDouble(lickgen.getParameter(
                    LickGen.CHORD_TONE_WEIGHT));

            colorToneWeightField.setText(lickgen.getParameter(LickGen.COLOR_TONE_WEIGHT));

            colorToneWeight = Double.parseDouble(lickgen.getParameter(
                    LickGen.COLOR_TONE_WEIGHT));

            scaleToneWeightField.setText(lickgen.getParameter(LickGen.SCALE_TONE_WEIGHT));

            scaleToneWeight = Double.parseDouble(lickgen.getParameter(
                    LickGen.SCALE_TONE_WEIGHT));

            chordToneDecayField.setText(lickgen.getParameter(LickGen.CHORD_TONE_DECAY));

            chordToneDecayRate = Double.parseDouble(lickgen.getParameter(
                    LickGen.CHORD_TONE_DECAY));

            autoFillCheckBox.setSelected(Boolean.parseBoolean(lickgen.getParameter(
                    LickGen.AUTO_FILL)));

            autoFill = Boolean.parseBoolean(lickgen.getParameter(LickGen.AUTO_FILL));

            rectify = Boolean.parseBoolean(lickgen.getParameter(LickGen.RECTIFY));

            rectifyCheckBox.setSelected(rectify);

            useGrammar = true; // Boolean.parseBoolean(lickgen.getParameter(LickGen.USE_GRAMMAR));

            useGrammarMI1.setSelected(useGrammar);
            useGrammarAction();

            avoidRepeats = Boolean.parseBoolean(lickgen.getParameter(
                    LickGen.AVOID_REPEATS));

            if (menu) {
                int rootIndex = ((DefaultComboBoxModel) rootComboBox.getModel()).getIndexOf(
                        lickgen.getParameter(LickGen.SCALE_ROOT));

                int scaleIndex = ((DefaultComboBoxModel) scaleComboBox.getModel()).getIndexOf(
                        lickgen.getParameter(LickGen.SCALE_TYPE));

                rootComboBox.setSelectedIndex(rootIndex);

                scaleComboBox.setSelectedIndex(scaleIndex);

                lickgen.setPreferredScale(lickgen.getParameter(LickGen.SCALE_ROOT),
                        lickgen.getParameter(LickGen.SCALE_TYPE));
            }
        } catch (Exception e) {
        }
    }
 /**@TODO figure out how to make the notes stop merging*/

    public void uncheckAvoidNotes(){
//        System.out.println("avoid repeats before update: "+avoidRepeats);
//        avoidRepeatsCheckbox.setSelected(false);
//        saveTriageParameters();
//        avoidRepeats = avoidRepeatsCheckbox.isSelected();
//        System.out.println("avoid repeats before update: "+avoidRepeats);
    }


    /**
     * Builds an association list with all of the parameters of the grammar. On
     * saving a file, additional parameters may be added within Lickgen.
     */
    public void saveTriageParameters() {
        lickgen.setParameter(LickGen.MIN_PITCH_STRING, minPitch);

        lickgen.setParameter(LickGen.MAX_PITCH_STRING, maxPitch);

        lickgen.setParameter(LickGen.MIN_DURATION, minDuration);

        lickgen.setParameter(LickGen.MAX_DURATION, maxDuration);

        lickgen.setParameter(LickGen.MIN_INTERVAL, minInterval);

        lickgen.setParameter(LickGen.MAX_INTERVAL, maxInterval);

        lickgen.setParameter(LickGen.REST_PROB, restProb);

        lickgen.setParameter(LickGen.LEAP_PROB, leapProb);

        lickgen.setParameter(LickGen.CHORD_TONE_WEIGHT, chordToneWeight);

        lickgen.setParameter(LickGen.COLOR_TONE_WEIGHT, colorToneWeight);

        lickgen.setParameter(LickGen.SCALE_TONE_WEIGHT, scaleToneWeight);

        lickgen.setParameter(LickGen.CHORD_TONE_DECAY, chordToneDecayRate);

        lickgen.setParameter(LickGen.AUTO_FILL, autoFill);

        lickgen.setParameter(LickGen.RECTIFY, rectifyCheckBox.isSelected());

        lickgen.setParameter(LickGen.USE_GRAMMAR, useGrammar);

        lickgen.setParameter(LickGen.AVOID_REPEATS, avoidRepeats);

        lickgen.setParameter(LickGen.SCALE_ROOT, rootComboBox.getSelectedItem());

        lickgen.setParameter(LickGen.SCALE_TYPE, scaleComboBox.getSelectedItem());

        // These should not have to go to Lickgen to set stuff back in Lickgen,
        // but it's convenient to do it this way for now.

        lickgen.setParameter(LickGen.USE_SYNCOPATION, lickgen.getUseSyncopation());

        lickgen.setParameter(LickGen.SYNCOPATION_TYPE, lickgen.getSyncopationType());

        lickgen.setParameter(LickGen.SYNCOPATION_MULTIPLIER, lickgen.getSyncopationMultiplier());

        lickgen.setParameter(LickGen.SYNCOPATION_CONSTANT, lickgen.getSyncopationConstant());

        lickgen.setParameter(LickGen.EXPECTANCY_MULTIPLIER, lickgen.getExpectancyMultiplier());

        lickgen.setParameter(LickGen.EXPECTANCY_CONSTANT, lickgen.getExpectancyConstant());
    }

    public void verifyAndFill() {
        verifyTriageFields();

        if (autoFill) {
            FillProbsButtonActionPerformed(null);
        }
    }

    private void triageAndGenerate(int number) {
        triageLick(saveLickTF.getText(), number);
        if (continuallyGenerate) {
            generateLickButtonActionPerformed(null);
        }
    }
                        private void openGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openGrammarMI1ActionPerformed
                            notate.openGrammar();
                        }//GEN-LAST:event_openGrammarMI1ActionPerformed

                        private void showLogMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLogMI1ActionPerformed
                            openLog();
                        }//GEN-LAST:event_showLogMI1ActionPerformed

                        private void saveGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGrammarMI1ActionPerformed
                            notate.saveGrammarAs();
                        }//GEN-LAST:event_saveGrammarMI1ActionPerformed

                        private void editGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editGrammarMI1ActionPerformed
                            notate.editGrammar();
                        }//GEN-LAST:event_editGrammarMI1ActionPerformed

                        private void reloadGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadGrammarMI1ActionPerformed
                            notate.loadGrammar();
                            updateUseSoloist();
                        }//GEN-LAST:event_reloadGrammarMI1ActionPerformed

                        private void toCriticMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toCriticMI1ActionPerformed
                            notate.toCritic();
                        }//GEN-LAST:event_toCriticMI1ActionPerformed

                        private void showCriticMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCriticMI1ActionPerformed
                            notate.showCritic();
                        }//GEN-LAST:event_showCriticMI1ActionPerformed

                        private void useGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useGrammarMI1ActionPerformed
                            useGrammarAction();
                        }//GEN-LAST:event_useGrammarMI1ActionPerformed

                        private void grammarMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grammarMenu1ActionPerformed
                            // TODO add your handling code here:
                        }//GEN-LAST:event_grammarMenu1ActionPerformed

                        private void closeWindowMI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMI2ActionPerformed
                            closeWindow();
                        }//GEN-LAST:event_closeWindowMI2ActionPerformed
    public void closeWindow() {
        this.setVisible(false);

        // Used to prevent the improvise button from using the critic filter
        // since the Improvise button uses the same lick generation method.
        useCriticCheckBox.setSelected(false);
        criticGradeThreshold = DEFAULT_GRADE; // Reset default grade
        useCriticCheckBoxMouseClicked(null);

        WindowRegistry.unregisterWindow(this);
    }

    private void useGrammarAction() {
        useGrammar = useGrammarMI1.isSelected();

        fillMelodyButton.setEnabled(useGrammar);

        genRhythmButton.setEnabled(useGrammar);

        minDurationField.setEnabled(!useGrammar);

        maxDurationField.setEnabled(!useGrammar);

        restProbField.setEnabled(!useGrammar);
    }

    public boolean getUseGrammar() {

        //System.out.println("useGrammar = " + useGrammar);
        return useGrammar;
    }

                        private void cascadeMI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMI2ActionPerformed
                            WindowRegistry.cascadeWindows(this);
                        }//GEN-LAST:event_cascadeMI2ActionPerformed

                        private void generatorWindowMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_generatorWindowMenu1MenuSelected

                            generatorWindowMenu1.removeAll();

                            generatorWindowMenu1.add(closeWindowMI2);

                            generatorWindowMenu1.add(cascadeMI2);

                            generatorWindowMenu1.add(windowMenuSeparator2);

                            for (WindowMenuItem w : WindowRegistry.getWindows()) {

                                generatorWindowMenu1.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
                            }

                            generatorWindowMenu1.repaint();

                        }//GEN-LAST:event_generatorWindowMenu1MenuSelected

                        private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow
                            closeWindow();
                        }//GEN-LAST:event_closeWindow

    private void weightFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_weightFileButtonActionPerformed
    {//GEN-HEADEREND:event_weightFileButtonActionPerformed
        JFileChooser openDialog = new JFileChooser(ImproVisor.getVocabDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Weight File", "save");
        openDialog.setFileFilter(filter);
        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);

        if (openDialog.showDialog(this, "Open") != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File file = openDialog.getSelectedFile();
        weightFileTextField.setText(file.getAbsolutePath());
    }//GEN-LAST:event_weightFileButtonActionPerformed

    private void moveLayerDownTableButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveLayerDownTableButtonActionPerformed
    {//GEN-HEADEREND:event_moveLayerDownTableButtonActionPerformed
        int row = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        if (row != layerDataTable.getRowCount() - 1 && row != - 1
            && row != layerDataTable.getRowCount() - 2)
            {
                model.moveRow(row, row, row + 1);

                // Update index values
                resetIndexColumn(model);

                model.fireTableDataChanged();

                layerDataTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
            }
    }//GEN-LAST:event_moveLayerDownTableButtonActionPerformed

    private void moveLayerUpTableButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveLayerUpTableButtonActionPerformed
    {//GEN-HEADEREND:event_moveLayerUpTableButtonActionPerformed
        int row = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        if (row != 0 && row != - 1 && row != layerDataTable.getRowCount() - 1)
        {
            model.moveRow(row, row, row - 1);

            // Update index values
            resetIndexColumn(model);

            model.fireTableDataChanged();

            layerDataTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
        }
    }//GEN-LAST:event_moveLayerUpTableButtonActionPerformed

    private void removeLayerFromTableButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removeLayerFromTableButtonActionPerformed
    {//GEN-HEADEREND:event_removeLayerFromTableButtonActionPerformed
        if (layerDataTable.getRowCount() <= 2)
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Must have at least two layers for network."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        } else
        {
            int[] indices = layerDataTable.getSelectedRows();
            int lastIndex = layerDataTable.getRowCount() - 1;
            if (indices.length > layerDataTable.getRowCount() - 2)
            {
                JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "Selected too many layers for deletion,<br/>"
                        + "must have at least two layers for network."),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
            } else if (indices.length != 0)
            {
                DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
                for (int i = indices.length - 1; i >= 0; i--)
                {
                    int index = indices[i];
                    if (index != lastIndex)
                    {
                        model.removeRow(index);
                    }
                    model.fireTableRowsDeleted(index, index);
                }

                // Update index values
                resetIndexColumn(model);

                model.fireTableDataChanged();

                numberOfLayersTextField.setText(String.valueOf(layerDataTable.getRowCount()));
            }
        }
    }//GEN-LAST:event_removeLayerFromTableButtonActionPerformed

    private void addLayerToTableButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addLayerToTableButtonActionPerformed
    {//GEN-HEADEREND:event_addLayerToTableButtonActionPerformed
        int index = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();

        int nextIndex = layerDataTable.getRowCount();
        model.insertRow(nextIndex - 1, new Object[]{nextIndex, new Integer(64), "Logsig"});
        model.setValueAt(nextIndex + 1, nextIndex, 0);
        model.fireTableRowsInserted(nextIndex, nextIndex);
        layerDataTable.getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
        numberOfLayersTextField.setText(String.valueOf(layerDataTable.getRowCount()));

        // Move row up to insert it below currently selected.
        if (index != -1 && index != layerDataTable.getRowCount() - 2)
        {
            model.moveRow(nextIndex - 1, nextIndex - 1, index + 1);

            // Update index values
            resetIndexColumn(model);

            model.fireTableDataChanged();

            layerDataTable.getSelectionModel().setSelectionInterval(index, index);
        }
    }//GEN-LAST:event_addLayerToTableButtonActionPerformed

    private void trainingFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_trainingFileButtonActionPerformed
    {//GEN-HEADEREND:event_trainingFileButtonActionPerformed
        JFileChooser openDialog = new JFileChooser(ImproVisor.getNNetDataDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Training Data", "data");
        openDialog.setFileFilter(filter);
        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);

        if (openDialog.showDialog(this, "Open") != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File file = openDialog.getSelectedFile();
        trainingFileTextField.setText(file.getAbsolutePath());
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".training.data");
        if (pos > 0)
        {
            fileName = fileName.substring(0, pos);
        }
        weightFileTextField.setText(fileName + ".weights.save");
    }//GEN-LAST:event_trainingFileButtonActionPerformed

    private void resetNetworkButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetNetworkButtonActionPerformed
    {//GEN-HEADEREND:event_resetNetworkButtonActionPerformed
        critic.resetNetwork();
        soloCorrectionPanel.setVisible(false);
        resetNnetInstructionsButtonActionPerformed(null);
        resetDefaultValuesButtonActionPerformed(null);
    }//GEN-LAST:event_resetNetworkButtonActionPerformed

    private void resetDefaultValuesButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetDefaultValuesButtonActionPerformed
    {//GEN-HEADEREND:event_resetDefaultValuesButtonActionPerformed
        resetNnetInstructionsButtonActionPerformed(null);
        trainingFileTextField.setText("");
        epochLimitTextField.setText("20000");
        learningRateTextField.setText("0.01");
        mseGoalTextField.setText("0.01");
        modeComboBox.setSelectedIndex(2);
        weightFileTextField.setText("");

        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{1, 64, "Logsig"});
        model.addRow(new Object[]{2, 1, "Logsig"});
        numberOfLayersTextField.setText("2");
    }//GEN-LAST:event_resetDefaultValuesButtonActionPerformed

    private void resetNnetInstructionsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetNnetInstructionsButtonActionPerformed
    {//GEN-HEADEREND:event_resetNnetInstructionsButtonActionPerformed
        nnetOutputTextField.setCaretPosition(0);
        nnetScrollPane.getVerticalScrollBar().setValue(0);
        nnetOutputTextField.setText("To generate a weight file:\n"
            + "-Select training file (File name will end with \".training.data\")\n"
            + "-Weight file name with automatically be set\n"
            + "--Weight file will save to personal settings folder, in vocab\n"
            + "-Change the epoch limit if desired\n"
            + "-Change the default learning rate if desired\n"
            + "-Change the default MSE goal if desired\n"
            + "-Change the default mode if desired\n"
            + "-In the table to the right:\n"
            + "--Set the layer size for each layer\n"
            + "---Input (first) layer size determinted at runtime from input size\n"
            + "---The last layer, for output, should be of size 1\n"
            + "--Set the function for each layer\n"
            + "--Reorder rows as desired. Empty rows will be ignored.\n"
            + "-Press \"Generate Weight File\"\n\nTo load network:\n"
            + "-Select the weight file, from the vocab folder, under \"Weight File\"\n"
            + "-Press \"Load Weight\"\n-Network will be initialized per leadsheet\n\n"
            + "To clear a weight file:\n-Select the weight file, from the vocab folder, "
            + "under \"Weight File\"\n-Press \"Clear Weight File\""
            + "\n\n***There is a sample weight file in impro-visor-version-X.xx-files/vocab\n"
            + "   for general use. The licks used to create it were subjectively graded,\n"
            + "   and therefore may not reflect the preferences of the user.");
    }//GEN-LAST:event_resetNnetInstructionsButtonActionPerformed

    private void loadWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadWeightFileButtonActionPerformed
    {//GEN-HEADEREND:event_loadWeightFileButtonActionPerformed
        // Attempt to initialize the network if it hasn't been initialize
        if (critic.getNetwork() == null)
        {
            try
            {
                String filePath = weightFileTextField.getText();
                if(filePath.equals("")){
                    JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "Missing the weight file, <br/>"
                        + "need to train the network offline first<br/>"
                        + "and generate a weight file.<br/>"
                        + "Then enter the name of the file <br/>"
                        + "in the \"Weight File\" text field."),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
                }else{
                    StringBuilder weightOutput =
                    critic.prepareNetwork(filePath);

                    nnetOutputTextField.setText(weightOutput.toString());
                    nnetOutputTextField.setCaretPosition(0);
                    nnetScrollPane.getVerticalScrollBar().setValue(0);
                }
            } catch (IllegalArgumentException e)
            {
                JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "File in the \"Weight File\" text field is invalid.<br/>"
                        + "Make sure the file you selected is a <br/>"
                        + ".weights.save file generated by Impro-Visor."),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException e){
                JOptionPane.showMessageDialog(null,
                    new JLabel(e.getMessage()), "Alert", JOptionPane.PLAIN_MESSAGE);
            }
        }

        if (critic.getNetwork() != null && soloCorrectionPanel.isVisible())
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Network already initialized.<br/>"
                    + "Reset the network to load a new weight file."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }

        // If the network has been initialized, allow for critic use
        if (critic.getNetwork() != null)
        {
            soloCorrectionPanel.setVisible(true);
        }
    }//GEN-LAST:event_loadWeightFileButtonActionPerformed

    private void clearWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearWeightFileButtonActionPerformed
    {//GEN-HEADEREND:event_clearWeightFileButtonActionPerformed
        if (!weightFileTextField.getText().isEmpty())
        {
            
            String text = weightFileTextField.getText();
            if (text.endsWith(".weights.save")) {
                File file = new File(text);
                if (file.exists()) {
                    file.delete();
                    weightFileTextField.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        new JLabel("<html><div style=\"text-align: center;\">"
                                + "Attempting to delete a file<br/>"
                                + "that is not a weight file."),
                        "Alert", JOptionPane.PLAIN_MESSAGE);
            }
            
        }
    }//GEN-LAST:event_clearWeightFileButtonActionPerformed

    private void getNetworkStatsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_getNetworkStatsButtonActionPerformed
    {//GEN-HEADEREND:event_getNetworkStatsButtonActionPerformed
        if (critic.getNetwork() == null)
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Network not initialized,<br/>"
                    + "need to load the weights file."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        } else
        {
            StringBuilder statOutput = critic.getNetwork().getStatistics();
            nnetOutputTextField.setCaretPosition(0);
            nnetScrollPane.getVerticalScrollBar().setValue(0);
            nnetOutputTextField.setText(statOutput.toString());
        }
    }//GEN-LAST:event_getNetworkStatsButtonActionPerformed

    private void generateWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generateWeightFileButtonActionPerformed
    {//GEN-HEADEREND:event_generateWeightFileButtonActionPerformed
        int numRows = layerDataTable.getRowCount();
        int count = 0;
        ArrayList<Object[]> data = new ArrayList<Object[]>();
        boolean incompleteRows = false;

        for (int i = 0; i < numRows; i++)
        {
            try
            {
                int size = (Integer) layerDataTable.getValueAt(i, 1);
                String type = (String) layerDataTable.getValueAt(i, 2);
                Object[] items = new Object[2];
                items[0] = size;
                items[1] = type;
                data.add(items);
                count++;
            } catch (Exception e)
            {
                incompleteRows = true;
            }
        }

        boolean badTextField = false;
        try
        {
            int i = Integer.parseInt(epochLimitTextField.getText());
            double j = Double.parseDouble(learningRateTextField.getText());
            double k = Double.parseDouble(mseGoalTextField.getText());
        } catch (Exception e)
        {
            badTextField = true;
        }

        if (trainingFileTextField.getText().isEmpty()
            || epochLimitTextField.getText().isEmpty()
            || learningRateTextField.getText().isEmpty()
            || mseGoalTextField.getText().isEmpty()
            || weightFileTextField.getText().isEmpty()
            || data.size() < 2
            || incompleteRows
            || badTextField)
            {
                StringBuilder output = new StringBuilder();
                if (trainingFileTextField.getText().isEmpty())
                {
                    output.append("    ").append("Training File").append("<br/>");
                }
                if (epochLimitTextField.getText().isEmpty())
                {
                    output.append("    ").append("Epoch Limit").append("<br/>");
                }
                if (learningRateTextField.getText().isEmpty())
                {
                    output.append("    ").append("Learning Rate").append("<br/>");
                }
                if (mseGoalTextField.getText().isEmpty())
                {
                    output.append("    ").append("MSE Goal").append("<br/>");
                }
                if (weightFileTextField.getText().isEmpty())
                {
                    output.append("    ").append("Weight File").append("<br/>");
                }
                if (data.size() < 2)
                {
                    output.append("    ").append("Too Few Layers").append("<br/>");
                }
                if (incompleteRows)
                {
                    output.append("    ").append("Incomplete layer, delete or complete").append("<br/>");
                }
                if (badTextField)
                {
                    output.append("    ").append("Incorrect value(s) for numeric field(s)").append("<br/>");
                }

                JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "Missing the following needed values:<br/>"
                        + output.toString()),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
            } else
            {
                critic.trainNetwork(trainingFileTextField.getText(),
                    epochLimitTextField.getText(),
                    learningRateTextField.getText(),
                    mseGoalTextField.getText(),
                    Integer.toString(modeComboBox.getSelectedIndex()),
                    weightFileTextField.getText(),
                    count,
                    data);
            }
    }//GEN-LAST:event_generateWeightFileButtonActionPerformed

    private void layerDataTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_layerDataTableMouseClicked
    {//GEN-HEADEREND:event_layerDataTableMouseClicked
        int row = layerDataTable.rowAtPoint(evt.getPoint());
        int column = layerDataTable.columnAtPoint(evt.getPoint());

        if (row == -1 || column == -1)
        {
            ListSelectionModel model = layerDataTable.getSelectionModel();
            model.removeSelectionInterval(0, layerDataTable.getRowCount());
            model.removeSelectionInterval(0, layerDataTable.getColumnCount());
        }
    }//GEN-LAST:event_layerDataTableMouseClicked

    private void testGenerationActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_testGenerationActionPerformed
    {//GEN-HEADEREND:event_testGenerationActionPerformed
        notate.generateFromButton();
    }//GEN-LAST:event_testGenerationActionPerformed

    private void toGrammarBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_toGrammarBtnActionPerformed
    {//GEN-HEADEREND:event_toGrammarBtnActionPerformed
        toGrammar();
        
        if(rhythmClusterCheckbox.isSelected()){
              setRhythmClusterFilenameInPreferences(CreateGrammar.getClusterOutputFile(notate.getGrammarFileName()));
         }
    }//GEN-LAST:event_toGrammarBtnActionPerformed

    private void openCorpusBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openCorpusBtnActionPerformed
    {//GEN-HEADEREND:event_openCorpusBtnActionPerformed
        if(rhythmClusterCheckbox.isSelected()){
            windowSlideField.setText(windowSizeField.getText());
        }
        notate.openCorpus();
        toFront();
    }//GEN-LAST:event_openCorpusBtnActionPerformed

    private void saveGrammarAsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveGrammarAsButtonActionPerformed
    {//GEN-HEADEREND:event_saveGrammarAsButtonActionPerformed
       initializeGrammarLearning();
    }//GEN-LAST:event_saveGrammarAsButtonActionPerformed

    private void useAbstractBricksCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useAbstractBricksCheckboxActionPerformed
    {//GEN-HEADEREND:event_useAbstractBricksCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useAbstractBricksCheckboxActionPerformed

    private void useRelativeBricksCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useRelativeBricksCheckboxActionPerformed
    {//GEN-HEADEREND:event_useRelativeBricksCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useRelativeBricksCheckboxActionPerformed

    private void MarkovLengthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_MarkovLengthFieldActionPerformed
    {//GEN-HEADEREND:event_MarkovLengthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MarkovLengthFieldActionPerformed

    private void useBricksCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useBricksCheckboxActionPerformed
    {//GEN-HEADEREND:event_useBricksCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useBricksCheckboxActionPerformed

    private void useRelativeWindowsCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useRelativeWindowsCheckboxActionPerformed
    {//GEN-HEADEREND:event_useRelativeWindowsCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useRelativeWindowsCheckboxActionPerformed

    private void windowSlideFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_windowSlideFieldActionPerformed
    {//GEN-HEADEREND:event_windowSlideFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_windowSlideFieldActionPerformed

    private void windowSizeFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_windowSizeFieldActionPerformed
    {//GEN-HEADEREND:event_windowSizeFieldActionPerformed
        CreateGrammar.setMotifWindowSizeAndSlide(Integer.parseInt(windowSizeField.getText()));
        if(rhythmClusterCheckbox.isSelected()){
            windowSlideField.setText(windowSizeField.getText());
            windowParametersPanel.invalidate();
            windowParametersPanel.validate();
            this.repaint();
        }
    }//GEN-LAST:event_windowSizeFieldActionPerformed

    private void loadRandomGrammarButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadRandomGrammarButtonActionPerformed
    {//GEN-HEADEREND:event_loadRandomGrammarButtonActionPerformed
        // Load Random grammar for neural network lick generation
        notate.setGrammar("Random");
    }//GEN-LAST:event_loadRandomGrammarButtonActionPerformed

    private void criticGradeTextFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_criticGradeTextFieldFocusLost
    {//GEN-HEADEREND:event_criticGradeTextFieldFocusLost
        // Set lower limit for criticGrade filter
        String gradeField = criticGradeTextField.getText();
        if (!gradeField.equals(""))
        {
            try
            {
                int grade = Integer.parseInt(gradeField);
                // Boundary cases for lick filter
                if (grade < 1)
                {
                    criticGradeThreshold = 1;
                    criticGradeTextField.setText(String.valueOf(criticGradeThreshold));
                } else if (grade > 9)
                {
                    criticGradeThreshold = 9;
                    criticGradeTextField.setText(String.valueOf(criticGradeThreshold));
                } else
                {
                    criticGradeThreshold = grade;
                }
            } // Reset if the entry isn't an integer value
            catch (Exception e)
            {
                criticGradeTextField.setText("Grade");
            }
        } // Reset if empty entry
        else
        {
            criticGradeThreshold = DEFAULT_GRADE;
            criticGradeTextField.setText("Grade");
        }
    }//GEN-LAST:event_criticGradeTextFieldFocusLost

    private void criticGradeTextFieldFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_criticGradeTextFieldFocusGained
    {//GEN-HEADEREND:event_criticGradeTextFieldFocusGained
        criticGradeTextField.setText("");
    }//GEN-LAST:event_criticGradeTextFieldFocusGained

    private void useCriticCheckBoxMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_useCriticCheckBoxMouseClicked
    {//GEN-HEADEREND:event_useCriticCheckBoxMouseClicked
        useCritic = useCriticCheckBox.isSelected();
        criticGradeTextField.setEnabled(useCritic);
        counterForCriticTextField.setEnabled(useCritic);
        // Reset all text fields
        if (!useCritic)
        {
            gradeFromCritic = null;
            criticGradeTextField.setText("Grade");
            counterForCriticTextField.setText("Counter");
            lickFromStaveGradeTextField.setText("Grade");
        }
    }//GEN-LAST:event_useCriticCheckBoxMouseClicked

    private void gradeLickFromStaveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gradeLickFromStaveButtonActionPerformed
    {//GEN-HEADEREND:event_gradeLickFromStaveButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();
        
        if(end - start <= BEAT * 7){
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Selection is too short, critic<br/>"
                    + "needs to see at least 8 beats."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }else{

            ArrayList<Note> noteList = new ArrayList<Note>();
            ArrayList<Chord> chordList = new ArrayList<Chord>();

            // Generate notes and chords over the lick
            critic.generateNotesAndChords(noteList, chordList, start, end);

            // Grade the lick, passing it through the critic filter
            gradeFromCritic = critic.gradeFromCritic(noteList, chordList);
            if (gradeFromCritic != null)
            {
                String formattedGrade = String.format("%.3f", gradeFromCritic);
                lickFromStaveGradeTextField.setText(formattedGrade);
            } else
            {
                lickFromStaveGradeTextField.setText("Error");
            }
        }
    }//GEN-LAST:event_gradeLickFromStaveButtonActionPerformed

    private void regenerateLickForSoloButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_regenerateLickForSoloButtonActionPerformed
    {//GEN-HEADEREND:event_regenerateLickForSoloButtonActionPerformed
        gradeLickFromStaveButtonActionPerformed(null);
        if (gradeFromCritic != null && gradeFromCritic < criticGradeThreshold)
        {
            generateLickButtonActionPerformed(null);
        }
    }//GEN-LAST:event_regenerateLickForSoloButtonActionPerformed

    private void gradeAllMeasuresButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gradeAllMeasuresButtonActionPerformed
    {//GEN-HEADEREND:event_gradeAllMeasuresButtonActionPerformed
        final int totalMeasures = notate.getCurrentStave().getNumMeasures();
        if (totalMeasures % 2 == 1)
        {
            offsetByMeasureGradeSoloButtonActionPerformed(null);
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Odd number of measures,<br/>"
                    + "offsetting grading by one measure."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }

        new Thread(new Runnable()
        {
            public void run()
            {

                // Mute since putlick() will play what it places on the leadsheet
                int volume = notate.getScore().getMasterVolume();
                notate.getScore().setMasterVolumeMuted(true);
                notate.setMasterVolumes(0);

                int thisTotalSlots = totalMeasures * WHOLE;
                int start = notate.getCurrentStave().getSelectionStart();
                int end = notate.getCurrentStave().getSelectionEnd();

                // Round to the nearest measure
                int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

                // Iterate through all two measure selections
                while (start < thisTotalSlots && end < thisTotalSlots)
                {
                    ArrayList<Note> noteList = new ArrayList<Note>();
                    ArrayList<Chord> chordList = new ArrayList<Chord>();

                    // Generate notes and chords over the lick
                    critic.generateNotesAndChords(noteList, chordList, start, end);

                    // Grade the lick, passing it through the critic filter
                    Double gradeFromFilter = critic.gradeFromCritic(noteList, chordList);

                    // Default grade guarentees generating a new lick if there
                    // if an error
                    double grade = 0;
                    if (gradeFromFilter != null)
                    {
                        grade = gradeFromFilter;
                    }

                    if (grade < criticGradeThreshold)
                    {
                        generateLickButtonActionPerformed(null);
                    }

                    // Move forward by the selection length
                    start += numSlotsSelected;
                    end += numSlotsSelected;
                    notate.getCurrentStave().setSelection(start, end);
                }

                // Restore volume
                notate.getScore().setMasterVolumeMuted(false);
                notate.setMasterVolumes(volume);

                notate.getCurrentStave().play(0);

            } // End of Runnable
        }).start(); // End of Thread
    }//GEN-LAST:event_gradeAllMeasuresButtonActionPerformed

    private void resetSelectionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetSelectionButtonActionPerformed
    {//GEN-HEADEREND:event_resetSelectionButtonActionPerformed
        notate.getCurrentStave().setSelection(0, 16 * EIGHTH - 1);
        notate.getCurrentStave().repaint();
    }//GEN-LAST:event_resetSelectionButtonActionPerformed

    private void backwardGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_backwardGradeSoloButtonActionPerformed
    {//GEN-HEADEREND:event_backwardGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

        // Move backwards by the selection length
        start -= numSlotsSelected;
        end -= numSlotsSelected;

        if (start >= 0)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_backwardGradeSoloButtonActionPerformed

    private void forwardGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_forwardGradeSoloButtonActionPerformed
    {//GEN-HEADEREND:event_forwardGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

        // Move forwards by the selection length
        start += numSlotsSelected;
        end += numSlotsSelected;
        int thisTotalSlots = notate.getCurrentStave().getNumMeasures() * WHOLE;

        if (start < thisTotalSlots && end < thisTotalSlots)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_forwardGradeSoloButtonActionPerformed

    private void offsetByMeasureGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_offsetByMeasureGradeSoloButtonActionPerformed
    {//GEN-HEADEREND:event_offsetByMeasureGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        // Move the selection two measures ahead
        start += 8 * EIGHTH;
        end += 8 * EIGHTH;
        int totalSlotsOffset = (notate.getCurrentStave().getNumMeasures() * WHOLE) - WHOLE;

        if (start < totalSlotsOffset)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_offsetByMeasureGradeSoloButtonActionPerformed

    private void autoFillCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_autoFillCheckBoxActionPerformed
    {//GEN-HEADEREND:event_autoFillCheckBoxActionPerformed
        autoFill = autoFillCheckBox.isSelected();

        if (autoFill)
        {
            redrawTriage();
        }
    }//GEN-LAST:event_autoFillCheckBoxActionPerformed

    private void FillProbsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_FillProbsButtonActionPerformed
    {//GEN-HEADEREND:event_FillProbsButtonActionPerformed
        if( notate.getCurrentSelectionStart() == -1 )
          {
            return;
          }

        ArrayList<double[]> probs = lickgen.fillProbs(
                notate.getChordProg(),
                chordToneWeight,
                scaleToneWeight,
                colorToneWeight,
                chordToneDecayRate,
                notate.getCurrentSelectionStart(),
                notate.getTotalSlots());

        for( int i = 0; i < Math.min(probs.size(), lickPrefs.size()); ++i )
          {
            double[] pArray = probs.get(i);
            JTextField[] tfArray = lickPrefs.get(i);
            for( int j = 0; j < Math.max(pArray.length, tfArray.length); ++j )
              {
                String p = ((Double) pArray[j]).toString();
                JTextField field = tfArray[j];
                field.setText(p);   //.substring(0, Math.min(p.length(), 5)));
                field.setCaretPosition(0);
              }
          }
    }//GEN-LAST:event_FillProbsButtonActionPerformed

    private void clearProbsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearProbsButtonActionPerformed
    {//GEN-HEADEREND:event_clearProbsButtonActionPerformed
        for (int i = 0; i < lickPrefs.size(); ++i)
        {
            for (int j = 0; j < 12; ++j)
            {
                lickPrefs.get(i)[j].setText("0");
            }
        }
    }//GEN-LAST:event_clearProbsButtonActionPerformed

    private void grade10BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade10BtnActionPerformed
    {//GEN-HEADEREND:event_grade10BtnActionPerformed
        triageAndGenerate(10);
    }//GEN-LAST:event_grade10BtnActionPerformed

    private void grade9BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade9BtnActionPerformed
    {//GEN-HEADEREND:event_grade9BtnActionPerformed
        triageAndGenerate(9);
    }//GEN-LAST:event_grade9BtnActionPerformed

    private void grade8BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade8BtnActionPerformed
    {//GEN-HEADEREND:event_grade8BtnActionPerformed
        triageAndGenerate(8);
    }//GEN-LAST:event_grade8BtnActionPerformed

    private void grade7BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade7BtnActionPerformed
    {//GEN-HEADEREND:event_grade7BtnActionPerformed
        triageAndGenerate(7);
    }//GEN-LAST:event_grade7BtnActionPerformed

    private void grade6BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade6BtnActionPerformed
    {//GEN-HEADEREND:event_grade6BtnActionPerformed
        triageAndGenerate(6);
    }//GEN-LAST:event_grade6BtnActionPerformed

    private void grade5BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade5BtnActionPerformed
    {//GEN-HEADEREND:event_grade5BtnActionPerformed
        triageAndGenerate(5);
    }//GEN-LAST:event_grade5BtnActionPerformed

    private void grade4BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade4BtnActionPerformed
    {//GEN-HEADEREND:event_grade4BtnActionPerformed
        triageAndGenerate(4);
    }//GEN-LAST:event_grade4BtnActionPerformed

    private void grade3BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade3BtnActionPerformed
    {//GEN-HEADEREND:event_grade3BtnActionPerformed
        triageAndGenerate(3);
    }//GEN-LAST:event_grade3BtnActionPerformed

    private void grade2BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade2BtnActionPerformed
    {//GEN-HEADEREND:event_grade2BtnActionPerformed
        triageAndGenerate(2);
    }//GEN-LAST:event_grade2BtnActionPerformed

    private void grade1BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade1BtnActionPerformed
    {//GEN-HEADEREND:event_grade1BtnActionPerformed
        triageAndGenerate(1);
    }//GEN-LAST:event_grade1BtnActionPerformed

    private void rootComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rootComboBoxActionPerformed
    {//GEN-HEADEREND:event_rootComboBoxActionPerformed
        lickgen.setPreferredScale(
                (String) rootComboBox.getSelectedItem(),
                (String) scaleComboBox.getSelectedItem());

        redrawTriage();
    }//GEN-LAST:event_rootComboBoxActionPerformed

    private void scaleComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_scaleComboBoxActionPerformed
    {//GEN-HEADEREND:event_scaleComboBoxActionPerformed
        String root = (String) rootComboBox.getSelectedItem();

        String type = (String) scaleComboBox.getSelectedItem();

        if( root == null || type == null )
          {
            return;
          }

        if( type.equals("None") || type.equals(FIRST_SCALE) )
          {
            rootComboBox.setEnabled(false);
          }
        else
          {
            rootComboBox.setEnabled(true);
          }

        lickgen.setPreferredScale((String) rootComboBox.getSelectedItem(),
                                  (String) scaleComboBox.getSelectedItem());

        redrawTriage();

        if( autoFill )
          {
            FillProbsButtonActionPerformed(null);
          }
    }//GEN-LAST:event_scaleComboBoxActionPerformed

    private void chordToneDecayFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_chordToneDecayFieldFocusLost
    {//GEN-HEADEREND:event_chordToneDecayFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_chordToneDecayFieldFocusLost

    private void chordToneDecayFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chordToneDecayFieldActionPerformed
    {//GEN-HEADEREND:event_chordToneDecayFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_chordToneDecayFieldActionPerformed

    private void scaleToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_scaleToneWeightFieldFocusLost
    {//GEN-HEADEREND:event_scaleToneWeightFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_scaleToneWeightFieldFocusLost

    private void scaleToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_scaleToneWeightFieldActionPerformed
    {//GEN-HEADEREND:event_scaleToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_scaleToneWeightFieldActionPerformed

    private void colorToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_colorToneWeightFieldFocusLost
    {//GEN-HEADEREND:event_colorToneWeightFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_colorToneWeightFieldFocusLost

    private void colorToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_colorToneWeightFieldActionPerformed
    {//GEN-HEADEREND:event_colorToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_colorToneWeightFieldActionPerformed

    private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_chordToneWeightFieldFocusLost
    {//GEN-HEADEREND:event_chordToneWeightFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_chordToneWeightFieldFocusLost

    private void chordToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chordToneWeightFieldActionPerformed
    {//GEN-HEADEREND:event_chordToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_chordToneWeightFieldActionPerformed

    private void styleRecognitionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleRecognitionButtonActionPerformed
    {//GEN-HEADEREND:event_styleRecognitionButtonActionPerformed
        // First prepare critics for usage
        if (styleRecognitionButton.getText().equals("Prepare Critics"))
        {

            new Thread(new Runnable()
            {
                public void run()
                {

                    // Create list of critics for grading, paired with a musician's name
                    critics = new TreeMap<String, Critic>();

                    // Iterate through every weight file
                    File folder = ImproVisor.getStyleRecognitionDirectory();
                    File[] files = folder.listFiles();
                    if( files == null )
                      {
                        return;
                      }
                    Arrays.sort(files, new Comparator<File>()
                    {
                        public int compare(File f1, File f2)
                        {
                            return f1.getName().compareTo(f2.getName());
                        }
                    });

                    setRhythmFieldText("Preparing critics for grading...");

                    // Prepare all critics, and pair them with a file name
                    for (File f : files)
                    {
                        if (f.getName().endsWith(".weights.save"))
                        {
                            try
                            {
                                Critic currCritic = new Critic();
                                currCritic.prepareNetworkFromFile(f);

                                String fileName = f.getName();
                                int pos = fileName.lastIndexOf(".weights.save");
                                if (pos > 0)
                                {
                                    fileName = fileName.substring(0, pos);
                                }
                                critics.put(fileName, currCritic);
                            } catch (Exception e)
                            {
                                System.out.println("Problem with one file: " + f.getName());
                            }
                        }
                    }

                    setRhythmFieldText("");

                    if (critics.size() != numCritics)
                    {
                        JOptionPane.showMessageDialog(null,
                            new JLabel("<html><div style=\"text-align: center;\">"
                                + "This feature works best with the full set of critics.<br/>"
                                + "You have " + critics.size() + " out of the total " + numCritics + " critics.<br/>"
                                + "Please download the rest of the critics."),
                            "Using Critics", JOptionPane.PLAIN_MESSAGE);
                    }

                    styleRecognitionButton.setText("Guess Musician");

                } // End of Runnable
            }).start(); // End of Thread
        } // Do only if there is some selection
        else if (notate.getCurrentStave().getSelectionLength() != 0)
        {

            new Thread(new Runnable()
            {
                public void run()
                {

                    TreeMap<String, Double> grades = new TreeMap<String, Double>();

                    // Use all critics to get all grades for each network
                    for (String name : critics.keySet())
                    {
                        Critic thisCritic = critics.get(name);
                        int start = notate.getCurrentStave().getSelectionStart();
                        int end = notate.getCurrentStave().getSelectionEnd();

                        ArrayList<Note> noteList = new ArrayList<Note>();
                        ArrayList<Chord> chordList = new ArrayList<Chord>();

                        // Generate notes and chords over the lick
                        thisCritic.generateNotesAndChords(noteList, chordList, start, end);

                        // Grade the lick, passing it through the critic filter
                        Double gradeFromFilter = thisCritic.gradeFromCritic(noteList, chordList);
                        if (gradeFromFilter != null)
                        {
                            grades.put(name, gradeFromFilter);
                        } else
                        {
                            System.out.println("Error from grading.");
                        }
                    }

                    // Output for extra content from critics
                    StringBuilder criticsOutput = new StringBuilder();

                    // Guess on stylistic similarity based on highest grade
                    double highestGrade = 0.0;
                    String likelyName = "";
                    for (String name : grades.keySet())
                    {
                        double currGrade = grades.get(name);

                        criticsOutput.append(fixName(name)).append(": ").append(String.format("%.3f", currGrade)).append("\n\n");

                        if (currGrade > highestGrade)
                        {
                            highestGrade = currGrade;
                            likelyName = name;
                        }
                    }

                    // Clean up formatting
                    String cleanName = fixName(likelyName);
                    String cleanGrade = String.format("%.3f", highestGrade);

                    setNetworkOutputTextField(criticsOutput.toString());

                    Object[] options = {"Yes, to Neural Network tab",
                        "Cancel"};
                    String label = "<html><div style=\"text-align: center;\">"
                    + "The musician whose style is most similar: <br/>"
                    + cleanName + "<br/><br/>"
                    + "Grade: " + cleanGrade + "<br/><br/>"
                    + "Choose \"Yes\" if you want to see more output";
                    int n = JOptionPane.showOptionDialog(null,
                        label,
                        "Style Recogntion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                    if (n == 0)
                    {
                        // Avoids using a specific index for setting the tab
                        int index = 0;
                        for (int i = 0; i < generatorPane.getTabCount(); i++)
                        {
                            if (generatorPane.getTitleAt(i).contains("Network"))
                            {
                                index = i;
                            }
                        }
                        generatorPane.setSelectedIndex(index);
                    }

                } // End of Runnable
            }).start(); // End of Thread
        } else
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Choose a selection of measures before guessing."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }
    }//GEN-LAST:event_styleRecognitionButtonActionPerformed

    private void generationSelectionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generationSelectionButtonActionPerformed
    {//GEN-HEADEREND:event_generationSelectionButtonActionPerformed
        if (notate.getCurrentStave().getLockSelectionWidth() == -1)
        {
            String s = JOptionPane.showInputDialog("Select the number of measures\n"
                + "for the graded licks", 2);

            if (s != null && s.length() > 0)
            {
                int measureNum;

                try
                {
                    measureNum = Integer.parseInt(s);
                } catch (Exception e)
                {
                    measureNum = 2;
                }

                notate.getCurrentStave().lockSelectionWidth(measureNum * WHOLE);

                notate.getCurrentStave().repaint();

                generationSelectionButton.setText("Unlock selection");
            }
        } else
        {
            notate.getCurrentStave().unlockSelectionWidth();

            generationSelectionButton.setText("Size of Selection");
        }
    }//GEN-LAST:event_generationSelectionButtonActionPerformed

    private void continuallyGenerateCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_continuallyGenerateCheckBoxActionPerformed
    {//GEN-HEADEREND:event_continuallyGenerateCheckBoxActionPerformed
        continuallyGenerate = continuallyGenerateCheckBox.isSelected();
    }//GEN-LAST:event_continuallyGenerateCheckBoxActionPerformed

    private void regenerateHeadDataBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_regenerateHeadDataBtnActionPerformed
    {//GEN-HEADEREND:event_regenerateHeadDataBtnActionPerformed
        notate.writeHeadData();
    }//GEN-LAST:event_regenerateHeadDataBtnActionPerformed

    private void useSoloistCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useSoloistCheckBoxActionPerformed
    {//GEN-HEADEREND:event_useSoloistCheckBoxActionPerformed
        updateUseSoloist();
    }//GEN-LAST:event_useSoloistCheckBoxActionPerformed

    private void rectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rectifyCheckBoxActionPerformed
    {//GEN-HEADEREND:event_rectifyCheckBoxActionPerformed
        rectify = rectifyCheckBox.isSelected();
        chordBox.setEnabled(rectifyCheckBox.isSelected());
        colorBox.setEnabled(rectifyCheckBox.isSelected());
        approachBox.setEnabled(rectifyCheckBox.isSelected());
    }//GEN-LAST:event_rectifyCheckBoxActionPerformed

    private void gapFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gapFieldActionPerformed
    {//GEN-HEADEREND:event_gapFieldActionPerformed
        notate.setGenerationGap(Notate.doubleFromTextField(gapField, 0, 9.99, 1));
    }//GEN-LAST:event_gapFieldActionPerformed

    private void recurrentCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_recurrentCheckboxActionPerformed
    {//GEN-HEADEREND:event_recurrentCheckboxActionPerformed
        notate.setRecurrent(recurrentCheckbox.isSelected());
    }//GEN-LAST:event_recurrentCheckboxActionPerformed

    private void avoidRepeatsCheckboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_avoidRepeatsCheckboxActionPerformed
    {//GEN-HEADEREND:event_avoidRepeatsCheckboxActionPerformed
        avoidRepeats = avoidRepeatsCheckbox.isSelected();
    }//GEN-LAST:event_avoidRepeatsCheckboxActionPerformed

    private void leapProbFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_leapProbFieldFocusLost
    {//GEN-HEADEREND:event_leapProbFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_leapProbFieldFocusLost

    private void leapProbFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leapProbFieldActionPerformed
    {//GEN-HEADEREND:event_leapProbFieldActionPerformed
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_leapProbFieldActionPerformed

    private void restProbFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_restProbFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_restProbFieldenterLickKeyPressed

    }//GEN-LAST:event_restProbFieldenterLickKeyPressed

    private void restProbFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_restProbFieldFocusLost
    {//GEN-HEADEREND:event_restProbFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_restProbFieldFocusLost

    private void restProbFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_restProbFieldGetsFocus
    {//GEN-HEADEREND:event_restProbFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_restProbFieldGetsFocus

    private void restProbFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_restProbFieldActionPerformed
    {//GEN-HEADEREND:event_restProbFieldActionPerformed
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_restProbFieldActionPerformed

    private void totalBeatsFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_totalBeatsFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_totalBeatsFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldenterLickKeyPressed

    private void totalBeatsFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_totalBeatsFieldFocusLost
    {//GEN-HEADEREND:event_totalBeatsFieldFocusLost
        notate.setCurrentSelectionEnd(
                notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldFocusLost

    private void totalBeatsFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_totalBeatsFieldGetsFocus
    {//GEN-HEADEREND:event_totalBeatsFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldGetsFocus

    private void totalBeatsFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_totalBeatsFieldActionPerformed
    {//GEN-HEADEREND:event_totalBeatsFieldActionPerformed
        notate.setCurrentSelectionEnd(
        notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldActionPerformed

    private void maxDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_maxDurationFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_maxDurationFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxDurationFieldenterLickKeyPressed

    private void maxDurationFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxDurationFieldFocusLost
    {//GEN-HEADEREND:event_maxDurationFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_maxDurationFieldFocusLost

    private void maxDurationFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxDurationFieldActionPerformed
    {//GEN-HEADEREND:event_maxDurationFieldActionPerformed
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_maxDurationFieldActionPerformed

    private void minDurationFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minDurationFieldFocusLost
    {//GEN-HEADEREND:event_minDurationFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_minDurationFieldFocusLost

    private void minDurationFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minDurationFieldActionPerformed
    {//GEN-HEADEREND:event_minDurationFieldActionPerformed
        verifyAndSaveTriageFields();
    }//GEN-LAST:event_minDurationFieldActionPerformed

    private void maxIntervalFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxIntervalFieldFocusLost
    {//GEN-HEADEREND:event_maxIntervalFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_maxIntervalFieldFocusLost

    private void maxIntervalFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxIntervalFieldActionPerformed
    {//GEN-HEADEREND:event_maxIntervalFieldActionPerformed
        verifyAndSaveTriageFields();
    }//GEN-LAST:event_maxIntervalFieldActionPerformed

    private void minIntervalFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minIntervalFieldFocusLost
    {//GEN-HEADEREND:event_minIntervalFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_minIntervalFieldFocusLost

    private void minIntervalFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minIntervalFieldActionPerformed
    {//GEN-HEADEREND:event_minIntervalFieldActionPerformed
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_minIntervalFieldActionPerformed

    private void minPitchFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minPitchFieldFocusLost
    {//GEN-HEADEREND:event_minPitchFieldFocusLost
       verifyAndSaveTriageFields();
    }//GEN-LAST:event_minPitchFieldFocusLost

    private void minPitchFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minPitchFieldActionPerformed
    {//GEN-HEADEREND:event_minPitchFieldActionPerformed
        verifyAndSaveTriageFields();
    }//GEN-LAST:event_minPitchFieldActionPerformed

    private void maxPitchFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxPitchFieldFocusLost
    {//GEN-HEADEREND:event_maxPitchFieldFocusLost
        verifyAndSaveTriageFields();
    }//GEN-LAST:event_maxPitchFieldFocusLost

    private void maxPitchFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxPitchFieldActionPerformed
    {//GEN-HEADEREND:event_maxPitchFieldActionPerformed
       verifyAndSaveTriageFields(); 
    }//GEN-LAST:event_maxPitchFieldActionPerformed

    private void saveLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveLickButtonActionPerformed
    {//GEN-HEADEREND:event_saveLickButtonActionPerformed
        notate.openSaveLickFrame("<Generated Lick>");
    }//GEN-LAST:event_saveLickButtonActionPerformed

    private void stopLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopLickButtonActionPerformed
    {//GEN-HEADEREND:event_stopLickButtonActionPerformed
        stopPlaying();
    }//GEN-LAST:event_stopLickButtonActionPerformed

    private void playLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playLickButtonActionPerformed
    {//GEN-HEADEREND:event_playLickButtonActionPerformed
        playSelection();
    }//GEN-LAST:event_playLickButtonActionPerformed

    private void getSelRhythmButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_getSelRhythmButtonActionPerformed
    {//GEN-HEADEREND:event_getSelRhythmButtonActionPerformed
        int selStart = notate.getCurrentSelectionStart();

        int selEnd = notate.getCurrentSelectionEnd();

        MelodyPart part = notate.getCurrentMelodyPart();

        int current = selStart;

        Polylist rhythmString = new Polylist();

        while( current <= selEnd )
          {
            StringBuilder sb = new StringBuilder();

            int value = Note.getDurationString(sb, part.getNote(current).getRhythmValue());

            int rhythm = 0;

            if( part.getNote(current).isRest() )
              {
                rhythmString = rhythmString.cons("R" + sb.substring(1));
              }
            else
              {
                rhythmString = rhythmString.cons("X" + sb.substring(1));
              }
            current += part.getNote(current).getRhythmValue();
          }

        rhythmString = rhythmString.reverse();

        setRhythmFieldText(rhythmString.toString());
    }//GEN-LAST:event_getSelRhythmButtonActionPerformed

    private void getAbstractMelodyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_getAbstractMelodyButtonActionPerformed
    {//GEN-HEADEREND:event_getAbstractMelodyButtonActionPerformed

    extractAbstractMelody();

  }
public boolean getAvoidRepeats(){
    return avoidRepeats;
}
public void getAbstractMelody()
  {
    //create a file
    File f = new File(notate.getGrammarFileName());
    String dir = f.getParentFile().getPath();

    if( useBricksCheckbox.isSelected() )
      {
        imp.generalCluster.CreateBrickGrammar.processByBrick(notate, notate.getSelectedIndex(), this);
      }
    if( useWindowsCheckbox.isSelected() )
      {
        if( !allMeasures )
          {
            melodyData = notate.getMelodyData(notate.getSelectedIndex());
          }

        int minMeasureWindow = Integer.parseInt(windowSizeField.getText());
        int maxMeasureWindow = Integer.parseInt(windowSizeField.getText());

        int beatsToSlide = Integer.parseInt(windowSlideField.getText());

        int selStart = notate.getCurrentSelectionStart();

        int selEnd = notate.getCurrentSelectionEnd();
        for( int measureWindow = minMeasureWindow; measureWindow <= maxMeasureWindow; measureWindow++ )
          {

            int slotsPerMeasure = BEAT;

            int slotsPerSection = slotsPerMeasure * measureWindow;

            int start = selStart - (selStart % slotsPerMeasure);

            int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure - 1;

            int numMeasures = (end + 1 - start) / slotsPerSection;

            //writeBeatsToSlide(beatsToSlide);
            //loop through places to start the measure window

            for( int window = 0; window < measureWindow; window += beatsToSlide )
              {
                //extract all sections of size measureWindow
                for( int i = 0;
                        (i * slotsPerSection) + (window * BEAT) + slotsPerSection <= (numMeasures) * slotsPerSection;
                        i++ )
                  {
                    String production = addMeasureToAbstractMelody(
                            start + (i * slotsPerSection) + (window * BEAT),
                            measureWindow,
                            i == 0,
                            true);
                    if( production != null )
                      {
                        writeProduction(production,
                                        measureWindow,
                                        (i * slotsPerSection) + (window * BEAT),
                                        true,
                                        null,
                                        -1);
                      }
                  }
              }
          }

        lickgen.loadGrammar(notate.getGrammarFileName());
        updateUseSoloist();
        Grammar g = lickgen.getGrammar();
        Polylist rules = g.getRules();

        ArrayList<Polylist> ruleList = new ArrayList<Polylist>();
        for( Polylist L = rules; L.nonEmpty(); L = L.rest() )
          {
            ruleList.add((Polylist) L.first());
          }
        Collections.sort(ruleList, new PolylistComparer());

        ArrayList<Polylist> newRules = new ArrayList<Polylist>();

        Polylist previous = Polylist.nil;
        float accumulatedProbability = 0;

        for( Iterator<Polylist> e = ruleList.iterator(); e.hasNext(); )
          {
            Polylist current = e.next();
            Object last = current.last();
            if( current.first().equals("rule") || current.first().equals("base") )
              {

                  if( (!previous.equals(Polylist.nil))
                        && current.allButLast().equals(previous.allButLast()) )
                  {
                    accumulatedProbability += ((Number) current.last()).floatValue();
                    int round = (int) (accumulatedProbability * 100);
                    accumulatedProbability = (float) (round / 100.0);
                  }
                else
                  {
                    if( previous.nonEmpty() )
                      {
                        newRules.add(Polylist.list(previous.first(),
                                                   previous.second(),
                                                   previous.third(),
                                                   accumulatedProbability));
                      }
                    

                    accumulatedProbability = ((Number) last).floatValue();

                    previous = current;
                  }
              }
            else
              {
                newRules.add(current);
              }
          }
        if( previous.nonEmpty() )
          {
            newRules.add(Polylist.list(previous.first(),
                                       previous.second(),
                                       previous.third(),
                                       accumulatedProbability));
          }

        try
          {
            File f1 = new File(notate.getGrammarFileName());
            if( f1.exists() )
              {
                System.gc();
                boolean deleted = f1.delete();
                while( !deleted )
                  {
                    deleted = f1.delete();
                  }
              }

            File f_out = new File(notate.getGrammarFileName());
            FileWriter out = new FileWriter(f_out, true);

            setLickGenStatus(
                    "Writing " + newRules.size() + " grammar rules to " + notate.getGrammarFileName());

            for( int i = 0; i < newRules.size(); i++ )
              {
                out.write(newRules.get(i).toString() + "\n");
              }
            out.close();

            notate.refreshGrammarEditor();

          }
        catch( Exception e )
          {
            System.out.println(e.getMessage());
          }
        //Enter the whole selection into the window
        int slotsPerMeasure = BEAT;
        int start = selStart - (selStart % slotsPerMeasure);
        int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure;
        int measureWindow = (end - start) / BEAT;

        String production = addMeasureToAbstractMelody(start,
                                                       measureWindow,
                                                       false,
                                                       true);

        if( production != null )
          {
            if( production.contains("STARTER") )
              {
                production = production.replace("STARTER", "");
              }
            if( production.contains("ENDTIED") )
              {
                production = production.replace("ENDTIED ", "");
              }
            if( production.contains("STARTTIED") )
              {
                production = production.replace("STARTTIED ", "");
              }
            if( production.contains("CHORDS") )
              {
                production = production.substring(0, production.indexOf("CHORDS"));
              }
            setRhythmFieldText(production);
          }
      }
  }

/**
 * Extract melody for purpose of display only, not creating productions. This
 * code was adapted from getAbstractMelody()
 */
public void extractAbstractMelody()
  {
    if( !allMeasures )
      {
        melodyData = notate.getMelodyData(notate.getSelectedIndex());
      }

    int minMeasureWindow = Integer.parseInt(windowSizeField.getText());
    int maxMeasureWindow = Integer.parseInt(windowSizeField.getText());
    int beatsToSlide = Integer.parseInt(windowSlideField.getText());

    int selStart = notate.getCurrentSelectionStart();

    int selEnd = notate.getCurrentSelectionEnd();

    for( int measureWindow = minMeasureWindow;
            measureWindow <= maxMeasureWindow;
            measureWindow++ )
      {
        //int slotsPerMeasure = score.getMetre()[0] * BEAT; //assume something/4 time

        int slotsPerMeasure = BEAT;

        int slotsPerSection = slotsPerMeasure * measureWindow;

        int start = selStart - (selStart % slotsPerMeasure);

        int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure - 1;

        int numMeasures = (end + 1 - start) / slotsPerSection;

        //loop through places to start the measure window
        for( int window = 0; window < measureWindow; window += beatsToSlide )
          {
            //extract all sections of size measureWindow
            for( int i = 0;
                    (i * slotsPerSection) + (window * BEAT) + slotsPerSection
                    <= (numMeasures) * slotsPerSection;
                    i++ )
              {
                String production = addMeasureToAbstractMelody(
                        start + (i * slotsPerSection) + (window * BEAT),
                        measureWindow,
                        i == 0,
                        false);
              }
          }
      }

    //Enter the whole selection into the window
    int slotsPerMeasure = BEAT;
    int start = selStart - (selStart % slotsPerMeasure);
    int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure;
    int measureWindow = (end - start) / BEAT;

    String production = addMeasureToAbstractMelody(start, measureWindow, false, false);

    if( production != null )
      {
        setRhythmFieldText(production);
      }
    
    MelodyPart segment = notate.getCurrentMelodyPart().extract(selStart, selEnd);
    ChordPart chords = notate.getChordProg().extract(selStart, selEnd);
    Polylist relativePitchMelody = NoteConverter.melodyPart2Relative(segment, chords, selStart);
    setRelativeFieldText(relativePitchMelody.toString());
  }

/**
 * add the production to file
 */
public String writeProduction(String production,
                            int measureWindow,
                            int location,
                            boolean writeExactMelody,
                            String brickType,
                            int chorus)
  {
    String finalProduction = null;

    
    if( chorus == -1 )
      { //didn't provide any information about what chorus we're on
        chorus = notate.getSelectedIndex();
      }

    if( !allMeasures )
      {
        melodyData = notate.getMelodyData(chorus);
      }

    if( production == null )
      {
        return finalProduction;
      }
    String chords = "";

    if( production.contains("CHORDS") )
      {
        chords = production.substring(production.indexOf("CHORDS"));
        production = production.substring(0, production.indexOf("CHORDS"));
      }

    try
      {
        BufferedWriter out;
        if( brickType != null )
          {
            out = new BufferedWriter(brickProductionsWriter);
          }
        else
          {
            out = new BufferedWriter(windowProductionsWriter);
          }

        if( !writeExactMelody )
          {
            out.write("(rule (Seg"
                    + measureWindow
                    + ") "
                    + production
                    + " ) "
                    + chords
                    + "\n");
          }
        else
          {
            //check that index of exact melody matches index of abstract melody
            //then concatenate the two and write them to the file
            int slotsPerSection = measureWindow * BEAT;
            String melodyToWrite;
            String relativePitchMelody;
            String exactMelody = production;
            String[] splitMel;
            boolean foundMatch = false;

            for( int i = 0; i < melodyData.size(); i++ )
              {
                splitMel = melodyData.get(i).split(" ");
                if( splitMel[0].equals(Integer.toString(location)) )
                  { //we've located our production's data in our melody data
                    exactMelody = melodyData.get(i); //the melody data is in small (1 measure) fixed-length strips
                    int j = i; //starting at this strip, we'll see if we need to add more strips
                    //(to match the length of our production)
                    int exactMelodyLength = 0;
                    if( j < melodyData.size() - 1 )
                      {
                        String[] splitNextMel = melodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        exactMelodyLength = nextSlot - location;
                      }
                    int thisSlot = location; //starting slot of the most recently examined strip of data
                    //while the total amount of melody data we've gone through is less than the amount of data in our production
                    while( j < melodyData.size() - 1 && exactMelodyLength < slotsPerSection )
                      {
                        String[] splitNextMel = melodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        exactMelodyLength += nextSlot - thisSlot; //we're adding another measure's worth of data onto our exact melody
                        for( int k = 1; k < splitNextMel.length; k++ )
                          { //tack on the melody data for the next measure-length strip to exact melody
                            exactMelody = exactMelody.concat(splitNextMel[k] + " ");
                          }
                        thisSlot = nextSlot;
                        j++;
                      }
                    foundMatch = true;
                    break;
                  }
              }
            if( foundMatch == false )
              {
                System.out.println("Weird. This shouldn't happen: " + location);
                System.out.println("Melody data: " + melodyData);
              }

            if( notate.getSelectedIndex() == 0 ) /*head*/

              {
                melodyToWrite = "Head " + exactMelody;
              }
            else
              {
                melodyToWrite = "Chorus" + (notate.getSelectedIndex() + 1) + " " + exactMelody;
              }

            ChordPart chordProg = notate.getChordProg().extract(location, location + slotsPerSection - 1);
            relativePitchMelody = NoteConverter.melStringToRelativePitch(slotsPerSection, chordProg, exactMelody);
            finalProduction = "(rule (Seg"
                    + measureWindow
                    + ") "
                    + production
                    + " ) "
                    + "(Xnotation "
                    + relativePitchMelody
                    + ") "
                    + "(Brick-type "
                    + brickType
                    + ") "
                    + melodyToWrite
                    + " "
                    + chords
                    + "\n";
            out.write(finalProduction);



          }
        out.close();
      }
    catch( IOException e )
      {
        System.out.println("IO EXCEPTION! " + e.toString());
      }        
    
    return finalProduction;
 
  }

public String writeProductionForBricks(String production,
                            int measureWindow,
                            int location,
                            boolean writeExactMelody,
                            String brickType,
                            int chorus)
  {
    String finalProduction = null;
 
   
    if( chorus == -1 )
      { //didn't provide any information about what chorus we're on
        chorus = notate.getSelectedIndex();
      }
    ArrayList<String> oneMeasureMelodyData = melodyData;
    if( !allMeasures )
      {
        oneMeasureMelodyData = notate.getOneMeasureMelodyData(chorus);
      }
 
    if( production == null )
      {
        return finalProduction;
      }
    String chords = "";
 
    if( production.contains("CHORDS") )
      {
        chords = production.substring(production.indexOf("CHORDS"));
        production = production.substring(0, production.indexOf("CHORDS"));
      }
 
    try
      {
        BufferedWriter out;
        if( brickType != null )
          {
            out = new BufferedWriter(brickProductionsWriter);
          }
        else
          {
            out = new BufferedWriter(windowProductionsWriter);
          }
 
        if( !writeExactMelody )
          {
            out.write("(rule (Seg"
                    + measureWindow
                    + ") "
                    + production
                    + " ) "
                    + chords
                    + "\n");
          }
        else
          {
            //check that index of exact melody matches index of abstract melody
            //then concatenate the two and write them to the file
            int slotsPerSection = measureWindow * BEAT;
            String melodyToWrite;
            String relativePitchMelody;
            String exactMelody = production;
            String[] splitMel;
            boolean foundMatch = false;
 
            for( int i = 0; i < oneMeasureMelodyData.size(); i++ )
              {
                splitMel = oneMeasureMelodyData.get(i).split(" ");
                if( splitMel[0].equals(Integer.toString(location)) )
                  { //we've located our production's data in our melody data
                    exactMelody = oneMeasureMelodyData.get(i); //the melody data is in small (1 measure) fixed-length strips
                    int j = i; //starting at this strip, we'll see if we need to add more strips
                    //(to match the length of our production)
                    int exactMelodyLength = 0;
                    if( j < oneMeasureMelodyData.size() - 1 )
                      {
                        String[] splitNextMel = oneMeasureMelodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        //System.out.println("nextSlot (is it 960?): "+nextSlot);
                        //System.out.println("location (is it 0?): "+nextSlot);
                        exactMelodyLength = nextSlot - location;
                        //System.out.println("exactMelodyLength: "+exactMelodyLength);
                      }
                    int thisSlot = location; //starting slot of the most recently examined strip of data
                    //while the total amount of melody data we've gone through is less than the amount of data in our production
                    while( j < oneMeasureMelodyData.size() - 1 && exactMelodyLength < slotsPerSection )
                      {
                        String[] splitNextMel = oneMeasureMelodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        exactMelodyLength += nextSlot - thisSlot; //we're adding another measure's worth of data onto our exact melody
                        for( int k = 1; k < splitNextMel.length; k++ )
                          { //tack on the melody data for the next measure-length strip to exact melody
                            exactMelody = exactMelody.concat(splitNextMel[k] + " ");
                          }
                        thisSlot = nextSlot;
                        j++;
                      }
                    foundMatch = true;
                    break;
                  }
              }
            if( foundMatch == false )
              {
                System.out.println("Weird. This shouldn't happen: " + location);
                System.out.println("Melody data: " + oneMeasureMelodyData);
              }
 
            if( notate.getSelectedIndex() == 0 ) /*head*/
 
              {
                melodyToWrite = "Head " + exactMelody;
              }
            else
              {
                melodyToWrite = "Chorus" + (notate.getSelectedIndex() + 1) + " " + exactMelody;
              }
 
            ChordPart chordProg = notate.getChordProg().extract(location, location + slotsPerSection - 1);
            relativePitchMelody = NoteConverter.melStringToRelativePitch(slotsPerSection, chordProg, exactMelody);
            finalProduction = "(rule (Seg"
                    + measureWindow
                    + ") "
                    + production
                    + " ) "
                    + "(Xnotation "
                    + relativePitchMelody
                    + ") "
                    + "(Brick-type "
                    + brickType
                    + ") "
                    + melodyToWrite
                    + " "
                    + chords
                    + "\n";
            out.write(finalProduction);
 
 
 
          }
        out.close();
      }
    catch( IOException e )
      {
        System.out.println("IO EXCEPTION! " + e.toString());
      }        
   
    return finalProduction;
 
  }

/**Method to write the user's data (stored in the My.rhythms file) to the outWriter,
 * which is used to create the grammar and clusters. Basically what makes us learn
 * a grammar with user data.
 * 
 * @throws IOException 
 */
private void writeUserRuleStrings() throws IOException{
    BufferedWriter out = new BufferedWriter(windowProductionsWriter);
    String userRhythmsFileName = Preferences.getPreference(Preferences.MY_RHYTHMS_FILE);
    String userRhythmsFilePath = ImproVisor.getRhythmClusterDirectory().toString() + "/" + userRhythmsFileName;
    ArrayList<Polylist> userRuleStringPL = UserRhythmSelecterDialog.readInRuleStringsFromFile(userRhythmsFilePath);
    for(int i = 0; i < userRuleStringPL.size(); i++){
        Polylist ruleStringPL = userRuleStringPL.get(i);
        //skip userRuleString tag
        ruleStringPL = (Polylist) ruleStringPL.rest();
        String ruleString = ruleStringPL.toStringSansParens();//add newline
        out.write(ruleString + "\n");
    }
    
    out.close();
    
}

/**Method to take get the exact melody String from a User Melody
 * used to create ruleString so that we can make a dataPoint for the user 
 * 
 * @param measureWindow - length of a measure in terms of beats
 * @param production - abstract Melody represented as string
 * @param location - start index of user's melody part in notate's leadsheet
 * @return the exactMelody String as represented in a ruleString
 */
public String getExactMelody(int measureWindow, String production, int location){
    int slotsPerSection = measureWindow * BEAT;
            //System.out.println("measureWindow: " + measureWindow + " Beat: " + BEAT + " slotsPerSection: " + slotsPerSection);
                   
            
            String melodyToWrite;
            String relativePitchMelody;
            String exactMelody = production;
            String[] splitMel;
            boolean foundMatch = false;

            
            melodyData = notate.getMelodyData(notate.getSelectedIndex());//melody data is all of the data for the ENTIRE leadsheet
            //We later find the melody data section corresponding to location

            for( int i = 0; i < melodyData.size(); i++ )
              {
                splitMel = melodyData.get(i).split(" ");
                
                
                if( splitMel[0].equals(Integer.toString(location)) )
                  { //we've located our production's data in our melody data
                    exactMelody = melodyData.get(i); //the melody data is in small (1 measure) fixed-length strips
                    int j = i; //starting at this strip, we'll see if we need to add more strips
                    //(to match the length of our production)
                    int exactMelodyLength = 0;
                    if( j < melodyData.size() - 1 )
                      {
                        String[] splitNextMel = melodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        exactMelodyLength = nextSlot - location;
                      }
                    int thisSlot = location; //starting slot of the most recently examined strip of data
                    
                    //while the total amount of melody data we've gone through is less than the amount of data in our production
                    while( j < melodyData.size() - 1 && exactMelodyLength < slotsPerSection )
                      {
                        String[] splitNextMel = melodyData.get(j + 1).split(" ");
                        int nextSlot = Integer.parseInt(splitNextMel[0]);
                        exactMelodyLength += nextSlot - thisSlot; //we're adding another measure's worth of data onto our exact melody
                        for( int k = 1; k < splitNextMel.length; k++ )
                          { //tack on the melody data for the next measure-length strip to exact melody
                            exactMelody = exactMelody.concat(splitNextMel[k] + " ");
                          }
                        thisSlot = nextSlot;
                        j++;
                      }
                    foundMatch = true;
                    break;
                  }
              }
            
            return exactMelody;
}

//add the production to the grammar file
public void addProduction(String production, int measureWindow, double prob) //formerly private
  {
    try
      {
        BufferedWriter out = new BufferedWriter(new FileWriter(
                notate.getGrammarFileName(), true));
        out.write(
                "(rule (Seg"
                + measureWindow
                + ") "
                + production
                + " "
                + prob
                + ") \n");
        out.close();
      }
    catch( IOException e )
      {
        System.out.println("IO EXCEPTION!" + e.toString());
      }
  }

 public String addMeasureToAbstractMelody(int selStart,
                                         int measureWindow,
                                         boolean isSongStart,
                                         boolean writeChords)
  {
    //int slotsPerMeasure = score.getMetre()[0] * BEAT; //assume something/4 time
    int slotsPerSection = BEAT * measureWindow;
    //boolean isSongStart = (selStart == 0);
    int selEnd = selStart + slotsPerSection;
    MelodyPart part = notate.getCurrentMelodyPart().copy();

    if( part.melodyIsEmpty(selStart, slotsPerSection) )
      {
        //if this is empty, the last measure is empty,
        //and the rest of the chorus is empty, return null
        if( part.getFreeSlotsFromEnd() >= (part.size() - selEnd)
                && part.melodyIsEmpty(selStart - slotsPerSection, slotsPerSection) )
          {
            return null;
          } //otherwise return a section of rests
        else
          {
            StringBuilder sb = new StringBuilder();
            Note n = new Note(72, 1);
            n.getDurationString(sb, slotsPerSection);
            String returnString = "((slope 0 0 R" + sb.substring(1) + "))";
            if( isSongStart )
              {
                returnString = returnString.concat("STARTER");
              }
            return returnString;
          }
      }

    int current = selStart;

    Polylist rhythmString = new Polylist();

    //pitches of notes in measure not including rests
    ArrayList<Integer> notes = new ArrayList<Integer>();

    boolean tiedAtStart = false, tiedAtEnd = false;

    //untie first note if it is tied from last measure
    if( part.getPrevNote(current) != null
            && part.getPrevNote(current).getRhythmValue() > current - part.getPrevIndex(current) )
      {

        tiedAtStart = true;

        //untie and set the previous note
        Note untiedNote = part.getPrevNote(current).copy();
        int originalRhythmVal = untiedNote.getRhythmValue();
        int rhythmVal = slotsPerSection - part.getPrevIndex(current) % slotsPerSection;
        untiedNote.setRhythmValue(rhythmVal);
        part.setNote(part.getPrevIndex(current), untiedNote);

        //set the current note
        rhythmVal = originalRhythmVal - rhythmVal;
        Note currNote = part.getPrevNote(current).copy();
        currNote.setRhythmValue(rhythmVal);
        part.setNote(current, currNote);
      }

    if( part.getPrevNote(selEnd) != null )
      {
        //untie notes at end of measure and beginning of next measure
        if( part.getPrevNote(selEnd).getRhythmValue() > selEnd - part.getPrevIndex(
                selEnd) )
          {
            tiedAtEnd = true;
            int tracker = part.getPrevIndex(selEnd);
            Note untiedNote = part.getNote(tracker).copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = slotsPerSection - (tracker % slotsPerSection);
            untiedNote.setRhythmValue(rhythmVal);
            part.setNote(tracker, untiedNote);
            int secondRhythmVal = originalRhythmVal - rhythmVal;
            untiedNote = part.getNote(tracker).copy();
            untiedNote.setRhythmValue(secondRhythmVal);
            part.setNote(selEnd, untiedNote);
          }
      }

    if( part.getPrevNote(selStart + 1) != null )
      {
        if( (part.getPrevIndex(selStart + 1) != selStart)
                && !(part.getPrevNote(selStart + 1).isRest()) )
          {
            return null;
          }
      }

    while( current < selEnd )
      {

        //if the current note is a null note, make it a rest
        if( part.getNote(current) == null )
          {
            int next = part.getNextIndex(current);
            Note n = Note.makeRest(next - current);
            part.setNote(current, n);
          }

        StringBuilder sb = new StringBuilder();

        //FIX: Suspicious that value is not used.
        int value = part.getNote(current).getDurationString(sb, part.getNote(
                current).getRhythmValue());

        int pitch = part.getNote(current).getPitch();

        if( part.getNote(current).isRest() )
          {
            rhythmString = rhythmString.cons("R" + sb.substring(1));
          }
        else
          {

            //add pitch to notes
            notes.add(pitch);
            //get note type
            char notetype;
            int[] notetone = lickgen.getNoteTypes(current, pitch, pitch,
                                                  notate.getChordProg());
            switch( notetone[0] )
              {
                case LickGen.CHORD:
                    notetype = 'C';
                    break;
                case LickGen.COLOR:
                    notetype = 'L';
                    break;
                default:
                    notetype = 'X';
                    break;
              }
            if( notetype == 'X' && part.getNextNote(current) != null )
              {

                int nextPitch = part.getNextNote(current).getPitch();
                int nextIndex = part.getNextIndex(current);
                if( nextIndex <= selEnd )
                  {
                    int pitchdiff = nextPitch - pitch;
                    if( Math.abs(pitchdiff) == 1 )
                      {
                        notetype = 'A';
                      }
                  }
              }
            rhythmString = rhythmString.cons(notetype + sb.substring(1));
          }

        current = part.getNextIndex(current);

      }

    rhythmString = rhythmString.reverse();

    //add in goal notes to the rhythmString

    //process intervals
    ArrayList<Integer> intervals = new ArrayList<Integer>();
    intervals.add(0);
    for( int i = 1; i < notes.size(); i++ )
      {
        intervals.add(notes.get(i) - notes.get(i - 1));
      }

    //process slopes
    ArrayList<int[]> slopes = new ArrayList<int[]>();
    int[] slope = new int[3];
    int tracker = 0;

    //get the slope from the note before this section to the first note in the measure
    int prevIndex = part.getPrevIndex(selStart);
    Note lastNote = part.getNote(prevIndex);
    while( lastNote != null && lastNote.isRest() )
      {
        prevIndex = part.getPrevIndex(prevIndex);
        lastNote = part.getNote(prevIndex);
      }
    int lastpitch = 0;
    if( lastNote != null && !lastNote.isRest() )
      {
        lastpitch = lastNote.getPitch();
      }
    int pitch = notes.get(0);
    int pitchChange;
    if( lastpitch == 0 )
      {
        pitchChange = 0;
      }
    else
      {
        pitchChange = pitch - lastpitch;
      }
    int minPitchChange = 0, maxPitchChange = 0;
    //avoid random notes and repeated notes
    if( pitchChange != 0 )
      {
        if( pitchChange == 1 )
          {
            minPitchChange = 1;
            maxPitchChange = 2;
          }
        else if( pitchChange == -1 )
          {
            minPitchChange = -2;
            maxPitchChange = -1;
          }
        else
          {
            minPitchChange = pitchChange - 1;
            maxPitchChange = pitchChange + 1;
          }
      }

    //if there is only 1 note, return it with its slope
    if( intervals.size() <= 1 )
      {

        String rhythm = rhythmString.toString();
        rhythm = rhythm.substring(1, rhythm.length() - 1);

        //handle case of only 1 note
        if( rhythm.equals("") )
          {
            char thisPitch = lickgen.getNoteType(selStart, notes.get(0), notes.get(
                    0), notate.getChordProg());
            String len = Note.getDurationString(slotsPerSection);
            rhythm = thisPitch + len;
          }
        String returnString = "((slope "
                + minPitchChange + " "
                + maxPitchChange + " "
                + rhythm + "))";
        if( isSongStart )
          {
            returnString = returnString.concat("STARTER");
          }
        if( tiedAtEnd )
          {
            returnString = returnString.concat(" ENDTIED");
          }
        if( tiedAtStart )
          {
            returnString = returnString.concat(" STARTTIED");
          }
        return returnString;
      }

    for( int i = 0; i < intervals.size(); i++ )
      {
        tracker = i;
        if( intervals.get(i) != 0 )
          {
            i = intervals.size();
          }
      }

    //direction is -1 if slope is going down, 0 for repeated note, 1 for up
    int direction = 0;
    if( intervals.get(tracker) > 0 )
      {
        direction = 1;
      }
    else if( intervals.get(tracker) < 0 )
      {
        direction = -1;
      }
    //initialize stuff - first note is in its own slope
    slope[0] = minPitchChange;
    slope[1] = maxPitchChange;
    slope[2] = 1;
    slopes.add(slope.clone());

    slope[0] = intervals.get(1);
    slope[1] = intervals.get(1);
    slope[2] = 0;
    for( int i = 1; i < intervals.size(); i++ )
      {
        //slope was going up but not any more
        if( direction == 1 && intervals.get(i) <= 0 )
          {
            if( intervals.get(i) == 0 )
              {
                direction = 0;
              }
            else
              {
                direction = -1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }

            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
            //slope was going down but not any more
          }
        else if( direction == -1 && intervals.get(i) >= 0 )
          {
            if( intervals.get(i) == 0 )
              {
                direction = 0;
              }
            else
              {
                direction = 1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
            //slope was 0 but not any more
          }
        else if( direction == 0 && intervals.get(i) != 0 )
          {
            if( intervals.get(i) > 0 )
              {
                direction = 1;
              }
            else
              {
                direction = -1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
          }
        else
          {
            slope[2]++;
            if( intervals.get(i) > slope[1] )
              {
                slope[1] = intervals.get(i);
              }
            if( intervals.get(i) < slope[0] )
              {
                slope[0] = intervals.get(i);
              }
          }

        if( i == intervals.size() - 1 )
          {
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
          }
      }

    //add in slopes
    StringBuilder strbuf = new StringBuilder();
    strbuf.append("(");
    Polylist tempString = rhythmString;
    for( int i = 0; i < slopes.size(); i++ )
      {
        slope = slopes.get(i);
        strbuf.append("(slope ");
        strbuf.append(slope[0]);
        strbuf.append(" ");
        strbuf.append(slope[1]);
        strbuf.append(" ");

        int j = 0;
        //get all of notes if last slope
        if( i == slopes.size() - 1 )
          {
            while( tempString.nonEmpty() )
              {
                strbuf.append(tempString.first().toString());
                strbuf.append(" ");
                tempString = tempString.rest();
              }
          }
        else
          {
            while( j < slope[2] )
              {
                String temp = tempString.first().toString();
                strbuf.append(temp);
                strbuf.append(" ");
                tempString = tempString.rest();
                if( temp.charAt(0) != 'R' )
                  {
                    j++;
                  }
              }
          }
        strbuf.deleteCharAt(strbuf.length() - 1);
        strbuf.append(")");
      }
    strbuf.append(")");
      //if we are writing to file, write the chords, start data, and tie data
      {

        //Mark measure as 'songStarter' if it is the first of a song
        if( isSongStart )
          {
            strbuf.append("STARTER");
          }
        if( writeChords )
          {
            strbuf.append("CHORDS ");

            ChordPart chords = notate.getChordProg()
                    .extract(selStart,
                             selStart + slotsPerSection - 1);
            ArrayList<Unit> chordList = chords.getUnitList();
            if( chordList.isEmpty() )
              {
                System.out.println("No chords");
              }
            for (Unit chordList1 : chordList) {
                String nextChord = ((Chord) chordList1).toLeadsheet();
                strbuf.append(nextChord);
                strbuf.append(" ");
            }
          }
      }

    return strbuf.toString();
    }//GEN-LAST:event_getAbstractMelodyButtonActionPerformed

    private void fillMelodyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fillMelodyButtonActionPerformed
    {//GEN-HEADEREND:event_fillMelodyButtonActionPerformed
        fillMelodyFromText(rhythmField.getText());
    }//GEN-LAST:event_fillMelodyButtonActionPerformed

/**
 * Fill an abstract or relative-pitch melody from text, such as acquired from
 * either abstract melody or relative pitch melody field.
 * If the text is not already a Polylist, this will first make a Polylist
 * out of it.
 *
 * @param r
 */
public void fillMelodyFromText(String r)
  {
    r = r.trim();
    if( r.equals("") )
      {
        return; // no text specified
      }
    if( r.charAt(0) != '(' )
      {
        r = "(".concat(r);
      }

    if( r.charAt(r.length() - 1) != ')' )
      {
        r = r.concat(")");
      }

    setRhythmFieldText(r);

    Polylist rhythm = new Polylist();
    StringReader rhythmReader = new StringReader(r);
    Tokenizer in = new Tokenizer(rhythmReader);
    Object ob;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
        if( ob instanceof Polylist )
          {
            rhythm = (Polylist) ob;
          }
      }

    notate.generateAndPutLick(rhythm);
  }

public MelodyPart fillAndReturnMelodyFromText(String r, ChordPart chordPart)
  {
    r = r.trim();
    if( r.equals("") )
      {
        return new MelodyPart(); // no text specified
      }
    if( r.charAt(0) != '(' )
      {
        r = "(".concat(r);
      }

    if( r.charAt(r.length() - 1) != ')' )
      {
        r = r.concat(")");
      }

    setRhythmFieldText(r);

    Polylist rhythm = new Polylist();
    StringReader rhythmReader = new StringReader(r);
    Tokenizer in = new Tokenizer(rhythmReader);
    Object ob;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
        if( ob instanceof Polylist )
          {
            rhythm = (Polylist) ob;
          }
      }

    return notate.generateLick(rhythm, chordPart);
  }

    private void genRhythmButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_genRhythmButtonActionPerformed
    {//GEN-HEADEREND:event_genRhythmButtonActionPerformed
        verifyTriageFields();

        if( useGrammar )
          {
            setRhythmFieldText(
                    lickgen.generateRhythmFromGrammar(0, notate.getTotalSlots()).toString());
          }
        else
          {
            setRhythmFieldText(lickgen.generateRandomRhythm(totalSlots,
                                                            minDuration,
                                                            maxDuration,
                                                             restProb).toString());
          }
        
        
    }//GEN-LAST:event_genRhythmButtonActionPerformed

    private void generateLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generateLickButtonActionPerformed
    {//GEN-HEADEREND:event_generateLickButtonActionPerformed
        notate.generateFromButton();
    }//GEN-LAST:event_generateLickButtonActionPerformed

    private void fillRelativePitchMelodyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fillRelativePitchMelodyButtonActionPerformed
    {//GEN-HEADEREND:event_fillRelativePitchMelodyButtonActionPerformed
       fillMelodyFromText(relativeField.getText());
    }//GEN-LAST:event_fillRelativePitchMelodyButtonActionPerformed

    private void chooseGrammarButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chooseGrammarButtonActionPerformed
    {//GEN-HEADEREND:event_chooseGrammarButtonActionPerformed
        notate.openGrammarMenuDialog();
        setGrammarName(notate.getGrammarName());
    }//GEN-LAST:event_chooseGrammarButtonActionPerformed

    private void useMotifsCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useMotifsCheckboxActionPerformed
        CreateGrammar.setMotifGrammarUse(useMotifsCheckbox.isSelected());
        useWindowsCheckbox.setSelected(useMotifsCheckbox.isSelected());
        useWindowsCheckbox.setEnabled(!useMotifsCheckbox.isSelected());
    }//GEN-LAST:event_useMotifsCheckboxActionPerformed

    private void useWindowsCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useWindowsCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useWindowsCheckboxActionPerformed

    private void motifnessSliderCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_motifnessSliderCaretPositionChanged
        // TODO add your handling code here:
    
    }//GEN-LAST:event_motifnessSliderCaretPositionChanged

    private void motifnessSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_motifnessSliderStateChanged
        // TODO add your handling code here:
        CreateGrammar.setMotifness(motifnessSlider.getValue() / (float) 100);
    }//GEN-LAST:event_motifnessSliderStateChanged

    private void userRhythmCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userRhythmCheckBoxActionPerformed

    }//GEN-LAST:event_userRhythmCheckBoxActionPerformed

    private void rhythmClusterCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rhythmClusterCheckboxActionPerformed
        if (rhythmClusterCheckbox.isSelected()){
            windowSizeField.setText("16");
            windowSlideField.setText("16");
            useBricksCheckbox.setSelected(false);
            useBricksCheckbox.setEnabled(false);
            useRelativeBricksCheckbox.setSelected(false);
            useRelativeBricksCheckbox.setEnabled(false);
            useAbstractBricksCheckbox.setSelected(false);
            useAbstractBricksCheckbox.setEnabled(false);
            useMotifsCheckbox.setSelected(false);
            windowSlideField.setEditable(false);

            windowSizeField.getDocument().addDocumentListener(windowSlideDocListener);

            //userRhythmCheckBox.setVisible(true);
        }else{
            useBricksCheckbox.setSelected(true);
            useBricksCheckbox.setEnabled(true);
            useRelativeBricksCheckbox.setSelected(true);
            useRelativeBricksCheckbox.setEnabled(true);
            useAbstractBricksCheckbox.setSelected(true);
            useAbstractBricksCheckbox.setEnabled(true);
            useMotifsCheckbox.setSelected(true);
            windowSlideField.setEditable(true);

            windowSizeField.getDocument().removeDocumentListener(windowSlideDocListener);
            //userRhythmCheckBox.setVisible(false);
        }
    }//GEN-LAST:event_rhythmClusterCheckboxActionPerformed

    private void motifnessGenerationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_motifnessGenerationSliderStateChanged
        
        double motifProbability     = motifnessGenerationSlider.getValue() / 100.0;
        double notMotifProbability  = 1.0 - motifProbability;
        
//        System.err.println("Motif Probability: " + Y);
        
        
        String useMotifs = "UseMotif";
        
//        System.out.println("Grammar File Name: " + notate.getGrammarFileName());
        
        Grammar g = new Grammar(notate.getGrammarFileName());
        
//        System.out.println("About to load grammar");
        g.loadGrammar(notate.getGrammarFileName());
        
//        System.out.println(g.getRules());
//        System.out.println("loaded grammar");
        Polylist rules = g.getRules();
        Polylist finalRules = Polylist.nil;
        
        for(Polylist R = rules; R.nonEmpty(); R = R.rest()){
//            System.out.println(R.flatten());
            if(((Polylist) R.first()).member(Polylist.list("P_motif")) && ((Polylist) R.first()).flatten().member(useMotifs)){
                System.out.println("Changed motif probability...");
                R.setFirst(((Polylist) R.first()).replaceLast(motifProbability));
            } else if (((Polylist) R.first()).member(Polylist.list("P_motif")) && ((Polylist) R.first()).flatten().member("P")) {
                System.out.println("Changed normal grammar probability...");
                R.setFirst(((Polylist) R.first()).replaceLast(notMotifProbability));
            }
            
            finalRules.addToEnd(R.first());
            
        }
        
        g.saveGrammar(notate.getGrammarFileName());
//        System.out.println("saved grammar");
        notate.setGrammar(notate.getGrammarName());
//        System.out.println("reloaded grammar");

    }//GEN-LAST:event_motifnessGenerationSliderStateChanged
                                             
    private void setRhythmClusterFilenameInPreferences(String rhythmClusterFilename){
        Preferences.setPreference(Preferences.CLUSTER_FILENAME, rhythmClusterFilename);
    }

    
public void setGrammarName(String grammarName)
{
    currentGrammarLabel.setText("Grammar: " + grammarName);
}

private void updateUseSoloist()
  {
    if( useSoloistCheckBox.isSelected() && lickgen.soloistIsLoaded() )
      {
        setLickGenStatus("Using Soloist file");
      }
    else
      {
        useSoloistCheckBox.setSelected(false);
        setLickGenStatus("Non-Matching Soloist file or No Soloist file exists");
      }
  }

    // Return min duration text field
    public int getMinDuration() {
        return minDuration;
    }

    // Return max duration text field
    public int getMaxDuration() {
        return maxDuration;
    }

    // Return rest prob
    public double getRestProb() {
        return restProb;
    }

    // Return critic
    public Critic getCritic() {
        return critic;
    }

    // Return if the critic is selected and should be used
    public boolean useCritic() {
        return useCritic;
    }

    // Returns the lower-limit grade for the critic filter
    public int getCriticGradeThreshold() {
        return criticGradeThreshold;
    }

    // Sets the counter for the number of generations of licks
    public void setCounterForCriticTextField(int count) {
        counterForCriticTextField.setText(String.valueOf(count));
    }

    // Sets the name for the lick generator name text field
    public void setSaveLickTextField(String text) {
        saveLickTF.setText(text);
    }

    // Sets the grade text field with a given grade
    public void setLickFromStaveGradeTextField(Double grade) {
        lickFromStaveGradeTextField.setText(String.format("%.3f", grade));
    }

    // Sets the neural network output text field
    public void setNetworkOutputTextField(String text) {
        nnetOutputTextField.setText(text);
        nnetOutputTextField.setCaretPosition(nnetOutputTextField.getText().length());
    }

    // Appends text to the neural network output text field
    public void appendNetworkOutputTextField(String text) {
        String currentText = nnetOutputTextField.getText();
        nnetOutputTextField.setText(currentText + text);
        nnetOutputTextField.setCaretPosition(nnetOutputTextField.getText().length());
    }

    // Changes if we are sending licks to the critic panel
    public void setToCriticDialog(boolean bool) {
        toCriticMI1.setSelected(bool);
    }

    // Re-number all rows, reseting the index of each row
    private void resetIndexColumn(DefaultTableModel model) {
        for (int i = 0; i < layerDataTable.getRowCount(); i++) {
            model.setValueAt(new Integer(i + 1), i, 0);
        }
    }

    // Cleanly print a musician's name
    private String fixName(String name) {
        int pos = 0;
        char[] chars = name.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            pos += Character.isUpperCase(chars[i]) ? i : 0;
        }

        // Display in format "Firstname Lastname"
        name = name.substring(0, 1).toUpperCase()
                + name.substring(1, pos)
                + " "
                + name.substring(pos);
        return name;
    }

    /**
     * Checks how many beats are selected in the current leadsheet.
     */
    public void verifyBeats() {
        totalBeats = Notate.doubleFromTextField(totalBeatsField, 0.0,
                Double.POSITIVE_INFINITY, 0.0);
        totalBeats = Math.round(totalBeats);
        totalSlots = (int) (BEAT * totalBeats);
        notate.getCurrentStave().repaint();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FillProbsButton;
    private javax.swing.JTextField MarkovLengthField;
    private javax.swing.JPanel ProbFillClearPanel;
    private javax.swing.JButton addLayerToTableButton;
    private javax.swing.JCheckBox approachBox;
    private javax.swing.JCheckBox autoFillCheckBox;
    private javax.swing.JCheckBox avoidRepeatsCheckbox;
    private javax.swing.JButton backwardGradeSoloButton;
    private javax.swing.JMenuItem cascadeMI2;
    private javax.swing.JButton chooseGrammarButton;
    private javax.swing.JCheckBox chordBox;
    private javax.swing.JPanel chordProbPanel;
    private javax.swing.JTextField chordToneDecayField;
    private javax.swing.JLabel chordToneDecayRateLabel;
    private javax.swing.JLabel chordToneProbLabel;
    private javax.swing.JTextField chordToneWeightField;
    private javax.swing.JButton clearProbsButton;
    private javax.swing.JButton clearWeightFileButton;
    private javax.swing.JMenuItem closeWindowMI2;
    private javax.swing.JCheckBox colorBox;
    private javax.swing.JLabel colorToneProbLabel;
    private javax.swing.JTextField colorToneWeightField;
    private javax.swing.JCheckBox continuallyGenerateCheckBox;
    private javax.swing.JLabel counterForCriticLabel;
    private javax.swing.JTextField counterForCriticTextField;
    private javax.swing.JLabel criticGradeLabel;
    private javax.swing.JTextField criticGradeTextField;
    private javax.swing.JLabel currentGrammarLabel;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JMenuItem editGrammarMI1;
    private javax.swing.JRadioButton emptyBaseLearningButton;
    private javax.swing.JRadioButton emptyMotifBaseLearningButton;
    private javax.swing.JLabel epochLimitLabel;
    private javax.swing.JTextField epochLimitTextField;
    private javax.swing.JButton fillMelodyButton;
    private javax.swing.JButton fillRelativePitchMelodyButton;
    private javax.swing.JLabel finalLabel;
    private javax.swing.JButton forwardGradeSoloButton;
    private javax.swing.JTextField gapField;
    private javax.swing.JButton genRhythmButton;
    private javax.swing.JButton generateLickButton;
    private javax.swing.JButton generateWeightFileButton;
    private javax.swing.JLabel generationGapLabel;
    private javax.swing.JButton generationSelectionButton;
    private javax.swing.JMenuBar generatorMenuBar1;
    private javax.swing.JTabbedPane generatorPane;
    private javax.swing.JMenu generatorWindowMenu1;
    private javax.swing.JButton getAbstractMelodyButton;
    private javax.swing.JButton getNetworkStatsButton;
    private javax.swing.JButton getSelRhythmButton;
    private javax.swing.JButton grade10Btn;
    private javax.swing.JButton grade1Btn;
    private javax.swing.JButton grade2Btn;
    private javax.swing.JButton grade3Btn;
    private javax.swing.JButton grade4Btn;
    private javax.swing.JButton grade5Btn;
    private javax.swing.JButton grade6Btn;
    private javax.swing.JButton grade7Btn;
    private javax.swing.JButton grade8Btn;
    private javax.swing.JButton grade9Btn;
    private javax.swing.JButton gradeAllMeasuresButton;
    private javax.swing.JButton gradeAverageBtn;
    private javax.swing.JButton gradeBadBtn;
    private javax.swing.JButton gradeGoodBtn;
    private javax.swing.JLabel gradeLabel;
    private javax.swing.JButton gradeLickFromStaveButton;
    private javax.swing.JPanel grammarLearningPanel;
    private javax.swing.JMenu grammarMenu1;
    private javax.swing.JLabel intervalLabel;
    private javax.swing.JTable layerDataTable;
    private javax.swing.JScrollPane layerInfoScrollPane;
    private javax.swing.JTextField leapProbField;
    private javax.swing.JLabel leapProbLabel;
    private javax.swing.ButtonGroup learningBaseButtonGroup;
    private javax.swing.JLabel learningRateLabel;
    private javax.swing.JTextField learningRateTextField;
    private javax.swing.JLabel learningStep0Label;
    private javax.swing.JTextField lickFromStaveGradeTextField;
    private javax.swing.JPanel lickGenPanel;
    private javax.swing.JPanel lickGenerationButtonsPanel;
    private javax.swing.JPanel lickGradeButtonsPanel;
    private javax.swing.JPanel lickgenParametersPanel;
    private javax.swing.JButton loadRandomGrammarButton;
    private javax.swing.JButton loadWeightFileButton;
    private javax.swing.JLabel markovChainLengthLabel;
    private javax.swing.JTextField maxDurationField;
    private javax.swing.JTextField maxIntervalField;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JTextField maxPitchField;
    private javax.swing.JTextField minDurationField;
    private javax.swing.JTextField minIntervalField;
    private javax.swing.JLabel minLabel;
    private javax.swing.JTextField minPitchField;
    private javax.swing.JComboBox modeComboBox;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JPanel motifParametersPanel;
    private javax.swing.JPanel motifnessGenerationPanel;
    private javax.swing.JSlider motifnessGenerationSlider;
    private javax.swing.JSlider motifnessSlider;
    private javax.swing.JButton moveLayerDownTableButton;
    private javax.swing.JButton moveLayerUpTableButton;
    private javax.swing.JLabel mseGoalLabel;
    private javax.swing.JTextField mseGoalTextField;
    private javax.swing.JPanel neuralNetworkPanel;
    private javax.swing.JPanel nnetOutputPanel;
    private javax.swing.JTextArea nnetOutputTextField;
    private javax.swing.JPanel nnetParametersPanel;
    private javax.swing.JScrollPane nnetScrollPane;
    private javax.swing.JPanel nnetWeightGenerationPanel;
    private javax.swing.JTextField numClusterRepsField;
    private javax.swing.JLabel numClusterRepsLabel;
    private javax.swing.JLabel numberOfLayersLabel;
    private javax.swing.JTextField numberOfLayersTextField;
    private javax.swing.JButton offsetByMeasureGradeSoloButton;
    private javax.swing.JButton openCorpusBtn;
    private javax.swing.JMenuItem openGrammarMI1;
    private javax.swing.JLabel pitchLabel;
    private javax.swing.JPanel pitchProbabilitiesPanel;
    private javax.swing.JButton playLickButton;
    private javax.swing.JCheckBox rectifyCheckBox;
    private javax.swing.JPanel rectifyPanel;
    private javax.swing.JCheckBox recurrentCheckbox;
    private javax.swing.JButton regenerateHeadDataBtn;
    private javax.swing.JButton regenerateLickForSoloButton;
    private javax.swing.JTextArea relativeField;
    private javax.swing.JPanel relativePanel;
    private javax.swing.JScrollPane relativeScrollPane;
    private javax.swing.JMenuItem reloadGrammarMI1;
    private javax.swing.JButton removeLayerFromTableButton;
    private javax.swing.JButton resetDefaultValuesButton;
    private javax.swing.JButton resetNetworkButton;
    private javax.swing.JButton resetNnetInstructionsButton;
    private javax.swing.JButton resetSelectionButton;
    private javax.swing.JTextField restProbField;
    private javax.swing.JLabel restProbLabel;
    private javax.swing.JCheckBox rhythmClusterCheckbox;
    private javax.swing.JPanel rhythmClusterPanel;
    private javax.swing.JTextArea rhythmField;
    private javax.swing.JPanel rhythmPanel;
    private javax.swing.JScrollPane rhythmScrollPane;
    private javax.swing.JComboBox rootComboBox;
    private javax.swing.JLabel rootLabel;
    private javax.swing.JButton saveGrammarAsButton;
    private javax.swing.JMenuItem saveGrammarMI1;
    private javax.swing.JButton saveLickButton;
    private javax.swing.JTextField saveLickTF;
    private javax.swing.JLabel saveLickWithLabelLabel;
    private javax.swing.JPanel scaleChoicePanel;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JLabel scaleToneProbLabel;
    private javax.swing.JTextField scaleToneWeightField;
    private javax.swing.JRadioButton selectBaseLearningButton;
    private javax.swing.JMenuItem showCriticMI1;
    private javax.swing.JMenuItem showLogMI1;
    private javax.swing.JPanel soloCorrectionPanel;
    private javax.swing.JButton stopLickButton;
    private javax.swing.JButton styleRecognitionButton;
    private javax.swing.JButton testGeneration;
    private javax.swing.JCheckBoxMenuItem toCriticMI1;
    private javax.swing.JButton toGrammarBtn;
    private javax.swing.JPanel toneProbabilityPanel;
    private javax.swing.JPanel topGrammarLearningPanel;
    private javax.swing.JTextField totalBeatsField;
    private javax.swing.JLabel totalBeatsLabel;
    private javax.swing.JButton trainingFileButton;
    private javax.swing.JTextField trainingFileTextField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JCheckBox useAbstractBricksCheckbox;
    private javax.swing.JCheckBox useAbstractWindowsCheckbox;
    private javax.swing.JCheckBox useBricksCheckbox;
    private javax.swing.JCheckBox useCriticCheckBox;
    private javax.swing.JCheckBoxMenuItem useGrammarMI1;
    private javax.swing.JCheckBox useHeadCheckBox;
    private javax.swing.JCheckBox useMarkovCheckbox;
    private javax.swing.JCheckBox useMotifsCheckbox;
    private javax.swing.JCheckBox useRelativeBricksCheckbox;
    private javax.swing.JCheckBox useRelativeWindowsCheckbox;
    private javax.swing.JCheckBox useSoloistCheckBox;
    private javax.swing.JCheckBox useWindowsCheckbox;
    private javax.swing.JCheckBox userRhythmCheckBox;
    private javax.swing.JButton weightFileButton;
    private javax.swing.JTextField weightFileTextField;
    private javax.swing.JSeparator windowMenuSeparator2;
    private javax.swing.JPanel windowParametersPanel;
    private javax.swing.JTextField windowSizeField;
    private javax.swing.JLabel windowSizeLabel;
    private javax.swing.JTextField windowSlideField;
    private javax.swing.JLabel windowSlideLabel;
    // End of variables declaration//GEN-END:variables

    private void triageLick(String lickName, int grade) {
        notate.triageLick(lickName, grade);
    }

    public boolean getRecurrent() {
        return recurrentCheckbox.isSelected();
    }

    public void setRecurrent(boolean value) {
        recurrentCheckbox.setSelected(value);
        notate.setRecurrent(value);
    }

    public void setTotalBeats(double beats) {
        totalBeats = beats;
        totalBeatsField.setText("" + beats);
        String b = Integer.toString((int) beats);

    }
    
    public void setUseRelative(boolean value) {
        useRelativeWindowsCheckbox.setSelected(value);
    }
    
    public void setUseBricks(boolean value) {
        useBricksCheckbox.setSelected(value);
    }
    
    public void setUseMarkov(boolean value) {
        useMarkovCheckbox.setSelected(value);
    }
    
    public void setUseWindows(boolean value ) {
        //useWindowsCheckbox.setSelected(value);
        useWindowsCheckbox.setSelected(value);
    }
    
    public void setUseMotifs(boolean value){
        useMotifsCheckbox.setSelected(value);
    }

    public boolean toCriticSelected() {
        return toCriticMI1.isSelected();
    }

    public boolean useGrammarSelected() {
        return useGrammarMI1.isSelected();
    }

    public boolean rectifySelected() {
        return rectifyCheckBox.isSelected();
    }
    
    public boolean [] getRectifyOptions(){
        boolean [] options = 
          {chordBox.isSelected(), 
           colorBox.isSelected(), 
           approachBox.isSelected(),
           avoidRepeats};
        return options;
    }

    public boolean useCriticSelected() {
        return useCriticCheckBox.isSelected();
    }

    public boolean useHeadSelected() {
        return useHeadCheckBox.isSelected();
    }

    public boolean useSoloistSelected() {
        return useSoloistCheckBox.isSelected();
    }

    public int getGap() {
        return (int) (BEAT * Notate.quietDoubleFromTextField(gapField, -Double.MAX_VALUE,
                +Double.MAX_VALUE, 0));
    }

    public void setGap(double value) {
        gapField.setText("" + value);
    }
    
    public StringWriter getBrickProductionsWriter() {
        return brickProductionsWriter;
    }
    
    public StringWriter getWindowProductionsWriter() {
        return windowProductionsWriter;
    }
    
    public boolean getUseWindows() {
        return useWindowsCheckbox.isSelected();
    }

    public int getWindowSize() {
        return Integer.parseInt(windowSizeField.getText());
    }

    public int getWindowSlide() {
        return Integer.parseInt(windowSlideField.getText());
    }

    public int getNumClusterReps() {
        return Integer.parseInt(numClusterRepsField.getText());
    }

    public int getMarkovFieldLength() {
        return Integer.parseInt(MarkovLengthField.getText());
    }

    public boolean getUseRelativeWindows() {
        return useRelativeWindowsCheckbox.isSelected();
    }
    
    public boolean getUseAbstractWindows() {
        return useAbstractWindowsCheckbox.isSelected();
    }
    
    public boolean getUseMarkov() {
        return useMarkovCheckbox.isSelected();
    }
    
    public boolean getUseBricks() {
        if(rhythmClusterCheckbox.isSelected()){
            return false;
        }
        return useBricksCheckbox.isSelected();
    }
    
    public boolean getUseRelativeBricks() {
        return useRelativeBricksCheckbox.isSelected();
    }
    
    public boolean getUseAbstractBricks() {
        return useAbstractBricksCheckbox.isSelected();
    }
   

public void redoScales()
  {
    DefaultComboBoxModel dcbm = (DefaultComboBoxModel) scaleComboBox.getModel();

    dcbm.removeAllElements();

    Polylist scales = Advisor.getAllScales();

    dcbm.addElement("None");

    dcbm.addElement(FIRST_SCALE);

    while( scales.nonEmpty() )
      {
        Polylist scale = (Polylist) scales.first();

        dcbm.addElement(scale.first());

        scales = scales.rest();
      }
  }


    public void toGrammar() {
        String outFile = notate.getGrammarFileName();

        File f = new File(outFile);
        MetricListFactory metricListFactory;
        setLickGenStatus("Writing productions to grammar file: " + outFile);
        if(rhythmClusterCheckbox.isSelected()){
            metricListFactory = new RhythmMetricListFactory();
        }else{
            metricListFactory = new DefaultMetricListFactory();
        }
        
        if (getUseBricks()) {
            imp.generalCluster.CreateBrickGrammar.create(notate.getChordProg(),
                    //inFile,
                    brickProductionsWriter,
                    outFile,
                    getNumClusterReps(),
                    getUseRelativeBricks(),
                    getUseAbstractBricks(),
                    notate,
                    metricListFactory);
            brickProductionsWriter.getBuffer().setLength(0); //reset for next usage
        } 
        if (getUseWindows()) {
            try {
                
               if(rhythmClusterCheckbox.isSelected()){
                   imp.generalCluster.CreateGrammar.setCreateClusterFileFlag(true);
                   imp.generalCluster.CreateGrammar.setClusterWindowSize(getWindowSize());
               }else{
                   imp.generalCluster.CreateGrammar.setCreateClusterFileFlag(false);
               }
               
                           
                    
        
                if(userRhythmCheckBox.isSelected()){
                    try {
                        writeUserRuleStrings();
         
                    } catch (IOException ex) {
                        Logger.getLogger(LickgenFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                
                imp.generalCluster.CreateGrammar.create(notate.getChordProg(),
                    //inFile,
                    windowProductionsWriter,
                    outFile,
                    getNumClusterReps(),
                    getUseMarkov(),
                    getMarkovFieldLength(),
                    getUseRelativeWindows(),
                    getUseAbstractWindows(),
                    notate,
                    metricListFactory);
            } catch (IOException ex) {
                Logger.getLogger(LickgenFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            windowProductionsWriter.getBuffer().setLength(0); //reset for next usage
        }
        setLickGenStatus("Done writing productions to grammar file: " + outFile);

        notate.refreshGrammarEditor();
    }
    
private static final LogDialog logDialog = new LogDialog(false);

public void openLog()
  {
    logDialog.setVisible(true);
  }

public void setLickGenStatus(String string)
  {
    if( logDialog != null )
      {
        logDialog.append(string + "\n");
      }
  }

public void openGrammar(String fileName)
  {
    File directory = ImproVisor.getGrammarDirectory();
    
    File grammarFile = new File(directory, fileName);

    lickgen.loadGrammar(grammarFile.getAbsolutePath());
  }

private void initializeGrammarLearning()
{
    if( emptyBaseLearningButton.isSelected() )
    {
        openGrammar("_empty.grammar");
    }
    else if ( emptyMotifBaseLearningButton.isSelected() )
    {
        openGrammar("_emptyMotif.grammar");
    }
    else
    {
        notate.openGrammar();
    }
  notate.saveGrammarAs();
}

/**
 * Document Listener for changes to window size, used to change window slide property
 * so that slide and size are always the same for cluster generation
 */
    private void setUpWindowSlideDocListener() {
        windowSlideDocListener = new DocumentListener(){
                  @Override
                  public void insertUpdate(DocumentEvent e) {
                      windowSlideField.setText(windowSizeField.getText());
                    }

                  @Override
                  public void removeUpdate(DocumentEvent e) {
                      windowSlideField.setText(windowSizeField.getText());                  
                  }

                  @Override
                  public void changedUpdate(DocumentEvent e) {
                      windowSlideField.setText(windowSizeField.getText());
                  }
                    
               };
    }




}
