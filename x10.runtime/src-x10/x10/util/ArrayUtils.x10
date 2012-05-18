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
 * This class contains utility methods for manipulating Array and
 * IndexedMemoryChunk instances.
 */
public class ArrayUtils {
    /**
     * Sorts the given array into ascending order.
     * @param a the array to be sorted
     * @param cmp the comparison function to use
     * @see qsort
     */
    public static def sort[T](a:Array[T], cmp:(T,T)=>Int) {
        qsort[T](a.raw(), 0, (a.size-1), cmp);
    }

    public static def sort[T](a:Array[T]){T<:Comparable[T]} {
        sort[T](a, (x:T,y:T) => x.compareTo(y));
    }

    //
    // quick&dirty sort
    //
    static def qsort[T](a:IndexedMemoryChunk[T], lo:Int, hi:Int, cmp:(T,T)=>Int) {
        if (hi <= lo) return;
        var l:Int = lo - 1;
        var h:Int = hi;
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

    private static def exch[T](a:IndexedMemoryChunk[T], i:Int, j:Int):void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;
    }

    /**
     * Searches the specified array for the key using the binary search
     * algorithm.  The array must be sorted (e.g. by the qsort method).
     * If the key is found, return its index in the array.
     * Otherwise, return (-(insertion point) -1), where insertion point is the
     * index at which the key would be inserted into the sorted array.
     * @param a the array to be searched
     * @param key the value to find
     * @param cmp the comparison function to use
     */
    public static def binarySearch[T](a:Array[T], key:T, cmp:(T,T)=>Int) {
        return binarySearch[T](a.raw(), key, 0, a.size, cmp);
    }

    public static def binarySearch[T](a:Array[T], key:T){T<:Comparable[T]} {
        return binarySearch[T](a.raw(), key, 0, a.size, (x:T,y:T) => x.compareTo(y));
    }

    /**
     * Searches the given array for the key using the binary search
     * algorithm.  The array must be sorted (e.g. by the sort method).
     * If the key is found, return its index in the array.
     * Otherwise, return (-(insertion point) -1), where insertion point is the
     * index at which the key would be inserted into the sorted array.
     * @param a the array to be searched
     * @param key the value to find
     * @param min the index of the first element to be searched
     * @param max the index of the last element (exclusive) to be searched 
     * @param cmp the comparison function to use
     */
    public static def binarySearch[T](a:Array[T], key:T, min:Int, max:Int, cmp:(T,T)=>Int) {
        return binarySearch[T](a.raw(), key, 0, a.size, cmp);
    }

    public static def binarySearch[T](a:Array[T], key:T, min:Int, max:Int){T<:Comparable[T]} {
        return binarySearch[T](a.raw(), key, 0, a.size, (x:T,y:T) => x.compareTo(y));
    }

    static def binarySearch[T](a:IndexedMemoryChunk[T], key:T, var min:Int, var max:Int, cmp:(T,T)=>Int):Int {
        while (min < max) {
            var mid:Int = (min + max) / 2;
            if(cmp(a(mid), key) < 0) {
                min = mid + 1;
            } else {
                max = mid;
            }
        }

        if((max == min) && (a(min).equals(key))) {
            return min;
        } else {
            return -max;
        }
    }
}
