/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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
package imp.com;

import imp.*;
import imp.data.*;
import polya.*;

/**
 * An undoable Command that can match a contiguous set of pitches to the
 * chord and scale tones played over the melody part.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Steven Gomez
 */
public class RectifyPitchesCommand
        implements Command, Constants
  {
  /**
   * the first slot to resolve
   */
  int startIndex;

  /**
   * the last slot to resolve
   */
  int stopIndex;

  /**
   * the part in which to resolve pitches
   */
  MelodyPart part;


  /**
   * the part before changes are made (for undo)
   */
  Part originalPart;

 /**
   * the new part, in case redo is called for
   */
  Part saveForRedo;

  /**
   * the chord progression to be played with the melody part
   */
  ChordPart chordProg;

  /**
   * false since this Command can not be undone
   */
  boolean undoable = true;

  boolean directional = false;

  boolean direction = true;
  
  boolean chordTones = true;
  
  boolean colorTones = true;
  
  boolean approachTones = true;

  /**
   * Creates a new Command that can resolve pitches of a set of Notes.
   */
  public RectifyPitchesCommand(MelodyPart part, int startIndex,
                                int stopIndex, ChordPart chordProg,
                                boolean directional, boolean direction)
    {
    this.startIndex = startIndex;
    this.part = part;
    this.stopIndex = stopIndex;
    this.chordProg = chordProg;
    this.directional = directional; // whether specific direction is preferred
    this.direction = direction;     // preferred direction  (true = up)
    }
  
  public RectifyPitchesCommand(MelodyPart part, int startIndex,
                                int stopIndex, ChordPart chordProg,
                                boolean directional, boolean direction,
                                boolean chord, boolean color, boolean approach){
      this(part, startIndex, stopIndex, chordProg, directional, direction);
      this.chordTones = chord;
      this.colorTones = color;
      this.approachTones = approach;
  }

/**
 * Executes the resolutions.
 */
public void execute()
  {
      //System.out.println("*******Rectifying Pitches*******");
      if(!(chordTones||colorTones)){
          //Invalid input. Include all.
          chordTones = true;
          colorTones = true;
          approachTones = true;
      }
      
      //int lastActiveSlot = part.getLastActiveSlot();
    //Trace.log(2, "executing RectifyPitchesCommand");

    //Was used to prevent two of the same note in a row
    //Note previousNote = null;
    //Note previouslyResolved = null;
    //int previousIndex = 0;

    // reserve for possible undo:
    originalPart = part.extractSlots(startIndex, stopIndex);


    int slotsRemaining = stopIndex - startIndex + 1;

    try
      {
        //for( int i = startIndex; i < stopIndex; i++ )
          for(int i = startIndex; i < stopIndex; i = part.getNextIndex(i))
          {
            Note currentNote = part.getNote(i);
            Note resolved = currentNote;
            
            if( currentNote != null )
              {
               //System.out.println("part at " + i + " had " + part.getNote(i));

               //int value = Math.min(currentNote.getRhythmValue(), slotsRemaining);
               int value = currentNote.getRhythmValue();

                if( currentNote.isRest() )
                  {
                      //was used to disallow repeated notes
                    //previouslyResolved = null;
                  }
                else
                  {
                    //System.out.println("Not a rest");
                    Chord chord = chordProg.getCurrentChord(i);
                    
                    //nextChord - for use in determing appraoch tones
                    Chord nextChord = null;
                    int nextIndex = part.getNextIndex(i);
                    if(nextIndex <= part.getLastActiveSlot()){
                        nextChord = chordProg.getCurrentChord(nextIndex);
                    }
                    
                    if( chord.isNOCHORD() )
                      {
                      // leave currentNote as resolved
                      }
                    else
                      {
                        //System.out.println("Not isNOCHORD");
                        ChordForm form = chord.getChordSymbol().getChordForm();
                        String root = chord.getRoot();
                        //System.out.println("Got form and root");
                        ChordForm nextForm = null;
                        String nextRoot = null;
                        if(nextChord != null){
                            nextForm = nextChord.getChordSymbol().getChordForm();
                            nextRoot = nextChord.getRoot();
                        }
                        //System.out.println("Got nextForm and nextRoot");
                        
                        // usableTones combines chord, color, and scale tones
                        // This could be done within ChordForm more efficiently

                        Polylist usableTones = Polylist.nil;
                        Polylist nextUsableTones = Polylist.nil;
                        //System.out.println("Checkpoint 1");
                        if(chordTones){
                            //System.out.println("Checkpoint 1.25");
                            usableTones = usableTones.append(form.getSpell(root));
                            if(nextForm!=null){
                                nextUsableTones = nextUsableTones.append(nextForm.getSpell(nextRoot));
                            }
                            
                        }
                        //System.out.println("Checkpoint 2");

                        //option to only include chord tones
                        if(colorTones){
                           usableTones = usableTones.append(form.getColor(root)); 
                           if(nextForm!=null){
                             nextUsableTones = nextUsableTones.append(nextForm.getColor(nextRoot));  
                           }
                           
                        }
                        //System.out.println("Checkpoint 3");

                        // Not so good: usableTones = usableTones.append(form.getFirstScaleTones(root));

                        if( directional )
                        {
                            //System.out.println("Directional Mode");
                            // Directional transposition specified
                            resolved = Note.getClosestMatchDirectional(currentNote.getPitch(), usableTones, direction);
                        }
                        else
                        {
                            //System.out.println("Rectification Mode");
                            // Rectification specified
                            Note nextNote = part.getNextNote(i);
                            if(NoteSymbol.makeNoteSymbol(currentNote).enhMember(usableTones) )
                              {
                                // No rectification necessary
                                resolved = currentNote;
                                //System.out.println("No rectification necessary");
                                //System.out.println(resolved);
                                //TEST
//                                if(!NoteSymbol.makeNoteSymbol(resolved).enhMember(usableTones) )
//                              {
//                                  System.out.println("No rectification necessary - not enhMember");
//                                System.out.println(resolved);
//                              }
                              }
                            //PROBLEM: They are using the chord of the current note to see if the next note is a chord/color tone - BAD
                            else if( approachTones && nextNote != null && nextNote.adjacentPitch(currentNote) && NoteSymbol.makeNoteSymbol(nextNote).enhMember(nextUsableTones) )
                            {
                                // Allow approach tones to stand
                                //System.out.println("allowing approach: " + currentNote.toLeadsheet() + " to " + nextNote.toLeadsheet() );
                                
                                resolved = currentNote;
                                //TEST
                                //System.out.println("Allow approach tones to stand");
                                //System.out.println(resolved);
//                                if(!NoteSymbol.makeNoteSymbol(resolved).enhMember(usableTones) )
//                              {
//                                  System.out.println("Allow approach tones to stand - not enhMember");
//                                System.out.println(resolved);
//                              }
                                
                                //System.out.println("Allow approach tone");
                                //System.out.println(currentNote);
                            }
                            else
                              {
                                // Move anything else to a usable tone
                                  //This should NOT use getClosestMatchDirectional because a direction was not specified.
                                  resolved = Note.getClosestMatch(currentNote.getPitch(), usableTones);
                                  
                              //resolved = Note.getClosestMatchDirectional(currentNote.getPitch(), usableTones, direction);

                                resolved.setRhythmValue(value);
                                //System.out.println("Move anything else to a usable tone");
                                //System.out.println(resolved);
                                //TEST
//                                if(!NoteSymbol.makeNoteSymbol(resolved).enhMember(usableTones) )
//                              {
//                                  System.out.println("Move anything else to a usable tone - not enhMember");
//                                System.out.println(resolved);
//                              }                              
                                //System.out.println("Move to usable tone");
                                //System.out.println(resolved);
                              }
                        }

                        // If note is a repeat of the previous, try moving it up or down a half step then
                        // resolving it in the direction moved.

                        /*if( previouslyResolved != null && previouslyResolved.samePitch(resolved) )
                          {
                            // Decide direction randomly

                            boolean dir = Math.random() > 0.5;

                            int offset = dir ? +1 : -1;

                            resolved =
                                Note.getClosestMatchDirectional(resolved.getPitch()+offset, usableTones, dir);

                            //System.out.println(
                            //    "repeated pitch at slot " + i + ": " + resolved.toLeadsheet() + " to " + previouslyResolved.toLeadsheet() + ", dir = " + dir);
                           }*/
                      }
                    
                     part.setNote(i, resolved);
                     
                     
                     //was used to prevent having two of the same note in a row
                     //previousNote = currentNote;
                     //previouslyResolved = resolved;

                     //previousIndex = i;
                  }

               slotsRemaining -= value;
               }
           }
      }
    catch( Exception ex )
      {
        //ErrorLog.log(ErrorLog.WARNING, "*** Warning: pitch resolution failed.");
      }
  //playIt();
//    int duration = part.getNote(0).getRhythmValue();
//    for(int i = 0; i <= part.getLastActiveSlot(); i += duration){
//        Note n = part.getNote(i);
//        Note next = part.getNextNote(i);
//        Chord c = chordProg.getCurrentChord(i);
//        Polylist usableTones = c.getSpell().append(c.getColor());
//        if(!n.isRest() && NoteSymbol.makeNoteSymbol(n).enhMember(usableTones)){
//            if(n.adjacentPitch(next)){
//                System.out.println("Approach:");
//            }else{
//                System.out.println("Red:");
//            }
//            System.out.println(n);
//        }
//    }
  }
  
  private void playIt()
  {
    if( ImproVisor.getPlay() )
      {
      ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS);

      //new PlayPartCommand(part.extract(startIndex, stopIndex)).execute();
      }
    
  }

  /**
   * Undoes the shifts.
   */
  public void undo()
    {
    saveForRedo = part.extract(startIndex, stopIndex);
    part.pasteOver(originalPart, startIndex);
    playIt();
    }

  /**
   * Redoes the shifts.
   */
  public void redo()
    {
    part.pasteOver(saveForRedo, startIndex);
    playIt();
    }

  public boolean isUndoable()
    {
    return undoable;
    }

  }
