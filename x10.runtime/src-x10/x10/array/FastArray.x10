// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * Temporary class representing fastest possible array. Eliminates
 * checking, assumes 0-based, inlines RectLayout.
 *
 * Goal is to eliminate need for this class by using inlining and
 * compile-time constant propagation to handle the optimizations.
 *
 * @author bdlucas
 */

import x10.compiler.Native;

public final value class FastArray[T] extends BaseArray[T] {

    private val raw: Rail[T];
    private val layout: RectLayout;

    final public def raw(): Rail[T] {
        return raw;
    }

    final protected def layout(): RectLayout {
        return layout;
    }

    //
    // high-performance methods here to facilitate inlining
    // fast, so don't do any checking (place or bounds)
    //

    val delta0: int;
    val delta1: int;
    val delta2: int;
    val delta3: int;

    val offset0: int;
    val offset1: int;
    val offset2: int;
    val offset3: int;

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset0) + (#1)]")
    final public safe def apply(i0: int): T {
        var offset:int = i0;
        return raw(-offset0 + offset);
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset1) + (#1) * (#0)->FMGL(delta1) + (#2)]")
    final public safe def apply(i0: int, i1: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        return raw(-offset1 + offset);
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset2) + ((#1) * (#0)->FMGL(delta1) + (#2)) * (#0)->FMGL(delta2) + (#3)]")
    final public safe def apply(i0: int, i1: int, i2: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        return raw(-offset2 + offset);
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset3) + (((#1) * (#0)->FMGL(delta1) + (#2)) * (#0)->FMGL(delta2) + (#3)) * (#0)->FMGL(delta3) + (#4)]")
    final public safe def apply(i0: int, i1: int, i2: int, i3: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        offset = offset*delta3 + i3;
        return raw(-offset3 + offset);
    }

    //
    //
    //

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset0) + (#2)] = (#1)")
    final public safe def set(v: T, i0: int): T {
        var offset:int = i0;
        raw(offset - offset0) = v;
        return v;
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset1) + (#2) * (#0)->FMGL(delta1) + (#3)] = (#1)")
    final public safe def set(v: T, i0: int, i1: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        raw(offset - offset1) = v;
        return v;
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset2) + ((#2) * (#0)->FMGL(delta1) + (#3)) * (#0)->FMGL(delta2) + (#4)] = (#1)")
    final public safe def set(v: T, i0: int, i1: int, i2: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        raw(offset - offset2) = v;
        return v;
    }

	@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset3) + (((#2) * (#0)->FMGL(delta1) + (#3)) * (#0)->FMGL(delta2) + (#4)) * (#0)->FMGL(delta3) + (#5)] = (#1)")
    final public safe def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        offset = offset*delta3 + i3;
        raw(offset - offset3) = v;
        return v;
    }


    //
    // is this really a good idea?
    //

    static class Accumulator[S] {
        var result:S;
        def this(r:S) {
            result = r;
        }
    }

    public def scan(op:(T,T)=>T, unit:T): Array[T](dist) {
        val a = new Accumulator[T](unit);
        return Array.make[T](dist, (p:Point):T => {
            a.result = op(a.result, apply(p as Point(rank)));
            return a.result;
        });

    }

    

    //
    //
    //

    def this(dist: Dist{constant}, init: Box[(Point)=>T]): FastArray[T]{self.dist==dist} {

        super(dist);

        /* might be more efficient but gives the compiler heartburn
        val there = here;
        finish async (dist.onePlace) {
            val layout = layout(region);
            val n = layout.size();
            val raw = Rail.makeVar[T](n);
            if (init!=null) {
                val f = at (init.location) { init as (Point) => T };
                for (p:Point in region)
                    raw(layout.offset(p)) = f(p);
            }
            async (there) {
                this.layout = layout;
                this.raw = raw;
            }
        }
        */

        layout = at (dist.onePlace) layout(region);
        raw = at (dist.onePlace) {
            val n = layout.size();
            val raw = Rail.makeVar[T](n);
            if (init!=null) {
                val f = at (init.location) { init as (Point) => T };
                for (p:Point in region)
                    raw(layout.offset(p)) = f(p);
            }
            return raw;
        };

        delta0 = layout.delta0;
        delta1 = layout.delta1;
        delta2 = layout.delta2;
        delta3 = layout.delta3;
        offset0 = (layout.min0);
        offset1 = (offset0)*layout.delta1 + layout.min1;
        offset2 = offset1*layout.delta2 + layout.min2;
        offset3 = offset2*layout.delta3 + layout.min3;
    }


    /*
     * restriction view
     */

    public safe def restriction(d: Dist): Array[T] {
        return new FastArray[T](this, d as Dist{constant});
    }

    def this(a: BaseArray[T], d: Dist{constant}) {

        super(d);
        val l = at (d.onePlace) a.layout();
        this.layout = l;
        this.raw = at (d.onePlace) a.raw();

        delta0 = l.delta0;
        delta1 = l.delta1;
        delta2 = l.delta2;
        delta3 = l.delta3;

	val o0 = (l.min0);
        offset0 = o0;
        val o1 = (o0)*l.delta1 + l.min1;
        offset1 = o1;
        val o2 = o1*l.delta2 + l.min2;
        offset2 = o2;
        offset3 = o2*l.delta3 + l.min3;
      
    }

}
