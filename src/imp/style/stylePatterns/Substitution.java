/* Impro-Visor is free software; you can redistribute it and/or modify
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

import imp.data.Duration;
import imp.data.Leadsheet;
import java.io.Serializable;
import polya.Polylist;

/**
 *
 * @author Isys Johnson
 */
public class Substitution
        extends Pattern implements Serializable
{
Polylist originals;    

Polylist substitutions;

float weight;

int minslots;

public Substitution() {
    
}

public Substitution(Polylist originals, Polylist subs, float weight, int minslots)
  {
       this.originals = originals;
       this.substitutions = subs;
       this.weight = weight;
       this.minslots = minslots;
  }

private static final String keyword[] =
  {
  "from", "to", "weight", "min-duration"
  };

// indices into the keyword array
private static final int ORIGINS = 0;

private static final int SUBSTITUTIONS = 1;

private static final int WEIGHTS = 2;

private static final int MIN_DURATION = 3;

public static Substitution makeSubstitution(Polylist L) {
   float problemchild = ((Number)L.second()).floatValue();
    Substitution sub =  new Substitution((Polylist)L.first(), 
            (Polylist)L.second(),problemchild, (int)L.last());
    return sub;
}

public static Substitution makeSubstitutionFromExp(Polylist L) {
    Polylist original = L;
    Substitution sub = new Substitution();
  
    while( L.nonEmpty() )
    {
        Polylist item = (Polylist)L.first();
        L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case ORIGINS:
        {
            
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               sub.setError("No orignal chords in substitution expression");
               break;
           }
           else if (item.first() instanceof Polylist) {
               sub.setOriginals((Polylist)item.first());
           }
           break;
        }  
        
      case SUBSTITUTIONS:
        {
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               sub.setError("No substitutes in substitution expression");
               break;
           }
           else if (item.first() instanceof Polylist) {
               sub.setSubs((Polylist)item.first());
           }
           break;
        }
          
      case WEIGHTS:
        {
        try
          {
          Number w = (Number)item.first();
          sub.weight = w.floatValue();
     
          break;
          }
        catch( Exception e )
          {
            sub.setError("Expected weight value, but found " + item.first()
                      + " in " + original);
          }
        break;
        }
          
      case MIN_DURATION:
        {
        if( item.nonEmpty() )
          {
              
          sub.setMinSlots(Duration.getDuration("" + item.first()));
          
          }
        break;
        }
          
      default:
          sub.setError("Error in substitution " + original);
          return sub;
      }
    }
  return sub;
  }

public Polylist getOriginals() {
    return originals;
}

public void setOriginals(Polylist originals) {
    this.originals = originals;
}

public Polylist makeSubstitutionList() {
    Polylist subList = Polylist.list(originals,substitutions, weight, minslots);
    return subList;
}

public Polylist getSubs() {
    return substitutions;
}

public void setSubs(Polylist substitutions) {
    this.substitutions = substitutions;
}

public int getMinSlots() {
    return minslots;
}

public void setMinSlots(int minslots) {
    this.minslots = minslots;
}

public float getWeight() {
    return weight;
}

public void setWeight(float weight) {
    this.weight = weight;
}



public String toString() {
    return ""+makeSubstitutionList();
}

}
