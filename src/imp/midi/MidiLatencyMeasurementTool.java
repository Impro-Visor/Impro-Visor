/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.midi;

import imp.midi.MidiSynth;
import imp.Constants;
import imp.data.*;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.midi.MidiManager;
import imp.midi.MidiNoteListener;
import imp.midi.MidiPlayListener;
import javax.sound.midi.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author mhunt
 */
public class MidiLatencyMeasurementTool
        extends javax.swing.JPanel
        implements Constants, MidiNoteListener, MidiPlayListener, Receiver {

    Notate notate;

    MidiSynth midiSynth;

    boolean repeatTest = false;

    Score testScore;

    MeasurementTableModel tableModel;

    private long timeStart;

    public void close() {
       }
    

    private class MeasurementTableModel
            extends AbstractTableModel {

        Vector<Double> measurement = new Vector<Double>();

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Trial";
                case 1:
                    return "Latency";
            }
            return "";
        }

        public int getRowCount() {
            return measurement.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return row + 1;
            } else {
                return measurement.get(row);
            }
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void addMeasurement(double latency) {
            measurement.add(latency);
            int index = measurement.size() - 1;
            fireTableRowsInserted(index, index);
        }

        public void clear() {
            int oldSize = measurement.size();
            if (oldSize == 0) {
                return;
            }
            measurement.clear();
            fireTableRowsDeleted(0, oldSize - 1);
            fireTableRowsUpdated(0, oldSize - 1);
        }

        public double getAverage() {
            // this is a quick hack since there is only one table...
            int[] rowIndices = measurementTable.getSelectedRows();
            int sum = 0;

            if (rowIndices.length == 0) {
                for (double latency : measurement) {
                    sum += latency;
                }
                return sum / measurement.size();
            } else {
                for (int i : rowIndices) {
                    sum += measurement.get(i);
                }
                return sum / rowIndices.length;
            }
        }

    }

    private class SelectionListener
            implements ListSelectionListener {

  JTable table;

        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            updateAverage();
        }

    }

    /**
     * Creates new form MidiLatencyMeasurementTool
     */
    public MidiLatencyMeasurementTool(Notate notate) {
        this.notate = notate;

        tableModel = new MeasurementTableModel();

        initComponents();

        SelectionListener listener = new SelectionListener(measurementTable);
        measurementTable.getSelectionModel().addListSelectionListener(listener);
        measurementTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);

        testScore = new Score();
        playStatus = MidiPlayListener.Status.STOPPED;
        MelodyPart part = new MelodyPart();
        part.setInstrument(1);
        
        part.addNote(new Note(60, BEAT));
        part.addRest(new Rest(BEAT * 2));
        
        testScore.setTempo(60);
        testScore.addPart(part);
    }

    public void startLatencyMeasurement() {
        midiSynth = notate.getMidiSynth();
        repeatTest = true;

        tableModel.clear();
        midiSynth.registerReceiver(this);
        midiSynth.registerNoteListener(this);
        midiSynth.setPlayListener(this);
        startPlay(0);
    }

    public void stopLatencyMeasurement() {
        midiSynth = notate.getMidiSynth();
        repeatTest = false;
        midiSynth.unregisterReceiver(this);
        midiSynth.unregisterNoteListener(this);
        midiSynth.stop("stop");
    }

    public void startPlay(int transposition) {
        try {
            midiSynth.play(testScore, 0, 1000, transposition);
        } catch (Exception e) {
            ErrorLog.log(ErrorLog.WARNING, "Error playing sound");
        }
    }

    MidiPlayListener.Status playStatus;

    public void setPlaying(MidiPlayListener.Status playing, int transposition) {
        playStatus = playing;
        switch (playing) {
            case PLAYING:
                break;
            case STOPPED:
                if (repeatTest && isShowing()) {
                    startPlay(transposition);
                } else if (repeatTest) {
                    stopLatencyMeasurement();
                }
                break;
        }
    }

    public MidiPlayListener.Status getPlaying() {
        return playStatus;
    }

    double latency = 0;

    public void updateAverage() {
        latency = tableModel.getAverage();
        averageLatencyTF.setText(String.valueOf(latency));
    }

    /**
     * This function receives the note on event from the keyboard and uses it to
     * calculate the latency
     */
    public void send(MidiMessage message, long timeStamp) {
        byte[] m = message.getMessage();
        if (m.length < 3) {
            return;
        }

        int highNibble = (m[0] & 0xF0) >> 4;
        int velocity = m[2];

        switch (highNibble) {
            case 9: // note on
                if (velocity == 0) {
                    break;
                }

                //System.out.println("Key down: " + midiSynth.getSequencer().getMicrosecondPosition());
                long delay
                        = (2 * (midiSynth.getSequencer().getMicrosecondPosition() - timeStart));
                tableModel.addMeasurement(((double) delay) / 1000);
                updateAverage();
                break;
        }
    }
    
    

    /**
     * This function receives the note played event from the midiSynth and uses
     * it to start timing the latency
     */
    public void noteOn(int note, int channel) {
        timeStart = midiSynth.getSequencer().getMicrosecondPosition();
         
         //System.out.println("Note On: " + timeStart);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();
        measurementTableScrollPane = new javax.swing.JScrollPane();
        measurementTable = new javax.swing.JTable();
        calibrationPanel = new javax.swing.JPanel();
        averageLabel = new javax.swing.JLabel();
        averageLatencyTF = new javax.swing.JTextField();
        msLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        startBtn = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        addAncestorListener(new javax.swing.event.AncestorListener()
        {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt)
            {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt)
            {
                formAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt)
            {
            }
        });
        setLayout(new java.awt.GridBagLayout());

        mainPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        instructionsTextArea.setEditable(false);
        instructionsTextArea.setColumns(20);
        instructionsTextArea.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setRows(5);
        instructionsTextArea.setText("To begin calibration, click Start.  You will hear a repeated note.  Play a note on your MIDI instrument at the same time as the note you hear.");
        instructionsTextArea.setWrapStyleWord(true);
        instructionsTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        mainPanel.add(instructionsTextArea, gridBagConstraints);

        measurementTableScrollPane.setBackground(new java.awt.Color(249, 247, 247));
        measurementTableScrollPane.setOpaque(false);

        measurementTable.setModel(tableModel);
        measurementTableScrollPane.setViewportView(measurementTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mainPanel.add(measurementTableScrollPane, gridBagConstraints);

        calibrationPanel.setOpaque(false);
        calibrationPanel.setLayout(new java.awt.GridBagLayout());

        averageLabel.setText("Average Value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        calibrationPanel.add(averageLabel, gridBagConstraints);

        averageLatencyTF.setText("0");
        averageLatencyTF.setMinimumSize(new java.awt.Dimension(40, 19));
        averageLatencyTF.setPreferredSize(new java.awt.Dimension(60, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        calibrationPanel.add(averageLatencyTF, gridBagConstraints);

        msLabel.setText("ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        calibrationPanel.add(msLabel, gridBagConstraints);

        jButton1.setText("Use this value as the latency");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        calibrationPanel.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        mainPanel.add(calibrationPanel, gridBagConstraints);

        startBtn.setText("Start Calibration");
        startBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        mainPanel.add(startBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(mainPanel, gridBagConstraints);

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        titleLabel.setText("Latency Measurement Tool");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        add(titleLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        notate.setMidiLatency(latency);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void returnBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnBtnActionPerformed
    }//GEN-LAST:event_returnBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    }//GEN-LAST:event_saveBtnActionPerformed

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        if (!repeatTest) {
            startLatencyMeasurement();
            startBtn.setText("Stop");
        } else {
            stopLatencyMeasurement();
            startBtn.setText("Start Calibration");
        }
    }//GEN-LAST:event_startBtnActionPerformed

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        refreshText();
    }//GEN-LAST:event_formAncestorAdded

    public void refreshText() {
        if (!repeatTest) {
            startBtn.setText("Start Calibration");
        } else {
            startBtn.setText("Stop");
        }
    }
    
    public void stop() {
        repeatTest = false;
        stopLatencyMeasurement();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel averageLabel;
    private javax.swing.JTextField averageLatencyTF;
    private javax.swing.JPanel calibrationPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTable measurementTable;
    private javax.swing.JScrollPane measurementTableScrollPane;
    private javax.swing.JLabel msLabel;
    private javax.swing.JButton startBtn;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
