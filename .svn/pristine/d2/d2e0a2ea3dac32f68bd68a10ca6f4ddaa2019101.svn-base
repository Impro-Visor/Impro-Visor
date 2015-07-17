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


import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author keller
 */

public class StaveScrollPane extends JScrollPane
{
/**
 * The index of this StaveScrollPane
 */
  
int myIndex;

/**
 * background for stave
 */

JPanel BG;

/**
 * The Stave in this StaveScrollPane
 */

protected Stave stave;


public StaveScrollPane(int index)
{
  myIndex = index;
  BG = new JPanel();
}

public void setStave(Stave stave)
  {
  this.stave = stave;
  }

public Stave getStave()
  {
  return stave;
  }

public java.awt.Point getBGlocation()
{
  return BG.getLocation();
}

public void setBGlocation(int x, int y)
{
  BG.setLocation(x, y);
}

public void resetViewportView()
{
  setViewportView(BG);
}

public void setBGcolor(java.awt.Color color)
{
  BG.setBackground(color);
}

public void removeAllBG()
{
  BG.removeAll();
}

public void addStave(Stave stave)
{
  BG.add(stave);
}
public JPanel getBG()
{
  return BG;
}

public int getNumLines()
  {
    return stave.getNumLines();
  }

public void setKeySignature(int key)
  {
    stave.setKeySignatureNonRecursively(key);
  }
}
