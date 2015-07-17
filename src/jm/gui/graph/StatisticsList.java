/*
 * StatisticsList.java 0.0.1 1st September 2000
 *
 * Copyright (C) 2000 Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.gui.graph;

/**
 * @author  Adam Kirby
 * @version 1.0,Sun Feb 25 18:43
 */
public class StatisticsList implements Cloneable, java.io.Serializable {
    /**
     */
    private transient Statistics elementData[];

    /**
     */
    private int size;

    /**
     */
    protected transient int modCount = 0;

    /**
     */
    public StatisticsList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
        this.elementData = new Statistics[initialCapacity];
    }

    /**
     */
    public StatisticsList() {
        this(10);
    }

    /**
     */
    public void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            Statistics oldData[] = elementData;
            elementData = new Statistics[size];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    /**
     */
    public void ensureCapacity(int minCapacity) {
        modCount++;
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            Statistics oldData[] = elementData;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new Statistics[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    /**
     */
    public int size() {
        return size;
    }

    /**
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     */
    public boolean contains(Statistics elem) {
        return indexOf(elem) >= 0;
    }

    /**
     */
    public int indexOf(Statistics elem) {
        if (elem == null) {
            for (int i = 0; i < size; i++)
            if (elementData[i]==null) {
                return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (elem.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     */
    public int lastIndexOf(Statistics elem) {
        if (elem == null) {
            for (int i = size-1; i >= 0; i--) {
                if (elementData[i]==null) {
                    return i;
                }
            }
        } else {
            for (int i = size-1; i >= 0; i--) {
                if (elem.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     */
    public Object clone() {
        try { 
            StatisticsList sl = (StatisticsList) super.clone();
            sl.elementData = new Statistics[size];
            System.arraycopy(elementData, 0, sl.elementData, 0, size);
            sl.modCount = 0;
            return sl;
        } catch (CloneNotSupportedException e) { 
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     */
    public Statistics[] toArray() {
        Statistics[] result = new Statistics[size];
        System.arraycopy(elementData, 0, result, 0, size);
        return result;
    }

    /**
     */
    public Statistics[] toArray(Statistics a[]) {
        if (a.length < size) {
            a = (Statistics[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    // Positional Access Operations

    /**
     */
    public Statistics get(int index) {
        rangeCheck(index);
        return elementData[index];
    }

    /**
     */
    public Statistics set(int index, Statistics element) {
        rangeCheck(index);
        Statistics oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    /**
     */
    public boolean add(Statistics stats) {
        ensureCapacity(size + 1);  // Increments modCount!!
        elementData[size++] = stats;
        return true;
    }

    /**
     */
    public void add(int index, Statistics element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);
        }
        ensureCapacity(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                 size - index);
        elementData[index] = element;
        size++;
    }

    /**
     */
    public Statistics remove(int index) {
        rangeCheck(index);
        modCount++;
        Statistics oldValue = elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index+1, elementData, index,
                     numMoved);
        }
        elementData[--size] = null; // Let gc do its work
        return oldValue;
    }

    /**
     */
    public void clear() {
        modCount++;
    
        // Let gc do its work
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
    
        size = 0;
    }

    /**
     */
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                             numMoved);
    
        // Let gc do its work
        int newSize = size - (toIndex-fromIndex);
        while (size != newSize) {
            elementData[--size] = null;
        }
    }

    /**
     */
    private void rangeCheck(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException(
                   "Index: "+index+", Size: "+size);
        }
    }

    /**
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();
    
        // Write out array length
        s.writeInt(elementData.length);
    
        // Write out all elements in the proper order.
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
    }

    /**
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        elementData = new Statistics[arrayLength];

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
            elementData[i] = (Statistics) s.readObject();
        }
    }

    // Comparison and Hashing - from AbstractList

    /**
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatisticsList)) {
            return false;
        }

        StatisticsList sl = (StatisticsList) o                                    ;
        if (size == sl.size()) {
            for (int i = 0; i < size; i++) {
                Statistics s1 = get(i);
                Statistics s2 = sl.get(i);
                if (!(s1 == null ? s2 == null : s1.equals(s2))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     */
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size; i++) { 
            Statistics stats = get(i);
            hashCode = 31 * hashCode + (stats == null ? 0 : stats.hashCode());
        }
        return hashCode;
    }

    // Modification Operations - from AbstractCollection

    /**
     */
    public boolean remove(Statistics stats) {
        if (stats == null) {
            for (int i = 0; i < size; i++) {
                if (get(i) == null) {
                    remove(i);
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (stats.equals(get(i))) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    // String conversion - from AbstractCollection

    /**
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        int maxIndex = size() - 1;
        for (int i = 0; i <= maxIndex; i++) {
            buf.append(String.valueOf(get(i)));
            if (i < maxIndex)
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

}
