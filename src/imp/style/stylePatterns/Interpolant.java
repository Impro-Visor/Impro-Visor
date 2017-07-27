/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

import imp.data.Duration;
import imp.data.Leadsheet;
import java.io.Serializable;
import polya.Polylist;

/**
 * 
 * @author Isys Johnson
 */

public class Interpolant
        extends Pattern implements Serializable
{
Polylist chords;

Polylist divide;

float weight;

int minslots;

public Interpolant() {
    
}

public Interpolant(Polylist chords, Polylist divide, float weight, int minslots)
  {
       
       this.chords = chords;
       this.divide = divide;
       this.weight = weight;
       this.minslots = minslots;
  }

private static final String keyword[] =
  {
  "chords", "division", "weight", "min-duration"
  };

// indices into the keyword array
private static final int CHORD = 0;

private static final int DIVISION = 1;

private static final int WEIGHTS = 2;

private static final int MIN_DURATION = 3;

public static Interpolant makeInterpolant(Polylist L) {
   float problemchild = ((Number)L.second()).floatValue();
    Interpolant interp =  new Interpolant((Polylist)L.first(), (Polylist)L.second(), problemchild, (int)L.last());
    return interp;
}

public static Interpolant makeInterpolantFromExp(Polylist L) {
    Polylist original = L;
    Interpolant interp = new Interpolant();
    //System.out.println("got here and broke1 " + L);
    while( L.nonEmpty() )
    {
        Polylist item = (Polylist)L.first();
        L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
        
      case CHORD:
        {
            //System.out.println("got here and broke2 " + item);
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               interp.setError("No chords in interpolate expression");
               break;
           }
           else if (item.first() instanceof Polylist) {
        
               interp.setCHORDS((Polylist)item.first());
           }
           break;
        }
        
      case DIVISION:
        {
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               interp.setError("No specified division in interpolate expression");
               break;
           }
           else if (item.first() instanceof Polylist) {
        
               interp.setDivide((Polylist)item.first());
           }
           break;
        }  
          
      case WEIGHTS:
        {
        try
          {
          Number w = (Number)item.first();
          interp.weight = w.floatValue();
     
          break;
          }
        catch( Exception e )
          {
            interp.setError("Expected weight value, but found " + item.first()
                      + " in " + original);
          }
        break;
        }
          
      case MIN_DURATION:
        {
        if( item.nonEmpty() )
          {
              
          interp.setMINSLOTS(Duration.getDuration("" + item.first()));
          
          }
        break;
        }
          
      default:
          interp.setError("Error in interpolation " + original);
          return interp;
      }
    }
  //System.out.println("makeInterpolantfromExp on " + original + " returns " + interp);
  return interp;
  }



public Polylist makeInterpolantList() {
    Polylist interpList = Polylist.list(chords, divide, weight, minslots);
    return interpList;
}

public Polylist getCHORDS() {
    return chords;
}

public void setCHORDS(Polylist CHORDS) {
    this.chords = CHORDS;
}

public Polylist getDivide() {
    return divide;
}

public void setDivide(Polylist divide) {
    this.divide = divide;
}



public float getWEIGHT() {
    return weight;
}

public void setWEIGHT(float WEIGHT) {
 
    this.weight = WEIGHT;
}

public int getMINSLOTS() {
    return minslots;
}

public void setMINSLOTS(int MINSLOTS) {
    this.minslots = MINSLOTS;
}


public String toString() {
    return ""+makeInterpolantList();
}

}
