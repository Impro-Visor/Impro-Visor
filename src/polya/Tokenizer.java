/**
 * Polya library: Implements Lisp-like structures in Java.
 *
 * Copyright (C) 2009-2011 Robert Keller
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

package polya;

import java.io.*;
	
/**
  *  Tokenizer provides a tokenizer of the package polya.
  *  <p>
  *  In addition to tokenizing for values of specific types, an entire
  *  Poly can be read by one method call, assuming the input is in the
  *  for of an S expression or an R expression.
  *  <p>
  *  In an S expression, the elements of a Polylist are shown in parentheses
  *  with a space between each element.  (The elements can themselves be
  *  Polylists.)
  *  <p>
  *  In an R expression, the elements are shown in square brackets, with
  *  an optional comma separator between them.
  */
public class Tokenizer extends StreamTokenizer
  {
  public final static EOF eof = new EOF();

  public long lval;                          // the long value, if used

  public final static int TT_LONG = -4;      // indicates a long token value

  /** construct tokenizer from input stream */

  public Tokenizer(InputStream I)
    {
    this(new InputStreamReader(I));
    }

  /** construct tokenizer from Reader */

  public Tokenizer(Reader R)
    {
    super(new BufferedReader(R));	     // initialize 
    
    resetSyntax();   	                     // These call parent methods
    slashSlashComments(true);
    slashStarComments(true);
    eolIsSignificant(false);
    ordinaryChar('[');
    ordinaryChar(']');
    ordinaryChar(',');
    ordinaryChar('|');
    }    
  

  /** 
    *  exceptionHandler can be over-ridden to handle IO exceptions
    */

  public void exceptionHandler( Exception e )
    {
    System.out.print("polya.Tokenizer caught ");
    if( e instanceof IOException )
      {
      System.out.println("IOException " + e);
      } 
    else if( e instanceof eofException )
      {
      System.out.println("eofException " + e);
      }
    else if( e instanceof NumberFormatException )
      {
      System.out.println("NumberFormatException " + e);
      }
    else 
      {
      System.out.println("exception " + e);
      }
    }

  boolean isWhiteSpace(int c)
    {
    switch( c )
      {
      case ' ': case '\t': case '\n': case '\r': case '\f':
        return true;

      default:
        return false;
      }
    }

  // hand-implemented get/putBack system, since the one in StreamTokenizer
  // doesn't work

  int buff;
  boolean valid = false;

  void get()
    {
    if( valid )
      {
      valid = false;
      ttype = buff;
      return;
      }
    try
      {
      super.nextToken();
      }
    catch( IOException e )
      {
      exceptionHandler(e);
      ttype = TT_EOF;
      }
    }


  void putBack(int c)
    {
    valid = true;
    buff = c;
    }

  public void getSkippingWhiteSpace()
    {
    get();
    while( isWhiteSpace(ttype) )
      {
      get();
      }
    }


  /** 
    * get token, indicating TT_LONG, TT_NUMBER, TT_WORD, or TT_EOF 
    */

  public int nextToken() 
    {
    getSkippingWhiteSpace();
    switch( ttype )	// parens by themselves are tokens
      {
      case '|':
      case ',':
      case '[': case ']': 
      case '(': case ')':
      case TT_EOF: 
        return ttype;
      }

    StringBuffer buff = new StringBuffer();

    buff.append((char)ttype);

    get(); 
out:
    while( ttype != TT_EOF && !isWhiteSpace(ttype) )
      {
      switch( ttype )
	{
        case '|':
        case ',':
	case '[':  case ']': 
	case '(':  case ')':
	  break out;

	default:
	  buff.append((char)ttype);
	}
      get(); 
      }
    putBack(ttype);

  sval = buff.toString();

  try                                  // try token as a long (TT_LONG)
    {
    Long as_long = new Long(sval);
    lval = as_long.longValue();
    ttype = TT_LONG; 
    return ttype;
    }
  catch( NumberFormatException e )
    {                                  // exception converting to long
    try                                // try token as double (TT_NUMBER)
      {
      Double as_double = new Double(sval);
      nval = as_double.doubleValue();
      ttype = TT_NUMBER; 
      return ttype;
      }
    catch( NumberFormatException f )   // exception converting to double
      {                                // default to word (TT_WORD)
      ttype = TT_WORD;
      return ttype;
      }
    }
  }


  /** 
    * get next S expression from input stream 
    * returns an object of class EOF on end-of-file
    */ 

  public Object nextSexp()
    {
    try
      {
      int c = nextToken();
      switch( c )
	{
	case TT_EOF:
	  return eof;

        case ',':
          return ",";
          
        case '|':
          return "|";

	case ')':
	  {
	  return Polylist.nil;
	  }

	case '(':
	  {
	  return getRestSexp();
	  }

	case '[':
	  {
	  return "[";
	  }

	case ']':
	  {
	  return "]";
	  }

	case TT_LONG:
	  return new Long(lval);

	case TT_NUMBER:
	  return new Double(nval);

	default:
	  return new String(sval);
	}
      }
    catch( Exception e )
      {
      exceptionHandler(e);
      }
    return eof;
    }

  Polylist getRestSexp()
    {
    try
      {
      int c = nextToken();

      switch( c )
	{
	case TT_EOF:
	  return Polylist.nil;

        case '|':
	  return Polylist.cons("|", getRestSexp());

        case ',':
	  return Polylist.cons(",", getRestSexp());

	case ')':
	  return Polylist.nil;

	case '(':
	  {
	  return Polylist.cons(getRestSexp(), getRestSexp());
	  }

	case '[':
	  {
	  return Polylist.cons("[", getRestSexp());
	  }

	case ']':
	  {
	  return Polylist.cons("]", getRestSexp());
	  }

	case TT_LONG:
	  return Polylist.cons(new Long(lval), getRestSexp());

	case TT_NUMBER:
	  return Polylist.cons(new Double(nval), getRestSexp());

	default:
	  return Polylist.cons(new String(sval), getRestSexp());
	}
      }
    catch( Exception e )
      {
      exceptionHandler(e);
      }
    return Polylist.nil;
    }


  /** get next R expression from input stream  */ 

  public Object nextRexp() 
    {
    int c = 0;
    try
      {
      c = nextToken();
      }

    catch( Exception e )
      {
      exceptionHandler(e);
      }

    switch( c )
      {
      case '[':
        {
        return getRestRexp();
        }
       
      case TT_EOF:		// shouldn't happen
        {
	return eof;
        }

      case ']':
        {
        return Polylist.nil;
        }
       
/*
      case ',':
        {
        return Polylist.nil;
        }
*/
       
      case '(':
        return new String("(");

      case ')':
        return new String(")");

      case TT_LONG:
        return new Long(lval);

      case TT_NUMBER:
        return new Double(nval);

      default:
        return sval;
      }
    }

  Polylist getRestRexp()
    {
    int c = 0;
    try
      {
      c = nextToken();
      }

    catch( Exception e )
      {
      exceptionHandler(e);
      }

    switch( c )
      {
      case TT_EOF:
        return Polylist.nil;	// shouldn't happen

      case ']':
        return Polylist.nil;

      case '[':
        {
        return Polylist.cons(getRestRexp(), getRestRexp());
        }
       
/*      case ',':
        return getRestRexp();
*/

      case '(':
        return Polylist.cons(new String("("), getRestRexp());

      case ')':
        return Polylist.cons(new String(")"), getRestRexp());

      case TT_LONG:
        return Polylist.cons(new Long(lval), getRestRexp());

      case TT_NUMBER:
        return Polylist.cons(new Double(nval), getRestRexp());

      default:
        return Polylist.cons(sval, getRestRexp());
      }
    }


  /** 
   * Test method:
   * loops through input, printing out each type of token.
   * Terminates on end-of-filek, with test of S expressions, then R 
   *  expressions.
   */

  static public void main(String[] args)
    {
    // Create a tokenizer and set parameters
    Tokenizer input = new Tokenizer(System.in);

    input.eolIsSignificant(true);

    System.out.println("ready");

  outer:
    for( ;; )
      {
      switch( input.nextToken() )
        {
        case Tokenizer.TT_EOF:
          System.out.println("EOF");
          break outer;                       // end-of-file; quit

        case Tokenizer.TT_EOL:
          System.out.println("EOL");
          break;

        case Tokenizer.TT_WORD:
          System.out.print("word: ");
          System.out.println(input.sval);
          break;

        case Tokenizer.TT_NUMBER:
          System.out.print("double: ");
          System.out.println(input.nval);
          System.out.print("as a string: ");           
          System.out.println(input.sval);
          break;

        case Tokenizer.TT_LONG:
          System.out.print("long: ");
          System.out.println(input.lval);
          System.out.print("as a double: ");
          System.out.println(input.nval);
          System.out.print("as a string: ");           
          System.out.println(input.sval);
          break;

        default:
          System.out.print("other: ");
          System.out.println(input.sval);
          break;
        }
      }
    }
  }
