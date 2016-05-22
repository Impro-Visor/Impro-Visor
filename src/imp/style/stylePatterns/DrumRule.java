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

package imp.style.stylePatterns;

/**
 *
 * Created on September 2, 2007, 5:36 PM
 *
 * Added after the fact, a "DrumRule" is the part of a DrumPattern 
 * corresponding to a single percussion instrument.
 * This is temporarily hacked for the spreadsheet table view, but ultimately 
 * should be replaced with DrumRuleRep
 *
 *
 * @author keller
 */
public class DrumRule
{
  String pattern;
  int instrument;
  String name;
      
      
  /** Creates a new instance of DrumRule */
  public DrumRule(String pattern, int instrument)
  {
    this.pattern = pattern;
    this.instrument = instrument;
  }
  
  public String getPattern()
  {
    return pattern;
  }
  
  public int getInstrument()
  {
    return instrument;
  }
  
  public String getName()
  {
      return name;
  }
  
  public void setName(String ruleName)
  {
      name = ruleName;
  }
  
}
