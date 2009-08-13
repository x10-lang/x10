// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with raw chunk in each place,
 * initialized at its place. The chunk is stored in a PlaceLocal
 * object.
 *
 * @author bdlucas
 */

import x10.array.PlaceLocal;

final value class DistArray[T] extends BaseArray[T] {

    private val raws: PlaceLocal[Rail[T]];
    private val layouts: PlaceLocal[RectLayout];

    final protected def raw(): Rail[T] {
        return raws();
    }

    final protected def layout(): RectLayout {
        return layouts();
    }

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe def apply(i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        return raw()(layout().offset(i0));
    }

    final public safe def apply(i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        return raw()(layout().offset(i0,i1));
    }

    final public safe def apply(i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw()(layout().offset(i0,i1,i2));
    }

    final public safe def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw()(layout().offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe def set(v: T, i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        raw()(layout().offset(i0)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        raw()(layout().offset(i0,i1)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        raw()(layout().offset(i0,i1,i2)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw()(layout().offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    //
    //

    def this(dist: Dist, val init: Box[(Point)=>T]): DistArray[T]{self.dist==dist} {

        super(dist);

        // compute per-place layout
        val layoutInit = ()=> layout(dist.get(here));
        layouts = PlaceLocal.make[RectLayout](dist.places(), layoutInit);
            
        // compute per-place raw storage
        val rawInit = ()=>{
            val layout = layouts();
            val n = layout.size();
            val raw = Rail.makeVar[T](n);
            if (init!=null) {
                val f = at (init.location) { init as (Point) => T };
                for (p:Point in dist.get(here))
                    raw(layout.offset(p)) = f(p);
            }
            return raw;
        };
        raws = PlaceLocal.make[Rail[T]](dist.places(), rawInit);

    }


    /*
     * restriction view
     */

    public safe def restriction(d: Dist) {
        if (d.constant)
            return new LocalArray[T](this, d as Dist{constant});
        else
            return new DistArray[T](this, d);
    }

    def this(a: DistArray[T], d: Dist) {

        super(d);

        val ps = dist.places();
        layouts = PlaceLocal.make[RectLayout](ps, a.layouts);
        raws = PlaceLocal.make[Rail[T]](ps, a.raws);

    }

}
