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

import imp.com.PlayPartCommand;
import imp.com.PlayScoreCommand;
import imp.com.RectifyPitchesCommand;
import imp.data.ChordPart;
import java.util.ArrayList;
import imp.data.MelodyPart;
import imp.data.MidiSynth;
import imp.data.Rest;
import imp.data.Score;
import imp.util.MidiPlayListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.midi.InvalidMidiDataException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Stack;
import jm.midi.event.Event;
/**
 *
 * @author muddCS15
 */
public class TradingWindow 
extends javax.swing.JFrame
{
    static Notate notate;
    
    private int melodyNum = 0;
    private ArrayList<MelodyPart>  melodies   = new ArrayList<MelodyPart>();
    private LinkedList<MelodyPart> hotswapper = new LinkedList<MelodyPart>();
    private Score tradeScore;
    private Stack<Integer> triggers = new Stack();
    //length of trading in measures
    private boolean isUserTurn;
    
    private Integer scoreLength;
    private Integer slotsPerMeasure;
    private Integer slotsPerTurn;
    private Integer adjustedLength;
    private ChordPart chords;
    private MelodyPart aMelodyPart;
    private MidiSynth midiSynth;
    private boolean isTrading;
    private long slotDelay;
    private boolean isUserInputError;
    
    
    
    //magic values
    private int endLimitIndex = -1;
    private boolean isSwing = false;
    private Integer snapResolution = 2;
    private Integer measures = 4;
    
    private static final int zero = 0;
    private static final int one = 1;
    
    private void setSlotDelay(double beatDelay){
        double doubleSlotsPerMeasure = (double) tradeScore.getSlotsPerMeasure();
        double beatsPerMeasure = (double) tradeScore.getBeatsPerMeasure();
        double slotsPerBeat = doubleSlotsPerMeasure / beatsPerMeasure;
        
        long newDelay = Math.round(slotsPerBeat * beatDelay);
        slotDelay = newDelay;
        System.out.println(slotDelay);
    }
    
    public void trackPlay(ActionEvent e) {
        long currentPosition = notate.getSlotInPlayback();
        if (triggers.isEmpty()) {
            stopTrading();
        } else {
            long nextTrig = (long) triggers.peek();
            if (nextTrig <= currentPosition) {
                //System.out.println("long: " + nextTrig);
                triggers.pop();
                if (nextTrig != zero) {
                    switchTurn();
                }
            }
        }
    }

    public void switchTurn(){
        if (this.isUserTurn){
            this.computerTurn();
        } else{
            this.userTurn();
        }
    }
    
    /**
     * Creates new form TradingWindow
     * @param notate
     */
    public TradingWindow(Notate notate) {
        initComponents();
        this.notate = notate;
        tradeScore = new Score();
    }

    /**
     * Starts interactive trading
     */
    public void startTrading() {
        startTradingButton.setText("StopTrading");
        isTrading = true;
        midiSynth = new MidiSynth(notate.getMidiManager());
        scoreLength = notate.getScoreLength();
        slotsPerMeasure = notate.getScore().getSlotsPerMeasure();
        slotsPerTurn = measures * slotsPerMeasure;
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        for (int trigSlot = adjustedLength; trigSlot >= zero; trigSlot = trigSlot - slotsPerTurn) {
            triggers.push(trigSlot);
            //System.out.println(trigSlot);
        }
        notate.playScore();
        userTurn();
    }
    
    /**
     * Stops interactive trading
     */
    public void stopTrading() {
        startTradingButton.setText("StartTrading");
        isTrading = false;
        notate.stopRecording();
        notate.stopPlaying("stop trading");
        notate.getMidiRecorder().setDestination(null);
    }
    
    public void userTurn() {
        this.isUserTurn = true;
        int nextSection;
        if (triggers.isEmpty()){
            nextSection = adjustedLength;
        }
        else {
            nextSection = triggers.peek();
        }
        chords = notate.getScore().getChordProg().extract(nextSection, nextSection + slotsPerTurn - one);
        aMelodyPart = new MelodyPart(slotsPerTurn);
        tradeScore = new Score("trading", notate.getTempo(), zero);
        tradeScore.setChordProg(chords);
        tradeScore.addPart(aMelodyPart);
        notate.initTradingRecorder(aMelodyPart);
        notate.enableRecording();
        
    }
    
    
    public void computerTurn() {
        
        this.isUserTurn = false;
        notate.stopRecording();
        //snap? tradeScore.getPart(0).applyResolution(snapResolution);
        
        

        RectifyPitchesCommand fixPitches = new RectifyPitchesCommand(
                aMelodyPart, 
                zero, 
                slotsPerTurn, 
                chords, 
                false, 
                false);
        fixPitches.execute();
        tradeScore.setBassMuted(true);
        tradeScore.delPart(0);
        tradeScore.deleteChords();
        
        aMelodyPart = aMelodyPart.applyResolution(60);
        
        Long delayCopy = new Long(this.slotDelay);
        aMelodyPart = aMelodyPart.extract(delayCopy.intValue(), slotsPerTurn - one, true, true);
        tradeScore.addPart(aMelodyPart);
        
        
        new PlayScoreCommand(
                tradeScore,
                zero,
                isSwing,
                midiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                endLimitIndex
        ).execute();
//        notate.playAscore(tradeScore);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startTradingButton = new javax.swing.JButton();
        beatDelayBox = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        startTradingButton.setLabel("Start Trading");
        startTradingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTradingButtonActionPerformed(evt);
            }
        });

        beatDelayBox.setForeground(new java.awt.Color(255, 0, 0));
        beatDelayBox.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beatDelayBox.setText("0.0");
        beatDelayBox.setAutoscrolls(false);
        beatDelayBox.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                beatDelayBoxCaretUpdate(evt);
            }
        });
        beatDelayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beatDelayBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("delay in beats:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(startTradingButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(beatDelayBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(beatDelayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startTradingButton)
                .addContainerGap())
        );

        startTradingButton.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //
    }//GEN-LAST:event_formWindowClosed

    private void startTradingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTradingButtonActionPerformed
        if (!isUserInputError) {
            if (!isTrading) {
                startTrading();
            } else {
                stopTrading();
            }
        }
    }//GEN-LAST:event_startTradingButtonActionPerformed

    private void beatDelayBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beatDelayBoxActionPerformed
        setSlotDelay(tryDouble(beatDelayBox.getText()));
    }//GEN-LAST:event_beatDelayBoxActionPerformed

    private void beatDelayBoxCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_beatDelayBoxCaretUpdate
        setSlotDelay(tryDouble(beatDelayBox.getText()));
    }//GEN-LAST:event_beatDelayBoxCaretUpdate

    private double tryDouble(String number){
        double newNumber;
        try {
            newNumber = Double.parseDouble(number);
            isUserInputError = false;
        } catch (Exception e){
            isUserInputError = true;
            newNumber = 0;
        }
        return newNumber;
    }
    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TradingWindow(notate).setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField beatDelayBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton startTradingButton;
    // End of variables declaration//GEN-END:variables

    

    

}