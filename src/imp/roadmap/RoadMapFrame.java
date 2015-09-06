/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011-2015 Robert Keller and Harvey Mudd College
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110z-1301  USA
 */

package imp.roadmap;

import imp.ImproVisor;
import imp.brickdictionary.Block;
import imp.brickdictionary.Brick;
import imp.brickdictionary.BrickLibrary;
import imp.brickdictionary.ChordBlock;
import imp.cykparser.CYKParser;
import imp.data.*;
import imp.gui.*;
import imp.util.DictionaryFilter;
import imp.util.ErrorLog;
import imp.util.FileUtilities;
import imp.util.MidiPlayListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import polya.Tokenizer;


/** The main roadmap window. This class deals with user interaction as well as
 * interaction with other parts of improvisor.
 * @author August Toman-Yih
 */

public class RoadMapFrame extends javax.swing.JFrame implements MidiPlayListener {

    public static final String DICTIONARY_EXT = ".dictionary";

    private int keyColorationOffset = 0;
    
    private String defaultDictionaryName = "My";
    
    private String dictionaryNameSuffix = " dictionary";
    
    JCheckBoxMenuItem recentlySelected = null;
    
    //private String dictionaryDir = Directories.dictionaryDirName + File.separator;
    
    private String dictionaryFilename;
    
    private String dictionaryName;
    
     /** Communication with leadsheet and score is done through Notate frame. */
    private Notate notate = null;
    
    /** auxNotate is a separate notate window for converting the roadmap
     * to a leadsheet. */
    private Notate auxNotate = null;;
    
    /** Buffer for the preview panel  */
    private Image bufferPreviewPanel;
    
    /** Width of the preview buffer */
    private final int previewBufferWidth  = 2400;
    
    /** Height of the preview buffer */
    private final int previewBufferHeight = 100;

    /** Buffer for the roadmap panel */
    private Image bufferRoadMap;  
    
    /** Width of the roadmap buffer */
    private final int roadMapBufferWidth  = 2400;
    
    /** Height of the roadmap buffer */
    private final int roadMapBufferHeight = 2400;
    
    /** Dictionary frame location, relative to the roadmap panel */
    private final int dictionaryFrameX = 910;
    private final int dictionaryFrameY = 350;
    
    /** Panel for previewing bricks from the library */
    private PreviewPanel previewPanel;
    
    /** Panel where the roadmap is drawn */
    private RoadMapPanel roadMapPanel;
    
    /** Library of available bricks */
    private BrickLibrary brickLibrary;
    
    /** Parser for chord analysis */
    private CYKParser cykParser = new CYKParser();
    
    /** When bricks are dragged, they are removed from the roadmap and store here */
    private ArrayList<GraphicBrick> draggedBricks = new ArrayList();
    
    /** Stores copied bricks */
    private static ArrayList<Block> clipboard = new ArrayList();
    
    /** Choices in the duration combobox */
    private Object[] durationChoices = {8,7,6,5,4,3,2,1};
    
    /** Combo box model for choosing styles */
    private Notate.StyleComboBoxModel styleComboBoxModel = new Notate.StyleComboBoxModel();
    
    /** Default width of the roadmap frame */
    private int RMframeWidth = 1315;
    
    /** Playback status */
    private MidiPlayListener.Status isPlaying = MidiPlayListener.Status.STOPPED;
    
    /** This timer provides updates to the roadmap panel during playback */
    private javax.swing.Timer playTimer;
    
    /** Tree used to store the brick library */
    private DefaultTreeModel libraryTreeModel;
    
    /** Graphical settings are stored here */
    private RoadMapSettings settings = new RoadMapSettings();
    
    /** Actions that can be undone */
    private LinkedList<RoadMapSnapShot> roadMapHistory = new LinkedList();
    
    /** Actions that can be redone */
    private LinkedList<RoadMapSnapShot> roadMapFuture = new LinkedList();
    
    /** Prefix on the frame title */
    private static String roadMapTitlePrefix = "RoadMap: ";
    
    /** Title for feature width */
    private static String featureWidthTitle = "Width";
    
    /** Suffix for constrained feature width */
    private static String featureWidthSuffix = "(Constrained)";
    
    /** Title of this piece */
    public String roadMapTitle = "Untitled";
    
    /** Default style name */
    public String styleName = "swing";
    
    
    /** Style of this piece */
    public Style style = Advisor.getStyle(styleName);
        
    /** Time signature of this piece */
    public int[] metre = {4,4};
    
    private SourceEditorDialog dictionaryEditor = null;
    
    private static int DICTIONARY_EDITOR_ROWS = 3000;
    private static int DICTIONARY_EDITOR_WIDTH = 700;
    private static int DICTIONARY_EDITOR_HEIGHT = 900;
    private static int DICTIONARY_EDITOR_X_OFFSET = 200;
    private static int DICTIONARY_EDITOR_Y_OFFSET = 200;
    
    FileDialog saveAWT = new FileDialog(this, "Save Dictionary As...", FileDialog.SAVE);
    
    public static final String INVISIBLE = "Invisible";
    
    private boolean jSliderIgnoreStateChangedEvt = false;
    
    private ArrayList<String> styleNames = new ArrayList<String>();
  /**
   *
   * The file chooser for opening the dictionary
   *
   */
    
  private JFileChooser dictionaryfc = new JFileChooser();;

  public int roadMapTransposition = 0;
   
    private RoadMapFrame() {} // Not for you.
    
    /** Creates new form RoadMapFrame */
    public RoadMapFrame(Notate notate) 
      {
        this(notate, notate.getTitle());
      }

    /** Creates new form RoadMapFrame, with a different title */
    public RoadMapFrame(Notate notate, String title) 
      {
        this.notate = notate;
        
        previewPanel = new PreviewPanel(this);
        roadMapPanel = new RoadMapPanel(this);
                
        initComponents(); // Must be done before newDictionary is called.
        
        setDictionaryFilename(defaultDictionaryName);
        newDictionary();
        
        initBuffer();
        
        initTimer();
        
        deactivateButtons();

        setRoadMapTitle(title);

        roadMapScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        brickDictionaryFrame.setSize(brickDictionaryFrame.getPreferredSize());
        
        brickDictionaryFrame.setLocationRelativeTo(roadMapPanel);
    
        brickDictionaryFrame.setLocation(dictionaryFrameX, dictionaryFrameY);
        
        WindowRegistry.registerWindow(this);
        
        setFeatureWidthLocked(true);
        
        setStyle(style);
        
        //settings.generateColors(.3f);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        addBrickDialog = new javax.swing.JDialog();
        dialogNameLabel = new javax.swing.JLabel();
        dialogKeyLabel = new javax.swing.JLabel();
        dialogNameField = new javax.swing.JTextField();
        dialogAcceptButton = new javax.swing.JButton();
        dialogCancelButton = new javax.swing.JButton();
        dialogModeComboBox = new javax.swing.JComboBox();
        dialogKeyComboBox = new javax.swing.JComboBox();
        dialogTypeComboBox = new javax.swing.JComboBox();
        dialogTypeLabel = new javax.swing.JLabel();
        dialogVariantLabel = new javax.swing.JLabel();
        dialogVariantField = new javax.swing.JTextField();
        chordChangeDialog = new javax.swing.JDialog();
        chordDialogNameField = new javax.swing.JTextField();
        chordDialogAcceptButton = new javax.swing.JButton();
        chordDialogDurationComboBox = new javax.swing.JComboBox(durationChoices);
        preferencesDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        prefDialogCancelButton = new javax.swing.JButton();
        prefDialogAcceptButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        prefDialogMeterLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        prefDialogTitleField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        upperMetre = new javax.swing.JTextField();
        lowerMetre = new javax.swing.JTextField();
        brickDictionaryFrame = new javax.swing.JFrame();
        libraryScrollPane = new javax.swing.JScrollPane();
        libraryTree = new javax.swing.JTree();
        keyComboBox = new javax.swing.JComboBox();
        deleteButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        durationComboBox = new javax.swing.JComboBox(durationChoices);
        colorationPreferencesButtonGroup = new javax.swing.ButtonGroup();
        romanNumeralPreferencesButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        fileStepBackBtn = new javax.swing.JButton();
        fileStepForwardBtn = new javax.swing.JButton();
        scaleLabel = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        breakButton = new javax.swing.JButton();
        flattenButton = new javax.swing.JButton();
        selectAllBricksButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
        newBrickButton = new javax.swing.JButton();
        keyColorationButton = new javax.swing.JToggleButton();
        masterVolumePanel = new javax.swing.JPanel();
        allVolumeToolBarSlider = new javax.swing.JSlider();
        loopToggleButton = new javax.swing.JToggleButton();
        playButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        playOnClickToggleButton = new javax.swing.JToggleButton();
        tempoPanel = new javax.swing.JPanel();
        tempoSet = new javax.swing.JTextField();
        tempoSlider = new javax.swing.JSlider();
        styleComboBox = new javax.swing.JComboBox();
        barsPerLineComboBox = new javax.swing.JComboBox();
        featureWidthSlider = new javax.swing.JSlider();
        transposeSpinner = new javax.swing.JSpinner();
        roadMapTextEntry = new javax.swing.JTextField();
        roadMapStatus = new javax.swing.JTextField();
        roadMapScrollPane = new javax.swing.JScrollPane(roadMapPanel);
        previewScrollPane = new javax.swing.JScrollPane(previewPanel);
        clearButton = new javax.swing.JButton();
        insertBrickButton = new javax.swing.JButton();
        roadmapMenuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        openLeadsheetMI = new javax.swing.JMenuItem();
        printRoadMapMI = new javax.swing.JMenuItem();
        saveAsToNewLeadsheetMI = new javax.swing.JMenuItem();
        saveToNewLeadsheetMI = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        selectAllMenuItem = new javax.swing.JMenuItem();
        unselectAllMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        deleteMenuItem = new javax.swing.JMenuItem();
        flattenMenuItem = new javax.swing.JMenuItem();
        breakMenuItem = new javax.swing.JMenuItem();
        copyToTextMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        analyzeMenuItem = new javax.swing.JMenuItem();
        transposeMenu = new javax.swing.JMenu();
        transposeDownMenuItem = new javax.swing.JMenuItem();
        transposeUpMenuItem = new javax.swing.JMenuItem();
        sectionMenu = new javax.swing.JMenu();
        toggleSectionMenuItem = new javax.swing.JMenuItem();
        togglePhraseMenuItem = new javax.swing.JMenuItem();
        dictionaryMenu = new javax.swing.JMenu();
        brickLibraryMenuItem = new javax.swing.JCheckBoxMenuItem();
        editorMenu = new javax.swing.JMenu();
        dictionaryEditorMI = new javax.swing.JMenuItem();
        saveDictionaryAsMI = new javax.swing.JMenuItem();
        preferencesMenu = new javax.swing.JMenu();
        preferencesMenuItem = new javax.swing.JMenuItem();
        colorationPreferences = new javax.swing.JMenu();
        fixedColorsRadioBtn = new javax.swing.JRadioButtonMenuItem();
        relativeToCbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToBbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToBbbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToAbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToAbbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToGbutton = new javax.swing.JRadioButtonMenuItem();
        relativeTGbbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToFbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToEbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToEbbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToDbutton = new javax.swing.JRadioButtonMenuItem();
        relativeToDbbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralPreferences = new javax.swing.JMenu();
        noRomanNumeralsBtn = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToCbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToBbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToBbbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToAbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToAbbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToGbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeTGbbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToFbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToEbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToEbbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToDbutton = new javax.swing.JRadioButtonMenuItem();
        romanNumeralRelativeToDbbutton = new javax.swing.JRadioButtonMenuItem();
        showKeysCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        showBrickNamesCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        showJoinsCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        showStartingNoteCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        showVariantsCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        showStylesCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        replaceWithPhiCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        replaceWithDeltaCheckBoxMI = new javax.swing.JCheckBoxMenuItem();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        windowMenuSeparator = new javax.swing.JSeparator();
        playMenu = new javax.swing.JMenu();
        playSelectionMI = new javax.swing.JMenuItem();
        playAllMI = new javax.swing.JMenuItem();
        stopPlayMI = new javax.swing.JMenuItem();

        addBrickDialog.setTitle("Add New Brick"); // NOI18N
        addBrickDialog.setMinimumSize(new java.awt.Dimension(250, 180));
        addBrickDialog.setName("addBrickDialog"); // NOI18N
        addBrickDialog.setResizable(false);
        addBrickDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        dialogNameLabel.setText("Name:"); // NOI18N
        dialogNameLabel.setName("dialogNameLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        addBrickDialog.getContentPane().add(dialogNameLabel, gridBagConstraints);

        dialogKeyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dialogKeyLabel.setText("Key:"); // NOI18N
        dialogKeyLabel.setName("dialogKeyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        addBrickDialog.getContentPane().add(dialogKeyLabel, gridBagConstraints);

        dialogNameField.setToolTipText("Enter the name of the new brick type. If it is the same as an existing brick type, the Variant field will be enabled."); // NOI18N
        dialogNameField.setName("dialogNameField"); // NOI18N
        dialogNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogNameFieldActionPerformed(evt);
            }
        });
        dialogNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dialogNameFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        addBrickDialog.getContentPane().add(dialogNameField, gridBagConstraints);

        dialogAcceptButton.setText("Accept"); // NOI18N
        dialogAcceptButton.setName("dialogAcceptButton"); // NOI18N
        dialogAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogAccepted(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        addBrickDialog.getContentPane().add(dialogAcceptButton, gridBagConstraints);

        dialogCancelButton.setText("Cancel\n"); // NOI18N
        dialogCancelButton.setDefaultCapable(false);
        dialogCancelButton.setName("dialogCancelButton"); // NOI18N
        dialogCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogCancelButtondialogAccepted(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        addBrickDialog.getContentPane().add(dialogCancelButton, gridBagConstraints);

        dialogModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Major", "Minor", "Dominant" }));
        dialogModeComboBox.setName("dialogModeComboBox"); // NOI18N
        dialogModeComboBox.setPreferredSize(new java.awt.Dimension(120, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        addBrickDialog.getContentPane().add(dialogModeComboBox, gridBagConstraints);

        dialogKeyComboBox.setMaximumRowCount(12);
        dialogKeyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "B", "Bb", "A", "Ab", "G", "Gb", "F", "E", "Eb", "D", "Db" }));
        dialogKeyComboBox.setName("dialogKeyComboBox"); // NOI18N
        dialogKeyComboBox.setPreferredSize(new java.awt.Dimension(74, 200));
        dialogKeyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogKeyComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        addBrickDialog.getContentPane().add(dialogKeyComboBox, gridBagConstraints);

        dialogTypeComboBox.setMaximumRowCount(15);
        dialogTypeComboBox.setMinimumSize(new java.awt.Dimension(52, 50));
        dialogTypeComboBox.setName("dialogTypeComboBox"); // NOI18N
        dialogTypeComboBox.setPreferredSize(new java.awt.Dimension(52, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        addBrickDialog.getContentPane().add(dialogTypeComboBox, gridBagConstraints);

        dialogTypeLabel.setText("Type:"); // NOI18N
        dialogTypeLabel.setName("dialogTypeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        addBrickDialog.getContentPane().add(dialogTypeLabel, gridBagConstraints);

        dialogVariantLabel.setText("Variant:"); // NOI18N
        dialogVariantLabel.setEnabled(false);
        dialogVariantLabel.setName("dialogVariantLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        addBrickDialog.getContentPane().add(dialogVariantLabel, gridBagConstraints);

        dialogVariantField.setEditable(false);
        dialogVariantField.setToolTipText("Enter a qualifying name of your brick (optional for uniquely-defined bricks)"); // NOI18N
        dialogVariantField.setName("dialogVariantField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 3);
        addBrickDialog.getContentPane().add(dialogVariantField, gridBagConstraints);

        chordChangeDialog.setTitle("Settings"); // NOI18N
        chordChangeDialog.setMinimumSize(new java.awt.Dimension(300, 100));
        chordChangeDialog.setModal(true);
        chordChangeDialog.setName("chordChangeDialog"); // NOI18N
        chordChangeDialog.setResizable(false);
        chordChangeDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        chordDialogNameField.setText("CM7"); // NOI18N
        chordDialogNameField.setMinimumSize(new java.awt.Dimension(14, 20));
        chordDialogNameField.setName("chordDialogNameField"); // NOI18N
        chordDialogNameField.setPreferredSize(new java.awt.Dimension(42, 20));
        chordDialogNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordDialogNameFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        chordChangeDialog.getContentPane().add(chordDialogNameField, gridBagConstraints);

        chordDialogAcceptButton.setText("Accept"); // NOI18N
        chordDialogAcceptButton.setName("chordDialogAcceptButton"); // NOI18N
        chordDialogAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordDialogAcceptButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        chordChangeDialog.getContentPane().add(chordDialogAcceptButton, gridBagConstraints);

        chordDialogDurationComboBox.setName("chordDialogDurationComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        chordChangeDialog.getContentPane().add(chordDialogDurationComboBox, gridBagConstraints);

        preferencesDialog.setTitle("Roadmap Info"); // NOI18N
        preferencesDialog.setMinimumSize(new java.awt.Dimension(400, 200));
        preferencesDialog.setName("preferencesDialog"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        prefDialogCancelButton.setText("Cancel"); // NOI18N
        prefDialogCancelButton.setToolTipText("Do not make changes."); // NOI18N
        prefDialogCancelButton.setMaximumSize(new java.awt.Dimension(145, 29));
        prefDialogCancelButton.setMinimumSize(new java.awt.Dimension(145, 29));
        prefDialogCancelButton.setName("prefDialogCancelButton"); // NOI18N
        prefDialogCancelButton.setPreferredSize(new java.awt.Dimension(145, 29));
        prefDialogCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefDialogCancelButtonActionPerformed(evt);
            }
        });
        jPanel2.add(prefDialogCancelButton);

        prefDialogAcceptButton1.setText("Accept Changes"); // NOI18N
        prefDialogAcceptButton1.setToolTipText("Accept the changes as indicated."); // NOI18N
        prefDialogAcceptButton1.setName("prefDialogAcceptButton1"); // NOI18N
        prefDialogAcceptButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefDialogAcceptButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(prefDialogAcceptButton1);

        preferencesDialog.getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        prefDialogMeterLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        prefDialogMeterLabel.setText("Time Signature:"); // NOI18N
        prefDialogMeterLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        prefDialogMeterLabel.setName("prefDialogMeterLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(prefDialogMeterLabel, gridBagConstraints);

        jLabel6.setText("/"); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel6, gridBagConstraints);

        prefDialogTitleField.setName("prefDialogTitleField"); // NOI18N
        prefDialogTitleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(prefDialogTitleField, gridBagConstraints);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Title:"); // NOI18N
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabel7, gridBagConstraints);

        upperMetre.setToolTipText("Upper time signature"); // NOI18N
        upperMetre.setName("upperMetre"); // NOI18N
        upperMetre.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(upperMetre, gridBagConstraints);

        lowerMetre.setToolTipText("Lower time signature"); // NOI18N
        lowerMetre.setName("lowerMetre"); // NOI18N
        lowerMetre.setPreferredSize(new java.awt.Dimension(50, 28));
        lowerMetre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowerMetreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(lowerMetre, gridBagConstraints);

        preferencesDialog.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        brickDictionaryFrame.setTitle("Brick Dictionary"); // NOI18N
        brickDictionaryFrame.setAlwaysOnTop(true);
        brickDictionaryFrame.setMinimumSize(new java.awt.Dimension(200, 400));
        brickDictionaryFrame.setName("brickDictionaryFrame"); // NOI18N
        brickDictionaryFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                brickDictionaryFrameWindowClosing(evt);
            }
        });
        brickDictionaryFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                brickDictionaryFrameComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                brickDictionaryFrameComponentHidden(evt);
            }
        });
        brickDictionaryFrame.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keyPressedInDictionaryFrame(evt);
            }
        });
        brickDictionaryFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        libraryScrollPane.setName("libraryScrollPane"); // NOI18N

        libraryTree.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        libraryTree.setModel(libraryTreeModel);
        libraryTree.setToolTipText("Dictionary of bricks that may be viewed and inserted"); // NOI18N
        libraryTree.setMaximumSize(new java.awt.Dimension(400, 3000));
        libraryTree.setMinimumSize(new java.awt.Dimension(200, 200));
        libraryTree.setName("Bricks"); // NOI18N
        libraryTree.setPreferredSize(new java.awt.Dimension(300, 3000));
        libraryTree.setRootVisible(false);
        libraryTree.setShowsRootHandles(true);
        libraryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        libraryTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                libraryTreeMouseClicked(evt);
            }
        });
        libraryTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                libraryTreeTreeCollapsed(evt);
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                libraryTreeTreeExpanded(evt);
            }
        });
        libraryTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                libraryTreeSelected(evt);
            }
        });
        libraryTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedDictionaryTree(evt);
            }
        });
        libraryScrollPane.setViewportView(libraryTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        brickDictionaryFrame.getContentPane().add(libraryScrollPane, gridBagConstraints);

        keyComboBox.setMaximumRowCount(12);
        keyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "B", "Bb", "A", "Ab", "G", "Gb", "F", "E", "Eb", "D", "Db" }));
        keyComboBox.setToolTipText("Key of the brick."); // NOI18N
        keyComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Key/Root"));
        keyComboBox.setMinimumSize(new java.awt.Dimension(52, 54));
        keyComboBox.setName("keyComboBox"); // NOI18N
        keyComboBox.setPreferredSize(new java.awt.Dimension(52, 54));
        keyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.02;
        brickDictionaryFrame.getContentPane().add(keyComboBox, gridBagConstraints);

        deleteButton.setText("Delete from Dictionary"); // NOI18N
        deleteButton.setToolTipText("Makes the selected brick \"invisible\" in the dictionary."); // NOI18N
        deleteButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        deleteButton.setMaximumSize(new java.awt.Dimension(182, 40));
        deleteButton.setMinimumSize(new java.awt.Dimension(115, 40));
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.setPreferredSize(new java.awt.Dimension(147, 40));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.02;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        brickDictionaryFrame.getContentPane().add(deleteButton, gridBagConstraints);

        reloadButton.setText("Reload Dictionary"); // NOI18N
        reloadButton.setToolTipText("Reloads the dictionary, in case it was edited externally."); // NOI18N
        reloadButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        reloadButton.setMaximumSize(new java.awt.Dimension(182, 40));
        reloadButton.setMinimumSize(new java.awt.Dimension(115, 40));
        reloadButton.setName("reloadButton"); // NOI18N
        reloadButton.setPreferredSize(new java.awt.Dimension(115, 40));
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.02;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        brickDictionaryFrame.getContentPane().add(reloadButton, gridBagConstraints);

        durationComboBox.setSelectedItem(2);
        durationComboBox.setToolTipText("Set the duration of this brick (in slots)."); // NOI18N
        durationComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Duration\n"));
        durationComboBox.setMinimumSize(new java.awt.Dimension(52, 54));
        durationComboBox.setName("durationComboBox"); // NOI18N
        durationComboBox.setPreferredSize(new java.awt.Dimension(52, 54));
        durationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationComboBoxdurationChosen(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.02;
        brickDictionaryFrame.getContentPane().add(durationComboBox, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Road Map\n"); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 600));
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                roadMapWindowClosing(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setFocusable(false);
        toolBar.setMaximumSize(new java.awt.Dimension(100, 60));
        toolBar.setMinimumSize(new java.awt.Dimension(500, 50));
        toolBar.setName("toolBar"); // NOI18N
        toolBar.setPreferredSize(new java.awt.Dimension(500, 50));

        fileStepBackBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperBack.png"))); // NOI18N
        fileStepBackBtn.setToolTipText("Browse previous leadsheet file in the current directory.\n"); // NOI18N
        fileStepBackBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepBackBtn.setFocusable(false);
        fileStepBackBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileStepBackBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setName("fileStepBackBtn"); // NOI18N
        fileStepBackBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fileStepBackBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepBackBtnActionPerformed(evt);
            }
        });
        toolBar.add(fileStepBackBtn);

        fileStepForwardBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperFront.png"))); // NOI18N
        fileStepForwardBtn.setToolTipText("Browse next leadsheet file in the current directory.\n"); // NOI18N
        fileStepForwardBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepForwardBtn.setFocusable(false);
        fileStepForwardBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileStepForwardBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setName("fileStepForwardBtn"); // NOI18N
        fileStepForwardBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fileStepForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepForwardBtnActionPerformed(evt);
            }
        });
        toolBar.add(fileStepForwardBtn);

        scaleLabel.setName("scaleLabel"); // NOI18N
        toolBar.add(scaleLabel);

        scaleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "/5", "/4", "/3", "/2", "x1", "x2", "x3", "x4", "x5" }));
        scaleComboBox.setSelectedIndex(4);
        scaleComboBox.setToolTipText("Scale the length of the brick or chord by a factor."); // NOI18N
        scaleComboBox.setMaximumSize(new java.awt.Dimension(80, 45));
        scaleComboBox.setMinimumSize(new java.awt.Dimension(80, 30));
        scaleComboBox.setName("scaleComboBox"); // NOI18N
        scaleComboBox.setPreferredSize(new java.awt.Dimension(80, 30));
        scaleComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                scaleComboBoxscaleComboReleased(evt);
            }
        });
        scaleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleComboBoxscaleChosen(evt);
            }
        });
        toolBar.add(scaleComboBox);

        breakButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        breakButton.setText("Break"); // NOI18N
        breakButton.setToolTipText("Break this brick into constitutent parts."); // NOI18N
        breakButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        breakButton.setFocusable(false);
        breakButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        breakButton.setMaximumSize(new java.awt.Dimension(50, 30));
        breakButton.setMinimumSize(new java.awt.Dimension(50, 30));
        breakButton.setName("breakButton"); // NOI18N
        breakButton.setPreferredSize(new java.awt.Dimension(50, 30));
        breakButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        breakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakButtonPressed(evt);
            }
        });
        toolBar.add(breakButton);

        flattenButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        flattenButton.setText("Flatten"); // NOI18N
        flattenButton.setToolTipText("Flatten selected bricks into their constituent chords."); // NOI18N
        flattenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        flattenButton.setFocusable(false);
        flattenButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        flattenButton.setMaximumSize(new java.awt.Dimension(50, 30));
        flattenButton.setMinimumSize(new java.awt.Dimension(50, 30));
        flattenButton.setName("flattenButton"); // NOI18N
        flattenButton.setPreferredSize(new java.awt.Dimension(50, 30));
        flattenButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        flattenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenButtonPressed(evt);
            }
        });
        toolBar.add(flattenButton);

        selectAllBricksButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12)); // NOI18N
        selectAllBricksButton.setText("Select All"); // NOI18N
        selectAllBricksButton.setToolTipText("Select all bricks.\n"); // NOI18N
        selectAllBricksButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        selectAllBricksButton.setFocusable(false);
        selectAllBricksButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAllBricksButton.setMaximumSize(new java.awt.Dimension(60, 30));
        selectAllBricksButton.setMinimumSize(new java.awt.Dimension(60, 30));
        selectAllBricksButton.setName("selectAllBricksButton"); // NOI18N
        selectAllBricksButton.setPreferredSize(new java.awt.Dimension(60, 30));
        selectAllBricksButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectAllBricksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBricksButtonPressed(evt);
            }
        });
        toolBar.add(selectAllBricksButton);

        analyzeButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        analyzeButton.setToolTipText("Analyze the selection into bricks."); // NOI18N
        analyzeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        analyzeButton.setFocusable(false);
        analyzeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        analyzeButton.setLabel("Analyze"); // NOI18N
        analyzeButton.setMaximumSize(new java.awt.Dimension(60, 30));
        analyzeButton.setMinimumSize(new java.awt.Dimension(60, 30));
        analyzeButton.setName("analyzeButton"); // NOI18N
        analyzeButton.setPreferredSize(new java.awt.Dimension(60, 30));
        analyzeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analyzeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeButtonPressed(evt);
            }
        });
        toolBar.add(analyzeButton);

        newBrickButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        newBrickButton.setText("Define Brick"); // NOI18N
        newBrickButton.setToolTipText("Define a new brick in the dictionary."); // NOI18N
        newBrickButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newBrickButton.setFocusable(false);
        newBrickButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newBrickButton.setMaximumSize(new java.awt.Dimension(80, 30));
        newBrickButton.setMinimumSize(new java.awt.Dimension(80, 30));
        newBrickButton.setName("newBrickButton"); // NOI18N
        newBrickButton.setPreferredSize(new java.awt.Dimension(90, 30));
        newBrickButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newBrickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBrickButtonPressed(evt);
            }
        });
        toolBar.add(newBrickButton);

        keyColorationButton.setBackground(new java.awt.Color(153, 204, 255));
        keyColorationButton.setFont(new java.awt.Font("Arial 11", 0, 12)); // NOI18N
        keyColorationButton.setToolTipText("Turn note coloration off or on."); // NOI18N
        keyColorationButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        keyColorationButton.setFocusable(false);
        keyColorationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        keyColorationButton.setLabel("<html><center>Gray</center></html>");
        keyColorationButton.setMaximumSize(new java.awt.Dimension(40, 30));
        keyColorationButton.setMinimumSize(new java.awt.Dimension(40, 30));
        keyColorationButton.setName("keyColorationButton"); // NOI18N
        keyColorationButton.setOpaque(true);
        keyColorationButton.setPreferredSize(new java.awt.Dimension(40, 30));
        keyColorationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        keyColorationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyColorationButtonActionPerformed(evt);
            }
        });
        toolBar.add(keyColorationButton);

        masterVolumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 12))); // NOI18N
        masterVolumePanel.setToolTipText("Control playback volume."); // NOI18N
        masterVolumePanel.setMaximumSize(new java.awt.Dimension(120, 40));
        masterVolumePanel.setMinimumSize(new java.awt.Dimension(100, 40));
        masterVolumePanel.setName("masterVolumePanel"); // NOI18N
        masterVolumePanel.setOpaque(false);
        masterVolumePanel.setPreferredSize(new java.awt.Dimension(100, 40));
        masterVolumePanel.setLayout(new java.awt.GridBagLayout());

        allVolumeToolBarSlider.setMajorTickSpacing(5);
        allVolumeToolBarSlider.setMaximum(127);
        allVolumeToolBarSlider.setToolTipText("Set the master volume."); // NOI18N
        allVolumeToolBarSlider.setValue(80);
        allVolumeToolBarSlider.setMaximumSize(new java.awt.Dimension(200, 20));
        allVolumeToolBarSlider.setMinimumSize(new java.awt.Dimension(120, 20));
        allVolumeToolBarSlider.setName("allVolumeToolBarSlider"); // NOI18N
        allVolumeToolBarSlider.setPreferredSize(new java.awt.Dimension(120, 20));
        allVolumeToolBarSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                allVolumeToolBarSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        masterVolumePanel.add(allVolumeToolBarSlider, gridBagConstraints);

        toolBar.add(masterVolumePanel);

        loopToggleButton.setBackground(new java.awt.Color(0, 255, 0));
        loopToggleButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        loopToggleButton.setText("Loop"); // NOI18N
        loopToggleButton.setToolTipText("Loop the playback until stop is pressed, or loop is toggled."); // NOI18N
        loopToggleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loopToggleButton.setFocusable(false);
        loopToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loopToggleButton.setMaximumSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setMinimumSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setName("loopToggleButton"); // NOI18N
        loopToggleButton.setOpaque(true);
        loopToggleButton.setPreferredSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loopToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToggleButtonPressed(evt);
            }
        });
        toolBar.add(loopToggleButton);

        playButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12)); // NOI18N
        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        playButton.setText("\n"); // NOI18N
        playButton.setToolTipText("Play the selection.\n"); // NOI18N
        playButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setMaximumSize(new java.awt.Dimension(40, 30));
        playButton.setMinimumSize(new java.awt.Dimension(30, 30));
        playButton.setName("playButton"); // NOI18N
        playButton.setPreferredSize(new java.awt.Dimension(40, 30));
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonPressed(evt);
            }
        });
        toolBar.add(playButton);

        stopButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12)); // NOI18N
        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif"))); // NOI18N
        stopButton.setText(" "); // NOI18N
        stopButton.setToolTipText("Stop playing the selection.\n"); // NOI18N
        stopButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setMaximumSize(new java.awt.Dimension(40, 30));
        stopButton.setMinimumSize(new java.awt.Dimension(40, 30));
        stopButton.setName("stopButton"); // NOI18N
        stopButton.setPreferredSize(new java.awt.Dimension(35, 30));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonPressed(evt);
            }
        });
        toolBar.add(stopButton);

        playOnClickToggleButton.setBackground(new java.awt.Color(0, 255, 0));
        playOnClickToggleButton.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        playOnClickToggleButton.setText("<html><center>\nPlay on\n<br>\nClick\n</center></html>\n");
        playOnClickToggleButton.setToolTipText("Play brick when brick is clicked.\n"); // NOI18N
        playOnClickToggleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playOnClickToggleButton.setFocusable(false);
        playOnClickToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playOnClickToggleButton.setMaximumSize(new java.awt.Dimension(60, 30));
        playOnClickToggleButton.setMinimumSize(new java.awt.Dimension(60, 30));
        playOnClickToggleButton.setOpaque(true);
        playOnClickToggleButton.setPreferredSize(new java.awt.Dimension(60, 30));
        playOnClickToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playOnClickToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playOnClickToggleButtonPressed(evt);
            }
        });
        toolBar.add(playOnClickToggleButton);

        tempoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tempo (BPM) & Style\n", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 12))); // NOI18N
        tempoPanel.setToolTipText("Set the playback tempo."); // NOI18N
        tempoPanel.setMaximumSize(new java.awt.Dimension(300, 50));
        tempoPanel.setMinimumSize(new java.awt.Dimension(250, 50));
        tempoPanel.setName("tempoPanel"); // NOI18N
        tempoPanel.setOpaque(false);
        tempoPanel.setPreferredSize(new java.awt.Dimension(250, 50));
        tempoPanel.setLayout(new java.awt.GridBagLayout());

        tempoSet.setFont(new java.awt.Font("Dialog 12", 1, 12)); // NOI18N
        tempoSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tempoSet.setText("120"); // NOI18N
        tempoSet.setToolTipText("Set the tempo for playback in beats per minute."); // NOI18N
        tempoSet.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tempoSet.setMaximumSize(new java.awt.Dimension(30, 20));
        tempoSet.setMinimumSize(new java.awt.Dimension(30, 20));
        tempoSet.setName("tempoSet"); // NOI18N
        tempoSet.setPreferredSize(new java.awt.Dimension(30, 20));
        tempoSet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tempoSetMousePressed(evt);
            }
        });
        tempoSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempoSetActionPerformed(evt);
            }
        });
        tempoSet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tempoSetFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tempoSetFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 0);
        tempoPanel.add(tempoSet, gridBagConstraints);

        tempoSlider.setMaximum(300);
        tempoSlider.setMinimum(30);
        tempoSlider.setMinorTickSpacing(4);
        tempoSlider.setToolTipText("Sets the number of beats per minute in playback."); // NOI18N
        tempoSlider.setValue(160);
        tempoSlider.setMaximumSize(new java.awt.Dimension(120, 30));
        tempoSlider.setMinimumSize(new java.awt.Dimension(36, 20));
        tempoSlider.setName("tempoSlider"); // NOI18N
        tempoSlider.setPreferredSize(new java.awt.Dimension(100, 20));
        tempoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tempoSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 1, 6, 1);
        tempoPanel.add(tempoSlider, gridBagConstraints);

        styleComboBox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        styleComboBox.setMaximumRowCount(30);
        styleComboBox.setModel(getStyleMenuModel());
        styleComboBox.setToolTipText("Select the style for playback and for leadsheet creation."); // NOI18N
        styleComboBox.setMinimumSize(new java.awt.Dimension(100, 30));
        styleComboBox.setName("styleComboBox"); // NOI18N
        styleComboBox.setPreferredSize(new java.awt.Dimension(100, 30));
        styleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleChosen(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        tempoPanel.add(styleComboBox, gridBagConstraints);

        toolBar.add(tempoPanel);

        barsPerLineComboBox.setMaximumRowCount(24);
        barsPerLineComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        barsPerLineComboBox.setSelectedIndex(7);
        barsPerLineComboBox.setToolTipText("Set the maximum number of bars per line.\n"); // NOI18N
        barsPerLineComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bars/Line ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande 12", 0, 12))); // NOI18N
        barsPerLineComboBox.setMaximumSize(new java.awt.Dimension(80, 45));
        barsPerLineComboBox.setMinimumSize(new java.awt.Dimension(80, 30));
        barsPerLineComboBox.setName("barsPerLineComboBox"); // NOI18N
        barsPerLineComboBox.setPreferredSize(new java.awt.Dimension(80, 30));
        barsPerLineComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                barsPerLineComboBoxscaleComboReleased(evt);
            }
        });
        barsPerLineComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barsPerLineComboBoxscaleChosen(evt);
            }
        });
        toolBar.add(barsPerLineComboBox);

        featureWidthSlider.setMaximum(300);
        featureWidthSlider.setMinimum(60);
        featureWidthSlider.setToolTipText("Slide to adjust visual width of bricks. Double-click to constrain the setting so that it changes with the window width."); // NOI18N
        featureWidthSlider.setValue(settings.measureLength);
        featureWidthSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Width", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 12))); // NOI18N
        featureWidthSlider.setFocusable(false);
        featureWidthSlider.setMaximumSize(new java.awt.Dimension(200, 40));
        featureWidthSlider.setMinimumSize(new java.awt.Dimension(100, 40));
        featureWidthSlider.setName("featureWidthSlider"); // NOI18N
        featureWidthSlider.setPreferredSize(new java.awt.Dimension(100, 40));
        featureWidthSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                featureWidthSliderMouseClicked(evt);
            }
        });
        featureWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                featureWidthSliderChanged(evt);
            }
        });
        toolBar.add(featureWidthSlider);

        transposeSpinner.setToolTipText("Transposes the playback the specified number of half steps (e.g. use -2 for Bb instruments, +3 for Eb).");
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("imp/roadmap/Bundle"); // NOI18N
        transposeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RoadMapFrame.transposeSpinner.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
        transposeSpinner.setMaximumSize(new java.awt.Dimension(65, 45));
        transposeSpinner.setMinimumSize(new java.awt.Dimension(65, 45));
        transposeSpinner.setName(""); // NOI18N
        transposeSpinner.setPreferredSize(new java.awt.Dimension(65, 45));
        transposeSpinner.setValue(0);
        transposeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                transposeSpinnerStateChanged(evt);
            }
        });
        toolBar.add(transposeSpinner);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(toolBar, gridBagConstraints);

        roadMapTextEntry.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        roadMapTextEntry.setToolTipText("Enter chords using Leadsheet Notation. Separate measures with , or |."); // NOI18N
        roadMapTextEntry.setBorder(javax.swing.BorderFactory.createTitledBorder("Textual chord entry"));
        roadMapTextEntry.setMaximumSize(new java.awt.Dimension(2147483647, 45));
        roadMapTextEntry.setMinimumSize(new java.awt.Dimension(600, 45));
        roadMapTextEntry.setName("roadMapTextEntry"); // NOI18N
        roadMapTextEntry.setPreferredSize(new java.awt.Dimension(900, 45));
        roadMapTextEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textualEntryKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.65;
        gridBagConstraints.weighty = 0.08;
        getContentPane().add(roadMapTextEntry, gridBagConstraints);

        roadMapStatus.setEditable(false);
        roadMapStatus.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        roadMapStatus.setToolTipText("Shows the status of the roadmap."); // NOI18N
        roadMapStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Roadmap status"));
        roadMapStatus.setMaximumSize(new java.awt.Dimension(2147483647, 45));
        roadMapStatus.setMinimumSize(new java.awt.Dimension(300, 45));
        roadMapStatus.setName("roadMapStatus"); // NOI18N
        roadMapStatus.setPreferredSize(new java.awt.Dimension(300, 45));
        roadMapStatus.setRequestFocusEnabled(false);
        roadMapStatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                roadMapStatustextualEntryKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.08;
        getContentPane().add(roadMapStatus, gridBagConstraints);

        roadMapScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        roadMapScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        roadMapScrollPane.setMinimumSize(new java.awt.Dimension(800, 400));
        roadMapScrollPane.setName("roadMapScrollPane"); // NOI18N
        roadMapScrollPane.setPreferredSize(new java.awt.Dimension(2500, 900));
        roadMapScrollPane.setRequestFocusEnabled(false);
        roadMapScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapClicked(evt);
            }
        });
        roadMapScrollPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapDragged(evt);
            }
        });
        roadMapScrollPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                roadMapScrollPaneroadMapKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                roadMapScrollPaneroadMapKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.75;
        getContentPane().add(roadMapScrollPane, gridBagConstraints);

        previewScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Brick preview (select from Dictionary)\n", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        previewScrollPane.setToolTipText("Provides a preview of a brick selected from the Brick Dictionary."); // NOI18N
        previewScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        previewScrollPane.setDoubleBuffered(true);
        previewScrollPane.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        previewScrollPane.setMaximumSize(new java.awt.Dimension(32767, 100));
        previewScrollPane.setMinimumSize(new java.awt.Dimension(900, 100));
        previewScrollPane.setName("previewScrollPane"); // NOI18N
        previewScrollPane.setPreferredSize(new java.awt.Dimension(1200, 100));
        previewScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                previewScrollPanepreviewPaneReleased(evt);
            }
        });
        previewScrollPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                previewScrollPanepreviewPaneDragged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.09;
        getContentPane().add(previewScrollPane, gridBagConstraints);

        clearButton.setBackground(new java.awt.Color(255, 255, 51));
        clearButton.setText("<html><center>\nClear<br>\nText\n</center></html>"); // NOI18N
        clearButton.setToolTipText("Clear the textual entry field."); // NOI18N
        clearButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        clearButton.setMaximumSize(new java.awt.Dimension(46, 30));
        clearButton.setMinimumSize(new java.awt.Dimension(46, 30));
        clearButton.setName("clearButton"); // NOI18N
        clearButton.setPreferredSize(new java.awt.Dimension(46, 30));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(clearButton, gridBagConstraints);

        insertBrickButton.setBackground(new java.awt.Color(0, 255, 0));
        insertBrickButton.setText("<html>\n<center>\nInsert\n<br>\nBrick\n</center>\n</html>"); // NOI18N
        insertBrickButton.setToolTipText("Enter the previewed brick into the roadmap."); // NOI18N
        insertBrickButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        insertBrickButton.setMaximumSize(new java.awt.Dimension(46, 38));
        insertBrickButton.setMinimumSize(new java.awt.Dimension(46, 38));
        insertBrickButton.setName("insertBrickButton"); // NOI18N
        insertBrickButton.setPreferredSize(new java.awt.Dimension(46, 38));
        insertBrickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertBrickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(insertBrickButton, gridBagConstraints);

        roadmapMenuBar.setName("roadmapMenuBar"); // NOI18N

        fileMenu.setText("File"); // NOI18N
        fileMenu.setMaximumSize(new java.awt.Dimension(50, 40));
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setPreferredSize(new java.awt.Dimension(50, 21));

        openLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openLeadsheetMI.setMnemonic('o');
        openLeadsheetMI.setText("Open a leadsheet in parent window."); // NOI18N
        openLeadsheetMI.setToolTipText("Open a leadsheet in the parent leadsheet window."); // NOI18N
        openLeadsheetMI.setName("openLeadsheetMI"); // NOI18N
        openLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLeadsheetMIActionPerformed(evt);
            }
        });
        fileMenu.add(openLeadsheetMI);

        printRoadMapMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printRoadMapMI.setText("Print RoadMap");
        printRoadMapMI.setToolTipText("Print the contents of this roadmap."); // NOI18N
        printRoadMapMI.setName("printRoadMapMI"); // NOI18N
        printRoadMapMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printRoadMapMIActionPerformed(evt);
            }
        });
        fileMenu.add(printRoadMapMI);

        saveAsToNewLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        saveAsToNewLeadsheetMI.setText("Save As to a new leadsheet.\n"); // NOI18N
        saveAsToNewLeadsheetMI.setToolTipText("Create a new leadsheet and store all chords in it."); // NOI18N
        saveAsToNewLeadsheetMI.setName("saveAsToNewLeadsheetMI"); // NOI18N
        saveAsToNewLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsToNewLeadsheetMIaction(evt);
            }
        });
        fileMenu.add(saveAsToNewLeadsheetMI);

        saveToNewLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveToNewLeadsheetMI.setText("Save to the new leadsheet."); // NOI18N
        saveToNewLeadsheetMI.setToolTipText("Store entire contents in the newly-created leadsheet."); // NOI18N
        saveToNewLeadsheetMI.setName("saveToNewLeadsheetMI"); // NOI18N
        saveToNewLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToNewLeadsheetMIaction(evt);
            }
        });
        fileMenu.add(saveToNewLeadsheetMI);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Close this window."); // NOI18N
        exitMenuItem.setToolTipText("Closes this window."); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIhandler(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        roadmapMenuBar.add(fileMenu);

        editMenu.setText("Edit"); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        selectAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        selectAllMenuItem.setText("Select All"); // NOI18N
        selectAllMenuItem.setName("selectAllMenuItem"); // NOI18N
        selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllMenuItemClicked(evt);
            }
        });
        editMenu.add(selectAllMenuItem);

        unselectAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        unselectAllMenuItem.setText("Unselect All"); // NOI18N
        unselectAllMenuItem.setToolTipText("Unselects any selected bricks."); // NOI18N
        unselectAllMenuItem.setName("unselectAllMenuItem"); // NOI18N
        unselectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unselectAllMenuItemClicked(evt);
            }
        });
        editMenu.add(unselectAllMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        editMenu.add(jSeparator1);

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, 0));
        undoMenuItem.setText("Undo"); // NOI18N
        undoMenuItem.setName("undoMenuItem"); // NOI18N
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, 0));
        redoMenuItem.setText("Redo"); // NOI18N
        redoMenuItem.setName("redoMenuItem"); // NOI18N
        redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(redoMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        editMenu.add(jSeparator2);

        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, 0));
        cutMenuItem.setText("Cut"); // NOI18N
        cutMenuItem.setName("cutMenuItem"); // NOI18N
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
        copyMenuItem.setText("Copy"); // NOI18N
        copyMenuItem.setName("copyMenuItem"); // NOI18N
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        pasteMenuItem.setText("Paste"); // NOI18N
        pasteMenuItem.setName("pasteMenuItem"); // NOI18N
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(pasteMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        editMenu.add(jSeparator3);

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
        deleteMenuItem.setText("Delete Selection"); // NOI18N
        deleteMenuItem.setName("deleteMenuItem"); // NOI18N
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);

        flattenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, 0));
        flattenMenuItem.setText("Flatten Selection"); // NOI18N
        flattenMenuItem.setToolTipText("Repeatedly break the selection until there are only chords, with no composite bricks.\n"); // NOI18N
        flattenMenuItem.setName("flattenMenuItem"); // NOI18N
        flattenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(flattenMenuItem);

        breakMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        breakMenuItem.setText("Break Selection"); // NOI18N
        breakMenuItem.setName("breakMenuItem"); // NOI18N
        breakMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(breakMenuItem);

        copyToTextMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, 0));
        copyToTextMenuItem.setText("Copy Selection to Text Window");
        copyToTextMenuItem.setName("copyToTextMenuItem"); // NOI18N
        copyToTextMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyToTextMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(copyToTextMenuItem);

        jSeparator4.setName("jSeparator4"); // NOI18N
        editMenu.add(jSeparator4);

        analyzeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        analyzeMenuItem.setText("Analyze Selection"); // NOI18N
        analyzeMenuItem.setToolTipText("Performs a brick analysis on the selected chords, or on all chords if nothing selected."); // NOI18N
        analyzeMenuItem.setName("analyzeMenuItem"); // NOI18N
        analyzeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(analyzeMenuItem);

        roadmapMenuBar.add(editMenu);

        transposeMenu.setText("Transpose"); // NOI18N
        transposeMenu.setName("transposeMenu"); // NOI18N

        transposeDownMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        transposeDownMenuItem.setText("Transpose Selection Up Semitone"); // NOI18N
        transposeDownMenuItem.setName("transposeDownMenuItem"); // NOI18N
        transposeDownMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeDownMenuItemActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeDownMenuItem);

        transposeUpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        transposeUpMenuItem.setText("Transpose Selection Down Semitone"); // NOI18N
        transposeUpMenuItem.setName("transposeUpMenuItem"); // NOI18N
        transposeUpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeUpMenuItemActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeUpMenuItem);

        roadmapMenuBar.add(transposeMenu);

        sectionMenu.setText("Sections"); // NOI18N
        sectionMenu.setName("sectionMenu"); // NOI18N

        toggleSectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, 0));
        toggleSectionMenuItem.setText("Toggle Section"); // NOI18N
        toggleSectionMenuItem.setName("toggleSectionMenuItem"); // NOI18N
        toggleSectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleSectionMenuItemActionPerformed(evt);
            }
        });
        sectionMenu.add(toggleSectionMenuItem);

        togglePhraseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK));
        togglePhraseMenuItem.setText("Toggle Phrase"); // NOI18N
        togglePhraseMenuItem.setName("togglePhraseMenuItem"); // NOI18N
        togglePhraseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePhraseMenuItemActionPerformed(evt);
            }
        });
        sectionMenu.add(togglePhraseMenuItem);

        roadmapMenuBar.add(sectionMenu);

        dictionaryMenu.setMnemonic('W');
        dictionaryMenu.setText("Dictionary"); // NOI18N
        dictionaryMenu.setName("dictionaryMenu"); // NOI18N
        dictionaryMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                dictionaryMenuMenuSelected(evt);
            }
        });

        brickLibraryMenuItem.setText("Brick Dictionary"); // NOI18N
        brickLibraryMenuItem.setToolTipText("Opens the dictionary of all currently-defined bricks."); // NOI18N
        brickLibraryMenuItem.setName("brickLibraryMenuItem"); // NOI18N
        brickLibraryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brickLibraryMenuItemActionPerformed(evt);
            }
        });
        dictionaryMenu.add(brickLibraryMenuItem);

        roadmapMenuBar.add(dictionaryMenu);

        editorMenu.setText("Editor"); // NOI18N
        editorMenu.setToolTipText("Open dictionary textual editor."); // NOI18N
        editorMenu.setName("editorMenu"); // NOI18N

        dictionaryEditorMI.setText("Dictionary Textual Editor"); // NOI18N
        dictionaryEditorMI.setToolTipText("Open a text editor for the Brick Dictionary."); // NOI18N
        dictionaryEditorMI.setName("dictionaryEditorMI"); // NOI18N
        dictionaryEditorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dictionaryEditorMIActionPerformed(evt);
            }
        });
        editorMenu.add(dictionaryEditorMI);

        saveDictionaryAsMI.setText("Save Dictionary as ..."); // NOI18N
        saveDictionaryAsMI.setToolTipText("Save the current dictionary under a new name."); // NOI18N
        saveDictionaryAsMI.setName("saveDictionaryAsMI"); // NOI18N
        saveDictionaryAsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDictionaryAsMIActionPerformed(evt);
            }
        });
        editorMenu.add(saveDictionaryAsMI);

        roadmapMenuBar.add(editorMenu);

        preferencesMenu.setMnemonic('P');
        preferencesMenu.setText("Preferences"); // NOI18N
        preferencesMenu.setToolTipText("Set preferences for this roadmap."); // NOI18N
        preferencesMenu.setName("preferencesMenu"); // NOI18N
        preferencesMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                preferencesMenuMenuSelected(evt);
            }
        });

        preferencesMenuItem.setText("Roadmap Preferences"); // NOI18N
        preferencesMenuItem.setName("preferencesMenuItem"); // NOI18N
        preferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(preferencesMenuItem);

        colorationPreferences.setText("Key Coloration"); // NOI18N
        colorationPreferences.setName("colorationPreferences"); // NOI18N

        colorationPreferencesButtonGroup.add(fixedColorsRadioBtn);
        fixedColorsRadioBtn.setSelected(true);
        fixedColorsRadioBtn.setText("Absolute Colors"); // NOI18N
        fixedColorsRadioBtn.setName("fixedColorsRadioBtn"); // NOI18N
        fixedColorsRadioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedColorsRadioBtnActionPerformed(evt);
            }
        });
        colorationPreferences.add(fixedColorsRadioBtn);

        colorationPreferencesButtonGroup.add(relativeToCbutton);
        relativeToCbutton.setText("Relative to C"); // NOI18N
        relativeToCbutton.setName("relativeToCbutton"); // NOI18N
        relativeToCbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToCbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToCbutton);

        colorationPreferencesButtonGroup.add(relativeToBbutton);
        relativeToBbutton.setText("Relative to B"); // NOI18N
        relativeToBbutton.setName("relativeToBbutton"); // NOI18N
        relativeToBbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToBbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToBbutton);

        colorationPreferencesButtonGroup.add(relativeToBbbutton);
        relativeToBbbutton.setText("Relative to Bb"); // NOI18N
        relativeToBbbutton.setName("relativeToBbbutton"); // NOI18N
        relativeToBbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToBbbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToBbbutton);

        colorationPreferencesButtonGroup.add(relativeToAbutton);
        relativeToAbutton.setText("Relative to A"); // NOI18N
        relativeToAbutton.setName("relativeToAbutton"); // NOI18N
        relativeToAbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToAbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToAbutton);

        colorationPreferencesButtonGroup.add(relativeToAbbutton);
        relativeToAbbutton.setText("Relative to Ab"); // NOI18N
        relativeToAbbutton.setName("relativeToAbbutton"); // NOI18N
        relativeToAbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToAbbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToAbbutton);

        colorationPreferencesButtonGroup.add(relativeToGbutton);
        relativeToGbutton.setText("Relative to G"); // NOI18N
        relativeToGbutton.setName("relativeToGbutton"); // NOI18N
        relativeToGbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToGbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToGbutton);

        colorationPreferencesButtonGroup.add(relativeTGbbutton);
        relativeTGbbutton.setText("Relative to Gb"); // NOI18N
        relativeTGbbutton.setName("relativeTGbbutton"); // NOI18N
        relativeTGbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeTGbbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeTGbbutton);

        colorationPreferencesButtonGroup.add(relativeToFbutton);
        relativeToFbutton.setText("Relative to F"); // NOI18N
        relativeToFbutton.setName("relativeToFbutton"); // NOI18N
        relativeToFbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToFbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToFbutton);

        colorationPreferencesButtonGroup.add(relativeToEbutton);
        relativeToEbutton.setText("Relative to E"); // NOI18N
        relativeToEbutton.setName("relativeToEbutton"); // NOI18N
        relativeToEbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToEbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToEbutton);

        colorationPreferencesButtonGroup.add(relativeToEbbutton);
        relativeToEbbutton.setText("Relative to Eb"); // NOI18N
        relativeToEbbutton.setName("relativeToEbbutton"); // NOI18N
        relativeToEbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToEbbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToEbbutton);

        colorationPreferencesButtonGroup.add(relativeToDbutton);
        relativeToDbutton.setText("Relative to D"); // NOI18N
        relativeToDbutton.setName("relativeToDbutton"); // NOI18N
        relativeToDbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToDbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToDbutton);

        colorationPreferencesButtonGroup.add(relativeToDbbutton);
        relativeToDbbutton.setText("Relative to Db"); // NOI18N
        relativeToDbbutton.setName("relativeToDbbutton"); // NOI18N
        relativeToDbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeToDbbuttonActionPerformed(evt);
            }
        });
        colorationPreferences.add(relativeToDbbutton);

        preferencesMenu.add(colorationPreferences);

        romanNumeralPreferences.setText("Display Roman Numerals");
        romanNumeralPreferences.setName("romanNumeralPreferences"); // NOI18N

        romanNumeralPreferencesButtonGroup.add(noRomanNumeralsBtn);
        noRomanNumeralsBtn.setSelected(true);
        noRomanNumeralsBtn.setText("Do Not Display"); // NOI18N
        noRomanNumeralsBtn.setName("noRomanNumeralsBtn"); // NOI18N
        noRomanNumeralsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noRomanNumeralsBtnActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(noRomanNumeralsBtn);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToCbutton);
        romanNumeralRelativeToCbutton.setText("Relative to C"); // NOI18N
        romanNumeralRelativeToCbutton.setName("romanNumeralRelativeToCbutton"); // NOI18N
        romanNumeralRelativeToCbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToCbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToCbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToBbutton);
        romanNumeralRelativeToBbutton.setText("Relative to B"); // NOI18N
        romanNumeralRelativeToBbutton.setName("romanNumeralRelativeToBbutton"); // NOI18N
        romanNumeralRelativeToBbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToBbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToBbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToBbbutton);
        romanNumeralRelativeToBbbutton.setText("Relative to Bb"); // NOI18N
        romanNumeralRelativeToBbbutton.setName("romanNumeralRelativeToBbbutton"); // NOI18N
        romanNumeralRelativeToBbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToBbbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToBbbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToAbutton);
        romanNumeralRelativeToAbutton.setText("Relative to A"); // NOI18N
        romanNumeralRelativeToAbutton.setName("romanNumeralRelativeToAbutton"); // NOI18N
        romanNumeralRelativeToAbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToAbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToAbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToAbbutton);
        romanNumeralRelativeToAbbutton.setText("Relative to Ab"); // NOI18N
        romanNumeralRelativeToAbbutton.setName("romanNumeralRelativeToAbbutton"); // NOI18N
        romanNumeralRelativeToAbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToAbbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToAbbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToGbutton);
        romanNumeralRelativeToGbutton.setText("Relative to G"); // NOI18N
        romanNumeralRelativeToGbutton.setName("romanNumeralRelativeToGbutton"); // NOI18N
        romanNumeralRelativeToGbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToGbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToGbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeTGbbutton);
        romanNumeralRelativeTGbbutton.setText("Relative to Gb"); // NOI18N
        romanNumeralRelativeTGbbutton.setName("romanNumeralRelativeTGbbutton"); // NOI18N
        romanNumeralRelativeTGbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeTGbbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeTGbbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToFbutton);
        romanNumeralRelativeToFbutton.setText("Relative to F"); // NOI18N
        romanNumeralRelativeToFbutton.setName("romanNumeralRelativeToFbutton"); // NOI18N
        romanNumeralRelativeToFbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToFbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToFbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToEbutton);
        romanNumeralRelativeToEbutton.setText("Relative to E"); // NOI18N
        romanNumeralRelativeToEbutton.setName("romanNumeralRelativeToEbutton"); // NOI18N
        romanNumeralRelativeToEbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToEbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToEbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToEbbutton);
        romanNumeralRelativeToEbbutton.setText("Relative to Eb"); // NOI18N
        romanNumeralRelativeToEbbutton.setName("romanNumeralRelativeToEbbutton"); // NOI18N
        romanNumeralRelativeToEbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToEbbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToEbbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToDbutton);
        romanNumeralRelativeToDbutton.setText("Relative to D"); // NOI18N
        romanNumeralRelativeToDbutton.setName("romanNumeralRelativeToDbutton"); // NOI18N
        romanNumeralRelativeToDbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToDbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToDbutton);

        romanNumeralPreferencesButtonGroup.add(romanNumeralRelativeToDbbutton);
        romanNumeralRelativeToDbbutton.setText("Relative to Db"); // NOI18N
        romanNumeralRelativeToDbbutton.setName("romanNumeralRelativeToDbbutton"); // NOI18N
        romanNumeralRelativeToDbbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romanNumeralRelativeToDbbuttonActionPerformed(evt);
            }
        });
        romanNumeralPreferences.add(romanNumeralRelativeToDbbutton);

        preferencesMenu.add(romanNumeralPreferences);

        showKeysCheckBoxMI.setSelected(true);
        showKeysCheckBoxMI.setText("Show Keys"); // NOI18N
        showKeysCheckBoxMI.setToolTipText("Indicate whether to show keys."); // NOI18N
        showKeysCheckBoxMI.setName("showKeysCheckBoxMI"); // NOI18N
        showKeysCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showKeysCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showKeysCheckBoxMI);

        showBrickNamesCheckBoxMI.setSelected(true);
        showBrickNamesCheckBoxMI.setText("Show Brick Names"); // NOI18N
        showBrickNamesCheckBoxMI.setToolTipText("Indicate whether to show the names of bricks."); // NOI18N
        showBrickNamesCheckBoxMI.setName("showBrickNamesCheckBoxMI"); // NOI18N
        showBrickNamesCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBrickNamesCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showBrickNamesCheckBoxMI);

        showJoinsCheckBoxMI.setSelected(true);
        showJoinsCheckBoxMI.setText("Show Joins"); // NOI18N
        showJoinsCheckBoxMI.setToolTipText("Indicate whether to show joins between bricks."); // NOI18N
        showJoinsCheckBoxMI.setName("showJoinsCheckBoxMI"); // NOI18N
        showJoinsCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showJoinsCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showJoinsCheckBoxMI);

        showStartingNoteCheckBoxMI.setSelected(true);
        showStartingNoteCheckBoxMI.setText("Show Starting Note");
        showStartingNoteCheckBoxMI.setName("showStartingNoteCheckBoxMI"); // NOI18N
        showStartingNoteCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStartingNoteCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showStartingNoteCheckBoxMI);

        showVariantsCheckBoxMI.setSelected(true);
        showVariantsCheckBoxMI.setText("Show Variants");
        showVariantsCheckBoxMI.setName("showVariantsCheckBoxMI"); // NOI18N
        showVariantsCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showVariantsCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showVariantsCheckBoxMI);

        showStylesCheckBoxMI.setSelected(true);
        showStylesCheckBoxMI.setText("Show Styles");
        showStylesCheckBoxMI.setName("showStylesCheckBoxMI"); // NOI18N
        showStylesCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStylesCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(showStylesCheckBoxMI);

        replaceWithPhiCheckBoxMI.setSelected(true);
        replaceWithPhiCheckBoxMI.setText("Use \u03D5 for m7b5");
        replaceWithPhiCheckBoxMI.setName("replaceWithPhiCheckBoxMI"); // NOI18N
        replaceWithPhiCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithPhiCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(replaceWithPhiCheckBoxMI);

        replaceWithDeltaCheckBoxMI.setSelected(true);
        replaceWithDeltaCheckBoxMI.setText("Use \u0394 for M7");
        replaceWithDeltaCheckBoxMI.setName("replaceWithDeltaCheckBoxMI"); // NOI18N
        replaceWithDeltaCheckBoxMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithDeltaCheckBoxMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(replaceWithDeltaCheckBoxMI);

        roadmapMenuBar.add(preferencesMenu);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window"); // NOI18N
        windowMenu.setName("windowMenu"); // NOI18N
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window"); // NOI18N
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)"); // NOI18N
        closeWindowMI.setName("closeWindowMI"); // NOI18N
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows"); // NOI18N
        cascadeMI.setToolTipText("Rearrange windows into a cascade.\n"); // NOI18N
        cascadeMI.setName("cascadeMI"); // NOI18N
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);

        jSeparator5.setName("jSeparator5"); // NOI18N
        windowMenu.add(jSeparator5);

        windowMenuSeparator.setName("windowMenuSeparator"); // NOI18N
        windowMenu.add(windowMenuSeparator);

        roadmapMenuBar.add(windowMenu);

        playMenu.setMnemonic('p');
        playMenu.setText("Play"); // NOI18N
        playMenu.setToolTipText("Select type of playing."); // NOI18N
        playMenu.setName("playMenu"); // NOI18N
        playMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMenuActionPerformed(evt);
            }
        });

        playSelectionMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        playSelectionMI.setText("Play Selection"); // NOI18N
        playSelectionMI.setToolTipText("Play only the selection."); // NOI18N
        playSelectionMI.setName("playSelectionMI"); // NOI18N
        playSelectionMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playSelectionMIActionPerformed(evt);
            }
        });
        playMenu.add(playSelectionMI);

        playAllMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, 0));
        playAllMI.setMnemonic('p');
        playAllMI.setText("Play All"); // NOI18N
        playAllMI.setToolTipText("Play the entire chorus."); // NOI18N
        playAllMI.setName("playAllMI"); // NOI18N
        playAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playAllMIActionPerformed(evt);
            }
        });
        playMenu.add(playAllMI);

        stopPlayMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, 0));
        stopPlayMI.setText("Stop Playback"); // NOI18N
        stopPlayMI.setToolTipText("Stop the current playing."); // NOI18N
        stopPlayMI.setName("stopPlayMI"); // NOI18N
        stopPlayMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayMIActionPerformed(evt);
            }
        });
        playMenu.add(stopPlayMI);

        roadmapMenuBar.add(playMenu);

        setJMenuBar(roadmapMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /* IMPORTANT: Any menu item with accelerators should include the conditional
     * if(!roadMapTextEntry.isFocusOwner())
     * Otherwise it will do actions while you type in the text field.
     * I've added it to all menu items just in case they're given accelerators.
     * It's dumb, but it's for safety. Feel free to implement a better solution.
     */
    // <editor-fold defaultstate="collapsed" desc="Events">
    private void libraryTreeSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_libraryTreeSelected
        setPreview();
}//GEN-LAST:event_libraryTreeSelected

    private void previewScrollPanepreviewPaneReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewScrollPanepreviewPaneReleased
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue() + previewScrollPane.getY() - roadMapScrollPane.getY();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue() + previewScrollPane.getX();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        dropFromPreview(x, y);
}//GEN-LAST:event_previewScrollPanepreviewPaneReleased

    private void previewScrollPanepreviewPaneDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewScrollPanepreviewPaneDragged
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue() + previewScrollPane.getY() - roadMapScrollPane.getY();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue() + previewScrollPane.getX();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        dragFromPreview(x, y);
}//GEN-LAST:event_previewScrollPanepreviewPaneDragged

    private void durationComboBoxdurationChosen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationComboBoxdurationChosen
        setPreviewDuration();
}//GEN-LAST:event_durationComboBoxdurationChosen

    private void roadMapScrollPaneroadMapReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapReleased
        int x = evt.getX() + roadMapScrollPane.getHorizontalScrollBar().getValue();
        int y = evt.getY() + roadMapScrollPane.getVerticalScrollBar().getValue();
        dropCurrentBrick(x, y);
}//GEN-LAST:event_roadMapScrollPaneroadMapReleased

    private void roadMapScrollPaneroadMapClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapClicked
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        int index = roadMapPanel.getBrickIndexAt(x,y);
        if(evt.getButton() == evt.BUTTON1) {
            if(index != -1) {
                int jndex = roadMapPanel.getBrick(index).getChordAt(x, y);
                if(evt.isShiftDown())
                    selectBricks(index);
                else if( roadMapPanel.getBrick(index).isSelected() &&
                        evt.getClickCount() == 2 && jndex != -1) {
                    selectChord(index,jndex);
                    activateChordDialog();
                } else if( roadMapPanel.getBrick(index).isSelected() && jndex != -1) {
                    selectChord(index,jndex);
                } else
                    selectBrick(index);
                if( playOnClickToggleButton.isSelected() )
                  {
                    playSelection();
                  }
            } else //TODO, renaming and other outside clicks
                deselectBricks();
        } else if(evt.getButton() == evt.BUTTON3) {
            // Nothing
        }
        roadMapScrollPane.requestFocus();
}//GEN-LAST:event_roadMapScrollPaneroadMapClicked

    private void roadMapScrollPaneroadMapDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapDragged
        int x = evt.getX() + roadMapScrollPane.getHorizontalScrollBar().getValue();
        int y = evt.getY() + roadMapScrollPane.getVerticalScrollBar().getValue();
        dragSelectedBricks(x, y);
}//GEN-LAST:event_roadMapScrollPaneroadMapDragged

    private void roadMapScrollPaneroadMapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapKeyPressed
        switch (evt.getKeyCode()) {
            default:                                                    break;
        }
}//GEN-LAST:event_roadMapScrollPaneroadMapKeyPressed

    private void roadMapScrollPaneroadMapKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapKeyReleased
        switch (evt.getKeyCode()) {
            default:                            break;
        }
}//GEN-LAST:event_roadMapScrollPaneroadMapKeyReleased

    private void flattenButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenButtonPressed
        flattenSelection();
}//GEN-LAST:event_flattenButtonPressed

    private void breakButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakButtonPressed
        breakSelection();
}//GEN-LAST:event_breakButtonPressed

    private void scaleComboBoxscaleComboReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scaleComboBoxscaleComboReleased
}//GEN-LAST:event_scaleComboBoxscaleComboReleased

    private void scaleComboBoxscaleChosen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleComboBoxscaleChosen
        scaleSelection();
        scaleComboBox.setSelectedItem("x1");
}//GEN-LAST:event_scaleComboBoxscaleChosen

    private void newBrickButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBrickButtonPressed
        dialogNameField.setText("");
        dialogVariantField.setText("");
        dialogTypeComboBox.setSelectedIndex(0);
        dialogKeyComboBox.setSelectedIndex(0);
        addBrickDialog.setLocation(100, 100);
        addBrickDialog.setVisible(true);
}//GEN-LAST:event_newBrickButtonPressed

    private void analyzeButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeButtonPressed
        analyzeInBackground(settings.showJoins);
}//GEN-LAST:event_analyzeButtonPressed

    private void exitMIhandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIhandler
        closeWindow();
    }//GEN-LAST:event_exitMIhandler

    private void dialogAccepted(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogAccepted
        //TODO: maybe put warning/don't close window when brick name is taken
        addBrickDialog.setVisible(false);
        makeBrickFromSelection();
    }//GEN-LAST:event_dialogAccepted

    private void selectAllBricksButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBricksButtonPressed
        selectAllBricks();
    }//GEN-LAST:event_selectAllBricksButtonPressed

    private void playButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonPressed
        playSelection();
    }//GEN-LAST:event_playButtonPressed

    private void featureWidthSliderChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_featureWidthSliderChanged
        settings.measureLength = featureWidthSlider.getValue();
        roadMapPanel.placeBricks();
        setFeatureWidthLocked(false);
    }//GEN-LAST:event_featureWidthSliderChanged

    private void selectAllMenuItemClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllMenuItemClicked
        if(!roadMapTextEntry.isFocusOwner()) selectAllBricks();
    }//GEN-LAST:event_selectAllMenuItemClicked

    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) undo();
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) redo();
    }//GEN-LAST:event_redoMenuItemActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) cutSelection();
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) copySelection();
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) pasteSelection();
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) deleteSelection();
    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void flattenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) flattenSelection();
    }//GEN-LAST:event_flattenMenuItemActionPerformed

    private void breakMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) breakSelection();
    }//GEN-LAST:event_breakMenuItemActionPerformed

    private void toggleSectionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleSectionMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) toggleSectionBreak();
    }//GEN-LAST:event_toggleSectionMenuItemActionPerformed

    private void loopToggleButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToggleButtonPressed
        if( loopToggleButton.isSelected() )
          {
            loopToggleButton.setText("No Loop");
            loopToggleButton.setBackground(Color.RED);
          }
        else
          {
            loopToggleButton.setText("Loop");
            loopToggleButton.setBackground(Color.GREEN);
            stopPlayingSelection();
          }
    }//GEN-LAST:event_loopToggleButtonPressed

    
    /**
     * Add chords in leadsheet notation (with bar lines, etc.) from textual Entry
     * @param evt 
     */
    private void textualEntryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textualEntryKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
            saveState("TextEntry");

            String entered = roadMapTextEntry.getText();

            if( !entered.isEmpty() ){
                Score score = new Score();
                score.setMetre(getMetre());
                Tokenizer tokenizer = new Tokenizer(new StringReader(entered));

                Leadsheet.readLeadSheet(tokenizer, score);
                
                System.out.println(score.getPart(0));
                if( score.getPart(0).size() > 120 )
                    ErrorLog.log(ErrorLog.WARNING, "Melody notes entered with chord part will be ignored.");

            roadMapPanel.addBlocksBeforeSelection(score.getChordProg().toBlockList(), true); 
            roadMapPanel.placeBricks();
            roadMapPanel.deselectBricks();
            activateButtons();

            this.requestFocus();
            }
        }
    }//GEN-LAST:event_textualEntryKeyPressed

    private void chordDialogAcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordDialogAcceptButtonActionPerformed
        String name = chordDialogNameField.getText();
        if(ChordSymbol.makeChordSymbol(name) != null) {
            chordChangeDialog.setVisible(false);
            changeChord(name,
                    (Integer)chordDialogDurationComboBox.getSelectedItem() * settings.getSlotsPerBeat());
        }
    }//GEN-LAST:event_chordDialogAcceptButtonActionPerformed

    private void chordDialogNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordDialogNameFieldActionPerformed
        chordDialogAcceptButtonActionPerformed(evt);
    }//GEN-LAST:event_chordDialogNameFieldActionPerformed

    private void libraryTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_libraryTreeMouseClicked
        int clicks = evt.getClickCount();
        TreePath path = libraryTree.getPathForLocation(evt.getX(), evt.getY());
        if(path != null && previewPanel.getBrick() != null && clicks%2==0)
          {
            // If double-clicking with shift down, the brick will be played
            // after it is added.
            
            if( evt.isShiftDown() )
              {
                addAndPlayBrickFromPreview();
              }
            else
              {
                addBrickFromPreview();
              }
          }
    libraryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);        
    }//GEN-LAST:event_libraryTreeMouseClicked

    private void transposeDownMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeDownMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) transposeSelection(1);
    }//GEN-LAST:event_transposeDownMenuItemActionPerformed

    private void transposeUpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeUpMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) transposeSelection(-1);
    }//GEN-LAST:event_transposeUpMenuItemActionPerformed

    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMIActionPerformed
        closeWindow();
    }//GEN-LAST:event_closeWindowMIActionPerformed

    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMIActionPerformed
        
        WindowRegistry.cascadeWindows(this);
    }//GEN-LAST:event_cascadeMIActionPerformed

    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected
        
        windowMenu.removeAll();
        
        windowMenu.add(closeWindowMI);
        
        windowMenu.add(cascadeMI);
        
        for(WindowMenuItem w : WindowRegistry.getWindows())
            windowMenu.add(w.getMI(this));       // these are static, and calling getMI updates the name on them too in case the window title changed
        
        windowMenu.repaint();
    }//GEN-LAST:event_windowMenuMenuSelected

    private void roadMapWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_roadMapWindowClosing
        closeWindow();
    }//GEN-LAST:event_roadMapWindowClosing

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        roadMapPanel.requestFocusInWindow();
    }//GEN-LAST:event_formWindowActivated

    private void togglePhraseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togglePhraseMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) togglePhraseEnd();
    }//GEN-LAST:event_togglePhraseMenuItemActionPerformed

    private void openLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openLeadsheetMIActionPerformed
        notate.openLeadsheet(false);
}//GEN-LAST:event_openLeadsheetMIActionPerformed

    private void saveAsToNewLeadsheetMIaction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsToNewLeadsheetMIaction
        if(!roadMapTextEntry.isFocusOwner()) 
          {
            // To force saving to a new leadsheet.
            auxNotate = null;
            saveToNewNotate();
          }
    }//GEN-LAST:event_saveAsToNewLeadsheetMIaction

    private void keyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyComboBoxActionPerformed
        setPreviewKey();
    }//GEN-LAST:event_keyComboBoxActionPerformed

    private void roadMapScrollPaneMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneMouseMoved
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        roadMapPanel.setRolloverPos(new Point(x,y));
        roadMapPanel.draw();
    }//GEN-LAST:event_roadMapScrollPaneMouseMoved

    private void printRoadMapMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printRoadMapMIActionPerformed
        PrintUtilitiesRoadMap.printRoadMap(roadMapPanel);
    }//GEN-LAST:event_printRoadMapMIActionPerformed


    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (previewPanel.currentBrick != null)
        {
            brickLibrary.exileBrick((Brick)previewPanel.currentBrick.getBlock(), dictionaryFilename);
            initLibraryTree();
            libraryTree.setModel(libraryTreeModel);
            cykParser.createRules(brickLibrary);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed


    private void preferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner())
          {
            activatePreferencesDialog();
          }
    }//GEN-LAST:event_preferencesMenuItemActionPerformed

    private void prefDialogCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefDialogCancelButtonActionPerformed

            preferencesDialog.setVisible(false);

    }//GEN-LAST:event_prefDialogCancelButtonActionPerformed

    private void brickLibraryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brickLibraryMenuItemActionPerformed
        
    }//GEN-LAST:event_brickLibraryMenuItemActionPerformed

    private void brickDictionaryFrameWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_brickDictionaryFrameWindowClosing
        brickLibraryMenuItem.setSelected(false);
    }//GEN-LAST:event_brickDictionaryFrameWindowClosing

    private void dialogNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dialogNameFieldKeyReleased
        if(brickLibrary.hasBrick(BrickLibrary.dashless(dialogNameField.getText()))) {
            // why? ErrorLog.log(ErrorLog.WARNING, dialogNameField.getText());
            dialogVariantField.setEditable(true);
            dialogVariantLabel.setEnabled(true);
        } else {
            dialogVariantField.setEditable(false);
            dialogVariantLabel.setEnabled(false);
        }
    }//GEN-LAST:event_dialogNameFieldKeyReleased

    private void brickDictionaryFrameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_brickDictionaryFrameComponentShown
        WindowRegistry.registerWindow(brickDictionaryFrame);
    }//GEN-LAST:event_brickDictionaryFrameComponentShown

    private void brickDictionaryFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_brickDictionaryFrameComponentHidden
        WindowRegistry.unregisterWindow(brickDictionaryFrame);
    }//GEN-LAST:event_brickDictionaryFrameComponentHidden

    private void featureWidthSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_featureWidthSliderMouseClicked
        if(evt.getClickCount()%2 == 0) {
            scaleToWindow();
        } else {
            setFeatureWidthLocked(false);
        }
    }//GEN-LAST:event_featureWidthSliderMouseClicked

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)featureWidthSlider.getBorder();
        if(border.getTitle().endsWith(featureWidthSuffix))
          {
            scaleToWindow();
          }
    }//GEN-LAST:event_formComponentResized

    private void libraryTreeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_libraryTreeTreeCollapsed
        // Doesn't currently work on Linux and Windows; destroys vertical scrollbar
        // adjustForTreeChange();
    }//GEN-LAST:event_libraryTreeTreeCollapsed

    private void libraryTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_libraryTreeTreeExpanded
        // Doesn't currently work on Linux and Windows; destroys vertical scrollbar
        // adjustForTreeChange();
    }//GEN-LAST:event_libraryTreeTreeExpanded

private void adjustForTreeChange()
  {
  int width = libraryTree.getPreferredSize().width;
  
  int newHeight = libraryTree.getRowCount() * libraryTree.getRowHeight();
  
  libraryTree.setPreferredSize(new Dimension(width, newHeight));
  }

    private void fileStepBackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepBackBtnActionPerformed
        notate.fileStepBackward();
        if(notate.getAutoCreateRoadMap())
            ;
        else
          {
            notate.roadMapThisAnalyze();
          }
}//GEN-LAST:event_fileStepBackBtnActionPerformed

    private void fileStepForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepForwardBtnActionPerformed
        notate.fileStepForward();
        if(notate.getAutoCreateRoadMap())
            ;
        else
          {
            notate.roadMapThisAnalyze();
          }
}//GEN-LAST:event_fileStepForwardBtnActionPerformed

    private void dialogKeyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogKeyComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dialogKeyComboBoxActionPerformed

    private void dictionaryMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_dictionaryMenuMenuSelected
        populateRoadmapDictionaryMenu(defaultDictionaryName);
        
    }//GEN-LAST:event_dictionaryMenuMenuSelected

    private void keyColorationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyColorationButtonActionPerformed
        if( settings.keysColored )
          {
            settings.keysColored = false;
            keyColorationButton.setBackground(Color.red);
            keyColorationButton.setText("Color");
          }
        else
          {
            settings.keysColored = true;
            keyColorationButton.setBackground(new Color(153, 204, 255));
            keyColorationButton.setText("Gray");
          }
        roadMapPanel.draw();
    }//GEN-LAST:event_keyColorationButtonActionPerformed

    private void acceptPreferences()
      {
      if( intFromTextField(lowerMetre) % 2 == 0) {
            preferencesDialog.setVisible(false);
            setRoadMapInfo();
        } else
            {
              ErrorLog.log(ErrorLog.COMMENT, "Metre bottom must be 1, 2, 4 or 8");
            }    
      }
    private void prefDialogAcceptButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefDialogAcceptButton1ActionPerformed
       acceptPreferences();                                                     
    }//GEN-LAST:event_prefDialogAcceptButton1ActionPerformed

    private void dialogCancelButtondialogAccepted(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogCancelButtondialogAccepted
        addBrickDialog.setVisible(false);
    }//GEN-LAST:event_dialogCancelButtondialogAccepted

    private void dialogNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dialogNameFieldActionPerformed

    private void preferencesMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_preferencesMenuMenuSelected
        // TODO add your handling code here:
    }//GEN-LAST:event_preferencesMenuMenuSelected

    private void fixedColorsRadioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedColorsRadioBtnActionPerformed
        settings.setColorationBias(0); roadMapPanel.draw();
    }//GEN-LAST:event_fixedColorsRadioBtnActionPerformed

    private void relativeToCbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToCbuttonActionPerformed
        settings.setColorationBias(0); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToCbuttonActionPerformed

    private void relativeToBbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToBbuttonActionPerformed
        settings.setColorationBias(1); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToBbuttonActionPerformed

    private void relativeToBbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToBbbuttonActionPerformed
        settings.setColorationBias(2); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToBbbuttonActionPerformed

    private void relativeToAbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToAbuttonActionPerformed
        settings.setColorationBias(3); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToAbuttonActionPerformed

    private void relativeToAbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToAbbuttonActionPerformed
        settings.setColorationBias(4); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToAbbuttonActionPerformed

    private void relativeToGbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToGbuttonActionPerformed
        settings.setColorationBias(5); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToGbuttonActionPerformed

    private void relativeTGbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeTGbbuttonActionPerformed
        settings.setColorationBias(6); roadMapPanel.draw();
    }//GEN-LAST:event_relativeTGbbuttonActionPerformed

    private void relativeToFbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToFbuttonActionPerformed
        settings.setColorationBias(7); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToFbuttonActionPerformed

    private void relativeToEbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToEbuttonActionPerformed
        settings.setColorationBias(8); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToEbuttonActionPerformed

    private void relativeToEbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToEbbuttonActionPerformed
       settings.setColorationBias(9); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToEbbuttonActionPerformed

    private void relativeToDbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToDbuttonActionPerformed
        settings.setColorationBias(10); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToDbuttonActionPerformed

    private void relativeToDbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeToDbbuttonActionPerformed
        settings.setColorationBias(11); roadMapPanel.draw();
    }//GEN-LAST:event_relativeToDbbuttonActionPerformed

    private void analyzeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeMenuItemActionPerformed
        analyzeInBackground(settings.showJoins);
    }//GEN-LAST:event_analyzeMenuItemActionPerformed

    private void unselectAllMenuItemClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unselectAllMenuItemClicked
        deselectBricks();
    }//GEN-LAST:event_unselectAllMenuItemClicked

    private void titleBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleBoxActionPerformed
       acceptPreferences();
    }//GEN-LAST:event_titleBoxActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
                roadMapTextEntry.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void saveToNewLeadsheetMIaction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToNewLeadsheetMIaction
          saveToNewNotate();          
    }//GEN-LAST:event_saveToNewLeadsheetMIaction

    private void stopButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonPressed
         stopPlayingSelection();
    }//GEN-LAST:event_stopButtonPressed

    private void insertBrickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertBrickButtonActionPerformed
        addBrickFromPreview();
    }//GEN-LAST:event_insertBrickButtonActionPerformed

private void playSelectionMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playSelectionMIActionPerformed
  {//GEN-HEADEREND:event_playSelectionMIActionPerformed
   if( !roadMapTextEntry.isFocusOwner() )
     {
       playSelection();
     }
  }//GEN-LAST:event_playSelectionMIActionPerformed

private void playAllMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playAllMIActionPerformed
  {//GEN-HEADEREND:event_playAllMIActionPerformed
   if( !roadMapTextEntry.isFocusOwner() )
     {  
     selectAllBricks();
     playSelection();
     deselectBricks();
     }
  }//GEN-LAST:event_playAllMIActionPerformed

private void stopPlayMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopPlayMIActionPerformed
  {//GEN-HEADEREND:event_stopPlayMIActionPerformed
  stopPlayingSelection();
 }//GEN-LAST:event_stopPlayMIActionPerformed

private void playMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playMenuActionPerformed
  {//GEN-HEADEREND:event_playMenuActionPerformed

  }//GEN-LAST:event_playMenuActionPerformed

private void showJoinsCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showJoinsCheckBoxMIActionPerformed
  {//GEN-HEADEREND:event_showJoinsCheckBoxMIActionPerformed
    setShowJoins(showJoinsCheckBoxMI.getState());
    roadMapPanel.draw();
  }//GEN-LAST:event_showJoinsCheckBoxMIActionPerformed

private void dictionaryEditorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dictionaryEditorMIActionPerformed
  {//GEN-HEADEREND:event_dictionaryEditorMIActionPerformed
    openDictionaryEditor();
  }//GEN-LAST:event_dictionaryEditorMIActionPerformed

private void roadMapStatustextualEntryKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_roadMapStatustextualEntryKeyPressed
  {//GEN-HEADEREND:event_roadMapStatustextualEntryKeyPressed
    // TODO add your handling code here:
  }//GEN-LAST:event_roadMapStatustextualEntryKeyPressed

private void saveDictionaryAsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveDictionaryAsMIActionPerformed
  {//GEN-HEADEREND:event_saveDictionaryAsMIActionPerformed
    saveDictionaryAs();
  }//GEN-LAST:event_saveDictionaryAsMIActionPerformed

private void barsPerLineComboBoxscaleComboReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_barsPerLineComboBoxscaleComboReleased
  {//GEN-HEADEREND:event_barsPerLineComboBoxscaleComboReleased
    roadMapScrollPane.requestFocus();
  }//GEN-LAST:event_barsPerLineComboBoxscaleComboReleased

private void barsPerLineComboBoxscaleChosen(java.awt.event.ActionEvent evt)//GEN-FIRST:event_barsPerLineComboBoxscaleChosen
  {//GEN-HEADEREND:event_barsPerLineComboBoxscaleChosen
    settings.setBarsPerLine(Integer.parseInt((String)barsPerLineComboBox.getSelectedItem()));
  }//GEN-LAST:event_barsPerLineComboBoxscaleChosen

private void allVolumeToolBarSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_allVolumeToolBarSliderStateChanged
  {//GEN-HEADEREND:event_allVolumeToolBarSliderStateChanged
    setVolumeSlider(allVolumeToolBarSlider.getValue());
  }//GEN-LAST:event_allVolumeToolBarSliderStateChanged

private void tempoSetMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tempoSetMousePressed
  {//GEN-HEADEREND:event_tempoSetMousePressed
     tempoSet.requestFocusInWindow();
   }//GEN-LAST:event_tempoSetMousePressed

private void tempoSetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tempoSetActionPerformed
  {//GEN-HEADEREND:event_tempoSetActionPerformed
     setTempo(Notate.intFromTextField(tempoSet, Notate.MIN_TEMPO, Notate.MAX_TEMPO, (int)notate.getDefaultTempo()));   
}//GEN-LAST:event_tempoSetActionPerformed

private void tempoSetFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tempoSetFocusGained
  {//GEN-HEADEREND:event_tempoSetFocusGained

     //tempoSetOldTempo = tempoSet.getText();   
}//GEN-LAST:event_tempoSetFocusGained

private void tempoSetFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tempoSetFocusLost
  {//GEN-HEADEREND:event_tempoSetFocusLost

     updateTempoFromTextField();
}//GEN-LAST:event_tempoSetFocusLost

private void tempoSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tempoSliderStateChanged
  {//GEN-HEADEREND:event_tempoSliderStateChanged

     if( jSliderIgnoreStateChangedEvt )
       {
         return;
       }
     int value = tempoSlider.getValue();
     value = 2 * Math.round(value / 2);
     setTempo(value);
     //setPlaybackManagerTime(); 
}//GEN-LAST:event_tempoSliderStateChanged

private void lowerMetreActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lowerMetreActionPerformed
  {//GEN-HEADEREND:event_lowerMetreActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_lowerMetreActionPerformed

private void showBrickNamesCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showBrickNamesCheckBoxMIActionPerformed
  {//GEN-HEADEREND:event_showBrickNamesCheckBoxMIActionPerformed
      settings.showBrickNames = showBrickNamesCheckBoxMI.getState();
      roadMapPanel.draw();
  }//GEN-LAST:event_showBrickNamesCheckBoxMIActionPerformed

private void showKeysCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showKeysCheckBoxMIActionPerformed
  {//GEN-HEADEREND:event_showKeysCheckBoxMIActionPerformed
      settings.showKeys = showKeysCheckBoxMI.getState();
      roadMapPanel.draw();
  }//GEN-LAST:event_showKeysCheckBoxMIActionPerformed

private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reloadButtonActionPerformed
  {//GEN-HEADEREND:event_reloadButtonActionPerformed
    reloadDictionary();
  }//GEN-LAST:event_reloadButtonActionPerformed

private void keyPressedInDictionaryFrame(java.awt.event.KeyEvent evt)//GEN-FIRST:event_keyPressedInDictionaryFrame
  {//GEN-HEADEREND:event_keyPressedInDictionaryFrame

  }//GEN-LAST:event_keyPressedInDictionaryFrame

private void keyPressedDictionaryTree(java.awt.event.KeyEvent evt)//GEN-FIRST:event_keyPressedDictionaryTree
  {//GEN-HEADEREND:event_keyPressedDictionaryTree
    if( evt.getKeyChar() == java.awt.event.KeyEvent.VK_ENTER )
      {
        playSelection();
      }
  }//GEN-LAST:event_keyPressedDictionaryTree

private void styleChosen(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleChosen
  {//GEN-HEADEREND:event_styleChosen
    style = (Style)styleComboBox.getSelectedItem();
    roadMapScrollPane.requestFocus();
  }//GEN-LAST:event_styleChosen

private void playOnClickToggleButtonPressed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playOnClickToggleButtonPressed
  {//GEN-HEADEREND:event_playOnClickToggleButtonPressed
        if( playOnClickToggleButton.isSelected() )
          {
            playOnClickToggleButton.setText("<html><center>No Play<br>on Click</center></html>");
            playOnClickToggleButton.setBackground(Color.RED);
          }
        else
          {
            playOnClickToggleButton.setText("<html><center>Play<br>on Click</center></html>");
            playOnClickToggleButton.setBackground(Color.GREEN);
          }
  }//GEN-LAST:event_playOnClickToggleButtonPressed

    private void replaceWithPhiCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithPhiCheckBoxMIActionPerformed
        boolean checked = replaceWithPhiCheckBoxMI.getState();
        updatePhiAndDelta(checked,replaceWithDeltaCheckBoxMI.getState());
        roadMapPanel.draw();
    }//GEN-LAST:event_replaceWithPhiCheckBoxMIActionPerformed

    private void replaceWithDeltaCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithDeltaCheckBoxMIActionPerformed
        boolean checked = replaceWithDeltaCheckBoxMI.getState();
        updatePhiAndDelta(replaceWithPhiCheckBoxMI.getState(),checked);
        roadMapPanel.draw();
    }//GEN-LAST:event_replaceWithDeltaCheckBoxMIActionPerformed

    private void noRomanNumeralsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noRomanNumeralsBtnActionPerformed
        settings.setRomanNumeralHomeKey(""); roadMapPanel.draw();
    }//GEN-LAST:event_noRomanNumeralsBtnActionPerformed

    private void romanNumeralRelativeToCbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToCbuttonActionPerformed
        settings.setRomanNumeralHomeKey("c"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToCbuttonActionPerformed

    private void romanNumeralRelativeToBbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToBbuttonActionPerformed
        settings.setRomanNumeralHomeKey("b"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToBbuttonActionPerformed

    private void romanNumeralRelativeToBbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToBbbuttonActionPerformed
        settings.setRomanNumeralHomeKey("bb"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToBbbuttonActionPerformed

    private void romanNumeralRelativeToAbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToAbuttonActionPerformed
        settings.setRomanNumeralHomeKey("a"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToAbuttonActionPerformed

    private void romanNumeralRelativeToAbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToAbbuttonActionPerformed
        settings.setRomanNumeralHomeKey("ab"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToAbbuttonActionPerformed

    private void romanNumeralRelativeToGbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToGbuttonActionPerformed
        settings.setRomanNumeralHomeKey("g"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToGbuttonActionPerformed

    private void romanNumeralRelativeTGbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeTGbbuttonActionPerformed
        settings.setRomanNumeralHomeKey("gb"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeTGbbuttonActionPerformed

    private void romanNumeralRelativeToFbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToFbuttonActionPerformed
        settings.setRomanNumeralHomeKey("f"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToFbuttonActionPerformed

    private void romanNumeralRelativeToEbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToEbuttonActionPerformed
        settings.setRomanNumeralHomeKey("e"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToEbuttonActionPerformed

    private void romanNumeralRelativeToEbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToEbbuttonActionPerformed
        settings.setRomanNumeralHomeKey("eb"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToEbbuttonActionPerformed

    private void romanNumeralRelativeToDbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToDbuttonActionPerformed
        settings.setRomanNumeralHomeKey("d"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToDbuttonActionPerformed

    private void romanNumeralRelativeToDbbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romanNumeralRelativeToDbbuttonActionPerformed
        settings.setRomanNumeralHomeKey("db"); roadMapPanel.draw();
    }//GEN-LAST:event_romanNumeralRelativeToDbbuttonActionPerformed

    private void copyToTextMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyToTextMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) copySelectionToTextWindow();
    }//GEN-LAST:event_copyToTextMenuItemActionPerformed

    private void showStartingNoteCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showStartingNoteCheckBoxMIActionPerformed
        settings.showStartingNote = showStartingNoteCheckBoxMI.getState();
        roadMapPanel.draw();
    }//GEN-LAST:event_showStartingNoteCheckBoxMIActionPerformed

    private void showVariantsCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showVariantsCheckBoxMIActionPerformed
       settings.showVariants = showVariantsCheckBoxMI.getState(); // TODO add your handling code here:
       roadMapPanel.draw();
    }//GEN-LAST:event_showVariantsCheckBoxMIActionPerformed

    private void showStylesCheckBoxMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showStylesCheckBoxMIActionPerformed
        settings.showStyles = showStylesCheckBoxMI.getState();
        roadMapPanel.draw();
    }//GEN-LAST:event_showStylesCheckBoxMIActionPerformed

    private void transposeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_transposeSpinnerStateChanged
        setPlayTransposed();
    }//GEN-LAST:event_transposeSpinnerStateChanged

private Notate.StyleComboBoxModel getStyleMenuModel()
  {
    return styleComboBoxModel;
  }
        
public void setVolumeSlider(int volume)
  {
    if( jSliderIgnoreStateChangedEvt )
      {
        return;
      }

    jSliderIgnoreStateChangedEvt = true;
    
    allVolumeToolBarSlider.setValue(volume);
    
    notate.setSliderVolumes(volume);

    jSliderIgnoreStateChangedEvt = false;
  }

//</editor-fold>
    /** Creates the play timer and adds a listener */
    private void initTimer()
    {
        playTimer = new javax.swing.Timer(10,
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        if(notate.getMidiSynthRM().finishedPlaying())
                        {
                            setPlaying(false);
                            roadMapPanel.draw();
                            return;
                        }
                        
                        ///*
                        Rectangle playline = getRoadMapPanel().getPlayline();

                        if( playline != null && playline.height == 0 )
                        {
                            roadMapPanel.draw();
                            //System.out.println("checkpoint 11111");
                            return;
                        }

                        Rectangle viewport = getRoadMapScrollPane().getViewport().getViewRect();
                        //System.out.println("checkpoint 22222");
                        
                        if( viewport != null && playline != null )
                        {
                        Boolean temp = viewport.contains(playline);
                        //System.out.println(temp + "");
                        
                        //System.out.println("viewport " + viewport.toString());
                        //System.out.println("playline " + playline.toString());

                        if( !viewport.contains(playline) )
                        {
                            // If out of view, try adjusting x-coordinate first
                            //System.out.println("checkpoint <---->");
                            int adjust = 20 + 10; //240 is approx. left indent

                            if( viewport.width < adjust )
                            {
                                adjust = 0;
                            }

                            viewport.x = playline.x - adjust;
                            
                            if( viewport.x < 0 )
                            {
                                //System.out.println("checkpoint <----> ++");
                                viewport.x = 0;
                            }
                        }

                        // If still out of view, try adjusting the y-coordinate

                        if( !viewport.contains(playline) )
                        {
                            viewport.y = playline.y;
                            //System.out.println("checkpoint ^^^^^^");
                            if( playline.y < 0 )
                            {
                                playline.y = 0;
                                //System.out.println("checkpoint ^^^^^^ ++");
                            }
                        }

                        if( viewport.contains(playline) )
                        {
                            setCurrentScrollPosition(viewport);
                            //System.out.println("checkpoint finale");
                        }
                        }
                        //*/
                        roadMapPanel.draw();
                    }
                }
                );
    }
    
    public void setCurrentScrollPosition(Rectangle r) {
   //System.out.println("setCurrentScrollPosition(" + r + ")");
        getRoadMapScrollPane().getViewport().setViewPosition(r.getLocation());
    }
    
    public RoadMapPanel getRoadMapPanel()
    {
        return roadMapPanel;
    }
    
    public JScrollPane getRoadMapScrollPane()
    {
        return roadMapScrollPane;
    }
    
    /** Initializes the buffers for the roadmap and preview panel. */
    private void initBuffer()
    {
      try 
        {
        bufferPreviewPanel = new BufferedImage(previewBufferWidth, previewBufferHeight, BufferedImage.TYPE_INT_RGB);
        bufferRoadMap = new BufferedImage(roadMapBufferWidth, roadMapBufferHeight, BufferedImage.TYPE_INT_RGB);
        previewPanel.setBuffer(bufferPreviewPanel);
        roadMapPanel.setBuffer(bufferRoadMap);
        
        
        roadMapScrollPane.setViewportView(roadMapPanel);
        
        roadMapPanel.draw();
        previewPanel.draw();
        }
      catch( java.lang.OutOfMemoryError e)
        {
        ErrorLog.log(ErrorLog.SEVERE, "Out of memory. It might not be possible to continue.");
        }
    }
    
    /** Builds the library tree from the brick library */
    private void initLibraryTree()
    {
        LinkedList<LinkedList<Brick>> bricks = brickLibrary.getMap();
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        
        ArrayList<String> categoryNames = new ArrayList<String>(Arrays.asList(brickLibrary.getTypes()));
        ArrayList<DefaultMutableTreeNode> categories = new ArrayList<DefaultMutableTreeNode>();
        
        for(String name : categoryNames)
          {
          categories.add(new DefaultMutableTreeNode(name));
          }
        
        DefaultMutableTreeNode node;
        
        for( LinkedList<Brick> variants : bricks )
        {
            Brick brick = variants.getFirst();
            String name = brick.getName();
            String type = brick.getType();
            
            node = new DefaultMutableTreeNode(name);
            
            if(variants.size() > 1)
              {
                for( Brick variant : variants)
                    {
                      node.add(new DefaultMutableTreeNode(variant.getVariant()));
                    }
              }
             
            int ind = categoryNames.indexOf(type);
            
            if(ind != -1)
              {
                categories.get(ind).add(node);
              }
            else
              {
                ErrorLog.log(ErrorLog.WARNING, type + " is not in type list.");
              }
        }
        
        for(DefaultMutableTreeNode type : categories)
          {
            if( !type.toString().equals(INVISIBLE) )
              {
              root.add(type);
              }
          }
        
        libraryTreeModel = new DefaultTreeModel(root);
    }
    
    /** Recycle buffer memory by setting them to null*/
    private void disposeBuffers()
    {
        bufferPreviewPanel = null;
        bufferRoadMap = null;
        previewPanel.setBuffer(null);
        roadMapPanel.setBuffer(null);
    }

    /** Paints the image white.
     * @param image, an Image
     */
    public void setBackground(Image image)
    {
      if( image != null )
        {
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
        }
    }
    
    /** setBackgrounds <p>
     * Sets the background of each bufferPreviewPanel.
     */
    public void setBackgrounds()
    {
        setBackground(bufferPreviewPanel);
        setBackground(bufferRoadMap);
    }
 
    /** Sets the roadmap and frame title to the given string */
    public void setRoadMapTitle(String title)
    {
        
        setTitle(roadMapTitlePrefix + title);
        roadMapTitle = title;
        roadMapPanel.draw();
    }
    
   
    /** Returns the current graphical settings */
    public RoadMapSettings getSettings()
    {
        return settings;
    }
    
    /** Returns the brick library */
    public BrickLibrary getLibrary()
    {
        return brickLibrary;
    }
    
    /** Returns the chords in the current selection */
    public ArrayList<ChordBlock> getChordsInSelection()
    {
        return RoadMap.getChords(roadMapPanel.getSelection());
    }
    
    /** Saves the current state of the roadmap */
    private void saveState(String name)
    {
        stopPlayingSelection(); //TODO this probably doesn't belong here,
        //but I don't want to write it over and over again and this is called for
        //in relevant actions
        if(name.equals("Transpose") &&
                roadMapHistory.getLast().getName().equals("Transpose"))
          {
            return;
          } //Multiple transpositions should be the same action
            //ISSUE: changing multiple bricks in render undoes them all
        RoadMapSnapShot ss = new RoadMapSnapShot(name, roadMapPanel.getRoadMap());
        roadMapHistory.add(ss);
        roadMapFuture.clear();
    }
    
    /** Reverts to the previous state */
    private void stepStateBack()
    {
        if(roadMapHistory.peek() != null) {
            RoadMapSnapShot ss = roadMapHistory.removeLast();
            roadMapFuture.add(new RoadMapSnapShot(ss.getName(), roadMapPanel.getRoadMap()));
            roadMapPanel.setRoadMap(ss.getRoadMap());
        }
    }
    
    /** Verts to the next state */
    private void stepStateForward()
    {
        if(roadMapFuture.peek() != null) {
            RoadMapSnapShot ss = roadMapFuture.removeLast();
            roadMapHistory.add(new RoadMapSnapShot(ss.getName(), roadMapPanel.getRoadMap()));
            roadMapPanel.setRoadMap(ss.getRoadMap());
        }
    }
    
    /** Undoes the any actions performed */
    private void undo()
    {
        deselectBricks();
        stepStateBack();
        //System.out.println("History: " + roadMapHistory);
        //System.out.println("Future: " + roadMapFuture);
        roadMapPanel.placeBricks();
    }
    
    /** Redoes any undone action */
    private void redo()
    {
        deselectBricks();
        stepStateForward();
        //System.out.println("History: " + roadMapHistory);
        //System.out.println("Future: " + roadMapFuture);
        roadMapPanel.placeBricks();
    }
       
    /* -------- Actions -------- */
    
    /** Action to add a chord to the roadmap */
    public void addChord(ChordBlock chord)
    {
        saveState("AddChord");
        roadMapPanel.addBlock(chord);
        roadMapPanel.placeBricks();
    }
    
    /** Action to insert a list of blocks into the roadmap */
    public void addBlocks(int ind, ArrayList<Block> blocks)
    {
        saveState("AddBricks");
        roadMapPanel.addBlocks(ind, blocks);
        roadMapPanel.placeBricks();
    }
    
    /** Action to delete the selection */
    public void deleteSelection()
    {
        saveState("Delete");
        roadMapPanel.deleteSelection();
        deactivateButtons();
    }
    
    /** Action to break the selected bricks */
    public void breakSelection()
    {
        saveState("Break");
        roadMapPanel.breakSelection();
    }
    
    /** Action to copy the chords of the selection to the text entry window */
    public void copySelectionToTextWindow(){
        String chordText = roadMapPanel.copySelectionToTextWindow();
        roadMapTextEntry.setText(chordText);
    }
    
    /** Action to create a new brick from the selection */
    public void makeBrickFromSelection()
    {
        saveState("Merge");
        long key = BrickLibrary.keyNameToNum((String) dialogKeyComboBox.getSelectedItem());
        String name = dialogNameField.getText();
        
        String variant = "";
        if(brickLibrary.hasBrick(name))
            variant = dialogVariantField.getText();
        
        String mode = (String)dialogModeComboBox.getSelectedItem();
        String type = (String)dialogTypeComboBox.getSelectedItem();
        Brick newBrick = roadMapPanel.makeBrickFromSelection(name, variant, key,
                                                             mode, type);
        addToLibrary(newBrick);
    }

    /** Action to transpose the key of the selection */
    public void transposeSelection(long diff)
    {
        saveState("Transpose");
        roadMapPanel.transposeSelection(diff);
    }
    
    /** Action to analyze the selection */
    public void analyzeSelection(boolean showJoinsOnCompletion)
    {
        saveState("Analyze");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        ArrayList<Block> blocks = roadMapPanel.getSelection();
        roadMapPanel.replaceSelection(analyze(blocks));
        
        setShowJoins(showJoinsOnCompletion);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /** Action to flatten the selected bricks */
    public void flattenSelection()
    {
        saveState("Flatten");
        roadMapPanel.flattenSelection();
    }
    
    /** Action to scale the durations of the selected bricks */
    public void scaleSelection()
    {
        saveState("Scale");
        String choice = (String)scaleComboBox.getSelectedItem();
        
        if( choice == null )
            return;
        
        int scale = choice.charAt(1) - 48; // set to integer
        
        if( choice.charAt(0) == 47) //  / = division
            scale = -scale;
        
        roadMapPanel.scaleSelection(scale);       
    }
    
    /** Action to change a chord's name and/or duration */
    public void changeChord(String name, int dur)
    {
        saveState("ChordChange");
        roadMapPanel.changeChord(name, dur);
    }
    
    /** Action to add/remove a phrase end */
    private void togglePhraseEnd()
    {
        saveState("PhraseEnd");
        roadMapPanel.togglePhrase();
    }
    
    /** Action to add/remove a section end */
    private void toggleSectionBreak()
    {
        saveState("SectionBreak");
        roadMapPanel.toggleSection();
    }
    
    /** Action to add a section end to the end of the roadmap */
    public void endSection()
    {
        saveState("SectionBreak");
        roadMapPanel.endSection();
    }

    /** Action to cut the selection, adding it to the clipboard */
    public void cutSelection()
    {
        saveState("Cut");
        clipboard = roadMapPanel.removeSelection();
        roadMapPanel.placeBricks();
    }
    
    /** Action to paste the selection, adding it to the roadmap from the clipboard */
    public void pasteSelection()
    {
        saveState("Paste");
        
        ArrayList<Block> blocks = RoadMap.cloneBlocks(clipboard);
        
        roadMapPanel.addBlocksBeforeSelection(blocks, false);
        
        roadMapPanel.placeBricks();
    }
    
    /** Action to copy the selection, adding it to the clipboard */
    public void copySelection()
    {
        clipboard = RoadMap.cloneBlocks(roadMapPanel.getSelection());        
    }
    
    /** Implements dragging behavior.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dragSelectedBricks(int x, int y)
    {   
        int index = roadMapPanel.getBrickIndexAt(x, y);
        if( draggedBricks.isEmpty() ) {
            saveState("Drag");
            roadMapPanel.setRolloverPos(null);
            if( !roadMapPanel.isSelection(index))
                selectBrick(index);
            if( roadMapPanel.hasSelection() )
                draggedBricks = roadMapPanel.makeBricks(roadMapPanel.removeSelectionNoUpdate());
        }
        
        if( !draggedBricks.isEmpty() ) {
            roadMapPanel.setInsertLine(x, y);
            roadMapPanel.draw();
            roadMapPanel.drawBricksAt(draggedBricks, x, y);
        }
    }
    
    /** Implements dropping behavior.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dropCurrentBrick(int x, int y)
    {   
        if( !draggedBricks.isEmpty() ) {
            int index = roadMapPanel.getIndexAt(x, y);
            roadMapPanel.addBlocks(index, roadMapPanel.makeBlocks(draggedBricks), true);
            draggedBricks.clear();
            roadMapPanel.setInsertLine(-1);
        }
        roadMapPanel.placeBricks();
    }
    
    /** Implements dragging behavior from the preview window.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dragFromPreview(int x, int y) 
    {        
        if( draggedBricks.isEmpty() ) {
            roadMapPanel.deselectBricks();
            
            if (previewPanel.currentBrick != null) {
                draggedBricks.add(previewPanel.getBrick());
                setPreview();
            }
        }
        dragSelectedBricks(x, y);
    }
    
    /** Implements dropping behavior from the preview window.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dropFromPreview(int x, int y)
    {
        saveState("Drop");
        dropCurrentBrick(x, y);
        activateButtons();
    }
    
    /** Adds the current preview brick to the roadmap */
    public void addBrickFromPreview()
    {
        saveState("Drop");
        ArrayList<Block> block = new ArrayList<Block>();
        Block preview = previewPanel.getBlock();
        if( preview == null )
          {
            return;
          }
        block.add(previewPanel.getBlock());
        roadMapPanel.addBlocksBeforeSelection(block, true);
        roadMapPanel.placeBricks();
    }
    
    /** Adds the current preview brick to the roadmap and plays it */
    public void addAndPlayBrickFromPreview()
    {
        addBrickFromPreview();
        playSelection();
    }
    
    /** Sets the preview brick (from the library), as well as its duration and key. */
    public void setPreview()
    {
        TreePath path = libraryTree.getSelectionPath();
        if(path != null) {

            int pathLength = path.getPathCount();
            
            if( pathLength > 2 ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)path.getParentPath().getLastPathComponent();
                Brick brick;
                
                if(pathLength > 3 )
                  {
                    brick = brickLibrary.getBrick(parent.toString(),node.toString(), 0);
                  }
                else
                  {
                    brick = brickLibrary.getBrick(node.toString(), 0);
                  }
                
                //setDurationChoices(brick);
                
                if( brick != null )  // This has happened
                  {
                  previewPanel.setBrick( brick );
                  }

                setPreviewKey();
                setPreviewDuration();
            }
            
        }
    }
    
    /** Sets the key of the brick in the preview pane to the key chosen by
     * the key spinner. */
    public void setPreviewKey()
    {
        String key = (String)keyComboBox.getSelectedItem();
        if(BrickLibrary.isValidKey(key))
          {
            previewPanel.setKey( key );
          }
    }
    
    /** Sets the duration of the brick in the preview pane to the key chosen by
     * the duration combo box. */
    public void setPreviewDuration()
    {
        previewPanel.setDuration(settings.getSlotsPerBeat()*(Integer)durationChoices[durationComboBox.getSelectedIndex()]);
        previewPanel.draw();
    }
           
    /** Adds the brick at index to the selection, either extending the selection
     * or reducing it depending on whether the brick is selected. 
     * @param index, the index of the brick to be selected */
    public void selectBricks(int index)
    {
        roadMapPanel.selectBricks(index);
        activateButtons();   
    }
    
    /** Selects all bricks in the roadmap */
    public void selectAllBricks()
    {
        roadMapPanel.selectAll();
        activateButtons();
    }
    
    /** Selects only the brick at index, deselecting all other bricks.
     * @param index, the index of the brick to be selected */
    public void selectBrick(int index)
    {
        roadMapPanel.selectBrick(index);
        activateButtons();
    }
    
    /** Selects a chord within a brick
     * @param brickInd the index of the brick
     * @param chordInd the index of the chord within the brick */
    public void selectChord(int brickInd, int chordInd)
    {
        roadMapPanel.selectChord(brickInd, chordInd);
        deactivateButtons();
        deleteMenuItem.setEnabled(true);
        sectionMenu.setEnabled(true);
    }
    
    /** Deselects all bricks. */
    public void deselectBricks()
    {
        roadMapPanel.deselectBricks();
        deactivateButtons();
    }
       
    /** Uses cykParser to analyze a list of blocks */
    public ArrayList<Block> analyze(ArrayList<Block> blocks)
    {
        long startTime = System.currentTimeMillis();
        ArrayList<Block> result = cykParser.parse(blocks, brickLibrary);
        long endTime = System.currentTimeMillis();
        //ErrorLog.log(ErrorLog.WARNING, "Analysis: " + (endTime - startTime) + "ms");
        
        return result;
    }  
  
    /** Deactivates relevant buttons for selection */
    public void deactivateButtons()
    {
        setButtonEnabled(false);
    }
    
    /** Activates relevant buttons for selection */
    public void activateButtons()
    {
        setButtonEnabled(true);
    }
    
    /** Activates/Deactivates relecent buttons for selection */
    public void setButtonEnabled(boolean value)
    {
        cutMenuItem.setEnabled(value);
        copyMenuItem.setEnabled(value);
        
        transposeMenu.setEnabled(value);
        
        sectionMenu.setEnabled(value);
        
        flattenButton.setEnabled(value);
        flattenMenuItem.setEnabled(value);
       
        deleteMenuItem.setEnabled(value);
        
        breakButton.setEnabled(value);
        breakMenuItem.setEnabled(value);
        
        newBrickButton.setEnabled(value);
        scaleComboBox.setEnabled(value);
    }

    /** Adds a brick to the brick library */
    private void addToLibrary(Brick brick)
    {
        Brick scaledBrick = new Brick(brick);
        scaledBrick.reduceDurations();
        brickLibrary.addBrickDefinition(scaledBrick, dictionaryFilename);
        cykParser.createRules(brickLibrary);
        initLibraryTree();
        libraryTree.setModel(libraryTreeModel);
        if (scaledBrick.getVariant().isEmpty())
          {
            addToLibraryTree(scaledBrick.getName());
          }
        else
          {
            addToLibraryTree(scaledBrick.getName(), scaledBrick.getVariant());
          }
    }
    
    /** Adds a brick name to the library tree */
    private void addToLibraryTree(String name)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)libraryTreeModel.getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getLastChild();
        
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        if(node.toString().equals("Recently Created")) {
            libraryTreeModel.insertNodeInto(newNode, node, node.getChildCount());
        } else {
            DefaultMutableTreeNode newParent = new DefaultMutableTreeNode("Recently Created");
            newParent.add(newNode);
            libraryTreeModel.insertNodeInto(newParent, root, root.getChildCount());
        }
    }
    
    /** Adds a brick by name and variant to the library tree */
    private void addToLibraryTree(String name, String variant)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)libraryTreeModel.getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getLastChild();
        
        DefaultMutableTreeNode variantNode= new DefaultMutableTreeNode(variant);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        newNode.add(variantNode);
        if(node.toString().equals("Recently Created")) {
            libraryTreeModel.insertNodeInto(newNode, node, node.getChildCount());
        } else {
            DefaultMutableTreeNode newParent = new DefaultMutableTreeNode("Recently Created");
            newParent.add(newNode);
            libraryTreeModel.insertNodeInto(newParent, root, root.getChildCount());
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog addBrickDialog;
    private javax.swing.JSlider allVolumeToolBarSlider;
    private javax.swing.JButton analyzeButton;
    private javax.swing.JMenuItem analyzeMenuItem;
    private javax.swing.JComboBox barsPerLineComboBox;
    private javax.swing.JButton breakButton;
    private javax.swing.JMenuItem breakMenuItem;
    private javax.swing.JFrame brickDictionaryFrame;
    private javax.swing.JCheckBoxMenuItem brickLibraryMenuItem;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JDialog chordChangeDialog;
    private javax.swing.JButton chordDialogAcceptButton;
    private javax.swing.JComboBox chordDialogDurationComboBox;
    private javax.swing.JTextField chordDialogNameField;
    private javax.swing.JButton clearButton;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JMenu colorationPreferences;
    private javax.swing.ButtonGroup colorationPreferencesButtonGroup;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem copyToTextMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JButton dialogAcceptButton;
    private javax.swing.JButton dialogCancelButton;
    private javax.swing.JComboBox dialogKeyComboBox;
    private javax.swing.JLabel dialogKeyLabel;
    private javax.swing.JComboBox dialogModeComboBox;
    private javax.swing.JTextField dialogNameField;
    private javax.swing.JLabel dialogNameLabel;
    private javax.swing.JComboBox dialogTypeComboBox;
    private javax.swing.JLabel dialogTypeLabel;
    private javax.swing.JTextField dialogVariantField;
    private javax.swing.JLabel dialogVariantLabel;
    private javax.swing.JMenuItem dictionaryEditorMI;
    private javax.swing.JMenu dictionaryMenu;
    private javax.swing.JComboBox durationComboBox;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu editorMenu;
    private javax.swing.JSlider featureWidthSlider;
    private javax.swing.JButton fileStepBackBtn;
    private javax.swing.JButton fileStepForwardBtn;
    private javax.swing.JRadioButtonMenuItem fixedColorsRadioBtn;
    private javax.swing.JButton flattenButton;
    private javax.swing.JMenuItem flattenMenuItem;
    private javax.swing.JButton insertBrickButton;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToggleButton keyColorationButton;
    private javax.swing.JComboBox keyComboBox;
    private javax.swing.JScrollPane libraryScrollPane;
    private javax.swing.JTree libraryTree;
    private javax.swing.JToggleButton loopToggleButton;
    private javax.swing.JTextField lowerMetre;
    private javax.swing.JPanel masterVolumePanel;
    private javax.swing.JButton newBrickButton;
    private javax.swing.JRadioButtonMenuItem noRomanNumeralsBtn;
    private javax.swing.JMenuItem openLeadsheetMI;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem playAllMI;
    private javax.swing.JButton playButton;
    private javax.swing.JMenu playMenu;
    private javax.swing.JToggleButton playOnClickToggleButton;
    private javax.swing.JMenuItem playSelectionMI;
    private javax.swing.JButton prefDialogAcceptButton1;
    private javax.swing.JButton prefDialogCancelButton;
    private javax.swing.JLabel prefDialogMeterLabel;
    private javax.swing.JTextField prefDialogTitleField;
    private javax.swing.JDialog preferencesDialog;
    private javax.swing.JMenu preferencesMenu;
    private javax.swing.JMenuItem preferencesMenuItem;
    private javax.swing.JScrollPane previewScrollPane;
    private javax.swing.JMenuItem printRoadMapMI;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JRadioButtonMenuItem relativeTGbbutton;
    private javax.swing.JRadioButtonMenuItem relativeToAbbutton;
    private javax.swing.JRadioButtonMenuItem relativeToAbutton;
    private javax.swing.JRadioButtonMenuItem relativeToBbbutton;
    private javax.swing.JRadioButtonMenuItem relativeToBbutton;
    private javax.swing.JRadioButtonMenuItem relativeToCbutton;
    private javax.swing.JRadioButtonMenuItem relativeToDbbutton;
    private javax.swing.JRadioButtonMenuItem relativeToDbutton;
    private javax.swing.JRadioButtonMenuItem relativeToEbbutton;
    private javax.swing.JRadioButtonMenuItem relativeToEbutton;
    private javax.swing.JRadioButtonMenuItem relativeToFbutton;
    private javax.swing.JRadioButtonMenuItem relativeToGbutton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JCheckBoxMenuItem replaceWithDeltaCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem replaceWithPhiCheckBoxMI;
    private javax.swing.JScrollPane roadMapScrollPane;
    private javax.swing.JTextField roadMapStatus;
    private javax.swing.JTextField roadMapTextEntry;
    private javax.swing.JMenuBar roadmapMenuBar;
    private javax.swing.JMenu romanNumeralPreferences;
    private javax.swing.ButtonGroup romanNumeralPreferencesButtonGroup;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeTGbbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToAbbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToAbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToBbbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToBbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToCbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToDbbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToDbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToEbbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToEbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToFbutton;
    private javax.swing.JRadioButtonMenuItem romanNumeralRelativeToGbutton;
    private javax.swing.JMenuItem saveAsToNewLeadsheetMI;
    private javax.swing.JMenuItem saveDictionaryAsMI;
    private javax.swing.JMenuItem saveToNewLeadsheetMI;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JMenu sectionMenu;
    private javax.swing.JButton selectAllBricksButton;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JCheckBoxMenuItem showBrickNamesCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem showJoinsCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem showKeysCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem showStartingNoteCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem showStylesCheckBoxMI;
    private javax.swing.JCheckBoxMenuItem showVariantsCheckBoxMI;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenuItem stopPlayMI;
    private javax.swing.JComboBox styleComboBox;
    private javax.swing.JPanel tempoPanel;
    private javax.swing.JTextField tempoSet;
    private javax.swing.JSlider tempoSlider;
    private javax.swing.JMenuItem togglePhraseMenuItem;
    private javax.swing.JMenuItem toggleSectionMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuItem transposeDownMenuItem;
    private javax.swing.JMenu transposeMenu;
    private javax.swing.JSpinner transposeSpinner;
    private javax.swing.JMenuItem transposeUpMenuItem;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenuItem unselectAllMenuItem;
    private javax.swing.JTextField upperMetre;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

 
    /** Sends the currently-selected blocks to a new Notate window called auxNotate.
     * Any existing associate with that window is detached and lost.
     *
     * If no blocks are selected, selects them all first.
     *
     * If the road map is empty, does nothing.
     */
    
public void saveToNewNotate()
  {
    selectAllBricks();

    ChordPart chordPart = new ChordPart();
    chordPart.setStyle(getStyle());

    chordPart.addFromRoadMapFrame(this);

    // A small hack to deal with a totally empty chord part.

    if( chordPart.size() == 0 )
      {
        chordPart.addChord("NC", 480);
      }

    Score score = new Score(chordPart);
    score.getPart(0).setUnit(0, new Rest(score.getChordProg().size()));
    score.setMetre(getMetre());
    score.setTempo(getTempo());
    score.setTitle(roadMapTitle);
    score.setStyle(getStyle());
    score.setDefaultLayout();
    score.setRoadmapLayout(settings.barsPerLine);

    //System.out.println("new score: " +  score);


    if( auxNotate == null )
      {
        // Save As ... branch
        auxNotate = notate.newNotateWithScore(score, getNewXlocation(), getNewYlocation());
        //System.out.println("auxNotate = " + auxNotate);
        auxNotate.forceNotateFrameHeight();
        auxNotate.setAutoCreateRoadMap(false);
        //auxNotate.setupScore(score);
        auxNotate.setVisible(true);
        auxNotate.saveAsLeadsheet();
      }
    else
      {
        // Save ... branch
        auxNotate.setupScore(score);
        auxNotate.setVisible(true);
        auxNotate.saveLeadsheet();
      }


    deselectBricks();
  }


public void setParent(Notate notate)
  {
    this.notate = notate;
  }


    
    /** Call from auxNotate when deleted to prevent dangling reference. */
    public void resetAuxNotate()
    {
        auxNotate = null;
    }

    /** Plays the currently-selected blocks. The style is determined from the
     * Notate window where this roadmap was opened.
     *
     * If no blocks are selected, selects them all first.
     *
     * If the road map is empty, does nothing.
     */
    
    public void playSelection() {
        if (roadMapPanel.getNumBlocks() < 1)
          {
            return;
          }
        
        boolean nothingSelected = !roadMapPanel.hasSelection();
        if (nothingSelected)
         {
          selectAllBricks();            
         }
         
        ChordPart chordPart = new ChordPart();
        ArrayList<Block> blocks = roadMapPanel.getSelection();
       
        chordPart.addFromRoadMapFrame(this);
 
        Score score = new Score(chordPart);
        score.setMetre(getMetre());
        score.setTempo(getTempo());
        score.setTransposition(roadMapTransposition);
        //System.out.print(score.getTransposition());
        int volume = allVolumeToolBarSlider.getValue();
        
        score.setMasterVolume(volume); 
        setVolumeSlider(volume);
         
        setPlaying(MidiPlayListener.Status.PLAYING, 0);
         
        if( loopToggleButton.isSelected() )
          {
            notate.playAscoreWithStyle(score, -1, score.getTransposition());
          }
        else
          {
            notate.playAscoreWithStyle(score, 0, score.getTransposition());
          }
        
        if( nothingSelected )
          {
            deselectBricks();
          }
    }

    /** Stops playback */
    public void stopPlayingSelection()
    {
        if(isPlaying()) {
            notate.stopPlayAscore();
            setPlaying(MidiPlayListener.Status.STOPPED, 0);
        }
    }
    
    /** Stops then restarts playback */
    public void restartPlayingSelection()
    {
        stopPlayingSelection();
        playSelection();
    }

    /** Set the playback status */
    public void setPlaying(MidiPlayListener.Status playing, int transposition)
    {
        isPlaying = playing;
        if(isPlaying()) {
            roadMapPanel.setPlayLineOffset();
            roadMapPanel.setPlaySection();
            playTimer.start();
        } else {
            playTimer.stop();
            roadMapPanel.draw();
        }
    }
    
    /** Sets the playback status */
    public void setPlaying(boolean status)
    {
        if(status)
          {
            setPlaying(MidiPlayListener.Status.PLAYING,0);
          }
        else
          {
            setPlaying(MidiPlayListener.Status.STOPPED,0);
          }
    }
    
    /** Returns the playback status */
    public MidiPlayListener.Status getPlaying()
    {
        return isPlaying;
    }

    /** Returns whether payback is active */
    public boolean isPlaying()
    {
        return isPlaying == MidiPlayListener.Status.PLAYING;
    }
    

    /** Close this RoadMapFrame and clean up. */
    public void closeWindow()
    {
        brickDictionaryFrame.setVisible(false); //TODO somehow make only one window
        if(isPlaying())
          {
            stopPlayingSelection();
          }
        WindowRegistry.unregisterWindow(this);

        if( notate != null )
          {
            notate.disestablishRoadMapFrame();
          }

        disposeBuffers();
        dispose();
        if( notate != null )
          {
            notate.setNormalMode();
          }
    }


    /** Get X location for new frame cascaded from original. */
    public int getNewXlocation()
    {
        return (int)getLocation().getX() + WindowRegistry.defaultXnewWindowStagger;
    }


    /** Get Y location for new frame cascaded from original. */
    public int getNewYlocation()
    {
        return (int)getLocation().getY() + WindowRegistry.defaultYnewWindowStagger;
    }


    /**
     * Set the height of specified RoadMapFrame so that it fills the screen.
     * This seems to work fine when the dock is at the right, but when
     * it is at the bottom, for some reason vertical staggering does not happen.
     * @param notate
     */
    public void setRoadMapFrameHeight()
    {
        int desiredWidth = RMframeWidth; // alternatively: dm.getWidth() - x
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices(); // Get size of each screen
        DisplayMode dm = gs[0].getDisplayMode();
        int x;
        int y;
        x = notate.getNewXlocation();
        y = notate.getNewYlocation();
        setLocation(x, y);
        setSize(desiredWidth, dm.getHeight() - y);
    }


    /** Make this RoadMapFrame visible */
    public void makeVisible(boolean analyze)
    {
      //System.out.println("makeVisible analyze = " + analyze);
        // Don't show joins until analysis is done.
        boolean showJoinsOnCompletion = settings.showJoins;
        
        //setVisible(true); 
        // Instead, it will be visible through "Roadmap this Leadsheet" under Roadmap tab
        if( brickLibraryMenuItem.isSelected() )
          {
          brickDictionaryFrame.setVisible(true);
          }
        
        if( analyze )
          {
            setShowJoins(false);
            analyzeInBackground(showJoinsOnCompletion);
          }
    }
    
    /**
     * Analyze in the background by creating Analyzer Thread.
     */
    
public void analyzeInBackground(boolean showJoinsOnCompletion)
  {
    final Analyzer analyzer = new Analyzer(this, showJoinsOnCompletion);
    analyzer.start();
    synchronized(analyzer)
      {
        try
          {
            analyzer.wait();
          }
        catch( Exception e )
          {
            System.out.println("Exception waiting for analyzer: " + e);
          }
      }
  }

    /** Sets the time signature of the roadmap for Americans
     * @param meter
     */
    public void setMeter(int meter[])
    {
        setMetre(meter);
    }

    /** Sets the time signature of the roadmap for the rest of the world
     * @param metre 
     */
    public void setMetre(int metre[])
    {
        this.metre[0] = metre[0];
        this.metre[1] = metre[1];
        settings.setMetre(metre);
    }

    /** Returns the time signature of the roadmap for Americans
     * @return 
     */
    public int[] getMeter()
    {
        return metre;
    }

    /** Returns the time signature of the roadmap for the rest of the world
     * @return 
     */
    public int[] getMetre()
    {
        return metre;
    }

    /** Returns the tempo */
    public int getTempo()
    {
        return Notate.intFromTextField(tempoSet, 
                                       Notate.MIN_TEMPO, 
                                       Notate.MAX_TEMPO, 
                                       (int)notate.getDefaultTempo());
    }

    /** Returns the style */ 
    public Style getStyle()
    {
        return style;
    }

    /** Gets the current playback slot from notate */
    public int getMidiSlot()
    {
        return notate.getMidiSlot();
    }

    /** Gets the roadmap's musical info from a score */
    public void setMusicalInfo(Score score)
    {
        setMetre(score.getMetre());
        setTempo((int)score.getTempo());
        setStyle(score.getStyle());
        setBarsPerLine(score.getRoadmapLayout());
    }

    /**
     * Set the bars per line parameter for the roadmap layout.
     * Assumes that exactly the integer values from 1 to size of the
     * ComboBoxModel are present in the model.
     * @param bars 
     */
    private void setBarsPerLine(int bars)
      {
        settings.setBarsPerLine(bars);
        
        ComboBoxModel model = barsPerLineComboBox.getModel();
        int size = model.getSize();
        if( bars < 1 || bars > size )
          {
            return; // cannot set 
          }
        barsPerLineComboBox.setSelectedIndex(bars-1);
      }
    
    /** Activate the preferences dialog and set the default values */
    private void activatePreferencesDialog()
    {
        prefDialogTitleField.setText(roadMapTitle);
        upperMetre.setText(String.valueOf(getMetre()[0]));
        lowerMetre.setText(String.valueOf(getMetre()[1]));
        preferencesDialog.setVisible(true);
    }
    
    /** Activate the chord change dialog and set the default values*/
    private void activateChordDialog()
    {
        ChordBlock chord = (ChordBlock)roadMapPanel.getSelection().get(0);
        chordDialogNameField.setText(chord.getName());
        chordDialogDurationComboBox.setSelectedItem(chord.getDuration()/settings.slotsPerBeat);
        chordChangeDialog.setLocation(roadMapPanel.getLocationOnScreen());
        chordChangeDialog.setVisible(true);
    }

    /** Gets the info from the preferences dialog */
    private void setRoadMapInfo()
    {
        setRoadMapTitle(prefDialogTitleField.getText());
        int metreTop = intFromTextField(upperMetre);
        int metreBottom = intFromTextField(lowerMetre);
        setMetre(new int[]{metreTop, metreBottom});
        style = (Style)styleComboBox.getSelectedItem();
        styleName = style.getName();
        roadMapPanel.updateBricks();
    }
    
    /** Scales the roadmap display to the current window size */
    private void scaleToWindow()
    {
        int width = roadMapScrollPane.getWidth()-roadMapScrollPane.getVerticalScrollBar().getWidth()-5;
        featureWidthSlider.setValue((width - 2*settings.xOffset)/settings.barsPerLine);    
        setFeatureWidthLocked(true);
    }
    
    /** Lock the feature width to scale to the window */
    private void setFeatureWidthLocked(boolean value)
    {
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)featureWidthSlider.getBorder();
        String title = featureWidthTitle;
        if(value)
          {
            title += " " + featureWidthSuffix;
          }
        border.setTitle(title);
    }
    
    
 /**
  * Populate the dictionary menu in the Roadmap window
  * Creates actionListener for each name in the menu.
  */

private void populateRoadmapDictionaryMenu(String dictionaryName)
  {
    dictionaryfc.setCurrentDirectory(ImproVisor.getDictionaryDirectory());
    
    File directory = dictionaryfc.getCurrentDirectory();
    if( directory.isDirectory() )
      {
        dictionaryMenu.removeAll();
        
        String fileName[] = directory.list();
        for( int i = 0; i < fileName.length; i++ )
          {
            String name = fileName[i];

            if( name.endsWith(DictionaryFilter.EXTENSION) )
              {
                int len = name.length();
                String stem = name.substring(0, len - DictionaryFilter.EXTENSION.length());
                final JCheckBoxMenuItem item = new JCheckBoxMenuItem(stem);
                dictionaryMenu.add(item);
                
                item.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        
                        if( recentlySelected != null )
                          {
                          // If some other item was, unselect it.
                          recentlySelected.setIcon(null);
                          }
                        
                        item.setSelected(true);

                        recentlySelected = item;

                        // Item wasn't selected, but is now.
                        
                        String newDictionaryName = item.getText();
                        
                        setDictionaryFilename(newDictionaryName);
                        newDictionary();
                        populateRoadmapDictionaryMenu(item.getText());
                       
                        if(!roadMapTextEntry.isFocusOwner())
                          {
                          brickDictionaryFrame.setVisible(true);
                          }
                      }
                });
              }
          }
      }
  }


public void setDictionaryFilename(String dictionaryName)
  {
    this.dictionaryName = dictionaryName;
    dictionaryFilename = ImproVisor.getDictionaryDirectory() + File.separator + dictionaryName + DictionaryFilter.EXTENSION;
    setDictionaryTitle(dictionaryName);
  }

/**
 * Create a new dictionary with the given name, based on a similarly-named
 * dictionary file.
 * @param dictionaryName 
 */

public void newDictionary()
  {
    newDictionary(dictionaryFilename);
  }

public void newDictionary(String dictionaryFilename)
  {
    //System.out.println("newDictionary: " + dictionaryFilename);
    try
      {
        brickLibrary = new BrickLibrary();
        brickLibrary.processDictionary(dictionaryFilename);
        cykParser.createRules(brickLibrary);
        initLibraryTree();
        libraryTree.setModel(libraryTreeModel);

        dialogTypeComboBox.removeAllItems();

        for( String type : brickLibrary.getTypes() )
          {
            dialogTypeComboBox.addItem(type);
          }
      }
    catch( Exception e )
      {
        // Usually redundant due to another error message
        //ErrorLog.log(ErrorLog.SEVERE, "In processing dictionary by RoadMap: " + e);
      }

  }


/**
 * Set the title for the dictionary on the dictionary frame and on
 * the menu bar.
 * @param dictionaryName 
 */
private void setDictionaryTitle(String dictionaryName)
  {
   String dictionaryTitle = dictionaryName + dictionaryNameSuffix;
   dictionaryMenu.setText(dictionaryTitle);
   brickDictionaryFrame.setTitle(dictionaryTitle); 
  }


/**
 * Analyze the selection if there is one.
 * If no selection, then analyze the entire roadmap.
 * Deselect everything following analysis in the second case.
 */

public void analyze(boolean showJoinsOnCompletion)
  {
    if( !roadMapPanel.hasSelection() )
      {
        roadMapPanel.selectAll();
        analyzeSelection(showJoinsOnCompletion);
        roadMapPanel.deselectBricks();
      }
    else
      {
        analyzeSelection(showJoinsOnCompletion);
      }
    
  //notate.getScore().getChordProg().setRoadmapPoly(roadMapPanel.getRoadmapPoly());
  }

public ArrayList<Block> getFullRoadMap()
  {
    roadMapPanel.selectAll();
    return analyze(roadMapPanel.getSelection());
  }

public String getDictionaryFilename()
  {
    return dictionaryFilename;
  }

private void openDictionaryEditor()
  {
      dictionaryEditor = new SourceEditorDialog(this, false, notate, null,
            SourceEditorDialog.DICTIONARY);

    dictionaryEditor.setRows(DICTIONARY_EDITOR_ROWS);
    dictionaryEditor.setSize(DICTIONARY_EDITOR_WIDTH, DICTIONARY_EDITOR_HEIGHT);
    dictionaryEditor.setLocation(DICTIONARY_EDITOR_X_OFFSET, DICTIONARY_EDITOR_Y_OFFSET);
    dictionaryEditor.fillEditor();
    dictionaryEditor.setVisible(true);
  }

public void setStatus(String text)
  {
    roadMapStatus.setText(text);
  }

public void setStatusColor(Color color)
  {
    roadMapStatus.setBackground(color);
  }

public void saveDictionaryAs()
  {
    saveDictionaryAsAWT();
  }

 
  public boolean saveDictionaryAsAWT()
    {
    saveAWT.setDirectory(ImproVisor.getDictionaryDirectory().getAbsolutePath());
    saveAWT.setVisible(true);
    
    String selected = saveAWT.getFile();
    
    String newFileName = selected;

    String dir = saveAWT.getDirectory();
    
    if( selected != null )
      {

      boolean noErrors = true;

      if( !newFileName.endsWith(DICTIONARY_EXT) )
        {
        newFileName += DICTIONARY_EXT;
        }

      File newFile = new File(dir + newFileName);
      
      try
        {
          FileUtilities.copy(new File(dictionaryFilename), newFile);
        }
      catch( IOException e)
        {
          ErrorLog.log(ErrorLog.SEVERE, "Error writing new dictionary: " + newFile);
        }
      
      setDictionaryFilename(selected);
      }

    return false;
    }
  
  

  
 private void setTempo(double value)
    {
    if( value >= Notate.MIN_TEMPO && value <= Notate.MAX_TEMPO )
      {
      tempoSet.setText("" + (int)value);

      tempoSlider.setValue((int)value);
      notate.getMidiSynthRM().setTempo((float)value);
      }
    else
      {
      ErrorLog.log(ErrorLog.COMMENT,
              "The tempo must be in the range "
            + notate.MIN_TEMPO 
            + " to " + notate.MAX_TEMPO 
            + ",\nusing default: " + notate.getDefaultTempo() + ".");
      }
    }
  
  private void updateTempoFromTextField()
  {
    try
      {
        int value = (int)Double.valueOf(tempoSet.getText()).doubleValue();

        jSliderIgnoreStateChangedEvt = true;

        setTempo(value);

        jSliderIgnoreStateChangedEvt = false;
      }
    catch( NumberFormatException e )
      {
      tempoSet.setForeground(Color.RED);

      return;
      }

    tempoSet.setForeground(Color.BLACK);
  }
    
  private static int intFromTextField(JTextField field)
    {
      String text = field.getText();
      
      int value = 0;
      try
        {
         value = Integer.parseInt(text); 
        }
      catch( Exception e )
        {
        }
      
      return value;
    }
  
private void reloadDictionary()
  {
    setDictionaryFilename(dictionaryName);
    newDictionary();
    populateRoadmapDictionaryMenu(dictionaryName);

    brickDictionaryFrame.setVisible(true);
  }

private void setShowJoins(boolean value)
  {
    //System.out.println("showJoins = " + value);
    settings.showJoins = value;
  }
private void setShowStartingNote(){
    
}

public void setStyle(Style style)
  {
    this.style = style;
    styleComboBox.setSelectedItem(style);
    //System.out.println("setting Style to " + style.toString());
  }

public RoadMap getRoadMap()
  {
    
    return roadMapPanel.getRoadMap();
  }

@Override
public String toString()
    {
        return roadMapPanel.toString();
    }
public void updatePhiAndDelta(boolean phi, boolean delta){
    setPhiStatus(phi);
    setDeltaStatus(delta);
    settings.setPhi(phi);
    settings.setDelta(delta);
    
}
public void setPhiStatus(boolean phi){
    replaceWithPhiCheckBoxMI.setState(phi);
}
public void setDeltaStatus(boolean delta){
    replaceWithDeltaCheckBoxMI.setState(delta);
}
public boolean getPhiStatus(){
    return replaceWithPhiCheckBoxMI.getState();
}
public boolean getDeltaStatus(){
    return replaceWithDeltaCheckBoxMI.getState();
}

public void setRoadMap(RoadMap roadmap)
  {
    roadMapPanel.setRoadMap(roadmap);
  }

public void rawSetRoadMap(RoadMap roadmap)
  {
    roadMapPanel.rawSetRoadMap(roadmap);
}

public void analyze()
  {
    analyzeInBackground(settings.showJoins);
  }

public void reset()
  {
    roadMapPanel.reset();
  }

public Note getFirstNote()
{
    return notate.getFirstNote();
}

public String getComposer()
{
    return notate.getComposer();
}

public void setStyleNames(ArrayList<String> styles)
{
        styleNames = styles;
}


public ArrayList<String> getStyleNames()
{
    return styleNames;
}

 private void setPlayTransposed() {
    setTransposition(getTransposition());
}
    
public void setTransposition(int transposition)
  {
    if( transposeSpinner != null )
      {
        transposeSpinner.setValue(transposition);
      }

    roadMapTransposition = transposition;
  } 

public int getTransposition()
  {
    return Integer.parseInt(transposeSpinner.getValue().toString());
  }

}
