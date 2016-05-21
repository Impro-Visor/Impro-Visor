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

package imp.data.stylePatterns;

import imp.Constants;
import imp.com.CommandManager;
import imp.data.*;
import imp.gui.Notate;
import imp.gui.StyleEditor;
import java.util.LinkedHashMap;
import java.awt.Color;
import polya.Polylist;

/**
 * Note: The GUI part of this is defunct, subsumed in StyleEditor now.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class ChordPatternDisplay 
        extends PatternDisplay 
        implements Constants, Playable, Displayable {
  
    public static Color playableColor = Color.green;
    public static Color unplayableColor = Color.red;

    //The number added to the title of this object to help the user distinguish it from others.
    private int titleNumber = 0;

    String pushString = "";
    
    private String chordPatternText = "";
    private String chordDisplayText = "";
    
    private ChordPattern chordPattern;
    
    LinkedHashMap<String, Polylist> definedRules;
    
   /**
     * Constructs a new ChordPatternDisplay JPanel with default weight 10 and an empty pattern.
     **/
    public ChordPatternDisplay(Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize("", 10, "");
    }
    
   /**
     * Constructs a new ChordPatternDisplay JPanel with weight and rule parameters.
     **/   
    public ChordPatternDisplay(String rule, float weight, String pushString, Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize(rule, weight, pushString);
    }
    
    public ChordPatternDisplay(String rule, float weight, String pushString, String name, Notate parent, CommandManager cm, StyleEditor styleParent)
    {
        super(parent, cm, styleParent);
        initialize(rule, weight, pushString, name);
    }
    
    /**
     * Initializes all elements and components for the BassPatternDisplay GUI and collapses the pane.
     **/
    private void initialize(String rule, float weight, String pushString) {
  
        setWeight(weight);
        setDisplayText(rule);
        
        setPushString(pushString);   
    }
    
    private void initialize(String rule, float weight, String pushString, String name)
    {
        setWeight(weight);
        setDisplayText(rule, name);
        setName(name);
        setPushString(pushString);
    }
    
    //Accessors:
   
    /**
     * @return titleNumber
     **/    
    public int getTitleNumber() {
        return titleNumber;
    }
    
   /**
     * @return the actual text displpayed in the text field
     **/       
    public String getDisplayText() 
    {
        return chordDisplayText.trim();
    }
    
    @Override
    public String getPatternText()
    {
        return chordPatternText.trim();
    }
    
    /**
     * This is used for saving the pattern to a file, among possibly other uses.
     * @return the text and weight formatted with bass-pattern syntax used by the style classes 
     **/        
    public String getSavePattern() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(chord-pattern " );
        if( getDefinedRules().containsKey(getName()))
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
        String trimmed = pushString.trim();
        if( !trimmed.equals("") )
          {
            buffer.append(")(push ");
            buffer.append(pushString);
          }
        buffer.append("))");
        return buffer.toString();
    }    
    
    /**
     * used to create a temp style to play the pattern
     * @param 
     */
    public String getPattern()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(chord-pattern (name ");
        buffer.append(getName());
        buffer.append(")(rules ");
        buffer.append(getPatternText());
        buffer.append(")(weight ");
        buffer.append(getWeight());
        
        String trimmed = pushString.trim();
        if( !trimmed.equals("") )
        {
            buffer.append(")(push ");
            buffer.append(pushString);
        }
        
        buffer.append("))");
        return buffer.toString();
    }

    
    public void setPushString(String pushString) {
        this.pushString = pushString;
    }
    
    public String getPushString() {
        return pushString;
    }
    
    /**
     * @return the beats in this pattern.
     **/    
    public double getBeats() {
     double slots = getChordPattern().getDuration();
     return slots/BEAT; 
     }
    
    public ChordPattern getChordPattern()
      {
        //Polylist list = Polylist.PolylistFromString(getPattern());
        //Polylist argument = ((Polylist)list.first()).rest();
        
        //ChordPattern chordPattern = ChordPattern.makeChordPattern(argument);
//System.out.println("pattern = " + getPattern() + ", list = " + list +", ChordPattern = " + chordPattern);
        return chordPattern;
      }
    /**
     * @return the selected value of the checkbox marked "include" in the upper right corner
     **/
    public boolean getIncludedStatus() {
        return true;
    }
 
    //Mutators:
    
    /**
     * Sets the number in the title to num.
     **/         
    public void setTitleNumber(int num) {

        titleNumber = num;
    }

    /**
     * Sets the text in the text field to the parameter text and updates the user feedback information.
     **/ 
    public void setDisplayText(String text) 
        {
    chordPatternText = text.trim();
    chordDisplayText = text.trim();
    if( chordPatternText.equals("") )
      {
        return;
      }
    Polylist list = Polylist.PolylistFromString('(' + chordPatternText + ')');
    chordPattern = ChordPattern.makeChordPattern(Polylist.list(((Polylist)list.first()).cons("rules")));
    if( !chordPattern.getStatus() )
      {
        cannotPlay(chordPattern.getErrorMessage());
      }
  }
    
    public void setDisplayText(String text, String name)
   {
    //System.out.println("The name is: " + name);
    //System.out.println("The text is: " + text);
    chordPatternText = text.trim();
    if( chordPatternText.equals("") )
    {
        return;
    }
    if( name == null || name.equals("null") || name.equals("") )
    {
        chordDisplayText = text.trim();
        String textPattern = "(rules " + chordPatternText + ")";
        Polylist list = Polylist.PolylistFromString(textPattern);
        chordPattern = ChordPattern.makeChordPattern(list);
    }
    else
    {
        chordDisplayText = name.trim(); 
        String textPattern = "(name " + chordDisplayText + ")(rules "+ chordPatternText + ")";
        Polylist list = Polylist.PolylistFromString(textPattern);
        chordPattern = ChordPattern.makeChordPattern(list);  
    }
    //System.out.println("The bass pattern's display text is: " + bassDisplayText);
    if( !chordPattern.getStatus() )
    {
        cannotPlay(chordPattern.getErrorMessage());
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
 * Checks the pattern for correctness for the given time signature. Changes
 * icons, tooltips, and errorMsg to appropriate error feedback information.
 *
 * @return true if the pattern is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
 *
 */
   
public boolean checkStatus()
  {
  if( chordPattern != null )
    {
      return chordPattern.getStatus();
    }
  else return false;
  }

/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */
public boolean playMe(double swingVal, int loopCount, double tempo, Score score)
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
            tempStyle.setName("chordPattern");
            Style.setStyle("chordPattern", tempStyle);
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            ChordPart c = new ChordPart();
            String chord = styleEditor.getChord();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getCP().get(0).getDuration();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            score.setChordProg(c);
            notate.setChordVolume(styleEditor.getVolume());
            score.setTempo(tempo);

            playScore(notate, score, styleEditor);

          }
        catch( Exception e )
          {
            cannotPlay("exception " + e);
            return false;
          }
      }
    else
      {
        if( chordPattern != null )
          {
          cannotPlay(chordPattern.getErrorMessage());
          return false;
          }
      }
    return true;
  }

    /**
     * @return the actual text displpayed in the text field
     **/    
    @Override
    public String toString() {
        return chordPatternText.trim();
    }
           
}
