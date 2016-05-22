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

import java.util.Vector;
import javax.swing.table.TableColumn;


/**
 *
 * @author keller
 */

// Currently not used, nor is it correct.

public class StyleTableColumn extends TableColumn
{
  
  protected Object headerValue = "";
  
  /** the column is represented as a Vector */
  
  Vector<Object> theColumn;
  
  /** Create a new instance */
  
  public StyleTableColumn(int rows)
  {
    theColumn = new Vector<Object>();
    for( int row = 0; row < rows; row++ )
    {
      theColumn.addElement(new String(""));
    }
  }
  
 public StyleTableColumn(Object headerValue, Vector<Object> contents)
  {
    this.headerValue = headerValue;
    theColumn = contents;
  }
  
  public Object getValue(int row)
 {
   ensureRowExists(row);
   return theColumn.elementAt(row);
 }
 
public void ensureRowExists(int row)
{
  while( row >= theColumn.size() )
  {
    theColumn.addElement(new String(""));
  }
}
 
public void setValue(Object value, int row)
 {
  ensureRowExists(row);
   theColumn.setElementAt(value, row);
 }

public void setHeaderValue(Object value)
{
  headerValue = value;
}
 
 public void addElement(Object value)
 {
   theColumn.add(value);
 }
}
