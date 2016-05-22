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

package imp.style;

/**
 *
 * @author keller
 */

import imp.style.StyleCellRenderer;
import imp.style.StyleTableModel;
import imp.style.stylePatterns.DrumRuleDisplay;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

class RowHeaderRenderer
        extends JLabel
        implements ListCellRenderer
  {
  public static int ROW_HEADER_WIDTH = 200;

  JTable table;

  ArrayList<String> rowHeaders;

  RowHeaderRenderer(ArrayList<String> rowHeaders, JTable table)
    {
    this.rowHeaders = rowHeaders;
    this.table = table;
    JTableHeader header = table.getTableHeader();
    setOpaque(true);
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    setHorizontalAlignment(LEFT);
    setForeground(header.getForeground());

    setBackground(StyleCellRenderer.backgroundBassColor);

//    setBackground(header.getBackground());
    setFont(header.getFont());
    setSize(ROW_HEADER_WIDTH, header.getHeight());

    addMouseListener(
            new MouseAdapter()
              {
              public void mousePressed(MouseEvent e)
                {
                System.out.println("event" + e);
                }

              });
    }

  public int getNumRows()
  {
      return rowHeaders.size();
  }
  
  public String getValue(int index)
  {
      return rowHeaders.get(index);
  }
  
  public void setValue(String text, int index)
    {
    rowHeaders.set(index, text);
    // Set all instruments in index row to the selected one
    int ncols = table.getColumnCount();

    for( int j = StyleTableModel.FIRST_PATTERN_COLUMN; j < ncols; j++ )
      {
      Object cell = table.getValueAt(index, j);
      if( cell != null & cell instanceof DrumRuleDisplay )
        {
        ((DrumRuleDisplay)cell).setInstrument(text);
        //System.out.println("Setting instrument in row " + index + " column " + j + " to " + text);
        }
      }
    }

  /**
   * called when the cell is rendered or re-rendered due to some screen change.
  If the index is not that of an instrument name, use a fixed label.
  Otherwise, take take the instrument name from the first column.
  (This should be changed when we can figure out a better way to do it.)
  @param list
  @param value
  @param index
  @param isSelected
  @param cellHasFocus
  @return
   */
  public Component getListCellRendererComponent(JList list,
                                                 Object value, int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
    {

    /*if( index < StyleTableModel.numberFixedRowHeaders )
    {
    setText(StyleTableModel.initialRowHeaders[index]);
    }
    else */

    if( index < rowHeaders.size() )
      {
      setText(rowHeaders.get(index));
      }

    if( index <= StyleTableModel.BASS_PATTERN_ROW )
      {
      setBackground(StyleCellRenderer.backgroundBassColor);
      }
    else if( index <= StyleTableModel.CHORD_PATTERN_ROW )
      {
      setBackground(StyleCellRenderer.backgroundChordColor);
      }
    else
      {
      setBackground(StyleCellRenderer.backgroundDrumColor);
      }

    return this;
    }

  }
 



