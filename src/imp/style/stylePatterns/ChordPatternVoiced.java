/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2016 Robert Keller and Harvey Mudd College
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

import imp.data.MelodyPart;
import imp.data.Note;
import java.util.Iterator;
import java.util.LinkedList;
import polya.Polylist;

/**
 *
 * @author keller
 */
public class ChordPatternVoiced
{
    private LinkedList<Polylist> voicings;
    private MelodyPart durations;
    
    public ChordPatternVoiced(LinkedList<Polylist> voicings,  
                              MelodyPart durations)
      {
        this.voicings = voicings;
        this.durations = durations;
      }
    
    public LinkedList<Polylist> getVoicings()
      {
        return voicings;
      }
    
    public Polylist getFirstVoicing()
      {
        return voicings.get(0);
      }
    
    public MelodyPart getDurations()
      {
        return durations;
      }
    
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(pattern (slots voicing) ");
        Iterator<Note> it = durations.iterator();
        for( Polylist v: voicings )
        {
            buffer.append("(");
            buffer.append(it.next().getRhythmValue());
            buffer.append(" ");
            buffer.append(v);
            buffer.append(") ");
        }
        buffer.append(")");
        return buffer.toString();
    }
}
