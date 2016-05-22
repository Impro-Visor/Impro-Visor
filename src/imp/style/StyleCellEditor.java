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

import imp.style.stylePatterns.Displayable;
import imp.style.stylePatterns.DrumPatternDisplay;
import imp.style.stylePatterns.PatternDisplay;
import imp.style.stylePatterns.ChordPatternDisplay;
import imp.style.stylePatterns.BassPatternDisplay;
import java.awt.Choice;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Robert Keller
 */

public class StyleCellEditor
        extends DefaultCellEditor
        implements TableCellEditor
  {
  /** The editor text field, for editing pattern and rules */
  JTextField editField;

  /** Checkbox for inclusion or exclusion of instruments or patterns */
  JCheckBox inclusionCheckbox;

  /** Menu of possible instrument values */
  Choice volumeChoiceMenu;

  /** Minimum volume value in menu */
  int VOLUME_MIN = 0;

  /** Nominal volume value in menu */
  int VOLUME_NOMINAL = 100;

  /** Maximum volume value in menu */
  int VOLUME_MAX = 127;

  /** The rule or pattern being edited */
  Object beingEdited;

  /** Row of selected cell */
  int row;

  /** Column of selected cell */
  int column;

  /** Table being edited */
  JTable table;

  /** The original value in a cell */
  Object value;

  /** The StyleEditor for which this is a cell editor */
  StyleEditor styleEditor;

  /** A Float value of zero */
  
  static Float zeroFloat = new Float(0);

  /**
   * Create a new instance of StyleCellEditor.
   */
  
  public StyleCellEditor(JTextField textField, StyleEditor styleEditor)
    {
    super(textField);
    editField = textField;
    this.styleEditor = styleEditor;

    // Establish representation for inclusion choices.

    inclusionCheckbox = new JCheckBox();


    // Establish menu for inclusion choices.

    volumeChoiceMenu = new Choice();

    for( int i = VOLUME_MAX; i >= VOLUME_MIN; i-- )
      {
      volumeChoiceMenu.addItem("" + i);
      }
    }

  
  /** 
   * This method is called when editing is begun.
   * It gets the value stored in the cell and saves it in this editor object.
   */
  
  @Override
  public Component getTableCellEditorComponent(JTable table, Object value,
                                                boolean isSelected, int row,
                                                int column)
    {
    // Capture stuff, some of which is not used until getCellEditorValue() is called.
    this.table = table;
    this.row = row;
    this.column = column;
    this.value = value; // reserve original value, for possible restoration
    beingEdited = value;
    if( row >= table.getRowCount() || column >= table.getColumnCount() )
      {
      // Not sure how this can happen, but apparently it can -- FIX
      return null;
      }

    // Handle instrument inclusion entry
    if( column == StyleTableModel.INSTRUMENT_INCLUDE_COLUMN 
        && row >= StyleTableModel.FIRST_INSTRUMENT_ROW )
      {
      inclusionCheckbox.setSelected(beingEdited.equals(Boolean.TRUE));
      return inclusionCheckbox;
      }

    // Handle pattern and rule entries
    if( beingEdited instanceof Displayable )
      {
      editField.setText(((Displayable)beingEdited).getDisplayText());
      return editField;
      }
    else if( beingEdited != null )
      {
      // For now, anything else is treated as a plain string.
      editField.setText(beingEdited.toString());
      return editField;
      }

    // An editor component must be returned.

    return new JTextField(styleEditor.setCell(StyleCellRenderer.NULL_DATA_RENDERING, row, column, StyleEditor.SILENT).toString());
    }

  
  /** 
   * This method is called when editing is completed.
   * It must return the new value to be stored in the cell.
   */
   
  @Override
  public Object getCellEditorValue()
    {

    // Handle simple attributed modifications that are done by components.

    if( row >= table.getRowCount() || column >= table.getColumnCount() )
      {
      // Not sure how this can happen, but apparently it can -- FIX
      return null;
      }

    // Possible edit of weight entry
    if( column >= StyleTableModel.FIRST_PATTERN_COLUMN 
            && (row == StyleTableModel.BASS_PATTERN_WEIGHT_ROW 
             || row == StyleTableModel.CHORD_PATTERN_WEIGHT_ROW 
             || row == StyleTableModel.DRUM_PATTERN_WEIGHT_ROW) )
      {
      Float weight = zeroFloat;
      try
        {
        weight = new Float(editField.getText().trim());  // FIX: Check that it is an integer
        if( weight < 0 )
          {
          throw new ClassCastException("weight must be non-negative");
          }
        styleEditor.setStatus("OK");
        }
      catch( Exception e )
        {
        try
          {
          weight = new Float(value.toString());             // restore original
          styleEditor.setStatus("must be non-negative number");
          return weight;
          }
        catch( Exception f )
          {
          return weight;
          }
        }

      // set weights if pattern exists

      if( row == StyleTableModel.BASS_PATTERN_WEIGHT_ROW )
        {
        PatternDisplay ob = styleEditor.getBassPattern(column);
        if( ob instanceof BassPatternDisplay )
          {
          ((BassPatternDisplay)ob).setWeight(weight.floatValue());
          }
        }
      else if( row == StyleTableModel.CHORD_PATTERN_WEIGHT_ROW )
        {
        PatternDisplay ob = styleEditor.getChordPattern(column);
        if( ob instanceof ChordPatternDisplay )
          {
          ((ChordPatternDisplay)ob).setWeight(weight.floatValue());
          }
        }
      else if( row == StyleTableModel.DRUM_PATTERN_WEIGHT_ROW )
        {
        PatternDisplay ob = styleEditor.getDrumPattern(column);
        if( ob instanceof DrumPatternDisplay )
          {
          ((DrumPatternDisplay)ob).setWeight(weight.floatValue());
          }
        }

      // Return the weight
      return weight;
      }
    
    if( row == StyleTableModel.CHORD_PATTERN_PUSH_ROW )
      {
      PatternDisplay ob = styleEditor.getChordPattern(column);
        if( ob instanceof ChordPatternDisplay )
          {
          String pushString = editField.getText().trim();
          ((ChordPatternDisplay)ob).setPushString(pushString);
          return pushString;
          }
      }
    
    if( row == StyleTableModel.DRUM_PATTERN_NAME_ROW )
    {
        PatternDisplay ob = styleEditor.getDrumPattern(column);
        if( ob instanceof DrumPatternDisplay )
        {
            String patternName = editField.getText().trim();
            ((DrumPatternDisplay)ob).setName(patternName);
            return patternName;
        }
    }

    if( column == StyleTableModel.INSTRUMENT_INCLUDE_COLUMN 
     && row >= StyleTableModel.FIRST_INSTRUMENT_ROW )
      {
      return inclusionCheckbox.isSelected();
      }

    // Handle pattern and rule values.

    if( beingEdited instanceof Displayable )
      {
      Displayable thing = (Displayable)beingEdited;

      String contents = editField.getText().trim();

      thing.setDisplayText(contents);
      return styleEditor.setCell(editField.getText().toUpperCase(), row, column, StyleEditor.PLAY);

      }
    else if( beingEdited == null )
      {
      return styleEditor.setCell("", row, column, StyleEditor.SILENT);

      }

    return styleEditor.setCell(editField.getText().toUpperCase(), row, column, StyleEditor.PLAY);
    }

  }

