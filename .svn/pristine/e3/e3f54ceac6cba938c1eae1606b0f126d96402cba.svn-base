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

/**
 * Executes the resolutions.
 */
public void execute()
  {
    //Trace.log(2, "executing RectifyPitchesCommand");

    Note previousNote = null;
    Note previouslyResolved = null;
    int previousIndex = 0;

    // reserve for possible undo:
    originalPart = part.extractSlots(startIndex, stopIndex);


    int slotsRemaining = stopIndex - startIndex + 1;

    try
      {
        for( int i = startIndex; i < stopIndex; i++ )
          {
            Note currentNote = part.getNote(i);
            Note resolved = currentNote;
            
            if( currentNote != null )
              {
               //System.out.println("part at " + i + " had " + part.getNote(i));

               int value = Math.min(currentNote.getRhythmValue(), slotsRemaining);

                if( currentNote.isRest() )
                  {
                    previouslyResolved = null;
                  }
                else
                  {
                    Chord chord = chordProg.getCurrentChord(i);
                    if( chord.isNOCHORD() )
                      {
                      // leave currentNote as resolved
                      }
                    else
                      {
                        ChordForm form = chord.getChordSymbol().getChordForm();
                        String root = chord.getRoot();

                        // usableTones combines chord, color, and scale tones
                        // This could be done within ChordForm more efficiently

                        Polylist usableTones = Polylist.nil;

                        usableTones = usableTones.append(form.getSpell(root));

                        usableTones = usableTones.append(form.getColor(root));

                        // Not so good: usableTones = usableTones.append(form.getFirstScaleTones(root));

                        if( directional )
                        {
                            // Directional transposition specified
                            resolved = Note.getClosestMatchDirectional(currentNote.getPitch(), usableTones, direction);
                        }
                        else
                        {
                            // Rectification specified
                        Note nextNote = part.getNextNote(i);
                        if( NoteSymbol.makeNoteSymbol(currentNote).enhMember(usableTones) )
                          {
                            // No rectification necessary
                            resolved = currentNote;
                          }
                        else if( nextNote != null && nextNote.adjacentPitch(currentNote) && NoteSymbol.makeNoteSymbol(nextNote).enhMember(usableTones) )
                        {
                            // Allow approach tones to stand
                            //System.out.println("allowing approach: " + currentNote.toLeadsheet() + " to " + nextNote.toLeadsheet() );

                            resolved = currentNote;
                        }
                        else
                          {
                            // Move anything else to a usable tone
                          resolved =
                            Note.getClosestMatchDirectional(currentNote.getPitch(), usableTones, direction);

                          resolved.setRhythmValue(value);
                          }
                        }

                        // If note is a repeat of the previous, try moving it up or down a half step then
                        // resolving it in the direction moved.

                        if( previouslyResolved != null && previouslyResolved.samePitch(resolved) )
                          {
                            // Decide direction randomly

                            boolean dir = Math.random() > 0.5;

                            int offset = dir ? +1 : -1;

                            resolved =
                                Note.getClosestMatchDirectional(resolved.getPitch()+offset, usableTones, dir);

                            //System.out.println(
                            //    "repeated pitch at slot " + i + ": " + resolved.toLeadsheet() + " to " + previouslyResolved.toLeadsheet() + ", dir = " + dir);
                           }
                      }
                    
                     part.setNote(i, resolved);

                     previousNote = currentNote;
                     previouslyResolved = resolved;

                     previousIndex = i;
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
