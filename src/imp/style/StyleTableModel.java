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

import imp.Constants;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Robert Keller 
 */

public class StyleTableModel extends DefaultTableModel implements TableModel, Constants {
  
    /** These determine number of table columns and rows */
  
    /* Caution: If initialNumberOfPatterns is less than 93, the header gets out of sync
       with the table when new columns are added. I have no idea why. It could be a bug
       in Java.  They sync up again if the column boundary is adjusted in any minor way
       and the scrollbar is scrolled slightly, but not unless this is done.  Also, adding
       a new column disturbs the width of the initial columns.
     */
  
    public static int initialNumberOfPatterns = 95;
    public static int initialExtraColumns = 1;
    
    int initialPercussionInstruments = 30;  
    
    // FIX: An out-of-bounds error can occur if table is enlarged sufficiently
    //       at imp.gui.StyleTableModel.isChordCell(StyleTableModel.java:333)
    //    at imp.gui.StyleEditor.isChordCell(StyleEditor.java:2017)
    //    at imp.gui.StyleCellEditor.getCellEditorValue(StyleCellEditor.java:281)
    // Rows look like they are being added when the table is stretched, but
    // apparently they are  not.

    
    int patternColumnCount = 0;
    int rowCount = 0;

    /* Pattern categories */
    public static final String BASS = "Bass";
    public static final String CHORD = "Chord";
    public static final String PERCUSSION = "Percussion";
    
    public static String categoryName[] = {BASS, CHORD, PERCUSSION};
    
    public static final int BassCategory       = 0;
    public static final int ChordCategory      = 1;
    public static final int PercussionCategory = 2;
    
    private static String emptyCell = ""; // Change to make emptiness visible
    
    public static String BLANK = "";   // For an intentionally-blank cell
    public static String PATTERN = ""; // Prefix for naming patterns numerically
    
    public static int numberFixedRowHeaders = 8;
    
    /* 
     * In the following, the literal string is used for table lookup, so
     * these names should not be changed casually.  In particular, the method
     * addPatternColumn will need to be changed to conform.
     */
    
    public static String initialRowHeaders[] =
      {
      "Bass Beats",
      "Bass Weight",
      BASS,
      "Chord Beats",
      "Chord Weight",
      "Chord Push",
      CHORD,
      "Drum Beats",
      "Drum Weight",
      "Pattern Name",
      "Acoustic_Bass_Drum",
      "Acoustic_Snare",
      "Ride_Cymbal_1",
      "Closed_Hi-Hat",
      "Open_Hi-Hat",
      PERCUSSION
     };
    
    /** Defining list of row names */
    ArrayList<String> rowNames;

    ArrayList<Long> instrumentNumbers;
    
    int minRowCount;
    
    JTable theTable;
    
    public static final String  UNNAMED_PATTERN_NAME   = "";
    public static final Integer DEFAULT_PATTERN_WEIGHT = 10;
    public static final String  DEFAULT_PATTERN_PUSH   = "";
    public static final Integer DEFAULT_PATTERN_BEATS  = 0;
    public static final String  DEFAULT_INSTRUMENT     = PERCUSSION;
    
    // Designated columns

    public static final int INSTRUMENT_INCLUDE_COLUMN = 0;
    public static final int FIRST_PATTERN_COLUMN = 1;
    public static final int PATTERN_COLUMN_BASE = FIRST_PATTERN_COLUMN - 1;
    
    // Designated rows

    public static final int BASS_PATTERN_BEATS_ROW          = 0;
    public static final int BASS_PATTERN_WEIGHT_ROW         = 1;
    public static final int BASS_PATTERN_ROW                = 2;
    
    public static final int CHORD_PATTERN_BEATS_ROW         = 3;
    public static final int CHORD_PATTERN_WEIGHT_ROW        = 4;
    public static final int CHORD_PATTERN_PUSH_ROW          = 5;
    public static final int CHORD_PATTERN_ROW               = 6;
    
    public static final int DRUM_PATTERN_BEATS_ROW          = 7;
    public static final int DRUM_PATTERN_WEIGHT_ROW         = 8;
    public static final int DRUM_PATTERN_NAME_ROW           = 9;

    public static final int FIRST_INSTRUMENT_ROW            = 2;
    public static final int FIRST_PERCUSSION_INSTRUMENT_ROW = 10;
    
    int lastPatternColumn = PATTERN_COLUMN_BASE;
    int lastPercussionrowUsed = FIRST_PERCUSSION_INSTRUMENT_ROW - 1;
    
    public static final Boolean POSITIVE_INCLUDE_VALUE = Boolean.TRUE;
    public static final Boolean NEGATIVE_INCLUDE_VALUE = Boolean.FALSE;
    public static final Boolean INITIAL_INCLUDE_VALUE = POSITIVE_INCLUDE_VALUE;
    
    private static boolean setValueTraceValue = false;
    
    /**
   * Creates a new instance of StyleTableModel
   */
    
    public StyleTableModel(JTable theTable) {
      resetPatterns();
      this.theTable = theTable;
      minRowCount = initialRowHeaders.length + initialPercussionInstruments;
      // doesn't help: theTable.setAutoCreateColumnsFromModel(true);
      
      theTable.setColumnModel(new StyleTableColumnModel(theTable));

      // Create row headers. These will determine how long columns are.
      
      initRowHeaders();
            
      // Create the  non-pattern columns

       addEmptyColumn("Use");

      // Add columns  for initial blank patterns
      
      while( patternColumnCount < initialNumberOfPatterns  )
        {
        newPatternColumn();
        }

     
     // Indicate int two columns that all instruments are included
     setValueAt(INITIAL_INCLUDE_VALUE, BASS_PATTERN_ROW,  INSTRUMENT_INCLUDE_COLUMN);
     setValueAt(INITIAL_INCLUDE_VALUE, CHORD_PATTERN_ROW, INSTRUMENT_INCLUDE_COLUMN);
     int nrows =  getRowCount();
     for( int i = FIRST_PERCUSSION_INSTRUMENT_ROW; i < nrows; i++ )
      {
      setValueAt(INITIAL_INCLUDE_VALUE,     i, INSTRUMENT_INCLUDE_COLUMN);
      }
     
    }
    
/** Defining list of row names */

public void initRowHeaders()
  {
    rowNames = new ArrayList<String>();
    rowCount = 0;

    for( ; rowCount < initialRowHeaders.length; rowCount++ )
      {
        rowNames.add(initialRowHeaders[rowCount]);
      }

    // Create header rows for percussion instruments

    for( int i = 1; i <= initialPercussionInstruments; i++ )
      {
        rowNames.add(DEFAULT_INSTRUMENT);
        rowCount++;
      }
  }

    
  public ArrayList<String> getRowHeaders()
  {
    return rowNames;
  }

                
 /** Initialize one pattern column: used when loading style file. 
  * Note that the code in addPatternColumn has to be kept in synch with this.
  */

   public void initializePatternColumn(int j)
   {
   // Set default values for pattern
   setValueAt(DEFAULT_PATTERN_BEATS,    BASS_PATTERN_BEATS_ROW,    j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   BASS_PATTERN_WEIGHT_ROW,   j);
   setValueAt(BLANK,                    BASS_PATTERN_ROW,          j);
   setValueAt(DEFAULT_PATTERN_BEATS,    CHORD_PATTERN_BEATS_ROW,   j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   CHORD_PATTERN_WEIGHT_ROW,  j);
   setValueAt(DEFAULT_PATTERN_PUSH,     CHORD_PATTERN_PUSH_ROW,    j);
   setValueAt(BLANK,                    CHORD_PATTERN_ROW,         j);
   setValueAt(DEFAULT_PATTERN_BEATS,    DRUM_PATTERN_BEATS_ROW,    j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   DRUM_PATTERN_WEIGHT_ROW,   j);
   setValueAt(BLANK,                    DRUM_PATTERN_NAME_ROW,     j);
   int numRows = getRowCount();
   for( int row = FIRST_PERCUSSION_INSTRUMENT_ROW; row < numRows; row ++ )
     {
     setValueAt(emptyCell, row, j);
     }
   }
 
 public void newPatternColumn()
 {
   addPatternColumn(PATTERN + (++patternColumnCount));
 }
 
/** Add one empty column, with size determined by the number of rows. */

 public void addEmptyColumn(String name)
   {
    ArrayList<Object> columnContents = new ArrayList<Object>();
    
    //columnContents.addElement(name);
    
    int numRows = Math.max(minRowCount, getRowCount());
    for( int j = 0; j < numRows; j++)
      {
      columnContents.add(emptyCell);
      }
    
    addColumn(name, columnContents.toArray());

     }
 
 /** Add one pattern column, with size determined by the number of rows. 
  *  Note that this needs to be changed if there is any change in the order
  *  of rows. See also the "designated row" constants.
  */

 public void addPatternColumn(String name)
   {
    // Initialized pattern column
   
    ArrayList<Object> columnContents = new ArrayList<Object>();
    
    // For Bass
    columnContents.add(DEFAULT_PATTERN_BEATS);
    columnContents.add(DEFAULT_PATTERN_WEIGHT);
    columnContents.add(BLANK);  // To allow for instrument sub-header
    
    // For Chord
    columnContents.add(DEFAULT_PATTERN_BEATS);
    columnContents.add(DEFAULT_PATTERN_WEIGHT);
    columnContents.add(BLANK);
    columnContents.add(BLANK);  // To allow for instrument sub-header
    
    // For Drum
    columnContents.add(DEFAULT_PATTERN_BEATS);
    columnContents.add(DEFAULT_PATTERN_WEIGHT);
    columnContents.add(BLANK);
    
    int numRows = Math.max(minRowCount, getRowCount());
    for( int j = FIRST_PERCUSSION_INSTRUMENT_ROW; j < numRows; j++)
      {
      columnContents.add(emptyCell);
      }
    
    addColumn(name, columnContents.toArray());
    }
 
/**
 * All column additions should go through here, for monitoring.
 * However, the work is done in super.
 */
 
public void addColumn(String name, Object[] contents)
{
  super.addColumn(name, contents);
}

public int getNumColumns()
{
    return super.getColumnCount()-1;
}

 public void newRow()
 {
   addRow(PERCUSSION);
 }
 
 /**
  * Add a new row to the bottom of the table.
  */
 
 public void addRow(String rowHeader)
  {
    rowNames.add(rowHeader);
    rowCount++;
    
    int size = getColumnCount();
    //System.out.println("add row named " + rowHeader + ", size = " + size);
    ArrayList<Object> row = new ArrayList<Object>(size);
    row.add(INITIAL_INCLUDE_VALUE);
    for( int j = 2; j < size; j++)
    {
      row.add(emptyCell);
    }
    super.addRow(row.toArray());
    int rowNumber = getRowCount()-1;
    // System.out.println("new row " + rowNumber + " is " + row);
    fireTableRowsInserted(rowNumber, rowNumber);
    theTable.addNotify();
    theTable.tableChanged(null);
  }
 
 public void setValueTrace(boolean value)
 {
   setValueTraceValue = value;
 }
  
 /**
  * The raw interface to setting a value in a cell.
  *
  @param value the Object to be stored in the cell
  @param row the row in which the value is stored
  @param col the column in which the value is stored
  */
 
  @Override
  public void setValueAt(Object value, int row, int col)
  {
    if( setValueTraceValue )
    {
    System.out.println("setValue at row = " + row + ", col = " + col + " to " + value);
    }
    
    try
    {
    super.setValueAt(value, row, col);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      System.out.println("Ignoring array out of bounds setValue at " + row + ", " + col + ": " + value);
    }
  }
  
  public int lastColumnIndex() {
        return getColumnCount() - 1;
    }
    
   public int lastrowIndex() {
        return getRowCount() - 1;
    }
    
   /** 
    * Determines which cells are editable
    */
   
    @Override
    public boolean isCellEditable(int row, int col) {
        // the data/cell address is constant, no matter where the cell appears onscreen.
        return ( col == INSTRUMENT_INCLUDE_COLUMN 
                  && (  row == BASS_PATTERN_ROW 
                     || row == CHORD_PATTERN_ROW 
                     || row >= FIRST_PERCUSSION_INSTRUMENT_ROW
                     )
                )
                // If you want to return editability to the cells, 
                // uncomment this!
                
                //|| ( col >= FIRST_PATTERN_COLUMN 
                  //&& (  row == BASS_PATTERN_WEIGHT_ROW 
                     //|| row == BASS_PATTERN_ROW 
                     //|| row == CHORD_PATTERN_WEIGHT_ROW 
                     //|| row == CHORD_PATTERN_PUSH_ROW 
                     //|| row == CHORD_PATTERN_ROW 
                     //|| row == DRUM_PATTERN_WEIGHT_ROW 
                     //|| row == DRUM_PATTERN_NAME_ROW
                     //|| row >= FIRST_PERCUSSION_INSTRUMENT_ROW
                     //)
               //) 
                
            ;
    }
    
  public boolean isChordCell(int row, int col)
    {
    return getRowHeaders().get(row).equals(CHORD);
    }
  
  public boolean isBassCell(int row, int col)
    {
    return getRowHeaders().get(row).equals(BASS);
    }
  
  public boolean isDrumCell(int row, int col)
  {
    return row >= FIRST_PERCUSSION_INSTRUMENT_ROW;
   }
   
 /**
  * Reset the pattern counter in preparation for loading file.
  * Note for the future: Not resetting is a way to add more patterns for a file to an existing set.
  */
 
 public void resetPatterns()
 {
   for( int col = FIRST_PATTERN_COLUMN; col <= lastPatternColumn; col++ )
     {
     initializePatternColumn(col);
     }
   
   rowNames = getRowHeaders();
 }
    
 /**
  * Ensure there is space when loading patterns from file.
  */
 
 public void ensurePatternSpace()
 {
   lastPatternColumn++;
   if( lastPatternColumn >= getColumnCount() )
   {
     addColumn(PATTERN + lastPatternColumn);
   }
 }
 
/**
 * Sets for various row positions in a specified column
 * @param column 
 */

public void setBassPatternWeight(float weight, int column)
 {
 setValueAt(weight, BASS_PATTERN_WEIGHT_ROW, column);
 }

public void setBassPatternBeats(double beats, int column)
 {
 setValueAt(beats, BASS_PATTERN_BEATS_ROW, column);
  }

public void setChordPatternWeight(float weight, int column)
 {
 setValueAt(weight, CHORD_PATTERN_WEIGHT_ROW, column);
 }

public void setChordPatternPush(String push, int column)
 {
 setValueAt(push, CHORD_PATTERN_PUSH_ROW, column);
 }

public void setChordPatternBeats(double beats, int column)
 {
 setValueAt(beats, CHORD_PATTERN_BEATS_ROW, column);
  }

public void setDrumPatternWeight(float weight, int column)
 {
 setValueAt(weight, DRUM_PATTERN_WEIGHT_ROW, column);
 }

public void setDrumPatternBeats(double beats, int column)
 {
 setValueAt(beats, DRUM_PATTERN_BEATS_ROW, column);
 }

public void setDrumPatternName(String name, int column)
{
    setValueAt(name, DRUM_PATTERN_NAME_ROW, column);
}

public ArrayList<Long> getInstrumentNumbers()
  {
    return instrumentNumbers;
  }

}
