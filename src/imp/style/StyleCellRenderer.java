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

package imp.style;

import imp.style.stylePatterns.DrumRuleDisplay;
import imp.style.stylePatterns.ChordPatternDisplay;
import imp.style.stylePatterns.BassPatternDisplay;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Robert Keller
 */

public class StyleCellRenderer
        extends DefaultTableCellRenderer
        implements javax.swing.table.TableCellRenderer
  {
  public static final Color selectedColor = new Color(146, 222, 254);

  public static String NULL_DATA_RENDERING = "";

  public static final Color playableBassColor = Color.ORANGE;

  public static final Color playableChordColor = Color.GREEN;

  public static final Color playableDrumColor = Color.YELLOW;

  public static final Color unplayableColor = Color.RED;

  public static final Color backgroundBassColor = playableBassColor;

  public static final Color backgroundChordColor = playableChordColor;

  public static final Color backgroundDrumColor = playableDrumColor;

  /** Creates a new instance of StyleTableRenderer */
  public StyleCellRenderer()
    {
    super();
    }

  @Override
  public Component getTableCellRendererComponent(
          JTable table,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column)
    {

    Component component = null;

    Object data = table.getValueAt(row, column);

    String rendering;

    if( column == StyleTableModel.INSTRUMENT_INCLUDE_COLUMN 
    && (row == StyleTableModel.BASS_PATTERN_ROW 
     || row == StyleTableModel.CHORD_PATTERN_ROW 
     || row >= StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW) )
      {
      // Handle check boxes for including instruments
      JCheckBox box = new JCheckBox("");
      box.setSelected(value.equals(Boolean.TRUE));
      if( row <= StyleTableModel.BASS_PATTERN_ROW )
        {
        box.setBackground(backgroundBassColor);
        }
      else if( row <= StyleTableModel.CHORD_PATTERN_ROW )
        {
        box.setBackground(backgroundChordColor);
        }
      else
        {
        box.setBackground(backgroundDrumColor);
        }
      return box;
      }
    else if( data instanceof BassPatternDisplay )
      {
      BassPatternDisplay pattern = (BassPatternDisplay)value;

      JTextField field = new JTextField(pattern.getDisplayText());
      field.setBackground(isSelected ? selectedColor : pattern.getColor());
      return field;
      }
    else if( data instanceof ChordPatternDisplay )
      {
      ChordPatternDisplay pattern = (ChordPatternDisplay)value;

      JTextField field = new JTextField(pattern.getDisplayText());
      field.setBackground(isSelected ? selectedColor : pattern.getColor());
      return field;
      }
    else if( data instanceof DrumRuleDisplay )
      {
      DrumRuleDisplay rule = (DrumRuleDisplay)value;

      JTextField field = new JTextField(rule.getDisplayText());
      field.setBackground(isSelected ? selectedColor : rule.getColor());
      return field;
      }
    else
      {
      rendering = data == null ? NULL_DATA_RENDERING : data.toString();
      JTextField field = new JTextField(rendering);

      // How to make cell not be highlighted??

      if( row <= StyleTableModel.BASS_PATTERN_ROW )
        {
        field.setBackground(isSelected ? selectedColor : backgroundBassColor);
        }
      else if( row <= StyleTableModel.CHORD_PATTERN_ROW )
        {
        field.setBackground(isSelected ? selectedColor : backgroundChordColor);
        }
      else
        {
        field.setBackground(isSelected ? selectedColor : backgroundDrumColor);
        }
      return field;
      }
    }

  }


