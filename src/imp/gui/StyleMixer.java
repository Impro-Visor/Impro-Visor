/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.gui;

import imp.midi.MIDIBeast;
import imp.data.stylePatterns.BassPattern;
import imp.data.stylePatterns.DrumPattern;
import imp.data.stylePatterns.DrumRuleRep;
import imp.data.stylePatterns.ChordPattern;
import imp.Constants;
import imp.ImproVisor;
import imp.com.OpenLeadsheetCommand;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import imp.util.LeadsheetFileView;
import imp.util.StyleFilter;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTable;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.Tokenizer;


/**
 * @author Robert Keller, Caitlin Chen
 * 
 * Use of public access to elements in MIDIBeast should
 * be changed to use proper methods.
 */

@SuppressWarnings("serial")

public class StyleMixer extends javax.swing.JDialog implements Constants
{
/**
 * name used in drum rules
 */
public static final String DRUM_SYMBOL = "drum";

Notate notate;
StyleEditor styleEditor;


/**
 * minimum duration (in slots) for a note not to be counted as a rest.
 */
private int minDuration = 0;


public static final int BASS = 0;
public static final int DRUM = 1;
public static final int CHORD = 2;

public static final Color BASS_COLOR = Color.orange;
public static final Color CHORD_COLOR = Color.green;
public static final Color DRUM_COLOR = Color.yellow;

public static final Boolean USE_TRUE = true;
public static final Boolean USE_FALSE = false;

public static final int USE = 0;
public static final int STYLE = 1;
public static final int NAME = 2;
public static final int PATTERN = 3;

// Drum colum indices
public static final int DRUM_USE = 0;
public static final int DRUM_STYLE = 1;
public static final int DRUM_PATTERN_NAME = 2;
public static final int DRUM_NAME = 3;
public static final int DRUM_RULE = 4;

/**
 * stuff for the pattern tables
 */
String[] columnHeaders = new String[]
    {
        "Use", "Style", "Name", "Pattern"
    };

String[] drumColumnHeaders = new String[]
    {
        "Use", "Style", "Pattern Name", "Rule Name", "Rule"
    };

private BassTableModel bassTableModel = 
        new BassTableModel(columnHeaders, ROW_COUNT);

private ChordTableModel chordTableModel =
        new ChordTableModel(columnHeaders, ROW_COUNT);

private DrumTableModel drumTableModel =
        new DrumTableModel(drumColumnHeaders, DRUM_ROW_COUNT);

public static final int ROW_COUNT = 200;

public static final int DRUM_ROW_COUNT = 400;

// Used to load styles into the mixer
private JFileChooser openStyle = new JFileChooser();

private File savedStyle = null;

private File styleDir;

// Hash Maps hold all the patterns that are loaded into the mixer
private LinkedHashMap<String, String> bassRules =
        new LinkedHashMap<String, String>();

private LinkedHashMap<String, String> chordRules =
        new LinkedHashMap<String, String>();

private LinkedHashMap<String, String> drumRules =
        new LinkedHashMap<String, String>();

private LinkedHashMap<String, ArrayList<DrumRuleRep>> drumPatterns = 
        new LinkedHashMap<String, ArrayList<DrumRuleRep>>();

private LinkedHashMap<DrumRuleRep, Integer> ruleIndex = 
        new LinkedHashMap<DrumRuleRep, Integer>();

private ArrayList<String> bassPatternNames = new ArrayList<String>();
private ArrayList<String> chordPatternNames = new ArrayList<String>();

private ArrayList<String> drumRuleNames = new ArrayList<String>();
private ArrayList<String> drumPatternNames = new ArrayList<String>();


/**
 * Creates new form ExtractionEditor
 */

public StyleMixer(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p)
  {
    super(parent, modal);
    this.styleEditor = p;
    this.notate = p.getNotate();
    
    initComponents();
    
    bassTable.setModel(bassTableModel);
    chordTable.setModel(chordTableModel);
    drumTable.setModel(drumTableModel);
    
    styleDir = ImproVisor.getStyleDirectory();

    initComponents2();
    setSize(900, 425);

    //SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
    //loadStyleMixerPatterns();
  }

public class BassTableModel extends DefaultTableModel
{
    private static final int columnCount = 4;
    
    boolean[] canEdit = new boolean[]
    {
      //use,  style, name, pattern
        true, false, true, true
    }; 
    
    
    public BassTableModel(String[] headers, int rows)
    {
        super(headers, rows);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) 
    {
        return canEdit [columnIndex];
    }
    
    @Override
    public Class<?> getColumnClass(int column)
    {
        if( column == USE )
        {
            return Boolean.class;
        }
        else
        {
            return Object.class;
        }
    }
}

public class ChordTableModel extends DefaultTableModel
{
    private static final int columnCount = 4;
    
    boolean[] canEdit = new boolean[]
    {
      //use,  style, name, pattern
        true, false, true, true
    };
    
    
    public ChordTableModel(String[] columnHeaders, int rows)
    {
        super(columnHeaders, rows);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) 
    {
        return canEdit [columnIndex];
    }
    
    @Override
    public Class<?> getColumnClass(int column)
    {
        if( column == USE )
        {
            return Boolean.class;
        }
        else
        {
            return Object.class;
        }
    }
}

public class DrumTableModel extends DefaultTableModel
{
    private static final int columnCount = 5;
    
    boolean[] canEdit = new boolean[]
    {
      //use,  style, name, pattern
        true, false, true, true, true
    };
    
    
    public DrumTableModel(String[] headers, int rows)
    {
        super(headers, rows);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) 
    {
        return canEdit [columnIndex];
    }
    
    @Override
    public Class<?> getColumnClass(int column)
    {
        if( column == DRUM_USE )
        {
            return Boolean.class;
        }
        else
        {
            return Object.class;
        }
    }
}

/**
 * Clears the style mixer to allow for new things to be added
 */
public void reset()
{
    bassTableModel = new BassTableModel(columnHeaders, ROW_COUNT);
    chordTableModel = new ChordTableModel(columnHeaders, ROW_COUNT);
    drumTableModel = new DrumTableModel(drumColumnHeaders, DRUM_ROW_COUNT);
    
    bassTable.setModel(bassTableModel);
    chordTable.setModel(chordTableModel);
    drumTable.setModel(drumTableModel);
    
    bassRules = new LinkedHashMap<String, String>();
    chordRules = new LinkedHashMap<String, String>();
    drumRules = new LinkedHashMap<String, String>();
    
    drumPatterns = new LinkedHashMap<String, ArrayList<DrumRuleRep>>();
    drumPatternNames = new ArrayList<String>();
    drumRuleNames = new ArrayList<String>();
    ruleIndex = new LinkedHashMap<DrumRuleRep, Integer>();
}

public StyleEditor newStyleEditor()
{
    StyleEditor m = new StyleEditor(notate);
      m.pack();
      m.setLocationRelativeTo(this);
      m.setLocation(m.getX() + WindowRegistry.defaultXnewWindowStagger, 
                    m.getY() + WindowRegistry.defaultYnewWindowStagger);
      WindowRegistry.registerWindow(m, "New Style");
      m.setVisible(true);
    return m;
}

/*
public void setBass()
  {
    styleMixerPanel.setBackground(Color.orange);
    setBassRawRules();
  }

public void setChords()
  {
    setChordRawRules();
  }

public void setDrums()
  {
    setDrumRawRules();
  }



public void setBassRawRules()
  {
    //rawRulesModelBass.clear();
    //rawRulesJListBass.setModel(rawRulesModelBass);
  }

public void setChordRawRules()
  {
    //rawRulesModelChord.clear();
    //rawRulesJListChord.setModel(rawRulesModelChord);
  }

public void setDrumRawRules()
  {
    //rawRulesModelDrum.clear();    
    //rawRulesJListDrum.setModel(rawRulesModelDrum);
   }

//private RepresentativeDrumRules.DrumPattern makeDrumPattern(String string)
//  {
//    String[] split = string.split("\n");
//    RepresentativeDrumRules.DrumPattern drumPattern = repDrumRules.makeDrumPattern();
//    for( int i = 1; i < split.length - 1; i++ )
//      {
//        RepresentativeDrumRules.DrumRule drumRule = repDrumRules.makeDrumRule();
//        int instrumentNumber = Integer.parseInt(split[i].substring(split[i].indexOf('m') + 2, split[i].indexOf('m') + 4));
//        drumRule.setInstrumentNumber(instrumentNumber);
//        int startIndex = split[i].indexOf('m') + 2;
//        int endIndex = split[i].indexOf(')');
//        String elements = split[i].substring(startIndex, endIndex);
//        String[] split2 = elements.split(" ");
//        // Start at 1 rather than 0, to skip over the drum number
//        for( int j = 1; j < split2.length; j++ )
//          {
//            drumRule.addElement(split2[j]);
//          }
//        String weightString = split[split.length - 1];
//
//        drumPattern.setWeight(1);
//        //System.out.println("adding drumPattern " + drumPattern);
//        drumPattern.addRule(drumRule);
//      }
//    return drumPattern;
//  }
*/

private void initComponents2()
  {
    java.awt.GridBagConstraints gridBagConstraints;
    LeadsheetFileView styView = new LeadsheetFileView();
    
    openStyle.setCurrentDirectory(styleDir);
    openStyle.setDialogType(JFileChooser.OPEN_DIALOG);
    openStyle.setDialogTitle("Open Style");
    openStyle.setFileSelectionMode(JFileChooser.FILES_ONLY);
    openStyle.resetChoosableFileFilters();
    openStyle.addChoosableFileFilter(new StyleFilter());
    openStyle.setFileView(styView);

    errorDialog = new javax.swing.JDialog();
    errorMessage = new javax.swing.JLabel();
    errorButton = new javax.swing.JButton();

    errorDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    errorDialog.setTitle("Error");
    errorDialog.setBackground(java.awt.Color.white);
    errorMessage.setForeground(new java.awt.Color(255, 0, 51));
    errorMessage.setText("jLabel1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
    errorDialog.getContentPane().add(errorMessage, gridBagConstraints);

    errorButton.setText("OK");
    errorButton.addActionListener(new java.awt.event.ActionListener()
    {

    public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        errorButtonActionPerformed(evt);
      }
    });
  }

private void errorButtonActionPerformed(java.awt.event.ActionEvent evt)
  {//GEN-FIRST:event_errorButtonActionPerformed
    errorDialog.setVisible(false);
  }//GEN-LAST:event_errorButtonActionPerformed



/**
 * This method is called from within the constructor to initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is always
 * regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        styleMixerPanel = new javax.swing.JPanel();
        bassPatternButtonPanel = new javax.swing.JPanel();
        selectPatternBtnBass = new javax.swing.JButton();
        deletePatternBtnBass = new javax.swing.JButton();
        chordPatternButtonPanel = new javax.swing.JPanel();
        selectPatternBtnChord = new javax.swing.JButton();
        deletePatternBtnChord = new javax.swing.JButton();
        drumPatternButtonPanel = new javax.swing.JPanel();
        selectPatternBtnDrum = new javax.swing.JButton();
        deletePatternBtnDrum = new javax.swing.JButton();
        bassScrollPane = new javax.swing.JScrollPane();
        bassTable = new javax.swing.JTable();
        chordScrollPane = new javax.swing.JScrollPane();
        chordTable = new javax.swing.JTable();
        drumScrollPane = new javax.swing.JScrollPane();
        drumTable = new javax.swing.JTable();
        styleButtonPanel = new javax.swing.JPanel();
        playStyleButton = new javax.swing.JButton();
        copyStyleButton = new javax.swing.JButton();
        loadFileButton = new javax.swing.JButton();
        stopPlayingButton = new javax.swing.JButton();
        clearMixerButton = new javax.swing.JButton();
        mirrorPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        patternLabel = new javax.swing.JLabel();
        patternField = new javax.swing.JTextField();
        extractionEditorMenuBar = new javax.swing.JMenuBar();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        setTitle("Style Mixer");
        setMinimumSize(new java.awt.Dimension(1220, 600));
        setPreferredSize(new java.awt.Dimension(1377, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        styleMixerPanel.setLayout(new java.awt.GridBagLayout());

        bassPatternButtonPanel.setLayout(new java.awt.GridBagLayout());

        selectPatternBtnBass.setText("Copy Bass Patterns to Style Editor");
        selectPatternBtnBass.setToolTipText("Move the selected Bass Pattern to the next column of the Style Editor.");
        selectPatternBtnBass.setPreferredSize(new java.awt.Dimension(265, 23));
        selectPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBassPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        bassPatternButtonPanel.add(selectPatternBtnBass, gridBagConstraints);

        deletePatternBtnBass.setText("Delete Bass Pattern");
        deletePatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBassPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        bassPatternButtonPanel.add(deletePatternBtnBass, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        styleMixerPanel.add(bassPatternButtonPanel, gridBagConstraints);

        chordPatternButtonPanel.setLayout(new java.awt.GridBagLayout());

        selectPatternBtnChord.setText("Copy Chord Patterns to Style Editor");
        selectPatternBtnChord.setToolTipText("Move the selected Chord Pattern to the next column of the Style Editor.");
        selectPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyChordPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        chordPatternButtonPanel.add(selectPatternBtnChord, gridBagConstraints);

        deletePatternBtnChord.setText("Delete Chord Pattern");
        deletePatternBtnChord.setToolTipText("Delete Chord Pattern");
        deletePatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteChordPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        chordPatternButtonPanel.add(deletePatternBtnChord, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        styleMixerPanel.add(chordPatternButtonPanel, gridBagConstraints);

        drumPatternButtonPanel.setLayout(new java.awt.GridBagLayout());

        selectPatternBtnDrum.setText("Copy Drum Patterns to Style Editor");
        selectPatternBtnDrum.setToolTipText("Move the selected Drum Pattern to the next column of the Style Editor.");
        selectPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyDrumPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        drumPatternButtonPanel.add(selectPatternBtnDrum, gridBagConstraints);

        deletePatternBtnDrum.setText("Delete Drum Pattern");
        deletePatternBtnDrum.setToolTipText("Delete Drum Pattern");
        deletePatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDrumPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        drumPatternButtonPanel.add(deletePatternBtnDrum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        styleMixerPanel.add(drumPatternButtonPanel, gridBagConstraints);

        bassScrollPane.setMinimumSize(new java.awt.Dimension(454, 404));

        bassTable.setBackground(java.awt.Color.orange);
        bassTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        bassTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bassTableMouseClicked(evt);
            }
        });
        bassScrollPane.setViewportView(bassTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        styleMixerPanel.add(bassScrollPane, gridBagConstraints);

        chordScrollPane.setMinimumSize(new java.awt.Dimension(454, 404));

        chordTable.setBackground(java.awt.Color.green);
        chordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        chordTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chordTableMouseClicked(evt);
            }
        });
        chordScrollPane.setViewportView(chordTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        styleMixerPanel.add(chordScrollPane, gridBagConstraints);

        drumScrollPane.setMinimumSize(new java.awt.Dimension(454, 404));

        drumTable.setBackground(java.awt.Color.yellow);
        drumTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        drumTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drumTableMouseClicked(evt);
            }
        });
        drumScrollPane.setViewportView(drumTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        styleMixerPanel.add(drumScrollPane, gridBagConstraints);

        styleButtonPanel.setLayout(new java.awt.GridBagLayout());

        playStyleButton.setText("Play Style");
        playStyleButton.setPreferredSize(new java.awt.Dimension(300, 23));
        playStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playStyleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleButtonPanel.add(playStyleButton, gridBagConstraints);

        copyStyleButton.setText("Copy Style to Editor");
        copyStyleButton.setPreferredSize(new java.awt.Dimension(300, 23));
        copyStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyStyleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleButtonPanel.add(copyStyleButton, gridBagConstraints);

        loadFileButton.setText("Load Style From File");
        loadFileButton.setPreferredSize(new java.awt.Dimension(300, 23));
        loadFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleButtonPanel.add(loadFileButton, gridBagConstraints);

        stopPlayingButton.setText("Stop Playing");
        stopPlayingButton.setPreferredSize(new java.awt.Dimension(300, 23));
        stopPlayingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayingButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleButtonPanel.add(stopPlayingButton, gridBagConstraints);

        clearMixerButton.setText("Clear Style Mixer");
        clearMixerButton.setPreferredSize(new java.awt.Dimension(300, 23));
        clearMixerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMixerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleButtonPanel.add(clearMixerButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        styleMixerPanel.add(styleButtonPanel, gridBagConstraints);

        mirrorPanel.setMinimumSize(new java.awt.Dimension(804, 30));
        mirrorPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("Name: ");
        nameLabel.setMaximumSize(new java.awt.Dimension(52, 16));
        nameLabel.setMinimumSize(new java.awt.Dimension(52, 16));
        nameLabel.setPreferredSize(new java.awt.Dimension(52, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mirrorPanel.add(nameLabel, gridBagConstraints);

        nameField.setMinimumSize(new java.awt.Dimension(200, 22));
        nameField.setPreferredSize(new java.awt.Dimension(200, 28));
        mirrorPanel.add(nameField, new java.awt.GridBagConstraints());

        patternLabel.setText("Pattern: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mirrorPanel.add(patternLabel, gridBagConstraints);

        patternField.setMinimumSize(new java.awt.Dimension(500, 22));
        patternField.setPreferredSize(new java.awt.Dimension(500, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mirrorPanel.add(patternField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        styleMixerPanel.add(mirrorPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(styleMixerPanel, gridBagConstraints);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
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
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        extractionEditorMenuBar.add(windowMenu);

        setJMenuBar(extractionEditorMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
  {//GEN-HEADEREND:event_closeWindowMIActionPerformed
    dispose();
  }//GEN-LAST:event_closeWindowMIActionPerformed

private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
  {//GEN-HEADEREND:event_cascadeMIActionPerformed
    WindowRegistry.cascadeWindows(this);
  }//GEN-LAST:event_cascadeMIActionPerformed

private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
  {//GEN-HEADEREND:event_windowMenuMenuSelected
    windowMenu.removeAll();

    windowMenu.add(closeWindowMI);

    windowMenu.add(cascadeMI);

    windowMenu.add(windowMenuSeparator);

    for( WindowMenuItem w : WindowRegistry.getWindows() )
      {
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
      }

    windowMenu.repaint();
  }//GEN-LAST:event_windowMenuMenuSelected

    private void copyDrumPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyDrumPatternToStyleEditor
        copySelectedDrumPatternsToStyleEditor();
    }//GEN-LAST:event_copyDrumPatternToStyleEditor

    private void copyChordPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordPatternToStyleEditor
        copySelectedChordPatternsToStyleEditor();
    }//GEN-LAST:event_copyChordPatternToStyleEditor

private void copyBassPatternToStyleEditor(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyBassPatternToStyleEditor
  {//GEN-HEADEREND:event_copyBassPatternToStyleEditor
     copySelectedBassPatternsToStyleEditor(); 
  }//GEN-LAST:event_copyBassPatternToStyleEditor

    private void deleteBassPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBassPattern
       String name = (String)bassTable.getValueAt(bassTable.getSelectedRow(), NAME);
       bassTableModel.removeRow(bassTable.getSelectedRow());  
       bassRules.remove(name);
       bassPatternNames.remove(name);
    }//GEN-LAST:event_deleteBassPattern

    private void deleteChordPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteChordPattern
       String name = (String)chordTable.getValueAt(chordTable.getSelectedRow(), NAME);
       chordTableModel.removeRow(chordTable.getSelectedRow()); 
       chordRules.remove(name);
       chordPatternNames.remove(name);
    }//GEN-LAST:event_deleteChordPattern

    private void deleteDrumPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDrumPattern
       int row = drumTable.getSelectedRow();
       String name = (String)drumTable.getValueAt(row, DRUM_NAME);
       drumTableModel.removeRow(row);
       drumRules.remove(name);
       drumRuleNames.remove(name);
    }//GEN-LAST:event_deleteDrumPattern

    private void playStyleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playStyleButtonActionPerformed
        Style tempStyle = makeTempStyle();
        notate.playScore(tempStyle);
    }//GEN-LAST:event_playStyleButtonActionPerformed

    private void copyStyleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyStyleButtonActionPerformed
        StyleEditor newEditor = newStyleEditor();
        //WindowRegistry.registerWindow(newEditor);
        styleEditor = newEditor;
        copySelectedBassPatternsToStyleEditor();
        copySelectedChordPatternsToStyleEditor();
        copySelectedDrumPatternsToStyleEditor();
    }//GEN-LAST:event_copyStyleButtonActionPerformed

    private void loadFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileButtonActionPerformed
        openStyle();
    }//GEN-LAST:event_loadFileButtonActionPerformed

    private void stopPlayingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPlayingButtonActionPerformed
        notate.stopPlaying();
    }//GEN-LAST:event_stopPlayingButtonActionPerformed

    private void clearMixerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMixerButtonActionPerformed
        reset();
        //initComponents();
        //initComponents2();
    }//GEN-LAST:event_clearMixerButtonActionPerformed

    private void bassTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bassTableMouseClicked
        int row = bassTable.getSelectedRow();
        String pattern = (String)bassTable.getValueAt(row, PATTERN);
        playPattern(BASS, pattern);
        String name = (String)bassTable.getValueAt(row, NAME);
        updateMixerMirror( BASS_COLOR, name, pattern);      
    }//GEN-LAST:event_bassTableMouseClicked

    private void chordTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chordTableMouseClicked
        int row = chordTable.getSelectedRow();
        String pattern = (String)chordTable.getValueAt(row, PATTERN);
        playPattern(CHORD, pattern);
        String name = (String)chordTable.getValueAt(row, NAME);
        updateMixerMirror( CHORD_COLOR, name, pattern );
    }//GEN-LAST:event_chordTableMouseClicked

    private void drumTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drumTableMouseClicked
        int row = drumTable.getSelectedRow();
        String pattern = (String)drumTable.getValueAt(row, DRUM_RULE);
        playPattern(DRUM, pattern);
        String name = (String)drumTable.getValueAt(row, DRUM_NAME);
        updateMixerMirror( DRUM_COLOR, name, pattern );
    }//GEN-LAST:event_drumTableMouseClicked


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bassPatternButtonPanel;
    private javax.swing.JScrollPane bassScrollPane;
    private javax.swing.JTable bassTable;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JPanel chordPatternButtonPanel;
    private javax.swing.JScrollPane chordScrollPane;
    private javax.swing.JTable chordTable;
    private javax.swing.JButton clearMixerButton;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JButton copyStyleButton;
    private javax.swing.JButton deletePatternBtnBass;
    private javax.swing.JButton deletePatternBtnChord;
    private javax.swing.JButton deletePatternBtnDrum;
    private javax.swing.JPanel drumPatternButtonPanel;
    private javax.swing.JScrollPane drumScrollPane;
    private javax.swing.JTable drumTable;
    private javax.swing.JMenuBar extractionEditorMenuBar;
    private javax.swing.JButton loadFileButton;
    private javax.swing.JPanel mirrorPanel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField patternField;
    private javax.swing.JLabel patternLabel;
    private javax.swing.JButton playStyleButton;
    private javax.swing.JButton selectPatternBtnBass;
    private javax.swing.JButton selectPatternBtnChord;
    private javax.swing.JButton selectPatternBtnDrum;
    private javax.swing.JButton stopPlayingButton;
    private javax.swing.JPanel styleButtonPanel;
    private javax.swing.JPanel styleMixerPanel;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

private javax.swing.JButton errorButton;
private javax.swing.JDialog errorDialog;
private javax.swing.JLabel errorMessage;

  /**
   * Override dispose so as to unregister this window first.
   */
  
  @Override
  public void dispose()
    {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
    }
 
/**
 * Opens the directory to choose a file to load into the mixer
 */
public void openStyle()
{
    if( openStyle.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
      openStyle.setCurrentDirectory(styleDir);
      }
    //show open file dialog
    if( openStyle.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
      {
      // The opened file becomes the saved file, in case of save.
      savedStyle = openStyle.getSelectedFile();
      
      // Load the file.
      loadFromFile(savedStyle);
      }
}

/**
 * loads the patters from a style file into the style mixer
 * @param file 
 */
public void loadCleanFromFile( File file )
{
    reset();
    
    loadFromFile(file);
}

public void loadFromFile( File file )
{
    // Parse style.
    String s = OpenLeadsheetCommand.fileToString(file);
    
    if( s == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Unable to open style file: " + file.getName());
        return;
      }
    
    savedStyle = file;
    ImproVisor.setRecentStyleFile(file);
    
    s = s.substring(1, s.length() - 1);
    Polylist poly = Notate.parseListFromString(s);
    Style style = Style.makeStyle(Notate.parseListFromString(s));
    String styleName = style.getName();
    
    ArrayList<BassPattern> bp = style.getBP();
    ArrayList<DrumPattern> dp = style.getDP();
    ArrayList<ChordPattern> cp = style.getCP();
    
    //add all bass patterns into bassRules hash map
    for( int i = 0; i < bp.size(); i++ )
    {
        BassPattern curPat = bp.get(i);
        String name = curPat.getName();
        String rule = curPat.forGenerator();       
              
        int row = bassRules.size();
        
        if( name.equals("") )
        {
            name = "" + row;
        }
        
        bassPatternNames.add(name);
        bassRules.put(name, rule);
        //System.out.println("bass rules: " + bassRules);
        
        bassTable.setValueAt(USE_TRUE, row, USE); //set use cell
        bassTable.setValueAt(styleName, row, STYLE); //set style name cell
        bassTable.setValueAt(name, row, NAME); // set pattern name
        bassTable.setValueAt(rule, row, PATTERN); //set pattern
    }
    
    //add chord patterns into table and hash map
    for( int i = 0; i < cp.size(); i++)
    {
        ChordPattern curPat = cp.get(i);
        String name = curPat.getName();
        String rule = curPat.forGenerator();
               
        int row = chordRules.size();
        
        if( name.equals("") )
        {
            name = "" + row;
        }
        
        chordPatternNames.add(name);
        chordRules.put(name, rule);
        //System.out.println("chord rules: " + chordRules);
        
        chordTable.setValueAt(USE_TRUE, row, USE); //set use cell
        chordTable.setValueAt(styleName, row, STYLE); //set style name cell
        chordTable.setValueAt(name, row, NAME); // set pattern name
        chordTable.setValueAt(rule, row, PATTERN); //set pattern
    }
    
    //add drum patterns into table and hash map
    int patternSize = drumPatterns.size();
    for( int i = 0; i< dp.size(); i++ )
    {
        DrumPattern curPat = dp.get(i);
        String patternName = curPat.getName();
        ArrayList<DrumRuleRep> drums = curPat.getDrums();
        
        
        if( patternName.equals("") )
        {
            patternName = "" + (i + patternSize);
        }
        
        drumPatterns.put(patternName, drums);
        drumPatternNames.add(patternName);
        
        for( int j = 0; j < drums.size(); j++ )
        {
            DrumRuleRep curRep = drums.get(j);
            String name = curRep.getName();
            String rule = curRep.forStyleMixer();
            //System.out.println("drum rule: " + curRep.toString());
            
            int row = drumRules.size();
            ruleIndex.put(curRep, row);
            
            if( name.equals("") )
            {
                name = "" + row;
            }
            
            drumRules.put(name, rule);
            drumRuleNames.add(name);
            
            drumTable.setValueAt(USE_TRUE, row, DRUM_USE);
            drumTable.setValueAt(styleName, row, DRUM_STYLE);
            drumTable.setValueAt(patternName, row, DRUM_PATTERN_NAME);
            drumTable.setValueAt(name, row, DRUM_NAME);
            drumTable.setValueAt(rule, row, DRUM_RULE);
        }
    }
}

/**
 * Displays the selected pattern and name in the fields above the
 * instrument tables
 * @param color
 * @param name
 * @param pattern 
 */
public void updateMixerMirror(Color color, String name, String pattern)
{
        nameField.setBackground(color);
        patternField.setBackground(color);
        nameField.setText(name);
        patternField.setText(pattern);
}

/**
 * Used to copy the patterns for each instrument to the style editor
 */
public void copySelectedBassPatternsToStyleEditor()
{
    for(int i = 0; i < bassRules.size(); i++)
     {
         Boolean useValue = (Boolean)bassTable.getValueAt(i, USE);
         if( useValue )
         {
             String name = (String)bassTable.getValueAt(i, NAME);
             if( name.matches("[0-9]+") )
             {
                 name = "";
             }
             String pattern = (String)bassTable.getValueAt(i, PATTERN);
             styleEditor.setNextBassPattern(pattern, name);
         }
     } 
}

public void copySelectedChordPatternsToStyleEditor()
{
    for(int i = 0; i < chordRules.size(); i++)
    {
        Boolean useValue = (Boolean)chordTable.getValueAt(i, USE);
        if( useValue )
        {
            String name = (String)chordTable.getValueAt(i, NAME);
            if( name.matches("[0-9]+") )
            {
                name = "";
            }
            String pattern = (String)chordTable.getValueAt(i, PATTERN);
            styleEditor.setNextChordPattern(pattern, name);
        }
    } 
}

public void copySelectedDrumPatternsToStyleEditor()
{
    for( int i = 0; i < drumPatterns.size(); i++ )
    {
        String name = drumPatternNames.get(i);
        StringBuilder drumRules = new StringBuilder();
        drumRules.append("(");
        ArrayList<DrumRuleRep> rules = drumPatterns.get(name);
        
        if( name.matches("[0-9]+") )
            {
                name = "";
            }
        
        for( DrumRuleRep rep: rules )
        {
            //System.out.println("drum rule: " + rep);
            int repIndex = ruleIndex.get(rep);
            Boolean useValue = (Boolean)drumTable.getValueAt(repIndex, DRUM_USE);
            if( useValue )
            {
                drumRules.append(rep.toString().trim());
            }
        }
        //System.out.println("drum pattern: " + drumRules);
        //drumRules.append(")");
        styleEditor.setNextDrumPattern(drumRules.toString().trim(), name);
    }
}

/*

public Object getKey( Set keySet, int i )
{
    int count = 0;
    Iterator it = keySet.iterator();
    Object key = it.next();
    while( it.hasNext() )
    {
        if(count == i)
        {
            key = it.next();
            return key;
        }
        count++;
    }
    return key;
}
*/

//public int getPatternIndex( int i, int patternSize )
//{
    //if
//}


/**
 * Currently NOT used because of the changes to the style mixer
 * Could eventually be modified to copy patterns into the instrument tables
 * Copy a rectangle of cells for copying to the Style Mixer
 * @param cells
 * @param rowNumber
 * @param instrumentName 
 */
public void copyCellsForStyleMixer(Polylist cells, int rowNumber, String instrumentName[])
  {
    // cells are organized by column, so put each column into an array 
    // element.
    
      
    Polylist column[] = new Polylist[cells.length()];
    
    int j = 0;
    while( cells.nonEmpty() )
      {
        column[j++] = (Polylist)cells.first();
        cells = cells.rest();
      }
    
    int numColumns = j;
    
    int numRows = numColumns > 0 ? column[0].length() : 0;
    
    //System.out.println(numRows + " rows, " + numColumns + " columns");
    
    // Buffers for concatenating any drum rules by column
    PolylistBuffer buffer[] = new PolylistBuffer[numColumns];
    
    for( j = 0; j < numColumns; j++ )
      {
         buffer[j] = new PolylistBuffer();
       }

    for( int i = 0; i < numRows; i++ )
      {
        for( j = 0; j < numColumns; j++ )
          {
            int trueRow = rowNumber + i;
            Polylist item = (Polylist)column[j].first();
            
            if( item.nonEmpty() && !item.toString().equals("()") )
              {
              //System.out.println("row " + trueRow + ", column " + j + ": " + item);
              switch(trueRow)
                {
                case StyleTableModel.BASS_PATTERN_ROW:
                    //if( !containsAsString(rawRulesModelBass, item) )
                    //{
                    //rawRulesModelBass.addElement(item);
                    //}
                 break;
                  
                case StyleTableModel.CHORD_PATTERN_ROW:
                    //if( !containsAsString(rawRulesModelChord, item) )
                    //{
                    //rawRulesModelChord.addElement(item);
                    //}
                 break;
                  
                default:
                 // Buffer drum rules as belonging to a paJttern in a given
                 // column. At the end of transfer, create drum patterns out
                 // of rules stored in a specific buffer.
                 if( trueRow >= StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW)
                      {
                      buffer[j].append(item.cons(instrumentName[i]).cons(DRUM_SYMBOL));    
                      }
                }
              }
            
            column[j] = column[j].rest();
          }
      }
    
    for( j = 0; j < numColumns; j++ )
      {
        Polylist L = buffer[j].toPolylist();
        if( L.nonEmpty() )
          {
            //if ( !containsAsString(rawRulesModelDrum, L) )
            //{
             //rawRulesModelDrum.addElement(L);
            //}
          }
      }
    saveStylePatterns();
  }

  /*
   * Only used in the method above, which isn't used currently
   */
  public void saveStylePatterns()
    {
    String eol = System.getProperty( "line.separator" );
  
    File file = ImproVisor.getStyleMixerFile();
    try
      {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      StringBuilder buffer = new StringBuilder();
        
      //for( Enumeration e = rawRulesModelBass.elements(); e.hasMoreElements(); )
      //{
          buffer.append("(bass-pattern ");
          //buffer.append(e.nextElement().toString());
          buffer.append(")");
          buffer.append(eol);
      //}
      
      //for( Enumeration e = rawRulesModelChord.elements(); e.hasMoreElements(); )
      //{
          buffer.append("(chord-pattern ");
          //buffer.append(e.nextElement().toString());
          buffer.append(")");
          buffer.append(eol);
      //}
            
      //for( Enumeration e = rawRulesModelDrum.elements(); e.hasMoreElements(); )
      //{
          buffer.append("(drum-pattern ");
          //buffer.append((e.nextElement()).toString().substring(1));
          buffer.append(eol);
      //}            
      
      String styleResult = buffer.toString();
      
      //System.out.println("Saving mixer patterns to file: " + eol + styleResult);
      
      out.write(styleResult);
      out.close();

       }
    catch( Exception e )
      {
      }
    }
  
  /*
   * Not used currently
   */
  private void loadStyleMixerPatterns()
  {
  File mixerFile = ImproVisor.getStyleMixerFile();
  try
    {
    FileInputStream fis = new FileInputStream(mixerFile);
    Tokenizer in = new Tokenizer(fis);
    Object token;
         
    // Read in S expressions until end of file is reached
    while ((token = in.nextSexp()) != Tokenizer.eof)
     {
      //System.out.println("token = " + token);
      Polylist tokenP = (Polylist)token;
      if(tokenP.first().equals("bass-pattern"))
        {
          //rawRulesModelBass.addElement(tokenP.second());
        }
      else if(tokenP.first().equals("chord-pattern"))
        {
          //rawRulesModelChord.addElement(tokenP.second());
        }
      else if(tokenP.first().equals("drum-pattern"))
        {
          //rawRulesModelDrum.addElement(tokenP.rest());
        }   
    }
  }
  catch( java.io.FileNotFoundException e )
        { 
            ErrorLog.log(ErrorLog.WARNING, "StyleMixer file not found");
        }
  }
  
private void playPattern(int type, String string)
  {
   //String string;
   PatternDisplay display;
   switch( type )
     {
       case BASS:
           //string = polylist.toStringSansParens();
           display = new BassPatternDisplay(string, 1.0f, styleEditor.getNotate(), null, styleEditor);
           //System.out.println("display = " + display);
           display.playMe();
           break;
           
        case CHORD:
           //string = polylist.toStringSansParens();
           display = new ChordPatternDisplay(string, 1.0f, "", styleEditor.getNotate(), null, styleEditor);
           //System.out.println("display = " + display);
           display.playMe();
           break;
           
           
       case DRUM:
           Polylist polylist = Polylist.PolylistFromString(string);
           Polylist pattern = (Polylist)polylist.rest();
           Long longNumber = (Long)polylist.first();
           int number = longNumber.intValue();
           String instrument = MIDIBeast.spacelessDrumNameFromNumber(number);
           //Polylist patternProper = subpattern.rest().rest();
           DrumRuleDisplay rule = new DrumRuleDisplay(pattern.toStringSansParens(), instrument, styleEditor.getNotate(), null, styleEditor);
           //dpd.addRule(rule);
           //polylist = polylist.rest();
           rule.playMe();
           break;
     }
  }

public Style makeTempStyle()
{
    StringBuilder buffer = new StringBuilder();
    //convert all the patterns to a polylist
    for( int i = 0; i < bassPatternNames.size(); i++ )
    {
        String key = bassPatternNames.get(i);
        String pattern = bassRules.get(key);
        Boolean useValue = (Boolean)bassTable.getValueAt(i, USE);
        //System.out.println(useString);
        if( useValue )
        {
            buffer.append("(bass-pattern ");
            buffer.append("(rules ");
            buffer.append(pattern);
            buffer.append(")(weight 10.0))");
        }      
    }
    
    for( int j = 0; j < chordPatternNames.size(); j++ )
    {
        String key = chordPatternNames.get(j);
        String pattern = chordRules.get(key);
        Boolean useValue = (Boolean)chordTable.getValueAt(j, USE);
        if( useValue )
        {
            buffer.append("(chord-pattern ");
            buffer.append("(rules ");
            buffer.append(pattern);
            buffer.append(")(weight 10.0))");
        } 
    }
    
    for( int k = 0; k < drumPatterns.size(); k++ )
    {
        String key = drumPatternNames.get(k);
        buffer.append("(drum-pattern ");
        ArrayList<DrumRuleRep> drumRules = drumPatterns.get(key);
        
        for( DrumRuleRep rep: drumRules )
        {
            //System.out.println("drum rule: " + rep);
            int repIndex = ruleIndex.get(rep);
            Boolean useValue = (Boolean)drumTable.getValueAt(k, USE);
            if( useValue )
            {
                buffer.append(rep.toString().trim());
            }
        }
        buffer.append(")");
    }
    String styleString = buffer.toString();
    Polylist style = Polylist.PolylistFromString(styleString);
    Style tempStyle = Style.makeStyle(style);
    return tempStyle;
}

/**
 * Checks to see if model contains p, using String equivalence as a basis
 * of comparison.
 * @param model
 * @param p
 * @return 
 */
/*
boolean containsAsString(DefaultListModel model, Polylist p)
  {
    String s = p.toString();
    for( Enumeration e = model.elements(); e.hasMoreElements(); )
      {
        if( s.equals(e.nextElement().toString() ))
          {
            return true;
          }
      }
    return false;
  }
  */
}
