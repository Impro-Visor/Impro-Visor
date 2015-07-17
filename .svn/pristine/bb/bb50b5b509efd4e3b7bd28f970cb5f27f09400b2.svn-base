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

package imp.data;

import polya.*;
import imp.*;
import imp.data.*;
import imp.util.*;

/**
 * A ScaleForm houses the form information for a scale
 * The actual scale information is determined by supplying a tonic pitch.
 * @see         Advisor
 * @author      Robert Keller
 */
public class ScaleForm implements Constants
{
    static final String NAME       = "name";
    static final String SPELL      = "spell";
    static final String SAME       = "same";

/**
 * The constructor is private. A factory is used so that
 * error-checking can be done prior to construction
 */

String name = null;

Polylist spell = null;

String same = null;

static public ScaleForm makeScaleForm(Polylist arg)
  {
  ScaleForm form = new ScaleForm();

  Polylist nameElement = arg.assoc(NAME);
  if( nameElement == null )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Scale definition needs a name: " + arg + ", ignoring");
    return null;
    }
  
  if( nameElement.rest().isEmpty() )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Error empty scalename: " + arg + ", ignoring");
    return null;
    }
  
   nameElement = nameElement.rest();

   if( !nameElement.first().equals(CROOT) )
     {
     ErrorLog.log(ErrorLog.SEVERE, "Only scales with tonic C are allowed currently: " + arg);
     return null;
     }

  form.name = Advisor.concatListWithSpaces(nameElement.rest());

  Polylist sameElement = arg.assoc(SAME);

  if( sameElement != null )
   {
   if( sameElement.isEmpty() )
     {
     ErrorLog.log(ErrorLog.SEVERE, "'same' reference in scale cannot be empty : " + arg);
     return null;
     }

   sameElement = sameElement.rest();
   if( !sameElement.first().equals(CROOT) )
     {
     ErrorLog.log(ErrorLog.SEVERE, "Only scales with tonic C are allowed currently: " + arg);
     return null;
     }
   form.same = Advisor.concatListWithSpaces(sameElement.rest());
   }
  else
    {  
    // Note: Scales designated as "same" have no spelling, etc.

    form.spell = arg.assoc(SPELL);

    if( form.spell == null || form.spell.rest().isEmpty() )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Scale must have a non-empty spelling: " + arg);
      return null;
      }

    form.spell = form.spell.rest();	// get rid of SPELL keyword
    Polylist invalid = Key.invalidNotes(form.spell);
    if( invalid.nonEmpty() )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Invalid notes in scale spelling: " + invalid);
      return null;
      }

    form.spell = NoteSymbol.makeNoteSymbolList(form.spell, 0);
    }

  if( Trace.atLevel(2) )
    {
    form.showForm(System.out);
    }

  if( form.name == null )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Scale must have a name: " + arg);
      return null;
      }

  if( form.same == null )
    {
    if( form.spell == null )
	{
	ErrorLog.log(ErrorLog.SEVERE, "Scale must have a non-empty spelling: " + arg);
	return null;
	}
    }

  return form;  
  }

public void showForm(java.io.PrintStream out)
  {
  out.println("ScaleForm name = " + getName() + ":");
  if( same != null )
    {
    out.println("same = " + getSame());
    }
  else
    {
    out.println("spell = " + getSpell());
    }
  }

String getName() { return name; }

String getSame() { return same; }

public Polylist getSpell()
  {
  return spell;
  }

public Polylist getSpell(NoteSymbol tonic)
  {
  return getSpell(tonic, Key.Ckey);
  }

public Polylist getSpell(NoteSymbol tonic, Key key)
  {
  int rise = PitchClass.findRise(tonic.getPitchClass());
  Polylist newSpell = NoteSymbol.transposeNoteSymbolList(spell, rise);

  Trace.log(2, "notes before transposition: " + spell + " to tonic " + tonic + ": " + newSpell);

  return newSpell;
  }

private ScaleForm()
  {
  }

}
