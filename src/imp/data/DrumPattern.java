/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.data;

import imp.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import polya.Polylist;

/**
 * Contains rhythmic patterns for several drums, which are used to construct a
 * drumline according to methods contained in this class.
 *
 * @see Style
 * @author Stephen Jones; Robert Keller cleaned up Polylist code to not use nth
 */

public class DrumPattern extends Pattern implements Constants, Serializable
{

public static int defaultDrumPatternDuration = 480;

/**
 * a Polylist of drum Polylists, the structure of each drum Polylist is: (DRUM
 * RULES DURATIONS)
 */

private ArrayList<DrumRuleRep> drums;

/**
 * defined rules for drums
 */
private LinkedHashMap<String, Polylist> definedRules = 
        new LinkedHashMap<String, Polylist>();

/**
 * Symbols used in drum patterns
 */

public static final char DRUM_STRIKE = 'X';
public static final char DRUM_REST   = 'R';
public static final char DRUM_VOLUME = 'V';


/**
 * array containing the types of rules as Strings
 */

public static final String ruleTypes[] =
  {
    "X", "R", "V"
  };


// indices into the ruleTypes array
public static final int STRIKE = 0;
public static final int REST = 1;
public static final int VOLUME = 2;


/**
 * array containing DrumPattern keywords
 */

private static String keyword[] =
  {
    "drum", "weight", "volume", "pattern-name"
  };


// indices into the keyword array
private static final int DRUM = 0;
private static final int WEIGHT = 1;
private static final int NAME = 3;

private String patternName = "";


/**
 * Creates a new DrumPattern (only used by the factory).
 */
public DrumPattern()
  {
    drums = new ArrayList<DrumRuleRep>();
  }

/**
 * A factory for creating a DrumPattern from a Polylist.
 *
 * @param L a Polylist containing DrumPattern information
 * @return the DrumPattern created from the Polylist, or null if there was a
 * problem
 */
public static DrumPattern makeDrumPattern(Polylist L)
  {
    DrumPattern dp = new DrumPattern();
    while( L.nonEmpty() )
      {
        Polylist item = (Polylist) L.first();
        L = L.rest();

        String dispatcher = (String) item.first();
        item = item.rest();
        
        switch( Leadsheet.lookup(dispatcher, keyword) )
          {
            case NAME:
            {
                if( item == null || item.isEmpty() || item.first().equals("") )
                {
                    break;
                }
                else if( item.first() instanceof String )
                {
                    dp.patternName = (String) item.first();
                }
                else 
                {
                    dp.setError("Unrecognized name type in drum pattern: " + item.first());
                    return dp;
                }
                break;
            }
            case DRUM: // a single drum "rule" in the pattern
              {
                DrumRuleRep rep = new DrumRuleRep(item);
                rep.setDefinedRules(dp.getDefinedRules());
                dp.addRule(rep);
                break;
              }
                
            case WEIGHT: // weight of entire pattern
              {
                Number w = (Number) item.first();
                dp.setWeight(w.floatValue());
                break;
              }
          }
      }
    return dp;
  }


/**
 * A method that adds rules and durations to an existing bass pattern
 * Used in place of makeDrumPattern when the Style has pre-defined rules
 * @param L
 * @return 
 */
public DrumPattern makePattern(Polylist L)
  {
    DrumPattern dp = this;
    while( L.nonEmpty() )
      {
        Polylist item = (Polylist) L.first();
        L = L.rest();

        String dispatcher = (String) item.first();
        item = item.rest();
        
        switch( Leadsheet.lookup(dispatcher, keyword) )
          {
            case NAME:
            {
                if( item == null || item.isEmpty() || item.first().equals("") )
                {
                    break;
                }
                else if( item.first() instanceof String )
                {
                    dp.patternName = (String) item.first();
                }
                else 
                {
                    dp.setError("Unrecognized name type in drum pattern: " + item.first());
                    return dp;
                }
                break;
            }
            case DRUM: // a single drum "rule" in the pattern
              {
                DrumRuleRep rep = new DrumRuleRep();
                rep.setDefinedRules(dp.getDefinedRules());
                rep.makeDrumRuleRep(item);
                dp.addRule(rep);
                break;
              }
                
            case WEIGHT: // weight of entire pattern
              {
                Number w = (Number) item.first();
                dp.setWeight(w.floatValue());
                break;
              }
          }
      }
    return dp;
  }


public static boolean isValidDrumPatternChar(char x)
  {
   switch( x )
     {
       case DRUM_STRIKE: 
       case DRUM_REST: 
       case DRUM_VOLUME: 
           return true;
           
       default:
           return false;
     }
  }


/**
 * Adds rules and durations for a drum to this DrumPattern. Note that there
 * should only be one rule for a given instrument in a pattern. Hence we will
 * check this and replace any previous rule with the new one
 *
 * @param drum a Long specifying the MIDI number for the drum
 * @param rules a Polylist of the rules for this drum
 * @param durations a Polylist of durations for this drum
 * @param volume a Long specifying the volume for the drum
 * @param ruleAsList the rule in Polylist form, for use in StyleEditorTableModel
 */

private void addRule(DrumRuleRep rep)
  {
    for( DrumRuleRep existing : drums )
      {
        if( existing.getInstrument() == rep.getInstrument() )
          {
            drums.remove(existing);
            break;
          }
      }
    drums.add(rep);
  }


public ArrayList<DrumRuleRep> getDrums()
  {
    return drums;
  }


@Override
public int getDuration()
  {
    if( drums.isEmpty() )
      {
        return defaultDrumPatternDuration;
      }
    // here we're basing the duration of this DrumPattern on the 
    // the max duration across all drums

    int maxDuration = 0;

    for( DrumRuleRep rep: drums )
      {
        // drum.first() is the instrument number, not used here

        int duration = rep.getDuration();

        if( duration > maxDuration )
          {
            maxDuration = duration;
          }
      }
    
    return maxDuration;
  }
  

/**
 * Renders this drum pattern as a Polylist of MelodyPart objects to be
 * sequenced.
 *
 * @return a Polylist of MelodyPart objects
 */
    
public DrumLine applyRules()
  {
    DrumLine drumline = new DrumLine();

    for( DrumRuleRep rep: drums )
      {
        MelodyPart melodyPart = new MelodyPart();
        int drumInstrument = rep.getInstrument();
        melodyPart.setInstrument(drumInstrument);
        
        int localVolume = 127;

        for( DrumRuleRep.Element element: rep.getElements() )
          {
            switch( element.getType() )
              {
                case DRUM_STRIKE:
                  {
                   int dur = Duration.getDuration(element.getSuffix());
                   Note note = new Note(drumInstrument, dur);
                   note.setVolume(localVolume);
                   melodyPart.addNote(note);
                   
                   break;
                  }
                    
                case DRUM_REST:
                  {
                   int dur = Duration.getDuration(element.getSuffix());
                   melodyPart.addNote(new Rest(dur));
                   break;
                  }
                    
                case DRUM_VOLUME:
                  {
                   localVolume = Integer.parseInt(element.getSuffix());
                   break;
                  }
              }
          }
        
        drumline.add(melodyPart);
      }

    return drumline;
  }

public String getName()
    {
    return patternName;
    }

public LinkedHashMap getDefinedRules()
{
    return definedRules;
}

public void setDefinedRules(LinkedHashMap map)
{
    if( map.isEmpty() )
    {
        return;
    }
    else
    {
        definedRules = map;
    }
}

}
