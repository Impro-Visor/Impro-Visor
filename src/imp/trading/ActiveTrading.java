/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2016 Robert Keller and Harvey Mudd College.
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

package imp.trading;

import static imp.Constants.BEAT;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.midi.MidiSynth;
import imp.data.Score;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import imp.midi.MidiManager;
import imp.util.TransformFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zach Kondak
 */
public class ActiveTrading {

    private final Notate notate;

    private Score tradeScore;
    private final LinkedList<Integer> triggers = new LinkedList();
    private int triggerIndex;
    private TradingResponseContoller tradeResponseController;

    private Integer scoreLength;
    private Integer slotsPerMeasure;
    private Integer slotsPerTurn;
    private Integer adjustedLength;
    private Integer slotsForProcessing;
    private Integer numberOfTurns;
    private Integer measures;
    private Integer nextSection;
    private Integer volume;
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
    private static final int END_LIMIT_INDEX = 1;
    private final javax.swing.JCheckBox swingCheckBox;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int DEFAULT_TRADE_LENGTH = 4;
    public static final String DEFAULT_TRADE_MODE = "Transform";
    public static final int DEFAULT_VOLUME = 80;
    private final LinkedList<TradeListener> tradeListeners = new LinkedList<TradeListener>();

    public enum TradePhase {
        USER_TURN,
        PROCESS_INPUT,
        COMPUTER_TURN
    }

    /**
     * Creates new form TradingWindow
     *
     * @param notate
     * @param swingCheckBox
     */
    public ActiveTrading(Notate notate, javax.swing.JCheckBox swingCheckBox) {
        this.notate = notate;
        this.swingCheckBox = swingCheckBox;
        tradeScore = new Score();
        
        //defaults on open
        this.tradeMode = DEFAULT_TRADE_MODE;
        this.measures = DEFAULT_TRADE_LENGTH;
        this.slotsPerTurn = notate.getSlotsPerMeasure() * measures;
        firstPlay = true;
        isTrading = false;
        isUserLeading = true;
        slotsForProcessing = BEAT / 2;
        volume = DEFAULT_VOLUME;
        midiManager = notate.getMidiManager();
        midiSynth = new MidiSynth(midiManager);
        midiSynth.setMasterVolume(volume);
    }
    
    public int getSlotsForProcessing(){
        return slotsForProcessing;
    }
    
    public int getMeasures(){
        return measures;
    }
    
    public boolean getIsLoop(){
        return this.isLoop;
    }
    
    public int getVolume(){
        return this.volume;
    }
    
    public boolean getIsUserLeading(){
        return this.isUserLeading;
    }
    
    public double getTempo(){
        return notate.getTempo();
    }
    
    public String getTradeMode(){
        return this.tradeMode;
    }
    
    public String getMusician(){
        return this.musician;
    }
    
    public String getGrammar(){
        return notate.getSelectedGrammar();
    }
    
    public void setProcessTime(float beats) {
        int slotLength = beatsToSlots(beats);
        //System.out.println("Slot length: " + slotLength);
        if (slotLength > slotsPerTurn) {
            slotsForProcessing = slotsPerTurn;
        } else if (slotLength <= 0) {
            slotsForProcessing = 1;
        } else {
            slotsForProcessing = slotLength;
        }
        //System.out.println("Slot delay now: " + slotsForProcessing);
    }
    
    public void setVolume(int volume){
        this.volume = volume;
        midiSynth.setMasterVolume(volume);
    }
    
    public void setTradeMode(String tradeMode){
        this.tradeMode = tradeMode;
    }
    
    public void setLoop(boolean isLoop){
        this.isLoop = isLoop;
        notate.setLoop(true);
    }
    
    public void setTempo(int tempo) {
        notate.changeTempo(tempo);
        Integer newTemp = tempo;
        midiSynth.setTempo(newTemp.floatValue());
    }
    
     public void setMusician(String musician){
        this.musician = musician;
    }
     
     public void setIsUserLeading(boolean isUserLeading){
         this.isUserLeading = isUserLeading;
     }
     
     public void setNotateDefaults() {
        firstPlay = true;
        isTrading = false;
        //notate.setEnabled(false);
        notate.setLoop(true);
    }
     
     public void register(TradeListener tradeListener){
         tradeListeners.add(tradeListener);
     }

    /**
     * Method converts a delay given in a factor of beats to delay in slots,
     *
     * @param beatDelay
     */
    public int beatsToSlots(float beatDelay) {
        int newDelay = Math.round(BEAT * beatDelay);
        return newDelay;
        //System.out.println(slotDelay);
    }

    public double slotsToBeats(double slots) {
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
                if (nextTrig == 0 && !firstPlay && !isLoop) {
                    stopTrading();
                } else {
                    firstPlay = false;
                    //System.out.println("Increment to trig: " + nextTrig + " at index " + triggerIndex);
                    int nextIndex = triggerIndex + 1;
                    //System.out.println("Triggers length " + triggers.size() + " next index " + nextIndex);
                    if (nextIndex >= triggers.size()) {
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
        int nextSectionIndex = (triggerIndex + 1) % triggers.size();
        nextSection = triggers.get(nextSectionIndex);
        //System.out.println("Chords extracted from chord prog from : " + nextSection + " to " + (nextSection + slotsPerTurn - one));
        response = new MelodyPart(slotsPerTurn);
        notate.initTradingRecorder(response);
        notate.enableRecording();
        int sectionStart;
        int sectionEnd;
        if (nextSection == 0){
            sectionStart = adjustedLength - slotsPerTurn;
            sectionEnd = adjustedLength - 1;
        } else {
            sectionStart = nextSection - slotsPerTurn;
            sectionEnd = nextSection - 1;
        }
        ChordPart chordProg = notate.getChordProg();
        soloChords = chordProg.extract(sectionStart, sectionEnd);
        responseChords = chordProg.extract(nextSection, nextSection + slotsPerTurn - 1);
        tradeScore = new Score("trading", notate.getTempo(), ZERO);
        tradeScore.setChordProg(responseChords);
        tradeScore.addPart(response);
    }

    public void processInput() {
        //System.out.println("Process input at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.PROCESS_INPUT;
        notate.stopRecording();

        int userStartIndex = triggerIndex - 2;
        if(userStartIndex < 0){
            userStartIndex = triggers.size() + userStartIndex;
        }
        int userStartSlot = triggers.get(userStartIndex);
        MelodyPart melodyPart = notate.getCurrentMelodyPart();
        melodyPart.altPasteOver(response, userStartSlot);
        melodyPart.altPasteOver(new MelodyPart(slotsPerTurn), (userStartSlot + slotsPerTurn) % this.adjustedLength);

        tradeScore.setBassMuted(true);
        tradeScore.delPart(0);

        //snap? aMelodyPart = aMelodyPart.applyResolution(120);
        //System.out.println(chords);
        applyTradingMode();
        tradeScore.deleteChords();

        Long delayCopy = slotDelay;
        response = response.extract(delayCopy.intValue(), slotsPerTurn - ONE, true, true);
        //System.out.println(response);
        tradeScore.addPart(response);
        //System.out.println("TRADE SCORE" + tradeScore);
        //System.out.println("NOTATE SCORE" + notate.getScore());

        midiSynth = new MidiSynth(midiManager);
        midiSynth.setMasterVolume(volume);
        //System.out.println("NOTATE: " + notate.getMidiSynth().getSequencer());
        //System.out.println("TRADING WINDOW: " + midiSynth.getSequencer());

        playCommand = new PlayScoreCommand(
                tradeScore,
                ZERO,
                swingCheckBox.isSelected(),
                midiSynth,
                notate,
                ZERO,
                notate.getTransposition(),
                false,
                slotsPerTurn - END_LIMIT_INDEX,
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

    private void notifyListeners(boolean isTradeStarted) {
        if (isTradeStarted) {
            for (TradeListener tradeListener : tradeListeners) {
                tradeListener.tradingStarted();
            }
        } else {
            for (TradeListener tradeListener : tradeListeners) {
                tradeListener.tradingStopped();
            }
        }
    }

    /**
     * Starts interactive trading
     */
    public void startTrading() {
        notifyListeners(true);
        //make this more general
        File directory = ImproVisor.getTransformDirectory();
        File file = new File(directory, musician + TransformFilter.EXTENSION);
        //String dir = System.getProperty("user.dir");
        //File file = new File(dir + "/transforms/"+musician+".transform");
        transform = new Transform(file);

        response = new MelodyPart();
        firstPlay = true;
        notate.setFirstTab();
        lastPosition = 0;
        loopLock = false;
        isTrading = true;
        midiSynth = new MidiSynth(midiManager);
        scoreLength = notate.getScoreLength();
        slotsPerMeasure = notate.getScore().getSlotsPerMeasure();
        metre = notate.getScore().getMetre();
        slotsPerTurn = measures * slotsPerMeasure;
        tradeResponseController = new TradingResponseContoller(notate, metre, slotsPerTurn);
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        numberOfTurns = adjustedLength / slotsPerTurn;
        notate.getCurrentStave().setSelection(0, scoreLength);
        notate.pasteMelody(new MelodyPart(scoreLength));
        notate.getCurrentStave().unselectAll();
        triggerIndex = 0;
        populateTriggers();
        //initDelay();

        //if computer is leading, generate a solo via selected grammar
        if (!isUserLeading) {
            tradeScore = new Score("trading", notate.getTempo(), ZERO);
            tradeScore.setBassMuted(true);
            tradeScore.delPart(0);
            response = tradeResponseController.extractFromGrammarSolo(0, slotsPerTurn);
            Long delayCopy = slotDelay;
            MelodyPart adjustedResponse = response.extract(delayCopy.intValue(), slotsPerTurn - ONE, true, true);
            //notate.establishCountIn(tradeScore);  // Doesn't work for Impro-Visor first
            tradeScore.addPart(adjustedResponse);
            playCommand = new PlayScoreCommand(
                    tradeScore,
                    ZERO,
                    swingCheckBox.isSelected(),
                    midiSynth,
                    notate,
                    ZERO,
                    notate.getTransposition(),
                    false,
                    slotsPerTurn - END_LIMIT_INDEX,
                    true
            );
        }

        midiSynth.setMasterVolume(volume);
        notate.playFirstChorus();

        if (isUserLeading) {
            phase = TradePhase.COMPUTER_TURN;
        } else {
            //TODO make a nice comment
            phase = TradePhase.PROCESS_INPUT;
            MelodyPart currentMelodyPart = notate.getCurrentMelodyPart();
            currentMelodyPart.altPasteOver(response, 0);
            currentMelodyPart.altPasteOver(new MelodyPart(slotsPerTurn), 0 + slotsPerTurn);
        }
    }

    private void populateTriggers() {
        //clear triggers
        triggers.clear();
        //populate trigger stack (scheduler)
        boolean computerTurnNext;
        if (numberOfTurns % 2 == ZERO) {
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
        for (int trigSlot = length; trigSlot >= ZERO; trigSlot = trigSlot - slotsPerTurn) {
            triggers.push(trigSlot);
            if (computerTurnNext) {
                computerTurnNext = false;
                if (trigSlot != ZERO) {
                    triggers.push(trigSlot - slotsForProcessing);
                }
            } else {
                computerTurnNext = true;
            }
        }
        triggers.removeLast();
        for (int i = 0; i < triggers.size(); i++) {
            int trig = triggers.get(i) % adjustedLength;
            triggers.set(i, trig);
        }
        //System.out.println(triggers);
    }

    /**
     * Stops interactive trading
     */
    public void stopTrading() {
        notifyListeners(false);
        if (isTrading) {
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
        try {
            tradeResponseController.updateResponse(response, soloChords, responseChords, nextSection, transform);
            response = tradeResponseController.response(tradeMode);
            //System.out.println(response);
            MelodyPart currentMelodyPart = notate.getCurrentMelodyPart();
            currentMelodyPart.altPasteOver(response, (triggers.get(triggerIndex) % adjustedLength));
            currentMelodyPart.altPasteOver(new MelodyPart(slotsPerTurn), triggers.get(triggerIndex) + slotsPerTurn);
        } catch (ExceptionTradeModeNotFound ex) {
            Logger.getLogger(ActiveTrading.class.getName()).log(Level.SEVERE, null, ex);
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
        }
        measures = length;
    }
    
    
    public void tradingClosed() {
        if (isTrading) {
            stopTrading();
        }
        //notate.setEnabled(true);
        notate.setLoop(false);
        //notate.tradingDialogClosed();
    }

    public void startOrSsettop() {
        if (!isUserInputError) {
            if (!isTrading) {
                //System.out.println("Starting");
                startTrading();
            } else {
                stopTrading();
            }
        }
    }

}
