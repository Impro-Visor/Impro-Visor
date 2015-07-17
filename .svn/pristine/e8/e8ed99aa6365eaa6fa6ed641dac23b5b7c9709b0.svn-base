/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2013-2014 Robert Keller and Harvey Mudd College
 * XML export code is also Copyright (C) 2009-2011 Nicolas Froment (aka Lasconic).
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

// NOTE: Currently not active. If used, uncomment calls to notate.generate.

package imp.gui;

import imp.com.Command;
import imp.com.PlayScoreFastCommand;
import imp.data.MelodyPart;
import imp.data.Score;
import imp.data.Style;

/**
 * Class for controlling Trading over any grammar
 * @author Robert Keller
 */

public class Trading
{
Notate notate;

/**
 * Whether auto improvisation is to be done
 */

boolean selected = false;

/**
 * Tracing for auto improvisation
 */

boolean traceAutoImprov = false;

/**
 * whether Impro-Visor is to go first in trading, or not
 */

boolean ivFirst = true;

/**
 * The command that is executed to generate an improvised lick
 */

Command improCommand = null;

/*
 * Parameters for trading
 */

/**
 * The number of slots in one full cycle of trading.
 * This will be set by setImproInterval.
 */

int improInterval = 3840;

/**
 * One half of improInterval
 */

int halfInterval = 1920;


/**
 * The number of slots by which generation will lead use.
 *
 * Ultimately this should be made to depend on tempo, etc.
 */

int generationLeadSlots = 240;


/**
 * The number of slots by which the midiSynth should be
 * started before playing is required
 */

int playLeadSlots = 30;


/**
 * The lick that is generated, played, and inserted into the current
 * melody part
 */

MelodyPart improLick = null;


/**
 * The size of MelodyPart in slots
 */
int size;


/**
 * An indication of whether this generation is the very first, with
 * Impro-Visor starting
 */

boolean firstTime = false;

/**
 * Whether there is a lick generated, but not played
 */
boolean played = false;

/**
 * Whether there is a lick generated
 */

boolean generated = false;


/**
 * The long-term slot at which the next lick will be generated
 */

long generateAtSlot = 0;

/**
 * The long-term slot at which the next generation is to be played
 */

long playAtSlot = 0;

/**
 * The next long-term cycle at which generation will occur
 */

long nextGenerateCycle = 0;


/** The long-term view of when the next improvised melody will start
 *
 */

long melodyStart;


/**
 * The slot within the current chorus at which the improvised melody starts
 */

int melodyStartsAtSlot = 0;

/**
 * The number of cycles of trading within a chorus
 */

int numCycles;

int licksGenerated = 0;

public Trading(Notate notate)
  {
    this.notate = notate;
  }

public void reset()
  {
    size = notate.getCurrentMelodyPart().size();
    generateAtSlot = 0;
    melodyStartsAtSlot = 0;
    melodyStart = 0;
    improLick = null;
    improCommand = null;
    generated = false;
    played = false;
    numCycles = size / improInterval;
    nextGenerateCycle = 0;
    //firstTime = ivFirst == 0;
    setGenerationLeadSlots(480);
    setPlayLeadSlots(30);
    licksGenerated = 0;
  }


int failCounter;

public void autoImprovCycle(int slotInPlayback)
  {
    maybeCreateLick(slotInPlayback);
    maybePlayLick(slotInPlayback);
  }

/**
 * This creates the first melody at slot 0 when Impro-Visor is to begin
 * trading.
 * @param currentMelodyPart
 */

public MelodyPart createAndPlayInitialLick(MelodyPart melodyPart)
  {
  System.out.println("createAndPlayInitialLick");
  int countInOffset = notate.getScore().getCountInOffset();
    if( improLick != null && improLick.size() > 0 )
      {
        melodyStartsAtSlot = 0; //countInOffset;
        melodyStart = 0; //melodyStartsAtSlot;
        playAtSlot = 0;
        firstTime = true;
        played = false;
      }
    
    maybeCreateInitialLick(countInOffset);

    return improLick;
  }

public void maybeCreateInitialLick(int melodyStart)
  {
    long biasedCyclesElapsed = (notate.getTotalSlotsElapsed() + generationLeadSlots) / improInterval;
    //System.out.println("biasedCyclesElapsed = " + biasedCyclesElapsed + " nextGenerateCycle = " + nextGenerateCycle);

    // This prevents multiple generations within one cycle.
    // Trying to hinge on generated does not work.
    // Neither does hinging on slotInPlayback < generateAtSlot

    if( biasedCyclesElapsed < nextGenerateCycle )
      {
        return;
      }

//    melodyStart = ivFirst ? improInterval * biasedCyclesElapsed
//                          : improInterval * biasedCyclesElapsed + halfInterval;

    playAtSlot = Math.max(0, melodyStart - playLeadSlots);

    generateAtSlot = melodyStart - generationLeadSlots;

    MelodyPart melodyPart = notate.getImprovMelodyPart();
    if( melodyPart == null )
      {
        return;
      }

//    if( !generated 
//      && notate.getTotalSlotsElapsed() >= generateAtSlot 
//      )
      {
       // We are generating for the NEXT cycle, due to using the bias in triggering
      
        melodyStartsAtSlot = 0; // melodyStart;
        melodyStart = 0;
        playAtSlot = 0; //960;
              
        int partsize = melodyPart.size();

        int chordStartSlot = melodyStartsAtSlot % partsize; // TEMP
        int chordStopSlot = (melodyStartsAtSlot + halfInterval - 1)% partsize; // TEMP
        
        
        if( traceAutoImprov )
          {
          System.out.println("\ncreate lick for initial"
                           + ", melodyStart " + notate.bar(melodyStart)
                           + " to play at " + notate.bar(playAtSlot)
                           + ", chords from " + notate.bar(chordStartSlot) + " to " + notate.bar(chordStopSlot));
          }
        
        //improLick = notate.generate(chordStartSlot, chordStopSlot);

        generated = improLick != null && improLick.size() > 0;

        if( generated )
          {
            licksGenerated++;
            //System.out.println("licks generated = " + licksGenerated + ", numCycles = " + numCycles);
            if( licksGenerated > 1 && (licksGenerated - 1) % numCycles == 0 )
              {
                // Change leadsheet chorus number  for pasting
                notate.incrementMelodyPartIndex();
                melodyPart = notate.getImprovMelodyPart();
                if( melodyPart == null )
                  {
                    return;
                  }
              }
            nextGenerateCycle = biasedCyclesElapsed + 1;

            if( traceAutoImprov )
              {
                System.out.println("\n"  + notate.bar(notate.getTotalSlotsElapsed())
                        + ": result: "   + improLick
                        + ", generate: " + notate.bar(generateAtSlot)
                        + ", play: "     + notate.bar(playAtSlot)
                        + ", paste: "    + notate.bar(melodyStartsAtSlot)
                        + ", hear: "     + notate.bar(melodyStart));

                if( failCounter > 0 )
                  {
                    System.out.println(" generation succeeded after " + failCounter + " failures.");
                    failCounter = 0;
                  }
                else
                  {
                    System.out.println(" generation succeeded first time.");
                  }
              }

            played = false;
            Score improScore = new Score();
            improScore.setTempo(notate.getTempo());

            improLick.setInstrument(notate.getMelodyInstrument().getValue()-1);

            improScore.addPart(improLick);

            Style style = notate.getChordProg().getStyleAtSlot(chordStartSlot);
            improLick.setSwing(style.getSwing());

            //System.out.println("slot " + chordStartSlot + " style " + style);
            setImproCommand(
                    new PlayScoreFastCommand(improScore,
                                             0,         // startTime
                                             true,      // To cause swing value to be used.
                                             style,
                                             notate.getMidiSynth2(),
                                             null,      // play listener
                                             0,         // loopCount,
                                             notate.getTransposition(), // transposition
                                             false,     // use drums
                                             -1));      // end
          }
        else
          {
            ++failCounter;
            //System.out.println(" *** generation failed " + failCounter + " time consecutively.");
          }
      }
  }


/**
 * Create lick at indicated slot, for playback in a subsequent slot.
 * @param slotInPlayback
 * @return
 */

public void maybeCreateLick(int slotInPlayback)
  {
    long biasedCyclesElapsed = (notate.getTotalSlotsElapsed() + generationLeadSlots) / improInterval;
    //System.out.println("biasedCyclesElapsed = " + biasedCyclesElapsed + " nextGenerateCycle = " + nextGenerateCycle);

    // This prevents multiple generations within one cycle.
    // Trying to hinge on generated does not work.
    // Neither does hinging on slotInPlayback < generateAtSlot

    if( biasedCyclesElapsed < nextGenerateCycle )
      {
        return;
      }

    melodyStart = ivFirst ? improInterval * biasedCyclesElapsed
                          : improInterval * biasedCyclesElapsed + halfInterval;

    playAtSlot = Math.max(0, melodyStart - playLeadSlots);

    generateAtSlot = melodyStart - generationLeadSlots;

    MelodyPart melodyPart = notate.getImprovMelodyPart();
    if( melodyPart == null )
      {
        return;
      }

    if( !generated 
     && notate.getTotalSlotsElapsed() >= generateAtSlot 
      )
      {
       // We are generating for the NEXT cycle, due to using the bias in triggering
      
        melodyStartsAtSlot = (int)melodyStart;
               
        int partsize = melodyPart.size();

        int chordStartSlot = melodyStartsAtSlot % partsize; // TEMP
        int chordStopSlot = (melodyStartsAtSlot + halfInterval - 1)% partsize; // TEMP
        
        if( traceAutoImprov )
          {
          System.out.println("\ncreate lick for next cycle at " + notate.bar(slotInPlayback) 
                           + ", melodyStart " + notate.bar(melodyStart)
                           + " to play at " + notate.bar(playAtSlot)
                           + ", chords from " + notate.bar(chordStartSlot) + " to " + notate.bar(chordStopSlot));
          }
        
        //improLick = notate.generate(chordStartSlot, chordStopSlot);

        generated = improLick != null && improLick.size() > 0;

        if( generated )
          {
            licksGenerated++;
            //System.out.println("licks generated = " + licksGenerated + ", numCycles = " + numCycles);
            if( licksGenerated > 1 && (licksGenerated - 1) % numCycles == 0 )
              {
                // Change leadsheet chorus number  for pasting
                notate.incrementMelodyPartIndex();
                melodyPart = notate.getImprovMelodyPart();
                if( melodyPart == null )
                  {
                    return;
                  }
              }
            nextGenerateCycle = biasedCyclesElapsed + 1;

            if( traceAutoImprov )
              {
                System.out.println("\n"  + notate.bar(notate.getTotalSlotsElapsed())
                        + ": result: "   + improLick
                        + ", generate: " + notate.bar(generateAtSlot)
                        + ", play: "     + notate.bar(playAtSlot)
                        + ", paste: "    + notate.bar(melodyStartsAtSlot)
                        + ", hear: "     + notate.bar(melodyStart));

                if( failCounter > 0 )
                  {
                    System.out.println(" generation succeeded after " + failCounter + " failures.");
                    failCounter = 0;
                  }
                else
                  {
                    System.out.println(" generation succeeded first time.");
                  }
              }

            played = false;
            Score improScore = new Score();
            improScore.setTempo(notate.getTempo());

            improLick.setInstrument(notate.getMelodyInstrument().getValue()-1);

            improScore.addPart(improLick);

            Style style = notate.getChordProg().getStyleAtSlot(chordStartSlot);
            improLick.setSwing(style.getSwing());

            //System.out.println("slot " + chordStartSlot + " style " + style);
            setImproCommand(
                    new PlayScoreFastCommand(improScore,
                                             0,         // startTime
                                             true,      // To cause swing value to be used.
                                             style,
                                             notate.getMidiSynth2(),
                                             null,      // play listener
                                             0,         // loopCount,
                                             notate.getTransposition(), // transposition
                                             false,     // use drums
                                             -1));      // end
          }
        else
          {
            ++failCounter;
            //System.out.println(" *** generation failed " + failCounter + " time consecutively.");
          }
      }
  }


public void maybePlayLick(int slotInPlayback)
  {
    MelodyPart melodyPart = notate.getImprovMelodyPart();
    if( melodyPart == null )
      {
        return;
      }
    
    boolean paste = true;
    boolean timeOk = notate.getTotalSlotsElapsed() > playAtSlot; // + (notate.getScore().getCountInOffset())/4;
    boolean commandOk = improCommand != null;
    boolean result = !played && generated && commandOk && timeOk;

    if( result )
      {
        improCommand.execute();

        if( traceAutoImprov )
          {
          System.out.println(notate.bar(notate.getTotalSlotsElapsed())
                           + " >= " + notate.bar(playAtSlot)
                           + " playing");
          }

        played = true;
        generated = false;

        if( paste )
          {
            if( firstTime )
              {
                firstTime = false;
                // firstTime pasteOver is handled outside
              }
            else
              {
                //System.out.println("pasting over " + melodyPart.hashCode());
                int partsize = melodyPart.size();
                int chordStartSlot = melodyStartsAtSlot % partsize;
                int chordStopSlot = (melodyStartsAtSlot + halfInterval - 1)% partsize;
                melodyPart.newPasteOver(improLick, chordStartSlot);
//System.out.println("pasting into " + melodyPart.getMelodyPartId() + ": " + melodyPart + ", lick = " + improLick);
                notate.rectifySelection(notate.getCurrentStave(), chordStartSlot, chordStopSlot);
                // We are not getting refresh on other than the first chorus
                // This is stuff that I've tried.
                //notate.refreshCurrentStaveScrollPane(); not effective
                //notate.repaintAndStaveRequestFocus(); not effective
                //notate.getCurrentStave().repaintDuringPlayback(chordStartSlot);
              }
          }

        setImproCommand(null); // Don't play twice
      }
  }

public int getGenerationLeadSlots()
  {
    return generationLeadSlots;
  }

public void setGenerationLeadSlots(int generationLeadSlots)
  {
    this.generationLeadSlots = generationLeadSlots;
  }

public Command getImproCommand()
  {
    return improCommand;
  }

public void setImproCommand(Command improCommand)
  {
    this.improCommand = improCommand;
  }

public int getImproInterval()
  {
    return improInterval;
  }

public void setImproInterval(int improInterval)
  {
    this.improInterval = improInterval;
    halfInterval = improInterval/2;
  //System.out.println("improInterval = " + improInterval + ", halfInterval = " + halfInterval);
  }

public int getPlayLeadSlots()
  {
    return playLeadSlots;
  }

public void setPlayLeadSlots(int playLeadSlots)
  {
    this.playLeadSlots = playLeadSlots;
  }

public void setSelected(boolean selected)
  {
    this.selected = selected;
  }

public boolean isSelected()
  {
    return selected;
  }

public void setIVfirst(boolean value)
  {
    ivFirst = value;
  }

public boolean improviseAtStart()
  {
    return selected && ivFirst;
  }

 }