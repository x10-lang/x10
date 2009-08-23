// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with single raw chunk in one place.
 *
 * @author bdlucas
 */

final value class LocalArray[T] extends BaseArray[T] {

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
    //
    // NB: local array, so don't do place checking
    //

    final public safe def apply(i0: int): T {
        if (checkBounds) checkBounds(i0);
        return raw(layout.offset(i0));
    }

    final public safe def apply(i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        return raw(layout.offset(i0,i1));
    }

    final public safe def apply(i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw(layout.offset(i0,i1,i2));
    }

    final public safe def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw(layout.offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    //
    // NB: local array, so don't do place checking
    //

    final public safe def set(v: T, i0: int): T {
        if (checkBounds) checkBounds(i0);
        raw(layout.offset(i0)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        raw(layout.offset(i0,i1)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        raw(layout.offset(i0,i1,i2)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw(layout.offset(i0,i1,i2,i3)) = v;
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

    def this(dist: Dist{constant}, init: Box[(Point)=>T]): LocalArray[T]{self.dist==dist} {

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
    }


    /*
     * restriction view
     */

    public safe def restriction(d: Dist): Array[T] {
        return new LocalArray[T](this, d as Dist{constant});
    }

    def this(a: BaseArray[T], d: Dist{constant}) {
        super(d);
	
	if (d.region.isEmpty()) {
            this.layout = layout(d.region);
	    this.raw = Rail.makeVar[T](0);
        } else {
            this.layout = at (d.onePlace) a.layout();
            this.raw = at (d.onePlace) a.raw();
        }
    }

}
