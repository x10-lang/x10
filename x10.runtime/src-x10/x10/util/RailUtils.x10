/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;

import x10.compiler.Inline;

/**
 * This class contains utility methods for performing common bulk
 * operations on Rails such as sorting, searching, mapping, and reducing.
 */
public class RailUtils {
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

    /**
     * Sorts a portion of the given rail into ascending order.
     * @param a the rail to be sorted
     * @param lo the index of the start of the range to be sorted
     * @param hi the index of the end of the range to be sorted
     * @param cmp the comparison function to use
     */
    public static def qsort[T](a:Rail[T], lo:Long, hi:Long, cmp:(T,T)=>Int) {
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


    /**
     * Reduce the src Rail using the given function and the given initial value.
     * Each element of the Rail will be given as an argument to the reduction
     * function exactly once, but in an arbitrary order.  The reduction function
     * may be applied concurrently to implement a parallel reduction. 
     * 
     * @param rail the reduction function
     * @param op the reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     */
    public static @Inline def reduce[T,U](src:Rail[T], op:(U,T)=>U, unit:U):U {
        // TODO: Structure as a recursive divide and conquer down to some minimal
        //       grain size.
        // TODO: Benchmark async initialization vs. collecting finish for accumulation
        var accum:U = unit;
        for (i in src.range()) {
            accum = op(accum, src(i));
        }          
        return accum;
    }
    

    /**
     * Map the given function onto the elements of the src Rail
     * storing the results in the dst Rail such that 
     * <code>for all i in src.range</code>, <code>dst(i) = op(src(i))</code>
     * 
     * @param src the source rail for the results of the map operation
     * @param dst the destination rail for the results of the map operation
     * @param op the function to apply to each element of the array
     * @return dst after updating its contents to contain the result of the map operation.
     */
    public static @Inline def map[T,U](src:Rail[T], dst:Rail[U], 
                                       op:(T)=>U)/* {src.size <= dst.size} */ : Rail[U]{self==dst} {
        // TODO: Structure as a recursive divide and conquer down to some minimal
        //       grain size.
        // TODO: Benchmark async initialization vs. collecting finish for accumulation
        for (i in src.range()) {
            dst(i) = op(src(i));
        }
        return dst;
    }


    /**
     * Map the given function onto the elements of src1 and src2 
     * storing the results in dst Rail such that 
     * <code>for all i in src.range()</code>, <code>dst(i) = op(src1(i), src2(i))</code>
     * 
     * @param src1 the first source array to use as input to the map function
     * @param src2 the second source array to use as input to the map function
     * @param dst the destination array for the results of the map operation
     * @param op the function to apply to each element of the arrays
     * @return dst after updating its contents to contain the result of the map operation.
     */
    public static @Inline def map[S,T,U](src1:Rail[S], src2:Rail[T], dst:Rail[U], op:(S,T)=>U) 
                                           {src1.size == src2.size /*, src1.size<==dst.size*/} : Rail[U]{self==dst} {
        // TODO: Structure as a recursive divide and conquer down to some minimal
        //       grain size.
        // TODO: Benchmark async initialization vs. collecting finish for accumulation
        for (i in src1.range()) {
            dst(i) = op(src1(i), src2(i));
        }
        return dst;
    }

}
