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
    static def binarySearch[T](a:IndexedMemoryChunk[T], key:T, min:Int, max:Int, cmp:(T,T)=>Int):Int {
        var low:Int = min;
        var high:Int = max-1;
        while (low <= high) {
            var mid:Int = (low + high) / 2;
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
