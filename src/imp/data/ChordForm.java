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

package imp.data;

import imp.Constants;
import imp.util.ErrorLog;
import imp.util.Trace;
import java.io.Serializable;
import java.util.ArrayList;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.PolylistEnum;

/**
 * A ChordForm houses the form information for a chord.
 * The actual chord information is determined by supplying a root pitch.
 * @see         Advisor
 * @author      Robert Keller
 */
public class ChordForm
        implements Constants, Serializable
{
  
/**
 * Number of notes max of priority to use in voicing.
 */
  
private int PRIORITY_PREFIX_MAX = 5; 

static final String APPROACH = "approach";

static final String AVOID = "avoid";

static final String COLOR = "color";

static final String EXTENSION = "extension";

static final String EXTENSIONS = "extensions";

static final String FAMILY = "family";

static final String KEY = "key";

static final String NAME = "name";

static final String NOTES = "notes";

static final String PRIORITY = "priority";

static final String PRONOUNCE = "pronounce";

static final String SAME = "same";

static final String SCALES = "scales";

static final String SPELL = "spell";

static final String SUBS = "substitute";

static final String TYPE = "type";

static final String DEFAULT_FAMILY = "other";

static final String VOICINGS = "voicings";

static final String VOICING_WILD_CARD = "any";

/**
 * The constructor is private. A factory is used so that
 * error-checking can be done prior to construction
 */
String name = null;

Key key = Key.Ckey;

String family = DEFAULT_FAMILY;

Polylist pronounce = Polylist.nil;

Polylist spell = Polylist.nil;

Polylist priority = Polylist.nil;

Polylist avoid = Polylist.nil;

Polylist color = Polylist.nil;

Polylist approach = Polylist.nil;

Polylist scales = Polylist.nil;

Polylist voicings = Polylist.nil;

Polylist extensions = Polylist.nil;

Polylist substitutions = Polylist.nil;

String same = null;


static public ChordForm makeChordForm(Polylist arg)
  {
  ChordForm form = new ChordForm();

  Polylist nameElement = arg.assoc(NAME);
  if( nameElement == null )
    {
    ErrorLog.log(ErrorLog.WARNING,
            "Chord definition needs a name: " + arg + ", ignoring");
    return null;
    }

  if( nameElement.length() != 2 || !(nameElement.second() instanceof String) )
    {
    ErrorLog.log(ErrorLog.WARNING, "Error in chord name: " + arg + ", ignoring");
    return null;
    }

  form.name = (String)nameElement.second();

  Polylist keyElement = arg.assoc(KEY);

  if( keyElement != null )
    {
    if( (keyElement.length() != 2) || !(nameElement.second() instanceof String) || ((form.key =
            Key.getKey((String)keyElement.second())) == null) )
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Key " + (String)keyElement.second() + " in chord is invalid: " + arg + ", using default of C");
      form.key = Key.Ckey;
      }
    }
  else
    {
    form.key = Key.Ckey;
    }
  Polylist familyElement = arg.assoc(FAMILY);

  if( familyElement != null )
    {
    if( familyElement.length() != 2 || !(nameElement.second() instanceof String) )
      {
      ErrorLog.log(ErrorLog.WARNING, "FAMILY in chord is invalid: " + arg);
      return null;
      }
    form.family = (String)familyElement.second();
    }

  Polylist sameElement = arg.assoc(SAME);

  if( sameElement != null )
    {
    if( sameElement.length() != 2 || !(nameElement.second() instanceof String) )
      {
      ErrorLog.log(ErrorLog.WARNING, "SAME in chord is invalid: " + arg);
      return null;
      }
    form.same = (String)sameElement.second();
    }
  else
    {
    // Note: Chords designated as "same" have no spelling, etc.
    form.spell = arg.assoc(SPELL);

    if( form.spell == null || form.spell.rest().isEmpty() )
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Chord must have a non-empty spelling: " + arg);
      return null;
      }

    form.spell = form.spell.rest();	// get rid of SPELL keyword
    Polylist invalid = Key.invalidNotes(form.spell);
    if( invalid.nonEmpty() )
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Invalid notes in chord spelling: " + invalid);
      return null;
      }

    form.spell = NoteSymbol.makeNoteSymbolList(form.spell, 0);

    form.priority = arg.assoc(PRIORITY);

    if( form.priority != null )  // priority could be null
      {
      form.priority = form.priority.rest();	// get rid of PRIORITY keyword
      invalid = Key.invalidNotes(form.priority);
      if( invalid.nonEmpty() )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Invalid notes in chord priority list: " + invalid);
        return null;
        }
      }
    else
      {
      form.priority = Polylist.nil;
      }
    form.priority = NoteSymbol.makeNoteSymbolList(form.priority, 0);

    form.avoid = arg.assoc(AVOID); // avoid could be null

    if( form.avoid != null )
      {
      form.avoid = form.avoid.rest();
      invalid = Key.invalidNotes(form.avoid);
      if( invalid.nonEmpty() )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Invalid notes in chord avoid notes: " + invalid);
        return null;
        }
      }
    else
      {
      form.avoid = Polylist.nil;
      }
    form.avoid = NoteSymbol.makeNoteSymbolList(form.avoid, 0);

    form.color = arg.assoc(COLOR); // color could be null

    if( form.color != null )
      {
      form.color = form.color.rest();
      invalid = Key.invalidNotes(form.color);
      if( invalid.nonEmpty() )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Invalid tones in chord color tones: " + invalid);
        return null;
        }
      }
    else
      {
      form.color = Polylist.nil;
      }
    form.color = NoteSymbol.makeNoteSymbolList(form.color, 0);

    form.approach = arg.assoc(APPROACH); // approach could be null
    if( form.approach != null )
      {
      form.approach = form.approach.rest();	// get rid of APPROACH keyword
      Polylist L = form.approach;
      Polylist R = Polylist.nil;
      while( L.nonEmpty() )
        {
        Object first = L.first();
        if( !(first instanceof Polylist) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "approach tones must be a list of lists, but found: " + first);
          return null;
          }
        invalid = Key.invalidNotes((Polylist)first);
        if( invalid.nonEmpty() )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "Invalid notes in chord approach tones: " + invalid);
          return null;
          }
        R = R.cons(NoteSymbol.makeNoteSymbolList((Polylist)L.first(), 0));
        L = L.rest();
        }
      form.approach = R.reverse();
      }
    else
      {
      form.approach = Polylist.nil;      // Note: Chords designated as "same" have no spelling, etc.
      }
    form.pronounce = arg.assoc(PRONOUNCE);

    if( form.pronounce != null || form.pronounce.rest().nonEmpty() )
      {
      form.pronounce = form.pronounce.rest();	// get rid of PRONOUNCE keyword
      }

    form.voicings = Polylist.nil;

    Polylist voicings = arg.assoc(VOICINGS); // voicings could be null

    if( voicings != null && voicings.nonEmpty() )
      {
      voicings = voicings.rest(); //get rid of VOICINGS keyword

      while( voicings.nonEmpty() )
        {
        Object first = voicings.first();
        if( !(first instanceof Polylist) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "voicing specification must be of the form (NAME (type TYPE) (notes NOTE NOTE ...)): " + first);
          return null;
          }
        Polylist voicing = (Polylist)first;

        if( !(voicing.first() instanceof String) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "voicing specification must be of the form (NAME (type TYPE) (notes NOTE NOTE ...)): " + first);
          return null;
          }
        String id = (String)voicing.first();

        Polylist t = voicing.assoc(TYPE);
        if( t == null )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "voicing must have a type specification: " + first);
          return null;
          }
        if( !(t.second() instanceof String) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "voicing specification must be of form (NAME (type TYPE) (notes NOTE NOTE ...)): " + first);
          return null;
          }
        String type = (String)t.second();

        Polylist n = voicing.assoc(NOTES);
        if( n == null )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "voicing must have a notes specification: " + first);
          return null;
          }
        Polylist notes = NoteSymbol.makeNoteSymbolList(n.rest(), 0);

        Polylist ex = voicing.assoc(EXTENSION);
        Polylist extension = Polylist.nil;
        if( ex != null )
          {
          extension = NoteSymbol.makeNoteSymbolList(ex.rest(), 0);
          }

        form.addVoicing(id, type, notes, extension);

        voicings = voicings.rest();
        }
      
      form.voicings = form.voicings.reverse();
      }

    form.scales = Polylist.nil;

    Polylist scales = arg.assoc(SCALES); // scales could be null

    if( scales != null && scales.nonEmpty() )
      {
      scales = scales.rest();	// get rid of SCALES keyword

      while( scales.nonEmpty() )
        {
        Object first = scales.first();
        if( !(first instanceof Polylist) || ((Polylist)first).rest().isEmpty() // need at least one word to describe scale
                || !isListOfStrings((Polylist)first) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "scale specification must be of the form (tonic scale-name): " + first);
          return null;
          }

        String tonic = (String)((Polylist)first).first();
        String scaleName =
                Advisor.concatListWithSpaces(((Polylist)first).rest());

        if( Advisor.getScale(scaleName) == null )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "ignoring undefined scale named " + scaleName + " in chord: " + form.name);
          }
        else
          {
          form.scales = form.scales.cons(Polylist.list(tonic, scaleName));
          }
        scales = scales.rest();
        }
      }

    form.scales = form.scales.reverse();

    form.extensions = arg.assoc(EXTENSIONS); // could be null or empty
    if( form.extensions != null )
      {
      form.extensions = form.extensions.rest();	// get rid of EXTENSIONS keyword
      }
    else
      {
      form.extensions = Polylist.nil;
      }
    form.substitutions = arg.assoc(SUBS); // could be null or empty
    if( form.substitutions != null )
      {
      form.substitutions = form.substitutions.rest();	// get rid of SUBSTITUTIONS keyword
      }
    else
      {
      form.substitutions = Polylist.nil;
      }
    }

  if( Trace.atLevel(2) )
    {
    form.showForm(System.out);
    }

  if( form.name == null )
    {
    ErrorLog.log(ErrorLog.WARNING, "Chord must have a name: " + arg);
    return null;
    }

  if( form.same == null )
    {
    if( form.spell == null )
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Chord must have a non-empty spelling: " + arg);
      return null;
      }
    }

  return form;
  }

/**
 * Add a new voicing
 @param voicing
 */

public void addVoicing(String id, String type, Polylist notes, Polylist extension)
{
Voicing voicing = new Voicing(id, type, notes, extension);
voicings = voicings.cons(voicing);
}

/**
 * Remove first voicing with the given id
 @param voicing
 */

public void removeVoicing(String id)
  {
  PolylistEnum e = voicings.elements();
  PolylistBuffer buffer = new PolylistBuffer();
  
  boolean found = false;
  while( e.hasMoreElements() )
    {
    Voicing voicing = (Voicing)e.nextElement();
    if( !found && voicing.getName().equals(id) )
      {
      found = true;
      }
    else
      {
      buffer.append(voicing);
      }
    }
  voicings = buffer.toPolylist();
  }

/**
 * Remove the voicing in the given position
 @param voicing
 */

public void removeNthVoicing(int position)
  {
  PolylistEnum e = voicings.elements();
  PolylistBuffer buffer = new PolylistBuffer();
  
  // Keep all voicings except for the one at position
  
  for( int index = 0; e.hasMoreElements(); index++ )
    {
    Voicing voicing = (Voicing)e.nextElement();
    if( index != position )
      {
      buffer.append(voicing);
      }
    }
  
  voicings = buffer.toPolylist();
  }


public void showForm(java.io.PrintStream out)
  {
  out.println("\nChordForm name = " + getName() + ":");
  if( same != null )
    {
    out.println("same = " + getSame());
    }
  else
    {
    out.println("family = " + getFamily());
    out.println("spell = " + getSpell(CROOT));
    out.println("priority = " + getPriority(CROOT));
    out.println("color = " + getColor());
    out.println("scales = " + getScales());
    out.println("extensions = " + getExtensions());
    out.println("substitutions = " + getSubstitutions());
    }
  }


public void showName(java.io.PrintStream out)
  {
  int tab = 15;
  out.print(padTo(getName(), tab, '.') + " ");
  if( same != null )
    {
    out.println("same as " + getSame());
    }
  else
    {
    out.println(getPrononunce() + " " + spell);
    }
  }


static String padTo(String in, int desiredLen, char padChar)
  {
  if( in.length() >= desiredLen )
    {
    return in;
    }
  StringBuffer result = new StringBuffer();
  result.append(in);
  for( int i = in.length(); i < desiredLen; i++ )
    {
    result.append(padChar);
    }
  return result.toString();
  }


public String getName()
  {
  return name;
  }


/**
 * Returns the name of the chord for which this chord is a synonym, if any; otherwise null 
 @return the name of the chord for which this chord is a synonym, if any; otherwise null 
 */

public String getSame()
  {
  return same;
  }

/**
 @return true if this chord is a synonym for another chord,
 * as indicated by a non-null 'same' field
 */
public boolean isSynonym()
  {
    return same != null;
  }


/**
 * Returns the family to which this chord belongs, if any is specified. Otherwise returns null.
 @return the family to which this chord belongs
 */

public String getFamily()
  {
  if( same == null )
    {
    return family;
    }
  else
    {
    return Advisor.getChordForm(same).getFamily();
    }
  }

/**
 * Returns the color tones for this chord, relative to root "C"
 @return the color tones for this chord, relative to root "C"
 */

public Polylist getColor()
  {
  return color;
  }


/**
 * Returns the color tones for this chord, relative to the specified root
 @return the color tones for this chord, relative to the specified root
 */

public Polylist getColor(String root)
  {
  return getColor(root, Key.Ckey);
  }


public Polylist getColor(String root, Key key)
  {
  int rise = PitchClass.findRise(root);
  Polylist newColor = keepPositiveProbs(NoteSymbol.transposeNoteSymbolList(color, rise));
  Trace.log(2,
          "spell before transposition: " + spell + " to key " + key + ": " + newColor);

  return newColor;
  }

public static Polylist keepPositiveProbs(Polylist L)
{
    Polylist M = Polylist.nil;
    while( L.nonEmpty() )
    {
        NoteSymbol ns = (NoteSymbol)L.first();
        if( ns.getProbability() > 0 )
        {
            M = M.cons(ns);
        }
        L = L.rest();
    }
    return M;
}

public Key getKey(String root)
  {
  return key.transposeKey(LCROOT, root);
  }


public Key getKey()
  {
  return key;
  }


public String getPrononunce()
  {
  return Advisor.concatListWithSpaces(pronounce);
  }

/* old version
public String toString()
  {
  return "ChordForm: " + name;
  }

*/

public Polylist toPolylist()
  {
  PolylistBuffer buffer = new PolylistBuffer();
  buffer.append("chord");
  buffer.append(Polylist.list(NAME, name));
  buffer.append(pronounce.cons(PRONOUNCE));
  if( same != null )
    {
    buffer.append(Polylist.list(SAME, same));
    }
  else
    {
    buffer.append(Polylist.list(KEY, key));
    buffer.append(Polylist.list(FAMILY, family));
    buffer.append(spell.cons(SPELL));
    buffer.append(color.cons(COLOR));
    buffer.append(priority.cons(PRIORITY));
    buffer.append(approach.cons(APPROACH));
    buffer.append(voicings.cons(VOICINGS)); // FIX!!
    buffer.append(extensions.cons(EXTENSIONS));
    buffer.append(scales.cons(SCALES));
    buffer.append(avoid.cons(AVOID));
    buffer.append(substitutions.cons(SUBS));
    }
  return buffer.toPolylist();
  }

@Override
public String toString()
{
  return toPolylist().toString();
}

public Polylist getSpell(String root)
  {
  return getSpell(root, Key.Ckey);
  }


public Polylist getSpell(String root, Key key)
  {
  int rise = PitchClass.findRise(root);
  Polylist newSpell = NoteSymbol.transposeNoteSymbolList(spell, rise);
  Trace.log(2,
          "spell before transposition: " + spell + " to key " + key + ": " + newSpell);

  return newSpell;
  }

public boolean[] getSpellVector(String root, Key key)
  {
    return NoteSymbol.noteSymbolListToBitVector(getSpell(root, key));
  }

public ArrayList<Integer> getSpellMIDIarray(String root)
  {
    return NoteSymbol.noteSymbolListToMIDIarray(getSpell(root, Key.Ckey));
  }

public ArrayList<Integer> getScaleMIDIarray(String root)
  {
    return NoteSymbol.noteSymbolListToMIDIarray(getFirstScaleTones(root));
  }

public ArrayList<Integer> getColorMIDIarray(String root)
  {
    return NoteSymbol.noteSymbolListToMIDIarray(getColor(root, Key.Ckey));
  }

public Polylist getPriority(String root)
  {
  return getPriority(root, Key.Ckey);
  }


public Polylist getPriority(String root, Key key)
  {
  if( priority == null )
    {
    return getSpell(root, key);
    }

  int rise = PitchClass.findRise(root);
  Polylist newPriority = NoteSymbol.transposeNoteSymbolList(priority, rise);
  Trace.log(2,
          "priority before transposition: " + priority + " to key " + key + ": " + newPriority);

  return newPriority;
  }


public Polylist getScales()
  {
  return scales;
  }


public Polylist getFirstScale()
  {
  Polylist scales = (Polylist)getScales();
  Polylist scale = new Polylist();
  if( scales.nonEmpty() )
    {
    scale = (Polylist)scales.first();
    }
  else
    {
    return null;
    }
  NoteSymbol tonic = NoteSymbol.makeNoteSymbol((String)scale.first());

  String scaleType = Advisor.concatListWithSpaces(scale.rest());

  ScaleForm scaleForm = Advisor.getScale(scaleType);

  Polylist tones = scaleForm.getSpell(tonic);

  return tones;
  }


public Polylist getFirstScaleTones(String root)
  {
  Polylist firstScale = getFirstScale();
  int rise = PitchClass.findRise(root);

  if( firstScale == null )
    {
    return null;
    }
  return NoteSymbol.transposeNoteSymbolList(firstScale, rise);

  }


public Polylist getPriority()
  {
  return priority;
  }


public Polylist getApproach()
  {
  return approach;
  }


public Polylist getApproach(String root)
  {
  Polylist out = Polylist.nil;
  Polylist in = approach;
  int rise = PitchClass.findRise(root);
  while( in.nonEmpty() )
    {
    out = out.cons(
            NoteSymbol.transposeNoteSymbolList((Polylist)in.first(), rise));
    in = in.rest();
    }

  return out.reverse();
  }


public Polylist getSubstitutions()
  {
  return substitutions;
  }


public Polylist getExtensions()
  {
  return extensions;
  }


/**
 * Add more color tones, such as ones implied by extensions.
 */
public void addColor(Polylist newColor)
  {
  color = color.append(newColor);
  /*
  while( newColor.nonEmpty() )
    {
    NoteSymbol symbol = (NoteSymbol)newColor.first();
    if( !symbol.enhMember(color) )
      {
      color = color.cons(symbol);
      }
    newColor = newColor.rest();
    }
   */
  }


public Polylist generateVoicings(String root, Key key)
  {
  Polylist spell = getPriority(root, key);

  /* Maybe we shouldn't always drop the root, as this does? */
  
  spell = NoteSymbol.makeNoteSymbol(root).enhDrop(spell);

  /* evidently intended to drop the 5th out for longer voicings.
   * Should be handled by priority
  if( spell.length() > 3 )
    {
    spell = NoteSymbol.makeNoteSymbol(root).transpose(7).enhDrop(spell);
    }
  */
  
  spell = spell.prefix(PRIORITY_PREFIX_MAX);
  
  /* poor
  if( spell.length() > 4 )
    {
    spell =
            Polylist.list(spell.nth(0), spell.nth(1), spell.nth(2), spell.nth(3));
    }
  */
  
  PolylistEnum perms = permutations(spell).elements();
  PolylistBuffer newVoicings = new PolylistBuffer();

  while( perms.hasMoreElements() )
    {
    Polylist voicing = (Polylist)perms.nextElement();
    PolylistBuffer newVoicing = new PolylistBuffer();
    newVoicing.append(voicing.first());
 
    NoteSymbol lastNote = (NoteSymbol)voicing.first();
    
    PolylistEnum notes = voicing.rest().elements();
    
    while( notes.hasMoreElements() )
      {
      NoteSymbol note = (NoteSymbol)notes.nextElement();

      while( note.getMIDI() < lastNote.getMIDI() )
        {
        note = note.transpose(12);
        }
     
      
      lastNote = note;
      newVoicing.append(note);
      }
    
    /* not sure why to use this. Something to do with excluding flat 9? 
    
    if( containsInterval(13, newVoicing.toPolylist()) )
      {
      continue;
      }
    */
    
    /* The nil below is the extension part, I believe. */
    
    newVoicings.append(Polylist.list(newVoicing.toPolylist(), Polylist.nil));
    }

  return newVoicings.toPolylist();

  }

/**
 * Put voicings in a form for output
 @param root
 @return
 */

public Polylist getVoicingInfo(String root)
  {
  int rise = PitchClass.findRise(root);
  PolylistEnum v = voicings.elements();
  PolylistBuffer voicingsOut = new PolylistBuffer();

    while( v.hasMoreElements() )
      {
      Voicing first = (Voicing)v.nextElement();

      String id = first.getName();
      String type = first.getType();
      Polylist notes = first.getNotes();
      Polylist exts = first.getExtension();

      notes = NoteSymbol.transposeNoteSymbolList(notes, rise);
      exts = NoteSymbol.transposeNoteSymbolList(exts, rise);
      voicingsOut.append(Polylist.list(id, type, notes, exts));
      }

  return voicingsOut.toPolylist();
  }


public Polylist getVoicings(String root, Key key)
  {
  return getVoicings(root, key, "");
  }


public Polylist getVoicings(String root, Key key, String type)
  {
  PolylistEnum v = voicings.elements();
  PolylistBuffer voicings = new PolylistBuffer();
  while( v.hasMoreElements() )
    {
    Voicing first = (Voicing)v.nextElement();

    String vType = first.getType();
    
    if( !type.equals("") && !vType.equals(type) && !type.equals(
            VOICING_WILD_CARD) )
      {
      continue;
      }
    Polylist voicing = first.getNotes();
    Polylist extension = first.getExtension();
    int rise = PitchClass.findRise(root);
    voicing = NoteSymbol.transposeNoteSymbolList(voicing, rise);
    extension = NoteSymbol.transposeNoteSymbolList(extension, rise);
    voicings.append(Polylist.list(voicing, extension));
    }
  return voicings.toPolylist();
  }

/**
 * Test whether a list of NoteSymbols contains a specified interval
 @param interval
 @param notes
 @return
 */

public static boolean containsInterval(int interval, Polylist notes)
  {
  
  for( PolylistEnum i = notes.elements(); i.hasMoreElements();  )
    {
    NoteSymbol first = (NoteSymbol)i.nextElement();
    int firstMIDI = first.getMIDI();
    int firstPlusInterval = firstMIDI + interval;
    
    for( PolylistEnum j = notes.elements(); j.hasMoreElements();  )
      {
      NoteSymbol last = (NoteSymbol)j.nextElement();
      
      if( last.getMIDI() == firstPlusInterval )
        {
        return true;
        }
      }
    }

  return false;
  }


public static Polylist permutations(Polylist L)
  {
  if( L.length() == 0 )
    {
    return Polylist.nil;
    }
  if( L.length() == 1 )
    {
    return Polylist.list(L);
    }
  Object first = L.first();
  Polylist rest = permutations(L.rest());
  Polylist permutes = Polylist.nil;
  for( int i = 0; i < rest.length(); i++ )
    {
    Polylist list = (Polylist)rest.nth(i);
    for( int j = 0; j < list.length() + 1; j++ )
      {
      Polylist newList = Polylist.nil;
      for( int k = 0; k < j; k++ )
        {
        newList = newList.reverse().cons(list.nth(k)).reverse();
        }
      newList = newList.reverse().cons(first).reverse();
      for( int k = j + 1; k < list.length() + 1; k++ )
        {
        newList = newList.reverse().cons(list.nth(k - 1)).reverse();
        }
      permutes = permutes.cons(newList);
      }
    }
  return permutes;
  }


private ChordForm()
  {
  }


static boolean isListOfStrings(Polylist L)
  {
  while( L.nonEmpty() )
    {
    if( !(L.first() instanceof String) )
      {
      return false;
      }
    L = L.rest();
    }
  return true;
  }


/**
 * Return the color type associated with a given note and transposition.
 */
public int getTypeIndex(Note note, int transpose)
  {
  // 8 is to accomodate the way notes are stored currently
  String noteName = note.getPitchClassName() + "8";

  if( noteName.equals(EIGHTH_REST_STRING) )
    {
    return CHORD_TONE;
    }

  NoteSymbol symbol = NoteSymbol.makeNoteSymbol(noteName, transpose);

  if( symbol.enhMember(spell) )
    {
//System.out.println(note + " named " + noteName + " trans " + transpose + " to " + symbol + " is in spell: " + spell + " of " + name);  
    return CHORD_TONE;
    }

  if( symbol.enhMember(color) )
    {
//System.out.println(note + " named " + noteName + " trans " + transpose + " to " + symbol + " is in color: " + color + " of " + name);
    return COLOR_TONE;
    }

//System.out.println(note + " named " + noteName + " trans " + transpose + " to " + symbol + " is foreign to " + name);
  return FOREIGN_TONE;
  }

}
