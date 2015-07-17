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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JDialog;
import javax.swing.JTextField;

/**
 * EntryPopup is a simple text editor that popups up triggered by certain
 * fields on the leadsheet's Stave.
 * They are used for the Title, Composer, part (chorus) title, etc.
 *
 * @author Martin Hunt, refactored by Robert Keller, March 1, 2012
 */

public class EntryPopup
        extends JDialog
        implements ActionListener, KeyListener, FocusListener
{

public static final int LEFT = 0, RIGHT = 1, CENTER = 2;
private Color outline = new Color(230, 230, 230);
private Color titleColor = new Color(0, 0, 40);
private Color titleBGHighlightColor = new Color(250, 250, 250);
private JTextField input;
private Font font;
private Font smallFont;
private FontMetrics fontMetrics;
private FontMetrics smallFontMetrics;
private Rectangle bounds;
private int alignment = LEFT;
private Graphics g;
private String name;
private boolean mouseOver = false;

private Stave stave;
private Notate notate;

public EntryPopup(String name, 
                  Font font, 
                  Font smallFont, 
                  int alignment,  
                  Stave stave)
  {
    this.stave = stave;
    this.notate = stave.getNotate();
    this.name = name;
    this.font = font;
    this.smallFont = smallFont;
    this.alignment = alignment;

    setDefaultLookAndFeelDecorated(false);
    setUndecorated(true);
    stave.setOpaque(false);

    input = new JTextField()
        {
        @Override
        public void paintComponent(Graphics g)
          {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            super.paintComponent(g2);
          }
        };

    getContentPane().add(input);

    fontMetrics = getFontMetrics(font);
    smallFontMetrics = getFontMetrics(smallFont);
    bounds = new Rectangle(-1, -1, 0, 0);

    //input.setMargin(new Insets(1, 3, 2, 4));
    input.setFont(font);
    input.addKeyListener(this);
    input.addActionListener(this);
    input.addFocusListener(this);
  }

public void setAlignment(int alignment)
  {
    this.alignment = alignment;
  }

public void textToStave(String data)
  {
    // override, stores text to stave
  }

public String staveToText()
  {
    // override, returns text to edit
    return "";
  }

public void repaintBounds()
  {
    stave.repaint(0, 0, stave.getPanelWidth(), stave.getHeadSpace());
  }

public boolean checkEvent(MouseEvent e)
  {
    if( !isVisible() && bounds.contains(e.getPoint()) )
      {
        if( !mouseOver )
          {
            repaintBounds();
            mouseOver = true;
            return e.getID() == MouseEvent.MOUSE_MOVED;
          }
      }
    else
      {
        if( mouseOver )
          {
            repaintBounds();
            mouseOver = false;
          }
      }

    if( e.getID() == MouseEvent.MOUSE_PRESSED 
     && bounds.contains(e.getPoint()) )
      {
        setVisible(true);
        return true;
      }
    return false;
  }

@Override
public void setVisible(boolean visible)
  {
    setVisible(visible, true);
  }

public void setVisible(boolean visible, boolean save)
  {
    if( visible )
      {
        input.setText(staveToText());
        updateBounds(staveToText());
        stave.repaint();
      }
    else if( isVisible() && save )
      { // check if visible cause we can lose focus when not being visible 
        // and thus this method might get called twice
        textToStave(input.getText());
        this.notate.cm.changedSinceLastSave(true);
      }

    super.setVisible(visible);
    pack();

    if( visible )
      {
        updatePosition();
        repaint();
      }
    else
      {
        dispose();
        stave.repaint();
      }
  }

private void updatePosition()
  {
    int x = notate.getX() + bounds.x;
    int y = notate.getY() + bounds.y;

    Component c = stave;
    while( c.getParent() != null && c != notate )
      {
        x += c.getX();
        y += c.getY();
        c = c.getParent();
      }

    setLocation(x - 1, y + 1);
  }

public void actionPerformed(ActionEvent e)
  {
    setVisible(false);
  }

private void updateWidth()
  {
    input.setSize(input.getWidth() + 10, input.getHeight());
  }

public void focusLost(FocusEvent e)
  {
    setVisible(false);
  }

public void focusGained(FocusEvent e)
  {
  }

public void keyTyped(KeyEvent e)
  {
    updateWidth();
  }

public void keyPressed(KeyEvent e)
  {
    //System.out.println("keyPressed in Stave: " + e);
    switch( e.getKeyCode() )
      {
        case KeyEvent.VK_ESCAPE: setVisible(false, false); break;
       
        case KeyEvent.VK_BACK_SPACE: break;
            
        default: // not good. causes actions in stave stave.getActionHandler().keyPressed(e);
      }
    
    updateWidth();
  }

public void keyReleased(KeyEvent e)
  {
    pack();
    switch( alignment )
      {
        case LEFT:
            break;
        case CENTER:
        case RIGHT:
            updateBounds(input.getText());
            updatePosition();
            break;
      }
    repaint();
  }

private int x = 0, y = 0;

public void draw(Graphics g, int x, int y)
  {
    this.x = x;
    this.y = y;

    if( isVisible() )
      {
        return;
      }

    String text = staveToText().trim();
    if( text.equals("") && !stave.getPrinting() /* && showEmptyTitles */ )
      {
        g.setFont(smallFont);
        if( mouseOver )
          {
            text = "click here to add " + name;
            updateBounds(smallFontMetrics, text);
          }
        else
          {
            updateBounds(smallFontMetrics, text, 50);
          }

        bounds.x -= 2;
        bounds.width += 4;
        bounds.height -= 3;

        drawFill(g);
        bounds.height += 2;

        if( mouseOver )
          {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(text, bounds.x + 2, y - 2);
          }
      }
    else
      {
        g.setFont(font);
        updateBounds(text);

        if( !stave.getPrinting() && mouseOver )
          {
            bounds.width += 2;
            bounds.x--;
            drawFill(g);
            bounds.x++;
          }

        g.setColor(titleColor);
        g.drawString(text, bounds.x, y);
      }

    g.setColor(Color.BLACK);
  }

private void drawFill(Graphics g)
  {
    int w = 4;
    g.translate(bounds.x, bounds.y + 1);
    g.setColor(titleBGHighlightColor);
    g.fillRect(0, 0, bounds.width, bounds.height + 1);
    g.setColor(outline);

    g.drawLine(0, 0, w, 0);
    g.drawLine(0, 0, 0, bounds.height);
    g.drawLine(0, bounds.height, w, bounds.height);
    g.translate(bounds.width - 1, 0);
    g.drawLine(0, 0, -w, 0);
    g.drawLine(0, 0, 0, bounds.height);
    g.drawLine(0, bounds.height, -w, bounds.height);
    g.translate(-bounds.x - bounds.width + 1, -bounds.y - 1);
  }

private void updateBounds(FontMetrics fontMetrics, String text)
  {
    Rectangle2D textSize = fontMetrics.getStringBounds(text, g);
    int width = (int) Math.ceil(textSize.getWidth());
    int height = (int) Math.ceil(textSize.getHeight());
    int descent = (int) Math.ceil(fontMetrics.getDescent());
    updateBounds(width, height, descent);
  }

private void updateBounds(FontMetrics fontMetrics, String text, int width)
  {
    Rectangle2D textSize = fontMetrics.getStringBounds(text, g);
    int height = (int) Math.ceil(textSize.getHeight());
    int descent = (int) Math.ceil(fontMetrics.getDescent());
    updateBounds(width, height, descent);
  }

private void updateBounds(String text)
  {
    Rectangle2D textSize = fontMetrics.getStringBounds(text, g);
    int width = (int) Math.ceil(textSize.getWidth());
    int height = (int) Math.ceil(textSize.getHeight());
    int descent = (int) Math.ceil(fontMetrics.getDescent());
    updateBounds(width, height, descent);
  }

private void updateBounds(int width, int height, int descent)
  {
    switch( alignment )
      {
        case LEFT:
            bounds.x = x;
            break;
        case RIGHT:
            bounds.x = x - width;
            break;
        case CENTER:
            bounds.x = x - width / 2;
            break;
      }
    bounds.width = width;
    bounds.y = y - height;
    bounds.height = height + descent;
    if( bounds.width == 0 )
      {
        bounds.width = 100;
      }
  }

}