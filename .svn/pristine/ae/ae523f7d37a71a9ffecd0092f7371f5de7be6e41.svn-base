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

package imp.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author keller
 */
public class StyleTableColumnModel 
             extends DefaultTableColumnModel 
             implements TableColumnModel
{
   
  JTable theTable;
  
  StyleTableColumnModelListener listener = new StyleTableColumnModelListener(theTable, this);
      
  /** Creates a new instance of StyleTableColumnModel */
  public StyleTableColumnModel(JTable theTable)
  {
    super();
    this.theTable = theTable;
    setColumnSelectionAllowed(true);
    addColumnModelListener(listener);
  }
  
/**
 * This is an attempt to get the header and columns to line up.
 * It doesn't do anything currently.
 */
  
@Override
public void addColumn(TableColumn tc)
{
  tc.setMinWidth(10);
  super.addColumn(tc);
}


public void refresh()
{
// What, if anything, to put here to make column headers refresh?
}
}
