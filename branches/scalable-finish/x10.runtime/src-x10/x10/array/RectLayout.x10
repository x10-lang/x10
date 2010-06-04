// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * A RectLayout represents a rectangular memory layout for a
 * region. It provides efficient access but wastes storage for
 * non-rectangular regions.
 *
 * Artificially generic to work around XTENLANG-327 (allows methods to
 * be inlined).
 *
 * @author bdlucas
 */

final class RectLayout(rank: int) extends Layout {

    global val size: int;

    global val min: ValRail[int];
    global val min0: int;
    global val min1: int;
    global val min2: int;
    global val min3: int;

    global val delta: ValRail[int];
    global val delta0: int;
    global val delta1: int;
    global val delta2: int;
    global val delta3: int;

    def this(min: ValRail[int], max: ValRail[int]) {
        
        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        val r = min.length;
        this.rank = r;
        this.min = min;

        val d0 = ValRail.make[int](r, (i:Int) => max(i) - min(i) + 1);
        delta = d0;

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
    }


    //
    // Layout
    //

    final global def size(): int {
        return size;
    }

    final global def offset(pt: Point): int {
        var offset: int = pt(0) - min(0);
        for (var i:int=1; i<rank; i++)
            offset = offset*delta(i) + pt(i) - min(i);
        return offset;
    }

    final global def offset(i0: int): int  {
        var offset:int = i0 - min0;
        return offset;
    }

    final global def offset(i0: int, i1: int): int {
        var offset:int  = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        return offset;
    }

    final global def offset(i0: int, i1: int, i2: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        return offset;
    }

    final global def offset(i0: int, i1: int, i2: int, i3: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        offset = offset*delta3 + i3 - min3;
        return offset;
    }

    public global safe def toString() {
        var s:String = "RectLayout[";
        s += "size=" + size;
        for (var i:int=0; i<min.length; i++)
            s += "," + min(i) + "/" + delta(i);
        s += "]";
        return s;
    }

}
