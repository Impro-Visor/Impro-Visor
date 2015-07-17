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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;


/**
 *
 * @author keller
 */
public class StyleTableColumnModelListener implements TableColumnModelListener
{
  JTable theTable;
  StyleTableColumnModel theModel;
  
  /** Creates a new instance of StyleTableColumnModelListener */
  public StyleTableColumnModelListener(JTable theTable, StyleTableColumnModel theModel)
  {
    this.theTable = theTable;
    this.theModel = theModel;
  }
  
public void columnAdded(TableColumnModelEvent e) {
    //System.out.println("StyleTableColumn columnAdded event: " + e);
}


public void columnRemoved(TableColumnModelEvent e) {
    //System.out.println("StyleTableColumn columnRemoved event: " + e);
}


public void columnMoved(TableColumnModelEvent e) {
    //System.out.println("StyleTableColumn columnMoved event: " + e);
}


public void columnMarginChanged(ChangeEvent e) {

    //System.out.println("StyleTableColumn columnMarginChanged event: " + e);
}


public void columnSelectionChanged(ListSelectionEvent e)
  {
    
    //System.out.println("StyleTableColumn columnSelectionChanged event: " + e);
    if( e instanceof ListSelectionEvent )
    {
      // Only record if a single column is selected.
      
      if( theModel.getSelectedColumnCount() == 1 )
      {
      int column = theModel.getSelectedColumns()[0];
      
      // System.out.println("selected column: " + theTable.getSelectedColumn());
      }
    }
  }


}
