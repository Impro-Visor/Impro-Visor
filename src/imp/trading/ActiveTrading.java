/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2018 Robert Keller and Harvey Mudd College.
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
import imp.style.Style;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import imp.midi.MidiManager;
import imp.trading.tradingResponseModes.CorrectRhythmTRM;
import imp.trading.tradingResponseModes.TradingResponseMode;
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
    private TradingResponseController tradeResponseController;

    private Integer scoreLength;
    private Integer slotsPerBar;
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
    private MelodyPart responseToUser;
    private MidiSynth midiSynth;
    private long slotDelay;
    private boolean isTrading;
    private boolean firstPlay;
    private boolean isUserLeading;
    private boolean isLoop;
    //TODO ADD COMMENT:
    private boolean loopLock;
    private TradePhase phase;
    private TradingResponseMode tradeMode;
    private Transform currentTransform;
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
    private final LinkedList<TradeListener> tradeListeners = new LinkedList<>();
    private int slotsPerChorus;
    
    //fields used in functionality changes for asynchronous processing
    // The slot where we will check if the next desired userResponse part 
    // (during the computer's turn) is done processing.
    private int nextPartOffset; 
    
    //The slot when the current computer turn started
    private int currentComputerTurnStartSlot; 
    
    // The number of slots to request the next part from the 
    // tradingResponseController in advance of when that part will be played
    //private final int generatedPartPasteBuffer = 50; 
    private int nextPlayPartOffset;
        
    public enum TradePhase {
        USER_TURN,
        PROCESS_INPUT,
        COMPUTER_TURN,
        NONE
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
        //defaults on open
        //tradeScore = new Score();
        slotsPerBar = notate.getSlotsPerMeasure();
        measures = DEFAULT_TRADE_LENGTH;
        slotsPerTurn = slotsPerBar * measures;
        firstPlay = true;
        isTrading = false;
        isUserLeading = true;
        slotsForProcessing = BEAT;
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
    
    public int getSlotsPerTurn()
    {
        return getMeasures() * slotsPerBar;
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
    
    public String getTradeModeName(){
        if(tradeMode == null)
            return "unassigned";
        else
            return this.tradeMode.toString();
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
    
    public void setTradeMode(TradingResponseMode tradeMode){
        this.tradeMode = tradeMode;
    }
    
    public TradingResponseMode getTradeMode(){
        return this.tradeMode;
    }
    
    public void setLoop(boolean isLoop){
        this.isLoop = isLoop;
        notate.setLooping(true);
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
        notate.setLooping(true);
    }
     
     public void register(TradeListener tradeListener){
         tradeListeners.add(tradeListener);
     }

    /**
     * Method converts a delay given in a factor of beats to delay in slots,
     *
     * @param beatDelay
     * @return 
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

    Score makeTradeScore(MelodyPart response)
    {
    Score newScore = new Score("trading", notate.getTempo(), ZERO);
    newScore.setBassMuted(true);
    newScore.delPart(0);
    newScore.addPart(response);
    return newScore;
    }
    
    PlayScoreCommand makePlayCommand(Score tradeScore, 
                                     boolean swing, 
                                     MidiSynth midiSynth)
    {
    PlayScoreCommand command = 
        new PlayScoreCommand(
                    tradeScore,
                    ZERO,
                    swing,
                    midiSynth,
                    notate,
                    ZERO,
                    notate.getTransposition(),
                    false,
                    slotsPerTurn - END_LIMIT_INDEX,
                    true
                    );  
    return command;
    }
    
    /**
     * Called continuously throughout trading. This method acts as a primitive
     * scheduler. Uses the stack 'triggers' to check if it is time to change
     * phases (userTurn, processInput, computerTurn)
     *
     * @param e
     */
    public void trackPlay(ActionEvent e)
    {
        long currentPosition = notate.getSlotInPlayback();
        //System.out.println(currentPosition);

        if( (triggers.isEmpty() || currentPosition == scoreLength) && !isLoop )
          {
            stopTrading();
          }
        else
          {
            long nextTrig = (long) triggers.get(triggerIndex);
            //if you passed the point to trigger the next phase
            if( nextTrig <= currentPosition && !loopLock )
              {
                if( nextTrig == 0 && !firstPlay && !isLoop )
                  {
                    stopTrading();
                  }
                else
                  {
                    firstPlay = false;
                    //System.out.println("Increment to trig: " + nextTrig + 
                    //                     " at index " + triggerIndex);
                    int nextIndex = triggerIndex + 1;
                    //System.out.println("Triggers length " + triggers.size() + 
                    //                     " next index " + nextIndex);
                    if( nextIndex >= triggers.size() )
                      {
                        nextIndex = 0;
                      }
                    loopLock = triggers.get(nextIndex) == 0; 
                    //System.out.println("Setting lock on");
                    triggerIndex = nextIndex;
                    //System.out.println("Trigs: " + triggers);
                    switchTurn();
                  }
              }
          }

        //if the current slot is nearing the slot for which we need the melody
        // userResponse part, go grab it and any other available parts
        
// Not sure, but this seems to be redundant, generating part that is overwritten
//        int value = currentComputerTurnStartSlot + nextPartOffset 
//                  - generatedPartPasteBuffer;
//        if( tradeResponseController.hasNext()
//         && value < currentPosition && !isUserLeading )  // MAYBE && !isUserLeading
//          {
//            //System.out.println("At slot for retrieving next parts: " 
//            //                     + currentPosition);
//            // We need the next part to play asap, and give us all additional 
//            // ready parts, if any.
//            
//            pasteNextAvailableParts(true); 
//            tradeScore = makeTradeScore(userResponse);
//            
//            int slot = currentComputerTurnStartSlot;
//            int barNumber = 1 + (slot/slotsPerMeasure);
//            Style currentStyle = notate.getStyleAtSlot(slot + slotsPerTurn);
//            double swing = notate.getSwingAtSlot(slot + slotsPerTurn);
//            tradeScore.setStyle(currentStyle);
//            System.out.println("C at " + barNumber + " swing = " + 
//                               swing + " " + currentStyle.getName());
//            playCommand = makePlayCommand(tradeScore, swing > 0.5, midiSynth);
//          }

        /*
         * This if statement checks if we need to refresh playback on our
         * midiSynth.
         * As parts are generated and added to userResponse, if the current
         * midiSynth has already been started, it will not play the added parts.
         * Thus, in the if statement above, a playCommand to play the most up to
         * date userResponse parts on the midiSynth is created, and in this if
         * statement the playCommand is executed
         */
        if( phase == TradePhase.COMPUTER_TURN
         && nextPlayPartOffset < nextPartOffset
         && currentComputerTurnStartSlot + nextPlayPartOffset < currentPosition )
          {
            //System.out.println("activating new midi Synth when 
            // next part offset is " + nextPartOffset);
            
            long slotsBefore = notate.getSlotInPlayback();
            midiSynth.setSlot(nextPlayPartOffset);
            nextPlayPartOffset = nextPartOffset;
            
            playCommand.execute();
            
            long slotsAfter = notate.getSlotInPlayback();

            //update delay
            slotDelay = (slotDelay + (slotsAfter - slotsBefore)) / 2;
          }

        //TODO : EXPLAIN THIS
        if( loopLock )
          {
            if( currentPosition < lastPosition )
              {
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

    public void userTurn()
    {
        //System.out.println("User turn at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.USER_TURN;
        int nextSectionIndex = (triggerIndex + 1) % triggers.size();
        nextSection = triggers.get(nextSectionIndex);
        //System.out.println("Chords extracted from chord prog from : " + 
        // nextSection + " to " + (nextSection + slotsPerTurn - one));
        responseToUser = new MelodyPart(slotsPerTurn);
        notate.initTradingRecorder(responseToUser);
        notate.enableRecording();
        int sectionStart;
        int sectionEnd;
        if( nextSection == 0 )
          {
            sectionStart = adjustedLength - slotsPerTurn;
            sectionEnd = adjustedLength - 1;
          }
        else
          {
            sectionStart = nextSection - slotsPerTurn;
            sectionEnd = nextSection - 1;
          }
        ChordPart chordProg = notate.getChordProg();
        soloChords = chordProg.extract(sectionStart, sectionEnd);
        responseChords = chordProg.extract(nextSection,
                                           nextSection + slotsPerTurn - 1);

        //start generation of the userResponse by passing the next section 
        // of chords to the trading userResponse controller
        tradeResponseController.startTradingGeneration(responseChords,
                                                       nextSection);
        tradeScore = makeTradeScore(responseToUser);
        tradeScore.setChordProg(responseChords);
    }

    /**
     * Process the user's input
     */
    public void processInput()
    {
        //System.out.println("Process input at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.PROCESS_INPUT;
        notate.stopRecording();

        int userStartIndex = triggerIndex - 2;
        if( userStartIndex < 0 )
          {
            userStartIndex = triggers.size() + userStartIndex;
          }
        int userStartSlot = triggers.get(userStartIndex);
        
        MelodyPart currentMelodyPart = notate.getCurrentMelodyPart();
        //System.out.println("\nbefore paste at " + userStartSlot + ": " + currentMelodyPart);
        currentMelodyPart.altPasteOver(responseToUser, userStartSlot);
        if( !isUserLeading )
          {
          maybeSaveImprovChorus();
          }
        currentMelodyPart.altPasteOver(new MelodyPart(slotsPerTurn),
                          (userStartSlot + slotsPerTurn) % this.adjustedLength);
        //System.out.println(" after paste at " + userStartSlot + ": " + currentMelodyPart);
        // trigger index is incremented before calling processInput, 
        // so triggerIndex points to the computer turn's trigger
        nextPartOffset = 0;
        nextPlayPartOffset = 0;

        currentComputerTurnStartSlot = triggers.get(triggerIndex);
        //System.out.println("Next computer turn will be at slot: " + 
        // currentComputerTurnStartSlot);
        tradeScore.setBassMuted(true);
        tradeScore.delPart(0);

        //snap? aMelodyPart = aMelodyPart.applyResolution(120);
        //System.out.println(chords);
        applyTradingMode();
        tradeScore.deleteChords();

        Long delayCopy = slotDelay;
        responseToUser = responseToUser.extract(delayCopy.intValue(), 
                                    slotsPerTurn - ONE,
                                    true, 
                                    true);
        //System.out.println(userResponse);
        tradeScore.addPart(responseToUser);
        //System.out.println("TRADE SCORE" + tradeScore);
        //System.out.println("NOTATE SCORE" + notate.getScore());

        midiSynth = new MidiSynth(midiManager);
        midiSynth.setMasterVolume(volume);
        //System.out.println("NOTATE: " + notate.getMidiSynth().getSequencer());
        //System.out.println("TRADING WINDOW: " + midiSynth.getSequencer());

        // This version works if the user goes first.
        // It also works if Impro-Visor goes first, except
        // it doesn't get the swing right on the first trade.
        
        // This command will be executed in computerTurn()
        int currentSlot = notate.getSlotInPlayback() % slotsPerChorus;
        int styleSlot = (currentSlot - slotsPerTurn) % slotsPerChorus;
        // Force styleSlot to be 0 if near the end of the chorus
        if( currentSlot + slotsPerTurn > slotsPerChorus )
          {
            styleSlot = 0;
          }
        //System.out.println("\ncurrentSlot = " + currentSlot + " styleSlot = " 
        //+ styleSlot + " slotsPerTurn = " 
        //+ slotsPerTurn + " slotsPerChorus = " + slotsPerChorus);

        //int styleBar = 1 + styleSlot/slotsPerBar;
        //int barNumber = 1 + currentSlot/slotsPerBar;
        Style currentStyle = notate.getStyleAtSlot(styleSlot);
        double swing = notate.getSwingAtSlot(styleSlot);
        tradeScore.setStyle(currentStyle);
        //System.out.println("D at " + barNumber + " styleBar = " 
        //+ styleBar + " swing = " + swing + 
        //" " + currentStyle.getName());

        playCommand = makePlayCommand(tradeScore, swing > 0.6, midiSynth);
    }

    /**
     * computerTurn() is called only by switchTurn()
     */
    public void computerTurn() {
        //System.out.println("Computer turn at slot: " + 
        //                   notate.getSlotInPlayback());
        
        nextPlayPartOffset = nextPartOffset;
        long slotsBefore = notate.getSlotInPlayback();

        midiSynth.setSlot(slotsBefore % slotsPerTurn);
        
        playCommand.execute();
        
        long slotsAfter = notate.getSlotInPlayback();

        //update delay
        slotDelay = (slotDelay + (slotsAfter - slotsBefore)) / 2;

        phase = TradePhase.COMPUTER_TURN;
    }

    private void notifyListeners(boolean isTradeStarted)
    {
        if( isTradeStarted )
          {
            for( TradeListener tradeListener : tradeListeners )
              {
                tradeListener.tradingStarted();
              }
          }
        else
          {
            for( TradeListener tradeListener : tradeListeners )
              {
                tradeListener.tradingStopped();
              }
          }
    }

    public TradingResponseController getTradeResponseController(){
        return this.tradeResponseController;
    }

    
    /**
     * This is used for the rhythm helper, not normal trading.
     * @param activeTradingDialog 
     */
    public void showGoalsDialog(TradingDialog activeTradingDialog){
        notifyListeners(true);
        //make this more general      
        phase = TradePhase.NONE;
        
        scoreLength = notate.getScoreLength();
        slotsPerBar = notate.getScore().getSlotsPerMeasure();
        metre = notate.getScore().getMetre();
        slotsPerTurn = measures * slotsPerBar;
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        numberOfTurns = adjustedLength / slotsPerTurn;
        
        triggerIndex = 0;
        populateTriggers();
        try {
            tradeResponseController = 
                    new TradingResponseController(notate, 
                                                  metre, 
                                                  slotsPerTurn, 
                                                  tradeMode);
        } catch (ExceptionTradeModeNotFound ex) {
            Logger.getLogger(ActiveTrading.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
        
        showTradingGoalsDialog(activeTradingDialog);
    }
    
   /**
    * Used for the rhythm helper, not normal trading.
    */
    
    public void startTradingFromTradingGoalsDialog(){
        slotsPerChorus = notate.getSlotsPerChorus();
        //System.out.println("slotsPerChorus = " + slotsPerChorus);
        notifyListeners(true);
        //make this more general
        File directory = ImproVisor.getTransformDirectory();
        File file = new File(directory, getMusician() + TransformFilter.EXTENSION);
        currentTransform = new Transform(file);

        responseToUser = new MelodyPart();
        firstPlay = true;
        notate.setFirstTab();
        lastPosition = 0;
        loopLock = false;
        isTrading = true;
        midiSynth = new MidiSynth(midiManager);

        tradeResponseController.onStartTrading();
        
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
            responseToUser = ((CorrectRhythmTRM) tradeMode).getFirstRhythm();
            Long delayCopy = slotDelay;
            MelodyPart adjustedResponse = responseToUser.extract(delayCopy.intValue(), 
                                                 slotsPerTurn - ONE, true, true);
            //notate.establishCountIn(tradeScore);  
            // Doesn't work for Impro-Visor first
            tradeScore = makeTradeScore(adjustedResponse);
           // This command will be executed in next phase.
            int currentSlot = notate.getSlotInPlayback() % slotsPerChorus;
            //int barNumber = 1 + (currentSlot/slotsPerBar);
            Style currentStyle = notate.getStyleAtSlot(currentSlot);
            //double swing = notate.getSwingAtSlot(currentSlot);
            tradeScore.setStyle(currentStyle);

            //System.out.println("A at " + barNumber + " swing = " + 
            //                   swing + " " + currentStyle.getName());

            playCommand = makePlayCommand(                    
                    tradeScore,
                    swingCheckBox.isSelected(),
                    midiSynth
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
            currentMelodyPart.altPasteOver(responseToUser, 0);
            currentMelodyPart.altPasteOver(new MelodyPart(slotsPerTurn), 
                                                          0 + slotsPerTurn);
        }
    }
    
    
    private void showTradingGoalsDialog(TradingDialog activeTradingDialog){
        TradingGoalsDialog userGoalsDialog = 
                new TradingGoalsDialog(tradeMode, activeTradingDialog);
        userGoalsDialog.setLocation(TradingGoalsDialog.INITIAL_OPEN_POINT);
        userGoalsDialog.setSize(800, 400);
        userGoalsDialog.setVisible(true);              
    }

    /**
     * Starts interactive trading
     */
    public void startTrading()
    {
        notifyListeners(true);
        slotsPerChorus = notate.getSlotsPerChorus();
        //System.out.println("slotsPerChorus = " + slotsPerChorus);
        //make this more general
        File directory = ImproVisor.getTransformDirectory();
        File file = new File(directory,
                             getMusician() + TransformFilter.EXTENSION);
        currentTransform = new Transform(file);

        responseToUser = new MelodyPart();
        firstPlay = true;
        notate.setFirstTab();
        lastPosition = 0;
        loopLock = false;
        isTrading = true;
        midiSynth = new MidiSynth(midiManager);
        scoreLength = notate.getScoreLength();
        metre = notate.getScore().getMetre();
        slotsPerTurn = measures * slotsPerBar;
        assert( slotsPerTurn != 0 );
        try
          {
          tradeResponseController = 
          new TradingResponseController(notate, metre, slotsPerTurn, tradeMode);
            tradeResponseController.onStartTrading();
          }
        catch( ExceptionTradeModeNotFound ex )
          {
            Logger.getLogger(ActiveTrading.class.getName()).log(Level.SEVERE,
                                                                null, ex);
          }
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        numberOfTurns = adjustedLength / slotsPerTurn;
        notate.getCurrentStave().setSelection(0, scoreLength);
        notate.pasteMelody(new MelodyPart(scoreLength));
        notate.getCurrentStave().unselectAll();
        triggerIndex = 0;
        populateTriggers();
        //initDelay();

        //if computer is leading, generate a solo via selected grammar
        if( !isUserLeading )
          {
            responseToUser = tradeResponseController.
                    extractFromGrammarSolo(0, slotsPerTurn);
            Long delayCopy = slotDelay;
            MelodyPart adjustedResponse = responseToUser.extract(delayCopy.intValue(),
                                                           slotsPerTurn - ONE,
                                                           true, 
                                                           true);
            tradeScore = makeTradeScore(adjustedResponse);
            
            // This command will be executed in next phase.
            int currentSlot = notate.getSlotInPlayback() % slotsPerChorus;
            //int barNumber = 1 + (currentSlot/slotsPerBar);
            Style currentStyle = notate.getStyleAtSlot(currentSlot);
            double swing = notate.getSwingAtSlot(currentSlot);
            tradeScore.setStyle(currentStyle);
            //System.out.println("B at " + barNumber + " swing = " + 
            //                   swing + " " + currentStyle.getName());

            playCommand = makePlayCommand(
                    tradeScore,
                    swing > 0.6,
                    midiSynth);
          }

        midiSynth.setMasterVolume(volume);
        notate.playFirstChorus();

        if( isUserLeading )
          {
            phase = TradePhase.COMPUTER_TURN;
          }
        else
          {
            phase = TradePhase.PROCESS_INPUT;
          }
        MelodyPart currentMelodyPart = notate.getCurrentMelodyPart();
        currentMelodyPart.altPasteOver(responseToUser, 0);
        currentMelodyPart.altPasteOver(new MelodyPart(slotsPerTurn),
                                       0 + slotsPerTurn);
    }

    private void populateTriggers()
    {
        //clear triggers
        triggers.clear();
        //populate trigger stack (scheduler)
        boolean computerTurnNext;
        if( numberOfTurns % 2 == ZERO )
          {
            //even number of turns
            computerTurnNext = !isUserLeading; //user turn first.
            //this seems couter-intuitive, but this is the case since we're
            //working from the end of the score (backwards)
            //computer turn first.
          }
        else
          {
            //odd number of turns
            computerTurnNext = isUserLeading; //user turn first.
            //computer turn first.
          }
        int length = adjustedLength;
        if( isLoop )
          {
            if( (numberOfTurns % 2) == 1 )
              {
                //do this to deal with scores that have odd trading parts
                length = adjustedLength * 2;
                computerTurnNext = !computerTurnNext;
              }
          }
        for( int trigSlot = length; 
                 trigSlot >= ZERO; 
                 trigSlot = trigSlot - slotsPerTurn )
          {
            triggers.push(trigSlot);
            if( computerTurnNext )
              {
                computerTurnNext = false;
                if( trigSlot != ZERO )
                  {
                    triggers.push(trigSlot - slotsForProcessing);
                  }
              }
            else
              {
                computerTurnNext = true;
              }
          }
        triggers.removeLast();
        // This loop mods all of the triggers by the length 
        // of the leadsheet in slots
        
        for( int i = 0; i < triggers.size(); i++ )
          {
            int trig = triggers.get(i) % adjustedLength;
            triggers.set(i, trig);
          }
        //System.out.println("triggers = " + triggers);
    }

    /**
     * Stops interactive trading
     */
    public void stopTrading()
    {
        notifyListeners(false);
        if( isTrading )
          {
            isTrading = false;
            notate.stopRecording();
            notate.stopPlaying("stop trading");
            midiSynth.stop("stop trading");

            //to avoid an extremely stange error that occurs when
            //the user presses sstop trading before the initialization
            //process has finished
            if( notate.getMidiRecorder() != null )
              {
                notate.getMidiRecorder().setDestination(null);
              }
          }
    }
    
    private void pasteNextAvailableParts(boolean forceFirstPart)
    {
        //System.out.println("starting to paste parts");
        //use this boolean to make an easy toggleable do-while loop
        boolean isForcedPart = forceFirstPart;
        while( /* isForcedPart || */ tradeResponseController.hasNextReady() )
          {
            //System.out.println("pasting a ready part");
            
            // if the next part is ready, we'll paste it and increment 
            // nextGenerationCheckSlotOffset by its length.
            
            // Get the current melody part (of the entire leadsheet 
            // in which we're working from notate
            
            MelodyPart currentMelodyPart = notate.getCurrentMelodyPart(); 
            
            // Ask our tradeResponseController nicely for the next melody part 
            // (it will be ready immediately - we checked)

            MelodyPart responsePart = tradeResponseController.retrieveNext();
            
            //System.out.println("at " + nextPartOffset + ":" + responsePart);
            
            // Paste our generated userResponse part onto our userResponse part from  
            // which our own midiSynth is playing.
            
            responseToUser.altPasteOver(responsePart, nextPartOffset); 
            
            // Paste our generated userResponse onto the melody part from notate 
            // (so that it will be visible).
            
            currentMelodyPart.altPasteOver(responsePart,
              (currentComputerTurnStartSlot % adjustedLength) + nextPartOffset); 
            
            // Increment our slotOffset.
            
            nextPartOffset += responsePart.getSize(); 
            
            //System.out.println(responsePart.getSize());
            
            // regardless of whether we forced the first part.
            
            isForcedPart = false; 
          }
    }

    private void applyTradingMode()
    {
        tradeResponseController.setMusician(currentTransform);
        tradeResponseController.finishResponse(responseToUser, 
                                               soloChords,
                                               responseChords, 
                                               nextSection);
        /*
         * //Old usage of tradeResponseController, where you update information
         * and then perform calculations. ^above call is for new usage, where
         * calculating has been done in the background
         * tradeResponseController.updateResponse(userResponse, soloChords,
         * responseChords, nextSection, currentTransform);
         * userResponse = tradeResponseController.userResponse();
         */
        //System.out.println(userResponse);
        // For new trading userResponse system, while we have another melodyPart 
        // in the userResponse
        
        // Check whether this is pasting one part too many, over-writing the
        // first trade; I think it is okay now, with other changes.
        pasteNextAvailableParts(false);

        notate.getCurrentMelodyPart().altPasteOver(new MelodyPart(slotsPerTurn),
                                     triggers.get(triggerIndex) + slotsPerTurn);
        
        if( isUserLeading )
        {
            maybeSaveImprovChorus();
        }
    }

    private void maybeSaveImprovChorus()
    {
        if( notate.getSlotInPlayback() >= 
                notate.getChordProg().size() - 2 * slotsPerTurn )
          {
            //System.out.println("succeeded at " + notate.getSlotInPlayback() );
            notate.saveImprovChorus();
          }        
    }
    
    public void updateTradeLength(String newLength)
    {
        int length = 1;
        try
          {
            length = Integer.parseInt(newLength);
          }
        catch( Exception e )
          {
          }

        int numOfMeasuresInScore = notate.getScoreLength() / slotsPerBar;
        
        if( length > numOfMeasuresInScore )
          {
            length = numOfMeasuresInScore;
          }
        else if( length < 1 )
          {
            length = 1;
          }
        measures = length;
        slotsPerTurn = slotsPerBar * measures;
    }

}
