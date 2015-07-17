/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import polya.Polylist;

/**
 *
 * @author keller
 */

public class DrumRuleRep
{

/**
 * Element represents one element of a rule.
 * It consists of a type and a suffix.
 * For example, if the rule is X4+8, then the type is 'X' and the suffix is "4+8".
 * For X and R types, the suffix represents a duration string.
 * For V types, the suffix represents a volume setting 0-127.
 */
public class Element
{
char elementType;
String suffix;

Element(char elementType, String suffix)
  {
    this.elementType = elementType;
    this.suffix = suffix;
  }

public char getType()
  {
    return elementType;
  }

public String getSuffix()
  {
    return suffix;
  }

public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(elementType);
        buffer.append(suffix);
        return buffer.toString();
    }
} // end of inner class Element


private int drumNumber;

private ArrayList<Element> elements;

private LinkedHashMap<String, Polylist> definedRules = 
        new LinkedHashMap<String, Polylist>();

private Polylist ruleAsList;

private String ruleName = "";

private static String keyword[] = 
{
    "name", "rules", "use"
};
        
private static final int NAME = 0;
private static final int RULES = 1;
private static final int USE = 2;

/**
 * If the raw Polylist was erroneous, errorMessage will be set.
 */

private String errorMessage = null;


/**
 * Construct an empty DrumRuleRep
 */
public DrumRuleRep()
{
    elements = new ArrayList<Element>();
}

/**
 * Construct a DrumRuleRep from a String
 * @param rawString 
 */

public DrumRuleRep(String rawString)
  {
    this(Polylist.PolylistFromString(rawString));
  }


/**
 * Construct a DrumRuleRep from an S-expression
 * @param raw 
 */

public DrumRuleRep(Polylist raw)
  {
    Polylist original = raw;
    
     Object first = raw.first();
    
    if( first instanceof Number )
      {
        drumNumber = ((Number) first).intValue();
      }
    else if( first instanceof String )
      {
        drumNumber = MIDIBeast.numberFromSpacelessDrumName((String)first);
      }
    else
      {
        errorMessage = "The first element needs to be a drum instrument "
                     + "number or one of the standard names for the instrument "
                     + "with _ rather than spaces: " + raw;
      }

    raw = raw.rest();

    ruleAsList = raw;
    
    elements = new ArrayList<Element>();

    while( raw.nonEmpty() )
      {
        Object ob = raw.first();
        
        if( ob instanceof Polylist )
        {
            Polylist item = (Polylist)ob;
            String dispatcher = (String)item.first();
            item = item.rest();
            
            switch( Leadsheet.lookup(dispatcher, keyword) )
            {
                case NAME:
                {
                    if( item.isEmpty() || item.first().equals("") )
                    {
                        break;
                    }
                    else if( item.first() instanceof String )
                    {
                        ruleName = (String) item.first();
                    }
                    else
                    {
                        setError("Unrecognized name type in drum rule: " + item.first()); 
                    }
                    break;
                }
                case USE:
                {
                    if( item.first() instanceof String )
                    {
                        String name = (String)item.first();
                        ruleName = name;
                        LinkedHashMap ruleDefinitions = getDefinedRules();
                        Polylist rules = (Polylist)ruleDefinitions.get(name);
                        String start = (String)rules.first();
                        if( Leadsheet.lookup(start, keyword) == RULES )
                        {
                            rules = rules.rest();
                            while( rules.nonEmpty() )
                            {
                                Object entry = rules.first();
                        rules = rules.rest();
                        
                        if( entry instanceof String )
                        {
                            String s = (String)entry;
                            
                            char type = s.charAt(0);
                            String suffix = s.substring(1);
            
                            switch( type )
                            {
                                case DrumPattern.DRUM_STRIKE:
                                case DrumPattern.DRUM_REST:
                                {
                                    int duration = Duration.getDuration0(suffix);
                                    if( duration <= 0 )
                                    {
                                        errorMessage = "The duration in " + s
                                            + " is invalid in " + original;
                                    }
                                }
                                break;
                    
                                case DrumPattern.DRUM_VOLUME:
                                {
                                    VolumeSymbol vs = new VolumeSymbol(s);
                                    if( vs == null )
                                    {
                                        errorMessage = "Error in volume symbol in"  + original;
                                        break;
                                    }
                                }
                                    break;
                    
                                default:
                                    errorMessage = "Each pattern element must begin with one "
                                            + "of 'X', 'R', or 'V', but this one begins with '" 
                                            + type + "': " + original;                   
                            }
                            elements.add(new Element(type, suffix));
                         }
                            }
                        }
                    }
                    break;
                }                
                case RULES:
                {
                    while( item.nonEmpty() )
                    {
                        Object entry = item.first();
                        item = item.rest();
                        
                        if( entry instanceof String )
                        {
                            String s = (String)entry;
                            
                            char type = s.charAt(0);
                            String suffix = s.substring(1);
            
                            switch( type )
                            {
                                case DrumPattern.DRUM_STRIKE:
                                case DrumPattern.DRUM_REST:
                                {
                                    int duration = Duration.getDuration0(suffix);
                                    if( duration <= 0 )
                                    {
                                        errorMessage = "The duration in " + s
                                            + " is invalid in " + original;
                                    }
                                }
                                break;
                    
                                case DrumPattern.DRUM_VOLUME:
                                {
                                    VolumeSymbol vs = new VolumeSymbol(s);
                                    if( vs == null )
                                    {
                                        errorMessage = "Error in volume symbol in"  + original;
                                        break;
                                    }
                                }
                                    break;
                    
                                default:
                                    errorMessage = "Each pattern element must begin with one "
                                            + "of 'X', 'R', or 'V', but this one begins with '" 
                                            + type + "': " + original;                   
                            }
                            elements.add(new Element(type, suffix));
                         }
                    }
                    
                 }
             }    
        }
        else if( ob instanceof String )
          {
            String s = (String) ob;

            char type = s.charAt(0);
            String suffix = s.substring(1);
            
            switch( type )
              {
                case DrumPattern.DRUM_STRIKE:
                case DrumPattern.DRUM_REST:
                  {
                    int duration = Duration.getDuration0(suffix);
                    if( duration <= 0 )
                      {
                        errorMessage = "The duration in " + s
                                + " is invalid in " + original;
                      }
                  }
                    break;
                    
                case DrumPattern.DRUM_VOLUME:
                  {
                    VolumeSymbol vs = new VolumeSymbol(s);
                    if( vs == null )
                      {
                        errorMessage = "Error in volume symbol in"  + original;
                        break;
                      }
                  }
                    break;
                    
                default:
                     errorMessage = "Each pattern element must begin with one "
                            + "of 'X', 'R', or 'V', but this one begins with '" 
                            + type + "': " + original;                   
                }
          elements.add(new Element(type, suffix));
          }
        else
          {
            errorMessage = "Pattern does not begin correctly: " + original;
            
          }
        raw = raw.rest();
      }
  }


/**
 * Makes a rule out of a polylist
 * @param L
 * @return 
 */
public DrumRuleRep makeDrumRuleRep(Polylist L)
{
    Polylist original = L;
    
     Object first = L.first();
    
    if( first instanceof Number )
      {
        drumNumber = ((Number) first).intValue();
      }
    else if( first instanceof String )
      {
        drumNumber = MIDIBeast.numberFromSpacelessDrumName((String)first);
      }
    else
      {
        errorMessage = "The first element needs to be a drum instrument "
                     + "number or one of the standard names for the instrument "
                     + "with _ rather than spaces: " + L;
      }

    L = L.rest();

    ruleAsList = L;
    
    elements = new ArrayList<Element>();

    while( L.nonEmpty() )
      {
        Object ob = L.first();
        
        if( ob instanceof Polylist )
        {
            Polylist item = (Polylist)ob;
            String dispatcher = (String)item.first();
            item = item.rest();
            
            switch( Leadsheet.lookup(dispatcher, keyword) )
            {
                case NAME:
                {
                    if( item.isEmpty() || item.first().equals("") )
                    {
                        break;
                    }
                    else if( item.first() instanceof String )
                    {
                        ruleName = (String) item.first();
                    }
                    else
                    {
                        setError("Unrecognized name type in drum rule: " + item.first()); 
                    }
                    break;
                }                   
                case USE:
                {
                    if( item.first() instanceof String )
                    {
                        String name = (String)item.first();
                        ruleName = name;
                        LinkedHashMap ruleDefinitions = getDefinedRules();
                        Polylist rules = (Polylist)ruleDefinitions.get(name);
                        String start = (String)rules.first();
                        if( Leadsheet.lookup(start, keyword) == RULES )
                        {
                            rules = rules.rest();
                            while( rules.nonEmpty() )
                            {
                                Object entry = rules.first();
                        rules = rules.rest();
                        
                        if( entry instanceof String )
                        {
                            String s = (String)entry;
                            
                            char type = s.charAt(0);
                            String suffix = s.substring(1);
            
                            switch( type )
                            {
                                case DrumPattern.DRUM_STRIKE:
                                case DrumPattern.DRUM_REST:
                                {
                                    int duration = Duration.getDuration0(suffix);
                                    if( duration <= 0 )
                                    {
                                        errorMessage = "The duration in " + s
                                            + " is invalid in " + original;
                                    }
                                }
                                break;
                    
                                case DrumPattern.DRUM_VOLUME:
                                {
                                    VolumeSymbol vs = new VolumeSymbol(s);
                                    if( vs == null )
                                    {
                                        errorMessage = "Error in volume symbol in"  + original;
                                        break;
                                    }
                                }
                                    break;
                    
                                default:
                                    errorMessage = "Each pattern element must begin with one "
                                            + "of 'X', 'R', or 'V', but this one begins with '" 
                                            + type + "': " + original;                   
                            }
                            elements.add(new Element(type, suffix));
                         }
                            }
                        }
                    }
                    break;
                }               
                case RULES:
                {
                    while( item.nonEmpty() )
                    {
                        Object entry = item.first();
                        item = item.rest();
                        
                        if( entry instanceof String )
                        {
                            String s = (String)entry;
                            
                            char type = s.charAt(0);
                            String suffix = s.substring(1);
            
                            switch( type )
                            {
                                case DrumPattern.DRUM_STRIKE:
                                case DrumPattern.DRUM_REST:
                                {
                                    int duration = Duration.getDuration0(suffix);
                                    if( duration <= 0 )
                                    {
                                        errorMessage = "The duration in " + s
                                            + " is invalid in " + original;
                                    }
                                }
                                break;
                    
                                case DrumPattern.DRUM_VOLUME:
                                {
                                    VolumeSymbol vs = new VolumeSymbol(s);
                                    if( vs == null )
                                    {
                                        errorMessage = "Error in volume symbol in"  + original;
                                        break;
                                    }
                                }
                                    break;
                    
                                default:
                                    errorMessage = "Each pattern element must begin with one "
                                            + "of 'X', 'R', or 'V', but this one begins with '" 
                                            + type + "': " + original;                   
                            }
                            elements.add(new Element(type, suffix));
                         }
                    }
                    
                 }
             }    
        }
        else if( ob instanceof String )
          {
            String s = (String) ob;

            char type = s.charAt(0);
            String suffix = s.substring(1);
            
            switch( type )
              {
                case DrumPattern.DRUM_STRIKE:
                case DrumPattern.DRUM_REST:
                  {
                    int duration = Duration.getDuration0(suffix);
                    if( duration <= 0 )
                      {
                        errorMessage = "The duration in " + s
                                + " is invalid in " + original;
                      }
                  }
                    break;
                    
                case DrumPattern.DRUM_VOLUME:
                  {
                    VolumeSymbol vs = new VolumeSymbol(s);
                    if( vs == null )
                      {
                        errorMessage = "Error in volume symbol in"  + original;
                        break;
                      }
                  }
                    break;
                    
                default:
                     errorMessage = "Each pattern element must begin with one "
                            + "of 'X', 'R', or 'V', but this one begins with '" 
                            + type + "': " + original;                   
                }
          elements.add(new Element(type, suffix));
          }
        else
          {
            errorMessage = "Pattern does not begin correctly: " + original;
            
          }
        L = L.rest();
      }
    return this;
}


public static DrumRuleRep makeDrumRuleRep(String string)
  {
    Polylist L = (Polylist)(Polylist.PolylistFromString(string).first());
    //System.out.println("string = " + string + "\nlist = " + L);
    return new DrumRuleRep(L.rest());
  }

public int getInstrument()
  {
    return drumNumber;
  }

public ArrayList<Element> getElements()
  {
    return elements;
  }

/**
 * Get the duration, in slots
 * @return 
 */
public int getDuration()
  {
    int duration = 0;
    
    for( Element element: elements )
      {
        switch( element.getType() )
          {
            case DrumPattern.DRUM_STRIKE :
                // fall through
            case DrumPattern.DRUM_REST:
                duration += Duration.getDuration(element.getSuffix());
                break;
                
            case DrumPattern.DRUM_VOLUME:
                // ignore volume
                break;
          }
      }
    
    return duration;
  }

@Override
public String toString()
  {
      fixRuleList();
      StringBuilder buffer = new StringBuilder();
      buffer.append("(drum ");
      buffer.append(drumNumber);
      buffer.append(" ");
      buffer.append(ruleAsList.toStringSansParens());
      buffer.append(")");
      return buffer.toString();
  }

public void fixRuleList()
{
        Object ob = ruleAsList.first();
        if( ob instanceof Polylist )
        {
            Polylist item = (Polylist)ob;
            String dispatcher = (String)item.first();
            item = item.rest();
            switch( Leadsheet.lookup(dispatcher, keyword) )
            {
                case USE:
                {
                    if( item.first() instanceof String )
                    {
                        StringBuilder buffer = new StringBuilder();
                        String name = (String)item.first();
                        buffer.append("(name ");
                        buffer.append(name);
                        buffer.append(")");

                        LinkedHashMap ruleDefinitions = getDefinedRules();
                        Polylist rules = (Polylist)ruleDefinitions.get(name);
                        buffer.append(rules.toString());
                        
                        String newString = buffer.toString();
                        //System.out.println("rule list: " + newString);
                        ruleAsList = Polylist.PolylistFromString(newString);                   
                    }
                    break;
                }
                default:
                    break;
            }
        }
}

public String forStyleMixer()
{
    StringBuilder buffer = new StringBuilder();
    buffer.append("");
    buffer.append(drumNumber);
    for( Element e: getElements() )
    {
        buffer.append(" ");
        buffer.append(e.toString());
    }
    return buffer.toString();
}

public boolean getStatus()
  {
    return errorMessage == null;
  }

public String getErrorMessage()
  {
    return errorMessage == null ? "" : errorMessage;
  }

private void setError(String error)
  {
    errorMessage = error;
  }

public String getName()
{
    return ruleName;
}

public void setName(String name)
{
    ruleName = name;
}

public LinkedHashMap getDefinedRules()
{
    return definedRules;
}

public void setDefinedRules(LinkedHashMap map)
{
    if( map.isEmpty() )
    {
        return;
    }
    else
    {
        definedRules = map;
    }
}

}
