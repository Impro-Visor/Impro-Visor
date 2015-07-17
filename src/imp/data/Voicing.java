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

import java.io.Serializable;
import polya.Polylist;

/**
 * A Voicing represents the voicing of a Chord.
 * @author keller
 */

public class Voicing implements Serializable
{
private String name;

private String type;

private Polylist notes;

private Polylist extension;


public Voicing(String name, String type, Polylist notes, Polylist extension)
  {
  this.name = name;
  this.type = type;
  this.notes = notes;
  this.extension = extension;
  }


public String getName()
  {
  return name;
  }


public String getType()
  {
  return type;
  }


public Polylist getNotes()
  {
  return notes;
  }


public Polylist getExtension()
  {
  return extension;
  }

public String toString()
{
  StringBuffer buffer = new StringBuffer();
  buffer.append("(");
  buffer.append(name);
  buffer.append(" ");
  buffer.append("(type ");
  buffer.append(type);
  buffer.append(")");
  buffer.append(notes.cons(("notes")));
  buffer.append(extension.cons(("extension")));
  buffer.append(")");
  return buffer.toString();
}

}
