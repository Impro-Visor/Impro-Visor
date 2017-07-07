/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.generalCluster;

import static imp.Constants.BEAT;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Unit;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class MelodyDataExtractor {
    
    private String abstractMelodyString;
    private Notate notate;
    private LickGen lickgen;
    
    public MelodyDataExtractor(Notate notate){
        this.notate=notate;
    }
    
    public String getAbstractMelodyString(){
       return abstractMelodyString; 
    }
    
    public String 
        addMeasureToAbstractMelody(int selStart,
                                         int measureWindow,
                                         boolean isSongStart,
                                         boolean writeChords)
  {
    //int slotsPerMeasure = score.getMetre()[0] * BEAT; //assume something/4 time
    int slotsPerSection = BEAT * measureWindow;
    //boolean isSongStart = (selStart == 0);
    int selEnd = selStart + slotsPerSection;
    MelodyPart part = notate.getCurrentMelodyPart().copy();

    if( part.melodyIsEmpty(selStart, slotsPerSection) )
      {
        //if this is empty, the last measure is empty,
        //and the rest of the chorus is empty, return null
        if( part.getFreeSlotsFromEnd() >= (part.size() - selEnd)
                && part.melodyIsEmpty(selStart - slotsPerSection, slotsPerSection) )
          {
            return null;
          } //otherwise return a section of rests
        else
          {
            StringBuilder sb = new StringBuilder();
            Note n = new Note(72, 1);
            n.getDurationString(sb, slotsPerSection);
            String returnString = "((slope 0 0 R" + sb.substring(1) + "))";
            if( isSongStart )
              {
                returnString = returnString.concat("STARTER");
              }
            return returnString;
          }
      }

    int current = selStart;

    Polylist rhythmString = new Polylist();

    //pitches of notes in measure not including rests
    ArrayList<Integer> notes = new ArrayList<Integer>();

    boolean tiedAtStart = false, tiedAtEnd = false;

    //untie first note if it is tied from last measure
    if( part.getPrevNote(current) != null
            && part.getPrevNote(current).getRhythmValue() > current - part.getPrevIndex(current) )
      {

        tiedAtStart = true;

        //untie and set the previous note
        Note untiedNote = part.getPrevNote(current).copy();
        int originalRhythmVal = untiedNote.getRhythmValue();
        int rhythmVal = slotsPerSection - part.getPrevIndex(current) % slotsPerSection;
        untiedNote.setRhythmValue(rhythmVal);
        part.setNote(part.getPrevIndex(current), untiedNote);

        //set the current note
        rhythmVal = originalRhythmVal - rhythmVal;
        Note currNote = part.getPrevNote(current).copy();
        currNote.setRhythmValue(rhythmVal);
        part.setNote(current, currNote);
      }

    if( part.getPrevNote(selEnd) != null )
      {
        //untie notes at end of measure and beginning of next measure
        if( part.getPrevNote(selEnd).getRhythmValue() > selEnd - part.getPrevIndex(
                selEnd) )
          {
            tiedAtEnd = true;
            int tracker = part.getPrevIndex(selEnd);
            Note untiedNote = part.getNote(tracker).copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = slotsPerSection - (tracker % slotsPerSection);
            untiedNote.setRhythmValue(rhythmVal);
            part.setNote(tracker, untiedNote);
            int secondRhythmVal = originalRhythmVal - rhythmVal;
            untiedNote = part.getNote(tracker).copy();
            untiedNote.setRhythmValue(secondRhythmVal);
            part.setNote(selEnd, untiedNote);
          }
      }

    if( part.getPrevNote(selStart + 1) != null )
      {
        if( (part.getPrevIndex(selStart + 1) != selStart)
                && !(part.getPrevNote(selStart + 1).isRest()) )
          {
            return null;
          }
      }

    while( current < selEnd )
      {

        //if the current note is a null note, make it a rest
        if( part.getNote(current) == null )
          {
            int next = part.getNextIndex(current);
            Note n = Note.makeRest(next - current);
            part.setNote(current, n);
          }

        StringBuilder sb = new StringBuilder();

        //FIX: Suspicious that value is not used.
        int value = part.getNote(current).getDurationString(sb, part.getNote(
                current).getRhythmValue());

        int pitch = part.getNote(current).getPitch();

        if( part.getNote(current).isRest() )
          {
            rhythmString = rhythmString.cons("R" + sb.substring(1));
          }
        else
          {

            //add pitch to notes
            notes.add(pitch);
            //get note type
            char notetype;
            int[] notetone = lickgen.getNoteTypes(current, pitch, pitch,
                                                  notate.getChordProg());
            switch( notetone[0] )
              {
                case LickGen.CHORD:
                    notetype = 'C';
                    break;
                case LickGen.COLOR:
                    notetype = 'L';
                    break;
                default:
                    notetype = 'X';
                    break;
              }
            if( notetype == 'X' && part.getNextNote(current) != null )
              {

                int nextPitch = part.getNextNote(current).getPitch();
                int nextIndex = part.getNextIndex(current);
                if( nextIndex <= selEnd )
                  {
                    int pitchdiff = nextPitch - pitch;
                    if( Math.abs(pitchdiff) == 1 )
                      {
                        notetype = 'A';
                      }
                  }
              }
            rhythmString = rhythmString.cons(notetype + sb.substring(1));
          }

        current = part.getNextIndex(current);

      }

    rhythmString = rhythmString.reverse();

    //add in goal notes to the rhythmString

    //process intervals
    ArrayList<Integer> intervals = new ArrayList<Integer>();
    intervals.add(0);
    for( int i = 1; i < notes.size(); i++ )
      {
        intervals.add(notes.get(i) - notes.get(i - 1));
      }

    //process slopes
    ArrayList<int[]> slopes = new ArrayList<int[]>();
    int[] slope = new int[3];
    int tracker = 0;

    //get the slope from the note before this section to the first note in the measure
    int prevIndex = part.getPrevIndex(selStart);
    Note lastNote = part.getNote(prevIndex);
    while( lastNote != null && lastNote.isRest() )
      {
        prevIndex = part.getPrevIndex(prevIndex);
        lastNote = part.getNote(prevIndex);
      }
    int lastpitch = 0;
    if( lastNote != null && !lastNote.isRest() )
      {
        lastpitch = lastNote.getPitch();
      }
    int pitch = notes.get(0);
    int pitchChange;
    if( lastpitch == 0 )
      {
        pitchChange = 0;
      }
    else
      {
        pitchChange = pitch - lastpitch;
      }
    int minPitchChange = 0, maxPitchChange = 0;
    //avoid random notes and repeated notes
    if( pitchChange != 0 )
      {
        if( pitchChange == 1 )
          {
            minPitchChange = 1;
            maxPitchChange = 2;
          }
        else if( pitchChange == -1 )
          {
            minPitchChange = -2;
            maxPitchChange = -1;
          }
        else
          {
            minPitchChange = pitchChange - 1;
            maxPitchChange = pitchChange + 1;
          }
      }

    //if there is only 1 note, return it with its slope
    if( intervals.size() <= 1 )
      {

        String rhythm = rhythmString.toString();
        rhythm = rhythm.substring(1, rhythm.length() - 1);

        //handle case of only 1 note
        if( rhythm.equals("") )
          {
            char thisPitch = lickgen.getNoteType(selStart, notes.get(0), notes.get(
                    0), notate.getChordProg());
            String len = Note.getDurationString(slotsPerSection);
            rhythm = thisPitch + len;
          }
        String returnString = "((slope "
                + minPitchChange + " "
                + maxPitchChange + " "
                + rhythm + "))";
        if( isSongStart )
          {
            returnString = returnString.concat("STARTER");
          }
        if( tiedAtEnd )
          {
            returnString = returnString.concat(" ENDTIED");
          }
        if( tiedAtStart )
          {
            returnString = returnString.concat(" STARTTIED");
          }
        return returnString;
      }

    for( int i = 0; i < intervals.size(); i++ )
      {
        tracker = i;
        if( intervals.get(i) != 0 )
          {
            i = intervals.size();
          }
      }

    //direction is -1 if slope is going down, 0 for repeated note, 1 for up
    int direction = 0;
    if( intervals.get(tracker) > 0 )
      {
        direction = 1;
      }
    else if( intervals.get(tracker) < 0 )
      {
        direction = -1;
      }
    //initialize stuff - first note is in its own slope
    slope[0] = minPitchChange;
    slope[1] = maxPitchChange;
    slope[2] = 1;
    slopes.add(slope.clone());

    slope[0] = intervals.get(1);
    slope[1] = intervals.get(1);
    slope[2] = 0;
    for( int i = 1; i < intervals.size(); i++ )
      {
        //slope was going up but not any more
        if( direction == 1 && intervals.get(i) <= 0 )
          {
            if( intervals.get(i) == 0 )
              {
                direction = 0;
              }
            else
              {
                direction = -1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }

            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
            //slope was going down but not any more
          }
        else if( direction == -1 && intervals.get(i) >= 0 )
          {
            if( intervals.get(i) == 0 )
              {
                direction = 0;
              }
            else
              {
                direction = 1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
            //slope was 0 but not any more
          }
        else if( direction == 0 && intervals.get(i) != 0 )
          {
            if( intervals.get(i) > 0 )
              {
                direction = 1;
              }
            else
              {
                direction = -1;
              }
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
            slope[0] = intervals.get(i);
            slope[1] = intervals.get(i);
            slope[2] = 1;
          }
        else
          {
            slope[2]++;
            if( intervals.get(i) > slope[1] )
              {
                slope[1] = intervals.get(i);
              }
            if( intervals.get(i) < slope[0] )
              {
                slope[0] = intervals.get(i);
              }
          }

        if( i == intervals.size() - 1 )
          {
            if( slope[2] != 0 )
              {
                slopes.add(slope.clone());
              }
          }
      }

    //add in slopes
    StringBuilder strbuf = new StringBuilder();
    strbuf.append("(");
    Polylist tempString = rhythmString;
    for( int i = 0; i < slopes.size(); i++ )
      {
        slope = slopes.get(i);
        strbuf.append("(slope ");
        strbuf.append(slope[0]);
        strbuf.append(" ");
        strbuf.append(slope[1]);
        strbuf.append(" ");

        int j = 0;
        //get all of notes if last slope
        if( i == slopes.size() - 1 )
          {
            while( tempString.nonEmpty() )
              {
                strbuf.append(tempString.first().toString());
                strbuf.append(" ");
                tempString = tempString.rest();
              }
          }
        else
          {
            while( j < slope[2] )
              {
                String temp = tempString.first().toString();
                strbuf.append(temp);
                strbuf.append(" ");
                tempString = tempString.rest();
                if( temp.charAt(0) != 'R' )
                  {
                    j++;
                  }
              }
          }
        strbuf.deleteCharAt(strbuf.length() - 1);
        strbuf.append(")");
      }
    strbuf.append(")");
      //if we are writing to file, write the chords, start data, and tie data
      {

        //Mark measure as 'songStarter' if it is the first of a song
        if( isSongStart )
          {
            strbuf.append("STARTER");
          }
        if( writeChords )
          {
            strbuf.append("CHORDS ");

            ChordPart chords = notate.getChordProg()
                    .extract(selStart,
                             selStart + slotsPerSection - 1);
            ArrayList<Unit> chordList = chords.getUnitList();
            if( chordList.isEmpty() )
              {
                System.out.println("No chords");
              }
            for (Unit chordList1 : chordList) {
                String nextChord = ((Chord) chordList1).toLeadsheet();
                strbuf.append(nextChord);
                strbuf.append(" ");
            }
          }
      }

    return strbuf.toString();
    }                                                       

}
