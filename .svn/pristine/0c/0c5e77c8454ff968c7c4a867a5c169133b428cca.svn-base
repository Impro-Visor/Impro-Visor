/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
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
package imp.gui;

import imp.ImproVisor;
import imp.com.PasteCommand;
import imp.com.PlayScoreCommand;
import imp.data.ChordPart;
import imp.data.MelodyInContext;
import imp.data.MelodyPart;
import static imp.gui.UnsavedChanges.Value.CANCEL;
import static imp.gui.UnsavedChanges.Value.NO;
import static imp.gui.UnsavedChanges.Value.YES;
import imp.lickgen.LickGen;
import imp.lickgen.transformations.Substitution;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.Transformation;
import imp.util.ErrorLog;
import imp.util.GrammarFilter;
import imp.util.Preferences;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import polya.Polylist;

/**
 * Panel that displays a transform and functions that use it. Is used in
 * LickgenFrame.
 * 
 * @author Alex Putman
 */
public class TransformPanel extends javax.swing.JPanel {

    /**
     * The extension for transform files
     */
    public final String EXTENSION = ".transform";
    
    /**
     * The notate that leadsheets are taken from
     */
    private Notate notate;
    
    /**
     * The transform being displayed in this panel
     */
    private Transform transform;
    
    /**
     * The row of the substitution being edited (name editing)
     */
    private int editSubstitutionRow;
    
    /**
     * File chooser used to save and open transform files
     */
    private JFileChooser chooser;
    
    /**
     * Filename of the currently open transform
     */
    private String filename;
    
    /**
     * Creates new form SubstitutorTabPanel
     */
    public TransformPanel(Notate notate) {
        this.notate = notate;
        initComponents();
        
        // No substitution row is being edited at first
        editSubstitutionRow = -1; 
        
        // set the editor and renderer for the substitution table
        subJTable.setTableHeader(null);
        subJTable.setRowHeight(36);
        subJTable.setDefaultRenderer(Object.class, new SubstitutionCellRenderer());
        subJTable.setDefaultEditor(Object.class, new SubstitutionCellEditor());
        
        // set the editor and renderer for the transformation table
        transJTable.setTableHeader(null);
        transJTable.setRowHeight(36);
        transJTable.setDefaultRenderer(Object.class, new TransformationCellRenderer());
        transJTable.setDefaultEditor(Object.class, new TransformationCellEditor());
        
        // set the file chooser and add the detection of overriding a file
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
        
        chooser.setCurrentDirectory(ImproVisor.getGrammarDirectory());
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Transform Files",
                                                                     "transform");
        chooser.setFileFilter(filter);
        
        
        // Tries to set the transform as the default transform for the user
        // (as in My.transform)
        setDefaultTrans();

    }
    
    /**
     * Tries to open the default transform "My.transform".
     */
    private void setDefaultTrans()
    {
        filename = Preferences.DVF_TRANSFORM_VAL;
        String transformStr = "";
        try {
            transformStr = new Scanner(ImproVisor.getTransformFile()).useDelimiter("\\Z").next();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TransformPanel.class.getName()).log(Level.WARNING, null, ex);
        }
        if(transformStr.length() > 0)
        {
            transform = new Transform(transformStr);
        }
        else
        {
            transform = new Transform();
        }
        
        redrawSubstitutionsList();
        redrawTransformationsList();
        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
        cleanTransformButton.setEnabled(true);
        
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

        transformationsFunctionDialogue = new javax.swing.JDialog();
        functionTypeSelectionPanel = new javax.swing.JPanel();
        booleanReturnButton = new javax.swing.JButton();
        noteReturnButton = new javax.swing.JButton();
        otherReturnButton = new javax.swing.JButton();
        anyReturnButton = new javax.swing.JButton();
        documentationPane = new javax.swing.JScrollPane();
        documentationPanel = new javax.swing.JPanel();
        functionNamePane = new javax.swing.JScrollPane();
        functionNamePanel = new javax.swing.JPanel();
        selectSubstitutionsButtonsPanel = new javax.swing.JPanel();
        createNewSubstitutionsFileButton = new javax.swing.JButton();
        openSubstitutionsFileButton = new javax.swing.JButton();
        saveSubstitutionsButton = new javax.swing.JButton();
        SubstitutorParametersPanel = new javax.swing.JPanel();
        substitutorRectifyCheckBox = new javax.swing.JCheckBox();
        enforceDurationCheckBox = new javax.swing.JCheckBox();
        useSubstitutionsButtonsPanel = new javax.swing.JPanel();
        applySubstitutionsButton = new javax.swing.JButton();
        revertSubstitutionsButton = new javax.swing.JButton();
        reapplySubstitutionsButton = new javax.swing.JButton();
        cleanTransformButton = new javax.swing.JButton();
        substitutionsPanel = new javax.swing.JPanel();
        addSubsFromOtherFileButton = new javax.swing.JButton();
        createNewSubstitutionButton = new javax.swing.JButton();
        editSubstitutionNameButton = new javax.swing.JButton();
        deleteSubstitutionButton = new javax.swing.JButton();
        substitutionFromLabel = new javax.swing.JLabel();
        subsScrollPane = new javax.swing.JScrollPane();
        subJTable = new javax.swing.JTable();
        totalMotifWeightsPanel = new javax.swing.JPanel();
        motifTotalLabel = new javax.swing.JLabel();
        scaleMotifWeightsButton = new javax.swing.JButton();
        motifTotalWeightValueLabel = new javax.swing.JLabel();
        totalEmbWeightsPanel = new javax.swing.JPanel();
        scaleEmbWeightsButton = new javax.swing.JButton();
        embTotalLabel = new javax.swing.JLabel();
        embTotalWeightValueLabel = new javax.swing.JLabel();
        transformationsPanel = new javax.swing.JPanel();
        transformationSubstitutionNameLabel = new javax.swing.JLabel();
        createNewTransformationButton = new javax.swing.JButton();
        editSelectedTransformationButton = new javax.swing.JButton();
        deleteTransformationButton = new javax.swing.JButton();
        transScrollPane = new javax.swing.JScrollPane();
        transJTable = new javax.swing.JTable();
        totalTransformationsWeightsPanel = new javax.swing.JPanel();
        scaleTransWeightsButton = new javax.swing.JButton();
        transTotalWeightValueLabel = new javax.swing.JLabel();
        transTotalLabel = new javax.swing.JLabel();
        transformationsFunctionsPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        playbackPanel = new javax.swing.JPanel();
        substitutorPlayLeadsheetButton = new javax.swing.JButton();
        substitutorStopLeadsheetButton = new javax.swing.JButton();
        substitutorSaveLeadsheetButton = new javax.swing.JButton();

        transformationsFunctionDialogue.setTitle("Transformation Functions");
        transformationsFunctionDialogue.getContentPane().setLayout(new java.awt.GridBagLayout());

        functionTypeSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Function Return Types", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        functionTypeSelectionPanel.setMinimumSize(new java.awt.Dimension(175, 61));
        functionTypeSelectionPanel.setPreferredSize(new java.awt.Dimension(175, 61));
        functionTypeSelectionPanel.setLayout(new java.awt.GridBagLayout());

        booleanReturnButton.setText("Boolean");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        functionTypeSelectionPanel.add(booleanReturnButton, gridBagConstraints);

        noteReturnButton.setText("Note");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 9, 0);
        functionTypeSelectionPanel.add(noteReturnButton, gridBagConstraints);

        otherReturnButton.setText("Other / Not Determinable");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        functionTypeSelectionPanel.add(otherReturnButton, gridBagConstraints);

        anyReturnButton.setText("All");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(9, 0, 0, 0);
        functionTypeSelectionPanel.add(anyReturnButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        transformationsFunctionDialogue.getContentPane().add(functionTypeSelectionPanel, gridBagConstraints);

        documentationPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Function Documentation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        documentationPane.setViewportView(documentationPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        transformationsFunctionDialogue.getContentPane().add(documentationPane, gridBagConstraints);

        functionNamePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Function Names", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        functionNamePane.setViewportView(functionNamePanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        transformationsFunctionDialogue.getContentPane().add(functionNamePane, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        selectSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Transformation Class List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        selectSubstitutionsButtonsPanel.setMinimumSize(new java.awt.Dimension(230, 130));
        selectSubstitutionsButtonsPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        selectSubstitutionsButtonsPanel.setLayout(new java.awt.GridBagLayout());

        createNewSubstitutionsFileButton.setText("Create New Transform File");
        createNewSubstitutionsFileButton.setToolTipText("Create a new empty transform file with no classes.");
        createNewSubstitutionsFileButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        createNewSubstitutionsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewSubstitutionsFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(createNewSubstitutionsFileButton, gridBagConstraints);

        openSubstitutionsFileButton.setText("Open Transform File");
        openSubstitutionsFileButton.setToolTipText("Open transform file from grammars folder.");
        openSubstitutionsFileButton.setAutoscrolls(true);
        openSubstitutionsFileButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        openSubstitutionsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSubstitutionsFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(openSubstitutionsFileButton, gridBagConstraints);

        saveSubstitutionsButton.setText("Save Current Transform");
        saveSubstitutionsButton.setToolTipText("Save the transformations below into a file.");
        saveSubstitutionsButton.setEnabled(false);
        saveSubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        saveSubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selectSubstitutionsButtonsPanel.add(saveSubstitutionsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(selectSubstitutionsButtonsPanel, gridBagConstraints);

        SubstitutorParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transform Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        SubstitutorParametersPanel.setMinimumSize(new java.awt.Dimension(180, 60));
        SubstitutorParametersPanel.setPreferredSize(new java.awt.Dimension(180, 60));
        SubstitutorParametersPanel.setLayout(new java.awt.GridBagLayout());

        substitutorRectifyCheckBox.setSelected(true);
        substitutorRectifyCheckBox.setText("Rectify");
        substitutorRectifyCheckBox.setToolTipText("rectify selection after applying substitutions");
        substitutorRectifyCheckBox.setMaximumSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.setMinimumSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.setPreferredSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorRectifyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        SubstitutorParametersPanel.add(substitutorRectifyCheckBox, gridBagConstraints);

        enforceDurationCheckBox.setSelected(true);
        enforceDurationCheckBox.setText("Enforce Duration Equality");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        SubstitutorParametersPanel.add(enforceDurationCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(SubstitutorParametersPanel, gridBagConstraints);

        useSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Use Transformation Classes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        useSubstitutionsButtonsPanel.setMinimumSize(new java.awt.Dimension(230, 130));
        useSubstitutionsButtonsPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        useSubstitutionsButtonsPanel.setLayout(new java.awt.GridBagLayout());

        applySubstitutionsButton.setText("Transform Melody");
        applySubstitutionsButton.setToolTipText("Apply the transformation classes below to the selected melody.");
        applySubstitutionsButton.setEnabled(false);
        applySubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        applySubstitutionsButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                applySubstitutionsButtonStateChanged(evt);
            }
        });
        applySubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        useSubstitutionsButtonsPanel.add(applySubstitutionsButton, gridBagConstraints);

        revertSubstitutionsButton.setText("Revert Application");
        revertSubstitutionsButton.setToolTipText("Undo transformations that were applied.\n");
        revertSubstitutionsButton.setEnabled(false);
        revertSubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        revertSubstitutionsButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                revertSubstitutionsButtonStateChanged(evt);
            }
        });
        revertSubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertSubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        useSubstitutionsButtonsPanel.add(revertSubstitutionsButton, gridBagConstraints);

        reapplySubstitutionsButton.setText("Re-Apply");
        reapplySubstitutionsButton.setToolTipText("Revert transformations and Apply again.");
        reapplySubstitutionsButton.setEnabled(false);
        reapplySubstitutionsButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                reapplySubstitutionsButtonStateChanged(evt);
            }
        });
        reapplySubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reapplySubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        useSubstitutionsButtonsPanel.add(reapplySubstitutionsButton, gridBagConstraints);

        cleanTransformButton.setText("Clean Transform File");
        cleanTransformButton.setToolTipText("Remove duplicate classes, but add their weights together.");
        cleanTransformButton.setEnabled(false);
        cleanTransformButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        cleanTransformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanTransformButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        useSubstitutionsButtonsPanel.add(cleanTransformButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(useSubstitutionsButtonsPanel, gridBagConstraints);

        substitutionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transform Classes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        substitutionsPanel.setMinimumSize(new java.awt.Dimension(490, 500));
        substitutionsPanel.setPreferredSize(new java.awt.Dimension(490, 500));
        substitutionsPanel.setLayout(new java.awt.GridBagLayout());

        addSubsFromOtherFileButton.setText("Add Classes From Other File");
        addSubsFromOtherFileButton.setToolTipText("Add all the classes in another transform file to the current set.");
        addSubsFromOtherFileButton.setEnabled(false);
        addSubsFromOtherFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubsFromOtherFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        substitutionsPanel.add(addSubsFromOtherFileButton, gridBagConstraints);

        createNewSubstitutionButton.setText("Create New Transform Class");
        createNewSubstitutionButton.setToolTipText("Create a new class with no transformations that will be added to the currrent set.");
        createNewSubstitutionButton.setEnabled(false);
        createNewSubstitutionButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        createNewSubstitutionButton.setMaximumSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.setMinimumSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.setPreferredSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewSubstitutionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        substitutionsPanel.add(createNewSubstitutionButton, gridBagConstraints);

        editSubstitutionNameButton.setText("Edit Transform Class Name");
        editSubstitutionNameButton.setToolTipText("Edit the name of a class.");
        editSubstitutionNameButton.setEnabled(false);
        editSubstitutionNameButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        editSubstitutionNameButton.setMaximumSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.setMinimumSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.setPreferredSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSubstitutionNameButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        substitutionsPanel.add(editSubstitutionNameButton, gridBagConstraints);

        deleteSubstitutionButton.setText("Delete Class");
        deleteSubstitutionButton.setToolTipText("Delete the selected class from the set.");
        deleteSubstitutionButton.setEnabled(false);
        deleteSubstitutionButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        deleteSubstitutionButton.setMaximumSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.setMinimumSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.setPreferredSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSubstitutionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 7);
        substitutionsPanel.add(deleteSubstitutionButton, gridBagConstraints);

        substitutionFromLabel.setText("Transform Classes From: ");
        substitutionFromLabel.setMaximumSize(new java.awt.Dimension(10000, 20));
        substitutionFromLabel.setMinimumSize(new java.awt.Dimension(300, 20));
        substitutionFromLabel.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        substitutionsPanel.add(substitutionFromLabel, gridBagConstraints);

        subsScrollPane.setMaximumSize(new java.awt.Dimension(465, 32767));
        subsScrollPane.setMinimumSize(new java.awt.Dimension(465, 402));
        subsScrollPane.setPreferredSize(new java.awt.Dimension(465, 402));

        subJTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        subJTable.setModel(new javax.swing.table.AbstractTableModel() {

            Object[] subs = new Object[0];
            public Class getColumnClass(int columnIndex)
            {
                return Substitution.class;
            }
            public int getRowCount()
            {
                return subs.length;
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Substitution getValueAt(int rowIndex, int columnIndex) {
                return (Substitution)subs[columnIndex];
            }
        });
        subJTable.setMaximumSize(new java.awt.Dimension(100000, 1000000));
        subJTable.setMinimumSize(new java.awt.Dimension(360, 450));
        subJTable.getTableHeader().setReorderingAllowed(false);
        subJTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                subJTableFocusGained(evt);
            }
        });
        subsScrollPane.setViewportView(subJTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 0, 6);
        substitutionsPanel.add(subsScrollPane, gridBagConstraints);

        totalMotifWeightsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Motif Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        totalMotifWeightsPanel.setMinimumSize(new java.awt.Dimension(200, 51));
        totalMotifWeightsPanel.setLayout(new java.awt.GridBagLayout());

        motifTotalLabel.setText("Total: ");
        motifTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        motifTotalLabel.setMinimumSize(new java.awt.Dimension(50, 20));
        motifTotalLabel.setPreferredSize(new java.awt.Dimension(50, 20));
        totalMotifWeightsPanel.add(motifTotalLabel, new java.awt.GridBagConstraints());

        scaleMotifWeightsButton.setText("Scale All");
        scaleMotifWeightsButton.setToolTipText("scale all the weights of substitutions currently labeled as motifs");
        scaleMotifWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleMotifWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        totalMotifWeightsPanel.add(scaleMotifWeightsButton, gridBagConstraints);

        motifTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(60, 20));
        motifTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        totalMotifWeightsPanel.add(motifTotalWeightValueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        substitutionsPanel.add(totalMotifWeightsPanel, gridBagConstraints);

        totalEmbWeightsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Embellishment Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        totalEmbWeightsPanel.setMinimumSize(new java.awt.Dimension(200, 51));
        totalEmbWeightsPanel.setLayout(new java.awt.GridBagLayout());

        scaleEmbWeightsButton.setText("Scale All");
        scaleEmbWeightsButton.setToolTipText("scale all the weights of substitutions currently labeled as embellishments");
        scaleEmbWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleEmbWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        totalEmbWeightsPanel.add(scaleEmbWeightsButton, gridBagConstraints);

        embTotalLabel.setText("Total: ");
        embTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        embTotalLabel.setMinimumSize(new java.awt.Dimension(50, 20));
        embTotalLabel.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        totalEmbWeightsPanel.add(embTotalLabel, gridBagConstraints);

        embTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(60, 20));
        embTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        totalEmbWeightsPanel.add(embTotalWeightValueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        substitutionsPanel.add(totalEmbWeightsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(substitutionsPanel, gridBagConstraints);

        transformationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        transformationsPanel.setMinimumSize(new java.awt.Dimension(490, 500));
        transformationsPanel.setPreferredSize(new java.awt.Dimension(490, 500));
        transformationsPanel.setLayout(new java.awt.GridBagLayout());

        transformationSubstitutionNameLabel.setText("For Transformation Class:");
        transformationSubstitutionNameLabel.setMaximumSize(new java.awt.Dimension(400, 20));
        transformationSubstitutionNameLabel.setMinimumSize(new java.awt.Dimension(400, 20));
        transformationSubstitutionNameLabel.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 2);
        transformationsPanel.add(transformationSubstitutionNameLabel, gridBagConstraints);

        createNewTransformationButton.setText("Create New Transformation");
        createNewTransformationButton.setToolTipText("Create a new empty transform that will be added to the selected class.");
        createNewTransformationButton.setEnabled(false);
        createNewTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        createNewTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        transformationsPanel.add(createNewTransformationButton, gridBagConstraints);

        editSelectedTransformationButton.setText("Edit Transformation");
        editSelectedTransformationButton.setToolTipText("Edit the grammar for the selected transform.");
        editSelectedTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        editSelectedTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSelectedTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        transformationsPanel.add(editSelectedTransformationButton, gridBagConstraints);

        deleteTransformationButton.setText("Delete Transformation");
        deleteTransformationButton.setToolTipText("Delete the selected transform from the selected class.");
        deleteTransformationButton.setEnabled(false);
        deleteTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        deleteTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 7);
        transformationsPanel.add(deleteTransformationButton, gridBagConstraints);

        transScrollPane.setMaximumSize(new java.awt.Dimension(465, 32767));
        transScrollPane.setMinimumSize(new java.awt.Dimension(465, 402));
        transScrollPane.setPreferredSize(new java.awt.Dimension(465, 402));

        transJTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        transJTable.setModel(new javax.swing.table.AbstractTableModel() {

            Object[] subs = new Object[0];
            public Class getColumnClass(int columnIndex)
            {
                return Transformation.class;
            }
            public int getRowCount()
            {
                return subs.length;
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                return subs[columnIndex];
            }
        });
        transJTable.setMaximumSize(new java.awt.Dimension(10000, 100000));
        transJTable.setMinimumSize(new java.awt.Dimension(360, 450));
        transScrollPane.setViewportView(transJTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 0, 6);
        transformationsPanel.add(transScrollPane, gridBagConstraints);

        totalTransformationsWeightsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformation Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        totalTransformationsWeightsPanel.setMinimumSize(new java.awt.Dimension(190, 51));
        totalTransformationsWeightsPanel.setPreferredSize(new java.awt.Dimension(190, 51));
        totalTransformationsWeightsPanel.setLayout(new java.awt.GridBagLayout());

        scaleTransWeightsButton.setText("Scale All");
        scaleTransWeightsButton.setToolTipText("scale all the weights of transformations in the currently selected substitution");
        scaleTransWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleTransWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        totalTransformationsWeightsPanel.add(scaleTransWeightsButton, gridBagConstraints);

        transTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(60, 20));
        transTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        totalTransformationsWeightsPanel.add(transTotalWeightValueLabel, gridBagConstraints);

        transTotalLabel.setText("Total: ");
        transTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        transTotalLabel.setMinimumSize(new java.awt.Dimension(50, 20));
        transTotalLabel.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        totalTransformationsWeightsPanel.add(transTotalLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        transformationsPanel.add(totalTransformationsWeightsPanel, gridBagConstraints);

        transformationsFunctionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformation Functions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        transformationsFunctionsPanel.setMinimumSize(new java.awt.Dimension(80, 51));
        transformationsFunctionsPanel.setPreferredSize(new java.awt.Dimension(80, 51));
        transformationsFunctionsPanel.setLayout(new java.awt.GridBagLayout());

        jButton1.setText("Show Function Documentation");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        transformationsFunctionsPanel.add(jButton1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        transformationsPanel.add(transformationsFunctionsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(transformationsPanel, gridBagConstraints);

        playbackPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LeadSheet Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        playbackPanel.setMinimumSize(new java.awt.Dimension(187, 130));
        playbackPanel.setPreferredSize(new java.awt.Dimension(187, 130));
        playbackPanel.setLayout(new java.awt.GridBagLayout());

        substitutorPlayLeadsheetButton.setText("Play Selection");
        substitutorPlayLeadsheetButton.setToolTipText("Play the selected melody in the leadsheet.");
        substitutorPlayLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorPlayLeadsheetButton.setMinimumSize(new java.awt.Dimension(153, 23));
        substitutorPlayLeadsheetButton.setPreferredSize(new java.awt.Dimension(153, 23));
        substitutorPlayLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorPlayLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorPlayLeadsheetButton, gridBagConstraints);

        substitutorStopLeadsheetButton.setText("Stop Playback");
        substitutorStopLeadsheetButton.setToolTipText("Stop leadsheet playback.");
        substitutorStopLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorStopLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorStopLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorStopLeadsheetButton, gridBagConstraints);

        substitutorSaveLeadsheetButton.setText("Save");
        substitutorSaveLeadsheetButton.setToolTipText("Save current lick.");
        substitutorSaveLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorSaveLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorSaveLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorSaveLeadsheetButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(playbackPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cleanTransformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanTransformButtonActionPerformed
        transform.cleanSubs();
        transform.findDuplicatesAndAddToWeight();
        redrawSubstitutionsList();
        redrawTransformationsList();
    }//GEN-LAST:event_cleanTransformButtonActionPerformed

    private void openSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSubstitutionsFileButtonActionPerformed
        
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            String newFilename = chooser.getSelectedFile().getName();
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TransformPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                Transform newTrans = new Transform(transformStr);
                changeTransform(newTrans, newFilename);
            }
        }
    }//GEN-LAST:event_openSubstitutionsFileButtonActionPerformed

    private void substitutorRectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorRectifyCheckBoxActionPerformed

    }//GEN-LAST:event_substitutorRectifyCheckBoxActionPerformed

    private void applySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySubstitutionsButtonActionPerformed
        // TODO add your handling code here:
        applySubstitutions();
    }//GEN-LAST:event_applySubstitutionsButtonActionPerformed

    private void revertSubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertSubstitutionsButtonActionPerformed
        revertSubs();
    }//GEN-LAST:event_revertSubstitutionsButtonActionPerformed

    private void createNewSubstitutionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewSubstitutionButtonActionPerformed
        transform.addNewSubstitution();
        redrawSubstitutionsList();
        redrawTransformationsList();
    }//GEN-LAST:event_createNewSubstitutionButtonActionPerformed

    private void editSubstitutionNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSubstitutionNameButtonActionPerformed
        if(editSubstitutionRow < 0)
        {
            int editIndex = subJTable.getEditingRow();
            if(editIndex >= 0)
            {
                editSubstitutionRow = editIndex;
                subJTable.setEditingRow(-1);
                redrawSubstitutionsList();
                redrawTransformationsList();
                editSubstitutionNameButton.setText("Save Transformation Class Name");
            }
        }
        else
        {

            resetEditNameButton();
            redrawSubstitutionsList();
            redrawTransformationsList();
        }

    }//GEN-LAST:event_editSubstitutionNameButtonActionPerformed

    private void deleteSubstitutionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSubstitutionButtonActionPerformed
        int editRow = subJTable.getEditingRow();
        if(editRow >= 0)
        {
            Object toDelete = subJTable.getValueAt(editRow, 0);
            transform.removeSubstitution((Substitution)toDelete);

            redrawSubstitutionsList();
            redrawTransformationsList();
        }
    }//GEN-LAST:event_deleteSubstitutionButtonActionPerformed

    private void subJTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_subJTableFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_subJTableFocusGained

    private void createNewTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewTransformationButtonActionPerformed
        
        Object toAddSub = subJTable.getValueAt(subJTable.getSelectedRow(), 0);
        if(toAddSub != null)
        {
            Substitution subToAddTo = (Substitution) toAddSub;
            subToAddTo.addNewTransformation();
        }
        redrawTransformationsList();
    }//GEN-LAST:event_createNewTransformationButtonActionPerformed

    private void editSelectedTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSelectedTransformationButtonActionPerformed
        int editTransRow = transJTable.getEditingRow();
        if(editTransRow >= 0)
        {
            Transformation currentTrans = (Transformation)transJTable.getValueAt(editTransRow, 0);

            TransformationDialogue transEditor = new TransformationDialogue(notate.lickgenFrame, currentTrans);
            transEditor.setLocationRelativeTo(this);
            transEditor.toFront();
        }
    }//GEN-LAST:event_editSelectedTransformationButtonActionPerformed

    private void deleteTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTransformationButtonActionPerformed
        int editSubRow = subJTable.getEditingRow();
        if(editSubRow >= 0)
        {
            Object toDeleteSub = subJTable.getValueAt(editSubRow, 0);
            int editTransRow = transJTable.getEditingRow();
            if(editTransRow >= 0)
            {
                Object toDelete = transJTable.getValueAt(editTransRow, 0);
                ((Substitution)toDeleteSub).removeTransformation((Transformation)toDelete);

                redrawTransformationsList();
            }

        }
    }//GEN-LAST:event_deleteTransformationButtonActionPerformed

    private void substitutorPlayLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorPlayLeadsheetButtonActionPerformed
        Stave stave = notate.getCurrentStave();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        notate.playCurrentSelection(false, 
                                    0, 
                                    PlayScoreCommand.USEDRUMS, 
                                    "putLick " + start + " - " + stop);
        ImproVisor.setPlayEntrySounds(true);
    }//GEN-LAST:event_substitutorPlayLeadsheetButtonActionPerformed

    private void substitutorStopLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorStopLeadsheetButtonActionPerformed
        notate.stopPlaying();
    }//GEN-LAST:event_substitutorStopLeadsheetButtonActionPerformed

    private void substitutorSaveLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorSaveLeadsheetButtonActionPerformed
        notate.setLickTitle("<Generated Lick>");

        notate.openSaveLickFrame();
    }//GEN-LAST:event_substitutorSaveLeadsheetButtonActionPerformed

    private void saveSubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSubstitutionsButtonActionPerformed
        saveCurrentTransform();
    }//GEN-LAST:event_saveSubstitutionsButtonActionPerformed

    private void createNewSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewSubstitutionsFileButtonActionPerformed
        Transform newTrans = Transform.makeTransformWithIdentities();
        String newFilename = "newTransformFile.transform";
        changeTransform(newTrans, newFilename);
    }//GEN-LAST:event_createNewSubstitutionsFileButtonActionPerformed

    private void addSubsFromOtherFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubsFromOtherFileButtonActionPerformed
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TransformPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                Transform addTransform = new Transform(transformStr);
                for(Substitution sub: addTransform.substitutions)
                {
                    transform.addSubstitution(sub);
                }
                transform.hasChanged = true;
                redrawSubstitutionsList();
                redrawTransformationsList();
            }
        }
    }//GEN-LAST:event_addSubsFromOtherFileButtonActionPerformed

    private void reapplySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reapplySubstitutionsButtonActionPerformed
        revertSubs();
        applySubstitutions();
    }//GEN-LAST:event_reapplySubstitutionsButtonActionPerformed

    private void scaleMotifWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleMotifWeightsButtonActionPerformed
        ScaleMotifWeightsDialogue scale = new ScaleMotifWeightsDialogue(notate.lickgenFrame, transform);
    }//GEN-LAST:event_scaleMotifWeightsButtonActionPerformed

    private void scaleEmbWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleEmbWeightsButtonActionPerformed
        ScaleEmbWeightsDialogue scale = new ScaleEmbWeightsDialogue(notate.lickgenFrame, transform);
    }//GEN-LAST:event_scaleEmbWeightsButtonActionPerformed

    private void scaleTransWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleTransWeightsButtonActionPerformed
        int selectedSubRow = subJTable.getEditingRow();
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 
                                                                                 0);
            ScaleTransWeightsDialogue scale = new ScaleTransWeightsDialogue(notate.lickgenFrame, 
                                                                            selectedSub);
        }
    }//GEN-LAST:event_scaleTransWeightsButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        notate.openHelpDialog();
        notate.helpDialog.showTransformationDocs();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void applySubstitutionsButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_applySubstitutionsButtonStateChanged
        if(notate.guideToneLineDialog!=null){
           notate.guideToneLineDialog.updateTransformButtons(); 
        }
    }//GEN-LAST:event_applySubstitutionsButtonStateChanged

    private void revertSubstitutionsButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_revertSubstitutionsButtonStateChanged
        if(notate.guideToneLineDialog!=null){
           notate.guideToneLineDialog.updateTransformButtons(); 
        }
    }//GEN-LAST:event_revertSubstitutionsButtonStateChanged

    private void reapplySubstitutionsButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_reapplySubstitutionsButtonStateChanged
        if(notate.guideToneLineDialog!=null){
           notate.guideToneLineDialog.updateTransformButtons(); 
        }
    }//GEN-LAST:event_reapplySubstitutionsButtonStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SubstitutorParametersPanel;
    private javax.swing.JButton addSubsFromOtherFileButton;
    private javax.swing.JButton anyReturnButton;
    private javax.swing.JButton applySubstitutionsButton;
    private javax.swing.JButton booleanReturnButton;
    private javax.swing.JButton cleanTransformButton;
    private javax.swing.JButton createNewSubstitutionButton;
    private javax.swing.JButton createNewSubstitutionsFileButton;
    private javax.swing.JButton createNewTransformationButton;
    private javax.swing.JButton deleteSubstitutionButton;
    private javax.swing.JButton deleteTransformationButton;
    private javax.swing.JScrollPane documentationPane;
    private javax.swing.JPanel documentationPanel;
    private javax.swing.JButton editSelectedTransformationButton;
    private javax.swing.JButton editSubstitutionNameButton;
    private javax.swing.JLabel embTotalLabel;
    private javax.swing.JLabel embTotalWeightValueLabel;
    private javax.swing.JCheckBox enforceDurationCheckBox;
    private javax.swing.JScrollPane functionNamePane;
    private javax.swing.JPanel functionNamePanel;
    private javax.swing.JPanel functionTypeSelectionPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel motifTotalLabel;
    private javax.swing.JLabel motifTotalWeightValueLabel;
    private javax.swing.JButton noteReturnButton;
    private javax.swing.JButton openSubstitutionsFileButton;
    private javax.swing.JButton otherReturnButton;
    private javax.swing.JPanel playbackPanel;
    private javax.swing.JButton reapplySubstitutionsButton;
    private javax.swing.JButton revertSubstitutionsButton;
    private javax.swing.JButton saveSubstitutionsButton;
    private javax.swing.JButton scaleEmbWeightsButton;
    private javax.swing.JButton scaleMotifWeightsButton;
    private javax.swing.JButton scaleTransWeightsButton;
    private javax.swing.JPanel selectSubstitutionsButtonsPanel;
    private javax.swing.JTable subJTable;
    private javax.swing.JScrollPane subsScrollPane;
    private javax.swing.JLabel substitutionFromLabel;
    private javax.swing.JPanel substitutionsPanel;
    private javax.swing.JButton substitutorPlayLeadsheetButton;
    private javax.swing.JCheckBox substitutorRectifyCheckBox;
    private javax.swing.JButton substitutorSaveLeadsheetButton;
    private javax.swing.JButton substitutorStopLeadsheetButton;
    private javax.swing.JPanel totalEmbWeightsPanel;
    private javax.swing.JPanel totalMotifWeightsPanel;
    private javax.swing.JPanel totalTransformationsWeightsPanel;
    private javax.swing.JTable transJTable;
    private javax.swing.JScrollPane transScrollPane;
    private javax.swing.JLabel transTotalLabel;
    private javax.swing.JLabel transTotalWeightValueLabel;
    private javax.swing.JLabel transformationSubstitutionNameLabel;
    private javax.swing.JDialog transformationsFunctionDialogue;
    private javax.swing.JPanel transformationsFunctionsPanel;
    private javax.swing.JPanel transformationsPanel;
    private javax.swing.JPanel useSubstitutionsButtonsPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Pastes source melody over dest melody at startingSlot. This calls the 
     * PasteCommand so that undo and redo can be used in notate. 
     * @param dest
     * @param source
     * @param startingSlot 
     */
    public void pasteOver(MelodyPart dest, MelodyPart source, int startingSlot)
    {
        PasteCommand paste = new PasteCommand(source,dest,startingSlot,false);
        notate.cm.execute(paste);
    }
    
    /**
     * Try to change the current transform and if there are unsaved changes then
     * ask if they want to save. 
     * @param transform
     * @param newFilename 
     */
    public void changeTransform(Transform transform, String newFilename)
    {
      

        boolean redisplay = true;

        while( redisplay )
          {
          redisplay = false;

          if( unsavedChanges() )
            {

            Object[] options =
              {
              "<html><b><u>Y</u>es</b>, save modifications.</html>",
              "<html><b><u>N</u>o</b>, do not save modifications.</html>",
              "<html><b>Cancel</b>, do not close this transform.</html>"
              };

            UnsavedChanges dialog = new UnsavedChanges(notate.lickgenFrame,
                    "Save changes to transform before changing?", options);

            dialog.setVisible(true);

            dialog.dispose();

            UnsavedChanges.Value choice = dialog.getValue();

            switch( choice )
              {
              case YES:

                if( !saveCurrentTransform() )
                  {
                  redisplay = true;
                  }
                break;

              case NO:

                break;

              case CANCEL:

                return;
              }
            }
          }

        this.transform = transform;
        resetEditNameButton();
        redrawSubstitutionsList();
        redrawTransformationsList();
        
        
        filename = newFilename;
        substitutionFromLabel.setText("Transformation Classes From: " + filename);
        
        revertSubstitutionsButton.setEnabled(false);
        reapplySubstitutionsButton.setEnabled(false);

        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
        cleanTransformButton.setEnabled(true);
    }
    
    /**
     * Tries to save the current transform by bringing up a file saver dialogue
     * @return whether the save went through or not
     */
    private boolean saveCurrentTransform()
    {
        chooser.setSelectedFile(new File(filename));
        
        if( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            if( chooser.getSelectedFile().getName().endsWith(
                EXTENSION) )
              {
                filename = chooser.getSelectedFile().getName();

                saveTransformFile(chooser.getSelectedFile().getAbsolutePath());
              }
            else
              {
                filename = chooser.getSelectedFile().getName() + EXTENSION;

                saveTransformFile(chooser.getSelectedFile().getAbsolutePath() + EXTENSION);
              }
            transform.hasChanged = false;
            substitutionFromLabel.setText("Transfromation Classes From: " + filename);
            return true;
          }
        else
        {
            return false;
        }
    }
    
    /**
     * 
     * @return true if the the current transform has unsaved changes.
     */
    private boolean unsavedChanges()
    {
        return transform.hasChanged();
    }
    
    public boolean enforceDuration()
    {
        return enforceDurationCheckBox.isSelected();
    }
    
    /**
     * If a substitution name is in the process of being edited, the process is
     * stopped. 
     */
    private void resetEditNameButton()
    {
        editSubstitutionRow = -1;
        editSubstitutionNameButton.setText("Edit Transformation Class Name");
    }
    
    /**
     * Tries to change the current transform to a new transform.
     * @param trans 
     */
    public void setTransform(Transform trans)
    {
        // Currently only used for setting learned transforms from transform
        // learning panel. If used for other uses, think of adding a string for
        // new file name which is just being set here. 
        changeTransform(trans, "learnedTransform");
    }
    
    public boolean getRevertEnabled(){
        return revertSubstitutionsButton.isEnabled();
    }
    
    public boolean getReapplyEnabled(){
        return reapplySubstitutionsButton.isEnabled();
    }
    
    /**
     * Apply the current transform to the currently selected melody. 
     */
    public void applySubstitutions()
    {
        if(notate.getChordProg().getChords().isEmpty()){
            return;
        }
        notate.stopPlaying();
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart melody = notate.getCurrentMelodyPart().extract(start,
                                                                  stop,
                                                                  false);
        ChordPart chords = notate.getChordProg().extract(start, stop);
        applySubstitutionsToPart(melody, chords);
    }
    
    public void updateButtons(){
        if(notate.getCurrentMelodyPart().isOriginal()){
            revertSubstitutionsButton.setEnabled(false);
            reapplySubstitutionsButton.setEnabled(false);
        }else{
            revertSubstitutionsButton.setEnabled(true);
            reapplySubstitutionsButton.setEnabled(true);
        }
    }
    
    /**
     * Apply the current transform the melodyPart and ChordPart sent at the
     * location selected in notate.
     * @param melody
     * @param chords 
     */
    public void applySubstitutionsToPart(MelodyPart melody, ChordPart chords)
    {
        
        if(transform != null)
        {
            Stave stave = notate.getCurrentStave();
            int start = notate.getCurrentSelectionStart();
            int stop = notate.getCurrentSelectionEnd();
            notate.getCurrentMelodyPart().pushOriginalVersion(new MelodyInContext(melody.copy(), stave, start, stop));
//            savedMelodies.add(new MelodyInContext(melody.copy(), 
//                                                  stave, 
//                                                  start, 
//                                                  stop));
            
            MelodyPart transformedPart = transform.applySubstitutionsToMelodyPart(melody,
                                                                                  chords,
                                                                                  this);
            

            pasteOver(notate.getMelodyPart(stave), transformedPart, start);
            
            if(substitutorRectifyCheckBox.isSelected())
            {
                notate.rectifySelection(stave,start,stop);
                
            }
            notate.playCurrentSelection(false, 
                                        0, 
                                        PlayScoreCommand.USEDRUMS, 
                                        "putLick " + start + " - " + stop);
            ImproVisor.setPlayEntrySounds(true);
            
            //Current MelodyPart was just transformed - set enabled to true
            revertSubstitutionsButton.setEnabled(true);
            reapplySubstitutionsButton.setEnabled(true);
        }
        
    }
    
    /**
     * Revert the last application of substitutions. 
     */
    public void revertSubs()
    {
        MelodyPart currentPart = notate.getCurrentMelodyPart();
        MelodyInContext originalPart = currentPart.getRecentVersion();
        
        //prevent null pointer exception, don't try to revert an original melody
        if(originalPart==null){
            return;
        }
        //MelodyInContext originalPart = savedMelodies.pop();
        notate.stopPlaying();
        Stave stave = originalPart.getStave();
        int start = originalPart.getStart();
        int stop = originalPart.getStop();
        
        stave.setSelection(start, stop);
        pasteOver(notate.getMelodyPart(stave), originalPart.getMelody(), start);
        
        //if the stack is empty
        if(currentPart.isOriginal()){
           revertSubstitutionsButton.setEnabled(false);
           reapplySubstitutionsButton.setEnabled(false); 
        }
        
    }
    
    /**
     * First resets buttons that are used to edit the substitutions list. Then
     * refills the substitutions table which redraws it. 
     * This can probably be done more efficiently, but currently there are no 
     * performance problems, so no need to rewrite.
     */
    private void redrawSubstitutionsList()
    {
        // if a substitution name is being edited, then only allow that row to
        // be edited and set most buttons to not enabled, so that they have to 
        // save changes to name before continuing.
        if(filename != null && filename.length() > 0)
        {
            substitutionFromLabel.setText("Transformation Classes From: " + filename);
        }
        createNewSubstitutionButton.setEnabled((editSubstitutionRow < 0));
        addSubsFromOtherFileButton.setEnabled((editSubstitutionRow < 0));
        deleteSubstitutionButton.setEnabled(false);
        editSubstitutionNameButton.setEnabled((editSubstitutionRow >= 0));

        deleteTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setEnabled(false);
        createNewTransformationButton.setEnabled(false);
        scaleTransWeightsButton.setEnabled(false);
        
        scaleMotifWeightsButton.setEnabled((editSubstitutionRow < 0));
        scaleEmbWeightsButton.setEnabled((editSubstitutionRow < 0));
        
        setTotalSubWeights();
        
        subJTable.setModel(new javax.swing.table.AbstractTableModel() {
            ArrayList<Substitution> subs = transform.substitutions;
            
            public int getRowCount()
            {
                return subs.size();
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Substitution getValueAt(int rowIndex, int columnIndex) {
                return subs.get(rowIndex);
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                // If a substitution name is being edited, only let that cell
                // be edited. 
                if(editSubstitutionRow >= 0)
                {
                    return (row == editSubstitutionRow);
                }
                else
                    return true;
		}
            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                subs.set(rowIndex, (Substitution)value);
            }
        });
    }
    /**
     * First resets buttons that are used to edit the transformations list. Then
     * refills the transformations table which redraws it. 
     * This can probably be done more efficiently, but currently there are no 
     * performance problems, so no need to rewrite.
     */
    private void redrawTransformationsList()
    {
        deleteTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setEnabled(false);
        
        
        int selectedSubRow = subJTable.getEditingRow();
        String subName = "";
        int totalSubWeight = 0;
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution)subJTable.getValueAt(selectedSubRow, 0);
            subName = selectedSub.getName();

            totalSubWeight = selectedSub.getTotalWeight();
            // resets the transformations table
            transJTable.setModel(new javax.swing.table.AbstractTableModel() {
                ArrayList<Transformation> trans = selectedSub.transformations;


                public int getRowCount()
                {
                    return trans.size();
                }
                public int getColumnCount()
                {
                    return 1;
                }
                public Transformation getValueAt(int rowIndex, int columnIndex) {
                    return trans.get(rowIndex);
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                            return true;
                    }
                @Override
                public void setValueAt(Object value, int rowIndex, int columnIndex) {
                    trans.set(rowIndex, (Transformation)value);
                }
            });
            
        }
        else
        {
            // resets the transformations table
            transJTable.setModel(new javax.swing.table.AbstractTableModel() {
                ArrayList<Transformation> trans = new ArrayList<Transformation>();


                public int getRowCount()
                {
                    return trans.size();
                }
                public int getColumnCount()
                {
                    return 1;
                }
                public Transformation getValueAt(int rowIndex, int columnIndex) {
                    return trans.get(rowIndex);
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                            return true;
                    }
                @Override
                public void setValueAt(Object value, int rowIndex, int columnIndex) {
                    trans.set(rowIndex, (Transformation)value);
                }
            });
        }
        transformationSubstitutionNameLabel.setText("For Transformation Class: " + subName);
        transTotalWeightValueLabel.setText(((totalSubWeight > 0)? 
                                            (totalSubWeight): 
                                            "") + "");
        
    }
    /**
     * Tries to save the current transform to the given filepath string
     * @param filepath                      filename to save transform to
     * @return 0 if save worked, -1 if an error occurred
     */
    private int saveTransformFile(String filepath) {
        try
        {
            StringBuilder content = new StringBuilder();
            transform.toFile(content);
            FileWriter out = new FileWriter(new File(filepath));
            out.write(content.toString());
            out.close();
            transform.hasChanged = false;
            return 0;
        }
        catch( IOException e )
        {
            ErrorLog.log(ErrorLog.WARNING, "Error saving to " + filename);
            return -1;
        }
    }

/**
 * Set the labels that show the total weight for the substitution types
 */
public void setTotalSubWeights()
{
    if(transform != null)
    {
        motifTotalWeightValueLabel.setText(transform.getTotalMotifWeight() +"");
        embTotalWeightValueLabel.setText(transform.getTotalEmbWeight() +"");
    }
    else
    {
        motifTotalWeightValueLabel.setText("");
        embTotalWeightValueLabel.setText("");
    }
}

/**
 * Set the labels that show the total weight for the transformations in the
 * selected substitution. 
 */
public void setTotalTransWeights()
{
        int selectedSubRow = subJTable.getEditingRow();
        int totalSubWeight = 0;
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 0);

            totalSubWeight = selectedSub.getTotalWeight();
            
        }
        transTotalWeightValueLabel.setText(((totalSubWeight > 0)? 
                                            (totalSubWeight): 
                                            "") + "");
}


/* 
********************************************************************************
START OF CUSTOM COMPONENTS FOR SUBSTITUTIONS TABLE
********************************************************************************
*/
    
/**
 * The renderer for each cell of the substitutions table. This should be
 * perfectly fine and coded decently. WARNING: These are pretty complicated, 
 * so if you don't know what you are doing, don't try to change non superficial
 * things. Could be combined with the Editor in the future for ease of changing.
 */
public class SubstitutionCellRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // For grid bag contraints, arguments on the same line are the same type
        // of argument, but represent x and y values. 
        Substitution sub = (Substitution)value;
        
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        // First adding the enabled check box
        SubCheckBox subEnabled = new SubCheckBox(sub);
        if(editSubstitutionRow >= 0)
        {
            subEnabled.setEnabled(false);
        }
        GridBagConstraints subEnabledC = new GridBagConstraints(  0, 0,
                                                                  1, 1,
                                                                  0.0, 0.0,
                                                                  GridBagConstraints.WEST, 
                                                                  GridBagConstraints.NONE, 
                                                                  new Insets(0,0,0,0), 
                                                                  0, 0);
        panel.add(subEnabled, subEnabledC);
        
        // then adding the name label, or if the name is being edited then a
        // text box where the name can be edited
        GridBagConstraints subNameC = new GridBagConstraints( 1, 0,
                                                              1, 1,
                                                              1.0, 0.0,
                                                              GridBagConstraints.WEST, 
                                                              GridBagConstraints.HORIZONTAL, 
                                                              new Insets(0,4,0,0), 
                                                              0, 0);
        if(editSubstitutionRow == row)
        {
            SubNameEditField editName = new SubNameEditField(sub);
            panel.add(editName, subNameC);
        }
        else
        {
            javax.swing.JLabel subName = new javax.swing.JLabel(sub.getName());
            subName.setMinimumSize(new Dimension(190, 25));
            subName.setPreferredSize(new Dimension(190, 25));
            panel.add(subName, subNameC);
        }
        
        // then add the combo box for the type of the substitution
        SubTypeComboBox subTypesList = new SubTypeComboBox(sub);
        if(editSubstitutionRow >= 0)
        {
            subTypesList.setEnabled(false);
        }
        GridBagConstraints subTypesC = new GridBagConstraints(  2, 0,
                                                                1, 1,
                                                                0.0, 0.0,
                                                                GridBagConstraints.WEST, 
                                                                GridBagConstraints.NONE, 
                                                                new Insets(0,0,0,0), 
                                                                0, 0);
        panel.add(subTypesList, subTypesC);
        
        // then add the label the just says that weight
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        
        GridBagConstraints subWeightLabelC = new GridBagConstraints(3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,20,0,0), 
                                                                    0, 0);
        panel.add(weightLabel, subWeightLabelC);
        
        // finally add the customized spinner for the weight of the substitution
        SubWeightField subWeightField = new SubWeightField(sub);
        if(editSubstitutionRow >= 0)
        {
            subWeightField.setEnabled(false);
        }
        GridBagConstraints subWeightC = new GridBagConstraints(4, 0,
                                                               1, 1,
                                                               0.0, 0.0,
                                                               GridBagConstraints.WEST, 
                                                               GridBagConstraints.NONE, 
                                                               new Insets(0,0,0,4), 
                                                               0, 0);
        panel.add(subWeightField, subWeightC);
        
        // set the background of the cell based on its selection or editing
        if ((hasFocus && editSubstitutionRow < 0) || (editSubstitutionRow == row)) {
                panel.setBackground(table.getSelectionBackground());
         }
        else
        {
            if(editSubstitutionRow >= 0)
                panel.setBackground(new Color(240,240,240));
            else
                panel.setBackground(table.getBackground());
        }
        return panel;
    }
    }

/**
 * The editor for each cell of the substitution table. This is not done 
 * completely to the general practices of editors. When adding or deleting
 * elements, the table will just be regenerated instead of using the editor.
 * Everything works though, so we probably don't want to change anything. 
 * WARNING: These are pretty complicated, so if you don't know what you are
 * doing, don't try to change non superficial things. 
 */
public class SubstitutionCellEditor extends AbstractCellEditor implements TableCellEditor {
    Substitution sub;
    public Component getTableCellEditorComponent(javax.swing.JTable table, Object value,
        boolean isSelected, int row, int column) {
        
        // I'm not sure this is needed, but its not causing problems so I didn't
        // remove it.
        if(editSubstitutionRow < 0)
        {
            deleteSubstitutionButton.setEnabled(true);
            createNewTransformationButton.setEnabled(true);
            scaleTransWeightsButton.setEnabled(true);
        }
        editSubstitutionNameButton.setEnabled(true);
        
        // Not sure how necessary this is either but wrote it a while ago, so 
        // leaving it in.
        table.setEditingRow(row);
        sub = (Substitution)value;

        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        // First add the enabled checkbox for the sub
        SubCheckBox subEnabled = new SubCheckBox(sub);
        if(editSubstitutionRow >= 0)
        {
            subEnabled.setEnabled(false);
        }
        GridBagConstraints subEnabledC = new GridBagConstraints(  0, 0,
                                                                  1, 1,
                                                                  0.0, 0.0,
                                                                  GridBagConstraints.WEST, 
                                                                  GridBagConstraints.NONE, 
                                                                  new Insets(0,0,0,0), 
                                                                  0, 0);
        panel.add(subEnabled, subEnabledC);
        
        // Then add the name of the sub. If it is not being edited, then just
        // put it in a label, else in a custom text box to edit. 
        GridBagConstraints subNameC = new GridBagConstraints( 1, 0,
                                                              1, 1,
                                                              1.0, 0.0,
                                                              GridBagConstraints.WEST, 
                                                              GridBagConstraints.HORIZONTAL, 
                                                              new Insets(0,4,0,0), 
                                                              0, 0);
        if(editSubstitutionRow == row)
        {
            SubNameEditField editName = new SubNameEditField(sub);
            panel.add(editName, subNameC);
        }
        else
        {
            javax.swing.JLabel subName = new javax.swing.JLabel(sub.getName());
            subName.setMinimumSize(new Dimension(190, 25));
            subName.setPreferredSize(new Dimension(190, 25));
            panel.add(subName, subNameC);
        }
        
        // then add the type of the substitution in a combobox
        SubTypeComboBox subTypesList = new SubTypeComboBox(sub);
        if(editSubstitutionRow >= 0)
        {
            subTypesList.setEnabled(false);
        }
        GridBagConstraints subTypesC = new GridBagConstraints(  2, 0,
                                                                1, 1,
                                                                0.0, 0.0,
                                                                GridBagConstraints.WEST, 
                                                                GridBagConstraints.NONE, 
                                                                new Insets(0,0,0,0), 
                                                                0, 0);
        panel.add(subTypesList, subTypesC);
        
        // then add the label that just says weight.
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        
        GridBagConstraints subWeightLabelC = new GridBagConstraints(3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,20,0,0), 
                                                                    0, 0);
        panel.add(weightLabel, subWeightLabelC);
        
        // finally add the custom spinner for the substitution weight.
        SubWeightField subWeightField = new SubWeightField(sub);
        if(editSubstitutionRow >= 0)
        {
            subWeightField.setEnabled(false);
        }
        GridBagConstraints subWeightC = new GridBagConstraints(4, 0,
                                                               1, 1,
                                                               0.0, 0.0,
                                                               GridBagConstraints.WEST, 
                                                               GridBagConstraints.NONE, 
                                                               new Insets(0,0,0,4), 
                                                               0, 0);
        panel.add(subWeightField, subWeightC);
        
        // Set the back ground based on selection of row
        panel.setBackground(table.getSelectionBackground());
        
        if(editSubstitutionRow < 0)
            redrawTransformationsList();
        return panel;
    }
    
    @Override
    public boolean isCellEditable(EventObject e)
    {
        return true;
    }

    public Object getCellEditorValue() {
        //System.out.println("getting here");
        return sub;
    }
}

/**
 * Custom CheckBox for the enabling or disabling of a substitution in the
 * substitutions table.
 */
public class SubCheckBox extends javax.swing.JCheckBox implements ActionListener {
    private Substitution sub;
    
    public SubCheckBox(Substitution sub)
    {
        this.sub = sub;
        super.setSelected(sub.getEnabled());
        super.addActionListener(this);
        super.setBackground(Color.WHITE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JCheckBox cb = (javax.swing.JCheckBox)e.getSource();
        sub.setEnabled(cb.isSelected());
    }
}

/**
 * Custom TextField for editing a name of a substitution in the substitutions
 * table.
 */
public class SubNameEditField extends javax.swing.JTextField 
                              implements ActionListener, DocumentListener {
    private Substitution sub;
    
    public SubNameEditField(Substitution sub)
    {
        this.sub = sub;
        super.setText(sub.getName());
        super.setMinimumSize(new Dimension(190, 25));
        super.setPreferredSize(new Dimension(190, 25));
        super.addActionListener(this);
        super.getDocument().addDocumentListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        editSubstitutionRow = -1;
        redrawSubstitutionsList();
        redrawTransformationsList();
        editSubstitutionNameButton.setText("Edit Transformation Class Name");
    }

    public void insertUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }

    public void removeUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }

    public void changedUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }
}

/**
 * Custom combobox for the type of a substitution in the substitutions table.
 */
public class SubTypeComboBox extends javax.swing.JComboBox implements ActionListener {
    private Substitution sub;
    String[] subTypes = { "motif", "embellishment" };
    
    public SubTypeComboBox(Substitution sub)
    {
        super.setEditable(false);
        super.setEnabled(true);
        super.addItem("motif");
        super.addItem("embellishment");
        this.sub = sub;
        
        if(sub.getType().equals("motif"))
            super.setSelectedIndex(0);
        else if(sub.getType().equals("embellishment"))
            super.setSelectedIndex(1);   
        
        super.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JComboBox cb = (javax.swing.JComboBox)e.getSource();
        
        if(cb.getSelectedItem() != null && sub != null)
        {
            sub.setType(cb.getSelectedItem().toString());
            setTotalSubWeights();
        }
    }
    public void itemStateChanged(ItemEvent e) 
    {
        sub.setType(e.getItem().toString());
        setTotalSubWeights();
    }
}

/**
 * Custom JSpinner for the weight of a substitution in the substitutions table.
 */
public class SubWeightField extends javax.swing.JSpinner implements ChangeListener {
    private Substitution sub;
    
    public SubWeightField(Substitution sub)
    {
        this.sub = sub;
        SpinnerNumberModel model = new SpinnerNumberModel(sub.getWeight(), 
                                                          0, 
                                                          Integer.MAX_VALUE, 
                                                          1);
        super.setModel(model);
        super.setMinimumSize(new Dimension(60, 25));
        super.setPreferredSize(new Dimension(60, 25));
        super.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        SpinnerModel numberModel = super.getModel();
        if(numberModel instanceof SpinnerNumberModel)
        {
            sub.setWeight((Integer)numberModel.getValue());
            setTotalSubWeights();
        }
    }
}

/* 
********************************************************************************
END OF CUSTOM COMPONENTS FOR SUBSTITUTIONS TABLE
********************************************************************************
*/

/* 
********************************************************************************
START OF CUSTOM COMPONENTS FOR TRANSFORMATIONS TABLE
********************************************************************************
*/

/**
 * The renderer for each cell of the transformations table. This should be
 * perfectly fine and coded decently. WARNING: These are pretty complicated, 
 * so if you don't know what you are doing, don't try to change non superficial
 * things. Could be combined with the Editor in the future for ease of changing.
 */
public class TransformationCellRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Transformation trans = (Transformation)value;
        
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        // first add the enable check box for the transformation
        TransCheckBox transEnabled = new TransCheckBox(trans);
        GridBagConstraints transEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,0,0,0), 
                                                                    0, 0);
        panel.add(transEnabled, transEnabledC);
        
        // then add the description label for the transformation
        javax.swing.JLabel transDescLabel = new javax.swing.JLabel(trans.getDescription());
        transDescLabel.setMinimumSize(new Dimension(307, 25));
        transDescLabel.setPreferredSize(new Dimension(307, 25));
        transDescLabel.setMaximumSize(new Dimension(100000, 25));
        GridBagConstraints transNameC = new GridBagConstraints( 1, 0,
                                                                1, 1,
                                                                1.0, 0.0,
                                                                GridBagConstraints.WEST, 
                                                                GridBagConstraints.HORIZONTAL, 
                                                                new Insets(0,4,0,0), 
                                                                0, 0);
        panel.add(transDescLabel, transNameC);
        
        // then add the label that just says weight
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        GridBagConstraints transWeightLabelC = new GridBagConstraints(  2, 0,
                                                                        1, 1,
                                                                        0.0, 0.0,
                                                                        GridBagConstraints.WEST, 
                                                                        GridBagConstraints.NONE, 
                                                                        new Insets(0,0,0,0), 
                                                                        0, 0);
        panel.add(weightLabel, transWeightLabelC);
        
        // then add the jspinner for the weight of the transformation
        TransWeightField transWeightField = new TransWeightField(trans);
        GridBagConstraints transWeightC = new GridBagConstraints(   3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,0,0,4), 
                                                                    0, 0);
        panel.add(transWeightField, transWeightC);
        
        // set the background based on the focus of the cell
        if (hasFocus) {
                panel.setBackground(table.getSelectionBackground());
         }
        else
        {
                panel.setBackground(table.getBackground());
        }
        return panel;
    }
}

/**
 * The editor for each cell of the transformations table. This is not done 
 * completely to the general practices of editors. When adding or deleting
 * elements, the table will just be regenerated instead of using the editor.
 * Everything works though, so we probably don't want to change anything. 
 * WARNING: These are pretty complicated, so if you don't know what you are
 * doing, don't try to change non superficial things. 
 */
public class TransformationCellEditor extends AbstractCellEditor implements TableCellEditor {
    Transformation trans;
    public Component getTableCellEditorComponent(javax.swing.JTable table, Object value,
        boolean isSelected, int row, int column) {
        trans = (Transformation)value;
        deleteTransformationButton.setEnabled(true);
        editSelectedTransformationButton.setEnabled(true);
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        // first add the check box for the enable of the transformation
        TransCheckBox transEnabled = new TransCheckBox(trans);
        GridBagConstraints transEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,0,0,0),    
                                                                    0, 0);
        panel.add(transEnabled, transEnabledC);
        
        // then add the label for the description of the transformation
        javax.swing.JLabel transDescLabel = new javax.swing.JLabel(trans.getDescription());
        transDescLabel.setMinimumSize(new Dimension(307, 25));
        transDescLabel.setPreferredSize(new Dimension(307, 25));
        transDescLabel.setMaximumSize(new Dimension(100000, 25));
        GridBagConstraints transNameC = new GridBagConstraints( 1, 0,
                                                                1, 1,
                                                                1.0, 0.0,
                                                                GridBagConstraints.WEST, 
                                                                GridBagConstraints.HORIZONTAL,  
                                                                new Insets(0,4,0,0), 
                                                                0, 0);
        panel.add(transDescLabel, transNameC);
        
        // then add the label that just says weight
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        GridBagConstraints transWeightLabelC = new GridBagConstraints(  2, 0,
                                                                        1, 1,
                                                                        0.0, 0.0,
                                                                        GridBagConstraints.WEST, 
                                                                        GridBagConstraints.NONE, 
                                                                        new Insets(0,0,0,0), 
                                                                        0, 0);
        panel.add(weightLabel, transWeightLabelC);
        
        // then add the custom spinner for the weight of the transformation
        TransWeightField transWeightField = new TransWeightField(trans);
        GridBagConstraints transWeightC = new GridBagConstraints(   3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                                                                    GridBagConstraints.WEST, 
                                                                    GridBagConstraints.NONE, 
                                                                    new Insets(0,0,0,4), 
                                                                    0, 0);
        panel.add(transWeightField, transWeightC);
        
        // then set the background of the cell
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }
    
    @Override
    public boolean isCellEditable(EventObject e)
    {
        return true;
    }

    public Object getCellEditorValue() {
        //System.out.println("getting here");
        return trans;
    }
}

/**
 * The checkBox for a transformation saying if it is enabled or not.
 */
public class TransCheckBox extends javax.swing.JCheckBox implements ActionListener {
    private Transformation trans;
    
    public TransCheckBox(Transformation trans)
    {
        this.trans = trans;
        super.setSelected(trans.getEnabled());
        super.addActionListener(this);
        super.setBackground(Color.WHITE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JCheckBox cb = (javax.swing.JCheckBox)e.getSource();
        trans.setEnabled(cb.isSelected());
    }
}

/**
 * The custom spinner that holds the value for the weight of a transformation
 */
public class TransWeightField extends javax.swing.JSpinner implements ChangeListener {
    private Transformation trans;
    
    public TransWeightField(Transformation trans)
    {
        this.trans = trans;
        SpinnerNumberModel model = new SpinnerNumberModel(trans.getWeight(), 
                                                          0, 
                                                          Integer.MAX_VALUE, 
                                                          1);
        super.setModel(model);
        super.setMinimumSize(new Dimension(60, 25));
        super.setPreferredSize(new Dimension(60, 25));
        super.addChangeListener(this);
    }
    

    public void stateChanged(ChangeEvent e) {
        SpinnerModel numberModel = super.getModel();
        if(numberModel instanceof SpinnerNumberModel)
        {
            trans.setWeight((Integer)numberModel.getValue());
            setTotalTransWeights();
        }
    }
}

/**
 * The dialogue that appears to edit a transformation.
 */
public class TransformationDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transformation trans;
    private javax.swing.JTextArea contents;
    
    public TransformationDialogue(javax.swing.JFrame frame, Transformation trans)
    {
        super(frame, "Transformation Editor", true);
        super.setSize(800,600);
        this.trans = trans;
        StringBuilder transFile = new StringBuilder();
        trans.toFile(transFile, "");
        contents = new javax.swing.JTextArea();
        
        // THIS IS VERY IMPORTANT FOR TAB SPACING
        // Tabs are being based on size of 8, and need every character to have 
        // then same width, so lines line up. 
        contents.setFont(new Font("monospaced", Font.PLAIN, 14));
        contents.setTabSize(8);
        
        contents.setText(transFile.toString());
        super.setLocationRelativeTo(frame);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane();
        scroll.setPreferredSize(new Dimension(820,620));
        scroll.setViewportView(contents);
        getContentPane().add(scroll);
        
        javax.swing.JButton saveButton = new javax.swing.JButton("Save and Close"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        Polylist edit = (Polylist)Polylist.PolylistFromString(contents.getText()).first();
        boolean result = trans.setTransformation(edit);
        // it the edited transform is invalid, the save was not made, so do not
        // close the dialogue. 
        if(result)
        {
            dispose(); 
            setVisible(false); 
            redrawTransformationsList();
        }
    }
}    

/* 
********************************************************************************
END OF CUSTOM COMPONENTS FOR TRANSFORMATIONS TABLE
********************************************************************************
*/

/**
 * Dialogue that opens to scale the weights of substitutions that are of the 
 * type motif. 
 */
public class ScaleMotifWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transform transform;
    private javax.swing.JTextField contents;
    
    public ScaleMotifWeightsDialogue(javax.swing.JFrame frame, Transform trans)
    {
        super(frame, "Scale Motif Weights", true);
        this.transform = trans;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        String label = "Scale all motif weights by: ";
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel(label);
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            transform.scaleMotifWeights(scale);
            dispose(); 
            setVisible(false); 
            redrawSubstitutionsList();
            redrawTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    

/**
 * Dialogue that opens to scale the weights of substitutions that are of the 
 * type embellishment. 
 */
public class ScaleEmbWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transform transform;
    private javax.swing.JTextField contents;
    
    public ScaleEmbWeightsDialogue(javax.swing.JFrame frame, Transform trans)
    {
        super(frame, "Scale Embellishment Weights", true);
        this.transform = trans;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        String label = "Scale all embellishment weights by: ";
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel(label);
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            transform.scaleEmbWeights(scale);
            dispose(); 
            setVisible(false); 
            redrawSubstitutionsList();
            redrawTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    
/**
 * Dialogue that opens to scale the weights of the transformations of the
 * selected substitution.
 */
public class ScaleTransWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Substitution sub;
    private javax.swing.JTextField contents;
    
    public ScaleTransWeightsDialogue(javax.swing.JFrame frame, Substitution sub)
    {
        super(frame, "Scale Transformation Weights", true);
        this.sub = sub;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        String label = "Scale all transformation weights by: ";
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel(label);
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            sub.scaleTransWeights(scale);
            dispose(); 
            setVisible(false); 
            redrawTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    
}
