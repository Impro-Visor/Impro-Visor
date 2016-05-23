/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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

/**
 *
 * @author Nava Dallal, Amelia Sheppard
 */
package imp.themeWeaver;

import imp.data.MelodyPart;

public class ThemeUse 
{
Theme theme;

double probUse;

double probTranspose;

double probInvert;

double probReverse;

double probExpand;

double probSideslip;

double probBarLineShift;

public ThemeUse(MelodyPart melody)
{
    theme = new Theme(melody);
} 

  public ThemeUse(Theme theme, double probUse, double probTranspose, double probInvert,
          double probReverse, double probExpand, double probSideslip)
      {
        this.theme = theme;
        this.probUse = probUse;
        this.probTranspose = probTranspose;
        this.probInvert = probInvert;
        this.probReverse = probReverse;
        this.probExpand = probExpand;
        this.probSideslip = probSideslip;
      }

    public Theme getTheme()
      {
        return theme;
      }

    public double getProbUse()
      {
        return probUse;
      }

    public double getProbTranspose()
      {
        return probTranspose;
      }

    public double getProbInvert()
      {
        return probInvert;
      }

    public double getProbReverse()
      {
        return probReverse;
      }
    
    public double getProbExpand()
      {
        return probExpand;
      }
    
    public double getProbSideslip()
      {
        return probSideslip;
      }
    
    public double getProbBarLineShift()
      {
        return probBarLineShift;
      }
public String toString()
{
    return "ThemeUse " + theme.melody + " " 
          + probUse + " " 
          + probTranspose + " "
          + probInvert + " " 
          + probReverse;
}
}
