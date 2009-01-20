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

public final value class FastArray[T] extends BaseArray[T] {

    private val raw: Rail[T];
    private val layout: RectLayout;

    final protected def raw(): Rail[T] {
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

    final public def apply(i0: int): T {
        var offset:int = i0;
        return raw(offset);
    }

    final public def apply(i0: int, i1: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        return raw(offset);
    }

    final public def apply(i0: int, i1: int, i2: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        return raw(offset);
    }

    final public def apply(i0: int, i1: int, i2: int, i3: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        offset = offset*delta3 + i3;
        return raw(offset);
    }

    //
    //
    //

    final public def set(v: T, i0: int): T {
        var offset:int = i0;
        raw(offset) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        raw(offset) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        raw(offset) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        var offset:int = i0;
        offset = offset*delta1 + i1;
        offset = offset*delta2 + i2;
        offset = offset*delta3 + i3;
        raw(offset) = v;
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

    def this(dist: Dist{constant}, init: (Point)=>T): FastArray[T]{self.dist==dist} {

        super(dist);

        /* might be more efficient but gives the compiler heartburn
        val there = here;
        finish async (dist.onePlace) {
            val layout = layout(region);
            val n = layout.size();
            val raw = Rail.makeVar[T](n);
            if (init!=null) {
                for (p:Point in region)
                    raw(layout.offset(p)) = init(p);
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
                for (p:Point in region)
                    raw(layout.offset(p)) = init(p);
            }
            return raw;
        };

        delta0 = layout.delta0;
        delta1 = layout.delta1;
        delta2 = layout.delta2;
        delta3 = layout.delta3;
    }


    /*
     * restriction view
     */

    public def restriction(d: Dist): Array[T] {
        return new FastArray[T](this, d as Dist{constant});
    }

    def this(a: BaseArray[T], d: Dist{constant}) {

        super(d);

        this.layout = (future(d.onePlace) a.layout()) .force();
        this.raw = (future(d.onePlace) a.raw()) .force();

        delta0 = layout.delta0;
        delta1 = layout.delta1;
        delta2 = layout.delta2;
        delta3 = layout.delta3;
    }

}
