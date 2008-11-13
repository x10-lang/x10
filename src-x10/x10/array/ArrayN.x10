// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with raw chunk in each place,
 * initialized at its place.
 *
 * The per-chunk information is stored in a rail, one entry per
 * place. This is suitable for distributed arrays where we do not have
 * place-local fields, as is currently the case. If we had place-local
 * storage we could use the Array1 design instead, saving the cost of
 * accessing here and indexing the per-place rail of chunks.
 *
 * @author bdlucas
 */

final value class ArrayN[T] extends BaseArray[T] {

    //private val raws: Rail[Rail[T]];
    //private val layouts: Rail[RectLayout];

    private val raws: PlaceLocal[Rail[T]];
    private val layouts: PlaceLocal[RectLayout];

    final protected def raw(): Rail[T] {
        //return raws(here.id);
        return raws();
    }

    final protected def layout(): RectLayout {
        //return layouts(here.id);
        return layouts();
    }

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public def apply(i0: int): T {
        if (checkBounds) checkBounds(i0);
        return raw()(layout().offset(i0));
    }

    final public def apply(i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        return raw()(layout().offset(i0,i1));
    }

    final public def apply(i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw()(layout().offset(i0,i1,i2));
    }

    final public def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw()(layout().offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public def set(v: T, i0: int): T {
        if (checkBounds) checkBounds(i0);
        raw()(layout().offset(i0)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        raw()(layout().offset(i0,i1)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        raw()(layout().offset(i0,i1,i2)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw()(layout().offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    //
    //

    def this(dist: Dist, val init: (Point)=>T): ArrayN[T]{self.dist==dist} {

        super(dist);

        // compute per-place layout
        val layoutInit = ()=>layout(dist.get(here));
        layouts = PlaceLocal.make[RectLayout](dist.places(), layoutInit);
            
        // compute per-place raw storage
        val rawInit = ()=>{
            val layout = layouts();
            val n = layout.size();
            val raw = Rail.makeVar[T](n);
            if (init!=null)
                for (p:Point in dist.get(here))
                    raw(layout.offset(p)) = init(p);
            return raw;
        };
        raws = PlaceLocal.make[Rail[T]](dist.places(), rawInit);

    }


    /*
     * restriction view
     */

    public def restriction(d: Dist) {
        return new ArrayN[T](this, d);
    }

    def this(a: ArrayN[T], d: Dist) {

        super(d);

        val ps = dist.places();
        layouts = PlaceLocal.make[RectLayout](ps, a.layouts);
        raws = PlaceLocal.make[Rail[T]](ps, a.raws);

    }

}
