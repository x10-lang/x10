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

final value class RectLayout(rank: int) extends Layout {

    val size: int;

    val min: ValRail[int];
    val min0: int;
    val min1: int;
    val min2: int;
    val min3: int;

    val delta: ValRail[int];
    val delta0: int;
    val delta1: int;
    val delta2: int;
    val delta3: int;

    def this(min: ValRail[int], max: ValRail[int]) {
        
        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        this.rank = min.length;

        this.min = min;

        delta = Rail.makeVal[int](rank, (i:nat) => max(i) - min(i) + 1);

        var size: int = 1;
        for (d:int in delta)
            size *= d;
        this.size = size;

        min0 = rank>=1? min(0) : 0;
        min1 = rank>=2? min(1) : 0;
        min2 = rank>=3? min(2) : 0;
        min3 = rank>=4? min(3) : 0;

        delta0 = rank>=1? delta(0) : 0;
        delta1 = rank>=2? delta(1) : 0;
        delta2 = rank>=3? delta(2) : 0;
        delta3 = rank>=4? delta(3) : 0;
    }


    //
    // Layout
    //

    final def size(): int {
        return size;
    }

    final def offset(pt: Point): int {
        var offset: int = pt(0) - min(0);
        for (var i:int=1; i<rank; i++)
            offset = offset*delta(i) + pt(i) - min(i);
        return offset;
    }

    final def offset(i0: int): int  {
        var offset:int = i0 - min0;
        return offset;
    }

    final def offset(i0: int, i1: int): int {
        var offset:int  = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        return offset;
    }

    final def offset(i0: int, i1: int, i2: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        return offset;
    }

    final def offset(i0: int, i1: int, i2: int, i3: int): int {
        var offset:int = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        offset = offset*delta3 + i3 - min3;
        return offset;
    }

    public def toString() {
        var s:String = "RectLayout[";
        s += "size=" + size;
        for (var i:int=0; i<min.length; i++)
            s += "," + min(i) + "/" + delta(i);
        s += "]";
        return s;
    }

}
