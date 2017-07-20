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

import imp.data.Leadsheet;
import java.io.Serializable;
import polya.Polylist;

/**
 *
 * @author Isys Johnson
 */
public class Interpolable 
    extends Pattern implements Serializable
{
    float PROBABILITY;
    
    public Interpolable()        
  {
    
  }
    public Interpolable(float weight)
  {
       
       PROBABILITY = weight;
       
  }

private static final String keyword[] =
  {
    "weight"
  };

// indices into the keyword array
private static final int WEIGHT = 0;

public Interpolable makeInterpolable(Polylist L) {
   //  System.out.println("hello Intrp" + L);
   
    Interpolable interp =  new Interpolable((float)L.first());
  //  System.out.println("hello interp" + interp);
    return interp;
}

public static Interpolable makeInterpolableFromExp(Polylist L) {
    Polylist original = L;
    
    Interpolable interp = new Interpolable();

    while( L.nonEmpty() )
    {
        Polylist item = (Polylist)L.first();
        L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      
          
      case WEIGHT:
        {
        try
          {
              
          Number w = (Number)item.first();
          
          interp.PROBABILITY = w.floatValue();
     //System.out.println(interp.PROBABILITY);
          break;
          }
        catch( Exception e )
          {
            interp.setError("Expected weight value, but found " + item.first()
                      + " in " + original);
          }
        break;
        }
          
      
          
      default:
          interp.setError("Error in interpolable " + original);
          return interp;
      }
    }
  //System.out.println("makeInterpolantfromExp on " + original + " returns " + interp);
  return interp;
  }

public Polylist makeInterpolableList() {
    Polylist interpList = Polylist.list(PROBABILITY);
    return interpList;
}

    public float getPROBABILITY() {
        return PROBABILITY;
    }

    public void setPROBABILITY(float PROBABILITY) {
        this.PROBABILITY = PROBABILITY;
    }
    
    public String toString() {
    return ""+makeInterpolableList();
}
    
}
