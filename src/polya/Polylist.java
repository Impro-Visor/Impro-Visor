/**
 * Polya library: Implements Lisp-like structures in Java.
 *
 * Copyright (C) 2009-2014 Robert Keller
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

import java.io.Serializable;
import java.io.StringReader;
import java.util.Enumeration;

/**   
<pre>
Polylist is the basic unit for constructing an open linked list of Objects.
A Polylist can be either :
empty, or
non-empty, in which case it consists of:
a Object as the first thing in the list
a rest which is a Polylist.
A Polylist can also be a Incremental, which may be converted into an
ordinary Polylist by applying methods such as isEmpty(), first(), and
rest(). 
</pre>
 */

public class Polylist implements Serializable
{
/**
 *  nil is the empty-list constant
 */
public static final Polylist nil = new Polylist();

private polycell ptr;


/**
 *  construct empty Polylist
 */
public Polylist()
  {
  ptr = null;
  }


/**
 *  construct non-empty Polylist from a First and Rest
 */
public Polylist(Object First, Polylist Rest)
  {
  ptr = new polycell(First, Rest);
  }


/**
 *  construct non-empty Polylist from a First and a Seed for the Rest
 */
public Polylist(Object First, Seed Rest)
  {
  ptr = new polycell(First, Rest);
  }


/**
 *  isEmpty() tells whether the Polylist is empty.
 */
public boolean isEmpty()
  {
  return ptr == null;
  }


/**
 *  nonEmpty() tells whether the Polylist is non-empty.
 */
public boolean nonEmpty()
  {
  return ptr != null;
  }


/**
 *  first() returns the first element of a non-empty Polylist.
 * @exception NullPointerException Can't take first of an empty Polylist.
 *
 */
public Object first()
  {
  return ptr.first();
  }


/**
 *  last() returns the last element of a non-empty Polylist.
 * @exception NullPointerException Can't take last of an empty Polylist.
 *
 */
public Object last()
  {
  Polylist L = this;
  while( L.nonEmpty() )
    {
    if( L.rest().isEmpty() )
      {
      return L.first();
      }
    L = L.rest();
    }
  return null; // should not occur
  }


/**
 *  allButLast() returns all but the last element of a non-empty Polylist.
 * @exception NullPointerException Can't take allButLast of an empty Polylist.
 *
 */
public Polylist allButLast()
  {
  if( rest().isEmpty() )
    {
    return nil;
    }

  return rest().allButLast().cons(first());
  }


/**
 *  replaceLast(newLast) returns a list similar to the original, but with a different last element
 * @exception NullPointerException Can't take allButLast of an empty Polylist.
 *
 */
public Polylist replaceLast(Object newLast)
  {
  if( rest().isEmpty() )
    {
    return list(newLast);
    }

  return rest().replaceLast(newLast).cons(first());
  }


/**
 *  addToEnd(newLast) returns a new list with a new last element at the end.
 */
public Polylist addToEnd(Object newLast)
  {
  if( isEmpty() )
    {
    return list(newLast);
    }

  return rest().addToEnd(newLast).cons(first());
  }


/**
 *  setFirst() sets the first of a list to an object
 * @exception NullPointerException Can't take first of an empty Polylist.
 *
 */
public void setFirst(Object ob)
  {
  ptr.setFirst(ob);
  }


/**
 *  rest() returns the rest of a non-empty Polylist.
 * @exception NullPointerException Can't take rest of an empty Polylist.
 */
public Polylist rest()
  {
  return ptr.rest();
  }


/**
 *  toString() converts Polylist to string, e.g. for printing
 */
@Override
public String toString()
  {
  StringBuilder buff = new StringBuilder();

  buff.append("(");

  // See if this is an incremental list; if so, show ...

  if( this instanceof Incremental && !((Incremental)this).grown() )
    {
    buff.append("...");
    }
  else if( nonEmpty() )
    {
    buff.append(first().toString());
    Polylist L = rest();

    // print the rest of the items

    for(;;)
      {
      if( L instanceof Incremental && !((Incremental)L).grown() )
        {
        buff.append(" ...");
        break;
        }
      if( L.isEmpty() )
        {
        break;
        }
      buff.append(" ");
      buff.append(L.first().toString());
      L = L.rest();
      }
    }
  buff.append(")");
  return buff.toString();
  }


/**
 *  toString() converts Polylist to string, e.g. for printing
 */
public String toStringSansParens()
  {
  StringBuilder buff = new StringBuilder();

  // See if this is an incremental list; if so, show ...

  if( this instanceof Incremental && !((Incremental)this).grown() )
    {
    buff.append("...");
    }
  else if( nonEmpty() )
    {
    buff.append(first());
    Polylist L = rest();

    // print the rest of the items

    for(;;)
      {
      if( L instanceof Incremental && !((Incremental)L).grown() )
        {
        buff.append(" ...");
        break;
        }
      if( L.isEmpty() )
        {
        break;
        }
      buff.append(" ");
      buff.append(L.first().toString());
      L = L.rest();
      }
    }
  return buff.toString();
  }


/**
 *  cons returns a new Polylist given a First and this as a Rest
 */
public Polylist cons(Object First)
  {
  return new Polylist(First, this);
  }


/**
 *  static cons returns a new Polylist given a First and a Rest.
 */
public static Polylist cons(Object First, Polylist Rest)
  {
  return Rest.cons(First);
  }


/**
 *  This variant of cons takes a Seed instead of a Polylist as rest, so
 *  the list can be grown incrementally.
 */
public static Polylist cons(Object First, Seed Rest)
  {
  return new Polylist(First, Rest);
  }


/**
 *  PolylistFromEnum makes a Polylist out of any Enumeration.
 */
public static Polylist PolylistFromEnum(java.util.Enumeration e)
  {
  if( e.hasMoreElements() )
    {
    return cons(e.nextElement(), PolylistFromEnum(e));
    }
  else
    {
    return nil;
    }
  }


/**
 *  return a list of no elements
 */
public static Polylist list()
  {
  return nil;
  }


/**
 *  return a list of one element
 */
public static Polylist list(Object A)
  {
  return cons(A, nil);
  }


/**
 *  return a list of two elements
 */
public static Polylist list(Object A, Object B)
  {
  return cons(A, cons(B, nil));
  }


/**
 *  return a list of three elements
 */
public static Polylist list(Object A, Object B, Object C)
  {
  return cons(A, cons(B, cons(C, nil)));
  }


/**
 *  return a list of four elements
 */
public static Polylist list(Object A, Object B, Object C, Object D)
  {
  return cons(A, cons(B, cons(C, cons(D, nil))));
  }


/**
 *  return a list of five elements
 */
public static Polylist list(Object A, Object B, Object C, Object D, Object E)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, nil)))));
  }


/**
 *  return a list of six elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, nil))))));
  }


/**
 *  return a list of seven elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, cons(G, nil)))))));
  }


/**
 *  return a list of eight elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H)
  {
  return cons(A, cons(B,
          cons(C, cons(D, cons(E, cons(F, cons(G, cons(H, nil))))))));
  }


/**
 *  return a list of nine elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I)
  {
  return cons(A, cons(B, cons(C,
          cons(D, cons(E, cons(F, cons(G, cons(H, cons(I, nil)))))))));
  }


/**
 *  return a list of ten elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J)
  {
  return cons(A, cons(B, cons(C, cons(D,
          cons(E, cons(F, cons(G, cons(H, cons(I, cons(J, nil))))))))));
  }


/**
 *  return a list of eleven elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E,
          cons(F, cons(G, cons(H, cons(I, cons(J, cons(K, nil)))))))))));
  }


/**
 *  return a list of twelve elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K, Object L)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F,
          cons(G, cons(H, cons(I, cons(J, cons(K, cons(L, nil))))))))))));
  }


/**
 *  return a list of thirteen elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K, Object L,
                            Object M)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, cons(G, cons(H,
          cons(I, cons(J, cons(K, cons(L, cons(M, nil)))))))))))));
  }


/**
 *  return a list of fourteen elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K, Object L,
                            Object M, Object N)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, cons(G, cons(H,
          cons(I, cons(J, cons(K, cons(L, cons(M, cons(N, nil))))))))))))));
  }


/**
 *  return a list of fifteen elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K, Object L,
                            Object M, Object N, Object O)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, cons(G, cons(H,
          cons(I, cons(J, cons(K, cons(L, cons(M, cons(N,
          cons(O, nil)))))))))))))));
  }


/**
 *  return a list of sixteen elements
 */
public static Polylist list(Object A, Object B, Object C, Object D,
                            Object E, Object F, Object G, Object H,
                            Object I, Object J, Object K, Object L,
                            Object M, Object N, Object O, Object P)
  {
  return cons(A, cons(B, cons(C, cons(D, cons(E, cons(F, cons(G, cons(H,
          cons(I, cons(J, cons(K, cons(L, cons(M, cons(N, cons(O,
          cons(P, nil))))))))))))))));
  }


/**
 *  return the length of this list
 */
public int length()
  {
  int len = 0;
  for( Enumeration e = elements(); e.hasMoreElements(); e.nextElement() )
    {
    len++;
    }
  return len;
  }


/**
 *  elements() returns a PolylistEnum object, which implements the
 *  interface java.util.Enumeration.
 */
public PolylistEnum elements()
  {
  return new PolylistEnum(this);
  }


/**
 *  first(L) returns the first element of its argument.
 * @exception NullPointerException Can't take first of empty List.
 */
static public Object first(Polylist L)
  {
  return L.first();
  }


/**
 *  rest(L) returns the rest of its argument.
 * @exception NullPointerException Can't take rest of empty Polylist.
 */
static public Polylist rest(Polylist L)
  {
  return L.rest();
  }


/**
 *  reverse(L) returns the reverse of this
 */
public Polylist reverse()
  {
  Polylist rev = nil;
  for( Enumeration e = elements(); e.hasMoreElements();)
    {
    rev = cons(e.nextElement(), rev);
    }
  return rev;
  }


/**
 *  append(M) returns a Polylist consisting of the elements of this
 *  followed by those of M.
 */
public Polylist append(Polylist M)
  {
  if( isEmpty() )
    {
    return M;
    }
  else
    {
    return cons(first(), rest().append(M));
    }
  }


/**
 *  member(A, L) tells whether A is a member of this
 */
public boolean member(Object A)
{
    for( Enumeration e = elements(); e.hasMoreElements();)
    {
        Object next = e.nextElement();
        if(A == null)
        {
            if(next == null)
                return true;
        }
        else if(next != null && ( A.equals(next) || Arith.equal(next, A) ))
        {
          return true;
        }
    }
    return false;
}


/**
 *  range(M, N) returns a Polylist of the form (M M+1 .... N)
 */
public static Polylist range(long M, long N)
  {
  if( M > N )
    {
    return nil;
    }
  else
    {
    return cons(new Long(M), range(M + 1, N));
    }
  }


/**
 *  range(M, N, S) returns a Polylist of the form (M M+S .... N)
 */
public static Polylist range(long M, long N, long S)
  {
  if( S >= 0 )
    {
    return rangeUp(M, N, S);
    }
  else
    {
    return rangeDown(M, N, S);
    }
  }


/**
 *  rangeUp(M, N, S) is an auxiliary function for range
 */
static Polylist rangeUp(long M, long N, long S)
  {
  if( M > N )
    {
    return nil;
    }
  else
    {
    return cons(new Long(M), rangeUp(M + S, N, S));
    }
  }


/**
 *  rangeDown(M, N, S) is auxiliary function for range
 */
static Polylist rangeDown(long M, long N, long S)
  {
  if( M < N )
    {
    return nil;
    }
  else
    {
    return cons(new Long(M), range(M + S, N, S));
    }
  }


/**
 *  second selects the second element of a Polylist.
 * @exception NullPointerException Can't take second of Polylist.
 */
public Object second()
  {
  return rest().first();
  }


/**
 *  third selects the third element of a Polylist.
 * @exception NullPointerException Can't take third of Polylist.
 */
public Object third()
  {
  return rest().rest().first();
  }


/**
 *  fourth selects the fourth element of a Polylist.
 * @exception NullPointerException Can't take fourth of Polylist.
 */
public Object fourth()
  {
  return rest().rest().rest().first();
  }


/**
 *  fifth selects the fifth element of a Polylist.
 * @exception NullPointerException Can't take fifth of Polylist
 */
public Object fifth()
  {
  return rest().rest().rest().rest().first();
  }


/**
 *  sixth selects the sixth element of a Polylist.
 * @exception NullPointerException Can't take sixth of Polylist
 */
public Object sixth()
  {
  return rest().rest().rest().rest().rest().first();
  }


/**
 *  nth selects Polylist item by index (0, 1, 2, ...).
 * @exception NullPointerException Can't select from an empty Polylist.
 */
public Object nth(long n)
  {
  Polylist L = this;
  while( n-- > 0 )
    {
    L = L.rest();
    }
  return L.first();
  }


public void setNth(long n, Object o)
  {
  Polylist L = this;
  while( n-- > 0 )
    {
    L = L.rest();
    }
  L.setFirst(o);
  }


/**
 *   prefix creates the length-n prefix of a Polylist.
 */
public Polylist prefix(long n)
  {
  if( n <= 0 || isEmpty() )
    {
    return nil;
    }
  else
    {
    return cons(first(), rest().prefix(n - 1));
    }
  }

/**
 *   flatten nesting Polylists
 */
private static Polylist flatten(Object l)
  {
  if( l instanceof Polylist && ((Polylist)l).isEmpty() )
    {
    return nil;
    }
  else if(!(l instanceof Polylist))
    {
    return new Polylist(l, new Polylist());
    }
  else
  {
      return Polylist.flatten(((Polylist)l).first()).append(Polylist.flatten(((Polylist)l).rest()));
  }
  }

public Polylist flatten()
  {
  return flatten(this);
  }
/**
 *   coprefix creates the Polylist with all but the length-n prefix of 
 *   a Polylist
 */
public Polylist coprefix(long n)
  {
  if( n <= 0 || isEmpty() )
    {
    return this;
    }
  else
    {
    return rest().coprefix(n - 1);
    }
  }


/**
 *  equals(L, M) tells whether Polylists L and M are equal
 */
public static boolean equals(Polylist L, Polylist M)
  {
  Enumeration e = L.elements();
  Enumeration f = M.elements();

  while( e.hasMoreElements() && f.hasMoreElements() )
    {
    if( !Arith.equal(e.nextElement(), f.nextElement()) )
      {
      return false;
      }
    }
  return !(e.hasMoreElements() || f.hasMoreElements());
  }


/**
 *  equals(M) tells whether this Polylist is equal to some other Object
 */
public boolean equals(Object M)
  {
  if( M instanceof Polylist )
    {
    return equals(this, (Polylist)M);
    }
  else
    {
    return false;
    }
  }


/**
 * Convert a String to a Polylist.
 * It is assumed that the String contains 0 or more S expressions
 * A Polylist will be returned with one element per S expression.
 * Note that if the string is empty, Polylist.nil will be returned.
 * If it has just one S expression, then a list of the corresponding
 * Item will be returned.
 */
static public Polylist PolylistFromString(String arg)
  {
  StringReader reader = new StringReader(arg);

  Tokenizer in = new Tokenizer(reader);

  Polylist L = Polylist.nil;

  Object ob;

  while( (ob = in.nextSexp()) != Tokenizer.eof )
    {
    L = L.cons(ob);
    }

  return L.reverse();
  }


/**
 *  test program for Polylists etc.
 */
static public void main(String args[])
  {
  System.out.println("Testing Polylist");

  Polylist x = list(new Long(3), new Long(4));

  // x = (3 4)


  x = list(new Long(2), x);

  // x = (2 (3 4))


  x = list(list(new Long(1)), x);

  // x = ((1) (2 (3 4)))


  x = cons(new Long(0), x);

  // x = (0 (1) (2 (3 4)))


  x = x.append(range(5, 9));

  // x = (0 (1) (2 (3 4)) 5 6 7 8 9)


  // print using the default style (S expression)

  System.out.println(x);

  // prints (0 (1) (2 (3 4)) 5 6 7 8 9)


  // print only the third element

  System.out.println(x.third());

  // prints (2 (3 4))


  // print only the fourth element

  System.out.println(x.nth(3));

  // prints 5


  Polylist q = x.reverse();

  System.out.println(q);

  // prints (9 8 7 6 5 (2 (3 4)) (1) 0)


  System.out.println(x.nth(2).equals(q.nth(5)));

  // prints true


  // print first 2 elements

  System.out.println(q.prefix(2));

  // prints (9 8)


  // check out getting enumeration from a list

  System.out.println("Printed using an enumeration:");

  for( Enumeration e = q.elements(); e.hasMoreElements();)
    {
    System.out.print(e.nextElement() + "  ");
    }
  System.out.println();

  // check out ListFromEnum

  /*
  Stack s = new Stack();
  for( int i = 0; i < 20; i++ )
  s.push(new Integer(i));
  
  Polylist z = PolylistFromEnum(s.elements());
  
  System.out.println(z);
  
  System.out.println(z.member(new Long(9)));
  
  System.out.println("From array: ");
  
  Object[] a = z.array();
  
  for( int i = 0; i < a.length; i++ )
  System.out.print(a[i] + " ");
  
  System.out.println();
  
  System.out.println(PolylistFromArray(a));
  
   */

  Polylist nitt = explode("Now is the time");

  System.out.println(nitt);

  System.out.println(nitt.implode());

  System.out.println(list("foo", "bar", new Long(123)).implode());

  try
    {
    System.out.println(nil.first());
    }
  catch( NullPointerException e )
    {
    System.out.println("NullPointerException check on first() passed");
    }

  try
    {
    System.out.println(nil.rest());
    }
  catch( NullPointerException e )
    {
    System.out.println("NullPointerException check on rest() passed");
    }

  System.out.println("Type in S expressions for analysis");

  Tokenizer in = new Tokenizer(System.in);

  Object ob;
  while( (ob = in.nextSexp()) != Tokenizer.eof )
    {
    System.out.println(analysis(ob));
    }

  /*
  
  System.out.println("Type in R expressions for analysis");
  
  in = new Tokenizer(System.in);
  
  while( (ob = in.nextRexp()) != Tokenizer.eof )
  {
  System.out.println(analysis(ob));
  }
   */
  System.out.println("Test completed");
  }


/**
 *  analysis produces a string analyzing objects, especially Polylists
 */
public static String analysis(Object Ob)
  {
  return analysis(Ob, 0);
  }


/**
 *  produce an analysis of this Polylist
 */
String analysis()
  {
  return analysis(0);
  }


/**
 *  produce an analysis of this Polylist, indenting N spaces
 */
String analysis(int N)
  {
  if( isEmpty() )
    {
    return spaces(N) + "The empty Polylist\n";
    }
  StringBuilder buff = new StringBuilder();
  buff.append(spaces(N));
  int len = length();
  buff.append("A Polylist consisting of ");
  buff.append(len);
  buff.append(" element");
  buff.append(len > 1
          ? "s"
          : "");
  buff.append(": \n");
  Polylist L = this;
  for( Enumeration e = elements(); e.hasMoreElements();)
    {
    buff.append(analysis(e.nextElement(), N + 1));
    }
  return buff.toString();
  }


/**
 *  produce an analysis of the first argument, indenting N spaces
 */
static String analysis(Object Ob, int N)
  {
  if( Ob instanceof Polylist )
    {
    return ((Polylist)Ob).analysis(N);
    }
  else
    {
    return spaces(N) + Ob.toString() + " (class " + Ob.getClass().getName() + ")\n";
    }
  }


/**
 * Indent N spaces.
 */
static String spaces(int N)
  {
  StringBuilder buff = new StringBuilder();
  while( N > 0 )
    {
    buff.append("  ");
    N--;
    }
  return buff.toString();
  }


/**
 * array() returns an array of elements in list
 */
public Object[] array()
  {
  Object[] result = new Object[length()];
  int i = 0;
  for( Enumeration e = elements(); e.hasMoreElements();)
    {
    result[i++] = e.nextElement();
    }
  return result;
  }

/**
 * toStringArray() returns an Object[] containing a String[] of elements in list
 */
public Object[] toStringArray()
  {
  Object[] result = new String[length()];
  int i = 0;
  for( Enumeration e = elements(); e.hasMoreElements();)
    {
    result[i++] = e.nextElement();
    }
  return result;
  }


/**
 * PolylistFromArray makes a list out of an array of objects
 */
public static Polylist PolylistFromArray(Object array[])
  {
  Polylist result = nil;
  for( int i = array.length - 1; i >= 0; i-- )
    {
    result = cons(array[i], result);
    }
  return result;
  }


/**
 * explode(String S) converts a string into a Polylist of Character
 */
public static Polylist explode(String S)
  {
  Polylist result = nil;
  for( int i = S.length() - 1; i >= 0; i-- )
    {
    result = cons(new Character(S.charAt(i)), result);
    }
  return result;
  }


/**
 * implode() creates a String from a Polylist of items
 */
public String implode()
  {
  StringBuilder buff = new StringBuilder();
  for( Enumeration e = elements(); e.hasMoreElements();)
    {
    buff.append(e.nextElement().toString());
    }
  return buff.toString();
  }


public String implode(String separator)
  {
  StringBuilder buff = new StringBuilder();
  Enumeration e = elements();
  if( !e.hasMoreElements() )
    {
    return "";
    }
  buff.append(e.nextElement());
  while( e.hasMoreElements() )
    {
    buff.append(separator);
    buff.append(e.nextElement().toString());
    }
  return buff.toString();
  }


/**
 * map maps an object of class Function1 over a Polylist returning a 
 * Polylist
 */
public Polylist map(Function1 F)
  {
  if( isEmpty() )
    {
    return nil;
    }
  else
    {

    return cons(F.apply(first()), rest().map(F));
    }
  }


/**
 * reduce reduces a Polylist by a Function2 object, with unit
 * 
 */
public Object reduce(Function2 F, Object unit)
  {
  Object result = unit;
  Polylist L = this;

  for(; L.nonEmpty(); L = L.rest() )
    {
    result = F.apply(result, L.first());
    }

  return result;
  }


public static Polylist assoc(Object ob, Polylist alist)
  {
  while( alist.nonEmpty() )
    {
    if( alist.first() instanceof Polylist )
      {
      Polylist first = (Polylist)alist.first();

      if( first.nonEmpty() && ob.equals(first.first()) )
        {
        return first;
        }
      }
    alist = alist.rest();
    }

  return null;
  }


public Polylist assoc(Object ob)
  {
  return assoc(ob, this);
  }

}  // class Polylist
