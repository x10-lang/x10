/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.array;

import x10.compiler.Header;
import x10.compiler.Inline;

/**
 * A RectLayout represents a rectangular memory layout for a
 * region. It provides efficient access but wastes storage for
 * non-rectangular regions.
 */
struct RectLayout(rank:int) {

    val size: int;

    val min:Array[int](1); /* will be null if rank<5 */
    val min0:int;
    val min1:int;
    val min2:int;
    val min3:int;

    val delta:Array[int](1); /* will be null if rank<5 */
    val delta0:int;
    val delta1:int;
    val delta2:int;
    val delta3:int;

    def this(reg:Region):RectLayout{this.rank==reg.rank} {
        property(reg.rank);

	if (reg.isEmpty()) {
	    min0 = min1 = min2 = min3 = 0;
            delta0 = delta1 = delta2 = delta3 = 0;
            size = 0;
            if (rank>4) {
                min = new Array[int](rank, (int)=>0);
                delta = new Array[int](rank, (int)=>0);
            } else {
                min = delta = null;
            }
        } else {
            var sz:int = 1;
            if (rank>4) {
                val tmpMin = new Array[int](rank, (i:int) => reg.min(i));
                min = tmpMin;
                delta = new Array[int](rank, (i:int) => reg.max(i) - tmpMin(i) +1);
                for ([r] in 4..(rank-1)) {
                    sz *= delta(r);
                }
            } else {
                min = null;
                delta = null;
            }

	    min0 = reg.min(0);
	    delta0 = reg.max(0) - min0 + 1;
	    sz *= delta0;

            if (rank > 1) {
	        min1 = reg.min(1);
	        delta1 = reg.max(1) - min1 + 1;
	        sz *= delta1;
            } else {
                min1 = delta1 = 0;
            }

            if (rank > 2) {
	        min2 = reg.min(2);
	        delta2 = reg.max(2) - min2 + 1;
	        sz *= delta2;
            } else {
                min2 = delta2 = 0;
            }

            if (rank > 3) {
	        min3 = reg.min(3);
	        delta3 = reg.max(3) - min3 + 1;
	        sz *= delta3;
            } else {
                min3 = delta3 = 0;
             }

	    size = sz;
        }
    }

    def this(_min0:int, _max0:int):RectLayout{this.rank==1} {
        property(1);
        min0 = _min0;
        delta0 = _max0-_min0+1;
        size = delta0 > 0 ? delta0 : 0;  

        min1 = 0; delta1 = 0; 
        min2 = 0; delta2 = 0;
        min3 = 0; delta3 = 0;
        min = null; delta = null;
    }


    /*@Header @Inline*/ def size(): int {
        return size;
    }

    def offset(pt: Point): int {
        switch(pt.rank) {
            case 1: return offset(pt(0));
            case 2: return offset(pt(0), pt(1));
            case 3: return offset(pt(0), pt(1), pt(2));
            case 4: return offset(pt(0), pt(1), pt(2), pt(3));
            default: {
                var offset: int = pt(0) - min(0);
                for (var i:int=1; i<rank; i++)
                    offset = offset*delta(i) + pt(i) - min(i);
                return offset;
            }
        }
    }

    @Header @Inline def offset(i0: int): int  {
        var offset:int = i0 - min0;
        return offset;
    }

    @Header @Inline def offset(i0: int, i1: int): int {
        var offset:int  = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        return offset;
    }

    @Header @Inline def offset(i0: int, i1: int, i2: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        return offset;
    }

    @Header @Inline def offset(i0: int, i1: int, i2: int, i3: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        offset = offset*delta3 + i3 - min3;
        return offset;
    }

    public def toString() {
        var s:String = "RectLayout[";
        s += "size=" + size;
        for (var i:int=0; i<rank; i++) {
            val m:int;
            val d:int;
            switch (i) {
                case 0: m = min0; d = delta0; break;
                case 1: m = min1; d = delta1; break;
                case 2: m = min2; d = delta2; break;
                case 3: m = min3; d = delta3; break;
                default: m = min(i); d = delta(i);
            }
            s += "," + m + "/" + d;
        }
        s += "]";
        return s;
    }

}
