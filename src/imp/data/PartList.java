/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.data;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author keller
 */
public class PartList
{
private ArrayList<MelodyPart> theList;

public PartList(int size)
  {
  theList = new ArrayList<MelodyPart>(size);
  }

public ListIterator<MelodyPart> listIterator()
  {
    return theList.listIterator();
  }

public int size()
  {
    return theList.size();
  }

public MelodyPart get(int i)
  {
    return theList.get(i);
  }

/**
 * Caution: set seems not to work, for reasons I don't understand.
 * The capacity doesn't increase as prescribed.
 *
 @param i
 @param part
 */
public void set(int i, MelodyPart part)
  {
    theList.ensureCapacity(i);
    for( int j = theList.size(); j <= i; j++ )
      {
        theList.add(null);
      }
    theList.set(i, part);
  }

public void setSize(int size)
  {
    theList.ensureCapacity(size);
    for( int j = theList.size(); j < size; j++ )
      {
        theList.add(null);
      }
  }

public void add(MelodyPart part)
  {
    theList.add(part);
  }

public void remove(int i)
  {
    theList.remove(i);
  }

public void move(int i1, int i2){
    theList.add(i2,theList.remove(i1));
}
}
