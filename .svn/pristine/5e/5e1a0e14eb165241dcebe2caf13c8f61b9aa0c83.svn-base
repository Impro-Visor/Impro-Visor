/*
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

import imp.util.ErrorLog;
import polya.Polylist;

/**
 *
 * @author Carli Lessard
 */
public class ChordPatternElement {

    public enum ChordNoteType
    {
    CHORD, PITCH, VOLUME, REST, UNKNOWN
    }

    ChordNoteType noteType = ChordNoteType.CHORD;

    int degree = 1;

    String durationString;

    public ChordPatternElement getCopy()
    {
	return new ChordPatternElement(noteType, degree, durationString);
    }

    public ChordPatternElement(String durationString)
    {
	this(ChordNoteType.CHORD, durationString);
    }

    public ChordPatternElement(ChordNoteType noteType, String durationString)
    {
	this.noteType = noteType;
	this.durationString = durationString;
    }

    public ChordPatternElement(ChordNoteType noteType, int degree, String durationString)
    {
	this.noteType = noteType;
	this.degree = degree;
	this.durationString = durationString;
    }

    public ChordNoteType getNoteType()
    {
	return noteType;
    }

    public void setNoteType(ChordNoteType noteType)
    {
    	this.noteType = noteType;
    }

    public int getDegree()
    {
	return degree;
    }

    public void setDegree(int degree)
    {
	this.degree = degree;
    }

    public int getSlots()
    {
	return Duration.getDuration(durationString);
    }

    public void setDuration(int slots)
    {
	durationString = Note.getDurationString(slots);
    }

    public static ChordPatternElement makeChordPatternElement(Object ob) 
    {
	if( ob instanceof String )
	{
		String stringOb = (String) ob;
		
		if( stringOb.equals("") )
		{
			return null;
		}
		
		String durationString = stringOb.substring(1);

		if( stringOb.startsWith("V") )
		{
			return new ChordPatternElement(ChordNoteType.VOLUME, durationString);
		}

		ChordNoteType noteType = getChordNoteType(stringOb.charAt(0));

		if( noteType == ChordNoteType.UNKNOWN )
		{
			ErrorLog.log(ErrorLog.WARNING, "Unknown chord note type: " + stringOb);
			return null;
		}
		
		if( durationString.equals("") )
		{
			ErrorLog.log(ErrorLog.WARNING, "Chord pattern element has no duration: " + stringOb);
			return null;
		}
		
		else
		{
			int duration = Duration.getDuration(durationString);

			if( duration == 0 )
			{
				ErrorLog.log(ErrorLog.WARNING, "Chord pattern ele,ent has 0 duration: " + stringOb);
				return null;
			}

			else
			{
				return new ChordPatternElement(noteType, durationString);
			}
		}
	}

	else if( ob instanceof Polylist )
	{
		Polylist listOb = (Polylist) ob;
		int len = listOb.length();
		if( len == 3 )
		{
			if( listOb.first().equals("X") )
			{
				Object second = listOb.second();

				int degreeValue = 1;

				if( second instanceof Long )
				{
					degreeValue = ((Long) listOb.second()).intValue();

					if( degreeValue < 1 || degreeValue > 11 || degreeValue == 8 || degreeValue == 10 )
					{
						ErrorLog.log(ErrorLog.WARNING, "Scale degree out of range in chord note: " + ob);
						return null;
					}
				}
				
				String durationString = "";
			
				Object third = listOb.third();

				if( third instanceof Long || third instanceof String )
				{
					durationString = "" + third;

					int duration = Duration.getDuration(durationString);

					if ( duration == 0 )
					{
						ErrorLog.log(ErrorLog.WARNING, "Chord pattern element has 0 duration: " + ob);
						return null;
					}
				}

				return new ChordPatternElement(ChordNoteType.PITCH, degreeValue, durationString);

			}
		}
	}
	ErrorLog.log(ErrorLog.WARNING, "Unrecognized chord note: " + ob);
	return null;
    }

public static ChordNoteType getChordNoteType(char c)
{
	switch( c )
	{
		case 'X':
		case 'x':
			return ChordNoteType.CHORD;

		case 'R':
		case 'r':
			return ChordNoteType.REST;

		case 'V':
		case 'v': 
			return ChordNoteType.VOLUME;

		default:	
			return ChordNoteType.UNKNOWN;
	}
}

public String getDurationString()
{
	return durationString;
}

public String getDegreeString()
{
	return "" + getDegree();
}

public Object getText()
{
	switch( noteType )
	{
		default:
		case CHORD:
			return "X" + getDurationString();

		case PITCH:
			return Polylist.list("X", getDegreeString(), getDurationString());
	}
}

public boolean nonRest()
{
	return noteType != ChordNoteType.REST;
}
    
}
