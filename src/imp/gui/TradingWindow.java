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

import static imp.Constants.BEAT;
import static imp.Constants.C4;
import imp.ImproVisor;
import imp.com.PlayPartCommand;
import imp.com.PlayScoreCommand;
import imp.com.RectifyPitchesCommand;
import imp.data.ChordPart;
import imp.data.IntervalLearner;
import static imp.data.IntervalLearner.intervals;
import java.util.ArrayList;
import imp.data.MelodyPart;
import imp.data.MidiSynth;
import imp.data.Note;
import imp.data.ResponseGenerator;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import imp.util.GrammarFilter;
import imp.util.MidiManager;
import imp.util.MidiPlayListener;
import imp.util.TransformFilter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.sound.midi.InvalidMidiDataException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Stack;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToggleButton.ToggleButtonModel;
import jm.midi.event.Event;

/**
 * This class embodies the UI and functionality of interactive trading.
 *
 * An instance of TradingWindow works by keeping a reference to the instance
 * of notate that instantiated itself. Each instance of TradingWindow is
 * destroyed when it's associated UI window is closed. When interactive trading
 * is initialized (the method '.startTrading()' is called), notate is triggered
 * to play its current open chorus; trading is made possible
 * by dispatching events based upon the position of the play head in said
 * chorus. Events are scheduled in three main phases, each phase with
 * its respective method:
 *
 * User turn - When the user plays. During this phase,
 * the user input is recorded into
 * the instance variable 'tradeScore'.
 *                                      * associated method: .userTurn()
 * 
 * Processing - When processing takes place. During this
 * phase, user input is no longer recorded.
 * TradeScore is manipulated to produce a
 * finalized response for the computer to play.
 *                                      * associated method: .processInput()
 * 
 * Computer Turn - When the computer plays. During this phase,
 * the response finalized in processing phase
 * is played. When the computer is first,
 * some solo pre-generated solo is used.
 *                                      * associated method: .computerTurn()
 * 
 *
 * @author Zachary Kondak
 */
public class TradingWindow
        extends javax.swing.JFrame {

    static Notate notate;

    private int melodyNum = 0;
    private ArrayList<MelodyPart> melodies = new ArrayList<MelodyPart>();
    private LinkedList<MelodyPart> hotswapper = new LinkedList<MelodyPart>();
    private Score tradeScore;
    private LinkedList<Integer> triggers = new LinkedList();
    private int triggerIndex;
    private ResponseGenerator responseGenerator;

    private Integer scoreLength;
    private Integer slotsPerMeasure;
    private Integer slotsPerTurn;
    private Integer adjustedLength;
    private Integer slotsForProcessing;
    private Integer numberOfTurns;
    private Integer measures;
    private Integer nextSection;
    private String musician;
    private long lastPosition;
    private int[] metre;
    private ChordPart soloChords;
    private ChordPart responseChords;
    private MelodyPart response;
    private MidiSynth midiSynth;
    private long slotDelay;
    private boolean isTrading;
    private boolean isUserInputError;
    private boolean firstPlay;
    private boolean isUserLeading;
    private boolean isLoop;
    //TODO ADD COMMENT:
    private boolean loopLock;
    private TradePhase phase;
    private String tradeMode;
    private Transform transform;
    private PlayScoreCommand playCommand;
    private static MidiManager midiManager;
    //magic values
    private int endLimitIndex = 1;
    private boolean isSwing = true;
    private Integer snapResolution = 2;
    public static final int zero = 0;
    public static final int one = 1;
    public static final int DEFAULT_TRADE_LENGTH = 4;

    public enum TradePhase {

        USER_TURN,
        PROCESS_INPUT,
        COMPUTER_TURN
    }

    public enum TradeMode {

        REPEAT,
        REPEAT_AND_RECTIFY
    }

    /**
     * Creates new form TradingWindow
     *
     * @param notate
     */
    public TradingWindow(Notate notate) {
        initComponents();
        this.notate = notate;
        tradeScore = new Score();
        notate.populateGenericGrammarMenu(tradeGrammarMenu);

        //defaults on open
        updateTradeMode();
        populateMusicianList();
        updateMusician();
        updateTradeLength("4");
        updateTradeLengthText();
        updateTradeMode();
        firstPlay = true;
        isTrading = false;
        measures = DEFAULT_TRADE_LENGTH;
        isUserLeading = true;
        slotsForProcessing = BEAT / 2;
        midiManager = notate.getMidiManager();
        midiSynth = new MidiSynth(midiManager);
        midiSynth.setMasterVolume(volumeSlider.getValue());
        notate.setEnabled(false);
        setLoop();
        Double newTempo = notate.getTempo();
        this.tempoSlider.setValue(newTempo.intValue());
    }
    
    public void setNotateDefaults() {
        notate.populateGenericGrammarMenu(tradeGrammarMenu);
        firstPlay = true;
        isTrading = false;
        notate.setEnabled(false);
        setLoop();
        Double newTempo = notate.getTempo();
        this.tempoSlider.setValue(newTempo.intValue());
    }

    /**
     * Method converts a delay given in a factor of beats to delay in slots,
     *
     * @param beatDelay
     */
    private int beatsToSlots(float beatDelay) {
        int newDelay = Math.round(BEAT * beatDelay);
        return newDelay;
        //System.out.println(slotDelay);
    }

    private double slotsToBeats(double slots) {
        double newDelay = slots / BEAT;
        return newDelay;
        //System.out.println(slotDelay);
    }

    /**
     * Called continuously throughout trading. This method acts as a primitive
     * scheduler. Uses the stack 'triggers' to check if it is time to change
     * phases (userTurn, processInput, computerTurn)
     *
     * @param e
     */
    public void trackPlay(ActionEvent e) {
        long currentPosition = notate.getSlotInPlayback();
        //System.out.println(currentPosition);

        if ((triggers.isEmpty() || currentPosition == scoreLength) && !isLoop) {
            stopTrading();
        } else {
            long nextTrig = (long) triggers.get(triggerIndex);
            if (nextTrig <= currentPosition && !loopLock) {
                //System.out.println("Increment to trig: " + nextTrig);
                int nextIndex = triggerIndex + 1;
                if (nextIndex >= triggers.size()) {
                    if (!isLoop) {
                        stopTrading();
                    }
                    nextIndex = 0;
                }
                if (triggers.get(nextIndex) == 0) {
                    loopLock = true;
                    //System.out.println("Setting lock on");
                } else {
                    loopLock = false;
                }
                triggerIndex = nextIndex;
                //System.out.println("Trigs: " + triggers);
                switchTurn();
            }
        }
        //TODO : EXPLAIN THIS
        if (loopLock) {
            if (currentPosition < lastPosition) {
                loopLock = false;
            }
            lastPosition = currentPosition;
        }
    }

    /**
     * Switches between phases (userTurn, processInput, computerTurn). Called
     * by trackPlay.
     */
    public void switchTurn() {
        switch (phase) {
            case USER_TURN:
                processInput();
                break;
            case PROCESS_INPUT:
                computerTurn();
                break;
            case COMPUTER_TURN:
                userTurn();
                break;
            default:
                break;

        }

    }

    public void userTurn() {
        //System.out.println("User turn at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.USER_TURN;
        if (triggers.isEmpty()) {
            nextSection = adjustedLength;
        } else {
            nextSection = (triggers.get(triggerIndex) + slotsForProcessing) % adjustedLength;

        }
        //System.out.println("Chords extracted from chord prog from : " + nextSection + " to " + (nextSection + slotsPerTurn - one));
        response = new MelodyPart(slotsPerTurn);
        notate.initTradingRecorder(response);
        notate.enableRecording();
        soloChords = notate.getScore().getChordProg().extract(nextSection - slotsPerTurn, nextSection - one);
        responseChords = notate.getScore().getChordProg().extract(nextSection, nextSection + slotsPerTurn - one);
        tradeScore = new Score("trading", notate.getTempo(), zero);
        tradeScore.setChordProg(responseChords);
        tradeScore.addPart(response);
    }

    public void processInput() {
        //System.out.println("Process input at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.PROCESS_INPUT;
        notate.stopRecording();

        tradeScore.setBassMuted(true);
        tradeScore.delPart(0);

        //snap? aMelodyPart = aMelodyPart.applyResolution(120);
        //System.out.println(chords);
        applyTradingMode();
        tradeScore.deleteChords();

        Long delayCopy = new Long(slotDelay);
        response = response.extract(delayCopy.intValue(), slotsPerTurn - one, true, true);
        //System.out.println(response);
        tradeScore.addPart(response);
        //System.out.println("TRADE SCORE" + tradeScore);
        //System.out.println("NOTATE SCORE" + notate.getScore());

        midiSynth = new MidiSynth(midiManager);
        midiSynth.setMasterVolume(volumeSlider.getValue());
        //System.out.println("NOTATE: " + notate.getMidiSynth().getSequencer());
        //System.out.println("TRADING WINDOW: " + midiSynth.getSequencer());

        playCommand = new PlayScoreCommand(
                tradeScore,
                zero,
                isSwing,
                midiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                slotsPerTurn - endLimitIndex,
                true
        );
    }

    public void computerTurn() {

        long slotsBefore = notate.getSlotInPlayback();

        midiSynth.setSlot(notate.getSlotInPlayback() % slotsPerTurn);
        playCommand.execute();
        long slotsAfter = notate.getSlotInPlayback();

        //update delay
        slotDelay = (slotDelay + (slotsAfter - slotsBefore)) / 2;

        phase = TradePhase.COMPUTER_TURN;
    }

    /**
     * Starts interactive trading
     */
    public void startTrading() {
        //make this more general
        startTradingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif")));
        updateMusician();
        updateTradeMode();
        File directory = ImproVisor.getTransformDirectory();
        File file = new File(directory, musician + TransformFilter.EXTENSION);
        //String dir = System.getProperty("user.dir");
        //File file = new File(dir + "/transforms/"+musician+".transform");
        transform = new Transform(file);

        notate.setFirstTab();
        lastPosition = 0;
        loopLock = false;
        setIsUserLeading(userFirstButton.isSelected());
        startTradingButton.setText("Stop Trading");
        isTrading = true;
        midiSynth = new MidiSynth(midiManager);
        scoreLength = notate.getScoreLength();
        slotsPerMeasure = notate.getScore().getSlotsPerMeasure();
        metre = notate.getScore().getMetre();
        slotsPerTurn = measures * slotsPerMeasure;
        responseGenerator = new ResponseGenerator(notate, metre, slotsPerTurn);
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        if (!(adjustedLength == scoreLength)) {
            while (adjustedLength < scoreLength) {
                adjustedLength = adjustedLength + slotsPerTurn;
            }
            notate.getScore().setLength(adjustedLength);
            scoreLength = notate.getScoreLength();
        }
        numberOfTurns = adjustedLength / slotsPerTurn;
        notate.getCurrentStave().setSelection(0, scoreLength);
        notate.pasteMelody(new MelodyPart(scoreLength));
        notate.getCurrentStave().unselectAll();
        triggerIndex = 0;
        populateTriggers();
        //initDelay();

        //if computer is leading, generate a solo via selected grammar
        if (!isUserLeading) {
            tradeScore = new Score("trading", notate.getTempo(), zero);
            tradeScore.setBassMuted(true);
            tradeScore.delPart(0);
            response = responseGenerator.extractFromGrammarSolo(0, slotsPerTurn);
            Long delayCopy = new Long(slotDelay);
            MelodyPart adjustedResponse = response.extract(delayCopy.intValue(), slotsPerTurn - one, true, true);
            tradeScore.deleteChords();
            tradeScore.addPart(adjustedResponse);
            playCommand = new PlayScoreCommand(
                    tradeScore,
                    zero,
                    isSwing,
                    midiSynth,
                    notate,
                    zero,
                    notate.getTransposition(),
                    false,
                    slotsPerTurn - endLimitIndex,
                    true
            );
        }

        notate.playFirstChorus();

        if (isUserLeading) {
            phase = TradePhase.COMPUTER_TURN;
        } else {
            //TODO make a nice comment
            phase = TradePhase.PROCESS_INPUT;
            notate.getCurrentMelodyPart().altPasteOver(response, 0);
            notate.getCurrentMelodyPart().altPasteOver(new MelodyPart(slotsPerTurn), 0 + slotsPerTurn);
        }
    }

    //NO LONGER USED
    private void initDelay() {
        ChordPart testChords = notate.getScore().copy().getChordProg().extract(zero, slotsPerMeasure - one);
        MelodyPart testMelody = new MelodyPart(slotsPerMeasure);
        Score testScore = new Score();
        testScore.addPart(testMelody);
        testScore.setChordProg(testChords);
        MelodyPart testSolo = new MelodyPart(slotsPerMeasure / 2);
        Note newNote = new Note(C4, true, slotsPerMeasure - one);
        testSolo.addNote(newNote);
        Score soloScore = new Score();
        soloScore.addPart(testSolo);

        MidiSynth testMidiSynth = new MidiSynth(midiManager);

        testMidiSynth.setMasterVolume(zero);

        PlayScoreCommand testCommand = new PlayScoreCommand(
                soloScore,
                zero,
                isSwing,
                midiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                slotsPerTurn - endLimitIndex,
                true
        );

        testCommand.execute();

        new PlayScoreCommand(
                testScore,
                zero,
                isSwing,
                testMidiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                slotsPerTurn - endLimitIndex,
                true
        ).execute();

        midiSynth.setMasterVolume(zero);

        testCommand.execute();
        long slot1 = testMidiSynth.getSlot();
        testCommand.execute();
        long slot2 = testMidiSynth.getSlot();
        //System.out.println(slot2 - slot1);

        slotDelay = slot2 - slot1;

        midiSynth.stop("Trading Delay Initialization Complete");
        midiSynth.setMasterVolume(volumeSlider.getValue());
    }

    private void populateTriggers() {
        //clear triggers
        triggers.clear();
        //populate trigger stack (scheduler)
        boolean computerTurnNext;
        if (numberOfTurns % 2 == zero) {
            //even number of turns
            if (isUserLeading) {
                //user turn first.
                //this seems couter-intuitive, but this is the case since we're
                //working from the end of the score (backwards)
                computerTurnNext = false;

            } else {
                //computer turn first.
                computerTurnNext = true;
            }
        } else {
            //odd number of turns
            if (isUserLeading) {
                //user turn first.
                computerTurnNext = true;
            } else {
                //computer turn first.
                computerTurnNext = false;
            }
        }
        int length = adjustedLength;
        if (isLoop) {
            if ((numberOfTurns % 2) == 1) {
                //do this to deal with scores that have odd trading parts
                length = adjustedLength * 2;
                computerTurnNext = !computerTurnNext;
            }
        }
        for (int trigSlot = length; trigSlot >= zero; trigSlot = trigSlot - slotsPerTurn) {
            triggers.push(trigSlot);
            if (computerTurnNext) {
                computerTurnNext = false;
                if (trigSlot != zero) {
                    triggers.push(trigSlot - slotsForProcessing);
                }
            } else {
                computerTurnNext = true;
            }
        }
        if (isLoop) {
            triggers.removeLast();
            for (int i = 0; i < triggers.size(); i++) {
                int trig = triggers.get(i) % adjustedLength;
                triggers.set(i, trig);
            }
            //System.out.println(triggers);
        }
        //System.out.println(triggers);
    }

    /**
     * Stops interactive trading
     */
    public void stopTrading() {
        //System.out.println("hi");
        startTradingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif")));
        if (isTrading) {
            startTradingButton.setText("StartTrading");
            isTrading = false;
            notate.stopRecording();
            notate.stopPlaying("stop trading");
            midiSynth.stop("stop trading");

            //to avoid an extremely stange error that occurs when
            //the user presses sstop trading before the initialization
            //process has finished
            if (notate.getMidiRecorder() != null) {
                notate.getMidiRecorder().setDestination(null);
            }
        }
    }

    private void applyTradingMode() {
        updateTradeMode();
        responseGenerator.newResponse(response, soloChords, responseChords, nextSection);
        response = responseGenerator.response(transform, tradeMode);
        notate.getCurrentMelodyPart().altPasteOver(response, triggers.get(triggerIndex));
        notate.getCurrentMelodyPart().altPasteOver(new MelodyPart(slotsPerTurn), triggers.get(triggerIndex) + slotsPerTurn);
//        switch (tradeMode) {
//            case REPEAT_AND_RECTIFY:
//                repeatAndRectify();
//                break;
//            default:
//                break;
//        }
    }

//    private void changeTradeMode(String newMode) {
//        if (newMode.equals("Repeat")) {
//            tradeMode = TradeMode.REPEAT;
//        } else if (newMode.equals("Repeat and Rectify")) {
//            tradeMode = TradeMode.REPEAT_AND_RECTIFY;
//        } else {
//            tradeMode = TradeMode.REPEAT;
//            //System.out.println("Not a valid mode");
//        }
//    }
    private void changeTradeLength(String newLength) {
        measures = Integer.parseInt(newLength);
        //System.out.println("TRADE LENGTH SET: " + newLength);
    }

    private void setIsUserLeading(boolean isUserLeading) {
        this.isUserLeading = isUserLeading;
        //System.out.println("USER FIRST: " + isUserLeading);
    }

    public void updateProcessTime(float beats) {
        String lengthString = tradeLengthField.getText();
        int length = Integer.parseInt(lengthString);
        int maxLength = (((notate.getBeatsPerMeasure() * length) * BEAT) / 2);
        int slotLength = beatsToSlots(beats);
        //System.out.println("Slot length: " + slotLength);
        if (slotLength > maxLength) {
            slotsForProcessing = maxLength;
        } else if (slotLength <= 0) {
            slotsForProcessing = 1;
        } else {
            slotsForProcessing = slotLength;
        }
        //System.out.println("Slot delay now: " + slotsForProcessing);
    }

    public void updateProcessTimeText() {
        if (slotsForProcessing == 1) {
            ProcessTimeSelector.setText("0.0");
        } else {
            Double newBeatVal = slotsToBeats(slotsForProcessing);
            ProcessTimeSelector.setText(newBeatVal.toString());
        }
    }

    public void updateTradeLength(String newLength) {
        int length = 1;
        try {
            length = Integer.parseInt(newLength);
        } catch (Exception e) {
        }

        int numOfMeasuresInScore = notate.getScoreLength() / notate.getScore().getSlotsPerMeasure();
        if (length > numOfMeasuresInScore) {
            length = numOfMeasuresInScore;
        } else if (length < 1) {
            length = 1;
        } else {
            int mod = numOfMeasuresInScore % length;
            if (mod != 0) {
                int highLength = length;
                int lowLength = length;
                while (true) {
                    if (highLength >= numOfMeasuresInScore) {
                        length = numOfMeasuresInScore;
                        break;
                    } else if (lowLength <= 1) {
                        length = 1;
                        break;
                    } else if ((numOfMeasuresInScore % highLength) == 0) {
                        length = highLength;
                        break;
                    } else if ((numOfMeasuresInScore % lowLength) == 0) {
                        length = lowLength;
                        break;
                    } else {
                        lowLength--;
                        highLength++;
                    }
                }
            }
        }
        measures = length;
    }

    public void updateTradeLengthText() {
        Integer newLength = new Integer(measures);
        tradeLengthField.setText(newLength.toString());

        updateProcessTime(tryFloat(ProcessTimeSelector.getText()));
        updateProcessTimeText();
    }

    public void setVolume() {
        midiSynth.setMasterVolume(volumeSlider.getValue());
        Integer newVol = volumeSlider.getValue();
        volume.setText(newVol.toString() + "%");
    }
    
    public void setTempo(){
        notate.changeTempo(tempoSlider.getValue());
        Integer newTemp = tempoSlider.getValue();
        tempoLabel.setText(newTemp.toString());
        midiSynth.setTempo(newTemp.floatValue());
    }

    public void setLoop() {
        this.isLoop = loopToggle.isSelected();
        notate.setLoop(true);
    }

    private void updateTradeMode() {
        tradeMode = updateFromDropDown(modeMenu);
    }

    private void updateMusician() {
        musician = updateFromDropDown(tradeMusicianMenu);
    }

    private String updateFromDropDown(JMenu menu) {
        Component[] modes = menu.getMenuComponents();
        String selection = "";
        for (Component mode : modes) {
            JRadioButtonMenuItem modeButton = (JRadioButtonMenuItem) mode;
            if (modeButton.isSelected()) {
                selection = modeButton.getText();
                //System.out.println(selection);
                return selection;
            }
        }
        return selection;
    }
    
    private void tradingWindowClosed(){
        if (isTrading) {
            stopTrading();
        }
        notate.setEnabled(true);
        notate.setLoop(false);
        notate.tradingWindowClosed();
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

        leadingSelector = new javax.swing.ButtonGroup();
        modeSelector = new javax.swing.ButtonGroup();
        transformFileSelector = new javax.swing.ButtonGroup();
        grammarGroup = new javax.swing.ButtonGroup();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        colorRight = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        colorLeft = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        mainStuff = new javax.swing.JPanel();
        tradeLengthPanel = new javax.swing.JPanel();
        tradeLengthField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ProcessTimeSelector = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        volumePanel = new javax.swing.JPanel();
        volumeSlider = new javax.swing.JSlider();
        volume = new javax.swing.JLabel();
        leadSelectors = new javax.swing.JPanel();
        userFirstButton = new javax.swing.JRadioButton();
        improvisorFirstButton = new javax.swing.JRadioButton();
        playbackControls = new javax.swing.JPanel();
        startTradingButton = new javax.swing.JButton();
        loopToggle = new javax.swing.JCheckBox();
        tempoPanel = new javax.swing.JPanel();
        tempoSlider = new javax.swing.JSlider();
        tempoLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        mainTradeMenuBar = new javax.swing.JMenuBar();
        modeMenu = new javax.swing.JMenu();
        tradeRepeat = new javax.swing.JRadioButtonMenuItem();
        tradeRepeatAndRectify = new javax.swing.JRadioButtonMenuItem();
        tradeFlatten = new javax.swing.JRadioButtonMenuItem();
        tradeRandomModify = new javax.swing.JRadioButtonMenuItem();
        tradeFMR = new javax.swing.JRadioButtonMenuItem();
        tradeWithAMusician = new javax.swing.JRadioButtonMenuItem();
        tradeAbstract = new javax.swing.JRadioButtonMenuItem();
        tradeRhythmicResponse = new javax.swing.JRadioButtonMenuItem();
        tradeContourTest = new javax.swing.JRadioButtonMenuItem();
        tradeGrammarSolo = new javax.swing.JRadioButtonMenuItem();
        tradeUserMelody = new javax.swing.JRadioButtonMenuItem();
        tradeUserRhythm = new javax.swing.JRadioButtonMenuItem();
        tradeUserMelodyOrRhythm = new javax.swing.JRadioButtonMenuItem();
        tradeStore = new javax.swing.JRadioButtonMenuItem();
        tradeMusicianMenu = new javax.swing.JMenu();
        tradeGrammarMenu = new javax.swing.JMenu();

        jScrollPane1.setViewportView(jEditorPane1);

        setTitle("Active Trading - Impro-Visor");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(153, 153, 255));
        setMaximumSize(new java.awt.Dimension(600, 362));
        setMinimumSize(new java.awt.Dimension(600, 362));
        setPreferredSize(new java.awt.Dimension(600, 362));
        setResizable(false);
        setSize(new java.awt.Dimension(600, 362));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        colorRight.setBackground(new java.awt.Color(153, 153, 255));
        colorRight.setMinimumSize(new java.awt.Dimension(100, 0));

        javax.swing.GroupLayout colorRightLayout = new javax.swing.GroupLayout(colorRight);
        colorRight.setLayout(colorRightLayout);
        colorRightLayout.setHorizontalGroup(
            colorRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorRightLayout.createSequentialGroup()
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        colorRightLayout.setVerticalGroup(
            colorRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorRightLayout.createSequentialGroup()
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(colorRight, gridBagConstraints);

        colorLeft.setBackground(new java.awt.Color(153, 153, 255));
        colorLeft.setMinimumSize(new java.awt.Dimension(100, 0));

        javax.swing.GroupLayout colorLeftLayout = new javax.swing.GroupLayout(colorLeft);
        colorLeft.setLayout(colorLeftLayout);
        colorLeftLayout.setHorizontalGroup(
            colorLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorLeftLayout.createSequentialGroup()
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        colorLeftLayout.setVerticalGroup(
            colorLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorLeftLayout.createSequentialGroup()
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(colorLeft, gridBagConstraints);

        mainStuff.setBackground(new java.awt.Color(255, 255, 255));
        mainStuff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        mainStuff.setForeground(new java.awt.Color(255, 255, 255));
        mainStuff.setMaximumSize(new java.awt.Dimension(500, 340));
        mainStuff.setMinimumSize(new java.awt.Dimension(500, 340));
        mainStuff.setPreferredSize(new java.awt.Dimension(500, 340));
        mainStuff.setSize(new java.awt.Dimension(500, 340));
        mainStuff.setLayout(new java.awt.GridBagLayout());

        tradeLengthPanel.setBackground(new java.awt.Color(255, 255, 255));
        tradeLengthPanel.setMaximumSize(new java.awt.Dimension(200, 123));
        tradeLengthPanel.setMinimumSize(new java.awt.Dimension(200, 123));
        tradeLengthPanel.setPreferredSize(new java.awt.Dimension(200, 123));
        tradeLengthPanel.setLayout(new java.awt.GridBagLayout());

        tradeLengthField.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        tradeLengthField.setText("1");
        tradeLengthField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tradeLengthFieldCaretUpdate(evt);
            }
        });
        tradeLengthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tradeLengthFieldFocusLost(evt);
            }
        });
        tradeLengthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeLengthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        tradeLengthPanel.add(tradeLengthField, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        jLabel5.setText("Measures");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        tradeLengthPanel.add(jLabel5, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        jLabel3.setText("Processing Time");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        tradeLengthPanel.add(jLabel3, gridBagConstraints);

        ProcessTimeSelector.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        ProcessTimeSelector.setText("0.5");
        ProcessTimeSelector.setMaximumSize(new java.awt.Dimension(50, 2147483647));
        ProcessTimeSelector.setMinimumSize(new java.awt.Dimension(50, 28));
        ProcessTimeSelector.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ProcessTimeSelectorCaretUpdate(evt);
            }
        });
        ProcessTimeSelector.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ProcessTimeSelectorFocusLost(evt);
            }
        });
        ProcessTimeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProcessTimeSelectorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        tradeLengthPanel.add(ProcessTimeSelector, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        jLabel4.setText("Beats");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        tradeLengthPanel.add(jLabel4, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        jLabel2.setText("Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        tradeLengthPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        tradeLengthPanel.add(filler9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        mainStuff.add(tradeLengthPanel, gridBagConstraints);

        volumePanel.setBackground(new java.awt.Color(255, 255, 255));
        volumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume of Response", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 0, 12))); // NOI18N
        volumePanel.setMaximumSize(new java.awt.Dimension(200, 76));
        volumePanel.setMinimumSize(new java.awt.Dimension(200, 76));
        volumePanel.setPreferredSize(new java.awt.Dimension(200, 76));
        volumePanel.setLayout(new java.awt.GridBagLayout());

        volumeSlider.setMaximumSize(new java.awt.Dimension(150, 29));
        volumeSlider.setMinimumSize(new java.awt.Dimension(150, 29));
        volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        volumePanel.add(volumeSlider, gridBagConstraints);

        volume.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        volume.setText("50%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        volumePanel.add(volume, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        mainStuff.add(volumePanel, gridBagConstraints);

        leadSelectors.setBackground(new java.awt.Color(255, 255, 255));
        leadSelectors.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        leadSelectors.setMaximumSize(new java.awt.Dimension(200, 76));
        leadSelectors.setMinimumSize(new java.awt.Dimension(200, 76));
        leadSelectors.setPreferredSize(new java.awt.Dimension(200, 76));
        leadSelectors.setLayout(new java.awt.GridBagLayout());

        leadingSelector.add(userFirstButton);
        userFirstButton.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        userFirstButton.setSelected(true);
        userFirstButton.setText("User First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        leadSelectors.add(userFirstButton, gridBagConstraints);

        leadingSelector.add(improvisorFirstButton);
        improvisorFirstButton.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        improvisorFirstButton.setText("Impro-Visor First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        leadSelectors.add(improvisorFirstButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        mainStuff.add(leadSelectors, gridBagConstraints);

        playbackControls.setBackground(new java.awt.Color(255, 255, 255));
        playbackControls.setLayout(new java.awt.GridBagLayout());

        startTradingButton.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        startTradingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        startTradingButton.setLabel("Start Trading");
        startTradingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTradingButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 38;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 19, 11, 10);
        playbackControls.add(startTradingButton, gridBagConstraints);
        startTradingButton.getAccessibleContext().setAccessibleDescription("");

        loopToggle.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        loopToggle.setText("Loop");
        loopToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 19, 11, 1);
        playbackControls.add(loopToggle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        mainStuff.add(playbackControls, gridBagConstraints);

        tempoPanel.setBackground(new java.awt.Color(255, 255, 255));
        tempoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tempo (Beats/Minute)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica", 0, 12))); // NOI18N
        tempoPanel.setMaximumSize(new java.awt.Dimension(200, 76));
        tempoPanel.setMinimumSize(new java.awt.Dimension(200, 76));
        tempoPanel.setPreferredSize(new java.awt.Dimension(200, 76));
        tempoPanel.setLayout(new java.awt.GridBagLayout());

        tempoSlider.setMaximum(300);
        tempoSlider.setMinimum(40);
        tempoSlider.setValue(120);
        tempoSlider.setMaximumSize(new java.awt.Dimension(150, 29));
        tempoSlider.setMinimumSize(new java.awt.Dimension(150, 29));
        tempoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tempoSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tempoPanel.add(tempoSlider, gridBagConstraints);

        tempoLabel.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
        tempoLabel.setText("120");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tempoPanel.add(tempoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        mainStuff.add(tempoPanel, gridBagConstraints);
        tempoPanel.getAccessibleContext().setAccessibleName("Tempo (Beats/Minute)");

        jSeparator1.setPreferredSize(new java.awt.Dimension(12, 12));
        jSeparator1.setSize(new java.awt.Dimension(12, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(20, 12, 0, 14);
        mainStuff.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        mainStuff.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        mainStuff.add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        mainStuff.add(filler6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        mainStuff.add(filler7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        mainStuff.add(filler8, gridBagConstraints);

        getContentPane().add(mainStuff, new java.awt.GridBagConstraints());

        mainTradeMenuBar.setFont(new java.awt.Font("Helvetica", 0, 14)); // NOI18N

        modeMenu.setText("Trading Mode");
        modeSelector.add(modeMenu);
        modeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeMenuActionPerformed(evt);
            }
        });

        modeSelector.add(tradeRepeat);
        tradeRepeat.setSelected(true);
        tradeRepeat.setText("Repeat");
        modeMenu.add(tradeRepeat);

        modeSelector.add(tradeRepeatAndRectify);
        tradeRepeatAndRectify.setText("Repeat and Rectify");
        modeMenu.add(tradeRepeatAndRectify);

        modeSelector.add(tradeFlatten);
        tradeFlatten.setText("Flatten");
        modeMenu.add(tradeFlatten);

        modeSelector.add(tradeRandomModify);
        tradeRandomModify.setText("Random Modify");
        modeMenu.add(tradeRandomModify);

        modeSelector.add(tradeFMR);
        tradeFMR.setText("Flatten, Modify, Rectify");
        modeMenu.add(tradeFMR);

        modeSelector.add(tradeWithAMusician);
        tradeWithAMusician.setText("Trade with a Musician (Use Transforms)");
        modeMenu.add(tradeWithAMusician);

        modeSelector.add(tradeAbstract);
        tradeAbstract.setText("Abstract");
        modeMenu.add(tradeAbstract);

        modeSelector.add(tradeRhythmicResponse);
        tradeRhythmicResponse.setText("Rhythmic Response");
        modeMenu.add(tradeRhythmicResponse);

        modeSelector.add(tradeContourTest);
        tradeContourTest.setText("Contour Test");
        modeMenu.add(tradeContourTest);

        modeSelector.add(tradeGrammarSolo);
        tradeGrammarSolo.setText("Zach 1 - Grammar Solo");
        modeMenu.add(tradeGrammarSolo);

        modeSelector.add(tradeUserMelody);
        tradeUserMelody.setText("Zach 2 - User Melody");
        modeMenu.add(tradeUserMelody);

        modeSelector.add(tradeUserRhythm);
        tradeUserRhythm.setText("Zach 3 - User Rhythm");
        modeMenu.add(tradeUserRhythm);

        modeSelector.add(tradeUserMelodyOrRhythm);
        tradeUserMelodyOrRhythm.setText("Zach 4 - Last Two");
        modeMenu.add(tradeUserMelodyOrRhythm);

        modeSelector.add(tradeStore);
        tradeStore.setText("Zach 5 - Store");
        modeMenu.add(tradeStore);

        mainTradeMenuBar.add(modeMenu);

        tradeMusicianMenu.setText("Transform File");
        mainTradeMenuBar.add(tradeMusicianMenu);

        tradeGrammarMenu.setText("Grammar");
        mainTradeMenuBar.add(tradeGrammarMenu);

        setJMenuBar(mainTradeMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void populateMusicianList() {
        File directory = ImproVisor.getTransformDirectory();
        //System.out.println("populating from " + directory);
        if (directory.isDirectory()) {
            String fileName[] = directory.list();

            // 6-25-13 Hayden Blauzvern
            // Fix for Linux, where the file list is not in alphabetic order
            Arrays.sort(fileName, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return s1.toUpperCase().compareTo(s2.toUpperCase());
                }

            });

            // Add names of grammar files
            for (String name : fileName) {
                if (name.endsWith(TransformFilter.EXTENSION)) {
                    int len = name.length();
                    String stem = name.substring(0, len - TransformFilter.EXTENSION.length());
                    JRadioButtonMenuItem newMusician = new JRadioButtonMenuItem();
                    newMusician.setText(stem);
                    newMusician.setSelected(true);
                    transformFileSelector.add(newMusician);
                    tradeMusicianMenu.add(newMusician);
                }
            }
        }
    }


    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        tradingWindowClosed();
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

    private void ProcessTimeSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProcessTimeSelectorActionPerformed
        updateProcessTimeText();
    }//GEN-LAST:event_ProcessTimeSelectorActionPerformed

    private void ProcessTimeSelectorCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ProcessTimeSelectorCaretUpdate
        updateProcessTime(tryFloat(ProcessTimeSelector.getText()));
    }//GEN-LAST:event_ProcessTimeSelectorCaretUpdate

    private void ProcessTimeSelectorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ProcessTimeSelectorFocusLost
        updateProcessTimeText();
    }//GEN-LAST:event_ProcessTimeSelectorFocusLost

    private void volumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeSliderStateChanged
        setVolume();
    }//GEN-LAST:event_volumeSliderStateChanged

    private void tradeLengthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeLengthFieldActionPerformed
        updateTradeLengthText();
    }//GEN-LAST:event_tradeLengthFieldActionPerformed

    private void tradeLengthFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tradeLengthFieldCaretUpdate
        updateTradeLength(tradeLengthField.getText());
    }//GEN-LAST:event_tradeLengthFieldCaretUpdate

    private void tradeLengthFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tradeLengthFieldFocusLost
        updateTradeLengthText();
    }//GEN-LAST:event_tradeLengthFieldFocusLost

    private void modeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeMenuActionPerformed
        updateTradeMode();
    }//GEN-LAST:event_modeMenuActionPerformed

    private void loopToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToggleActionPerformed
        setLoop();
    }//GEN-LAST:event_loopToggleActionPerformed

    private void tempoSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tempoSliderStateChanged
        setTempo();
    }//GEN-LAST:event_tempoSliderStateChanged

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        tradingWindowClosed();
    }//GEN-LAST:event_formComponentHidden

    public void refreshSelectedGrammar(String gram) {
        this.tradeGrammarMenu.setText(gram);
    }

    private double tryDouble(String number) {
        double newNumber;
        try {
            newNumber = Double.parseDouble(number);
            isUserInputError = false;
        } catch (Exception e) {
            isUserInputError = true;
            newNumber = 0;
        }
        return newNumber;
    }

    private float tryFloat(String number) {
        float newNumber;
        try {
            newNumber = Float.parseFloat(number);
            isUserInputError = false;
        } catch (Exception e) {
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
    private javax.swing.JTextField ProcessTimeSelector;
    private javax.swing.JPanel colorLeft;
    private javax.swing.JPanel colorRight;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.ButtonGroup grammarGroup;
    private javax.swing.JRadioButton improvisorFirstButton;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel leadSelectors;
    private javax.swing.ButtonGroup leadingSelector;
    private javax.swing.JCheckBox loopToggle;
    private javax.swing.JPanel mainStuff;
    private javax.swing.JMenuBar mainTradeMenuBar;
    private javax.swing.JMenu modeMenu;
    private javax.swing.ButtonGroup modeSelector;
    private javax.swing.JPanel playbackControls;
    private javax.swing.JButton startTradingButton;
    private javax.swing.JLabel tempoLabel;
    private javax.swing.JPanel tempoPanel;
    private javax.swing.JSlider tempoSlider;
    private javax.swing.JRadioButtonMenuItem tradeAbstract;
    private javax.swing.JRadioButtonMenuItem tradeContourTest;
    private javax.swing.JRadioButtonMenuItem tradeFMR;
    private javax.swing.JRadioButtonMenuItem tradeFlatten;
    private javax.swing.JMenu tradeGrammarMenu;
    private javax.swing.JRadioButtonMenuItem tradeGrammarSolo;
    private javax.swing.JTextField tradeLengthField;
    private javax.swing.JPanel tradeLengthPanel;
    private javax.swing.JMenu tradeMusicianMenu;
    private javax.swing.JRadioButtonMenuItem tradeRandomModify;
    private javax.swing.JRadioButtonMenuItem tradeRepeat;
    private javax.swing.JRadioButtonMenuItem tradeRepeatAndRectify;
    private javax.swing.JRadioButtonMenuItem tradeRhythmicResponse;
    private javax.swing.JRadioButtonMenuItem tradeStore;
    private javax.swing.JRadioButtonMenuItem tradeUserMelody;
    private javax.swing.JRadioButtonMenuItem tradeUserMelodyOrRhythm;
    private javax.swing.JRadioButtonMenuItem tradeUserRhythm;
    private javax.swing.JRadioButtonMenuItem tradeWithAMusician;
    private javax.swing.ButtonGroup transformFileSelector;
    private javax.swing.JRadioButton userFirstButton;
    private javax.swing.JLabel volume;
    private javax.swing.JPanel volumePanel;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
