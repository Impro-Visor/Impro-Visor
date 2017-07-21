/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

import polya.Polylist;
import imp.util.Preferences;
import imp.com.TransposeAllInPlaceCommand;
import imp.com.TransposeInstrumentsCommand;
import imp.data.Key;
import imp.data.Score;
import imp.data.Transposition;

/**
 * @author Samantha Long and Robert Keller
 */
public class TranspositionWizardDialog extends javax.swing.JDialog {

    public TranspositionWizardDialog(Notate notate)
    {
        initComponents();
        this.notate = notate;
        WindowRegistry.registerWindow(this);
        setVisible(true);
    }


     Notate getNotate()
    {
        return notate;
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

        transposeTitle = new javax.swing.JLabel();
        instrumentSelectionPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        transpositionWizardJList = new javax.swing.JList<>();
        instrumentLabel = new javax.swing.JLabel();
        transpositionPreviewPanel = new javax.swing.JPanel();
        melodyWizardLabel = new javax.swing.JLabel();
        chordWizardLabel = new javax.swing.JLabel();
        bassWizardLabel = new javax.swing.JLabel();
        melodyWizardSpinner = new javax.swing.JSpinner();
        bassWizardSpinner = new javax.swing.JSpinner();
        chordWizardSpinner = new javax.swing.JSpinner();
        playbackLabel = new javax.swing.JLabel();
        transpositionWizardSaveButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        concertPitchButton = new javax.swing.JButton();
        customLeadsheetTransposeSpinner = new javax.swing.JSpinner();
        clefWizardPanel = new javax.swing.JPanel();
        clefWizardTextField = new javax.swing.JTextField();
        clefLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setTitle("Transposition Wizard");
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setBounds(new java.awt.Rectangle(400, 300, 570, 450));
        setMinimumSize(new java.awt.Dimension(570, 450));
        setSize(new java.awt.Dimension(570, 450));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        transposeTitle.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        transposeTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        transposeTitle.setText("Transpose Notation and/or Playback");
        transposeTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 30;
        getContentPane().add(transposeTitle, gridBagConstraints);

        instrumentSelectionPanel.setToolTipText("");
        instrumentSelectionPanel.setMaximumSize(new java.awt.Dimension(350, 250));
        instrumentSelectionPanel.setMinimumSize(new java.awt.Dimension(350, 250));
        instrumentSelectionPanel.setPreferredSize(new java.awt.Dimension(180, 200));
        instrumentSelectionPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(160, 175));

        transpositionWizardJList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "No-Transposition", "Bb-Trumpet", "Bb-TenorSax", "Bb-SopranoSax", "Eb-AltoSax", "Eb-BaritoneSax", "F-Horn", "Trombone", "SopranoRecorder" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        transpositionWizardJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        transpositionWizardJList.setToolTipText("First select an instrument, it's pitch can be transposed in playback and/or in the leadsheet ");
        transpositionWizardJList.setBounds(new java.awt.Rectangle(0, 0, 100, 100));
        transpositionWizardJList.setMaximumSize(new java.awt.Dimension(100, 100));
        transpositionWizardJList.setMinimumSize(new java.awt.Dimension(100, 100));
        transpositionWizardJList.setPreferredSize(new java.awt.Dimension(100, 80));
        transpositionWizardJList.setVisibleRowCount(10);
        transpositionWizardJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                transpositionWizardJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(transpositionWizardJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        instrumentSelectionPanel.add(jScrollPane1, gridBagConstraints);

        instrumentLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        instrumentLabel.setText("  Instrument  ");
        instrumentLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        instrumentSelectionPanel.add(instrumentLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        getContentPane().add(instrumentSelectionPanel, gridBagConstraints);

        transpositionPreviewPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        transpositionPreviewPanel.setToolTipText("Here is a preview of what the playback will sound like, transposed into the selected instrument's pitch.");
        transpositionPreviewPanel.setMaximumSize(new java.awt.Dimension(250, 200));
        transpositionPreviewPanel.setMinimumSize(new java.awt.Dimension(250, 200));
        transpositionPreviewPanel.setPreferredSize(new java.awt.Dimension(250, 210));
        transpositionPreviewPanel.setLayout(new java.awt.GridBagLayout());

        melodyWizardLabel.setText("Melody Transposition:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 51, 0, 0);
        transpositionPreviewPanel.add(melodyWizardLabel, gridBagConstraints);

        chordWizardLabel.setText("Chord Transposition:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 51, 0, 0);
        transpositionPreviewPanel.add(chordWizardLabel, gridBagConstraints);

        bassWizardLabel.setText("Bass Transposition:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 51, 0, 0);
        transpositionPreviewPanel.add(bassWizardLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 51);
        transpositionPreviewPanel.add(melodyWizardSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 51);
        transpositionPreviewPanel.add(bassWizardSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 51);
        transpositionPreviewPanel.add(chordWizardSpinner, gridBagConstraints);

        playbackLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        playbackLabel.setText("Playback");
        playbackLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        transpositionPreviewPanel.add(playbackLabel, gridBagConstraints);
        playbackLabel.getAccessibleContext().setAccessibleName(" Playback ");

        transpositionWizardSaveButton.setText("Transpose Playback ");
        transpositionWizardSaveButton.setToolTipText("Clicking this button will set the transposed playback.");
        transpositionWizardSaveButton.setPreferredSize(new java.awt.Dimension(141, 40));
        transpositionWizardSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transpositionWizardSaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        transpositionPreviewPanel.add(transpositionWizardSaveButton, gridBagConstraints);

        jPanel3.setMaximumSize(new java.awt.Dimension(200, 32));
        jPanel3.setMinimumSize(new java.awt.Dimension(200, 32));
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 32));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("     Select instrument to set");
        jLabel3.setMaximumSize(new java.awt.Dimension(300, 16));
        jLabel3.setMinimumSize(new java.awt.Dimension(300, 16));
        jLabel3.setPreferredSize(new java.awt.Dimension(200, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("      playback transpositions");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel3.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 20;
        transpositionPreviewPanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(transpositionPreviewPanel, gridBagConstraints);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setToolTipText("");
        jPanel1.setMaximumSize(new java.awt.Dimension(320, 150));
        jPanel1.setMinimumSize(new java.awt.Dimension(320, 150));
        jPanel1.setPreferredSize(new java.awt.Dimension(425, 120));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        concertPitchButton.setText("Transpose Leadsheet");
        concertPitchButton.setToolTipText("Clicking this button will visually transpose the leadsheet");
        concertPitchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concertPitchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(concertPitchButton, gridBagConstraints);

        customLeadsheetTransposeSpinner.setToolTipText("This is the amount of semitones the leadsheet will be transposed.");
        customLeadsheetTransposeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("Transpose"));
        customLeadsheetTransposeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                customLeadsheetTransposeSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 40;
        jPanel1.add(customLeadsheetTransposeSpinner, gridBagConstraints);

        clefWizardPanel.setLayout(new java.awt.GridBagLayout());

        clefWizardTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        clefWizardTextField.setToolTipText("This is the clef that will appear on the transposed leadsheet.");
        clefWizardTextField.setMaximumSize(new java.awt.Dimension(90, 20));
        clefWizardTextField.setMinimumSize(new java.awt.Dimension(90, 20));
        clefWizardTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        clefWizardPanel.add(clefWizardTextField, gridBagConstraints);

        clefLabel.setText("Clef:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        clefWizardPanel.add(clefLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(clefWizardPanel, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("Use this once to transpose the leadsheet");
        jLabel1.setPreferredSize(new java.awt.Dimension(300, 16));
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("notation from its current pitch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel1.add(jPanel2, gridBagConstraints);
        jPanel1.add(jSeparator1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
Notate notate;

    private void transpositionWizardJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_transpositionWizardJListValueChanged
        //gives preview of what will be transposed in transposition wizard window
        String transpositionInstrument = transpositionWizardJList.getSelectedValue();
        String allValues = Preferences.getPreference("transposing-instruments");
        Polylist ALL_VALUES = Polylist.PolylistFromString(allValues);
        Polylist found = ALL_VALUES.assoc(transpositionInstrument);
        Long mel = (Long) found.second();
        Long chordbass = (Long) found.third();
        String clef = (String) found.fourth();
        scoreTransposition = ((Long) found.fifth()).intValue();
        melodyWizardSpinner.setValue(mel);
        chordWizardSpinner.setValue(chordbass);
        bassWizardSpinner.setValue(chordbass);
        clefWizardTextField.setText(clef);
        customLeadsheetTransposeSpinner.setValue(scoreTransposition);
        notate.setLeadsheetTransValue(-mel.intValue());
    }//GEN-LAST:event_transpositionWizardJListValueChanged

    private void transpositionWizardSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transpositionWizardSaveButtonActionPerformed
        if (transpositionWizardJList.getSelectedValue() != null)
        {
            //deals with changing transposition spinners & radio buttons in preferences windows
            String transpositionInstrument = transpositionWizardJList.getSelectedValue();
            String allValues = Preferences.getPreference("transposing-instruments");
            Polylist ALL_VALUES = Polylist.PolylistFromString(allValues);
            Polylist found = ALL_VALUES.assoc(transpositionInstrument);
            int mel = ((Long)found.second()).intValue();
            int chordbass = ((Long) found.third()).intValue();
            String clef = (String) found.fourth();
            Transposition newTransposition = new Transposition(chordbass, chordbass, mel);
            notate.executeCommand(new TransposeInstrumentsCommand(notate,
                                                                  newTransposition, 
                                                                  clef));
        }
        setVisible(false);
        notate.getTranspositionWizardMI().setEnabled(true);
    }//GEN-LAST:event_transpositionWizardSaveButtonActionPerformed

    int scoreTransposition = 0;
    int newKeySignature = 0;

    private void concertPitchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concertPitchButtonActionPerformed
        if (transpositionWizardJList.getSelectedValue() != null)
        {
             String transpositionInstrument = transpositionWizardJList.getSelectedValue();
             String allValues = Preferences.getPreference("transposing-instruments");
             Polylist all_values = Polylist.PolylistFromString(allValues);
             Polylist found = all_values.assoc(transpositionInstrument);
             int chordbass = ((Long)found.third()).intValue();
             scoreTransposition = ((Long)found.fifth()).intValue();

             //transposes score/leadsheet visually (notes + key signature)
             Score score = notate.getScore();
             int oldKeySignature = score.getKeySignature();
             int transpositionIndex = (132 - chordbass)%12;
             newKeySignature = Key.transpositions[(12 + oldKeySignature)%12][transpositionIndex];
             // 132 = 12*11 to ensure the result is positive, yet be equivalent to chordbass value mod 12
             
             // Using a command allows this action to be undoable.
             notate.executeCommand(new TransposeAllInPlaceCommand(notate, 
                                                                  scoreTransposition, 
                                                                  scoreTransposition, 
                                                                  newKeySignature));
        } 
    }//GEN-LAST:event_concertPitchButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        notate.getTranspositionWizardMI().setEnabled(true);
    }//GEN-LAST:event_formWindowClosing

    private void customLeadsheetTransposeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_customLeadsheetTransposeSpinnerStateChanged
        Object change = customLeadsheetTransposeSpinner.getValue();
        String temp = change.toString();
        scoreTransposition = Integer.valueOf(temp);
        
        if (transpositionWizardJList.getSelectedValue() != null)
        {
             String transpositionInstrument = transpositionWizardJList.getSelectedValue();
             String allValues = Preferences.getPreference("transposing-instruments");
             Polylist all_values = Polylist.PolylistFromString(allValues);
             Polylist found = all_values.assoc(transpositionInstrument);
             int chordbass = ((Long)found.third()).intValue();
             //transposes score/leadsheet visually (notes + key signature)
             Score score = notate.getScore();
             int oldKeySignature = score.getKeySignature();
             int transpositionIndex = (132 - chordbass)%12;
             newKeySignature = Key.transpositions[(12 + oldKeySignature)%12][transpositionIndex];
             // 132 = 12*11 to ensure the result is positive, yet be equivalent to chordbass value mod 12
             
             // Using a command allows this action to be undoable.
//             notate.executeCommand(new TransposeAllInPlaceCommand(notate, 
//                                                                  scoreTransposition, 
//                                                                  scoreTransposition, 
//                                                                  newKeySignature));
        }
    }//GEN-LAST:event_customLeadsheetTransposeSpinnerStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bassWizardLabel;
    private javax.swing.JSpinner bassWizardSpinner;
    private javax.swing.JLabel chordWizardLabel;
    private javax.swing.JSpinner chordWizardSpinner;
    private javax.swing.JLabel clefLabel;
    private javax.swing.JPanel clefWizardPanel;
    private javax.swing.JTextField clefWizardTextField;
    private javax.swing.JButton concertPitchButton;
    private javax.swing.JSpinner customLeadsheetTransposeSpinner;
    private javax.swing.JLabel instrumentLabel;
    private javax.swing.JPanel instrumentSelectionPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel melodyWizardLabel;
    private javax.swing.JSpinner melodyWizardSpinner;
    private javax.swing.JLabel playbackLabel;
    private javax.swing.JLabel transposeTitle;
    private javax.swing.JPanel transpositionPreviewPanel;
    private javax.swing.JList<String> transpositionWizardJList;
    private javax.swing.JButton transpositionWizardSaveButton;
    // End of variables declaration//GEN-END:variables
}
