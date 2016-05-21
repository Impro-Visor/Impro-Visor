/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
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

package imp.data.stylePatterns;

import imp.data.stylePatterns.PatternDisplay;
import imp.midi.MIDIBeast;
import imp.data.stylePatterns.DrumRuleRep;
import imp.com.CommandManager;
import imp.data.*;
import imp.gui.Notate;
import imp.gui.StyleEditor;
import imp.util.ErrorNonModal;
import java.util.LinkedHashMap;
import java.awt.Color;
import polya.Polylist;

/**
 * Created Summer 2007 @authors Brandy McMenamy, Sayuri Soejima
 */

public class DrumRuleDisplay extends PatternDisplay implements Playable, Displayable
{

public static Color playableColor = Color.yellow;
public static Color unplayableColor = Color.red;

//Useful notate containers.
private String instrumentName;
private int instrumentNumber = -1;
private String ruleText = "";
private String displayText = "";

DrumRuleRep ruleRep;

LinkedHashMap<String, Polylist> definedRules;

/**
 * Constructs a new DrumRuleDisplay JPanel with default empty rule and
 * instrument "Acoustic Bass Drum".
 *
 */
public DrumRuleDisplay(Notate parent, CommandManager cm, StyleEditor styleParent)
  {
    super(parent, cm, styleParent);
    initialize("", "Acoustic_Bass_Drum");
  }

/**
 * Constructs a new DrumRuleDisplay JPanel with rule and instrument parameters
     *
 */
public DrumRuleDisplay(String rule, 
                       String instrument, 
                       Notate parent, 
                       CommandManager cm, 
                       StyleEditor styleParent)
  {
    super(parent, cm, styleParent);

    //System.out.println("new DrumRuleDisplay " + rule + " " + instrument);
    initialize(rule, instrument);
  }

public DrumRuleDisplay(String rule,
                       String name,
                       String instrument,
                       Notate parent,
                       CommandManager cm,
                       StyleEditor styleParent)
{
    super(parent, cm, styleParent);
    initialize(rule, name, instrument);
}

/**
 * Initializes all elements and components for the DrumRuleDisplay GUI
 *
 */
private void initialize(String rule, String instrument)
  {    
//System.out.println("making DrumRuleRep for " + instrument + " from " + rule);

    setInstrument(instrument);
    
    setRuleText(rule);
  }

private void initialize(String rule, String name, String instrument)
  {    
//System.out.println("making DrumRuleRep for " + instrument + " from " + rule);

    setInstrument(instrument);
    setName(name);
    setRuleText(rule, name);
  }

//Accessors:
/**
 * @return the actual text displpayed in the text field
     *
 */
public String getDisplayText()
  {
    return displayText.trim();
  }

@Override
public String getPatternText()
{
    return ruleText.trim();
}

/**
 * @return the text formatted with drum-rule syntax to be included with the
 * overall drum-pattern
 */

public String getSaveRule()
  {
    instrumentName = MIDIBeast.spacelessDrumNameFromNumber(instrumentNumber);
    StringBuilder buffer = new StringBuilder();
    buffer.append("(drum ");
    buffer.append(instrumentName);
    if( getDefinedRules().containsKey(getName()))
    {
        buffer.append(" (use ");
        buffer.append(getName());
        buffer.append(")");
    }
    else
    {
        buffer.append(" (name ");
        buffer.append(getName());
        buffer.append(")(rules ");
        buffer.append(getPatternText());
        buffer.append(")");
    }
    buffer.append(")");
    String rule = buffer.toString();
    //String rule = "(drum " + instrumentName + " " + getPatternText() + ")";

    //System.out.println("rule = " + rule);

    return rule;
  }

/**
 * used to create a temp style to play
 * @return 
 */
public String getRule()
{
    StringBuilder buffer = new StringBuilder();
    buffer.append("(drum ");
    buffer.append(instrumentName);
    buffer.append("(name ");
    buffer.append(getName());
    buffer.append(")(rules ");
    buffer.append(getPatternText());
    buffer.append("))");
    return buffer.toString();
}

// "(drum " + getInstrument() + " (rules " + ruleText + "))";

public void setRuleText(String text)
  {
    //System.out.println("setting rule text to " + text);
    ruleText = text.trim();
    displayText = text.trim();
    ruleRep = new DrumRuleRep(instrumentName + " " + ruleText);

    if( !ruleRep.getStatus() )
      {
        ErrorNonModal.log("Error in drum pattern text: " + ruleRep.getErrorMessage());
        cannotPlay(ruleRep.getErrorMessage());
      }
  }

public void setRuleText(String text, String name)
{
    ruleText = text.trim();
    ruleRep = new DrumRuleRep(instrumentName + " " + ruleText);
    if( name.isEmpty() || name == null || name.equals("") )
    {
        displayText = text.trim();
    }
    else
    {
        displayText = name.trim();
    }
    
    if( !ruleRep.getStatus() )
    {
        ErrorNonModal.log("Error in drum pattern text: " + ruleRep.getErrorMessage());
        cannotPlay(ruleRep.getErrorMessage());
    }  
}

/**
 * @return the text formatted as if it were the only rule in a drum-pattern.
 * Used to play the rule. Note that the weight is arbitrary.
 */

public String getFullPattern()
  {
    StringBuilder buffer = new StringBuilder(); 
    buffer.append("(drum-pattern (pattern-name ");
    buffer.append(patternName);
    buffer.append(")");
    buffer.append(getRule());
    buffer.append("(weight 10))");
    String pattern = buffer.toString();
    
    return pattern;
  }


/**
 * @return the instrument selected in the combo box
 */
public String getInstrument()
  {
    return instrumentName;
  }

/**
 * @return the number of the instrument*
 */
public int getInstrumentNumber()
  {
    return instrumentNumber;
  }

//Mutators:
/**
 * Sets the displayed text to parameter rule and updates its legality feedback
 */
public void setDisplayText(String rule)
  {
    setRuleText(rule);
    checkStatus();
  }

public void setDisplayText(String rule, String name)
{
    setRuleText(rule, name);
    checkStatus();
}

/**
 * Sets the selected instrument to parameter instrument if it exists in the
 */
public void setInstrument(String instrument)
  {
    instrumentName = instrument;
    instrumentNumber = MIDIBeast.numberFromSpacelessDrumName(instrument);

    //System.out.println("getting instrument number for " + instrument + " = " + instrumentNumber);

    if( instrumentNumber < 0 )
      {
        String message = "Instrument has no corresponding number: " + instrument;
        ErrorNonModal.log(message);
        cannotPlay(message);
      }
  }

public boolean playMe(double swingVal, int loopCount, double tempo, Score score)
  {
    try
      {
        if( checkStatus() )
          {
            String r = this.getFullPattern();
            //System.out.println("fullPattern = " + r);
            Polylist rule = Notate.parseListFromString(r);
            if( rule.isEmpty() )
              {
                ErrorNonModal.log("Incorrect drum pattern " + ruleRep.getErrorMessage());
                return false;
              }
            Style tempStyle = Style.makeStyle(rule);
            
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("drumRule");
            Style.setStyle("drumRule", tempStyle);
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getDP().get(0).getDuration();
            ChordPart c = new ChordPart();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            score.setChordProg(c);
            if( muteChord )
              {
                notate.setChordVolume(0);
              }
            else
              {
                notate.setChordVolume(styleEditor.getVolume());
              }
            notate.setBassVolume(styleEditor.getVolume());
            score.setTempo(tempo);
            playScore(notate, score, styleEditor);
          }
        else
          {
            cannotPlay();
            ErrorNonModal.log("Incorrect drum pattern " + ruleRep.getErrorMessage());           
            return false;
          }

      }
    catch( Exception e )
      {
        ErrorNonModal.log("Incorrect drum pattern " + ruleRep.getErrorMessage());

        return false;
      }
    return isPlayable();
  }

public Color getPlayableColor()
  {
    return playableColor;
  }

public Color getUnplayableColor()
  {
    return unplayableColor;
  }

/**
 * @return the actual text displpayed in the text field
     *
 */
@Override
public String toString()
  {
    return ruleText;
  }

/**
 * Checks the rule for correctness (uses the time signature in MIDIBeast, which
 * is appropriately updated from a DrumPatternDisplay notate). Changes icons,
 * tooltips, and errorMsgRule to appropriate error feedback information.
 *
 * @return true if the rule is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
 */

public boolean checkStatus()
  {
    return ruleRep.getStatus();
  }


public double getPatternLength()
  {
    return ruleRep.getDuration();
  }

public double getBeats()
  {
    return getPatternLength() / BEAT;
  }

public LinkedHashMap getDefinedRules()
{
    return definedRules;
}

public void setDefinedRules(LinkedHashMap map)
{
    definedRules = map;
}

}
