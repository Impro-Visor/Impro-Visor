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

import imp.data.stylePatterns.Playable;
import imp.data.stylePatterns.BassPatternElement;
import imp.util.ErrorLog;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author  Robert Keller, Sayuri Soejima
 * June 2008
 */
public class PianoRoll extends JFrame
  {
String DEFAULT_DURATION_STRING = "8";

public static final double BEAT_DOUBLE = 120.;

public static final int MIN_PIXELS_PER_BEAT = 30;

public static final int MAX_PIXELS_PER_BEAT = 120;

public static final int MIN_TICKS_PER_BEAT = 1;

public static final int MAX_TICKS_PER_BEAT = 120;

public static final int WINDOWHEIGHT = 730;

public static final int WINDOWWIDTH = 1030;

public static final int PANELHEIGHT = 550;

public static final int PANELWIDTH = 770;

public static final int SCROLLMARGIN = 10;

public static final Color BGCOLOR = Color.WHITE;

public static final Color GRIDCOLOR = Color.LIGHT_GRAY; // color of the grid lines

public static int LOOP_LIMIT = 10;

int xTitleOffset = 10;
int yTitleOffset = 0;
int yTitleSpacing = 25;


public static final int BASS_CHORD_ROWS = 2;


private StyleEditor styleEditor;

  // Have to convert the RGB values to HSB values in order to get color below
  // This is the original periwinkle/blue color that was used to implement the
  // PianoRoll, although once the other colors are added, it isn't necessary.
  // It can be taken out for functionality purposes, but I'm leaving the code
  // in here for the moment because I'm pretty fond of the color it produces. -Sayuri
  // EDIT: A blue color (same shade as in the Style Editor table) can be used 
  //       to represent a bar that is "selected," once that functionality is
  //       implemented.

  public static final
          Color BARBORDERCOLOR = Color.BLACK;    // color of bar borders

 public static final
          Color BASSCOLOR = Color.ORANGE;    // color of bass bars
  
 public static final
          Color CHORDCOLOR = Color.GREEN;    // color of chord bars

 public static final
          Color DRUMSCOLOR = Color.YELLOW;   // color of drum bars

 public static final
          Color SELECTEDCOLOR = Color.getHSBColor(0.46F, 0.25F, 0.9F); // LIGHT_BLUE

 public static final
          int NUMROWS = 38;          // number of rows in the grid

 public static final
          int NUMSLOTS = 2120;        // number of slots in the grid, enough for 16 beats

 public static final
          int ROWHEIGHT = 10;        // the height of each row in grid
  
 public static final
          int ROWCUSHION = 15;       // height of the cushion between each row

 public static final
          int TICKHEIGHT = 10;        // length of the slot-division lines (top & bottom)

 public static final
          int SLOTDIVISIONS = 8;     // the default for the tick display is
                                     // to display a tick for eighth notes,
                                     // which is 120/2 = 60 slots
  
 public static final
          int SLOTSPERBEAT = 120;     // 120 slots, or a quarternote, 
                                      // is the default for one beat value
  
 public static final
          int BEATSPERROW = 16;       // the number of beats per row
  
 public static final int BASS_ROW = 0; // bass row in piano roll
 public static final int CHORD_ROW = 1; // chord row in piano roll
 public static final int FIRST_DRUM_ROW = 2; // first percussion row in piano roll
  
  
  /**
   * The JPanel derivative on which the grid is drawn.
   * Note that this is passed as an argument to the constructor of the
   * JScrollPane.
   */
  private PianoRollPanel pianoRollPanel;

  /**
   * Grid representing contents
   */
  private PianoRollGrid grid;
  
  /**
   * off-screen buffer, initialized after JFrame is opened.
   */
  private Image buffer;

  /**
   * The x and y coordinates that are recorded on mouseclicks and 
   * will be used when we want to add a new bar through the jPopUpMenu.
   */
  public int x;
  public int y;

  private int styleEditorColumn = 1;


  private PianoRollBar selectedBar = null;

  /** 
   * Creates new form BeanForm 
   */  
  public PianoRoll(StyleEditor styleEditor, int x, int y) 
    {
    this.styleEditor = styleEditor;
    grid = new PianoRollGrid(NUMROWS, NUMSLOTS, ROWHEIGHT, ROWCUSHION, this);
    pianoRollPanel = new PianoRollPanel(grid, this); // passed to JScrollPane constructor in initComponents() 
    initComponents();

    makeRowButtons(rowTitlePanel);
    
    setSize(WINDOWWIDTH, WINDOWHEIGHT);
    setLocation(x, y);
    setVisible(true);
    initBuffer(); 
    WindowRegistry.registerWindow(this);
    }
  

  /**
   * initBuffer() creates the off-screen buffer. 
   *
   * NOTE: Must call only AFTER this JFrame is open!!!
   * due to the use of createImage.
   */
  public void initBuffer()
    {
    try
    {
    buffer = createImage(slotsToPixels(1+NUMSLOTS),
                         ((1+NUMROWS)*ROWHEIGHT) + (1+NUMROWS)*ROWCUSHION);
    pianoRollPanel.setBuffer(buffer);
    pianoRollPanel.setSize(PANELWIDTH, PANELHEIGHT);
    pianoRollScrollPane.setSize(SCROLLMARGIN+PANELWIDTH, SCROLLMARGIN+PANELHEIGHT);

    }
    catch( OutOfMemoryError e )
    {
        ErrorLog.log(ErrorLog.SEVERE, "Not enough memory to create image at this resolution");
    }
    }

  /** 
   * Draw a bar on the grid, at row, col, representing numSlots slots.
   */
  public void drawBar(int row, int startSlot, int numSlots, Color barColor, Color borderColor, Color tabColor)
    {
    grid.drawBar(buffer.getGraphics(), barColor, borderColor, tabColor, row, startSlot, numSlots);
    }
  

  /** 
   * Add a bar to this PianoRoll and draw it.
   */
  public void addBar(int row, 
                     int startSlot, 
                     int numSlots, 
                     Object text, 
                     Color barColor, 
                     Color borderColor,
                     int volume,
                     boolean volumeImplied)
    {
    //System.out.println("adding bar text: " + text);

    PianoRollBar bar = new PianoRollBar(row, 
                                        startSlot, 
                                        numSlots, 
                                        text, 
                                        barColor, 
                                        borderColor,
                                        volume,
                                        volumeImplied,
                                        grid, 
                                        this);

    addBar(bar);
    }

   /**
   * Add a bar to this PianoRoll and draw it.
   */
  public void addBar(PianoRollBar bar)
    {
    pianoRollPanel.addBar(bar);
    selectBar(bar);
    }

    /**
   * Add a bar to this PianoRoll, with optional end block, and draw it.
   */
  public void addBarWithOptionalEndBlock(PianoRollBar bar)
    {
    pianoRollPanel.addBarWithOptionalEndBlock(bar);
    selectBar(bar);
    }

   /**
   * Add Endblock
   */
  public void placeEndBlock(int row, int startSlot)
    {
    pianoRollPanel.placeEndBlock(row, startSlot);
    }


  /**
   * 
   * @param slots
   * @return the number of pixels equivalent to the slots number.
   */  
  public int slotsToPixels(int slots)
    {
    int pixelsPerBeat = getPixelsPerBeat();
    return (slots * pixelsPerBeat)/SLOTSPERBEAT;
    }  
  
  /**
   * @return The number of pixels we want one beat's worth to be.
   * @throws java.lang.NumberFormatException
   */
  public int getPixelsPerBeat()
    {
    int value = intFromTextField(pixelsPerBeatTextField, MIN_PIXELS_PER_BEAT, MAX_PIXELS_PER_BEAT, MIN_PIXELS_PER_BEAT);
    return value;
    }
  
  /**
   * @return The number of ticks we'd want to dislay per beat,
   *         A.K.A. the resolution, or the number of divisions
   *         we'd have within a quarter note duration.
   * @throws java.lang.NumberFormatException
   */
  public int getTicksPerBeat() 
    {
    int value = intFromTextField(ticksPerBeatTextField, MIN_TICKS_PER_BEAT, MAX_TICKS_PER_BEAT, MIN_TICKS_PER_BEAT);
    return value;
    }

  static int intFromTextField(javax.swing.JTextField field, int low, int high, int error)
      {
        String contents = field.getText();

        try
          {
            int value = Notate.intFromString(contents);

            if (value >= low && value <= high)
              {
                return value;
              }

            field.setText("" + error);
            ErrorLog.log(ErrorLog.COMMENT,
                    "Number out of range, must be integer between " + low + " and " + high + "; using " + error);

            return error;
          }
        catch( NumberFormatException e )
          {
            field.setText("" + error);
            ErrorLog.log(ErrorLog.COMMENT,
                    "Invalid Number Format, must be integer between " + low + " and " + high+ "; using " + error);

            return error;
          }
      }

  /**
   * Blackens (enables) the right-click paste bar menu item.
   */
  public void enablePasteBar() 
    {
    pasteBar.setEnabled(true);
    }
  
  /**
   * Grays out (disables) the right-click paste bar menu item.
   */
  public void disablePasteBar() 
    {
    pasteBar.setEnabled(false);
    }
 

  /**
   * Clear the bars from the PianoRoll.
   */
  public void clearBars()
    {
    pianoRollPanel.clearBars();
    }
  
  /**
   * Get the bars from the PianoRoll.
   */
  public ArrayList<PianoRollBar> getBars()
    {
    return pianoRollPanel.getBars();  
    }
  
  /**
   * Get the bars sorted from the PianoRoll.
   */
  public ArrayList<PianoRollBar> getSortedBars()
    {
    return pianoRollPanel.getSortedBars();  
    }
  /**
   * Display the PianoRoll.
   */
  public void display()
    {
    if( pianoRollPanel == null )
      {
        return;
      }
    
    pianoRollPanel.drawAll(buffer.getGraphics());
    //makeRowButtons(rowTitlePanel);

    setRowButtonLabels(rowTitlePanel);
    setVisible(true);
    toFront();
    }

  private AbstractButton rowButton[] = new AbstractButton[NUMROWS];

  public void makeRowButtons(JPanel rowTitlePanel)
  {
     ArrayList<String> rowHeaders = styleEditor.getRowHeaders();

      makeRowPressButton(0, "Bass", BASSCOLOR, rowTitlePanel);
      makeRowPressButton(1, "Chord", CHORDCOLOR, rowTitlePanel);

      int vectorSize = rowHeaders.size();

      for (int row = 2; row < NUMROWS; row++)
      {
      String header = rowHeaders.get(row - BASS_CHORD_ROWS + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW);
      makeRowToggleButtons(row, header, DRUMSCOLOR, rowTitlePanel);
      }

  }

   public void makeRowPressButton(final int row, String instrument, Color color, JPanel rowTitlePanel)
  {
      int boxHeight = 21;
      int boxXoffset = 0;
      int boxYmargin = 2;

      int buttonHeight = 21;
      int buttonWidth = 190;
      int buttonXmargin = 14;
      int buttonYmargin = 2;

      int yPosition = yTitleOffset + row* yTitleSpacing;

      JButton thisButton = new JButton();
      rowButton[row] = thisButton;
      thisButton.setText(instrument);
      thisButton.setBackground(color);
      thisButton.setOpaque(true);
      thisButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.black, Color.black));
      thisButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      thisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playRowBtnActionPerformed(evt, row);
            }
        });
      thisButton.setBounds(buttonXmargin + xTitleOffset, buttonYmargin + yPosition, buttonWidth, buttonHeight);
      rowTitlePanel.add(thisButton);
  }

  public void makeRowToggleButtons(final int row, String instrument, Color color, JPanel rowTitlePanel)
  {
      int boxHeight = 21;
      int boxXoffset = 0;
      int boxYmargin = 2;

      int buttonHeight = 21;
      int buttonWidth = 190;
      int buttonXmargin = 14;
      int buttonYmargin = 2;
      
      int yPosition = yTitleOffset + row* yTitleSpacing;
      
      JToggleButton thisButton = new JToggleButton();
      rowButton[row] = thisButton;
      thisButton.setText(instrument);
      thisButton.setBackground(color);
      thisButton.setOpaque(true);
      thisButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.black, Color.black));
      thisButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      thisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playRowBtnLoopActionPerformed(evt, row);
            }
        });
      thisButton.setBounds(buttonXmargin + xTitleOffset, buttonYmargin + yPosition, buttonWidth, buttonHeight);
      rowTitlePanel.add(thisButton);
  }


  public void setRowButtonLabels(JPanel rowTitlePanel)
  {
     ArrayList<String> rowHeaders = styleEditor.getRowHeaders();

     for (int row = 2; row < NUMROWS; row++)
      {
      String header = rowHeaders.get(row - BASS_CHORD_ROWS + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW);
      rowButton[row].setText(header);
      }
  }

 
public void paint(Graphics g)
{
  super.paint(g);
  display();
}
 
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        barCreatePopupMenu = new javax.swing.JPopupMenu();
        addNewBar = new javax.swing.JMenuItem();
        pasteBar = new javax.swing.JMenuItem();
        noAction = new javax.swing.JMenuItem();
        barEditPopupMenu = new javax.swing.JPopupMenu();
        copyBar = new javax.swing.JMenuItem();
        cutBar = new javax.swing.JMenuItem();
        deleteBar = new javax.swing.JMenuItem();
        barEditorFrame = new javax.swing.JFrame();
        barEditorContents = new javax.swing.JTextField();
        editLabel = new javax.swing.JLabel();
        okEditBtn = new javax.swing.JButton();
        cancelEditButton = new javax.swing.JButton();
        editErrorLabel = new javax.swing.JLabel();
        noteTypePanel = new javax.swing.JPanel();
        bassNoteButton = new javax.swing.JRadioButton();
        repeatPitchButton = new javax.swing.JRadioButton();
        chordToneButton = new javax.swing.JRadioButton();
        scaleToneButton = new javax.swing.JRadioButton();
        approachToneButton = new javax.swing.JRadioButton();
        nextToneButton = new javax.swing.JRadioButton();
        pitchPanel = new javax.swing.JPanel();
        degreePanel = new javax.swing.JPanel();
        pitch1Button = new javax.swing.JRadioButton();
        pitch2Button = new javax.swing.JRadioButton();
        pitch3Button = new javax.swing.JRadioButton();
        pitch4Button = new javax.swing.JRadioButton();
        pitch5Button = new javax.swing.JRadioButton();
        pitch6Button = new javax.swing.JRadioButton();
        pitch7Button = new javax.swing.JRadioButton();
        accidentalPanel = new javax.swing.JPanel();
        noAccidental = new javax.swing.JRadioButton();
        sharpAccidental = new javax.swing.JRadioButton();
        flatAccidental = new javax.swing.JRadioButton();
        directionPanel = new javax.swing.JPanel();
        upDirection = new javax.swing.JRadioButton();
        noDirection = new javax.swing.JRadioButton();
        downDirection = new javax.swing.JRadioButton();
        pitchToneButton = new javax.swing.JRadioButton();
        slotsLabel = new javax.swing.JLabel();
        slotsTextField = new javax.swing.JTextField();
        beatsLabel = new javax.swing.JLabel();
        beatsTextField = new javax.swing.JTextField();
        noteTypeButtonGroup = new javax.swing.ButtonGroup();
        scalePitchButtonGroup = new javax.swing.ButtonGroup();
        accidentalButtonGroup = new javax.swing.ButtonGroup();
        directionButtonGroup = new javax.swing.ButtonGroup();
        barVolumeDialog = new javax.swing.JDialog();
        bassEditorToggleButton1 = new javax.swing.JToggleButton();
        cautionLabelForStylePatterns = new javax.swing.JLabel();
        rowTitlePanel = new javax.swing.JPanel();
        pianoRollScrollPane = new JScrollPane(pianoRollPanel);
        barVolumePanel = new javax.swing.JPanel();
        barVolumeTF = new javax.swing.JTextField();
        barVolumeSlider = new javax.swing.JSlider();
        barVolumeImpliedCheckBox = new javax.swing.JCheckBox();
        pianoRollResolutionsPanel = new javax.swing.JPanel();
        bpmLabel = new javax.swing.JLabel();
        tempoComboBox = new javax.swing.JComboBox();
        pixelsPerBeatLabel = new javax.swing.JLabel();
        pixelsPerBeatTextField = new javax.swing.JTextField();
        pixelsPerBeatComboBox = new javax.swing.JComboBox();
        ticksPerBeatLabel = new javax.swing.JLabel();
        ticksPerBeatTextField = new javax.swing.JTextField();
        ticksPerBeatComboBox = new javax.swing.JComboBox();
        importExportPanel = new javax.swing.JPanel();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        importFromColumnTF = new javax.swing.JTextField();
        exportToColumnTF = new javax.swing.JTextField();
        importFromColumnComboBox = new javax.swing.JComboBox();
        exportToColumnComboBox = new javax.swing.JComboBox();
        fileStepBackBtn = new javax.swing.JButton();
        fileStepForwardBtn = new javax.swing.JButton();
        playPanel = new javax.swing.JPanel();
        playBassButton = new javax.swing.JButton();
        playChordButton = new javax.swing.JButton();
        playPercussionButton = new javax.swing.JButton();
        loopToggleButton = new javax.swing.JToggleButton();
        SaveEntireStyleButton = new javax.swing.JToggleButton();
        pianoRollMenuBar = new javax.swing.JMenuBar();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        addNewBar.setText("Add a new bar");
        addNewBar.setToolTipText("Add a new bar by clicking");
        addNewBar.setMinimumSize(new java.awt.Dimension(200, 100));
        addNewBar.setSelected(true);
        addNewBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewBarActionPerformed(evt);
            }
        });
        barCreatePopupMenu.add(addNewBar);

        pasteBar.setText("Paste bar");
        pasteBar.setToolTipText("Paste bar");
        pasteBar.setEnabled(false);
        pasteBar.setMinimumSize(new java.awt.Dimension(200, 100));
        pasteBar.setSelected(true);
        pasteBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteBarActionPerformed(evt);
            }
        });
        barCreatePopupMenu.add(pasteBar);

        noAction.setText("Release");
        noAction.setToolTipText("");
        noAction.setEnabled(false);
        noAction.setMinimumSize(new java.awt.Dimension(200, 100));
        noAction.setSelected(true);
        barCreatePopupMenu.add(noAction);

        copyBar.setText("Copy bar");
        copyBar.setToolTipText("Copy bar");
        copyBar.setMinimumSize(new java.awt.Dimension(200, 100));
        copyBar.setSelected(true);
        copyBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBarActionPerformed(evt);
            }
        });
        barEditPopupMenu.add(copyBar);

        cutBar.setText("Cut bar");
        cutBar.setToolTipText("Cut bar");
        cutBar.setMinimumSize(new java.awt.Dimension(200, 100));
        cutBar.setSelected(true);
        cutBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutBarActionPerformed(evt);
            }
        });
        barEditPopupMenu.add(cutBar);

        deleteBar.setText("Delete bar");
        deleteBar.setToolTipText("Delete this bar");
        deleteBar.setMinimumSize(new java.awt.Dimension(200, 100));
        deleteBar.setSelected(true);
        deleteBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBarActionPerformed(evt);
            }
        });
        barEditPopupMenu.add(deleteBar);

        barEditorFrame.setTitle("Bass Bar Editor");
        barEditorFrame.setAlwaysOnTop(true);
        barEditorFrame.setName("barEditorFrame"); // NOI18N
        barEditorFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                barEditorFrameComponentHidden(evt);
            }
        });
        barEditorFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barEditorFrameadviceFocusGained(evt);
            }
        });
        barEditorFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                barEditorFrameadviceWindowClosing(evt);
            }
        });
        barEditorFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        barEditorContents.setEditable(false);
        barEditorContents.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        barEditorContents.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        barEditorContents.setToolTipText("Bar contents (in the Style language)");
        barEditorContents.setMinimumSize(new java.awt.Dimension(200, 29));
        barEditorContents.setPreferredSize(new java.awt.Dimension(200, 29));
        barEditorContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barEditorContentsActionPerformed(evt);
            }
        });
        barEditorContents.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                barEditorContentsKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        barEditorFrame.getContentPane().add(barEditorContents, gridBagConstraints);

        editLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        editLabel.setText("Symbolic content:");
        editLabel.setMaximumSize(new java.awt.Dimension(115, 16));
        editLabel.setMinimumSize(new java.awt.Dimension(115, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        barEditorFrame.getContentPane().add(editLabel, gridBagConstraints);

        okEditBtn.setText("Set");
        okEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okEditBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        barEditorFrame.getContentPane().add(okEditBtn, gridBagConstraints);

        cancelEditButton.setText("Close");
        cancelEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        barEditorFrame.getContentPane().add(cancelEditButton, gridBagConstraints);

        editErrorLabel.setForeground(new java.awt.Color(255, 0, 51));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        barEditorFrame.getContentPane().add(editErrorLabel, gridBagConstraints);

        noteTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass note category"));
        noteTypePanel.setMinimumSize(new java.awt.Dimension(150, 385));
        noteTypePanel.setPreferredSize(new java.awt.Dimension(150, 385));
        noteTypePanel.setLayout(new java.awt.GridBagLayout());

        noteTypeButtonGroup.add(bassNoteButton);
        bassNoteButton.setSelected(true);
        bassNoteButton.setText("Bass");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(bassNoteButton, gridBagConstraints);

        noteTypeButtonGroup.add(repeatPitchButton);
        repeatPitchButton.setText("Repeat Pitch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(repeatPitchButton, gridBagConstraints);

        noteTypeButtonGroup.add(chordToneButton);
        chordToneButton.setText("Chord tone");
        chordToneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordToneButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(chordToneButton, gridBagConstraints);

        noteTypeButtonGroup.add(scaleToneButton);
        scaleToneButton.setText("Scale tone");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(scaleToneButton, gridBagConstraints);

        noteTypeButtonGroup.add(approachToneButton);
        approachToneButton.setText("Approach tone");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(approachToneButton, gridBagConstraints);

        noteTypeButtonGroup.add(nextToneButton);
        nextToneButton.setText("Next measure");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(nextToneButton, gridBagConstraints);

        pitchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pitchPanel.setMinimumSize(new java.awt.Dimension(240, 220));
        pitchPanel.setPreferredSize(new java.awt.Dimension(240, 220));
        pitchPanel.setLayout(new java.awt.GridBagLayout());

        degreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Degree"));
        degreePanel.setMinimumSize(new java.awt.Dimension(60, 190));
        degreePanel.setPreferredSize(new java.awt.Dimension(60, 190));
        degreePanel.setLayout(new java.awt.GridBagLayout());

        scalePitchButtonGroup.add(pitch1Button);
        pitch1Button.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch1Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch2Button);
        pitch2Button.setText("2");
        pitch2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pitch2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch2Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch3Button);
        pitch3Button.setText("3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch3Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch4Button);
        pitch4Button.setText("4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch4Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch5Button);
        pitch5Button.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch5Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch6Button);
        pitch6Button.setText("6");
        pitch6Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pitch6ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch6Button, gridBagConstraints);

        scalePitchButtonGroup.add(pitch7Button);
        pitch7Button.setText("7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        degreePanel.add(pitch7Button, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pitchPanel.add(degreePanel, gridBagConstraints);

        accidentalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Accidental"));
        accidentalPanel.setMinimumSize(new java.awt.Dimension(80, 100));
        accidentalPanel.setPreferredSize(new java.awt.Dimension(80, 100));
        accidentalPanel.setLayout(new java.awt.GridBagLayout());

        accidentalButtonGroup.add(noAccidental);
        noAccidental.setText("none");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        accidentalPanel.add(noAccidental, gridBagConstraints);

        accidentalButtonGroup.add(sharpAccidental);
        sharpAccidental.setText("#");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        accidentalPanel.add(sharpAccidental, gridBagConstraints);

        accidentalButtonGroup.add(flatAccidental);
        flatAccidental.setText("b");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        accidentalPanel.add(flatAccidental, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pitchPanel.add(accidentalPanel, gridBagConstraints);

        directionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Direction"));
        directionPanel.setMinimumSize(new java.awt.Dimension(90, 100));
        directionPanel.setPreferredSize(new java.awt.Dimension(90, 100));
        directionPanel.setLayout(new java.awt.GridBagLayout());

        directionButtonGroup.add(upDirection);
        upDirection.setText("Up");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        directionPanel.add(upDirection, gridBagConstraints);

        directionButtonGroup.add(noDirection);
        noDirection.setText("Any");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        directionPanel.add(noDirection, gridBagConstraints);

        directionButtonGroup.add(downDirection);
        downDirection.setText("Down");
        downDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downDirectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        directionPanel.add(downDirection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pitchPanel.add(directionPanel, gridBagConstraints);

        noteTypeButtonGroup.add(pitchToneButton);
        pitchToneButton.setText("Pitch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        pitchPanel.add(pitchToneButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        noteTypePanel.add(pitchPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        barEditorFrame.getContentPane().add(noteTypePanel, gridBagConstraints);

        slotsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        slotsLabel.setText("Slots:");
        slotsLabel.setMaximumSize(new java.awt.Dimension(50, 16));
        slotsLabel.setMinimumSize(new java.awt.Dimension(50, 16));
        slotsLabel.setPreferredSize(new java.awt.Dimension(50, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        barEditorFrame.getContentPane().add(slotsLabel, gridBagConstraints);

        slotsTextField.setEditable(false);
        slotsTextField.setMinimumSize(new java.awt.Dimension(75, 29));
        slotsTextField.setPreferredSize(new java.awt.Dimension(75, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        barEditorFrame.getContentPane().add(slotsTextField, gridBagConstraints);

        beatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        beatsLabel.setText("Beats:");
        beatsLabel.setMaximumSize(new java.awt.Dimension(115, 16));
        beatsLabel.setMinimumSize(new java.awt.Dimension(50, 16));
        beatsLabel.setPreferredSize(new java.awt.Dimension(50, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        barEditorFrame.getContentPane().add(beatsLabel, gridBagConstraints);

        beatsTextField.setEditable(false);
        beatsTextField.setMinimumSize(new java.awt.Dimension(150, 29));
        beatsTextField.setPreferredSize(new java.awt.Dimension(150, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        barEditorFrame.getContentPane().add(beatsTextField, gridBagConstraints);

        barVolumeDialog.setBounds(new java.awt.Rectangle(0, 22, 30, 200));
        barVolumeDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Piano Roll Style Pattern Editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                closePianoRollWindow(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        bassEditorToggleButton1.setBackground(BASSCOLOR);
        bassEditorToggleButton1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        bassEditorToggleButton1.setText("Open Bass Bar Editor");
        bassEditorToggleButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bassEditorToggleButton1.setMaximumSize(new java.awt.Dimension(140, 20));
        bassEditorToggleButton1.setMinimumSize(new java.awt.Dimension(140, 20));
        bassEditorToggleButton1.setOpaque(true);
        bassEditorToggleButton1.setPreferredSize(new java.awt.Dimension(140, 20));
        bassEditorToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassEditorToggleButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(bassEditorToggleButton1, gridBagConstraints);

        cautionLabelForStylePatterns.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        cautionLabelForStylePatterns.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cautionLabelForStylePatterns.setText("  Long vertical lines are beats.   Bass, Chord, and Percussion sections are independent, not linked together.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(cautionLabelForStylePatterns, gridBagConstraints);

        rowTitlePanel.setMaximumSize(new java.awt.Dimension(200, 32767));
        rowTitlePanel.setMinimumSize(new java.awt.Dimension(200, 550));
        rowTitlePanel.setPreferredSize(new java.awt.Dimension(200, 550));
        rowTitlePanel.setLayout(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(rowTitlePanel, gridBagConstraints);

        pianoRollScrollPane.setToolTipText("Each bar represents an interval for which the instrument is played.");
        pianoRollScrollPane.setViewportBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pianoRollScrollPane.setInheritsPopupMenu(true);
        pianoRollScrollPane.setMinimumSize(new java.awt.Dimension(680, 580));
        pianoRollScrollPane.setPreferredSize(new java.awt.Dimension(680, 580));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pianoRollScrollPane, gridBagConstraints);

        barVolumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bar Volume (MIDI Velocity)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        barVolumePanel.setLayout(new java.awt.GridBagLayout());

        barVolumeTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        barVolumeTF.setText("127");
        barVolumeTF.setToolTipText("The volume is a MIDI \"velocity\", in the range 0-127.");
        barVolumeTF.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        barVolumeTF.setMinimumSize(new java.awt.Dimension(60, 28));
        barVolumeTF.setPreferredSize(new java.awt.Dimension(60, 28));
        barVolumeTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barVolumeTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        barVolumePanel.add(barVolumeTF, gridBagConstraints);

        barVolumeSlider.setMajorTickSpacing(10);
        barVolumeSlider.setMaximum(127);
        barVolumeSlider.setPaintTicks(true);
        barVolumeSlider.setToolTipText("Set the volume of the selected bar.");
        barVolumeSlider.setMaximumSize(new java.awt.Dimension(500, 29));
        barVolumeSlider.setMinimumSize(new java.awt.Dimension(500, 29));
        barVolumeSlider.setPreferredSize(new java.awt.Dimension(500, 29));
        barVolumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                barVolumeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.6;
        barVolumePanel.add(barVolumeSlider, gridBagConstraints);

        barVolumeImpliedCheckBox.setSelected(true);
        barVolumeImpliedCheckBox.setText("Implied");
        barVolumeImpliedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barVolumeImpliedCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        barVolumePanel.add(barVolumeImpliedCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(barVolumePanel, gridBagConstraints);

        pianoRollResolutionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grid Resolutions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pianoRollResolutionsPanel.setMinimumSize(new java.awt.Dimension(350, 109));
        pianoRollResolutionsPanel.setPreferredSize(new java.awt.Dimension(400, 109));
        pianoRollResolutionsPanel.setLayout(new java.awt.GridBagLayout());

        bpmLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bpmLabel.setText("Editor Tempo (Beats per Minute)");
        bpmLabel.setToolTipText("Tempo in Beats Per Minute");
        bpmLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pianoRollResolutionsPanel.add(bpmLabel, gridBagConstraints);

        tempoComboBox.setMaximumRowCount(30);
        tempoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30", "40", "50", "60", "70", "80", "90", "100", "120", "130", "140", "150", "160", "170", "180", "190", "200", "210", "220", "230", "240", "250", "260", "270", "280" }));
        tempoComboBox.setSelectedIndex(9);
        tempoComboBox.setToolTipText("Change tempo for the style editor.");
        tempoComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
        tempoComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
        tempoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempoComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pianoRollResolutionsPanel.add(tempoComboBox, gridBagConstraints);

        pixelsPerBeatLabel.setText("Visual (30-120 pixels per beat)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pianoRollResolutionsPanel.add(pixelsPerBeatLabel, gridBagConstraints);

        pixelsPerBeatTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pixelsPerBeatTextField.setText("120");
        pixelsPerBeatTextField.setMinimumSize(new java.awt.Dimension(40, 19));
        pixelsPerBeatTextField.setPreferredSize(new java.awt.Dimension(40, 19));
        pixelsPerBeatTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pixelsPerBeatTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        pianoRollResolutionsPanel.add(pixelsPerBeatTextField, gridBagConstraints);

        pixelsPerBeatComboBox.setMaximumRowCount(4);
        pixelsPerBeatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "120", "90", "60" }));
        pixelsPerBeatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pixelsPerBeatComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pianoRollResolutionsPanel.add(pixelsPerBeatComboBox, gridBagConstraints);

        ticksPerBeatLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ticksPerBeatLabel.setText("Time (1-120 tick marks per beat)");
        ticksPerBeatLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pianoRollResolutionsPanel.add(ticksPerBeatLabel, gridBagConstraints);

        ticksPerBeatTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ticksPerBeatTextField.setText("8");
        ticksPerBeatTextField.setMinimumSize(new java.awt.Dimension(40, 19));
        ticksPerBeatTextField.setPreferredSize(new java.awt.Dimension(40, 19));
        ticksPerBeatTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticksPerBeatTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 3, 0, 0);
        pianoRollResolutionsPanel.add(ticksPerBeatTextField, gridBagConstraints);

        ticksPerBeatComboBox.setMaximumRowCount(20);
        ticksPerBeatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "120", "60", "40", "30", "20", "15", "12", "9", "8", "7", "6", "5", "4", "3", "2", "1" }));
        ticksPerBeatComboBox.setSelectedIndex(8);
        ticksPerBeatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticksPerBeatComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pianoRollResolutionsPanel.add(ticksPerBeatComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(pianoRollResolutionsPanel, gridBagConstraints);

        importExportPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Import/Export From/To Style Editor  ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        importExportPanel.setMinimumSize(new java.awt.Dimension(400, 82));
        importExportPanel.setPreferredSize(new java.awt.Dimension(333, 82));
        importExportPanel.setLayout(new java.awt.GridBagLayout());

        importButton.setText("From Style Editor Column");
        importButton.setDefaultCapable(false);
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        importExportPanel.add(importButton, gridBagConstraints);

        exportButton.setText("To Style Editor Column ");
        exportButton.setActionCommand("From Piano Roll to Style Editor Column\n");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        importExportPanel.add(exportButton, gridBagConstraints);

        importFromColumnTF.setText("1");
        importFromColumnTF.setToolTipText("Style editor column number from which pattern is imported to pianoroll.");
        importFromColumnTF.setMinimumSize(new java.awt.Dimension(50, 28));
        importFromColumnTF.setPreferredSize(new java.awt.Dimension(50, 28));
        importFromColumnTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFromColumnTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.1;
        importExportPanel.add(importFromColumnTF, gridBagConstraints);

        exportToColumnTF.setText("1");
        exportToColumnTF.setToolTipText("Style editor column number to which pattern is exported from pianoroll.");
        exportToColumnTF.setMinimumSize(new java.awt.Dimension(50, 28));
        exportToColumnTF.setPreferredSize(new java.awt.Dimension(50, 28));
        exportToColumnTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToColumnTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        importExportPanel.add(exportToColumnTF, gridBagConstraints);

        importFromColumnComboBox.setMaximumRowCount(30);
        importFromColumnComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", " " }));
        importFromColumnComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFromColumnComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.2;
        importExportPanel.add(importFromColumnComboBox, gridBagConstraints);

        exportToColumnComboBox.setMaximumRowCount(30);
        exportToColumnComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", " " }));
        exportToColumnComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToColumnComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        importExportPanel.add(exportToColumnComboBox, gridBagConstraints);

        fileStepBackBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperBack.png"))); // NOI18N
        fileStepBackBtn.setToolTipText("Import the previous column of the Style Editor.");
        fileStepBackBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepBackBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepBackBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        importExportPanel.add(fileStepBackBtn, gridBagConstraints);

        fileStepForwardBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperFront.png"))); // NOI18N
        fileStepForwardBtn.setToolTipText("Import the next column of the Style Editor.");
        fileStepForwardBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepForwardBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pianoRollButtonStepForwardBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        importExportPanel.add(fileStepForwardBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        getContentPane().add(importExportPanel, gridBagConstraints);

        playPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Play Pattern as Saved", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        playPanel.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        playPanel.setMinimumSize(new java.awt.Dimension(200, 82));
        playPanel.setPreferredSize(new java.awt.Dimension(200, 82));
        playPanel.setLayout(new java.awt.GridBagLayout());

        playBassButton.setBackground(BASSCOLOR);
        playBassButton.setText("Bass\n");
        playBassButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playBassButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        playBassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBassButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        playPanel.add(playBassButton, gridBagConstraints);

        playChordButton.setBackground(CHORDCOLOR);
        playChordButton.setText("Chord\n");
        playChordButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playChordButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        playChordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playChordButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        playPanel.add(playChordButton, gridBagConstraints);

        playPercussionButton.setBackground(DRUMSCOLOR);
        playPercussionButton.setText("Percussion");
        playPercussionButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playPercussionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        playPercussionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPercussionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        playPanel.add(playPercussionButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(playPanel, gridBagConstraints);

        loopToggleButton.setBackground(DRUMSCOLOR);
        loopToggleButton.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        loopToggleButton.setText("Loop Selected Percussion");
        loopToggleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loopToggleButton.setMaximumSize(new java.awt.Dimension(200, 20));
        loopToggleButton.setMinimumSize(new java.awt.Dimension(200, 20));
        loopToggleButton.setOpaque(true);
        loopToggleButton.setPreferredSize(new java.awt.Dimension(200, 20));
        loopToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(loopToggleButton, gridBagConstraints);

        SaveEntireStyleButton.setBackground(new java.awt.Color(255, 51, 51));
        SaveEntireStyleButton.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        SaveEntireStyleButton.setText("Save Entire Style");
        SaveEntireStyleButton.setToolTipText("Saves this Column and the entire style to the file.");
        SaveEntireStyleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SaveEntireStyleButton.setMaximumSize(new java.awt.Dimension(200, 20));
        SaveEntireStyleButton.setMinimumSize(new java.awt.Dimension(200, 20));
        SaveEntireStyleButton.setOpaque(true);
        SaveEntireStyleButton.setPreferredSize(new java.awt.Dimension(200, 20));
        SaveEntireStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveEntireStyleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(SaveEntireStyleButton, gridBagConstraints);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        pianoRollMenuBar.add(windowMenu);

        setJMenuBar(pianoRollMenuBar);
    }// </editor-fold>//GEN-END:initComponents

/**
 * When stuff is changed in the jTextField, we need to update the window
 * to reflect the changes in the tick divisions.
 * @param evt
 */
private void pixelsPerBeatTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixelsPerBeatTextFieldActionPerformed
    initBuffer();
    pianoRollPanel.drawAll(buffer.getGraphics());
    repaint();
}//GEN-LAST:event_pixelsPerBeatTextFieldActionPerformed

/**
 * When stuff is changed in the jTextField, we need to update the window
 * to reflect the changes in the tick divisions.
 * @param evt
 */
private void ticksPerBeatTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ticksPerBeatTextFieldActionPerformed
    initBuffer();
    pianoRollPanel.drawAll(buffer.getGraphics());
    repaint();
}//GEN-LAST:event_ticksPerBeatTextFieldActionPerformed

/**
 * Add a new bar to the row at the tick right before where the user specified.
 * @param evt
 */
private void addNewBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewBarActionPerformed
    // Convert the x and y coordinates (in pixels) into row and column numbers
    // so that we can add the bars at the appropriate locations.
    int row = y / (ROWHEIGHT + ROWCUSHION);
    
    Color barColor;
    Color borderColor = BARBORDERCOLOR;
    
    if(row == 0)
      barColor = BASSCOLOR;
    else if(row == 1)
      barColor = CHORDCOLOR;    
    else
      barColor = DRUMSCOLOR;
    
    // Define where the bar should start
    int clickedSlot = (x * SLOTSPERBEAT) / getPixelsPerBeat();
    int oneTick = SLOTSPERBEAT / getTicksPerBeat();
    //round it off so that it snaps to the tick before where the click was
    int startSlot = pianoRollPanel.nearestTick(clickedSlot, oneTick);    
    
    // Define the size of the bar, or where it ends
    ArrayList<PianoRollBar> bars = pianoRollPanel.getBars();
    int barWidth; // in slots
    int maxWidth = SLOTSPERBEAT/2; // the default value, in slots
    for(int i = 0; i < bars.size(); ++i)
      {
      PianoRollBar bar = bars.get(i);   // go through all the bars
      if(bar.row == row)                      // in the row we want to add into
        {
        barWidth = bar.startSlot - startSlot; // distance to the next bar (to the right)
        // define how wide we want to make the bar, depending on the next bar's place
        if((barWidth < maxWidth) && (barWidth > 0)) 
            maxWidth = barWidth;
        }
      }

    // Actually make the bar with the width and starting positions defined above
    PianoRollBar newBar = 
        barColor == BASSCOLOR ? //BassNoteType noteType, int degree, AccidentalType accidental, String durationString, DirectionType direction
            new PianoRollBassBar(startSlot, new BassPatternElement(DEFAULT_DURATION_STRING), 127, true, this)
          : new PianoRollBar(row, startSlot, maxWidth, barColor, borderColor, 127, true, grid, this);
    
    if(!pianoRollPanel.collides(newBar))  // if an added bar wouldn't collide 
      {                                   // with existing bars        
      addBarWithOptionalEndBlock(newBar);
      }
}//GEN-LAST:event_addNewBarActionPerformed

private void selectBar()
{
PianoRollBar bar = pianoRollPanel.findBar(x, y);
selectBar(bar);
}

public void selectBar(PianoRollBar bar)
{
if( bar != null )
   {
   if( selectedBar != null )
     {
     selectedBar.setSelected(false);
     }
   selectedBar = bar;
   bar.setSelected(true);
   updateBarEditor(selectedBar);
   
   boolean volumeImplied = bar.getVolumeImplied();
   barVolumeImpliedCheckBox.setSelected(volumeImplied);
   barVolumeSlider.setEnabled(!volumeImplied);
   barVolumeTF.setEnabled(!volumeImplied);
   int imputedVolume = pianoRollPanel.getImputedVolume(selectedBar); 
   setVolumeIndicators(imputedVolume);
   pianoRollPanel.drawAll(buffer.getGraphics());
   }
}


/**
 * Delete the bar at which this action was called.
 * @param evt
 */
private void deleteBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBarActionPerformed
    // Remove the bar from the vector which stores
    // the information for the bars.
    PianoRollBar bar = pianoRollPanel.findBar(x, y);
    pianoRollPanel.removeBar(bar);
    
    // Redraw the screen to reflect the change in the vector.
    pianoRollPanel.drawAll(buffer.getGraphics());
}//GEN-LAST:event_deleteBarActionPerformed

/**
 * Copies the bar at the position where the action was called.
 * @param evt
 */
private void copyBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBarActionPerformed
    PianoRollBar bar = pianoRollPanel.findBar(x, y);
    pianoRollPanel.addTempBar(bar);
    selectBar(bar);
}//GEN-LAST:event_copyBarActionPerformed

/**
 * Cuts the bar at the position where the action was called.
 * @param evt
 */
private void cutBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutBarActionPerformed
    PianoRollBar bar = pianoRollPanel.findBar(x, y);
    pianoRollPanel.addTempBar(bar);
    pianoRollPanel.removeBar(bar);
    
    // Redraw the screen to reflect the change in the bars vector.
    pianoRollPanel.drawAll(buffer.getGraphics());      
}//GEN-LAST:event_cutBarActionPerformed

private void barEditorCommit()
{
    if( selectedBar != null && selectedBar instanceof PianoRollBassBar )
    {
        PianoRollBassBar bar = (PianoRollBassBar)selectedBar;

        bar.setBassParameters(
              pitchToneButton.isSelected()    ? BassPatternElement.BassNoteType.PITCH
            : repeatPitchButton.isSelected()  ? BassPatternElement.BassNoteType.REPEAT
            : chordToneButton.isSelected()    ? BassPatternElement.BassNoteType.CHORD
            : scaleToneButton.isSelected()    ? BassPatternElement.BassNoteType.SCALE
            : approachToneButton.isSelected() ? BassPatternElement.BassNoteType.APPROACH
            : nextToneButton.isSelected()     ? BassPatternElement.BassNoteType.NEXT
            : BassPatternElement.BassNoteType.BASS,

            bar.getNumSlots(),

              flatAccidental.isSelected() ? BassPatternElement.AccidentalType.FLAT
            : sharpAccidental.isSelected() ? BassPatternElement.AccidentalType.SHARP
            : BassPatternElement.AccidentalType.NONE,

              pitch2Button.isSelected() ? 2
            : pitch3Button.isSelected() ? 3
            : pitch4Button.isSelected() ? 4
            : pitch5Button.isSelected() ? 5
            : pitch6Button.isSelected() ? 6
            : pitch7Button.isSelected() ? 7
            : 1,

              upDirection.isSelected() ? BassPatternElement.DirectionType.UP
            : downDirection.isSelected() ? BassPatternElement.DirectionType.DOWN
            : BassPatternElement.DirectionType.ANY
            );

        selectedBar.setText(barEditorContents.getText());

        selectBar(selectedBar);
    }
 }

/**
 * Pastes the last bar that was cut/copied at the location at which the
 * action was called.
 * @param evt
 */
private void pasteBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteBarActionPerformed
    PianoRollBar bar = pianoRollPanel.getLastTempBar();

    Color barColor = bar.barColor;
    Color borderColor = bar.borderColor;
    int numSlots = bar.numSlots;
    int row = y / (ROWHEIGHT + ROWCUSHION);

    // Define where the bar should start
    int clickedSlot = (x * SLOTSPERBEAT) / getPixelsPerBeat();
    int oneTick = SLOTSPERBEAT / getTicksPerBeat();
    //round it off so that it snaps to the tick before where the click was
    int startSlot = pianoRollPanel.nearestTick(clickedSlot, oneTick);

    if( bar instanceof PianoRollBassBar )
      {
        if( row == BASS_ROW )
          {
            PianoRollBassBar bassBar = (PianoRollBassBar)bar;
            addBarWithOptionalEndBlock(new PianoRollBassBar(startSlot,
                                        bassBar.getElementCopy(),
                                        bassBar.getVolume(),
                                        bassBar.getVolumeImplied(),
                                        this));
          }
      // Don't paste if row does not correspond to instrument
      }
    else
      {
        if( row != BASS_ROW )
          {
            Color color = row == CHORD_ROW ? CHORDCOLOR : DRUMSCOLOR;
            addBarWithOptionalEndBlock(new PianoRollBar(row, startSlot, numSlots, color,
                                      borderColor, bar.getVolume(), bar.getVolumeImplied(), grid, this));
          }
      // Don't paste if row does not correspond to instrument
      }
}//GEN-LAST:event_pasteBarActionPerformed

public void setColumnsInOut(int col, String styleName)
{
    setTitle("Piano-Roll Pattern Editor: Column " + col + " of " + styleName);
    setColumnIn(col);
    setColumnOut(col);
    styleEditorColumn = col;
}

public void setColumnIn(int col)
{
    importFromColumnTF.setText("" + col);
    importFromColumnComboBox.setSelectedIndex(col-1);
}

public void setColumnOut(int col)
{
  exportToColumnTF.setText("" + col);
  exportToColumnComboBox.setSelectedIndex(col-1);
}


/**
 * Call to move a column From the Piano Roll to the Style Editor
 * @param evt
 */

private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
    int minColumn = 1; // FIX
    int maxColumn = styleEditor.getNumColumns();

    int col = intFromTextField(exportToColumnTF, minColumn, maxColumn, 0); // FIX!

    // col <= 0 indicates an error

    if( col > 0 )
      {
        styleEditor.pianoRollToStyleEditorColumn(this, col);
      }
}//GEN-LAST:event_exportButtonActionPerformed



private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
  importFromStyleEditorColumn();
}//GEN-LAST:event_importButtonActionPerformed


/**
 * Call to move a column From the Style Editor to the Piano Roll
 * @param evt
 */

private void importFromStyleEditorColumn()
  {
    int minColumn = 1; // FIX
    int maxColumn = styleEditor.getNumColumns();

    int col = intFromTextField(importFromColumnTF, minColumn, maxColumn, 0); // FIX!

    // col <= 0 indicates an error

    if( col > 0 )
      {
        importFromColumnComboBox.setSelectedIndex(col - 1);
        styleEditor.styleEditorColumnToPianoRoll(col, this);
        updatePlayablePercussion();
      }    
  }

private void playPercussionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPercussionButtonActionPerformed
styleEditor.playPercussionColumn(styleEditorColumn);
}//GEN-LAST:event_playPercussionButtonActionPerformed

private void playChordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playChordButtonActionPerformed
styleEditor.playChordColumn(styleEditorColumn);
}//GEN-LAST:event_playChordButtonActionPerformed

private void playBassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBassButtonActionPerformed
styleEditor.playBassColumn(styleEditorColumn);
}//GEN-LAST:event_playBassButtonActionPerformed

private void closePianoRollWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closePianoRollWindow
    
    closeWindow();
}//GEN-LAST:event_closePianoRollWindow

private void exportToColumnTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToColumnTFActionPerformed
    exportButtonActionPerformed(null);
}//GEN-LAST:event_exportToColumnTFActionPerformed

public void openBarEditor()
{
   barEditorFrame.setLocation(100, 100);
   barEditorFrame.setSize(400, 560);
   barEditorFrame.setVisible(true);
   if( selectedBar != null )
   {
   updateBarEditor(selectedBar);
   }
}

public void updateBarEditor(PianoRollBar barBeingEdited)
{
   if( !(barBeingEdited instanceof PianoRollBassBar) )
   {
       return;
   }

   PianoRollBassBar bar = (PianoRollBassBar)barBeingEdited;

   if( bar != null )
   {
   barEditorContents.setText(bar.getText().toString());

   int slots = bar.getSlots();

   slotsTextField.setText("" + slots);

   beatsTextField.setText("" + (slots/BEAT_DOUBLE));

   //barEditorFrame.setLocation(x+100, y+100);
   //barEditorFrame.setSize(350, 500);

   switch( bar.getNoteType() )
   {
       case BASS:     bassNoteButton.setSelected(true);     break;
       case PITCH:    pitchToneButton.setSelected(true);    break;
       case REPEAT:   repeatPitchButton.setSelected(true);  break;
       case CHORD:    chordToneButton.setSelected(true);    break;
       case SCALE:    scaleToneButton.setSelected(true);    break;
       case APPROACH: approachToneButton.setSelected(true); break;
       case NEXT:     nextToneButton.setSelected(true);     break;

   }

   switch( bar.getAccidental() )
   {
       case FLAT:   flatAccidental.setSelected(true);   break;
       case NONE:   noAccidental.setSelected(true);     break;
       case SHARP:  sharpAccidental.setSelected(true);  break;
   }

   switch( bar.getDegree() )
   {
       case 1: pitch1Button.setSelected(true); break;
       case 2: pitch2Button.setSelected(true); break;
       case 3: pitch3Button.setSelected(true); break;
       case 4: pitch4Button.setSelected(true); break;
       case 5: pitch5Button.setSelected(true); break;
       case 6: pitch6Button.setSelected(true); break;
       case 7: pitch7Button.setSelected(true); break;
    }

   switch( bar.getDirection() )
   {
       case UP:   upDirection.setSelected(true); break;
       case ANY:  noDirection.setSelected(true); break;
       case DOWN: downDirection.setSelected(true); break;
   }
  }
barEditorFrame.repaint();
}

private void barEditorContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barEditorContentsActionPerformed
    barEditorCommit();
}//GEN-LAST:event_barEditorContentsActionPerformed

private void barEditorContentsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barEditorContentsKeyPressed

    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        barEditorCommit();
    }
}//GEN-LAST:event_barEditorContentsKeyPressed

private void okEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okEditBtnActionPerformed
    barEditorCommit();
}//GEN-LAST:event_okEditBtnActionPerformed

private void barEditorFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_barEditorFrameComponentHidden

    // TODO add your handling code here:
}//GEN-LAST:event_barEditorFrameComponentHidden

private void barEditorFrameadviceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barEditorFrameadviceFocusGained

    // TODO add your handling code here:
}//GEN-LAST:event_barEditorFrameadviceFocusGained

private void barEditorFrameadviceWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_barEditorFrameadviceWindowClosing
    
}//GEN-LAST:event_barEditorFrameadviceWindowClosing

private void cancelEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelEditButtonActionPerformed
    barEditorFrame.setVisible(false);
}//GEN-LAST:event_cancelEditButtonActionPerformed

private void pitch2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pitch2ButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_pitch2ButtonActionPerformed

private void pitch6ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pitch6ButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_pitch6ButtonActionPerformed

private void bassEditorToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassEditorToggleButton1ActionPerformed
openBarEditor();
}//GEN-LAST:event_bassEditorToggleButton1ActionPerformed

private void importFromColumnComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFromColumnComboBoxActionPerformed
    importFromColumnTF.setText(importFromColumnComboBox.getSelectedItem().toString());
    importFromStyleEditorColumn();
}//GEN-LAST:event_importFromColumnComboBoxActionPerformed

private void exportToColumnComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToColumnComboBoxActionPerformed
    exportToColumnTF.setText(exportToColumnComboBox.getSelectedItem().toString());
}//GEN-LAST:event_exportToColumnComboBoxActionPerformed

private void importFromColumnTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFromColumnTFActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_importFromColumnTFActionPerformed

private void chordToneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_chordToneButtonActionPerformed

private void pixelsPerBeatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixelsPerBeatComboBoxActionPerformed
pixelsPerBeatTextField.setText(pixelsPerBeatComboBox.getSelectedItem().toString());
if( checkPixelBeatConstraint() )
{
    pixelsPerBeatTextFieldActionPerformed(evt);
}
}//GEN-LAST:event_pixelsPerBeatComboBoxActionPerformed

private void ticksPerBeatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ticksPerBeatComboBoxActionPerformed
ticksPerBeatTextField.setText(ticksPerBeatComboBox.getSelectedItem().toString());
if( checkPixelBeatConstraint() )
{
    ticksPerBeatTextFieldActionPerformed(evt);
}
}//GEN-LAST:event_ticksPerBeatComboBoxActionPerformed


/**
 * This is called when a (non-toggle) button at the left end of a row is pressed.
 * It corresponds to bass and chord instruments, which don't get looped.
 * @param evt
 * @param row 
 */

private void playRowBtnActionPerformed(java.awt.event.ActionEvent evt, int row)
  {
    setLooping(false);
    
    Playable playable = styleEditor.getPlayableFromPianoRollRow(this, row);

    AbstractButton thisButton = rowButton[row];

    updatePlayablePercussion();
    
    if( playable != null )
      {
      playable.playMe();
      }

  }


/**
 * This is called when a toggle button at the left end of a row is toggled.
 * It corresponds to  percussion instruments, and starts looping them.
 * @param evt
 * @param row 
 */

private void playRowBtnLoopActionPerformed(java.awt.event.ActionEvent evt, int row)
  {
    Playable playable = styleEditor.getPlayableFromPianoRollRow(this, row);

    AbstractButton thisButton = rowButton[row];

    if( thisButton.isSelected() )
      {
        setLooping(true);
        thisButton.setBackground(SELECTEDCOLOR);
      }
    else
      {
        // Don't stop looping in this case, because other drums may still be on
        thisButton.setBackground(DRUMSCOLOR);
      }

    updatePlayablePercussion();
  }


/**
 * Sets selected percussion instruments to loop or not.
 * @param value 
 */

public void setLooping(boolean value)
  {
    if( value )
      {
        loopToggleButton.setBackground(SELECTEDCOLOR);
        loopToggleButton.setText("<html><center>Stop Looping</center></html>");
        loopToggleButton.setSelected(true);
        styleEditor.setLooping(true);
        startPlaying();
      }
    else
      {
        loopToggleButton.setBackground(DRUMSCOLOR);
        loopToggleButton.setText("<html><center>Loop Percussion</center></html>");
        loopToggleButton.setSelected(false);
        styleEditor.setLooping(false);
        stopPlaying();
      }   
  }

/**
 * This is for communication with StyleEditor, so that it can determine whether
 * or not the pianoroll is looping.
 * @return 
 */
public boolean getLooping()
  {
    return loopToggleButton.isSelected();
  }


Playable nowPlaying = null;


/**
 * Update nowPlaying to include the instruments defined by selected buttons
 * in array rowButton.
 * 
 * If looping, i.e. already playing, stop playing in order to update.,
 * then restart following the update.
 */

public void updatePlayablePercussion()
  {
  //System.out.println("updatePlayablePercussion");
    
    if( loopToggleButton.isSelected() )
      {
      stopPlaying();
      
      nowPlaying = getPlayablePercussion();
      
      startPlaying();
      }
    else
      {
       nowPlaying = getPlayablePercussion();       
      }
  }

private Playable getPlayablePercussion()
  {
    return styleEditor.getPlayablePercussionFromPianoRoll(this, rowButton);
  }

public void stopPlaying()
  {
    //System.out.println("stopPlaying: " + nowPlaying);
    if( nowPlaying != null )
      {
        nowPlaying.stopPlaying();
      }
  }

public void startPlaying()
  {
    if( nowPlaying != null )
      {
        nowPlaying.playMe();
      }
  }

public boolean nowPlaying()
  {
    return nowPlaying != null;
  }

private void tempoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempoComboBoxActionPerformed
styleEditor.setTempo((String)tempoComboBox.getSelectedItem());
}//GEN-LAST:event_tempoComboBoxActionPerformed

private void loopToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToggleButtonActionPerformed
// Note that the entire array of rowButton is passed, so that the combination
// can be discerened.

    setLooping(loopToggleButton.isSelected());

}//GEN-LAST:event_loopToggleButtonActionPerformed

private void downDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downDirectionActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_downDirectionActionPerformed

private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMIActionPerformed
    
    closeWindow();
}//GEN-LAST:event_closeWindowMIActionPerformed

private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMIActionPerformed
    
    WindowRegistry.cascadeWindows(this);
}//GEN-LAST:event_cascadeMIActionPerformed

private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected
    
    windowMenu.removeAll();
    
    windowMenu.add(closeWindowMI);
    
    windowMenu.add(cascadeMI);
    
    windowMenu.add(windowMenuSeparator);
    
    for(WindowMenuItem w : WindowRegistry.getWindows()) {
        
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
        
    }
    
    windowMenu.repaint();
}//GEN-LAST:event_windowMenuMenuSelected

private void barVolumeTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_barVolumeTFActionPerformed
  {//GEN-HEADEREND:event_barVolumeTFActionPerformed
    int value = Integer.parseInt(barVolumeTF.getText());
    
    if( value > 127 )
      {
        value = 127;
      }
    else if( value < 0 )
      {
        value = 0;
      }
    
    setVolumeIndicators(value);
    setBarVolume(value);
  }//GEN-LAST:event_barVolumeTFActionPerformed

private void barVolumeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_barVolumeSliderStateChanged
  {//GEN-HEADEREND:event_barVolumeSliderStateChanged
    int value = barVolumeSlider.getValue();
    barVolumeTF.setText("" + value);
    setBarVolume(value);
  }//GEN-LAST:event_barVolumeSliderStateChanged

private void SaveEntireStyleButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveEntireStyleButtonActionPerformed
  {//GEN-HEADEREND:event_SaveEntireStyleButtonActionPerformed
    styleEditor.saveStyle();
  }//GEN-LAST:event_SaveEntireStyleButtonActionPerformed

private void barVolumeImpliedCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_barVolumeImpliedCheckBoxActionPerformed
  {//GEN-HEADEREND:event_barVolumeImpliedCheckBoxActionPerformed
    boolean volumeImplied = barVolumeImpliedCheckBox.isSelected();
    if( selectedBar != null )
      {
        selectedBar.setVolumeImplied(volumeImplied); 
      }
    barVolumeSlider.setEnabled(!volumeImplied);
    barVolumeTF.setEnabled(!volumeImplied);
  }//GEN-LAST:event_barVolumeImpliedCheckBoxActionPerformed

    private void fileStepBackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepBackBtnActionPerformed
        columnStepBackward();
    }//GEN-LAST:event_fileStepBackBtnActionPerformed

    private void pianoRollButtonStepForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pianoRollButtonStepForwardBtnActionPerformed
        columnStepForward();
    }//GEN-LAST:event_pianoRollButtonStepForwardBtnActionPerformed

private void columnStepForward()
{
    Object selectedItem = importFromColumnComboBox.getSelectedItem();
    int selectedIndex = importFromColumnComboBox.getSelectedIndex();
    int columnNumber = Integer.parseInt(((String)selectedItem));
    if (selectedIndex == styleEditor.getColumns().getColumnCount()-2 ) 
      { 
       // Already at last column
       return;
      }
    importFromColumnComboBox.setSelectedIndex(selectedIndex+1);
    importFromStyleEditorColumn();
}

private void columnStepBackward()
{
    Object selectedItem = importFromColumnComboBox.getSelectedItem();
    int selectedIndex = importFromColumnComboBox.getSelectedIndex();
    int columnNumber = Integer.parseInt(((String)selectedItem));
    if (selectedIndex == 0) 
    { 
      // Already at first column
       return;
    }
    importFromColumnComboBox.setSelectedIndex(selectedIndex-1);
    importFromStyleEditorColumn();
}
private void setBarVolume(int value)
  {
    if( selectedBar != null && !selectedBar.getVolumeImplied() )
      {
        selectedBar.setVolume(value);
      }
  }

private void setVolumeIndicators(int value)
  {
    barVolumeTF.setText("" + value);
    
    barVolumeSlider.setValue(value);    
  }

private boolean checkPixelBeatConstraint()
{
int ticksPerBeat = intFromTextField(ticksPerBeatTextField, MIN_TICKS_PER_BEAT, MAX_TICKS_PER_BEAT, MIN_TICKS_PER_BEAT);
int pixelsPerBeat = intFromTextField(pixelsPerBeatTextField, MIN_PIXELS_PER_BEAT, MAX_PIXELS_PER_BEAT, MIN_PIXELS_PER_BEAT);
if( pixelsPerBeat >= ticksPerBeat )
    {
    return true;
    }
ErrorLog.log(ErrorLog.WARNING, "Pixels per beat must be greater than or equal to ticks per beat.");
return false;
}

PianoRollGrid getGrid()
{
    return grid;
}

PianoRollPanel getPanel()
{
    return pianoRollPanel;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton SaveEntireStyleButton;
    private javax.swing.ButtonGroup accidentalButtonGroup;
    private javax.swing.JPanel accidentalPanel;
    private javax.swing.JMenuItem addNewBar;
    private javax.swing.JRadioButton approachToneButton;
    protected javax.swing.JPopupMenu barCreatePopupMenu;
    protected javax.swing.JPopupMenu barEditPopupMenu;
    private javax.swing.JTextField barEditorContents;
    protected javax.swing.JFrame barEditorFrame;
    private javax.swing.JDialog barVolumeDialog;
    private javax.swing.JCheckBox barVolumeImpliedCheckBox;
    private javax.swing.JPanel barVolumePanel;
    private javax.swing.JSlider barVolumeSlider;
    private javax.swing.JTextField barVolumeTF;
    private javax.swing.JToggleButton bassEditorToggleButton1;
    private javax.swing.JRadioButton bassNoteButton;
    private javax.swing.JLabel beatsLabel;
    private javax.swing.JTextField beatsTextField;
    private javax.swing.JLabel bpmLabel;
    private javax.swing.JButton cancelEditButton;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JLabel cautionLabelForStylePatterns;
    private javax.swing.JRadioButton chordToneButton;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JMenuItem copyBar;
    private javax.swing.JMenuItem cutBar;
    private javax.swing.JPanel degreePanel;
    private javax.swing.JMenuItem deleteBar;
    private javax.swing.ButtonGroup directionButtonGroup;
    private javax.swing.JPanel directionPanel;
    private javax.swing.JRadioButton downDirection;
    private javax.swing.JLabel editErrorLabel;
    private javax.swing.JLabel editLabel;
    private javax.swing.JButton exportButton;
    private javax.swing.JComboBox exportToColumnComboBox;
    private javax.swing.JTextField exportToColumnTF;
    private javax.swing.JButton fileStepBackBtn;
    private javax.swing.JButton fileStepForwardBtn;
    private javax.swing.JRadioButton flatAccidental;
    private javax.swing.JButton importButton;
    private javax.swing.JPanel importExportPanel;
    private javax.swing.JComboBox importFromColumnComboBox;
    private javax.swing.JTextField importFromColumnTF;
    private javax.swing.JToggleButton loopToggleButton;
    private javax.swing.JRadioButton nextToneButton;
    private javax.swing.JRadioButton noAccidental;
    private javax.swing.JMenuItem noAction;
    private javax.swing.JRadioButton noDirection;
    private javax.swing.ButtonGroup noteTypeButtonGroup;
    private javax.swing.JPanel noteTypePanel;
    private javax.swing.JButton okEditBtn;
    private javax.swing.JMenuItem pasteBar;
    private javax.swing.JMenuBar pianoRollMenuBar;
    private javax.swing.JPanel pianoRollResolutionsPanel;
    private javax.swing.JScrollPane pianoRollScrollPane;
    private javax.swing.JRadioButton pitch1Button;
    private javax.swing.JRadioButton pitch2Button;
    private javax.swing.JRadioButton pitch3Button;
    private javax.swing.JRadioButton pitch4Button;
    private javax.swing.JRadioButton pitch5Button;
    private javax.swing.JRadioButton pitch6Button;
    private javax.swing.JRadioButton pitch7Button;
    private javax.swing.JPanel pitchPanel;
    private javax.swing.JRadioButton pitchToneButton;
    private javax.swing.JComboBox pixelsPerBeatComboBox;
    private javax.swing.JLabel pixelsPerBeatLabel;
    private javax.swing.JTextField pixelsPerBeatTextField;
    private javax.swing.JButton playBassButton;
    private javax.swing.JButton playChordButton;
    private javax.swing.JPanel playPanel;
    private javax.swing.JButton playPercussionButton;
    private javax.swing.JRadioButton repeatPitchButton;
    private javax.swing.JPanel rowTitlePanel;
    private javax.swing.ButtonGroup scalePitchButtonGroup;
    private javax.swing.JRadioButton scaleToneButton;
    private javax.swing.JRadioButton sharpAccidental;
    private javax.swing.JLabel slotsLabel;
    private javax.swing.JTextField slotsTextField;
    private javax.swing.JComboBox tempoComboBox;
    private javax.swing.JComboBox ticksPerBeatComboBox;
    private javax.swing.JLabel ticksPerBeatLabel;
    private javax.swing.JTextField ticksPerBeatTextField;
    private javax.swing.JRadioButton upDirection;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

public void closeWindow() {
        setLooping(false);
        styleEditor.unusePianoRoll();
        setVisible(false);
        WindowRegistry.unregisterWindow(this);
    }

}
