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
Polylist TARGETS;

String INSERT;

float WEIGHT;

int MINSLOTS;

public Interpolant() {
    
}

public Interpolant(Polylist targets, String insert, float weight, int minslots)
  {
       TARGETS = targets;
       INSERT = insert;
       WEIGHT = weight;
       MINSLOTS = minslots;
  }

private static final String keyword[] =
  {
  "target", "interpolant", "weight", "min-duration"
  };

// indices into the keyword array
private static final int TARGET = 0;

private static final int INTERPOLANT = 1;

private static final int WEIGHTS = 2;

private static final int MIN_DURATION = 3;

public static Interpolant makeInterpolant(Polylist L) {
   //  System.out.println("hello Intrp" + L);
   float problemchild = ((Number)L.third()).floatValue();
    Interpolant interp =  new Interpolant((Polylist)L.first(), (String)L.second(),problemchild, (int)L.last());
  //  System.out.println("hello interp" + interp);
    return interp;
}

public static Interpolant makeInterpolantFromExp(Polylist L) {
    Polylist original = L;
    
    Interpolant interp = new Interpolant();

    while( L.nonEmpty() )
    {
        Polylist item = (Polylist)L.first();
        L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case TARGET:
        {
           if( item == null || item.isEmpty() || item.first().equals("") )
           {
               interp.setError("No target in interpolate expression");
               break;
           }
           else if (item.first() instanceof Polylist) {
               interp.setTARGETS((Polylist)item.first());
           }
           break;
        }
      case INTERPOLANT:
        {
          if( item == null || item.isEmpty() || item.first().equals("") )
           {
               interp.setError("No insert in interpolate expression");
               break;
           }
           else if (item.first() instanceof String) {
               interp.setINSERT((String)item.first());
           }
           else {
               interp.setError("Unrecognized insert in interpolate expression");
               break;
           } 
         
        break;
        }
          
      case WEIGHTS:
        {
        try
          {
          Number w = (Number)item.first();
          interp.WEIGHT = w.floatValue();
     
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
          
        //System.out.println("pushAmount " + pushString + " = " + cp.pushAmount + " slots");
          }
        break;
        }
          
      default:
          interp.setError("Error in chord pattern " + original);
          return interp;
      }
    }
  //System.out.println("makeInterpolantfromExp on " + original + " returns " + interp);
  return interp;
  }



public Polylist makeInterpolantList() {
    Polylist interpList = Polylist.list(TARGETS, INSERT, WEIGHT, MINSLOTS);
    return interpList;
}

public Polylist getTARGETS() {
    return TARGETS;
}

public void setTARGETS(Polylist TARGETS) {
    this.TARGETS = TARGETS;
}

public String getINSERT() {
    return INSERT;
}

public void setINSERT(String INSERT) {
    this.INSERT = INSERT;
}

public float getWEIGHT() {
    return WEIGHT;
}

public void setWEIGHT(float WEIGHT) {
    System.out.println("in setweight");
    this.WEIGHT = WEIGHT;
}

public int getMINSLOTS() {
    return MINSLOTS;
}

public void setMINSLOTS(int MINSLOTS) {
    this.MINSLOTS = MINSLOTS;
}


public String toString() {
    return ""+makeInterpolantList();
}

}
