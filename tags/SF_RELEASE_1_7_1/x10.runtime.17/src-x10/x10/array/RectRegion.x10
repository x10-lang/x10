// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;


/**
 * XXX caching of min/max in constructor will generate exception that
 * prevents creation of unbounded rectangular RectRegion - fix that so
 * it only happens when scanner or iterator are created
 */

final value class RectRegion extends PolyRegion {

    private class NonValue {
        var size:int = -1;
    }

    val nonValue: NonValue = new NonValue();


    /**
     * computation of size and min/max is deferred until needed to
     * allow unbounded regions
     */

    def this(val hl: HalfspaceList): RectRegion{rank==hl.rank && rect} {
        super(hl);
    }

    public static def make1(val min: Rail[int], val max: Rail[int]): Region{rank==min.length&&rect} { // XTENLANG-4

        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        var hl: HalfspaceList{rank==min.length} = new HalfspaceList(min.length);
        for (var i: int = 0; i<min.length; i++) {
            hl.add(hl.X(i), hl.GE, min(i));
            hl.add(hl.X(i), hl.LE, max(i));
        }
        hl.isSimplified = true;

        return new RectRegion(hl);
    }


    // XTENLANG-109
    public static def make1(min: int, max: int): Region{rect && rank==1 /*&& zeroBased==(min==0)*/} {
        return make1([min], [max]) as Region{rect && rank==1 /*&& zeroBased==(min==0)*/};
    }

    public def size(): int {
        if (nonValue.size < 0) {
            var min: Rail[int] = halfspaces.rectMin();
            var max: Rail[int] = halfspaces.rectMax();
            var size:int = 1;
            for (var i: int = 0; i<rank; i++)
                size *= max(i) - min(i) + 1;
            nonValue.size = size;
        }
        return nonValue.size;
    }


    /**
     * scanner
     */

    final private static class Scanner implements Region.Scanner {

        private val min: Rail[int];
        private val max: Rail[int];

        def this(r: PolyRegion): Scanner {
            min = r.halfspaces.rectMin();
            max = r.halfspaces.rectMax();
        }

        final public def set(axis: int, position: int): void {
            // no-op
        }
        
        final public def min(axis: int): int {
            return min(axis);
        }
        
        final public def max(axis: int): int {
            return max(axis);
        }
    }

    public def scanner(): Region.Scanner {
        return new RectRegion.Scanner(this);
    }


    /**
     * specialized from PolyRegion.Iterator
     * keep them in sync
     *
     * XXX this is actually SLOWER than the generic PolyRegion.Iterator!!!???
     */

    final private static class It implements Iterator[Rail[int]] {
        
        // parameters
        private val rank: int;
        private val min: Rail[int];
        private val max: Rail[int];

        // state
        private val x: Rail[int];
        private var k: int;

        def this(val r: RectRegion): It {
            rank = r.rank;
            min = r.halfspaces.rectMin();
            max = r.halfspaces.rectMax();
            x = Rail.makeVar[int](rank);
            for (var i: int = 0; i<rank; i++)
                x(i) = min(i);
            x(rank-1)--;
        }

        final public def hasNext(): boolean {
            k = rank-1;
            while (x(k)>=max(k))
                if (--k<0)
                    return false;
            return true;
        }

        final public def next(): Rail[int] {
            x(k)++;
            for (k=k+1; k<rank; k++)
                x(k) = min(k);
            return x;
        }

        incomplete public def remove(): void;
    }

    /* slower!!!
    public Region.Iterator iterator() {
        return new RectRegion.Iterator(this);
    }
    */


    //
    // region operations
    //

    public def boundingBox(): Region(rank) {
        return this;
    }

    public def min(): Rail[int] {
        return halfspaces.rectMin();
    }

    public def max(): Rail[int] {
        return halfspaces.rectMax();
    }


    // XTENLANG-28

    public def equals(that: Region): boolean {

        // we only handle rect==rect
        if (!(that instanceof RectRegion))
            return super.equals(that);

        // ranks must match
        if (this.rank!=that.rank)
            return false;

        // fetch bounds
        var thisMin: Rail[int] = this.min();
        var thisMax: Rail[int] = this.max();
        var thatMin: Rail[int] = (that as RectRegion).min();
        var thatMax: Rail[int] = (that as RectRegion).max();

        // compare 'em
        for (var i: int = 0; i<rank; i++) {
            if (thisMin(i)!=thatMin(i) || thisMax(i)!=thatMax(i))
                return false;
        }
        return true;
    }


    //
    //
    //

    public def toString(): String {
        var min: Rail[int] = min();
        var max: Rail[int] = max();
        var s: String = "[";
        for (var i: int = 0; i<rank; i++) {
            if (i>0) s += ",";
            s += min(i) + ".." + max(i);
        }
        s += "]";
        return s;
    }

}
