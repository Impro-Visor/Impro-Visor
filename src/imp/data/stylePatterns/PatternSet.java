/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

import imp.gui.StyleTableModel;
import java.util.ArrayList;
import javax.swing.JTable;

/**
 * Keeps information for a set of patterns (such as all Bass patterns, 
 * all Chord patterns, all Percussion patterns)
 * for the StyleTable.
 *
 * @author keller
 */
public class PatternSet
        extends ArrayList<PatternDisplay>
{
  protected String setTitle;

  public static String[] rowHeadSegment =
    {
    "Use",
    "Weight",
    "Beats"
    };

  JTable styleTable;

  int nextPatternIndex;
  int lastPatternColumnUsed;

  
  /**
   *Constructor for PatternSet
   @param setTitle
   @param styleTable
   */
  public PatternSet(String setTitle, JTable styleTable)
    {
    this.setTitle = setTitle;
    this.styleTable = styleTable;
    initialize();
    }

  public void initialize()
    {
    // initialExtrColumns is used so that this Vector index is the same as
    // the corresponding table column index
    clear();
    nextPatternIndex =
         StyleTableModel.initialExtraColumns + StyleTableModel.initialNumberOfPatterns;

    lastPatternColumnUsed = StyleTableModel.initialExtraColumns - 1;
    for( int i = 0; i < nextPatternIndex; i++ )
      {
      add(null);
      }
    }

  public int getLastPatternColumnUsed()
  {
    return lastPatternColumnUsed;
  }
  
  public int newPattern()
  {
    return ++lastPatternColumnUsed;
  }
  
  public void removePattern(PatternDisplay pattern)
    {
    int index = indexOf(pattern);
    set(index, null);
    //System.out.println("removing pattern: " + pattern + " at index " + index);
    }
}

