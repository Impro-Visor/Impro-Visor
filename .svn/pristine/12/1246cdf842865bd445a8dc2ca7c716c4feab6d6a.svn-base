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

package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import java.util.LinkedHashMap;
import java.awt.Color;
import polya.Polylist;

/**
 * Created Summer 2007 @authors Brandy McMenamy; 
 * Robert Keller removed the unused GUI component
 */
public class BassPatternDisplay extends PatternDisplay
        implements Constants, Playable, Displayable
{

public static Color playableColor = Color.orange;
public static Color unplayableColor = Color.red;

//The number added to the title of this object to help the user distinguish it from others.
private int titleNumber = 0;

String bassPatternText = "";
String bassDisplayText = "";
BassPattern bassPattern;

LinkedHashMap<String, Polylist> definedRules;

/**
 * Constructs a new BassPatternDisplay JPanel with default weight 10 and an empty
 * pattern.
     *
 */
public BassPatternDisplay(Notate notate, CommandManager cm, StyleEditor styleEditor)
  {
    super(notate, cm, styleEditor);
    initialize("", 10);
  }

public BassPatternDisplay(String rule, float weight, String name, Notate notate, CommandManager cm, StyleEditor styleEditor)
{
    super(notate, cm, styleEditor);
    initialize(rule, weight, name);
}

/**
 * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
 */
public BassPatternDisplay(String rule, float weight, Notate notate, CommandManager cm, StyleEditor styleEditor)
  {
    super(notate, cm, styleEditor);
    initialize(rule, weight);
  }

/**
 * Initializes all elements and components for the BassPatternDisplay GUI and
 * collapses the pane.
 */
private void initialize(String rule, float weight)
  {
    setWeight(weight);
    setDisplayText(rule);
  }

private void initialize(String rule, float weight, String name)
{
    setWeight(weight);
    setDisplayText(rule, name);
    //System.out.println("The bass pattern's display text is: " + bassDisplayText);
    setName(name);
}

@Override
public boolean playMe(double swingVal)
  {
    return playMe(swingVal, 0);
  }

/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */

public boolean playMe(double swingVal, int loopCount, double tempo, Score s)
  {
    if( checkStatus() )
      {
        try
          {
            String r = this.getPattern();
            Polylist rule = Notate.parseListFromString(r);
        
            Style tempStyle = Style.makeStyle(rule);
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("bassPattern");
            Style.setStyle("bassPattern", tempStyle);
            
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            ChordPart c = new ChordPart();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getBP().get(0).getDuration();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            s.setChordProg(c);

            if( muteChord )
              {
                notate.setChordVolume(0);
              }
            else
              {
                notate.setChordVolume(styleEditor.getVolume());
              }
            notate.setBassVolume(styleEditor.getVolume());
            s.setTempo(tempo);

            playScore(notate, s, styleEditor);
          }
        catch( Exception e )
          {
            cannotPlay("Exception " + e);
            return false;
          }
      }
    else
      {
        cannotPlay(bassPattern.getErrorMessage());
        return false;
      }
    return true;
  }

//Accessors:
/**
 * @return the actual text displpayed in the text field
 *
 */
@Override
public String getPatternText()
  {
    return bassPatternText.trim();
  }

public String getDisplayText()
{
    return bassDisplayText.trim();
}

/**
 * @return the text and weight formatted with bass-pattern syntax used by the
 * style classes 
     *
 */
public String getSavePattern()
  {
      StringBuilder buffer = new StringBuilder();
      buffer.append("(bass-pattern ");
      if( getDefinedRules().containsKey(getName()) )
      {
          buffer.append("(use ");
          buffer.append(getName());
          buffer.append(")");
      }
      else
      {
          buffer.append("(name ");
          buffer.append(getName());
          buffer.append(")(rules ");
          buffer.append(getPatternText());
          buffer.append(")");
      }
      buffer.append("(weight ");
      buffer.append(getWeight());
      buffer.append("))");
    return buffer.toString();
  }


/**
 * Used to create a temporary style
 * @return 
 */
public String getPattern()
{
    StringBuilder buffer = new StringBuilder();
    buffer.append("(bass-pattern (name ");
    buffer.append(getName());
    buffer.append(")(rules ");
    buffer.append(getPatternText());
    buffer.append(")(weight ");
    buffer.append(getWeight());
    buffer.append("))");
    return buffer.toString();
}

public BassPattern getBassPattern()
    {
    return bassPattern;
    }


public int getPatternLength()
  {
    return getBassPattern().getDuration();
  }

public double getBeats()
  {
    return ((double)getPatternLength()) / BEAT;
  }


/**
 * @return the selected value of the checkbox marked "include" in the upper
 * right corner
     *
 */
public boolean getIncludedStatus()
  {
    return true;  // FIX
  }

//Mutators:
/**
 * Sets the number in the title to num.
     *
 */
public void setTitleNumber(int num)
  {
    titleNumber = num;
  }

/**
 * Sets the text in the text field to the parameter text and updates the user
 * feedback information.
     *
 */
public void setDisplayText(String text)
  {
    bassPatternText = text.trim();
    bassDisplayText = text.trim();
    if( bassPatternText.equals("") )
      {
        return;
      }
    Polylist list = Polylist.PolylistFromString('(' + bassPatternText + ')');
    bassPattern = BassPattern.makeBassPattern(Polylist.list(((Polylist)list.first()).cons("rules")));
    if( !bassPattern.getStatus() )
      {
        cannotPlay(bassPattern.getErrorMessage());
      }
  }

/**
 * Alternative method to set the display text if there is a name
 * @param text
 * @param name 
 */
public void setDisplayText(String text, String name)
{
    //System.out.println("The name is: " + name);
    //System.out.println("The text is: " + text);
    bassPatternText = text.trim();
    if( bassPatternText.equals("") )
    {
        return;
    }
    if( name == null || name.equals("null") || name.equals("") )
    {
        bassDisplayText = text.trim();
        String textPattern = "(rules " + bassPatternText + ")";
        Polylist list = Polylist.PolylistFromString(textPattern);
        bassPattern = BassPattern.makeBassPattern(list);
    }
    else
    {
        bassDisplayText = name.trim(); 
        String textPattern = "(name " + bassDisplayText + ")(rules "+ bassPatternText + ")";
        Polylist list = Polylist.PolylistFromString(textPattern);
        bassPattern = BassPattern.makeBassPattern(list);  
    }
    //System.out.println("The bass pattern's display text is: " + bassDisplayText);
    if( !bassPattern.getStatus() )
    {
        cannotPlay(bassPattern.getErrorMessage());
    }
}


public Color getPlayableColor()
  {
    return playableColor;
  }

public Color getUnplayableColor()
  {
    return unplayableColor;
  }

public void setDefinedRules(LinkedHashMap map)
{
    definedRules = map;
}

public LinkedHashMap getDefinedRules()
{
    return definedRules;
}
/**
 * Checks the pattern for correctness for the given time signature. 
 *
 * @return true if the pattern is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
 *
 */

public boolean checkStatus()
  {
    return bassPattern.getStatus();
  }


/**
 * @return the actual text displpayed in the text field
 *
 */

@Override
public String toString()
  {
    return bassPatternText.trim();
  }

}
