/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

/**
 * This class contains utility methods for manipulating Rail and
 * IndexedMemoryChunk instances.
 */
public class ArrayUtils {
    /**
     * Sorts the given rail into ascending order.
     * @param a the rail to be sorted
     * @param cmp the comparison function to use
     * @see qsort
     */
    public static def sort[T](a:Rail[T], cmp:(T,T)=>Int) {
        qsort[T](a, 0, (a.size-1), cmp);
    }

    public static def sort[T](a:Rail[T]){T<:Comparable[T]} {
        sort[T](a, (x:T,y:T) => x.compareTo(y));
    }

    //
    // quick&dirty sort
    //
    static def qsort[T](a:IndexedMemoryChunk[T], lo:Long, hi:Long, cmp:(T,T)=>Int) {
        if (hi <= lo) return;
        var l:Long = lo - 1;
        var h:Long = hi;
        while (true) {
            while (cmp(a(++l), a(hi))<0);
            while (cmp(a(hi), a(--h))<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch[T](a, l, hi);
        qsort[T](a, lo, l-1, cmp);
        qsort[T](a, l+1, hi, cmp);
    }

    private static def exch[T](a:IndexedMemoryChunk[T], i:Long, j:Long):void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;
    }

    static def qsort[T](a:Rail[T], lo:Long, hi:Long, cmp:(T,T)=>Int) {
        if (hi <= lo) return;
        var l:Long = lo - 1;
        var h:Long = hi;
        while (true) {
            while (cmp(a(++l), a(hi))<0);
            while (cmp(a(hi), a(--h))<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch[T](a, l, hi);
        qsort[T](a, lo, l-1, cmp);
        qsort[T](a, l+1, hi, cmp);
    }

    private static def exch[T](a:Rail[T], i:Long, j:Long):void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;
    }

    /**
     * Searches the specified rail for the key using the binary search
     * algorithm.  The rail must be sorted (e.g. by the qsort method).
     * If the key is found, return its index in the rail.
     * Otherwise, return (-(insertion point) -1), where insertion point is the
     * index at which the key would be inserted into the sorted rail.
     * @param a the rail to be searched
     * @param key the value to find
     * @param cmp the comparison function to use
     */
    public static def binarySearch[T](a:Rail[T], key:T, cmp:(T,T)=>Int) {
        return binarySearch[T](a, key, 0, a.size, cmp);
    }

    public static def binarySearch[T](a:Rail[T], key:T){T<:Comparable[T]} {
        return binarySearch[T](a, key, 0, a.size, (x:T,y:T) => x.compareTo(y));
    }

    /**
     * Searches the given rail for the key using the binary search
     * algorithm.  The rail must be sorted (e.g. by the sort method).
     * If the key is found, return its index in the rail.
     * Otherwise, return (-(insertion point) -1), where insertion point is the
     * index at which the key would be inserted into the sorted rail.
     * @param a the rail to be searched
     * @param key the value to find
     * @param min the index of the first element to be searched
     * @param max the index of the last element (exclusive) to be searched 
     * @param cmp the comparison function to use
     */
    public static def binarySearch[T](a:Rail[T], key:T, min:Long, max:Long, cmp:(T,T)=>Int) {
        var low:Long = min;
        var high:Long = max-1;
        while (low <= high) {
            var mid:Long = (low + high) / 2;
            val compare = cmp(a(mid), key);
            if(compare < 0) {
                low = mid + 1;
            } else if (compare > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return -(low + 1);
    }

    public static def binarySearch[T](a:Rail[T], key:T, min:Long, max:Long){T<:Comparable[T]} {
        return binarySearch[T](a, key, 0, a.size, (x:T,y:T) => x.compareTo(y));
    }

    static def binarySearch[T](a:IndexedMemoryChunk[T], key:T, min:Long, max:Long, cmp:(T,T)=>Int) {
        var low:Long = min;
        var high:Long = max-1;
        while (low <= high) {
            var mid:Long = (low + high) / 2;
            val compare = cmp(a(mid), key);
            if(compare < 0) {
                low = mid + 1;
            } else if (compare > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return -(low + 1);
    }
}
