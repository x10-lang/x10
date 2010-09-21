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

    val min:ValRail[int]; /* will be null if rank<5 */
    val min0:int;
    val min1:int;
    val min2:int;
    val min3:int;

    val delta:ValRail[int]; /* will be null if rank<5 */
    val delta0:int;
    val delta1:int;
    val delta2:int;
    val delta3:int;

    def this(min:ValRail[int], max:ValRail[int]):RectLayout{this.rank==min.length} {
        if (max.length!=min.length)
            throw new IllegalArgumentException("min and max must have same length");

        val r = min.length;
        property(r);

        val d0 = ValRail.make[int](r, (i:Int) => max(i) - min(i) + 1);

        var size: int = 1;
        for (d:int in d0)
            size *= d;
        this.size = size;

        min0 = r>=1? min(0) : 0;
        min1 = r>=2? min(1) : 0;
        min2 = r>=3? min(2) : 0;
        min3 = r>=4? min(3) : 0;

        delta0 = r>=1? d0(0) : 0;
        delta1 = r>=2? d0(1) : 0;
        delta2 = r>=3? d0(2) : 0;
        delta3 = r>=4? d0(3) : 0;

        if (r>4) {
            this.min = min;
            delta = d0;
        } else {
	    this.min = null;
	    delta = null;
        }
    }


    def this(_min0:int, _max0:int):RectLayout{this.rank==1} {
        property(1);
	min0 = _min0;
	delta0 = _max0-_min0+1;
	size = delta0;	

	min1 = 0; delta1 = 0; 
	min2 = 0; delta2 = 0;
	min3 = 0; delta3 = 0;
	min = null; delta = null;
    }


    def this(_min0:int, _max0:int, 
             _min1:int, _max1:int):RectLayout{this.rank==2} {
        property(2);
	min0 = _min0;
	min1 = _min1;
	delta0 = _max0-_min0+1;
	delta1 = _max1-_min1+1;
	size = delta0*delta1;

	min2 = 0; delta2 = 0;
	min3 = 0; delta3 = 0;
	min = null; delta = null;
    }


    def this(_min0:int, _max0:int, 
             _min1:int, _max1:int,
             _min2:int, _max2:int):RectLayout{this.rank==3} {
        property(3);
	min0 = _min0;
	min1 = _min1;
	min2 = _min2;
	delta0 = _max0-_min0+1;
	delta1 = _max1-_min1+1;
	delta2 = _max2-_min2+1;
	size = delta0*delta1*delta2;

	min3 = 0; delta3 = 0;
	min = null; delta = null;
    }


    def this(_min0:int, _max0:int, 
             _min1:int, _max1:int,
             _min2:int, _max2:int,
             _min3:int, _max3:int):RectLayout{this.rank==3} {
        property(3);
	min0 = _min0;
	min1 = _min1;
	min2 = _min2;
	min3 = _min3;
	delta0 = _max0-_min0+1;
	delta1 = _max1-_min1+1;
	delta2 = _max2-_min2+1;
	delta3 = _max3-_min3+1;
	size = delta0*delta1*delta2*delta3;

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

    public safe def toString() {
        var s:String = "RectLayout[";
        s += "size=" + size;
        for (var i:int=0; i<min.length; i++) {
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
