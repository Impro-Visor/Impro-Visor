/*
 * Statistics.java 0.0.1 4th September 2000
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
public class Statistics implements Cloneable, java.io.Serializable {
    /**
     */
    private double elementData[];

    /**
     */
    private double largestValue = 0.0;

    /**
     */
    private int size;

    /**
     */
    public Statistics(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
        this.elementData = new double[initialCapacity];
    }

    /**
     */
    public Statistics() {
        this(100);
    }

    /**
     */
    public void trimToSize() {
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            double oldData[] = elementData;
            elementData = new double[size];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    /**
     */
    public void ensureCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            double oldData[] = elementData;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new double[newCapacity];
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
    public double largestValue() {
        return largestValue;
    }


    /**
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     */
    public boolean contains(double elem) {
        return indexOf(elem) >= 0;
    }

    /**
     */
    public int indexOf(double value) {
        for (int i = 0; i < size; i++) {
            if (value == elementData[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     */
    public int lastIndexOf(double value) {
        for (int i = size-1; i >= 0; i--) {
            if (value == elementData[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     */
    public Object clone() {
        try { 
            Statistics stats = (Statistics) super.clone();
            stats.elementData = new double[size];
            System.arraycopy(elementData, 0, stats.elementData, 0, size);
            return stats;
        } catch (CloneNotSupportedException e) { 
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     */
    public double[] toArray() {
        double[] result = new double[size];
        System.arraycopy(elementData, 0, result, 0, size);
        return result;
    }

    /**
     */
    public double[] toArray(double a[]) {
        if (a.length < size) {
            a = new double[size];
        }                    
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
            a[size] = 0;
        }
        return a;
    }

    // Positional Access Operations

    public double resetLargestValue() {
        largestValue = 0;
        for (int i = 0; i < size; i++) {
            if (get(i) > largestValue) {
                largestValue = get(i);
            }
        }
        return largestValue;
    }

    /**
     */
    public double get(int index) {
        rangeCheck(index);
        return elementData[index];
    }

    /**
     */
    public double set(int index, double element) {
        rangeCheck(index);
        double oldValue = elementData[index];
        elementData[index] = element;
        if (oldValue == largestValue) {
            resetLargestValue();
        } else if (element > largestValue) {
            largestValue = oldValue;
        }
        return oldValue;
    }

    /**
     */
    public boolean add(double stats) {
        ensureCapacity(size + 1);
        elementData[size++] = stats;
        if (stats > largestValue) {
            largestValue = stats;
        }
        return true;
    }

    /**
     */
    public boolean add(double[] stats) {
        ensureCapacity(size + stats.length);
        for (int i = 0; i < stats.length; i++) {
            elementData[size++] = stats[i];
            if (stats[i] > largestValue) {
                largestValue = stats[i];
            }
        }
        return true;
    }

    /**
     */
    public void add(int index, double element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);
        }
        ensureCapacity(size + 1); 
        System.arraycopy(elementData, index, elementData, index + 1,
                 size - index);
        elementData[index] = element;
        size++;
        if (element > largestValue) {
            largestValue = element;
        }
    }

    /**
     */
    public double removeIndex(int index) {
        rangeCheck(index);
        double oldValue = elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index+1, elementData, index,
                     numMoved);
        }
        elementData[--size] = 0; // Let gc do its work
        return oldValue;
    }

    /**
     */
    public void clear() {
        // Let gc do its work
        for (int i = 0; i < size; i++) {
            elementData[i] = 0;
        }
    
        size = 0;
    }

    /**
     */
    protected void removeRange(int fromIndex, int toIndex) {
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                             numMoved);
    
        // Let gc do its work
        int newSize = size - (toIndex-fromIndex);
        while (size != newSize) {
            elementData[--size] = 0;
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
            s.writeDouble(elementData[i]);
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
        elementData = new double[arrayLength];

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
            elementData[i] = s.readDouble();
        }
    }

    // Comparison and Hashing - from AbstractList

    /**
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Statistics)) {
            return false;
        }

        Statistics stats = (Statistics) o                                    ;
        if (size == stats.size()) {
            for (int i = 0; i < size; i++) {
                double s1 = get(i);
                double s2 = stats.get(i);
                if (!(s1 == s2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // Modification Operations - from AbstractCollection

    /**
     */
    public boolean removeValue(double stats) {
        for (int i = 0; i < size; i++) {
            if (stats == get(i)) {
                removeIndex(i);
                return true;
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
