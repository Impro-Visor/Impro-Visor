/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011-2014 Robert Keller and Harvey Mudd College
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

//TODO: become consistent with either long or int
//TODO: rework access levels
//TODO: setting a song to an equal metre (4/4 -> 8/8) changes the measures. This is wrong.

package imp.roadmap;

import imp.brickdictionary.*;
import imp.data.PitchClass;
import java.awt.*;

/**
 * Contains constants and methods used to draw the roadmap and its contents.
 * @author August Toman-Yih
 */
public class RoadMapSettings {

    /** Slots contained in each beat */
    public int slotsPerBeat = imp.Constants.BEAT;
    
    /** Horizontal margin of roadmap */
    public int xOffset = 50;
    
    /** 
     * Vertical offset of roadmap. This needs to be large enough that
     * printing does not cut off the title.
     */
    public int yOffset = 100;
    
    /** Number of measures allowed per line */
    public int barsPerLine = 8;
    
    /** Height of a line on the roadMap */
    public int lineHeight = 60;
    
    /** Space between lines */
    public int lineSpacing = 40;
    
    /** Pixel width of each measure */
    public int measureLength = 100;
    
    private int[] metre = {4,4}; // This is only here because I want measures to be a set length.
                                 // CURSE YOU POOR PLANNING
    
    /* --- Colors --- */
    /** Color of the grid lines */
    public Color gridLineColor = new Color(100,100,100);
    
    /** Color of the grid background */
    public Color gridBGColor = new Color(245,245,245);
    
    /** Color of a basic line */
    public Color lineColor = Color.BLACK;
    
    /** Color of text */
    public Color textColor = Color.BLACK;
    
    /** Color of a selected brick */
    public Color selectedColor = new Color(208, 255, 255);
    
    /** Color of the brick background */
    public Color brickBGColor = Color.WHITE;
    
    /** Color of the brick background */
    public Color rolloverBGColor = new Color(255, 225, 50);
    
    /** Color of the join background*/
    public Color joinBGColor = new Color(255, 255, 171);
    
    /** Color of the playline */
    public Color playLineColor = Color.RED;
    
    /** Color of the play section markers */
    public Color playSectionColor = Color.GREEN;
    
    /** Are keys colored? */
    public boolean keysColored = true;
    
    /** Color for non colored mode */
    public Color defaultColor = Color.LIGHT_GRAY;
    
    /** Color for no key */
    public Color noKeyColor = new Color(235, 235, 235);
    
    /** Colors associated with different keys */
    public Color[] keyColors = {new Color(250, 220, 100), // C
                                        new Color(247, 126, 255), // Db
                                        new Color(150, 255,   0), // D
                                        new Color(255, 182, 180), // Eb
                                        new Color(131, 235, 255),  // E
                                        new Color(255, 221, 118), // F
                                        new Color(169, 184, 250), // Gb
                                        new Color(255, 255,   0), // G
                                        new Color(255, 189, 255), // Ab
                                        new Color(150, 255, 202), // A
                                        new Color(255, 217, 150), // Bb
                                        new Color(157, 209, 255)};// B
    
    private int colorationBias = 0;
    
    /* --- Strokes --- */
    
    /** Basic line */
    public BasicStroke basicLine    = new BasicStroke(1);
    
    /** Line for brick outline */
    public BasicStroke brickOutline = new BasicStroke(2);
    
    /** Cursor line */
    public BasicStroke cursorLine   = new BasicStroke(2);
    
    /* --- Fonts --- */
    /** Font for normal text, such as bricks, joins, rollovers, etc */
    
    public Font basicFont = new Font("Lucida Grande", Font.BOLD, 14);
    
    /** Font for titles */
    
    public Font titleFont = new Font("Lucida Grande", Font.BOLD, 24);
    
    /**
     * whether to show joins in roadmap or not
     */
    
    public boolean showJoins = true;
    
    /**
     * whether to show brick names in roadmap or not
     */
    
    public boolean showBrickNames = true;
    
    /**
     * whether to show keys in roadmap or not
     */
    
    public boolean showKeys = true;
    
    /**
     * whether to show the starting note from the leadsheet or not
     * 
     */
    public boolean showStartingNote = true;
    
    /**
     * whether to show the starting note from the leadsheet or not
     * 
     */
    public boolean showVariants = true;
    
    /**
     * whether to show the starting note from the leadsheet or not
     * 
     */
    public boolean showStyles = true;
    
    public int getBlockLength(Block block)
    {
        return (int) (block.getDuration() * measureLength)/getSlotsPerMeasure();
    }

    /**
     * Calculates the slots contained in a measure
     * @return number of slots in a measure
     */
    public int getSlotsPerMeasure()
    {
        return slotsPerBeat * metre[0] * 4 / metre[1];
    }
    
    /**
     * Returns the length of a duration in the current settings
     * @param dur the duration
     * @return the length
     */
    public int getLength(int dur)
    {
        return (dur * measureLength)/getSlotsPerMeasure();
    }
    
    /**
     * Returns the x cutoff in the current settings
     * @return the x cutoff
     */
    public int getCutoff()
    {
        return xOffset + getLineLength();
    }
    
    /**
     * Returns the number of beats per line
     * @return the number of beats per line
     */
    public int getSlotsPerLine()
    {
        return getSlotsPerMeasure()*barsPerLine;
    }
    
    /**
     * Returns the length of a line
     * @return the length
     */
    public int getLineLength()
    {
        return barsPerLine * measureLength;
    }
    
    /**
     * Returns the distance between each line
     * @return the distance between each line
     */
    public int getLineOffset()
    {
        return lineHeight + lineSpacing;
    }
    
    /**
     * Returns the height of a block
     * @return the height of a block
     */
    public int getBlockHeight()
    {
        return lineHeight/3;
    }
    
    /**
     * Returns the color of the given key (assumed to be major)
     * @param key the key
     * @return the color
     */
    public Color getKeyColor(int key)
    {
        if(keysColored) {
            if(key != -1)
                return keyColors[(int)(key + colorationBias) % 12];
            return defaultColor;
        }
        return noKeyColor;
    }
    
    /**
     * Returns the color for a keyspan (taking mode into account)
     * @param keySpan
     * @return the color
     */
    public Color getKeyColor(KeySpan keySpan)
    {
        return getKeyColor((int)keySpan.getKey(), keySpan.getMode());
    }
    
    /**
     * Returns the color for the key with the given mode
     * @param key the key
     * @param mode the mode
     * @return the color
     */
    public Color getKeyColor(int key, String mode)
    {
        if(mode.equalsIgnoreCase("minor"))
                    return getKeyColor(key+3);
                else if (mode.equalsIgnoreCase("Dominant"))
                    return getKeyColor(key+5);
                else
                    return getKeyColor(key);
    }
    
    /**
     * Returns the number of lines taken up by this number of beats
     * @param beats number of beats
     * @return number of lines
     */
    public int getLines(int beats)
    {
        int lines = (int) (beats/getSlotsPerLine());
        return lines;
    }

    /**
     * Getter for slotsPerBeat
     * @return the number of slots in a beat
     */
    public int getSlotsPerBeat()
    {
        return slotsPerBeat;
    }
    
    /**
     * Wraps x coordinate
     * @param x
     * @return the new x coordinate, the number of lines
     */
    public int[] wrap(int x)
    {
        int lines = (x - xOffset)/getLineLength();
        int endX = xOffset + ((x - xOffset) % getLineLength());
        return new int[]{endX, lines};
    }
    
    /**
     * Returns a slot offset, line pair resulting from wrapping the given number
     * of slots
     * @param slots
     * @return int[]{slot offset, line}
     */
    public int[] wrapFromSlots(int slots)
    {
        int lines = getLines(slots);
        int endX = (int)(slots%getSlotsPerLine());
        return new int[]{endX, lines};
    }

    /**
     * Trims a string to the desired length, using the given fontMetrics
     * @param string string to be trimmed
     * @param length length to trim to
     * @param metrics fontMetrics
     * @return trimmed string
     */
    public static String trimString(String string, int length, FontMetrics metrics)
    {
        int stringLength = metrics.stringWidth(string);
        if(stringLength < length)
            return string;
        
        if((string.length() * length)/stringLength <= 1)
            return "...";
        return string.substring(0, (string.length() * length)/stringLength - 2).concat("...");
    }
  
    /**
     * Protected method to set the metre. Only used to keep drawing correct.
     * @param metre 
     */
    protected void setMetre(int[] metre)
    {
        this.metre[0] = metre[0];
        this.metre[1] = metre[1];
    }
    
    public void generateColors(float sat)
    {
        for(int i = 0; i < 12; i++) {
            keyColors[(i*7)%12] = Color.getHSBColor(i/13.0f, sat, 1.0f);
        }
    }
    
    public void setColorationBias(int bias)
      {
        colorationBias = bias;
      }
    
    public int getColorationBias()
      {
        return colorationBias;
      }
    
    public void setBarsPerLine(int bars)
      {
        if( bars > 0 )
          {
            barsPerLine = bars;
          }
      }
    private boolean usePhi = false;
    private boolean useDelta = false;

    public boolean getPhi() {
        return usePhi;
    }

    public boolean getDelta() {
        return useDelta;
    }

    public void setPhi(boolean phi) {
        usePhi = phi;
    }

    public void setDelta(boolean delta) {
        useDelta = delta;
    }
    
    private PitchClass romanNumeralHomeKey;
    
    public void setRomanNumeralHomeKey(String keyName) {
        romanNumeralHomeKey = keyName.equals("") ? null : PitchClass.getPitchClass(keyName);
    }
    
    public PitchClass getRomanNumeralHomeKey(){
        return romanNumeralHomeKey;
    }
}