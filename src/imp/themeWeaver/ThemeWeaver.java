/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2015 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
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
package imp.themeWeaver;

import imp.style.stylePatterns.PatternDisplay;
import static imp.Constants.BEAT;
import static imp.Constants.OCTAVE;
import imp.com.CommandManager;
import imp.com.InvertCommand;
import imp.com.LoadThemesCommand;
import imp.com.PlayScoreCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ReverseCommand;
import imp.com.SaveThemesCommand;
import imp.com.ShiftPitchesCommand;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.Part;
import imp.data.PitchClass;
import imp.data.Unit;
import imp.lickgen.NoteConverter;
import imp.util.ThemesFilter;
import polya.Polylist;
import imp.lickgen.LickGen;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.LinkedHashMap;
import imp.util.ErrorLog;
import java.util.Map;
import imp.ImproVisor;
import imp.com.TimeWarpCommand;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import polya.Tokenizer;
import imp.data.Score;
import imp.gui.LickgenFrame;
import imp.gui.Notate;
import imp.gui.RangeChooser;
import imp.gui.WindowMenuItem;
import imp.gui.WindowRegistry;
import javax.swing.table.DefaultTableCellRenderer;
import polya.PolylistEnum;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Component;
import java.util.Stack;


/**
 *
 * @author David Morrison, Nava Dallal, Amelia Sheppard
 */
@SuppressWarnings("serial")
public class ThemeWeaver extends javax.swing.JFrame
{

String TITLE = "Theme Weaver";
LickgenFrame lickgenFrame;
private static String themesExt = ".themes";
public String themesFile = "My" + themesExt;
JFileChooser themesfc;
private File savedThemes;

private boolean endSoloEarly = false;
boolean soloPlaying = false;
protected ThemeWeaver themeWeaver;
private int themeLength = 8;
private Notate notate;
private int minPitch = 60;
private int maxPitch = 82;
private int [] range;
private int minInterval = 0;
private int maxInterval = 6;
private double probWholeToneTranspose = .7;
private double probSemitoneTranspose = .3;
private double probSlideUp = 0.5;
private double probHalfSideSlip = 0.4;
private double probWholeSideSlip = 0.4;
private double probThirdSideSlip = 0.2;
private double probForwardShift = 0.5;
private boolean barLineShiftForward = false;
private int shiftForwardBy = 60;
private final int shiftForwardByFinal = 60;
private double leapProb = 0.2;
private boolean avoidRepeats = true;
private LickGen lickgen;
private CommandManager cm;
static final int NAME_COLUMN = 0;
static final int LENGTH_COLUMN = 1;
static final int THEME_COLUMN = 2;
static final int USE_COLUMN = 3;
static final int TRANSPOSE_COLUMN = 4;
static final int INVERT_COLUMN = 5;
static final int REVERSE_COLUMN = 6;
static final int EXPAND_COLUMN = 7;
static final int SIDESLIP_COLUMN = 8;
static final int BARLINESHIFT_COLUMN = 9;
static final String USE_DEFAULT_VALUE = "1.0";
static final String TRANSPOSE_DEFAULT_VALUE = "0.0";
static final String INVERT_DEFAULT_VALUE = "0.0";
static final String REVERSE_DEFAULT_VALUE = "0.0";
static final String EXPAND_DEFAULT_VALUE = "0.0";
static final String SIDESLIP_DEFAULT_VALUE = "0.0";
static final String BARLINESHIFT_DEFAULT_VALUE = "0.0";
private ArrayList<Object> itemsLeft = new ArrayList<Object>();
private ArrayList<Object> itemsLeft1 = new ArrayList<Object>();
private boolean canEnter2 = true;//controls whether trans2ComboBoxActionPerformed can execute or not
private boolean canEnter3 = true;//controls whether trans3ComboBoxActionPerformed can execute or not
private boolean canEnter4 = true;//controls whether trans4ComboBoxActionPerformed can execute or not
private boolean canEnter5 = true;//controls whether trans5ComboBoxActionPerformed can execute or not
private boolean throughOnce = false;//set to true once all of the transition combo boxes have been gone through
private int transformNum = 0;//keeps track of the tranformation number that is being set
private String themeUsageText = "";
static final int ROW_COUNT = 20;
double probTheme;//probability of using a theme 
public ThemeListModel themeListModel = new ThemeListModel();
private final javax.swing.JComboBox [] transformationComboBoxes = new javax.swing.JComboBox[6];//array of the tranformation combo boxes
private boolean barlineshift = false;
private boolean sideslip = false;
private boolean transposeButtonPressed = false;
private boolean invertButtonPressed = false;
private boolean reverseButtonPressed = false;
private boolean expandButtonPressed = false;
private boolean sideslipButtonPressed = false;
private boolean barlineshiftButtonPressed = false;
private ArrayList<String> transformationOrder = new ArrayList<String>();
private String transformationUsedText = "";
private String directionOfTransposition = "";
private String distanceOfTransposition = "";
private int expandBy = -1;
private String directionOfSideslip = "";
private String distanceOfSideslip = "";
private boolean barlineshift2 = false;
private boolean sideslip2 = false;
private String directionOfShift = "";
private MelodyPart chosenCustomThemeOriginal = new MelodyPart();//this will keep track of the u melody
private MelodyPart chosenCustomTheme = new MelodyPart();
private int currentSlotCS = 0; //the current slot, used for custom solo
private MelodyPart customSolo = new MelodyPart(0);
private String chosenThemeName = "";
private boolean specifiedBar = false;
private Stack<MelodyPart> edits = new Stack<MelodyPart>();
private Stack<MelodyPart> undos = new Stack<MelodyPart>();

Random random;
File fileName = ImproVisor.getThemesFile();

/**
 * Creates new form ThemeWeaver
 */
public ThemeWeaver(LickGen lickgen, Notate notate, CommandManager cm)
  {
    this.random = new Random();
    initComponents();
    //    testDialog.setVisible(true);
    this.cm = cm;
    this.lickgen = lickgen;
    this.notate = notate;

    setTitle(TITLE);
    WindowRegistry.registerWindow(this);
    soloTable.setModel(soloTableModel);
    soloTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    soloTable.addMouseListener(new MouseAdapter()
    {
    @Override
    public void mouseReleased(MouseEvent e)
      {
        if( e.getClickCount() >= 1 )
          {
            int row = soloTable.rowAtPoint(e.getPoint());
            soloTable.getSelectionModel().setSelectionInterval(row, row);
          }

      }

    });
    setTableColumnWidths();
    themesfc = new JFileChooser();
    loadFromFile(fileName);
    //added code to fill up theme weaver with one of each them by default
    ensureThemeArray();
    fillWithOneOfEachTheme();

  }

public void setTableColumnWidths()
  {
    for( int j = 0; j < soloTableModel.getColumnCount(); j++ )
      {
        soloTable.getColumnModel().getColumn(j).
                setPreferredWidth(soloTableModel.getColumnWidths(j));
      }


    for( int j = 0; j < soloTableModel.getColumnCount(); j++ )
      {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(soloTableModel.getColumnAdjustments(j));
        soloTable.getColumnModel().getColumn(j).setCellRenderer(renderer);
      }
  }
    



/**
 * This method is called from within the constructor to initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is always
 * regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        nameErrorMessage = new javax.swing.JDialog();
        nameField = new javax.swing.JTextField();
        namePicked = new javax.swing.JLabel();
        CancelButton = new javax.swing.JButton();
        OkButton = new javax.swing.JButton();
        chooseName = new javax.swing.JLabel();
        enteredIncorrectly = new javax.swing.JDialog();
        typedWrong = new javax.swing.JLabel();
        tryAgain = new javax.swing.JLabel();
        cellOkbutton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        resetCheck = new javax.swing.JDialog();
        Resettable = new javax.swing.JLabel();
        youSure = new javax.swing.JLabel();
        YesButton = new javax.swing.JButton();
        NoButton = new javax.swing.JButton();
        rangeTooSmall = new javax.swing.JDialog();
        tryAgain1 = new javax.swing.JLabel();
        cellOkbutton1 = new javax.swing.JButton();
        rangeWrong = new javax.swing.JLabel();
        resetCheck1 = new javax.swing.JDialog();
        youSure1 = new javax.swing.JLabel();
        YesButton1 = new javax.swing.JButton();
        NoButton1 = new javax.swing.JButton();
        deleteThemeDialog = new javax.swing.JDialog();
        areYouSure = new javax.swing.JLabel();
        yesdeletethemeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        rangeWrong1 = new javax.swing.JLabel();
        noRowSelected = new javax.swing.JDialog();
        tryAgain2 = new javax.swing.JLabel();
        cellOkbutton2 = new javax.swing.JButton();
        rangeWrong2 = new javax.swing.JLabel();
        enterAnInteger = new javax.swing.JDialog();
        typedWrong1 = new javax.swing.JLabel();
        tryAgain3 = new javax.swing.JLabel();
        cellOkbutton3 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        sideslipPreference = new javax.swing.JFrame();
        probUpOrDown = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        SoloGeneratorTitle1 = new java.awt.Label();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        probHalf = new javax.swing.JTextField();
        probWhole = new javax.swing.JTextField();
        probThird = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        expandPreference = new javax.swing.JFrame();
        probExpandby2or3 = new javax.swing.JSlider();
        ExpandPreferenceTitle = new java.awt.Label();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        barLineShiftPreference = new javax.swing.JFrame();
        SoloGeneratorTitle2 = new java.awt.Label();
        jLabel23 = new javax.swing.JLabel();
        probShiftForwardorBackSlider = new javax.swing.JSlider();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        customizeSolo = new javax.swing.JFrame();
        SoloGeneratorTitle3 = new java.awt.Label();
        themeListScrollPane1 = new javax.swing.JScrollPane();
        themeList1 = new javax.swing.JList();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        transposeButton = new javax.swing.JToggleButton();
        invertButton = new javax.swing.JToggleButton();
        reverseButton = new javax.swing.JToggleButton();
        expandButton = new javax.swing.JToggleButton();
        sideslipButton = new javax.swing.JToggleButton();
        barlineshiftButton = new javax.swing.JToggleButton();
        resetTransformationChoicesButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        transposeUpRadioButton = new javax.swing.JRadioButton();
        transposeDownRadioButton = new javax.swing.JRadioButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        halfStepRadioButton = new javax.swing.JRadioButton();
        wholeStepRadioButton = new javax.swing.JRadioButton();
        thirdRadioButton = new javax.swing.JRadioButton();
        jPanel8 = new javax.swing.JPanel();
        expandBy2RadioButton = new javax.swing.JRadioButton();
        expandBy3RadioButton = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        halfStepRadioButton1 = new javax.swing.JRadioButton();
        wholeStepRadioButton1 = new javax.swing.JRadioButton();
        thirdRadioButton1 = new javax.swing.JRadioButton();
        sideslipUpRadioButton = new javax.swing.JRadioButton();
        sideslipDownRadioButton = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        shiftBackwardRadioButton = new javax.swing.JRadioButton();
        shiftForwardRadioButton = new javax.swing.JRadioButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        eighthShiftRadioButton = new javax.swing.JRadioButton();
        quarterShiftRadioButton = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        transformationsUsed = new javax.swing.JScrollPane();
        transformationsUsedTextArea = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        playThemeButton = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        resetSoloButton = new javax.swing.JButton();
        rectifySoloButton = new javax.swing.JButton();
        undoEditButton = new javax.swing.JButton();
        fillSoloButton = new javax.swing.JButton();
        keepEditsFromLeadsheetCheckBox = new javax.swing.JCheckBox();
        redoEditButton = new javax.swing.JButton();
        pasteToLeadsheetButton = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        barNumberTextField = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        themeNameTextPane = new javax.swing.JTextPane();
        themeNameTextPane.setEditable(false);
        jLabel44 = new javax.swing.JLabel();
        soloTableScrollPane = new javax.swing.JScrollPane();
        soloTable = new javax.swing.JTable();
        themeListScrollPane = new javax.swing.JScrollPane();
        themeList = new javax.swing.JList();
        themeUsageScrollPane = new javax.swing.JScrollPane();
        themeUsageTextArea = new javax.swing.JTextArea();
        SoloGeneratorTitle = new java.awt.Label();
        themeIntervalTextField = new javax.swing.JTextField();
        themeIntervalLabel = new javax.swing.JLabel();
        themeProbTextField = new javax.swing.JTextField();
        noThemeProbLabel = new javax.swing.JLabel();
        generateSoloJButton = new javax.swing.JButton();
        stopPlaytoggle = new javax.swing.JToggleButton();
        deleteThemebutton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        TransformationOrder = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        currentSelectionJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Reset = new javax.swing.JButton();
        expandPreferenceButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        barLineShiftPreferencesButton = new javax.swing.JButton();
        deleteRowbutton = new javax.swing.JButton();
        generateThemeJButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        setAllProbTextField = new javax.swing.JTextField();
        setAllProbButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        setProbToZeroButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rangeChooserButton = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        trans2ComboBox = new javax.swing.JComboBox();
        trans1ComboBox = new javax.swing.JComboBox();
        trans3ComboBox = new javax.swing.JComboBox();
        trans4ComboBox = new javax.swing.JComboBox();
        trans5ComboBox = new javax.swing.JComboBox();
        trans6ComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        roadmapMenuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        loadThemesMI = new javax.swing.JMenuItem();
        saveAsAdvice = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        windowMenuSeparator = new javax.swing.JSeparator();

        nameErrorMessage.setAlwaysOnTop(true);
        nameErrorMessage.setMinimumSize(new java.awt.Dimension(600, 400));
        nameErrorMessage.getContentPane().setLayout(new java.awt.GridBagLayout());

        nameField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                nameFieldActionPerformed(evt);
            }
        });
        nameField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                nameFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 309;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        nameErrorMessage.getContentPane().add(nameField, gridBagConstraints);

        namePicked.setText("The name you have picked is already assigned to a theme. ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipadx = 75;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(53, 20, 0, 0);
        nameErrorMessage.getContentPane().add(namePicked, gridBagConstraints);

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                CancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 43, 105, 0);
        nameErrorMessage.getContentPane().add(CancelButton, gridBagConstraints);

        OkButton.setText("Ok");
        OkButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                OkButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = -18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 20, 105, 0);
        nameErrorMessage.getContentPane().add(OkButton, gridBagConstraints);

        chooseName.setText("Please choose another:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        nameErrorMessage.getContentPane().add(chooseName, gridBagConstraints);

        enteredIncorrectly.setAlwaysOnTop(true);
        enteredIncorrectly.setMinimumSize(new java.awt.Dimension(400, 300));
        enteredIncorrectly.getContentPane().setLayout(new java.awt.GridBagLayout());

        typedWrong.setText("You have either typed information into a cell incorrectly,");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(67, 0, 0, 0);
        enteredIncorrectly.getContentPane().add(typedWrong, gridBagConstraints);

        tryAgain.setText("Please try again.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        enteredIncorrectly.getContentPane().add(tryAgain, gridBagConstraints);

        cellOkbutton.setText("Ok");
        cellOkbutton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cellOkbuttonActionPerformed(evt);
            }
        });
        cellOkbutton.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                cellOkbuttonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 50, 0);
        enteredIncorrectly.getContentPane().add(cellOkbutton, gridBagConstraints);

        jLabel1.setText("not finished entering contents into a cell, or left a cell blank.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
        enteredIncorrectly.getContentPane().add(jLabel1, gridBagConstraints);

        jLabel38.setText("Theme Weaver");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        enteredIncorrectly.getContentPane().add(jLabel38, gridBagConstraints);

        resetCheck.setAlwaysOnTop(true);
        resetCheck.setMinimumSize(new java.awt.Dimension(600, 300));
        resetCheck.setModal(true);
        resetCheck.setSize(new java.awt.Dimension(600, 300));
        resetCheck.getContentPane().setLayout(new java.awt.GridBagLayout());

        Resettable.setText("Resetting the table will clear everything you currently have entered. ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(96, 39, 0, 77);
        resetCheck.getContentPane().add(Resettable, gridBagConstraints);

        youSure.setText("Are you sure you want to do this?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 137, 0, 0);
        resetCheck.getContentPane().add(youSure, gridBagConstraints);

        YesButton.setText("Yes, Continue");
        YesButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                YesButtonActionPerformed(evt);
            }
        });
        YesButton.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                YesButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 78, 150, 0);
        resetCheck.getContentPane().add(YesButton, gridBagConstraints);

        NoButton.setText("No, Cancel");
        NoButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                NoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 76, 150, 0);
        resetCheck.getContentPane().add(NoButton, gridBagConstraints);

        rangeTooSmall.setAlwaysOnTop(true);
        rangeTooSmall.setMinimumSize(new java.awt.Dimension(900, 500));
        rangeTooSmall.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowOpened(java.awt.event.WindowEvent evt)
            {
                rangeTooSmallWindowOpened(evt);
            }
        });
        rangeTooSmall.getContentPane().setLayout(new java.awt.GridBagLayout());

        tryAgain1.setText("Please reset and try again.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        rangeTooSmall.getContentPane().add(tryAgain1, gridBagConstraints);

        cellOkbutton1.setText("Ok");
        cellOkbutton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cellOkbutton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(50, 50, 50, 50);
        rangeTooSmall.getContentPane().add(cellOkbutton1, gridBagConstraints);

        rangeWrong.setText("The set range is too small for the theme(s) you chose. ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        rangeTooSmall.getContentPane().add(rangeWrong, gridBagConstraints);

        resetCheck1.setAlwaysOnTop(true);
        resetCheck1.setMinimumSize(new java.awt.Dimension(500, 300));
        resetCheck1.getContentPane().setLayout(new java.awt.GridBagLayout());

        youSure1.setText("Are you sure you want to start over?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 21, 7);
        resetCheck1.getContentPane().add(youSure1, gridBagConstraints);

        YesButton1.setText("Yes, Continue");
        YesButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                YesButton1ActionPerformed(evt);
            }
        });
        YesButton1.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                YesButton1KeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        resetCheck1.getContentPane().add(YesButton1, gridBagConstraints);

        NoButton1.setText("No, Cancel");
        NoButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                NoButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        resetCheck1.getContentPane().add(NoButton1, gridBagConstraints);

        deleteThemeDialog.setAlwaysOnTop(true);
        deleteThemeDialog.setMinimumSize(new java.awt.Dimension(900, 500));
        deleteThemeDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        areYouSure.setText("Are you sure you want to continue?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        deleteThemeDialog.getContentPane().add(areYouSure, gridBagConstraints);

        yesdeletethemeButton.setText("Yes");
        yesdeletethemeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                yesdeletethemeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(50, 30, 50, 30);
        deleteThemeDialog.getContentPane().add(yesdeletethemeButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        deleteThemeDialog.getContentPane().add(cancelButton, gridBagConstraints);

        rangeWrong1.setText("You are about to delete a theme.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 24, 0, 21);
        deleteThemeDialog.getContentPane().add(rangeWrong1, gridBagConstraints);

        noRowSelected.setAlwaysOnTop(true);
        noRowSelected.setMinimumSize(new java.awt.Dimension(900, 500));
        noRowSelected.getContentPane().setLayout(new java.awt.GridBagLayout());

        tryAgain2.setText("Please select a row and try again.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        noRowSelected.getContentPane().add(tryAgain2, gridBagConstraints);

        cellOkbutton2.setText("Ok");
        cellOkbutton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cellOkbutton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(50, 50, 50, 50);
        noRowSelected.getContentPane().add(cellOkbutton2, gridBagConstraints);

        rangeWrong2.setText("No row has been selected.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        noRowSelected.getContentPane().add(rangeWrong2, gridBagConstraints);

        enterAnInteger.setAlwaysOnTop(true);
        enterAnInteger.setMinimumSize(new java.awt.Dimension(400, 300));
        enterAnInteger.getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(67, 0, 0, 0);
        enterAnInteger.getContentPane().add(typedWrong1, gridBagConstraints);

        tryAgain3.setText("Please enter an Integer for the bar number.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        enterAnInteger.getContentPane().add(tryAgain3, gridBagConstraints);

        cellOkbutton3.setText("Ok");
        cellOkbutton3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cellOkbutton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 50, 0);
        enterAnInteger.getContentPane().add(cellOkbutton3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
        enterAnInteger.getContentPane().add(jLabel42, gridBagConstraints);

        jLabel43.setText("Theme Weaver");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        enterAnInteger.getContentPane().add(jLabel43, gridBagConstraints);

        sideslipPreference.setAlwaysOnTop(true);
        sideslipPreference.setLocation(new java.awt.Point(10, 10));
        sideslipPreference.setLocationByPlatform(true);
        sideslipPreference.setMinimumSize(new java.awt.Dimension(200, 300));
        sideslipPreference.setSize(new java.awt.Dimension(750, 600));
        sideslipPreference.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                sideslipPreferencewindowClosed(evt);
            }
        });
        sideslipPreference.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                sideslipPreferenceformKeyPressed(evt);
            }
        });
        sideslipPreference.getContentPane().setLayout(new java.awt.GridBagLayout());

        probUpOrDown.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        probUpOrDown.setMajorTickSpacing(10);
        probUpOrDown.setMinorTickSpacing(1);
        probUpOrDown.setPaintLabels(true);
        probUpOrDown.setPaintTicks(true);
        probUpOrDown.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseDragged(java.awt.event.MouseEvent evt)
            {
                probUpOrDownMouseDragged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        sideslipPreference.getContentPane().add(probUpOrDown, gridBagConstraints);

        jLabel10.setText("Slide Up");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        sideslipPreference.getContentPane().add(jLabel10, gridBagConstraints);

        SoloGeneratorTitle1.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        SoloGeneratorTitle1.setMaximumSize(new java.awt.Dimension(327, 327));
        SoloGeneratorTitle1.setText("Side Slip Preferences");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 40, 0);
        sideslipPreference.getContentPane().add(SoloGeneratorTitle1, gridBagConstraints);

        jLabel2.setText("Prob Half Step Slide");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 25, 0);
        sideslipPreference.getContentPane().add(jLabel2, gridBagConstraints);

        jLabel12.setText("Prob Whole Step Slide");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 25, 10);
        sideslipPreference.getContentPane().add(jLabel12, gridBagConstraints);

        jLabel13.setText("Prob Third Slide");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 25, 0);
        sideslipPreference.getContentPane().add(jLabel13, gridBagConstraints);

        probHalf.setText("0.4");
        probHalf.setToolTipText("");
        probHalf.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                probHalfActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sideslipPreference.getContentPane().add(probHalf, gridBagConstraints);

        probWhole.setText("0.4");
        probWhole.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                probWholeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sideslipPreference.getContentPane().add(probWhole, gridBagConstraints);

        probThird.setText("0.2");
        probThird.setToolTipText("");
        probThird.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                probThirdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sideslipPreference.getContentPane().add(probThird, gridBagConstraints);

        jLabel17.setText("*Here you can control the probability that the melody will be side slipped up or down.*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        sideslipPreference.getContentPane().add(jLabel17, gridBagConstraints);

        jLabel18.setText("*Enter the probability that it will slide up (%). The probability that it will slide down is the inverse of this.*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        sideslipPreference.getContentPane().add(jLabel18, gridBagConstraints);

        expandPreference.setAlwaysOnTop(true);
        expandPreference.setLocation(new java.awt.Point(10, 10));
        expandPreference.setLocationByPlatform(true);
        expandPreference.setMinimumSize(new java.awt.Dimension(200, 300));
        expandPreference.setSize(new java.awt.Dimension(700, 600));
        expandPreference.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                expandPreferencewindowClosed(evt);
            }
        });
        expandPreference.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                expandPreferenceformKeyPressed(evt);
            }
        });
        expandPreference.getContentPane().setLayout(new java.awt.GridBagLayout());

        probExpandby2or3.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        probExpandby2or3.setMajorTickSpacing(50);
        probExpandby2or3.setMinorTickSpacing(5);
        probExpandby2or3.setPaintLabels(true);
        probExpandby2or3.setPaintTicks(true);
        probExpandby2or3.setMinimumSize(new java.awt.Dimension(280, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 10;
        expandPreference.getContentPane().add(probExpandby2or3, gridBagConstraints);

        ExpandPreferenceTitle.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        ExpandPreferenceTitle.setMaximumSize(new java.awt.Dimension(327, 327));
        ExpandPreferenceTitle.setText("Expand Preferences");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 40, 0);
        expandPreference.getContentPane().add(ExpandPreferenceTitle, gridBagConstraints);

        jLabel25.setText("*Probability of Expanding by 2 (%)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        expandPreference.getContentPane().add(jLabel25, gridBagConstraints);

        jLabel26.setText("This transformation expands the melody to either 2 or 3 times its original duration.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        expandPreference.getContentPane().add(jLabel26, gridBagConstraints);

        jLabel27.setText("The probability of expanding by 3 will be the inverse of the probability of expanding by 2.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 20);
        expandPreference.getContentPane().add(jLabel27, gridBagConstraints);

        barLineShiftPreference.setAlwaysOnTop(true);
        barLineShiftPreference.setLocation(new java.awt.Point(10, 10));
        barLineShiftPreference.setLocationByPlatform(true);
        barLineShiftPreference.setMinimumSize(new java.awt.Dimension(200, 300));
        barLineShiftPreference.setSize(new java.awt.Dimension(700, 600));
        barLineShiftPreference.getContentPane().setLayout(new java.awt.GridBagLayout());

        SoloGeneratorTitle2.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        SoloGeneratorTitle2.setMaximumSize(new java.awt.Dimension(327, 327));
        SoloGeneratorTitle2.setText("Bar Line Shift Preferences");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 40, 0);
        barLineShiftPreference.getContentPane().add(SoloGeneratorTitle2, gridBagConstraints);

        jLabel23.setText("Shift Forward");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        barLineShiftPreference.getContentPane().add(jLabel23, gridBagConstraints);

        probShiftForwardorBackSlider.setMajorTickSpacing(10);
        probShiftForwardorBackSlider.setMinorTickSpacing(1);
        probShiftForwardorBackSlider.setPaintLabels(true);
        probShiftForwardorBackSlider.setPaintTicks(true);
        probShiftForwardorBackSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseDragged(java.awt.event.MouseEvent evt)
            {
                probShiftForwardorBackSliderMouseDragged(evt);
            }
        });
        probShiftForwardorBackSlider.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                probShiftForwardorBackSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        barLineShiftPreference.getContentPane().add(probShiftForwardorBackSlider, gridBagConstraints);

        jLabel15.setText("*This represents the probability (% chance) that the bar line shift will be forwards.*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        barLineShiftPreference.getContentPane().add(jLabel15, gridBagConstraints);

        jLabel16.setText("*The probability that it will shift backwards is the inverse of this probability.*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        barLineShiftPreference.getContentPane().add(jLabel16, gridBagConstraints);

        customizeSolo.setLocation(new java.awt.Point(10, 10));
        customizeSolo.setLocationByPlatform(true);
        customizeSolo.setSize(new java.awt.Dimension(1100, 950));
        customizeSolo.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                customizeSolowindowClosed(evt);
            }
        });
        customizeSolo.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                customizeSoloformKeyPressed(evt);
            }
        });
        customizeSolo.getContentPane().setLayout(new java.awt.GridBagLayout());

        SoloGeneratorTitle3.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        SoloGeneratorTitle3.setMaximumSize(new java.awt.Dimension(327, 327));
        SoloGeneratorTitle3.setText("Customize Solo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 40, 0);
        customizeSolo.getContentPane().add(SoloGeneratorTitle3, gridBagConstraints);
        SoloGeneratorTitle3.getAccessibleContext().setAccessibleDescription("");

        themeListScrollPane1.setMaximumSize(new java.awt.Dimension(0, 0));
        themeListScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        themeListScrollPane1.setPreferredSize(new java.awt.Dimension(100, 100));

        themeList1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        themeList1.setModel(themeListModel);
        themeList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        themeList1.setToolTipText("shift-click a theme to listen to it");
        themeList1.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        themeList1.setPreferredSize(new java.awt.Dimension(200, 200));
        themeList1.setVisibleRowCount(30);
        themeList1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                themeList1Clicked(evt);
            }
        });
        themeListScrollPane1.setViewportView(themeList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 320;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 0);
        customizeSolo.getContentPane().add(themeListScrollPane1, gridBagConstraints);

        jLabel21.setText("Step 1: Choose a Theme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        customizeSolo.getContentPane().add(jLabel21, gridBagConstraints);

        jLabel22.setText("Step 2: Choose Some Transformations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        customizeSolo.getContentPane().add(jLabel22, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        transposeButton.setText("Transpose");
        transposeButton.setToolTipText("");
        transposeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                transposeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(transposeButton, gridBagConstraints);

        invertButton.setText("Invert");
        invertButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                invertButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(invertButton, gridBagConstraints);

        reverseButton.setText("Reverse");
        reverseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                reverseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(reverseButton, gridBagConstraints);

        expandButton.setText("Expand");
        expandButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                expandButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(expandButton, gridBagConstraints);

        sideslipButton.setText("Side Slip");
        sideslipButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sideslipButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(sideslipButton, gridBagConstraints);

        barlineshiftButton.setText("Bar Line Shift");
        barlineshiftButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                barlineshiftButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 16, 0);
        jPanel4.add(barlineshiftButton, gridBagConstraints);

        resetTransformationChoicesButton.setText("Reset Choices");
        resetTransformationChoicesButton.setToolTipText("");
        resetTransformationChoicesButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                resetTransformationChoicesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.insets = new java.awt.Insets(40, 0, 0, 0);
        jPanel4.add(resetTransformationChoicesButton, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        transposeUpRadioButton.setText("Transpose Up");
        transposeUpRadioButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        transposeUpRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                transposeUpRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel7.add(transposeUpRadioButton, gridBagConstraints);

        transposeDownRadioButton.setText("Transpose Down");
        transposeDownRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                transposeDownRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel7.add(transposeDownRadioButton, gridBagConstraints);

        jLabel32.setText("Direction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel7.add(jLabel32, gridBagConstraints);

        jLabel33.setText("Distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel7.add(jLabel33, gridBagConstraints);

        halfStepRadioButton.setText("Half");
        halfStepRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                halfStepRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel7.add(halfStepRadioButton, gridBagConstraints);

        wholeStepRadioButton.setText("Whole");
        wholeStepRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                wholeStepRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel7.add(wholeStepRadioButton, gridBagConstraints);

        thirdRadioButton.setText("Third");
        thirdRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                thirdRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel7.add(thirdRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        jPanel4.add(jPanel7, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        expandBy2RadioButton.setText("Expand By 2");
        expandBy2RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                expandBy2RadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanel8.add(expandBy2RadioButton, gridBagConstraints);

        expandBy3RadioButton.setText("Expand By 3");
        expandBy3RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                expandBy3RadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanel8.add(expandBy3RadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel4.add(jPanel8, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());

        jLabel34.setText("Direction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel10.add(jLabel34, gridBagConstraints);

        jLabel35.setText("Distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel10.add(jLabel35, gridBagConstraints);

        halfStepRadioButton1.setText("Half");
        halfStepRadioButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                halfStepRadioButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel10.add(halfStepRadioButton1, gridBagConstraints);

        wholeStepRadioButton1.setText("Whole");
        wholeStepRadioButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                wholeStepRadioButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel10.add(wholeStepRadioButton1, gridBagConstraints);

        thirdRadioButton1.setText("Third");
        thirdRadioButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                thirdRadioButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel10.add(thirdRadioButton1, gridBagConstraints);

        sideslipUpRadioButton.setText("Side Slip Up");
        sideslipUpRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sideslipUpRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel10.add(sideslipUpRadioButton, gridBagConstraints);

        sideslipDownRadioButton.setText("Side Slip Down");
        sideslipDownRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sideslipDownRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel10.add(sideslipDownRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        jPanel4.add(jPanel10, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        shiftBackwardRadioButton.setText("Shift Backwards");
        shiftBackwardRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                shiftBackwardRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel9.add(shiftBackwardRadioButton, gridBagConstraints);

        shiftForwardRadioButton.setText("Shift Forwards");
        shiftForwardRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                shiftForwardRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel9.add(shiftForwardRadioButton, gridBagConstraints);

        jLabel36.setText("Distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel9.add(jLabel36, gridBagConstraints);

        jLabel37.setText("Direction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel9.add(jLabel37, gridBagConstraints);

        eighthShiftRadioButton.setText("Eighth");
        eighthShiftRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                eighthShiftRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel9.add(eighthShiftRadioButton, gridBagConstraints);

        quarterShiftRadioButton.setText("Quarter");
        quarterShiftRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                quarterShiftRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel9.add(quarterShiftRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanel4.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        customizeSolo.getContentPane().add(jPanel4, gridBagConstraints);

        jLabel24.setText("*Please select in the order you want the transformations done in*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        customizeSolo.getContentPane().add(jLabel24, gridBagConstraints);

        transformationsUsed.setMinimumSize(new java.awt.Dimension(300, 100));

        transformationsUsedTextArea.setColumns(20);
        transformationsUsedTextArea.setRows(70);
        transformationsUsedTextArea.setPreferredSize(new java.awt.Dimension(240, 200));
        transformationsUsedTextArea.setRequestFocusEnabled(false);
        transformationsUsedTextArea.setSize(new java.awt.Dimension(200, 200));
        transformationsUsed.setViewportView(transformationsUsedTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        customizeSolo.getContentPane().add(transformationsUsed, gridBagConstraints);

        jLabel29.setText("Chosen Transformations (In Order):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        customizeSolo.getContentPane().add(jLabel29, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel30.setText("Step 3: Listen To Adjusted Theme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel6.add(jLabel30, gridBagConstraints);

        playThemeButton.setText("Click To Play Theme");
        playThemeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                playThemeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.ipady = 32;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 40, 0);
        jPanel6.add(playThemeButton, gridBagConstraints);

        jLabel31.setText("Step 4: Add To Solo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(40, 0, 0, 0);
        jPanel6.add(jLabel31, gridBagConstraints);

        jPanel12.setLayout(new java.awt.GridBagLayout());

        resetSoloButton.setText("Reset Solo");
        resetSoloButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                resetSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 22;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(resetSoloButton, gridBagConstraints);

        rectifySoloButton.setText("Rectify Pitches");
        rectifySoloButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rectifySoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 22;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(rectifySoloButton, gridBagConstraints);
        rectifySoloButton.getAccessibleContext().setAccessibleDescription("Changes pitches so they fit with the corresponding chord on the leadsheet.");

        undoEditButton.setText("Undo Edit");
        undoEditButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                undoEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 22;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(undoEditButton, gridBagConstraints);
        undoEditButton.getAccessibleContext().setAccessibleDescription("Undo the last added theme. You can only revert by one addition.");

        fillSoloButton.setText("Fill Solo");
        fillSoloButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fillSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 22;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(fillSoloButton, gridBagConstraints);

        keepEditsFromLeadsheetCheckBox.setText("Keep Edits Made In The Leadsheet");
        keepEditsFromLeadsheetCheckBox.setSelected(true);
        keepEditsFromLeadsheetCheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                keepEditsFromLeadsheetCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(keepEditsFromLeadsheetCheckBox, gridBagConstraints);

        redoEditButton.setText("Redo Edit");
        redoEditButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                redoEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 22;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel12.add(redoEditButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
        jPanel6.add(jPanel12, gridBagConstraints);

        pasteToLeadsheetButton.setText("Paste Theme To Leadsheet");
        pasteToLeadsheetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pasteToLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanel6.add(pasteToLeadsheetButton, gridBagConstraints);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        barNumberTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                barNumberTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel11.add(barNumberTextField, gridBagConstraints);

        jLabel40.setText("Bar #:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel11.add(jLabel40, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        jPanel6.add(jPanel11, gridBagConstraints);

        jLabel41.setText("*Leave Blank To Advance Step by Step*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        jPanel6.add(jLabel41, gridBagConstraints);

        jLabel39.setText("Optional: Choose Placement of Theme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanel6.add(jLabel39, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        customizeSolo.getContentPane().add(jPanel6, gridBagConstraints);

        jPanel13.setLayout(new java.awt.GridBagLayout());

        themeNameTextPane.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(themeNameTextPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel13.add(jScrollPane1, gridBagConstraints);

        jLabel44.setText("Chosen Theme:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel13.add(jLabel44, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        customizeSolo.getContentPane().add(jPanel13, gridBagConstraints);

        setLocation(new java.awt.Point(10, 10));
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(1000, 800));
        setPreferredSize(new java.awt.Dimension(1100, 820));
        setSize(new java.awt.Dimension(1100, 820));
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                ThemeWeaver.this.windowClosed(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        soloTableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        soloTableScrollPane.setMaximumSize(new java.awt.Dimension(32767, 400));
        soloTableScrollPane.setMinimumSize(new java.awt.Dimension(23, 350));
        soloTableScrollPane.setPreferredSize(new java.awt.Dimension(0, 0));
        soloTableScrollPane.setRowHeaderView(null);

        soloTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String []
            {
                "Name", "Theme Length", "Theme", "Prob. to Use", "Prob. to Transpose", "Prob. to Invert", "Prob. to Reverse", "Prob. to Expand", "Prob. to Side Slip", "Prob. to Bar Line Shift"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
        });
        soloTable.setToolTipText("shift-click a cell in a row of a theme to listen to it");
        soloTable.setAlignmentY(1.0F);
        soloTable.setColumnSelectionAllowed(true);
        soloTable.setGridColor(new java.awt.Color(0, 0, 0));
        soloTable.setPreferredSize(new java.awt.Dimension(675, 600));
        soloTable.setSelectionBackground(javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground"));
        soloTable.setShowGrid(true);
        soloTable.getTableHeader().setReorderingAllowed(false);
        soloTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                soloTableMouseClicked(evt);
            }
        });
        soloTable.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentShown(java.awt.event.ComponentEvent evt)
            {
                soloTableComponentShown(evt);
            }
        });
        soloTable.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                soloTableKeyPressed(evt);
            }
        });
        soloTableScrollPane.setViewportView(soloTable);
        soloTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.ipadx = 680;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, -38, 0);
        getContentPane().add(soloTableScrollPane, gridBagConstraints);

        themeListScrollPane.setMaximumSize(new java.awt.Dimension(0, 0));
        themeListScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        themeListScrollPane.setPreferredSize(new java.awt.Dimension(100, 100));

        themeList.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        themeList.setModel(themeListModel);
        themeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        themeList.setToolTipText("shift-click a theme to listen to it");
        themeList.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        themeList.setPreferredSize(null);
        themeList.setVisibleRowCount(30);
        themeList.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                themeListClicked(evt);
            }
        });
        themeList.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                themeListKeyPressed(evt);
            }
        });
        themeListScrollPane.setViewportView(themeList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 320;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        getContentPane().add(themeListScrollPane, gridBagConstraints);

        themeUsageScrollPane.setMinimumSize(new java.awt.Dimension(300, 100));

        themeUsageTextArea.setColumns(20);
        themeUsageTextArea.setRows(70);
        themeUsageScrollPane.setViewportView(themeUsageTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 30);
        getContentPane().add(themeUsageScrollPane, gridBagConstraints);

        SoloGeneratorTitle.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        SoloGeneratorTitle.setMaximumSize(new java.awt.Dimension(327, 327));
        SoloGeneratorTitle.setText("Theme Weaver ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 0);
        getContentPane().add(SoloGeneratorTitle, gridBagConstraints);

        themeIntervalTextField.setText("8");
        themeIntervalTextField.setMaximumSize(new java.awt.Dimension(50, 2147483647));
        themeIntervalTextField.setMinimumSize(new java.awt.Dimension(50, 28));
        themeIntervalTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                themeIntervalTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(themeIntervalTextField, gridBagConstraints);

        themeIntervalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        themeIntervalLabel.setText("Theme Use Interval (# of Beats)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        getContentPane().add(themeIntervalLabel, gridBagConstraints);

        themeProbTextField.setText("0.5");
        themeProbTextField.setMaximumSize(new java.awt.Dimension(50, 2147483647));
        themeProbTextField.setMinimumSize(new java.awt.Dimension(50, 28));
        themeProbTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                themeProbTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(themeProbTextField, gridBagConstraints);

        noThemeProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        noThemeProbLabel.setText("Probability of Using Theme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        getContentPane().add(noThemeProbLabel, gridBagConstraints);

        generateSoloJButton.setText("Generate Solo");
        generateSoloJButton.setToolTipText("Creates a solo from the themes in the table and displays and plays the solo in the leadsheet window");
        generateSoloJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                generateSoloJButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        getContentPane().add(generateSoloJButton, gridBagConstraints);

        stopPlaytoggle.setBackground(new java.awt.Color(255, 255, 255));
        stopPlaytoggle.setText("Stop Playing");
        stopPlaytoggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopPlaytoggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(stopPlaytoggle, gridBagConstraints);

        deleteThemebutton.setText("Delete Theme From File");
        deleteThemebutton.setPreferredSize(new java.awt.Dimension(78, 29));
        deleteThemebutton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteThemebuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(deleteThemebutton, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel4.setText("             Themes                      ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(jLabel4, gridBagConstraints);

        TransformationOrder.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        TransformationOrder.setText("Transformation Order");
        TransformationOrder.setToolTipText("If a transformation is entered more than once, it will use the default order (Transpose, Invert, Reverse, Expand, Side Slip).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        getContentPane().add(TransformationOrder, gridBagConstraints);
        TransformationOrder.getAccessibleContext().setAccessibleDescription("");

        jLabel28.setText("*Shift-Click on a row to play the theme*");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        getContentPane().add(jLabel28, gridBagConstraints);

        currentSelectionJButton.setText("Use Current Selection in Leadsheet Window as Theme");
        currentSelectionJButton.setToolTipText("Adds the selection from the window into the first empty theme cell in the table");
        currentSelectionJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                currentSelectionJButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(currentSelectionJButton, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        Reset.setText("Reset Table");
        Reset.setToolTipText("Clears the table of all themes currently entered");
        Reset.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel1.add(Reset, gridBagConstraints);

        expandPreferenceButton.setText("Open Expand Preferences");
        expandPreferenceButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                expandPreferenceButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        jPanel1.add(expandPreferenceButton, gridBagConstraints);

        jButton1.setText("Open Side Slip Preferences");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jButton1, gridBagConstraints);

        barLineShiftPreferencesButton.setText("Open Bar Line Shift Preferences");
        barLineShiftPreferencesButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                barLineShiftPreferencesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(barLineShiftPreferencesButton, gridBagConstraints);

        deleteRowbutton.setText("Delete Selected Row");
        deleteRowbutton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteRowbuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(deleteRowbutton, gridBagConstraints);

        generateThemeJButton.setText("Generate Theme");
        generateThemeJButton.setToolTipText("Generates a theme for every non empty length cell in the table that doesn't have a corresponding theme");
        generateThemeJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                generateThemeJButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 2.0;
        jPanel1.add(generateThemeJButton, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel20.setText("Set All Theme Probabilites To: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel20, gridBagConstraints);

        setAllProbTextField.setText("0.0");
        setAllProbTextField.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        jPanel3.add(setAllProbTextField, gridBagConstraints);

        setAllProbButton.setText("Set Probabilities");
        setAllProbButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                setAllProbButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(setAllProbButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        jPanel1.add(jPanel3, gridBagConstraints);

        jButton2.setText("Set All Probabilities Equal To 1st Theme");
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jButton2, gridBagConstraints);

        setProbToZeroButton.setText("Reset Theme Probabilites To Zero");
        setProbToZeroButton.setToolTipText("");
        setProbToZeroButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                setProbToZeroButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(setProbToZeroButton, gridBagConstraints);

        jLabel3.setText("Tranformation Preferences:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel11.setText("Edit Table:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jLabel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        rangeChooserButton.setText("Choose Range");
        rangeChooserButton.setToolTipText("");
        rangeChooserButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rangeChooserButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(rangeChooserButton, gridBagConstraints);

        jLabel19.setText("Range:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jLabel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        trans2ComboBox.addItem("Invert");
        trans2ComboBox.addItem("Reverse");
        trans2ComboBox.addItem("Expand");
        trans2ComboBox.addItem("Side Slip");
        trans2ComboBox.addItem("Bar Line Shift");
        trans2ComboBox.setSelectedItem("Invert");
        transformationComboBoxes[1] = trans2ComboBox;
        trans2ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                trans2ComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans2ComboBox, gridBagConstraints);

        trans1ComboBox.setToolTipText("");
        trans1ComboBox.addItem("Transpose");
        trans1ComboBox.addItem("Invert");
        trans1ComboBox.addItem("Reverse");
        trans1ComboBox.addItem("Expand");
        trans1ComboBox.addItem("Side Slip");
        trans1ComboBox.addItem("Bar Line Shift");
        trans1ComboBox.setSelectedItem("Transpose");
        transformationComboBoxes[0] = trans1ComboBox;
        trans1ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                trans1ComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans1ComboBox, gridBagConstraints);

        trans3ComboBox.addItem("Reverse");
        trans3ComboBox.addItem("Expand");
        trans3ComboBox.addItem("Side Slip");
        trans3ComboBox.addItem("Bar Line Shift");
        trans3ComboBox.setSelectedItem("Reverse");
        transformationComboBoxes[2] = trans3ComboBox;
        trans3ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                trans3ComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans3ComboBox, gridBagConstraints);

        trans4ComboBox.addItem("Expand");
        trans4ComboBox.addItem("Side Slip");
        trans4ComboBox.addItem("Bar Line Shift");
        trans4ComboBox.setSelectedItem("Expand");
        transformationComboBoxes[3] = trans4ComboBox;
        trans4ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                trans4ComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans4ComboBox, gridBagConstraints);

        trans5ComboBox.setToolTipText("");
        trans5ComboBox.addItem("Side Slip");
        trans5ComboBox.addItem("Bar Line Shift");
        trans5ComboBox.setSelectedItem("Side Slip");
        transformationComboBoxes[4] = trans5ComboBox;
        trans5ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                trans5ComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans5ComboBox, gridBagConstraints);

        trans6ComboBox.setToolTipText("");
        trans6ComboBox.addItem("Bar Line Shift");
        trans6ComboBox.setSelectedItem("Bar Line Shift");
        transformationComboBoxes[5] = trans6ComboBox;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipady = 24;
        jPanel5.add(trans6ComboBox, gridBagConstraints);

        jLabel5.setText("1:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel5, gridBagConstraints);

        jLabel6.setText("2:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel6, gridBagConstraints);

        jLabel7.setText("3:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel7, gridBagConstraints);

        jLabel8.setText("4:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel8, gridBagConstraints);

        jLabel9.setText("5:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel9, gridBagConstraints);

        jLabel14.setText("6:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel5.add(jLabel14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 6;
        getContentPane().add(jPanel5, gridBagConstraints);

        fileMenu.setText("File"); // NOI18N
        fileMenu.setMaximumSize(new java.awt.Dimension(50, 40));
        fileMenu.setPreferredSize(new java.awt.Dimension(50, 21));

        loadThemesMI.setText("Load Themes File");
        loadThemesMI.setToolTipText("Load a new vocabulary.");
        loadThemesMI.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                loadThemesMIActionPerformed(evt);
            }
        });
        fileMenu.add(loadThemesMI);

        saveAsAdvice.setText("Save Themes As");
        saveAsAdvice.setToolTipText("Save the current vocabulary in a file.");
        saveAsAdvice.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveAsThemesActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsAdvice);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Close this window."); // NOI18N
        exitMenuItem.setToolTipText("Closes this window."); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exitMenuItemexitMIhandler(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        roadmapMenuBar.add(fileMenu);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window"); // NOI18N
        windowMenu.addMenuListener(new javax.swing.event.MenuListener()
        {
            public void menuSelected(javax.swing.event.MenuEvent evt)
            {
                windowMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt)
            {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt)
            {
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window"); // NOI18N
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)"); // NOI18N
        closeWindowMI.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows"); // NOI18N
        cascadeMI.setToolTipText("Rearrange windows into a cascade.\n"); // NOI18N
        cascadeMI.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(jSeparator5);
        windowMenu.add(windowMenuSeparator);

        roadmapMenuBar.add(windowMenu);

        setJMenuBar(roadmapMenuBar);

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

public void stopPlaying()
  {
    notate.stopPlaying();
  }

private void playSelection()
  {
    notate.getCurrentStave().playSelection(false, notate.getLoopCount(), PlayScoreCommand.USEDRUMS, "LickGenFrame");
  }
    private void soloTableComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_soloTableComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_soloTableComponentShown

    private void soloTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_soloTableKeyPressed
        int index = soloTable.getSelectedRow();
        int col = soloTable.getSelectedColumn();

        if( evt.getKeyCode() == KeyEvent.VK_DELETE )
          {
            soloTableModel.removeRow(index);
          }

        // When enter is pressed on this cell, keep the same cell selected
        // (This can be conditioned on column later if desired.)
        if( evt.getKeyCode() == KeyEvent.VK_ENTER )
          {
            //index = index <= 1 ? 0 : index-1;
            //soloTable.setRowSelectionInterval(index, index);

            if( col == THEME_COLUMN && getValueAt(index, THEME_COLUMN) != null )
              {
                
                MelodyPart melody = new MelodyPart((String) getValueAt(index, THEME_COLUMN));
                int themeLengthBeats = melody.getSize() / BEAT;
                soloTable.setValueAt(themeLengthBeats + "", index, LENGTH_COLUMN);
              }
          }
    }//GEN-LAST:event_soloTableKeyPressed

    private void ResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetActionPerformed
        resetCheck.setVisible(true);

    }//GEN-LAST:event_ResetActionPerformed

    private void soloTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soloTableMouseClicked
        //Play a theme in the table using shift-click
        if( evt.isShiftDown() )
          {
            int index = soloTable.getSelectedRow();

            String name = (String) getValueAt(index, NAME_COLUMN);
            Score score = new Score(name);

            String themeString = (String) getValueAt(index, THEME_COLUMN);
            MelodyPart melody = new MelodyPart(themeString);

            score.addPart(melody);
            PatternDisplay.playScore(notate, score, themeWeaver);
          }
    }//GEN-LAST:event_soloTableMouseClicked

    private void fillWithOneOfEachTheme(){
        for(int i = 0; i < orderedThemes.size(); i++){
            for( int j = 0; j < soloTable.getRowCount(); j++ )
          {//loop through table

            if( //themeList.isSelectedIndex(i)&& 
                        (getValueAt(j, NAME_COLUMN) == null)
                    && (getValueAt(j, THEME_COLUMN) == null)
                    && (getValueAt(j, LENGTH_COLUMN) == null) )
              {
                //if a theme in the Themes scroll box is clicked and a theme
                //cell is selected and there is an empty row

                if( (getValueAt(j, USE_COLUMN) == null)
                        && (getValueAt(j, TRANSPOSE_COLUMN) == null)
                        && (getValueAt(j, INVERT_COLUMN) == null)
                        && (getValueAt(j, REVERSE_COLUMN) == null) 
                        && (getValueAt(j, EXPAND_COLUMN) == null)
                        && (getValueAt(j, SIDESLIP_COLUMN) == null)
                        && (getValueAt(j, BARLINESHIFT_COLUMN) == null))
                  {
                    // if the weighted value cells are null
                    //set default values for weighted values
                    soloTable.setValueAt(USE_DEFAULT_VALUE, j, USE_COLUMN);
                    soloTable.setValueAt(TRANSPOSE_DEFAULT_VALUE, j, TRANSPOSE_COLUMN);
                    soloTable.setValueAt(INVERT_DEFAULT_VALUE, j, INVERT_COLUMN);
                    soloTable.setValueAt(REVERSE_DEFAULT_VALUE, j, REVERSE_COLUMN);
                    soloTable.setValueAt(EXPAND_DEFAULT_VALUE, j, EXPAND_COLUMN);
                    soloTable.setValueAt(SIDESLIP_DEFAULT_VALUE, j, SIDESLIP_COLUMN);
                    soloTable.setValueAt(BARLINESHIFT_DEFAULT_VALUE, j, BARLINESHIFT_COLUMN);
                  }

                String name = orderedThemes.get(i);
                //String name = (String) themeList.getSelectedValue();
                //set name equal to the one clicked in the scroll box

                for( Map.Entry pair : allThemes.entrySet() )
                  {
                    //loop through entries in allThemes

                    if( name == pair.getValue() )
                      { //if the name in the themeList is equal to the name in the entry
                        Theme theme = (Theme) pair.getKey();
                        //set theme equal to the corresponding theme in that entry
                        MelodyPart melody = theme.melody; //get the melody of the theme
                        Part.PartIterator k = melody.iterator(); //iterate over melody
                        String themestring = ""; //set theme as empty to start

                        while( k.hasNext() ) //while you can still iterate through the melody
                          {
                            Unit unit = k.next();
                            if( unit != null ) //if next isn't empty
                              {
                                themestring += unit.toLeadsheet() + " ";
                                //add it to the theme in leadsheet notation
                              }
                          }

                        setValueAt(name, j, NAME_COLUMN);
                        //paste in the name of theme to the table
                        setValueAt(melody.size() / BEAT + "", j, LENGTH_COLUMN);
                        //paste in the theme length
                        setValueAt(themestring, j, THEME_COLUMN);
                        //paste in the theme in leadsheet notation

                        //in case the length is different than the one typed by the user 
                        int n = 0;

                        for( int x = 0; x < soloTable.getRowCount(); x++ )
                          { //loop through table
                            if( (getValueAt(x, NAME_COLUMN) != null)
                                    && ((((String) getValueAt(x, NAME_COLUMN)).equals(name))
                                    || ((String) getValueAt(x, NAME_COLUMN)).equals(name + "- " + n))
                                    && (x != j)
                                    && (((String) getValueAt(x, THEME_COLUMN)).equals(themestring)) )
                              {

                                n += 1; //add one to n so if the same theme 
                                //is already in the table it will be differentiated from it
                                //if the names are the same, the rows are different,
                                //the themes are the same, the lengths are different
                                setValueAt(melody.size() / BEAT + "", x, LENGTH_COLUMN);
                                //make the lengths the same 
                                setValueAt(name + "- " + n, j, NAME_COLUMN);

                                //make a copy of the theme and add it to the file and scroll box
                                Theme copy = Theme.makeTheme(name + "- " + n, melody);
                                addTheme(copy);
                                saveRules(fileName);
                                break;
                              }
                          }
                        break;
                      }
                  }
                break;
              }
          }        
        }
    }
    
    private void themeListClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_themeListClicked
        if( !evt.isShiftDown() )
          {
            // add theme into table if clicked and not shift-clicked
            for( int i = 0; i < orderedThemes.size(); i++ )
              { //loop through size of orderedThemes
                for( int j = 0; j < soloTable.getRowCount(); j++ )
                  {//loop through table

                    if( themeList.isSelectedIndex(i)
                            && (getValueAt(j, NAME_COLUMN) == null)
                            && (getValueAt(j, THEME_COLUMN) == null)
                            && (getValueAt(j, LENGTH_COLUMN) == null) )
                      {
                        //if a theme in the Themes scroll box is clicked and a theme
                        //cell is selected and there is an empty row

                        if( (getValueAt(j, USE_COLUMN) == null)
                                && (getValueAt(j, TRANSPOSE_COLUMN) == null)
                                && (getValueAt(j, INVERT_COLUMN) == null)
                                && (getValueAt(j, REVERSE_COLUMN) == null) 
                                && (getValueAt(j, EXPAND_COLUMN) == null)
                                && (getValueAt(j, SIDESLIP_COLUMN) == null)
                                && (getValueAt(j, BARLINESHIFT_COLUMN) == null))
                          {
                            // if the weighted value cells are null
                            //set default values for weighted values
                            soloTable.setValueAt(USE_DEFAULT_VALUE, j, USE_COLUMN);
                            soloTable.setValueAt(TRANSPOSE_DEFAULT_VALUE, j, TRANSPOSE_COLUMN);
                            soloTable.setValueAt(INVERT_DEFAULT_VALUE, j, INVERT_COLUMN);
                            soloTable.setValueAt(REVERSE_DEFAULT_VALUE, j, REVERSE_COLUMN);
                            soloTable.setValueAt(EXPAND_DEFAULT_VALUE, j, EXPAND_COLUMN);
                            soloTable.setValueAt(SIDESLIP_DEFAULT_VALUE, j, SIDESLIP_COLUMN);
                            soloTable.setValueAt(BARLINESHIFT_DEFAULT_VALUE, j, BARLINESHIFT_COLUMN);
                          }

                        String name = (String) themeList.getSelectedValue();
                        //set name equal to the one clicked in the scroll box

                        for( Map.Entry pair : allThemes.entrySet() )
                          {
                            //loop through entries in allThemes

                            if( name == pair.getValue() )
                              { //if the name in the themeList is equal to the name in the entry
                                Theme theme = (Theme) pair.getKey();
                                //set theme equal to the corresponding theme in that entry
                                MelodyPart melody = theme.melody; //get the melody of the theme
                                Part.PartIterator k = melody.iterator(); //iterate over melody
                                String themestring = ""; //set theme as empty to start

                                while( k.hasNext() ) //while you can still iterate through the melody
                                  {
                                    Unit unit = k.next();
                                    if( unit != null ) //if next isn't empty
                                      {
                                        themestring += unit.toLeadsheet() + " ";
                                        //add it to the theme in leadsheet notation
                                      }
                                  }

                                setValueAt(name, j, NAME_COLUMN);
                                //paste in the name of theme to the table
                                setValueAt(melody.size() / BEAT + "", j, LENGTH_COLUMN);
                                //paste in the theme length
                                setValueAt(themestring, j, THEME_COLUMN);
                                //paste in the theme in leadsheet notation

                                //in case the length is different than the one typed by the user 
                                int n = 0;

                                for( int x = 0; x < soloTable.getRowCount(); x++ )
                                  { //loop through table
                                    if( (getValueAt(x, NAME_COLUMN) != null)
                                            && ((((String) getValueAt(x, NAME_COLUMN)).equals(name))
                                            || ((String) getValueAt(x, NAME_COLUMN)).equals(name + "- " + n))
                                            && (x != j)
                                            && (((String) getValueAt(x, THEME_COLUMN)).equals(themestring)) )
                                      {

                                        n += 1; //add one to n so if the same theme 
                                        //is already in the table it will be differentiated from it
                                        //if the names are the same, the rows are different,
                                        //the themes are the same, the lengths are different
                                        setValueAt(melody.size() / BEAT + "", x, LENGTH_COLUMN);
                                        //make the lengths the same 
                                        setValueAt(name + "- " + n, j, NAME_COLUMN);

                                        //make a copy of the theme and add it to the file and scroll box
                                        Theme copy = Theme.makeTheme(name + "- " + n, melody);
                                        addTheme(copy);
                                        saveRules(fileName);
                                        break;
                                      }
                                  }
                                break;
                              }
                          }
                        break;
                      }
                  }
              }
          }

        //Play selected theme if shift-clicked
        if( evt.isShiftDown() )
          {
            for( int i = 0; i < orderedThemes.size(); i++ )
              { //loop through all saved themes
                if( themeList.isSelectedIndex(i) )
                  {
                    String name = (String) themeList.getSelectedValue();
                    Score score = new Score(name);
                    //create empty score with name of theme selected
                    for( Map.Entry pair : allThemes.entrySet() )
                      {
                        //loop through entries in allThemes

                        if( name == pair.getValue() )
                          {
                            //if the name in the themeList is equal to the name in the entry
                            Theme theme = (Theme) pair.getKey();
                            //set theme equal to the corresponding theme in that entry
                            MelodyPart melody = theme.melody; //get the melody of the theme
                            score.addPart(melody);
                            PatternDisplay.playScore(notate, score, themeWeaver);
                          }
                      }
                  }
              }
          }
    }//GEN-LAST:event_themeListClicked

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        if( orderedThemes.contains(nameField.getText()) )
          { //if the user enters a name that is already in orderedThemes
            nameErrorMessage.setVisible(true); //same error message pops up
          }
        else
          {
            for( int i = 0; i < soloTable.getRowCount(); i++ )
              {// loop through table

                if( soloTable.isCellSelected(i, NAME_COLUMN) )
                  {
                    setValueAt(nameField.getText(), i, NAME_COLUMN);
                    //set the name in the table
                    String name = nameField.getText();
                    String melodyString = (String) getValueAt(i, THEME_COLUMN);
                    MelodyPart themeMelody = new MelodyPart(melodyString);
                    Theme theme = Theme.makeTheme(name, themeMelody);

                    addTheme(theme);
                    saveRules(fileName);
                    //add the theme to the scroll box and save it
                  }
              }
          }
        nameErrorMessage.setVisible(false); //close the error window
    }//GEN-LAST:event_OkButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        nameErrorMessage.setVisible(false);
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void cellOkbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellOkbuttonActionPerformed
        enteredIncorrectly.setVisible(false);
    }//GEN-LAST:event_cellOkbuttonActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void YesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YesButtonActionPerformed
        soloTableModel.tableReset();
        resetCheck.setVisible(false);
    }//GEN-LAST:event_YesButtonActionPerformed

    private void NoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoButtonActionPerformed
        resetCheck.setVisible(false);
    }//GEN-LAST:event_NoButtonActionPerformed

    private void themeListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_themeListKeyPressed
    }//GEN-LAST:event_themeListKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    }//GEN-LAST:event_formKeyPressed

    private void nameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER )
          {
            if( orderedThemes.contains(nameField.getText()) )
              {
                //if the user enters a name that is already in orderedThemes
                nameErrorMessage.setVisible(true); //same error message pops up
              }
            else
              {
                for( int i = 0; i < soloTable.getRowCount(); i++ )
                  {// loop through table
                    if( soloTable.isCellSelected(i, NAME_COLUMN) )
                      {
                        setValueAt(nameField.getText(), i, NAME_COLUMN);
                        //set the name in the table
                        String name = nameField.getText();
                        String melodyString = (String) getValueAt(i, THEME_COLUMN);
                        MelodyPart themeMelody = new MelodyPart(melodyString);
                        Theme theme = Theme.makeTheme(name, themeMelody);
                        addTheme(theme);
                        saveRules(fileName);
                        //add the theme
                      }
                  }
              }
            nameErrorMessage.setVisible(false); //close the error window
          }
    }//GEN-LAST:event_nameFieldKeyPressed

    private void cellOkbuttonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cellOkbuttonKeyPressed
        enteredIncorrectly.setVisible(false);
    }//GEN-LAST:event_cellOkbuttonKeyPressed

    private void YesButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_YesButtonKeyPressed
        soloTableModel.tableReset();
        resetCheck.setVisible(false);
    }//GEN-LAST:event_YesButtonKeyPressed

    private void windowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_windowClosed
    {//GEN-HEADEREND:event_windowClosed
        closeWindow();
    }//GEN-LAST:event_windowClosed

    private void exitMenuItemexitMIhandler(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitMenuItemexitMIhandler
    {//GEN-HEADEREND:event_exitMenuItemexitMIhandler
        closeWindow();
    }//GEN-LAST:event_exitMenuItemexitMIhandler

    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
    {//GEN-HEADEREND:event_closeWindowMIActionPerformed
        closeWindow();
    }//GEN-LAST:event_closeWindowMIActionPerformed

private void closeWindow()
  {
    WindowRegistry.unregisterWindow(this);
    setVisible(false);
  }

    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
    {//GEN-HEADEREND:event_cascadeMIActionPerformed
        WindowRegistry.cascadeWindows(this);
    }//GEN-LAST:event_cascadeMIActionPerformed

    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
    {//GEN-HEADEREND:event_windowMenuMenuSelected
        windowMenu.removeAll();

        windowMenu.add(closeWindowMI);

        windowMenu.add(cascadeMI);

        for( WindowMenuItem w : WindowRegistry.getWindows() )
          {
            windowMenu.add(w.getMI(this)); // these are static, and calling getMI updates the name on them too in case the window title changed
          }
        windowMenu.repaint();
    }//GEN-LAST:event_windowMenuMenuSelected

    private void themeIntervalTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_themeIntervalTextFieldActionPerformed
    {//GEN-HEADEREND:event_themeIntervalTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_themeIntervalTextFieldActionPerformed

    private void themeProbTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_themeProbTextFieldActionPerformed
    {//GEN-HEADEREND:event_themeProbTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_themeProbTextFieldActionPerformed

    private void generateSoloJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSoloJButtonActionPerformed
        generateThemeWovenSolo();
    }//GEN-LAST:event_generateSoloJButtonActionPerformed
    
    public void generateThemeWovenSolo()
    {
        ArrayList<ThemeUse> themeUses = new ArrayList<ThemeUse>();
        //create an empty array of themeUses

        for( int i = 0; i < soloTable.getRowCount(); i++ )
          { //loop through table
            if( (getValueAt(i, THEME_COLUMN) == null)
                    && (getValueAt(i, LENGTH_COLUMN) != null) )
              {
                //if theme cell is empty and length isn't
                
                enteredIncorrectly.setVisible(true); //show error message
                
                break;
              }
            else if( getValueAt(i, THEME_COLUMN) != null )
              {
                if ( (getValueAt(i, USE_COLUMN) == null)
                        || (getValueAt(i, TRANSPOSE_COLUMN) == null)
                        || (getValueAt(i, INVERT_COLUMN) == null)
                        || (getValueAt(i, REVERSE_COLUMN) == null)
                        || (getValueAt(i, EXPAND_COLUMN) == null)
                        || (getValueAt(i, SIDESLIP_COLUMN) == null)
                        || (getValueAt(i, BARLINESHIFT_COLUMN) == null))
                {
                    //if one necessary cell isn't filled
                    
                    enteredIncorrectly.setVisible(true); //show error message 
                    break;
                }
                else if( !isNumeric((getValueAt(i, USE_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, TRANSPOSE_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, INVERT_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, REVERSE_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, EXPAND_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, SIDESLIP_COLUMN).toString()))
                        || !isNumeric((getValueAt(i, BARLINESHIFT_COLUMN).toString())))
                  {
                    //if theme cell not empty but weighted values are entered wrong
                    
                    enteredIncorrectly.setVisible(true); //show error message 
                    break;
                  }
                else
                  { //if all cells are entered correctly, form theme uses
                    ThemeUse use = new ThemeUse(new MelodyPart((String) getValueAt(i, THEME_COLUMN)));
                    use.probUse = Double.valueOf(String.valueOf(getValueAt(i, USE_COLUMN)));
                    use.probTranspose = Double.valueOf(String.valueOf(getValueAt(i, TRANSPOSE_COLUMN)));
                    use.probInvert = Double.valueOf(String.valueOf(getValueAt(i, INVERT_COLUMN)));
                    use.probReverse = Double.valueOf(String.valueOf(getValueAt(i, REVERSE_COLUMN)));
                    use.probExpand = Double.valueOf(String.valueOf(getValueAt(i, EXPAND_COLUMN)));
                    use.probSideslip = Double.valueOf(String.valueOf(getValueAt(i, SIDESLIP_COLUMN)));
                    use.probBarLineShift = Double.valueOf(String.valueOf(getValueAt(i, BARLINESHIFT_COLUMN)));
                    themeUses.add(use); // add a new ThemeUse to the arraylist with respective elements

                    if( getValueAt(i, NAME_COLUMN) != null )
                      {
                        //if the theme has a name assign it to the theme
                        use.theme.name = (String) getValueAt(i, NAME_COLUMN);
                      }
                  }
              }
          }
        if (themeUses.isEmpty())
        {
            enteredIncorrectly.setVisible(true);
            return;
        }

        myGenerateSolo(themeUses, cm);//generates the solo
        playSelection();

        //Set play button to say Stop Playing
        soloPlaying = false;
        stopPlaytoggle.setSelected(false);
        stopPlaytoggle.setText("<html><center>Stop Playing</center></html>");
    }
    
    private static boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    
    private void currentSelectionJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentSelectionJButtonActionPerformed
        // int index = soloTable.getSelectedRow();
        MelodyPart sel = notate.getCurrentStave().getMelodyPart().extract(
                notate.getCurrentSelectionStart(),
                notate.getCurrentSelectionEnd());

        // The commented-out code below illustrates how we can get the relative-pitch
        // melody from a selection, then use it over other chords at other parts
        // of the progression. Moreover, the shape of the original melody
        // will be roughly maintained (except for possible octave displacements).
        // We could use the relative-pitch melody as the Theme in lieu of the
        // absolute pitch one we are now using.
        Polylist relativePitchMelody = 
            NoteConverter.melodyPart2Relative(sel, 
                                              notate.getChordProg(), 
                                              notate.getCurrentSelectionStart());

        //System.out.println("FYI: relative pitch melody: "+ relativePitchMelody);
        
        MelodyPart gen = fillMelody(BEAT, 
                                    relativePitchMelody, 
                                    notate.getChordProg(),  
                                    notate.getCurrentSelectionStart());
        
        //System.out.println("FYI: filled melody: "+ gen);


        Part.PartIterator i = sel.iterator();
        String theme = "";
        while( i.hasNext() )
          {
            theme += i.next().toLeadsheet() + " ";
          }
        for( int j = 0; j < soloTable.getRowCount(); j++ )
          {
            if( (getValueAt(j, NAME_COLUMN) == null)
                    && (getValueAt(j, THEME_COLUMN) == null)
                    && (getValueAt(j, LENGTH_COLUMN) == null) )
              {
                setValueAt(theme, j, THEME_COLUMN);
                setValueAt(sel.getSize() / BEAT + "", j, LENGTH_COLUMN);

                MelodyPart melody = new MelodyPart((String) getValueAt(j, THEME_COLUMN));

                for( Map.Entry pair : allThemes.entrySet() )
                  {
                    //loop through all the entry sets of {Theme,name} in allThemes
                    Theme ThemeKey = (Theme) pair.getKey(); //get the Theme of each entry

                    if( melody.toString().equals(ThemeKey.melody.toString()) )
                      {
                        //if the melody in allThemes is the name as the melody in the table
                        setValueAt(pair.getValue(), j, NAME_COLUMN);
                        //set the name to the one that matches that theme
                      }
                    else
                      {// if there is no matching theme in allThemes
                        setValueAt(null, j, NAME_COLUMN); //set the name to empty
                      }
                  }
                break;
              }
          }
    }//GEN-LAST:event_currentSelectionJButtonActionPerformed

    private void loadThemesMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadThemesMIActionPerformed
    {//GEN-HEADEREND:event_loadThemesMIActionPerformed
        themesfc.setDialogTitle("Load Themes File");

        themesfc.resetChoosableFileFilters();

        themesfc.addChoosableFileFilter(new ThemesFilter());

        if( themesfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            cm.execute(new LoadThemesCommand(themesfc.getSelectedFile(), this, notate));
          }

        savedThemes = themesfc.getSelectedFile();
    }//GEN-LAST:event_loadThemesMIActionPerformed

    private void saveAsThemesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveAsThemesActionPerformed
    {//GEN-HEADEREND:event_saveAsThemesActionPerformed
        if( themesfc == null )
          {
            return;
          }

        themesfc.setDialogTitle("Save Themes As");
        themesfc.setCurrentDirectory(ImproVisor.getVocabDirectory());

        // If never saved before, used the name specified in vocFile.
        // Otherwise use previous file.

        if( savedThemes == null )
          {
            themesfc.setSelectedFile(new File(themesFile));
          }

        themesfc.resetChoosableFileFilters();
        themesfc.addChoosableFileFilter(new ThemesFilter());

        if( themesfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            if( themesfc.getSelectedFile().getName().endsWith(themesExt) )
              {
                new SaveThemesCommand(themesfc.getSelectedFile(), this).execute();

                savedThemes = themesfc.getSelectedFile();
              }
            else
              {
                String file = themesfc.getSelectedFile().getAbsolutePath() + themesExt;

                savedThemes = new File(file);

                new SaveThemesCommand(savedThemes, this).execute();
              }
          }
    }//GEN-LAST:event_saveAsThemesActionPerformed

    private void stopPlaytoggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPlaytoggleActionPerformed
        soloPlaying = stopPlaytoggle.isSelected();

        if( soloPlaying )
          {
              //System.out.println("!!!!!!!!!!!!!!$$$$$@*($&!@$(*@&*(");
            stopPlaytoggle.setText("<html><center>Play Solo</center></html>");
            stopPlaying();
          }
        else
          {
            soloPlaying = false;
            stopPlaytoggle.setSelected(false);
            stopPlaytoggle.setText("<html><center>Stop Playing</center></html>");
            playSelection();
          }
    }//GEN-LAST:event_stopPlaytoggleActionPerformed

    private void deleteRowbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRowbuttonActionPerformed
        int index = soloTable.getSelectedRow();
        if (index >= 0)
        {
            soloTableModel.removeRow(index);
        }
        else
        {
            noRowSelected.setVisible(true);
        }
    }//GEN-LAST:event_deleteRowbuttonActionPerformed

    private void deleteThemebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteThemebuttonActionPerformed
        deleteThemeDialog.setVisible(true);
    }//GEN-LAST:event_deleteThemebuttonActionPerformed

    private void pitchRangeMaxSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pitchRangeMaxSliderMouseDragged
            
    }//GEN-LAST:event_pitchRangeMaxSliderMouseDragged

    private void trans1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trans1ComboBoxActionPerformed
        //sets transformation 1-6 according to what the user chose for 1
        canEnter2 = false;
        canEnter3 = false;
        canEnter4 = false;
        canEnter5 = false;//doesn't let trans2(3, 4, and 5)ComboBoxActionPerformed activate at the wrong time
        transformNum = 1;
        adjustItemsInComboBox(1, 6);
    }//GEN-LAST:event_trans1ComboBoxActionPerformed

    private void trans2ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trans2ComboBoxActionPerformed
        //sets transformation 2-6 according to what the user chose for 2
        if (canEnter2)
        {
            canEnter3=false;
            canEnter4=false;
            canEnter5=false;
            transformNum = 2;
            adjustItemsInComboBox(2, 5);
        }
    }//GEN-LAST:event_trans2ComboBoxActionPerformed

    private void trans3ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trans3ComboBoxActionPerformed
        //sets transformation 3-6 according to what the user chose for 3
        if (canEnter3)
        {
            canEnter4=false;
            canEnter5=false;
            transformNum = 3;
            adjustItemsInComboBox(3, 4);
        }
    }//GEN-LAST:event_trans3ComboBoxActionPerformed

    private void trans4ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trans4ComboBoxActionPerformed
        //sets transformation 4-6 according to what the user chose for 4
        if (canEnter4)
        {
            canEnter5=false;
            transformNum = 4;
            adjustItemsInComboBox(4, 3);
        }
    }//GEN-LAST:event_trans4ComboBoxActionPerformed

    private void cellOkbutton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellOkbutton1ActionPerformed
        rangeTooSmall.setVisible(false);
        stopPlaying();
    }//GEN-LAST:event_cellOkbutton1ActionPerformed

    private void yesdeletethemeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesdeletethemeButtonActionPerformed
        String name = (String) themeList.getSelectedValue();
        
        //delete the desired theme from the table
        for( int j = 0; j < soloTable.getRowCount(); j++ )
          {
            if(soloTable.getValueAt(j, NAME_COLUMN) != null 
                    && (soloTable.getValueAt(j, NAME_COLUMN).equals(name)))
              {
                soloTable.setValueAt(null, j, NAME_COLUMN);
                soloTable.setValueAt(null, j, LENGTH_COLUMN);
                soloTable.setValueAt(null, j, THEME_COLUMN);
                soloTable.setValueAt(null, j, USE_COLUMN);
                soloTable.setValueAt(null, j, TRANSPOSE_COLUMN);
                soloTable.setValueAt(null, j, INVERT_COLUMN);
                soloTable.setValueAt(null, j, REVERSE_COLUMN);
                soloTable.setValueAt(null, j, EXPAND_COLUMN);
                soloTable.setValueAt(null, j, SIDESLIP_COLUMN);
                soloTable.setValueAt(null, j, BARLINESHIFT_COLUMN);
              }
          }

        //delete the selected theme from file
        for( int i = 0; i < orderedThemes.size(); i++ )
          {
            if( themeList.isSelectedIndex(i) )
              {
                deleteTheme(name);
              }
          }
        
        deleteThemeDialog.setVisible(false);
        
    }//GEN-LAST:event_yesdeletethemeButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        deleteThemeDialog.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void cellOkbutton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellOkbutton2ActionPerformed
        noRowSelected.setVisible(false);
    }//GEN-LAST:event_cellOkbutton2ActionPerformed

    private void rangeTooSmallWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_rangeTooSmallWindowOpened
        stopPlaying();
    }//GEN-LAST:event_rangeTooSmallWindowOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setAllProb();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void setProbToZeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setProbToZeroButtonActionPerformed
        setAllProbTo(0.0);
    }//GEN-LAST:event_setProbToZeroButtonActionPerformed

    private void expandPreferencewindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_expandPreferencewindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_expandPreferencewindowClosed

    private void expandPreferenceformKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_expandPreferenceformKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_expandPreferenceformKeyPressed

    private void expandPreferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandPreferenceButtonActionPerformed
        expandPreference.setVisible(true);
    }//GEN-LAST:event_expandPreferenceButtonActionPerformed

    private void sideslipPreferenceformKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sideslipPreferenceformKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_sideslipPreferenceformKeyPressed

    private void sideslipPreferencewindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_sideslipPreferencewindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_sideslipPreferencewindowClosed

    private void probShiftForwardorBackSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_probShiftForwardorBackSliderMouseReleased
        probForwardShift = probShiftForwardorBackSlider.getValue()/100.0;
    }//GEN-LAST:event_probShiftForwardorBackSliderMouseReleased

    private void probThirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_probThirdActionPerformed
        probThirdSideSlip = Double.parseDouble(probThird.getText());
    }//GEN-LAST:event_probThirdActionPerformed

    private void probWholeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_probWholeActionPerformed
        probWholeSideSlip = Double.parseDouble(probWhole.getText());
    }//GEN-LAST:event_probWholeActionPerformed

    private void probHalfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_probHalfActionPerformed
        probHalfSideSlip = Double.parseDouble(probHalf.getText());
    }//GEN-LAST:event_probHalfActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sideslipPreference.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void generateThemeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateThemeJButtonActionPerformed
        generateTheme();
    }//GEN-LAST:event_generateThemeJButtonActionPerformed

    private void trans5ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trans5ComboBoxActionPerformed
        //sets transformation 4-5 according to what the user chose for 4
        if (canEnter5)
        {
            transformNum = 5;
            adjustItemsInComboBox(5, 2);
            canEnter2=true;
            canEnter3=true;
            canEnter4=true;
            canEnter5=true;
        }
    }//GEN-LAST:event_trans5ComboBoxActionPerformed

    private void probShiftForwardorBackSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_probShiftForwardorBackSliderMouseDragged
        probForwardShift = probShiftForwardorBackSlider.getValue()/100.0;
    }//GEN-LAST:event_probShiftForwardorBackSliderMouseDragged

    private void probUpOrDownMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_probUpOrDownMouseDragged
        probSlideUp = probUpOrDown.getValue()/100;
    }//GEN-LAST:event_probUpOrDownMouseDragged

    private void barLineShiftPreferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barLineShiftPreferencesButtonActionPerformed
        barLineShiftPreference.setVisible(true);
    }//GEN-LAST:event_barLineShiftPreferencesButtonActionPerformed

    private void rangeChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rangeChooserButtonActionPerformed
        RangeChooser rangeChooser = new RangeChooser(notate, minPitch, maxPitch, OCTAVE);
        range = rangeChooser.getRange();
        minPitch = range[0];
        maxPitch = range[1];
    }//GEN-LAST:event_rangeChooserButtonActionPerformed

    private void setAllProbButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setAllProbButtonActionPerformed
        setAllProbTo(Double.parseDouble(setAllProbTextField.getText()));
    }//GEN-LAST:event_setAllProbButtonActionPerformed

    public void openCustomizeSoloWindow()
    {
        customizeSolo.setVisible(true);
        transposeUpRadioButton.setSelected(true);
        directionOfTransposition = "up";
        distanceOfTransposition = "half";
        halfStepRadioButton.setSelected(true);
        expandBy2RadioButton.setSelected(true);
        expandBy = 2;
        sideslipUpRadioButton.setSelected(true);
        directionOfSideslip = "up";
        distanceOfSideslip = "half";
        halfStepRadioButton1.setSelected(true);
        shiftForwardRadioButton.setSelected(true);
        directionOfShift = "forwards";
        eighthShiftRadioButton.setSelected(true);
        
        customSolo = new MelodyPart();
    }
    
    private void customizeSolowindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_customizeSolowindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_customizeSolowindowClosed

    private void customizeSoloformKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customizeSoloformKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_customizeSoloformKeyPressed

    private void themeList1Clicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_themeList1Clicked
        chosenThemeName = (String) themeList1.getSelectedValue();
        
        //set name equal to the one clicked in the scroll box
        for( Map.Entry pair : allThemes.entrySet() )
        {
          //loop through entries in allThemes

          if( chosenThemeName == pair.getValue() )
            { //if the name in the themeList is equal to the name in the entry
              Theme theme = (Theme) pair.getKey();
              //set theme equal to the corresponding theme in that entry
              chosenCustomTheme = theme.melody; //get the melody of the theme
              chosenCustomThemeOriginal = theme.melody;
              break;
            }
        }       
        
        themeNameTextPane.setText(chosenThemeName);
    }//GEN-LAST:event_themeList1Clicked

    private void resetTransformationChoicesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetTransformationChoicesButtonActionPerformed
        resetTransformationChoices();
    }//GEN-LAST:event_resetTransformationChoicesButtonActionPerformed

    private void resetTransformationChoices()
    {
        transformationOrder.clear();
        transposeButton.setSelected(false);
        invertButton.setSelected(false);
        reverseButton.setSelected(false);
        expandButton.setSelected(false);
        sideslipButton.setSelected(false);
        barlineshiftButton.setSelected(false);
        transposeButtonPressed = false;
        invertButtonPressed = false;
        reverseButtonPressed = false;
        expandButtonPressed = false;
        sideslipButtonPressed = false;
        barlineshiftButtonPressed = false;
        
        chosenCustomTheme = chosenCustomThemeOriginal.copy();
        transformationsUsedTextArea.setText(null);
    }
    private void barlineshiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barlineshiftButtonActionPerformed
        if (barlineshiftButtonPressed == false)
        {
            barlineshiftButtonPressed = true;
            transformationOrder.add("Bar Line Shift");
        }
        else
        {
            barlineshiftButtonPressed = false;
            transformationOrder.remove("Bar Line Shift");
        }
        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_barlineshiftButtonActionPerformed

    private void sideslipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sideslipButtonActionPerformed
        if (sideslipButtonPressed == false)
        {
            sideslipButtonPressed = true;
            transformationOrder.add("Side Slip");
        }
        else
        {
            sideslipButtonPressed = false;
            transformationOrder.remove("Side Slip");
        }

        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_sideslipButtonActionPerformed

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if (expandButtonPressed == false)
        {
            expandButtonPressed = true;
            transformationOrder.add("Expand");
        }
        else
        {
            expandButtonPressed = false;
            transformationOrder.remove("Expand");
        }
        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_expandButtonActionPerformed

    private void reverseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reverseButtonActionPerformed

        if (reverseButtonPressed == false)
        {
            reverseButtonPressed = true;
            transformationOrder.add("Reverse");
        }
        else
        {
            reverseButtonPressed = false;
            transformationOrder.remove("Reverse");
        }

        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_reverseButtonActionPerformed

    private void invertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertButtonActionPerformed
        if (invertButtonPressed == false)
        {
            invertButtonPressed = true;
            transformationOrder.add("Invert");
        }
        else
        {
            invertButtonPressed = false;
            transformationOrder.remove("Invert");
        }
        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_invertButtonActionPerformed

    private void transposeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeButtonActionPerformed
        if (transposeButtonPressed == false)
        {
            transposeButtonPressed = true;
            transformationOrder.add("Transpose");
        }
        else
        {
            transposeButtonPressed = false;
            transformationOrder.remove("Transpose");
        }
        setTransformationsTextArea();
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_transposeButtonActionPerformed

    private void playThemeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playThemeButtonActionPerformed
        //play it
        String name = (String) themeList.getSelectedValue();
        Score score = new Score(name);
       
        score.addPart(chosenCustomTheme);
        PatternDisplay.playScore(notate, score, themeWeaver);
    }//GEN-LAST:event_playThemeButtonActionPerformed

    private void transposeUpRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeUpRadioButtonActionPerformed
        transposeDownRadioButton.setSelected(false);
        directionOfTransposition = "up";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_transposeUpRadioButtonActionPerformed

    private void transposeDownRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeDownRadioButtonActionPerformed
        transposeUpRadioButton.setSelected(false);
        directionOfTransposition = "down";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_transposeDownRadioButtonActionPerformed

    private void halfStepRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_halfStepRadioButtonActionPerformed
        wholeStepRadioButton.setSelected(false);
        thirdRadioButton.setSelected(false);
        distanceOfTransposition = "half";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_halfStepRadioButtonActionPerformed

    private void wholeStepRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wholeStepRadioButtonActionPerformed
        halfStepRadioButton.setSelected(false);
        thirdRadioButton.setSelected(false);
        distanceOfTransposition = "whole";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_wholeStepRadioButtonActionPerformed

    private void thirdRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thirdRadioButtonActionPerformed
        wholeStepRadioButton.setSelected(false);
        halfStepRadioButton.setSelected(false);
        distanceOfTransposition = "third";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_thirdRadioButtonActionPerformed

    private void expandBy3RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandBy3RadioButtonActionPerformed
        expandBy2RadioButton.setSelected(false);
        expandBy = 3;
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_expandBy3RadioButtonActionPerformed

    private void expandBy2RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandBy2RadioButtonActionPerformed
        expandBy3RadioButton.setSelected(false);
        expandBy = 2;
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_expandBy2RadioButtonActionPerformed

    private void sideslipUpRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sideslipUpRadioButtonActionPerformed
        sideslipDownRadioButton.setSelected(false);
        directionOfSideslip = "up";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_sideslipUpRadioButtonActionPerformed

    private void sideslipDownRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sideslipDownRadioButtonActionPerformed
        sideslipUpRadioButton.setSelected(false);
        directionOfSideslip = "down";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_sideslipDownRadioButtonActionPerformed

    private void halfStepRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_halfStepRadioButton1ActionPerformed
        wholeStepRadioButton1.setSelected(false);
        thirdRadioButton1.setSelected(false);
        distanceOfSideslip = "half";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_halfStepRadioButton1ActionPerformed

    private void wholeStepRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wholeStepRadioButton1ActionPerformed
        halfStepRadioButton1.setSelected(false);
        thirdRadioButton1.setSelected(false);
        distanceOfSideslip = "whole";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_wholeStepRadioButton1ActionPerformed

    private void thirdRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thirdRadioButton1ActionPerformed
        wholeStepRadioButton1.setSelected(false);
        halfStepRadioButton1.setSelected(false);
        distanceOfSideslip = "third";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_thirdRadioButton1ActionPerformed

    private void shiftForwardRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftForwardRadioButtonActionPerformed
        shiftBackwardRadioButton.setSelected(false);
        directionOfShift = "forwards";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_shiftForwardRadioButtonActionPerformed

    private void shiftBackwardRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftBackwardRadioButtonActionPerformed
        shiftForwardRadioButton.setSelected(false);
        directionOfShift = "backwards";
        //tranform theme
        MelodyPart adjustedTheme = chosenCustomThemeOriginal.copy();
        adjustedTheme = transformTheme(adjustedTheme, transformationOrder);
        chosenCustomTheme = adjustedTheme;
    }//GEN-LAST:event_shiftBackwardRadioButtonActionPerformed
    
    private void pasteToLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteToLeadsheetButtonActionPerformed
        MelodyPart last = customSolo.copy();
        edits.push(last);
        if (keepEditsFromLeadsheetCheckBox.isSelected())
        {
            notate.selectAll();
            MelodyPart wholeLeadsheet = notate.getCurrentMelodyPart();
            
            int lastIndex = 0;
            try//make sure lastIndex isn't null
            {
                lastIndex = wholeLeadsheet.getLastNoteIndex();
                customSolo = wholeLeadsheet.extract(0, lastIndex);
                currentSlotCS = customSolo.getSize();
            }
            
            catch(java.lang.NullPointerException n)
            {
                lastIndex = 0;
            }
           
        }
        //keeps there from being tons of tied notes where there should be rests
        chosenCustomTheme.pasteSlots(new MelodyPart(), chosenCustomTheme.getSize());
        if (!barNumberTextField.getText().equals(""))//see if we need to go to a specific bar or not
        {
            specifiedBar = true;
            try//make sure the user entered an integer for the bar number
            {
                Integer.parseInt(barNumberTextField.getText());
            }
            
            catch(java.lang.NumberFormatException n)
            {
                enterAnInteger.setVisible(true);
                return;
            }
            int barSlot = 480*(Integer.parseInt(barNumberTextField.getText())-1);
            
            if (currentSlotCS < barSlot+chosenCustomTheme.getSize())
            {
                int oldLength = customSolo.copy().getSize();
                MelodyPart rests = new MelodyPart(barSlot-customSolo.getSize());
                customSolo.setSize(barSlot + chosenCustomTheme.getSize());
                customSolo.pasteSlots(rests, oldLength);
            }
            currentSlotCS = barSlot;
        }
        else
        {
            customSolo.setSize(customSolo.getSize() + chosenCustomTheme.getSize());
        }
        
        customSolo.pasteSlots(chosenCustomTheme, currentSlotCS);
        currentSlotCS += chosenCustomTheme.getSize();
        notate.setCurrentSelectionStart(0); //start selection at beginning
        notate.pasteMelody(customSolo); //paste solo into leadsheet
        specifiedBar = false;
    }//GEN-LAST:event_pasteToLeadsheetButtonActionPerformed

    private void quarterShiftRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quarterShiftRadioButtonActionPerformed
        eighthShiftRadioButton.setSelected(false);
    }//GEN-LAST:event_quarterShiftRadioButtonActionPerformed

    private void eighthShiftRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eighthShiftRadioButtonActionPerformed
        quarterShiftRadioButton.setSelected(false);
    }//GEN-LAST:event_eighthShiftRadioButtonActionPerformed

    private void resetSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSoloButtonActionPerformed
        resetCheck1.setVisible(true);
    }//GEN-LAST:event_resetSoloButtonActionPerformed

    private void YesButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YesButton1ActionPerformed
        notate.delAllMelody();
        customSolo = new MelodyPart(0);
        currentSlotCS = 0;
        notate.pasteMelody(customSolo);
        edits.push(customSolo);
        resetCheck1.setVisible(false);
    }//GEN-LAST:event_YesButton1ActionPerformed

    private void YesButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_YesButton1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_YesButton1KeyPressed

    private void NoButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoButton1ActionPerformed
        resetCheck1.setVisible(false);
    }//GEN-LAST:event_NoButton1ActionPerformed

    private void barNumberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barNumberTextFieldActionPerformed
       
    }//GEN-LAST:event_barNumberTextFieldActionPerformed

    private void cellOkbutton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellOkbutton3ActionPerformed
        enterAnInteger.setVisible(false);
    }//GEN-LAST:event_cellOkbutton3ActionPerformed

    private void rectifySoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectifySoloButtonActionPerformed
        int length = customSolo.getSize();
        ChordPart themeChords = notate.getChordProg().extract(0, length);
        //set chords of theme to be the chordpart extracted from length to length +length
        cm.execute(new RectifyPitchesCommand(customSolo, 0, length, themeChords, false, false, true, false, false));
        notate.setCurrentSelectionStart(0); //start selection at beginning
        
        notate.pasteMelody(customSolo); //paste solo into leadsheet
        edits.push(customSolo);
    }//GEN-LAST:event_rectifySoloButtonActionPerformed

    private void undoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoEditButtonActionPerformed
        if (!edits.isEmpty())
        {
            undos.push(customSolo);
            customSolo = edits.pop();
            notate.pasteMelody(customSolo);

            notate.setCurrentSelectionStart(0); //start selection at beginning
            notate.delAllMelody();
            notate.pasteMelody(customSolo); //paste solo into leadsheet
            currentSlotCS = customSolo.getSize();
        }
    }//GEN-LAST:event_undoEditButtonActionPerformed

    private void fillSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillSoloButtonActionPerformed
        int measureSize = 480;
        ArrayList<Integer> emptyStartSlot = new ArrayList<Integer>();
        
        //loop through the solo to find the measures that are empty
        //add them to the array list
        for (int i=0; i<customSolo.getSize(); i+=measureSize)
        {
            MelodyPart tempSolo = customSolo.extract(i, customSolo.getSize());
            int numSlotsToNextNote = tempSolo.getFreeSlots(0);
            if (numSlotsToNextNote >= measureSize)
            {
                emptyStartSlot.add(i);
                i+= measureSize*(numSlotsToNextNote%measureSize);
            }
        }
        MelodyPart miniMelody = new MelodyPart(480);//mini melody is the melody that will be
                                                    //added to the solo
        emptyStartSlot.add(-1);//this is so it will fill the whole space and wont stop early
        
        if (emptyStartSlot.size() > 0)
        {
            int startSlot = emptyStartSlot.get(0);
            for (int i=1; i<emptyStartSlot.size(); i++)
            {
                if (emptyStartSlot.get(i-1)+measureSize == emptyStartSlot.get(i))
                {//if the current measure is adjacent to the previous
                    miniMelody.setSize(miniMelody.getSize() + measureSize);//increase melody size
                }
                else
                {//add an actual melody to miniMelody and add it to the correct place in the solo
                    miniMelody = generateFromGrammar(miniMelody.getSize(), miniMelody, startSlot, minPitch, maxPitch);
                    
                    customSolo.pasteSlots(miniMelody, startSlot);

                    startSlot = emptyStartSlot.get(i);
                    miniMelody = new MelodyPart(480);
                }
            }
        }
        
        notate.setCurrentSelectionStart(0); //start selection at beginning
        
        notate.pasteMelody(customSolo); //paste solo into leadsheet
        edits.push(customSolo);
    }//GEN-LAST:event_fillSoloButtonActionPerformed

    private void keepEditsFromLeadsheetCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keepEditsFromLeadsheetCheckBoxActionPerformed
        
    }//GEN-LAST:event_keepEditsFromLeadsheetCheckBoxActionPerformed

    private void redoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoEditButtonActionPerformed
        if (!undos.isEmpty())
        {
            customSolo = undos.pop();
            edits.push(customSolo);
            notate.pasteMelody(customSolo);
        
            notate.setCurrentSelectionStart(0); //start selection at beginning
            notate.delAllMelody();
            notate.pasteMelody(customSolo); //paste solo into leadsheet
            currentSlotCS = customSolo.getSize();
        }
    }//GEN-LAST:event_redoEditButtonActionPerformed
    
    
    private void setTransformationsTextArea()
    {
        transformationsUsedTextArea.setText(null);
        for (int i=0; i<transformationOrder.size(); i++)
        {
            transformationsUsedTextArea.append(transformationOrder.get(i) + "\n");
        }
    }
    
    private void setAllProb()
    {//sets all the probabilites to the same value
        Object probUse = soloTable.getValueAt(0, 3);
        Object probTranpose = soloTable.getValueAt(0, 4);
        Object probInvert = soloTable.getValueAt(0, 5);
        Object probReverse = soloTable.getValueAt(0, 6);
        Object probExpand = soloTable.getValueAt(0, 7);
        Object probSideSlip = soloTable.getValueAt(0, 8);
        Object probBarLineShift = soloTable.getValueAt(0,9);
        Object [] probs = new Object[7];
        probs[0] = probUse;
        probs[1] = probTranpose;
        probs[2] = probInvert;
        probs[3] = probReverse;
        probs[4] = probExpand;
        probs[5] = probSideSlip;
        probs[6] = probBarLineShift;
        
        for (int r=1; r<soloTable.getRowCount(); r++)
        {
            for (int c=3; c<soloTable.getColumnCount(); c++)
            {
                if (soloTable.getValueAt(r,2)==null)
                {
                    return;
                }
                soloTable.setValueAt(probs[c-3], r, c);
            }
        }
    }
    
    private void setAllProbTo(double prob)
    {//sets all probabilites to prob
        for (int r=0; r<soloTable.getRowCount(); r++)
        {
            for (int c=4; c<soloTable.getColumnCount(); c++)
            {
                if (soloTable.getValueAt(r,2)==null)
                {
                    return;
                }
                soloTable.setValueAt(prob, r, c);
            }
        }
    }
    
    private void adjustItemsInComboBox(int start, int numItems)
    {//updates the remaining transformations so that they don't include one
        //that was previously chosen. prevents duplicates
        for (int i=start-1; i<5; i++)
        {
            itemsLeft.clear();
            javax.swing.JComboBox currBox = transformationComboBoxes[i];
            javax.swing.JComboBox nextBox = transformationComboBoxes[i+1];
            Object chosenItem = currBox.getSelectedItem();
            for (int j=0; j<numItems; j++)
            {
                Object currItem = currBox.getItemAt(j);
                if (!currItem.equals(chosenItem))
                {
                    itemsLeft.add(currItem);
                }
            }
            nextBox.removeAllItems();
            for (int j=0; j<numItems-1; j++)
            {
                nextBox.addItem(itemsLeft.get(j));
            }
            nextBox.setSelectedIndex(0);
            numItems-=1;
        }
        
        canEnter2=true;
        canEnter3=true; 
        canEnter4=true;
        canEnter5=true;
    }
    

private SoloGeneratorTableModel soloTableModel = new SoloGeneratorTableModel(
        new Object[][]
  {
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null, null, null, null
      }
  },
        new String[]
  {
    "Name", "Length", "Theme", "Use", "Transpose", "Invert", "Reverse", "Expand", "Side Slip", "Bar Line Shift"
  });

public class SoloGeneratorTableModel extends DefaultTableModel
{

private static final int columnCount = 10;
boolean[] canEdit = new boolean[]
  {
    //name, theme, length, use, transpose, invert, reverse, expand, side slip, bar line shift
    true, true, true, true, true, true, true, true, true, true
  };
int[] columnWidths = new int[]
  {
    40, 4, 40, 3, 10, 5, 5, 5, 5, 40
  };
int[] columnAdjustment =
  {
    DefaultTableCellRenderer.LEFT,
    DefaultTableCellRenderer.LEFT,
    DefaultTableCellRenderer.LEFT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
    DefaultTableCellRenderer.RIGHT,
  };

public SoloGeneratorTableModel(Object[][] myTable, String[] columnHeaders)
  {
    super(myTable, columnHeaders);
  }

public int getColumnWidths(int index)
  {
    return columnWidths[index];
  }
//

public int getColumnAdjustments(int index)
  {
    return columnAdjustment[index];
  }

@Override
public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return canEdit[columnIndex];
  }

public void tableRefresh()
  {
    for( int i = 0; i < soloTable.getRowCount(); i++ )
      {

        //add back rows that were deleted
        if( soloTable.getRowCount() != ROW_COUNT )
          {
            for( int x = 0; x < ROW_COUNT - soloTable.getRowCount(); x++ )
              {
                soloTableModel.addARow();
              }
          }

        soloTable.setValueAt(null, i, NAME_COLUMN);
        soloTable.setValueAt(null, i, LENGTH_COLUMN);
        soloTable.setValueAt(null, i, THEME_COLUMN);
        soloTable.setValueAt(null, i, USE_COLUMN);
        soloTable.setValueAt(null, i, TRANSPOSE_COLUMN);
        soloTable.setValueAt(null, i, INVERT_COLUMN);
        soloTable.setValueAt(null, i, REVERSE_COLUMN);
        soloTable.setValueAt(null, i, EXPAND_COLUMN);
        soloTable.setValueAt(null, i, SIDESLIP_COLUMN);
        soloTable.setValueAt(null, i, BARLINESHIFT_COLUMN);
      }

    fireTableDataChanged();
    themeUsageTextArea.setText(null);
  }

public void tableReset()
  {
       tableRefresh();
  }

@Override
public int getColumnCount()
  {
    return columnCount;
  }

public void addARow()
  {
    soloTableModel.insertRow(0, new Object[]
      {
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
      });
  }

@Override
public Class getColumnClass(int column)
  {
    switch( column )
      {
        case 0:
            return Object.class;
      }
    return Object.class;
  }

@Override
public void setValueAt(Object value, int row, int col)
  {
    super.setValueAt(value, row, col);
    if( themeList.hasFocus() )
      {
        return;
      }

    switch( col )
      {
        //for naming a theme
        case NAME_COLUMN:
            if( soloTable.isCellSelected(row, NAME_COLUMN)
                    && (getValueAt(row, THEME_COLUMN) != null)
                    && (getValueAt(row, NAME_COLUMN) != null) )
              {
                namingSaving(row, col, row);
              }
            break;

        case LENGTH_COLUMN:
            break;

        //for editing a theme
        case THEME_COLUMN:
            if( (soloTable.isCellSelected(row, THEME_COLUMN))
                    && (getValueAt(row, THEME_COLUMN) != null)
                    && (getValueAt(row, LENGTH_COLUMN) != null) )
              {
                updateLength(row, col, row);
                return;
              }

            //for typing in own theme
            if( (soloTable.isCellSelected(row, THEME_COLUMN))
                    && (getValueAt(row, THEME_COLUMN) != null)
                    && (getValueAt(row, LENGTH_COLUMN) == null) )
              {
                addLength(row, col, row);
              }
            break;
      }
  }

}

public void setValueAt(Object value, int row, int col)
  {
    soloTable.setValueAt(value, row, col);
    if( !themeList.hasFocus() )
      {
        enteringValue(row, col);
      }
  }

public Object getValueAt(int row, int col)
  {
    return soloTable.getValueAt(row, col);
  }

//if a theme is edited 
public void updateLength(int row, int col, int i)
  {
    MelodyPart melody = new MelodyPart((String) getValueAt(i, THEME_COLUMN));

    int themelength = melody.size() / BEAT;

    if( themelength != (Integer) getValueAt(i, LENGTH_COLUMN) )
      {
        //if the lengths are different
        soloTable.setValueAt(themelength + "", i, LENGTH_COLUMN);

        for( Map.Entry pair : allThemes.entrySet() )
          {
            //loop through all the entry sets of {Theme,name} in allThemes
            Theme ThemeKey = (Theme) pair.getKey();
            //get the Theme of each entry

            if( melody.toString().equals(ThemeKey.melody.toString()) )
              {
                //if the melody in allThemes is the name as the melody in the table
                soloTable.setValueAt(pair.getValue(), i, NAME_COLUMN);
                //set the name to the one that matches that theme
              }
            else
              {// if there is no matching theme in allThemes
                soloTable.setValueAt(null, i, NAME_COLUMN);
                //set the name to empty
              }
          }
      }
  }

//if a theme is typed in 
public void addLength(int row, int col, int i)
  {
    MelodyPart melody = new MelodyPart((String) getValueAt(i, THEME_COLUMN));

    int themelength = melody.size() / BEAT;

    if( (getValueAt(i, NAME_COLUMN) == null) )
      { //if there is no name
        soloTable.setValueAt(themelength + "", i, LENGTH_COLUMN);
        //set themelength in the table
      }
    else
      { //if there is already a name
        soloTable.setValueAt(themelength + "", i, LENGTH_COLUMN);
        for( Map.Entry pair : allThemes.entrySet() )
          {
            //loop through all the entry sets of {Theme,name} in allThemes
            Theme ThemeKey = (Theme) pair.getKey();
            //get the Theme of each entry

            if( melody.toString().equals(ThemeKey.melody.toString()) )
              {
                //if the melody in allThemes is the name as the melody in the table
                soloTable.setValueAt(pair.getValue(), i, NAME_COLUMN);
                //set the name to the one that matches that theme
              }
            else
              {// if there is no matching theme in allThemes
                soloTable.setValueAt(null, i, NAME_COLUMN);
                //set the name to empty
              }
          }
      }
  }

//when a theme is named
public void namingSaving(int row, int col, int i)
  {
    MelodyPart melody = new MelodyPart((String) getValueAt(i, THEME_COLUMN));

    int themelength = melody.size() / BEAT;
    String name = (String) getValueAt(i, NAME_COLUMN);
    String themestring = (String) getValueAt(i, THEME_COLUMN);
    MelodyPart themeMelody = new MelodyPart(themestring);
    Theme theme = Theme.makeTheme(name.trim(), themeMelody);

    if( orderedThemes.contains(name) )
      { //if the user types a name already in the list
        nameErrorMessage.setVisible(true);
        //give name error message to rename the theme
      }
    else
      {
        if( !name.trim().isEmpty() )
          { //if the name is not whitespace

            for( Map.Entry pair : allThemes.entrySet().toArray(new Map.Entry[0]) )
              {
                // loop through the entries of allThemes
                Theme ThemeKey = (Theme) pair.getKey();
                //get the Theme of each entry
                String nameValue = (String) pair.getValue();
                //get the name of each entry

                if( melody.toString().equals(ThemeKey.melody.toString()) )
                  {
                    deleteTheme(nameValue); //delete the old theme
                    addTheme(theme); //add the new one

                    saveRules(fileName);
                    themeListModel.reset();

                    nameErrorMessage.setVisible(false);
                    break;
                  }
                else
                  { //if there is no melody that matches the one in the table
                    addTheme(theme);
                    saveRules(fileName);
                  }
              }
          }
      }
  }

public void enteringValue(int row, int col)
  {
    for( int i = 0; i < soloTable.getRowCount(); i++ )
      { //loop through table
        //updating length
        if( (soloTable.isCellSelected(i, THEME_COLUMN))
                && (getValueAt(i, THEME_COLUMN) != null)
                && (getValueAt(i, LENGTH_COLUMN) != null) )
          {
            updateLength(row, col, i);
            return;
          }
        //for typing in own theme
        if( (soloTable.isCellSelected(i, THEME_COLUMN))
                && (getValueAt(i, THEME_COLUMN) != null)
                && (getValueAt(i, LENGTH_COLUMN) == null) )
          {
            addLength(row, col, i);
            return;
          }

        //naming and saving
        if( soloTable.isCellSelected(i, NAME_COLUMN)
                && (getValueAt(i, THEME_COLUMN) != null)
                && (getValueAt(i, NAME_COLUMN) != null) )
          { //if name cell is selected, it's not empty and the theme isn't empty
            namingSaving(row, col, i);
            return;
          }
      }
  }

private static LinkedHashMap<Theme, String> allThemes = new LinkedHashMap<Theme, String>();
private static ArrayList<String> orderedThemes = null;
int allThemesIndex = allThemes.size() - 1;

public static int numberOfThemes()
  {
    ensureThemeArray();
    return orderedThemes.size();
  }

public static void setTheme(String name, Theme theme)
  {
    allThemes.put(theme, name);
  }

public static String getNth(int index)
  {
    ensureThemeArray();
    return orderedThemes.get(index);
  }

private static void ensureThemeArray()
  {
    orderedThemes = new ArrayList<String>(allThemes.values());
  }

public void addTheme(Theme theme)
  {
    ensureThemeArray();
    int orderedThemesIndex = orderedThemes.size() - 1;
    String name = theme.name;

    for( int i = 0; i < soloTable.getRowCount(); i++ )
      {
        if( (!orderedThemes.contains(name)) )
          {
            // if ordered themes doesn't already have it, add to both 
            //orderedThemes and allThemes
            orderedThemes.add(name);
            allThemes.put(theme, name);
          }
        // reset themeListModel so it will update to add the new theme
        themeListModel.reset();
        orderedThemesIndex = orderedThemes.indexOf(theme);
      }
  }

//delete a theme from a file based on the string name shown in the Themes scroll box
public void deleteTheme(String name)
  {
    orderedThemes.remove(name);

    for( Map.Entry pair : allThemes.entrySet() )
    {
        if( name.equals(pair.getValue()) )
        {
            //if the name in the themeList is equal to the name in the entry
            Theme theme = (Theme) pair.getKey();
            allThemes.remove(theme);
            break;
        }
    }
    saveRules(fileName);
    themeListModel.reset();
  }

//saving themes into My.themes
public void saveRules(File file)
  {
    try
      {
        java.io.PrintStream out = new PrintStream(new FileOutputStream(file));

        for( Map.Entry pair : allThemes.entrySet() )
          {
            Theme key = (Theme) pair.getKey();
            //key.showForm(System.out);
            key.showForm(out);
          }
      }
    catch( IOException e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Saving themes in file failed: " + file);
      }
  }

//load the themes in My.themes into the Theme Weaver window
public void loadFromFile(File file)
  {
    java.io.FileInputStream themeStream;

    try
      {
        themeStream = new FileInputStream(file);
      }
    catch( Exception e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Loading themes in file failed: " + file);
        return;
      }

    Tokenizer in = new Tokenizer(themeStream);
    Object ob;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
        if( ob instanceof Polylist )
          {
            Polylist themePoly = (Polylist) ob;
            Theme theme = new Theme(themePoly);
            addTheme(theme);
            //System.out.println("adding " + theme);
            //System.out.println(orderedThemes);
          }
      }
  }

public class ThemeListModel
        extends AbstractListModel
{

public int getSize()
  {
    int number = numberOfThemes();

    return number;

  }

public Object getElementAt(int index)
  {
    return getNth(index);
  }

public void reset()
  {
    fireContentsChanged(this, 0, getSize());
  }

public void adjust()
  {
    fireIntervalAdded(this, 0, getSize());
  }
}

public static boolean isInteger(String s)
  {
    try
      {
        Integer.parseInt(s);
      }
    catch( NumberFormatException e )
      {
        return false;
      }
    return true;
  }

public static boolean isDouble(String s)
  {
    try
      {
        Double.parseDouble(s);
      }
    catch( NumberFormatException e )
      {
        return false;
      }
    return true;
  }

/**
 * This fillMelody is called in three places within ThemeWeaver.
 *
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

//work in progress
public void ExpandCommand(Polylist list)
  {
    Polylist melodyList = (Polylist) list.last(); //get polylist of the melody
    Polylist melodyNotes = melodyList.rest(); //get the notes in a polylist

    PolylistEnum melodyElements = melodyNotes.elements(); //get the notes as elements

    //To get the notes of the theme in a string:
    String melodyString = "";
    while( melodyElements.hasMoreElements() )
      { //while there are more notes
        Object current = melodyElements.nextElement();//get next note
        String currentString = current.toString(); //convert it to String
        if( currentString.length() == 2 )
          {
            int intValue = Integer.parseInt(currentString.charAt(1) + "");
            int newValue = intValue / 2;
            //System.out.println(newValue);
            //System.out.println(currentString.charAt(0));
            String newNote = currentString.charAt(0) + newValue + "";
            // System.out.println(newNote);
            melodyString += newNote + " "; //add the note to the melodyString
          }
        else if( currentString.length() == 3 )
          {
            int newValue = currentString.charAt(2) / 2;
            String newNote = currentString.charAt(0) + currentString.charAt(1) + newValue + "";
            melodyString += newNote + " "; //add the note to the melodyString
          }
        else
          {
            int newValue = currentString.charAt(3) / 2;
            String newNote = currentString.charAt(0) + currentString.charAt(1) + currentString.charAt(2) + newValue + "";
            melodyString += newNote + " "; //add the note to the melodyString
          }
      }
    //System.out.println(melodyString);
    MelodyPart melody = new MelodyPart(melodyString); //create a MelodyPart of the string
  }

public void generateTheme()
  {
    for( int x = 0; x < soloTable.getRowCount(); x++ )
      { //loop through the rows of the table
        if( (getValueAt(x, LENGTH_COLUMN) != null)
                && (!isInteger((String) getValueAt(x, LENGTH_COLUMN))) )
          {
            enteredIncorrectly.setVisible(true);
          }
        else
          {
            if( ((soloTable.isCellSelected(x, LENGTH_COLUMN))
                    && (getValueAt(x, LENGTH_COLUMN) != null))
                    || ((getValueAt(x, LENGTH_COLUMN) != null)
                     && (getValueAt(x, THEME_COLUMN) == null)) )
              {
                //if the theme length cell is selected and has something in it 
                // or if there is something in a length cell and has no theme

                //set default values for weighted values
                soloTable.setValueAt(USE_DEFAULT_VALUE,       x, USE_COLUMN);
                soloTable.setValueAt(TRANSPOSE_DEFAULT_VALUE, x, TRANSPOSE_COLUMN);
                soloTable.setValueAt(INVERT_DEFAULT_VALUE,    x, INVERT_COLUMN);
                soloTable.setValueAt(REVERSE_DEFAULT_VALUE,   x, REVERSE_COLUMN);
                soloTable.setValueAt(EXPAND_DEFAULT_VALUE,    x, EXPAND_COLUMN);
                soloTable.setValueAt(SIDESLIP_DEFAULT_VALUE,    x, SIDESLIP_COLUMN);
                soloTable.setValueAt(BARLINESHIFT_DEFAULT_VALUE,    x, BARLINESHIFT_COLUMN);

                int Length = notate.intFromStringInRange((String) getValueAt(x, LENGTH_COLUMN), 0, 100, themeLength);
                //get length from table
                themeLength = BEAT * Length;
                Polylist rhythm = lickgen.generateRhythmFromGrammar(0, themeLength);
                //get rhythm for theme from grammar

                MelodyPart lick = fillMelody(BEAT, rhythm, notate.getChordProg(), 0);
                //get the melody of the theme

                Part.PartIterator i = lick.iterator(); //iterate over lick
                String theme = ""; //set theme as empty to start

                while( i.hasNext() ) //while you can still iterate through the lick
                  {
                    Unit unit = i.next();
                    if( unit != null ) //if next isn't empty
                      {
                        theme += unit.toLeadsheet() + " "; //add it to the theme
                      }
                  }

                soloTable.setValueAt(theme, x, THEME_COLUMN);
                //make theme appear in table 

                if( getValueAt(x, NAME_COLUMN) != null )
                  {
                    soloTable.setValueAt(null, x, NAME_COLUMN);
                  }
              }
          }
      }
  }

public MelodyPart generateSolohelper(ThemeUse chosenthemeUse, MelodyPart chosentheme, MelodyPart solo, CommandManager cm)
        //adjusts chosentheme (not currently used)
  {
    int length = chosentheme.size(); // get length of theme
    MelodyPart adjustedTheme = chosentheme.copy(); //made a copy of the theme
    imp.ImproVisor.setPlayEntrySounds(false); //don't play yet
    int unmodified = 0;
    int multipleuse = 0;

    if( Notate.bernoulli(chosenthemeUse.probTranspose) )
      {
        // if a random number is greater than the probability not to transpose theme
        multipleuse += 1;
        //System.out.println("Transpose");
        themeUsageTextArea.append("transposed");
        ChordPart chordProg = notate.getChordProg(); //get current chord progression
        int rise = PitchClass.findRise(PitchClass.getPitchClass(chordProg.getCurrentChord(0).getRoot()),
                                       PitchClass.getPitchClass(chordProg.getCurrentChord(length).getRoot()));
        //set rise equal to the rise of semitones from the root of the 0 chord to i chord
        int index = 0;
        Note n = adjustedTheme.getNote(index); //get the note of the theme at index 0

        while( n.isRest() )
          { //while there is still theme left
            index += n.getRhythmValue(); //add the rhythm value of the theme to the index
            n = adjustedTheme.getNote(index); //get the note of the theme at new index
          }

        if( n.getPitch() >= (minPitch + maxPitch) / 2 && rise > 0 )
          { // if pitch of theme is greater than or equal to the average pitch and change in semitones increased
            cm.execute(new ShiftPitchesCommand(-1 * (12 - rise), adjustedTheme,
                                               0, length, 0, 128, notate.getScore().getKeySignature()));
          } //shift theme pitches down an octave + rise from 0 to the end of the theme
        else if( n.getPitch() < (minPitch + maxPitch) / 2 && rise < 0 )
          { //if pitch of theme is less than the average pitch and change in semitones increased
            cm.execute(new ShiftPitchesCommand((12 + rise), adjustedTheme,
                                               0, length, 0, 128, notate.getScore().getKeySignature()));
          } //shift theme pitches up an octave + rise from 0 to end of theme
        else
          {
            cm.execute(new ShiftPitchesCommand(rise, adjustedTheme, 0, length, 0, 128, notate.getScore().getKeySignature()));
            
          }
        //shift theme pitches by the rise in semitones
      }
    else
      {
        unmodified += 1;
      }

    if( Notate.bernoulli(chosenthemeUse.probInvert) )
      { // if a random number is greater than the probability not to invert the theme
        multipleuse += 1;
        if( multipleuse == 2 )
          {
            themeUsageTextArea.append(", and inverted");
          }
        //System.out.println("Invert");
        if( !(multipleuse == 2) )
          {
            themeUsageTextArea.append("inverted");
          }
        cm.execute(new InvertCommand(adjustedTheme, 0, length, false)); //invert theme
      }
    else
      {
        unmodified += 1;
      }

    if( Notate.bernoulli(chosenthemeUse.probReverse) )
      {
        // if a random number is greater than the probability not to reverse the theme
        multipleuse += 1;
        //System.out.println("Reverse");

        if( multipleuse == 1 )
          {
            themeUsageTextArea.append("reversed");
          }

        if( multipleuse != 1 )
          {
            themeUsageTextArea.append(", and reversed");
          }

        cm.execute(new ReverseCommand(adjustedTheme, 0, length, false)); //reverse theme
      }
    else
      {
        unmodified += 1;
      }

    if( unmodified == 3 )
      {
        themeUsageTextArea.append(" unmodified");
      }

    themeUsageTextArea.append("\n");
    ChordPart themeChords = notate.getChordProg().extract(length, length + adjustedTheme.getSize());
    //set chords of theme to be the chordpart extracted from length to length +length
    cm.execute(new RectifyPitchesCommand(adjustedTheme, 0, length, themeChords, false, false));
    //resolve pitches of the theme
    solo.setSize(solo.getSize() + length);
    //set size of solo to the existing length of the solo plus the length of the theme
    return adjustedTheme;
  }

public void generateSolohelper2(int themeLength, MelodyPart solo)
  {//(not currently used)
    Polylist rhythm = lickgen.generateRhythmFromGrammar(0, themeLength);
    //generate rhythm 

    MelodyPart lick = fillMelody(BEAT, rhythm, notate.getChordProg(), 0);
    //create melody


    Part.PartIterator j = lick.iterator(); //iterate over lick
    lick.setSize(themeLength);
    while( j.hasNext() ) //while any lick is left
      {
        Unit unit = j.next();
        if( unit != null ) //if next is not empty
          {
            solo.addNote(NoteSymbol.toNote(unit.toLeadsheet()));
            //add the unit to the solo
          }
      }
    
  }

public void generateSolo(ArrayList<ThemeUse> themeUses, CommandManager cm)
  {//not currently used
    themeUsageTextArea.setText(null);
    //System.out.println(themeUses);
    // create four empty lists to start for all the probabilities
    List<Double> probUselist = new ArrayList(Arrays.asList());
    List<Double> probTransposelist = new ArrayList(Arrays.asList());
    List<Double> probInvertlist = new ArrayList(Arrays.asList());
    List<Double> probReverselist = new ArrayList(Arrays.asList());

    int n = 0;

    //loop through the themeUses list and get the probabilities for each
    //themeuse and add it to the corresponding empty list
    for( int i = 0; i < themeUses.size(); i++ )
      {
        probUselist.add(themeUses.get(i).probUse);
        probTransposelist.add(themeUses.get(i).probTranspose);
        probInvertlist.add(themeUses.get(i).probInvert);
        probReverselist.add(themeUses.get(i).probReverse);

        //give names to themes that don't have one for text area
        if( themeUses.get(i).theme.name == null )
          {
            n += 1;
            themeUses.get(i).theme.name = "Theme " + n;
          }
      }


    // find max of probability use to use that corresponding theme first
    double max = Collections.max(probUselist);
    int index = probUselist.indexOf(max); //get the index of the max
    int length = themeUses.get(index).theme.melody.getSize();
    //get length of theme
    themeLength = length; //set themeLength to it
    MelodyPart solo = new MelodyPart(themeLength);
    //set solo equal to a MelodyPart of the theme length

    imp.ImproVisor.setPlayEntrySounds(false); //don't play insertions yet

    solo.pasteSlots(themeUses.get(index).theme.melody, 0);
    //paste theme into solo at starting point

    themeUsageTextArea.append("Bar 1: " + themeUses.get(index).theme.name + " unmodified \n");

    // set totals of probabilities to 0
    int probUsetotal = 0;
    int probTransposetotal = 0;
    int probInverttotal = 0;
    int probReversetotal = 0;

    //loop through each respective list and add elements together to get 
    //the total of each probability
    // multiply each element by 10 so its an integer
    for( int i = 0; i < themeUses.size(); i++ )
      {
        probUsetotal += 10 * probUselist.get(i);
        probTransposetotal += 10 * probTransposelist.get(i);
        probInverttotal += 10 * probInvertlist.get(i);
        probReversetotal += 10 * probReverselist.get(i);
      }

    //use Theme Use Interval from text field
    int themeInterval = new Integer(themeIntervalTextField.getText());
    //int themeIntervalUse = themeInterval * 120;//comment 6.4.15: this is assuming 120 bpm
    int tempo = (int)notate.getTempo();
    int themeIntervalUse = themeInterval * tempo;//change made 6.4.15

    for( int i = length; i <= notate.getScoreLength() - themeIntervalUse; i += themeIntervalUse )
      {
        //loop through the remaining length of the score
        int beat = i / tempo;//this is the beat number that we're on
        int bar = 1 + (beat / notate.getBeatsPerMeasure());//this is the bar number we're on
        Integer noThemevalue = (int) (10 * themeUses.size() * (1.0-Double.valueOf(themeProbTextField.getText())));

        int themei = random.nextInt(probUsetotal + noThemevalue);
        //System.out.println(themei); 
        //pick a random number from 0 inclusive to 10*the probability list size
        //since all the elements in the list are multpled by 10, the size has to be multiplied by 10 too


        //To implement the probabilities I broke up the size of the list times 10 into intervals
        //the first interval is from 0 to to the first probability - 1 
        //so that way the number of slots in that interval is equal
        //to that first probability times 10
        //so if the random number chosen is in that interval, then that first theme is used
        if( themei <= 10 * probUselist.get(0) - 1 )
          {
            //System.out.println("Theme 1");
            MelodyPart chosentheme = themeUses.get(0).theme.melody;
            ThemeUse chosenthemeUse = themeUses.get(0);
            themeUsageTextArea.append("Bar " + bar + ": " + chosenthemeUse.theme.name);
            MelodyPart adjustedTheme = generateSolohelper(chosenthemeUse, chosentheme, solo, cm);

            //this if takes care of the case if the index is out of bounds
            if( i + adjustedTheme.size() >= solo.getSize() )
              {
                generateSolohelper2(themeLength, solo);
              }
            else
              {
                // if there is no index out of bounds issue, then add the adjusted theme into the solo
                solo.pasteSlots(adjustedTheme, i);
              }
          }
        //if the themeUses size is more than one themeuse then the other intervals have to be accounted for 
        else if( themeUses.size() > 1 )
          {
            double A = 0;
            double B = 10 * probUselist.get(0);

            //this loop covers the rest of the intervals
            for( int k = 0; k < probUselist.size() - 1; k++ )
              {
                A += 10 * probUselist.get(k);
                B += 10 * probUselist.get(k + 1);

                if( (themei >= A) && (themei <= B - 1) )
                  {
                    //System.out.println("Theme " + x);
                    ThemeUse chosenthemeUse = themeUses.get(k + 1);
                    MelodyPart chosentheme = themeUses.get(k + 1).theme.melody;
                    themeUsageTextArea.append("Bar " + bar + ": " + chosenthemeUse.theme.name);
                    MelodyPart adjustedTheme = generateSolohelper(chosenthemeUse, chosentheme, solo, cm);

                    if( i + adjustedTheme.size() >= solo.getSize() )
                      {
                        generateSolohelper2(themeLength, solo);

                      }
                    else
                      {
                        solo.pasteSlots(adjustedTheme, i);
                      }
                  }
              }
          }

        //this interval is for not using any theme at all    
        if( (themei <= probUsetotal + noThemevalue) && (themei >= probUsetotal) )
          {
            //System.out.println("no Theme");
            themeUsageTextArea.append("Bar " + bar + ": Used grammar\n");
            generateSolohelper2(themeLength, solo);
          }
      }


    if( notate.getScore().getLength() - solo.getSize() != 0 )
      {//if the length of the score does not equal the length of the solo
       //finishes the solo
        Polylist rhythm = lickgen.generateRhythmFromGrammar(0, notate.getScore().getLength() - solo.getSize());
        // generate rhythm for the space

        MelodyPart lick = fillMelody(BEAT, rhythm, notate.getChordProg(), 0);
        //create melody for the space

        Part.PartIterator j = lick.iterator(); //iterate over the lick
        while( j.hasNext() ) //while lick has next
          {
            solo.addNote(NoteSymbol.toNote(j.next().toLeadsheet()));
            //add the note of the lick to the solo
          }
      }
    notate.setCurrentSelectionStart(0); //start selection at beginning

    //Resolve pitches in entire solo
    cm.execute(new RectifyPitchesCommand(solo, 0, solo.getSize(), notate.getChordProg(), false, false));

    notate.pasteMelody(solo); //paste solo into leadsheet

    imp.ImproVisor.setPlayEntrySounds(true); //play solo
  }

private void setSliderLabels(javax.swing.JSlider slider)
{//used to set the labels on the pitch range sliders
    slider.setPaintLabels(true);
    
    Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
    table.put (40, new JLabel(("E2")));
    table.put (50, new JLabel("D3"));
    table.put (60, new JLabel("C4"));
    table.put (70, new JLabel("Bb4"));
    table.put (80, new JLabel("Ab5"));
    table.put (90, new JLabel("Gb6"));
    table.put (100, new JLabel("E7"));
    Font f = table.get(40).getFont();
    for (int i = 1; i < 8; i++)
    {
        Component c = table.get(i*10+30);
        c.setFont(new Font(f.getName(), f.getStyle(), 10));
    }
    slider.setLabelTable (table);
}

public void myGenerateSolo(ArrayList<ThemeUse> themeUses, CommandManager cm)
{//generates a solo using the themes chosen by the user
    
    themeUsageTextArea.setText(null);
    
    probTheme = Double.parseDouble(themeProbTextField.getText());//probability of not using a theme at all 
    MelodyPart solo = new MelodyPart(0);//new melody part of size 0
    
    imp.ImproVisor.setPlayEntrySounds(false); //don't play insertions yet
    
    //use Theme Use Interval from text field
    int themeInterval = new Integer(themeIntervalTextField.getText());
    
    int slots = 120;//number of slots in a beat
    
    int themeIntervalUseIncrement = themeInterval * slots; //amount by which we'll increment over the score
    
    int i=0;
    
    MelodyPart newMelody = new MelodyPart();
    
    MelodyPart prevSection = new MelodyPart();
    MelodyPart nextSection = new MelodyPart();
    
    while (i<notate.getScoreLength())
    {//increment through and fill the whole score 
        prevSection = newMelody;
        
        themeInterval = new Integer(themeIntervalTextField.getText());
        double randProbNT = random.nextDouble();//random number used for no-theme
        
        int beat = i / slots;//this is the beat number that we're on
        int bar = 1 + (beat / notate.getBeatsPerMeasure());//this is the bar number we're on
        
        newMelody = new MelodyPart();//this will be added to the solo eventually
        
        int increaseIncrement = 0;//keeps track of how i should change depending on how newMelody is changed 
                                        //and how the solo size should change
        
        if (probTheme > randProbNT)//if we get to use a theme!
        {
           
            //choose a theme
            //normalize use probabilities
            double sumUseProb=0;
            for (ThemeUse themeUse : themeUses) 
            {
                sumUseProb += themeUse.getProbUse();
            }
            double randProbUse = random.nextDouble();//random number used for use

            //defaults chosen theme as last in array list (changes it later
            //                                                      if necessary)
            //uses probability to choose a theme in themeUses
            ThemeUse chosenThemeUse=themeUses.get(themeUses.size()-1);
            double tempTotalProb=0;
            for (ThemeUse themeUse : themeUses) 
            {
                tempTotalProb += themeUse.getProbUse()/sumUseProb;
                if (tempTotalProb > randProbUse) 
                {
                    chosenThemeUse = themeUse;
                    break;
                }
            }

            MelodyPart chosenTheme = chosenThemeUse.getTheme().melody;//randomly chosen theme's melody
            int originalSize = chosenTheme.getSize();//keeps the original size in case the size is changed later
            
            newMelody.setSize(chosenTheme.getSize());
            //change theme according to given probabilities for transpose, invert, reverse, expand, side slip, and bar line shift
            newMelody = adjustTheme(chosenTheme, chosenThemeUse, cm, themeInterval);//adjustTheme is a helper method
          
            if (newMelody.getSize()+i > notate.getScoreLength())
            {//if it doesn't fit between the current position and the end, stop early
                newMelody = new MelodyPart(0);
                endSoloEarly = true;
            }
            
            else if(newMelody.getSize() > themeIntervalUseIncrement)
            {//if the theme doesn't fit in the allotted interval 
                //reset solo size so it fits and adjust increment approprately
                themeUsageTextArea.append("Bar " + bar + ": " + chosenThemeUse.theme.name + ": ");
                themeUsageTextArea.append(themeUsageText + "\n");
                double numIncrements = newMelody.getSize()*1.0/themeIntervalUseIncrement ;
               
                increaseIncrement += themeIntervalUseIncrement*numIncrements;
       
            }
            else
            {//it fits properly
                themeUsageTextArea.append("Bar " + bar + ": " + chosenThemeUse.theme.name + ": ");
                themeUsageTextArea.append(themeUsageText + "\n");
                
                increaseIncrement += themeIntervalUseIncrement;
            }
            
            themeUsageText = "";
          
            chosenTheme.setSize(originalSize);
            
        }   
        else
        {
            //doesn't use a theme, use grammar
            themeUsageTextArea.append("Bar " + bar + ": Generated Melody\n");
            //set size of solo to the existing length of the solo plus the length of the theme
            int min =minPitch;
            int max = maxPitch;
            if (prevSection.getSize()!=0)
            {
                Note lastNote = prevSection.getLastNote();
                int lastPitch = lastNote.getPitch();
            
                min=lastPitch-6;
                max=lastPitch+6;//the 6 is kind of arbitrary
            }
            if (min < minPitch)
            {
                min = minPitch;
            }
            if (max > maxPitch)
            {
                max = maxPitch;
            }
            newMelody = generateFromGrammar(themeIntervalUseIncrement, solo, i, min, max); 
            increaseIncrement += themeIntervalUseIncrement;
        }
        
        int noteDiff = highestNote(newMelody) - lowestNote(newMelody);//range size of newMelody
        int chosenRangeDiff = maxPitch - minPitch;//user entered range size
        if (noteDiff > chosenRangeDiff)//if the chosen theme won't fit in the chosen range
        {//show error and stop generating solo
            rangeTooSmall.setVisible(true);
            stopPlaying();
            return;
        }
        
        if (!inRange(newMelody))//if newMelody isn't in the given range, transpose until it is
        {
            newMelody = adjustToFit(newMelody);
            themeUsageText = "";
        }
        nextSection = newMelody;
        MelodyPart addToSolo = connectSections(prevSection, nextSection);
        solo.setSize(solo.getSize() + increaseIncrement);//increase the solo size appropriately
        solo.pasteSlots(addToSolo, i-prevSection.getSize());//add newMelody to the solo
        i+=increaseIncrement;//and increment
        if (endSoloEarly)
        {
            i = notate.getScoreLength();
            endSoloEarly = false;
        }
        
    }
    solo.setSize(notate.getScoreLength());
    notate.setCurrentSelectionStart(0); //start selection at beginning
    //rectify pitches in entire solo
    cm.execute(new RectifyPitchesCommand(solo, 0, solo.getSize(), notate.getChordProg(), false, false, true, false, false));
    notate.pasteMelody(solo); //paste solo into leadsheet
    imp.ImproVisor.setPlayEntrySounds(true); //play solo
}

private MelodyPart connectSections(MelodyPart previous, MelodyPart next)
{// makes a smoother connection between previous and next
    MelodyPart solo = new MelodyPart();
    ArrayList<Note> previousNotes1 = previous.getNoteList();
    ArrayList<Note> nextNotes1 = next.getNoteList();
    ArrayList<Integer> prevNotesMidi1 = new ArrayList<Integer>();
    ArrayList<Integer> nextNotesMidi1 = new ArrayList<Integer>();
    
    for (int i=0; i<previousNotes1.size(); i++)
    {//get all the notes in the previous melody, add to array list
        prevNotesMidi1.add(previousNotes1.get(i).getPitch());
    }
    for (int i=0; i<nextNotes1.size(); i++)
    {//get all the notes in the next melody, add to array list
        nextNotesMidi1.add(nextNotes1.get(i).getPitch());
    }
    if (previous.getSize() != 0 && next.getSize() != 0)
    {
        Note prevNote = previous.getLastNote();
        Note nextNote = next.getFirstNote();
        if (prevNote != null && nextNote != null)
        {//check the pitches
            int prevPitch = prevNote.getPitch();
            int nextPitch = nextNote.getPitch();
            //System.out.println(prevPitch + " "+ nextPitch);
            int difference = Math.abs(prevPitch - nextPitch);
            if (difference >= 4)
            {
                /*int movePrevBy;
                int moveNextBy;
                if (prevPitch>nextPitch)
                {
                    movePrevBy = -1*difference/4;
                    moveNextBy = difference/4;
                }
                else
                {
                    movePrevBy = difference/4;
                    moveNextBy = -1*difference/4;
                }
                Note newPrev = new Note(prevPitch);
                newPrev.shiftPitch(movePrevBy, notate.getScore().getKeySignature());
                Note newNext = new Note(nextPitch);
                newNext.shiftPitch(moveNextBy, notate.getScore().getKeySignature());
                previous.setNote(getLastNoteIndex(previous), newPrev);
                next.setNote(next.getFirstIndex(), newNext);*/
                
                
                //make array lists of the note indices
                ArrayList<Integer> previousIndices = new ArrayList<Integer>();
                if (previous.getNote(previous.getFirstIndex()) != null && !previous.getNote(previous.getFirstIndex()).isRest());
                {
                    previousIndices.add(previous.getFirstIndex());
                }
                int prevInitialSize = previousIndices.size();
                
                //get the indices of the each note
                for (int i=prevInitialSize; i<previous.size(); i++)
                {
                    int nextIndex = previous.getNextIndex(i);
                    if (previous.getNote(nextIndex)!= null && !previous.getNote(nextIndex).isRest())
                    {
                        previousIndices.add(nextIndex);
                        i=nextIndex;
                    }
                }
                ArrayList<Integer> nextIndices = new ArrayList<Integer>();
                if (next.getNote(next.getFirstIndex()) != null && !next.getNote(next.getFirstIndex()).isRest());
                {
                    nextIndices.add(next.getFirstIndex());
                }
                int nextInitialSize = nextIndices.size();
                for (int i=nextInitialSize; i<previous.size(); i++)
                {
                    int nextIndex = next.getNextIndex(i);
                    if (next.getNote(nextIndex)!= null && !next.getNote(nextIndex).isRest())
                    {
                        nextIndices.add(nextIndex);
                        i=nextIndex;
                    }
                }
                
                //System.out.println("prev indices*** " + previousIndices.toString());
                //System.out.println("next indices*** " + nextIndices.toString());
                
                
                int div1 = 4;//the amount by which we'll split the difference
                int nextMoveBy = difference/div1;
                int div2 = 4;
                int prevMoveBy = difference/div2;
                if (prevPitch>nextPitch)
                {
                    prevMoveBy = -1*difference/div2;
                    nextMoveBy = difference/div1;
                }
                else
                {
                    prevMoveBy = difference/div2;
                    nextMoveBy = -1*difference/div1;
                }
                
                for (int i=0; i<nextIndices.size(); i++)
                {//shift the pitch appropriately and decrease the amount by which you shift
                    if (div1 <= Math.abs(difference))
                    {//move note by moveBy 
                        next.getNote(nextIndices.get(i)).shiftPitch(nextMoveBy, notate.getScore().getKeySignature());
                        
                        next.setNote(nextIndices.get(i), next.getNote(nextIndices.get(i)));
                        
                        
                        div1+=2;
                        nextMoveBy = difference/div1 ;
                    }
                    else
                    {
                        break;
                    }
                }
                for (int i=previousIndices.size()-1; i>=0; i--)
                {
                    if (div2 <= Math.abs(difference))
                    {//move note by moveBy
                        previous.getNote(previousIndices.get(i)).shiftPitch(prevMoveBy, notate.getScore().getKeySignature());
                        previous.setNote(previousIndices.get(i), previous.getNote(previousIndices.get(i)));
                        
                        
                        div1+=2;
                        prevMoveBy = difference/div2 ;
                    }
                    else
                    {
                        break;
                    }
                }
                
            }
        }
    }
    //System.out.println("2 "+previous.toString());
    //System.out.println("2 "+next.toString());
    solo = new MelodyPart(previous.getSize() + next.getSize());
    ArrayList<Note> previousNotes = previous.getNoteList();
    ArrayList<Note> nextNotes = next.getNoteList();
    ArrayList<Integer> prevNotesMidi = new ArrayList<Integer>();
    ArrayList<Integer> nextNotesMidi = new ArrayList<Integer>();
    for (int i=0; i<previousNotes.size(); i++)
    {
        prevNotesMidi.add(previousNotes.get(i).getPitch());
    }
    for (int i=0; i<nextNotes.size(); i++)
    {
        nextNotesMidi.add(nextNotes.get(i).getPitch());
    }
    solo.pasteSlots(previous, 0);
    solo.pasteSlots(next, previous.getSize());
    return solo;
}

private int getLastNoteIndex(MelodyPart melody)
{//could probably put this in the MelodyPart class....
    int index = melody.getPrevIndex(melody.getSize());

    Note n = melody.getNote(index);

     while(n.isRest()) {
         index = melody.getPrevIndex(index);
         n = melody.getNote(index);
     }

    return index;
}

private MelodyPart adjustToFit(MelodyPart melody)
{//adjusts melody to fit in the given range
    
    MelodyPart copy = melody.copy();//copies melody
    int length = copy.getSize();
    
    while (true)
    {//loop until you find a transposition in range (it will break while loop
     //                                                        when this happens)
        int lowest = lowestNote(copy);//lowest note in copy
        int highest = highestNote(copy);//highest note in copy
        
        if (lowest<minPitch)
        {//transpose up by numWholeSteps
            cm.execute(new ShiftPitchesCommand(1, copy, 
                    0, length, 0, 128, notate.getScore().getKeySignature()));
        }
        
        else if (highest>maxPitch)
        {//transpose down by numWholeSteps
            cm.execute(new ShiftPitchesCommand(-1, copy, 
                    0, length, 0, 128, notate.getScore().getKeySignature()));
        }
        
        if (inRange(copy))
        {
            return copy;
        }
    }
}

//adjustTheme is a helper method for myGenerateSolo
public MelodyPart adjustTheme(MelodyPart chosenTheme, ThemeUse chosenThemeUse, CommandManager cm, int themeInterval)
{//adjust the chosen theme according to the probabilities of using certain transformations (entered by the user)
    MelodyPart adjustedMelody = chosenTheme.copy();
    int length = chosenTheme.size(); 
    
    imp.ImproVisor.setPlayEntrySounds(false); //don't play yet
    
    int numAdjustments = 0;//keeps track of how many transformations are used (used for the themeUsageText)
   
    Object trans1 = trans1ComboBox.getSelectedItem();
    Object trans2 = trans2ComboBox.getSelectedItem();
    Object trans3 = trans3ComboBox.getSelectedItem();
    Object trans4 = trans4ComboBox.getSelectedItem();
    Object trans5 = trans5ComboBox.getSelectedItem();
    Object trans6 = trans6ComboBox.getSelectedItem();
    Object[] transformOrder = new Object[6];
    transformOrder[0]=trans1;
    transformOrder[1]=trans2;
    transformOrder[2]=trans3;//organizes transformations in an array
    transformOrder[3]=trans4;
    transformOrder[4]=trans5;
    transformOrder[5]=trans6;
    
    if (trans1==null||trans2==null||trans3==null||trans4==null||trans5==null||trans6==null||transformDoubled(transformOrder))
    {// if anything isn't entered correctly (if something is null or a transformation is entered more than once)
        //use the default order
        transformOrder[0]="Transpose";
        transformOrder[1]="Invert";
        transformOrder[2]="Reverse";
        transformOrder[3]="Expand";
        transformOrder[4]="Side Slip";
        transformOrder[5]="Bar Line Shift";
    }
    int count = 0;
    while (count < 6)//loop until all of the transformations are reached
    {
        if (transformOrder[count]=="Transpose")
        {
            //decide whether to transpose or not
            if (Notate.bernoulli(chosenThemeUse.getProbTranspose()) ) 
            {    //if random number is > the probability of transposing
                //transpose
                MelodyPart transposedMelody = chosenTheme.copy();
                numAdjustments ++;
                if (numAdjustments > 1)
                {
                    themeUsageText+=", transposed";
                }
                else
                {
                    themeUsageText+="transposed";
                }
                adjustedMelody = transpose(transposedMelody,length);//transpose melody
            }
        }
        else if (transformOrder[count]=="Invert")
        {
            //decide whether to invert or not
            if (Notate.bernoulli(chosenThemeUse.getProbInvert()) )
                //if random number is > the probability of inverting
            {
                //invert
                numAdjustments++;
                if (numAdjustments > 1)
                {
                    themeUsageText+=", inverted";
                }
                else
                {
                    themeUsageText+="inverted";
                }
                cm.execute(new InvertCommand(adjustedMelody, 0, length, false));
            }
        }
        else if (transformOrder[count] == "Reverse")
        {
            //decide whether to reverse or not
            if (Notate.bernoulli(chosenThemeUse.getProbReverse()) )
                //if random number is > the probability of reversing
            {
                numAdjustments++;
                if (numAdjustments > 1)
                {
                    themeUsageText+=", reversed";
                }
                else
                {
                    themeUsageText+="reversed";
                }
                //reverse
                cm.execute(new ReverseCommand(adjustedMelody, 0, length, false));
            }
        }
        else if (transformOrder[count] == "Expand")
        {
            //decide whether to expand or not
            if (Notate.bernoulli(chosenThemeUse.getProbExpand()))
            {
                numAdjustments ++;
                double probExpandBy3 = probExpandby2or3.getValue()/100; 
                //decide whether to expand by 2 or 3
                if (Notate.bernoulli(probExpandBy3))
                {
                    if (numAdjustments > 1)
                    {
                        themeUsageText+=", expanded by 3";
                    }
                    else
                    {
                        themeUsageText+="expanded by 3";
                    }
                    //expand by 3
                    adjustedMelody = expandBy(adjustedMelody, length, 3);
                }
                else
                {
                    if (numAdjustments > 1)
                    {
                        themeUsageText+=", expanded by 2";
                    }
                    else
                    {
                        themeUsageText+="expanded by 2";
                    }
                    //expand by 2
                    adjustedMelody = expandBy(adjustedMelody, length, 2);
                }
            }
        }
        else if (transformOrder[count] == "Side Slip")
        {
            //decide whether to side slip or not
            if (Notate.bernoulli(chosenThemeUse.getProbSideslip()))
            {
                numAdjustments ++;
                sideslip = true;
                MelodyPart sideslippedMelody = adjustedMelody.copy();
                if (numAdjustments > 1)
                {
                    themeUsageText+=", side slip";
                }
                else
                {
                    themeUsageText+="side slip";
                }
                adjustedMelody = sideslip(sideslippedMelody, length);
          
            }
        }
        else if (transformOrder[count] == "Bar Line Shift")
        {
            if (Notate.bernoulli(chosenThemeUse.getProbBarLineShift()))
            {
                numAdjustments++;
                barlineshift = true;
                MelodyPart shiftedMelody = adjustedMelody.copy();
                if (numAdjustments > 1)
                {
                    themeUsageText += ", bar line shift";
                }
                else
                {
                    themeUsageText+= "bar line shift";
                }
                adjustedMelody = barLineShift(shiftedMelody, length);
               
            }
        }
        count++;
    }
    if (numAdjustments == 0)
    {
        themeUsageText+=" unmodified";
    }
    barlineshift = false;
    sideslip = false;
    return adjustedMelody;
}

private MelodyPart transpose(MelodyPart melody, int length)
{//transposes melody according to set probabilities
    MelodyPart transposedMelody = melody.copy();
    
    while (true)
    {//loop until you find a transposition in range (it will break while loop
     //                                                        when this happens)
        //System.out.println("try to transpose");
        int maxTransposeDist = (maxPitch + minPitch)/2; 
            //if pitch is exactly in the center or min and max pitch, 
                            //maximum number of steps to reach min and max pitch


        //decide to move by semitones or whole tone according to set probability
        if (Notate.bernoulli(probWholeToneTranspose))
        {//transpose by whole tones
            int numWholeSteps = random.nextInt(maxTransposeDist/2);

            int lowest = lowestNote(transposedMelody)-numWholeSteps*2;
            int highest = highestNote(transposedMelody)+numWholeSteps*2;

            if (Notate.bernoulli(.5))
            {//transpose down by numWholeSteps


                if (lowest >= minPitch)//make sure the new melody will be in range
                {
                    //transpose copy of chosen theme
                    cm.execute(new ShiftPitchesCommand(-1*numWholeSteps, transposedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
                    break;
                }
            }
            else
            {//transpose up by numWholeSteps

                if (highest <= maxPitch)//make sure the new melody will be in range
                {
                    //transpose copy of chosen theme
                    cm.execute(new ShiftPitchesCommand(numWholeSteps, transposedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
                    break;
                }
            }

        }

        else
        {//transpose by semitones
            int numHalfSteps = random.nextInt(maxTransposeDist);
            if (Notate.bernoulli(.5))
            {//transpose down by numWholeSteps

                int lowest = lowestNote(transposedMelody)-numHalfSteps;

                if (lowest >= minPitch)//make sure the new melody will be in range
                {
                    //transpose copy of chosen theme
                    cm.execute(new ShiftPitchesCommand(-1*numHalfSteps, transposedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
                    break;
                }
            }
            else
            {//transpose up by numWholeSteps

                int highest = highestNote(transposedMelody)+numHalfSteps*2;

                if (highest <= maxPitch)//make sure the new melody will be in range
                {
                    //transpose copy of chosen theme
                    cm.execute(new ShiftPitchesCommand(numHalfSteps, transposedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
                    break;
                }
            }
        }
    }
    
    return transposedMelody;
}

private MelodyPart expandBy(MelodyPart melody, int length, int num)
{
    int newLength = length*num;
    MelodyPart adjustedMelody = melody.copy();
    adjustedMelody.setSize(newLength);
   
    cm.execute(new TimeWarpCommand(adjustedMelody,0, newLength, true, num, 1));
   
    return adjustedMelody;
}

private MelodyPart sideslip(MelodyPart melody, int length)
{ 
    probHalfSideSlip = Double.parseDouble(probHalf.getText());
    probWholeSideSlip = Double.parseDouble(probWhole.getText());
    probThirdSideSlip = Double.parseDouble(probThird.getText());
    
    //probBarLineShift = probBarlineShiftSlider.getValue()/100.0;
    double[] probs = new double[3];
    probs[0] = probHalfSideSlip;
    probs[1] = probWholeSideSlip;
    probs[2] = probThirdSideSlip;
    if (probHalfSideSlip+probWholeSideSlip+probThirdSideSlip != 1.0)
    {
        //normalize
        probs = normalizeProbabilities(probs);
        probHalfSideSlip = probs[0];
        probWholeSideSlip = probs[1];
        probThirdSideSlip = probs[2];
    }
    
    double rand = random.nextDouble();
    double tempTotalProb=0;
    int slideInterval = 2;
    for (int i=0; i<probs.length; i++) 
    {
        tempTotalProb += probs[i];
        if (rand > tempTotalProb) 
        {
            slideInterval = i;
            break;
        }
    }
    
    MelodyPart sideslippedMelody = melody.copy();
    MelodyPart adjustedMelody = melody.copy();
    int start = melody.getSize();
    if (barlineshift)
    {
        start = melody.getSize()/2 - shiftForwardBy;
        sideslippedMelody = melody.extract(start, melody.getSize());
        adjustedMelody = melody.extract(0, start);
        adjustedMelody.setSize(adjustedMelody.getSize());
    }
    
    if (Notate.bernoulli(probSlideUp))
    {//slide up
        if (slideInterval == 0)
        {
            cm.execute(new ShiftPitchesCommand(1, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up half step";
        }
        else if (slideInterval == 1)
        {
            cm.execute(new ShiftPitchesCommand(2, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up whole step";
        }
        else
        {
            cm.execute(new ShiftPitchesCommand(3, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up third";
        }
    }
    else
    {//slide down
        if (slideInterval == 0)
        {
            cm.execute(new ShiftPitchesCommand(-1, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down half step";
        }
        else if (slideInterval == 1)
        {
            cm.execute(new ShiftPitchesCommand(-2, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down whole step";
        }
        else
        {
            cm.execute(new ShiftPitchesCommand(-3, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down third";
        }
    }
    
    //add two of the same melodies with one transposed
    adjustedMelody.setSize(adjustedMelody.getSize()+sideslippedMelody.getSize());
    adjustedMelody.pasteSlots(sideslippedMelody, start);
    return adjustedMelody;
}

private MelodyPart barLineShift(MelodyPart melody, int length)
{
    MelodyPart adjustedMelody = melody.copy();
    MelodyPart secondHalf;
    int start = adjustedMelody.getSize();
    
    if (sideslip)
    {
        start = melody.getSize()/2;
        secondHalf = melody.extract(start, melody.getSize());
        sideslip = false;
    }
    else
    {
        secondHalf = melody.copy();
    }
    probForwardShift = probShiftForwardorBackSlider.getValue()/100.0; 
    shiftForwardBy = shiftForwardByFinal;
    int numBeats = random.nextInt(2) + 1;
    shiftForwardBy *= numBeats;
    if (Notate.bernoulli(probForwardShift))
    {//shift forward
        MelodyPart addRest = new MelodyPart(shiftForwardBy+start);
        addRest.pasteSlots(secondHalf, shiftForwardBy);
        
        adjustedMelody.setSize(start + addRest.getSize());
        adjustedMelody.pasteSlots(addRest, start);
        
        themeUsageText += " forwards";
        barLineShiftForward = true;
        
    }
    else
    {//shift backward
        int shiftBackTo = start- shiftForwardBy;
        adjustedMelody.setSize(start+secondHalf.getSize());
        adjustedMelody.pasteSlots(secondHalf, shiftBackTo);
        themeUsageText += " backwards";
        
    }
    return adjustedMelody;
}

private double[] normalizeProbabilities(double[] probs)
{
    double sum=0;
    for (int i=0; i<probs.length; i++)
    {
        sum+=probs[i];
    }
    for (int i=0; i<probs.length; i++)
    {
        probs[i]/=sum;
    }
    return probs;
}

private boolean transformDoubled(Object[] transforms)
{//returns true if any two of the transforms in the array are equal
    for (int i=0; i<transforms.length; i++)
    {
        for (int j=0; j<transforms.length; j++)
        {
            if (transforms[i] == transforms[j] && i != j)
            {
                return true;
            }
        }
    }
    return false;
}

public MelodyPart generateFromGrammar(int themeLength, MelodyPart solo, int slotNum, int minPitch, int maxPitch)
  {//generates/returns a MelodyPart using the grammar
    Polylist rhythm = lickgen.generateRhythmFromGrammar(slotNum, themeLength);
    //generate rhythm 

    MelodyPart lick = lickgen.fillMelody(minPitch, maxPitch, minInterval,
                                           maxInterval, BEAT, leapProb, rhythm, 
                                           notate.getChordProg(), slotNum, avoidRepeats);
    //create melody
    return lick;
    
}

//inRange is a helper method for myGenerateSolo
public int lowestNote(MelodyPart melody)
{
    //returns the min pitch in melody 
    int lengthOfMelody = melody.size();
    int lowestNote = 128;
    for (int note = 0; note<lengthOfMelody; note++)
    {
        int currentNote = melody.getCurrentNote(note).getPitch();
        if (currentNote < lowestNote && !melody.getCurrentNote(note).isRest())
        {
            lowestNote = currentNote;
        }
    }
    return lowestNote;
}

public int highestNote(MelodyPart melody)
{
    //returns the max pitch in melody 
    int lengthOfMelody = melody.getSize();
    int highestNote = 0;
    for (int note = 0; note<lengthOfMelody; note++)
    {
        int currentNote = melody.getCurrentNote(note).getPitch();
        if (currentNote > highestNote && !melody.getCurrentNote(note).isRest())
        {
            highestNote = currentNote;
        }
    }
    return highestNote;
}  

public boolean inRange(MelodyPart melody)
{//returns true if melody is in the chosen range
    return highestNote(melody) <= maxPitch && lowestNote(melody) >= minPitch;
}

private MelodyPart transformTheme(MelodyPart chosenTheme, ArrayList<String> transformations)
{
    MelodyPart adjustedMelody = chosenTheme.copy();
    
    for (int i=0; i<transformations.size(); i++)
    {
        if (transformations.get(i).equals("Transpose"))
        {
            //transpose
            adjustedMelody = transpose2(adjustedMelody, directionOfTransposition, 
                    distanceOfTransposition, adjustedMelody.getSize());
        }
        else if (transformations.get(i).equals("Invert"))
        {
            //invert
            cm.execute(new InvertCommand(adjustedMelody, 0, adjustedMelody.getSize(), false));
        }
        else if (transformations.get(i).equals("Reverse"))
        {
            //reverse
            cm.execute(new ReverseCommand(adjustedMelody, 0, adjustedMelody.getSize(), false));
        }
        else if  (transformations.get(i).equals("Expand"))
        {
            //expand
            if (expandBy == 2)
            {
                adjustedMelody = expandBy(adjustedMelody, adjustedMelody.getSize(), 2);
            }
            else if (expandBy == 3)
            {
                adjustedMelody = expandBy(adjustedMelody, adjustedMelody.getSize(), 3);
            }
        }
        else if (transformations.get(i).equals("Side Slip"))
        {
            //side slip
            adjustedMelody = sideslip2(adjustedMelody, directionOfSideslip, distanceOfSideslip, adjustedMelody.getSize());
        }
        else if (transformations.get(i).equals("Bar Line Shift"))
        {
            //bar line shift
            adjustedMelody = barlineshift2(adjustedMelody, directionOfShift);
        }
    }
    return adjustedMelody;
}

private MelodyPart transpose2(MelodyPart melody, String direction, String distance, int length)
{
    MelodyPart adjustedMelody = melody.copy();
    if (direction.equals("up"))
    {//slide up
        if (distance.equals("half"))
        {
            cm.execute(new ShiftPitchesCommand(1, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
        }
        else if (distance.equals("whole"))
        {
            cm.execute(new ShiftPitchesCommand(2, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up whole step";
        }
        else if (distance.equals("third"))
        {
            cm.execute(new ShiftPitchesCommand(3, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up third";
        }
    }
    else if (direction.equals("down"))
    {//slide down
        if (distance.equals("half"))
        {
            cm.execute(new ShiftPitchesCommand(-1, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down half step";
        }
        else if (distance.equals("whole"))
        {
            cm.execute(new ShiftPitchesCommand(-2, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down whole step";
        }
        else if(distance.equals("third"))
        {
            cm.execute(new ShiftPitchesCommand(-3, adjustedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down third";
        }
    }
   
    return adjustedMelody;
}

private MelodyPart sideslip2(MelodyPart melody, String direction, String distance, int length)
{
    MelodyPart sideslippedMelody = melody.copy();
    MelodyPart adjustedMelody = melody.copy();
    int start = melody.getSize();
    if (barlineshift2)
    {
        start = melody.getSize()/2 - shiftForwardBy;
        sideslippedMelody = melody.extract(start, melody.getSize());
        adjustedMelody = melody.extract(0, start);
        adjustedMelody.setSize(adjustedMelody.getSize());
    }
    
    if (direction.equals("up"))
    {//slide up
        if (distance.equals("half"))
        {
            cm.execute(new ShiftPitchesCommand(1, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up half step";
        }
        else if (distance.equals("whole"))
        {
            cm.execute(new ShiftPitchesCommand(2, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up whole step";
        }
        else if (distance.equals("third"))
        {
            cm.execute(new ShiftPitchesCommand(3, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " up third";
        }
    }
    else if (direction.equals("down"))
    {//slide down
        if (distance.equals("half"))
        {
            cm.execute(new ShiftPitchesCommand(-1, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down half step";
        }
        else if (distance.equals("whole"))
        {
            cm.execute(new ShiftPitchesCommand(-2, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down whole step";
        }
        else if (distance.equals("third"))
        {
            cm.execute(new ShiftPitchesCommand(-3, sideslippedMelody, 
                        0, length, 0, 128, notate.getScore().getKeySignature()));
            themeUsageText += " down third";
        }
    }
    
    //add two of the same melodies with one transposed
    adjustedMelody.setSize(adjustedMelody.getSize()+sideslippedMelody.getSize());
    adjustedMelody.pasteSlots(sideslippedMelody, start);
    return adjustedMelody;
}

private MelodyPart barlineshift2(MelodyPart melody, String direction)
{
    MelodyPart adjustedMelody = melody.copy();
    MelodyPart secondHalf;
    int start = adjustedMelody.getSize();
    
    if (sideslip2)
    {
        start = melody.getSize()/2;
        secondHalf = melody.extract(start, melody.getSize());
        sideslip2 = false;
    }
    else
    {
        secondHalf = melody.copy();
    }
    
    shiftForwardBy = shiftForwardByFinal;
    int numBeats = 0;
    if (eighthShiftRadioButton.isSelected())
    {
        numBeats = 1;
    }
    else if (quarterShiftRadioButton.isSelected())
    {
        numBeats = 2;
    }
    shiftForwardBy *= numBeats;
    if (direction.equals("forwards"))
    {//shift forward
        MelodyPart addRest = new MelodyPart(shiftForwardBy+start);
        addRest.pasteSlots(secondHalf, shiftForwardBy);
        
        adjustedMelody.setSize(start + addRest.getSize());
        adjustedMelody.pasteSlots(addRest, start);
        
       
        barLineShiftForward = true;
        
    }
    else if (direction.equals("backwards"))
    {//shift backward
        int shiftBackTo = start- shiftForwardBy;
        adjustedMelody.setSize(start+secondHalf.getSize());
        adjustedMelody.pasteSlots(secondHalf, shiftBackTo);
    }
    return adjustedMelody;
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private java.awt.Label ExpandPreferenceTitle;
    private javax.swing.JButton NoButton;
    private javax.swing.JButton NoButton1;
    private javax.swing.JButton OkButton;
    private javax.swing.JButton Reset;
    private javax.swing.JLabel Resettable;
    private java.awt.Label SoloGeneratorTitle;
    private java.awt.Label SoloGeneratorTitle1;
    private java.awt.Label SoloGeneratorTitle2;
    private java.awt.Label SoloGeneratorTitle3;
    private javax.swing.JLabel TransformationOrder;
    private javax.swing.JButton YesButton;
    private javax.swing.JButton YesButton1;
    private javax.swing.JLabel areYouSure;
    private javax.swing.JFrame barLineShiftPreference;
    private javax.swing.JButton barLineShiftPreferencesButton;
    private javax.swing.JTextField barNumberTextField;
    private javax.swing.JToggleButton barlineshiftButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JButton cellOkbutton;
    private javax.swing.JButton cellOkbutton1;
    private javax.swing.JButton cellOkbutton2;
    private javax.swing.JButton cellOkbutton3;
    private javax.swing.JLabel chooseName;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JButton currentSelectionJButton;
    private javax.swing.JFrame customizeSolo;
    private javax.swing.JButton deleteRowbutton;
    private javax.swing.JDialog deleteThemeDialog;
    private javax.swing.JButton deleteThemebutton;
    private javax.swing.JRadioButton eighthShiftRadioButton;
    private javax.swing.JDialog enterAnInteger;
    private javax.swing.JDialog enteredIncorrectly;
    private javax.swing.JToggleButton expandButton;
    private javax.swing.JRadioButton expandBy2RadioButton;
    private javax.swing.JRadioButton expandBy3RadioButton;
    private javax.swing.JFrame expandPreference;
    private javax.swing.JButton expandPreferenceButton;
    private javax.swing.JButton fillSoloButton;
    private javax.swing.JButton generateSoloJButton;
    private javax.swing.JButton generateThemeJButton;
    private javax.swing.JRadioButton halfStepRadioButton;
    private javax.swing.JRadioButton halfStepRadioButton1;
    private javax.swing.JToggleButton invertButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JCheckBox keepEditsFromLeadsheetCheckBox;
    private javax.swing.JMenuItem loadThemesMI;
    private javax.swing.JDialog nameErrorMessage;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel namePicked;
    private javax.swing.JDialog noRowSelected;
    private javax.swing.JLabel noThemeProbLabel;
    private javax.swing.JButton pasteToLeadsheetButton;
    private javax.swing.JButton playThemeButton;
    private javax.swing.JSlider probExpandby2or3;
    private javax.swing.JTextField probHalf;
    private javax.swing.JSlider probShiftForwardorBackSlider;
    private javax.swing.JTextField probThird;
    private javax.swing.JSlider probUpOrDown;
    private javax.swing.JTextField probWhole;
    private javax.swing.JRadioButton quarterShiftRadioButton;
    private javax.swing.JButton rangeChooserButton;
    private javax.swing.JDialog rangeTooSmall;
    private javax.swing.JLabel rangeWrong;
    private javax.swing.JLabel rangeWrong1;
    private javax.swing.JLabel rangeWrong2;
    private javax.swing.JButton rectifySoloButton;
    private javax.swing.JButton redoEditButton;
    private javax.swing.JDialog resetCheck;
    private javax.swing.JDialog resetCheck1;
    private javax.swing.JButton resetSoloButton;
    private javax.swing.JButton resetTransformationChoicesButton;
    private javax.swing.JToggleButton reverseButton;
    private javax.swing.JMenuBar roadmapMenuBar;
    private javax.swing.JMenuItem saveAsAdvice;
    private javax.swing.JButton setAllProbButton;
    private javax.swing.JTextField setAllProbTextField;
    private javax.swing.JButton setProbToZeroButton;
    private javax.swing.JRadioButton shiftBackwardRadioButton;
    private javax.swing.JRadioButton shiftForwardRadioButton;
    private javax.swing.JToggleButton sideslipButton;
    private javax.swing.JRadioButton sideslipDownRadioButton;
    private javax.swing.JFrame sideslipPreference;
    private javax.swing.JRadioButton sideslipUpRadioButton;
    private javax.swing.JTable soloTable;
    private javax.swing.JScrollPane soloTableScrollPane;
    private javax.swing.JToggleButton stopPlaytoggle;
    private javax.swing.JLabel themeIntervalLabel;
    private javax.swing.JTextField themeIntervalTextField;
    private javax.swing.JList themeList;
    private javax.swing.JList themeList1;
    private javax.swing.JScrollPane themeListScrollPane;
    private javax.swing.JScrollPane themeListScrollPane1;
    private javax.swing.JTextPane themeNameTextPane;
    private javax.swing.JTextField themeProbTextField;
    private javax.swing.JScrollPane themeUsageScrollPane;
    private javax.swing.JTextArea themeUsageTextArea;
    private javax.swing.JRadioButton thirdRadioButton;
    private javax.swing.JRadioButton thirdRadioButton1;
    private javax.swing.JComboBox trans1ComboBox;
    private javax.swing.JComboBox trans2ComboBox;
    private javax.swing.JComboBox trans3ComboBox;
    private javax.swing.JComboBox trans4ComboBox;
    private javax.swing.JComboBox trans5ComboBox;
    private javax.swing.JComboBox trans6ComboBox;
    private javax.swing.JScrollPane transformationsUsed;
    private javax.swing.JTextArea transformationsUsedTextArea;
    private javax.swing.JToggleButton transposeButton;
    private javax.swing.JRadioButton transposeDownRadioButton;
    private javax.swing.JRadioButton transposeUpRadioButton;
    private javax.swing.JLabel tryAgain;
    private javax.swing.JLabel tryAgain1;
    private javax.swing.JLabel tryAgain2;
    private javax.swing.JLabel tryAgain3;
    private javax.swing.JLabel typedWrong;
    private javax.swing.JLabel typedWrong1;
    private javax.swing.JButton undoEditButton;
    private javax.swing.JRadioButton wholeStepRadioButton;
    private javax.swing.JRadioButton wholeStepRadioButton1;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    private javax.swing.JButton yesdeletethemeButton;
    private javax.swing.JLabel youSure;
    private javax.swing.JLabel youSure1;
    // End of variables declaration//GEN-END:variables
}
